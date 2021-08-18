package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatQcscandataSerializer<T extends XnatQcscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1347665487634415918L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcscandataSerializer() {
        this((Class<T>) XnatQcscandata.class);
    }

    protected XnatQcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "comments", instance.getComments());
        writeNonBlankField(generator, "coverage", instance.getCoverage());
        // TODO: Write out the "fields_field" property here: java.util.List
        writeNonBlankField(generator, "imagescanId", instance.getImagescanId());
        writeNonBlankField(generator, "motion", instance.getMotion());
        writeNonBlankField(generator, "otherimageartifacts", instance.getOtherimageartifacts());
        writeNonBlankField(generator, "pass", instance.getPass());
        writeNonBlankField(generator, "rater", instance.getRater());
        writeNonBlankField(generator, "rating", instance.getRating());
        writeNonBlankField(generator, "rating_scale", instance.getRating_scale());
        writeNonNullNumber(generator, "xnatQcscandataId", instance.getXnatQcscandataId());
    }
}

