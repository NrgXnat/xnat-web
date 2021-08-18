package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironmentParameter;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class WrkXnatexecutionenvironmentParameterDeserializer<T extends WrkXnatexecutionenvironmentParameter> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7866665864782600836L;

    @SuppressWarnings({"unchecked", "unused"})
    public WrkXnatexecutionenvironmentParameterDeserializer() {
        this((Class<T>) WrkXnatexecutionenvironmentParameter.class);
    }

    protected WrkXnatexecutionenvironmentParameterDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                instance.setName(parser.getText());
                break;
            case "parameter":
                instance.setParameter(parser.getText());
                break;
            case "wrkXnatexecutionenvironmentParameterId":
                instance.setWrkXnatexecutionenvironmentParameterId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

