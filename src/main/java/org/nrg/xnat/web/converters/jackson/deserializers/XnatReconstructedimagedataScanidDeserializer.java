package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatReconstructedimagedataScanid;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatReconstructedimagedataScanidDeserializer<T extends XnatReconstructedimagedataScanid> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1463963090794501745L;

    @SuppressWarnings("unchecked")
    public XnatReconstructedimagedataScanidDeserializer() {
        this((Class<T>) XnatReconstructedimagedataScanid.class);
    }

    public XnatReconstructedimagedataScanidDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "scanid":
                // TODO: Handle the "scanid" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatReconstructedimagedataScanidId":
                // TODO: Handle the "xnatReconstructedimagedataScanidId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

