package com.savbill.integrationsystem.deviceveri.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.CustomerServiceMappingData;
import com.savbill.integrationsystem.deviceveri.mapper.CustomerServiceMappingMapper;
import com.savbill.integrationsystem.deviceveri.model.CustomerServiceMappingDTO;
import com.savbill.integrationsystem.deviceveri.repository.CustomerServiceMappingRepo;

@Service
public class CustServiceMappingService extends ExBaseAbstractService<CustomerServiceMappingDTO, CustomerServiceMappingData, Long> {


    @Autowired
    private CustomerServiceMappingRepo repo;

    @Autowired
    private CustomerServiceMappingMapper mapper;

    public CustServiceMappingService(CustomerServiceMappingRepo repo, CustomerServiceMappingMapper mapper) {
        super(repo, mapper);
    }


    @Override
    public String getModuleNameForLog() {
        return "CustomerServiceMappingService[]";
    }
    
    public List<CustomerServiceMappingDTO> findByConnectionNoAndIsDelete(String connectionNo, Integer isDelete){
    	List<CustomerServiceMappingData> list = repo.findByConnectionNoAndIsDelete(connectionNo, isDelete);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<CustomerServiceMappingDTO> findByCustidAndIsDelete(Long custId, Integer isDelete){
    	List<CustomerServiceMappingData> list = repo.findByCustidAndIsDelete(custId, isDelete);
    	return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

}
