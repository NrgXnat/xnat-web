package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatRegionresource;

import java.io.IOException;

@Slf4j
public abstract class XnatImagesessiondataDeserializer<T extends XnatImagesessiondata> extends XnatSubjectassessordataDeserializer<T> {
    private static final long serialVersionUID = 8714542973804658983L;

    protected XnatImagesessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "dcmAccessionNumber":
                instance.setDcmaccessionnumber(parser.getText());
                break;
            case "dcmPatientBirthDate":
                instance.setDcmpatientbirthdate(parser.getText());
                break;
            case "dcmPatientId":
                instance.setDcmpatientid(parser.getText());
                break;
            case "dcmPatientName":
                instance.setDcmpatientname(parser.getText());
                break;
            case "dcmPatientWeight":
                instance.setDcmpatientweight(Double.parseDouble(parser.getText()));
                break;
            case "modality":
                instance.setModality(parser.getText());
                break;
            case "operator":
                instance.setOperator(parser.getText());
                break;
            case "prearchivePath":
                instance.setPrearchivepath(parser.getText());
                break;
            case "scanner":
                instance.setScanner(parser.getText());
                break;
            case "studyId":
                instance.setStudyId(parser.getText());
                break;
            case "sessionType":
                instance.setSessionType(parser.getText());
                break;
            case "uid":
                instance.setUid(parser.getText());
                break;
            case "regions":
                try {
                    instance.setRegions_region(parser.readValueAs(XnatRegionresource.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
