package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdataAlias;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatProjectdataAliasDeserializer<T extends XnatProjectdataAlias> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5251512483300154595L;

    @SuppressWarnings("unchecked")
    public XnatProjectdataAliasDeserializer() {
        this((Class<T>) XnatProjectdataAlias.class);
    }

    public XnatProjectdataAliasDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "alias":
                // TODO: Handle the "alias" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "source":
                // TODO: Handle the "source" property here: String
                break;
            case "xnatProjectdataAliasId":
                // TODO: Handle the "xnatProjectdataAliasId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

