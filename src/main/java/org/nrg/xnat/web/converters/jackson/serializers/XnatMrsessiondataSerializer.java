package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrsessiondata;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

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
    protected void serializeImpl(final XnatMrsessiondata xnatMrsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(xnatMrsessiondata, generator, provider);
        writeNonBlankField(generator, "coil", xnatMrsessiondata.getCoil());
        writeNonBlankField(generator, "fieldStrength", xnatMrsessiondata.getFieldstrength());
        writeNonBlankField(generator, "marker", xnatMrsessiondata.getMarker());
        writeNonBlankField(generator, "stabilization", xnatMrsessiondata.getStabilization());
    }
}
