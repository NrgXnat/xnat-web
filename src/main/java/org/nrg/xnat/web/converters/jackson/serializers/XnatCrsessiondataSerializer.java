package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatCrsessiondataSerializer<T extends XnatCrsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 8658472985438578233L;

    @SuppressWarnings("unchecked")
    public XnatCrsessiondataSerializer() {
        this((Class<T>) XnatCrsessiondata.class);
    }

    protected XnatCrsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final XnatCrsessiondata xnatCrsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(xnatCrsessiondata, generator, provider);
        writeNonBlankField(generator, "id", xnatCrsessiondata.getId());
        writeNonBlankField(generator, "label", xnatCrsessiondata.getLabel());
        writeNonBlankField(generator, "project", xnatCrsessiondata.getProject());
        writeNonBlankField(generator, "note", xnatCrsessiondata.getNote());
        writeNonBlankField(generator, "protocol", xnatCrsessiondata.getProtocol());
        writeNonBlankField(generator, "original", xnatCrsessiondata.getOriginal());
        writeNonNullField(generator, "date", xnatCrsessiondata.getDate());
        writeNonNullNumber(generator, "delay", xnatCrsessiondata.getDelay());
        writeNonNullNumber(generator, "version", xnatCrsessiondata.getVersion());
        writeNonBlankField(generator, "acquisitionSite", xnatCrsessiondata.getAcquisitionSite());
        writeNonBlankField(generator, "visit", xnatCrsessiondata.getVisit());
        writeNonBlankField(generator, "visitId", xnatCrsessiondata.getVisitId());
        writeNonBlankField(generator, "description", xnatCrsessiondata.getDescription());
        writeNonNullField(generator, "subjectId", xnatCrsessiondata.getSubjectId());
        writeNonNullField(generator, "scans", xnatCrsessiondata.getScans_scan());
    }
}
