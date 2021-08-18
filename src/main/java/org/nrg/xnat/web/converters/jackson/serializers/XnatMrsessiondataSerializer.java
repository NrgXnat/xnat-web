package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatMrsessiondataSerializer<T extends XnatMrsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 4428724978548105980L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMrsessiondataSerializer() {
        this((Class<T>) XnatMrsessiondata.class);
    }

    protected XnatMrsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "coil", instance.getCoil());
        writeNonBlankField(generator, "fieldstrength", instance.getFieldstrength());
        writeNonBlankField(generator, "marker", instance.getMarker());
        writeNonBlankField(generator, "stabilization", instance.getStabilization());
        super.serializeImpl(instance, generator, provider);
    }
}

