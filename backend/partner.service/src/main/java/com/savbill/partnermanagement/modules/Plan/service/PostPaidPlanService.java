package com.savbill.partnermanagement.modules.Plan.service;

import com.savbill.partnermanagement.core.constants.CommonConstants;
import com.savbill.partnermanagement.core.constants.Constants;
import com.savbill.partnermanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.Charge.domain.Charge;
import com.savbill.partnermanagement.modules.Charge.repocitory.ChargeRepository;
import com.savbill.partnermanagement.modules.MasterManagement.BusinessUnit.BusinessUnit;
import com.savbill.partnermanagement.modules.MasterManagement.BusinessUnit.BusinessUnitRepository;
import com.savbill.partnermanagement.modules.Plan.domain.*;
import com.savbill.partnermanagement.modules.Plan.domain.PostPaidPlanServiceAreaMapping;
import com.savbill.partnermanagement.modules.Plan.domain.PostpaidPlan;
import com.savbill.partnermanagement.modules.Plan.domain.PostpaidPlanCharge;
import com.savbill.partnermanagement.modules.Plan.domain.QPostpaidPlan;
import com.savbill.partnermanagement.modules.Plan.dto.PostpaidPlanPojo;
import com.savbill.partnermanagement.modules.Plan.mapper.PostpaidPlanMapper;
import com.savbill.partnermanagement.modules.Plan.repository.PostPaidPlanServiceAreaMappingRepo;
import com.savbill.partnermanagement.modules.Plan.repository.PostpaidPlanRepo;
//import com.savbill.partnermanagement.modules.PriceGroup.domain.PriceBookPlanDetail;
//import com.savbill.partnermanagement.modules.PriceGroup.domain.QPriceBookPlanDetail;
//import com.savbill.partnermanagement.modules.PriceGroup.repository.PriceBookPlanDtlRepository;
import com.savbill.partnermanagement.modules.Services.ServiceRepository;
import com.savbill.partnermanagement.modules.partner.repository.PartnerRepository;
import com.savbill.partnermanagement.rabbitmq.product.SavePlanSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.product.UpdatePlanSharedDataMessage;
import com.savbill.partnermanagement.security.dto.LoggedInUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PostPaidPlanService {

    @Autowired
    PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepo;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    BusinessUnitRepository businessUnitRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    PartnerRepository partnerRepository;

    @PersistenceContext
    EntityManager entityManager;

//    @Autowired
//    PriceBookPlanDtlRepository priceBookPlanDtlRepository;

    @Autowired
    PostPaidPlanServiceAreaMappingRepo planServiceAreaRepo;

    @Autowired
    PostpaidPlanMapper postpaidPlanMapper;

    private static Log log = LogFactory.getLog(PostPaidPlanService.class);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    @Transactional
    public void savePostpaidPlan(SavePlanSharedDataMessage message){
        try {

            PostpaidPlan postpaidPlan = new PostpaidPlan();

            postpaidPlan.setId(message.getId());
            postpaidPlan.setServiceId(message.getServiceId());
            postpaidPlan.setDisplayName(message.getDisplayName());
            postpaidPlan.setCode(message.getCode());
            postpaidPlan.setPlanStatus(message.getPlanStatus());
            postpaidPlan.setName(message.getName());
            postpaidPlan.setBuId(message.getBuId());
            postpaidPlan.setMvnoId(message.getMvnoId());
            postpaidPlan.setIsDelete(message.getIsDelete());
            postpaidPlan.setStatus(message.getStatus());
            postpaidPlan.setPlantype(message.getPlantype());
            postpaidPlan.setMode(message.getMode());
            postpaidPlan.setCategory(message.getCategory());
            postpaidPlan.setDesc(message.getDesc());

            postpaidPlanRepo.save(postpaidPlan);
            ApplicationLogger.logger.info("Postpaid Plan Saved Successfully");
        }catch (Exception e){
            ApplicationLogger.logger.error("Error While Creating Postpaid Plan, "+e.getMessage());
        }

    }


    @Transactional
    public void updatePostPaidPlan(UpdatePlanSharedDataMessage message){
        ApplicationLogger.logger.info("update Postpaid Plan Started");
        try {
            updatePostPaidPlanData(message);
        }catch (Exception e){
            ApplicationLogger.logger.error("error while Updating Postpaid plan"+e.getMessage());
        }

    }

    public void savePostPaidPlanData(SavePlanSharedDataMessage savePlanSharedDataMessage) {
        ApplicationLogger.logger.info("save Postpaid Plan Started");
        PostpaidPlan postpaidPlan=new PostpaidPlan();
        postpaidPlan.setId(savePlanSharedDataMessage.getId());
        postpaidPlan.setName(savePlanSharedDataMessage.getName());
        postpaidPlan.setDisplayName(savePlanSharedDataMessage.getDisplayName());
        postpaidPlan.setCode(savePlanSharedDataMessage.getCode());
        postpaidPlan.setDesc(savePlanSharedDataMessage.getDesc());
        postpaidPlan.setCategory(savePlanSharedDataMessage.getCategory());
        postpaidPlan.setMaxChild(savePlanSharedDataMessage.getMaxChild());
        postpaidPlan.setStartDate( LocalDate.parse(savePlanSharedDataMessage.getStartDate()));
        postpaidPlan.setEndDate(LocalDate.parse(savePlanSharedDataMessage.getEndDate()));
        postpaidPlan.setQuota(savePlanSharedDataMessage.getQuota());
        postpaidPlan.setQuotaUnit(savePlanSharedDataMessage.getQuotaUnit());
        postpaidPlan.setUploadQOS(savePlanSharedDataMessage.getUploadQOS());
        postpaidPlan.setDownloadQOS(savePlanSharedDataMessage.getDownloadQOS());
        postpaidPlan.setUploadTs(savePlanSharedDataMessage.getUploadTs());
        postpaidPlan.setDownloadTs(savePlanSharedDataMessage.getDownloadTs());
        postpaidPlan.setAllowOverUsage(savePlanSharedDataMessage.getAllowOverUsage());
        postpaidPlan.setStatus(savePlanSharedDataMessage.getStatus());
        postpaidPlan.setPlanStatus(savePlanSharedDataMessage.getPlanStatus());
        postpaidPlan.setChildQuota(savePlanSharedDataMessage.getChildQuota());
        postpaidPlan.setChildQuotaUnit(savePlanSharedDataMessage.getChildQuotaUnit());
        postpaidPlan.setSlice(savePlanSharedDataMessage.getSlice());
        postpaidPlan.setSliceUnit(savePlanSharedDataMessage.getSliceUnit());
        postpaidPlan.setAttachedToAllHotSpots(savePlanSharedDataMessage.getAttachedToAllHotSpots());
        postpaidPlan.setParam1(savePlanSharedDataMessage.getParam1());
        postpaidPlan.setParam2(savePlanSharedDataMessage.getParam2());
        postpaidPlan.setMvnoId(savePlanSharedDataMessage.getMvnoId());
        postpaidPlan.setTaxId(savePlanSharedDataMessage.getTaxId());
        postpaidPlan.setServiceId(savePlanSharedDataMessage.getServiceId());
        postpaidPlan.setTimebasepolicyId(savePlanSharedDataMessage.getTimebasepolicyId());
        postpaidPlan.setPlantype(savePlanSharedDataMessage.getPlantype());
        postpaidPlan.setDbr(savePlanSharedDataMessage.getDbr());
        List<PostpaidPlanCharge> postpaidPlanChargeList = new ArrayList<>();
        if(savePlanSharedDataMessage.getChargeList().size()>0){
            ApplicationLogger.logger.info("save Plan Shared Data Message.getChargeList().size()"+savePlanSharedDataMessage.getChargeList().size());
            for(PostpaidPlanCharge postpaidPlanCharge : savePlanSharedDataMessage.getChargeList() ){
                PostpaidPlanCharge postpaidPlanCharge1 = new PostpaidPlanCharge();
                postpaidPlanCharge1.setId(postpaidPlanCharge.getId());
                postpaidPlanCharge1.setPlan(postpaidPlan);
                Charge charge = null;
                if (postpaidPlanCharge.getChargeId()!=null) {
                    ApplicationLogger.logger.info("post paid Plan Charge.getChargeId()"+postpaidPlanCharge.getChargeId());
                     charge = chargeRepository.findById(postpaidPlanCharge.getChargeId()).get();
                }else {
                     charge = chargeRepository.findById(postpaidPlanCharge.getCharge().getId()).get();

                }
                postpaidPlanCharge1.setCharge(charge);
                postpaidPlanCharge1.setChargeName(charge.getName());
                postpaidPlanCharge1.setChargeprice(postpaidPlanCharge.getChargeprice());
                postpaidPlanCharge1.setBillingCycle(postpaidPlanCharge.getBillingCycle());
                postpaidPlanChargeList.add(postpaidPlanCharge1);
            }
            postpaidPlan.setChargeList(postpaidPlanChargeList);
        }
        postpaidPlan.setPlanGroup(savePlanSharedDataMessage.getPlanGroup());
        postpaidPlan.setValidity(savePlanSharedDataMessage.getValidity());
        postpaidPlan.setSaccode(savePlanSharedDataMessage.getSaccode());
        postpaidPlan.setMaxconcurrentsession(savePlanSharedDataMessage.getMaxconcurrentsession());
        postpaidPlan.setQuotatime(savePlanSharedDataMessage.getQuotatime());
        postpaidPlan.setQuotaunittime(savePlanSharedDataMessage.getQuotaunittime());
        postpaidPlan.setQuotatype(savePlanSharedDataMessage.getQuotatype());
        postpaidPlan.setOfferprice(savePlanSharedDataMessage.getOfferprice());
        postpaidPlan.setQuotadid(savePlanSharedDataMessage.getQuotadid());
        postpaidPlan.setQuotaintercom(savePlanSharedDataMessage.getQuotaintercom());
        postpaidPlan.setIsDelete(savePlanSharedDataMessage.getIsDelete());
        postpaidPlan.setDataCategory(savePlanSharedDataMessage.getDataCategory());
        postpaidPlan.setTaxamount(savePlanSharedDataMessage.getTaxamount());
        if(savePlanSharedDataMessage.getPostPaidPlanServiceAreaMappingList().size()>0){
            ApplicationLogger.logger.info("save Postpaid Plan Shared Data Message.getPostPaidPlanServiceAreaMappingList().size()"+savePlanSharedDataMessage.getPostPaidPlanServiceAreaMappingList().size());
            List<PostPaidPlanServiceAreaMapping> planServiceAreaMappingList = new ArrayList<>();
            for(PostPaidPlanServiceAreaMapping entity :savePlanSharedDataMessage.getPostPaidPlanServiceAreaMappingList()){
                PostPaidPlanServiceAreaMapping postPaidPlanServiceAreaMapping = new PostPaidPlanServiceAreaMapping();
                postPaidPlanServiceAreaMapping.setId(entity.getId());
                postPaidPlanServiceAreaMapping.setPlanId(entity.getPlanId());
                postPaidPlanServiceAreaMapping.setServiceId(entity.getServiceId());
                postPaidPlanServiceAreaMapping.setCreatedOn(LocalDateTime.parse(entity.getCreatedOnString(),formatter));
                postPaidPlanServiceAreaMapping.setLastmodifiedOn(LocalDateTime.parse(entity.getLastmodifiedOnString(),formatter));
                planServiceAreaMappingList.add(postPaidPlanServiceAreaMapping);
            }
            postPaidPlanServiceAreaMappingRepo.saveAll(planServiceAreaMappingList);
        ApplicationLogger.logger.info("save Postpaid Plan Shared Data Message.getPostPaidPlanServiceAreaMappingList().size()"+savePlanSharedDataMessage.getPostPaidPlanServiceAreaMappingList().size());
        }
        postpaidPlan.setQuotaResetInterval(savePlanSharedDataMessage.getQuotaResetInterval());
        postpaidPlan.setMode(savePlanSharedDataMessage.getMode());
        postpaidPlan.setUnitsOfValidity(savePlanSharedDataMessage.getUnitsOfValidity());
        postpaidPlan.setBuId(savePlanSharedDataMessage.getBuId());
        postpaidPlan.setNextTeamHierarchyMapping(savePlanSharedDataMessage.getNextTeamHierarchyMapping());
        postpaidPlan.setNextStaff(savePlanSharedDataMessage.getNextStaff());
        postpaidPlan.setNewOfferPrice(savePlanSharedDataMessage.getNewOfferPrice());
        postpaidPlan.setAccessibility(savePlanSharedDataMessage.getAccessibility());
        postpaidPlan.setProductId(savePlanSharedDataMessage.getProductId());
        postpaidPlan.setInvoiceToOrg(savePlanSharedDataMessage.getInvoiceToOrg());
        postpaidPlan.setRequiredApproval(savePlanSharedDataMessage.getRequiredApproval());
        postpaidPlan.setBandwidth(savePlanSharedDataMessage.getBandwidth());
        postpaidPlan.setLink_type(savePlanSharedDataMessage.getLink_type());
        postpaidPlan.setConnection_type(savePlanSharedDataMessage.getConnection_type());
        postpaidPlan.setDistance(savePlanSharedDataMessage.getDistance());
        postpaidPlan.setRam(savePlanSharedDataMessage.getRam());
        postpaidPlan.setCpu(savePlanSharedDataMessage.getCpu());
        postpaidPlan.setStorage(savePlanSharedDataMessage.getStorage());
        postpaidPlan.setStorage_type(savePlanSharedDataMessage.getStorage_type());
        postpaidPlan.setAuto_backup(savePlanSharedDataMessage.getAuto_backup());
        postpaidPlan.setCpanel(savePlanSharedDataMessage.getCpanel());
        postpaidPlan.setLocation(savePlanSharedDataMessage.getLocation());
        postpaidPlan.setQuantity(savePlanSharedDataMessage.getQuantity());
        postpaidPlan.setPackage_type(savePlanSharedDataMessage.getPackage_type());
        postpaidPlan.setNumber_of_days(savePlanSharedDataMessage.getNumber_of_days());
        postpaidPlan.setNo_of_users(savePlanSharedDataMessage.getNo_of_users());
        postpaidPlan.setRack_space(savePlanSharedDataMessage.getRack_space());
        postpaidPlan.setPower_consumption(savePlanSharedDataMessage.getPower_consumption());
        postpaidPlan.setNetwork_card(savePlanSharedDataMessage.getNetwork_card());
        postpaidPlan.setIp_or_ip_pool(savePlanSharedDataMessage.getIp_or_ip_pool());
        postpaidPlan.setNo_of_license(savePlanSharedDataMessage.getNo_of_license());
        postpaidPlan.setNo_of_email_user_license(savePlanSharedDataMessage.getNo_of_email_user_license());
        postpaidPlan.setNo_of_server_license(savePlanSharedDataMessage.getNo_of_server_license());
        postpaidPlan.setNo_of_user_license(savePlanSharedDataMessage.getNo_of_user_license());
        postpaidPlan.setNo_of_nodes(savePlanSharedDataMessage.getNo_of_nodes());
        postpaidPlan.setEvent_per_second(savePlanSharedDataMessage.getEvent_per_second());
        postpaidPlan.setNo_of_additional_server(savePlanSharedDataMessage.getNo_of_additional_server());
        postpaidPlan.setNo_of_additional_storage(savePlanSharedDataMessage.getNo_of_additional_storage());
        postpaidPlan.setAdditional_storage_type(savePlanSharedDataMessage.getAdditional_storage_type());
        postpaidPlan.setEps_License(savePlanSharedDataMessage.getEps_License());
        postpaidPlan.setNo_of_nodes_license(savePlanSharedDataMessage.getNo_of_nodes_license());
        postpaidPlan.setHardware_resource(savePlanSharedDataMessage.getHardware_resource());
        postpaidPlan.setMan_power(savePlanSharedDataMessage.getMan_power());
        postpaidPlan.setNo_of_domains(savePlanSharedDataMessage.getNo_of_domains());
        postpaidPlan.setSecurity_modules(savePlanSharedDataMessage.getSecurity_modules());
        postpaidPlan.setHardware_or_servers(savePlanSharedDataMessage.getHardware_or_servers());
        postpaidPlan.setCountry(savePlanSharedDataMessage.getCountry());
        postpaidPlan.setNo_of_vpn(savePlanSharedDataMessage.getNo_of_vpn());
        postpaidPlan.setDevice_throughput(savePlanSharedDataMessage.getDevice_throughput());
        postpaidPlan.setRetail(savePlanSharedDataMessage.getRetail());
        postpaidPlan.setBusinessType(savePlanSharedDataMessage.getBusinessType());
        postpaidPlan.setBasePlan(savePlanSharedDataMessage.getBasePlan());
        postpaidPlan.setTemplateId(savePlanSharedDataMessage.getTemplateId());
        postpaidPlan.setCreatedById(savePlanSharedDataMessage.getCreatedById());
        postpaidPlan.setLastModifiedById(savePlanSharedDataMessage.getLastModifiedById());
        postpaidPlan.setIsApprove(false);
        if(postpaidPlan.getStatus().equalsIgnoreCase("Active")){
            ApplicationLogger.logger.info("postpaidPlan.getPlanStatus()"+postpaidPlan.getPlanStatus());
            postpaidPlan.setPlanStatus("Approved");

        }
        postpaidPlanRepo.save(postpaidPlan);
        ApplicationLogger.logger.info("Postpaid Plan Saved Successfully");
    }

    public void updatePostPaidPlanData(UpdatePlanSharedDataMessage updatePlanSharedDataMessage) {
        ApplicationLogger.logger.info("update Postpaid Plan Started");
        PostpaidPlan postpaidPlan=postpaidPlanRepo.findById(updatePlanSharedDataMessage.getId()).orElse(null);
        if(postpaidPlan!=null) {
            ApplicationLogger.logger.info("Post paid plan found");
            postpaidPlan.setId(updatePlanSharedDataMessage.getId());
            postpaidPlan.setName(updatePlanSharedDataMessage.getName());
            postpaidPlan.setDisplayName(updatePlanSharedDataMessage.getDisplayName());
            postpaidPlan.setCode(updatePlanSharedDataMessage.getCode());
            postpaidPlan.setDesc(updatePlanSharedDataMessage.getDesc());
            postpaidPlan.setCategory(updatePlanSharedDataMessage.getCategory());
            postpaidPlan.setMaxChild(updatePlanSharedDataMessage.getMaxChild());
            postpaidPlan.setStartDate(LocalDate.parse(updatePlanSharedDataMessage.getStartDate()));
            postpaidPlan.setEndDate(LocalDate.parse(updatePlanSharedDataMessage.getEndDate()));
            postpaidPlan.setQuota(updatePlanSharedDataMessage.getQuota());
            postpaidPlan.setQuotaUnit(updatePlanSharedDataMessage.getQuotaUnit());
            postpaidPlan.setUploadQOS(updatePlanSharedDataMessage.getUploadQOS());
            postpaidPlan.setDownloadQOS(updatePlanSharedDataMessage.getDownloadQOS());
            postpaidPlan.setUploadTs(updatePlanSharedDataMessage.getUploadTs());
            postpaidPlan.setDownloadTs(updatePlanSharedDataMessage.getDownloadTs());
            postpaidPlan.setAllowOverUsage(updatePlanSharedDataMessage.getAllowOverUsage());
            postpaidPlan.setStatus(updatePlanSharedDataMessage.getStatus());
            postpaidPlan.setPlanStatus(updatePlanSharedDataMessage.getPlanStatus());
            postpaidPlan.setChildQuota(updatePlanSharedDataMessage.getChildQuota());
            postpaidPlan.setChildQuotaUnit(updatePlanSharedDataMessage.getChildQuotaUnit());
            postpaidPlan.setSlice(updatePlanSharedDataMessage.getSlice());
            postpaidPlan.setSliceUnit(updatePlanSharedDataMessage.getSliceUnit());
            postpaidPlan.setAttachedToAllHotSpots(updatePlanSharedDataMessage.getAttachedToAllHotSpots());
            postpaidPlan.setParam1(updatePlanSharedDataMessage.getParam1());
            postpaidPlan.setParam2(updatePlanSharedDataMessage.getParam2());
            postpaidPlan.setMvnoId(updatePlanSharedDataMessage.getMvnoId());
            postpaidPlan.setTaxId(updatePlanSharedDataMessage.getTaxId());
            postpaidPlan.setServiceId(updatePlanSharedDataMessage.getServiceId());
            postpaidPlan.setTimebasepolicyId(updatePlanSharedDataMessage.getTimebasepolicyId());
            postpaidPlan.setPlantype(updatePlanSharedDataMessage.getPlantype());
            postpaidPlan.setDbr(updatePlanSharedDataMessage.getDbr());
            if(postpaidPlan.getStatus().equalsIgnoreCase("Active")){
                ApplicationLogger.logger.info("postpaidPlan.getPlanStatus()"+postpaidPlan.getPlanStatus());
                postpaidPlan.setPlanStatus("Approved");

            }

            List<PostpaidPlanCharge> postpaidPlanChargeList = new ArrayList<>();
            if(updatePlanSharedDataMessage.getChargeList().size()>0){
                ApplicationLogger.logger.info("update Plan Shared Data Message.getChargeList().size()"+updatePlanSharedDataMessage.getChargeList().size());
                for(PostpaidPlanCharge postpaidPlanCharge : updatePlanSharedDataMessage.getChargeList() ){
                    PostpaidPlanCharge postpaidPlanCharge1 = new PostpaidPlanCharge();
                    postpaidPlanCharge1.setId(postpaidPlanCharge.getId());
                    postpaidPlanCharge1.setPlan(postpaidPlan);
                    Charge charge =chargeRepository.findById(postpaidPlanCharge.getChargeId()).get();
                    postpaidPlanCharge1.setCharge(charge);
                    postpaidPlanCharge1.setChargeName(charge.getName());
                    postpaidPlanCharge1.setChargeprice(postpaidPlanCharge.getChargeprice());
                    postpaidPlanCharge1.setBillingCycle(postpaidPlanCharge.getBillingCycle());
                    postpaidPlanChargeList.add(postpaidPlanCharge1);
                }
                postpaidPlan.setChargeList(postpaidPlanChargeList);
            }
            postpaidPlan.setPlanGroup(updatePlanSharedDataMessage.getPlanGroup());
            postpaidPlan.setValidity(updatePlanSharedDataMessage.getValidity());
            postpaidPlan.setSaccode(updatePlanSharedDataMessage.getSaccode());
            postpaidPlan.setMaxconcurrentsession(updatePlanSharedDataMessage.getMaxconcurrentsession());
            postpaidPlan.setQuotatime(updatePlanSharedDataMessage.getQuotatime());
            postpaidPlan.setQuotaunittime(updatePlanSharedDataMessage.getQuotaunittime());
            postpaidPlan.setQuotatype(updatePlanSharedDataMessage.getQuotatype());
            postpaidPlan.setOfferprice(updatePlanSharedDataMessage.getOfferprice());
            postpaidPlan.setQuotadid(updatePlanSharedDataMessage.getQuotadid());
            postpaidPlan.setQuotaintercom(updatePlanSharedDataMessage.getQuotaintercom());
            postpaidPlan.setIsDelete(updatePlanSharedDataMessage.getIsDelete());
            postpaidPlan.setDataCategory(updatePlanSharedDataMessage.getDataCategory());
            postpaidPlan.setTaxamount(updatePlanSharedDataMessage.getTaxamount());
            postpaidPlan.setQuotaResetInterval(updatePlanSharedDataMessage.getQuotaResetInterval());
            postpaidPlan.setMode(updatePlanSharedDataMessage.getMode());
            postpaidPlan.setUnitsOfValidity(updatePlanSharedDataMessage.getUnitsOfValidity());
            postpaidPlan.setBuId(updatePlanSharedDataMessage.getBuId());
            postpaidPlan.setNextTeamHierarchyMapping(updatePlanSharedDataMessage.getNextTeamHierarchyMapping());
            postpaidPlan.setNextStaff(updatePlanSharedDataMessage.getNextStaff());
            postpaidPlan.setNewOfferPrice(updatePlanSharedDataMessage.getNewOfferPrice());
            postpaidPlan.setAccessibility(updatePlanSharedDataMessage.getAccessibility());
            postpaidPlan.setProductId(updatePlanSharedDataMessage.getProductId());
            postpaidPlan.setProductplanmappingList(updatePlanSharedDataMessage.getProductplanmappingList());
            postpaidPlan.setInvoiceToOrg(updatePlanSharedDataMessage.getInvoiceToOrg());
            postpaidPlan.setRequiredApproval(updatePlanSharedDataMessage.getRequiredApproval());
            postpaidPlan.setBandwidth(updatePlanSharedDataMessage.getBandwidth());
            postpaidPlan.setLink_type(updatePlanSharedDataMessage.getLink_type());
            postpaidPlan.setConnection_type(updatePlanSharedDataMessage.getConnection_type());
            postpaidPlan.setDistance(updatePlanSharedDataMessage.getDistance());
            postpaidPlan.setRam(updatePlanSharedDataMessage.getRam());
            postpaidPlan.setCpu(updatePlanSharedDataMessage.getCpu());
            postpaidPlan.setStorage(updatePlanSharedDataMessage.getStorage());
            postpaidPlan.setStorage_type(updatePlanSharedDataMessage.getStorage_type());
            postpaidPlan.setAuto_backup(updatePlanSharedDataMessage.getAuto_backup());
            postpaidPlan.setCpanel(updatePlanSharedDataMessage.getCpanel());
            postpaidPlan.setLocation(updatePlanSharedDataMessage.getLocation());
            postpaidPlan.setQuantity(updatePlanSharedDataMessage.getQuantity());
            postpaidPlan.setPackage_type(updatePlanSharedDataMessage.getPackage_type());
            postpaidPlan.setNumber_of_days(updatePlanSharedDataMessage.getNumber_of_days());
            postpaidPlan.setNo_of_users(updatePlanSharedDataMessage.getNo_of_users());
            postpaidPlan.setRack_space(updatePlanSharedDataMessage.getRack_space());
            postpaidPlan.setPower_consumption(updatePlanSharedDataMessage.getPower_consumption());
            postpaidPlan.setNetwork_card(updatePlanSharedDataMessage.getNetwork_card());
            postpaidPlan.setIp_or_ip_pool(updatePlanSharedDataMessage.getIp_or_ip_pool());
            postpaidPlan.setNo_of_license(updatePlanSharedDataMessage.getNo_of_license());
            postpaidPlan.setNo_of_email_user_license(updatePlanSharedDataMessage.getNo_of_email_user_license());
            postpaidPlan.setNo_of_server_license(updatePlanSharedDataMessage.getNo_of_server_license());
            postpaidPlan.setNo_of_user_license(updatePlanSharedDataMessage.getNo_of_user_license());
            postpaidPlan.setNo_of_nodes(updatePlanSharedDataMessage.getNo_of_nodes());
            postpaidPlan.setEvent_per_second(updatePlanSharedDataMessage.getEvent_per_second());
            postpaidPlan.setNo_of_additional_server(updatePlanSharedDataMessage.getNo_of_additional_server());
            postpaidPlan.setNo_of_additional_storage(updatePlanSharedDataMessage.getNo_of_additional_storage());
            postpaidPlan.setAdditional_storage_type(updatePlanSharedDataMessage.getAdditional_storage_type());
            postpaidPlan.setEps_License(updatePlanSharedDataMessage.getEps_License());
            postpaidPlan.setNo_of_nodes_license(updatePlanSharedDataMessage.getNo_of_nodes_license());
            postpaidPlan.setHardware_resource(updatePlanSharedDataMessage.getHardware_resource());
            postpaidPlan.setMan_power(updatePlanSharedDataMessage.getMan_power());
            postpaidPlan.setNo_of_domains(updatePlanSharedDataMessage.getNo_of_domains());
            postpaidPlan.setSecurity_modules(updatePlanSharedDataMessage.getSecurity_modules());
            postpaidPlan.setHardware_or_servers(updatePlanSharedDataMessage.getHardware_or_servers());
            postpaidPlan.setCountry(updatePlanSharedDataMessage.getCountry());
            postpaidPlan.setNo_of_vpn(updatePlanSharedDataMessage.getNo_of_vpn());
            postpaidPlan.setDevice_throughput(updatePlanSharedDataMessage.getDevice_throughput());
            postpaidPlan.setRetail(updatePlanSharedDataMessage.getRetail());
            postpaidPlan.setBusinessType(updatePlanSharedDataMessage.getBusinessType());
            postpaidPlan.setBasePlan(updatePlanSharedDataMessage.getBasePlan());
            postpaidPlan.setTemplateId(updatePlanSharedDataMessage.getTemplateId());
            postpaidPlan.setCreatedById(updatePlanSharedDataMessage.getCreatedById());
            postpaidPlan.setLastModifiedById(updatePlanSharedDataMessage.getLastModifiedById());
            postpaidPlan.setIsApprove(false);
            postpaidPlanRepo.save(postpaidPlan);
            ApplicationLogger.logger.info("Postpaid Plan Updated Successfully");
        }
    }


//    public List<PostpaidPlan> getAllActiveEntities(String type, String planGroup) {
//        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
//        JPAQuery<?> query = new JPAQuery<>(entityManager);
//        QPostPaidPlanServiceAreaMapping qPostPaidPlanServiceAreaMapping = QPostPaidPlanServiceAreaMapping.postPaidPlanServiceAreaMapping;
//        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.eq(CommonConstants.ACTIVE_STATUS));
//        if (type.equalsIgnoreCase(Constants.NORMAL))
//            booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(Constants.NORMAL));
//        else if (type.equalsIgnoreCase(Constants.SPECIAL))
//            booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(Constants.SPECIAL));
//
//        if (planGroup != null && !"".equals(planGroup) && !Constants.PLAN_GROUP_ALL.equalsIgnoreCase(planGroup))
//            booleanExpression = booleanExpression.and(qPostpaidPlan.planGroup.eq(planGroup));
//
//        if (getLoggedInUserId() != 1) {
//            List<Integer> serviceIDs = getLoggedInUser().getServiceAreaIdList();
//            if(serviceIDs!=null && serviceIDs.size()>0)
//                booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(query.select(qPostPaidPlanServiceAreaMapping.planId).from(qPostPaidPlanServiceAreaMapping).where(qPostPaidPlanServiceAreaMapping.serviceId.in(serviceIDs))));
//        }
//        booleanExpression = booleanExpression.and(qPostpaidPlan.status.notEqualsIgnoreCase("INACTIVE"));
//        if (getBUIdsFromCurrentStaff().size() != 0)
//            booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()))));
//        if (getLoggedInUserPartnerId() != 1) {
//            List<Integer> planIds = new ArrayList<>();
//
//            Partner partner = partnerRepository.findById(getLoggedInUserPartnerId()).orElse(null);
//            if(partner!=null)
//            {
//
//                QPriceBookPlanDetail qPriceBookPlanDetail = QPriceBookPlanDetail.priceBookPlanDetail;
//                BooleanExpression expression =null;// qPriceBookPlanDetail.isNotNull().and(qPriceBookPlanDetail.priceBook.eq(partner.getPriceBookId()));
//                List<PriceBookPlanDetail> list = (List<PriceBookPlanDetail>) priceBookPlanDtlRepository.findAll(expression);
//                for (PriceBookPlanDetail plan : list) {
//                    if (plan.getPostpaidPlan() != null) {
//                        planIds.add(plan.getPostpaidPlan().getId());
//                    }
//                }
//            }
//
//            if(partner!=null && partner.getPriceBookId()!=null && partner.getPriceBookId().getIsAllPlanSelected()!=null && partner.getPriceBookId().getIsAllPlanSelected())
//            {
//                List<PostpaidPlanPojo> plans=getPlanListByServiceArea(getLoggedInUser().getServiceAreaIdList(), type, Constants.PLAN_GROUP_ALL,null, null, null,null);
//                List<Integer> planIds1=plans.stream().map(x->x.getId()).collect(Collectors.toList());
//                if(planIds1!=null && !planIds1.isEmpty())
//                    planIds.addAll(planIds1);
//            }
//
//            booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));
//        }
//
//        Iterable<PostpaidPlan> postpaidPlanList = postpaidPlanRepo.findAll(booleanExpression);
//
//        for (PostpaidPlan plan : postpaidPlanList) {
//            plan.setServiceName(serviceRepository.findById(plan.getServiceId().longValue()).get().getServiceName());
//        }
//
//        BusinessUnit businessUnit = new BusinessUnit();
//        List<PostpaidPlan> postpaidPlanList1 = new ArrayList<>();
//        if (getBUIdsFromCurrentStaff().size() == 1) {
//            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
//        }
//        if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
//            postpaidPlanList1 = IterableUtils.toList(postpaidPlanList).stream().filter(postpaidPlan -> postpaidPlan.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || postpaidPlan.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() &&
//                    (Objects.isNull(postpaidPlan.getBusinessType()) || (CommonConstants.RETAIL).equalsIgnoreCase(postpaidPlan.getBusinessType()))).collect(Collectors.toList());
//        } else if ((CommonConstants.ON_DEMAND).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
//            postpaidPlanList1 = IterableUtils.toList(postpaidPlanList).stream().filter(postpaidPlan -> postpaidPlan.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || postpaidPlan.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() &&
//                    ((CommonConstants.ENTERPRISE).equalsIgnoreCase(postpaidPlan.getBusinessType()))).collect(Collectors.toList());
//        }
//        return postpaidPlanList1;
//    }



    public List<PostpaidPlanPojo> getPlanListByServiceArea(List<Integer> serviceAreaId, String planmode, String planGroup, String planCategory, Integer validity, String unitsOfValidity, Integer custId) {
ApplicationLogger.logger.info("get Plan List By Service Area: " + serviceAreaId);
        List<PostPaidPlanServiceAreaMapping> list = planServiceAreaRepo.findAllByServiceIdIn(serviceAreaId);
        List<Integer> planIds = list.stream().map(PostPaidPlanServiceAreaMapping::getPlanId).collect(Collectors.toList());

        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.eq(CommonConstants.ACTIVE_STATUS));

        booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));
ApplicationLogger.logger.info("planIds: " + planIds + " booleanExpression: " + booleanExpression);
        if (!planmode.equals(Constants.ALL)) booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(planmode));
        if (planGroup != null && !"".equals(planGroup) && !Constants.PLAN_GROUP_ALL.equalsIgnoreCase(planGroup))
            booleanExpression = booleanExpression.and(qPostpaidPlan.planGroup.equalsIgnoreCase(planGroup));

        if (getBUIdsFromCurrentStaff().size() != 0)
            ApplicationLogger.logger.info("get BUIds From Current Staff: " + getBUIdsFromCurrentStaff());
            booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()))));

        booleanExpression = booleanExpression.and(qPostpaidPlan.endDate.eq(LocalDate.now()).or(qPostpaidPlan.endDate.after(LocalDate.now())));
        if (validity != null && !(unitsOfValidity.isEmpty())) {
            ApplicationLogger.logger.info("validity: " + validity + " unitsOfValidity: " + unitsOfValidity);
            booleanExpression = booleanExpression.and(qPostpaidPlan.unitsOfValidity.equalsIgnoreCase(unitsOfValidity)).and(qPostpaidPlan.validity.eq(validity.doubleValue()));
        }
        List<PostpaidPlan> planList = (List<PostpaidPlan>) postpaidPlanRepo.findAll(booleanExpression);
        List<PostpaidPlanPojo> result = planList.stream().map(data -> {
            try {
                ApplicationLogger.logger.info("data: " + data);
                return postpaidPlanMapper.domainToDTO(data, new CycleAvoidingMappingContext());
            } catch (NoSuchFieldException e) {
                ApplicationLogger.logger.error("error while Mapping Postpaid plan"+e.getMessage());
            }
            return null;
        }).collect(Collectors.toList());
        BusinessUnit businessUnit = new BusinessUnit();
        List<PostpaidPlanPojo> pojoList = new ArrayList<>();
        if (getBUIdsFromCurrentStaff().size() == 1) {
            ApplicationLogger.logger.info("get BUIds From Current Staff: " + getBUIdsFromCurrentStaff());
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
            pojoList = result.stream().filter(postpaidPlanPojo -> (Objects.isNull(postpaidPlanPojo.getBusinessType()) || (CommonConstants.RETAIL).equalsIgnoreCase(postpaidPlanPojo.getBusinessType()))).collect(Collectors.toList());
        } else if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)) {
            pojoList = result.stream().filter(postpaidPlanPojo -> (CommonConstants.ENTERPRISE).equalsIgnoreCase(postpaidPlanPojo.getBusinessType())).collect(Collectors.toList());
        }
        ApplicationLogger.logger.info("pojo List: " + pojoList);
        return pojoList;
    }


    public Integer getMvnoIdFromCurrentStaff() {
       ApplicationLogger.logger.info("MVNO - getMvnoIdFromCurrentStaff");
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if(securityContext.getAuthentication().getPrincipal() != null)
                    mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            ApplicationLogger.logger.info("mvnoId: " + mvnoId);
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }


    public LoggedInUser getLoggedInUser() {
   ApplicationLogger.logger.info("get Logged In User");
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getLoggedInUser" + e.getMessage(), e);
            user = null;
        }
        ApplicationLogger.logger.info("User logged in: {}", user);
        return user;
    }


    public int getLoggedInUserPartnerId() {
        ApplicationLogger.logger.info("get Logged In User Partner Id");
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
        ApplicationLogger.logger.info("User logged in: {}", partnerId);
        return partnerId;
    }


    public List<java.lang.Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = new ArrayList<java.lang.Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }

    public int getLoggedInUserId() {
        ApplicationLogger.logger.info("get Logged In User Id");
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getLoggedInUserId" + e.getMessage(), e);
            loggedInUserId = -1;
        }
        ApplicationLogger.logger.info("User logged in: {}", loggedInUserId);
        return loggedInUserId;
    }
    public void assignPlanToServiceArea(List<PostPaidPlanServiceAreaMapping> mappingList) {
//        if (mappingList == null || mappingList.isEmpty()) {
//            ApplicationLogger.logger.warn("No plan-service area mappings received to assign.");
//        }
        LocalDateTime now = LocalDateTime.now();
        for (PostPaidPlanServiceAreaMapping mapping : mappingList) {
            if (mapping.getCreatedOn() == null) {
                mapping.setCreatedOn(now);
            }
        }
        List<PostPaidPlanServiceAreaMapping> savedMappings = postPaidPlanServiceAreaMappingRepo.saveAll(mappingList);
    }
}
