package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetscandataFrame;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatPetscandataFrameSerializer<T extends XnatPetscandataFrame> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -7415651361016993138L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetscandataFrameSerializer() {
        this((Class<T>) XnatPetscandataFrame.class);
    }

    protected XnatPetscandataFrameSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "length", instance.getLength());
        // TODO: Write out the "number" property here: Object
        writeNonNullNumber(generator, "starttime", instance.getStarttime());
        writeNonBlankField(generator, "units", instance.getUnits());
        writeNonNullNumber(generator, "xnatPetscandataFrameId", instance.getXnatPetscandataFrameId());
    }
}

