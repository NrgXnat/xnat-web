package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGenericdata;

import java.io.IOException;

@Slf4j
public abstract class XnatGenericdataDeserializer<T extends XnatGenericdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2144229650649178305L;

    protected XnatGenericdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "experimentdata":
                // TODO: Handle the "experimentdata" property here: org.nrg.xdat.om.XnatExperimentdata
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

