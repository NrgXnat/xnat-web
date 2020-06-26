package org.nrg.xnat.export.event;

import static reactor.bus.selector.Selectors.type;

import org.nrg.framework.services.NrgEventService;
import org.nrg.xnat.event.EventListener;
import org.nrg.xnat.tracking.TrackEvent;
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
@EventListener
public class ProjectEventListener implements Consumer<Event<ProjectEvent>> {


	/**
     * {@inheritDoc}
     */
    @Override
    @TrackEvent
    public void accept(Event<ProjectEvent> busEvent) {
        log.trace("Received event {} for project event {}", busEvent.getId(), busEvent.getData());
    }
}

