package org.nrg.xnat.snapshot.services.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.nrg.action.ClientException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.UriParserUtils;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.snapshot.services.SnapshotGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author pradeep.d
 *
 */
@Service
@Slf4j
public class SnapshotGenerationServiceImpl implements SnapshotGenerationService {
	
	/**
	 * @param catalogService
	 */
	@Autowired
	public SnapshotGenerationServiceImpl(final CatalogService catalogService) {
		_catalogService = catalogService;
	}

	@Override
	public String generateSnapshot(String projectID, String sessionIdentifier, String scanIdentifier, String gridView) {
		String path = null;
		try {
			_log.debug("SnapshotServiceImpl  generateSnapshot method start ");
			boolean verifySnapshot = verifySnapshots(projectID, sessionIdentifier, scanIdentifier);
			boolean verifyImage = verifyImage(projectID, sessionIdentifier, scanIdentifier);
			if (verifySnapshot && verifyImage) {
				path = getImagePath(projectID, sessionIdentifier, scanIdentifier);
			} else if (!verifySnapshot) {
				_message = "Snapshots Folder does not exist";
				_log.error(_message);
				return _message;
			} else {
				_message = "Snapshots- images does not exist";
				_log.error(_message);
				return _message;
			}
			System.out.println("generateSnapshot()- Snapshot path ::  " + path);
			_log.debug("generateSnapshot()- Snapshot path ::  " + path);
		} catch (Exception ex) {
			_log.error("SnapshotServiceImpl  generateSnapshot method error -" + ex.getMessage());
		}
		return path;
	}

	/**
	 * @param projectID
	 * @param sessionIdentifier
	 * @param scanIdentifier
	 * @return
	 */
	private String getImagePath(String projectID, String sessionIdentifier, String scanIdentifier) {
		String path;
		path = "/data/xnat/archive/" + projectID + "/arc001/" + sessionIdentifier + "/SCANS/" + scanIdentifier
				+ "/SNAPSHOTS";
		String image = getImageName(path);
		if (image != null && !image.isEmpty()) {
			return path + "/" + image;
		}
		System.out.println("getImagePath()- Snapshot path ::  " + path);
		_log.debug("getImagePath()- Snapshot path ::  " + path);
		return path;
	}

	/**
	 * @param projectID
	 * @param sessionIdentifier
	 * @param scanIdentifier
	 * @return
	 */
	private boolean verifySnapshots(String projectID, String sessionIdentifier, String scanIdentifier) {
		System.out.println("Snapshots verifySnapshots()");
		_log.debug("Snapshots verifySnapshots() ");
		// Provide as input accessionNo and ScanID
		String accessionNo = sessionIdentifier ; // Need to be implements to get accession# example :XNAT_E00004
		boolean flag = snapshotsFolder(accessionNo, scanIdentifier);
		return true;
	}

	/**
	 * @param projectID
	 * @param sessionIdentifier
	 * @param scanIdentifier
	 * @return
	 */
	private boolean verifyImage(String projectID, String sessionIdentifier, String scanIdentifier) {
		// implementation
		System.out.println("Snapshots verifyImage() ");
		_log.debug("Snapshots verifyImage() ");
		return true;
	}

	/**
	 * @param snapshotPath
	 * @return
	 */
	private String getImageName(String snapshotPath) {
		String extension = "";
		File snapshotFile = new File(snapshotPath);
		String[] fileNames = snapshotFile.list();
		if (fileNames != null && fileNames.length > 0) {
			for (String fileNm : fileNames) {
				System.out.println("fileNm:: " + fileNm);
				extension = fileNm.substring(fileNm.lastIndexOf(".") + 1).toLowerCase().trim();
				System.out.println("extension::" + extension);
				System.out.println("validImageType :: " + validImageType());
				if (validImageType().contains(extension)) {
					System.out.println("Snapshots getImageName()- Snapshot image ::  " + fileNm);
					_log.debug("Snapshots getImageName()- Snapshot image ::  " + fileNm);
					return fileNm;
				}
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	private Set<String> validImageType() {
		Set<String> validImageTypeList = new HashSet<>();
		validImageTypeList.add("jpg");
		validImageTypeList.add("gif");
		return validImageTypeList;
	}

	/**
	 * @param projectID
	 * @param sessionIdentifier
	 * @param scanIdentifier
	 * @return
	 */
	private boolean snapshotsFolder(String accessionNo, String scanIdentifier) {
		String parentUri = "/archive/experiments/" + accessionNo + "/scans/" + scanIdentifier + "/resources/"
				+ SNAPSHOTS + "/files";
		System.out.println("-------------Snapshots directory generation-----------------");
		_log.debug(" Snapshots directory generation ");
		String createdUri = null;
		try {
			final UserI userI = XDAT.getUserDetails();
			final URIManager.DataURIA uri = UriParserUtils.parseURI(parentUri);
			System.out.println("---------uri " + uri);
			String[] tags = { "" };
			final XnatResourcecatalog resourcecatalog = _catalogService.createAndInsertResourceCatalog(userI, parentUri,
					1, SNAPSHOTS, "Snapshots Desc", "GIF", SNAPSHOTS, tags);
			System.out.println(" resourcecatalog  ::" + resourcecatalog);
			createdUri = UriParserUtils.getArchiveUri(resourcecatalog);
			System.out.println(" Snapshots directory generation-  createdUri :: " + createdUri);
			_log.debug(" Snapshots directory generation-  createdUri :: " + createdUri);
		} catch (ClientException e) {
			_message = String.format(": " + e.getMessage(), parentUri);
			System.out.println(_message);
			_log.error(_message);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			_message = "Snapshot folder not generated";
			System.out.println("**" + _message);
			e.printStackTrace();
			_log.error(_message);
			return false;
		}
		return true;
	}

	private String _message = "";
	private final String SNAPSHOTS = "SNAPSHOTS";
	private final CatalogService _catalogService;
	private static final Logger _log = LoggerFactory.getLogger(SnapshotGenerationServiceImpl.class);
}
