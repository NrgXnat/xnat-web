package org.nrg.xnat.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.nrg.xnat.services.archive.ResourceMitigationReport;
import org.nrg.xnat.services.archive.ResourceScanReport;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(schema = "xdat_search", uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "subjectId", "experimentId", "scanId"})})
@Access(AccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class ResourceScanRequest extends AbstractHibernateEntity {
    private static final long serialVersionUID = 2010289624125993378L;

    private static final String TEMPLATE_REPAIR_ID = "repair-id-%09d-%s";

    public static final RowMapper<ResourceScanRequest> ROW_MAPPER = (resultSet, index) -> ResourceScanRequest.builder()
                                                                                                             .subjectLabel(resultSet.getString("subject_label"))
                                                                                                             .experimentLabel(resultSet.getString("experiment_label"))
                                                                                                             .scanLabel(resultSet.getString("scan_label"))
                                                                                                             .scanDescription(resultSet.getString("scan_description"))
                                                                                                             .projectId(resultSet.getString("project_id"))
                                                                                                             .subjectId(resultSet.getString("subject_id"))
                                                                                                             .experimentId(resultSet.getString("experiment_id"))
                                                                                                             .xsiType(resultSet.getString("xsi_type"))
                                                                                                             .scanId(resultSet.getInt("scan_id"))
                                                                                                             .resourceId(resultSet.getInt("resource_id"))
                                                                                                             .resourceUri(resultSet.getString("resource_uri"))
                                                                                                             .build();

    public enum Status {
        Created,
        QueuedForScanning,
        Scanning,
        Divergent,
        Conforming,
        QueuedForRepair,
        Repairing
    }

    @Column(unique = true)
    private int resourceId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status rsnStatus = Status.Created;

    @NonNull
    @NotNull
    private String projectId;

    @NonNull
    @NotNull
    private String subjectId;

    @NonNull
    @NotNull
    private String experimentId;

    @NonNull
    @NotNull
    private String xsiType;

    @NotNull
    private int scanId;

    @NonNull
    @NotNull
    private String resourceUri;

    private String subjectLabel;
    private String experimentLabel;
    private String scanLabel;
    private String scanDescription;

    private int    workflowId;
    private String requester;

    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    @Column(columnDefinition = "json")
    private ResourceScanReport scanReport;

    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonType")
    @Column(columnDefinition = "json")
    private ResourceMitigationReport mitigationReport;
}
