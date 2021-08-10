package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironmentParameter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WrkXnatexecutionenvironmentParameterSerializer<T extends WrkXnatexecutionenvironmentParameter> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1413583869102755504L;

    @SuppressWarnings("unchecked")
    public WrkXnatexecutionenvironmentParameterSerializer() {
        this((Class<T>) WrkXnatexecutionenvironmentParameter.class);
    }

    protected WrkXnatexecutionenvironmentParameterSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "parameter" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "wrkXnatexecutionenvironmentParameterId" property here: Integer
    }
}

