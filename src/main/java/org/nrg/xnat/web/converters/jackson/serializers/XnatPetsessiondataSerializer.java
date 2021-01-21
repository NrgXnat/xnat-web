package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.om.XnatPetsessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatPetsessiondataSerializer extends AbstractBaseElementSerializer<XnatPetsessiondata> {
    public XnatPetsessiondataSerializer() {
        super(XnatPetsessiondata.class);
    }

    @Override
    protected void serializeImpl(final XnatPetsessiondata xnatPetsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonBlankField(generator, "id", xnatPetsessiondata.getId());
        writeNonBlankField(generator, "label", xnatPetsessiondata.getLabel());
        writeNonBlankField(generator, "project", xnatPetsessiondata.getProject());
        writeNonBlankField(generator, "note", xnatPetsessiondata.getNote());
        writeNonBlankField(generator, "protocol", xnatPetsessiondata.getProtocol());
        writeNonBlankField(generator, "original", xnatPetsessiondata.getOriginal());
        writeNonNullField(generator, "date", xnatPetsessiondata.getDate());
        writeNonNullNumber(generator, "delay", xnatPetsessiondata.getDelay());
        writeNonNullNumber(generator, "version", xnatPetsessiondata.getVersion());
        writeNonBlankField(generator, "acquisitionSite", xnatPetsessiondata.getAcquisitionSite());
        writeNonBlankField(generator, "visit", xnatPetsessiondata.getVisit());
        writeNonBlankField(generator, "visitId", xnatPetsessiondata.getVisitId());
        writeNonBlankField(generator, "description", xnatPetsessiondata.getDescription());
        writeNonNullField(generator, "subjectId", xnatPetsessiondata.getSubjectId());
       // writeNonNullField(generator, "experiment", xnatPetsessiondata.getExperimentdata());
    }

}
