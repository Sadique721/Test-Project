package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository;

import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPayment;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BatchPaymentDetailsRepository extends JpaRepository<BatchPaymentDetails, Long>, QuerydslPredicateExecutor<BatchPaymentDetails> {

    @Query(value = "select count(*) from tbltbatchpaymentdetails t where  t.staff_id = :id", nativeQuery = true)
    public Long findMinimumApprovalReuqestByStaff(@Param("id") Integer id);

    @Query(value = "select * from tbltbatchpaymentdetails t where  t.batch_id = :batchId and t.staff_id= :staffId", nativeQuery = true)
    public List<BatchPaymentDetails> findByBatchPaymentAndStaffUser(@Param("batchId")  Long batchId, @Param("staffId") Integer staffId);

    @Query(value = "select * from tbltbatchpaymentdetails t where  t.batch_id = :batchId and t.staff_id= :staffId", nativeQuery = true)
    public BatchPaymentDetails findByBatchPaymentAndStaffUserDelete(@Param("batchId")  Long batchId, @Param("staffId") Integer staffId);

    @Query(value = "select * from tbltbatchpaymentdetails t where  t.staff_id = :staffId", nativeQuery = true)
    public List<BatchPaymentDetails> findByStaffId(@Param("staffId") Long staffId);

    @Query(value = "select * from tbltbatchpaymentdetails t where t.batch_id= :batchId and t.next_staff_id = :staffId", nativeQuery = true)
    public List<BatchPaymentDetails> findPreviousAssigne(@Param("batchId") Long batchId,@Param("staffId") Integer staffId);
    @Query(value = "select * from tbltbatchpaymentdetails t where t.batch_id= :batchId and t.staff_id = :staffId and t.assignedstatus ='Assigned'", nativeQuery = true)
    public BatchPaymentDetails findPreviousAssignees(@Param("batchId") Long batchId,@Param("staffId") Integer staffId);

    @Query(value = "select * from tbltbatchpaymentdetails t where t.batch_id= :batchId and t.staff_id = :staffId", nativeQuery = true)
    public BatchPaymentDetails findNextAssignee(@Param("batchId") Long batchId,@Param("staffId") Integer staffId);

    Optional<BatchPaymentDetails> findTopByBatchPaymentAndStaffUserAndStatusOrderByAssignedDateDesc(BatchPayment batchPayment, StaffUser staffUser, String status);

}
