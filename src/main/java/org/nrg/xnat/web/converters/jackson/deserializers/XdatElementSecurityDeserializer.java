package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementSecurity;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatElementSecurityDeserializer<T extends XdatElementSecurity> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8146264746581387047L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatElementSecurityDeserializer() {
        this((Class<T>) XdatElementSecurity.class);
    }

    protected XdatElementSecurityDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "accessible":
                instance.setAccessible(parser.getBooleanValue());
                break;
            case "browse":
                instance.setBrowse(parser.getBooleanValue());
                break;
            case "category":
                instance.setCategory(parser.getText());
                break;
            case "code":
                instance.setCode(parser.getText());
                break;
            case "elementActions_elementAction":
                // TODO: Handle the "elementActions_elementAction" property here: java.util.ArrayList
                break;
            case "elementName":
                instance.setElementName(parser.getText());
                break;
            case "listingActions_listingAction":
                // TODO: Handle the "listingActions_listingAction" property here: java.util.ArrayList
                break;
            case "plural":
                instance.setPlural(parser.getText());
                break;
            case "preLoad":
                instance.setPreLoad(parser.getBooleanValue());
                break;
            case "primarySecurityFields_primarySecurityField":
                // TODO: Handle the "primarySecurityFields_primarySecurityField" property here: java.util.ArrayList
                break;
            case "quarantine":
                instance.setQuarantine(parser.getBooleanValue());
                break;
            case "searchable":
                instance.setSearchable(parser.getBooleanValue());
                break;
            case "secondaryPassword":
                instance.setSecondaryPassword(parser.getBooleanValue());
                break;
            case "secure":
                instance.setSecure(parser.getBooleanValue());
                break;
            case "secureCreate":
                instance.setSecureCreate(parser.getBooleanValue());
                break;
            case "secureDelete":
                instance.setSecureDelete(parser.getBooleanValue());
                break;
            case "secureEdit":
                instance.setSecureEdit(parser.getBooleanValue());
                break;
            case "secureIp":
                instance.setSecureIp(parser.getBooleanValue());
                break;
            case "secureRead":
                instance.setSecureRead(parser.getBooleanValue());
                break;
            case "sequence":
                instance.setSequence(parser.getIntValue());
                break;
            case "singular":
                instance.setSingular(parser.getText());
                break;
            case "usage":
                instance.setUsage(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

