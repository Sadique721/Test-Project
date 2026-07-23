package com.savbill.ticketmanagement.core.modules.tickets.service;


import com.savbill.ticketmanagement.RabbitCallFromTicketToGW.CommanRabbitCall;
import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.dto.GenericSearchModel;
import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.ticketmanagement.core.modules.Plan.repository.PostpaidPlanRepo;
import com.savbill.ticketmanagement.core.modules.PlanService.repository.ServiceRepository;
import com.savbill.ticketmanagement.core.modules.tickets.domain.*;
import com.savbill.ticketmanagement.core.modules.tickets.domain.*;
import com.savbill.ticketmanagement.core.modules.tickets.mapper.TicketReasonCategoryMapper;
import com.savbill.ticketmanagement.core.modules.tickets.model.TicketReasonCategoryDTO;
import com.savbill.ticketmanagement.core.modules.tickets.repository.CaseRepository;
import com.savbill.ticketmanagement.core.modules.tickets.repository.TicketReasonCategoryRepo;
import com.savbill.ticketmanagement.core.modules.tickets.repository.TicketSubCategoryReasonCategoryMappingRepository;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
//import jdk.vm.ci.services.Services;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TicketReasonCategoryService extends ExBaseAbstractService<TicketReasonCategoryDTO, TicketReasonCategory, Long> {


    public TicketReasonCategoryService(TicketReasonCategoryRepo repository, TicketReasonCategoryMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "ticket_reason_category_id");
    }

    @Override
    public String getModuleNameForLog() {
        return "{TicketReasonCategoryService}";
    }

    @Autowired
    TicketReasonCategoryRepo repository;
    @Autowired
    TicketReasonCategoryMapper mapper;


    @Autowired
    CommanRabbitCall commanRabbitCall;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    CaseRepository caseRepository;



    @Autowired
    TicketSubCategoryReasonCategoryMappingRepository ticketSubCategoryReasonCategoryMappingRepository;


    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        //makeGenericResponse()
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



    //This below api is not used anywhare in ticket hence commenting the api and in future will remoe it


//    public List<TicketReasonCategoryDTO> getReasonCategoryByCustomer(Integer customerId) {
//        //Make this rabbitmq call
//        List<Integer> activePlanIds =new ArrayList<>();
//
//        commanRabbitCall.senReqForActivePlanListToApiGw(customerId);
//
//        List<CustActivePlanListMapping> custActivePlanListMappings = custActivePlanMappingRepo.findAllByCustomerId(customerId);
//        for(int i =0; i<custActivePlanListMappings.size();i++){
//            activePlanIds.add(custActivePlanListMappings.get(i).getActivePlanId());
//        }
//
//        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
//        List<Integer> serviceIds = new ArrayList<>();
//        postpaidPlanRepo.findAll(qPostpaidPlan.id.in(activePlanIds)).forEach(postpaidPlan -> {
//            serviceIds.add(postpaidPlan.getServiceId());
//        });
//        List<TicketReasonCategoryDTO> ticketReasonCategoryDTOS = new ArrayList<>();
//        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
//        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false)).and(qTicketReasonCategory.status.eq("Active")).and(qTicketReasonCategory.service.id.in(serviceIds));
//        if (getMvnoIdFromCurrentStaff() != 1)
//            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
//        }
//
//        if(getLoggedInUser().getLco())
//            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
//        else
//            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());
//
////        if (!departmentName.isEmpty()) {
////            booleanExpression = booleanExpression.and(qTicketReasonCategory.department.equalsIgnoreCase(departmentName));
////        }
//        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
//        return ticketReasonCategoryDTOS;
//
//    }

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


    //This service api we will call from the apigw to reduce the rabbitq call hence commenting this api
//    public List<Services> getActiveServiceForSubscribers(Integer customerId){
//        List<Services> activeServiceList = new ArrayList<>();
//        //Make this rabbitmq call
//        List<Integer> activePlanIds = new ArrayList<>();
//
//        commanRabbitCall.senReqForActivePlanListToApiGw(customerId);
//
//        List<CustActivePlanListMapping> custActivePlanListMappings = custActivePlanMappingRepo.findAllByCustomerId(customerId);
//        for(int i =0; i<custActivePlanListMappings.size();i++){
//            activePlanIds.add(custActivePlanListMappings.get(i).getActivePlanId());
//        }
//
//        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
//        List<Long> serviceIds = new ArrayList<>();
//        postpaidPlanRepo.findAll(qPostpaidPlan.id.in(activePlanIds)).forEach(postpaidPlan -> {
//            serviceIds.add(Long.valueOf(postpaidPlan.getServiceId()));
//        });
//
//        activeServiceList = serviceRepository.findServicesByIdIn(serviceIds);
//
//        return activeServiceList;
//
//
//    }

    public List<TicketReasonCategoryDTO> getReasonCategoryByActiveServices(List<Integer> servicesListIds) {
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

        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
        return ticketReasonCategoryDTOS;

    }

    public Boolean getUniqueCategory(Long reasoneCatId) {
        Boolean falg=false;
        QCase qCase=QCase.case$;
        BooleanExpression booleanExpression=qCase.isNotNull();
        booleanExpression=booleanExpression.and(qCase.isDelete.eq(false));
        booleanExpression=booleanExpression.and(qCase.ticketReasonCategoryId.eq(reasoneCatId));
        List<TicketSubCategoryReasonCategoryMapping> ticketReasonCategoryDTOS=IterableUtils.toList(ticketSubCategoryReasonCategoryMappingRepository.findByTicketReasonCategoryId(reasoneCatId));
       List<Case>caselist=IterableUtils.toList(caseRepository.findAll(booleanExpression));
       if(caselist.size()>0 || ticketReasonCategoryDTOS.size()>0){
           falg=true;
       }
       return falg;

    }

    public List<TicketReasonCategory> isReasonCategoryDefault(Integer serviceId){
        List<TicketReasonCategory> ticketReasonCategoryList = repository.findAllDefualtReasonCategoryUsingServiceId(serviceId);
        return ticketReasonCategoryList;
    }


}
