package com.savbill.taskmanagement.core.modules.tasks.service;



import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCustomerDetails;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseDTO;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseCustomerDetailsRepository;
import com.savbill.taskmanagement.rabbitmq.messages.CloseTicketCheckMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CaseCustometDetailsService{

    @Autowired
    CaseCustomerDetailsRepository caseCustomerDetailsRepository;

    public void saveCaseCustomerDetails(CloseTicketCheckMessage closeTicketCheckMessage){
        CaseDTO caseDTO = new CaseDTO();
        Integer custId = null;

        custId = closeTicketCheckMessage.getCustomerId();

        CaseCustomerDetails caseCustomerDetails = new CaseCustomerDetails();

        caseCustomerDetails.setCustomerId(custId);
        caseCustomerDetails.setCaseNumber(closeTicketCheckMessage.getCaseNumber());
        caseCustomerDetails.setCaseStatus(closeTicketCheckMessage.getStatus());
        caseCustomerDetails.setCaseId(closeTicketCheckMessage.getCaseId());

        caseCustomerDetailsRepository.save(caseCustomerDetails);

    }


    public void updateCaseCustomerDetails(CloseTicketCheckMessage closeTicketCheckMessage){
        CaseDTO caseDTO = new CaseDTO();
        Integer custId = null;
        custId = closeTicketCheckMessage.getCustomerId();

        List<CaseCustomerDetails> caseCustomerDetailsList = new ArrayList<>();
        caseCustomerDetailsList = caseCustomerDetailsRepository.findByCaseId(closeTicketCheckMessage.getCaseId());
        if(caseCustomerDetailsList.size()>0){
            for(CaseCustomerDetails caseCustomerDetails : caseCustomerDetailsList){
                caseCustomerDetails.setCaseStatus(closeTicketCheckMessage.getStatus());
                caseCustomerDetailsRepository.save(caseCustomerDetails);
            }

        }




    }
}
