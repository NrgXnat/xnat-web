package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironmentNotify;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class WrkXnatexecutionenvironmentNotifyDeserializer<T extends WrkXnatexecutionenvironmentNotify> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1721726764643225666L;

    @SuppressWarnings({"unchecked", "unused"})
    public WrkXnatexecutionenvironmentNotifyDeserializer() {
        this((Class<T>) WrkXnatexecutionenvironmentNotify.class);
    }

    protected WrkXnatexecutionenvironmentNotifyDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "notify":
                instance.setNotify(parser.getText());
                break;
            case "wrkXnatexecutionenvironmentNotifyId":
                instance.setWrkXnatexecutionenvironmentNotifyId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

