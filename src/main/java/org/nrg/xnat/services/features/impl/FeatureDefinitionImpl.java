package org.nrg.xnat.services.features.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.model.FeatureDefinitionDto;
import org.nrg.xapi.model.FeatureDefinitionUserGroupDto;
import org.nrg.xapi.model.FeatureUserGroupDto;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.UserGroupI;
import org.nrg.xdat.security.helpers.FeatureDefinitionI;
import org.nrg.xdat.security.helpers.Features;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.services.RoleRepositoryServiceI.RoleDefinitionI;
import org.nrg.xft.XFTTable;
import org.nrg.xft.exception.DBPoolException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.features.FeatureDefinitionService;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FeatureDefinitionImpl<T> implements FeatureDefinitionService<T>{

	@Override
	public T findAll(UserI user, String [] tags, String type, String group) throws SQLException, DBPoolException {
		if (log.isDebugEnabled()) {
			log.debug("Entering the featureDefinitionRestlet represent() method");
		}
		if(tags ==null && StringUtils.isBlank(type) && StringUtils.isBlank(group)){
			return getFeatureDefinitions();
		}else if(tags != null) {
			return getFeatureUserGroups(user, tags);
		}else if(type !=null){   
			return getFeatureUserGroupsWithRole(user);
		}
		return null;
	}
	
	@Override
	public void create(UserI user) {
		
	}

	@SuppressWarnings("unchecked")
	private T getFeatureUserGroupsWithRole(UserI user) throws SQLException, DBPoolException {
		Collection<String> siteWideEnabled=Features.getEnabledFeatures();
    	Collection<String> siteWideBanned=Features.getBannedFeatures();
    	
		List<FeatureDefinitionUserGroupDto> groups = new ArrayList<>();
		XFTTable t= XFTTable.Execute("SELECT DISTINCT displayname FROM xdat_usergroup WHERE tag IS NOT NULL;", null, null);
        List<Object> groupTypes=t.convertColumnToArrayList("displayname");
        
        FeatureDefinitionUserGroupDto userGroup = getFeatureUserGroup(groupTypes);
        groups.add(userGroup);
        
        FeatureDefinitionUserGroupDto userGroupWithRole = getFeatureUserGroupWithRole(groupTypes);
        groups.add(userGroupWithRole);
        
        userGroup.setId(Features.SITE_WIDE);
        userGroup.setBanned(siteWideBanned);
        userGroup.setOnByDefault(siteWideEnabled);
		return (T) groups;
	}

	private FeatureDefinitionUserGroupDto getFeatureUserGroupWithRole(List<Object> groupTypes) {
		FeatureDefinitionUserGroupDto userGroup = new FeatureDefinitionUserGroupDto();
		 for(RoleDefinitionI role : Roles.getRoles()){
	        	List<FeatureUserGroupDto> group = new ArrayList<>();
	        	String key="role:"+role.getKey();
	        	group.add(FeatureUserGroupDto.builder()
	        			.id(key)
	        			.display("Role: " + role.getName())
	        			.isRole(true)
	        			.features(Features.getEnabledFeaturesForGroupType(key))
	        			.blocked(Features.getBannedFeaturesForGroupType(key)).build());
	        	userGroup.setGroups(group);
	        }
		return userGroup;
	}

	private FeatureDefinitionUserGroupDto getFeatureUserGroup(List<Object> groupTypes) {
		 FeatureDefinitionUserGroupDto userGroup = new FeatureDefinitionUserGroupDto();
		for(Object gType:groupTypes){
        	List<FeatureUserGroupDto> group = new ArrayList<>();
        	group.add(FeatureUserGroupDto.builder()
        			.id(gType.toString())
        			.display(gType.toString())
        			.features(Features.getEnabledFeaturesForGroupType((String)gType))
        			.blocked(Features.getBannedFeaturesForGroupType((String)gType)).build());
        	userGroup.setGroups(group);
		}
		return userGroup;
	}

	@SuppressWarnings("unchecked")
	private T getFeatureUserGroups(UserI user, String[] tags) {
		Collection<String> siteWideEnabled=Features.getEnabledFeatures();
    	Collection<String> siteWideBanned=Features.getBannedFeatures();
    	List<FeatureDefinitionUserGroupDto> groups = new ArrayList<>();
    	for(String tag:tags){
    		XnatProjectdata proj=XnatProjectdata.getProjectByIDorAlias(tag, user, false);
    		List<FeatureUserGroupDto> group = new ArrayList<>();
    		for(List gID:proj.getGroupIDs()){
    			UserGroupI ug=Groups.getGroup((String)gID.get(0));
    			group.add(FeatureUserGroupDto.builder()
    					.id(ug.getId())
    					.display(gID.get(1))
    					.features(Features.getFeaturesForGroup(ug))
    					.blocked(Features.getBlockedFeaturesForGroup(ug))
    					.inherited_banned(Features.getBannedFeaturesForGroupType((String)gID.get(1)))
    					.inherited_features(Features.getEnabledFeaturesForGroupType((String)gID.get(1))).build());
    		}
    		groups.add(FeatureDefinitionUserGroupDto.builder()
    				.id(proj.getId())
    				.banned(siteWideBanned)
    				.onByDefault(siteWideEnabled)
    				.groups(group).build());
    	}
		return (T) groups;
	}

	//Replace with FeatureDefination
	@SuppressWarnings("unchecked")
	private T getFeatureDefinitions() {
		List<FeatureDefinitionDto> response = new ArrayList<>();
		for(FeatureDefinitionI feature: Features.getAllFeatures()){
			response.add(FeatureDefinitionDto.builder()
					.key(feature.getKey())
					.name(feature.getName())
					.enabled(feature.isOnByDefault())
					.banned(feature.isBanned()).build());
        }
		return (T)response;
	}

}
