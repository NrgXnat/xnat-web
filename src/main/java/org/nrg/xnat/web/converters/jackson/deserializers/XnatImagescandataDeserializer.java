package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Objects;

import org.nrg.xdat.om.XnatImagescandata;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatImagescandataDeserializer extends AbstractBaseElementDeserializer<XnatImagescandata> {
    public XnatImagescandataDeserializer() {
        super(XnatImagescandata.class);
    }
    
    @Override
    protected XnatImagescandata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatImagescandata scans = new XnatImagescandata();
        
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "xnatImagescandataId":
                	scans.setXnatImagescandataId(parser.getIntValue());
                    break;
                case "id":
                	scans.setId(parser.getText());
                    break;
                case "project":
                	scans.setId(parser.getText());
                    break;
                case "imageSessionId":
                	scans.setImageSessionId(parser.getText());
                    break;
                case "type":
                	scans.setType(parser.getText());
                    break;
                case "note":
                	scans.setNote(parser.getText());
                    break;
                case "quality":
                	scans.setQuality(parser.getText());
                    break;
                case "modality":
                	scans.setModality(parser.getText());
                    break;
                case "seriesDescription":
                	scans.setSeriesDescription(Objects.nonNull(parser.getText()) || !parser.getText().isEmpty() ? parser.getText() : "");
                    break;
                case "condition":
                	scans.setCondition(parser.getText());
                    break;
                case "documentation":
                	scans.setDocumentation(parser.getText());
                    break;
                case "scanner":
                	scans.setScanner(parser.getText());
                    break;
                case "scannerManufacturer":
                	scans.setScanner_manufacturer(parser.getText());
                    break;
                case "scannerModel":
                	scans.setScanner_model(parser.getText());
                    break;
                case "scannerSoftwareVersion":
                	scans.setScanner_softwareversion(parser.getText());
                    break;
                case "seriesClass":
                	scans.setSeriesClass(parser.getText());
                    break;
                case "operator":
                	scans.setOperator(parser.getText());
                    break;
                case "frame":
                	scans.setFrames(parser.getIntValue());
                    break;
            }
        }
        return scans;
    }
}
