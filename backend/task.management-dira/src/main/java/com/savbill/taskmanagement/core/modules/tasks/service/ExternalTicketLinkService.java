package com.savbill.taskmanagement.core.modules.tasks.service;

import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCustomerDetails;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseCustomerDetailsRepository;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseRepository;
import com.savbill.taskmanagement.core.modules.tasks.repository.ExternalTicketLinkRepository;
import com.savbill.taskmanagement.core.modules.utils.CommonConstants;
import com.savbill.taskmanagement.kafka.KafkaMessageData;
import com.savbill.taskmanagement.kafka.KafkaMessageSender;
import com.savbill.taskmanagement.rabbitmq.messages.CloseTicketCheckMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExternalTicketLinkService {


    @Autowired
    ExternalTicketLinkRepository externalTicketLinkRepository;


    @Autowired
    CaseCustomerDetailsRepository caseCustomerDetailsRepository;

    @Autowired
    KafkaMessageSender kafkaMessageSender;
    @Autowired
    CaseRepository caseRepository;




    public void externalTicketClose(Integer taskId){
        try{
            CloseTicketCheckMessage closeTicketCheckMessage = new CloseTicketCheckMessage();
            List<Integer> closedTicketIds = new ArrayList<>();
            HashMap<String,Object> map = new HashMap<>();
            closedTicketIds = externalTicketLinkRepository.findAllByTaskId(taskId);

            Optional<Case> cases=caseRepository.findById(Long.valueOf(taskId));
            if (cases.isPresent() && cases.get().getFinalResolvedBy() != null) {
                closeTicketCheckMessage.setStaffId(cases.get().getFinalResolvedBy().getId());
            }
          List<CaseCustomerDetails> caseCustomerDetails=  caseCustomerDetailsRepository.findByCaseId(taskId);
            if (!caseCustomerDetails.isEmpty()) {
                caseCustomerDetails.stream().forEach(i -> i.setCaseStatus("Closed"));
                caseCustomerDetailsRepository.saveAll(caseCustomerDetails);
            }
            if(closedTicketIds!=null){
                map.put("ticketIds",closedTicketIds);
                closeTicketCheckMessage.setCustomerMessage(map);
                log.debug("External Linked Ticket Message :"+closeTicketCheckMessage);
                kafkaMessageSender.send(new KafkaMessageData(closeTicketCheckMessage, CloseTicketCheckMessage.class.getSimpleName(), CommonConstants.EXTERNAL_TICKET_CLOSE));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public GenericDataDTO getAllExternalLinkedTickets(Integer taskId){
        try{
            GenericDataDTO genericDataDTO = new GenericDataDTO();

            List<Integer>integerList = externalTicketLinkRepository.findAllByTaskId(taskId);

            if(!integerList.isEmpty()){
                List<CaseCustomerDetails> caseCustomerDetailsList = caseCustomerDetailsRepository.findAllByCaseIdIn(integerList);
                if(!caseCustomerDetailsList.isEmpty()){
                    genericDataDTO.setDataList(caseCustomerDetailsList);
                    log.debug("External Linked Ticket DataList :"+caseCustomerDetailsList);
                    return genericDataDTO;
                }

            }
        }catch (Exception e){

        }
        return  null;
    }

}
