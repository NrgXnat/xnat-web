package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomseriesImage;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatDicomseriesImageDeserializer<T extends XnatDicomseriesImage> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8540423417029388243L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDicomseriesImageDeserializer() {
        this((Class<T>) XnatDicomseriesImage.class);
    }

    protected XnatDicomseriesImageDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "instanceNumber":
                instance.setInstanceNumber(parser.getIntValue());
                break;
            case "sopInstanceUid":
                instance.setSopInstanceUid(parser.getText());
                break;
            case "uri":
                instance.setUri(parser.getText());
                break;
            case "xnatDicomseriesImageId":
                instance.setXnatDicomseriesImageId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

