package org.nrg.xnat.model;

public interface XnatScanI {

	public int getXnatImageScanDataId();

	public void setXnatImageScanDataId(int xnatImageScanDataId);

	public String getId();

	public void setId(String id);

	public String getType();

	public void setType(String type);

	public String getQuality();

	public void setQuality(String quality);

	public String getXsiType();

	public void setXsiType(String xsiType);

	public String getNote();

	public void setNote(String note);

	public String getSeriesDescription();

	public void setSeriesDescription(String seriesDescription);
}
