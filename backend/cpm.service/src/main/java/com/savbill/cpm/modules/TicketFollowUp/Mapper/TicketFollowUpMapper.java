package com.savbill.cpm.modules.TicketFollowUp.Mapper;
//
//import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
//import com.savbill.cpm.core.mapper.IBaseMapper;
//import com.savbill.cpm.core.utillity.log.ApplicationLogger;
//import com.savbill.cpm.model.common.StaffUser;
//import com.savbill.cpm.modules.TicketFollowUp.Domain.TicketFollowUp;
//import com.savbill.cpm.modules.TicketFollowUp.Model.TicketFollowUpDTO;
//import com.savbill.cpm.modules.tickets.domain.Case;
//import com.savbill.cpm.modules.tickets.repository.CaseRepository;
//import com.savbill.cpm.modules.tickets.service.CaseService;
//import com.savbill.cpm.service.common.StaffUserService;
//import org.mapstruct.*;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Mapper
public abstract class TicketFollowUpMapper {
//
//    String MODULE = " [TicketFollowUpMapper] ";
//
//    @Autowired
//    private StaffUserService staffUserService;
//
//    @Autowired
//    private CaseService caseService;
//
//    @Autowired
//    private CaseRepository caseRepository;
//
//
//    @Mapping(source = "ticket", target = "caseId")
//    @Mapping(source = "ticket", target = "caseNumber")
//    @Mapping(source = "staffUser", target = "staffUserId")
//    @Mapping(source = "staffUser", target = "staffUserName")
//    @Override
//    public abstract TicketFollowUpDTO domainToDTO(TicketFollowUp ticketFollowUp, @Context CycleAvoidingMappingContext context);
//
//    @Mapping(source = "caseId", target = "ticket")
//    @Mapping(source = "staffUserId", target = "staffUser")
//    @Override
//    public abstract TicketFollowUp dtoToDomain(TicketFollowUpDTO dtoData, @Context CycleAvoidingMappingContext context);
//
////    @Override
////    public List<TicketFollowUpDTO> domainToDTO(List<TicketFollowUp> ticketFollowUps, CycleAvoidingMappingContext context) {
////        return null;
////    }
////
////    @Override
////    public TicketFollowUp updateDTOToDomain(TicketFollowUpDTO ticketFollowUpDTO, TicketFollowUp ticketFollowUp, CycleAvoidingMappingContext context) {
////        return null;
////    }
//
//
//    Integer fromStaffUserToStaffUserId(StaffUser entity) {
//        return entity == null ? null : entity.getId();
//    }
//
//    String fromStaffUserToStaffUserName(StaffUser entity) {
//        return entity == null ? null : entity.getFirstname() + " " + entity.getLastname();
//    }
//
//    StaffUser fromStaffUserIdToStaffUser(Integer entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        StaffUser entity;
//        try {
//            entity = staffUserService.get(entityId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }
//
//    Integer fromTicketToCaseId(Case entity) {
//        return Math.toIntExact(entity == null ? null : entity.getCaseId());
//    }
//
//    String fromTicketToCaseNumber(Case entity) {
//        return entity == null ? null : entity.getCaseNumber();
//    }
//
//    Case fromCaseIdToTicket(Integer entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        Case entity;
//        try {
//            entity = caseRepository.findById(Long.valueOf(entityId)).orElse(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }
//
//    @AfterMapping
//    void afterMapping(@MappingTarget TicketFollowUpDTO ticketFollowUpDTO, TicketFollowUp ticketFollowUp) {
//        try {
//            if (ticketFollowUp != null) {
//                if (ticketFollowUp.getTicket() != null) {
//                    ticketFollowUpDTO.setCaseId(Math.toIntExact(ticketFollowUp.getTicket().getCaseId()));
//                    ticketFollowUpDTO.setCaseNumber(ticketFollowUp.getTicket().getCaseNumber());
//                }
//                if (ticketFollowUp.getStaffUser() != null) {
//                    ticketFollowUpDTO.setStaffUserId(ticketFollowUp.getStaffUser().getId());
//                    ticketFollowUpDTO.setStaffUserName(ticketFollowUp.getStaffUser().getFirstname() + " " + ticketFollowUp.getStaffUser().getLastname());
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
//            ex.printStackTrace();
//        }
//    }
}
