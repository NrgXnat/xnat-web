package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatCtsessiondata;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatCtsessiondataSerializer extends AbstractBaseElementSerializer<XnatCtsessiondata> {
    public XnatCtsessiondataSerializer() {
        super(XnatCtsessiondata.class);
    }

    @Override
    protected void serializeImpl(final XnatCtsessiondata xnatCtsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
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
