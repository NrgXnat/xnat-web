package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatMrsessiondataDeserializer<T extends XnatMrsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 6343007508302993389L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMrsessiondataDeserializer() {
        this((Class<T>) XnatMrsessiondata.class);
    }

    protected XnatMrsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "coil":
                instance.setCoil(parser.getText());
                break;
            case "fieldStrength":
                instance.setFieldstrength(parser.getText());
                break;
            case "marker":
                instance.setMarker(parser.getText());
                break;
            case "stabilization":
                instance.setStabilization(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
