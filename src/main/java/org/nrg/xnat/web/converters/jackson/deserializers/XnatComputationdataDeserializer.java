package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatComputationdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatComputationdataDeserializer<T extends XnatComputationdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7187588102381527515L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatComputationdataDeserializer() {
        this((Class<T>) XnatComputationdata.class);
    }

    protected XnatComputationdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                instance.setName(parser.getText());
                break;
            case "source":
                instance.setSource(parser.getText());
                break;
            case "units":
                instance.setUnits(parser.getText());
                break;
            case "value":
                instance.setValue(parser.getText());
                break;
            case "xnatComputationdataId":
                instance.setXnatComputationdataId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

