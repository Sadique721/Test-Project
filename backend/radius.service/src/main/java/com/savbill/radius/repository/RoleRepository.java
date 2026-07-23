package com.savbill.radius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, QuerydslPredicateExecutor<Role> {

    Role findByName(String name);

    @Query("select r from Role r where r.name=:name and r.mvnoId=:mvno")
    Role findByNameAndMvno(@Param("name") String name,@Param("mvno") Long mvno);

}
