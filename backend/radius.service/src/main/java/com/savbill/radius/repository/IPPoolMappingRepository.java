package com.savbill.radius.repository;

import com.savbill.radius.ippool.domain.IPPoolMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPPoolMappingRepository extends JpaRepository<IPPoolMapping, Long>, QuerydslPredicateExecutor<IPPoolMapping> {
    List<IPPoolMapping> findByIpPoolId(Long poolId);

    @Query(value="select c.clientip from tbltclients c JOIN tblmippoolmapping im ON c.clientid = im.clientid " +
            "WHERE im.ippool_id = :poolId", nativeQuery = true)
    List<String> findClientIPByIpPoolId(Long poolId);

    @Query(value="select distinct im.clientid from tblmippoolmapping im JOIN tbltclients c ON c.clientid = im.clientid" , nativeQuery = true)
    List<Long> findDistinctClientId();

    @Query(value="select *  from tblmippoolmapping im JOIN tbltclients c ON c.clientid = im.clientid" , nativeQuery = true)
    List<Long> findIPPoolWithIPRange();

}
