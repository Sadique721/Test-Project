package com.diameter.service;

import java.util.List;

import javax.xml.bind.ValidationException;

import com.diameter.model.QOSPolicy;
import com.diameter.model.QOSPolicyGatewayMapping;

public interface QOSPolicyService {

	QOSPolicy create(QOSPolicy policy) throws ValidationException;

	QOSPolicy getById(String id);

	QOSPolicy getByName(String name);

	List<QOSPolicy> getAll();

	List<QOSPolicyGatewayMapping> getGatewayMappingByQosPolicyId(String qosPolicyId);
}
