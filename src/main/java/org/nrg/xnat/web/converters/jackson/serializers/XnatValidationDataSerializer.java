package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatValidationdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatValidationDataSerializer<T extends XnatValidationdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 3246369647669211355L;

    @SuppressWarnings("unchecked")
    public XnatValidationDataSerializer() {
        this((Class<T>) XnatValidationdata.class);
    }

    protected XnatValidationDataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T xnatValidationdata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "method", xnatValidationdata.getMethod());
        generator.writeObjectField("date", xnatValidationdata.getDate());
        writeNonBlankField(generator, "notes", xnatValidationdata.getNotes());
        writeNonBlankField(generator, "validated_by", xnatValidationdata.getValidatedBy());
        writeNonBlankField(generator, "status", xnatValidationdata.getStatus());
    }

}
