package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatCrsessiondata;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatCrsessiondataSerializer extends AbstractBaseElementSerializer<XnatCrsessiondata> {
    public XnatCrsessiondataSerializer() {
        super(XnatCrsessiondata.class);
    }

    @Override
    protected void serializeImpl(final XnatCrsessiondata xnatCrsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
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
       // writeNonNullField(generator, "experiment", xnatCrsessiondata.getExperimentdata());
    }

}
