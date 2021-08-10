package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUserGroupid;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatUserGroupidDeserializer<T extends XdatUserGroupid> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 2193934431769182115L;

    @SuppressWarnings("unchecked")
    public XdatUserGroupidDeserializer() {
        this((Class<T>) XdatUserGroupid.class);
    }

    public XdatUserGroupidDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "groupid":
                // TODO: Handle the "groupid" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xdatUserGroupidId":
                // TODO: Handle the "xdatUserGroupidId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

