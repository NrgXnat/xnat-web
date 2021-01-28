package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xft.ItemI;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XnatMrsessiondataDeserializer extends AbstractBaseElementDeserializer<XnatMrsessiondata> {
    public XnatMrsessiondataDeserializer() {
        super(XnatMrsessiondata.class);
    }

    @Override
    protected XnatMrsessiondata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatMrsessiondata xnatMrsessiondata = new XnatMrsessiondata();
        List<XnatImagescandata> xnatImagescandatas= null;
//        try {
//        	xnatMrsessiondata.setScans_scan(xnatImagescandatas);
//        } catch (Exception e) {
//            log.error("An error occurred trying to set demographics data while deserializing an object. Sorry about that.", e);
//        }
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            if(Objects.nonNull(field)){
            	   switch (field) {
                   case "id":
                   	xnatMrsessiondata.setId(parser.getText());
                       break;
                   case "label":
                   	xnatMrsessiondata.setLabel(parser.getText());
                       break;
                   case "project":
                       xnatMrsessiondata.setProject(parser.getText());
                       break;
                   case "note":
                       xnatMrsessiondata.setNote(parser.getText());
                       break;
                   case "protocol":
                       xnatMrsessiondata.setProtocol(parser.getText());
                       break;
                   case "original":
                       xnatMrsessiondata.setOriginal(parser.getText());
                       break;
                   case "date":
                       xnatMrsessiondata.setDate(parseDate(parser.getText()));
                       break;
                   case "delay":
                       xnatMrsessiondata.setDelay(parser.getIntValue());
                       break;
                   case "version":
                       xnatMrsessiondata.setVersion(parser.getIntValue());
                       break;
                   case "acquisitionSite":
                       xnatMrsessiondata.setAcquisitionSite(parser.getText());
                       break;
                   case "visit":
                       xnatMrsessiondata.setVisit(parser.getText());
                       break;
                   case "visitId":
                       xnatMrsessiondata.setVisitId(parser.getText());
                       break;
                   case "subjectId":
                       xnatMrsessiondata.setSubjectId(parser.getText());
                       break;
//                   case "scans":
//					try {
//						xnatMrsessiondata.setScans_scan((ItemI) xnatImagescandatas);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					break;
				}
            }
         
        }
        return xnatMrsessiondata;
    }

}
