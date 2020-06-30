package org.nrg.xnat.export.transporters.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.httpclient.HttpStatus;
import org.nrg.xnat.export.interfaces.TransformerI;

/**
 * @author Mohana Ramaratnam
 *
 */
public class XMIRCContentFileUploader {
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private final String DEFAULT_CONTENT_TYPE = "application/x-mirc";


    public XMIRCContentFileUploader(String requestURL)  throws IOException {
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type",DEFAULT_CONTENT_TYPE);
        httpConn.setRequestProperty("User-Agent", "XNAT Export Agent");
        outputStream = httpConn.getOutputStream();
    }

	public HTTPResponseHolder exportToDestination(File fileToExport, final TransformerI transformer) throws Exception {
	    try {
	    	httpConn.connect();
	    	//Call the Transformer
	    	FileInputStream inFile = new FileInputStream(fileToExport);
	    	long bytesSent = streamFile(fileToExport);
		    //TODO handle response code
		    int responseCode = httpConn.getResponseCode();
		    String message = "";
		    if (responseCode != HttpStatus.SC_OK) {
		     message = getTextOrException(httpConn.getInputStream(), Charset.forName("UTF-8"), true);
		    }
		    return new HTTPResponseHolder(responseCode, message, 1, bytesSent);
	    }finally {
	    	httpConn.disconnect();
	    	if (outputStream != null) {
	    		outputStream.close();
	    	}
	    }
	}
	 
	private long streamFile(File file) throws Exception {
	    long bytesSent = 0;
		BufferedInputStream bis = null;
	    BufferedOutputStream bos = null;
	    byte[] buffer = new byte[4096];
	    int n=0;
	    try {
	        bis = new BufferedInputStream( new FileInputStream(file) );
	        bos = new BufferedOutputStream(outputStream);
	        while ((n=bis.read(buffer,0,buffer.length)) > 0) {
	        	bos.write(buffer,0,n);
	        	bytesSent += n;
	        }
	        bos.flush();
	        return bytesSent;
	    }
	    finally {
	        bis.close();
	        bos.close();
	    }
	}
	 
	private String getTextOrException(InputStream stream, Charset charset, boolean close) throws Exception {
	    BufferedReader br = null;
	    try {
	        br = new BufferedReader( new InputStreamReader(stream, charset) );
	        StringWriter sw = new StringWriter();
	        int n;
	        char[] cbuf = new char[1024];
	        while ((n=br.read(cbuf, 0, cbuf.length)) != -1) sw.write(cbuf,0,n);
	        if (close) br.close();
	        return sw.toString();
	    }
	    catch (Exception e) {  throw(e); }
	    finally {
	    	if (br != null) br.close();
	    }
	    
	}





}
