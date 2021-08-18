package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ValProtocoldataSerializer<T extends ValProtocoldata> extends XnatImageassessordataSerializer<T> {
    private static final long serialVersionUID = -6607648408518831322L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataSerializer() {
        this((Class<T>) ValProtocoldata.class);
    }

    protected ValProtocoldataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "check_additionalval" property here: org.nrg.xdat.om.ValAdditionalval
        // TODO: Write out the "check_comments_comment" property here: java.util.List
        // TODO: Write out the "check_conditions_condition" property here: java.util.List
        writeNonBlankField(generator, "check_status", instance.getCheck_status());
        // TODO: Write out the "scans_scanCheck" property here: java.util.List
        super.serializeImpl(instance, generator, provider);
    }
}

