package com.savbill.ticketmanagement.core.modules.TicketFollowUp.Service;


import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.TicketFollowUp.Domain.TicketFollowUpAudit;
import com.savbill.ticketmanagement.core.modules.TicketFollowUp.Domain.TicketFollowUpRemark;
import com.savbill.ticketmanagement.core.modules.TicketFollowUp.Mapper.TicketFollowUpRemarkMapper;
import com.savbill.ticketmanagement.core.modules.TicketFollowUp.Model.TicketFollowUpRemarkDTO;
import com.savbill.ticketmanagement.core.modules.TicketFollowUp.Repository.TicketFollowUpAuditRepository;
import com.savbill.ticketmanagement.core.modules.TicketFollowUp.Repository.TicketFollowUpRemarkRepository;
import com.savbill.ticketmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.ticketmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Service
public class TicketFollowUpRemarkService extends ExBaseAbstractService<TicketFollowUpRemarkDTO, TicketFollowUpRemark, Long> {


    public TicketFollowUpRemarkService(JpaRepository<TicketFollowUpRemark, Long> repository, IBaseMapper<TicketFollowUpRemarkDTO, TicketFollowUpRemark> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[TicketFollowUpRemarkService]";
    }




    @Autowired
    private TicketFollowUpRemarkRepository ticketFollowUpRemarkRepository;

    @Autowired
    private TicketFollowUpRemarkMapper ticketFollowUpRemarkMapper;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private TicketFollowUpAuditRepository ticketFollowUpAuditRepository;

    @Transactional
    public GenericDataDTO save(TicketFollowUpRemarkDTO ticketFollowUpRemarkDTO, Integer staffUserId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            TicketFollowUpRemark ticketFollowUpRemark = this.ticketFollowUpRemarkMapper.dtoToDomain(ticketFollowUpRemarkDTO,
                    new CycleAvoidingMappingContext());
            TicketFollowUpRemark savedCafFollowUpRemark = this.ticketFollowUpRemarkRepository.save(ticketFollowUpRemark);
            // add followup remark audit
            Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffUserId);
            if (optionalStaffUser.isPresent()) {
                StaffUser staffUser = optionalStaffUser.get();
                String name = staffUser.getFirstname() + " added follow up remark in "
                        + savedCafFollowUpRemark.getTicketFollowUp().getFollowUpName() + ".Remark: "
                        + savedCafFollowUpRemark.getRemark() + ".";
                addCafFollowUpRemarkAudit(Math.toIntExact(savedCafFollowUpRemark.getTicketFollowUp().getTicket().getCaseId()), staffUser,
                        name, "Followup Remark Added");
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("CafFollowUp Remark has been created successfully");
            genericDataDTO.setData(this.ticketFollowUpRemarkMapper.domainToDTO(savedCafFollowUpRemark,
                    new CycleAvoidingMappingContext()));
        } catch (Exception e) {
            ApplicationLogger.logger.error("[CafFollowUpRemarkService]" + e.getMessage(), e);
            e.printStackTrace();
            return genericDataDTO;
        }
        return genericDataDTO;
    }

    public void addCafFollowUpRemarkAudit(Integer ticketId, StaffUser staffUser, String name, String auditName) {
        TicketFollowUpAudit ticketFollowUpAudit = new TicketFollowUpAudit();
        ticketFollowUpAudit.setName(name);
        ticketFollowUpAudit.setAuditName(auditName);
        ticketFollowUpAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
        ticketFollowUpAudit.setTicketId(ticketId);
        this.ticketFollowUpAuditRepository.save(ticketFollowUpAudit);
    }

    public GenericDataDTO getAllByTicketFollowUpId(Long cafFollowUpId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<TicketFollowUpRemark> ticketFollowUpRemarkList = this.ticketFollowUpRemarkRepository.findByTicketFollowUpId(cafFollowUpId);
            genericDataDTO.setDataList(this.ticketFollowUpRemarkMapper.domainToDTO(ticketFollowUpRemarkList, new CycleAvoidingMappingContext()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Fetching All CafFollowUpRemark With cafFollowUpId " + cafFollowUpId);
        } catch (Exception e) {
            ApplicationLogger.logger.error("[CafFollowUpRemarkService]" + e.getMessage(), e);
            e.printStackTrace();
            return genericDataDTO;
        }
        return genericDataDTO;
    }
}
