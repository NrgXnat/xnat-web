package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatInvestigatordata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatInvestigatordataSerializer<T extends XnatInvestigatordata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1337889342345146869L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatInvestigatordataSerializer() {
        this((Class<T>) XnatInvestigatordata.class);
    }

    protected XnatInvestigatordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "department", instance.getDepartment());
        writeNonBlankField(generator, "email", instance.getEmail());
        writeNonBlankField(generator, "firstname", instance.getFirstname());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "institution", instance.getInstitution());
        writeNonBlankField(generator, "lastname", instance.getLastname());
        writeNonBlankField(generator, "phone", instance.getPhone());
        writeNonBlankField(generator, "title", instance.getTitle());
        writeNonNullNumber(generator, "xnatInvestigatordataId", instance.getXnatInvestigatordataId());
    }
}

