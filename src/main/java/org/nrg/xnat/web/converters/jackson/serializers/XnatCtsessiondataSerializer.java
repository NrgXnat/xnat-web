package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtsessiondata;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class XnatCtsessiondataSerializer<T extends XnatCtsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -2317154238007680655L;

    @SuppressWarnings("unchecked")
    public XnatCtsessiondataSerializer() {
        this((Class<T>) XnatCtsessiondata.class);
    }

    protected XnatCtsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final XnatCtsessiondata xnatCtsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(xnatCtsessiondata, generator, provider);
        writeNonBlankField(generator, "id", xnatCtsessiondata.getId());
        writeNonBlankField(generator, "label", xnatCtsessiondata.getLabel());
        writeNonBlankField(generator, "project", xnatCtsessiondata.getProject());
        writeNonBlankField(generator, "note", xnatCtsessiondata.getNote());
        writeNonBlankField(generator, "protocol", xnatCtsessiondata.getProtocol());
        writeNonBlankField(generator, "original", xnatCtsessiondata.getOriginal());
        writeNonNullField(generator, "date", xnatCtsessiondata.getDate());
        writeNonNullNumber(generator, "delay", xnatCtsessiondata.getDelay());
        writeNonNullNumber(generator, "version", xnatCtsessiondata.getVersion());
        writeNonBlankField(generator, "acquisitionSite", xnatCtsessiondata.getAcquisitionSite());
        writeNonBlankField(generator, "visit", xnatCtsessiondata.getVisit());
        writeNonBlankField(generator, "visitId", xnatCtsessiondata.getVisitId());
        writeNonBlankField(generator, "description", xnatCtsessiondata.getDescription());
        writeNonNullField(generator, "subjectId", xnatCtsessiondata.getSubjectId());
        // writeNonNullField(generator, "experiment", xnatCrsessiondata.getExperimentdata());
    }

}
