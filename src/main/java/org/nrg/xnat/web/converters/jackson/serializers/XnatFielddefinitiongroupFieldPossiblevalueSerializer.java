package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroupFieldPossiblevalue;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatFielddefinitiongroupFieldPossiblevalueSerializer<T extends XnatFielddefinitiongroupFieldPossiblevalue> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2402327630289902774L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatFielddefinitiongroupFieldPossiblevalueSerializer() {
        this((Class<T>) XnatFielddefinitiongroupFieldPossiblevalue.class);
    }

    protected XnatFielddefinitiongroupFieldPossiblevalueSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "display", instance.getDisplay());
        writeNonBlankField(generator, "possiblevalue", instance.getPossiblevalue());
        writeNonNullNumber(generator, "xnatFielddefinitiongroupFieldPossiblevalueId", instance.getXnatFielddefinitiongroupFieldPossiblevalueId());
    }
}

