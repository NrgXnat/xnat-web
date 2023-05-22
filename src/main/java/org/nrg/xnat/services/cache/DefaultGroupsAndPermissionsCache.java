package org.nrg.xnat.services.cache;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nrg.framework.generics.GenericUtils;
import org.nrg.framework.jcache.JCacheHelper;
import org.nrg.xdat.display.ElementDisplay;
import org.nrg.xdat.om.XdatElementSecurity;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.ElementAccessManager;
import org.nrg.xdat.security.PermissionCriteriaI;
import org.nrg.xdat.security.SecurityManager;
import org.nrg.xdat.security.UserGroup;
import org.nrg.xdat.security.UserGroupI;
import org.nrg.xdat.security.XDATUser;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xdat.services.Initializing;
import org.nrg.xdat.services.cache.GroupsAndPermissionsCache;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xft.event.XftItemEventI;
import org.nrg.xft.event.methods.XftItemEventCriteria;
import org.nrg.xft.exception.ItemNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.cache.extractors.DataExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nrg.xdat.security.PermissionCriteria.dumpCriteriaList;
import static org.nrg.xdat.security.SecurityManager.EDIT;
import static org.nrg.xdat.security.SecurityManager.READ;
import static org.nrg.xdat.security.helpers.Groups.ALL_DATA_ACCESS_GROUP;
import static org.nrg.xdat.security.helpers.Groups.ALL_DATA_ADMIN_GROUP;
import static org.nrg.xdat.security.helpers.Groups.ALL_DATA_GROUPS;
import static org.nrg.xdat.security.helpers.Users.DEFAULT_GUEST_USERNAME;
import static org.nrg.xft.event.XftItemEventI.CREATE;
import static org.nrg.xft.event.XftItemEventI.DELETE;
import static org.nrg.xft.event.XftItemEventI.SHARE;
import static org.nrg.xft.event.XftItemEventI.UPDATE;
import static org.nrg.xnat.services.cache.extractors.DataExtractor.PARAM_PROJECT_ID;
import static org.nrg.xnat.services.cache.extractors.DataExtractor.PARAM_USERNAME;

@Service(GroupsAndPermissionsCache.CACHE_NAME)
@Slf4j
public class DefaultGroupsAndPermissionsCache extends AbstractXftItemAndCacheEventHandlerMethod implements GroupsAndPermissionsCache, Initializing, GroupsAndPermissionsCache.Provider {
    public static final String CACHE_ACCESS_MANAGERS   = "accessManagers";
    public static final String CACHE_ACTIONS           = "actions";
    public static final String CACHE_BROWSEABLES       = "browseables";
    public static final String CACHE_GROUPS            = "groups";
    public static final String CACHE_PROJECT_GROUPS    = "projectGroups";
    public static final String CACHE_PROJECT_MEMBERS   = "projectMembers";
    public static final String CACHE_READABLE_COUNTS   = "readableCounts";
    public static final String CACHE_USER_GROUPS       = "userGroups";
    public static final String CACHE_USER_LAST_UPDATED = "userLastUpdated";

    private static final List<String> USER_CACHES                          = Arrays.asList(CACHE_ACCESS_MANAGERS,
                                                                                           createCacheIdFromElements(CACHE_ACTIONS, READ),
                                                                                           createCacheIdFromElements(CACHE_ACTIONS, EDIT),
                                                                                           createCacheIdFromElements(CACHE_ACTIONS, SecurityManager.DELETE),
                                                                                           CACHE_BROWSEABLES,
                                                                                           CACHE_READABLE_COUNTS,
                                                                                           CACHE_USER_GROUPS, CACHE_USER_LAST_UPDATED);
    private static final String       COLUMN_ELEMENT_NAME                  = "element_name";
    private static final String       COLUMN_ELEMENT_COUNT                 = "count";
    private static final String       QUERY_EXPT_COUNTS_BY_TYPE            = "SELECT " + COLUMN_ELEMENT_NAME + ", COUNT(id) AS " + COLUMN_ELEMENT_COUNT + " " +
                                                                             "FROM xnat_experimentData x " +
                                                                             "         LEFT JOIN xdat_meta_element e ON x.extension = e.xdat_meta_element_id " +
                                                                             "GROUP BY " + COLUMN_ELEMENT_NAME;
    private static final String       QUERY_PROJECT_COUNTS                 = "SELECT COUNT(*) FROM xnat_projectdata";
    private static final String       QUERY_SUBJECT_COUNTS                 = "SELECT COUNT(*) FROM xnat_subjectdata";
    private static final String       QUERY_SESSION_COUNTS                 = "SELECT COUNT(*) FROM xnat_experimentdata";
    private static final String       QUERY_GET_ALL_ROLE_GROUPS            = "SELECT " +
                                                                             "  projectId AS project_id, " +
                                                                             "  id AS group_id " +
                                                                             "FROM " +
                                                                             "  xdat_usergroup " +
                                                                             "WHERE " +
                                                                             "  projectId IS NOT NULL AND " +
                                                                             "  id LIKE '%%_%s' " +
                                                                             "ORDER BY project_id, group_id";
    private static final String       QUERY_GET_ALL_MEMBER_GROUPS          = String.format(QUERY_GET_ALL_ROLE_GROUPS, "member");
    private static final String       QUERY_GET_ALL_COLLAB_GROUPS          = String.format(QUERY_GET_ALL_ROLE_GROUPS, "collaborator");
    private static final String       QUERY_ORPHANED_EXPERIMENTS           = "SELECT " +
                                                                             "    experiment_id, " +
                                                                             "    data_type, " +
                                                                             "    coalesce(xdat_meta_element_id, -1) AS xdat_meta_element_id " +
                                                                             "FROM " +
                                                                             "    data_type_views_experiments_without_data_type";
    private static final String       QUERY_CORRECT_ORPHANED_EXPERIMENTS   = "SELECT " +
                                                                             "    orphaned_experiment, " +
                                                                             "    original_data_type " +
                                                                             "FROM " +
                                                                             "    data_type_fns_correct_experiment_extension()";
    private static final String       QUERY_ACCESSIBLE_DATA_PROJECTS       = "SELECT  " +
                                                                             "  project  " +
                                                                             "FROM  " +
                                                                             "  (SELECT DISTINCT f.field_value AS project  " +
                                                                             "   FROM  " +
                                                                             "     xdat_user u  " +
                                                                             "     LEFT JOIN xdat_user_groupid map ON u.xdat_user_id = map.groups_groupid_xdat_user_xdat_user_id  " +
                                                                             "     LEFT JOIN xdat_usergroup g ON map.groupid = g.id  " +
                                                                             "     LEFT JOIN xdat_element_access a ON (g.xdat_usergroup_id = a.xdat_usergroup_xdat_usergroup_id OR u.xdat_user_id = a.xdat_user_xdat_user_id)  " +
                                                                             "     LEFT JOIN xdat_field_mapping_set s ON a.xdat_element_access_id = s.permissions_allow_set_xdat_elem_xdat_element_access_id  " +
                                                                             "     LEFT JOIN xdat_field_mapping f ON s.xdat_field_mapping_set_id = f.xdat_field_mapping_set_xdat_field_mapping_set_id  " +
                                                                             "   WHERE  " +
                                                                             "     f.field_value != '*' AND  " +
                                                                             "     a.element_name = 'xnat:subjectData' AND  " +
                                                                             "     f.%s = 1 AND  " +
                                                                             "     u.login IN ('guest', :" + PARAM_USERNAME + ")) projects";
    private static final String       QUERY_READABLE_PROJECTS              = String.format(QUERY_ACCESSIBLE_DATA_PROJECTS, "read_element");
    private static final String       QUERY_EDITABLE_PROJECTS              = String.format(QUERY_ACCESSIBLE_DATA_PROJECTS, "edit_element");
    private static final String       QUERY_OWNED_PROJECTS                 = String.format(QUERY_ACCESSIBLE_DATA_PROJECTS, "delete_element");
    private static final String       QUERY_HAS_ALL_DATA_PRIVILEGES        = "SELECT  " +
                                                                             "  EXISTS(SELECT TRUE  " +
                                                                             "         FROM  " +
                                                                             "           xdat_user u  " +
                                                                             "           LEFT JOIN xdat_user_groupid map ON u.xdat_user_id = map.groups_groupid_xdat_user_xdat_user_id  " +
                                                                             "           LEFT JOIN xdat_usergroup ug ON map.groupid = ug.id  " +
                                                                             "           LEFT JOIN xdat_element_access ea ON (ug.xdat_usergroup_id = ea.xdat_usergroup_xdat_usergroup_id OR u.xdat_user_id = ea.xdat_user_xdat_user_id)  " +
                                                                             "           LEFT JOIN xdat_field_mapping_set fms ON ea.xdat_element_access_id = fms.permissions_allow_set_xdat_elem_xdat_element_access_id  " +
                                                                             "           LEFT JOIN xdat_field_mapping fm ON fms.xdat_field_mapping_set_id = fm.xdat_field_mapping_set_xdat_field_mapping_set_id  " +
                                                                             "         WHERE  " +
                                                                             "           fm.field_value = '*' AND  " +
                                                                             "           ea.element_name = 'xnat:projectData' AND  " +
                                                                             "           fm.%s = 1 AND  " +
                                                                             "           u.login = :" + PARAM_USERNAME + ")";
    private static final String       QUERY_HAS_ALL_DATA_ACCESS            = String.format(QUERY_HAS_ALL_DATA_PRIVILEGES, "read_element");
    private static final String       QUERY_HAS_ALL_DATA_ADMIN             = String.format(QUERY_HAS_ALL_DATA_PRIVILEGES, "edit_element");
    private static final String       QUERY_ALL_DATA_ACCESS_PROJECTS       = "SELECT id AS project FROM xnat_projectdata ORDER BY project";
    private static final String       QUERY_GET_GROUP_FOR_USER_AND_PROJECT = "SELECT id " +
                                                                             "FROM xdat_usergroup xug " +
                                                                             "  LEFT JOIN xdat_user_groupid xugid ON xug.id = xugid.groupid " +
                                                                             "  LEFT JOIN xdat_user xu ON xugid.groups_groupid_xdat_user_xdat_user_id = xu.xdat_user_id " +
                                                                             "WHERE xu.login = :" + PARAM_USERNAME + " AND tag = :" + PARAM_PROJECT_ID + " " +
                                                                             "ORDER BY groupid";


    private final NamedParameterJdbcTemplate _template;
    private final Map<String, Long>          _totalCounts;
    private final Map<String, Boolean>       _userChecks;
    private final AtomicBoolean              _initialized;
    private       XDATUser                   _guest;


    @Autowired
    public DefaultGroupsAndPermissionsCache(final JCacheHelper cacheHelper, final NamedParameterJdbcTemplate template, final List<DataExtractor<?, ?>> extractors) {
        super(cacheHelper,
              extractors.stream().filter(extractor -> StringUtils.equals(extractor.getCacheGroup(), GroupsAndPermissionsCache.CACHE_NAME)).collect(Collectors.toList()),
              XftItemEventCriteria.builder().xsiType(XnatProjectdata.SCHEMA_ELEMENT_NAME).actions(CREATE, UPDATE, DELETE).build(),
              XftItemEventCriteria.builder().xsiType(XnatSubjectdata.SCHEMA_ELEMENT_NAME).xsiType(XnatExperimentdata.SCHEMA_ELEMENT_NAME).actions(CREATE, DELETE, SHARE).build(),
              XftItemEventCriteria.getXsiTypeCriteria(XdatUsergroup.SCHEMA_ELEMENT_NAME),
              XftItemEventCriteria.getXsiTypeCriteria(XdatElementSecurity.SCHEMA_ELEMENT_NAME));
        _template    = template;
        _totalCounts = new ConcurrentHashMap<>();
        _userChecks  = new ConcurrentHashMap<>();
        _initialized = new AtomicBoolean(false);
    }

    @Override
    public boolean canInitialize() {
        return !_initialized.get();
    }

    @Override
    public Future<Boolean> initialize() {
        _initialized.set(true);
        return new AsyncResult<>(true);
    }

    @Override
    public boolean isInitialized() {
        return _initialized.get();
    }

    @Override
    public Map<String, String> getInitializationStatus() {
        return Collections.emptyMap();
    }

    @Override
    public void registerListener(final Listener listener) {

    }

    @Override
    public Listener getListener() {
        return null;
    }

    @Nullable
    @Override
    public UserGroupI get(final String groupId) {
        final UserGroup group = getCacheItem(CACHE_GROUPS, groupId, UserGroup.class);
        if (group != null) {
            log.debug("Found group for ID '{}'", groupId);
        } else {
            log.info("Someone requested group with ID '{}' but I can't find that group", groupId);
        }
        return group;
    }

    @Override
    public Map<String, Long> getReadableCounts(final UserI user) {
        return getReadableCounts(user.getUsername());
    }

    @Override
    public Map<String, Long> getReadableCounts(final String username) {
        return getCacheMap(CACHE_GROUPS, username, String.class, Long.class);
    }

    @Override
    public Map<String, ElementDisplay> getBrowseableElementDisplays(final UserI user) {
        return getCacheMap(CACHE_BROWSEABLES, user.getUsername(), String.class, ElementDisplay.class);
    }

    @Override
    public List<ElementDisplay> getSearchableElementDisplays(final UserI user) {
        return getCacheList(CACHE_BROWSEABLES, user.getUsername(), ElementDisplay.class);
    }

    @Override
    public List<ElementDisplay> getActionElementDisplays(final UserI user, final String action) {
        return getActionElementDisplays(user.getUsername(), action);
    }

    @Override
    public List<ElementDisplay> getActionElementDisplays(final String username, final String action) {
        return GenericUtils.convertToTypedList(getCacheMapPartition(CACHE_ACTIONS, username, action, List.class), ElementDisplay.class);
    }

    @Override
    public List<PermissionCriteriaI> getPermissionCriteria(final UserI user, final String dataType) {
        return getPermissionCriteria(user.getUsername(), dataType);
    }

    @Override
    public List<PermissionCriteriaI> getPermissionCriteria(final String username, final String dataType) {
        try {
            PoolDBUtils.CheckSpecialSQLChars(dataType);
        } catch (Exception e) {
            throw new IllegalArgumentException("The specified data type \"" + dataType + "\" includes one or more reserved characters");
        }

        try {
            final List<PermissionCriteriaI> criteria = new ArrayList<>();

            final Map<String, ElementAccessManager> managers = getElementAccessManagers(username);
            if (managers.isEmpty()) {
                log.info("Couldn't find element access managers for user {} trying to retrieve permissions for data type {}", username, dataType);
            } else {
                final ElementAccessManager manager = managers.get(dataType);
                if (manager == null) {
                    log.info("Couldn't find element access manager for data type {} for user {} while trying to retrieve permissions ", dataType, username);
                } else {
                    criteria.addAll(manager.getCriteria());
                    if (criteria.isEmpty()) {
                        log.debug("Couldn't find any permission criteria for data type {} for user {} while trying to retrieve permissions ", dataType, username);
                    }
                }
            }

            final Map<String, UserGroupI> userGroups = getMutableGroupsForUser(username);
            log.debug("Found {} user groups for the user {}{}", userGroups.size(), username, userGroups.isEmpty() ? "" : ": " + StringUtils.join(userGroups.keySet(), ", "));
            final Set<String> groups = userGroups.keySet();
            if (CollectionUtils.containsAny(groups, ALL_DATA_GROUPS)) {
                if (groups.contains(ALL_DATA_ADMIN_GROUP)) {
                    _template.query(QUERY_GET_ALL_MEMBER_GROUPS, resultSet -> {
                        final String projectId = resultSet.getString("project_id");
                        final String groupId   = resultSet.getString("group_id");

                        // If the user is a collaborator on a project, we're going to upgrade them to member,
                        // so remove that collaborator nonsense, this is the big time.
                        userGroups.remove(projectId + "_collaborator");

                        // If the user is already a member of owner of a project, then don't bother: they already have
                        // sufficient access to the project.
                        if (!userGroups.containsKey(projectId + "_owner") && !userGroups.containsKey(projectId + "_member")) {
                            userGroups.put(groupId, get(groupId));
                        }
                    });
                } else if (userGroups.containsKey(ALL_DATA_ACCESS_GROUP)) {
                    _template.query(QUERY_GET_ALL_COLLAB_GROUPS, resultSet -> {
                        final String projectId = resultSet.getString("project_id");
                        final String groupId   = resultSet.getString("group_id");

                        // If the user has no group membership, then add as a collaborator.
                        if (!CollectionUtils.containsAny(groups, Arrays.asList(groupId, projectId + "_member", projectId + "_owner"))) {
                            userGroups.put(groupId, get(groupId));
                        }
                    });
                }
            }

            for (final UserGroupI group : userGroups.values()) {
                final List<PermissionCriteriaI> permissions = group.getPermissionsByDataType(dataType);
                if (permissions != null) {
                    if (log.isTraceEnabled()) {
                        log.trace("Searched for permission criteria for user {} on type {} in group {}: {}", username, dataType, group.getId(), dumpCriteriaList(permissions));
                    } else {
                        log.debug("Searched for permission criteria for user {} on type {} in group {}: {} permissions found", username, dataType, group.getId(), permissions.size());
                    }
                    criteria.addAll(permissions);
                } else {
                    log.warn("Tried to retrieve permissions for data type {} for user {} in group {}, but this returned null.", dataType, username, group.getId());
                }
            }

            if (!isGuest(username)) {
                try {
                    final List<PermissionCriteriaI> permissions = getPermissionCriteria(getGuest().getUsername(), dataType);
                    if (permissions != null) {
                        criteria.addAll(permissions);
                    } else {
                        log.warn("Tried to retrieve permissions for data type {} for the guest user, but this returned null.", dataType);
                    }
                } catch (Exception e) {
                    log.error("An error occurred trying to retrieve the guest user", e);
                }
            }

            if (log.isTraceEnabled()) {
                log.trace("Retrieved permission criteria for user {} on the data type {}: {}", username, dataType, dumpCriteriaList(criteria));
            } else {
                log.debug("Retrieved permission criteria for user {} on the data type {}: {} criteria found", username, dataType, criteria.size());
            }

            return ImmutableList.copyOf(criteria);
        } catch (UserNotFoundException e) {
            log.error("Couldn't find the indicated user");
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Long> getTotalCounts() {
        if (_totalCounts.isEmpty()) {
            resetTotalCounts();
        }
        return ImmutableMap.copyOf(_totalCounts);
    }

    @NotNull
    @Override
    public List<String> getProjectsForUser(final String username, final String access) {
        log.info("Getting projects with {} access for user {}", access, username);
        switch (access) {
            case READ:
                return getUserReadableProjects(username);

            case SecurityManager.EDIT:
                return getUserEditableProjects(username);

            case SecurityManager.DELETE:
                return getUserOwnedProjects(username);

            default:
                throw new IllegalArgumentException("Invalid access level '" + access + "', valid values are: '" + READ + "', '" + SecurityManager.EDIT + "', and '" + SecurityManager.DELETE + "'.");
        }
    }

    @NotNull
    @Override
    public List<UserGroupI> getGroupsForProject(final String projectId) {
        return getCacheList(CACHE_PROJECT_GROUPS, projectId, String.class).stream().map(this::get).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Map<String, UserGroupI> getGroupsForUser(final String username) throws UserNotFoundException {
        return ImmutableMap.copyOf(getMutableGroupsForUser(username));
    }

    @Override
    public void refreshGroupsForUser(final String username) throws UserNotFoundException {
        checkUser(username);
        evict(CACHE_USER_GROUPS, username);
        getCacheList(CACHE_USER_GROUPS, username, String.class);
    }

    @Override
    public UserGroupI getGroupForUserAndProject(final String username, final String projectId) throws UserNotFoundException {
        final String groupId = _template.query(QUERY_GET_GROUP_FOR_USER_AND_PROJECT, checkUser(username).addValue(PARAM_PROJECT_ID, projectId), results -> results.next() ? results.getString("id") : null);
        return StringUtils.isNotBlank(groupId) ? get(groupId) : null;
    }

    @Override
    public List<String> getUserIdsForGroup(final String groupId) {
        final UserGroupI userGroup = get(groupId);
        if (userGroup == null) {
            return Collections.emptyList();
        }
        return ImmutableList.copyOf(userGroup.getUsernames());
    }

    @Override
    public void refreshGroup(final String groupId) throws ItemNotFoundException {
        evict(CACHE_GROUPS, groupId);
        final UserGroupI group = Optional.ofNullable(get(groupId)).orElseThrow(() -> new ItemNotFoundException("No group with ID " + groupId + " found"));
        for (final String username : group.getUsernames()) {
            try {
                if (!getGroupIdsForUser(username).contains(groupId)) {
                    refreshGroupsForUser(username);
                }
            } catch (UserNotFoundException ignored) {
                //
            }
        }
    }

    @Override
    public Date getUserLastUpdateTime(final UserI user) {
        return getUserLastUpdateTime(user.getUsername());
    }

    @Override
    public Date getUserLastUpdateTime(final String username) {
        return getCacheItem(CACHE_USER_LAST_UPDATED, username, Date.class);
    }

    @Override
    public void clearUserCache(final String username) {
        USER_CACHES.forEach(cacheName -> evict(cacheName, username));
    }

    @Override
    public String getCacheName() {
        return GroupsAndPermissionsCache.CACHE_NAME;
    }

    @Override
    protected boolean handleEventImpl(final XftItemEventI event) {
        switch (event.getXsiType()) {
            case XnatProjectdata.SCHEMA_ELEMENT_NAME:
                return handleProjectEvents(event);

            case XnatSubjectdata.SCHEMA_ELEMENT_NAME:
                return handleSubjectEvents(event);

            case XdatUsergroup.SCHEMA_ELEMENT_NAME:
                return handleGroupRelatedEvents(event);

            case XdatElementSecurity.SCHEMA_ELEMENT_NAME:
                return handleElementSecurityEvents(event);

            default:
                // This is always some type of experiment.
                return handleExperimentEvents(event);
        }
    }

    private boolean isImageSession(String xsiType){
        try {
            return SchemaElement.GetElement(xsiType).instanceOf(XnatImagesessiondata.SCHEMA_ELEMENT_NAME);
        } catch (XFTInitException|ElementNotFoundException e) {
            log.error("Failed to parse passed xsiType in event handler",e);
            return false;
        }
    }

    private void incrementCount(final String xsiType) {
        if(isImageSession(xsiType)){
            _totalCounts.merge(XnatImagesessiondata.SCHEMA_ELEMENT_NAME, 1L, Long::sum);
        }else{
            _totalCounts.merge(xsiType, 1L, Long::sum);
        }
    }

    private void decrementCount(final String xsiType) {
        // decrement, but don't go below zero
        if(isImageSession(xsiType)){
            _totalCounts.merge(XnatImagesessiondata.SCHEMA_ELEMENT_NAME, 1L, (a, b) -> max(a - b,0L));
        }else{
            _totalCounts.merge(xsiType, 1L, (a, b) -> max(a - b,0L));
        }
    }

    private boolean handleProjectEvents(final XftItemEventI event) {
        final String         xsiType    = event.getXsiType();
        final String         id         = event.getId();
        final String         action     = event.getAction();
        final Map<String, ?> properties = event.getProperties();

        try {
            switch (action) {
                case CREATE:
                    log.debug("New project created with ID {}, caching new instance", id);
                    for (final String owner : getProjectOwners(id)) {
                        if (getActionElementDisplays(owner).get(SecurityManager.CREATE).stream().noneMatch(CONTAINS_MR_SESSION)) {
                            initializeActionElementDisplays(owner, true);
                        }
                    }

                    final boolean created = !initializeGroups(getGroups(xsiType, id)).isEmpty();
                    final String access = Permissions.getProjectAccess(_template, id);
                    if (StringUtils.isNotBlank(access)) {
                        switch (access) {
                            case "private":
                                break;

                            case "public":
                                if (getActionElementDisplays(DEFAULT_GUEST_USERNAME).get(SecurityManager.CREATE).stream().noneMatch(CONTAINS_MR_SESSION)) {
                                    initializeActionElementDisplays(DEFAULT_GUEST_USERNAME, true);
                                }

                            case "protected":
                                updateProjectRelatedCaches(xsiType, id, false);
                                break;
                        }
                    }
                    resetProjectCount();
                    return created;

                case UPDATE:
                    log.debug("The {} object {} was updated, caching updated instance", xsiType, id);
                    if (properties.containsKey("accessibility")) {
                        final String accessibility = (String) properties.get("accessibility");
                        switch (accessibility) {
                            case "private":
                                return updateProjectRelatedCaches(xsiType, id, true);

                            case "public":
                                if (getActionElementDisplays(DEFAULT_GUEST_USERNAME).get(SecurityManager.CREATE).stream().noneMatch(CONTAINS_MR_SESSION)) {
                                    initializeActionElementDisplays(DEFAULT_GUEST_USERNAME, true);
                                }

                            case "protected":
                                return updateProjectRelatedCaches(xsiType, id, true);

                            default:
                                log.warn("The project {}'s accessibility setting was updated to an invalid value: {}. Must be one of private, protected, or public.", id, accessibility);
                        }
                    }
                    break;

                case DELETE:
                    log.debug("The {} {} was deleted, removing related instances from cache", xsiType, id);
                    List<String> usernames = GenericUtils.convertToTypedList((List<?>) evict(PROJECT_MEMBERS_CACHE, id), String.class);
                    List<String> groups = GenericUtils.convertToTypedList((List<?>) evict(PROJECT_GROUPS_CACHE, id), String.class);
                    /*
                    CACHING: Not clear what this stuff is doing. Certainly updating caches, but not sure which ones since they're based on the cache keys.
                    getCacheIdsForUserElements().stream().filter(current -> REGEX_USER_PROJECT_ACCESS_CACHE_ID.matcher(current).matches()).forEach(accessCacheId -> {
                        final List<String> projectIds = getCachedList(accessCacheId);
                        if (projectIds != null && projectIds.contains(id)) {
                            final List<String> updated = new ArrayList<>(projectIds);
                            updated.remove(id);
                            forceCacheObject(accessCacheId, updated);
                        }
                    });
                    initializeUserReadableCounts(getCachedSet(cacheId));
                    */
                    resetGuestBrowseableElementDisplays();
                    decrementCount(xsiType);
                    return true;

                default:
                    log.warn("I was informed that the '{}' action happened to the project with ID '{}'. I don't know what to do with this action.", action, id);
                    break;
            }
        } catch (ItemNotFoundException e) {
            log.warn("While handling action {}, I couldn't find a group for type {} ID {}.", action, xsiType, id);
        }

        return false;
    }

    private boolean updateProjectRelatedCaches(final String xsiType, final String id, final boolean affectsOtherDataTypes) throws ItemNotFoundException {
        final boolean cachedRelatedGroups = !initGroups(getGroups(xsiType, id)).isEmpty();

        evict(GUEST_CACHE_ID);
        evict(GUEST_ACTION_READ);
        resetGuestBrowseableElementDisplays();
        initActionElementDisplays(DEFAULT_GUEST_USERNAME, true);

        final Set<String> readableCountCacheIds = new HashSet<>(getCacheIdsForUserReadableCounts());
        if (affectsOtherDataTypes) {
            for (final String cacheId : readableCountCacheIds) {
                evict(cacheId);
            }
        } else {
            // Update existing user element displays
            final List<String> cacheIds = getCacheIdsForActions();
            cacheIds.addAll(getCacheIdsForUserElements());
            clearAllUserProjectAccess();
            initReadableCountsForUsers(cacheIds.stream().map(DefaultGroupsAndPermissionsCache::getUsernameFromCacheId).filter(StringUtils::isNotBlank).collect(Collectors.toSet()));
        }

        return cachedRelatedGroups;
    }

    private boolean handleSubjectEvents(final XftItemEventI event) {
        log.debug("Got a subject event: {}", event);
        return true;
    }

    private boolean handleGroupRelatedEvents(final XftItemEventI event) {
        log.debug("Got a group event: {}", event);
        return true;
    }

    private boolean handleElementSecurityEvents(final XftItemEventI event) {
        log.debug("Got an element security event: {}", event);
        return true;
    }

    private boolean handleExperimentEvents(final XftItemEventI event) {
        final String action  = event.getAction();
        final String xsiType = event.getXsiType();
        log.debug("Handling experiment {} event for {} {}", XftItemEventI.ACTIONS.get(action), xsiType, event.getId());
        final String      target, origin;
        final Set<String> projectIds = new HashSet<>();
        switch (action) {
            case CREATE:
                target = _template.queryForObject(QUERY_GET_EXPERIMENT_PROJECT, new MapSqlParameterSource(PARAM_EXPERIMENT_ID, event.getId()), String.class);
                origin = null;
                projectIds.add(target);
                incrementCount(xsiType);
                break;

            case SHARE:
                target = (String) event.getProperties().get("target");
                origin = null;
                projectIds.add(target);
                break;

            case DELETE:
                target = (String) event.getProperties().get("target");
                origin = null;
                projectIds.add(target);
                decrementCount(xsiType);
                break;

            case MOVE:
                origin = (String) event.getProperties().get("origin");
                target = (String) event.getProperties().get("target");
                projectIds.add(target);
                projectIds.add(origin);
                break;

            default:
                log.warn("I was informed that the '{}' action happened to experiment '{}' with ID '{}'. I don't know what to do with this action.", action, xsiType, event.getId());
                return false;
        }

        final Map<String, ElementDisplay> displays = getGuestBrowseableElementDisplays();
        log.debug("Found {} elements for guest user: {}", displays.size(), StringUtils.join(displays.keySet(), ", "));

        // If the data type of the experiment isn't in the guest list AND the target project is public,
        // OR if the origin project is both specified and public (meaning the data type might be REMOVED
        // from the guest browseable element displays), then we update the guest browseable element displays.
        final boolean hasEventXsiType        = displays.containsKey(xsiType);
        final boolean isTargetProjectPublic  = Permissions.isProjectPublic(_template, target);
        final boolean hasOriginProject       = StringUtils.isNotBlank(origin);
        final boolean isMovedFromPublicToNon = !isTargetProjectPublic && hasOriginProject && Permissions.isProjectPublic(_template, origin);

        // We need to add the XSI type if guest doesn't already have it and the target project is public.
        final boolean needsPublicXsiTypeAdded = !hasEventXsiType && isTargetProjectPublic;

        // We need to check if the XSI type should be removed if guest has XSI type and item was moved from public to non-public.
        final boolean needsXsiTypeChecked = hasEventXsiType && isMovedFromPublicToNon;

        if (needsPublicXsiTypeAdded || needsXsiTypeChecked) {
            if (needsPublicXsiTypeAdded) {
                log.debug("Updating guest browseable element displays: guest doesn't have the event XSI type '{}' and the target project {} is public.", xsiType, target);
            } else {
                log.debug("Updating guest browseable element displays: guest has the event XSI type '{}' and item was moved from public project {} to non-public project {}.", xsiType, origin, target);
            }
            resetGuestBrowseableElementDisplays();
        } else {
            log.debug("Not updating guest browseable element displays: guest {} '{}' and {}",
                      hasEventXsiType ? "already has the event XSI type " : "doesn't have the event XSI type",
                      xsiType,
                      isTargetProjectPublic ? "target project is public" : "target project is not public");
        }

        updateProjectUsersReadableCounts(projectIds, action, event.getXsiType());

        return true;
    }

    /**
     * Checks whether the user exists. If not, this throws the {@link UserNotFoundException}. Otherwise, it returns
     * a parameter source containing the username that can be used in subsequent queries.
     *
     * @param username The user to test.
     *
     * @return A parameter source containing the username parameter.
     *
     * @throws UserNotFoundException If the user doesn't exist.
     */
    private MapSqlParameterSource checkUser(final String username) throws UserNotFoundException {
        final MapSqlParameterSource parameters = new MapSqlParameterSource(PARAM_USERNAME, username);

        // If the user isn't in the check map OR the user is in the check map but is set as not existing...
        if (!_userChecks.containsKey(username) || !_userChecks.get(username)) {
            // See if the user exists now. The non-existent user existing should be updated with the add user event,
            // but we don't have a clearly defined handler for that yet.
            _userChecks.put(username, _template.queryForObject(UserManagementServiceI.QUERY_CHECK_USER_EXISTS, parameters, Boolean.class));
        }
        if (!_userChecks.get(username)) {
            throw new UserNotFoundException(username);
        }
        return parameters;
    }

    private Map<String, ElementAccessManager> getElementAccessManagers(final String username) {
        return getCacheMap(CACHE_ACCESS_MANAGERS, username, String.class, ElementAccessManager.class);
    }

    @Nonnull
    private Map<String, UserGroupI> getMutableGroupsForUser(final String username) throws UserNotFoundException {
        return getCacheList(CACHE_USER_GROUPS, username, String.class).stream()
                                                                      .map(this::get)
                                                                      .filter(Objects::nonNull)
                                                                      .collect(Collectors.toMap(UserGroupI::getId, Function.identity()));
    }

    private List<String> getGroupIdsForUser(final String username) throws UserNotFoundException {
        final List<String> groupIds = getCacheList(CACHE_USER_GROUPS, username, String.class);
        if (log.isTraceEnabled()) {
            log.trace("Found {} groups for user '{}': {}", groupIds.size(), username, StringUtils.join(groupIds, ", "));
        } else {
            log.info("Found {} groups for user '{}'", groupIds.size(), username);
        }
        return groupIds;
    }

    /**
     * Retrieves a list of projects where the specified user has read access, including protected and public projects and projects
     * to which the user has read access due to all data access privileges.
     *
     * @param username The username to retrieve projects for.
     *
     * @return A list of projects to which the specified user has read access.
     */
    private List<String> getUserReadableProjects(final String username) {
        return getProjectsByAccessQuery(username, QUERY_READABLE_PROJECTS, false);
    }

    /**
     * Retrieves a list of projects where the specified user has edit access.
     *
     * @param username The username to retrieve projects for.
     *
     * @return A list of projects to which the specified user has edit access.
     */
    private List<String> getUserEditableProjects(final String username) {
        return getProjectsByAccessQuery(username, QUERY_EDITABLE_PROJECTS, true);
    }

    /**
     * Retrieves a list of projects where the specified user is an owner (i.e. has delete access).
     *
     * @param username The username to retrieve projects for.
     *
     * @return A list of projects to which the specified user has delete access.
     */
    private List<String> getUserOwnedProjects(final String username) {
        return getProjectsByAccessQuery(username, QUERY_OWNED_PROJECTS, true);
    }

    private List<String> getProjectsByAccessQuery(final String username, final String query, final boolean requireAdminAccess) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource("username", username);
        return hasAllDataAdmin(username) || (hasAllDataAccess(username) && !requireAdminAccess)
               ? _template.queryForList(QUERY_ALL_DATA_ACCESS_PROJECTS, parameters, String.class)
               : _template.queryForList(query, parameters, String.class);
    }

    /**
     * Cheap test to see if the user is in a group that has data read access on all projects.
     *
     * @param username The username to be checked.
     *
     * @return Returns true if the user has all-data-access, false otherwise.
     */
    private boolean hasAllDataAccess(final String username) {
        return _template.queryForObject(QUERY_HAS_ALL_DATA_ACCESS, new MapSqlParameterSource(PARAM_USERNAME, username), Boolean.class);
    }

    /**
     * Cheap test to see if the user is in a group that has data admin access on all projects.
     *
     * @param username The username to be checked.
     *
     * @return Returns true if the user has all-data-admin, false otherwise.
     */
    private boolean hasAllDataAdmin(final String username) {
        return _template.queryForObject(QUERY_HAS_ALL_DATA_ADMIN, new MapSqlParameterSource(PARAM_USERNAME, username), Boolean.class);
    }

    private void resetTotalCounts() {
        _totalCounts.clear();
        resetProjectCount();
        resetSubjectCount();
        resetImageSessionCount();
        final List<Map<String, Object>> elementCounts = _template.queryForList(QUERY_EXPT_COUNTS_BY_TYPE, EmptySqlParameterSource.INSTANCE);
        for (final Map<String, Object> elementCount : elementCounts) {
            final String elementName = (String) elementCount.get(COLUMN_ELEMENT_NAME);
            final Long   count       = (Long) elementCount.get(COLUMN_ELEMENT_COUNT);
            if (StringUtils.isBlank(elementName)) {
                final String orphaned = _template.queryForList(QUERY_ORPHANED_EXPERIMENTS, EmptySqlParameterSource.INSTANCE).stream().map(experiment -> {
                    final String experimentId = (String) experiment.get("experiment_id");
                    final String dataType     = (String) experiment.get("data_type");
                    final int    extensionId  = (Integer) experiment.get("xdat_meta_element_id");
                    return " * " + experimentId + " was a " + dataType + ", " + (extensionId >= 0 ? "should be extension ID " + extensionId : "data type doesn't appear in xdat_meta_element table");
                }).collect(Collectors.joining("\n"));
                log.warn("Found {} elements that are not associated with a valid data type:\n\n{}\n\nYou can correct some of these orphaned experiments by running the query:\n\n{}\n\nAny experiment IDs and data types returned from that query indicate data types that can not be resolved on the system (i.e. they don't exist in the primary data-type table).",
                         count, orphaned, QUERY_CORRECT_ORPHANED_EXPERIMENTS);
            } else {
                _totalCounts.put(elementName, count);
            }
        }
    }

    private void resetProjectCount() {
        _totalCounts.put(XnatProjectdata.SCHEMA_ELEMENT_NAME, _template.queryForObject(QUERY_PROJECT_COUNTS, EmptySqlParameterSource.INSTANCE, Long.class));
    }

    private void resetSubjectCount() {
        _totalCounts.put(XnatSubjectdata.SCHEMA_ELEMENT_NAME, _template.queryForObject(QUERY_SUBJECT_COUNTS, EmptySqlParameterSource.INSTANCE, Long.class));
    }

    private void resetImageSessionCount() {
        _totalCounts.put(XnatImagesessiondata.SCHEMA_ELEMENT_NAME, _template.queryForObject(QUERY_SESSION_COUNTS, EmptySqlParameterSource.INSTANCE, Long.class));
    }

    private XDATUser getGuest() {
        if (_guest == null) {
            log.debug("No guest user initialized, trying to retrieve now.");
            try {
                final UserI guest = Users.getGuest();
                if (guest instanceof XDATUser) {
                    _guest = (XDATUser) guest;
                } else {
                    _guest = new XDATUser(guest.getUsername());
                }
            } catch (UserNotFoundException e) {
                log.error("Got a user name not found exception for the guest user which is very strange.", e);
            } catch (UserInitException e) {
                log.error("Got a user init exception for the guest user which is very unfortunate.", e);
            }
        }
        return _guest;
    }

    private boolean isGuest(final String username) {
        return getGuest() != null ? StringUtils.equalsIgnoreCase(getGuest().getUsername(), username) : StringUtils.equalsIgnoreCase(DEFAULT_GUEST_USERNAME, username);
    }
}
