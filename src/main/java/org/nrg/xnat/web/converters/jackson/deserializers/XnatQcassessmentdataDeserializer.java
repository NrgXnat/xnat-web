package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatQcassessmentdataDeserializer<T extends XnatQcassessmentdata> extends XnatMrassessordataDeserializer<T> {
    private static final long serialVersionUID = -9055330766995158772L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcassessmentdataDeserializer() {
        this((Class<T>) XnatQcassessmentdata.class);
    }

    protected XnatQcassessmentdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "scans_scan":
                // TODO: Handle the "scans_scan" property here: java.util.List
                break;
            case "type":
                instance.setType(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

