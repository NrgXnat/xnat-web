package org.nrg.xnat.services.resources.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectRepresentationUtil {
	final JSONObject o;

	public JSONObjectRepresentationUtil(final JSONObject o) {
		this.o = o;
	}
	
	public String getText() throws IOException {
	       String result = null;

	       if (isAvailable()) {
	           final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	           write(baos);
	           result = baos.toString();
	       }

	       return result;
	   }

	private boolean isAvailable() {
		return true;
	}

	public void write(OutputStream os) throws IOException {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
			o.write(writer);
			writer.flush();
		} catch (JSONException e) {
			new IOException(e);
		}
	}
}
