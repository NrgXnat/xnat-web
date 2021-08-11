package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGmvsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatGmvsessiondataSerializer<T extends XnatGmvsessiondata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1318854063380174127L;

    @SuppressWarnings("unchecked")
    public XnatGmvsessiondataSerializer() {
        this((Class<T>) XnatGmvsessiondata.class);
    }

    protected XnatGmvsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagesessiondata" property here: org.nrg.xdat.om.XnatImagesessiondata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

