package com.savbill.revenuemanagement.core.service.common;

import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.dto.invoice.CreditDebitDataPojo;
import com.savbill.revenuemanagement.core.dto.invoice.CreditDebitMappingPojo;
import com.savbill.revenuemanagement.core.dto.invoice.OnlineInvoicePaymentDTO;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.inventory.CustomerInventoryMapping;
import com.savbill.revenuemanagement.core.entity.invoice.Invoice;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.nepaliCalendarUtils.model.EnglishDateDTO;
import com.savbill.revenuemanagement.core.nepaliCalendarUtils.model.NepaliDateDTO;

import com.savbill.revenuemanagement.core.repository.customer.CustomerChargeHistoryRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerServiceMapRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocDetailRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.inventory.CustomerInventoryMappingRepo;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PostpaidPlanChargeRepo;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.postpaid.PostPaidInvoiceUtil;
import com.savbill.revenuemanagement.core.util.CurrencyUtil;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.core.nepaliCalendarUtils.service.DateConverterService;
import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.ServiceRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import com.savbill.revenuemanagement.utils.CommonUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InvoiceUtil {

    private static final org.apache.log4j.Logger logger = Logger.getLogger(InvoiceUtil.class);

    @Value("${project.currency: Rs.}")
    private String curr;

    @Value("${project.currency.cent: Rs.}")
    private String centCurr;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    private CustomerServiceMapRepository customerServiceMapRepository;
    @Autowired
    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    private ChargeInvoiceUtil chargeInvoiceUtil;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private CustomerInventoryUtil customerInventoryUtil;

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private DateConverterService dateConverterService;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustomerChargeHistoryRepository customerChargeHistoryRepository;

    @Autowired
    private PostPaidInvoiceUtil postPaidInvoiceUtil;
    @Autowired
    TaxService taxService;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;

    @Autowired
    private TrialDebitDocRepository trialDebitDocRepository;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    public InvoiceDetails prepareInvoiceDetail(Customers customers, List<CustPlanMappping> custPlanMapppings, List<CustomerChargeHistory> customerChargeHistories,
                                               List<CustChargeDetails> custChargeDetails, List<CustomerServiceMapping> customerServiceMappings, List<Long> custInvIds, String postpaidAdvance,boolean planValidityChangePlan) {
        try {
            logger.debug("Initiated prepareInvoiceDetail process for  customer :  "+ customers.getUsername());
            if(!CollectionUtils.isEmpty(custPlanMapppings)) {
                DebitDocument debitDocument = new DebitDocument();
                List<DebitDocDetails> debitDocDetailsList = new ArrayList<>();
                Customers parentCustomers = customers;
                for (CustPlanMappping planMappping: custPlanMapppings) {
                    logger.debug("Initiating prepareInvoiceDetail process for  Cust Plan Mapping ID :  "+ planMappping.getId()+ " for customer: " + customers.getUsername());
                    List<Integer> chargeIds = postpaidPlanChargeRepo.getChargeListByPlanId(planMappping.getPlanId());
//                    List<CustomerChargeHistory> chargeHistories = customerChargeHistories.stream().filter(custChargeHis -> custChargeHis.getCustPlanMapppingId().equals(planMappping.getId())).collect(Collectors.toList());
                    List<CustomerChargeHistory> chargeHistories = customerChargeHistories.stream().filter(custChargeHis -> chargeIds.contains(custChargeHis.getChargeId())).collect(Collectors.toList());
                    Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappings.stream().filter(custSerMap -> custSerMap.getId().equals(planMappping.getCustServiceMappingId())).findFirst();
                    //For Bill to Organization
                    if(!customerServiceMapping.isPresent() && planMappping.getCustRefId() != null) {
                        Optional<CustPlanMappping> custPlanMappping = custPlanMapppingRepository.findById(planMappping.getCustRefId());
                        if(custPlanMappping.isPresent()) {
                            customerServiceMapping = customerServiceMapRepository.findById(custPlanMappping.get().getCustServiceMappingId());
                            if(customerServiceMapping.isPresent()) {
                                planMappping.setCustServiceMappingId(customerServiceMapping.get().getId());
                            }
                            Customers refCustomer = custPlanMappping.get().getCustomer();
                            customers.setCreatedById(refCustomer.getCreatedById());
                            customers.setCreatedByName(refCustomer.getCreatedByName());
                            customers.setLastModifiedById(refCustomer.getLastModifiedById());
                            customers.setLastModifiedByName(refCustomer.getLastModifiedByName());
                        }
                    }
                    for (CustomerChargeHistory chargeHistory: chargeHistories) {
                        logger.debug("Initiating prepareInvoiceDetail process for  chargeHistory Mapping ID :  "+ chargeHistory.getId() + " for customer: " + customers.getUsername());
                        DebitDocDetails debitDocDetails = new DebitDocDetails();
                        Boolean flag =false;
                         for (CustomerServiceMapping customerServiceMapping1 :customerServiceMappings) {
                             flag = getDiscountFlag(customerServiceMapping1);
                             if (flag){
                                 logger.debug("Checking Discount flag  for  CustomerServiceMapping  ID :  "+ customerServiceMapping1.getId() + " for customer: " + customers.getUsername());
                                 if (CommonConstants.DISCOUNT_TYPE.RECURRING.equals(customerServiceMapping.get().getDiscountType()) && customerServiceMapping.get().getDiscount() != 0.0  && customerServiceMapping.get().getNewDiscountExpiryDate() != null && (customerServiceMapping.get().getNewDiscountExpiryDate().isAfter(LocalDate.now()) || customerServiceMapping.get().getNewDiscountExpiryDate().equals(LocalDate.now()))) {
                                     debitDocDetails.setDiscountPercentage(customerServiceMapping.get().getDiscount());
                                     logger.debug("Setting RECURRING Discount of "+customerServiceMapping.get().getDiscount()+" for  CustomerServiceMapping  ID :  "+ customerServiceMapping1.getId() + " for customer: " + customers.getUsername());
                                     chargeHistory.setDiscount(customerServiceMapping.get().getDiscount());;//used in further calulations in taxcalculation method
                                 }else if(CommonConstants.DISCOUNT_TYPE.ONE_TIME.equals(customerServiceMapping.get().getDiscountType()) && customerServiceMapping.get().getNewDiscountExpiryDate() == null) {
                                     debitDocDetails.setDiscountPercentage(customerServiceMapping.get().getDiscount());
                                     logger.debug("Setting ONE_TIME Discount of "+customerServiceMapping.get().getDiscount()+" for  CustomerServiceMapping  ID :  "+ customerServiceMapping1.getId() + " for customer: " + customers.getUsername());
                                     chargeHistory.setDiscount(customerServiceMapping.get().getDiscount());
                                 }else {
                                     debitDocDetails.setDiscountPercentage(0d);
                                     chargeHistory.setDiscount(0d);
                                 }
                             }
                         }
                        // For parent-child
                        if(customerServiceMapping.get().getInvoiceType() != null && customerServiceMapping.get().getInvoiceType().equalsIgnoreCase(Constants.CUSTOMER_INVOICE_TYPE.GROUP) && customers.getParentCustomers() != null) {
                            parentCustomers = customers.getParentCustomers();
                        } else {
                            parentCustomers = customers;
                        }
                        if(planMappping.getId().equals(chargeHistory.getCustPlanMapppingId())) {
                            logger.info("Initiating     setDebitDocDetails process for  customer :  "+ customers.getUsername());
                            debitDocDetails = setDebitDocDetails(planMappping, chargeHistory, parentCustomers, debitDocDetails, customerServiceMapping.get(),postpaidAdvance,planValidityChangePlan);
                            debitDocDetailsList.add(debitDocDetails);
                        }
                        debitDocument.setStartdate(debitDocDetails.getStartdate());
                        if (customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                            debitDocument.setEndate(debitDocDetails.getEnddate());
                        }else {
                            debitDocument.setEndate(planMappping.getEndDate());
                        }
                    }
                    debitDocument.setCustRefName(planMappping.getCustRefName());
                    if(planMappping.getBillableCustomerId()!=null){
                        debitDocument.setBillableToName(customersRepository.findNameById(planMappping.getBillableCustomerId()));
                    }else{
                        debitDocument.setBillableToName(planMappping.getCustomer().getUsername());
                    }
                }

                Integer cprId = 0;
                for (CustChargeDetails custChargeDetail : custChargeDetails){
                    CustPlanMappping custPlanMappping = custPlanMapppingRepository.findById(custChargeDetail.getCustPlanMapppingId()).get();
                    DebitDocDetails debitDocDetails = chargeInvoiceUtil.setDebitDocDetailsForCharge(custChargeDetail, custPlanMappping);
                    debitDocDetailsList.add(debitDocDetails);
                }
                if (!CollectionUtils.isEmpty(debitDocDetailsList)) {
                    debitDocument.setEndate(debitDocDetailsList.stream()
                            .map(DebitDocDetails::getEnddate) // Extract end dates
                            .max(LocalDateTime::compareTo).get());
//                    List<DebitDocDetails> debitDocDetails= debitDocDetailsList.stream().filter(i->i.getChargetype().equalsIgnoreCase("ADVANCE")).collect(Collectors.toList());
//                    if (debitDocDetails.size()>0){
//                        debitDocument.setStartdate(debitDocDetails.get(0).getStartdate());
//                    }
                }


                if (!custInvIds.isEmpty()) {
                    List<CustomerInventoryMapping> inventoryMappings = customerInventoryMappingRepo.findAllByIdInAndCustomerId(custInvIds, customers.getId().longValue());
                    inventoryMappings=inventoryMappings.stream().filter(x->x.getIsDeleted()!=null && x.getIsDeleted().equals(false)).collect(Collectors.toList());
                    for(CustomerInventoryMapping inventoryMapping: inventoryMappings) {
                        DebitDocDetails debitDocDetails =customerInventoryUtil.setDebitDocDetailsForInventory(inventoryMapping);
                        if(debitDocDetails != null) {
                            debitDocDetailsList.add(debitDocDetails);
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(debitDocDetailsList))
                    debitDocument = setDebitDocBasicDetails(debitDocument, debitDocDetailsList, parentCustomers, custPlanMapppings.get(custPlanMapppings.size() - 1).getId());
                InvoiceDetails invoiceDetails = new InvoiceDetails(debitDocument, debitDocDetailsList, null);
                return invoiceDetails;
            } else {
                logger.error("No plan available with customer!");
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while generate invoice: "+ex.getMessage());
        }
        return null;
    }

    public DebitDocDetails setDebitDocDetails(CustPlanMappping planMappping, CustomerChargeHistory chargeHistory, Customers customers, DebitDocDetails debitDocDetails,CustomerServiceMapping customerServiceMapping,String postpaidAdvance,boolean planValidityChangePlan) {
        logger.info("Initiated setDebitDocDetails process for  customer :  "+ customers.getUsername());
        debitDocDetails.setChargecycle(String.valueOf(chargeHistory.getBillingCycle()));
        debitDocDetails.setChargename(chargeHistory.getChargeName());
        debitDocDetails.setChargeid(chargeHistory.getChargeId());
        debitDocDetails.setChargetype(chargeHistory.getChargeType());
        debitDocDetails.setCustServiceId(Long.valueOf(planMappping.getCustServiceMappingId()));
        debitDocDetails.setIcCode(String.valueOf(planMappping.getCustServiceMappingId()));
        debitDocDetails.setNoofcycle(-1);//TODO: Need to confirm
        debitDocDetails.setDescription(chargeHistory.getCharge_desc());
        debitDocDetails.setProrationtype("F");
        debitDocDetails.setStartdate(LocalDateTime.now());
        debitDocDetails.setEnddate(planMappping.getExpiryDate());
        debitDocDetails.setPlanId(String.valueOf(planMappping.getPlanId()));
        boolean discountAlreadyAppliend = false;
        if(customerServiceMapping.getDiscountType() != null && customerServiceMapping.getDiscountExpiryDate() != null) {
            logger.debug("Checking whether Discount is already applied and is discount expired for :  "+ customers.getUsername());

            if(customerServiceMapping.getDiscountType().equalsIgnoreCase(CommonConstants.DISCOUNT_TYPE.RECURRING) && (customerServiceMapping.getDiscountExpiryDate().isAfter(LocalDate.now()) || customerServiceMapping.getDiscountExpiryDate().equals(LocalDate.now()))) {
                discountAlreadyAppliend = true;
            } else if (chargeHistory.getDiscountExpDate()!=null && (chargeHistory.getDiscountExpDate().isAfter(LocalDate.now()) || chargeHistory.getDiscountExpDate().equals(LocalDate.now()))){
                discountAlreadyAppliend = true;
            }

            if(discountAlreadyAppliend && customerServiceMapping.getDiscount()!=null && customerServiceMapping.getDiscount()!=0)
                debitDocDetails.setDiscountPercentage(customerServiceMapping.getDiscount());
            if(customerServiceMapping.getDiscount()!=null && discountAlreadyAppliend )
                chargeHistory.setDiscount(customerServiceMapping.getDiscount());
            else
                chargeHistory.setDiscount(0d);

            if(discountAlreadyAppliend && customerServiceMapping.getNewDiscount()!=null && customerServiceMapping.getNewDiscount()!=0)
                debitDocDetails.setDiscountPercentage(customerServiceMapping.getNewDiscount());
            if(planMappping.getDiscount()!=null && discountAlreadyAppliend && customerServiceMapping.getNewDiscount()!=null && customerServiceMapping.getNewDiscount()!=0)
                chargeHistory.setDiscount(customerServiceMapping.getNewDiscount());
        }

        Optional<Services> services = serviceRepository.findById(customerServiceMapping.getServiceId());
        if (services.isPresent()){
            debitDocDetails.setServiceId(services.get().getId());
        }
        long planValidityDays = planMappping.getPlanValidityDays();
        if(planValidityChangePlan){
            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(planMappping.getPlanId()).get();
            Integer planValidity = postpaidPlan.getValidity().intValue();
            String validityUnit = postpaidPlan.getUnitsOfValidity();
            if (validityUnit != null) {
                switch (validityUnit.trim().toLowerCase()) {
                    case "hours":
                        planValidity = (int) Math.ceil(planValidity / 24.0);
                        break;
                    case "months":
                        planValidity = (int) ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.now().plusMonths(planValidity));
                        break;
                    case "years":
                        planValidity = (int) ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.now().plusYears(planValidity));
                        break;
                    case "days":
                    default:
                        break;
                }
            }
            if(planValidity!=null){
                planValidityDays = planValidity.longValue();
            }
        }

        long usedDaysValidity = Duration.between(planMappping.getStartDate(), planMappping.getEndDate()).toDays();
            if(customers.getMvnoId()!=null) {
                ClientService clientService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.ADD_1_DAY_INVOICE_CREATION, customers.getMvnoId());
                if (clientService!=null && clientService.getValue().equalsIgnoreCase("true")) {
                    usedDaysValidity = Duration.between(planMappping.getStartDate(), planMappping.getEndDate().plusHours(24)).toDays();
                }
            }

        if(usedDaysValidity == 0L)
            usedDaysValidity = 1L;
        double chargeAmount = chargeHistory.getChargeAmount();


        if(customers.getCusttype().equalsIgnoreCase("Prepaid") && customers.getId()!=1 && ! customers.getUsername().equalsIgnoreCase("ORGANIZATIONPRE") ) {
            //amount calculations
            if (planValidityDays > usedDaysValidity && planValidityDays != usedDaysValidity) {
                Double chargePrice = chargeHistory.getChargeAmount();
                Double dbr = chargePrice / planValidityDays;
                Double proratCharge = dbr * usedDaysValidity;
                chargeHistory.setChargeAmount(proratCharge);
                logger.debug("Charge amount based on plan Validity days is  :  " +proratCharge + " for customer:  "+ customers.getUsername());
            }
            if(postpaidAdvance.equalsIgnoreCase(Constants.INVOICE_TYPE.RENEW)){
                debitDocDetails.setStartdate(planMappping.getStartDate());
            }
            if(postpaidAdvance!=null && (postpaidAdvance.equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN) || postpaidAdvance.equalsIgnoreCase(Constants.INVOICE_TYPE.RENEW))){
                List<CustPlanMappping> custPlanMapppings = custPlanMapppingRepository.findAllByGraceDaysAndCustomerId(customers.getId());
                logger.info("Charge amount calculation for change plan and renew   for customer:  "+ customers.getUsername());
                if (custPlanMapppings.size()>0){
                    Double chargePrice = chargeHistory.getChargeAmount();
                    Double dbr = chargePrice / planValidityDays;
                    Integer graceDays = 0;
                    for (CustPlanMappping custPlanMappping : custPlanMapppings){
                        Long daysBetween = 0l;
                        logger.info("Charge amount calculation for change plan and renew  plan if there was Promise_to_pay used  for customer:  "+ customers.getUsername());
                        if(custPlanMappping.getPromise_to_pay_enddate().isBefore(LocalDateTime.now()) && postpaidAdvance.equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN)){
                            daysBetween = ChronoUnit.DAYS.between(custPlanMappping.getPromise_to_pay_startdate(), LocalDateTime.now());
                        }else {
                            daysBetween = ChronoUnit.DAYS.between(custPlanMappping.getPromise_to_pay_startdate(), custPlanMappping.getPromise_to_pay_enddate());
                        }
                        graceDays += daysBetween.intValue();
                    }
                    Double proratCharge = chargePrice+(dbr*graceDays);
                    chargeHistory.setChargeAmount(proratCharge);
                    logger.debug("Charge amount based on plan Validity days is  :  " +proratCharge + " for customer:  "+ customers.getUsername());
                    custPlanMapppings = custPlanMapppings.stream().peek(i->i.setGraceDays(0)).collect(Collectors.toList());
                    custPlanMapppingRepository.saveAll(custPlanMapppings);
                }

            }
        }

        debitDocDetails.setSubtotal(chargeHistory.getChargeAmount());
//        chargeHistory.setTaxAmount(0.0);      //Will not work as set 0 will set 0 in all tax
        logger.info("Initiating calculateTierTax process for  customer :  "+ customers.getUsername());
        taxService.calculateTierTax(chargeHistory,chargeHistory.getTaxId());
//        CustomerServiceMapping customerServiceMapping = customerServiceMapRepository.findById(serviceMappingId).orElse(null);
        if (customerServiceMapping != null) {
            if ( customerServiceMapping.getNewDiscountType()!= null && customerServiceMapping.getNewDiscountType().equalsIgnoreCase(CommonConstants.DISCOUNT_TYPE.ONE_TIME)) {
                logger.info("Setting new discount expiry date in customerServiceMapping for customer : "+ customers.getUsername() );
                customerServiceMapping.setNewDiscountExpiryDate(LocalDate.now().minusDays(1));
               customerServiceMapRepository.save(customerServiceMapping);
            }
        }

        debitDocDetails.setTax(chargeHistory.getTaxAmount());
        //double discountedAmount = debitDocDetails.getSubtotal() + debitDocDetails.getTax();
        if(debitDocDetails.getDiscountPercentage() != null) {
            //debitDocDetails.setDiscount(calculateDiscount(discountedAmount, debitDocDetails.getDiscountPercentage()));
            if(chargeHistory.getDiscount()!=null) //-ve discount should not be in invoice
                debitDocDetails.setDiscount(chargeHistory.getDiscount());
            else
                debitDocDetails.setDiscount(0d);
        }
        else
            debitDocDetails.setDiscount(0d);
        chargeHistory.setChargeAmount(chargeAmount);
        logger.debug("Setting final charge amount in Charge History without tax for  customer : "+ customers.getUsername() + " as :" + chargeAmount);
        customerChargeHistoryRepository.save(chargeHistory);
        debitDocDetails.setTotalamount(debitDocDetails.getSubtotal() - debitDocDetails.getDiscount() + debitDocDetails.getTax());
        logger.debug("Setting Total amount in debitDocDetails with tax for   customer : "+ customers.getUsername() + " as :" + (debitDocDetails.getSubtotal() - debitDocDetails.getDiscount() + debitDocDetails.getTax()));

        return debitDocDetails;
    }

    public DebitDocument setDebitDocBasicDetails(DebitDocument debitDocument, List<DebitDocDetails> debitDocDetailsList, Customers customers, Integer cprId) {
       // double totalCharge = debitDocDetailsList.stream().mapToDouble(DebitDocDetails::getSubtotal).sum();
        double totalCharge = (double) Math.round(debitDocDetailsList.stream().mapToDouble(DebitDocDetails::getSubtotal).sum() * 10000) / 10000.0;
        double totalTax = (double) Math.round(debitDocDetailsList.stream().mapToDouble(DebitDocDetails::getTax).sum() * 10000) / 10000.0;
        double totalDiscount = (double) Math.round(debitDocDetailsList.stream().mapToDouble(DebitDocDetails::getDiscount).sum() * 10000) / 10000.0;
        double totalInstallmentCharge = (double) Math.round(debitDocDetailsList.stream().mapToDouble(d -> d.getInstallmentCharge() != null ? d.getInstallmentCharge() : 0.0).sum() * 10000) / 10000.0;
        double total =(double) Math.round( debitDocDetailsList.stream().mapToDouble(DebitDocDetails::getTotalamount).sum() * 10000) / 10000.0;
        double installmentAmount = totalCharge * totalInstallmentCharge;
        debitDocument.setDebitDocDetailsList(debitDocDetailsList);
        debitDocument.setSubtotal(totalCharge);
        debitDocument.setTax(totalTax);
        debitDocument.setInstallmentInterest(installmentAmount);
        debitDocument.setTotalamount(total);
        debitDocument.setDiscount(totalDiscount);
        debitDocument.setTotalCustomerDiscount(totalDiscount);
        debitDocument.setBilldate(LocalDateTime.now());
        debitDocument.setAdjustedAmount(0d);
        debitDocument.setCustomer(customers);
        debitDocument.setBillrunstatus(Constants.INVOICE_STATUS.GENERATED.status());
        debitDocument.setBillrunid(debitDocument.getBillrunid());
        debitDocument.setBillableToName(debitDocument.getBillableToName());
        debitDocument.setCustpackrelid(cprId);
        debitDocument.setIsDelete(false);
        debitDocument.setBuId(customers.getBuId());
        Boolean isLco = false;
        if(customers.getLcoId() != null)
            isLco = true;
        //debitDocument.setDocnumber(numberSequenceUtil.getInvoiceNumber(isLco, customers.getPartner(), customers.getMvnoId()));
        debitDocument.setDocnumber("");
        debitDocument.setUsedByThread(false);
        debitDocument.setLcoId(customers.getLcoId());
        debitDocument.setPendingAmt(debitDocument.getTotalamount());
        debitDocument.setTotaldue(debitDocument.getTotalamount());
        debitDocument.setCreatedate(LocalDateTime.now());
        if (customers.getCreatedById()!=null) {
            debitDocument.setCreatedById(customers.getCreatedById());
            debitDocument.setNextStaff(customers.getCreatedById());
        }else {
            debitDocument.setCreatedById(1);
        }
        debitDocument.setUpdatedate(LocalDateTime.now());
        if (customers.getLastModifiedByName()!=null) {
            debitDocument.setLastModifiedByName(customers.getLastModifiedByName());
            debitDocument.setCreatedByName(customers.getCreatedByName());
            debitDocument.setLastModifiedById(customers.getLastModifiedById());
        }else{
            debitDocument.setLastModifiedByName("admin");
            debitDocument.setCreatedByName("admin");
            debitDocument.setLastModifiedById(1);

        }
        if(customers.getLastBillDate() != null)
            debitDocument.setFirstbill("N");
        else
            debitDocument.setFirstbill("Y");
        debitDocument.setPaymentStatus(Constants.INVOICE_PAYMENT_STATUS.UNPAID.status());
        debitDocument.setIsDirectChargeInvoice(false);
        debitDocument.setStaffid(customers.getCreatedById());
        debitDocument.setPromiseToPayHoldDays("0");
        debitDocument.setIsPromiseToPayInOldCPR(false);
        debitDocument.setStatus("pending");
        debitDocument.setIsCNEnable(false);
       // debitDocument.setLocalbilldate(debitDocument.getBilldate().getDayOfMonth() + " " + debitDocument.getBilldate().getMonth().name() + " " + debitDocument.getBilldate().getYear());
        if(debitDocument.getStartdate() == null)
            debitDocument.setStartdate(LocalDateTime.now());
        debitDocument.setLocalstartdate(debitDocument.getStartdate().getDayOfMonth() + " " + debitDocument.getStartdate().getMonth().name() + " " + debitDocument.getStartdate().getYear());
        if(debitDocument.getEndate() == null)
            debitDocument.setEndate(LocalDateTime.now());
        if(customers.getMvnoId()!=1) {
            LocalDateTime dueDate = postPaidInvoiceUtil.getDueDate(debitDocument.getStartdate(), customers.getMvnoId());
            debitDocument.setDuedate(dueDate);
        }
        debitDocument.setLocalenddate(debitDocument.getEndate().getDayOfMonth() + " " + debitDocument.getEndate().getMonth().name() + " " + debitDocument.getEndate().getYear());
        if(customers.getCurrency() != null){
            String centCurrDynamic = getSubunitName(customers.getCurrency());
            debitDocument.setTotalamountinwords(convertToAmount((debitDocument.getTotalamount() * 100) / 100, customers.getCurrency(), centCurrDynamic) + " Only");
            debitDocument.setTotaldueinwords(convertToAmount(debitDocument.getTotaldue(), customers.getCurrency(), centCurrDynamic) + " Only");
        } else {
            debitDocument.setTotalamountinwords(convertToAmount((debitDocument.getTotalamount() * 100) / 100, curr, centCurr) + " Only");
            debitDocument.setTotaldueinwords(convertToAmount(debitDocument.getTotaldue(), curr, centCurr) + " Only");
        }
        debitDocument.setPreviousbalance(0.0);
        debitDocument.setLatepaymentfee(0.0);
        debitDocument.setCurrentpayment(0.0);
        debitDocument.setCurrentdebit(0.0);

        return debitDocument;
    }

    public static String convertToAmount(Double value, String curr, String centCurr) {
//        double amount =  Math.floor(value);
//        double cents = value - amount;
//        double centsAsInt = Math.round(100 * cents);

        long roundedValue = Math.round(value * 100); // Convert to paisa/cent fully  (3540.28=354028)
        long amount = roundedValue / 100;            // Rs part    ( 3540)
        long centsAsInt = roundedValue % 100;             // Paisa part  ( 28)

        String amountStr = CurrencyUtil.convert(amount);
        String centStr = CurrencyUtil.convert(centsAsInt);
        return amountStr + " " + curr + " AND " + centStr + " " + centCurr;
    }

    public static String getSubunitName(String currCode) {
        Currency c = Currency.getInstance(currCode);
        int fractionDigits = c.getDefaultFractionDigits();
        switch(currCode) {
            case "MMK":
                return "Pya";
            case "INR":
                return "Paise";
            case "USD":
                return "Cent";
            default:
                return (fractionDigits == 2 ? "Cent" : "");
        }
    }


    public String getInvoiceNo(){
        String currinvoiceNo = "";
        String newInvoiceNo = "";
        try {
            Resource resource = null;
            LocalDate current_date = LocalDate.now();
            int current_Year = current_date.getYear();
            try {
                synchronized (this) {
                    currinvoiceNo = debitDocRepository.getInvoiceNo();
                }
            }
            catch (Exception e){
                logger.error("Payment Function not found ");
            }

            StringBuilder sb = new StringBuilder();
            sb.append(current_Year);
            sb.append("-");
            if(currinvoiceNo != null) {
                while (sb.length() < 12 - currinvoiceNo.length()) {
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



    public String getInvoiceNoForTrial() {
        String currinvoiceNo=null;
        String newInvoiceNo=null;
        try {
            Resource resource = null;
            LocalDate current_date = LocalDate.now();
            int current_Year = current_date.getYear();

            try{
                synchronized (this) {
                    currinvoiceNo = debitDocRepository.getInvoiceNoForTrial();
                }
            } catch (Exception e) {
                logger.error("Payment Function not found ");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(current_Year);
            sb.append("-");
            while (sb.length() < 12 - currinvoiceNo.length()) {
                sb.append('0');
            }
            sb.append(currinvoiceNo);
            newInvoiceNo=sb.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        newInvoiceNo = "TRIAL-"+newInvoiceNo;
        return newInvoiceNo;
    }


    public double calculateDiscount(double amount, double disPer) {
        if(disPer == 0 || disPer < 0)
            return 0;

        double s=100-disPer;
        return  amount - (s*amount)/100;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    /**
     Create XML for invoice
     * @param invoice
     * @return
     */
    public String createXML(Invoice invoice) {
        JAXBContext jaxbContext;
        StringWriter sw = new StringWriter();
        try {
            //logger.debug("[InvoiceUtil]:Invoice Data is " + invoice);
            jaxbContext = JAXBContext.newInstance(Invoice.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(invoice, sw);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }



    public TrialDebitDocument setDebitDocBasicDetails(TrialDebitDocument debitDocument, List<TrialDebitDocumentDetail> debitDocDetailsList, Customers customers, Integer cprId) {
        double totalCharge = debitDocDetailsList.stream().mapToDouble(TrialDebitDocumentDetail::getSubtotal).sum();
        double totalTax = debitDocDetailsList.stream().mapToDouble(TrialDebitDocumentDetail::getTax).sum();
        double totalDiscount = debitDocDetailsList.stream().filter(detail -> detail.getDiscount() != null).mapToDouble(TrialDebitDocumentDetail::getDiscount).sum();
        double total = debitDocDetailsList.stream().mapToDouble(TrialDebitDocumentDetail::getTotalamount).sum();
        debitDocument.setTrialDebitDocumentDetails(debitDocDetailsList);
        debitDocument.setSubtotal(totalCharge);
        debitDocument.setTax(totalTax);
        debitDocument.setTotalamount(total);
        debitDocument.setDiscount(totalDiscount);
        debitDocument.setBilldate(LocalDateTime.now());
        debitDocument.setCustomer(customers);
        debitDocument.setBillrunstatus(Constants.INVOICE_STATUS.GENERATED.status());
//            debitDocument.setBillrunid(); TODO: need to understand
        debitDocument.setBillableToName(customers.getFullName());//TODO: Need to check
        debitDocument.setCustpackrelid(cprId);
//            debitDocument.setDebitDocumentTAXRels(); TODO: need to add
        debitDocument.setIsDelete(false);
        Boolean isLco = false;
        if(customers.getLcoId() != null)
            isLco = true;
        debitDocument.setDocnumber(numberSequenceUtil.getInvoiceNumberForTrial(isLco, customers.getPartner(), customers.getMvnoId()));
        debitDocument.setTotaldue(debitDocument.getTotalamount());
        debitDocument.setCreatedate(LocalDateTime.now());
        debitDocument.setLastModifiedByName(customers.getLastModifiedByName());
        debitDocument.setCreatedByName(customers.getCreatedByName());
         if(debitDocument.getStartdate() == null)
            debitDocument.setStartdate(LocalDateTime.now());
        if(debitDocument.getEndate() == null)
            debitDocument.setEndate(LocalDateTime.now());
        if(customers.getCurrency() != null){
            String centCurrDynamic = getSubunitName(customers.getCurrency());
            debitDocument.setAmountinwords(convertToAmount(debitDocument.getTotalamount(), customers.getCurrency(), centCurrDynamic) + " Only");
            debitDocument.setDueinwords(convertToAmount(debitDocument.getTotaldue(), customers.getCurrency(), centCurrDynamic) + " Only");
        } else {
            debitDocument.setAmountinwords(convertToAmount(debitDocument.getTotalamount(), curr, centCurr) + " Only");
            debitDocument.setDueinwords(convertToAmount(debitDocument.getTotaldue(), curr, centCurr) + " Only");
        }
        debitDocument.setPreviousbalance(0.0);
        debitDocument.setLatepaymentfee(0.0);
        debitDocument.setCurrentpayment(0.0);
        debitDocument.setCurrentdebit(0.0);
        debitDocument.setPaymentStatus(Constants.INVOICE_PAYMENT_STATUS.UNPAID.status());
        return debitDocument;
    }

    public TrialDebitDocumentDetail setDebitDocDetails(CustPlanMappping planMappping, CustomerChargeHistory chargeHistory, Customers customers, TrialDebitDocumentDetail debitDocDetails,String postpaidAdvance) {
        logger.debug("Initiated setDebitDocDetails process for  PlanMappind Id: "+planMappping.getId() +" and chargeHistory Mapping ID :  "+ chargeHistory.getId() + " for customer: " + customers.getUsername());
        debitDocDetails.setChargecycle(String.valueOf(chargeHistory.getBillingCycle()));
        debitDocDetails.setChargename(chargeHistory.getChargeName());
        debitDocDetails.setChargeid(chargeHistory.getChargeId());
        debitDocDetails.setChargetype(chargeHistory.getChargeType());
        debitDocDetails.setNoofcycle(-1);//TODO: Need to confirm
        debitDocDetails.setDescription(chargeHistory.getCharge_desc());
        debitDocDetails.setPlanId(planMappping.getPlanId());
        if (chargeHistory.getBillingCycle()!=null && chargeHistory.getCustomerBillDay()!=null) {
            LocalDate nextBillDate = LocalDate.now().plusMonths(chargeHistory.getBillingCycle()).withDayOfMonth(Integer.parseInt(chargeHistory.getCustomerBillDay().toString()));
            LocalDate endDate = nextBillDate.minusDays(1);
            debitDocDetails.setEnddate(endDate.atStartOfDay());
        }else {
            debitDocDetails.setEnddate(customers.getNextBillDate().atStartOfDay().minusHours(24));
        }
        debitDocDetails.setProrationtype("F");
        debitDocDetails.setStartdate(LocalDateTime.now());
        double discount=0.0;
        if(planMappping.getDiscount() != null)
            discount=planMappping.getDiscount();
        debitDocDetails.setDiscount(discount);
        long planValidityDays = planMappping.getPlanValidityDays();

        if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)){
            LocalDateTime endDate = chargeHistory.getCreatedate().plusMonths(chargeHistory.getBillingCycle());
            planValidityDays =  Duration.between(chargeHistory.getCreatedate(), endDate).toDays();
        }
        long usedDaysValidity = Duration.between(planMappping.getStartDate(), planMappping.getEndDate().plusHours(24)).toDays();
        double chargeAmount = chargeHistory.getChargeAmount();
        if(usedDaysValidity == 0L)
            usedDaysValidity = 1L;
        //amount calculations
        if(customers.getCusttype().equalsIgnoreCase("Postpaid") && customers.getId()!=2 && ! customers.getUsername().equalsIgnoreCase("ORGANIZATIONPOS"))
        {
            logger.debug("Calculating UsedValidity days for customer: " + customers.getUsername());
            if(planMappping.getPurchaseType().equalsIgnoreCase("Volume Booster")  ||  planMappping.getPurchaseType().equalsIgnoreCase("Bandwidthbooster"))
                planMappping.setIsInvoiceCreated(true);

            if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {


                if (postpaidAdvance != null && postpaidAdvance.equalsIgnoreCase("ChangePlan")) {
                    if (chargeHistory.getLastBillDate() == null) {
                        usedDaysValidity = Duration.between(chargeHistory.getCreatedate(), chargeHistory.getNextBillDate().atStartOfDay()).plusHours(24).toDays();
                    } else if (chargeHistory.getLastBillDate() != null){ //Issue resolved for change plan postpaid customer with recurring postapid charge
                        usedDaysValidity = Duration.between(chargeHistory.getLastBillDate().atStartOfDay(), chargeHistory.getNextBillDate().atStartOfDay()).toDays();
                    }else {
                        usedDaysValidity = planValidityDays;
                    }
                } else {
                    if(chargeHistory.getChargeType().equalsIgnoreCase("ADVANCE")) { // For Advance recurring charge type
                        if ((postpaidAdvance.equalsIgnoreCase("Advance"))){//this condition is for prorated calculation for change plan and first time advance charge cust creation
                            if (chargeHistory.getCreatedate().toLocalDate().equals(LocalDate.now())){
                                usedDaysValidity = Duration.between( LocalDate.now().atStartOfDay(),chargeHistory.getNextBillDate().atStartOfDay()).toDays();
                            }else if (chargeHistory.getBillingCycle()==1 && chargeHistory.getLastBillDate().getDayOfMonth() != chargeHistory.getCustomerBillDay()){
                                usedDaysValidity = Duration.between(chargeHistory.getNextBillDate().atStartOfDay(), customers.getNextBillDate().atStartOfDay()).toDays();
                            }
                        } else {
                            usedDaysValidity = planValidityDays;
                        }
                    } else { // For Postpaid recurring charge type
                        if (chargeHistory.getLastBillDate() == null) {  // First Time
                            usedDaysValidity = Duration.between(chargeHistory.getCreatedate(), chargeHistory.getNextBillDate().atStartOfDay()).plusHours(24).toDays();
                        } else if (( postpaidAdvance.equalsIgnoreCase("Advance")) || chargeHistory.getLastBillDate().getDayOfMonth() != chargeHistory.getCustomerBillDay()){//this condition is for prorated calculation for change plan and first time advance charge cust creation
                            usedDaysValidity = Duration.between(chargeHistory.getLastBillDate().atStartOfDay(), chargeHistory.getNextBillDate().atStartOfDay()).toDays();
                        } else {
                            usedDaysValidity = planValidityDays;
                        }
                    }


                }
            }
            logger.debug("UsedValiditys days for customer: " + customers.getUsername() + " is :" + usedDaysValidity + " days");
            if(usedDaysValidity == 0L)
                usedDaysValidity = 1L;
            Double chargePrice = chargeHistory.getChargeAmount();
            Double dbr = chargePrice / planValidityDays;
            Double proratCharge = dbr * usedDaysValidity;
            if (chargeHistory.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_NONRECURRING)){
                chargeHistory.setIsFirstChargeApply(true);
                chargeHistory.setChargeAmount(chargePrice);
                logger.debug("Setting ChargeAmount in chargeHistory for customer :  "+ customers.getUsername() + " as: " + chargePrice);
            }else {
                chargeHistory.setChargeAmount(proratCharge);
                logger.debug("Setting ChargeAmount in chargeHistory for customer :  "+ customers.getUsername() + " as: " + proratCharge);
            }
            if (chargeHistory.getNextBillDate()!=null) {
                chargeHistory.setLastBillDate(LocalDate.now());
                chargeHistory.setNextBillDate(LocalDate.now().plusMonths(chargeHistory.getBillingCycle()).withDayOfMonth(customers.getBillday()));
            }

        }

        if(customers.getCusttype().equalsIgnoreCase("Prepaid") && customers.getId()!=1 && ! customers.getUsername().equalsIgnoreCase("ORGANIZATIONPRE") ) {
            //amount calculations
            if (planValidityDays > usedDaysValidity && planValidityDays != usedDaysValidity) {
                logger.info("[" + this.getClass().getName() + "] proration Required for greater than Actual Validity : ");
                Double chargePrice = chargeHistory.getChargeAmount();
                Double dbr = chargePrice / planValidityDays;
                Double proratCharge = dbr * usedDaysValidity;
                chargeHistory.setChargeAmount(proratCharge);
            }
        }
        logger.debug("Setting Subtotal in debitDocDetails for customer :  "+ customers.getUsername() + " as: " + chargeHistory.getChargeAmount());
        debitDocDetails.setSubtotal(chargeHistory.getChargeAmount());
        taxService.calculateTierTax(chargeHistory,chargeHistory.getTaxId());
        debitDocDetails.setDiscount(chargeHistory.getDiscount());
        debitDocDetails.setTax(chargeHistory.getTaxAmount());
        debitDocDetails.setTotalamount(debitDocDetails.getSubtotal() - debitDocDetails.getDiscount() + debitDocDetails.getTax());
        logger.debug("Setting Totalamount in debitDocDetails for customer :  "+ customers.getUsername() + " as: " + (debitDocDetails.getSubtotal() - debitDocDetails.getDiscount() + debitDocDetails.getTax()));
        chargeHistory.setDiscount(discount);
        chargeHistory.setChargeAmount(chargeAmount);
        return debitDocDetails;
    }


    /**
     prepare for caf invoice details
     * @Author Vikas
     * @param prepareInvoiceDetailCaf
     * @param TrialInvoiceDetails
     *
     */
    public TrialInvoiceDetails prepareInvoiceDetailCaf(Customers customers, List<CustPlanMappping> custPlanMapppings, List<CustomerChargeHistory> customerChargeHistories,
                                                       List<CustChargeDetails> custChargeDetails, List<CustomerServiceMapping> customerServiceMappings, String postpaidAdvance) {
        try {
            logger.debug("Initiated prepareInvoiceDetailCaf  for Trial invoice of Customer : " + customers.getUsername());
            if(!CollectionUtils.isEmpty(custPlanMapppings)) {
//                DebitDocument debitDocument = new DebitDocument();
                TrialDebitDocument debitDocument = new TrialDebitDocument();
                List<TrialDebitDocumentDetail> debitDocDetailsList = new ArrayList<>();
                Customers parentCustomers = customers;
                for (CustPlanMappping planMappping: custPlanMapppings) {
                    logger.debug("Initiating prepareInvoiceDetailCaf process for  Cust Plan Mapping ID :  "+ planMappping.getId()+ " for customer: " + customers.getUsername());
                    List<CustomerChargeHistory> chargeHistories = customerChargeHistories.stream().filter(custChargeHis -> custChargeHis.getCustPlanMapppingId().equals(planMappping.getId())).collect(Collectors.toList());
                    Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappings.stream().filter(custSerMap -> custSerMap.getId().equals(planMappping.getCustServiceMappingId())).findFirst();
                    //For Bill to Organization
                    if(!customerServiceMapping.isPresent()) {
                        Optional<CustPlanMappping> custPlanMappping = custPlanMapppingRepository.findById(planMappping.getCustRefId());
                        if(custPlanMappping.isPresent()) {
                            customerServiceMapping = customerServiceMapRepository.findById(custPlanMappping.get().getCustServiceMappingId());
                            if(customerServiceMapping.isPresent()) {
                                planMappping.setCustServiceMappingId(customerServiceMapping.get().getId());
                            }
//                            TODO
                            Customers refCustomer = custPlanMappping.get().getCustomer();
                            customers.setCreatedById(refCustomer.getCreatedById());
                            customers.setCreatedByName(refCustomer.getCreatedByName());
                            customers.setLastModifiedById(refCustomer.getLastModifiedById());
                            customers.setLastModifiedByName(refCustomer.getLastModifiedByName());
                        }
                    }
                    for (CustomerChargeHistory chargeHistory: chargeHistories) {
                        logger.debug("Initiating prepareInvoiceDetail process for  chargeHistory Mapping ID :  "+ chargeHistory.getId() + " for customer: " + customers.getUsername());
                        TrialDebitDocumentDetail debitDocDetails = new TrialDebitDocumentDetail();
                        if(customerServiceMapping.get().getDiscount() != null) {
                            debitDocDetails.setDiscount(customerServiceMapping.get().getDiscount());
                            chargeHistory.setDiscount(customerServiceMapping.get().getDiscount());
                            logger.debug("Setting Discount of "+customerServiceMapping.get().getDiscount()+" in debitDocDetails and chargeHistory  ID :  "+ chargeHistory.getId() + " for customer: " + customers.getUsername());
                        }
                        else {
                            debitDocDetails.setDiscount(0d);
                            chargeHistory.setDiscount(0d);//used in further calulations in taxcalculation method
                            logger.debug("Setting Discount of "+customerServiceMapping.get().getDiscount()+" in debitDocDetails and chargeHistory  ID :  "+ chargeHistory.getId() + " for customer: " + customers.getUsername());
                        }
                        // For parent-child
                        if(customerServiceMapping.get().getInvoiceType() != null && customerServiceMapping.get().getInvoiceType().equalsIgnoreCase(Constants.CUSTOMER_INVOICE_TYPE.GROUP) && customers.getParentCustomers() != null) {
                            parentCustomers = customers.getParentCustomers();
                        } else {
                            parentCustomers = customers;
                        }
                        logger.info("Initiating setDebitDocDetails process for  PlanMappind Id: "+planMappping.getId() +" and chargeHistory Mapping ID :  "+ chargeHistory.getId() + " for customer: " + customers.getUsername());
                        debitDocDetails = setDebitDocDetails(planMappping, chargeHistory, parentCustomers, debitDocDetails,postpaidAdvance);
                        debitDocDetailsList.add(debitDocDetails);
                        debitDocument.setStartdate(debitDocDetails.getStartdate());
                        if (customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                            debitDocument.setEndate(debitDocDetails.getEnddate());
                        }else {
                            debitDocument.setEndate(planMappping.getEndDate());
                        }
                    }
                }
                debitDocument = setDebitDocBasicDetails(debitDocument, debitDocDetailsList, parentCustomers, custPlanMapppings.get(0).getId());
                TrialInvoiceDetails invoiceDetails = new TrialInvoiceDetails(debitDocument, debitDocDetailsList);
                return invoiceDetails;
            } else {
                logger.error("No plan available with customer!");
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while generate invoice: "+ex.getMessage());
        }
        return null;
    }

    public LocalDateTime calculateExpiryDate(Customers parentCustomer, PostpaidPlan plan, Long validity) {
        LocalDateTime expDate = null;

            if (plan.getUnitsOfValidity() != null && !"".equals(plan.getUnitsOfValidity())) {
                if (parentCustomer.getCalendarType() != null && parentCustomer.getCalendarType().equalsIgnoreCase(CommonConstants.CAL_TYPE_NEPALI)) {
                    LocalDateTime date = LocalDateTime.now();
                    String currentDateAndTime = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + date.getYear() + " " + date.getHour() + ":" + date.getMinute() + ":" + date.getSecond();
                    NepaliDateDTO nepaliCurrentDateDTO = dateConverterService.getNepaliDateFromEnglishDate(currentDateAndTime);
                    NepaliDateDTO nepaliEndDateDTO = null;
                    long plusDays = plan.getValidity().longValue();
                    if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_DAYS)) {
                        LocalDateTime endDateForday = date.plusDays(plusDays);
                        nepaliEndDateDTO = dateConverterService.getNepaliDateFromEnglishDate(endDateForday.getDayOfMonth() + "-" + endDateForday.getMonthValue() + "-" + endDateForday.getYear() + " " + endDateForday.getHour() + ":" + endDateForday.getMinute() + ":" + endDateForday.getSecond());
//                        nepaliEndDateDTO = dateConverterService.calculateEndDateNepaliByDay(nepaliCurrentDateDTO, (int) plusDays);
                    } else if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_MONTHS)) {
                        nepaliEndDateDTO = dateConverterService.calculateEndDateNepaliByMonth(nepaliCurrentDateDTO, (int) plusDays);
                    } else if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_YEARS)) {
                        nepaliEndDateDTO = dateConverterService.calculateEndDateNepaliByYear(nepaliCurrentDateDTO, (int) plusDays);
                    } else if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                        LocalDateTime endDateForday = date.plusHours(plusDays);
                        nepaliEndDateDTO = dateConverterService.getNepaliDateFromEnglishDate(endDateForday.getDayOfMonth() + "-" + endDateForday.getMonthValue() + "-" + endDateForday.getYear() + " " + endDateForday.getHour() + ":" + endDateForday.getMinute() + ":" + endDateForday.getSecond());
                    }

                    EnglishDateDTO englishEndDateDTO = dateConverterService.getEnglishDateDTOFromNepaliDate(nepaliEndDateDTO.toString());

                    expDate = LocalDateTime.of(englishEndDateDTO.getYear(), englishEndDateDTO.getMonth(), englishEndDateDTO.getDate(), englishEndDateDTO.getHour(), englishEndDateDTO.getMin(), englishEndDateDTO.getSec());
                }
                if (parentCustomer.getCalendarType().equalsIgnoreCase(CommonConstants.CAL_TYPE_ENGLISH)) {
                    if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_DAYS)) {
                        expDate = LocalDateTime.now().plusDays(validity);
                    } else if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_MONTHS)) {
                        expDate = LocalDateTime.now().plusDays(CommonUtils.getDaysForExpiryDateByMonth(validity.doubleValue(), LocalDate.now()));
                    } else if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_YEARS)) {
                        expDate = LocalDateTime.now().plusDays(CommonUtils.getDaysForExpiryDateByYear(validity.doubleValue(), LocalDate.now()));
                    } else if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                        Long hours =validity;
                        expDate = LocalDateTime.now().plusHours(hours);
                    }
                }
            }
        return expDate;
    }
    public Boolean getDiscountFlag(CustomerServiceMapping customerServiceMapping){
        if(customerServiceMapping.getDiscount()!=null &&customerServiceMapping.getDiscountType()!=null &&customerServiceMapping.getDiscount() == 0.0 && customerServiceMapping.getDiscountType().equalsIgnoreCase("Recurring")){
            return false;
        }
        return true;
    }

    public void adjustInvoicePaymentFromOnlinePayment(OnlineInvoicePaymentDTO onlineInvoicePaymentDTO) throws Exception{
       logger.info("kafka recieve for invoice payment "+onlineInvoicePaymentDTO.getPaymentGatewayName()+" by this gateway and invoice Id "+onlineInvoicePaymentDTO.getInvoiceId()+" with amount "+onlineInvoicePaymentDTO.getAmount());
       Customers customers = customersRepository.findById(onlineInvoicePaymentDTO.getCustId()).orElse(null);
       if(customers != null){
           logger.info("customer is found by given custId");
           if(customers.getStatus().equalsIgnoreCase("NewActivation")){
               logger.info("Caf customer is found");
               TrialDebitDocument trialDebitDocument = trialDebitDocRepository.findById(onlineInvoicePaymentDTO.getInvoiceId()).orElse(null);
               if(trialDebitDocument != null) {
                   RecordPaymentPojo recordPaymentPojo = creditDocService.createPaymentForOnlineCaf(trialDebitDocument, onlineInvoicePaymentDTO.getPaymentGatewayName(), onlineInvoicePaymentDTO.getTransactionNumber().toString(),onlineInvoicePaymentDTO.getAmount());
                   creditDocService.saveTrialCreditDocument(recordPaymentPojo, false, false, false, onlineInvoicePaymentDTO.getMvnoId(), onlineInvoicePaymentDTO.getPartnerId(), onlineInvoicePaymentDTO.getBuId(), onlineInvoicePaymentDTO.getIsLco(), onlineInvoicePaymentDTO.getCreatedById(), onlineInvoicePaymentDTO.getCreatedByName());
                   List<CreditDocument> getAllCreditDoc = creditDocRepository.findAllByCustomer(trialDebitDocument.getCustomer());
                   if (!getAllCreditDoc.isEmpty()) {
                       logger.info("caf customer is found going for caf adjustment");
                       creditDocService.addPaymentInCustomerLedger(trialDebitDocument.getCustomer(), getAllCreditDoc.get(getAllCreditDoc.size() - 1)); /**for ledger correction**/
                       CreditDebitMappingPojo creditDebitDocMappingPojo = new CreditDebitMappingPojo();
                       creditDebitDocMappingPojo.setInvoiceId(trialDebitDocument.getId());
                       CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
                       creditDebitDataPojo.setAmount(onlineInvoicePaymentDTO.getAmount());
                       creditDebitDataPojo.setId(getAllCreditDoc.get(getAllCreditDoc.size() - 1).getId());
                       List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
                       creditDebitDataPojoList.add(creditDebitDataPojo);
                       creditDebitDocMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
                       creditDocService.adjustManualPaymentToCafInvoice(creditDebitDocMappingPojo);
                       trialDebitDocument = trialDebitDocRepository.findById(trialDebitDocument.getId()).get();
                       List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findByCreditDocId(getAllCreditDoc.get(getAllCreditDoc.size() - 1).getId());
                       creditDocService.deleteDuplicateEntry(creditDebitDocMappingList);
                   }
           }
               else{
                 logger.error("Trial invoice not found by given ");
               }
           }
           if(customers.getStatus().equalsIgnoreCase("Active")){
               DebitDocument debitDocument = debitDocRepository.findById(onlineInvoicePaymentDTO.getInvoiceId()).orElse(null);
               if(debitDocument != null) {
                   RecordPaymentPojo recordPaymentPojo = creditDocService.createPaymentForOnline(debitDocument, onlineInvoicePaymentDTO.getPaymentGatewayName(), onlineInvoicePaymentDTO.getTransactionNumber().toString());
                   creditDocService.save(recordPaymentPojo, false, false, false, onlineInvoicePaymentDTO.getMvnoId(), onlineInvoicePaymentDTO.getPartnerId(), onlineInvoicePaymentDTO.getBuId(), onlineInvoicePaymentDTO.getIsLco(), onlineInvoicePaymentDTO.getCreatedById(), onlineInvoicePaymentDTO.getCreatedByName());
                   List<CreditDocument> getAllCreditDoc = creditDocRepository.findAllByCustomer(debitDocument.getCustomer());
                   if (!getAllCreditDoc.isEmpty()) {
                       logger.info("Normal customer is found going for normal adjustment");
                       creditDocService.addPaymentInCustomerLedger(debitDocument.getCustomer(), getAllCreditDoc.get(getAllCreditDoc.size() - 1)); /**for ledger correction**/
                       CreditDebitMappingPojo creditDebitDocMappingPojo = new CreditDebitMappingPojo();
                       creditDebitDocMappingPojo.setInvoiceId(debitDocument.getId());
                       CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
                       creditDebitDataPojo.setAmount(debitDocument.getTotalamount());
                       creditDebitDataPojo.setId(getAllCreditDoc.get(getAllCreditDoc.size() - 1).getId());
                       List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
                       creditDebitDataPojoList.add(creditDebitDataPojo);
                       creditDebitDocMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
                       creditDocService.adjustManualPaymentToInvoice(creditDebitDocMappingPojo);
                       debitDocument = debitDocRepository.findById(debitDocument.getId()).get();
                       List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findByCreditDocId(getAllCreditDoc.get(getAllCreditDoc.size() - 1).getId());
                       creditDocService.deleteDuplicateEntry(creditDebitDocMappingList);
                   }
               }
               else{
                   logger.error("Invoice not found by given invoiceId");
               }
           }

       }
       else{
           logger.error("Customer is not found by given custId");
       }
    }
}
