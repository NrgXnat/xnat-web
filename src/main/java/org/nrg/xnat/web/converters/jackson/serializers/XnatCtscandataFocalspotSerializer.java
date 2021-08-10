package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtscandataFocalspot;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatCtscandataFocalspotSerializer<T extends XnatCtscandataFocalspot> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3995839271057921292L;

    @SuppressWarnings("unchecked")
    public XnatCtscandataFocalspotSerializer() {
        this((Class<T>) XnatCtscandataFocalspot.class);
    }

    protected XnatCtscandataFocalspotSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        generator.writeNumber(instance.getFocalspot());
    }
}

