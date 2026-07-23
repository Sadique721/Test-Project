package com.savbill.commonGateway.moules.MasterManagement.State.service;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.service.AbstractService;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.constants.*;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.Country.domain.Country;
import com.savbill.commonGateway.moules.MasterManagement.Country.service.CountryService;


import com.savbill.commonGateway.moules.MasterManagement.State.domain.QState;
import com.savbill.commonGateway.moules.MasterManagement.State.domain.State;
import com.savbill.commonGateway.moules.MasterManagement.State.model.StatePojo;
import com.savbill.commonGateway.moules.MasterManagement.State.repository.StateRepository;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.spring.MessagesPropertyConfig;
import com.savbill.commonGateway.spring.SpringContext;
import com.savbill.commonGateway.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StateService extends AbstractService<State, StatePojo, Integer> {

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private StateRepository entityRepository;

    @Autowired
    MessageSender messageSender;

    @Autowired
    CreateDataSharedService createDataSharedService;

    public static final String MODULE = "[StateService]";
    private static final Logger LOGGER = LoggerFactory.getLogger(StateService.class);
    public StateService() {
        sortColMap.put("countryName", "country.name");
        sortColMap.put("id", "stateid");
    }

    @Override
    public JpaRepository<State, Integer> getRepository() {
        return entityRepository;
    }
    
    public Page<State> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if(getMvnoIdFromCurrentStaff() == 1)
            return entityRepository.findAll(pageRequest);
        if (null == filterList || 0 == filterList.size())
            return entityRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
    }

    public Page<State> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.searchEntity(searchText, pageRequest,getMvnoIdFromCurrentStaff());
    }

    public List<StatePojo> getAllActiveEntities() {
//        return entityRepository.findAllByStatusAndMvnoId(CommonConstants.ACTIVE_STATUS,getMvnoIdFromCurrentStaff().intValue());
        return entityRepository.findAllActiveStatesByStatusAndMvnoId(CommonConstants.ACTIVE_STATUS,getMvnoIdFromCurrentStaff().intValue());
//        		.stream().filter(state -> state.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || state.getMvnoId() == null || state.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
    }

    public List<State> getAllEntities() {
        return entityRepository.findAll()
        		.stream().filter(state -> state.getMvnoId() == getMvnoIdFromCurrentStaff() || state.getMvnoId() == null).collect(Collectors.toList());
    }

    public List<State> findByCountry(Country country) {
        return entityRepository.findAllByCountryAndIsDeletedIsFalseOrderByIdDesc(country)
        		.stream().filter(state -> state.getMvnoId() == getMvnoIdFromCurrentStaff() || state.getMvnoId() == null).collect(Collectors.toList());
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

    public void deleteState(Integer id) throws Exception {
        String SUBMODULE = MODULE + " [deleteState()] ";
        try {
            State state = entityRepository.getOne(id);
            boolean flag=this.deleteVerification(state.getId());
            if(flag){
                state.setIsDeleted(true);
                entityRepository.save(state);
                //StateMessage stateMessage = new StateMessage(state);
                //this.messageSender.send(stateMessage, RabbitMqConstants.QUEUE_STATE);
                createDataSharedService.deleteEntityDataForAllMicroService(state);
            }else{
                throw new RuntimeException(DeleteContant.STATE_DELETE_EXIST);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }

    }

    public State getStateForAdd() {
        return new State();
    }

    public State getStateForEdit(Integer id) throws Exception {
        return entityRepository.getOne(id);
    }

    public State saveState(State state) throws Exception {
    	if(getMvnoIdFromCurrentStaff() != null) {
    		state.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        State save = entityRepository.save(state);
        return save;
    }

    public List<State> getStateListByCountry(Country country) throws Exception {
        return entityRepository.findAllByCountryAndIsDeletedIsFalseOrderByIdDesc(country)
        		.stream().filter(state -> state.getMvnoId() == getMvnoIdFromCurrentStaff() || state.getMvnoId() == null).collect(Collectors.toList());
    }

    public StatePojo save(StatePojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [save()] ";
        try {
            State obj = convertStatePojoToStateModel(pojo);
            obj = saveState(obj);
            pojo = convertStateModelToStatePojo(obj);
            createDataSharedService.sendEntitySaveDataForAllMicroService(obj);
            //StateMessage stateMessage = new StateMessage(obj);
            //this.messageSender.send(stateMessage, RabbitMqConstants.QUEUE_STATE);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name){
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
    public boolean duplicateVerifyCountryAtSave(String name, Integer countryId){
        boolean flag = false;
        if (name != null && countryId != null) {
            name = name.trim();
            Integer count = entityRepository.duplicateVerifyCountryAtSave(name , countryId);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }
    public StatePojo update(StatePojo pojo, HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + " [update()] ";
        Integer respCode = APIConstants.FAIL;
       State old1 = get(pojo.getId());
        try {
            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
            State newobj=getStateForEdit(pojo.getId());
            State obj = convertStatePojoToStateModel(pojo);
            State updatedValue = new State(pojo,pojo.getId());
            getStateForUpdateAndDelete(obj.getId());

            /*State oldObj1=getStateForEdit(pojo.getId());
            State obj = convertStatePojoToStateModel(pojo);
            StatePojo pojold=  convertStateModelToStatePojo(oldObj1);
            State oldstate=getStateForUpdateAndDelete(pojo.getId());
            State newvalues=new State(pojold,pojo.getId());
            getStateForUpdateAndDelete(obj.getId());
            State newobj=getStateForEdit(pojo.getId());*/
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update state "+LogConstants.LOG_BY_NAME+pojo.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + " , Updated state Details " + UpdateDiffFinder.getUpdatedDiff(old1 , obj)+ LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS  );
           // String updatedValues = CommonUtils.getUpdatedDiff(pojold,pojo);
            obj = saveState(obj);
            pojo = convertStateModelToStatePojo(obj);
            //StateMessage stateMessage = new StateMessage(obj);
            //this.messageSender.send(stateMessage, RabbitMqConstants.QUEUE_STATE);
            createDataSharedService.updateEntityDataForAllMicroService(obj);

        } catch (Exception ex) {
            respCode=APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "update state " + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS +LogConstants.LOG_FAILED+ LogConstants.LOG_STATUS_CODE +APIConstants.ERROR_MESSAGE + ex.getMessage()+ HttpStatus.NOT_ACCEPTABLE.value());
            throw ex;
        }
        return pojo;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count = entityRepository.duplicateVerifyAtSave(name);
            if (count >= 1) {
                Integer countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
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
    public boolean duplicateVerifyCountryAtEdit(String name,Integer countryId , Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count = entityRepository.duplicateVerifyCountryAtSave(name , countryId);
            if (count >= 1) {
                Integer countEdit = entityRepository.duplicateVerifyCountryAtEdit(name,countryId,id);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }
    public State convertStatePojoToStateModel(StatePojo pojo) throws Exception {
        String SUBMODULE = MODULE + " [convertStatePojoToStateModel()] ";
        State state = null;
        try {
            if (pojo != null) {
                state = new State();
                if (pojo.getId() != null) {
                    state.setId(pojo.getId());
                }
                state.setName(pojo.getName());
                state.setStatus(pojo.getStatus());
                if(pojo.getMvnoId() != null) {
                	state.setMvnoId(pojo.getMvnoId());
                }
                CountryService countryService = SpringContext.getBean(CountryService.class);
                if (countryService.get(pojo.getCountryPojo().getId()) != null) {
                    state.setCountry(countryService.convertCountryPojoToCountryModel(pojo.getCountryPojo()));
                } else {
                    throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.country.not.available"), null);
                }
                return state;
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }

        return null;
    }

    public StatePojo convertStateModelToStatePojo(State state) throws Exception {
        String SUBMODULE = MODULE + " [convertStateModelToStatePojo()] ";
        StatePojo pojo = null;
        try {
            if (state != null) {
                pojo = new StatePojo();
                pojo.setId(state.getId());
                pojo.setName(state.getName());
                pojo.setStatus(state.getStatus());
                pojo.setCreatedate(state.getCreatedate());
                pojo.setUpdatedate(state.getUpdatedate());
                pojo.setCreatedById(state.getCreatedById());
                pojo.setCreatedByName(state.getCreatedByName());
                pojo.setLastModifiedById(state.getLastModifiedById());
                pojo.setLastModifiedByName(state.getLastModifiedByName());
                pojo.setDisplayId(state.getId());
                pojo.setDisplayName(state.getName());
                CountryService countryService = SpringContext.getBean(CountryService.class);
                pojo.setCountryName(countryService.get(state.getCountry().getId()) != null ? countryService.get(state.getCountry().getId()).getName() : null);
                pojo.setCountryPojo(countryService.convertCountryModelToCountryPojo(state.getCountry()));
                if(state.getMvnoId() != null) {
                	pojo.setMvnoId(state.getMvnoId());
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return pojo;
    }

    public List<StatePojo> convertResponseModelIntoPojo(List<State> stateList) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
        List<StatePojo> pojoListRes = new ArrayList<StatePojo>();
        try {
            if (stateList != null && stateList.size() > 0) {
                for (State state : stateList) {
                    pojoListRes.add(convertStateModelToStatePojo(state));
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return pojoListRes;
    }


    public void validateRequest(StatePojo pojo, Integer operation) {

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
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE)
                || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_UPDATE)
                || operation.equals(CommonConstants.OPERATION_DELETE)) {
            if (entityRepository.findById(pojo.getId()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.state.not.found"), null);
            }
        }
    }
    public List<State> getName(String n){
        QState qState = QState.state;
        BooleanExpression booleanExpression = qState.isNotNull()
                .and(qState.name.containsIgnoreCase(n));
        return (List<State>) entityRepository.findAll(booleanExpression);
    }
    public List<State> getByName(String stateName) {
        return entityRepository.findByNameContainingIgnoreCase(stateName);
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("State");
        List<StatePojo> statePojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, StatePojo.class, statePojos, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{
                StatePojo.class.getDeclaredField("id"),
                StatePojo.class.getDeclaredField("name"),
                StatePojo.class.getDeclaredField("status"),
                StatePojo.class.getDeclaredField("countryName"),
        };
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<StatePojo> statePojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, StatePojo.class, statePojos, getFields());
    }

    public Page<State> getStateByNameOrCountryName(String s1, PageRequest pageRequest) {
        if(getMvnoIdFromCurrentStaff() == 1)
            return entityRepository.findAllByNameContainingIgnoreCaseOrCountry_NameContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, s1, pageRequest);
        return entityRepository.findAllByNameContainingIgnoreCaseOrCountry_NameContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
    }

    @Override
    public Page<State> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getStateByNameOrCountryName(searchModel.getFilterValue(), pageRequest);
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

    @Override
    public State get(Integer id) {
        State state = super.get(id);
        if (getMvnoIdFromCurrentStaff() == 1 || (state.getMvnoId().intValue() == getMvnoIdFromCurrentStaff().intValue() || state.getMvnoId() == 1))
            return state;
        return null;
    }

    public State getStateForUpdateAndDelete(Integer id) {
        State state = get(id);
        if(state == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == state.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return state;
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
    public boolean duplicateVerification(String name, Integer id, Integer countryId, Integer operation) {
        boolean flag = false;
        if(name != null) {
            name = name.trim();
            Long count = null;
            if (getMvnoIdFromCurrentStaff() == 1) {
                count = entityRepository.countByNameAndIsDeletedIsFalseAndCountry_Id(name, countryId);
            } else if (getMvnoIdFromCurrentStaff() != 1){
                count = entityRepository.countByNameAndIsDeletedIsFalseAndMvnoIdInAndCountry_Id(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), countryId);
            }
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                if (count == 0) {
                    flag = true;
                }
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                if (count >= 1) {
                    Long countEdit = null;
                    if (getMvnoIdFromCurrentStaff() == 1) {
                        countEdit = entityRepository.countByNameAndIdAndIsDeletedIsFalseAndCountry_Id(name, id, countryId);
                    } else {
                        countEdit = entityRepository.countByNameAndIdAndIsDeletedIsFalseAndMvnoIdInAndCountry_Id(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), countryId);
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
