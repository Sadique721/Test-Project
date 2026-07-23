package com.savbill.inventorymanagement.modules.CustomerServiceMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerServiceMappingRepository extends JpaRepository<CustomerServiceMapping, Integer>, QuerydslPredicateExecutor<CustomerServiceMapping> {
    List<CustomerServiceMapping> findByCustId(Integer custId);

    List<CustomerServiceMapping> findAllByCustIdIn(List<Integer> custId);

    CustomerServiceMapping findByConnectionNo(String connectionNo);

    List<CustomerServiceMapping> findAllByConnectionNo(String connectionNo);

//    CustomerServiceMapping findByConnectionNoAndCustId(String connectionNo,Integer custId);

    @Query(value = "SELECT csm.serviceid FROM tbltcustomerservicemapping csm WHERE csm.connection_no = :connectionNo AND csm.custid = :custId", nativeQuery = true)
    List<Integer> findServiceIdsByConnectionNoAndCustId(@Param("connectionNo") String connectionNo, @Param("custId") Integer custId);

    List<CustomerServiceMapping> findAllByServiceIdAndCustId(Long serviceId, Integer custId);

    @Query("select csm.id from CustomerServiceMapping csm where csm.connectionNo=:connectionNo")
    List<Integer> findCustServiceIdByConnectionNo(@Param("connectionNo") String connectionNo);

    @Query(value = "SELECT csm.connection_no FROM CustomerServiceMapping csm WHERE csm.serviceid = :serviceId AND csm.custid = :custId LIMIT 1", nativeQuery = true)
    String findConnectionNoByServiceIdAndCustId(@Param("serviceId") Long serviceId, @Param("custId") Integer custId);
    List<CustomerServiceMapping> findAllByIdIn(List<Integer> serviceIds);

    boolean existsByCustIdAndStatusNotIn(Integer id, List<String> strings);
}
