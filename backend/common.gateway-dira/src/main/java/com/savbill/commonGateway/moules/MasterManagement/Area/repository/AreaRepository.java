package com.savbill.commonGateway.moules.MasterManagement.Area.repository;


import com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area;
import com.savbill.commonGateway.moules.MasterManagement.Area.model.NewAreaDto;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface AreaRepository extends JpaRepository<Area, Long>, QuerydslPredicateExecutor<Area> {

    Page<Area> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);

    Page<Area> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area(t.id, t.name, t.status, t.countryId, t.cityId, t.stateId, t.pincode.id, t.mvnoId) from Area t WHERE t.isDeleted = false")
    Page<Area> findAll(Pageable pageable);

//    @Query(value = "select t from Area t where t.isDeleted=false and mvnoId in :mvnoIds")
//    Page<Area> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

//    @Query(value = "SELECT t FROM Area t WHERE t.isDeleted = false and t.mvnoId in (:mvnoIds)",
//            countQuery = "SELECT COUNT(t) FROM Area t WHERE t.isDeleted = false and t.mvnoId in (:mvnoIds)")
//    Page<Area> findAllByIsDeletedFalseAndMvnoIdIn(List mvnoIds, Pageable pageable);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area(a.id, a.name, a.status, a.countryId, a.cityId, a.stateId, a.pincode.id, a.mvnoId) " +
            "FROM Area a WHERE a.isDeleted = false AND a.mvnoId IN (:mvnoIds)")
    Page<Area> findAllByMvnoIds(@Param("mvnoIds") List mvnoIds, Pageable pageable);


    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area(a.id, a.name, a.status, a.countryId, a.cityId, a.stateId, a.pincode.id, a.mvnoId) " +
            "FROM Area a WHERE a.id = :id")
    Area findAreaById(Long id);

    @Query(value = "SELECT COUNT(*) FROM tblmarea WHERE is_deleted = false AND MVNOID IN (:mvnoIds)", nativeQuery = true)
    long countByMvnoIds(@Param("mvnoIds") List mvnoIds);

    @Query(value = "select sum(tbl.tab) from(\n" +
            /*
    "select count(*) as tab from tblmsubscriberaddressrel t4 where t4.AREAID =:id and t4.is_delete =false \n" +
    "union all\n" + */
    "select count(*) as tab from tblmservicearea t2 where t2.areaid =:id and t2.is_deleted =false \n" +
    ")tbl",nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);
    
    @Query(value = "select count(*) from tblmarea where name=:name and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmarea where name=:name and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmarea where name=:name and areaid =:id and  countryid=:countryId and  stateid = :stateId and cityid=:cityId and pincodeid = :pincodeId and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("countryId") Integer countryId,@Param("stateId") Integer stateId,@Param("cityId") Integer cityId, @Param("pincodeId") Integer pincodeId,@Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmarea where name=:name and areaid =:id and countryid=:countryId and  stateid = :stateId and cityid=:cityId and pincodeid = :pincodeId and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id,@Param("countryId") Integer countryId,@Param("stateId") Integer stateId,@Param("cityId") Integer cityId, @Param("pincodeId") Integer pincodeId);

    @Query(value = "select * from tblmarea where pincodeid = :pincodeId and is_deleted=false", nativeQuery = true)
    List<Area> findAreasByPincode(@Param("pincodeId")Long pincodeId);

    @Query(value = "select count(*) from tblmarea where name=:name and countryid=:countryId and  stateid = :stateId and cityid=:cityId and pincodeid = :pincodeId and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name,@Param("countryId") Integer countryId,@Param("stateId") Integer stateId,@Param("cityId") Integer cityId, @Param("pincodeId") Integer pincodeId);

    @Query(value = "select count(*) from tblmarea where name=:name and countryid=:countryId and  stateid = :stateId and cityid=:cityId and pincodeid = :pincodeId and is_deleted=false  and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name,@Param("countryId") Integer countryId,@Param("stateId") Integer stateId,@Param("cityId") Integer cityId, @Param("pincodeId") Integer pincodeId,  @Param("mvnoIds") List mvnoIds);

    List<Area> findAllByIsDeletedIsFalseAndPincode_Id(Long pincode_id);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area(a.id, a.name, a.mvnoId) " +
            "FROM Area a WHERE a.isDeleted = false AND a.pincode.id = :pincode_id AND a.mvnoId IN (:mvnoId)")
    List<Area> findAllByIsDeletedIsFalseAndPincodeIdAndMvnoIdIn(Long pincode_id, List<Integer> mvnoId);

    @Query(value = "select areaid from tblmarea where name=:name and is_deleted=false", nativeQuery = true)
    Integer findByNameAndIsDeletedFalse(String name);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Area.model.NewAreaDto(a.id, a.name) " +
            "FROM Area a WHERE a.isDeleted = false order by id desc")
    List<NewAreaDto> getIdAndName();

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Area.model.NewAreaDto(a.id, a.name) " +
            "FROM Area a WHERE a.isDeleted = false and a.mvnoId in :mvnoIds order by id desc")
    List<NewAreaDto> getIdAndNameByMvnoIds(@Param("mvnoIds")List mvnoIds);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Area.model.NewAreaDto" +
            "(a.id, a.name, a.countryId, a.cityId, a.stateId, a.pincode.id) " +
            "FROM Area a WHERE a.isDeleted = false ORDER BY a.id DESC")
    List<NewAreaDto> findAllAreas();

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Area.model.NewAreaDto" +
            "(a.id, a.name, a.countryId, a.cityId, a.stateId, a.pincode.id) " +
            "FROM Area a WHERE a.isDeleted = false AND a.mvnoId IN :mvnoIds ORDER BY a.id DESC")
    List<NewAreaDto> findAreasByMvnoIds(@Param("mvnoIds") List<Integer> mvnoIds);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area(" +
            "a.id, a.name, a.status, a.countryId, a.cityId, a.stateId, a.pincode.id, a.mvnoId) " +
            "FROM Area a " +
            "WHERE a.isDeleted = false " +
            "ORDER BY a.id DESC")
    Page<Area> findAllByAreas(Pageable pageable);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area(" +
            "a.id, a.name, a.status, a.countryId, a.cityId, a.stateId, a.pincode.id, a.mvnoId) " +
            "FROM Area a " +
            "WHERE a.isDeleted = false AND a.mvnoId IN :mvnoIds " +
            "ORDER BY a.id DESC")
    Page<Area> findAllAreaByMvnoIds(@Param("mvnoIds") List<Integer> mvnoIds, Pageable pageable);

    @Query("SELECT a.pincode.id " +
            "FROM Area a " +
            "WHERE a.id = :areaId " +
            "AND a.isDeleted = false")
    Long findPincodeIdByAreaId(@Param("areaId") Long areaId);



}
