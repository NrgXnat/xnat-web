package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetqcscandataProcessingerror;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatPetqcscandataProcessingerrorDeserializer<T extends XnatPetqcscandataProcessingerror> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -328268954682757670L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetqcscandataProcessingerrorDeserializer() {
        this((Class<T>) XnatPetqcscandataProcessingerror.class);
    }

    protected XnatPetqcscandataProcessingerrorDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "processingerror":
                instance.setProcessingerror(parser.getText());
                break;
            case "xnatPetqcscandataProcessingerrorId":
                instance.setXnatPetqcscandataProcessingerrorId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

