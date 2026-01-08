package org.nrg.xnat.ingest.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class XnatUriComponents {
    private String projectId;
    private String subjectId;
    private String experimentId;
    private String scanId;
    private String resourceId;

    public XnatUriComponents(String projectId, String resourceId) {
        this.projectId = projectId;
        this.resourceId = resourceId;
    }

    public XnatUriComponents(String projectId, String subjectId, String resourceId) {
        this.projectId = projectId;
        this.subjectId = subjectId;
        this.resourceId = resourceId;
    }

    public XnatUriComponents(String projectId, String subjectId, String experimentId, String resourceId) {
        this.projectId = projectId;
        this.subjectId = subjectId;
        this.experimentId = experimentId;
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return String.format(
                "XnatUriComponents{projectId='%s', subjectId='%s', experimentId='%s', scanId='%s', resourceId='%s'}",
                projectId, subjectId, experimentId, scanId, resourceId
        );
    }
}