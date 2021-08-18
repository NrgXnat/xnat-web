package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolVariable;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatStudyprotocolVariableSerializer<T extends XnatStudyprotocolVariable> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1693407482655651512L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStudyprotocolVariableSerializer() {
        this((Class<T>) XnatStudyprotocolVariable.class);
    }

    protected XnatStudyprotocolVariableSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatStudyprotocolVariableId", instance.getXnatStudyprotocolVariableId());
    }
}

