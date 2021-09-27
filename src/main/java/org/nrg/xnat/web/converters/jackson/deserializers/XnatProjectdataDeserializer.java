package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.constants.PrearchiveCode;
import org.nrg.xdat.om.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                final List<XnatProjectdataAlias> aliases;
                if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
                    final List<String> values = parser.readValueAs(LIST_STRING);
                    aliases = values.stream().map(XnatProjectdataDeserializer::getAlias).collect(Collectors.toList());
                } else {
                    final Map<String, String> values = parser.readValueAs(MAP_STRING_STRING);
                    aliases = values.entrySet().stream().map(entry -> getAlias(entry.getKey(), entry.getValue())).collect(Collectors.toList());
                }
                aliases.forEach(alias -> addAlias(instance, alias));
                break;

            case "fields":
                final Map<String, String> fields = parser.readValueAs(MAP_STRING_STRING);
                fields.forEach((key, value) -> {
                    final XnatProjectdataField projField = new XnatProjectdataField();
                    projField.setName(key);
                    projField.setField(value);
                    try {
                        instance.addFields_field(projField);
                    } catch (Exception e) {
                        log.error("Tried to set a field on an project with name {} and field {} but failed", key, value, e);
                    }
                });
                break;

            case "investigator":
                final XnatInvestigatordata investigator = parser.readValueAs(XnatInvestigatordata.class);
                investigator.setXnatInvestigatordataId(investigator.getXnatInvestigatordataId());
                break;

            case "autoArchive":
                final String autoArchive = parser.getText();
                final PrearchiveCode prearchiveCode = PrearchiveCode.normalize(autoArchive);
                if (prearchiveCode == null) {
                    log.error("The value specified for the prearchive code \"{}\" is invalid", autoArchive);
                } else {
                    final ArcProject arcProject = getArcProject(instance);
                    arcProject.setPrearchiveCode(prearchiveCode.getCode());
                }
                break;

            case "quarantine":
                final ArcProject arcProject = getArcProject(instance);
                arcProject.setQuarantineCode(parser.getBooleanValue() ? 1 : 0);
                break;

            default:
                super.handleField(instance, field, parser, context);
        }
    }

    private static ArcProject getArcProject(final XnatProjectdata instance) {
        try {
            final ArcProject arcProject = instance.getArcSpecification();
            if (arcProject != null) {
                return arcProject;
            }
        } catch (Exception e) {
            log.warn("Got an exception trying to retrieve an arc project. This might be okay in some circumstances (unit tests), but bad in a deployed XNAT environment.");
        }
        return new ArcProject();
    }

    private static XnatProjectdataAlias getAlias(final String alias) {
        return getAlias(alias, null);
    }

    private static XnatProjectdataAlias getAlias(final String alias, final String source) {
        final XnatProjectdataAlias projectAlias = new XnatProjectdataAlias();
        projectAlias.setAlias(alias);
        projectAlias.setSource(source);
        return projectAlias;
    }

    private static void addAlias(final XnatProjectdata instance, final XnatProjectdataAlias alias) {
        try {
            instance.addAliases_alias(alias);
        } catch (Exception e) {
            log.error("An error occurred trying to add alias \"{}\" to project {}", alias.getAlias(), StringUtils.defaultIfBlank(instance.getId(), "<no ID set>"));
        }
    }
}
