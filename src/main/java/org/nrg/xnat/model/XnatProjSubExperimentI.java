package org.nrg.xnat.model;

public interface XnatProjSubExperimentI extends XnatExperimentI {
	
	public String getXnatSubjectAccessorDataId();

	public void setXnatSubjectAccessorDataId(String xnatSubjectAccessorDataId);
}
