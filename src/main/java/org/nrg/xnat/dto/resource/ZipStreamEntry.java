package org.nrg.xnat.dto.resource;

import static lombok.AccessLevel.PROTECTED;

import java.io.InputStream;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(PROTECTED)
@Accessors(prefix = "_")
public class ZipStreamEntry extends ZipEntry{
	 ZipStreamEntry(final String path, final InputStream input) {
         super(path);
         _input = input;
     }

     public InputStream getInputStream() {
         return _input;
     }

     private final InputStream _input;
}
