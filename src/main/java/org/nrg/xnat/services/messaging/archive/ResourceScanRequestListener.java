package org.nrg.xnat.services.messaging.archive;

import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.messaging.JmsRequestListener;
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
            log.debug("Received request to direct archive {}", request.getId());
        } catch (Exception e) {
            log.error("An error occurred during direct archive of {}", request.getId(), e);
        }
    }
}
