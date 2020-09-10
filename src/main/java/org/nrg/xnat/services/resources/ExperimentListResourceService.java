package org.nrg.xnat.services.resources;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.nrg.xft.XFTTable;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xnat.restlet.resources.ExperimentListResource;
import org.nrg.xnat.restlet.resources.ExperimentListResource.FilteredExptListHandlerI;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface ExperimentListResourceService
{
	public ArrayList<String> getDefaultFields(GenericWrapperElement e);

	public String getDefaultElementName();

	public Representation represent(Variant variant);

	public XFTTable build(ExperimentListResource resource, Hashtable<String, Object> params) throws Exception;
	
	public  List<FilteredExptListHandlerI> getHandlers() throws InstantiationException, IllegalAccessException;

}
