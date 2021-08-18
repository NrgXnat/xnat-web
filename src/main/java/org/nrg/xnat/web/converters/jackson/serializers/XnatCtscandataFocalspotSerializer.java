package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtscandataFocalspot;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatCtscandataFocalspotSerializer<T extends XnatCtscandataFocalspot> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3995839271057921292L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatCtscandataFocalspotSerializer() {
        this((Class<T>) XnatCtscandataFocalspot.class);
    }

    protected XnatCtscandataFocalspotSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "focalspot", instance.getFocalspot());
        writeNonNullNumber(generator, "xnatCtscandataFocalspotId", instance.getXnatCtscandataFocalspotId());
    }
}

