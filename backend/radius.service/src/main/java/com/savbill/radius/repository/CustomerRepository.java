package com.savbill.radius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>,QuerydslPredicateExecutor<Customer> {
	List<Customer> findByUserNameContaining(String name);
	
//	@Query(value = "select custid,username from tblmcustomers c where c.username=:username",nativeQuery = true)
//	List<Object[]> checkForDuplicateUserOnInsert(@Param("username") String username);
	
//	@Query(value = "select custid,username from tblmcustomers c where c.username=:username AND custid!=:custid",nativeQuery = true)
//	List<Object[]> checkForDuplicateUserOnUpdate(@Param("username") String username,@Param("custid") Long custid);
	
		Optional<Customer> findByUserName(String name);
	
	@Query("SELECT c FROM Customer c WHERE (:name is null or c.userName LIKE %:name%)")
	List<Customer> getCustomersList(@Param("name") String name);
	Optional<Customer> findByUserNameAndMvnoId(String name,Integer mvnoId);

	List<Customer> findByCustomerIdIn(List<Long> ids);

	@Query("SELECT c FROM Customer c WHERE c.userName = :userName")
	Optional<Customer> findByUserNamee(@Param("userName") String userName);

}
