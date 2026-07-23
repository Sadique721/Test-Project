package com.savbill.ticketmanagement.core.modules.PartnerServiceArea.repository;

import com.savbill.ticketmanagement.core.modules.PartnerServiceArea.domain.PartnerServiceAreaMapping;
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

    @Query(value = "select partnerServiceAreaMapping.serviceId\n" +
            "from PartnerServiceAreaMapping partnerServiceAreaMapping\n" +
            "where partnerServiceAreaMapping.partnerId IN :partnerIDs")
    List<Long> serviceAreaIdListWherePartnerIsNotBind(@Param("partnerIDs") List partnerIDs);

    @Query(value = "select partnerServiceAreaMapping.serviceId\n" +
            "from PartnerServiceAreaMapping partnerServiceAreaMapping\n" +
            "where partnerServiceAreaMapping.partnerId =:partnerID")
    List<Long> serviceAreaIdWherePartnerIsNotBind(@Param("partnerID") Integer partnerID);

    List<PartnerServiceAreaMapping> findAllByPartnerIdIn(List<Integer> partnerid);

}
