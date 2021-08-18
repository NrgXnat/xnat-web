package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroupFieldPossiblevalue;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatFielddefinitiongroupFieldPossiblevalueDeserializer<T extends XnatFielddefinitiongroupFieldPossiblevalue> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3791048551934120334L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatFielddefinitiongroupFieldPossiblevalueDeserializer() {
        this((Class<T>) XnatFielddefinitiongroupFieldPossiblevalue.class);
    }

    protected XnatFielddefinitiongroupFieldPossiblevalueDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "display":
                instance.setDisplay(parser.getText());
                break;
            case "possiblevalue":
                instance.setPossiblevalue(parser.getText());
                break;
            case "xnatFielddefinitiongroupFieldPossiblevalueId":
                instance.setXnatFielddefinitiongroupFieldPossiblevalueId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

