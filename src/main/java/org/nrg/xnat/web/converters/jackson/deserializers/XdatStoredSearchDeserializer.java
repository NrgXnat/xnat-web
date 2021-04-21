package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Objects;

import org.nrg.xdat.om.XdatCriteria;
import org.nrg.xdat.om.XdatCriteriaSet;
import org.nrg.xdat.om.XdatSearchField;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.om.XdatStoredSearchAllowedUser;
import org.nrg.xft.ItemI;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XdatStoredSearchDeserializer extends AbstractBaseElementDeserializer<XdatStoredSearch> {
    public XdatStoredSearchDeserializer() {
        super(XdatStoredSearch.class);
    }
    
    @Override
    protected XdatStoredSearch deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XdatStoredSearch search = new XdatStoredSearch();
        XdatStoredSearchAllowedUser xdatStoredSearchAllowedUser = new XdatStoredSearchAllowedUser();
        XdatSearchField seachField = new XdatSearchField();
        XdatCriteriaSet searchWhere = new XdatCriteriaSet();
        XdatCriteriaSet child = new XdatCriteriaSet();
        XdatCriteria criteria = new XdatCriteria();
        
        try {
        	search.setAllowedUser((ItemI)xdatStoredSearchAllowedUser);
        	search.setSearchField((ItemI)seachField);
        	search.setSearchWhere((ItemI)searchWhere);
        	searchWhere.setChildSet((ItemI)child);
        	child.setCriteria((ItemI)criteria);
        } catch (Exception e) {
            log.error("An error occurred trying to set XdatStoredSearchAllowedUser data while deserializing an object. Sorry about that.", e);
        }
        
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "allowDiffColumns":
                    search.setAllowDiffColumns(parser.getBooleanValue());
                    break;
                case "briefDescription":
                	search.setBriefDescription(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "description":
                    search.setDescription(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "layeredsequence":
                	search.setLayeredsequence(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "secure":
                    search.setSecure(parser.getBooleanValue());
                    break;
                case "rootElementName":
                	search.setRootElementName(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "id":
                	search.setId(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "tag":
                	search.setTag(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "sortByElementName":
                	search.setSortBy_elementName(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "elementName":
                	seachField.setElementName(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "field":
                	seachField.setFieldId(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "sequence":
                	seachField.setSequence(Objects.nonNull(parser.getIntValue())?parser.getIntValue():null);
                    break;
                case "type":
                	seachField.setType(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "header":
                	seachField.setHeader(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "schemaField":
                	criteria.setSchemaField(Objects.nonNull(parser.getText())?parser.getText():null);
                    break;
                case "comparison":
                	criteria.setComparisonType(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
                case "value":
                	criteria.setValue(Objects.nonNull(parser.getText())?parser.getText():"");
                    break;
            }
        }
        return search;
    }
}


