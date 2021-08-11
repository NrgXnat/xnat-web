package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAybocsdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAybocsdataDeserializer<T extends XnatAybocsdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5958369912500625099L;

    @SuppressWarnings("unchecked")
    public XnatAybocsdataDeserializer() {
        this((Class<T>) XnatAybocsdata.class);
    }

    public XnatAybocsdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "behaviordrivestrength":
                // TODO: Handle the "behaviordrivestrength" property here: Integer
                break;
            case "behaviorsinterferefunctioning":
                // TODO: Handle the "behaviorsinterferefunctioning" property here: Integer
                break;
            case "controloverthoughts":
                // TODO: Handle the "controloverthoughts" property here: Integer
                break;
            case "currentorworstever":
                // TODO: Handle the "currentorworstever" property here: String
                break;
            case "distresscaused":
                // TODO: Handle the "distresscaused" property here: Integer
                break;
            case "efforttoresistbehaviors":
                // TODO: Handle the "efforttoresistbehaviors" property here: Integer
                break;
            case "efforttoresistthoughts":
                // TODO: Handle the "efforttoresistthoughts" property here: Integer
                break;
            case "feelingifprevented":
                // TODO: Handle the "feelingifprevented" property here: Integer
                break;
            case "firstuntiljustrightage":
                // TODO: Handle the "firstuntiljustrightage" property here: Double
                break;
            case "frequencyuntiljustright":
                // TODO: Handle the "frequencyuntiljustright" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "subjectassessordata":
                // TODO: Handle the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
                break;
            case "thoughtsinterferefunctioning":
                // TODO: Handle the "thoughtsinterferefunctioning" property here: Integer
                break;
            case "timeoccupiedwiththoughts":
                // TODO: Handle the "timeoccupiedwiththoughts" property here: Integer
                break;
            case "timeperforming":
                // TODO: Handle the "timeperforming" property here: Integer
                break;
            case "untiljustright":
                // TODO: Handle the "untiljustright" property here: Boolean
                break;
            case "untiljustrightawareness":
                // TODO: Handle the "untiljustrightawareness" property here: String
                break;
            case "untiljustrightperceptions":
                // TODO: Handle the "untiljustrightperceptions" property here: String
                break;
            case "whenstartuntiljustright":
                // TODO: Handle the "whenstartuntiljustright" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

