package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatUsscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatUsscandataSerializer<T extends XnatUsscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -685853847365582507L;

    @SuppressWarnings("unchecked")
    public XnatUsscandataSerializer() {
        this((Class<T>) XnatUsscandata.class);
    }

    protected XnatUsscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

