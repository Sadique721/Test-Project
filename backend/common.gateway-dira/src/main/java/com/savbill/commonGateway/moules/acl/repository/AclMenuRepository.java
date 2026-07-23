package com.savbill.commonGateway.moules.acl.repository;

import com.savbill.commonGateway.moules.acl.domain.AclMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AclMenuRepository extends JpaRepository<AclMenu, Long>, QuerydslPredicateExecutor<AclMenu> {
    List<AclMenu> findAllByIsDeleteFalseAndProduct(String productName);
    List<AclMenu> findAllByIsDeleteFalseAndProductAndIdIn(String productName, List<Long> ids);

}
