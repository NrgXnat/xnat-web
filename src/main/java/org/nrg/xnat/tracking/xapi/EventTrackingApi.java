package org.nrg.xnat.tracking.xapi;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.tracking.exceptions.SummarizerException;
import org.nrg.xnat.tracking.model.EventLogSummary;
import org.nrg.xnat.tracking.model.EventLog;
import org.nrg.xnat.tracking.services.EventTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Api("The XNAT Event Tracking Data API")
@XapiRestController
@RequestMapping(value = "event_tracking")
@Slf4j
public class EventTrackingApi extends AbstractXapiRestController {
    private final EventTrackingService eventTrackingService;

    @Autowired
    public EventTrackingApi(final EventTrackingService eventTrackingService,
                            final UserManagementServiceI userManagementService,
                            final RoleHolder roleHolder) {
        super(userManagementService, roleHolder);
        this.eventTrackingService = eventTrackingService;
    }

    @XapiRequestMapping(value = {"{key}/payload"}, produces = {MediaType.TEXT_PLAIN_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<List<? extends EventLog>> getPayload(@PathVariable final String key) {
        try {
            return new ResponseEntity<>(eventTrackingService.getPayloadByKey(key, getSessionUser()), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Throwable t) {
            log.error("Issue getting event tracking payload for key {}", key, t);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @XapiRequestMapping(value = {"{key}"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<EventLogSummary> getData(@PathVariable final String key) {
        try {
            return new ResponseEntity<>(eventTrackingService.getSummaryForKey(key, getSessionUser()), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Throwable t) {
            log.error("Issue getting event tracking data for key {}", key, t);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
