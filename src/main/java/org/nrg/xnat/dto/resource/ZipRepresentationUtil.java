package org.nrg.xnat.dto.resource;

import static lombok.AccessLevel.PROTECTED;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.nrg.xdat.XDAT;
import org.nrg.xft.utils.zip.TarUtils;
import org.nrg.xft.utils.zip.ZipI;
import org.nrg.xft.utils.zip.ZipUtils;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Getter(PROTECTED)
@Accessors(prefix = "_")
@Slf4j
public class ZipRepresentationUtil implements StreamingResponseBody{

	 public ZipRepresentationUtil(final MediaType mediaType, final List<String> tokens, final Integer compression) {
			_executor = ObjectUtils.defaultIfNull(XDAT.getContextService().getBeanSafely(ExecutorService.class), Executors.newSingleThreadExecutor());
	        _tokens.addAll(tokens);
	        _compression = deriveCompression(compression);
	        _mediaType = mediaType;
	    }

	 public String getDownloadName() {
	        if (_mediaType.equals(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_GNU_TAR))) {
	            return getTokenName() + ".tar.gz";
	        }
	        if (_mediaType.equals( MediaType.parseMediaType(MediaTypeUtil.APPLICATION_TAR))) {
	            return getTokenName() + ".tar";
	        }
	        if (_mediaType.equals(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_XAR))) {
	            return getTokenName() + ".xar";
	        }
	        return getTokenName() + ".zip";
	    }
	 
	 @NotNull
	    private ZipI initializeZip(final OutputStream output, final MediaType mediaType) throws IOException {
	        final ZipI zip;
	        if (mediaType.equals(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_GNU_TAR))) {
	            zip = new TarUtils();
	            zip.setOutputStream(output, ZipOutputStream.DEFLATED);
	        } else if (mediaType.equals(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_TAR))) {
	            zip = new TarUtils();
	            zip.setOutputStream(output, ZipOutputStream.STORED);
	        } else {
	            zip = new ZipUtils();
	            zip.setOutputStream(output, _compression);
	        }
	        return zip;
	    }
	 
	@Override
	public void writeTo(OutputStream output) throws IOException {
		 try (final ZipI zip = initializeZip(output, _mediaType)) {
	            for (final ZipEntry zipEntry : _entries) {
	                if (zipEntry instanceof ZipFileEntry) {
	                    final File file = ((ZipFileEntry) zipEntry).getFile();
	                    if (!file.isDirectory()) {
	                        zip.write(zipEntry.getPath(), file);
	                    }
	                } else {
	                    zip.write(zipEntry.getPath(), ((ZipStreamEntry) zipEntry).getInputStream());
	                }
	            }
	        } finally {
	            if (!_afterWrite.isEmpty()) {
	                final Executor executor = getExecutor();
	                for (final Runnable runnable : _afterWrite) {
	                    executor.execute(runnable);
	                }
	            }
	        }
	}
	
	public void addEntry(final String path, final File file) {
        _entries.add(new ZipFileEntry(path, file));
    }

    public void addEntry(final File file) {
        final String path      = file.getAbsolutePath().replace('\\', '/');
        int    i      = -1;
        String _token = null;

        for (final String token : _tokens) {
            _token = token;
            i = path.indexOf('/' + _token + '/');
            if (i == -1) {
                i = path.indexOf('/' + _token);

                if (i == -1) {
                    i = path.indexOf(_token + '/');
                    if (i > -1) {
                        i = (path.substring(0, i)).lastIndexOf('/') + 1;
                        break;
                    }
                } else {
                    i++;
                    break;
                }
            } else {
                i++;
                break;
            }
        }

        if (i == -1) {
            String derivedPath;
            if (path.contains(":")) {
                derivedPath = path.substring(path.indexOf(":"));
                derivedPath = _token + derivedPath.substring(derivedPath.indexOf("/"));
            } else {
                derivedPath = _token + path;
            }

            _entries.add(new ZipFileEntry(derivedPath, file));
        } else {
            _entries.add(new ZipFileEntry(path.substring(i), file));
        }
    }

    public void addAll(final List<File> files) {
        for (final File file : files) {
            addEntry(file);
        }
    }

    public int getEntryCount() {
        return _entries.size();
    }
    
    private int deriveCompression(final Integer compression) {
        return ObjectUtils.defaultIfNull(compression, ZipUtils.DEFAULT_COMPRESSION);
    }

    private String getTokenName() {
        return _tokens.size() > 1 ? "various" : _tokens.get(0);
    }
	
	private final List<String> _tokens = new ArrayList<>();
	private final List<ZipEntry> _entries = new ArrayList<>();
	private final List<Runnable> _afterWrite = new ArrayList<>();
	private final int _compression;
	private final ExecutorService _executor;
	private final MediaType _mediaType;
}
