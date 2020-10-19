package org.nrg.xnat.model;

import java.sql.Timestamp;
import java.util.Date;

public interface XnatExperimentI {
	
	public String getId();

	public void setId(String id);

	public String getLable();

	public void setLable(String lable);

	public String getProject();

	public void setProject(String project);

	public Date getDate();

	public void setDate(Date date);

	public Date getInsertDate();

	public void setInsertDate(Date insertDate);

	public String getXsiType();

	public void setXsiType(String xsiType);
}
