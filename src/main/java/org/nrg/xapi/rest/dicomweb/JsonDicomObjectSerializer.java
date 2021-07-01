package org.nrg.xapi.rest.dicomweb;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.pgelinas.jackson.javax.json.stream.JacksonGenerator;
import org.nrg.xapi.model.dicomweb.DicomObject;

import java.io.IOException;

public class JsonDicomObjectSerializer extends StdSerializer<DicomObject> {

    JsonDicomObjectSerializer() {
        super(DicomObject.class);
    }

    @Override
    public void serialize(DicomObject dicomObject, JsonGenerator gen, SerializerProvider provider) throws IOException {
        JacksonGenerator jgen = new JacksonGenerator( gen);
        dicomObject.writeAsJSON( jgen);
    }

}
