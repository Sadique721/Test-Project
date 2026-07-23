package com.savbill.taskmanagement.core.modules.tasks.service;


import com.savbill.taskmanagement.core.modules.tasks.domain.CaseUpdateDetails;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseUpdateDetailsMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseUpdateDetailsDTO;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseUpdateDetailsRepository;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import org.springframework.stereotype.Service;

@Service
public class CaseUpdateDetailsService extends ExBaseAbstractService<CaseUpdateDetailsDTO, CaseUpdateDetails, Long> {

    public CaseUpdateDetailsService(CaseUpdateDetailsRepository repository, CaseUpdateDetailsMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return " [CaseUpdateDetailsService] ";
    }

}
