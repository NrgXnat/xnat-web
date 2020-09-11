/**
 * 
 */
package org.nrg.xnat.services.resources.search.impl;

import org.nrg.xnat.services.resources.impl.BaseXapiServiceImpl;
import org.nrg.xnat.services.resources.search.SearchResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * @author afour
 *
 */
public class SearchResourceServiceImpl extends BaseXapiServiceImpl implements SearchResourceService {

	@Override
	public boolean allowGet() {
		return false;
	}

	@Override
	public boolean handlePost() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Representation represent(Variant variant) {
		// TODO Auto-generated method stub
		return null;
	}

}
