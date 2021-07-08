package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatValidationdata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatValidationDataSerializer extends AbstractBaseElementSerializer<XnatValidationdata> {
	private static final long serialVersionUID = 3246369647669211355L;

	public XnatValidationDataSerializer() {
        super(XnatValidationdata.class);
    }

    @Override
    protected void serializeImpl(final XnatValidationdata xnatValidationdata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonBlankField(generator, "method", xnatValidationdata.getMethod());
    	generator.writeObjectField("date", xnatValidationdata.getDate());
		writeNonBlankField(generator, "notes", xnatValidationdata.getNotes());
		writeNonBlankField(generator, "validated_by", xnatValidationdata.getValidatedBy());
		writeNonBlankField(generator, "status", xnatValidationdata.getStatus());
    }

}
