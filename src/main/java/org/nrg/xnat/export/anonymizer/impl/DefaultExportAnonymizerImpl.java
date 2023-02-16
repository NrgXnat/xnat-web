package org.nrg.xnat.export.anonymizer.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.nrg.dicom.dicomedit.BaseScriptApplicator;
import org.nrg.dicom.mizer.exceptions.MizerException;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourceseries;
import org.nrg.xnat.export.interfaces.ExportAnonymizerI;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.python.jline.internal.Log;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
@Component
public class DefaultExportAnonymizerImpl implements ExportAnonymizerI{
	

	public boolean anonymize(XnatAbstractresourceI absRsc, final InputStream anonIs, File outDir) {
		boolean anonymized = false;
		try {
			final BaseScriptApplicator applicator  = new BaseScriptApplicator(anonIs);
			String uriAsStr = null;
			if(absRsc instanceof XnatResource){
				uriAsStr = ((XnatResource)absRsc).getUri();
			}else{
				uriAsStr = ((XnatResourceseries)absRsc).getPath();
			}
			if (uriAsStr != null) {
				URI uri = new URI(uriAsStr);
				String path  = uri.getPath();
				if (path != null) {
					File rscFolder = new File(path);
					if (rscFolder.exists()) {
					   anonymizeFiles(applicator, rscFolder, outDir);
					   anonymized = true;
					}
				}
			}
		}catch(Exception e) {
			Log.error(e.getLocalizedMessage());
		}
		return anonymized;
	}
	
	private void anonymizeFiles(BaseScriptApplicator applicator,File inFolder, File outDir) throws MizerException, FileNotFoundException, IOException{
		FilenameFilter filter = new FilenameFilter() {
	        @Override
	        public boolean accept(File f, String name) {
	            return  !name.endsWith(".xml");
	        }
	    };
	    
	    File[] files =  inFolder.listFiles(filter);
	    if (!outDir.exists()) {
	    	outDir.mkdirs();
	    }
	    	
	    for (File f : files) {
    		 FileOutputStream out = new FileOutputStream(mapFileName(outDir,f));
             applicator.apply(f).write(out);
	    }
	}
	
	private String getScanFolder(XnatProjectdata proj, String sessionLabel) {
		final String output_path = ArcSpecManager.GetInstance().getArchivePathForProject(proj.getId()) + proj.getCurrentArc() + "/"+ sessionLabel + "/SCANS";
		return output_path;
	}
	
	private  File mapFileName(File outFolder,File f) {
        final String presuffix = "-mod";
        final StringBuilder name = new StringBuilder(f.getName());
        final int presuffixLoc = name.lastIndexOf(".");
        if (presuffixLoc < 0) {
            name.append(presuffix);
        } else {
            name.insert(presuffixLoc, presuffix);
        }
        return new File(outFolder, name.toString());
    }
}
