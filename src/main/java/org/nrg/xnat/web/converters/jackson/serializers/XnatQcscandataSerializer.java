package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcscandata;

import java.io.IOException;

@Slf4j
public abstract class XnatQcscandataSerializer<T extends XnatQcscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3421222833329689425L;

    @SuppressWarnings("unchecked")
    public XnatQcscandataSerializer() {
        this((Class<T>) XnatQcscandata.class);
    }

    protected XnatQcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "comments" property here: String
        // TODO: Write out the "coverage" property here: String
        // TODO: Write out the "fields_field" property here: java.util.List
        // TODO: Write out the "imagescanId" property here: String
        // TODO: Write out the "motion" property here: String
        // TODO: Write out the "otherimageartifacts" property here: String
        // TODO: Write out the "pass" property here: String
        // TODO: Write out the "rater" property here: String
        // TODO: Write out the "rating" property here: String
        // TODO: Write out the "rating_scale" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "summary" property here: String
        // TODO: Write out the "xnatQcscandataId" property here: Integer
    }
}

