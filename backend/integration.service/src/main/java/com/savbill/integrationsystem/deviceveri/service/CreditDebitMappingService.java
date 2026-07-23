package com.savbill.integrationsystem.deviceveri.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.CreditDebitMappingData;
import com.savbill.integrationsystem.deviceveri.mapper.CreditDebitMappingMapper;
import com.savbill.integrationsystem.deviceveri.model.CreditDebitMappingDTO;
import com.savbill.integrationsystem.deviceveri.repository.CreditDebitMappingRepo;

@Service
public class CreditDebitMappingService extends ExBaseAbstractService<CreditDebitMappingDTO, CreditDebitMappingData, Long> {


    @Autowired
    private CreditDebitMappingRepo repo;

    @Autowired
    private CreditDebitMappingMapper mapper;

    public CreditDebitMappingService(CreditDebitMappingRepo repo, CreditDebitMappingMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "CreditDebitMappingService[]";
    }
    
    public List<CreditDebitMappingDTO> findByDebitdocumentidAndIsDeleted(Long debitdocumentid, Integer isDeleted){
    	List<CreditDebitMappingData> list = repo.findByDebitdocumentidAndIsDeleted(debitdocumentid, isDeleted);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }
    
    public List<CreditDebitMappingDTO> findByCreditdocidAndIsDeleted(Long creditdocid, Integer isDeleted){
    	List<CreditDebitMappingData> list = repo.findByCreditdocidAndIsDeleted(creditdocid, isDeleted);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }
}
