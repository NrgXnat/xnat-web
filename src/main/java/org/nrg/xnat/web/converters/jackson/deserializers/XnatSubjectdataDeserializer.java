package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.XnatAbstractdemographicdata;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatAbstractsubjectmetadata;
import org.nrg.xdat.om.XnatExperimentdataField;
import org.nrg.xdat.om.XnatInvestigatordata;
import org.nrg.xdat.om.XnatProjectparticipant;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.ItemI;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class XnatSubjectdataDeserializer<T extends XnatSubjectdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1154625120496783681L;

    @SuppressWarnings("unchecked")
    public XnatSubjectdataDeserializer() {
        this((Class<T>) XnatSubjectdata.class);
    }

    protected XnatSubjectdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "project":
                instance.setProject(parser.getText());
                break;
            case "group":
                instance.setGroup(parser.getText());
                break;
            case "src":
                // TODO: This is a repeat of the case above. Turn on warnings for duplicate branches.
                instance.setGroup(parser.getText());
                break;
            case "initials":
                instance.setInitials(parser.getText());
                break;
            case "demographics":
            	try {
            		instance.setDemographics((ItemI)parser.readValueAs(XnatAbstractdemographicdata.class));
            	} catch (Exception e) {
                    // TODO: Use log.error("message", e) here, not e.printStackTrace()
            		e.printStackTrace();
            	}
            	break;
            case "sharing":
            	try {
            		instance.setSharing_share(parser.readValueAs(XnatProjectparticipant.class));
            	} catch (Exception e) {
                    // TODO: Use log.error("message", e) here, not e.printStackTrace()
            		e.printStackTrace();
            	}
            	break;
            case "resources":
            	try {
            		instance.setResources_resource(parser.readValueAs(XnatAbstractresource.class));
            	} catch (Exception e) {
                    // TODO: Use log.error("message", e) here, not e.printStackTrace()
            		e.printStackTrace();
            	}
            	break;
            case "investigator":
            	final String investigatorId = parser.getText();
                try {
                    instance.setInvestigator((ItemI) XnatInvestigatordata.getXnatInvestigatordatasByXnatInvestigatordataId(investigatorId, null, false));
                } catch (Exception e) {
                    log.error("An error occurred trying to set the investigator with ID {} on this subjects", investigatorId, e);
                }
            	break; 	
            case "metadata":
            	try {
            		instance.setMetadata((ItemI)parser.readValueAs(XnatAbstractsubjectmetadata.class));
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            	break;  
            // TODO: Treat common acronyms and abbreviations as single words, capitalizing only the first letter: "ID" becomes "Id", "UID" becomes "Uid".
            case "addID":
                // TODO: This is a repeat of the case above. Turn on warnings for duplicate branches.
            	try {
            		instance.setMetadata((ItemI)parser.readValueAs(XnatAbstractsubjectmetadata.class));
            	} catch (Exception e) {
                    // TODO: Use log.error("message", e) here, not e.printStackTrace()
            		e.printStackTrace();
            	}
            	break;
            case "fields":
                final Map<String, String> fields = parser.readValueAs(MAP_STRING_STRING);
                fields.forEach((key, value) -> {
                    final XnatExperimentdataField exptField = new XnatExperimentdataField();
                    exptField.setName(key);
                    exptField.setField(value);
                    try {
                        instance.setFields_field(exptField);
                    } catch (Exception e) {
                        log.error("Tried to set a field on an subject with name {} and field {} but failed", key, value, e);
                    }
                });
                break;
            case "experiments":
            	try {
            		instance.setExperiments_experiment(parser.readValueAs(XnatSubjectassessordata.class));
            	} catch (Exception e) {
                    // TODO: Use log.error("message", e) here, not e.printStackTrace()
            		e.printStackTrace();
            	}
            	break;    
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
