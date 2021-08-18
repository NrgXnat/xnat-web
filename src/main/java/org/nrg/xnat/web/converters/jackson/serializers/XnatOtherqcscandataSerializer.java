package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOtherqcscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatOtherqcscandataSerializer<T extends XnatOtherqcscandata> extends XnatQcscandataSerializer<T> {
    private static final long serialVersionUID = 7396103347593283597L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatOtherqcscandataSerializer() {
        this((Class<T>) XnatOtherqcscandata.class);
    }

    protected XnatOtherqcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "other", instance.getOther());
        super.serializeImpl(instance, generator, provider);
    }
}

