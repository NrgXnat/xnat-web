package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdataAlias;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatProjectdataAliasDeserializer<T extends XnatProjectdataAlias> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4457085762253594385L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatProjectdataAliasDeserializer() {
        this((Class<T>) XnatProjectdataAlias.class);
    }

    protected XnatProjectdataAliasDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "alias":
                instance.setAlias(parser.getText());
                break;
            case "source":
                instance.setSource(parser.getText());
                break;
            case "xnatProjectdataAliasId":
                instance.setXnatProjectdataAliasId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

