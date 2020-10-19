package org.nrg.xnat.model;

public interface XnatExperimentResourceI {

	public int getXnatAbstractResourceId();

	public void setXnatAbstractResourceId(int xnatAbstractResourceId);

	public String getCategoryId();

	public void setCategoryId(String categoryId);

	public String getCategoryDescription();

	public void setCategoryDescription(String categoryDescription);

	public String getCategory();

	public void setCategory(String category);

	public String getElementName();

	public void setElementName(String elementName);

	public int getFileCount();

	public void setFileCount(int fileCount);

	public long getFileSize();

	public void setFileSize(long fileSize);

	public String getFormat();

	public void setFormat(String format);

	public String getContent();

	public void setContent(String content);

	public String getTags();

	public void setTags(String tags);
}
