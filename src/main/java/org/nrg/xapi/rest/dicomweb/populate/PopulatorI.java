package org.nrg.xapi.rest.dicomweb.populate;

/**
 * Populate the XNAT DB to be consistent with DICOMweb requirements.
 *
 * An implementations of this is needed when data has been uploaded by a method that is not DICOMweb aware.
 *
 */
public interface PopulatorI {

    void populate( String project) throws Exception;
}
