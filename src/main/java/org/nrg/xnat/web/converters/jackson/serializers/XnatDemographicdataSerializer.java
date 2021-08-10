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
    protected void serializeImpl(final XnatDemographicdata demographic, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(demographic, generator, provider);
        writeNonBlankField(generator, "ethnicity", demographic.getEthnicity());
        writeNonBlankField(generator, "race", demographic.getRace());
        writeNonBlankField(generator, "gender", demographic.getGender());
        writeNonBlankField(generator, "handedness", demographic.getHandedness());
        writeNonNullField(generator, "dob", demographic.getDob());
        writeNonNullNumber(generator, "weight", demographic.getWeight());
        writeNonNullNumber(generator, "age", demographic.getAge());
        writeNonNullNumber(generator, "height", demographic.getHeight());
        writeNonNullNumber(generator, "ses", demographic.getSes());
        writeNonBlankField(generator, "gender", demographic.getGender());
        writeNonBlankField(generator, "educationDesc", demographic.getEducationdesc());
        writeNonNullNumber(generator, "education", demographic.getEducation());

    }
}
