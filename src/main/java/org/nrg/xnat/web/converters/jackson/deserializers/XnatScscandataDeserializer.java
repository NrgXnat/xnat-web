package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Objects;

import org.nrg.xdat.om.XnatDemographicdata;
import org.nrg.xdat.om.XnatScscandata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.ItemI;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatScscandataDeserializer extends AbstractBaseElementDeserializer<XnatScscandata> {
    public XnatScscandataDeserializer() {
        super(XnatScscandata.class);
    }
    
    @Override
    protected XnatScscandata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatScscandata scans = new XnatScscandata();
        
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "xnatImagescandataId":
                	scans.setXnatImagescandataId(parser.getIntValue());
                    break;
                case "seriesDescription":
                	scans.setSeriesDescription(Objects.nonNull(parser.getText()) || !parser.getText().isEmpty() ? parser.getText() : "");
                    break;
                case "type":
                	scans.setType(parser.getText());
                    break;
                case "note":
                	scans.setNote(parser.getText());
                    break;
                case "id":
                	scans.setId(parser.getText());
                    break;
                case "quality":
                	scans.setQuality(parser.getText());
                    break;
            }
        }
        return scans;
    }
}
