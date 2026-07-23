package com.savbill.inventorymanagement.modules.PartnerManagement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface PartnerRepository extends JpaRepository<Partner, Integer>, QuerydslPredicateExecutor<Partner> {
    List<Partner> findByStatusAndIsDeleteIsFalse(String status);
    Page<Partner> findAllByIdInAndStatusAndIsDeleteIsFalse(List<Integer> id, String status, Pageable pageable);
    Page<Partner> findAllByIdInAndStatusAndIsDeleteIsFalseAndMvnoIdIn(List<Integer> id, String status, List<Integer> mvnoId, Pageable pageable);

    List<Partner> findAllByIdInAndStatusAndIsDeleteIsFalse(List<Integer> id, String status);
    List<Partner> findAllByIdInAndStatusAndIsDeleteIsFalseAndMvnoIdIn(List<Integer> id, String status, List<Integer> mvnoId);
    List<Partner> findAllByIdInAndIsDeleteIsFalseAndMvnoIdIn(List<Integer> id, List<Integer> mvnoId);
    List<Partner> findAllByIsDeleteIsFalseAndMvnoIdIn(List<Integer> mvnoId);
    List<Partner> findAllByIsDeleteIsFalse();

    @Query("SELECT new Partner(p.id, p.name) " +
            "FROM Partner p " +
            "WHERE p.isDelete = false " +
            "AND p.id IN :ids " +
            "AND p.mvnoId IN :mvnoIds")
    List<Partner> findAllLightPartnerByIdInAndIsDeleteIsFalseAndMvnoIdIn(
            @Param("ids") List<Integer> ids,
            @Param("mvnoIds") List<Integer> mvnoIds);

    @Query("SELECT new Partner(p.id, p.name) " +
            "FROM Partner p " +
            "WHERE p.isDelete = false " +
            "AND p.id =:id ")
    Partner findAllLightPartnerById(
            @Param("id") Integer id);

    @Query("SELECT p.id FROM Partner p WHERE p.isDelete = false AND p.id = :partnerId AND LOWER(p.status) = LOWER(:status)")
    List<Long> findActivePartnerIds(@Param("partnerId") Integer partnerId,
                                    @Param("status") String status);

}
