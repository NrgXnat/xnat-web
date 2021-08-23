package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.model.XnatRegionresourceLabelI;
import org.nrg.xdat.om.XnatAbstractdemographicdata;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatRegionresource;
import org.nrg.xdat.om.XnatRegionresourceLabel;
import org.nrg.xft.ItemI;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatRegionresourceDeserializer<T extends XnatRegionresource> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -6384721418363550725L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRegionresourceDeserializer() {
        this((Class<T>) XnatRegionresource.class);
    }

    protected XnatRegionresourceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "baseImage":
                // TODO: Handle the "baseimage" property here: org.nrg.xdat.model.XnatAbstractresourceI
            	try {
            		instance.setBaseimage((ItemI)parser.readValueAs(XnatAbstractresource.class));
            	} catch (Exception e) {
            		log.error("Tried to set a field base image in XnatRegionresource but failed", e);
            	}
                break;
            case "creatorFirstName":
                instance.setCreator_firstname(parser.getText());
                break;
            case "creatorLastName":
                instance.setCreator_lastname(parser.getText());
                break;
            case "file":
                // TODO: Handle the "file" property here: org.nrg.xdat.model.XnatAbstractresourceI
            	try {
            		instance.setFile((ItemI)parser.readValueAs(XnatAbstractresource.class));
            	} catch (Exception e) {
            		log.error("Tried to set a field base image in XnatRegionresource but failed", e);
            	}
                break;
            case "hemisphere":
                instance.setHemisphere(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "sessionId":
                instance.setSessionId(parser.getText());
                break;
            case "subregionLabels":
                // TODO: Handle the "subregionlabels_label" property here: java.util.List
            	try {
            		instance.setSubregionlabels_label((ItemI)parser.readValueAs(XnatRegionresourceLabel.class));
            	} catch (Exception e) {
            		log.error("Tried to set a field base image in XnatRegionresource but failed", e);
            	}
                break;
            case "xnatRegionresourceId":
                instance.setXnatRegionresourceId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

