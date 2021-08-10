package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementAccess;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatElementAccessSerializer<T extends XdatElementAccess> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4134600568289609224L;

    @SuppressWarnings("unchecked")
    public XdatElementAccessSerializer() {
        this((Class<T>) XdatElementAccess.class);
    }

    protected XdatElementAccessSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final XdatElementAccess elementAccess, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullNumber(generator, "xdatElementAccessId", elementAccess.getXdatElementAccessId());
        writeNonBlankField(generator, "elementName", elementAccess.getElementName());
        writeNonBlankField(generator, "xsiType", elementAccess.getXSIType());
    }
}