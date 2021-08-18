package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomcodedvalue;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatDicomcodedvalueDeserializer<T extends XnatDicomcodedvalue> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -224496881789332302L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDicomcodedvalueDeserializer() {
        this((Class<T>) XnatDicomcodedvalue.class);
    }

    protected XnatDicomcodedvalueDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "designator":
                instance.setDesignator(parser.getText());
                break;
            case "meaning":
                instance.setMeaning(parser.getText());
                break;
            case "value":
                instance.setValue(parser.getText());
                break;
            case "version":
                instance.setVersion(parser.getText());
                break;
            case "xnatDicomcodedvalueId":
                instance.setXnatDicomcodedvalueId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

