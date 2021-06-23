package org.nrg.xnat.services.extensions.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.extensions.IpWhitelistService;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IpWhitelistServiceImpl implements IpWhitelistService {

	@Override
	public String findAllIpWhiteList(UserI user) throws InsufficientPrivilegesException, InitializationException {
		if (!Roles.isSiteAdmin(user)) {
			throw new InsufficientPrivilegesException(user.getUsername());
		}
		if (log.isDebugEnabled()) {
			log.debug("Entering the IP whitelist represent() method");
		}

		try {
			return XDAT.getWhitelistConfiguration(user);
		} catch (ConfigServiceException e) {
			throw new InitializationException(e.getMessage());
		}
	}

	@Override
	public void updateIpWhiteList(UserI user, String whitelist) throws InitializationException, IOException {
		try {
			List<String> addresses = new ArrayList<>(Arrays.asList(whitelist.split("[\\s,]+")));
			for (String localhost : XDAT.getLocalhostIPs()) {
				if (!addresses.contains(localhost)) {
					addresses.add(localhost);
				}
			}
			XDAT.getConfigService().replaceConfig(user.getLogin(), "", XDAT.IP_WHITELIST_TOOL, XDAT.IP_WHITELIST_PATH, Joiner.on("\n").join(addresses));
		} catch (ConfigServiceException e) {
			log.error("Error occurred writing to the configuration service", e);
			throw new InitializationException(e.getMessage());
		}
	}
}
