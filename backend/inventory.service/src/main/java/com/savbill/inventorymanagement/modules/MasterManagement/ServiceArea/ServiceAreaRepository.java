package com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAreaRepository extends JpaRepository<ServiceArea, Long>, QuerydslPredicateExecutor<ServiceArea> {
     List<ServiceArea> findAllByIdInAndStatusAndIsDeletedIsFalse(List<Long> result,String Status);
     List<ServiceArea> findAllByIdInAndStatusAndIsDeletedIsFalseAndMvnoIdIn(List<Long> id, String status, List<Integer> mvnoId);
     List<ServiceArea> findAllByIdInAndIsDeletedIsFalseAndMvnoIdIn(List<Long> id, List<Integer> mvnoId);
     List<ServiceArea> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId);
     List<ServiceArea> findAllByIdIn(List<Long> id);
     List<ServiceArea> findAllByStatusAndIsDeletedIsFalse(String Status);
     List<ServiceArea> findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(String status, List<Integer> mvnoId);

     @Query("SELECT new ServiceArea( " +
             "s.id, s.name) " +
             "FROM ServiceArea s " +
             "WHERE s.isDeleted = false " +
             "AND s.id IN :ids " +
             "AND s.mvnoId IN :mvnoIds")
     List<ServiceArea> findAllLightServiceAreaByIdInAndIsDeletedIsFalseAndMvnoIdIn(
             @Param("ids") List<Long> ids,
             @Param("mvnoIds") List<Integer> mvnoIds);

     @Query("SELECT new ServiceArea( " +
             "s.id, s.name) " +
             "FROM ServiceArea s " +
             "WHERE s.isDeleted = false " +
             "AND s.id =:id")
     ServiceArea findAllLightServiceAreaById(@Param("id") Long id);

     @Query("select s.name from ServiceArea s where s.id= :id ")
     String findServiceAreaNameById(@Param("id") Long id);

}
