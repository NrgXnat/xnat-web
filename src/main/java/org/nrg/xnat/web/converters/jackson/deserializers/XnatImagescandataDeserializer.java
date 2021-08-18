package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImagescandata;

import java.io.IOException;

@Slf4j
public abstract class XnatImagescandataDeserializer<T extends XnatImagescandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8766094712475530153L;

    protected XnatImagescandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "bodypartexamined":
                instance.setBodypartexamined(parser.getText());
                break;
            case "condition":
                instance.setCondition(parser.getText());
                break;
            case "documentation":
                instance.setDocumentation(parser.getText());
                break;
            case "file":
                // TODO: Handle the "file" property here: java.util.List
                break;
            case "frames":
                instance.setFrames(parser.getIntValue());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "imageSessionId":
                instance.setImageSessionId(parser.getText());
                break;
            case "modality":
                instance.setModality(parser.getText());
                break;
            case "note":
                instance.setNote(parser.getText());
                break;
            case "operator":
                instance.setOperator(parser.getText());
                break;
            case "project":
                instance.setProject(parser.getText());
                break;
            case "protocolname":
                instance.setProtocolname(parser.getText());
                break;
            case "quality":
                instance.setQuality(parser.getText());
                break;
            case "requestedproceduredescription":
                instance.setRequestedproceduredescription(parser.getText());
                break;
            case "scanner":
                instance.setScanner(parser.getText());
                break;
            case "scanner_manufacturer":
                instance.setScanner_manufacturer(parser.getText());
                break;
            case "scanner_model":
                instance.setScanner_model(parser.getText());
                break;
            case "scanner_softwareversion":
                instance.setScanner_softwareversion(parser.getText());
                break;
            case "seriesClass":
                instance.setSeriesClass(parser.getText());
                break;
            case "seriesDescription":
                instance.setSeriesDescription(parser.getText());
                break;
            case "sharing_share":
                // TODO: Handle the "sharing_share" property here: java.util.List
                break;
            case "startDate":
                // TODO: Handle the "startDate" property here: Object
                break;
            case "starttime":
                // TODO: Handle the "starttime" property here: Object
                break;
            case "type":
                instance.setType(parser.getText());
                break;
            case "uid":
                instance.setUid(parser.getText());
                break;
            case "validation":
                // TODO: Handle the "validation" property here: org.nrg.xdat.model.XnatValidationdataI
                break;
            case "xnatImagescandataId":
                instance.setXnatImagescandataId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

