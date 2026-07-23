package com.savbill.integrationsystem.deviceveri.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.service.ExBaseAbstractService;
import com.savbill.integrationsystem.deviceveri.domain.CustomerPackageRelData;
import com.savbill.integrationsystem.deviceveri.mapper.CustomerPackageRelMapper;
import com.savbill.integrationsystem.deviceveri.model.CustomerPackageRelDTO;
import com.savbill.integrationsystem.deviceveri.repository.CustomerPackckageRelRepo;

@Service
public class CustomerPackageRelService
		extends ExBaseAbstractService<CustomerPackageRelDTO, CustomerPackageRelData, Long> {

	@Autowired
	private CustomerPackckageRelRepo repo;

	@Autowired
	private CustomerPackageRelMapper mapper;

	public CustomerPackageRelService(CustomerPackckageRelRepo repo, CustomerPackageRelMapper mapper) {
		super(repo, mapper);
	}

	@Override
	public String getModuleNameForLog() {
		return "CustomerPackageRelService[]";
	}

	public List<CustomerPackageRelDTO> findByCustservicemappingid(Long custservicemappingid) {
		//List<CustomerPackageRelData> list = repo.findByCustservicemappingidAndStartdateBeforeAndEnddateAfterAndIsDeleteFalse(
				//custservicemappingid, LocalDateTime.now(), LocalDateTime.now());

		List<CustomerPackageRelData> list = repo.findByCustservicemappingidAndIsDeleteFalse(custservicemappingid);
		return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext()))
				.collect(Collectors.toList());
	}

	public List<CustomerPackageRelDTO> findByDebitdocid(Long debitdocid) {
		List<CustomerPackageRelData> list = repo.findByDebitdocidAndIsDeleteFalse(debitdocid);
		return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext()))
				.collect(Collectors.toList());
	}
	
	public List<CustomerPackageRelDTO> findByCustpackageidAndIsDeleteFalse(Long custPackageRelId) {
		List<CustomerPackageRelData> list = repo.findByCustpackageidAndIsDeleteFalse(custPackageRelId);
		return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext()))
				.collect(Collectors.toList());
	}

	public List<CustomerPackageRelDTO> findByCustidAndStartdateBeforeAndEnddateAfterAndIsDeleteFalse(Long custid, LocalDateTime startDate, LocalDateTime endDate) {
		List<CustomerPackageRelData> list = repo.findByCustidAndStartdateBeforeAndEnddateAfterAndIsDeleteFalse(custid, startDate, endDate);
		return list.stream().map(t -> mapper.domainToDTO(t, new CycleAvoidingMappingContext()))
				.collect(Collectors.toList());
	}
}
