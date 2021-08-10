package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatRoleType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatRoleTypeSerializer<T extends XdatRoleType> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3500996899748825623L;

    @SuppressWarnings("unchecked")
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
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "roleName" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "sequence" property here: Integer
    }
}

