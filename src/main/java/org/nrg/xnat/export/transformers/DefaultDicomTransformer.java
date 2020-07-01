package org.nrg.xnat.export.transformers;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.nrg.dicom.dicomedit.DE6Script;
import org.nrg.dicom.dicomedit.SerialScriptApplicator;

import org.nrg.xnat.export.anonymizer.utils.AnonymizeHelper;
import org.nrg.xnat.export.interfaces.TransformerI;

/**
 * @author Mohana Ramaratnam
 *
 */
public class DefaultDicomTransformer implements TransformerI {

	File outDir = null;
	List<AnonymizeHelper> anonymizeFileSequence = null;
	
	public DefaultDicomTransformer(final List<AnonymizeHelper> anonymizeFileSequence, final File outDir) {
		this.anonymizeFileSequence = anonymizeFileSequence;
		this.outDir = outDir;
	}
	
	public boolean transform(File inFile) {
        List<DE6Script> scripts = new ArrayList<>();
        if (anonymizeFileSequence == null) {
        	return false;
        }else {
	        try {
	        	for (AnonymizeHelper anon : anonymizeFileSequence) {
	                FileReader scriptReader = new FileReader( anon.getAnonymizeScriptFile());
	                File lookupTableFile = anon.getLookUpTable();
	                FileReader lookupFileReader = (lookupTableFile != null)? new FileReader( lookupTableFile): null;
	                scripts.add( new DE6Script( scriptReader, lookupFileReader));
	        	}
	        }catch(Exception e) {
	        }
        }
		return transform(inFile, outDir);
	}

	public boolean transform(File inFile, final File outD) {
		boolean succeeded = false;
	    List<DE6Script> scripts = new ArrayList<>();
		final SerialScriptApplicator applicator = new SerialScriptApplicator( scripts);

		return succeeded;
	}
	
}
