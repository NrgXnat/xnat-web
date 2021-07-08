package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;

import java.io.IOException;

@Slf4j
public class XnatExperimentdataSerializer extends AbstractBaseElementSerializer<XnatExperimentdata> {
	private static final long serialVersionUID = -745536640121672797L;

	public XnatExperimentdataSerializer() {
        super(XnatExperimentdata.class);
    }

    @Override
    protected void serializeImpl(final XnatExperimentdata experiment, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "label", experiment.getLabel());
        writeNonBlankField(generator, "protocol", experiment.getProtocol());
        writeNonNullNumber(generator, "version", experiment.getVersion());
        writeNonBlankField(generator, "visit", experiment.getVisit());
        writeNonBlankField(generator, "visitId", experiment.getVisitId());
        writeNonBlankField(generator, "project", experiment.getProject());
        writeNonBlankField(generator, "id", experiment.getId());
        writeNonBlankField(generator, "xsiType", experiment.getXSIType());
        writeNonBlankField(generator, "acquisitionSite", experiment.getAcquisitionSite());
        writeNonBlankField(generator, "note", experiment.getNote());
        writeNonNullField(generator, "duration", experiment.getDuration());
        writeNonNullField(generator, "time", experiment.getTime());
        writeNonNullField(generator, "date", experiment.getDate());
        writeNonNullField(generator, "fields", experiment.getFields_field());
        writeNonNullField(generator, "resources", experiment.getResources_resource());
        writeNonNullField(generator, "sharing", experiment.getSharing_share());
        generator.writeObjectField("investigator", experiment.getInvestigator());
        generator.writeObjectField("validation", experiment.getValidation());
    }
}
