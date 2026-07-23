package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.CustomerQosPolicyMapping;

public interface CustomerQosPolicyMappingService {

	List<CustomerQosPolicyMapping> findAllQosPolicyMappings(Integer mvnoId);

	CustomerQosPolicyMapping findQosPolicyMappingById(Long qosPolicyMappingId, Integer mvnoId);

	List<CustomerQosPolicyMapping> findQosPolicyMappingByCustId(Long custId, Integer mvnoId);

	CustomerQosPolicyMapping addQosPolicyMapping(CustomerQosPolicyMapping qosPolicyMapping, Integer mvnoId);

	CustomerQosPolicyMapping updateQosPolicyMapping(CustomerQosPolicyMapping qosPolicyMapping, Integer mvnoId);

	void deleteQosPolicyMapping(Long qosPolicyMappingId, Integer mvnoId);

	void deleteByCustId(Long custId, Integer mvnoId);

}
