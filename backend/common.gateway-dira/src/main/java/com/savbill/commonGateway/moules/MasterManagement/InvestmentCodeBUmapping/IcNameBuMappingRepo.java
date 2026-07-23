package com.savbill.commonGateway.moules.MasterManagement.InvestmentCodeBUmapping;


import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface IcNameBuMappingRepo extends JpaRepository<IcNameBuMapping,Long>, QuerydslPredicateExecutor<IcNameBuMapping> {
    List<IcNameBuMapping> findAllByBusinessUnitidIn(List<BusinessUnit> buId);
}
