package org.nrg.xnat.export.transporter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatExperimentdataI;
import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatImagesessiondataI;
import org.nrg.xdat.model.XnatMrscandataI;
import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatMrscandata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.om.base.auto.AutoXnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.exception.FailedToExportException;
import org.nrg.xnat.export.manifest.ExportManifest;
import org.nrg.xnat.export.manifest.TransportManifest;
import org.nrg.xnat.export.model.endpoint.EndpointDefinition;
import org.nrg.xnat.export.model.endpoint.EndpointExportedData;
import org.nrg.xnat.export.model.endpoint.EndpointIdXsiType;
import org.nrg.xnat.export.model.endpoint.EndpointImagingSessionScanType;
import org.nrg.xnat.export.model.endpoint.EndpointImagingSessionXsiType;
import org.nrg.xnat.export.model.endpoint.EndpointItems;
import org.nrg.xnat.export.resource.selector.ImagingSessionResourceSelector;
import org.nrg.xnat.export.resource.selector.ProjectResourceSelector;
import org.nrg.xnat.export.resource.selector.SubjectAssessorResourceSelector;
import org.nrg.xnat.export.resource.selector.SubjectResourceSelector;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
public class ProjectTransportBuilder {
	
	final ExportManifest exportManifest;
	final UserI user;
	
	public ProjectTransportBuilder(final ExportManifest exportManifest, final UserI user) {
	  	this.exportManifest = exportManifest;
	  	this.user = user;
	}
	
	public TransportManifest constructTransportManifest() throws FailedToExportException { 
		  //For the Project,
		  //Identify the DICOM files
		  //Run the Mapping Service
		  //Run the Anonymizer Service
		  //Stage the files in cache
		  //Queue the export task
			TransportManifest transportManifest = null;
			EndpointDefinition endpointDefinition = exportManifest.getEndpointDefinition();
			if (null == endpointDefinition) {
				return null;
			}
		    
			try {
			   String projectId = exportManifest.getProjectId(); 
			   final XnatProjectdata project=AutoXnatProjectdata.getXnatProjectdatasById(projectId, user, true);
			   if (project == null) {
					log.error("Invalid project passed to export " + projectId);
			        throw new FailedToExportException("Project could not be found " + projectId,new IllegalArgumentException());
			   }
				XnatProjectdata projectDataToBeTransported = new XnatProjectdata();
				projectDataToBeTransported.setId(projectId);

				Hashtable<String, XnatSubjectdata> subjectsTobeIncluded = new Hashtable<String, XnatSubjectdata>();

				EndpointExportedData exportedData = endpointDefinition.getExportedData();
				boolean exportAll = false;
				if (null == exportedData) {
					//All data has to be transported
					exportAll = true;
					addAllResources(project, projectDataToBeTransported);
				}

			   ArrayList<XnatSubjectdata> subjects = new ArrayList<XnatSubjectdata>();
			   //Get any files at the subject resource level
			   if (exportAll) {
			       subjects = project.getParticipants_participant();
			   }else {
				   try {
					   boolean exportAllSubjects = exportedData.exportAllSubjects();
					   if (exportAllSubjects) { 
					       subjects = project.getParticipants_participant();
					   }else {
						   List<String> subjectIds = exportedData.getSubjectResources().getIds();
						   for (String subId : subjectIds) {
					           XnatSubjectdata subject = XnatSubjectdata.GetSubjectByProjectIdentifier(projectId, subId, user,false);
						       if (subject == null) {
						    	   subject = XnatSubjectdata.getXnatSubjectdatasById(subId, user, false);
						       }
						       if (null != subject)
					              subjects.add(subject);
						   }
					   }
				   }catch(NullPointerException npe ) {
					   log.debug("EndpointDefinition does not have a subject_resources subfield");
				   }
			   }
			   if (null != subjects && subjects.size() > 0) {
					for (final XnatSubjectdata sub: subjects) {
						   SubjectResourceSelector subjectResourceSelector = new SubjectResourceSelector(exportManifest);
						   List<XnatAbstractresourceI> subRsc = subjectResourceSelector.getSelectedResources(sub);
						   if (null != subRsc && subRsc.size() > 0) {
							   XnatSubjectdata transportedSubject = new XnatSubjectdata();
							   transportedSubject.setProject(projectDataToBeTransported.getId());
							   transportedSubject.setId(sub.getId());
							   transportedSubject.setLabel(sub.getLabel());
							   for (XnatAbstractresourceI a: subRsc)
							      transportedSubject.addResources_resource(a);
							   subjectsTobeIncluded.put(transportedSubject.getId(), transportedSubject);
						   }
					}
			   }
			   //Now look at all Subject Assessors which are to be included
			    
			   Hashtable<String, List<XnatSubjectassessordataI>> experimentByXsiTypeHash = new Hashtable<String, List<XnatSubjectassessordataI>>();
			   Hashtable<String, String> experimentAccountedFor = new Hashtable<String,String>();
			   //Get any files at the subject assessor resource level
			   try {
				   List<EndpointIdXsiType> subjectAssessorsByXsiType = exportedData.getSubjectAssessors().getXsiTypes();
				   for (EndpointIdXsiType x : subjectAssessorsByXsiType) {
					   String xsiType = x.getXsiType();
					   EndpointItems resources = x.getResources();
					   
					   List<String> ids = x.getIds();
			           if (null != ids && ids.size() > 0) {
			        	  //Only these subject assessors are to be exported which have the given resources
			        	   for (String id: ids) {
			        		   XnatSubjectassessordata transportedSubjectAssessor = getSubjectAssessorToExportById(projectId, id,projectDataToBeTransported.getId(),user);
							   if (null != transportedSubjectAssessor) {
				        		   if (subjectsTobeIncluded.containsKey(transportedSubjectAssessor.getSubjectId())) {
									   subjectsTobeIncluded.get(transportedSubjectAssessor.getSubjectId()).addExperiments_experiment(transportedSubjectAssessor);
								   }else {
							           XnatSubjectdata sub = XnatSubjectdata.getXnatSubjectdatasById(transportedSubjectAssessor.getSubjectId(), user, false);
							           XnatSubjectdata transportedSubject = new XnatSubjectdata();
									   transportedSubject.setProject(projectDataToBeTransported.getId());
									   transportedSubject.setId(sub.getId());
									   transportedSubject.setLabel(sub.getLabel());
									   transportedSubject.addExperiments_experiment(transportedSubjectAssessor);
									   subjectsTobeIncluded.put(transportedSubject.getId(), transportedSubject);
								   }
				        		   experimentAccountedFor.put(transportedSubjectAssessor.getId(), "");
							   }
			        	   }
			           }else {
			        	   //We will handle the SubjectAssessor of a given type along with 
			        	   //The ImagingSessions
			        	   experimentByXsiTypeHash.put(xsiType,new ArrayList<XnatSubjectassessordataI>());
			           }
				   }
			   }catch(NullPointerException npe) {
				   
			   }

			   //Look for imaging sessions
			   //Filter the resources
			   //Filter the scans - by type and ids and resources
			   //Filter the imageAssessors
			   try {
				   List<EndpointImagingSessionXsiType> imagingSessionsByXsiType = exportedData.getImagingSessions().getXsiTypes();
				   for (EndpointImagingSessionXsiType x : imagingSessionsByXsiType) {
					   String xsiType = x.getXsiType();
					   EndpointItems resources = x.getResources();
					   List<String> ids = x.getIds();
			           if (null != ids && ids.size() > 0) {
			        	  //Only these subject assessors are to be exported which have the given resources
			        	   for (String id: ids) {
			        		   XnatImagesessiondataI transportedSubjectAssessor = getImagingSessionToExportById(projectId, id,projectDataToBeTransported.getId(),user);
			        		   if (null != transportedSubjectAssessor) {
				        		   if (subjectsTobeIncluded.containsKey(transportedSubjectAssessor.getSubjectId())) {
									   subjectsTobeIncluded.get(transportedSubjectAssessor.getSubjectId()).addExperiments_experiment(transportedSubjectAssessor);
								   }else {
							           XnatSubjectdata sub = XnatSubjectdata.getXnatSubjectdatasById(transportedSubjectAssessor.getSubjectId(), user, false);
							           XnatSubjectdata transportedSubject = new XnatSubjectdata();
									   transportedSubject.setProject(projectDataToBeTransported.getId());
									   transportedSubject.setId(sub.getId());
									   transportedSubject.setLabel(sub.getLabel());
									   transportedSubject.addExperiments_experiment(transportedSubjectAssessor);
									   subjectsTobeIncluded.put(transportedSubject.getId(), transportedSubject);
								   }
				        		   experimentAccountedFor.put(transportedSubjectAssessor.getId(), "");
				        		   List<XnatImageassessordataI> accountedAssessor = transportedSubjectAssessor.getAssessors_assessor();
				        		   if (accountedAssessor != null && accountedAssessor.size() > 0) {
					        		   for (XnatImageassessordataI a : accountedAssessor) {
						        		   experimentAccountedFor.put(a.getId(), "");
					        		   }
				        		   }
			        		   }
			        	   }
			           }else {
			        	   //We will handle the SubjectAssessor of a given type along with 
			        	   //The ImagingSessions
			        	   experimentByXsiTypeHash.put(xsiType,new ArrayList<XnatSubjectassessordataI>());
			           }
				   }
			   }catch(NullPointerException npe) {}
			   
			   //Now handle each XSI Type which did not have any ids specified
			   Set<String> xsiTypesSet = experimentByXsiTypeHash.keySet();
			   Iterator<String> xsiTypes = xsiTypesSet.iterator();
			    while (xsiTypes.hasNext()) { 
			    	String xsiType = xsiTypes.next();
			    	//Find all experiments in this project of this xsiType
			    	ArrayList experiments = null;
			    	if (xsiType.equals(XnatImagesessiondata.SCHEMA_ELEMENT_NAME)) {
			    		experiments = new ArrayList();
			    		ArrayList<XnatExperimentdata> imageSessionExperiments = project.getExperiments();
			    		if (imageSessionExperiments != null && imageSessionExperiments.size() > 0) {
				    		for (XnatExperimentdata e : imageSessionExperiments ) {
				    			if (e instanceof XnatImagesessiondata) {
				    				experiments.add((XnatExperimentdataI)e);
				    			}
				    		}
			    		}
			    	}else {
			    		experiments = project.getExperimentsByXSIType(xsiType);
			    	}
			    	if (experiments != null && experiments.size() > 0) {
			    		for (int i=0; i< experiments.size(); i++) {
			    			XnatExperimentdataI exp = (XnatExperimentdataI)experiments.get(i);
			    			if (!experimentAccountedFor.containsKey(exp.getId())) {
			    				if (exp instanceof XnatImagesessiondata) {
			    					XnatImagesessiondataI exportedImg = buildImageSessionToExport(exp, projectDataToBeTransported.getId());
					        		if (exportedImg == null) continue;
			    				    if (subjectsTobeIncluded.containsKey(exportedImg.getSubjectId())) {
									   subjectsTobeIncluded.get(exportedImg.getSubjectId()).addExperiments_experiment(exportedImg);
								    }else {
							           XnatSubjectdata sub = XnatSubjectdata.getXnatSubjectdatasById(exportedImg.getSubjectId(), user, false);
							           XnatSubjectdata transportedSubject = new XnatSubjectdata();
									   transportedSubject.setProject(projectDataToBeTransported.getId());
									   transportedSubject.setId(sub.getId());
									   transportedSubject.setLabel(sub.getLabel());
									   transportedSubject.addExperiments_experiment(exportedImg);
									   subjectsTobeIncluded.put(transportedSubject.getId(), transportedSubject);
								    }
			    				}else if (exp instanceof XnatSubjectassessordata) {
				    				   XnatSubjectassessordata exportedExp = buildSubjectAssessorToExport(exp,projectDataToBeTransported.getId());
					        		   if (exportedExp == null) continue;
				    				   if (subjectsTobeIncluded.containsKey(exportedExp.getSubjectId())) {
										   subjectsTobeIncluded.get(exportedExp.getSubjectId()).addExperiments_experiment(exportedExp);
									   }else {
								           XnatSubjectdata sub = XnatSubjectdata.getXnatSubjectdatasById(exportedExp.getSubjectId(), user, false);
								           XnatSubjectdata transportedSubject = new XnatSubjectdata();
										   transportedSubject.setProject(projectDataToBeTransported.getId());
										   transportedSubject.setId(sub.getId());
										   transportedSubject.setLabel(sub.getLabel());
										   transportedSubject.addExperiments_experiment(exportedExp);
										   subjectsTobeIncluded.put(transportedSubject.getId(), transportedSubject);
									   }
			    				}
			    			}
			    		}
			    	}
			    	
			    } 
			    List<XnatSubjectdata> subjectsToBeExported = new ArrayList<XnatSubjectdata>();
			    Set<String> subjectIdSet = subjectsTobeIncluded.keySet();
			    Iterator<String> subjectIds = subjectIdSet.iterator();
			    while (subjectIds.hasNext()) { 
			    	String subjectId = subjectIds.next();
			    	subjectsToBeExported.add(subjectsTobeIncluded.get(subjectId));
			    }
			    transportManifest = new TransportManifest(projectDataToBeTransported, subjectsToBeExported, exportManifest); 
			    transportManifest.updateEstimates();
			    transportManifest.setAuthorizedBy(user);
		   }catch(Exception e) {}
		   return transportManifest;
		}
		
 	   private XnatSubjectassessordata getSubjectAssessorToExportById(String inProjectId, String expid, String outProjectid, UserI user) throws Exception {
		   XnatExperimentdata exp =  XnatSubjectassessordata.GetExptByProjectIdentifier(inProjectId, expid, user, false);
		   if (exp == null) {
			   exp = XnatExperimentdata.getXnatExperimentdatasById(expid, user, false);
		   }
		   return buildSubjectAssessorToExport(exp, outProjectid);
		}
	
	

		private XnatSubjectassessordata buildSubjectAssessorToExport(XnatExperimentdataI exp, String outProjectid) throws Exception {
		   XnatSubjectassessordata transportedSubjectAssessor = null;
		   if (null != exp && exp instanceof XnatSubjectassessordataI) {
			   SubjectAssessorResourceSelector subjectAssessorResourceSelector = new SubjectAssessorResourceSelector(exportManifest);
			   List<XnatAbstractresourceI> subRsc = subjectAssessorResourceSelector.getSelectedResources((XnatSubjectassessordataI)exp);
			   if (null != subRsc && subRsc.size() > 0) {
				   transportedSubjectAssessor = new XnatSubjectassessordata();
				   transportedSubjectAssessor.setProject(outProjectid);
				   transportedSubjectAssessor.setId(exp.getId());
				   transportedSubjectAssessor.setLabel(exp.getLabel());
				   transportedSubjectAssessor.setSubjectId(((XnatSubjectassessordataI)exp).getSubjectId());
				   transportedSubjectAssessor.getItem().setXmlType(((XnatSubjectassessordataI)exp).getXSIType());
				   for (XnatAbstractresourceI a: subRsc)
				      transportedSubjectAssessor.addResources_resource(a);
			   }
		   }
		   return transportedSubjectAssessor;
		}

		private XnatImagesessiondataI getImagingSessionToExportById(String inProjectId, String expid, String outProjectid, UserI user) throws Exception  {
			   XnatExperimentdata exp =  XnatSubjectassessordata.GetExptByProjectIdentifier(inProjectId, expid, user, false);
			   if (exp == null) {
				   exp = XnatExperimentdata.getXnatExperimentdatasById(expid, user, false);
			   }
			   return buildImageSessionToExport(exp, outProjectid);
		}		
		
		private XnatImagesessiondataI buildImageSessionToExport(XnatExperimentdataI exp, String outProjectid) throws Exception  {
			   XnatImagesessiondata transportedSubjectAssessor = null;
			   if (null != exp && exp instanceof XnatImagesessiondataI) {
				   XnatImagesessiondataI imageSession = (XnatImagesessiondata)exp;
				   transportedSubjectAssessor = new XnatImagesessiondata();
				   transportedSubjectAssessor.setProject(outProjectid);
				   transportedSubjectAssessor.setId(imageSession.getId());
				   transportedSubjectAssessor.setLabel(imageSession.getLabel());
				   transportedSubjectAssessor.setSubjectId(((XnatImagesessiondataI)exp).getSubjectId());
				   transportedSubjectAssessor.getItem().setXmlType(imageSession.getXSIType());
				   ImagingSessionResourceSelector imagingSessionResourceSelector = new ImagingSessionResourceSelector(exportManifest);
				   try {
					   List<XnatAbstractresourceI> subRsc = imagingSessionResourceSelector.getSelectedResources(imageSession);
					   if (null != subRsc && subRsc.size() > 0) {
						 for (XnatAbstractresourceI a: subRsc)
						      transportedSubjectAssessor.addResources_resource(a);
					   }
				   }catch(NullPointerException npe) {}
				   //Handle Scans
				   List<XnatImagescandataI> scans = imageSession.getScans_scan();
				   for (XnatImagescandataI s: scans) {
					   boolean include = imagingSessionResourceSelector.isIncludedByIdAndType(imageSession, s);
					   if (include) {
							   XnatImagescandataI mrScan = new XnatImagescandata();
							   mrScan.setId(s.getId());
							   mrScan.setType(s.getType());
							   mrScan.setImageSessionId(transportedSubjectAssessor.getId());
							   List<XnatAbstractresourceI> rscs = imagingSessionResourceSelector.getSelectedScanResources(imageSession, s); 
							   if (null != rscs && rscs.size() > 0) {
								   for (XnatAbstractresourceI a : rscs) {
									   mrScan.addFile(a);
								   }
								   transportedSubjectAssessor.addScans_scan(mrScan);
							   }
					   }else {
						   //If a scantype filter was specified, skip this
							boolean hasScanType = imagingSessionResourceSelector.isScanTypeIncluded(imageSession);

						   if(hasScanType) {
							   continue;
						   }
						   //Does the scan have the exported scan resource?
						   List<XnatAbstractresourceI> rscs = imagingSessionResourceSelector.getSelectedScanResources(imageSession, s);
						   if (null != rscs && rscs.size() > 0) {
							   XnatImagescandataI mrScan = new XnatImagescandata();
							   mrScan.setId(s.getId());
							   mrScan.setType(mrScan.getType());
							   mrScan.setImageSessionId(transportedSubjectAssessor.getId());
							   for (XnatAbstractresourceI a : rscs) {
								   mrScan.addFile(a);
							   }
							   transportedSubjectAssessor.addScans_scan(mrScan);
						   }
					   }
				   }
				   //Handle Image Assessors
				   List<XnatImageassessordataI> assessors = imageSession.getAssessors_assessor();
				   for (XnatImageassessordataI s: assessors) {
					   boolean include = imagingSessionResourceSelector.isIncludedById(imageSession, s);
					   if (include) {
						   if (s instanceof XnatImageassessordataI) {
							   XnatImageassessordataI mrAssessor = new XnatImageassessordata();
							   mrAssessor.setId(s.getId());
							   mrAssessor.setProject(s.getProject());
							   mrAssessor.setImagesessionId(transportedSubjectAssessor.getId());
							   List<XnatAbstractresourceI> rscs = imagingSessionResourceSelector.getSelectedAssessorResources(imageSession, s); 
							   if (null != rscs && rscs.size() > 0) {
								   for (XnatAbstractresourceI a : rscs) {
									   mrAssessor.addResources_resource(a);
								   }
							   }
							   transportedSubjectAssessor.addAssessors_assessor(mrAssessor);
						   }
					   }else {
						 //TODO  
					   }
				   }

			   }
			   return transportedSubjectAssessor;
			}

		private void addAllResources(XnatProjectdata inProject, XnatProjectdata outProject) throws Exception  {
			List<XnatAbstractresourceI> projRscs =  inProject.getResources_resource();
			if (projRscs != null && projRscs.size() >0) {
				   for (XnatAbstractresourceI a: projRscs)
					   outProject.addResources_resource(a);
			}
		}
		
		private void addExportDefinitionProjectResources(XnatProjectdata inProject, XnatProjectdata outProject) throws Exception {
			   //Get any relavant files at the project resource level.
			   ProjectResourceSelector projectResourceSelector = new ProjectResourceSelector(exportManifest);
			   List<XnatAbstractresourceI> projRsc = projectResourceSelector.getSelectedResources(inProject);
			   if (null != projRsc && projRsc.size() > 0) {
				   for (XnatAbstractresourceI a: projRsc)
					   outProject.addResources_resource(a);
			   }

		}

}
