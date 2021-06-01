package org.nrg.xnat.services.dump.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dcm4che2.data.ElementDictionary;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xdat.XDAT;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.dicom.DicomHeaderDump;
import org.nrg.xnat.helpers.dicom.DicomSummary;
import org.nrg.xnat.helpers.dicom.DicomSummaryHeaderDump;
import org.nrg.xnat.services.dump.DicomDumpService;
import org.nrg.xnat.services.dump.util.DumpUtil;
import org.nrg.xnat.services.dump.util.Env;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

@Service
public class DicomDumpServiceImpl implements DicomDumpService {
	private static final String SUMMARY_VALUE = "true";
	private Env env = null;
	 // image type supported.
	 private static final List<String> imageTypes = new ArrayList<>();
    private static final ElementDictionary TAG_DICTIONARY = ElementDictionary.getDictionary();
	
	@Override
	public List<DicomSummary> findAll(UserI user, String src, String summary, String[] fieldVals) throws Exception {
		 if(StringUtils.isBlank(src)) {
			 throw new DataFormatException("Please set the src parameter");
		 }
	        Map<Integer, Set<String>> fields =  new HashMap<>();
	        try {
	            fields = getFields(fieldVals);
	        } catch (IllegalArgumentException e) {
	        	 env = null;
	        	 throw new DataFormatException(e.getMessage());
	        }
	        env = new Env(src, fields);
	        try {
	            final String dumpImageTypes = XDAT.getSiteConfigurationProperty("dumpImageTypes", "DICOM, secondary");
	            Collections.addAll(imageTypes, dumpImageTypes.split("\\s*,\\s*"));
	        } catch (ConfigServiceException e) {
	        	throw new InitializationException("Error trying to get site configuration property");
	        }
        if (SUMMARY_VALUE.equals(summary)){
        	Iterable<File> files = env.h.retrieveAll(env, user, imageTypes);
        	DumpUtil dumpUtil = new DumpUtil(files, env.fields);
        	return dumpUtil.dicomSummaryDumpRender();
        }else{//default..
        	String file = env.h.retrieve(this.env, user);
        	DumpUtil dumpUtil = new DumpUtil(file, env.fields);
        	return dumpUtil.dicomHeaderDumpRender();
        }
	}
	

	private static ImmutableMap<Integer, Set<String>> getFields(String[] fieldVals) {
        ImmutableMap.Builder<Integer, Set<String>> fieldsb = ImmutableMap.builder();
        for (final String field : fieldVals) {
            final String[] parts = field.split(":");
            final String tag_s = parts[0];
            final Set<String> subs = Sets.newHashSet();
            subs.addAll(Arrays.asList(parts).subList(1, parts.length));
            int tag;
            try {
                tag = TAG_DICTIONARY.tagForName(tag_s);
            } catch (IllegalArgumentException e) {
                try {
                    tag = Integer.parseInt(tag_s, 16);
                } catch (NumberFormatException e1) {
                    throw new IllegalArgumentException("not a valid DICOM attribute tag: " + tag_s, e1);
                }
            }
            fieldsb.put(tag, subs);
        }
        return fieldsb.build();
    }
}
