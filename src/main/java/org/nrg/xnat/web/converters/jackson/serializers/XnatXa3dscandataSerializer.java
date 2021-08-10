package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXa3dscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatXa3dscandataSerializer<T extends XnatXa3dscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4492103680111135670L;

    @SuppressWarnings("unchecked")
    public XnatXa3dscandataSerializer() {
        this((Class<T>) XnatXa3dscandata.class);
    }

    protected XnatXa3dscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

