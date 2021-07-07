package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;

import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xdat.om.XdatUser;
import org.nrg.xdat.om.XnatDemographicdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.user.XnatUserProvider;
import org.nrg.xft.ItemI;
import org.nrg.xft.security.UserI;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XdatUsergroupDeserializer extends AbstractBaseElementDeserializer<XdatUsergroup> {
    public XdatUsergroupDeserializer() {
        super(XdatUsergroup.class);
    }

    @Override
    protected XdatUsergroup deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XdatUsergroup userGroup = new XdatUsergroup();
        
        final UserI user = userGroup.getUser();
        	userGroup.setUser(user);
        
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	userGroup.setId(parser.getText());
                    break;
                case "displayname":
                	userGroup.setDisplayname(parser.getText());
                    break;
                case "firstname":
                    user.setFirstname(parser.getText());
                    break;
                case "lastname":
                	user.setLastname(parser.getText());
                    break;
                case "email":
                	user.setEmail(parser.getText());
                    break;
                case "tag":
                	userGroup.setTag(parser.getText());
                    break;

            }
        }
        return userGroup;
    }

	@Override
	protected XdatUsergroup getNewInstance() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
