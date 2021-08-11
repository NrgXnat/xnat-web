package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEcgsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatEcgsessiondataSerializer<T extends XnatEcgsessiondata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4529729469875306254L;

    @SuppressWarnings("unchecked")
    public XnatEcgsessiondataSerializer() {
        this((Class<T>) XnatEcgsessiondata.class);
    }

    protected XnatEcgsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagesessiondata" property here: org.nrg.xdat.om.XnatImagesessiondata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

