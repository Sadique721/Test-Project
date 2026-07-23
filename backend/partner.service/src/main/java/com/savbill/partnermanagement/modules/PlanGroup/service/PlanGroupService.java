package com.savbill.partnermanagement.modules.PlanGroup.service;


import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.Charge.domain.Charge;
import com.savbill.partnermanagement.modules.Charge.repocitory.ChargeRepository;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.partnermanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.partnermanagement.modules.PartnerServiceAreaMapping.PartnerServiceAreaMappingRepo;
//import com.savbill.partnermanagement.modules.PartnerServiceAreaMapping.QPartnerServiceAreaMapping;
//import com.savbill.partnermanagement.modules.Plan.domain.QPostpaidPlan;
import com.savbill.partnermanagement.modules.PlanGroup.domain.*;
//import com.savbill.partnermanagement.modules.PriceGroup.domain.PriceBookPlanDetail;
//import com.savbill.partnermanagement.modules.PriceGroup.domain.QPriceBookPlanDetail;
//import com.savbill.partnermanagement.modules.PriceGroup.repository.PriceBookPlanDtlRepository;
import com.savbill.partnermanagement.modules.PlanGroup.domain.PlanGroup;
import com.savbill.partnermanagement.modules.PlanGroup.domain.PlanGroupMapping;
import com.savbill.partnermanagement.modules.PlanGroup.domain.PlanGroupMappingChargeRel;
import com.savbill.partnermanagement.modules.PlanGroup.domain.ServiceAreaPlanGroupMapping;
import com.savbill.partnermanagement.modules.partner.repository.PartnerRepository;
import com.savbill.partnermanagement.modules.partner.repository.PlanGroupMappingChargeRelRepo;
import com.savbill.partnermanagement.modules.Plan.domain.PostpaidPlan;
import com.savbill.partnermanagement.modules.Plan.repository.PostpaidPlanRepo;
import com.savbill.partnermanagement.modules.PlanGroup.repocitory.PlanGroupMappingRepository;
import com.savbill.partnermanagement.modules.PlanGroup.repocitory.PlanGroupRepository;
import com.savbill.partnermanagement.modules.PlanGroup.repocitory.ServiceAreaPlangroupMappingRepo;
import com.savbill.partnermanagement.modules.partner.service.PartnerService;
import com.savbill.partnermanagement.rabbitmq.product.SavePlanGroupSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.product.UpdatePlanGroupSharedDataMessage;
import com.savbill.partnermanagement.security.dto.LoggedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanGroupService  {

    @Autowired
    private PlanGroupRepository entityRepository;

    @Autowired
    private ServiceAreaPlangroupMappingRepo serviceAreaPlangroupMappingRepo;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private PlanGroupMappingRepository planGroupMappingRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private PlanGroupMappingChargeRelRepo chargerelrepo;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private  PlanGroupRepository planGroupRepository;

    @Autowired
    PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    PartnerService partnerService;

//    @Autowired
//    PriceBookPlanDtlRepository priceBookPlanDtlRepository;
    
    public void savePlanGroupData(SavePlanGroupSharedDataMessage planGroupSharedDataMessage){
        ApplicationLogger.logger.info("save Plan Group Data called");
        PlanGroup planGroup=new PlanGroup();
        planGroup.setPlanGroupId(planGroupSharedDataMessage.getPlanGroupId());
        planGroup.setPlanGroupName(planGroupSharedDataMessage.getPlanGroupName());
        planGroup.setStatus(planGroupSharedDataMessage.getStatus());
        planGroup.setMvnoId(planGroupSharedDataMessage.getMvnoId());
        planGroup.setPlantype(planGroupSharedDataMessage.getPlantype());
        planGroup.setPlanMode(planGroupSharedDataMessage.getPlanMode());
        planGroup.setIsDelete(planGroupSharedDataMessage.getIsDelete());
        planGroup.setDbr(planGroupSharedDataMessage.getDbr());
        planGroup.setPlanGroupType(planGroupSharedDataMessage.getPlanGroupType());
        planGroup.setCategory(planGroupSharedDataMessage.getCategory());
        planGroup.setNextTeamHierarchyMappingId(planGroupSharedDataMessage.getNextTeamHierarchyMappingId());
        planGroup.setNextStaff(planGroupSharedDataMessage.getNextStaff());
        planGroup.setAccessibility(planGroupSharedDataMessage.getAccessibility());
        planGroup.setAllowDiscount(planGroupSharedDataMessage.getInvoiceToOrg());
        planGroup.setOfferprice(planGroupSharedDataMessage.getOfferprice());
        planGroup.setTemplateId(planGroupSharedDataMessage.getTemplateId());
        planGroup.setInvoiceToOrg(planGroupSharedDataMessage.getInvoiceToOrg());
        planGroup.setRequiredApproval(planGroupSharedDataMessage.getRequiredApproval());
        planGroup.setCreatedById(planGroupSharedDataMessage.getCreatedById());
        planGroup.setLastModifiedById(planGroupSharedDataMessage.getLastModifiedById());
        planGroupRepository.save(planGroup);
ApplicationLogger.logger.info("save Plan Group Data saved");
        List<PlanGroupMapping> planGroupMappings = planGroupSharedDataMessage.getPlanMappingList();
        List<PlanGroupMapping> planGroupMappingList = new ArrayList<>();
        for (PlanGroupMapping data : planGroupMappings){
            PostpaidPlan plan = postpaidPlanRepo.findById(data.getPlanId().intValue()).get();
            PlanGroupMapping planGroupMapping = new PlanGroupMapping(data,plan, planGroup);
            planGroupMappingList.add(planGroupMapping);
        }
        planGroupMappingRepository.saveAll(planGroupMappingList);
        ApplicationLogger.logger.info("save Plan Group Mapping Data saved");

        List<PlanGroupMappingChargeRel> planGroupMappingChargeRels = planGroupSharedDataMessage.getPlanGroupMappingChargeRelsList();
        List<PlanGroupMappingChargeRel> planGroupMappingChargeRelList = new ArrayList<>();
        for (PlanGroupMappingChargeRel data : planGroupMappingChargeRels){
            PlanGroupMapping planGroupMapping = planGroupMappingRepository.findById(data.getPlanGroupMappingId()).get();
            Charge charge = chargeRepository.findById(data.getChargeid()).get();
            PlanGroupMappingChargeRel planGroupMappingChargeRel = new PlanGroupMappingChargeRel(data,charge,planGroupMapping);
            planGroupMappingChargeRelList.add(planGroupMappingChargeRel);
        }
        chargerelrepo.saveAll(planGroupMappingChargeRelList);
        ApplicationLogger.logger.info("save Plan Group Mapping Charge Data saved");

        List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappings = planGroupSharedDataMessage.getServiceAreaPlanGroupMappingList();
        List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappingList = new ArrayList<>();
        for (ServiceAreaPlanGroupMapping data : serviceAreaPlanGroupMappings){
            ServiceArea serviceArea = serviceAreaRepository.findById(data.getServiceAreaId()).get();
            ServiceAreaPlanGroupMapping serviceAreaPlanGroupMapping = new ServiceAreaPlanGroupMapping(data,planGroup,serviceArea);
            serviceAreaPlanGroupMappingList.add(serviceAreaPlanGroupMapping);
        }
        serviceAreaPlangroupMappingRepo.saveAll(serviceAreaPlanGroupMappingList);
        ApplicationLogger.logger.info("save Service Area Plan Group Mapping Data saved");
        List<Long>serviceAreaList=new ArrayList<>();
        for(ServiceArea  area:planGroupSharedDataMessage.getServicearea()){
            serviceAreaList.add(area.getId());
        }
        List<ServiceArea> serviceArea1=serviceAreaRepository.findAllById(serviceAreaList);
        ApplicationLogger.logger.info("save Service Area Plan Group Mapping Data saved");
        planGroup.setServicearea_id(serviceArea1.get(0).getId());
        planGroupRepository.save(planGroup);
        ApplicationLogger.logger.info("save Plan Group Data saved");
    }
    @Transactional
    public void updatePlanGroupData(UpdatePlanGroupSharedDataMessage updatePlanGroupSharedDataMessage) {
        ApplicationLogger.logger.info("update Plan Group Data called");
        PlanGroup planGroup=planGroupRepository.findById(updatePlanGroupSharedDataMessage.getPlanGroupId()).orElse(null);
        if(planGroup!=null){
            planGroup.setPlanGroupId(updatePlanGroupSharedDataMessage.getPlanGroupId());
            planGroup.setPlanGroupName(updatePlanGroupSharedDataMessage.getPlanGroupName());
            planGroup.setStatus(updatePlanGroupSharedDataMessage.getStatus());
            planGroup.setMvnoId(updatePlanGroupSharedDataMessage.getMvnoId());
            planGroup.setPlantype(updatePlanGroupSharedDataMessage.getPlantype());
            planGroup.setPlanMode(updatePlanGroupSharedDataMessage.getPlanMode());
            planGroup.setIsDelete(updatePlanGroupSharedDataMessage.getIsDelete());
            planGroup.setDbr(updatePlanGroupSharedDataMessage.getDbr());
            planGroup.setPlanGroupType(updatePlanGroupSharedDataMessage.getPlanGroupType());
            planGroup.setCategory(updatePlanGroupSharedDataMessage.getCategory());
            planGroup.setNextTeamHierarchyMappingId(updatePlanGroupSharedDataMessage.getNextTeamHierarchyMappingId());
            planGroup.setNextStaff(updatePlanGroupSharedDataMessage.getNextStaff());
            planGroup.setAccessibility(updatePlanGroupSharedDataMessage.getAccessibility());
            planGroup.setAllowDiscount(updatePlanGroupSharedDataMessage.getInvoiceToOrg());
            planGroup.setOfferprice(updatePlanGroupSharedDataMessage.getOfferprice());
            planGroup.setProductPlanGroupMappingList(updatePlanGroupSharedDataMessage.getProductPlanGroupMappingList());
            planGroup.setTemplateId(updatePlanGroupSharedDataMessage.getTemplateId());
            planGroup.setInvoiceToOrg(updatePlanGroupSharedDataMessage.getInvoiceToOrg());
            planGroup.setRequiredApproval(updatePlanGroupSharedDataMessage.getRequiredApproval());
            planGroup.setCreatedById(updatePlanGroupSharedDataMessage.getCreatedById());
            planGroup.setLastModifiedById(updatePlanGroupSharedDataMessage.getLastModifiedById());
            planGroupRepository.save(planGroup);
ApplicationLogger.logger.info(" Plan Group Data saved");
            List<PlanGroupMapping> planGroupMappings = planGroup.getPlanMappingList();
            List<PlanGroupMapping> existingPlangroupMapping =  planGroupMappingRepository.findByPlanGroupMappingIdIn(planGroupMappings.stream().map(PlanGroupMapping::getPlanGroupMappingId).collect(Collectors.toList()));
            planGroupMappingRepository.deleteAll(existingPlangroupMapping);
            ApplicationLogger.logger.info("existingPlangroupMapping deleted");
            planGroupMappingRepository.flush();
            ApplicationLogger.logger.info("existingPlangroupMapping flushed");
            List<PlanGroupMapping> planGroupMappingList = new ArrayList<>();
            PostpaidPlan plan=null;
            for (PlanGroupMapping data : planGroupMappings){
                if (data.getPlanId()!=null) {
                    plan = postpaidPlanRepo.findById(data.getPlanId().intValue()).get();
                }else {
                    plan = data.getPlan();

                }
                PlanGroupMapping planGroupMapping = new PlanGroupMapping(data,plan, planGroupRepository.findById(planGroup.getPlanGroupId()).get());
                planGroupMappingList.add(planGroupMapping);
            }
            planGroupMappingRepository.saveAll(planGroupMappingList);
ApplicationLogger.logger.info("planGroupMappingList saved");
            List<PlanGroupMappingChargeRel> planGroupMappingChargeRels = updatePlanGroupSharedDataMessage.getPlanGroupMappingChargeRelsList();
            List<Long> ids = planGroupMappingChargeRels.stream().map(PlanGroupMappingChargeRel::getId).collect(Collectors.toList());
            List<PlanGroupMappingChargeRel> existingPlanGrpchargeRel = chargerelrepo.findByIdIn(ids);
            chargerelrepo.deleteAll(existingPlanGrpchargeRel);
            ApplicationLogger.logger.info("existingPlanGrpchargeRel deleted");
            List<PlanGroupMappingChargeRel> planGroupMappingChargeRelList = new ArrayList<>();
            for (PlanGroupMappingChargeRel data : planGroupMappingChargeRels){
                PlanGroupMapping planGroupMapping = planGroupMappingRepository.findById(data.getPlanGroupMappingId()).orElse(null);
                Charge charge = chargeRepository.findById(data.getChargeid()).get();
                PlanGroupMappingChargeRel planGroupMappingChargeRel = new PlanGroupMappingChargeRel(data,charge,planGroupMapping);
                planGroupMappingChargeRelList.add(planGroupMappingChargeRel);
            }
            chargerelrepo.saveAll(planGroupMappingChargeRelList);
ApplicationLogger.logger.info("planGroupMappingChargeRelList saved");
            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappings = updatePlanGroupSharedDataMessage.getServiceAreaPlanGroupMappingList();
            List<Long> saMappingIds = serviceAreaPlanGroupMappings.stream().map(ServiceAreaPlanGroupMapping::getId).collect(Collectors.toList());
            List<ServiceAreaPlanGroupMapping> existingList = serviceAreaPlangroupMappingRepo.findByIdIn(saMappingIds);
            serviceAreaPlangroupMappingRepo.deleteAll(existingList);
ApplicationLogger.logger.info("existingList deleted");
            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappingList = new ArrayList<>();
            for (ServiceAreaPlanGroupMapping data : serviceAreaPlanGroupMappings){
                ServiceArea serviceArea = serviceAreaRepository.findById(data.getServiceAreaId()).get();
                ServiceAreaPlanGroupMapping serviceAreaPlanGroupMapping = new ServiceAreaPlanGroupMapping(data,planGroup,serviceArea);
                serviceAreaPlanGroupMappingList.add(serviceAreaPlanGroupMapping);
            }
            serviceAreaPlangroupMappingRepo.saveAll(serviceAreaPlanGroupMappingList);
        ApplicationLogger.logger.info("service Area Plan Group Mapping List saved");
        }
    }
//    public List<PlanGroup> findAllPlanGroupList(Integer mvnoId, String mode, String planCategory, Integer custId, String accessibility) {
//        try {
//            QPlanGroup qPlanGroup = QPlanGroup.planGroup;
//            QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
//            QPlanGroupMapping qPlanGroupMapping = QPlanGroupMapping.planGroupMapping;
//
//            BooleanExpression exp = qPlanGroup.isNotNull();
//            if (getMvnoIdFromCurrentStaff() != 1)
//                exp = exp.and(qPlanGroup.mvnoId.in(mvnoId, 1));
//            exp = exp.and(qPlanGroup.isDelete.eq(false));
//            exp=exp.and(qPlanGroup.status.notEqualsIgnoreCase("Inactive"));
//            exp=exp.and(qPlanGroup.status.notEqualsIgnoreCase("Rejected"));
//            if(!mode.equalsIgnoreCase("NORMAL") && !mode.equalsIgnoreCase("SPECIAL")) {
//                exp = exp.and(qPlanGroup.planGroupType.notEqualsIgnoreCase("DTV Addon"));
//            }
//            if (mode != null && !mode.isEmpty()) {
//                if (mode.equalsIgnoreCase(Constants.NORMAL))
//                    exp = exp.and(qPlanGroup.planMode.eq(Constants.NORMAL)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
//                else if (mode.equalsIgnoreCase(Constants.SPECIAL))
//                    exp = exp.and(qPlanGroup.planMode.eq(Constants.SPECIAL)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
//            }
//            if (getLoggedInUserPartnerId() != 1) {
//                Partner partner = partnerService.get(getLoggedInUserPartnerId());
//                Boolean isAllPlanGroupSelected =  partner.getPriceBookId().getIsAllPlanGroupSelected();
//                if (!isAllPlanGroupSelected) {
//                    QPriceBookPlanDetail qPriceBookPlanDetail = QPriceBookPlanDetail.priceBookPlanDetail;
//                    BooleanExpression expression =null;// qPriceBookPlanDetail.isNotNull().and(qPriceBookPlanDetail.priceBook.eq(partner.getPriceBookId()));
//                    List<PriceBookPlanDetail> list = (List<PriceBookPlanDetail>) priceBookPlanDtlRepository.findAll(expression);
//                    List<Integer> plangroupIds = new ArrayList<>();
//                    for (PriceBookPlanDetail planGroup : list) {
//                        if (planGroup.getPlanGroup() != null) {
//                            plangroupIds.add(planGroup.getPlanGroup().getPlanGroupId());
//                        }
//                    }
//                    exp = exp.and(qPlanGroup.planGroupId.in(plangroupIds));
//                }
//            }
//
//            if (planCategory != null && !planCategory.isEmpty()) {
//                if (planCategory.equalsIgnoreCase(Constants.NORMAL))
//                    exp = exp.and(qPlanGroup.category.eq(Constants.NORMAL)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
//                else if (planCategory.equalsIgnoreCase(Constants.BUSINESS_PROMOTION))
//                    exp = exp.and(qPlanGroup.category.eq(Constants.BUSINESS_PROMOTION)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
//
//            }
//
//            if (accessibility != null && !accessibility.isEmpty()) {
//                exp = exp.and(qPlanGroup.accessibility.equalsIgnoreCase(accessibility)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
//            }
//
//
//            if (getBUIdsFromCurrentStaff().size() != 0)
//                exp = exp
//                        .and(qPlanGroup.mvnoId.eq(1)
//                                .or(qPlanGroup.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPlanGroup.buId.in(getBUIdsFromCurrentStaff())))).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
//            List<Integer> serviceIds = null;
//
//            exp=exp.and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
//            List<PlanGroup> planGroupList = (List<PlanGroup>) entityRepository.findAll(exp);
//            if (serviceIds != null) {
//                List<Integer> finalServiceIds = serviceIds.stream().sorted().collect(Collectors.toList());
//                planGroupList.removeIf(planGroup -> planGroup.getPlanMappingList().size() != finalServiceIds.size());
//                planGroupList.removeIf(planGroup -> {
//                    List<PostpaidPlan> list = planGroup.getPlanMappingList().stream().map(planGroupMapping -> planGroupMapping.getPlan()).collect(Collectors.toList());
//                    List<Integer> serviceIdsList = list.stream().map(PostpaidPlan::getServiceId).collect(Collectors.toList());
//                    return !CommonUtils.listEqualsIgnoreOrder(serviceIdsList, finalServiceIds);
//                });
//            }
//            Integer partnerId = getLoggedInUserPartnerId();
//            if (partnerId!=null) {
//                Partner partner = partnerRepository.findById(partnerId).get();
//                if (partner.getPartnerType() != "LCO" && partnerId != 1) {
//                    QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
//                    BooleanExpression exp1 = qPartnerServiceAreaMapping.isNotNull().and(qPartnerServiceAreaMapping.partnerId.eq(partnerId));
//                    List<PartnerServiceAreaMapping> partnerServiceAreaMappings = (List<PartnerServiceAreaMapping>) partnerServiceAreaMappingRepo.findAll(exp1);
//                    List<Long> serviceReaIds = partnerServiceAreaMappings.stream()
//                            .mapToLong(PartnerServiceAreaMapping::getServiceId)
//                            .boxed()
//                            .collect(Collectors.toList());
//                    List<ServiceArea> serviceAreas = serviceAreaRepository.findAllByIdIn(serviceReaIds);
//                    planGroupList = planGroupList.stream()
//                            .filter(two -> two.getServicearea().stream().anyMatch(serviceAreas::contains))
//                            .collect(Collectors.toList());
//                }
//            }
//
//            return planGroupList;
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }


    public Integer getMvnoIdFromCurrentStaff() {
        ApplicationLogger.logger.info("MVNO - get Mvno Id From Current Staff");
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if(securityContext.getAuthentication().getPrincipal() != null)
                    mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        ApplicationLogger.logger.info("MVNO - get Mvno Id From Current Staff" + mvnoId);
        return mvnoId;
    }


    public int getLoggedInUserPartnerId() {
        ApplicationLogger.logger.info("MVNO - get Logged In User Partner Id");
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getLoggedInUserPartnerId" + e.getMessage(), e);
            partnerId = -1;
        }
        ApplicationLogger.logger.info("MVNO - get Logged In User Partner Id" + partnerId);
        return partnerId;
    }


    public List<java.lang.Long> getBUIdsFromCurrentStaff() {
        ApplicationLogger.logger.info("MVNO - get BUIds From Current Staff");
        List<java.lang.Long> mvnoIds = new ArrayList<java.lang.Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        ApplicationLogger.logger.info("MVNO - get BUIds From Current Staff" + mvnoIds);
        return mvnoIds;
    }
}
