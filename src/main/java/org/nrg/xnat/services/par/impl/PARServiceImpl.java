package org.nrg.xnat.services.par.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.par.PARService;
import org.nrg.xnat.turbine.utils.ProjectAccessRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PARServiceImpl implements PARService {
	
	@Autowired
	public PARServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public List<ProjectAccessRequest> findAllProjectAccessRequests(UserI user) throws InitializationException {
		return _template.query(PAR_QUERY, new MapSqlParameterSource(),new ProjectAccessRequestRowMapper(user));
	}

	@Override
	public ProjectAccessRequest findParResourceByParId(UserI user, Integer parId) throws DataFormatException {
		if(Objects.nonNull(parId))
			return ProjectAccessRequest.RequestPARById(parId, user);
		else throw new DataFormatException("parId is Missing");
	}

	@Override
	public List<ProjectAccessRequest> findProjectParsByProjectId(UserI user, String projectId) throws DataFormatException {
		if(Objects.nonNull(projectId))
			return _template.query(PROJECT_PAR_QUERY + ID_WHERE_PAR_PROJECT, new MapSqlParameterSource("projectId", projectId),new ProjectAccessRequestRowMapper(user));
		else throw new DataFormatException("ProjectId is Missing");
	}
	
	@Override
	public ProjectAccessRequest update(UserI user, ProjectAccessRequest projectAccessRequest, Integer parId, String accept, String decline) throws Exception {
		ProjectAccessRequest par = getParObject(user,projectAccessRequest,parId);
	       if (par != null) {
	            if (par.getApproved() != null || par.getApprovalDate() != null) {
	            	throw new NotFoundException("This project invitation has already been accepted.");
	            } else {
	                try {
	                    if (accept != null) {
	                        par.process(user, true, getEventType(), getReason(), getComment());
	                    } else if (decline != null) {
	                        par.process(user, false, getEventType(), getReason(), getComment());
	                    }
	                } catch (Exception e) {
	                    log.error("Error trying to process PAR " + par.getRequestId(), e);
	                }
	            }
	       }
		return par;
	}
	
	private String getComment() {
		return null;
	}

	private String getReason() {
		return null;
	}

	public EventUtils.TYPE getEventType() {
        final String id = getQueryVariable(EventUtils.EVENT_TYPE);
        if (id != null) {
            return EventUtils.getType(id, EventUtils.TYPE.WEB_SERVICE);
        } else {
            return EventUtils.TYPE.WEB_SERVICE;
        }
    }

	private String getQueryVariable(String string) {
		return null;
	}

	private ProjectAccessRequest getParObject(UserI user, ProjectAccessRequest projectAccessRequest, Integer parId ) throws Exception {
		ProjectAccessRequest par = ProjectAccessRequest.RequestPARByGUID(projectAccessRequest.getGuid(), user);
        if (par == null) {
            par = ProjectAccessRequest.RequestPARById(parId, user);
        }
        if (par != null) {
            final String projectId = par.getProjectId();
            if (StringUtils.isBlank(projectId)) {
                if (!Roles.isSiteAdmin(user)) 
                	throw new InsufficientPrivilegesException("Only site admins can view this type of PAR.");
                if (log.isWarnEnabled())
                	log.warn("Attempt by user " + user.getLogin() + " to access PAR " + par.getRequestId());
            } 
	}else {
        XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectAccessRequest.getProjectId(), null, false);
        if (project == null) {
        	 log.error("Found the PAR " + par.getRequestId() + " which is missing associated project " + par.getProjectId());
        	throw new NotFoundException("The project associated with the project access request appears to be gone.");
        } else {
            if (!Roles.isSiteAdmin(user) && !project.canEdit(user) && !isParUser(user, par)) 
            	throw new InsufficientPrivilegesException("You don't have the appropriate permissions to view this PAR (must be admin or have edit permissions on the associated project).");
            if (log.isWarnEnabled()) {
                log.warn("Attempt by user " + user.getLogin() + " to access PAR " + par.getRequestId() + " associated with project " + par.getProjectId());
				}
			}
		}
		return par;
	}

	private boolean isParUser(UserI user, ProjectAccessRequest par) {
        return Objects.equals(par.getUserId(), user.getID()) || (par.getUserId() == null && StringUtils.equalsIgnoreCase(par.getEmail(), user.getEmail()));
    }
	
	private static class ProjectAccessRequestRowMapper implements RowMapper<ProjectAccessRequest> {
		ProjectAccessRequestRowMapper(final UserI user) {
	        _user = user;
	    }
	    @Override
	    public ProjectAccessRequest mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	        final Integer parId = resultSet.getInt("par_id");
	        ProjectAccessRequest projectAccessRequest= ProjectAccessRequest.RequestPARById(parId, _user);
	        return projectAccessRequest;
	    }
	    private final UserI _user;
	}
	
	private final NamedParameterJdbcTemplate _template;
	private static final String PAR_QUERY = "SELECT par.par_id,par.proj_id,par.level,par.create_date,u.login, u.firstname, u.lastname,p.secondary_id,p.name,p.id,SUBSTRING(p.description,0,300) as description,pi.firstname || ' ' || pi.lastname FROM xs_par_table par LEFT JOIN xnat_projectData p ON par.proj_id=p.id LEFT JOIN xnat_investigatordata pi ON p.pi_xnat_investigatordata_id=pi.xnat_investigatordata_id LEFT JOIN xdat_user u ON par.approver_id=u.xdat_user_id WHERE LOWER(par.email)='%s' AND approval_date IS NULL";
	private static final String PROJECT_PAR_QUERY = "SELECT par.par_id,par.proj_id,par.level,par.create_date,par.email,u.login,p.secondary_id,par.approved, par.approval_date FROM xs_par_table par LEFT JOIN xnat_projectData p ON par.proj_id=p.id LEFT JOIN xdat_user u ON par.approver_id=u.xdat_user_id ";
	private static final String ID_WHERE_PAR_PROJECT= "  WHERE par.proj_id = :projectId ";
	
}
