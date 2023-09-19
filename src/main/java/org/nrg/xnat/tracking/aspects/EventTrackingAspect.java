package org.nrg.xnat.tracking.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.nrg.xnat.tracking.model.TrackableEvent;
import org.nrg.xnat.tracking.services.EventTrackingService;
import org.nrg.xnat.tracking.TrackEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;
import reactor.bus.Event;

@Slf4j
@Aspect
@Component
public class EventTrackingAspect {
    @Autowired
    public EventTrackingAspect(final EventTrackingService eventTrackingService) {
        this.eventTrackingService = eventTrackingService;
    }

    @Pointcut("@annotation(trackEvent)")
    public void trackingPointcut(final TrackEvent trackEvent) {
    }

    @Before(value = "trackingPointcut(trackEvent) && args(event)", argNames = "trackEvent, event")
    public <T extends TrackableEvent> void updateEventTracker(final TrackEvent trackEvent, final Event<T> event) {
        try {
            eventTrackingService.create(event.getData());
        } catch (Exception e) {
            log.error("Unable to track event", e);
        }
    }

    private final EventTrackingService eventTrackingService;
}
