package com.savbill.cpm.service.postpaid;

import com.savbill.cpm.constants.SearchConstants;
import com.savbill.cpm.controller.api.APIController;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.model.postpaid.DunningRule;
import com.savbill.cpm.model.postpaid.DunningRuleAction;
import com.savbill.cpm.modules.Branch.repository.BranchRepository;
import com.savbill.cpm.modules.DunningRuleBranchMapping.domain.DunningRuleBranchMapping;
import com.savbill.cpm.modules.DunningRuleBranchMapping.repository.DunningRuleBranchMappingRepository;
import com.savbill.cpm.modules.Mvno.repository.MvnoRepository;
import com.savbill.cpm.pojo.api.DunningRuleActionPojo;
import com.savbill.cpm.pojo.api.DunningRulePojo;
import com.savbill.cpm.repository.common.BranchServiceAreaMappingRepository;
import com.savbill.cpm.repository.postpaid.DunningRuleRepository;
import com.savbill.cpm.repository.postpaid.PartnerRepository;
import com.savbill.cpm.repository.postpaid.PartnerServiceAreaMappingRepo;
import com.savbill.cpm.service.radius.AbstractService;
import com.savbill.cpm.spring.MessagesPropertyConfig;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonConstants;
import com.itextpdf.text.Document;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DunningRuleService extends AbstractService<DunningRule, DunningRulePojo, Integer> {

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private DunningRuleRepository entityRepository;

    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;

    @Autowired
    private DunningRuleBranchMappingRepository dunningRuleBranchMappingRepository;

    @Autowired
    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private BranchRepository branchRepository;


    @Autowired
    private MvnoRepository  mvnoRepository;


    public static final String MODULE = "[DunningRuleSerprivate com.savbill.cpm.repository.postpaid.DunningRuleActionRepository dunningRuleActionRepository;vice]";

    @Override
    protected JpaRepository<DunningRule, Integer> getRepository() {
        return entityRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(APIController.class);


    @PreAuthorize("hasPermission('com.savbill.cpm.model.postpaid.DunningRule', '1')")
    public Page<DunningRule> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        if(getMvnoIdFromCurrentStaff() == 1)
            return entityRepository.searchEntity(searchText, pageRequest);
        return entityRepository.searchEntity(searchText, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.postpaid.DunningRule', '1')")
    public List<DunningRule> getAllActiveEntities() {
        return entityRepository.findByStatus("Y").stream().filter(dunningRule -> dunningRule.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || dunningRule.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.postpaid.DunningRule', '1')")
    public List<DunningRule> getAllEntities() {
        return entityRepository.findAll().stream().filter(dunningRule -> dunningRule.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || dunningRule.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.postpaid.DunningRule', '2')")
    public DunningRule getDunningRuleForAdd() {
        return new DunningRule();
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.postpaid.DunningRule', '2')")
    public DunningRule getDunningRuleForEdit(Integer id) {
        return getEntityForUpdateAndDelete(id);
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.postpaid.DunningRule', '2')")
    public DunningRule saveDunningRule(DunningRule dunningRule) {
        dunningRule.setMvnoId(getMvnoIdFromCurrentStaff());
        for (DunningRuleAction item : dunningRule.getActionList()) {
            item.setDrule(dunningRule);
        }
        if(getMvnoIdFromCurrentStaff() != null) {
        	dunningRule.setMvnoId(getMvnoIdFromCurrentStaff());
    	}
        if(getLoggedInUser().getLco())
            dunningRule.setLcoId(getLoggedInUser().getPartnerId());
        else
            dunningRule.setLcoId(null);

        DunningRule save = entityRepository.save(dunningRule);
        return save;
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.postpaid.DunningRule', '4')")
    public void deleteDunningRule(Integer id) {
        getEntityForUpdateAndDelete(id);
        entityRepository.deleteById(id);
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.postpaid.DunningRule', '2')")
    public DunningRulePojo save(DunningRulePojo pojo) throws Exception {
        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
        if(getLoggedInUser().getLco())
            pojo.setLcoId(getLoggedInUser().getPartnerId());
        else
            pojo.setLcoId(null);
        DunningRule obj = convertDunningRulePojoToDunningRuleModel(pojo);
        obj = saveDunningRule(obj);
        if(!Objects.isNull(obj.getId())) {
            List<DunningRuleBranchMapping> dunningRuleBranchMappingList1 = dunningRuleBranchMappingRepository.findAllByByDunningId(obj.getId());
            dunningRuleBranchMappingRepository.deleteAll(dunningRuleBranchMappingList1);
        }
        if(pojo.getBranchIds() != null && !pojo.getBranchIds().isEmpty()){
            List<DunningRuleBranchMapping> dunningRuleBranchMappingList =  new ArrayList<>();
            for(int i =0 ;i<pojo.getBranchIds().size();i++) {
                List<Integer> serviceAreaIds = branchServiceAreaMappingRepository.getAllServiceAreaIdsWithBranchId(pojo.getBranchIds().get(i).intValue());
              for(Integer serviceAreaId:serviceAreaIds) {
                  DunningRuleBranchMapping dunningRuleBranchMapping1 = new DunningRuleBranchMapping();
                  dunningRuleBranchMapping1.setBranchId(pojo.getBranchIds().get(i));
                  dunningRuleBranchMapping1.setDunningRuleId(obj.getId());
                  dunningRuleBranchMapping1.setServiceAreaId(serviceAreaId.longValue());
                  dunningRuleBranchMappingList.add(dunningRuleBranchMapping1);
              }
            }

            dunningRuleBranchMappingRepository.saveAll(dunningRuleBranchMappingList);

        }
        if(pojo.getPartnerIds() != null && !pojo.getPartnerIds().isEmpty()){
            List<DunningRuleBranchMapping> dunningRuleBranchMappingList =  new ArrayList<>();
            for(int i =0 ;i<pojo.getPartnerIds().size();i++) {
                DunningRuleBranchMapping dunningRuleBranchMapping1 = new DunningRuleBranchMapping();
                dunningRuleBranchMapping1.setPartnerId(pojo.getPartnerIds().get(i).longValue());
                dunningRuleBranchMapping1.setDunningRuleId(obj.getId());
                List<Long> serviceAreaIds = partnerServiceAreaMappingRepo.serviceAreaIdWherePartnerIsNotBind(Math.toIntExact(pojo.getPartnerIds().get(i)));
                dunningRuleBranchMapping1.setServiceAreaId(serviceAreaIds.get(0));
                dunningRuleBranchMappingList.add(dunningRuleBranchMapping1);
            }
            dunningRuleBranchMappingRepository.saveAll(dunningRuleBranchMappingList);

        }
        pojo = convertDunningRuleModelToDunningRulePojo(obj);
        return pojo;
    }

    public DunningRule convertDunningRulePojoToDunningRuleModel(DunningRulePojo dunningRulePojo) throws Exception {
        DunningRule dunningRule = null;
        if (dunningRulePojo != null) {
            dunningRule = new DunningRule();
            if (dunningRulePojo.getId() != null) {
                dunningRule.setId(dunningRulePojo.getId());
            }
            dunningRule.setStatus(dunningRulePojo.getStatus());
            dunningRule.setIsGeneratepaymentLink(dunningRulePojo.getIsGeneratepaymentLink());
            dunningRule.setName(dunningRulePojo.getName());
            if(dunningRulePojo.getCcemail() != null) {
                dunningRule.setCcemail(dunningRulePojo.getCcemail());
            }
            if(dunningRulePojo.getMobile() != null){
                dunningRule.setMobile(dunningRulePojo.getMobile());
            }
            dunningRule.setCreditclass(dunningRulePojo.getCreditclass());
            dunningRule.setCustomerType(dunningRulePojo.getCustomerType());
            dunningRule.setDunningType(dunningRulePojo.getDunningType());
            dunningRule.setDunningSector(dunningRulePojo.getDunningSector());
            dunningRule.setDunningSubType(dunningRulePojo.getDunningSubType());
            dunningRule.setDunningSubSector(dunningRulePojo.getDunningSubSector());
            dunningRule.setCustomerPayType(dunningRulePojo.getCustomerPayType());
            dunningRule.setDunningFor(dunningRulePojo.getDunningFor());
            if(dunningRulePojo.getMvnoId() != null) {
            	dunningRule.setMvnoId(dunningRulePojo.getMvnoId());
            }
            if (dunningRulePojo.getDunningRuleActionPojoList() != null && dunningRulePojo.getDunningRuleActionPojoList().size() > 0) {
                List<DunningRuleAction> dunningRuleActionsList = new ArrayList<DunningRuleAction>();
                for (DunningRuleActionPojo actionPojo : dunningRulePojo.getDunningRuleActionPojoList()) {
                    DunningRuleAction dunningRuleAction = new DunningRuleAction();
                    if (actionPojo.getId() != null) {
                        dunningRuleAction.setId(actionPojo.getId());
                    }
                    dunningRuleAction.setAction(actionPojo.getAction());
                    dunningRuleAction.setDays(actionPojo.getDays());
                    if (actionPojo.getDunningRuleId() != null) {
                        dunningRuleAction.setDrule(entityRepository.getOne(actionPojo.getDunningRuleId()));
                    }
                    dunningRuleActionsList.add(dunningRuleAction);
                }
                dunningRule.setActionList(dunningRuleActionsList);
            }
        }
        return dunningRule;
    }

    public DunningRulePojo convertDunningRuleModelToDunningRulePojo(DunningRule dunningRule) throws Exception {
        DunningRulePojo dunningRulePojo = null;
        if (dunningRule != null) {
            dunningRulePojo = new DunningRulePojo();
            if (dunningRule.getId() != null) {
                dunningRulePojo.setId(dunningRule.getId());
            }
            dunningRulePojo.setStatus(dunningRule.getStatus());
            dunningRulePojo.setName(dunningRule.getName());
            if(dunningRule.getCcemail() != null){
                dunningRulePojo.setCcemail(dunningRule.getCcemail());
            }
            if(dunningRule.getMobile() != null){
                dunningRulePojo.setMobile(dunningRule.getMobile());
            }
            dunningRulePojo.setCreditclass(dunningRule.getCreditclass());
            dunningRulePojo.setCustomerType(dunningRule.getCustomerType());
            dunningRulePojo.setDunningType(dunningRule.getDunningType());
            dunningRulePojo.setDunningSector(dunningRule.getDunningSector());
            dunningRulePojo.setCreatedate(dunningRule.getCreatedate());
            dunningRulePojo.setUpdatedate(dunningRule.getUpdatedate());
            dunningRulePojo.setCreatedById(dunningRule.getCreatedById());
            dunningRulePojo.setCreatedByName(dunningRule.getCreatedByName());
            dunningRulePojo.setLastModifiedById(dunningRule.getLastModifiedById());
            dunningRulePojo.setLastModifiedByName(dunningRule.getLastModifiedByName());
            dunningRulePojo.setDunningSector(dunningRule.getDunningSector());
            dunningRulePojo.setDunningSubType(dunningRule.getDunningSubType());
            dunningRulePojo.setDunningSubSector(dunningRule.getDunningSubSector());
            dunningRulePojo.setCustomerPayType(dunningRule.getCustomerPayType());
            dunningRulePojo.setIsGeneratepaymentLink(dunningRule.getIsGeneratepaymentLink());
            dunningRulePojo.setDunningFor(dunningRule.getDunningFor());
            dunningRulePojo.setPartnerIds(dunningRuleBranchMappingRepository.findAllPartnerIdByDunningId(dunningRule.getId()).stream().filter(aLong -> aLong != null).distinct().collect(Collectors.toList()));
            dunningRulePojo.setBranchIds(dunningRuleBranchMappingRepository.findAllBranchIdByDunningId(dunningRule.getId()).stream().filter(aLong -> aLong != null).distinct().collect(Collectors.toList()));
            dunningRulePojo.setServiceAreaIds(dunningRuleBranchMappingRepository.findAllServiceAreaByDunningId(dunningRule.getId()).stream().distinct().collect(Collectors.toList()));
            if(!dunningRulePojo.getPartnerIds().isEmpty()) {
                dunningRulePojo.setPartnerNames(partnerRepository.getAllPartnerNamesByPartnerIds(dunningRulePojo.getPartnerIds().stream().map(aLong -> aLong.intValue()).collect(Collectors.toList())));
            }
            if(!dunningRulePojo.getBranchIds().isEmpty()) {
                dunningRulePojo.setBranchNames(branchRepository.getAllBranchNamesByBranchIds(dunningRulePojo.getBranchIds()));
            }
            if(dunningRule.getMvnoId() != null) {
            	dunningRulePojo.setMvnoId(dunningRule.getMvnoId());
            }
            if (dunningRule.getActionList() != null && dunningRule.getActionList().size() > 0) {
                List<DunningRuleActionPojo> dunningRuleActionsPojoList = new ArrayList<DunningRuleActionPojo>();

                for (DunningRuleAction dunningRuleAction : dunningRule.getActionList()) {
                    DunningRuleActionPojo actionPojo = new DunningRuleActionPojo();
                    if (dunningRuleAction.getId() != null) {
                        actionPojo.setId(dunningRuleAction.getId());
                    }
                    actionPojo.setAction(dunningRuleAction.getAction());
                    actionPojo.setDays(dunningRuleAction.getDays());
                    if (dunningRuleAction.getDrule() != null) {
                        actionPojo.setDunningRuleId(dunningRuleAction.getDrule().getId());
                    }
                    dunningRuleActionsPojoList.add(actionPojo);
                }
                dunningRulePojo.setDunningRuleActionPojoList(dunningRuleActionsPojoList);
            }
        }
        return dunningRulePojo;
    }

    @PreAuthorize("hasPermission('com.savbill.cpm.model.postpaid.DunningRule', '1')")
    public List<DunningRulePojo> convertResponseModelIntoPojo(List<DunningRule> dunningRuleList) throws Exception {
        List<DunningRulePojo> pojoListRes = new ArrayList<DunningRulePojo>();
        if (dunningRuleList != null && dunningRuleList.size() > 0) {
            for (DunningRule dunningRule : dunningRuleList) {
                pojoListRes.add(convertDunningRuleModelToDunningRulePojo(dunningRule));
            }
        }
        return pojoListRes;
    }

    public Page<DunningRule> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        Page<DunningRule> dunningRules = null;
        pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
        Integer lcoId=null;
        if(getLoggedInUser().getLco()) {
            lcoId = getLoggedInUser().getPartnerId();
            if (null == filterList || 0 == filterList.size()) {
                if(getMvnoIdFromCurrentStaff() == 1){
                    dunningRules= entityRepository.findAll(pageRequest,lcoId);
                    dunningRules = setMvnoName(dunningRules);
                }
                dunningRules= entityRepository.findAll(pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(), 1),lcoId);
                dunningRules = setMvnoName(dunningRules);
                return dunningRules;
            }
            else {
                return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
            }
        }
        else
        {
            if (null == filterList || 0 == filterList.size()) {
                if(getMvnoIdFromCurrentStaff() == 1){
                    dunningRules = entityRepository.findAll(pageRequest);
                    dunningRules = setMvnoName(dunningRules);
                    return dunningRules;
                }
                dunningRules = entityRepository.findAll(pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                dunningRules = setMvnoName(dunningRules);
                return dunningRules;
            }
            else {
                return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
            }
        }

        //return null;
    }

    @Override
    public Page<DunningRule> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [search()] ";
        Page<DunningRule> dunningRules = null;
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        dunningRules = getDunningRuleByNameOrCreditClass(searchModel.getFilterValue(), pageRequest);
                        for(DunningRule  dunningRule : dunningRules){
                            dunningRule.setMvnoName(mvnoRepository.findMvnoNameById(dunningRule.getMvnoId().longValue()));
                        }
                        return dunningRules;
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

    public Page<DunningRule> getDunningRuleByNameOrCreditClass(String s1, PageRequest pageRequest) {
        if(getLoggedInUser().getLco())
        {
            if(getMvnoIdFromCurrentStaff() == 1)
                return entityRepository.findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, pageRequest,getLoggedInUser().getPartnerId());
            return entityRepository.findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1),getLoggedInUser().getPartnerId());
        }
        else
        {
            if(getMvnoIdFromCurrentStaff() == 1)
                return entityRepository.findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, pageRequest);
            return entityRepository.findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
        }
    }

    public void validateRequest(DunningRulePojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getDunningRuleActionPojoList() == null || pojo.getDunningRuleActionPojoList().size() == 0) {
                throw new CustomValidationException(APIConstants.FAIL, "Dunning rule action list is required", null);
            }
        }
        if (!(pojo.getStatus().equalsIgnoreCase("Y") || pojo.getStatus().equalsIgnoreCase("N"))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Dunning Rule");
        List<DunningRulePojo> dunningRulePojoList =  new ArrayList<>();
        List<DunningRule> dunningRuleList = entityRepository.findAll();
        for(DunningRule dunningRule : dunningRuleList)
            dunningRulePojoList.add(convertDunningRuleModelToDunningRulePojo(dunningRule));
        createExcel(workbook, sheet, DunningRulePojo.class, dunningRulePojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        List<DunningRulePojo> dunningRulePojoList =  new ArrayList<>();
        List<DunningRule> dunningRuleList = entityRepository.findAll();
        for(DunningRule dunningRule : dunningRuleList)
            dunningRulePojoList.add(convertDunningRuleModelToDunningRulePojo(dunningRule));
        createPDF(doc, DunningRulePojo.class, dunningRulePojoList, null);
    }

    @Override
    public DunningRule get(Integer id) {
        DunningRule dunningRule = super.get(id);
        if (dunningRule != null && (getMvnoIdFromCurrentStaff() == 1 || (dunningRule.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || dunningRule.getMvnoId() == 1)))
            return dunningRule;
        return null;
    }

    public DunningRule getEntityForUpdateAndDelete(Integer id) {
        DunningRule dunningRule = get(id);
        if(dunningRule == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == dunningRule.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return dunningRule;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
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
                else
                    countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public Page<DunningRule> setMvnoName(Page<DunningRule> dunningRules){
        for (DunningRule dunningRule : dunningRules){
            dunningRule.setMvnoName(mvnoRepository.findMvnoNameById(dunningRule.getMvnoId().longValue()));
        }
        return dunningRules;
    }

}
