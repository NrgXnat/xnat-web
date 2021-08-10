package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatEegsessiondataSerializer<T extends XnatEegsessiondata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2219243428416879329L;

    @SuppressWarnings("unchecked")
    public XnatEegsessiondataSerializer() {
        this((Class<T>) XnatEegsessiondata.class);
    }

    protected XnatEegsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "dataformatversion" property here: String
        // TODO: Write out the "imagesessiondata" property here: org.nrg.xdat.om.XnatImagesessiondata
        // TODO: Write out the "numberofchannels" property here: Integer
        // TODO: Write out the "samplinginterval" property here: Double
        // TODO: Write out the "samplinginterval_units" property here: String
        // TODO: Write out the "samplingrate" property here: Double
        // TODO: Write out the "samplingrate_units" property here: String
        // TODO: Write out the "schemaElementName" property here: String
    }
}

