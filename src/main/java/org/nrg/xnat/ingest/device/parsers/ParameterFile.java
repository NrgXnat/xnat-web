package org.nrg.xnat.ingest.device.parsers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParameterFile {
    private Map<String, Object> headers = new LinkedHashMap<>();
    private Map<String, Object> parameters = new LinkedHashMap<>();
    private List<String> comments = new ArrayList<>();

    public Map<String, Object> getHeaders() { return headers; }
    public Map<String, Object> getParameters() { return parameters; }
    public List<String> getComments() { return comments; }

    public void addHeader(String key, Object value) { headers.put(key, value); }
    public void addParameter(String key, Object value) { parameters.put(key, value); }
    public void addComment(String comment) { comments.add(comment); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Headers: ").append(headers.size()).append("\n");
        sb.append("Parameters: ").append(parameters.size()).append("\n");
        sb.append("Comments: ").append(comments.size()).append("\n");
        return sb.toString();
    }
}
