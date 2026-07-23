package com.savbill.revenuemanagement.core.service.prepaid;

import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.dto.commission.PartnerCommissionLevel;
import com.savbill.revenuemanagement.core.entity.customers.CustPlanMappping;
import com.savbill.revenuemanagement.core.entity.customers.CustomerChargeHistory;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.inventory.CustomerInventoryMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedger;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedgerDtls;
import com.savbill.revenuemanagement.core.entity.partner.*;
import com.savbill.revenuemanagement.core.entity.partner.*;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.repository.customer.CustomerLedgerRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CustomerLedgerDtlsRepository;
import com.savbill.revenuemanagement.core.repository.partner.*;
import com.savbill.revenuemanagement.core.repository.partner.*;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.kafka.KafkaConstant;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory.PlanGroupMappingRepository;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.ServiceRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.Tax;
import com.savbill.revenuemanagement.productmanagement.Tax.repository.TaxRepository;

import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.PartnerAmountMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartnerCommissionService {

    private static final Logger logger = LoggerFactory.getLogger(PartnerCommissionService.class);


    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    CreditDocRepository creditDocRepository;
    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    TempPartnerLedgerDetailsRepository tempPartnerLedgerDetailsRepository;

    @Autowired
    PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    CustomerLedgerDtlsRepository customerLedgerDtlsRepository;

    @Autowired
    CustomerLedgerRepository customerLedgerRepository;

    @Autowired
    PartnerCommissionRepository partnerCommissionRepository;

    @Autowired
    PartnerLedgerRepository partnerLedgerRepository;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    PlanGroupMappingRepository planGroupMappingRepository;

    @Autowired
    TaxRepository taxRepository;

    @Autowired
    StaffUserRepository staffUserRepository;
    //@Autowired
    //MessageSender messageSender;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    ServiceRepository serviceRepository;


    @Autowired
    KafkaMessageSender kafkaMessageSender;

    public void addPartnerCommission(List<CustomerChargeHistory> chargeHistories,List<CustPlanMappping> custPlanMapppings,DebitDocument debitDocument,Customers customers,Integer staffId,StaffUser staffUser)
    {
        try {
            if(staffId!=null)
            {
                List<Integer> mappingIds=custPlanMapppings.stream().filter(x->!x.getIsInvoiceCreated()).map(x->x.getId()).collect(Collectors.toList());
                List<CustomerChargeHistory> chargeHistories1=chargeHistories.stream().filter(x->mappingIds.contains(x.getCustPlanMapppingId())).collect(Collectors.toList());
                if(staffUser==null)
                    staffUser=staffUserRepository.findById(staffId).orElse(null);
                Integer paymentStatusForFranCustomer = checkAndUpdatePaymentAdjustmentAgainstInvoiceAmount(chargeHistories1, custPlanMapppings, debitDocument, customers,staffUser);
                chargeHistories1 = chargeHistories1.stream().filter(x -> (x.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_RECURRING) || x.getChargeType().equalsIgnoreCase(Constants.ADVANCE))).collect(Collectors.toList());
                Double offerPrice1 = chargeHistories1.stream().mapToDouble(x -> (x.getChargeAmount() + x.getTaxAmount() - x.getDiscount())).sum();
                partnerCommissionForPrepaidCustomerCreation(debitDocument.getTotalamount(), offerPrice1, chargeHistories1, customers, debitDocument.getId().longValue(), paymentStatusForFranCustomer,staffUser.getId(), staffUser.getFirstname(),custPlanMapppings);
            }
        }catch (Exception e){
            logger.error("Partner Commission Error :- "+e.getMessage());
        }
    }

    public Integer checkAndUpdatePaymentAdjustmentAgainstInvoiceAmount(List<CustomerChargeHistory> items,List<CustPlanMappping> custPlanMapppings,DebitDocument document, Customers customers,StaffUser staffUser) {
        try {
            String createFrom=null;
            if(items!=null && items.size()>0)
            {
                if(custPlanMapppings!=null && !custPlanMapppings.isEmpty())
                    createFrom=custPlanMapppings.get(0).getPurchaseFrom();
            }

            if(document.getAdjustedAmount()==null)
                document.setAdjustedAmount(0.0);

            if(document.getAdjustedAmount()==null)
                document.setAdjustedAmount(0.0);

            Double totalInvoiceAmount=document.getTotalamount()-document.getAdjustedAmount();

            if(createFrom!=null && createFrom.equalsIgnoreCase("admin") && document.getTotalamount().doubleValue() == document.getAdjustedAmount().doubleValue())
            {
                return 1;
            }
            else if(createFrom!=null && !createFrom.equalsIgnoreCase("admin") && customers.getLcoId()!=null)
            {
                return 1;
            }
            else if(createFrom!=null && !createFrom.equalsIgnoreCase("admin") && document.getTotalamount().doubleValue() == document.getAdjustedAmount().doubleValue())
            {

                updatePartnerBalanceAgainstInvoiceAmount(customers,document.getAdjustedAmount(),document.getId().longValue());
                return 1;
            }

            Partner partner=partnerRepository.findById(customers.getPartner()).orElse(null);

            if ((customers != null && customers.getPartner()!=Constants.DEFAULT_PARTNER_ID && customers.getLcoId()==null && (createFrom!=null && !createFrom.equalsIgnoreCase("admin")) && (partner.getBalance()>0 && partner.getBalance() >= totalInvoiceAmount))) {
                if (adjustPaymentAgainstInvoiceAmount(customers,totalInvoiceAmount,document, staffUser.getId(),staffUser.getFirstname(),staffUser.getMvnoId())) {
                    updatePartnerBalanceAgainstInvoiceAmount(customers, totalInvoiceAmount,document.getId().longValue());
                    return 1;
                }
            }
            else if(customers != null && customers.getPartner()!=Constants.DEFAULT_PARTNER_ID && customers.getLcoId()==null && ((createFrom!=null && !createFrom.equalsIgnoreCase("admin"))) && (partner.getBalance() < totalInvoiceAmount))
            {
                if (adjustPaymentAgainstInvoiceAmount(customers,totalInvoiceAmount,document, staffUser.getId(), staffUser.getFirstname(),staffUser.getMvnoId())) {
                    updatePartnerBalanceAgainstInvoiceAmount(customers, totalInvoiceAmount,document.getId().longValue());
                    return 2;
                }
            }
            else if (customers != null && customers.getIs_from_pwc() && customers.getLcoId()==null && ((createFrom!=null && !createFrom.equalsIgnoreCase("admin"))) && (partner.getBalance()>0 && partner.getBalance() < totalInvoiceAmount))
                return 2;
            else if(customers != null && customers.getIs_from_pwc() && customers.getLcoId()==null && staffUser.getPartnerid().intValue()==Constants.DEFAULT_PARTNER_ID)
                return 3;
            else if(customers != null && !customers.getIs_from_pwc() && customers.getPartner()!=Constants.DEFAULT_PARTNER_ID && staffUser.getPartnerid().intValue()==Constants.DEFAULT_PARTNER_ID)
                return 4;
            return 5;
        }catch (Exception e)
        {
            return 5;
        }
    }


    public Integer paymentAdjustmentAgainstDirectChargeInvoice(DebitDocument document, Customers customers,Integer staffId) {
        try {
            if(staffId!=null)
            {
                StaffUser staffUser = staffUserRepository.findById(staffId).orElse(null);
                Partner partner=partnerRepository.findById(customers.getPartner()).orElse(null);
                if(document.getAdjustedAmount()==null)
                    document.setAdjustedAmount(0.0);

                if(document.getAdjustedAmount()==null)
                    document.setAdjustedAmount(0.0);

                Double totalInvoiceAmount=document.getTotalamount()-document.getAdjustedAmount();

                if(staffUser!=null && staffUser.getPartnerid()==Constants.DEFAULT_PARTNER_ID && document.getTotalamount().doubleValue() == document.getAdjustedAmount().doubleValue())
                {
                    return 1;
                }
                else if(staffUser!=null && staffUser.getPartnerid()!=Constants.DEFAULT_PARTNER_ID && customers.getLcoId()!=null)
                {
                    return 1;
                }
                else if(staffUser!=null && staffUser.getPartnerid()!=Constants.DEFAULT_PARTNER_ID && document.getTotalamount().doubleValue() == document.getAdjustedAmount().doubleValue())
                {

                    updatePartnerBalanceAgainstInvoiceAmount(customers,document.getAdjustedAmount(),document.getId().longValue());
                    return 1;
                }
                else if ((customers != null && customers.getPartner()!=Constants.DEFAULT_PARTNER_ID && customers.getLcoId()==null && (staffUser!=null && staffUser.getPartnerid()!=Constants.DEFAULT_PARTNER_ID) && (partner.getBalance()>0 && partner.getBalance() >= totalInvoiceAmount))) {
                    if (adjustPaymentAgainstInvoiceAmount(customers,totalInvoiceAmount,document, staffUser.getId(),staffUser.getFirstname(),staffUser.getMvnoId())) {
                        updatePartnerBalanceAgainstInvoiceAmount(customers, totalInvoiceAmount,document.getId().longValue());
                        return 1;
                    }
                }
                else if(customers != null && customers.getPartner()!=Constants.DEFAULT_PARTNER_ID && customers.getLcoId()==null && ((staffUser!=null && staffUser.getPartnerid()!=Constants.DEFAULT_PARTNER_ID)) && (partner.getBalance() < totalInvoiceAmount))
                {
                    if (adjustPaymentAgainstInvoiceAmount(customers,totalInvoiceAmount,document, staffUser.getId(), staffUser.getFirstname(),staffUser.getMvnoId())) {
                        updatePartnerBalanceAgainstInvoiceAmount(customers, totalInvoiceAmount,document.getId().longValue());
                        return 2;
                    }
                }
                else if (customers != null && customers.getIs_from_pwc() && customers.getLcoId()==null && ((staffUser!=null && staffUser.getPartnerid()!=Constants.DEFAULT_PARTNER_ID)) && (partner.getBalance()>0 && partner.getBalance() < totalInvoiceAmount))
                    return 2;
                else if(customers != null && customers.getIs_from_pwc() && customers.getLcoId()==null && staffUser.getPartnerid().intValue()==Constants.DEFAULT_PARTNER_ID)
                    return 3;
                else if(customers != null && !customers.getIs_from_pwc() && customers.getPartner()!=Constants.DEFAULT_PARTNER_ID && staffUser.getPartnerid().intValue()==Constants.DEFAULT_PARTNER_ID)
                    return 4;
            }
            return 5;
        }catch (Exception e){return 5;}
    }


    public void partnerCommissionForPrepaidCustomerCreation(Double grossOfferPrice, Double offerPrice, List<CustomerChargeHistory> list, Customers customers, Long invoiceId, Integer paymentStatusForFranCustomer, Integer staffId, String staffName, List<CustPlanMappping> custPlanMapppings)
    {
        if (customers.getPartner() != null && customers.getPartner() != Constants.DEFAULT_PARTNER_ID) {
            Partner partner = partnerRepository.findById(customers.getPartner()).orElse(null);
            if(list!=null && !list.isEmpty() && custPlanMapppings!=null && !custPlanMapppings.isEmpty())
            {
                Partner finalPartner = partner;
                list.stream().forEach(history->{
                    List<CustPlanMappping> services=custPlanMapppings.stream().filter(custPackRel->custPackRel.getId().equals(history.getCustPlanMapppingId())).collect(Collectors.toList());
                    if(!services.isEmpty()) {
                        history.setServiceName(services.get(0).getService());
                        Integer serviceId=serviceRepository.findServiceNameByServiceIdAndMvnoId(services.get(0).getService(), finalPartner.getMvnoId().longValue());
                        history.setServiceId(serviceId);
                        if(services.get(0).getPlanGroup()!=null)
                            history.setPlanGroupName(services.get(0).getPlanGroup().getPlanGroupName());
                    }
                });
            }
            List<String> planIdList = list.stream().filter(x->x.getPlanId()!=null).map(x -> x.getPlanId().toString()).distinct().collect(Collectors.toList());
            for(String planId:planIdList){

                List<PartnerCommissionLevel> partnerCommissionLevels=new ArrayList<>();
                if(partner.getPartnerType().equalsIgnoreCase("Franchise") && partner.getParentPartner()!=null)
                {
                    List<CustomerChargeHistory> chargesForSelectedPlan = list.stream().filter(data -> data.getPlanId()!=null && data.getPlanId().toString().equalsIgnoreCase(planId)).collect(Collectors.toList());
                    Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> (d.getChargeAmount() - d.getDiscount())).sum();

                    ClientService clientService=clientServiceRepository.findByNameAndMvnoId("COMMISSION_LEVEL",partner.getMvnoId());
                    if(clientService!=null)
                    {
                        Integer commissionLevel=Integer.parseInt(clientService.getValue());
                        if(commissionLevel!=null && commissionLevel>0)
                        {
                            while(partner!=null && partnerCommissionLevels.size()<(commissionLevel+1))
                            {
                                partnerCommissionLevels.add(new PartnerCommissionLevel(partner,0.0,0.0));
                                partner=partner.getParentPartner();
                            }
                            Collections.reverse(partnerCommissionLevels);
                            if(partnerCommissionLevels.size()>1)
                            {
                                for(Integer i=0;i<partnerCommissionLevels.size();i++)
                                {
                                    if(i==0)
                                        baseOfferPrice=getPartnerCommission(baseOfferPrice,partnerCommissionLevels.get(i).getPartner(),planId,true,list.get(0).getPlanGroupId(),partnerCommissionLevels.get(i));
                                    else
                                        baseOfferPrice=getPartnerCommission(baseOfferPrice,partnerCommissionLevels.get(i).getPartner(),planId,false,list.get(0).getPlanGroupId(),partnerCommissionLevels.get(i));

                                    partnerCommissionLevels.get(i).setCommission(baseOfferPrice);
                                    if(i>0)
                                        partnerCommissionLevels.get(i-1).setCommission(partnerCommissionLevels.get(i-1).getCommission()-baseOfferPrice);
                                }
                            }
                        }
                    }
                }


                if(partnerCommissionLevels!=null && partnerCommissionLevels.isEmpty())
                {
                    if (partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN))
                    {
                        List<PriceBookPlanDetail> priceBookPlanDetailList = partner.getPriceBookId().getPriceBookPlanDetailList();
                        PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                        List<PriceBookPlanDetail> tmpBookList = priceBookPlanDetailList.stream().filter(x -> x.getPostpaidPlan()!=null && x.getPostpaidPlan().getId().equals(Integer.parseInt(planId))).collect(Collectors.toList());
                        if(!list.isEmpty() && list.get(0).getPlanGroupId()!=null)
                            tmpBookList = priceBookPlanDetailList.stream().filter(x -> x.getPostpaidPlan()==null && x.getPlanGroup()!=null && x.getPlanGroup().getPlanGroupId().equals(list.get(0).getPlanGroupId())).collect(Collectors.toList());


                        Double revenueSharePercentage=0.0;
                        List<CustomerChargeHistory> chargesForSelectedPlan=new ArrayList<>();
                        if ((tmpBookList != null && tmpBookList.size() > 0) || partner.getPriceBookId().getIsAllPlanSelected() || partner.getPriceBookId().getIsAllPlanGroupSelected()) {
                            chargesForSelectedPlan = list.stream().filter(data -> data.getPlanId()!=null && data.getPlanId().toString().equalsIgnoreCase(planId)).collect(Collectors.toList());
                            Double totalTax = chargesForSelectedPlan.stream().mapToDouble(d -> d.getTaxAmount()).sum();
                            Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> (d.getChargeAmount() - d.getDiscount())).sum();
                            Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                            Double basePriceExcludeAGR = baseOfferPrice - agr_tax;
                            Double operatorCommission=basePriceExcludeAGR;
                            Double partner_commission = null;
                            Double partnerTax=0d;
                            Double tds_tax = null;
                            List<PriceBookSlabDetails> priceBookSlabDetailsList = null;
                            Long customerCount = partner.getTotalCustomerCount();
                            customerCount = customerCount != null ? customerCount : 0;

                            if(partner.getPriceBookId().getIsAllPlanSelected() || partner.getPriceBookId().getIsAllPlanGroupSelected())
                            {
                                if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)){
                                    partner_commission = (basePriceExcludeAGR * Double.parseDouble(partner.getPriceBookId().getRevenueSharePercentage().toString())) / 100.00;
                                    revenueSharePercentage=Double.parseDouble(partner.getPriceBookId().getRevenueSharePercentage().toString());
                                }
                                else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)) {
                                    Long finalCustomerCount = customerCount;
                                    priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                                    if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                        partner_commission = priceBookSlabDetailsList.get(0).getCommissionAmount();
                                }
                            }
                            if(tmpBookList!=null && tmpBookList.size()>0)
                            {
                                if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)){
                                    partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenueSharePercentage())) / 100.00;
                                    revenueSharePercentage=Double.parseDouble(tmpBookList.get(0).getRevenueSharePercentage());
                                }
                                else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)) {
                                    Long finalCustomerCount = customerCount;
                                    priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                                    if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                        partner_commission = priceBookSlabDetailsList.get(0).getCommissionAmount();
                                }
                            }

                            operatorCommission-=partner_commission;

                            Tax tax = getTax(Integer.parseInt(partner.getTaxid().toString()));
                            partnerTax = getTaxAmount(tax, partner_commission);

                            tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                            partner_commission = partner_commission + partnerTax - tds_tax;
                            addPartnerLedgerAndLedgerDetailAgainstCommission(grossOfferPrice,baseOfferPrice + totalTax, partner_commission, totalTax, agr_tax, tds_tax, null, customers, partner, plan, invoiceId, partnerTax,paymentStatusForFranCustomer,staffId,0.0,0.0,0.0,0.0,null,operatorCommission,chargesForSelectedPlan.get(0).getServiceId(),chargesForSelectedPlan.get(0).getServiceName(),list.get(0).getPlanGroupId(),chargesForSelectedPlan.get(0).getPlanGroupName(),Double.parseDouble(partner.getPriceBookId().getAgrPercentage()),Double.parseDouble(partner.getPriceBookId().getTdsPercentage()),revenueSharePercentage);
                        }
                    } else {
                        List<ServiceCommission> serviceCommissionList = partner.getPriceBookId().getServiceCommissionList();
                        PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                        List<ServiceCommission> tmpBookList = serviceCommissionList.stream().filter(x -> x.getServiceId().equals(plan.getServiceId().longValue())).collect(Collectors.toList());
                        Double revenueSharePercentage=0.0;
                        List<CustomerChargeHistory> chargesForSelectedPlan=new ArrayList<>();
                        if (tmpBookList != null && tmpBookList.size() > 0) {
                            chargesForSelectedPlan = list.stream().filter(data -> data.getPlanId().toString().equalsIgnoreCase(planId)).collect(Collectors.toList());
                            Double totalTax = chargesForSelectedPlan.stream().mapToDouble(d -> d.getTaxAmount()).sum();
                            Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> (d.getChargeAmount() - d.getDiscount())).sum();
                            Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                            Double basePriceExcludeAGR = baseOfferPrice - agr_tax;
                            Double operatorCommission=basePriceExcludeAGR;

                            Double partner_commission = null;
                            Double royaltyCommission=0d;
                            Double partnerTax=0d;
                            Double tds_tax = null;
                            Double royaltyBasePrice=0.0;

                            partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                            revenueSharePercentage=Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString());

                            operatorCommission-=partner_commission;


                            Double baseOfferPriceForRoyalty = chargesForSelectedPlan.stream().filter(x->x.getIsRoyaltyApply()).mapToDouble(d -> (d.getChargeAmount()-d.getDiscount())).sum();
                            if(baseOfferPriceForRoyalty!=null && baseOfferPriceForRoyalty>0) {
                                Double agr_taxForRoyalty = (baseOfferPriceForRoyalty * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                                Double basePriceExcludeAGRForRoyalty = baseOfferPriceForRoyalty - agr_taxForRoyalty;
                                //basePriceExcludeAGRForRoyalty=(basePriceExcludeAGRForRoyalty * Double.parseDouble(tmpBookList.get(0).getRoyaltyPercentage().toString())) / 100.00;
                                royaltyBasePrice=basePriceExcludeAGRForRoyalty;
                                if(tmpBookList.get(0).getRoyaltyPercentage()!=0)
                                    royaltyCommission = (basePriceExcludeAGRForRoyalty * Double.parseDouble(String.valueOf(tmpBookList.get(0).getRoyaltyPercentage()))) / 100.00;
                            }

                            if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                                partner_commission -= royaltyCommission;
                            else
                                partner_commission -= royaltyCommission;

                            Tax tax = getTax(Integer.parseInt(partner.getTaxid().toString()));
                            partnerTax = getTaxAmount(tax, partner_commission);

                            tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                            partner_commission = partner_commission + partnerTax - tds_tax;
                            addPartnerLedgerAndLedgerDetailAgainstCommission(grossOfferPrice,baseOfferPrice + totalTax, partner_commission, totalTax, agr_tax, tds_tax, royaltyCommission, customers, partner, plan, invoiceId, partnerTax,paymentStatusForFranCustomer,staffId,royaltyBasePrice,0.0,0.0,0.0,null,operatorCommission,chargesForSelectedPlan.get(0).getServiceId(),chargesForSelectedPlan.get(0).getServiceName(),chargesForSelectedPlan.get(0).getPlanGroupId(),chargesForSelectedPlan.get(0).getPlanGroupName(),Double.parseDouble(partner.getPriceBookId().getAgrPercentage()),Double.parseDouble(partner.getPriceBookId().getTdsPercentage()),revenueSharePercentage);
                        }
                    }
                }
                else
                {
                    List<CustomerChargeHistory> charges = list.stream().filter(data -> data.getPlanId()!=null && data.getPlanId().toString().equalsIgnoreCase(planId)).collect(Collectors.toList());
                    Double basePrice = charges.stream().mapToDouble(d -> (d.getChargeAmount() - d.getDiscount())).sum();
                    Double parentAgr=basePrice * Double.parseDouble(partnerCommissionLevels.get(0).getPartner().getPriceBookId().getAgrPercentage())/100.0d;
                    for(PartnerCommissionLevel level:partnerCommissionLevels)
                    {
                        partner=level.getPartner();
                        if (partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)) {
                            List<PriceBookPlanDetail> priceBookPlanDetailList = partner.getPriceBookId().getPriceBookPlanDetailList();
                            PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                            List<PriceBookPlanDetail> tmpBookList = priceBookPlanDetailList.stream().filter(x -> x.getPostpaidPlan()!=null && x.getPostpaidPlan().getId().equals(Integer.parseInt(planId))).collect(Collectors.toList());
                            if(!list.isEmpty() && list.get(0).getPlanGroupId()!=null)
                                tmpBookList = priceBookPlanDetailList.stream().filter(x -> x.getPostpaidPlan()==null && x.getPlanGroup()!=null && x.getPlanGroup().getPlanGroupId().equals(list.get(0).getPlanGroupId())).collect(Collectors.toList());


                            Double revenueSharePercentage=0.0;
                            List<CustomerChargeHistory> chargesForSelectedPlan=new ArrayList<>();
                            if ((tmpBookList != null && tmpBookList.size() > 0) || partner.getPriceBookId().getIsAllPlanSelected() || partner.getPriceBookId().getIsAllPlanGroupSelected()) {
                                chargesForSelectedPlan = list.stream().filter(data -> data.getPlanId()!=null && data.getPlanId().toString().equalsIgnoreCase(planId)).collect(Collectors.toList());
                                Double totalTax = chargesForSelectedPlan.stream().mapToDouble(d -> d.getTaxAmount()).sum();
                                Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> (d.getChargeAmount() - d.getDiscount())).sum();
                                Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                                Double basePriceExcludeAGR = baseOfferPrice - agr_tax;
                                Double operatorCommission=basePriceExcludeAGR;
                                Double partner_commission = null;
                                Double partnerTax=0d;
                                Double tds_tax = null;
                                List<PriceBookSlabDetails> priceBookSlabDetailsList = null;
                                Long customerCount = partner.getTotalCustomerCount();
                                customerCount = customerCount != null ? customerCount : 0;

                                if(partner.getPriceBookId().getIsAllPlanSelected() || partner.getPriceBookId().getIsAllPlanGroupSelected())
                                {
                                    if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN))
                                        partner_commission = level.getCommission();
                                    else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)) {
                                        Long finalCustomerCount = customerCount;
                                        priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                                        if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                            partner_commission = level.getCommission();
                                    }
                                }
                                if(tmpBookList!=null && tmpBookList.size()>0)
                                {
                                    if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN))
                                        partner_commission = level.getCommission();
                                    else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)) {
                                        Long finalCustomerCount = customerCount;
                                        priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                                        if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                            partner_commission = level.getCommission();
                                    }
                                }

                                Tax tax = getTax(Integer.parseInt(partner.getTaxid().toString()));
                                partnerTax = getTaxAmount(tax, partner_commission);

                                tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                                partner_commission = partner_commission + partnerTax - tds_tax;

                                Double totalSharedCommission=partnerCommissionLevels.stream().mapToDouble(x->x.getCommission()).sum();
                                operatorCommission-=totalSharedCommission;
                                Double parentSharedCommission=0.0d;
                                for(int i=0;i<partnerCommissionLevels.size()-1;i++)
                                    parentSharedCommission+=partnerCommissionLevels.get(i).getCommission();

                                Double childSharedCommission=totalSharedCommission-parentSharedCommission;
                                addPartnerLedgerAndLedgerDetailAgainstCommission(grossOfferPrice,baseOfferPrice + totalTax, partner_commission, totalTax, agr_tax, tds_tax, null, customers, partner, plan, invoiceId, partnerTax,paymentStatusForFranCustomer,staffId,0.0,totalSharedCommission,parentSharedCommission,childSharedCommission,parentAgr,operatorCommission,chargesForSelectedPlan.get(0).getServiceId(),chargesForSelectedPlan.get(0).getServiceName(),chargesForSelectedPlan.get(0).getPlanGroupId(),chargesForSelectedPlan.get(0).getPlanGroupName(),Double.parseDouble(partner.getPriceBookId().getAgrPercentage()),Double.parseDouble(partner.getPriceBookId().getTdsPercentage()),level.getCommissionPercentage());
                            }
                        } else {
                            List<ServiceCommission> serviceCommissionList = partner.getPriceBookId().getServiceCommissionList();
                            PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                            List<ServiceCommission> tmpBookList = serviceCommissionList.stream().filter(x -> x.getServiceId().equals(plan.getServiceId().longValue())).collect(Collectors.toList());
                            Double revenueSharePercentage=0.0;
                            List<CustomerChargeHistory> chargesForSelectedPlan=new ArrayList<>();
                            if (tmpBookList != null && tmpBookList.size() > 0) {
                                chargesForSelectedPlan = list.stream().filter(data -> data.getPlanId().toString().equalsIgnoreCase(planId)).collect(Collectors.toList());
                                Double totalTax = chargesForSelectedPlan.stream().mapToDouble(d -> d.getTaxAmount()).sum();
                                Double baseOfferPrice = chargesForSelectedPlan.stream().mapToDouble(d -> (d.getChargeAmount() - d.getDiscount())).sum();
                                Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                                Double basePriceExcludeAGR = baseOfferPrice - agr_tax;
                                Double operatorCommission=basePriceExcludeAGR;

                                Double partner_commission = null;
                                Double royaltyCommission=0d;
                                Double partnerTax=0d;
                                Double tds_tax = null;
                                Double royaltyBasePrice=0.0;

                                partner_commission = level.getCommission();


                                Double baseOfferPriceForRoyalty = chargesForSelectedPlan.stream().filter(x->x.getIsRoyaltyApply()).mapToDouble(d -> (d.getChargeAmount()-d.getDiscount())).sum();
                                if(baseOfferPriceForRoyalty!=null && baseOfferPriceForRoyalty>0) {
                                    Double agr_taxForRoyalty = (baseOfferPriceForRoyalty * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                                    Double basePriceExcludeAGRForRoyalty = baseOfferPriceForRoyalty - agr_taxForRoyalty;
                                    //basePriceExcludeAGRForRoyalty=(basePriceExcludeAGRForRoyalty * Double.parseDouble(tmpBookList.get(0).getRoyaltyPercentage().toString())) / 100.00;
                                    revenueSharePercentage=Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString());

                                    royaltyBasePrice=basePriceExcludeAGRForRoyalty;
                                    if(tmpBookList.get(0).getRoyaltyPercentage()!=0)
                                        royaltyCommission = (basePriceExcludeAGRForRoyalty * Double.parseDouble(String.valueOf(tmpBookList.get(0).getRoyaltyPercentage()))) / 100.00;
                                }

                                if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                                    partner_commission -= royaltyCommission;
                                else
                                    partner_commission -= royaltyCommission;

                                Tax tax = getTax(Integer.parseInt(partner.getTaxid().toString()));
                                partnerTax = getTaxAmount(tax, partner_commission);

                                tds_tax = (partner_commission * Double.parseDouble(partner.getPriceBookId().getTdsPercentage())) / 100.00;
                                partner_commission = partner_commission + partnerTax - tds_tax;

                                Double totalSharedCommission=partnerCommissionLevels.stream().mapToDouble(x->x.getCommission()).sum();
                                operatorCommission-=totalSharedCommission;
                                Double parentSharedCommission=0.0d;
                                for(int i=0;i<partnerCommissionLevels.size()-1;i++)
                                    parentSharedCommission+=partnerCommissionLevels.get(i).getCommission();

                                Double childSharedCommission=partnerCommissionLevels.stream().mapToDouble(x->x.getCommission()).sum()-parentSharedCommission;
                                addPartnerLedgerAndLedgerDetailAgainstCommission(grossOfferPrice,baseOfferPrice + totalTax, partner_commission, totalTax, agr_tax, tds_tax, royaltyCommission, customers, partner, plan, invoiceId, partnerTax,paymentStatusForFranCustomer,staffId,royaltyBasePrice,totalSharedCommission,parentSharedCommission,childSharedCommission,parentAgr,operatorCommission,chargesForSelectedPlan.get(0).getServiceId(),chargesForSelectedPlan.get(0).getServiceName(),list.get(0).getPlanGroupId(),chargesForSelectedPlan.get(0).getPlanGroupName(),Double.parseDouble(partner.getPriceBookId().getAgrPercentage()),Double.parseDouble(partner.getPriceBookId().getTdsPercentage()),level.getCommissionPercentage());
                            }
                        }
                    }
                }
            }
        }
    }

    private Double getPartnerCommission(Double baseOfferPrice, Partner partner, String planId,Boolean flag,Integer planGroupId,PartnerCommissionLevel partnerCommissionLevel)
    {
        Double partner_commission=0d;
        if (partner != null && partner.getId() != Constants.DEFAULT_PARTNER_ID)
        {
            if (partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)) {
                List<PriceBookPlanDetail> priceBookPlanDetailList = partner.getPriceBookId().getPriceBookPlanDetailList();
                PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                List<PriceBookPlanDetail> tmpBookList = priceBookPlanDetailList.stream().filter(x -> x.getPostpaidPlan()!=null && x.getPostpaidPlan().getId().equals(Integer.parseInt(planId))).collect(Collectors.toList());
                if(planGroupId!=null)
                    tmpBookList = priceBookPlanDetailList.stream().filter(x -> x.getPostpaidPlan()==null && x.getPlanGroup()!=null && x.getPlanGroup().getPlanGroupId().equals(planGroupId)).collect(Collectors.toList());


                if ((tmpBookList != null && tmpBookList.size() > 0) || partner.getPriceBookId().getIsAllPlanSelected() || partner.getPriceBookId().getIsAllPlanGroupSelected()) {
                    Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                    if(!flag)
                        agr_tax=0.0;
                    Double basePriceExcludeAGR = baseOfferPrice - agr_tax;
                    List<PriceBookSlabDetails> priceBookSlabDetailsList = null;
                    Long customerCount = partner.getTotalCustomerCount();
                    customerCount = customerCount != null ? customerCount : 0;

                    if(partner.getPriceBookId().getIsAllPlanSelected() || partner.getPriceBookId().getIsAllPlanGroupSelected())
                    {
                        if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)){
                            partner_commission = (basePriceExcludeAGR * Double.parseDouble(partner.getPriceBookId().getRevenueSharePercentage().toString())) / 100.00;
                            partnerCommissionLevel.setCommissionPercentage(Double.parseDouble(partner.getPriceBookId().getRevenueSharePercentage().toString()));
                        }
                        else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)) {
                            Long finalCustomerCount = customerCount;
                            priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                            if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                partner_commission = priceBookSlabDetailsList.get(0).getCommissionAmount();
                        }
                    }
                    if(tmpBookList!=null && tmpBookList.size()>0)
                    {
                        if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Percentage") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)){
                            partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenueSharePercentage())) / 100.00;
                            partnerCommissionLevel.setCommissionPercentage(Double.parseDouble(tmpBookList.get(0).getRevenueSharePercentage()));
                        }
                        else if (partner.getPriceBookId().getRevenueType() != null && partner.getPriceBookId().getRevenueType().equalsIgnoreCase("Slab") && partner.getPriceBookId().getCommission_on().equalsIgnoreCase(Constants.COMMISSION_ON_PLAN)) {
                            Long finalCustomerCount = customerCount;
                            priceBookSlabDetailsList = partner.getPriceBookId().getPriceBookSlabDetailsList().stream().filter(data -> data.getFromRange() <= finalCustomerCount && finalCustomerCount <= data.getToRange()).collect(Collectors.toList());
                            if (priceBookSlabDetailsList != null && !priceBookSlabDetailsList.isEmpty())
                                partner_commission = priceBookSlabDetailsList.get(0).getCommissionAmount();
                        }
                    }
                }
            } else {
                List<ServiceCommission> serviceCommissionList = partner.getPriceBookId().getServiceCommissionList();
                PostpaidPlan plan = postpaidPlanRepo.findById(Integer.parseInt(planId)).get();
                List<ServiceCommission> tmpBookList = serviceCommissionList.stream().filter(x -> x.getServiceId().equals(plan.getServiceId().longValue())).collect(Collectors.toList());
                if (tmpBookList != null && tmpBookList.size() > 0) {
                    Double agr_tax = (baseOfferPrice * Double.parseDouble(partner.getPriceBookId().getAgrPercentage())) / 100.00;
                    if(!flag)
                        agr_tax=0.0;
                    Double basePriceExcludeAGR = baseOfferPrice - agr_tax;
                    partner_commission = (basePriceExcludeAGR * Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString())) / 100.00;
                    partnerCommissionLevel.setCommissionPercentage(Double.parseDouble(tmpBookList.get(0).getRevenue_share_percentage().toString()));
                }
            }
        }
        return partner_commission;
    }

    private void addPartnerLedgerAndLedgerDetailAgainstCommission(Double grossOfferPrice,Double offerPrice, Double partner_commission, Double totalTax, Double agr_tax, Double tds_tax, Double royalty, Customers customers, Partner partner, PostpaidPlan plan, Long invoiceId, Double partnerTax,Integer paymentStatusForFranCustomer,Integer staffId,Double royaltyBasePrice,Double totalSharedCommission,Double parentSharedCommission,Double childSharedCommission,Double parentAgr,Double operatorCommission,Integer serviceId,String serviceName,Integer planGroup,String planGroupName,Double agrTaxPercentage,Double tdsTaxPercentage,Double revenueSharePercentage) {

        Optional<DebitDocument> document=debitDocRepository.findById(invoiceId.intValue());
        if(document.isPresent()) {
            DecimalFormat df = new DecimalFormat("0.00");
            DebitDocument debitDocument=document.get();
            Double amount=debitDocument.getTotalamount();
            if(debitDocument.getAdjustedAmount()!=null)
                amount=debitDocument.getTotalamount()-debitDocument.getAdjustedAmount();
            amount=Double.parseDouble(df.format(amount));
            if ((customers.getLcoId() != null && (partner.getBalance() + (partner.getCredit() - partner.getCreditConsume()) - partner.getCommrelvalue()) >=partner_commission) || (paymentStatusForFranCustomer == 1 && customers.getLcoId() == null && customers.getIs_from_pwc() && (amount==0.0d || partner.getBalance() > 0 || (partner.getBalance() == 0 && partner.getCreditConsume().intValue() == 0))) || paymentStatusForFranCustomer.longValue()==1) {
                PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
                partnerLedgerDetails.setCustid(customers.getId());
                partnerLedgerDetails.setOfferprice(offerPrice);
                partnerLedgerDetails.setTax(totalTax);
                if(parentAgr!=null)
                    partnerLedgerDetails.setAgr_amount(parentAgr);
                else
                    partnerLedgerDetails.setAgr_amount(agr_tax);
                partnerLedgerDetails.setTds_amount(tds_tax);
                partnerLedgerDetails.setCommission(partner_commission);
                partnerLedgerDetails.setAmount(0.00);
                partnerLedgerDetails.setPartner(partner);
                partnerLedgerDetails.setDebitDocId(invoiceId);
                partnerLedgerDetails.setGrossOfferPrice(grossOfferPrice);
                partnerLedgerDetails.setRoyalty(royalty);
                if (customers.getLcoId() == null)
                    partnerLedgerDetails.setTranstype(Constants.TRANS_TYPE_CREDIT);
                else {
                    partnerLedgerDetails.setTranstype(Constants.TRANS_TYPE_DEBIT);
                    partnerLedgerDetails.setAmount(partner_commission);
                    partnerLedgerDetails.setCommission(0.0d);
                }
                partnerLedgerDetails.setTotalSharedCommission(totalSharedCommission);
                partnerLedgerDetails.setParentSharedCommission(parentSharedCommission);
                partnerLedgerDetails.setChildSharedCommission(childSharedCommission);
                partnerLedgerDetails.setOperatorCommission(operatorCommission);
                partnerLedgerDetails.setTranscategory(Constants.TRANS_CATEGORY_COMMISSION);
                partnerLedgerDetails.setDescription("Commission against creation of customer = " + customers.getFirstname() + " For PlanID = " + plan.getId());
                partnerLedgerDetails.setCreateDate(LocalDateTime.now());
                partnerLedgerDetails.setPartnerTax(partnerTax);
                partnerLedgerDetails.setRoyaltyBasePrice(royaltyBasePrice);

                partnerLedgerDetails.setServiceId(serviceId);
                partnerLedgerDetails.setServiceName(serviceName);
                partnerLedgerDetails.setPlanGroupId(planGroup);
                partnerLedgerDetails.setPlanGroupName(planGroupName);
                partnerLedgerDetails.setAgrPercentage(agrTaxPercentage);
                partnerLedgerDetails.setTdsPercentage(tdsTaxPercentage);
                partnerLedgerDetails.setRevenueSharePercentage(revenueSharePercentage);
                if(plan!=null) {
                    partnerLedgerDetails.setPlanname(plan.getName());
                    partnerLedgerDetails.setPlanid(plan.getId().toString());
                }
                partnerLedgerDetails.setPartnerTaxId(partner.getTaxid());
                partnerLedgerDetailsRepository.save(partnerLedgerDetails);
                addPartnerLedgerEntryAgainstCommission(partner_commission, partner, customers);

            } else if (paymentStatusForFranCustomer == 5 || paymentStatusForFranCustomer == 4 || paymentStatusForFranCustomer == 3 || paymentStatusForFranCustomer == 2 || (customers.getLcoId() != null && (partner.getBalance() + (partner.getCredit() - partner.getCreditConsume()) - partner.getCommrelvalue()) < partner_commission) || (paymentStatusForFranCustomer == 1 && customers.getLcoId() == null && customers.getIs_from_pwc() && (partner.getBalance() == 0 && partner.getCreditConsume().intValue() > 0))) {
                TempPartnerLedgerDetail partnerLedgerDetails = new TempPartnerLedgerDetail();
                partnerLedgerDetails.setCustid(customers.getId());
                partnerLedgerDetails.setOfferprice(offerPrice);
                partnerLedgerDetails.setTax(totalTax);
                partnerLedgerDetails.setInvoice_id(invoiceId.toString());
                if(parentAgr!=null)
                    partnerLedgerDetails.setAgr_amount(parentAgr);
                else
                    partnerLedgerDetails.setAgr_amount(agr_tax);
                partnerLedgerDetails.setTds_amount(tds_tax);
                partnerLedgerDetails.setCommission(partner_commission);
                partnerLedgerDetails.setGrossOfferPrice(grossOfferPrice);
                partnerLedgerDetails.setAmount(0.00);
                partnerLedgerDetails.setPartner(partner);
                partnerLedgerDetails.setRoyalty(royalty);
                if (customers.getLcoId() == null)
                    partnerLedgerDetails.setTranstype(Constants.TRANS_TYPE_CREDIT);
                else {
                    partnerLedgerDetails.setTranstype(Constants.TRANS_TYPE_DEBIT);
                    partnerLedgerDetails.setAmount(partner_commission);
                }

                partnerLedgerDetails.setTotalSharedCommission(totalSharedCommission);
                partnerLedgerDetails.setParentSharedCommission(parentSharedCommission);
                partnerLedgerDetails.setChildSharedCommission(childSharedCommission);
                partnerLedgerDetails.setOperatorCommission(operatorCommission);
                partnerLedgerDetails.setTranscategory(Constants.TRANS_CATEGORY_COMMISSION);
                partnerLedgerDetails.setDebitDocId(invoiceId.longValue());
                partnerLedgerDetails.setDescription("Commission against creation of customer = " + customers.getFirstname() + " For PlanID = " + plan.getId());
                partnerLedgerDetails.setCREATE_DATE(LocalDateTime.now());
                partnerLedgerDetails.setPartnerTax(partnerTax);
                if (paymentStatusForFranCustomer == 2)
                    partnerLedgerDetails.setPaymentStatus(2);
                else if (paymentStatusForFranCustomer == 3)
                    partnerLedgerDetails.setPaymentStatus(3);
                else if (paymentStatusForFranCustomer == 4)
                    partnerLedgerDetails.setPaymentStatus(4);
                else if (paymentStatusForFranCustomer == 1 && customers.getLcoId() == null && customers.getIs_from_pwc() && (partner.getBalance() == 0 && partner.getCreditConsume().intValue() > 0))
                    partnerLedgerDetails.setPaymentStatus(5);
                else
                    partnerLedgerDetails.setPaymentStatus(6);

                partnerLedgerDetails.setRoyaltyBasePrice(royaltyBasePrice);
                partnerLedgerDetails.setStaffUserId(staffId);

                partnerLedgerDetails.setServiceId(serviceId);
                partnerLedgerDetails.setServiceName(serviceName);
                partnerLedgerDetails.setPlanGroupId(planGroup);
                partnerLedgerDetails.setPlanGroupName(planGroupName);
                partnerLedgerDetails.setAgrPercentage(agrTaxPercentage);
                partnerLedgerDetails.setTdsPercentage(tdsTaxPercentage);
                partnerLedgerDetails.setRevenueSharePercentage(revenueSharePercentage);
                partnerLedgerDetails.setPartnerTaxId(partner.getTaxid());
                if(plan!=null) {
                    partnerLedgerDetails.setPlanname(plan.getName());
                    partnerLedgerDetails.setPlanid(plan.getId().toString());
                }
                tempPartnerLedgerDetailsRepository.save(partnerLedgerDetails);
            }
        }
    }


    public void updatePartnerBalanceAgainstInvoiceAmount(Customers customers, Double totalInvoiceAmount, Long invoiceId)
    {
        Partner partner = partnerRepository.findById(customers.getPartner()).orElse(null);
        if (partner != null && partner.getId() != Constants.DEFAULT_PARTNER_ID) {
            Double amount=totalInvoiceAmount;
            if(customers.getLcoId()==null)
            {
                if (partner.getBalance() >= totalInvoiceAmount) {
                    amount=totalInvoiceAmount;
                    partner.setBalance(partner.getBalance() - totalInvoiceAmount);
                    partner=partnerRepository.save(partner);
                    addPartnerLedgerDetailAgainstInvoiceAmount(totalInvoiceAmount, customers, partner,invoiceId);
                }
                else if ((partner.getBalance() - totalInvoiceAmount) < 0) {
                    if(partner.getBalance() > 0) {
                        amount=partner.getBalance();
                        Double creditConsume = partner.getCreditConsume() + (totalInvoiceAmount - partner.getBalance());
                        partner.setCreditConsume(creditConsume);
                        partner.setBalance(0d);
                        addPartnerLedgerDetailAgainstInvoiceAmount(amount, customers, partner,invoiceId);
                    } else {
                        Double creditConsume = partner.getCreditConsume() + totalInvoiceAmount;
                        partner.setCreditConsume(creditConsume);
                    }
                    partnerRepository.save(partner);
                }
                PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
                partnerAmountMessage.setPartnerId(partner.getId());
                partnerAmountMessage.setComrelval(partner.getCommrelvalue());
                partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
                partnerAmountMessage.setBalance(partner.getBalance());
                partnerAmountMessage.setCredit(partner.getCredit());
                partnerAmountMessage.setRenewcust_count(partner.getRenewCustomerCount().intValue());
                partnerAmountMessage.setNewCustomer_count(partner.getNewCustomerCount().intValue());
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
                kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
            }
        }
    }


    public void updatePartnerBalanceAgainstInvoiceAmount(Partner partner, Double voucherBatchTotalAmount,String planId,String batchName)
    {
        if (partner != null && partner.getId() != Constants.DEFAULT_PARTNER_ID) {
            Double amount=voucherBatchTotalAmount;
            if (partner.getBalance() >= voucherBatchTotalAmount) {
                amount=voucherBatchTotalAmount;
                partner.setBalance(partner.getBalance() - voucherBatchTotalAmount);
                partner=partnerRepository.save(partner);
                addPartnerLedgerDetailAgainstInvoiceAmount(voucherBatchTotalAmount, partner,planId,batchName);
            }
            else if ((partner.getBalance() - voucherBatchTotalAmount) < 0) {
                if(partner.getBalance() > 0) {
                    amount=partner.getBalance();
                    Double creditConsume = partner.getCreditConsume() + (voucherBatchTotalAmount - partner.getBalance());
                    partner.setCreditConsume(creditConsume);
                    partner.setBalance(0d);
                    addPartnerLedgerDetailAgainstInvoiceAmount(amount, partner,planId,batchName);
                } else {
                    Double creditConsume = partner.getCreditConsume() + voucherBatchTotalAmount;
                    partner.setCreditConsume(creditConsume);
                }
                partnerRepository.save(partner);
            }
            PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
            partnerAmountMessage.setPartnerId(partner.getId());
            partnerAmountMessage.setComrelval(partner.getCommrelvalue());
            partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
            partnerAmountMessage.setBalance(partner.getBalance());
            partnerAmountMessage.setCredit(partner.getCredit());
            partnerAmountMessage.setRenewcust_count(partner.getRenewCustomerCount().intValue());
            partnerAmountMessage.setNewCustomer_count(partner.getNewCustomerCount().intValue());
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
            kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
        }
    }


    public void addPartnerLedgerDetailAgainstInvoiceAmount(Double offerPrice, Partner partner,String planId,String batchName) {
        PartnerLedgerDetails details = new PartnerLedgerDetails();
        details.setCommission(Double.parseDouble(new DecimalFormat("##.##").format(0.0)));
        details.setTranstype(Constants.TRANS_TYPE_DEBIT);
        details.setTranscategory(Constants.TRANS_CATEGORY_CUST_CREATE);
        details.setDescription("Debit Against Voucher Batch Creation = " + batchName);
        details.setPartner(partner);
        details.setCreateDate(LocalDateTime.now());
        details.setTds_amount(0.0);
        details.setAgr_amount(0.0);
        details.setTax(0.0);
        details.setPlanid(planId);
        if(planId!=null)
        {
            PostpaidPlan plan=postpaidPlanRepo.findById(Integer.parseInt(planId)).orElse(null);
            if(plan!=null)
                details.setPlanname(plan.getName());
        }
        details.setDebitDocId(null);
        details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(offerPrice)));
        details.setGrossOfferPrice(offerPrice);
        details.setCustid(null);
        details.setOfferprice(offerPrice);
        partnerLedgerDetailsRepository.save(details);
    }



    public boolean adjustPaymentAgainstInvoiceAmount(Customers customers,Double totalInvoiceAmount,DebitDocument document,Integer staffId,String staffName,Integer mvnoId)
    {
        CreditDebitDocMapping creditDebitDocMapping=null;
        Double amount=null;
        if(document!=null) {
            Partner partner=partnerRepository.findById(customers.getPartner()).orElse(null);
            CreditDocument creditDocument = new CreditDocument();
            creditDocument.setAdjustedAmount(0.0);
            if(partner.getBalance()>totalInvoiceAmount)
                creditDocument.setAmount(totalInvoiceAmount);
            else
                creditDocument.setAmount(partner.getBalance());
            creditDocument.setCustomer(customers);
            creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            creditDocument.setLcoid(customers.getLcoId());
            creditDocument.setPaymentdate(LocalDate.now());
            creditDocument.setType(Constants.PAYMENT_TYPE);
            creditDocument.setCreatedate(LocalDateTime.now());
            creditDocument.setIsDelete(false);
            creditDocument.setTdsflag(false);
            creditDocument.setPaydetails4("Received By Partner : "+partner.getName());
            creditDocument.setPaytype(Constants.ADVANCE);
            creditDocument.setMvnoId(mvnoId);
            creditDocument.setApproverid(staffId);
            creditDocument.setReferenceno(String.valueOf(Constants.getUniqueNumber()));
            creditDocument.setPaymode(Constants.PAYMENT_MODE_TYPE_CASH);
            creditDocument.setTds_received(false);
            creditDocument.setCreditdocumentno(getPaymentInvoiceNo());
            creditDocument.setCreatedById(staffId);
            creditDocument.setCreatedByName(staffName);
            creditDocument.setLastModifiedById(staffId);
            creditDocument.setLastModifiedByName(staffName);
            creditDocument.setDebitDocumentList(Arrays.asList(document));
            DebitDocument debitDocument= document;
            if(creditDocument.getAdjustedAmount()>0.0d)
                creditDocument=creditDocRepository.save(creditDocument);

            if(partner.getBalance()>totalInvoiceAmount)
                creditDocument.setAdjustedAmount(totalInvoiceAmount);
            else
                creditDocument.setAdjustedAmount(partner.getBalance());

            if(creditDocument.getAdjustedAmount()>0.0d)
                creditDocument=creditDocRepository.save(creditDocument);

            addLedgerAndLedgerDetailEntry(creditDocument,customers,false);
            if(partner.getBalance()>totalInvoiceAmount) {
                if(debitDocument.getAdjustedAmount()==null)
                    debitDocument.setAdjustedAmount(totalInvoiceAmount);
                else
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + totalInvoiceAmount);
                debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.FULLY_PAID);
            }
            else {
                if(debitDocument.getAdjustedAmount()==null)
                    debitDocument.setAdjustedAmount(partner.getBalance());
                else
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount()+partner.getBalance());
                debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
            }
            List<CreditDocument>creditDocumentList=new ArrayList<>();
            creditDocumentList.add(creditDocument);
            debitDocument.setCreditDocumentList(creditDocumentList);
            if(creditDocument.getAdjustedAmount()>0.0d)
                debitDocument=debitDocRepository.save(debitDocument);


            creditDebitDocMapping = new CreditDebitDocMapping();
            creditDebitDocMapping.setAdjustedAmount(creditDocument.getAdjustedAmount());
            creditDebitDocMapping.setIsDeleted(false);
            creditDebitDocMapping.setDebtDocId(document.getId());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebtMappingRepository.save(creditDebitDocMapping);

            List<CreditDebitDocMapping> debitDocMappings=creditDebtMappingRepository.findByCreditDocId(creditDocument.getId());
            if(debitDocMappings!=null && !debitDocMappings.isEmpty())
            {
                CreditDocument finalCreditDocument = creditDocument;
                debitDocMappings.stream().forEach(mapping->{
                    creditDebtMappingRepository.updateAdjustmentAmount(mapping.getId(), finalCreditDocument.getAdjustedAmount());
                });
            }

            if(debitDocMappings!=null && !debitDocMappings.isEmpty() && debitDocMappings.size()>1)
                creditDebtMappingRepository.delete(debitDocMappings.get(0));
            return true;
        }

        if(creditDebitDocMapping!=null && creditDebitDocMapping.getId()!=null)
        {
            creditDebitDocMapping.setAdjustedAmount(amount);
            creditDebtMappingRepository.save(creditDebitDocMapping);
        }

        return false;
    }


    public Double getTaxAmount(Tax tax, Double offerPrice) {
        Double tmpOfferPrice = offerPrice;
        Double totalTaxAmount = 0.0;
        if (tax != null) {
            Double tier1 = 0.0;
            Double tier2 = 0.0;
            Double tier3 = 0.0;

            for (int i = 0; i < tax.getTieredList().size(); i++) {
                String tierGroup = tax.getTieredList().get(i).getTaxGroup();
                Double rate = tax.getTieredList().get(i).getRate();
                if (tierGroup.equalsIgnoreCase("TIER1")) {
                    tier1 = ((tmpOfferPrice + tier1) * (rate / 100.0));
                    totalTaxAmount += tier1;
                }
                if (tierGroup.equalsIgnoreCase("TIER2") && tier1 != 0) {
                    tier2 = ((tier1) * (rate / 100.0));
                    totalTaxAmount += tier2;
                }
                if (tierGroup.equalsIgnoreCase("TIER1") && tier2 != 0) {
                    tier3 = ((tier2) * (rate / 100.0));
                    totalTaxAmount += tier3;
                }
            }
        }
        return totalTaxAmount;
    }

    public Tax getTax(Integer taxId) {
        Optional<Tax> tax = taxRepository.findById(taxId);
        return tax.isPresent() ? tax.get() : null;
    }


    public String getPaymentInvoiceNo(){
        String currinvoiceNo = null;
        String newInvoiceNo = null;
        try {
            Resource resource = null;
            LocalDate current_date = LocalDate.now();
            int current_Year = current_date.getYear();
            try {
                currinvoiceNo = creditDocRepository.getPaymentFuction();
            }
            catch (Exception e){
                logger.error("Payment Function not found ");
            }

            StringBuilder sb = new StringBuilder();
            sb.append("PY");
            sb.append(current_Year);
            sb.append("-");
            if(currinvoiceNo != null) {
                while (sb.length() < 14 - currinvoiceNo.length()) {
                    sb.append('0');
                }
                sb.append(currinvoiceNo);
                newInvoiceNo = sb.toString();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newInvoiceNo;
    }


    public void addLedgerAndLedgerDetailEntry(CreditDocument creditDocument, Customers customers, boolean isVoidInvoice) {
        CustomerLedger ledger = null;
        CustomerLedgerDtls ledgerDtls = null;

        if (creditDocument.getAdjustedAmount() > 0.0d) {
            ledger = customerLedgerRepository.findByCustomer(customers);
            if (Objects.nonNull(ledger)) {
                ledger.setTotalpaid(ledger.getTotalpaid() + creditDocument.getAmount());
                ledger.setTotaldue(ledger.getTotaldue() - creditDocument.getAmount());
                customerLedgerRepository.save(ledger);
            }

            ledgerDtls = new CustomerLedgerDtls();
            ledgerDtls.setAmount(creditDocument.getAmount());
            ledgerDtls.setPaymentMode(creditDocument.getPaymode());
            ledgerDtls.setBank(null);
            ledgerDtls.setBranch(null);
            ledgerDtls.setPaymentRefNo(creditDocument.getReciptNo());
            ledgerDtls.setCreditdocid(creditDocument.getId());
            ledgerDtls.setCREATE_DATE(LocalDateTime.now());
            ledgerDtls.setIsVoid(isVoidInvoice);
            ledgerDtls.setIsDelete(isVoidInvoice);
            ledgerDtls.setTranscategory(Constants.TRANS_CATEGORY_PAYMENT);
            ledgerDtls.setDescription(creditDocument.getPaydetails4());
            ledgerDtls.setCustomer(customers);
            ledgerDtls.setTranstype(Constants.TRANS_TYPE_CREDIT);
            customerLedgerDtlsRepository.save(ledgerDtls);
        }
    }

    public void addPartnerLedgerDetailAgainstInvoiceAmount(Double offerPrice, Customers customers, Partner partner,Long invoiceId) {
        DebitDocument debitDocument;
        String planId = "";
        if(invoiceId != null){
            debitDocument = debitDocRepository.findById(invoiceId.intValue()).orElse(null);
            if(debitDocument != null){
                if(debitDocument.getPostpaidPlan()!=null)
                    planId = debitDocument.getPostpaidPlan().getId().toString();
            }
        }
        PartnerLedgerDetails details = new PartnerLedgerDetails();
        details.setCommission(Double.parseDouble(new DecimalFormat("##.##").format(0.0)));
        details.setTranstype(Constants.TRANS_TYPE_DEBIT);
        details.setTranscategory(Constants.TRANS_CATEGORY_CUST_CREATE);
        details.setDescription("Debit Against Customer Creation = " + customers.getFirstname());
        details.setPartner(partner);
        details.setCreateDate(LocalDateTime.now());
        details.setTds_amount(0.0);
        details.setAgr_amount(0.0);
        details.setTax(0.0);
        details.setPlanid(planId);
        details.setDebitDocId(invoiceId);
        if(planId==null){
            details.setPlanid(customers.getPlanMappingList().get(0).getPlanId().toString());
            PostpaidPlan postpaidPlan=postpaidPlanRepo.findById(customers.getPlanMappingList().get(0).getPlanId()).orElse(null);
            if(postpaidPlan!=null)
                details.setPlanname(postpaidPlan.getName());
        }
        details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(offerPrice)));
        details.setGrossOfferPrice(offerPrice);
        details.setCustid(customers.getId());
        details.setOfferprice(offerPrice);
        details.setTotalSharedCommission(0.0d);
        details.setParentSharedCommission(0.0d);
        details.setChildSharedCommission(0.0d);
        details.setOperatorCommission(0.0d);

        details.setPartnerTaxId(partner.getTaxid());
        details.setAgrPercentage(Double.parseDouble(partner.getPriceBookId().getAgrPercentage()));
        details.setTdsPercentage(Double.parseDouble(partner.getPriceBookId().getTdsPercentage()));
        partnerLedgerDetailsRepository.save(details);
    }


    private void addPartnerLedgerEntryAgainstCommission(Double partner_commission, Partner partner, Customers customer) {
        if (partner.getCommissionShareType().equalsIgnoreCase("Revenue")) {
            PartnerCommission commission = new PartnerCommission();
            commission.setPartnerid(partner.getId());
            commission.setCustomerid(customer.getId());
            commission.setCommtype(partner.getCommissionShareType());
            commission.setCommval(Double.parseDouble(new DecimalFormat("##.##").format(partner_commission)));
            commission.setStatus(Constants.STATUS_PENDING);
            commission.setBilldate(LocalDateTime.now());
            commission=partnerCommissionRepository.save(commission);
        }

        PartnerLedger partnerLedger = partnerLedgerRepository.findByPartner_Id(partner.getId());
        if (partnerLedger != null) {
            if (partnerLedger.getTotaldue() == null) {
                partnerLedger.setTotaldue(0.0);
            }

            partnerLedger.setTotaldue(Double.parseDouble(new DecimalFormat("##.####").format(partnerLedger.getTotaldue() + partner_commission)));
            partnerLedger.setUpdatedate(LocalDate.now());
            partnerLedger = partnerLedgerRepository.save(partnerLedger);
        }

        if (partner.getCommissionShareType().equalsIgnoreCase("balance")) {
            Double b = partner.getBalance() + partner_commission;
            partner.setBalance(Double.parseDouble(new DecimalFormat("##.##").format(b)));
            partner=partnerRepository.save(partner);
        }
        if (partner.getCommissionShareType().equalsIgnoreCase("Revenue")) {
            partner.setCommrelvalue(Double.parseDouble(new DecimalFormat("##.##").format(partner.getCommrelvalue()+partner_commission))); //+ partnerCommRelValue);
            partner=partnerRepository.save(partner);
        }

        PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
        partnerAmountMessage.setPartnerId(partner.getId());
        partnerAmountMessage.setComrelval(partner.getCommrelvalue());
        partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
        partnerAmountMessage.setBalance(partner.getBalance());
        partnerAmountMessage.setCredit(partner.getCredit());
        partnerAmountMessage.setRenewcust_count(partner.getRenewCustomerCount().intValue());
        partnerAmountMessage.setNewCustomer_count(partner.getNewCustomerCount().intValue());
//        messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//         messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
        kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
    }



    public void revertPartnerCommission(DebitDocument document, Double creditNoteAmount) {
        try {
            Double adjustedCreditNoteAmount=creditNoteAmount;
            Double totalAdjustedAmount=0.0;
            List<PartnerLedgerDetails> details =partnerLedgerDetailsRepository.findAllByInvoiceId(document.getId());
            List<CreditDebitDocMapping> creditDebitDocMappings=creditDebtMappingRepository.findBydebtDocId(document.getId());
            List<Integer> creditDocIdList=creditDebitDocMappings.stream().map(x->x.getCreditDocId()).collect(Collectors.toList());
            List<CreditDocument> creditDocuments=creditDocRepository.findAllByIdIn(creditDocIdList);
            creditDocuments=creditDocuments.stream().filter(x->x.getStatus().equalsIgnoreCase(Constants.PAYMENT_STATUS_PENDDING) || x.getStatus().equalsIgnoreCase(Constants.CUSTOMER_STATUS_REJECTED)).collect(Collectors.toList());
            creditDocIdList=creditDocuments.stream().map(x->x.getId()).collect(Collectors.toList());

            creditDocIdList.stream().forEach(id->{
                for(int i=0;i<creditDebitDocMappings.size();i++)
                {
                    if(creditDebitDocMappings.get(i).getCreditDocId().equals(id))
                    {
                        creditDebitDocMappings.remove(creditDebitDocMappings.get(i));
                    }
                }
            });

            if(creditDebitDocMappings!=null && !creditDebitDocMappings.isEmpty()) {
                totalAdjustedAmount=creditDebitDocMappings.stream().filter(x->x.getAdjustedAmount()!=null).mapToDouble(x->x.getAdjustedAmount()).sum();
                adjustedCreditNoteAmount = creditDebitDocMappings.get(creditDebitDocMappings.size() - 1).getAdjustedAmount();
            }

            if(creditNoteAmount>0.0)
            {
                if (details != null && !details.isEmpty()) {
                    List<PartnerLedgerDetails> commissionList = details.stream().filter(x -> x.getTranscategory().equalsIgnoreCase(Constants.TRANS_CATEGORY_COMMISSION)).collect(Collectors.toList());
                    if (commissionList != null && !commissionList.isEmpty()) {
                        List<Integer> partnerList=commissionList.stream().map(x->x.getPartner().getId()).distinct().collect(Collectors.toList());
                        for(int i=0;i<partnerList.size();i++)
                        {
                            int finalI = i;
                            List<PartnerLedgerDetails> commissionList1=commissionList.stream().filter(x->x.getPartner()!=null && x.getPartner().getId().equals(partnerList.get(finalI))).collect(Collectors.toList());
                            Double commission = commissionList1.stream().mapToDouble(x -> x.getCommission()).sum();
                            if(document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() != null)
                                commission = commissionList1.stream().mapToDouble(x -> x.getAmount()).sum();

                            Double prorateCommission = creditNoteAmount * commission / commissionList1.get(0).getGrossOfferPrice();
                            if (document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() != null) {
                                revertCommission(document, creditNoteAmount, prorateCommission, commissionList1, null,partnerList.get(finalI));
                            } else if (document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() == null) {
                                revertCommission(document, creditNoteAmount, prorateCommission, commissionList1, null,partnerList.get(finalI));
                            } else if (!document.getCustomer().getIs_from_pwc() && document.getCustomer().getPartner() != Constants.DEFAULT_PARTNER_ID) {
                                revertCommission(document, creditNoteAmount, prorateCommission, commissionList1, null,partnerList.get(finalI));
                            }
                        }
                    }
                    List<PartnerLedgerDetails> balanceList = details.stream().filter(x -> x.getTranscategory().equalsIgnoreCase(Constants.TRANS_CATEGORY_ADD_BALANCE) && x.getTranstype().equalsIgnoreCase(Constants.TRANS_TYPE_DEBIT)).collect(Collectors.toList());
                    if (balanceList != null && !balanceList.isEmpty()) {
                        revertCreditNoteAmount(document, creditNoteAmount);
                    }
                }
                else if (!document.getCustomer().getIs_from_pwc() && document.getCustomer().getPartner() != Constants.DEFAULT_PARTNER_ID) {
                    if(document.getTotalamount().doubleValue()> creditNoteAmount)
                    {
                        List<TempPartnerLedgerDetail> details1 = tempPartnerLedgerDetailsRepository.findAllByInvoiceId(document.getId());
                        details1=details1.stream().filter(x->x.getTranscategory().equalsIgnoreCase(Constants.TRANS_CATEGORY_COMMISSION)).collect(Collectors.toList());
                        if (details1 != null && !details1.isEmpty()) {
                            Double commission = details1.stream().mapToDouble(x -> x.getCommission()).sum();
                            Double prorateCommission = creditNoteAmount * commission / details1.get(0).getGrossOfferPrice();
                            addRevertCommissionEntryInTmp(document,creditNoteAmount,prorateCommission,details1);
                            if(creditDebitDocMappings!=null && !creditDebitDocMappings.isEmpty())
                            {
                                if(document.getTotalamount().doubleValue()==totalAdjustedAmount.doubleValue())
                                {
                                    List<TempPartnerLedgerDetail> detail =tempPartnerLedgerDetailsRepository.findAllByInvoiceId(document.getId());
                                    if (detail != null && !detail.isEmpty()) {
                                        tempPartnerLedgerDetailsRepository.deleteAll(detail);
                                        addPartnerLedgerDetailAgainstCommissionAmount(detail,document.getCustomer());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error Unable to Add Revert Commission Against CreditNote : "+e.getMessage());
        }
    }


    private void revertCreditNoteAmount(DebitDocument document, Double creditNoteAmount) {
        PartnerLedgerDetails reverseCommission = new PartnerLedgerDetails();
        reverseCommission.setAmount(creditNoteAmount);
        reverseCommission.setDebitDocId(document.getId().longValue());
        reverseCommission.setTranstype(Constants.TRANS_TYPE_CREDIT);
        reverseCommission.setGrossOfferPrice(document.getTotalamount());
        reverseCommission.setOfferprice(document.getTotalamount());
        reverseCommission.setCustid(document.getCustomer().getId());
        reverseCommission.setPartner(partnerRepository.findById(document.getCustomer().getPartner()).orElse(null));
        reverseCommission.setIsDeleted(false);
        reverseCommission.setDebitDocId(document.getId().longValue());
        reverseCommission.setCreateDate(LocalDateTime.now());
        reverseCommission.setDescription("CreditNote Amount reverted for the invoice " + document.getDocnumber());
        reverseCommission.setTranscategory("Revert Balance");
        reverseCommission.setCommission(0.0d);
        partnerLedgerDetailsRepository.save(reverseCommission);

        Optional<Partner> partner = partnerRepository.findById(document.getCustomer().getPartner());
        if (partner.isPresent()) {
            Partner partner1 = partner.get();
            partner1.setBalance(partner.get().getBalance() + creditNoteAmount);
            partner1 = partnerRepository.save(partner1);
            PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
            partnerAmountMessage.setPartnerId(partner1.getId());
            partnerAmountMessage.setComrelval(partner1.getCommrelvalue());
            partnerAmountMessage.setCreditconsume(partner1.getCreditConsume());
            partnerAmountMessage.setBalance(partner1.getBalance());
            partnerAmountMessage.setCredit(partner1.getCredit());
            partnerAmountMessage.setRenewcust_count(partner.get().getRenewCustomerCount().intValue());
            partnerAmountMessage.setNewCustomer_count(partner.get().getNewCustomerCount().intValue());
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
            kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
        }

    }

    private void revertCommission(DebitDocument document, Double creditNoteAmount, Double prorateCommission, List<PartnerLedgerDetails> commissionList, List<TempPartnerLedgerDetail> tempCommissionList,Integer partnerId) {
        if(document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() != null)
        {
            PartnerLedgerDetails reverseCommission = new PartnerLedgerDetails();
            reverseCommission.setAmount(prorateCommission);
            reverseCommission.setDebitDocId(document.getId().longValue());
            reverseCommission.setTranstype(Constants.TRANS_TYPE_CREDIT);
            reverseCommission.setCustid(document.getCustomer().getId());
            reverseCommission.setPartner(partnerRepository.findById(partnerId).orElse(null));
            reverseCommission.setIsDeleted(false);
            reverseCommission.setCreateDate(LocalDateTime.now());
            reverseCommission.setDescription("Commission reverted for the invoice " + document.getDocnumber());
            reverseCommission.setTranscategory("Revert Commission");
            reverseCommission.setCommission(0.0);
            reverseCommission.setGrossOfferPrice(document.getTotalamount());
            reverseCommission.setOfferprice(document.getTotalamount());

            if (commissionList != null && !commissionList.isEmpty()) {
                Double agr = commissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = commissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = commissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
            }

            if (tempCommissionList != null && !tempCommissionList.isEmpty()) {
                Double agr = tempCommissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = tempCommissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = tempCommissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
            }

            partnerLedgerDetailsRepository.save(reverseCommission);
        }
        else
        {
            PartnerLedgerDetails reverseCommission = new PartnerLedgerDetails();
            reverseCommission.setAmount(0.0d);
            reverseCommission.setDebitDocId(document.getId().longValue());
            reverseCommission.setTranstype(Constants.TRANS_TYPE_DEBIT);
            reverseCommission.setCustid(document.getCustomer().getId());
            reverseCommission.setPartner(partnerRepository.findById(partnerId).orElse(null));
            reverseCommission.setIsDeleted(false);
            reverseCommission.setCreateDate(LocalDateTime.now());
            reverseCommission.setDescription("Commission reverted for the invoice " + document.getDocnumber());
            reverseCommission.setTranscategory("Revert Commission");
            reverseCommission.setCommission(prorateCommission);
            reverseCommission.setGrossOfferPrice(document.getTotalamount());
            reverseCommission.setOfferprice(document.getTotalamount());

            if (commissionList != null && !commissionList.isEmpty()) {
                Double agr = commissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = commissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = commissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
                Double totalSharedCommission=commissionList.stream().filter(x->x.getTotalSharedCommission()!=null).mapToDouble(x->x.getTotalSharedCommission()).sum();
                Double parentSharedCommission=commissionList.stream().filter(x->x.getParentSharedCommission()!=null).mapToDouble(x->x.getParentSharedCommission()).sum();
                Double childSharedCommission=commissionList.stream().filter(x->x.getChildSharedCommission()!=null).mapToDouble(x->x.getChildSharedCommission()).sum();
                Double operatorCommission=commissionList.stream().filter(x->x.getOperatorCommission()!=null).mapToDouble(x->x.getOperatorCommission()).sum();
                reverseCommission.setTotalSharedCommission(totalSharedCommission);
                reverseCommission.setParentSharedCommission(parentSharedCommission);
                reverseCommission.setChildSharedCommission(childSharedCommission);
                reverseCommission.setOperatorCommission(operatorCommission);


            }

            if (tempCommissionList != null && !tempCommissionList.isEmpty()) {
                Double agr = tempCommissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = tempCommissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = tempCommissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
                Double totalSharedCommission=tempCommissionList.stream().filter(x->x.getTotalSharedCommission()!=null).mapToDouble(x->x.getTotalSharedCommission()).sum();
                Double parentSharedCommission=tempCommissionList.stream().filter(x->x.getParentSharedCommission()!=null).mapToDouble(x->x.getParentSharedCommission()).sum();
                Double childSharedCommission=tempCommissionList.stream().filter(x->x.getChildSharedCommission()!=null).mapToDouble(x->x.getChildSharedCommission()).sum();
                Double operatorCommission=tempCommissionList.stream().filter(x->x.getOperatorCommission()!=null).mapToDouble(x->x.getOperatorCommission()).sum();

                reverseCommission.setTotalSharedCommission(totalSharedCommission);
                reverseCommission.setParentSharedCommission(parentSharedCommission);
                reverseCommission.setChildSharedCommission(childSharedCommission);
                reverseCommission.setOperatorCommission(operatorCommission);
            }

            partnerLedgerDetailsRepository.save(reverseCommission);
        }

        Optional<Partner> partner = partnerRepository.findById(partnerId);
        if (partner.isPresent()) {
            if (partner.get().getCommissionShareType().equalsIgnoreCase("Balance")) {
                Partner partner1 = partner.get();
                partner1.setBalance(partner.get().getBalance() - prorateCommission);
                partner1 = partnerRepository.save(partner1);
            }

            if (partner.get().getCommissionShareType().equalsIgnoreCase("Revenue")) {
                Partner partner1 = partner.get();
                partner1.setCommrelvalue(partner.get().getCommrelvalue() - prorateCommission);
                partner1 = partnerRepository.save(partner1);

                PartnerCommission commission = new PartnerCommission();
                commission.setPartnerid(partner1.getId());
                commission.setCustomerid(document.getCustomer().getId());
                commission.setCommtype(partner1.getCommissionShareType());
                commission.setCommval(Double.parseDouble(new DecimalFormat("##.##").format(-prorateCommission)));
                commission.setStatus(Constants.STATUS_PENDING);
                commission.setBilldate(LocalDateTime.now());
                //commission = partnerCommissionRepository.save(commission);
            }
        }
        if(partner!=null) {
            PartnerAmountMessage partnerAmountMessage = new PartnerAmountMessage();
            partnerAmountMessage.setPartnerId(partner.get().getId());
            partnerAmountMessage.setComrelval(partner.get().getCommrelvalue());
            partnerAmountMessage.setCreditconsume(partner.get().getCreditConsume());
            partnerAmountMessage.setBalance(partner.get().getBalance());
            partnerAmountMessage.setCredit(partner.get().getCredit());
            partnerAmountMessage.setRenewcust_count(partner.get().getRenewCustomerCount().intValue());
            partnerAmountMessage.setNewCustomer_count(partner.get().getNewCustomerCount().intValue());
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
            kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
        }
    }

    public void addPartnerLedgerDetailAgainstCommissionAmount(List<TempPartnerLedgerDetail> details,Customers customer) {
        if (details != null && details.size() > 0) {
            for (TempPartnerLedgerDetail tempPartnerLedgerDetail : details) {
                if (customer != null) {
                    PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
                    partnerLedgerDetails.setCustid(tempPartnerLedgerDetail.getCustid());
                    partnerLedgerDetails.setOfferprice(tempPartnerLedgerDetail.getOfferprice());
                    partnerLedgerDetails.setTax(tempPartnerLedgerDetail.getTax());
                    partnerLedgerDetails.setAgr_amount(tempPartnerLedgerDetail.getAgr_amount());
                    partnerLedgerDetails.setTds_amount(tempPartnerLedgerDetail.getTds_amount());
                    partnerLedgerDetails.setCommission(tempPartnerLedgerDetail.getCommission());
                    partnerLedgerDetails.setAmount(tempPartnerLedgerDetail.getAmount());
                    partnerLedgerDetails.setPartner(tempPartnerLedgerDetail.getPartner());
                    partnerLedgerDetails.setRoyalty(tempPartnerLedgerDetail.getRoyalty());
                    partnerLedgerDetails.setPartnerTax(tempPartnerLedgerDetail.getPartnerTax());
                    partnerLedgerDetails.setGrossOfferPrice(tempPartnerLedgerDetail.getGrossOfferPrice());
                    partnerLedgerDetails.setTranstype(tempPartnerLedgerDetail.getTranstype());
                    partnerLedgerDetails.setTranscategory(tempPartnerLedgerDetail.getTranscategory());
                    partnerLedgerDetails.setDescription(tempPartnerLedgerDetail.getDescription());
                    partnerLedgerDetails.setDebitDocId(tempPartnerLedgerDetail.getInvoice_id() != null ? Long.parseLong(tempPartnerLedgerDetail.getInvoice_id()) : null);
                    partnerLedgerDetails.setCreateDate(LocalDateTime.now());
                    partnerLedgerDetails.setPlanid(tempPartnerLedgerDetail.getPlanid());
                    partnerLedgerDetails.setRoyalty(tempPartnerLedgerDetail.getRoyalty());
                    partnerLedgerDetails.setPartnerTax(tempPartnerLedgerDetail.getPartnerTax());
                    partnerLedgerDetails.setGrossOfferPrice(tempPartnerLedgerDetail.getGrossOfferPrice());
                    partnerLedgerDetails.setRoyaltyBasePrice(tempPartnerLedgerDetail.getRoyaltyBasePrice());
                    partnerLedgerDetails.setTotalSharedCommission(tempPartnerLedgerDetail.getTotalSharedCommission());
                    partnerLedgerDetails.setParentSharedCommission(tempPartnerLedgerDetail.getParentSharedCommission());
                    partnerLedgerDetails.setChildSharedCommission(tempPartnerLedgerDetail.getChildSharedCommission());
                    partnerLedgerDetails.setOperatorCommission(tempPartnerLedgerDetail.getOperatorCommission());

                    partnerLedgerDetails.setServiceId(tempPartnerLedgerDetail.getServiceId());
                    partnerLedgerDetails.setServiceName(tempPartnerLedgerDetail.getServiceName());
                    partnerLedgerDetails.setPlanGroupId(tempPartnerLedgerDetail.getPlanGroupId());
                    partnerLedgerDetails.setPlanGroupName(tempPartnerLedgerDetail.getPlanGroupName());
                    partnerLedgerDetails.setAgrPercentage(tempPartnerLedgerDetail.getAgrPercentage());
                    partnerLedgerDetails.setTdsPercentage(tempPartnerLedgerDetail.getTdsPercentage());
                    partnerLedgerDetails.setRevenueSharePercentage(tempPartnerLedgerDetail.getRevenueSharePercentage());
                    partnerLedgerDetails.setPartnerTaxId(tempPartnerLedgerDetail.getPartner().getTaxid());
                    partnerLedgerDetails.setPlanname(tempPartnerLedgerDetail.getPlanname());
                    partnerLedgerDetails.setPlanid(tempPartnerLedgerDetail.getPlanid());

                    partnerLedgerDetailsRepository.save(partnerLedgerDetails);
                    addPartnerLedgerEntryAgainstCommission(tempPartnerLedgerDetail.getCommission(), tempPartnerLedgerDetail.getPartner(), customer);
                }
            }
        }
    }
    public void addPartnerLedgerDetailAgainstCommissionAmount(List<TempPartnerLedgerDetail> details) {
        if (details != null && details.size() > 0) {
            for (TempPartnerLedgerDetail tempPartnerLedgerDetail : details) {
                Customers customer = customersRepository.findById(tempPartnerLedgerDetail.getCustid()).orElse(null);
                if (customer != null) {
                    PartnerLedgerDetails partnerLedgerDetails = new PartnerLedgerDetails();
                    partnerLedgerDetails.setCustid(tempPartnerLedgerDetail.getCustid());
                    partnerLedgerDetails.setOfferprice(tempPartnerLedgerDetail.getOfferprice());
                    partnerLedgerDetails.setTax(tempPartnerLedgerDetail.getTax());
                    partnerLedgerDetails.setAgr_amount(tempPartnerLedgerDetail.getAgr_amount());
                    partnerLedgerDetails.setTds_amount(tempPartnerLedgerDetail.getTds_amount());
                    partnerLedgerDetails.setCommission(tempPartnerLedgerDetail.getCommission());
                    partnerLedgerDetails.setAmount(tempPartnerLedgerDetail.getAmount());
                    partnerLedgerDetails.setPartner(tempPartnerLedgerDetail.getPartner());
                    partnerLedgerDetails.setRoyalty(tempPartnerLedgerDetail.getRoyalty());
                    partnerLedgerDetails.setPartnerTax(tempPartnerLedgerDetail.getPartnerTax());
                    partnerLedgerDetails.setGrossOfferPrice(tempPartnerLedgerDetail.getGrossOfferPrice());
                    partnerLedgerDetails.setTranstype(tempPartnerLedgerDetail.getTranstype());
                    partnerLedgerDetails.setTranscategory(tempPartnerLedgerDetail.getTranscategory());
                    partnerLedgerDetails.setDescription(tempPartnerLedgerDetail.getDescription());
                    partnerLedgerDetails.setDebitDocId(tempPartnerLedgerDetail.getInvoice_id() != null ? Long.parseLong(tempPartnerLedgerDetail.getInvoice_id()) : null);
                    partnerLedgerDetails.setCreateDate(LocalDateTime.now());
                    partnerLedgerDetails.setPlanid(tempPartnerLedgerDetail.getPlanid());
                    partnerLedgerDetails.setRoyalty(tempPartnerLedgerDetail.getRoyalty());
                    partnerLedgerDetails.setPartnerTax(tempPartnerLedgerDetail.getPartnerTax());
                    partnerLedgerDetails.setGrossOfferPrice(tempPartnerLedgerDetail.getGrossOfferPrice());
                    partnerLedgerDetails.setRoyaltyBasePrice(tempPartnerLedgerDetail.getRoyaltyBasePrice());
                    partnerLedgerDetails.setTotalSharedCommission(tempPartnerLedgerDetail.getTotalSharedCommission());
                    partnerLedgerDetails.setParentSharedCommission(tempPartnerLedgerDetail.getParentSharedCommission());
                    partnerLedgerDetails.setChildSharedCommission(tempPartnerLedgerDetail.getChildSharedCommission());
                    partnerLedgerDetails.setOperatorCommission(tempPartnerLedgerDetail.getOperatorCommission());

                    partnerLedgerDetails.setServiceId(tempPartnerLedgerDetail.getServiceId());
                    partnerLedgerDetails.setServiceName(tempPartnerLedgerDetail.getServiceName());
                    partnerLedgerDetails.setPlanGroupId(tempPartnerLedgerDetail.getPlanGroupId());
                    partnerLedgerDetails.setPlanGroupName(tempPartnerLedgerDetail.getPlanGroupName());
                    partnerLedgerDetails.setAgrPercentage(tempPartnerLedgerDetail.getAgrPercentage());
                    partnerLedgerDetails.setTdsPercentage(tempPartnerLedgerDetail.getTdsPercentage());
                    partnerLedgerDetails.setRevenueSharePercentage(tempPartnerLedgerDetail.getRevenueSharePercentage());
                    partnerLedgerDetails.setPartnerTaxId(tempPartnerLedgerDetail.getPartner().getTaxid());
                    partnerLedgerDetails.setPlanname(tempPartnerLedgerDetail.getPlanname());
                    partnerLedgerDetails.setPlanid(tempPartnerLedgerDetail.getPlanid());

                    partnerLedgerDetailsRepository.save(partnerLedgerDetails);
                    addPartnerLedgerEntryAgainstCommission(tempPartnerLedgerDetail.getCommission(), tempPartnerLedgerDetail.getPartner(), customer);
                }
            }
        }
    }

    private void addRevertCommissionEntryInTmp(DebitDocument document, Double creditNoteAmount, Double prorateCommission, List<TempPartnerLedgerDetail> tempCommissionList) {
        if(document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() != null)
        {
            TempPartnerLedgerDetail reverseCommission = new TempPartnerLedgerDetail();
            reverseCommission.setAmount(prorateCommission);
            reverseCommission.setDebitDocId(document.getId().longValue());
            reverseCommission.setTranstype(Constants.TRANS_TYPE_CREDIT);
            reverseCommission.setCustid(document.getCustomer().getId());
            reverseCommission.setPartner(partnerRepository.findById(document.getCustomer().getPartner()).orElse(null));
            reverseCommission.setIsDeleted(false);
            reverseCommission.setDescription("Commission reverted for the invoice " + document.getDocnumber());
            reverseCommission.setTranscategory(Constants.TRANS_CATEGORY_REVERT_COMMISSION);
            reverseCommission.setCommission(0.0);
            reverseCommission.setGrossOfferPrice(document.getTotalamount());
            reverseCommission.setOfferprice(document.getTotalamount());
            reverseCommission.setCREATE_DATE(LocalDateTime.now());

            if (tempCommissionList != null && !tempCommissionList.isEmpty()) {
                Double agr = tempCommissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = tempCommissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = tempCommissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
            }
            tempPartnerLedgerDetailsRepository.save(reverseCommission);
        }
        else
        {
            TempPartnerLedgerDetail reverseCommission = new TempPartnerLedgerDetail();
            reverseCommission.setAmount(0.0d);
            reverseCommission.setDebitDocId(document.getId().longValue());
            reverseCommission.setTranstype(Constants.TRANS_TYPE_DEBIT);
            reverseCommission.setCustid(document.getCustomer().getId());
            reverseCommission.setPartner(partnerRepository.findById(document.getCustomer().getPartner()).orElse(null));
            reverseCommission.setIsDeleted(false);
            reverseCommission.setDescription("Commission reverted for the invoice " + document.getDocnumber());
            reverseCommission.setTranscategory("Revert Commission");
            reverseCommission.setCommission(prorateCommission);
            reverseCommission.setGrossOfferPrice(document.getTotalamount());
            reverseCommission.setOfferprice(document.getTotalamount());
            reverseCommission.setCREATE_DATE(LocalDateTime.now());

            if (tempCommissionList != null && !tempCommissionList.isEmpty()) {
                Double agr = tempCommissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = tempCommissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = tempCommissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
            }
            tempPartnerLedgerDetailsRepository.save(reverseCommission);
        }
    }

    public void inventoryPayment(List<CustomerInventoryMapping> mapping,Double totalInvoiceAmount, Customers customers,Integer staffId,DebitDocument debitDocument)
    {
        Long planId=null;
        String planName=null;
        Long serviceId=-1l;

        if(debitDocument!=null && staffId!=null)
        {
            if(debitDocument.getInventoryMappingId()!=null)
            {
                if(mapping!=null && !mapping.isEmpty())
                {
                    StaffUser staffUser = staffUserRepository.findById(staffId).orElse(null);
                    if(mapping.get(0).getPlanId()!=null)
                    {
                        Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(mapping.get(0).getPlanId().intValue());
                        if (plan.isPresent()) {
                            serviceId = plan.get().getServiceId().longValue();
                            planName=plan.get().getName();
                        }
                    }
                    if(staffId!=null && staffUser.getPartnerid()!=null && !staffUser.getPartnerid().equals(Constants.DEFAULT_PARTNER_ID))
                    {
                        if(customers.getLcoId()==null) {
                            paymentAdjustmentAgainstInventoryInvoice(totalInvoiceAmount, customers,staffUser,debitDocument);
                        }
                    }
                }
            }
        }
    }

    public void paymentAdjustmentAgainstInventoryInvoice(Double totalInvoiceAmount, Customers customers,StaffUser staffUser,DebitDocument document)
    {
        Partner partner=partnerRepository.findById(customers.getPartner()).orElse(null);
        if(document.getAdjustedAmount()==null)
            document.setAdjustedAmount(0.0);
        totalInvoiceAmount=totalInvoiceAmount-document.getAdjustedAmount();

        if ((customers.getLcoId()==null && (partner.getBalance()>0 && partner.getBalance() >= totalInvoiceAmount)))
            if (adjustPaymentAgainstInvoiceAmount(customers, totalInvoiceAmount, document,staffUser.getId(),staffUser.getFirstname(),staffUser.getMvnoId()))
                updatePartnerBalanceAgainstInvoiceAmount(customers, totalInvoiceAmount,document.getId().longValue());
    }

    
    public void transferBalanceFromOnePartnerToAnotherPartner(Integer oldPartnerId,Integer newPartnerId,Double transferableBalance,Customers customers)
    {
        Partner oldPartner = partnerRepository.findById(oldPartnerId).get();
        if (oldPartner != null && oldPartner.getId() != CommonConstants.DEFAULT_PARTNER_ID)
        {
            oldPartner.setBalance(oldPartner.getBalance() + transferableBalance);
            oldPartner=partnerRepository.save(oldPartner);
            addPartnerLedgerDetailAgainstBalanceShiftLocation(transferableBalance, oldPartner,customers,false);
        }

        Partner newPartner = partnerRepository.findById(newPartnerId).get();
        if (newPartner != null && newPartner.getId() != CommonConstants.DEFAULT_PARTNER_ID)
        {
            if (newPartner.getBalance() >= transferableBalance)
            {
                newPartner.setBalance(newPartner.getBalance() - transferableBalance);
                newPartner=partnerRepository.save(newPartner);
                addPartnerLedgerDetailAgainstBalanceShiftLocation(transferableBalance, newPartner,customers,true);
            }
            else if ((newPartner.getBalance() - transferableBalance) < 0)
            {
                if(newPartner.getBalance() > 0) {
                    Double amount=newPartner.getBalance();
                    Double creditConsume = newPartner.getCreditConsume() + (transferableBalance - newPartner.getBalance());
                    newPartner.setCreditConsume(creditConsume);
                    newPartner.setBalance(0d);
                    addPartnerLedgerDetailAgainstBalanceShiftLocation(amount, newPartner,customers,true);
                } else {
                    Double creditConsume = newPartner.getCreditConsume() + transferableBalance;
                    newPartner.setCreditConsume(creditConsume);
                }
                partnerRepository.save(newPartner);
            }
        }

        if(newPartner!=null){
            PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
            partnerAmountMessage.setPartnerId(newPartner.getId());
            partnerAmountMessage.setComrelval(newPartner.getCommrelvalue());
            partnerAmountMessage.setCreditconsume(newPartner.getCreditConsume());
            partnerAmountMessage.setBalance(newPartner.getBalance());
            partnerAmountMessage.setCredit(newPartner.getCredit());
            partnerAmountMessage.setRenewcust_count(newPartner.getRenewCustomerCount().intValue());
            partnerAmountMessage.setNewCustomer_count(newPartner.getNewCustomerCount().intValue());
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
            kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
        }
        if(oldPartner!=null){
            PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
            partnerAmountMessage.setPartnerId(oldPartner.getId());
            partnerAmountMessage.setComrelval(oldPartner.getCommrelvalue());
            partnerAmountMessage.setCreditconsume(oldPartner.getCreditConsume());
            partnerAmountMessage.setBalance(oldPartner.getBalance());
            partnerAmountMessage.setCredit(oldPartner.getCredit());
            partnerAmountMessage.setRenewcust_count(oldPartner.getRenewCustomerCount().intValue());
            partnerAmountMessage.setNewCustomer_count(oldPartner.getNewCustomerCount().intValue());
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
            kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
        }
    }



    public void transferCommissionFromOnePartnerToAnotherPartner(Integer oldPartnerId,Integer newPartnerId,Double transferableCommission,Customers customers)
    {
        Partner oldPartner = partnerRepository.findById(oldPartnerId).get();
        if (oldPartner != null && oldPartner.getId() != CommonConstants.DEFAULT_PARTNER_ID)
        {
            if(oldPartner.getCommtype().equalsIgnoreCase("Balance"))
            {
                if (oldPartner.getBalance() >= transferableCommission)
                {
                    oldPartner.setBalance(oldPartner.getBalance() - transferableCommission);
                    oldPartner=partnerRepository.save(oldPartner);
                    addPartnerLedgerDetailAgainstShiftLocation(transferableCommission, oldPartner,customers,false);
                }
                else if ((oldPartner.getBalance() - transferableCommission) < 0)
                {
                    if(oldPartner.getBalance() > 0) {
                        Double amount=oldPartner.getBalance();
                        Double creditConsume = oldPartner.getCreditConsume() + (transferableCommission - oldPartner.getBalance());
                        oldPartner.setCreditConsume(creditConsume);
                        oldPartner.setBalance(0d);
                        addPartnerLedgerDetailAgainstShiftLocation(amount, oldPartner,customers,false);
                    } else {
                        Double creditConsume = oldPartner.getCreditConsume() + transferableCommission;
                        oldPartner.setCreditConsume(creditConsume);
                    }
                    partnerRepository.save(oldPartner);
                }
            }
            else
            {
                if(oldPartner.getCommrelvalue()!=null)
                    oldPartner.setCommrelvalue(oldPartner.getCommrelvalue() - transferableCommission);
                else
                    oldPartner.setCommrelvalue(-transferableCommission);
                oldPartner=partnerRepository.save(oldPartner);
                addPartnerLedgerDetailAgainstShiftLocation(transferableCommission, oldPartner,customers,false);
            }
        }


        Partner newPartner = partnerRepository.findById(newPartnerId).get();
        if (newPartner != null && newPartner.getId() != CommonConstants.DEFAULT_PARTNER_ID)
        {
            if(newPartner.getCommtype().equalsIgnoreCase("Balance"))
            {
                newPartner.setBalance(newPartner.getBalance() + transferableCommission);
                newPartner=partnerRepository.save(newPartner);
                addPartnerLedgerDetailAgainstShiftLocation(transferableCommission, newPartner,customers,true);
            }
            else
            {
                if(newPartner.getCommrelvalue()!=null)
                    newPartner.setCommrelvalue(newPartner.getCommrelvalue() + transferableCommission);
                else
                    newPartner.setCommrelvalue(transferableCommission);
                newPartner=partnerRepository.save(newPartner);
                addPartnerLedgerDetailAgainstShiftLocation(transferableCommission, newPartner,customers,true);
            }
        }
      if(newPartner!=null){
          PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
          partnerAmountMessage.setPartnerId(newPartner.getId());
          partnerAmountMessage.setComrelval(newPartner.getCommrelvalue());
          partnerAmountMessage.setCreditconsume(newPartner.getCreditConsume());
          partnerAmountMessage.setBalance(newPartner.getBalance());
          partnerAmountMessage.setCredit(newPartner.getCredit());
          partnerAmountMessage.setRenewcust_count(newPartner.getRenewCustomerCount().intValue());
          partnerAmountMessage.setNewCustomer_count(newPartner.getNewCustomerCount().intValue());
//          messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//          messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
          kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
                }
        if(oldPartner!=null){
            PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
            partnerAmountMessage.setPartnerId(oldPartner.getId());
            partnerAmountMessage.setComrelval(oldPartner.getCommrelvalue());
            partnerAmountMessage.setCreditconsume(oldPartner.getCreditConsume());
            partnerAmountMessage.setBalance(oldPartner.getBalance());
            partnerAmountMessage.setCredit(oldPartner.getCredit());
            partnerAmountMessage.setRenewcust_count(oldPartner.getRenewCustomerCount().intValue());
            partnerAmountMessage.setNewCustomer_count(oldPartner.getNewCustomerCount().intValue());
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//            messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
            kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
        }
    }



    public void addPartnerLedgerDetailAgainstShiftLocation(Double transferableCommission, Partner partner,Customers customers,Boolean isNewPartner) {
        PartnerLedgerDetails details = new PartnerLedgerDetails();
        details.setCommission(Double.parseDouble(new DecimalFormat("##.##").format(0.0)));
        details.setTranscategory(CommonConstants.TRANS_CATEGORY_COMMISSION_TRANSFER);
        if(isNewPartner)
        {
            details.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            details.setDescription("Credit Against Customer Location Shift = " + customers.getFirstname());
            details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferableCommission)));
        }
        else
        {
            details.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
            details.setDescription("Debit Against Customer Location Shift = " + customers.getFirstname());
            details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferableCommission)));
        }

        details.setPartner(partner);
        details.setCreateDate(LocalDateTime.now());
        details.setTds_amount(0.0);
        details.setAgr_amount(0.0);
        details.setTax(0.0);
        details.setDebitDocId(null);
        details.setGrossOfferPrice(null);
        details.setCustid(null);
        details.setOfferprice(null);
     //   details.setPlanid(customers.getPlanMappingList().get(0));
        partnerLedgerDetailsRepository.save(details);
    }

    public void addPartnerLedgerDetailAgainstBalanceShiftLocation(Double transferableBalance, Partner partner,Customers customers,Boolean isNewPartner) {
        PartnerLedgerDetails details = new PartnerLedgerDetails();
        details.setCommission(Double.parseDouble(new DecimalFormat("##.##").format(0.0)));
        details.setTranscategory(CommonConstants.TRANS_CATEGORY_BALANCE_TRANSFER);
        if(isNewPartner)
        {
            details.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
            details.setDescription("Debit Against Customer Location Shift = " + customers.getFirstname());
            details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferableBalance)));
        }
        else
        {
            details.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            details.setDescription("Credit Against Customer Location Shift = " + customers.getFirstname());
            details.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(transferableBalance)));
        }

        details.setPartner(partner);
        details.setCreateDate(LocalDateTime.now());
        details.setTds_amount(0.0);
        details.setAgr_amount(0.0);
        details.setTax(0.0);
        details.setDebitDocId(null);
        details.setGrossOfferPrice(null);
        details.setCustid(null);
        details.setOfferprice(null);
        partnerLedgerDetailsRepository.save(details);
    }
}
