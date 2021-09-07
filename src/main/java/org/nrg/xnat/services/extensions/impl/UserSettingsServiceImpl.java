package org.nrg.xnat.services.extensions.impl;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.services.SerializerService;
import org.nrg.mail.services.EmailRequestLogService;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.model.users.User;
import org.nrg.xdat.entities.XdatUserAuth;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xdat.services.XdatUserAuthService;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.extensions.util.UserAction;
import org.nrg.xnat.extensions.util.UserProperty;
import org.nrg.xnat.services.extensions.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserSettingsServiceImpl implements UserSettingsService {
    @Autowired
    public UserSettingsServiceImpl(final XdatUserAuthService userAuthService, final SerializerService serializer, final EmailRequestLogService requestLogService) {
        _userAuthService = userAuthService;
        _serializer = serializer;
        _requestLogService = requestLogService;
    }

    @Override
    public List<String> findAllUser(UserI user) throws InitializationException, InsufficientPrivilegesException {
        setBuildersAndPropertyMappings();
        validateRole(user);
        return new ArrayList<>(Users.getAllLogins());
    }

    @Override
    public User findUserByUserId(UserI user, String userId) throws InitializationException, InsufficientPrivilegesException, UserNotFoundException, UserInitException {
        if (StringUtils.isBlank(userId)) {
            throw new UserInitException("No user ID provided by user " + user.getUsername());
        }

        setBuildersAndPropertyMappings();
        validateRole(user);
        return getXdatUser(Users.getUser(userId));
    }

    @Override
    public void updateUserAction(String action, String userId) throws InitializationException, DataFormatException {
        if (StringUtils.isNoneBlank(action)) {
            handleAction(action, userId);
        }
    }

    @Override
    public void deleteUserAction(String action, String userId) throws InitializationException, DataFormatException {
        if (StringUtils.isNoneBlank(action)) {
            handleAction(action, userId);
        }
    }

    private void handleAction(String requestAction, String userId) throws InitializationException, DataFormatException {
        final UserAction action = UserAction.action(requestAction);
        if (action != UserAction.Reset && action != UserAction.ResetEmailRequests) {
            throw new DataFormatException("Unknown action: " + action);
        }
        switch (action) {
            case Reset:
                final List<XdatUserAuth> auths = getAuth(userId);
                if (auths != null) {
                    for (final XdatUserAuth auth : auths) {
                        _userAuthService.resetFailedLogins(auth);
                    }
                }
                break;
            case ResetEmailRequests:
                try {
                    _requestLogService.unblockEmail(Users.getUser(userId).getEmail());
                } catch (Exception e) {
                    throw new InitializationException("Error resetting email requests for " + userId);
                }
        }
    }

    private List<XdatUserAuth> getAuth(String userId) throws DataFormatException {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        final List<XdatUserAuth> auths = _userAuthService.getUsersByXdatUsername(userId);
        if (auths == null || auths.isEmpty()) {
            throw new DataFormatException("As of this release, you must specify a user on which to perform.");
        }
        return auths;
    }

    private User getXdatUser(UserI xdatUser) {
        return User.builder()
                   .email(xdatUser.getEmail())
                   .firstName(xdatUser.getFirstname())
                   .lastName(xdatUser.getLastname())
                   .enabled(xdatUser.isEnabled())
                   .verified(xdatUser.isVerified())
                   .build();
    }

    private void setBuildersAndPropertyMappings() throws InitializationException {
        synchronized (UserSettingsService.class) {
            XPath xpath = XPathFactory.newInstance().newXPath();
            try {
                final DocumentBuilder                    builder          = _serializer.getDocumentBuilder();
                final Map<UserProperty, XPathExpression> propertyMappings = new HashMap<>();
                for (UserProperty property : UserProperty.values()) {
                    propertyMappings.put(property, xpath.compile(XPATH_EXPRESSIONS.containsKey(property) ? XPATH_EXPRESSIONS.get(property) : String.format("/user/%s", property.toString())));
                }
            } catch (XPathExpressionException | ParserConfigurationException exception) {
                throw new InitializationException("Error in initialization", exception);
            }
        }
    }

    private void validateRole(UserI user) throws InsufficientPrivilegesException {
        if (!Roles.isSiteAdmin(user)) {
            throw new InsufficientPrivilegesException("User does not have privileges to access this project.");
        }
    }

    private static final Map<UserProperty, String> XPATH_EXPRESSIONS = ImmutableMap.of(UserProperty.userAuths, "//userAuths/userAuth",
                                                                                       UserProperty.authId, "authId",
                                                                                       UserProperty.method, "method",
                                                                                       UserProperty.methodId, "methodId");

    private final XdatUserAuthService    _userAuthService;
    private final SerializerService      _serializer;
    private final EmailRequestLogService _requestLogService;
}
