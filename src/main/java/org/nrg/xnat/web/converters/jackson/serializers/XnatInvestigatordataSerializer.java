package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatInvestigatordata;

import java.io.IOException;

@Slf4j
public class XnatInvestigatordataSerializer extends AbstractBaseElementSerializer<XnatInvestigatordata> {
    public XnatInvestigatordataSerializer() {
        super(XnatInvestigatordata.class);
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
