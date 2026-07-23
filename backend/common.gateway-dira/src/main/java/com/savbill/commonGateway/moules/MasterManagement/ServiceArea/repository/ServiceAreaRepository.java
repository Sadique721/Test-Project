package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository;


import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LightServiceAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaCommonDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO;
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
import java.util.Set;
import java.util.function.Predicate;

@Repository
@JaversSpringDataAuditable
public interface ServiceAreaRepository extends JpaRepository<ServiceArea, Long>, QuerydslPredicateExecutor<ServiceArea> {
    ServiceArea findByName(String serviceName);

    @Query(value = "SELECT * from tblmservicearea t WHERE t.is_deleted = false"
            , nativeQuery = true
            , countQuery = "SELECT count(*) from tblmservicearea t WHERE t.is_deleted = false")
    Page<ServiceArea> findAll(Pageable pageable);

    @Query(value = "select * from tblmservicearea t where t.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Page<ServiceArea> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

//    @Query(value = "select * from tblservicearea t where t.name like '%' :s1 '%' and t.is_deleted  = 0", nativeQuery = true
//            , countQuery = "select count(*) from tblservicearea t where t.name like '%' :s1 '%' and t.is_deleted  = 0")
//    Page<ServiceArea> findAllByNameAndIsDeletedIsFalse(@Param("s1") String s1, Pageable pageable);

    @Query(value = "select * from tblmservicearea t WHERE t.is_deleted = false and t.service_area_id NOT IN :ids", nativeQuery = true)
    List<ServiceArea> findAllByIdOut(@Param("ids") List<Long> ids);

    @Query(value = "select * from tblmservicearea t \n" +
            "where t.service_area_id not in (\n" +
            "select t.service_area_id from tblmservicearea t \n" +
            "inner join tblcasereasonconfig t2 \n" +
            "on t2.serviceareaid = t.service_area_id\n" +
            "inner join tblcasereasons t3 \n" +
            "on t3.reason_id = t2.reasonid \n" +
            "where t3.reason_id = :s1 )\n" +
            "and t.is_deleted = 0  and MVNOID in :mvnoIds", nativeQuery = true)
    List<ServiceArea> findAllServiceArea(@Param("s1") Long s1, @Param("mvnoIds") List mvnoIds);

//    @Query(value = "select sum(tbl.tab) from(\n" +
//            "select count(*) as tab from tblcustomers t2 where t2.servicearea_id =:id and t2.is_deleted =false\n" +
//            "union all\n" +
//            "select count(*) as tab from tblnetworkdevices t where t.servicearea_id =:id and t.is_deleted =false \n" +
//            ")tbl;",nativeQuery = true)
//    Integer deleteVerify(@Param("id") Integer id);

    @Query(value = "select count(*) from tblmservicearea m where m.name=:name and m.is_deleted=false and MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmservicearea m where m.name=:name and m.service_area_id !=:id and  m.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmservicearea m where m.name=:name and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);

    @Query(value = "select count(*) from tblmservicearea m where m.name=:name and m.service_area_id !=:id and  m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    List<ServiceArea> findAllByIdIn(List<Long> result);

    List<ServiceArea> findAllByIdInAndStatusAndIsDeletedIsFalse(List<Long> result,String Status);

    @Query("select  new  ServiceArea(c) from ServiceArea c WHERE c.id in :ids" )
    List<ServiceArea> getServiceAreaByServiceAreaId( @Param("ids") List<Long> ids );

    List<ServiceArea> findAllByIsDeletedIsFalseAndStatus(String status);

    @Query(value = "select * from tblmservicearea t where t.is_deleted=false and t.status =:status", nativeQuery = true)
    Page<ServiceArea> findAllByIsDeletedIsFalseAndStatus(Pageable pageable, String status);
    List<ServiceArea> findAllByIsDeletedIsFalseAndStatusAndMvnoIdIn(String status, List<Integer> mvnoId);

    @Query(value = "select * from tblmservicearea t where t.is_deleted=false and t.status =:status and t.MVNOID in :mvnoId", nativeQuery = true)
    Page<ServiceArea> findAllByIsDeletedIsFalseAndStatusAndMvnoIdIn(Pageable pageable, String status, List<Integer> mvnoId);
    List<ServiceArea> findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(String status, List<Integer> mvnoId, List<Long> id);

    @Query(value = "select * from tblmservicearea t where t.is_deleted=false and t.status =:status and t.MVNOID in :mvnoId and t.service_area_id in :id", nativeQuery = true)
    Page<ServiceArea> findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(Pageable pageable, String status, List<Integer> mvnoId, List<Long> id);

    List<ServiceArea> findAllByIdInAndStatusAndIsDeletedIsFalseAndMvnoIdIn(List<Long> id, String status, List<Integer> mvnoId);

    @Query(value = "SELECT * FROM tblmservicearea t WHERE t.is_deleted = false AND t.MVNOID=:mvnoId", nativeQuery = true)
    List<ServiceArea> findAllByMvnoId(@Param("mvnoId") Integer mvnoId);

//    @Query("select ServiceArea(sa.id, sa.mvnoId, sa.latitude, sa.longitude, sa.radius) from ServiceArea sa where sa.isDeleted=:false and sa.radius is not null")
//    List<ServiceArea> findAllByLatitudeAndLongitude();

//    @Query("select sa.id, sa.mvnoId, sa.latitude, sa.longitude, sa.radius from ServiceArea sa where sa.isDeleted=:false and sa.radius is not null")
//    List<ServiceArea> findAllByLatitudeAndLongitude();

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO(sa.id, sa.name, sa.latitude, sa.longitude, sa.mvnoId, sa.radius) FROM ServiceArea sa WHERE sa.isDeleted = false AND sa.radius IS NOT NULL AND sa.latitude IS NOT NULL AND sa.longitude IS NOT NULL")
    List<ServiceAreaDTO> findAllByLatitudeAndLongitude();

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO(sa.id, sa.name, sa.latitude, sa.longitude, sa.mvnoId, sa.radius) FROM ServiceArea sa WHERE sa.isDeleted = false AND sa.mvnoId=:mvnoId AND sa.radius IS NOT NULL AND sa.latitude IS NOT NULL AND sa.longitude IS NOT NULL")
    List<ServiceAreaDTO> findAllByLatitudeAndLongitudeAndMvno(Integer mvnoId);


    @Query(value = "SELECT name FROM tblmservicearea t WHERE t.is_deleted = false AND t.service_area_id=:serviceAreaId", nativeQuery = true)
    String getNameByServieAreaId(Integer serviceAreaId);

    Boolean existsBySiteNameAndMvnoId(String siteName, Integer mvnoId);

    @Query(value = "SELECT t.site_name " +
            "FROM tblmservicearea t " +
            "WHERE t.MVNOID = 1 " +
            "AND t.site_name NOT IN (" +
            "    SELECT s.site_name " +
            "    FROM tblmservicearea s " +
            "    WHERE s.MVNOID = :mvnoId " +
            "    AND s.site_name IS NOT NULL)",
            nativeQuery = true)
    List<String> findsiteNameBymvnoId(Integer mvnoId);


    List<ServiceArea> findAllBySiteNameAndMvnoIdInAndIsDeletedFalse(String siteName, List<Integer> mvnoId);

    List<ServiceArea> findAllByIsDeletedIsFalseAndStatusIn(List<String> status);

    List<ServiceArea> findAllByIsDeletedIsFalseAndStatusInAndMvnoIdIn(List<String> status, List<Integer> mvnoId);

//    List<ServiceArea> findAllByIsDeletedIsFalseAndStatusInAndMvnoIdInAndIdIn(List<String> status, List<Integer> mvnoId, List<Long> id);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea(s.id, s.name, s.status, s.mvnoId, s.latitude, s.longitude, s.serviceAreaType) " +
            "FROM ServiceArea s " +
            "WHERE s.isDeleted = false " +
            "AND s.status IN :statuses " +
            "AND s.mvnoId IN :mvnoIds " +
            "AND s.id IN :ids")
    List<ServiceArea> findAllByIsDeletedIsFalseAndStatusInAndMvnoIdInAndIdIn(
            @Param("statuses") List<String> statuses,
            @Param("mvnoIds") List<Integer> mvnoIds,
            @Param("ids") List<Long> ids);

    Page<ServiceArea> findAllByIsDeletedIsFalseAndStatusIn(List<String> status, Pageable pageable);

    Page<ServiceArea> findAllByIsDeletedIsFalseAndStatusInAndMvnoIdIn(List<String> status, List<Integer> mvnoId, Pageable pageable);

    Page<ServiceArea> findAllByIsDeletedIsFalseAndStatusInAndMvnoIdInAndIdIn(List<String> status, List<Integer> mvnoId, List<Long> id, Pageable pageable);

    @Query("select c.id from ServiceArea c WHERE c.siteName =:siteName and c.mvnoId != 1" )
    List<Long> findServiceAreaIdsFromSiteName(String siteName);

    ServiceArea findByIdAndStatusIn(Long key, List<String> list);

    Optional<ServiceArea> findAllByNameAndMvnoIdAndIsDeletedIsFalse(String name, Integer mvnoId);

    List<ServiceArea> findAllByNameAndIsDeletedIsFalse(String name);

    @Query("SELECT new ServiceArea(sa.id, sa.name, sa.mvnoId, sa.latitude, sa.longitude, sa.radius) FROM ServiceArea sa WHERE sa.id IN :ids")
    List<ServiceArea> getLightServiceAreaFromIds(List<Long> ids);

    @Query("SELECT new ServiceArea(sa.id, sa.name, sa.mvnoId, sa.latitude, sa.longitude, sa.radius) FROM ServiceArea sa")
    List<ServiceArea> getAllLightServiceAreaFromIds();

    @Query("SELECT new ServiceArea(sa.id, sa.name, sa.mvnoId, sa.latitude, sa.longitude, sa.radius) " +
            "FROM ServiceArea sa " +
            "WHERE sa.id IN :result AND sa.status = :status AND sa.isDeleted = false")
    List<ServiceArea> findLightServiceAreas(@Param("result") List<Long> result, @Param("status") String status);

    @Query("SELECT new ServiceArea(sa.id, sa.name, sa.mvnoId, sa.latitude, sa.longitude, sa.radius) " +
            "FROM ServiceArea sa " +
            "WHERE sa.id IN :ids AND mvnoId IN :mvnoIds AND sa.status = :status AND sa.isDeleted = false ")
    List<ServiceArea> findLightServiceAreasWithMvnoIds(@Param("ids") List<Long> ids,
                                            @Param("status") String status,
                                            @Param("mvnoIds") List<Integer> mvnoIds);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LightServiceAreaDTO(s.id, s.name, s.createdById,s.status) " +
            "FROM ServiceArea s WHERE s.isDeleted = false AND s.status = :status")
    List<LightServiceAreaDTO> findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatus(String status);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LightServiceAreaDTO(s.id, s.name, s.createdById,s.status) " +
            "FROM ServiceArea s " +
            "WHERE s.isDeleted = false AND s.status = :status AND s.mvnoId IN :mvnoId")
    List<LightServiceAreaDTO> findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusAndMvnoIdIn(@Param("status") String status, @Param("mvnoId") List<Integer> mvnoId);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LightServiceAreaDTO(s.id, s.name, s.createdById,s.status) " +
            "FROM ServiceArea s " +
            "WHERE s.isDeleted = false AND s.status = :status " +
            "AND s.mvnoId IN :mvnoId AND s.id IN :id")
    List<LightServiceAreaDTO> findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusAndMvnoIdInAndIdIn(
            @Param("status") String status,
            @Param("mvnoId") List<Integer> mvnoId,
            @Param("id") List<Long> id);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LightServiceAreaDTO(s.id, s.name, s.createdById,s.status) " +
            "FROM ServiceArea s " +
            "WHERE s.isDeleted = false AND s.status IN :statuses")
    List<LightServiceAreaDTO> findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusIn(@Param("statuses") List<String> statuses);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LightServiceAreaDTO(s.id, s.name, s.createdById,s.status) " +
            "FROM ServiceArea s " +
            "WHERE s.isDeleted = false AND s.status IN :statuses AND s.mvnoId IN :mvnoIds")
    List<LightServiceAreaDTO> findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusInAndMvnoIdIn(
            @Param("statuses") List<String> statuses,
            @Param("mvnoIds") List<Integer> mvnoIds);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.LightServiceAreaDTO(s.id, s.name, s.createdById,s.status) " +
            "FROM ServiceArea s " +
            "WHERE s.isDeleted = false " +
            "AND s.status IN :statuses " +
            "AND s.mvnoId IN :mvnoIds " +
            "AND s.id IN :ids")
    List<LightServiceAreaDTO> findAllServiceAreaForDropdownByIsDeletedIsFalseAndStatusInAndMvnoIdInAndIdIn(
            @Param("statuses") List<String> statuses,
            @Param("mvnoIds") List<Integer> mvnoIds,
            @Param("ids") List<Long> ids);

    @Query("SELECT new ServiceArea(sa.id, sa.name) FROM ServiceArea sa WHERE sa.id IN :ids")
    List<ServiceArea> findServiceAreaIdAndNameByIdsIn(List<Long> ids);
    @Query("SELECT sa.name  FROM ServiceArea sa WHERE sa.mvnoId= :mvnoId")
    List<String>findServiceAreaNameByMvnoId(Integer mvnoId);

    @Query("SELECT sa.id  FROM ServiceArea sa WHERE sa.mvnoId= :mvnoId")
    List<Long>findServiceAreaIdByMvnoId(Integer mvnoId);

    @Query("SELECT sa.name  FROM ServiceArea sa WHERE sa.id in (:serviceareaid)")
    List<String>findServiceAreaNameByServiceareaId(List<Long> serviceareaid);

    @Query("SELECT DISTINCT s FROM ServiceArea s " +
            "LEFT JOIN FETCH s.polyGoneList " +
            "WHERE s.id = :id")
    ServiceArea findByServiceAreaId(@Param("id") Long id);

    @Query("SELECT new ServiceArea(sa.id, sa.name, sa.mvnoId, sa.latitude, sa.longitude, sa.radius) FROM ServiceArea sa WHERE sa.id IN (:serviceAreaIds)")
    List<ServiceArea> getLightServiceAreaFromId(List<Integer> serviceAreaIds);

    @Query("SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO(" +
            "s.id, s.name, s.siteName, s.status, s.isDeleted, s.mvnoId, s.latitude, s.longitude, " +
            "s.areaId, s.cityid, s.radius, s.serviceAreaType, s.blockNo, s.mvnoLists) " +
            "FROM ServiceArea s WHERE s.id = :id")
    ServiceAreaDTO findDTOById(@Param("id") Long id);


    @Query(
            "SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaCommonDTO(" +
                    "s.id, s.name, s.siteName, s.status, s.isDeleted, s.mvnoId, s.latitude, s.longitude, " +
                    "s.areaId, s.cityid, s.radius, s.serviceAreaType, s.blockNo, s.mvnoLists, " +
                    "s.createdate, s.updatedate, s.createdByName, s.lastModifiedByName, s.createdById, s.lastModifiedById) " +
                    "FROM ServiceArea s " +
                    "WHERE s.isDeleted = false " +
                    "AND s.status IN :statuses "
    )
    Page<ServiceAreaCommonDTO> findAllDTOByStatuses(
            @Param("statuses") List<String> statuses,
            Pageable pageable
    );


    @Query(
            "SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaCommonDTO(" +
                    "s.id, s.name, s.siteName, s.status, s.isDeleted, s.mvnoId, s.latitude, s.longitude, " +
                    "s.areaId, s.cityid, s.radius, s.serviceAreaType, s.blockNo, s.mvnoLists, " +
                    "s.createdate, s.updatedate, s.createdByName, s.lastModifiedByName, s.createdById, s.lastModifiedById) " +
                    "FROM ServiceArea s " +
                    "WHERE s.isDeleted = false " +
                    "AND s.status IN :statuses " +
                    "AND s.mvnoId IN :mvnoIds"
    )
    Page<ServiceAreaCommonDTO> findAllDTOByStatusesAndMvnoIds(
            @Param("statuses") List<String> statuses,
            @Param("mvnoIds") List<Integer> mvnoIds,
            Pageable pageable
    );

    @Query(
            "SELECT new com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaCommonDTO(" +
                    "s.id, s.name, s.siteName, s.status, s.isDeleted, s.mvnoId, s.latitude, s.longitude, " +
                    "s.areaId, s.cityid, s.radius, s.serviceAreaType, s.blockNo, s.mvnoLists, " +
                    "s.createdate, s.updatedate, s.createdByName, s.lastModifiedByName, s.createdById, s.lastModifiedById) " +
                    "FROM ServiceArea s " +
                    "WHERE s.isDeleted = false " +
                    "AND s.status IN :statuses " +
                    "AND s.mvnoId IN :mvnoIds " +
                    "AND s.id IN :serviceAreaIds"
    )
    Page<ServiceAreaCommonDTO> findAllDTOByStatusesMvnoIdsAndIds(
            @Param("statuses") List<String> statuses,
            @Param("mvnoIds") List<Integer> mvnoIds,
            @Param("serviceAreaIds") List<Long> serviceAreaIds,
            Pageable pageable
    );







}
