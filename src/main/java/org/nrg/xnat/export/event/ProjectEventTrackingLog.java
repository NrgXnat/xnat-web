package org.nrg.xnat.export.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xnat.export.manifest.DataDescendantManifest;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Mohana Ramaratnam
 *
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mohana Ramaratnam
 *
 */
@JsonInclude
public class ProjectEventTrackingLog {
	    
		private long totalFiles = 0;
	    private long totalDataSize = 0;
	    private int successCount = 0;
	    private int failureCount = 0;
	    boolean exportedAll = false;
	    private String finalMessage =null;
	    
	    private Map<String, String> subjects = new HashMap<String, String>();
	    private Map<String,String> experiments = new HashMap<String, String>();
	    private Map<String, ExportLog> exports = new HashMap<String, ExportLog>();

	    public long getTotalFiles() {
	        return totalFiles;
	    }

	    public void setTotalFiles(long total) {
	        this.totalFiles = total;
	    }

	    public long getTotalDataSize() {
	        return totalDataSize;
	    }

	    public void setTotalDataSize(long total) {
	        this.totalDataSize = total;
	    }

		@JsonIgnore
	    public int getTotalExperiments() {
	        return experiments.size();
	    }


		@JsonIgnore
	    public int getTotalSubjects() {
	        return subjects.size();
	    }


	    public int getSuccessCount() {
	        return successCount;
	    }

	    public void setSuccessCount(int successCount) {
	        this.successCount = successCount;
	    }

	    public int getFailureCount() {
	        return failureCount;
	    }

	    public void setFailureCount(int failureCount) {
	        this.failureCount = failureCount;
	    }

	    public Map<String, ExportLog> getExports() {
	        return exports;
	    }

	    public void setExports(Map<String, ExportLog> exports) {
	        this.exports = exports;
	    }
	    

	    @JsonIgnore
	    public void addOrUpdateExport(DataDescendantManifest dataDescendantManifest, String exportStatus, String exportMessage, long nFiles, long fileSize, boolean exportComplete) {
	    	if (dataDescendantManifest.isAProjectOnlyManifest()) {
	        	this.exportedAll = exportComplete;
	        	if ( nFiles > 0) {
		        	this.totalFiles = nFiles;
		        }
		        if ( fileSize > 0) {
		        	this.totalDataSize = fileSize;
		        }
		        this.finalMessage= exportMessage;
	    	}else {
		        if (dataDescendantManifest.getSubjectId() != null) {
		        	subjects.put(dataDescendantManifest.getSubjectId(), "");
		        	if (dataDescendantManifest.getExperimentId() != null) {
		        		experiments.put(dataDescendantManifest.getExperimentId(),"");
		        	}
		        }
		        exports.put(dataDescendantManifest.getKey(), new ExportLog(dataDescendantManifest, exportStatus, exportMessage, nFiles, fileSize, exportComplete));
		        if (exportStatus.contains(PersistentWorkflowUtils.FAILED)) {
		            failureCount++;
		        } else if (exportStatus.equals(PersistentWorkflowUtils.COMPLETE)) {
		            successCount++;
		        }
	        }
	    }

	    
	    

	    /**
		 * @return the exportComplete
		 */
	    @JsonProperty("exportedAll")
		public boolean isExportComplete() {
			return exportedAll;
		}

		/**
		 * @param exportComplete the exportComplete to set
		 */
		public void setExportedAll(boolean exportComplete) {
			this.exportedAll = exportComplete;
		}

		@JsonIgnore
	    public boolean exportSuccess() {
	        return (successCount != 0 && failureCount == 0);
	    }

	    @JsonIgnore
	    @Nullable
	    public String exportMessage() {
	        if (successCount == 0 && failureCount == 0) {
	            return finalMessage;
	        }
	        if (exportSuccess()) {
	            return "Data was exported successfully";
	        } else if (successCount == 0 && failureCount != 0 ) {
	            return "Data export failed (" + failureCount + " exports failed)";
	        } else {
	            return successCount + " exports succeeded / " + failureCount + " exports failed";
	        }
	    }

	    @JsonIgnore
	    public void addFailures(Integer failures) {
	        failureCount += failures;
	    }
	    
	    
	    @JsonIgnore
	    public void saveToDb(String trackingId) {
	    	//TODO
	    	//Collection<ExportLog> logsToExport = exports.values();
	    	//for (ExportLog e: logsToExport) {
	    	//	DataDescendantManifest data  = e.getDataDescendantManifest();
	    		
	    	//}
	    }

	    /**
		 * @return the finalMessage
		 */
		public String getFinalMessage() {
			return finalMessage;
		}

		/**
		 * @param finalMessage the finalMessage to set
		 */
		public void setFinalMessage(String finalMessage) {
			this.finalMessage = finalMessage;
		}

		@JsonInclude
	    private static class ExportLog {
	    	private DataDescendantManifest dataDescendantManifest;
	    	private String status;
	    	private String message;
	    	private long numberOfFiles = -1;
	    	private long fileSize = -1;
	    	private boolean exportComplete;
	    	
	        public ExportLog(){}

	        public ExportLog(DataDescendantManifest dataDescendantManifest, String status, String message, long nFiles, long fileSize, boolean exportComplete) {
	            this.dataDescendantManifest = dataDescendantManifest;
	            this.status = status;
	            this.message = message;
	            this.numberOfFiles = nFiles;
	            this.fileSize = fileSize;
	            this.exportComplete = exportComplete;
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
			 * @return the totalFiles
			 */
			public long getNumberOfFiles() {
				return numberOfFiles;
			}

			/**
			 * @param totalFiles the totalFiles to set
			 */
			public void setNumberOfFiles(long nFiles) {
				this.numberOfFiles = nFiles;
			}

			/**
			 * @return the totalSize
			 */
			public long getFileSize() {
				return fileSize;
			}

			/**
			 * @param totalSize the totalSize to set
			 */
			public void setFileSize(long totalSize) {
				this.fileSize = totalSize;
			}

			/**
			 * @return the exportComplete
			 */
			public boolean isExportComplete() {
				return exportComplete;
			}

			/**
			 * @param exportComplete the exportComplete to set
			 */
			public void setExportComplete(boolean exportComplete) {
				this.exportComplete = exportComplete;
			}
	    	
	    	
	    	
	    	
	    }

}
