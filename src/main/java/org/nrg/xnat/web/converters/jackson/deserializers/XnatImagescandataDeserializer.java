package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.om.XnatImagescandata;

import java.io.IOException;

@Slf4j
public abstract class XnatImagescandataDeserializer<T extends XnatImagescandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8411039314137244911L;

    protected XnatImagescandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "xnatImagescandataId":
                instance.setXnatImagescandataId(parser.getIntValue());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "project":
                instance.setProject(parser.getText());
                break;
            case "imageSessionId":
                instance.setImageSessionId(parser.getText());
                break;
            case "type":
                instance.setType(parser.getText());
                break;
            case "note":
                instance.setNote(parser.getText());
                break;
            case "quality":
                instance.setQuality(parser.getText());
                break;
            case "modality":
                instance.setModality(parser.getText());
                break;
            case "seriesDescription":
                final String description = parser.getText();
                if (StringUtils.isNotBlank(description)) {
                    instance.setSeriesDescription(description);
                }
                break;
            case "condition":
                instance.setCondition(parser.getText());
                break;
            case "documentation":
                instance.setDocumentation(parser.getText());
                break;
            case "scanner":
                instance.setScanner(parser.getText());
                break;
            case "scannerManufacturer":
                instance.setScanner_manufacturer(parser.getText());
                break;
            case "scannerModel":
                instance.setScanner_model(parser.getText());
                break;
            case "scannerSoftwareVersion":
                instance.setScanner_softwareversion(parser.getText());
                break;
            case "seriesClass":
                instance.setSeriesClass(parser.getText());
                break;
            case "operator":
                instance.setOperator(parser.getText());
                break;
            case "frames":
                instance.setFrames(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
