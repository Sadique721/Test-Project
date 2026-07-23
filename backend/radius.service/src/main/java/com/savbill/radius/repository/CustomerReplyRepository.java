package com.savbill.radius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.CustomerReply;

@Repository
public interface CustomerReplyRepository extends JpaRepository<CustomerReply, Long>,QuerydslPredicateExecutor<CustomerReply> {

	List<CustomerReply> findByCustomerId(Long customerId);
	
//	@Query(value = "Select * from tbltradiuscustomerreply where custid=?",nativeQuery = true)
//	List<CustomerReply> findCustomerReplyByCustomerId(Long id);
	
}
