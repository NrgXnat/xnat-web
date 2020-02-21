/*
 * web: org.nrg.xnat.helpers.prearchive.PrearcDatabase
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.prearchive;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.automation.entities.Script;
import org.nrg.automation.services.ScriptService;
import org.nrg.dicomtools.filters.DicomFilterService;
import org.nrg.dicomtools.filters.SeriesImportFilter;
import org.nrg.framework.constants.PrearchiveCode;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.framework.status.StatusListenerI;
import org.nrg.framework.utilities.Reflection;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.bean.XnatMrsessiondataBean;
import org.nrg.xdat.bean.XnatPetmrsessiondataBean;
import org.nrg.xdat.bean.XnatPetsessiondataBean;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatPetscandataI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.user.XnatUserProvider;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xft.exception.DBPoolException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.ValidationUtils.XFTValidator;
import org.nrg.xft.utils.predicates.ProjectAccessPredicate;
import org.nrg.xnat.archive.PrearcSessionArchiver;
import org.nrg.xnat.archive.XNATSessionBuilder;
import org.nrg.xnat.helpers.prearchive.PrearcUtils.PrearcStatus;
import org.nrg.xnat.restlet.XNATApplication;
import org.nrg.xnat.restlet.util.RequestUtil;
import org.nrg.xnat.status.ListenerUtils;
import org.restlet.data.Status;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.nrg.xft.db.PoolDBUtils.SEARCH_SCHEMA_NAME;
import static org.nrg.xft.utils.predicates.ProjectAccessPredicate.UNASSIGNED;
import static org.nrg.xnat.helpers.prearchive.DatabaseSession.createTableSql;
import static org.nrg.xnat.helpers.prearchive.SessionException.Error.*;

@Slf4j
public final class PrearcDatabase {
    public final static String UNASSIGNED                   = "unassigned";
    public final static String PREARCHIVE_TABLE             = "prearchive";
    public final static String PREARCHIVE_TABLE_WITH_SCHEMA = SEARCH_SCHEMA_NAME + "." + PREARCHIVE_TABLE;

    public static boolean isSplitPetMrSessionScript(final String scriptId) {
        return StringUtils.equalsIgnoreCase(SPLIT_PETMR_SESSION_ID, scriptId);
    }

    public static Script getSplitPetMrSessionScript() {
        return DEFAULT_SPLIT_PETMR_SESSION_SCRIPT;
    }

    public static boolean isReady() {
        return PREARC_READY.get();
    }

    public static void setReady(final boolean ready) {
        PREARC_READY.set(ready);
    }

    public static String formatSession(final String project, final String timestamp, final String session) {
        return StringUtils.joinWith(":", StringUtils.defaultIfBlank(project, UNASSIGNED), timestamp, session);
    }

    public static Connection conn;
    private final static String tableSql = createTableSql();
    public static boolean ready = false;

    /**
     * The default initializer uses the file system as this cache's permanent store.
     *
     * @param recreateDBMSTablesFromScratch Indicates whether the prearchive database tables should be rebuilt from scratch.
     *
     * @throws Exception When an error occurs.
     */
    public static void initDatabase(final boolean recreateDBMSTablesFromScratch) throws Exception {
        initDatabase(null, null, recreateDBMSTablesFromScratch);
    }

    /**
     * Initialize the cache with a path to the prearchive that syncs up the cache with the permanent store.
     *
     * @param prearcPath                    Indicates the prearchive path.
     * @param recreateDBMSTablesFromScratch Indicates whether the prearchive database tables should be rebuilt from scratch.
     *
     * @throws IllegalStateException
     * @throws SessionException
     * @throws IOException
     */
    public static void initDatabase(final String prearcPath, final boolean recreateDBMSTablesFromScratch) throws Exception {
        initDatabase(prearcPath, null, recreateDBMSTablesFromScratch);
    }

    /**
     * Initialize the cache with a session data delegate that syncs up the cache with the permanent store.
     *
     * @param delegate                      Carries the session data delegate.
     * @param recreateDBMSTablesFromScratch Indicates whether the prearchive database tables should be rebuilt from scratch.
     *
     * @throws IllegalStateException
     * @throws SessionException
     * @throws IOException
     */
    public static void initDatabase(SessionDataDelegate delegate, boolean recreateDBMSTablesFromScratch) throws Exception {
        initDatabase(null, delegate, recreateDBMSTablesFromScratch);
    }

    /**
     * Initialize the cache with the prearchive path and a session data delegate that syncs up the cache with the permanent store.
     *
     * @param givenPrearcPath                    Indicates the prearchive path.
     * @param delegate                      Carries the session data delegate.
     * @param recreateDBMSTablesFromScratch Indicates whether the prearchive database tables should be rebuilt from scratch.
     *
     * @throws IllegalStateException
     * @throws SessionException
     * @throws IOException
     */
    public static void initDatabase(final String givenPrearcPath, final SessionDataDelegate delegate, final boolean recreateDBMSTablesFromScratch) throws Exception {
        if (!ready) {
            prearcPath = StringUtils.defaultIfBlank(givenPrearcPath, getPrearcPath());
            if (prearcPath != null) {
                sessionDelegate = delegate != null ? delegate : new FileSystemSessionData(prearcPath);

                if (!tableExists()) { // create the table if it does not currently exist
                    createTable();
                } else { // check to see if the table has the correct set of columns (older versions may not)
                    correctTable(); // if not, correct the table by adding the required columns
                }

                if (recreateDBMSTablesFromScratch) {
                    clearPrearchive();
                }

                populateTable(); // add rows to the table from the prearchive directory if not already present
                pruneDatabase(); // remove rows from the table if they are not present in the prearchive directory

                ready = true;
            }
        }
    }

    /**
     * @return Path to the prearchive on the user filesystem
     */

    protected static String getPrearcPath() {
        return XDAT.getSiteConfigPreferences().getPrearchivePath();
    }

    private static boolean tableExists() throws Exception {
        try {
            return new SessionOp<Boolean>() {
                public Boolean op() throws Exception {
                    String query = "SELECT * FROM pg_catalog.pg_tables WHERE schemaname = 'xdat_search' AND tablename = 'prearchive';";

                    ResultSet r = this.pdb.executeQuery(null, query, null);
                    return PoolDBUtils.GetResultSetSize(r) == 1;
                }
            }.run();
        }
        // can't happen
        catch (SessionException e) {
            log.error("", e);
        }
        return true;
    }

    /**
     * Create the table if it doesn't exist. Should only be called once. Delete table argument on class load.
     *
     * @throws Exception
     */
    private static void createTable() throws Exception {
        try {
            new SessionOp<Void>() {
                public Void op() throws Exception {
                    String query = "SELECT * FROM information_schema.tables WHERE table_schema = LOWER('xdat_search') and table_name = LOWER('" + PREARCHIVE_TABLE + "');";
                    String exists = (String) PoolDBUtils.ReturnStatisticQuery(query, "relname", null, null);
                    if (exists == null) {
                        PoolDBUtils.ExecuteNonSelectQuery(tableSql, null, null);
                    }
                    return null;
                }
            }.run();
        } catch (SessionException e) {
            log.error("", e);
        }
    }

    /**
     * Corrects the prearchive table, checking for column existence and including new columns when required.
     *
     * @throws Exception
     */
    private static void correctTable() throws Exception {
        try {
            new SessionOp<Void>() {
                public Void op() throws Exception {
                    // First find out what's in the existing table. We're assuming it exists, because we should have
                    // checked for existing prior to trying to correct the table.
                    final List<String> existing = new ArrayList<>();
                    final String query = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = 'xdat_search' AND table_name = 'prearchive';";
                    final ResultSet results = this.pdb.executeQuery(null, query, null);
                    while (results.next()) {
                        existing.add(results.getString("column_name").toLowerCase());
                    }

                    // Now find out what's SUPPOSED to be in the table.
                    final List<String> required = new ArrayList<>(DatabaseSession.values().length);
                    for (final DatabaseSession d : DatabaseSession.values()) {
                        required.add(d.getColumnName().toLowerCase());
                    }

                    // Now check the ordinals. This is where undeclared column queries go to die, e.g. insert into table
                    // values (1, 2, 3) when the columns have actually moved. Start by checking table size. If that's
                    // off, we don't even need to check the ordering of the columns, since the column mismatch will
                    // cause ordering errors anyways.
                    boolean ordered;
                    if (required.size() == existing.size()) {
                        ordered = true;
                        for (int index = 0; index < required.size(); index++) {
                            if (!required.get(index).equals(existing.get(index))) {
                                ordered = false;
                                break;
                            }
                        }
                    } else {
                        ordered = false;
                    }

                    // Now find out what the existing and required columns have in common. If the in-common columns list
                    // is the same size as the required, that means we have all of the required columns.
                    Collection inCommon = CollectionUtils.intersection(required, existing);
                    boolean allRequiredExist = inCommon.size() == required.size();

                    // If we have all required columns and the ordering is good, we're done, the table matches.
                    if (allRequiredExist && ordered) {
                        return null;
                    }

                    // Build the ALTER query required to sync to the required definition. First rename prearc table to
                    // a holding table.
                    final StringBuilder buffer = new StringBuilder();
                    buffer.append("ALTER TABLE ").append(PREARCHIVE_TABLE_WITH_SCHEMA).append(" RENAME TO ");
                    buffer.append(PREARCHIVE_TABLE).append("_deprecated");
                    PoolDBUtils.ExecuteNonSelectQuery(buffer.toString(), null, null);

                    // Now create the standard prearchive table.
                    createTable();

                    // Create a list of column names with the in-common columns. These are specified on both the insert
                    // and select to match mis-ordered columns in the query results. This is what does the migration
                    // mapping for us so that the ordinality of the table structure matches the expectations of the
                    // later INSERT queries. So we remove required columns that aren't in-common because we can't
                    // migrate those.
                    if (!allRequiredExist) {
                        List<String> removals = new ArrayList<>();
                        for (String column : required) {
                            if (!inCommon.contains(column)) {
                                removals.add(column);
                            }
                        }
                        if (removals.size() > 0) {
                            required.removeAll(removals);
                        }
                    }

                    String columns = StringUtils.join(required.toArray(), ", ");

                    // Clear the query and create an insert that will select all of the in-common columns from the
                    // holding table and put them into the new prearchive table.
                    buffer.setLength(0);
                    buffer.append("INSERT INTO ").append(PREARCHIVE_TABLE_WITH_SCHEMA).append(" (").append(columns).append(")");
                    buffer.append("SELECT ").append(columns).append(" FROM ").append(PREARCHIVE_TABLE_WITH_SCHEMA).append("_deprecated");
                    PoolDBUtils.ExecuteNonSelectQuery(buffer.toString(), null, null);

                    // OK, data's migrated! Great! Nuke the old table.
                    buffer.setLength(0);
                    buffer.append("DROP TABLE ").append(PREARCHIVE_TABLE_WITH_SCHEMA).append("_deprecated");
                    PoolDBUtils.ExecuteNonSelectQuery(buffer.toString(), null, null);

                    // Leave.
                    return null;
                }
            }.run();
        } catch (SessionException e) {
            log.error("", e);
        }
    }

    /**
     * Populate the table with sessions in the prearchive directory. Should only be called once on class load.
     *
     * @throws SessionException
     * @throws SAXException
     * @throws SQLException
     * @throws IOException
     * @throws IllegalStateException
     */
    private static void populateTable() throws Exception {
        addSessions(sessionDelegate.get());
    }

    private static void addSessions(final Collection<SessionData> ss) throws Exception {
        new SessionOp<Void>() {
            public java.lang.Void op() throws Exception {
                PreparedStatement statement = this.pdb.getPreparedStatement(null, insertSql());
                for (final SessionData s : ss) {
                    SessionDataTriple sdt = s.getSessionDataTriple(); // only insert if the session is not already present
                    SessionData session = getSessionIfExists(sdt.getFolderName(), sdt.getTimestamp(), sdt.getProject());

                    if (session == null) {
                        for (int i = 0; i < DatabaseSession.values().length; i++) {
                            DatabaseSession.values()[i].setInsertStatement(statement, s);
                        }
                        statement.executeUpdate();
                    } else if (!s.getStatus().equals(session.getStatus())) { // newly generated status may need to override existing status
                        setStatus(sdt.getFolderName(), sdt.getTimestamp(), sdt.getProject(), s.getStatus());
                    }
                }
                return null;
            }
        }.run();
    }

    /**
     * Add the given session to the table. Only used when initially populating the database, or when it is refreshed.
     *
     * @param s The session
     *
     * @throws SQLException
     */
    public static void addSession(final SessionData s) throws Exception {
        checkArgs(s);
        new SessionOp<Void>() {
            public java.lang.Void op() throws Exception {
                int rowCount = countOf(s.getFolderName(), s.getTimestamp(), s.getProject());
                if (rowCount >= 1) {
                    throw new SessionException(AlreadyExists, "Trying to add an existing session");
                } else {
                    PreparedStatement statement = this.pdb.getPreparedStatement(null, insertSql());
                    for (int i = 0; i < DatabaseSession.values().length; i++) {
                        DatabaseSession.values()[i].setInsertStatement(statement, s);
                    }
                    statement.executeUpdate();
                }
                return null;
            }
        }.run();
    }


    /**
     * Parse the given uri and return a list of sessions in the database.
     *
     * @param uri The URI from which projects should be retrieved.
     *
     * @return A list of session data objects containing the projects present at the indicated URI.
     *
     * @throws Exception Thrown if there is an issue with the database connection.
     */
    public static List<SessionData> getProjects(String uri) throws Exception {
        final PrearcUriParserUtils.ProjectsParser parser = new PrearcUriParserUtils.ProjectsParser(new PrearcUriParserUtils.UriParser(XNATApplication.PREARC_PROJECT_URI));
        final List<String> projects = parser.readUri(uri);
        return new SessionOp<List<SessionData>>() {
            public List<SessionData> op() throws Exception {
                List<SessionData> ls = new ArrayList<>();
                String sql = DatabaseSession.PROJECT.allMatchesSql(projects.toArray(new String[projects.size()]));
                ResultSet rs;
                try {
                    rs = this.pdb.executeQuery(null, sql, null);
                } catch (DBPoolException e) {
                    throw new Exception(e.getMessage());
                }
                while (rs.next()) {
                    ls.add(DatabaseSession.fillSession(rs));
                }
                return ls;
            }
        }.run();
    }

    /**
     * Parse uri and return a specific session in the database
     *
     * @param uri The URI from which to retrieve a session.
     *
     * @return The retrieved session
     *
     * @throws Exception
     */
    public static SessionData getSession(String uri) throws Exception {
        final PrearcUriParserUtils.SessionParser parser = new PrearcUriParserUtils.SessionParser(new PrearcUriParserUtils.UriParser(XNATApplication.PREARC_SESSION_URI));
        final Map<String, String> sess = parser.readUri(uri);
        return new SessionOp<SessionData>() {
            public SessionData op() throws Exception {
                return getSession(sess.get("SESSION_LABEL"), sess.get("SESSION_TIMESTAMP"), sess.get("PROJECT_ID"));
            }
        }.run();
    }


    /**
     * Path to the project in the users prearchive directory
     *
     * @param project The project for which you want to retrieve the path.
     *
     * @return The path to the project.
     */
    public static String projectPath(String project) {
        if(project==null){
            return prearcPath;
        }
        else {
            return Paths.get(prearcPath, project).toString();
        }
    }

    /**
     * Generate prepared SQL statement to insert a session.
     *
     * @return The insert SQL for the current schema and table.
     */
    private static String insertSql() {
        List<String> ss = new ArrayList<>();
        for (int i = 0; i < DatabaseSession.values().length; i++) {
            ss.add("?");
        }
        return "INSERT INTO " + PREARCHIVE_TABLE_WITH_SCHEMA + " VALUES(" + StringUtils.join(ss.toArray(), ',') + ")";
    }

    /**
     * Recreate the database from scratch. This is an expensive operation.
     *
     * @throws SQLException
     * @throws SAXException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void refresh() throws Exception {
        refresh(XDAT.getBoolSiteConfigurationProperty("reloadPrearcDatabaseOnStartup", false));
    }

    /**
     * Recreate the database from scratch. This is an expensive operation. The {@link #refresh()} version of this method
     * will only recreate the database from scratch if the {@link SiteConfigPreferences#isReloadPrearcDatabaseOnStartup()}
     * property is set to <b>true</b>. This is useful for cleaning up the table on application start-up without
     * incurring the additional overhead of a full rebuild of the prearchive database. This version lets you specify
     * <b>true</b> for the force parameter to force the delete and full rebuild of the table.
     *
     * @param force Indicates whether the table should be dropped.
     *
     * @throws SQLException
     * @throws SAXException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void refresh(boolean force) throws Exception {
        if (force) {
            clearPrearchive();
        }

        populateTable(); // add rows to the table from the prearchive directory if not already present
        pruneDatabase(); // remove rows from the table if they are not present in the prearchive directory
    }

    /**
     * Move a session from one project to another. 'oldProj' is allowed to be null or empty to allow moving from an unassigned project to a real one. The other arguments must be non-empty, non-null values.
     *
     * @param session       The session name
     * @param timestamp     The session timestamp
     * @param origin        The origin project of the session.
     * @param destination   Name of the new Project
     *
     * @return Return true if successful, false otherwise
     *
     * @throws SessionException When an error occurs with the session.
     * @throws SQLException     When an error occurs running a query.
     * @throws Exception        When an unknown error occurs.
     */
    private static void _moveToProject(final String session, final String timestamp, final String origin, final String destination) throws Exception {
        if (StringUtils.isBlank(destination)) {
            throw new SessionException(NoProjectSpecified, "Destination project argument is null or empty");
        }

        log.info("Got request to move {} to project {}", formatSession(origin, timestamp, session), destination);
        final SessionData sessionData = getSession(session, timestamp, origin);

        final LockAndSync<Void> l = new LockAndSync<Void>(sessionData.getFolderName(), sessionData.getTimestamp(), sessionData.getProject(), sessionData.getStatus()) {
            Void extSync() throws SyncFailedException {
                sessionDelegate.move(sessionData, destination);
                return null;
            }

            void cacheSync() throws Exception {
                modifySession(sess, timestamp, proj, new SessionOp<Void>() {
                    public Void op() throws Exception {
                        try {
                            log.debug("Deleting session {} as part of move to project {}", formatSession(proj, timestamp, session), destination);
                            _unsafeDeleteSession(sess, timestamp, proj);
                            sessionData.setProject(destination);
                            sessionData.setStatus(PrearcUtils.PrearcStatus.READY);

                            final File projectF     = new File(getPrearcPath(), destination);
                            final File timestampDir = new File(projectF, timestamp);
                            final File session = new File(timestampDir, sess);
                            sessionData.setUrl(session.getAbsolutePath());

                            addSession(sessionData);

                            PrearcUtils.log(sessionData, new Exception(String.format("Moved from %1$s to %2$s", proj, destination)));
                        } catch (SyncFailedException e) {
                            log.error("Session sync operation failed", e);
                            throw new IllegalStateException("Session sync operation failed", e);
                        }
                        return null;
                    }
                });
            }

            @Override
            boolean checkStatus() {
                return sessionData.getStatus().equals(PrearcStatus.MOVING);
            }
        };
        Exception e = null;
        try {
            l.run();
        } catch (Exception _e) {
            log.error("", _e);
            e = _e;
        }

        if (e != null) {
            wrapException(e);
        }
    }

    /**
     * Separate a PET/MR session into separate MR and PET sessions.
     *
     * @param session       The session name
     * @param timestamp     The session timestamp
     * @param project       The origin project of the session.
     * @param petmrSession  The PET/MR session bean.
     *
     * @return Return true if successful, false otherwise
     *
     * @throws SessionException When an error occurs with the session.
     * @throws SQLException     When an error occurs running a query.
     * @throws Exception        When an unknown error occurs.
     */
    private static Map<String, SessionData> _separatePetMrSession(final String session, final String timestamp, final String project, final XnatPetmrsessiondataBean petmrSession) throws Exception {
        final SessionData sessionData = getSession(session, timestamp, project);

        final XnatUserProvider provider = XDAT.getContextService().getBean("receivedFileUserProvider", XnatUserProvider.class);
        final UserI            importer = provider.get();

        final LockAndSync<Map<String, SessionData>> l = new LockAndSync<Map<String, SessionData>>(sessionData.getName(), sessionData.getTimestamp(), sessionData.getProject(), sessionData.getStatus()) {
            @Override
            Map<String, SessionData> extSync() throws SyncFailedException {
                final String label = petmrSession.getLabel();
                _mrSession = getUniqueSessionLabel(label, "PETMR", "MR", sessionData.getProject(), importer);
                _petSession = getUniqueSessionLabel(label, "PETMR", "PET", sessionData.getProject(), importer);
                _mrSessionTimestamp = PrearcUtils.makeTimestamp();
                do {
                    _petSessionTimestamp = PrearcUtils.makeTimestamp();
                } while (_mrSessionTimestamp.equals(_petSessionTimestamp));
                try {
                    _mrSessionFolder = PrearcUtils.getPrearcSessionDir(importer, sessionData.getProject(), _mrSessionTimestamp, _mrSession, true).getAbsolutePath();
                    _petSessionFolder = PrearcUtils.getPrearcSessionDir(importer, sessionData.getProject(), _petSessionTimestamp, _petSession, true).getAbsolutePath();
                } catch (Exception e) {
                    throw new SyncFailedException("Sync failed trying to create new session folders", e);
                }
                final Map<String, List<String>> separatedScans;
                try {
                    separatedScans = separateScans(petmrSession);
                } catch (IOException e) {
                    throw new SyncFailedException("An error occurred trying to separate the scans", e);
                }
                _mrScanIds = separatedScans.get("MR");
                _petScanIds = separatedScans.get("PT");
                sessionDelegate.moveScans(sessionData, _mrSession, _mrSessionFolder, _mrScanIds);
                sessionDelegate.moveScans(sessionData, _petSession, _petSessionFolder, _petScanIds);

                s = new HashMap<>();
                s.put("MR", getSessionData(_mrSessionFolder));
                s.put("PT", getSessionData(_petSessionFolder));
                return s;
            }

            @Override
            void cacheSync() throws Exception {
                modifySession(sess, timestamp, proj, new SessionOp<Void>() {
                    public Void op() throws Exception {
                        SessionData mrSessionData = s.get("MR");
                        SessionData petSessionData = s.get("PT");
                        try {
                            addSession(mrSessionData);
                            final File mrSessionDir = new File(_mrSessionFolder);
                            setStatus(mrSessionDir.getName(), _mrSessionTimestamp, project, PrearcUtils.PrearcStatus.BUILDING);
                            buildSession(mrSessionDir, mrSessionDir.getName(), _mrSessionTimestamp, project, sessionData.getVisit(), sessionData.getProtocol(), sessionData.getTimeZone(), sessionData.getSource());
                            PrearcUtils.resetStatus(importer, project, _mrSessionTimestamp, mrSessionDir.getName(), true);
                            PrearcUtils.log(mrSessionData, String.format("Moved %d scans from %s to %s", _mrScanIds.size(), sessionData.getName(), _mrSession));

                            addSession(petSessionData);
                            final File petSessionDir = new File(_petSessionFolder);
                            setStatus(petSessionDir.getName(), _petSessionTimestamp, project, PrearcUtils.PrearcStatus.BUILDING);
                            buildSession(petSessionDir, petSessionDir.getName(), _petSessionTimestamp, project, sessionData.getVisit(), sessionData.getProtocol(), sessionData.getTimeZone(), sessionData.getSource());
                            PrearcUtils.resetStatus(importer, project, _petSessionTimestamp, petSessionDir.getName(), true);
                            PrearcUtils.log(petSessionData, String.format("Moved %d scans from %s to %s", _petScanIds.size(), sessionData.getName(), _petSession));
                        } catch (SyncFailedException e) {
                            log.error("Session sync failed", e);
                            throw new IllegalStateException(e.getMessage());
                        } finally {
                            if (mrSessionData != null && petSessionData != null) {
                                _unsafeDeleteSession(sess, timestamp, proj);
                            }
                        }
                        return null;
                    }
                });
            }

            @Override
            boolean checkStatus() {
                return sessionData.getStatus().equals(PrearcStatus.SEPARATING);
            }

            private SessionData getSessionData(final String folder) {
                final File path = Paths.get(folder).toFile();
                final SessionData newSessionData = new SessionData();
                newSessionData.setName(path.getName());
                newSessionData.setFolderName(path.getName());
                newSessionData.setSubject(sessionData.getSubject());
                newSessionData.setProject(path.getParentFile().getParentFile().getName());
                newSessionData.setUrl(path.getAbsolutePath());
                newSessionData.setUploadDate(sessionData.getUploadDate());
                newSessionData.setTimestamp(path.getParentFile().getName());
                newSessionData.setScan_date(sessionData.getScan_date());
                newSessionData.setScan_time(sessionData.getScan_time());
                newSessionData.setTag(sessionData.getTag());
                newSessionData.setProtocol(sessionData.getProtocol());
                newSessionData.setSource(sessionData.getSource());
                newSessionData.setVisit(sessionData.getVisit());
                newSessionData.setTimeZone(sessionData.getTimeZone());
                newSessionData.setAutoArchive(sessionData.getAutoArchive());
                newSessionData.setPreventAnon(sessionData.getPreventAnon());
                newSessionData.setPreventAutoCommit(sessionData.getPreventAutoCommit());
                newSessionData.setStatus(PrearcStatus.READY);
                return newSessionData;
            }

            String _mrSession;
            String _petSession;
            String _mrSessionTimestamp;
            String _petSessionTimestamp;
            String _mrSessionFolder;
            String _petSessionFolder;
            List<String> _mrScanIds;
            List<String> _petScanIds;
        };

        boolean ran;
        Exception e = null;
        try {
            ran = l.run();
        } catch (Exception _e) {
            log.error("", _e);
            e = _e;
            ran = false;
        }

        if (!ran) {
            wrapException(e);
            return null;
        }

        return l.s;
    }

    public static SeriesImportFilter getSplitPetMrSessionsFilter() throws IOException {
        final ScriptService service = XDAT.getContextService().getBean(ScriptService.class);
        final Script script = service.getByScriptId(SPLIT_PETMR_SESSION_ID);
        final String content;
        if (script == null) {
            content = DEFAULT_SPLIT_PETMR_SESSION_FILTER;
        } else {
            content = script.getContent();
        }
        final LinkedHashMap<String, String> keys = XDAT.getSerializerService().deserializeJson(content, SeriesImportFilter.MAP_TYPE_REFERENCE);
        return DicomFilterService.buildSeriesImportFilter(keys);
    }

    @SuppressWarnings("SameParameterValue")
    static String getUniqueSessionLabel(final String stem, final String target, final String replacement, final String projectId, final UserI user) {
        String label;
        if (stem.contains(target.toUpperCase())) {
            label = stem.replace(target.toUpperCase(), replacement.toUpperCase());
        } else if (stem.contains(target.toLowerCase())) {
            label = stem.replace(target.toLowerCase(), replacement.toLowerCase());
        } else {
            label = stem + "_" + replacement;
        }

        int index = 0;
        while (XnatExperimentdata.GetExptByProjectIdentifier(projectId, getExperimentId(label, index), user, false) != null) {
            index++;
        }

        return getExperimentId(label, index);
    }

    private static String getExperimentId(String label, int index) {
        return label + (index == 0 ? "" : Integer.toString(index));
    }

    private static Map<String, List<String>> separateScans(final XnatPetmrsessiondataBean petmrSession) throws IOException {
        final Map<String, List<String>> scansByModality = new HashMap<>();
        scansByModality.put("MR", new ArrayList<String>());
        scansByModality.put("PT", new ArrayList<String>());

        final SeriesImportFilter splitPetMrSessionFilter = getSplitPetMrSessionsFilter();

        final List<XnatImagescandataI> scans = petmrSession.getScans_scan();
        if (log.isDebugEnabled()) {
            log.debug("Processing {} scans from the PET/MR session {} from the scanner {} in the project {}", scans.size(), petmrSession.getId(), petmrSession.getScanner(), petmrSession.getProject());
        }
        for (final XnatImagescandataI scan : scans) {
            final String index = scan.getId();
            final String modality = getScanModality(scan);
            final String description = scan.getSeriesDescription();
            if (log.isDebugEnabled()) {
                log.debug("Processing scan {} with modality {} and description {}", index, modality, description);
            }
            final Map<String, String> headers = new HashMap<>();
            headers.put("SeriesNumber", index);
            headers.put("Modality", modality);
            headers.put("SeriesDescription", description);
            final String foundModality = splitPetMrSessionFilter.findModality(headers);
            if (StringUtils.isNotEmpty(foundModality)) {
                final List<String> foundScans = scansByModality.get(foundModality);
                if (foundScans != null) {
                    foundScans.add(index);
                } else {
                    log.warn("Session " + petmrSession.getLabel() + " scan " + scan.getId() + "\"" + scan.getSeriesDescription() + "\" has a modality that didn't map to MR or PET according to the split PET/MR session series import filter: " + foundModality);
                }
            }
        }
        return scansByModality;
    }

    // TODO: This should use modality-mapped series import filters to define the appropriate modality.
    private static String getScanModality(final XnatImagescandataI scan) {
        if (!StringUtils.isBlank(scan.getModality())) {
            return scan.getModality();
        }
        if (scan instanceof XnatPetscandataI) {
            return "PT";
        }
        return "MR";
    }

    @SuppressWarnings("unused")
    private static void copySessionMetaData(final XnatPetmrsessiondataBean petmrSession, final XnatPetsessiondataBean petSession, final XnatMrsessiondataBean mrSession) {
        final Method[] methods = XnatPetmrsessiondataBean.class.getMethods();
        for (final Method method : methods) {
            if (Reflection.isGetter(method)) {
                try {
                    final Object value = method.invoke(petmrSession);
                    invokeSetter(method, petSession, value);
                    invokeSetter(method, mrSession, value);
                } catch (IllegalAccessException e) {
                    // This shouldn't happen since we're checking for accessibility, but still...
                    log.warn("Illegal access of method " + method.getName(), e);
                } catch (InvocationTargetException e) {
                    log.warn("An error occurred invoking method " + method.getName(), e);
                }
            }
        }
        petSession.setId(petmrSession.getId().replace("PETMR", "PET"));
        petSession.setLabel(petmrSession.getId().replace("PETMR", "PET"));
        mrSession.setId(petmrSession.getId().replace("PETMR", "MR"));
        mrSession.setLabel(petmrSession.getLabel().replace("PETMR", "MR"));

        // TODO: Fill this in.
    }

    private static void invokeSetter(final Method method, final Object target, final Object value) {
        try {
            final Method setter = target.getClass().getMethod(method.getName().replaceFirst("get", "set"), method.getReturnType());
            setter.invoke(target, value);
        } catch (NoSuchMethodException ignored) {
            // This is totally OK: it just means that, e.g., the MR session bean doesn't have one of the PET properties or vice versa.
        } catch (IllegalAccessException e) {
            // This shouldn't happen since we're checking for accessibility, but still...
            log.warn("Illegal access of method " + method.getName(), e);
        } catch (InvocationTargetException e) {
            log.warn("An error occurred invoking method " + method.getName(), e);
        }
    }

    private static void pruneDatabase() throws Exception {
        // construct list of timestamps with extant folders
        // delete all prearchive entries that are not in that timestamp set
        deleteUnusedPrearchiveEntries(getPrearchiveFolderTimestamps());
    }

    private static Set<String> getPrearchiveFolderTimestamps() {
        final Set<String> timestamps = new HashSet<>();
        timestamps.add("0"); // there must be at least one element in the list
        final File baseDir = new File(prearcPath);
        if (!baseDir.exists()) {
            final boolean success = baseDir.mkdirs();
            if (!success) {
                throw new NrgServiceRuntimeException(NrgServiceError.Unknown, "Couldn't create the base prearchive folder in " + baseDir.getPath());
            }
            // One thing we know: if we had to create this folder, there ain't anything in it.
            return timestamps;
        }
        final File[] dirs = baseDir.listFiles(FileSystemSessionTrawler.hiddenAndDatabaseFileFilter);
        if (dirs != null) {
            for (final File dir : dirs) {
                timestamps.add(dir.getName());
                final String[] folders = dir.list();
                if (folders != null) {
                    timestamps.addAll(Arrays.asList(folders));
                }
            }
        }
        return timestamps;
    }

    private static void deleteUnusedPrearchiveEntries(final Set<String> timestamps) throws Exception {
        new SessionOp<Void>() {
            public Void op() throws Exception {
                final String query = DatabaseSession.deleteUnusedSessionsSql("'" + StringUtils.join(Iterables.transform(timestamps, new Function<String, String>() {
                    @Override
                    public String apply(final String timestamp) {
                        return RegExUtils.replaceAll(timestamp, "'", "''");
                    }
                }), "', '") + "'");
                log.info("Deleting unused sessions with the query: {}", query);
                PoolDBUtils.ExecuteNonSelectQuery(query, null, null);
                return null;
            }
        }.run();
    }

    public static void moveToProject(final String sess, final String timestamp, final String proj, final String destination) throws Exception {
        final SessionData sessionData = getSession(sess, timestamp, proj);
        final String project = sessionData.getProject();
        if (!sessionData.getStatus().equals(PrearcStatus._MOVING) && markSession(sessionData.getSessionDataTriple(), PrearcStatus.MOVING)) {
            if (!project.equals(destination)) {
                _moveToProject(sessionData.getFolderName(), sessionData.getTimestamp(), sessionData.getProject(), destination);
            } else {
                // cannot move a session back on itself.
                markSession(sessionData.getSessionDataTriple(), PrearcStatus.READY);
            }
        }
    }

    public static Map<String, SessionData> separatePetMrSession(final String session, final String timestamp, final String project, final XnatPetmrsessiondataBean petmrSession) throws Exception {
        final SessionData sessionData = getSession(session, timestamp, project);
        if (!sessionData.getStatus().equals(PrearcStatus._SEPARATING) && markSession(sessionData.getSessionDataTriple(), PrearcStatus.SEPARATING)) {
            return _separatePetMrSession(session, timestamp, project, petmrSession);
        } else {
            // Something weird happened...
            log.error("Couldn't separate the session {}, not sure what happened.", sessionData.getUrl());
            markSession(sessionData.getSessionDataTriple(), PrearcStatus.READY);
            return null;
        }
    }

    /**
     * Move a session from the prearchive to the archive.
     *
     * @param sessions              The sessions to archive
     * @param overrideExceptions    Whether archiving should continue if there's an exception.
     * @param allowSessionMerge     Whether sessions should be merged.
     * @param overwriteFiles        Whether existing session files should be overwritten.
     * @param user                  The requesting user.
     * @param listeners             Any listeners to be notified.
     *
     * @return Return true if successful, false otherwise
     *
     * @throws Exception When an unknown error occurs.
     */
    public static Map<SessionDataTriple, Boolean> archive(final List<PrearcSession> sessions, final Boolean overrideExceptions, final Boolean allowSessionMerge, final Boolean overwriteFiles, final UserI user, final Set<StatusListenerI> listeners) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("User {} requested to archive {} sessions: {}", user.getUsername(), sessions.size(), StringUtils.join(Lists.transform(sessions, new Function<PrearcSession, String>() {
                @Override
                public String apply(final PrearcSession session) {
                    return session.getProject() + "-" + session.getTimestamp() + "-" + session.getFolderName();
                }
            }), ", "));
        } else {
            log.info("User {} requested to archive {} sessions", user.getUsername(), sessions.size());
        }

        final Map<SessionDataTriple, Boolean> ret = markSessions(Lists.transform(sessions, new Function<PrearcSession, SessionDataTriple>() {
            @Override
            public SessionDataTriple apply(final PrearcSession session) {
                return SessionDataTriple.fromPrearcSession(session);
        }
        }));
        new Thread() {
            public void run() {
                for (final PrearcSession session : sessions) {
                    try {
                        log.debug("Now starting archive of session {}", session);
                        _archive(session, overrideExceptions, allowSessionMerge, overwriteFiles, user, listeners, true);
                    } catch (SyncFailedException e) {
                        log.error("An error occurred trying to sync the session {}", session, e);
                    }
                }
            }
        }.start();
        return ret;
    }

    public static String archive(PrearcSession session, final Boolean overrideExceptions, final Boolean allowSessionMerge, final Boolean overwriteFiles, final UserI user, final Set<StatusListenerI> listeners) throws SyncFailedException {
        log.debug("Now starting archive of session {}", session);
        return _archive(session, overrideExceptions, allowSessionMerge, overwriteFiles, user, listeners, false);
    }

    private static String _archive(final PrearcSession session, final Boolean overrideExceptions, final Boolean allowSessionMerge, final Boolean overwriteFiles, final UserI user, final Set<StatusListenerI> listeners, final boolean waitFor) throws SyncFailedException {
        final String folder    = session.getFolderName();
        final String timestamp = session.getTimestamp();
        final String project = session.getProject();
        log.info("Now archiving the session {} with {} listeners", formatSession(project, timestamp, folder), listeners == null ? 0 : listeners.size());

        final PrearcSessionArchiver archiver;
        try {
            archiver = new PrearcSessionArchiver(session, user, session.getAdditionalValues(), overrideExceptions, allowSessionMerge, waitFor, overwriteFiles);
        } catch (Exception e1) {
            PrearcUtils.log(project, timestamp, folder, e1);
            throw new IllegalStateException(e1);
        }

        if (listeners != null && !listeners.isEmpty()) {
        ListenerUtils.addListeners(listeners, archiver);
        }

        final SessionData sd;
        try {
            sd = session.getSessionData();
        } catch (Exception e) {
            PrearcUtils.log(project, timestamp, folder, e);
            throw new IllegalStateException(e);
        }

        final LockAndSync<String> lockAndSync = new LockAndSync<String>(folder, timestamp, project, sd.getStatus()) {
            String extSync() throws SyncFailedException {
                try {
                    return archiver.call();
                } catch (Exception e) {
                    throw new SyncFailedException(e.getMessage(), e);
                }
            }

            void cacheSync() throws Exception {
                modifySession(sess, timestamp, proj, new SessionOp<Void>() {
                    public Void op() throws Exception {
                        final String query = DatabaseSession.deleteSessionSql(sess, timestamp, proj);
                        log.info("Archiving should be complete, so deleting the session {} with query: {}", formatSession(proj, timestamp, sess), query);
                        PoolDBUtils.ExecuteNonSelectQuery(query, null, null);
                        return null;
                    }
                });
            }

            @Override
            boolean checkStatus() {
                return sd.getStatus().equals(PrearcStatus.ARCHIVING);
            }
        };

        try {
            lockAndSync.run();
        } catch (Exception e) {
            log.error("An error occurred trying to archive the session {}", formatSession(project, timestamp, folder), e);
            PrearcUtils.log(sd, e);
            wrapException(e);
        }

        return lockAndSync.s;
    }

    public static void wrapException(Exception e) throws SyncFailedException {
        if ((e instanceof SyncFailedException && e.getCause() != null)) {
            throw new SyncFailedException("Operation Failed: " + e.getCause().getMessage(), e.getCause());
        } else {
            throw new SyncFailedException("Operation Failed: " + e.getMessage(), e);
        }
    }

    public static void buildSession(final File sessionDir, final String session, final String timestamp, final String project, final String visit, final String protocol, final String timezone, final String source) throws Exception {
        final SessionData sd = getSession(session, timestamp, project);

        try {
            new LockAndSync<Void>(session, timestamp, project, sd.getStatus()) {
                Void extSync() throws SyncFailedException {
                    final Map<String, String> params = new LinkedHashMap<>();
                    if (!Strings.isNullOrEmpty(project) && !UNASSIGNED.equals(project)) {
                        params.put("project", project);
                        params.put("separatePetMr", PrearcUtils.getSeparatePetMr(project));
                    } else {
                        params.put("separatePetMr", PrearcUtils.getSeparatePetMr());
                    }
                    params.put("label", session);
                    final String subject = sd.getSubject();
                    if (!Strings.isNullOrEmpty(subject)) {
                        params.put("subject_ID", sd.getSubject());
                    }
                    if (!Strings.isNullOrEmpty(visit)) {
                        params.put("visit", visit);
                    }
                    if (!Strings.isNullOrEmpty(protocol)) {
                        params.put("protocol", protocol);
                    }
                    if (!Strings.isNullOrEmpty(timezone)) {
                        params.put("TIMEZONE", timezone);
                    }
                    if (!Strings.isNullOrEmpty(source)) {
                        params.put("SOURCE", source);
                    }

                    PrearcUtils.cleanLockDirs(sd.getSessionDataTriple());

                    try {
                        final File sessionXmlFile = new File(sessionDir.getPath() + ".xml");
                        log.info("Attempting to build prearchive session in folder '{}' into the session XML file '{}'", sessionDir.getPath(), sessionXmlFile.getPath());

                        final Boolean success = new XNATSessionBuilder(sessionDir, sessionXmlFile, true, params).call();
                        if (BooleanUtils.isNotTrue(success)) {
                            throw new SyncFailedException("Error building session");
                        }
                    } catch (SyncFailedException e) {
                        throw e;
                    } catch (Throwable t) {
                        throw new SyncFailedException("Error building session", t);
                    }
                    return null;
                }

                void cacheSync() {
                }

                @Override
                boolean checkStatus() {
                    return sd.getStatus().equals(PrearcStatus.BUILDING);
                }
            }.run();
        }
        // cacheSync is empty so it can't throw an exception
        catch (SQLException | SessionException ignored) {
        }
    }

    protected static boolean markSession(SessionDataTriple ss, PrearcUtils.PrearcStatus s) throws Exception {
        return setStatus(ss.getFolderName(), ss.getTimestamp(), ss.getProject(), s);
    }

    protected static Map<SessionDataTriple, Boolean> markSessions(List<SessionDataTriple> ss) throws Exception {
        java.util.Iterator<SessionDataTriple> i = ss.iterator();
        Map<SessionDataTriple, Boolean> ret = new HashMap<>();
        while (i.hasNext()) {
            SessionDataTriple t = i.next();
            ret.put(t, markSession(t, PrearcStatus.ARCHIVING));
            }
        return ret;
    }

    /**
     * Move to project by uri.
     *
     * @param uri    The project URI.
     *
     * @return Whether the move was successful.
     *
     * @throws SessionException
     * @throws SyncFailedException
     * @throws SQLException
     */
    public static void moveToProject(final String uri) throws Exception, SQLException {
        final PrearcUriParserUtils.SessionParser parser = new PrearcUriParserUtils.SessionParser(new PrearcUriParserUtils.UriParser(XNATApplication.PREARC_SESSION_URI));
        final Map<String, String> session = parser.readUri(uri);
        moveToProject(session.get("SESSION_LABEL"), session.get("SESSION_TIMESTAMP"), session.get("PROJECT_ID"), parser.i.f.getValues("dest"));
    }


    /**
     * Set the status of an existing session. All arguments must be non-null and non-empty. Allows the user to set an in-process status (i.e a status that begins with '_')
     *
     * @param sess          Session label.
     * @param timestamp     The session timestamp.
     * @param proj          Project name.
     * @param status        Status to be set.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static boolean setStatus(final String sess, final String timestamp, final String proj, final PrearcUtils.PrearcStatus status) throws Exception {
        return setStatus(sess, timestamp, proj, status, false);
    }

    /**
     * Set the status of an existing session. All arguments must be non-null and non-empty. Allows the user to set an in-process status (i.e a status that begins with '_')
     *
     * @param sessionFolder          Session label.
     * @param timestamp     The session timestamp.
     * @param project          Project name.
     * @param status        Status to be set.
     *
     * @return True if the status was set properly, false otherwise.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static boolean setStatus(final String sessionFolder, final String timestamp, final String project, final PrearcUtils.PrearcStatus status, final boolean overrideLock) throws Exception {
        if (!overrideLock && isLocked(sessionFolder, timestamp, project)) {
            log.info("The prearc session {} is locked and the override lock flag is set to false. Can't set the status to {} as requested.", formatSession(project, timestamp, sessionFolder), status);
            return false;
        }
        unsafeSetStatus(sessionFolder, timestamp, project, status);
        return true;
    }

    /**
     * Set the status of a session, accept the status as a string and before setting it first check that the given status isn't one that can lock a session (i.e begins with '_').
     * <p/>
     * However a status of "_RECEIVING" is allowed because it allows the sys admin to lock a session directory if they need to mess with it manually.
     *
     * @return True if the status was set properly, false otherwise.
     */
    public static boolean setStatus(final String sess, final String timestamp, final String proj, final String status) throws Exception {
        PrearcUtils.PrearcStatus p = PrearcUtils.PrearcStatus.valueOf(status);
        if (PrearcUtils.inProcessStatusMap.containsValue(p)) {
            throw new SessionException(InvalidStatus, "Cannot set session status to " + status);
        } else {
            return setStatus(sess, timestamp, proj, p);
        }
    }

    /**
     * Set the status of an existing session. No check is performed to see if the database is locked. Allows the user to set an in-process status (i.e a status that begins with '_')
     *
     * @param sessionFolder          Session label.
     * @param timestamp     The session timestamp.
     * @param project          Project name.
     * @param status        Status to be set.
     *
     * @throws Exception        When an unknown error occurs.
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static void unsafeSetStatus(final String sessionFolder, final String timestamp, final String project, final PrearcUtils.PrearcStatus status) throws Exception {
        if (null == status) {
            throw new SessionException(InvalidStatus, "Status argument is null or empty");
        }
        log.debug("Attempting to set the status of prearchive session {} to status {}", formatSession(project, timestamp, sessionFolder), status);
        modifySession(sessionFolder, timestamp, project, new SessionOp<Void>() {
            public Void op() throws Exception {
                PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.STATUS.updateSessionSql(sessionFolder, timestamp, project, status), null, null);
                return null;
            }
        });
    }

    /**
     * Set the status given a uri specifying the project, timestamp and session and the new status. Allows the user to set an in-process status (i.e a status that begins with '_')
     *
     * @param uri       The project URI.
     * @param status    Status to be set.
     *
     * @return True if the status was set properly, false otherwise.
     *
     * @throws Exception        When an unknown error occurs.
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static boolean setStatus(final String uri, final PrearcUtils.PrearcStatus status) throws Exception {
        return setStatus(uri, status, false);
    }

    /**
     * Set the status given the uri specifying the project,timestamp and session, and an override lock that will that will set status even if the session is locked. Allows the user to set an in-process status (i.e a status that begins with '_')
     *
     * @param uri             The project URI.
     * @param status          Status to be set.
     * @param overrideLock    Whether an existing lock should be overridden.
     *
     * @return True if the status was set properly, false otherwise.
     *
     * @throws Exception        When an unknown error occurs.
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static boolean setStatus(final String uri, final PrearcUtils.PrearcStatus status, boolean overrideLock) throws Exception {
        final PrearcUriParserUtils.SessionParser parser = new PrearcUriParserUtils.SessionParser(new PrearcUriParserUtils.UriParser(XNATApplication.PREARC_SESSION_URI));
        final Map<String, String> sess = parser.readUri(uri);
        return setStatus(sess.get("SESSION_LABEL"), sess.get("SESSION_TIMESTAMP"), sess.get("PROJECT_ID"), status, overrideLock);
    }

    /**
     * Delete a session from the prearchive database. if the session is locked.
     *
     * @param sess         Session label.
     * @param timestamp    The session timestamp.
     * @param proj         Project name.
     *
     * @throws Exception           When an unknown error occurs.
     * @throws SQLException        When an error occurs running a query.
     * @throws SessionException    When an error occurs with the session.
     * @throws SyncFailedException When the sync fails.
     */
    public static void deleteCacheRow(final String sess, final String timestamp, final String proj) throws Exception, SyncFailedException {
        final SessionData sd = getSession(sess, timestamp, proj);
        log.debug("Got a request to delete the cache entry {} with status: {}", formatSession(proj, timestamp, sess), sd.getStatus());
        new LockAndSync<Void>(sess, timestamp, proj, sd.getStatus()) {
            protected boolean checkStatus() {
                return PrearcStatus._DELETING.equals(status);
            }

            Void extSync() {
                return null;
            }

            void cacheSync() throws Exception {
                modifySession(sess, timestamp, proj, new SessionOp<Void>() {
                    public Void op() throws Exception {
                        final String query = DatabaseSession.deleteSessionSql(sess, timestamp, proj);
                        log.debug("Deleting the cache entry {} with query: {}", formatSession(proj, timestamp, sess), query);
                        PoolDBUtils.ExecuteNonSelectQuery(query, null, null);
                        return null;
                    }
                });
            }
        }.run();
    }


    /**
     * Delete a session from the prearchive database. if the session is locked.
     *
     * @param sess         Session label.
     * @param timestamp    The session timestamp.
     * @param proj         Project name.
     *
     * @throws Exception           When an unknown error occurs.
     * @throws SQLException        When an error occurs running a query.
     * @throws SessionException    When an error occurs with the session.
     * @throws SyncFailedException When the sync fails.
     */
    private static void _deleteSession(final String sess, final String timestamp, final String proj) throws Exception, SyncFailedException {
        final SessionData sessionData = getSession(sess, timestamp, proj);
        log.debug("Got a request to delete the session {} with status: {}", sessionData, sessionData.getStatus());
        final LockAndSync<Void> lockAndSync = new LockAndSync<Void>(sess, timestamp, proj, sessionData.getStatus()) {
            protected boolean checkStatus() {
                return PrearcStatus.DELETING.equals(status);
            }

            Void extSync() throws SyncFailedException {
                sessionDelegate.delete(sessionData);
                return null;
            }

            void cacheSync() throws Exception {
                withSession(sess, timestamp, proj, new SessionOp<Void>() {
                    public Void op() throws Exception {
                        final String query = DatabaseSession.deleteSessionSql(sess, timestamp, proj);
                        log.debug("Deleting the session {} with query: {}", sessionData, query);
                        PoolDBUtils.ExecuteNonSelectQuery(query, null, null);
                        return null;
                    }
                });
            }
        };

        Exception e = null;
        try {
            lockAndSync.run();
        } catch (Exception _e) {
            log.error("", _e);
            e = _e;
        }

        if (e != null) {
            wrapException(e);
        }
    }

    private static void _unsafeDeleteSession(final String sess, final String timestamp, final String proj) throws Exception {
        final SessionData sessionData = getSession(sess, timestamp, proj);
        new LockAndSync<Void>(sess, timestamp, proj, sessionData.getStatus()) {
            protected boolean checkStatus() {
                return true;
            }

            Void extSync() throws SyncFailedException {
                sessionDelegate.delete(sessionData);
                return null;
            }

            void cacheSync() throws Exception {
                modifySession(sess, timestamp, proj, new SessionOp<Void>() {
                    public Void op() throws Exception {
                        final String query = DatabaseSession.deleteSessionSql(sess, timestamp, proj);
                        log.debug("Deleting the session {} with query: {}", sessionData, query);
                        PoolDBUtils.ExecuteNonSelectQuery(query, null, null);
                        return null;
                    }
                });
            }
        }.run();
    }

    /**
     * Delete the prearchive row with the given session, timestamp,project triple
     *
     * @param sess         Session label.
     * @param timestamp    The session timestamp.
     * @param proj         Project name.
     *
     * @return True if the session was deleted properly, false otherwise.
     *
     * @throws Exception           When an unknown error occurs.
     * @throws SQLException        When an error occurs running a query.
     * @throws SessionException    When an error occurs with the session.
     * @throws SyncFailedException When the sync fails.
     */
    public static void deleteSession(final String sess, final String timestamp, final String proj) throws Exception, SessionException, SyncFailedException {
        new PredicatedOp<Void, Void>() {
            @Override
            boolean predicate() throws Exception {
                final SessionData sessionData = getSessionIfExists(sess, timestamp, proj);
                if (sessionData == null) {
                    log.error("Got a request to delete the session {}, but I couldn't find it.", formatSession(proj, timestamp, sess));
                    return false;
                }
                if (!sessionData.getStatus().equals(PrearcStatus.QUEUED_DELETING)) {
                    log.error("Got a request to delete the session {}, but that session has not been marked as QUEUED_DELETING.", sessionData);
                    return false;
                }
                if (!markSession(sessionData.getSessionDataTriple(), PrearcStatus.DELETING)) {
                    log.error("Got a request to delete the session {}, but that failed when marked as DELETING.", sessionData);
                    return false;
                }
                log.debug("Got a request to delete the session {}, marked it as DELETING, we're good to go.", sessionData);
                return true;
            }

            @Override
            Either<Void, Void> trueOp() throws Exception {
                log.debug("Now trying to delete the session {}", formatSession(proj, timestamp, sess));
                _deleteSession(sess, timestamp, proj);
                return new Either<Void, Void>() {
                }.setRight(null);
            }

            @Override
            Either<Void, Void> falseOp() {
                log.debug("Something went wrong, so I'm not trying to delete the session {}", formatSession(proj, timestamp, sess));
                return new Either<Void, Void>() {
                }.setLeft(null);
            }

        }.run();
    }

    /**
     * Abstract holding results of a binary choice. Inspired by Haskell's Either datatype the Left branch is typically used to store the results of an error and the Right branch stores the results of a successful operation.
     * <p/>
     * The user of this class needs to make sure that only one of the branches is set.
     *
     * @param <Left>  Return type if the left branch of tree is taken.
     * @param <Right> Return type if the right branch of the tree is taken.
     *
     * @author aditya
     */
    public static abstract class Either<Left, Right> {
        enum Eithers {LEFT, RIGHT}

        // typically the result of an error
        Left l;
        // typically the result of a successful operation
        Right r;
        // true if Right is not null, false if Left is not null.
        Eithers set;

        Either<Left, Right> setLeft(Left l) {
            this.set = Eithers.LEFT;
            this.l = l;
            return this;
        }

        Either<Left, Right> setRight(Right r) {
            this.set = Eithers.RIGHT;
            this.r = r;
            return this;
        }

        public Left getLeft() {
            return this.l;
        }

        public Right getRight() {
            return this.r;
        }

        public boolean isLeft() {
            return this.set == Eithers.LEFT;
        }

        public boolean isRight() {
            return this.set == Eithers.RIGHT;
        }
    }

    /**
     * Abstract running a operation depending on the value of a predicate. Testing the predicate and running the operation are done atomically and are thus thread-safe.
     *
     * @param <X> Return type if the predicate fails
     * @param <Y> Return type if the predicate holds
     *
     * @author aditya
     */
    static abstract class PredicatedOp<X, Y> {
        // the predicate
        abstract boolean predicate() throws Exception;

        // run if predicate holds
        abstract Either<X, Y> trueOp() throws Exception;

        // run if predicate fails
        abstract Either<X, Y> falseOp() throws Exception;

        // thread-safe driver
        synchronized Either<X, Y> run() throws Exception {
            if (predicate()) {
                return trueOp();
            } else {
                return falseOp();
            }
        }
    }

    /**
     * Retrieve a session if it exists or null.
     *
     * @param session      The session name.
     * @param timestamp    The session timestamp.
     * @param project      The project of the session.
     *
     * @return The session data if it exists, null otherwise.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     * @throws Exception        When an unknown error occurs.
     */
    public static SessionData getSessionIfExists(final String session, final String timestamp, final String project) throws SQLException, SessionException, Exception {
        Either<Void, SessionData> result = new PredicatedOp<Void, SessionData>() {
            /**
             * Retrieve the session for prearchive table
             */
            Either<Void, SessionData> trueOp() throws Exception {
                return new Either<Void, SessionData>() {
                }.setRight(getSession(session, timestamp, project));
            }

            /**
             * Set the result to null
             */
            Either<Void, SessionData> falseOp() {
                return new Either<Void, SessionData>() {
                }.setLeft(null);
            }

            /**
             * Test whether the session exists
             */
            boolean predicate() throws Exception {
                return exists(session, timestamp, project);
            }
        }.run();

        if (result.isLeft()) {
            return null;
        } else {
            return result.getRight();
        }
    }

    /**
     * A class that abstracts syncing of the prearchive table and the filesystem. It ensures that a session is locked before any operation and any error that occurs on the filesystem side leaves the session with a status of ERROR.
     *
     * @param <T>
     *
     * @author aditya
     */
    static abstract class LockAndSync<T> {
        final String sess, timestamp, proj;
        final PrearcStatus status;
        T s;

        /**
         * The session, timestamp, proj triple on which to run this operation
         *
         * @param sess      The session label.
         * @param timestamp The session timestamp.
         * @param proj      The session project.
         * @param status    Session status.
         */
        LockAndSync(String sess, String timestamp, String proj, PrearcStatus status) {
            this.sess = sess;
            this.timestamp = timestamp;
            this.proj = proj;
            this.status = status;
        }

        abstract boolean checkStatus();

        abstract T extSync() throws SyncFailedException;

        abstract void cacheSync() throws Exception;

        boolean run() throws Exception {
            try {
                if (!checkStatus()) {
                    return false;
                }
                lockSession(this.sess, this.timestamp, this.proj);
                s = extSync();
                cacheSync();
                return true;
            } catch (SyncFailedException e) {
                log.error("", e);

                unLockSession(this.sess, this.timestamp, this.proj);
                if (((e.cause instanceof ClientException) && Status.CLIENT_ERROR_CONFLICT.equals(((ClientException) e.cause).getStatus()))) {
                    //if this failed due to a conflict
                    setStatus(sess, timestamp, proj, PrearcUtils.PrearcStatus.CONFLICT);
                    PrearcUtils.log(proj, timestamp, sess, e.cause);
                } else {
                    setStatus(sess, timestamp, proj, PrearcUtils.PrearcStatus.ERROR);
                    PrearcUtils.log(proj, timestamp, sess, (e.cause != null) ? e.cause : e);
                }
                throw e;
            } catch (Exception e) {
                log.error("", e);
                unLockSession(this.sess, this.timestamp, this.proj);
                PrearcUtils.log(proj, timestamp, sess, e);
                throw e;
            }
        }
    }

    /**
     * A URI decoding wrapper around {@link PrearcDatabase#isLocked(String, String, String)}
     *
     * @param uri The URI to test.
     *
     * @return Returns true if the session is locked, false otherwise.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    protected static boolean isLocked(String uri) throws Exception, SQLException, SessionException {
        final PrearcUriParserUtils.SessionParser parser = new PrearcUriParserUtils.SessionParser(new PrearcUriParserUtils.UriParser(XNATApplication.PREARC_SESSION_URI));
        final Map<String, String> sess = parser.readUri(uri);
        return isLocked(sess.get("SESSION_LABEL"), sess.get("SESSION_TIMESTAMP"), sess.get("PROJECT_ID"));
    }


    /**
     * Check to see if the sessions locked against edits.
     *
     * @param sess      The session label.
     * @param timestamp The session timestamp.
     * @param proj      The session project.
     *
     * @return Returns true if the session is locked, false otherwise.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static boolean isLocked(final String sess, final String timestamp, final String proj) throws Exception, SQLException, SessionException {
        SessionData sd = getSession(sess, timestamp, proj);
        return PrearcUtils.inProcessStatusMap.containsValue(sd.getStatus());
    }

    /**
     * Reset the session status to READY
     *
     * @param uri
     *
     * @throws Exception
     * @throws SQLException
     * @throws SessionException
     */
    public static void unLockSession(String uri) throws Exception, SQLException, SessionException {
        final PrearcUriParserUtils.SessionParser parser = new PrearcUriParserUtils.SessionParser(new PrearcUriParserUtils.UriParser(XNATApplication.PREARC_SESSION_URI));
        final Map<String, String> sess = parser.readUri(uri);
        unLockSession(sess.get("SESSION_LABEL"), sess.get("SESSION_TIMESTAMP"), sess.get("PROJECT_ID"));
    }

    protected static void unLockSession(final String sess, final String timestamp, final String proj) throws Exception, SQLException, SessionException {
        try {
            getSession(sess, timestamp, proj);
            unsafeSetStatus(sess, timestamp, proj, PrearcUtils.PrearcStatus.READY);
        } catch (SessionException ignored) {

        }
    }

    /**
     * A URI decoding wrapper around {@link PrearcDatabase#lockSession(String, String, String)}
     *
     * @param uri
     *
     * @return
     *
     * @throws SQLException
     * @throws SessionException
     */
    protected static void lockSession(String uri) throws Exception, SQLException, SessionException {
        final PrearcUriParserUtils.SessionParser parser = new PrearcUriParserUtils.SessionParser(new PrearcUriParserUtils.UriParser(XNATApplication.PREARC_SESSION_URI));
        final Map<String, String> session = parser.readUri(uri);
        lockSession(session.get("SESSION_LABEL"), session.get("SESSION_TIMESTAMP"), session.get("PROJECT_ID"));
    }

    /**
     * A database row is locked by setting its status to the "locked" version its current status. {@link PrearcUtils#inProcessStatusMap} shows the mapping.
     *
     * @param sess      The session label.
     * @param timestamp The session timestamp.
     * @param proj      The session project.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    protected static void lockSession(final String sess, final String timestamp, final String proj) throws Exception, SQLException, SessionException {
        SessionData sd = getSession(sess, timestamp, proj);
        if (PrearcUtils.inProcessStatusMap.containsKey(sd.getStatus())) {
            final PrearcUtils.PrearcStatus inp = PrearcUtils.inProcessStatusMap.get(sd.getStatus());
            modifySession(sess, timestamp, proj, new SessionOp<Void>() {
                public Void op() throws Exception {
                    PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.STATUS.updateSessionSql(sess, timestamp, proj, inp), null, null);
                    return null;
                }
            });
        }
    }

    /**
     * A URI decoding wrapper for {@link PrearcDatabase#deleteSession(String, String, String)}
     *
     * @param uri
     *
     * @throws SQLException
     * @throws SessionException
     * @throws SyncFailedException
     */
    public static void deleteSession(final String uri) throws Exception, SQLException, SessionException, SyncFailedException {
        final PrearcUriParserUtils.SessionParser parser = new PrearcUriParserUtils.SessionParser(new PrearcUriParserUtils.UriParser(XNATApplication.PREARC_SESSION_URI));
        final Map<String, String> session = parser.readUri(uri);
        deleteSession(session.get("SESSION_LABEL"), session.get("SESSION_TIMESTAMP"), session.get("PROJECT_ID"));
    }

    /**
     * Search for a session given its name and project.
     *
     * @param sess Session label.
     * @param proj Project name.
     *
     * @return The session data if found.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException Throws if the given arguments match more than one session
     */
    public static SessionData getSession(final String sess, final String timestamp, final String proj) throws Exception, SQLException, SessionException {
        return withSession(sess, timestamp, proj, new SessionOp<SessionData>() {
            public SessionData op() throws Exception {
                final String query = DatabaseSession.findSessionSql(sess, timestamp, proj);
                log.debug("Trying to locate the session {}", formatSession(proj, timestamp, sess));
                final ResultSet resultSet = this.pdb.executeQuery(null, query, null);
                resultSet.next();
                return DatabaseSession.fillSession(resultSet);
            }
        });
    }

    /**
     * Gets the session from a given triple.
     *
     * @param triple The triple containing the session name, timestamp, and project.
     *
     * @return The corresponding session data.
     *
     * @throws Exception When something goes wrong.
     */
    public static SessionData getSession(final SessionDataTriple triple) throws Exception {
        return getSession(triple.getFolderName(), triple.getTimestamp(), triple.getProject());
    }

    /**
     * Set the prearchive row that corresponds to the given session, timestamp, project triple to the given autoArchive setting.
     *
     * @param sess        Session label.
     * @param timestamp   The session timestamp
     * @param proj        Project name.
     * @param autoArchive The value to set for auto-archive.
     *
     * @throws Exception        When an unknown error occurs.
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static void setAutoArchive(final String sess, final String timestamp, final String proj, final PrearchiveCode autoArchive) throws Exception, SQLException, SessionException {
        modifySession(sess, timestamp, proj, new SessionOp<Void>() {
            public Void op() throws Exception {
                log.debug("Setting auto-archive for {} to {}", formatSession(proj, timestamp, sess), autoArchive);
                PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.AUTOARCHIVE.updateSessionSql(sess, timestamp, proj, autoArchive), null, null);
                return null;
            }
        });
    }

    public static void setPreventAnon(final String sess, final String timestamp, final String proj, final boolean preventAnon) throws Exception {
        modifySession(sess, timestamp, proj, new SessionOp<Void>() {
            public Void op() throws Exception {
                log.debug("Setting prevent anon for {} to {}", formatSession(proj, timestamp, sess), preventAnon);
                PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.PREVENT_ANON.updateSessionSql(sess, timestamp, proj, preventAnon), null, null);
                return null;
            }
        });
    }

    public static void setSource(final String sess, final String timestamp, final String proj, final String source) throws Exception {
        modifySession(sess, timestamp, proj, new SessionOp<Void>() {
            public Void op() throws Exception {
                log.debug("Setting source for {} to {}", formatSession(proj, timestamp, sess), source);
                PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.SOURCE.updateSessionSql(sess, timestamp, proj, source), null, null);
                return null;
            }
        });
    }

    public static void setPreventAutoCommit(final String sess, final String timestamp, final String proj, final boolean preventAutoCommit) throws Exception {
        modifySession(sess, timestamp, proj, new SessionOp<Void>() {
            public Void op() throws Exception {
                log.debug("Setting prevent auto-commit for {} to {}", formatSession(proj, timestamp, sess), preventAutoCommit);
                PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.PREVENT_AUTO_COMMIT.updateSessionSql(sess, timestamp, proj, preventAutoCommit), null, null);
                return null;
            }
        });
    }

    /**
     * Return all sessions with the given session, timestamp and project. There should only be one row returned, but if not this function will return all the duplicate rows.
     *
     * @param sess
     * @param timestamp
     * @param proj
     *
     * @return
     *
     * @throws Exception
     * @throws SQLException
     * @throws SessionException
     */
    private static Collection<SessionData> unsafeGetSession(final String sess, final String timestamp, final String proj) throws Exception, SQLException, SessionException {
        return new SessionOp<Collection<SessionData>>() {
            public Collection<SessionData> op() throws SQLException, Exception {
                ResultSet rs = this.pdb.executeQuery(null, DatabaseSession.findSessionSql(sess, timestamp, proj), null);
                Collection<SessionData> ss = new ArrayList<SessionData>();
                while (rs.next()) {
                    ss.add(DatabaseSession.fillSession(rs));
                }
                return ss;
            }
        }.run();
    }

    /**
     * Return all sessions in the prearchive table
     *
     * @return All sessions in the prearchive table.
     *
     * @throws Exception        When an unknown error occurs.
     * @throws SessionException When an error occurs with the session.
     * @throws SQLException     When an error occurs running a query.
     */
    @Nonnull
    public static List<SessionData> getAllSessions() throws Exception {
        return new SessionOp<List<SessionData>>() {
            public List<SessionData> op() throws Exception {
                final List<SessionData> sessionData = new ArrayList<>();
                final ResultSet results = pdb.executeQuery(null, DatabaseSession.getAllRows(), null);
                while (results.next()) {
                    sessionData.add(DatabaseSession.fillSession(results));
                }
                return sessionData;
            }
        }.run();
    }

    /**
     * Search for a session given its UID.
     *
     * @param uid The UID on which to search.
     *
     * @return All matching sessions.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException Thrown if the given arguments match more than one session
     */
    public static Collection<SessionData> getSessionByUID(final String uid) throws Exception, SQLException, SessionException {
        return new SessionOp<Collection<SessionData>>() {
            public Collection<SessionData> op() throws Exception {
                final List<SessionData> matches = new ArrayList<>();
                ResultSet rs = this.pdb.executeQuery(null, DatabaseSession.TAG.findSql(uid), null);
                while (rs.next()) {
                    matches.add(DatabaseSession.fillSession(rs));
                }
                return matches;
            }
        }.run();
    }

    /**
     * Count the number of session in the database with the given name associated with the given project.
     *
     * @param sess      Session label.
     * @param proj      The session project.
     * @param timestamp The session timestamp.
     *
     * @return The number of sessions with the specified name.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static int countOf(final String sess, final String timestamp, final String proj) throws Exception, SQLException, SessionException {
        return new SessionOp<Integer>() {
            public Integer op() throws Exception {
                ResultSet rs = this.pdb.executeQuery(null, DatabaseSession.countSessionSql(sess, timestamp, proj), null);
                rs.next();
                return rs.getInt(1);
            }
        }.run();
    }

    /**
     * Either retrieve and existing session or create a new one. If a session is created an Either object with the "Right" branch set is returned. If we just retrieve one that is already in the prearchive table an Either object with the "Left" branch set is returned.
     * <p/>
     * This is useful in case the caller needs to know which operation was performed.
     *
     * @param sessionData The session data.
     * @param tsFile      The timestamp folder.
     * @param autoArchive The value to set for auto-archive.
     *
     * @return The created or retrieved session data.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     * @throws Exception        When an unknown error occurs.
     */
    public static synchronized Either<SessionData, SessionData> eitherGetOrCreateSession(final SessionData sessionData, final File tsFile, final PrearchiveCode autoArchive) throws SQLException, SessionException, Exception {
        return new PredicatedOp<SessionData, SessionData>() {
            SessionData _sessionData;

            /**
             * Return the found session
             * (non-Javadoc)
             * @see PredicatedOp#trueOp()
             */
            Either<SessionData, SessionData> trueOp() {
                return new Either<SessionData, SessionData>() {
                }.setRight(_sessionData);
            }

            /**
             * Create and return a new session
             */
            Either<SessionData, SessionData> falseOp() throws Exception {
                Either<SessionData, SessionData> result = new Either<SessionData, SessionData>() {
                };

                SessionData resultSession = new SessionOp<SessionData>() {
                    public SessionData op() throws Exception {
                        int    duplicates   = countOf(sessionData.getFolderName(), sessionData.getTimestamp(), sessionData.getProject());
                        int suffix = 1;
                        String suffixString = "";
                        while (duplicates == 1) {
                            suffixString = "_" + suffix;
                            duplicates = countOf(sessionData.getFolderName() + suffixString, sessionData.getTimestamp(), sessionData.getProject());
                            if (duplicates > 1) {
                                throw new SessionException(DatabaseError, "Database is in a bad state, " + duplicates + "sessions (name : " + sessionData.getFolderName() + " timestamp: " + sessionData.getTimestamp() + " project : " + sessionData.getProject());
                            }
                            suffix++;
                        }

                        sessionData.setFolderName(sessionData.getFolderName() + suffixString);
                        sessionData.setName(sessionData.getName() + suffixString);
                        sessionData.setUrl((new File(tsFile, sessionData.getFolderName()).getAbsolutePath()));
                        sessionData.setAutoArchive((Object) autoArchive);

                        PreparedStatement statement = this.pdb.getPreparedStatement(null, insertSql());
                        for (int i = 0; i < DatabaseSession.values().length; i++) {
                            DatabaseSession.values()[i].setInsertStatement(statement, sessionData);
                        }
                        statement.executeUpdate();
                        return getSession(sessionData.getFolderName(), sessionData.getTimestamp(), sessionData.getProject());
                    }
                }.run();
                result.setLeft(resultSession);
                return result;
            }

            /**
             * Test whether session exists. If it find the session the instance variable "SessionData _sessionData"
             * is initialized here.
             *
             * Originally this function initialized a "ResultSet r" instance variable and the "trueOp()" above
             * read that into a SessionData, but I kept running into "ResultSet Is Closed" errors when "trueOp()"
             * was called so I'm doing it here.
             *
             */
            boolean predicate() throws Exception {
                return new SessionOp<Boolean>() {
                    public Boolean op() throws Exception {
                        final List<String> constraints = new ArrayList<>();
                        constraints.add(DatabaseSession.PROJECT.searchSql(sessionData.getProject()));
                        constraints.add(DatabaseSession.TAG.searchSql(sessionData.getTag()));
                        constraints.add(DatabaseSession.NAME.searchSql(sessionData.getName()));

                        final ResultSet rs = pdb.executeQuery(null, DatabaseSession.findSessionSql(constraints.toArray(new String[constraints.size()])), null);
                        if (!rs.next()) {
                            if(log.isDebugEnabled()) {
                                log.debug("Found no existing session for " + sessionData.getSessionDataTriple().toString() + ". A new session data object will be created for data reception.");
                            }
                            return false;
                        }

                        final SessionData sessionData = DatabaseSession.fillSession(rs);

                        final PrearcStatus status = sessionData.getStatus();
                        if (PrearcStatus.RECEIVING.equals(status)|| PrearcStatus.RECEIVING_INTERRUPT.equals(status)) {
                            // Obviously if we're receiving we're fine.
                            if(log.isDebugEnabled()) {
                                log.debug("Receiving incoming data for session " + sessionData.getSessionDataTriple().toString() + ", which is currently in " + status + " state, which is totally fine.");
                            }
                            _sessionData = sessionData;
                            return true;
                        }
                        if (status == PrearcStatus.BUILDING) {
                            // If the session is currently building, then set this session to RECEIVING_INTERRUPT,
                            // which will allow it to continue receiving but prevent autoarchiving or session
                            // splitting afterwards.
                            if(log.isWarnEnabled()) {
                                log.warn("Receiving incoming data for session " + sessionData.getSessionDataTriple().toString() + " in BUILDING state, setting status to RECEIVING_INTERRUPT to block autoarchive and other operations and allow continuation of data reception.");
                            }
                            PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.updateSessionStatusSQL(sessionData.getName(), sessionData.getTimestamp(), sessionData.getProject(), PrearcStatus.RECEIVING_INTERRUPT), null, null);
                            _sessionData = sessionData;
                            return true;
                        }
                        if (status.isInterruptable()) {
                            // If the session is interruptable, which means it's not receiving but it's OK to set it to
                            // receiving (ready, in error, or in conflict), that's OK. Set to RECEIVING and return the
                            // session. Any other issues will be worked out (or re-occur) later.
                            if (log.isInfoEnabled()) {
                                log.info("Receiving incoming data for session " + sessionData.getSessionDataTriple().toString() + ", which is currently in the interruptable " + status + " state. Setting status to RECEIVING to allow continuation of data reception.");
                            }
                            PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.updateSessionStatusSQL(sessionData.getName(), sessionData.getTimestamp(), sessionData.getProject(), PrearcStatus.RECEIVING), null, null);
                            _sessionData = sessionData;
                            return true;
                        }
                        // If the status isn't interruptable, e.g. we're archiving or moving or deleting or whatever,
                        // then return false: we'll create a new session to receive the incoming data. This may require
                        // a merge later, but should prevent data loss.
                        if (log.isWarnEnabled()) {
                            log.warn("Receiving incoming data for session " + sessionData.getSessionDataTriple().toString() + ", which is currently in the non-interruptable " + status + " state. Creating a new RECEIVING session to allow continuation of data reception.");
                        }
                        return false;
                    }
                }.run();
            }
        }.run();
    }

    /**
     * Delete all the rows in the prearchive table.
     *
     * @throws SQLException When an error occurs running a query.
     */
    private static void clearPrearchive() throws Exception {
        try {
            new SessionOp<Void>() {
                public Void op() throws Exception {
                    log.info("Got request to clear the prearchive table");
                    PoolDBUtils.ExecuteNonSelectQuery("DELETE FROM " + PREARCHIVE_TABLE_WITH_SCHEMA, null, null);
                    return null;
                }
            }.run();
        } catch (SessionException e) {
            // should never happen
        }
    }

    /**
     * Debug method : Print the rows of the given ResultSet.
     *
     * @return A string containing the rows from the result set.
     *
     * @throws SQLException
     */
    private static String showRows(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            sb.append("[");
            for (DatabaseSession d : DatabaseSession.values()) {
                sb.append(d.getColumnName());
                sb.append(":");
                String tmp = d.resultToString(rs);
                sb.append(tmp == null ? "NULL" : tmp);
                sb.append("\n");
            }
            sb.append("]");
        }
        return sb.toString();
    }

    // prevent instantiation
    private PrearcDatabase() {
    }

    /**
     * Generate SQL statement to create the table.
     *
     * @return The string containing the SQL for creating the prearchive table.
     */
    private static String createPrearchiveTableSql() {
        final String query = "CREATE TABLE " + PREARCHIVE_TABLE_WITH_SCHEMA + "(" + StringUtils.join(Lists.transform(Arrays.asList(DatabaseSession.values()), new Function<DatabaseSession, String>() {
            @Override
            public String apply(final DatabaseSession session) {
                return session.getColumnName() + " " + session.getColumnDefinition();
        }
        }), ",") + ")";
        log.info("Creating the prearchive table with SQL: {}", query);
        return query;
    }

    /**
     * Build a list of sessions in the given projects.
     *
     * @param projects    The projects for which sessions should be retrieved.
     *
     * @return A list of lists of objects.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static List<List<Object>> buildRows(final String[] projects) throws Exception, SessionException {
        return new SessionOp<List<List<Object>>>() {
            public List<List<Object>> op() throws Exception {
                return projects.length == 0 ? Collections.<List<Object>>emptyList() : convertRStoList(pdb.executeQuery(null, DatabaseSession.PROJECT.allMatchesSql(projects), null));
            }
        }.run();
    }

    /**
     * Retrieve all sessions in the prearchive table that are part of the given project
     *
     * @param proj Project name.
     *
     * @return A list of sessions for the specified project.
     *
     * @throws Exception        When an unknown error occurs.
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static List<SessionData> getSessionsInProject(final String proj) throws Exception, SQLException, SessionException {
        return new SessionOp<List<SessionData>>() {
            public List<SessionData> op() throws Exception {
                final List<SessionData> ao  = new ArrayList<>();
                String[]                sdr = {proj};
                ResultSet rs = this.pdb.executeQuery(null, DatabaseSession.PROJECT.allMatchesSql(sdr), null);
                while (rs.next()) {
                    ao.add(DatabaseSession.fillSession(rs));
                }
                return ao;
            }
        }.run();
    }

    /**
     * Build a list of all sessions in the prearchive.
     *
     * @return
     *
     * @throws Exception
     * @throws SQLException
     * @throws SessionException
     */
    public static List<List<Object>> buildRows() throws Exception, SQLException, SessionException {
        return new SessionOp<List<List<Object>>>() {
            public List<List<Object>> op() throws SQLException, SessionException, Exception {
                return convertRStoList(pdb.executeQuery(null, DatabaseSession.allMatchesSql(), null));
            }
        }.run();
    }

    /**
     * Build a list of sessions in the given projects.
     *
     * @param sessions Sessions.
     *
     * @return A list of sessions for each specified project.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static List<List<Object>> buildRows(final Collection<SessionDataTriple> sessions) throws Exception, SQLException, SessionException {
        return new SessionOp<List<List<Object>>>() {
            public List<List<Object>> op() throws Exception {
                final List<List<Object>> rows = new ArrayList<>();
                for (final SessionDataTriple session : sessions) {
                    rows.addAll(convertRStoList(pdb.executeQuery(null, DatabaseSession.findSessionSql(session.getFolderName(), session.getTimestamp(), session.getProject()), null)));
                }
                return rows;
            }
        }.run();
    }

    public static List<List<Object>> findMyStudy(final String patientName, final String patientID, final Date studyDate) throws Exception {
        return new SessionOp<List<List<Object>>>() {
            public List<List<Object>> op() throws Exception {
                final PreparedStatement statement = this.pdb.getPreparedStatement(null, DatabaseSession.findMyStudySql());//patientID, patientName, studyDate
                statement.setString(1,patientID);
                statement.setString(2,patientName);
                if(studyDate!=null) {
                    statement.setDate(3, new java.sql.Date(studyDate.getTime()));
                } else {
                    statement.setDate(3, new java.sql.Date(0L));
                }
                ResultSet rs = statement.executeQuery();
                return convertRStoList(rs);
            }
        }.run();
    }

    private static List<List<Object>> convertRStoList(final ResultSet rs) throws SQLException {
        final List<List<Object>> ao = new ArrayList<>();
        while (rs.next()) {
            final List<Object> al = new ArrayList<>();
            for (DatabaseSession d : DatabaseSession.values()) {
                if (d.equals(DatabaseSession.URL)) {
                    final String project = DatabaseSession.PROJECT.getFromResult(rs);
                    final String timestamp = DatabaseSession.TIMESTAMP.getFromResult(rs);
                    final String session = DatabaseSession.FOLDER_NAME.getFromResult(rs);
                    al.add(String.format("/prearchive/projects/%s/%s/%s", project, timestamp, session));
                } else {
                    al.add(d.getFromResult(rs));
                }
            }
            ao.add(al);
        }
        return ao;
    }

    /**
     * Get the columns in the database table.
     *
     * @return A list of columns in the database table.
     */
    public static List<String> getCols() {
        return Lists.transform(Arrays.asList(DatabaseSession.values()), new Function<DatabaseSession, String>() {
            @Override
            public String apply(final DatabaseSession session) {
                return session.getColumnName();
            }
        });
    }

    /**
     * Debug method that outputs the columns in the prearchive table
     *
     * @return
     *
     * @throws SQLException
     */
    public static String printCols() throws SQLException {
        return StringUtils.join(XDAT.getJdbcTemplate().queryForList(QUERY_PREARC_TABLE_COLUMNS, String.class), ", ");
    }

    /**
     * Update the last modified time of the session to the current time.
     *
     * @param triple    The triple containing the session name, timestamp, and project.
     *
     * @throws SQLException
     * @throws SessionException
     * @throws Exception
     */
    public static void updateTimestamp(final SessionDataTriple triple) throws SQLException, SessionException, Exception {
        updateTimestamp(triple.getFolderName(), triple.getTimestamp(), triple.getProject());
    }

    /**
     * Update the last modified time of the session to the current time.
     *
     * @param sess      Session label
     * @param timestamp Timestamp directory
     * @param proj      Project name
     *
     * @throws SQLException
     * @throws SessionException
     * @throws Exception
     */
    public static void updateTimestamp(String sess, String timestamp, String proj) throws SQLException, SessionException, Exception {
        modifySession(sess, timestamp, proj, new SessionOp<java.lang.Void>() {
            public Void op() throws SQLException, Exception {
                return null;
            }
        });
    }

    /**
     * A generic class that stores a database operation on a session. It assumes that PrearcTable.conn is a valid connection, and the operations that change the database run a conn.commit() after they are done.
     *
     * @param <T> The type of data returned by the operation, use Void of the operation returns nothing
     *
     * @author aditya
     */

    static abstract class SessionOp<T> {
        // Connection conn;
        PoolDBUtils pdb;

        public void createConnection() {
            // this.conn = DriverManager.getConnection("jdbc:h2:" + prearcPath + dbName, "sa", "");
            this.pdb = new PoolDBUtils();
        }

        public void closeConnection() {
            this.pdb.closeConnection();
        }

        public abstract T op() throws Exception;

        public T run() throws Exception {
            this.createConnection();
            try {
                return this.op();
            } catch (SessionException e) {
                // Don't log session exceptions: they should be handled by whoever called this.
                throw e;
            } catch (Exception e) {
                log.error("", e);
                throw e;
            } finally {
                closeConnection();
            }
        }
    }

    /**
     * Check that session arguments are valid and there is unique session that matches the arguments. If 'proj' is null
     * it is assumed that the session is {@link ProjectAccessPredicate#UNASSIGNED unassigned}.
     *
     * @param sess      Session
     * @param timestamp Timestamp
     * @param proj      Project
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     * @throws Exception        When an unknown error occurs.
     */
    private static void checkSession(String sess, String timestamp, String proj) throws Exception, SQLException, SessionException {
        checkArgs(sess, timestamp, proj);
        checkUniqueRow(sess, timestamp, proj);
    }

    private static void checkArgs(String sess, String timestamp, String proj) throws SessionException {
        if (StringUtils.isBlank(sess)) {
            throw new SessionException(InvalidSession, "Session argument is null or empty");
        }
        if (StringUtils.isBlank(timestamp)) {
            throw new SessionException(InvalidSession, "Timestamp argument is null or empty");
        }
        if (StringUtils.isBlank(proj)) {
            log.info("The project is blank for timestamp {} and session {}. Could be unassigned.", timestamp, sess);
        }
    }

    private static void checkArgs(SessionData s) throws SessionException {
        checkArgs(s.getFolderName(), s.getTimestamp(), s.getProject());
    }

    private static void checkUniqueRow(String sess, String timestamp, String proj) throws Exception {
        int rowCount = countOf(sess, timestamp, proj);
        if (rowCount == 0) {
            throw new SessionException(DoesntExist, "A record with session " + sess + ", timestamp " + timestamp + " and project " + proj + " could not be found.");
        }
        if (rowCount > 1) {
            throw new SessionException(DatabaseError, "Multiple records with session " + sess + ", timestamp " + timestamp + " and project " + proj + " were found.");
        }
    }

    /**
     * Check that a session exists in the prearchive table.
     *
     * @param sess      Session label.
     * @param timestamp The session timestamp
     * @param proj      Project name.
     *
     * @return If the session exists.
     *
     * @throws Exception        When an unknown error occurs.
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */
    public static boolean exists(final String sess, final String timestamp, final String proj) throws Exception, SQLException, SessionException {
        return countOf(sess, timestamp, proj) == 1;
    }

    public static Map<String, Object> removePrearcVariables(final Map<String, Object> variables) {
        log.debug("I'm trimming prearchive variables from a map with the keys: {}", variables.keySet());
        for (final String param : PREARC_VARIABLES) {
            log.debug("Removing variable {}", param);
            variables.remove(param);
        }
        return variables;
    }

    /**
     * Check session parameters and run the operation
     *
     * @param <T>       The type of the session parameter.
     * @param sess      Session label.
     * @param timestamp The session timestamp
     * @param proj      Project name.
     * @param op        The operation.
     *
     * @return The value resulting from the operation.
     *
     * @throws SQLException     When an error occurs running a query.
     * @throws SessionException When an error occurs with the session.
     */

    private static <T> T withSession(String sess, String timestamp, String proj, SessionOp<T> op) throws Exception, SQLException, SessionException {
        checkSession(sess, timestamp, proj);
        return op.run();
    }

    private static <T> void modifySession(final String sess, final String timestamp, final String proj, SessionOp<T> op) throws Exception {
        withSession(sess, timestamp, proj, new SessionOp<Void>() {
            public Void op() throws Exception {
                PoolDBUtils.ExecuteNonSelectQuery(DatabaseSession.LASTMOD.updateSessionSql(sess, timestamp, proj, Calendar.getInstance().getTime()), null, null);
                return null;
            }
        });
        op.run();
    }

    @SuppressWarnings("serial")
    public static class SyncFailedException extends IOException {
        public Throwable cause = null;

        public SyncFailedException(String message, Throwable cause) {
            super(message, cause);
            this.cause = cause;
        }

        public SyncFailedException(String message) {
            super(message);
        }

        public SyncFailedException(Throwable cause) {
            super(cause);
            this.cause = cause;
        }

    }

    private static final List<String> PREARC_VARIABLES = ImmutableList.of(RequestUtil.AA, RequestUtil.AUTO_ARCHIVE, PrearcUtils.PREARC_SESSION_FOLDER, PrearcUtils.PREARC_TIMESTAMP);
    private static final String        QUERY_PREARC_TABLE_EXISTS             = "SELECT * FROM information_schema.tables WHERE table_schema = '" + SEARCH_SCHEMA_NAME + "' and table_name = '" + PREARCHIVE_TABLE + "'";
    private static final String        PREARCHIVE_TABLE_SQL                  = createPrearchiveTableSql();
    private static final String        QUERY_PREARC_TABLE_COLUMNS = "SELECT column_name FROM information_schema.columns WHERE table_schema = '" + SEARCH_SCHEMA_NAME + "' AND table_name = '" + PREARCHIVE_TABLE + "'";
    private static final AtomicBoolean PREARC_READY               = new AtomicBoolean();
    private static final String        COLUMN_NAME                = "column_name";
    private static final String        QUERY_DEPRECATE_PREARC_TABLE          = "ALTER TABLE " + PREARCHIVE_TABLE_WITH_SCHEMA + " RENAME TO " + PREARCHIVE_TABLE + "_deprecated";
    private static final String        QUERY_MIGRATE_DEPRECATED_PREARC_TABLE = "INSERT INTO " + PREARCHIVE_TABLE_WITH_SCHEMA + " (${columns}) SELECT ${columns} FROM " + PREARCHIVE_TABLE_WITH_SCHEMA + "_deprecated";
    private static final String        QUERY_DROP_DEPRECATED_PREARC_TABLE    = "DROP TABLE " + PREARCHIVE_TABLE_WITH_SCHEMA + "_deprecated";
    private static final String        QUERY_CLEAR_PREARCHIVE                = "DELETE FROM " + PREARCHIVE_TABLE_WITH_SCHEMA;
    private static final String        SPLIT_PETMR_SESSION_ID                = "SplitPetMrSessions";
    private static final String        DEFAULT_SPLIT_PETMR_SESSION_FILTER    = "{\n" +
                                                                               "    \"mode\": \"modalityMap\",\n" +
                                                                               "    \"exclude\": \"/^yes$/i.test('#BurnedInAnnotation#')\",\n" +
                                                                               "    \"PT\": \"'#Modality#' == 'PT' || ('#Modality#' == 'MR' && /^.*MRAC.*$/.test('#SeriesDescription#'))\",\n" +
                                                                               "    \"MR\": \"'#Modality#' != 'PT' && !('#Modality#' == 'MR' && /^.*MRAC.*$/.test('#SeriesDescription#'))\",\n" +
                                                                               "    \"default\": \"MR\"\n" +
                                                                               "}\n";
    private static final Script        DEFAULT_SPLIT_PETMR_SESSION_SCRIPT    = new Script(SPLIT_PETMR_SESSION_ID,
                                                                                          "Split PET/MR script",
                                                                                          "Default implementation of the split PET/MR session script.",
                                                                                          "groovy", "", DEFAULT_SPLIT_PETMR_SESSION_FILTER);

    // an object that synchronizes the cache with some permanent store
    private static SessionDataDelegate sessionDelegate;
    private static String              prearcPath;
}
