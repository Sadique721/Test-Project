package com.savbill.taskmanagement.core.modules.tasks.service;


import com.savbill.taskmanagement.core.constants.SubscriberConstants;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseDocDetails;
import com.savbill.taskmanagement.core.modules.tasks.domain.QCaseDocDetails;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseDocDetailsMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseDocDetailsDTO;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseDocDetailsRepository;

import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CaseDocDetailsService extends ExBaseAbstractService<CaseDocDetailsDTO, CaseDocDetails, Long> {

    @Autowired
    CaseDocDetailsRepository repository;

    public CaseDocDetailsService(CaseDocDetailsRepository repository, CaseDocDetailsMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return " [CaseDocDetailsService] ";
    }

    public CaseDocDetails downloadDocument(Long docId, Long caseId) throws Exception {
        QCaseDocDetails qCaseDocDetails = QCaseDocDetails.caseDocDetails;
        BooleanExpression booleanExpression = qCaseDocDetails.ticketId.eq(caseId).and(qCaseDocDetails.docId.eq(docId)).and(qCaseDocDetails.docStatus.eq(SubscriberConstants.ACTIVE));
        Optional<CaseDocDetails> caseDocDetails = repository.findOne(booleanExpression);
        return caseDocDetails.orElse(null);


    }
}
