package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatValidationdata;

import java.io.IOException;
import java.util.Date;

@XnatSerializer
@Slf4j
public class XnatValidationDataSerializer<T extends XnatValidationdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -4680148921293489049L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatValidationDataSerializer() {
        this((Class<T>) XnatValidationdata.class);
    }

    protected XnatValidationDataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullNumber(generator, "xnatValidationDataId", instance.getXnatValidationdataId());
        writeNonBlankField(generator, "method", instance.getMethod());
        writeNonNullDate(generator, "date", (Date) instance.getDate());
        writeNonBlankField(generator, "notes", instance.getNotes());
        writeNonBlankField(generator, "validatedBy", instance.getValidatedBy());
        writeNonBlankField(generator, "status", instance.getStatus());
    }
}
