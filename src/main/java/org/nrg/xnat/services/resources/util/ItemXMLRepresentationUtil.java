package org.nrg.xnat.services.resources.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.Logger;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.XFTItem;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXWriter;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ItemXMLRepresentationUtil {
	XFTItem item = null;
	boolean includeSchemaLocations=true;
	private boolean allowDBAccess=true;
	private boolean hidden_fields=true;
	
	public ItemXMLRepresentationUtil(XFTItem i,boolean includeSchemaLocations,boolean writeHiddenFields) {
		item=i;	
		this.includeSchemaLocations=includeSchemaLocations;
		hidden_fields=writeHiddenFields;
	}

	public ItemXMLRepresentationUtil(XFTItem i) {
		item=i;	
	}
	
	public void setAllowDBAccess(boolean b){
		this.allowDBAccess=b;
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
				SAXWriter writer = new SAXWriter(out,this.allowDBAccess);
				if(includeSchemaLocations){
					writer.setAllowSchemaLocation(true);
					writer.setLocation(TurbineUtils.GetFullServerPath() + "/" + "schemas/");
				}
				writer.setWriteHiddenFields(hidden_fields);
				writer.write(item);
			} catch (TransformerConfigurationException e) {
				log.error("",e);
			} catch (IllegalArgumentException e) {
				log.error("",e);
			} catch (TransformerFactoryConfigurationError e) {
				log.error("",e);
			} catch (FieldNotFoundException e) {
				log.error("",e);
			} catch (SAXException e) {
				log.error("",e);
			}
	}

}
