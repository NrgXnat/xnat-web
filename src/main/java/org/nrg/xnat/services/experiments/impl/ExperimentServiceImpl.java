package org.nrg.xnat.services.experiments.impl;

import static org.nrg.xft.event.XftItemEventI.DELETE;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.transaction.TransactionException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.XnatExperimentdataShareI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatExperimentdataShare;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagescandataShare;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatPvisitdata;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.om.base.BaseXnatExperimentdata;
import org.nrg.xdat.om.base.BaseXnatImagescandata;
import org.nrg.xdat.om.base.BaseXnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEvent;
import org.nrg.xft.event.EventUtils.TYPE;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.InvalidValueException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xft.utils.ValidationUtils.ValidationResults;
import org.nrg.xnat.archive.ValidationException;
import org.nrg.xnat.helpers.merge.ProjectAnonymizer;
import org.nrg.xnat.model.util.SecureResoureUtil;
import org.nrg.xnat.model.util.XnatProjectUtil;
import org.nrg.xnat.restlet.actions.FixScanTypes;
import org.nrg.xnat.restlet.actions.PullSessionDataFromHeaders;
import org.nrg.xnat.restlet.actions.TriggerPipelines;
import org.nrg.xnat.restlet.util.XNATRestConstants;
import org.nrg.xnat.services.experiments.ExperimentService;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExperimentServiceImpl implements ExperimentService {
	
	@Autowired
	public ExperimentServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public List<XnatExperimentdata> getAll(UserI user) {
		return XnatExperimentdata.getAllXnatExperimentdatas(user, false);
	}

	@Override
	public XnatExperimentdata findById(UserI user, String experimentId) {
		return XnatExperimentdata.getXnatExperimentdatasById(experimentId, user, false);
	}

	@Override
	public List<XnatExperimentdata> findByProject(UserI user, String projectId) {
		return _template.query(PROJECT_EXPERIMENT_QUERY, new MapSqlParameterSource("projectId", projectId),new ExperimentRowMapper(user));
	}

	@Override
	public List<XnatExperimentdata> findByProjectAndSubject(UserI user, String projectId, String subjectId) {
		return _template.query(PROJECT_SUBJECT_EXPERIMENT_QUERY, new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId),new ExperimentRowMapper(user));
	}
	
	@Override
	public XnatExperimentdata findByIdAndProject(UserI user, String experimentId, String projectId) {
		return _template.queryForObject(PROJECT_AND_EXPERIMENT_QUERY + BY_PRO_EXP_ID_WHERE, new MapSqlParameterSource("experimentId", experimentId).addValue("projectId", projectId),new ExperimentRowMapper(user));
	}
	
	@Override
	public XnatExperimentdata create(UserI user, XnatExperimentdata xnatExperimentdata, String projectId, String subjectId) throws Exception {
		
		XnatProjectdata proj = null;
		XnatSubjectdata subject=null;
		XnatSubjectassessordata expt=null;
		SecureResoureUtil secureResoureUtil = new SecureResoureUtil();
		if (projectId != null) {
			proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
			if (proj == null)
				throw new NotFoundException("Unable to identify project " + projectId);
			
			if (subjectId != null) {
				subject = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subjectId, user, false);
				if (subject == null) {
					subject = XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
					if (subject != null && (proj != null && !subject.hasProject(proj .getId()))) {
						subject = null;
					}
				}
			}
			
		try {
		XFTItem item = xnatExperimentdata.getItem();

		if (item == null) {
			String xsiType = this.getQueryVariable("xsiType");
			if (xsiType != null) {
				item = XFTItem.NewItem(xsiType, user);
			}
		}

		if (item == null) 
			throw new DataFormatException("Need PUT Contents");

		if (item.instanceOf("xnat:subjectAssessorData")) {
			 expt = (XnatSubjectassessordata) BaseElement.GetGeneratedItem(item);

			//MATCH PROJECT
			if (proj == null && expt.getProject() != null) 
				proj = XnatProjectdata.getXnatProjectdatasById(expt.getProject(), user, false);
			

			if (proj != null) {
				if (expt.getProject() == null || expt.getProject().equals("")) {
					expt.setProject(proj.getId());
				} else if (expt.getProject().equals(proj.getId())) {
				} else {
					boolean matched = false;
					for (XnatExperimentdataShareI pp : expt.getSharing_share()) {
						if (pp.getProject().equals(proj.getId())) {
							matched = true;
							break;
						}
					}

					if (!matched) {
						XnatExperimentdataShare pp = new XnatExperimentdataShare((UserI) user);
						pp.setProject(proj.getId());
						expt.setSharing_share(pp);
					}
				}
			} else 
				throw new DataFormatException("Submitted experiment record must include the project attribute.");
			

			//MATCH SUBJECT
			if (subject != null) {
				expt.setSubjectId(subject.getId());
			} else {
				if (expt.getSubjectId() != null && !expt.getSubjectId().equals("")) {
					subject = XnatSubjectdata.getXnatSubjectdatasById(expt.getSubjectId(), user, false);

					if (subject == null && expt.getProject() != null && expt.getLabel() != null) {
						subject = XnatSubjectdata.GetSubjectByProjectIdentifier(expt.getProject(), expt.getSubjectId(), user, false);
					}

					if (subject == null) {
						for (XnatExperimentdataShareI pp : expt.getSharing_share()) {
							subject = XnatSubjectdata.GetSubjectByProjectIdentifier(pp.getProject(), expt.getSubjectId(), user, false);
							if (subject != null) {
								break;
							}
						}
					}

					if (subject == null) {
						final String newSubjectId = XnatSubjectdata.CreateNewID();
						subject = new XnatSubjectdata(user);
						subject.setProject(proj.getId());
						subject.setLabel(expt.getSubjectId());
						subject.setId(newSubjectId);
						secureResoureUtil.create(subject, false, true, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.AUTO_CREATE_SUBJECT), user);
						expt.setSubjectId(subject.getId());
					}
				}
			}

			if (subject == null) 
				throw new DataFormatException("Submitted experiment record must include the subject.");
			

			//FIND PRE-EXISTING
			XnatSubjectassessordata existing = null;
			if (expt.getId() != null) {
				existing = (XnatSubjectassessordata) XnatExperimentdata.getXnatExperimentdatasById(expt.getId(), user, completeDocument);
			}

			if (existing == null && expt.getProject() != null && expt.getLabel() != null) {
				existing = (XnatSubjectassessordata) XnatExperimentdata.GetExptByProjectIdentifier(expt.getProject(), expt.getLabel(), user, completeDocument);
			}

			if (existing == null) {
				for (XnatExperimentdataShareI pp : expt.getSharing_share()) {
					existing = (XnatSubjectassessordata) XnatExperimentdata.GetExptByProjectIdentifier(pp.getProject(), pp.getLabel(), user, completeDocument);
					if (existing != null) {
						break;
					}
				}
			}

			if (existing == null) {
				if (!Permissions.canCreate(user, expt)) 
					throw new InsufficientPrivilegesException("Specified user account has insufficient create privileges for experiments in this project.");
			
				//IS NEW
				if (expt.getId() == null || expt.getId().equals("")) {
					expt.setId(XnatExperimentdata.CreateNewID());
				}
			} else {
				throw new ResourceAlreadyExistsException("Specified experiment already exists.", expt.getLabel());
			}

			boolean allowDataDeletion = false;
			if (this.getQueryVariable("allowDataDeletion") != null && this.getQueryVariable("allowDataDeletion").equals("true")) {
				allowDataDeletion = true;
			}

			if (StringUtils.isNotBlank(expt.getLabel()) && !XftStringUtils.isValidId(expt.getId()))
				throw new DataFormatException("Invalid character in experiment label.");

			final ValidationResults vr = expt.validate();

			if (vr != null && !vr.isValid()) 
				throw new DataFormatException(vr.toFullString());

			secureResoureUtil.create(expt, false, allowDataDeletion, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getAddModifyAction(expt.getXSIType(), (existing == null))), user);

			secureResoureUtil.postSaveManageStatus(expt, user);

			if (Permissions.canEdit(user, expt.getItem())) {
				if (this.isQueryVariableTrue(XNATRestConstants.TRIGGER_PIPELINES) || secureResoureUtil.containsAction(XNATRestConstants.TRIGGER_PIPELINES)) {
					TriggerPipelines tp = new TriggerPipelines(expt, this.isQueryVariableTrue(XNATRestConstants.SUPRESS_EMAIL), user);
					tp.call();
				}
					}

				} else {
					throw new DataFormatException("Only xnat:Subject documents can be PUT to this address.");
				}
			} catch (ActionException e) {
				log.error("ActionException", e.getMessage());
			} catch (InvalidValueException e) {
				 log.error("InvalidValueException", e.getMessage());
			} catch (Exception e) {
				log.error("SERVER_ERROR_INTERNAL", e);
	        	throw new Exception("Something went wrong");
			}
		}
		return expt;
	}
	
	@Override
	public XnatExperimentdata update(UserI user, XnatExperimentdata xnatexperiment, String experimentId, String projectId, String subjectId) throws Exception {
		XnatExperimentdata existing   = new XnatExperimentdata();
		XnatProjectdata project = null;
		XnatExperimentdata experiment = null;
		SecureResoureUtil secureResoureUtil = new SecureResoureUtil();
		if (StringUtils.isNotBlank(projectId)) {
			project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
			existing = XnatExperimentdata.GetExptByProjectIdentifier(projectId, experimentId, user, false);
		}

		try {
			XFTItem template = null;
			if (existing != null)
				template = existing.getItem().getCurrentDBVersion();

			XFTItem item = getXnatExperimentItem(user, project, xnatexperiment, experimentId);

			experiment = (XnatExperimentdata) BaseElement.GetGeneratedItem(item);

			String filepath = "";

			if (filepath != null && !filepath.equals("")) {
				filePathIsNotNull(filepath, user, experiment, project);
			} else {
				if (experiment.getLabel() == null)
					experiment.setLabel(experimentId);
				
				// MATCH PROJECT
				if (project == null && experiment.getProject() != null) 
					project = XnatProjectdata.getXnatProjectdatasById(experiment.getProject(), user, false);
				
				experiment = verifyProjectNotNull(project, experiment, user);

                // Find the pre-existing experiment
                if (existing == null)
                	existing = getExistingExperiment(experiment, user);
                
                if (existing == null) 
                	experiment = existingExperimentIsNull(experiment, user, existing, project, subjectId);
                 else 
                	 experiment = existingExperimentIsNotNull(experiment, user, existing, project, secureResoureUtil, subjectId);
                

                boolean allowDataDeletion = false;
                if (getQueryVariable("allowDataDeletion") != null && getQueryVariable("allowDataDeletion").equals("true")) 
                	allowDataDeletion = true;
                
                PersistentWorkflowI wrk = WorkflowUtils.buildOpenWorkflow(user, experiment.getItem(), newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getAddModifyAction(experiment.getXSIType(), (existing == null))));
                EventMetaI c = wrk.buildEvent();

                if (isQueryVariableTrue(XNATRestConstants.FIX_SCAN_TYPES) || secureResoureUtil.containsAction(XNATRestConstants.FIX_SCAN_TYPES)) {
                    if (experiment instanceof XnatImagesessiondata) {
                        FixScanTypes fst = new FixScanTypes(experiment, user, project, false, c);
                        fst.call();
                    }
                }

                if (StringUtils.isNotBlank(experiment.getLabel()) && !XftStringUtils.isValidId(experiment.getId()))
                	throw new DataFormatException("Invalid character in experiment label.");
              

                final ValidationResults vr = experiment.validate();

                if (vr != null && !vr.isValid()) 
                	throw new DataFormatException(vr.toFullString());
              
                try {
                    //check for unexpected modifications of ID, Project and label
                    if (existing != null && !StringUtils.equals(existing.getId(), experiment.getId())) 
                    	throw new DataFormatException("ID cannot be modified");

                    if (existing != null && !StringUtils.equals(existing.getProject(), experiment.getProject())) 
                    	throw new DataFormatException("Project must be modified through separate URI.");

                    //MATCHED
                    if (existing != null && !StringUtils.equals(existing.getLabel(), experiment.getLabel())) 
                    	throw new DataFormatException("Label must be modified through separate URI.");

                    // Preserve the previous version of the experiment before we save it.
                    XnatExperimentdata previous = getExistingExperiment(experiment,user);
                    if (existing == null ? secureResoureUtil.create(experiment, false, allowDataDeletion, wrk, c, user) : secureResoureUtil.update(experiment, false, allowDataDeletion, wrk, c, user)) {
                        if (project.getArcSpecification().getQuarantineCode() != null && project.getArcSpecification().getQuarantineCode().equals(1)) {
                            experiment.quarantine(user);
                        }

                        if (experiment instanceof XnatImagesessiondata && previous != null) {
                            anonymize((XnatImagesessiondata) experiment, (XnatImagesessiondata) previous, experiment);
                        }
                    }
                } catch (Exception e1) {
                    WorkflowUtils.fail(wrk, c);
                    throw e1;
                }

                secureResoureUtil.postSaveManageStatus(experiment, user);

                verifyPermission(user,experiment, wrk, c, secureResoureUtil);
                
            }

        } catch (InvalidValueException e) {
            log.error("InvalidValueException", e);
        } catch (ActionException e) {
        	log.error("ActionException", e.getMessage());
        } catch (Exception e) {
        	log.error("SERVER_ERROR_INTERNAL", e);
        	throw new Exception("Something went wrong");
            
        }
		return experiment;
	}
	
	private void verifyPermission(UserI user, XnatExperimentdata experiment, PersistentWorkflowI wrk, EventMetaI c, SecureResoureUtil secureResoureUtil) throws Exception {
		  if (Permissions.canEdit(user, experiment.getItem())) {
              if ((isQueryVariableTrue(XNATRestConstants.PULL_DATA_FROM_HEADERS) || secureResoureUtil.containsAction(XNATRestConstants.PULL_DATA_FROM_HEADERS)) && experiment instanceof XnatImagesessiondata) {
                  try {
                      wrk = PersistentWorkflowUtils.buildOpenWorkflow(user, experiment.getItem(), newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.DICOM_PULL));
                      assert wrk != null;
                      c = wrk.buildEvent();
                      try {
                          PullSessionDataFromHeaders pull = new PullSessionDataFromHeaders((XnatImagesessiondata) experiment, user, allowDataDeletion(), isQueryVariableTrue("overwrite"), false, c);
                          pull.call();
                          WorkflowUtils.complete(wrk, c);
                      } catch (Exception e) {
                          WorkflowUtils.fail(wrk, c);
                          throw e;
                      }

                  } catch (SAXException e) {
                      log.error("Error processing XML", e.getMessage());
                  } catch (ValidationException e) {
                      log.error("Error validating the item", e.getMessage());
                  } catch (Exception e) {
                      log.error("Unknown error encountered",  e.getMessage());
                  }
              }

              if (isQueryVariableTrue(XNATRestConstants.TRIGGER_PIPELINES) || secureResoureUtil.containsAction(XNATRestConstants.TRIGGER_PIPELINES)) {
                  TriggerPipelines tp = new TriggerPipelines(experiment, isQueryVariableTrue(XNATRestConstants.SUPRESS_EMAIL), user);
                  tp.call();
              }
          }
		
	}

	private XnatExperimentdata existingExperimentIsNotNull(XnatExperimentdata experiment, UserI user, XnatExperimentdata existing, XnatProjectdata project, SecureResoureUtil secureResoureUtil, String subjectId) throws Exception {
		 if (StringUtils.isBlank(experiment.getId())) {
             experiment.setId(existing.getId());
         }

         //MATCHED
         if (!existing.getProject().equals(experiment.getProject())) 
        	 throw new ResourceAlreadyExistsException("Project must be modified through separate URI.", experiment.getProject());

         if (!Permissions.canEdit(user, experiment))
        	 throw new InsufficientPrivilegesException("Specified user account has insufficient edit privileges for experiments in this project.");

         setSubject(existing.getItem(), experiment, project, existing, user, subjectId);

         if (getQueryVariable("label") != null && !getQueryVariable("label").equals("")) {
             if (!experiment.getLabel().equals(existing.getLabel())) {
                 experiment.setLabel(existing.getLabel());
             }
             String label = getQueryVariable("label");

             if (!label.equals(existing.getLabel())) {
                 XnatExperimentdata match = XnatExperimentdata.GetExptByProjectIdentifier(project.getId(), label, user, false);
                 if (match != null)
                	 throw new ResourceAlreadyExistsException("Specified label is already in use.", existing.getLabel());

                 secureResoureUtil.rename(project, existing, label, user);
             }
         }
		return experiment;
	}

	private XnatExperimentdata existingExperimentIsNull(XnatExperimentdata experiment, UserI user, XnatExperimentdata existing, XnatProjectdata project, String subjectId) throws Exception {
		 if (!Permissions.canCreate(user, experiment)) 
			 throw new InsufficientPrivilegesException("Specified user account has insufficient create privileges for experiments in this project.");

		 //IS NEW
         if (StringUtils.isBlank(experiment.getId())) {
             experiment.setId(XnatExperimentdata.CreateNewID());
         }

         setSubject(existing.getItem(), experiment, project, existing, user, subjectId);
         
		return experiment;
		
	}

	private XnatExperimentdata verifyProjectNotNull(XnatProjectdata project, XnatExperimentdata experiment, UserI user) throws Exception {
		  if (project != null) {
              if (experiment.getProject() == null || experiment.getProject().equals("")) {
                  experiment.setProject(project.getId());
              } else if (!experiment.getProject().equals(project.getId())) {
                  boolean matched = false;
                  for (XnatExperimentdataShareI pp : experiment.getSharing_share()) {
                      if (pp.getProject().equals(project.getId())) {
                          matched = true;
                          break;
                      }
                  }

                  if (!matched) {
                      XnatExperimentdataShare pp = new XnatExperimentdataShare(user);
                      pp.setProject(project.getId());
                      experiment.setSharing_share(pp);
                  }
              }
          } else 
        	  throw new DataFormatException("Submitted experiment record must include the project attribute.");
		  
		return experiment;
		
	}

	@SuppressWarnings("unused")
	private void filePathIsNotNull(String filepath, UserI user, XnatExperimentdata experiment, XnatProjectdata project) throws Exception {
        if (filepath.startsWith("projects/")) {
            String newProjectS = filepath.substring(9);
            XnatProjectdata newProject = XnatProjectdata.getXnatProjectdatasById(newProjectS, user, false);
            String newLabel = null;
            		//getQueryVariable("label");
            if (newProject != null) {
                int index = 0;
                XnatExperimentdataShare matched = null;
                for (XnatExperimentdataShareI pp : experiment.getSharing_share()) {
                    if (pp.getProject().equals(newProject.getId())) {
                        matched = (XnatExperimentdataShare) pp;
                        if (newLabel != null && !pp.getLabel().equals(newLabel)) {
                            pp.setLabel(newLabel);
                            BaseXnatExperimentdata.SaveSharedProject((XnatExperimentdataShare) pp, experiment, user, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.RENAME_IN_SHARED_PROJECT));
                        }
                        break;
                    }
                    index++;
                }

                if (getQueryVariable("primary") != null && getQueryVariable("primary").equals("true")) {
                    changeExperimentPrimaryProject(experiment, project, newProject, newLabel, matched, index, user);
                    //return;
                } else {
                    if (matched == null) {
                        if (newLabel != null) {
                            XnatExperimentdata temp = XnatExperimentdata.GetExptByProjectIdentifier(newProject.getId(), newLabel, null, false);
                            if (temp != null) 
                            	throw new ResourceAlreadyExistsException("Label already in use:" , newLabel);
                        }
                        if (Permissions.canCreate(user, experiment.getXSIType() + "/project", newProject.getId())) {
                            shareExperimentToProject(user, newProject, experiment, newLabel);
                        } else 
                        	throw new InsufficientPrivilegesException("Specified user account has insufficient create privileges for experiments in the " + newProject.getId() + " project.");
                    } else
                    	throw new ResourceAlreadyExistsException("Already assigned to project:" , newProject.getId());
                }

            } else {
                //setGuestDataResponse("Unable to identify project: " + newProjectS);
                //return;
            }
        } else 
        	throw new DataFormatException("Please check experiment request");
    
		
	}

	@SuppressWarnings("unused")
	private XFTItem getXnatExperimentItem(UserI user, XnatProjectdata project, XnatExperimentdata experiment, String experimentId) throws XFTInitException, ElementNotFoundException, DataFormatException {
		 XFTItem item = experiment.getItem();
       
		 if (item == null) {
             String xsiType = experiment.getXSIType();
             		//getQueryVariable("xsiType");
             if (xsiType != null) 
            	 item = XFTItem.NewItem(xsiType, user);
         }

         if (item == null) {
             if (project != null) {
                 XnatSubjectassessordata om = (XnatSubjectassessordata) XnatSubjectassessordata.GetExptByProjectIdentifier(project.getId(), experimentId, user, false);
                 if (om != null) 
                	 item = om.getItem();
             }

             if (item == null) {
                 XnatSubjectassessordata om = (XnatSubjectassessordata) XnatSubjectassessordata.getXnatExperimentdatasById(experimentId, null, false);
                 if (om != null) 
                	 item = om.getItem();
             }
         }

         if (item == null)
        	 throw new DataFormatException("Need PUT Contents");

         if (!item.instanceOf("xnat:experimentData"))
        	 throw new DataFormatException("Only xnat:Subject documents can be PUT to this address.");

         return item;
	}

	private void anonymize(final XnatImagesessiondata session, final XnatImagesessiondata previous, XnatExperimentdata experiment) throws BaseXnatExperimentdata.UnknownPrimaryProjectException {
        if (StringUtils.isNotBlank(session.getSubjectId()) && !StringUtils.equalsIgnoreCase(session.getSubjectId(), previous.getSubjectId())) {
            try {
                // re-apply this project's edit script
                session.applyAnonymizationScript(new ProjectAnonymizer((XnatImagesessiondata) experiment, experiment.getProject(), session.getArchiveRootPath()));
            } catch (TransactionException e) {
                log.error("TransactionException", e.getMessage());
            }
        }
    }
	
	
	protected boolean completeDocument = false;

	private XnatExperimentdata getExistingExperiment(XnatExperimentdata currExp, UserI user) {
		XnatExperimentdata retExp = null;
		if (currExp.getId() != null) {
			retExp = XnatExperimentdata.getXnatExperimentdatasById(currExp.getId(), null, completeDocument);
		}

		if (retExp == null && currExp.getProject() != null && currExp.getLabel() != null) {
			retExp = XnatExperimentdata.GetExptByProjectIdentifier(currExp.getProject(), currExp.getLabel(), user,
					completeDocument);
		}

		if (retExp == null) {
			for (XnatExperimentdataShareI pp : currExp.getSharing_share()) {
				retExp = XnatExperimentdata.GetExptByProjectIdentifier(pp.getProject(), pp.getLabel(), user,
						completeDocument);
				if (retExp != null) {
					break;
				}
			}
		}
		return retExp;
	}

	 protected void shareExperimentToProject(final UserI user, final XnatProjectdata newProject, final XnatExperimentdata experiment, final String newLabel) throws Exception {
	        shareExperimentToProject(user, newProject, experiment, new XnatExperimentdataShare(user), newLabel);
	    }

	    protected void shareExperimentToProject(final UserI user, final XnatProjectdata newProject, final XnatExperimentdata experiment, final XnatExperimentdataShare shared, final String newLabel) throws Exception {
	        shareExperimentToProject(user, newProject, experiment, shared, newLabel, true);
	    }

	    protected void shareExperimentToProject(final UserI user, final XnatProjectdata newProject, final XnatExperimentdata experiment, final XnatExperimentdataShare shared, final String newLabel, boolean shareAllScans) throws Exception {
	        final String newProjectId = newProject.getId();

	        shared.setProject(newProjectId);
	        shared.setProperty("sharing_share_xnat_experimentda_id", experiment.getId());
	        if(StringUtils.isNotBlank(newLabel)) {
	            shared.setLabel(newLabel);
	        }
	        if (shareAllScans) {
	            if (experiment instanceof XnatImagesessiondata) {
	                for (XnatImagescandataI scan : ((XnatImagesessiondata) experiment).getScans_scan()) {
	                    shareScanToProject(user, newProject, (XnatImagescandata) scan);
	                }
	            }
	        }
	        BaseXnatExperimentdata.SaveSharedProject(shared, experiment, user, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING));
	        XDAT.triggerXftItemEvent(experiment, XftItemEvent.SHARE, ImmutableMap.<String, Object>of("target", newProjectId));
	    }
	    
	    private void setSubject(final XFTItem item, XnatExperimentdata experiment, XnatProjectdata project, XnatExperimentdata existing, UserI user, String subjectId2) throws Exception {
	        //MATCH SUBJECT
	        XnatSubjectdata subject;
	        try {
	        	if (item.instanceOf(XnatSubjectassessordata.SCHEMA_ELEMENT_NAME)) {
		            final XnatSubjectassessordata assessor = (XnatSubjectassessordata) experiment;

//		            if (StringUtils.isNotBlank(getQueryVariable("subject_ID"))) {
//		                assessor.setSubjectId(getQueryVariable("subject_ID"));
//		            }
		            if (StringUtils.isNotBlank(subjectId2)) {
		                assessor.setSubjectId(subjectId2);
		            }

		            if (StringUtils.isNotBlank(assessor.getSubjectId())) {
		                subject = getSubject(assessor, user);

		                if (subject == null && existing != null) {
		                    subject = ((XnatSubjectassessordata) existing).getSubjectData();
		                    if (subject != null) {
		                        assessor.setSubjectId(subject.getId());
		                    }
		                }

		                if (subject == null) {
		                    final String subjectId = XnatSubjectdata.CreateNewID();
		                    subject = new XnatSubjectdata(user);
		                    subject.setProject(project.getId());
		                    subject.setLabel(assessor.getSubjectId());
		                    subject.setId(subjectId);
		                    if (!Permissions.canCreate(user, subject)) 
		                    	throw new InsufficientPrivilegesException("Specified user account has insufficient create privileges for subjects in this project.");
		                 
		                    BaseXnatSubjectdata.save(subject, false, true, user, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.AUTO_CREATE_SUBJECT));
		                    assessor.setSubjectId(subject.getId());
		                }
		            }
		        }
	        }catch (Exception e) {
				throw new Exception("Error in set subject");
			}
	        
	    }
	    
	    private XnatSubjectdata getSubject(XnatSubjectassessordata assessor, UserI user) {
	        XnatSubjectdata subject = XnatSubjectdata.getXnatSubjectdatasById(assessor.getSubjectId(), user, false);
	        if (subject != null) {
	            return subject;
	        }

	        if (StringUtils.isNotBlank(assessor.getProject()) && StringUtils.isNotBlank(assessor.getLabel())) {
	            subject = XnatSubjectdata.GetSubjectByProjectIdentifier(assessor.getProject(), assessor.getSubjectId(), user, false);
	        }
	        if (subject != null) {
	            return subject;
	        }

	        for (final XnatExperimentdataShareI pp : assessor.getSharing_share()) {
	            subject = XnatSubjectdata.GetSubjectByProjectIdentifier(pp.getProject(), assessor.getSubjectId(), user, false);
	            if (subject != null) {
	                break;
	            }
	        }
	        return subject;
	    }
	    
	   
	    protected void shareScanToProject(final UserI user, final XnatProjectdata newProject, final XnatImagescandata scan)
	            throws Exception {
	        XnatImagescandataShare shared = new XnatImagescandataShare(user);
	        final String newProjectId = newProject.getId();

	        shared.setProject(newProjectId);
	        shared.setProperty("sharing_share_xnat_imagescandat_xnat_imagescandata_id", scan.getXnatImagescandataId());
	        shared.setLabel(scan.getId());
	        BaseXnatImagescandata.SaveSharedProject(shared, scan, user, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING));
	        XDAT.triggerXftItemEvent(scan, XftItemEvent.SHARE, ImmutableMap.<String, Object>of("target", newProjectId));
	    }
	

	
	
	protected void changeExperimentPrimaryProject(final XnatExperimentdata experiment, final XnatProjectdata source, final XnatProjectdata destination, final String newLabel, final XnatExperimentdataShare share, final int index, UserI user) throws Exception {
		if (!Permissions.canDelete(user, experiment)) 
			throw new InsufficientPrivilegesException("Specified user account has insufficient privileges for experiments in this project.");

		if (experiment.getProject().equals(destination.getId())) 
			throw new ResourceAlreadyExistsException("Already assigned to project: " , destination.getId());

		final String workingLabel = StringUtils.defaultIfBlank(newLabel, StringUtils.defaultIfBlank(experiment.getLabel(), experiment.getId()));

		final XnatExperimentdata match = XnatExperimentdata.GetExptByProjectIdentifier(destination.getId(), workingLabel, user, false);

		if (match != null) 
			throw new ResourceAlreadyExistsException("Specified label is already in use.", match.getLabel());

		final List<String> assessorList = StringUtils.isNotBlank(getQueryVariable("moveAssessors")) ? Arrays.asList(getQueryVariable("moveAssessors").split(",")) : null;

		final EventMetaI meta = BaseXnatExperimentdata.ChangePrimaryProject(user, experiment, destination, workingLabel, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.MODIFY_PROJECT), assessorList);
		XDAT.triggerXftItemEvent(experiment, XftItemEvent.MOVE, ImmutableMap.<String, Object>of("origin", source.getId(), "target", destination.getId()));

		if (share != null) {
			SaveItemHelper.authorizedRemoveChild(experiment.getItem(), "xnat:experimentData/sharing/share", share.getItem(), user, meta);
			experiment.removeSharing_share(index);
		}
    }
	

	private boolean allowDataDeletion() {
		return false;
	}

	private String getQueryVariable(String string) {
		return null;
	}

	@Override
	public void deleteById(UserI user, String experimentId, String projectId) throws DataFormatException, NotFoundException {
		delete(user, findById(user, experimentId), projectId);
	}

	@SuppressWarnings("unused")
	@Override
	public void delete(UserI user, XnatExperimentdata experiment, String projectId) throws DataFormatException, NotFoundException  {
		 if(Objects.isNull(experiment))
			 throw new NotFoundException("The experiment not found");
		
		if (StringUtils.isNotBlank(projectId) && !StringUtils.equals(experiment.getProject(), projectId)) 
			 throw new DataFormatException("You specified the project " + projectId + " in your request but the experiment is assigned to project " + experiment.getProject() + ". These values must be the same.");
	        
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(experiment.getProject(), user, false);
		 if (experiment == null && experiment.getId() != null) {
	            experiment = XnatExperimentdata.getXnatExperimentdatasById(experiment.getId(), user, false);

	            if (experiment == null && project != null) {
	                experiment = XnatExperimentdata.GetExptByProjectIdentifier(project.getId(), experiment.getId(), user, false);
	            }
	        }
		 
	        deleteItem(user, project, experiment);
		
	}
	 protected void deleteItem(UserI user, final XnatProjectdata proj, final BaseElement item) {
	        if (!ArchivableItem.class.isAssignableFrom(item.getClass())) {
	            throw new IllegalArgumentException("The BaseElement item must also implement the ArchivableItem interface, but the class " + item.getClass().getName() + " doesn't.");
	        }

	        try {
	          XnatProjectUtil xnatProjectUtil = new XnatProjectUtil();
	            final XnatProjectdata     newProject = xnatProjectUtil.getProjectFromFilePath(proj, (ArchivableItem) item, user);
	            final PersistentWorkflowI wrk        = WorkflowUtils.buildOpenWorkflow(user, item.getItem(), newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getDeleteAction(item.getXSIType())));
	            final EventMetaI          c          = wrk.buildEvent();

	            try {
	                final boolean                      removeFiles = isQueryVariableTrue("removeFiles");
	                final XnatProjectdata              project     = (newProject != null) ? newProject : proj;
	                final Class<? extends BaseElement> itemType    = item.getClass();

	                final String message;
	                if (XnatPvisitdata.class.isAssignableFrom(itemType)) {
	                    message = ((XnatPvisitdata) item).delete(project, user, removeFiles, c);
	                } else if (XnatImagesessiondata.class.isAssignableFrom(itemType)) {
	                    message = ((XnatImagesessiondata) item).delete(project, user, removeFiles, c);
	                } else if (XnatSubjectdata.class.isAssignableFrom(itemType)) {
	                    message = ((XnatSubjectdata) item).delete(project, user, removeFiles, c);
	                } else if (XnatExperimentdata.class.isAssignableFrom(itemType)) {
	                    message = ((XnatExperimentdata) item).delete(project, user, removeFiles, c);
	                } else {
	                    message = null;
	                }
	                if (message != null) {
	                    WorkflowUtils.fail(wrk, c);
	                    throw new InsufficientPrivilegesException("You don't have permission to delete", message);
	                } else {
	                    XDAT.triggerXftItemEvent(item, DELETE, ImmutableMap.of("target", project.getId()));
	                    WorkflowUtils.complete(wrk, c);
	                }
	            } catch (Exception e) {
	                try {
	                    WorkflowUtils.fail(wrk, c);
	                } catch (Exception e1) {
	                    log.error("", e1);
	                }
	                log.error("", e);
	            }
	        } catch (PersistentWorkflowUtils.EventRequirementAbsent e) {
	            log.error("Forbidden: " + e.getMessage(), e);
	        } catch (NotFoundException e) {
	        	log.error("Not Found: " + e.getMessage(), e);
	        } catch (IllegalArgumentException e) {
	        	log.error("Bad Request Found: " + e.getMessage(), e);
	        }
	    }

	
	
	
	private EventDetails newEventInstance(EventUtils.CATEGORY cat, String deleteAction) {
		 return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : "", getReason(), getComment());
	}

	private String getComment() {
		return null;
	}

	private String getReason() {
		return null;
	}

	private String getAction() {
		return null;
	}

	private TYPE getEventType() {
		final String id = null;
				//getQueryVariable(EventUtils.EVENT_TYPE);
        if (id != null) {
            return EventUtils.getType(id, EventUtils.TYPE.WEB_SERVICE);
        } else {
            return EventUtils.TYPE.WEB_SERVICE;
        }
	}

	private boolean isQueryVariableTrue(String string) {
		return false;
	}

	@Override
	public List<XnatExperimentdata> findByProjectAndLabel(UserI user, String projectId, String label) {
		return null;
	}
	
	
	private static class ExperimentRowMapper implements RowMapper<XnatExperimentdata> {
	    ExperimentRowMapper(final UserI user) {
	        _user = user;
	    }
	    @Override
	    public XnatExperimentdata mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	        final String experimentId = resultSet.getString("id");
	        XnatExperimentdata xnatExperimentdata= XnatExperimentdata.getXnatExperimentdatasById(experimentId, _user, false);
	        return xnatExperimentdata;
	    }
	    private final UserI _user;
	}
	
	
	
	private static final String PROJECT_AND_EXPERIMENT_QUERY= "SELECT ed.id FROM xnat_experimentdata ed LEFT JOIN xnat_projectdata pd ON ed.project = pd.id ";
																
	private static final String BY_PRO_EXP_ID_WHERE = " WHERE ed.id= :experimentId AND pd.id = :projectId";
	
	private  final String PROJECT_EXPERIMENT_QUERY = EXPERIMENT_SUB_QUERY1 + BY_PRO_ID_WHERE1  +  EXPERIMENT_SUB_QUERY2 +  BY_PRO_ID_WHERE2 + EXPERIMENT_SUB_QUERY3;
	
	private  final String PROJECT_SUBJECT_EXPERIMENT_QUERY = EXPERIMENT_SUB_QUERY1 + BY_PRO_SUB_ID_WHERE  +  EXPERIMENT_SUB_QUERY2 +  BY_PRO_SUB_ID_WHERE2 + EXPERIMENT_SUB_QUERY3;
	
	
	private static final String BY_PRO_SUB_ID_WHERE ="    SECURITY WHERE ((((xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" + 
			" ((xnat_subjectAssessorData1= :subjectId))) AND (( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" + 
			" ((xnat_subjectAssessorData1= :subjectId)))))";
	
	private static final String BY_PRO_SUB_ID_WHERE2 = "    SECURITY WHERE ((((xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" + 
			" ((xnat_subjectAssessorData1= :subjectId))) AND (( (xnat_experimentData14=:projectId) OR  (xnat_experimentData_share25= :projectId)) AND   \n" + 
			" ((xnat_subjectAssessorData1= :subjectId)))))";

	
	private static final String BY_PRO_ID_WHERE1 ="   SECURITY WHERE ((( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))) AND \n" + 
			" (( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))))) ";
	
	
	private static final String BY_PRO_ID_WHERE2 ="    SECURITY WHERE ((( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))) AND \n" + 
			" (( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))))) ";
	
	
	
	private static final String EXPERIMENT_SUB_QUERY1 =" SELECT table0.id AS id, xnat_experimentData.xnatSubjectAssessorDataId AS xnatSubjectAssessorDataId, \n" + 
			"xnat_experimentData.project AS project, xnat_experimentData.date AS date, xnat_experimentData.xsiType AS xsiType, \n" + 
			"xnat_experimentData.label AS label,xnat_experimentData.insertDate AS insertDate \n" + 
			"FROM (SELECT SEARCH.* FROM (SELECT DISTINCT ON (id) * FROM (SELECT table0.id AS id, table0.project AS xnat_experimentData14, \n" + 
			"table2.project AS xnat_experimentData_share25, xnat_subjectAssessorData.subject_id AS \n" + 
			"xnat_subjectAssessorData1 FROM xnat_subjectAssessorData xnat_subjectAssessorData   \n" + 
			"LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id   \n" + 
			"LEFT JOIN xnat_experimentData_share table2 ON table0.id=table2.sharing_share_xnat_experimentDa_id)  ";
	
	private static final String EXPERIMENT_SUB_QUERY2 = " SECURITY LEFT JOIN xnat_subjectAssessorData SEARCH ON SECURITY.id=SEARCH.id) xnat_subjectAssessorData   \n" + 
			" LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id \n" + 
			" LEFT JOIN (SELECT table0.id AS id FROM (SELECT SEARCH.* FROM (SELECT DISTINCT ON (id) * FROM (SELECT table0.id AS id, table0.project AS xnat_experimentData14, table2.project AS xnat_experimentData_share25, xnat_subjectAssessorData.subject_id AS xnat_subjectAssessorData1 FROM xnat_subjectAssessorData xnat_subjectAssessorData   \n" + 
			" LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id   LEFT JOIN xnat_experimentData_share table2 ON table0.id=table2.sharing_share_xnat_experimentDa_id)    " ;
	
	private static final String EXPERIMENT_SUB_QUERY3 = "  SECURITY LEFT JOIN xnat_subjectAssessorData SEARCH ON SECURITY.id=SEARCH.id) xnat_subjectAssessorData   \n" + 
			" LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id) AS map_xnat_experimentData ON table0.id=map_xnat_experimentData.id \n" + 
			" LEFT JOIN (SELECT xnat_experimentData.id AS xnatSubjectAssessorDataId, xnat_experimentData.project AS project, xnat_experimentData.date AS date, table1.element_name AS xsiType, xnat_experimentData.label AS label, table2.insert_date AS insertDate   \n" + 
			" FROM xnat_experimentData xnat_experimentData   \n" + 
			" LEFT JOIN xdat_meta_element table1 ON xnat_experimentData.extension=table1.xdat_meta_element_id   \n" + 
			" LEFT JOIN xnat_experimentData_meta_data table2 ON xnat_experimentData.experimentData_info=table2.meta_data_id) AS xnat_experimentData ON map_xnat_experimentData.id=xnat_experimentData.xnatSubjectAssessorDataId";
	

	private final NamedParameterJdbcTemplate _template;


}
