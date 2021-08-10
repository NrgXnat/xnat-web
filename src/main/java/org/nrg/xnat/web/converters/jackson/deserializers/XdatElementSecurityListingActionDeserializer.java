package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementSecurityListingAction;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatElementSecurityListingActionDeserializer<T extends XdatElementSecurityListingAction> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 399185457856993221L;

    @SuppressWarnings("unchecked")
    public XdatElementSecurityListingActionDeserializer() {
        this((Class<T>) XdatElementSecurityListingAction.class);
    }

    public XdatElementSecurityListingActionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "displayName":
                // TODO: Handle the "displayName" property here: String
                break;
            case "elementActionName":
                // TODO: Handle the "elementActionName" property here: String
                break;
            case "image":
                // TODO: Handle the "image" property here: String
                break;
            case "parameterstring":
                // TODO: Handle the "parameterstring" property here: String
                break;
            case "popup":
                // TODO: Handle the "popup" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "secureaccess":
                // TODO: Handle the "secureaccess" property here: String
                break;
            case "sequence":
                // TODO: Handle the "sequence" property here: Integer
                break;
            case "xdatElementSecurityListingActionId":
                // TODO: Handle the "xdatElementSecurityListingActionId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

