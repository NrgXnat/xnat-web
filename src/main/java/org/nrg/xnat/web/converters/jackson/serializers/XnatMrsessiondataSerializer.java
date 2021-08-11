package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatMrsessiondataSerializer<T extends XnatMrsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 7260985692118037295L;

    @SuppressWarnings("unchecked")
    public XnatMrsessiondataSerializer() {
        this((Class<T>) XnatMrsessiondata.class);
    }

    protected XnatMrsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(instance, generator, provider);
        writeNonBlankField(generator, "coil", instance.getCoil());
        writeNonBlankField(generator, "fieldStrength", instance.getFieldstrength());
        writeNonBlankField(generator, "marker", instance.getMarker());
        writeNonBlankField(generator, "stabilization", instance.getStabilization());
    }
}
