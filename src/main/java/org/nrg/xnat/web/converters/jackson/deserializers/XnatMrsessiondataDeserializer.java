package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatMrsessiondataDeserializer<T extends XnatMrsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 8714542973804658983L;

    @SuppressWarnings("unchecked")
    public XnatMrsessiondataDeserializer() {
        super((Class<T>) XnatMrsessiondata.class);
    }

    public XnatMrsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "coil":
                parser.nextToken();  //move to next token in string
                instance.setCoil(parser.getText());
                break;
            case "fieldStrength":
                parser.nextToken();  //move to next token in string
                instance.setFieldstrength(parser.getText());
                break;
            case "marker":
                parser.nextToken();  //move to next token in string
                instance.setMarker(parser.getText());
                break;
            case "stabilization":
                parser.nextToken();  //move to next token in string
                instance.setStabilization(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
