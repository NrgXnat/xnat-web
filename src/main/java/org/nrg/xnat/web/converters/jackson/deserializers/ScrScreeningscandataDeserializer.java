package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ScrScreeningscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ScrScreeningscandataDeserializer<T extends ScrScreeningscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5258423983436001307L;

    @SuppressWarnings({"unchecked", "unused"})
    public ScrScreeningscandataDeserializer() {
        this((Class<T>) ScrScreeningscandata.class);
    }

    protected ScrScreeningscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comments":
                instance.setComments(parser.getText());
                break;
            case "imagescanId":
                instance.setImagescanId(parser.getText());
                break;
            case "pass":
                instance.setPass(parser.getText());
                break;
            case "scrScreeningscandataId":
                instance.setScrScreeningscandataId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

