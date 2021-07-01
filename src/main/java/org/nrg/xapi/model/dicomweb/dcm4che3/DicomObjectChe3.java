package org.nrg.xapi.model.dicomweb.dcm4che3;

//import com.fasterxml.jackson.core.JsonFactory;
//import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pgelinas.jackson.javax.json.stream.JacksonGenerator;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.io.DicomEncodingOptions;
import org.dcm4che3.io.SAXWriter;
import org.dcm4che3.json.JSONWriter;
import org.nrg.xapi.model.dicomweb.DicomObject;
import org.nrg.xapi.rest.dicomweb.JsonDicomObjectSerializer;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Implements Dcm4che3 version of DicomObject.
 *
 */
@JsonSerialize(using= JsonDicomObjectSerializer.class)
public class DicomObjectChe3 implements DicomObject {

    private Attributes attributes;
    private TransformerHandler transformerHandler;

    public DicomObjectChe3() {
        attributes = new Attributes();
    }

    public int getLength() {
        attributes.calcLength( DicomEncodingOptions.DEFAULT, true);
        return attributes.getLength();
    }

    @Override
    public void writeAsXML( OutputStream os) throws IOException {
        TransformerHandler th;
        try {
            th = getTransformerHandler();

            SAXWriter writer = new SAXWriter( th);
            th.setResult( new StreamResult( os));
            writer.write( attributes);

        } catch (TransformerConfigurationException | SAXException e) {
            throw new IOException( "Error writing dicom object as XML.", e);
        }
    }

    /**
     * Create the TransformerHandler lazily.
     *
     * @return
     * @throws TransformerConfigurationException
     * @throws IOException
     */
    private TransformerHandler getTransformerHandler() throws TransformerConfigurationException, IOException {
        if( transformerHandler == null) {
            SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
            String xsltURL = null;
            if (xsltURL == null)
                transformerHandler = tf.newTransformerHandler();
            else
                transformerHandler = tf.newTransformerHandler( new StreamSource(xsltURL));
        }
        return transformerHandler;
    }

    @Override
    public void writeAsJSON( OutputStream os) throws IOException {
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(os);
        JacksonGenerator jgen = new JacksonGenerator( jsonGenerator);
        writeAsJSON( jgen);
    }

    @Override
    public void writeAsJSON(javax.json.stream.JsonGenerator jsonGenerator) throws IOException {
        JSONWriter jsonWriter = new JSONWriter( jsonGenerator);
        jsonWriter.write( attributes);
        jsonGenerator.flush();
    }

    @Override
    public String getString( int tag) {
        return attributes.getString( tag);
    }

    @Override
    public int getInt( int tag, int def) {
        return attributes.getInt( tag, def);
    }

    @Override
    public byte[] getBytes( int tag) throws IOException {
        return attributes.getBytes(tag);
    }

    @Override
    public void setString( int tag, String value) {
        attributes.setString(tag, ElementDictionary.vrOf(tag, null), value);
    }

    @Override
    public void setInt( int tag, int value) {
        attributes.setInt(tag, ElementDictionary.vrOf(tag, null), value);
    }
}
