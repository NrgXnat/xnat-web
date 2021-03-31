package org.nrg.xnat.services.par.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xft.XFTTable;
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
	public void getParList(UserI user) throws InitializationException {
		final Hashtable<String, Object> params = new Hashtable<>();
		try {
			final XFTTable table = XFTTable.Execute(String.format(PAR_QUERY, user.getEmail().toLowerCase()),
					user.getDBName(), user.getLogin());

			if (table != null) {
				params.put("totalRecords", table.size());
			}
		} catch (Exception e) {
			log.error("An error occurred attempting to access the project invitations for user " + user.getLogin(), e);
			throw new InitializationException("An error occurred attempting to access the project invitations.");
		}
	}

	@Override
	public ProjectAccessRequest getParResourceByParId(UserI user, Integer parId) throws DataFormatException {
		if(Objects.nonNull(parId))
			return ProjectAccessRequest.RequestPARById(parId, user);
		else throw new DataFormatException("parId is Missing");
	}

	@Override
	public List<ProjectAccessRequest> getProjectParsByProjectId(UserI user, String projectId) throws DataFormatException {
		if(Objects.nonNull(projectId))
			return _template.query(PROJECT_PAR_QUERY + ID_WHERE_PAR_PROJECT, new MapSqlParameterSource("projectId", projectId),new ProjectAccessRequestRowMapper(user));
		else throw new DataFormatException("ProjectId is Missing");
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
