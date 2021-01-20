package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.XnatCrsessiondata;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatMrsessiondata;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
public class XnatExperimentdataDeserializer extends AbstractBaseElementDeserializer<XnatExperimentdata> {
	
    public XnatExperimentdataDeserializer() {
        super(XnatExperimentdata.class);
    }

    @Override
    protected XnatExperimentdata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
    	XnatExperimentdata experiment = null;
    	final TreeNode tree= parser.readValueAsTree();
    	final TreeNode xsiType = tree.get("xsiType");
    
    	if(xsiType == null) 
    		throw new RuntimeException("xsiType not found");
    	
    	if(removeFirstAndLastQuotes(xsiType.toString()).equals(MR_SESSION_DATA)) 
    		experiment =new XnatMrsessiondata();
    	else if(removeFirstAndLastQuotes(xsiType.toString()).equals(CR_SESSION_DATA)) 
    		experiment =new XnatCrsessiondata();
    	
    	Iterator<String> optionsKeys = tree.fieldNames();
    	  while (optionsKeys.hasNext()) {
    		  String field = optionsKeys.next();
    		  TreeNode optionValue = tree.get(field);
    		  switch (field) {
              case "id":
              	experiment.setId(removeFirstAndLastQuotes(optionValue.toString()));
                  break;
              case "label":
              	experiment.setLabel(removeFirstAndLastQuotes(optionValue.toString()));
                  break;
              case "project":
                  experiment.setProject(removeFirstAndLastQuotes(optionValue.toString()));
                  break;
              case "note":
                  experiment.setNote(removeFirstAndLastQuotes(optionValue.toString()));
                  break;
              case "protocol":
                  experiment.setProtocol(removeFirstAndLastQuotes(optionValue.toString()));
                  break;
              case "original":
                  experiment.setOriginal(removeFirstAndLastQuotes(optionValue.toString()));
                  break;
              case "date":
                  experiment.setDate(parseDate(removeFirstAndLastQuotes(optionValue.toString())));
                  break;
              case "delay":
                  experiment.setDelay(Integer.parseInt(removeFirstAndLastQuotes(optionValue.toString())));
                  break;
              case "version":
                  experiment.setVersion(Integer.parseInt(removeFirstAndLastQuotes(optionValue.toString())));
                  break;
              case "acquisitionSite":
                  experiment.setAcquisitionSite(removeFirstAndLastQuotes(optionValue.toString()));
                  break;
              case "visit":
                  experiment.setVisit(removeFirstAndLastQuotes(optionValue.toString()));
                  break;
              case "visitId":
                  experiment.setVisitId(removeFirstAndLastQuotes(optionValue.toString()));
                  break;
			}
    	  }
        return experiment;
    }
    
    public String removeFirstAndLastQuotes(String inputString) {
    	return inputString.toString().replace("\"", "");
    }
    
	private static final String MR_SESSION_DATA= "xnat:mrSessionData";
	private static final String CR_SESSION_DATA= "xnat:crSessionData";
}
