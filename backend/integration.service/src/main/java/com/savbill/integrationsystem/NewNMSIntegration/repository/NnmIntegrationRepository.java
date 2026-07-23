package com.savbill.integrationsystem.NewNMSIntegration.repository;

import com.savbill.integrationsystem.NewNMSIntegration.entity.NmsIntegration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NnmIntegrationRepository extends JpaRepository<NmsIntegration, Long> {
    List<NmsIntegration> findAllByCustomerId(Long customerId);

    @Query(value = "SELECT * FROM tblmnmsintegration " +
            "WHERE item_id = :itemId " +
            "AND customer_id = :customerId " +
            "AND cust_inven_id = :custInvenId " +
            "AND operation = :operation " +
            "AND status = :status " +
            "AND serial_number = :serialNumber " +
            "AND mvno_id = :mvnoId",
            nativeQuery = true)
    List<NmsIntegration> findByItemIdAndCustomerIdAndCustInvenIdAndOperationAndStatusAndSerialNumberAndMvnoId(
            @Param("itemId") Long itemId,
            @Param("customerId") Long customerId,
            @Param("custInvenId") Long custInvenId,
            @Param("operation") String operation,
            @Param("status") String status,
            @Param("serialNumber") String serialNumber,
            @Param("mvnoId") Long mvnoId);

    List<NmsIntegration> findAllNmsIntegrationsByCustomerIdAndOperationAndStatusOrderByIdDesc(Long customerId,String operation,String status);


}
