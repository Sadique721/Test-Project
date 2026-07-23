package com.savbill.revenuemanagement.core.repository.customer;

import com.savbill.revenuemanagement.core.entity.customers.CustomerServiceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerServiceMapRepository extends JpaRepository<CustomerServiceMapping, Integer> {

    @Query(value = "SELECT c.serviceId FROM CustomerServiceMapping c where c.connectionNo =:connectionNo and c.custId =:custId")
    Long findServiceIdFromConnectionNo(String connectionNo, Integer custId);

    List<CustomerServiceMapping> findAllByCustId(Integer custid);
    List<CustomerServiceMapping> findAllByIdIn(List<Integer> serviceIds);

    boolean existsByCustIdAndStatusNotIn(Integer custId, List<String> status);

    List<CustomerServiceMapping> findAllByCustIdAndStatus(Integer id, String newActivation);

    Boolean existsByCustIdInAndServiceIdInAndInvoiceType(List<Integer> childCustIds, List<Long> singletonList, String invoiceTypeGroup);
    List<CustomerServiceMapping>findAllByCustIdInAndInvoiceType(List<Integer> custId,String status);

    @Query(value = "SELECT custid\n" +
            "FROM tbltcustomerservicemapping\n" +
            "WHERE invoice_type = 'Group'\n" +
            "  AND custid IN (SELECT custid\n" +
            "                 FROM tblcustomers\n" +
            "                 WHERE parentcustid = :custId\n" +
            "                   AND CAST(NEXTBILLDATE AS DATE) = :date)", nativeQuery = true)
    List<Integer> findChildIds(@Param("custId") Integer custId, @Param("date") LocalDate date);


    @Query(value = "SELECT c.newDiscountExpiryDate FROM CustomerServiceMapping c where c.id =:custServiceMappingId")
    LocalDate newDiscountExpiryDate(Integer custServiceMappingId);

}
