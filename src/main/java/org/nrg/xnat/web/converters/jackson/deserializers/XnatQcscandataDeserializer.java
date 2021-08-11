package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcscandata;

import java.io.IOException;

@Slf4j
public abstract class XnatQcscandataDeserializer<T extends XnatQcscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7800770534405847949L;

    protected XnatQcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comments":
                // TODO: Handle the "comments" property here: String
                break;
            case "coverage":
                // TODO: Handle the "coverage" property here: String
                break;
            case "fields_field":
                // TODO: Handle the "fields_field" property here: java.util.List
                break;
            case "imagescanId":
                // TODO: Handle the "imagescanId" property here: String
                break;
            case "motion":
                // TODO: Handle the "motion" property here: String
                break;
            case "otherimageartifacts":
                // TODO: Handle the "otherimageartifacts" property here: String
                break;
            case "pass":
                // TODO: Handle the "pass" property here: String
                break;
            case "rater":
                // TODO: Handle the "rater" property here: String
                break;
            case "rating":
                // TODO: Handle the "rating" property here: String
                break;
            case "rating_scale":
                // TODO: Handle the "rating_scale" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "summary":
                // TODO: Handle the "summary" property here: String
                break;
            case "xnatQcscandataId":
                // TODO: Handle the "xnatQcscandataId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

