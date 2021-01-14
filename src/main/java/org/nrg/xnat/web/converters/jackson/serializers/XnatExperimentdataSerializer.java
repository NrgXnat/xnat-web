package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatSubjectdata;

import java.io.IOException;
import java.util.List;

@Slf4j
public class XnatExperimentdataSerializer extends AbstractBaseElementSerializer<XnatExperimentdata> {
    public XnatExperimentdataSerializer() {
        super(XnatExperimentdata.class);
    }

    @Override
    protected void serializeImpl(final XnatExperimentdata experiment, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonBlankField(generator, "id", experiment.getId());
        writeNonBlankField(generator, "label", experiment.getLabel());
        writeNonBlankField(generator, "project", experiment.getProject());
        writeNonBlankField(generator, "xsiType", experiment.getXSIType());
        writeNonBlankField(generator, "note", experiment.getNote());
        writeNonBlankField(generator, "protocol", experiment.getProtocol());
        writeNonBlankField(generator, "original", experiment.getOriginal());
        writeNonNullField(generator, "date", experiment.getDate());
        writeNonNullNumber(generator, "delay", experiment.getDelay());
        writeNonNullNumber(generator, "version", experiment.getVersion());
        writeNonBlankField(generator, "acquisitionSite", experiment.getAcquisitionSite());
        writeNonBlankField(generator, "visit", experiment.getVisit());
        writeNonBlankField(generator, "visitId", experiment.getVisitId());
        writeNonBlankField(generator, "description", experiment.getDescription());
        writeNonNullField(generator, "fields", experiment.getFields_field());
        writeNonNullField(generator, "resources", experiment.getResources_resource());
    }
}
