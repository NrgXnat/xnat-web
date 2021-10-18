/*
 * web: org.nrg.xnat.helpers.resource.direct.ResourceModifierBuilderI
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.resource.direct;

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

public interface ResourceModifierBuilderI {

	public abstract ResourceModifierA buildResourceModifier(final boolean overwrite, final UserI user,final EventMetaI ci) throws Exception;

	public abstract void setProject(XnatProjectdataI project);

	public abstract XnatProjectdata getProject();

	public abstract void setSubject(XnatProjectdataI project, XnatSubjectdataI subject);

	public abstract XnatSubjectdata getSubject();

	public abstract void setExpt(XnatProjectdataI project, XnatExperimentdataI expt);

	public abstract XnatExperimentdata getExpt();

	public abstract void setAssess(XnatImagesessiondataI assessed, XnatImageassessordataI assess, String type);

	public abstract XnatImageassessordata getAssess();

	public abstract void setScan(XnatImagesessiondataI assessed, XnatImagescandataI scan);

	public abstract XnatImagescandata getScan();

	public abstract void setRecon(XnatImagesessiondataI assessed, XnatReconstructedimagedataI recon, String type);

	public abstract XnatReconstructedimagedata getRecon();

	public abstract void setType(String type);

	public abstract String getType();


}
