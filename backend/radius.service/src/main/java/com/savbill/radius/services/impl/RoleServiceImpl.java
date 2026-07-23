package com.savbill.radius.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.entity.QRole;
import com.savbill.radius.entity.Role;
import com.savbill.radius.entity.RoleScreens;
import com.savbill.radius.entity.Screens;
import com.savbill.radius.repository.RoleRepository;
import com.savbill.radius.repository.RoleScreensRepository;
import com.savbill.radius.repository.ScreenRepository;
import com.savbill.radius.services.RoleService;
import com.savbill.radius.utils.RadiusConstants;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ScreenRepository screenRepository;

    @Autowired
    RoleScreensRepository roleScreensRepository;

    @Transactional
    @Override
    public void saveRole(String queueStaffSuccess, Role role, List<RoleScreens> roleScreenList, boolean isUpdate,
	    boolean isDelete, String oldName) {

	Map<Long, Screens> screes = getScreenDataMap();
	try {
	    if (isDelete) {
		Role roleToDelete = roleRepository.findByNameAndMvno(role.getName(), role.getMvnoId());
		if (roleToDelete != null) {
		    List<Long> ids = new ArrayList<Long>();
		    ids.add(roleToDelete.getRoleId());
		    List<RoleScreens> roleScreenListToDelete = roleScreensRepository.getScreensByRoles(ids);
		    ArrayList<RoleScreens> oldScreens = new ArrayList<RoleScreens>();
		    if (!roleScreenListToDelete.isEmpty()) {

			for (RoleScreens roleScreens : roleScreenListToDelete) {
			    if (roleScreens.getMvnoId() == role.getMvnoId()) {
				oldScreens.add(roleScreens);
			    }
			}

			roleScreensRepository.deleteAll(oldScreens);
		    }

		    roleRepository.delete(roleToDelete);
		    MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		    log.info("role deleted succefully from wifi: " + roleToDelete.getName());
		}

	    } else {
		if (!isUpdate) {
		    if (role != null) {

			Role newRole = new Role();
			newRole.setName(role.getName());
			newRole.setMvnoId(role.getMvnoId());
			Role roleToSave = roleRepository.save(newRole);

			List<RoleScreens> roleScreenListToSave = new ArrayList<RoleScreens>();
			if (!roleScreenList.isEmpty()) {
			    for (RoleScreens roleScreen : roleScreenList) {

				if (screes.containsKey(roleScreen.getScreenId())) {
				    RoleScreens roleScr = new RoleScreens();
				    roleScr.setCreateUpdateOnly(roleScreen.isCreateUpdateOnly());
				    roleScr.setReadOnly(roleScreen.isReadOnly());
				    roleScr.setDeleteOnly(roleScreen.isDeleteOnly());
				    roleScr.setRoleId(roleToSave.getRoleId());
				    roleScr.setMvnoId(roleScreen.getMvnoId());
				    roleScr.setScreenId(roleScreen.getScreenId());
				    roleScr.setScreens(roleScreen.getScreens());
				    roleScreenListToSave.add(roleScr);
				}
			    }
			    roleScreensRepository.saveAll(roleScreenListToSave);
			}

			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
			log.info("role created succefully from wifi: " + newRole.getName());
		    }
		} else {

		    Role roleToSave = roleRepository.findByNameAndMvno(oldName, role.getMvnoId());
		    if (roleToSave != null) {
			roleToSave.setName(role.getName());
			roleRepository.save(roleToSave);

			List<RoleScreens> roleScreenListToSave = new ArrayList<RoleScreens>();
			if (!roleScreenList.isEmpty()) {

			    List<Long> ids = new ArrayList<Long>();
			    ids.add(roleToSave.getRoleId());
			    List<RoleScreens> roleScreenListToDelete = roleScreensRepository.getScreensByRoles(ids);
			    ArrayList<RoleScreens> oldScreens = new ArrayList<RoleScreens>();
			    if (!roleScreenListToDelete.isEmpty()) {

				for (RoleScreens roleScreens : roleScreenListToDelete) {
				    if (roleScreens.getMvnoId() == role.getMvnoId()) {
					oldScreens.add(roleScreens);
				    }
				}

				roleScreensRepository.deleteAll(oldScreens);
			    }

			    for (RoleScreens roleScreen : roleScreenList) {

				if (screes.containsKey(roleScreen.getScreenId())) {
				    RoleScreens roleScr = new RoleScreens();
				    roleScr.setCreateUpdateOnly(roleScreen.isCreateUpdateOnly());
				    roleScr.setReadOnly(roleScreen.isReadOnly());
				    roleScr.setDeleteOnly(roleScreen.isDeleteOnly());
				    roleScr.setRoleId(roleToSave.getRoleId());
				    roleScr.setMvnoId(roleScreen.getMvnoId());
				    roleScr.setScreenId(roleScreen.getScreenId());
				    roleScr.setScreens(roleScreen.getScreens());
				    roleScreenListToSave.add(roleScr);
				}
			    }
			    roleScreensRepository.saveAll(roleScreenListToSave);
			}

			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
			log.info("role updated succefully from wifi: " + roleToSave.getName());
		    }
		}
	    }
	} catch (Exception e) {
	    if (isDelete) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		log.error("Error to delete role from radius: " + role.getName() + " " + e.getMessage());
	    }
	    if (isUpdate) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		log.error("Error to update role from radius: " + role.getName() + " " + e.getMessage());
	    } else {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
		log.error("Error to create role from radius: " + role.getName() + " " + e.getMessage());
	    }
	} finally {
	    MDC.remove(RadiusConstants.TYPE);
	}

    }

    private Map<Long, Screens> getScreenDataMap() {

	Map<Long, Screens> dataMap = new HashMap<Long, Screens>();
	List<Screens> screensList = screenRepository.findAll();
	if (!screensList.isEmpty()) {
	    for (Screens screens : screensList) {
		dataMap.put(screens.getScreenId(), screens);
	    }
	}
	return dataMap;
    }

    @Override
    public Role findByName(String name, Long mvnoId) {
	QRole qRole = QRole.role;
	BooleanExpression boolExp = qRole.isNotNull();
	boolExp = boolExp.and(qRole.name.eq(name));
	if (mvnoId == null || mvnoId != 1)
	    boolExp = boolExp.and(qRole.mvnoId.in(mvnoId, 1));
	Optional<Role> optionalRadiusProfile = roleRepository.findOne(boolExp);
	if (!optionalRadiusProfile.isPresent()) {
	    throw new IllegalArgumentException(
		    "No record found with Role name " + name + " . Please enter valid role name.");
	}
	return optionalRadiusProfile.get();
    }

}
