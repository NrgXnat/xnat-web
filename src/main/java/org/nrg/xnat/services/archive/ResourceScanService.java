package org.nrg.xnat.services.archive;

import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xapi.exceptions.ConflictedStateException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.entities.ResourceScanRequest;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ResourceScanService extends BaseHibernateService<ResourceScanRequest> {
    /**
     * Gets all queued resource scan requests for the specified project.
     *
     * @param requester The user requesting the scan requests.
     * @param projectId The project to be scanned.
     *
     * @return A list of resource scan requests for the specified project.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request resource scans.
     * @throws NotFoundException               When the specified project doesn't exist.
     */
    List<ResourceScanRequest> getByProject(final UserI requester, final String projectId) throws InsufficientPrivilegesException, NotFoundException;

    /**
     * Gets all resource scan requests for the specified project with the specified status. If the status is null, the
     * returned requests are not restricted by status.
     *
     * @param requester The user requesting the scan requests.
     * @param projectId The project to be scanned.
     * @param status    The status to match.
     *
     * @return A list of resource scan requests matching the requests status for the specified project.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request resource scans.
     * @throws NotFoundException               When the specified project doesn't exist.
     */
    List<ResourceScanRequest> getByProject(final UserI requester, final String projectId, final ResourceScanRequest.Status status) throws InsufficientPrivilegesException, NotFoundException;

    /**
     * Returns the {@link ResourceScanRequest resource scan request} for the specified resource ID.
     *
     * @param requester  The user requesting the scan request.
     * @param resourceId The ID of the resource to scan.
     *
     * @return The scan request for the specified resource.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request resource scans.
     * @throws NotFoundException               When the specified resource doesn't exist.
     */
    ResourceScanRequest getByResourceId(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException;

    /**
     * Creates a {@link ResourceScanRequest resource scan request} for the resource with the specified ID. The requesting
     * user <i>must</i> have sufficient permissions to delete data in the project containing the resource.
     *
     * @param requester  The user requesting the project scan.
     * @param resourceId The ID of the resource to scan.
     *
     * @return The scan request created for the resource.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request resource scans.
     * @throws NotFoundException               When the specified resource doesn't exist.
     */
    ResourceScanRequest createResourceScanRequest(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException;

    /**
     * Locates all DICOM resources in the specified project and creates a {@link ResourceScanRequest resource scan
     * request} for each one. The requesting user <i>must</i> have sufficient permissions to delete data in the specified
     * project.
     *
     * @param requester The user requesting the project scan.
     * @param projectId The project to be scanned.
     *
     * @return A list of the resource scan requests created for the project.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request resource scans.
     * @throws NotFoundException               When the specified project doesn't exist.
     */
    List<ResourceScanRequest> createResourceScanRequests(final UserI requester, final @Nullable Date startDate, final String projectId) throws InsufficientPrivilegesException, NotFoundException;

    /**
     * Runs scan on the catalog and files for resource scan requests with status "Created" in the specified project. The
     * requesting user <i>must</i> have sufficient permissions to delete data in the project containing the specified
     * resource.
     *
     * @param requester The user requesting the scan.
     * @param projectId The ID of the project to scan.
     *
     * @return Report for the scan results.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request a resource scan.
     * @throws NotFoundException               When the specified resource doesn't exist.
     */
    List<ResourceScanReport> scanResources(final UserI requester, final String projectId) throws InsufficientPrivilegesException, NotFoundException;

    /**
     * Runs a scan on the catalog and files in the specified resource. The requesting user <i>must</i> have sufficient
     * permissions to delete data in the project containing the specified resource.
     *
     * @param requester  The user requesting the scan.
     * @param resourceId The ID of the resource to scan.
     *
     * @return A report on the scan results.
     *
     * @throws ConflictedStateException        When no resource scan exists for the resource or the scan request status is not queued.
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request a resource scan.
     * @throws NotFoundException               When the specified resource doesn't exist.
     */
    ResourceScanReport scanResource(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException, ConflictedStateException;

    /**
     * Queues request to use scan reports for resources in the specified project to mitigate mismatched file names and
     * duplicate DICOM instances. The requesting user <i>must</i> have sufficient permissions to delete data in the
     * project containing the specified resource.
     *
     * @param requester The user requesting the scan repairs.
     * @param projectId The project containing resource to be repaired.
     *
     * @return A list of workflow IDs for the queued requests.
     */
    List<Integer> queueRepairResourcesForProject(final UserI requester, final String projectId) throws InsufficientPrivilegesException, NotFoundException;

    List<Integer> queueRepairResourcesForProject(final UserI requester, final String projectId, final String reason, final String comment) throws InitializationException, InsufficientPrivilegesException, NotFoundException;

    /**
     * Queues a request to use the scan report for the specified resource to mitigate mismatched file names and duplicate
     * DICOM instances. The requesting user <i>must</i> have sufficient permissions to delete data in the project
     * containing the specified resource.
     *
     * @param requester  The user requesting the scan repair.
     * @param resourceId The ID of the resource to repair.
     *
     * @return The workflow ID for the queued repair request.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request resource repairs.
     * @throws NotFoundException               When the specified resource doesn't exist.
     */
    Integer queueRepairResource(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException, InitializationException;

    /**
     * Queues a request to use the scan report for the specified resource to mitigate mismatched file names and duplicate
     * DICOM instances. The requesting user <i>must</i> have sufficient permissions to delete data in the project
     * containing the specified resource.
     *
     * @param requester  The user requesting the scan repair.
     * @param resourceId The ID of the resource to repair.
     *
     * @return The workflow ID for the queued repair request.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request resource repairs.
     * @throws NotFoundException               When the specified resource doesn't exist.
     */
    Integer queueRepairResource(final UserI requester, final int resourceId, final String reason, final String comment) throws InsufficientPrivilegesException, NotFoundException, InitializationException;

    /**
     * Uses the scan report for the specified resource to mitigate mismatched file names and duplicate DICOM instances.
     * The requesting user <i>must</i> have sufficient permissions to delete data in the project containing the specified
     * resource.
     *
     * @param requester  The user requesting the scan repair.
     * @param resourceId The ID of the resource to repair.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request resource repairs.
     * @throws NotFoundException               When the specified resource doesn't exist.
     */
    void repairResource(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException, InitializationException;

    /**
     * Gets the current status of the repair operation for the specified resource ID.
     *
     * @param requester  The user requesting the repair status.
     * @param resourceId The resource ID to check.
     *
     * @return The status of the repair request.
     */
    String getRepairStatus(final UserI requester, final int resourceId) throws NotFoundException, InsufficientPrivilegesException;

    /**
     * Gets the current status of one or more repair operations for the specified resource IDs.
     *
     * @param requester   The user requesting the repair status.
     * @param resourceIds The resource IDs to check.
     *
     * @return The resource ID with the status of each repair request.
     */
    Map<Integer, String> getRepairStatuses(final UserI requester, final List<Integer> resourceIds);
}
