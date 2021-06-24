package org.nrg.xnat.services.extensions.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.extensions.ScanQualityLabelService;
import org.nrg.xnat.turbine.utils.ScanQualityUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ScanQualityLabelServiceImpl implements ScanQualityLabelService {

	@Override
	public String findAllScanQualityLable(UserI user, String projectId) throws InitializationException {
		if (log.isDebugEnabled()) {
			log.debug("Entering the scan quality label represent() method");
		}
		try {
			List<String> labels = ScanQualityUtils.getQualityLabels(projectId, user);
			JSONObject json = new JSONObject();
			json.put(StringUtils.isBlank(projectId) ? SITE_KEY : projectId, labels);
			return json.toString();
		} catch (JSONException e) {
			throw new InitializationException(e);
		}
	}
	
	private static final String SITE_KEY = "site";
}
