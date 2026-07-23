package com.savbill.radius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.radius.entity.RoleScreens;

@Repository
public interface RoleScreensRepository extends JpaRepository<RoleScreens, Long>, QuerydslPredicateExecutor<RoleScreens> {

    @Query("select rs from RoleScreens rs where rs.roleId=:id")
    List<RoleScreens> getScreensByRole(@Param("id") Long id);

    @Query("select rs from RoleScreens rs where rs.roleId IN (:ids)")
    List<RoleScreens> getScreensByRoles(@Param("ids") List<Long> ids);

	List<RoleScreens> findByRoleIdAndMvnoId(Long roleId, Long mvnoId);
}
