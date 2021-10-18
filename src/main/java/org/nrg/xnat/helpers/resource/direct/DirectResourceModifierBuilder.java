/*
 * web: org.nrg.xnat.helpers.resource.direct.DirectResourceModifierBuilder
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.resource.direct;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.model.*;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatReconstructedimagedata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.security.UserI;

public class DirectResourceModifierBuilder implements ResourceModifierBuilderI {
	private XnatReconstructedimagedata recon;
	private XnatImagescandata scan;
	private XnatImageassessordata assess;
	private XnatImagesessiondata assessed;
	private XnatExperimentdata expt;
	private XnatSubjectdata subject;
	private XnatProjectdata project;
	
	private String type;
	
	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public XnatReconstructedimagedata getRecon() {
		return recon;
	}

	@Override
	public void setRecon(XnatImagesessiondataI assessed, XnatReconstructedimagedataI recon, String type) {
		this.type = StringUtils.defaultIfBlank(type, "out");
		this.assessed= (XnatImagesessiondata) assessed;
		this.recon = (XnatReconstructedimagedata) recon;
	}

	@Override
	public XnatImagescandata getScan() {
		return scan;
	}

	@Override
	public void setScan(XnatImagesessiondataI assessed, XnatImagescandataI scan) {
		this.assessed= (XnatImagesessiondata) assessed;
		this.scan = (XnatImagescandata) scan;
	}

	@Override
	public XnatImageassessordata getAssess() {
		return assess;
	}

	@Override
	public void setAssess(XnatImagesessiondataI assessed, XnatImageassessordataI assess, String type) {
		this.type = StringUtils.defaultIfBlank(type, "out");
		this.assessed= (XnatImagesessiondata) assessed;
		this.assess = (XnatImageassessordata) assess;
	}

	@Override
	public XnatExperimentdata getExpt() {
		return expt;
	}

	@Override
	public void setExpt(XnatProjectdataI project,XnatExperimentdataI expt) {
		this.project = (XnatProjectdata) project;
		this.expt = (XnatExperimentdata) expt;
	}

	@Override
	public XnatSubjectdata getSubject() {
		return subject;
	}

	@Override
	public void setSubject(XnatProjectdataI project, XnatSubjectdataI subject) {
		this.project = (XnatProjectdata) project;
		this.subject = (XnatSubjectdata) subject;
	}

	@Override
	public XnatProjectdata getProject() {
		return project;
	}

	@Override
	public void setProject(XnatProjectdataI project) {
		this.project = (XnatProjectdata) project;
	}
	
	/* (non-Javadoc)
	 * @see org.nrg.xnat.helpers.resource.direct.DirectResourceBuilderI#buildResourceModifier()
	 */
	@Override
	public ResourceModifierA buildResourceModifier(final boolean overwrite, final UserI user,EventMetaI ci) throws Exception{        
		if(recon!=null){
			//reconstruction			
			if(assessed==null){
				throw new Exception("Invalid session id");
			}
			
			return new DirectReconResourceImpl(recon, assessed, type,overwrite,user,ci);
		}else if(scan!=null){
			//scan
			if(assessed==null){
				throw new Exception("Invalid session id");
			}
			
			return new DirectScanResourceImpl(scan, assessed,overwrite,user,ci);
		}else if(assess!=null){
			if(assessed==null){
				throw new Exception("Invalid session id");
			}
		
			return new DirectAssessResourceImpl((XnatImageassessordata)assess,(XnatImagesessiondata)assessed,type,overwrite,user,ci);
		}else if(expt!=null){
			return new DirectExptResourceImpl(project, expt,overwrite,user,ci);
		}else if(subject!=null){
			return new DirectSubjResourceImpl(project, subject,overwrite,user,ci);
		}else if(project!=null){
			return new DirectProjResourceImpl(project,overwrite,user,ci);
		}else{
			throw new Exception("Invalid resource (perhaps a parent element has been deleted)");
		}
	}
}
