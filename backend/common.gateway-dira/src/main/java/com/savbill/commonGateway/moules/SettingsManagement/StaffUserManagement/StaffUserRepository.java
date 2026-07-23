package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement;

import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO.StaffUserDropdownDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@JaversSpringDataAuditable
public interface StaffUserRepository extends JpaRepository<StaffUser, Integer>, QuerydslPredicateExecutor<StaffUser> {
    @Query(value = "SELECT t.*\n" +
            "    FROM tblmstaffuser t\n" +
            "    JOIN tbltstaffrolerel rs ON t.staffid = rs.staffid\n" +
            "    JOIN tblmroles r  ON rs.roleid = r.roleid\n" +
            "    WHERE r.product = :product and t.MVNOID = :mvnoId",
            nativeQuery = true)
    List<StaffUser> findAllStaffByRoleProduct(@Param("product") String product, @Param("mvnoId") Integer mvnoId);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(s.id, s.username, s.password, s.firstname, s.lastname, " +
            "s.email, s.phone, s.countryCode, s.failcount, s.status, s.partnerid, " +
            "s.isDelete, s.sysstaff, s.tacacsAccessLevelGroup, s.mvnoId, " +
            "s.branchId, s.totalCollected, s.totalTransferred, s.availableAmount, s.lcoId, s.hrmsId, " +
            "s.profileImage, s.department, s.uuid, " +
            "s.isPasswordExpired, s.passwordDate, s.staffUserparent.id) " +
            "FROM StaffUser s " +
            "JOIN s.roles r " +
            "WHERE r.product = :product and s.mvnoId =:mvnoId")
    List<StaffUserPojo> findAllStaffUsersByRoleProduct(@Param("product") String product, @Param("mvnoId") Integer mvnoId);

    @Query(value = "SELECT t.*\n" +
            "    FROM tblmstaffuser t\n" +
            "    JOIN tbltstaffrolerel rs ON t.staffid = rs.staffid\n" +
            "    JOIN tblmroles r  ON rs.roleid = r.roleid\n" +
            "    WHERE r.product = :product",
            nativeQuery = true)
    List<StaffUser> findAllStaffByRoleProduct(@Param("product") String product);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(s.id, s.username, s.password, s.firstname, s.lastname, " +
            "s.email, s.phone, s.countryCode, s.failcount, s.status, s.partnerid, " +
            "s.isDelete, s.sysstaff, s.tacacsAccessLevelGroup, s.mvnoId, " +
            "s.branchId, s.totalCollected, s.totalTransferred, s.availableAmount, s.lcoId, s.hrmsId, " +
            "s.profileImage, s.department, s.uuid, " +
            "s.isPasswordExpired, s.passwordDate, s.staffUserparent.id) " +
            "FROM StaffUser s " +
            "JOIN s.roles r " +
            "WHERE r.product = :product")
    List<StaffUserPojo> findAllStaffUsers(@Param("product") String product);

    @Query(value = "select * from tblmstaffuser where lower(username) like '%' :search '%' order by staffid",
            countQuery = "select count(*) from tblmstaffuser where lower(username) like '%' :search  '%'",
            nativeQuery = true)
    Page<StaffUser> searchEntity(@Param("search") String searchText, Pageable pageable);

    @Query(value = "select * from tblmstaffuser where (lower(username) like '%'  :search '%' ) and partnerid = :partnerid order by staffid",
            countQuery = "select count(*) from tblmstaffuser where (lower(username) like '%' :search  '%') and partnerid= :partnerid",
            nativeQuery = true)
    Page<StaffUser> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("partnerid") Integer partnerid);

    List<StaffUser> findByStatusAndIsDeleteIsFalse(String status);

//    @Query("select new StaffUser(c.id,c.email,c.username,c.mvnoId)   from StaffUser c, CustomerPackage d where "
//            + " c.isDelete =false  and  cast (datediff(d.endDate, curdate()) as integer) = :dateDiff ")
//    List<StaffUser> getDocumentForDunning( @Param(value = "dateDiff") Integer dateDiff);

//    @Query(nativeQuery = true, value = "(select t.* , t3.username from tblmstaffuser t \n" +
//            "inner join tblcustdocdetails t2 \n" +
//            "on  t2.CREATEDBYSTAFFID  = t.staffid \n" +
//            "join tblcustomers t3 \n" +
//            "on t2.cust_id = t3.custid \n"+
//            "where t.is_delete = 0  and datediff(t2.ENDDATE  , current_date()))")
//    List<StaffUser> getDocumentForDunning(@Param(value = "dateDiff") Integer dateDiff);


    List<StaffUser> findByStatusAndPartneridAndIsDeleteIsFalse(String status, Integer partnerid);

    List<StaffUser> findByUsername(String username);

    List<StaffUser> findByUsernameAndStatusAndIsDeleteIsFalse(String username, String status);

    @Query(value = "select * from tblmstaffuser t where t.username=:username And t.is_delete=false And t.sstatus='ACTIVE'", nativeQuery = true)
    StaffUser findUsername(@Param("username") String username);

    @Query(value = "select * from tblmstaffuser t where t.username=:username And t.is_delete=false", nativeQuery = true)
    StaffUser findStaffUserByUsername(@Param("username") String username);

    @Query(value = "SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser(t.id, t.password, t.email, t.phone, t.countryCode, t.mvnoId, t.businessUnit.id) FROM StaffUser t where t.username=:username And t.isDelete=false")
    StaffUser findStaffByUsername(@Param("username") String username);

    @Query(value = "select t4.* from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.partnerid = :s1 and t4.MVNOID = :mvnoId", nativeQuery = true, countQuery = "select count(*) from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.partnerid = :s1 and t4.MVNOID = :mvnoId")
    Page<StaffUser> findByPartneridAndIsDeleteIsFalse(@Param("s1") Integer PartnerId, Pageable pageable, @Param("mvnoId") Integer mvnoId);

    @Query(value = "SELECT * FROM tblmstaffuser WHERE username like '%' :searchText '%' ", nativeQuery = true)
    public List<StaffUser> findAllUsername(@Param("searchText") String searchText);

//    @Query("select t from StaffUser t where t.isDelete=false")
//    List<StaffUser> findAll();

    @Query("update StaffUser t set t.isDelete=true where t.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(nativeQuery = true, value = "select * from tblmstaffuser t \n" +
            "inner join tbltstaffrolerel t2 \n" +
            "on t2.roleid = :s1 and t2.staffid  = t.staffid \n" +
            "where t.is_delete = 0 and t.sstatus = 'Active'")
    List<StaffUser> findStaffByRole(@Param("s1") Long s1);

    @Query(nativeQuery = true, value = "select * from tblmstaffuser t \n" +
            "inner join tbltstaffrolerel t2 \n" +
            "on t2.roleid = :s1 and t2.staffid  = t.staffid \n" +
            "where t.is_delete = 0 and t.sstatus = 'Active' and t.partnerid = :s2")
    List<StaffUser> findStaffByRoleAndPartnerid(@Param("s1") Long s1, @Param("s2") Integer partnerid);


    @Query(value = "select t4.* from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.MVNOID in :mvnoIds and t4.MVNOID !=1 and t4.lcoid IS NULL"
            , countQuery = "select count(*) from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.MVNOID in :mvnoIds and t4.MVNOID !=1 and t4.lcoid IS NULL", nativeQuery = true)
    Page<StaffUser> findAll(Pageable pageable, @Param("mvnoIds") List<Integer> mvnoIds);

    @Query(value = "select t4.* from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.MVNOID in :mvnoIds and t4.MVNOID !=1 and and t4.lcoid=:lcoId"
            , countQuery = "select count(*) from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.MVNOID in :mvnoIds and t4.MVNOID !=1 and t4.lcoid=:lcoId", nativeQuery = true)
    Page<StaffUser> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds, @Param("lcoId") Integer lcoId);

    @Query(value = "select t4.* from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "left join \n" +
            "tbltstaffbusinessunitrel t5  on t5.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and (t4.MVNOID in :mvnoIds and t5.businessunitid in :buIds) and t4.lcoid IS NULL",
            countQuery = "select count(*) from tblmstaffuser t4 \n" +
                    "left join \n" +
                    "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
                    "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
                    "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
                    "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
                    "left join \n" +
                    "tbltstaffbusinessunitrel t5  on t5.staffid = t4.staffid \n" +
                    "where t4.is_delete = 0 and (t4.MVNOID in :mvnoIds and t5.businessunitid in :buIds) and t4.lcoid IS NULL",
            nativeQuery = true)
    Page<StaffUser> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds, @Param("buIds") List buIds);

    @Query(value = "select t4.* from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "left join \n" +
            "tbltstaffbusinessunitrel t5  on t5.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and (t4.MVNOID in :mvnoIds and t5.businessunitid in :buIds) and t4.lcoid=:lcoId",
            countQuery = "select count(*) from tblmstaffuser t4 \n" +
                    "left join \n" +
                    "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
                    "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
                    "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
                    "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
                    "left join \n" +
                    "tbltstaffbusinessunitrel t5  on t5.staffid = t4.staffid \n" +
                    "where t4.is_delete = 0 and (t4.MVNOID in :mvnoIds and t5.businessunitid in :buIds) and t4.lcoid=:lcoId",
            nativeQuery = true)
    Page<StaffUser> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds, @Param("buIds") List buIds, @Param("lcoId") Integer lcoId);

    @Query(value = "select t4.* from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.lcoid IS NULL", nativeQuery = true
            , countQuery = "select count(*) from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.lcoid IS NULL")
    Page<StaffUser> findAll(Pageable pageable);

    @Query(value = "select t4.* from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.lcoid=:lcoId", nativeQuery = true
            , countQuery = "select count(*) from tblmstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblmstaffuser t2\n" +
            "inner join tbltstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblmroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.lcoid=:lcoId")
    Page<StaffUser> findAll(Pageable pageable, @Param("lcoId") Integer lcoId);

    @Query(nativeQuery = true, value = "select * from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1) and t.lcoid IS NULL", countQuery = "select count(*) from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0  and (t.MVNOID in :mvnoId and t.MVNOID != 1) and t.lcoid IS NULL")
//(t.businessunitid is null or t.businessunitid in :buIds)
    Page<StaffUser> findAllByNameOrEmailOrRole(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("mvnoId") List mvnoId);
    //  Page<StaffUser> findAllByNameOrEmailOrRole(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("mvnoId") List mvnoId, @Param("buIds") List buIds);


    @Query(nativeQuery = true, value = "select * from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1) and t.lcoid=:lcoId", countQuery = "select count(*) from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0  and (t.MVNOID in :mvnoId and t.MVNOID != 1) and t.lcoid=:lcoId")
//(t.businessunitid is null or t.businessunitid in :buIds)
    Page<StaffUser> findAllByNameOrEmailOrRole(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("mvnoId") List mvnoId, @Param("lcoId") Integer lcoId);

    @Query(nativeQuery = true, value = "select t.* from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join tbltstaffbusinessunitrel t5\n" +
            "on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1 and t5.businessunitid in :buIds and t.lcoid IS NULL)", countQuery = "select count(*) from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join tbltstaffbusinessunitrel t5\n" +
            "on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1 and t5.businessunitid in :buIds) and t.lcoid IS NULL")
    Page<StaffUser> findAllByNameOrEmailOrRole(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("mvnoId") List mvnoId, @Param("buIds") List buIds);


    @Query(nativeQuery = true, value = "select t.* from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join tbltstaffbusinessunitrel t5\n" +
            "on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1 and t5.businessunitid in :buIds and t.lcoid=:lcoId)", countQuery = "select count(*) from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join tbltstaffbusinessunitrel t5\n" +
            "on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1 and t5.businessunitid in :buIds) and t.lcoid=:lcoId")
    Page<StaffUser> findAllByNameOrEmailOrRole(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("mvnoId") List mvnoId, @Param("buIds") List buIds, @Param("lcoId") Integer lcoId);

    @Query(nativeQuery = true, value = "select t.* from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join \n" +
            "tbltstaffbusinessunitrel t5  on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t5.businessunitid in :buIds and t.lcoid IS NULL", countQuery = "select count(*) from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join \n" +
            "tbltstaffbusinessunitrel t5  on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t5.businessunitid in :buIds and t.lcoid IS NULL")
    Page<StaffUser> findAllByNameOrEmailOrRoleByPartner(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("s6") Integer s6, @Param("mvnoId") List mvnoId, @Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select t.* from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join \n" +
            "tbltstaffbusinessunitrel t5  on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t5.businessunitid in :buIds and t.lcoid=:lcoId", countQuery = "select count(*) from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join \n" +
            "tbltstaffbusinessunitrel t5  on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t5.businessunitid in :buIds and t.lcoid=:lcoId")
    Page<StaffUser> findAllByNameOrEmailOrRoleByPartner(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("s6") Integer s6, @Param("mvnoId") List mvnoId, @Param("buIds") List buIds, @Param("lcoId") Integer lcoId);

    @Query(nativeQuery = true, value = "select * from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t.lcoid IS NULL", countQuery = "select count(*) from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t.lcoid IS NULL")
    Page<StaffUser> findAllByNameOrEmailOrRoleByPartner(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("s6") Integer s6, @Param("mvnoId") List mvnoId);

    @Query(nativeQuery = true, value = "select * from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t.lcoid=:lcoId", countQuery = "select count(*) from tblmstaffuser t \n" +
            "left join tbltstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblmroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t.lcoid=:lcoId")
    Page<StaffUser> findAllByNameOrEmailOrRoleByPartner(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("s6") Integer s6, @Param("mvnoId") List mvnoId, @Param("lcoId") Integer lcoId);


    @Query(value = "select * from tblmstaffuser where service_area_id=:id and sstatus='ACTIVE' and is_delete = false",
            countQuery = "select count(*) from tblmstaffuser where service_area_id=:id and sstatus='ACTIVE' and is_delete = false",
            nativeQuery = true)
    List<StaffUser> getByServiceAreaId(@Param("id") Integer id);

    @Query(value = "select * from tblmstaffuser t where t.is_delete = 0 and t.sstatus = 'ACTIVE' and (t.MVNOID in :MVNOIDS)",
            countQuery = "select count(*) tblmstaffuser t where t.is_delete = 0 and t.sstatus = 'ACTIVE' and (t.MVNOID in :MVNOIDS)",
            nativeQuery = true)
    List<StaffUser> findAllUsername(@Param("MVNOIDS") List MVNOIDS);

    @Query(value = "select * from tblmstaffuser t left join tbltstaffbusinessunitrel t2 on t.staffid = t2.staffid where t.is_delete = 0 and t.sstatus = 'ACTIVE' and (t.MVNOID in :MVNOIDS and t.MVNOID != 1) and (t2.businessunitid is null or t2.businessunitid in :buIds)",
            countQuery = "select count(*) from tblmstaffuser t left join tbltstaffbusinessunitrel t2 on t.staffid = t2.staffid where t.is_delete = 0 and t.sstatus = 'ACTIVE' and (t.MVNOID in :MVNOIDS and t.MVNOID != 1) and (t2.businessunitid is null or t2.businessunitid in :buIds)",
            nativeQuery = true)
    List<StaffUser> findAllUsername(@Param("MVNOIDS") List MVNOIDS, @Param("buIds") List buIds);

    List<StaffUser> findByIdAndStatusAndIsDeleteIsFalseAndMvnoIdIn(Integer id, String status, List mvnoIs);

    // List<StaffUser> findBystaffId(List<Integer> staffserviceidList);

    List<StaffUser> findByIdIn(List<Integer> staffserviceidList);

    //List<StaffUser> findByIdAndStatusAndIsDeleteIsFalse(Integer id, String status);
    List<StaffUser> findAllByStaffUserparent(StaffUser staffUser);

    @Query(value = "select count(*) from tblmstaffuser c where c.username=:username and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("username") String username);

    @Query(value = "select count(*) from tblmstaffuser c where c.username=:username and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("username") String username, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select t.staffid  from tbltstaffservicearearel t where t.serviceareaid =:serviceareaid", nativeQuery = true)
    List<Integer> findAllByServiceareaId(@Param("serviceareaid") Integer serviceareaid);

    List<StaffUser> findByIsDeleteIsFalseOrderByIdDesc();

    @Query(value = "select * from tblmstaffuser t where partnerid in :partnerids", nativeQuery = true)
    List<StaffUser> getAllStaffUserByPartnerIds(@Param("partnerids") List<Integer> partnerids);

    @Query(value = "select  c.id from StaffUser c where c.team=:teamname")
    List<Integer> findAllByTeamName(@Param("teamname") String teamname);

    @Query(value = "select s.firstname from StaffUser s where s.id=:staffid")
    String findNameById(@Param("staffid") Integer staffid);

    @Query(value = "select CONCAT(firstName, ' ', lastName) from tblmstaffuser where staffid= :staffId", nativeQuery = true)
    String findStaffFullNameById(@Param("staffId") Integer staffId);

    @Query(value = "select * from tblmstaffuser t where t.parent_staff_id =:parentStaffId", nativeQuery = true)
    List<StaffUser> findAllByParentStaffId(@Param("parentStaffId") Integer parentStaffId);


    Page<StaffUser> findAllByMvnoIdIn(Pageable pageableList, List<Integer> mvnoId);

//    List<StaffUser> findAllByTeam(Set<Teams> team);

    @Query(value = "select profile_image from tblmstaffuser t where t.staffid = :staffId", nativeQuery = true)
    Byte[] getProfileImageByStaffId(@Param("staffId") Integer staffId);

    StaffUser findByUsernameAndIsDeleteIsFalseAndMvnoId(String username, Integer mvnoId);

    List<StaffUser> findAllByIdIn(List<Integer> id);


    @Query(value = "select staffid from tblmstaffuser t where t.mvno_deactivation_flag =true and t.MVNOID=:MVNOID", nativeQuery = true)
    List<Integer> findStaffidByMvnoDeativationFlag(@Param("MVNOID") Integer MVNOID);

    Optional<StaffUser> findByUuid(String uuid);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO.StaffUserDropdownDTO(s.id, s.username, s.firstname, s.lastname) " +
            "FROM StaffUser s " +
            "WHERE s.isDelete = false AND LOWER(s.status) = LOWER(:status) AND s.mvnoId = :mvnoId " +
            "ORDER BY s.id DESC")
    List<StaffUserDropdownDTO> findAllStaffForDropdownByIsDeleteIsFalseAndStatusActiveOrderByIdDesc(@Param("status") String status, @Param("mvnoId") Integer mvnoId);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo( " +
            "s.id, s.username, s.password, s.firstname, s.lastname, " +
            "s.email, s.phone, s.countryCode, s.failcount, s.status, s.partnerid, " +
            "s.isDelete, s.sysstaff, s.tacacsAccessLevelGroup, s.mvnoId, " +
            "s.branchId, s.totalCollected, s.totalTransferred, s.availableAmount, s.lcoId, s.hrmsId, " +
            "s.profileImage, s.department, s.uuid, s.isPasswordExpired, s.passwordDate, " +
            "s.staffUserparent.id) " +
            "FROM StaffUser s " +
            "WHERE s.isDelete = false AND s.lcoId IS NULL AND s.mvnoId = :mvnoId " +
            "ORDER BY s.createdate DESC")
    List<StaffUserPojo> getFilteredStaffUsers(@Param("mvnoId") Integer mvnoId);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo( " +
            "s.id, s.username, s.password, s.firstname, s.lastname, " +
            "s.email, s.phone, s.countryCode, s.failcount, s.status, s.partnerid, " +
            "s.isDelete, s.sysstaff, s.tacacsAccessLevelGroup, s.mvnoId, " +
            "s.branchId, s.totalCollected, s.totalTransferred, s.availableAmount, s.lcoId, s.hrmsId, " +
            "s.profileImage, s.department, s.uuid, s.isPasswordExpired, s.passwordDate, " +
            "s.staffUserparent.id) " +
            "FROM StaffUser s " +
            "WHERE s.isDelete = false AND s.lcoId=:lcoId AND s.partnerid is null AND s.mvnoId = :mvnoId " +
            "ORDER BY s.createdate DESC")
    List<StaffUserPojo> getFilteredStaffUsersLCO(@Param("mvnoId") Integer mvnoId,@Param("lcoId") Integer lcoId);

    @Query("SELECT r.rolename FROM StaffRoleRel sr " +
            "JOIN Role r ON sr.roleId = r.id " +
            "WHERE sr.staffId = :staffId")
    List<String> getRolesByStaffId(@Param("staffId") Long staffId);


    @Query("SELECT sr.staffId, r.rolename FROM StaffRoleRel sr " +
            "JOIN Role r ON sr.roleId = r.id " +
            "WHERE sr.staffId IN :staffIds")
    List<Object[]> getRolesByStaffIds(@Param("staffIds") List<Long> staffIds);


    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(s.id, s.username, s.firstname, s.lastname) " +
            "FROM StaffUser s " +
            "WHERE s.status = :status AND s.isDelete = false")
    List<StaffUserPojo> findAllLightStaffUserPojoByStatusAndIsDeleteIsFalse(@Param("status") String status);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(s.id, s.username, s.firstname, s.lastname) " +
            "FROM StaffUser s " +
            "WHERE s.status = :status AND s.partnerid = :partnerid AND s.isDelete = false")
    List<StaffUserPojo> findAllLightStaffUserPojoByStatusAndPartneridAndIsDeleteIsFalse(
            @Param("status") String status,
            @Param("partnerid") Integer partnerid);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(s.id, s.username, s.firstname, s.lastname) " +
            "FROM StaffUser s " +
            "WHERE s.isDelete = false AND s.status = 'ACTIVE' AND s.mvnoId IN :mvnoIds")
    List<StaffUserPojo> findAllLightStaffUserPojoByMvnoId(@Param("mvnoIds") List<Integer> mvnoIds);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(s.id, s.username, s.firstname, s.lastname) " +
            "FROM StaffUser s " +
            "LEFT JOIN s.businessUnit bu " +
            "WHERE s.isDelete = false AND s.status = 'ACTIVE' " +
            "AND s.mvnoId IN :mvnoIds AND s.mvnoId != 1 " +
            "AND (bu.id IS NULL OR bu.id IN :buIds)")
    List<StaffUserPojo> findAllLightStaffUserByMvnoIdAndBuIds(@Param("mvnoIds") List<Integer> mvnoIds,
                                                              @Param("buIds") List<Long> buIds);

//    @Query(value = "select t.staffid  from tbltstaffservicearearel t where t.serviceareaid =:serviceareaid", nativeQuery = true)
//    List<Integer> findAllByServiceareaId(@Param("serviceareaid") Integer serviceareaid);

    @Query(value = "SELECT DISTINCT s.staffid, s.username, s.firstname, s.lastname " +
            "FROM tblmstaffuser s " +
            "LEFT JOIN tbltstaffservicearearel sa ON s.staffid = sa.staffid " +
            "WHERE s.is_delete = 0 " +
            "AND s.sstatus = 'ACTIVE' " +
            "AND s.MVNOID IN (:mvnoIds) " +
            //        "AND s.MVNOID != 1 " +
            "AND (sa.serviceareaid IS NULL OR sa.serviceareaid IN (:serviceAreaIds)) " +
            "ORDER BY s.staffid ASC",
            nativeQuery = true)
    List<Object[]> findAllLightStaffUserPojoByMvnoIdAndServiceAreaIds(@Param("mvnoIds") List<Integer> mvnoIds, @Param("serviceAreaIds") List<Long> serviceAreaIds);

    @Query(value = "SELECT DISTINCT s.staffid, s.username, s.firstname, s.lastname " +
            "FROM tblmstaffuser s " +
            "LEFT JOIN tbltstaffbusinessunitrel bu ON s.staffid = bu.staffid " +
            "LEFT JOIN tbltstaffservicearearel sa ON s.staffid = sa.staffid " +
            "WHERE s.is_delete = 0 " +
            "AND s.sstatus = 'ACTIVE' " +
            "AND s.MVNOID IN (:mvnoIds) " +
            "AND s.MVNOID != 1 " +
            "AND (sa.serviceareaid IS NULL OR sa.serviceareaid IN (:serviceAreaIds)) " +
            "AND (bu.id IS NULL OR bu.id IN (:buIds))",
            nativeQuery = true)
    List<Object[]> findAllLightStaffUserByMvnoIdAndBuIdsAndServiceAreaIds(@Param("mvnoIds") List<Integer> mvnoIds,
                                                                          @Param("buIds") List<Long> buIds, @Param("serviceAreaIds") List<Long> serviceAreaIds);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(s.id, s.firstname) " +
            "FROM StaffUser s " +
            "Where s.id in (:id)" +
            "AND s.isDelete = false AND s.status = 'ACTIVE' " +
            "AND s.mvnoId = :mvnoid ")
    List<StaffUserPojo> findAllLightStaffUsersByStaffIds(List<Integer> id, Integer mvnoid);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(s.id, s.firstname) " +
            "FROM StaffUser s " +
            "Where s.id in (:id)" +
            "and s.isDelete = false AND s.status = 'ACTIVE' ")
    List<StaffUserPojo> findAllLightStaffUsersByStaffIdsForSuperadmin(List<Integer> id);

    @Query(nativeQuery = true, value = "select t.username from tblmstaffuser t where t.staffid =:staffId")
    String findParentStaffNameById(@Param("staffId") Integer staffId);

    //    @Query(value = "select  new StaffUser(s)  from StaffUser s where s.id= :staffId")
//    StaffUser findByStaffUserId(Integer staffId);
    @Query(value = "SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(" +
            "s.id, s.username, s.firstname, s.lastname, s.email, s.phone, s.countryCode, s.status, s.partnerid, " +
            "s.isDelete, s.sysstaff, s.tacacsAccessLevelGroup, s.mvnoId, s.branchId, s.totalCollected, s.totalTransferred, " +
            "s.availableAmount, s.lcoId, s.hrmsId, s.profileImage, s.department, s.uuid, s.isPasswordExpired, " +
            "s.passwordDate,s.password, s.staffUserparent.id) " +
            "FROM StaffUser s " +
            "WHERE s.id = :staffId")
    StaffUserPojo findByStaffUserId(Integer staffId);


    @Query(value = "select new StaffUser (t.staffUserparent.id,t.staffUserparent.firstname,t.staffUserparent.lastname,t.staffUserparent.phone) from StaffUser t where t.id =:staffId")
    Optional<StaffUser> findParentStaffById(@Param("staffId") Integer staffId);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser(" +
            "t.id, t.username, t.firstname, t.lastname, t.email, t.phone, t.countryCode, t.status, " +
            "t.partnerid, t.isDelete, t.mvnoId, t.branchId, t.hrmsId, t.department, t.mvnoDeactivationFlag) " +
            "FROM StaffUser t WHERE t.id = :staffId")
    Optional<StaffUser> findStaffUserStaffById(@Param("staffId") Integer staffId);

//    @Query(value = "SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(m.id, concat(m.firstname, ' ',m.lastname)) " +
//            "FROM StaffUser m " +
//            "LEFT JOIN StaffUserServiceAreaMapping r " +
//            "ON m.id = r.staffId AND r.serviceId != :serviceAreaId " +
//            "WHERE r.serviceId IS null and m.status = 'Active' and m.mvnoId = :mvnoId")
//    List<StaffUserPojo> findStaffIdsWithoutServiceArea(@Param("serviceAreaId") Integer serviceAreaId, Integer mvnoId);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(m.id, CONCAT(m.firstname, ' ', m.lastname)) " +
            "FROM StaffUser m " +
            "WHERE m.status = 'Active' " +
            "AND m.mvnoId = :mvnoId " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM StaffUserServiceAreaMapping r " +
            "    WHERE r.staffId = m.id AND r.serviceId = :serviceAreaId" +
            ")")
    List<StaffUserPojo> findStaffIdsWithoutServiceArea(@Param("serviceAreaId") Integer serviceAreaId,
                                                       @Param("mvnoId") Integer mvnoId);

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserSearchDTO(" +
            "s.id, s.firstname, s.lastname, s.username, s.email, r.rolename, s.status) " +
            "FROM StaffUser s " +
            "LEFT JOIN StaffRoleRel rr ON rr.staffId = s.id " +
            "LEFT JOIN Role r ON rr.roleId = r.id " +
            "LEFT JOIN StaffUserBusinessUnitMapping m ON m.staffId = s.id " +
            "LEFT JOIN BusinessUnit bu ON bu.id = m.businessunitId " +
            "WHERE (" +
            "  LOWER(s.firstname) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(s.lastname) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(s.username) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(r.rolename) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(bu.buname) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") " +
            "AND s.isDelete = false " +
            "AND (" +
            "  :mvnoIds IS NULL OR (" +
            "    1 IN (:mvnoIds) OR (s.mvnoId IN :mvnoIds AND s.mvnoId != 1)" +
            "  )" +
            ") " +
            "AND (:isLco IS NULL OR (:isLco = true AND s.lcoId IS NOT NULL) OR (:isLco = false AND s.lcoId IS NULL)) " +
            "AND (:buIds IS NULL OR EXISTS (" +
            "   SELECT 1 FROM StaffUserBusinessUnitMapping m2 " +
            "   WHERE m2.staffId = s.id AND m2.businessunitId IN :buIds))")
    List<StaffUserSearchDTO> searchStaffUserByKeyword(
            @Param("keyword") String keyword,
            @Param("mvnoIds") List<Integer> mvnoIds,
            @Param("buIds") List<Long> buIds,
            @Param("isLco") Boolean isLco
    );

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserSearchDTO(" +
            "s.id, s.firstname, s.lastname, s.username, s.email, r.rolename, s.status) " +
            "FROM StaffUser s " +
            "LEFT JOIN StaffRoleRel rr ON rr.staffId = s.id " +
            "LEFT JOIN Role r ON rr.roleId = r.id " +
            "LEFT JOIN StaffUserBusinessUnitMapping m ON m.staffId = s.id " +
            "LEFT JOIN BusinessUnit bu ON bu.id = m.businessunitId " +
            "WHERE (" +
            "  LOWER(s.firstname) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(s.lastname) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(s.username) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(r.rolename) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(bu.buname) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") " +
            "AND s.isDelete = false " +
            "AND (" +
            "  :mvnoIds IS NULL OR (" +
            "    1 IN (:mvnoIds) OR (s.mvnoId IN :mvnoIds AND s.mvnoId != 1)" +
            "  )" +
            ") " +
            "AND (:buIds IS NULL OR EXISTS (" +
            "   SELECT 1 FROM StaffUserBusinessUnitMapping m2 " +
            "   WHERE m2.staffId = s.id AND m2.businessunitId IN :buIds)) " +
            "AND (:lco IS NULL OR (:lco = true AND s.lcoId = :partnerId) OR (:lco = false AND s.lcoId IS NULL)) " +
            "AND s.partnerid = :partnerId " +
            "ORDER BY s.createdate DESC")
    List<StaffUserSearchDTO> searchStaffUserByKeywordWithPartner(
            @Param("keyword") String keyword,
            @Param("mvnoIds") List<Integer> mvnoIds,
            @Param("buIds") List<Long> buIds,
            @Param("lco") Boolean lco,
            @Param("partnerId") int partnerId
    );

    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserPojo(s.id, s.username, s.password, s.firstname, s.lastname, " +
            "s.email, s.phone, s.countryCode, s.failcount, s.status, s.partnerid, " +
            "s.isDelete, s.sysstaff, s.tacacsAccessLevelGroup, s.mvnoId, " +
            "s.branchId, s.totalCollected, s.totalTransferred, s.availableAmount, s.lcoId, s.hrmsId, " +
            "s.profileImage, s.department, s.uuid, " +
            "s.isPasswordExpired, s.passwordDate, s.staffUserparent.id) " +
            "FROM StaffUser s " +
            "JOIN s.roles r " +
            "WHERE r.product = :product and s.department= :department")
    List<StaffUserPojo> findAllStaffUsersByDepartment(@Param("product") String product,@Param("department") Integer department);

    @Query(value = " SELECT \n" +
            "    t.NAME AS plan_name,\n" +

            "    -- t.offerprice,\n" +
            "    -- t.new_offer_price,\n" +
            "    \n" +
            "    -- Count active customers (debitdocument = active)\n" +
            "    COUNT(DISTINCT CASE \n" +
            "        WHEN t5.debitdocumentid IS NOT NULL THEN t3.CUSTID \n" +
            "        ELSE NULL \n" +
            "    END) AS active_customer_count,\n" +
            "\n" +
            "    -- Count new activation customers (trialdebitdocument = new activation)\n" +
            "    COUNT(DISTINCT CASE \n" +
            "        WHEN t8.trialdebitdocumentid IS NOT NULL THEN t3.CUSTID \n" +
            "        ELSE NULL \n" +
            "    END) AS new_activation_customer_count,\n" +
            "\n" +
            "    t.offerprice AS plan_price\n" +
            "FROM \n" +
            "    savbillcpm.tblmpostpaidplan t\n" +
            "\n" +
            "INNER JOIN savbillcpm.tblcustpackagerel t2 \n" +
            "    ON t.POSTPAIDPLANID = t2.planid and t.offerprice>0\n" +
            "\n" +
            "INNER JOIN savbillrevenuemanagement.tbltcreditdoc t3 \n" +
            "    ON t3.CUSTID = t2.custid AND t3.STATUS IN ('Approved', 'Fully Adjusted')\n" +
            "\n" +
            "INNER JOIN savbillrevenuemanagement.tbltcreditdebitmapping t4 \n" +
            "    ON t4.CREDITDOCID = t3.CREDITDOCID\n" +
            "\n" +
            "-- Active customer invoice\n" +
            "LEFT JOIN savbillrevenuemanagement.tbltdebitdocument t5 \n" +
            "    ON t5.debitdocumentid = t4.debitdocumentid \n" +
            "    AND t5.payment_status = 'Fully Paid'\n" +
            "\n" +
            "-- NewActivation invoice\n" +
            "LEFT JOIN savbillrevenuemanagement.tblttrialdebitdocument t8 \n" +
            "    ON t8.trialdebitdocumentid = t4.trialdebitdocumentid \n" +
            "    AND t8.paymentStatus = 'Fully Paid'\n" +
            "\n" +
            "INNER JOIN savbillcpm.tblstaffuser t6 \n" +
            "    ON t6.staffid = :staffId AND t.CREATEDBYSTAFFID = :staffId\n" +
            "\n" +
            "INNER JOIN tblcustomers t7 \n" +
            "    ON t7.custid = t3.CUSTID\n" +
            "\n" +
            "WHERE \n" +
            "    t7.createdate BETWEEN :startDate AND :endDate \n" +
            "\n" +
            "GROUP BY \n" +
            "    t.POSTPAIDPLANID, t.NAME, t6.firstname;",
            nativeQuery = true)
    List<Object[]> getTotalSoldPlanCount(@Param("staffId") Integer staffId, @Param("startDate")Timestamp startDate, @Param("endDate")Timestamp endDate);




    @Query("SELECT new com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.CommonDTO.StaffUserDropdownDTO(s.id, s.username, s.firstname, s.lastname) " +
            "FROM StaffUser s " +
            "WHERE s.isDelete = false AND " +
            "s.status ='Active'AND " +
            "s.staffUserparent.id = :staffId " +
            "ORDER BY s.id DESC")
    List<StaffUserDropdownDTO> findAllChildStaffForDropdownByIsDeleteIsFalseAndStatusActiveOrderByIdDesc(@Param("staffId") Integer staffId);

}
