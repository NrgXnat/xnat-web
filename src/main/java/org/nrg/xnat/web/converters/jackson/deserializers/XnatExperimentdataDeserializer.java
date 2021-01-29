package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.schema.SchemaElement;
import org.nrg.xft.XFTItem;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.XFTInitException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class XnatExperimentdataDeserializer extends AbstractBaseElementDeserializer<XnatExperimentdata> {
	
    public XnatExperimentdataDeserializer() {
        super(XnatExperimentdata.class);
    }

    @Override
    protected XnatExperimentdata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
    	final TreeNode tree= parser.readValueAsTree();
    	final TreeNode xsiType = tree.get(DATA_TYPE);
    	final TreeNode scan = tree.get(XNAT_SCAN);
    	final String property = SCAN_METHOD_NAME;
    	final boolean isArray = scan.isArray();
    	List<XnatImagescandata> xnatImagescandatas = new ArrayList<>();
    	if(isArray)
    		xnatImagescandatas= getXnatImagescandata(scan);
    	 
    	if(xsiType == null) 
    		throw new RuntimeException("xsiType not found");
    	
		XnatExperimentdata experiment = getExperimentdata(xsiType, property, xnatImagescandatas);
		
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
    
    
    /**
     * 
     * @param xsiType
     * @param property
     * @param xnatImagescandatas
     * @return
     */

	private XnatExperimentdata getExperimentdata(TreeNode xsiType, String property, List<XnatImagescandata> xnatImagescandatas) {
		XnatExperimentdata experiment = null;
		SchemaElement element;
		XFTItem item =null;
		try {
			element = SchemaElement.GetElement(removeFirstAndLastQuotes(xsiType.toString()));
			final Class<? extends XnatExperimentdata> xsiTypeClass = element.getCorrespondingJavaClass().asSubclass(XnatExperimentdata.class);
			final Method[] xsiTypeMethods = xsiTypeClass.getMethods();
			experiment = xsiTypeClass.newInstance();
			
			if(!xnatImagescandatas.isEmpty()) {
				for (XnatImagescandata xnatImagescandata : xnatImagescandatas) {
					item = xnatImagescandata.getItem();
					final Optional<Method> found = Arrays.stream(xsiTypeMethods).filter(method -> StringUtils.equals(method.getName(), "set" + StringUtils.capitalize(property))
									&& method.getParameterCount() == 1) .findFirst();
					found.orElseThrow(() -> new NoSuchMethodException("The class " + xsiTypeClass.getName() + " doesn't have a set method for the property " + property + "")).invoke(experiment, item);
				}
			}
		} catch (XFTInitException | ElementNotFoundException | ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}
		return experiment;
	}

	/**
	 * 
	 * @param scan
	 * @return
	 */
	private List<XnatImagescandata> getXnatImagescandata(TreeNode scan) {
		List<XnatImagescandata> xnatImagescandatas = new ArrayList<>();
		if (Objects.nonNull(scan) && scan.toString().length() > 0) {
			JSONArray jsonArray = new JSONArray(scan.toString());
			for (int i = 0, size = jsonArray.length(); i < size; i++) {
				XnatImagescandata xnatImagescandata = new XnatImagescandata();
				JSONObject objectInArray = jsonArray.getJSONObject(i);
				String[] elementNames = JSONObject.getNames(objectInArray);
				for (String elementName : elementNames) {
					switch (elementName) {
					case "id":
						xnatImagescandata.setId(objectInArray.getString(elementName));
						break;
					case "type":
						xnatImagescandata.setType(objectInArray.getString(elementName));
						break;
					case "xsiType":
						xnatImagescandata.getItem().setXmlType(objectInArray.getString(elementName));
						break;
					case "project":
						xnatImagescandata.setProject(objectInArray.getString(elementName));
						break;
					case "note":
						xnatImagescandata.setNote(objectInArray.getString(elementName));
						break;
					case "quality":
						xnatImagescandata.setQuality(objectInArray.getString(elementName));
						break;
					}
				}
				xnatImagescandatas.add(xnatImagescandata);
			}
		}

		return xnatImagescandatas;
	}

	public String removeFirstAndLastQuotes(String inputString) {
    	return inputString.toString().replace("\"", "");
    }
    
	private static final String DATA_TYPE = "xsiType";
	private static final String XNAT_SCAN = "scans";
	private static final String SCAN_METHOD_NAME = "Scans_scan";
	
}
