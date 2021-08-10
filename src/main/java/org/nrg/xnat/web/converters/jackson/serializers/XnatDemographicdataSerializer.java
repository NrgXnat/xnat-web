package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDemographicdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDemographicdataSerializer<T extends XnatDemographicdata> extends XnatAbstractdemographicdataSerializer<T> {
    private static final long serialVersionUID = 17220835527191304L;

    @SuppressWarnings("unchecked")
    public XnatDemographicdataSerializer() {
        this((Class<T>) XnatDemographicdata.class);
    }

    protected XnatDemographicdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(instance, generator, provider);
        writeNonBlankField(generator, "ethnicity", instance.getEthnicity());
        writeNonBlankField(generator, "race", instance.getRace());
        writeNonBlankField(generator, "gender", instance.getGender());
        writeNonBlankField(generator, "handedness", instance.getHandedness());
        writeNonNullField(generator, "dob", instance.getDob());
        writeNonNullNumber(generator, "weight", instance.getWeight());
        writeNonNullNumber(generator, "age", instance.getAge());
        writeNonNullNumber(generator, "height", instance.getHeight());
        writeNonNullNumber(generator, "ses", instance.getSes());
        writeNonBlankField(generator, "gender", instance.getGender());
        writeNonBlankField(generator, "educationDesc", instance.getEducationdesc());
        writeNonNullNumber(generator, "education", instance.getEducation());

    }
}
