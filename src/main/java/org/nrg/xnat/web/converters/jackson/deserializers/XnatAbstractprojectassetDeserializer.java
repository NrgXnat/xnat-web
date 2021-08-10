package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractprojectasset;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAbstractprojectassetDeserializer<T extends XnatAbstractprojectasset> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8991369565819783475L;

    @SuppressWarnings("unchecked")
    public XnatAbstractprojectassetDeserializer() {
        this((Class<T>) XnatAbstractprojectasset.class);
    }

    public XnatAbstractprojectassetDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "experimentIds":
                // TODO: Handle the "experimentIds" property here: java.util.List
                break;
            case "experiments_experiment":
                // TODO: Handle the "experiments_experiment" property here: java.util.List
                break;
            case "genericdata":
                // TODO: Handle the "genericdata" property here: org.nrg.xdat.om.XnatGenericdata
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "subjectIds":
                // TODO: Handle the "subjectIds" property here: java.util.List
                break;
            case "subjects_subject":
                // TODO: Handle the "subjects_subject" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

