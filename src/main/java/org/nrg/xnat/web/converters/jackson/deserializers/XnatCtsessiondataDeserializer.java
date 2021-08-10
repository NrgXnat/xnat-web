package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatCtsessiondataDeserializer<T extends XnatCtsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = -8584699356621636576L;

    @SuppressWarnings("unchecked")
    public XnatCtsessiondataDeserializer() {
        this((Class<T>) XnatCtsessiondata.class);
    }

    protected XnatCtsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            // None of the fields below is unique to CT session: all handled in superclasses
            /*
            case "id":
                instance.setId(parser.getText());
                break;
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "project":
                instance.setProject(parser.getText());
                break;
            case "note":
                instance.setNote(parser.getText());
                break;
            case "protocol":
                instance.setProtocol(parser.getText());
                break;
            case "original":
                instance.setOriginal(parser.getText());
                break;
            case "date":
                instance.setDate(parseDate(parser.getText()));
                break;
            case "delay":
                instance.setDelay(parser.getIntValue());
                break;
            case "version":
                instance.setVersion(parser.getIntValue());
                break;
            case "acquisitionSite":
                instance.setAcquisitionSite(parser.getText());
                break;
            case "visit":
                instance.setVisit(parser.getText());
                break;
            case "visitId":
                instance.setVisitId(parser.getText());
                break;
             */
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
