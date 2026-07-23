package com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.SearchConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.fileUtillity.FileUtility;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.InventoryManagement.BulkConsumption.BulkConsumptionRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionMappingServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemStatusMapping.ItemStatusMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.QProduct;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WarehouseManagementServiceImpl;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.inventorymanagement.utils.APIConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NonSerializedItemServiceImpl extends ExBaseAbstractService<NonSerializedItemDto, NonSerializedItem, Long> {

    @Autowired
    NonSerializedItemMapper nonSerializedItemMapper;
    @Autowired
    private NonSerializedItemRepository nonSerializedItemRepository;
    @Autowired
    NonSerializedItemServiceImpl nonSerializedItemService;
    @Autowired
    private InwardServiceImpl inwardService;
    @Autowired
    InwardRepository inwardRepository;
    @Autowired
    OutwardServiceImpl outwardService;
    @Autowired
    InOutWardMacRepo inOutWardMacRepo;
    @Autowired
    InOutWardMacMapper inOutWardMacMapper;
    @Autowired
    WarehouseManagementServiceImpl warehouseManagementService;
    @Autowired
    PopManagementService popManagementService;
    @Autowired
    ServiceAreaService serviceAreaService;

    @Autowired
    ItemConditionMappingServiceImpl itemConditionMappingService;

    @Autowired
    ItemWarrantyMappingServiceImpl itemWarrantyMappingService;

    @Autowired
    ItemWarrantyMappingRepository itemWarrantyMappingRepository;

    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private ItemConditionMappingRepository itemConditionMappingRepository;

    @Autowired
    ItemStatusMappingRepo itemStatusMappingRepo;

    @Autowired
    BulkConsumptionRepository bulkConsumptionRepository;

    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    @Autowired
    PopManagementRepository popManagementRepository;

    @Autowired
    InOutWardMACService inOutWardMACService;

    public static final String MODULE = "[CreditDocService]";

    public String PATH;


    @Autowired
    ProductServiceImpl productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ItemRepository itemRepository;

    public NonSerializedItemServiceImpl(NonSerializedItemRepository nonSerializedItemRepository, IBaseMapper<NonSerializedItemDto, NonSerializedItem> mapper) {
        super(nonSerializedItemRepository, mapper);
    }

    private static final Logger logger = LoggerFactory.getLogger(NonSerializedItemServiceImpl.class);

    @Override
    public String getModuleNameForLog() {
        return "[NonSerializedItemServiceImpl]";
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        QProduct qProduct = QProduct.product;
        BooleanExpression booleanExpression = qProduct.isNotNull().and(qProduct.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<NonSerializedItem> paginationList = null;
        try {
            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                paginationList = itemRepository.findAll(pageRequest);
//            } else {
            paginationList = nonSerializedItemRepository.findAll(booleanExpression, pageRequest);
//            }

            if (paginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getProductList(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Unable to Search :  Response : {{}};Error :{} ;Exception:{}", APIConstants.FAIL, HttpStatus.NOT_ACCEPTABLE, ex.getStackTrace());
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getProductList(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getProductList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Page<NonSerializedItem> productList;
            if (getMvnoIdFromCurrentStaff() == 1)
                productList = nonSerializedItemRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
            else
                productList = nonSerializedItemRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != productList && 0 < productList.getSize()) {
                makeGenericResponse(genericDataDTO, productList);
            }
            if (productList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Data Not Found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Unable to Fetch all charge by Type :  Response : {{}};Error :{} ;Exception:{}", APIConstants.FAIL, HttpStatus.NOT_ACCEPTABLE, ex.getStackTrace());
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return genericDataDTO;
    }

    @Override
    public void deleteEntity(NonSerializedItemDto entity) throws Exception {
        super.deleteEntity(entity);
    }

    @Override
    public NonSerializedItemDto saveEntity(NonSerializedItemDto entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        return super.saveEntity(entity);
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = nonSerializedItemRepository.duplicateVerifyAtSave(name);
            else count = nonSerializedItemRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = nonSerializedItemRepository.deleteVerify(id);
        if (count == 1) {
            flag = true;
        }
        return flag;
    }

    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        try {
            boolean flag = false;
            List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
            if (name != null) {
                name = name.trim();
                Integer count;
                if (getMvnoIdFromCurrentStaff() == 1) count = nonSerializedItemRepository.duplicateVerifyAtSave(name);
                else count = nonSerializedItemRepository.duplicateVerifyAtSave(name, mvnoIds);
                if (count >= 1) {
                    Integer countEdit;
                    if (getMvnoIdFromCurrentStaff() == 1)
                        countEdit = nonSerializedItemRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id));
                    else countEdit = nonSerializedItemRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id), mvnoIds);
                    if (countEdit == 1) {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<NonSerializedItem> findAllByByInwardInAndProductId(Long inwardId, Long productId) {
        return nonSerializedItemRepository.findAllByCurrentInwardIdAndProductId(inwardId, productId);
    }

//    public saveNonSerializedItems(){}
//    public GenericDataDTO searchNonSerializedItems(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchItemsPojo searchItemsPojo) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            if (searchItemsPojo != null) {
//
//                genericDataDTO = nonSerializedItemService.findItems(pageNumber, customPageSize, sortBy, sortOrder, searchItemsPojo);
//            }
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        return genericDataDTO;
//    }


//    public GenericDataDTO findItems(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchItemsPojo search) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        PageRequest pageRequest;
//        Page<NonSerializedItem> paginationList = null;
////        QNonSerializedItem qNonSerializedItem = QNonSerializedItem.non;
//        List<NonSerializedItemDto> itemDtoList = new ArrayList<>();
//
//        BooleanExpression booleanExpression = qItem.isNotNull().and(qItem.isDeleted.eq(false));
//        try {
//            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            if (getMvnoIdFromCurrentStaff() != 1)
//                booleanExpression = booleanExpression.and(qItem.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//
//            if (search.getOwnerType() != null && !"null".equals(search.getOwnerType()) && !"".equals(search.getOwnerType())) {
//                booleanExpression = booleanExpression.and(qItem.ownerType.startsWithIgnoreCase(search.getOwnerType()));
//            }
//            if (search.getOwnerId() != null && !"null".equals(search.getOwnerType()) && !"".equals(search.getOwnerType())) {
//                booleanExpression = booleanExpression.and(qItem.ownerId.eq(Long.valueOf(String.valueOf(search.getOwnerId()))));
//            }
//            if (search.getProductId() != null && !"null".equals(search.getProductId()) && !"".equals(search.getProductId())) {
//                booleanExpression = booleanExpression.and(qItem.productId.eq(Long.valueOf(String.valueOf(search.getProductId()))));
//            }
//
//            if (search.getInwardId() != null && !"null".equals(search.getInwardId()) && !"".equals(search.getInwardId())) {
//                booleanExpression = booleanExpression.and(qItem.currentInwardId.eq(Long.valueOf(String.valueOf(search.getInwardId()))));
//            }
//
//            if (search.getItemType() != null && !"null".equals(search.getItemType()) && !"".equals(search.getItemType())) {
//                booleanExpression = booleanExpression.and(qItem.condition.startsWithIgnoreCase(search.getItemType()));
//            }
//
//            if (search.getItemStatus() != null && !"null".equals(search.getItemStatus()) && !"".equals(search.getItemStatus())) {
//                booleanExpression = booleanExpression.and(qItem.itemStatus.startsWithIgnoreCase(search.getItemStatus()));
//            }
//
//            if (search.getOwnership() != null && !"null".equals(search.getOwnership()) && !"".equals(search.getOwnership())) {
//                booleanExpression = booleanExpression.and(qItem.ownershipType.startsWithIgnoreCase(search.getOwnership()));
//            }
//
//            if (search.getWarrantyStatus() != null && !"null".equals(search.getWarrantyStatus()) && !"".equals(search.getWarrantyStatus())) {
//                booleanExpression = booleanExpression.and(qItem.warranty.startsWithIgnoreCase(search.getWarrantyStatus()));
//            }
//            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//
//            paginationList = itemRepository.findAll(booleanExpression, pageRequest);
//            List<ItemDto> dto = paginationList.get().map(item -> itemMapper.domainToDTO(item, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//
//            for (ItemDto itemDto : dto) {
//                if (itemDto.getCurrentInwardId() != null) {
//                    itemDto.setCurrentInwardNumber(inwardRepository.findById(itemDto.getCurrentInwardId()).get().getInwardNumber());
//                }
//                if (itemDto.getProductId() != null) {
//                    itemDto.setProductName(productService.getEntityById(itemDto.getProductId().longValue()).getName());
//                }
//                if (!itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).isEmpty()) {
//                    itemDto.setFilename(itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).get(0).getFilename());
//                    itemDto.setItemConditionId(itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).get(0).getId());
//                }
//                if (itemRepository.getOne(itemDto.getId()).getRemarks() != null) {
//                    itemDto.setRemarks(itemRepository.getOne(itemDto.getId()).getRemarks());
//                }
//                if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.STAFF))
//                    itemDto.setOwnerName(staffService.get(itemDto.getOwnerId().intValue()).getFirstname());
//                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.WAREHOUSE))
//                    itemDto.setOwnerName(warehouseManagementService.getEntityById(itemDto.getOwnerId().longValue()).getName());
//                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.POP))
//                    itemDto.setOwnerName(popManagementService.getEntityById(itemDto.getOwnerId().longValue()).getName());
//                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.SERVICE_AREA))
//                    itemDto.setOwnerName(serviceAreaService.getByID(itemDto.getOwnerId().longValue()).getName());
//                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.PARTNER))
//                    itemDto.setOwnerName(partnerService.get(itemDto.getOwnerId().intValue()).getName());
//
//                itemDtoList.add(itemDto);
//            }
//
//        } catch (Exception ex) {
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//
//        genericDataDTO.setDataList(itemDtoList);
//        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
//        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
//        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
//        genericDataDTO.setTotalPages(paginationList.getTotalPages());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        return genericDataDTO;
//    }

//    public List<ItemDto> findItemsSuibiseOwned(Long currentInwardId) {
//        List<ItemDto> itemDtoList = null;
//        try {
//            QItem qItem = QItem.item;
//            BooleanExpression booleanExpression = qItem.isNotNull().and(qItem.currentInwardId.eq(currentInwardId)).and(qItem.isDeleted.eq(false).and(qItem.ownershipType.eq("Subisu Owned")).and(qItem.itemStatus.eq("Unallocated")));
//            List<Item> itemList = (List<Item>) itemRepository.findAll(booleanExpression);
//            itemDtoList = itemList.stream().map(item -> itemMapper.domainToDTO(item, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//            return itemDtoList;
//        } catch (Exception exception) {
//            exception.getMessage();
//        }
//        return itemDtoList;
//    }

    public String getRandomenumber(String flag1, String flag2, String flag3) {
        try {
            String flag = "";
            if (flag1 != null) {
                flag += flag1;
            }
            if (flag2 != null) {
                flag += flag2;
            }
            if (flag3 != null) {
                NonSerializedItem nonSerializedItem = nonSerializedItemRepository.findTopByOrderByIdDesc();
                if (nonSerializedItem == null) {
                    flag += 1;
                } else {
                    flag += nonSerializedItem.getId() + 1;
                }
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<NonSerializedItem> getNonSerializedItemForInward(Long inwardId, Long id, Long ownerId, String ownerType) {
        try {
            QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
            BooleanExpression booleanExpression = qNonSerializedItem.isDeleted.eq(false).and(qNonSerializedItem.productId.eq(id)).and(qNonSerializedItem.ownerId.eq(ownerId)).and(qNonSerializedItem.ownerType.equalsIgnoreCase(ownerType)).and(qNonSerializedItem.currentInwardId.eq(inwardId));
            List<NonSerializedItem> nonSerializedItemList = IterableUtils.toList(nonSerializedItemRepository.findAll(booleanExpression));
//        List<InOutWardMACMapping> result = new ArrayList<>();
//        for (int i=0; i<itemList.size(); i++) {
//            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
//            BooleanExpression aBoolean = qInOutWardMACMapping.isDeleted.eq(false).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.itemId.eq(itemList.get(i).getId())).and(qInOutWardMACMapping.isReturned.eq(0));
//            List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(aBoolean));
//            for (int j=0; j<inOutWardMACMappingList.size(); j++) {
//                result.add(inOutWardMACMappingList.get(j));
//            }
//        }
            return nonSerializedItemList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for Get Non Serialized Item for Outward
     * @Author Darshan
     * @param id
     * @param ownerId
     * @param ownerType
     * @return
     */
    public List<NonSerializedItem> getNonSerializedItemForOutward(Long id, Long ownerId, String ownerType) {
        QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
        BooleanExpression booleanExpression = qNonSerializedItem.isDeleted.eq(false).and(qNonSerializedItem.productId.eq(id)).and(qNonSerializedItem.ownerId.eq(ownerId)).and(qNonSerializedItem.ownerType.equalsIgnoreCase(ownerType));
        return IterableUtils.toList(nonSerializedItemRepository.findAll(booleanExpression));
    }

    public List<InOutWardMACMapingDTO> getInOutMacMappingForNonSerializedItem(Long id, Long ownerId, String ownerType) {
        try {
            QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
            BooleanExpression booleanExpression = qNonSerializedItem.isDeleted.eq(false).and(qNonSerializedItem.productId.eq(id)).and(qNonSerializedItem.ownerId.eq(ownerId))
                    .and(qNonSerializedItem.ownerType.equalsIgnoreCase(ownerType)).and(qNonSerializedItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qNonSerializedItem.itemStatus.ne(CommonConstants.DEFECTIVE));
            List<NonSerializedItem> nonSerializedItemList = IterableUtils.toList(nonSerializedItemRepository.findAll(booleanExpression));
            List<Long> nonSerializedItemIds = nonSerializedItemList.stream().map(NonSerializedItem::getId).collect(Collectors.toList());
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
            boolExp = qInOutWardMACMapping.nonSerializedItemId.in(nonSerializedItemIds).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull());;
            List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(boolExp));
            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS= (List<InOutWardMACMapingDTO>)inOutWardMACService.getEntityForUpdateAndDelete(inOutWardMACMappingList.get(0).getId());
            inOutWardMACMapingDTOS.stream().forEach(r->{
                r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());

            });
            return inOutWardMACMapingDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}



