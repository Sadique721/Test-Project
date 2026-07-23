package com.savbill.taskmanagement.core.modules.tasks.service;

import com.savbill.taskmanagement.core.constants.CaseConstants;
import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.GenericSearchModel;
import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.exceptions.DataNotFoundException;
import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.domain.*;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCategory;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseSubCategory;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseSubCategoryCategoryMapping;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseSubCategoryMapper;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseSubCategoryDTO;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseCategoryRepository;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseRepository;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseSubCategoryCategoryMappingRepository;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseSubCategoryRepository;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import com.savbill.taskmanagement.core.modules.utils.Constants;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CaseSubCategoryService extends ExBaseAbstractService<CaseSubCategoryDTO, CaseSubCategory,Long> {
    public CaseSubCategoryService(JpaRepository<CaseSubCategory, Long> repository, IBaseMapper<CaseSubCategoryDTO, CaseSubCategory> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CaseSubCategoryService]";
    }

    @Autowired
    CaseRepository caseRepository;

    @Autowired
    CaseSubCategoryRepository caseSubCategoryRepository;

    @Autowired
    CaseCategoryRepository caseCategoryRepository;

    @Autowired
    CaseSubCategoryCategoryMappingRepository caseSubCategoryCategoryMappingRepository;


    @Autowired
    CaseSubCategoryMapper caseSubCategoryMapper;


    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        log.debug("searching for Case Sub Categories Module : {}; ",SUBMODULE);
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QCaseSubCategory qCaseSubCategory = QCaseSubCategory.caseSubCategory;
        BooleanExpression booleanExpression = qCaseSubCategory.isNotNull().and(qCaseSubCategory.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        makeGenericResponse()
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                switch (genericSearchModel.getFilterColumn()) {
                    case "name":
                        booleanExpression = booleanExpression.and(qCaseSubCategory.subCategoryName.containsIgnoreCase(genericSearchModel.getFilterValue()));
                        break;
                }
            }
        }
        if(getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qCaseSubCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
        {
            booleanExpression = booleanExpression
                    .and(qCaseSubCategory.mvnoId.eq(1)
                            .or(qCaseSubCategory.mvnoId.eq(getMvnoIdFromCurrentStaff())).and(qCaseSubCategory.buId.in(getBUIdsFromCurrentStaff())));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qCaseSubCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qCaseSubCategory.lcoId.isNull());
        log.debug("searching completed for Case Sub Categories Module : {}; ",SUBMODULE);
        return makeGenericResponse(genericDataDTO, caseSubCategoryRepository.findAll(booleanExpression, pageRequest));
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        log.debug("fetching list of case sub categories by page and size and sort order; Module : {};",SUBMODULE);
        PageRequest pageRequest = super.generatePageRequest(page, size, "createdate", 0);
        QCaseCategory qCaseCategory = QCaseCategory.caseCategory;
        BooleanExpression booleanExpression = qCaseCategory.isNotNull().and(qCaseCategory.isDeleted.eq(false));
        if(getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qCaseCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
        {
            booleanExpression = booleanExpression
                    .and(qCaseCategory.mvnoId.eq(1)
                            .or(qCaseCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCaseCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }


        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qCaseCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qCaseCategory.lcoId.isNull());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        log.debug("successfully fetched list of case sub categories by page and size and sort order; Module : {};",SUBMODULE);
        return makeGenericResponse(genericDataDTO, caseSubCategoryRepository.findAll(booleanExpression, pageRequest));
//        return super.getListByPageAndSizeAndSortByAndOrderBy(page,size,"createdate",0,filterList);
    }



    public Boolean getUniqueSubCategory(Long caseSubCategoryId) {
        String SUBMODULE = getModuleNameForLog() + " [getUniqueSubCategory()] ";
        Boolean flag=false;
        QCase qCase = QCase.case$;
        if(caseSubCategoryId != null) {
            log.debug("get unique sub-category check for caseSubCategoryId: {}; Module: {};",caseSubCategoryId,SUBMODULE);
        }
        CaseSubCategory caseSubCategory = caseSubCategoryRepository.getOne(caseSubCategoryId);
        if(caseSubCategory!=null){
            List<CaseSubCategoryCategoryMapping> caseSubCategoryCategoryMappingList = caseSubCategoryCategoryMappingRepository.findAllByCaseCategoryId(caseSubCategoryId);
            if(!caseSubCategoryCategoryMappingList.isEmpty()){
                for (CaseSubCategoryCategoryMapping   caseSubCategoryCategoryMapping: caseSubCategoryCategoryMappingList) {
                    CaseCategory caseCategory = caseCategoryRepository.getOne(caseSubCategoryCategoryMapping.getCaseCategoryId());
                    if(caseCategory!=null){
                        BooleanExpression booleanExpression = qCase.isDelete.eq(false)
                                .and(qCase.caseStatus.notEqualsIgnoreCase((CaseConstants.TASK_STATUS_DONE))
                                        .or(qCase.caseStatus.notEqualsIgnoreCase((CaseConstants.TASK_STATUS_DISCARDED))
                                                .and(qCase.caseCategoryId.eq(caseCategory.getCategoryId()))));

                        Long caseCount = caseRepository.count(booleanExpression);
                        if(caseCount>0){
                            flag = true;
                            return flag;
                        }else{
                            return flag;
                        }
                    }
                }
            }else {
                flag =false;
                return flag;
            }

            log.debug("completed get unique sub-category check with flag: {} for caseSubCategory ID : {}; Module : {};", flag, caseSubCategoryId,SUBMODULE);
        }
        return flag;
    }



    @Override
    public boolean duplicateVerifyAtSave(String name) {
        String SUBMODULE = getModuleNameForLog() + " [duplicateVerifyAtSave()] ";
        boolean flag = false;
        if (name != null) {
            log.debug("checking duplicate at save with name : {}; at Module : {};",name,SUBMODULE);
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = caseSubCategoryRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) count = caseSubCategoryRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else count = caseSubCategoryRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
            log.debug("duplicate verification at save flag: {}; with name: {}; Module : {};", flag,name,SUBMODULE);
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [duplicateVerifyAtEdit()] ";
        boolean flag = false;
        if (name != null) {
            log.debug("checking duplicate at edit with name : {}; at : Module : {};",name,SUBMODULE);
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = caseSubCategoryRepository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    count = caseSubCategoryRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = caseSubCategoryRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());            }
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = caseSubCategoryRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                        countEdit = caseSubCategoryRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = caseSubCategoryRepository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
            log.debug("duplicate verification at edit flag: {}; with name : {}; Module : {};", flag,name,SUBMODULE);
        }
        return flag;
    }


    @Override
    public void deleteEntity(CaseSubCategoryDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [deleteEntity()] ";
        CaseSubCategory entityDomain = caseSubCategoryMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
        //   log.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + entityDomain.toString() + "]");
        try {
            if (entityDomain.getDeleteFlag()) {
                log.warn("Case Sub Category : {}, is either deleted or Unable to update/ delete this record; Module : {};",entityDomain.getSubCategoryId(),SUBMODULE);
                throw new DataNotFoundException();
            }
            if(entity == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entity.getMvnoId().intValue())){
                log.error("Case Sub Category is null or empty or Permission Denied. Unable to update/ delete this record; Module : {};",SUBMODULE);
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            }
            entityDomain.setDeleteFlag(true);
            log.debug("setting delete flag : {}; for Sub Categoty ID : {}; Module : {};",entityDomain.getDeleteFlag(),entityDomain.getSubCategoryId(),SUBMODULE);
            caseSubCategoryRepository.save(entityDomain);
        } catch (Exception ex) {
            log.error("error while deleting case Sub Categoty ID ; {}; Error Message : {}; Module : {};",entityDomain.getSubCategoryId(),ex.getMessage(),SUBMODULE);
            //          log.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while deleting Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }


    public List<CaseSubCategory> getAllActiveEntities() throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [getAllActiveEntities()] ";
        List<CaseSubCategory> list=new ArrayList<>();
        QCaseSubCategory qcaseSubCategory=QCaseSubCategory.caseSubCategory;
//        BooleanExpression expression=ticketReasonSubCategory.isNotNull();
        BooleanExpression expression=qcaseSubCategory.isDeleted.eq(false);

        if (getMvnoIdFromCurrentStaff() != 1)
        {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                expression=expression.and(qcaseSubCategory.mvnoId.in(1,getMvnoIdFromCurrentStaff()));
            else
                expression=expression.and(qcaseSubCategory.buId.in(getBUIdsFromCurrentStaff())).and(qcaseSubCategory.mvnoId.in(getMvnoIdFromCurrentStaff()));
        }

        if(getLoggedInUser().getLco())
            expression=expression.and(qcaseSubCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            expression=expression.and(qcaseSubCategory.lcoId.isNull());

        try {
            list = IterableUtils.toList(caseSubCategoryRepository.findAll(expression));
            log.debug("successfully retrieved active entities; Module: {}; Total entities: {}", SUBMODULE, list.size());
        } catch (Exception ex) {
            log.error("error retrieving active entities; Module: {}; Message: {}", SUBMODULE, ex.getMessage(), ex);
            throw ex;
        }
        return list;
    }

    public GenericDataDTO getAllSubcategoryFromCategoryWithoutPagination(Integer caseCategoryId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllSubcategoryFromCategoryWithoutPagination()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            if(caseCategoryId!=null){
                log.debug("get all subcategory from category at { Module :{}; with Case-CategoryID : {}", SUBMODULE,caseCategoryId);
                List<CaseSubCategoryCategoryMapping> caseSubCategoryCategoryMappingList =  caseSubCategoryCategoryMappingRepository.findAllByCaseCategoryId(caseCategoryId.longValue());
                List<Long> caseSubCategoryIds = caseSubCategoryCategoryMappingList.stream().map(caseSubCategoryCategoryMapping -> caseSubCategoryCategoryMapping.getCaseSubCategoryId()).collect(Collectors.toList());
               // List<CaseSubCategory> list = caseSubCategoryRepository.findAllById(caseSubCategoryIds);
                List<CaseSubCategory> list =
                        caseSubCategoryRepository.findBySubCategoryIdInAndIsDeletedFalse(caseSubCategoryIds);
                genericDataDTO.setDataList(list);
                genericDataDTO.setTotalRecords(list.size());
            }
            return genericDataDTO;
        } catch (Exception ex) {
            log.error("get all sub case category from category at { Module :{}; Message : {}; ", SUBMODULE, ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }

        return genericDataDTO;
    }

}
