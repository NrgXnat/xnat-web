package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXcvsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatXcvsessiondataSerializer<T extends XnatXcvsessiondata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 8082667864102201550L;

    @SuppressWarnings("unchecked")
    public XnatXcvsessiondataSerializer() {
        this((Class<T>) XnatXcvsessiondata.class);
    }

    protected XnatXcvsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagesessiondata" property here: org.nrg.xdat.om.XnatImagesessiondata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

