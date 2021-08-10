package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroupFieldPossiblevalue;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatFielddefinitiongroupFieldPossiblevalueSerializer<T extends XnatFielddefinitiongroupFieldPossiblevalue> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2402327630289902774L;

    @SuppressWarnings("unchecked")
    public XnatFielddefinitiongroupFieldPossiblevalueSerializer() {
        this((Class<T>) XnatFielddefinitiongroupFieldPossiblevalue.class);
    }

    protected XnatFielddefinitiongroupFieldPossiblevalueSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "display" property here: String
        // TODO: Write out the "possiblevalue" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatFielddefinitiongroupFieldPossiblevalueId" property here: Integer
    }
}

