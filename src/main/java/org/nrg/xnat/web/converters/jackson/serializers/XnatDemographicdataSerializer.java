package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDemographicdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatDemographicdataSerializer<T extends XnatDemographicdata> extends XnatAbstractdemographicdataSerializer<T> {
    private static final long serialVersionUID = 1968510988018549755L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDemographicdataSerializer() {
        this((Class<T>) XnatDemographicdata.class);
    }

    protected XnatDemographicdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullNumber(generator, "age", instance.getAge());
        writeNonNullNumber(generator, "birthWeight", instance.getBirthWeight());
        writeNonNullField(generator, "dob", instance.getDob());
        writeNonNullNumber(generator, "education", instance.getEducation());
        writeNonBlankField(generator, "educationDesc", instance.getEducationdesc());
        writeNonNullNumber(generator, "employment", instance.getEmployment());
        writeNonBlankField(generator, "ethnicity", instance.getEthnicity());
        writeNonBlankField(generator, "gender", instance.getGender());
        writeNonNullNumber(generator, "gestationalAge", instance.getGestationalAge());
        writeNonBlankField(generator, "handedness", instance.getHandedness());
        writeNonNullNumber(generator, "height", instance.getHeight());
        writeNonBlankField(generator, "height_units", instance.getHeight_units());
        writeNonNullNumber(generator, "postMenstrualAge", instance.getPostMenstrualAge());
        writeNonBlankField(generator, "race", instance.getRace());
        writeNonBlankField(generator, "race2", instance.getRace2());
        writeNonBlankField(generator, "race3", instance.getRace3());
        writeNonBlankField(generator, "race4", instance.getRace4());
        writeNonBlankField(generator, "race5", instance.getRace5());
        writeNonBlankField(generator, "race6", instance.getRace6());
        writeNonNullNumber(generator, "ses", instance.getSes());
        writeNonNullNumber(generator, "weight", instance.getWeight());
        writeNonBlankField(generator, "weight_units", instance.getWeight_units());
        writeNonNullNumber(generator, "yob", instance.getYob());
        super.serializeImpl(instance, generator, provider);
    }
}
