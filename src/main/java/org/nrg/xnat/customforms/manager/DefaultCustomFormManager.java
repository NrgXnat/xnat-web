/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2021, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * @author: Mohana Ramaratnam (mohana@radiologics.com)
 * @since: 07-03-2021
 */
package org.nrg.xnat.customforms.manager;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.XDAT;
import org.nrg.xnat.customforms.exceptions.CustomFormFetcherNotFoundException;
import org.nrg.xnat.customforms.interfaces.CustomFormDisplayFieldsI;
import org.nrg.xnat.customforms.interfaces.CustomFormFetcherI;
import org.nrg.xnat.customforms.interfaces.annotations.CustomFormFetcherAnnotation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DefaultCustomFormManager {

    /**
     * Based on the annotation, returns the Custom Form Fetcher
     * @param type - the type in the annonation @see CustomFormFetcherAnnotation
     * @return - the CustomFormFetcher for the given type
     * @throws CustomFormFetcherNotFoundException
     */
    public CustomFormFetcherI getCustomFormFetcherByTypeAnnotation(String type) throws CustomFormFetcherNotFoundException {
        List<CustomFormFetcherI> fetchers = null;
        CustomFormFetcherI formFetcher = null;
        try {
            Map<String, CustomFormFetcherI> serviceMap = XDAT.getContextService().getBeansOfType(CustomFormFetcherI.class);
            if (serviceMap != null) {
                fetchers = new ArrayList<>(serviceMap.values());
            }
        } catch (Exception e) {
            log.error("Unable to retrieve injected CustomFetcher beans", e);
            throw new CustomFormFetcherNotFoundException("Could not retrieve custom form fetcher of class " + CustomFormFetcherI.class.getName(), new IllegalArgumentException());
        }

        if (fetchers == null || fetchers.isEmpty()) {
            log.trace("No Custom Fetcher  beans");
            throw new CustomFormFetcherNotFoundException("No form fetching beans ", new IllegalArgumentException());
        }

        for (CustomFormFetcherI e : fetchers) {
            final CustomFormFetcherAnnotation annotation = e.getClass().getAnnotation(CustomFormFetcherAnnotation.class);
            if (annotation != null) {
                if (type.equals(annotation.type())) {
                    formFetcher = e;
                    break;
                }
            }

        }
        return formFetcher;
    }

    public CustomFormDisplayFieldsI getCustomFormDisplayFieldBuilderByTypeAnnotation(String type) throws CustomFormFetcherNotFoundException {
        List<CustomFormDisplayFieldsI> displayBuilders = null;
        CustomFormDisplayFieldsI displayBuilder = null;
        try {
            Map<String, CustomFormDisplayFieldsI> serviceMap = XDAT.getContextService().getBeansOfType(CustomFormDisplayFieldsI.class);
            if (serviceMap != null) {
                displayBuilders = new ArrayList<>(serviceMap.values());
            }
        } catch (Exception e) {
            log.error("Unable to retrieve injected CustomFetcher beans", e);
            throw new CustomFormFetcherNotFoundException("Could not retrieve custom form fetcher of class " + CustomFormDisplayFieldsI.class.getName(), new IllegalArgumentException());
        }

        if (displayBuilders == null || displayBuilders.isEmpty()) {
            log.trace("No Custom Fetcher  beans");
            throw new CustomFormFetcherNotFoundException("No form fetching beans ", new IllegalArgumentException());
        }

        for (CustomFormDisplayFieldsI e : displayBuilders) {
            final CustomFormFetcherAnnotation annotation = e.getClass().getAnnotation(CustomFormFetcherAnnotation.class);
            if (annotation != null) {
                if (type.equals(annotation.type())) {
                    displayBuilder = e;
                    break;
                }
            }

        }
        return displayBuilder;
    }

}
