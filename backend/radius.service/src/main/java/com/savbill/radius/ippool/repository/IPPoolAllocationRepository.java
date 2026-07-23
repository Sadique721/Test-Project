package com.savbill.radius.ippool.repository;

import com.savbill.radius.ippool.domain.IPPoolAllocationDtls;
import com.savbill.radius.ippool.model.IPPoolAllocationDtlsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IPPoolAllocationRepository extends JpaRepository<IPPoolAllocationDtls, Long>, QuerydslPredicateExecutor<IPPoolAllocationDtls> {

    @Query(value = "select * from tblipallocationdtls t where t.ip_address=:ipAddress", nativeQuery = true)
    List<IPPoolAllocationDtls> findAllByIpAddress(@Param("ipAddress") String ipAddress);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tblipallocationdtls im WHERE im.pool_id = :poolId", nativeQuery = true)
    void deleteByPoolId(Long poolId);

    @Transactional
    @Modifying
    @Query(value = "Update tblipallocationdtls p set p.status = 'Free', block_by_cust_id = null, block_by_session_id = null, nas_ip_address = null where p.pool_id in :ipPoolIds and p.lastmodificationdate < NOW() - INTERVAL :threshold MINUTE and p.status = :status",
            nativeQuery = true)
    int releaseIpBasedOnStatusAndIdleTimeOut(List<Long> ipPoolIds, Long threshold, String status);

    @Query(value = "SELECT count(*) FROM tblipallocationdtls WHERE (INET_ATON(ip_address) >= :newStart) AND (INET_ATON(ip_address) <= :newEnd)", nativeQuery = true)
    Long checkForIPOverLapping(long newStart, long newEnd);

    @Query("SELECT new com.savbill.radius.ippool.model.IPPoolAllocationDtlsDTO(p.ipAddress, p.status, p.blockBySessionId," +
            "p.nasIpAddress, c.username ) " +
            "FROM IPPoolAllocationDtls p left JOIN Customers c ON c.id = p.blockByCustId " +
            "WHERE p.poolId = :poolId")
    Page<IPPoolAllocationDtlsDTO> findAll(@Param("poolId") Long poolId, Pageable pageable);
}
