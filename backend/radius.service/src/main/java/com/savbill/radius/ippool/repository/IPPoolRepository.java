package com.savbill.radius.ippool.repository;

import com.savbill.radius.ippool.domain.IPPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPPoolRepository extends JpaRepository<IPPool, Long>, QuerydslPredicateExecutor<IPPool> {
    List<IPPool> findAllByIsDeleteIsFalse();

    @Query(value = "select count(*) from tblippool m where m.pool_name =:name and m.MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, List<Long> mvnoIds);

    @Query(value="SELECT * FROM tblippool p LEFT JOIN tblmippoolmapping m ON p.pool_id = m.ippool_id" +
            " WHERE p.usage_category = 'RADIUS' and m.ippool_id IS NULL and p.MVNOID in :mvnoIds" , nativeQuery = true)
    List<IPPool> getAvailableIPPoolIds(List<Long> mvnoIds);

    @Query(value = "select count(*) from tblippool m where m.ip_range =:ipRange and m.MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateIPRange(String ipRange, List<Long> mvnoIds);

    @Query(value = "SELECT * FROM tblippool WHERE (INET_ATON(first_host) = :newStart) OR (INET_ATON(last_host) = :newEnd) OR ((INET_ATON(first_host) <= :newStart) AND (INET_ATON(last_host) >= :newEnd)) OR ((INET_ATON(first_host) >= :newStart) AND (INET_ATON(last_host) <= :newEnd))", nativeQuery = true)
    List<IPPool> checkForIPOverLapping(long newStart, long newEnd);

}
