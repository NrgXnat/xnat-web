package org.nrg.xnat.services.subjects.impl;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.transaction.TransactionException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.XnatProjectdataI;
import org.nrg.xdat.model.XnatProjectparticipantI;
import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.model.XnatSubjectdataI;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatProjectparticipant;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.om.base.BaseXnatExperimentdata;
import org.nrg.xdat.om.base.BaseXnatSubjectassessordata;
import org.nrg.xdat.om.base.BaseXnatSubjectdata;
import org.nrg.xdat.om.base.auto.AutoXnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.helpers.merge.ProjectAnonymizer;
import org.nrg.xnat.helpers.merge.anonymize.DefaultAnonUtils;
import org.nrg.xnat.model.util.SecureResourceUtil;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.projects.ProjectService;
import org.nrg.xnat.services.subjects.SubjectService;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import static org.nrg.xft.event.XftItemEventI.CREATE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class SubjectServiceImpl implements SubjectService {
	
    @Autowired
    public SubjectServiceImpl(final NamedParameterJdbcTemplate template, final ProjectService projectService) {
        _template = template;
        _projectService = projectService;
    }

    @Override
    public List<XnatSubjectdataI> findAll(final UserI user) throws NotFoundException {
    	List<XnatSubjectdataI> subjects = Collections.singletonList((XnatSubjectdata) _template.query(SUBJECT_QUERY, new MapSqlParameterSource(), new SubjectRowMapper(user)));
    	if(Objects.isNull(subjects) || subjects.isEmpty()) {
    		throw new  NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME) ;
    	}
    	subjects.removeIf(Objects:: isNull);
        return subjects;
    }

    @Override
    public Optional<XnatSubjectdataI> findById(final UserI user, final String subjectId) throws DataFormatException, NotFoundException {
    	if(StringUtils.isBlank(subjectId)) {
    		throw new DataFormatException("The requested subject ID" + subjectId + " wasn't found ");
    	}
    	XnatSubjectdataI subject = XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
    	if(Objects.isNull(subject)) {
    		throw new  NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME) ;
    	}
    	return Optional.of(subject);
    }

    @Override
    public Optional<XnatSubjectdataI> findByProjectIdAndSubjectId(final UserI user, final String projectId, final String subjectId) throws DataFormatException, NotFoundException {
    	if(StringUtils.isBlank(projectId))
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
    	if(StringUtils.isBlank(subjectId))
    		throw new DataFormatException("The requested subject ID" + subjectId + " wasn't found ");
		XnatSubjectdataI subject = _template.queryForObject(SUBJECT_QUERY + BY_ID_WHERE_PRO + BY_ID_WHERE_SUB + BY_LABEL_WHERE_SUB, new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId), new SubjectRowMapper(user));
    	if(Objects.isNull(subject))
    		throw new  NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME);
    	return Optional.of(subject);
    }

    @Override
    public List<XnatSubjectdataI> findAllByProjectId(final UserI user, final String projectId) throws DataFormatException, NotFoundException {
    	if(StringUtils.isBlank(projectId))
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
    	List<XnatSubjectdataI> subjects = Collections.singletonList((XnatSubjectdata)_template.query(SUBJECT_QUERY + BY_ID_WHERE_PRO, new MapSqlParameterSource("projectId", projectId), new SubjectRowMapper(user)));
    	if(Objects.isNull(subjects) || subjects.isEmpty())
    		throw new  NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME, projectId) ;
    	return subjects;
    }
    
    @Override
    public void deleteById(final UserI user, final String subjectId, boolean removeFiles, XnatEventUtil event) throws ClientException, DataFormatException, NotFoundException, InitializationException, InsufficientPrivilegesException, org.nrg.framework.exceptions.NotFoundException {
        
    	delete(user, findById(user, subjectId).isPresent()? (XnatSubjectdata) findById(user, subjectId).get() :null, removeFiles, event);
    }

    public void delete(final UserI user, final XnatSubjectdataI subject, boolean removeFiles, XnatEventUtil event) throws ClientException, DataFormatException, NotFoundException, InitializationException, InsufficientPrivilegesException, org.nrg.framework.exceptions.NotFoundException {
        log.debug("User {} is deleting the subject {} in the project {}", user.getUsername(), subject.getLabel(), subject.getProject());
        if(Objects.nonNull(subject)) {
        	SecureResourceUtil secureResoureUtil = new SecureResourceUtil();
        	secureResoureUtil.deleteItem((XnatProjectdata) _projectService.findById(user, subject.getProject()).get(), (BaseElement) subject, removeFiles, user, event);
		} else {
			throw new NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME);
		}
    }
    
    @Override
    public XnatSubjectdataI create(final UserI user, final XnatSubjectdataI subject, XnatEventUtil event) throws Exception {
    	  SecureResourceUtil secureResoureUtil = new SecureResourceUtil();
    	  boolean completeDocument = false;
    	  XnatProjectdataI proj = null;
    	  XFTItem item;
			//Step 1: get the XFTItem from subject 
			item = ((ItemI)subject).getItem();

			//Step 2: if item is null then created new XFTItem.
			if (item == null)
				item = XFTItem.NewItem("xnat:subjectData", user);

			// Step 3: Verifying item object is the type of XnatSubjectdata
			if (item.instanceOf("xnat:subjectData")) {
				XnatSubjectdataI sub = new XnatSubjectdata(item);

				if (sub.getExperiments_experiment().size() > 0)
					throw new ClientException("Submitted subject record must not include subject assessors.");

				// Step 4: if XnatProjectdata is null and XnatSubjectdata contains project then fetch XnatProjectdata  
				if (proj == null && sub.getProject() != null)
					proj = XnatProjectdata.getXnatProjectdatasById(sub.getProject(), user, false);

				// Step 5: Verifying XnatProjectdata from XnatSubjectdata
				sub = verifyXnatProjectdataAndGetXnatSubjectdata(proj, sub, user);

				// Step 6: Verifying XnatSubjectdata already exist or not if exist then return
				sub = verifyExistingXnatSubject(sub, user, completeDocument);

				// Step 7: validate XnatSubjectData label and Id
				secureResoureUtil.validateSubject(sub);

				// Step 8: create the XnatSubjectData
				secureResoureUtil.create((ArchivableItem) sub, false, false, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getAddModifyAction(sub.getXSIType(), true), event), event, user);

				// Step 9:
				secureResoureUtil.postSaveManageStatus((ItemI) sub,user,event);
				
				// Step 10:
				return XnatSubjectdata.getXnatSubjectdatasById(sub.getId(), user, false);
        } 
		
          return null;
   	}
    
    public  XnatSubjectdataI verifyXnatProjectdataAndGetXnatSubjectdata(XnatProjectdataI proj, XnatSubjectdataI sub, UserI user) throws Exception {
    	if (proj != null) {
			if (sub.getProject() == null || sub.getProject().equals("")) {
				sub.setProject(proj.getId());
			} else if (sub.getProject().equals(proj.getId())) {
			} else {
				boolean matched = false;
				for (XnatProjectparticipantI pp : sub.getSharing_share()) {
					if (pp.getProject().equals(proj.getId())) {
						matched = true;
						break;
					}
				}
				if (!matched) {
					final XnatProjectparticipantI participant = new XnatProjectparticipant(user);
					participant.setProject(proj.getId());
					((AutoXnatSubjectdata)sub).setSharing_share((ItemI) participant);
				}
			}
		} else 
			throw new ResourceAlreadyExistsException("Submitted subject record must include the project attribute.", null);
		
		return sub;
	}
    
    public XnatSubjectdataI verifyExistingXnatSubject(XnatSubjectdataI sub, UserI user, boolean completeDocument) throws Exception {
    	XnatSubjectdataI existing = null;
		if (sub.getId() != null) 
			existing = XnatSubjectdata.getXnatSubjectdatasById(sub.getId(), user, completeDocument);

		if (existing == null && sub.getProject() != null && sub.getLabel() != null) 
			existing = XnatSubjectdata.GetSubjectByProjectIdentifier(sub.getProject(), sub.getLabel(), user, completeDocument);

		if (existing == null) {
			for (XnatProjectparticipantI pp : sub.getSharing_share()) {
				existing = XnatSubjectdata.GetSubjectByProjectIdentifier(pp.getProject(), pp.getLabel(), user, completeDocument);
				if (existing != null) {
					break;
				}
			}
		}
		if (existing == null) {
			if (!Permissions.canCreate(user, (ItemI) sub))
				throw new InsufficientPrivilegesException("Specified user account has insufficient create privileges for subjects in this project.");
			//IS NEW
			if (StringUtils.isBlank(sub.getId())) 
				sub.setId(XnatSubjectdata.CreateNewID());
		} else 
			throw new ResourceAlreadyExistsException("Subject already exists.", null);
		
		return sub;
	}
    
    @SuppressWarnings("unused")
	@Override
    public XnatSubjectdataI update(final UserI user, final XnatSubjectdataI subject,  String label,boolean primary, String gender, XnatEventUtil event ) throws Exception {
        log.debug("User {} is updating the subject {} in the project {}", user.getUsername(), subject.getLabel(), subject.getProject());
        XnatSubjectdataI existing = null;
        XnatProjectdataI proj = null;
        XnatSubjectdataI sub = null;
       
        String  filepath = null;
        
        if (subject.getProject() != null)
        	proj = XnatProjectdata.getProjectByIDorAlias(subject.getProject(), user, false);
        
        if (proj != null) 
        	existing = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subject.getId(), user, false);
        
        if (existing == null) {
            existing = XnatSubjectdata.getXnatSubjectdatasById(subject.getId(), user, false);
            if (existing != null && (proj != null && !((BaseXnatSubjectdata)existing).hasProject(proj.getId()))) {
                existing = null;
            }
        }
        
        XFTItem item = ((ItemI)subject).getItem();
        
        if (item == null) 
        	item = XFTItem.NewItem("xnat:subjectData", user);
        
        if (item.instanceOf("xnat:subjectData")) {
            sub = new XnatSubjectdata(item);
            
            if (filepath != null && !filepath.equals("")) 
            	sub = updateXnatSubjectFilePathNull(filepath, label, sub, user, primary, existing, event);
            else
            	sub= updateXnatSubjectFilePathNotNull(proj, sub, user, subject, existing, label, event, gender);
            
        } else {
        	throw new ClientException("Only xnat:Subject documents can be PUT to this address.");
        }
		return sub;
    }
    
    
    private static class SubjectRowMapper implements RowMapper<XnatSubjectdataI> {
        SubjectRowMapper(final UserI user) {
            _user = user;
        }

        @Override
        public XnatSubjectdataI mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
            final String subjectId = resultSet.getString("id");
            return XnatSubjectdata.getXnatSubjectdatasById(subjectId, _user, false);
        }

        private final UserI _user;
    }
    
	 
	private XnatSubjectdataI updateXnatSubjectFilePathNotNull(XnatProjectdataI proj, XnatSubjectdataI sub, UserI user, XnatSubjectdataI subject, XnatSubjectdataI existing, String label, XnatEventUtil event, String gender) throws Exception {
		 SecureResourceUtil secureResoureUtil = new SecureResourceUtil();
			if (proj == null && sub.getProject() != null)
				proj = XnatProjectdata.getXnatProjectdatasById(sub.getProject(), user, false);

			verifyUpdateXnatProjectNotNull(proj, sub, user, subject);

			verifyUpdateXnatSubjectExisting(existing, sub, user);

			verifyUpdateXnatSubjectExistingPermission(sub, proj, user, existing, label);

			if (gender != null)
				((BaseElement)sub).setProperty("xnat:subjectData/demographics[@xsi:type=xnat:demographicData]/gender", gender);

			secureResoureUtil.validateSubject(sub);

			PersistentWorkflowI wrk = PersistentWorkflowUtils.buildOpenWorkflow(user, ((ItemI)sub).getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getAddModifyAction(sub.getXSIType(), (existing == null)), event));
			EventMetaI c = wrk.buildEvent();

			// Save the experiment.
			saveExperiment(sub, user, wrk, existing, c);

			secureResoureUtil.postSaveManageStatus((ItemI) sub, user, event);

		return sub;
	}

	private void verifyUpdateXnatSubjectExistingPermission(XnatSubjectdataI sub, XnatProjectdataI proj, UserI user, XnatSubjectdataI existing, String label) throws Exception {
		SecureResourceUtil secureResoureUtil = new SecureResourceUtil();
		if (existing == null) {
			if (!Permissions.canCreate(user, (ItemI) sub))
				throw new InsufficientPrivilegesException("Specified user  account has insufficient create privileges for subjects in this project.");
			
			// IS NEW
			if (StringUtils.isBlank(sub.getId())) {
				sub.setId(XnatSubjectdata.CreateNewID());
			}
		} else {
			if (!existing.getProject().equals(sub.getProject()))
				throw new ResourceAlreadyExistsException("Project must be modified through separate URI.", label);
			

			if (!Permissions.canEdit(user, (ItemI) sub))
				throw new InsufficientPrivilegesException("Specified user account has insufficient edit privileges for subjects in this project.");
			
			if (sub.getId() == null || sub.getId().equals("")) {
				sub.setId(existing.getId());
			}
			if (label != null && !label.equals("")) {
				if (!label.equals(existing.getLabel())) {

					if (!sub.getLabel().equals(existing.getLabel())) {
						// set to old label
						sub.setLabel(existing.getLabel());
					}

					XnatSubjectdataI match = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), label, user,
							false);
					if (match != null) 
						throw new ResourceAlreadyExistsException("Specified label is already in use.", label);

					secureResoureUtil.rename(proj, (ArchivableItem) existing, label, user);
				}
			}
		}
	}

	private XnatSubjectdataI verifyUpdateXnatSubjectExisting(XnatSubjectdataI existing, XnatSubjectdataI sub, UserI user) {
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

	private XnatSubjectdataI verifyUpdateXnatProjectNotNull(XnatProjectdataI proj, XnatSubjectdataI sub, UserI user, XnatSubjectdataI subject) throws ClientException {
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
                        XnatProjectparticipantI pp = new XnatProjectparticipant(user);
                        pp.setProject(proj.getId());
                        pp.setLabel(subject.getId());
                    }
                }
            }
        } else {
        	throw new ClientException("Submitted subject record must include the project attribute.");
        }
		return sub;
	}

	private XnatSubjectdataI saveExperiment(XnatSubjectdataI sub, UserI user, PersistentWorkflowI wrk, XnatSubjectdataI existing, EventMetaI c) throws BaseXnatExperimentdata.UnknownPrimaryProjectException, Exception {
		try {
			//check for unexpected modifications of ID and Project
			if(existing !=null && !StringUtils.equals(existing.getId(),sub.getId()))
				throw new DataFormatException("ID cannot be modified");
			
			
			if(existing !=null && !StringUtils.equals(existing.getProject(),sub.getProject()))
				throw new DataFormatException("Project must be modified through separate URI.");
		
		if (SaveItemHelper.authorizedSave((ItemI) sub, user, false, isQueryVariableTrue("allowDataDeletion"), c)) {
             XDAT.triggerXftItemEvent((BaseElement) sub, CREATE);

             WorkflowUtils.complete(wrk, c);
				Users.clearCache(user);
             MaterializedView.deleteByUser(user);

             // If the label was changed, re apply the anonymization script on all the subject's imaging sessions.
             boolean applyAnonScript = (null != existing && !existing.getLabel().equals(sub.getLabel()));

             if(applyAnonScript){
                for(final XnatSubjectassessordataI expt : ((XnatSubjectdata)sub).getExperiments_experiment("xnat:imageSessionData")){
                     try{
                         String prId = expt.getProject();
                         try {
                             if (DefaultAnonUtils.getService().isProjectScriptEnabled(prId)) {
                                 // re-apply this project's edit script
								 ((BaseXnatSubjectassessordata)expt).applyAnonymizationScript(new ProjectAnonymizer((XnatImagesessiondata) expt, sub.getLabel(), prId, ((BaseXnatSubjectassessordata)expt).getArchiveRootPath()));
                             }
                         }
                         catch(NullPointerException e){
                             log.warn("NullPointerException likely caused by no project anon script configuration ever having been set, so we do not perform anonymization.", e);
                         }
                     }
                     catch (TransactionException e) {
                         throw new InitializationException(e.getMessage());
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

	private XnatSubjectdataI updateXnatSubjectFilePathNull(String filepath, String label, XnatSubjectdataI sub, UserI user, boolean primary, XnatSubjectdataI existing, XnatEventUtil event) throws Exception {
        if (filepath.startsWith("projects/")) {
            if (!Permissions.canRead(user, (ItemI) sub))
            	throw new InsufficientPrivilegesException("Specified user account has insufficient privileges for subjects in this project.");

            String newProjectS = filepath.substring(9);
            XnatProjectdataI newProject = XnatProjectdata.getXnatProjectdatasById(newProjectS, user, false);
            String newLabel = label;

            if (newProject != null) {
                XnatProjectparticipantI matched = null;
                int index = 0;
                for (XnatProjectparticipantI pp : sub.getSharing_share()) {
                    if (pp.getProject().equals(newProject.getId())) {
                        matched = ((XnatProjectparticipant) pp);
                        if (newLabel != null && (pp.getLabel() == null || (!pp.getLabel().equals(newLabel)))) {
                            XnatSubjectdataI temp = XnatSubjectdata.GetSubjectByProjectIdentifier(newProject.getId(), newLabel, null, false);
                            if (temp != null)
                            	throw new ResourceAlreadyExistsException("Label already in use:" , newLabel);
                            
                            pp.setLabel(newLabel);
                            BaseXnatSubjectdata.SaveSharedProject((XnatProjectparticipant) pp, (XnatSubjectdata) sub, user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING, event));

                            if (!primary) {
                                //this.returnDefaultRepresentation();
                                //return;
                            }
                        }
                        break;
                    }
                    index++;
                }

                if (newLabel != null) {
                    XnatSubjectdataI exist = XnatSubjectdata.getXnatSubjectdatasById(sub.getId(), user, false);
                    if (existing != null && !sub.getLabel().equals(exist.getLabel())) {
                        sub.setLabel(exist.getLabel());
                    }
                }

                if (primary) {
                    if (!Permissions.canDelete(user, (ItemI) sub))
                    	throw new InsufficientPrivilegesException("Specified user account has insufficient privileges for subjects in this project.");
                    

                    EventMetaI c = BaseXnatSubjectdata.ChangePrimaryProject(user, (XnatSubjectdata) sub, (XnatProjectdata) newProject, newLabel, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.MODIFY_PROJECT, event));

                    if (matched != null) {
                        SaveItemHelper.authorizedRemoveChild(((ItemI)sub).getItem(), "xnat:subjectData/sharing/share",((ItemI) matched).getItem(), user, c);
						((AutoXnatSubjectdata)sub).removeSharing_share(index);
                    }
                } else {
                    if (matched == null) {
                        if (newLabel != null) {
                            XnatSubjectdataI temp = XnatSubjectdata.GetSubjectByProjectIdentifier(newProject.getId(), newLabel, null, false);
                            if (temp != null)
                            	throw new ResourceAlreadyExistsException( "Label already in use:" , newLabel);
                        }
                        if (Permissions.canCreate(user,sub.getXSIType() + "/project", newProject.getId())) {
                            XnatProjectparticipantI pp = new XnatProjectparticipant(user);
                            pp.setProject(newProject.getId());
                            if (newLabel != null) pp.setLabel(newLabel);
                            pp.setSubjectId(sub.getId());
                            BaseXnatSubjectdata.SaveSharedProject((XnatProjectparticipant) pp, (XnatSubjectdata) sub, user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING, event));
                        } else 
                        	throw new InsufficientPrivilegesException("Specified user account has insufficient create privileges for subjects in the " + newProject.getId() + " project.");
                    } else 
                    	throw new ResourceAlreadyExistsException("Already assigned to project:", newProject.getId());
                }

            } else 
            	throw new NotFoundException("Unable to identify project: " + newProjectS);
        } else 
        	throw new DataFormatException("Please check request object");

		return sub;
    
		
	}

	private boolean isQueryVariableTrue(String pRIMARY) {
		return false;
	}

    
    private static final String BY_ID_WHERE_PRO = " WHERE xnat_subjectData.project = :projectId";

    private static final String BY_ID_WHERE_SUB = "  and  xnat_subjectData.id = :subjectId";
    
    private static final String BY_LABEL_WHERE_SUB = "  OR  xnat_subjectData.label = :subjectId";

    private static final String SUBJECT_QUERY = " SELECT xnat_subjectData.id AS id, xnat_subjectData.project AS project, xnat_subjectData.label AS label,\n"
                                                + " table1.insert_date AS insertDate, table2.login AS insertUser\n" + "FROM xnat_subjectData\n"
                                                + "LEFT JOIN xnat_subjectData_meta_data table1 ON xnat_subjectData.subjectData_info=table1.meta_data_id \n"
                                                + "LEFT JOIN xdat_user table2 ON table1.insert_user_xdat_user_id=table2.xdat_user_id";

    private final NamedParameterJdbcTemplate _template;
    private final ProjectService _projectService;

}
