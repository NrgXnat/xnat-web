package org.nrg.xnat.ingest.model.pojo;

public class XnatUriComponents {
    private String projectId;
    private String subjectId;
    private String experimentId;
    private String scanId;
    private String resourceId;

    public XnatUriComponents() {}

    public XnatUriComponents(String projectId, String subjectId, String experimentId,
                             String scanId, String resourceId) {
        this.projectId = projectId;
        this.subjectId = subjectId;
        this.experimentId = experimentId;
        this.scanId = scanId;
        this.resourceId = resourceId;
    }

    // Getters and Setters
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public String getExperimentId() { return experimentId; }
    public void setExperimentId(String experimentId) { this.experimentId = experimentId; }

    public String getScanId() { return scanId; }
    public void setScanId(String scanId) { this.scanId = scanId; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    @Override
    public String toString() {
        return String.format(
                "XnatUriComponents{projectId='%s', subjectId='%s', experimentId='%s', scanId='%s', resourceId='%s'}",
                projectId, subjectId, experimentId, scanId, resourceId
        );
    }
}