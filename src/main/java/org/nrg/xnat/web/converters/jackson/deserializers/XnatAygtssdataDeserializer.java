package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAygtssdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatAygtssdataDeserializer<T extends XnatAygtssdata> extends XnatSubjectassessordataDeserializer<T> {
    private static final long serialVersionUID = -2465946895610525808L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAygtssdataDeserializer() {
        this((Class<T>) XnatAygtssdata.class);
    }

    protected XnatAygtssdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "filledoutby":
                instance.setFilledoutby(parser.getText());
                break;
            case "impairment":
                instance.setImpairment(parser.getIntValue());
                break;
            case "motor_complexity":
                instance.setMotor_complexity(parser.getIntValue());
                break;
            case "motor_frequency":
                instance.setMotor_frequency(parser.getIntValue());
                break;
            case "motor_intensity":
                instance.setMotor_intensity(parser.getIntValue());
                break;
            case "motor_interference":
                instance.setMotor_interference(parser.getIntValue());
                break;
            case "motor_inventory":
                instance.setMotor_inventory(parser.getText());
                break;
            case "motor_number":
                instance.setMotor_number(parser.getIntValue());
                break;
            case "period":
                instance.setPeriod(parser.getText());
                break;
            case "phonic_complexity":
                instance.setPhonic_complexity(parser.getIntValue());
                break;
            case "phonic_frequency":
                instance.setPhonic_frequency(parser.getIntValue());
                break;
            case "phonic_intensity":
                instance.setPhonic_intensity(parser.getIntValue());
                break;
            case "phonic_interference":
                instance.setPhonic_interference(parser.getIntValue());
                break;
            case "phonic_inventory":
                instance.setPhonic_inventory(parser.getText());
                break;
            case "phonic_number":
                instance.setPhonic_number(parser.getIntValue());
                break;
            case "worsteverage":
                instance.setWorsteverage(parser.getDoubleValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

