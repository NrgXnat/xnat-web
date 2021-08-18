package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdataScanSlice;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatQcassessmentdataScanSliceDeserializer<T extends XnatQcassessmentdataScanSlice> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7958280574656563259L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcassessmentdataScanSliceDeserializer() {
        this((Class<T>) XnatQcassessmentdataScanSlice.class);
    }

    protected XnatQcassessmentdataScanSliceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "number":
                instance.setNumber(parser.getText());
                break;
            case "slicestatistics":
                // TODO: Handle the "slicestatistics" property here: org.nrg.xdat.om.XnatAbstractstatistics
                break;
            case "xnatQcassessmentdataScanSliceId":
                instance.setXnatQcassessmentdataScanSliceId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

