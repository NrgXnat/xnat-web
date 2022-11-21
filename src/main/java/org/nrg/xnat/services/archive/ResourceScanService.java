package org.nrg.xnat.services.archive;

import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.entities.ResourceScanRequest;

import java.util.List;

public interface ResourceScanService extends BaseHibernateService<ResourceScanRequest> {
    /**
     * Gets all resource scan requests for the specified project.
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
    List<ResourceScanRequest> queueScansForProject(final UserI requester, final String projectId) throws InsufficientPrivilegesException, NotFoundException;

    /**
     * Runs a scan on the catalog and files in the specified resource. The requesting user <i>must</i> have sufficient
     * permissions to delete data in the project containing the specified resource.
     *
     * @param requester  The user requesting the scan.
     * @param resourceId The ID of the resource to scan.
     *
     * @return A report on the scan results.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request a resource scan.
     * @throws NotFoundException               When the specified resource doesn't exist.
     */
    ResourceScanReport scanResource(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException;

    /**
     * Uses the scan report for the specified resource to mitigate mismatched file names and duplicate DICOM instances.
     * The requesting user <i>must</i> have sufficient permissions to delete data in the project containing the specified
     * resource.
     *
     * @param requester  The user requesting the scan repair.
     * @param resourceId The ID of the resource to repair.
     *
     * @return A report on the repair results.
     *
     * @throws InsufficientPrivilegesException When the requesting user has insufficient permissions to request resource repairs.
     * @throws NotFoundException               When the specified resource doesn't exist.
     */
    ResourceMitigationReport repairResource(final UserI requester, final int resourceId) throws InsufficientPrivilegesException, NotFoundException;
}
