/*
 * web: org.nrg.xnat.helpers.prearchive.SessionXMLRebuilder
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.prearchive;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.framework.task.XnatTask;
import org.nrg.framework.task.services.XnatTaskService;
import org.nrg.xdat.XDAT;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.archive.Operation;
import org.nrg.xnat.services.XnatAppInfo;
import org.nrg.xnat.services.messaging.prearchive.PrearchiveOperationRequest;
import org.nrg.xnat.task.AbstractXnatTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;

import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.nrg.xft.db.PoolDBUtils.SEARCH_SCHEMA_NAME;
import static org.nrg.xnat.helpers.prearchive.PrearcDatabase.PREARCHIVE_TABLE;
import static org.nrg.xnat.helpers.prearchive.PrearcUtils.PREARC_LOCKS;

/**
 * The Class SessionXMLRebuilder.
 */
@XnatTask(taskId = "SessionXMLRebuilder", description = "Session XML Rebuilder", defaultExecutionResolver = "SingleNodeExecutionResolver", executionResolverConfigurable = true)
@Slf4j
public class SessionXMLRebuilder extends AbstractXnatTask {
    /**
     * Instantiates a new session XML rebuilder.
     *
     * @param provider    the provider
     * @param appInfo     the app info
     * @param jmsTemplate the jms template
     * @param interval    the interval
     */
    public SessionXMLRebuilder(final Provider<UserI> provider, final XnatTaskService taskService, final XnatAppInfo appInfo, final JmsTemplate jmsTemplate, final JdbcTemplate jdbcTemplate, final double interval) {
        super(taskService, true, appInfo, jdbcTemplate);
        _provider = provider;
        _interval = interval;
        _jmsTemplate = jmsTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runTask() {
        try {
            final UserI user = _provider.get();
            if (user == null) {
                log.warn("The user for running the session XML rebuilder process was not found. Aborting for now.");
                return;
            }

            if (!PrearcDatabase.isReady()) {
                log.info("The prearchive database is not ready, exiting.");
                return;
            }

            log.trace("Running prearc job as {}", user.getLogin());
            final List<SessionData> allSessions       = PrearcDatabase.getAllSessions();
            final int               totalSessionCount = allSessions.size();

            int updatedSessionCount   = 0;
            int processedSessionCount = 0;
            log.info("Checking whether any prearc entries should be processed, found {} sessions", totalSessionCount);
            if (!allSessions.isEmpty()) {
                final long now = Calendar.getInstance().getTimeInMillis();
                for (final SessionData sessionData : allSessions) {
                    processedSessionCount++;
                    final SessionDataTriple        triple            = sessionData.getSessionDataTriple();
                    final PrearcUtils.PrearcStatus status            = sessionData.getStatus();
                    final Boolean                  preventAutoCommit = sessionData.getPreventAutoCommit();
                    final String                   source            = sessionData.getSource();

                    log.info("Testing session #{} of {} total, '{}' with status {}, prevent auto commit {}, source {}", processedSessionCount, totalSessionCount, triple, status, preventAutoCommit, source);

                    if (status.equals(PrearcUtils.PrearcStatus.RECEIVING) && !preventAutoCommit && !StringUtils.trimToEmpty(source).equals(SessionData.UPLOADER)) {
                        try {
                            final File   sessionDir = PrearcUtils.getPrearcSessionDir(user, sessionData.getProject(), sessionData.getTimestamp(), sessionData.getFolderName(), false);
                            final long   then       = sessionData.getLastBuiltDate().getTime();
                            final double diff       = diffInMinutes(then, now);

                            log.debug("Prearchive session '{}' is {} minutes old", sessionData, diff);

                            final boolean intervalExceeded        = diff >= _interval;
                            final boolean extremeIntervalExceeded = intervalExceeded && diff >= _interval * 10;

                            if (extremeIntervalExceeded) {
                                log.error("Prearchive session {} locked for an abnormally large time: {} minutes", sessionData, diff);
                            } else if (intervalExceeded) {
                                log.info("Update #{}: prearchive session {} is {} minutes old, greater than configured interval {}, checking that session isn't currently receiving data", updatedSessionCount, sessionData, diff, _interval);
                                final boolean sessionReceiving = isSessionReceiving(triple);
                                if (sessionReceiving) {
                                    log.info("Update #{}: prearchive session {} is {} minutes old but is reported as receiving.", updatedSessionCount, sessionData, diff);
                                } else {
                                    log.info("Update #{}: creating JMS queue entry for {} to build session {} to {}", updatedSessionCount, user.getUsername(), sessionData, sessionData.getExternalUrl());
                                updatedSessionCount++;
                                    XDAT.sendJmsRequest(_jmsTemplate, new PrearchiveOperationRequest(user, Operation.Rebuild, sessionData, sessionDir));
                                }
                            } else {
                                log.debug("Prearchive session {} is {} minutes old, less than configured interval {}, remaining in RECEIVING status", sessionData, diff, _interval);
                            }
                        } catch (IOException e) {
                            final String message = String.format("An error occurred trying to write the session %s %s %s.", sessionData.getFolderName(), sessionData.getTimestamp(), sessionData.getProject());
                            log.error(message, e);
                        } catch (InvalidPermissionException e) {
                            final String message = String.format("A permissions error occurred trying to write the session %s %s %s.", sessionData.getFolderName(), sessionData.getTimestamp(), sessionData.getProject());
                            log.error(message, e);
                        } catch (Exception e) {
                            final String message = String.format("An unknown error occurred trying to write the session %s %s %s.", sessionData.getFolderName(), sessionData.getTimestamp(), sessionData.getProject());
                            log.error(message, e);
                        }
                    }
                }
            }
            log.info("Built {} of {}", updatedSessionCount, processedSessionCount);
        } catch (SessionException e) {
            log.error("", e);
        } catch (SQLException e) {
            // Swallow this message so it doesn't fill the logs before the prearchive is initialized.
            if (!e.getMessage().contains("relation \"" + SEARCH_SCHEMA_NAME + "." + PREARCHIVE_TABLE + "\" does not exist")) {
                log.error("", e);
            }
        } catch (final NrgServiceRuntimeException e) {
            if (e.getServiceError() != NrgServiceError.UserServiceError) {
                throw e;
            }
            log.warn("The user for running the session XML rebuilder process could not be initialized. This probably means the system is still initializing. Check the database if this is not the case.");
        } catch (final Exception e) {
            log.error("An unknown error occurred", e);
        }
    }

    /**
     * Diff in minutes.
     *
     * @param start the start
     * @param end   the end
     *
     * @return the double
     */
    private static double diffInMinutes(final long start, final long end) {
        return Math.floor(Math.floor(((float) end - (float) start) / 1000) / 60);
    }

    /**
     * Checks whether the session is currently receiving files. This method reviews the file locks that are currently open for this session.
     *
     * @param session The session to test for receiving.
     *
     * @return Returns true if the session still appears to be receiving new files, false otherwise.
     */
    private static boolean isSessionReceiving(final SessionDataTriple session) {
        final File lockFolder = PrearcUtils.buildCacheSubDir(PREARC_LOCKS, session.getProject(), session.getTimestamp(), session.getFolderName());
        log.debug("Checking for lock folder \"{}\" for session {}", lockFolder, session);
        if (!lockFolder.exists()) {
            log.debug("The lock folder \"{}\" for session {} doesn't exist, session is not currently receiving", lockFolder, session);
            return false;
        }

        final File[] locks = lockFolder.listFiles();
        final boolean hasLockFiles = locks != null && locks.length > 0;
        if (hasLockFiles) {
            log.info("Found {} lock files in the folder \"{}\" for session {}: {}", locks.length, lockFolder, session, StringUtils.join(Lists.transform(Arrays.asList(locks), new Function<File, String>() {
                @Override
                public String apply(final File file) {
                    return file.getName();
                }
            }), ", "));
        } else {
            log.info("There's a lock folder \"{}\" for session {} but it doesn't have any lock files in it.", lockFolder, session);
        }
        return hasLockFiles;
    }

    private final Provider<UserI> _provider;
    private final double          _interval;
    private final JmsTemplate     _jmsTemplate;
}
