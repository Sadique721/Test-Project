package com.savbill.radius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.ClientGroup;

@Repository
public interface ClientGroupRepository extends JpaRepository<ClientGroup, Long>,QuerydslPredicateExecutor<ClientGroup> {

	List<ClientGroup> findByNameContaining(String name);

	Optional<ClientGroup> findById(Long id);

	List<ClientGroup> findAll();

	@Query(value = "select clientgroupid,name from tblmclientgroup cg where name=:name",nativeQuery = true)
	List<Object[]> checkForUniqueClientGroup(@Param("name") String name);
	
	@Query(value = "select clientgroupid,name from tblmclientgroup cg where name=:name AND clientgroupid!=:clientgroupid",nativeQuery = true)
	List<Object[]> checkForUniqueClientGroupOnUpdate(@Param("clientgroupid") Long clientgroupid, @Param("name") String name);
}
