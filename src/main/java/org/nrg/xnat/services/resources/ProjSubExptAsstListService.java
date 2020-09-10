package org.nrg.xnat.services.resources;

import java.util.ArrayList;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;

public interface ProjSubExptAsstListService {

	boolean allowPost();

	void handlePost();

	ArrayList<String> getDefaultFields(GenericWrapperElement e);

	String getDefaultElementName();

	Representation represent(Variant variant);

}