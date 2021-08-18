package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatValidationdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatValidationDataDeserializer<T extends XnatValidationdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3211819690500955629L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatValidationDataDeserializer() {
        this((Class<T>) XnatValidationdata.class);
    }

    protected XnatValidationDataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "xnatValidationDataId":
                instance.setXnatValidationdataId(parser.getIntValue());
            case "method":
                instance.setMethod(parser.getText());
                break;
            case "date":
                instance.setDate(parseDate(parser.getText()));
                break;
            case "notes":
                instance.setNotes(parser.getText());
                break;
            case "validatedBy":
                instance.setValidatedBy(parser.getText());
                break;
            case "status":
                instance.setStatus(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
