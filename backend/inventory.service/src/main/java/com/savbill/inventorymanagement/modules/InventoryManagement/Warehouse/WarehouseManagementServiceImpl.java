package com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.SearchConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.domain.ProductWarehouseMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.model.ProductWarehouseMapViewDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.model.ProductWarehouseMappingDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductWarehouseMapping.repository.ProductWarehouseMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory.RequestInventory;
import com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory.RequestInventoryRepo;
import com.savbill.inventorymanagement.modules.MasterManagement.Branch.BranchRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.City.CityRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.Country.CountryRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.Pincode.PincodeRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.*;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.*;
import com.savbill.inventorymanagement.modules.MasterManagement.State.StateRepository;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.*;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.*;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveWarehouseTeamMappingSharedMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateWarehouseTeamMappingSharedMessage;
import com.savbill.inventorymanagement.security.spring.SpringContext;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import springfox.documentation.swagger2.mappers.ModelMapper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Warehouse management service.
 */
@Service
public class WarehouseManagementServiceImpl extends ExBaseAbstractService<WareHouseDto, WareHouse, Long> {
    /**
     * The Warehouse management repository.
     */
    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;
    /**
     * The Service area repository.
     */
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    /**
     * The Service area service.
     */
    @Autowired
    ServiceAreaService serviceAreaService;
    /**
     * The Service area mapper.
     */
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;
    /**
     * The Ware house managment service areamapping repo.
     */
    @Autowired
    WareHouseManagmentServiceAreamappingRepo wareHouseManagmentServiceAreamappingRepo;
    /**
     * The Model mapper.
     */
    @Autowired
    private ModelMapper modelMapper;
    /**
     * The Pincode repository.
     */
    @Autowired
    PincodeRepository pincodeRepository;
    /**
     * The City repository.
     */
    @Autowired
    CityRepository cityRepository;
    /**
     * The State repository.
     */
    @Autowired
    StateRepository stateRepository;
    /**
     * The Country repository.
     */
    @Autowired
    CountryRepository countryRepository;
    /**
     * The Branch repository.
     */
    @Autowired
    BranchRepository branchRepository;
    /**
     * The Teams repository.
     */
    @Autowired
    TeamsRepository teamsRepository;
    /**
     * The Ware house teams mapping repo.
     */
    @Autowired
    WareHouseTeamsMappingRepo wareHouseTeamsMappingRepo;
    /**
     * The Entity manager.
     */
    @PersistenceContext
    EntityManager entityManager;

    /**
     * The Ware house parent service area map repo.
     */
    @Autowired
    WareHouseParentServiceAreaMapRepo wareHouseParentServiceAreaMapRepo;

    /**
     * The Warehouse mapper.
     */
    @Autowired
    WarhouseMapper warehouseMapper;

    /**
     * The Product warehouse mapping repo.
     */
    @Autowired
    ProductWarehouseMappingRepo productWarehouseMappingRepo;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    /**
     * The Teams service.
     */
    @Autowired
    private TeamsService teamsService;

    /**
     * The Teams mapper.
     */
    @Autowired
    private TeamsMapper teamsMapper;
    /**
     * The Inward repository.
     */
    @Autowired
    private InwardRepository inwardRepository;
    /**
     * The Request inventory repo.
     */
    @Autowired
    private RequestInventoryRepo requestInventoryRepo;

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
     * Instantiates a new Warehouse management service.
     * @param warehouseManagementRepository the warehouse management repository
     * @param mapper the mapper
     */
    public WarehouseManagementServiceImpl(WarehouseManagementRepository warehouseManagementRepository, IBaseMapper<WareHouseDto, WareHouse> mapper) {
        super(warehouseManagementRepository, mapper);
    }

    /**
     * Gets module name for log.
     * @return the module name for log
     */
    @Override
    public String getModuleNameForLog() {
        return "[WarehouseManagementServiceImpl]";
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
        String SUBMODULE = getModuleNameForLog() + "[getListByPageAndSizeAndSortByAndOrderBy()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
        ;
        Page<WareHouse> paginationList = null;
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                paginationList = warehouseManagementRepository.findAllByIsDeletedIsFalse(pageRequest);
            } else {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
                if (!serviceAreaIds.isEmpty()) {
                    List<Long> ids = wareHouseManagmentServiceAreamappingRepo.findAllByServiceIdIn(serviceAreaIds).stream().map(WareHouseServiceAreaMapping::getWarehouseId).collect(Collectors.toList());
                    paginationList = warehouseManagementRepository.findAllByIsDeletedIsFalseAndIdInAndMvnoIdIn(ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
                } else {
                    paginationList = warehouseManagementRepository.findAllByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
                }
            }
            if (paginationList.getSize() > 0) {
                paginationList.stream().forEach(x -> {
                    DecimalFormat df = new DecimalFormat("0.0000000");
                    if (x.getLatitude() != null && !x.getLatitude().isEmpty() && isNumeric(x.getLatitude()))
                        x.setLatitude(df.format(Double.parseDouble(x.getLatitude())));
                    if (x.getLongitude() != null && !x.getLongitude().isEmpty() && isNumeric(x.getLongitude()))
                        x.setLongitude(df.format(Double.parseDouble(x.getLongitude())));
                });
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
     * Gets all active warehouse.
     * @return the all active warehouse
     */
    GenericDataDTO getAllActiveWarehouse() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Long> Ids = null;
        List<WareHouse> wareHouseList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                wareHouseList = warehouseManagementRepository.findAllByIsDeletedIsFalseWithoutPageable();
            } else {
                List<Integer> serviceAreaIdIDs = serviceAreaService.getServiceAreaByStaffId();
                if (!serviceAreaIdIDs.isEmpty()) {
                    Ids = wareHouseManagmentServiceAreamappingRepo.findWarehouseIdsBySAIds(serviceAreaIdIDs);
                    wareHouseList = warehouseManagementRepository.findAllByIdInAndIsDeletedIsFalseAndMvnoIdInWithoutPageable(Ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    wareHouseList = warehouseManagementRepository.findAllByIsDeletedIsFalseAndMvnoIdInWithoutPageable(Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
            List<WareHouseDto> wareHouseDtoList = wareHouseList.stream().map(wareHouse -> warehouseMapper.domainToDTO(wareHouse, new CycleAvoidingMappingContext()))
                    .sorted(Comparator.comparing(WareHouseDto::getId).reversed()).collect(Collectors.toList());
            wareHouseDtoList.stream().forEach(x -> {
                DecimalFormat df = new DecimalFormat("0.0000000");
                if (x.getLatitude() != null && !x.getLatitude().isEmpty() && isNumeric(x.getLatitude()))
                    x.setLatitude(df.format(Double.parseDouble(x.getLatitude())));
                if (x.getLongitude() != null && !x.getLongitude().isEmpty() && isNumeric(x.getLongitude()))
                    x.setLongitude(df.format(Double.parseDouble(x.getLongitude())));
            });
            genericDataDTO.setDataList(wareHouseDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }

    /**
     * Compare psa ids and sa ids ware house dto.
     * @param wareHouseDto the ware house dto
     * @param warehouseOperation the warehouse operation
     * @return the ware house dto
     * @throws Exception the exception
     */
    public WareHouseDto validateEntity(WareHouseDto wareHouseDto, String warehouseOperation) throws Exception {
        try {
            wareHouseDto.setMvnoId(getMvnoIdFromCurrentStaff());
            // Validate required fields
            if ("3PL".equalsIgnoreCase(wareHouseDto.getWarehouseType())) {
                if (wareHouseDto.getParentServiceAreaIdsList() == null) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please Select Parent Service Area", null);
                }
                if (wareHouseDto.getServiceAreaIdsList() == null) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please Select Service Area", null);
                }
            } else if ("OWN".equalsIgnoreCase(wareHouseDto.getWarehouseType()) && wareHouseDto.getServiceAreaIdsList() == null) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please Select Service Area", null);
            }
            // Only proceed if both lists are non-null
            if (wareHouseDto.getWarehouseType().equalsIgnoreCase("3PL") &&
                    wareHouseDto.getParentServiceAreaIdsList() != null &&
                    wareHouseDto.getServiceAreaIdsList() != null) {
                Set<Long> parentServiceAreaIds = new HashSet<>(wareHouseDto.getParentServiceAreaIdsList());
                Set<Long> serviceAreaIds = new HashSet<>(wareHouseDto.getServiceAreaIdsList());
                // Check for duplicates
                for (Long parentSAId : parentServiceAreaIds) {
                    if (serviceAreaIds.contains(parentSAId)) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please select different Service Area", null);
                    }
                }
            }
            if (wareHouseDto.getProductWarehouseMappingDTOS() != null) {
                List<ProductWarehouseMappingDTO> mappingDTOs = wareHouseDto.getProductWarehouseMappingDTOS();
                Set<Long> seenProductIds = new HashSet<>();
                for (ProductWarehouseMappingDTO dto : mappingDTOs) {
                    if (!seenProductIds.add(dto.getProductId())) {
                        String productName = productRepository.findProductNameByProductId(dto.getProductId());
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Duplicate product found with name: " + productName, null);
                    }
                }
            }
            return wareHouseDto;
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Save entity ware house dto.
     * @param wareHouseDto the ware house dto
     * @return the ware house dto
     * @throws Exception the exception
     */
    @Override
    public WareHouseDto saveEntity(WareHouseDto wareHouseDto) throws Exception {
        try {
            if (wareHouseDto.getServiceAreaIdsList().size() > 0) {
                wareHouseDto.setMvnoId(getMvnoIdFromCurrentStaff());
                WareHouse wareHouse = new WareHouse();
                if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                    List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                    List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                    for (int i = 0; i < wareHouseDto.getServiceAreaIdsList().size(); i++) {
                        serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                    }
                    wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                }
                List<TeamsDTO> teamsDTOS = new ArrayList<>();
                if (wareHouseDto.getTeamsIdsList() != null && wareHouseDto.getTeamsIdsList().size() > 0) {
                    List<Teams> teams = teamsRepository.findAllById(wareHouseDto.getTeamsIdsList());
                    for (int i = 0; i < wareHouseDto.getTeamsIdsList().size(); i++) {
                        teamsDTOS.add(teamsMapper.domainToDTO(teams.get(i), new CycleAvoidingMappingContext()));
                    }
                }
                WareHouse house = warehouseManagementRepository.save(warehouseMapper.dtoToDomain(wareHouseDto, new CycleAvoidingMappingContext()));
                WareHouseDto warehouseDto = warehouseMapper.domainToDTO(house, new CycleAvoidingMappingContext());
                //Todo: Code for Warehouse for Integration
    //            messageSender.send(warehouseDto, RabbitMqConstants.QUEUE_WAREHOUSE_INTEGRATOIN);
                warehouseDto.setTeamsDTOList(teamsDTOS);
                // To check warehouse type 3PL
                if (wareHouseDto.getWarehouseType().equalsIgnoreCase("3PL")) {
                    warehouseDto = saveThreePL(wareHouseDto, warehouseDto, wareHouse);
                }
                // To check warehouse type OWN
                if (wareHouseDto.getWarehouseType().equalsIgnoreCase("OWN")) {
                    warehouseDto = saveOWN(wareHouseDto, warehouseDto, wareHouse);
                }
                if (wareHouseDto.getProductWarehouseMappingDTOS() != null) {
                    warehouseDto = saveOrUpdateProductWarehouseMapping(house.getId(), wareHouseDto, warehouseDto, CommonConstants.OPERATION_ADD);
                }
                return warehouseDto;
            } else {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Service Area is Mandatory", null);
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Save own ware house dto.
     * @param wareHouseDto the ware house dto
     * @param warehouseDto the warehouse dto
     * @param wareHouse the ware house
     * @return the ware house dto
     */
    private WareHouseDto saveOWN(WareHouseDto wareHouseDto, WareHouseDto warehouseDto, WareHouse wareHouse) {
        try {
            if (wareHouseDto.getServiceAreaIdsList() != null) {
                wareHouse.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
                //wareHouseDto.setServiceAreaNameList(wareHouse.getServiceAreaNameList());
                if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                    List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                    List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                    for (int i = 0; i < wareHouseDto.getServiceAreaIdsList().size(); i++) {
                        serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                    }
                    wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                }
                Set<Long> saIds = new HashSet<>();
                if (wareHouseDto.getParentServiceAreaIdsList() != null && wareHouseDto.getParentServiceAreaIdsList().size() > 0)
                    saIds.addAll(wareHouseDto.getParentServiceAreaIdsList());
                if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0)
                    saIds.addAll(wareHouseDto.getServiceAreaIdsList());
                List<ServiceAreaDTO> parentSAList = new ArrayList<>();
                for (Long id : saIds) {
                    WareHouseParentServiceAreaMapping wareHouseParentServiceAreaMapping = new WareHouseParentServiceAreaMapping();
                    wareHouseParentServiceAreaMapping.setWarehouseId(warehouseDto.getId());
                    wareHouseParentServiceAreaMapping.setParentServiceAreaId(id.intValue());
                    wareHouseParentServiceAreaMapRepo.save(wareHouseParentServiceAreaMapping);
                    parentSAList.add(serviceAreaMapper.domainToDTO(serviceAreaRepository.findById(id).get(), new CycleAvoidingMappingContext()));
                }
                warehouseDto.setParenetServiceAreaNameList(parentSAList);
            }
            return warehouseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Save three pl ware house dto.
     * @param wareHouseDto the ware house dto
     * @param warehouseDto the warehouse dto
     * @param wareHouse the ware house
     * @return the ware house dto
     */
    private WareHouseDto saveThreePL(WareHouseDto wareHouseDto, WareHouseDto warehouseDto, WareHouse wareHouse) {
        try {
            if (wareHouseDto.getParentServiceAreaIdsList() != null) {
                List<WareHouseParentServiceAreaMapping> wareHouseParentServiceAreaMappings = wareHouseParentServiceAreaMapRepo.findAllByWarehouseId(warehouseDto.getId());
                if (!wareHouseParentServiceAreaMappings.isEmpty()) {
                    wareHouseParentServiceAreaMapRepo.deleteAll(wareHouseParentServiceAreaMappings);
                }
                List<ServiceAreaDTO> parentSAList = new ArrayList<>();
                for (Long id : wareHouseDto.getParentServiceAreaIdsList()) {
                    WareHouseParentServiceAreaMapping wareHouseParentServiceAreaMapping = new WareHouseParentServiceAreaMapping();
                    wareHouseParentServiceAreaMapping.setWarehouseId(warehouseDto.getId());
                    wareHouseParentServiceAreaMapping.setParentServiceAreaId(id.intValue());
                    wareHouseParentServiceAreaMapRepo.save(wareHouseParentServiceAreaMapping);
                    parentSAList.add(serviceAreaMapper.domainToDTO(serviceAreaRepository.findById(id).get(), new CycleAvoidingMappingContext()));
                }
                if (wareHouseDto.getServiceAreaIdsList() != null) {
                    wareHouse.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
                    //wareHouseDto.setServiceAreaNameList(wareHouse.getServiceAreaNameList());
                    if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                        List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                        List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                        for (int i = 0; i < wareHouseDto.getServiceAreaIdsList().size(); i++) {
                            serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                        }
                        wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                    }
                }
                warehouseDto.setParenetServiceAreaNameList(parentSAList);
            } else {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Parent Service Area is Mandatory", null);
            }
            return warehouseDto;
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Save or update product warehouse mapping ware house dto.
     * @param warehouseId the warehouse id
     * @param inputDto the input dto
     * @param responseDto the response dto
     * @param operationAdd
     * @return the ware house dto
     */
    private WareHouseDto saveOrUpdateProductWarehouseMapping(Long warehouseId, WareHouseDto inputDto, WareHouseDto responseDto, Integer operation) {
        try {
            List<ProductWarehouseMappingDTO> mappingDTOs = inputDto.getProductWarehouseMappingDTOS();
            List<ProductWarehouseMapping> resultMappings = new ArrayList<>();
            if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                deleteProductWarehouseMapping(warehouseId);
            }
            for (ProductWarehouseMappingDTO dto : mappingDTOs) {
                ProductWarehouseMapping newMapping = new ProductWarehouseMapping();
                newMapping.setProductId(dto.getProductId());
                newMapping.setWarehouseId(warehouseId);
                newMapping.setThresholdQty(dto.getThresholdQty());
                newMapping.setMvnoId(dto.getMvnoId().longValue());
                resultMappings.add(newMapping);
            }
            if (!resultMappings.isEmpty()) {
                productWarehouseMappingRepo.saveAll(resultMappings);
            }
            responseDto.setProductWarehouseMappingDTOS(mappingDTOs);
            return responseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update entity ware house dto.
     * @param wareHouseDto the ware house dto
     * @return the ware house dto
     * @throws Exception the exception
     */
    @Override
    public WareHouseDto updateEntity(WareHouseDto wareHouseDto) throws Exception {
        try {
            getEntityForUpdateAndDelete(wareHouseDto.getId());
            WareHouse wareHouse = new WareHouse();
            if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                for (int i = 0; i < wareHouseDto.getServiceAreaIdsList().size(); i++) {
                    serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                }
                wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
            }
            List<TeamsDTO> teamsDTOS = new ArrayList<>();

            if (wareHouseDto.getTeamsIdsList() != null && wareHouseDto.getTeamsIdsList().size() > 0) {
                List<Teams> teams = teamsRepository.findAllById(wareHouseDto.getTeamsIdsList());
                for (int i = 0; i < wareHouseDto.getTeamsIdsList().size(); i++) {
                    teamsDTOS.add(teamsMapper.domainToDTO(teams.get(i), new CycleAvoidingMappingContext()));
                }
            }
            WareHouse house = warehouseManagementRepository.save(warehouseMapper.dtoToDomain(wareHouseDto, new CycleAvoidingMappingContext()));
            WareHouseDto warehouseDto = super.updateEntity(warehouseMapper.domainToDTO(house, new CycleAvoidingMappingContext()));
            warehouseDto.setTeamsDTOList(teamsDTOS);
            // To check warehouse type 3PL
            if (wareHouseDto.getWarehouseType().equalsIgnoreCase("3PL")) {
                warehouseDto = updateThreePL(wareHouseDto, warehouseDto, wareHouse);
            }
            // To check warehouse type OWN
            if (wareHouseDto.getWarehouseType().equalsIgnoreCase("OWN")) {
                warehouseDto = updateOwn(wareHouseDto, warehouseDto, wareHouse);
            }
            if (wareHouseDto.getProductWarehouseMappingDTOS() != null) {
                warehouseDto = saveOrUpdateProductWarehouseMapping(house.getId(), wareHouseDto, warehouseDto, CommonConstants.OPERATION_UPDATE);
            }
            return warehouseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update own ware house dto.
     * @param wareHouseDto the ware house dto
     * @param warehouseDto the warehouse dto
     * @param wareHouse the ware house
     * @return the ware house dto
     */
    private WareHouseDto updateOwn(WareHouseDto wareHouseDto, WareHouseDto warehouseDto, WareHouse wareHouse) {
        try {
            if (wareHouseDto.getServiceAreaIdsList() != null) {
                wareHouse.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
                if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                    List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                    List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                    for (int i = 0; i < wareHouseDto.getServiceAreaIdsList().size(); i++) {
                        serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                    }
                    wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                }
                Set<Long> saIds = new HashSet<>();
                if (wareHouseDto.getParentServiceAreaIdsList() != null && wareHouseDto.getParentServiceAreaIdsList().size() > 0)
                    saIds.addAll(wareHouseDto.getParentServiceAreaIdsList());
                if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0)
                    saIds.addAll(wareHouseDto.getServiceAreaIdsList());
                List<ServiceAreaDTO> parentSAList = new ArrayList<>();
                if (wareHouseDto.getParentServiceAreaIdsList() != null && wareHouseDto.getParentServiceAreaIdsList().size() > 0) {
                    List<WareHouseParentServiceAreaMapping> wareHouseParentServiceAreaMappings = wareHouseParentServiceAreaMapRepo.findAllByWarehouseId(wareHouseDto.getId());
                    if (!wareHouseParentServiceAreaMappings.isEmpty()) {
                        wareHouseParentServiceAreaMapRepo.deleteAll(wareHouseParentServiceAreaMappings);
                    }
                    for (Long id : saIds) {
                        WareHouseParentServiceAreaMapping wareHouseParentServiceAreaMapping = new WareHouseParentServiceAreaMapping();
                        wareHouseParentServiceAreaMapping.setWarehouseId(warehouseDto.getId());
                        wareHouseParentServiceAreaMapping.setParentServiceAreaId(id.intValue());
                        wareHouseParentServiceAreaMapRepo.save(wareHouseParentServiceAreaMapping);
                        parentSAList.add(serviceAreaMapper.domainToDTO(serviceAreaRepository.findById(id).get(), new CycleAvoidingMappingContext()));
                    }
                    warehouseDto.setParenetServiceAreaNameList(parentSAList);
                }
            }
            return warehouseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Update three pl ware house dto.
     * @param wareHouseDto the ware house dto
     * @param warehouseDto the warehouse dto
     * @param wareHouse the ware house
     * @return the ware house dto
     */
    private WareHouseDto updateThreePL(WareHouseDto wareHouseDto, WareHouseDto warehouseDto, WareHouse wareHouse) {
        try {
            if (wareHouseDto.getParentServiceAreaIdsList() != null) {
                List<WareHouseParentServiceAreaMapping> wareHouseParentServiceAreaMappings = wareHouseParentServiceAreaMapRepo.findAllByWarehouseId(warehouseDto.getId());
                if (!wareHouseParentServiceAreaMappings.isEmpty()) {
                    wareHouseParentServiceAreaMapRepo.deleteAll(wareHouseParentServiceAreaMappings);
                }
                List<ServiceAreaDTO> parentSAList = new ArrayList<>();
                for (Long id : wareHouseDto.getParentServiceAreaIdsList()) {
                    WareHouseParentServiceAreaMapping wareHouseParentServiceAreaMapping = new WareHouseParentServiceAreaMapping();
                    wareHouseParentServiceAreaMapping.setWarehouseId(warehouseDto.getId());
                    wareHouseParentServiceAreaMapping.setParentServiceAreaId(id.intValue());
                    wareHouseParentServiceAreaMapRepo.save(wareHouseParentServiceAreaMapping);
                    parentSAList.add(serviceAreaMapper.domainToDTO(serviceAreaRepository.findById(id).get(), new CycleAvoidingMappingContext()));
                }
                warehouseDto.setParenetServiceAreaNameList(parentSAList);
                if (wareHouseDto.getServiceAreaIdsList() != null) {
                    wareHouse.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
                    if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                        List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                        List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                        for (int i = 0; i < wareHouseDto.getServiceAreaIdsList().size(); i++) {
                            serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                        }
                        wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                    }
                }
            }
            return warehouseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets ownwarehouse dto.
     * @param warehouseDto the warehouse dto
     * @param wareHouseDto the ware house dto
     * @return the ownwarehouse dto
     */
    private WareHouseDto setOwnwarehouseDto(WareHouseDto warehouseDto, WareHouseDto wareHouseDto) {
//        long startTime = System.currentTimeMillis();
        try {
            if (wareHouseDto.getServiceAreaIdsList() != null) {
    //                wareHouse.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
                if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                    List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                    List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                    for (int i = 0; i < wareHouseDto.getServiceAreaIdsList().size(); i++) {
                        serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                    }
                    wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                }
                Set<Long> saIds = new HashSet<>();
                if (wareHouseDto.getParentServiceAreaIdsList() != null && wareHouseDto.getParentServiceAreaIdsList().size() > 0)
                    saIds.addAll(wareHouseDto.getParentServiceAreaIdsList());
                if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0)
                    saIds.addAll(wareHouseDto.getServiceAreaIdsList());
                List<ServiceAreaDTO> parentSAList = new ArrayList<>();
                if (wareHouseDto.getParentServiceAreaIdsList() != null && wareHouseDto.getParentServiceAreaIdsList().size() > 0) {
                    List<WareHouseParentServiceAreaMapping> wareHouseParentServiceAreaMappings = wareHouseParentServiceAreaMapRepo.findAllByWarehouseId(wareHouseDto.getId());
                    if (!wareHouseParentServiceAreaMappings.isEmpty()) {
                        wareHouseParentServiceAreaMapRepo.deleteAll(wareHouseParentServiceAreaMappings);
                    }
                    for (Long id : saIds) {
                        WareHouseParentServiceAreaMapping wareHouseParentServiceAreaMapping = new WareHouseParentServiceAreaMapping();
                        wareHouseParentServiceAreaMapping.setWarehouseId(warehouseDto.getId());
                        wareHouseParentServiceAreaMapping.setParentServiceAreaId(id.intValue());
                        wareHouseParentServiceAreaMapRepo.save(wareHouseParentServiceAreaMapping);
                        parentSAList.add(serviceAreaMapper.domainToDTO(serviceAreaRepository.findById(id).get(), new CycleAvoidingMappingContext()));
                    }
                    warehouseDto.setParenetServiceAreaNameList(parentSAList);
                }
            }
//        long endTime = System.currentTimeMillis();
//        System.out.println("Time taken to set OWN warehouse: " + (endTime - startTime) + " ms");
            return warehouseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets 3 p lwarehouse dto.
     * @param warehouseDto the warehouse dto
     * @param wareHouseDto the ware house dto
     * @return the 3 p lwarehouse dto
     */
    private WareHouseDto set3PLwarehouseDto(WareHouseDto warehouseDto, WareHouseDto wareHouseDto) {
//        long startTime = System.currentTimeMillis();
        try {
            if (wareHouseDto.getParentServiceAreaIdsList() != null) {
                List<WareHouseParentServiceAreaMapping> wareHouseParentServiceAreaMappings = wareHouseParentServiceAreaMapRepo.findAllByWarehouseId(warehouseDto.getId());
                if (!wareHouseParentServiceAreaMappings.isEmpty()) {
                    wareHouseParentServiceAreaMapRepo.deleteAll(wareHouseParentServiceAreaMappings);
                }
                List<ServiceAreaDTO> parentSAList = new ArrayList<>();
                for (Long id : wareHouseDto.getParentServiceAreaIdsList()) {
                    WareHouseParentServiceAreaMapping wareHouseParentServiceAreaMapping = new WareHouseParentServiceAreaMapping();
                    wareHouseParentServiceAreaMapping.setWarehouseId(warehouseDto.getId());
                    wareHouseParentServiceAreaMapping.setParentServiceAreaId(id.intValue());
                    wareHouseParentServiceAreaMapRepo.save(wareHouseParentServiceAreaMapping);
                    parentSAList.add(serviceAreaMapper.domainToDTO(serviceAreaRepository.findById(id).get(), new CycleAvoidingMappingContext()));
                }
                warehouseDto.setParenetServiceAreaNameList(parentSAList);
                if (wareHouseDto.getServiceAreaIdsList() != null) {
    //                    wareHouse.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
                    if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                        List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                        List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                        for (int i = 0; i < wareHouseDto.getServiceAreaIdsList().size(); i++) {
                            serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                        }
                        wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                    }
                }
            }
//        long endTime = System.currentTimeMillis();
//        System.out.println("Time taken to fetch warehouse: " + (endTime - startTime) + " ms");
            return warehouseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets teams dto.
     * @param wareHouseDto the ware house dto
     * @return the teams dto
     */
    private List<TeamsDTO> getTeamsDto(WareHouseDto wareHouseDto) {
//        long startTime = System.currentTimeMillis();
        try {
            List<TeamsDTO> teamsDTOS = new ArrayList<>();
            if (wareHouseDto.getTeamsIdsList() != null && wareHouseDto.getTeamsIdsList().size() > 0) {
                List<Teams> teams = teamsRepository.findAllById(wareHouseDto.getTeamsIdsList());
                for (int i = 0; i < wareHouseDto.getTeamsIdsList().size(); i++) {
                    teamsDTOS.add(teamsMapper.domainToDTO(teams.get(i), new CycleAvoidingMappingContext()));
                }
            }
//        long endTime = System.currentTimeMillis();
//        System.out.println("Time taken to fetch teams: " + (endTime - startTime) + " ms");
            return teamsDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets service area name list.
     * @param wareHouseDto the ware house dto
     * @return the service area name list
     */
    private List<ServiceAreaDTO> setServiceAreaNameList(WareHouseDto wareHouseDto) {
//        long startTime = System.currentTimeMillis();
        try {
            List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
            if (wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                for (int i = 0; i < wareHouseDto.getServiceAreaIdsList().size(); i++) {
                    serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                }
            }
//        long endTime = System.currentTimeMillis();
//        System.out.println("Time taken to fetch service areas: " + (endTime - startTime) + " ms");
            return serviceAreaDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WareHouseDto> getAllEntities() {
        try {
            QWareHouse qWareHouse = QWareHouse.wareHouse;
            QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
            QWareHouseParentServiceAreaMapping qWareHouseParentServiceAreaMapping = QWareHouseParentServiceAreaMapping.wareHouseParentServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qWareHouse.isNotNull().and(qWareHouse.isDeleted.eq(false));
            if (getLoggedInUserId() != 1) {
                //List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                // Common method for find Service Area List Based on StaffId
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
//                aBoolean = aBoolean.and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId).from(qWareHouseServiceAreaMapping).where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff()))).and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId).from(qWareHouseParentServiceAreaMapping).where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())));
                aBoolean = aBoolean
                        .and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId)
                                .from(qWareHouseServiceAreaMapping)
                                .where(qWareHouseServiceAreaMapping.serviceId.in(serviceAreaIds))));
                if (getMvnoIdFromCurrentStaff() != 1) {
                    aBoolean = aBoolean
                            .and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId)
                                            .from(qWareHouseServiceAreaMapping)
                                            .where(qWareHouseServiceAreaMapping.serviceId.in(serviceAreaIds)))
                                    .and(qWareHouse.mvnoId.in(getMvnoIdFromCurrentStaff(), 1)));
                }
            }
            List<WareHouse> serviceAreas = IterableUtils.toList(warehouseManagementRepository.findAll(aBoolean));
            return serviceAreas.stream().map(data -> super.getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//                    .stream().filter(wareHouseDto -> wareHouseDto.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || wareHouseDto.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
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
                        if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                            return getWarehouseList(searchModel.getFilterValue(), pageRequest);
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
     * Gets warehouse list.
     * @param name the name
     * @param pageRequest the page request
     * @return the warehouse list
     */
    public GenericDataDTO getWarehouseList(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getProductList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<WareHouse> paginationList = null;
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                paginationList = warehouseManagementRepository.findAllByIsDeletedIsFalseAndNameContainsIgnoreCase(pageRequest, name);
            } else {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
                if (!serviceAreaIds.isEmpty()) {
                    List<Long> ids = wareHouseManagmentServiceAreamappingRepo.findAllByServiceIdIn(serviceAreaIds).stream().map(WareHouseServiceAreaMapping::getWarehouseId).collect(Collectors.toList());
                    paginationList = warehouseManagementRepository.findAllByIsDeletedIsFalseAndIdInAndMvnoIdInAndNameContainsIgnoreCase(ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), name, pageRequest);
                } else {
                    paginationList = warehouseManagementRepository.findAllByIsDeletedIsFalseAndMvnoIdInAndNameContainsIgnoreCase(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), name, pageRequest);
                }
            }
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
     * Duplicate verify at save boolean.
     * @param name the name
     * @return the boolean
     */
    @Override
    public boolean duplicateVerifyAtSave(String name) {
        try {
            boolean flag = false;
            if (name != null) {
                name = name.trim();
                Integer count;
                if (getMvnoIdFromCurrentStaff() == 1) count = warehouseManagementRepository.duplicateVerifyAtSave(name);
                else {
                    count = warehouseManagementRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
                if (count == 0) {
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
     * Duplicate verify at edit boolean.
     * @param name the name
     * @param id the id
     * @return the boolean
     * @throws Exception the exception
     */
    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        try {
            boolean flag = false;
            if (name != null) {
                name = name.trim();
                Integer count;
                if (getMvnoIdFromCurrentStaff() == 1) count = warehouseManagementRepository.duplicateVerifyAtSave(name);
                else
                    count = warehouseManagementRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                if (count >= 1) {
                    Integer countEdit;
                    if (getMvnoIdFromCurrentStaff() == 1)
                        countEdit = warehouseManagementRepository.duplicateVerifyAtEdit(name, id);
                    else
                        countEdit = warehouseManagementRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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
     * Gets name.
     * @param wareHouseDto the ware house dto
     * @return the name
     * @throws Exception the exception
     */
    public String getName(WareHouseDto wareHouseDto) throws Exception {
        return getEntityForUpdateAndDelete(wareHouseDto.getId()).getName();
    }

    /**
     * Gets all parent service areas.
     * @return the all parent service areas
     */
    public List<ServiceAreaDTO> getAllParentServiceAreas() {
        try {
            QWareHouse qWareHouse = QWareHouse.wareHouse;
            QWareHouseParentServiceAreaMapping qWareHouseParentServiceAreaMapping = QWareHouseParentServiceAreaMapping.wareHouseParentServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qWareHouse.isNotNull().and(qWareHouse.isDeleted.eq(false));
            if (getLoggedInUserId() != 1) {
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceIDs = serviceAreaService.getServiceAreaByStaffId();
                if (!serviceIDs.isEmpty()) {
                    aBoolean = aBoolean
                            .and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId)
                                    .from(qWareHouseParentServiceAreaMapping)
                                    .where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs)
                                            .and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())))));
                } else {
                    aBoolean = aBoolean
                            .and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId)
                                    .from(qWareHouseParentServiceAreaMapping)
                                    .where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.notIn(serviceIDs)
                                            .and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())))));
                }
            }
            if (getLoggedInUserId() == 1) {
                aBoolean = aBoolean.and(qWareHouse.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceIDs = serviceAreaService.getServiceAreaByStaffId();
                if (!serviceIDs.isEmpty()) {
                    aBoolean = aBoolean
                            .and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId)
                                    .from(qWareHouseParentServiceAreaMapping)
                                    .where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))));
                } else {
                    aBoolean = aBoolean
                            .and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId)
                                    .from(qWareHouseParentServiceAreaMapping)
                                    .where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.notIn(serviceIDs))));
                }
            }
            List<WareHouse> wareHouseList = new JPAQuery<>(entityManager)
                    .select(Projections.constructor(WareHouse.class,
                            qWareHouse.id, qWareHouse.name))
                    .from(qWareHouse)
                    .where(aBoolean)
                    .fetch();

            QWareHouseParentServiceAreaMapping qWareHouseParentServiceAreaMapping1 = QWareHouseParentServiceAreaMapping.wareHouseParentServiceAreaMapping;
            BooleanExpression whParentSaBoolExp = qWareHouseParentServiceAreaMapping1.isNotNull();
            whParentSaBoolExp = whParentSaBoolExp
                    .and(qWareHouseParentServiceAreaMapping1.warehouseId.in(wareHouseList.stream()
                            .map(WareHouse::getId).toArray(Long[]::new)));
            List<WareHouseParentServiceAreaMapping> wareHouseParentServiceAreaMappings = (List<WareHouseParentServiceAreaMapping>) wareHouseParentServiceAreaMapRepo.findAll(whParentSaBoolExp);

            QServiceArea qServiceArea = QServiceArea.serviceArea;
            BooleanExpression serviceBooleanExp = qServiceArea.isNotNull();
            Set<Integer> saIds = wareHouseParentServiceAreaMappings.stream()
                    .map(WareHouseParentServiceAreaMapping::getParentServiceAreaId)
                    .collect(Collectors.toSet());
            Integer saArray[] = saIds.stream().toArray(Integer[]::new);
            serviceBooleanExp = serviceBooleanExp.and(qServiceArea.id.in(saArray));

            List<ServiceArea> serviceAreas = new JPAQuery<>(entityManager)
                    .select(Projections.constructor(ServiceArea.class,
                            qServiceArea.id, qServiceArea.name))
                    .from(qServiceArea)
                    .where(serviceBooleanExp)
                    .fetch();

//            List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
//            for (ServiceArea sa : serviceAreas) {
//                serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(sa, new CycleAvoidingMappingContext()));
//            }
            List<ServiceAreaDTO> serviceAreaDTOS = serviceAreas.stream()
                    .map(sa -> serviceAreaMapper.domainToDTO(sa, new CycleAvoidingMappingContext()))
                    .sorted(Comparator.comparing(ServiceAreaDTO::getId).reversed())
                    .collect(Collectors.toList());
            return serviceAreaDTOS;
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Gets all parent service areas by warehouse id.
     * @param warehouseId the warehouse id
     * @return the all parent service areas by warehouse id
     */
    public List<ServiceAreaDTO> getAllParentServiceAreasByWarehouseId(Integer warehouseId) {
        try {

            QWareHouseParentServiceAreaMapping qWareHouseParentServiceAreaMapping = QWareHouseParentServiceAreaMapping.wareHouseParentServiceAreaMapping;
            BooleanExpression booleanExpression = qWareHouseParentServiceAreaMapping.isNotNull();
            booleanExpression = booleanExpression.and(qWareHouseParentServiceAreaMapping.warehouseId.eq(warehouseId.longValue()));

            List<WareHouseParentServiceAreaMapping> wareHouseParentServiceAreaMappings = (List<WareHouseParentServiceAreaMapping>) wareHouseParentServiceAreaMapRepo.findAll(booleanExpression);

            QServiceArea qServiceArea = QServiceArea.serviceArea;
            BooleanExpression serviceBooleanExp = qServiceArea.isNotNull();
            Integer[] saIds = wareHouseParentServiceAreaMappings.stream().map(WareHouseParentServiceAreaMapping::getParentServiceAreaId).toArray(Integer[]::new);
            serviceBooleanExp = serviceBooleanExp.and(qServiceArea.id.in(saIds));

//            List<ServiceArea> serviceAreas = (List<ServiceArea>) serviceAreaRepository.findAll(serviceBooleanExp);
            List<ServiceArea> serviceAreas = new JPAQuery<>(entityManager)
                    .select(Projections.constructor(ServiceArea.class,
                            qServiceArea.id, qServiceArea.name))
                    .from(qServiceArea)
                    .where(serviceBooleanExp)
                    .fetch();
            List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
            for (ServiceArea sa : serviceAreas) {
                serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(sa, new CycleAvoidingMappingContext()));
            }
            return serviceAreaDTOS;
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Gets all by warehouse ids.
     * @param warehouseIds the warehouse ids
     * @return the all by warehouse ids
     */
    public List<String> getAllByWarehouseIds(List<Long> warehouseIds) {
        try {
            List<WareHouse> wareHouses = new ArrayList<>();
            for (Long id : warehouseIds)
                wareHouses.add(warehouseManagementRepository.findById(id).get());
            return wareHouses.stream().map(WareHouse::getName).collect(Collectors.toList());
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
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
        Integer count = inwardRepository.deleteVerifyWareHouse(id);
        if (count != 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * Gets warehouse view.
     * @param id the id
     * @return the warehouse view
     */
    public GenericDataDTO getWarehouseView(Long id) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        WarehouseViewDto warehouseViewDto = new WarehouseViewDto();
        WareHouse wareHouse = warehouseManagementRepository.findById(id).orElse(null);
        try {
            if (wareHouse != null) {
                Long pincodeId = Long.valueOf(wareHouse.getPincode());
                Integer cityId = Integer.valueOf(wareHouse.getCity());
                Integer stateId = Integer.valueOf(wareHouse.getState());
                Integer countryId = Integer.valueOf(wareHouse.getCountry());
                String pincodeName = pincodeRepository.findPincodeById(pincodeId);
                String cityName = cityRepository.findNameById(cityId);
                String stateName = stateRepository.findNameById(stateId);
                String countryName = countryRepository.findNameById(countryId);
                List<Integer> serviceAreaIds = wareHouseManagmentServiceAreamappingRepo.findAllByWarehouseId1(id);
                List<Integer> parentServiceAreaIds = wareHouseParentServiceAreaMapRepo.findAllByWarehouseId1(id);
                List<ServiceAreaViewDTO> serviceAreaDTOs = mapServiceAreas(serviceAreaIds);
                List<ServiceAreaViewDTO> parentServiceAreaDTOs = mapServiceAreas(parentServiceAreaIds);
                List<TeamsViewDTO> teamsDTOs = wareHouse.getTeamsIdsList().stream()
                        .map(team -> teamsRepository.findAllLightTeamsById(team.getId()))
                        .filter(Objects::nonNull)
                        .map(team -> new TeamsViewDTO(team.getId(), team.getName()))
                        .collect(Collectors.toList());
                DecimalFormat df = new DecimalFormat("0.0000000");
                String latitude = formatCoordinate(wareHouse.getLatitude(), df);
                String longitude = formatCoordinate(wareHouse.getLongitude(), df);
                warehouseViewDto.setId(wareHouse.getId());
                warehouseViewDto.setName(wareHouse.getName());
                warehouseViewDto.setDescription(wareHouse.getDescription());
                warehouseViewDto.setStatus(wareHouse.getStatus());
                warehouseViewDto.setAddress1(wareHouse.getAddress1());
                warehouseViewDto.setAddress2(wareHouse.getAddress2());
                warehouseViewDto.setPincode(pincodeName);
                warehouseViewDto.setCity(cityName);
                warehouseViewDto.setState(stateName);
                warehouseViewDto.setCountry(countryName);
                warehouseViewDto.setLatitude(latitude);
                warehouseViewDto.setLongitude(longitude);
                warehouseViewDto.setMvnoId(wareHouse.getMvnoId());
                warehouseViewDto.setServiceAreaNameList(serviceAreaDTOs);
                warehouseViewDto.setParenetServiceAreaNameList(parentServiceAreaDTOs);
                warehouseViewDto.setTeamsList(teamsDTOs);
                warehouseViewDto.setWarehouseType(wareHouse.getWarehouseType());
                warehouseViewDto.setRmsWarehouseId(wareHouse.getRmsWarehouseId());
                warehouseViewDto.setNavWarehouseId(wareHouse.getNavWarehouseId());
                warehouseViewDto.setWarehouseCode(wareHouse.getWarehouseCode());
                if (wareHouse.getBranchId() != null) {
                    String branchName = branchRepository.findNameById(wareHouse.getBranchId());
                    warehouseViewDto.setBarnchName(branchName);
                }
                List<ProductWarehouseMapViewDTO> productWarehouseMapViewDTOS = getProductWarehouseMapViewDtos(id);
                warehouseViewDto.setProductWarehouseMapViewDTOS(productWarehouseMapViewDTOS);
            }
            genericDataDTO.setData(warehouseViewDto);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }

    private List<ProductWarehouseMapViewDTO> getProductWarehouseMapViewDtos(Long id) {
        try {
            List<ProductWarehouseMapping> productWarehouseMappingList = productWarehouseMappingRepo.findByWarehouseId(id);
            List<ProductWarehouseMapViewDTO> productWarehouseMapViewDTOS = new ArrayList<>();
            if (!productWarehouseMappingList.isEmpty()) {
                for (ProductWarehouseMapping mapping : productWarehouseMappingList) {
                    Long productId = mapping.getProductId();
                    ProductWarehouseMapViewDTO dto = new ProductWarehouseMapViewDTO();
                    dto.setId(mapping.getId());
                    dto.setThresholdQty(mapping.getThresholdQty());
                    /** Fetch product name */
                    String productName = productRepository.findProductNameByProductId(productId);
                    dto.setProductName(productName);
                    /** Fetch unit by product category */
                    Long productCategoryId = productRepository.findProductCategoryIdByProductId(productId);
                    String unit = productCategoryRepository.findUnitById(productCategoryId);
                    dto.setUnit(unit);
                    productWarehouseMapViewDTOS.add(dto);
                }
            }
            return productWarehouseMapViewDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<ServiceAreaViewDTO> mapServiceAreas(List<Integer> ids) {
        return ids.stream()
                .map(id -> serviceAreaRepository.findAllLightServiceAreaById(id.longValue()))
                .filter(Objects::nonNull)
                .map(area -> new ServiceAreaViewDTO(area.getId(), area.getName()))
                .collect(Collectors.toList());
    }

    private String formatCoordinate(String coordinate, DecimalFormat df) {
        return (coordinate != null && !coordinate.isEmpty() && isNumeric(coordinate))
                ? df.format(Double.parseDouble(coordinate))
                : coordinate;
    }

    /**
     * Shared warehouse team mapping.
     * @param warehouseId the warehouse id
     * @param operation the operation
     */
    public void sharedWarehouseTeamMapping(Long warehouseId, Integer operation) {
        try {
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                SaveWarehouseTeamMappingSharedMessage message = new SaveWarehouseTeamMappingSharedMessage();
                List<WareHouseTeamsMapping> wareHouseTeamsMappingList = wareHouseTeamsMappingRepo.findAllByWarehouseId(warehouseId);
                message.setWareHouseTeamsMappingList(wareHouseTeamsMappingList);
                message.setOperation(CommonConstants.OPERATION_ADD);
                message.setWarehouseId(warehouseId);
                //messageSender.send(message, RabbitMqConstants.QUEUE_SEND_CREATE_WAREHOUSE_TEAM_MAPPING_DATA_COMMONAPIGW);
                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                UpdateWarehouseTeamMappingSharedMessage message = new UpdateWarehouseTeamMappingSharedMessage();
                List<WareHouseTeamsMapping> wareHouseTeamsMappingList = wareHouseTeamsMappingRepo.findAllByWarehouseId(warehouseId);
                message.setWareHouseTeamsMappingList(wareHouseTeamsMappingList);
                message.setOperation(CommonConstants.OPERATION_UPDATE);
                message.setWarehouseId(warehouseId);
                //messageSender.send(message, RabbitMqConstants.QUEUE_SEND_UPDATE_WAREHOUSE_TEAM_MAPPING_DATA_COMMONAPIGW);
                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

            } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
                UpdateWarehouseTeamMappingSharedMessage message = new UpdateWarehouseTeamMappingSharedMessage();
                List<WareHouseTeamsMapping> wareHouseTeamsMappingList = wareHouseTeamsMappingRepo.findAllByWarehouseId(warehouseId);
                message.setWareHouseTeamsMappingList(wareHouseTeamsMappingList);
                message.setOperation(CommonConstants.OPERATION_DELETE);
                message.setWarehouseId(warehouseId);
                //messageSender.send(message, RabbitMqConstants.QUEUE_SEND_UPDATE_WAREHOUSE_TEAM_MAPPING_DATA_COMMONAPIGW);
                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets ware house by id.
     * @param id the id
     * @return the ware house by id
     */
    public WareHouse getWareHouseById(long id) {
        return warehouseManagementRepository.findById(id).get();
    }


    /**
     * Is numeric boolean.
     * @param str the str
     * @return the boolean
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete verification in request boolean.
     * @param id the id
     * @return the boolean
     */
    public boolean deleteVerificationInRequest(Long id) {
        boolean flag = false;
        List<RequestInventory> count = requestInventoryRepo.findAllByRequestNameId(id);
        List<RequestInventory> count2 = requestInventoryRepo.findAllByRequestToWarehouseId(id);
        if (!count.isEmpty() || !count2.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    /**
     * Delete product warehouse mapping.
     * @param warehouseId the warehouse id
     */
//    @Transactional
    public void deleteProductWarehouseMapping(Long warehouseId) {
        List<ProductWarehouseMapping> mapping = productWarehouseMappingRepo.findByWarehouseId(warehouseId);
        if (!mapping.isEmpty()) {
            productWarehouseMappingRepo.deleteAll(mapping);
        }
    }

    @Override
    public WareHouseDto getEntityById(Long id) throws Exception {
        try {
            WareHouseDto wareHouseDto = super.getEntityById(id);
            List<ProductWarehouseMappingDTO> productWarehouseMappingDTOS = Optional.ofNullable(productWarehouseMappingRepo.findByWarehouseId(id))
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(mapping -> {
                        ProductWarehouseMappingDTO dto = new ProductWarehouseMappingDTO();
                        Long pcId = productRepository.findProductCategoryIdByProductId(mapping.getProductId());
                        String unit = productCategoryRepository.findUnitById(pcId);
                        dto.setId(mapping.getId());
                        dto.setWarehouseId(mapping.getWarehouseId());
                        dto.setProductId(mapping.getProductId());
                        dto.setThresholdQty(mapping.getThresholdQty());
                        dto.setMvnoId(mapping.getMvnoId().intValue());
                        dto.setUnit(unit);
                        return dto;
                    })
                    .collect(Collectors.toList());
            wareHouseDto.setProductWarehouseMappingDTOS(productWarehouseMappingDTOS);
            return wareHouseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
