package com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemMacSerialMapping;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagementDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagementService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionMappingServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionsMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import com.savbill.inventorymanagement.security.spring.SpringContext;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class ExternalItemMacSerialMappingService extends ExBaseAbstractService<ExternalItemMacSerialMappingDTO, ExternalItemMacSerialMapping, Long> {

    @Autowired
    public ExternalItemMacSerialMappingRepo externalItemMacSerialMappingRepo;

    @Autowired
    public ExternalItemManagementRepository externalItemManagementRepository;

    @Autowired
    public ExternalItemManagementService externalItemManagementService;

    @Autowired
    public CustomerInventoryMappingService customerInventoryMappingService;

//    @Autowired
//    public CustMacMapppingService custMacMapppingService;

//    @Autowired
//    public CreditDocService creditDocService;

    @Autowired
    public ProductServiceImpl productService;

    @Autowired
    public ItemServiceImpl itemService;

    @Autowired
    public ItemRepository itemRepository;

    @Autowired
    public ItemConditionMappingRepository itemConditionMappingRepository;

    @Autowired
    public ItemWarrantyMappingRepository itemWarrantyMappingRepository;

    @Autowired
    InOutWardMacRepo inOutWardMacRepo;

    @Autowired
    public ItemWarrantyMappingServiceImpl itemWarrantyMappingService;

    @Autowired
    public ItemConditionMappingServiceImpl itemConditionMappingService;

    public ExternalItemMacSerialMappingService(ExternalItemMacSerialMappingRepo externalItemMacSerialMappingRepo, ExternalItemMacSerialMappingMapper externalItemMacSerialMappingMapper) {
        super(externalItemMacSerialMappingRepo, externalItemMacSerialMappingMapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ExternalItemMacSerialMappingService]";
    }

    //Get List By External Item Id
    List<ExternalItemMacSerialMapping> getByExternalItemId(Long externalItemId) {
        try {
            QExternalItemMacSerialMapping qExternalItemMacSerialMapping = QExternalItemMacSerialMapping.externalItemMacSerialMapping;
            ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(externalItemId).get();
            if (externalItemId != null) {
                BooleanExpression booleanExpression = qExternalItemMacSerialMapping.isNotNull().and(qExternalItemMacSerialMapping.externalItemId.eq(externalItemId)).and(qExternalItemMacSerialMapping.isDeleted.eq(false)).and(qExternalItemMacSerialMapping.custInventoryMappingId.isNull());
                return IterableUtils.toList(externalItemMacSerialMappingRepo.findAll(booleanExpression));
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //Get all MacMapping By External Item Id
    List<ExternalItemMacSerialMapping> getAllMACMappingByExternalItemId(Long externalItemId) {
        QExternalItemMacSerialMapping qExternalItemMacSerialMapping = QExternalItemMacSerialMapping.externalItemMacSerialMapping;
        BooleanExpression booleanExpression = qExternalItemMacSerialMapping.isNotNull().and(qExternalItemMacSerialMapping.isDeleted.eq(false)).and(qExternalItemMacSerialMapping.custInventoryMappingId.isNull());
        return IterableUtils.toList(externalItemMacSerialMappingRepo.findAll(booleanExpression));
    }

    @Override
    public ExternalItemMacSerialMappingDTO saveEntity(ExternalItemMacSerialMappingDTO entity) throws Exception {
        try {
            if (entity.getExternalItemId() != null) {
                boolean flag = true;
                if (entity.getMacAddress() != null) {
                    InOutWardMACService inOutWardMACService = SpringContext.getBean(InOutWardMACService.class);
                    flag = inOutWardMACService.duplicateVerifyAtSave(entity.getMacAddress());
                }
                if (flag) {
                    entity.setMvnoId(getMvnoIdFromCurrentStaff());
                    ExternalItemManagementService externalItemManagementService = SpringContext.getBean(ExternalItemManagementService.class);
                    ExternalItemManagementDTO externalItemManagementDTO = externalItemManagementService.getEntityForUpdateAndDelete(entity.getExternalItemId());
                    externalItemManagementDTO.setTotalMacSerial(externalItemManagementDTO.getTotalMacSerial() + 1);
                    externalItemManagementService.updateEntity(externalItemManagementDTO);
                    ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(entity.getExternalItemId()).get();
                    // Save Item Entity By External Item Entity
                    Item item = new Item();
                    item.setMacAddress(entity.getMacAddress());
                    item.setSerialNumber(entity.getSerialNumber());
                    item.setName(externalItemManagement.getProductId().getName());
                    item.setCondition(externalItemManagement.getOwnershipType());
                    item.setMvnoId(externalItemManagement.getMvnoId());
                    item.setOwnerId(null);
                    item.setOwnerId(externalItemManagement.getServiceAreaId().getId());
                    item.setOwnerType("ServiceArea");
                    item.setItemStatus(CommonConstants.UNALLOCATED);
                    item.setCurrentInwardType(null);
                    item.setCurrentInwardId(null);
                    item.setExternalItemId(externalItemManagement.getId());
                    item.setProductId(externalItemManagement.getProductId().getId());
                    item.setOwnershipType(externalItemManagement.getOwnershipType());

                    Item item1 = null;
                    item1 = itemRepository.save(item);
                    ItemConditionsMapping itemConditionsMapping = new ItemConditionsMapping();
                    itemConditionsMapping.setItemId(item1.getId());
                    itemConditionsMapping.setCondition(externalItemManagement.getOwnershipType());
                    itemConditionsMapping.setMvnoId(externalItemManagement.getMvnoId());
                    itemConditionMappingRepository.save(itemConditionsMapping);
                    //itemConditionMappingService.saveEntity(itemConditionsMappingDto);

                    ItemWarrantyMapping itemWarrantyMapping = new ItemWarrantyMapping();
                    itemWarrantyMapping.setItemId(item1.getId());
                    itemWarrantyMapping.setWarranty(item1.getWarranty());
                    itemWarrantyMapping.setMvnoId(externalItemManagement.getMvnoId());
                    itemWarrantyMappingRepository.save(itemWarrantyMapping);

                    // Save InoutMac Mapping Entity By ExternalItem
                    InOutWardMACMapping inOutWardMACMapping = new InOutWardMACMapping();
                    inOutWardMACMapping.setMacAddress(entity.getMacAddress());
                    inOutWardMACMapping.setSerialNumber(entity.getSerialNumber());
                    inOutWardMACMapping.setStatus(externalItemManagement.getStatus());
                    inOutWardMACMapping.setIsForwarded(0);
                    inOutWardMACMapping.setExternalItemId(externalItemManagement.getId());
                    inOutWardMACMapping.setItemId(item1.getId());
                    inOutWardMacRepo.save(inOutWardMACMapping);
                    entity.setItemId(item1.getId());
                    return super.saveEntity(entity);
                }
                else {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Duplicate Mac Exists Already", null);
                }
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), ce.getMessage(), null);
        }
        return null;
    }

    @Transactional
    public void deleteExternalItemMac(Long itemId) {
        try {
            Item item = itemRepository.findById(itemId).orElse(null);
            if (item != null) {
                item.setIsDeleted(true);
                itemRepository.save(item);
                InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findByItemId(itemId);
                if (!Objects.isNull(inOutWardMACMapping)) {
                    inOutWardMACMapping.setIsDeleted(true);
                    inOutWardMacRepo.save(inOutWardMACMapping);
                }
                QExternalItemMacSerialMapping qExternalItemMacSerialMapping = QExternalItemMacSerialMapping.externalItemMacSerialMapping;
                BooleanExpression booleanExpression = qExternalItemMacSerialMapping.itemId.eq(itemId);
                List<ExternalItemMacSerialMapping> externalItemMacSerialMapping = IterableUtils.toList(externalItemMacSerialMappingRepo.findAll(booleanExpression));
                for (int i = 0; i < externalItemMacSerialMapping.size(); i++) {
                    externalItemMacSerialMapping.get(0).setIsDeleted(true);
                    externalItemMacSerialMappingRepo.save(externalItemMacSerialMapping.get(0));
                }
                ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(externalItemMacSerialMapping.get(0).getExternalItemId()).get();
                externalItemManagement.setTotalMacSerial(externalItemManagement.getTotalMacSerial() - 1);
                externalItemManagementRepository.save(externalItemManagement);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
