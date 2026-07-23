package com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@JaversSpringDataAuditable
public interface PopManagementRepository extends JpaRepository<PopManagement, Long>, QuerydslPredicateExecutor<PopManagement> {
    // find duplicate pop name at save
    @Query(value = "select count(*) from tblmpopmanagement t where t.pop_name =:popname and t.is_deleted =false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("popname")String popname);

    // find duplicate pop name at save with mvnoId
    @Query(value = "select count(*) from tblmpopmanagement t where t.pop_name =:popname and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("popname")String popname, @Param("mvnoIds") List mvnoids);

    //Find duplicate pop name at edit
    @Query(value = "select count(*) from tblmpopmanagement t where t.pop_id =:popid and t.pop_name =:popname and t.is_deleted =false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("popname")String popname, @Param("popid") Integer popid);

    // Find duplicate pop name at edit with mvnoId
    @Query(value = "select count(*) from tblmpopmanagement t where t.pop_id =:popid and  t.pop_name =:popname and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("popname")String popname, @Param("popid") Integer popid, @Param("mvnoIds") List mvnoids);

    //Pop ID verify at Delete
//    @Query(value = "select count(*) as tab from tblmpopmanagement t  where t.pop_id =:popid" ,nativeQuery = true)
//    Integer deleteVerify(@Param("popid")Integer popid);

    Long countByNameAndIsDeletedIsFalse(String name);

    Long countByNameAndIsDeletedIsFalseAndMvnoIdIn(String name, List<Integer> mvnoId);

    Long countByNameAndIdAndIsDeletedIsFalse(String name, Long id);

    Long countByNameAndIdAndIsDeletedIsFalseAndMvnoIdIn(String name, Long id, List<Integer> mvnoId);
    List<PopManagement> findAllByStatusAndIsDeletedIsFalse(String status);
    List<PopManagement> findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(String status, List<Integer> mvnoId);
    List<PopManagement> findAllByStatusAndIdInAndIsDeletedIsFalse(String status, List<Long> id);
    List<PopManagement> findAllByStatusAndIdInAndIsDeletedIsFalseAndMvnoIdIn(String status, List<Long> id,List<Integer> mvnoId);

    Page<PopManagement> findAllByIsDeletedIsFalse(Pageable pageable);
    Page<PopManagement> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId, Pageable pageable);
    Page<PopManagement> findAllByIdInAndIsDeletedIsFalse(List<Long> id, Pageable pageable);
    Page<PopManagement> findAllByIdInAndIsDeletedIsFalseAndMvnoIdIn(List<Long> id,List<Integer> mvnoId, Pageable pageable);

    Page<PopManagement> findAllByIsDeletedIsFalseAndNameContainingIgnoreCase(Pageable pageable, String name);
    Page<PopManagement> findAllByIsDeletedIsFalseAndMvnoIdInAndNameContainingIgnoreCase(List<Integer> mvnoId, Pageable pageable, String name);
    Page<PopManagement> findAllByIdInAndIsDeletedIsFalseAndNameContainingIgnoreCase(List<Long> id, Pageable pageable, String name);
    Page<PopManagement> findAllByIdInAndIsDeletedIsFalseAndMvnoIdInAndNameContainingIgnoreCase(List<Long> id,List<Integer> mvnoId, Pageable pageable, String name);
    List<PopManagement> findAllByIsDeletedIsFalse();
    List<PopManagement> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId);
    List<PopManagement> findAllByIsDeletedIsFalseAndMvnoIdInAndIdIn(List<Integer> mvnoId, List<Long> id);

    @Query("SELECT new PopManagement(p.id, p.name) FROM PopManagement p WHERE p.isDeleted = false")
    List<PopManagement> findAllLightPopManagementByIsDeletedIsFalse();

    @Query("SELECT new PopManagement(p.id, p.name) FROM PopManagement p WHERE p.isDeleted = false AND p.mvnoId IN :mvnoIds AND p.id IN :ids")
    List<PopManagement> findAllLightPopManagementByIsDeletedIsFalseAndMvnoIdInAndIdIn(@Param("mvnoIds") List<Integer> mvnoIds, @Param("ids") List<Long> ids);

    @Query("SELECT new PopManagement(p.id, p.name) FROM PopManagement p WHERE p.isDeleted = false AND p.mvnoId IN :mvnoIds")
    List<PopManagement> findAllLightPopManagementByIsDeletedIsFalseAndMvnoIdIn(@Param("mvnoIds") List<Integer> mvnoIds);

    @Query("SELECT new PopManagement(p.id, p.name) FROM PopManagement p WHERE p.id =:id")
    PopManagement findLightPopManagementById(Long id);

    @Query("SELECT p.name FROM PopManagement p WHERE p.id =:id")
    Optional<String> findNameById(Long id);
}
