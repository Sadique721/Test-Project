package com.savbill.cpm.modules.CafFollowUp.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.model.common.Customers;
import com.savbill.cpm.model.common.StaffUser;
import com.savbill.cpm.modules.CafFollowUp.domain.CafFollowUp;
import com.savbill.cpm.modules.CafFollowUp.model.CafFollowUpDTO;
import com.savbill.cpm.service.common.CustomersService;
import com.savbill.cpm.service.common.StaffUserService;

@Mapper
public abstract class CafFollowUpMapper implements IBaseMapper<CafFollowUpDTO, CafFollowUp> {

	String MODULE = " [CafFollowUpMapper] ";

	@Autowired
	private StaffUserService staffUserService;
	
	@Autowired
	private CustomersService customersService;

	@Mapping(source = "customers", target = "customersId")
	@Mapping(source = "customers", target = "customersName")
	@Mapping(source = "staffUser", target = "staffUserId")
	@Mapping(source = "staffUser", target = "staffUserName")
	@Override
	public abstract CafFollowUpDTO domainToDTO(CafFollowUp data, @Context CycleAvoidingMappingContext context);

	@Mapping(source = "customersId", target = "customers")
	@Mapping(source = "staffUserId", target = "staffUser")
	@Override
	public abstract CafFollowUp dtoToDomain(CafFollowUpDTO dtoData, @Context CycleAvoidingMappingContext context);

	Integer fromStaffUserToStaffUserId(StaffUser entity) {
		return entity == null ? null : entity.getId();
	}
	
	String fromStaffUserToStaffUserName(StaffUser entity) {
		return entity == null ? null : entity.getFirstname()+" "+entity.getLastname();
	}

	StaffUser fromStaffUserIdToStaffUser(Integer entityId) {
		if (entityId == null) {
			return null;
		}
		StaffUser entity;
		try {
			entity = staffUserService.get(entityId);
		} catch (Exception e) {
			e.printStackTrace();
			entity = null;
		}
		return entity;
	}

	Integer fromCustomersToCustomersId(Customers entity) {
		return entity == null ? null : entity.getId();
	}
	
	String fromCustomersToCustomersName(Customers entity) {
		return entity == null ? null : entity.getFirstname()+" "+entity.getLastname();
	}

	Customers fromCustomersIdToCustomers(Integer entityId) {
		if (entityId == null) {
			return null;
		}
		Customers entity;
		try {
			entity = customersService.get(entityId);
		} catch (Exception e) {
			e.printStackTrace();
			entity = null;
		}
		return entity;
	}
	
	@AfterMapping
	void afterMapping(@MappingTarget CafFollowUpDTO cafFollowUpDTO, CafFollowUp cafFollowUp) {
		try {
			if (cafFollowUp != null) {
				if (cafFollowUp.getCustomers() != null) {
					cafFollowUpDTO.setCustomersId(cafFollowUp.getCustomers().getId());
					cafFollowUpDTO.setCustomersName(
							cafFollowUp.getCustomers().getFirstname() + " " + cafFollowUp.getCustomers().getLastname());
				}
				if (cafFollowUp.getStaffUser() != null) {
					cafFollowUpDTO.setStaffUserId(cafFollowUp.getStaffUser().getId());
					cafFollowUpDTO.setStaffUserName(
							cafFollowUp.getStaffUser().getFirstname() + " " + cafFollowUp.getStaffUser().getLastname());
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}
}
