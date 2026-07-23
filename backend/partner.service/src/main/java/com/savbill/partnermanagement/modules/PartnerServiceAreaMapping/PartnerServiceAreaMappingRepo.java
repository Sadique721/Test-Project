package com.savbill.partnermanagement.modules.PartnerServiceAreaMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerServiceAreaMappingRepo extends JpaRepository<PartnerServiceAreaMapping, Integer>, QuerydslPredicateExecutor<PartnerServiceAreaMapping> {

    @Query(value = "select partnerServiceAreaMapping.partnerId\n" +
            "from PartnerServiceAreaMapping partnerServiceAreaMapping\n" +
            "where partnerServiceAreaMapping.serviceId in :serviceIDs")
    List<Integer> partnerIdList(@Param("serviceIDs") List serviceIDs);
}
