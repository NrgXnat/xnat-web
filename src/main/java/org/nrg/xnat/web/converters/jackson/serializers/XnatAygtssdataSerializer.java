package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAygtssdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatAygtssdataSerializer<T extends XnatAygtssdata> extends XnatSubjectassessordataSerializer<T> {
    private static final long serialVersionUID = 7269582507610307611L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAygtssdataSerializer() {
        this((Class<T>) XnatAygtssdata.class);
    }

    protected XnatAygtssdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "filledoutby", instance.getFilledoutby());
        writeNonNullNumber(generator, "impairment", instance.getImpairment());
        writeNonNullNumber(generator, "motor_complexity", instance.getMotor_complexity());
        writeNonNullNumber(generator, "motor_frequency", instance.getMotor_frequency());
        writeNonNullNumber(generator, "motor_intensity", instance.getMotor_intensity());
        writeNonNullNumber(generator, "motor_interference", instance.getMotor_interference());
        writeNonBlankField(generator, "motor_inventory", instance.getMotor_inventory());
        writeNonNullNumber(generator, "motor_number", instance.getMotor_number());
        writeNonBlankField(generator, "period", instance.getPeriod());
        writeNonNullNumber(generator, "phonic_complexity", instance.getPhonic_complexity());
        writeNonNullNumber(generator, "phonic_frequency", instance.getPhonic_frequency());
        writeNonNullNumber(generator, "phonic_intensity", instance.getPhonic_intensity());
        writeNonNullNumber(generator, "phonic_interference", instance.getPhonic_interference());
        writeNonBlankField(generator, "phonic_inventory", instance.getPhonic_inventory());
        writeNonNullNumber(generator, "phonic_number", instance.getPhonic_number());
        writeNonNullNumber(generator, "worsteverage", instance.getWorsteverage());
        super.serializeImpl(instance, generator, provider);
    }
}

