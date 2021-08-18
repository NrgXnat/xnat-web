package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDemographicdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatDemographicdataDeserializer<T extends XnatDemographicdata> extends XnatAbstractdemographicdataDeserializer<T> {
    private static final long serialVersionUID = 5433315834239914083L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDemographicdataDeserializer() {
        this((Class<T>) XnatDemographicdata.class);
    }

    protected XnatDemographicdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "age":
                instance.setAge(parser.getIntValue());
                break;
            case "birthWeight":
                instance.setBirthWeight(parser.getDoubleValue());
                break;
            case "dob":
                // TODO: Handle the "dob" property here: Object
                break;
            case "education":
                instance.setEducation(parser.getIntValue());
                break;
            case "educationdesc":
                instance.setEducationdesc(parser.getText());
                break;
            case "employment":
                instance.setEmployment(parser.getIntValue());
                break;
            case "ethnicity":
                instance.setEthnicity(parser.getText());
                break;
            case "gender":
                instance.setGender(parser.getText());
                break;
            case "gestationalAge":
                instance.setGestationalAge(parser.getDoubleValue());
                break;
            case "handedness":
                instance.setHandedness(parser.getText());
                break;
            case "height":
                instance.setHeight(parser.getDoubleValue());
                break;
            case "height_units":
                instance.setHeight_units(parser.getText());
                break;
            case "postMenstrualAge":
                instance.setPostMenstrualAge(parser.getDoubleValue());
                break;
            case "race":
                instance.setRace(parser.getText());
                break;
            case "race2":
                instance.setRace2(parser.getText());
                break;
            case "race3":
                instance.setRace3(parser.getText());
                break;
            case "race4":
                instance.setRace4(parser.getText());
                break;
            case "race5":
                instance.setRace5(parser.getText());
                break;
            case "race6":
                instance.setRace6(parser.getText());
                break;
            case "ses":
                instance.setSes(parser.getIntValue());
                break;
            case "weight":
                instance.setWeight(parser.getDoubleValue());
                break;
            case "weight_units":
                instance.setWeight_units(parser.getText());
                break;
            case "yob":
                instance.setYob(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

