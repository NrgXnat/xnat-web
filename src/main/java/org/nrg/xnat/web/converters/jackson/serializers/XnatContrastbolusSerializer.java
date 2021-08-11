package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatContrastbolus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatContrastbolusSerializer<T extends XnatContrastbolus> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -9145785325928739572L;

    @SuppressWarnings("unchecked")
    public XnatContrastbolusSerializer() {
        this((Class<T>) XnatContrastbolus.class);
    }

    protected XnatContrastbolusSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "agent", instance.getAgent());
        writeNonBlankField(generator, "route", instance.getRoute());
        writeNonNullNumber(generator, "volume", instance.getVolume());
        writeNonNullNumber(generator, "totalDose", instance.getTotaldose());
        writeNonNullNumber(generator, "flowRate", instance.getFlowrate());
        writeNonNullNumber(generator, "flowDuration", instance.getFlowduration());
        writeNonBlankField(generator, "activeIngredient", instance.getActiveingredient());
        writeNonNullNumber(generator, "concentration", instance.getConcentration());
    }
}

