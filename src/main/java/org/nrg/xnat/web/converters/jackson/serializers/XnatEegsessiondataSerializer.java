package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatEegsessiondataSerializer<T extends XnatEegsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 5990651366591260224L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEegsessiondataSerializer() {
        this((Class<T>) XnatEegsessiondata.class);
    }

    protected XnatEegsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "dataformatversion", instance.getDataformatversion());
        writeNonNullNumber(generator, "numberofchannels", instance.getNumberofchannels());
        writeNonNullNumber(generator, "samplinginterval", instance.getSamplinginterval());
        writeNonBlankField(generator, "samplinginterval_units", instance.getSamplinginterval_units());
        writeNonNullNumber(generator, "samplingrate", instance.getSamplingrate());
        writeNonBlankField(generator, "samplingrate_units", instance.getSamplingrate_units());
        super.serializeImpl(instance, generator, provider);
    }
}

