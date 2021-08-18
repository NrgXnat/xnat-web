package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdataScan;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatQcassessmentdataScanSerializer<T extends XnatQcassessmentdataScan> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -4045403614105507451L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcassessmentdataScanSerializer() {
        this((Class<T>) XnatQcassessmentdataScan.class);
    }

    protected XnatQcassessmentdataScanSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "id", instance.getId());
        // TODO: Write out the "scanstatistics" property here: org.nrg.xdat.om.XnatAbstractstatistics
        // TODO: Write out the "sliceqc_slice" property here: java.util.List
        writeNonNullNumber(generator, "xnatQcassessmentdataScanId", instance.getXnatQcassessmentdataScanId());
    }
}

