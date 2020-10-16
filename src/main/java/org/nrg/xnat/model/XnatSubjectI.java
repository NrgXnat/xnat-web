package org.nrg.xnat.model;

import java.util.Date;

public interface XnatSubjectI {
	public void toXML(java.io.Writer writer) throws java.lang.Exception;

	public String getId();
	
	public void setId(final String id);

	public void setLabel(final String label);

	public String getLabel();
	
	public void setProject(String project);
	
	public String getProject();
	
	public Date getInsertDate();
	
	public void setInsertDate(Date insertDate);
	
	public String getInsertUser();
	
	public void setInsertUser(String insertUser);
	
}
