package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.framework.utilities.Reflection;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatMrsessiondata;

import java.io.IOException;
import java.util.List;

@Slf4j
public class XnatExperimentdataDeserializer extends AbstractBaseElementDeserializer<XnatExperimentdata> {
    public XnatExperimentdataDeserializer() {
        super(XnatExperimentdata.class);
    }

    @Override
    protected XnatExperimentdata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        XnatExperimentdata experiment = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	experiment.setId(parser.getText());
                    break;
                case "label":
                	experiment.setLabel(parser.getText());
                    break;
                case "project":
                    experiment.setProject(parser.getText());
                    break;
                case "note":
                    experiment.setNote(parser.getText());
                    break;
                case "protocol":
                    experiment.setProtocol(parser.getText());
                    break;
                case "original":
                    experiment.setOriginal(parser.getText());
                    break;
                case "date":
                    experiment.setDate(parseDate(parser.getText()));
                    break;
                case "delay":
                    experiment.setDelay(parser.getIntValue());
                    break;
                case "version":
                    experiment.setVersion(parser.getIntValue());
                    break;
                case "acquisitionSite":
                    experiment.setAcquisitionSite(parser.getText());
                    break;
                case "visit":
                    experiment.setVisit(parser.getText());
                    break;
                case "visitId":
                    experiment.setVisitId(parser.getText());
                    break;
                case "xsiType":
                	System.out.println("Outer XSI TYPE experiment ===>");
						if (parser.getText().equals("xnat:mrSessionData")) {
							experiment = new XnatMrsessiondata();
							System.out.println("Inner XSI TYPE experiment ===>");
							XnatMrsessiondata experiment1 = (XnatMrsessiondata)experiment;
								System.out.println("Inner XSI TYPE experiment ==>"+ experiment1.getXSIType());
						}
				break;
			}
        }
        return experiment;
    }
}
