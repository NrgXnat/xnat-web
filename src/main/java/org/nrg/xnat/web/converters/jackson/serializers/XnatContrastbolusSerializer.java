package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatContrastbolus;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatContrastbolusSerializer<T extends XnatContrastbolus> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -9145785325928739572L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatContrastbolusSerializer() {
        this((Class<T>) XnatContrastbolus.class);
    }

    protected XnatContrastbolusSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "activeingredient", instance.getActiveingredient());
        writeNonBlankField(generator, "agent", instance.getAgent());
        writeNonNullNumber(generator, "concentration", instance.getConcentration());
        writeNonNullNumber(generator, "flowduration", instance.getFlowduration());
        writeNonNullNumber(generator, "flowrate", instance.getFlowrate());
        writeNonBlankField(generator, "route", instance.getRoute());
        writeNonNullNumber(generator, "totaldose", instance.getTotaldose());
        writeNonNullNumber(generator, "volume", instance.getVolume());
        writeNonNullNumber(generator, "xnatContrastbolusId", instance.getXnatContrastbolusId());
    }
}

