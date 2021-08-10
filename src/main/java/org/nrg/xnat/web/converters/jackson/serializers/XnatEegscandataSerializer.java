package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatEegscandataSerializer<T extends XnatEegscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3846087960623143572L;

    @SuppressWarnings("unchecked")
    public XnatEegscandataSerializer() {
        this((Class<T>) XnatEegscandata.class);
    }

    protected XnatEegscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "channels_channel" property here: java.util.List
        // TODO: Write out the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
        // TODO: Write out the "parameters_datarecord_duration" property here: Double
        // TODO: Write out the "parameters_datarecord_units" property here: String
        // TODO: Write out the "parameters_numberofdatarecords" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "softwarefiltersimpedances_impedance" property here: java.util.List
        // TODO: Write out the "softwarefiltersimpedances_mean" property here: Double
    }
}

