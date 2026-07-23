package com.savbill.ticketmanagement.core.modules.tickets.service;


import com.savbill.ticketmanagement.core.modules.tickets.domain.CaseUpdateDetails;
import com.savbill.ticketmanagement.core.modules.tickets.mapper.CaseUpdateDetailsMapper;
import com.savbill.ticketmanagement.core.modules.tickets.model.CaseUpdateDetailsDTO;
import com.savbill.ticketmanagement.core.modules.tickets.repository.CaseUpdateDetailsRepository;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
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
