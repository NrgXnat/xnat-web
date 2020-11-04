package org.nrg.xnat.services.resources.impl;

import java.util.List;

import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.ResourceService;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements ResourceService{

	@Override
	public List<XnatAbstractresource> findByExperimentId(UserI user, String experimentId) {
		String xmlPath ="xnat:experimentData/resources/resource";
		return XnatAbstractresource.getXnatAbstractresourcesByField(xmlPath, experimentId, user, false);
	}
}
