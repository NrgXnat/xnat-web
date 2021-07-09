package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.ArcProject;
import java.io.IOException;

@Slf4j
public class ArcProjectSerializer extends AbstractBaseElementSerializer<ArcProject> {
    public ArcProjectSerializer() {
        super(ArcProject.class);
    }

    @Override
    protected void serializeImpl(final ArcProject arcProject, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", arcProject.getId());
        writeNonBlankField(generator, "currentArc", arcProject.getCurrentArc());
        writeNonNullNumber(generator, "prearchiveCode", arcProject.getPrearchiveCode());
        generator.writeObjectField("paths", arcProject.getPaths());

    }
}
