package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAygtssdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAygtssdataSerializer<T extends XnatAygtssdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6774888516925866335L;

    @SuppressWarnings("unchecked")
    public XnatAygtssdataSerializer() {
        this((Class<T>) XnatAygtssdata.class);
    }

    protected XnatAygtssdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "filledoutby" property here: String
        // TODO: Write out the "impairment" property here: Integer
        // TODO: Write out the "motor_complexity" property here: Integer
        // TODO: Write out the "motor_frequency" property here: Integer
        // TODO: Write out the "motor_intensity" property here: Integer
        // TODO: Write out the "motor_interference" property here: Integer
        // TODO: Write out the "motor_inventory" property here: String
        // TODO: Write out the "motor_number" property here: Integer
        // TODO: Write out the "period" property here: String
        // TODO: Write out the "phonic_complexity" property here: Integer
        // TODO: Write out the "phonic_frequency" property here: Integer
        // TODO: Write out the "phonic_intensity" property here: Integer
        // TODO: Write out the "phonic_interference" property here: Integer
        // TODO: Write out the "phonic_inventory" property here: String
        // TODO: Write out the "phonic_number" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
        // TODO: Write out the "worsteverage" property here: Double
    }
}

