/**
 * 
 */
package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.UserSessionService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

/**
 * @author afour
 *
 */
public class UserSessionServiceImpl implements UserSessionService {

	@Override
	public boolean allowDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowPost() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeRepresentations() {
		// TODO Auto-generated method stub

	}

	@Override
	public void acceptRepresentation(Representation entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
