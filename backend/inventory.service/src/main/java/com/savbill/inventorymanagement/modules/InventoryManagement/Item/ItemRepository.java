package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Repository
//@JaversSpringDataAuditable
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
	Page<Item> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);
	Page<Item> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);
	@Query(value = "select count(*) from tblmserializeditem m where m.name=:name and m.is_deleted=false",nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name")String name);

	@Query(value = "select * from tblmserializeditem m where current_inward_id=:id and m.is_deleted=false",nativeQuery = true)
	List<Item> findAllByInwardId(@Param("id")Integer id);

	@Query(value = "select count(*) from tblmserializeditem m where m.name=:name and m.is_deleted=false and mvno_id in :mvnoIds",nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

	@Query(value = "select count(*) from tblmserializeditem where id =:id and is_deleted=false " ,nativeQuery = true)
	Integer deleteVerify(@Param("id")Integer id);

	@Query(value = "select count(*) from tblmserializeditem t where t.id =:id and t.name =:name and t.is_deleted =false", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id);

	@Query(value = "select count(*) from tblmserializeditem t where t.id =:id and  t.name =:name and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoids);

	List<Item> findAllByCurrentInwardIdAndProductId(Long inwardId, Long productId);

//	@Query(value = "select * from tblmserializeditem t where t.current_inward_id in :id and t.mac in :mac and t.is_deleted =false", nativeQuery = true)
//	List<Item> findByCurrentId(@Param("id") List<Long> id,@Param("mac") List<String> mac);

//	@Query(value = "select * from tblmserializeditem t where t.external_item_id in :id and t.mac in :mac and t.is_deleted =false", nativeQuery = true)
//	List<Item> findByCurrentExternalItemId(@Param("id") List<Long> id,@Param("mac") List<String> mac);
	List<Item> findByMacAddress(String mac);
//	@Query(value = "select * from tblmserializeditem t where t.warranty='InWarranty'",nativeQuery = true)
//	List<Item> findBywarranty();

//	@Query(value = "select * from tblmserializeditem t where id =:id",nativeQuery = true)
//	List<Item> getall(@Param("id") Long id);
	List<Item> findAllById(Long id);
 	List<Item> findAllByIdIn(List<Long> id);
//	Item findTopByOrderByIdDesc();
	 List<Item> findAllByProductIdIn(List<Long> productId);
	Item findByIsDeletedIsFalseAndMacAddress(String macAddress);
	Item findByIsDeletedIsFalseAndMacAddressAndMvnoIdIn(String macAddress, List<Integer> mvnoId);
	List<Item> findAllByIsDeletedIsFalse();
	List<Item> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId);
	List<Item> findAllByOwnerIdInAndIsDeletedIsFalse(List<Long> ownerId);
	List<Item> findAllByOwnerIdInAndOwnerTypeAndIsDeletedIsFalseAndMvnoIdIn(List<Long> ownerId, String ownerType, List<Integer> mvnoId);
	Page<Item> findAllByIdIn(List<Long> id, Pageable pageable);
	List<Item> findAllByIsDeletedIsFalseAndProductIdAndOwnerIdAndOwnerType(Long productId, Long ownerId, String ownerType);

	@Query(value = "select count(*) from tblmserializeditem t where t.owner_id =:ownerId and  t.owner_type =:ownerType and t.is_deleted =false", nativeQuery = true)
	Long findAllByIsDeletedIsFalseAndOwnerIdAndOwnerType(@Param("ownerId") Long ownerId, @Param("ownerType") String ownerType);

	List<Item> findAllByWarranty(String warranty);

	@Query(value = "select * from tblmserializeditem t where t.is_deleted =false and t.oemstartdate IS NOT NULL and t.oemenddate IS NOT NULL", nativeQuery = true)
	List<Item> findAllByOemWarranty();
	Item findByIsDeletedIsFalseAndMacAddressAndCurrentInwardId(String macAddress, Long currentInwardId);
	Item findByIsDeletedIsFalseAndMacAddressAndMvnoIdInAndCurrentInwardId(String macAddress, Collection<Integer> mvnoId, Long currentInwardId);

	@Query("SELECT i.invenSpecId FROM Item i WHERE i.id IN :serializedItemIds")
	List<Long> getInvSpecIdBySerializedItemIds(@Param("serializedItemIds") List<Long> serializedItemIds);

    @Query(value = "SELECT i.mac FROM tblmserializeditem i WHERE i.serialized_item_id = :itemId and i.is_deleted =false", nativeQuery = true)
    String findMacByItemId(@Param("itemId") Long itemId);

    @Query(value = "SELECT i.serial_number FROM tblmserializeditem i WHERE i.serialized_item_id = :itemId and i.is_deleted =false", nativeQuery = true)
    String findSerialNumberByItemId(@Param("itemId") Long itemId);

    @Query(value = "SELECT i.item_condition FROM tblmserializeditem i WHERE i.serialized_item_id = :itemId", nativeQuery = true)
    String findItemConditionByItemId(@Param("itemId") Long itemId);

    @Query(value = "SELECT i.warranty FROM tblmserializeditem i WHERE i.serialized_item_id = :itemId", nativeQuery = true)
    String findWarrantyByItemId(@Param("itemId") Long itemId);

    @Query(value = "SELECT i.expiry_date FROM tblmserializeditem i WHERE i.serialized_item_id = :itemId", nativeQuery = true)
    LocalDateTime findExpiry_dateByItemId(@Param("itemId") Long itemId);

    @Query(value = "SELECT i.product_id FROM tblmserializeditem i WHERE i.serialized_item_id = :itemId", nativeQuery = true)
    Long findProductIdByItemId(@Param("itemId") Long itemId);

    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) FROM Item i " +
            "WHERE i.isDeleted = false AND i.productId = :productId AND i.ownerId = :ownerId AND LOWER(i.ownerType) = LOWER(:ownerType)AND " +
            "i.itemStatus IS NOT NULL AND LOWER(i.itemStatus) != 'destroyed' AND i.currentInwardId IN (:activeInwardIds)")
    List<Item> findActiveItems(
            @Param("productId") Long productId,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("activeInwardIds") List<Long> activeInwardIds
    );

    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) " +
            "FROM Item i " +
            "WHERE i.isDeleted = false " +
            "AND i.id IN :itemIds")
    List<Item> findItemsByIds(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) " +
            "FROM Item i " +
            "WHERE i.isDeleted = false " +
            "AND i.id = :itemId")
    Item findItemDetailsById(@Param("itemId") Long itemId);
	@Query("SELECT new Item(i.id, i.condition, i.ownerType) FROM Item i WHERE i.isDeleted = false " +
			"AND i.productId = :productId " +
			"AND i.ownerId = :ownerId " +
			"AND i.ownerType = :ownerType " +
			"AND i.itemStatus IN :statuses")
	List<Item> findItemIdsByFilters(@Param("productId") Long productId,
									@Param("ownerId") Long ownerId,
									@Param("ownerType") String ownerType,
									@Param("statuses") List<String> statuses);


    @Query(value = "SELECT i.inven_spec_id FROM tblmserializeditem i WHERE i.serialized_item_id = :itemId", nativeQuery = true)
    Long findInvSpecIdBySerializedItemId(@Param("itemId") Long itemId);

	@Modifying
	@Query("DELETE FROM Item i WHERE i.id IN :ids")
	void deleteByIds(@Param("ids") List<Long> ids);

	@Modifying
	@Query("DELETE FROM Item i WHERE i.currentInwardId = :inwardId AND i.productId = :productId")
	void deleteByCurrentInwardIdAndProductId(@Param("inwardId") Long inwardId, @Param("productId") Long productId);
	@Query(value = "SELECT i.serialized_item_id FROM tblmserializeditem i ORDER BY i.serialized_item_id DESC LIMIT 1", nativeQuery = true)
	Integer findItemIdOrderbyIdDesc();

	@Query(value = "SELECT mac FROM tblmserializeditem " +
			"WHERE is_deleted = false " +
			"AND mac IN (:macAddressSet) " +
			"AND current_inward_id != :inwardId " +
            "AND mvno_id = :mvnoId",
			nativeQuery = true)
	List<String> findExistingMacs(@Param("macAddressSet") Set<String> macAddressSet,
								  @Param("inwardId") Long inwardId, @Param("mvnoId") Integer mvnoId);

    @Query(value = "SELECT count(*) FROM tblmserializeditem WHERE is_deleted = false AND mac =:macAddress", nativeQuery = true)
    Integer findCountByMacAddress(@Param("macAddress") String macAddress);

    @Query(value = "SELECT count(*) FROM tblmserializeditem WHERE is_deleted = false AND mac =:macAddress and mvno_id IN (:mvno_id))", nativeQuery = true)
    Integer findCountByMacAddressAndMvnoId(@Param("macAddress") String macAddress, @Param("mvno_id") List<Integer> mvno_id);

    @Query("SELECT new Item(i.id, i.condition, i.ownershipType) " +
            "FROM Item i WHERE i.id IN :itemIds")
    List<Item> findItemSummariesById(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT new Item(i.id, i.condition, i.ownershipType) " +
            "FROM Item i WHERE i.id = :itemId")
    Item findItemSummariesById(@Param("itemId") Long itemId);

    @Modifying
    @Query("UPDATE Item i SET i.macAddress = :macAddress, i.serialNumber = :serialNumber WHERE i.id = :itemId and i.isDeleted = false")
    int updateItemDetails(@Param("itemId") Long itemId,
                          @Param("macAddress") String macAddress,
                          @Param("serialNumber") String serialNumber);

    @Modifying
    @Query("UPDATE Item i SET i.serialNumber = :serialNumber WHERE i.id = :itemId and i.isDeleted = false")
    int updateItemDetails(@Param("itemId") Long itemId,
                          @Param("serialNumber") String serialNumber);

    @Modifying
    @Query("UPDATE Item i SET i.oemStartDate = :oemStartDate, i.oemEndDate = :oemEndDate, i.oemWarrantyStatus = :oemWarrantyStatus, i.oemWarrantyRemainingDays = :oemWarrantyRemainingDays WHERE i.id = :id")
    void updateItemDetails(@Param("id") Long id,
                           @Param("oemStartDate") LocalDate oemStartDate,
                           @Param("oemEndDate") LocalDate oemEndDate,
                           @Param("oemWarrantyStatus") String oemWarrantyStatus,
                           @Param("oemWarrantyRemainingDays") Integer oemWarrantyRemainingDays);

    @Modifying
    @Query(value = "UPDATE Item i SET i.isDeleted = true WHERE i.id IN (:ids)", nativeQuery = true)
    void markItemsAsDeleted(@Param("ids") List<Long> ids);

    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) " +
            "FROM Item i " +
            "WHERE i.isDeleted = false " +
            "AND i.id IN :itemIds")
    Page<Item> findItemsByIds(@Param("itemIds") List<Long> itemIds, Pageable pageable);

    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) FROM Item i " +
            "WHERE i.isDeleted = false AND i.productId = :productId AND i.ownerId = :ownerId AND LOWER(i.ownerType) = LOWER(:ownerType)AND " +
            "i.itemStatus IS NOT NULL AND LOWER(i.itemStatus) != 'destroyed' AND i.currentInwardId IN (:activeInwardIds)")
    Page<Item> findActiveItems(
            @Param("productId") Long productId,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("activeInwardIds") List<Long> activeInwardIds,
            Pageable pageable
    );

    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) " +
            "FROM Item i " +
            "WHERE i.isDeleted = false " +
            "AND i.id IN :itemIds " +
            "AND CAST(i.id AS string) LIKE CONCAT('%', :id, '%')")
    Page<Item> findItemsByIdsAndItemId(@Param("itemIds") List<Long> itemIds,
                                             @Param("id") String idPart,
                                             Pageable pageable);
    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) FROM Item i " +
            "WHERE i.isDeleted = false AND i.productId = :productId AND i.ownerId = :ownerId AND LOWER(i.ownerType) = LOWER(:ownerType)AND " +
            "i.itemStatus IS NOT NULL AND LOWER(i.itemStatus) != 'destroyed' AND i.currentInwardId IN (:activeInwardIds)" +
            "AND CAST(i.id AS string) LIKE CONCAT('%', :id, '%')")
    Page<Item> findActiveItemsByItemId(
            @Param("productId") Long productId,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("activeInwardIds") List<Long> activeInwardIds,
            @Param("id") String id,
            Pageable pageable);

    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) " +
            "FROM Item i " +
            "WHERE i.isDeleted = false " +
            "AND i.id IN :itemIds " +
            "AND LOWER(i.macAddress) LIKE LOWER(CONCAT('%', :macAddress, '%'))")
    Page<Item> findItemsByIdsAndMacAddress(@Param("itemIds") List<Long> itemIds, @Param("macAddress") String macAddress, Pageable pageable);
    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) FROM Item i " +
            "WHERE i.isDeleted = false AND i.productId = :productId AND i.ownerId = :ownerId AND LOWER(i.ownerType) = LOWER(:ownerType)AND " +
            "i.itemStatus IS NOT NULL AND LOWER(i.itemStatus) != 'destroyed' AND i.currentInwardId IN (:activeInwardIds) " +
            "AND LOWER(i.macAddress) LIKE LOWER(CONCAT('%', :macAddress, '%'))")
    Page<Item> findActiveItemsByMac(
            @Param("productId") Long productId,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("activeInwardIds") List<Long> activeInwardIds,
            @Param("macAddress") String macAddress,
            Pageable pageable
    );
    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) " +
            "FROM Item i " +
            "WHERE i.isDeleted = false " +
            "AND i.id IN :itemIds " +
            "AND LOWER(i.serialNumber) LIKE LOWER(CONCAT('%', :serialNumber, '%'))")
    Page<Item> findItemsByIdsAndSerialNumber(@Param("itemIds") List<Long> itemIds, @Param("serialNumber") String serialNumber, Pageable pageable);
    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) FROM Item i " +
            "WHERE i.isDeleted = false AND i.productId = :productId AND i.ownerId = :ownerId AND LOWER(i.ownerType) = LOWER(:ownerType)AND " +
            "i.itemStatus IS NOT NULL AND LOWER(i.itemStatus) != 'destroyed' AND i.currentInwardId IN (:activeInwardIds) " +
            "AND LOWER(i.serialNumber) LIKE LOWER(CONCAT('%', :serialNumber, '%'))")
    Page<Item> findActiveItemsBySerialNumber(
            @Param("productId") Long productId,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("activeInwardIds") List<Long> activeInwardIds,
            @Param("serialNumber") String serialNumber,
            Pageable pageable
    );
    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) " +
            "FROM Item i " +
            "WHERE i.isDeleted = false " +
            "AND i.id IN :itemIds " +
            "AND LOWER(i.assetId) LIKE LOWER(CONCAT('%', :assetId, '%'))")
    Page<Item> findItemsByIdsAndAssetId(@Param("itemIds") List<Long> itemIds, @Param("assetId") String assetId, Pageable pageable);
    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) FROM Item i " +
            "WHERE i.isDeleted = false AND i.productId = :productId AND i.ownerId = :ownerId AND LOWER(i.ownerType) = LOWER(:ownerType)AND " +
            "i.itemStatus IS NOT NULL AND LOWER(i.itemStatus) != 'destroyed' AND i.currentInwardId IN (:activeInwardIds) " +
            "AND LOWER(i.assetId) LIKE LOWER(CONCAT('%', :assetId, '%'))")
    Page<Item> findActiveItemsByAssetId(
            @Param("productId") Long productId,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("activeInwardIds") List<Long> activeInwardIds,
            @Param("assetId") String assetId,
            Pageable pageable
    );
    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) " +
            "FROM Item i " +
            "WHERE i.isDeleted = false " +
            "AND i.id IN :itemIds " +
            "AND LOWER(i.condition) LIKE LOWER(CONCAT('%', :condition, '%'))")
    Page<Item> findItemsByIdsAndItemType(@Param("itemIds") List<Long> itemIds, @Param("condition") String condition, Pageable pageable);
    @Query("SELECT new Item(i.id, i.name, i.macAddress, i.serialNumber, i.assetId, i.condition, i.productId) FROM Item i " +
            "WHERE i.isDeleted = false AND i.productId = :productId AND i.ownerId = :ownerId AND LOWER(i.ownerType) = LOWER(:ownerType)AND " +
            "i.itemStatus IS NOT NULL AND LOWER(i.itemStatus) != 'destroyed' AND i.currentInwardId IN (:activeInwardIds) " +
            "AND LOWER(i.condition) LIKE LOWER(CONCAT('%', :condition, '%'))")
    Page<Item> findActiveItemsByItemType(
            @Param("productId") Long productId,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("activeInwardIds") List<Long> activeInwardIds,
            @Param("condition") String condition,
            Pageable pageable
    );

    @Query("SELECT new Item(i.id, i.condition, i.ownerType) FROM Item i WHERE i.isDeleted = false " +
            "AND i.productId = :productId " +
            "AND i.ownerId = :ownerId " +
            "AND i.ownerType = :ownerType " +
            "AND i.itemStatus IN :statuses " +
            "AND CAST(i.id AS string) LIKE CONCAT('%', :id, '%')")
    List<Item> findItemByItemId(@Param("productId") Long productId,
                                @Param("ownerId") Long ownerId,
                                @Param("ownerType") String ownerType,
                                @Param("statuses") List<String> statuses,
                                @Param("id") String id);

    @Query("SELECT new Item(i.id, i.condition, i.ownerType) FROM Item i WHERE i.isDeleted = false " +
            "AND i.productId = :productId " +
            "AND i.ownerId = :ownerId " +
            "AND i.ownerType = :ownerType " +
            "AND i.itemStatus IN :statuses " +
            "AND LOWER(i.macAddress) LIKE LOWER(CONCAT('%', :macAddress, '%'))")
    List<Item> findItemByMac(@Param("productId") Long productId,
                             @Param("ownerId") Long ownerId,
                             @Param("ownerType") String ownerType,
                             @Param("statuses") List<String> statuses,
                             @Param("macAddress") String macAddress);

    @Query("SELECT new Item(i.id, i.condition, i.ownerType) FROM Item i WHERE i.isDeleted = false " +
            "AND i.productId = :productId " +
            "AND i.ownerId = :ownerId " +
            "AND i.ownerType = :ownerType " +
            "AND i.itemStatus IN :statuses " +
            "AND LOWER(i.serialNumber) LIKE LOWER(CONCAT('%', :serialNumber, '%'))")
    List<Item> findItemBySerialNumber(@Param("productId") Long productId,
                                      @Param("ownerId") Long ownerId,
                                      @Param("ownerType") String ownerType,
                                      @Param("statuses") List<String> statuses,
                                      @Param("serialNumber") String serialNumber);

    @Query("SELECT new Item(i.id, i.condition, i.ownerType) FROM Item i WHERE i.isDeleted = false " +
            "AND i.productId = :productId " +
            "AND i.ownerId = :ownerId " +
            "AND i.ownerType = :ownerType " +
            "AND i.itemStatus IN :statuses " +
            "AND LOWER(i.condition) LIKE LOWER(CONCAT('%', :condition, '%'))")
    List<Item> findItemByCondition(@Param("productId") Long productId,
                                   @Param("ownerId") Long ownerId,
                                   @Param("ownerType") String ownerType,
                                   @Param("statuses") List<String> statuses,
                                   @Param("condition") String condition);

    @Query(value = "SELECT serialized_item_id FROM tblmserializeditem " +
            "WHERE is_deleted = false " +
            "AND product_id = :productId " +
            "AND (LOWER(item_status) = LOWER(:unallocatedStatus) OR LOWER(item_status) = LOWER(:staffAllocatedStatus)) " +
            "AND item_condition IN (:conditionList) " +
            "AND (LOWER(ownership_type) = LOWER(:subisuOwned) OR LOWER(ownership_type) = LOWER(:organizationOwned)) " +
            "AND owner_id = :ownerId " +
            "AND LOWER(owner_type) = LOWER(:ownerType) " +
            "AND LOWER(item_status) <> LOWER(:defectiveStatus)", nativeQuery = true)
    List<Long> findMatchingItemsForOrganization(
            @Param("productId") Long productId,
            @Param("unallocatedStatus") String unallocatedStatus,
            @Param("staffAllocatedStatus") String staffAllocatedStatus,
            @Param("subisuOwned") String subisuOwned,
            @Param("organizationOwned") String organizationOwned,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("defectiveStatus") String defectiveStatus,
            @Param("conditionList") List<String> conditionList
    );


    @Query(value = "SELECT serialized_item_id FROM tblmserializeditem " +
            "WHERE is_deleted = false " +
            "AND product_id = :productId " +
            "AND (LOWER(item_status) = LOWER(:unallocatedStatus) OR LOWER(item_status) = LOWER(:staffAllocatedStatus)) " +
            "AND item_condition IN (:conditionList) " +
            "AND (LOWER(ownership_type) = LOWER(:subisuOwned) OR LOWER(ownership_type) = LOWER(:organizationOwned)) " +
            "AND owner_id = :ownerId " +
            "AND LOWER(owner_type) = LOWER(:ownerType) " +
            "AND LOWER(item_status) <> LOWER(:defectiveStatus)", nativeQuery = true)
    List<Long> findMatchingItemsForNew(
            @Param("productId") Long productId,
            @Param("unallocatedStatus") String unallocatedStatus,
            @Param("staffAllocatedStatus") String staffAllocatedStatus,
            @Param("subisuOwned") String subisuOwned,
            @Param("organizationOwned") String organizationOwned,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("defectiveStatus") String defectiveStatus,
            @Param("conditionList") List<String> conditionList
    );

    @Query(value = "SELECT serialized_item_id FROM tblmserializeditem " +
            "WHERE is_deleted = false " +
            "AND product_id = :productId " +
            "AND (LOWER(item_status) = LOWER(:unallocatedStatus) OR LOWER(item_status) = LOWER(:staffAllocatedStatus)) " +
            "AND item_condition IN (:conditionList) " +
            "AND (LOWER(ownership_type) = LOWER(:subisuOwned) OR LOWER(ownership_type) = LOWER(:organizationOwned)) " +
            "AND owner_id = :ownerId " +
            "AND LOWER(owner_type) = LOWER(:ownerType) " +
            "AND LOWER(item_status) <> LOWER(:defectiveStatus)", nativeQuery = true)
    List<Long> findMatchingItemsForRefurbished(
            @Param("productId") Long productId,
            @Param("unallocatedStatus") String unallocatedStatus,
            @Param("staffAllocatedStatus") String staffAllocatedStatus,
            @Param("subisuOwned") String subisuOwned,
            @Param("organizationOwned") String organizationOwned,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("defectiveStatus") String defectiveStatus,
            @Param("conditionList") List<String> conditionList
    );

    @Query("SELECT i FROM Item i WHERE i.currentInwardId = :inwardId AND i.productId = :productId")
    Stream<Item> streamByCurrentInwardIdAndProductId(@Param("inwardId") Long inwardId, @Param("productId") Long productId);

    @Query("SELECT i.ownerId FROM Item i WHERE i.isDeleted = false AND i.id = :id")
    Long findOwerIdById(@Param("id") Long id);

//    @Query(
//            "SELECT i.id " +
//                    "FROM Item i " +
//                    "JOIN Inward t ON i.currentInwardId = t.id " +
//                    "WHERE i.isDeleted = false " +
//                    "AND t.isDeleted = false " +
//                    "AND LOWER(t.approvalStatus) = LOWER(:approvalStatus) " +
//                    "AND i.productId = :productId " +
//                    "AND i.ownerId = :ownerId " +
//                    "AND LOWER(i.ownerType) = LOWER(:ownerType) " +
//                    "AND LOWER(i.itemStatus) != 'destroyed' " +
//                    "AND ( " +
//                    "    (:isSuperAdmin = true) " +
//                    "    OR t.mvnoId IN (:mvnoIds) " +
//                    ")" +
//                    "AND NOT EXISTS ( " +
//                    "    SELECT 1 FROM InOutWardMACMapping h " +
//                    "    WHERE h.itemId = i.id " +
//                    "    AND h.isDeleted = false " +
//                    ")"
//    )
//    List<Long> findItemIdsForOutwardSim(
//            @Param("productId") Long productId,
//            @Param("ownerId") Long ownerId,
//            @Param("ownerType") String ownerType,
//            @Param("approvalStatus") String approvalStatus,
//            @Param("mvnoIds") List<Integer> mvnoIds,
//            @Param("isSuperAdmin") boolean isSuperAdmin
//    );

    @Query(value =
            "SELECT " +
                    "    i.serialized_item_id " +
                    "FROM tblmserializeditem i " +
                    "JOIN tblminward t " +
                    "    ON i.current_inward_id = t.inward_id " +
                    "WHERE t.approval_status = 'Approve' " +
                    "AND t.is_deleted = false " +
                    "AND i.is_deleted = false " +
                    "AND t.destination_type = :ownerType " +
                    "AND t.destination_id = :ownerId " +
                    "AND t.product_id = :productId " +
                    "AND i.product_id = :productId " +
                    "AND i.owner_id = :ownerId " +
                    "AND LOWER(i.owner_type) = LOWER(:ownerType) " +
                    "AND i.item_status IS NOT NULL " +
                    "AND LOWER(i.item_status) <> 'destroyed' " +
                    "AND ( " +
                    "      :isSuperAdmin = true " +
                    "      OR t.mvno_id IN (:mvnoIds) " +
                    ")",
            nativeQuery = true)
    List<Long> findItemIdsForOutwardSim(
            @Param("productId") Long productId,
            @Param("ownerId") Long ownerId,
            @Param("ownerType") String ownerType,
            @Param("mvnoIds") List<Integer> mvnoIds,
            @Param("isSuperAdmin") boolean isSuperAdmin
    );

    @Query(
            "SELECT i.id, " +
                    " i.macAddress " +
                    "FROM Item i " +
                    "WHERE i.macAddress IN :values "

    )
    List<Object[]> findItemByImsiOrMsisdnIn(
            @Param("values") Set<String> values
    );
}
