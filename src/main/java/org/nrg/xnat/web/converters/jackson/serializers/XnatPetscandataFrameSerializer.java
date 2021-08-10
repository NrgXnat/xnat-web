package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetscandataFrame;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetscandataFrameSerializer<T extends XnatPetscandataFrame> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -7415651361016993138L;

    @SuppressWarnings("unchecked")
    public XnatPetscandataFrameSerializer() {
        this((Class<T>) XnatPetscandataFrame.class);
    }

    protected XnatPetscandataFrameSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "length" property here: Double
        // TODO: Write out the "number" property here: Object
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "starttime" property here: Double
        // TODO: Write out the "units" property here: String
        // TODO: Write out the "xnatPetscandataFrameId" property here: Integer
    }
}

