package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdataScan;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatQcassessmentdataScanDeserializer<T extends XnatQcassessmentdataScan> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2342048693815397796L;

    @SuppressWarnings("unchecked")
    public XnatQcassessmentdataScanDeserializer() {
        this((Class<T>) XnatQcassessmentdataScan.class);
    }

    public XnatQcassessmentdataScanDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "scanstatistics":
                // TODO: Handle the "scanstatistics" property here: org.nrg.xdat.om.XnatAbstractstatistics
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "sliceqc_slice":
                // TODO: Handle the "sliceqc_slice" property here: java.util.List
                break;
            case "summary":
                // TODO: Handle the "summary" property here: String
                break;
            case "xnatQcassessmentdataScanId":
                // TODO: Handle the "xnatQcassessmentdataScanId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

