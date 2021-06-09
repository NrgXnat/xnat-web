package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.model.XnatFielddefinitiongroupI;
import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.om.XnatDatatypeprotocol;
import org.nrg.xdat.om.XnatFielddefinitiongroup;

import java.io.IOException;

@Slf4j
public class XnatDatatypeprotocolSerializer extends AbstractBaseElementSerializer<XnatDatatypeprotocol> {
	private static final long serialVersionUID = -6912627144885118417L;

	public XnatDatatypeprotocolSerializer() {
        super(XnatDatatypeprotocol.class);
    }

    @Override
    protected void serializeImpl(final XnatDatatypeprotocol protocol, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonNullField(generator, "xnatAbstractProtocolId", protocol.getXnatAbstractprotocolId());
        writeNonBlankField(generator, "name", protocol.getName());
        writeNonBlankField(generator, "id", protocol.getId());
        writeNonBlankField(generator, "dataType", protocol.getDataType());
        writeNonBlankField(generator, "description", protocol.getDescription());
        writeNonBlankField(generator, "xsiType", protocol.getXSIType());
        generator.writeArrayFieldStart("definition");
        for(final XnatFielddefinitiongroupI definition : protocol.getDefinitions_definition()) {
        		generator.writeString(definition.getId());
        }
        generator.writeEndArray();
    }
}
