package org.nrg.xnat.snapshot.services.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.nrg.action.ClientException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.bean.XnatImagescandataBean;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.archive.ResourceData;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.UriParserUtils;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.snapshot.services.SnapshotGenerationService;
import org.nrg.xnat.snapshot.services.convert.SnapshotDicomConventImage;
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
			boolean verifySnapshot = verifySnapshots(sessionIdentifier, scanIdentifier);
			boolean verifyImage = verifyImage(sessionIdentifier, scanIdentifier);
			if (verifySnapshot && verifyImage) {
				path = getValidImage(sessionIdentifier, scanIdentifier);
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
	 * @param accessionId
	 * @param scanIdentifier
	 * @return
	 */
	private String getValidImage(String accessionId, String scanIdentifier) {
		String parentUri = ROOT_URI + accessionId + "/scans/" + scanIdentifier + SNAPSHOTS_RESOURCE;
		String snanshotsImage = null;
		try {
			ResourceData resourceData = _catalogService.getResourceDataFromUri(parentUri);
			XnatResourcecatalog xnatResourcecatalog = resourceData.getCatalogResource();
			snanshotsImage = getImage(xnatResourcecatalog.getUri());
		} catch (ClientException e) {
			_message = String.format(e.getMessage(), parentUri);
			_log.error(_message);
		} catch (Exception e) {
			_log.error(" Snapshots image error:: " + e.getMessage());
		}
		System.out.println("getValidImage()- Snapshot path ::  " + snanshotsImage);
		_log.debug("getValidImage()- Snapshot path ::  " + snanshotsImage);
		return snanshotsImage;
	}

	/**
	 * @param projectID
	 * @param sessionIdentifier
	 * @param scanIdentifier
	 * @return
	 * @throws Exception 
	 */
	private boolean verifySnapshots(String accessionId, String scanIdentifier) throws Exception {
		System.out.println("Snapshots verifySnapshots()");
		_log.debug("Snapshots verifySnapshots() ");
		// implementation pending
		boolean flag = snapshotsFolder(accessionId, scanIdentifier);
		return true;
	}

	/**
	 * @param accessionId
	 * @param scanIdentifier
	 * @return
	 * @throws Exception
	 */
	private boolean verifyImage(String accessionId, String scanIdentifier) throws Exception {
		System.out.println("Snapshots verifyImage() ");
		_log.debug("Snapshots verifyImage() ");
		// implementation pending
		boolean imageFlag = imageUpload(accessionId, scanIdentifier);
		return true;
	}

	/**
	 * @param snapshotPath
	 * @return
	 */
	private String getImage(String snapshotPath) {
		String extension = "";
		String path = new File(snapshotPath).getParent();
		File snapshotFile = new File(path);
		String[] fileNames = snapshotFile.list();
		if (fileNames != null && fileNames.length > 0) {
			for (String fileNm : fileNames) {
				System.out.println("fileNm:: " + fileNm);
				extension = fileNm.substring(fileNm.lastIndexOf(".") + 1).toLowerCase().trim();
				System.out.println("extension::" + extension);
				System.out.println("validImageType :: " + validImageType());
				if (validImageType().contains(extension)) {
					System.out.println("Snapshots getImage()- Snapshot image ::  " + fileNm);
					_log.debug("Snapshots getImage()- Snapshot image ::  " + fileNm);
					return path + "/" + fileNm;
				}
			}
		}
		return "Snapshots -Valid image does not exist";
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
	 * @param accessionNo
	 * @param scanIdentifier
	 * @return
	 */
	private boolean snapshotsFolder(String accessionNo, String scanIdentifier) {
		String parentUri = ROOT_URI + accessionNo + "/scans/" + scanIdentifier + SNAPSHOTS_RESOURCE;
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
			_message = "Snapshot folder not generated- error";
			System.out.println(_message);
			e.printStackTrace();
			_log.error(_message,e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * @param accessionId
	 * @param scanIdentifier
	 * @return
	 * @throws Exception
	 */
	private boolean imageUpload(String accessionId, String scanIdentifier) throws Exception {
		String parentUri = ROOT_URI + accessionId + "/scans/" + scanIdentifier;
		try {
			final UserI userI = XDAT.getUserDetails();
			ResourceData resourceData = _catalogService.getResourceDataFromUri(parentUri + "/resources/DICOM/files",
					true);
			XnatResourcecatalog xnatResourcecatalog = resourceData.getCatalogResource();
			File dicomFile = new File(xnatResourcecatalog.getUri());
			String dicompath = dicomFile.getParent();
			String tempImagePath = new File(dicompath).getParent();
			SnapshotDicomConventImage dcm = new SnapshotDicomConventImage(dicompath);
			XnatImagescandataBean scan = new XnatImagescandataBean();
			scan.setId(scanIdentifier);
			File file = dcm.createThumbnail(dcm.getImagePlus(), scan, accessionId, tempImagePath);
			System.out.println("file1 :: " + file);
			String[] tags = { "" };
			_catalogService.insertResources(userI, parentUri + SNAPSHOTS_RESOURCE, file, SNAPSHOTS, null, "GIF",
					"ORIGINAL", tags);
			System.out.println("delete temp file ::" + file.getAbsolutePath());
			dcm.deleteFile(file);
		} catch (ClientException e) {
			_message = String.format(e.getMessage(), parentUri);
			_log.error(_message);
			e.printStackTrace();
			return false ;
		} catch (Exception e) {
			_log.error(" Snapshots image error:: " + e.getMessage());
			e.printStackTrace();
			return false ;
		}
        return true;
	}
	
	private final String SNAPSHOTS_RESOURCE = "/resources/SNAPSHOTS/files";
	private final String ROOT_URI = "/archive/experiments/";
	private final String SNAPSHOTS = "SNAPSHOTS";
	private String _message = "";
	private final CatalogService _catalogService;
	private static final Logger _log = LoggerFactory.getLogger(SnapshotGenerationServiceImpl.class);
}
