package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.om.XdatStoredSearchAllowedUser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XdatStoredSearchSerializer extends AbstractBaseElementSerializer<XdatStoredSearch> {
    public XdatStoredSearchSerializer() {
        super(XdatStoredSearch.class);
    }

    @Override
    protected void serializeImpl(final XdatStoredSearch search, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonNullBoolean(generator, "allowDiffColumns", search.getAllowDiffColumns());
        writeNonBlankField(generator, "sortByFieldId", search.getSortBy_fieldId());
        writeNonBlankField(generator, "briefDescription", search.getBriefDescription());
        writeNonBlankField(generator, "description", search.getDescription());
        writeNonBlankField(generator, "layeredsequence", search.getLayeredsequence());
        writeNonNullBoolean(generator, "secure", search.getSecure());
        writeNonBlankField(generator, "rootElementName", search.getRootElementName());
        writeNonBlankField(generator, "id", search.getId());
        writeNonBlankField(generator, "tag", search.getTag());
        writeNonBlankField(generator, "sortByElementName", search.getSortBy_elementName());
        generator.writeArrayFieldStart("login");
        for(final XdatStoredSearchAllowedUser user : search.getAllowedUser()) {
        	generator.writeString(user.getLogin());
        }
        generator.writeEndArray();
    }
}

