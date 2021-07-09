package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Optional;

import org.nrg.xdat.om.ArcPathinfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class ArcPathinfoDeserializer extends AbstractBaseElementDeserializer<ArcPathinfo> {
	private static final long serialVersionUID = -5473780316917413125L;

	public ArcPathinfoDeserializer() {
        super(ArcPathinfo.class);
    }

    @Override
    protected ArcPathinfo deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final ArcPathinfo arcPathinfo = Optional.ofNullable((ArcPathinfo) context.getAttribute("XnatItem")).orElseThrow(() -> new RuntimeException("ArcPathinfo can't be created on its own"));      
        
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "archivePath":
                	arcPathinfo.setArchivepath(parser.getText());
                    break;
                case "prearchivePath":
                	arcPathinfo.setPipelinepath(parser.getText());
                    break;
                case "cachePath":
                	arcPathinfo.setCachepath(parser.getText());
                    break;
                case "buildPath":
                	arcPathinfo.setBuildpath(parser.getText());
                    break;
                case "ftpPath":
                	arcPathinfo.setFtppath(parser.getText());
                    break;
                case "pipelinePath":
                	arcPathinfo.setPipelinepath(parser.getText());
                    break;
            }
        }
        return arcPathinfo;
    }

	@Override
	protected ArcPathinfo getNewInstance() throws JsonProcessingException {
		return null;
	}

}
