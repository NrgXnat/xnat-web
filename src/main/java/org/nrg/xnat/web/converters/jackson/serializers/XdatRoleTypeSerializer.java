package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatRoleType;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatRoleTypeSerializer<T extends XdatRoleType> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3500996899748825623L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatRoleTypeSerializer() {
        this((Class<T>) XdatRoleType.class);
    }

    protected XdatRoleTypeSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "allowedActions_allowedAction" property here: java.util.ArrayList
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "roleName", instance.getRoleName());
        writeNonNullNumber(generator, "sequence", instance.getSequence());
    }
}

