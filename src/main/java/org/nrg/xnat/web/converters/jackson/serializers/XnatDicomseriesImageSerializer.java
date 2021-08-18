package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomseriesImage;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatDicomseriesImageSerializer<T extends XnatDicomseriesImage> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 220920606868893570L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDicomseriesImageSerializer() {
        this((Class<T>) XnatDicomseriesImage.class);
    }

    protected XnatDicomseriesImageSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "instanceNumber", instance.getInstanceNumber());
        writeNonBlankField(generator, "sopInstanceUid", instance.getSopInstanceUid());
        writeNonBlankField(generator, "uri", instance.getUri());
        writeNonNullNumber(generator, "xnatDicomseriesImageId", instance.getXnatDicomseriesImageId());
    }
}

