package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandataImpedance;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatEegscandataImpedanceDeserializer<T extends XnatEegscandataImpedance> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1012382039410316717L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEegscandataImpedanceDeserializer() {
        this((Class<T>) XnatEegscandataImpedance.class);
    }

    protected XnatEegscandataImpedanceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                instance.setName(parser.getText());
                break;
            case "value":
                instance.setValue(parser.getText());
                break;
            case "xnatEegscandataImpedanceId":
                instance.setXnatEegscandataImpedanceId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

