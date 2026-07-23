package com.savbill.radius.repository;

import com.savbill.radius.entity.CoaDMProfileAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoaDMProfileAttributeRepository extends JpaRepository<CoaDMProfileAttribute, Long>,QuerydslPredicateExecutor<CoaDMProfileAttribute> {
    List<CoaDMProfileAttribute> findCoaDMProfileAttributeByCoaDMProfileId(Long coaDMProfileId);
}
