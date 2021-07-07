package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatInvestigatordata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.ItemI;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatInvestigatordataDeserializer extends AbstractBaseElementDeserializer<XnatInvestigatordata> {
    public XnatInvestigatordataDeserializer() {
        super(XnatInvestigatordata.class);
    }

    @Override
    protected XnatInvestigatordata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {

        final XnatInvestigatordata xnatInvestigatordata = new XnatInvestigatordata();
        
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	xnatInvestigatordata.setId(parser.getText());
                    break;
                case "xnatInvestigatordataId":
                	xnatInvestigatordata.setXnatInvestigatordataId(parser.getIntValue());
                    break;
                case "firstname":
                	xnatInvestigatordata.setFirstname(parser.getText());
                    break;
                case "lastname":
                	xnatInvestigatordata.setLastname(parser.getText());
                    break;
                case "title":
                    xnatInvestigatordata.setTitle(parser.getText());
                    break;
                case "institution":
                    xnatInvestigatordata.setInstitution(parser.getText());
                    break;
                case "department":
                    xnatInvestigatordata.setDepartment(parser.getText());
                    break;
                case "email":
                    xnatInvestigatordata.setEmail(parser.getText());
                    break;
                case "phone":
                    xnatInvestigatordata.setPhone(parser.getText());
                    break;
            }
        }
        return xnatInvestigatordata;
    }

	@Override
	protected XnatInvestigatordata getNewInstance() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
