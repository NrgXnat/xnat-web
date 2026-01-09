package org.nrg.xnat.ingest.device.parsers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ParameterFile {
    private Map<String, Object> headers = new LinkedHashMap<>();
    private Map<String, Object> parameters = new LinkedHashMap<>();
    private List<String> comments = new ArrayList<>();

    public void addHeader(String key, Object value) { headers.put(key, value); }
    public void addParameter(String key, Object value) { parameters.put(key, value); }
    public void addComment(String comment) { comments.add(comment); }

    @Override
    public String toString() {
        return "Headers: " + headers.size() + "\n" + "Parameters: " +
                parameters.size() + "\n" + "Comments: " + comments.size() + "\n";
    }
}