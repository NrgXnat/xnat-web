package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUserGroupid;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatUserGroupidSerializer<T extends XdatUserGroupid> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -578230105497060178L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatUserGroupidSerializer() {
        this((Class<T>) XdatUserGroupid.class);
    }

    protected XdatUserGroupidSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "groupid", instance.getGroupid());
        writeNonNullNumber(generator, "xdatUserGroupidId", instance.getXdatUserGroupidId());
    }
}

