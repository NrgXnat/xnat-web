package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPublicationresource;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatPublicationresourceDeserializer<T extends XnatPublicationresource> extends XnatAbstractResourceDeserializer<T> {
    private static final long serialVersionUID = -5072414654198519130L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPublicationresourceDeserializer() {
        this((Class<T>) XnatPublicationresource.class);
    }

    protected XnatPublicationresourceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "abstract":
                instance.setAbstract(parser.getText());
                break;
            case "citation":
                instance.setCitation(parser.getText());
                break;
            case "commentary":
                instance.setCommentary(parser.getText());
                break;
            case "doi":
                instance.setDoi(parser.getText());
                break;
            case "isprimary":
                instance.setIsprimary(parser.getBooleanValue());
                break;
            case "medline":
                instance.setMedline(parser.getText());
                break;
            case "other":
                instance.setOther(parser.getText());
                break;
            case "pubmed":
                instance.setPubmed(parser.getText());
                break;
            case "title":
                instance.setTitle(parser.getText());
                break;
            case "type":
                instance.setType(parser.getText());
                break;
            case "uri":
                instance.setUri(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

