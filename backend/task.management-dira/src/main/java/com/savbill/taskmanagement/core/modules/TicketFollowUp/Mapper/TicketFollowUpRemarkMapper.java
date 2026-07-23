package com.savbill.taskmanagement.core.modules.TicketFollowUp.Mapper;


import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.TicketFollowUp.Domain.TicketFollowUp;
import com.savbill.taskmanagement.core.modules.TicketFollowUp.Domain.TicketFollowUpRemark;
import com.savbill.taskmanagement.core.modules.TicketFollowUp.Model.TicketFollowUpRemarkDTO;
import com.savbill.taskmanagement.core.modules.TicketFollowUp.Service.TicketFollowUpService;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class TicketFollowUpRemarkMapper implements IBaseMapper<TicketFollowUpRemarkDTO, TicketFollowUpRemark> {

    String MODULE = " [TicketFollowUpRemarkMapper] ";


    @Autowired
    private TicketFollowUpService ticketFollowUpService;


    @Mappings({ @Mapping(source = "ticketFollowUp", target = "ticketFollowUpId"),
            @Mapping(source = "ticketFollowUp", target = "ticketFollowUpName")})
    @Override
    public abstract TicketFollowUpRemarkDTO domainToDTO(TicketFollowUpRemark ticketFollowUpRemark, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "ticketFollowUpId", target = "ticketFollowUp")
    @Override
    public abstract TicketFollowUpRemark dtoToDomain(TicketFollowUpRemarkDTO dtoData, @Context CycleAvoidingMappingContext context);

//    @Override
//    public List<TicketFollowUpRemarkDTO> domainToDTO(List<TicketFollowUpRemark> ticketFollowUpRemarks, CycleAvoidingMappingContext context) {
//        return null;
//    }
//
//    @Override
//    public TicketFollowUpRemark updateDTOToDomain(TicketFollowUpRemarkDTO ticketFollowUpRemarkDTO, TicketFollowUpRemark ticketFollowUpRemark, CycleAvoidingMappingContext context) {
//        return null;
//    }


    Long fromTicketFollowUpToTicketFollowUpId(TicketFollowUp entity) {
        return entity == null ? null : entity.getId();
    }

    String fromTicketFollowUpToTicketFollowUpName(TicketFollowUp entity) {
        return entity == null ? null : entity.getFollowUpName();
    }

    TicketFollowUp fromTicketFollowUpIdToTicketFollowUp(Long entityId) {
        if (entityId == null) {
            return null;
        }
        TicketFollowUp entity;
        try {
            entity = ticketFollowUpService.get(entityId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    void afterMapping(@MappingTarget TicketFollowUpRemarkDTO ticketFollowUpRemarkDTO, TicketFollowUpRemark ticketFollowUpRemark) {
        try {
            if (ticketFollowUpRemark != null) {
                if (ticketFollowUpRemark.getTicketFollowUp() != null) {
                    ticketFollowUpRemarkDTO.setTicketFollowUpId(ticketFollowUpRemark.getTicketFollowUp().getId());
                    ticketFollowUpRemarkDTO.setTicketFollowUpName(ticketFollowUpRemark.getTicketFollowUp().getFollowUpName());
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
