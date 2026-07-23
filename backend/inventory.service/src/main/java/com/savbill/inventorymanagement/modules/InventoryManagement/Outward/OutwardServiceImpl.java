package com.savbill.inventorymanagement.modules.InventoryManagement.Outward;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.SearchConstants;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopServiceAreaMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopServiceAreaMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WareHouse;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WareHouseManagmentServiceAreamappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WareHouseServiceAreaMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WarehouseManagementRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMapping;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.inventorymanagement.modules.PartnerManagement.*;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerPojo;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerRepository;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerService;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerService;
import com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory.RequestInvenotryProductMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory.RequestInventoryProductMappingRepo;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamUserMapping.TeamUserMapping;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamUserMapping.TeamUserMappingsRepocitory;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.Teams;
import com.savbill.inventorymanagement.rabbitmq.InventoryFulfilmentMessage;
import com.savbill.inventorymanagement.rabbitmq.RabbitMqConstants;
import com.savbill.inventorymanagement.security.spring.SpringContext;
import com.savbill.inventorymanagement.utils.CommonUtils;
import com.google.gson.Gson;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Outward service.
 */
@Service
public class OutwardServiceImpl extends ExBaseAbstractService<OutwardDto, Outward, Long> {
    /**
     * The Outward repository.
     */
    @Autowired
    private OutwardRepository outwardRepository;

    /**
     * The Inward service.
     */
    @Autowired
    private InwardServiceImpl inwardService;

    /**
     * The Outward mapper.
     */
    @Autowired
    private OutwardMapper outwardMapper;
    /**
     * The Entity manager.
     */
    @PersistenceContext
    EntityManager entityManager;

    /**
     * The Product owner service.
     */
    @Autowired
    private ProductOwnerService productOwnerService;

    /**
     * The Product owner mapper.
     */
    @Autowired
    private ProductOwnerMapper productOwnerMapper;

    /**
     * The Inward repository.
     */
    @Autowired
    private InwardRepository inwardRepository;

    /**
     * The Warehouse management repository.
     */
    @Autowired
    private WarehouseManagementRepository warehouseManagementRepository;

    /**
     * The Team user mappings repocitory.
     */
    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;

    /**
     * The Pop management repository.
     */
    @Autowired
    private PopManagementRepository popManagementRepository;

    /**
     * The Staff user repository.
     */
    @Autowired
    private StaffUserRepository staffUserRepository;

    /**
     * The In out ward mac repo.
     */
    @Autowired
    private InOutWardMacRepo inOutWardMacRepo;
    /**
     * The Item repository.
     */
    @Autowired
    private ItemRepository itemRepository;
    /**
     * The Partner repository.
     */
    @Autowired
    private PartnerRepository partnerRepository;
    /**
     * The Partner service.
     */
    @Autowired
    private PartnerService partnerService;
    /**
     * The Product service.
     */
    @Autowired
    private ProductServiceImpl productService;
    /**
     * The Product repository.
     */
    @Autowired
    ProductRepository productRepository;
    /**
     * The Staff user service area mapping repository.
     */
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
    /**
     * The Ware house managment service areamapping repo.
     */
    @Autowired
    WareHouseManagmentServiceAreamappingRepo wareHouseManagmentServiceAreamappingRepo;

    /**
     * The Pop service area mapping repo.
     */
    @Autowired
    PopServiceAreaMappingRepo popServiceAreaMappingRepo;
    /**
     * The Service area service.
     */
    @Autowired
    ServiceAreaService serviceAreaService;

    /**
     * The Kafka message sender.
     */
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    /**
     * The Request inventory product mapping repo.
     */
    @Autowired
    private RequestInventoryProductMappingRepo requestInventoryProductMappingRepo;

    /**
     * Instantiates a new Outward service.
     * @param outwardRepository the outward repository
     * @param outwardMapper the outward mapper
     */
    public OutwardServiceImpl(OutwardRepository outwardRepository, OutwardMapper outwardMapper) {
        super(outwardRepository, outwardMapper);
    }

    /**
     * Gets module name for log.
     * @return the module name for log
     */
    @Override
    public String getModuleNameForLog() {
        return "[OutwardServiceImpl]";
    }

    /**
     * Gets all outward by product and staff.
     * @param productId the product id
     * @param staffId the staff id
     * @return the all outward by product and staff
     */
    public List<Outward> getAllOutwardByProductAndStaff(Long productId, Long staffId) {
        try {
            QOutward qOutward = QOutward.outward;
            JPAQuery<Outward> query = new JPAQuery<>(entityManager);
            List<Outward> outwardList = new ArrayList<>();
            BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.productId.id.eq(productId)).and(qOutward.destinationType.equalsIgnoreCase(CommonConstants.STAFF)).and(qOutward.destinationId.eq(staffId).and(qOutward.isDeleted.eq(false)));
            List<Tuple> result = query.select(qOutward.id, qOutward.outwardNumber, qOutward.unusedQty, qOutward.mvnoId).from(qOutward).where(booleanExpression).fetch();
            if (!result.isEmpty()) {
                result.forEach(tuple -> {
                    Outward outward = new Outward();
                    outward.setId(tuple.get(qOutward.id));
                    outward.setOutwardNumber(tuple.get(qOutward.outwardNumber));
                    outward.setUnusedQty(tuple.get(qOutward.unusedQty));
                    outward.setMvnoId(tuple.get(qOutward.mvnoId));
                    outwardList.add(outward);
                });
            }
            if (getMvnoIdFromCurrentStaff() == 1)
                return outwardList;
            else
                return outwardList.stream().filter(outward -> outward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || outward.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets by staff id.
     * @param staffId the staff id
     * @return the by staff id
     */
    public List<Outward> getByStaffId(Long staffId) {
        try {
            QOutward qOutward = QOutward.outward;
            JPAQuery<Outward> query = new JPAQuery<>(entityManager);
            List<Outward> outwardList = new ArrayList<>();
            BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.destinationType.eq(CommonConstants.STAFF)).and(qOutward.destinationId.eq(staffId).and(qOutward.isDeleted.eq(false)));
            List<Tuple> result = query.select(qOutward.id, qOutward.outwardNumber, qOutward.productId.name, qOutward.sourceType, qOutward.sourceId, qOutward.inwardId.inwardNumber, qOutward.outwardDateTime, qOutward.qty, qOutward.usedQty, qOutward.unusedQty, qOutward.productId.productCategory.unit).from(qOutward).where(booleanExpression).fetch();
            if (!result.isEmpty()) {
                result.forEach(tuple -> {
                    Outward outward = new Outward();
                    outward.setId(tuple.get(qOutward.id));
                    outward.setOutwardNumber(tuple.get(qOutward.outwardNumber));
                    outward.setProductName(tuple.get(qOutward.productId.name));
                    outward.setSourceType(tuple.get(qOutward.sourceType));
                    outward.setSourceId(tuple.get(qOutward.sourceId));
                    outward.setInwardNumber(tuple.get(qOutward.inwardId.inwardNumber));
                    outward.setOutwardDateTime(tuple.get(qOutward.outwardDateTime));
                    outward.setQty(tuple.get(qOutward.qty));
                    outward.setUsedQty(tuple.get(qOutward.usedQty));
                    outward.setUnusedQty(tuple.get(qOutward.unusedQty));
                    outward.setUnit(tuple.get(qOutward.productId.productCategory.unit));
                    outwardList.add(outward);
                });
            }
            return outwardList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets assign inventories.
     * @param staffId the staff id
     * @return the assign inventories
     */
    public List<Outward> getAssignInventories(Long staffId) {
        try {
            QOutward qOutward = QOutward.outward;
            JPAQuery<Outward> query = new JPAQuery<>(entityManager);
            List<Outward> outwardList = new ArrayList<>();
            BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.sourceType.eq(CommonConstants.STAFF)).and(qOutward.sourceId.eq(staffId).and(qOutward.isDeleted.eq(false)));
            List<Tuple> result = query.select(qOutward.id, qOutward.outwardNumber, qOutward.productId.name, qOutward.sourceType, qOutward.sourceId, qOutward.inwardId.inwardNumber, qOutward.outwardDateTime, qOutward.qty, qOutward.usedQty, qOutward.unusedQty, qOutward.productId.productCategory.unit).from(qOutward).where(booleanExpression).fetch();
            if (!result.isEmpty()) {
                result.forEach(tuple -> {
                    Outward outward = new Outward();
                    outward.setId(tuple.get(qOutward.id));
                    outward.setOutwardNumber(tuple.get(qOutward.outwardNumber));
                    outward.setProductName(tuple.get(qOutward.productId.name));
                    outward.setSourceType(tuple.get(qOutward.sourceType));
                    outward.setSourceId(tuple.get(qOutward.sourceId));
                    outward.setInwardNumber(tuple.get(qOutward.inwardId.inwardNumber));
                    outward.setOutwardDateTime(tuple.get(qOutward.outwardDateTime));
                    outward.setQty(tuple.get(qOutward.qty));
                    outward.setUsedQty(tuple.get(qOutward.usedQty));
                    outward.setUnusedQty(tuple.get(qOutward.unusedQty));
                    outward.setUnit(tuple.get(qOutward.productId.productCategory.unit));
                    outwardList.add(outward);
                });
            }
            if (getMvnoIdFromCurrentStaff() == 1)
                return outwardList;
            else
                return outwardList.stream().filter(outward -> outward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || outward.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
        QOutward qOutward = QOutward.outward;
        BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
        List<Long> resultPaginationList = new ArrayList<>();
        Page<Outward> finalPaginationList = null;
        String outwardNumber = null;
        //Page<Outward> paginationList = null;
        try {
            if (getMvnoIdFromCurrentStaff() != 1) {
                List<Outward> outwardWarehouseList = getAllWarehouseForOutward(null);
                List<Outward> outwardPopManagementList = getAllPOPForOutward(null);
                List<Outward> outwardStaffList = getAllStaffForOutward(null);
                List<Outward> outwardPartnerList = getAllPartnerStaffForOutward(null);
                List<Outward> outwardServiceAreaStaffList = getAllServiceAreaForOutward(null);
                if (outwardWarehouseList != null) {
                    if (outwardWarehouseList.size() > 0) {
                        for (int w = 0; w < outwardWarehouseList.size(); w++) {
                            resultPaginationList.add(outwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (outwardPopManagementList != null) {
                    if (outwardPopManagementList.size() > 0) {
                        for (int p = 0; p < outwardPopManagementList.size(); p++) {
                            resultPaginationList.add(outwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (outwardStaffList != null) {
                    if (outwardStaffList.size() > 0) {
                        for (int s = 0; s < outwardStaffList.size(); s++) {
                            resultPaginationList.add(outwardStaffList.get(s).getId());
                        }
                    }
                }
                if (outwardPartnerList != null) {
                    if (outwardPartnerList.size() > 0) {
                        for (int p = 0; p < outwardPartnerList.size(); p++) {
                            resultPaginationList.add(outwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (outwardServiceAreaStaffList != null) {
                    if (outwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < outwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(outwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
//                finalPaginationList = outwardRepository.findAllByIdIn(resultPaginationList, pageRequest); // old
                finalPaginationList = outwardRepository.findAllByIdInAndGroupIdIsNull(resultPaginationList, pageRequest);
            }
            if (getMvnoIdFromCurrentStaff() == 1) {
                finalPaginationList = outwardRepository.findAll(booleanExpression, pageRequest);
            }
            if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, finalPaginationList);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }


    /**
     * Gets all outwards.
     * @return the all outwards
     */
    public List<Outward> getAllOutwards() {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        QOutward qOutward = QOutward.outward;
        BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.isDeleted.eq(false));
        List<Long> resultPaginationList = new ArrayList<>();
        List<Outward> outwardList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() != 1) {
                List<Outward> outwardWarehouseList = getAllWarehouseForOutward(null);
                List<Outward> outwardPopManagementList = getAllPOPForOutward(null);
                List<Outward> outwardStaffList = getAllStaffForOutward(null);
                List<Outward> outwardPartnerList = getAllPartnerStaffForOutward(null);
                List<Outward> outwardServiceAreaStaffList = getAllServiceAreaForOutward(null);
                if (outwardWarehouseList != null) {
                    if (outwardWarehouseList.size() > 0) {
                        for (int w = 0; w < outwardWarehouseList.size(); w++) {
                            resultPaginationList.add(outwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (outwardPopManagementList != null) {
                    if (outwardPopManagementList.size() > 0) {
                        for (int p = 0; p < outwardPopManagementList.size(); p++) {
                            resultPaginationList.add(outwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (outwardStaffList != null) {
                    if (outwardStaffList.size() > 0) {
                        for (int s = 0; s < outwardStaffList.size(); s++) {
                            resultPaginationList.add(outwardStaffList.get(s).getId());
                        }
                    }
                }
                if (outwardPartnerList != null) {
                    if (outwardPartnerList.size() > 0) {
                        for (int p = 0; p < outwardPartnerList.size(); p++) {
                            resultPaginationList.add(outwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (outwardServiceAreaStaffList != null) {
                    if (outwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < outwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(outwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
                outwardList = outwardRepository.findAllByIdIn(resultPaginationList);

            }
            if (getMvnoIdFromCurrentStaff() == 1) {
                outwardList = (List<Outward>) outwardRepository.findAll(booleanExpression);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return outwardList;
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
                    if (searchModel.getFilterColumn() != "") {
                        if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Outward Number")) {
                            return getOutwardListBaseOnInwardNumber(searchModel.getFilterValue(), pageRequest);
                        }
                        if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Product Name")) {
                            return getOutwardListbaseOnProductname(searchModel.getFilterValue(), pageRequest);
                        }
                    } else {
                        return getListByPageAndSizeAndSortByAndOrderBy(page, pageSize, sortBy, sortOrder, filterList);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Gets outward list base on inward number.
     * @param outwardNumber the outward number
     * @param pageRequest the page request
     * @return the outward list base on inward number
     */
    private GenericDataDTO getOutwardListBaseOnInwardNumber(String outwardNumber, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getOutwardListBaseOnInwardNumber()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Outward> finalPaginationList = null;
        try {
            List<Outward> outwardList = getAllOutwards();
            if (outwardNumber != null)
                outwardList = outwardList.stream().filter(outward -> outward.getOutwardNumber().toLowerCase().contains(outwardNumber.toLowerCase())).collect(Collectors.toList());

            Pageable pageable = pageRequest;

            List<Outward> paginatedList = outwardList.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).collect(Collectors.toList());
            long totalCount = outwardList.size();
            finalPaginationList = new PageImpl<>(paginatedList, pageable, totalCount);
            if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, finalPaginationList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Gets outward list.
     * @param outwardNumber the outward number
     * @param pageRequest the page request
     * @return the outward list
     */
    public GenericDataDTO getOutwardList(String outwardNumber, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getOutwardList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Outward> finalPaginationList = null;
        try {
            if (getMvnoIdFromCurrentStaff() != 1) {
                List<Long> resultPaginationList = new ArrayList<>();
                List<Outward> outwardWarehouseList = getAllWarehouseForOutward(outwardNumber);
                List<Outward> outwardPopManagementList = getAllPOPForOutward(outwardNumber);
                List<Outward> outwardStaffList = getAllStaffForOutward(outwardNumber);
                List<Outward> outwardPartnerList = getAllPartnerStaffForOutward(outwardNumber);
                List<Outward> outwardServiceAreaStaffList = getAllServiceAreaForOutward(outwardNumber);
                if (outwardWarehouseList != null) {
                    if (outwardWarehouseList.size() > 0) {
                        for (int w = 0; w < outwardWarehouseList.size(); w++) {
                            resultPaginationList.add(outwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (outwardPopManagementList != null) {
                    if (outwardPopManagementList.size() > 0) {
                        for (int p = 0; p < outwardPopManagementList.size(); p++) {
                            resultPaginationList.add(outwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (outwardStaffList != null) {
                    if (outwardStaffList.size() > 0) {
                        for (int s = 0; s < outwardStaffList.size(); s++) {
                            resultPaginationList.add(outwardStaffList.get(s).getId());
                        }
                    }
                }
                if (outwardPartnerList != null) {
                    if (outwardPartnerList.size() > 0) {
                        for (int p = 0; p < outwardPartnerList.size(); p++) {
                            resultPaginationList.add(outwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (outwardServiceAreaStaffList != null) {
                    if (outwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < outwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(outwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
                finalPaginationList = outwardRepository.findAllByIdIn(resultPaginationList, pageRequest);
            }
            if (getMvnoIdFromCurrentStaff() == 1) {
                finalPaginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndIsDeletedIsFalse(outwardNumber, pageRequest);
            }
            if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, finalPaginationList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Gets outward listbase on productname.
     * @param productName the product name
     * @param pageRequest the page request
     * @return the outward listbase on productname
     */
    public GenericDataDTO getOutwardListbaseOnProductname(String productName, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getOutwardList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Outward> finalPaginationList = null;
        try {
            List<Outward> outwardList = getAllOutwards();

            // Filter the outwardList based on the productName
            if (productName != null) {
                outwardList = outwardList.stream()
                        .filter(outward -> outward.getProductId().getName().toLowerCase().contains(productName.toLowerCase()))
                        .collect(Collectors.toList());
            }

            // Create a Pageable object based on the provided pageRequest
            Pageable pageable = pageRequest;

            // Apply pagination and sorting to the filtered data
            List<Outward> paginatedList = outwardList.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());

            // Count the total number of matching records
            long totalCount = outwardList.size();

            // Create a Page object containing the paginated list and the total count
            finalPaginationList = new PageImpl<>(paginatedList, pageable, totalCount);

            if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, finalPaginationList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Search assign inventories generic data dto.
     * @param filterList the filter list
     * @param page the page
     * @param pageSize the page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param staffId the staff id
     * @return the generic data dto
     */
    public GenericDataDTO searchAssignInventories(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, Long staffId) {
        String SUBMODULE = getModuleNameForLog() + " [searchAssignInventories()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Pageable pageable = generatePageRequest(page, pageSize, "id", sortOrder);
        try {
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpression = qOutward.isDeleted.eq(false);
                        if (!searchModel.getFilterValue().isEmpty()) {
                            String searchKey = searchModel.getFilterValue();
                            List<Product> product = productRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(searchKey);
                            booleanExpression = booleanExpression.and(qOutward.destinationId.eq(staffId))
                                    .and(qOutward.destinationType.equalsIgnoreCase(CommonConstants.STAFF));
                            if (product != null && product.size() > 0) {
                                booleanExpression = booleanExpression.and(qOutward.productId.id.in(product.stream().map(product1 -> product1.getId()).collect(Collectors.toList())));
                            }
                        }
                        if (getMvnoIdFromCurrentStaff() != 1)
                            booleanExpression = booleanExpression.and(qOutward.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                        Page<Outward> assignInventoriesList = outwardRepository.findAll(booleanExpression, pageable);
                        if (null != assignInventoriesList && 0 < assignInventoriesList.getSize()) {
                            makeGenericResponse(genericDataDTO, assignInventoriesList);
                        }
                        return genericDataDTO;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
        return null;
    }

   /* public GenericDataDTO getAssignInventories(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long staffId) {
        String SUBMODULE = getModuleNameForLog() + " [getAssignInventories()] ";
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.destinationType.equalsIgnoreCase(CommonConstants.STAFF)).and(qInward.destinationId.eq(staffId).and(qInward.isDeleted.eq(false)));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Inward> paginationList = null;
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qInward.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            paginationList = inwardRepository.findAll(booleanExpression, pageRequest);
            if (paginationList.getSize() > 0) {
                genericDataDTO = inwardService.makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }*/

    /**
     * Save entity outward dto.
     * @param entity the entity
     * @param isReturned the is returned
     * @return the outward dto
     * @throws Exception the exception
     */
    @Transactional
    public OutwardDto saveEntity(OutwardDto entity, Boolean isReturned) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        try {
            if (entity.getSourceType().equalsIgnoreCase(CommonConstants.WAREHOUSE) && entity.getDestinationType().equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
                if (warehouseManagementRepository.findById(entity.getSourceId()).get().getWarehouseType().equalsIgnoreCase("3PL")
                        && warehouseManagementRepository.findById(entity.getDestinationId()).get().getWarehouseType().equalsIgnoreCase("3PL")) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "3rd party to 3rd party warehouse transefer not allowed.", null);
                }
            }
            Long unused = entity.getUnusedQty();
            Long inTransit = entity.getInTransitQty();
            if (unused != null && inTransit != null) {
                if (unused < inTransit) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Fulfilment Qty cannot be greater than Available Qty", null);
                }
            }
            entity.setQty(0L);
            entity.setUnusedQty(0L);
            entity.setInTransitQty(entity.getInTransitQty());
            entity.setUsedQty(0L);
            entity.setOutTransitQty(0L);
            entity.setRejectedQty(0L);
            entity.setType(null);
            entity.setSelectedItems(0L);
            if (!isReturned)
                entity.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
            entity.setApprovalStatus(CommonConstants.PENDING);
            entity.setOutwardNumber(getRandomenumber("OUT", "-", "", getMvnoIdFromCurrentStaff()));
            if (!isReturned && !inventoryTransferValidation(entity.getSourceType(), entity.getDestinationType()))
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.SOURCE_DESTINATION_MISMATCH, null);
            OutwardDto outwardDto = super.saveEntity(entity);
            InwardDto inward = inwardService.saveInwardOfOutwardEntity(convertOutwardDtoToInwardDto(outwardDto, isReturned), true, true);
            outwardDto.setOutwardsInwardId(inward.getId());

            // Managing quantities in product owner
            ProductOwnerDto destination = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
            if (destination != null) {
                destination.setQuantity(destination.getQuantity());
                destination.setUnusedQty(destination.getUnusedQty());
                destination.setUsedQty(destination.getUsedQty());
                destination.setInTransitQty(destination.getInTransitQty() + entity.getInTransitQty());
                destination.setProductId(entity.getProductId().getId());
                destination.setOwnerId(entity.getDestinationId());
                destination.setOwnerType(entity.getDestinationType());
                productOwnerService.updateEntity(destination);
            } else {
                ProductOwnerDto productOwnerDto = new ProductOwnerDto();
                productOwnerDto.setQuantity(entity.getQty());
                productOwnerDto.setUnusedQty(entity.getQty());
                productOwnerDto.setUsedQty(entity.getUsedQty());
                productOwnerDto.setInTransitQty(entity.getInTransitQty());
                productOwnerDto.setProductId(entity.getProductId().getId());
                productOwnerDto.setOwnerId(entity.getDestinationId());
                productOwnerDto.setOwnerType(entity.getDestinationType());
                productOwnerService.saveEntity(productOwnerDto);
            }
            ProductOwnerDto source = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getSourceId(), entity.getSourceType());
            if (!isReturned) {
                if (source != null) {
                    source.setQuantity(source.getQuantity());
                    source.setUnusedQty(source.getUnusedQty() - entity.getInTransitQty());
                    source.setUsedQty(source.getUsedQty() + entity.getInTransitQty());
                    source.setInTransitQty(source.getInTransitQty());
                    source.setProductId(entity.getProductId().getId());
                    source.setOwnerId(entity.getSourceId());
                    source.setOwnerType(entity.getSourceType());
                    productOwnerService.updateEntity(source);
                } else {
                    ProductOwnerDto productOwnerDto = new ProductOwnerDto();
                    productOwnerDto.setQuantity(entity.getQty());
                    productOwnerDto.setUnusedQty(entity.getQty());
                    productOwnerDto.setUsedQty(entity.getUsedQty());
                    productOwnerDto.setInTransitQty(entity.getInTransitQty());
                    productOwnerDto.setProductId(entity.getProductId().getId());
                    productOwnerDto.setOwnerId(entity.getSourceId());
                    productOwnerDto.setOwnerType(entity.getSourceType());
                    productOwnerService.saveEntity(productOwnerDto);
                }
            }
            //To add RequestInvetory Status

            if (outwardDto.getRequestInventoryProductId() != null) {
                RequestInvenotryProductMapping requestInvenotryProductMapping = requestInventoryProductMappingRepo.findById(outwardDto.getRequestInventoryProductId()).orElse(null);
                if (requestInvenotryProductMapping != null) {
                    if (outwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.APPROVE)) {
                        requestInvenotryProductMapping.setRequestStatus("Close");
                    } else if (outwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.PENDING)) {
                        requestInvenotryProductMapping.setRequestStatus("Open");
                    } else {
                        requestInvenotryProductMapping.setRequestStatus("Reject");
                    }
                }
            }
            if (entity.getRequestInventoryId() != null) {
                RequestInvenotryProductMapping requestInvenotryProductMapping = requestInventoryProductMappingRepo.findById(entity.getRequestInventoryProductId()).orElse(null);
                if (requestInvenotryProductMapping != null) {
                    requestInvenotryProductMapping.setRequestStatus(CommonConstants.REQUEST_INVENTORY_PRODUCT_MAPPING.PARTIAL_OPEN);
                    requestInventoryProductMappingRepo.save(requestInvenotryProductMapping);
                }
            }
            return outwardDto;
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
        }
    }

  //does bulk save but does not use outwardId as groupId and does single save per record
    @Transactional
    public List<OutwardDto> saveBulk(List<OutwardDto> entityDTOList) throws Exception {
        if (entityDTOList == null || entityDTOList.isEmpty()) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),
                    "Outward list cannot be empty", null);
        }

        List<OutwardDto> savedList = new ArrayList<>();

        String defaultTimezone = TimeZone.getDefault().getID();
        TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
        Integer second = tz.getOffset(new Date().getTime()) / 1000;

        for (OutwardDto entityDTO : entityDTOList) {
            if (entityDTO == null) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),
                        "Outward item cannot be null", null);
            }

            if (entityDTO.getOutwardDateTime() != null) {
                LocalDateTime localDateTime = entityDTO.getOutwardDateTime().plusSeconds(second);
                entityDTO.setOutwardDateTime(localDateTime);
            }

            OutwardDto saved = saveEntity(entityDTO, false);
            savedList.add(saved);
        }

        return savedList;
    }


    @Transactional
    public List<OutwardDto> saveBulkEntity(
            List<OutwardDto> list,
            Boolean isReturned
    ) throws Exception {

        try {

            if (list == null || list.isEmpty()) {
                throw new IllegalArgumentException("Outward list cannot be empty");
            }

            // Validation...
            OutwardDto firstInput = list.get(0);
            for (OutwardDto e : list) {
                // ... validations ...
            }

            List<OutwardDto> result = new ArrayList<>();
            Long outwardGroupId = null;
            Long inwardGroupId = null;

            for (int i = 0; i < list.size(); i++) {

                OutwardDto dto = list.get(i);
                Long originalInTransitQty = dto.getInTransitQty();

                // Reset fields
                dto.setQty(0L);
                dto.setUnusedQty(0L);
                dto.setUsedQty(0L);
                dto.setOutTransitQty(0L);
                dto.setRejectedQty(0L);
                dto.setType(null);
                dto.setSelectedItems(0L);
                dto.setInTransitQty(originalInTransitQty);

                if (!isReturned) {
                    dto.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
                }

                dto.setApprovalStatus(CommonConstants.PENDING);
                dto.setOutwardNumber(getRandomenumber("OUT", "-", "", getMvnoIdFromCurrentStaff()));

                // Group logic
                if (i == 0) {
                    dto.setGroup(list.size() > 1);
                    dto.setGroupId(null);
                } else {
                    dto.setGroup(false);
                    dto.setGroupId(outwardGroupId);
                }

                // Save outward
                OutwardDto savedOutward = super.saveEntity(dto);

                // Create inward
                InwardDto inwardDto = convertOutwardDtoToInwardDto(savedOutward, isReturned);

                if (i == 0) {
                    inwardDto.setIsGroup(list.size() > 1);
                    inwardDto.setGroupId(null);
                } else {
                    inwardDto.setIsGroup(false);
                    inwardDto.setGroupId(inwardGroupId);
                }

                // Save inward - THIS WILL SET THE BIDIRECTIONAL RELATIONSHIP
                InwardDto savedInward = inwardService.saveInwardOfOutwardEntity(
                        inwardDto,
                        true,
                        true
                );

                //  JUST SET ON DTO - Database relationship is handled by Inward entity
                savedOutward.setOutwardsInwardId(savedInward.getId());

                // Store group IDs
                if (i == 0) {
                    outwardGroupId = savedOutward.getId();
                    inwardGroupId = savedInward.getId();
                }

                // Request inventory handling
                handleRequestInventory(savedOutward, dto);

                result.add(savedOutward);
            }

            // Product Owner Update (Aggregated)
            Map<Long, Long> productQtyMap = list.stream()
                    .collect(Collectors.groupingBy(
                            e -> e.getProductId().getId(),
                            Collectors.summingLong(OutwardDto::getInTransitQty)
                    ));

            for (Map.Entry<Long, Long> entry : productQtyMap.entrySet()) {
                Long productId = entry.getKey();
                Long totalQty = entry.getValue();

                // Destination update
                ProductOwnerDto destination = productOwnerService
                        .findByProductIdOwnerIdAndOwnerType(
                                productId,
                                firstInput.getDestinationId(),
                                firstInput.getDestinationType()
                        );

                if (destination != null) {
                    destination.setInTransitQty(destination.getInTransitQty() + totalQty);
                    productOwnerService.updateEntity(destination);
                } else {
                    ProductOwnerDto dto = new ProductOwnerDto();
                    dto.setQuantity(0L);
                    dto.setUnusedQty(0L);
                    dto.setUsedQty(0L);
                    dto.setInTransitQty(totalQty);
                    dto.setProductId(productId);
                    dto.setOwnerId(firstInput.getDestinationId());
                    dto.setOwnerType(firstInput.getDestinationType());
                    productOwnerService.saveEntity(dto);
                }

                // Source update (if not returned)
                if (!isReturned) {
                    ProductOwnerDto source = productOwnerService
                            .findByProductIdOwnerIdAndOwnerType(
                                    productId,
                                    firstInput.getSourceId(),
                                    firstInput.getSourceType()
                            );

                    if (source != null) {
                        source.setUnusedQty(source.getUnusedQty() - totalQty);
                        source.setUsedQty(source.getUsedQty() + totalQty);
                        productOwnerService.updateEntity(source);
                    } else {
                        ProductOwnerDto dto = new ProductOwnerDto();
                        dto.setQuantity(0L);
                        dto.setUnusedQty(0L);
                        dto.setUsedQty(totalQty);
                        dto.setInTransitQty(0L);
                        dto.setProductId(productId);
                        dto.setOwnerId(firstInput.getSourceId());
                        dto.setOwnerType(firstInput.getSourceType());
                        productOwnerService.saveEntity(dto);
                    }
                }
            }

            return result;

        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        }
    }
    private void handleRequestInventory(
            OutwardDto savedOutward,
            OutwardDto originalEntity
    ) {

        // =====================================================
        // REQUEST INVENTORY PRODUCT STATUS
        // =====================================================

        if (savedOutward.getRequestInventoryProductId() != null) {

            RequestInvenotryProductMapping mapping =
                    requestInventoryProductMappingRepo
                            .findById(
                                    savedOutward.getRequestInventoryProductId()
                            )
                            .orElse(null);

            if (mapping != null) {

                if (savedOutward.getApprovalStatus()
                        .equalsIgnoreCase(CommonConstants.APPROVE)) {

                    mapping.setRequestStatus("Close");

                } else if (savedOutward.getApprovalStatus()
                        .equalsIgnoreCase(CommonConstants.PENDING)) {

                    mapping.setRequestStatus("Open");

                } else {

                    mapping.setRequestStatus("Reject");
                }

                requestInventoryProductMappingRepo
                        .save(mapping);
            }
        }

        // =====================================================
        // PARTIAL OPEN HANDLING
        // =====================================================

        if (originalEntity.getRequestInventoryId() != null
                && originalEntity.getRequestInventoryProductId() != null) {

            RequestInvenotryProductMapping mapping =
                    requestInventoryProductMappingRepo
                            .findById(
                                    originalEntity.getRequestInventoryProductId()
                            )
                            .orElse(null);

            if (mapping != null) {

                mapping.setRequestStatus(
                        CommonConstants
                                .REQUEST_INVENTORY_PRODUCT_MAPPING
                                .PARTIAL_OPEN
                );

                requestInventoryProductMappingRepo
                        .save(mapping);
            }
        }
    }


    public List<OutwardDto> getOutwardGroup(Long id) {

        Outward base = outwardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outward not found"));

        Long parentId = (base.getGroupId() == null) ? base.getId() : base.getGroupId();

        List<Outward> list = outwardRepository.findParentWithChildren(parentId);

        return list.stream()
                .map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }
    /**
     * Convert outward dto to inward dto inward dto.
     * @param outwardDto the outward dto
     * @param isReturned the is returned
     * @return the inward dto
     */
    private InwardDto convertOutwardDtoToInwardDto(OutwardDto outwardDto, Boolean isReturned) {
        try {
            InwardDto inwardDto = new InwardDto();
            inwardDto.setInwardNumber(CommonUtils.getResponse("", "", null, 5));
            //inwardDto.setQty(outwardDto.getQty());
            inwardDto.setQty(outwardDto.getInTransitQty());
            if (isReturned)
                inwardDto.setTotalMacSerial(outwardDto.getInTransitQty());
            inwardDto.setUsedQty(0L);
            inwardDto.setUnusedQty(outwardDto.getUsedQty());
            //inwardDto.setInTransitQty(outwardDto.getInTransitQty());
            inwardDto.setInTransitQty(outwardDto.getInTransitQty());
            inwardDto.setInwardDateTime(outwardDto.getOutwardDateTime());
            inwardDto.setDestinationType(outwardDto.getDestinationType());
            inwardDto.setDestinationId(outwardDto.getDestinationId());
            inwardDto.setSourceType(outwardDto.getSourceType());
            inwardDto.setSourceId(outwardDto.getSourceId());
            inwardDto.setIsDeleted(outwardDto.getIsDeleted());
            inwardDto.setMvnoId(outwardDto.getMvnoId());
            inwardDto.setType(null);
            inwardDto.setStatus(outwardDto.getStatus());
            inwardDto.setProductId(outwardDto.getProductId());
            inwardDto.setServiceAreaId(outwardDto.getServiceAreaId());
            inwardDto.setApprovalStatus(CommonConstants.PENDING);
            inwardDto.setType(outwardDto.getType());
            inwardDto.setIsGroup(outwardDto.isGroup());
            inwardDto.setGroupId(outwardDto.getGroupId());
            if (outwardDto.getRequestInventoryId() != null) {
                inwardDto.setRequestInventoryId(outwardDto.getRequestInventoryId());
            }
            if (!isReturned)
                inwardDto.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
            else
                inwardDto.setCategoryType(CommonConstants.RETURNED_INWARD_TYPE);
            inwardDto.setOutwardId(outwardMapper.dtoToDomain(outwardDto, new CycleAvoidingMappingContext()));
            return inwardDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Check has mac and has serial boolean.
     * @param inwardId the inward id
     * @return the boolean
     * @throws Exception the exception
     */
    public boolean checkHasMacAndHasSerial(Long inwardId) throws Exception {
        try {
            boolean flag = false;
            QInward qInward = QInward.inward;
            BooleanExpression booleanExpression = QInward.inward.isNotNull().and(qInward.isDeleted.eq(false));
            booleanExpression = booleanExpression.and(qInward.id.eq(inwardId));
            Inward inward = inwardRepository.findOne(booleanExpression).get();
            boolean hasMac = inward.getProductId().getProductCategory().isHasMac();
            boolean hasSerial = inward.getProductId().getProductCategory().isHasSerial();
            Inward inward1 = inwardRepository.findById(inwardId).get();
            Integer count = null;
            if (hasMac == true && hasSerial == true) {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    if (inward1.getOutwardId() == null) {
                        count = inOutWardMacRepo.countInward(Math.toIntExact(inwardId));
                    } else {
                        if (inward1 != null) {
                            Integer countInwardId = Math.toIntExact(inward1.getId());
                            if (countInwardId != null) {
                                count = inOutWardMacRepo.countInwardIdOfOutward(Math.toIntExact(countInwardId));
                            }
                        }
                    }
                } else {
                    if (inward1.getOutwardId() == null) {
                        count = inOutWardMacRepo.countInward(Math.toIntExact(inwardId), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        if (inward1 != null) {
                            Integer countInwardId = Math.toIntExact(inward1.getId());
                            if (countInwardId != null) {
                                count = inOutWardMacRepo.countInwardIdOfOutward(Math.toIntExact(inwardId), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                            }
                        }
                    }
                }
                if (count != 0) {
                    flag = true;
                } else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please enter has mac and has serial", null);
                }
            } else if (hasSerial == true) {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    if (inward1.getOutwardId() == null) {
                        count = inOutWardMacRepo.countInward(Math.toIntExact(inwardId));
                    } else {
                        if (inward1 != null) {
                            Integer countInwardId = Math.toIntExact(inward1.getId());
                            if (countInwardId != null) {
                                count = inOutWardMacRepo.countInwardIdOfOutward(Math.toIntExact(countInwardId));
                            }
                        }
                    }
                } else {
                    if (inward1.getOutwardId() == null) {
                        count = inOutWardMacRepo.countInward(Math.toIntExact(inwardId), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        if (inward1 != null) {
                            Integer countInwardId = Math.toIntExact(inward1.getId());
                            if (countInwardId != null) {
                                count = inOutWardMacRepo.countInwardIdOfOutward(Math.toIntExact(inwardId), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                            }
                        }
                    }
                }
                if (count != 0) {
                    flag = true;
                } else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please enter has serial", null);
                }
            } else if (hasMac == false && hasSerial == false) {
                flag = true;
            }
            return flag;
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Inventory transfer validation boolean.
     * @param source the source
     * @param destination the destination
     * @return the boolean
     */
    private Boolean inventoryTransferValidation(String source, String destination) {
        try {
            if (source.equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
                if (destination.equalsIgnoreCase(CommonConstants.WAREHOUSE) || destination.equalsIgnoreCase(CommonConstants.PARTNER) || destination.equalsIgnoreCase(CommonConstants.STAFF))
                    return true;
                else return false;
            } else if (source.equalsIgnoreCase(CommonConstants.PARTNER)) {
                if (destination.equalsIgnoreCase(CommonConstants.PARTNER))
                    return true;
                else return false;
            } else if (source.equalsIgnoreCase(CommonConstants.STAFF)) {
                if (destination.equalsIgnoreCase(CommonConstants.WAREHOUSE))
                    return true;
                else return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
        Integer count = outwardRepository.deleteVerify(id);
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * Gets all warehouse for outward.
     * @param outwardNumber the outward number
     * @return the all warehouse for outward
     */
//Get All Inward based on Warehouse
    public List<Outward> getAllWarehouseForOutward(String outwardNumber) {
        try {
            List<Outward> paginationList = null;
            List<WareHouse> wareHouseList = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                wareHouseList = warehouseManagementRepository.findAllByIsDeletedIsFalseWithoutPageable();
            } else {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
                if (!serviceAreaIds.isEmpty()) {
                    List<Long> warehouseIds = wareHouseManagmentServiceAreamappingRepo.findAllByServiceIdIn(serviceAreaIds).stream()
                            .map(WareHouseServiceAreaMapping::getWarehouseId)
                            .collect(Collectors.toList());
                    wareHouseList = warehouseManagementRepository.findAllByIdInAndIsDeletedIsFalseAndMvnoIdInWithoutPageable(warehouseIds, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    wareHouseList = warehouseManagementRepository.findAllByIsDeletedIsFalseAndMvnoIdInWithoutPageable(Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
            if (!wareHouseList.isEmpty()) {
                String warehouseDestinationType = "Warehouse";
                List<Long> warehouseResult = new ArrayList<>();
                for (WareHouse wareHouse : wareHouseList) {
                    Long warehouseDestinationId = wareHouse.getId();
                    warehouseResult.add(warehouseDestinationId);
                }
                if (getMvnoIdFromCurrentStaff() != 1) {
                    if (outwardNumber != null) {
                        paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdInWithConstructor(outwardNumber, warehouseResult, warehouseDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdInWithConstructor(warehouseResult, warehouseDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }
                } else {
                    if (outwardNumber != null) {
                        paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseWithConstructor(outwardNumber, warehouseResult, warehouseDestinationType);
                    } else {
                        paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseWithConstructor(warehouseResult, warehouseDestinationType);
                    }
                }
            }
            return paginationList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all pop for outward.
     * @param outwardNumber the outward number
     * @return the all pop for outward
     */
//Get All Inward based on POP
    public List<Outward> getAllPOPForOutward(String outwardNumber) {
        try {
            List<Outward> paginationList = null;
            List<PopManagement> popManagementList = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                popManagementList = popManagementRepository.findAllLightPopManagementByIsDeletedIsFalse();
            } else {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
                if (!serviceAreaIds.isEmpty()) {
                    List<Long> ids = popServiceAreaMappingRepo.findAllByServiceAreaIdIn(serviceAreaIds).stream()
                            .map(PopServiceAreaMapping::getPopId)
                            .collect(Collectors.toList());
                    popManagementList = popManagementRepository.findAllLightPopManagementByIsDeletedIsFalseAndMvnoIdInAndIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), ids);
                } else {
                    popManagementList = popManagementRepository.findAllLightPopManagementByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
            if (!popManagementList.isEmpty()) {
                String popDestinationType = "POP";
                List<Long> popResult = new ArrayList<>();
                for (PopManagement popManagement : popManagementList) {
                    Long popDestinationId = popManagement.getId();
                    popResult.add(popDestinationId);
                }
                if (getMvnoIdFromCurrentStaff() != 1) {
                    if (outwardNumber != null) {
                        paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdInWithConstructor(outwardNumber, popResult, popDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdInWithConstructor(popResult, popDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }
                } else {
                    if (outwardNumber != null) {
                        paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseWithConstructor(outwardNumber, popResult, popDestinationType);
                    } else {
                        paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseWithConstructor(popResult, popDestinationType);
                    }
                }
            }
            return paginationList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all staff for outward.
     * @param outwardNumber the outward number
     * @return the all staff for outward
     */
    public List<Outward> getAllStaffForOutward(String outwardNumber) {
//        String status = "ACTIVE";
        try {
            List<Long> resultStaffId = new ArrayList<>();
            List<Outward> paginationList = null;
            if (getMvnoIdFromCurrentStaff() != 1) {
                List<StaffUser> staffUserList = staffUserRepository.findByIdAndIsDeleteIsFalseAndMvnoIdIn(getLoggedInUserId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                for (StaffUser staffUser : staffUserList) {
                    Integer staffIds = staffUser.getId();
                    resultStaffId.add(Long.valueOf(staffIds));
                }
            } else {
                List<StaffUser> staffUserList = staffUserRepository.findAllByIdAndIsDeleteIsFalse(getLoggedInUserId());
                for (StaffUser staffUser : staffUserList) {
                    Integer staffIds = staffUser.getId();
                    resultStaffId.add(Long.valueOf(staffIds));
                }
            }
            if (!resultStaffId.isEmpty()) {
                String staffDestinationType = "Staff";
                if (getMvnoIdFromCurrentStaff() != 1) {
                    if (outwardNumber != null) {
    //                    paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(outwardNumber, resultStaffId, staffDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                        paginationList = outwardRepository.findAllByIsDeletedIsFalseAndCreatedByIdAndMvnoIdInAndOutwardNumberWithConstructor(getLoggedInUserId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1), outwardNumber);
                    } else {
    //                    paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(resultStaffId, staffDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                        paginationList = outwardRepository.findAllByIsDeletedIsFalseAndCreatedByIdAndMvnoIdInWithConstructor(getLoggedInUserId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }
                } else {
                    if (outwardNumber != null) {
    //                    paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(outwardNumber, resultStaffId, staffDestinationType);
                        paginationList = outwardRepository.findAllByIsDeletedIsFalseAndCreatedByIdAndOutwardNumberWithConstructor(getLoggedInUserId(), outwardNumber);
                    } else {
    //                    paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(resultStaffId, staffDestinationType);
                        paginationList = outwardRepository.findAllByIsDeletedIsFalseAndCreatedByIdWithConstructor(getLoggedInUserId());
                    }
                }
            }
            return paginationList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all partner staff for outward.
     * @param outwardNumber the outward number
     * @return the all partner staff for outward
     */
    public List<Outward> getAllPartnerStaffForOutward(String outwardNumber) {
        try {
            String status = "ACTIVE";
            List<Long> resultStaffId = new ArrayList<>();
            List<Outward> paginationList = null;
            List<PartnerPojo> partners = partnerService.getAllActiveEntities();
            if (!partners.isEmpty()) {
                for (int i = 0; i < partners.size(); i++) {
                    Integer staffIds = partners.get(i).getId();
                    resultStaffId.add(Long.valueOf(staffIds));
                }
            }
            if (!resultStaffId.isEmpty()) {
                String partnerDestinationType = "Partner";
                if (getMvnoIdFromCurrentStaff() != 1) {
                    if (outwardNumber != null) {
                        paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdInWithConstructor(outwardNumber, resultStaffId, partnerDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdInWithConstructor(resultStaffId, partnerDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }
                } else {
                    if (outwardNumber != null) {
                        paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseWithConstructor(outwardNumber, resultStaffId, partnerDestinationType);
                    } else {
                        paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseWithConstructor(resultStaffId, partnerDestinationType);
                    }
                }
            }
            return paginationList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all service area for outward.
     * @param outwardNumber the outward number
     * @return the all service area for outward
     */
    public List<Outward> getAllServiceAreaForOutward(String outwardNumber) {
        try {
            List<Outward> paginationList = null;
            List<StaffUser> staffUserList = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                staffUserList = staffUserRepository.findAllByIsDeleteIsFalseWithSpecificParameter();
            } else {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
                if (!serviceAreaIds.isEmpty()) {
                    List<Integer> ids = staffUserServiceAreaMappingRepository.findAllByServiceIdIn(serviceAreaIds).stream()
                            .map(StaffUserServiceAreaMapping::getStaffId)
                            .collect(Collectors.toList());
                    staffUserList = staffUserRepository.findAllByIsDeleteIsFalseAndMvnoIdInAndIdInWithSpecificParameter(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), ids);
                } else {
                    staffUserList = staffUserRepository.findAllByIsDeleteIsFalseAndMvnoIdInWithSpecificParameter(Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
            if (!staffUserList.isEmpty()) {
                String serviceAreaDestinationType = "ServiceArea";
                List<Long> serviceAreaStaffResult = new ArrayList<>();
                for (StaffUser staffUser : staffUserList) {
                    Long serviceAreaDestinationId = Long.valueOf(staffUser.getId());
                    serviceAreaStaffResult.add(serviceAreaDestinationId);
                }
                if (getMvnoIdFromCurrentStaff() != 1) {
                    if (outwardNumber != null) {
                        paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdInWithConstructor(outwardNumber, serviceAreaStaffResult, serviceAreaDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdInWithConstructor(serviceAreaStaffResult, serviceAreaDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }
                } else {
                    if (outwardNumber != null) {
                        paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseWithConstructor(outwardNumber, serviceAreaStaffResult, serviceAreaDestinationType);
                    } else {
                        paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseWithConstructor(serviceAreaStaffResult, serviceAreaDestinationType);
                    }
                }
            }
            return paginationList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets randomenumber.
     * @param flag1 the flag 1
     * @param flag2 the flag 2
     * @param flag3 the flag 3
     * @param mvnoId the mvno id
     * @return the randomenumber
     */
    public String getRandomenumber(String flag1, String flag2, String flag3, Integer mvnoId) {
        String flag = "";
        if (flag1 != null)
            flag += flag1;
        if (flag2 != null)
            flag += flag2;
        if (flag3 != null) {
            Integer count = outwardRepository.findTopByOrderByIdDesc(mvnoId);
            if (count == null || count == 0)
                flag += 1;
            else
                flag += count + 1;
        }
        return flag;
    }

    /**
     * Gets entity by id.
     * @param id the id
     * @return the entity by id
     */
    @Override
    public OutwardDto getEntityById(Long id) {

        try {
            Outward outward = outwardRepository.findById(id).get();
            OutwardDto outwardDto = getMapper().domainToDTO(outward, new CycleAvoidingMappingContext());
            if (outwardDto.getDestinationType().equals("") || (outwardDto.getDestinationType() == null)) {
                outwardDto.setDestination("");
            } else if (outwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
                outwardDto.setDestination(warehouseManagementRepository.findLightWarehouseById(outwardDto.getDestinationId()).getName());
            } else if (outwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.STAFF)) {
                outwardDto.setDestination(staffUserRepository.findLightStaffUserById(Math.toIntExact(outwardDto.getDestinationId())).map(staffUser -> staffUser.getFirstname() + " " + staffUser.getLastname()).orElse(null));
            } else if (outwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.PARTNER)) {
                outwardDto.setDestination(partnerRepository.findAllLightPartnerById(Math.toIntExact(outwardDto.getDestinationId())).getName());
            }


            if ((outwardDto.getSourceType() == null) || (outwardDto.getSourceType().equals(""))) {
                outwardDto.setSource("");
            } else if (outwardDto.getSourceType().equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
                outwardDto.setSource(warehouseManagementRepository.findLightWarehouseById(outwardDto.getSourceId()).getName());
            } else if (outwardDto.getSourceType().equalsIgnoreCase(CommonConstants.STAFF)) {
                outwardDto.setSource(staffUserRepository.findLightStaffUserById(Math.toIntExact(outwardDto.getSourceId())).map(staffUser -> staffUser.getFirstname() + " " + staffUser.getLastname()).orElse(null));
            } else if (outwardDto.getSourceType().equalsIgnoreCase(CommonConstants.PARTNER)) {
                outwardDto.setSource(partnerRepository.findAllLightPartnerById(Math.toIntExact(outwardDto.getSourceId())).getName());
            }
            return outwardDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets outward by id.
     * @param id the id
     * @return the outward by id
     */
    public Outward getOutwardById(long id) {
        return outwardRepository.findById(id).get();
    }

    /**
     * Send inventory fulfilment kafka message.
     * @param entityDTO the entity dto
     */
    public void sendInventoryFulfilmentKafkaMessage(OutwardDto entityDTO) {
        try {
            List<TeamUserMapping> teamUserMappings = new ArrayList<>();
            List<String> staffEmails = new ArrayList<>();
            String destinationName;
            Optional<WareHouse> wareHouseOptional = warehouseManagementRepository.findById(entityDTO.getSourceId());
            if (entityDTO.getDestinationType().equalsIgnoreCase("Warehouse")) {
                Optional<WareHouse> wareHouseDestination = warehouseManagementRepository.findById(entityDTO.getDestinationId());
                destinationName = wareHouseDestination.get().getName();
                List<Teams> teamsList = wareHouseDestination.get().getTeamsIdsList();
                for (Teams teams : teamsList) {
                    List<TeamUserMapping> teamsMapping = teamUserMappingsRepocitory.findAllByTeamId(teams.getId());
                    teamUserMappings.addAll(teamsMapping);
                }
                if (!teamUserMappings.isEmpty()) {
                    for (TeamUserMapping teamUserMapping : teamUserMappings) {
                        Optional<StaffUser> staffUser = staffUserRepository.findById(teamUserMapping.getStaffId().intValue());
                        if (staffUser.isPresent()) {
                            staffEmails.add(staffUser.get().getEmail());
                        }
                    }
                }
            } else {
                Optional<StaffUser> staffUser = staffUserRepository.findById(entityDTO.getDestinationId().intValue());
                destinationName = staffUser.get().getUsername();
                staffEmails.add(staffUser.get().getEmail());
            }
            Optional<RequestInvenotryProductMapping> requestInvenotryProductMapping = requestInventoryProductMappingRepo.findById(entityDTO.getRequestInventoryProductId());
            Optional<Product> productOptional = null;
            if (requestInvenotryProductMapping.isPresent()) {
                productOptional = productRepository.findById(requestInvenotryProductMapping.get().getProductId());
            }
            Optional<Inward> inwardOptional = inwardRepository.findById(entityDTO.getOutwardsInwardId());
            if (!staffEmails.isEmpty()) {
                String emailId = staffEmails.get(0);
                List<String> altEmailList = staffEmails.size() > 1 ? staffEmails.subList(1, staffEmails.size()).stream().distinct().collect(Collectors.toList()) : new ArrayList<>();
                /** Send Inventory Fulfilment Message to Notification */
                InventoryFulfilmentMessage inventoryFulfilmentMessage = new InventoryFulfilmentMessage(
                        RabbitMqConstants.INVENTORY_FULFILMENT_SUCCESS,
                        getMvnoIdFromCurrentStaff(),
                        destinationName,
                        wareHouseOptional.get().getName(),
                        RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_INVENTORY,
                        entityDTO.getInTransitQty().toString(),
                        productOptional.get().getName(),
                        inwardOptional.get().getInwardNumber(),
                        getLoggedInUser().getUsername(),
                        emailId,
                        altEmailList);
                Gson gson = new Gson();
                gson.toJson(inventoryFulfilmentMessage);
                kafkaMessageSender.send(new KafkaMessageData(inventoryFulfilmentMessage, inventoryFulfilmentMessage.getClass().getSimpleName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}