package org.nrg.xnat.services.resources;

import java.util.ArrayList;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface ScanListService {

	boolean allowPost();

	void handlePost();

	ArrayList<String> getDefaultFields(GenericWrapperElement e);

	String getDefaultElementName();

	Representation getRepresentation(Variant variant);

}