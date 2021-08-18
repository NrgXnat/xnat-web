package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ScrScreeningscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ScrScreeningscandataSerializer<T extends ScrScreeningscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5947999598407813208L;

    @SuppressWarnings({"unchecked", "unused"})
    public ScrScreeningscandataSerializer() {
        this((Class<T>) ScrScreeningscandata.class);
    }

    protected ScrScreeningscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "comments", instance.getComments());
        writeNonBlankField(generator, "imagescanId", instance.getImagescanId());
        writeNonBlankField(generator, "pass", instance.getPass());
        writeNonNullNumber(generator, "scrScreeningscandataId", instance.getScrScreeningscandataId());
    }
}

