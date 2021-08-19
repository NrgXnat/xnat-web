package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.*;

import java.io.IOException;
import java.util.Map;

@XnatDeserializer
@Slf4j
public class XnatProjectdataDeserializer<T extends XnatProjectdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2441594064844506957L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatProjectdataDeserializer() {
        this((Class<T>) XnatProjectdata.class);
    }

    protected XnatProjectdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "type":
                instance.setType(parser.getText());
                break;
            case "secondaryId":
                instance.setSecondaryId(parser.getText());
                break;
            case "keywords":
                instance.setKeywords(parser.getText());
                break;
            case "active":
                instance.setActive(parser.getText());
                break;
            case "publications":
                try {
                    instance.setPublications_publication(parser.readValueAs(XnatPublicationresource.class));
                } catch (Exception e) {
                	log.error("Tried to set a field publications but failed", e);
                }
                break;
            case "studyProtocol":
                try {
                    instance.setStudyprotocol(parser.readValueAs(XnatAbstractprotocol.class));
                } catch (Exception e) {
                    log.error("Tried to set a field study protocol but failed", e);
                }
                break;

            case "aliases":
                final Map<String, String> projfields = parser.readValueAs(MAP_STRING_STRING);
                projfields.forEach((key, value) -> {
                    final XnatProjectdataAlias projAliase = new XnatProjectdataAlias();
                    projAliase.setSource(key);
                    projAliase.setAlias(value);
                    try {
                        instance.setFields_field(projAliase);
                    } catch (Exception e) {
                        log.error("Tried to set a field on an project with source {} and alias {} but failed", key, value, e);
                    }
                });
                break;

            case "fields":
                final Map<String, String> fields = parser.readValueAs(MAP_STRING_STRING);
                fields.forEach((key, value) -> {
                    final XnatProjectdataField projField = new XnatProjectdataField();
                    projField.setName(key);
                    projField.setField(value);
                    try {
                        instance.setFields_field(projField);
                    } catch (Exception e) {
                        log.error("Tried to set a field on an project with name {} and field {} but failed", key, value, e);
                    }
                });
                break;

            case "investigator":
                final XnatInvestigatordata investigator = parser.readValueAs(XnatInvestigatordata.class);
                investigator.setXnatInvestigatordataId(investigator.getXnatInvestigatordataId());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
