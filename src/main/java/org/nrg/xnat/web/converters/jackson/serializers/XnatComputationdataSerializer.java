package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatComputationdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatComputationdataSerializer<T extends XnatComputationdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 8829193100931990770L;

    @SuppressWarnings("unchecked")
    public XnatComputationdataSerializer() {
        this((Class<T>) XnatComputationdata.class);
    }

    protected XnatComputationdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "source" property here: String
        // TODO: Write out the "units" property here: String
        // TODO: Write out the "value" property here: String
        // TODO: Write out the "xnatComputationdataId" property here: Integer
    }
}

