package com.savbill.revenuemanagement.core.repository.customer;

import com.savbill.revenuemanagement.core.controller.invoice.postpaid.MvnoDebitDocDetailsPojo;
import com.savbill.revenuemanagement.core.dto.dbr.AggregateCount;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.server.CustomerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomersRepository extends JpaRepository<Customers, Integer> {

//    @Query(value = "SELECT new com.savbill.revenuemanagement.core.data.DBResult(c.id, c.username) FROM Customers c where c.id =:custId")
//    List<DBResult> getAllByCustId(@Param("custId") Integer custId);

//    @Query(value = "SELECT c.id FROM Customers c where c.nextBillDate =:billDate and c.status =:status and c.customerType =:type")

//    Optional<Customers> findById(Integer id);
    @Query(value = "select custid from tblcustomers t where customertype = 'Postpaid' and cstatus = 'Active' and NEXTBILLDATE = ?1", nativeQuery = true)
    List<Integer> findAllByNextBillDateAndStatusAndCustomerType(LocalDate billDate, String status, String type);
//    List<Customers> findAddressesByCustomerId(Integer customerId);
//    boolean existsById(Integer customerId);
    @Query("SELECT c.username FROM Customers c where c.id = :custId")
    String findNameById(Integer custId);

//    @Query(value = "Select rev.custid from tblcustomers rev Where rev.customertype = 'Postpaid' and rev.cstatus = 'Active' and date(rev.NEXTBILLDATE) = '2023-09-01' \n" +
//            "and rev.MVNOID =16 and (rev.servicearea_id  In (5));", nativeQuery = true)
//    List<Integer> getCustomersByQuery(@Param("nextbilldate") String nextbilldate,@Param("custType") String custType,@Param("serviceareaIds") List<Integer> serviceareaIds ,@Param("custType") Integer mvnoId);

    List<Customers> findAllByParentCustomersAndStatus(Customers parentCust,String status);

    @Query(value = "select * from tblcustomers  where custid = :custId", nativeQuery = true)
    Customers getByCustomerId(@Param("custId") Integer custId);

    @Query("SELECT c.status FROM Customers c where c.id = :custId")
    String findStatusById(Integer custId);
    @Query(value = "select custname from tblcustomers where custid= :customerId",nativeQuery = true)
    String findCustomerName(@Param("customerId") Integer CustomerId);

    @Query(value = "select username from tblcustomers where custid= :Id",nativeQuery = true)
    String findUsernameById(Integer Id);

    @Query(value = "select * from tblcustomers t where t.servicearea_id IN :serviceAreaIds and partnerid != 1 and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    List<Customers> findByServiceAreaIdInA(@Param("serviceAreaIds") List<Long> serviceAreaIds,@Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select * from tblcustomers t where t.servicearea_id IN :serviceAreaIds and partnerid != 1 and MVNOID in :mvnoIds", nativeQuery = true)
    List<Customers> findByServiceAreaIdInA(@Param("serviceAreaIds") List<Long> serviceAreaIds,@Param("mvnoIds") List mvnoIds);

    List<Customers> findAllByParentCustomers(Customers customers);

    @Query(value = "SELECT MVNOID AS mvnoId, BUID AS buId, servicearea_id AS serviceAreaId FROM tblcustomers WHERE custid in :custIds GROUP BY MVNOID, BUID, servicearea_id",nativeQuery = true)
    List<AggregateCount> getAllByAggregateByDate(@Param("custIds") List<BigInteger> custIds);

    @Query("select c.id from Customers c where c.isDeleted = false and c.mvnoId =:mvnoId")
    List<Integer> getCustIdFromMvnoId(Integer mvnoId);

    @Query("SELECT c.custtype FROM Customers c where c.id = :custId")
    String findCustTypeId(Integer custId);

    @Query(value = "SELECT parentcustid \n" +
            "FROM tblcustomers \n" +
            "WHERE custid IN (SELECT custid FROM tbltcustomerservicemapping WHERE custid IN :custIds AND invoice_type ='Group') \n" +
            "AND parentcustid IS NOT NULL\n",nativeQuery = true)
    List<Integer> findParentIds(@Param("custIds") List<BigInteger> custIds);

    List<Customers> findAllByIdIn(List<Integer> custIds);

    @Query("SELECT c.id FROM Customers c where c.custtype = :customerType")
    List<Integer> findAllIdByCusttypeIgnoreCase(@Param("customerType") String customerType);

    @Query(value = "select t.custid as id,t.lco_id as isLco,t.mvnoId as mvnoId,t.partnerid as partnerId,t.email as email,t.mobile as mobile,t.country_code as countryCode,t.buId as buId,t.customertype as custtype,t.username as username from tblcustomers t where t.custid=:subscriberId", nativeQuery = true)
    CustomerData findByCustomerId(@Param("subscriberId") Integer subscriberId);

    @Query("SELECT new Customers(" +
            "c.id, c.username, c.custname, c.status) " +
            "FROM Customers c " +
            "WHERE c.id = :id")
    Customers findCustomerById(@Param("id") Integer id);

    @Query("SELECT new com.savbill.revenuemanagement.core.controller.invoice.postpaid.MvnoDebitDocDetailsPojo(t1.id, \n" +
            "    t1.docnumber,\n" +
            "    t1.customer.id,\n" +
            "    t1.startdate,\n" +
            "    t1.endate,\n" +
            "    t1.totalamount ,\n" +
            "    t2.username)\n" +
            "FROM \n" +
            "    DebitDocument t1\n" +
            "JOIN \n" +
            "    Customers t2  ON t1.customer = t2.id \n" +
            "WHERE\n" +
            "    t1.id IN :custDebitDocIds" )
    List<MvnoDebitDocDetailsPojo> findInvoiceDetailsofCusMvno(@Param("custDebitDocIds") List<Integer> custDebitDocIds);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tblcustomers set earlybilldate=:earlyBillDate WHERE custid=:custId",nativeQuery = true)
    Integer updateEarlyBillDate(@Param("custId") Integer custId,@Param("earlyBillDate") LocalDate earlyBillDate);

    List<Customers> findByAcctno(String acctno);

    List<Customers> findByAcctnoAndMvnoId(String acctno, Integer mvnoId);


    List<Customers> findByMobile(String phoneNumber);

    Optional<Customers> findByAcctnoAndMobile(String accountNo, String phoneNumber);

    @Query("SELECT c.id FROM Customers c where c.custtype = :customerType AND (c.status='Active' OR c.status='ACTIVE')")
    List<Integer> findAllIdByCustomertypeIgnoreCaseAndStatus(@Param("customerType") String customerType);

    @Query("SELECT c.id FROM Customers c " +
            "WHERE c.status NOT IN ('NewActivation', 'ActivationPending') " +
            "AND EXISTS (" +
            "   SELECT 1 FROM CreditDocument t " +
            "   WHERE t.customer.id = c.id " +
            "   AND t.status IN ('approved', 'Partialy Adjusted') " +
            "   AND t.type IN ('Payment', 'CREDITNOTE') " +
            ") " +
            "AND EXISTS (" +
            "   SELECT 1 FROM DebitDocument d " +
            "   WHERE d.customer.id = c.id AND d.isDelete = false " +
            "   AND d.paymentStatus IN ('Unpaid', 'Partialy Paid') " +
            ")")
    List<Integer> findCustomerByStatusAndCreditDocument();

    @Query("SELECT c FROM Customers c WHERE c.acctno = :accountNo AND c.mobile = :mobile")
    List<Customers> findByAcctnoAndMobiles(@Param("accountNo") String accountNo, @Param("mobile") String phoneNumber);

    @Query("SELECT c FROM Customers c WHERE c.acctno = :acctno AND c.mobile = :mobile")
    List<Customers> findUniqueCustomer(@Param("acctno") String acctno, @Param("mobile") String mobile);


    @Query("SELECT c.id FROM Customers c " +
            "WHERE c.status IN ('NewActivation', 'ActivationPending') " +
            "AND EXISTS (" +
            "   SELECT 1 FROM CreditDocument t " +
            "   WHERE t.customer.id = c.id " +
            "   AND t.status IN ('approved', 'Partialy Adjusted') " +
            "   AND t.type = 'Payment' " +
            ") " +
            "AND EXISTS (" +
            "   SELECT 1 FROM TrialDebitDocument d " +
            "   WHERE d.customer.id = c.id AND d.isDelete = false " +
            "   AND (d.paymentStatus IN ('Unpaid', 'Partialy Paid')  OR d.paymentStatus IS NULL) " +
            "   AND d.billrunstatus NOT IN ('Cancelled') "+
            ")")
    List<Integer> findTrialCustomerByStatusAndCreditDocument();

    @Query("SELECT c.id, c.renewPlanLimit, c.mvnoId FROM Customers c " +
            "WHERE c.status NOT IN ('NewActivation', 'ActivationPending') " +
            " AND LOWER(c.custtype) != 'postpaid' " +
            " AND EXISTS (" +
            "   SELECT 1 FROM DebitDocument d " +
            "   WHERE d.customer.id = c.id AND d.isDelete = false " +
            "   AND d.paymentStatus IN ('Fully Paid') " +
            ")")
    List<Object[]> findCustomerByStatusAndDebitDocument();

    @Query("SELECT c.id, c.renewPlanLimit, c.mvnoId " +
            "FROM Customers c " +
            "WHERE c.status NOT IN ('NewActivation', 'ActivationPending') " +
            "AND LOWER(c.custtype) != 'postpaid' " +
            "AND EXISTS ( " +
            "   SELECT 1 FROM CustPlanMappping cpr " +
            "   JOIN PostpaidPlan p ON cpr.planId = p.id " +
            "   WHERE cpr.customer.id = c.id " +
            "   AND cpr.isDelete = false " +
            "   AND p.offerprice > 0 " +
            "   AND cpr.endDate = ( " +
            "       SELECT MAX(cpr2.endDate) " +
            "       FROM CustPlanMappping cpr2 " +
            "       WHERE cpr2.customer.id = cpr.customer.id " +
            "       AND cpr2.isDelete = false " +
            "   ) " +
            ")")
    List<Object[]> findEligibleCustomerForRenewal();

    @Query("SELECT c.id, c.renewPlanLimit, c.mvnoId FROM Customers c " +
            "WHERE c.status NOT IN ('NewActivation', 'ActivationPending') " +
            " AND LOWER(c.custtype) != 'postpaid' " +
            " AND c.id=:custId " +
            " AND EXISTS (" +
            "   SELECT 1 FROM DebitDocument d " +
            "   WHERE d.customer.id = c.id AND d.isDelete = false " +
            "   AND d.paymentStatus IN ('Fully Paid') " +
            ")")
    Object[] findCustomerByStatusAndDebitDocumentAndCustId(@Param("custId") Integer custId);


    @Query("SELECT c.id, c.renewPlanLimit, c.mvnoId FROM Customers c " +
            "WHERE c.status NOT IN ('NewActivation', 'ActivationPending') " +
            " AND LOWER(c.custtype) != 'postpaid' " +
            " AND c.id=:custId ")
    Object[] findCustomerByStatusAndCustId(@Param("custId") Integer custId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tblcustomers SET earlybilldate = earlybilldate + INTERVAL :billCycle MONTH WHERE custid = :custId", nativeQuery = true)
    int updateEarlyBillDate(@Param("billCycle") int billCycle, @Param("custId") Integer custId);


    @Query("SELECT new Customers(" +
            "c.id, c.username, c.custname, c.status,c.mvnoId,c.buId) " +
            "FROM Customers c " +
            "WHERE c.id = :id")
    Customers findCustomerDataById(@Param("id") Integer id);


    @Query(value = "SELECT CASE WHEN LOWER(customertype) = 'prepaid' THEN 1 ELSE 0 END " +
            "FROM tblcustomers " +
            "WHERE custid = :custId",
            nativeQuery = true)
    Integer isCustomerPrepaid(@Param("custId") Integer custId);

    @Query("SELECT new Customers(" +
            "c.id, c.username, c.custname, c.status,c.mvnoId,c.buId,c.partner) " +
            "FROM Customers c " +
            "WHERE c.id = :id")
    Customers findCustomerDataByIdForPayment(@Param("id") Integer id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tblcustomers SET bill_day_updated = 0 WHERE custid = :custId", nativeQuery = true)
    int resetBillDayUpdatedFlagByCustId(@Param("custId") Integer custId);

    @Query(value = "SELECT * FROM tblcustomers WHERE username = :username", nativeQuery = true)
    Optional<Customers> findByUsername(@Param("username") String username);

    @Query("SELECT c.id, c.renewPlanLimit, c.mvnoId " +
            "FROM Customers c " +
            "WHERE c.status NOT IN ('NewActivation', 'ActivationPending') " +
            "AND LOWER(c.custtype) != 'postpaid' " +
            "AND c.id = :custId " +
            "AND EXISTS ( " +
            "   SELECT 1 FROM CustPlanMappping cpr " +
            "   JOIN PostpaidPlan p ON cpr.planId = p.id " +
            "   WHERE cpr.customer.id = c.id " +
            "   AND cpr.isDelete = false " +
            "   AND p.offerprice > 0 " +
            "   AND cpr.endDate = ( " +
            "       SELECT MAX(cpr2.endDate) " +
            "       FROM CustPlanMappping cpr2 " +
            "       WHERE cpr2.customer.id = cpr.customer.id " +
            "       AND cpr2.isDelete = false " +
            "   ) " +
            ")")
    Object[] findEligibleCustomerForRenewalByCustId(@Param("custId") Integer custId);
}
