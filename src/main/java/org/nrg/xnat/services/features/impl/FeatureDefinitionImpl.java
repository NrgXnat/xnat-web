package org.nrg.xnat.services.features.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.model.features.FeatureDefinitionModel;
import org.nrg.xapi.model.features.FeatureDefinitionUserGroupModel;
import org.nrg.xapi.model.features.FeatureUserGroupModel;
import org.nrg.xdat.model.XnatProjectdataI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.base.BaseXnatProjectdata;
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
    	
		List<FeatureDefinitionUserGroupModel> groups = new ArrayList<>();
		XFTTable                              t      = XFTTable.Execute("SELECT DISTINCT displayname FROM xdat_usergroup WHERE tag IS NOT NULL;", null, null);
        List<Object> groupTypes=t.convertColumnToArrayList("displayname");
        
        FeatureDefinitionUserGroupModel userGroup = getFeatureUserGroup(groupTypes);
        groups.add(userGroup);
        
        FeatureDefinitionUserGroupModel userGroupWithRole = getFeatureUserGroupWithRole(groupTypes);
        groups.add(userGroupWithRole);
        
        userGroup.setId(Features.SITE_WIDE);
        userGroup.setBanned(siteWideBanned);
        userGroup.setOnByDefault(siteWideEnabled);
		return (T) groups;
	}

	private FeatureDefinitionUserGroupModel getFeatureUserGroupWithRole(List<Object> groupTypes) {
		FeatureDefinitionUserGroupModel userGroup = new FeatureDefinitionUserGroupModel();
		 for(RoleDefinitionI role : Roles.getRoles()){
	        	List<FeatureUserGroupModel> group = new ArrayList<>();
	        	String                      key   ="role:"+role.getKey();
	        	group.add(FeatureUserGroupModel.builder()
											   .id(key)
											   .display("Role: " + role.getName())
											   .isRole(true)
											   .features(Features.getEnabledFeaturesForGroupType(key))
											   .blocked(Features.getBannedFeaturesForGroupType(key)).build());
	        	userGroup.setGroups(group);
	        }
		return userGroup;
	}

	private FeatureDefinitionUserGroupModel getFeatureUserGroup(List<Object> groupTypes) {
		 FeatureDefinitionUserGroupModel userGroup = new FeatureDefinitionUserGroupModel();
		for(Object gType:groupTypes){
        	List<FeatureUserGroupModel> group = new ArrayList<>();
        	group.add(FeatureUserGroupModel.builder()
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
    	Collection<String>                    siteWideBanned =Features.getBannedFeatures();
    	List<FeatureDefinitionUserGroupModel> groups         = new ArrayList<>();
    	for(String tag:tags){
    		XnatProjectdataI            proj  =XnatProjectdata.getProjectByIDorAlias(tag, user, false);
    		List<FeatureUserGroupModel> group = new ArrayList<>();
    		for(List gID:((BaseXnatProjectdata)proj).getGroupIDs()){
    			UserGroupI ug=Groups.getGroup((String)gID.get(0));
    			group.add(FeatureUserGroupModel.builder()
											   .id(ug.getId())
											   .display(gID.get(1))
											   .features(Features.getFeaturesForGroup(ug))
											   .blocked(Features.getBlockedFeaturesForGroup(ug))
											   .inheritedBanned(Features.getBannedFeaturesForGroupType((String)gID.get(1)))
											   .inheritedFeatures(Features.getEnabledFeaturesForGroupType((String)gID.get(1))).build());
    		}
    		groups.add(FeatureDefinitionUserGroupModel.builder()
													  .id(proj.getId())
													  .banned(siteWideBanned)
													  .onByDefault(siteWideEnabled)
													  .groups(group).build());
    	}
		return (T) groups;
	}

	//Replace with FeatureDefinition
	@SuppressWarnings("unchecked")
	private T getFeatureDefinitions() {
		List<FeatureDefinitionModel> response = new ArrayList<>();
		for(FeatureDefinitionI feature: Features.getAllFeatures()){
			response.add(FeatureDefinitionModel.builder()
                                               .key(feature.getKey())
                                               .name(feature.getName())
                                               .enabled(feature.isOnByDefault())
                                               .banned(feature.isBanned()).build());
        }
		return (T)response;
	}

}
