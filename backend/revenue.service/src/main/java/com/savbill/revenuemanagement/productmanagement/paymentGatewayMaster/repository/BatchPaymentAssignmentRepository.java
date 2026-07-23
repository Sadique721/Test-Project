package com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.repository;

import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPayment;
import com.savbill.revenuemanagement.productmanagement.paymentGatewayMaster.model.BatchPaymentAssignment;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@JaversSpringDataAuditable
public interface BatchPaymentAssignmentRepository extends JpaRepository<BatchPaymentAssignment, Long>,QuerydslPredicateExecutor<BatchPaymentAssignment> {

    @Query(value = "select count(*) from tbltbatchpaymentassignment t where  t.staff_id = :id", nativeQuery = true)
    public Long findMinimumApprovalReuqestByStaff(@Param("id") Integer id);

    @Query(value = "select * from tbltbatchpaymentassignment t where  t.batch_id = :batchId and t.staff_id= :staffId", nativeQuery = true)
    public List<BatchPaymentAssignment> findByBatchPaymentAndStaffUser(@Param("batchId")  Long batchId,@Param("staffId") Integer staffId);

    @Query(value = "select * from tbltbatchpaymentassignment t where  t.staff_id = :staffId", nativeQuery = true)
    public List<BatchPaymentAssignment> findByStaffId(@Param("staffId") Long staffId);

    @Query(value = "select * from tbltbatchpaymentassignment t where t.batch_id= :batchId and t.next_staff_id = :staffId", nativeQuery = true)
    public List<BatchPaymentAssignment> findPreviousAssigne(@Param("batchId") Long batchId,@Param("staffId") Integer staffId);
    @Query(value = "select * from tbltbatchpaymentassignment t where t.batch_id= :batchId and t.staff_id = :staffId and t.assignedstatus ='Assigned'", nativeQuery = true)
    public BatchPaymentAssignment findPreviousAssignees(@Param("batchId") Long batchId,@Param("staffId") Integer staffId);

    @Query(value = "select * from tbltbatchpaymentassignment t where t.batch_id= :batchId and t.staff_id = :staffId", nativeQuery = true)
    public BatchPaymentAssignment findNextAssignee(@Param("batchId") Long batchId,@Param("staffId") Integer staffId);

    Optional<BatchPaymentAssignment> findTopByBatchPaymentAndStaffUserAndStatusOrderByAssignedDateDesc(BatchPayment batchPayment, StaffUser staffUser,String status);
}

