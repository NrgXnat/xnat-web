package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectvariablesdataVariable;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectvariablesdataVariableSerializer<T extends XnatSubjectvariablesdataVariable> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6744290324427044840L;

    @SuppressWarnings("unchecked")
    public XnatSubjectvariablesdataVariableSerializer() {
        this((Class<T>) XnatSubjectvariablesdataVariable.class);
    }

    protected XnatSubjectvariablesdataVariableSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "variable" property here: String
        // TODO: Write out the "xnatSubjectvariablesdataVariableId" property here: Integer
    }
}

