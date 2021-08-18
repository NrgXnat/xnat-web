package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdataField;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatExperimentdataFieldSerializer<T extends XnatExperimentdataField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5035580015368884431L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatExperimentdataFieldSerializer() {
        this((Class<T>) XnatExperimentdataField.class);
    }

    protected XnatExperimentdataFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "field", instance.getField());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatExperimentdataFieldId", instance.getXnatExperimentdataFieldId());
    }
}

