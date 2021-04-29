package org.nrg.xnat.model.util;

import java.util.Objects;

import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.EventUtils.TYPE;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XnatEventUtil {
	private final String eventType;
	private final String eventReason;
	private final String eventId;
	private final String eventAction;
	private final String eventComment;
	
	public static EventDetails newEventInstance(EventUtils.CATEGORY cat, String action, XnatEventUtil event ) {
    	return EventUtils.newEventInstance(cat, getEventTypedData(event.getEventType()), (event.getEventAction()!= null) ? event.getEventAction() : action ,event.getEventReason(),event.getEventComment());
	}
	
	public static EventDetails newEventInstance(EventUtils.CATEGORY cat, XnatEventUtil event ) {
    	return EventUtils.newEventInstance(cat, getEventTypedData(event.getEventType()), event.getEventAction() ,event.getEventReason(),event.getEventComment());
	}

	public static TYPE getEventTypedData(String eventId) {
		if (eventId != null) {
			return EventUtils.getType(eventId, EventUtils.TYPE.WEB_SERVICE);
		} else {
			return EventUtils.TYPE.WEB_SERVICE;
		}
	}
	
	public XnatEventUtil(String eventType, String eventReason, String eventId, String eventAction, String eventComment){
		this.eventAction = eventAction;
		this.eventComment = eventComment;
		this.eventId = eventId;
		this.eventType = eventType;
		this.eventReason = eventReason;
	}
	
	public static XnatEventUtil  getXnatEventUtil(String eventType, String eventReason, String eventId, String eventAction, String eventComment) {
		return XnatEventUtil.builder()
		.eventId(Objects.nonNull(eventId)?eventId:null)
		.eventAction(Objects.nonNull(eventAction)?eventAction:null)
		.eventReason(Objects.nonNull(eventReason)?eventReason:null)
		.eventType(Objects.nonNull(eventType)?eventType:null)
		.eventComment(Objects.nonNull(eventComment)?eventComment:null)
		.build();
	}
	
	public static Integer getEventId(String eventId) {
		if (eventId != null) 
			return Integer.valueOf(eventId);
         else 
        	 return null;
	}

}
