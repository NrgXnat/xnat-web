package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdataScanSlice;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatQcassessmentdataScanSliceSerializer<T extends XnatQcassessmentdataScanSlice> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1123323900903533648L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcassessmentdataScanSliceSerializer() {
        this((Class<T>) XnatQcassessmentdataScanSlice.class);
    }

    protected XnatQcassessmentdataScanSliceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "number", instance.getNumber());
        // TODO: Write out the "slicestatistics" property here: org.nrg.xdat.om.XnatAbstractstatistics
        writeNonNullNumber(generator, "xnatQcassessmentdataScanSliceId", instance.getXnatQcassessmentdataScanSliceId());
    }
}

