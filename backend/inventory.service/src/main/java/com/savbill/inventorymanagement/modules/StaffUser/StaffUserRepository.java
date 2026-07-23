package com.savbill.inventorymanagement.modules.StaffUser;

import com.savbill.inventorymanagement.modules.MasterManagement.BusinessUnit.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffUserRepository extends JpaRepository<StaffUser, Integer>, QuerydslPredicateExecutor<StaffUser> {

    Optional<StaffUser> findByUsername(String username);
    List<StaffUser> findByIdAndStatusAndIsDeleteIsFalseAndMvnoIdIn(Integer id, String status, List<Integer> mvnoId);
    List<StaffUser> findByIdInAndStatusAndIsDeleteIsFalseAndMvnoIdIn(List<Integer> id, String status, List<Integer> mvnoId);
    List<StaffUser> findByIdAndIsDeleteIsFalseAndMvnoIdIn(Integer id, List<Integer> mvnoId);
    @Query("SELECT s.partnerid FROM StaffUser s WHERE s.isDelete = false AND s.id = :staffId AND s.mvnoId IN :mvnoIds")
    Integer findPartnerIdByStaffIdAndMvnoIdIn(@Param("staffId") Integer staffId,
                                              @Param("mvnoIds") List<Integer> mvnoIds);

    List<StaffUser> findAllByIdAndIsDeleteIsFalse(Integer id);
    @Query("SELECT s.partnerid FROM StaffUser s WHERE s.isDelete = false AND s.id = :staffId")
    Integer findPartnerIdByStaffId(@Param("staffId") Integer staffId);

//    StaffUser findById(Integer id);

    List<StaffUser> findAllByIsDeleteIsFalse();
    List<StaffUser> findAllByIsDeleteIsFalseAndMvnoIdIn(List<Integer> mvnoId);
    List<StaffUser> findAllByIsDeleteIsFalseAndMvnoIdInAndIdIn(List<Integer> mvnoId, List<Integer> id);
    List<StaffUser> findAllByIsDeleteIsFalseAndMvnoIdInAndId(List<Integer> mvnoId, Integer id);
    List<StaffUser> findAllByStatusAndIsDeleteIsFalse(String status);
    List<StaffUser> findAllByStatusAndPartneridAndIsDeleteIsFalse(String status, Integer partnerid);
    List<StaffUser> findAllByStatusAndPartneridAndIsDeleteIsFalseAndMvnoIdIn(String status, Integer partnerid, List<Integer> mvnoId);
    List<StaffUser> findAllByStatusAndIsDeleteIsFalseAndMvnoIdIn(String status, List<Integer> mvnoId);
    List<StaffUser> findAllByStatusAndBusinessUnitNameListInAndIsDeleteIsFalseAndMvnoIdIn(String status, List<BusinessUnit> businessUnit, List<Integer> mvnoId);

    @Query("SELECT s.id FROM StaffUser s WHERE s.status = :status AND s.isDelete = false " +
            "AND s.businessUnitNameList IN :businessUnit AND s.mvnoId IN :mvnoId")
    List<Integer> findIdsByStatusAndBusinessUnitNameListInAndIsDeleteFalseAndMvnoIdIn(
            @Param("status") String status,
            @Param("businessUnit") List<BusinessUnit> businessUnit,
            @Param("mvnoId") List<Integer> mvnoId);

    List<StaffUser> findAllByStatusAndPartneridAndBusinessUnitNameListInAndIsDeleteIsFalseAndMvnoIdIn(String status, Integer partnerid, List<BusinessUnit> businessUnitNameList, List<Integer> mvnoId);

    @Query("SELECT s.id FROM StaffUser s WHERE s.status = :status AND s.partnerid = :partnerid " +
            "AND s.isDelete = false AND s.businessUnitNameList IN :businessUnitNameList " +
            "AND s.mvnoId IN :mvnoId")
    List<Integer> findIdsByStatusAndPartnerIdAndBusinessUnitNameListInAndIsDeleteFalseAndMvnoIdIn(
            @Param("status") String status,
            @Param("partnerid") Integer partnerid,
            @Param("businessUnitNameList") List<BusinessUnit> businessUnitNameList,
            @Param("mvnoId") List<Integer> mvnoId);

    List<StaffUser> findAllByIdInAndStatusAndIsDeleteIsFalse(List<Integer> id, String status);
    List<StaffUser> findAllByIdInAndStatusAndIsDeleteIsFalseAndMvnoIdIn(List<Integer> id, String status, List<Integer> mvnoId);

    List<StaffUser> findByUsernameAndStatusAndIsDeleteIsFalse(String username, String status);


    @Query("select new StaffUser(s.id,s.username,s.password,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s where s.status=:status")
    List<StaffUser> findAllLightStaffUserByStatusAndIsDeleteIsFalse(String status);

    @Query("select s.id from StaffUser s where s.status = :status and s.isDelete = false")
    List<Integer> findAllStaffIdsByStatusAndIsDeleteFalse(@Param("status") String status);

    @Query("select s.id from StaffUser s where s.status=:status")
    List<Integer> findAllIdsByStatus(String status);

    @Query("select new StaffUser(s.id,s.username,s.password,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s where s.status=:status and s.partnerid =:partnerid")
    List<StaffUser> findAllLightStaffUserByStatusAndPartneridAndIsDeleteIsFalse(String status, Integer partnerid);

    @Query("select s.id from StaffUser s where s.status=:status and s.partnerid =:partnerid")
    List<Integer> findAllStaffIdsByStatusAndPartneridAndIsDeleteIsFalse(String status, Integer partnerid);

    @Query("select s.id from StaffUser s where s.status=:status and s.partnerid =:partnerid")
    List<Integer> findAllIdsByStatusAndPartnerId(String status, Integer partnerid);


    @Query("select new StaffUser(s.id,s.username,s.password,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s where s.status=:status and s.mvnoId IN :mvnoIds")
    List<StaffUser> findAllLightStaffByStatusAndIsDeleteIsFalseAndMvnoIdIn(String status, List<Integer> mvnoIds);

    @Query("select s.id from StaffUser s where s.status=:status and s.mvnoId IN :mvnoIds")
    List<Integer> findAllStaffIdsByStatusAndIsDeleteIsFalseAndMvnoIdIn(String status, List<Integer> mvnoIds);

    @Query("select s.id from StaffUser s where s.status=:status and s.mvnoId IN :mvnoIds")
    List<Integer> findAllIdsByStatusAndMvnoIds(String status, List<Integer> mvnoIds);


    @Query("select new StaffUser(s.id,s.username,s.password,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s where s.status=:status and s.partnerid =:partnerid and s.mvnoId IN :mvnoIds")
    List<StaffUser> findAllLightStaffByStatusAndPartneridAndIsDeleteIsFalseAndMvnoIdIn(String status, Integer partnerid, List<Integer> mvnoId);

    @Query("select s.id from StaffUser s where s.status=:status and s.partnerid =:partnerid and s.mvnoId IN :mvnoIds")
    List<Integer> findAllStaffIdsByStatusAndPartneridAndIsDeleteIsFalseAndMvnoIdIn(String status, Integer partnerid, List<Integer> mvnoId);

    @Query("select s.id from StaffUser s where s.status=:status and s.partnerid =:partnerid and s.mvnoId IN :mvnoIds")
    List<Integer> findAllIdsByStatusAndPartnerIdAndMvnoIds(String status, Integer partnerid, List<Integer> mvnoId);


    @Query("select new StaffUser(s.id,s.username,s.password,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s where s.id IN :ids")
    List<StaffUser> findAllLightStaffById(List<Integer> ids);

    @Query("select new com.savbill.inventorymanagement.modules.StaffUser.StaffUserPojo(s.id,s.username,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s where s.id IN :ids")
    List<StaffUserPojo> findAllLightStaffUserPojoById(List<Integer> ids);

    @Query("select new StaffUser(s.id,s.username,s.password,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s WHERE s.isDelete = false")
    List<StaffUser> findAllByIsDeleteIsFalseWithSpecificParameter();

    @Query("select new StaffUser(s.id,s.username,s.password,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s WHERE s.isDelete = false AND s.mvnoId IN (:mvnoId) AND s.id IN (:id) ")
    List<StaffUser> findAllByIsDeleteIsFalseAndMvnoIdInAndIdInWithSpecificParameter(List<Integer> mvnoId, List<Integer> id);

    @Query("select new StaffUser(s.id,s.username,s.password,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s WHERE s.isDelete = false AND s.mvnoId IN (:mvnoId) ")
    List<StaffUser> findAllByIsDeleteIsFalseAndMvnoIdInWithSpecificParameter(List<Integer> mvnoId);

    @Query("select new StaffUser(s.id,s.username,s.password,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s WHERE s.isDelete = false AND s.mvnoId IN (:mvnoId) AND s.id =:id ")
    List<StaffUser> findAllByIdIsDeleteIsFalseAndMvnoIdInWithSpecificParameter(Integer id ,List<Integer> mvnoId);

    @Query("SELECT s.id FROM StaffUser s WHERE s.isDelete = false AND s.mvnoId IN :mvnoIds AND s.id = :loggedInUserId")
    List<Long> findStaffIdsByIsDeleteFalseAndMvnoIdIn(@Param("loggedInUserId") Integer loggedInUserId, @Param("mvnoIds") List<Integer> mvnoIds);


    @Query("select new StaffUser(s.id,s.username,s.password,s.firstname,s.lastname,s.status,s.partnerid) from StaffUser s WHERE s.id =:id ")
    List<StaffUser> findAllByIdWithSpecificParameter(Integer id);

    @Query("SELECT s.id FROM StaffUser s WHERE s.id = :id")
    List<Long> findStaffIdsById(@Param("id") Integer id);

    @Query("SELECT new StaffUser( " +
            "s.id, s.username, s.password, s.firstname, s.lastname, s.status, s.partnerid) " +
            "FROM StaffUser s " +
            "WHERE s.id = :id")
    Optional<StaffUser> findLightStaffUserById(@Param("id") Integer id);

    @Query(value = "SELECT s.email FROM StaffUser s WHERE s.isDelete = false and s.id = :id")
    String findEmailByUserId(@Param("id") Integer id);

    @Query(value = "SELECT s.firstname FROM StaffUser s WHERE s.isDelete = false and s.id = :id")
    String findFirstNameByUserId(@Param("id") Integer id);

    @Query(value = "SELECT s.phone FROM StaffUser s WHERE s.isDelete = false and s.id = :id")
    String findPhoneByUserId(@Param("id") Integer id);

    @Query(value = "SELECT s.partnerid FROM StaffUser s WHERE s.isDelete = false and s.id = :id")
    Integer findPartnerIdByUserId(@Param("id") Integer id);
}
