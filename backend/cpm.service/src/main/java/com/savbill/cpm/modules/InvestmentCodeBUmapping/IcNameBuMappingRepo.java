package com.savbill.cpm.modules.InvestmentCodeBUmapping;

import com.savbill.cpm.modules.BusinessUnit.domain.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface IcNameBuMappingRepo extends JpaRepository<IcNameBuMapping,Long>, QuerydslPredicateExecutor<IcNameBuMapping> {
    List<IcNameBuMapping> findAllByBusinessUnitidIn(List<BusinessUnit> buId);
}
