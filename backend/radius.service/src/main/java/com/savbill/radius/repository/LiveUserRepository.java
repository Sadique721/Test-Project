package com.savbill.radius.repository;

import com.savbill.radius.SoapApi.Dto.GetUserSessionresponseDto;
import com.savbill.radius.entity.LiveUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveUserRepository extends JpaRepository<LiveUser, Long>, QuerydslPredicateExecutor<LiveUser> {
    @Query("SELECT u FROM LiveUser u WHERE (:userName is null or u.userName LIKE %:userName%) and (:framedIpAddress is null or u.framedIpAddress LIKE %:framedIpAddress%)")
    List<LiveUser> findByUserName(@Param("userName") String userName, @Param("framedIpAddress") String framedIpAddress);

    LiveUser findFirstByUserNameOrderByCdrIDDesc(String userName);

    @Query(value = "SELECT UserName from tbltliveuser where UserName in :users", nativeQuery = true)
    List<String> findUsernameByStatus(@Param("users") List<String> users);

    List<LiveUser> findAllByCallingStationId(String mac);

    @Query("SELECT u FROM LiveUser u WHERE u.lClass = :userName")
    List<LiveUser> findLiveUsersByLClass(@Param("userName") String userName);

    int deleteByCdrIDIn(List<Long> ids);

    @Query(value = "Select * from  TBLTLIVEUSER u where u.mvnoid = :mvnoId and u.NASIPAddress = :sourceIpAddress and u.lastmodificationdate < NOW() - INTERVAL :threshold MINUTE", nativeQuery = true)
    List<LiveUser> getLiveUsersToPurgeSession(Integer mvnoId, String sourceIpAddress, Long threshold);


    List<LiveUser> getLiveUsersByCdrIDIn(List<Long> liveUserIds);

    @Query("SELECT new com.savbill.radius.SoapApi.Dto.GetUserSessionresponseDto(" +
            "lu.cdrID,lu.acctSessionId , lu.callingStationId , lu.delegatedIPv6Prefix , lu.framedIPv6Prefix , lu.nasPortId , lu.nasPortType , lu.framedIpAddress , lu.userName , lu.acctSessionTime, lu.createdDate) " +
            "FROM LiveUser lu " +
            "WHERE lu.framedIpAddress = :ipAddress")
    List<GetUserSessionresponseDto> findUserSessionByIp(@Param("ipAddress") String ipAddress);

    @Query("SELECT new com.savbill.radius.SoapApi.Dto.GetUserSessionresponseDto(" +
            "lu.cdrID,lu.acctSessionId , lu.callingStationId , lu.delegatedIPv6Prefix , lu.framedipv6address , lu.nasPortId , lu.nasPortType , lu.framedIpAddress , lu.userName , lu.acctSessionTime, lu.createdDate) " +
            "FROM LiveUser lu " +
            "WHERE lu.framedIpAddress = :ipAddress")
    List<GetUserSessionresponseDto> findUserSessionByIpAddress(@Param("ipAddress") String ipAddress);
    Long countLiveUserByCustid(String custId);


    @Query(value = "SELECT UserName from tbltliveuser where framedIpAddress =:ipAddress", nativeQuery = true)
    List<String> findUsernameByipAddress(@Param("ipAddress") String ipAddress);

    //For retrive listof framedip
//    @Query("SELECT u FROM LiveUser u WHERE u.framedIpAddress = :framedIpAddress")
//    List<LiveUser> findByFramedIpAddress(@Param("framedIpAddress") String framedIpAddress);

    @Query(value = "SELECT u FROM LiveUser u WHERE u.framedIpAddress = :framedIpAddress ORDER BY u.createdDate DESC")
    List<LiveUser> findByFramedIpAddressOrderByCreatedDateDesc(@Param("framedIpAddress") String framedIpAddress);

    LiveUser findFirstByCustidOrderByCdrIDAsc(String custid);

//    @Query("SELECT u.cdrID FROM LiveUser u WHERE u.framedIpAddress = :ipAddress")
//    Long findCdrIDByIp(@Param("ipAddress") String ipAddress);

    @Query("SELECT u.cdrID FROM LiveUser u WHERE u.framedIpAddress = :ipAddress")
    List<Long> findCdrIDByIp(@Param("ipAddress") String ipAddress);

    @Query("SELECT u.cdrID FROM LiveUser u WHERE u.userName = :username")
    List<Long> findCdrIDByuserName(@Param("username") String username);

    @Query("SELECT new com.savbill.radius.SoapApi.Dto.GetUserSessionresponseDto(" +
            "lu.cdrID,lu.acctSessionId , lu.callingStationId , lu.delegatedIPv6Prefix , lu.framedIPv6Prefix , lu.nasPortId , lu.nasPortType , lu.framedIpAddress , lu.userName , lu.acctSessionTime) " +
            "FROM LiveUser lu " +
            "WHERE lu.userName = :username")
    List<GetUserSessionresponseDto> findUserSessionByuserName(@Param("username") String username);

    boolean existsByUserName(String userName);

    List<LiveUser> findAllByUserName(String username);

    List<LiveUser> findAllByCustid(String custId);

    @Query(value = "SELECT * FROM TBLTLIVEUSER u WHERE u.FRAMEDIPADDRESS = :ipAddress", nativeQuery = true)
    List<LiveUser> findByFramedIpAddress(@Param("ipAddress") String ipAddress);

    List<LiveUser> findAllByFramedIpAddress(String username);

    List<LiveUser> findAllByNasIpAddress(String username);

    List<LiveUser> findByUserName(String subscriberId);

    @Query("SELECT l.acctInputOctets, l.acctOutputOctets FROM LiveUser l WHERE l.custid = :custid")
    List<Object[]> findQuotaByCustid(@Param("custid") String custid);

    @Query("SELECT u.custid, u.lastmodifiedDate, u.acctInputOctets, u.acctOutputOctets , u.cprId FROM LiveUser u WHERE u.custid = :custid")
    List<Object[]> findLiveUserDataByCustid(@Param("custid") String custid);

    @Query(value = "select t.FRAMEDIPADDRESS from TBLTLIVEUSER t where t.custid =:custid order by t.CREATEDATE desc limit 1",nativeQuery = true)
    Optional<String> findFramedIpAddressByCustid(@Param("custid") String custid);
}
