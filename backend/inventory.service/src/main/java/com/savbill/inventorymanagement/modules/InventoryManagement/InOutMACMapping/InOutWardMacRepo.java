package com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InOutWardMacRepo extends JpaRepository<InOutWardMACMapping, Long>, QuerydslPredicateExecutor<InOutWardMACMapping> {
    @Query(nativeQuery = true
    ,value = "select count(*)  from tblhitemhistory tiowmm\n" +
            "left join\n" +
            "tblminward t \n" +
            "on t.inward_id = tiowmm.inward_id \n" +
            "where tiowmm.inward_id =:id and t.is_deleted =false")
    Integer countInward(@Param("id") Integer id);

    @Query(nativeQuery = true
            ,value = "select count(*)  from tblhitemhistory tiowmm\n" +
            "left join\n" +
            "tblminward t \n" +
            "on t.inward_id = tiowmm.inward_id \n" +
            "where tiowmm.inward_id =:id and t.is_deleted =false and t.mvno_id in :mvnoIds")
    Integer countInward(@Param("id") Integer id,  @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true
            ,value = "select count(*)  from tblhitemhistory tiowmm\n" +
            "left join\n" +
            "tblminward t \n" +
            "on t.inward_id = tiowmm.inward_id \n" +
            "where tiowmm.inward_id_of_outward =:id and t.is_deleted =false")
    Integer countInwardIdOfOutward(@Param("id") Integer id);

    @Query(nativeQuery = true
            ,value = "select count(*)  from tblhitemhistory tiowmm\n" +
            "left join\n" +
            "tblminward t \n" +
            "on t.inward_id = tiowmm.inward_id \n" +
            "where tiowmm.inward_id_of_outward =:id and t.is_deleted =false and t.mvno_id in :mvnoIds")
    Integer countInwardIdOfOutward(@Param("id") Integer id,  @Param("mvnoIds") List mvnoIds);

    @Query(nativeQuery = true, value = "select * from tblhitemhistory tiowmm where tiowmm.inward_id =:id")
    List<InOutWardMACMapping> findByInwardId(@Param("id") Long id);

//    @Query(nativeQuery = true, value = "select * from tblhitemhistory tiowmm where tiowmm .mac_mapping_id =:id")
//    InOutWardMACMapping findByMacMappingId(@Param("id") Long id);
    List<InOutWardMACMapping> findAllByCustInventoryMappingId(Long id);

    List<InOutWardMACMapping> findAllByInventoryMappingId(Long id);

    InOutWardMACMapping findByItemId(Long id);

    List<InOutWardMACMapping> findAllByItemId(Long id);

    List<InOutWardMACMapping> findAllByItemIdIn(List<Long> id);
    List<InOutWardMACMapping> findAllByItemIdInAndOutwardIdInAndInventoryMappingIdIsNull(List<Long> itemId, List<Long> outwardId);
    List<InOutWardMACMapping> findAllByItemIdInAndOutwardIdInAndCustInventoryMappingIdIsNull(List<Long> itemId, List<Long> outwardId);
    List<InOutWardMACMapping> findAllByItemIdInAndOutwardIdInAndCustInventoryMappingIdIsNullAndInventoryMappingIdIsNull(List<Long> itemId, List<Long> outwardId);

    @Query(value = "select count(*) from tblmserializeditem m where m.mac=:mac and m.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("mac") String mac);

    @Query(value = "select count(*) from tblmserializeditem m where m.mac=:mac and m.is_deleted=false and serialized_item_id!=:itemId", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("mac") String mac,@Param("itemId") Long itemId);

    @Query(value = "select count(*) from tblmserializeditem m where m.mac=:mac and m.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("mac") String mac, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmserializeditem m where m.mac=:mac and m.is_deleted=false and mvno_id in :mvnoIds and serialized_item_id!=:itemId", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("mac") String mac, @Param("mvnoIds") List mvnoIds,@Param("itemId") Long itemId);

    @Query(value = "select count(*) from tblmserializeditem where mac=:mac and item_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("mac") String mac, @Param("id") Long id);

    @Query(value = "select count(*) from tblmserializeditem where mac=:mac and item_id =:id and is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("mac") String mac, @Param("id") Long id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblhitemhistory where inward_id =:id and is_forwarded =0 and is_deleted=false",nativeQuery = true)
    List<InOutWardMACMapping> findbyinwardid(@Param("id") Long id);

    @Query(value = "select * from tblhitemhistory where outward_id =:id and is_deleted=false",nativeQuery = true)
    List<InOutWardMACMapping> findbyoutwardid(@Param("id") Long id);

    @Query(value = "select * from tblhitemhistory tiowmm where inward_id =:id and is_deleted =false",nativeQuery = true)
    List<InOutWardMACMapping> deleteVerify(@Param("id") Integer id);

    List<InOutWardMACMapping> bulkConsumptionId(Long id);

    @Query(value="select * from tblhitemhistory tiowmm  left join tblmserializeditem t on t.id = tiowmm.item_id\n" +
            "where tiowmm.is_deleted = 0 and tiowmm.inward_id = :inwardId and tiowmm.is_forwarded = 0 and cust_inventory_mapping_id is null and bulkconsumption_id is null and inventory_mapping_id is null and t.item_status in ('UnAllocated','Defective');\n",nativeQuery = true)
    List<InOutWardMACMapping> findAllItemsByInwardIdAndItemStatus(@Param("inwardId")Long inwardId);

    @Query(value="select * from tblhitemhistory tiowmm  left join tblmserializeditem t on t.serialized_item_id = tiowmm.item_id\n" +
            "where tiowmm.is_deleted = 0 and tiowmm.external_item_id = :externalId and tiowmm.is_forwarded = 0 and cust_inventory_mapping_id is null and bulkconsumption_id is null and inventory_mapping_id is null and t.item_status = 'UnAllocated';\n",nativeQuery = true)
    List<InOutWardMACMapping> findAllItemsByExternalIdAndItemStatus(@Param("externalId")Long externalId);
    List<InOutWardMACMapping> findAllByItemIdInAndIsForwardedAndIsDeletedIsFalse(List<Long> itemId, Integer forwarded);
    InOutWardMACMapping findByItemIdAndIsForwardedAndIsDeletedIsFalse(Long itemId, Integer forwarded);

    List<InOutWardMACMapping> findAllByItemIdInAndIsForwarded(List<Long> itemId, Integer forwarded);


    @Query(value = "select item_id from tblhitemhistory where inward_id =:id and  is_deleted=false AND outward_id IS NULL",nativeQuery = true)
    List<Long> findByInwardIdAndOutwardIdIsNull(@Param("id") Long id);


//    @Query(value = "select item_id from tblhitemhistory where inward_id =:id and is_forwarded =0 and is_deleted=false",nativeQuery = true)
//    List<Long> findItemIdsByInwardId(@Param("id") Long id);

    List<InOutWardMACMapping> findAllByInwardIdOfOutwardAndIsDeletedIsFalse(Long id);
    List<InOutWardMACMapping> findAllByItemIdInAndOutwardIdInAndCustInventoryMappingIdIsNullAndIsForwarded(
            List<Long> itemIds, List<Long> outwardIds, Integer isForwarded);

    @Query(value = "SELECT item_id FROM tblhitemhistory WHERE mac_mapping_id = :id and is_deleted = false", nativeQuery = true)
    Long findItemIdById(@Param("id") Long id);

    @Query(value = "SELECT item_id FROM tblhitemhistory WHERE inward_id = :inwardId AND is_deleted = false", nativeQuery = true)
    List<Long> findItemIdsByInwardId(@Param("inwardId") Long inwardId);
    @Modifying
    @Query("UPDATE InOutWardMACMapping m SET m.isForwarded = :newValue WHERE m.inwardId = :inwardId AND m.isForwarded = :oldValue AND m.isReturned = :returnedValue")
    long updateIsForwarded(@Param("inwardId") Long inwardId, @Param("newValue") int newValue, @Param("oldValue") int oldValue, @Param("returnedValue") int returnedValue);

    @Query(value = "SELECT count(*) from tblhitemhistory t where t.inward_id = :inwardId", nativeQuery = true)
    Integer countItemsByInwardId(@Param("inwardId") Long inwardId);

    @Query(value = "select count(*) from tblhitemhistory where outward_id =:id and is_forwarded =0 and is_deleted=false",nativeQuery = true)
    Integer countItemsByOutwardId(@Param("id") Long id);

    @Query(value = "select t.item_id from tblhitemhistory t where t.inward_id_of_outward = :inwardId", nativeQuery = true)
    List<Long> findItemIdsByInwardIdOfOutward(@Param("inwardId") Long inwardId);

    @Query("SELECT m FROM InOutWardMACMapping m WHERE m.isDeleted = false AND m.itemId = :itemId")
    List<InOutWardMACMapping> findMappingsByItemId(@Param("itemId") Long itemId);

    @Modifying
    @Query("UPDATE InOutWardMACMapping m SET m.macAddress = :macAddress, m.serialNumber = :serialNumber WHERE m.isDeleted = false AND m.itemId = :itemId")
    int updateMappings(@Param("itemId") Long itemId,
                       @Param("macAddress") String macAddress,
                       @Param("serialNumber") String serialNumber);

    @Modifying
    @Query("UPDATE InOutWardMACMapping m SET m.serialNumber = :serialNumber WHERE m.isDeleted = false AND m.itemId = :itemId")
    int updateMappings(@Param("itemId") Long itemId,
                       @Param("serialNumber") String serialNumber);

    @Query(
            value = "SELECT * FROM tblhitemhistory " +
                    "WHERE item_id IN (:itemIds) " +
                    "AND is_forwarded = 0 " +
                    "AND cust_inventory_mapping_id IS NULL " +
                    "AND outward_id IN (:outwardIds) " +
                    "AND is_deleted = false",
            nativeQuery = true
    )
    List<InOutWardMACMapping> findMappingsNative(
            @Param("itemIds") List<Long> itemIds,
            @Param("outwardIds") List<Long> outwardIds
    );

    @Query("SELECT m.custInventoryMappingId FROM InOutWardMACMapping m WHERE m.id = :id")
    Long findCustInvMapId(@Param("id") Long id);
}
