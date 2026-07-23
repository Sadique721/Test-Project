package com.savbill.radius.services;

import java.util.List;

import com.savbill.radius.entity.CustomerReply;
import com.querydsl.core.types.dsl.BooleanExpression;

public interface CustomerReplyService 
{
	CustomerReply findCustomerReplyById(Long id, Integer mvnoId);
	List<CustomerReply> findCustomerReplyByCustomerId(Long id, Integer mvnoId);
	List<CustomerReply> findAllCustomerReply(Integer mvnoId);
	CustomerReply addCustomerReply(CustomerReply customerReply, Integer mvnoId);
	CustomerReply updateCustomerReply(CustomerReply customerReply, Integer MvnoId);
	void deleteCustomerReply(Long id, Integer mvnoId);
	BooleanExpression findCustomerRepliesByCustomerId(Long customerId, Integer MvnoId);
}
