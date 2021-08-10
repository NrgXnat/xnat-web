package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRegionresourceLabel;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatRegionresourceLabelSerializer<T extends XnatRegionresourceLabel> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3901434119594396892L;

    @SuppressWarnings("unchecked")
    public XnatRegionresourceLabelSerializer() {
        this((Class<T>) XnatRegionresourceLabel.class);
    }

    protected XnatRegionresourceLabelSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "hemisphere" property here: String
        // TODO: Write out the "label" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatRegionresourceLabelId" property here: Integer
    }
}

