package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdataField;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatExperimentdataFieldDeserializer<T extends XnatExperimentdataField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1038750389778157400L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatExperimentdataFieldDeserializer() {
        this((Class<T>) XnatExperimentdataField.class);
    }

    protected XnatExperimentdataFieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "field":
                instance.setField(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "xnatExperimentdataFieldId":
                instance.setXnatExperimentdataFieldId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

