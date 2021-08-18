package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatComputationdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatComputationdataSerializer<T extends XnatComputationdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 8829193100931990770L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatComputationdataSerializer() {
        this((Class<T>) XnatComputationdata.class);
    }

    protected XnatComputationdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "name", instance.getName());
        writeNonBlankField(generator, "source", instance.getSource());
        writeNonBlankField(generator, "units", instance.getUnits());
        writeNonBlankField(generator, "value", instance.getValue());
        writeNonNullNumber(generator, "xnatComputationdataId", instance.getXnatComputationdataId());
    }
}

