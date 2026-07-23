package com.savbill.commonGateway.moules.MasterManagement.Branch.repository;


import com.savbill.commonGateway.moules.MasterManagement.Branch.domain.Branch;
import com.savbill.commonGateway.moules.MasterManagement.Branch.model.BranchIdNameDTO;
import com.savbill.commonGateway.moules.MasterManagement.Branch.model.CustomBranchDTO;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.BranchNameProjection;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface BranchRepository  extends JpaRepository<Branch, Long>, QuerydslPredicateExecutor<Branch> {
	
 	@Query(value = "SELECT * from tblmbranch t WHERE t.is_deleted = false"
            , nativeQuery = true
            , countQuery = "SELECT count(*) from tblmbranch t WHERE t.is_deleted = false")
    Page<Branch> findAll(Pageable pageable);

//    @Query(value = "select * from tblmbranch t where t.is_deleted = false and MVNOID in :mvnoIds", nativeQuery = true)
    @Query("SELECT new Branch(b.id, b.name, b.status, b.mvnoId, b.isDeleted, b.dunningDays) FROM Branch b WHERE b.isDeleted = false AND b.mvnoId IN (:mvnoIds) ORDER BY b.id DESC")
    Page<Branch> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmbranch m where m.name=:name and m.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbranch m where m.branch_code=:code and m.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer findBranchCountFromBranchCode(@Param("code")String code, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbranch m where m.name=:name and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);

    @Query(value = "select count(*) from tblmbranch m where m.branch_code=:code and m.is_deleted=false",nativeQuery = true)
    Integer findBranchCountFromBranchCode(@Param("code")String code);

    @Query(value = "select count(*) from tblmbranch where name=:name and branchid =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmbranch where branch_code=:code and branchid =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer findBranchCountFromBranchCodeAndIdAndMvnoId(@Param("code") String code, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmbranch where name=:name and branchid =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);

    @Query(value = "select count(*) from tblmbranch where branch_code=:code and branchid =:id and is_deleted=false", nativeQuery = true)
    Integer findBranchCountFromBranchCodeAndId(@Param("code") String code, @Param("id") Long id);

//    Page<Branch> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);
    @Query("SELECT new Branch(b.id, b.name, b.status, b.mvnoId, b.isDeleted, b.dunningDays) " +
        "FROM Branch b " +
        "WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
        "AND b.isDeleted = false")
    Page<Branch> findBranchByNameFiltered(
        @Param("name") String name,
        Pageable pageable
    );
//    Page<Branch> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);
    @Query("SELECT new Branch(b.id, b.name, b.status, b.mvnoId, b.isDeleted, b.dunningDays) " +
        "FROM Branch b " +
        "WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
        "AND b.isDeleted = false " +
        "AND b.mvnoId IN :mvnoIds")
    Page<Branch> findBranchByNameFiltered(
        @Param("name") String name,
        Pageable pageable,
        @Param("mvnoIds") List<Integer> mvnoIds
    );
    @Query(value = "select count(*) as tab from tblmstaffuser t1  where t1.branchid =:id " ,nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);

    @Query(value = "select count(*) as tab from tbltregionbranchmapping t1  where t1.branchid =:id " ,nativeQuery = true)
    Integer deleteVerifyForRegion(@Param("id")Integer id);

    Branch findByIdAndIsDeletedIsFalse(Long id);

    List<Branch> findAllByIdIn(List<Long> result);

    List<Branch> findAllByStatusAndIsDeletedFalseAndIdIn(String Status,List<Long> result);

    List<Branch> findAllById(Long id);

   Branch findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(String name);

    @Query(value = "select t.name from tblmbranch t where t.branchid in :branchids",nativeQuery = true)
    List<String> getAllBranchNamesByBranchIds(@Param("branchids") List<Long> branchids);

    @Query("SELECT new Branch(b.id , b.name , b.status) FROM Branch b where b.isDeleted = false order by id desc")
    Page<Branch> findAllProjectedBranch(Pageable pageable);

    @Query("SELECT new Branch(b.id, b.name, b.status) FROM Branch b WHERE b.id IN :ids AND b.isDeleted = false ORDER BY b.id DESC")
    List<Branch> findAllByIdInWithSpecificParam(@Param("ids") List<Long> ids);

    @Query("SELECT b FROM Branch b WHERE b.id IN :branchIds AND b.status = :status AND b.isDeleted = false")
    List<Branch> findActiveBranches(@Param("branchIds") Set<Long> branchIds, @Param("status") String status);

    @Query(value = "select t.name from tblmbranch t where t.branchid =:branchId",nativeQuery = true)
    String findNameById (@Param("branchId") Long branchId);

    @Query("SELECT new Branch(b.id, b.name, b.status) FROM Branch b WHERE b.id IN :branchIds AND b.status = :status AND b.isDeleted = false")
    List<Branch> findAllActiveBranches(@Param("branchIds") Set<Long> branchIds, @Param("status") String status);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Branch.model.BranchIdNameDTO(b.id, b.name) " +
            "FROM Branch b " +
            "WHERE b.status = :status AND b.isDeleted = false AND b.id IN :branchIds")
    List<BranchIdNameDTO> findIdAndNameByStatusAndIsDeletedFalseAndIdIn(
            @Param("status") String status,
            @Param("branchIds") List<Long> branchIds);

    List<Branch> findByIsDeletedFalseAndMvnoIdIn(List<Integer> mvnoIds);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Branch.model.CustomBranchDTO(" +
            "b.id, b.name, b.status, b.branch_code, b.mvnoId, b.isDeleted, b.revenue_sharing, " +
            "b.sharing_percentage, b.dunningDays, b.createdate, b.updatedate, " +
            "b.createdById, b.lastModifiedById, b.createdByName, b.lastModifiedByName) " +
            "FROM Branch b " +
            "WHERE b.isDeleted = false AND b.mvnoId IN :mvnoIds")
    List<CustomBranchDTO> findBranchesBasic(@Param("mvnoIds") List<Integer> mvnoIds);

    @Query(value = "SELECT rel.branchid AS branchId, sa.service_area_id AS serviceAreaId, sa.name AS serviceAreaName " +
            "FROM tbltbranchservicearearel rel " +
            "LEFT JOIN tblmservicearea sa ON sa.service_area_id = rel.servicearea_id " +
            "WHERE rel.branchid IN (:branchIds)", nativeQuery = true)
    List<Object[]> findBranchServiceAreas(@Param("branchIds") List<Long> branchIds);

    @Query("SELECT b.name AS name FROM Branch b WHERE b.id = :id")
    BranchNameProjection findBranchNameById(@Param("id") Long id);
}
