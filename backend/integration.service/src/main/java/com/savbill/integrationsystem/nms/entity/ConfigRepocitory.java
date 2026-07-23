package com.savbill.integrationsystem.nms.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepocitory extends JpaRepository<Connfiguration,Long>, QuerydslPredicateExecutor<Connfiguration> {
    Connfiguration findByName(String name);
    Connfiguration findByNameAndMvnoId(String name, Integer mvnoId);
    Connfiguration findAllByName(String name);
    Page<Connfiguration>findByIsdeletedIsFalse(PageRequest pageRequest);
    Integer countByName(String name);

}
