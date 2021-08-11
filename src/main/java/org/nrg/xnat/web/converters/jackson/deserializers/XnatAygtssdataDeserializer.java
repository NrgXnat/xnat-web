package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAygtssdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAygtssdataDeserializer<T extends XnatAygtssdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7805028987763398997L;

    @SuppressWarnings("unchecked")
    public XnatAygtssdataDeserializer() {
        this((Class<T>) XnatAygtssdata.class);
    }

    public XnatAygtssdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "filledoutby":
                // TODO: Handle the "filledoutby" property here: String
                break;
            case "impairment":
                // TODO: Handle the "impairment" property here: Integer
                break;
            case "motor_complexity":
                // TODO: Handle the "motor_complexity" property here: Integer
                break;
            case "motor_frequency":
                // TODO: Handle the "motor_frequency" property here: Integer
                break;
            case "motor_intensity":
                // TODO: Handle the "motor_intensity" property here: Integer
                break;
            case "motor_interference":
                // TODO: Handle the "motor_interference" property here: Integer
                break;
            case "motor_inventory":
                // TODO: Handle the "motor_inventory" property here: String
                break;
            case "motor_number":
                // TODO: Handle the "motor_number" property here: Integer
                break;
            case "period":
                // TODO: Handle the "period" property here: String
                break;
            case "phonic_complexity":
                // TODO: Handle the "phonic_complexity" property here: Integer
                break;
            case "phonic_frequency":
                // TODO: Handle the "phonic_frequency" property here: Integer
                break;
            case "phonic_intensity":
                // TODO: Handle the "phonic_intensity" property here: Integer
                break;
            case "phonic_interference":
                // TODO: Handle the "phonic_interference" property here: Integer
                break;
            case "phonic_inventory":
                // TODO: Handle the "phonic_inventory" property here: String
                break;
            case "phonic_number":
                // TODO: Handle the "phonic_number" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "subjectassessordata":
                // TODO: Handle the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
                break;
            case "worsteverage":
                // TODO: Handle the "worsteverage" property here: Double
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

