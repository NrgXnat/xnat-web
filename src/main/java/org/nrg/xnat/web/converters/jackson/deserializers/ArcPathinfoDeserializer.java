package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcPathinfo;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcPathinfoDeserializer<T extends ArcPathinfo> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1308335401269492284L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcPathinfoDeserializer() {
        this((Class<T>) ArcPathinfo.class);
    }

    protected ArcPathinfoDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcPathinfoId":
                instance.setArcPathinfoId(parser.getIntValue());
                break;
            case "archivepath":
                instance.setArchivepath(parser.getText());
                break;
            case "buildpath":
                instance.setBuildpath(parser.getText());
                break;
            case "cachepath":
                instance.setCachepath(parser.getText());
                break;
            case "ftppath":
                instance.setFtppath(parser.getText());
                break;
            case "pipelinepath":
                instance.setPipelinepath(parser.getText());
                break;
            case "prearchivepath":
                instance.setPrearchivepath(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

