package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdataAddfield;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatStatisticsdataAddfieldSerializer<T extends XnatStatisticsdataAddfield> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 474206837182039612L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStatisticsdataAddfieldSerializer() {
        this((Class<T>) XnatStatisticsdataAddfield.class);
    }

    protected XnatStatisticsdataAddfieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "addfield", instance.getAddfield());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatStatisticsdataAddfieldId", instance.getXnatStatisticsdataAddfieldId());
    }
}

