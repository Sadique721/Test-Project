package com.savbill.revenuemanagement.core.service.postpaid;

import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.InvoiceDetails;
import com.savbill.revenuemanagement.core.entity.inventory.CustomerInventoryMapping;
import com.savbill.revenuemanagement.core.repository.customer.CustomerChargeHistoryRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerServiceMapRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.inventory.CustomerInventoryMappingRepo;
import com.savbill.revenuemanagement.core.repository.partner.PostpaidPlanChargeRepo;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.common.ChargeInvoiceUtil;
import com.savbill.revenuemanagement.core.service.common.CustomerInventoryUtil;
import com.savbill.revenuemanagement.core.service.common.InvoiceUtil;
import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.ServiceRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PostPaidInvoiceUtil {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(PostPaidInvoiceUtil.class);
    @Autowired
    PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    CustomerServiceMapRepository customerServiceMapRepository;

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomerInventoryUtil customerInventoryUtil;

    @Autowired
    private InvoiceUtil invoiceUtil;

    @Autowired
    private ChargeInvoiceUtil chargeInvoiceUtil;

    @Autowired
    private TaxService taxService;
    @Autowired
    private CustomerChargeHistoryRepository customerChargeHistoryRepository;

    @Autowired
    private ClientServiceRepository clientServiceRepository;

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;
    public InvoiceDetails prepareInvoiceDetail(Customers customers, List<CustPlanMappping> custPlanMapppings, List<CustomerChargeHistory> customerChargeHistories,
                                               List<CustChargeDetails> custChargeDetails, List<CustomerServiceMapping> customerServiceMappings, List<Long> custInvIds, String postpaidAdvance, boolean isEarlyBillDate, boolean trailPlanFromToday, boolean trailPlanFromTrailday, boolean cafCustomerApprove) {
        try {
            logger.debug("Initiated prepareInvoiceDetail process for  customer :  "+ customers.getUsername());
            if(!CollectionUtils.isEmpty(custPlanMapppings)) {
                logger.warn(String.format("In prepareInvoiceDetail isEarlyBillDate: %s, CPR: %s, CCH: %s, CCD: %s, CSM: %s, CIS: %s",isEarlyBillDate
                        ,custPlanMapppings.size(),customerChargeHistories.size(),custChargeDetails.size(),customerServiceMappings.size(),custInvIds.size()));
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
                        if(customerServiceMapping.get().getDiscount() != null && chargeHistory.getDiscountExpDate()!=null &&  (chargeHistory.getDiscountExpDate().isAfter(LocalDate.now()) || chargeHistory.getDiscountExpDate().equals(LocalDate.now()))) {
                            debitDocDetails.setDiscountPercentage(customerServiceMapping.get().getDiscount());
                            chargeHistory.setDiscount(customerServiceMapping.get().getDiscount());//used in further calulations in taxcalculation method
                        }else {
                            debitDocDetails.setDiscountPercentage(0d);
                            chargeHistory.setDiscount(0d);
                        }
                        // For parent-child
                        if(customerServiceMapping.get().getInvoiceType() != null && customerServiceMapping.get().getInvoiceType().equalsIgnoreCase(Constants.CUSTOMER_INVOICE_TYPE.GROUP) && customers.getParentCustomers() != null) {
                            parentCustomers = customers.getParentCustomers();
                        } else {
                            parentCustomers = customers;
                        }
                        if(planMappping.getId().equals(chargeHistory.getCustPlanMapppingId())) {
                            logger.info("Initiating     setDebitDocDetails process for  customer :  "+ customers.getUsername());
                            debitDocDetails = setDebitDocDetails(planMappping, chargeHistory, parentCustomers, debitDocDetails, customerServiceMapping.get(),postpaidAdvance,isEarlyBillDate,trailPlanFromToday,trailPlanFromTrailday,cafCustomerApprove);
                            debitDocDetailsList.add(debitDocDetails);
                            debitDocument.setStartdate(debitDocDetails.getStartdate());
                            if (customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                                debitDocument.setEndate(debitDocDetails.getEnddate());
                                LocalDateTime dueDate = getDueDate(debitDocDetails.getStartdate(),customers.getMvnoId());
                                debitDocument.setDuedate(dueDate);
                            }else {
                                debitDocument.setEndate(planMappping.getEndDate());
                            }
                        }

                    }
                    debitDocument.setCustRefName(planMappping.getCustRefName());
                    if(planMappping.getBillableCustomerId()!=null){
                        debitDocument.setBillableToName(customersRepository.findNameById(planMappping.getBillableCustomerId()));
                    }
                }

                Integer cprId = 0;
                for (CustChargeDetails custChargeDetail : custChargeDetails){
                    CustPlanMappping custPlanMappping = custPlanMapppingRepository.findById(custChargeDetail.getCustPlanMapppingId()).get();
                    DebitDocDetails debitDocDetails = chargeInvoiceUtil.setDebitDocDetailsForCharge(custChargeDetail, custPlanMappping);
                    debitDocDetailsList.add(debitDocDetails);
                }
                if (customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID) && !CollectionUtils.isEmpty(debitDocDetailsList)) {
                    LocalDateTime endDate = debitDocDetailsList.stream()
                            .map(DebitDocDetails::getEnddate) // Extract end dates
                            .max(LocalDateTime::compareTo).get();
                    debitDocument.setEndate(endDate.minusDays(1));
                    List<DebitDocDetails> debitDocDetails= debitDocDetailsList.stream().filter(i->i.getChargetype().equalsIgnoreCase("ADVANCE")).collect(Collectors.toList());
                    if (debitDocDetails.size()>0){
                        debitDocument.setStartdate(debitDocDetails.get(0).getStartdate());
                    }
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
                if(!CollectionUtils.isEmpty(debitDocDetailsList)){
                    debitDocument = invoiceUtil.setDebitDocBasicDetails(debitDocument, debitDocDetailsList, parentCustomers, custPlanMapppings.get(0).getId());
                    logger.warn("Invoice Details startDate: "+debitDocument.getStartdate()+" endDate: "+debitDocument.getEndate());
                }
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

    public LocalDateTime getDueDate(LocalDateTime startDate,Integer mvnoId) {
        LocalDateTime duedate = LocalDateTime.now();
        try{
            String fromDate =clientServiceRepository.findValueByNameAndMvnoId(CommonConstants.DUE_DATE_CONSTANTS.DUEDATETYPE,mvnoId);
            if (fromDate==null){
                return duedate;
            } else if (fromDate.equalsIgnoreCase(CommonConstants.DUE_DATE_CONSTANTS.STARTDATE)) {
                String days =clientServiceRepository.findValueByNameAndMvnoId(CommonConstants.DUE_DATE_CONSTANTS.DUEDAYS,mvnoId);
                duedate = startDate.plusDays(Long.parseLong(days));
            } else if (fromDate.equalsIgnoreCase(CommonConstants.DUE_DATE_CONSTANTS.CREATEDDATE)) {
                String days =clientServiceRepository.findValueByNameAndMvnoId(CommonConstants.DUE_DATE_CONSTANTS.DUEDAYS,mvnoId);
                duedate = duedate.plusDays(Long.parseLong(days));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return duedate;
    }

    public DebitDocDetails setDebitDocDetails(CustPlanMappping planMappping, CustomerChargeHistory chargeHistory, Customers customers, DebitDocDetails debitDocDetails,CustomerServiceMapping customerServiceMapping,String postpaidAdvance,boolean isEarlyBillDate, boolean trailPlanFromToday, boolean trailPlanFromTrailday, boolean cafCustomerApprove) {
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
        debitDocDetails.setEnddate(customers.getNextBillDate().atStartOfDay().minusHours(24));
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

        long usedDaysValidity = Duration.between(planMappping.getStartDate(), planMappping.getEndDate().plusHours(24)).toDays();
        if(usedDaysValidity == 0L)
            usedDaysValidity = 1L;
        double chargeAmount = chargeHistory.getChargeAmount();
        if(customers.getCusttype().equalsIgnoreCase("Postpaid") && customers.getId()!=2 && ! customers.getUsername().equalsIgnoreCase("ORGANIZATIONPOS"))
        {
            if(!planMappping.getPurchaseType().equalsIgnoreCase("Volume Booster")  &&  !planMappping.getPurchaseType().equalsIgnoreCase("Bandwidthbooster")) {

                LocalDateTime endDate = chargeHistory.getCreatedate().plusMonths(chargeHistory.getBillingCycle());
                planValidityDays =  Duration.between(chargeHistory.getCreatedate(), endDate).toDays();

                if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {


                    if (postpaidAdvance != null && postpaidAdvance.equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN)) {
                        if (chargeHistory.getLastBillDate() == null) {
                            usedDaysValidity = Duration.between(chargeHistory.getCreatedate(), chargeHistory.getNextBillDate().atStartOfDay()).plusHours(24).toDays();
                        } else if (chargeHistory.getLastBillDate() != null){ //Issue resolved for change plan postpaid customer with recurring postapid charge
                            usedDaysValidity = Duration.between(chargeHistory.getLastBillDate().atStartOfDay(), chargeHistory.getNextBillDate().atStartOfDay()).toDays();
                        }else {
                            usedDaysValidity = planValidityDays;
                        }
                    } else if (postpaidAdvance != null && postpaidAdvance.equalsIgnoreCase(Constants.INVOICE_TYPE.RENEW)) {
                        usedDaysValidity = planValidityDays;
                    } else {
                        if(chargeHistory.getChargeType().equalsIgnoreCase("ADVANCE")) { // For Advance recurring charge type
                            if ((postpaidAdvance != null && postpaidAdvance.equalsIgnoreCase("Advance"))){//this condition is for prorated calculation for change plan and first time advance charge cust creation
                                LocalDateTime date = LocalDateTime.now();
                                if (chargeHistory.getCreatedate().toLocalDate().equals(LocalDate.now())){
                                    usedDaysValidity = Duration.between(LocalDate.now().atStartOfDay(),chargeHistory.getNextBillDate().atStartOfDay()).toDays();
                                } else if(cafCustomerApprove){
                                    usedDaysValidity = Duration.between(LocalDate.now().atStartOfDay(), customers.getNextBillDate().atStartOfDay()).toDays();
                                } else if (customers.getFirstActivationDate()!=null && customers.getFirstActivationDate().toLocalDate().equals(LocalDate.now())) {
                                    usedDaysValidity = Duration.between(chargeHistory.getLastBillDate().atStartOfDay(), customers.getNextBillDate().atStartOfDay()).toDays();
                                } else if(trailPlanFromToday){
                                    usedDaysValidity = Duration.between(LocalDate.now().atStartOfDay(), customers.getNextBillDate().atStartOfDay()).toDays();
                                } else if(trailPlanFromTrailday){
                                    usedDaysValidity = Duration.between(chargeHistory.getLastBillDate().atStartOfDay(), customers.getNextBillDate().atStartOfDay()).toDays();
                                } else if (chargeHistory.getBillingCycle()==1 && chargeHistory.getLastBillDate().getDayOfMonth() != chargeHistory.getCustomerBillDay()){
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
            }
            logger.debug("UsedDaysValidity for customer :  "+ customers.getUsername() + " is: " + usedDaysValidity);
            if(planMappping.getPurchaseType().equalsIgnoreCase("Volume Booster")  &&  planMappping.getPurchaseType().equalsIgnoreCase("Bandwidthbooster")) {
                planMappping.setIsInvoiceCreated(true);
                logger.debug("Setting  setIsInvoiceCreated flag for customer :  "+ customers.getUsername() + " as: True ");
            }

            LocalDate nextchargeenddate = null;
            if (customers.isBillDayUpdated()) {
                int billDay = customers.getBillday();
                LocalDate today = LocalDate.now();
                if (today.getDayOfMonth() >= billDay) {
                    nextchargeenddate = LocalDate.of(today.getYear(), today.getMonth(), 1)
                            .plusMonths(1)
                            .withDayOfMonth(billDay);
                } else {
                    nextchargeenddate = LocalDate.of(today.getYear(), today.getMonth(), 1)
                            .withDayOfMonth(billDay);
                }
                usedDaysValidity = Duration.between(chargeHistory.getNextBillDate().atStartOfDay(), nextchargeenddate.atStartOfDay()).toDays();
            }

//            if(usedDaysValidity == 0L)
//                    usedDaysValidity = 1L;
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
            if (chargeHistory.getNextBillDate()!=null && postpaidAdvance != null && !postpaidAdvance.equalsIgnoreCase("Advance")) {
                if(!chargeHistory.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_ADVANCE)) {
                    debitDocDetails.setStartdate(chargeHistory.getLastBillDate() != null ? ((customers.getEarlyBillDay()>0 && isEarlyBillDate) ? customers.getNextBillDate().atStartOfDay() : chargeHistory.getLastBillDate().atStartOfDay()) : customers.getCreatedate());
                    debitDocDetails.setEnddate((customers.getEarlyBillDay()>0 || postpaidAdvance.equalsIgnoreCase("addon"))? customers.getNextBillDate().atStartOfDay():LocalDateTime.now());
                }
                chargeHistory.setLastBillDate(LocalDate.now());
                if (planMappping.getCustPlanStatus().equalsIgnoreCase("Active")) {
                    if(chargeHistory.getBillingCycle() == 1 && postpaidAdvance.equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN) ) {
                        chargeHistory.setNextBillDate(customers.getNextBillDate());
                    }else if (postpaidAdvance.equalsIgnoreCase(Constants.INVOICE_TYPE.RENEW)) {
                        chargeHistory.setLastBillDate(chargeHistory.getNextBillDate());
                        chargeHistory.setNextBillDate(chargeHistory.getNextBillDate().plusMonths(chargeHistory.getBillingCycle()).withDayOfMonth(customers.getBillday()));
                    } else {
                        LocalDate nextBillDate = getNextBilldate(chargeHistory,customers,isEarlyBillDate);
                        chargeHistory.setNextBillDate(nextBillDate);
                    }
                }

            }

            if(chargeHistory.getDiscount()!=null && chargeHistory.getDiscount()!=0){
                chargeHistory.setDiscountExpDate(LocalDate.now());
            }
            if (chargeHistory.getDiscount()!=null && customerServiceMapping.getDiscountType() != null && customerServiceMapping.getDiscountType().equalsIgnoreCase("Recurring")){
                LocalDate nextDiscountdate = LocalDate.now().plusMonths(chargeHistory.getBillingCycle()).withDayOfMonth(customers.getBillday());
                if (customerServiceMapping.getDiscountExpiryDate().isAfter(nextDiscountdate) || customerServiceMapping.getDiscountExpiryDate().isEqual(nextDiscountdate)) {
                    chargeHistory.setDiscountExpDate(LocalDate.now().plusMonths(chargeHistory.getBillingCycle()).withDayOfMonth(customers.getBillday()));
                }
            }

            if(customers.getCusttype().equals(Constants.CUSTOMER_TYPE.POSTPAID)) {
                if(chargeHistory.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_ADVANCE)) {
                    if (chargeHistory.getBillingCycle()!=null && chargeHistory.getCustomerBillDay()!=null) {
                        LocalDate endDate = chargeHistory.getNextBillDate();
                        if (customers.isBillDayUpdated()) {
                            int billDay = customers.getBillday();
                            LocalDate today = LocalDate.now();
                            if (today.getDayOfMonth() >= billDay) {
                                endDate = LocalDate.of(today.getYear(), today.getMonth(), 1)
                                        .plusMonths(1)
                                        .withDayOfMonth(billDay);
                            } else {
                                endDate = LocalDate.of(today.getYear(), today.getMonth(), 1)
                                        .withDayOfMonth(billDay);
                            }
                            logger.info("****************** bill_day_updated true for custid : {} " + customers.getId() + " and bill_day_updated : {} " + endDate);
                            customersRepository.resetBillDayUpdatedFlagByCustId(customers.getId());
                        }
                        debitDocDetails.setEnddate(endDate.atStartOfDay());
                        if(trailPlanFromToday || cafCustomerApprove){
                            debitDocDetails.setStartdate(LocalDateTime.now());
                        } else {
                            debitDocDetails.setStartdate(chargeHistory.getLastBillDate()!=null ? ((customers.getEarlyBillDay()>0 && isEarlyBillDate) ? customers.getNextBillDate().atStartOfDay() : chargeHistory.getLastBillDate().atStartOfDay()) : LocalDateTime.now());
                        }
                    }
                }
            }

        }

        if(customers.getCusttype().equalsIgnoreCase("Prepaid") && customers.getId()!=1 && ! customers.getUsername().equalsIgnoreCase("ORGANIZATIONPRE") ) {
            //amount calculations
            if (planValidityDays > usedDaysValidity && planValidityDays != usedDaysValidity) {
                Double chargePrice = chargeHistory.getChargeAmount();
                Double dbr = chargePrice / planValidityDays;
                Double proratCharge = dbr * usedDaysValidity;
                chargeHistory.setChargeAmount(proratCharge);
                logger.debug("Setting ChargeAmount in chargeHistory for customer :  "+ customers.getUsername() + " as: " + proratCharge);
            }
        }

        debitDocDetails.setSubtotal(chargeHistory.getChargeAmount());
//        chargeHistory.setTaxAmount(0.0);      //Will not work as set 0 will set 0 in all tax
        taxService.calculateTierTax(chargeHistory,chargeHistory.getTaxId());
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
        customerChargeHistoryRepository.save(chargeHistory);
        debitDocDetails.setTotalamount(debitDocDetails.getSubtotal() - debitDocDetails.getDiscount() + debitDocDetails.getTax());
        logger.debug("Setting Total amount in debitDocDetails with tax for   customer : "+ customers.getUsername() + " as :" + (debitDocDetails.getSubtotal() - debitDocDetails.getDiscount() + debitDocDetails.getTax()));
        return debitDocDetails;
    }

    private LocalDate getNextBilldate(CustomerChargeHistory chargeHistory, Customers customers, boolean isEarlyBillDate) {
        try{
            LocalDate nextBilldate = LocalDate.now();
            if (isEarlyBillDate){
                nextBilldate = customers.getNextBillDate().plusMonths(chargeHistory.getBillingCycle()).withDayOfMonth(customers.getBillday());
            }else {
                nextBilldate = nextBilldate.plusMonths(chargeHistory.getBillingCycle()).withDayOfMonth(customers.getBillday());
            }
            return  nextBilldate;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
