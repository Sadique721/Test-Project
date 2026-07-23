package com.savbill.notification.services.impl;

import java.util.List;

import javax.jdo.annotations.Transactional;

import org.apache.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.savbill.notification.helper.UserDto;
import com.savbill.notification.entity.Role;
import com.savbill.notification.entity.RoleScreens;
import com.savbill.notification.entity.Staff;
import com.savbill.notification.repository.RoleRepository;
import com.savbill.notification.repository.StaffRepository;
import com.savbill.notification.services.StaffService;
import com.savbill.notification.utils.Dao;

@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    StaffRepository staffRepository;
    
    @Autowired
    RoleRepository roleRepository;

    
    @Autowired
    Dao dao;

	final Logger log = Logger.getLogger(StaffServiceImpl.class);
    
    // This method is used TO ================================================================== authenticate user.
    @Override
    public UserDetails loadUserByUsername(String userName)
    {

    	UserDetails userDetails=null;

    	UserDto userDto =dao.getUserDetailsFromUsername(userName); /** we get username,password,roleid From this **/
              if(userDto!=null)
              {
            	 String role=dao.getRoleFromRoleId(userDto.getRoleid());
            	 if(role!=null)
            	 {
            		 userDto.setRole(role);
            		 userDto.setAuthorities(role);
            	 }

            	 userDetails= new org.springframework. security.core.userdetails.User(userDto.getUsername(),userDto.getPassword(),userDto.getAuthorities());
              }
    	return userDetails;
    }
    
    @Transactional
    @Override
    public void saveRoleAndStaff(String queueStaffSuccess, Staff staff, List<RoleScreens> roleScreenList,
	    boolean isUpdate, boolean isDelete, String oldName) {
		MDC.get("traceId");
		MDC.get("spanId");
	if (isDelete) {
	    Staff staffToDelete = staffRepository.findByUserNameAndMvnoId(staff.getUserName(), staff.getMvnoId());
	    if (staffToDelete != null) {
		staffRepository.delete(staffToDelete);
		log.info("Staff deleted successfully, deleted staff : "+ staff.getUserName());
	    }

	} else {
	    if (!isUpdate) {
		if (staff != null) {
		    Staff staffToSave = new Staff();
		    staffToSave.setUserName(staff.getUserName());
		    staffToSave.setPassword(staff.getPassword());
		    staffToSave.setMvnoId(staff.getMvnoId() != null ? staff.getMvnoId() : null);
		    staffToSave.setRole(getRoles(staff.getRole()));
			staffRepository.save(staffToSave);
			log.info("Staff updated successfully, updated staff : "+ staff.getUserName());

		}
	    } else {
		Staff staffToSave = staffRepository.findByUserNameAndMvnoId(oldName, staff.getMvnoId());
		if (staffToSave != null) {
		    staffToSave.setUserName(staff.getUserName());
		    staffToSave.setPassword(staff.getPassword());
		    staffToSave.setMvnoId(staff.getMvnoId() != null ? staff.getMvnoId() : null);
		    staffToSave.setRole(getRoles(staff.getRole()));
		    staffRepository.save(staffToSave);
			log.info("Staff created successfully, new staff : "+ staff.getUserName());
		}
	    }
	}
    }
    
    private Role getRoles(Role role)
    {
		Role roleVo = null;
		List<Role> roleList = roleRepository.findAll();
		if (!roleList.isEmpty())
		{
			for (Role roleToCheck : roleList) 
			{
			    if (roleToCheck.getName().equalsIgnoreCase(role.getName())
				    && roleToCheck.getMvnoId() == role.getMvnoId()) 
			    {
			    	roleVo = roleToCheck;
			    }
			}
		}
		return roleVo;
    }
}
