package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.generics.GenericUtils;
import org.nrg.xdat.om.*;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatStoredSearchSerializer<T extends XdatStoredSearch> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4065543805756070441L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatStoredSearchSerializer() {
        this((Class<T>) XdatStoredSearch.class);
    }

    protected XdatStoredSearchSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T search, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullBoolean(generator, "allowDiffColumns", search.getAllowDiffColumns());
        writeNonBlankField(generator, "briefDescription", search.getBriefDescription());
        writeNonBlankField(generator, "description", search.getDescription());
        writeNonBlankField(generator, "layeredSequence", search.getLayeredsequence());
        writeNonNullBoolean(generator, "secure", search.getSecure());
        writeNonBlankField(generator, "rootElementName", search.getRootElementName());
        writeNonBlankField(generator, "id", search.getId());
        writeNonBlankField(generator, "tag", search.getTag());
        writeNonBlankField(generator, "sortByElementName", search.getSortBy_elementName());
        writeNonBlankField(generator, "sortByFieldId", search.getSortBy_fieldId());

        generator.writeArrayFieldStart("login");
        for (final XdatStoredSearchAllowedUserI user : search.getAllowedUser()) {
            generator.writeString(user.getLogin());
        }
        generator.writeEndArray();

        generator.writeArrayFieldStart("searchField");
        for (final XdatSearchFieldI field : search.getSearchField()) {
            generator.writeString(field.getElementName());
            generator.writeString(field.getFieldId());
            generator.writeNumber(field.getSequence());
            generator.writeString(field.getType());
            generator.writeString(field.getHeader());
        }
        generator.writeEndArray();

        generator.writeArrayFieldStart("searchWhere");
        for (final XdatCriteriaSetI searchWhere : search.getSearchWhere()) {
            generator.writeArrayFieldStart("searchChild");
            for (final XdatCriteriaSetI child : GenericUtils.convertToTypedList(searchWhere.getChildSet(), XdatCriteriaSetI.class)) {
                generator.writeArrayFieldStart("searchCriteria");
                for (final XdatCriteriaI criteria : GenericUtils.convertToTypedList(child.getCriteria(), XdatCriteriaI.class)) {
                    generator.writeString(criteria.getSchemaField());
                    generator.writeString(criteria.getComparisonType());
                    generator.writeNumber(criteria.getValue());
                }
                generator.writeEndArray();
            }
            generator.writeEndArray();
        }
        generator.writeEndArray();
        // TODO: Write out the "allowedGroups_groupid" property here: java.util.ArrayList
        // TODO: Write out the "allowedUser" property here: java.util.ArrayList

    }
}

