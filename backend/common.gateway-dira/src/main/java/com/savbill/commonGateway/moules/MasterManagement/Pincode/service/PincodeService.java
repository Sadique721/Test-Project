package com.savbill.commonGateway.moules.MasterManagement.Pincode.service;




import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.constants.SubscriberConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericRequestDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Area.model.AreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.City.service.CityService;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.Pincode;


import com.savbill.commonGateway.moules.MasterManagement.Pincode.domain.QPincode;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.mapper.PincodeMapper;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeDTO;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeDetailDTO;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.model.PincodeRespDTO;
import com.savbill.commonGateway.moules.MasterManagement.Pincode.repository.PincodeRepository;


import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.QServiceAreaPincodeRel;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceAreaPincodeRel;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaPincodeRelRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import com.savbill.commonGateway.moules.MasterManagement.State.service.StateService;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PincodeService extends ExBaseAbstractService<PincodeDTO, Pincode, Long> {

    public PincodeService(PincodeRepository repository, PincodeMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PincodeService]";
    }

    @Autowired
    private PincodeRepository pincodeRepository;
    @Autowired
    private CityService cityService;
    @Autowired
    private StateService stateService;
    @Autowired
    private CountryService countryService;

    @Autowired
    private ServiceAreaPincodeRelRepository serviceAreaPincodeRelRepository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private PincodeMapper pincodeMapper;

    public PincodeDetailDTO getDetailsByPin(String pincode) throws Exception {
        String SUBMODULE = " [getDetailsByPin()] ";
        PincodeDetailDTO detailsModel = new PincodeDetailDTO();
        try {
            if (pincode.length() == SubscriberConstants.PINCODE_LENGTH) {
                Pincode entity = pincodeRepository.findByPincodeAndIsDeletedIsFalse(pincode);
                if (getMvnoIdFromCurrentStaff() == 1 || entity.getMvnoId() == 1 || entity.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()) {
                    if (entity != null) {
                        //Set City
                        City city = cityService.get(entity.getCityId());
                        if (city != null) {
                            GenericRequestDTO cityModel = new GenericRequestDTO();
                            cityModel.setId(city.getId().longValue());
                            cityModel.setName(city.getName());
                            detailsModel.setCity(cityModel);
                        }

                        //Set Country
                        Country country = countryService.get(entity.getCountryId());
                        if (country != null) {
                            GenericRequestDTO countryModel = new GenericRequestDTO();
                            countryModel.setId(country.getId().longValue());
                            countryModel.setName(country.getName());
                            detailsModel.setCountry(countryModel);
                        }

                        //Set State
                        State state = stateService.get(entity.getStateId());
                        if (state != null) {
                            GenericRequestDTO stateModel = new GenericRequestDTO();
                            stateModel.setId(state.getId().longValue());
                            stateModel.setName(state.getName());
                            detailsModel.setState(stateModel);
                        }

                        //Set Area
                        if (entity.getAreaList().size() > 0) {
                            List<GenericRequestDTO> areaList = new ArrayList<>();
                            entity.getAreaList().forEach(data -> {
                                GenericRequestDTO area = new GenericRequestDTO();
                                area.setName(data.getName());
                                area.setId(data.getId());
                                areaList.add(area);
                            });
                            detailsModel.setAreaList(areaList);
                        }

                        //Set pincode
                        GenericRequestDTO pinCodeModel = new GenericRequestDTO();
                        pinCodeModel.setId(entity.getId());
                        pinCodeModel.setName(entity.getPincode());
                        detailsModel.setPincode(pinCodeModel);
                    } else {
                        throw new RuntimeException("Pincode not found!!");
                    }
                } else {
                    throw new RuntimeException("Pincode not found!!");
                }
            } else {
                throw new RuntimeException("Please provide valid pin code");
            }
        } catch (RuntimeException ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return detailsModel;
    }

    public GenericDataDTO getPincode(String pincode, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPincode()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Page<Pincode> qosPolicyList = null;
            if(getMvnoIdFromCurrentStaff() == 1)
                qosPolicyList = pincodeRepository.findAllByPincodeContainingIgnoreCaseAndIsDeletedIsFalse(pincode, pageRequest);
            else
                qosPolicyList = pincodeRepository.findAllByPincodeContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(pincode, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (null != qosPolicyList && 0 < qosPolicyList.getSize()) {
                makeGenericResponse(genericDataDTO, qosPolicyList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public List<PincodeDTO> getAllPincodeBySearch(String s1) {
        String SUBMODULE = getModuleNameForLog() + " [getAllPincodeBySearch()] ";
        List<Pincode> pincodeList = null;
        Page<Pincode> qosPolicyList = null;
        try {
            QPincode qPincode = QPincode.pincode1;
            //List<Pincode> entity = pincodeRepository.findByPincode(s1.trim());
            List<Country> country = countryService.getName(s1);
            List<State> state = stateService.getName(s1);
            List<City> city = cityService.getName(s1);
            BooleanExpression booleanExpression = qPincode.isNotNull()
                   // .and(qPincode.isDeleted.eq(false))
                    .and(qPincode.pincode.containsIgnoreCase(s1))
                    .or(qPincode.status.containsIgnoreCase(s1));
            if(country != null){
                booleanExpression = booleanExpression.or(qPincode.countryId.in(country.stream().map(st->st.getId()).collect(Collectors.toList())));
            }
            if(state != null && state.size() > 0){
                booleanExpression = booleanExpression.or(qPincode.stateId.in(state.stream().map(st->st.getId()).collect(Collectors.toList())));
            }
            if(city != null  && city.size() > 0){
                //booleanExpression = booleanExpression.or(qPincode.cityId.eq(city.getId()));
                booleanExpression = booleanExpression.or(qPincode.cityId.in(city.stream().map(st->st.getId()).collect(Collectors.toList())));
            }
            //pincodeList = pincodeRepository.findAllByPincodeStartingWithAndIsDeletedIsFalse(s1);
            booleanExpression =booleanExpression.and(qPincode.isDeleted.eq(false));
            pincodeList = (List<Pincode>) pincodeRepository.findAll(booleanExpression);

            if (null != pincodeList && 0 < pincodeList.size()) {
                pincodeList.sort(Comparator.comparing(pincode -> pincode.getCreatedate()));
                Collections.reverse(pincodeList);
                return pincodeList.stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext()))
                        .collect(Collectors.toList())
                        .stream().filter(pincodeDTO -> pincodeDTO.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || pincodeDTO.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
            }
            return new ArrayList<>();
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }



    @Override
    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        Integer count=pincodeRepository.deleteVerify(id);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
                        return getPincode(searchModel.getFilterValue(), pageRequest);
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
        Sheet sheet = workbook.createSheet("Pincode");
        createExcel(workbook, sheet, AreaDTO.class, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                AreaDTO.class.getDeclaredField("id"),
                AreaDTO.class.getDeclaredField("pincode"),
        };
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, AreaDTO.class, getFields());
    }
    
    @Override
    public boolean duplicateVerifyAtSave(String pincode) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (pincode != null) {
        	pincode = pincode.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = pincodeRepository.duplicateVerifyAtSave(pincode);
            else count = pincodeRepository.duplicateVerifyAtSave(pincode, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }


    public boolean duplicateVerifyAtSaveWithPincodeAndCityID(String pincode, Integer cityId) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (pincode != null&& cityId!=null) {
            pincode = pincode.trim();
            cityId = cityId.intValue();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = pincodeRepository.duplicateVerifyAtSaveWithPincodeAndCityID(pincode, cityId);
            else count = pincodeRepository.duplicateVerifyAtSaveWithPincodeAndCityID(pincode, cityId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    
    
    public boolean duplicateVerifyAtEdit(String pincode, Long id,Integer cityId) throws Exception {
        boolean flag = false;
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
        if (pincode != null) {
        	pincode = pincode.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = pincodeRepository.duplicateVerifyAtSaveWithPincodeAndCityID(pincode,cityId);
            else count = pincodeRepository.duplicateVerifyAtSaveWithPincodeAndCityID(pincode,cityId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = pincodeRepository.duplicateVerifyAtEdit(pincode,id,cityId);
                else countEdit = pincodeRepository.duplicateVerifyAtEdit(pincode, id,cityId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Pincode> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        if(getMvnoIdFromCurrentStaff() == 1)
            paginationList = pincodeRepository.findAll(pageRequest);
        else
            paginationList = pincodeRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }
    public String getPincode(Long id){
       return pincodeRepository.getPincode(id);
    }
    public Pincode getPinCodeById(Long id){
        return pincodeRepository.findById(id).get();
    }

    public List<Pincode> getPincodeListByServiceId(List<Long> serviceAreaIds) throws Exception{
        try {
            QPincode qPincode = QPincode.pincode1;
            QServiceAreaPincodeRel qServiceAreaPincodeRel = QServiceAreaPincodeRel.serviceAreaPincodeRel;
//            List<Pincode> pincodeList = new ArrayList<>();
            BooleanExpression booleanExpression = qServiceAreaPincodeRel.isNotNull().and(qServiceAreaPincodeRel.isDeleted.eq(false));
            booleanExpression = booleanExpression.and(qServiceAreaPincodeRel.serviceArea.id.in(serviceAreaIds));
            List<ServiceAreaPincodeRel> serviceAreaPincodeRelList = (List<ServiceAreaPincodeRel>) serviceAreaPincodeRelRepository.findAll(booleanExpression);
            List<Long> pincodes = serviceAreaPincodeRelList.stream().map(ServiceAreaPincodeRel::getPincodeData).collect(Collectors.toList()).stream().map(Pincode::getId).collect(Collectors.toList());
            BooleanExpression booleanExpPincode = qPincode.isNotNull().and(qPincode.isDeleted.eq(false)).and(qPincode.id.in(pincodes)).and(qPincode.status.equalsIgnoreCase("Active"));
            if (getLoggedInUserId() != 1) {
                booleanExpPincode = booleanExpPincode.and(qPincode.mvnoId.eq(getMvnoIdFromCurrentStaff()));
            }
            List<Pincode> finalPincodeList = (List<Pincode>) pincodeRepository.findAll(booleanExpPincode);
            return finalPincodeList;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<Pincode> findByName(String pincode){
        return pincodeRepository.findByPincodeContainingIgnoreCase(pincode);
    }

    public List<PincodeRespDTO> findAllPincode() {
        List<PincodeRespDTO> pincodeList;
        if(getMvnoIdFromCurrentStaff()!=1) {
            pincodeList =  pincodeRepository.findAll(Arrays.asList(getMvnoIdFromCurrentStaff(),1));
        }else{
            pincodeList = pincodeRepository.findAllPinCode();
        }
        pincodeList.sort(Comparator.comparing(PincodeRespDTO::getPincodeid).reversed());
        return pincodeList;
    }

    /**
     * Duplicate Verification
     * @Author Darshan
     * @param id
     * @param operation
     * @return
     */
    public boolean duplicateVerification(String pincode, Integer cityId, Long id, Integer operation) {
        boolean flag = false;
        if(pincode != null) {
            pincode = pincode.trim();
            Long count = null;
            if (getMvnoIdFromCurrentStaff() == 1) {
                count = pincodeRepository.countByPincodeAndIsDeletedIsFalseAndAndCityId(pincode, cityId);
            } else if (getMvnoIdFromCurrentStaff() != 1){
                count = pincodeRepository.countByPincodeAndIsDeletedIsFalseAndAndCityIdAndMvnoIdIn(pincode, cityId,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                if (count == 0) {
                    flag = true;
                }
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                if (count >= 1) {
                    Long countEdit = null;
                    if (getMvnoIdFromCurrentStaff() == 1) {
                        countEdit = pincodeRepository.countByPincodeAndIsDeletedIsFalseAndAndCityIdAndId(pincode, cityId, id);
                    } else {
                        countEdit = pincodeRepository.countByPincodeAndIsDeletedIsFalseAndAndCityIdAndMvnoIdInAndId(pincode, cityId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), id);
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


    public Integer getPinCodeByName(String name) {
        return pincodeRepository.findIdByPincode(name);
    }


    public Map<String, Object> getServiceAreaIdForPincodeId(Long pincodeid) {
        Integer serviceAreaId = serviceAreaPincodeRelRepository
                .getServiceAreaIdFromPincodeId(pincodeid, getMvnoIdFromCurrentStaff());

        if (serviceAreaId == null) {
            return null;
        }

        // ID se ServiceArea fetch
        ServiceArea serviceArea = serviceAreaRepository
                .findById(serviceAreaId.longValue())
                .orElse(null);

        Map<String, Object> data = new HashMap<>();
        data.put("serviceAreaId", serviceAreaId);

        if (serviceArea != null) {
            data.put("serviceAreaName", serviceArea.getName());
        } else {
            data.put("serviceAreaName", null);
        }

        return data;
    }

    public Pincode get(Long id) {
        Pincode pincode = pincodeRepository.findById(id).orElse(null);
        if (getMvnoIdFromCurrentStaff() == 1 ||
                (pincode.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() ||
                        pincode.getMvnoId() == 1))
            return pincode;
        return null;
    }



    public PincodeDTO getPincodeDTO(Long id) {
        Object row1 = pincodeRepository.getPincodeDtoById(id)[0];
        List<String> areaList = pincodeRepository.getAreaList(id);
        StringBuilder builder = new StringBuilder();
        for (String s : areaList) {
            builder.append(s).append(", ");
        }
        Object[] row = (Object[]) row1;
        PincodeDTO pincodeDTO = new PincodeDTO();
        pincodeDTO.setPincodeid(((BigInteger) row[0]).longValue());
        pincodeDTO.setPincode((String) row[1]);
        pincodeDTO.setStatus((String) row[2]);
        pincodeDTO.setCountryId(((BigInteger) row[3]).intValue());
        pincodeDTO.setStateId(((BigInteger) row[4]).intValue());
        pincodeDTO.setCityId(((BigInteger) row[5]).intValue());
        pincodeDTO.setCityName((String) row[6]);
        pincodeDTO.setStateName((String) row[7]);
        pincodeDTO.setCountryName((String) row[8]);
        pincodeDTO.setDisplayId(((BigInteger) row[10]).longValue());
        pincodeDTO.setDisplayName((String) row[11]);
        pincodeDTO.setMvnoId(((BigInteger) row[12]).intValue());
        pincodeDTO.setCreatedate(((Timestamp) row[13]).toLocalDateTime());
        pincodeDTO.setUpdatedate(((Timestamp) row[14]).toLocalDateTime());
        pincodeDTO.setCreatedByName((String) row[15]);
        pincodeDTO.setLastModifiedByName((String) row[16]);
        pincodeDTO.setCreatedById(((BigInteger) row[17]).intValue());
        pincodeDTO.setLastModifiedById(((BigInteger) row[18]).intValue());
        pincodeDTO.setAreas(builder.toString());
        return pincodeDTO;
    }

    public List<PincodeDTO> getAllPincodes() throws Exception {
        try {
            Integer currentStaffMvnoId = getMvnoIdFromCurrentStaff();

            // Call efficient repository method with filtering in DB
            List<Pincode> filteredBranches = pincodeRepository.findByIsDeletedFalseAndMvnoIdIn(Arrays.asList(1, currentStaffMvnoId));

            // Map entities to DTOs
            return filteredBranches.stream()
                    .map(pincodes -> pincodeMapper.domainToDTO(pincodes, new CycleAvoidingMappingContext()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }
}

