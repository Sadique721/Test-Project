package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.ConcurrentPolicy;
import com.savbill.radius.helper.ConcurrentPolicyDto;

public interface ConcurrentPolicyService {

	List<ConcurrentPolicy> findAll(Integer mvnoId);

	ConcurrentPolicy findById(Long concurrentPolicyId, Integer mvnoId);

	List<ConcurrentPolicy> searchByPolicyName(String policyName, Integer mvnoId);

	ConcurrentPolicy add(ConcurrentPolicyDto concurrentPolicyDto, Integer mvnoId);

	ConcurrentPolicy update(ConcurrentPolicyDto concurrentPolicyDto, Integer mvnoId);

	void delete(Long concurrentPolicyId, Integer mvnoId);

	String changePolicyStatus(Long concurrentPolicyId, String status,Integer mvnoId);

	List<ConcurrentPolicy> getConcurrentPolicies(Integer mvnoId);

	List<ConcurrentPolicy> findByNameAndMvnoId(String policyName, Integer mvnoId);
}
