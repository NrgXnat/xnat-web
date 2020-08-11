package org.nrg.xapi.rest.dicomweb;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.pgelinas.jackson.javax.json.stream.JacksonGenerator;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.json.JSONWriter;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.dcm4che3.DicomObjectChe3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: there is this serializer and JsonDicomObjectISerializer. There should be one and it shouldn't leak dcm4che3.
public class JsonDicomObjectISerializer extends StdSerializer<DicomObjectI> {
    private static final Logger _log = LoggerFactory.getLogger(DicomWebApi.class);

    JsonDicomObjectISerializer() {
        super(DicomObjectI.class);
    }

    @Override
    public void serialize(DicomObjectI value, JsonGenerator gen, SerializerProvider provider) {

        JacksonGenerator jgen = new JacksonGenerator( gen);
//        javax.json.stream.JsonGenerator sgen = (javax.json.stream.JsonGenerator) gen;
        JSONWriter jsonWriter = new JSONWriter( jgen);
        if( value instanceof DicomObjectChe3) {
            DicomObjectChe3 do3 = (DicomObjectChe3) value;
            Attributes attributes = do3.getAttributes();
            jsonWriter.write( attributes);
            jgen.flush();
        }
        else {
            _log.error("Can't json serialize DicomObjectI of class {}", value.getClass().toString());
        }
    }

}
