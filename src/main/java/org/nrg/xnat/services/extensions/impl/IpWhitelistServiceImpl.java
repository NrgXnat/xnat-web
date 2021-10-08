package org.nrg.xnat.services.extensions.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.nrg.config.entities.Configuration;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.config.services.ConfigService;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.extensions.IpWhitelistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

import lombok.extern.slf4j.Slf4j;

import static org.nrg.config.entities.Configuration.DISABLED_STRING;

@Service
@Slf4j
public class IpWhitelistServiceImpl implements IpWhitelistService {

	@Autowired
	public IpWhitelistServiceImpl(ConfigService configurationService) {
		_configurationService = configurationService;
	}

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
//			return getWhitelistConfiguration(user);

//			return Optional.ofNullable(getConfigValue(IP_WHITELIST_TOOL, IP_WHITELIST_PATH).orElseGet(() -> {
//				try {
//					return createDefaultWhitelist(user).getContents();
//				} catch (ConfigServiceException e) {
//					log.error("User {} tried to create the default whitelist configuration but an error occurred", user.getUsername(), e);
//					return null;
//				}
//			}));

		} catch (ConfigServiceException e) {
			throw new InitializationException(e.getMessage());
		}
	}
	public String getConfigValue(final String toolName, final String path, final String defaultValue) {
		return getConfigValue(Optional.ofNullable(_configurationService.getConfig(toolName, path)).orElse(null), defaultValue);
	}
	public String getWhitelistConfiguration(UserI user) throws ConfigServiceException {
		return Optional.ofNullable(getConfigValue(IP_WHITELIST_TOOL, IP_WHITELIST_PATH)).orElseGet(() -> {
			try {
				return createDefaultWhitelist(user).getContents();
			} catch (ConfigServiceException e) {
				log.error("User {} tried to create the default whitelist configuration but an error occurred", user.getUsername(), e);
				return null;
			}
		});
	}


	public static String getConfigValue(final String toolName, final String path) {
		return getConfigValue(toolName, path);
	}

	private String getConfigValue(final Configuration config, final String defaultValue) {
		return config == null || StringUtils.equals(DISABLED_STRING, config.getStatus()) ? defaultValue : config.getContents();
	}

	private synchronized Configuration createDefaultWhitelist(final UserI user) throws ConfigServiceException {
		final String username = user.getUsername();
		final String reason   = Roles.isSiteAdmin(user) ? "Site admin created default IP whitelist from localhost IP values." : "User hit site before default IP whitelist was constructed.";
		return _configurationService.replaceConfig(username, reason, IP_WHITELIST_TOOL, IP_WHITELIST_PATH, String.join("\n", Stream.concat(LOCALHOST_IPS.stream(), getInetAddresses().stream().map(InetAddress::getHostAddress).map(address -> StringUtils.substringBefore(address, "%"))).collect(Collectors.toSet())));
	}
	@Override
	public void updateIpWhiteList(UserI user, String whitelist) throws InitializationException, IOException {
		try {
			List<String> addresses = new ArrayList<>(Arrays.asList(whitelist.split("[\\s,]+")));
//			for (String localhost : XDAT.getLocalhostIPs()) {
			for (String localhost : Stream.concat(LOCALHOST_IPS.stream(), getInetAddresses().stream().map(InetAddress::getHostAddress).map(address -> StringUtils.substringBefore(address, "%"))).collect(Collectors.toSet())) {

				if (!addresses.contains(localhost)) {
					addresses.add(localhost);
				}
			}
			_configurationService.replaceConfig(user.getLogin(), "", IP_WHITELIST_TOOL, IP_WHITELIST_PATH, Joiner.on("\n").join(addresses));
		} catch (ConfigServiceException e) {
			log.error("Error occurred writing to the configuration service", e);
			throw new InitializationException(e.getMessage());
		}
	}
		public static List<InetAddress> getInetAddresses() {
			if (INET_ADDRESSES.isEmpty()) {
				synchronized (INET_ADDRESSES) {
					try {
						INET_ADDRESSES.addAll(Arrays.asList(InetAddress.getAllByName(InetAddress.getLocalHost().getHostName())));
					} catch (UnknownHostException exception) {
						log.error("Localhost is unknown host... Wha?", exception);
					}
				}
			}
			return INET_ADDRESSES;
		}

	private final String   IP_WHITELIST_TOOL               = "ipWhitelist";
	private final String   IP_WHITELIST_PATH               = "/system/ipWhitelist";
	private static final List<String>      LOCALHOST_IPS  = Arrays.asList("127.0.0.1", "0:0:0:0:0:0:0:1");
	private static final List<InetAddress> INET_ADDRESSES = new ArrayList<>();
	private final ConfigService _configurationService;
}
