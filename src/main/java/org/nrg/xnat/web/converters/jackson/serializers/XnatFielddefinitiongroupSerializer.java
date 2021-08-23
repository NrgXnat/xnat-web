package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroup;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatFielddefinitiongroupSerializer<T extends XnatFielddefinitiongroup> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3733491409445656006L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatFielddefinitiongroupSerializer() {
        this((Class<T>) XnatFielddefinitiongroup.class);
    }

    protected XnatFielddefinitiongroupSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "dataType", instance.getDataType());
        writeNonBlankField(generator, "description", instance.getDescription());
        // TODO: Write out the "fields_field" property here: java.util.List
        writeNonNullField(generator, "fields", instance.getFields_field());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonNullBoolean(generator, "projectSpecific", instance.getProjectSpecific());
        writeNonNullBoolean(generator, "shareable", instance.getShareable());
        writeNonNullNumber(generator, "xnatFielddefinitiongroupId", instance.getXnatFielddefinitiongroupId());
    }
}

