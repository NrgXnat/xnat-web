package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatInvestigatordata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatInvestigatordataSerializer<T extends XnatInvestigatordata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8940722139202082695L;

    @SuppressWarnings("unchecked")
    public XnatInvestigatordataSerializer() {
        this((Class<T>) XnatInvestigatordata.class);
    }

    protected XnatInvestigatordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void serializeImpl(final XnatInvestigatordata investigator, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", investigator.getId());
        writeNonNullNumber(generator, "xnatInvestigatordataId", investigator.getXnatInvestigatordataId());
        writeNonBlankField(generator, "firstname", investigator.getFirstname());
        writeNonBlankField(generator, "lastname", investigator.getLastname());
        writeNonBlankField(generator, "title", investigator.getTitle());
        writeNonBlankField(generator, "institution", investigator.getInstitution());
        writeNonBlankField(generator, "department", investigator.getDepartment());
        writeNonBlankField(generator, "email", investigator.getEmail());
        writeNonBlankField(generator, "phone", investigator.getPhone());
    }
}
