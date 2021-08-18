package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProject;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ArcProjectSerializer<T extends ArcProject> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2801751366397830646L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcProjectSerializer() {
        this((Class<T>) ArcProject.class);
    }

    protected ArcProjectSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "arcProjectId", instance.getArcProjectId());
        writeNonBlankField(generator, "currentArc", instance.getCurrentArc());
        // TODO: Write out the "fieldspecifications_fieldspecification" property here: java.util.List
        writeNonBlankField(generator, "id", instance.getId());
        // TODO: Write out the "paths" property here: org.nrg.xdat.model.ArcPathinfoI
        // TODO: Write out the "pipelines_descendants_descendant" property here: java.util.List
        // TODO: Write out the "pipelines_pipeline" property here: java.util.List
        writeNonNullNumber(generator, "prearchiveCode", instance.getPrearchiveCode());
        // TODO: Write out the "properties_property" property here: java.util.List
        writeNonNullNumber(generator, "quarantineCode", instance.getQuarantineCode());
    }
}

