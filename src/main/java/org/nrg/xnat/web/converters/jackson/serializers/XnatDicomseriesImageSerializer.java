package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomseriesImage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDicomseriesImageSerializer<T extends XnatDicomseriesImage> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 220920606868893570L;

    @SuppressWarnings("unchecked")
    public XnatDicomseriesImageSerializer() {
        this((Class<T>) XnatDicomseriesImage.class);
    }

    protected XnatDicomseriesImageSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "instanceNumber" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "sopInstanceUid" property here: String
        // TODO: Write out the "xnatDicomseriesImageId" property here: Integer
    }
}

