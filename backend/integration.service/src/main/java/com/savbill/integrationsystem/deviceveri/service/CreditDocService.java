package com.savbill.integrationsystem.deviceveri.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.CreditDocData;
import com.savbill.integrationsystem.deviceveri.mapper.CreditDocMapper;
import com.savbill.integrationsystem.deviceveri.model.CreditDocDTO;
import com.savbill.integrationsystem.deviceveri.repository.CreditDocRepo;

@Service
public class CreditDocService extends ExBaseAbstractService<CreditDocDTO, CreditDocData, Long> {


    @Autowired
    private CreditDocRepo repo;

    @Autowired
    private CreditDocMapper mapper;

    public CreditDocService(CreditDocRepo repo, CreditDocMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "CreditDebitMappingService[]";
    }
    
    public List<CreditDocDTO> findByIdAndIsDelete(Long creditdocid, Integer isDeleted){
    	Optional<CreditDocData> optional = repo.findByIdAndIsDelete(creditdocid, 0);
    	List<CreditDocDTO> list = new ArrayList<>();
    	if(optional.isPresent()) {
    		CreditDocDTO creditDocDTO = mapper.domainToDTO(optional.get(), new CycleAvoidingMappingContext());
    		list.add(creditDocDTO);
    	}
    	return list;
    }
    
    public List<CreditDocDTO> findByPaymentdateBetween(String paymentDate1, String paymentDate2){
    	List<CreditDocData> list = repo.findByDateBtw(paymentDate1, paymentDate2);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<CreditDocDTO> findByCustomerAndIsDeleteOrderByCreatedateDesc(Long custId, Integer isDelete){
    	List<CreditDocData> list = repo.findByCustomerAndIsDeleteOrderByCreatedateDesc(custId, isDelete);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

}
