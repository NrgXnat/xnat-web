package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatEegscandataDeserializer<T extends XnatEegscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 4399647718364032172L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEegscandataDeserializer() {
        this((Class<T>) XnatEegscandata.class);
    }

    protected XnatEegscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "channels_channel":
                // TODO: Handle the "channels_channel" property here: java.util.List
                break;
            case "parameters_datarecord_duration":
                instance.setParameters_datarecord_duration(parser.getDoubleValue());
                break;
            case "parameters_datarecord_units":
                instance.setParameters_datarecord_units(parser.getText());
                break;
            case "parameters_numberofdatarecords":
                instance.setParameters_numberofdatarecords(parser.getIntValue());
                break;
            case "softwarefiltersimpedances_impedance":
                // TODO: Handle the "softwarefiltersimpedances_impedance" property here: java.util.List
                break;
            case "softwarefiltersimpedances_mean":
                instance.setSoftwarefiltersimpedances_mean(parser.getDoubleValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

