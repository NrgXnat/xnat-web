package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.action.ActionException;
import org.nrg.xft.ItemI;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface AuditRestletService {

	ItemI retrieveItemByIds(String xsiType, List<String> ids) throws ActionException;

	Representation represent(Variant variant) throws ResourceException;

}