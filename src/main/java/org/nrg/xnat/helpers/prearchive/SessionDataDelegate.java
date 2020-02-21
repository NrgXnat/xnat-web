/*
 * web: org.nrg.xnat.helpers.prearchive.SessionDataDelegate
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.prearchive;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase.SyncFailedException;
import org.nrg.xnat.helpers.prearchive.PrearcUtils.PrearcStatus;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j
public abstract class SessionDataDelegate implements SessionDataProducerI, SessionDataModifierI {
	private final SessionDataProducerI _producer;
	private final SessionDataModifierI _modifier;

	public SessionDataDelegate(final SessionDataProducerI producer, final SessionDataModifierI modifier) {
		_producer = producer;
		_modifier = modifier;
	}
	
	public Collection<SessionData> get() throws IOException {
		return _producer.get();
	}

	public void move(final SessionData sessionData, final String project) throws SyncFailedException {
		log.debug("Moving session {} to project {}", sessionData, project);
		_modifier.move(sessionData, project);
	}

	public void moveScans(final SessionData sessionData, final String label, final String folder, final List<String> scans) throws SyncFailedException {
		log.debug("Moving session {} to new folder {} with label {} and {} scans: {}", sessionData, folder, label, scans.size(), scans);
		_modifier.moveScans(sessionData, label, folder, scans);
	}

	public void delete(final SessionData sessionData) throws SyncFailedException {
		log.debug("Deleting session {}", sessionData);
		_modifier.delete(sessionData);
	}

	public void setStatus(final SessionData sessionData, final PrearcStatus status) {
		log.debug("Setting status for session {} to {}", sessionData, status);
		_modifier.setStatus(sessionData, status);
	}
}
