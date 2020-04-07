package org.nrg.xnat.snapshot.services.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.nrg.xnat.snapshot.services.SnapshotGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author pradeep.d
 *
 */
@Service
@Data
@Slf4j

public class SnapshotGenerationServiceImpl implements SnapshotGenerationService {

	@Override
	public String generateSnapshot(String projectID, String sessionIdentifier, String scanIdentifier, String gridView) {
		String path = null;
		try {
			_log.debug("SnapshotServiceImpl  generateSnapshot method start ");
			boolean verifySnapshot = verifySnapshots(projectID, sessionIdentifier, scanIdentifier);
			boolean verifyImage = verifyImage(projectID, sessionIdentifier, scanIdentifier);
			if (verifySnapshot && verifyImage) {
				path = getImagePath(projectID, sessionIdentifier, scanIdentifier);
				System.out.println("generateSnapshot()-verifySnapshot Snapshot path ::  " + path);
			} else if (verifySnapshot && !verifyImage) {
				// shopshots folder is exist ,but not exist image
			} else {
				// shopshots folder as well image does not exist
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
		// implementation
		System.out.println("Snapshots verifySnapshots()");
		_log.debug("Snapshots verifySnapshots() ");
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

	private static final Logger _log = LoggerFactory.getLogger(SnapshotGenerationServiceImpl.class);
}
