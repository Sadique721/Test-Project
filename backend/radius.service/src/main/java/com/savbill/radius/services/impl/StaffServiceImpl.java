package com.savbill.radius.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.jdo.annotations.Transactional;

import com.savbill.radius.entity.*;
import com.savbill.radius.entity.Role;
import com.savbill.radius.entity.RoleScreens;
import com.savbill.radius.entity.Staff;
import com.savbill.radius.entity.StaffUserServiceAreaMapping;
import com.savbill.radius.repository.StaffUserServiceAreaMappingRepo;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.repository.RoleRepository;
import com.savbill.radius.repository.RoleScreensRepository;
import com.savbill.radius.repository.StaffRepository;
import com.savbill.radius.services.RoleScreenService;
import com.savbill.radius.services.RoleService;
import com.savbill.radius.services.StaffService;
import com.savbill.radius.utils.RadiusConstants;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class StaffServiceImpl implements StaffService {

    private static final Logger log = LoggerFactory.getLogger(StaffServiceImpl.class);

    @Autowired
    RoleService roleService;

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    RoleScreensRepository roleScreensRepository;

    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    RoleScreenService roleScreenService;

	@Autowired
	StaffUserServiceAreaMappingRepo staffUserServiceAreaMappingRepo;

    @Transactional
    @Override
    public void saveRoleAndStaff(String queueStaffSuccess, Staff staff, List<RoleScreens> roleScreenList,
                                 boolean isUpdate, boolean isDelete, String oldName) {
	try {
	    if (isDelete) {

		Staff staffToDelete = staffRepository.findByUserNameAndMvnoId(staff.getUserName(), staff.getMvnoId());
		if (staffToDelete != null) {
		    staffRepository.delete(staffToDelete);
		    MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		    log.debug("staff deleted succefully from radius: " + staffToDelete.getUserName());
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
			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
			log.debug("staff created succefully from radius: " + staffToSave.getUserName());
		    }
		} else {
		    Staff staffToSave = staffRepository.findByUserNameAndMvnoId(oldName, staff.getMvnoId());
		    if (staffToSave != null) {
			staffToSave.setUserName(staff.getUserName());
			staffToSave.setPassword(staff.getPassword());
			staffToSave.setMvnoId(staff.getMvnoId() != null ? staff.getMvnoId() : null);
			staffToSave.setRole(getRoles(staff.getRole()));
			staffRepository.save(staffToSave);
			MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
			log.debug("staff updated succefully from radius: " + staffToSave.getUserName());
		    }
		}
	    }
	} catch (Exception e) {
	    if (isDelete) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		log.error("Error to delete staff from radius: " + staff.getUserName() + " " + e.getMessage());
	    }
	    if (isUpdate) {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_UPDATE);
		log.error("Error to update staff from radius: " + staff.getUserName() + " " + e.getMessage());
	    } else {
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_CREATE);
		log.error("Error to create staff from radius: " + staff.getUserName() + " " + e.getMessage());
	    }
	} finally {
	    MDC.remove(RadiusConstants.TYPE);
	}
    }

    @Override
    public Optional<Staff> findByUserName(String userName, Long mvnoId) {
	if (!ValidateCrudTransactionData.validateStringTypeFieldValue(userName))
	    throw new IllegalArgumentException("Please enter valid staff username.");
	QStaff qStaff = QStaff.staff;
	BooleanExpression boolExp = qStaff.isNotNull();
	boolExp = boolExp.and(qStaff.userName.eq(userName));
	if (mvnoId == null || mvnoId != 1)
	    boolExp = boolExp.and(qStaff.mvnoId.eq(mvnoId));
	Optional<Staff> optionalStaff = staffRepository.findOne(boolExp);
	if (!optionalStaff.isPresent()) {
	    throw new IllegalArgumentException(
		    "No record found with Staff user name " + userName + " . Please enter valid staff user name.");
	}
	return optionalStaff;
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

	public List<Long> ListOfIds (Integer staffid){
		if(staffid!=null){
			QStaffUserServiceAreaMapping qstaffUserServiceAreaMapping=QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
			BooleanExpression booleanExpression=qstaffUserServiceAreaMapping.isNotNull();
			booleanExpression=booleanExpression.and(qstaffUserServiceAreaMapping.staffId.in(staffid));
			List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings= (List<StaffUserServiceAreaMapping>) staffUserServiceAreaMappingRepo.findAll(booleanExpression);
			List<Long> serviceAreaIds=staffUserServiceAreaMappings.stream().map(StaffUserServiceAreaMapping :: getServiceId).collect(Collectors.toList());
			return serviceAreaIds;
		}
		else{
			throw new RuntimeException("Service Area not found");
		}
 	}
}
