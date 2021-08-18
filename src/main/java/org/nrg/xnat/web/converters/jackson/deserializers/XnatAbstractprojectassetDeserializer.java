package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractprojectasset;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractprojectassetDeserializer<T extends XnatAbstractprojectasset> extends XnatGenericdataDeserializer<T> {
    private static final long serialVersionUID = -8157904633972018278L;

    protected XnatAbstractprojectassetDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "experiments_experiment":
                // TODO: Handle the "experiments_experiment" property here: java.util.List
                break;
            case "subjects_subject":
                // TODO: Handle the "subjects_subject" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

