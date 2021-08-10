package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdataScanSlice;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatQcassessmentdataScanSliceDeserializer<T extends XnatQcassessmentdataScanSlice> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -656053293607561479L;

    @SuppressWarnings("unchecked")
    public XnatQcassessmentdataScanSliceDeserializer() {
        this((Class<T>) XnatQcassessmentdataScanSlice.class);
    }

    public XnatQcassessmentdataScanSliceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "number":
                // TODO: Handle the "number" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "slicestatistics":
                // TODO: Handle the "slicestatistics" property here: org.nrg.xdat.model.XnatAbstractstatisticsI
                break;
            case "xnatQcassessmentdataScanSliceId":
                // TODO: Handle the "xnatQcassessmentdataScanSliceId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

