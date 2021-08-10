package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPathinfo;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcPathinfoDeserializer<T extends ArcPathinfo> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5473780316917413125L;

    @SuppressWarnings("unchecked")
    public ArcPathinfoDeserializer() {
        this((Class<T>) ArcPathinfo.class);
    }

    public ArcPathinfoDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "archivePath":
                instance.setArchivepath(parser.getText());
                break;
            case "prearchivePath":
                instance.setPrearchivepath(parser.getText());
                break;
            case "cachePath":
                instance.setCachepath(parser.getText());
                break;
            case "buildPath":
                instance.setBuildpath(parser.getText());
                break;
            case "ftpPath":
                instance.setFtppath(parser.getText());
                break;
            case "pipelinePath":
                instance.setPipelinepath(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
