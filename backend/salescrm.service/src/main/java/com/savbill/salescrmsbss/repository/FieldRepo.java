package com.savbill.salescrmsbss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.Fields;

@Repository
public interface FieldRepo extends JpaRepository<Fields, Long> , QuerydslPredicateExecutor<Fields> {
    Fields findByFieldname(String name);

}
