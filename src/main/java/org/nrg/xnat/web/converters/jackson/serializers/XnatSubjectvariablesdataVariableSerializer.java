package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectvariablesdataVariable;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSubjectvariablesdataVariableSerializer<T extends XnatSubjectvariablesdataVariable> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6744290324427044840L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectvariablesdataVariableSerializer() {
        this((Class<T>) XnatSubjectvariablesdataVariable.class);
    }

    protected XnatSubjectvariablesdataVariableSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "name", instance.getName());
        writeNonBlankField(generator, "variable", instance.getVariable());
        writeNonNullNumber(generator, "xnatSubjectvariablesdataVariableId", instance.getXnatSubjectvariablesdataVariableId());
    }
}

