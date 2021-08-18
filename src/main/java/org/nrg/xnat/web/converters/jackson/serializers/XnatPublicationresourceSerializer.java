package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPublicationresource;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatPublicationresourceSerializer<T extends XnatPublicationresource> extends XnatAbstractResourceSerializer<T> {
    private static final long serialVersionUID = -6136253276631483903L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPublicationresourceSerializer() {
        this((Class<T>) XnatPublicationresource.class);
    }

    protected XnatPublicationresourceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "abstract", instance.getAbstract());
        writeNonBlankField(generator, "citation", instance.getCitation());
        writeNonBlankField(generator, "commentary", instance.getCommentary());
        writeNonBlankField(generator, "doi", instance.getDoi());
        writeNonNullBoolean(generator, "isprimary", instance.getIsprimary());
        writeNonBlankField(generator, "medline", instance.getMedline());
        writeNonBlankField(generator, "other", instance.getOther());
        writeNonBlankField(generator, "pubmed", instance.getPubmed());
        writeNonBlankField(generator, "title", instance.getTitle());
        writeNonBlankField(generator, "type", instance.getType());
        writeNonBlankField(generator, "uri", instance.getUri());
        super.serializeImpl(instance, generator, provider);
    }
}

