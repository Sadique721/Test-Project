package com.savbill.commonGateway.moules.MasterManagement.SubArea.Service;

import com.savbill.commonGateway.common.domain.ClientService;
import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.ClientServiceConstant;
import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.exceptions.DataNotFoundException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.fileUtillity.FileUtility;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Area.domain.QArea;
import com.savbill.commonGateway.moules.MasterManagement.Area.repository.AreaRepository;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.QCity;
import com.savbill.commonGateway.moules.MasterManagement.City.service.CityService;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.QCountry;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.QPincode;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.QState;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import com.savbill.commonGateway.moules.MasterManagement.State.service.StateService;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO.SubAreaAll;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.DTO.SubAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain.QSubArea;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Domain.SubArea;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Mapper.SubAreaMapper;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Repository.SubAreaRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubAreaService extends ExBaseAbstractService<SubAreaDTO, SubArea, Long> {



    @Autowired
    CountryService countryService;

    @Autowired
    StateService stateService;

    @Autowired
    CityService cityService;

    @Autowired
    SubAreaRepository subAreaRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    SubAreaMapper subAreaMapper;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    private FileUtility fileUtility;

    private static final Logger logger = LoggerFactory.getLogger(ExBaseAbstractController.class);

    public SubAreaService(JpaRepository<SubArea, Long> repository, IBaseMapper<SubAreaDTO, SubArea> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[SubAreaService]";
    }





    public GenericDataDTO getSubAreaByName(List<GenericSearchModel> filters, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getSubAreaByName()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            QSubArea qSubArea = QSubArea.subArea;
            BooleanExpression booleanExpression = qSubArea.isNotNull();

            for (GenericSearchModel filter : filters) {
                String column = filter.getFilterColumn() != null ? filter.getFilterColumn().trim() : "";
                String value = filter.getFilterValue() != null ? filter.getFilterValue().trim() : "";
                String condition = filter.getFilterCondition() != null ? filter.getFilterCondition().toLowerCase() : "and";

                BooleanExpression expression = null;

                switch (column.toLowerCase()) {
                    case "any":

                        Country country = countryService.getByName(value);
                        List<State> stateList = stateService.getByName(value);
                        List<City> cityList = cityService.getCityByName(value);

                        expression = qSubArea.name.likeIgnoreCase("%" + value + "%")
                                .or(qSubArea.status.likeIgnoreCase("%" + value + "%"));

                        if (country != null) {
                            expression = expression.or(qSubArea.countryId.eq(country.getId()));
                        }
                        if (stateList != null && !stateList.isEmpty()) {
                            expression = expression.or(qSubArea.stateId.in(stateList.stream().map(State::getId).collect(Collectors.toList())));
                        }
                        if (cityList != null && !cityList.isEmpty()) {
                            expression = expression.or(qSubArea.cityId.in(cityList.stream().map(City::getId).collect(Collectors.toList())));
                        }
                        break;

                    case "status":
                        if ("equalto".equalsIgnoreCase(filter.getFilterOperator())) {
                            expression = qSubArea.status.equalsIgnoreCase(value);
                        } else {
                            expression = qSubArea.status.likeIgnoreCase("%" + value + "%");
                        }
                        break;

                    case "area":
                        if ("equalto".equalsIgnoreCase(filter.getFilterOperator())) {
                            expression = qSubArea.area.id.eq(Long.valueOf(value));
                        } else {
                            expression = qSubArea.area.id.stringValue().like("%" + value + "%");
                        }
                    break;

                    default:
                        continue;
                }

                if ("or".equals(condition)) {
                    booleanExpression = booleanExpression.or(expression);
                } else {
                    booleanExpression = booleanExpression.and(expression);
                }
            }

            if (getMvnoIdFromCurrentStaff() != 1) {
                booleanExpression = booleanExpression.and(qSubArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                booleanExpression = booleanExpression.and(qSubArea.isDeleted.eq(false));
            }


            Page<SubArea> subAreaList = subAreaRepository.findAll(booleanExpression, pageRequest);

            if (subAreaList != null && subAreaList.hasContent()) {
                makeGenericResponse(genericDataDTO, subAreaList);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("No records found.");
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Search failed: " + ex.getMessage());
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
                    return getSubAreaByName(filterList, pageRequest);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
    @Override
    public GenericDataDTO getListByPagination(PageRequest pageRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<SubArea> paginationList = subAreaRepository.findAllActiveSubAreas(pageRequest);
        if (null != paginationList && 0 < paginationList.getSize()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }
    @Override
    @GetMapping(path = "/all")
    public List<SubAreaDTO> getAllEntities() {
        List<Integer>mvnoIds=new ArrayList<>();
        if(getMvnoIdFromCurrentStaff()!=1){
            mvnoIds.add(getMvnoIdFromCurrentStaff());
        }
        mvnoIds.add(1);
        QSubArea qSubArea =QSubArea.subArea;
        BooleanExpression booleanExpression=qSubArea.isNotNull().and(qSubArea.isDeleted.eq(false)).and(qSubArea.mvnoId.in(mvnoIds));
        if (Objects.nonNull(getBUIdsFromCurrentStaff()) && !getBUIdsFromCurrentStaff().isEmpty()) {
            booleanExpression = booleanExpression.and(qSubArea.buId.eq(getBUIdsFromCurrentStaff().get(0)));
        }
        List<SubArea> subAreas= IterableUtils.toList(subAreaRepository.findAll(booleanExpression));
        List<SubAreaDTO> subAreaDTOList=subAreaMapper.domainToDTO(subAreas,new CycleAvoidingMappingContext());
        return subAreaDTOList;
    }

    public List<SubAreaAll> getAllSubareas() {
        List<Integer>mvnoIds=new ArrayList<>();
        if(getMvnoIdFromCurrentStaff()!=1){
            mvnoIds.add(getMvnoIdFromCurrentStaff());
        }
        mvnoIds.add(1);
        QSubArea qSubArea =QSubArea.subArea;
        QArea qArea = QArea.area;
        QPincode qPincode = QPincode.pincode1;
        QCountry qCountry = QCountry.country;
        QState qState = QState.state;
        QCity qCity = QCity.city;
        BooleanExpression booleanExpression=qSubArea.isNotNull().and(qSubArea.isDeleted.eq(false)).and(qSubArea.mvnoId.in(mvnoIds)).and(qSubArea.status.eq("Active"));
        if (Objects.nonNull(getBUIdsFromCurrentStaff()) && !getBUIdsFromCurrentStaff().isEmpty()) {
            booleanExpression = booleanExpression.and(qSubArea.buId.eq(getBUIdsFromCurrentStaff().get(0)));
        }
//        List<SubArea> subAreas= IterableUtils.toList(subAreaRepository.findAll(booleanExpression));

        JPAQuery<SubAreaAll> query = new JPAQuery<>(entityManager);
        List<SubAreaAll> subAreas = query.select(
                        Projections.constructor(SubAreaAll.class, qSubArea.id, qSubArea.name, qSubArea.status, qSubArea.mvnoId, qSubArea.isDeleted, qSubArea.countryId, qCountry.name, qSubArea.stateId, qState.name, qSubArea.cityId, qCity.name, qPincode.id, qPincode.pincode, qArea.id, qArea.name)
                )
                .from(qSubArea)
                .leftJoin(qSubArea.area, qArea)
                .leftJoin(qArea.pincode, qPincode)
                .leftJoin(qCountry).on(qCountry.id.eq(qSubArea.countryId))
                .leftJoin(qState).on(qState.id.eq(qSubArea.stateId))
                .leftJoin(qCity).on(qCity.id.eq(qSubArea.cityId))
                .where(booleanExpression)
                .fetch();

//        List<SubAreaDTO> subAreaDTOList=subAreaMapper.domainToDTO(subAreas,new CycleAvoidingMappingContext());
        return subAreas;
    }

    public GenericDataDTO getAllSubAreasWithPagination(int page, int pageSize,Long area) {
        List<Integer> mvnoIds = new ArrayList<>();
        if (getMvnoIdFromCurrentStaff() != 1) {
            mvnoIds.add(getMvnoIdFromCurrentStaff());
        }
        mvnoIds.add(1);

        QSubArea qSubArea = QSubArea.subArea;

        BooleanExpression booleanExpression = qSubArea.isNotNull()
                .and(qSubArea.isDeleted.eq(false))
                .and(qSubArea.mvnoId.in(mvnoIds))
                .and(qSubArea.status.eq("Active"));

        if (Objects.nonNull(getBUIdsFromCurrentStaff()) && !getBUIdsFromCurrentStaff().isEmpty()) {
            booleanExpression = booleanExpression.and(qSubArea.buId.eq(getBUIdsFromCurrentStaff().get(0)));
        }

        if (area != null) {
            booleanExpression = booleanExpression.and(qSubArea.area.id.eq(area));
        }

        JPAQuery<SubAreaAll> query = new JPAQuery<>(entityManager);

        long totalRecords = query.from(qSubArea).where(booleanExpression).fetchCount();

        List<SubAreaAll> subAreas = query.select(
                        Projections.bean(SubAreaAll.class,
                                qSubArea.id,
                                qSubArea.name,
                                qSubArea.status,
                                qSubArea.mvnoId)
                )
                .from(qSubArea)
                .where(booleanExpression)
                .orderBy(qSubArea.id.desc())
                .offset((long) (page - 1) * pageSize)
                .limit(pageSize)
                .fetch();

        GenericDataDTO response = new GenericDataDTO();
        response.setResponseCode(HttpStatus.OK.value());
        response.setResponseMessage("Success");
        response.setDataList(subAreas);
        response.setTotalRecords(totalRecords);
        response.setPageRecords(subAreas.size());
        response.setCurrentPageNumber(page);
        response.setTotalPages((long) Math.ceil((double) totalRecords / pageSize));

        return response;
    }


//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<SubArea> paginationList = null;
//        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
//        if(getMvnoIdFromCurrentStaff() == 1)
//            paginationList = subAreaRepository.findAll(pageRequest);
//        else {
//            long startTime = System.currentTimeMillis();
//            paginationList = subAreaRepository.findAllByMvnoIds(Arrays.asList(getMvnoIdFromCurrentStaff(), 1), pageRequest);
//        }
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }

    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        long startTime = System.currentTimeMillis();
        System.out.println("getListByPageAndSizeAndSortByAndOrderBy -- Start");

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<SubAreaDTO> paginationList = null;

        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        Integer currentMvnoId = getMvnoIdFromCurrentStaff();
        List<Integer> mvnoIds = Arrays.asList(1, currentMvnoId);

        try {
            if (currentMvnoId == 1) {
                paginationList = subAreaRepository.findAllProjected(pageRequest);
            } else {
                paginationList = subAreaRepository.findAllSubAreaByMvnoIds(mvnoIds, pageRequest);
            }

            if (paginationList != null && !paginationList.getContent().isEmpty()) {
                genericDataDTO.setDataList(paginationList.getContent());
                genericDataDTO.setTotalRecords(paginationList.getTotalElements());
            }

        } catch (Exception e) {
            System.out.println("Exception in getListByPageAndSizeAndSortByAndOrderBy: " + e.getMessage());
            e.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("getListByPageAndSizeAndSortByAndOrderBy -- End | Time taken: " + (endTime - startTime) + " ms");
        }

        return genericDataDTO;
    }


    @Override
    public SubAreaDTO getEntityById(Long id) throws Exception {
        try {
            SubArea domain = subAreaRepository.findSubAreaById(id);
            if (null == domain || domain.getDeleteFlag()) {
                throw new DataNotFoundException(getModuleNameForLog() + "--" + "Data not found for id " + id);
            }
            SubAreaDTO dto = getMapper().domainToDTO(domain, new CycleAvoidingMappingContext());
            if (dto != null && (getMvnoIdFromCurrentStaff() == 1 || (dto.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || dto.getMvnoId() == 1)))
                return dto;
            return null;
            /*if(null == dto){

            }*/
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                throw new DataNotFoundException();
            }
            //   ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Integer findIdByName(String name){
       return subAreaRepository.findIdByName(name);
    }
    public List<Integer> findIdsByName(String name){
       return subAreaRepository.findIdsByName(name);
    }


    public SubAreaDTO uploadDocumentsForSubArea(SubAreaDTO entityDTO, MultipartFile[] files) throws Exception {
        String SUBMODULE = "SubArea [uploadDocumentsForSubArea()] ";
        ClientService clientService= clientServiceSrv
                .getByNameAndMvnoId(ClientServiceConstant.SUBAREA_DOCPATH,getMvnoIdFromCurrentStaff());
        if(Objects.isNull(clientService)){
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(),
                    "File Path Not Found " , null);
        }
        String PATH =clientService.getValue();
            try {
                String subFolderName = "/" + entityDTO.getId() + "/";
                String path = PATH + subFolderName;

                String existingFilenames = entityDTO.getFilename() != null ? entityDTO.getFilename() : "";
                String existingUniqueNames = entityDTO.getUniquename() != null ? entityDTO.getUniquename() : "";

                StringBuilder filenames = new StringBuilder();
                StringBuilder uniqueNames = new StringBuilder();

                if (files != null) {
                    for (MultipartFile file : files) {
                        if (!file.isEmpty()) {
                            if (!isValidFileExtension(file.getOriginalFilename())) {
                                throw new CustomValidationException(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                                        "Unsupported file type: " + file.getOriginalFilename(), null);
                            }

                            String uniqueName = fileUtility.saveFileToServer(file, path);
                            if (filenames.length() > 0) {
                                filenames.append(",");
                            }
                            filenames.append(file.getOriginalFilename());
                            if (uniqueNames.length() > 0) {
                                uniqueNames.append(",");
                            }
                            uniqueNames.append(uniqueName);
                        }
                    }
                    if (!existingFilenames.isEmpty() && filenames.length() > 0) {
                        filenames.insert(0, existingFilenames + ",");
                    } else if (filenames.length() == 0 && !existingFilenames.isEmpty()) {
                        filenames.append(existingFilenames);
                    }

                    if (!existingUniqueNames.isEmpty() && uniqueNames.length() > 0) {
                        uniqueNames.insert(0, existingUniqueNames + ",");
                    } else if (uniqueNames.length() == 0 && !existingUniqueNames.isEmpty()) {
                        uniqueNames.append(existingUniqueNames);
                    }
                    entityDTO.setFilename(filenames.toString());
                    entityDTO.setUniquename(uniqueNames.toString());

                }
                } catch (Exception ex) {
                  logger.error(SUBMODULE + "Error uploading files: " + ex.getMessage(), ex);
                    throw ex;
                }
                return entityDTO;

        }
    private boolean isValidFileExtension(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        return lowerCaseFilename.endsWith(".png") ||
                lowerCaseFilename.endsWith(".jpeg") ||
                lowerCaseFilename.endsWith(".jpg") ||
                lowerCaseFilename.endsWith(".pdf");
    }

    public Resource getsubareadoc(Integer subareaId, String uniqueName) {
        Resource resource = null;
        String PATH = clientServiceSrv
                .getByNameAndMvnoId(ClientServiceConstant.SUBAREA_DOCPATH,getMvnoIdFromCurrentStaff()).getValue();

        try {
            String subFolderName = File.separator + subareaId + File.separator ;
            Path basePath = Paths.get(PATH + subFolderName);
            Path filePath = basePath.resolve(uniqueName).normalize();
            resource = new UrlResource(filePath.toUri());
            return resource;

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
            logger.error("Error while get subarea Doc : " + ex.getMessage() + " for SubareaID : " + subareaId);
        }
        return resource;
    }

    public File getsubareadocdelete(Integer subAreaId, String uniqueName) {
        String PATH = clientServiceSrv
                .getByNameAndMvnoId(ClientServiceConstant.SUBAREA_DOCPATH,getMvnoIdFromCurrentStaff()).getValue();
        try {
            String subFolderName = File.separator + subAreaId + File.separator;
            Path basePath = Paths.get(PATH + subFolderName);
            Path filePath = basePath.resolve(uniqueName).normalize();
            return filePath.toFile();
        } catch (Exception ex) {
            String errorMessage = "Error while retrieving get subarea doc delete: " + ex.getMessage();
            logger.error(errorMessage, ex);
            throw new CustomValidationException(500, errorMessage, ex);
        }
    }

    public boolean duplicateVerification(String name, Integer cityId, Integer stateId, Long id, Integer operation) {
        boolean flag = false;
        if(name != null) {
            name = name.trim();
            Long count = null;
            if (getMvnoIdFromCurrentStaff() == 1) {
                count = subAreaRepository.countByNameAndIsDeletedIsFalseAndCityIdAndStateId(name, cityId, stateId);
            } else if (getMvnoIdFromCurrentStaff() != 1){
                count = subAreaRepository.countByNameAndIsDeletedIsFalseAndCityIdAndStateIdAndMvnoIdIn(name, cityId, stateId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                if (count == 0) {
                    flag = true;
                }
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                if (count >= 1) {
                    Long countEdit = null;
                    if (getMvnoIdFromCurrentStaff() == 1) {
                        countEdit = subAreaRepository.countByNameAndIsDeletedIsFalseAndCityIdAndStateIdAndId(name, cityId, stateId, id);
                    } else {
                        countEdit = subAreaRepository.countByNameAndIsDeletedIsFalseAndCityIdAndStateIdAndMvnoIdInAndId(name, cityId, stateId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), id);
                    }
                    if (countEdit == 1) {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
            }
        }
        return flag;
    }
    public boolean duplicateVerification1(String name, Integer cityId, Integer stateId, Long areaId, Long pincodeId, Long id, Integer operation) {

        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Long count = null;

            // ===== COUNT DUPLICATES =====
            if (getMvnoIdFromCurrentStaff() == 1) {
                count = subAreaRepository.countDuplicate(name, cityId, stateId, areaId, pincodeId);
            } else {
                count = subAreaRepository.countDuplicateWithMvno(name, cityId, stateId, areaId, pincodeId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }

            // ===== ADD OPERATION =====
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                if (count == 0) {
                    flag = true;
                }

                // ===== UPDATE OPERATION =====
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                if (count >= 1) {
                    Long countEdit = null;
                    if (getMvnoIdFromCurrentStaff() == 1) {
                        countEdit = subAreaRepository.countDuplicateExcludingSelf(name, cityId, stateId, areaId, pincodeId, id);
                    } else {
                        countEdit = subAreaRepository.countDuplicateExcludingSelfWithMvno(name, cityId, stateId, areaId, pincodeId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), id);
                    }
                    if (countEdit == 0) {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public Long findPincodeIdByAreaId(Long areaId) {
        Long pincodeId = null;

        if (areaId != null) {
            pincodeId = areaRepository.findPincodeIdByAreaId(areaId);
        }

        return pincodeId;
    }
}
