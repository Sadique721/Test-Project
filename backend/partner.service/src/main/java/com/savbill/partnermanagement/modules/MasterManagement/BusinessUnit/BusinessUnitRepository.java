package com.savbill.partnermanagement.modules.MasterManagement.BusinessUnit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, Long>, QuerydslPredicateExecutor<BusinessUnit> {
    List<BusinessUnit> findAllByIdIn(List<Long> buids);
}
