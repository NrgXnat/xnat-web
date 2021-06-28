package org.nrg.xnat.services.extensions.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.nrg.mail.services.EmailRequestLogService;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.entities.XdatUserAuth;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.extensions.util.UserAction;
import org.nrg.xnat.extensions.util.UserProperty;
import org.nrg.xnat.extensions.util.XdatUserUtil;
import org.nrg.xnat.services.extensions.UserSettingsService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserSettingsServiceImpl implements UserSettingsService {

	
	@Override
	public List<String> findAllUser(UserI user) throws InitializationException, InsufficientPrivilegesException {
		
		setBuildersAndPropertyMappings();
		
		validteRole(user);
		
		 return new ArrayList<>(Users.getAllLogins());
	}
	
	@Override
	public XdatUserUtil findUserByUserId(UserI user, String userId) throws InitializationException, InsufficientPrivilegesException, UserNotFoundException, UserInitException {
		if(StringUtils.isBlank(userId)) {
			
		}
		setBuildersAndPropertyMappings();
		
		validteRole(user);
		 UserI requestedUser = Users.getUser(userId);
		return getXdatUser(requestedUser);
	}
	
	@Override
	public void updateUserAction(String action, String userId) throws InitializationException, DataFormatException {
		if(StringUtils.isNoneBlank(action)) {
			handleAction(action, userId);
		}
	}

	@Override
	public void deleteUserAction(String action, String userId) throws InitializationException, DataFormatException {
		if(StringUtils.isNoneBlank(action)) {
			handleAction(action, userId);
		}
	}
	
	
	private void handleAction(String requestAction, String userId) throws InitializationException, DataFormatException {
		UserAction action = UserAction.action(requestAction);
		 List<XdatUserAuth>  auths = getAuth(userId);
        if (action == UserAction.Reset) {
            for (final XdatUserAuth auth : auths) {
                XDAT.getXdatUserAuthService().resetFailedLogins(auth);
            }
        } else if (action == UserAction.ResetEmailRequests) {
            try {
                final UserI u = Users.getUser(userId);
                final EmailRequestLogService requests = XDAT.getContextService().getBean(EmailRequestLogService.class);
                requests.unblockEmail(u.getEmail());
            } catch (Exception e) {
               throw new InitializationException("Error resetting email requests for " + userId);
            }
        } else {
            throw new RuntimeException("Unknown action: " + action);
        }
    }
	
	
	private List<XdatUserAuth> getAuth(String userId) throws DataFormatException {
		List<XdatUserAuth> auths; 
		 if (!StringUtils.isBlank(userId)) {
             auths = XDAT.getXdatUserAuthService().getUsersByXdatUsername(userId);
             if (auths == null || auths.size() == 0) {
                throw new DataFormatException("As of this release, you must specify a user on which to perform.");
             }
         } else {
             auths = null;
         }
		 return auths;
	}

	
	private XdatUserUtil getXdatUser(UserI xdatUser) {
		return XdatUserUtil.builder()
				.login(xdatUser.getLogin())
				.email(xdatUser.getEmail())
				.firstname(xdatUser.getFirstname())
				.lastname(xdatUser.getLastname())
				.enabled(xdatUser.isEnabled())
				.verified(xdatUser.isVerified())
				.build();
	}

	private void setBuildersAndPropertyMappings() throws InitializationException {
		if (builder == null) {
			synchronized (UserSettingsService.class) {
				XPath xpath = XPathFactory.newInstance().newXPath();
				try {
					builder = XDAT.getSerializerService().getDocumentBuilder();
					propertyMappings = new HashMap<>();
					for (UserProperty property : UserProperty.values()) {
						propertyMappings.put(property,xpath.compile(XPATH_EXPRESSIONS.containsKey(property) ? XPATH_EXPRESSIONS.get(property) : String.format("/user/%s", property.toString())));
					}
				} catch (XPathExpressionException | ParserConfigurationException exception) {
					throw new InitializationException("Error in initialization", exception);
				}
			}
		}
	}
	
	private void validteRole(UserI user) throws InsufficientPrivilegesException {
		if (!Roles.isSiteAdmin(user)) {
			//action = null;
			//isJsonRequested = false;
			//userId = null;
			throw new InsufficientPrivilegesException("User does not have privileges to access this project.");
			
		}
	}
	
	
	private static final Map<UserProperty, String> XPATH_EXPRESSIONS = new HashMap<UserProperty, String>() {{
        put(UserProperty.userAuths, "//userAuths/userAuth");
        put(UserProperty.authId, "authId");
        put(UserProperty.method, "method");
        put(UserProperty.methodId, "methodId");
    }};
	
	private static DocumentBuilder builder;
    private static Map<UserProperty, XPathExpression> propertyMappings;
//    private  String userId;
//    private  UserAction action;
//    private String payload;
//    private Document document;
//    private JsonNode node;
//    private  boolean isJsonRequested;
	
}
