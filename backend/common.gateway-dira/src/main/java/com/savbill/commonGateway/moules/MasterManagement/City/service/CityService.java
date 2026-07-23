package com.savbill.commonGateway.moules.MasterManagement.City.service;


import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.service.AbstractService;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.City.controller.CityController;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.City;
import com.savbill.commonGateway.moules.MasterManagement.Country.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.savbill.commonGateway.moules.MasterManagement.City.domain.QCity;
import com.savbill.commonGateway.moules.MasterManagement.City.model.CityPojo;
import com.savbill.commonGateway.moules.MasterManagement.City.repository.CityRepository;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import com.savbill.commonGateway.moules.MasterManagement.State.service.StateService;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.spring.MessagesPropertyConfig;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService extends AbstractService<City, CityPojo, Integer> {

    public static final String MODULE = "[CityService]";

    public CityService() {
        sortColMap.put("stateName", "state.name");
        sortColMap.put("countryName", "country.name");
        sortColMap.put("id", "cityid");
    }

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private CityRepository entityRepository;

    @Autowired
    private CountryService countryService;

    @Autowired
    private StateService stateService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CreateDataSharedService createDataSharedService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CityController.class);
    @Override
    public JpaRepository<City, Integer> getRepository() {
        return entityRepository;
    }
    
    public Page<City> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if(getMvnoIdFromCurrentStaff() == 1)
            return entityRepository.findAll(pageRequest);
        if (null == filterList || 0 == filterList.size())
            return entityRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
    }

    public Page<City> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.searchEntity(searchText, pageRequest, getMvnoIdFromCurrentStaff());
    }

    public List<CityPojo> getAllActiveEntities() {
        return entityRepository.findAllActiveCitiesByStatusAndMvnoId(CommonConstants.ACTIVE_STATUS,getMvnoIdFromCurrentStaff());
//        		.stream().filter(city -> city.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || city.getMvnoId() == null || city.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());

    }

    public List<City> getAllEntities() {
        return entityRepository.findAll()
        		.stream().filter(city -> city.getMvnoId() == getMvnoIdFromCurrentStaff() || city.getMvnoId() == null).collect(Collectors.toList());
    }

    public List<City> findByState(State state) {
        return entityRepository.findByStateAndIsDeleteIsFalseOrderByIdDesc(state)
        		.stream().filter(city -> city.getMvnoId() == getMvnoIdFromCurrentStaff() || city.getMvnoId() == null).collect(Collectors.toList());
    }

    @Override
    public boolean deleteVerification(Integer id)throws Exception
    {
        boolean flag=false;
        Integer count=entityRepository.deleteVerify(id);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    public void deleteCity(Integer id) throws Exception {
        String SUBMODULE = MODULE + "deleteCity()";
        try {
            City city = entityRepository.getOne(id);
            boolean flag=this.deleteVerification(city.getId());
            if(flag){
                city.setIsDelete(true);
                entityRepository.save(city);
                //CityMessage cityMessage = new CityMessage(city);
                //this.messageSender.send(cityMessage, RabbitMqConstants.QUEUE_CITY);
                createDataSharedService.deleteEntityDataForAllMicroService(city);
            }else{
                throw new RuntimeException(DeleteContant.CITY_DELETE_EXIST);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public City getCityForAdd() {
        return new City();
    }

    public City getCityForEdit(Integer id) throws Exception {
        return entityRepository.getOne(id);
    }

    public City saveCity(City city) throws Exception {
    	if(getMvnoIdFromCurrentStaff() != null) {
    		city.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        return entityRepository.save(city);

    }

    public CityPojo save(CityPojo pojo) throws Exception {
        String SUBMODULE = MODULE + "save()";
        try {
            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
            City obj = convertCityPojoToCityModel(pojo);
            obj = saveCity(obj);
            pojo = convertCityModelToCityPojo(obj);
            //CityMessage cityMessage = new CityMessage(obj);
            //this.messageSender.send(cityMessage, RabbitMqConstants.QUEUE_CITY);
            createDataSharedService.sendEntitySaveDataForAllMicroService(obj);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count = entityRepository.duplicateVerifyAtSave(name);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }
    @Override
    public boolean duplicateVerifyStateAtSave(String name , Integer countryId,Integer STATEID) {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyStateAtSave(name, countryId, STATEID);
            else count = entityRepository.duplicateVerifyStateAtSave(name,countryId,STATEID, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public CityPojo update(CityPojo pojo, HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + "update()";
        Integer respCode = APIConstants.FAIL;
        City old1=get(pojo.getId());
        try {
            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
            City dvValue = getCityForUpdateAndDelete(pojo.getId());
            City obj = convertCityPojoToCityModel(pojo);
            City newcity=new City(obj,pojo.getId());
            getCityForUpdateAndDelete(obj.getId());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Update City "+LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + " , Updated City Details " + UpdateDiffFinder.getUpdatedDiff(old1,obj)+LogConstants.LOG_STATUS+LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS  );

            //String updatedValues = CommonUtils.getUpdatedDiff(convertCityModelToCityPojo(obj),convertCityModelToCityPojo(old1));
            obj = saveCity(obj);
            pojo = convertCityModelToCityPojo(obj);
            //CityMessage cityMessage = new CityMessage(obj);
            //this.messageSender.send(cityMessage, RabbitMqConstants.QUEUE_CITY);
            createDataSharedService.updateEntityDataForAllMicroService(obj);

        } catch (Exception ex) {
            LOGGER.error("Request From : "+ req.getHeader("requestFrom")+", Request for : "+", Request to update city :  "+pojo.getName()+", Requested by : "+ getLoggedInUser().getFirstName()+", Status : FAILED " + ", ERROR : " + ex.getMessage());
            throw ex;
        }
        return pojo;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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
    public boolean duplicateVerifyStateAtEdit(String name ,Integer countryId , Integer STATEID, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count = entityRepository.duplicateVerifyStateAtSave(name,countryId,STATEID);
            if (count >= 1) {
                Integer countEdit = entityRepository.duplicateVerifyStateAtEdit(name,countryId,STATEID, id);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public City convertCityPojoToCityModel(CityPojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [convertCityPojoToCityModel()] ";
        City city = null;
        try {
            if (pojo != null) {
                city = new City();
                if (pojo.getId() != null) {
                    city.setId(pojo.getId());
                }
                city.setName(pojo.getName());
                city.setStatus(pojo.getStatus());
                if(pojo.getMvnoId() != null) {
                	city.setMvnoId(pojo.getMvnoId());
                }
                CountryService countryService = SpringContext.getBean(CountryService.class);
                if (countryService.get(pojo.getCountryId()) != null) {
                    city.setCountryId(pojo.getCountryId());
                } else {
                    throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.country.not.available"), null);
                }
                StateService stateService = SpringContext.getBean(StateService.class);
                if (stateService.get(pojo.getStatePojo().getId()) != null) {
                    city.setState(stateService.get(pojo.getStatePojo().getId()));
                } else {
                    throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.state.not.available"), null);
                }
                return city;
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return city;
    }


    public CityPojo convertCityModelToCityPojo(City city) throws Exception {
        String SUBMODULE = MODULE + " [convertCityModelToCityPojo()] ";
        CityPojo pojo = null;
        try {
            if (city != null) {
                pojo = new CityPojo();
                pojo.setId(city.getId());
                pojo.setName(city.getName());
                pojo.setStatus(city.getStatus());
                pojo.setCreatedate(city.getCreatedate());
                pojo.setUpdatedate(city.getUpdatedate());
                pojo.setCreatedById(city.getCreatedById());
                pojo.setLastModifiedById(city.getLastModifiedById());
                pojo.setCreatedByName(city.getCreatedByName());
                pojo.setLastModifiedByName(city.getLastModifiedByName());
                pojo.setCountryId(city.getCountryId());
                pojo.setStateName(city.getState().getName());
                if(city.getMvnoId() != null) {
                	pojo.setMvnoId(city.getMvnoId());
                }
                pojo.setDisplayId(city.getId());
                pojo.setDisplayName(city.getName());
                CountryService countryService = SpringContext.getBean(CountryService.class);
                pojo.setCountryName(countryService.get(city.getCountryId()) != null ? countryService.get(city.getCountryId()).getName() : null);
                StateService stateService = SpringContext.getBean(StateService.class);
                pojo.setStatePojo(stateService.convertStateModelToStatePojo(city.getState()));
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public List<CityPojo> convertResponseModelIntoPojo(List<City> cityList) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
        List<CityPojo> pojoListRes = new ArrayList<>();
        try {
            if (cityList != null && cityList.size() > 0) {
                for (City city : cityList) {
                    pojoListRes.add(convertCityModelToCityPojo(city));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;

    }

    public void validateRequest(CityPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)
                || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) {
            if (entityRepository.findById(pojo.getId()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.city.not.found"), null);
            }
        }
        if (!operation.equals(CommonConstants.OPERATION_DELETE) && pojo != null && pojo.getCountryId() != null) {
            if (countryService.get(pojo.getCountryId()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.city.not.found"), null);
            }
        }
        if (!operation.equals(CommonConstants.OPERATION_DELETE) && pojo != null && pojo.getStatePojo().getId() != null) {
            if (stateService.get(pojo.getStatePojo().getId()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.state.not.found"), null);
            }
        }
    }

    public List<City> getName(String n){
        QCity qCity = QCity.city;
        BooleanExpression booleanExpression = qCity.isNotNull()
                .and(qCity.name.containsIgnoreCase(n));
        return (List<City>) entityRepository.findAll(booleanExpression);
    }
    public List<City> getCityByName(String name) {
        return entityRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("City");
        List<CityPojo> cityPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, CityPojo.class, cityPojoList, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                CityPojo.class.getDeclaredField("id"),
                CityPojo.class.getDeclaredField("name"),
                CityPojo.class.getDeclaredField("status"),
                CityPojo.class.getDeclaredField("stateName"),
                CityPojo.class.getDeclaredField("countryName"),
        };
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<CityPojo> cityPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, CityPojo.class, cityPojoList, getFields());
    }

    @Override
    public Page<City> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getCityByNameOrStateNameOrCountryName(searchModel.getFilterValue(), pageRequest);
                    }
                } else
                    throw new RuntimeException("Please Provide Search Column!");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public Page<City> getCityByNameOrStateNameOrCountryName(String s1, PageRequest pageRequest) {
        if(getMvnoIdFromCurrentStaff() == 1)
            return entityRepository.findAllByNameContainingIgnoreCaseOrState_NameAndIsDeleteIsFalse(s1, s1, s1, s1, pageRequest);
        return entityRepository.findAllByNameContainingIgnoreCaseOrState_NameAndIsDeleteIsFalse(s1, s1, s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
    }

    @Override
    public City get(Integer id) {
        City city = super.get(id);
        if (getMvnoIdFromCurrentStaff() == 1 || (city.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || city.getMvnoId() == 1))
            return city;
        return null;
    }

    public City getCityForUpdateAndDelete(Integer id) {
        City city = get(id);
        if(city == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == city.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return city;
    }

    /**
     * Duplicate Verification
     * @Author Darshan
     * @param name
     * @param id
     * @param countryId
     * @param operation
     * @return
     */
    public boolean duplicateVerification(String name, Integer id, Integer countryId, Integer stateId, Integer operation) {
        boolean flag = false;
        if(name != null) {
            name = name.trim();
            Long count = null;
            if (getMvnoIdFromCurrentStaff() == 1) {
                count = entityRepository.countByNameAndIsDeleteIsFalseAndCountryIdAndState_Id(name, countryId, stateId);
            } else if (getMvnoIdFromCurrentStaff() != 1){
                count = entityRepository.countByNameAndIsDeleteIsFalseAndMvnoIdInAndCountryIdAndState_Id(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), countryId, stateId);
            }
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                if (count == 0) {
                    flag = true;
                }
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                if (count >= 1) {
                    Long countEdit = null;
                    if (getMvnoIdFromCurrentStaff() == 1) {
                        countEdit = entityRepository.countByNameAndIdAndIsDeleteIsFalseAndCountryIdAndState_Id(name, id, countryId, stateId);
                    } else {
                        countEdit = entityRepository.countByNameAndIdAndIsDeleteIsFalseAndMvnoIdInAndCountryIdAndState_Id(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), countryId, stateId);
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
}
