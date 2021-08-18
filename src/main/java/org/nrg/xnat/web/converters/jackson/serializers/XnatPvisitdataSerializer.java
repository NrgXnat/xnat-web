package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPvisitdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatPvisitdataSerializer<T extends XnatPvisitdata> extends XnatGenericdataSerializer<T> {
    private static final long serialVersionUID = -214008702530738987L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPvisitdataSerializer() {
        this((Class<T>) XnatPvisitdata.class);
    }

    protected XnatPvisitdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullBoolean(generator, "closed", instance.getClosed());
        // TODO: Write out the "endDate" property here: Object
        writeNonBlankField(generator, "notes", instance.getNotes());
        writeNonBlankField(generator, "protocolid", instance.getProtocolid());
        writeNonNullNumber(generator, "protocolversion", instance.getProtocolversion());
        // TODO: Write out the "startDate" property here: Object
        writeNonBlankField(generator, "status", instance.getStatus());
        writeNonBlankField(generator, "subjectId", instance.getSubjectId());
        writeNonNullBoolean(generator, "terminal", instance.getTerminal());
        writeNonBlankField(generator, "visitName", instance.getVisitName());
        writeNonBlankField(generator, "visitType", instance.getVisitType());
        super.serializeImpl(instance, generator, provider);
    }
}

