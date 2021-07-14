package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatMrsessiondata;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatMrsessiondataSerializer extends AbstractBaseElementSerializer<XnatMrsessiondata> {
	private static final long serialVersionUID = 7260985692118037295L;

	public XnatMrsessiondataSerializer() {
        super(XnatMrsessiondata.class);
    }

    @Override
    protected void serializeImpl(final XnatMrsessiondata xnatMrsessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonBlankField(generator, "coil", xnatMrsessiondata.getCoil());
		writeNonBlankField(generator, "fieldStrength", xnatMrsessiondata.getFieldstrength());
		writeNonBlankField(generator, "marker", xnatMrsessiondata.getMarker());
		writeNonBlankField(generator, "stabilization", xnatMrsessiondata.getStabilization());
		//generator.writeFieldName("imageSessions");
		provider.findValueSerializer(XnatImagesessiondata.class).serialize(xnatMrsessiondata, generator, provider);
    }
}
