package com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.RequestConstants;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.*;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserMapper;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.Outward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.QOutward;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopServiceAreaMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopServiceAreaMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.*;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaMapper;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMapping;
import com.savbill.inventorymanagement.modules.MasterManagement.StaffServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamUserMapping.TeamUserMapping;
import com.savbill.inventorymanagement.modules.WorkflowManagement.TeamUserMapping.TeamUserMappingsRepocitory;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Teams.Teams;
import com.savbill.inventorymanagement.rabbitmq.InventoryRequestMessage;
import com.savbill.inventorymanagement.rabbitmq.RabbitMqConstants;
import com.savbill.inventorymanagement.utils.APIConstants;
import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Request inventory service.
 */
@Service
public class RequestInventoryServiceImpl extends ExBaseAbstractService<RequestInventoryDto, RequestInventory, Long> {

    /**
     * The Request inventory repo.
     */
    @Autowired
    RequestInventoryRepo requestInventoryRepo;

    /**
     * The Request inventory product mapping repo.
     */
    @Autowired
    RequestInventoryProductMappingRepo requestInventoryProductMappingRepo;

    /**
     * The Staff user service area mapping repository.
     */
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;

    /**
     * The Warehouse management repository.
     */
    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;

    /**
     * The Ware house managment service areamapping repo.
     */
    @Autowired
    WareHouseManagmentServiceAreamappingRepo wareHouseManagmentServiceAreamappingRepo;

    /**
     * The Pop management repository.
     */
    @Autowired
    PopManagementRepository popManagementRepository;

    /**
     * The Pop service area mapping repo.
     */
    @Autowired
    PopServiceAreaMappingRepo popServiceAreaMappingRepo;

    /**
     * The Staff user repository.
     */
    @Autowired
    StaffUserRepository staffUserRepository;
    /**
     * The Service area repository.
     */
    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    /**
     * The Request inventory mapper.
     */
    @Autowired
    RequestInventoryMapper requestInventoryMapper;

    /**
     * The Request invenotry product mapping repository.
     */
    @Autowired
    RequestInventoryProductMappingRepo requestInvenotryProductMappingRepository;

    /**
     * The Outward repository.
     */
    @Autowired
    OutwardRepository outwardRepository;

    /**
     * The Request inventory product mapping mapper.
     */
    @Autowired
    RequestInventoryProductMappingMapper requestInventoryProductMappingMapper;

    /**
     * The Warhouse mapper.
     */
    @Autowired
    WarhouseMapper warhouseMapper;

    /**
     * The Service area mapper.
     */
    @Autowired
    ServiceAreaMapper serviceAreaMapper;

    /**
     * The Pop management mapper.
     */
    @Autowired
    PopManagementMapper popManagementMapper;

    /**
     * The Staff user mapper.
     */
    @Autowired
    StaffUserMapper staffUserMapper;
    /**
     * The Product category repository.
     */
    @Autowired
    ProductCategoryRepository productCategoryRepository;

    /**
     * The Product repository.
     */
    @Autowired
    ProductRepository productRepository;

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
     * The Request inventory service.
     */
    @Autowired
    private RequestInventoryServiceImpl requestInventoryService;

    /**
     * The Request inventory history repo.
     */
    @Autowired
    private RequestInventoryHistoryRepo requestInventoryHistoryRepo;

    /**
     * The Kafka message sender.
     */
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    /**
     * Instantiates a new Request inventory service.
     * @param repository the repository
     * @param mapper the mapper
     */
    public RequestInventoryServiceImpl(JpaRepository<RequestInventory, Long> repository, IBaseMapper<RequestInventoryDto, RequestInventory> mapper) {
        super(repository, mapper);
    }


    /**
     * Save entity request inventory dto.
     * @param requestInventoryDto the request inventory dto
     * @return the request inventory dto
     * @throws Exception the exception
     */
    @Override
    public RequestInventoryDto saveEntity(RequestInventoryDto requestInventoryDto) throws Exception {
        try {
            RequestInventory requestInventory = requestInventoryMapper.dtoToDomain(requestInventoryDto, new CycleAvoidingMappingContext());
            List<RequestInventoryProductMappingDto> invenotryProductMappingList = requestInventoryDto.getRequestInvenotryProductMappings();
            requestInventory.setInventoryRequestStatus(RequestConstants.REQUEST_STATUS.WAITING_FOR_APPROVAL);
            RequestInventory finalrequestInventory = requestInventoryRepo.save(requestInventory);
            if (requestInventoryDto != null) {
                String requestInventoryName = getInventoryRequestName("Request", "-Inventory-", "");
                finalrequestInventory.setRequestInventoryName(requestInventoryName);
                requestInventoryRepo.save(finalrequestInventory);
                invenotryProductMappingList.stream().forEach(requestInvenotryProductMapping -> {
                    RequestInvenotryProductMapping invenotryProductMapping = new RequestInvenotryProductMapping();
                    invenotryProductMapping.setInventoryRequestId(finalrequestInventory.getId());
                    invenotryProductMapping.setProductId(requestInvenotryProductMapping.getProductId());
                    invenotryProductMapping.setProductCategoryId(requestInvenotryProductMapping.getProductCategoryId());
                    invenotryProductMapping.setQuantity(requestInvenotryProductMapping.getQuantity());
                    invenotryProductMapping.setItemType(requestInvenotryProductMapping.getItemType());
                    requestInventoryProductMappingRepo.save(invenotryProductMapping);
                });
                String requesterName = null;
                String requestTo = null;
                String onBehalfOf = null;
                if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.POP)) {
                    onBehalfOf = RequestConstants.ON_BE_HALF.POP;
                    requesterName = popManagementRepository.findById(requestInventory.getRequestNameId()).get().getName();
                } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.SERVICEAREA)) {
                    onBehalfOf = RequestConstants.ON_BE_HALF.SERVICEAREA;
                    requesterName = serviceAreaRepository.findById(requestInventory.getRequestNameId()).get().getName();
                } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.STAFFUSER)) {
                    onBehalfOf = RequestConstants.ON_BE_HALF.STAFFUSER;
                    requesterName = staffUserRepository.findById(Integer.valueOf(requestInventory.getRequestNameId().intValue())).get().getUsername();
                } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.WAREHOUSE)) {
                    onBehalfOf = RequestConstants.ON_BE_HALF.WAREHOUSE;
                    requesterName = warehouseManagementRepository.findById(requestInventory.getRequestNameId()).get().getName();
                }
                Optional<WareHouse> requestToWarehouse = warehouseManagementRepository.findById(requestInventory.getRequestToWarehouseId());
                requestInventoryDto.setRequesterName(requesterName);
                requestInventoryDto.setRequestToName(requestToWarehouse.get().getName());
                /** Send Inventory Request Message to Notification */
                sendRequestInventoryMessage(requestToWarehouse, requesterName, onBehalfOf);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return requestInventoryDto;
    }

    /**
     * Send request inventory message.
     * @param requestToWarehouse the request to warehouse
     * @param requesterName the requester name
     * @param onBehalfOf the on behalf of
     */
    private void sendRequestInventoryMessage(Optional<WareHouse> requestToWarehouse, String requesterName, String onBehalfOf) {
        try {
            List<Teams> teamsList = requestToWarehouse.get().getTeamsIdsList();
            List<TeamUserMapping> teamUserMappings = new ArrayList<>();
            List<String> staffEmails = new ArrayList<>();
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
            if (!staffEmails.isEmpty()) {
                String emailId = staffEmails.get(0);
                List<String> altEmailList = staffEmails.size() > 1 ? staffEmails.subList(1, staffEmails.size()).stream().distinct().collect(Collectors.toList()) : new ArrayList<>();
                InventoryRequestMessage inventoryRequestMessage = new InventoryRequestMessage(
                        RabbitMqConstants.INVENTORY_REQUEST_SUCCESS,
                        getMvnoIdFromCurrentStaff(),
                        requestToWarehouse.get().getName(),
                        requesterName,
                        RabbitMqConstants.SOURCE_NAME_SAVBILL_BSS_INVENTORY,
                        onBehalfOf,
                        getLoggedInUser().getUsername(),
                        emailId,
                        altEmailList
                );
                Gson gson = new Gson();
                gson.toJson(inventoryRequestMessage);
                kafkaMessageSender.send(new KafkaMessageData(inventoryRequestMessage, inventoryRequestMessage.getClass().getSimpleName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Validate request.
     * @param requestInventoryDto the request inventory dto
     */
    public void validateRequest(RequestInventoryDto requestInventoryDto) {
        try {
            if (requestInventoryDto != null) {
                if (requestInventoryDto.getRequestInvenotryProductMappings() != null || requestInventoryDto.getRequestInvenotryProductMappings().size() > 0) {
                    List<Long> productId = new ArrayList<>();
                    for (RequestInventoryProductMappingDto requestInventoryProductMappingDto : requestInventoryDto.getRequestInvenotryProductMappings()) {
                        if (productId.contains(requestInventoryProductMappingDto.getProductId())) {
                            throw new CustomValidationException(APIConstants.FAIL, RequestConstants.FAIL_MESSAGE.DUPLICATE_PRODUCT_SELECTION, null);
                        }
                        productId.add(requestInventoryProductMappingDto.getProductId());
                    }
                }
                if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
                    WareHouse requesterWH = warehouseManagementRepository.findById(requestInventoryDto.getRequestNameId()).orElse(null);
                    WareHouse requestToWh = warehouseManagementRepository.findById(requestInventoryDto.getRequestToWarehouseId()).orElse(null);
                    String requestToType = requestToWh.getWarehouseType();
                    String requesterType = requesterWH.getWarehouseType();
                    if (requesterWH != null && requestToWh != null) {
                        if (requestToType.equalsIgnoreCase("3PL") && requesterType.equalsIgnoreCase("3PL")) {
                            throw new CustomValidationException(APIConstants.FAIL, RequestConstants.FAIL_MESSAGE.THIRED_PARTY_TO_THIRED_PARTY_REQUEST, null);
                        }
                    }
                }
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Find by id request inventory dto.
     * @param id the id
     * @return the request inventory dto
     */
    RequestInventoryDto findById(Long id) {
        try {

            RequestInventory requestInventory = requestInventoryRepo.findById(id).get();
            RequestInventoryDto requestInventoryDto = requestInventoryMapper.domainToDTO(requestInventory, new CycleAvoidingMappingContext());
            List<RequestInvenotryProductMapping> requestInvenotryProductMappingList = requestInvenotryProductMappingRepository.findAllByInventoryRequestId(id);
            List<RequestInventoryProductMappingDto> requestInventoryProductMappingDtoList = new ArrayList<>();
            requestInvenotryProductMappingList.stream().forEach(r -> {

                RequestInventoryProductMappingDto requestInventoryProductMappingDto = new RequestInventoryProductMappingDto();
                ProductCategory productCategory = productCategoryRepository.findById(r.getProductCategoryId()).get();
                Product product = productRepository.findById(r.getProductId()).get();
                requestInventoryProductMappingDto.setId(r.getId());
                requestInventoryProductMappingDto.setInventoryRequestId(r.getInventoryRequestId());
                requestInventoryProductMappingDto.setProductId(r.getProductId());
                requestInventoryProductMappingDto.setProductName(product.getName());
                requestInventoryProductMappingDto.setProductCategoryId(r.getProductCategoryId());
                requestInventoryProductMappingDto.setProductCategoryName(productCategory.getName());
                requestInventoryProductMappingDto.setQuantity(r.getQuantity());
                requestInventoryProductMappingDto.setItemType(r.getItemType());
                requestInventoryProductMappingDto.setRequestStatus(r.getRequestStatus());

                //to Set Invetory Status
                QOutward qOutward = QOutward.outward;
                BooleanExpression booleanExpression = qOutward.isNotNull();
                booleanExpression = booleanExpression.and(qOutward.productId.id.eq(r.getProductId()).and(qOutward.requestInventoryId.eq(r.getInventoryRequestId())).and(qOutward.requestInventoryProductId.eq(r.getId())));
                Optional<Outward> outward = outwardRepository.findOne(booleanExpression);
                if (outward.isPresent()) {
                    requestInventoryProductMappingDto.setRequestStatus(r.getRequestStatus());
                    requestInventoryProductMappingDto.setOutWardCreated(true);
                }
                requestInventoryProductMappingDtoList.add(requestInventoryProductMappingDto);
            });
            requestInventoryDto.setRequestInvenotryProductMappings(requestInventoryProductMappingDtoList);

            if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.POP)) {
                requestInventoryDto.setRequesterName(popManagementRepository.findById(requestInventory.getRequestNameId()).get().getName());
            } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.SERVICEAREA)) {
                requestInventoryDto.setRequesterName(serviceAreaRepository.findById(requestInventory.getRequestNameId()).get().getName());
            } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.STAFFUSER)) {
                requestInventoryDto.setRequesterName(staffUserRepository.findById(Math.toIntExact(requestInventory.getRequestNameId())).get().getUsername());
            } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.WAREHOUSE)) {
                requestInventoryDto.setRequesterName(warehouseManagementRepository.findById(requestInventory.getRequestNameId()).get().getName());
            }
            requestInventoryDto.setRequestToName(warehouseManagementRepository.findById(requestInventory.getRequestToWarehouseId()).get().getName());
            return requestInventoryDto;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }


    /**
     * Approve status request inventory dto.
     * @param status the status
     * @param id the id
     * @param remarks the remarks
     * @return the request inventory dto
     */
    public RequestInventoryDto approveStatus(String status, Long id, String remarks) {
        try {
            RequestInventory requestInventory = requestInventoryRepo.findById(id).get();
            List<RequestInvenotryProductMapping> requestInvenotryProductMappings = requestInvenotryProductMappingRepository.findAllByInventoryRequestId(id);
            if (status.equalsIgnoreCase(CommonConstants.REJECTED)) {
                requestInvenotryProductMappings.stream().forEach(requestInventoryProductMapping -> {
                    RequestInvenotryProductMapping requestInvenotryProductMapping = requestInventoryProductMappingRepo.findById(requestInventoryProductMapping.getId()).get();
                    //requestInvenotryProductMapping.setDeleted(true);
                    requestInvenotryProductMapping.setRequestStatus(CommonConstants.REJECTED);
                    requestInventoryProductMappingRepo.save(requestInvenotryProductMapping);
                });
                //requestInventory.setDeleted(true);
                requestInventory.setStatus(CommonConstants.REJECTED);
                requestInventory.setInventoryRequestStatus(CommonConstants.REJECTED);
                requestInventory.setRemarks(remarks);
                RequestInventory inventory = requestInventoryRepo.save(requestInventory);
                return requestInventoryMapper.domainToDTO(inventory, new CycleAvoidingMappingContext());
            } else {
                requestInventory.setStatus(CommonConstants.APPROVE);
                requestInvenotryProductMappings.stream().forEach(requestInventoryProductMapping -> {
                    RequestInvenotryProductMapping requestInvenotryProductMapping = requestInventoryProductMappingRepo.findById(requestInventoryProductMapping.getId()).get();
                    requestInvenotryProductMapping.setRequestStatus(RequestConstants.REQUEST_STATUS.OPEN);
                    requestInventoryProductMappingRepo.save(requestInvenotryProductMapping);
                });
                requestInventory.setInventoryRequestStatus(RequestConstants.REQUEST_STATUS.IN_PROGRESS);
                requestInventory.setRemarks(remarks);
                RequestInventory inventory = requestInventoryRepo.save(requestInventory);
                return requestInventoryMapper.domainToDTO(inventory, new CycleAvoidingMappingContext());
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }


    /**
     * Gets all.
     * @param onBehalgOf the on behalg of
     * @return the all
     * @throws Exception the exception
     */
    public GenericDataDTO getAll(String onBehalgOf) throws Exception {
        GenericDataDTO genericDataDTO;
        try {
            genericDataDTO = new GenericDataDTO();
            if (onBehalgOf.equalsIgnoreCase(RequestConstants.ON_BE_HALF.WAREHOUSE)) {
                List wareHouseList = getAllWareHousesByLoggedInSA();
                genericDataDTO.setDataList(wareHouseList);
            } else if (onBehalgOf.equalsIgnoreCase(RequestConstants.ON_BE_HALF.POP)) {
                List popList = getAllPop();
                genericDataDTO.setDataList(popList);
            } else if (onBehalgOf.equalsIgnoreCase(RequestConstants.ON_BE_HALF.SERVICEAREA)) {
                List serviceAreaList = getAllServiceAreas();
                genericDataDTO.setDataList(serviceAreaList);
            } else {
                List staffUserList = getAllStaff();
                genericDataDTO.setDataList(staffUserList);

            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }

        return genericDataDTO;
    }

    /**
     * Gets all ware houses by logged in sa.
     * @return the all ware houses by logged in sa
     */
    List<CommonResponceDto> getAllWareHousesByLoggedInSA() {
        try {
            List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
            List<WareHouse> allActiveWareHouses = new ArrayList<>();
            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
            List<Integer> serviceAreaIdList = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toList());
            if (getMvnoIdFromCurrentStaff() == 1) {
                allActiveWareHouses = warehouseManagementRepository.findAllByIsDeletedIsFalseAndStatus(CommonConstants.ACTIVE_STATUS);
            } else {
                if (!serviceAreaIdList.isEmpty()) {
                    List<Long> ids = wareHouseManagmentServiceAreamappingRepo.findAllByServiceIdIn(serviceAreaIdList).stream().map(WareHouseServiceAreaMapping::getWarehouseId).collect(Collectors.toList());
                    allActiveWareHouses = warehouseManagementRepository.findAllByIdInAndIsDeletedIsFalseAndMvnoIdInAndStatus(ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), CommonConstants.ACTIVE_STATUS);
                } else {
                    allActiveWareHouses = warehouseManagementRepository.findAllByIsDeletedIsFalseAndMvnoIdInAndStatus(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), CommonConstants.ACTIVE_STATUS);
                }
            }
            if (!allActiveWareHouses.isEmpty()) {
                allActiveWareHouses.forEach(wareHouse -> {
                    CommonResponceDto commonResponceDto = new CommonResponceDto();
                    commonResponceDto.setId(wareHouse.getId());
                    commonResponceDto.setName(wareHouse.getName());
                    commonResponceDtos.add(commonResponceDto);
                });
            }
            return commonResponceDtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets all service areas.
     * @return the all service areas
     */
    List<CommonResponceDto> getAllServiceAreas() {
        try {
            List<ServiceArea> allActiveServiceAreas = new ArrayList<>();
            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
            List<Long> ids = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).map(Integer::longValue).collect(Collectors.toList());
            if (getMvnoIdFromCurrentStaff() == 1) {
                allActiveServiceAreas = serviceAreaRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS);
            } else {
                if (!ids.isEmpty()) {
                    allActiveServiceAreas = serviceAreaRepository.findAllByIdInAndStatusAndIsDeletedIsFalseAndMvnoIdIn(ids, CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    allActiveServiceAreas = serviceAreaRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
            List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
            allActiveServiceAreas.stream().forEach(serviceArea -> {
                CommonResponceDto commonResponceDto = new CommonResponceDto();
                commonResponceDto.setId(serviceArea.getId());
                commonResponceDto.setName(serviceArea.getName());
                commonResponceDtos.add(commonResponceDto);

            });
            return commonResponceDtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all staff.
     * @return the all staff
     */
    List<CommonResponceDto> getAllStaff() {
        try {
            List<StaffUser> allActiveStaffUsers = new ArrayList<>();
            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
            List<Integer> serviceAreaIdList = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toList());
            if (getMvnoIdFromCurrentStaff() == 1) {
                allActiveStaffUsers = staffUserRepository.findAllByStatusAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS);
            } else {
                if (!serviceAreaIdList.isEmpty()) {
                    List<Integer> ids = staffUserServiceAreaMappingRepository.findAllByServiceIdIn(serviceAreaIdList).stream().map(StaffUserServiceAreaMapping::getStaffId).collect(Collectors.toList());
                    allActiveStaffUsers = staffUserRepository.findByIdInAndStatusAndIsDeleteIsFalseAndMvnoIdIn(ids, CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    allActiveStaffUsers = staffUserRepository.findAllByStatusAndIsDeleteIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
            List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
            allActiveStaffUsers.stream().forEach(staffUser -> {
                CommonResponceDto commonResponceDto = new CommonResponceDto();
                commonResponceDto.setId(staffUser.getId().longValue());
                commonResponceDto.setName(staffUser.getUsername());
                commonResponceDtos.add(commonResponceDto);
            });
            return commonResponceDtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets all pop.
     * @return the all pop
     */
    List<CommonResponceDto> getAllPop() {
        try {
            List<PopManagement> allActivePops = new ArrayList<>();
            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Collections.singletonList(getLoggedInUserId()));
            List<Integer> serviceAreaIdList = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toList());
            if (getMvnoIdFromCurrentStaff() == 1) {
                allActivePops = popManagementRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS);
            } else {
                if (!serviceAreaIdList.isEmpty()) {
                    List<Long> ids = popServiceAreaMappingRepo.findAllByServiceAreaIdIn(serviceAreaIdList).stream().map(PopServiceAreaMapping::getPopId).collect(Collectors.toList());
                    allActivePops = popManagementRepository.findAllByStatusAndIdInAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                } else {
                    allActivePops = popManagementRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            }
            List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
            allActivePops.stream().forEach(pop -> {
                CommonResponceDto commonResponceDto = new CommonResponceDto();
                commonResponceDto.setId(pop.getId().longValue());
                commonResponceDto.setName(pop.getName());
                commonResponceDtos.add(commonResponceDto);

            });
            return commonResponceDtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all request by current staff.
     * @param filterList the filter list
     * @param page the page
     * @param pageSize the page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the all request by current staff
     */
    public GenericDataDTO getAllRequestByCurrentStaff(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            QRequestInventory qRequestInventory = QRequestInventory.requestInventory;
            BooleanExpression boolExp = qRequestInventory.isNotNull().and(qRequestInventory.createdById.eq(getLoggedInUserId()).and(qRequestInventory.isDeleted.eq(false)));
            Page<RequestInventory> page1 = requestInventoryRepo.findAll(boolExp, pageRequest);
            page1.forEach(p -> p.setRequesterName(
                    p.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.POP) ? popManagementRepository.findById(p.getRequestNameId()).get().getName() :
                            p.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.WAREHOUSE) ? warehouseManagementRepository.findById(p.getRequestNameId()).get().getName() :
                                    p.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.SERVICEAREA) ? serviceAreaRepository.findById(p.getRequestNameId()).get().getName() :
                                            p.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.STAFFUSER) ? staffUserRepository.findById(Integer.valueOf(p.getRequestNameId().intValue())).get().getUsername() : ""));

            page1.forEach(p -> p.setRequestToName(warehouseManagementRepository.findById(p.getRequestToWarehouseId()).get().getName()));
            page1.forEach(r -> {
                r.setInventoryRequestStatus(r.getInventoryRequestStatus());
                List<RequestInvenotryProductMapping> requestInvenotryProductMappingList = requestInvenotryProductMappingRepository.findAllByInventoryRequestId(r.getId());
                requestInvenotryProductMappingList.stream().forEach(t -> {
                    t.setRequestStatus(t.getRequestStatus());
                });
            });

            if (null != page && 0 < page1.getSize()) {
                makeGenericResponse(genericDataDTO, page1);
            }


            return genericDataDTO;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }


    /**
     * Gets all assigned request inventory.
     * @param filterList the filter list
     * @param page the page
     * @param pageSize the page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the all assigned request inventory
     */
    public GenericDataDTO getAllAssignedRequestInventory(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        try {

            GenericDataDTO genericDataDTO = new GenericDataDTO();
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);

            List<Long> teamIdList = teamUserMappingsRepocitory.teamIds(Long.valueOf(getLoggedInUserId()));
            List<WareHouseTeamsMapping> wareHouseTeamsMappings = wareHouseTeamsMappingRepo.findAllByTeamIdIn(teamIdList);
            if (!(wareHouseTeamsMappings.isEmpty())) {
                List<Long> wareHouseIdList = wareHouseTeamsMappings.stream().map(WareHouseTeamsMapping::getWarehouseId).collect(Collectors.toList());

                QRequestInventory qRequestInventory = QRequestInventory.requestInventory;
                BooleanExpression booleanExpression = qRequestInventory.isNotNull();
                booleanExpression = booleanExpression.and(qRequestInventory.isDeleted.isFalse()).and(qRequestInventory.requestToWarehouseId.in(wareHouseIdList));

                Page<RequestInventory> page1 = requestInventoryRepo.findAll(booleanExpression, pageRequest);
                page1.forEach(p -> p.setRequesterName(
                        p.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.POP) ? popManagementRepository.findById(p.getRequestNameId()).get().getName() :
                                p.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.WAREHOUSE) ? warehouseManagementRepository.findById(p.getRequestNameId()).get().getName() :
                                        p.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.SERVICEAREA) ? serviceAreaRepository.findById(p.getRequestNameId()).get().getName() :
                                                p.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.STAFFUSER) ? staffUserRepository.findById(Integer.valueOf(p.getRequestNameId().intValue())).get().getUsername() : ""));

                page1.forEach(p -> p.setRequestToName(warehouseManagementRepository.findById(p.getRequestToWarehouseId()).get().getName()));
                page1.forEach(r -> {
                    List<RequestInvenotryProductMapping> requestInvenotryProductMappingList = requestInvenotryProductMappingRepository.findAllByInventoryRequestId(r.getId());
                    if (requestInvenotryProductMappingList.size() != 0) {
                        List<String> list = new ArrayList<>(requestInvenotryProductMappingList.stream().map(RequestInvenotryProductMapping::getRequestStatus).collect(Collectors.toList()));
                        if (!(list.stream().anyMatch(str -> str == null))) {
                            if (list.stream().anyMatch(str -> str.equalsIgnoreCase(CommonConstants.PENDING))) {
                                r.setInventoryRequestStatus(RequestConstants.REQUEST_STATUS.WAITING_FOR_APPROVAL);
                            } else if (list.stream().allMatch(str -> str.equalsIgnoreCase(RequestConstants.REQUEST_STATUS.OPEN))) {
                                r.setInventoryRequestStatus(RequestConstants.REQUEST_STATUS.INPROGRESS);
                            } else if (list.stream().anyMatch(str -> str.equalsIgnoreCase(RequestConstants.REQUEST_STATUS.CLOSE))) {
                                r.setInventoryRequestStatus(RequestConstants.REQUEST_STATUS.COMPLETED);
                            } else {
                                r.setInventoryRequestStatus(RequestConstants.REQUEST_STATUS.PARTIALLY_COMPLETED);
                            }
                        }
                    }
                });

                if (null != page && 0 < page1.getSize()) {
                    makeGenericResponse(genericDataDTO, page1);
                }
            }
            return genericDataDTO;

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * Delete inventory string.
     * @param id the id
     * @return the string
     */
    public String deleteInventory(Long id) {
        try {
            RequestInventory requestInventory = requestInventoryRepo.findById(id).get();
            List<RequestInvenotryProductMapping> requestInventoryProductMappingRepoList = requestInvenotryProductMappingRepository.findAllByInventoryRequestId(id);
            requestInventoryProductMappingRepoList.stream().forEach(r -> {
                r.setDeleted(true);
                requestInvenotryProductMappingRepository.save(r);
            });
            requestInventory.setDeleteFlag(true);
            requestInventoryRepo.save(requestInventory);
            return MessageConstants.DELETE_SUCCESSFUL;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }


    /**
     * Gets inventory request name.
     * @param flag1 the flag 1
     * @param flag2 the flag 2
     * @param flag3 the flag 3
     * @return the inventory request name
     */
    public String getInventoryRequestName(String flag1, String flag2, String flag3) {
        String flag = "";
        if (flag1 != null) {
            flag += flag1;
        }
        if (flag2 != null) {
            flag += flag2;
        }
        if (flag3 != null) {
            RequestInventory requestInventory = requestInventoryRepo.findTopByOrderByIdDesc();
            if (requestInventory == null) {
                flag += 1;
            } else {
                flag += requestInventory.getId() + 1;
            }
        }
        return flag;
    }

    /**
     * Forward request to ware house generic data dto.
     * @param reqId the req id
     * @param forwardToReqId the forward to req id
     * @param remarks the remarks
     * @return the generic data dto
     * @throws Exception the exception
     */
    public GenericDataDTO forwardRequestToWareHouse(Long reqId, Long forwardToReqId, String remarks) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            RequestInventory requestInventory = requestInventoryRepo.findById(reqId).orElse(null);
            RequestInventoryDto requestInventoryDto = new RequestInventoryDto();
            if (requestInventory != null && requestInventory.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.WAREHOUSE)) {
                if (requestInventory.getRequestNameId() == forwardToReqId) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), RequestConstants.FAIL_MESSAGE.WAREHOUSE_DIFFERENT_SELECTION, null);
                }
                WareHouse requesterWh = warehouseManagementRepository.findById(requestInventory.getRequestNameId()).orElse(null);
                WareHouse forwardToWh = warehouseManagementRepository.findById(forwardToReqId).orElse(null);
                String forwardToType = forwardToWh.getWarehouseType();
                String requesterType = requesterWh.getWarehouseType();
                if (requesterWh != null && forwardToWh != null) {
                    if (forwardToType.equalsIgnoreCase("3PL") && requesterType.equalsIgnoreCase("3PL")) {
                        throw new CustomValidationException(APIConstants.FAIL, RequestConstants.FAIL_MESSAGE.THIRED_PARTY_TO_THIRED_PARTY_FORWARD, null);
                    }
                }
            }
            if (requestInventory != null && requestInventory.getRequestToWarehouseId() == forwardToReqId) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), RequestConstants.FAIL_MESSAGE.WAREHOUSE_DIFFERENT_SELECTION, null);
            } else if (requestInventory != null && requestInventory.getRequestToWarehouseId() != forwardToReqId) {
                RequestInventoryHistory requestInventoryHistory = new RequestInventoryHistory();
                requestInventoryHistory.setRequestInventoryId(requestInventory.getId());
                requestInventoryHistory.setRequestInventoryName(requestInventory.getRequestInventoryName());
                requestInventoryHistory.setRequestNameId(requestInventory.getRequestNameId());
                requestInventoryHistory.setRequestToWarehouseId(requestInventory.getRequestToWarehouseId());
                requestInventoryHistory.setRemarks(remarks);
                requestInventoryHistoryRepo.save(requestInventoryHistory);
                requestInventory.setRequestToWarehouseId(forwardToReqId);
                Optional<WareHouse> wareHouse = warehouseManagementRepository.findById(forwardToReqId);
                requestInventory.setRequestToName(wareHouse.get().getName());
                RequestInventory requestInventory1 = requestInventoryRepo.save(requestInventory);
                requestInventoryDto = requestInventoryMapper.domainToDTO(requestInventory1, new CycleAvoidingMappingContext());
                String requesterName = null;
                String requestTo = null;
                String onBehalfOf = null;
                if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.POP)) {
                    onBehalfOf = RequestConstants.ON_BE_HALF.POP;
                    requesterName = popManagementRepository.findById(requestInventory.getRequestNameId()).get().getName();
                } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.SERVICEAREA)) {
                    onBehalfOf = RequestConstants.ON_BE_HALF.SERVICEAREA;
                    requesterName = serviceAreaRepository.findById(requestInventory.getRequestNameId()).get().getName();
                } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.STAFFUSER)) {
                    onBehalfOf = RequestConstants.ON_BE_HALF.STAFFUSER;
                    requesterName = staffUserRepository.findById(Integer.valueOf(requestInventory.getRequestNameId().intValue())).get().getUsername();
                } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase(RequestConstants.ON_BE_HALF.WAREHOUSE)) {
                    onBehalfOf = RequestConstants.ON_BE_HALF.WAREHOUSE;
                    requesterName = warehouseManagementRepository.findById(requestInventory.getRequestNameId()).get().getName();
                }
                /** Send Inventory Request Message to Notification */
                sendRequestInventoryMessage(wareHouse, requesterName, onBehalfOf);
                genericDataDTO.setData(requestInventoryDto);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                return genericDataDTO;
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage(), null);
        }
        return genericDataDTO;
    }

    /**
     * Gets all ware house.
     * @return the all ware house
     */
    List<CommonResponceDto> getAllWareHouse() {
        try {
            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Arrays.asList(getLoggedInUserId()));
            List<Integer> serviceAreaIdList = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toList());
            if (!serviceAreaIdList.isEmpty()) {
                List<WareHouseServiceAreaMapping> wareHouseServiceAreaMappingList = wareHouseManagmentServiceAreamappingRepo.findAllByServiceIdIn(serviceAreaIdList);
                List<Long> ids = wareHouseServiceAreaMappingList.stream().map(WareHouseServiceAreaMapping::getWarehouseId).collect(Collectors.toList());
                QWareHouse qWareHouse = QWareHouse.wareHouse;
                BooleanExpression booleanExpression = qWareHouse.isDeleted.eq(false).and(qWareHouse.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qWareHouse.id.in(ids));
                if (getMvnoIdFromCurrentStaff() != 1) {
                    booleanExpression = booleanExpression.and(qWareHouse.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                }
                List<WareHouse> allActiveWareHouses = IterableUtils.toList(warehouseManagementRepository.findAll(booleanExpression));
                List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
                allActiveWareHouses.stream().forEach(wareHouse -> {
                    CommonResponceDto commonResponceDto = new CommonResponceDto();
                    commonResponceDto.setId(wareHouse.getId());
                    commonResponceDto.setName(wareHouse.getName());
                    commonResponceDtos.add(commonResponceDto);
                });
                return commonResponceDtos.stream()
                        .sorted(Comparator.comparing(CommonResponceDto::getId).reversed())
                        .collect(Collectors.toList());
            } else {
                List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
                QWareHouse qWareHouse = QWareHouse.wareHouse;
                BooleanExpression booleanExpression = qWareHouse.isDeleted.eq(false).and(qWareHouse.status.eq(CommonConstants.ACTIVE_STATUS));
                if (getMvnoIdFromCurrentStaff() != 1) {
                    booleanExpression = booleanExpression.and(qWareHouse.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                }
                List<WareHouse> wareHouseList = IterableUtils.toList(warehouseManagementRepository.findAll(booleanExpression));
                wareHouseList.stream().forEach(wareHouse -> {
                    CommonResponceDto commonResponceDto = new CommonResponceDto();
                    commonResponceDto.setId(wareHouse.getId());
                    commonResponceDto.setName(wareHouse.getName());
                    commonResponceDtos.add(commonResponceDto);
                });
                return commonResponceDtos.stream()
                        .sorted(Comparator.comparing(CommonResponceDto::getId).reversed())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets module name for log.
     * @return the module name for log
     */
    @Override
    public String getModuleNameForLog() {
        return "[RequestIvnetoryService]";
    }

    /**
     * Validate approve request.
     * @param requestId the request id
     */
//Validate Approve Request
    public void validateApproveRequest(Long requestId) {
        QRequestInventory qRequestInventory = QRequestInventory.requestInventory;
        BooleanExpression booleanExpression = qRequestInventory.id.eq(requestId);
        List<RequestInventory> requestInventories = IterableUtils.toList(requestInventoryRepo.findAll(booleanExpression));
        if (requestInventories.get(0).isDeleted()) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), RequestConstants.FAIL_MESSAGE.DELETE_REQUEST, null);
        }
    }
}
