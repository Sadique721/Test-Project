package com.savbill.integrationsystem.billgen.service;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.integrationsystem.billgen.entity.CustomerData;
import com.savbill.integrationsystem.billgen.mapper.CustomerMapper;
import com.savbill.integrationsystem.billgen.model.CustomerDTO;
import com.savbill.integrationsystem.billgen.repository.CustomerRepository;
import com.savbill.integrationsystem.core.mapper.CycleAvoidingMappingContext;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.rabbitmq.CustomerMessage;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository repo;

	private final CustomerMapper mapper;
	@Autowired
	Tracer tracer;

	public void save(CustomerMessage message) {
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put("userName","RabbitMq");
		MDC.put("traceId",traceContext.traceIdString());
		MDC.put("spanId",traceContext.spanIdString());
		CustomerData customerData = new CustomerData(message);
		repo.save(customerData);
		MDC.remove("traceId");
		MDC.remove("spanId");

	}

	public List<CustomerDTO> findById(Integer custId) {
		Optional<CustomerData> optional = repo.findById(custId);
		List<CustomerDTO> list = new ArrayList<>();
		if (optional.isPresent()) {
			CustomerDTO customerDTO = mapper.domainToDTO(optional.get(), new CycleAvoidingMappingContext());
			list.add(customerDTO);
		}
		return list;
	}

	public LoggedInUser getLoggedInUser() {
		LoggedInUser user = null;
		try {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (null != securityContext.getAuthentication()) {
				user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
			}
		} catch (Exception e) {
			user = null;
		}
		return user;
	}
}
