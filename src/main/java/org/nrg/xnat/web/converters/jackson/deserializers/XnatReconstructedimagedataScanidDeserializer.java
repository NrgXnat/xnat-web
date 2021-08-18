package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatReconstructedimagedataScanid;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatReconstructedimagedataScanidDeserializer<T extends XnatReconstructedimagedataScanid> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1398824391476978202L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatReconstructedimagedataScanidDeserializer() {
        this((Class<T>) XnatReconstructedimagedataScanid.class);
    }

    protected XnatReconstructedimagedataScanidDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "scanid":
                instance.setScanid(parser.getText());
                break;
            case "xnatReconstructedimagedataScanidId":
                instance.setXnatReconstructedimagedataScanidId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

