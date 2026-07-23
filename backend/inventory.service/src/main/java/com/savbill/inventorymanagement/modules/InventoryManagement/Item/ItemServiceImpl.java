package com.savbill.inventorymanagement.modules.InventoryManagement.Item;

import com.savbill.inventorymanagement.core.constants.ClientServiceConstant;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.SearchConstants;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceService;
import com.savbill.inventorymanagement.modules.Customers.CustomerService;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.modules.Customers.QCustomers;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionsMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.QProductplanmapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.*;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaDTO;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerService;
import com.savbill.inventorymanagement.modules.PartnerServiceAreaMapping.PartnerServiceAreaMapping;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.fileUtillity.FileUtility;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.BulkConsumption.BulkConsumptionRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.GenerateremoveInventoryRequest.GenerateRemoveRequest;
import com.savbill.inventorymanagement.modules.InventoryManagement.GenerateremoveInventoryRequest.GenerateRemoveRequestRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.Inward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.QInward;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionMappingServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemConditionMapping.ItemConditionsMappingDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemStatusMapping.ItemStatusMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemStatusMapping.ItemStatusMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.NonSerializedItem;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.NonSerializedItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.QNonSerializedItem;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.QProduct;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.ProductPlanGroupMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.ProductPlanGroupMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.QProductPlanGroupMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.Productplanmapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.ProductPlanMappingRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMapping;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.inventorymanagement.modules.PartnerManagement.Partner;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerRepository;
import com.savbill.inventorymanagement.modules.PartnerServiceAreaMapping.PartnerServiceAreaMappingRepo;
import com.savbill.inventorymanagement.modules.PlanService.PlanService;
import com.savbill.inventorymanagement.modules.PlanService.PlanServiceRepository;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlan;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlanRepo;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserService;
import com.savbill.inventorymanagement.utils.APIConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The type Item service.
 */
@Service
public class ItemServiceImpl extends ExBaseAbstractService<ItemDto, Item, Long> {

    @Autowired
    ItemMapper itemMapper;
    @Autowired
    public ItemRepository itemRepository;
    //    @Autowired
//    ChargeService chargeService;
    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    ProductOwnerService productOwnerService;
    @Autowired
    public InwardServiceImpl inwardService;
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
    StaffUserService staffUserService;
    @Autowired
    PartnerService partnerService;
    @Autowired
    CustomerService customerService;
    @Autowired
    PopManagementService popManagementService;
    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;
    @Autowired
    PopManagementRepository popManagementRepository;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    @Autowired
    PopServiceAreaMappingRepo popServiceAreaMappingRepo;
    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    ItemConditionMappingServiceImpl itemConditionMappingService;

    @Autowired
    ItemWarrantyMappingServiceImpl itemWarrantyMappingService;

    @Autowired
    ItemWarrantyMappingRepository itemWarrantyMappingRepository;

    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;

    @Autowired
    public FileUtility fileUtility;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    public ItemConditionMappingRepository itemConditionMappingRepository;

    @Autowired
    ItemStatusMappingRepo itemStatusMappingRepo;

    @Autowired
    BulkConsumptionRepository bulkConsumptionRepository;
    @Autowired
    InOutWardMACService inOutWardMACService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;

    @Autowired
    ProductPlanMappingRepository productPlanMappingRepository;

    @Autowired
    ProductPlanGroupMappingRepository productPlanGroupMappingRepository;

    @Autowired
    NonSerializedItemRepository nonSerializedItemRepository;

    @Autowired
    CustomerInventoryMappingMapper customerInventoryMappingMapper;
    @Autowired
    OutwardRepository outwardRepository;
    @Autowired
    ExternalItemManagementRepository externalItemManagementRepository;
    @Autowired
    PlanServiceRepository planServiceRepository;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    WareHouseManagmentServiceAreamappingRepo wareHouseManagmentServiceAreamappingRepo;

    @Autowired
    ServiceAreaService serviceAreaService;

//    @Autowired
//    CustomersService customersService;

    @Autowired
    StaffUserRepository staffUserRepository;

    public static final String MODULE = "[CreditDocService]";

    public String PATH;


    @Autowired
    ProductServiceImpl productService;

    /**
     * Instantiates a new Item service.
     * @param itemRepository the item repository
     * @param mapper the mapper
     */
    public ItemServiceImpl(ItemRepository itemRepository, IBaseMapper<ItemDto, Item> mapper) {
        super(itemRepository, mapper);
    }

    public static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
    @Autowired
    public CustomersRepository customersRepository;
    @Autowired
    public CustomerInventoryMappingRepo customerInventoryMappingRepo;
    @Autowired
    public ProductCategoryRepository productCategoryRepository;
    @Autowired
    public GenerateRemoveRequestRepo generateRemoveRequestRepo;

    @Autowired
    ClientServiceService clientServiceSrv;

    /**
     * Gets module name for log.
     * @return the module name for log
     */
    @Override
    public String getModuleNameForLog() {
        return "[ProductServiceImpl]";
    }

    /**
     * Gets list by page and size and sort by and order by.
     * @param pageNumber the page number
     * @param customPageSize the custom page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param filterList the filter list
     * @return the list by page and size and sort by and order by
     */
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        QProduct qProduct = QProduct.product;
        BooleanExpression booleanExpression = qProduct.isNotNull().and(qProduct.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Item> paginationList = null;
        try {
            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                paginationList = itemRepository.findAll(pageRequest);
//            } else {
            paginationList = itemRepository.findAll(booleanExpression, pageRequest);
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

    /**
     * Search generic data dto.
     * @param filterList the filter list
     * @param page the page
     * @param pageSize the page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the generic data dto
     */
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

    /**
     * Gets product list.
     * @param name the name
     * @param pageRequest the page request
     * @return the product list
     */
    public GenericDataDTO getProductList(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getProductList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Page<Item> productList;
            if (getMvnoIdFromCurrentStaff() == 1)
                productList = itemRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
            else
                productList = itemRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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

    /**
     * Delete entity.
     * @param entity the entity
     * @throws Exception the exception
     */
    @Override
    public void deleteEntity(ItemDto entity) throws Exception {
        super.deleteEntity(entity);
    }

    /**
     * Save entity item dto.
     * @param entity the entity
     * @return the item dto
     * @throws Exception the exception
     */
    @Override
    public ItemDto saveEntity(ItemDto entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        return super.saveEntity(entity);
    }


    /**
     * Save entity from rms item dto.
     * @param entity the entity
     * @return the item dto
     * @throws Exception the exception
     */
    public ItemDto saveEntityFromRms(ItemDto entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());
        return super.saveEntity(entity);
    }

    /**
     * Duplicate verify at save boolean.
     * @param name the name
     * @return the boolean
     * @throws Exception the exception
     */
    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = itemRepository.duplicateVerifyAtSave(name);
            else count = itemRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * Delete verification boolean.
     * @param id the id
     * @return the boolean
     * @throws Exception the exception
     */
    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = itemRepository.deleteVerify(id);
        if (count == 1) {
            flag = true;
        }
        return flag;
    }

    /**
     * Duplicate verify at edit boolean.
     * @param name the name
     * @param id the id
     * @return the boolean
     * @throws Exception the exception
     */
    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        try {
            boolean flag = false;
            List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
            if (name != null) {
                name = name.trim();
                Integer count;
                if (getMvnoIdFromCurrentStaff() == 1) count = itemRepository.duplicateVerifyAtSave(name);
                else count = itemRepository.duplicateVerifyAtSave(name, mvnoIds);
                if (count >= 1) {
                    Integer countEdit;
                    if (getMvnoIdFromCurrentStaff() == 1)
                        countEdit = itemRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id));
                    else countEdit = itemRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id), mvnoIds);
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

    /**
     * Gets all items by owner.
     * @param pageNumber the page number
     * @param customPageSize the custom page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param filterList the filter list
     * @param ownerId the owner id
     * @param ownerType the owner type
     * @return the all items by owner
     * @throws Exception the exception
     */
    public GenericDataDTO getAllItemsByOwner(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long ownerId, String ownerType) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [getAssignInventories()] ";
        QItem qItem = QItem.item;
        BooleanExpression booleanExpression = qItem.isNotNull().and(qItem.ownerType.equalsIgnoreCase(ownerType)).and(qItem.ownerId.eq(ownerId).and(qItem.isDeleted.eq(false)));
//        List<ServiceArea> serviceAreaList = serviceAreaService.getAllServiceAreaByStaffId();
//        List<StaffUser> staffUserList = staffUserServiceAreaMappingRepository.find();
//        if (ownerType.equalsIgnoreCase(CommonConstants.STAFF)){
//            booleanExpression = booleanExpression.and()
//        }
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Item> paginationList = null;
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qItem.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            paginationList = itemRepository.findAll(booleanExpression, pageRequest);

            List<ItemDto> dto = paginationList.get().map(item -> itemMapper.domainToDTO(item, new CycleAvoidingMappingContext())).collect(Collectors.toList());

            List<ItemDto> itemDtoList = new ArrayList<>();
            for (ItemDto itemDto : dto) {

                itemDto.setCurrentInwardNumber(inwardRepository.findById(itemDto.getCurrentInwardId()).get().getInwardNumber());
                itemDto.setProductName(productService.getEntityById(itemDto.getProductId().longValue()).getName());
//                System.out.println(itemDto.getId());
//                System.out.println("*********************()()()()()()");
                if (!itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).isEmpty()) {
                    itemDto.setFilename(itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).get(0).getFilename());
                    itemDto.setItemConditionId(itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).get(0).getId());
                }
                if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.STAFF)) {
                    StaffUser staffUser = staffUserRepository.findById(Math.toIntExact(itemDto.getOwnerId())).get();
                    itemDto.setOwnerName(staffUser.getUsername());
                } else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.WAREHOUSE))
                    itemDto.setOwnerName(warehouseManagementService.getEntityById(itemDto.getOwnerId().longValue()).getName());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.POP))
                    itemDto.setOwnerName(popManagementService.getEntityById(itemDto.getOwnerId().longValue()).getName());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.SERVICE_AREA)) {
                    ServiceArea serviceArea = serviceAreaRepository.findById(itemDto.getOwnerId()).get();
                    itemDto.setOwnerName(serviceArea.getName());
                } else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.PARTNER)) {
                    Partner partner = partnerRepository.findById(Math.toIntExact(itemDto.getOwnerId())).get();
                    itemDto.setOwnerName(partner.getName());
                }

                itemDtoList.add(itemDto);
            }

            genericDataDTO.setDataList(itemDtoList);

            genericDataDTO.setTotalRecords(paginationList.getTotalElements());
            genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
            genericDataDTO.setTotalPages(paginationList.getTotalPages());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());


        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    /**
     * Return item string.
     * @param itemReturnDTOList the item return dto list
     * @return the string
     */
    @Transactional
    public String returnItem(List<ItemReturnDTO> itemReturnDTOList) {
        // Considering all itemIds here are of same inward
        List<Long> itemIds = new ArrayList<>();
        for (ItemReturnDTO itemReturnDTO : itemReturnDTOList) {
            itemIds.add(itemReturnDTO.getId());
        }
        try {
            List<Item> itemList = itemRepository.findAllById(itemIds);
            Map<String, String> serialRemarksMap = new HashMap<>();
            for (int i = 0; i < itemList.size(); i++) {
                serialRemarksMap.put(itemList.get(i).getSerialNumber(), itemReturnDTOList.get(i).getRemarks());
            }
            Inward inward = inwardRepository.findById(itemList.get(0).getCurrentInwardId()).get();

            // Outward by taking destination as inward's source and source as inward's destination
            OutwardDto outwardDto = new OutwardDto();
            outwardDto.setQty((long) itemList.size());
            outwardDto.setStatus(inward.getStatus());
            outwardDto.setProductId(inward.getProductId());
            outwardDto.setMvnoId(inward.getMvnoId());
            outwardDto.setOutwardDateTime(LocalDateTime.now());
            outwardDto.setIsDeleted(false);
            outwardDto.setInwardId(inward);
            outwardDto.setSourceType(inward.getDestinationType());
            outwardDto.setSourceId(inward.getDestinationId());
            outwardDto.setDestinationType(inward.getSourceType());
            outwardDto.setDestinationId(inward.getSourceId());
            outwardDto.setInTransitQty((long) itemIds.size());
            outwardDto.setCategoryType(CommonConstants.RETURNED_INWARD_TYPE);
            outwardDto.setType(CommonConstants.NEW);
            //Return Outward and Inward
            OutwardDto savedOutward = outwardService.saveEntity(outwardDto, true);
            QInward qInward = QInward.inward;
            BooleanExpression inwardBoolExp = qInward.isNotNull();
            inwardBoolExp = inwardBoolExp.and(qInward.outwardId.id.eq(savedOutward.getId()));
            Inward inwardOfSavedOutward = inwardRepository.findOne(inwardBoolExp).get();

            // Get List of serials by current items productId, inwardId and is_forwarded = 0(items which are not forwarded and currently with destination)
            // place is_returned = 1 in them and create new mappings with by placing inwardIdOfOutward into existing records just like when we select mac from outward, new entry is created in mac,
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
            booleanExpression = booleanExpression.and(qInOutWardMACMapping.inwardId.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(0)));
//                    .or(qInOutWardMACMapping.inwardIdOfOutward.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(1)));
            List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
            inOutWardMACMappingList = inOutWardMACMappingList.stream().filter(macMapping -> itemList.stream().map(Item::getSerialNumber).collect(Collectors.toSet()).contains(macMapping.getSerialNumber())).collect(Collectors.toList());

            List<InOutWardMACMapping> newInOutwardMacMapping = new ArrayList<>();
            for (InOutWardMACMapping inOutWardMACMapping : inOutWardMACMappingList) {
                InOutWardMACMapingDTO macMapping = new InOutWardMACMapingDTO();
                macMapping = inOutWardMacMapper.domainToDTO(inOutWardMACMapping, new CycleAvoidingMappingContext());
                macMapping.setId(null);
                macMapping.setInwardId(inwardOfSavedOutward.getId());
                macMapping.setOutwardId(savedOutward.getId());
                macMapping.setIsForwarded(0);
                InOutWardMACMapingDTO finalMacMapping = macMapping;
                String remark = serialRemarksMap.get(serialRemarksMap.keySet().stream().filter(s -> s.equalsIgnoreCase(finalMacMapping.getSerialNumber())).collect(Collectors.toList()).get(0));
                macMapping.setRemark(remark);
                newInOutwardMacMapping.add(inOutWardMacMapper.dtoToDomain(macMapping, new CycleAvoidingMappingContext()));
            }
            inOutWardMacRepo.saveAll(newInOutwardMacMapping);
            inOutWardMACMappingList.forEach(s -> s.setIsReturned(1));
            inOutWardMACMappingList.forEach(s -> s.setIsForwarded(1));
            inOutWardMacRepo.saveAll(inOutWardMACMappingList);
            for (Item item : itemList) {
                item.setCurrentInwardId(inwardOfSavedOutward.getId());
                item.setCurrentInwardType(CommonConstants.RETURNED_INWARD_TYPE);
                item.setOwnerType(inwardOfSavedOutward.getDestinationType());
                item.setOwnerId(inwardOfSavedOutward.getDestinationId());
//                itemList.forEach(s -> s.setCurrentInwardId(inwardOfSavedOutward.getId()));
            }
            itemRepository.saveAll(itemList);

            // manage product owner quantities after return

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
        return "Items returned";
    }

    /**
     * Return itemfrom staffremove string.
     * @param itemReturnDTOList the item return dto list
     * @return the string
     */
    @Transactional
    public String returnItemfromStaffremove(Item item) {
        // Considering all itemIds here are of same inward
//        List<Long> itemIds = new ArrayList<>();
//        for (ItemReturnDTO itemReturnDTO : itemReturnDTOList) {
//            itemIds.add(itemReturnDTO.getId());
//        }
        try {
//            List<Item> itemList = itemRepository.findAllById(itemIds);
            //  Map<String, String> serialRemarksMap = new HashMap<>();
            /*for (int i = 0; i < itemList.size(); i++) {
                serialRemarksMap.put(itemList.get(i).getSerialNumber(), itemReturnDTOList.get(i).getRemarks());
            }*/
            Inward inward = inwardRepository.findById(item.getCurrentInwardId()).get();

            // Outward by taking destination as inward's source and source as inward's destination
            OutwardDto outwardDto = new OutwardDto();
            outwardDto.setQty(1L);
            outwardDto.setStatus(inward.getStatus());
            outwardDto.setProductId(inward.getProductId());
            outwardDto.setMvnoId(inward.getMvnoId());
            outwardDto.setOutwardDateTime(LocalDateTime.now());
            outwardDto.setIsDeleted(false);
            outwardDto.setSourceType(inward.getDestinationType());
            outwardDto.setSourceId(inward.getDestinationId());
            if (getLoggedInUser().getPartnerId() != 1) {
                outwardDto.setDestinationType(CommonConstants.PARTNER);
                outwardDto.setDestinationId(Long.valueOf(getLoggedInUser().getPartnerId()));
            } else {
                outwardDto.setDestinationType(CommonConstants.STAFF);
                outwardDto.setDestinationId(Long.valueOf(getLoggedInUser().getUserId()));
            }
            outwardDto.setInTransitQty(1L);
            outwardDto.setCategoryType(CommonConstants.RETURNED_INWARD_TYPE);
            outwardDto.setType(CommonConstants.NEW);
            //Return Outward and Inward
            OutwardDto savedOutward = outwardService.saveEntity(outwardDto, true);
            QInward qInward = QInward.inward;
            BooleanExpression inwardBoolExp = qInward.isNotNull();
            inwardBoolExp = inwardBoolExp.and(qInward.outwardId.id.eq(savedOutward.getId()));
            Inward inwardOfSavedOutward = inwardRepository.findOne(inwardBoolExp).get();

            // Get List of serials by current items productId, inwardId and is_forwarded = 0(items which are not forwarded and currently with destination)
            // place is_returned = 1 in them and create new mappings with by placing inwardIdOfOutward into existing records just like when we select mac from outward, new entry is created in mac,
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
            booleanExpression = booleanExpression.and(qInOutWardMACMapping.inwardId.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(0)));
//                    .or(qInOutWardMACMapping.inwardIdOfOutward.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(1)));
            List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
            inOutWardMACMappingList = inOutWardMACMappingList.stream()
                    .filter(macMapping -> item.getSerialNumber().equals(macMapping.getSerialNumber()))
                    .collect(Collectors.toList());
            List<InOutWardMACMapping> newInOutwardMacMapping = new ArrayList<>();
            for (InOutWardMACMapping inOutWardMACMapping : inOutWardMACMappingList) {
                InOutWardMACMapingDTO macMapping = new InOutWardMACMapingDTO();
                macMapping = inOutWardMacMapper.domainToDTO(inOutWardMACMapping, new CycleAvoidingMappingContext());
                macMapping.setId(null);
                macMapping.setInwardId(inwardOfSavedOutward.getId());
                macMapping.setOutwardId(savedOutward.getId());
                macMapping.setIsForwarded(0);
                macMapping.setInReplacementProcess(false);
                InOutWardMACMapingDTO finalMacMapping = macMapping;
                //   String remark = serialRemarksMap.get(serialRemarksMap.keySet().stream().filter(s -> s.equalsIgnoreCase(finalMacMapping.getSerialNumber())).collect(Collectors.toList()).get(0));
                //   macMapping.setRemark(remark);
                newInOutwardMacMapping.add(inOutWardMacMapper.dtoToDomain(macMapping, new CycleAvoidingMappingContext()));
            }
            inOutWardMacRepo.saveAll(newInOutwardMacMapping);
            inOutWardMACMappingList.forEach(s -> s.setIsReturned(1));
            inOutWardMACMappingList.forEach(s -> s.setIsForwarded(1));
            inOutWardMacRepo.saveAll(inOutWardMACMappingList);
            savedItems(item, inwardOfSavedOutward);
            // manage product owner quantities after return

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
        return "Items returned";
    }

    /**
     * Saved items.
     * @param itemList the item list
     * @param inwardOfSavedOutward the inward of saved outward
     */
    @Transactional
    public void savedItems(Item item, Inward inwardOfSavedOutward) {
//        System.out.println("Save Item Started");
        try {
            Long pcId = productRepository.findProductCategoryIdByProductId(item.getProductId());
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            boolean hasTrackable = productCategoryRepository.findHasTrackableById(pcId);
            String uom = productCategoryRepository.findUnitById(pcId);
//        itemList.forEach(item -> {
            item.setCurrentInwardId(inwardOfSavedOutward.getId());
            item.setCurrentInwardType(CommonConstants.RETURNED_INWARD_TYPE);
            item.setOwnerType(inwardOfSavedOutward.getDestinationType());
            item.setOwnerId(inwardOfSavedOutward.getDestinationId());
//        });
            Item savedItem = itemRepository.save(item);
            List<Item> itemList = Collections.singletonList(savedItem);
            inwardService.saveInwardApproval(
                    inwardOfSavedOutward,
                    CommonConstants.APPROVE,
                    "",
                    savedItem.getProductId(),
                    itemList,
                    hasMac, hasSerial, hasTrackable,
                    inwardOfSavedOutward.getOutwardId(),
                    false);
//        System.out.println("Save Item Ended");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Replace andreturn itemfrom staffremove string.
     * @param itemReturnDTOList the item return dto list
     * @param custId the cust id
     * @return the string
     */
    @Transactional
    public String replaceAndreturnItemfromStaffremove(Item item, Integer custId) {
//        System.out.println("Replace and Return Item From Staff Remove Started");
        try {
            Inward inward = inwardRepository.findById(item.getCurrentInwardId()).get();
            // Outward by taking destination as inward's source and source as inward's destination
            OutwardDto outwardDto = new OutwardDto();
            outwardDto.setQty(1L);
            outwardDto.setStatus(inward.getStatus());
            outwardDto.setProductId(inward.getProductId());
            outwardDto.setMvnoId(inward.getMvnoId());
            outwardDto.setOutwardDateTime(LocalDateTime.now());
            outwardDto.setIsDeleted(false);
            outwardDto.setSourceType(inward.getDestinationType());
            outwardDto.setSourceId(inward.getDestinationId());
            if (getLoggedInUser().getPartnerId() != 1) {
                Long id = item.getId();
                QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(id)).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(custId)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS));
                CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                outwardDto.setDestinationType(CommonConstants.PARTNER);
                Integer partnerIdByUserId = staffUserRepository.findPartnerIdByUserId(Integer.valueOf(customerInventoryMapping.getPreviousApproveId()));
                outwardDto.setDestinationId(partnerIdByUserId.longValue());
            } else {
                Long id = item.getId();
                QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(id)).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(custId)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS));
                CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                outwardDto.setDestinationType(CommonConstants.STAFF);
                outwardDto.setDestinationId(customerInventoryMapping.getPreviousApproveId().longValue());
            }
            outwardDto.setInTransitQty(1L);
            outwardDto.setCategoryType(CommonConstants.RETURNED_INWARD_TYPE);
            outwardDto.setType(CommonConstants.NEW);
            //Return Outward and Inward
            OutwardDto savedOutward = outwardService.saveEntity(outwardDto, true);
            QInward qInward = QInward.inward;
            BooleanExpression inwardBoolExp = qInward.isNotNull();
            inwardBoolExp = inwardBoolExp.and(qInward.outwardId.id.eq(savedOutward.getId()));
            Inward inwardOfSavedOutward = inwardRepository.findOne(inwardBoolExp).get();

            // Get List of serials by current items productId, inwardId and is_forwarded = 0(items which are not forwarded and currently with destination)
            // place is_returned = 1 in them and create new mappings with by placing inwardIdOfOutward into existing records just like when we select mac from outward, new entry is created in mac,
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
            booleanExpression = booleanExpression.and(qInOutWardMACMapping.inwardId.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(0)));
//                    .or(qInOutWardMACMapping.inwardIdOfOutward.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(1)));
            List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
            inOutWardMACMappingList = inOutWardMACMappingList.stream()
                    .filter(macMapping -> item.getSerialNumber().equals(macMapping.getSerialNumber()))
                    .collect(Collectors.toList());
            List<InOutWardMACMapping> newInOutwardMacMapping = new ArrayList<>();
            for (InOutWardMACMapping inOutWardMACMapping : inOutWardMACMappingList) {
                InOutWardMACMapingDTO macMapping = new InOutWardMACMapingDTO();
                macMapping = inOutWardMacMapper.domainToDTO(inOutWardMACMapping, new CycleAvoidingMappingContext());
                macMapping.setId(null);
                macMapping.setInwardId(inwardOfSavedOutward.getId());
                macMapping.setOutwardId(savedOutward.getId());
                macMapping.setIsForwarded(0);
                InOutWardMACMapingDTO finalMacMapping = macMapping;
                //   String remark = serialRemarksMap.get(serialRemarksMap.keySet().stream().filter(s -> s.equalsIgnoreCase(finalMacMapping.getSerialNumber())).collect(Collectors.toList()).get(0));
                //   macMapping.setRemark(remark);
                newInOutwardMacMapping.add(inOutWardMacMapper.dtoToDomain(macMapping, new CycleAvoidingMappingContext()));
            }
            inOutWardMacRepo.saveAll(newInOutwardMacMapping);
            inOutWardMACMappingList.forEach(s -> s.setIsReturned(1));
            inOutWardMACMappingList.forEach(s -> s.setIsForwarded(1));
            inOutWardMacRepo.saveAll(inOutWardMACMappingList);
            savedItems(item, inwardOfSavedOutward);

            // manage product owner quantities after return

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
//        System.out.println("Replace and Return Item From Staff Remove Ended");
        return "Items returned";
    }


    /**
     * Remove andreturn itemfrom staffremove string.
     * @param itemReturnDTOList the item return dto list
     * @param customerInventoryMapping the customer inventory mapping
     * @return the string
     */
    @Transactional
    public String removeAndreturnItemfromStaffremove(Item item, CustomerInventoryMapping customerInventoryMapping) {
        // Considering all itemIds here are of same inward
//        List<Long> itemIds = new ArrayList<>();
//        for (ItemReturnDTO itemReturnDTO : itemReturnDTOList) {
//            itemIds.add(itemReturnDTO.getId());
//        }
        try {
//            Item itemList = itemRepository.findById(itemId);
            //  Map<String, String> serialRemarksMap = new HashMap<>();
            /*for (int i = 0; i < itemList.size(); i++) {
                serialRemarksMap.put(itemList.get(i).getSerialNumber(), itemReturnDTOList.get(i).getRemarks());
            }*/
            Inward inward = inwardRepository.findById(item.getCurrentInwardId()).get();

            // Outward by taking destination as inward's source and source as inward's destination
            OutwardDto outwardDto = new OutwardDto();
            outwardDto.setQty(1L);
            outwardDto.setStatus(inward.getStatus());
            outwardDto.setProductId(inward.getProductId());
            outwardDto.setMvnoId(inward.getMvnoId());
            outwardDto.setOutwardDateTime(LocalDateTime.now());
            outwardDto.setIsDeleted(false);
            outwardDto.setSourceType(inward.getDestinationType());
            outwardDto.setSourceId(inward.getDestinationId());
            if (getLoggedInUser().getPartnerId() != 1) {
//                Long id = item.getId();
//                QCustomerInventoryMapping qCustomerInventoryMapping=QCustomerInventoryMapping.customerInventoryMapping;
//                BooleanExpression booleanExpression=qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(id)).and(qCustomerInventoryMapping.isDeleted.eq(false));;
//                CustomerInventoryMapping customerInventoryMapping=customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryMapping.getId());
                if (generateRemoveRequest != null) {
                    outwardDto.setDestinationType(CommonConstants.PARTNER);
                    StaffUser staffUser = staffUserRepository.findById(Math.toIntExact(generateRemoveRequest.getStaffid())).get();
                    outwardDto.setDestinationId(staffUser.getPartnerid().longValue());
                }
            } else {
//                Long id = item.getId();
//                QCustomerInventoryMapping qCustomerInventoryMapping=QCustomerInventoryMapping.customerInventoryMapping;
//                BooleanExpression booleanExpression=qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(id)).and(qCustomerInventoryMapping.isDeleted.eq(false));
//                CustomerInventoryMapping customerInventoryMapping=customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryMapping.getId());
                if (generateRemoveRequest != null) {
                    outwardDto.setDestinationType(CommonConstants.STAFF);
                    outwardDto.setDestinationId(Long.valueOf(generateRemoveRequest.getStaffid()));
                }
            }
            outwardDto.setInTransitQty(1L);
            outwardDto.setCategoryType(CommonConstants.RETURNED_INWARD_TYPE);
            outwardDto.setType(CommonConstants.NEW);
            //Return Outward and Inward
            OutwardDto savedOutward = outwardService.saveEntity(outwardDto, true);
            QInward qInward = QInward.inward;
            BooleanExpression inwardBoolExp = qInward.isNotNull();
            inwardBoolExp = inwardBoolExp.and(qInward.outwardId.id.eq(savedOutward.getId()));
            Inward inwardOfSavedOutward = inwardRepository.findOne(inwardBoolExp).get();

            // Get List of serials by current items productId, inwardId and is_forwarded = 0(items which are not forwarded and currently with destination)
            // place is_returned = 1 in them and create new mappings with by placing inwardIdOfOutward into existing records just like when we select mac from outward, new entry is created in mac,
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
            booleanExpression = booleanExpression.and(qInOutWardMACMapping.inwardId.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(0)));
//                    .or(qInOutWardMACMapping.inwardIdOfOutward.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(1)));
            List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
            inOutWardMACMappingList = inOutWardMACMappingList.stream()
                    .filter(macMapping -> item.getSerialNumber().equals(macMapping.getSerialNumber()))
                    .collect(Collectors.toList());
            List<InOutWardMACMapping> newInOutwardMacMapping = new ArrayList<>();
            for (InOutWardMACMapping inOutWardMACMapping : inOutWardMACMappingList) {
                InOutWardMACMapingDTO macMapping = new InOutWardMACMapingDTO();
                macMapping = inOutWardMacMapper.domainToDTO(inOutWardMACMapping, new CycleAvoidingMappingContext());
                macMapping.setId(null);
                macMapping.setInwardId(inwardOfSavedOutward.getId());
                macMapping.setOutwardId(savedOutward.getId());
                macMapping.setIsForwarded(0);
                InOutWardMACMapingDTO finalMacMapping = macMapping;
                //   String remark = serialRemarksMap.get(serialRemarksMap.keySet().stream().filter(s -> s.equalsIgnoreCase(finalMacMapping.getSerialNumber())).collect(Collectors.toList()).get(0));
                //   macMapping.setRemark(remark);
                newInOutwardMacMapping.add(inOutWardMacMapper.dtoToDomain(macMapping, new CycleAvoidingMappingContext()));
            }
            inOutWardMacRepo.saveAll(newInOutwardMacMapping);
            inOutWardMACMappingList.forEach(s -> s.setIsReturned(1));
            inOutWardMACMappingList.forEach(s -> s.setIsForwarded(1));
            inOutWardMacRepo.saveAll(inOutWardMACMappingList);
            savedItems(item, inwardOfSavedOutward);

            // manage product owner quantities after return

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
        return "Items returned";
    }


    /**
     * Item return check boolean.
     * @param itemIds the item ids
     * @return the boolean
     */
    public Boolean itemReturnCheck(List<Long> itemIds) {
        Boolean canReturnItems = false;
//    As gui is giving same inwards items so no need to place check
//    Before hitting this api, check all ids selected has same inward, if not give error from gui

//        avoid first inward from return by sending false, if not first inward then it will has outward and
//        can be returned
        Inward inward = inwardRepository.findById(itemRepository.findById(itemIds.get(0)).get().getCurrentInwardId()).get();
        if (inward.getOutwardId() != null)
            canReturnItems = true;
        return canReturnItems;
    }

    /**
     * Update item warranty by list generic data dto.
     * @param itemWarrantyTypeDTOS the item warranty type dtos
     * @return the generic data dto
     * @throws Exception the exception
     */
    public GenericDataDTO updateItemWarrantyByList(List<ItemWarrantyTypeDTO> itemWarrantyTypeDTOS) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        QItem qItem = QItem.item;
        try {
            List<Long> itemIds = new ArrayList<>();
            for (ItemWarrantyTypeDTO itemWarrantyTypeDTO : itemWarrantyTypeDTOS) {
                itemIds.add(itemWarrantyTypeDTO.getId());
            }
            List<Item> itemList = itemRepository.findAllById(itemIds);

            List<ItemDto> lst = new ArrayList<>();
            if (itemList != null) {
                if (itemList.size() > 0)
                    for (int i = 0; i <= itemList.size() - 1; i++) {
                        ItemDto itemDto = getEntityForUpdateAndDelete(itemIds.get(i));
                        String expiryTimeUnit = productRepository.findExpiryTimeUnitByProductId(itemDto.getProductId());
                        Integer expiryTime = productRepository.findExpiryTimeByProductId(itemDto.getProductId());
                        String warranty = itemWarrantyTypeDTOS.get(i).getWarranty();
                        itemDto.setWarranty(warranty);
                        if (itemDto.getRemainingDays() == null) {
                            if (expiryTimeUnit != null && expiryTimeUnit.equalsIgnoreCase("Month")) {
                                LocalDateTime expDate = LocalDateTime.now().plusMonths(expiryTime);
                                itemDto.setExpireDate(expDate);
                                LocalDateTime now = LocalDateTime.now();
                                Duration duration = Duration.between(now, expDate);
                                long remainingDays = duration.toDays();
                                itemDto.setRemainingDays(String.valueOf(remainingDays));
                                itemDto.setWarrantyPeriod((int) remainingDays);
                            }
                            if (expiryTimeUnit != null && expiryTimeUnit.equalsIgnoreCase("Day")) {
                                LocalDateTime expDate = LocalDateTime.now().plusDays(expiryTime);
                                itemDto.setExpireDate(expDate);
                                Duration duration = Duration.between(LocalDateTime.now(), expDate);
                                itemDto.setRemainingDays(String.valueOf(duration.toDays()));
                                itemDto.setWarrantyPeriod((int) duration.toDays());
                            }
                        }
                        List<ItemWarrantyMapping> itemWarrantyMappings = itemWarrantyMappingRepository.findByItemId(itemIds.get(i));
                        if (!itemWarrantyMappings.isEmpty()) {
                            itemWarrantyMappings.forEach(itemWarrantyMapping -> {
                                itemWarrantyMapping.setWarranty(warranty);
                                itemWarrantyMappingRepository.save(itemWarrantyMapping);
                            });
                        }
                        lst.add(super.updateEntity(itemDto));
                    }
                dataDTO.setDataList(lst);

            }
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    /**
     * Update item type by list generic data dto.
     * @param itemChangeTypeDto the item change type dto
     * @param files the files
     * @return the generic data dto
     * @throws Exception the exception
     */
    public GenericDataDTO updateItemTypeByList(List<ItemChangeTypeDto> itemChangeTypeDto, List<MultipartFile> files) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        QItem qItem = QItem.item;
        try {
            ItemConditionsMappingDto itemConditionsMapping = new ItemConditionsMappingDto();

            List<Long> itemIds = new ArrayList<>();
            for (ItemChangeTypeDto itemChangeTypeDto1 : itemChangeTypeDto) {
                itemIds.add(itemChangeTypeDto1.getItemId());
            }
            List<Item> itemList = itemRepository.findAllById(itemIds);

            List<ItemDto> lst = new ArrayList<>();
            if (itemList != null) {
                if (itemList.size() > 0)
                    for (int i = 0; i <= itemList.size() - 1; i++) {
                        ItemDto itemDto = getEntityForUpdateAndDelete(itemIds.get(i));
                        if ((itemDto.getWarranty().equalsIgnoreCase(CommonConstants.EXPIRED)) && (itemDto.getCondition().equalsIgnoreCase(CommonConstants.NEW))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                            if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                            }
                            if (!files.isEmpty()) {
                                if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                    uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                    itemConditionsMapping.setFilename(files.get(i).getOriginalFilename());
                                    //itemConditionsMapping.setUniquename(fileUtility.saveFileToServer(files ,path));
                                }
                            }
                            itemConditionsMapping.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setItemId(itemChangeTypeDto.get(i).getItemId());
                            itemConditionMappingService.saveEntity(itemConditionsMapping);

                            lst.add(super.updateEntity(itemDto));
                        } else if ((itemDto.getWarranty().equalsIgnoreCase(CommonConstants.EXPIRED)) && (itemDto.getCondition().equalsIgnoreCase(CommonConstants.NEW)) && (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                            if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                            }
                            if (!files.isEmpty()) {
                                if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                    uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                    itemConditionsMapping.setFilename(files.get(i).getOriginalFilename());
                                    //itemConditionsMapping.setUniquename(fileUtility.saveFileToServer(files ,path));
                                }
                            }
                            itemConditionsMapping.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setItemId(itemChangeTypeDto.get(i).getItemId());
                            itemConditionMappingService.saveEntity(itemConditionsMapping);

                            lst.add(super.updateEntity(itemDto));
                        } else if ((itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_STORE)) && (itemDto.getWarranty().equalsIgnoreCase(CommonConstants.EXPIRED)) && (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                        } else if ((!(itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_STORE) || (itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_SITE)))) && (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                            if (!files.isEmpty()) {
                                if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                    uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                    itemConditionsMapping.setFilename(files.get(i).getOriginalFilename());
                                }
                            }
                            itemConditionsMapping.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                            if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                            }
                            itemConditionsMapping.setItemId(itemIds.get(i));
                            itemConditionMappingService.saveEntity(itemConditionsMapping);

                            lst.add(super.updateEntity(itemDto));
                        } else if ((itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_STORE)) && (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                            if (files.isEmpty()) {
                                if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                    uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                    itemConditionsMapping.setFilename(files.get(i).getOriginalFilename());
                                }
                            }
                            itemConditionsMapping.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                            if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                            }
                            itemConditionsMapping.setItemId(itemIds.get(i));
                            if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                itemConditionsMapping.setFilename(itemChangeTypeDto.get(i).getFilename());
                            }
                            itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                            if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                            }
                            itemConditionsMapping.setIsDeleted(true);
                            itemConditionMappingService.saveEntity(itemConditionsMapping);
                            lst.add(super.updateEntity(itemDto));
                        } else if ((itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_SITE)) && (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setItemId(itemIds.get(i));
                            if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                itemConditionsMapping.setFilename(itemChangeTypeDto.get(i).getFilename());
                                itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                                if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                    itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                                }
                            }
                            itemConditionsMapping.setIsDeleted(true);
                            itemConditionMappingService.saveEntity(itemConditionsMapping);
                            lst.add(super.updateEntity(itemDto));
                        } else {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                            if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                            }
                            if (!files.isEmpty()) {
                                if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                    uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                    itemConditionsMapping.setFilename(files.get(i).getOriginalFilename());
                                }
                            }
                            itemConditionsMapping.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setItemId(itemChangeTypeDto.get(i).getItemId());
                            itemConditionMappingService.saveEntity(itemConditionsMapping);
                            lst.add(super.updateEntity(itemDto));
                        }
                    }
            }
            dataDTO.setDataList(lst);
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    /**
     * Update item type generic data dto.
     * @param itemId the item id
     * @param condition the condition
     * @return the generic data dto
     * @throws Exception the exception
     */
    public GenericDataDTO updateItemType(Long itemId, String condition) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        QItem qItem = QItem.item;
        try {
            ItemConditionsMappingDto itemConditionsMapping = new ItemConditionsMappingDto();
            ItemDto itemDto = getEntityForUpdateAndDelete(itemId);

            itemDto.setCondition(condition);
            itemConditionsMapping.setCondition(condition);
            itemConditionsMapping.setItemId(itemId);

            itemConditionMappingService.saveEntity(itemConditionsMapping);

            dataDTO.setData(super.updateEntity(itemDto));
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    /**
     * Update item warranty generic data dto.
     * @param itemId the item id
     * @param warranty the warranty
     * @return the generic data dto
     * @throws Exception the exception
     */
    @Transactional
    public GenericDataDTO updateItemWarranty(Item item, String warranty) throws Exception {
//        System.out.println("Update Item Warranty Started");
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            ItemDto itemDto = itemMapper.domainToDTO(item, new CycleAvoidingMappingContext());
            itemDto.setWarranty(warranty);
            List<ItemWarrantyMapping> itemWarrantyMappings = itemWarrantyMappingRepository.findByItemId(item.getId());
            if (!itemWarrantyMappings.isEmpty()) {
                itemWarrantyMappings.forEach(itemWarrantyMapping -> {
                    itemWarrantyMapping.setWarranty(warranty);
                    itemWarrantyMappingRepository.save(itemWarrantyMapping);
                });
            } else {
                ItemWarrantyMappingDto itemWarrantyMappingDto = new ItemWarrantyMappingDto();
                itemWarrantyMappingDto.setWarranty(warranty);
                itemWarrantyMappingDto.setItemId(item.getId());
                itemWarrantyMappingService.saveEntity(itemWarrantyMappingDto);
            }
            Item item1 = itemMapper.dtoToDomain(itemDto, new CycleAvoidingMappingContext());
            dataDTO.setData(itemRepository.save(item1));
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
//        System.out.println("Update Item Warranty Ended");
        return dataDTO;
    }

    /**
     * Change item warranty status.
     * @param inventoryMappingId the inventory mapping id
     * @param itemId the item id
     * @param billdate the billdate
     */
    public void changeItemWarrantyStatus(Long inventoryMappingId, Long itemId, LocalDateTime billdate) {
        try {
            Item item = itemRepository.findById(itemId).orElse(null);
            String expiryTimeUnit = productRepository.findExpiryTimeUnitByProductId(item.getProductId());
            Integer expiryTime = productRepository.findExpiryTimeByProductId(item.getProductId());
            if (inventoryMappingId != null) {
                if (item != null && inventoryMappingId != null && expiryTime != 0) {
                    item.setOwnershipType("Sold");
                    if (expiryTimeUnit != null && expiryTimeUnit.equalsIgnoreCase("Month")) {
                        LocalDateTime expDate = billdate.plusMonths(expiryTime);
                        item.setExpireDate(expDate);
                    }
                    if (expiryTimeUnit != null && expiryTimeUnit.equalsIgnoreCase("Day")) {
                        LocalDateTime expDate = billdate.plusDays(expiryTime);
                        item.setExpireDate(expDate);
                    }
                    itemRepository.save(item);
                    updateItemWarranty(item, "InWarranty");
                } else {
                    itemRepository.save(item);
                    updateItemWarranty(item, "NoWarranty");
                }
            } else {
                if (inventoryMappingId == null && expiryTime != 0) {
                    updateItemWarranty(item, "InWarranty");
                } else {
                    itemRepository.save(item);
                    updateItemWarranty(item, "NoWarranty");
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }


//    public GenericDataDTO updateItemStatusForCustomer(Long itemId, String itemStatus, LocalDateTime assignDate, Long customerId,String event) {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        try {
//            Item item = itemRepository.findById(itemId).get();
//            ItemStatusMapping itemStatusMapping = new ItemStatusMapping();
//            itemStatusMapping.setItemId(itemId);
//            itemStatusMapping.setCustomerId(customerId);
//            itemStatusMapping.setItemStatus(itemStatus);
//            itemStatusMapping.setEvent(event);
//
//            if (itemStatus.equalsIgnoreCase(CommonConstants.ALLOCATED)) {
//                List<ItemStatusMapping> itemStatusMappings = itemStatusMappingRepo.findByStatus(itemId);
//                if (itemStatusMappings.size() != 0) {
//                    ItemStatusMapping statusMapping = itemStatusMappings.get(itemStatusMappings.size() - 1);
//                    if (statusMapping != null && statusMapping.getItemStatus().equalsIgnoreCase(CommonConstants.ALLOCATED)) {
//                        if (statusMapping.getEndDate().isAfter(assignDate)) {
//                            throw new RuntimeException("Item was already allocated during this assigned date.");
//                        } else {
//                            itemStatusMapping.setStartDate(assignDate);
//                            List<ItemStatusMapping> itemStatusMappingList = itemStatusMappingRepo.findByItemStatus(itemId);
//                            if (itemStatusMappingList.size() != 0) {
//                                ItemStatusMapping statusMappings = itemStatusMappingList.get(itemStatusMappingList.size() - 1);
//                                statusMappings.setEndDate(assignDate);
//                                itemStatusMappingRepo.save(statusMappings);
//                            }
//                        }
//                    }
//
//                } else {
//                    itemStatusMapping.setStartDate(assignDate);
//                }
//            }
//            if (itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)) {
//                List<ItemStatusMapping> itemStatusMappings = itemStatusMappingRepo.findByStatus(itemId);
//                if (itemStatusMappings.size() != 0) {
//                    ItemStatusMapping statusMapping = itemStatusMappings.get(itemStatusMappings.size() - 1);
//                    if (statusMapping.getItemStatus().equalsIgnoreCase(CommonConstants.ALLOCATED)) {
//                        statusMapping.setEndDate(LocalDateTime.now());
//                        itemStatusMappingRepo.save(statusMapping);
//                        itemStatusMapping.setStartDate(LocalDateTime.now());
//                        //    itemStatusMapping.setEndDate(LocalDateTime.now());
//                        Long days = 0L;
//                        if (itemStatusMappings.size() != 0) {
//                            for (ItemStatusMapping mapping : itemStatusMappings) {
//                                if ((mapping.getStartDate() != null) && mapping.getEndDate() != null) {
//                                    Duration duration = Duration.between(mapping.getStartDate(), mapping.getEndDate());
//                                    days = days + duration.toDays();
//                                }
//                                if (mapping.getEndDate() == null) {
//                                    Duration duration = Duration.between(mapping.getStartDate(), LocalDateTime.now());
//                                    days = days + duration.toDays();
//                                }
//                            }
//                        }
//                        if (days >= 60) {

    /// /                            Item item = itemRepository.findById(itemId).get();
//                            item.setCondition(CommonConstants.REFURBISHED);
//                        }
//                    }
//                } else {
//
//                    itemStatusMapping.setStartDate(LocalDateTime.now());
//                    //  itemStatusMapping.setEndDate(LocalDateTime.now());
//                }
//            }
//
//            if (itemStatus.equalsIgnoreCase(CommonConstants.DEFECTIVE)) {
//                List<ItemStatusMapping> itemStatusMappings = itemStatusMappingRepo.findByStatus(itemId);
//                if (itemStatusMappings.size() != 0) {
//                    ItemStatusMapping statusMapping = itemStatusMappings.get(itemStatusMappings.size() - 1);
//                    if (statusMapping.getItemStatus().equalsIgnoreCase(CommonConstants.ALLOCATED)) {
//                        statusMapping.setEndDate(LocalDateTime.now());
//                        itemStatusMappingRepo.save(statusMapping);
//                        itemStatusMapping.setStartDate(LocalDateTime.now());
//                        //    itemStatusMapping.setEndDate(LocalDateTime.now());
//                        Long days = 0L;
//                        if (itemStatusMappings.size() != 0) {
//                            for (ItemStatusMapping mapping : itemStatusMappings) {
//                                if ((mapping.getStartDate() != null) && mapping.getEndDate() != null) {
//                                    Duration duration = Duration.between(mapping.getStartDate(), mapping.getEndDate());
//                                    days = days + duration.toDays();
//                                }
//                                if (mapping.getEndDate() == null) {
//                                    Duration duration = Duration.between(mapping.getStartDate(), LocalDateTime.now());
//                                    days = days + duration.toDays();
//                                }
//                            }
//                        }
//                        if (days >= 60) {

    //                            item.setCondition(CommonConstants.REFURBISHED);
//                        }
//                    }
//                } else {
//
//                    itemStatusMapping.setStartDate(LocalDateTime.now());
//                    //  itemStatusMapping.setEndDate(LocalDateTime.now());
//                }
//            }
//

    //            item.setItemStatus(itemStatus);
//            itemRepository.save(item);
//            itemStatusMappingRepo.save(itemStatusMapping);
//
//        } catch (Exception e) {
//            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
//        }
//        return dataDTO;
//    }
    @Transactional
    public GenericDataDTO updateItemStatusForCustomer(Item item, String itemStatus, LocalDateTime assignDate, Long customerId, String event) {
//        System.out.println("Update Item Status For Customer Started");
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            ItemStatusMapping itemStatusMapping = new ItemStatusMapping(item.getId(), customerId, itemStatus, event);
            List<ItemStatusMapping> itemStatusMappings = itemStatusMappingRepo.findByStatus(item.getId());
            ItemStatusMapping lastStatusMapping = itemStatusMappings.isEmpty() ? null : itemStatusMappings.get(itemStatusMappings.size() - 1);
            if (CommonConstants.ALLOCATED.equalsIgnoreCase(itemStatus)) {
                handleAllocation(itemStatusMapping, lastStatusMapping, assignDate);
            } else if (CommonConstants.UNALLOCATED.equalsIgnoreCase(itemStatus) || CommonConstants.DEFECTIVE.equalsIgnoreCase(itemStatus)) {
                handleUnallocationOrDefective(itemStatusMapping, lastStatusMapping);
                if (isUsageMoreThan60Days(itemStatusMappings)) {
                    item.setCondition(CommonConstants.REFURBISHED);
                }
            }
            item.setItemStatus(itemStatus);
            itemRepository.save(item);
            itemStatusMappingRepo.save(itemStatusMapping);
        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
//        System.out.println("Update Item Status For Customer Ended");
        return dataDTO;
    }

    @Transactional
    public void handleAllocation(ItemStatusMapping itemStatusMapping, ItemStatusMapping lastStatusMapping, LocalDateTime assignDate) {
//        System.out.println("Handle Allocation Started");
        try {
            if (lastStatusMapping != null && CommonConstants.ALLOCATED.equalsIgnoreCase(lastStatusMapping.getItemStatus())) {
                if (lastStatusMapping.getEndDate() != null && lastStatusMapping.getEndDate().isAfter(assignDate)) {
                    throw new RuntimeException("Item was already allocated during this assigned date.");
                } else {
                    itemStatusMapping.setStartDate(assignDate);
                    lastStatusMapping.setEndDate(assignDate);
                    itemStatusMappingRepo.save(lastStatusMapping);
                }
            } else {
                itemStatusMapping.setStartDate(assignDate);
            }
//        System.out.println("Handle Allocation Ended");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void handleUnallocationOrDefective(ItemStatusMapping itemStatusMapping, ItemStatusMapping lastStatusMapping) {
//        System.out.println("Handle UnAllocation Or Defective Started");
        if (lastStatusMapping != null && CommonConstants.ALLOCATED.equalsIgnoreCase(lastStatusMapping.getItemStatus())) {
            lastStatusMapping.setEndDate(LocalDateTime.now());
            itemStatusMappingRepo.save(lastStatusMapping);
        }
        itemStatusMapping.setStartDate(LocalDateTime.now());
//        System.out.println("Handle UnAllocation Or Defective Ended");
    }

    public boolean isUsageMoreThan60Days(List<ItemStatusMapping> itemStatusMappings) {
        long totalDays = itemStatusMappings.stream()
                .filter(mapping -> mapping.getStartDate() != null)
                .mapToLong(mapping -> {
                    LocalDateTime endDate = (mapping.getEndDate() != null) ? mapping.getEndDate() : LocalDateTime.now();
                    return Duration.between(mapping.getStartDate(), endDate).toDays();
                })
                .sum();
        return totalDays >= 60;
    }


    public GenericDataDTO updateItemStatusForServiceAreaAndPop(Long itemId, String itemStatus, Long bulkConsumptionID, Long serviceAreaID, Long popId, String event) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            ItemStatusMapping itemStatusMapping = new ItemStatusMapping();
            itemStatusMapping.setItemId(itemId);
            itemStatusMapping.setEvent(event);
            if (bulkConsumptionID != null) {
                itemStatusMapping.setBulkConsumptionId(bulkConsumptionID);
            }
            if (serviceAreaID != null) {
                itemStatusMapping.setServiceAreaId(serviceAreaID);
            }
            if (popId != null) {
                itemStatusMapping.setPopId(popId);
            }
            itemStatusMapping.setItemStatus(itemStatus);
            Item item = itemRepository.findById(itemId).get();
            Inward inward = inwardRepository.findById(item.getCurrentInwardId()).get();
            if (itemStatus.equalsIgnoreCase(CommonConstants.ALLOCATED)) {
                List<ItemStatusMapping> itemStatusMappings = itemStatusMappingRepo.findByStatus(itemId);
                if (itemStatusMappings.size() != 0) {
                    ItemStatusMapping statusMapping = itemStatusMappings.get(itemStatusMappings.size() - 1);
                    itemStatusMapping.setStartDate(statusMapping.getEndDate());
                } else {
                    itemStatusMapping.setStartDate(inward.getInwardDateTime());
                }
            }
            Long allocatedDays = 0L;
            if (itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)) {
                List<ItemStatusMapping> statusMapping = itemStatusMappingRepo.findByItemId(itemId);
                if (statusMapping.size() != 0) {
                    ItemStatusMapping mapping = statusMapping.get(statusMapping.size() - 1);
                    mapping.setEndDate(LocalDateTime.now());
                    itemStatusMappingRepo.save(mapping);

                    itemStatusMapping.setStartDate(LocalDateTime.now());
                    itemStatusMapping.setEndDate(LocalDateTime.now());

                    List<ItemStatusMapping> itemStatusMappingList = itemStatusMappingRepo.findByStatus(itemId);
                    if (itemStatusMappingList.size() != 0) {
                        for (ItemStatusMapping statusMapping1 : itemStatusMappingList) {
                            if (statusMapping1.getStartDate() != null && statusMapping1.getEndDate() != null) {
                                Duration duration = Duration.between(statusMapping1.getStartDate(), statusMapping1.getEndDate());
                                allocatedDays = allocatedDays + duration.toDays();
                            }
                            if (statusMapping1.getEndDate() == null) {
                                Duration duration = Duration.between(statusMapping1.getStartDate(), LocalDateTime.now());
                                allocatedDays = allocatedDays + duration.toDays();
                            }
                        }
                    }
                }
            }
            if (bulkConsumptionID != null && itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)) {
                itemStatusMapping.setStartDate(LocalDateTime.now());
                itemStatusMapping.setEndDate(LocalDateTime.now());
            }

            if (allocatedDays >= 60) {
                item.setCondition(CommonConstants.REFURBISHED);
            }
            item.setItemStatus(itemStatus);
            itemRepository.save(item);
            itemStatusMappingRepo.save(itemStatusMapping);

        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }


    public GenericDataDTO updateItemStatusByList(List<ItemStatusDTO> itemsStatusDtoList) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        QItem qItem = QItem.item;
        try {
            List<Long> itemIds = new ArrayList<>();
            for (ItemStatusDTO itemStatusDTO : itemsStatusDtoList) {
                itemIds.add(itemStatusDTO.getId());
            }
//            List<String> statuses = new ArrayList<>(itemsList.values());
            List<Item> itemList = itemRepository.findAllById(itemIds);
            ItemDto itemDto = new ItemDto();
            List<ItemDto> lst = new ArrayList<>();
            if (itemList != null) {
                if (itemList.size() > 0)
                    for (int i = 0; i <= itemList.size() - 1; i++) {
                        itemDto = getEntityForUpdateAndDelete(itemIds.get(i));
                        itemDto.setItemStatus(itemsStatusDtoList.get(i).getItemStatus());

                        lst.add(super.updateEntity(itemDto));
                    }
            }
            dataDTO.setDataList(lst);

        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    public GenericDataDTO updateItemOwnerShipStatusByList(List<ItemOwnerShipDTO> itemOwnerShipDTOList) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        QItem qItem = QItem.item;
        try {
            List<Long> itemIds = new ArrayList<>();
            for (ItemOwnerShipDTO itemOwnerShipDTO : itemOwnerShipDTOList) {
                itemIds.add(itemOwnerShipDTO.getId());
            }
            //List<String> ownerships = new ArrayList<>(itemsList.values());
            List<Item> itemList = itemRepository.findAllById(itemIds);

            List<ItemDto> lst = new ArrayList<>();
            if (itemList != null) {
                if (itemList.size() > 0)
                    for (int i = 0; i <= itemList.size() - 1; i++) {
                        ItemDto itemDto = getEntityForUpdateAndDelete(itemIds.get(i));
                        itemDto.setOwnershipType(itemOwnerShipDTOList.get(i).getOwnershipType());
                        if (itemOwnerShipDTOList.get(i).getRemarks() != null) {
                            itemDto.setRemarks(itemOwnerShipDTOList.get(i).getRemarks());
                        }
                        lst.add(super.updateEntity(itemDto));
                    }
            }
            dataDTO.setDataList(lst);

        } catch (Exception e) {
            e.printStackTrace();
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    public void uploadDocument(Long id, MultipartFile file, ItemConditionsMappingDto itemConditionsMapping) throws Exception {
        String SUBMODULE = "item" + " [uploadDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.ITEM_COMPLAIN).get(0).getValue();
        try {
            Item item = itemRepository.getOne(id);
            String subFolderName = item.getName().trim() + "/";
            String path = PATH + subFolderName;
            ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + path);
            if (null != file.getOriginalFilename()) {
//                System.out.println(file.getSize());
                MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
                if (null != file1) {
                    itemConditionsMapping.setUniquename(fileUtility.saveFileToServer(file1, path));
                    itemConditionsMapping.setFilename(file.getOriginalFilename());
                }
            } else {
                if (null != file) {
                    if (null != file.getOriginalFilename()
                            && null != file.getOriginalFilename()
                            && !file.getOriginalFilename().equalsIgnoreCase(file.getOriginalFilename())) {
                        fileUtility.removeFileAtServer(itemConditionsMapping.getUniquename(), path);
                    }
                    MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
                    if (null != file1)
                        itemConditionsMapping.setUniquename(fileUtility.saveFileToServer(file1, path));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public GenericDataDTO searchItems(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchItemsPojo searchItemsPojo) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (searchItemsPojo != null) {
                PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
                List<ServiceAreaDTO> serviceAreaDTOS = serviceAreaService.getAllServiceAreaByStaffId();
                List<Integer> serviceAreaIds = serviceAreaDTOS.stream()
                        .map(ServiceAreaDTO::getId)
                        .map(Long::intValue)
                        .collect(Collectors.toList());
                if (!serviceAreaIds.isEmpty()) {
                    List<List<Item>> items = new ArrayList<>();
                    if (shouldPerformFullSearch(searchItemsPojo)) {
                        List<Long> staffIds = findStaffIds(staffUserServiceAreaMappingRepository, serviceAreaIds);
                        List<Long> warehouseIds = findWarehouseIds(wareHouseManagmentServiceAreamappingRepo, serviceAreaIds);
                        List<Long> partnerIds = findPartnerIds(partnerServiceAreaMappingRepo, serviceAreaIds);
                        List<Long> serviceAreaIdsLong = serviceAreaIds.stream().map(Integer::longValue).collect(Collectors.toList());
                        List<Long> customerIds = findCustomerIds(customersRepository, serviceAreaIdsLong);
                        List<Long> ownerIds = new ArrayList<>();
                        ownerIds.addAll(staffIds);
                        ownerIds.addAll(warehouseIds);
                        ownerIds.addAll(partnerIds);
                        ownerIds.addAll(serviceAreaIdsLong);
                        ownerIds.addAll(customerIds);
                        items = findItemByOwnerIds(ownerIds);
                    } else {
                        List<Item> foundItems = findItemBySearch(searchItemsPojo);
                        if (!foundItems.isEmpty()) {
                            items.add(foundItems);
                        }
                    }
                    List<Item> itemsFlat = items.stream().flatMap(List::stream).collect(Collectors.toList());
                    List<Long> itemIds = itemsFlat.stream().map(Item::getId).collect(Collectors.toList());
                    Page<Item> paginationList = itemRepository.findAllByIdIn(itemIds, pageRequest);
                    List<ItemDto> itemDtoList = paginationList.get().map(item -> itemMapper.domainToDTO(item, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                    List<ItemDto> finalItemDTOList = processItemDTOList(itemDtoList);

                    genericDataDTO.setDataList(finalItemDTOList);
                    genericDataDTO.setTotalRecords(paginationList.getTotalElements());
                    genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
                    genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
                    genericDataDTO.setTotalPages(paginationList.getTotalPages());
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    public List<Long> findStaffIds(StaffUserServiceAreaMappingRepository repository, List<Integer> serviceAreaIds) {
        if (!serviceAreaIds.isEmpty()) {
            return repository.findAllByServiceIdIn(serviceAreaIds).stream()
                    .map(StaffUserServiceAreaMapping::getStaffId)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Long> findWarehouseIds(WareHouseManagmentServiceAreamappingRepo repository, List<Integer> serviceAreaIds) {
        if (!serviceAreaIds.isEmpty()) {
            return repository.findAllByServiceIdIn(serviceAreaIds).stream()
                    .map(WareHouseServiceAreaMapping::getWarehouseId)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Long> findPartnerIds(PartnerServiceAreaMappingRepo repository, List<Integer> serviceAreaIds) {
        if (!serviceAreaIds.isEmpty()) {
            return repository.findAllByServiceIdIn(serviceAreaIds).stream()
                    .map(PartnerServiceAreaMapping::getPartnerId)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Long> findCustomerIds(CustomersRepository repository, List<Long> serviceAreaIds) {
        if (!serviceAreaIds.isEmpty()) {
            return repository.findAllByIsDeletedIsFalseAndStatusAndServiceareaIdIn(CommonConstants.ACTIVE_STATUS, serviceAreaIds).stream()
                    .map(Customers::getId)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean shouldPerformFullSearch(SearchItemsPojo searchItemsPojo) {
        return Optional.ofNullable(searchItemsPojo)
                .map(s -> s.getOwnerType() == "" &&
                        s.getOwnerId() == null &&
                        s.getProductId() == null &&
                        s.getInwardId() == null &&
                        s.getItemStatus() == "" &&
                        s.getItemType() == "" &&
                        s.getWarrantyStatus() == "" &&
                        s.getOwnership() == "" &&
                        s.getSerialNumber() == "" &&
                        s.getMacAddress() == null)
                .orElse(false);
    }

    public List<List<Item>> findItemByOwnerIds(List<Long> ownerIds) {
        List<List<Item>> finalItemList = new ArrayList<>();
        if (!ownerIds.isEmpty()) {
            List<Item> itemsByStaffIds = itemRepository.findAllByOwnerIdInAndIsDeletedIsFalse(ownerIds);
            finalItemList.add(itemsByStaffIds);
        }
        return finalItemList;
    }

    public List<Item> findItemBySearch(SearchItemsPojo search) {
        try {
            QItem qItem = QItem.item;
            BooleanExpression booleanExpression = qItem.isDeleted.eq(false);

            if (getMvnoIdFromCurrentStaff() != 1) {
                booleanExpression = booleanExpression.and(qItem.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            }
            if (search.getOwnerType() != null && !search.getOwnerType().isEmpty()) {
                booleanExpression = booleanExpression.and(qItem.ownerType.startsWithIgnoreCase(search.getOwnerType()));
            }
            if (search.getOwnerId() != null) {
                booleanExpression = booleanExpression.and(qItem.ownerId.eq(search.getOwnerId().longValue()));
            }
            if (search.getProductId() != null) {
                booleanExpression = booleanExpression.and(qItem.productId.eq(search.getProductId().longValue()));
            }
            if (search.getInwardId() != null) {
                booleanExpression = booleanExpression.and(qItem.currentInwardId.eq(search.getInwardId().longValue()));
            }
            if (search.getItemType() != null && !search.getItemType().isEmpty()) {
                booleanExpression = booleanExpression.and(qItem.condition.startsWithIgnoreCase(search.getItemType()));
            }
            if (search.getItemStatus() != null && !search.getItemStatus().isEmpty()) {
                booleanExpression = booleanExpression.and(qItem.itemStatus.startsWithIgnoreCase(search.getItemStatus()));
            }
            if (search.getOwnership() != null && !search.getOwnership().isEmpty()) {
                booleanExpression = booleanExpression.and(qItem.ownershipType.startsWithIgnoreCase(search.getOwnership()));
            }
            if (search.getWarrantyStatus() != null && !search.getWarrantyStatus().isEmpty()) {
                booleanExpression = booleanExpression.and(qItem.warranty.startsWithIgnoreCase(search.getWarrantyStatus()));
            }
            if (search.getSerialNumber() != null && !search.getSerialNumber().isEmpty()) {
                booleanExpression = booleanExpression.and(qItem.serialNumber.containsIgnoreCase(search.getSerialNumber()));
            }
            if (search.getMacAddress() != null && !search.getMacAddress().isEmpty()) {
                booleanExpression = booleanExpression.and(qItem.macAddress.startsWithIgnoreCase(search.getMacAddress()));
            }
            return IterableUtils.toList(itemRepository.findAll(booleanExpression));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<ItemDto> processItemDTOList(List<ItemDto> dto) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        try {
            Map<String, Function<Long, String>> ownerTypeMap = new HashMap<>();
            ownerTypeMap.put(CommonConstants.SERIALISED_ITEM_OWNERTYPE.STAFF, ownerId -> staffUserRepository.findById(Math.toIntExact(ownerId)).map(StaffUser::getUsername).orElse(null));
            ownerTypeMap.put(CommonConstants.SERIALISED_ITEM_OWNERTYPE.WAREHOUSE, ownerId -> warehouseManagementRepository.findById(ownerId).map(WareHouse::getName).orElse(null));
            ownerTypeMap.put(CommonConstants.SERIALISED_ITEM_OWNERTYPE.POP, ownerId -> popManagementRepository.findById(ownerId).map(PopManagement::getName).orElse(null));
            ownerTypeMap.put(CommonConstants.SERIALISED_ITEM_OWNERTYPE.SERVICE_AREA, ownerId -> serviceAreaRepository.findById(ownerId).map(ServiceArea::getName).orElse(null));
            ownerTypeMap.put(CommonConstants.SERIALISED_ITEM_OWNERTYPE.EXTERNALITEM_SERVICEAREA, ownerId -> serviceAreaRepository.findById(ownerId).map(ServiceArea::getName).orElse(null));
            ownerTypeMap.put(CommonConstants.SERIALISED_ITEM_OWNERTYPE.PARTNER, ownerId -> partnerRepository.findById(Math.toIntExact(ownerId)).map(Partner::getName).orElse(null));
            ownerTypeMap.put(CommonConstants.SERIALISED_ITEM_OWNERTYPE.CUSTOMER, ownerId -> customersRepository.findById(Math.toIntExact(ownerId)).map(Customers::getUsername).orElse(null));
            for (ItemDto itemDto : dto) {
                if (itemDto.getCurrentInwardId() != null) {
                    itemDto.setCurrentInwardNumber(inwardRepository.findById(itemDto.getCurrentInwardId()).map(Inward::getInwardNumber).orElse(null));
                }
                if (itemDto.getProductId() != null) {
                    itemDto.setProductName(productService.getEntityById(itemDto.getProductId().longValue()).getName());
                }

                List<ItemConditionsMapping> itemConditions = itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId());
                if (!itemConditions.isEmpty()) {
                    ItemConditionsMapping itemCondition = itemConditions.get(0);
                    itemDto.setFilename(itemCondition.getFilename());
                    itemDto.setItemConditionId(itemCondition.getId());
                }

                Item item = itemRepository.getOne(itemDto.getId());
                if (item.getRemarks() != null) {
                    itemDto.setRemarks(item.getRemarks());
                }

                String ownerType = itemDto.getOwnerType();
                Long ownerId = itemDto.getOwnerId();
                String ownerName = ownerTypeMap.getOrDefault(ownerType, id -> null).apply(ownerId);
                itemDto.setOwnerName(ownerName);

                itemDtoList.add(itemDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return itemDtoList;
    }

    public GenericDataDTO searchItembasedOnProductAndCustomer(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
        try {
            PageRequest pageRequest1 = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim() != null) {
                        return getProductNameAndCustomerName(searchModel.getFilterValue(), pageRequest1, searchModel.getFilterColumn());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }


    public GenericDataDTO getProductNameAndCustomerName(String s1, PageRequest pageRequest, String s2) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (s2.equalsIgnoreCase("Product")) {
                QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(getLoggedInUserId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.product.name.likeIgnoreCase(("%" + s1 + "%"))));
                Page<CustomerInventoryMapping> page = customerInventoryMappingRepo.findAll(booleanExpression, pageRequest);
                genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(page.getContent(), new CycleAvoidingMappingContext()));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            }
            if (s2.equalsIgnoreCase("Customer")) {

                QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(getLoggedInUserId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.product.name.likeIgnoreCase(("%" + s1 + "%"))));
                Page<CustomerInventoryMapping> page = customerInventoryMappingRepo.findAll(booleanExpression, pageRequest);
                genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(page.getContent(), new CycleAvoidingMappingContext()));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public List<ItemDto> findItemsSuibiseOwned(Long currentInwardId) {
        List<ItemDto> itemDtoList = null;
        try {
            QItem qItem = QItem.item;
            BooleanExpression booleanExpression = qItem.isNotNull()
                    .and(qItem.currentInwardId.eq(currentInwardId))
                    .and(qItem.isDeleted.eq(false))
                    .and(qItem.ownershipType.eq(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED)
                            .or(qItem.ownershipType.eq(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED)))
                    .and(qItem.itemStatus.eq("Unallocated"));
            List<Item> itemList = (List<Item>) itemRepository.findAll(booleanExpression);
            itemDtoList = itemList.stream().map(item -> itemMapper.domainToDTO(item, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            return itemDtoList;
        } catch (Exception exception) {
            exception.printStackTrace();
            exception.getMessage();
        }
        return itemDtoList;
    }

    public List<ItemStatusMapping> getAllCustomerInvetoryHistory(Long custId) {
        List<ItemStatusMapping> itemList = new ArrayList<>();
        try {
            itemList = itemStatusMappingRepo.findByCustomerId(custId);

            QCustomers qCustomers = QCustomers.customers;
            BooleanExpression getChildExpression = qCustomers.isNotNull().and(qCustomers.parentCustId.eq(custId.intValue())).and(qCustomers.parentExperience.equalsIgnoreCase(CommonConstants.PARENT_EXPERIENCE_SINGLE).and(qCustomers.isDeleted.eq(false).and(qCustomers.status.eq(CommonConstants.CUSTOMER_STATUS_ACTIVE))));
            List<Long> childCustIds = ((List<Customers>) customersRepository.findAll(getChildExpression)).stream().map(customers -> Long.valueOf(customers.getId())).collect(Collectors.toList());
            if (childCustIds != null && childCustIds.size() > 0) {
                itemList.addAll(itemStatusMappingRepo.findByCustomerIdIn(childCustIds));
            }
            if (itemList.size() != 0) {
                itemList.stream().forEach(itemStatusMapping -> {
                    Item item = itemRepository.findById(itemStatusMapping.getItemId()).get();
                    if (item != null) {
                        List<CustomerInventoryMapping> customerInventoryMappingList = customerInventoryMappingRepo.findByItemId(item.getId());
                        if (customerInventoryMappingList.size() > 0 || customerInventoryMappingList != null) {
                            customerInventoryMappingList.stream().forEach(customerInventoryMapping -> {
                                itemStatusMapping.setCondition(item.getCondition());
                                itemStatusMapping.setMacAddress(item.getMacAddress());
                                itemStatusMapping.setSerialNumber(item.getSerialNumber());
                                if (customerInventoryMapping.getExternalItemId() != null) {
                                    ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(customerInventoryMapping.getExternalItemId()).get();
                                    itemStatusMapping.setExternalItemGroupNumber(externalItemManagement.getExternalItemGroupNumber());
                                }
                                if (itemStatusMapping.getEvent().equalsIgnoreCase("assign_inventory")) {
                                    itemStatusMapping.setApprovalRemark(null);
                                } else {
                                    itemStatusMapping.setApprovalRemark(customerInventoryMapping.getApprovalRemark());
                                }
                                if (customerInventoryMapping.getPlanId() != null) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(customerInventoryMapping.getPlanId())).get();
                                    itemStatusMapping.setPostPaidPlanName(postpaidPlan.getName());
                                }
                                if (customerInventoryMapping.getServiceId() != null) {
                                    PlanService planService = planServiceRepository.findById(Math.toIntExact(customerInventoryMapping.getServiceId())).get();
                                    itemStatusMapping.setServiceName(planService.getName());
                                }
                                if (customerInventoryMapping.getBillTo() != null) {
                                    itemStatusMapping.setBillTo(customerInventoryMapping.getBillTo());
                                }
                                if (customerInventoryMapping.getIsInvoiceToOrg() != null) {
                                    itemStatusMapping.setIsInvoiceToOrg(customerInventoryMapping.getIsInvoiceToOrg());
                                }
                                if (customerInventoryMapping.getIsRequiredApproval() != null) {
                                    itemStatusMapping.setIsRequiredApproval(customerInventoryMapping.getIsRequiredApproval());
                                }
                                itemStatusMapping.setConnectionNo(customerInventoryMapping.getConnectionNo());
                            });
                        }
                    }
                });
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            exception.getMessage();
        }
        return itemList;
    }


    public List<InOutWardMACMapingDTO> getInOutMacMappingForSerializedItem(Long productId, Long ownerId, String ownerType,
                                                                           boolean hasMac, boolean hasSerial, String productName,
                                                                           boolean bySearch, String filterValue, String filterColumn) {
        try {
            List<String> statuses = Arrays.asList(CommonConstants.UNALLOCATED, CommonConstants.DEFECTIVE, CommonConstants.STAFF_ALLOCATED);
            Map<Long, Item> itemMap;
            if (bySearch) {
                itemMap = getItemMapBySearch(productId, ownerId, ownerType, statuses, filterColumn, filterValue);
            } else {
                itemMap = itemRepository.findItemIdsByFilters(productId, ownerId, ownerType, statuses).stream()
                        .collect(Collectors.toMap(Item::getId, item -> item));
            }
            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = new ArrayList<>();
            if (!itemMap.isEmpty() || itemMap!= null) {
                List<Long> itemIds = new ArrayList<>(itemMap.keySet());
                List<Long> outwardIds = outwardRepository.findOutwardIds(CommonConstants.ACTIVE_STATUS, productId, CommonConstants.APPROVE);
                List<InOutWardMACMapping> inOutWardMACMappingList = inOutWardMacRepo
                        .findAllByItemIdInAndOutwardIdInAndCustInventoryMappingIdIsNullAndIsForwarded(itemIds, outwardIds, 0);
                inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                if (!inOutWardMACMapingDTOS.isEmpty()) {
                    inOutWardMACMapingDTOS.forEach(r -> {
                        Item item = itemMap.get(r.getItemId());
                        if (item != null && productId != null) {
                            r.setProductId(productId);
                            r.setProductName(productName);
                            r.setHasMac(hasMac);
                            r.setHasSerial(hasSerial);
                            r.setCondition(item.getCondition());
                            r.setOwnerShip(item.getOwnershipType());
                        }
                    });
                }
            }
            return inOutWardMACMapingDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<InOutWardMACMapingDTO> getInOutMacMappingForSerializedCBNB(Long productId, Long ownerId, String ownerType, boolean bySearch, String filterValue, String filterColumn, String productName, boolean hasMac, boolean hasSerial) {
        try {
            List<String> statuses = Arrays.asList(CommonConstants.UNALLOCATED, CommonConstants.DEFECTIVE, CommonConstants.STAFF_ALLOCATED);
            Map<Long, Item> itemMap;
            if (bySearch) {
                itemMap = getItemMapBySearch(productId, ownerId, ownerType, statuses, filterColumn, filterValue);
            } else {
                itemMap = itemRepository.findItemIdsByFilters(productId, ownerId, ownerType, statuses).stream()
                        .collect(Collectors.toMap(Item::getId, item -> item));
            }
            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = new ArrayList<>();
            if (!itemMap.isEmpty() || itemMap!= null) {
                List<Long> itemIds = new ArrayList<>(itemMap.keySet());
                List<Long> outwardIds = outwardRepository.findOutwardIds(CommonConstants.ACTIVE_STATUS, productId, CommonConstants.APPROVE);
                List<InOutWardMACMapping> inOutWardMACMappingList = inOutWardMacRepo
                        .findAllByItemIdInAndOutwardIdInAndCustInventoryMappingIdIsNullAndIsForwarded(itemIds, outwardIds, 0);
                inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                if (!inOutWardMACMapingDTOS.isEmpty()) {
                    inOutWardMACMapingDTOS.forEach(r -> {
                        Item item = itemMap.get(r.getItemId());
                        if (item != null && productId != null) {
                            r.setProductId(productId);
                            r.setProductName(productName);
                            r.setHasMac(hasMac);
                            r.setHasSerial(hasSerial);
                            r.setCondition(item.getCondition());
                            r.setOwnerShip(item.getOwnershipType());
                        }
                    });
                }
            }
            return inOutWardMACMapingDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<InOutWardMACMapingDTO> getInOutMacMappingForPopAndSA(Long productId, Long ownerId, String ownerType,
                                                                     boolean bySearch, String filterValue,
                                                                     String filterColumn, String productName,
                                                                     boolean hasMac, boolean hasSerial) {
        try {
            List<String> statuses = Arrays.asList(CommonConstants.UNALLOCATED, CommonConstants.DEFECTIVE, CommonConstants.STAFF_ALLOCATED);
            Map<Long, Item> itemMap;
            if (bySearch) {
                itemMap = getItemMapBySearch(productId, ownerId, ownerType, statuses, filterColumn, filterValue);
            } else {
                itemMap = itemRepository.findItemIdsByFilters(productId, ownerId, ownerType, statuses).stream()
                        .collect(Collectors.toMap(Item::getId, item -> item));
            }
            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = new ArrayList<>();
            if (!itemMap.isEmpty() || itemMap!= null) {
                List<Long> itemIds = new ArrayList<>(itemMap.keySet());
                List<Long> outwardIds = outwardRepository.findOutwardIds(CommonConstants.ACTIVE_STATUS, productId, CommonConstants.APPROVE);
                List<InOutWardMACMapping> inOutWardMACMappingList = inOutWardMacRepo.findAllByItemIdInAndOutwardIdInAndInventoryMappingIdIsNull(itemIds, outwardIds).stream()
                        .filter(inOutWardMACMapping -> inOutWardMACMapping.getIsForwarded().equals(0))
                        .collect(Collectors.toList());
                inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                if (!inOutWardMACMapingDTOS.isEmpty()) {
                    inOutWardMACMapingDTOS.forEach(r -> {
                        Item item = itemMap.get(r.getItemId());
                        if (item != null && productId != null) {
                            r.setProductId(productId);
                            r.setProductName(productName);
                            r.setCondition(item.getCondition());
                        }
                    });
                }
            }
            return inOutWardMACMapingDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<InOutWardMACMapingDTO> getInOutMacMappingForSerializedItemBasedOnItemCondtion(Long productId, Long olditemId, Long ownerId, String ownerShipType, String replacementReason) {
        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = null;
        String productName = productRepository.findProductNameByProductId(productId);
        Long pcId = productRepository.findProductCategoryIdByProductId(productId);
        String pcCatType = productCategoryRepository.findTypeById(pcId);
        boolean hasMac = productCategoryRepository.findHasMacById(pcId);
        boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
        boolean isTrackable = productCategoryRepository.findHasTrackableById(pcId);
        try {
            Item olditem = itemRepository.findItemSummariesById(olditemId);
            if (olditem != null) {
                if ((olditem.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED)
                        || olditem.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED))
                        && olditem.getCondition().equalsIgnoreCase("New")) {
                    List<Long> itemIds = itemRepository.findMatchingItemsForOrganization(productId, CommonConstants.UNALLOCATED,
                            CommonConstants.STAFF_ALLOCATED, CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED,
                            CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED,
                            ownerId, ownerShipType, CommonConstants.DEFECTIVE, Arrays.asList("New", "Refurbished"));
                    List<Long> outwardIds = outwardRepository.findOutwardIds(CommonConstants.ACTIVE_STATUS, productId, CommonConstants.APPROVE);
                    List<InOutWardMACMapping> inOutWardMACMappingList = inOutWardMacRepo.findMappingsNative(itemIds, outwardIds);
                    inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                    inOutWardMACMapingDTOS.stream().forEach(r -> {
                        r.setProductId(productId);
                        r.setProductName(productName);
                        r.setHasMac(hasMac);
                        r.setHasSerial(hasSerial);
                        r.setCondition(itemRepository.findItemConditionByItemId(r.getItemId()));
                    });
                }
                if (olditem.getOwnershipType().equalsIgnoreCase("Sold") && olditem.getCondition().equalsIgnoreCase("New")) {
                    List<Long> itemIds = itemRepository.findMatchingItemsForNew(productId, CommonConstants.UNALLOCATED,
                            CommonConstants.STAFF_ALLOCATED, CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED,
                            CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED,
                            ownerId, ownerShipType, CommonConstants.DEFECTIVE, Arrays.asList("New"));
                    List<Long> outwardIds = outwardRepository.findOutwardIds(CommonConstants.ACTIVE_STATUS, productId, CommonConstants.APPROVE);
                    List<InOutWardMACMapping> inOutWardMACMappingList = inOutWardMacRepo.findMappingsNative(itemIds, outwardIds);
                    inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                    inOutWardMACMapingDTOS.stream().forEach(r -> {
                        r.setProductId(productId);
                        r.setProductName(productName);
                        r.setHasMac(hasMac);
                        r.setHasSerial(hasSerial);
                        r.setCondition(itemRepository.findItemConditionByItemId(r.getItemId()));
                    });
                }
                if (olditem.getCondition().equalsIgnoreCase("Refurbished") || replacementReason.equalsIgnoreCase("Temporary Replacement")) {
                    List<Long> itemIds = itemRepository.findMatchingItemsForRefurbished(productId, CommonConstants.UNALLOCATED,
                            CommonConstants.STAFF_ALLOCATED, CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED,
                            CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED,
                            ownerId, ownerShipType, CommonConstants.DEFECTIVE, Arrays.asList("Refurbished"));
                    List<Long> outwardIds = outwardRepository.findOutwardIds(CommonConstants.ACTIVE_STATUS, productId, CommonConstants.APPROVE);
                    List<InOutWardMACMapping> inOutWardMACMappingList = inOutWardMacRepo.findMappingsNative(itemIds, outwardIds);
                    inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                    inOutWardMACMapingDTOS.stream().forEach(r -> {
                        r.setProductId(productId);
                        r.setProductName(productName);
                        r.setHasMac(hasMac);
                        r.setHasSerial(hasSerial);
                        r.setCondition(itemRepository.findItemConditionByItemId(r.getItemId()));
                    });
                }

            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return inOutWardMACMapingDTOS;
    }

    public List<InOutWardMACMapingDTO> getInOutMacMappingBasedOnProductType(Long productId, Long ownerId, String ownerType, Long planId, Long planGroupId, Long productCategoryId) {
        List<InOutWardMACMapingDTO> listList = new ArrayList<>();
        if (getLoggedInUser().getPartnerId() != 1) {
            ownerId = Long.valueOf(getLoggedInUser().getPartnerId());
            ownerType = CommonConstants.PARTNER;
        }
        Long finalOwnerId = ownerId;
        String finalOwnerType = ownerType;
        try {
            List<Productplanmapping> productplanmappingList = null;
            List<ProductPlanGroupMapping> productPlanGroupMappingList = null;
            if (planGroupId != null) {
                QProductPlanGroupMapping qProductPlanGroupMapping = QProductPlanGroupMapping.productPlanGroupMapping;
                BooleanExpression booleanExpression = qProductPlanGroupMapping.planId.eq(planId).and(qProductPlanGroupMapping.planGroupId.eq(planGroupId));
                if (productCategoryId != null) {
                    booleanExpression = booleanExpression.and(qProductPlanGroupMapping.productCategoryId.eq(productCategoryId));
                }
                productPlanGroupMappingList = IterableUtils.toList(productPlanGroupMappingRepository.findAll(booleanExpression));
            } else {
                QProductplanmapping qProductplanmapping = QProductplanmapping.productplanmapping;
                BooleanExpression booleanExpression = qProductplanmapping.planId.eq(planId).and(qProductplanmapping.productId.eq(productId));
                if (productCategoryId != null) {
                    booleanExpression = booleanExpression.and(qProductplanmapping.productCategoryId.eq(productCategoryId));
                }
                productplanmappingList = IterableUtils.toList(productPlanMappingRepository.findAll(booleanExpression));
            }
            if (productplanmappingList != null && productplanmappingList.size() != 0) {
                productplanmappingList.stream().forEach(t -> {
                    if (t.getProduct_type().equalsIgnoreCase("New")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false)
                                .and(qItem.productId.eq(productId))
                                .and(qItem.ownerId.eq(finalOwnerId))
                                .and(qItem.ownerType.equalsIgnoreCase(finalOwnerType))
                                .and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)
                                        .or(qItem.itemStatus.equalsIgnoreCase(CommonConstants.STAFF_ALLOCATED)))
                                .and(qItem.condition.equalsIgnoreCase("New"))
                                .and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(productId)).and(qOutward.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        if (inOutWardMACMapingDTOS.size() > 0) {
                            inOutWardMACMapingDTOS.stream().forEach(r -> {
                                Item item = itemRepository.findById(r.getItemId()).orElse(null);
                                Product product = productRepository.findById(item.getProductId()).orElse(null);
                                r.setProductId(product.getId());
                                r.setProductName(product.getName());
                                r.setHasMac(product.getProductCategory().isHasMac());
                                r.setHasSerial(product.getProductCategory().isHasSerial());
                                r.setCondition(item.getCondition());
                            });
                        }
                        listList.addAll(inOutWardMACMapingDTOS);
                    }
                    if (t.getProduct_type().equalsIgnoreCase("Refurbished")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(productId)).and(qItem.ownerId.eq(finalOwnerId)).and(qItem.ownerType.equalsIgnoreCase(finalOwnerType)).and(qItem.condition.eq("Refurbished"));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(productId)).and(qOutward.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        if (inOutWardMACMapingDTOS.size() > 0) {
                            inOutWardMACMapingDTOS.stream().forEach(r -> {
                                Item item = itemRepository.findById(r.getItemId()).orElse(null);
                                Product product = productRepository.findById(item.getProductId()).orElse(null);
                                r.setProductId(product.getId());
                                r.setProductName(product.getName());
                                r.setHasMac(product.getProductCategory().isHasMac());
                                r.setHasSerial(product.getProductCategory().isHasSerial());
                                r.setCondition(item.getCondition());
                            });
                        }
                        listList.addAll(inOutWardMACMapingDTOS);
                    }
                });
            }
            if (productPlanGroupMappingList != null && productPlanGroupMappingList.size() != 0) {
                productPlanGroupMappingList.stream().forEach(t -> {
                    if (t.getProduct_type().equalsIgnoreCase("New")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(productId)).and(qItem.ownerId.eq(finalOwnerId)).and(qItem.ownerType.equalsIgnoreCase(finalOwnerType))
                                .and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)
                                        .or(qItem.itemStatus.equalsIgnoreCase(CommonConstants.STAFF_ALLOCATED)))
                                .and(qItem.condition.equalsIgnoreCase("New"))
                                .and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(productId)).and(qOutward.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        if (inOutWardMACMapingDTOS.size() > 0) {
                            inOutWardMACMapingDTOS.stream().forEach(r -> {
                                Item item = itemRepository.findById(r.getItemId()).orElse(null);
                                Product product = productRepository.findById(item.getProductId()).orElse(null);
                                r.setProductId(product.getId());
                                r.setProductName(product.getName());
                                r.setHasMac(product.getProductCategory().isHasMac());
                                r.setHasSerial(product.getProductCategory().isHasSerial());
                                r.setCondition(item.getCondition());
                            });
                        }
                        listList.addAll(inOutWardMACMapingDTOS);
                    }
                    if (t.getProduct_type().equalsIgnoreCase("Refurbished")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(productId)).and(qItem.ownerId.eq(finalOwnerId)).and(qItem.ownerType.equalsIgnoreCase(finalOwnerType)).and(qItem.condition.eq("Refurbished"));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(productId)).and(qOutward.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        if (inOutWardMACMapingDTOS.size() > 0) {
                            inOutWardMACMapingDTOS.stream().forEach(r -> {
                                Item item = itemRepository.findById(r.getItemId()).orElse(null);
                                Product product = productRepository.findById(item.getProductId()).orElse(null);
                                r.setProductId(product.getId());
                                r.setProductName(product.getName());
                                r.setHasMac(product.getProductCategory().isHasMac());
                                r.setHasSerial(product.getProductCategory().isHasSerial());
                                r.setCondition(item.getCondition());
                            });
                        }
                        listList.addAll(inOutWardMACMapingDTOS);
                    }
                });
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return listList;
    }

    public List<InOutWardMACMapingDTO> getInOutMacMappingForNonSerializedItemBasedOnProductCondtion(Long id, Long ownerId, String ownerType, Long planId, Long planGroupId, Long productCategoryId) {
        try {
            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOList = new ArrayList<>();
            List<Productplanmapping> productplanmappingList = productPlanMappingRepository.getallfromplanid(planId);
            if (productplanmappingList.size() != 0) {
                productplanmappingList.stream().forEach(r -> {
                    if (r.getProduct_type().equalsIgnoreCase("New")) {
                        QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
                        BooleanExpression booleanExpression = qNonSerializedItem.isDeleted.eq(false).and(qNonSerializedItem.productId.eq(id)).and(qNonSerializedItem.ownerId.eq(ownerId)).and(qNonSerializedItem.ownerType.equalsIgnoreCase(ownerType))
                                .and(qNonSerializedItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qNonSerializedItem.itemStatus.ne(CommonConstants.DEFECTIVE)).and(qNonSerializedItem.nonSerializedItemcondition.equalsIgnoreCase("New"));
                        List<NonSerializedItem> nonSerializedItemList = IterableUtils.toList(nonSerializedItemRepository.findAll(booleanExpression));
                        List<Long> nonSerializedItemIds = nonSerializedItemList.stream().map(NonSerializedItem::getId).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = qInOutWardMACMapping.nonSerializedItemId.in(nonSerializedItemIds).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull());
                        List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(boolExp));
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        if (inOutWardMACMapingDTOS.size() > 0) {
                            inOutWardMACMapingDTOS.stream().forEach(inOutWardMACMapingDTO -> {
                                Item item = itemRepository.findById(inOutWardMACMapingDTO.getItemId()).orElse(null);
                                Product product = productRepository.findById(item.getProductId()).orElse(null);
                                inOutWardMACMapingDTO.setProductId(product.getId());
                            });
                        }
                        inOutWardMACMapingDTOList.addAll(inOutWardMACMapingDTOS);
                    }
                    if (r.getProduct_type().equalsIgnoreCase("Refurbished")) {
                        QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
                        BooleanExpression booleanExpression = qNonSerializedItem.isDeleted.eq(false).and(qNonSerializedItem.productId.eq(id)).and(qNonSerializedItem.ownerId.eq(ownerId)).and(qNonSerializedItem.ownerType.equalsIgnoreCase(ownerType)).and(qNonSerializedItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qNonSerializedItem.nonSerializedItemcondition.equalsIgnoreCase("Refurbished"));
                        List<NonSerializedItem> nonSerializedItemList = IterableUtils.toList(nonSerializedItemRepository.findAll(booleanExpression));
                        List<Long> nonSerializedItemIds = nonSerializedItemList.stream().map(NonSerializedItem::getId).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = qInOutWardMACMapping.nonSerializedItemId.in(nonSerializedItemIds).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull());
                        List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(boolExp));
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        if (inOutWardMACMapingDTOS.size() > 0) {
                            inOutWardMACMapingDTOS.stream().forEach(inOutWardMACMapingDTO -> {
                                Item item = itemRepository.findById(inOutWardMACMapingDTO.getItemId()).orElse(null);
                                Product product = productRepository.findById(item.getProductId()).orElse(null);
                                inOutWardMACMapingDTO.setProductId(product.getId());
                                inOutWardMACMapingDTO.setProductName(product.getName());
                            });
                        }
                        inOutWardMACMapingDTOList.addAll(inOutWardMACMapingDTOS);
                    }
                });

            }
            return inOutWardMACMapingDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public List<InOutWardMACMapingDTO> getInOutMacMappingForSerializedItemBasedOnProductType(Long id, Long ownerId, String ownerType, Long planid, Long planGroupId, Long productCategoryId) {
        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOList = new ArrayList<>();
        try {
            List<Productplanmapping> productplanmappingList = productPlanMappingRepository.getallfromplanid(planid);
            if (productplanmappingList.size() != 0) {
                productplanmappingList.stream().forEach(t -> {
                    if (t.getProduct_type().equalsIgnoreCase("New")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(id)).and(qItem.ownerId.eq(ownerId)).and(qItem.ownerType.equalsIgnoreCase(ownerType))
                                .and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)
                                        .or(qItem.itemStatus.equalsIgnoreCase(CommonConstants.STAFF_ALLOCATED)))
                                .and(qItem.condition.equalsIgnoreCase("New"))
                                .and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(id)).and(qOutward.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        if (inOutWardMACMapingDTOS.size() > 0) {
                            inOutWardMACMapingDTOS.stream().forEach(r -> {
                                Item item = itemRepository.findById(r.getItemId()).orElse(null);
                                Product product = productRepository.findById(item.getProductId()).orElse(null);
                                r.setProductId(product.getId());
                                r.setProductName(product.getName());
                                r.setHasMac(product.getProductCategory().isHasMac());
                                r.setHasSerial(product.getProductCategory().isHasSerial());
                            });
                        }
                        inOutWardMACMapingDTOList.addAll(inOutWardMACMapingDTOS);
                    }
                    if (t.getProduct_type().equalsIgnoreCase("New")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false)
                                .and(qItem.productId.eq(id))
                                .and(qItem.ownerId.eq(ownerId))
                                .and(qItem.ownerType.equalsIgnoreCase(ownerType))
                                .and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)
                                        .or(qItem.itemStatus.equalsIgnoreCase(CommonConstants.STAFF_ALLOCATED)))
                                .and(qItem.condition.equalsIgnoreCase("New"));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(id)).and(qOutward.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        if (inOutWardMACMapingDTOS.size() > 0) {
                            inOutWardMACMapingDTOS.stream().forEach(r -> {
                                Item item = itemRepository.findById(r.getItemId()).orElse(null);
                                Product product = productRepository.findById(item.getProductId()).orElse(null);
                                r.setProductId(product.getId());
                                r.setProductName(product.getName());
                                r.setHasMac(product.getProductCategory().isHasMac());
                                r.setHasSerial(product.getProductCategory().isHasSerial());
                            });
                        }
                        inOutWardMACMapingDTOList.addAll(inOutWardMACMapingDTOS);
                    }
                });
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return inOutWardMACMapingDTOList;
    }

    public Page<Item> getSerializedItemForInward(Long inwardId, Long id, Long ownerId, String ownerType, String inwardApprovalStatus, PaginationRequestDTO requestDTO) {
        try {
            PageRequest pageRequest = PageRequest.of(requestDTO.getPage() - 1, requestDTO.getPageSize(), Sort.by("id").descending());
            if (inwardApprovalStatus != null && inwardApprovalStatus.equalsIgnoreCase("Pending")) {
                List<Long> itemIds = inOutWardMacRepo.findItemIdsByInwardIdOfOutward(inwardId);
                if (!itemIds.isEmpty()) {
                    return itemRepository.findItemsByIds(itemIds, pageRequest);
                } else {
                    return itemRepository.findActiveItems(id, ownerId, ownerType, Collections.singletonList(inwardId), pageRequest);
                }
            } else {
                return itemRepository.findActiveItems(id, ownerId, ownerType, Collections.singletonList(inwardId), pageRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Page<Item> searchSerializedItemForShowMacAddress(Long inwardId, Long productId, Long ownerId, String ownerType,
                                                            String inwardApprovalStatus, SearchInventoryDTO requestDTO,
                                                            List<GenericSearchModel> filterList) {
        try {
            PageRequest pageRequest = PageRequest.of(requestDTO.getPage() - 1, requestDTO.getPageSize(), Sort.by("id").descending());
            if (filterList != null && !filterList.isEmpty()) {
                return searchItemShowMacAddress(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest, filterList.get(0));
            }
            return Page.empty(pageRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error searching serialized items for show MAC address", e);
        }
    }

    public Page<Item> searchSerializedItemForAddMacAddress(Long productId, Long ownerId, String ownerType,
                                                           SearchInventoryDTO searchInventoryDTO, List<GenericSearchModel> filters) {
        try {
            PageRequest pageRequest = PageRequest.of(searchInventoryDTO.getPage() - 1, searchInventoryDTO.getPageSize(), Sort.by("id").descending());
            if (filters != null && !filters.isEmpty()) {
                return searchItemAddMacAddress(productId, ownerId, ownerType, pageRequest, filters.get(0));
            }
            return Page.empty(pageRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error searching serialized items for add MAC address", e);
        }
    }

    public Page<Item> searchItemAddMacAddress(Long productId, Long ownerId, String ownerType, PageRequest pageRequest, GenericSearchModel searchModel) {
        try {
            String filterColumn = Optional.ofNullable(searchModel.getFilterColumn()).map(String::trim).orElse("");
            List<Long> activeInwardIds = getActiveInwardIds(productId, ownerId, ownerType);
            if (activeInwardIds.isEmpty()) {
                return Page.empty(pageRequest);
            }
            switch (filterColumn) {
                case SearchConstants.ITEM_ID:
                    return searchAddMacByItemId(productId, ownerId, ownerType, pageRequest, searchModel, activeInwardIds);
                case SearchConstants.MAC:
                    return searchAddMacByMac(productId, ownerId, ownerType, pageRequest, searchModel, activeInwardIds);
                case SearchConstants.SERIAL_NUMBER:
                    return searchAddMacBySerialNumber(productId, ownerId, ownerType, pageRequest, searchModel, activeInwardIds);
                case SearchConstants.ASSET_ID:
                    return searchAddMacByAssetId(productId, ownerId, ownerType, pageRequest, searchModel, activeInwardIds);
                case SearchConstants.ITEM_TYPE:
                    return searchAddMacByItemType(productId, ownerId, ownerType, pageRequest, searchModel, activeInwardIds);
                default:
                    return Page.empty(pageRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Long> getActiveInwardIds(Long productId, Long ownerId, String ownerType) {
        return getMvnoIdFromCurrentStaff() != 1
                ? inwardRepository.findInwardIdsByApprovalStatusAndMvnoIdAndDestinationTypeAndDestinationIdAndProductId(
                "Approve", Arrays.asList(getMvnoIdFromCurrentStaff(), 1), ownerType, ownerId, productId)
                : inwardRepository.findInwardIdsByApprovalStatusAndDestinationTypeAndDestinationIdAndProductId(
                "Approve", ownerType, ownerId, productId);
    }

    public Page<Item> searchAddMacByItemType(Long productId, Long ownerId, String ownerType, PageRequest pageRequest,
                                             GenericSearchModel searchModel, List<Long> activeInwardIds) {
        String condition = searchModel.getFilterValue();
        return itemRepository.findActiveItemsByItemType(productId, ownerId, ownerType,
                activeInwardIds, condition, pageRequest);
    }

    public Page<Item> searchAddMacByAssetId(Long productId, Long ownerId, String ownerType, PageRequest pageRequest,
                                            GenericSearchModel searchModel, List<Long> activeInwardIds) {
        String assetId = searchModel.getFilterValue();
        return itemRepository.findActiveItemsByAssetId(productId, ownerId, ownerType,
                activeInwardIds, assetId, pageRequest);
    }

    public Page<Item> searchAddMacBySerialNumber(Long productId, Long ownerId, String ownerType, PageRequest pageRequest,
                                                 GenericSearchModel searchModel, List<Long> activeInwardIds) {
        String serialNumber = searchModel.getFilterValue();
        return itemRepository.findActiveItemsBySerialNumber(productId, ownerId, ownerType,
                activeInwardIds, serialNumber, pageRequest);
    }

    public Page<Item> searchAddMacByMac(Long productId, Long ownerId, String ownerType, PageRequest pageRequest,
                                        GenericSearchModel searchModel, List<Long> activeInwardIds) {
        String macAddress = searchModel.getFilterValue();
        return itemRepository.findActiveItemsByMac(productId, ownerId, ownerType,
                activeInwardIds, macAddress, pageRequest);
    }

    public Page<Item> searchAddMacByItemId(Long productId, Long ownerId, String ownerType,
                                           PageRequest pageRequest, GenericSearchModel searchModel,
                                           List<Long> activeInwardIds) {
        String itemIdValue = searchModel.getFilterValue();
        return itemRepository.findActiveItemsByItemId(productId, ownerId, ownerType,
                activeInwardIds, itemIdValue, pageRequest);
    }

    public Page<Item> searchItemShowMacAddress(String inwardApprovalStatus, Long inwardId, Long productId,
                                               Long ownerId, String ownerType, PageRequest pageRequest, GenericSearchModel searchModel) {
        try {
            String filterColumn = Optional.ofNullable(searchModel.getFilterColumn())
                    .map(String::trim)
                    .orElse("");
            switch (filterColumn) {
                case SearchConstants.ITEM_ID:
                    return searchByItemId(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest, searchModel);
                case SearchConstants.MAC:
                    return searchByMac(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest, searchModel);
                case SearchConstants.SERIAL_NUMBER:
                    return searchBySerialNumber(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest, searchModel);
                case SearchConstants.ASSET_ID:
                    return searchByAssetId(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest, searchModel);
                case SearchConstants.ITEM_TYPE:
                    return searchByItemType(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest, searchModel);
                default:
                    return Page.empty(pageRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Page<Item> searchByItemId(String inwardApprovalStatus, Long inwardId, Long productId,
                                     Long ownerId, String ownerType, PageRequest pageRequest,
                                     GenericSearchModel searchModel) {

        String itemIdValue = searchModel.getFilterValue();
        return searchItemsByCondition(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest,
                itemIds -> itemRepository.findItemsByIdsAndItemId(itemIds, itemIdValue, pageRequest),
                () -> itemRepository.findActiveItemsByItemId(productId, ownerId, ownerType,
                        Collections.singletonList(inwardId), itemIdValue, pageRequest)
        );
    }


    public Page<Item> searchByMac(String inwardApprovalStatus, Long inwardId, Long productId,
                                  Long ownerId, String ownerType, PageRequest pageRequest,
                                  GenericSearchModel searchModel) {

        String macValue = searchModel.getFilterValue();
        return searchItemsByCondition(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest,
                itemIds -> itemRepository.findItemsByIdsAndMacAddress(itemIds, macValue, pageRequest),
                () -> itemRepository.findActiveItemsByMac(productId, ownerId, ownerType,
                        Collections.singletonList(inwardId), macValue, pageRequest)
        );
    }

    public Page<Item> searchBySerialNumber(String inwardApprovalStatus, Long inwardId, Long productId,
                                           Long ownerId, String ownerType, PageRequest pageRequest,
                                           GenericSearchModel searchModel) {

        String serialValue = searchModel.getFilterValue();
        return searchItemsByCondition(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest,
                itemIds -> itemRepository.findItemsByIdsAndSerialNumber(itemIds, serialValue, pageRequest),
                () -> itemRepository.findActiveItemsBySerialNumber(productId, ownerId, ownerType,
                        Collections.singletonList(inwardId), serialValue, pageRequest)
        );
    }

    public Page<Item> searchByAssetId(String inwardApprovalStatus, Long inwardId, Long productId,
                                      Long ownerId, String ownerType, PageRequest pageRequest,
                                      GenericSearchModel searchModel) {

        String assetIdValue = searchModel.getFilterValue();
        return searchItemsByCondition(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest,
                itemIds -> itemRepository.findItemsByIdsAndAssetId(itemIds, assetIdValue, pageRequest),
                () -> itemRepository.findActiveItemsByAssetId(productId, ownerId, ownerType,
                        Collections.singletonList(inwardId), assetIdValue, pageRequest)
        );
    }

    public Page<Item> searchByItemType(String inwardApprovalStatus, Long inwardId, Long productId,
                                       Long ownerId, String ownerType, PageRequest pageRequest,
                                       GenericSearchModel searchModel) {

        String itemTypeValue = searchModel.getFilterValue();
        return searchItemsByCondition(inwardApprovalStatus, inwardId, productId, ownerId, ownerType, pageRequest,
                itemIds -> itemRepository.findItemsByIdsAndItemType(itemIds, itemTypeValue, pageRequest),
                () -> itemRepository.findActiveItemsByItemType(productId, ownerId, ownerType,
                        Collections.singletonList(inwardId), itemTypeValue, pageRequest)
        );
    }

    public Page<Item> searchItemsByCondition(String inwardApprovalStatus, Long inwardId, Long productId,
                                             Long ownerId, String ownerType, PageRequest pageRequest,
                                             Function<List<Long>, Page<Item>> idSearchFunction,
                                             Supplier<Page<Item>> defaultSearchFunction) {
        boolean isPending = Optional.ofNullable(inwardApprovalStatus)
                .map(status -> status.equalsIgnoreCase("Pending"))
                .orElse(false);

        if (isPending) {
            List<Long> itemIds = inOutWardMacRepo.findItemIdsByInwardIdOfOutward(inwardId);
            return !itemIds.isEmpty()
                    ? idSearchFunction.apply(itemIds)
                    : defaultSearchFunction.get();
        } else {
            return defaultSearchFunction.get();
        }
    }

    /**
     * Method for Get Serialized Item for Outward
     * @param id
     * @param ownerId
     * @param ownerType
     * @param requestDTO
     * @return
     */
    public Page<Item> getSerializedItemForOutward(Long id, Long ownerId, String ownerType, PaginationRequestDTO requestDTO) {
        try {
            PageRequest pageRequest = PageRequest.of(requestDTO.getPage() - 1, requestDTO.getPageSize(), Sort.by("id").descending());
            List<Long> activeInwardIds;
            if (getMvnoIdFromCurrentStaff() != 1) {
                activeInwardIds = inwardRepository.findInwardIdsByApprovalStatusAndMvnoIdAndDestinationTypeAndDestinationIdAndProductId(
                        "Approve", Arrays.asList(getMvnoIdFromCurrentStaff(), 1), ownerType, ownerId, id
                );
            } else {
                activeInwardIds = inwardRepository.findInwardIdsByApprovalStatusAndDestinationTypeAndDestinationIdAndProductId(
                        "Approve", ownerType, ownerId, id
                );
            }
            return activeInwardIds.isEmpty() ? Page.empty(pageRequest) : itemRepository.findActiveItems(id, ownerId, ownerType, activeInwardIds, pageRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getRandomenumber(String flag1, String flag2, String flag3) {
        String flag = "";
        if (flag1 != null) {
            flag += flag1;
        }
        if (flag2 != null) {
            flag += flag2;
        }
        if (flag3 != null) {
            flag += flag3;
        }
        return flag;
    }

    @Transactional
    public Item updateItemMacAndSerial(Long itemId, String macAddress, String serialNumber) {
        try {
//            long startTime = System.currentTimeMillis(); // Capture start time
            int updatedRows = itemRepository.updateItemDetails(itemId, macAddress, serialNumber);
            if (updatedRows == 0) {
                throw new Exception("Item not found for ID: " + itemId);
            }
            inOutWardMACService.updateMacSerialByItem(itemId, macAddress, serialNumber);
//            long endTime = System.currentTimeMillis(); // Capture end time
//            long duration = endTime - startTime; // Calculate duration
//            System.out.println("Execution Time for Item Update: " + duration + " ms");
            // Fetch the updated entity
            return itemRepository.findById(itemId)
                    .orElseThrow(() -> new Exception("Item not found after update"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating item: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Item updateItemSerial(Long itemId, String serialNumber) {
        try {
            int updatedRows = itemRepository.updateItemDetails(itemId, serialNumber);
            if (updatedRows == 0) {
                throw new Exception("Item not found for ID: " + itemId);
            }
            inOutWardMACService.updateSerialByItem(itemId, serialNumber);
            return itemRepository.findById(itemId)
                    .orElseThrow(() -> new Exception("Item not found after update"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating serial number: " + e.getMessage(), e);
        }

    }

    @Transactional
    public void updateSelectedItemMacAndSerial(Long itemId, String macAddress, String serialNumber) throws Exception {
        try {
            if (!Objects.equals(macAddress, null)) {
                Item item = itemRepository.findById(itemId).get();
                String itemMac = item.getMacAddress();
                if (!Objects.equals(macAddress, null)) {
                    if (itemMac != null) {
                        if (!itemMac.equals(macAddress)) {
                            inOutWardMACService.duplicateVerifyAtSave(macAddress);
                        }
                    } else {
                        inOutWardMACService.duplicateVerifyAtSave(macAddress);
                    }
                    updateItemMacAndSerial(itemId, macAddress, serialNumber);
                } else {
                    updateItemSerial(itemId, serialNumber);
                }
            } else {
                updateItemSerial(itemId, serialNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Selected " + macAddress + " is Already Exist");
        }
    }

    public void validateMac(String macAddress, String serialNumber) throws Exception {
        if (macAddress == null || macAddress.equals("")) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter mac in selected item", null);
        } else {
            validateSerialNumber(serialNumber);
        }
    }

    public void validateSerialNumber(String serialNumber) throws Exception {
        if (serialNumber == null || serialNumber.equals("")) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter serial number in selected item", null);
        }
    }

    public Item getItemDetails(Long itemId, Long custinventoryid) {
        try {
            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(custinventoryid).get();
            QItem qItem = QItem.item;
            BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.id.eq(itemId));
            List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
            if (!customerInventoryMapping.getCreatedate().toLocalDate().equals(LocalDate.now())) {
                itemList.stream().forEach(item -> {
                    Product product = productRepository.findById(item.getProductId()).get();
                    if (item.getCondition().equalsIgnoreCase("New")) {
                        if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                            item.setProductRefundAmount(product.getNewProductRefAmountInWarranty());
                        } else if (item.getWarranty().equalsIgnoreCase("Expired")) {
                            item.setProductRefundAmount(product.getNewProductRefAmountPostWarranty());
                        }
                    }
                    if (item.getCondition().equalsIgnoreCase("Refurbished")) {
                        if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                            item.setProductRefundAmount(product.getRefurburshiedProductRefAmountInWarranty());
                        } else if (item.getWarranty().equalsIgnoreCase("Expired")) {
                            item.setProductRefundAmount(product.getRefurburshiedProductRefAmountPostWarranty());
                        }
                    }
                    item.setRefundFlag(true);
                });
            } else {
                itemList.stream().forEach(item -> {
                    item.setRefundFlag(false);
                });
            }
            return itemList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public GenericDataDTO serializedItems(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchItemsPojo searchItemsPojo) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (searchItemsPojo != null) {
                genericDataDTO = itemService.findItems(pageNumber, customPageSize, sortBy, sortOrder, searchItemsPojo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }


    public GenericDataDTO findItems(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchItemsPojo search) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Item> paginationList = null;
        QItem qItem = QItem.item;
        List<ItemDto> itemDtoList = new ArrayList<>();
        BooleanExpression booleanExpression = qItem.isNotNull().and(qItem.isDeleted.eq(false));
        List<Integer> serviceAreaIds = getLoggedInUser().getServiceAreaIdList();
        List<Long> serviceAreaIdsLong = serviceAreaIds.stream().map(Integer::longValue).collect(Collectors.toList());
        if (!serviceAreaIds.isEmpty()) {
//                            Items By Staff
            List<Integer> staffIdsByMapping = staffUserServiceAreaMappingRepository.findAllByServiceIdIn(serviceAreaIds).stream()
                    .map(StaffUserServiceAreaMapping::getStaffId)
                    .collect(Collectors.toList());
            List<Long> staffIds = staffUserRepository.findAllByIsDeleteIsFalseAndMvnoIdInAndIdInWithSpecificParameter(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), staffIdsByMapping).stream()
                    .map(StaffUser::getId)
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
//                            Items By Warehouse
            List<Long> warehouseIdsByMapping = wareHouseManagmentServiceAreamappingRepo.findAllByServiceIdIn(serviceAreaIds).stream()
                    .map(WareHouseServiceAreaMapping::getWarehouseId)
                    .collect(Collectors.toList());
            List<Long> warehouseIds = warehouseManagementRepository.findAllByIdInAndIsDeletedIsFalseAndMvnoIdInWithoutPageable(warehouseIdsByMapping, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                    .map(WareHouse::getId)
                    .collect(Collectors.toList());
//                            Items by Partner
            List<Integer> partnerIdsMapping = partnerServiceAreaMappingRepo.findAllByServiceIdIn(serviceAreaIds).stream()
                    .map(PartnerServiceAreaMapping::getPartnerId)
                    .collect(Collectors.toList());
            List<Long> partnerIds = partnerRepository.findAllLightPartnerByIdInAndIsDeleteIsFalseAndMvnoIdIn(partnerIdsMapping, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                    .map(Partner::getId)
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
//                            Items by Customers
            List<Long> customerIds = customersRepository.findAllLightCustomerByServiceareaIdInAndMvnoIdInAndIsDeletedIsFalse(serviceAreaIdsLong, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                    .map(Customers::getId)
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
//                            Items by Pop
            List<Long> popIdsMapping = popServiceAreaMappingRepo.findAllByServiceAreaIdIn(serviceAreaIds).stream()
                    .map(PopServiceAreaMapping::getPopId)
                    .collect(Collectors.toList());
            List<Long> popIds = popManagementRepository.findAllLightPopManagementByIsDeletedIsFalseAndMvnoIdInAndIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), popIdsMapping).stream()
                    .map(PopManagement::getId)
                    .collect(Collectors.toList());
//                            Items by Service Area
            List<Long> serviceAreaIdList = serviceAreaRepository.findAllLightServiceAreaByIdInAndIsDeletedIsFalseAndMvnoIdIn(serviceAreaIdsLong, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                    .map(ServiceArea::getId)
                    .collect(Collectors.toList());
            booleanExpression = booleanExpression
                    .and((qItem.ownerId.in(staffIds).and(qItem.ownerType.equalsIgnoreCase(CommonConstants.SERIALISED_ITEM_OWNERTYPE.STAFF)))
                            .or(qItem.ownerId.in(warehouseIds).and(qItem.ownerType.equalsIgnoreCase(CommonConstants.SERIALISED_ITEM_OWNERTYPE.WAREHOUSE)))
                            .or(qItem.ownerId.in(partnerIds).and(qItem.ownerType.equalsIgnoreCase(CommonConstants.SERIALISED_ITEM_OWNERTYPE.PARTNER)))
                            .or(qItem.ownerId.in(customerIds).and(qItem.ownerType.equalsIgnoreCase(CommonConstants.SERIALISED_ITEM_OWNERTYPE.CUSTOMER)))
                            .or(qItem.ownerId.in(popIds).and(qItem.ownerType.equalsIgnoreCase(CommonConstants.SERIALISED_ITEM_OWNERTYPE.POP)))
                            .or(qItem.ownerId.in(serviceAreaIdList).and(qItem.ownerType.equalsIgnoreCase(CommonConstants.SERIALISED_ITEM_OWNERTYPE.EXTERNALITEM_SERVICEAREA)))
                            .or(qItem.ownerId.in(serviceAreaIdList).and(qItem.ownerType.equalsIgnoreCase(CommonConstants.SERIALISED_ITEM_OWNERTYPE.SERVICE_AREA))));
        }
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qItem.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));

            if (search.getOwnerType() != null && !"null".equals(search.getOwnerType()) && !"".equals(search.getOwnerType())) {
                booleanExpression = booleanExpression.and(qItem.ownerType.startsWithIgnoreCase(search.getOwnerType()));
            }
            if (search.getOwnerId() != null && !"null".equals(search.getOwnerType()) && !"".equals(search.getOwnerType())) {
                booleanExpression = booleanExpression.and(qItem.ownerId.eq(Long.valueOf(String.valueOf(search.getOwnerId()))));
            }
            if (search.getProductId() != null && !"null".equals(search.getProductId()) && !"".equals(search.getProductId())) {
                booleanExpression = booleanExpression.and(qItem.productId.eq(Long.valueOf(String.valueOf(search.getProductId()))));
            }

            if (search.getInwardId() != null && !"null".equals(search.getInwardId()) && !"".equals(search.getInwardId())) {
                booleanExpression = booleanExpression.and(qItem.currentInwardId.eq(Long.valueOf(String.valueOf(search.getInwardId()))));
            }

            if (search.getItemType() != null && !"null".equals(search.getItemType()) && !"".equals(search.getItemType())) {
                booleanExpression = booleanExpression.and(qItem.condition.startsWithIgnoreCase(search.getItemType()));
            }

            if (search.getItemStatus() != null && !"null".equals(search.getItemStatus()) && !"".equals(search.getItemStatus())) {
                booleanExpression = booleanExpression.and(qItem.itemStatus.startsWithIgnoreCase(search.getItemStatus()));
            }

            if (search.getOwnership() != null && !"null".equals(search.getOwnership()) && !"".equals(search.getOwnership())) {
                if (search.getOwnership().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED) ||
                        search.getOwnership().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED)) {
                    booleanExpression = booleanExpression.and(qItem.ownershipType.startsWithIgnoreCase(search.getOwnership())
                            .or(qItem.ownershipType.startsWithIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED))
                            .or(qItem.ownershipType.startsWithIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED)));
                } else {
                    booleanExpression = booleanExpression.and(qItem.ownershipType.startsWithIgnoreCase(search.getOwnership()));
                }
            }

            if (search.getWarrantyStatus() != null && !"null".equals(search.getWarrantyStatus()) && !"".equals(search.getWarrantyStatus())) {
                booleanExpression = booleanExpression.and(qItem.warranty.startsWithIgnoreCase(search.getWarrantyStatus()));
            }
            if (search.getSerialNumber() != null && !"null".equals(search.getSerialNumber()) && !"".equals(search.getSerialNumber())) {
                booleanExpression = booleanExpression.and(qItem.serialNumber.startsWithIgnoreCase(search.getSerialNumber()));
            }
            if (search.getMacAddress() != null && !"null".equals(search.getMacAddress()) && !"".equals(search.getMacAddress())) {
                booleanExpression = booleanExpression.and(qItem.macAddress.startsWithIgnoreCase(search.getSerialNumber()));
            }
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);

            paginationList = itemRepository.findAll(booleanExpression, pageRequest);
            List<ItemDto> dto = paginationList.get().map(item -> itemMapper.domainToDTO(item, new CycleAvoidingMappingContext())).collect(Collectors.toList());

            for (ItemDto itemDto : dto) {
                if (itemDto.getCurrentInwardId() != null) {
                    itemDto.setCurrentInwardNumber(inwardRepository.findById(itemDto.getCurrentInwardId()).get().getInwardNumber());
                }
                if (itemDto.getProductId() != null) {
                    itemDto.setProductName(productService.getEntityById(itemDto.getProductId().longValue()).getName());
                }
                if (!itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).isEmpty()) {
                    itemDto.setFilename(itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).get(0).getFilename());
                    itemDto.setItemConditionId(itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).get(0).getId());
                }
                Optional<Item> item = itemRepository.findById(itemDto.getId());
                if (itemRepository.getOne(itemDto.getId()).getRemarks() != null) {
                    itemDto.setRemarks(itemRepository.getOne(itemDto.getId()).getRemarks());
                }

                if (item.isPresent()) {
                    itemDto.setOemWarrantyStatus(item.get().getOemWarrantyStatus());
                    itemDto.setWarranty(item.get().getWarranty());
                    itemDto.setOemWarrantyRemainingDays(item.get().getOemWarrantyRemainingDays());
                    itemDto.setWarrantyPeriod(item.get().getWarrantyPeriod());
                }
                if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.STAFF))
                    itemDto.setOwnerName(staffUserRepository.findLightStaffUserById(itemDto.getOwnerId().intValue()).get().getUsername());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.WAREHOUSE))
                    itemDto.setOwnerName(warehouseManagementRepository.findLightWarehouseById(itemDto.getOwnerId().longValue()).getName());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.POP))
                    itemDto.setOwnerName(popManagementRepository.findLightPopManagementById(itemDto.getOwnerId().longValue()).getName());
                else if ((itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.SERVICE_AREA)) || (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.SERIALISED_ITEM_OWNERTYPE.SERVICEAREA)))
                    itemDto.setOwnerName(serviceAreaRepository.findAllLightServiceAreaById(itemDto.getOwnerId().longValue()).getName());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.PARTNER))
                    itemDto.setOwnerName(partnerRepository.findAllLightPartnerById(itemDto.getOwnerId().intValue()).getName());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.CUSTOMER))
                    itemDto.setOwnerName(customersRepository.findAllLightCustomerById(itemDto.getOwnerId().intValue()).getUsername());
                itemDtoList.add(itemDto);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }

        genericDataDTO.setDataList(itemDtoList);
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        return genericDataDTO;
    }

    public boolean deleteItemById(long id) {
        try {
            Optional<Item> itemOptional = itemRepository.findById(id);
            if (!itemOptional.isPresent()) {
                return false;
            }
            Item item = itemOptional.get();
            Optional<Inward> inward = inwardRepository.findById(item.getCurrentInwardId());
            String inwardApprovalStatus = inwardRepository.findApprovalStatusByInwardId(item.getCurrentInwardId());
            if ("Approve".equals(inwardApprovalStatus) || "Rejected".equals(inwardApprovalStatus)) {
                return false;
            }
            item.setIsDeleted(true);
            itemRepository.save(item);
            inward.get().setTotalMacSerial(inward.get().getTotalMacSerial() - 1);
            inwardRepository.save(inward.get());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Long resolveInwardId(SearchInventoryDTO searchInventoryDTO) {
        try {
            if (searchInventoryDTO.getEntityType() == null) {
                return null;
            }
            if ("inward".equalsIgnoreCase(searchInventoryDTO.getEntityType())) {
                return searchInventoryDTO.getEntityId();
            }
            if ("outward".equalsIgnoreCase(searchInventoryDTO.getEntityType())) {
                return inwardRepository.findInwardIdByOutwardId(searchInventoryDTO.getEntityId());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Page<Item> fetchSerializedItems(Long inwardId, SearchInventoryDTO searchInventoryDTO) {
        try {
            if (inwardId != null) {
                String inwardApprovalStatus = inwardRepository.findApprovalStatusByInwardId(inwardId);
                return searchSerializedItemForShowMacAddress(
                        inwardId, searchInventoryDTO.getProductId(),
                        searchInventoryDTO.getOwnerId(), searchInventoryDTO.getOwnerType(),
                        inwardApprovalStatus, searchInventoryDTO, searchInventoryDTO.getFilters()
                );
            }
            return searchSerializedItemForAddMacAddress(
                    searchInventoryDTO.getProductId(),
                    searchInventoryDTO.getOwnerId(), searchInventoryDTO.getOwnerType(),
                    searchInventoryDTO, searchInventoryDTO.getFilters()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<InOutWardMACMapingDTO> searchInOutwardMacMapping(SearchInOutMacMapping searchInOutMacMapping) {
        List<GenericSearchModel> filters = searchInOutMacMapping.getPaginationRequestDTO().getFilters();
        if (filters != null && !filters.isEmpty()) {
            return searchInOutMacMapByCondition(filters.get(0), searchInOutMacMapping);
        }
        return null;
    }

    private List<InOutWardMACMapingDTO> searchInOutMacMapByCondition(GenericSearchModel genericSearchModel,
                                                                     SearchInOutMacMapping searchInOutMacMapping) {
        String filterColumn = Optional.ofNullable(genericSearchModel.getFilterColumn())
                .map(String::trim)
                .orElse("");
        return searchInOutMacMap(genericSearchModel, searchInOutMacMapping, filterColumn);
    }

    private List<InOutWardMACMapingDTO> searchInOutMacMap(GenericSearchModel genericSearchModel, SearchInOutMacMapping searchInOutMacMapping, String filterColumn) {
        String filterValue = genericSearchModel.getFilterValue();
        return getInOutwardMACMappingList(searchInOutMacMapping, filterValue, filterColumn);
    }

    public List getInOutwardMappingDataList(GenericDataDTO genericDataDTO, ItemHistoryRequestDTO itemHistoryRequestDTO) throws Exception {
        try {
            Long productId = itemHistoryRequestDTO.getProductId();
            Integer ownerId = itemHistoryRequestDTO.getOwnerId();
            String ownerType = itemHistoryRequestDTO.getOwnerType();
            String productName = productRepository.findProductNameByProductId(productId);
            Long pcId = productRepository.findProductCategoryIdByProductId(productId);
            String pcCatType = productCategoryRepository.findTypeById(pcId);
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            boolean isTrackable = productCategoryRepository.findHasTrackableById(pcId);
            StaffUser staffUser = staffUserRepository.findAllByIdWithSpecificParameter(ownerId).get(0);
            if (staffUser.getPartnerid() != 1) {
                ownerId = Integer.valueOf(staffUser.getPartnerid());
                ownerType = CommonConstants.PARTNER;
            }
            if (pcCatType.equals(CommonConstants.CUSTOMER_BIND)) {
                if (hasMac || hasSerial) {
                    genericDataDTO.setDataList(getInOutMacMappingForSerializedItem(productId, Long.valueOf(ownerId), ownerType, hasMac, hasSerial, productName, false, null, null));
                }
                if (!hasSerial && !isTrackable) {
                    genericDataDTO.setDataList(getInOutMacMappingForSerializedItem(productId, Long.valueOf(ownerId), ownerType, hasMac, hasSerial, productName, false, null, null));
                }
            } else if (pcCatType.equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)) {
                if (hasMac || hasSerial) {
                    genericDataDTO.setDataList(getInOutMacMappingForSerializedCBNB(productId, Long.valueOf(ownerId), ownerType, false, null, null, productName, hasMac, hasSerial));
                }
                if (!hasSerial && !isTrackable) {
                    genericDataDTO.setDataList(getInOutMacMappingForSerializedCBNB(productId, Long.valueOf(ownerId), ownerType, false, null, null, productName, hasMac, hasSerial));
                }
            } else if (pcCatType.contains("NetworkBind") || pcCatType.equalsIgnoreCase("NA")) {
                if ((hasSerial) || (isTrackable)) {
                    genericDataDTO.setDataList(getInOutMacMappingForPopAndSA(productId, Long.valueOf(ownerId), ownerType, false, null, null, productName, hasMac, hasSerial));
                }
                if (!hasSerial && !isTrackable) {
                    genericDataDTO.setDataList(productOwnerService.getNonTrackableProductQty(productId, Long.valueOf(ownerId), ownerType));
                }
            }
            return genericDataDTO.getDataList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<InOutWardMACMapingDTO> getInOutwardMACMappingList(SearchInOutMacMapping searchInOutMacMapping, String filterValue, String filterColumn) {
        try {
            Long productId = searchInOutMacMapping.getProductId();
            Integer ownerId = searchInOutMacMapping.getOwnerId();
            String ownerType = searchInOutMacMapping.getOwnerType();
            Long pcId = productRepository.findProductCategoryIdByProductId(productId);
            String pcCatType = productCategoryRepository.findTypeById(pcId);
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            boolean isTrackable = productCategoryRepository.findHasTrackableById(pcId);
            String productName = productRepository.findProductNameByProductId(productId);
            StaffUser staffUser = staffUserRepository.findAllByIdWithSpecificParameter(ownerId).get(0);
            if (staffUser.getPartnerid() != 1) {
                ownerId = Integer.valueOf(staffUser.getPartnerid());
                ownerType = CommonConstants.PARTNER;
            }
            if (pcCatType.equals(CommonConstants.CUSTOMER_BIND)) {
                if (hasMac || hasSerial) {
                    return getInOutMacMappingForSerializedItem(productId, Long.valueOf(ownerId), ownerType, hasMac,
                            hasSerial, productName, true, filterValue, filterColumn);
                }
                if (!hasSerial && !isTrackable) {
                    return getInOutMacMappingForSerializedItem(productId, Long.valueOf(ownerId), ownerType, hasMac,
                            hasSerial, productName, true, filterValue, filterColumn);
                }
            } else if (pcCatType.equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)) {
                if (hasMac || hasSerial) {
                    return getInOutMacMappingForSerializedCBNB(productId, Long.valueOf(ownerId), ownerType, true, filterValue, filterColumn, productName, hasMac, hasSerial);
                }
                if (!hasSerial && !isTrackable) {
                    return getInOutMacMappingForSerializedCBNB(productId, Long.valueOf(ownerId), ownerType, true, filterValue, filterColumn, productName, hasMac, hasSerial);
                }
            } else if (pcCatType.contains("NetworkBind") || pcCatType.equalsIgnoreCase("NA")) {
                if (hasSerial || isTrackable) {
                    return getInOutMacMappingForPopAndSA(productId, Long.valueOf(ownerId), ownerType, true, filterValue, filterColumn, productName, hasMac, hasSerial);
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private Map<Long, Item> getItemMapBySearch(Long productId, Long ownerId, String ownerType, List<String> statuses, String filterColumn, String filterValue) {
        try {
            switch (filterColumn) {
                case SearchConstants.ITEM_ID:
                    return searchItemMapByItemId(filterValue, productId, ownerId, ownerType, statuses);
                case SearchConstants.MAC:
                    return searchItemMapByMac(filterValue, productId, ownerId, ownerType, statuses);
                case SearchConstants.SERIAL_NUMBER:
                    return searchItemMapBySerialNumber(filterValue, productId, ownerId, ownerType, statuses);
                case SearchConstants.ITEM_TYPE:
                    return searchItemMapByItemType(filterValue, productId, ownerId, ownerType, statuses);
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Map<Long, Item> searchItemMapByItemType(String filterValue, Long productId, Long ownerId, String ownerType, List<String> statuses) {
        return itemRepository.findItemByCondition(productId, ownerId, ownerType, statuses, filterValue).stream()
                .collect(Collectors.toMap(Item::getId, item -> item));
    }

    private Map<Long, Item> searchItemMapBySerialNumber(String filterValue, Long productId, Long ownerId, String ownerType, List<String> statuses) {
        return itemRepository.findItemBySerialNumber(productId, ownerId, ownerType, statuses, filterValue).stream()
                .collect(Collectors.toMap(Item::getId, item -> item));
    }

    private Map<Long, Item> searchItemMapByMac(String filterValue, Long productId, Long ownerId, String ownerType, List<String> statuses) {
        return itemRepository.findItemByMac(productId, ownerId, ownerType, statuses, filterValue)
                .stream()
                .collect(Collectors.toMap(Item::getId, item -> item));
    }

    private Map<Long, Item> searchItemMapByItemId(String filterValue, Long productId, Long ownerId, String ownerType, List<String> statuses) {
        return itemRepository.findItemByItemId(productId, ownerId, ownerType, statuses, filterValue)
                .stream()
                .collect(Collectors.toMap(Item::getId, item -> item));
    }
}



