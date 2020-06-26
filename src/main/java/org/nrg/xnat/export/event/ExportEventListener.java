package org.nrg.xnat.export.event;

import static reactor.bus.selector.Selectors.type;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.services.NrgEventService;
import org.nrg.xnat.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

/**
 * @author Mohana Ramaratnam
 *
 */

@Slf4j
@Component
public class ExportEventListener implements Consumer<Event<ExportEvent>> {

		private final NrgEventService eventService;

	    @Autowired
	    public ExportEventListener(final NrgEventService eventService, final EventBus eventBus) {
	        this.eventService = eventService;
	        eventBus.on(type(ExportEvent.class), this);
	    }

	    @Override
	    public void accept(Event<ExportEvent> busEvent) {
	    	final ExportEvent exportEvent = busEvent.getData();
	        final String projectExportTrackingId = exportEvent.getExportTrackingId();
	        final Integer userId = exportEvent.getUserId();
	        if (StringUtils.isBlank(projectExportTrackingId)) {
	            return;
	        }
	        eventService.triggerEvent(
	        		new ProjectEvent(exportEvent.getExportTrackingId(),
	        				userId,
	        				exportEvent.getDataDescendantManifest(),
	                        exportEvent.getStatus(),
	                        exportEvent.getMessage(),
	                        exportEvent.getNumberOfFiles(),
	                        exportEvent.getFileSize(), exportEvent.isExported()));
	    }

}
