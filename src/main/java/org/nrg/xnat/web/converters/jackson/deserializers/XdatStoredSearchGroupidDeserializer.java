package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatStoredSearchGroupid;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatStoredSearchGroupidDeserializer<T extends XdatStoredSearchGroupid> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -4735400088397139479L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatStoredSearchGroupidDeserializer() {
        this((Class<T>) XdatStoredSearchGroupid.class);
    }

    protected XdatStoredSearchGroupidDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "groupid":
                instance.setGroupid(parser.getText());
                break;
            case "xdatStoredSearchGroupidId":
                instance.setXdatStoredSearchGroupidId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

