package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import com.savbill.inventorymanagement.modules.Customers.Customers;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface CustomerInventoryMappingRepo extends JpaRepository<CustomerInventoryMapping, Long>, QuerydslPredicateExecutor<CustomerInventoryMapping> {

    List<CustomerInventoryMapping> findAllByCustomerAndStatusAndQtyIsGreaterThanAndIsDeletedFalse(Customers customers, String status,Long qty);
    List<CustomerInventoryMapping> findAllByCustomerAndStatus(Customers customers, String status);

    List<CustomerInventoryMapping> findAllByIdIn(List<Long> custIndMaps);
    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(String connectionNo,Integer customerId);
    List<CustomerInventoryMapping> findAllByItemAssemblyId(Long id);
    List<CustomerInventoryMapping> findByItemId(Long id);

    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalse(String connectionNo);

    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatus(String connectionNo,Integer customerId, String status);
    List<CustomerInventoryMapping> findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatusAndMvnoIdIn(String connectionNo, Integer customer_id, String status, List<Integer> mvnoId);
    List<CustomerInventoryMapping> findAllByCustomerIdAndPlanIdAndIsDeletedFalse(Integer customerId,Long planId);
    List<CustomerInventoryMapping> findAllByCustomerId(Integer customerId);
    List<CustomerInventoryMapping> findAllByCustomerIdAndExternalItemIdIsNullAndIsDeletedIsFalseAndStatus(Integer customer_id, String status);
    List<CustomerInventoryMapping> findAllByCustomerIdAndExternalItemIdIsNotNullAndIsDeletedIsFalseAndStatus(Integer customer_id, String status);
    CustomerInventoryMapping findByCustomerIdAndConnectionNoAndIsDeletedFalse(Integer customerId, String connectionNumber);
    @Query(value = "SELECT COUNT(*) FROM tbltcustomerinventorymapping WHERE customer_id = :customerId AND plan_id = :planId AND is_deleted = false AND status IN ('PENDING', 'ACTIVE')", nativeQuery = true)
    int countActiveOrPendingInventory(@Param("customerId") Integer customerId, @Param("planId") Long planId);

    @Query(value = "SELECT cim.mapping_id FROM tbltcustomerinventorymapping cim " +
            "JOIN tblmcustomers c ON cim.customer_id = c.custid " +
            "WHERE cim.is_deleted = false " +
            "AND c.custid IN (:custId) " +
            "AND (:pendingFilter = false OR UPPER(cim.status) = 'PENDING') " +
            "AND cim.next_approver = :currentStaffId " +
            "ORDER BY cim.mapping_id DESC", nativeQuery = true)
    List<Long> findCustomerInventoryIdsByNativeQuery(@Param("custId") List<Integer> custId,
                                                     @Param("pendingFilter") boolean pendingFilter,
                                                     @Param("currentStaffId") Long currentStaffId);

    @Query(value = "SELECT cim.mapping_id FROM tbltcustomerinventorymapping cim " +
            "JOIN tblmcustomers c ON cim.customer_id = c.custid " +
            "WHERE cim.is_deleted = false " +
            "AND c.parentcustid IN (:parentCustId) " +
            "AND UPPER(c.parent_experience) = :parentExperience " +
            "AND c.is_deleted = false " +
            "AND cim.next_approver = :currentStaffId " +
            "AND c.status = :customerStatus", nativeQuery = true)
    List<Long> findChildCustomerInventoryIdsByNativeQuery(@Param("parentCustId") List<Integer> custId,
                                                          @Param("parentExperience") String parentExperience,
                                                          @Param("customerStatus") String customerStatus,
                                                          @Param("currentStaffId") Long currentStaffId);

    @Transactional
    @Modifying
    @Query("UPDATE CustomerInventoryMapping c SET c.status = :status WHERE c.id IN :ids")
    void updateStatusByIds(@Param("status") String status, @Param("ids") List<Long> ids);

    @Query(value = "select t.connection_no from tbltcustomerinventorymapping t where t.mapping_id = :mappingId and t.is_deleted = false", nativeQuery = true)
    String findConnectionNoByMappingId(@Param("mappingId") Long mappingId);

    @Query(value = "select t.customer_id from tbltcustomerinventorymapping t where t.mapping_id = :mappingId and t.is_deleted = false", nativeQuery = true)
    Long findCustomerIdByMappingId(@Param("mappingId") Long mappingId);

    @Query(value = "select t.service_id from tbltcustomerinventorymapping t where t.mapping_id = :mappingId and t.is_deleted = false", nativeQuery = true)
    Long findServiceIdByMappingId(@Param("mappingId") Long mappingId);

    @Query(value = "select t.createdbystaffid from tbltcustomerinventorymapping t where t.mapping_id = :mappingId", nativeQuery = true)
    Long findCreatedbyIdByMappingId(@Param("mappingId") Long mappingId);

    @Query(value = "select cim.mapping_id from tbltcustomerinventorymapping cim where cim.customer_id = :customerId and cim.external_item_id is null and cim.is_deleted = false and cim.status = :status", nativeQuery = true)
    List<Long> findAllIdsByCustIdAndExternalIdIsNullAndStatus(@Param("customerId") Integer customerId, @Param("status") String status);

    @Query(value = "select cim.mapping_id from tbltcustomerinventorymapping cim where cim.customer_id = :customerId and cim.external_item_id is not null and cim.is_deleted = false and cim.status = :status", nativeQuery = true)
    List<Long> findAllIdsByCustIdAndExternalIdIsNotNullAndStatus(@Param("customerId") Integer customerId, @Param("status") String status);

    @Query(value = "select cim.item_id from tbltcustomerinventorymapping cim where cim.customer_id = :customerId and cim.external_item_id is null and cim.is_deleted = false and cim.status = :status", nativeQuery = true)
    List<Long> findAllItemIdsByCustIdAndExternalIdIsNullAndStatus(@Param("customerId") Integer customerId, @Param("status") String status);

    @Query(value = "select cim.item_id from tbltcustomerinventorymapping cim where cim.customer_id = :customerId and cim.external_item_id is not null and cim.is_deleted = false and cim.status = :status", nativeQuery = true)
    List<Long> findAllItemIdsByCustIdAndExternalIdIsNotNullAndStatus(@Param("customerId") Integer customerId, @Param("status") String status);

    @Query("SELECT new com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerServiceMappingMessage(tim.serviceId, tim.customer.id) FROM CustomerInventoryMapping tim WHERE tim.id = :id")
    CustomerServiceMappingMessage findServiceAndCustomerByMappingId(@Param("id") Long id);
}
