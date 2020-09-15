package org.nrg.xnat.services.archive;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xft.security.UserI;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

/**
 * Defines the file resource service, which supports operations on resources associated with projects, subjects, and
 * experiments. This includes managing the resource data objects themselves–abstract resources, resource catalogs, etc.–
 * as well as the files referenced by the catalogs.
 */
public interface FileResourceService {
    /**
     * Creates a new resource associated with the specified item.
     *
     * @param user     The user creating the new resource
     * @param itemId   The ID of the item with which the new resource is to be associated
     * @param resource The resource itself
     *
     * @return The newly created resource object.
     *
     * @throws ResourceAlreadyExistsException When a resource with the same ID or the same label and item already exists
     * @throws NotFoundException              When no items with the specified ID are found
     */
    XnatAbstractresourceI createResource(final UserI user, final String itemId, final XnatAbstractresourceI resource) throws ResourceAlreadyExistsException, NotFoundException;

    /**
     * Creates a new resource associated with the specified item and adds the submitted files to the resource. This call
     * combines the functionality in the {@link #createResource(UserI, String, XnatAbstractresourceI)} and the {@link
     * #addResourceFiles(UserI, int, List)} methods.
     *
     * @param user     The user creating the new resource
     * @param itemId   The ID of the item with which the new resource is to be associated
     * @param resource The resource itself
     * @param files    The files to add to the new resource
     *
     * @return The newly created resource object.
     *
     * @throws ResourceAlreadyExistsException When a resource with the same ID or the same label and item already exists
     * @throws NotFoundException              When no items with the specified ID are found
     */
    XnatAbstractresourceI createResource(final UserI user, final String itemId, final XnatAbstractresourceI resource, final List<Resource> files) throws ResourceAlreadyExistsException, NotFoundException;

    /**
     * Creates a new resource associated with the specified item and adds the submitted files to the resource. This call
     * combines the functionality in the {@link #createResource(UserI, String, XnatAbstractresourceI)} and the {@link
     * #addResourceFiles(UserI, int, Resource...)} methods.
     *
     * @param user     The user creating the new resource
     * @param itemId   The ID of the item with which the new resource is to be associated
     * @param resource The resource itself
     * @param files    The files to add to the new resource
     *
     * @return The newly created resource object.
     *
     * @throws ResourceAlreadyExistsException When a resource with the same ID or the same label and item already exists
     * @throws NotFoundException              When no items with the specified ID are found
     */
    XnatAbstractresourceI createResource(final UserI user, final String itemId, final XnatAbstractresourceI resource, final Resource... files) throws ResourceAlreadyExistsException, NotFoundException;

    /**
     * Gets the resources associated with the specified item.
     *
     * @param user   The user requesting the list of resources
     * @param itemId The ID of the item for which the user wants to retrieve a list of resources
     *
     * @return The list of resources associated with the specified item.
     *
     * @throws NotFoundException When no items with the specified ID are found
     */
    List<XnatAbstractresourceI> getResources(final UserI user, final String itemId) throws NotFoundException;

    /**
     * Retrieves the specified resource.
     *
     * @param user       The user requesting the resource
     * @param resourceId The ID of the resource the user wants to retrieve
     *
     * @return The specified resource.
     *
     * @throws NotFoundException When no resource with the specified ID is found
     */
    XnatAbstractresourceI getResource(final UserI user, final int resourceId) throws NotFoundException;

    /**
     * Updates the submitted resource.
     *
     * @param user     The user updating the resource
     * @param resource The resource itself
     *
     * @return The updated resource object.
     *
     * @throws NotFoundException   When no items with the specified ID are found
     * @throws DataFormatException When update is incompatible with the existing resource (e.g. different IDs or XSI types)
     */
    XnatAbstractresourceI updateResource(final UserI user, final XnatAbstractresourceI resource) throws NotFoundException, DataFormatException;

    /**
     * Deletes the specified resource.
     *
     * @param user     The user deleting the resource
     * @param resource The resource to be deleted
     *
     * @return Returns <b>true</b> if the resource is successfully deleted, <b>false</b> otherwise.
     *
     * @throws NotFoundException When the resource doesn't match an existing resource in the system.
     */
    boolean deleteResource(final UserI user, final XnatAbstractresourceI resource) throws NotFoundException;

    /**
     * Deletes the specified resource.
     *
     * @param user       The user deleting the resource
     * @param resourceId The ID of the resource to be deleted
     *
     * @return Returns <b>true</b> if the resource is successfully deleted, <b>false</b> otherwise.
     *
     * @throws NotFoundException When the resource ID doesn't exist in the system.
     */
    boolean deleteResource(final UserI user, final int resourceId) throws NotFoundException;

    /**
     * Adds the submitted files to the resource with the specified ID.
     *
     * @param user       The user adding the files
     * @param resourceId The ID of the resource to which the files should be added
     * @param incoming   The files to be added to the resource
     *
     * @return The URIs of the newly added files
     *
     * @throws NotFoundException When the resource ID doesn't exist in the system.
     */
    List<String> addResourceFiles(final UserI user, final int resourceId, final List<Resource> incoming) throws NotFoundException;

    /**
     * Adds the submitted files to the resource with the specified ID.
     *
     * @param user       The user adding the files
     * @param resourceId The ID of the resource to which the files should be added
     * @param incoming   The files to be added to the resource
     *
     * @return The URIs of the newly added files
     *
     * @throws NotFoundException When the resource ID doesn't exist in the system.
     */
    List<String> addResourceFiles(final UserI user, final int resourceId, final Resource... incoming) throws NotFoundException;

    /**
     * Adds the submitted files to the resource with the specified ID. This method differs from {@link #addResourceFiles(UserI, int, List)}
     * and {@link #addResourceFiles(UserI, int, Resource...)} in that the value corresponding to each resource key in
     * the incoming map is used as the destination path for the resource file (relative to the root resource folder).
     *
     * @param user       The user adding the files
     * @param resourceId The ID of the resource to which the files should be added
     * @param incoming   The files to be added to the resource
     *
     * @return The URIs of the newly added files
     *
     * @throws NotFoundException When the resource ID doesn't exist in the system.
     */
    List<String> addResourceFiles(final UserI user, final int resourceId, final Map<Resource, String> incoming) throws NotFoundException;

    /**
     * Gets the files associated with the resource with the specified ID.
     *
     * @param user       The user requesting the files
     * @param resourceId The ID of the resource for which file references should be retrieved
     *
     * @return The URIs of the files associated with the specified resource
     *
     * @throws NotFoundException When the resource ID doesn't exist in the system.
     */
    List<Resource> getResourceFiles(final UserI user, final int resourceId) throws NotFoundException;

    /**
     * Updates the submitted files on the resource with the specified ID.
     *
     * @param user       The user updating the files
     * @param resourceId The ID of the resource on which the files should be updated
     * @param incoming   The files to be updated on the resource
     *
     * @return The URIs of the newly updated files
     *
     * @throws NotFoundException   When the resource ID doesn't exist in the system.
     * @throws DataFormatException When one or more files doesn't already exist on the specified resource.
     */
    List<String> updateResourceFiles(final UserI user, final int resourceId, final List<Resource> incoming) throws NotFoundException, DataFormatException;

    /**
     * Updates the submitted files on the resource with the specified ID.
     *
     * @param user       The user updating the files
     * @param resourceId The ID of the resource on which the files should be updated
     * @param incoming   The files to be updated on the resource
     *
     * @return The URIs of the newly updated files
     *
     * @throws NotFoundException   When the resource ID doesn't exist in the system.
     * @throws DataFormatException When one or more files doesn't already exist on the specified resource.
     */
    List<String> updateResourceFiles(final UserI user, final int resourceId, final Resource... incoming) throws NotFoundException, DataFormatException;

    /**
     * Updates the submitted files on the resource with the specified ID. This method differs from {@link #updateResourceFiles(UserI, int, List)}
     * and {@link #updateResourceFiles(UserI, int, Resource...)} in that the value corresponding to each resource key in
     * the incoming map is used as the destination path for the resource file (relative to the root resource folder).
     *
     * @param user       The user updating the files
     * @param resourceId The ID of the resource on which the files should be updated
     * @param incoming   The files to be updated on the resource
     *
     * @return The URIs of the newly updated files
     *
     * @throws NotFoundException   When the resource ID doesn't exist in the system.
     * @throws DataFormatException When one or more files doesn't already exist on the specified resource.
     */
    List<String> updateResourceFiles(final UserI user, final int resourceId, final Map<Resource, String> incoming) throws NotFoundException, DataFormatException;

    /**
     * Deletes the specified files on the resource with the specified ID.
     *
     * @param user       The user deleting the files
     * @param resourceId The ID of the resource from which the files should be deleted
     * @param references The files to be deleted from the resource
     *
     * @return The URIs of the remaining resource files
     *
     * @throws NotFoundException   When the resource ID doesn't exist in the system.
     * @throws DataFormatException When one or more files to be deleted doesn't exist on the specified resource.
     */
    List<String> deleteResourceFiles(final UserI user, final int resourceId, final List<String> references) throws NotFoundException, DataFormatException;

    /**
     * Deletes the specified files on the resource with the specified ID.
     *
     * @param user       The user deleting the files
     * @param resourceId The ID of the resource from which the files should be deleted
     * @param references The files to be deleted from the resource
     *
     * @return The URIs of the remaining resource files
     *
     * @throws NotFoundException   When the resource ID doesn't exist in the system.
     * @throws DataFormatException When one or more files to be deleted doesn't exist on the specified resource.
     */
    List<String> deleteResourceFiles(final UserI user, final int resourceId, final String... references) throws NotFoundException, DataFormatException;
}
