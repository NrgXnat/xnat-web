package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroupFieldPossiblevalue;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatFielddefinitiongroupFieldPossiblevalueDeserializer<T extends XnatFielddefinitiongroupFieldPossiblevalue> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 226319763814396794L;

    @SuppressWarnings("unchecked")
    public XnatFielddefinitiongroupFieldPossiblevalueDeserializer() {
        this((Class<T>) XnatFielddefinitiongroupFieldPossiblevalue.class);
    }

    public XnatFielddefinitiongroupFieldPossiblevalueDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "display":
                // TODO: Handle the "display" property here: String
                break;
            case "possiblevalue":
                // TODO: Handle the "possiblevalue" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatFielddefinitiongroupFieldPossiblevalueId":
                // TODO: Handle the "xnatFielddefinitiongroupFieldPossiblevalueId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

