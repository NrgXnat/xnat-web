package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProject;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcProjectDeserializer<T extends ArcProject> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5697934541619107110L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcProjectDeserializer() {
        this((Class<T>) ArcProject.class);
    }

    protected ArcProjectDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcProjectId":
                instance.setArcProjectId(parser.getIntValue());
                break;
            case "currentArc":
                instance.setCurrentArc(parser.getText());
                break;
            case "fieldspecifications_fieldspecification":
                // TODO: Handle the "fieldspecifications_fieldspecification" property here: java.util.List
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "paths":
                // TODO: Handle the "paths" property here: org.nrg.xdat.model.ArcPathinfoI
                break;
            case "pipelines_descendants_descendant":
                // TODO: Handle the "pipelines_descendants_descendant" property here: java.util.List
                break;
            case "pipelines_pipeline":
                // TODO: Handle the "pipelines_pipeline" property here: java.util.List
                break;
            case "prearchiveCode":
                instance.setPrearchiveCode(parser.getIntValue());
                break;
            case "properties_property":
                // TODO: Handle the "properties_property" property here: java.util.List
                break;
            case "quarantineCode":
                instance.setQuarantineCode(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

