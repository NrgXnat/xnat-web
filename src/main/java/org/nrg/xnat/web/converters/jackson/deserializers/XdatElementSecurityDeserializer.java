package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementSecurity;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatElementSecurityDeserializer<T extends XdatElementSecurity> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2556290323057632551L;

    @SuppressWarnings("unchecked")
    public XdatElementSecurityDeserializer() {
        this((Class<T>) XdatElementSecurity.class);
    }

    public XdatElementSecurityDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "accessible":
                // TODO: Handle the "accessible" property here: Boolean
                break;
            case "browse":
                // TODO: Handle the "browse" property here: Boolean
                break;
            case "category":
                // TODO: Handle the "category" property here: String
                break;
            case "code":
                // TODO: Handle the "code" property here: String
                break;
            case "elementActions_elementAction":
                // TODO: Handle the "elementActions_elementAction" property here: java.util.ArrayList
                break;
            case "elementName":
                // TODO: Handle the "elementName" property here: String
                break;
            case "listingActions_listingAction":
                // TODO: Handle the "listingActions_listingAction" property here: java.util.ArrayList
                break;
            case "plural":
                // TODO: Handle the "plural" property here: String
                break;
            case "preLoad":
                // TODO: Handle the "preLoad" property here: Boolean
                break;
            case "primarySecurityFields_primarySecurityField":
                // TODO: Handle the "primarySecurityFields_primarySecurityField" property here: java.util.ArrayList
                break;
            case "quarantine":
                // TODO: Handle the "quarantine" property here: Boolean
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "searchable":
                // TODO: Handle the "searchable" property here: Boolean
                break;
            case "secondaryPassword":
                // TODO: Handle the "secondaryPassword" property here: Boolean
                break;
            case "secure":
                // TODO: Handle the "secure" property here: Boolean
                break;
            case "secureCreate":
                // TODO: Handle the "secureCreate" property here: Boolean
                break;
            case "secureDelete":
                // TODO: Handle the "secureDelete" property here: Boolean
                break;
            case "secureEdit":
                // TODO: Handle the "secureEdit" property here: Boolean
                break;
            case "secureIp":
                // TODO: Handle the "secureIp" property here: Boolean
                break;
            case "secureRead":
                // TODO: Handle the "secureRead" property here: Boolean
                break;
            case "sequence":
                // TODO: Handle the "sequence" property here: Integer
                break;
            case "singular":
                // TODO: Handle the "singular" property here: String
                break;
            case "usage":
                // TODO: Handle the "usage" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

