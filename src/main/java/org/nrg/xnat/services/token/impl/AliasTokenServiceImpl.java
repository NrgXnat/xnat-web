package org.nrg.xnat.services.token.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.framework.services.SerializerService;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotAuthenticatedException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.entities.AliasToken;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.services.AliasTokenService;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AliasTokenServiceImpl<T> implements TokenService<T> {
	
	@Autowired
	public AliasTokenServiceImpl() throws NotFoundException{
		 _serializer = XDAT.getSerializerService();
	        if (_serializer == null) {
	        	throw new NotFoundException("ERROR: Serializer service was not properly initialized.");
	        }
	        _service = XDAT.getContextService().getBean(AliasTokenService.class);
	        if (_service == null) {
	        	throw new NotFoundException("ERROR: Alias token service was not properly initialized.");
	        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findAll(UserI user, String operation, String tokenId, String secret, String requestedUserName) throws DataFormatException, NotFoundException, NotAuthenticatedException, InsufficientPrivilegesException {
		final boolean hasUsername = StringUtils.isNotBlank(requestedUserName);
		if (hasUsername && !StringUtils.equals(requestedUserName, user.getUsername()) && !Roles.isSiteAdmin(user)) {
			throw new InsufficientPrivilegesException("Only admins can work with alias tokens for other users.");
		}
		if (StringUtils.isBlank(operation)) {
			throw new DataFormatException("Operation value wasn't found");
		}
		String token = getToken(tokenId);
		switch (operation) {
		case OP_ISSUE:
			T issueResult = (T) getIssueOperatioAliasToken(operation, hasUsername, user, requestedUserName);
			if (Objects.isNull(issueResult)) {
				throw new NotFoundException("Alias token issue opertion wasn't found");
			}
			return issueResult;
		case OP_SHOW:
			T showResults = (T) getShowOperationAliasToken(operation, hasUsername, user, requestedUserName);
			if (Objects.isNull(showResults)) {
				throw new NotFoundException("Alias token show operation wasn't found");
			}
			return showResults;
		case OP_VALIDATE:
			return (T) getValidateOperationAliasToken(token, secret);
		case OP_INVALIDATE:
			return (T) getInValidateOperationAliasToken(token);
		default:
			throw new DataFormatException("Unknown operation: " + operation);
		}
	}
	
	private AliasToken getIssueOperatioAliasToken(String operation, boolean hasUsername, UserI user, String requestedUserName) throws DataFormatException {
		if (operation.equals(OP_ISSUE)) {
			try {
				return hasUsername ? _service.issueTokenForUser(requestedUserName) : _service.issueTokenForUser(user);
			} catch (Exception e) {
				throw new DataFormatException("An error occurred retrieving the user: " + requestedUserName);
			}
		}
		return null;
	}
	
	private List<AliasToken> getShowOperationAliasToken(String operation, boolean hasUsername, UserI user, String requestedUserName) throws DataFormatException {
		if (operation.equals(OP_SHOW)) {
			final String username = hasUsername ? requestedUserName : user.getUsername();
			if (StringUtils.isBlank(username)) {
				throw new DataFormatException("No user found: " + requestedUserName);
			}
			return _service.findTokensForUser(username);
		}
		return null;

	}
	
	private HashMap<String, String> getValidateOperationAliasToken(String token , String secret) throws NotAuthenticatedException{
		if (StringUtils.isBlank(token) || StringUtils.isBlank(secret)) {
			throw new NotAuthenticatedException("You must specify both token and secret to validate a token.");
        }
		final HashMap<String, String> results = new HashMap<>();
        results.put("valid", _service.validateToken(token, secret));
        return results;
	}
	
	private HashMap<String, String> getInValidateOperationAliasToken(String token){
		final HashMap<String, String> results = new HashMap<>();
		_service.invalidateToken(token);
		results.put("result","OK");
		return results;
	}
	
	private String getToken(String tokenId) throws DataFormatException {
		String token = null;
		final AliasToken aliseToken;
        if (StringUtils.isBlank(tokenId)) {
        	token = null;
            aliseToken = null;
        } else if (AliasToken.isAliasFormat(tokenId)) {
        	aliseToken = _service.locateToken(tokenId);
            if (aliseToken == null) {
            	throw new DataFormatException("Can't find an alias token with the alias " + tokenId);
            }
            token = tokenId;
        } else if (NumberUtils.isCreatable(tokenId)) {
            try {
            	aliseToken = _service.get(NumberUtils.toLong(tokenId));
            	token = aliseToken.getAlias();
            } catch (NotFoundException e) {
            	throw new DataFormatException("Can't find an alias token with the ID " + tokenId);
            }
        } else {
        	throw new DataFormatException( "Can't find an alias token with the ID " + tokenId);
        }
		return token;
	}
	
	
	private final SerializerService _serializer;
	private final AliasTokenService _service;
	private static final String OP_SHOW = "show";
	private static final String OP_ISSUE = "issue";
	private static final String OP_VALIDATE = "validate";
	private static final String OP_INVALIDATE = "invalidate";

}
