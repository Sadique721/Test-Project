package com.savbill.radius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long>, QuerydslPredicateExecutor<Staff> {

	Optional<Staff> findByUserName(String userName);
	
	@Query(value = "select * from tblmstaff s where s.username LIKE %:userName%",nativeQuery = true)
	List<Staff> searchByUserName(@Param("userName") String userName);
	
	@Query("select s from Staff s where s.userName = :username and s.mvnoId = :mvnoId")
	Staff findByUserNameAndMvnoId(@Param("username") String username, @Param("mvnoId") Long mvnoId);
}
