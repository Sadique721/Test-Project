package com.savbill.revenuemanagement.productmanagement.Plan.service;

import com.savbill.revenuemanagement.core.entity.partner.PostpaidPlanCharge;
import com.savbill.revenuemanagement.core.repository.partner.PostpaidPlanChargeRepo;
import com.savbill.revenuemanagement.core.util.DateTimeUtil;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostPaidPlanServiceAreaMapping;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.mapper.PostpaidPlanMapper;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostPaidPlanServiceAreaMappingRepo;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.SavePlanSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.UpdatePlanSharedDataMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostPaidPlanService {
    @Autowired
    PostpaidPlanMapper postpaidPlanMapper;

    @Autowired
    PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepo;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    private static Log log = LogFactory.getLog(PostPaidPlanService.class);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    @Transactional
    public void savePostpaidPlan(SavePlanSharedDataMessage message){
        try {

            PostpaidPlan postpaidPlan = new PostpaidPlan();

            postpaidPlan.setId(message.getId());
            postpaidPlan.setServiceId(message.getServiceId());
            postpaidPlan.setPlanStatus(message.getPlanStatus());
            postpaidPlan.setName(message.getName());
            postpaidPlan.setBuId(message.getBuId());
            postpaidPlan.setMvnoId(message.getMvnoId());
            postpaidPlan.setIsDelete(message.getIsDelete());
            postpaidPlan.setStatus(message.getStatus());
            postpaidPlan.setPlantype(message.getPlantype());
            postpaidPlan.setMode(message.getMode());
            postpaidPlan.setCategory(message.getCategory());

            postpaidPlanRepo.save(postpaidPlan);
        }catch (Exception e){
          log.error("Error While Creating Postpaid Plan, "+e.getMessage());
        }

    }


@Transactional
    public void updatePostPaidPlan(UpdatePlanSharedDataMessage message){
        try {
            PostpaidPlan postpaidPlan = new PostpaidPlan();

            postpaidPlan = postpaidPlanRepo.findById(message.getId()).orElse(null);

//            postpaidPlan.setServiceId(message.getServiceId());
//            postpaidPlan.setPlanStatus(message.getPlanStatus());
//            postpaidPlan.setName(message.getName());
//            postpaidPlan.setBuId(message.getBuId());
//            postpaidPlan.setMvnoId(message.getMvnoId());
//            postpaidPlan.setIsDelete(message.getIsDelete());
//            postpaidPlan.setStatus(message.getStatus());
//            postpaidPlan.setPlantype(message.getPlantype());
//            postpaidPlan.setMode(message.getMode());
//            postpaidPlan.setCategory(message.getCategory());

            postpaidPlanRepo.save(postpaidPlan);
        }catch (Exception e){
           log.error("error while Updating Postpaid plan"+e.getMessage());
        }

    }

    public void savePostPaidPlanData(SavePlanSharedDataMessage savePlanSharedDataMessage) {
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
//        postpaidPlan.setChargeList(savePlanSharedDataMessage.getChargeList());
        List<PostpaidPlanCharge> postpaidPlanChargeList = new ArrayList<>();
        if(savePlanSharedDataMessage.getChargeList().size()>0){
            for(PostpaidPlanCharge postpaidPlanCharge : savePlanSharedDataMessage.getChargeList() ){
                PostpaidPlanCharge postpaidPlanCharge1 = new PostpaidPlanCharge();
                postpaidPlanCharge1.setId(postpaidPlanCharge.getId());
                postpaidPlanCharge1.setPlan(postpaidPlan);
                Charge charge = null;
                if (postpaidPlanCharge.getChargeId()!=null) {
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
        postpaidPlan.setQospolicy(savePlanSharedDataMessage.getQospolicy_id());
        postpaidPlan.setQospolicy_name(savePlanSharedDataMessage.getQospolicy_name());
//        postpaidPlan.setRadiusprofile(savePlanSharedDataMessage.getRadiusprofile());
        postpaidPlan.setIsDelete(savePlanSharedDataMessage.getIsDelete());
        postpaidPlan.setDataCategory(savePlanSharedDataMessage.getDataCategory());
        postpaidPlan.setTaxamount(savePlanSharedDataMessage.getTaxamount());
//        postpaidPlan.setServiceAreaNameList(savePlanSharedDataMessage.getServiceAreaNameList());
        if(savePlanSharedDataMessage.getPostPaidPlanServiceAreaMappingList().size()>0){
            List<PostPaidPlanServiceAreaMapping> planServiceAreaMappingList = new ArrayList<>();
            for(PostPaidPlanServiceAreaMapping entity :savePlanSharedDataMessage.getPostPaidPlanServiceAreaMappingList()){
                PostPaidPlanServiceAreaMapping postPaidPlanServiceAreaMapping = new PostPaidPlanServiceAreaMapping();
                postPaidPlanServiceAreaMapping.setId(entity.getId());
                postPaidPlanServiceAreaMapping.setPlanId(entity.getPlanId());
                postPaidPlanServiceAreaMapping.setServiceId(entity.getServiceId());
                postPaidPlanServiceAreaMapping.setCreatedOn(DateTimeUtil.getLocaldateTimefromString(entity.getCreatedOnString()));
                postPaidPlanServiceAreaMapping.setLastmodifiedOn(DateTimeUtil.getLocaldateTimefromString(entity.getLastmodifiedOnString()));
                planServiceAreaMappingList.add(postPaidPlanServiceAreaMapping);
            }
            postPaidPlanServiceAreaMappingRepo.saveAll(planServiceAreaMappingList);
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
        postpaidPlan.setProductplanmappingList(savePlanSharedDataMessage.getProductplanmappingList());
        postpaidPlan.setInvoiceToOrg(savePlanSharedDataMessage.getInvoiceToOrg());
        postpaidPlan.setRequiredApproval(savePlanSharedDataMessage.getRequiredApproval());
       // postpaidPlan.setPlanCasMappingList(savePlanSharedDataMessage.getPlanCasMappingList());
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
//        postpaidPlan.setPlanQosMappingEntities(savePlanSharedDataMessage.getPlanQosMappingEntities());
        postpaidPlan.setCreatedById(savePlanSharedDataMessage.getCreatedById());
        postpaidPlan.setLastModifiedById(savePlanSharedDataMessage.getLastModifiedById());
        postpaidPlan.setIsApprove(false);
        if(postpaidPlan.getStatus().equalsIgnoreCase("Active")){
            postpaidPlan.setPlanStatus("Approved");

        }
        postpaidPlanRepo.save(postpaidPlan);
    }

    public void updatePostPaidPlanData(UpdatePlanSharedDataMessage updatePlanSharedDataMessage) {
        PostpaidPlan postpaidPlan=postpaidPlanRepo.findById(updatePlanSharedDataMessage.getId()).orElse(null);
        if(postpaidPlan!=null) {
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
                postpaidPlan.setPlanStatus("Approved");

            }

            List<PostpaidPlanCharge> postpaidPlanCharges =  postpaidPlan.getChargeList();

            List<PostpaidPlanCharge> postpaidPlanChargeList = new ArrayList<>();
            if(updatePlanSharedDataMessage.getChargeList().size()>0){
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
                postpaidPlan.getChargeList().clear();
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
            postpaidPlan.setQospolicy(updatePlanSharedDataMessage.getQospolicy_id());
            postpaidPlan.setQospolicy_name(updatePlanSharedDataMessage.getQospolicy_name());
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

            if (postpaidPlanCharges!=null && !postpaidPlanCharges.isEmpty()) {
                for (PostpaidPlanCharge postpaidPlanCharge : postpaidPlanCharges) {
                    postpaidPlanChargeRepo.deleteById(postpaidPlanCharge.getId());
                }
            }
        }

    }

    public void assignPlanToServiceArea(List<PostPaidPlanServiceAreaMapping> mappingList) {
        if (mappingList == null || mappingList.isEmpty()) {
            log.warn("No plan-service area mappings received to assign.");
        }
        LocalDateTime now = LocalDateTime.now();
        for (PostPaidPlanServiceAreaMapping mapping : mappingList) {
            if (mapping.getCreatedOn() == null) {
                mapping.setCreatedOn(now);
            }
            if (mapping.getLastmodifiedOn() == null) {
                mapping.setLastmodifiedOn(now);
            }
        }
        List<PostPaidPlanServiceAreaMapping> savedMappings = postPaidPlanServiceAreaMappingRepo.saveAll(mappingList);
        log.info("Saved {} plan-service area mappings received via Kafka."+ savedMappings.size());
    }
}
