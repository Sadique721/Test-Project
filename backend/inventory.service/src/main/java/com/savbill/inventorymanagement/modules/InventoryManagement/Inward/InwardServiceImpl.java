package com.savbill.inventorymanagement.modules.InventoryManagement.Inward;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.InventorySpecification;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.InventorySpecificationRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.InventorySpecificationService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory.InventorySpecificationHistory;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory.InventorySpecificationHistoryRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping.ItemAssignHistoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping.ItemAssignHistoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkDeviceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificatioParametersRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParameters;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.*;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMapping;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.QInOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.QItem;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.NonSerializedItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItemHierarchy.NonSerializedItemHierarchyServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory.RequestInvenotryProductMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory.RequestInventory;
import com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory.RequestInventoryProductMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory.RequestInventoryRepo;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.QServiceArea;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.inventorymanagement.modules.PartnerManagement.Partner;
import com.savbill.inventorymanagement.modules.PartnerManagement.PartnerRepository;
import com.savbill.inventorymanagement.modules.PartnerManagement.QPartner;
import com.savbill.inventorymanagement.modules.Services.QServices;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamUserMapping.TeamUserMappingsRepocitory;
import com.savbill.inventorymanagement.rabbitmq.InventoryApprovalSuccessMsg;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.savbill.inventorymanagement.rabbitmq.RabbitMqConstants;
import com.savbill.inventorymanagement.security.spring.SpringContext;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.savbill.inventorymanagement.utils.CommonUtils.distinctByKey;

/**
 * The type Inward service.
 */
@Service
public class InwardServiceImpl extends ExBaseAbstractService<InwardDto, Inward, Long> {

    /**
     * The Inward repository.
     */
    @Autowired
    InwardRepository inwardRepository;

    /**
     * The Inward mapper.
     */
    @Autowired
    InwardMapper inwardMapper;

    /**
     * The Product owner service.
     */
    @Autowired
    ProductOwnerService productOwnerService;

    /**
     * The Inward service.
     */
    @Autowired
    private InwardServiceImpl inwardService;

    /**
     * The Message sender.
     */
    @Autowired
    private MessageSender messageSender;
    /**
     * The Kafka message sender.
     */
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    /**
     * The Product owner mapper.
     */
    @Autowired
    ProductOwnerMapper productOwnerMapper;

    /**
     * The Outward repository.
     */
    @Autowired
    OutwardRepository outwardRepository;

    /**
     * The Entity manager.
     */
    @PersistenceContext
    EntityManager entityManager;

    /**
     * team user mapping
     */
    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;

    /**
     * The Warehouse management repository.
     */
    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;

    @Autowired
    WareHouseTeamsMappingRepo wareHouseTeamsMappingRepo;

    /**
     * The Pop management repository.
     */
    @Autowired
    PopManagementRepository popManagementRepository;

    /**
     * The Staff user repository.
     */
    @Autowired
    StaffUserRepository staffUserRepository;

    /**
     * The In out ward mac repo.
     */
    @Autowired
    InOutWardMacRepo inOutWardMacRepo;

    /**
     * The In out ward mac service.
     */
    @Autowired
    InOutWardMACService inOutWardMACService;
    /**
     * The Product repository.
     */
    @Autowired
    ProductRepository productRepository;

    /**
     * The Product category repository.
     */
    @Autowired
    ProductCategoryRepository productCategoryRepository;

    /**
     * The Non serialized item repository.
     */
    @Autowired
    NonSerializedItemRepository nonSerializedItemRepository;

    /**
     * The Non serialized item hierarchy service.
     */
    @Autowired
    NonSerializedItemHierarchyServiceImpl nonSerializedItemHierarchyService;
    /**
     * The Item repository.
     */
    @Autowired
    ItemRepository itemRepository;

    /**
     * The Product owner repository.
     */
    @Autowired
    ProductOwnerRepository productOwnerRepository;

    /**
     * The Partner repository.
     */
    @Autowired
    PartnerRepository partnerRepository;

    /**
     * The Request invenotry product mapping repo.
     */
    @Autowired
    RequestInventoryProductMappingRepo requestInvenotryProductMappingRepo;

    /**
     * The Request inventory repo.
     */
    @Autowired
    RequestInventoryRepo requestInventoryRepo;
    /**
     * The Customer inventory mapping repo.
     */
    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;

    /**
     * The Customer inventory mapping mapper.
     */
    @Autowired
    CustomerInventoryMappingMapper customerInventoryMappingMapper;
    /**
     * The Inventory mapping repo.
     */
    @Autowired
    InventoryMappingRepo inventoryMappingRepo;

    /**
     * The Inventory mapping mapper.
     */
    @Autowired
    InventoryMappingMapper inventoryMappingMapper;

    /**
     * The Service area repository.
     */
    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    /**
     * The Outward mapper.
     */
    @Autowired
    OutwardMapper outwardMapper;

    /**
     * The Outward service.
     */
    @Autowired
    OutwardServiceImpl outwardService;

    /**
     * The Staff user service area mapping repository.
     */
    @Autowired
    private StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;

    /**
     * The Inventory specification service.
     */
    @Autowired
    InventorySpecificationService inventorySpecificationService;

    /**
     * The Inventory specification repo.
     */
    @Autowired
    InventorySpecificationRepo inventorySpecificationRepo;

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
     * The Specificatio parameters repo.
     */
    @Autowired
    SpecificatioParametersRepo specificatioParametersRepo;

    /**
     * The Network device repository.
     */
    @Autowired
    NetworkDeviceRepository networkDeviceRepository;

    /**
     * The Item assign history mapping repo.
     */
    @Autowired
    ItemAssignHistoryMappingRepo itemAssignHistoryMappingRepo;

    /**
     * The Inventory specification history repo.
     */
    @Autowired
    InventorySpecificationHistoryRepo inventorySpecificationHistoryRepo;

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(InwardServiceImpl.class);

    /**
     * Instantiates a new Inward service.
     *
     * @param inwardRepository the inward repository
     * @param inwardMapper     the inward mapper
     */
    public InwardServiceImpl(InwardRepository inwardRepository, InwardMapper inwardMapper) {
        super(inwardRepository, inwardMapper);
    }

    /**
     * Gets module name for log.
     *
     * @return the module name for log
     */
    @Override
    public String getModuleNameForLog() {
        return "[InwardServiceImpl]";
    }

    /**
     * Gets inward details by product and destination.
     *
     * @param productId       the product id
     * @param warehouseId     the warehouse id
     * @param destinationType the destination type
     * @return the inward details by product and destination
     */
    public List<Inward> getInwardDetailsByProductAndDestination(Long productId, Long warehouseId, String destinationType) {
        try {
            QInward qInward = QInward.inward;
            BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.productId.id.eq(productId))
                    .and(qInward.destinationId.eq(warehouseId))
                    .and(qInward.destinationType.equalsIgnoreCase(destinationType))
                    .and(qInward.type.in(CommonConstants.REFURBISHED, CommonConstants.NEW, CommonConstants.OLD))
                    .and(qInward.status.eq(CommonConstants.ACTIVE_STATUS))
                    .and(qInward.isDeleted.eq(false))
                    .and(qInward.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE));
            return Lists.newArrayList(inwardRepository.findAll(booleanExpression))
                    .stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets list by page and size and sort by and order by.
     *
     * @param pageNumber     the page number
     * @param customPageSize the custom page size
     * @param sortBy         the sort by
     * @param sortOrder      the sort order
     * @param filterList     the filter list
     * @return the list by page and size and sort by and order by
     */
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
        List<Long> resultPaginationList = new ArrayList<>();
        Page<Inward> finalPaginationList = null;
        String inwardNumber = null;
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.isDeleted.eq(false));
        try {
            if (getMvnoIdFromCurrentStaff() != 1) {
                List<Inward> inwardWarehouseList = getAllWarehouseForInward(null);
                List<Inward> inwardPopManagementList = getAllPOPForInward(null);
                List<Inward> inwardStaffList = getAllStaffForInward(null);
                List<Inward> inwardPartnerList = getAllPartnerStaffForInward(null);
                List<Inward> inwardServiceAreaStaffList = getAllServiceAreaForInward(null);
                List<Inward> inwardCreatedByStaffList = getAllCreatedByStaffForInward(null);

                if (inwardWarehouseList != null) {
                    if (inwardWarehouseList.size() > 0) {
                        for (int w = 0; w < inwardWarehouseList.size(); w++) {
                            resultPaginationList.add(inwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (inwardPopManagementList != null) {
                    if (inwardPopManagementList.size() > 0) {
                        for (int p = 0; p < inwardPopManagementList.size(); p++) {
                            resultPaginationList.add(inwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (inwardStaffList != null) {
                    if (inwardStaffList.size() > 0) {
                        for (int s = 0; s < inwardStaffList.size(); s++) {
                            resultPaginationList.add(inwardStaffList.get(s).getId());
                        }
                    }
                }
                if (inwardPartnerList != null) {
                    if (inwardPartnerList.size() > 0) {
                        for (int p = 0; p < inwardPartnerList.size(); p++) {
                            resultPaginationList.add(inwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (inwardServiceAreaStaffList != null) {
                    if (inwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < inwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(inwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }

                if (inwardCreatedByStaffList != null) {
                    if (inwardCreatedByStaffList.size() > 0) {
                        for (int s = 0; s < inwardCreatedByStaffList.size(); s++) {
                            resultPaginationList.add(inwardCreatedByStaffList.get(s).getId());
                        }
                    }
                }
//                finalPaginationList = inwardRepository.findLightInwardByIds(resultPaginationList, pageRequest);
                finalPaginationList = inwardRepository.findLightInwardByIdsAndGroup(resultPaginationList, pageRequest);
            }
            if (getMvnoIdFromCurrentStaff() == 1) {
                finalPaginationList = inwardRepository.findAll(booleanExpression, pageRequest);
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
     * Search generic data dto.
     *
     * @param filterList the filter list
     * @param page       the page
     * @param pageSize   the page size
     * @param sortBy     the sort by
     * @param sortOrder  the sort order
     * @return the generic data dto
     */
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn() != "") {
                        if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Inward Number")) {
                            return getInwardList(searchModel.getFilterValue(), pageRequest);
                        }
                        if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Product Name")) {
                            return getInwardListbaseOnProductname(searchModel.getFilterValue(), pageRequest);
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
     * Gets inward list.
     *
     * @param inwardNumber the inward number
     * @param pageRequest  the page request
     * @return the inward list
     */
    public GenericDataDTO getInwardList(String inwardNumber, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getInwardList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Inward> finalPaginationList = null;
        try {
            if (getMvnoIdFromCurrentStaff() != 1) {
                List<Long> resultPaginationList = new ArrayList<>();
                List<Inward> inwardWarehouseList = getAllWarehouseForInward(inwardNumber);
                List<Inward> inwardPopManagementList = getAllPOPForInward(inwardNumber);
                List<Inward> inwardStaffList = getAllStaffForInward(inwardNumber);
                List<Inward> inwardPartnerList = getAllPartnerStaffForInward(inwardNumber);
                List<Inward> inwardServiceAreaStaffList = getAllServiceAreaForInward(inwardNumber);
                if (inwardWarehouseList != null) {
                    if (inwardWarehouseList.size() > 0) {
                        for (int w = 0; w < inwardWarehouseList.size(); w++) {
                            resultPaginationList.add(inwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (inwardPopManagementList != null) {
                    if (inwardPopManagementList.size() > 0) {
                        for (int p = 0; p < inwardPopManagementList.size(); p++) {
                            resultPaginationList.add(inwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (inwardStaffList != null) {
                    if (inwardStaffList.size() > 0) {
                        for (int s = 0; s < inwardStaffList.size(); s++) {
                            resultPaginationList.add(inwardStaffList.get(s).getId());
                        }
                    }
                }
                if (inwardPartnerList != null) {
                    if (inwardPartnerList.size() > 0) {
                        for (int p = 0; p < inwardPartnerList.size(); p++) {
                            resultPaginationList.add(inwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (inwardServiceAreaStaffList != null) {
                    if (inwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < inwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(inwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
                finalPaginationList = inwardRepository.findAllByIdIn(resultPaginationList, pageRequest);
            }
            if (getMvnoIdFromCurrentStaff() == 1) {
                finalPaginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndIsDeletedIsFalse(inwardNumber, pageRequest);
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
     * Search by product and status and service name generic data dto.
     *
     * @param filterColumn the filter column
     * @param value        the value
     * @param customerId   the customer id
     * @return the generic data dto
     */
    public GenericDataDTO searchByProductAndStatusAndServiceName(String filterColumn, String value, Long customerId) {
        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
        GenericDataDTO genericDataDTO = null;
        try {
            genericDataDTO = new GenericDataDTO();
            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
            QServices qServices = QServices.services;
            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull();
            if (filterColumn.equalsIgnoreCase("Product")) {
                booleanExpression = booleanExpression.and(qCustomerInventoryMapping.customer.id.eq(Math.toIntExact(customerId))).and(qCustomerInventoryMapping.product.name.likeIgnoreCase("%" + value + "%")).and(qCustomerInventoryMapping.isDeleted.eq(false));
            }
            if (filterColumn.equalsIgnoreCase("Status")) {
                booleanExpression = booleanExpression.and(qCustomerInventoryMapping.customer.id.eq(Math.toIntExact(customerId))).and(qCustomerInventoryMapping.status.likeIgnoreCase("%" + value + "%")).and(qCustomerInventoryMapping.isDeleted.eq(false));
            }
            if (filterColumn.equalsIgnoreCase("ServiceName")) {
                booleanExpression = booleanExpression.and(qCustomerInventoryMapping.customer.id.eq(Math.toIntExact(customerId)))
                        .and(qCustomerInventoryMapping.isDeleted.eq(false))
                        .and(qCustomerInventoryMapping.serviceId.in(
                                JPAExpressions.select(qServices.id)
                                        .from(qServices)
                                        .where(qServices.serviceName.likeIgnoreCase("%" + value + "%"))));
            }

            List<CustomerInventoryMapping> customerInventoryMappingList = (List<CustomerInventoryMapping>) customerInventoryMappingRepo.findAll(booleanExpression);
            List<CustomerInventoryMappingDto> customerInventoryMappingDtoList = customerInventoryMappingMapper.domainToDTO(customerInventoryMappingList, new CycleAvoidingMappingContext());
            if (null != customerInventoryMappingDtoList) {
                genericDataDTO.setDataList(customerInventoryMappingDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(customerInventoryMappingList.size());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return genericDataDTO;
    }

    /**
     * Search by customer and pop and service area name generic data dto.
     *
     * @param filterList  the filter list
     * @param page        the page
     * @param pageSize    the page size
     * @param sortBy      the sort by
     * @param sortOrder   the sort order
     * @param staffID     the staff id
     * @param fileterName the fileter name
     * @param isSerelized the is serelized
     * @return the generic data dto
     */
    public GenericDataDTO searchByCustomerAndPopAndServiceAreaName(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, Long staffID, String fileterName, boolean isSerelized) {
        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
        try {
            PageRequest pageRequest1 = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    return getPopAndServiceAreaAndsCustomerByName(searchModel.getFilterValue(), pageRequest1, fileterName, staffID, searchModel.getFilterColumn().trim(), isSerelized);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Gets all inward.
     *
     * @return the all inward
     */
    public List<Inward> getAllInward() {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Long> resultPaginationList = new ArrayList<>();
        List<Inward> finalPaginationList = null;
        String inwardNumber = null;
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.isDeleted.eq(false));
        try {
            if (getMvnoIdFromCurrentStaff() != 1) {
                List<Inward> inwardWarehouseList = getAllWarehouseForInward(null);
                List<Inward> inwardPopManagementList = getAllPOPForInward(null);
                List<Inward> inwardStaffList = getAllStaffForInward(null);
                List<Inward> inwardPartnerList = getAllPartnerStaffForInward(null);
                List<Inward> inwardServiceAreaStaffList = getAllServiceAreaForInward(null);
                if (inwardWarehouseList != null) {
                    if (inwardWarehouseList.size() > 0) {
                        for (int w = 0; w < inwardWarehouseList.size(); w++) {
                            resultPaginationList.add(inwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (inwardPopManagementList != null) {
                    if (inwardPopManagementList.size() > 0) {
                        for (int p = 0; p < inwardPopManagementList.size(); p++) {
                            resultPaginationList.add(inwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (inwardStaffList != null) {
                    if (inwardStaffList.size() > 0) {
                        for (int s = 0; s < inwardStaffList.size(); s++) {
                            resultPaginationList.add(inwardStaffList.get(s).getId());
                        }
                    }
                }
                if (inwardPartnerList != null) {
                    if (inwardPartnerList.size() > 0) {
                        for (int p = 0; p < inwardPartnerList.size(); p++) {
                            resultPaginationList.add(inwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (inwardServiceAreaStaffList != null) {
                    if (inwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < inwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(inwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
                finalPaginationList = inwardRepository.findAllByIdIn(resultPaginationList);
            }
            if (getMvnoIdFromCurrentStaff() == 1) {
                finalPaginationList = (List<Inward>) inwardRepository.findAll(booleanExpression);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return finalPaginationList;
    }


    /**
     * Gets inward listbase on productname.
     *
     * @param productName the product name
     * @param pageRequest the page request
     * @return the inward listbase on productname
     */
    public GenericDataDTO getInwardListbaseOnProductname(String productName, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getInwardwardList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Inward> finalPaginationList = null;
        try {
            List<Inward> inwardList = getAllInward();

            // Filter the outwardList based on the productName
            if (productName != null) {
                inwardList = inwardList.stream()
                        .filter(outward -> outward.getProductId().getName().toLowerCase().contains(productName.toLowerCase()))
                        .collect(Collectors.toList());
            }

            // Create a Pageable object based on the provided pageRequest
//            Pageable pageable = pageRequest;

            // Apply pagination and sorting to the filtered data
            List<Inward> paginatedList = inwardList.stream()
                    .skip(pageRequest.getOffset())
                    .limit(pageRequest.getPageSize())
                    .sorted(Comparator.comparing(Inward::getCreatedate, Comparator.reverseOrder()))
                    .collect(Collectors.toList());

            // Count the total number of matching records
            long totalCount = inwardList.size();

            // Create a Page object containing the paginated list and the total count
            finalPaginationList = new PageImpl<>(paginatedList, pageRequest, totalCount);

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
     * Gets pop and service area ands customer by name.
     *
     * @param s1            the s 1
     * @param pageRequest   the page request
     * @param fileterName   the fileter name
     * @param staffId       the staff id
     * @param fileterColumn the fileter column
     * @param isSerelized   the is serelized
     * @return the pop and service area ands customer by name
     */
    public GenericDataDTO getPopAndServiceAreaAndsCustomerByName(String s1, PageRequest pageRequest, String fileterName, Long staffId, String fileterColumn, boolean isSerelized) {
        String SUBMODULE = getModuleNameForLog() + " [getCustomerAndPopAndServiceAreaByName()] ";
        try {
            if (fileterName.equalsIgnoreCase("Customer")) {
                GenericDataDTO genericDataDTO = new GenericDataDTO();
                QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                JPAQuery<?> query = new JPAQuery<>(entityManager);
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull();
                if (isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomerInventoryMapping.qty.gt(0)).and(qCustomerInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qCustomerInventoryMapping.customer.firstname.likeIgnoreCase("%" + s1 + "%"));
                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomerInventoryMapping.qty.gt(0)).and(qCustomerInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qCustomerInventoryMapping.product.name.likeIgnoreCase("%" + s1 + "%"));
                    }
                    if (getMvnoIdFromCurrentStaff() != 1) {
                        booleanExpression = booleanExpression.and(qCustomerInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                    }
                }
                if (!isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomerInventoryMapping.qty.gt(0)).and(qCustomerInventoryMapping.inOutWardMACMapping.isEmpty()).and(qCustomerInventoryMapping.customer.firstname.likeIgnoreCase("%" + s1 + "%"));
                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomerInventoryMapping.qty.gt(0)).and(qCustomerInventoryMapping.inOutWardMACMapping.isEmpty()).and(qCustomerInventoryMapping.product.name.likeIgnoreCase("%" + s1 + "%"));
                    }
                    if (getMvnoIdFromCurrentStaff() != 1) {
                        booleanExpression = booleanExpression.and(qCustomerInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                    }
                }

                Page<CustomerInventoryMapping> customerInventoryMappingPage = customerInventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (customerInventoryMappingPage.getSize() != 0) {
                    customerInventoryMappingPage.stream().forEach(r -> {
                        r.setCustomerFirstName(r.getCustomer().getFirstname());
                        r.setCustomerLastName(r.getCustomer().getLastname());
                        r.setServiceAreaName(r.getCustomer().getServicearea().getName());
                        Item item = itemRepository.findById(r.getItemId()).orElse(null);
                        if (item != null) {
                            r.setItemwarranty(item.getWarranty());
                            r.setExpDate(item.getExpireDate());
                        }
                    });
                }
                //paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())

                if (null != customerInventoryMappingPage && 0 < customerInventoryMappingPage.getSize()) {
                    genericDataDTO.setDataList(customerInventoryMappingPage.getContent().stream().map(data -> customerInventoryMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setTotalRecords(customerInventoryMappingPage.getTotalElements());
                    genericDataDTO.setPageRecords(customerInventoryMappingPage.getNumberOfElements());
                    genericDataDTO.setCurrentPageNumber(customerInventoryMappingPage.getNumber() + 1);
                    genericDataDTO.setTotalPages(customerInventoryMappingPage.getTotalPages());
                }
                return genericDataDTO;
            }

            if (fileterName.equalsIgnoreCase("Pop")) {
                GenericDataDTO genericDataDTO = new GenericDataDTO();
                QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
                QPopManagement qPopManagement = QPopManagement.popManagement;
                JPAQuery<?> query = new JPAQuery<>(entityManager);
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull();
                if (isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop")).and(qInventoryMapping.ownerId.in(
                                JPAExpressions.select(qPopManagement.id)
                                        .from(qPopManagement)
                                        .where(qPopManagement.name.likeIgnoreCase(("%" + s1 + "%")))));

                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop")).and(qInventoryMapping.product.name.likeIgnoreCase(("%" + s1 + "%")));

                    }
                    if (getMvnoIdFromCurrentStaff() != 1) {
                        booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                    }
                }
                if (!isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop")).and(qInventoryMapping.ownerId.in(
                                JPAExpressions.select(qPopManagement.id)
                                        .from(qPopManagement)
                                        .where(qPopManagement.name.likeIgnoreCase(("%" + s1 + "%")))));
                        ;
                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop")).and(qInventoryMapping.product.name.likeIgnoreCase("%" + s1 + "%"));

                    }
                    if (getMvnoIdFromCurrentStaff() != 1) {
                        booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                    }
                }

                Page<InventoryMapping> inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        PopManagement popManagement = popManagementRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setPopName(popManagement.getName());
                    });
                }
                if (null != inventoryMappingPage && 0 < inventoryMappingPage.getSize()) {
                    List<InventoryMappingDto> mappingDtos = inventoryMappingPage.getContent().stream().map(data -> inventoryMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());

                    mappingDtos.stream().forEach(dto ->
                    {
                        if (dto.getProductId() != null) {
                            Product product = productRepository.findById(dto.getProductId()).orElse(null);
                            if (product != null)
                                dto.setDeviceType(product.getProductCategory().getDeviceType());

                            if (dto.getInOutWardMACMapping() != null && !dto.getInOutWardMACMapping().isEmpty() && dto.getInOutWardMACMapping().get(0).getInventoryMappingId() != null) {
                                NetworkDevices devices = networkDeviceRepository.findByInventorymappingId(dto.getInOutWardMACMapping().get(0).getInventoryMappingId());
                                if (devices != null) {
                                    dto.setDeviceName(devices.getName());
                                    dto.setTotalInPort(devices.getTotalInPorts());
                                    dto.setAvailableInPort(devices.getAvailableInPorts());
                                    dto.setTotalOutPort(devices.getTotalOutPorts());
                                    dto.setAvailableOutPort(devices.getAvailableOutPorts());
                                    dto.setTotalPort(devices.getTotalPorts());
                                    dto.setAvailablePort(devices.getAvailablePorts());
                                }
                            }
                        }
                    });
                    genericDataDTO.setDataList(mappingDtos);
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                    genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                    genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                    genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
                }
                return genericDataDTO;
            }

            if (fileterName.equalsIgnoreCase("Service Area")) {
                GenericDataDTO genericDataDTO = new GenericDataDTO();
                QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
                QServiceArea qServiceArea = QServiceArea.serviceArea;
                JPAQuery<?> query = new JPAQuery<>(entityManager);
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull();
                if (isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("Service Area")).and(qInventoryMapping.ownerId.in(
                                JPAExpressions.select(qServiceArea.id)
                                        .from(qServiceArea)
                                        .where(qServiceArea.name.likeIgnoreCase(("%" + s1 + "%")))));

                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("Service Area")).and(qInventoryMapping.product.name.likeIgnoreCase("%" + s1 + "%"));

                    }
                    if (getMvnoIdFromCurrentStaff() != 1) {
                        booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                    }
                }
                if (!isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.containsIgnoreCase("Service Area")).and(qInventoryMapping.ownerId.in(
                                JPAExpressions.select(qServiceArea.id)
                                        .from(qServiceArea)
                                        .where(qServiceArea.name.likeIgnoreCase(("%" + s1 + "%")))));
                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase(CommonConstants.APPROVE)).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.containsIgnoreCase("Service Area")).and(qInventoryMapping.product.name.likeIgnoreCase("%" + s1 + "%"));

                    }
                    if (getMvnoIdFromCurrentStaff() != 1) {
                        booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                    }
                }

                Page<InventoryMapping> inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        ServiceArea serviceArea = serviceAreaRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setServiceAreaName(serviceArea.getName());
                    });
                }
                if (null != inventoryMappingPage && 0 < inventoryMappingPage.getSize()) {

                    List<InventoryMappingDto> mappingDtos = inventoryMappingPage.getContent().stream().map(data -> inventoryMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                    mappingDtos.stream().forEach(dto ->
                    {
                        if (dto.getProductId() != null) {
                            Product product = productRepository.findById(dto.getProductId()).orElse(null);
                            if (product != null)
                                dto.setDeviceType(product.getProductCategory().getDeviceType());

                            if (dto.getInOutWardMACMapping() != null && !dto.getInOutWardMACMapping().isEmpty() && dto.getInOutWardMACMapping().get(0).getInventoryMappingId() != null) {
                                NetworkDevices devices = networkDeviceRepository.findByInventorymappingId(dto.getInOutWardMACMapping().get(0).getInventoryMappingId());
                                if (devices != null) {
                                    dto.setDeviceName(devices.getName());
                                    dto.setTotalInPort(devices.getTotalInPorts());
                                    dto.setAvailableInPort(devices.getAvailableInPorts());
                                    dto.setTotalOutPort(devices.getTotalOutPorts());
                                    dto.setAvailableOutPort(devices.getAvailableOutPorts());
                                    dto.setTotalPort(devices.getTotalPorts());
                                    dto.setAvailablePort(devices.getAvailablePorts());
                                }
                            }
                        }
                    });
                    genericDataDTO.setDataList(mappingDtos);

                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                    genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                    genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                    genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
                }
                return genericDataDTO;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }


    /**
     * Save entity inward dto.
     *
     * @param entity      the entity
     * @param fromOutward the from outward
     * @param isReturned  the is returned
     * @return the inward dto
     * @throws Exception the exception
     */
    @Transactional
    public InwardDto saveEntity(InwardDto entity, Boolean fromOutward, Boolean isReturned) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        InwardDto inwardDto = null;
        try {
            entity.setInwardNumber(getRandomenumber("IN", "-", "", getMvnoIdFromCurrentStaff()));
            entity.setQty(0L);
            entity.setUnusedQty(0L);
            Product product = productRepository.findById(entity.getProductId().getId()).get();
            String uom = product.getProductCategory().getUnit();
            if (uom.equalsIgnoreCase("kilometer")) {
                entity.setInTransitQty(1000 * entity.getInTransitQty());
            } else {
                entity.setInTransitQty(entity.getInTransitQty());
            }
            entity.setUsedQty(0L);
            entity.setOutTransitQty(0L);
            entity.setRejectedQty(0L);
            entity.setApprovalStatus(CommonConstants.PENDING);
            entity.setAssignNonSerializedItemQty(0L);
            if (!(entity.getTotalMacSerial() != null && entity.getTotalMacSerial() != 0))
                entity.setTotalMacSerial(0L);
            if (entity.getType().equalsIgnoreCase(CommonConstants.NEW))
                entity.setType(CommonConstants.NEW);
            else if (entity.getType().equalsIgnoreCase(CommonConstants.REFURBISHED))
                entity.setType(CommonConstants.REFURBISHED);
            else if (entity.getType().equalsIgnoreCase(CommonConstants.OLD))
                entity.setType(CommonConstants.OLD);
            if (!isReturned)
                entity.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);

            if (entity.getStartDateTime() != null && entity.getExpiryDateTime() != null) {
                if (entity.getStartDateTime().isAfter(LocalDate.now()) && entity.getExpiryDateTime().isAfter(LocalDate.now()))
                    entity.setOemWarrantyStatus("NotStarted");

                if (entity.getStartDateTime().equals(LocalDate.now()) || (entity.getStartDateTime().isBefore(LocalDate.now()) && (entity.getExpiryDateTime().isAfter(LocalDate.now()) || entity.getExpiryDateTime().equals(LocalDate.now()))))
                    entity.setOemWarrantyStatus("InWarranty");

                if ((entity.getStartDateTime().isBefore(LocalDate.now()) && (entity.getExpiryDateTime().isBefore(LocalDate.now()))))
                    entity.setOemWarrantyStatus("Expired");

                Long days = Duration.between(entity.getStartDateTime().atStartOfDay(), entity.getExpiryDateTime().atStartOfDay()).toDays();

                if (days != null)
                    entity.setOemWarrantyRemainingDays(days.intValue());
            }

            inwardDto = super.saveEntity(entity);
            List<SpecificationParametersDTO> specificationParametersDTOList = new ArrayList<>();
            List<InventorySpecification> inventorySpecificationList = inventorySpecificationRepo.findAllByInward_Id(inwardDto.getId());
            if (!inventorySpecificationList.isEmpty()) {
                for (InventorySpecification item : inventorySpecificationList) {
                    SpecificationParametersDTO specificationParametersDTO = new SpecificationParametersDTO();
                    specificationParametersDTO.setId(item.getSpecificationParameters().getId());
                    specificationParametersDTO.setPcid(item.getSpecificationParameters().getProductCategory().getId());
                    specificationParametersDTO.setParamName(item.getSpecificationParameters().getParamName());
                    specificationParametersDTO.setIsMandatory(item.getSpecificationParameters().getIsMandatory());
                    specificationParametersDTO.setMvnoId(item.getSpecificationParameters().getMvnoId());
                    specificationParametersDTO.setParamValue(item.getParamValue());
                    specificationParametersDTOList.add(specificationParametersDTO);
                }
            }
            if (!specificationParametersDTOList.isEmpty()) {
                entity.setSpecificationParametersDTOList(specificationParametersDTOList);
            }
            //Todo: Code for Inward for Integration
//            messageSender.send(inwardDto, RabbitMqConstants.QUEUE_SEND_INWARD_TO_INTEGRATOIN);
            saveProductOwnerAfterInward(entity, fromOutward);
            if (entity.getSpecificationParametersDTOList() != null && entity.getSpecificationParametersDTOList().size() > 0) {
                inventorySpecificationService.saveEntity(inwardDto.getId(), inwardDto.getProductId().getId(), entity.getSpecificationParametersDTOList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inwardDto;
    }


    public List<InwardDto> getInwardGroup(Long id) {

        Inward base = inwardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inward not found"));

        Long parentId = (base.getGroupId() == null) ? base.getId() : base.getGroupId();

        List<Inward> list = inwardRepository.findParentWithChildren(parentId);

        return list.stream()
                .map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }
    /**
     * Save entity from rms inward dto.
     *
     * @param entity      the entity
     * @param fromOutward the from outward
     * @param isReturned  the is returned
     * @return the inward dto
     * @throws Exception the exception
     */
    @Transactional
    public InwardDto saveEntityFromRms(InwardDto entity, Boolean fromOutward, Boolean isReturned) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
//        Product product = productRepository.findById(entity.getProductId().getId()).get();
        Product product = productRepository.findByName(entity.getProductId().getName());
        String uom = product.getProductCategory().getUnit();
        InwardDto inwardDto = null;
        try {
            entity.setInwardNumber(getRandomenumber("IN", "-", "", getMvnoIdFromCurrentStaff()));
            entity.setUnusedQty(0L);
            if (uom.equalsIgnoreCase("kilometer")) {
                entity.setInTransitQty(1000 * entity.getInTransitQty());
            } else {
                entity.setInTransitQty(entity.getInTransitQty());
            }
            entity.setUsedQty(0L);
            entity.setOutTransitQty(0L);
            entity.setRejectedQty(0L);
            entity.setAssignNonSerializedItemQty(0L);
            if (!(entity.getTotalMacSerial() != null && entity.getTotalMacSerial() != 0))
                entity.setTotalMacSerial(0L);
            if (entity.getType().equalsIgnoreCase(CommonConstants.NEW))
                entity.setType(CommonConstants.NEW);
            else if (entity.getType().equalsIgnoreCase(CommonConstants.REFURBISHED))
                entity.setType(CommonConstants.REFURBISHED);
            else if (entity.getType().equalsIgnoreCase(CommonConstants.OLD))
                entity.setType(CommonConstants.OLD);
            if (!isReturned)
                entity.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
            //inwardDto = super.saveEntity(entity);
            entity.setProductId(product);
            inwardDto = inwardMapper.domainToDTO(inwardRepository.save(inwardMapper.dtoToDomain(entity, new CycleAvoidingMappingContext())), new CycleAvoidingMappingContext());
            saveProductOwnerAfterInwardFromRms(entity, fromOutward);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inwardDto;
    }

    /**
     * Gets all inward by product and staff.
     *
     * @param productId the product id
     * @param staffId   the staff id
     * @return the all inward by product and staff
     */
    public List<Inward> getAllInwardByProductAndStaff(Long productId, Long staffId) {
        try {
            QInward qInward = QInward.inward;
            JPAQuery<Inward> query = new JPAQuery<>(entityManager);
            List<Inward> inwardList = new ArrayList<>();
            BooleanExpression booleanExpression = qInward.isNotNull()
                    .and(qInward.productId.id.eq(productId))
                    .and(qInward.destinationType.equalsIgnoreCase(CommonConstants.STAFF))
                    .and(qInward.destinationId.eq(staffId))
                    .and(qInward.isDeleted.eq(false))
                    .and(qInward.productId.productCategory.type.eq(CommonConstants.CUSTOMER_BIND))
                    .and(qInward.approvalStatus.contains(CommonConstants.APPROVE));
            List<Tuple> result = query.select(qInward.id, qInward.inwardNumber, qInward.unusedQty, qInward.mvnoId).from(qInward).where(booleanExpression).fetch();
            if (!result.isEmpty()) {
                result.forEach(tuple -> {
                    Inward inward = new Inward();
                    inward.setId(tuple.get(qInward.id));
                    inward.setInwardNumber(tuple.get(qInward.inwardNumber));
                    inward.setUnusedQty(tuple.get(qInward.unusedQty));
                    inward.setMvnoId(tuple.get(qInward.mvnoId));
                    inwardList.add(inward);
                });
            }
            if (getMvnoIdFromCurrentStaff() == 1)
                return inwardList;
            else
                return inwardList.stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList())
                        .stream().filter(inward -> inward.getUnusedQty() > 0).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all network bind inwards.
     *
     * @param productId the product id
     * @param staffId   the staff id
     * @return the all network bind inwards
     */
    public List<Inward> getAllNetworkBindInwards(Long productId, Long staffId) {
        try {
            QInward qInward = QInward.inward;
            JPAQuery<Inward> query = new JPAQuery<>(entityManager);
            List<Inward> inwardList = new ArrayList<>();
            BooleanExpression booleanExpression = qInward.isNotNull()
                    .and(qInward.productId.id.eq(productId))
                    .and(qInward.destinationType.equalsIgnoreCase(CommonConstants.STAFF))
                    .and(qInward.destinationId.eq(staffId))
                    .and(qInward.isDeleted.eq(false))
                    .and((qInward.productId.productCategory.type.eq(CommonConstants.NA_Bind))
                            .or(qInward.productId.productCategory.type.eq(CommonConstants.NETWORK_BIND)))
                    .and(qInward.approvalStatus.contains(CommonConstants.APPROVE));
            List<Tuple> result = query.select(qInward.id, qInward.inwardNumber, qInward.unusedQty, qInward.mvnoId).from(qInward).where(booleanExpression).fetch();
            if (!result.isEmpty()) {
                result.forEach(tuple -> {
                    Inward inward = new Inward();
                    inward.setId(tuple.get(qInward.id));
                    inward.setInwardNumber(tuple.get(qInward.inwardNumber));
                    inward.setUnusedQty(tuple.get(qInward.unusedQty));
                    inward.setMvnoId(tuple.get(qInward.mvnoId));
                    inwardList.add(inward);
                });
            }
            if (getMvnoIdFromCurrentStaff() == 1)
                return inwardList;
            else
                return inwardList.stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets all network bindand customerand pop inwards.
     *
     * @param productId the product id
     * @param staffId   the staff id
     * @return the all network bindand customerand pop inwards
     */
    public List<Inward> getAllNetworkBindandCustomerandPopInwards(Long productId, Long staffId) {
        try {
            QInward qInward = QInward.inward;
            JPAQuery<Inward> query = new JPAQuery<>(entityManager);
            List<Inward> inwardList = new ArrayList<>();
            BooleanExpression booleanExpression = qInward.isNotNull()
                    .and(qInward.productId.id.eq(productId))
                    .and(qInward.destinationType.equalsIgnoreCase(CommonConstants.STAFF))
                    .and(qInward.destinationId.eq(staffId))
                    .and(qInward.isDeleted.eq(false))
                    .and((qInward.productId.productCategory.type.eq(CommonConstants.NA_Bind))
                            .or(qInward.productId.productCategory.type.eq(CommonConstants.NETWORK_BIND))
                            .or(qInward.productId.productCategory.type.eq(CommonConstants.CUSTOMER_BIND)))
                    .and(qInward.approvalStatus.contains(CommonConstants.APPROVE));
            List<Tuple> result = query.select(qInward.id, qInward.inwardNumber, qInward.unusedQty, qInward.mvnoId).from(qInward).where(booleanExpression).fetch();
            if (!result.isEmpty()) {
                result.forEach(tuple -> {
                    Inward inward = new Inward();
                    inward.setId(tuple.get(qInward.id));
                    inward.setInwardNumber(tuple.get(qInward.inwardNumber));
                    inward.setUnusedQty(tuple.get(qInward.unusedQty));
                    inward.setMvnoId(tuple.get(qInward.mvnoId));
                    inwardList.add(inward);
                });
            }
            if (getMvnoIdFromCurrentStaff() == 1)
                return inwardList;
            else
                return inwardList.stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Update entity inward dto.
     *
     * @param entity      the entity
     * @param fromOutward the from outward
     * @param isReturned  the is returned
     * @return the inward dto
     * @throws Exception the exception
     */
    @Transactional
    public InwardDto updateEntity(InwardDto entity, Boolean fromOutward, Boolean isReturned) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [updateEntity()] ";
        Inward inward = inwardRepository.findById(entity.id).get();
        InwardDto inwardDto = null;
        try {
            entity.setQty(0L);
            entity.setUnusedQty(0L);
            Product product = productRepository.findById(entity.getProductId().getId()).get();
            String uom = product.getProductCategory().getUnit();
            if (uom.equalsIgnoreCase("kilometer")) {
                entity.setInTransitQty(1000 * entity.getInTransitQty());
            } else {
                entity.setInTransitQty(entity.getInTransitQty());
            }
            entity.setUsedQty(0L);
            entity.setOutTransitQty(0L);
            entity.setRejectedQty(0L);
            entity.setApprovalStatus(CommonConstants.PENDING);
            entity.setTotalMacSerial(inward.getTotalMacSerial());
            if (!isReturned || entity.getType().equalsIgnoreCase(CommonConstants.NEW))
                entity.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
            inwardDto = super.updateEntity(entity);
            if (entity.getSpecificationParametersDTOList() != null && entity.getSpecificationParametersDTOList().size() > 0) {
                inventorySpecificationService.updateEntity(inwardDto.getId(), inwardDto.getProductId().getId(), entity.getSpecificationParametersDTOList());
            }
            if (!fromOutward) {
                // Managing quantities in product owner
                ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
                List<Inward> inwardList = inwardRepository.findAllByProductId(Math.toIntExact(entity.getProductId().getId()));
//                Inward inward = inwardRepository.findById(entity.id).get();
                if (productOwner != null) {
                    productOwner.setProductId(entity.getProductId().getId());
                    productOwner.setOwnerId(entity.getDestinationId());
                    productOwner.setOwnerType(entity.getDestinationType());
                    productOwnerService.updateEntity(productOwner);
                    if (inwardList.size() == 1) {
                        productOwner.setQuantity(entity.getQty());
                        productOwner.setUnusedQty(entity.getQty());
                        productOwner.setUsedQty(entity.getUsedQty());
                        productOwner.setInTransitQty(entity.getInTransitQty());
                        productOwnerService.updateEntity(productOwner);
                    }
                    if (inwardList.size() > 1) {
                        //Set Quantity
                        if (entity.getQty() != null) {
                            if (inward.getQty() > entity.getQty()) {
                                productOwner.setQuantity(productOwner.getQuantity() - (inward.getQty() - entity.getQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else if (inward.getQty() < entity.getQty()) {
                                productOwner.setQuantity(productOwner.getQuantity() + (entity.getQty() - inward.getQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else {
                                productOwner.setQuantity(productOwner.getQuantity());
                                productOwnerService.updateEntity(productOwner);
                            }
                        }
                        //Set UnUsedQty
                        if (entity.getUnusedQty() != null) {
                            if (inward.getUnusedQty() > entity.getUnusedQty()) {
                                productOwner.setUnusedQty(productOwner.getUnusedQty() - (inward.getUnusedQty() - entity.getUnusedQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else if (inward.getUnusedQty() < entity.getUnusedQty()) {
                                productOwner.setUnusedQty(productOwner.getUnusedQty() + (entity.getUnusedQty() - inward.getUnusedQty()));
                                productOwnerService.updateEntity(productOwner);
                            }
                        }
                        //Set UsedQty
                        if (entity.getUsedQty() != null) {
                            if (inward.getUsedQty() > entity.getUsedQty()) {
                                productOwner.setUsedQty(productOwner.getUsedQty() - (inward.getUsedQty() - entity.getUsedQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else if (inward.getUsedQty() < entity.getUsedQty()) {
                                productOwner.setUsedQty(productOwner.getUsedQty() + (entity.getUsedQty() - inward.getUsedQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else {
                                productOwner.setUsedQty(productOwner.getUsedQty());
                                productOwnerService.updateEntity(productOwner);
                            }
                        }
                        //Set InTransitQty
                        if (entity.getInTransitQty() != null) {
                            if (inward.getInTransitQty() >= entity.getInTransitQty()) {
                                productOwner.setInTransitQty(entity.getInTransitQty());
                                //productOwner.setInTransitQty(productOwner.getInTransitQty() - (inward.getInTransitQty() - entity.getInTransitQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else if (inward.getInTransitQty() < entity.getInTransitQty()) {
                                productOwner.setInTransitQty(productOwner.getInTransitQty() + (entity.getInTransitQty() - inward.getInTransitQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else {
                                productOwner.setInTransitQty(productOwner.getInTransitQty());
                                productOwnerService.updateEntity(productOwner);
                            }
                        }
                    }
                }
            }
            inwardDto = super.saveEntity(entity);
            List<SpecificationParametersDTO> specificationParametersDTOList = new ArrayList<>();
            List<InventorySpecification> inventorySpecificationList = inventorySpecificationRepo.findAllByInward_Id(inwardDto.getId());
            if (!inventorySpecificationList.isEmpty()) {
                for (InventorySpecification item : inventorySpecificationList) {
                    SpecificationParametersDTO specificationParametersDTO = new SpecificationParametersDTO();
                    specificationParametersDTO.setId(item.getSpecificationParameters().getId());
                    specificationParametersDTO.setPcid(item.getSpecificationParameters().getProductCategory().getId());
                    specificationParametersDTO.setParamName(item.getSpecificationParameters().getParamName());
                    specificationParametersDTO.setIsMandatory(item.getSpecificationParameters().getIsMandatory());
                    specificationParametersDTO.setMvnoId(item.getSpecificationParameters().getMvnoId());
                    specificationParametersDTO.setParamValue(item.getParamValue());
                    specificationParametersDTOList.add(specificationParametersDTO);
                }
            }
            if (!specificationParametersDTOList.isEmpty()) {
                inwardDto.setSpecificationParametersDTOList(specificationParametersDTOList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inwardDto;
    }

    /**
     * Delete verification boolean.
     *
     * @param id the id
     * @return the boolean
     * @throws Exception the exception
     */
    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        return inwardRepository.deleteVerify(id) == 0;
    }


    /**
     * Delete inward generic data dto.
     *
     * @param entityDTO the entity dto
     * @return the generic data dto
     */
    public GenericDataDTO deleteInward(InwardDto entityDTO) {
        ExecutorService executor = Executors.newFixedThreadPool(5); // Use a thread pool with 5 threads
        try {
            // Delete InOutWardMACMappings in parallel
            Future<List<InOutWardMACMapping>> futureList = executor.submit(() -> {
                List<InOutWardMACMapping> list = inOutWardMACService.delete(entityDTO.getId().intValue());
                list.forEach(mapping -> mapping.setIsDeleted(true));
                return list;
            });

            // Delete Items in parallel
            Future<List<Item>> futureItemList = executor.submit(() -> {
                QItem qItem = QItem.item;
                BooleanExpression booleanExpression = qItem.isDeleted.eq(false)
                        .and(qItem.currentInwardId.eq(entityDTO.getId()));
                List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));

                itemList.forEach(item -> item.setIsDeleted(true));
                itemRepository.saveAll(itemList); // Batch update
                return itemList;
            });

            // Update ProductOwner in parallel
            Future<List<ProductOwner>> futureProductOwnerList = executor.submit(() -> {
                QProductOwner qProductOwner = QProductOwner.productOwner;
                BooleanExpression aBoolean = qProductOwner.ownerId.eq(entityDTO.getDestinationId())
                        .and(qProductOwner.ownerType.equalsIgnoreCase(entityDTO.getDestinationType()))
                        .and(qProductOwner.productId.eq(entityDTO.getProductId().getId()));
                List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(aBoolean));

                productOwnerList.forEach(owner ->
                        owner.setInTransitQty(owner.getInTransitQty() - entityDTO.getInTransitQty())
                );
                productOwnerRepository.saveAll(productOwnerList); // Batch update
                return productOwnerList;
            });

            // Wait for all tasks to complete
            futureList.get();
            futureItemList.get();
            futureProductOwnerList.get();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting inward entry", e);
        } finally {
            executor.shutdown(); // Ensure threads are properly shut down
        }
        return null;
    }


    /**
     * Gets all warehouse for inward.
     *
     * @param inwardNumber the inward number
     * @return the all warehouse for inward
     */
//Get All Inward based on Warehouse
    public List<Inward> getAllWarehouseForInward(String inwardNumber) {
        try {
            List<Inward> paginationList = null;
            List<WareHouse> wareHouseList = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                wareHouseList = warehouseManagementRepository.findAllByIsDeletedIsFalseWithoutPageable();
            } else {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffIdImprove();
                if (!serviceAreaIds.isEmpty()) {
                    List<Long> warehouseIds = wareHouseManagmentServiceAreamappingRepo.findWarehouseIdsByServiceIdIn(serviceAreaIds);
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
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findLightInwardByFilters(inwardNumber, warehouseResult, warehouseDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        paginationList = inwardRepository.findLightInwardByDestinationAndMvno(warehouseResult, warehouseDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }
                } else {
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findLightInwardByInwardNumberAndDestination(inwardNumber, warehouseResult, warehouseDestinationType);
                    } else {
                        paginationList = inwardRepository.findLightInwardByDestination(warehouseResult, warehouseDestinationType);
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
     * Gets all pop for inward.
     *
     * @param inwardNumber the inward number
     * @return the all pop for inward
     */
//Get All Inward based on POP
    public List<Inward> getAllPOPForInward(String inwardNumber) {
        try {
            List<Inward> paginationList = null;
            List<PopManagement> popManagementList = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                popManagementList = popManagementRepository.findAllLightPopManagementByIsDeletedIsFalse();
            } else {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffIdImprove();
                if (!serviceAreaIds.isEmpty()) {
                    List<Long> ids = popServiceAreaMappingRepo.findPopIdsByServiceAreaIdIn(serviceAreaIds);
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
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findLightInwardByFilters(inwardNumber, popResult, popDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        paginationList = inwardRepository.findLightInwardByDestinationAndMvno(popResult, popDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }
                } else {
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findLightInwardByInwardNumberAndDestination(inwardNumber, popResult, popDestinationType);
                    } else {
                        paginationList = inwardRepository.findLightInwardByDestination(popResult, popDestinationType);
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
     * Gets all staff for inward.
     *
     * @param inwardNumber the inward number
     * @return the all staff for inward
     */
    public List<Inward> getAllStaffForInward(String inwardNumber) {
//        String status = "ACTIVE";
        try {
            List<Long> resultStaffId = new ArrayList<>();
            List<Inward> paginationList = null;
            if (getMvnoIdFromCurrentStaff() != 1) {
                resultStaffId = staffUserRepository.findStaffIdsByIsDeleteFalseAndMvnoIdIn(getLoggedInUserId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));

            } else {
                resultStaffId = staffUserRepository.findStaffIdsById(getLoggedInUserId());
            }
            if (!resultStaffId.isEmpty()) {
                String staffDestinationType = "Staff";
                if (getMvnoIdFromCurrentStaff() != 1) {
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findLightInwardByFilters(inwardNumber, resultStaffId, staffDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        paginationList = inwardRepository.findLightInwardByDestinationAndMvno(resultStaffId, staffDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }
                } else {
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findLightInwardByInwardNumberAndDestination(inwardNumber, resultStaffId, staffDestinationType);
                    } else {
                        paginationList = inwardRepository.findLightInwardByDestination(resultStaffId, staffDestinationType);
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
     * Gets all created by staff for inward.
     *
     * @param inwardNumber the inward number
     * @return the all created by staff for inward
     */
    public List<Inward> getAllCreatedByStaffForInward(String inwardNumber) {
//        String status = "ACTIVE";
        try {
            List<Long> resultStaffId = new ArrayList<>();
            List<Inward> paginationList = null;
            if (getMvnoIdFromCurrentStaff() != 1) {
                List<StaffUser> staffUserList = staffUserRepository.findAllByIdIsDeleteIsFalseAndMvnoIdInWithSpecificParameter(getLoggedInUserId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                for (StaffUser staffUser : staffUserList) {
                    Integer staffIds = staffUser.getId();
                    resultStaffId.add(Long.valueOf(staffIds));
                }
                resultStaffId = staffUserRepository.findStaffIdsByIsDeleteFalseAndMvnoIdIn(getLoggedInUserId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            } else {
                resultStaffId = staffUserRepository.findStaffIdsById(getLoggedInUserId());
            }
            if (!resultStaffId.isEmpty()) {
                String staffDestinationType = "Staff";
                if (getMvnoIdFromCurrentStaff() != 1) {
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findAllLightInwardByInwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn1(inwardNumber, resultStaffId, staffDestinationType);
                    } else {
                        paginationList = inwardRepository.findAllLightInwardByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn1(resultStaffId, staffDestinationType);
                    }
                } else {
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findLightInwardByInwardNumberAndDestination(inwardNumber, resultStaffId, staffDestinationType);
                    } else {
                        paginationList = inwardRepository.findAllLightInwardByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn1(resultStaffId, staffDestinationType);
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
     * Gets all partner staff for inward.
     *
     * @param inwardNumber the inward number
     * @return the all partner staff for inward
     */
    public List<Inward> getAllPartnerStaffForInward(String inwardNumber) {
//        String status = "ACTIVE";
        try {
            List<Long> resultStaffId = new ArrayList<>();
            List<Inward> paginationList = null;
            if (getMvnoIdFromCurrentStaff() != 1) {
                Integer partnerId = staffUserRepository.findPartnerIdByStaffIdAndMvnoIdIn(getLoggedInUserId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                resultStaffId = partnerRepository.findActivePartnerIds(partnerId, CommonConstants.ACTIVE_STATUS);
            } else {
                Integer partnerId = staffUserRepository.findPartnerIdByStaffId(getLoggedInUserId());
                if (partnerId != null) {
                    resultStaffId.addAll(partnerRepository.findActivePartnerIds(partnerId, CommonConstants.ACTIVE_STATUS));
                }
            }
            if (!resultStaffId.isEmpty()) {
                String partnerDestinationType = "Partner";
                if (getMvnoIdFromCurrentStaff() != 1) {
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(inwardNumber, resultStaffId, partnerDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(resultStaffId, partnerDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }
                } else {
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(inwardNumber, resultStaffId, partnerDestinationType);
                    } else {
                        paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(resultStaffId, partnerDestinationType);
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
     * Gets all service area for inward.
     *
     * @param inwardNumber the inward number
     * @return the all service area for inward
     */
    public List<Inward> getAllServiceAreaForInward(String inwardNumber) {
        try {
            List<Inward> paginationList = null;
            // Common method for find Service Area List Based on StaffId
            List<StaffUser> staffUserList = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                staffUserList = staffUserRepository.findAllByIsDeleteIsFalseWithSpecificParameter();
            } else {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffIdImprove();
                if (!serviceAreaIds.isEmpty()) {
                    List<Integer> ids = staffUserServiceAreaMappingRepository.findStaffIdsByServiceIdIn(serviceAreaIds);
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
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findLightInwardByFilters(inwardNumber, serviceAreaStaffResult, serviceAreaDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    } else {
                        paginationList = inwardRepository.findLightInwardByDestinationAndMvno(serviceAreaStaffResult, serviceAreaDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    }
                } else {
                    if (inwardNumber != null) {
                        paginationList = inwardRepository.findLightInwardByInwardNumberAndDestination(inwardNumber, serviceAreaStaffResult, serviceAreaDestinationType);
                    } else {
                        paginationList = inwardRepository.findLightInwardByDestination(serviceAreaStaffResult, serviceAreaDestinationType);
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
     * Gets assign inventories.
     *
     * @param pageNumber     the page number
     * @param customPageSize the custom page size
     * @param sortBy         the sort by
     * @param sortOrder      the sort order
     * @param filterList     the filter list
     * @param staffId        the staff id
     * @return the assign inventories
     */
    public GenericDataDTO getAssignInventories(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long staffId) {
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
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    /**
     * Gets all inventories by owner.
     *
     * @param pageNumber     the page number
     * @param customPageSize the custom page size
     * @param sortBy         the sort by
     * @param sortOrder      the sort order
     * @param filterList     the filter list
     * @param ownerId        the owner id
     * @param ownerType      the owner type
     * @return the all inventories by owner
     */
    public GenericDataDTO getAllInventoriesByOwner(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long ownerId, String ownerType) {
        String SUBMODULE = getModuleNameForLog() + " [getAssignInventories()] ";
        QInward qInward = QInward.inward;

        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.destinationType.equalsIgnoreCase(ownerType)).and(qInward.destinationId.eq(ownerId).and(qInward.isDeleted.eq(false)));

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
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    /**
     * Save inward approval inward dto.
     * @param inward the inward
     * @param inwardApprovalStatus the inward approval status
     * @param approvalRemark the approval remark
     * @param productId the product id
     * @param items the items
     * @param hasMac the has mac
     * @param hasSerial the has serial
     * @param hasTrackable the has trackable
     * @param outward the outward
     * @return the inward dto
     */
    @Transactional
    public InwardDto saveInwardApproval(Inward inward, String inwardApprovalStatus, String approvalRemark,
                                        Long productId, List<Item> items, boolean hasMac, boolean hasSerial,
                                        boolean hasTrackable, Outward outward, boolean batch) {
//        System.out.println("Save Inward Approval Started");
        try {
            /** Initial Inward */
            if (outward == null) {
                /** Inward Reject */
                if (inwardApprovalStatus.equalsIgnoreCase(CommonConstants.REJECTED)) {
                    String sourceType = inward.getSourceType();
                    Long sourceId = inward.getSourceId();
                    Long destinationId = inward.getDestinationId();
                    String destinationType = inward.getDestinationType();
                    Long inTransQty = inward.getInTransitQty();
                    initialInwardReject(inward.getId(), productId, inwardApprovalStatus,
                            hasMac, hasSerial, destinationId,
                            destinationType, inTransQty);
                }
                Long initialInTransQty = inwardRepository.findInTransitQuantityByInwardIdAndApprovalStatusAndStatus(inward.getId(), CommonConstants.PENDING, CommonConstants.ACTIVE_STATUS);
                /** Update Inward Method*/
//                System.out.println("Save Inward Approval Ended");
                return updateInward(inwardApprovalStatus, approvalRemark, initialInTransQty, inward.getId(), items, true);
            }
            /** Inward Created By Outward */
            else if (outward != null) {
                String sourceType = inward.getSourceType();
                Long sourceId = inward.getSourceId();
                Long destinationId = inward.getDestinationId();
                String destinationType = inward.getDestinationType();
                Long inTransQty = inward.getInTransitQty();
                if (inwardApprovalStatus.equalsIgnoreCase(CommonConstants.REJECTED)) {
                    String ownerType = outward.getSourceType();
                    Long ownerId = outward.getSourceId();
                    /** Inward Of Outward Reject */
                    inwardOfOutwardReject(outward.getId(), inward.getId(), productId,
                            inwardApprovalStatus, hasMac, hasSerial,
                            hasTrackable, sourceId, sourceType,
                            destinationType, destinationId, ownerType,
                            ownerId, inTransQty, batch);
                } else if (inwardApprovalStatus.equalsIgnoreCase(CommonConstants.APPROVE)) {
                    updateItem(inward.getId(), CommonConstants.APPROVE, sourceId, sourceType, destinationType, destinationId, batch);
                    List<SpecificationParameters> specificationParameters = specificatioParametersRepo.findAllByProductCategory_Id(inward.getProductId().getProductCategory().getId());
                    if (!specificationParameters.isEmpty()) {
                        saveItemHistory(items, destinationId, destinationType);
                    }
                }
//                System.out.println("Save Inward Approval Ended");
                return saveInwardByOutwardApproval(inwardApprovalStatus, approvalRemark, outward.getId(), items);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }
    @Transactional
    public List<InwardDto> processInwardApproval(InwardApprovalDTO inwardDto) {
        try {

            Inward inward = inwardRepository.findById(inwardDto.getId())
                    .orElseThrow(() -> new RuntimeException("Inward not found with id " + inwardDto.getId()));

            // GROUP HANDLING (FINAL FIXED VERSION)
            List<Inward> inwardList;
            Long parentId = null;

            // CASE 1: CHILD
            if (inward.getGroupId() != null) {
                parentId = inward.getGroupId();

                // CASE 2: PARENT
            } else if (Boolean.TRUE.equals(inward.getIsGroup())) {
                parentId = inward.getId();
            }

            // FINAL DECISION
            if (parentId != null) {
                inwardList = inwardRepository.findByGroupIdOrId(parentId);
            } else {
                inwardList = Collections.singletonList(inward);
            }



            List<InwardDto> responseList = new ArrayList<>();

            for (Inward inwardRecord : inwardList) {


                // COMMON PRODUCT DATA (FETCH ONCE)
                Long pcId = productRepository.findProductCategoryIdByProductId(inwardRecord.getProductId().getId());
                boolean hasMac = productCategoryRepository.findHasMacById(pcId);
                boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
                boolean hasTrackable = productCategoryRepository.findHasTrackableById(pcId);
                boolean isoemConsiderByProductId = productRepository.findIsoemConsiderByProductId(inwardRecord.getProductId().getId());
                String uom = productCategoryRepository.findUnitById(pcId);

                List<Item> items = new ArrayList<>();

                //  APPROVE LOGIC
                if (inwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.APPROVE)) {

                    if (hasMac || hasSerial) {
                        if (!Objects.equals(inwardRecord.getInTransitQty(), inwardRecord.getTotalMacSerial())) {
                            throw new CustomValidationException(
                                    HttpStatus.EXPECTATION_FAILED.value(),
                                    "Only " + inwardRecord.getTotalMacSerial() + " items out of "
                                            + inwardRecord.getInTransitQty() + " are present in inward",
                                    null
                            );
                        }
                    }

                    if (inwardRecord.getOutwardId() == null) {

                        if (hasMac || hasSerial) {
                            items = inOutWardMACService.saveManualItems(
                                    inwardRecord,
                                    CommonConstants.APPROVE,
                                    isoemConsiderByProductId
                            );
                        }

                        if (!hasSerial && hasTrackable) {

                            InwardApprovalDTO inwardApprovalDTO = new InwardApprovalDTO();

                            inwardApprovalDTO.setId(inwardRecord.getId());
                            inwardApprovalDTO.setProductId(inwardRecord.getProductId().getId());
                            inwardApprovalDTO.setApprovalStatus(inwardDto.getApprovalStatus());
                            inwardApprovalDTO.setApprovalRemark(inwardDto.getApprovalRemark());
                            inwardApprovalDTO.setMvnoId(inwardDto.getMvnoId());

                            inOutWardMACService.saveNonSerializedItemsAfterApprovalInward(inwardApprovalDTO, uom);
                        }
                    }

                    //  REJECT LOGIC
                } else if (inwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.REJECTED)
                        && inwardRecord.getOutwardId() == null) {

                    if (hasMac || hasSerial) {
                        items = inOutWardMACService.saveManualItems(
                                inwardRecord,
                                CommonConstants.REJECTED,
                                isoemConsiderByProductId
                        );
                    }
                }

                // CALL EXISTING CORE METHOD (UNCHANGED)
                InwardDto saved = saveInwardApproval(
                        inwardRecord,
                        inwardDto.getApprovalStatus(),
                        inwardDto.getApprovalRemark(),
                        inwardRecord.getProductId().getId(),
                        items,
                        hasMac,
                        hasSerial,
                        hasTrackable,
                        inwardRecord.getOutwardId(),
                        true
                );

                // POST ACTIONS
                if (inwardRecord.getOutwardId() != null &&
                        inwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.APPROVE)) {

                    productOwnerService.sharedThresholdRequestMessage(
                            inwardRecord.getProductId().getId(),
                            inwardRecord.getSourceId(),
                            inwardRecord.getSourceType()
                    );

                } else if (inwardRecord.getOutwardId() == null &&
                        inwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.APPROVE)) {

                    productOwnerService.setIsNotify(
                            inwardRecord.getProductId().getId(),
                            inwardRecord.getDestinationId(),
                            inwardRecord.getDestinationType()
                    );
                }

                responseList.add(saved);
            }

            return responseList;

        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error("Error processing inward approval", e);
            throw new CustomValidationException(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error processing inward approval",
                    e
            );
        }
    }
     /**
     * Inward of outward reject.
     * @param outwardId the outward id
     * @param inwardId the inward id
     * @param productId the product id
     * @param inwardApprovalStatus the inward approval status
     * @param hasMac the has mac
     * @param hasSerial the has serial
     * @param hasTrackable the has trackable
     * @param sourceId the source id
     * @param sourceType the source type
     * @param destinationType the destination type
     * @param destinationId the destination id
     * @param ownerType the owner type
     * @param ownerId the owner id
     * @param inTransQty the in trans qty
     * @param batch
     */
    private void inwardOfOutwardReject(Long outwardId, Long inwardId, Long productId,
                                       String inwardApprovalStatus, boolean hasMac, boolean hasSerial,
                                       boolean hasTrackable, Long sourceId, String sourceType,
                                       String destinationType, Long destinationId, String ownerType,
                                       Long ownerId, Long inTransQty, boolean batch) {
        /** Update Product Owner Method*/
        try {
            updateProductOwner(inwardId, productId, inwardApprovalStatus, ownerType,
                    ownerId, destinationId, destinationType, inTransQty);
            /** Update Item Method*/
            updateItem(inwardId, CommonConstants.REJECTED, sourceId, sourceType, destinationType, destinationId, batch);
            if (hasMac || hasSerial) {
                /** Update Inward Outward Mac Mapping Method*/
                updateInOutMacMapping(inwardId, productId, inwardApprovalStatus);
                updateInOutMacMappingAfterInOutward(inwardId, productId, inwardApprovalStatus);
            }
            if (!hasSerial && hasTrackable) {
                nonSerializedItemHierarchyService.updateNonSerializedItemHierarchy(inwardId, productId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Initial inward reject.
     *
     * @param inwardId the inward id
     * @param productId the product id
     * @param inwardApprovalStatus the inward approval status
     * @param hasMac the has mac
     * @param hasSerial the has serial
     * @param destinationId the destination id
     * @param destinationType the destination type
     * @param inTransQty the in trans qty
     */
    private void initialInwardReject(Long inwardId, Long productId, String inwardApprovalStatus,
                                     boolean hasMac, boolean hasSerial, Long destinationId,
                                     String destinationType, Long inTransQty) {
        try {
            String ownerType = null;
            Long ownerId = null;
            /** Update Product Owner Method*/
            updateProductOwner(inwardId, productId, inwardApprovalStatus, ownerType, ownerId, destinationId, destinationType, inTransQty);
            if (hasMac || hasSerial) {
                Integer countItemsByInwardId = inOutWardMacRepo.countItemsByInwardId(inwardId);
                if (countItemsByInwardId > 0) {
                    updateInOutMacMapping(inwardId);
                }
            }
            inventorySpecificationRepo.deleteByInwardId(inwardId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Save item history.
     * @param items the inward id
     * @param destinationId the destination id
     * @param destinationType the destination type
     */
    public void saveItemHistory(List<Item> items, Long destinationId, String destinationType) {
        batchSaveItemHistory(items, destinationId, destinationType);
    }

    /**
     * Batch save item history.
     *
     * @param macMappings the mac mappings
     * @param destinationId the destination id
     * @param destinationType the destination type
     */
    private void batchSaveItemHistory(List<Item> macMappings, Long destinationId, String destinationType) {
        int batchSize = Math.max(1000, macMappings.size() / (Runtime.getRuntime().availableProcessors() * 2));
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < macMappings.size(); i += batchSize) {
                int start = i;
                int end = Math.min(i + batchSize, macMappings.size());
                List<Item> batch = macMappings.subList(start, end);

                futures.add(executor.submit(() -> {
                    List<ItemAssignHistoryMapping> historyMappings = new ArrayList<>();
                    batch.forEach(macMapping -> {
                        List<InventorySpecificationHistory> inventorySpecificationHistories =
                                inventorySpecificationHistoryRepo.findAllByItemIdAndStatus(macMapping.getId(), CommonConstants.NEW);

                        inventorySpecificationHistories.forEach(history -> {
                            ItemAssignHistoryMapping historyMapping = new ItemAssignHistoryMapping();
                            historyMapping.setItemId(macMapping.getId());
                            historyMapping.setOwnerId(destinationId);
                            historyMapping.setOwnerType(destinationType);
                            historyMapping.setCreatedate(LocalDateTime.now());
                            historyMapping.setSpecificationHistoryId(history.getId());
                            historyMappings.add(historyMapping);
                        });
                    });

                    // Batch insert history mappings
                    if (!historyMappings.isEmpty()) {
                        itemAssignHistoryMappingRepo.saveAll(historyMappings);
                    }
                }));
            }

            // Ensure all batches are processed
//            for (Future<?> future : futures) {
//                future.get();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            throw new RuntimeException("Batch save processing failed", e);
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Save inward by outward approval inward dto.
     * @param inwardApprovalStatus the inward approval status
     * @param approval_remark the approval remark
     * @param outwardId the outward id
     * @param items the items
     * @return the inward dto
     */
    public InwardDto saveInwardByOutwardApproval(String inwardApprovalStatus, String approval_remark, Long outwardId, List<Item> items) {
//        System.out.println("Save Inward By Outward Approval Started");
        try {
            Long inTransitQuantityByOutwardId = inwardRepository.findInTransitQuantityByOutwardId(outwardId, CommonConstants.ACTIVE_STATUS, getMvnoIdFromCurrentStaff());
            Optional<Long> inwardIdByOutwardIdAndStatus = inwardRepository.findInwardIdByOutwardIdAndStatus(outwardId, CommonConstants.ACTIVE_STATUS, getMvnoIdFromCurrentStaff());
            /** Update Inward */
            updateInward(inwardApprovalStatus, approval_remark, inTransitQuantityByOutwardId, inwardIdByOutwardIdAndStatus.get(), items, false);
            Long outwardInTransitQty = outwardRepository.findInTransitQuantityByOutwardId(outwardId, CommonConstants.ACTIVE_STATUS, getMvnoIdFromCurrentStaff());
            OutwardDto outwardDto = outwardMapper.domainToDTO(outwardRepository.findById(outwardId).orElse(null), new CycleAvoidingMappingContext());
            if (inwardApprovalStatus.equalsIgnoreCase(CommonConstants.APPROVE)) {
                //Update Outward
                outwardDto.setQty(outwardInTransitQty);
                outwardDto.setUnusedQty(outwardInTransitQty);
                outwardDto.setInTransitQty(0L);
                outwardDto.setUsedQty(0L);
                outwardDto.setOutTransitQty(0L);
                outwardDto.setRejectedQty(0L);
                outwardDto.setApprovalStatus(CommonConstants.APPROVE);
                outwardDto.setApprovalRemark(approval_remark);
                //update RequestInvetoryPrpduct
                if (outwardDto.getRequestInventoryProductId() != null) {
                    RequestInvenotryProductMapping requestInvenotryProductMapping = requestInvenotryProductMappingRepo.findById(outwardDto.getRequestInventoryProductId()).orElse(null);
                    requestInvenotryProductMapping.setRequestStatus("Close");
                    requestInvenotryProductMappingRepo.save(requestInvenotryProductMapping);
                    RequestInventory requestInventory = requestInventoryRepo.findById(outwardDto.getRequestInventoryId()).orElse(null);
                    if (requestInventory != null) {
                        List<RequestInvenotryProductMapping> requestInvenotryProductMappingList = requestInvenotryProductMappingRepo.findAllByInventoryRequestId(requestInventory.getId());
                        List<String> statusList = requestInvenotryProductMappingList.stream().map(RequestInvenotryProductMapping::getRequestStatus).collect(Collectors.toList());
                        if (statusList.stream().allMatch(str -> str.equalsIgnoreCase("Close"))) {
                            requestInventory.setInventoryRequestStatus("Complted");
                            requestInventoryRepo.save(requestInventory);
                        } else if (statusList.contains("Close") && statusList.contains("Open")) {
                            requestInventory.setInventoryRequestStatus("Partially Completed");
                            requestInventoryRepo.save(requestInventory);
                        }
                    }
                }
                outwardService.updateEntity(outwardDto);
            } else if (inwardApprovalStatus.equalsIgnoreCase(CommonConstants.REJECTED)) {
                //Update Outward
                outwardDto.setQty(0L);
                outwardDto.setUnusedQty(0L);
                outwardDto.setInTransitQty(0L);
                outwardDto.setUsedQty(0L);
                outwardDto.setOutTransitQty(0L);
                outwardDto.setRejectedQty(outwardInTransitQty);
                outwardDto.setApprovalStatus(CommonConstants.REJECTED);
                outwardDto.setApprovalRemark(approval_remark);
                if (outwardDto.getRequestInventoryProductId() != null) {
                    RequestInvenotryProductMapping requestInvenotryProductMapping = requestInvenotryProductMappingRepo.findById(outwardDto.getRequestInventoryProductId()).orElse(null);
                    requestInvenotryProductMapping.setRequestStatus(CommonConstants.REJECTED);
                    requestInvenotryProductMappingRepo.save(requestInvenotryProductMapping);
                    RequestInventory requestInventory = requestInventoryRepo.findById(outwardDto.getRequestInventoryId()).orElse(null);
                    List<RequestInvenotryProductMapping> requestInvenotryProductMappingList = requestInvenotryProductMappingRepo.findAllByInventoryRequestId(requestInventory.getId());
                    List<String> statusList = requestInvenotryProductMappingList.stream().map(RequestInvenotryProductMapping::getRequestStatus).collect(Collectors.toList());
                    if (statusList.stream().allMatch(str -> str.equalsIgnoreCase(CommonConstants.REJECTED))) {
                        requestInventory.setInventoryRequestStatus(CommonConstants.REJECTED);
                        requestInventoryRepo.save(requestInventory);
                    }
                }
                outwardService.updateEntity(outwardDto);
            }
//            System.out.println("Save Inward By Outward Approval Ended");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Update inward inward dto.
     * @param inwardApprovalStatus the inward approval status
     * @param approval_remark the approval remark
     * @param inTransitQty the in transit qty
     * @param inwardId the inward id
     * @param items the items
     * @param initialInward the initial inward
     * @return the inward dto
     */
    public InwardDto updateInward(String inwardApprovalStatus, String approval_remark, Long inTransitQty,
                                  Long inwardId, List<Item> items, boolean initialInward) {
        try {
//            System.out.println("********** Start Update Inward **********");
            InwardDto inwardDto = null;
            Inward inward = inwardRepository.findById(inwardId).get();
            inwardDto = inwardMapper.domainToDTO(inward, new CycleAvoidingMappingContext());
            if (inwardApprovalStatus.equalsIgnoreCase(CommonConstants.APPROVE)) {
                inwardDto.setQty(inTransitQty);
                inwardDto.setUnusedQty(inTransitQty);
                inwardDto.setUsedQty(0L);
                inwardDto.setInTransitQty(0L);
                inwardDto.setOutTransitQty(0L);
                inwardDto.setRejectedQty(0L);
                inwardDto.setApprovalStatus(CommonConstants.APPROVE);
                inwardDto.setApprovalRemark(approval_remark);
                /** Update Product Owner Entity */
                addInOwner(inwardDto, inward, true);
                /** Update Inward */
                updateEntity(inwardDto);
                /** Send Asset Inventory if Has Asset True */
                Boolean hasAssetConsiderByProductId = productRepository.findHasAssetConsiderByProductId(inwardDto.getProductId().getId());
                if (hasAssetConsiderByProductId) {
                    sendAssetInventory(inwardDto, items);
                }
                /** Save Inventory Specification History at Inti*/
                if (initialInward) {
                    List<InventorySpecification> inventorySpecificationList = inventorySpecificationRepo.findAllByInward_Id(inwardDto.getId());
                    if (!inventorySpecificationList.isEmpty()) {
                        List<InOutWardMACMapping> inOutWardMACMappingsList = inOutWardMacRepo.findByInwardId(inwardDto.getId());
                        saveHistory(inventorySpecificationList, inwardDto, inOutWardMACMappingsList);
                    }
                }
//                System.out.println("********** End Update Inward **********");
                return inwardDto;
            } else if (inwardApprovalStatus.equalsIgnoreCase(CommonConstants.REJECTED)) {
                inwardDto.setQty(0L);
                inwardDto.setUnusedQty(0L);
                inwardDto.setUsedQty(0L);
                inwardDto.setInTransitQty(0L);
                inwardDto.setOutTransitQty(0L);
                inwardDto.setRejectedQty(inTransitQty);
                inwardDto.setApprovalStatus(CommonConstants.REJECTED);
                inwardDto.setApprovalRemark(approval_remark);
                updateEntity(inwardDto);
                return inwardDto;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Send asset inventory.
     *
     * @param inwardDto the inward dto
     * @param items the items
     */
    private void sendAssetInventory(InwardDto inwardDto, List<Item> items) {
        try {
            String emailId = staffUserRepository.findEmailByUserId(inwardDto.getDestinationId().intValue());
            String userFirstName = staffUserRepository.findFirstNameByUserId(inwardDto.getDestinationId().intValue());
            String phoneNumber = staffUserRepository.findPhoneByUserId(inwardDto.getDestinationId().intValue());
            if (inwardDto.getDestinationType().equalsIgnoreCase("Staff") && inwardDto.getDestinationId() != null) {
                sendInwardWithStaffDestination(items, inwardDto.getApprovalStatus(), emailId, userFirstName, phoneNumber);
            }
            if (inwardDto.getSourceType() != null && inwardDto.getSourceType().equalsIgnoreCase("Staff") && inwardDto.getDestinationType() != null && inwardDto.getDestinationType().equalsIgnoreCase("Warehouse")) {
                sendInwardWithStaffSource(items, inwardDto.getApprovalStatus(), userFirstName, phoneNumber, emailId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Save history.
     *
     * @param inventorySpecificationList the inventory specification list
     * @param inwardDto                  the inward dto
     * @param inOutWardMACMappingsList   the in out ward mac mappings list
     */
    private void saveHistory(List<InventorySpecification> inventorySpecificationList, InwardDto inwardDto, List<InOutWardMACMapping> inOutWardMACMappingsList) {
        try {
            Long destinationId = inwardDto.getDestinationId();
            String destinationType = inwardDto.getDestinationType();
            boolean isInwardOnly = inwardDto.getId() != null && inwardDto.getOutwardId() == null;
            if (!isInwardOnly) return;
            List<InventorySpecificationHistory> invSpecHistories = new ArrayList<>();
            List<ItemAssignHistoryMapping> itemAssignHistories = new ArrayList<>();
            for (InventorySpecification inventorySpecification : inventorySpecificationList) {
                for (InOutWardMACMapping macMapping : inOutWardMACMappingsList) {
                    InventorySpecificationHistory invSpecHistory = new InventorySpecificationHistory();
                    invSpecHistory.setItemId(macMapping.getItemId());
                    invSpecHistory.setInvenId(inventorySpecification.getId());
                    invSpecHistory.setParamId(inventorySpecification.getSpecificationParameters().getId());
                    invSpecHistory.setParamValue(inventorySpecification.getParamValue());
                    invSpecHistory.setIsMandatory(inventorySpecification.getSpecificationParameters().getIsMandatory());
                    invSpecHistory.setCreatedById(getLoggedInUserId());
                    invSpecHistory.setLastModifiedById(getLoggedInUserId());
                    invSpecHistory.setCreatedByName(getLoggedInUser().getUsername());
                    invSpecHistory.setLastModifiedByName(getLoggedInUser().getUsername());
                    invSpecHistory.setStatus(CommonConstants.NEW);
                    invSpecHistories.add(invSpecHistory);
                }
            }
            // Save InventorySpecificationHistory in batch
            List<InventorySpecificationHistory> inventorySpecificationHistories = inventorySpecificationHistoryRepo.saveAll(invSpecHistories);
            // Populate ItemAssignHistoryMapping list with IDs from saved histories
            for (int i = 0; i < inventorySpecificationHistories.size(); i++) {
                InventorySpecificationHistory savedHistory = inventorySpecificationHistories.get(i);
                InOutWardMACMapping macMapping = inOutWardMACMappingsList.get(i % inOutWardMACMappingsList.size());
                ItemAssignHistoryMapping itemAssignHistoryMapping = new ItemAssignHistoryMapping();
                itemAssignHistoryMapping.setItemId(macMapping.getItemId());
                itemAssignHistoryMapping.setOwnerId(destinationId);
                itemAssignHistoryMapping.setOwnerType(destinationType);
                itemAssignHistoryMapping.setCreatedate(LocalDateTime.now());
                itemAssignHistoryMapping.setSpecificationHistoryId(savedHistory.getId());
                itemAssignHistories.add(itemAssignHistoryMapping);
            }
            batchSave(itemAssignHistories, itemAssignHistoryMappingRepo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Batch save list.
     *
     * @param <T>  the type parameter
     * @param entities the entities
     * @param repository the repository
     * @return the list
     */
    private <T> List<T> batchSave(List<T> entities, JpaRepository<T, ?> repository) {
        try {
            int batchSize = 1000;
            List<T> savedEntities = new ArrayList<>();

            for (int i = 0; i < entities.size(); i += batchSize) {
                int end = Math.min(i + batchSize, entities.size());
                List<T> batch = entities.subList(i, end);
                savedEntities.addAll(repository.saveAll(batch));
            }
            repository.flush();
            return savedEntities;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Send inward with staff source.
     * @param item the item
     * @param approvalStatus the approval status
     * @param userFirstName the user first name
     * @param phoneNumber the phone number
     * @param emailId the email id
     */
    private void sendInwardWithStaffSource(List<Item> item, String approvalStatus, String userFirstName, String phoneNumber, String emailId) {
        try {
            if (item != null && !item.isEmpty() && userFirstName != null && phoneNumber != null && emailId != null) {
                Long buId = null;
                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                    buId = getBUIdsFromCurrentStaff().get(0);
                }
                AtomicReference<String> actualData = new AtomicReference<>("");
                item.forEach(data -> {
                    String specificationData = "";
                    String assetsId = data.getAssetId();
                    String serialId = data.getSerialNumber();
                    List<String> specificationList = new ArrayList<>();
                    Long inventorySpecId = data.getInvenSpecId();
                    if (inventorySpecId != null) {
                        List<InventorySpecification> specifications = inventorySpecificationRepo.findAllByInvenSpecId(inventorySpecId);
                        specifications.forEach(spec -> {
                            String specification = spec.getSpecificationParameters().getParamName() + ": " + spec.getParamValue();
                            specificationList.add(specification);
                        });
                        if (specificationList != null && !specificationList.isEmpty())
                            specificationData = specificationList.stream().collect(Collectors.joining("<br/>"));
                    }
                    String assetData = "Asset ID:- " + assetsId + "<br />Serial Number:- " + serialId + "<br />Specifications:- <br />" + specificationData + "<br /><br />";
                    actualData.set(actualData + assetData);
                });
                InventoryApprovalSuccessMsg approvalSuccessMsg = new InventoryApprovalSuccessMsg(userFirstName, actualData.get(), LocalDate.now().toString(), phoneNumber, emailId, RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_INVENTORY, approvalStatus, RabbitMqConstants.INWARD_APPROVAL_TO_STAFF_SUCCESS, getMvnoIdFromCurrentStaff(), buId);
                Gson gson = new Gson();
                gson.toJson(approvalSuccessMsg);
                kafkaMessageSender.send(new KafkaMessageData(approvalSuccessMsg, approvalSuccessMsg.getClass().getSimpleName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Send inward with staff destination.
     * @param item the item
     * @param approvalStatus the approval status
     * @param emailId the email id
     * @param userFirstName the user first name
     * @param phoneNumber the phone number
     */
    private void sendInwardWithStaffDestination(List<Item> item, String approvalStatus, String emailId, String userFirstName, String phoneNumber) {
        try {
            if (item != null && !item.isEmpty() && emailId != null && userFirstName != null && phoneNumber != null) {
                Long buId = null;
                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                    buId = getBUIdsFromCurrentStaff().get(0);
                }
                AtomicReference<String> actualData = new AtomicReference<>("");
                item.forEach(data -> {
                    String specificationData = "";
                    String assetsId = data.getAssetId();
                    String serialId = data.getSerialNumber();
                    List<String> specificationList = new ArrayList<>();
                    Long inventorySpecId = data.getInvenSpecId();
                    if (inventorySpecId != null) {
                        List<InventorySpecification> specifications = inventorySpecificationRepo.findAllByInvenSpecId(inventorySpecId);
                        specifications.forEach(spec -> {
                            String specification = spec.getSpecificationParameters().getParamName() + ": " + spec.getParamValue();
                            specificationList.add(specification);
                        });

                        if (specificationList != null && !specificationList.isEmpty())
                            specificationData = specificationList.stream().collect(Collectors.joining("<br/>"));
                    }
                    String assetData = "Asset ID:- " + assetsId + "<br />Serial Number:- " + serialId + "<br />Specifications:- <br />" + specificationData + "<br /><br />";
                    actualData.set(actualData + assetData);
                });
                InventoryApprovalSuccessMsg approvalSuccessMsg = new InventoryApprovalSuccessMsg(userFirstName, actualData.get(), LocalDate.now().toString(), phoneNumber, emailId, RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_INVENTORY, approvalStatus, RabbitMqConstants.INWARD_APPROVAL_TO_STAFF_SUCCESS, getMvnoIdFromCurrentStaff(), buId);
                Gson gson = new Gson();
                gson.toJson(approvalSuccessMsg);
                kafkaMessageSender.send(new KafkaMessageData(approvalSuccessMsg, approvalSuccessMsg.getClass().getSimpleName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets randomenumber.
     *
     * @param flag1  the flag 1
     * @param flag2  the flag 2
     * @param flag3  the flag 3
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
            Integer count = inwardRepository.findTopByOrderByIdDesc(mvnoId);
            if (count == null || count == 0)
                flag += 1;
            else
                flag += count + 1;
        }
        return flag;
    }

    /**
     * Gets all inwards.
     *
     * @return the all inwards
     */
    public List<Inward> getAllInwards() {
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isNotNull()
                .and(qInward.isDeleted.eq(false));
        return Lists.newArrayList(inwardRepository.findAll(booleanExpression))
                .stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
    }

    /**
     * Update in out mac mapping inward dto.
     *
     * @param inwardId             the inward id
     * @param productId            the product id
     * @param inwardApprovalStatus the inward approval status
     * @return the inward dto
     */
    public InwardDto updateInOutMacMapping(Long inwardId, Long productId, String inwardApprovalStatus) {
        try {
            // Query for eligible InOutWardMACMappings
            QInOutWardMACMapping qMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression filter = qMapping.inwardId.eq(inwardId)
                    .and(qMapping.isForwarded.eq(0))
                    .and(qMapping.isReturned.eq(0));
            List<InOutWardMACMapping> mappings = Lists.newArrayList(inOutWardMacRepo.findAll(filter));
            if (mappings.isEmpty()) return null; // Exit early if no records found
            // Batch update
            mappings.forEach(mapping -> {
                mapping.setIsForwarded(-1);
            });
            inOutWardMacRepo.saveAll(mappings);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating InOutMacMapping: " + e.getMessage(), e);
        }
        return null; // Return meaningful data if required
    }

    /**
     * Update in out mac mapping.
     *
     * @param inwardId the inward id
     */
    public void updateInOutMacMapping(Long inwardId) {
        try {
            // Use bulk update query to avoid fetching and modifying entities in memory
            long updatedRows = inOutWardMacRepo.updateIsForwarded(inwardId, 0, 0, -1);

            if (updatedRows == 0) {
                LOGGER.info("No records updated for inwardId: {}" + inwardId);
            } else {
                LOGGER.info(" records updated for inwardId: " + updatedRows + inwardId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating InOutMacMapping", e);
        }
    }

    /**
     * Update in out mac mapping after in outward inward dto.
     *
     * @param inwardId             the inward id
     * @param productId            the product id
     * @param inwardApprovalStatus the inward approval status
     * @return the inward dto
     */
    public InwardDto updateInOutMacMappingAfterInOutward(Long inwardId, Long productId, String inwardApprovalStatus) {
        try {
            // Query for eligible InOutWardMACMappings
            QInOutWardMACMapping qMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression filter = qMapping.inwardIdOfOutward.eq(inwardId)
                    .and(qMapping.isForwarded.eq(1))
                    .and(qMapping.isReturned.eq(0));

            List<InOutWardMACMapping> mappings = Lists.newArrayList(inOutWardMacRepo.findAll(filter));

            if (mappings.isEmpty()) return null; // Exit early if no records found

            // Batch update
            mappings.forEach(mapping -> {
                mapping.setIsForwarded(0);
                mapping.setIsReturned(0);
                mapping.setInwardIdOfOutward(null);
            });

            inOutWardMacRepo.saveAll(mappings); // Bulk save for efficiency

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating InOutMacMappingAfterInOutward: " + e.getMessage(), e);
        }
        return null; // Return meaningful data if required
    }


    /**
     * Update product owner product owner.
     *
     * @param inwardId             the inward id
     * @param productId            the product id
     * @param inwardApprovalStatus the inward approval status
     * @param ownerType the owner type
     * @param ownerId the owner id
     * @param destinationId the destination id
     * @param destinationType the destination type
     * @param inTransQty the in trans qty
     * @return the product owner
     */
    public ProductOwner updateProductOwner(Long inwardId, Long productId, String inwardApprovalStatus, String ownerType, Long ownerId, Long destinationId, String destinationType, Long inTransQty) {
        try {
            if (inwardApprovalStatus.equalsIgnoreCase(CommonConstants.REJECTED)) {
                if (destinationId != null && destinationType != null) {
                    ProductOwner productOwnerList = productOwnerRepository.findByProductIdAndOwnerIdAndOwnerType(productId, destinationId, destinationType);
                    productOwnerList.setInTransitQty(productOwnerList.getInTransitQty() - inTransQty);
                    productOwnerRepository.save(productOwnerList);
                }
                if (ownerType != null && ownerId != null) {
                    ProductOwner productOwnerListSource = productOwnerRepository.findByProductIdAndOwnerIdAndOwnerType(productId, ownerId, ownerType);
                    productOwnerListSource.setUnusedQty(productOwnerListSource.getUnusedQty() + inTransQty);
                    productOwnerListSource.setUsedQty(productOwnerListSource.getUsedQty() - inTransQty);
                    productOwnerRepository.save(productOwnerListSource);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }


    /**
     * Update item.
     * @param inwardId the inward id
     * @param status the status
     * @param sourceId the source id
     * @param sourceType the source type
     * @param destinationType the destination type
     * @param destinationId the destination id
     * @param batch
     */
    public void updateItem(Long inwardId, String status, Long sourceId, String sourceType, String destinationType, Long destinationId, boolean batch) {
//        System.out.println("Update Item Started");
        try {
            List<Long> itemIds = inOutWardMacRepo.findItemIdsByInwardId(inwardId);
            if (itemIds.isEmpty()) {
                return; // Exit early if no items found
            }
            List<Item> items = itemRepository.findAllById(itemIds);
            if (batch) {
                // Process batch updates
                batchUpdateItems(items, status, sourceId, sourceType, destinationType, destinationId, inwardId);
            } else {
                singleUpdateItems(items, status, sourceId, sourceType, destinationType, destinationId, inwardId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating item: " + e.getMessage(), e);
        }
    }

    private void singleUpdateItems(List<Item> items, String status, Long sourceId, String sourceType, String destinationType, Long destinationId, Long inwardId) {
//        System.out.println("Single Update Item Started");
        try {
            for (Item item : items) {
                if (CommonConstants.REJECTED.equalsIgnoreCase(status)) {
                    item.setOwnerId(sourceId);
                    item.setOwnerType(sourceType);
                } else if (CommonConstants.APPROVE.equalsIgnoreCase(status)) {
                    item.setCurrentInwardId(inwardId);
                    item.setOwnerId(destinationId);
                    item.setOwnerType(destinationType);
                    item.setItemStatus(destinationType.equalsIgnoreCase(CommonConstants.STAFF)
                            ? CommonConstants.STAFF_ALLOCATED
                            : CommonConstants.UNALLOCATED);
                }
                itemRepository.save(item);
            }
//            System.out.println("Single Update Item Ended");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Batch update items.
     *
     * @param items the items
     * @param status the status
     * @param sourceId the source id
     * @param sourceType the source type
     * @param destinationType the destination type
     * @param destinationId the destination id
     * @param inwardId the inward id
     */
    private void batchUpdateItems(List<Item> items, String status, Long sourceId, String sourceType, String destinationType, Long destinationId, Long inwardId) {
        int batchSize = Math.max(1000, items.size() / (Runtime.getRuntime().availableProcessors() * 2));
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < items.size(); i += batchSize) {
                int start = i;
                int end = Math.min(i + batchSize, items.size());
                List<Item> batch = items.subList(start, end);
                futures.add(executor.submit(() -> {
                    batch.forEach(item -> {
                        if (CommonConstants.REJECTED.equalsIgnoreCase(status)) {
//                            item.setCurrentInwardId(inwardId);
                            item.setOwnerId(sourceId);
                            item.setOwnerType(sourceType);
                        } else if (CommonConstants.APPROVE.equalsIgnoreCase(status)) {
                            item.setCurrentInwardId(inwardId);
                            item.setOwnerId(destinationId);
                            item.setOwnerType(destinationType);
                            item.setItemStatus(destinationType.equalsIgnoreCase(CommonConstants.STAFF)
                                    ? CommonConstants.STAFF_ALLOCATED
                                    : CommonConstants.UNALLOCATED);
                        }
                    });
                    itemRepository.saveAll(batch);
                }));
            }

            // Ensure all batches are processed
//            for (Future<?> future : futures) {
//                future.get();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            throw new RuntimeException("Batch update processing failed", e);
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }


    /**
     * Save inward of outward entity inward dto.
     *
     * @param entity      the entity
     * @param fromOutward the from outward
     * @param isReturned  the is returned
     * @return the inward dto
     * @throws Exception the exception
     */
    @Transactional
    public InwardDto saveInwardOfOutwardEntity(InwardDto entity, Boolean fromOutward, Boolean isReturned) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        InwardDto inwardDto = null;
        try {
            entity.setInwardNumber(getRandomenumber("IN", "-", "", getMvnoIdFromCurrentStaff()));
            //entityDTO.unusedQty=entityDTO.getQty();
            entity.setQty(0L);
            entity.setUnusedQty(0L);
            entity.setInTransitQty(entity.getInTransitQty());
            entity.setUsedQty(0L);
            entity.setOutTransitQty(0L);
            entity.setRejectedQty(0L);
            entity.setAssignNonSerializedItemQty(0L);
            entity.setApprovalStatus(CommonConstants.PENDING);

            if (!(entity.getTotalMacSerial() != null && entity.getTotalMacSerial() != 0))
                entity.setTotalMacSerial(0L);
            if (!isReturned)
                entity.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
            inwardDto = super.saveEntity(entity);

            if (!fromOutward) {
                // Managing quantities in product owner
                ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
                if (productOwner != null) {
                    productOwner.setQuantity(productOwner.getQuantity() + entity.getQty());
                    productOwner.setUnusedQty(productOwner.getUnusedQty() + entity.getQty());
                    productOwner.setUsedQty(productOwner.getUsedQty());
                    productOwner.setInTransitQty(productOwner.getInTransitQty() + entity.getInTransitQty());
                    productOwner.setProductId(entity.getProductId().getId());
                    productOwner.setOwnerId(entity.getDestinationId());
                    productOwner.setOwnerType(entity.getDestinationType());
                    productOwnerService.updateEntity(productOwner);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return inwardDto;
    }

    /**
     * Add in owner.
     * @param entity the entity
     * @param inward the inward
     * @param fromOutward the from outward
     * @throws Exception the exception
     */
    public void addInOwner(InwardDto entity, Inward inward, Boolean fromOutward) throws Exception {
//        System.out.println("********** Start Add InOwner **********");
        try {
            if (fromOutward) {
                // Managing quantities in product owner
                ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
                if (productOwner != null) {
                    updateProductOwnerByOutwardId(productOwner, inward, entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
//        System.out.println("********** End Add InOwner **********");
    }

    /**
     * Update product owner by outward id.
     *
     * @param productOwner the product owner
     * @param inward       the inward
     * @param entity       the entity
     * @throws Exception the exception
     */
    public void updateProductOwnerByOutwardId(ProductOwnerDto productOwner, Inward inward, InwardDto entity) throws Exception {
        try {
            if (entity.getOutwardId() == null && productOwner.getQuantity() == 0) {
                productOwner.setQuantity(entity.getQty());
                productOwner.setUnusedQty(entity.getQty());
                productOwner.setUsedQty(entity.getUsedQty());
                productOwner.setInTransitQty(entity.getInTransitQty());
                productOwnerService.updateEntity(productOwner);
            } else {
                updateUnUsedQty(inward, entity, productOwner);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update un used qty.
     * @param inward the inward
     * @param entity the entity
     * @param productOwner the product owner
     * @throws Exception the exception
     */
    public void updateUnUsedQty(Inward inward, InwardDto entity, ProductOwnerDto productOwner) throws Exception {
        //Set UnUsedQty
        try {
            if (entity.getUnusedQty() != null) {
                if (inward.getUnusedQty() > entity.getUnusedQty()) {
                    productOwner.setUnusedQty(productOwner.getUnusedQty() - (inward.getUnusedQty() - entity.getUnusedQty()));
                } else if (inward.getUnusedQty() < entity.getUnusedQty()) {
                    productOwner.setUnusedQty(productOwner.getUnusedQty() + (entity.getUnusedQty() - inward.getUnusedQty()));
                }
            }
            updateUsedQty(inward, entity, productOwner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update used qty.
     * @param inward the inward
     * @param entity the entity
     * @param productOwner the product owner
     * @throws Exception the exception
     */
    public void updateUsedQty(Inward inward, InwardDto entity, ProductOwnerDto productOwner) throws Exception {
        //Set UsedQty
        try {
            if (entity.getUsedQty() != null) {
                if (inward.getUsedQty() > entity.getUsedQty()) {
                    productOwner.setUsedQty(productOwner.getUsedQty() - (inward.getUsedQty() - entity.getUsedQty()));
                } else if (inward.getUsedQty() < entity.getUsedQty()) {
                    productOwner.setUsedQty(productOwner.getUsedQty() + (entity.getUsedQty() - inward.getUsedQty()));
                    productOwner.setInTransitQty(productOwner.getInTransitQty() - (entity.getInTransitQty() + inward.getOutTransitQty()));
                    productOwner.setUnusedQty(productOwner.getUnusedQty() - (inward.getOutTransitQty()));
                } else {
                    productOwner.setUsedQty(productOwner.getUsedQty());
                }
            }
            updateInTransitQty(inward, entity, productOwner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update in transit qty.
     * @param inward the inward
     * @param entity the entity
     * @param productOwner the product owner
     * @throws Exception the exception
     */
    public void updateInTransitQty(Inward inward, InwardDto entity, ProductOwnerDto productOwner) throws Exception {
        //Set InTransitQty
        try {
            if (entity.getInTransitQty() != null) {
                if (inward.getInTransitQty() > entity.getInTransitQty()) {
                    productOwner.setInTransitQty(productOwner.getInTransitQty() - (inward.getInTransitQty() - entity.getInTransitQty()));
                } else if (inward.getInTransitQty() < entity.getInTransitQty()) {
                    productOwner.setInTransitQty(productOwner.getInTransitQty() + (entity.getInTransitQty() - inward.getInTransitQty()));
                } else {
                    productOwner.setInTransitQty(productOwner.getInTransitQty());
                }
            }
            updateQty(entity, productOwner);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update qty.
     * @param entity the entity
     * @param productOwner the product owner
     * @throws Exception the exception
     */
    public void updateQty(InwardDto entity, ProductOwnerDto productOwner) throws Exception {
        productOwner.setQuantity(productOwner.getUsedQty() + productOwner.getUnusedQty());
        productOwnerService.updateEntity(productOwner);
    }

    /**
     * Save product owner after inward.
     *
     * @param entity      the entity
     * @param fromOutward the from outward
     * @throws Exception the exception
     */
    public void saveProductOwnerAfterInward(InwardDto entity, Boolean fromOutward) throws Exception {
        try {
            Product product = productRepository.findById(entity.getProductId().getId()).get();
            String uom = product.getProductCategory().getUnit();
            if (!fromOutward) {
                // Managing quantities in product owner
                ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
                if (productOwner != null) {
                    productOwner.setQuantity(productOwner.getQuantity() + entity.getQty());
                    productOwner.setUnusedQty(productOwner.getUnusedQty() + entity.getQty());
                    productOwner.setUsedQty(productOwner.getUsedQty());
                    productOwner.setInTransitQty(productOwner.getInTransitQty() + entity.getInTransitQty());
                    productOwner.setProductId(entity.getProductId().getId());
                    productOwner.setOwnerId(entity.getDestinationId());
                    productOwner.setOwnerType(entity.getDestinationType());
                    productOwnerService.updateEntity(productOwner);
                } else {
                    ProductOwnerDto productOwnerDto = new ProductOwnerDto();
                    productOwnerDto.setQuantity(entity.getQty());
                    productOwnerDto.setUnusedQty(entity.getQty());
                    productOwnerDto.setUsedQty(entity.getUsedQty());
                    if (uom.equalsIgnoreCase("kilometer")) {
                        productOwnerDto.setInTransitQty(1000 * entity.getInTransitQty());
                    } else {
                        productOwnerDto.setInTransitQty(entity.getInTransitQty());
                    }
                    productOwnerDto.setProductId(entity.getProductId().getId());
                    productOwnerDto.setOwnerId(entity.getDestinationId());
                    productOwnerDto.setOwnerType(entity.getDestinationType());
                    productOwnerService.saveEntity(productOwnerDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Save product owner after inward from rms.
     *
     * @param entity      the entity
     * @param fromOutward the from outward
     * @throws Exception the exception
     */
    public void saveProductOwnerAfterInwardFromRms(InwardDto entity, Boolean fromOutward) throws Exception {
        try {
            Product product = productRepository.findByName(entity.getProductId().getName());
            String uom = product.getProductCategory().getUnit();
            if (!fromOutward) {
                // Managing quantities in product owner
                ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
                if (productOwner != null) {
                    productOwner.setQuantity(productOwner.getQuantity() + entity.getQty());
                    productOwner.setUnusedQty(productOwner.getUnusedQty() + entity.getQty());
                    productOwner.setUsedQty(productOwner.getUsedQty());
                    productOwner.setInTransitQty(productOwner.getInTransitQty() + entity.getInTransitQty());
                    productOwner.setProductId(product.getId());
                    productOwner.setOwnerId(entity.getDestinationId());
                    productOwner.setOwnerType(entity.getDestinationType());
                    productOwnerService.saveEntityFromRms(productOwner);
                } else {
                    ProductOwnerDto productOwnerDto = new ProductOwnerDto();
                    productOwnerDto.setQuantity(entity.getQty());
                    productOwnerDto.setUnusedQty(entity.getQty());
                    productOwnerDto.setUsedQty(entity.getUsedQty());
                    if (uom.equalsIgnoreCase("kilometer")) {
                        productOwnerDto.setInTransitQty(1000 * entity.getInTransitQty());
                    } else {
                        productOwnerDto.setInTransitQty(entity.getInTransitQty());
                    }
                    productOwnerDto.setProductId(product.getId());
                    productOwnerDto.setOwnerId(entity.getDestinationId());
                    productOwnerDto.setOwnerType(entity.getDestinationType());
                    productOwnerService.saveEntityFromRms(productOwnerDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets entity by id.
     *
     * @param id the id
     * @return the entity by id
     */
    @Override
    public InwardDto getEntityById(Long id) {

        try {
            Inward inward = inwardRepository.findById(id).get();
            InwardDto inwardDto = getMapper().domainToDTO(inward, new CycleAvoidingMappingContext());

            //inwardDto.setItemList(itemRepository.findAllByInwardId(inward.getId().intValue()));

            if (inwardDto.getDestinationType().equals("") || (inwardDto.getDestinationType() == null)) {
                inwardDto.setDestination("");
            } else if (inwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
                inwardDto.setDestination(warehouseManagementRepository.findLightWarehouseById(inwardDto.getDestinationId()).getName());
            } else if (inwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.STAFF)) {
                inwardDto.setDestination(staffUserRepository.findLightStaffUserById(Math.toIntExact(inwardDto.getDestinationId())).map(staffUser -> staffUser.getFirstname() + " " + staffUser.getLastname()).orElse(null));
            } else if (inwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.PARTNER)) {
                inwardDto.setDestination(partnerRepository.findAllLightPartnerById(Math.toIntExact(inwardDto.getDestinationId())).getName());
            }

            if ((inwardDto.getSourceType() == null) || (inwardDto.getSourceType().equals(""))) {
                inwardDto.setSource("");
            } else if (inwardDto.getSourceType().equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
                inwardDto.setSource(warehouseManagementRepository.findLightWarehouseById(inwardDto.getSourceId()).getName());
            } else if (inwardDto.getSourceType().equalsIgnoreCase(CommonConstants.STAFF)) {
                inwardDto.setSource(staffUserRepository.findLightStaffUserById(Math.toIntExact(inwardDto.getSourceId())).map(staffUser -> staffUser.getFirstname() + " " + staffUser.getLastname()).orElse(null));
            } else if (inwardDto.getSourceType().equalsIgnoreCase(CommonConstants.PARTNER)) {
                inwardDto.setSource(partnerRepository.findAllLightPartnerById(Math.toIntExact(inwardDto.getSourceId())).getName());
            }
            List<SpecificationParametersDTO> specificationParametersDTOList = new ArrayList<>();
            List<InventorySpecification> inventorySpecificationList = inventorySpecificationRepo.findAllByInward_Id(inwardDto.getId());
            if (!inventorySpecificationList.isEmpty()) {
                Collections.reverse(inventorySpecificationList);
                inventorySpecificationList = inventorySpecificationList.stream().filter(distinctByKey(p -> p.getSpecificationParameters().getId())).collect(Collectors.toList());
                for (InventorySpecification item : inventorySpecificationList) {
                    SpecificationParameters parameters = specificatioParametersRepo.findById(item.getSpecificationParameters().getId()).orElse(null);
                    SpecificationParametersDTO specificationParametersDTO = new SpecificationParametersDTO();
                    specificationParametersDTO.setId(item.getSpecificationParameters().getId());
                    specificationParametersDTO.setPcid(item.getSpecificationParameters().getProductCategory().getId());
                    specificationParametersDTO.setParamName(item.getSpecificationParameters().getParamName());
                    specificationParametersDTO.setIsMandatory(item.getSpecificationParameters().getIsMandatory());
                    specificationParametersDTO.setMvnoId(item.getSpecificationParameters().getMvnoId());
                    specificationParametersDTO.setParamValue(item.getParamValue());
                    specificationParametersDTO.setParamValues(parameters.getParamValues());

                    if (parameters.getIsMultiValueParam() != null && parameters.getIsMultiValueParam().equals(true))
                        specificationParametersDTO.setIsMultiValueParam(true);
                    else
                        specificationParametersDTO.setIsMultiValueParam(false);
                    if (parameters.getIsMultiValueParam() != null && parameters.getIsMultiValueParam() && parameters.getParamValues() != null && !parameters.getParamValues().isEmpty())
                        specificationParametersDTO.setParamMultiValues(Arrays.asList(parameters.getParamValues().split(",", -1)));

                    specificationParametersDTOList.add(specificationParametersDTO);
                }
            }
            if (!specificationParametersDTOList.isEmpty()) {
                inwardDto.setSpecificationParametersDTOList(specificationParametersDTOList);
            }
            return inwardDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update inward of outward status.
     *
     * @param outwardId the outward id
     * @param status    the status
     * @throws Exception the exception
     */
    public void updateInwardOfOutwardStatus(Long outwardId, String status) throws Exception {
        try {
            QInward qInward = QInward.inward;
            BooleanExpression booleanExpression = qInward.isDeleted.eq(false).and(qInward.outwardId.id.eq(outwardId));
            List<Inward> inwardList = IterableUtils.toList(inwardRepository.findAll(booleanExpression));
            Long inwardId = inwardList.get(0).getId();
            InwardDto inwardDto = getEntityForUpdateAndDelete(inwardId);
            inwardDto.setStatus(status);
            inwardService.updateEntity(inwardDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Validate inward.
     *
     * @param inwardDto the inward dto
     * @throws Exception the exception
     */
    public void validateInward(InwardDto inwardDto) throws Exception {
        try {
            if (inwardDto.getSpecificationParametersDTOList() != null) {
                boolean hasInvalidParam = inwardDto.getSpecificationParametersDTOList().stream()
                        .anyMatch(param ->
                                Boolean.TRUE.equals(param.getIsMandatory()) &&
                                        StringUtils.isEmpty(param.getParamValue()));

                if (hasInvalidParam) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please enter mandatory values.", null);
                }
            }

            productRepository.findById(inwardDto.getProductId().getId())
                    .filter(Product::isHasOEMConsider)
                    .ifPresent(product -> {
                        if (inwardDto.getStartDateTime() == null) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please enter warranty start date", null);
                        }
                    });
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Validate Inward Mac Method
     *
     * @param inwardSaveMacSerialDTO the inward save mac serial dto
     * @param hasSerial the has serial
     * @param hasMac the has mac
     * @param inTrasQty the in tras qty
     * @param entity the entity
     * @param manualMacAdd the manual mac add
     * @throws Exception the exception
     */
    public void validateInwardMAC(InwardSaveMacSerialDTO inwardSaveMacSerialDTO, boolean hasSerial,
                                  boolean hasMac, Long inTrasQty, Inward entity,
                                  boolean manualMacAdd) throws Exception {
        try {
            List<MacSerialListDTO> macSerialList = inwardSaveMacSerialDTO.getMacSerialListDTOList();
            if (macSerialList.isEmpty()) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please add Mac/Serial", null);
            }
            Set<String> macAddressSet = new HashSet<>();
            Set<String> duplicateMacs = new HashSet<>();
            Set<String> serialNumbers = new HashSet<>();
            for (MacSerialListDTO dto : macSerialList) {
                String macAddress = dto.getMacAddress();
                String serialNumber = dto.getSerialNumber();
                if (hasSerial && (serialNumber == null || serialNumber.trim().isEmpty())) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please add Serial Number", null);
                }
                if (hasMac && macAddress != null && !macAddress.trim().isEmpty()) {
                    String normalizedMac = macAddress.trim().toLowerCase();
                    if (!macAddressSet.add(normalizedMac)) {
                        duplicateMacs.add(macAddress);
                    }
                    if (manualMacAdd) {
                        validateMacSerial(macAddress);
                    }
                }
            }

            if (!duplicateMacs.isEmpty()) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                        "Duplicate MAC address detected: " + duplicateMacs.iterator().next(), null);
            }
            if (macSerialList.size() > inTrasQty && !manualMacAdd) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                        "Total entered Mac/Serial: " + macSerialList.size() + " are greater than inward intransit qty: " + inTrasQty, null);
            }
            if ((entity.getTotalMacSerial() + macSerialList.size()) > inTrasQty && manualMacAdd) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                        "Total Mac/Serial: " + (entity.getTotalMacSerial() + macSerialList.size()) + " are greater than inward intransit qty: " + inTrasQty, null);
            }
            if (hasMac && !macAddressSet.isEmpty()) {
                List<String> existingMacs = itemRepository.findExistingMacs(macAddressSet, inwardSaveMacSerialDTO.getInwardId(), getMvnoIdFromCurrentStaff());
                if (!existingMacs.isEmpty()) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                            "Entered MAC(s) " + String.join(", ", existingMacs) + " already exist", null);
                }
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Validate mac serial.
     *
     * @param mac the mac
     */
    private void validateMacSerial(String mac) {
        try {
            List<Integer> mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
            if (!Objects.equals(mac, null)) {
                mac = mac.trim();
                Integer count;
                if (getMvnoIdFromCurrentStaff() == 1)
                    count = inOutWardMacRepo.duplicateVerifyAtSave(mac);
                else
                    count = inOutWardMacRepo.duplicateVerifyAtSave(mac, mvnoIds);
                if (count != 0) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Entered a MAC " + mac + " is Already Exist", null);
                }
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * Reverse case string.
     *
     * @param s the s
     * @return the string
     */
    private String reverseCase(String s) {
        try {
            StringBuilder result = new StringBuilder(s.length());
            for (char c : s.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    result.append(Character.toLowerCase(c));
                } else if (Character.isLowerCase(c)) {
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets in ward by id.
     *
     * @param id the id
     * @return the in ward by id
     */
    public Inward getInWardById(long id) {
        return inwardRepository.findById(id).get();
    }
}
