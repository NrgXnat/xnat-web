package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrassessordata;

import java.io.IOException;

@Slf4j
public abstract class XnatMrassessordataDeserializer<T extends XnatMrassessordata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 472710790566182862L;

    protected XnatMrassessordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "addParametersByName":
                // TODO: Handle the "addParametersByName" property here: java.util.Hashtable
                break;
            case "imageassessordata":
                // TODO: Handle the "imageassessordata" property here: org.nrg.xdat.om.XnatImageassessordata
                break;
            case "mrSessionData":
                // TODO: Handle the "mrSessionData" property here: org.nrg.xdat.om.XnatMrsessiondata
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

