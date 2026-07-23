package com.savbill.salescrmsbss.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.savbill.salescrmsbss.entity.ScreenFieldMapping;

@Repository
public interface ScreenFieldMappingRepository extends JpaRepository<ScreenFieldMapping,Long> , QuerydslPredicateExecutor<ScreenFieldMapping> {

    @Query(value = "select * FROM tbltscreenfieldsmapping t where t.screenid =:screen",nativeQuery = true)
    List<ScreenFieldMapping> getFields(@Param("screen") Long screen);

    @Query(value = "select * from tbltscreenfieldsmapping t2 where t2.screenid =:screenid and parentfieldid is null",nativeQuery = true)
    List<ScreenFieldMapping> findAllByScreen(@Param("screenid") Long screenid);

    @Query(value = "select * FROM tbltscreenfieldsmapping t where t.parentfieldid =:id and t.screenid =:screenid ",nativeQuery = true)
    List<ScreenFieldMapping> findAllByParentfields(@Param("id") Long id,@Param("screenid")Long screenid);

    List<ScreenFieldMapping> findAllByParentfieldsId(Long parentFieldId);
    List<ScreenFieldMapping> findAllByFieldsIdInAndScreensId(List<Long> fieldsIdList,Long ScreenId);

    ScreenFieldMapping findByFieldsIdAndScreensId(Long fieldId, Long ScreenId);

}
