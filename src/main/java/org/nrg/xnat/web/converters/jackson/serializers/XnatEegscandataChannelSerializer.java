package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandataChannel;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatEegscandataChannelSerializer<T extends XnatEegscandataChannel> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2693166474767927399L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEegscandataChannelSerializer() {
        this((Class<T>) XnatEegscandataChannel.class);
    }

    protected XnatEegscandataChannelSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "highcutoff", instance.getHighcutoff());
        writeNonBlankField(generator, "lowcutoff", instance.getLowcutoff());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonBlankField(generator, "notch", instance.getNotch());
        writeNonNullNumber(generator, "resolution", instance.getResolution());
        writeNonNullNumber(generator, "xnatEegscandataChannelId", instance.getXnatEegscandataChannelId());
    }
}

