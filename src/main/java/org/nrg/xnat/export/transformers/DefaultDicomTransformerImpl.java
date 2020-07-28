package org.nrg.xnat.export.transformers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nrg.dicom.dicomedit.DE6Script;
import org.nrg.dicom.dicomedit.SerialScriptAnonymizer;
import org.nrg.dicom.mizer.exceptions.MizerException;
import org.nrg.xnat.export.annotation.TransformerHandler;
import org.nrg.xnat.export.anonymizer.utils.AnonymizeHelper;
import org.nrg.xnat.export.exception.FailedToTransformException;
import org.nrg.xnat.export.interfaces.TransformerI;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Component
@TransformerHandler(handler = "XNAT-TCIA-DICOM-ANONYMIZER")
@Slf4j
public class DefaultDicomTransformerImpl implements TransformerI {

	List<AnonymizeHelper> anonymizeFileSequence = null;
    List<DE6Script> scripts = new ArrayList<>();
	
    public void init(Map<String, Object> params) {
    	Object scriptObj = params.get("SCRIPT_FILE");
    	Object lookupObj = params.get("LOOKUP_TABLE");
    	File scriptFile = null;
    	File lookUpTable = null;
    	String scriptStr = null;
    	String lookUpTableStr = null;
    	if (scriptObj != null) { 
    		if (scriptObj instanceof File) {
    			scriptFile = (File)scriptObj;
    		}else if (scriptObj instanceof String) {
    			scriptStr = (String)scriptObj;
    		}
    	}
    	if (lookupObj != null) {
    		if (lookupObj instanceof File) {
    			lookUpTable = (File)lookupObj;
    		}else if (lookupObj instanceof String) {
    			lookUpTableStr = (String)lookupObj;
    		}
    	}
    	if (scriptFile != null ) {
    		AnonymizeHelper aHelper = new AnonymizeHelper(scriptFile, lookUpTable);
    		anonymizeFileSequence = new ArrayList<AnonymizeHelper>();
    		anonymizeFileSequence.add(aHelper);
    	}else if (scriptStr != null ) {
    		AnonymizeHelper aHelper = new AnonymizeHelper(scriptStr, lookUpTableStr);
    		anonymizeFileSequence = new ArrayList<AnonymizeHelper>();
    		anonymizeFileSequence.add(aHelper);
    	}
    }

	public boolean transform( File inFile, final File outD) throws FailedToTransformException{
        boolean success = true;
		if (anonymizeFileSequence == null || anonymizeFileSequence.size() < 1 ) {
        	throw new  FailedToTransformException("No anonymize file sequence provided. Expectation failed.");// Nothing to do here
        }else {
	        try {
	        	for (AnonymizeHelper anon : anonymizeFileSequence) {
	        		Reader scriptReader = null, lookupReader = null;
	                File anonFile = anon.getAnonymizeScriptFile();
	                File lookupTableFile = anon.getLookUpTable();
	                String anonStr = anon.getAnonStr();
	                String lookUpStr = anon.getLookupStr();
	                if (anonFile != null) {
		        		scriptReader = new FileReader( anonFile);
	                }else if (anonStr != null) {
		        		scriptReader = new StringReader( anonStr);
	                }
	                if (lookupTableFile != null) {
		               lookupReader = new FileReader( lookupTableFile);
	                }else if (lookUpStr != null) {
			           lookupReader = new StringReader( lookUpStr);
	                }
	                scripts.add( new DE6Script( scriptReader, lookupReader));
	        	}
	            SerialScriptAnonymizer anonymizer = new SerialScriptAnonymizer( scripts, Paths.get(inFile.getAbsolutePath()), Paths.get(outD.getAbsolutePath()));
	            anonymizer.anon();
	        } catch (MizerException | IOException e) {
	            log.error(e.getMessage());
	            throw new FailedToTransformException(e.getMessage());
	        }		
        }
        return success;
	}

	
	public boolean transform(InputStream inStream, OutputStream outD) throws FailedToTransformException{
		return false;
	}
}
