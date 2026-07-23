package com.savbill.integrationsystem.PaymentIntegration.Repository;

import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long>, QuerydslPredicateExecutor<CustomerPayment> {
    List<CustomerPayment> findAllByPgTransactionId(String id);

    List<CustomerPayment> findAllByCheckoutRequestId(String id);


    List<CustomerPayment> findAllByOrderIdAndStatusContainingIgnoreCase(Long orderId , String name);

    List<CustomerPayment> findAllByOrderId(Long orderId);

    List<CustomerPayment> findCustomerPaymentByCustId(Integer custId);
    List<CustomerPayment> findCustomerPaymentByCustIdOrderByPaymentDateDesc(Integer custId);

    List<CustomerPayment> findCustomerPaymentByOrderId(Long orderId);



    @Query(nativeQuery = true,value = "select * from tbltpayment t1 where  t1.MVNOID in :MVNOIDS"
            ,countQuery = "select count(*) from tbltpayment t1 where t1.MVNOID in :MVNOIDS")
    Page<CustomerPayment> findAll(Pageable pageable, @Param("MVNOIDS") List MVNOIDS);

    @Query(nativeQuery = true,value = "select * from tbltpayment t1 where (t1.MVNOID = 1 or (t1.MVNOID = :mvnoId and t1.BUID in :buIds))"
            ,countQuery = "select count(*) from tbltpayment t1 where  (t1.MVNOID = 1 or (t1.MVNOID = :mvnoId and t1.BUID in :buIds))")
    Page<CustomerPayment> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    // Method 1: findAllByCustomerUsernameAndOrderId
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2")
    Page<CustomerPayment> findAllByCustomerUsernameAndOrderId(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable);

    // Method 2: findAllByCustomerUsernameAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2 " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2 " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByCustomerUsernameAndMvnoidIn(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // Method 3: findAllByCustomerUsernameAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2 " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2 " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByCustomerUsernameAndMvnoidInAndBuidIn(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);

    // New Method 1: findAllByCustomerUsername
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByCustomerUsername(@Param("s1") String s1, Pageable pageable);

    // New Method 2: findAllByCustomerUsernameAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByCustomerUsernameAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByCustomerUsernameAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByCustomerUsernameAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);



    // serach using status


    // New Method 1: findAllByStatus
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.status like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.status like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByStatus(@Param("s1") String s1, Pageable pageable);

    // New Method 2: findAllByStatusAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.status like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.status like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByStatusAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByStatusAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.status like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.status like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByStatusAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);



    //find all by orderid


    // New Method 1: findAllByOrderid
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.orderid like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.orderid like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByOrderid(@Param("s1") String s1, Pageable pageable);

    // New Method 2: findAllByOrderidAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.orderid like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.orderid like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByOrderidAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByOrderIdAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.orderid like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.orderid like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByOrderidAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);


    //find all by merchantname


    // New Method 1: findAllByMerchantName
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.merchant_name like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.merchant_name like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByMerchantName(@Param("s1") String s1, Pageable pageable);

    // New Method 2: findAllByMerchantNameAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.merchant_name like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.merchant_name like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByMerchantNameAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByMerchantNameAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.merchant_name like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.merchant_name like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByMerchantNameAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);

    CustomerPayment findByCustIdAndAndOrderId(Integer custId,Long orderId);

    CustomerPayment findByOrderId(Long orderId);

    CustomerPayment findByCustomerUUID(String customerUuId);

    @Query(value = "SELECT MAX(id) AS latestId FROM tbltpayment t",nativeQuery = true)
    Long getLatestId();
    @Query(value = "select * from tbltpayment t1 where t1.status <> :status and t1.customer_uuid<> 'NULL' and t1.is_scheduled = :is_scheduled", nativeQuery = true)
    List<CustomerPayment> findAllByStatusAndIsScheduled(@Param("status") String status, @Param("is_scheduled") Boolean is_scheduled);

    @Query(value = "select * from tbltpayment t1 where lower(t1.status) in (:status) and t1.is_scheduled = :is_scheduled and t1.mvnoid =:mvnoId", nativeQuery = true)
    List<CustomerPayment> findAllByStatusAndIsScheduledAndMvnoId(@Param("status") List<String> status, @Param("is_scheduled") Boolean is_scheduled , @Param("mvnoId") Integer mvnoId);

    @Query(value = "select * from tbltpayment t1 where t1.status <> :status and t1.customer_uuid<> 'NULL' and t1.merchant_name = :merchant_name and t1.is_scheduled = :is_scheduled", nativeQuery = true)
    List<CustomerPayment> findAllByStatusAndMechantNameAndIsScheduled(@Param("status") String status, @Param("merchant_name") String merchant_name, @Param("is_scheduled") Boolean is_scheduled);

    @Query(value = "select t.orderid from tbltpayment t where t.pgtransactionid =:pgTransactionId",nativeQuery = true)
    Long findOrderIdBypgTransactionId (@Param("pgTransactionId") String pgTransactionId);

    @Query(value = "SELECT * FROM tbltpayment cp WHERE " +
            "((cp.payment_date >= :paidAfter AND cp.payment_date <= :paidBefore) OR " +
            "(cp.transaction_date >= :forwardedAfter AND cp.transaction_date <= :forwardedBefore)) " +
            "AND cp.merchant_name LIKE 'PAYWAY_PAYMENT%'", nativeQuery = true)
    List<CustomerPayment> findPaymentsByDateRange(
            @Param("paidAfter") LocalDateTime paidAfter,
            @Param("paidBefore") LocalDateTime paidBefore,
            @Param("forwardedAfter") LocalDateTime forwardedAfter,
            @Param("forwardedBefore") LocalDateTime forwardedBefore
    );

    @Query(value = "select * from tbltpayment t1 where (t1.pgtransactionid is NULL OR t1.pgtransactionid = 'NA');", nativeQuery = true)
    List<CustomerPayment> findAllByPgTransactionIdAndIsScheduled();

    @Query(value = "select * from savbillintegrationsystem.tbltpayment t where gateway_status in ('Initiate','TIP','PENDING') and (pgtransactionid is null or pgtransactionid = 'NA')", nativeQuery = true)
    List<CustomerPayment> findPendingPgTransactionPayments();

    List<CustomerPayment> findCustomerPaymentByPartnerId(Integer custId);

    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.pgtransactionid like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.pgtransactionid like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByPgTransactionIdWithSearch(@Param("s1") String s1, Pageable pageable);

    // New Method 2: findAllByPgTransactIdAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.pgtransactionid like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.pgtransactionid like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByPgTransactionIdWithSearchAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByPgTransactIdAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.pgtransactionid like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.pgtransactionid like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByPgTransactionIdWithSearchAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);

    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.account_number like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.account_number like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByAccountNumberWithSearch(@Param("s1") String s1, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.account_number like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.account_number like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByAccountNumberWithSearchAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByPgTransactIdAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.account_number like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.account_number like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByAccountNumberWithSearchAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);

    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.payer_mobile_number like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.payer_mobile_number like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByPayerMobileNumberWithSearch(@Param("s1") String s1, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.payer_mobile_number like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.payer_mobile_number like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByPayerMobileNumberWithSearchAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.account_number like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.account_number like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByPayerMobileNumberWithSearchAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);

    @Query("SELECT c FROM CustomerPayment c WHERE c.transactionDate >= :fromDate AND c.transactionDate < :toDate")
    Page<CustomerPayment> findByTransactionDateBetween(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
    @Query(value = "SELECT * FROM tbltpayment WHERE DATE(transaction_date) = :transactionDate",
            nativeQuery = true)
    Page<CustomerPayment> findByTransactionDate(
            @Param("transactionDate") String transactionDate,
            Pageable pageable
    );

    @Query(value = "select t.mvnoid from tbltpayment t where t.orderid = :orderId LIMIT 1",nativeQuery = true)
    Integer findMvnoIdByOrderId(@Param("orderId") Long orderId);


    CustomerPayment findByPgTransactionId(String pgTransactionId);

    @Query(
            value = "SELECT * FROM tbltpayment t WHERE  t.merchant_name = :merchantName AND t.is_scheduled = :isScheduled AND t.status = :status",
            nativeQuery = true
    )
    List<CustomerPayment> findAllByStatusAndMerchantNameAndIsScheduled(
            @Param("merchantName") String merchantName,
            @Param("isScheduled") Boolean isScheduled,
            @Param("status") String status
    );

    @Query(value = "SELECT * FROM tbltpayment WHERE pgtransactionid = :pgTransactionId", nativeQuery = true)
    Optional<CustomerPayment> findByPgTransaction(String pgTransactionId);

    CustomerPayment findByOrderIdAndGatewayStatusNot(Long orderId, String gatewayStatus);
    CustomerPayment findByPgTransactionIdAndGatewayStatusNot(String pgTransactionId,String gatewayStatus);


    @Query(value = "SELECT * FROM tbltpayment t " +
            "WHERE t.account_number = :accountNumber " +
            "AND t.payment = :payment " +
            "AND t.payer_mobile_number = :payerMobileNumber " +
            "AND t.status NOT IN ('SUCCESSFUL','FAILED') " +
            "ORDER BY t.id DESC " +
            "LIMIT 1",
            nativeQuery = true)
            CustomerPayment findLatestPendingPayment(
            @Param("accountNumber") String accountNumber,
            @Param("payment") Double payment,
            @Param("payerMobileNumber") String payerMobileNumber
    );
}
