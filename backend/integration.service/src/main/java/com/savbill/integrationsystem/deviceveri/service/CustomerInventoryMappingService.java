package com.savbill.integrationsystem.deviceveri.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.CustomerInventoryMappingData;
import com.savbill.integrationsystem.deviceveri.mapper.CustomerInventoryMappingMapper;
import com.savbill.integrationsystem.deviceveri.model.CustomerInventoryMappingDTO;
import com.savbill.integrationsystem.deviceveri.repository.CustomerInventoryMappingRepo;

@Service
public class CustomerInventoryMappingService
		extends ExBaseAbstractService<CustomerInventoryMappingDTO, CustomerInventoryMappingData, Long> {

	@Autowired
	private CustomerInventoryMappingRepo repo;

	@Autowired
	private CustomerInventoryMappingMapper mapper;

	public CustomerInventoryMappingService(CustomerInventoryMappingRepo repo, CustomerInventoryMappingMapper mapper) {
		super(repo, mapper);
	}

	@Override
	public String getModuleNameForLog() {
		return "CustomerInventoryMappingService[]";
	}

	public List<CustomerInventoryMappingDTO> findByItemIdAndIsDeleted(Long itemId, Integer isDeleted) {
		List<CustomerInventoryMappingData> list = repo.findByItemIdAndIsDeleted(itemId, isDeleted);
		return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext()))
				.collect(Collectors.toList());
	}

	public List<CustomerInventoryMappingDTO> findByCustomerIdAndIsDeleted(Long customerId, Integer isDeleted) {
		List<CustomerInventoryMappingData> list = repo.findByCustomerIdAndIsDeleted(customerId, isDeleted);
		return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext()))
				.collect(Collectors.toList());
	}
	
	public List<CustomerInventoryMappingDTO> findByCustomerIdAndItemIdNotNullAndIsDeleted(Long customerId, Integer isDeleted) {
		List<CustomerInventoryMappingData> list = repo.findByCustomerIdAndItemIdNotNullAndIsDeleted(customerId, isDeleted);
		return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext()))
				.collect(Collectors.toList());
	}
}
