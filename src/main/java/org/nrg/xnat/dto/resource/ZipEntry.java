package org.nrg.xnat.dto.resource;

import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(PROTECTED)
@Accessors(prefix = "_")
public abstract class ZipEntry {
	
	ZipEntry(final String path) {
		_path = path;
	}

	public String getPath() {
		return _path;
	}

	private final String _path;
}
