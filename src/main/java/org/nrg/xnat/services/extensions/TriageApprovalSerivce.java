package org.nrg.xnat.services.extensions;

import org.nrg.action.ActionException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xft.security.UserI;

public interface TriageApprovalSerivce {

	void create(UserI user, String src, String dest, String overwrite, String eventId) throws InitializationException, ActionException;
}
