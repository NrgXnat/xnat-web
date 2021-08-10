package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRegionresource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatRegionresourceSerializer<T extends XnatRegionresource> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8286945782773125591L;

    @SuppressWarnings("unchecked")
    public XnatRegionresourceSerializer() {
        this((Class<T>) XnatRegionresource.class);
    }

    protected XnatRegionresourceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "baseimage" property here: org.nrg.xdat.model.XnatAbstractresourceI
        // TODO: Write out the "creator_firstname" property here: String
        // TODO: Write out the "creator_lastname" property here: String
        // TODO: Write out the "file" property here: org.nrg.xdat.om.XnatAbstractresource
        // TODO: Write out the "hemisphere" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "sessionId" property here: String
        // TODO: Write out the "subregionlabels_label" property here: java.util.List
        // TODO: Write out the "xnatRegionresourceId" property here: Integer
    }
}

