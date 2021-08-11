package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdataScanSlice;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatQcassessmentdataScanSliceSerializer<T extends XnatQcassessmentdataScanSlice> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1123323900903533648L;

    @SuppressWarnings("unchecked")
    public XnatQcassessmentdataScanSliceSerializer() {
        this((Class<T>) XnatQcassessmentdataScanSlice.class);
    }

    protected XnatQcassessmentdataScanSliceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "number" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "slicestatistics" property here: org.nrg.xdat.model.XnatAbstractstatisticsI
        // TODO: Write out the "xnatQcassessmentdataScanSliceId" property here: Integer
    }
}

