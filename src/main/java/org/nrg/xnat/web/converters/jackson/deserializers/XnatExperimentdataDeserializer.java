package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatCrsessiondata;
import org.nrg.xdat.om.XnatCtsessiondata;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.om.XnatPetmrsessiondata;
import org.nrg.xdat.om.XnatPetsessiondata;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

@Slf4j
public class XnatExperimentdataDeserializer extends AbstractBaseElementDeserializer<XnatExperimentdata> {
	
    public XnatExperimentdataDeserializer() {
        super(XnatExperimentdata.class);
    }

    @Override
    protected XnatExperimentdata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
    	final TreeNode tree= parser.readValueAsTree();
    	final TreeNode xsiType = tree.get(DATA_TYPE);
    
    	if(xsiType == null) 
    		throw new RuntimeException("xsiType not found");
    	
    	XnatExperimentdata experiment = getExperimentTypeObject(xsiType);
    	
    	if(Objects.isNull(experiment))
    		throw new NullPointerException("Experiment object is Null");
    	
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
    
    private XnatExperimentdata getExperimentTypeObject(TreeNode xsiType) {
    	XnatExperimentdata experiment = null;
    	if(removeFirstAndLastQuotes(xsiType.toString()).equals(MR_SESSION_DATA)) 
    		experiment =new XnatMrsessiondata();
    	else if(removeFirstAndLastQuotes(xsiType.toString()).equals(CR_SESSION_DATA)) 
    		experiment =new XnatCrsessiondata();
    	else if(removeFirstAndLastQuotes(xsiType.toString()).equals(CT_SESSION_DATA)) 
    		experiment =new XnatCtsessiondata();
    	else if(removeFirstAndLastQuotes(xsiType.toString()).equals(PET_MR_SESSION_DATA)) 
    		experiment =new XnatPetmrsessiondata();
    	else if(removeFirstAndLastQuotes(xsiType.toString()).equals(PET_SESSION_DATA)) 
    		experiment =new XnatPetsessiondata();
		return experiment;
	}

	public String removeFirstAndLastQuotes(String inputString) {
    	return inputString.toString().replace("\"", "");
    }
    
	private static final String DATA_TYPE= "xsiType";
	private static final String MR_SESSION_DATA= "xnat:mrSessionData";
	private static final String CR_SESSION_DATA= "xnat:crSessionData";
	private static final String CT_SESSION_DATA= "xnat:ctSessionData";
	private static final String PET_MR_SESSION_DATA= "xnat:petmrSessionData";
	private static final String PET_SESSION_DATA= "xnat:petSessionData";
	
}
