//package com.savbill.revenuemanagement.productmanagement.qosPolicy.repository;
//
//
//import com.savbill.revenuemanagement.productmanagement.qosPolicy.domain.QOSPolicy;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.querydsl.QuerydslPredicateExecutor;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface QOSPolicyRepository extends JpaRepository<QOSPolicy, Long > {
//
//    Page<QOSPolicy> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoId(String name, Pageable pageable,Integer mvnoId);
//
//    @Query("SELECT t from QOSPolicy t WHERE t.isDeleted = false")
//    Page<QOSPolicy> findAll(Pageable pageable);
//
//    @Query("SELECT t from QOSPolicy t WHERE t.isDeleted = false and t.mvnoId in :mvnoIds")
//    Page<QOSPolicy> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);
//
//    @Query("SELECT t from QOSPolicy t WHERE t.isDeleted = false and (t.mvnoId = 1 or (t.mvnoId  = :mvnoId and t.buId in :buIds))")
//    Page<QOSPolicy> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);
//
//    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
//    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);
//
//    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))",nativeQuery = true)
//    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);
//
//    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.id=:id and  m.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
//    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);
//
//    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.id=:id and  m.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
//    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);
//
//    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.is_deleted=false",nativeQuery = true)
//    Integer duplicateVerifyAtSave(@Param("name")String name);
//
//    @Query(value = "select count(*) from tbl_qos_policy m where m.name=:name and m.id=:id and  m.is_deleted=false", nativeQuery = true)
//    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);
//
//    @Query(value = "select count(*) from tblcustpackagerel where qospolicyid =:id",nativeQuery = true)
//    Integer deleteVerify(@Param("id")Integer id);
//
//    Optional<QOSPolicy> findById(Long id);
//
//}
