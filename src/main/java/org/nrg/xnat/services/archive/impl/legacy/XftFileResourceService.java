package org.nrg.xnat.services.archive.impl.legacy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.archive.FileResourceService;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class XftFileResourceService extends AbstractXftServiceImpl implements FileResourceService {
    public XftFileResourceService(final NamedParameterJdbcTemplate template) {
        super(template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XnatAbstractresourceI createResource(final UserI user, final String itemId, final XnatAbstractresourceI resource) throws ResourceAlreadyExistsException, NotFoundException {
        // This check is probably inadequate, just a placeholder for now. Need to check for
        if (getArchivableItemResources(user, itemId).stream().anyMatch(found -> StringUtils.equals(found.getLabel(), resource.getLabel()))) {
            throw new ResourceAlreadyExistsException(resource.getXSIType(), resource.getXnatAbstractresourceId().toString());
        }

        // Create resource here: see CatalogResource.handlePost()
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XnatAbstractresourceI createResource(final UserI user, final String itemId, final XnatAbstractresourceI resource, final List<Resource> files) throws ResourceAlreadyExistsException, NotFoundException {
        // Use stand-alone createResource() method to create new resource.
        final XnatAbstractresourceI created = createResource(user, itemId, resource);
        // Use stand-alone addResourceFiles() method to add files to new resource.
        addResourceFiles(user, created.getXnatAbstractresourceId(), files);
        return created;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XnatAbstractresourceI createResource(final UserI user, final String itemId, final XnatAbstractresourceI resource, final Resource... files) throws ResourceAlreadyExistsException, NotFoundException {
        // Use stand-alone createResource() method to create new resource.
        final XnatAbstractresourceI created = createResource(user, itemId, resource);
        // Use stand-alone addResourceFiles() method to add files to new resource.
        addResourceFiles(user, created.getXnatAbstractresourceId(), files);
        return created;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<XnatAbstractresourceI> getResources(final UserI user, final String itemId) throws NotFoundException {
        return getArchivableItemResources(user, itemId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XnatAbstractresourceI getResource(final UserI user, final int resourceId) throws NotFoundException {
        validateResource(resourceId);
        return XnatResource.getXnatResourcesByXnatAbstractresourceId(resourceId, user, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XnatAbstractresourceI updateResource(final UserI user, final XnatAbstractresourceI resource) throws NotFoundException, DataFormatException {
        validateResource(resource.getXnatAbstractresourceId(), resource.getXSIType());
        // Update the resource and return a newly retrieved version of the updated resource.
        return XnatResource.getXnatResourcesByXnatAbstractresourceId(resource.getXnatAbstractresourceId(), user, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteResource(final UserI user, final XnatAbstractresourceI resource) {
        // Look at CatalogResource.handleDelete()
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteResource(final UserI user, final int resourceId) {
        // Look at CatalogResource.handleDelete()
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> addResourceFiles(final UserI user, final int resourceId, final List<Resource> incoming) {
        // Look at FileList.handlePost()
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> addResourceFiles(final UserI user, final int resourceId, final Resource... incoming) {
        // Look at FileList.handlePost()
        return null;
    }

    @Override
    public List<String> addResourceFiles(final UserI user, final int resourceId, final Map<Resource, String> incoming) throws NotFoundException {
        // Look at FileList.handlePost()
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public List<Resource> getResourceFiles(final UserI user, final int resourceId) {
        // See FileList.represent().
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> updateResourceFiles(final UserI user, final int resourceId, final List<Resource> incoming) {
        // See FileList.handlePost(): this is uploading files with overwrite set to true
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> updateResourceFiles(final UserI user, final int resourceId, final Resource... incoming) {
        // See FileList.handlePost(): this is uploading files with overwrite set to true
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> updateResourceFiles(final UserI user, final int resourceId, final Map<Resource, String> incoming) throws NotFoundException, DataFormatException {
        // See FileList.handlePost(): this is uploading files with overwrite set to true
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> deleteResourceFiles(final UserI user, final int resourceId, final List<String> references) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> deleteResourceFiles(final UserI user, final int resourceId, final String... references) {
        return null;
    }

    @SuppressWarnings("UnusedReturnValue")
    private Map<String, Object> validateResource(final int resourceId) throws NotFoundException {
        try {
            return validateResource(resourceId, null);
        } catch (DataFormatException e) {
            // This won't happen because DataFormatException is only thrown when the XSI type is specified.
            return Collections.emptyMap();
        }
    }

    private Map<String, Object> validateResource(final int resourceId, final String xsiType) throws NotFoundException, DataFormatException {
        final Map<String, Object> existing;
        try {
            existing = getTemplate().queryForMap(QUERY_RESOURCE_ID_EXISTS, new MapSqlParameterSource(PARAM_RESOURCE_ID, resourceId));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No resource found with ID " + resourceId);
        }
        if (StringUtils.isNotBlank(xsiType) && !StringUtils.equalsIgnoreCase((String) existing.get("xsiType"), xsiType)) {
            throw new DataFormatException("The existing resource with ID " + resourceId + " is " + existing.get("xsiType") + " but the submitted resource is " + xsiType);
        }
        return existing;
    }

    private static final String PARAM_RESOURCE_ID        = "resourceId";
    private static final String QUERY_RESOURCE_ID        = "SELECT " +
                                                           "    a.xnat_abstractresource_id, " +
                                                           "    a.label, " +
                                                           "    a.file_count, " +
                                                           "    a.file_size, " +
                                                           "    a.note, " +
                                                           "    e.element_name AS xsiType " +
                                                           "FROM " +
                                                           "    xnat_abstractresource a " +
                                                           "        LEFT JOIN xdat_meta_element e ON a.extension = e.xdat_meta_element_id " +
                                                           "WHERE " +
                                                           "    xnat_abstractresource_id = :" + PARAM_RESOURCE_ID;
    private static final String QUERY_RESOURCE_ID_EXISTS = "SELECT EXISTS(" + QUERY_RESOURCE_ID + ")";
}
