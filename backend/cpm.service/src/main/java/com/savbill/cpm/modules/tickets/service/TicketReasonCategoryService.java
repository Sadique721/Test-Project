package com.savbill.cpm.modules.tickets.service;


import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.service.ExBaseAbstractService2;
import com.savbill.cpm.model.postpaid.QPostpaidPlan;
import com.savbill.cpm.modules.servicePlan.domain.Services;
import com.savbill.cpm.modules.servicePlan.repository.ServiceRepository;
import com.savbill.cpm.modules.subscriber.model.CustomerPlansModel;
import com.savbill.cpm.modules.subscriber.service.SubscriberService;
import com.savbill.cpm.modules.tickets.domain.Case;
import com.savbill.cpm.modules.tickets.domain.QCase;
import com.savbill.cpm.modules.tickets.domain.QTicketReasonCategory;
import com.savbill.cpm.modules.tickets.domain.TicketReasonCategory;
import com.savbill.cpm.modules.tickets.mapper.TicketReasonCategoryMapper;
import com.savbill.cpm.modules.tickets.model.TicketReasonCategoryDTO;
import com.savbill.cpm.modules.tickets.repository.CaseRepository;
import com.savbill.cpm.modules.tickets.repository.TicketReasonCategoryRepo;
import com.savbill.cpm.repository.postpaid.PostpaidPlanRepo;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketReasonCategoryService extends ExBaseAbstractService2<TicketReasonCategoryDTO, TicketReasonCategory, Long> {


    @Autowired
    TicketReasonCategoryRepo repository;
    @Autowired
    TicketReasonCategoryMapper mapper;

    @Autowired
    SubscriberService subscriberService;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    CaseRepository caseRepository;

    public TicketReasonCategoryService(TicketReasonCategoryRepo repository, TicketReasonCategoryMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "ticket_reason_category_id");
    }

    @Override
    public String getModuleNameForLog() {
        return "{TicketReasonCategoryService}";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        makeGenericResponse()
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                switch (genericSearchModel.getFilterColumn()) {
                    case "name":
                        booleanExpression = booleanExpression.and(qTicketReasonCategory.categoryName.containsIgnoreCase(genericSearchModel.getFilterValue()));
                        break;
                    case "service":
                        booleanExpression = booleanExpression.and(qTicketReasonCategory.service.name.containsIgnoreCase(genericSearchModel.getFilterValue()));
                        break;
                }
            }
        }
        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());

        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        PageRequest pageRequest = super.generatePageRequest(page, size, "createdate", 0);
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false));
        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
//        return super.getListByPageAndSizeAndSortByAndOrderBy(page,size,"createdate",0,filterList);
    }


    public List<TicketReasonCategoryDTO> getReasonCategoryByCustomer(Integer customerId) {
        List<Integer> activePlanIds = subscriberService.getActivePlanList(customerId,false).stream().map(CustomerPlansModel::getPlanId).collect(Collectors.toList());
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        List<Integer> serviceIds = new ArrayList<>();
        postpaidPlanRepo.findAll(qPostpaidPlan.id.in(activePlanIds)).forEach(postpaidPlan -> {
            serviceIds.add(postpaidPlan.getServiceId());
        });
        List<TicketReasonCategoryDTO> ticketReasonCategoryDTOS = new ArrayList<>();
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false)).and(qTicketReasonCategory.status.eq("Active")).and(qTicketReasonCategory.service.id.in(serviceIds));
        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());

//        if (!departmentName.isEmpty()) {
//            booleanExpression = booleanExpression.and(qTicketReasonCategory.department.equalsIgnoreCase(departmentName));
//        }
        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
        return ticketReasonCategoryDTOS;

    }

    public List<TicketReasonCategoryDTO> getAllActiveReasonCategory() {
        List<TicketReasonCategoryDTO> ticketReasonCategoryDTOS = new ArrayList<>();
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false)).and(qTicketReasonCategory.status.eq("Active"));
        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }
        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());

        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
        return ticketReasonCategoryDTOS;
    }

    @Override
    public List<TicketReasonCategoryDTO> getAllEntities() throws Exception {
        List<TicketReasonCategoryDTO> list=new ArrayList<>();
        QTicketReasonCategory ticketReasonCategory=QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression expression=ticketReasonCategory.isNotNull();
        expression=expression.and(ticketReasonCategory.isDeleted.eq(false));

        if (getMvnoIdFromCurrentStaff() != 1)
        {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                expression=expression.and(ticketReasonCategory.mvnoId.in(1,getMvnoIdFromCurrentStaff()));
            else
                expression=expression.and(ticketReasonCategory.buId.in(getBUIdsFromCurrentStaff())).and(ticketReasonCategory.mvnoId.in(getMvnoIdFromCurrentStaff()));
        }

        if(getLoggedInUser().getLco())
            expression=expression.and(ticketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            expression=expression.and(ticketReasonCategory.lcoId.isNull());

        repository.findAll(expression).forEach(ticketReasonSubCategory -> list.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
        return list;
    }


    @Override
    public boolean duplicateVerifyAtSave(String name) {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    count = repository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = repository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
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
            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    count = repository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                else
                    count = repository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                if (getMvnoIdFromCurrentStaff() == 1) countEdit = repository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
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


    public List<Services> getActiveServiceForSubscribers(Integer customerId){
        List<Integer> activePlanIds = subscriberService.getActivePlanList2(customerId,false).stream().map(CustomerPlansModel::getPlanId).collect(Collectors.toList());
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        List<Long> serviceIds = new ArrayList<>();
        postpaidPlanRepo.findAll(qPostpaidPlan.id.in(activePlanIds)).forEach(postpaidPlan -> {
            serviceIds.add(Long.valueOf(postpaidPlan.getServiceId()));
        });

        List<Services> activeServiceList = new ArrayList<>();
        activeServiceList = serviceRepository.findServicesByIdIn(serviceIds);

        return activeServiceList;


    }

    public List<TicketReasonCategoryDTO> getReasonCategoryByActiveServices(List<Integer> servicesListIds) {
//        List<Integer> activePlanIds = subscriberService.getActivePlanList(customerId).stream().map(CustomerPlansModel::getPlanId).collect(Collectors.toList());
//        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
//        List<Integer> serviceIds = new ArrayList<>();
//        postpaidPlanRepo.findAll(qPostpaidPlan.id.in(activePlanIds)).forEach(postpaidPlan -> {
//            serviceIds.add(postpaidPlan.getServiceId());
//        });
        List<TicketReasonCategoryDTO> ticketReasonCategoryDTOS = new ArrayList<>();
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false)).and(qTicketReasonCategory.status.eq("Active")).and(qTicketReasonCategory.service.id.in(servicesListIds));
        if (getMvnoIdFromCurrentStaff() != 1)
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());

//        if (!departmentName.isEmpty()) {
//            booleanExpression = booleanExpression.and(qTicketReasonCategory.department.equalsIgnoreCase(departmentName));
//        }
        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
        return ticketReasonCategoryDTOS;

    }

    public Boolean getUniqueCategory(Long reasoneCatId) {
        Boolean falg=false;
        QCase qCase=QCase.case$;
        BooleanExpression booleanExpression=qCase.isNotNull();
        booleanExpression=booleanExpression.and(qCase.isDelete.eq(false));
        booleanExpression=booleanExpression.and(qCase.ticketReasonCategoryId.eq(reasoneCatId));
       List<Case>caselist=IterableUtils.toList(caseRepository.findAll(booleanExpression));
       if(caselist.size()>0){
           falg=true;
       }
       return falg;

    }
}
