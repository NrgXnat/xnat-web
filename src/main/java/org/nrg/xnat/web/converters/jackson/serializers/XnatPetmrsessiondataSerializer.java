package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.om.XnatPetmrsessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatPetmrsessiondataSerializer extends AbstractBaseElementSerializer<XnatPetmrsessiondata> {
    public XnatPetmrsessiondataSerializer() {
        super(XnatPetmrsessiondata.class);
    }

    @Override
    protected void serializeImpl(final XnatPetmrsessiondata xnatPetmrsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
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
