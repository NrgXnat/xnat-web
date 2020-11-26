package org.nrg.xnat.services.projects.impl;

import java.util.List;
import java.util.Objects;

import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.archive.impl.legacy.AbstractXftServiceImpl;
import org.nrg.xnat.services.projects.ProjectService;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl extends AbstractXftServiceImpl implements ProjectService{

	protected ProjectServiceImpl(NamedParameterJdbcTemplate template) {
		super(template);
	}

	@Override
	public List<XnatProjectdata> getAll(UserI user) {
		return XnatProjectdata.getAllXnatProjectdatas(user, false);
	}

	@Override
	public XnatProjectdata findById(UserI user, String projectId) {
		if(Objects.nonNull(projectId))
			return XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		else throw new NullPointerException("ProjectId is Null");
	}

	@Override
	public XnatProjectdata create(UserI user, XnatProjectdata xnatProjectdata) {
		return null;
	}

	@Override
	public XnatProjectdata update(UserI user, XnatProjectdata xnatProjectdata, String projectId) {
		return null;
	}

	@Override
	public void deleteById(UserI user, String projectId) {
		
	}

}
