package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatFieldMappingSet;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatFieldMappingSetSerializer<T extends XdatFieldMappingSet> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2577127607402373824L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatFieldMappingSetSerializer() {
        this((Class<T>) XdatFieldMappingSet.class);
    }

    protected XdatFieldMappingSetSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "allow" property here: java.util.ArrayList
        writeNonBlankField(generator, "method", instance.getMethod());
        // TODO: Write out the "subSet" property here: java.util.ArrayList
        writeNonNullNumber(generator, "xdatFieldMappingSetId", instance.getXdatFieldMappingSetId());
    }
}

