package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.ArcProject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcProjectSerializer<T extends ArcProject> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 3810695382068553705L;

    @SuppressWarnings("unchecked")
    public ArcProjectSerializer() {
        this((Class<T>) ArcProject.class);
    }

    protected ArcProjectSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final ArcProject arcProject, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", arcProject.getId());
        writeNonBlankField(generator, "currentArc", arcProject.getCurrentArc());
        writeNonNullNumber(generator, "prearchiveCode", arcProject.getPrearchiveCode());
        generator.writeObjectField("paths", arcProject.getPaths());

    }
}
