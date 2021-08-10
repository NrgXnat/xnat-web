package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xft.security.UserI;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatUsergroupDeserializer<T extends XdatUsergroup> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7910876525369749938L;

    @SuppressWarnings("unchecked")
    public XdatUsergroupDeserializer() {
        this((Class<T>) XdatUsergroup.class);
    }

    protected XdatUsergroupDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "displayName":
                instance.setDisplayname(parser.getText());
                break;
            case "tag":
                instance.setTag(parser.getText());
                break;
            case "xdatUsergroupId":
                instance.setXdatUsergroupId(parser.getIntValue());
                break;
            case "user":
                final UserI user = getUserI(parser.getText());
                if (user != null) {
                    instance.setUser(user);
                }
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
