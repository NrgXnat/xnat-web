package org.nrg.xnat.services.subjects.impl;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.transaction.TransactionException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.XnatProjectparticipantI;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatProjectparticipant;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.om.base.BaseXnatExperimentdata.UnknownPrimaryProjectException;
import org.nrg.xdat.om.base.BaseXnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.EventUtils.TYPE;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.InvalidValueException;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.helpers.merge.ProjectAnonymizer;
import org.nrg.xnat.helpers.merge.anonymize.DefaultAnonUtils;
import org.nrg.xnat.model.util.XnatSubjectUtil;
import org.nrg.xnat.services.projects.ProjectService;
import org.nrg.xnat.services.subjects.SubjectService;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXParseException;

import static org.nrg.xft.event.XftItemEventI.CREATE;

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
    
    
    
    
    @Override
    public XnatSubjectdata create(final UserI user, final XnatSubjectdata subject) throws XftItemException {
    	  XnatSubjectUtil xnatSubjectUtil = new XnatSubjectUtil();
    	  boolean completeDocument = false;
    	  XnatProjectdata proj = null;
    	  XFTItem item;
		try {
			//Step 1: get the XFTItem from subject 
			item = subject.getItem();

			//Step 2: if item is null then created new XFTItem.
			if (item == null)
				item = XFTItem.NewItem("xnat:subjectData", user);

			// Step 3: Verifying item object is the type of XnatSubjectdata
			if (item.instanceOf("xnat:subjectData")) {
				XnatSubjectdata sub = new XnatSubjectdata(item);

				if (sub.getExperiments_experiment().size() > 0)
					throw new ClientException("Submitted subject record must not include subject assessors.");

				// Step 4: if XnatProjectdata is null and XnatSubjectdata contains project then fetch XnatProjectdata  
				if (proj == null && sub.getProject() != null)
					proj = XnatProjectdata.getXnatProjectdatasById(sub.getProject(), user, false);

				// Step 5: Verifying XnatProjectdata from XnatSubjectdata
				sub = xnatSubjectUtil.verifyXnatProjectdataAndGetXnatSubjectdata(proj, sub, user);

				// Step 6: Verifying XnatSubjectdata already exist or not if exist then return
				sub = xnatSubjectUtil.verifyExistingXnatSubject(sub, user, completeDocument);

				// Step 7: validate XnatSubjectData label and Id
				xnatSubjectUtil.validateSubject(sub);

				// Step 8: create the XnatSubjectData
				xnatSubjectUtil.create(sub, false, false, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getAddModifyAction(sub.getXSIType(), true)), user);

				// Step 9:
				xnatSubjectUtil.postSaveManageStatus(sub,user);
				
				// Step 10:
				return XnatSubjectdata.getXnatSubjectdatasById(sub.getId(), user, false);
        } 
		} catch (Exception e) {
			throw new XftItemException("Failed to create the subject: " + subject.toString(), e);
		}
		
          return null;
   	}	
    
    @Override
    public XnatSubjectdata update(final UserI user, final XnatSubjectdata subject,  String label) throws XftItemException {
        log.debug("User {} is updating the subject {} in the project {}", user.getUsername(), subject.getLabel(), subject.getProject());
        XnatSubjectUtil xnatSubjectUtil = new XnatSubjectUtil();
        XnatSubjectdata existing = null;
        XnatProjectdata proj = null;
        XnatSubjectdata sub = null;
        try {
        String  filepath = null;
        final String PRIMARY = "primary";
        
        if (subject.getProject() != null)
        	proj = XnatProjectdata.getProjectByIDorAlias(subject.getProject(), user, false);
        
        if (proj != null) 
        	existing = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subject.getId(), user, false);
        
        if (existing == null) {
            existing = XnatSubjectdata.getXnatSubjectdatasById(subject.getId(), user, false);
            if (existing != null && (proj != null && !existing.hasProject(proj.getId()))) {
                existing = null;
            }
        }
        
        XFTItem item = subject.getItem();
        
        if (item == null) 
        	item = XFTItem.NewItem("xnat:subjectData", user);
        
        if (item.instanceOf("xnat:subjectData")) {
            sub = new XnatSubjectdata(item);
            
            if (filepath != null && !filepath.equals("")) {
            	sub = updateXnatSubjectFilePathNull(filepath, label, sub, user, PRIMARY, existing);
            } else {
            	sub= updateXnatSubjectFilePathNotNull(proj, sub, user, subject, existing, label, xnatSubjectUtil);
            }
        } else {
            //this.getResponse().setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, "Only xnat:Subject documents can be PUT to this address.");
        }
    } catch (Exception e) {
        //this.getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        log.error("", e);
    }
		return sub;
    }
    
	 
	private XnatSubjectdata updateXnatSubjectFilePathNotNull(XnatProjectdata proj, XnatSubjectdata sub, UserI user, XnatSubjectdata subject, XnatSubjectdata existing, String label, XnatSubjectUtil xnatSubjectUtil) throws Exception {
		try {

			if (proj == null && sub.getProject() != null)
				proj = XnatProjectdata.getXnatProjectdatasById(sub.getProject(), user, false);

			verifyUpdateXnatProjectNotNull(proj, sub, user, subject);

			verifyUpdateXnatSubjectExisting(existing, sub, user);

			verifyUpdateXnatSubjectExistingPermission(sub, proj, user, existing, label, xnatSubjectUtil);

//        if (getQueryVariable("gender") != null) {
//            sub.setProperty("xnat:subjectData/demographics[@xsi:type=xnat:demographicData]/gender", this.getQueryVariable("gender"));
//        }

			xnatSubjectUtil.validateSubject(sub);

			PersistentWorkflowI wrk = PersistentWorkflowUtils.buildOpenWorkflow(user, sub.getItem(), newEventInstance(
					EventUtils.CATEGORY.DATA, EventUtils.getAddModifyAction(sub.getXSIType(), (existing == null))));
			EventMetaI c = wrk.buildEvent();

			// Save the experiment.
			saveExperiment(sub, user, wrk, existing, c);

			xnatSubjectUtil.postSaveManageStatus(sub, user);

			// returnString(sub.getId(), (existing == null) ? Status.SUCCESS_CREATED :
			// Status.SUCCESS_OK);

		} catch (SAXParseException e) {
			// this.getResponse().setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY,
			// e.getMessage());
		} catch (InvalidValueException e) {
			// this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			log.error("", e);
		} catch (ActionException e) {
			// this.getResponse().setStatus(e.getStatus(),e.getMessage());
		}
		return sub;
	}

	private void verifyUpdateXnatSubjectExistingPermission(XnatSubjectdata sub, XnatProjectdata proj, UserI user, XnatSubjectdata existing, String label, XnatSubjectUtil xnatSubjectUtil) throws Exception {
		if (existing == null) {
			if (!Permissions.canCreate(user, sub)) {
				// this.getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Specified user
				// account has insufficient create privileges for subjects in this project.");
				// return;
			}
			// IS NEW
			if (StringUtils.isBlank(sub.getId())) {
				sub.setId(XnatSubjectdata.CreateNewID());
			}
		} else {
			if (!existing.getProject().equals(sub.getProject())) {
				// this.getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT, "Project must be
				// modified through separate URI.");
				// return;
			}

			if (!Permissions.canEdit(user, sub)) {
				// this.getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Specified user
				// account has insufficient edit privileges for subjects in this project.");
				// return;
			}
			if (sub.getId() == null || sub.getId().equals("")) {
				sub.setId(existing.getId());
			}
			// if(getQueryVariable("label")!=null && !getQueryVariable("label").equals("") )
			if (label != null && !label.equals("")) {
				// String label=getQueryVariable("label");

				if (!label.equals(existing.getLabel())) {

					if (!sub.getLabel().equals(existing.getLabel())) {
						// set to old label
						sub.setLabel(existing.getLabel());
					}

					XnatSubjectdata match = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), label, user,
							false);
					if (match != null) {
						// getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT,"Specified label is
						// already in use.");
						// return;
					}

					xnatSubjectUtil.rename(proj, existing, label, user);
				}
				// return;
			}
		}
	}

	private XnatSubjectdata verifyUpdateXnatSubjectExisting(XnatSubjectdata existing, XnatSubjectdata sub, UserI user) {
		 if (existing == null) {
	            if (sub.getId() != null) {
	                existing = XnatSubjectdata.getXnatSubjectdatasById(sub.getId(), user, false);
	            }

	            if (existing == null && sub.getProject() != null && sub.getLabel() != null) {
	                existing = XnatSubjectdata.GetSubjectByProjectIdentifier(sub.getProject(), sub.getLabel(), user, false);
	            }

	            if (existing == null) {
	                for (XnatProjectparticipantI pp : sub.getSharing_share()) {
	                    existing = XnatSubjectdata.GetSubjectByProjectIdentifier(pp.getProject(), pp.getLabel(), user, false);
	                    if (existing != null) {
	                        break;
	                    }
	                }
	            }
	        }
		return existing;
		
	}

	private XnatSubjectdata verifyUpdateXnatProjectNotNull(XnatProjectdata proj, XnatSubjectdata sub, UserI user, XnatSubjectdata subject) {
		if (proj != null) {
            if (sub.getProject() == null || sub.getProject().equals("")) {
                sub.setProject(proj.getId());

                if (sub.getLabel() == null || sub.getLabel().equals("")) {
                    sub.setLabel(subject.getId());
                }
            } else {
                if (sub.getProject().equals(proj.getId())) {
                    if (sub.getLabel() == null || sub.getLabel().equals("")) {
                        sub.setLabel(subject.getId());
                    }
                } else {
                    boolean matched = false;
                    for (XnatProjectparticipantI pp : sub.getSharing_share()) {
                        if (pp.getProject().equals(proj.getId())) {
                            matched = true;

                            if (pp.getLabel() == null || pp.getLabel().equals("")) {
                                pp.setLabel(subject.getId());
                            }
                            break;
                        }
                    }

                    if (!matched) {
                        XnatProjectparticipant pp = new XnatProjectparticipant(user);
                        pp.setProject(proj.getId());
                        pp.setLabel(subject.getId());
                    }
                }
            }
        } else {
            //this.getResponse().setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, "Submitted subject record must include the project attribute.");
           // return;
        }
		return sub;
		
	}

	private XnatSubjectdata saveExperiment(XnatSubjectdata sub, UserI user, PersistentWorkflowI wrk, XnatSubjectdata existing, EventMetaI c) throws UnknownPrimaryProjectException, Exception {
		try {
			//check for unexpected modifications of ID and Project
			if(existing !=null && !StringUtils.equals(existing.getId(),sub.getId())){
				//this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"ID cannot be modified");
				//return;
			}
			
			if(existing !=null && !StringUtils.equals(existing.getProject(),sub.getProject())){
				//this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,"Project must be modified through separate URI.");
				//return;
			}  
		
		if (SaveItemHelper.authorizedSave(sub, user, false, isQueryVariableTrue("allowDataDeletion"), c)) {
             XDAT.triggerXftItemEvent(sub, CREATE);
             WorkflowUtils.complete(wrk, c);
				Users.clearCache(user);
             MaterializedView.deleteByUser(user);

             // If the label was changed, re apply the anonymization script on all the subject's imaging sessions.
             boolean applyAnonScript = (null != existing && !existing.getLabel().equals(sub.getLabel()));

             if(applyAnonScript){
                for(final XnatSubjectassessordata expt : sub.getExperiments_experiment("xnat:imageSessionData")){
                     try{
                         String prId = expt.getProject();
                         try {
                             if (DefaultAnonUtils.getService().isProjectScriptEnabled(prId)) {
                                 // re-apply this project's edit script
                                 expt.applyAnonymizationScript(new ProjectAnonymizer((XnatImagesessiondata) expt, sub.getLabel(), prId, expt.getArchiveRootPath()));
                             }
                         }
                         catch(NullPointerException e){
                             log.warn("NullPointerException likely caused by no project anon script configuration ever having been set, so we do not perform anonymization.", e);
                         }
                     }
                     catch (TransactionException e) {
                        //this.getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
                     }
                }
             }
         }
		 } catch (Exception e) {
	            WorkflowUtils.fail(wrk, c);
	            throw e;
	        }
		return sub;
		
	}

	private XnatSubjectdata updateXnatSubjectFilePathNull(String filepath, String label, XnatSubjectdata sub, UserI user, String PRIMARY, XnatSubjectdata existing) throws Exception {

        if (filepath.startsWith("projects/")) {
            if (!Permissions.canRead(user,sub)) {
               // this.getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Specified user account has insufficient privileges for subjects in this project.");
                //return;
            }

            String newProjectS = filepath.substring(9);
            XnatProjectdata newProject = XnatProjectdata.getXnatProjectdatasById(newProjectS, user, false);
            String newLabel = label; //HC
            		//getQueryVariable("label");

            if (newProject != null) {
                XnatProjectparticipant matched = null;
                int index = 0;
                for (XnatProjectparticipantI pp : sub.getSharing_share()) {
                    if (pp.getProject().equals(newProject.getId())) {
                        matched = ((XnatProjectparticipant) pp);
                        if (newLabel != null && (pp.getLabel() == null || (!pp.getLabel().equals(newLabel)))) {
                            XnatSubjectdata temp = XnatSubjectdata.GetSubjectByProjectIdentifier(newProject.getId(), newLabel, null, false);
                            if (temp != null) {
                                //this.getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT, "Label already in use:" + newLabel);
                                //return;
                            }

                            pp.setLabel(newLabel);
                            BaseXnatSubjectdata.SaveSharedProject((XnatProjectparticipant) pp, sub, user, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING));

                            if (!isQueryVariableTrue(PRIMARY)) {
                                //this.returnDefaultRepresentation();
                                //return;
                            }
                        }
                        break;
                    }
                    index++;
                }

                if (newLabel != null) {
                    XnatSubjectdata exist = XnatSubjectdata.getXnatSubjectdatasById(sub.getId(), user, false);
                    if (existing != null && !sub.getLabel().equals(exist.getLabel())) {
                        sub.setLabel(exist.getLabel());
                    }
                }

                if (isQueryVariableTrue(PRIMARY)) {
                    if (!Permissions.canDelete(user,sub)) {
                        //this.getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Specified user account has insufficient privileges for subjects in this project.");
                        //return;
                    }

                    EventMetaI c = BaseXnatSubjectdata.ChangePrimaryProject(user, sub, newProject, newLabel, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.MODIFY_PROJECT));

                    if (matched != null) {
                        SaveItemHelper.authorizedRemoveChild(sub.getItem(), "xnat:subjectData/sharing/share", matched.getItem(), user, c);
                        sub.removeSharing_share(index);
                    }
                } else {
                    if (matched == null) {
                        if (newLabel != null) {
                            XnatSubjectdata temp = XnatSubjectdata.GetSubjectByProjectIdentifier(newProject.getId(), newLabel, null, false);
                            if (temp != null) {
                                //this.getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT, "Label already in use:" + newLabel);
                                //return;
                            }
                        }
                        if (Permissions.canCreate(user,sub.getXSIType() + "/project", newProject.getId())) {
                            XnatProjectparticipant pp = new XnatProjectparticipant(user);
                            pp.setProject(newProject.getId());
                            if (newLabel != null) pp.setLabel(newLabel);
                            pp.setSubjectId(sub.getId());
                            BaseXnatSubjectdata.SaveSharedProject(pp, sub, user, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING));
                        } else {
                           // this.getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Specified user account has insufficient create privileges for subjects in the " + newProject.getId() + " project.");
                            //return;
                        }
                    } else {
                        //this.getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT, "Already assigned to project:" + newProject.getId());
                        //return;
                    }
                }

                //this.returnDefaultRepresentation();
            } else {
                //this.getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Unable to identify project: " + newProjectS);
            }
        } else {
            //this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
		return sub;
    
		
	}

	private boolean isQueryVariableTrue(String pRIMARY) {
		return false;
	}

    public EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) { //HC
        return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action, "", "");
    }

    private TYPE getEventType() {
    	final String id = null;  //HC
    			//getQueryVariable(EventUtils.EVENT_TYPE);
        if (id != null) {
            return EventUtils.getType(id, EventUtils.TYPE.WEB_SERVICE);
        } else {
            return EventUtils.TYPE.WEB_SERVICE;
        }
	}

	private String getAction() {
		return "Added Subject";  //HC
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
