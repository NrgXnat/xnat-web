package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.schema.SchemaElement;
import org.nrg.xft.XFTItem;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.XFTInitException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
public class XnatResourceDeserializer extends AbstractBaseElementDeserializer<XnatResource> {
    public XnatResourceDeserializer() {
        super(XnatResource.class);
    }

    @Override
    protected XnatResource deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
    	final TreeNode tree= parser.readValueAsTree();
    	final TreeNode xsiType = tree.get(DATA_TYPE);
    	
    	
        final XnatResource resource =getResourcedata(xsiType);
        
        Iterator<String> optionsKeys = tree.fieldNames();
    	
  	  while (optionsKeys.hasNext()) {
  		  String field = optionsKeys.next();
  		  TreeNode optionValue = tree.get(field);
  		  switch (field) {
  		 case "fileCount":
             resource.setFileCount(Integer.parseInt(removeFirstAndLastQuotes(optionValue.toString())));
             break;
         case "label":
             resource.setLabel(removeFirstAndLastQuotes(optionValue.toString()));
             break;
         case "note":
             resource.setNote(removeFirstAndLastQuotes(optionValue.toString()));
             break;
         case "description":
             resource.setDescription(removeFirstAndLastQuotes(optionValue.toString()));
             break;
         case "content":
             resource.setContent(removeFirstAndLastQuotes(optionValue.toString()));
             break;
         case "format":
             resource.setFormat(removeFirstAndLastQuotes(optionValue.toString()));
             break;
         case "fileSize":
             resource.setFileSize(removeFirstAndLastQuotes(optionValue.toString()));
             break;
         case "uri":
             resource.setUri(removeFirstAndLastQuotes(optionValue.toString()));
             break;
         case "xnatAbstractResourceId":
             resource.setXnatAbstractresourceId(Integer.parseInt(removeFirstAndLastQuotes(optionValue.toString())));
             break;
  		  }

        }
        return resource;
    }
    
    private XnatResource getResourcedata(TreeNode xsiType) {
    	XnatResource resource = null;
		SchemaElement element;
		try {
			element = SchemaElement.GetElement(removeFirstAndLastQuotes(xsiType.toString()));
			final Class<? extends XnatResource> xsiTypeClass = element.getCorrespondingJavaClass().asSubclass(XnatResource.class);
			resource = xsiTypeClass.newInstance();
		} catch (XFTInitException | ElementNotFoundException | ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException  e) {
			e.printStackTrace();
		}
		return resource;
	}
    
    private String removeFirstAndLastQuotes(String inputString) {
    	return inputString.toString().replace("\"", "");
    }
    
    private static final String DATA_TYPE = "xsiType";

	@Override
	protected XnatResource getNewInstance() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
