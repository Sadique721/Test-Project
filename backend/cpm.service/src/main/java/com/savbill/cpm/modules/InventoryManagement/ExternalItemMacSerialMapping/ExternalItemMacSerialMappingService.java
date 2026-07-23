package com.savbill.cpm.modules.InventoryManagement.ExternalItemMacSerialMapping;

import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.savbill.cpm.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.savbill.cpm.modules.InventoryManagement.ExternalItemManagement.model.ExternalItemManagementDTO;
import com.savbill.cpm.modules.InventoryManagement.ExternalItemManagement.repository.ExternalItemManagementRepository;
import com.savbill.cpm.modules.InventoryManagement.ExternalItemManagement.service.ExternalItemManagementService;
import com.savbill.cpm.modules.InventoryManagement.InOutMACMapping.*;
import com.savbill.cpm.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.cpm.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.savbill.cpm.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.cpm.modules.InventoryManagement.item.Item;
import com.savbill.cpm.modules.InventoryManagement.item.ItemRepository;
import com.savbill.cpm.modules.InventoryManagement.item.ItemServiceImpl;
import com.savbill.cpm.modules.InventoryManagement.itemConditionMapping.ItemConditionMappingRepository;
import com.savbill.cpm.modules.InventoryManagement.itemConditionMapping.ItemConditionMappingServiceImpl;
import com.savbill.cpm.modules.InventoryManagement.itemConditionMapping.ItemConditionsMapping;
import com.savbill.cpm.modules.InventoryManagement.itemWarranty.ItemWarrantyMapping;
import com.savbill.cpm.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingRepository;
import com.savbill.cpm.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingServiceImpl;
import com.savbill.cpm.modules.InventoryManagement.product.ProductServiceImpl;
import com.savbill.cpm.service.postpaid.CreditDocService;
import com.savbill.cpm.service.postpaid.CustMacMapppingService;
import com.savbill.cpm.spring.SpringContext;
import com.savbill.cpm.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

    @Autowired
    public CustMacMapppingService custMacMapppingService;

    @Autowired
    public CreditDocService creditDocService;

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
        QExternalItemMacSerialMapping qExternalItemMacSerialMapping = QExternalItemMacSerialMapping.externalItemMacSerialMapping;
        ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(externalItemId).get();
        if (externalItemId != null) {
            BooleanExpression booleanExpression = qExternalItemMacSerialMapping.isNotNull().and(qExternalItemMacSerialMapping.externalItemId.eq(externalItemId)).and(qExternalItemMacSerialMapping.isDeleted.eq(false)).and(qExternalItemMacSerialMapping.custInventoryMappingId.isNull());
            return IterableUtils.toList(externalItemMacSerialMappingRepo.findAll(booleanExpression));
        } else {
            return null;
        }
    }

    //Get all MacMapping By External Item Id
//    List<ExternalItemMacSerialMapping> getAllMACMappingByExternalItemId(Long externalItemId) {
//        QExternalItemMacSerialMapping qExternalItemMacSerialMapping = QExternalItemMacSerialMapping.externalItemMacSerialMapping;
//        BooleanExpression booleanExpression = qExternalItemMacSerialMapping.isNotNull().and(qExternalItemMacSerialMapping.isDeleted.eq(false)).and(qExternalItemMacSerialMapping.custInventoryMappingId.isNull());
//        return IterableUtils.toList(externalItemMacSerialMappingRepo.findAll(booleanExpression));
//    }

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
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), ce.getMessage(), null);
        }
        return null;
    }

//    @Transactional
//    public void deleteExternalItemMac(Long itemId) {
//        Item item = itemRepository.findById(itemId).get();
//        item.setIsDeleted(true);
//        itemRepository.save(item);
//        InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findByItemId(itemId);
//        if (!Objects.isNull(inOutWardMACMapping)) {
//            inOutWardMACMapping.setIsDeleted(true);
//            inOutWardMacRepo.save(inOutWardMACMapping);
//        }
//        QExternalItemMacSerialMapping qExternalItemMacSerialMapping = QExternalItemMacSerialMapping.externalItemMacSerialMapping;
//        BooleanExpression booleanExpression = qExternalItemMacSerialMapping.itemId.eq(itemId);
//        List<ExternalItemMacSerialMapping> externalItemMacSerialMapping = IterableUtils.toList(externalItemMacSerialMappingRepo.findAll(booleanExpression));
//        for (int i = 0; i < externalItemMacSerialMapping.size(); i++) {
//            externalItemMacSerialMapping.get(0).setIsDeleted(true);
//            externalItemMacSerialMappingRepo.save(externalItemMacSerialMapping.get(0));
//        }
//        ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(externalItemMacSerialMapping.get(0).getExternalItemId()).get();
//        externalItemManagement.setTotalMacSerial(externalItemManagement.getTotalMacSerial() - 1);
//        externalItemManagementRepository.save(externalItemManagement);
//    }
}
