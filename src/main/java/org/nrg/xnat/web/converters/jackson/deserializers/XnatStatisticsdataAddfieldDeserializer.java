package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdataAddfield;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatStatisticsdataAddfieldDeserializer<T extends XnatStatisticsdataAddfield> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4402271150468185846L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStatisticsdataAddfieldDeserializer() {
        this((Class<T>) XnatStatisticsdataAddfield.class);
    }

    protected XnatStatisticsdataAddfieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "addfield":
                instance.setAddfield(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "xnatStatisticsdataAddfieldId":
                instance.setXnatStatisticsdataAddfieldId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

