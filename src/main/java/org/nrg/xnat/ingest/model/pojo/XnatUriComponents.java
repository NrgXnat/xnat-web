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
    private String subjectLabel;
    private String experimentLabel;
    private String scanLabel;
    private String resourceId;

    public XnatUriComponents(String projectId, String resourceId) {
        this.projectId = projectId;
        this.resourceId = resourceId;
    }

    public XnatUriComponents(String projectId, String subjectId, String resourceId) {
        this.projectId = projectId;
        this.subjectLabel = subjectId;
        this.resourceId = resourceId;
    }

    public XnatUriComponents(String projectId, String subjectId, String experimentId, String resourceId) {
        this.projectId = projectId;
        this.subjectLabel = subjectId;
        this.experimentLabel = experimentId;
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return String.format(
                "XnatUriComponents{projectId='%s', subjectId='%s', experimentId='%s', scanId='%s', resourceId='%s'}",
                projectId, subjectLabel, experimentLabel, scanLabel, resourceId
        );
    }
}