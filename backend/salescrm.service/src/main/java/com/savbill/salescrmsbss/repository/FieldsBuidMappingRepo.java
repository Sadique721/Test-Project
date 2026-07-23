package com.savbill.salescrmsbss.repository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.FieldsBuidMapping;

import java.util.List;
import java.util.Set;

@JaversSpringDataAuditable
@Repository
public interface FieldsBuidMappingRepo extends JpaRepository<FieldsBuidMapping,Long> , QuerydslPredicateExecutor<FieldsBuidMapping> {
/*

    @Query(value = "select t.field_id, t.is_mandatory, t.screen, t.module FROM tbltfieldsBuidMapping t where t.is_deleted=false and t.buid in :buid",nativeQuery = true)
    Set<FieldsBuidMapping> getFields(@Param("buid")List<Long> buid);
*/

    @Query(value = "select * FROM tbltfieldsBuidMapping t where t.is_deleted=false and t.screen=:screen and t.buid in :buid",nativeQuery = true)
    List<FieldsBuidMapping> getFields(@Param("screen") Long screen, @Param("buid")List<Long> buid);


    @Query(value = "select * FROM tbltfieldsBuidMapping t where t.is_deleted=false and t.screen=:screen and t.buid is null",nativeQuery = true)
    Set<FieldsBuidMapping> getFields(@Param("screen") Long screen);

    @Query(value = "select * FROM tbltfieldsBuidMapping t where t.is_deleted=false",nativeQuery = true)
    Set<FieldsBuidMapping> getAll();

    List<FieldsBuidMapping> findAllByBuid(Long id);

    @Query(value = "select * FROM tbltfieldsBuidMapping t where t.is_deleted=false and t.screen= 1 and t.buid is null",nativeQuery = true)
    List<FieldsBuidMapping> findAllByNullBuids();

    List<FieldsBuidMapping> findAllByScreen(String name);

    List<FieldsBuidMapping> findAllByserviceParamIdIn(List<Long> serviceparamIdList);
    
    @Query(value = "select * FROM tbltfieldsBuidMapping t where t.is_deleted=false and t.screen=:screen and t.field_id in :field_id and t.buid is null",nativeQuery = true)
    List<FieldsBuidMapping> getFieldsAndBuidIsNull(@Param("screen") Long screen, @Param("field_id")List<Long> field_id);
}
