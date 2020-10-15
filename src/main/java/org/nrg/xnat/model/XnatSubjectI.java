package org.nrg.xnat.model;

public interface XnatSubjectI {
	public void toXML(java.io.Writer writer) throws java.lang.Exception;

	public String getId();

	public void setId(final String id);

	public void setLabel(final String label);

	public String getLabel();
}
