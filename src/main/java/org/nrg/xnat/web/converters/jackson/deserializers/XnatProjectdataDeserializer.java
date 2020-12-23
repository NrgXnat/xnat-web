package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.XnatDemographicdata;
import org.nrg.xdat.om.XnatInvestigatordata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.ItemI;
import org.nrg.xft.security.UserI;

import java.io.IOException;

@Slf4j
public class XnatProjectdataDeserializer extends AbstractBaseElementDeserializer<XnatProjectdata> {
    public XnatProjectdataDeserializer() {
        super(XnatProjectdata.class);
    }

    @Override
    protected XnatProjectdata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatProjectdata project = new XnatProjectdata();
        final XnatInvestigatordata investigator = new XnatInvestigatordata();
        try {
        	project.setInvestigators_investigator((ItemI) investigator);
        } catch (Exception e) {
            log.error("An error occurred trying to set demographics data while deserializing an object. Sorry about that.", e);
        }
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                    project.setId(parser.getText());
                    break;
                case "description":
                    project.setDescription(parser.getText());
                    break;
                case "name":
                    project.setName(parser.getText());
                    break;
                case "secondaryId":
                    project.setSecondaryId(parser.getText());
                    break;
                case "keywords":
                    project.setKeywords(parser.getText());
                    break;
                case "active":
                    project.setActive(parser.getText());
                    break;
                case "firstName":
                	investigator.setFirstname(parser.getText());
                    break;
                case "lastName":
                	investigator.setLastname(parser.getText());
                    break;
                case "email":
                	investigator.setEmail(parser.getText());
                    break;
              
            }
        }
        return project;
    }
}
