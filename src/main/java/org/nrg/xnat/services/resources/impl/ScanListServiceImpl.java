/**
 * 
 */
package org.nrg.xnat.services.resources.impl;

import java.util.ArrayList;

import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xnat.services.resources.ScanListService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * @author afour
 *
 */
public class ScanListServiceImpl implements ScanListService {

	@Override
	public boolean allowPost() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void handlePost() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<String> getDefaultFields(GenericWrapperElement e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultElementName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Representation getRepresentation(Variant variant) {
		// TODO Auto-generated method stub
		return null;
	}

}
