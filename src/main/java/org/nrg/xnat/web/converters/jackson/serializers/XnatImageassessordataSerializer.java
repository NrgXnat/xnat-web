package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImageassessordata;

import java.io.IOException;

@Slf4j
public abstract class XnatImageassessordataSerializer<T extends XnatImageassessordata> extends XnatDeriveddataSerializer<T> {
    private static final long serialVersionUID = 8256008207344888109L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatImageassessordataSerializer() {
        this((Class<T>) XnatImageassessordata.class);
    }

    protected XnatImageassessordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "imagesessionId", instance.getImagesessionId());
        // TODO: Write out the "in_file" property here: java.util.List
        // TODO: Write out the "out_file" property here: java.util.List
        // TODO: Write out the "parameters_addparam" property here: java.util.List
        super.serializeImpl(instance, generator, provider);
    }
}

