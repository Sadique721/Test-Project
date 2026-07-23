package com.savbill.cpm.modules.TicketFollowupDetail.mapper;
//
//import org.mapstruct.AfterMapping;
//import org.mapstruct.Mapper;
//import org.mapstruct.MappingTarget;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.savbill.cpm.core.mapper.IBaseMapper;
//import com.savbill.cpm.core.utillity.log.ApplicationLogger;
//import com.savbill.cpm.model.common.Customers;
//import com.savbill.cpm.model.common.StaffUser;
//import com.savbill.cpm.modules.Pincode.mapper.PincodeMapper;
//import com.savbill.cpm.modules.TicketFollowupDetail.domain.TicketFollowupDetail;
//import com.savbill.cpm.modules.TicketFollowupDetail.model.TicketFollowupDetailDTO;
//import com.savbill.cpm.modules.tickets.model.CaseDTO;
//import com.savbill.cpm.modules.tickets.service.CaseService;
//import com.savbill.cpm.service.common.CustomersService;
//import com.savbill.cpm.service.common.StaffUserService;
//
//@Mapper(uses = PincodeMapper.class)
public abstract class TicketFollowupDetailMapper  {
//
//	String MODULE = " [class] ";
//
//	@Autowired
//    StaffUserService staffUserService;
//
//    @Autowired
//    CustomersService customersService;
//
//    @Autowired
//    CaseService caseService;
//
//    @AfterMapping
//    void afterMapping(@MappingTarget TicketFollowupDetailDTO ticketFollowupDetailDTO, TicketFollowupDetail ticketFollowupDetail) {
//        try {
//            if (ticketFollowupDetail != null) {
//                if (ticketFollowupDetail.getCaseId() != null) {
//                    CaseDTO caseDb = caseService.getEntityById(ticketFollowupDetail.getCaseId());
//                    if(caseDb != null) {
//                    	ticketFollowupDetailDTO.setCaseTitle(caseDb.getCaseTitle());
//                        ticketFollowupDetailDTO.setCaseId(caseDb.getCaseId());
//                	}
//                }
//                if (ticketFollowupDetail.getCustId() != null) {
//                	Customers customers = customersService.get(ticketFollowupDetail.getCustId());
//                	if(customers != null) {
//                		ticketFollowupDetailDTO.setCustomersName(customers.getFullName());
//                    	ticketFollowupDetailDTO.setCustId(customers.getId());
//                	}
//                }
//                if (ticketFollowupDetail.getStaffId() != null) {
//                	StaffUser staffUser = staffUserService.get(ticketFollowupDetail.getStaffId());
//                	if(staffUser != null) {
//                    	ticketFollowupDetailDTO.setStaffUserName(staffUser.getFullName());
//                    	ticketFollowupDetailDTO.setStaffId(staffUser.getId());
//                	}
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
//            ex.printStackTrace();
//        }
//    }
}
