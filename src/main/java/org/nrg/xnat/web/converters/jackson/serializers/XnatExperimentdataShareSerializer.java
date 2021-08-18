package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdataShare;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatExperimentdataShareSerializer<T extends XnatExperimentdataShare> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -4471877867170777957L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatExperimentdataShareSerializer() {
        this((Class<T>) XnatExperimentdataShare.class);
    }

    protected XnatExperimentdataShareSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "label", instance.getLabel());
        writeNonBlankField(generator, "project", instance.getProject());
        writeNonBlankField(generator, "protocol", instance.getProtocol());
        writeNonBlankField(generator, "share", instance.getShare());
        writeNonBlankField(generator, "visit", instance.getVisit());
        writeNonNullNumber(generator, "xnatExperimentdataShareId", instance.getXnatExperimentdataShareId());
    }
}

