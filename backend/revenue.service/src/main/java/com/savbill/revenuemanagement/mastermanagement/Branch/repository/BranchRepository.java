package com.savbill.revenuemanagement.mastermanagement.Branch.repository;

import com.savbill.revenuemanagement.mastermanagement.Branch.domain.Branch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository  extends JpaRepository<Branch, Long> {
	
 	@Query(value = "SELECT * from tblmbranch t WHERE t.is_deleted = false"
            , nativeQuery = true
            , countQuery = "SELECT count(*) from tblmbranch t WHERE t.is_deleted = false")
    Page<Branch> findAll(Pageable pageable);

    @Query(value = "select * from tblmbranch t where t.is_deleted = false and MVNOID in :mvnoIds", nativeQuery = true)
    Page<Branch> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmbranch m where m.name=:name and m.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbranch m where m.name=:name and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);

    @Query(value = "select count(*) from tblmbranch where name=:name and branchid =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmbranch where name=:name and branchid =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);

    Page<Branch> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);

    Page<Branch> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

    @Query(value = "select count(*) as tab from tblstaffuser t1  where t1.branchid =:id " ,nativeQuery = true)
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

}
