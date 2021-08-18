package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdataScan;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatQcassessmentdataScanDeserializer<T extends XnatQcassessmentdataScan> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 553732447777214604L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcassessmentdataScanDeserializer() {
        this((Class<T>) XnatQcassessmentdataScan.class);
    }

    protected XnatQcassessmentdataScanDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "scanstatistics":
                // TODO: Handle the "scanstatistics" property here: org.nrg.xdat.om.XnatAbstractstatistics
                break;
            case "sliceqc_slice":
                // TODO: Handle the "sliceqc_slice" property here: java.util.List
                break;
            case "xnatQcassessmentdataScanId":
                instance.setXnatQcassessmentdataScanId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

