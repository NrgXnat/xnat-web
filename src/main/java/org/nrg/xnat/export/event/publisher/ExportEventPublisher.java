package org.nrg.xnat.export.event.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author Mohana Ramaratnam
 *
 */
public class ExportEventPublisher {

	private final ApplicationEventPublisher publisher;
	
	@Autowired
	public ExportEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
	        this.publisher= applicationEventPublisher;
	}
	
	public void publishEvent(Object event) {
		publisher.publishEvent(event);
	}

}
