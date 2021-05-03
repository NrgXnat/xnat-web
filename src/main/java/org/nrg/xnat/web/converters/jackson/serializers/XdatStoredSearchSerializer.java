package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;
import org.nrg.xdat.om.XdatCriteria;
import org.nrg.xdat.om.XdatCriteriaSet;
import org.nrg.xdat.om.XdatSearchField;
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
        
        generator.writeArrayFieldStart("searchField");
        for(final XdatSearchField field : search.getSearchField()) {
        	generator.writeString(field.getElementName());
        	generator.writeString(field.getFieldId());
        	generator.writeNumber(field.getSequence());
        	generator.writeString(field.getType());
        	generator.writeString(field.getHeader());
        }
        generator.writeEndArray();
        
        generator.writeArrayFieldStart("searchWhere");
        for(final XdatCriteriaSet searchWhere : search.getSearchWhere()) {
        	 generator.writeArrayFieldStart("searchChild");
        	for(final XdatCriteriaSet child : searchWhere.getChildSet()) {
        		 generator.writeArrayFieldStart("searchCriteria");
        		for(final XdatCriteria criteria : child.getCriteria()) {
        			generator.writeString(criteria.getSchemaField());
                	generator.writeString(criteria.getComparisonType());
                	generator.writeNumber(criteria.getValue());
        		}
        		generator.writeEndArray();
        	}
        	  generator.writeEndArray();
        }
        generator.writeEndArray();
    }
}

