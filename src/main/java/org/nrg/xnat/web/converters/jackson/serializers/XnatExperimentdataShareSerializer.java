package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatExperimentdataShare;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatExperimentdataShareSerializer extends AbstractBaseElementSerializer<XnatExperimentdataShare> {
	private static final long serialVersionUID = 3246369647669211355L;

	public XnatExperimentdataShareSerializer() {
        super(XnatExperimentdataShare.class);
    }

    @Override
    protected void serializeImpl(final XnatExperimentdataShare xnatExperimentdataShare, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonBlankField(generator, "label", xnatExperimentdataShare.getLabel());
    	generator.writeObjectField("visit", xnatExperimentdataShare.getVisit());
		writeNonBlankField(generator, "project", xnatExperimentdataShare.getProject());
		writeNonBlankField(generator, "protcol", xnatExperimentdataShare.getProtocol());
    }

}
