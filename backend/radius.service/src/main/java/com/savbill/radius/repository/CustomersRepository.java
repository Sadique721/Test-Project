package com.savbill.radius.repository;

import com.savbill.radius.SoapApi.Dto.GetAccountDetailsDto;
import com.savbill.radius.SoapApi.Dto.ReAuthDto;
import com.savbill.radius.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomersRepository extends JpaRepository<Customers, Integer>, QuerydslPredicateExecutor<Customers> {
    List<Customers> findByUsernameOrFirstnameOrLastnameContainingIgnoreCase(String username, String firstname, String lastname);

    @Query(value = "select * from tblCustomers where firstname like '%' :search  '%' or lastname like '%' :search  '%'or username like '%' :search  '%' AND MVNOID= :MVNOID OR MVNOID IS NULL", nativeQuery = true)
    List<Customers> findBySearchText(@Param("search") String searchText);
    // Optional<Customers> findByUserName(String name);
    //  Optional<Customers> findByUserNameAndMvnoId(String name,Integer mvnoId);

    @Query("select username from Customers o where servicearea in :ids")
    List<String> findByServiceAreaIds(@Param("ids") List<Long> serviceAreaIds);

    @Query("select t from Customers t where id =:id")
    Customers findByCustomerId(@Param("id") Integer id);

    @Query("select t from Customers t where t.username =:username")
    Customers findByUsername(@Param(("username")) String username);


    Optional<Customers> findByUsernameAndMvnoId(String name, Integer mvnoId);

    Optional<Customers> findByUsernameAndMvnoIdAndStatusNot(String name, Integer mvnoId, String status);

    @Query(value = "SELECT custid, username, cstatus, password, CREATEDATE, nas_port_id " +
            "FROM tblcustomers " +
            "WHERE username = :name AND MVNOID = :mvnoId AND cstatus != :status",
            nativeQuery = true)
    Object[] findByUsernameAndMvnoIdAndStatusNotNative(
            @Param("name") String name,
            @Param("mvnoId") Integer mvnoId,
            @Param("status") String status);


    List<Customers> findByIdIn(List<Integer> ids);

    boolean existsByUsernameAndMvnoId(String userName, Integer mvnoId);

    boolean existsByUsername(String userName);

    @Query(value = "select custid+1  from tblcustomers t order by 1 desc limit 1 ", nativeQuery = true)
    Long getNextCustomerId();

    @Query("select t from Customers t where t.username =:username and t.gatewayip=:gatewayIP and t.isDeleted=false ")
    Customers findByUsernameAndGatewayIP(String username, String gatewayIP);

    @Query(value = "CALL updates_mvnoid(:oldMvnoid, :newMvnoid)", nativeQuery = true)
    void UpdateMvnoidISP(@Param("oldMvnoid") Integer oldMvnoid, @Param("newMvnoid") Integer newMvnoid);

    Optional<Customers> findByUsernameAndGatewayipEqualsIgnoreCaseAndMvnoIdEquals(@Param("username") String username, @Param("gatewayIP") String gatewayIP, @Param("mvnoId") Integer mvnoId);

    @Query(value = "SELECT custid FROM tblcustomers  WHERE  username =:subscriberId and cstatus != 'Terminate'", nativeQuery = true)
    Long findIdByUsername(String subscriberId);

    @Query("SELECT new com.savbill.radius.SoapApi.Dto.GetAccountDetailsDto(" +
            "c.id, c.maxconcurrentsession, c.status, c.password, c.billday, pp.name, c.framedIp, " +
            "c.VLANID, c.framedIPNetmask, c.framedroute, c.nasPortId, c.gatewayip, c.framedIpv6Address, " +
            "c.delegatedprefix, COALESCE(c.mac_provision, false), c.acctno, c.mobile, c.email) " +
            "FROM Customers c JOIN CustPlanMappping cpm ON c.id = cpm.custid JOIN PostpaidPlan pp on cpm.planId = pp.id " +
            "WHERE c.username = :username AND cpm.custPlanStatus = 'Active' AND c.status != 'Terminate' AND cpm.endDate > CURRENT_TIMESTAMP AND cpm.purchaseType = 'New'")
    List<GetAccountDetailsDto> findAccountDetailsByUsername(@Param("username") String username);

    Customers findAllByUsernameAndMvnoIdAndStatusNot(String name, Integer mvnoId, String status);

    @Query("SELECT c.id, c.username FROM Customers c WHERE c.username = :name AND c.mvnoId = :mvnoId AND c.status <> :status")
    List<Object[]> findByUsernameAndMvnoIdAndStatus(
            @Param("name") String name,
            @Param("mvnoId") Integer mvnoId,
            @Param("status") String status);

    @Query("SELECT new com.savbill.radius.SoapApi.Dto.ReAuthDto(t.username, t.password) from Customers t where t.username = :username")
    ReAuthDto findUsernameAndPasswordByUsername(@Param("username") String username);

    @Query("SELECT c FROM Customers c WHERE c.custtype = 'Postpaid' and c.status ='Active' and c.nextBillDate =:nextBillDate")
    List<Customers> findPostpaidCustomerByNextBillDate(@Param("nextBillDate") LocalDate nextBillDate);

    @Query("SELECT c FROM Customers c WHERE c.custtype = 'Postpaid' and c.status in ('Active', 'InActive', 'Suspend') and c.nextQuotaResetDate =:nextQuotaDate")
    List<Customers> findPostpaidCustomerByNextQuotaResetDate(@Param("nextQuotaDate") LocalDate nextQuotaDate);

    @Query(value = "SELECT custid FROM tblcustomers  WHERE  username =:subscriberId and MVNOID =:mvnoId", nativeQuery = true)
    Long findIdByUsernameAndMvnoId(String subscriberId, Long mvnoId);

    @Query(value = "SELECT custid FROM tblcustomers  WHERE  username =:subscriberId and MVNOID =:mvnoId and cstatus != 'Terminate'", nativeQuery = true)
    Long findIdByUsernameAndMvnoIdAndStatusNotTerminate(String subscriberId, Long mvnoId);

    @Query("SELECT c.id, c.MACADDRESS , c.framedIp FROM Customers c WHERE c.username = :name AND c.mvnoId = :mvnoId")
    List<Object[]> findByUsernameAndMvno(
            @Param("name") String name,
            @Param("mvnoId") Integer mvnoId);

    Customers findCustomerDetailsByUsernameAndMvnoIdAndStatus(String name, Integer mvnoId, String status);


}
