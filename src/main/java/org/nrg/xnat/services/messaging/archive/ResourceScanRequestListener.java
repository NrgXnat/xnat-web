package org.nrg.xnat.services.messaging.archive;

import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.messaging.JmsRequestListener;
import org.nrg.xapi.exceptions.ConflictedStateException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.nrg.xnat.services.archive.ResourceScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResourceScanRequestListener implements JmsRequestListener<ResourceScanRequest> {
    private final ResourceScanService _service;

    @Autowired
    public ResourceScanRequestListener(final ResourceScanService service) {
        _service = service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JmsListener(id = "resourceScanRequest", destination = "resourceScanRequest")
    public void onRequest(final ResourceScanRequest request) {
        log.info("Now handling request: {}", request);
        try {
            switch (request.getRsnStatus()) {
                case Created:
                case QueuedForScanning:
                    _service.scanResource(Users.getUser(request.getRequester()), request.getResourceId());
                    break;
                case QueuedForRepair:
                    _service.repairResource(Users.getUser(request.getRequester()), request.getResourceId());
                    break;
                default:
                    log.warn("User {} requested action on resource scan request {} for resource {} with status {} but I don't know what to do with a request in that status.", request.getRequester(), request.getId(), request.getResourceId(), request.getRsnStatus());
            }
        } catch (UserInitException e) {
            throw new RuntimeException(e);
        } catch (UserNotFoundException e) {
            log.error("Tried to handle resource scan request {} for resource {} with status {} but couldn't find the referenced user \"{}\"", request.getId(), request.getResourceId(), request.getRsnStatus(), request.getRequester());
        } catch (InsufficientPrivilegesException e) {
            log.error("Tried to handle resource scan request {} for resource {} with status {} but the referenced user \"{}\" doesn't have sufficient privileges on the project {}", request.getId(), request.getResourceId(), request.getRsnStatus(), request.getRequester(), request.getProjectId());
        } catch (NotFoundException e) {
            log.error("Tried to handle resource scan request {} for resource {} with status {} but couldn't find something: {}", request.getId(), request.getResourceId(), request.getRsnStatus(), e.getMessage());
        } catch (ConflictedStateException e) {
            log.error("Tried to handle resource scan request {} for resource {} with status {} but found a conflicted state: {}", request.getId(), request.getResourceId(), request.getRsnStatus(), e.getMessage());
        } catch (InitializationException e) {
            log.error("Tried to handle resource scan request {} for resource {} with status {} but a serious error occurred", request.getId(), request.getResourceId(), request.getRsnStatus(), e);
        }
    }
}
