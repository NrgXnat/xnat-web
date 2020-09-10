package org.nrg.xnat.services.resources;

import java.util.ArrayList;

import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface SubjectListResourceService {

	public Representation represent(Variant variant);

	public ArrayList<String> getDefaultFields(GenericWrapperElement e);

}
