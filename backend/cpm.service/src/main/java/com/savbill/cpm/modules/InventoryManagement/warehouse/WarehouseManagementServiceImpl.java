package com.savbill.cpm.modules.InventoryManagement.warehouse;

import com.savbill.cpm.constants.SearchConstants;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.modules.InventoryManagement.inward.InwardRepository;
import com.savbill.cpm.modules.ServiceArea.domain.QServiceArea;
import com.savbill.cpm.modules.ServiceArea.domain.ServiceArea;
import com.savbill.cpm.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.savbill.cpm.modules.ServiceArea.model.ServiceAreaDTO;
import com.savbill.cpm.modules.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.cpm.modules.ServiceArea.service.ServiceAreaService;
import com.savbill.cpm.modules.Teams.domain.Teams;
import com.savbill.cpm.modules.Teams.mapper.TeamsMapper;
import com.savbill.cpm.modules.Teams.model.TeamsDTO;
import com.savbill.cpm.modules.Teams.repository.TeamsRepository;
import com.savbill.cpm.modules.Teams.service.TeamsService;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.spring.SpringContext;
import com.savbill.cpm.utils.CommonConstants;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WarehouseManagementServiceImpl extends ExBaseAbstractService<WareHouseDto, WareHouse, Long> {
    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    private ModelMapper modelMapper;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    WareHouseParentServiceAreaMapRepo wareHouseParentServiceAreaMapRepo;

    @Autowired
    WarhouseMapper warehouseMapper;

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private TeamsMapper teamsMapper;
    @Autowired
    private InwardRepository inwardRepository;

    @Autowired
    private MessageSender messageSender;

    public WarehouseManagementServiceImpl(WarehouseManagementRepository warehouseManagementRepository, IBaseMapper<WareHouseDto, WareHouse> mapper) {
        super(warehouseManagementRepository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[WarehouseManagementServiceImpl]";
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        QWareHouse qWareHouse = QWareHouse.wareHouse;
        QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
        QWareHouseParentServiceAreaMapping qWareHouseParentServiceAreaMapping = QWareHouseParentServiceAreaMapping.wareHouseParentServiceAreaMapping;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        BooleanExpression aBoolean = qWareHouse.isNotNull().and(qWareHouse.isDeleted.eq(false));
        // Common method for find Service Area List Based on StaffId
        ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
        List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
        List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
//            List<Long>serviceIDs=super.getServiceAreaIdList().stream().map(Integer::longValue).collect(Collectors.toList());
        if(serviceAreaIds.size() != 0) {
            if (getLoggedInUserId() != 1) {
//            aBoolean = aBoolean.and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId).from(qWareHouseServiceAreaMapping).where(qWareHouseServiceAreaMapping.serviceId.in(serviceAreaIds)))).and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId).from(qWareHouseParentServiceAreaMapping).where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))));
                aBoolean = aBoolean
                        .and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId)
                                .from(qWareHouseServiceAreaMapping)
                                .where(qWareHouseServiceAreaMapping.serviceId.in(serviceAreaIds))));
            }
        }
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<WareHouse> paginationList = null;
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (getMvnoIdFromCurrentStaff() != 1)
                aBoolean = aBoolean.and(qWareHouse.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                paginationList = productRepository.findAll(pageRequest);
//            } else {
            paginationList = warehouseManagementRepository.findAll(aBoolean, pageRequest);
//            }

            if (paginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }


    GenericDataDTO getAllActiveWarehouse() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            QWareHouse qWareHouse = QWareHouse.wareHouse;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
            //QWareHouseParentServiceAreaMapping qWareHouseParentServiceAreaMapping = QWareHouseParentServiceAreaMapping.wareHouseParentServiceAreaMapping;
            BooleanExpression booleanExpression = qWareHouse.isNotNull().and(qWareHouse.status.eq(CommonConstants.ACTIVE_STATUS)).and(qWareHouse.isDeleted.eq(false));
            if (getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
//                booleanExpression = booleanExpression
//                        .and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId)
//                                .from(qWareHouseServiceAreaMapping)
//                                .where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))))
//                        .and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId)
//                                .from(qWareHouseParentServiceAreaMapping)
//                                .where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))));
                booleanExpression = booleanExpression
                        .and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId)
                                .from(qWareHouseServiceAreaMapping)
                                .where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))));
            }
            genericDataDTO.setDataList(IterableUtils.toList(this.warehouseManagementRepository.findAll(booleanExpression)).stream()
                    .filter(wareHouse -> wareHouse.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || wareHouse.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList())
                    .stream().map(wareHouse -> warehouseMapper.domainToDTO(wareHouse, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }
    public WareHouseDto comparePSAIdsAndSAIds(WareHouseDto wareHouseDto, String warehouseOperation) throws Exception{
        wareHouseDto.setMvnoId(getMvnoIdFromCurrentStaff());
        List<Integer> parentServiceAreaIds = new ArrayList<>();
        List<Integer> ServiceAreaIds = new ArrayList<>();
        if (wareHouseDto.getWarehouseType().equalsIgnoreCase("3PL")){
            if (wareHouseDto.getParentServiceAreaIdsList() == null){
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please Select Parent Service Area", null);
            }
            if (wareHouseDto.getServiceAreaIdsList() == null){
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please Select Service Area", null);
            }
        }
        if (wareHouseDto.getWarehouseType().equalsIgnoreCase("OWN")){
            if (wareHouseDto.getServiceAreaIdsList() == null){
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please Select Service Area", null);
            }
        }
        if (wareHouseDto.getParentServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList() != null) {
            for (int i = 0; i < wareHouseDto.getParentServiceAreaIdsList().size(); i++) {
                Integer parentSAId = wareHouseDto.getParentServiceAreaIdsList().get(i).intValue();
             //   Integer SAId = wareHouseDto.getServiceAreaIdsList().get(i).intValue();
                parentServiceAreaIds.add(parentSAId);
             //   ServiceAreaIds.add(SAId);
            }
            for(int j=0;j<wareHouseDto.getServiceAreaIdsList().size();j++){
                Integer SAId = wareHouseDto.getServiceAreaIdsList().get(j).intValue();
                ServiceAreaIds.add(SAId);
            }
            if (warehouseOperation.equalsIgnoreCase("UpdateWarehouseOperation")) {
                if (wareHouseDto.getWarehouseType().equalsIgnoreCase("3PL")) {
                    for (int a = 0; a < parentServiceAreaIds.size(); a++) {
                        for (int b = 0; b < ServiceAreaIds.size(); b++) {
                            if (parentServiceAreaIds.get(a) == ServiceAreaIds.get(b)) {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please select different Service Area", null);
                            }
                        }
                    }
                }
            }
            if (warehouseOperation.equalsIgnoreCase("SaveWarehouseOperation")) {
                for (int a = 0; a < parentServiceAreaIds.size(); a++) {
                    for (int b = 0; b < ServiceAreaIds.size(); b++) {
                        if (parentServiceAreaIds.get(a) == ServiceAreaIds.get(b)) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please select different Service Area", null);
                        }
                    }
                }
            }
        }
        return wareHouseDto;
    }

    @Override
    public WareHouseDto saveEntity(WareHouseDto wareHouseDto) throws Exception {
        if (wareHouseDto.getServiceAreaIdsList().size() > 0) {
            wareHouseDto.setMvnoId(getMvnoIdFromCurrentStaff());
            WareHouse wareHouse = new WareHouse();
            if(wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                for (int i=0; i<wareHouseDto.getServiceAreaIdsList().size() ; i++) {
                    serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                }
                wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
            }
            List<TeamsDTO> teamsDTOS = new ArrayList<>();
            if(wareHouseDto.getTeamsIdsList() != null && wareHouseDto.getTeamsIdsList().size() > 0) {
                List<Teams> teams = teamsRepository.findAllById(wareHouseDto.getTeamsIdsList());
                for (int i=0; i<wareHouseDto.getTeamsIdsList().size() ; i++) {
                    teamsDTOS.add(teamsMapper.domainToDTO(teams.get(i), new CycleAvoidingMappingContext()));
                }
//                wareHouseDto.setTeamsDTOList(teamsDTOS);
            }

//            WareHouseDto warehouseDto = super.saveEntity(wareHouseDto);
            WareHouse house=warehouseManagementRepository.save(warehouseMapper.dtoToDomain(wareHouseDto,new CycleAvoidingMappingContext()));
            WareHouseDto warehouseDto= warehouseMapper.domainToDTO(house, new CycleAvoidingMappingContext());
            //messageSender.send(warehouseDto, RabbitMqConstants.QUEUE_WAREHOUSE_INTEGRATOIN);

            warehouseDto.setTeamsDTOList(teamsDTOS);
            // To check warehouse type 3PL
            if (wareHouseDto.getWarehouseType().equalsIgnoreCase("3PL")) {
                if (wareHouseDto.getParentServiceAreaIdsList() != null) {
                    List<ServiceAreaDTO> parentSAList = new ArrayList<>();
                    for(Long id : wareHouseDto.getParentServiceAreaIdsList()) {
                        WareHouseParentServiceAreaMapping wareHouseParentServiceAreaMapping = new WareHouseParentServiceAreaMapping();
                        wareHouseParentServiceAreaMapping.setWarehouseId(warehouseDto.getId());
                        wareHouseParentServiceAreaMapping.setParentServiceAreaId(id.intValue());
                        wareHouseParentServiceAreaMapRepo.save(wareHouseParentServiceAreaMapping);
                        parentSAList.add(serviceAreaMapper.domainToDTO(serviceAreaRepository.findById(id).get(), new CycleAvoidingMappingContext()));
                    }
                    warehouseDto.setParenetServiceAreaNameList(parentSAList);

                    if (wareHouseDto.getServiceAreaIdsList() != null) {
                        wareHouse.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
                        //wareHouseDto.setServiceAreaNameList(wareHouse.getServiceAreaNameList());
                        if(wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                            List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                            List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                            for (int i=0; i<wareHouseDto.getServiceAreaIdsList().size() ; i++) {
                                serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                            }
                            wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                        }
                    }
                } else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Parent Service Area is Mandatory", null);
                }
            }
            // To check warehouse type OWN
            if (wareHouseDto.getWarehouseType().equalsIgnoreCase("OWN")) {
                if (wareHouseDto.getServiceAreaIdsList() != null) {
                    wareHouse.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
                    //wareHouseDto.setServiceAreaNameList(wareHouse.getServiceAreaNameList());
                    if(wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                        List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                        List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                        for (int i=0; i<wareHouseDto.getServiceAreaIdsList().size() ; i++) {
                            serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                        }
                        wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                    }
                    Set<Long> saIds = new HashSet<>();
                    if(wareHouseDto.getParentServiceAreaIdsList() != null && wareHouseDto.getParentServiceAreaIdsList().size() > 0)
                        saIds.addAll(wareHouseDto.getParentServiceAreaIdsList());
                    if(wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0)
                        saIds.addAll(wareHouseDto.getServiceAreaIdsList());
                    List<ServiceAreaDTO> parentSAList = new ArrayList<>();
                    for(Long id : saIds) {
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
        } else {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Service Area is Mandatory", null);
        }
    }

    public WareHouseDto saveParentServicearea(WareHouseDto wareHouseDto) throws Exception {
        if (wareHouseDto.getServiceAreaIdsList().size() > 0) {
            Integer warehouseId = warehouseManagementRepository.findId(wareHouseDto.getName());
            // To compare parentSAIds and SAIds
            if (warehouseId != null) {
                wareHouseDto.setMvnoId(getMvnoIdFromCurrentStaff());
                List<WareHouseParentServiceAreaMapping> wareHouseParentServiceAreaMappingList = new ArrayList<>();
                WareHouseParentServiceAreaMapping wareHouseParentServiceAreaMapping = new WareHouseParentServiceAreaMapping();
                if (wareHouseDto.getWarehouseType().equalsIgnoreCase("OWN")) {
                    if (wareHouseDto.getParentServiceAreaIdsList() != null) {
                        for (int i = 0; i < wareHouseDto.getParentServiceAreaIdsList().size(); i++) {
                            wareHouseParentServiceAreaMapping.setWarehouseId(Long.valueOf(warehouseId));
                            wareHouseParentServiceAreaMapping.setParentServiceAreaId(Math.toIntExact(wareHouseDto.getParentServiceAreaIdsList().get(i)));
                            wareHouseParentServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                            wareHouseParentServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
                            wareHouseParentServiceAreaMappingList.add(wareHouseParentServiceAreaMapping);
                        }
                        for (int j = 0; j < wareHouseParentServiceAreaMappingList.size(); j++) {
                            wareHouseParentServiceAreaMapRepo.save(wareHouseParentServiceAreaMappingList.get(j));
                        }
                    }
                }
           }
           return null;
        } else {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Service Area is Mandatory", null);
        }
    }

    @Override
    public WareHouseDto updateEntity(WareHouseDto wareHouseDto) throws Exception {
        getEntityForUpdateAndDelete(wareHouseDto.getId());
        WareHouse wareHouse = new WareHouse();
        //if(wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0)
            //wareHouseDto.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
        if(wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
            List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
            List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
            for (int i=0; i<wareHouseDto.getServiceAreaIdsList().size() ; i++) {
                serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
            }
            wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
        }
        List<TeamsDTO> teamsDTOS = new ArrayList<>();

        if(wareHouseDto.getTeamsIdsList() != null && wareHouseDto.getTeamsIdsList().size() > 0) {
            List<Teams> teams = teamsRepository.findAllById(wareHouseDto.getTeamsIdsList());
            for (int i=0; i<wareHouseDto.getTeamsIdsList().size() ; i++) {
                teamsDTOS.add(teamsMapper.domainToDTO(teams.get(i), new CycleAvoidingMappingContext()));
            }
//            wareHouseDto.setTeamsDTOList(teamsDTOS);
        }
        WareHouse house=warehouseManagementRepository.save(warehouseMapper.dtoToDomain(wareHouseDto,new CycleAvoidingMappingContext()));

        WareHouseDto warehouseDto = super.updateEntity(warehouseMapper.domainToDTO(house, new CycleAvoidingMappingContext()));
        warehouseDto.setTeamsDTOList(teamsDTOS);

        // To check warehouse type 3PL
        if (wareHouseDto.getWarehouseType().equalsIgnoreCase("3PL")) {
            if (wareHouseDto.getParentServiceAreaIdsList() != null) {

                List<ServiceAreaDTO> parentSAList = new ArrayList<>();
                for(Long id : wareHouseDto.getParentServiceAreaIdsList()) {
                    WareHouseParentServiceAreaMapping wareHouseParentServiceAreaMapping = new WareHouseParentServiceAreaMapping();
                    wareHouseParentServiceAreaMapping.setWarehouseId(warehouseDto.getId());
                    wareHouseParentServiceAreaMapping.setParentServiceAreaId(id.intValue());
                    wareHouseParentServiceAreaMapRepo.save(wareHouseParentServiceAreaMapping);
                    parentSAList.add(serviceAreaMapper.domainToDTO(serviceAreaRepository.findById(id).get(), new CycleAvoidingMappingContext()));
                }
                warehouseDto.setParenetServiceAreaNameList(parentSAList);


                if (wareHouseDto.getServiceAreaIdsList() != null) {
                    wareHouse.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
                    //wareHouseDto.setServiceAreaNameList(wareHouse.getServiceAreaNameList());
                    if(wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                        List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                        List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                        for (int i=0; i<wareHouseDto.getServiceAreaIdsList().size() ; i++) {
                            serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                        }
                        wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                    }
                }
            }
        }
        // To check warehouse type OWN
        if (wareHouseDto.getWarehouseType().equalsIgnoreCase("OWN")) {
            if (wareHouseDto.getServiceAreaIdsList() != null) {
                wareHouse.setServiceAreaNameList(serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList()));
                //wareHouseDto.setServiceAreaNameList(wareHouse.getServiceAreaNameList());
                if(wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0) {
                    List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                    List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(wareHouseDto.getServiceAreaIdsList());
                    for (int i=0; i<wareHouseDto.getServiceAreaIdsList().size() ; i++) {
                        serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                    }
                    wareHouseDto.setServiceAreaNameList(serviceAreaDTOS);
                }
                Set<Long> saIds = new HashSet<>();
                if(wareHouseDto.getParentServiceAreaIdsList() != null && wareHouseDto.getParentServiceAreaIdsList().size() > 0)
                    saIds.addAll(wareHouseDto.getParentServiceAreaIdsList());
                if(wareHouseDto.getServiceAreaIdsList() != null && wareHouseDto.getServiceAreaIdsList().size() > 0)
                    saIds.addAll(wareHouseDto.getServiceAreaIdsList());
                List<ServiceAreaDTO> parentSAList = new ArrayList<>();
                for(Long id : saIds) {
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
    }

    public WareHouseDto updateParentServicearea(WareHouseDto wareHouseDto) throws Exception {
        if (wareHouseDto.getId() != null) {
            wareHouseDto.setMvnoId(getMvnoIdFromCurrentStaff());
            List<WareHouseParentServiceAreaMapping> wareHouseParentServiceAreaMappingList = new ArrayList<>();
            WareHouseParentServiceAreaMapping wareHouseParentServiceAreaMapping = new WareHouseParentServiceAreaMapping();
            if (wareHouseDto.getWarehouseType().equalsIgnoreCase("OWN")) {
                if (wareHouseDto.getParentServiceAreaIdsList() != null) {
                    for (int i = 0; i < wareHouseDto.getParentServiceAreaIdsList().size(); i++) {
                        wareHouseParentServiceAreaMapping.setWarehouseId(Long.valueOf(wareHouseDto.getId()));
                        wareHouseParentServiceAreaMapping.setParentServiceAreaId(Math.toIntExact(wareHouseDto.getParentServiceAreaIdsList().get(i)));
                        wareHouseParentServiceAreaMapping.setCreatedOn(LocalDateTime.now());
                        wareHouseParentServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
                        wareHouseParentServiceAreaMappingList.add(wareHouseParentServiceAreaMapping);
                    }
                    for (int j = 0; j < wareHouseParentServiceAreaMappingList.size(); j++) {
                        wareHouseParentServiceAreaMapRepo.save(wareHouseParentServiceAreaMappingList.get(j));
                    }
                }
            }
        }
        return null;
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
                                .where(qWareHouseServiceAreaMapping.serviceId.in(serviceAreaIds)))
                                .and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())));
            }
            List<WareHouse> serviceAreas = IterableUtils.toList(warehouseManagementRepository.findAll(aBoolean));
            return serviceAreas.stream().map(data -> super.getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(wareHouseDto -> wareHouseDto.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || wareHouseDto.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<WarehouseViewDto>  getAllWarehouseView(){
        List<WareHouseDto> wareHouseDtoList = getAllEntities();
        List<WarehouseViewDto> warehouseViewDtoList = new ArrayList<>();
        for(WareHouseDto wareHouseDto : wareHouseDtoList){
            warehouseViewDtoList.add(dtoToViewdto(wareHouseDto));

        }
        return warehouseViewDtoList;

    }
    public WarehouseViewDto dtoToViewdto(WareHouseDto wareHouseDto){
        WarehouseViewDto warehouseViewDto = new WarehouseViewDto();
        warehouseViewDto.setId(wareHouseDto.getId());
        warehouseViewDto.setName(wareHouseDto.getName());
        warehouseViewDto.setStatus(wareHouseDto.getStatus());
        warehouseViewDto.setDescription(warehouseViewDto.getDescription());
        return warehouseViewDto;

    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getWarehouseList(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getWarehouseList(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getProductList()] ";
        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            Page<WareHouse> productList;
//            if (getMvnoIdFromCurrentStaff() == 1)
//                productList = warehouseManagementRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
//            else
//                productList = warehouseManagementRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (null != productList && 0 < productList.getSize()) {
//                makeGenericResponse(genericDataDTO, productList);
//            }
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QWareHouse qWareHouse = QWareHouse.wareHouse;
            QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression booleanExpression = qWareHouse.isNotNull()
                    .and(qWareHouse.isDeleted.eq(false))
                    .and(qWareHouse.name.likeIgnoreCase("%" + name + "%"));
            // Common method for find Service Area List Based on StaffId
            ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
            List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
            if(getMvnoIdFromCurrentStaff() != 1) {
                booleanExpression = booleanExpression
                        .and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId)
                                .from(qWareHouseServiceAreaMapping)
                                .where((qWareHouseServiceAreaMapping.serviceId.in(serviceAreaIds)).and(qWareHouse.mvnoId.in(getMvnoIdFromCurrentStaff(), 1)))));
            }
            Page<WareHouse> wareHouses = warehouseManagementRepository.findAll(booleanExpression, pageRequest);
            if (null != wareHouses && 0 < wareHouses.getSize()) {
                makeGenericResponse(genericDataDTO, wareHouses);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

//    @Override
//    public boolean duplicateVerifyAtSave(String name) {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = warehouseManagementRepository.duplicateVerifyAtSave(name);
//            else {
//                count = warehouseManagementRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            }
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }

//    @Override
//    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = warehouseManagementRepository.duplicateVerifyAtSave(name);
//            else
//                count = warehouseManagementRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count >= 1) {
//                Integer countEdit;
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    countEdit = warehouseManagementRepository.duplicateVerifyAtEdit(name, id);
//                else
//                    countEdit = warehouseManagementRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }

    public String getName(WareHouseDto wareHouseDto ) throws Exception {
        return getEntityForUpdateAndDelete(wareHouseDto.getId()).getName();
    }

    public List<ServiceAreaDTO> getAllParentServiceAreas() {
        try {
            QWareHouse qWareHouse = QWareHouse.wareHouse;
            QWareHouseParentServiceAreaMapping qWareHouseParentServiceAreaMapping = QWareHouseParentServiceAreaMapping.wareHouseParentServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qWareHouse.isNotNull().and(qWareHouse.isDeleted.eq(false));
            if (getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                aBoolean = aBoolean.and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId).from(qWareHouseParentServiceAreaMapping).where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs).and(qWareHouse.mvnoId.eq(getMvnoIdFromCurrentStaff())))));
            }
            if (getLoggedInUserId() == 1) {
                List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                aBoolean = aBoolean.and(qWareHouse.id.in(query.select(qWareHouseParentServiceAreaMapping.warehouseId).from(qWareHouseParentServiceAreaMapping).where(qWareHouseParentServiceAreaMapping.parentServiceAreaId.in(serviceIDs))));
            }
            List<WareHouse> wareHouseList = (List<WareHouse>) warehouseManagementRepository.findAll(aBoolean);

            QWareHouseParentServiceAreaMapping qWareHouseParentServiceAreaMapping1 = QWareHouseParentServiceAreaMapping.wareHouseParentServiceAreaMapping;
            BooleanExpression whParentSaBoolExp = qWareHouseParentServiceAreaMapping1.isNotNull();
            whParentSaBoolExp = whParentSaBoolExp.and(qWareHouseParentServiceAreaMapping1.warehouseId.in(wareHouseList.stream().map(WareHouse::getId).toArray(Long[]::new)));
            List<WareHouseParentServiceAreaMapping> wareHouseParentServiceAreaMappings = (List<WareHouseParentServiceAreaMapping>) wareHouseParentServiceAreaMapRepo.findAll(whParentSaBoolExp);

            QServiceArea qServiceArea = QServiceArea.serviceArea;
            BooleanExpression serviceBooleanExp = qServiceArea.isNotNull();
            Set<Integer> saIds = wareHouseParentServiceAreaMappings.stream().map(WareHouseParentServiceAreaMapping::getParentServiceAreaId).collect(Collectors.toSet());
            Integer saArray [] = saIds.stream().toArray(Integer[]::new);
            serviceBooleanExp = serviceBooleanExp.and(qServiceArea.id.in(saArray));

            List<ServiceArea> serviceAreas = (List<ServiceArea>) serviceAreaRepository.findAll(serviceBooleanExp);
            List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
            for(ServiceArea sa : serviceAreas){
                serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(sa, new CycleAvoidingMappingContext()));
            }
            return serviceAreaDTOS;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

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

            List<ServiceArea> serviceAreas = (List<ServiceArea>) serviceAreaRepository.findAll(serviceBooleanExp);
            List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
            for(ServiceArea sa : serviceAreas){
                serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(sa, new CycleAvoidingMappingContext()));
            }
            return serviceAreaDTOS;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<String> getAllByWarehouseIds(List<Long> warehouseIds) {
        try {
            List<WareHouse> wareHouses = new ArrayList<>();
            for(Long id : warehouseIds)
                wareHouses.add(warehouseManagementRepository.findById(id).get());
            return wareHouses.stream().map(WareHouse::getName).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }

    }



    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = inwardRepository.deleteVerifyWareHouse(id);
        if (count !=0) {
            flag = true;
        }
        return flag;
    }

}
