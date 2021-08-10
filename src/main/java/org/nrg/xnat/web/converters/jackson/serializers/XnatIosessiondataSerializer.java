package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatIosessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatIosessiondataSerializer<T extends XnatIosessiondata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6506791229715947438L;

    @SuppressWarnings("unchecked")
    public XnatIosessiondataSerializer() {
        this((Class<T>) XnatIosessiondata.class);
    }

    protected XnatIosessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagesessiondata" property here: org.nrg.xdat.om.XnatImagesessiondata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

