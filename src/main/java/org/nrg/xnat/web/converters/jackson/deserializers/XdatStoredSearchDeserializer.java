package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Objects;

import org.nrg.xdat.om.XdatStoredSearch;
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
            }
        }
        return search;
    }
}


