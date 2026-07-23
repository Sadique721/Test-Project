package com.savbill.inventorymanagement.modules.InventoryManagement.ippool.service;

import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.domain.IPAllocation;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.mapper.IPAllocationMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.model.IPAllocationDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.ippool.repository.IPAllocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
