package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetscandataFrame;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatPetscandataFrameDeserializer<T extends XnatPetscandataFrame> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3434776839358617981L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetscandataFrameDeserializer() {
        this((Class<T>) XnatPetscandataFrame.class);
    }

    protected XnatPetscandataFrameDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "length":
                instance.setLength(parser.getDoubleValue());
                break;
            case "number":
                // TODO: Handle the "number" property here: Object
                break;
            case "starttime":
                instance.setStarttime(parser.getDoubleValue());
                break;
            case "units":
                instance.setUnits(parser.getText());
                break;
            case "xnatPetscandataFrameId":
                instance.setXnatPetscandataFrameId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

