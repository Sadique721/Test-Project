package com.savbill.integrationsystem.Case;

import com.savbill.integrationsystem.Case.Repo.repository.*;
import com.savbill.integrationsystem.Case.Repo.repository.CaseUpdateRepository;
import com.savbill.integrationsystem.Case.Repo.repository.TicketAssignStaffMappingRepo;
import com.savbill.integrationsystem.Case.Repo.repository.TicketServiceMappingRepository;
import com.savbill.integrationsystem.Case.mapper.CaseUpdateMapper;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.rabbitmq.TicketMessageIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseService {

    @Autowired
    private CaseRepo caseRepo;

    @Autowired
    private CaseUpdateRepository caseUpdateRepository;

    @Autowired
    private TicketAssignStaffMappingRepo ticketAssignStaffMappingRepo;

    @Autowired
    private CaseUpdateMapper caseUpdateMapper;

    @Autowired
    private TicketServiceMappingRepository ticketServiceMappingRepository;


    public Case save(TicketMessageIntegration message) {
        Case casedata = new Case();
        try {


            if (message != null) {
                List<CaseUpdateDTO> caseUpdateDetailsList = message.getCaseUpdateList();
                List<TicketServicemapping> ticketServicemappingList = message.getTicketServicemappingList();
             //   List<TicketAssignStaffMapping> ticketAssignStaffMappings = message.getTicketAssignStaffMappings();

                Case aCase = new Case(message);
                caseRepo.save(aCase);

                if (!ticketServicemappingList.isEmpty()) {
                    ticketServicemappingList.get(0).setTicketid(aCase.getCaseId());
                }

                ticketServicemappingList.forEach(ticketServiceMapping -> ticketServiceMapping.setTicketid(aCase.getCaseId()));

                if (!caseUpdateDetailsList.isEmpty()) {
                    caseUpdateDetailsList.get(0).setTicket(Math.toIntExact(aCase.getCaseId()));
                }
                CaseUpdateDTO caseUpdateDTOdata = new CaseUpdateDTO(caseUpdateDetailsList);
                CaseUpdate caseUpdate = caseUpdateMapper.dtoToDomain(caseUpdateDTOdata, new CycleAvoidingMappingContext());
                caseUpdateRepository.save(caseUpdate);

                TicketServicemapping ticketServicemappingdata = new TicketServicemapping(ticketServicemappingList);
                ticketServiceMappingRepository.save(ticketServicemappingdata);

             /*   List<CaseUpdateDetailsDTO> caseUpdateDetailsDTOList = caseUpdateDetailsList.get(0).getUpdateDetails();
                CaseUpdateDetails caseUpdateDetails = new CaseUpdateDetails(caseUpdateDetailsDTOList);
                caseUpdateDetailsRepository.save(caseUpdateDetails);*/

              /*  if(ticketAssignStaffMappings != null) {
                    TicketAssignStaffMapping ticketAssignStaffMappingsdata = new TicketAssignStaffMapping(ticketAssignStaffMappings);
                    if (ticketAssignStaffMappingsdata != null && ticketAssignStaffMappingsdata.getTicketId() != null) {
                        ticketAssignStaffMappingRepo.save(ticketAssignStaffMappingsdata);
                    }
                }*/
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("INVALID_DATA");
        }
        return casedata;
    }
}
