package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatAccessLog;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatAccessLogDeserializer<T extends XdatAccessLog> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1004185398548676033L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatAccessLogDeserializer() {
        this((Class<T>) XdatAccessLog.class);
    }

    protected XdatAccessLogDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "accessDate":
                // TODO: Handle the "accessDate" property here: Object
                break;
            case "ip":
                instance.setIp(parser.getText());
                break;
            case "login":
                instance.setLogin(parser.getText());
                break;
            case "method":
                instance.setMethod(parser.getText());
                break;
            case "xdatAccessLogId":
                instance.setXdatAccessLogId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

