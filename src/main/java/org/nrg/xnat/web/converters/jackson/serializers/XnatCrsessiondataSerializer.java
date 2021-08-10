package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class XnatCrsessiondataSerializer<T extends XnatCrsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 8658472985438578233L;

    @SuppressWarnings("unchecked")
    public XnatCrsessiondataSerializer() {
        this((Class<T>) XnatCrsessiondata.class);
    }

    protected XnatCrsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final XnatCrsessiondata xnatCrsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(xnatCrsessiondata, generator, provider);
    }
}
