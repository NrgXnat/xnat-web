package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatRegionresource;

import java.io.IOException;

@Slf4j
public abstract class XnatImagesessiondataDeserializer<T extends XnatImagesessiondata> extends XnatSubjectassessordataDeserializer<T> {
    private static final long serialVersionUID = -7857539328931182208L;

    protected XnatImagesessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            // TODO: Same for the "dcm*" attributes as for "scanner" below: "dicom": { "accessionNumber": "...", etc. }.
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
                // TODO: I'd like things like this–scanner, scannerManufacturer, and scannerModel–to be serialized something like:
                //  "scanner": { "name": <instance.getScanner()>, "manufacturer": <instance.getScanner_manufacturer()>, "model": <instance.getScanner_model()> }
                //  and deserialized from that structure. That means there would be only one case statement for "scanner", which would get the next object and then
                //  the "name", "manufacturer", and "model" properties from that. See notes in XnatPetsessiondataDeserializer for more info.
                instance.setScanner(parser.getText());
                break;
            case "scannerManufacturer":
                instance.setScanner_manufacturer(parser.getText());
                break;
            case "scannerModel":
                instance.setScanner_model(parser.getText());
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
                    // TODO: This may not work: getRegions_region() returns a List<>, so I think this needs to be read as a list of XnatRegionresource objects.
                    instance.setRegions_region(parser.readValueAs(XnatRegionresource.class));
                } catch (Exception e) {
                    // TODO: Use log.error("message", e) here, not e.printStackTrace()
                    e.printStackTrace();
                }
                break;
            case "assessors":
                try {
                    // TODO: This may not work: getAssessors_assessor() returns a List<>, so I think this needs to be read as a list of XnatImageassessordata objects.
                    instance.setAssessors_assessor(parser.readValueAs(XnatImageassessordata.class));
                } catch (Exception e) {
                    // TODO: Use log.error("message", e) here, not e.printStackTrace()
                    e.printStackTrace();
                }
                break;
            case "scans":
                try {
                    // TODO: This may not work: getScans_scan() returns a List<>, so I think this needs to be read as a list of XnatImagescandata objects.
                    instance.setScans_scan(parser.readValueAs(XnatImagescandata.class));
                } catch (Exception e) {
                    // TODO: Use log.error("message", e) here, not e.printStackTrace()
                    e.printStackTrace();
                }
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

