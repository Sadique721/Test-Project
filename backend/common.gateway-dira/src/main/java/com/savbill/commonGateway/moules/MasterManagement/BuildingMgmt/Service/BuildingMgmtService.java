package com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Service;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.Constants;
import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.exceptions.DataNotFoundException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.Customers.Service.CustomersService;
import com.savbill.commonGateway.moules.DemoGraphicMapping.domain.DemoGraphicMappingTable;
import com.savbill.commonGateway.moules.DemoGraphicMapping.repository.DemoGraphicMappingRepository;
import com.savbill.commonGateway.moules.MasterManagement.Area.repository.AreaRepository;
import com.savbill.commonGateway.moules.MasterManagement.Area.service.AreaService;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingManagementDTO;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingMappingDTO;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.DTO.BuildingMgmtDTOLight;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingManagement;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.BuildingMapping;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Domain.QBuildingManagement;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Mapper.BuildingMappingMapper;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Mapper.BuildingMgmtMapper;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Repository.BuildingMappingRepository;
import com.savbill.commonGateway.moules.MasterManagement.BuildingMgmt.Repository.BuildingMgmtRepository;
import com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Entity.BuildingRefrence;
import com.savbill.commonGateway.moules.MasterManagement.BuildingReference.Repocitory.BuildingReferenceRepocitory;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.repository.PincodeRepository;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.service.PincodeService;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Service.SubAreaService;
import com.querydsl.core.types.dsl.BooleanExpression;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BuildingMgmtService extends ExBaseAbstractService<BuildingManagementDTO, BuildingManagement, Long> {


    @Autowired
    BuildingMgmtRepository buildingMgmtRepository;


    @Autowired
    BuildingMgmtMapper buildingMgmtMapper;

    @Autowired
    BuildingMappingRepository buildingMappingRepository;

    @Autowired
    BuildingMappingMapper buildingMappingMapper;


    @Autowired
    PincodeService pincodeService;

    @Autowired
    AreaService areaService;

    @Autowired
    SubAreaService subAreaService;

    @Autowired
    CreateDataSharedService createDataSharedService;
    @Autowired
    AreaRepository areaRepository;
    @Autowired
    PincodeRepository pincodeRepository;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    @Autowired
    DemoGraphicMappingRepository demoGraphicMappingRepository;

    @Autowired
    CustomersService customersService;

    @Autowired
    BuildingReferenceRepocitory buildingReferenceRepocitory;


    public BuildingMgmtService(JpaRepository<BuildingManagement, Long> repository, IBaseMapper<BuildingManagementDTO, BuildingManagement> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "BuildingMgmtService";
    }


    @Override
    public BuildingManagementDTO saveEntity(BuildingManagementDTO entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());

        BuildingManagement entityDomain = buildingMgmtMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
       if( validateBuilding(entity)) {
           // Ensure each BuildingMapping has a reference to its BuildingManagement
           if (entityDomain.getBuildingMappings() != null) {
               for (BuildingMapping mapping : entityDomain.getBuildingMappings()) {
                   mapping.setBuildingManagement(entityDomain);
               }
           }
           try {
               return buildingMgmtMapper.domainToDTO(buildingMgmtRepository.save(entityDomain), new CycleAvoidingMappingContext());
           } catch (Exception ex) {
               ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + " Error while saving Entity. Data[" + entityDomain + "]" + ex.getMessage(), ex);
               throw ex;
           }
       }else{
            throw new CustomValidationException(APIConstants.EXPECTATION_FAILED.intValue(),entityDomain.getBuildingName()+" Doesnot Exists",null);
       }
    }


    @Override
    public BuildingManagementDTO updateEntity(BuildingManagementDTO entity) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff());

        // Fetch the existing BuildingManagement entity from the database
        BuildingManagement existingEntity = buildingMgmtRepository.findById(entity.getBuildingMgmtId()).orElseThrow(() -> new EntityNotFoundException("BuildingManagement not found with ID: " + entity.getBuildingMgmtId()));


        List<Long> longList = existingEntity.getBuildingMappings().stream().map(BuildingMapping::getId).collect(Collectors.toList());
        // Remove existing BuildingMappings before adding new ones
        buildingMappingRepository.deleteByIds(longList);

        // Convert DTO to domain entity
        BuildingManagement entityDomain = buildingMgmtMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());

        // Ensure the new mappings are properly linked
        if (entityDomain.getBuildingMappings() != null) {
            for (BuildingMapping mapping : entityDomain.getBuildingMappings()) {
                mapping.setBuildingManagement(entityDomain);
            }
        }

        try {
            if (entity == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entity.getMvnoId().intValue())) {
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            }

            return buildingMgmtMapper.domainToDTO(buildingMgmtRepository.save(entityDomain), new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            throw ex;
        }
    }
    @Transactional
    public int updateBuildingNamesBySubAreaId(Integer subAreaId, String newName) {
        return buildingMgmtRepository.updateBuildingNameBySubAreaId(subAreaId, newName);
    }


    @Override
    public void deleteEntity(BuildingManagementDTO entity) throws Exception {
        try {
            if (entity == null || entity.getBuildingMgmtId() == null) {
                throw new CustomValidationException(APIConstants.FAIL, "Invalid request data", null);
            }
            BuildingManagement existingEntity = buildingMgmtRepository.findById(entity.getBuildingMgmtId())
                    .orElseThrow(() -> new DataNotFoundException("Building not found"));
            if (Boolean.TRUE.equals(existingEntity.getIsDeleted())) {
                throw new DataNotFoundException("Building already deleted");
            }
            if (!(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == existingEntity.getMvnoId().intValue())) {
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            }
            List<BuildingMapping> mappings = buildingMappingRepository.findAllByBuildingManagementId(existingEntity.getBuildingMgmtId());
            if (mappings != null && !mappings.isEmpty()) {
                List<Long> ids = mappings.stream().map(BuildingMapping::getId).collect(Collectors.toList());
                buildingMappingRepository.deleteByIds(ids);
            }
            existingEntity.setIsDeleted(true);
            BuildingManagement deletedBuildingMgmt = buildingMgmtRepository.save(existingEntity);
            deletedBuildingMgmt.setBuildingMappings(null);
            createDataSharedService.updateEntityDataForAllMicroService(buildingMgmtMapper.domainToDTO(deletedBuildingMgmt, new CycleAvoidingMappingContext()));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE ERROR] " + ex.getMessage(), ex);
            throw ex;
        }
    }


    public List<BuildingMappingDTO> processCsvFile(MultipartFile file, boolean isUpdaterequest, List<BuildingMappingDTO> buildingMappingDTOS) throws IOException {
        List<BuildingMappingDTO> buildingMappings = new ArrayList<>();
        if (isUpdaterequest) {
            if (buildingMappingDTOS != null) {
                List<Long> mappingDeleteIds = buildingMappingDTOS.stream().map(buildingMappingDTO -> buildingMappingDTO.getId()).collect(Collectors.toList());
                buildingMappingRepository.deleteByIds(mappingDeleteIds);
            }
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true; // To skip header
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] data = line.split(",");

                if (data.length >= 1) { // Ensure at least 1 column exists
                    BuildingMappingDTO mappingDTO = new BuildingMappingDTO();
                    mappingDTO.setBuildingNumber(data[0].trim()); // First column is Building Number

                    buildingMappings.add(mappingDTO);
                } else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Empty CSV found !!, Please add data in it and try again", null);
                }
            }
            return buildingMappings;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public GenericDataDTO getBuildingMgmtByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<BuildingManagement> buildingManagements = null;
            QBuildingManagement qBuildingManagement = QBuildingManagement.buildingManagement;
            Integer areaId = areaService.getIdByAreaName(name);
            Integer pinCodeId = pincodeService.getPinCodeByName(name);
            List<Integer> subAreaId = subAreaService.findIdsByName(name);
            String buildingType = name;
            boolean flag = false;
            BooleanExpression booleanExpression = qBuildingManagement.isNotNull()
                    .and(qBuildingManagement.buildingName.likeIgnoreCase("%" + name.trim() + "%"));
            //.or(qBuildingManagement.status.equalsIgnoreCase(name.trim()));
            if (pinCodeId != null) {
                booleanExpression = booleanExpression.or(qBuildingManagement.pincodeId.eq(Math.toIntExact(pinCodeId)));
            }
            if (areaId != null) {
                booleanExpression = booleanExpression.or(qBuildingManagement.areaId.eq(areaId));
            }
            if (subAreaId != null) {
                booleanExpression = booleanExpression.or(qBuildingManagement.subAreaId.in(subAreaId));
            }
            if (buildingType != null) {
                booleanExpression = booleanExpression.or(qBuildingManagement.subAreaId.in(subAreaId));
            }
            if (!CollectionUtils.isEmpty(getBUIdsFromCurrentStaff())) {
                booleanExpression = booleanExpression.and(qBuildingManagement.buid.eq(Math.toIntExact(getBUIdsFromCurrentStaff().get(0))));
            }
            if (getMvnoIdFromCurrentStaff() == 1) {
                buildingManagements = buildingMgmtRepository.findAll(booleanExpression, pageRequest);
            } else {
                booleanExpression = booleanExpression.and(qBuildingManagement.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                booleanExpression = booleanExpression.and(qBuildingManagement.isDeleted.eq(false));
                buildingManagements = buildingMgmtRepository.findAll(booleanExpression, pageRequest);
            }
            if (null != buildingManagements && 0 < buildingManagements.getSize()) {
                makeGenericResponse(genericDataDTO, buildingManagements);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }


    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getBuildingMgmtByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public List<BuildingMgmtDTOLight> getBuildingByEntity(String entityname, Long entityid) {
        List<BuildingMgmtDTOLight> dtos = new ArrayList<>();
        Optional<DemoGraphicMappingTable> convertedName = demoGraphicMappingRepository.findAllByNewName(entityname);
        String updatedName = entityname;
        if (convertedName.isPresent()) {
            updatedName = convertedName.get().getCurrentName();
        }
        List<BuildingMgmtDTOLight> results = new ArrayList<>();
        String normalizedUpdatedName = updatedName.trim().toLowerCase();

        if (normalizedUpdatedName.equals("area")) {
            results = buildingMgmtRepository.findBuildingMgmtByAreaId(Math.toIntExact(entityid));
        } else if (normalizedUpdatedName.equals("pincode") || normalizedUpdatedName.equals("pin code")) {
            results = buildingMgmtRepository.findBuildingMgmtByPincodeId(Math.toIntExact(entityid));
        } else if (normalizedUpdatedName.equals("subarea") || normalizedUpdatedName.equals("sub area")) {
            results = buildingMgmtRepository.findBuildingMgmtBySubAreaId(Math.toIntExact(entityid));
        } else {
            return dtos;
        }
        return results;
    }


    public List<String> getAvailableBuildingMgmtNumbers(Integer buildingMgmtId, String token) {
        try {
            // Step 1: Try from local DB
            List<BuildingMapping> localMappings = buildingMappingRepository.findAllByBuildingManagementId(buildingMgmtId.longValue());

            if (localMappings != null && !localMappings.isEmpty()) {
                // Return directly if found in DB
                return localMappings.stream()
                        .map(BuildingMapping::getBuildingNumber)
                        .distinct()
                        .collect(Collectors.toList());
            }

            // Step 2: If not found locally, fallback to Feign call
            GenericDataDTO dataDTO = customersService.getUsedBuildingIds(token, buildingMgmtId);
            List<String> usedBuildingNumbers = dataDTO.getDataList();

            boolean isEmpty = (usedBuildingNumbers == null || usedBuildingNumbers.isEmpty());

            List<BuildingMapping> fallbackMappings = buildingMappingRepository.findAllByBuildingManagementId(
                    buildingMgmtId.longValue(),
                    usedBuildingNumbers == null ? Collections.emptyList() : usedBuildingNumbers,
                    isEmpty
            );

            return fallbackMappings.stream()
                    .map(BuildingMapping::getBuildingNumber)
                    .distinct()
                    .collect(Collectors.toList());

        } catch (FeignException fe) {
            ApplicationLogger.logger.error("Error fetching used building numbers from CPM: ", fe);
        } catch (Exception e) {
            ApplicationLogger.logger.error("Unexpected error: ", e);
        }

        return Collections.emptyList();
    }



    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<BuildingManagement> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if (getMvnoIdFromCurrentStaff() == 1) paginationList = buildingMgmtRepository.findAll(pageRequest);
        else {
            long startTime = System.currentTimeMillis();
            paginationList = buildingMgmtRepository.findAllByMvnoIds(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
        }
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    @Override
    public BuildingManagementDTO getEntityById(Long id) throws Exception {
        try {
            BuildingManagement domain = buildingMgmtRepository.findBuildingManagementById(id);
            List<BuildingMapping> buildingMappings = buildingMappingRepository.findAllByBuildingManagementId(domain.getBuildingMgmtId());
            if (buildingMappings != null && !buildingMappings.isEmpty()) domain.setBuildingMappings(buildingMappings);
            if (null == domain || domain.getDeleteFlag()) {
                throw new DataNotFoundException(getModuleNameForLog() + "--" + "Data not found for id " + id);
            }
            BuildingManagementDTO dto = getMapper().domainToDTO(domain, new CycleAvoidingMappingContext());
            if (dto != null && (getMvnoIdFromCurrentStaff() == 1 || (dto.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || dto.getMvnoId() == 1)))
                return dto;
            return null;
            /*if(null == dto){

            }*/
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof NoSuchElementException) {
                throw new DataNotFoundException();
            }
            //   ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Page<BuildingManagement> getAllEntitiesWithPage(int page, int size) throws Exception {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Integer currentMvnoId = getMvnoIdFromCurrentStaff();

            Page<BuildingManagement> entityPage = buildingMgmtRepository.findAllWithPagination(currentMvnoId, pageable);
            return entityPage;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "-- Error while getting paginated list: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Boolean validateBuilding(BuildingManagementDTO buildingManagementDTO) {

        BuildingRefrence buildingRefrence=buildingReferenceRepocitory.findByMvnoId(getMvnoIdFromCurrentStaff());

        if(buildingRefrence.getMappingFrom().equalsIgnoreCase("Sub Area")){
        List<Integer> subareaId=subAreaService.findIdsByName(buildingManagementDTO.getBuildingName());
            if (subareaId.isEmpty() ) {
                return false;
            }
        } else if (buildingRefrence.getMappingFrom().equalsIgnoreCase("Area")) {
              Integer areaId=  areaRepository.findByNameAndIsDeletedFalse(buildingManagementDTO.getBuildingName());
            if (areaId == null) {
                return false;
            }
        } else if (buildingRefrence.getMappingFrom().equalsIgnoreCase("Pincode")) {
        Integer pincodeId=      pincodeRepository.findIdByPincode(buildingManagementDTO.getBuildingName());
            if (pincodeId == null) {
                return false;
            }
        }

        return true;
    }
}
