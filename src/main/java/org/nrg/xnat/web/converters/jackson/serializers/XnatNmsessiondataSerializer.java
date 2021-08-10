package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatNmsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatNmsessiondataSerializer<T extends XnatNmsessiondata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1412422184324739311L;

    @SuppressWarnings("unchecked")
    public XnatNmsessiondataSerializer() {
        this((Class<T>) XnatNmsessiondata.class);
    }

    protected XnatNmsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagesessiondata" property here: org.nrg.xdat.om.XnatImagesessiondata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

