package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEpsscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatEpsscandataSerializer<T extends XnatEpsscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1549014633644978607L;

    @SuppressWarnings("unchecked")
    public XnatEpsscandataSerializer() {
        this((Class<T>) XnatEpsscandata.class);
    }

    protected XnatEpsscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

