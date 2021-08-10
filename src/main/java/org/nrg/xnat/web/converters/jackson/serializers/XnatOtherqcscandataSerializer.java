package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOtherqcscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatOtherqcscandataSerializer<T extends XnatOtherqcscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3968516651208111400L;

    @SuppressWarnings("unchecked")
    public XnatOtherqcscandataSerializer() {
        this((Class<T>) XnatOtherqcscandata.class);
    }

    protected XnatOtherqcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "other" property here: String
        // TODO: Write out the "qcscandata" property here: org.nrg.xdat.om.XnatQcscandata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

