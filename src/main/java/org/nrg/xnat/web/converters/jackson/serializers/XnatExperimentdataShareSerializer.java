package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdataShare;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatExperimentdataShareSerializer<T extends XnatExperimentdataShare> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 3246369647669211355L;

    @SuppressWarnings("unchecked")
    public XnatExperimentdataShareSerializer() {
        this((Class<T>) XnatExperimentdataShare.class);
    }

    protected XnatExperimentdataShareSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T xnatExperimentdataShare, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "label", xnatExperimentdataShare.getLabel());
        generator.writeObjectField("visit", xnatExperimentdataShare.getVisit());
        writeNonBlankField(generator, "project", xnatExperimentdataShare.getProject());
        writeNonBlankField(generator, "protcol", xnatExperimentdataShare.getProtocol());
    }
}
