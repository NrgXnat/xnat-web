package org.nrg.xnat.dto.resource;

import java.io.File;

public class ZipFileEntry extends ZipEntry {
	ZipFileEntry(final String path, final File file) {
		super(path);
		_file = file;
	}

	public File getFile() {
		return _file;
	}

	private final File _file;
}
