/*
 * web: org.nrg.xnat.restlet.representations.ZipRepresentation
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.representations;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.nrg.xft.utils.zip.TarUtils;
import org.nrg.xft.utils.zip.ZipI;
import org.nrg.xft.utils.zip.ZipUtils;
import org.nrg.xnat.restlet.resources.SecureResource;
import org.restlet.data.MediaType;
import org.restlet.resource.OutputRepresentation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("UnusedReturnValue")
@Slf4j
public class ZipRepresentation extends OutputRepresentation {
    public ZipRepresentation(final MediaType mediaType, final String token, final Integer compression) {
        this(mediaType, Collections.singletonList(token), compression);
    }

    public ZipRepresentation(final MediaType mediaType, final List<String> tokens, final Integer compression) {
        super(mediaType);
        _mediaType = mediaType;
        _compression = deriveCompression(compression);
        _tokens.addAll(tokens);
    }

    public int deriveCompression(Integer compression) {
        if (compression == null) {
            return ZipUtils.DEFAULT_COMPRESSION;
        } else {
            return compression;
        }
    }

    public void addEntry(String p, File f) {
        _entries.add(new ZipFileEntry(p, f));
    }

    public void addEntry(String p, InputStream is) {
        _entries.add(new ZipStreamEntry(p, is));
    }

    public void addFolder(final String pre, final File directory) {
        if (directory.isDirectory()) {
            final File[] files = directory.listFiles();
            if (files != null) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        addFolder(append(pre, file.getName()), file);
                    } else {
                        addEntry(append(pre, file.getName()), file);
                    }
                }
            }
        } else {
            addEntry(append(pre, directory.getName()), directory);
        }
    }

    public String append(String pre, String post) {
        return (pre.endsWith("/")) ? pre + post : pre + "/" + post;
    }

    public void addEntry(File f) {
        String p      = f.getAbsolutePath().replace('\\', '/');
        int    i      = -1;
        String _token = null;

        for (String token : _tokens) {
            _token = token;
            i = p.indexOf('/' + _token + '/');
            if (i == -1) {
                i = p.indexOf('/' + _token);

                if (i == -1) {
                    i = p.indexOf(_token + '/');
                    if (i > -1) {
                        i = (p.substring(0, i)).lastIndexOf('/') + 1;
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
            if (p.contains(":")) {
                p = p.substring(p.indexOf(":"));
                p = p.substring(p.indexOf("/"));
                p = _token + p;
            } else {
                p = _token + p;
            }

            _entries.add(new ZipFileEntry(p, f));
        } else {

            _entries.add(new ZipFileEntry(p.substring(i), f));
        }
    }

    public void addAll(List<File> fs) {
        for (File f : fs) {
            this.addEntry(f);
        }
    }

    public void addAllAtRelativeDirectory(String ins, ArrayList<File> fs) {
        ins = ins.replace('\\', '/');
        for (File f : fs) {
            String pathS = f.getAbsolutePath().replace('\\', '/');
            int    pos   = pathS.indexOf(ins);
            if (pos >= 0) {
                this.addEntry(pathS.substring(pos + ins.length() + 1), f);
            } else {
                this.addEntry(f);
            }
        }
    }

    public String getTokenName() {
        if (this._tokens.size() > 1) {
            return "various";
        } else {
            return this._tokens.get(0);
        }
    }

    public int getEntryCount() {
        return this._entries.size();
    }

    @Override
    public String getDownloadName() {
        if (this._mediaType.equals(MediaType.APPLICATION_GNU_TAR)) {
            return getTokenName() + ".tar.gz";
        } else if (this._mediaType.equals(MediaType.APPLICATION_TAR)) {
            return getTokenName() + ".tar";
        } else if (this._mediaType.equals(SecureResource.APPLICATION_XAR)) {
            return getTokenName() + ".xar";
        } else {
            return getTokenName() + ".zip";
        }
    }

    /**
     * Adds a task that should be performed asynchronously after this ZipRepresentation
     * completes a write.
     *
     * @param runnable A runnable object to be called upon completion of write.
     *
     * @return This object.
     */
    public ZipRepresentation afterWrite(final Runnable runnable) {
        _afterWrite.add(runnable);
        return this;
    }

    /**
     * After this ZipRepresentation completes a write, remove the named directory.
     *
     * @param directory The directory to be deleted.
     *
     * @return This object.
     */
    public ZipRepresentation deleteDirectoryAfterWrite(final File directory) {
        afterWrite(() -> {
            try {
                FileUtils.deleteDirectory(directory);
            } catch (IOException e) {
                log.error("unable to remove specified directory " + directory, e);
            }
        });
        return this;
    }

    @Override
    public void write(OutputStream os) throws IOException {
        try {
            final ZipI zip;
            if (this._mediaType.equals(MediaType.APPLICATION_GNU_TAR)) {
                zip = new TarUtils();
                zip.setOutputStream(os, ZipOutputStream.DEFLATED);
                setDownloadName(getTokenName() + ".tar.gz");
                setDownloadable(true);
            } else if (_mediaType.equals(MediaType.APPLICATION_TAR)) {
                zip = new TarUtils();
                zip.setOutputStream(os, ZipOutputStream.STORED);
                setDownloadName(getTokenName() + ".tar");
                setDownloadable(true);
            } else {
                zip = new ZipUtils();
                zip.setOutputStream(os, _compression);
                setDownloadName(getTokenName() + ".zip");
                setDownloadable(true);
            }

            for (final ZipEntry ze : _entries) {
                if (ze instanceof ZipFileEntry) {
                    final ZipFileEntry zfe = (ZipFileEntry) ze;
                    final File         f   = zfe.getFile();
                    if (!f.isDirectory()) {
                        zip.write(ze.getPath(), f);
                    }
                } else {
                    zip.write(ze.getPath(), ((ZipStreamEntry) ze).getInputStream());
                }
            }

            // Complete the ZIP file
            zip.close();
        } finally {
            if (!_afterWrite.isEmpty()) {
                final Executor executor = Executors.newSingleThreadExecutor();
                for (final Runnable r : _afterWrite) {
                    executor.execute(r);
                }
            }
        }
    }

    public abstract class ZipEntry {
        public String getPath() {
            return _path;
        }

        public void setPath(String path) {
            _path = path;
        }

        private String _path = null;
    }

    public class ZipFileEntry extends ZipEntry {
        public ZipFileEntry(final String path, final File file) {
            setPath(path);
            _file = file;
        }

        public File getFile() {
            return _file;
        }

        public void setFile(final File file) {
            _file = file;
        }

        private File _file;
    }


    public class ZipStreamEntry extends ZipEntry {
        public ZipStreamEntry(final String path, final InputStream inputStream) {
            setPath(path);
            _inputStream = inputStream;
        }

        public InputStream getInputStream() {
            return _inputStream;
        }

        @SuppressWarnings("unused")
        public void setInputStream(final InputStream inputStream) {
            _inputStream = inputStream;
        }

        private InputStream _inputStream;
    }

    private final List<ZipEntry> _entries    = new ArrayList<>();
    private final List<String>   _tokens     = new ArrayList<>();
    private final List<Runnable> _afterWrite = new ArrayList<>();
    private final MediaType      _mediaType;
    private final int            _compression;
}
