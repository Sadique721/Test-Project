package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository;

import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
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
public interface NetworkDeviceRepository extends JpaRepository<NetworkDevices, Long>, QuerydslPredicateExecutor<NetworkDevices> {

    @Query(value = "SELECT nd.* FROM tblmnetworkdevices nd " +
            "JOIN tbltnetworkdevicesservicearearel rel ON nd.deviceid = rel.deviceid " +
            "WHERE rel.serviceareaid = :serviceId AND nd.is_deleted = false", nativeQuery = true)
    List<NetworkDevices> findByServiceareaIdAndIsDeletedIsFalse(@Param("serviceId") Long serviceId);

    List<NetworkDevices> findByNameAndDevicetypeAndIsDeletedIsFalse(String networkDeviceName, String deviceType);

    @Query(value = "select * from tblmnetworkdevices t\n" +
            "left join tblmservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where  t.is_deleted = 0", countQuery = "select count(*) from tblmnetworkdevices t\n" +
            "left join tblmservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where  t.is_deleted = 0", nativeQuery = true)
    Page<NetworkDevices> findAll(Pageable pageable);

    @Query(value = "select * from tblmnetworkdevices t\n" +
            "left join tblmservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where  t.is_deleted = 0 and t.MVNOID in :mvnoIds", countQuery = "select count(*) from tblmnetworkdevices t\n" +
            "left join tblmservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where  t.is_deleted = 0 and t.MVNOID in :mvnoIds", nativeQuery = true)
    Page<NetworkDevices> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from tblmnetworkdevices t\n" +
            "left join tblmservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where (t.name like '%' :s1 '%' or t.devicetype like '%' :s2 '%' or t2.name like '%' :s3 '%') and  t.is_deleted = 0"
            , countQuery = "select count(*) from tblmnetworkdevices t\n" +
            "left join tblmservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where (t.name like '%' :s1 '%' or t.devicetype like '%' :s2 '%' or t2.name like '%' :s3 '%') and  t.is_deleted = 0")
    Page<NetworkDevices> findAllByNameContainingIgnoreCaseOrDevicetypeContainingIgnoreCaseOrServicearea_NameContainingIgnoreCase(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tblmnetworkdevices t\n" +
            "left join tblmservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where (t.name like '%' :s1 '%' or t.devicetype like '%' :s2 '%' or t2.name like '%' :s3 '%') and  t.is_deleted = 0 AND t.MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblmnetworkdevices t\n" +
            "left join tblmservicearea t2 \n" +
            "on t2.service_area_id = t.servicearea_id \n" +
            "where (t.name like '%' :s1 '%' or t.devicetype like '%' :s2 '%' or t2.name like '%' :s3 '%') and  t.is_deleted = 0 AND t.MVNOID in :mvnoIds;")
    Page<NetworkDevices> findAllByNameContainingIgnoreCaseOrDevicetypeContainingIgnoreCaseOrServicearea_NameContainingIgnoreCaseAndMvnoIdIn(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblmoltslots t3 where t3.deviceid =:id and t3.is_deleted =false \n" +
            "union all\n" +
            "select count(*) as tab from tblmoltportdetails t where t.deviceid =:id and t.is_deleted =false\n" +
            "union all \n" +
            "select count(*) as tab from tblmcustomers t2 where t2.network_device_id =:id and t2.is_deleted =false\n" +
            ")tbl", nativeQuery = true)
    Integer deleteVerify(@Param("id") Integer id);

    @Query(value = "select count(*) from tblmnetworkdevices m where m.name=:name and m.is_deleted=false and m.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmnetworkdevices m where m.name=:name and m.deviceid !=:id and  m.is_deleted=false and m.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmnetworkdevices m where m.name=:name and m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmnetworkdevices m where m.name=:name and m.deviceid !=:id and  m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select * from tblmnetworkdevices where devicetype='Splitter' and is_deleted=false", nativeQuery = true)
    List<NetworkDevices> getAllSplitters();

    @Query(value = "select * from tblmnetworkdevices where parent_network_device_id=:parentId and is_deleted=false", nativeQuery = true)
    List<NetworkDevices> getByNetworkDeviceParentId(Long parentId);

    NetworkDevices findByIdAndMvnoIdIn(Long id, List<Integer> mvnoIds);

    NetworkDevices findByCustInventoryId(Long id);

    NetworkDevices findByInventorymappingId(Long id);

    NetworkDevices findByItemIdAndIsDeletedFalse(Long itemId);

    @Query("SELECT nd.id FROM NetworkDevices nd WHERE nd.itemId = :itemId AND nd.isDeleted = false AND nd.custInventoryId = :custInventoryId")
    Long findDeviceIdBYItemIdAndCustInventoryId(@Param("itemId") Long itemId, @Param("custInventoryId") Long custInventoryId);

    NetworkDevices findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(Long itemId, Long custInventoryId);
    List<NetworkDevices> findAllByIsDeletedFalseAndDevicetypeAndStatus(String deviceType, String status);

    List<NetworkDevices> findAllByIsDeletedFalseAndDevicetypeAndMvnoIdInAndStatus(String deviceType,List<Integer> mvnoId,String status);

    @Query(value = "select * from tblmnetworkdevices where devicetype like '%Splitter%' and  is_deleted=false;", nativeQuery = true)
    List<NetworkDevices> getAllSplittersByTypeSplitter();

    @Query(value = "SELECT * FROM tblmnetworkdevices " +
            "WHERE is_deleted = false AND devicetype = :deviceType " +
            "AND displayname LIKE %:displayName%",
            nativeQuery = true)
    Page<NetworkDevices> findAllNativeByDeviceTypeAndDisplayNameLike(
            @Param("deviceType") String deviceType,
            @Param("displayName") String displayName,
            Pageable pageable);

    @Query(value = "SELECT * FROM tblmnetworkdevices " +
            "WHERE is_deleted = false AND mvnoid IN (:mvnoIdList) " +
            "AND devicetype = :deviceType AND displayname LIKE %:displayName%",
            nativeQuery = true)
    Page<NetworkDevices> findAllNativeByMvnoIdInAndDeviceTypeAndDisplayNameLike(
            @Param("mvnoIdList") List<Integer> mvnoIdList,
            @Param("deviceType") String deviceType,
            @Param("displayName") String displayName,
            Pageable pageable);

    @Query(value = "SELECT * FROM tblmnetworkdevices " +
            "WHERE is_deleted = false AND displayname LIKE %:displayName%",
            nativeQuery = true)
    Page<NetworkDevices> findAllNativeByDisplayNameLike(
            @Param("displayName") String displayName,
            Pageable pageable);

    @Query(value = "SELECT * FROM tblmnetworkdevices " +
            "WHERE is_deleted = false AND mvnoid IN (:mvnoIdList) " +
            "AND displayname LIKE %:displayName%",
            nativeQuery = true)
    Page<NetworkDevices> findAllNativeByMvnoIdInAndDisplayNameLike(
            @Param("mvnoIdList") List<Integer> mvnoIdList,
            @Param("displayName") String displayName,
            Pageable pageable);

    @Query(value = "SELECT devicetype FROM NetworkDevices WHERE id = :id and isDeleted = false")
    String findDeviceTypeById(@Param("id") Long id);

    @Query(value = "SELECT product_id FROM tblmnetworkdevices WHERE deviceid = :deviceid and is_deleted = false", nativeQuery = true)
    Long findProductIdById(@Param("deviceid") Long deviceid);

    @Query("SELECT new NetworkDevices(n.id, n.custInventoryId, n.inventorymappingId, n.itemId) FROM NetworkDevices n WHERE n.id = :id AND n.isDeleted = false")
    NetworkDevices findDetailsById(@Param("id") Long id);

    @Query("SELECT new NetworkDevices(nd.id, nd.name, nd.displayname, " +
            "nd.devicetype, nd.status, nd.isDeleted, nd.mvnoId, nd.itemId) FROM NetworkDevices nd " +
            "WHERE LOWER(nd.devicetype) = 'olt' " +
            "AND nd.isDeleted = false " +
            "AND LOWER(nd.status) = 'active' " +
            "AND nd.mvnoId IN (:mvnoId) " +
            "AND nd.inventorymappingId In (:inventoryMappingId)")
    List<NetworkDevices> findActiveOltDeviceIdsByMvnoAndInventoryMapping(
            @Param("mvnoId") List<Integer> mvnoId,
            @Param("inventoryMappingId") List<Long> inventoryMappingId
    );

    @Query("SELECT new NetworkDevices(nd.id, nd.name, nd.displayname, " +
            "nd.devicetype, nd.status, nd.isDeleted, nd.mvnoId, nd.itemId) FROM NetworkDevices nd " +
            "WHERE LOWER(nd.devicetype) = 'olt' " +
            "AND nd.isDeleted = false " +
            "AND LOWER(nd.status) = 'active' " +
            "AND nd.inventorymappingId In (:inventoryMappingId)")
    List<NetworkDevices> findActiveOltDeviceIdsByInventoryMapping(
            @Param("inventoryMappingId") List<Long> inventoryMappingId
    );

    @Query("SELECT new NetworkDevices(nd.id, nd.name, nd.displayname, " +
            "nd.devicetype, nd.status, nd.isDeleted, nd.mvnoId, nd.itemId) FROM NetworkDevices nd " +
            "WHERE nd.id In (:id) " +
            "AND nd.isDeleted = false " +
            "AND nd.mvnoId In (:mvnoId)")
    List<NetworkDevices> findByIdInAndMvnoIdIn(List<Long> id, List<Integer> mvnoId);

    @Query("SELECT new NetworkDevices(nd.id, nd.name, nd.displayname, " +
            "nd.devicetype, nd.status, nd.isDeleted, nd.mvnoId, nd.itemId) FROM NetworkDevices nd " +
            "WHERE nd.id In (:id) " +
            "AND nd.isDeleted = false ")
    List<NetworkDevices> findByIdIn(List<Long> id);

    @Query("SELECT nd.id FROM NetworkDevices nd WHERE nd.custInventoryId IN (:custInventoryId) AND nd.isDeleted = false")
    List<Long> findAllIdsByCustomerInventoryIds(@Param("custInventoryId") List<Long> custInventoryId);

    @Query("SELECT nd.id FROM NetworkDevices nd WHERE nd.custInventoryId = :custInventoryId")
    List<Long> findAllIdsByCustomerInventoryId(@Param("custInventoryId") Long custInventoryId);

    @Query("SELECT nd.name FROM NetworkDevices nd WHERE nd.id = :id AND nd.isDeleted = false")
    Optional<String> findNameById(@Param("id") Long id);
}
