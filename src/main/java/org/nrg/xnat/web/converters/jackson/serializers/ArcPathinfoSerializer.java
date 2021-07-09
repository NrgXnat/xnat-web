package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.ArcPathinfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ArcPathinfoSerializer extends AbstractBaseElementSerializer<ArcPathinfo> {
	private static final long serialVersionUID = -3815153536091317883L;

	public ArcPathinfoSerializer() {
        super(ArcPathinfo.class);
    }

    @Override
    protected void serializeImpl(final ArcPathinfo  arcPathinfo, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "archivePath", arcPathinfo.getArchivepath());
        writeNonBlankField(generator, "prearchivePath", arcPathinfo.getPrearchivepath());
        writeNonBlankField(generator, "cachePath", arcPathinfo.getCachepath());
        writeNonBlankField(generator, "buildPath", arcPathinfo.getBuildpath());
        writeNonBlankField(generator, "ftpPath", arcPathinfo.getFtppath());
        writeNonBlankField(generator, "pipelinePath", arcPathinfo.getPipelinepath());
    }
}
