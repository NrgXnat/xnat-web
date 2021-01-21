package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatMrsessiondataSerializer extends AbstractBaseElementSerializer<XnatMrsessiondata> {
    public XnatMrsessiondataSerializer() {
        super(XnatMrsessiondata.class);
    }

    @Override
    protected void serializeImpl(final XnatMrsessiondata xnatMrsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonBlankField(generator, "id", xnatMrsessiondata.getId());
        writeNonBlankField(generator, "label", xnatMrsessiondata.getLabel());
        writeNonBlankField(generator, "project", xnatMrsessiondata.getProject());
        writeNonBlankField(generator, "note", xnatMrsessiondata.getNote());
        writeNonBlankField(generator, "protocol", xnatMrsessiondata.getProtocol());
        writeNonBlankField(generator, "original", xnatMrsessiondata.getOriginal());
        writeNonNullField(generator, "date", xnatMrsessiondata.getDate());
        writeNonNullNumber(generator, "delay", xnatMrsessiondata.getDelay());
        writeNonNullNumber(generator, "version", xnatMrsessiondata.getVersion());
        writeNonBlankField(generator, "acquisitionSite", xnatMrsessiondata.getAcquisitionSite());
        writeNonBlankField(generator, "visit", xnatMrsessiondata.getVisit());
        writeNonBlankField(generator, "visitId", xnatMrsessiondata.getVisitId());
        writeNonBlankField(generator, "description", xnatMrsessiondata.getDescription());
        writeNonNullField(generator, "subjectId", xnatMrsessiondata.getSubjectId());
        writeNonNullField(generator, "scans", xnatMrsessiondata.getScans_scan());
       // writeNonNullField(generator, "experiment", xnatSubjectassessordata.getExperimentdata());
    }

}
