package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.om.XdatUser;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xdat.security.XDATUser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class XdatStoredSearchDeserializer<T extends XdatStoredSearch> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5423193050766202850L;

    @SuppressWarnings("unchecked")
    public XdatStoredSearchDeserializer() {
        super((Class<T>) XdatStoredSearch.class);
    }

    public XdatStoredSearchDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "allowedUsers":
                final List<String> usernames = parser.readValueAs(LIST_STRING);
                final List<XdatUser> users = usernames.stream().map(username -> XDATUser.getXdatUsersByLogin(username, null, false)).collect(Collectors.toList());
                for (final XdatUser user : users) {
                    try {
                        instance.setAllowedUser(user);
                    } catch (Exception e) {
                        log.error("An error occurred trying to set the user {}", user.getLogin(), e);
                    }
                }
                break;
            case "allowedGroups":
                final List<String> groupIds = parser.readValueAs(LIST_STRING);
                final List<XdatUsergroup> groups = groupIds.stream().map(groupId -> XdatUsergroup.getXdatUsergroupsByXdatUsergroupId(groupId, null, false)).collect(Collectors.toList());
                for (final XdatUsergroup group : groups) {
                    try {
                        instance.setAllowedGroups_groupid(group);
                    } catch (Exception e) {
                        log.error("An error occurred trying to set the group with ID {}", group.getId(), e);
                    }
                }
                break;
            case "allowDiffColumns":
                instance.setAllowDiffColumns(parser.getBooleanValue());
                break;
            case "briefDescription":
                instance.setBriefDescription(Objects.nonNull(parser.getText()) ? parser.getText() : "");
                break;
            case "description":
                instance.setDescription(Objects.nonNull(parser.getText()) ? parser.getText() : "");
                break;
            case "layeredSequence":
                instance.setLayeredsequence(Objects.nonNull(parser.getText()) ? parser.getText() : "");
                break;
            case "secure":
                instance.setSecure(parser.getBooleanValue());
                break;
            case "rootElementName":
                instance.setRootElementName(Objects.nonNull(parser.getText()) ? parser.getText() : "");
                break;
            case "id":
                instance.setId(Objects.nonNull(parser.getText()) ? parser.getText() : "");
                break;
            case "tag":
                instance.setTag(Objects.nonNull(parser.getText()) ? parser.getText() : "");
                break;
            case "sortByElementName":
                instance.setSortBy_elementName(Objects.nonNull(parser.getText()) ? parser.getText() : "");
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
