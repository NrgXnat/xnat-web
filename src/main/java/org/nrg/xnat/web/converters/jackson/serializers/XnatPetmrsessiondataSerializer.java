package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetmrsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetmrsessiondataSerializer<T extends XnatPetmrsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -4049493112160240580L;

    @SuppressWarnings("unchecked")
    public XnatPetmrsessiondataSerializer() {
        this((Class<T>) XnatPetmrsessiondata.class);
    }

    protected XnatPetmrsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final XnatPetmrsessiondata xnatPetmrsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(xnatPetmrsessiondata, generator, provider);
        writeNonBlankField(generator, "id", xnatPetmrsessiondata.getId());
        writeNonBlankField(generator, "label", xnatPetmrsessiondata.getLabel());
        writeNonBlankField(generator, "project", xnatPetmrsessiondata.getProject());
        writeNonBlankField(generator, "note", xnatPetmrsessiondata.getNote());
        writeNonBlankField(generator, "protocol", xnatPetmrsessiondata.getProtocol());
        writeNonBlankField(generator, "original", xnatPetmrsessiondata.getOriginal());
        writeNonNullField(generator, "date", xnatPetmrsessiondata.getDate());
        writeNonNullNumber(generator, "delay", xnatPetmrsessiondata.getDelay());
        writeNonNullNumber(generator, "version", xnatPetmrsessiondata.getVersion());
        writeNonBlankField(generator, "acquisitionSite", xnatPetmrsessiondata.getAcquisitionSite());
        writeNonBlankField(generator, "visit", xnatPetmrsessiondata.getVisit());
        writeNonBlankField(generator, "visitId", xnatPetmrsessiondata.getVisitId());
        writeNonBlankField(generator, "description", xnatPetmrsessiondata.getDescription());
        writeNonNullField(generator, "subjectId", xnatPetmrsessiondata.getSubjectId());
        // writeNonNullField(generator, "experiment", xnatPetmrsessiondata.getExperimentdata());
    }

}
