package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDx3dcraniofacialscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDx3dcraniofacialscandataSerializer<T extends XnatDx3dcraniofacialscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2621750426573766898L;

    @SuppressWarnings("unchecked")
    public XnatDx3dcraniofacialscandataSerializer() {
        this((Class<T>) XnatDx3dcraniofacialscandata.class);
    }

    protected XnatDx3dcraniofacialscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

