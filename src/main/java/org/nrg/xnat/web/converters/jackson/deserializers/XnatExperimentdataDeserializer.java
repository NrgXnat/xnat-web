package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatExperimentdataField;
import org.nrg.xdat.om.XnatInvestigatordata;
import org.nrg.xft.ItemI;
import org.nrg.xft.security.UserI;

import java.io.IOException;
import java.util.Map;

@Slf4j
public abstract class XnatExperimentdataDeserializer<T extends XnatExperimentdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1267197324318417600L;

    protected XnatExperimentdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "user":
                final UserI user = getUserI(parser.getText());
                if (user != null) {
                    instance.setUser(user);
                }
                break;
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "project":
                instance.setProject(parser.getText());
                break;
            case "note":
                instance.setNote(parser.getText());
                break;
            case "protocol":
                instance.setProtocol(parser.getText());
                break;
            case "original":
                instance.setOriginal(parser.getText());
                break;
            case "date":
                instance.setDate(parseDate(parser.getText()));
                break;
            case "time":
                instance.setTime(parser.getText());
                break;
            case "version":
                instance.setVersion(parser.getIntValue());
                break;
            case "acquisitionSite":
                instance.setAcquisitionSite(parser.getText());
                break;
            case "visit":
                instance.setVisit(parser.getText());
                break;
            case "visitId":
                instance.setVisitId(parser.getText());
                break;
            case "duration":
                instance.setDuration(parser.getText());
                break;
            case "delay":
                instance.setDelay(parser.getIntValue());
                break;
            case "delayRefExptId":
                instance.setDelay_refExptId(parser.getText());
                break;
            case "investigator":
                // TODO: If this is just by ID, this is fine, but can also use investigator deserialization.
                final String investigatorId = parser.getText();
                try {
                    instance.setInvestigator((ItemI) XnatInvestigatordata.getXnatInvestigatordatasByXnatInvestigatordataId(investigatorId, null, false));
                } catch (Exception e) {
                    log.error("An error occurred trying to set the investigator with ID {} on this experiment", investigatorId, e);
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
                        log.error("Tried to set a field on an experiment with name {} and field {} but failed", key, value, e);
                    }
                });
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
