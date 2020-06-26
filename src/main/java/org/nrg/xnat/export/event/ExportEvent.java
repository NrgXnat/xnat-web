package org.nrg.xnat.export.event;

import org.nrg.framework.event.EventI;
import org.nrg.xnat.export.manifest.DataDescendantManifest;

/**
 * @author Mohana Ramaratnam
 *
 */
public class ExportEvent  implements EventI  {

	//This is a place holder for all things that were exported

	/**
	 *
	 */
	private static final long serialVersionUID = -983220063524330877L;

	private String exportTrackingId;
	private Integer userId;
	private DataDescendantManifest dataDescendantManifest;
	private String status;
	private String message;
	private boolean exported;
	private long numberOfFiles = 0;
	private long fileSize = 0;

	public ExportEvent(final String exportId, final Integer uId, final DataDescendantManifest dataDescendantManifest, final String status, final String message, final boolean exported) {
		this.exportTrackingId = exportId;
		userId = uId;
		this.dataDescendantManifest = dataDescendantManifest;
		this.status = status;
		this.message = message;
		this.exported = exported;

	}

	/**
	 * @return the dataDescendantManifest
	 */
	public DataDescendantManifest getDataDescendantManifest() {
		return dataDescendantManifest;
	}




	/**
	 * @param dataDescendantManifest the dataDescendantManifest to set
	 */
	public void setDataDescendantManifest(DataDescendantManifest dataDescendantManifest) {
		this.dataDescendantManifest = dataDescendantManifest;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer uId) {
		userId = uId;
	}



	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 * @return the exported
	 */
	public boolean isExported() {
		return exported;
	}

	/**
	 * @param exported the exported to set
	 */
	public void setExported(boolean exported) {
		this.exported = exported;
	}



	/**
	 * @return the exportId
	 */
	public String getExportTrackingId() {
		return exportTrackingId;
	}

	/**
	 * @param exportId the exportId to set
	 */
	public void setExportTrackingId(String exportId) {
		this.exportTrackingId = exportId;
	}



	/**
	 * @return the nFiles
	 */
	public long getNumberOfFiles() {
		return numberOfFiles;
	}

	/**
	 * @param nFiles the nFiles to set
	 */
	public void setNumberOfFiles(long nFiles) {
		this.numberOfFiles = nFiles;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return dataDescendantManifest.toString() + " (Status: "+ status + " Exported: " + exported + " NoOfFiles: " + this.numberOfFiles + " FileSize:" + this.fileSize + ")";
	}



}
