package org.nrg.xnat.export.transporters.http;

/**
 * @author Mohana Ramaratnam
 *
 */
public class HTTPResponseHolder {

	private int statusCode;

	private String statusMessage;
	
	private int filesSentCount;
	private long filesSentSize;
	
	public HTTPResponseHolder(final int code, final String msg) {
		statusCode = code;
		statusMessage = msg;
	}
	
	
	public HTTPResponseHolder(final int code, final String msg, final int filesSentCount, final long filesSentSize) {
		this(code, msg);
		this.filesSentCount  = filesSentCount;
		this.filesSentSize = filesSentSize;
	}
	
	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * @param statusMessage the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	/**
	 * @return the filesSentCount
	 */
	public int getFilesSentCount() {
		return filesSentCount;
	}

	/**
	 * @param filesSentCount the filesSentCount to set
	 */
	public void setFilesSentCount(int filesSentCount) {
		this.filesSentCount = filesSentCount;
	}

	/**
	 * @return the filesSentSize
	 */
	public long getFilesSentSize() {
		return filesSentSize;
	}

	/**
	 * @param filesSentSize the filesSentSize to set
	 */
	public void setFilesSentSize(long filesSentSize) {
		this.filesSentSize = filesSentSize;
	}
	
	
	
}
