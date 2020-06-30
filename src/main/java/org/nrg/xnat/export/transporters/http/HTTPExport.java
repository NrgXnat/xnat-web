package org.nrg.xnat.export.transporters.http;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xnat.export.interfaces.TransformerI;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
public class HTTPExport {

	
	String destinationUrl = null;
	String destinationPort = null;
	String username = null;
	String password = null;
	String authHeader = null;
	
	String requestURL = null;
	final String charset = "UTF-8";
	
	

	public HTTPExport(final String url, final String port) {
		destinationUrl = url;
		destinationPort = port;
	}

	public HTTPExport(String url, String port, String uName, String pWord) {
		this(url,port);
		username = uName;
		password = pWord;
	}

	
	public HTTPResponseHolder send(XnatAbstractresourceI a, final String projectRootPath, final TransformerI transformer) throws MalformedURLException, IOException {
		try {
			setUrl();
			HTTPResponseHolder aggregatedResponse = exportToDestination(a, projectRootPath, transformer);
			return aggregatedResponse;
		}catch(Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	private void setUrl() throws MalformedURLException{
		requestURL = this.destinationUrl;
		if (this.destinationPort != null) {
			requestURL += ":" + this.destinationPort;
		}
	}

	private HTTPResponseHolder exportToDestination(XnatAbstractresourceI abs,  final String projectRootPath, final TransformerI transformer) throws IOException {
		List<HTTPResponseHolder> httpResponses = new ArrayList<HTTPResponseHolder>();
		int fileCount = 0;
		long fileSize = 0;
		if (abs instanceof XnatResourcecatalog) {
			ArrayList files  = ((XnatResourcecatalog) abs).getCorrespondingFiles(projectRootPath);
			if (files != null && files.size() > 0) {
				for (int i=0; i< files.size() ; i++) {
					File f = (File)files.get(i);
					if (f.exists() && f.isFile() && f.length() > 0) {
						try {
							HTTPResponseHolder response = exportToDestination(f, transformer);
							if (response.getFilesSentSize() > 0) {
								fileCount += response.getFilesSentCount();
								fileSize += response.getFilesSentSize();
							}
							httpResponses.add(response);
						}catch(Exception e) {
							HTTPResponseHolder response = new HTTPResponseHolder(HttpStatus.SC_NO_CONTENT,"File " + f.getAbsolutePath() + " does not exist or is of zero lenth", 0,0);
							httpResponses.add(response);
						}
					}else {
						HTTPResponseHolder response = new HTTPResponseHolder(HttpStatus.SC_NO_CONTENT,"File " + f.getAbsolutePath() + " does not exist or is of zero lenth", 0,0);
						httpResponses.add(response);
					}
				}
			}
		}
		HTTPResponseHolder aggregateResponse = extractSingleResponse(httpResponses);
		aggregateResponse.setFilesSentCount(fileCount);
		aggregateResponse.setFilesSentSize(fileSize);
		return aggregateResponse;
	}

	
	
	
	  private HTTPResponseHolder exportToDestination(File fileToExport, final TransformerI transformer) throws Exception { 
		  XMIRCContentFileUploader uploader = new XMIRCContentFileUploader(requestURL);
		  return uploader.exportToDestination(fileToExport, transformer);
		  
	  }
	
	private HTTPResponseHolder  extractSingleResponse(List<HTTPResponseHolder> httpResponses) {
		HTTPResponseHolder aggregatedResponse = new HTTPResponseHolder(-2,"");
		if (httpResponses != null &&  httpResponses.size() > 0) {
			int successStatus = 200;
			String aggregatedMessage = "";
			for (HTTPResponseHolder response : httpResponses) {
				if (response.getStatusCode() != 200) {
					successStatus = response.getStatusCode();
					aggregatedMessage += ";" + response.getStatusMessage();
				}
			}
			aggregatedResponse.setStatusCode(successStatus);
			aggregatedResponse.setStatusMessage(aggregatedMessage);
		}
		
		return aggregatedResponse;
	}


}
