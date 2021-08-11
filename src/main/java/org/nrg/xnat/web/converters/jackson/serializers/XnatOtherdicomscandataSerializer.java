package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOtherdicomscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatOtherdicomscandataSerializer<T extends XnatOtherdicomscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5237230720940172145L;

    @SuppressWarnings("unchecked")
    public XnatOtherdicomscandataSerializer() {
        this((Class<T>) XnatOtherdicomscandata.class);
    }

    protected XnatOtherdicomscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

