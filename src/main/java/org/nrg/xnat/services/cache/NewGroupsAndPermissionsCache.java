package org.nrg.xnat.services.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nrg.framework.jcache.JCacheHelper;
import org.nrg.xdat.display.ElementDisplay;
import org.nrg.xdat.om.XdatElementSecurity;
import org.nrg.xdat.om.XdatUsergroup;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.PermissionCriteriaI;
import org.nrg.xdat.security.UserGroupI;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xdat.services.Initializing;
import org.nrg.xdat.services.cache.GroupsAndPermissionsCache;
import org.nrg.xft.event.XftItemEventI;
import org.nrg.xft.event.methods.XftItemEventCriteria;
import org.nrg.xft.exception.ItemNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.cache.extractors.DataExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.nrg.xft.event.XftItemEventI.CREATE;
import static org.nrg.xft.event.XftItemEventI.DELETE;
import static org.nrg.xft.event.XftItemEventI.SHARE;
import static org.nrg.xft.event.XftItemEventI.UPDATE;

@Service(GroupsAndPermissionsCache.CACHE_NAME)
@Slf4j
public class NewGroupsAndPermissionsCache extends AbstractXftItemAndCacheEventHandlerMethod implements GroupsAndPermissionsCache, Initializing, GroupsAndPermissionsCache.Provider {
    @Autowired
    public NewGroupsAndPermissionsCache(final JCacheHelper cacheHelper, final List<DataExtractor<?, ?>> extractors) {
        super(cacheHelper,
              extractors.stream().filter(extractor -> StringUtils.equals(extractor.getCacheGroup(), GroupsAndPermissionsCache.CACHE_NAME)).collect(Collectors.toList()),
              XftItemEventCriteria.builder().xsiType(XnatProjectdata.SCHEMA_ELEMENT_NAME).actions(CREATE, UPDATE, DELETE).build(),
              XftItemEventCriteria.builder().xsiType(XnatSubjectdata.SCHEMA_ELEMENT_NAME).xsiType(XnatExperimentdata.SCHEMA_ELEMENT_NAME).actions(CREATE, DELETE, SHARE).build(),
              XftItemEventCriteria.getXsiTypeCriteria(XdatUsergroup.SCHEMA_ELEMENT_NAME),
              XftItemEventCriteria.getXsiTypeCriteria(XdatElementSecurity.SCHEMA_ELEMENT_NAME));
    }

    @Override
    public boolean canInitialize() {
        return false;
    }

    @Override
    public Future<Boolean> initialize() {
        return null;
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public Map<String, String> getInitializationStatus() {
        return null;
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
        return null;
    }

    @Override
    public Map<String, Long> getReadableCounts(final UserI user) {
        return null;
    }

    @Override
    public Map<String, Long> getReadableCounts(final String username) {
        return null;
    }

    @Override
    public Map<String, ElementDisplay> getBrowseableElementDisplays(final UserI user) {
        return null;
    }

    @Override
    public List<ElementDisplay> getSearchableElementDisplays(final UserI user) throws Exception {
        return null;
    }

    @Override
    public List<ElementDisplay> getActionElementDisplays(final UserI user, final String action) throws Exception {
        return null;
    }

    @Override
    public List<ElementDisplay> getActionElementDisplays(final String username, final String action) {
        return null;
    }

    @Override
    public List<PermissionCriteriaI> getPermissionCriteria(final UserI user, final String dataType) {
        return null;
    }

    @Override
    public List<PermissionCriteriaI> getPermissionCriteria(final String username, final String dataType) {
        return null;
    }

    @Override
    public Map<String, Long> getTotalCounts() {
        return null;
    }

    @NotNull
    @Override
    public List<String> getProjectsForUser(final String username, final String access) {
        return null;
    }

    @NotNull
    @Override
    public List<UserGroupI> getGroupsForProject(final String tag) {
        return null;
    }

    @NotNull
    @Override
    public Map<String, UserGroupI> getGroupsForUser(final String username) throws UserNotFoundException {
        return null;
    }

    @Override
    public void refreshGroupsForUser(final String username) throws UserNotFoundException {

    }

    @Override
    public UserGroupI getGroupForUserAndProject(final String username, final String tag) throws UserNotFoundException {
        return null;
    }

    @Override
    public List<String> getUserIdsForGroup(final String groupId) {
        return null;
    }

    @Override
    public void refreshGroup(final String groupId) throws ItemNotFoundException {

    }

    @Override
    public Date getUserLastUpdateTime(final UserI user) {
        return null;
    }

    @Override
    public Date getUserLastUpdateTime(final String username) {
        return null;
    }

    @Override
    public void clearUserCache(final String username) {

    }

    @Override
    public String getCacheName() {
        return null;
    }

    @Override
    protected boolean handleEventImpl(final XftItemEventI event) {
        return false;
    }
}
