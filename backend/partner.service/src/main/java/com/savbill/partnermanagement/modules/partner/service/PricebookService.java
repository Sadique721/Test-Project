package com.savbill.partnermanagement.modules.partner.service;

import com.savbill.partnermanagement.modules.Plan.repository.PostpaidPlanRepo;
import com.savbill.partnermanagement.modules.PlanGroup.domain.PlanGroup;
import com.savbill.partnermanagement.modules.PlanGroup.repocitory.PlanGroupRepository;
import com.savbill.partnermanagement.modules.partner.entity.PriceBook1;
import com.savbill.partnermanagement.modules.partner.entity.PriceBookPlanDetail;
import com.savbill.partnermanagement.modules.partner.entity.PriceBookSlabDetails;
import com.savbill.partnermanagement.modules.partner.entity.ServiceCommission;
import com.savbill.partnermanagement.modules.partner.repository.PriceBookRepository1;
import com.savbill.partnermanagement.rabbitmq.product.SavePricebookSharedMessage;
import com.savbill.partnermanagement.rabbitmq.product.UpdatePricebookSharedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PricebookService {

    @Autowired
    PriceBookRepository1 priceBookRepository;
    @Autowired
    PlanGroupRepository planGroupRepository;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;
    public void save(SavePricebookSharedMessage message) {
        PriceBook1 priceBook=new PriceBook1();
        priceBook.setId(message.getId());
        priceBook.setBookname(message.getBookname());
        priceBook.setDescription(message.getDescription());
        if(message.getValidfrom()!=null) {
            priceBook.setValidfrom(LocalDateTime.parse(message.getValidfrom()));
        } if(message.getValidto()!=null) {
            priceBook.setValidto(LocalDateTime.parse(message.getValidto()));
        }
         priceBook.setStatus(message.getStatus());
         priceBook.setDescription(message.getDescription());
         priceBook.setCommission_on(message.getCommission_on());
         priceBook.setIsAllPlanSelected(message.getIsAllPlanSelected());
         priceBook.setIsAllPlanGroupSelected(message.getIsAllPlanGroupSelected());
         priceBook.setRevenueSharePercentage(message.getRevenueSharePercentage());
         priceBook.setIsDeleted(message.getIsDeleted());
         priceBook.setMvnoId(message.getMvnoId());
         priceBook.setAgrPercentage(message.getAgrPercentage());
         priceBook.setTdsPercentage(message.getTdsPercentage());
         priceBook.setBuId(message.getBuId());
         priceBook.setRevenueType(message.getRevenueType());
        List<PriceBookSlabDetails> pricebooklist=new ArrayList<>();
        for(PriceBookSlabDetails pricebookslablist :message.getPriceBookSlabDetailsList()){
            PriceBookSlabDetails priceBookSlabDetails=new PriceBookSlabDetails();
            priceBookSlabDetails.setId(pricebookslablist.getId());
            priceBookSlabDetails.setIsDeleted(pricebookslablist.getIsDeleted());
            priceBookSlabDetails.setFromRange(pricebookslablist.getFromRange());
            priceBookSlabDetails.setToRange(pricebookslablist.getToRange());
            priceBookSlabDetails.setCommissionAmount(pricebookslablist.getCommissionAmount());
            priceBookSlabDetails.setPriceBook(priceBook);
            pricebooklist.add(priceBookSlabDetails);

        }
        List<PriceBookPlanDetail> priceBookPlanDetails=new ArrayList<>();
        for(PriceBookPlanDetail priceBookPlanDetail:message.getPriceBookPlanDetailList() ){
            PriceBookPlanDetail priceBookPlanDetailslist=new PriceBookPlanDetail();
            priceBookPlanDetailslist.setId(priceBookPlanDetail.getId());
            priceBookPlanDetailslist.setOfferprice(priceBookPlanDetail.getOfferprice());
            priceBookPlanDetailslist.setPartnerofficeprice(priceBookPlanDetail.getPartnerofficeprice());
            priceBookPlanDetailslist.setRevsharen(priceBookPlanDetail.getRevsharen());
            priceBookPlanDetailslist.setRegistration(priceBookPlanDetail.getRegistration());
            priceBookPlanDetailslist.setRenewal(priceBookPlanDetail.getRenewal());
            priceBookPlanDetailslist.setIsDeleted(priceBookPlanDetail.getIsDeleted());
            if(priceBookPlanDetail.getPostpaidplanid()!=null){
                priceBookPlanDetailslist.setPostpaidPlan(postpaidPlanRepo.findById(Math.toIntExact(priceBookPlanDetail.getPostpaidplanid())).orElse(null));
            }
            if(priceBookPlanDetail.getPlanGroupId()!=null) {
                PlanGroup planGroup = planGroupRepository.findById(priceBookPlanDetail.getPlanGroupId()).orElse(null);
                priceBookPlanDetailslist.setPlanGroup(planGroup);
            }
            priceBookPlanDetailslist.setRevenueSharePercentage(priceBookPlanDetail.getRevenueSharePercentage()) ;
            priceBookPlanDetailslist.setIsTaxIncluded(priceBookPlanDetail.getIsTaxIncluded());
            priceBookPlanDetailslist.setPriceBook(priceBook);
            priceBookPlanDetails.add(priceBookPlanDetailslist);
        }
        List<ServiceCommission> serviceCommissionList=new ArrayList<>();

        for(ServiceCommission comission: message.getServiceCommissionList()){
            ServiceCommission serviceCommission=new ServiceCommission();
            serviceCommission.setId(comission.getId());
            serviceCommission.setServiceId(comission.getServiceId());
            serviceCommission.setServiceName(comission.getServiceName());
            serviceCommission.setRevenue_share_percentage(comission.getRevenue_share_percentage());
            serviceCommission.setRoyaltyPercentage(comission.getRoyaltyPercentage());
            serviceCommission.setIsDeleted(comission.getIsDeleted());
            serviceCommission.setPriceBook( priceBook);
            serviceCommissionList.add(serviceCommission);

        }
        priceBook.setPriceBookPlanDetailList(priceBookPlanDetails);
        priceBook.setServiceCommissionList(serviceCommissionList);
        priceBook.setPriceBookSlabDetailsList(pricebooklist);
        priceBook.setCreatedById(message.getCreatedBYId());
        priceBook.setLastModifiedById(message.getCreatedBYId());
        priceBook.setCreatedByName(message.getLastModifiedByname());
        priceBookRepository.save(priceBook);

    }

    public void update(UpdatePricebookSharedMessage message) {

        PriceBook1 priceBook=priceBookRepository.findById(message.getId()).orElse(null);
        if(priceBook!=null) {
            priceBook.setId(message.getId());
            priceBook.setBookname(message.getBookname());
            priceBook.setDescription(message.getDescription());
            if (message.getValidfrom() != null) {
                priceBook.setValidfrom(LocalDateTime.parse(message.getValidfrom()));
            }
            if (message.getValidto() != null) {
                priceBook.setValidto(LocalDateTime.parse(message.getValidto()));
            }
            priceBook.setStatus(message.getStatus());
            priceBook.setDescription(message.getDescription());
            priceBook.setCommission_on(message.getCommission_on());
            priceBook.setIsAllPlanSelected(message.getIsAllPlanSelected());
            priceBook.setIsAllPlanGroupSelected(message.getIsAllPlanGroupSelected());
            priceBook.setRevenueSharePercentage(message.getRevenueSharePercentage());
            priceBook.setIsDeleted(message.getIsDeleted());
            priceBook.setMvnoId(message.getMvnoId());
            priceBook.setAgrPercentage(message.getAgrPercentage());
            priceBook.setTdsPercentage(message.getTdsPercentage());
            priceBook.setBuId(message.getBuId());
            priceBook.setRevenueType(message.getRevenueType());
            List<PriceBookSlabDetails> pricebooklist = new ArrayList<>();
            for (PriceBookSlabDetails pricebookslablist : message.getPriceBookSlabDetailsList()) {
                PriceBookSlabDetails priceBookSlabDetails = new PriceBookSlabDetails();
                priceBookSlabDetails.setId(pricebookslablist.getId());
                priceBookSlabDetails.setIsDeleted(pricebookslablist.getIsDeleted());
                priceBookSlabDetails.setFromRange(pricebookslablist.getFromRange());
                priceBookSlabDetails.setToRange(pricebookslablist.getToRange());
                priceBookSlabDetails.setCommissionAmount(pricebookslablist.getCommissionAmount());
                priceBookSlabDetails.setPriceBook(priceBook);
                pricebooklist.add(priceBookSlabDetails);

            }
            List<PriceBookPlanDetail> priceBookPlanDetails = new ArrayList<>();
            for (PriceBookPlanDetail priceBookPlanDetail : message.getPriceBookPlanDetailList()) {
                PriceBookPlanDetail priceBookPlanDetailslist = new PriceBookPlanDetail();
                priceBookPlanDetailslist.setId(priceBookPlanDetail.getId());
                priceBookPlanDetailslist.setOfferprice(priceBookPlanDetail.getOfferprice());
                priceBookPlanDetailslist.setPartnerofficeprice(priceBookPlanDetail.getPartnerofficeprice());
                priceBookPlanDetailslist.setRevsharen(priceBookPlanDetail.getRevsharen());
                priceBookPlanDetailslist.setRegistration(priceBookPlanDetail.getRegistration());
                priceBookPlanDetailslist.setRenewal(priceBookPlanDetail.getRenewal());
                priceBookPlanDetailslist.setIsDeleted(priceBookPlanDetail.getIsDeleted());
                if (priceBookPlanDetail.getPostpaidplanid() != null) {
                    priceBookPlanDetailslist.setPostpaidPlan(postpaidPlanRepo.findById(Math.toIntExact(priceBookPlanDetail.getPostpaidplanid())).orElse(null));
                }
                if (priceBookPlanDetail.getPlanGroupId() != null) {
                    PlanGroup planGroup = planGroupRepository.findById(priceBookPlanDetail.getPlanGroupId()).orElse(null);
                    priceBookPlanDetailslist.setPlanGroup(planGroup);
                }
                priceBookPlanDetailslist.setRevenueSharePercentage(priceBookPlanDetail.getRevenueSharePercentage());
                priceBookPlanDetailslist.setIsTaxIncluded(priceBookPlanDetail.getIsTaxIncluded());
                priceBookPlanDetailslist.setPriceBook(priceBook);
                priceBookPlanDetails.add(priceBookPlanDetailslist);
            }
            List<ServiceCommission> serviceCommissionList = new ArrayList<>();

            for (ServiceCommission comission : message.getServiceCommissionList()) {
                ServiceCommission serviceCommission = new ServiceCommission();
                serviceCommission.setId(comission.getId());
                serviceCommission.setServiceId(comission.getServiceId());
                serviceCommission.setServiceName(comission.getServiceName());
                serviceCommission.setRevenue_share_percentage(comission.getRevenue_share_percentage());
                serviceCommission.setRoyaltyPercentage(comission.getRoyaltyPercentage());
                serviceCommission.setIsDeleted(comission.getIsDeleted());
                serviceCommission.setPriceBook(priceBook);
                serviceCommissionList.add(serviceCommission);

            }
            priceBook.setPriceBookPlanDetailList(priceBookPlanDetails);
            priceBook.setServiceCommissionList(serviceCommissionList);
            priceBook.setPriceBookSlabDetailsList(pricebooklist);
            priceBook.setCreatedById(message.getCreatedBYId());
            priceBook.setLastModifiedById(message.getCreatedBYId());
            priceBook.setCreatedByName(message.getLastModifiedByname());
            priceBookRepository.save(priceBook);
        }
    }
}
