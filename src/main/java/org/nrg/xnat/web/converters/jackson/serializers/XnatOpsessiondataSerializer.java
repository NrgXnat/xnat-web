package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOpsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatOpsessiondataSerializer<T extends XnatOpsessiondata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2562745812953113512L;

    @SuppressWarnings("unchecked")
    public XnatOpsessiondataSerializer() {
        this((Class<T>) XnatOpsessiondata.class);
    }

    protected XnatOpsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagesessiondata" property here: org.nrg.xdat.om.XnatImagesessiondata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

