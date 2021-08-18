package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUsergroup;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatUsergroupSerializer<T extends XdatUsergroup> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6376083510255516680L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatUsergroupSerializer() {
        this((Class<T>) XdatUsergroup.class);
    }

    protected XdatUsergroupSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T userGroup, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", userGroup.getId());
        writeNonBlankField(generator, "displayName", userGroup.getDisplayname());
        writeNonBlankField(generator, "tag", userGroup.getTag());
        writeNonNullNumber(generator, "xdatUsergroupId", userGroup.getXdatUsergroupId());
        writeNonNullField(generator, "elementAccess", userGroup.getElementAccess());
    }
}