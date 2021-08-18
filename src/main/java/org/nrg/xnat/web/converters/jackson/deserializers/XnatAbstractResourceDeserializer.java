package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatAbstractresourceTag;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class XnatAbstractresourceDeserializer<T extends XnatAbstractresource> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7694265172552963272L;

    protected XnatAbstractresourceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "note":
                instance.setNote(parser.getText());
                break;
            case "baseUri":
                instance.setBaseURI(parser.getText());
                break;
            case "fileCount":
                instance.setFileCount(parser.getIntValue());
                break;
            case "fileSize":
                instance.setFileSize(parser.getIntValue());
                break;
            case "xnatAbstractResourceId":
                instance.setXnatAbstractresourceId(parser.getIntValue());
                break;
            case "user":
                instance.setUser(getUserI(parser.getText()));
                break;
            case "tags":
                final List<String> tags = parser.readValueAs(LIST_STRING);
                for (final XnatAbstractresourceTag tag : tags.stream().map(tag -> {
                    final XnatAbstractresourceTag tagObject = new XnatAbstractresourceTag();
                    tagObject.setName(tag);
                    tagObject.setTag(tag);
                    return tagObject;
                }).collect(Collectors.toList())) {
                    try {
                        instance.setTags_tag(tag);
                    } catch (Exception e) {
                        log.error("An error occurred trying to set the tag {} on an abstract resource", tag.getTag());
                    }
                }
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
