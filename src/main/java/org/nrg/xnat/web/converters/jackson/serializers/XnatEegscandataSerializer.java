package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatEegscandataSerializer<T extends XnatEegscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 6423230778388069203L;

    @SuppressWarnings({"unchecked", "unused"})
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
        writeNonNullNumber(generator, "parameters_datarecord_duration", instance.getParameters_datarecord_duration());
        writeNonBlankField(generator, "parameters_datarecord_units", instance.getParameters_datarecord_units());
        writeNonNullNumber(generator, "parameters_numberofdatarecords", instance.getParameters_numberofdatarecords());
        // TODO: Write out the "softwarefiltersimpedances_impedance" property here: java.util.List
        writeNonNullNumber(generator, "softwarefiltersimpedances_mean", instance.getSoftwarefiltersimpedances_mean());
        super.serializeImpl(instance, generator, provider);
    }
}

