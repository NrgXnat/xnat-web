package org.nrg.xnat.services.subjects.impl;

import lombok.extern.slf4j.Slf4j;

import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatDemographicdata;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEvent;
import org.nrg.xft.event.EventUtils.TYPE;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.model.util.XnatSubjectUtil;
import org.nrg.xnat.services.projects.ProjectService;
import org.nrg.xnat.services.subjects.SubjectService;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    public SubjectServiceImpl(final NamedParameterJdbcTemplate template, final ProjectService projectService) {
        _template = template;
        _projectService = projectService;
    }

    @Override
    public List<XnatSubjectdata> getAll(final UserI user) {
        return XnatSubjectdata.getAllXnatSubjectdatas(user, false);
    }

    @Override
    public XnatSubjectdata findById(final UserI user, final String subjectId) {
        return XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
    }

    @Override
    public XnatSubjectdata findByProjectAndSubject(final UserI user, final String projectId, final String subjectId) {
        return _template.queryForObject(SUBJECT_QUERY + BY_ID_WHERE_PRO + BY_ID_WHERE_SUB, new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId), new SubjectRowMapper(user));
    }

    @Override
    public List<XnatSubjectdata> findByProject(final UserI user, final String projectId) {
        return _template.query(SUBJECT_QUERY + BY_ID_WHERE_PRO, new MapSqlParameterSource("projectId", projectId), new SubjectRowMapper(user));
    }

    @Override
    public XnatSubjectdata create(final UserI user, final XnatSubjectdata subject) throws XftItemException {
    	  XnatSubjectUtil xnatSubjectUtil = new XnatSubjectUtil();
    	  XFTItem item;
          try {
        		item = xnatSubjectUtil.loadItem("xnat:subjectData", true, subject);
        		
//          final XFTItem item = XFTItem.NewItem(XnatSubjectdata.SCHEMA_ELEMENT_NAME, user);
//  		 	item.setProperty(XnatSubjectdata.SCHEMA_ELEMENT_NAME + ".project", subject.getProject());
//  		    item.setProperty(XnatSubjectdata.SCHEMA_ELEMENT_NAME + ".group", subject.getGroup());
//  		    item.setProperty(XnatSubjectdata.SCHEMA_ELEMENT_NAME + ".label", subject.getLabel());
//          item.setProperty(XnatSubjectdata.SCHEMA_ELEMENT_NAME + ".src", subject.getSrc());
         // item.setProperty(XnatDemographicdata.SCHEMA_ELEMENT_NAME + ".demographics", subject.getDemographics());
          //item.setProperty(XnatSubjectdata.SCHEMA_ELEMENT_NAME + ".keywords", subject.getAge(subject.getDOB()));
          
          XnatSubjectdata sub = new XnatSubjectdata(item);
          xnatSubjectUtil.create(sub, false, false, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getAddModifyAction(sub.getXSIType(), true)), user);

          xnatSubjectUtil.postSaveManageStatus(sub,user);
          } catch (Exception e) {
  	            throw new XftItemException("Failed to create the subject: " + subject.toString(), e);
  	        }
          return subject;
   	}	


    @Override
    public XnatSubjectdata update(final UserI user, final XnatSubjectdata subject) throws XftItemException {
        log.debug("User {} is updating the subject {} in the project {}", user.getUsername(), subject.getLabel(), subject.getProject());
        return null;
    }
    
	 
	 public EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
	        return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action, "", "");
	    }

	    private TYPE getEventType() {
			return null;
		}

		private String getAction() {
			return "Added Subject";
		}

	@Override
    public void deleteById(final UserI user, final String subjectId) throws ClientException {
        delete(user, findById(user, subjectId));
    }

    @Override
    public void delete(final UserI user, final XnatSubjectdata subject) throws ClientException {
        log.debug("User {} is deleting the subject {} in the project {}", user.getUsername(), subject.getLabel(), subject.getProject());
        if(Objects.nonNull(subject)) {
        	XnatSubjectUtil xnatSubjectUtil = new XnatSubjectUtil();
        	xnatSubjectUtil.deleteItem(_projectService.findById(user, subject.getProject()), subject, user);
        }
    }
    
    
    
    private static class SubjectRowMapper implements RowMapper<XnatSubjectdata> {
        SubjectRowMapper(final UserI user) {
            _user = user;
        }

        @Override
        public XnatSubjectdata mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
            final String subjectId = resultSet.getString("id");
            return XnatSubjectdata.getXnatSubjectdatasById(subjectId, _user, false);
        }

        private final UserI _user;
    }

    
    private static final String BY_ID_WHERE_PRO = " WHERE xnat_subjectData.project = :projectId";

    private static final String BY_ID_WHERE_SUB = "  and  xnat_subjectData.id = :subjectId";

    private static final String SUBJECT_QUERY = " SELECT xnat_subjectData.id AS id, xnat_subjectData.project AS project, xnat_subjectData.label AS label,\n"
                                                + " table1.insert_date AS insertDate, table2.login AS insertUser\n" + "FROM xnat_subjectData\n"
                                                + "LEFT JOIN xnat_subjectData_meta_data table1 ON xnat_subjectData.subjectData_info=table1.meta_data_id \n"
                                                + "LEFT JOIN xdat_user table2 ON table1.insert_user_xdat_user_id=table2.xdat_user_id";

    private final NamedParameterJdbcTemplate _template;
    
    private final ProjectService _projectService;
}
