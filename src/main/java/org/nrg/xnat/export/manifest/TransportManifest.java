package org.nrg.xnat.export.manifest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatImagesessiondataI;
import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.om.base.BaseXnatResourcecatalog;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.manifest.serializer.TransportManifestSerializer;
import org.nrg.xnat.export.transformers.TransformerHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Mohana Ramaratnam
 *
 */
/*
 * This class represents all the entities in a XNAT Project 
 * which will be transported over to an export endpoint.
 * 
 * The request which resulted in this transport selection is represented
 * in the exportManifest
 */
@JsonSerialize(using = TransportManifestSerializer.class)
public class TransportManifest implements Serializable {
	
	private static final long serialVersionUID = -4427984163407110830L;

	@JsonIgnore
	 ExportManifest exportManifest;
     
	 Date exportStartDate;
     Date exportComplete;
 	 
     String exportEventId;
     
	 @JsonIgnore
     UserI authorizedBy;

 	 XnatProjectdata project;
 	 
 	 
 	 List<XnatSubjectdata> subjects;
 	 
 	 TransformerHelper transformerHelper;

 	 long estimatedFileCountToBeExported = 0;
 	 long estimatedAmountofDataToBeExported = 0;
 
 	 
 	public TransportManifest(XnatProjectdata project, List<XnatSubjectdata> subjects, ExportManifest exportManifest) {
 		this.project = project;
 		this.subjects = subjects;
 		this.exportManifest = exportManifest;
 	}
	
	/**
	 * @return the exportManifest
	 */
 	public ExportManifest getExportManifest() {
		return exportManifest;
	}
	
	/**
	 * @param exportManifest the exportManifest to set
	 */
	public void setExportManifest(ExportManifest exportManifest) {
		this.exportManifest = exportManifest;
	}

	
	
	public void updateEstimates() {
		//Look into every resource, add file counts from catalog and file size from catalog
		updateEstimates(project.getResources_resource());
		for (XnatSubjectdata s : subjects) {
			List<XnatAbstractresourceI> rscs = s.getResources_resource();
			updateEstimates(rscs);
			List<XnatSubjectassessordataI> exps = s.getExperiments_experiment();
			for (XnatSubjectassessordataI e : exps) {
				updateEstimates(e.getResources_resource());
				if (e instanceof XnatImagesessiondataI) {
					List<XnatImagescandataI> scans = ((XnatImagesessiondataI)e).getScans_scan();
					List<XnatImageassessordataI> imgAssessors = ((XnatImagesessiondataI)e).getAssessors_assessor();
					for (XnatImagescandataI sc : scans ) {
						updateEstimates(sc.getFile());
					}
					for (XnatImageassessordataI ass : imgAssessors) {
						updateEstimates(ass.getResources_resource());
					}
					
				}
			}
		}
		
	}
	
	private void updateEstimates(List<XnatAbstractresourceI> rscs) {
		if (rscs == null || rscs.size() <1) {
			return;
		}
		for (XnatAbstractresourceI rsc : rscs) {
			if (rsc instanceof XnatResourcecatalog) {
				estimatedFileCountToBeExported += ((BaseXnatResourcecatalog)rsc).getFileCount();
				estimatedFileCountToBeExported += Long.parseLong(((BaseXnatResourcecatalog)rsc).getFileSize().toString());
			}
		}
		
	}

	/**
	 * @return the exportStartDate
	 */
	public Date getExportStartDate() {
		return exportStartDate;
	}

	/**
	 * @param exportStartDate the exportStartDate to set
	 */
	public void setExportStartDate(Date exportStartDate) {
		this.exportStartDate = exportStartDate;
	}

	/**
	 * @return the exportComplete
	 */
	public Date getExportComplete() {
		return exportComplete;
	}

	/**
	 * @param exportComplete the exportComplete to set
	 */
	public void setExportComplete(Date exportComplete) {
		this.exportComplete = exportComplete;
	}

	/**
	 * @return the authorizedBy
	 */
	public UserI getAuthorizedBy() {
		return authorizedBy;
	}

	/**
	 * @param authorizedBy the authorizedBy to set
	 */
	public void setAuthorizedBy(UserI authorizedBy) {
		this.authorizedBy = authorizedBy;
	}

	/**
	 * @return the project
	 */
	public XnatProjectdata getProject() {
		return project;
	}


	/**
	 * @return the subjects
	 */
	public List<XnatSubjectdata> getSubjects() {
		return subjects;
	}

	/**
	 * @return the estimatedFileCountToBeExported
	 */
	public long getEstimatedFileCountToBeExported() {
		return estimatedFileCountToBeExported;
	}

	/**
	 * @return the estimatedAmountofDataToBeExported
	 */
	public long getEstimatedAmountofDataToBeExported() {
		return estimatedAmountofDataToBeExported;
	}

	/**
	 * @return the exportEventId
	 */
	public String getExportEventId() {
		return exportEventId;
	}

	/**
	 * @param exportEventId the exportEventId to set
	 */
	public void setExportEventId(String exportEventId) {
		this.exportEventId = exportEventId;
	}

	/**
	 * @return the transformerHelper
	 */
	public TransformerHelper getTransformerHelper() {
		return transformerHelper;
	}

	/**
	 * @param transformerHelper the transformerHelper to set
	 */
	public void setTransformerHelper(TransformerHelper transformerHelper) {
		this.transformerHelper = transformerHelper;
	}


	
	

}
