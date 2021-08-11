package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPublicationresource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPublicationresourceSerializer<T extends XnatPublicationresource> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 133835647161683121L;

    @SuppressWarnings("unchecked")
    public XnatPublicationresourceSerializer() {
        this((Class<T>) XnatPublicationresource.class);
    }

    protected XnatPublicationresourceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "abstract" property here: String
        // TODO: Write out the "abstractresource" property here: org.nrg.xdat.om.XnatAbstractresource
        // TODO: Write out the "citation" property here: String
        // TODO: Write out the "commentary" property here: String
        // TODO: Write out the "isprimary" property here: Boolean
        // TODO: Write out the "label" property here: String
        // TODO: Write out the "medline" property here: String
        // TODO: Write out the "other" property here: String
        // TODO: Write out the "pubmed" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "title" property here: String
        // TODO: Write out the "type" property here: String
        // TODO: Write out the "unresolvedPaths" property here: java.util.ArrayList
    }
}

