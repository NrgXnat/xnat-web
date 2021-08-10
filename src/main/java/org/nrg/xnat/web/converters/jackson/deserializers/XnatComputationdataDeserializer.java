package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatComputationdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatComputationdataDeserializer<T extends XnatComputationdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1799093954898185947L;

    @SuppressWarnings("unchecked")
    public XnatComputationdataDeserializer() {
        this((Class<T>) XnatComputationdata.class);
    }

    public XnatComputationdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "source":
                // TODO: Handle the "source" property here: String
                break;
            case "units":
                // TODO: Handle the "units" property here: String
                break;
            case "value":
                // TODO: Handle the "value" property here: String
                break;
            case "xnatComputationdataId":
                // TODO: Handle the "xnatComputationdataId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

