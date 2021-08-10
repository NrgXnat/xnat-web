package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrqcscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatMrqcscandataSerializer<T extends XnatMrqcscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2196774896114564349L;

    @SuppressWarnings("unchecked")
    public XnatMrqcscandataSerializer() {
        this((Class<T>) XnatMrqcscandata.class);
    }

    protected XnatMrqcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "blurring" property here: String
        // TODO: Write out the "flow" property here: String
        // TODO: Write out the "imagecontrast" property here: String
        // TODO: Write out the "inhomogeneity" property here: String
        // TODO: Write out the "interpacmotion" property here: String
        // TODO: Write out the "qcscandata" property here: org.nrg.xdat.om.XnatQcscandata
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "susceptibility" property here: String
        // TODO: Write out the "wrap" property here: String
    }
}

