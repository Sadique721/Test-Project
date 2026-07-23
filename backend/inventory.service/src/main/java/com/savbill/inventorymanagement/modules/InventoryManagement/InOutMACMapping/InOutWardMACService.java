package com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping;

import com.savbill.inventorymanagement.core.constants.ClientServiceConstant;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.NMSIntegrationConstants;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.ClientService.ClientService;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.InventorySpecificationRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping.ItemAssignHistoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDeviceBind;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkdeviceBindRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WareHouseManagmentServiceAreamappingRepo;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserPojo;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.CasMaster.CasMaster;
import com.savbill.inventorymanagement.modules.CasMaster.CasMasterRepository;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceRepository;
import com.savbill.inventorymanagement.modules.DebitDoc.DebitDocRepository;
import com.savbill.inventorymanagement.modules.DebitDoc.DebitDocument;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMappingService;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMappping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMapppingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.QCustMacMappping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.GenerateremoveInventoryRequest.GenerateRemoveRequest;
import com.savbill.inventorymanagement.modules.InventoryManagement.GenerateremoveInventoryRequest.GenerateRemoveRequestRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.GenerateremoveInventoryRequest.QGenerateRemoveRequest;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMappingService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkDeviceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItemHierarchy.NonSerializedItemHierarchy;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItemHierarchy.NonSerializedItemHierarchyRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.Outward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwner;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ReturnProduct.Return;
import com.savbill.inventorymanagement.modules.InventoryManagement.ReturnProduct.ReturnRepo;
import com.savbill.inventorymanagement.modules.PlanService.PlanServiceRepository;
import com.savbill.inventorymanagement.modules.RecordPayment.RecordPaymentPojo;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy.HierarchyService;
import com.savbill.inventorymanagement.modules.WorkflowManagement.WorkflowAudit.WorkflowAuditService;
import com.savbill.inventorymanagement.rabbitmq.*;
import com.savbill.inventorymanagement.rabbitmq.*;
import com.savbill.inventorymanagement.security.spring.SpringContext;
import com.savbill.inventorymanagement.utils.TypeConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InOutWardMACService extends ExBaseAbstractService<InOutWardMACMapingDTO, InOutWardMACMapping, Long> {
    @Autowired
    InOutWardMacRepo repository;

    @Autowired
    ExternalItemMacSerialMappingRepo externalItemMacSerialMappingRepo;
    @Autowired
    CustMacMapppingRepository custMacMapppingRepository;
    @Autowired
    private InwardRepository inwardRepository;
    @Autowired
    CustMacMappingService custMacMapppingService;
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;
    @Autowired
    ProductServiceImpl productService;
    @Autowired
    InOutWardMACService inOutWardMACService;
//    @Autowired
//    CreditDocService creditDocService;

    @Autowired
    WorkflowAuditService workflowAuditService;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ItemServiceImpl itemService;

    @Autowired
    InOutWardMacRepo inOutWardMacRepo;

    @Autowired
    ItemConditionMappingServiceImpl itemConditionMappingService;

    @Autowired
    ItemWarrantyMappingServiceImpl itemWarrantyMappingService;

    @Autowired
    ExternalItemManagementRepository externalItemManagementRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ReturnRepo returnRepo;

    @Autowired
    private InwardServiceImpl inwardService;


    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    InventoryMappingRepo inventoryMappingRepo;

    @Autowired
    InventoryMappingService inventoryMappingService;
    @Autowired
    NetworkDeviceRepository networkDeviceRepository;

    @Autowired
    OutwardRepository outwardRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    private NonSerializedItemServiceImpl nonSerializedItemService;
    @Autowired
    private NonSerializedItemRepository nonSerializedItemRepository;
    @Autowired
    private NonSerializedItemHierarchyRepository nonSerializedItemHierarchyRepository;
    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private DebitDocRepository debitDocRepository;
    @Autowired
    private ProductOwnerService productOwnerService;
    @Autowired
    private PlanServiceRepository planServiceRepository;
    @Autowired
    private CasMasterRepository casMasterRepository;

    @Autowired
    private CustomerInventoryMappingMapper customerInventoryMappingMapper;

    @Autowired
    private ClientServiceRepository clientServiceRepository;
    @Autowired
    WareHouseManagmentServiceAreamappingRepo wareHouseManagmentServiceAreamappingRepo;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    //    @Autowired
//    WorkflowAuditService workflowAuditService;
    @Autowired
    HierarchyService hierarchyService;
    @Autowired
    InOutWardMacMapper mapper;

    @Autowired
    GenerateRemoveRequestRepo generateRemoveRequestRepo;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private ProductOwnerRepository productOwnerRepository;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    public ItemConditionMappingRepository itemConditionMappingRepository;

    @Autowired
    NetworkdeviceBindRepository networkdeviceBindRepository;

    @Autowired
    public ItemWarrantyMappingRepository itemWarrantyMappingRepository;

    @Autowired
    InwardMapper inwardMapper;

    @Autowired
    private ItemConditionsMappingMapper itemConditionsMappingMapper;

    @Autowired
    private ItemWarrantyMappingMapper itemWarrantyMappingMapper;

    @Autowired
    private InOutWardMacMapper inOutWardMacMapper;

    @Autowired
    private InventorySpecificationRepo inventorySpecificationRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    ItemAssignHistoryMappingRepo itemAssignHistoryMappingRepo;
    @Autowired
    EntityManagerFactory entityManagerFactory;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static final Logger LOGGER = Logger.getLogger(InOutWardMACService.class);

    public InOutWardMACService(InOutWardMacRepo repository, InOutWardMacMapper mapper) {
        super(repository, mapper);
    }

    @Transactional
    public void processCsv(boolean flag, Inward entity, InwardSaveMacSerialDTO dto, String serviceAreaNamesByWarehouseId, boolean hasSerial, boolean hasMac) {
        try {
            Integer wrty = entity.getProductId().getExpiryTime();
            if ("Month".equalsIgnoreCase(entity.getProductId().getExpiryTimeUnit())) {
                wrty *= 30;
            }
            Integer finalWrty = wrty;
            List<MacSerialListDTO> macSerialList = dto.getMacSerialListDTOList();
            String entityType = entity.getType();
            Integer mvnoId = entity.getMvnoId();
            Long destinationId = entity.getDestinationId();
            String destinationType = entity.getDestinationType();
            Long entityId = entity.getId();
            Long productId = entity.getProductId().getId();
            boolean isAsset = entity.getProductId().getHasAssetConsider();
            String ownershipType = CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED;
            String status = "NotStarted";
            String unallocated = CommonConstants.UNALLOCATED;
            String typeForwarded = TypeConstants.FORWARDED;
            int size = macSerialList.size();
            List<Item> items = Collections.synchronizedList(new ArrayList<>(size));
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            List<Future<List<Item>>> futures = new ArrayList<>();
            for (int i = 0; i < macSerialList.size(); i++) {
                final int index = i;
                futures.add(executor.submit(() -> {
                    List<Item> threadLocalBatch = new ArrayList<>();
                    MacSerialListDTO macSerialListDTO = macSerialList.get(index);
                    threadLocalBatch.add(new Item(
                            macSerialListDTO != null && macSerialListDTO.getMacAddress() != null && !macSerialListDTO.getMacAddress().isEmpty()
                                    ? macSerialListDTO.getMacAddress()
                                    : null,
                            macSerialListDTO.getSerialNumber(),
                            itemService.getRandomenumber("SI", "-", macSerialListDTO.getSerialNumber()),
                            entityType, mvnoId, destinationId, destinationType,
                            typeForwarded, entityId, productId, ownershipType,
                            unallocated, finalWrty, status,
                            flag ? entityId : null
                    ));
                    return threadLocalBatch; // Return each thread's batch separately
                }));
            }
            executor.shutdown();
            List<Item> allItems = new ArrayList<>();
            for (Future<List<Item>> future : futures) {
                try {
                    allItems.addAll(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LocalDateTime startTime = LocalDateTime.now();
//            System.out.println("Total items to insert: " + allItems.size());
            batchInsertItems(allItems);
            LocalDateTime endTime = LocalDateTime.now();
//            System.out.println("Batch Insert Completed in " + Duration.between(startTime, endTime));
            if (isAsset) {
                ClientService clientService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.INVEN_ASSET_ORG, getMvnoIdFromCurrentStaff());
                String invenAssetOrg;
                if (clientService != null)
                    invenAssetOrg = clientService.getValue();
                else {
                    invenAssetOrg = null;
                }
                List<Item> finalItems = items;
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        CompletableFuture.runAsync(() -> {
                            finalItems.parallelStream().forEach(item ->
                                    item.setAssetId(getAssetIdWithFormate(item.getId(), invenAssetOrg, serviceAreaNamesByWarehouseId))
                            );
                            batchUpdateItems(finalItems);

                        });
                    }
                });

            }
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    @Transactional
    public void batchUpdateItems(List<Item> items) {
        try {
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                int batchSize = 10000;
                // Merge detached entities before persisting
                entityManager.merge(item);

                if (i % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear(); // Free memory
                }
            }

            // Final flush to ensure any remaining entities are persisted
            entityManager.flush();
            entityManager.clear();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void batchInsertItems(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        entityManager.unwrap(Session.class)
                .doWork(connection -> {
                    String sql = "INSERT INTO tblmserializeditem (mac, serial_number, name, item_condition, mvno_id, owner_id, owner_type, "
                            + "current_inward_type, current_inward_id, product_id, ownership_type, item_status, warranty_period, "
                            + "warranty, inven_spec_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = connection.prepareStatement(sql)) {
                        for (Item item : items) {
                            // Check if macAddress is null
                            if (item.getMacAddress() != null) {
                                ps.setString(1, item.getMacAddress());
                            } else {
                                ps.setNull(1, Types.VARCHAR);
                            }
                            // Check if serialNumber is null
                            if (item.getSerialNumber() != null) {
                                ps.setString(2, item.getSerialNumber());
                            } else {
                                ps.setNull(2, Types.VARCHAR);
                            }
                            // Check if name is null
                            if (item.getName() != null) {
                                ps.setString(3, item.getName());
                            } else {
                                ps.setNull(3, Types.VARCHAR);
                            }
                            // Check if condition is null
                            if (item.getCondition() != null) {
                                ps.setString(4, item.getCondition());
                            } else {
                                ps.setNull(4, Types.VARCHAR);
                            }
                            // Check if mvnoId is null (Int should be nullable, we use setNull for Integers)
                            if (item.getMvnoId() != null) {
                                ps.setInt(5, item.getMvnoId());
                            } else {
                                ps.setNull(5, Types.INTEGER);
                            }
                            // Check if ownerId is null (Long should be nullable, we use setNull for Long)
                            if (item.getOwnerId() != null) {
                                ps.setLong(6, item.getOwnerId());
                            } else {
                                ps.setNull(6, Types.BIGINT);
                            }
                            // Check if ownerType is null
                            if (item.getOwnerType() != null) {
                                ps.setString(7, item.getOwnerType());
                            } else {
                                ps.setNull(7, Types.VARCHAR);
                            }
                            // Check if currentInwardType is null
                            if (item.getCurrentInwardType() != null) {
                                ps.setString(8, item.getCurrentInwardType());
                            } else {
                                ps.setNull(8, Types.VARCHAR);
                            }
                            // Check if currentInwardId is null
                            if (item.getCurrentInwardId() != null) {
                                ps.setLong(9, item.getCurrentInwardId());
                            } else {
                                ps.setNull(9, Types.BIGINT);
                            }
                            // Check if productId is null
                            if (item.getProductId() != null) {
                                ps.setLong(10, item.getProductId());
                            } else {
                                ps.setNull(10, Types.BIGINT);
                            }
                            // Check if ownershipType is null
                            if (item.getOwnershipType() != null) {
                                ps.setString(11, item.getOwnershipType());
                            } else {
                                ps.setNull(11, Types.VARCHAR);
                            }
                            // Check if itemStatus is null
                            if (item.getItemStatus() != null) {
                                ps.setString(12, item.getItemStatus());
                            } else {
                                ps.setNull(12, Types.VARCHAR);
                            }
                            // Check if warrantyPeriod is null
                            if (item.getWarrantyPeriod() != null) {
                                ps.setInt(13, item.getWarrantyPeriod());
                            } else {
                                ps.setNull(13, Types.INTEGER);
                            }
                            // Check if warranty is null
                            if (item.getWarranty() != null) {
                                ps.setString(14, item.getWarranty());
                            } else {
                                ps.setNull(14, Types.VARCHAR);
                            }
                            // Check if invenSpecId is null
                            if (item.getInvenSpecId() != null) {
                                ps.setLong(15, item.getInvenSpecId());
                            } else {
                                ps.setNull(15, Types.BIGINT);
                            }
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                });
    }


    @Override
    public String getModuleNameForLog() {
        return "[InOutWardMACService]";
    }

    List<InOutWardMACMapping> getByInwardId(Long inwardId) {
        try {
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            Inward inwardDetail = inwardRepository.findById(inwardId).get();
            Outward outward = inwardDetail.getOutwardId();
            if (inwardId != null) {
                if (outward != null) {
                    Integer outwardId = Math.toIntExact(outward.getId());
                    BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull().and(qInOutWardMACMapping.isDeleted.eq(false))
                            .and(qInOutWardMACMapping.inwardId.eq(inwardId));
                    return IterableUtils.toList(repository.findAll(booleanExpression));
                } else {
                    BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull().and(qInOutWardMACMapping.inwardId.eq(inwardId)).and(qInOutWardMACMapping.isDeleted.eq(false));
                    return IterableUtils.toList(repository.findAll(booleanExpression));
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<InOutWardMACMapping> getByBulkConsumptionId(Long bulkconsumptionId) {
        try {
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            //       Inward inwardDetail = inwardRepository.findById(inwardId).get();
            //      Outward outward = inwardDetail.getOutwardId();
            if (bulkconsumptionId != null) {
                //   Integer outwardId = Math.toIntExact(outward.getId());
                BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull().and(qInOutWardMACMapping.isDeleted.eq(false))
                        .and(qInOutWardMACMapping.bulkConsumptionId.eq(bulkconsumptionId));
                return IterableUtils.toList(repository.findAll(booleanExpression));
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


    public List<InOutWardMACMapping> getAllMACMappingByInwardId(Long inwardId) {
        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
        return IterableUtils.toList(repository.findAllItemsByInwardIdAndItemStatus(inwardId));
    }


    public List<InOutWardMACMapping> getAllMACByExisitingMacType(Long inwardId, Long inOutMappingId, String inventoryType) {
        List<InOutWardMACMapping> outWardMACMappingListbaseOnType = new ArrayList<>();
        try {
            Long itemId = inOutWardMacRepo.findItemIdById(inOutMappingId);
            String condition = itemRepository.findItemConditionByItemId(itemId);
            if (inventoryType.equalsIgnoreCase("Permanant Replacement")) {
                List<InOutWardMACMapping> inOutWardMACMappingList = repository.findAllItemsByInwardIdAndItemStatus(inwardId);
                inOutWardMACMappingList.forEach(inOutWardMACMapping -> {
                    String itemCondition = itemRepository.findItemConditionByItemId(inOutWardMACMapping.getItemId());
                    if (itemCondition != null && itemCondition.equalsIgnoreCase(condition)) {
                        outWardMACMappingListbaseOnType.add(inOutWardMACMapping);
                    }
                });
            }
            if (inventoryType.equalsIgnoreCase("Temporary Replacement")) {
                List<InOutWardMACMapping> inOutWardMACMappingList = repository.findAllItemsByInwardIdAndItemStatus(inwardId);
                inOutWardMACMappingList.forEach(inOutWardMACMapping -> {
                    String itemCondition = itemRepository.findItemConditionByItemId(inOutWardMACMapping.getItemId());
                    if (itemCondition != null && itemCondition.equalsIgnoreCase(CommonConstants.REFURBISHED)) {
                        outWardMACMappingListbaseOnType.add(inOutWardMACMapping);
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
        return outWardMACMappingListbaseOnType;
    }

    public List<InOutWardMACMapping> getAllMACMappingByExternalId(Long externalId) {
        try {
            QInOutWardMACMapping tiowmm = QInOutWardMACMapping.inOutWardMACMapping;
            QItem t = QItem.item;
            JPAQuery<InOutWardMACMapping> query = new JPAQuery<>(entityManager);
            return query.select(tiowmm)
                    .from(tiowmm)
                    .leftJoin(t)
                    .on(t.id.eq(tiowmm.itemId))
                    .where(tiowmm.isDeleted.eq(false),
                            tiowmm.externalItemId.eq(externalId),
                            tiowmm.isForwarded.eq(0),
                            tiowmm.custInventoryMappingId.isNull(),
                            tiowmm.bulkConsumptionId.isNull(),
                            tiowmm.inventoryMappingId.isNull(),
                            t.itemStatus.containsIgnoreCase("UnAllocated"))
                    .fetch();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteMacMapInCustomer(Integer customerId, String macAddress) {
        QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
        BooleanExpression booleanExpression = qCustMacMappping.isNotNull().and(qCustMacMappping.customer.id.eq(customerId)).and(qCustMacMappping.macAddress.eq(macAddress));
        CustMacMappping custMacMappping = custMacMapppingRepository.findOne(booleanExpression).orElse(null);
        if (Objects.nonNull(custMacMappping)) {
            custMacMapppingService.delete(custMacMappping.getId());
        }
    }

    @Transactional
    public void deleteMac(Long itemId) {
        try {
            Item item = itemRepository.findById(itemId).get();
            if (!Objects.isNull(item)) {
                item.setIsDeleted(true);
                itemRepository.save(item);
            }
            InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findByItemId(itemId);
            if (!Objects.isNull(inOutWardMACMapping)) {
                inOutWardMACMapping.setIsDeleted(true);
                inOutWardMacRepo.save(inOutWardMACMapping);
            }
            Inward inward = inwardRepository.findById(inOutWardMACMapping.getInwardId()).get();
            if (!Objects.isNull(inward)) {
                inward.setTotalMacSerial(inward.getTotalMacSerial() - 1);
                inwardRepository.save(inward);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public InOutWardMACMapingDTO removeMappingWithCustomerInventory(Long mappingId, Customers customers) throws Exception {
        try {
            InOutWardMACMapingDTO outWardMACMapping = getMapper().domainToDTO(repository.findById(mappingId).get(), new CycleAvoidingMappingContext());
            if (Objects.nonNull(outWardMACMapping)) {
                if (customers == null) {
                    CustomerInventoryMappingDto entity = customerInventoryMappingService.getEntityById(outWardMACMapping.getCustInventoryMappingId());
                    customers = customersRepository.findById(entity.getCustomerId()).get();
                }
                if (outWardMACMapping.getMacAddress() != null) {
                    custMacMapppingService.deleteByMacAddress(outWardMACMapping.getMacAddress(), customers);
                }
                outWardMACMapping.setCustInventoryMappingId(null);
                return super.saveEntity(outWardMACMapping);
            }

            return outWardMACMapping;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Transactional
    public InOutWardMACMapping removeMappingWithPopANdServiceAreaInventory(Long mappingId) throws Exception {
        InOutWardMACMapping outWardMACMapping = inOutWardMacRepo.findById(mappingId).get();
        outWardMACMapping.setInventoryMappingId(null);
        inOutWardMacRepo.save(outWardMACMapping);
        return outWardMACMapping;
    }


//    //@Transactional
//    public GenericDataDTO removeInventory(Long mappingId, Long customerInventoryId, Long customerId, boolean isflag, String remark, boolean isApproveRequest) throws Exception {
//        GenericDataDTO genericDataDTO=new GenericDataDTO();
//        CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingService.getRepository().findById(customerInventoryId).orElse(null);
//        customerInventoryMapping.setApprovalRemark(remark);
//        PlanService planService=planServiceRepository.findById(customerServiceMappingRepository.findByConnectionNo(customerInventoryMapping.getConnectionNo()).getServiceId().intValue()).get();
//        if (planService != null) {
//            if (planService.getIs_dtv() == true) {
//                EzBillServiceUtility ezBillService = new EzBillServiceUtility();
//                Product product = productRepository.findById(customerInventoryMapping.getProduct().getId()).orElse(null);
//                CasMaster casMaster = casMasterRepository.findById(customerInventoryMapping.getProduct().getCaseId()).orElse(null);
//                Item item = itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
//                if (product.getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
//                    if (casMaster != null && item != null) {
//                        ezBillService.replaceSetupBox(casMaster, null, item.getSerialNumber(), 4);
//                      //  ezBillService.getUnPairedInfoResponse(casMaster, item.getSerialNumber());
//
//                    }
//                }
//            }
//        }
//        CustomerInventoryMappingDto entity = customerInventoryMappingService.getEntityById(customerInventoryId);
//
//        DebitDocument debitDocument=debitDocRepository.findByInventoryMappingId(customerInventoryId);
//        if(debitDocument!=null){
//            debitDocService.voidInvoice(debitDocument.getId());
//        }
//        StaffUser loggedInUser = staffUserService.get(getLoggedInUserId());
//        Customers customers = null;
//        if (customerId != null)
//            customers = customersRepository.findById(Math.toIntExact(customerId)).get();
//        if (Objects.nonNull(customerInventoryMapping)) {
//            if (Objects.equals(loggedInUser.getUsername(), "admin") || Objects.equals(loggedInUser.getUsername(), "superadmin")) {
//                customerInventoryMapping.setNextApprover(null);
//                customerInventoryMapping.setPreviousApproveId(getLoggedInUserId());
//                customerInventoryMapping.setTeamHierarchyMapping(null);
//                customerInventoryMapping.setStatus("TERMINATED");
//                if(!isApproveRequest){
//                    updateInventoryMapping(customerInventoryMapping,mappingId,customerInventoryId,customerId,isflag,remark);
//                }else {
//                    customerInventoryMappingRepo.save(customerInventoryMapping);
//                }
//
//            }
//            if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
//                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getLoggedInUserId(), customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
//                StaffUser assignedUser = null;
//                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                    StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
//                    assignedUser = staffUser;
//                    entity.setNextApproverId(Integer.valueOf(map.get("staffId")));
//                    entity.setPreviousApproveId(getLoggedInUserId());
//                    entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//                    entity.setStatus("PENDING");
//                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
//                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
//                } else {
//                    entity.setNextApproverId(null);
//                    entity.setTeamHierarchyMappingId(null);
//                    entity.setPreviousApproveId(getLoggedInUserId());
//                    if (!isApproveRequest) {
//                        entity.setStatus("REMOVED");
//                        entity =  updateInventoryMapping(customerInventoryMapping,mappingId,customerInventoryId,customerId,isflag,remark);;
//                    }
////                    else {
////                        entity.setStatus("REJECTED");
// //                       entity =  updateInventoryMapping(customerInventoryMapping,mappingId,customerInventoryId,customerId,isflag,remark);
////                    }
//                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
//                }
//                //TAT functionality
//                if (assignedUser != null) {
//                    if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                        if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
//                            map.put("tat_id", map.get("current_tat_id"));
//                        tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, entity.getId().intValue(), null);
//                    }
//                }
//            } else {
//                Map<String, Object> map = hierarchyService.getTeamForNextApprove(getLoggedInUserId(), customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
//                if (map.containsKey("assignableStaff")) {
//                    genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
//                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                    entity.setTeamHierarchyMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
//
//                } else {
//                    entity.setNextApproverId(null);
//                    entity.setTeamHierarchyMappingId(null);
//                    if (!isApproveRequest) {
//                        entity.setStatus("REMOVED");
//                        entity =  updateInventoryMapping(customerInventoryMapping,mappingId,customerInventoryId,customerId,isflag,remark);
//                        entity.setPreviousApproveId(getLoggedInUserId());
//
//                    }

    /// /                    else {
    /// /                        entity.setStatus("REJECTED");
    /// /                        entity =  updateInventoryMapping(customerInventoryMapping,mappingId,customerInventoryId,customerId,isflag,remark);
    /// /                    }
//
//                }
//                customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
//            }
//
//        }
//        return genericDataDTO;
//    }
    @Transactional
    public void removeInventory(Long mappingId, CustomerInventoryMapping customerInventoryMapping, Customers customers, String remark, Product product) throws Exception {
        try {
            DebitDocument debitDocument = debitDocRepository.findByInventoryMappingId(customerInventoryMapping.getId());

            ProductOwner productOwner = new ProductOwner();
            if (getLoggedInUser().getPartnerId() != 1) {
                productOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(customerInventoryMapping.getProduct().getId(), customerInventoryMapping.getCreatedById().longValue(), "Partner");
            } else {
                productOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(customerInventoryMapping.getProduct().getId(), customerInventoryMapping.getCreatedById().longValue(), "Staff");
            }
            Long quantity = null;
            Long unUsedQty = null;
            Long usedQty = null;
            if (productOwner != null) {
                quantity = productOwner.getQuantity();
                unUsedQty = productOwner.getUnusedQty();
                usedQty = productOwner.getUsedQty();
            }

            Boolean isDTVById = planServiceRepository.findIsDTVById(customerInventoryMapping.getServiceId());
            if (isDTVById) {
//                        EzBillServiceUtility ezBillService = new EzBillServiceUtility();
                CasMaster casMaster = casMasterRepository.findById(customerInventoryMapping.getProduct().getCaseId()).orElse(null);
                Item item = itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
                if (product.getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                    if (casMaster != null && item != null) {
                        try {
//                                    ezBillService.replaceSetupBox(casMaster, null, item.getSerialNumber(), 4, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
                        } catch (Exception e) {
                            if (!e.getMessage().contains("old STB doesn't exist or STB doesn't have customer or Old STB Already upgraded/surrender/defective.")) {
                                throw e;
                            }
                        }
                    }
                }
            }
            if (Objects.nonNull(customerInventoryMapping)) {
                customerInventoryMapping.setQty(customerInventoryMapping.getQty() - 1);
                removeMappingWithCustomerInventory(mappingId, customers);
                if (Objects.nonNull(product)) {
                    QGenerateRemoveRequest qGenerateRemoveRequest = QGenerateRemoveRequest.generateRemoveRequest;
                    BooleanExpression booleanExpression = qGenerateRemoveRequest.customerid.eq(customers.getId().longValue()).and(qGenerateRemoveRequest.customerinventoryId.eq(customerInventoryMapping.getId())).and(qGenerateRemoveRequest.macmappingid.eq(mappingId)).and(qGenerateRemoveRequest.isDeleted.eq(false));
                    GenerateRemoveRequest generateRemoveRequests = generateRemoveRequestRepo.findOne(booleanExpression).orElse(null);
                    LocalDate date = LocalDate.now();
                    if (!customerInventoryMapping.getCreatedate().toLocalDate().equals(date)) {
                        if (generateRemoveRequests.getRevisedcharge() != null) {
                            if (generateRemoveRequests.getRevisedcharge() != 0) {
                                RecordPaymentMessage message = new RecordPaymentMessage();
                                message.setCustomerid(customerInventoryMapping.getCustomer().getId());
                                message.setCustomerMappingId(customerInventoryMapping.getId());
                                message.setAmount(Double.valueOf(generateRemoveRequests.getRevisedcharge()));
                                message.setPaymode("Cash");
                                message.setIsSameDay(false);
                                if (customers != null && customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION))
                                    message.setIsCaf(true);
                                else
                                    message.setIsCaf(false);
                                message.setServiceId(customerInventoryMapping.getServiceId());
                                message.setPaytype("advance");
                                message.setType("creditnote");
                                message.setRemark("Refund amount for removing Product :-" + product.getName());
//                            messageSender.send(message, RabbitMqConstants.QUEUE_INVENTORY_SEND_RECORD_PAYMENT_TO_REVENUE);
                                kafkaMessageSender.send(new KafkaMessageData(message, RecordPaymentMessage.class.getSimpleName()));
                            }
                        }
                    }

                    if (customerInventoryMapping.getCreatedate().toLocalDate().equals(date) && customers != null && customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION)) {
                        RecordPaymentMessage message = new RecordPaymentMessage();
                        message.setCustomerid(customerInventoryMapping.getCustomer().getId());
                        message.setCustomerMappingId(customerInventoryMapping.getId());
                        message.setAmount(0.0d);
                        message.setPaymode("Cash");
                        message.setIsSameDay(true);
                        message.setIsCaf(true);
                        message.setServiceId(customerInventoryMapping.getServiceId());
                        message.setPaytype("advance");
                        message.setType("creditnote");
                        message.setRemark("Refund amount for removing Product :-" + product.getName());
//                        messageSender.send(message, RabbitMqConstants.QUEUE_INVENTORY_SEND_RECORD_PAYMENT_TO_REVENUE);
                        kafkaMessageSender.send(new KafkaMessageData(message, RecordPaymentMessage.class.getSimpleName()));
                    }
                }

                if (customerInventoryMapping.getInwardId() != null) {
                    Inward inward = inwardRepository.findById(customerInventoryMapping.getInwardId()).get();
                    if (inward != null) {
                        inward.setUnusedQty(inward.getUnusedQty() + 1);
                        inward.setUsedQty(inward.getUsedQty() - 1);
                        inwardRepository.save(inward);
                    }
                }

                if (customerInventoryMapping.getExternalItemId() != null) {
                    ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(customerInventoryMapping.getExternalItemId()).get();
                    if (externalItemManagement != null) {
                        externalItemManagement.setUnusedQty(externalItemManagement.getUnusedQty() + 1);
                        externalItemManagement.setUsedQty(externalItemManagement.getUsedQty() - 1);
                        externalItemManagementRepository.save(externalItemManagement);
                    }
                    for (int i = 0; i < customerInventoryMapping.getExternalItemMacSerialMappings().size(); i++) {
                        ExternalItemMacSerialMapping externalItemMacSerialMapping = externalItemMacSerialMappingRepo.findById(customerInventoryMapping.getExternalItemMacSerialMappings().get(i).getId()).get();
                        if (externalItemMacSerialMapping != null) {
                            externalItemMacSerialMapping.setCustInventoryMappingId(null);
                            externalItemMacSerialMappingRepo.save(externalItemMacSerialMapping);
                        }
                    }
                }
                Long itemIdById = repository.findItemIdById(mappingId);
                Item item = itemRepository.findById(itemIdById).get();
                if (!Objects.isNull(item)) {
                    if (item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED) || item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED)) {
                        itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                        if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item, "Paused");
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(item.getId(), customerInventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
//                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
//                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
//                        itemReturnDTO.setId(item.getId());
//                        itemReturnDTOList.add(itemReturnDTO);
                        itemService.removeAndreturnItemfromStaffremove(item, customerInventoryMapping);
                        GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryMapping.getId());
                        if (generateRemoveRequest != null) {
                            if (getLoggedInUser().getPartnerId() != 1) {
                                item.setOwnerType(CommonConstants.PARTNER);
                                StaffUser staffUser = staffUserRepository.findById(generateRemoveRequest.getStaffid()).get();
                                item.setOwnerId(staffUser.getPartnerid().longValue());
                            } else {
                                item.setOwnerType(CommonConstants.STAFF);
                                item.setItemStatus(CommonConstants.STAFF_ALLOCATED);
                                item.setOwnerId(Long.valueOf(generateRemoveRequest.getStaffid()));
                            }
                            itemRepository.save(item);
                        }
                    }

                    if (item.getOwnershipType().equalsIgnoreCase("Sold")) {
                        itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        item.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
                        if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item, "Paused");
                        }
//                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
//                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
//                        itemReturnDTO.setId(item.getId());
//                        itemReturnDTOList.add(itemReturnDTO);
                        itemService.removeAndreturnItemfromStaffremove(item, customerInventoryMapping);
                        if (getLoggedInUser().getPartnerId() != 1) {
                            GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryMapping.getId());
                            if (generateRemoveRequest != null) {
                                item.setOwnerType(CommonConstants.PARTNER);
                                StaffUser staffUser = staffUserRepository.findById(generateRemoveRequest.getStaffid()).get();
                                item.setOwnerId(staffUser.getPartnerid().longValue());
                            }
                        } else {
                            GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryMapping.getId());
                            item.setOwnerType(CommonConstants.STAFF);
                            item.setItemStatus(CommonConstants.STAFF_ALLOCATED);
                            item.setOwnerId(Long.valueOf(generateRemoveRequest.getStaffid()));
                        }
                        itemRepository.save(item);

                    }

                    if (item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.CUSTOMER_OWNED) ||
                            item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.TEMPORARY) ||
                            item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.PARTNER_OWNED)) {
                        itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                    }
                }
                //deleteCustomerInventory
                customerInventoryMapping.setIsDeleted(true);
                customerInventoryMapping.setApprovalRemark(remark);
                customerInventoryMappingRepo.save(customerInventoryMapping);
                Return aReturn = new Return();
                aReturn.setMac_name(item.getMacAddress());
                aReturn.setItem_status(item.getItemStatus());
                aReturn.setItem_condition(item.getCondition());
                aReturn.setProduct_id(item.getProductId());
                aReturn.setCurrent_inward_type(item.getCurrentInwardType());
                aReturn.setCurrent_inward_id(item.getCurrentInwardId());
                aReturn.setSerial_no(item.getSerialNumber());
                aReturn.setProduct_name(item.getName());
                aReturn.setCust_id(Long.parseLong(customerInventoryMapping.getCustomer().getId().toString()));
                returnRepo.save(aReturn);

            }
            if (productOwner != null) {
                //updateProductOwner Table
                productOwner.setQuantity(quantity);
                productOwner.setUsedQty(usedQty - 1);
                productOwner.setUnusedQty(unUsedQty + 1);
                ProductOwner owner = productOwnerRepository.save(productOwner);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage(), null);
        }
    }

    @Transactional
    public void removeInventoryfrompop(Long macMacmappingId, boolean isflag, String itemStatus) {
        try {
            InventoryMapping inventoryMapping = inventoryMappingRepo.findById(inOutWardMacRepo.findById(macMacmappingId).get().getInventoryMappingId()).get();
            Optional<Item> item = itemRepository.findById(inOutWardMacRepo.findById(macMacmappingId).get().getItemId());
            ProductOwner productOwner = new ProductOwner();
            if (getLoggedInUser().getPartnerId() != 1) {
                productOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(inventoryMapping.getProduct().getId(), Long.valueOf(getLoggedInUser().getPartnerId()), "Staff");
            } else {
                productOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(inventoryMapping.getProduct().getId(), Long.valueOf(getLoggedInUser().getUserId()), "Staff");
            }
            Long quantity = productOwner.getQuantity();
            Long unUsedQty = productOwner.getUnusedQty();
            Long usedQty = productOwner.getUsedQty();
            if (inventoryMapping.getQty() - 1 == 0) {
                InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(macMacmappingId).get();
                inOutWardMACMapping.setInventoryMappingId(null);
                inOutWardMacRepo.save(inOutWardMACMapping);

                if (!Objects.isNull(item)) {
                    if (item.get().getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED) || item.get().getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED)) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }

                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndIsDeletedFalse(item.get().getId());
                        if (networkDevices != null) {
                            List<NetworkDeviceBind> list = networkdeviceBindRepository.findByCurrentDeviceId(networkDevices.getId());
                            if (list != null && !list.isEmpty())
                                throw new RuntimeException("Can not Delete due to Network Device is already Bound in Network");
                        }

                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
//                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
//                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
//                        itemReturnDTO.setId(item.get().getId());
//                        itemReturnDTOList.add(itemReturnDTO);
                        itemService.returnItemfromStaffremove(item.get());
                        item.get().setOwnerType(CommonConstants.STAFF);
                        if (itemStatus != null && itemStatus.equalsIgnoreCase(CommonConstants.DEFECTIVE))
                            item.get().setItemStatus(CommonConstants.DEFECTIVE);
                        item.get().setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                        itemRepository.save(item.get());
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase("Sold") && isflag) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase("Sold") && isflag == true) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        item.get().setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
                        if (item.get().getWarranty().equalsIgnoreCase("InWarrenty")) {
                            itemService.updateItemWarranty(item.get(), "Paused");
                        }

//                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
//                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
//                        itemReturnDTO.setId(item.get().getId());
//                        itemReturnDTOList.add(itemReturnDTO);
                        itemService.returnItemfromStaffremove(item.get());
                        item.get().setOwnerType(CommonConstants.STAFF);
                        item.get().setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                        itemRepository.save(item.get());
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.CUSTOMER_OWNED) ||
                            item.get().getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.TEMPORARY) ||
                            item.get().getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.PARTNER_OWNED)) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }

                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                    }
                }

                inventoryMapping.setQty(0L);
                inventoryMapping.setIsDeleted(true);
                itemRepository.save(item.get());
                inventoryMappingRepo.save(inventoryMapping);
                item.get().setRemoveFrom("Pop");
                //Todo: Code for Approve Remove Inventory Serialized Item Request for Integration
//                ItemMessage message = new ItemMessage(item.get(),"Item Remove From Pop and Service Area");
//                messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_APPROVE_REMOVE_INVENTORY_SERIALIZEDITEM_REQUEST_IN_INTEGRATION);
            } else {
                // Optional<Item> item = itemRepository.findById(inOutWardMacRepo.findById(macMacmappingId).get().getItemId());
                InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(macMacmappingId).get();
                inOutWardMACMapping.setInventoryMappingId(null);
                inOutWardMacRepo.save(inOutWardMACMapping);
                if (!Objects.isNull(item)) {
                    if (item.get().getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED) || item.get().getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED)) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }

                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndIsDeletedFalse(item.get().getId());
                        if (networkDevices != null) {
                            List<NetworkDeviceBind> list = networkdeviceBindRepository.findByCurrentDeviceId(networkDevices.getId());
                            if (list != null && !list.isEmpty())
                                throw new RuntimeException("Can not Delete due to Network Device is already Bound in Network");
                        }

                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
//                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
//                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
//                        itemReturnDTO.setId(item.get().getId());
//                        itemReturnDTOList.add(itemReturnDTO);
                        itemService.returnItemfromStaffremove(item.get());
                        item.get().setOwnerType(CommonConstants.STAFF);
                        item.get().setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                        itemRepository.save(item.get());
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase("Sold") && isflag) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase("Sold") && isflag == true) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        item.get().setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
                        if (item.get().getWarranty().equalsIgnoreCase("InWarrenty")) {
                            itemService.updateItemWarranty(item.get(), "Paused");
                        }

//                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
//                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
//                        itemReturnDTO.setId(item.get().getId());
//                        itemReturnDTOList.add(itemReturnDTO);
                        itemService.returnItemfromStaffremove(item.get());
                        item.get().setOwnerType(CommonConstants.STAFF);
                        item.get().setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                        itemRepository.save(item.get());
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.CUSTOMER_OWNED) ||
                            item.get().getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.TEMPORARY) ||
                            item.get().getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.PARTNER_OWNED)) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }

                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                    }
                }

                inventoryMapping.setQty(inventoryMapping.getQty() - 1);
                itemRepository.save(item.get());
                inventoryMappingRepo.save(inventoryMapping);

            }
            //updateProductOwner Table
            productOwner.setQuantity(quantity);
            productOwner.setUsedQty(usedQty - 1);
            productOwner.setUnusedQty(unUsedQty + 1);
            ProductOwner owner = productOwnerRepository.save(productOwner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<CustomerInventoryMappingDto> getAllAssemblyInventory(Long assemblyId) {
        try {
            List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByItemAssemblyId(assemblyId);
            if (customerInventoryMappings.size() != 0) {

                List<CustomerInventoryMappingDto> customerInventoryMappingDtoList = customerInventoryMappingMapper.domainToDTO(customerInventoryMappings, new CycleAvoidingMappingContext());
                customerInventoryMappingDtoList.stream().forEach(r -> {
                    Product product = productRepository.findById(r.getProductId()).orElse(null);
                    if (product != null) {
                        r.setDtvCategory(product.getProductCategory().getDtvCategory());
                    }
                });
                return customerInventoryMappingDtoList;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return null;
    }

    @Override
    public InOutWardMACMapingDTO saveEntity(InOutWardMACMapingDTO entity) throws Exception {
        try {
            boolean flag = true;
            if (entity.getMacAddress() != null) {
                flag = inOutWardMACService.duplicateVerifyAtSave(entity.getMacAddress());
            }
            entity.setMvnoId(getMvnoIdFromCurrentStaff());
            if (flag) {
                Inward inward = inwardRepository.findById(entity.getInwardId()).get();
                InwardServiceImpl inwardService = SpringContext.getBean(InwardServiceImpl.class);
                InwardDto inwardDto = inwardMapper.domainToDTO(inwardRepository.findById(entity.getInwardId()).orElse(null), new CycleAvoidingMappingContext());
                inwardDto.setTotalMacSerial(inward.getTotalMacSerial() + 1);
                inwardService.updateEntity(inwardDto);
                ItemDto item = new ItemDto();
                item.setMacAddress(entity.getMacAddress());
                item.setSerialNumber(entity.getSerialNumber());
                item.setName(inward.getProductId().getName());
                item.setCondition(inward.getType());
                item.setMvnoId(inward.getMvnoId());

                item.setOwnerId(inward.getDestinationId());
                item.setOwnerType(inward.getDestinationType());
                item.setCurrentInwardType(TypeConstants.FORWARDED);
                item.setCurrentInwardId(inward.getId());
                item.setProductId(inward.getProductId().getId());
                item.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);

                item.setItemStatus(CommonConstants.UNALLOCATED);

                Integer wrty = inward.getProductId().getExpiryTime();
                if (inward.getProductId().getExpiryTimeUnit().equalsIgnoreCase("Month")) {
                    wrty = 30 * wrty;
                    item.setWarrantyPeriod(wrty);
                } else {
                    item.setWarrantyPeriod(wrty);
                }

                item.setWarranty("NotStarted");

                ItemDto item1 = itemService.saveEntity(item);

                ItemConditionsMappingDto itemConditionsMappingDto = new ItemConditionsMappingDto();
                itemConditionsMappingDto.setItemId(item1.getId());
                itemConditionsMappingDto.setCondition(inward.getType());
                itemConditionsMappingDto.setMvnoId(inward.getMvnoId());

                itemConditionMappingService.saveEntity(itemConditionsMappingDto);

                ItemWarrantyMappingDto itemWarrantyMappingDto = new ItemWarrantyMappingDto();
                itemWarrantyMappingDto.setItemId(item1.getId());
                itemWarrantyMappingDto.setWarranty(item1.getWarranty());
                itemWarrantyMappingDto.setMvnoId(inward.getMvnoId());

                itemWarrantyMappingService.saveEntity(itemWarrantyMappingDto);
                entity.setItemId(item1.getId());
                return super.saveEntity(entity);
            } else {

                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Mac Address Already Exists, It Should Be Unique", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
    }

    @Override
    public boolean duplicateVerifyAtSave(String mac) throws Exception {
        boolean flag = false;
        List<Integer> mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (!Objects.equals(mac, null)) {
            mac = mac.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(mac);
            else count = repository.duplicateVerifyAtSave(mac, mvnoIds);
            if (count == 0) {
                flag = true;
            } else {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Entered a MAC " + mac + " is Already Exist", null);
            }
        }
        return flag;
    }


    public boolean duplicateVerifyAtSave1(String mac, Long itemId) throws Exception {
        boolean flag = false;
        List<Integer> mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (!Objects.equals(mac, null)) {
            mac = mac.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1)
                count = repository.duplicateVerifyAtSave(mac, itemId);
            else
                count = repository.duplicateVerifyAtSave(mac, mvnoIds, itemId);
            if (count == 0) {
                flag = true;
            } else {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Entered a MAC " + mac + " is Already Exist", null);
            }
        }
        return flag;
    }

    public List<InOutWardMACMapping> findByCustInventoryMappingId(Long id) {
        return repository.findAllByCustInventoryMappingId(id);
    }

    public List<InOutWardMACMapping> findByInventoryMappingId(Long id) {
        return repository.findAllByInventoryMappingId(id);
    }

    public List<InOutWardMACMapping> findbyinwardid(Long id) {
        return repository.findbyinwardid(id);
    }

    public List<InOutWardMACMapping> findbyinwardOfOutwardId(Long inwardOfOutwardId) {
        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
        BooleanExpression booleanExpression = qInOutWardMACMapping.inwardIdOfOutward.eq(inwardOfOutwardId).and(qInOutWardMACMapping.isDeleted.eq(false)).and(qInOutWardMACMapping.isForwarded.eq(1));
        List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(booleanExpression));
        return inOutWardMACMappingList;
    }

    public List<InOutWardMACMapping> findbyoutwardid(Long id) {
        return repository.findbyoutwardid(id);
    }


    public List<InOutWardMACMapping> delete(Integer id) throws Exception {
        return inOutWardMacRepo.deleteVerify(id);
    }

    public void saveNonSerializedItemsAfterApprovalInward(InwardApprovalDTO entity, String uom) throws Exception {
//        long startTime = System.currentTimeMillis();  // Start time for method execution
//        LOGGER.debug("Method execution started at: " + startTime);

        try {
            // Logging initial parameters
            LOGGER.debug("Processing non-serialized items for Inward Approval ID: " + entity.getId());

            // Set MVNO ID from current staff
            entity.setMvnoId(getMvnoIdFromCurrentStaff());

            // Fetch and map inwardDto from entity
            InwardDto inwardDto = inwardMapper.domainToDTO(inwardRepository.findById(entity.getId()).orElse(null), new CycleAvoidingMappingContext());
            inwardService.updateEntity(inwardDto);

            // Retrieve Product information
            Product pcId = productRepository.findById(inwardDto.getProductId().getId()).get();

            // Create NonSerializedItemDto
            NonSerializedItemDto nonSerializedItemDto = new NonSerializedItemDto();
            nonSerializedItemDto.setName(nonSerializedItemService.getRandomenumber("NSI", "-", ""));
            nonSerializedItemDto.setProductId(pcId.getId());
            nonSerializedItemDto.setNonSerializedItemcondition(inwardDto.getType());
            nonSerializedItemDto.setCurrentInwardId(inwardDto.getId());
            nonSerializedItemDto.setMvnoId(inwardDto.getMvnoId());
            nonSerializedItemDto.setOwnerId(inwardDto.getDestinationId());
            nonSerializedItemDto.setOwnerType(inwardDto.getDestinationType());
            nonSerializedItemDto.setCurrentInwardType(TypeConstants.FORWARDED);
            nonSerializedItemDto.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
            nonSerializedItemDto.setItemStatus(CommonConstants.UNALLOCATED);
            nonSerializedItemDto.setWarranty("NotStarted");

            // Set Quantity based on UOM (meter or kilometer)
            if (uom.equalsIgnoreCase("meter")) {
                nonSerializedItemDto.setQty(inwardDto.getInTransitQty());
            } else if (uom.equalsIgnoreCase("kilometer")) {
                nonSerializedItemDto.setQty(1000 * inwardDto.getInTransitQty());
            }

            // Calculate warranty period based on Product's expiry time and unit
            Integer wrty = pcId.getExpiryTime();
            if (pcId.getExpiryTimeUnit().equalsIgnoreCase("Month")) {
                wrty = 30 * wrty;
                nonSerializedItemDto.setWarrantyPeriod(wrty);
            } else {
                nonSerializedItemDto.setWarrantyPeriod(wrty);
            }

            // Save the NonSerializedItem entity
            NonSerializedItemDto dto = nonSerializedItemService.saveEntity(nonSerializedItemDto);

            // Map InOutward MAC for NSI
            saveAutoInOutwardMacMappingForNSI(dto, inwardDto);

        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle exceptions as needed
            System.err.println("Error occurred: " + ex.getMessage());
        }
    }


    @Transactional
    public void saveManualMacSerial(InwardSaveMacSerialDTO dto, boolean hasMac, boolean hasSerial, boolean bulkUpload, Inward entity) throws Exception {
        try {
            if (bulkUpload) {
                entity.setTotalMacSerial(0L);
            }
            entity.setMvnoId(getMvnoIdFromCurrentStaff());
            int remainingTotalMacQty = Math.toIntExact(entity.getInTransitQty() - entity.getTotalMacSerial());
            int macSerialListSize = dto.getMacSerialListDTOList().size();
            if (remainingTotalMacQty > 0 && entity.getTotalMacSerial() == 0 && macSerialListSize > 0) {
                entity.setTotalMacSerial((long) macSerialListSize);
            } else if (entity.getTotalMacSerial() != 0 && macSerialListSize > 0) {
                entity.setTotalMacSerial(entity.getTotalMacSerial() + macSerialListSize);
            }
            inwardRepository.save(entity);
            if (bulkUpload) {
                itemRepository.deleteByCurrentInwardIdAndProductId(entity.getId(), entity.getProductId().getId());
            }
            String serviceAreaNamesByWarehouseId = entity.getDestinationType().equalsIgnoreCase(CommonConstants.WAREHOUSE)
                    ? wareHouseManagmentServiceAreamappingRepo.findServiceAreaNamesByWarehouseId(entity.getDestinationId())
                    : null;
            boolean flag = inventorySpecificationRepo.countByInwardId(entity.getId()) > 0;
            processCsv(flag, entity, dto, serviceAreaNamesByWarehouseId, hasSerial, hasMac);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
    }

    private void processMessage(List<Item> finalItemListByInward, boolean flag, Inward entity, InwardSaveMacSerialDTO dto, String invenAssetOrg, String warehouceName) {
        try {
            List<Item> items = new ArrayList<>();
            for (MacSerialListDTO macSerialListDTO : dto.getMacSerialListDTOList()) {
                Integer wrty = entity.getProductId().getExpiryTime();
                if (entity.getProductId().getExpiryTimeUnit().equalsIgnoreCase("Month")) {
                    wrty = 30 * wrty;
                }
                Item item = new Item();
                if (macSerialListDTO.getMacAddress() == null) {
                    item.setMacAddress(macSerialListDTO.getMacAddress());
                } else if (macSerialListDTO.getMacAddress().equals("")) {
                    item.setMacAddress(null);
                } else if (!macSerialListDTO.getMacAddress().equals("")) {
                    item.setMacAddress(macSerialListDTO.getMacAddress());
                }
                item.setSerialNumber(macSerialListDTO.getSerialNumber());
                item.setName(itemService.getRandomenumber("SI", "-", macSerialListDTO.getSerialNumber()));
                item.setCondition(entity.getType());
                item.setMvnoId(entity.getMvnoId());
                item.setOwnerId(entity.getDestinationId());
                item.setOwnerType(entity.getDestinationType());
                item.setCurrentInwardType(TypeConstants.FORWARDED);
                item.setCurrentInwardId(entity.getId());
                item.setProductId(entity.getProductId().getId());
                item.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
                item.setItemStatus(CommonConstants.UNALLOCATED);
                item.setWarrantyPeriod(wrty);
                item.setWarranty("NotStarted");
                if (flag == true)
                    item.setInvenSpecId(entity.getId());
                else
                    item.setInvenSpecId(null);
                items.add(item);
            }
            items = itemRepository.saveAll(items);

            items.stream().forEach(savedItem -> {
                savedItem.setAssetId(getAssetIdWithFormate(savedItem.getId(), invenAssetOrg, warehouceName));
            });
            itemRepository.saveAll(items);
            itemRepository.deleteAll(finalItemListByInward);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getAssetIdWithFormate(Long itemId, String invenAssetOrg, String warehouceSA) {
        String assetId = "";
        if (invenAssetOrg != null) {
            assetId += invenAssetOrg;
        }
        if (warehouceSA != null) {
            assetId += warehouceSA;
        }
        if (itemId != null) {
            assetId += itemId;
        }
        return assetId;
    }

    /**
     * Method of Set values in Item Condition Entity / DTO
     * @param item1
     * @param inward
     * @return
     */
    public ItemConditionsMapping saveAutoItemConditionsMapping(Item item1, Inward inward) {
        try {
            ItemConditionsMapping itemConditionsMappingDto = new ItemConditionsMapping();
            itemConditionsMappingDto.setItemId(item1.getId());
            itemConditionsMappingDto.setCondition(inward.getType());
            itemConditionsMappingDto.setMvnoId(inward.getMvnoId());
            return itemConditionsMappingDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for Set values in Item Warrenty Entity / DTO
     * @param item1
     * @param inward
     * @return
     */
    public ItemWarrantyMapping saveAutoItemWarrantyMapping(Item item1, Inward inward) {
        try {
            ItemWarrantyMapping itemWarrantyMappingDto = new ItemWarrantyMapping();
            itemWarrantyMappingDto.setItemId(item1.getId());
            itemWarrantyMappingDto.setWarranty(item1.getWarranty());
            itemWarrantyMappingDto.setMvnoId(inward.getMvnoId());
            return itemWarrantyMappingDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for Set values in Inoutward / Item History Entity / DTO
     * @param item1
     * @param inward
     * @return
     */
    public InOutWardMACMapping saveAutoInOutwardMacMapping(Item item1, Inward inward) {
        try {
            InOutWardMACMapping inOutWardMACMapingDTO = new InOutWardMACMapping();
            inOutWardMACMapingDTO.setInwardId(inward.getId());
            inOutWardMACMapingDTO.setOutwardId(null);
            inOutWardMACMapingDTO.setCustInventoryMappingId(null);
            inOutWardMACMapingDTO.setMacAddress(null);
            inOutWardMACMapingDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            inOutWardMACMapingDTO.setStatus(CommonConstants.ACTIVE_STATUS);
            inOutWardMACMapingDTO.setIsForwarded(0);
            inOutWardMACMapingDTO.setIsReturned(0);
            inOutWardMACMapingDTO.setItemId(item1.getId());
            return inOutWardMACMapingDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void checkItemsForInwardOfOutward(List<InOutWardMACMapping> list) throws Exception {
//        long startTime = System.currentTimeMillis();
//        LOGGER.info("checkItemsForInwardOfOutward started at: {}" + startTime);

        if (list == null || list.isEmpty()) {
            LOGGER.warn("Received an empty list for processing.");
            return;
        }

        ForkJoinPool customThreadPool = new ForkJoinPool(Math.min(list.size(), Runtime.getRuntime().availableProcessors() * 2));

        try {
            // Update items in parallel
            customThreadPool.submit(() -> list.parallelStream().forEach(mapping -> {
                try {
                    if (mapping.getMacAddress() != null) {
                        itemService.updateItemMacAndSerial(mapping.getId(), mapping.getMacAddress(), mapping.getSerialNumber());
                    } else {
                        itemService.updateItemSerial(mapping.getId(), mapping.getSerialNumber());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Error updating item MAC/Serial: ID = {}, Error = {}" + mapping.getId() + e.getMessage(), e);
                }
            })).get(); // Wait for completion
            Long outwardId = list.get(0).getOutwardId();
            if (outwardId == null) {
                LOGGER.warn("Outward ID is null, skipping further processing.");
                return;
            }

            int updatedRows = inwardRepository.updateTotalMacSerial(outwardId, list.size(), CommonConstants.ACTIVE_STATUS);

            if (updatedRows == 0) {
                LOGGER.warn("Inward record not found for outward ID: " + outwardId);
            }
            Optional<Long> inwardIdOpt = inwardRepository.findInwardIdByOutwardId(CommonConstants.ACTIVE_STATUS, outwardId);
            Optional<Long> destinationIdOpt = inwardRepository.findDestinationIdByOutwardId(CommonConstants.ACTIVE_STATUS, outwardId);
            Optional<String> destinationTypeOpt = inwardRepository.findDestinationTypeByOutwardId(CommonConstants.ACTIVE_STATUS, outwardId);

            Long inwardId = inwardIdOpt.orElse(null);
            Long destinationId = destinationIdOpt.orElse(null);
            String destinationType = destinationTypeOpt.orElse(null);
            // Fetch existing mappings in batch
            List<Long> itemIds = list.stream().map(InOutWardMACMapping::getId).collect(Collectors.toList());
            List<InOutWardMACMapping> inOutWardMACMappings = inOutWardMacRepo.findAllByItemIdInAndIsForwarded(itemIds, 0);

            // Use maps for quick lookups
            Map<Long, InOutWardMACMapping> mappingById = repository.findAllById(
                    inOutWardMACMappings.stream().map(InOutWardMACMapping::getId).collect(Collectors.toList())
            ).stream().collect(Collectors.toConcurrentMap(InOutWardMACMapping::getId, mapping -> mapping));

            Map<Long, Item> itemsById = itemRepository.findAllById(
                    inOutWardMACMappings.stream().map(InOutWardMACMapping::getItemId).collect(Collectors.toList())
            ).stream().collect(Collectors.toConcurrentMap(Item::getId, item -> item));

            // Update mappings and items in parallel
            customThreadPool.submit(() -> inOutWardMACMappings.parallelStream().forEach(mapping -> {
                try {
                    InOutWardMACMapping existingMapping = mappingById.get(mapping.getId());
                    if (existingMapping != null) {
                        existingMapping.setIsForwarded(1);
                        existingMapping.setInwardIdOfOutward(inwardId);
                    }

                    Item item = itemsById.get(mapping.getItemId());
                    if (item != null) {
//                        item.setCurrentInwardId(inward.getId());
                        item.setOwnerId(destinationId);
                        item.setOwnerType(destinationType);
                        item.setItemStatus(CommonConstants.UNALLOCATED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Error processing mapping: ID = {}, Error = {}" + mapping.getId() + e.getMessage() + e);
                }
            })).get(); // Wait for completion

            // Batch save
            repository.saveAll(mappingById.values());
            itemRepository.saveAll(itemsById.values());

            // Create and batch save new mappings
            List<InOutWardMACMapping> newMappings = list.parallelStream()
                    .filter(Objects::nonNull)
                    .map(mapping -> {
                        try {
                            if (inwardId == null || mapping.getOutwardId() == null || mapping.getId() == null) {
                                LOGGER.warn("Skipping mapping due to null values: {}" + mapping);
                                return null;
                            }
                            return new InOutWardMACMapping(
                                    inwardId,
                                    mapping.getOutwardId(),
                                    mapping.getId(),
                                    mapping.getMacAddress(),
                                    mapping.getSerialNumber(),
                                    getLoggedInUser() != null ? getLoggedInUser().getFirstName() : "admin",
                                    getLoggedInUserId(),
                                    LocalDateTime.now()
                            );
                        } catch (Exception e) {
                            LOGGER.error("Error creating new mapping: {}" + e.getMessage() + e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            repository.saveAll(newMappings);

            // Update Outward
            Outward outward = outwardRepository.findById(outwardId)
                    .orElseThrow(() -> new Exception("Outward not found for ID: " + outwardId));

            outward.setSelectedItems((outward.getSelectedItems() == null ? 0 : outward.getSelectedItems()) + list.size());
            outwardRepository.save(outward);

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error in checkItemsForInwardOfOutward: ", e);
        } finally {
            customThreadPool.shutdown();
//            long endTime = System.currentTimeMillis();
//            System.out.println("checkItemsForInwardOfOutward ended at: {}" + endTime);
//            System.out.println("Total execution time: {} ms" + (endTime - startTime));
        }
    }


    public void saveAutoInOutwardMacMappingForNSI(NonSerializedItemDto dto, InwardDto inwardDto) {
        try {
            InOutWardMACMapingDTO inOutWardMACMapingDTO = new InOutWardMACMapingDTO();
            inOutWardMACMapingDTO.setInwardId(inwardDto.getId());
            inOutWardMACMapingDTO.setOutwardId(null);
            inOutWardMACMapingDTO.setCustInventoryMappingId(null);
            inOutWardMACMapingDTO.setMacAddress(null);
            inOutWardMACMapingDTO.setMvnoId(getMvnoIdFromCurrentStaff());
            inOutWardMACMapingDTO.setStatus(CommonConstants.ACTIVE_STATUS);
            inOutWardMACMapingDTO.setIsForwarded(0);
            inOutWardMACMapingDTO.setIsReturned(0);
            inOutWardMACMapingDTO.setNonSerializedItemId(dto.getId());
            super.saveEntity(inOutWardMACMapingDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void checkNonSerializedItemsForInwardOfOutward(List<InOutWardMACMapping> list, OutwardDto outwardDto) throws Exception {
        try {
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            List<InOutWardMACMapping> resultInOutWardMACMappingList = new ArrayList<>();
            QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
            List<NonSerializedItem> resultNonSerializedItemList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                BooleanExpression booleanExpressionInoutward = qInOutWardMACMapping.isDeleted.eq(false).and(qInOutWardMACMapping.nonSerializedItemId.eq(list.get(i).getId())).and(qInOutWardMACMapping.isForwarded.eq(0));
                List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(booleanExpressionInoutward));
                resultInOutWardMACMappingList.addAll(inOutWardMACMappingList);
            }
            for (int j = 0; j < list.size(); j++) {
                BooleanExpression booleanExpressionNonSerializedItem = qNonSerializedItem.isDeleted.eq(false).and(qNonSerializedItem.id.eq(list.get(j).getId()));
                List<NonSerializedItem> nonSerializedItemList = IterableUtils.toList(nonSerializedItemRepository.findAll(booleanExpressionNonSerializedItem));
                resultNonSerializedItemList.addAll(nonSerializedItemList);
            }
            Long selectedNonSerializedItemQTY = 0L;
            for (int j = 0; j < resultNonSerializedItemList.size(); j++) {
                selectedNonSerializedItemQTY = selectedNonSerializedItemQTY + resultNonSerializedItemList.get(j).getQty();
            }
            if (selectedNonSerializedItemQTY > outwardDto.getInTransitQty()) {
                checkNewInwardFromOutwardDetail(resultInOutWardMACMappingList, resultNonSerializedItemList, outwardDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void checkNewInwardFromOutwardDetail(List<InOutWardMACMapping> resultInOutWardMACMappingList, List<NonSerializedItem> resultNonSerializedItemList, OutwardDto outwardDto) throws Exception {
        try {
            QInward qInward = QInward.inward;
            BooleanExpression booleanExpressionInwardOfOutward = qInward.isDeleted.eq(false).and(qInward.outwardId.id.eq(outwardDto.getId()));
            List<Inward> inwards = IterableUtils.toList(inwardRepository.findAll(booleanExpressionInwardOfOutward));
            for (int i = 0; i < resultNonSerializedItemList.size(); i++) {
                if (outwardDto.getInTransitQty() > inwards.get(0).getInTransitQty() || outwardDto.getInTransitQty().equals(inwards.get(0).getInTransitQty())) {
                    NonSerializedItem nonSerializedItem = resultNonSerializedItemList.get(i);
                    InOutWardMACMapping inOutWardMACMapping = resultInOutWardMACMappingList.get(i);
                    updateNonSerializedItemQty(inOutWardMACMapping, nonSerializedItem, outwardDto, inwards);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void updateNonSerializedItemQty(InOutWardMACMapping inOutWardMACMapping, NonSerializedItem nonSerializedItem, OutwardDto outwardDto, List<Inward> inwards) throws Exception {
        try {
            Inward inward = inwardRepository.findById(inwards.get(0).getId()).get();
            Long newQty = 0L;
            if (Objects.equals(nonSerializedItem.getQty(), outwardDto.getInTransitQty())) {
                newQty = nonSerializedItem.getQty();
                nonSerializedItem.setQty(nonSerializedItem.getQty());
                nonSerializedItemRepository.save(nonSerializedItem);
                inward.setAssignNonSerializedItemQty(newQty);
                inwardRepository.save(inward);
            } else if (nonSerializedItem.getQty() < outwardDto.getInTransitQty()) {
                if (inward.getAssignNonSerializedItemQty() == 0) {
                    newQty = nonSerializedItem.getQty();
                    nonSerializedItem.setQty(nonSerializedItem.getQty());
                    nonSerializedItemRepository.save(nonSerializedItem);
                    inward.setAssignNonSerializedItemQty(newQty);
                    inwardRepository.save(inward);
                } else {
                    if (!nonSerializedItem.getQty().equals(inward.getAssignNonSerializedItemQty())) {
                        newQty = nonSerializedItem.getQty() - inward.getAssignNonSerializedItemQty();
                        nonSerializedItem.setQty(nonSerializedItem.getQty() - inward.getAssignNonSerializedItemQty());
                        nonSerializedItemRepository.save(nonSerializedItem);
                        inward.setAssignNonSerializedItemQty(newQty + inward.getAssignNonSerializedItemQty());
                        inwardRepository.save(inward);
                    } else if (inward.getAssignNonSerializedItemQty() < outwardDto.getInTransitQty()) {
                        newQty = outwardDto.getInTransitQty() - nonSerializedItem.getQty();
                        nonSerializedItem.setQty(nonSerializedItem.getQty() - newQty);
                        nonSerializedItemRepository.save(nonSerializedItem);
                        inward.setAssignNonSerializedItemQty(newQty + inward.getAssignNonSerializedItemQty());
                        inwardRepository.save(inward);
                    }
                }
            } else if (nonSerializedItem.getQty() > outwardDto.getInTransitQty()) {
                newQty = outwardDto.getInTransitQty();
                nonSerializedItem.setQty(nonSerializedItem.getQty() - outwardDto.getInTransitQty());
                nonSerializedItemRepository.save(nonSerializedItem);
                inward.setAssignNonSerializedItemQty(newQty + inward.getAssignNonSerializedItemQty());
                inwardRepository.save(inward);
            }
            NonSerializedItemDto dto = saveNewNonSerializedItem(inward);
            saveNonSerializedItemHierarchy(nonSerializedItem, dto);
//            updateInoutMappingBYOldInwardForNonSerializedItem(resultInOutWardMACMappingList, )
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public NonSerializedItemDto saveNewNonSerializedItem(Inward inward) throws Exception {
        try {
//            InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(inward.getId());
            InwardDto inwardDto = inwardMapper.domainToDTO(inwardRepository.findById(inward.getId()).orElse(null), new CycleAvoidingMappingContext());
            Product pcId = productRepository.findById(inwardDto.getProductId().getId()).get();
            NonSerializedItemDto newNonSerializedItemDto = new NonSerializedItemDto();
            newNonSerializedItemDto.setName(nonSerializedItemService.getRandomenumber("NSI", "-", ""));
            newNonSerializedItemDto.setProductId(pcId.getId());
            newNonSerializedItemDto.setNonSerializedItemcondition(null);
            newNonSerializedItemDto.setCurrentInwardId(inwardDto.getId());
            newNonSerializedItemDto.setMvnoId(inwardDto.getMvnoId());
            newNonSerializedItemDto.setOwnerId(inwardDto.getDestinationId());
            newNonSerializedItemDto.setOwnerType(inwardDto.getDestinationType());
            newNonSerializedItemDto.setCurrentInwardType(TypeConstants.FORWARDED);
            newNonSerializedItemDto.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
            newNonSerializedItemDto.setItemStatus(CommonConstants.UNALLOCATED);
            newNonSerializedItemDto.setWarranty("NotStarted");
            newNonSerializedItemDto.setQty(inwardDto.getAssignNonSerializedItemQty());
            Integer wrty = pcId.getExpiryTime();
            if (pcId.getExpiryTimeUnit().equalsIgnoreCase("Month")) {
                wrty = 30 * wrty;
                newNonSerializedItemDto.setWarrantyPeriod(wrty);
            } else {
                newNonSerializedItemDto.setWarrantyPeriod(wrty);
            }
            NonSerializedItemDto dto = nonSerializedItemService.saveEntity(newNonSerializedItemDto);
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void saveNonSerializedItemHierarchy(NonSerializedItem nonSerializedItem, NonSerializedItemDto dto) {
        NonSerializedItemHierarchy nonSerializedItemHierarchy = new NonSerializedItemHierarchy();
        nonSerializedItemHierarchy.setParentItemId(nonSerializedItem.getId());
        nonSerializedItemHierarchy.setChildItemId(dto.getId());
        nonSerializedItemHierarchy.setMvnoId(getMvnoIdFromCurrentStaff());
        nonSerializedItemHierarchy.setQty(dto.getQty());
        nonSerializedItemHierarchyRepository.save(nonSerializedItemHierarchy);
    }

    public void updateMacSerialByItem(Long itemId, String macAddress, String serialNumber) throws Exception {
        try {
//            List<InOutWardMACMapping> inOutWardMACMappingList = inOutWardMacRepo.findMappingsByItemId(itemId);
//            if (inOutWardMACMappingList.isEmpty()) {
//                return; // No records to update
//            }
//
//            // Use a thread-safe list for updates
//            List<InOutWardMACMapping> updatedMappings = Collections.synchronizedList(new ArrayList<>());
//
//            inOutWardMACMappingList.parallelStream().forEach(mapping -> {
//                mapping.setMacAddress(macAddress);
//                mapping.setSerialNumber(serialNumber);
//                updatedMappings.add(mapping);
//            });
//
//            // Batch update the modified records
//            inOutWardMacRepo.saveAll(updatedMappings);

            int updatedRows = inOutWardMacRepo.updateMappings(itemId, macAddress, serialNumber);
            if (updatedRows == 0) {
                throw new Exception("Item History not found for ID: " + itemId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating MAC and Serial Number", e);
        }
    }


    public void updateSerialByItem(Long itemId, String serialNumber) {
        try {
//            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
//            BooleanExpression condition = qInOutWardMACMapping.isDeleted.eq(false)
//                    .and(qInOutWardMACMapping.itemId.eq(itemId));
//
//            List<InOutWardMACMapping> mappings = IterableUtils.toList(inOutWardMacRepo.findAll(condition));
//
//            if (!mappings.isEmpty()) {
//                mappings.forEach(mapping -> mapping.setSerialNumber(serialNumber));
//                inOutWardMacRepo.saveAll(mappings);
//            }

            int updatedRows = inOutWardMacRepo.updateMappings(itemId, serialNumber);
            if (updatedRows == 0) {
                throw new Exception("Item History not found for ID: " + itemId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating serial number for item ID: " + itemId, e);
        }
    }


    public CustomerInventoryMappingDto updateInventoryMapping(CustomerInventoryMapping customerInventoryMapping, Long mappingId, Long customerInventoryId, Long customerId, boolean isflag, String remark) throws Exception {

        customerInventoryMapping.setQty(customerInventoryMapping.getQty() - 1);
        Customers customers = customersRepository.findById(customerId.intValue())
                .orElseThrow(() -> new NotFoundException("Customer is not found by id: " + customerId));
        removeMappingWithCustomerInventory(mappingId, customers);
        Product product = productService.getRepository().getOne(customerInventoryMapping.getProduct().getId());
        if (Objects.nonNull(product)) {
            RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
            recordPaymentPojo.setAmount(Double.valueOf(product.getRefurburshiedProductRefAmountInWarranty()));
            recordPaymentPojo.setCustomerid(customerInventoryMapping.getCustomer().getId());
            List<Integer> invoiceIds = new ArrayList<>();
            invoiceIds.add(0);
            recordPaymentPojo.setInvoiceId(invoiceIds);
            recordPaymentPojo.setPaymentdate(LocalDate.now());
            recordPaymentPojo.setPaymode("Cash");
            recordPaymentPojo.setPaytype("advance");
            recordPaymentPojo.setType("creditnote");
            recordPaymentPojo.setRemark("Refund amount for removing Product :-" + product.getName());
//            creditDocService.save(recordPaymentPojo, false, false, false);

        }

        if (customerInventoryMapping.getInwardId() != null) {
            Inward inward = inwardRepository.findById(customerInventoryMapping.getInwardId()).get();
            if (inward != null) {
                inward.setUnusedQty(inward.getUnusedQty() + 1);
                inward.setUsedQty(inward.getUsedQty() - 1);
                inwardRepository.save(inward);
            }
        }

        if (customerInventoryMapping.getExternalItemId() != null) {
            ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(customerInventoryMapping.getExternalItemId()).get();
            if (externalItemManagement != null) {
                externalItemManagement.setUnusedQty(externalItemManagement.getUnusedQty() + 1);
                externalItemManagement.setUsedQty(externalItemManagement.getUsedQty() - 1);
                externalItemManagementRepository.save(externalItemManagement);
            }
            for (int i = 0; i < customerInventoryMapping.getExternalItemMacSerialMappings().size(); i++) {
                ExternalItemMacSerialMapping externalItemMacSerialMapping = externalItemMacSerialMappingRepo.findById(customerInventoryMapping.getExternalItemMacSerialMappings().get(i).getId()).get();
                if (externalItemMacSerialMapping != null) {
                    externalItemMacSerialMapping.setCustInventoryMappingId(null);
                    externalItemMacSerialMappingRepo.save(externalItemMacSerialMapping);
                }
            }
        }
        InOutWardMACMapping inOutWardMACMapping = repository.findById(mappingId).get();
        Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
             /*if(customers != null) {
                if (!customers.getIstrialplan() && item.getWarranty().equalsIgnoreCase("InWarranty")) {
                    item.setItemStatus(CommonConstants.RETURNED);
                    item.setWarranty("Paused");
                    item.setCondition(CommonConstants.DEFECTIVE);
                    item.setIntransiantWarrenty(null);
                    itemRepository.save(item);
                }*/
        if (!Objects.isNull(item)) {
            if (item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED) || item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED)) {
                itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                    itemService.updateItemWarranty(item, "Paused");
                }
                NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(item.getId(), customerInventoryId);
                if (!Objects.isNull(networkDevices)) {
                    networkDevices.setIsDeleted(true);
                    networkDeviceRepository.save(networkDevices);
                }
//                List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
//                ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
//                itemReturnDTO.setId(item.getId());
//                itemReturnDTOList.add(itemReturnDTO);
                itemService.returnItemfromStaffremove(item);
                if (getLoggedInUser().getPartnerId() != 1) {
                    item.setOwnerType(CommonConstants.PARTNER);
                    item.setOwnerId(Long.valueOf(getLoggedInUser().getPartnerId()));
                } else {
                    item.setOwnerType(CommonConstants.STAFF);
                    item.setItemStatus(CommonConstants.STAFF_ALLOCATED);
                    item.setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                }
                itemRepository.save(item);
            }
            if (item.getOwnershipType().equalsIgnoreCase("Sold") && !isflag) {
                itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryId);
                if (!Objects.isNull(networkDevices)) {
                    networkDevices.setIsDeleted(true);
                    networkDeviceRepository.save(networkDevices);
                }

            }
            if (item.getOwnershipType().equalsIgnoreCase("Sold") && isflag == true) {
                itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryId);
                if (!Objects.isNull(networkDevices)) {
                    networkDevices.setIsDeleted(true);
                    networkDeviceRepository.save(networkDevices);
                }
                item.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
                if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                    itemService.updateItemWarranty(item, "Paused");
                }
//                List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
//                ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
//                itemReturnDTO.setId(item.getId());
//                itemReturnDTOList.add(itemReturnDTO);
                itemService.returnItemfromStaffremove(item);
                if (getLoggedInUser().getPartnerId() != 1) {
                    item.setOwnerType(CommonConstants.PARTNER);
                    item.setOwnerId(Long.valueOf(getLoggedInUser().getPartnerId()));
                } else {
                    item.setOwnerType(CommonConstants.STAFF);
                    item.setItemStatus(CommonConstants.STAFF_ALLOCATED);
                    item.setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                }
                itemRepository.save(item);

            }

            if (item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.CUSTOMER_OWNED) ||
                    item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.TEMPORARY) ||
                    item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.PARTNER_OWNED)) {
                itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryId);
                if (!Objects.isNull(networkDevices)) {
                    networkDevices.setIsDeleted(true);
                    networkDeviceRepository.save(networkDevices);
                }
            }
        }
        //deleteCustomerInventory
        customerInventoryMapping.setIsDeleted(true);
        customerInventoryMapping.setApprovalRemark(remark);
        customerInventoryMapping.setNextApprover(null);
        customerInventoryMapping.setStatus("REMOVED");
        customerInventoryMapping.setTeamHierarchyMappingId(null);
        customerInventoryMappingRepo.save(customerInventoryMapping);


        try {
            List<Item> items = itemRepository.findAllById(inOutWardMACMapping.getItemId());
            Return aReturn = new Return();
            aReturn.setMac_name(items.get(0).getMacAddress());
            aReturn.setItem_status(items.get(0).getItemStatus());
            aReturn.setItem_condition(items.get(0).getCondition());
            aReturn.setProduct_id(items.get(0).getProductId());
            aReturn.setCurrent_inward_type(items.get(0).getCurrentInwardType());
            aReturn.setCurrent_inward_id(items.get(0).getCurrentInwardId());
            aReturn.setSerial_no(items.get(0).getSerialNumber());
            aReturn.setProduct_name(items.get(0).getName());
            aReturn.setCust_id(Long.parseLong(customerInventoryMapping.getCustomer().getId().toString()));
            returnRepo.save(aReturn);

        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
        return customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext());
    }

    public GenericDataDTO genearateRemoveInventoryRequest(Long macmappingId, Long customerInventoryId, Long customerId, boolean isflag, Long revisedcharge) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            GenerateRemoveRequest generateRemoveRequest = new GenerateRemoveRequest();
            generateRemoveRequest.setMacmappingid(macmappingId);
            generateRemoveRequest.setCustomerinventoryId(customerInventoryId);
            generateRemoveRequest.setCustomerid(customerId);
            generateRemoveRequest.setStaffid(getLoggedInUserId());
            generateRemoveRequest.setFlag(isflag);
            generateRemoveRequest.setRequestStatus("PENDING");
            generateRemoveRequest.setDeleted(false);
            generateRemoveRequest.setRevisedcharge(revisedcharge);
            generateRemoveRequest = generateRemoveRequestRepo.save(generateRemoveRequest);
            genericDataDTO.setData(generateRemoveRequest);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Generate Remove Inventory Request Successfully");
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return genericDataDTO;
    }

    @Transactional
    public GenericDataDTO removeInventoryWorkFlowNew(Long mappingId, Long customerInventoryId, Long customerId, Integer nextStaff, String remark, boolean isApprove) {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            CustomerInventoryMappingDto entity = customerInventoryMappingMapper.domainToDTO(customerInventoryMappingRepo.findById(customerInventoryId).orElse(null), new CycleAvoidingMappingContext());
            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(customerInventoryId)
                    .orElseThrow(() -> new NotFoundException("Customer Inventory is not found by id: " + customerInventoryId));
            Item item = itemRepository.findById(customerInventoryMapping.getItemId()).get();
            Product product = productRepository.findById(entity.getProductId()).get();
            Long pcId = productRepository.findProductCategoryIdByProductId(entity.getProductId());
            boolean hasmac = productCategoryRepository.findHasMacById(pcId);
            boolean hasserial = productCategoryRepository.findHasSerialById(pcId);
            StaffUser loggedInUser = staffUserRepository.findLightStaffUserById(Integer.valueOf(getLoggedInUserId())).get();
//            System.out.println(loggedInUser.getFullName());
            Customers customers = customersRepository.findById(entity.getCustomerId()).get();
            QGenerateRemoveRequest qGenerateRemoveRequest = QGenerateRemoveRequest.generateRemoveRequest;
            BooleanExpression booleanExpression = qGenerateRemoveRequest.customerid.eq(customerId).and(qGenerateRemoveRequest.customerinventoryId.eq(customerInventoryId)).and(qGenerateRemoveRequest.macmappingid.eq(mappingId)).and(qGenerateRemoveRequest.isDeleted.eq(false));
            GenerateRemoveRequest generateRemoveRequests = generateRemoveRequestRepo.findOne(booleanExpression).orElse(null);
//            boolean isflag = generateRemoveRequests.isFlag();
            if (Objects.equals(loggedInUser.getUsername(), "admin") || Objects.equals(loggedInUser.getUsername(), "superadmin")) {
                if (isApprove) {
                    entity.setStatus("ACTIVE");
                    inOutWardMACService.removeInventory(mappingId, customerInventoryMapping, customers, remark, product);
                    entity.setApprovalRemark(remark);
                    entity.setFlag("approved");
                    entity.setNextApproverId(null);
                    entity.setPreviousApproveId(null);
                    entity.setTeamHierarchyMappingId(null);
                    entity.setIsDeleted(true);
                    generateRemoveRequests.setRequestStatus("APPROVE");
                    generateRemoveRequestRepo.save(generateRemoveRequests);
//                    updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "APPROVE");
                    genericDataDTO.setResponseMessage("Remove Inventory Successfully");
                    customerInventoryMapping = customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                    List<Long> deviceIds = networkDeviceRepository.findAllIdsByCustomerInventoryId(customerInventoryMapping.getId());
                    if (!deviceIds.isEmpty()) {
                        List<NetworkDeviceBind> deviceBindsToDelete = networkdeviceBindRepository.findByDeviceIds(deviceIds);
                        if (!deviceBindsToDelete.isEmpty()) {
                            networkdeviceBindRepository.deleteAll(deviceBindsToDelete);
                        }
                    }
                    genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext()));
                    ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                    messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                    kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                    itemMessage.setMessage("Serialized Item after Approval of Remove Inventory Item");
                    item.setRemoveFrom("Customer");
                    //Todo: Code for Approve Remove Inventory Serialized Item Request for Integration
//                    ItemMessage itemMessage2 = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                    messageSender.send(itemMessage2,RabbitMqConstants.QUEUE_APIGW_APPROVE_REMOVE_INVENTORY_SERIALIZEDITEM_REQUEST_IN_INTEGRATION);
                    CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
                    kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
//                    messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
//                    if (customerInventoryMapping.getPlanId() != null || customerInventoryMapping.getPlanGroupId() != null) {
                    /**
                     * Send Approve Inventory From Inventory to CMS
                     */
                    InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
                    inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
                    inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
                    inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
                    inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
                    inventorySerialNumberMessage.setOperation(CommonConstants.REMOVE_INVETORIES);
                    inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
                    inventorySerialNumberMessage.setCustInventoryId(customerInventoryMapping.getId());
                    inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
                    inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
                    inventorySerialNumberMessage.setItemId(item.getId());
                    inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                    inventorySerialNumberMessage.setItemName(item.getName());
                    inventorySerialNumberMessage.setStatus("ACTIVE");
                    inventorySerialNumberMessage.setQty(customerInventoryMapping.getQty());
                    inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
                    kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
//                    messageSender.send(inventorySerialNumberMessage, RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS);
//                    }
                } else {
                    entity.setStatus("ACTIVE");
                    entity.setFlag("rejected");
                    entity.setApprovalRemark(remark);
                    entity.setNextApproverId(null);
                    entity.setPreviousApproveId(null);
                    entity.setTeamHierarchyMappingId(null);
                    //updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "REJECTED");
                    customerInventoryMapping = customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                    generateRemoveRequests.setDeleted(true);
                    generateRemoveRequests.setRequestStatus("REJECTED");
                    generateRemoveRequestRepo.save(generateRemoveRequests);
                    genericDataDTO.setResponseMessage("Rejected Remove Inventory Successfully");
                    genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext()));
                }
                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + entity.getApprovalRemark() + "\n" + entity.getFlag() + " By :- " + loggedInUser.getUsername());
                customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            }

            if (clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApprove, false, customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                StaffUser assignedUser = null;
                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                    StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
                    StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                    assignedUser = staffUser;
                    entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    entity.setNextApproverId(Integer.valueOf(map.get("staffId")));
                    entity.setPreviousApproveId(getLoggedInUserId());
                    entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    entity.setStatus("PENDING");
                    generateRemoveRequests.setRequestStatus("PENDING");
                    generateRemoveRequestRepo.save(generateRemoveRequests);
//                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, Math.toIntExact(entity.getId()), entity.getProductName(), assignedUser.getId(), assignedUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REMOVE, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApprove ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApprove ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
                } else {
                    if (isApprove) {
                        entity.setStatus("ACTIVE");
                        inOutWardMACService.removeInventory(mappingId, customerInventoryMapping, customers, remark, product);
                        entity.setFlag("approved");
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
                        entity.setIsDeleted(true);
                        generateRemoveRequests.setRequestStatus("APPROVE");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        customerInventoryMapping = customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                        List<Long> deviceIds = networkDeviceRepository.findAllIdsByCustomerInventoryId(customerInventoryMapping.getId());
                        if (!deviceIds.isEmpty()) {
                            List<NetworkDeviceBind> deviceBindsToDelete = networkdeviceBindRepository.findByDeviceIds(deviceIds);
                            if (!deviceBindsToDelete.isEmpty()) {
                                networkdeviceBindRepository.deleteAll(deviceBindsToDelete);
                            }
                        }
                        ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                        messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                        kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                        CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
                        kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
//                        if (customerInventoryMapping.getPlanId() != null || customerInventoryMapping.getPlanGroupId() != null) {
                        /**
                         * Send Approve Inventory From Inventory to CMS
                         */
                        InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
                        inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
                        inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
                        inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
                        inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
                        inventorySerialNumberMessage.setOperation(CommonConstants.REMOVE_INVETORIES);
                        inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
                        inventorySerialNumberMessage.setCustInventoryId(customerInventoryMapping.getId());
                        inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
                        inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
                        inventorySerialNumberMessage.setItemId(item.getId());
                        inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                        inventorySerialNumberMessage.setItemName(item.getName());
                        inventorySerialNumberMessage.setStatus("ACTIVE");
                        inventorySerialNumberMessage.setQty(customerInventoryMapping.getQty());
                        inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
                        kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
//                        messageSender.send(inventorySerialNumberMessage, RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS);
//                        }
                        genericDataDTO.setResponseMessage("Remove Inventory Successfully");
                        genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext()));
                    } else {
                        entity.setStatus("ACTIVE");
                        entity.setFlag("rejected");
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
                        entity.setIsDeleted(true);
                        // updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "REJECTED");
                        customerInventoryMapping = customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                        generateRemoveRequests.setDeleted(true);
                        generateRemoveRequests.setRequestStatus("REJECTED");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        genericDataDTO.setResponseMessage("Rejected Remove Inventory Successfully");
                        genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext()));
                    }
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApprove ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApprove ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
                }
                //TAT functionality
//                if (assignedUser != null) {
//                    if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                        if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
//                            map.put("tat_id", map.get("current_tat_id"));
//                        tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, entity.getId().intValue(), null);
//                    }
//                }
            } else {
                Map<String, Object> map = hierarchyService.getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApprove, false, customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                if (map.containsKey("assignableStaff")) {
//                    StaffUser staffUser = staffUserService.get(nextStaff);
                    StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(nextStaff)).get();
                    genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    if (isApprove) {
                        entity.setFlag("approved");
                        entity.setApprovalRemark(remark);
                        entity.setStatus("PENDING FOR REMOVE");
                        generateRemoveRequests.setRequestStatus("PENDING");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        genericDataDTO.setResponseMessage("Approved Successfully");

                    } else {
                        entity.setFlag("rejected");
                        entity.setApprovalRemark(remark);
                        entity.setStatus("PENDING FOR REMOVE");
//                        generateRemoveRequests.setDeleted(true);
                        generateRemoveRequests.setRequestStatus("PENDING");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        genericDataDTO.setResponseMessage("Rejected Remove Inventory Successfully");
                    }
                    customerInventoryMapping = customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + entity.getApprovalRemark() + "\n" + entity.getFlag() + " By :- " + staffUser.getUsername());
                    genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext()));
                    return genericDataDTO;
                } else {
                    if (isApprove) {
                        entity.setStatus("ACTIVE");
                        inOutWardMACService.removeInventory(mappingId, customerInventoryMapping, customers, remark, product);
                        entity.setFlag("approved");
                        entity.setApprovalRemark(remark);
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
                        entity.setIsDeleted(true);
//                        updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "APPROVE");
                        customerInventoryMapping = customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                        List<Long> deviceIds = networkDeviceRepository.findAllIdsByCustomerInventoryId(customerInventoryMapping.getId());
                        if (!deviceIds.isEmpty()) {
                            List<NetworkDeviceBind> deviceBindsToDelete = networkdeviceBindRepository.findByDeviceIds(deviceIds);
                            if (!deviceBindsToDelete.isEmpty()) {
                                networkdeviceBindRepository.deleteAll(deviceBindsToDelete);
                            }
                        }
                        ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                        messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                        kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                        CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
                        kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
//                        if (customerInventoryMapping.getPlanId() != null || customerInventoryMapping.getPlanGroupId() != null) {
                        /**
                         * Send Approve Inventory From Inventory to CMS
                         */
                        InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
                        inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
                        inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
                        inventorySerialNumberMessage.setItemId(item.getId());
                        inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                        inventorySerialNumberMessage.setItemName(item.getName());
                        inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
                        inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
                        inventorySerialNumberMessage.setOperation(CommonConstants.REMOVE_INVETORIES);
                        inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
                        inventorySerialNumberMessage.setCustInventoryId(customerInventoryMapping.getId());
                        inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
                        inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
                        inventorySerialNumberMessage.setStatus("ACTIVE");
                        inventorySerialNumberMessage.setQty(customerInventoryMapping.getQty());
                        inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
                        kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
//                        messageSender.send(inventorySerialNumberMessage, RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS);
//                        }
                        generateRemoveRequests.setRequestStatus("APPROVE");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        genericDataDTO.setResponseMessage("Remove Inventory Successfully");
                        genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext()));
                    } else {
                        entity.setStatus("ACTIVE");
                        entity.setApprovalRemark(remark);
                        entity.setFlag("rejected");
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
//                        updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "REJECTED");
                        customerInventoryMapping = customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                        generateRemoveRequests.setDeleted(true);
                        generateRemoveRequests.setRequestStatus("REJECTED");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        genericDataDTO.setResponseMessage("Rejected Remove Inventory Successfully");
                        genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext()));
                    }
                    entity.setPreviousApproveId(null);
                    customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + entity.getApprovalRemark() + "\n" + entity.getFlag() + " By :- " + loggedInUser.getUsername());
                    CustomerInventoryMappingDto customerInventoryMappingDto = (CustomerInventoryMappingDto) genericDataDTO.getData();
                    boolean itemAssemblyflag = customerInventoryMappingDto.isItemAssemblyflag();
                    Long custInventoryId = customerInventoryMappingDto.getId();
                    Long externalItem = customerInventoryMappingDto.getExternalItemId();
                    String status = customerInventoryMappingDto.getStatus();
                    String nmsEnable = clientServiceRepository.findValueByNameAndMvnoId(NMSIntegrationConstants.NMS_INTEGRATION.NMS_ENABLE, 1);
                    if (!itemAssemblyflag &&
                            isApprove &&
                            status.equalsIgnoreCase("ACTIVE") &&
                            externalItem == null &&
                            nmsEnable.equalsIgnoreCase(NMSIntegrationConstants.NMS_INTEGRATION.TRUE_FLAG)) {
                        if (hasserial || hasmac) {
                            customerInventoryMappingService.sendCustomerInventoryToNMSIntegration(item, pcId, custInventoryId, customers, NMSIntegrationConstants.NMS_INTEGRATION.DELETE_ONU_OPERATION);
                        }
                    }
                    return genericDataDTO;
                }
            }
            return genericDataDTO;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }

    public void updateInoutwardMacMappingforSerialized(Long custInventoryMapId, CustomerInventoryMappingDto customerInventoryMappingDto) {
        // To set CustomerInventoryMappingId to InOutMacMappingId
        try {
            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
            // Build the boolean expression for filtering the customer inventory mappings
            BooleanExpression booleanExpression = qCustomerInventoryMapping.isDeleted.eq(false)
                    .and(qCustomerInventoryMapping.id.eq(custInventoryMapId));
            // Retrieve the first matching customer inventory mapping
            Optional<CustomerInventoryMapping> optionalCustomerInventoryMapping = Optional.ofNullable(customerInventoryMappingRepo.findOne(booleanExpression).orElse(null));
            // Ensure that the customer inventory mapping exists and there's at least one InOutWardMACMapping
            if (optionalCustomerInventoryMapping.isPresent() && !customerInventoryMappingDto.getInOutWardMACMapping().isEmpty()) {
                // Retrieve the first InOutWardMACMapping
                InOutWardMACMapping inOutWardMACMapping = customerInventoryMappingDto.getInOutWardMACMapping().get(0);
                // Set the CustomerInventoryMappingId and save the InOutWardMACMapping
                inOutWardMACMapping.setCustInventoryMappingId(optionalCustomerInventoryMapping.get().getId());
                inOutWardMacRepo.save(inOutWardMACMapping);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    //Update Generate Remove Request
    public void updateGenerateRemoveRequestStatus(Long customerInventoryId, Long customerId, Long mappingId, String status) {
        try {
            GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndCustomeridAndMacmappingid(customerInventoryId, customerId, mappingId);
            if (generateRemoveRequest != null) {
                generateRemoveRequest.setCustomerinventoryId(generateRemoveRequest.getCustomerinventoryId());
                generateRemoveRequest.setFlag(generateRemoveRequest.isFlag());
                generateRemoveRequest.setCustomerid(generateRemoveRequest.getCustomerid());
                generateRemoveRequest.setMacmappingid(generateRemoveRequest.getMacmappingid());
                generateRemoveRequest.setStaffid(generateRemoveRequest.getStaffid());
                if (status.equalsIgnoreCase("APPROVE") || status.equalsIgnoreCase("REJECTED")) {
                    generateRemoveRequest.setRequestStatus(status);
                } else {
                    generateRemoveRequest.setRequestStatus(generateRemoveRequest.getRequestStatus());
                }
                generateRemoveRequestRepo.save(generateRemoveRequest);
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        }
    }

    //    //Send Serialized Item for Intrigartion
//    public void sendSerializedItemforIntrigation(Item item) {
//        try {
//            ItemMessage message = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//            messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
//        } catch (CustomValidationException e) {
//            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
//        }
//    }
    public void validateUpdateMacMappingList(List<InOutWardMACMapping> list, Outward outwardDto, boolean hasMac, boolean hasSerial) {
        try {
            Integer countItems = inOutWardMacRepo.countItemsByOutwardId(outwardDto.getId());
            Integer totalInoutMacSelQty = list.size() + countItems;
            Long selectedQty = outwardDto.getSelectedItems();
            Long totalSelQty = selectedQty + list.size();
            if (outwardDto.getInTransitQty() < list.size()) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                        "The selected Items are more than outward qty.", null);
            } else if ((totalInoutMacSelQty > outwardDto.getInTransitQty()) || (totalSelQty > outwardDto.getInTransitQty())) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                        "Outward has only " + outwardDto.getInTransitQty() + " quantity available for MAC mapping.", null);
            }
            // Check Duplicate MAC Address in Selected Item From List
            Set<String> macAddressSet = new HashSet<>();
            if (hasSerial) {
                for (InOutWardMACMapping mapping : list) {
                    // Check if Serial Number is present
                    if (hasSerial && (mapping.getSerialNumber() == null || mapping.getSerialNumber().trim().isEmpty())) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                                "Please enter a serial number in selected items.", null);
                    }
                    if (hasMac && mapping.getMacAddress() != null) {
                        if (!macAddressSet.add(mapping.getMacAddress())) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                                    "Entered MAC " + mapping.getMacAddress() + " already exists", null);
                        }
                        // Check if MAC address already exists in database
                        String itemMacAddress = itemRepository.findMacByItemId(mapping.getId());
                        if (itemMacAddress == null || !itemMacAddress.equals(mapping.getMacAddress())) {
                            Integer existingItemCount;
                            int mvnoId = getMvnoIdFromCurrentStaff();
                            if (mvnoId == 1) {
                                existingItemCount = itemRepository.findCountByMacAddress(mapping.getMacAddress());
                            } else {
                                existingItemCount = itemRepository.findCountByMacAddressAndMvnoId(
                                        mapping.getMacAddress(), Arrays.asList(mvnoId, 1));
                            }
                            if (existingItemCount != 0 && existingItemCount != null) {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                                        "Entered MAC " + mapping.getMacAddress() + " already exists", null);
                            }
                        }
                    }
                }
            }
        } catch (CustomValidationException exception) {
            exception.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), exception.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
//        finally {
//            long endTime = System.currentTimeMillis(); // Capture end time
//            System.out.println("Validation ended at: " + endTime);
//            System.out.println("Total validation time: {} ms" + (endTime - startTime));
//        }
    }


//    public InOutWardMACMapingDTO saveEntityFromIntegrationRms(InOutWardMACMapingDTO entity) throws Exception {
//        try {
//            boolean flag = true;
//            if (entity.getMacAddress() != null) {
//                flag = inOutWardMACService.duplicateVerifyAtSave(entity.getMacAddress());
//            }
//            entity.setMvnoId(getMvnoIdFromCurrentStaff());
//            if (flag) {
//                Inward inward = inwardRepository.findByRmsInwardId(String.valueOf(entity.getInwardId()));
//                InwardServiceImpl inwardService = SpringContext.getBean(InwardServiceImpl.class);
//                InwardDto inwardDto = inwardMapper.domainToDTO(inwardRepository.findByRmsInwardId(String.valueOf(entity.getInwardId())), new CycleAvoidingMappingContext());
//                entity.setInwardId(inwardDto.getId());
//                inwardDto.setTotalMacSerial(inward.getTotalMacSerial() + 1);
//                // inwardService.updateEntity(inwardDto);
//                inwardRepository.save(inwardMapper.dtoToDomain(inwardDto, new CycleAvoidingMappingContext()));
//                ItemDto item = new ItemDto();
//                item.setMacAddress(entity.getMacAddress());
//                item.setSerialNumber(entity.getSerialNumber());
//                item.setName(inwardDto.getProductId().getName());
//                item.setCondition(inward.getType());
//                item.setMvnoId(inward.getMvnoId());
//
//                item.setOwnerId(inward.getDestinationId());
//                item.setOwnerType(inward.getDestinationType());
//                item.setCurrentInwardType(TypeConstants.FORWARDED);
//                item.setCurrentInwardId(inward.getId());
//                item.setProductId(inward.getProductId().getId());
//                item.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
//
//                item.setItemStatus(CommonConstants.UNALLOCATED);
//
//                Integer wrty = inward.getProductId().getExpiryTime();
//                if (inward.getProductId().getExpiryTimeUnit().equalsIgnoreCase("Month")) {
//                    wrty = 30 * wrty;
//                    item.setWarrantyPeriod(wrty);
//                } else {
//                    item.setWarrantyPeriod(wrty);
//                }
//
//                item.setWarranty("NotStarted");
//
//                //  ItemDto item1 = itemService.saveEntity(item);
//                Item item2 = itemRepository.save(itemMapper.dtoToDomain(item, new CycleAvoidingMappingContext()));
//                ItemDto itemDto = itemMapper.domainToDTO(item2, new CycleAvoidingMappingContext());
//                ItemConditionsMappingDto itemConditionsMappingDto = new ItemConditionsMappingDto();
//                itemConditionsMappingDto.setItemId(itemDto.getId());
//                itemConditionsMappingDto.setCondition(inward.getType());
//                itemConditionsMappingDto.setMvnoId(inward.getMvnoId());
//                itemConditionMappingRepository.save(itemConditionsMappingMapper.dtoToDomain(itemConditionsMappingDto, new CycleAvoidingMappingContext()));
//
//                ItemWarrantyMappingDto itemWarrantyMappingDto = new ItemWarrantyMappingDto();
//                itemWarrantyMappingDto.setItemId(itemDto.getId());
//                itemWarrantyMappingDto.setWarranty(itemDto.getWarranty());
//                itemWarrantyMappingDto.setMvnoId(inward.getMvnoId());
//                itemWarrantyMappingRepository.save(itemWarrantyMappingMapper.dtoToDomain(itemWarrantyMappingDto, new CycleAvoidingMappingContext()));
//
//                entity.setItemId(itemDto.getId());
//                InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.save(inOutWardMacMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
//                return inOutWardMacMapper.domainToDTO(inOutWardMACMapping, new CycleAvoidingMappingContext());
//            } else {
//
//                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Mac Address Already Exists, It Should Be Unique", null);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
//        }
//    }

    @Transactional
    public List<Item> saveManualItems(Inward entity, String operation, boolean isoemConsiderByProductId) {
        try (Stream<Item> itemStream = itemRepository
                .streamByCurrentInwardIdAndProductId(entity.getId(), entity.getProductId().getId())) {
            List<Item> items = itemStream.collect(Collectors.toList());
            if (CommonConstants.APPROVE.equalsIgnoreCase(operation)) {
                ConcurrentLinkedQueue<ItemConditionsMapping> conditionsMappings = new ConcurrentLinkedQueue<>();
                ConcurrentLinkedQueue<ItemWarrantyMapping> warrantyMappings = new ConcurrentLinkedQueue<>();
                ConcurrentLinkedQueue<InOutWardMACMapping> macMappings = new ConcurrentLinkedQueue<>();
                LocalDate startDate = entity.getStartDateTime();
                LocalDate expiryDate = entity.getExpiryDateTime();
                LocalDate today = LocalDate.now();
                boolean isWarrantyApplicable = (isoemConsiderByProductId && startDate != null && expiryDate != null);
                String oemWarrantyStatus;

                if (isWarrantyApplicable) {
                    if (startDate.isAfter(today) && expiryDate.isAfter(today)) {
                        oemWarrantyStatus = "NotStarted";
                    } else if (!startDate.isAfter(today) && !expiryDate.isBefore(today)) {
                        oemWarrantyStatus = "InWarranty";
                    } else {
                        oemWarrantyStatus = "Expired";
                    }
                } else {
                    oemWarrantyStatus = null;
                }
                for (Item item : items) {
                    if (isWarrantyApplicable) {
                        item.setOemStartDate(startDate);
                        item.setOemEndDate(expiryDate);
                        item.setOemWarrantyStatus(oemWarrantyStatus);
                        item.setOemWarrantyRemainingDays((int) Duration.between(startDate.atStartOfDay(), expiryDate.atStartOfDay()).toDays());
                    }
                    conditionsMappings.add(saveAutoItemConditionsMapping(item, entity));
                    warrantyMappings.add(saveAutoItemWarrantyMapping(item, entity));
                    macMappings.add(saveAutoInOutwardMacMapping(item, entity));
                }
                if (isWarrantyApplicable) {
                    items = itemRepository.saveAll(items);
                }
                batchInsertMappings(conditionsMappings, warrantyMappings, macMappings);
            } else if (CommonConstants.REJECTED.equalsIgnoreCase(operation)) {
                items.forEach(item -> item.setIsDeleted(true));
                items = itemRepository.saveAll(items);
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void batchInsertMappings(
            ConcurrentLinkedQueue<ItemConditionsMapping> conditionsMappings,
            ConcurrentLinkedQueue<ItemWarrantyMapping> warrantyMappings,
            ConcurrentLinkedQueue<InOutWardMACMapping> macMappings) {
//        System.out.println("********** Start Batch Insert Mapping **********");
        if (conditionsMappings.isEmpty() && warrantyMappings.isEmpty() && macMappings.isEmpty()) {
            return;
        }
        entityManager.unwrap(Session.class).doWork(connection -> {
            try (
                    PreparedStatement psConditions = connection.prepareStatement(
                            "INSERT INTO tbltitemconditions (item_id, item_condition, mvno_id) VALUES (?, ?, ?)");
                    PreparedStatement psWarranty = connection.prepareStatement(
                            "INSERT INTO tbltitemwarranty (item_id, warranty, mvno_id) VALUES (?, ?, ?)");
                    PreparedStatement psMac = connection.prepareStatement(
                            "INSERT INTO tblhitemhistory (inward_id, outward_id, cust_inventory_mapping_id, mac, mvno_id, status, is_forwarded, is_returned, item_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
            ) {
                for (ItemConditionsMapping mapping : conditionsMappings) {
                    psConditions.setLong(1, mapping.getItemId());
                    psConditions.setString(2, mapping.getCondition());
                    if (mapping.getMvnoId() != null) {
                        psConditions.setInt(3, mapping.getMvnoId());
                    } else {
                        psConditions.setNull(3, Types.INTEGER);
                    }
                    psConditions.addBatch();
                }
                psConditions.executeBatch();

                for (ItemWarrantyMapping mapping : warrantyMappings) {
                    psWarranty.setLong(1, mapping.getItemId());
                    psWarranty.setString(2, mapping.getWarranty());
                    if (mapping.getMvnoId() != null) {
                        psWarranty.setInt(3, mapping.getMvnoId());
                    } else {
                        psWarranty.setNull(3, Types.INTEGER);
                    }
                    psWarranty.addBatch();
                }
                psWarranty.executeBatch();

                for (InOutWardMACMapping mapping : macMappings) {
                    psMac.setLong(1, mapping.getInwardId());
                    psMac.setNull(2, Types.BIGINT); // outward_id is null by default
                    psMac.setNull(3, Types.BIGINT); // cust_inventory_mapping_id is null by default
                    psMac.setNull(4, Types.VARCHAR); // mac_address is null by default
                    if (mapping.getMvnoId() != null) {
                        psMac.setInt(5, mapping.getMvnoId());
                    } else {
                        psMac.setNull(5, Types.INTEGER);
                    }
                    psMac.setString(6, mapping.getStatus());
                    psMac.setInt(7, mapping.getIsForwarded());
                    psMac.setInt(8, mapping.getIsReturned());
                    psMac.setLong(9, mapping.getItemId());
                    psMac.addBatch();
                }
                psMac.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
//        System.out.println("********** End Batch Insert Mapping **********");
    }

    @Transactional
    public <T> void batchSaveWithEntityManager(Queue<T> queue, int batchSize) {
        try {
            int count = 0;
            for (T entity; (entity = queue.poll()) != null; count++) {
                entityManager.persist(entity);

                if (count % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityManager.flush();
            entityManager.clear();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


//         List<Item> items = itemRepository.findAllByCurrentInwardIdAndProductId(entity.getId(), entity.getProductId().getId());
//
//         // Dynamic thread pool based on data size & CPU cores
//         int availableThreads = Math.min(Runtime.getRuntime().availableProcessors(), Math.max(1, items.size() / 1000));
//         ExecutorService executorService = Executors.newFixedThreadPool(availableThreads);
//
//         if (operation.equalsIgnoreCase(CommonConstants.APPROVE)) {
//             List<ItemConditionsMapping> conditionsMappings = new ArrayList<>();
//             List<ItemWarrantyMapping> warrantyMappings = new ArrayList<>();
//             List<InOutWardMACMapping> macMappings = new ArrayList<>();
//
//             // Step 1: Process Items in Parallel
//             items.parallelStream().forEach(item -> {
//                 item.setOemStartDate(entity.getStartDateTime());
//                 item.setOemEndDate(entity.getExpiryDateTime());
//
//                 if (entity.getStartDateTime() != null && entity.getExpiryDateTime() != null) {
//                     if (entity.getStartDateTime().isAfter(LocalDate.now()) && entity.getExpiryDateTime().isAfter(LocalDate.now())) {
//                         item.setOemWarrantyStatus("NotStarted");
//                     } else if (entity.getStartDateTime().equals(LocalDate.now()) ||
//                             (entity.getStartDateTime().isBefore(LocalDate.now()) && entity.getExpiryDateTime().isAfter(LocalDate.now()))) {
//                         item.setOemWarrantyStatus("InWarranty");
//                     } else if (entity.getStartDateTime().isBefore(LocalDate.now()) && entity.getExpiryDateTime().isBefore(LocalDate.now())) {
//                         item.setOemWarrantyStatus("Expired");
//                     }
//
//                     long days = Duration.between(entity.getStartDateTime().atStartOfDay(), entity.getExpiryDateTime().atStartOfDay()).toDays();
//                     item.setOemWarrantyRemainingDays((int) days);
//                 }
//
//                 // Add mappings for batch processing
//                 conditionsMappings.add(saveAutoItemConditionsMapping(item, entity));
//                 warrantyMappings.add(saveAutoItemWarrantyMapping(item, entity));
//                 macMappings.add(saveAutoInOutwardMacMapping(item, entity));
//             });
//
//             // Save items asynchronously
//             CompletableFuture<Void> saveItemsFuture = CompletableFuture.runAsync(() -> itemRepository.saveAll(items), executorService);
//
//             int batchSize = 1000;
//
//             // Step 2: Save Mappings in Parallel Batches
//             CompletableFuture<Void> macMappingFuture = CompletableFuture.runAsync(() -> saveInBatches(macMappings, inOutWardMacRepo, batchSize), executorService);
//             CompletableFuture<Void> warrantyMappingFuture = CompletableFuture.runAsync(() -> saveInBatches(warrantyMappings, itemWarrantyMappingRepository, batchSize), executorService);
//             CompletableFuture<Void> conditionMappingFuture = CompletableFuture.runAsync(() -> saveInBatches(conditionsMappings, itemConditionMappingRepository, batchSize), executorService);
//
//             // Ensure all tasks complete before proceeding
//             CompletableFuture.allOf(saveItemsFuture, macMappingFuture, warrantyMappingFuture, conditionMappingFuture).join();
//
//         } else if (operation.equalsIgnoreCase(CommonConstants.REJECTED)) {
//             // Step 3: Reject Items in Parallel
//             items.parallelStream().forEach(item -> item.setIsDeleted(true));
//             itemRepository.saveAll(items);
//         }
//
//         // Shut down the executor service to free up resources
//         executorService.shutdown();
//     }

// Generic method to process batch saving dynamically


    private <T> void saveInBatches(List<T> data, JpaRepository<T, ?> repository, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < data.size(); i += batchSize) {
            partitions.add(data.subList(i, Math.min(i + batchSize, data.size())));
        }
        partitions.parallelStream().forEach(repository::saveAll);
    }

    public void validateUpdateMacMappingListNEW(
            List<InOutWardMACMapping> list,
            Outward outwardDto,
            boolean hasMac,
            boolean hasSerial,
            Integer mvnoId,
            List<ItemSkipped> rejectedEntities) {

        Set<String> macAddressSet = new HashSet<>();

        Iterator<InOutWardMACMapping> iterator = list.iterator();

        while (iterator.hasNext()) {

            InOutWardMACMapping mapping = iterator.next();
            boolean reject = false;
            String reason = "";

            //  Serial Validation
            if (hasSerial &&
                    (mapping.getSerialNumber() == null
                            || mapping.getSerialNumber().trim().isEmpty())) {

                reject = true;
                reason = "Serial number is missing.";
            }

            if (!reject && hasMac && mapping.getMacAddress() != null) {

                String mac = mapping.getMacAddress().trim();

                if (!macAddressSet.add(mac)) {
                    reject = true;
                    reason = "Duplicate MAC in file: " + mac;
                }

                if (!reject) {

                    String itemMacAddress =
                            itemRepository.findMacByItemId(mapping.getId());

                    if (itemMacAddress == null
                            || !itemMacAddress.equals(mac)) {

                        Integer existingItemCount;

                        if (mvnoId == 1) {
                            existingItemCount =
                                    itemRepository.findCountByMacAddress(mac);
                        } else {
                            existingItemCount =
                                    itemRepository.findCountByMacAddressAndMvnoId(
                                            mac,
                                            Arrays.asList(mvnoId.intValue(), 1));
                        }

                        if (existingItemCount != null && existingItemCount != 0) {
                            reject = true;
                            reason = "MAC already exists in system: " + mac;
                        }
                    }
                }
            }

            //  If Rejected → Move to Skip Table
            if (reject) {

                ItemSkipped skipped = new ItemSkipped();
                skipped.setOutwardId(outwardDto.getId());
                skipped.setMvnoId(Long.valueOf(mvnoId));
                skipped.setType("OUTWARD");
                skipped.setReason(reason);

                rejectedEntities.add(skipped);

                iterator.remove(); // remove from valid list
            }
        }
    }
}
