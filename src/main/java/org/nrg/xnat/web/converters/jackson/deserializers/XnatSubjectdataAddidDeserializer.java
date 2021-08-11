package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectdataAddid;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectdataAddidDeserializer<T extends XnatSubjectdataAddid> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 600544670911188619L;

    @SuppressWarnings("unchecked")
    public XnatSubjectdataAddidDeserializer() {
        this((Class<T>) XnatSubjectdataAddid.class);
    }

    public XnatSubjectdataAddidDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "addid":
                // TODO: Handle the "addid" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatSubjectdataAddidId":
                // TODO: Handle the "xnatSubjectdataAddidId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

