package com.savbill.commonGateway.moules.MasterManagement.Area.service;



import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.exceptions.DataNotFoundException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Area.domain.Area;

import com.savbill.commonGateway.moules.MasterManagement.Area.domain.QArea;
import com.savbill.commonGateway.moules.MasterManagement.Area.mapper.AreaMapper;
import com.savbill.commonGateway.moules.MasterManagement.Area.model.AreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.Area.model.NewAreaDto;
import com.savbill.commonGateway.moules.MasterManagement.Area.repository.AreaRepository;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.City.service.CityService;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.service.PincodeService;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceAreaPincodeRel;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaPincodeRelRepository;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import com.savbill.commonGateway.moules.MasterManagement.State.service.StateService;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.savbill.commonGateway.moules.MasterManagement.SubArea.Repository.SubAreaRepository;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AreaService extends ExBaseAbstractService<AreaDTO, Area, Long> {

    public AreaService(AreaRepository repository, AreaMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[AreaService]";
    }

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private CountryService countryService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CityService cityService;

    @Autowired
    private PincodeService pincodeService;
    @Autowired
    ServiceAreaPincodeRelRepository serviceAreaPincodeRelRepository;

    @Autowired
    private SubAreaRepository subAreaRepository;


    public GenericDataDTO getAreaByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<Area> qosPolicyList = null;
            QArea qArea = QArea.area;
            Country country = countryService.getByName(name);
            List<State> state = stateService.getByName(name);
            List<City> city = cityService.getCityByName(name);
            List<Pincode> pincode = pincodeService.findByName(name);
            boolean flag = false;
            BooleanExpression booleanExpression = qArea.isNotNull()
                  //  .and(qArea.isDeleted.eq(false))
                    .and(qArea.name.likeIgnoreCase("%" + name.trim() + "%"))
                    .or(qArea.status.equalsIgnoreCase(name.trim()));
            if(country != null){
                booleanExpression = booleanExpression.or(qArea.countryId.eq(country.getId()));
                //flag = true;
            }
            if(state != null && state.size() > 0){
                //booleanExpression = booleanExpression.or(qArea.stateId.eq(state.stream().map(st->st.getId()).findAny().get()));
                booleanExpression = booleanExpression.or(qArea.stateId.in(state.stream().map(st->st.getId()).collect(Collectors.toList())));
            }
            if(city != null && city.size() > 0){
                //booleanExpression = booleanExpression.or(qArea.cityId.eq(city.getId()));
                booleanExpression = booleanExpression.or(qArea.cityId.in(city.stream().map(st->st.getId()).collect(Collectors.toList())));
            }
            if(pincode != null && pincode.size() > 0){
                //booleanExpression = booleanExpression.or(qArea.pincode.id.eq(pincode.getId()));
                booleanExpression = booleanExpression.or(qArea.pincode.id.in(pincode.stream().map(pc->pc.getId()).collect(Collectors.toList())));
            }
            if(getMvnoIdFromCurrentStaff() == 1) {
                //qosPolicyList = areaRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
                qosPolicyList = areaRepository.findAll(booleanExpression, pageRequest);
            }else {
                //qosPolicyList = areaRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                booleanExpression = booleanExpression.and(qArea.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                booleanExpression = booleanExpression.and(qArea.isDeleted.eq(false));
                qosPolicyList = areaRepository.findAll(booleanExpression, pageRequest);
            }
            if (null != qosPolicyList && 0 < qosPolicyList.getSize()) {
                makeGenericResponse(genericDataDTO, qosPolicyList);
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
                        return getAreaByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Area");
        createExcel(workbook, sheet, AreaDTO.class, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                AreaDTO.class.getDeclaredField("id"),
                AreaDTO.class.getDeclaredField("name"),
        };
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, AreaDTO.class, getFields());
    }
    
    public Area getById(Long id) {
    	return areaRepository.findById(id).get();
    }


    public boolean deleteVerification(Pincode pincode)throws Exception{
        boolean flag = false;
        List<ServiceAreaPincodeRel> serviceAreaPincodeRel= serviceAreaPincodeRelRepository.findByPincodeData(pincode);
        if(serviceAreaPincodeRel.size()==0){
            flag=true;
        }
        return flag;
    }

    public boolean isAreaReferencedInSubArea(Long areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new IllegalArgumentException("Area not found with id: " + areaId));
        return subAreaRepository.existsByArea(area);
    }


    public boolean duplicateVerifyAtSave(String name,Integer countryId,Integer stateId,Integer cityId,Integer pincodeId ) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (name != null) {
        	name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count =areaRepository.duplicateVerifyAtSave(name,countryId,stateId,cityId,pincodeId);
            else count = areaRepository.duplicateVerifyAtSave(name,countryId,stateId,cityId,pincodeId, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }
    
    
    public boolean duplicateVerifyAtEdit(String name, Long id,Integer countryId,Integer stateId,Integer cityId,Integer pincodeId) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (name != null) {
        	name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = areaRepository.duplicateVerifyAtSave(name,countryId,stateId,cityId,pincodeId);
            else count = areaRepository.duplicateVerifyAtSave(name,countryId,stateId,cityId,pincodeId, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = areaRepository.duplicateVerifyAtEdit(name,id,countryId,stateId,cityId,pincodeId);
                else countEdit = areaRepository.duplicateVerifyAtEdit(name, id,countryId,stateId,cityId,pincodeId, mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<Area> paginationList;
//        PageRequest pageRequest = generatePageRequestforArea(page, size, sortBy, sortOrder);
//
//        Integer currentMvnoId = getMvnoIdFromCurrentStaff();
//
//        if (currentMvnoId == 1) {
//            paginationList = areaRepository.findAllByAreas(pageRequest);
//        } else {
//            paginationList = areaRepository.findAllAreasByMvnoIds(Arrays.asList(currentMvnoId, 1), pageRequest);
//        }
//
//        if (paginationList != null && !paginationList.getContent().isEmpty()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//
//        return genericDataDTO;
//    }
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {

        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        Integer currentMvnoId = getMvnoIdFromCurrentStaff();

        Page<Area> paginationList = (currentMvnoId == 1)
                ? areaRepository.findAllByAreas(pageRequest)
                : areaRepository.findAllAreaByMvnoIds(Arrays.asList(currentMvnoId, 1), pageRequest);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (!paginationList.isEmpty()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }

        return genericDataDTO;
    }



    public List<Area> getAreaByPincodeId(Long pincodeId) {
        List<Area> areaList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                areaList = areaRepository.findAllByIsDeletedIsFalseAndPincode_Id(pincodeId);
            } else {
                areaList = areaRepository.findAllByIsDeletedIsFalseAndPincodeIdAndMvnoIdIn(pincodeId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return areaList;
    }


    public Integer getIdByAreaName(String name){
        return areaRepository.findByNameAndIsDeletedFalse(name);
    }



    public GenericDataDTO getAllEntityWithoutPagination() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<NewAreaDto> areaList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                areaList = areaRepository.getIdAndName();
            }else{
                areaList = areaRepository.getIdAndNameByMvnoIds(Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
            makeResponse(genericDataDTO,areaList);
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return genericDataDTO;
    }

    public GenericDataDTO makeResponse(GenericDataDTO genericDataDTO, List<NewAreaDto> areaList) {
        genericDataDTO.setDataList(areaList);
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(areaList.size());
        genericDataDTO.setPageRecords(0);
        genericDataDTO.setCurrentPageNumber(0);
        genericDataDTO.setTotalPages(0);
        return genericDataDTO;
    }
    public GenericDataDTO getAllAreasWithDetails() {
        GenericDataDTO response = new GenericDataDTO();
        List<NewAreaDto> areaList;
        try {
            int mvnoId = getMvnoIdFromCurrentStaff();
            if (mvnoId == 1) {
                areaList = areaRepository.findAllAreas();
            } else {
                areaList = areaRepository.findAreasByMvnoIds(Arrays.asList(mvnoId, 1));
            }
            response.setData(areaList);
            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("Data fetched successfully");
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return response;
    }

//    @Override
//    public AreaDTO getEntityById(Long id) throws Exception {
//        long startTime = System.currentTimeMillis();
//        System.out.println(getModuleNameForLog() + " -- Start fetching entity by ID: " + id);
//
//        try {
//            Area domain = areaRepository.findAreaById(id);
//            if (domain == null || Boolean.TRUE.equals(domain.getDeleteFlag())) {
//                throw new DataNotFoundException(getModuleNameForLog() + " -- Data not found for id " + id);
//            }
//
//            AreaDTO dto = getMapper().domainToDTO(domain, new CycleAvoidingMappingContext());
//
//            if (dto != null && (getMvnoIdFromCurrentStaff() == 1
//                    || dto.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue()
//                    || dto.getMvnoId() == 1)) {
//                return dto;
//            }
//
//            return null;
//
//        } catch (Exception ex) {
//            if (ex instanceof NoSuchElementException) {
//                throw new DataNotFoundException();
//            }
//            throw ex;
//        } finally {
//            long endTime = System.currentTimeMillis();
//            System.out.println(getModuleNameForLog() + " -- End fetching entity by ID: " + id + " | Time taken: " + (endTime - startTime) + " ms");
//        }
//    }
@Override
public AreaDTO getEntityById(Long id) throws Exception {
    long startTime = System.currentTimeMillis();
    System.out.println(getModuleNameForLog() + " -- Start fetching entity by ID: " + id);

    try {
        Area domain = areaRepository.findAreaById(id);
        if (domain == null || Boolean.TRUE.equals(domain.getDeleteFlag())) {
            throw new DataNotFoundException(getModuleNameForLog() + " -- Data not found for id " + id);
        }

        AreaDTO dto = getMapper().domainToDTO(domain, new CycleAvoidingMappingContext());
        if (dto == null) return null;

        Integer currentMvnoId = getMvnoIdFromCurrentStaff();
        Integer dtoMvnoId = dto.getMvnoId();

        if (Objects.equals(currentMvnoId, 1) || Objects.equals(dtoMvnoId, currentMvnoId) || Objects.equals(dtoMvnoId, 1)) {
            return dto;
        }

        return null;

    } catch (NoSuchElementException ex) {
        throw new DataNotFoundException();
    } catch (Exception ex) {
        throw ex;
    } finally {
        long endTime = System.currentTimeMillis();
        System.out.println(getModuleNameForLog() + " -- End fetching entity by ID: " + id + " | Time taken: " + (endTime - startTime) + " ms");
    }
}




}
