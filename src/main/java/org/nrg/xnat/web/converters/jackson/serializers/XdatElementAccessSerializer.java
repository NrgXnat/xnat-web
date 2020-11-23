package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.om.XdatElementAccess;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xft.exception.FieldNotFoundException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XdatElementAccessSerializer extends AbstractBaseElementSerializer<XdatElementAccess> {
	public XdatElementAccessSerializer() {
		super(XdatElementAccess.class);
	}

	@Override
	protected void serializeImpl(final XdatElementAccess elementAccess, final JsonGenerator generator,final SerializerProvider provider) throws IOException {
		writeNonNullNumber(generator, "xdatElementAccessId", elementAccess.getXdatElementAccessId());
		writeNonBlankField(generator, "elementName", elementAccess.getElementName());
		writeNonBlankField(generator, "xsiType", elementAccess.getXSIType());
	}
}