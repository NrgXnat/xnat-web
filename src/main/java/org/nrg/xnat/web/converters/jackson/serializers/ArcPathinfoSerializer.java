package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPathinfo;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ArcPathinfoSerializer<T extends ArcPathinfo> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8618290491746282463L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcPathinfoSerializer() {
        this((Class<T>) ArcPathinfo.class);
    }

    protected ArcPathinfoSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T arcPathinfo, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullNumber(generator, "arcPathinfoId", arcPathinfo.getArcPathinfoId());
        writeNonBlankField(generator, "archivePath", arcPathinfo.getArchivepath());
        writeNonBlankField(generator, "prearchivePath", arcPathinfo.getPrearchivepath());
        writeNonBlankField(generator, "cachePath", arcPathinfo.getCachepath());
        writeNonBlankField(generator, "buildPath", arcPathinfo.getBuildpath());
        writeNonBlankField(generator, "ftpPath", arcPathinfo.getFtppath());
        writeNonBlankField(generator, "pipelinePath", arcPathinfo.getPipelinepath());
    }
}
