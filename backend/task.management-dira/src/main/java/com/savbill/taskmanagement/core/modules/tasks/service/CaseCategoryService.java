package com.savbill.taskmanagement.core.modules.tasks.service;

import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.GenericSearchModel;
import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.exceptions.DataNotFoundException;
import com.savbill.taskmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.taskmanagement.core.modules.tasks.domain.*;
import com.savbill.taskmanagement.core.modules.tasks.domain.Case;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCategory;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCategoryTatMapping;
import com.savbill.taskmanagement.core.modules.tasks.domain.ResoSubCategoryMapping;
import com.savbill.taskmanagement.core.modules.tasks.mapper.CaseCategoryMapper;

import com.savbill.taskmanagement.core.modules.tasks.model.CaseCategoryDTO;
import com.savbill.taskmanagement.core.modules.tasks.repository.*;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseCategoryRepository;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseCategoryTatMappingRepo;
import com.savbill.taskmanagement.core.modules.tasks.repository.CaseRepository;
import com.savbill.taskmanagement.core.modules.tasks.repository.ResoSubCategoryMappingRepo;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import com.savbill.taskmanagement.core.modules.utils.Constants;
import com.savbill.taskmanagement.core.service.ExBaseAbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class CaseCategoryService  extends ExBaseAbstractService<CaseCategoryDTO, CaseCategory, Long> {


    @Autowired
    CaseCategoryRepository repository;

    @Autowired
    CaseCategoryMapper mapper;

//    @Autowired
//    TicketReasonSubCategoryMapper mapper;
//
//    @Autowired
//    TicketReasonSubCategoryMapper ticketReasonSubCategoryMapper;
//
//    @Autowired
//    TicketReasonSubCategoryRepo reasonSubCategoryRepo;

    @Autowired
    CaseCategoryTatMappingRepo repo;
    @Autowired
    CaseRepository caseRepository;

    @Autowired
    ResoSubCategoryMappingRepo resoSubCategoryMappingRepo;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    public CaseCategoryService(CaseCategoryRepository repository, CaseCategoryMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "ticket_reason_category_id");
    }

    @Override
    public String getModuleNameForLog() {
        return "[CaseCategoryService]";
    }

    @Override
    public CaseCategoryDTO getEntityForUpdateAndDelete(Long aLong) throws Exception {
        return super.getEntityForUpdateAndDelete(aLong);
    }


    @Override
    public void deleteEntity(CaseCategoryDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [deleteEntity()] ";
        CaseCategory entityDomain = mapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
        //   log.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + entityDomain.toString() + "]");
        log.debug("deleting case category : {}; Module : {};",entity.getCategoryId(),SUBMODULE);
        try {
            if (entityDomain.getDeleteFlag()) {
                throw new DataNotFoundException();
            }
            if(entity == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entity.getMvnoId().intValue()))
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            entityDomain.setDeleteFlag(true);
            log.debug("setting delete Flag : true For Case Castegory : {}; Module : {};", entityDomain.getCategoryId(),SUBMODULE);
            repository.save(entityDomain);
            log.info("case category deleted Case-Category ID: {}; Module : {};", entityDomain.getCategoryId(),SUBMODULE);
        } catch (Exception ex) {
            //          log.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while deleting Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            log.error("error while deleting case category at : {Module : {}; Message : {};}",SUBMODULE,ex.getMessage());
            throw ex;
        }
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        log.debug("searching case categories : {}; Module : {};", filterList, SUBMODULE);
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QCaseCategory qCaseCategory = QCaseCategory.caseCategory;
        BooleanExpression booleanExpression = qCaseCategory.isNotNull().and(qCaseCategory.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        makeGenericResponse()
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                switch (genericSearchModel.getFilterColumn()) {
                    case "name":
                        booleanExpression = booleanExpression.and(qCaseCategory.categoryName.containsIgnoreCase(genericSearchModel.getFilterValue()));
                        break;
                }
            }
        }
        if(getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qCaseCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
        {
            booleanExpression = booleanExpression
                    .and(qCaseCategory.mvnoId.eq(1)
                            .or(qCaseCategory.mvnoId.eq(getMvnoIdFromCurrentStaff())).and(qCaseCategory.buId.in(getBUIdsFromCurrentStaff())));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qCaseCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qCaseCategory.lcoId.isNull());
        log.debug("searching completed case categories : {}; Module : {};", filterList, SUBMODULE);
        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        log.debug("searching case categories by page,size and sort by : {}; Module : {};", sortOrder, SUBMODULE);
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
        log.debug("searching completed for case categories by page,size and sort by : {}; Module : {};", sortOrder, SUBMODULE);
        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
//        return super.getListByPageAndSizeAndSortByAndOrderBy(page,size,"createdate",0,filterList);
    }

//    public  List<CaseCategoryDTO> getSubCategoryReasons(Long parentCategoryId) {
//        List<CaseCategoryDTO> caseCategoryDTOS = new ArrayList<>();
//        HashSet<CaseCategoryDTO> finalResultTicketReasonSubCategoryDTOS = new HashSet<>();
//        QCaseCategory qCaseCategory = QCaseCategory.caseCategory;
//        BooleanExpression booleanExpression = qCaseCategory.isNotNull().and(qCaseCategory.isDeleted.eq(false)).and(qCaseCategory.status.eq("Active"));
//        if(getMvnoIdFromCurrentStaff() != 1)
//            booleanExpression = booleanExpression.and(qCaseCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
//        {
//            booleanExpression = booleanExpression
//                    .and(qCaseCategory.mvnoId.eq(1)
//                            .or(qCaseCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCaseCategory.buId.in(getBUIdsFromCurrentStaff().stream().map(aLong -> aLong.intValue()).collect(Collectors.toList())))));
//        }
//
//        if(getLoggedInUser().getLco())
//            booleanExpression=booleanExpression.and(qCaseCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
//        else
//            booleanExpression=booleanExpression.and(qCaseCategory.lcoId.isNull());
//
//        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> caseCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
//        for(CaseCategoryDTO caseCategoryDTO: caseCategoryDTOS){
//            if(!caseCategoryDTO.getCaseSubCategoryCategoryMappingList().isEmpty() &&
//                    ticketReasonSubCategoryDTO.getCaseSubCategoryCategoryMappingList()!=null){
//                for(CaseSubCategoryCategoryMapping ticketSubCategoryReasonCategoryMapping : ticketReasonSubCategoryDTO.getCaseSubCategoryCategoryMappingList()){
//                    if(ticketSubCategoryReasonCategoryMapping.getTicketReasonCategoryId().equals(parentCategoryId)  || ticketSubCategoryReasonCategoryMapping.getTicketReasonCategoryId()==parentCategoryId){
//                        finalResultTicketReasonSubCategoryDTOS.add(ticketReasonSubCategoryDTO);
//                    }
//                }
//            }
//        }
//        return finalResultTicketReasonSubCategoryDTOS.stream().collect(Collectors.toList());
//    }

    @Override
    public boolean duplicateVerifyAtSave(String name) {
        String SUBMODULE = getModuleNameForLog() + " [duplicateVerifyAtSave()] ";
        boolean flag = false;
        if (name != null) {
            log.debug("checking duplicate verify at save , with name : {}; Module : {};", name,SUBMODULE);
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) count = repository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else count = repository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }else{
            log.debug("checking duplicate verify at save , Name is getting null; Module : {}; ",SUBMODULE);
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [duplicateVerifyAtEdit()] ";
        boolean flag = false;
        if (name != null) {
            log.debug("checking duplicate verify at edit , with name : {}; Module : {}; ", name,SUBMODULE);
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(name);
            else {
                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    count = repository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = repository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());            }
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = repository.duplicateVerifyAtEdit(name, id);
                else {
                    if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                        countEdit = repository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                    else
                        countEdit = repository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }else{
            log.debug("checking duplicate verify at edit , Name is getting null; Module : {};",SUBMODULE);
        }
        return flag;
    }


    public Iterable<CaseCategoryTatMapping> updateStatus(CaseCategoryDTO entityDTO) {
        String SUBMODULE = getModuleNameForLog() + " [updateStatus()] ";
        Iterable<CaseCategoryTatMapping>mappings= new ArrayList<>();
        QCaseCategoryTatMapping caseCategoryTatMapping=QCaseCategoryTatMapping.caseCategoryTatMapping;
        BooleanExpression booleanExpression=caseCategoryTatMapping.caseCategoryId.isNull();
        mappings=  repo.findAll(booleanExpression);
//        mappings.forEach(ticketSubCategoryTatMapping1 ->booleanExpression.and(ticketSubCategoryTatMapping.isDeleted.eq(true)) );
        for(CaseCategoryTatMapping map : mappings){
            entityDTO.setStatus("True");
            map.setDeleteFlag(true);
            repo.save(map);
            repo.delete(map);
        }

        return mappings;
    }

    public Boolean getUniqueCategory(Long caseCategoryId) {
        String SUBMODULE = getModuleNameForLog() + " [getUniqueCategory()] ";
        Boolean flag=false;
        if(caseCategoryId == null) {
            log.debug("Case category id is getting null to get unique case category; Module : {}; ",SUBMODULE);
        }
        QCase qCase=QCase.case$;
        BooleanExpression booleanExpression=qCase.isNotNull();
        booleanExpression=booleanExpression.and(qCase.isDelete.eq(false));
        booleanExpression=booleanExpression.and(qCase.caseCategoryId.eq(caseCategoryId));
        List<Case>caselist= IterableUtils.toList(caseRepository.findAll(booleanExpression));
       List<ResoSubCategoryMapping> resolutionReasonsDTOS= IterableUtils.toList(resoSubCategoryMappingRepo.findByCaseCategoryId(caseCategoryId.intValue()));
        if(caselist.size()>0   || resolutionReasonsDTOS.size() >0 ) {
            log.debug("Case category is not unique: {}; Module: {}",caseCategoryId,SUBMODULE);
            flag=true;
        }
        return flag;

    }

//    public Boolean isReasonSubCategoryDefault(List<Integer> categoryIds , Long buId){
//        Boolean flag = false;
//        List<CaseCategory> ticketReasonSubCategoryList = new ArrayList<>();
//        if(buId != null) {
//            ticketReasonSubCategoryList = repository.findAllDefualtReasonSubCategoryUsingCategoryId(categoryIds, buId);
//        }
//        else{
//            ticketReasonSubCategoryList = repository.findAllDefualtReasonSubCategoryUsingCategoryId(categoryIds);
//        }
//        if(!ticketReasonSubCategoryList.isEmpty()){
//            flag = true;
//        }
//        return flag;
//    }


    public List<CaseCategoryDTO> getAllActiveReasonCategory() {
        String SUBMODULE = getModuleNameForLog() + " [getAllActiveReasonCategory()] ";
        log.debug("Entering method to get all active reason categories; Module: {}", SUBMODULE);

        List<CaseCategoryDTO> ticketReasonCategoryDTOS = new ArrayList<>();
        QCaseCategory qCaseCategory = QCaseCategory.caseCategory;
        BooleanExpression booleanExpression = qCaseCategory.isNotNull().and(qCaseCategory.isDeleted.eq(false)).and(qCaseCategory.status.eq("Active"));
        booleanExpression= booleanExpression.and(qCaseCategory.isDefaultCaseCategory.eq(false));
        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qCaseCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(qCaseCategory.mvnoId.eq(1).or(qCaseCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qCaseCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }
        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qCaseCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qCaseCategory.lcoId.isNull());

        try {
            repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
            log.debug("Successfully retrieved {} active reason case categories; Module: {}", ticketReasonCategoryDTOS.size(), SUBMODULE);
        }catch (Exception ex){
            log.error("Error retrieving active reason categories; Module: {}; Message: {}", SUBMODULE, ex.getMessage(), ex);
            throw ex;
        }
        return ticketReasonCategoryDTOS;
    }


    public List<CaseCategory> getAllActiveEntities() throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [getAllActiveEntities()] ";
        List<CaseCategory> list=new ArrayList<>();
        QCaseCategory  qCaseCategory=QCaseCategory.caseCategory;
//        BooleanExpression expression=ticketReasonSubCategory.isNotNull();
        BooleanExpression expression=qCaseCategory.isDeleted.eq(false);

        if (getMvnoIdFromCurrentStaff() != 1)
        {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                expression=expression.and(qCaseCategory.mvnoId.in(1,getMvnoIdFromCurrentStaff()));
            else
                expression=expression.and(qCaseCategory.buId.in(getBUIdsFromCurrentStaff())).and(qCaseCategory.mvnoId.in(getMvnoIdFromCurrentStaff()));
        }

        if(getLoggedInUser().getLco())
            expression=expression.and(qCaseCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            expression=expression.and(qCaseCategory.lcoId.isNull());

        try{
            list = IterableUtils.toList(repository.findAll(expression));
            log.debug("Successfully retrieved {} active entities; Module: {}", list.size(), SUBMODULE);
        }catch (Exception ex) {
            log.error("error retrieving active entities; Module: {}; Message: {}", SUBMODULE, ex.getMessage(), ex);
            throw ex;
        }
        return list;
    }
}
