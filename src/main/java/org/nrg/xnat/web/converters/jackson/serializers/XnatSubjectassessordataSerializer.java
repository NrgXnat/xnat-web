package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatSubjectassessordataSerializer extends AbstractBaseElementSerializer<XnatSubjectassessordata> {
    public XnatSubjectassessordataSerializer() {
        super(XnatSubjectassessordata.class);
    }

    @Override
    protected void serializeImpl(final XnatSubjectassessordata xnatSubjectassessordata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonBlankField(generator, "id", xnatSubjectassessordata.getId());
        writeNonBlankField(generator, "label", xnatSubjectassessordata.getLabel());
        writeNonBlankField(generator, "project", xnatSubjectassessordata.getProject());
        writeNonBlankField(generator, "xsiType", xnatSubjectassessordata.getXSIType());
        writeNonBlankField(generator, "note", xnatSubjectassessordata.getNote());
        writeNonBlankField(generator, "protocol", xnatSubjectassessordata.getProtocol());
        writeNonBlankField(generator, "original", xnatSubjectassessordata.getOriginal());
        writeNonNullField(generator, "date", xnatSubjectassessordata.getDate());
        writeNonNullNumber(generator, "delay", xnatSubjectassessordata.getDelay());
        writeNonNullNumber(generator, "version", xnatSubjectassessordata.getVersion());
        writeNonBlankField(generator, "acquisitionSite", xnatSubjectassessordata.getAcquisitionSite());
        writeNonBlankField(generator, "visit", xnatSubjectassessordata.getVisit());
        writeNonBlankField(generator, "visitId", xnatSubjectassessordata.getVisitId());
        writeNonBlankField(generator, "description", xnatSubjectassessordata.getDescription());
        writeNonNullField(generator, "subjectId", xnatSubjectassessordata.getSubjectId());
       // writeNonNullField(generator, "experiment", xnatSubjectassessordata.getExperimentdata());
    }

}
