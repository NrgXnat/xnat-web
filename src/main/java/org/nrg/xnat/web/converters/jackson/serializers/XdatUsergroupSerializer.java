package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XdatUsergroup;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XdatUsergroupSerializer extends AbstractBaseElementSerializer<XdatUsergroup> {
	public XdatUsergroupSerializer() {
		super(XdatUsergroup.class);
	}

	@Override
	protected void serializeImpl(final XdatUsergroup userGroup, final JsonGenerator generator,final SerializerProvider provider) throws IOException {
		writeNonBlankField(generator, "id", userGroup.getId());
		writeNonBlankField(generator, "displayname", userGroup.getDisplayname());
		writeNonBlankField(generator, "tag", userGroup.getTag());
		writeNonNullNumber(generator, "xdatUsergroupId", userGroup.getXdatUsergroupId());
		writeNonNullField(generator, "elementAccess", userGroup.getElementAccess());
		//writeNonNullField(generator, "user", userGroup.getUser());
	}
}