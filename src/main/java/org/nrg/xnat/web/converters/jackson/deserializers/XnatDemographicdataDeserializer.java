package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDemographicdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDemographicdataDeserializer<T extends XnatDemographicdata> extends XnatAbstractdemographicdataDeserializer<T> {
    private static final long serialVersionUID = -2139974715236848755L;

    @SuppressWarnings("unchecked")
    public XnatDemographicdataDeserializer() {
        this((Class<T>) XnatDemographicdata.class);
    }

    protected XnatDemographicdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "dob":
                instance.setDob(parseDate(parser.getText()));
                break;
            case "educationDesc":
                instance.setEducationdesc(parser.getText());
                break;
            case "education":
                instance.setEducation(parser.getIntValue());
                break;
            case "age":
                instance.setAge(parser.getIntValue());
                break;
            case "ses":
                instance.setSes(parser.getIntValue());
                break;
            case "gender":
                instance.setGender(parser.getText());
                break;
            case "handedness":
                instance.setHandedness(parser.getText());
                break;
            case "ethnicity":
                instance.setEthnicity(parser.getText());
                break;
            case "race":
                instance.setRace(parser.getText());
                break;
            case "weight":
                instance.setWeight(parser.getDoubleValue());
                break;
            case "height":
                instance.setHeight(parser.getDoubleValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
