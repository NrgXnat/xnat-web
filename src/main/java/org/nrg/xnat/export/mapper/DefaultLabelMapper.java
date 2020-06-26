package org.nrg.xnat.export.mapper;

import java.io.File;

import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xnat.export.interfaces.ExportLabelMappingI;
import org.springframework.stereotype.Component;

/**
 * @author Mohana Ramaratnam
 *
 */
@Component
public class DefaultLabelMapper implements ExportLabelMappingI{
	public void label(XnatImagesessiondata imageSession, File mappingFile) {
		
	}
}
