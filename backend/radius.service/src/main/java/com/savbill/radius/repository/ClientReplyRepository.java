package com.savbill.radius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.savbill.radius.entity.ClientReply;

public interface ClientReplyRepository extends JpaRepository<ClientReply, Long>, QuerydslPredicateExecutor<ClientReply>{
	
	@Query(value = "select * from TBLTRADIUSCLIENTREPLY cg where clientgroupid=:clientgroupid",nativeQuery = true)
	List<ClientReply> findbyClientGroup(@Param("clientgroupid") Long clientGroupId);


}
