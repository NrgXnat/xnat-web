/*
 * web: org.nrg.xnat.helpers.prearchive.handlers.PrearchiveRebuildHandler
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.prearchive.handlers;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.bean.XnatImagesessiondataBean;
import org.nrg.xdat.bean.XnatPetmrsessiondataBean;
import org.nrg.xdat.bean.reader.XDATXMLReader;
import org.nrg.xdat.security.user.XnatUserProvider;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.archive.FinishImageUpload;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase;
import org.nrg.xnat.helpers.prearchive.PrearcUtils;
import org.nrg.xnat.helpers.prearchive.SessionData;
import org.nrg.xnat.helpers.prearchive.SessionDataTriple;
import org.nrg.xnat.restlet.actions.PrearcImporterA;
import org.nrg.xnat.services.messaging.prearchive.PrearchiveOperationRequest;

import java.io.File;

import static org.nrg.xnat.helpers.prearchive.handlers.PrearchiveOperation.Rebuild;
import static org.nrg.xnat.helpers.prearchive.handlers.PrearchiveOperation.Separate;

@Handles(Rebuild)
@Slf4j
public class PrearchiveRebuildHandler extends AbstractPrearchiveOperationHandler {
    public PrearchiveRebuildHandler(final PrearchiveOperationRequest request) throws Exception {
        super(request);
    }

    @Override
    protected boolean shouldProceed() throws Exception {
        return PrearcDatabase.setStatus(getSessionData().getFolderName(), getSessionData().getTimestamp(), getSessionData().getProject(), PrearcUtils.PrearcStatus.BUILDING);
    }

    @Override
    protected void handle() throws Exception {
        final UserI  user        = getUser();
        final String folderName  = getSessionData().getFolderName();
        final String timestamp   = getSessionData().getTimestamp();
        final String project     = getSessionData().getProject();
        final String externalUrl = getSessionData().getExternalUrl();
        final String visit       = getSessionData().getVisit();
        final String protocol    = getSessionData().getProtocol();
        final String timeZone    = getSessionData().getTimeZone();
        final String source      = getSessionData().getSource();

        log.info("Received request to process prearchive session at: {}", externalUrl);
        PrearcDatabase.buildSession(getSessionDir(), folderName, timestamp, project, visit, protocol, timeZone, source);

        // We need to check whether the session was updated to RECEIVING_INTERRUPT while the rebuild operation
        // was happening. If that happened, that means more data started to arrive during the rebuild. If not,
        // we'll proceed down the path where we check for session splits and autoarchive. If so, we'll just
        // reset the status to RECEIVING and update the session timestamp.
        final SessionDataTriple sessionDataTriple = getSessionData().getSessionDataTriple();
        final SessionData       current           = PrearcDatabase.getSession(sessionDataTriple);
        if (current.getStatus() != PrearcUtils.PrearcStatus.RECEIVING_INTERRUPT) {
            final boolean separatePetMr = PrearcUtils.isUnassigned(getSessionData()) ? PrearcUtils.shouldSeparatePetMr() : PrearcUtils.shouldSeparatePetMr(project);
            if (separatePetMr) {
                log.debug("Found create separate PET and MR sessions setting for project {}, now working to separate that.", project);
                final File   sessionXml   = new File(getSessionDir() + ".xml");
                final String absolutePath = sessionXml.getAbsolutePath();
                if (sessionXml.exists()) {
                    log.debug("Found the session XML in the file {}, processing.", absolutePath);
                    final XnatImagesessiondataBean bean = (XnatImagesessiondataBean) new XDATXMLReader().parse(sessionXml);
                    if (bean instanceof XnatPetmrsessiondataBean) {
                        log.debug("Found a PET/MR session XML in the file {} with the separate PET/MR flag set to true for the site or project, creating a new request to separate the session.", absolutePath);
                        PrearcUtils.resetStatus(user, project, timestamp, folderName, true);
                        final PrearchiveOperationRequest request = new PrearchiveOperationRequest(user, getSessionData(), getSessionDir(), Separate);
                        XDAT.sendJmsRequest(request);
                        return;
                    } else {
                        log.debug("Found a session XML for a {} session in the file {}. Not PET/MR so not separating.", bean.getFullSchemaElementName(), absolutePath);
                    }
                } else {
                    log.warn("Tried to rebuild a session from the path {}, but that session XML doesn't exist.", absolutePath);
                }
            }

            PrearcUtils.resetStatus(user, project, timestamp, folderName, true);

            // we don't want to autoarchive a session that's just being rebuilt
            // but we still want to autoarchive sessions that just came from RECEIVING STATE
            final PrearcImporterA.PrearcSession session  = new PrearcImporterA.PrearcSession(project, timestamp, folderName, null, user);
            final FinishImageUpload             uploader = new FinishImageUpload(null, user, session, null, false, true, false);
            if (isReceiving() || !uploader.isAutoArchive()) {
                log.debug("Processing queue entry for {} in project {} to archive {}", user.getUsername(), project, externalUrl);
                uploader.call();
            }
        } else {
            log.info("Found session {} in RECEIVING_INTERRUPT state, meaning that data began arriving while session was in an interruptible non-receiving state. No session split or autoarchive checks will be performed and session will be restored to RECEIVING state.", sessionDataTriple);
            PrearcDatabase.setStatus(folderName, timestamp, project, PrearcUtils.PrearcStatus.RECEIVING);
        }
    }

    @Override
    protected UserI getUser() {
        XnatUserProvider provider = XDAT.getContextService().getBean("receivedFileUserProvider", XnatUserProvider.class);
        if (provider != null) {
            UserI provUser = provider.get();
            if (provUser != null) {
                return provUser;
            }
        }
        return super.getUser();
    }
}
