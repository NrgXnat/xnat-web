package org.nrg.xnat.services.resources.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.Logger;
import org.nrg.xdat.bean.base.BaseElement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanRepresentationUtil {
	BaseElement cat = null;
	boolean includeSchemaLocations=true;
	
	public BeanRepresentationUtil(BaseElement i,boolean includeSchemaLocations) {
		cat=i;	
		this.includeSchemaLocations=includeSchemaLocations;
	}

	public BeanRepresentationUtil(BaseElement i) {
		cat=i;	
	}
	
	public String getText() throws IOException {
	       String result = null;
	       if (isAvailable()) {
	           final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	           write(baos);
	           result = baos.toString();
	       }
	       return result;
	   }
	
	private boolean isAvailable() {
		return true;
	}

	public void write(OutputStream out) throws IOException {
			try {
				PrintWriter pw = new PrintWriter(out);
				cat.toXML(pw, false);
				pw.close();
			} catch (IllegalArgumentException e) {
				log.error("",e);
			} catch (TransformerFactoryConfigurationError e) {
				log.error("",e);
			}
	}

	
}
