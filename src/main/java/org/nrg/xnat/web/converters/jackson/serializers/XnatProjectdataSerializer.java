package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatProjectdataSerializer<T extends XnatProjectdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 5168583339226819279L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatProjectdataSerializer() {
        this((Class<T>) XnatProjectdata.class);
    }

    protected XnatProjectdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T project, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", project.getId());
        writeNonBlankField(generator, "description", project.getDescription());
        writeNonBlankField(generator, "name", project.getName());
        writeNonBlankField(generator, "type", project.getType());
        writeNonBlankField(generator, "secondaryId", project.getSecondaryId());
        writeNonBlankField(generator, "keywords", project.getKeywords());
        writeNonNullBoolean(generator, "active", project.getActive());
        writeNonNullField(generator, "publications", project.getPublications_publication());
        writeNonNullField(generator, "resources", project.getResources_resource());
        writeNonNullField(generator, "studyProtocol", project.getStudyprotocol());
        writeNonNullField(generator, "aliases", project.getAliases_alias());
        writeNonNullField(generator, "fields", project.getFields_field());
        writeNonNullField(generator, "pi", project.getPi());
        writeNonNullField(generator, "investigators", project.getInvestigators_investigator());
    }
}
