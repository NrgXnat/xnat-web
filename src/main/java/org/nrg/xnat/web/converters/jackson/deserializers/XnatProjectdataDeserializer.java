package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatInvestigatordata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.ItemI;

import java.io.IOException;

@Slf4j
public class XnatProjectdataDeserializer extends AbstractBaseElementDeserializer<XnatProjectdata> {
    public XnatProjectdataDeserializer() {
        super(XnatProjectdata.class);
    }

    @Override
    protected XnatProjectdata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatProjectdata project = new XnatProjectdata();

        final XnatInvestigatordata xnatInvestigatordata = new XnatInvestigatordata();
        try {
            project.setPi((ItemI) xnatInvestigatordata);
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
                case "firstName":
                    xnatInvestigatordata.setFirstname(parser.getText());
                    break;
                case "lastName":
                    xnatInvestigatordata.setLastname(parser.getText());
                    break;
            }
        }
        return project;
    }
}
