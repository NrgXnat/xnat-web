package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ProvProcessstep;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ProvProcessstepSerializer<T extends ProvProcessstep> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1475175171865130132L;

    @SuppressWarnings({"unchecked", "unused"})
    public ProvProcessstepSerializer() {
        this((Class<T>) ProvProcessstep.class);
    }

    protected ProvProcessstepSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "compiler", instance.getCompiler());
        writeNonBlankField(generator, "compiler_version", instance.getCompiler_version());
        writeNonBlankField(generator, "cvs", instance.getCvs());
        // TODO: Write out the "library" property here: java.util.List
        writeNonBlankField(generator, "machine", instance.getMachine());
        writeNonBlankField(generator, "platform", instance.getPlatform());
        writeNonBlankField(generator, "platform_version", instance.getPlatform_version());
        writeNonBlankField(generator, "program", instance.getProgram());
        writeNonBlankField(generator, "program_arguments", instance.getProgram_arguments());
        writeNonBlankField(generator, "program_version", instance.getProgram_version());
        writeNonNullNumber(generator, "provProcessstepId", instance.getProvProcessstepId());
        // TODO: Write out the "timestamp" property here: Object
    }
}

