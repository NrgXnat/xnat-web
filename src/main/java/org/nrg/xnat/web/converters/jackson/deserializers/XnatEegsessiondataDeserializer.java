package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatEegsessiondataDeserializer<T extends XnatEegsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = -6280519770190426813L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEegsessiondataDeserializer() {
        this((Class<T>) XnatEegsessiondata.class);
    }

    protected XnatEegsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "dataformatversion":
                instance.setDataformatversion(parser.getText());
                break;
            case "numberofchannels":
                instance.setNumberofchannels(parser.getIntValue());
                break;
            case "samplinginterval":
                instance.setSamplinginterval(parser.getDoubleValue());
                break;
            case "samplinginterval_units":
                instance.setSamplinginterval_units(parser.getText());
                break;
            case "samplingrate":
                instance.setSamplingrate(parser.getDoubleValue());
                break;
            case "samplingrate_units":
                instance.setSamplingrate_units(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

