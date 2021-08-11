package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.om.XnatAbstractdemographicdata;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractdemographicdataDeserializer<T extends XnatAbstractdemographicdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2357298303281276350L;

    protected XnatAbstractdemographicdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        if (StringUtils.equals(field, "xnatAbstractdemographicdataId")) {
            instance.setXnatAbstractdemographicdataId(parser.getIntValue());
        } else {
            super.handleField(instance, field, parser, context);
        }
    }
}
