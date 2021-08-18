package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrqcscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatMrqcscandataDeserializer<T extends XnatMrqcscandata> extends XnatQcscandataDeserializer<T> {
    private static final long serialVersionUID = 4243732266216672913L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMrqcscandataDeserializer() {
        this((Class<T>) XnatMrqcscandata.class);
    }

    protected XnatMrqcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "blurring":
                instance.setBlurring(parser.getText());
                break;
            case "flow":
                instance.setFlow(parser.getText());
                break;
            case "imagecontrast":
                instance.setImagecontrast(parser.getText());
                break;
            case "inhomogeneity":
                instance.setInhomogeneity(parser.getText());
                break;
            case "interpacmotion":
                instance.setInterpacmotion(parser.getText());
                break;
            case "susceptibility":
                instance.setSusceptibility(parser.getText());
                break;
            case "wrap":
                instance.setWrap(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

