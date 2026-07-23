package com.savbill.notification.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.notification.entity.Role;
import com.savbill.notification.entity.RoleScreens;
import com.savbill.notification.entity.Screens;
import com.savbill.notification.repository.RoleRepository;
import com.savbill.notification.repository.RoleScreensRepository;
import com.savbill.notification.repository.ScreenRepository;
import com.savbill.notification.services.RoleService;
import com.savbill.notification.utils.NotificationConstants;

@Service
public class RoleServiceImpl implements RoleService {

    private final Logger log = Logger.getLogger(RoleServiceImpl.class);

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
		    MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_DELETE);
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

			MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
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

			MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
		    }
		}
	    }
	} catch (Exception e) {
	    if (isDelete) {
		MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_DELETE);
		log.error("Error to delete role from radius: " + role.getName() + " " + e.getMessage());
	    }
	    if (isUpdate) {
		MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_UPDATE);
		log.error("Error to update role from radius: " + role.getName() + " " + e.getMessage());
	    } else {
		MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
		log.error("Error to create role from radius: " + role.getName() + " " + e.getMessage());
	    }
	} finally {
	    MDC.remove(NotificationConstants.TYPE);
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
}
