package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUserGroupid;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatUserGroupidDeserializer<T extends XdatUserGroupid> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1624590130349251659L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatUserGroupidDeserializer() {
        this((Class<T>) XdatUserGroupid.class);
    }

    protected XdatUserGroupidDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "groupid":
                instance.setGroupid(parser.getText());
                break;
            case "xdatUserGroupidId":
                instance.setXdatUserGroupidId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

