package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;

import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xdat.om.XdatElementAccess;
import org.nrg.xdat.om.XdatUser;
import org.nrg.xdat.om.XnatDemographicdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.user.XnatUserProvider;
import org.nrg.xft.ItemI;
import org.nrg.xft.security.UserI;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XdatElementAccessDeserializer extends AbstractBaseElementDeserializer<XdatElementAccess> {
    public XdatElementAccessDeserializer() {
        super(XdatElementAccess.class);
    }

    @Override
    protected XdatElementAccess deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XdatElementAccess elementAccess = new XdatElementAccess();
        
        
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "xdatElementAccessId":
                	elementAccess.setXdatElementAccessId(parser.getIntValue());
                    break;
                case "elementName":
                	elementAccess.setElementName(parser.getText());
                    break;
            }
        }
        return elementAccess;
    }
}
