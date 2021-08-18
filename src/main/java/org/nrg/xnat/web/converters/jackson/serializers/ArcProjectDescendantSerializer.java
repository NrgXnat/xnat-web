package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectDescendant;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ArcProjectDescendantSerializer<T extends ArcProjectDescendant> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -7287507891175895761L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcProjectDescendantSerializer() {
        this((Class<T>) ArcProjectDescendant.class);
    }

    protected ArcProjectDescendantSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "arcProjectDescendantId", instance.getArcProjectDescendantId());
        // TODO: Write out the "pipeline" property here: java.util.List
        writeNonBlankField(generator, "xsitype", instance.getXsitype());
    }
}

