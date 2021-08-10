package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocol;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStudyprotocolSerializer<T extends XnatStudyprotocol> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -846438649256472371L;

    @SuppressWarnings("unchecked")
    public XnatStudyprotocolSerializer() {
        this((Class<T>) XnatStudyprotocol.class);
    }

    protected XnatStudyprotocolSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "abstractprotocol" property here: org.nrg.xdat.om.XnatAbstractprotocol
        // TODO: Write out the "acqconditions_condition" property here: java.util.List
        // TODO: Write out the "imagesessiontypes_session" property here: java.util.List
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "subjectgroups_group" property here: java.util.List
        // TODO: Write out the "subjectvariables_variable" property here: java.util.List
    }
}

