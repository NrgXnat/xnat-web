package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironmentParameter;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class WrkXnatexecutionenvironmentParameterSerializer<T extends WrkXnatexecutionenvironmentParameter> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1413583869102755504L;

    @SuppressWarnings({"unchecked", "unused"})
    public WrkXnatexecutionenvironmentParameterSerializer() {
        this((Class<T>) WrkXnatexecutionenvironmentParameter.class);
    }

    protected WrkXnatexecutionenvironmentParameterSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "name", instance.getName());
        writeNonBlankField(generator, "parameter", instance.getParameter());
        writeNonNullNumber(generator, "wrkXnatexecutionenvironmentParameterId", instance.getWrkXnatexecutionenvironmentParameterId());
    }
}

