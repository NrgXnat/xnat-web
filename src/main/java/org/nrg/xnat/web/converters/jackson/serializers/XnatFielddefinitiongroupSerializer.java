package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroup;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatFielddefinitiongroupSerializer<T extends XnatFielddefinitiongroup> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3733491409445656006L;

    @SuppressWarnings("unchecked")
    public XnatFielddefinitiongroupSerializer() {
        this((Class<T>) XnatFielddefinitiongroup.class);
    }

    protected XnatFielddefinitiongroupSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "dataType" property here: String
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "fields_field" property here: java.util.List
        // TODO: Write out the "projectSpecific" property here: Boolean
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "shareable" property here: Boolean
        // TODO: Write out the "xnatFielddefinitiongroupId" property here: Integer
    }
}

