package com.savbill.taskmanagement.core.modules.TicketFollowupDetail.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.Customers.Service.CustomersService;
import com.savbill.taskmanagement.core.modules.Customers.domain.Customers;
import com.savbill.taskmanagement.core.modules.Pincode.mapper.PincodeMapper;
import com.savbill.taskmanagement.core.modules.TicketFollowupDetail.domain.TicketFollowupDetail;
import com.savbill.taskmanagement.core.modules.TicketFollowupDetail.model.TicketFollowupDetailDTO;
import com.savbill.taskmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.taskmanagement.core.modules.staffuser.service.StaffUserService;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseDTO;
import com.savbill.taskmanagement.core.modules.tasks.service.CaseService;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(uses = PincodeMapper.class)
public abstract class TicketFollowupDetailMapper implements IBaseMapper<TicketFollowupDetailDTO, TicketFollowupDetail> {
   
	String MODULE = " [class] ";
   
	@Autowired
	StaffUserService staffUserService;
   
    @Autowired
	CustomersService customersService;
   
    @Autowired
    CaseService caseService;

    @AfterMapping
    void afterMapping(@MappingTarget TicketFollowupDetailDTO ticketFollowupDetailDTO, TicketFollowupDetail ticketFollowupDetail) {
        try {
            if (ticketFollowupDetail != null) {
                if (ticketFollowupDetail.getCaseId() != null) {
                    CaseDTO caseDb = caseService.getEntityById(ticketFollowupDetail.getCaseId());
                    if(caseDb != null) {
                    	ticketFollowupDetailDTO.setCaseTitle(caseDb.getCaseTitle());
                        ticketFollowupDetailDTO.setCaseId(caseDb.getCaseId());
                	}
                }
                if (ticketFollowupDetail.getCustId() != null) {
                	Customers customers = customersService.get(ticketFollowupDetail.getCustId());
                	if(customers != null) {
                		ticketFollowupDetailDTO.setCustomersName(customers.getFullName());
                    	ticketFollowupDetailDTO.setCustId(customers.getId());
                	}
                }
                if (ticketFollowupDetail.getStaffId() != null) {
                	StaffUser staffUser = staffUserService.get(ticketFollowupDetail.getStaffId());
                	if(staffUser != null) {
                    	ticketFollowupDetailDTO.setStaffUserName(staffUser.getFullName());
                    	ticketFollowupDetailDTO.setStaffId(staffUser.getId());
                	}
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
