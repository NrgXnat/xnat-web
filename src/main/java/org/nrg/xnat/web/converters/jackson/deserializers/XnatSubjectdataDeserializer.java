package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
            case "initials":
                instance.setInitials(parser.getText());
                break;
            /*
            TODO: This is all demographics data. Let the demographics data deserializer handle this.

            case "dob":
                demographics.setDob(parseDate(parser.getText()));
                break;
            case "educationDesc":
                demographics.setEducationdesc(parser.getText());
                break;
            case "education":
                demographics.setEducation(parser.getIntValue());
                break;
            case "age":
                demographics.setAge(parser.getIntValue());
                break;
            case "ses":
                demographics.setSes(parser.getIntValue());
                break;
            case "gender":
                demographics.setGender(parser.getText());
                break;
            case "handedness":
                demographics.setHandedness(parser.getText());
                break;
            case "ethnicity":
                demographics.setEthnicity(parser.getText());
                break;
            case "race":
                demographics.setRace(parser.getText());
                break;
            */
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
