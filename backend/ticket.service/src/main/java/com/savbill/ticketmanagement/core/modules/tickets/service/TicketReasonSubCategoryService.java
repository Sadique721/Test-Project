package com.savbill.ticketmanagement.core.modules.tickets.service;


import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.dto.GenericSearchModel;
import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.ticketmanagement.core.modules.tickets.domain.*;
import com.savbill.ticketmanagement.core.modules.tickets.domain.*;
import com.savbill.ticketmanagement.core.modules.tickets.mapper.TicketReasonSubCategoryMapper;
import com.savbill.ticketmanagement.core.modules.tickets.model.TicketReasonSubCategoryDTO;
import com.savbill.ticketmanagement.core.modules.tickets.repository.*;
import com.savbill.ticketmanagement.core.modules.tickets.repository.*;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketReasonSubCategoryService extends ExBaseAbstractService<TicketReasonSubCategoryDTO, TicketReasonSubCategory, Long> {


    @Autowired
    TicketReasonSubCategoryRepo repository;
    @Autowired
    TicketReasonSubCategoryMapper mapper;

    @Autowired
    TicketReasonSubCategoryMapper ticketReasonSubCategoryMapper;

    @Autowired
    TicketReasonSubCategoryRepo reasonSubCategoryRepo;

    @Autowired
    TicketReasonCategoryTATMappingRepo mappingRepo;

    @Autowired
    TicketSubCategoryTatMappingRepo repo;
    @Autowired
    CaseRepository caseRepository;

    @Autowired
    ResoSubCategoryMappingRepo resoSubCategoryMappingRepo;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    public TicketReasonSubCategoryService(TicketReasonSubCategoryRepo repository, TicketReasonSubCategoryMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "ticket_reason_category_id");
    }

    @Override
    public String getModuleNameForLog() {
        return "[TicketReasonSubCategoryService]";
    }

    @Override
    public TicketReasonSubCategoryDTO getEntityForUpdateAndDelete(Long aLong) throws Exception {
        return super.getEntityForUpdateAndDelete(aLong);
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QTicketReasonSubCategory qTicketReasonSubCategory = QTicketReasonSubCategory.ticketReasonSubCategory;
        QTicketSubCategoryReasonCategoryMapping qTicketSubCategoryReasonCategoryMapping = QTicketSubCategoryReasonCategoryMapping.ticketSubCategoryReasonCategoryMapping;
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonSubCategory.isNotNull().and(qTicketReasonSubCategory.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        makeGenericResponse()
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                switch (genericSearchModel.getFilterColumn()) {
                    case "name":
                        booleanExpression = booleanExpression.and(qTicketReasonSubCategory.subCategoryName.containsIgnoreCase(genericSearchModel.getFilterValue()));
                        break;
                    case "parentCategoryName":
                        BooleanExpression parentCategoryExpression = qTicketReasonSubCategory.id.in(
                                JPAExpressions.select(qTicketSubCategoryReasonCategoryMapping.ticketReasonSubCategoryId)
                                        .from(qTicketSubCategoryReasonCategoryMapping)
                                        .join(qTicketReasonCategory).on(qTicketSubCategoryReasonCategoryMapping.ticketReasonCategoryId.eq(qTicketReasonCategory.id))
                                        .where(qTicketReasonCategory.categoryName.eq(genericSearchModel.getFilterValue()))
                        );
                        booleanExpression = booleanExpression.and(parentCategoryExpression);
                        break;

                }
            }
        }
        if(getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qTicketReasonSubCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
        {
            booleanExpression = booleanExpression
                    .and(qTicketReasonSubCategory.mvnoId.eq(1)
                            .or(qTicketReasonSubCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonSubCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.isNull());

        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        PageRequest pageRequest = super.generatePageRequest(page, size, "createdate", 0);
        QTicketReasonSubCategory qTicketReasonSubCategory = QTicketReasonSubCategory.ticketReasonSubCategory;
        BooleanExpression booleanExpression = qTicketReasonSubCategory.isNotNull().and(qTicketReasonSubCategory.isDeleted.eq(false));
        if(getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qTicketReasonSubCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
        {
            booleanExpression = booleanExpression
                    .and(qTicketReasonSubCategory.mvnoId.eq(1)
                            .or(qTicketReasonSubCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonSubCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }


        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.isNull());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
//        return super.getListByPageAndSizeAndSortByAndOrderBy(page,size,"createdate",0,filterList);
    }

    public  List<TicketReasonSubCategoryDTO> getSubCategoryReasons(Long parentCategoryId) {
        List<TicketReasonSubCategoryDTO> ticketReasonSubCategoryDTOS = new ArrayList<>();
        HashSet<TicketReasonSubCategoryDTO> finalResultTicketReasonSubCategoryDTOS = new HashSet<>();
        QTicketReasonSubCategory qTicketReasonSubCategory = QTicketReasonSubCategory.ticketReasonSubCategory;
        BooleanExpression booleanExpression = qTicketReasonSubCategory.isNotNull().and(qTicketReasonSubCategory.isDeleted.eq(false)).and(qTicketReasonSubCategory.status.eq("Active"));
        if(getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qTicketReasonSubCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
        {
            booleanExpression = booleanExpression
                    .and(qTicketReasonSubCategory.mvnoId.eq(1)
                            .or(qTicketReasonSubCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonSubCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.isNull());

        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonSubCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
        for(TicketReasonSubCategoryDTO ticketReasonSubCategoryDTO: ticketReasonSubCategoryDTOS){
            if(!ticketReasonSubCategoryDTO.getTicketSubCategoryReasonCategoryMappingList().isEmpty() &&
                    ticketReasonSubCategoryDTO.getTicketSubCategoryReasonCategoryMappingList()!=null){
                for(TicketSubCategoryReasonCategoryMapping ticketSubCategoryReasonCategoryMapping : ticketReasonSubCategoryDTO.getTicketSubCategoryReasonCategoryMappingList()){
                    if(ticketSubCategoryReasonCategoryMapping.getTicketReasonCategoryId().equals(parentCategoryId)  || ticketSubCategoryReasonCategoryMapping.getTicketReasonCategoryId()==parentCategoryId){
                        finalResultTicketReasonSubCategoryDTOS.add(ticketReasonSubCategoryDTO);
                    }
                }
            }
        }
        return finalResultTicketReasonSubCategoryDTOS.stream().collect(Collectors.toList());
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) {
        boolean flag = false;
        if (name != null) {
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
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
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
        }
        return flag;
    }


    public Iterable<TicketSubCategoryTatMapping> updateStatus(TicketReasonSubCategoryDTO entityDTO) {
        Iterable<TicketSubCategoryTatMapping>mappings= new ArrayList<>();
        QTicketSubCategoryTatMapping ticketSubCategoryTatMapping=QTicketSubCategoryTatMapping.ticketSubCategoryTatMapping;
        BooleanExpression booleanExpression=ticketSubCategoryTatMapping.ticketReasonSubCategoryId.isNull();
        mappings=  repo.findAll(booleanExpression);
//        mappings.forEach(ticketSubCategoryTatMapping1 ->booleanExpression.and(ticketSubCategoryTatMapping.isDeleted.eq(true)) );
        for(TicketSubCategoryTatMapping map : mappings){
            entityDTO.setStatus("True");
            map.setDeleteFlag(true);
            repo.save(map);
            repo.delete(map);
        }

        return mappings;
    }

    public Boolean getUniqueSubCategory(Long reasoneCatId) {
        Boolean flag=false;
        QCase qCase=QCase.case$;
        BooleanExpression booleanExpression=qCase.isNotNull();
        booleanExpression=booleanExpression.and(qCase.isDelete.eq(false));
        booleanExpression=booleanExpression.and(qCase.reasonSubCategoryId.eq(reasoneCatId));
        List<Case>caselist= IterableUtils.toList(caseRepository.findAll(booleanExpression));
        List<ResoSubCategoryMapping> resolutionReasonsDTOS= IterableUtils.toList(resoSubCategoryMappingRepo.findBysubcateId(reasoneCatId));
        if(caselist.size()>0 || resolutionReasonsDTOS.size() >0 ) {
            flag=true;
        }
        return flag;

    }

    public Boolean isReasonSubCategoryDefault(List<Integer> categoryIds , Long buId){
        Boolean flag = false;
        List<TicketReasonSubCategory> ticketReasonSubCategoryList = new ArrayList<>();
        if(buId != null) {
             ticketReasonSubCategoryList = repository.findAllDefualtReasonSubCategoryUsingCategoryId(categoryIds, buId);
        }
        else{
            ticketReasonSubCategoryList = repository.findAllDefualtReasonSubCategoryUsingCategoryId(categoryIds);
        }
        if(!ticketReasonSubCategoryList.isEmpty()){
            flag = true;
        }
        return flag;
    }


    public List<TicketReasonSubCategory> getAllActiveEntities() throws Exception {
        List<TicketReasonSubCategory> list=new ArrayList<>();
        QTicketReasonSubCategory ticketReasonSubCategory=QTicketReasonSubCategory.ticketReasonSubCategory;
//        BooleanExpression expression=ticketReasonSubCategory.isNotNull();
        BooleanExpression expression=ticketReasonSubCategory.isDeleted.eq(false);

        if (getMvnoIdFromCurrentStaff() != 1)
        {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                expression=expression.and(ticketReasonSubCategory.mvnoId.in(1,getMvnoIdFromCurrentStaff()));
            else
                expression=expression.and(ticketReasonSubCategory.buId.in(getBUIdsFromCurrentStaff())).and(ticketReasonSubCategory.mvnoId.in(getMvnoIdFromCurrentStaff()));
        }

        if(getLoggedInUser().getLco())
            expression=expression.and(ticketReasonSubCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            expression=expression.and(ticketReasonSubCategory.lcoId.isNull());

        list = IterableUtils.toList(repository.findAll(expression));
        return list;
    }

}
