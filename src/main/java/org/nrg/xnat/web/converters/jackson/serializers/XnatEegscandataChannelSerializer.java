package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandataChannel;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatEegscandataChannelSerializer<T extends XnatEegscandataChannel> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2693166474767927399L;

    @SuppressWarnings("unchecked")
    public XnatEegscandataChannelSerializer() {
        this((Class<T>) XnatEegscandataChannel.class);
    }

    protected XnatEegscandataChannelSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "highcutoff" property here: String
        // TODO: Write out the "lowcutoff" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "notch" property here: String
        // TODO: Write out the "resolution" property here: Double
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatEegscandataChannelId" property here: Integer
    }
}

