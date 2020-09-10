package org.nrg.xnat.services.resources.files.impl;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileUploadException;
import org.nrg.action.ClientException;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.services.resources.files.FileListService;
import org.nrg.xft.XFTTable;
import org.restlet.data.MediaType;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class FileListServiceImpl implements FileListService {

	@Override
	public boolean allowPut() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowPost() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Representation represent(Variant variant) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handlePut() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePost() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDelete() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<FileWriterWrapperI> getFileWritersAndLoadParams(Representation entity, boolean useFileFieldName)
			throws FileUploadException, ClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Representation representTable(XFTTable table, MediaType mediaType, Hashtable<String, Object> parameters,
			Map<String, Map<String, String>> columnProperties, Map<String, String> sessionMapping) {
		// TODO Auto-generated method stub
		return null;
	}

}
