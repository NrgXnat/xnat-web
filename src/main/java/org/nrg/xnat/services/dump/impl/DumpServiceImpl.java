package org.nrg.xnat.services.dump.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dcm4che2.data.ElementDictionary;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xdat.XDAT;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.dicom.DicomSummary;
import org.nrg.xnat.helpers.ecat.EcatSummary;
import org.nrg.xnat.services.dump.DumpService;
import org.nrg.xnat.services.dump.util.DumpUtil;
import org.nrg.xnat.services.dump.util.Env;
import org.nrg.xnat.services.dump.util.HeaderTypeUtil;
import org.nrg.xnat.services.dump.util.ResourceTypeUtil;
import org.restlet.data.Status;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

@Service
public class DumpServiceImpl implements DumpService {
	
	private static final String SUMMARY_VALUE = "true";
	private static final String DICOM_DUMP_TYPE = "DICOM";
	private static final String ECAT_DUMP_TYPE = "ECAT";
	private Env env = null;
	// image type supported.
	private static final List<String> DIOCM_IMAGE_TYPES = new ArrayList<>();
	 private static final String ECAT_IMAGE_TYPE = "ECAT";
    private static final ElementDictionary TAG_DICTIONARY = ElementDictionary.getDictionary();
	
	@Override
	public List<DicomSummary> findAllDicomSummary(UserI user, String src, String summary, String[] fieldVals) throws Exception {

		initialization(src, fieldVals,DICOM_DUMP_TYPE, DIOCM_IMAGE_TYPES);

		if (SUMMARY_VALUE.equals(summary)) {
			Iterable<File> files = env.h.retrieveAll(env, user);
			DumpUtil<DicomSummary> dumpUtil = new DumpUtil<DicomSummary>(files, env.fields, DICOM_DUMP_TYPE);
			return dumpUtil.summaryDumpRender();
		} else {// default..
			String file = env.h.retrieve(this.env, user);
			DumpUtil<DicomSummary> dumpUtil = new DumpUtil<DicomSummary>(file, env.fields, DICOM_DUMP_TYPE);
			return dumpUtil.dicomHeaderDumpRender();
		}
	}
	
	@Override
	public List<EcatSummary> findAllEcatSummary(UserI user, String src, String summary, String[] fieldVals) throws Exception {
		
		initialization(src, fieldVals,ECAT_DUMP_TYPE, Arrays.asList(ECAT_IMAGE_TYPE));
		
		 if (SUMMARY_VALUE.equals(summary)){
			 Iterable<File> files = env.h.retrieveAll(env, user);
				DumpUtil<EcatSummary> dumpUtil = new DumpUtil<EcatSummary>(files, env.fields, ECAT_DUMP_TYPE);
				return dumpUtil.summaryDumpRender();
         }else{//default..
        	 Iterable<File> files = env.h.retrieveAll(env, user);
 			DumpUtil<EcatSummary> dumpUtil = new DumpUtil<EcatSummary>(files, env.fields, ECAT_DUMP_TYPE);
 			return null;
         }
	}
	

	/**
	 * 
	 * @param src
	 * @param fieldVals
	 * @param dicomImageType 
	 * @param ecatDumpType 
	 * @throws DataFormatException
	 * @throws InitializationException
	 */
	private void initialization(String src, String[] fieldVals, String dumpType, List<String> imageType) throws DataFormatException, InitializationException {
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
	        if(DICOM_DUMP_TYPE.equals(dumpType)) {
		        try {
		            final String dumpImageTypes = XDAT.getSiteConfigurationProperty("dumpImageTypes", "DICOM, secondary");
		            Collections.addAll(imageType, dumpImageTypes.split("\\s*,\\s*"));
		        } catch (ConfigServiceException e) {
		        	throw new InitializationException("Error trying to get site configuration property");
		        }
	        }
	        
	        new ResourceTypeUtil(imageType, dumpType);
        	new HeaderTypeUtil(dumpType);
	}

	
	/**
	 * 
	 * @param fieldVals
	 * @return
	 */
	private static ImmutableMap<Integer, Set<String>> getFields(String[] fieldVals) {
        ImmutableMap.Builder<Integer, Set<String>> fieldsb = ImmutableMap.builder();
        if(Objects.nonNull(fieldVals)) {
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
        }
        return fieldsb.build();
    }
	
}
