package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocol;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatStudyprotocolSerializer<T extends XnatStudyprotocol> extends XnatAbstractprotocolSerializer<T> {
    private static final long serialVersionUID = 3217118363409696649L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStudyprotocolSerializer() {
        this((Class<T>) XnatStudyprotocol.class);
    }

    protected XnatStudyprotocolSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "acqconditions_condition" property here: java.util.List
        // TODO: Write out the "imagesessiontypes_session" property here: java.util.List
        // TODO: Write out the "subjectgroups_group" property here: java.util.List
        // TODO: Write out the "subjectvariables_variable" property here: java.util.List
        super.serializeImpl(instance, generator, provider);
    }
}

