package com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.domain.ProductWarehouseMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.repository.ProductWarehouseMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WareHouseTeamsMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WarehouseManagementRepository;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamUserMapping.TeamUserMappingsRepocitory;
import com.savbill.inventorymanagement.rabbitmq.RabbitMqConstants;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.InventoryThresholdMessage;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The type Product owner service.
 */
@Service
public class ProductOwnerService extends ExBaseAbstractService<ProductOwnerDto, ProductOwner, Long> {


    /**
     * The Product owner repository.
     */
    @Autowired
    private ProductOwnerRepository productOwnerRepository;
    /**
     * The Product owner mapper.
     */
//    @Autowired
//    ChargeService chargeService;
    @Autowired
    private ProductOwnerMapper productOwnerMapper;
    /**
     * The Product repository.
     */
    @Autowired
    private ProductRepository productRepository;
    /**
     * The Product warehouse mapping repo.
     */
    @Autowired
    ProductWarehouseMappingRepo productWarehouseMappingRepo;
    /**
     * The Staff user repository.
     */
    @Autowired
    StaffUserRepository staffUserRepository;
    /**
     * The Warehouse management repository.
     */
    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;
    /**
     * The Team user mappings repocitory.
     */
    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;
    /**
     * The Ware house teams mapping repo.
     */
    @Autowired
    WareHouseTeamsMappingRepo wareHouseTeamsMappingRepo;
    /**
     * The Kafka message sender.
     */
    @Autowired
    KafkaMessageSender kafkaMessageSender;


    /**
     * Instantiates a new Product owner service.
     * @param productOwnerRepository the product owner repository
     * @param mapper the mapper
     */
    public ProductOwnerService(ProductOwnerRepository productOwnerRepository, IBaseMapper<ProductOwnerDto, ProductOwner> mapper) {
        super(productOwnerRepository, mapper);
    }

    /**
     * Gets module name for log.
     * @return the module name for log
     */
    @Override
    public String getModuleNameForLog() {
        return "[ProductOwnerService]";
    }

    /**
     * Delete entity.
     * @param entity the entity
     * @throws Exception the exception
     */
    @Override
    public void deleteEntity(ProductOwnerDto entity) throws Exception {
        super.deleteEntity(entity);
    }

    /**
     * Save entity from rms product owner.
     * @param productOwnerDto the product owner dto
     * @return the product owner
     */
    public ProductOwner saveEntityFromRms(ProductOwnerDto productOwnerDto) {
        ProductOwner owner = productOwnerMapper.dtoToDomain(productOwnerDto, new CycleAvoidingMappingContext());
        productOwnerRepository.save(owner);
        return owner;
    }

    /**
     * Find by product id owner id and owner type product owner dto.
     * @param productId the product id
     * @param ownerId the owner id
     * @param type the type
     * @return the product owner dto
     */
    public ProductOwnerDto findByProductIdOwnerIdAndOwnerType(Long productId, Long ownerId, String type) {
        return productOwnerMapper.domainToDTO(productOwnerRepository.findByProductIdOwnerIdAndOwnerType(productId, ownerId, type), new CycleAvoidingMappingContext());
    }

    /**
     * Gets available qty details by product and destination.
     * @param productId the product id
     * @param ownerId the owner id
     * @param ownerType the owner type
     * @return the available qty details by product and destination
     */
    public List<ProductOwner> getAvailableQtyDetailsByProductAndDestination(Long productId, Long ownerId, String ownerType) {
        QProductOwner qProductOwner = QProductOwner.productOwner;
        BooleanExpression booleanExpression = qProductOwner.isNotNull()
                .and(qProductOwner.productId.eq(productId))
                .and(qProductOwner.ownerId.eq(ownerId))
                .and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
        return Lists.newArrayList(productOwnerRepository.findAll(booleanExpression));
    }

    /**
     * Find product owner details product owner.
     * @param productId the product id
     * @param ownerId the owner id
     * @param ownerType the owner type
     * @return the product owner
     */
    public ProductOwner findProductOwnerDetails(Long productId, Long ownerId, String ownerType) {
        return productOwnerRepository.findByProductIdAndOwnerIdAndOwnerType(productId, ownerId, ownerType);
    }

    /**
     * Gets non trackable product qty.
     * @param productId the product id
     * @param ownerId the owner id
     * @param ownerType the owner type
     * @return the non trackable product qty
     * @throws Exception the exception
     */
    public List<ProductOwnerDto> getNonTrackableProductQty(Long productId, Long ownerId, String ownerType) throws Exception {
        try {
            QProductOwner qProductOwner = QProductOwner.productOwner;
            BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(ownerId)).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
            List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
            List<ProductOwnerDto> productOwnerDtos = productOwnerMapper.domainToDTO(productOwnerList, new CycleAvoidingMappingContext());
            productOwnerDtos.stream().forEach(r -> {
                r.setProductId(productId);
                r.setProductName(productRepository.findById(r.getProductId()).get().getName());
            });
            return productOwnerDtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update product owner for non trackable product owner.
     * @param qty the qty
     * @param productId the product id
     * @param ownerId the owner id
     * @param ownerType the owner type
     * @return the product owner
     * @throws Exception the exception
     */
    public ProductOwner updateProductOwnerForNonTrackable(Long qty, Long productId, Long ownerId, String ownerType) throws Exception {
        QProductOwner qProductOwner = QProductOwner.productOwner;
        BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(ownerId)).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
        List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
        productOwnerList.get(0).setUnusedQty(productOwnerList.get(0).getUnusedQty() - qty);
        productOwnerList.get(0).setUsedQty(productOwnerList.get(0).getUsedQty() + qty);
        productOwnerList = productOwnerRepository.saveAll(productOwnerList);
        return productOwnerList.get(0);
    }

    /**
     * Update product owner for non trackable after reject product owner.
     * @param qty the qty
     * @param productId the product id
     * @param ownerId the owner id
     * @param ownerType the owner type
     * @return the product owner
     * @throws Exception the exception
     */
    public ProductOwner updateProductOwnerForNonTrackableAfterReject(Long qty, Long productId, Long ownerId, String ownerType) throws Exception {
        QProductOwner qProductOwner = QProductOwner.productOwner;
        BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(ownerId)).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
        List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
        productOwnerList.get(0).setUnusedQty(productOwnerList.get(0).getUnusedQty() + qty);
        productOwnerList.get(0).setUsedQty(productOwnerList.get(0).getUsedQty() - qty);
        productOwnerList = productOwnerRepository.saveAll(productOwnerList);
        return productOwnerList.get(0);
    }

    /**
     * Update product owner for serialized product product owner.
     * @param qty the qty
     * @param productId the product id
     * @param ownerId the owner id
     * @param ownerType the owner type
     * @return the product owner
     * @throws Exception the exception
     */
    public ProductOwner updateProductOwnerForSerializedProduct(Long qty, Long productId, Integer ownerId, String ownerType) throws Exception {
        try {
            QProductOwner qProductOwner = QProductOwner.productOwner;
//        StaffUser staffUser = staffUserRepository.findById(ownerId).get();
//        if(staffUser.getPartnerid() != 1) {
//            ownerId = Integer.valueOf(staffUser.getPartnerid());
//            ownerType = CommonConstants.PARTNER;
//        }
            if (getLoggedInUser().getPartnerId() != 1) {
                ownerId = Integer.valueOf(getLoggedInUser().getPartnerId());
                ownerType = CommonConstants.PARTNER;
            }
            BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(Long.valueOf(ownerId))).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
            Optional<ProductOwner> productOwnerOptional = Optional.ofNullable(productOwnerRepository.findOne(booleanExpression).orElse(null));
            if (productOwnerOptional.isPresent()) {
                productOwnerOptional.get().setUnusedQty(productOwnerOptional.get().getUnusedQty() - qty);
                productOwnerOptional.get().setUsedQty(productOwnerOptional.get().getUsedQty() + qty);
                return productOwnerRepository.save(productOwnerOptional.get());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update product owner for serialized product reject product owner.
     * @param qty the qty
     * @param productId the product id
     * @param ownerId the owner id
     * @param ownerType the owner type
     * @return the product owner
     * @throws Exception the exception
     */
    public ProductOwner updateProductOwnerForSerializedProductReject(Long qty, Long productId, Integer ownerId, String ownerType) throws Exception {
        try {
            QProductOwner qProductOwner = QProductOwner.productOwner;
//        StaffUser staffUser = staffUserRepository.findById(ownerId).get();
//        if(staffUser.getPartnerid() != 1) {
//            ownerId = Integer.valueOf(staffUser.getPartnerid());
//            ownerType = CommonConstants.PARTNER;
//        }
            if (getLoggedInUser().getPartnerId() != 1) {
                ownerId = Integer.valueOf(getLoggedInUser().getPartnerId());
                ownerType = CommonConstants.PARTNER;
            }
            BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(Long.valueOf(ownerId))).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
            List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
            productOwnerList.get(0).setUnusedQty(productOwnerList.get(0).getUnusedQty() + qty);
            productOwnerList.get(0).setUsedQty(productOwnerList.get(0).getUsedQty() - qty);
            productOwnerList = productOwnerRepository.saveAll(productOwnerList);
            return productOwnerList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Shared threshold request message.
     * @param productId the product id
     * @param ownerId the owner id
     * @param ownerType the owner type
     */
    public void sharedThresholdRequestMessage(Long productId, Long ownerId, String ownerType) {
        try {
            ProductOwner productOwner = findProductOwnerDetails(productId, ownerId, ownerType);
            ProductWarehouseMapping productWarehouseMapping = productWarehouseMappingRepo.findByProductIdAndWarehouseId(productId, ownerId);
            if (productOwner != null && productWarehouseMapping != null) {
                if (ownerType.equalsIgnoreCase("Warehouse") &&
                        (productOwner.getUnusedQty() <= productWarehouseMapping.getThresholdQty()) &&
                        productWarehouseMapping.getThresholdQty() != null &&
                        productOwner.getIsNotify() == false) {
                    String source = "warehouse";
                    String productName = productRepository.findProductNameByProductId(productId);
                    String warehouseName = warehouseManagementRepository.findNameById(ownerId);
                    List<Long> teamIds = wareHouseTeamsMappingRepo.findTeamIdsByWarehouseId(ownerId);
                    List<String> staffEmails = new ArrayList<>();
                    if (!teamIds.isEmpty()) {
                        List<Long> staffIds = teamUserMappingsRepocitory.findStaffIds(teamIds);
                        if (!staffIds.isEmpty()) {
                            for (Long staffId : staffIds) {
                                String emailId = staffUserRepository.findEmailByUserId(staffId.intValue());
                                if (emailId != null) {
                                    staffEmails.add(emailId);
                                }
                            }
                        }
                        if (!staffEmails.isEmpty()) {
                            String emailId = staffEmails.get(0);
                            List<String> altEmailList = staffEmails.size() > 1 ? staffEmails.subList(1, staffEmails.size()).stream().distinct().collect(Collectors.toList()) : new ArrayList<>();
                            InventoryThresholdMessage inventoryThresholdMessage = new InventoryThresholdMessage(
                                    RabbitMqConstants.INVENTORY_THRESHOLD_MESSAGE,
                                    getMvnoIdFromCurrentStaff(),
                                    productName,
                                    warehouseName,
                                    productOwner.getUnusedQty(),
                                    emailId,
                                    RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_INVENTORY,
                                    altEmailList
                            );
                            Gson gson = new Gson();
                            gson.toJson(inventoryThresholdMessage);
                            kafkaMessageSender.send(new KafkaMessageData(inventoryThresholdMessage, inventoryThresholdMessage.getClass().getSimpleName()));
                            productOwner.setIsNotify(true);
                            productOwnerRepository.save(productOwner);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Sets is notify.
     * @param productId the product id
     * @param ownerId the owner id
     * @param ownerType the owner type
     */
    public void setIsNotify(Long productId, Long ownerId, String ownerType) {
        try {
            ProductOwner productOwner = findProductOwnerDetails(productId, ownerId, ownerType);
            if (productOwner != null &&
                    ownerType.equalsIgnoreCase("Warehouse") &&
                    productOwner.getIsNotify() == true
            ) {
                productOwner.setIsNotify(false);
                productOwnerRepository.save(productOwner);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
