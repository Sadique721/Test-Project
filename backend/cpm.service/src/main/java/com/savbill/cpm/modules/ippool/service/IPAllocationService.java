package com.savbill.cpm.modules.ippool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.modules.ippool.domain.IPAllocation;
import com.savbill.cpm.modules.ippool.mapper.IPAllocationMapper;
import com.savbill.cpm.modules.ippool.model.IPAllocationDTO;
import com.savbill.cpm.modules.ippool.repository.IPAllocationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPAllocationService extends ExBaseAbstractService<IPAllocationDTO, IPAllocation, Long> {

    @Autowired
    private IPAllocationRepository ipAllocationRepository;

    public IPAllocationService(IPAllocationRepository repository, IPAllocationMapper mapper) {
        super(repository, mapper);
    }

    public List<IPAllocationDTO> getIPAllocationByCustId(Long custId){
        return ipAllocationRepository.findAllByCustId(custId).stream().map(data-> getMapper().domainToDTO(data,new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    @Override
    public String getModuleNameForLog() {
        return "[IPAllocationService]";
    }
}
