package com.savbill.revenuemanagement.core.service.prepaid;

import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.dto.dbr.AggregateCount;
import com.savbill.revenuemanagement.core.dto.invoice.CustomerDBRPartial;
import com.savbill.revenuemanagement.core.dto.invoice.DebitDocumentCreditNoteView;
import com.savbill.revenuemanagement.core.entity.DBR.CustomDailyRevenue;
import com.savbill.revenuemanagement.core.entity.DBR.CustomMonthlyRevenue;
import com.savbill.revenuemanagement.core.entity.DBR.TempCustomerChargeDBR;
import com.savbill.revenuemanagement.core.entity.DBR.TempCustomerDBR;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.repository.dbr.CustomDailyRevenueRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerChargeDBRRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerDBRRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerServiceMapRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.dbr.CustomMonthlyRevenueRepository;
import com.savbill.revenuemanagement.core.repository.dbr.TempCustomerChargeDBRRepository;
import com.savbill.revenuemanagement.core.repository.dbr.TempCustomerDBRRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocDetailRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.service.SchedulerLockService;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;
import com.savbill.revenuemanagement.productmanagement.PlanService.domain.Services;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.ServiceRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAudit;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * The type Dbr service.
 */
@Service
public class DbrService {

    private static final Logger logger = LoggerFactory.getLogger(DbrService.class);

    @Autowired
    private CustomerChargeDBRRepository chargeDBRRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CustomerDBRRepository customerDBRRepository;

    @Autowired
    private TaxService taxService;

    @Autowired
    private CustomerServiceMapRepository customerServiceMapRepository;

    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;
    @Autowired
    private DebitDocRepository debitDocRepository;

    /**
     * The Custom daily revenue repository.
     */
    @Autowired
    CustomDailyRevenueRepository customDailyRevenueRepository;

    /**
     * The Customer charge dbr repository.
     */
    @Autowired
    CustomerChargeDBRRepository customerChargeDBRRepository;

    @Autowired
    private CustomersRepository customersRepository;

    /**
     * The Custom monthly revenue repository.
     */
    @Autowired
    CustomMonthlyRevenueRepository customMonthlyRevenueRepository;


    /**
     * The Temp customer dbr repository.
     */
    @Autowired
    TempCustomerDBRRepository tempCustomerDBRRepository;

    /**
     * The Temp customer charge dbr repository.
     */
    @Autowired
    TempCustomerChargeDBRRepository tempCustomerChargeDBRRepository;
    /**
     * The Entity manager.
     */
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Autowired
    private SchedulerLockService schedulerLockService;

    /**
     * Add dbr for prepaid customer.
     * @param customerChargeHistories the customer charge histories
     * @param debitDocument the debit document
     * @param customers the customers
     * @param custPlanMapppings the cust plan mapppings
     * @param customerServiceMappings the customer service mappings
     * @param oneTimeCharges the one time charges
     */
    public void addDbrForPrepaidCustomer(List<CustomerChargeHistory> customerChargeHistories, DebitDocument debitDocument, Customers customers, List<CustPlanMappping> custPlanMapppings, List<CustomerServiceMapping> customerServiceMappings, List<CustChargeDetails> oneTimeCharges, boolean trailPlanFromTrailDay) {
        try {
            Long daysDiff = 0L;
            for (CustPlanMappping custPlanMappping : custPlanMapppings) {
                Optional<CustomerServiceMapping> customerServiceMappingOp = customerServiceMappings.stream().filter(custServMap -> custServMap.getId().equals(custPlanMappping.getCustServiceMappingId()) && !Boolean.TRUE.equals(custPlanMappping.getIsInvoiceCreated())).findFirst();
                CustomerServiceMapping customerServiceMapping = null;
                if (customerServiceMappingOp.isPresent())
                    customerServiceMapping = customerServiceMappingOp.get();
                List<CustomerChargeHistory> list = customerChargeHistories.stream().filter(custChargeHist -> custChargeHist.getCustPlanMapppingId().equals(custPlanMappping.getId())
                        && !custChargeHist.getChargeType().equalsIgnoreCase("NON_RECURRING")
                        && !custChargeHist.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_RECURRING)
                        && !custPlanMappping.getIsInvoiceCreated()).collect(Collectors.toList());
                List<CustomerChargeHistory> oneTimelist = customerChargeHistories.stream().filter(custChargeHist -> custChargeHist.getCustPlanMapppingId().equals(custPlanMappping.getId()) && custChargeHist.getChargeType().equalsIgnoreCase("NON_RECURRING")).collect(Collectors.toList());

                List<DebitDocDetails> debitDocDetails = debitDocument.getDebitDocDetailsList().stream()
                        .filter(i -> Long.valueOf(custPlanMappping.getCustServiceMappingId()).equals(i.getCustServiceId())
                                && !i.getChargetype().equalsIgnoreCase("NON_RECURRING")
                                && !i.getChargetype().equalsIgnoreCase("CUSTOMER_DIRECT"))
                        .collect(Collectors.toList());
                Double offerPrice = debitDocDetails.stream().mapToDouble(DebitDocDetails::getSubtotal).sum() - debitDocDetails.stream().mapToDouble(DebitDocDetails::getDiscount).sum();
                Double tmpOfferPrice = offerPrice;
                offerPrice = tmpOfferPrice;

                if (list.size() > 0) {

                    LocalDateTime promiseStartDate = null;
                    LocalDateTime promiseEndDate = null;
                    Long promiseDays = 0l;
                    Double totalGraceAmount = 0.0;
                    Boolean isPromiseToPay = false;
                    Boolean isAfterPromise = false;

                    LocalDate startDate =  LocalDate.from(custPlanMappping.getStartDate());
                    LocalDate endDate = LocalDate.from(custPlanMappping.getExpiryDate());
                    if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                        startDate = trailPlanFromTrailDay ? LocalDate.from(custPlanMappping.getStartDate()) : LocalDate.now();
                        List<CustomerChargeHistory> minCycle = list.stream().sorted(Comparator.comparing(CustomerChargeHistory::getNextBillDate)).collect(Collectors.toList());
                        endDate = minCycle.get(minCycle.size() - 1).getNextBillDate();
                    }
                    daysDiff = ChronoUnit.DAYS.between(startDate, endDate);
                    if (daysDiff < 1)
                        daysDiff = 1L;
                    //TODO: add hour plan calculation
//                    if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
//                        Double hours = plan.get().getValidity();
//                        Double converIntoDays = Math.ceil(hours / 24.0);
//                        daysDiff = converIntoDays.longValue();
//                    }

                    if (custPlanMappping != null && custPlanMappping.getCprIdForPromiseToPay() != null) {
                        CustPlanMappping custMap = custPlanMapppings.stream().filter(custPlan -> custPlan.getId().equals(custPlanMappping.getCprIdForPromiseToPay())).findAny().get();//custPlanMapppingRepository.findB(custPlanMappping.getCprIdForPromiseToPay());
                        if (custMap != null && custMap.getGraceDays() != null && custMap.getGraceDays() > 0 && custMap.getPromise_to_pay_startdate() != null && custMap.getPromise_to_pay_enddate() != null) {
                            isPromiseToPay = true;
                            promiseStartDate = custMap.getPromise_to_pay_startdate();
                            promiseEndDate = custMap.getPromise_to_pay_enddate();
                            promiseDays = custMap.getGraceDays().longValue();
                        }
                    }

                    Double dbr = tmpOfferPrice / daysDiff;
                    Double cummulativeRevenue = 0d;
                    DecimalFormat df = new DecimalFormat("0.00");
                    if (promiseStartDate != null && promiseEndDate != null && isPromiseToPay) {
                        if (isPromiseToPay && LocalDate.now().isAfter(promiseStartDate.toLocalDate()) && (LocalDate.now().isBefore(promiseEndDate.toLocalDate())) || LocalDate.now().equals(promiseEndDate.toLocalDate())) {
                            promiseDays = ChronoUnit.DAYS.between(promiseStartDate.toLocalDate(), LocalDate.now());
                            if (LocalDate.now().equals(promiseEndDate.toLocalDate()))
                                promiseDays = promiseDays - 1;
                            totalGraceAmount = dbr * promiseDays;
                            daysDiff = daysDiff - promiseDays;
                            endDate = endDate.minusDays(promiseDays);
                        } else if (isPromiseToPay && LocalDate.now().isAfter(promiseStartDate.toLocalDate()) && LocalDate.now().isAfter(promiseEndDate.toLocalDate())) {
                            totalGraceAmount = dbr * promiseDays;
                            daysDiff = daysDiff - promiseDays;
                            endDate = endDate.minusDays(promiseDays);
                            isAfterPromise = true;
                        }
                    }


//                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
//                    String convertedNumber = decimalFormat.format(dbr);
//                    Double totalOfferpriceForroundingOff = Double.parseDouble(convertedNumber) * daysDiff;
//                    Double diff = totalOfferpriceForroundingOff-offerPrice;

                    List<CustomerDBR> dbrs = new ArrayList<>();
                    for (int i = 0; i < daysDiff; i++) {
                        if (isPromiseToPay) {
                            tmpOfferPrice = tmpOfferPrice - dbr - totalGraceAmount;
                            cummulativeRevenue = cummulativeRevenue + dbr + totalGraceAmount;
                        } else {
                            tmpOfferPrice = tmpOfferPrice - dbr;
                            cummulativeRevenue = cummulativeRevenue + dbr;
                        }
                        CustomerDBR customerDBR = new CustomerDBR();
                        customerDBR.setInvoiceId(Long.valueOf(debitDocument.getId()));
                        customerDBR.setCprid(Long.valueOf(custPlanMappping.getId()));
                        customerDBR.setCustid(Long.valueOf(customers.getId()));
                        customerDBR.setPlanid(Long.valueOf(custPlanMappping.getPlanId()));
                        customerDBR.setCustname(customers.getUsername());
//                        customerDBR.setPlanname(custPlanMappping.getPla);
                        customerDBR.setCusttype(customers.getCusttype());
                        customerDBR.setValidity_days(daysDiff.intValue());
                        customerDBR.setOffer_price(Double.parseDouble(df.format(offerPrice)));
                        customerDBR.setStartdate(LocalDate.from(startDate.plusDays(i)));
                        customerDBR.setStatus("Active");
                        customerDBR.setEnddate(endDate);

                        if (isPromiseToPay) {
                            Double dbr1 = dbr + totalGraceAmount;
                            customerDBR.setDbr(Double.parseDouble(dbr1.toString()));
                        } else
                            customerDBR.setDbr(Double.parseDouble(dbr.toString()));

                        customerDBR.setIsDirectCharge(false);
                        customerDBR.setPendingamt(Double.parseDouble(tmpOfferPrice.toString()));
                        customerDBR.setCumm_revenue(cummulativeRevenue);
                        if (isPromiseToPay)
                            customerDBR.setRemark("Promise To Pay Adjusted for amount " + Double.parseDouble(df.format(totalGraceAmount)) + " for " + promiseDays + " Days");
                        else
                            customerDBR.setRemark("");
                        if (customerServiceMapping != null)
                            customerDBR.setServiceId(customerServiceMapping.getServiceId());
                        else
                            customerDBR.setServiceId(null);
                        customerDBR.setServiceArea(customers.getServiceAreaId());
                        customerDBR.setBuId(customers.getBuId());
                        customerDBR.setMvnoId(customers.getMvnoId());
                        dbrs.add(customerDBR);
                        //customerDBRRepository.save(customerDBR);
                        isPromiseToPay = false;

                    }
                    customerDBRRepository.saveAll(dbrs);
                    addDbrForPrepaidCustomerForChargeLevel(list, debitDocument, customers, custPlanMappping, customerServiceMapping, trailPlanFromTrailDay);
                }

                if (!CollectionUtils.isEmpty(oneTimelist)) {
                    addOneTimeEntryForPrepaidIntoDBR(oneTimelist, customers, daysDiff, debitDocument.getId().longValue(), customerServiceMapping.getServiceId());
                }

                if (!CollectionUtils.isEmpty(oneTimeCharges)) {
                    addOneTimeEntryForPrepaidIntoDBR(oneTimeCharges, customers, debitDocument, daysDiff.intValue(), customerServiceMapping.getServiceId());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while add customer dbr: " + ex.getMessage());
        }
    }


    /**
     * Add dbr for postpaid customer.
     * @param customerChargeHistories the customer charge histories
     * @param debitDocument the debit document
     * @param customers the customers
     * @param custPlanMapppings the cust plan mapppings
     * @param customerServiceMappings the customer service mappings
     * @param oneTimeCharges the one time charges
     */
    public void addDbrForPostpaidCustomer(List<CustomerChargeHistory> customerChargeHistories, DebitDocument debitDocument, Customers customers, List<CustPlanMappping> custPlanMapppings, List<CustomerServiceMapping> customerServiceMappings, List<CustChargeDetails> oneTimeCharges) {
        try {
            Long daysDiff = 0L;
            for (CustPlanMappping custPlanMappping : custPlanMapppings) {
                CustomerServiceMapping customerServiceMapping = customerServiceMappings.stream().filter(custServMap -> custServMap.getId().equals(custPlanMappping.getCustServiceMappingId())).findFirst().get();
                List<CustomerChargeHistory> list = customerChargeHistories.stream().filter(custChargeHist -> custChargeHist.getCustPlanMapppingId().equals(custPlanMappping.getId())
                        && !custChargeHist.getChargeType().equalsIgnoreCase("NON_RECURRING") &&
                        !custChargeHist.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_ADVANCE)).collect(Collectors.toList());
                List<CustomerChargeHistory> oneTimelist = customerChargeHistories.stream().filter(custChargeHist -> custChargeHist.getCustPlanMapppingId().equals(custPlanMappping.getId()) && custChargeHist.getChargeType().equalsIgnoreCase("NON_RECURRING")).collect(Collectors.toList());

                Double offerPrice = debitDocument.getDebitDocDetailsList().stream().mapToDouble(i -> i.getSubtotal()).sum() - debitDocument.getDebitDocDetailsList().stream().mapToDouble(i -> i.getDiscount()).sum();

                if (list.size() > 0) {
                    LocalDate startDate = LocalDate.from(custPlanMappping.getStartDate());
                    LocalDate endDate = LocalDate.from(custPlanMappping.getExpiryDate());
                    daysDiff = ChronoUnit.DAYS.between(startDate, endDate);
                    if (daysDiff < 1)
                        daysDiff = 1L;

                    Double cummulativeRevenue = offerPrice;
                    DecimalFormat df = new DecimalFormat("0.00");

                    CustomerDBR customerDBR = new CustomerDBR();
                    customerDBR.setInvoiceId(Long.valueOf(debitDocument.getId()));
                    customerDBR.setCprid(Long.valueOf(custPlanMappping.getId()));
                    customerDBR.setCustid(Long.valueOf(customers.getId()));
                    customerDBR.setPlanid(Long.valueOf(custPlanMappping.getPlanId()));
                    customerDBR.setCustname(customers.getUsername());
                    customerDBR.setCusttype(customers.getCusttype());
                    customerDBR.setValidity_days(daysDiff.intValue());
                    customerDBR.setOffer_price(Double.parseDouble(df.format(offerPrice)));
                    customerDBR.setStartdate(LocalDate.now());
                    customerDBR.setStatus("Active");
                    customerDBR.setEnddate(LocalDate.now());
                    customerDBR.setDbr(offerPrice);
                    customerDBR.setIsDirectCharge(false);
                    customerDBR.setPendingamt(0.0);
                    customerDBR.setCumm_revenue(cummulativeRevenue);
                    customerDBR.setRemark("");
                    if (customerServiceMapping != null)
                        customerDBR.setServiceId(customerServiceMapping.getServiceId());
                    else
                        customerDBR.setServiceId(null);
                    customerDBR.setServiceArea(customers.getServiceAreaId());
                    customerDBR.setBuId(customers.getBuId());
                    customerDBR.setMvnoId(customers.getMvnoId());

                    customerDBRRepository.save(customerDBR);

                    addDbrForPostpaidCustomerForChargeLevel(list, debitDocument, customers, custPlanMappping, customerServiceMapping);
                }

                if (!CollectionUtils.isEmpty(oneTimelist)) {
                    addOneTimeEntryForPrepaidIntoDBR(oneTimelist, customers, daysDiff, debitDocument.getId().longValue(), customerServiceMapping.getServiceId());
                }

                if (!CollectionUtils.isEmpty(oneTimeCharges)) {
                    addOneTimeEntryForPrepaidIntoDBR(oneTimeCharges, customers, debitDocument, daysDiff.intValue(), customerServiceMapping.getServiceId());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while add customer dbr: " + ex.getMessage());
        }
    }

    /**
     * Add one time entry for prepaid into dbr.
     * @param oneTimeCharges the one time charges
     * @param customers the customers
     * @param debitDocument the debit document
     * @param validityInDays the validity in days
     * @param serviceId the service id
     */
    public void addOneTimeEntryForPrepaidIntoDBR(List<CustChargeDetails> oneTimeCharges, Customers customers, DebitDocument debitDocument, Integer validityInDays, Long serviceId) {
        ApplicationLogger.logger.info("Start addOneTimeEntryForPrepaidIntoDBR() ", 200, oneTimeCharges);
        try {
            for (int i = 0; i < oneTimeCharges.size(); i++) {
                DecimalFormat df = new DecimalFormat("0.00");
                CustomerDBR customerDBR = new CustomerDBR();
                customerDBR.setInvoiceId(Long.valueOf(debitDocument.getId()));
                customerDBR.setCprid(Long.valueOf(debitDocument.getCustpackrelid()));
                customerDBR.setCustid(Long.valueOf(customers.getId()));
                if (oneTimeCharges.get(i).getPlanid() != null)
                    customerDBR.setPlanid(Long.valueOf(oneTimeCharges.get(i).getPlanid()));
                customerDBR.setCustname(customers.getUsername());
                customerDBR.setCusttype(customers.getCusttype());
                customerDBR.setValidity_days(validityInDays.intValue());
                customerDBR.setOffer_price(Double.parseDouble(df.format(oneTimeCharges.get(i).getPrice() - oneTimeCharges.get(i).getDiscount())));//+oneTimeCharges.get(i).getTax())));
                customerDBR.setStartdate(LocalDate.now());
                customerDBR.setStatus("Active");
                customerDBR.setEnddate(LocalDate.now());
                customerDBR.setDbr(Double.parseDouble(df.format(oneTimeCharges.get(i).getPrice() - oneTimeCharges.get(i).getDiscount())));//+oneTimeCharges.get(i).getTax())));
                customerDBR.setIsDirectCharge(true);
                customerDBR.setPendingamt(0.0);
                customerDBR.setCumm_revenue(oneTimeCharges.get(i).getPrice() - oneTimeCharges.get(i).getDiscount());
                customerDBR.setServiceId(serviceId);
                customerDBR.setRemark("Onetime Charge Added");

                customerDBR.setServiceArea(customers.getServiceAreaId());
                customerDBR.setBuId(customers.getBuId());
                customerDBR.setMvnoId(customers.getMvnoId());

                customerDBRRepository.save(customerDBR);
                addOneTimeEntryForPrepaidIntoChargeDBR(oneTimeCharges, customers, debitDocument, Long.valueOf(validityInDays), serviceId);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }
        ApplicationLogger.logger.info("End addOneTimeEntryForPrepaidIntoDBR() ", 200, oneTimeCharges);
    }


    /**
     * Add one time entry for prepaid into charge dbr.
     * @param custChargeDetailsList the cust charge details list
     * @param customers the customers
     * @param debitDocument the debit document
     * @param validityInDays the validity in days
     * @param serviceId the service id
     */
    public void addOneTimeEntryForPrepaidIntoChargeDBR(List<CustChargeDetails> custChargeDetailsList, Customers customers, DebitDocument debitDocument, Long validityInDays, Long serviceId) {
        try {
            for (CustChargeDetails data : custChargeDetailsList) {
                CustomerChargeDBR customerDBR = new CustomerChargeDBR();
                DecimalFormat df = new DecimalFormat("0.00");
                if (serviceId == null && data.getConnection_no() != null) {
                    serviceId = customerServiceMapRepository.findServiceIdFromConnectionNo(data.getConnection_no(), data.getCustomer().getId());
                }
                customerDBR.setInvoiceId(Long.valueOf(debitDocument.getId()));
                customerDBR.setChargeId(Long.valueOf(data.getChargeid()));
                if (debitDocument.getCustpackrelid() != null)
                    customerDBR.setCprid(Long.valueOf(debitDocument.getCustpackrelid()));
                customerDBR.setCustid(Long.valueOf(customers.getId()));
                if (data.getPlanid() != null)
                    customerDBR.setPlanid(Long.valueOf(data.getPlanid()));
                customerDBR.setCustname(customers.getUsername());
                customerDBR.setCusttype(customers.getCusttype());
                customerDBR.setValidity_days(validityInDays.intValue());
                customerDBR.setOffer_price(Double.parseDouble(df.format(data.getPrice() - data.getDiscount())));
                customerDBR.setStartdate(LocalDate.now());
                customerDBR.setStatus("Active");
                customerDBR.setEnddate(LocalDate.now());
                customerDBR.setDbr(Double.parseDouble(df.format(data.getPrice() - data.getDiscount())));
                customerDBR.setIsDirectCharge(true);
                customerDBR.setPendingamt(0.0);
                customerDBR.setCumm_revenue(data.getPrice() - data.getDiscount());
                customerDBR.setServiceId(serviceId);
                customerDBR.setRemark("Onetime Charge Added");
                customerDBR.setServiceArea(customers.getServiceAreaId());
                customerDBR.setBuId(customers.getBuId());
                customerDBR.setMvnoId(customers.getMvnoId());
                chargeDBRRepository.save(customerDBR);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while add one time dbr for charge level: " + ex.getMessage());
        }
    }

    /**
     * Add dbr for prepaid customer for charge level.
     * @param list the list
     * @param debitDocument the debit document
     * @param customers the customers
     * @param custPlanMap the cust plan map
     * @param customerServiceMapping the customer service mapping
     */
    public void addDbrForPrepaidCustomerForChargeLevel(List<CustomerChargeHistory> list, DebitDocument debitDocument, Customers customers, CustPlanMappping custPlanMap, CustomerServiceMapping customerServiceMapping, boolean trailPlanFromTrailDay) {

        try {
            if (!CollectionUtils.isEmpty(list)) {

                list.stream().forEach(data -> {
                    LocalDateTime promiseStartDate = null;
                    LocalDateTime promiseEndDate = null;
                    Long promiseDays = 0l;
                    Double totalGraceAmount = 0.0;
                    Boolean isPromiseToPay = false;
                    Boolean isAfterPromise = false;

                    LocalDate startDate = LocalDate.from(custPlanMap.getStartDate());//Instant.ofEpochMilli(custPlanMapppings.get(0).getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate endDate = LocalDate.from(custPlanMap.getExpiryDate());//Instant.ofEpochMilli(custPlanMapppings.get(0).getPlanExpireDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                        startDate = trailPlanFromTrailDay ? LocalDate.from(custPlanMap.getStartDate()) : LocalDate.now();
                        endDate = data.getNextBillDate();
                    }
                    Long daysDiff = ChronoUnit.DAYS.between(startDate, endDate);
                    if (daysDiff < 1)
                        daysDiff = 1L;
                    //TODO: Add dbr for hours plan;
//                if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
//                    Double hours = plan.get().getValidity();
//                    Double converIntoDays = Math.ceil(hours / 24.0);
//                    daysDiff = converIntoDays.longValue();
//                }


                    //TODO: Add dbr for P2P;
                    if (custPlanMap != null && custPlanMap.getCprIdForPromiseToPay() != null) {
//                        CustPlanMappping custMap=custPlanMapppings.stream().filter(custPlan -> custPlan.getId().equals(custPlanMap.getCprIdForPromiseToPay())).findAny().get();//custPlanMapppingRepository.findB(custPlanMappping.getCprIdForPromiseToPay());
//                        if(custMap!=null && custMap.getGraceDays()!=null && custMap.getGraceDays()>0 && custMap.getPromise_to_pay_startdate()!=null && custMap.getPromise_to_pay_enddate()!=null)
//                        {
//                            isPromiseToPay=true;
//                            promiseStartDate=custMap.getPromise_to_pay_startdate();
//                            promiseEndDate=custMap.getPromise_to_pay_enddate();
//                            promiseDays=custMap.getGraceDays().longValue();
//                        }
                    }
                    Double tmpOfferPrice = debitDocument.getDebitDocDetailsList().stream().filter(debitDocDetails -> debitDocDetails.getChargeid().equals(data.getChargeId())).map(DebitDocDetails::getSubtotal).findFirst().get();
//                    Double tmpOfferPrice = data.getChargeAmount()-data.getDiscount();
                    Double dbr = tmpOfferPrice / daysDiff;
                    Double cummulativeRevenue = 0d;
                    DecimalFormat df = new DecimalFormat("0.00");

                    if (isPromiseToPay && promiseStartDate != null && promiseEndDate != null) {

                        if (isPromiseToPay && LocalDate.now().isAfter(promiseStartDate.toLocalDate()) && (LocalDate.now().isBefore(promiseEndDate.toLocalDate())) || LocalDate.now().equals(promiseEndDate.toLocalDate())) {
                            promiseDays = ChronoUnit.DAYS.between(promiseStartDate.toLocalDate(), LocalDate.now());
                            if (LocalDate.now().equals(promiseEndDate.toLocalDate()))
                                promiseDays = promiseDays - 1;
                            totalGraceAmount = dbr * promiseDays;
                            daysDiff = daysDiff - promiseDays;
                            endDate = endDate.minusDays(promiseDays);
                        } else if (isPromiseToPay && LocalDate.now().isAfter(promiseStartDate.toLocalDate()) && LocalDate.now().isAfter(promiseEndDate.toLocalDate())) {
                            totalGraceAmount = dbr * promiseDays;
                            daysDiff = daysDiff - promiseDays;
                            endDate = endDate.minusDays(promiseDays);
                            isAfterPromise = true;
                        }
                    }

                    List<CustomerChargeDBR> dbrs = new ArrayList<>();
                    for (int i = 0; i < daysDiff; i++) {
                        if (isPromiseToPay) {
                            tmpOfferPrice = tmpOfferPrice - dbr - totalGraceAmount;
                            cummulativeRevenue = cummulativeRevenue + dbr + totalGraceAmount;
                        } else {
                            tmpOfferPrice = tmpOfferPrice - dbr;
                            cummulativeRevenue = cummulativeRevenue + dbr;
                        }
                        CustomerChargeDBR customerDBR = new CustomerChargeDBR();
                        customerDBR.setChargeId(Long.valueOf(data.getChargeId()));
                        customerDBR.setInvoiceId(Long.valueOf(debitDocument.getId()));
                        customerDBR.setCprid(Long.valueOf(data.getCustPlanMapppingId()));
                        customerDBR.setCustid(Long.valueOf(customers.getId()));
                        customerDBR.setPlanid(Long.valueOf(custPlanMap.getPlanId()));
                        customerDBR.setCustname(customers.getUsername());
//                    customerDBR.setPlanname(list.get(0).getPlanname());
                        customerDBR.setCusttype(customers.getCusttype());
                        customerDBR.setValidity_days(daysDiff.intValue());
                        customerDBR.setOffer_price(Double.parseDouble(df.format(tmpOfferPrice)));
                        customerDBR.setStartdate(LocalDate.from(startDate.plusDays(i)));
                        customerDBR.setStatus("Active");
                        customerDBR.setEnddate(endDate);
                        if (isPromiseToPay) {
                            Double dbr1 = dbr + totalGraceAmount;
                            customerDBR.setDbr(Double.parseDouble(dbr1.toString()));
                        } else
                            customerDBR.setDbr(Double.parseDouble(dbr.toString()));
                        customerDBR.setIsDirectCharge(false);
                        customerDBR.setPendingamt(Double.parseDouble(tmpOfferPrice.toString()));
                        customerDBR.setCumm_revenue(cummulativeRevenue);
                        customerDBR.setServiceId(customerServiceMapping.getServiceId());
                        customerDBR.setRemark("");
                        //TODO: Add serviceArea
                        customerDBR.setServiceArea(customers.getServiceAreaId());
                        customerDBR.setBuId(customers.getBuId());
                        customerDBR.setMvnoId(customers.getMvnoId());

                        dbrs.add(customerDBR);
                        //chargeDBRRepository.save(customerDBR);
                        isPromiseToPay = false;
                    }
                    chargeDBRRepository.saveAll(dbrs);
                    //taxService.getTaxAmountFromCharge(debitDocument,data.getChargeId());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while add customer charge dbr: " + ex.getMessage());
        }
    }


    /**
     * Add dbr for postpaid customer for charge level.
     * @param list the list
     * @param debitDocument the debit document
     * @param customers the customers
     * @param custPlanMap the cust plan map
     * @param customerServiceMapping the customer service mapping
     */
    public void addDbrForPostpaidCustomerForChargeLevel(List<CustomerChargeHistory> list, DebitDocument debitDocument, Customers customers, CustPlanMappping custPlanMap, CustomerServiceMapping customerServiceMapping) {

        try {
            if (!CollectionUtils.isEmpty(list)) {

                list.stream().forEach(data -> {
                    LocalDate startDate = LocalDate.from(custPlanMap.getStartDate());//Instant.ofEpochMilli(custPlanMapppings.get(0).getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate endDate = LocalDate.from(custPlanMap.getExpiryDate());//Instant.ofEpochMilli(custPlanMapppings.get(0).getPlanExpireDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    Long daysDiff = ChronoUnit.DAYS.between(startDate, endDate);
                    if (daysDiff < 1)
                        daysDiff = 1L;


                    Double tmpOfferPrice = data.getChargeAmount() - data.getDiscount();
                    Double cummulativeRevenue = tmpOfferPrice;
                    DecimalFormat df = new DecimalFormat("0.00");

                    CustomerChargeDBR customerDBR = new CustomerChargeDBR();
                    customerDBR.setChargeId(Long.valueOf(data.getChargeId()));
                    customerDBR.setInvoiceId(Long.valueOf(debitDocument.getId()));
                    customerDBR.setCprid(Long.valueOf(data.getCustPlanMapppingId()));
                    customerDBR.setCustid(Long.valueOf(customers.getId()));
                    customerDBR.setPlanid(Long.valueOf(custPlanMap.getPlanId()));
                    customerDBR.setCustname(customers.getUsername());
                    customerDBR.setCusttype(customers.getCusttype());
                    customerDBR.setValidity_days(daysDiff.intValue());
                    customerDBR.setOffer_price(Double.parseDouble(df.format(tmpOfferPrice)));
                    customerDBR.setStartdate(LocalDate.now());
                    customerDBR.setStatus("Active");
                    customerDBR.setEnddate(LocalDate.now());
                    customerDBR.setDbr(tmpOfferPrice);
                    customerDBR.setIsDirectCharge(false);
                    customerDBR.setPendingamt(0.0);
                    customerDBR.setCumm_revenue(cummulativeRevenue);
                    customerDBR.setServiceId(customerServiceMapping.getServiceId());
                    customerDBR.setRemark("");
                    customerDBR.setServiceArea(customers.getServiceAreaId());
                    customerDBR.setBuId(customers.getBuId());
                    customerDBR.setMvnoId(customers.getMvnoId());
                    chargeDBRRepository.save(customerDBR);
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while add customer charge dbr: " + ex.getMessage());
        }
    }

    /**
     * Add dbr for org customer prepaid.
     * @param customers the customers
     * @param oneTimeCharges the one time charges
     * @param debitDocument the debit document
     * @param custPlanMapppings the cust plan mapppings
     * @param customerChargeHistories the customer charge histories
     * @param customerServiceMappings the customer service mappings
     */
    public void addDbrForOrgCustomerPrepaid(Customers customers, List<CustChargeDetails> oneTimeCharges, DebitDocument debitDocument, List<CustPlanMappping> custPlanMapppings, List<CustomerChargeHistory> customerChargeHistories, List<CustomerServiceMapping> customerServiceMappings) {
        try {
            Long daysDiff = 0L;
            for (CustPlanMappping custPlanMappping : custPlanMapppings) {
                CustomerServiceMapping customerServiceMapping = customerServiceMapRepository.findById(custPlanMappping.getCustServiceMappingId()).get();//customerServiceMappings.stream().filter(custServMap -> custServMap.getId().equals(custPlanMappping.getCustServiceMappingId())).findFirst().get();
                List<CustomerChargeHistory> list = customerChargeHistories.stream().filter(custChargeHist -> custChargeHist.getCustPlanMapppingId().equals(custPlanMappping.getId())).collect(Collectors.toList());
                Double offerPrice1 = list.stream().mapToDouble(y -> y.getChargeAmount()).sum() + list.stream().mapToDouble(y -> y.getTaxAmount()).sum();

                Double offerPrice = list.stream().mapToDouble(y -> y.getChargeAmount()).sum();
                Double tmpOfferPrice = offerPrice;
                offerPrice = tmpOfferPrice;
                //TODO: add partner commission
//                partnerCommissionService.partnerCommissionForPrepaidCustomerCreation(message.getTotalInvoiceAmount(), offerPrice1, list, customers.get(), staffUser, message.getInvoiceId(), paymentStatusForFranCustomer);


                if (list.size() > 0) {

                    LocalDateTime promiseStartDate = null;
                    LocalDateTime promiseEndDate = null;
                    Long promiseDays = 0l;
                    Double totalGraceAmount = 0.0;
                    Boolean isPromiseToPay = false;
                    Boolean isAfterPromise = false;

                    LocalDate startDate = LocalDate.from(custPlanMappping.getStartDate());
                    LocalDate endDate = LocalDate.from(custPlanMappping.getExpiryDate());
                    daysDiff = ChronoUnit.DAYS.between(startDate, endDate);
                    if (daysDiff < 1)
                        daysDiff = 1L;
                    //TODO: add hour plan calculation
//                    if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
//                        Double hours = plan.get().getValidity();
//                        Double converIntoDays = Math.ceil(hours / 24.0);
//                        daysDiff = converIntoDays.longValue();
//                    }

                    if (custPlanMappping != null && custPlanMappping.getCprIdForPromiseToPay() != null) {
                        CustPlanMappping custMap = custPlanMapppings.stream().filter(custPlan -> custPlan.getId().equals(custPlanMappping.getCprIdForPromiseToPay())).findAny().get();//custPlanMapppingRepository.findB(custPlanMappping.getCprIdForPromiseToPay());
                        if (custMap != null && custMap.getGraceDays() != null && custMap.getGraceDays() > 0 && custMap.getPromise_to_pay_startdate() != null && custMap.getPromise_to_pay_enddate() != null) {
                            isPromiseToPay = true;
                            promiseStartDate = custMap.getPromise_to_pay_startdate();
                            promiseEndDate = custMap.getPromise_to_pay_enddate();
                            promiseDays = custMap.getGraceDays().longValue();
                        }
                    }

                    Double dbr = tmpOfferPrice / daysDiff;
                    Double cummulativeRevenue = 0d;
                    DecimalFormat df = new DecimalFormat("0.00");
                    if (promiseStartDate != null && promiseEndDate != null && isPromiseToPay) {
                        if (isPromiseToPay && LocalDate.now().isAfter(promiseStartDate.toLocalDate()) && (LocalDate.now().isBefore(promiseEndDate.toLocalDate())) || LocalDate.now().equals(promiseEndDate.toLocalDate())) {
                            promiseDays = ChronoUnit.DAYS.between(promiseStartDate.toLocalDate(), LocalDate.now());
                            if (LocalDate.now().equals(promiseEndDate.toLocalDate()))
                                promiseDays = promiseDays - 1;
                            totalGraceAmount = dbr * promiseDays;
                            daysDiff = daysDiff - promiseDays;
                            endDate = endDate.minusDays(promiseDays);
                        } else if (isPromiseToPay && LocalDate.now().isAfter(promiseStartDate.toLocalDate()) && LocalDate.now().isAfter(promiseEndDate.toLocalDate())) {
                            totalGraceAmount = dbr * promiseDays;
                            daysDiff = daysDiff - promiseDays;
                            endDate = endDate.minusDays(promiseDays);
                            isAfterPromise = true;
                        }
                    }

                    CustomerDBR customerDBR = new CustomerDBR();
                    customerDBR.setInvoiceId(Long.valueOf(debitDocument.getId()));
                    customerDBR.setCprid(Long.valueOf(custPlanMappping.getId()));
                    customerDBR.setCustid(Long.valueOf(customers.getId()));
                    customerDBR.setPlanid(Long.valueOf(custPlanMappping.getPlanId()));
                    customerDBR.setCustname(customers.getUsername());
//                    customerDBR.setPlanname(custPlanMappping.get);
                    customerDBR.setCusttype(customers.getCusttype());
                    customerDBR.setValidity_days(daysDiff.intValue());
                    customerDBR.setOffer_price(Double.parseDouble(df.format(offerPrice)));
                    customerDBR.setStartdate(LocalDate.from(startDate));
                    customerDBR.setStatus("Active");
                    customerDBR.setEnddate(endDate);
                    customerDBR.setDbr(offerPrice);
                    customerDBR.setIsDirectCharge(false);
                    customerDBR.setPendingamt(Double.parseDouble(df.format(tmpOfferPrice)));
                    customerDBR.setCumm_revenue(tmpOfferPrice);
                    customerDBR.setServiceId(customerServiceMapping.getServiceId());
                    customerDBR.setRemark("");

                    customerDBR.setServiceArea(customers.getServiceAreaId());
                    customerDBR.setBuId(customers.getBuId());
                    customerDBR.setMvnoId(customers.getMvnoId());
                    customerDBRRepository.save(customerDBR);
                    addDbrForPrepaidCustomerForChargeLevel(customerChargeHistories, debitDocument, customers, custPlanMappping, customerServiceMapping, false);
                }
                if (!CollectionUtils.isEmpty(oneTimeCharges)) {
                    addOneTimeEntryForPrepaidIntoDBR(oneTimeCharges, customers, debitDocument, daysDiff.intValue(), customerServiceMapping.getServiceId());
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while add customer dbr: " + ex.getMessage());
        }
    }

    /**
     * Add dbr entry for CN
     * @param document the document
     * @param invoiceId the invoice id
     * @param creditAmountExcludeTax the credit amount exclude tax
     * @param type the type
     * @param chargeType the charge type
     */
    public void addDbrEntry(DebitDocument document, Long invoiceId, Double creditAmountExcludeTax, String type, String chargeType) {
        DecimalFormat df = new DecimalFormat("0.00");
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId());
        map.entrySet().stream().forEach(data -> {
            Double amount = (creditAmountExcludeTax);
            if (!Double.isInfinite(data.getValue()))
                amount = (creditAmountExcludeTax * data.getValue()) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            if (type != null && chargeType != null && type.equalsIgnoreCase(Constants.INVOICE_TYPE.CANCEL_REGENERATE) && chargeType.equalsIgnoreCase("CUSTOMER_DIRECT")) {
                dbr.setDbr(-(creditAmountExcludeTax));
                dbr.setPendingamt(creditAmountExcludeTax);
                dbr.setCumm_revenue(-(creditAmountExcludeTax));
            } else {
                dbr.setDbr(0d);
                dbr.setPendingamt(amount);
                dbr.setCumm_revenue(0d);
            }

            dbr.setPartnerId(null);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            if (data.getKey().longValue() > 0)
                dbr.setServiceId(data.getKey().longValue());
            else
                dbr.setServiceId(0l);
            dbr.setRemark(df.format(amount) + " CreditNote Adjusted for " + document.getDocnumber() + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            try {
                customerDBRRepository.save(dbr);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("err: " + ex.getMessage());
            }
            addDbrEntryAtChargeLevel(document, invoiceId, amount, data.getKey().longValue());
        });
    }

    private void addDbrEntryAtChargeLevel(DebitDocument document, Long invoiceId, Double creditAmountExcludeTax, long serviceId) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            Map<Integer, Double> map = getChargeWiseRatioForInvoiceAmount(document.getId(), serviceId);
            map.entrySet().stream().forEach(data -> {
                CustomerChargeDBR dbr = new CustomerChargeDBR();
                dbr.setChargeId(data.getKey().longValue());
                dbr.setInvoiceId(invoiceId);
                dbr.setCustid(document.getCustomer().getId().longValue());
                dbr.setStartdate(LocalDate.now());
                dbr.setEnddate(LocalDate.now());
                dbr.setDbr(0.0);
                dbr.setPendingamt((creditAmountExcludeTax * data.getValue()) / 100.0d);
                dbr.setCustname(document.getCustomer().getCustname());
                dbr.setStatus("Active");
                dbr.setCusttype(document.getCustomer().getCustomerType());
                dbr.setIsDirectCharge(false);
                dbr.setCumm_revenue(0.0);
                dbr.setServiceId(serviceId);
                dbr.setRemark(df.format(((creditAmountExcludeTax * data.getValue()) / 100.0d)) + " CreditNote Adjusted for " + document.getCustomer().getServiceAreName() + " Service");
                dbr.setServiceArea(document.getCustomer().getServiceAreaId());
                dbr.setBuId(document.getCustomer().getBuId());
                dbr.setMvnoId(document.getCustomer().getMvnoId());
                chargeDBRRepository.save(dbr);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Gets service wise ratio for invoice amount.
     * @param invoiceId the invoice id
     * @return the service wise ratio for invoice amount
     */
    public Map<Integer, Double> getServiceWiseRatioForInvoiceAmount(Integer invoiceId) {
        Optional<DebitDocument> debitDocument = debitDocRepository.findById(invoiceId);
        AtomicReference<Double> invoiceAmountWithoutTax = new AtomicReference<>(0d);
        Map<Integer, Double> serviceWiseDbrs = new HashMap<>();
        List<DebitDocDetails> docDetails = debitDocDetailRepository.findAllByDebitdocumentid(invoiceId);
        if (docDetails != null && !docDetails.isEmpty()) {
            docDetails.stream().forEach(data -> {
                if (data.getPlanId() != null && debitDocument.get().getCustpackrelid() != null && debitDocument.get().getCustpackrelid() != 0) {
                    Integer serviceId = custPlanMapppingRepository.getServiceIdFromCustPlanMappingId(debitDocument.get().getCustpackrelid()).intValue();
                    invoiceAmountWithoutTax.updateAndGet(v -> v + (data.getSubtotal() + data.getDiscount()));
                    if (serviceId != null) {
                        if (serviceWiseDbrs.containsKey(serviceId))
                            serviceWiseDbrs.put(serviceId, serviceWiseDbrs.get(serviceId) + data.getSubtotal() + data.getDiscount());
                        else
                            serviceWiseDbrs.put(serviceId, data.getSubtotal() + data.getDiscount());
                    }
                } else {
                    if (!serviceWiseDbrs.containsKey(-1))
                        serviceWiseDbrs.put(-1, data.getSubtotal() + data.getDiscount());
                    else {
                        Double value = serviceWiseDbrs.get(-1);
                        if (value != null)
                            serviceWiseDbrs.put(-1, serviceWiseDbrs.get(-1) + data.getSubtotal() + data.getDiscount());

                        else
                            serviceWiseDbrs.put(-1, data.getSubtotal() + data.getDiscount());
                    }
                }
            });
        }

        serviceWiseDbrs.entrySet().stream().forEach(data -> {
            Double planAmount = data.getValue();
            Double invoiceAmount = invoiceAmountWithoutTax.get();
            Double percentageRatio = (planAmount / invoiceAmount) * 100.00d;
            data.setValue(percentageRatio);
        });
        return serviceWiseDbrs;
    }

    /**
     * Gets charge wise ratio for invoice amount.
     * @param invoiceId the invoice id
     * @param serviceId the service id
     * @return the charge wise ratio for invoice amount
     */
    public Map<Integer, Double> getChargeWiseRatioForInvoiceAmount(Integer invoiceId, Long serviceId) {
        Optional<DebitDocument> debitDocument = debitDocRepository.findById(invoiceId);
        AtomicReference<Double> invoiceAmountWithoutTax = new AtomicReference<>(0d);
        Map<Integer, Double> serviceWiseDbrs = new HashMap<>();
        List<DebitDocDetails> docDetails = debitDocDetailRepository.findAllByDebitdocumentid(invoiceId);
        if (docDetails != null && !docDetails.isEmpty()) {
            docDetails.stream().forEach(data ->
            {
                if (data.getPlanId() != null && debitDocument.get().getCustpackrelid() != null) {
                    invoiceAmountWithoutTax.updateAndGet(v -> v + (data.getSubtotal() + data.getDiscount()));

                    if (serviceWiseDbrs.containsKey(data.getChargeid()))
                        serviceWiseDbrs.put(data.getChargeid(), serviceWiseDbrs.get(data.getChargeid()) + data.getSubtotal() + data.getDiscount());
                    else
                        serviceWiseDbrs.put(data.getChargeid(), data.getSubtotal() + data.getDiscount());

                } else if (serviceId.intValue() == -1) {
                    if (serviceWiseDbrs.containsKey(-1))
                        serviceWiseDbrs.put(-1, data.getSubtotal() + data.getDiscount());
                    else
                        serviceWiseDbrs.put(-1, serviceWiseDbrs.get(-1) + data.getSubtotal() + data.getDiscount());
                }
            });
        }

        serviceWiseDbrs.entrySet().stream().forEach(data -> {
            Double planAmount = data.getValue();
            Double invoiceAmount = invoiceAmountWithoutTax.get();
            Double percentageRatio = (planAmount / invoiceAmount) * 100.00d;
            data.setValue(percentageRatio);
        });
        return serviceWiseDbrs;
    }

    /**
     * Gets credit note price excluding tax.
     * @param document the document
     * @param creditNoteAmount the credit note amount
     * @return the credit note price excluding tax
     */
    public Double getCreditNotePriceExcludingTax(DebitDocument document, Double creditNoteAmount) {
        DecimalFormat df = new DecimalFormat("####0.00");
        Double offerPriceExcludeTax = 0.0d;

        if (document != null && creditNoteAmount != null && creditNoteAmount > 0) {
            offerPriceExcludeTax = (((document.getSubtotal() - document.getDiscount()) / document.getTotalamount()) * creditNoteAmount);
        }
        return Double.parseDouble(df.format(offerPriceExcludeTax));
    }

    /**
     * Remove dbr by cpr list and invoice id start date at charge level.
     * @param cprId the cpr id
     * @param invoiceId the invoice id
     * @param from the from
     * @param to the to
     */
    public void removeDbrByCPRListAndInvoiceIdStartDateAtChargeLevel(List<Long> cprId, Integer invoiceId, LocalDate from, LocalDate to) {
        try {
            List<CustomerChargeDBR> customerDBRS = chargeDBRRepository.findAllByCpridInAndInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(cprId, Long.valueOf(invoiceId), from, to);//(List<CustomerChargeDBR>) chargeDBRRepository.findAll(expression);
            chargeDBRRepository.deleteAll(customerDBRS);
        } catch (Exception ex) {
            logger.error("Error while deleting DBR: " + ex.getMessage());
        }
    }

    /**
     * Remove dbr by cpr start date at charge level.
     * @param cprId the cpr id
     * @param from the from
     * @param to the to
     */
    public void removeDbrByCPRStartDateAtChargeLevel(Long cprId, LocalDate from, LocalDate to) {
        try {
            List<CustomerChargeDBR> customerDBRS = chargeDBRRepository.findAllByCpridAndStartdateGreaterThanEqualAndStartdateLessThanEqual(cprId, from, to);
            chargeDBRRepository.deleteAll(customerDBRS);
        } catch (Exception ex) {
            logger.error("Error while deleting DBR: " + ex.getMessage());
        }
    }

    /**
     * Remove dbr by cpr start date at charge level.
     * @param cprId the cpr id
     */
    public void removeDbrByCPRStartDateAtChargeLevel(Long cprId) {
        try {
            List<CustomerChargeDBR> customerDBRS = chargeDBRRepository.findAllByCprid(cprId);
            chargeDBRRepository.deleteAll(customerDBRS);
        } catch (Exception ex) {
            logger.error("Error while deleting DBR: " + ex.getMessage());
        }
    }

    /**
     * Removedbr by cpr list and invoice id start date.
     * @param cprId the cpr id
     * @param invoiceId the invoice id
     * @param from the from
     * @param to the to
     */
    public void removedbrByCPRListAndInvoiceIdStartDate(List<Long> cprId, Integer invoiceId, LocalDate from, LocalDate to) {
        try {
            List<CustomerDBR> customerDBRS = customerDBRRepository.findAllByCpridInAndInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(cprId, Long.valueOf(invoiceId), from, to);
            customerDBRRepository.deleteAll(customerDBRS);
        } catch (Exception ex) {
            logger.error("Error while deleting DBR: " + ex.getMessage());
        }
    }


    /**
     * during creation of customer in one time charge, dbr entry
     * @return Void
     */
    private void addOneTimeEntryForPrepaidIntoDBR(List<CustomerChargeHistory> oneTimeCharges, Customers customer, Long validityInDays, Long invoiceId, Long serviceId) {
        DebitDocument debitDocument = debitDocRepository.findById(invoiceId.intValue()).get();
        try {
            for (int i = 0; i < oneTimeCharges.size(); i++) {
                DecimalFormat df = new DecimalFormat("0.00");
                CustomerDBR customerDBR = new CustomerDBR();
                customerDBR.setInvoiceId(invoiceId);
                customerDBR.setCprid(oneTimeCharges.get(i).getCustPlanMapppingId().longValue());
                customerDBR.setCustid(customer.getId().longValue());
                customerDBR.setPlanid(Long.valueOf(oneTimeCharges.get(i).getPlanId()));
                customerDBR.setCustname(customer.getUsername());
                customerDBR.setCusttype(customer.getCusttype());
                customerDBR.setValidity_days(validityInDays.intValue());
                customerDBR.setOffer_price(oneTimeCharges.get(i).getChargeAmount() + oneTimeCharges.get(i).getTaxAmount());//+oneTimeCharges.get(i).getTax())));
                customerDBR.setStartdate(LocalDate.now());
                customerDBR.setStatus("Active");
                customerDBR.setEnddate(LocalDate.now());
                customerDBR.setDbr(oneTimeCharges.get(i).getChargeAmount() - oneTimeCharges.get(i).getDiscount());//+oneTimeCharges.get(i).getTax())));
                customerDBR.setIsDirectCharge(true);
                customerDBR.setPendingamt(0.0);
                customerDBR.setCumm_revenue(oneTimeCharges.get(i).getChargeAmount() - oneTimeCharges.get(i).getDiscount());
                customerDBR.setServiceId(serviceId);

                customerDBR.setRemark("Onetime Charge Added");

                if (customer != null) {
                    customerDBR.setServiceArea(customer.getServiceAreaId());
                    customerDBR.setBuId(customer.getBuId());
                    customerDBR.setMvnoId(customer.getMvnoId());
                }

                customerDBRRepository.save(customerDBR);
                addOneTimeEntryForPrepaidIntoChargeDBR(oneTimeCharges.get(i), customer, debitDocument, validityInDays);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * during creation of customer in one time charge, dbr entry
     * @return Void
     */
    private void addOneTimeEntryForPrepaidIntoChargeDBR(CustomerChargeHistory itemCharge, Customers customer, DebitDocument debitDocument, Long validityInDays) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");

            CustomerChargeDBR customerDBR = new CustomerChargeDBR();
            customerDBR.setInvoiceId(debitDocument.getId().longValue());
            customerDBR.setChargeId(itemCharge.getChargeId().longValue());
            customerDBR.setCprid(itemCharge.getCustPlanMapppingId().longValue());
            customerDBR.setCustid(customer.getId().longValue());
            customerDBR.setCustname(customer.getUsername());
            customerDBR.setCusttype(customer.getCusttype());
            customerDBR.setValidity_days(validityInDays.intValue());
            customerDBR.setOffer_price(itemCharge.getChargeAmount() + itemCharge.getTaxAmount());//+oneTimeCharges.get(i).getTax())));
            customerDBR.setStartdate(LocalDate.now());
            customerDBR.setStatus("Active");
            customerDBR.setEnddate(LocalDate.now());
            customerDBR.setDbr(itemCharge.getChargeAmount() + itemCharge.getTaxAmount());//+oneTimeCharges.get(i).getTax())));
            customerDBR.setIsDirectCharge(true);
            customerDBR.setPendingamt(0.0);
            customerDBR.setCumm_revenue(itemCharge.getChargeAmount() + itemCharge.getTaxAmount());

            customerDBR.setRemark("Onetime Charge Added");

            if (customer != null) {
                customerDBR.setServiceArea(customer.getServiceAreaId());
                customerDBR.setBuId(customer.getBuId());
                customerDBR.setMvnoId(customer.getMvnoId());
            }
            chargeDBRRepository.save(customerDBR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find all customer chargedbr by debit doc list.
     * @param debitDocument the debit document
     * @return the list
     */
    public List<CustomerChargeDBR> findAllCustomerChargedbrByDebitDoc(DebitDocument debitDocument) {
        return chargeDBRRepository.findAllByInvoiceId(Long.valueOf(debitDocument.getId()));
    }

    /**
     * Find all customerdbr by debit doc list.
     * @param debitDocument the debit document
     * @return the list
     */
    public List<CustomerDBR> findAllCustomerdbrByDebitDoc(DebitDocument debitDocument) {
        return customerDBRRepository.findAllByInvoiceId(Long.valueOf(debitDocument.getId()));
    }

    /**
     * Removedbr by cpr start date.
     * @param cprId the cpr id
     * @param from the from
     * @param to the to
     */
    public void removedbrByCPRStartDate(Long cprId, LocalDate from, LocalDate to) {
        try {
            List<CustomerDBR> customerDBRS = customerDBRRepository.findAllByInvoiceIdAndStartdateBetween(cprId, from, to);
            customerDBRRepository.deleteAll(customerDBRS);
        } catch (Exception ex) {
            logger.error("Error while deleting DBR: " + ex.getMessage());
        }
    }


    /**
     * Removedbr by cpr start date.
     * @param cprId the cpr id
     */
    public void removedbrByCPRStartDate(Long cprId) {
        try {
            List<CustomerDBR> customerDBRS = customerDBRRepository.findAllByInvoiceId(cprId);
            customerDBRRepository.deleteAll(customerDBRS);
        } catch (Exception ex) {
            logger.error("Error while deleting DBR: " + ex.getMessage());
        }
    }

    /**
     * Gets customer dbr list between start date and end date.
     * @param pendingDate the pending date
     * @param document the document
     * @return the customer dbr list between start date and end date
     */
    public List<CustomerDBR> getCustomerDBRListBetweenStartDateAndEndDate(LocalDate pendingDate, DebitDocument document) {
//        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
//        BooleanExpression expression = qCustomerDBR.isNotNull();
//        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(pendingDate, document.getEndate().toLocalDate())).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCpridIsNotNull(document.getId().longValue(), pendingDate, document.getEndate().toLocalDate());
        return dbrList;
    }

    /**
     * Gets customer dbr list between start date and end date 002.
     * @param pendingDate the pending date
     * @param document the document
     * @return the customer dbr list between start date and end date 002
     */
    public List<CustomerDBR> getCustomerDBRListBetweenStartDateAndEndDate002(LocalDate pendingDate, DebitDocument document) {
//        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
//        BooleanExpression expression = qCustomerDBR.isNotNull();
//        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(pendingDate, document.getEndate().toLocalDate())).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCpridIsNotNull002(document.getId().longValue(), pendingDate, document.getEndate().toLocalDate());
        return dbrList;
    }


    /**
     * Gets customer dbr list between start date and end date 001.
     * @param pendingDate the pending date
     * @param document the document
     * @return the customer dbr list between start date and end date 001
     */
    public List<CustomerDBR> getCustomerDBRListBetweenStartDateAndEndDate001(LocalDate pendingDate, DebitDocument document) {
//        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
//        BooleanExpression expression = qCustomerDBR.isNotNull();
//        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(pendingDate, document.getEndate().toLocalDate())).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCpridIsNotNull001(document.getId().longValue(), pendingDate, document.getEndate().toLocalDate());
        return dbrList;
    }


    /**
     * Gets daily dbr deatils.
     * @param startdate the startdate
     * @param endate the endate
     * @return the daily dbr deatils
     */
    //start commentd line-->
//    public List<CustomDailyRevenue> getDailyDbrDeatils(LocalDate startdate, LocalDate endate) {
//        Integer mvnoId = getLoggedInUser().getMvnoId();
//        List<Long> buIdsList = getLoggedInUser().getBuIds();
//        List<Integer> serviceAreaIdsList = getLoggedInUser().getServiceAreaIdList();
//        String query = "Select  rev from CustomDailyRevenue rev Where ";
//        if (mvnoId != null)
//            query += "(rev.mvnoId =" + mvnoId + " Or rev.mvnoId is null )";
//        else
//            query += " And rev.mvnoId is null ";
//
//        if (buIdsList != null && !buIdsList.isEmpty()) {
//            String buidList = "(" + buIdsList.stream()
//                    .map(String::valueOf)
//                    .collect(Collectors.joining(", ")) + ")";
//
//            query += " and (rev.buId In " + buidList + " Or rev.buId is null)";
//
//        } else {
//            query += " and rev.buId is null";
//        }
//        String formattedList = "(" + serviceAreaIdsList.stream()
//                .map(String::valueOf)
//                .collect(Collectors.joining(", ")) + ")";
//        if (serviceAreaIdsList != null && !serviceAreaIdsList.isEmpty()) {
//            query += " and (rev.serviceAreaId In " + formattedList + " Or rev.serviceAreaId is null)";
//        } else {
//            query += " and rev.serviceAreaId is null";
//        }
//        query += " and rev.date between '" + startdate + "' And '" + endate + "'";
//        Query q = entityManager.createQuery(query, CustomDailyRevenue.class);
//        List<CustomDailyRevenue> dailyRevenues = q.getResultList();
//        return dailyRevenues;
//    }

    //<<--end commnted line.

// reason: getDailyDbrDeatils method created in this getServiceAreaIdList not getting this time data not get so in this api to if serviceareaid not get then mvnoid throgh all data get.
    public List<CustomDailyRevenue> getDailyDbrDeatils(LocalDate startdate, LocalDate endate) {
        Integer mvnoId = getLoggedInUser().getMvnoId();
        List<Long> buIdsList = getLoggedInUser().getBuIds();
        List<Integer> serviceAreaIdsList = getLoggedInUser().getServiceAreaIdList();

        StringBuilder query = new StringBuilder("SELECT rev FROM CustomDailyRevenue rev WHERE 1=1");
        Map<String, Object> params = new HashMap<>();


        if (mvnoId != null) {
            query.append(" AND rev.mvnoId = :mvnoId");
            params.put("mvnoId", mvnoId);
        }


        if (buIdsList != null && !buIdsList.isEmpty()) {
            query.append(" AND rev.buId IN :buIds");
            params.put("buIds", buIdsList.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList()));
        }

        if (serviceAreaIdsList != null && !serviceAreaIdsList.isEmpty()) {
            query.append(" AND rev.serviceAreaId IN :saIds");
            params.put("saIds", serviceAreaIdsList.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList()));
        }



        query.append(" AND rev.date BETWEEN :startDate AND :endDate");
        params.put("startDate", startdate);
        params.put("endDate", endate);

        Query q = entityManager.createQuery(query.toString(), CustomDailyRevenue.class);
        params.forEach(q::setParameter);

        return q.getResultList();
    }

    /**
     * Gets month wise dbr deatils.
     * @return the month wise dbr deatils
     */
//    public List<CustomMonthlyRevenue> getMonthWiseDbrDeatils() {
//        Integer mvnoId = getLoggedInUser().getMvnoId();
//        List<Long> buIdsList = getLoggedInUser().getBuIds();
//        List<Integer> serviceAreaIdsList = getLoggedInUser().getServiceAreaIdList();
//        String query = "Select  rev from CustomMonthlyRevenue rev Where ";
//
//        if (mvnoId != null)
//            query += "(rev.mvnoId =" + mvnoId + " Or rev.mvnoId is null)";
//        else
//            query += "And rev.mvnoId is null";
//
//        if (buIdsList != null && !buIdsList.isEmpty()) {
//            String buidList = "(" + buIdsList.stream()
//                    .map(String::valueOf)
//                    .collect(Collectors.joining(", ")) + ")";
//            query += " and (rev.buId In " + buidList + " Or rev.buId is null)";
//        } else {
//            query += " and rev.buId is null";
//        }
//
//        String formattedList = "(" + serviceAreaIdsList.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";
//        if (serviceAreaIdsList != null && !serviceAreaIdsList.isEmpty()) {
//            query += " and (rev.serviceAreaId In " + formattedList + " Or rev.serviceAreaId is null)";
//        } else {
//            query += " and rev.serviceAreaId is null";
//        }
//        Query q = entityManager.createQuery(query, CustomMonthlyRevenue.class);
//        List<CustomMonthlyRevenue> dailyRevenues = q.getResultList();
//        return dailyRevenues;
//    }


    public List<CustomMonthlyRevenue> getMonthWiseDbrDeatils() {
        Integer mvnoId = getLoggedInUser().getMvnoId();
        List<Long> buIdsList = getLoggedInUser().getBuIds();
        List<Integer> serviceAreaIdsList = getLoggedInUser().getServiceAreaIdList();

        StringBuilder query = new StringBuilder("SELECT rev FROM CustomMonthlyRevenue rev WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (mvnoId != null) {
            query.append(" AND (rev.mvnoId = :mvnoId OR rev.mvnoId IS NULL)");
            params.put("mvnoId", mvnoId);
        }

        if (buIdsList != null && !buIdsList.isEmpty()) {
            query.append(" AND (rev.buId IN :buIds OR rev.buId IS NULL)");
            params.put("buIds", buIdsList.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList()));
        }

        if (serviceAreaIdsList != null && !serviceAreaIdsList.isEmpty()) {
            query.append(" AND (rev.serviceAreaId IN :saIds OR rev.serviceAreaId IS NULL)");
            params.put("saIds", serviceAreaIdsList.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList()));
        }

        Query q = entityManager.createQuery(query.toString(), CustomMonthlyRevenue.class);
        params.forEach(q::setParameter);

        return q.getResultList();
    }



    /**
     * Gets logged in user.
     * @return the logged in user
     */
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {

            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }

        } catch (Exception e) {
            user = null;
        }
        return user;
    }


    /**
     * Credit note dbr entry.
     * @param document the document
     * @param creditNoteAmount the credit note amount
     * @param isNeedToCalculateWithoutTax the is need to calculate without tax
     */
    public void creditNoteDbrEntry(DebitDocument document, Double creditNoteAmount, Boolean isNeedToCalculateWithoutTax) {
//        revertPartnerLedgerAndDetail(document, creditNoteAmount);
        DecimalFormat df = new DecimalFormat("0.00");
        LocalDate currentDate = LocalDate.now();
        Double creditAmountExcludeTax = getCreditNotePriceExcludingTax(document, creditNoteAmount);
        if (!isNeedToCalculateWithoutTax)
            creditAmountExcludeTax = creditNoteAmount;
        creditAmountExcludeTax = Double.parseDouble(df.format(creditAmountExcludeTax));

        try {
            if (!currentDate.isBefore(document.getStartdate().toLocalDate())) {
                if (!document.getIsDirectChargeInvoice()) {
                    List<CustomerDBR> customerDBRList = getCustomerDBRListBetweenStartDateAndEndDate001(currentDate, document);
                    List<CustomerChargeDBR> customerChargeDBRList = customerChargeDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual001(document.getId().longValue(), currentDate, document.getEndate().toLocalDate());
                    Double pendingAmount = null;
                    if (customerDBRList != null && customerDBRList.size() > 0)
                        pendingAmount = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(currentDate)).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));

                    List<CustomerDBR> customerDBRList1 = getCustomerDBRListBetweenStartDateAndEndDate002(currentDate, document);
                    List<CustomerChargeDBR> customerChargeDBRList1 = customerChargeDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual002(document.getBuId(), currentDate, document.getEndate().toLocalDate());
                    Double pendingAmount1 = null;
                    if (customerDBRList1 != null && customerDBRList1.size() > 0)
                        pendingAmount1 = Double.parseDouble(df.format(customerDBRList1.stream().filter(x -> x.getStartdate().equals(currentDate)).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));


                    if (creditAmountExcludeTax != null && creditAmountExcludeTax > 0 && document != null) {
                        if (pendingAmount != null) {
                            if (creditAmountExcludeTax.doubleValue() == pendingAmount.doubleValue()) {

                                removeAllEntry(document.getId().longValue(), currentDate, document.getEndate().toLocalDate());
                                addDbrEntry(document, document.getId().longValue(), creditAmountExcludeTax);
                                logger.info("Dbr entry against CreditNote Successfully Added", APIConstants.SUCCESS);

                            } else if (pendingAmount.doubleValue() < creditAmountExcludeTax.doubleValue()) {

                                removeAllEntry(document.getId().longValue(), currentDate, document.getEndate().toLocalDate());
                                if (document.getStartdate().equals(currentDate))
                                    addDbrEntry(document, creditAmountExcludeTax, currentDate, 2, customerDBRList, customerChargeDBRList);
                                else
                                    addDbrEntry1(document, creditAmountExcludeTax, pendingAmount - creditAmountExcludeTax, currentDate, customerDBRList);
                                logger.info("Dbr entry against CreditNote Successfully Added and Future Entries Deleted", APIConstants.SUCCESS);

                            } else if (pendingAmount > creditAmountExcludeTax) {
                                if (pendingAmount1 != null) {
                                    if (pendingAmount1.doubleValue() == creditAmountExcludeTax.doubleValue()) {

                                        removeAllEntry1(document.getId().longValue(), currentDate, document.getEndate().toLocalDate());
                                        addDbrEntry(document, creditAmountExcludeTax, currentDate, 3, customerDBRList, customerChargeDBRList);
                                        logger.info("Dbr entry against CreditNote Successfully Added", APIConstants.SUCCESS);

                                    } else if (pendingAmount1.doubleValue() > creditAmountExcludeTax.doubleValue()) {

                                        updateAllEntry(creditAmountExcludeTax, document.getStartdate().equals(currentDate), customerDBRList1, customerChargeDBRList1);
                                        addDbrEntry(document, creditAmountExcludeTax, currentDate, 3, customerDBRList, customerChargeDBRList);
                                        logger.info("Dbr entry against CreditNote Successfully Added", APIConstants.SUCCESS);

                                    }
                                } else {
                                    addDbrEntry1(document, document.getId().longValue(), creditAmountExcludeTax);
                                }
                            }
                        } else {
                            if (!document.getStartdate().equals(currentDate)) {
                                List<CustomerDBR> customerDBRList2 = customerDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCpridIsNotNull(document.getId().longValue(), document.getStartdate().toLocalDate(), document.getEndate().toLocalDate());
                                Double cumulativeRevenue = null;
                                if (customerDBRList2 != null && customerDBRList2.size() > 0)
                                    cumulativeRevenue = Double.parseDouble(df.format(customerDBRList2.stream().mapToDouble(x -> x.getDbr()).sum()));

                                if (cumulativeRevenue != null)
                                    addDbrEntry1(document, creditAmountExcludeTax, currentDate, customerDBRList);
                            }
                        }
                    }
                } else {
                    CustomerDBR customerDBR = getCustomerPendingRevenueForDirectChargeInvoice(document);
                    addDbrEntryForDirectCharge(document, creditAmountExcludeTax, currentDate);
                }
            } else {
                //future dbr update code
                List<CustomerDBR> futureDbrList = getFutureDbrList(document);
                List<CustomerChargeDBR> futureChargeDbrList = getFutureChargeDbrList(document);
                Double pendingAmount = 0.0;
                if (futureChargeDbrList != null && !futureChargeDbrList.isEmpty()) {
                    pendingAmount = Double.parseDouble(df.format(futureDbrList.get(0).getPendingamt() + futureDbrList.get(0).getDbr()));
                }

                if (futureDbrList != null && !futureDbrList.isEmpty()) {
                    if (document.getTotalamount().doubleValue() == creditNoteAmount.doubleValue() || pendingAmount.doubleValue() == creditAmountExcludeTax.doubleValue()) {
                        removeAllEntry(document.getId().longValue(), document.getStartdate().toLocalDate(), document.getEndate().toLocalDate());
                        addDbrEntry(document, document.getId().longValue(), creditAmountExcludeTax);
                    } else {
                        updateAllFutureEntry(futureDbrList, creditAmountExcludeTax, futureChargeDbrList);
                        addDbrFutureEntry(document, document.getId().longValue(), creditAmountExcludeTax, futureDbrList, futureChargeDbrList);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to Add Dbr Against CreditNote :error{};exception{}", APIConstants.FAIL, e.getStackTrace());
        }
    }


    /**
     * Remove all entry.
     * @param invoiceId the invoice id
     * @param currentDate the current date
     * @param endate the endate
     */
    public void removeAllEntry(Long invoiceId, LocalDate currentDate, LocalDate endate) {
//        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
//        BooleanExpression exp = qCustomerDBR.isNotNull();
//        exp = exp.and(qCustomerDBR.startdate.between(currentDate, endate)).and(qCustomerDBR.invoiceId.eq(invoiceId)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> customerDBRSList = customerDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCpridIsNotNull(invoiceId, currentDate, endate);
        for (CustomerDBR customerDBR : customerDBRSList) {
            customerDBRRepository.delete(customerDBR);
        }
        removeAllEntryAtChargeLevel(invoiceId, currentDate, endate);
    }

    private void removeAllEntryAtChargeLevel(Long invoiceId, LocalDate currentDate, LocalDate endate) {
//        QCustomerChargeDBR qCustomerChargeDBR=QCustomerChargeDBR.customerChargeDBR;
//        BooleanExpression exp = qCustomerChargeDBR.isNotNull();
//        exp = exp.and(qCustomerChargeDBR.startdate.between(currentDate, endate)).and(qCustomerChargeDBR.invoiceId.eq(invoiceId)).and(qCustomerChargeDBR.cprid.isNotNull());
        List<CustomerChargeDBR> customerChargeDBRList = customerChargeDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(invoiceId, currentDate, endate);
        for (CustomerChargeDBR customerChargeDBR : customerChargeDBRList) {
            customerChargeDBRRepository.delete(customerChargeDBR);
        }

    }


    /**
     * Add dbr entry.
     * @param document the document
     * @param invoiceId the invoice id
     * @param creditAmountExcludeTax the credit amount exclude tax
     */
    public void addDbrEntry(DebitDocument document, Long invoiceId, Double creditAmountExcludeTax) {
        DecimalFormat df = new DecimalFormat("0.00");
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId());
        map.entrySet().stream().forEach(data -> {
            Double amount = (creditAmountExcludeTax * data.getValue()) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(0.0);
            dbr.setPartnerId(null);
            dbr.setPendingamt(amount);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(0.0);
            dbr.setServiceId(data.getKey().longValue());
            dbr.setRemark(df.format(amount) + " CreditNote Adjusted for " + getServiceNameById(data.getKey().longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrEntryAtChargeLevel(document, invoiceId, amount, data.getKey().longValue());
        });
    }

    /**
     * Gets service name by id.
     * @param serviceId the service id
     * @return the service name by id
     */
    public String getServiceNameById(Long serviceId) {
        Optional<Services> services = serviceRepository.findById(serviceId);
        if (services.isPresent())
            return services.get().getServiceName();
        else
            return "Inventory As";
    }


    private void addDbrEntry(DebitDocument document, Double creditAmountExcludeTax, LocalDate currentDate, Integer flag, List<CustomerDBR> customerDBRList, List<CustomerChargeDBR> customerChargeDBRList) {
        Map<Integer, Double> map = getServiceWiseRatioForCreditNoteAmount(customerDBRList);
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            CustomerDBR customerDBR1 = customerDBRList.stream().filter(x -> x.getServiceId().equals(serviceId.longValue())).findFirst().get();

            Double amount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();

            if (flag.equals(2)) {

                dbr.setPendingamt(amount);
                dbr.setDbr(0d);
                dbr.setCumm_revenue(0d);
            }
            if (flag.equals(3)) {
                dbr.setDbr(0d);
                dbr.setPendingamt(amount);
                dbr.setCumm_revenue(0d);
            }

            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setPartnerId(null);
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrEntryAtChargeLevel(document, amount, currentDate, flag, customerChargeDBRList, serviceId);
        }
    }


    private void addDbrEntry1(DebitDocument document, Double creditAmountExcludeTax, Double extraAmount, LocalDate currentDate, List<CustomerDBR> customerDBRList) {
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId());

        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();

            Double extra = (extraAmount * percentage) / 100.0d;
            Double creditAmount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();
            dbr.setDbr(extra);
            dbr.setPendingamt(-(creditAmount + extra));
            dbr.setCumm_revenue((extraAmount * percentage) / 100.0d);
            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setPartnerId(null);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrEntry1AtChargeLevel(document, creditAmount, extra, currentDate, serviceId);
        }
    }

    /**
     * Gets service wise ratio for credit note amount.
     * @param customerDBRList the customer dbr list
     * @return the service wise ratio for credit note amount
     */
    public Map<Integer, Double> getServiceWiseRatioForCreditNoteAmount(List<CustomerDBR> customerDBRList) {
        Map<Integer, Double> serviceWiseDbrs = new HashMap<>();
        List<Integer> serviceIdList = customerDBRList.stream().map(x -> x.getServiceId().intValue()).distinct().collect(Collectors.toList());
        Double totalAmount = customerDBRList.stream().mapToDouble(x -> x.getDbr()).sum();

        if (serviceIdList != null && !serviceIdList.isEmpty()) {
            serviceIdList.stream().forEach(x -> {
                Double totalDbr = customerDBRList.stream().filter(y -> y.getServiceId().equals(x.longValue())).mapToDouble(y -> y.getDbr()).sum();
                Double percentage = (totalDbr / totalAmount) * 100.0d;
                serviceWiseDbrs.put(x.intValue(), percentage);

            });
        }
        return serviceWiseDbrs;
    }

    private void removeAllEntry1(Long invoiceId, LocalDate currentDate, LocalDate endate) {
//        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
//        BooleanExpression exp = qCustomerDBR.isNotNull();
//        exp = exp.and(qCustomerDBR.startdate.between(currentDate, endate)).and(qCustomerDBR.invoiceId.eq(invoiceId)).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> customerDBRSList = customerDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCpridIsNotNull(invoiceId, currentDate, endate);
        for (CustomerDBR customerDBR : customerDBRSList) {
            customerDBRRepository.delete(customerDBR);
        }
        removeAllEntry1AtChargeLevel(invoiceId, currentDate, endate);
    }

    private void removeAllEntry1AtChargeLevel(Long invoiceId, LocalDate currentDate, LocalDate endate) {
//        QCustomerChargeDBR qCustomerDBR = QCustomerChargeDBR.customerChargeDBR;
//        BooleanExpression exp = qCustomerDBR.isNotNull();
//        exp = exp.and(qCustomerDBR.startdate.between(currentDate, endate)).and(qCustomerDBR.invoiceId.eq(invoiceId)).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerChargeDBR> customerDBRSList = customerChargeDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(invoiceId, currentDate, endate);
        for (CustomerChargeDBR customerDBR : customerDBRSList) {
            customerChargeDBRRepository.delete(customerDBR);
        }
    }


    private void updateAllEntry(Double creditAmountExcludeTax, Boolean isSameDay, final List<CustomerDBR> customerDBRList, List<CustomerChargeDBR> customerChargeDBRList1) {

        List<Long> serviceIdList = customerDBRList.stream().map(x -> x.getServiceId()).distinct().collect(Collectors.toList());
        Double totalAmount = customerDBRList.stream().mapToDouble(x -> x.getDbr()).sum();

        serviceIdList.stream().forEach(x -> {

            List<CustomerDBR> list = customerDBRList.stream().filter(y -> y.getServiceId().equals(x.longValue())).collect(Collectors.toList());
            Long planCount = list.stream().filter(data -> data.getPlanid() != null).mapToLong(y -> y.getPlanid()).distinct().count();
            Double serviceAmount = list.stream().mapToDouble(d -> d.getDbr()).sum();
            Double percentage = (serviceAmount / totalAmount) * 100.0d;

            long days = list.size();
            if (days > 0) {
                Double customerPendingRevenue = 0d;
                Double updatedDbr = 0d;
                Double cummRevenue = 0d;

                if (isSameDay) {
                    customerPendingRevenue = list.get(0).getPendingamt() + list.get(0).getDbr() - (creditAmountExcludeTax * percentage) / 100.0d;
                    updatedDbr = customerPendingRevenue / days;
                } else {
                    customerPendingRevenue = list.get(0).getPendingamt() + list.get(0).getDbr() - (creditAmountExcludeTax * percentage) / 100.0d;
                    updatedDbr = customerPendingRevenue / days;
                    cummRevenue = list.get(0).getCumm_revenue() - list.get(0).getDbr();
                }


                if (planCount > 1) {
                    LocalDate startDate = list.get(0).getStartdate();
                    List<CustomerDBR> list02 = list.stream().filter(z -> z.getStartdate().equals(startDate)).collect(Collectors.toList());
                    Double pendingAmount = list02.stream().mapToDouble(amount -> amount.getPendingamt()).sum();
                    Double dbrAmount = list02.stream().mapToDouble(amount -> amount.getDbr()).sum();
                    Double cumRevenueAmount = list02.stream().mapToDouble(amount -> amount.getCumm_revenue()).sum();
                    customerPendingRevenue = pendingAmount + dbrAmount - (creditAmountExcludeTax * percentage) / 100.0d;
                    updatedDbr = customerPendingRevenue / days;
                    cummRevenue = cumRevenueAmount - dbrAmount;
                }

                //update Future Entries
                for (CustomerDBR customerDBR : list) {
                    cummRevenue += updatedDbr;
                    customerPendingRevenue -= updatedDbr;
                    customerDBR.setDbr(updatedDbr);
                    customerDBR.setPendingamt(customerPendingRevenue);
                    customerDBR.setCumm_revenue(cummRevenue);
                    customerDBRRepository.save(customerDBR);
                }
            }
        });

        updateAllEntryAtChargeLevel(creditAmountExcludeTax, isSameDay, customerChargeDBRList1);
    }

    private void updateAllEntryAtChargeLevel(Double creditAmountExcludeTax, Boolean isSameDay, List<CustomerChargeDBR> customerDBRList) {
        List<Long> serviceIdList = customerDBRList.stream().map(x -> x.getChargeId()).distinct().collect(Collectors.toList());
        Double totalAmount = customerDBRList.stream().mapToDouble(x -> x.getDbr()).sum();

        serviceIdList.stream().forEach(x -> {

            List<CustomerChargeDBR> list = customerDBRList.stream().filter(y -> y.getChargeId().equals(x.longValue())).collect(Collectors.toList());
            Double serviceAmount = list.stream().mapToDouble(d -> d.getDbr()).sum();
            Double percentage = (serviceAmount / totalAmount) * 100.0d;

            long days = list.size();
            if (days > 0) {
                Double customerPendingRevenue = 0d;
                Double updatedDbr = 0d;
                Double cummRevenue = 0d;

                if (isSameDay) {
                    customerPendingRevenue = list.get(0).getPendingamt() + list.get(0).getDbr() - (creditAmountExcludeTax * percentage) / 100.0d;
                    updatedDbr = customerPendingRevenue / days;
                } else {
                    customerPendingRevenue = list.get(0).getPendingamt() + list.get(0).getDbr() - (creditAmountExcludeTax * percentage) / 100.0d;
                    updatedDbr = customerPendingRevenue / days;
                    cummRevenue = list.get(0).getCumm_revenue() - list.get(0).getDbr();
                }

                //update Future Entries
                for (CustomerChargeDBR customerDBR : list) {
                    cummRevenue += updatedDbr;
                    customerPendingRevenue -= updatedDbr;
                    customerDBR.setDbr(updatedDbr);
                    customerDBR.setPendingamt(customerPendingRevenue);
                    customerDBR.setCumm_revenue(cummRevenue);
                    customerChargeDBRRepository.save(customerDBR);
                }
            }
        });
    }

    private void addDbrEntry1(DebitDocument document, Long invoiceId, Double creditAmountExcludeTax) {
        DecimalFormat df = new DecimalFormat("0.00");
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId());
        map.entrySet().stream().forEach(data -> {
            CustomerDBR dbr = new CustomerDBR();
            Double amount = (creditAmountExcludeTax * data.getValue()) / 100.0d;
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(-amount);
            dbr.setPartnerId(null);
            dbr.setPendingamt(amount);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(-amount);
            dbr.setServiceId(data.getKey().longValue());
            dbr.setRemark(df.format(amount) + " CreditNote Adjusted for " + getServiceNameById(data.getKey().longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrEntry1AtChargeLevel(document, invoiceId, amount, data.getKey().longValue());

        });
    }


    private void addDbrEntry1AtChargeLevel(DebitDocument document, Long invoiceId, Double creditAmountExcludeTax, Long serviceId) {
        DecimalFormat df = new DecimalFormat("0.00");
        Map<Integer, Double> map = getChargeWiseRatioForInvoiceAmount(document.getId(), serviceId);
        map.entrySet().stream().forEach(data -> {
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            Double amount = (creditAmountExcludeTax * data.getValue()) / 100.0d;
            dbr.setChargeId(data.getKey().longValue());
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(-amount);
            dbr.setPendingamt(amount);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(-amount);
            dbr.setServiceId(serviceId);
            dbr.setRemark(df.format(amount) + " CreditNote Adjusted for " + getServiceNameById(data.getKey().longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        });
    }


    private void addDbrEntryAtChargeLevel(DebitDocument document, Double creditAmountExcludeTax, LocalDate currentDate, Integer flag, List<CustomerChargeDBR> customerChargeDBRList, Integer serviceId) {
        Map<Integer, Double> map = getChargeWiseRatioForCreditNoteAmount(customerChargeDBRList, serviceId);
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer chargeId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            CustomerChargeDBR customerDBR1 = customerChargeDBRList.stream().filter(x -> x.getChargeId().equals(chargeId.longValue())).findFirst().get();

            Double amount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            dbr.setChargeId(chargeId.longValue());

            if (flag.equals(2)) {

                dbr.setPendingamt(amount);
                dbr.setDbr(0d);
                dbr.setCumm_revenue(0d);
            }
            if (flag.equals(3)) {
                dbr.setDbr(0d);
                dbr.setPendingamt(amount);
                dbr.setCumm_revenue(0d);
            }

            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        }
    }

    private void addDbrEntry1AtChargeLevel(DebitDocument document, Double creditAmountExcludeTax, Double extraAmount, LocalDate currentDate, Integer serviceId) {
        Map<Integer, Double> serviceMap = getChargeWiseRatioForInvoiceAmount(document.getId(), serviceId.longValue());
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : serviceMap.entrySet()) {
            Integer chargeId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();

            Double extra = (extraAmount * percentage) / 100.0d;
            Double creditAmount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            dbr.setChargeId(chargeId.longValue());
            dbr.setDbr(extra);
            dbr.setPendingamt(-(creditAmount + extra));
            dbr.setCumm_revenue((extraAmount * percentage) / 100.0d);
            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark("");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        }
    }

    /**
     * Gets charge wise ratio for credit note amount.
     * @param customerDBRList the customer dbr list
     * @param serviceId the service id
     * @return the charge wise ratio for credit note amount
     */
    public Map<Integer, Double> getChargeWiseRatioForCreditNoteAmount(List<CustomerChargeDBR> customerDBRList, Integer serviceId) {
        Map<Integer, Double> serviceWiseDbrs = new HashMap<>();
        customerDBRList = customerDBRList.stream().filter(x -> x.getServiceId().equals(serviceId.longValue())).collect(Collectors.toList());
       // List<Integer> chargeIdList = customerDBRList.stream().map(x -> x.getChargeId().intValue()).distinct().collect(Collectors.toList());
        List<Integer> chargeIdList = customerDBRList.stream().filter(x -> x.getChargeId() != null).map(x -> x.getChargeId().intValue()).distinct().collect(Collectors.toList());
        Double totalAmount = customerDBRList.stream().mapToDouble(x -> x.getDbr()).sum();

        if (chargeIdList != null && !chargeIdList.isEmpty()) {
            List<CustomerChargeDBR> finalCustomerDBRList = customerDBRList;
            chargeIdList.stream().forEach(x -> {
                Double totalDbr = finalCustomerDBRList.stream().filter(y -> y.getChargeId().equals(x.longValue())).mapToDouble(y -> y.getDbr()).sum();
                Double percentage = (totalDbr / totalAmount) * 100.0d;
                serviceWiseDbrs.put(x.intValue(), percentage);

            });
        }
        return serviceWiseDbrs;
    }


    private void addDbrEntry1(DebitDocument document, Double creditAmountExcludeTax, LocalDate currentDate, List<CustomerDBR> customerDBRList) {
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId());

        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();

            Double creditAmount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();
            dbr.setDbr(-creditAmount);
            dbr.setPendingamt(0.0d);
            dbr.setCumm_revenue(-creditAmount);
            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setPartnerId(null);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrEntry1AtChargeLevelX(document, creditAmount, currentDate, serviceId);
        }
    }


    private void addDbrEntry1AtChargeLevelX(DebitDocument document, Double creditAmountExcludeTax, LocalDate currentDate, Integer serviceId) {
        Map<Integer, Double> map = getChargeWiseRatioForInvoiceAmount(document.getId(), serviceId.longValue());
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer chargeId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();

            Double creditAmount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            dbr.setChargeId(chargeId.longValue());
            dbr.setDbr(-creditAmount);
            dbr.setPendingamt(0.0d);
            dbr.setCumm_revenue(-creditAmount);
            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        }
    }

    private CustomerDBR getCustomerPendingRevenueForDirectChargeInvoice(DebitDocument document) {
        CustomerDBR customerDBR = null;
//        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
//        BooleanExpression expression = qCustomerDBR.isNotNull();
//        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue()));
        List<CustomerDBR> dbrList = customerDBRRepository.findAllByInvoiceId(document.getId().longValue());
        if (dbrList != null && !dbrList.isEmpty()) {
            return dbrList.get(dbrList.size() - 1);
        }
        return customerDBR;

    }

    private void addDbrEntryForDirectCharge(DebitDocument document, Double creditAmountExcludeTax, LocalDate currentDate) {
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId());

        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();

            CustomerDBR dbr = new CustomerDBR();
            dbr.setDbr(-(creditAmountExcludeTax * percentage) / 100.0d);
            dbr.setPendingamt((creditAmountExcludeTax * percentage) / 100.0d);
            dbr.setCumm_revenue(-(creditAmountExcludeTax * percentage) / 100.0d);
            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setPartnerId(null);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
        }
    }

    private List<CustomerDBR> getFutureDbrList(DebitDocument document) {
//        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
//        BooleanExpression expression = qCustomerDBR.isNotNull();
//        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(document.getStartdate().toLocalDate(), document.getEndate().toLocalDate())).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> dbrList = customerDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCpridIsNotNull(document.getId().longValue(), document.getStartdate().toLocalDate(), document.getEndate().toLocalDate());
        if (dbrList != null && !dbrList.isEmpty())
            return dbrList;
        else
            return null;
    }

    private List<CustomerChargeDBR> getFutureChargeDbrList(DebitDocument document) {
//        QCustomerChargeDBR qCustomerDBR = QCustomerChargeDBR.customerChargeDBR;
//        BooleanExpression expression = qCustomerDBR.isNotNull();
//        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(document.getStartdate().toLocalDate(), document.getEndate().toLocalDate())).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerChargeDBR> dbrList = customerChargeDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(document.getId().longValue(), document.getStartdate().toLocalDate(), document.getEndate().toLocalDate());
        if (dbrList != null && !dbrList.isEmpty())
            return dbrList;
        else
            return null;
    }


    private void updateAllFutureEntry(List<CustomerDBR> customerDBRList, Double creditAmountExcludeTax, List<CustomerChargeDBR> futureChargeDbrList) {
        Map<Integer, Double> map = getServiceWiseRatioForCreditNoteAmount(customerDBRList);
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            Double amount1 = (creditAmountExcludeTax * percentage) / 100.0d;
            List<CustomerDBR> list = customerDBRList.stream().filter(x -> x.getServiceId().equals(serviceId.longValue())).collect(Collectors.toList());
            Integer days = list.size();
            if (days > 0) {
                CustomerDBR customerDBR1 = list.stream().filter(x -> x.getServiceId().equals(serviceId.longValue())).findFirst().get();
                AtomicReference<Double> amount = new AtomicReference<>(customerDBR1.getPendingamt() + customerDBR1.getDbr() - (creditAmountExcludeTax * percentage) / 100.0d);
                Double dbr = amount.get() / days;
                AtomicReference<Double> cummRevenue = new AtomicReference<>(0.0d);

                list.stream().forEach(data -> {
                    amount.set(amount.get() - dbr);
                    data.setPendingamt(amount.get());
                    data.setDbr(dbr);
                    cummRevenue.updateAndGet(v -> v + dbr);
                    data.setCumm_revenue(cummRevenue.get());
                    customerDBRRepository.save(data);
                });
            }
            updateAllFutureEntryAtChargeLevel(amount1, futureChargeDbrList, serviceId);
        }
    }


    private void updateAllFutureEntryAtChargeLevel(Double creditAmountExcludeTax, List<CustomerChargeDBR> futureChargeDbrList, Integer serviceId) {
        Map<Integer, Double> map = getChargeWiseRatioForCreditNoteAmount(futureChargeDbrList, serviceId);
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer chargeId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            List<CustomerChargeDBR> list = futureChargeDbrList.stream().filter(x -> x.getChargeId().equals(chargeId.longValue())).collect(Collectors.toList());
            Integer days = list.size();
            if (days > 0) {
                CustomerChargeDBR customerDBR1 = list.stream().findFirst().get();
                AtomicReference<Double> amount = new AtomicReference<>(customerDBR1.getPendingamt() + customerDBR1.getDbr() - (creditAmountExcludeTax * percentage) / 100.0d);
                Double dbr = amount.get() / days;
                AtomicReference<Double> cummRevenue = new AtomicReference<>(0.0d);

                list.stream().forEach(data -> {
                    amount.set(amount.get() - dbr);
                    data.setPendingamt(amount.get());
                    data.setDbr(dbr);
                    cummRevenue.updateAndGet(v -> v + dbr);
                    data.setCumm_revenue(cummRevenue.get());
                    customerChargeDBRRepository.save(data);
                });
            }
        }
    }


    private void addDbrFutureEntry(DebitDocument document, long invoiceId, Double creditAmountExcludeTax, List<CustomerDBR> customerDBRList, List<CustomerChargeDBR> futureChargeDbrList) {
        Map<Integer, Double> map = getServiceWiseRatioForCreditNoteAmount(customerDBRList);

        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            Double amount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(0.0);
            dbr.setPendingamt(-(creditAmountExcludeTax * percentage) / 100.0d);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setPartnerId(null);
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(0.0);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrFutureEntryAtChargeLevel(document, invoiceId, amount, futureChargeDbrList, serviceId);
        }
    }


    private void addDbrFutureEntryAtChargeLevel(DebitDocument document, long invoiceId, Double creditAmountExcludeTax, List<CustomerChargeDBR> futureChargeDbrList, Integer serviceId) {
        Map<Integer, Double> map = getChargeWiseRatioForCreditNoteAmount(futureChargeDbrList, serviceId);

        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer chargeId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            dbr.setChargeId(chargeId.longValue());
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(0.0);
            dbr.setPendingamt(-(creditAmountExcludeTax * percentage) / 100.0d);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(0.0);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServiceAreaId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        }
    }


    /**
     * Gets customer charge dbr list between start date and end date and by service.
     * @param pendingDate the pending date
     * @param document the document
     * @param custPackIds the cust pack ids
     * @return the customer charge dbr list between start date and end date and by service
     */
    public List<CustomerChargeDBR> getCustomerChargeDBRListBetweenStartDateAndEndDateAndByService(LocalDate pendingDate, DebitDocument document, List<Long> custPackIds) {
//        QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
//        BooleanExpression expression = qCustomerChargeDBR.isNotNull();
//        expression = expression.and(qCustomerChargeDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerChargeDBR.cprid.in(custPackIds)).and(qCustomerChargeDBR.startdate.between(pendingDate, document.getEndate().toLocalDate()));
        List<CustomerChargeDBR> dbrList = customerChargeDBRRepository.findAllByCpridInAndInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(custPackIds, document.getId().longValue(), pendingDate, document.getEndate().toLocalDate());
        return dbrList;
    }

    /**
     * Gets pending revenue with tax at current date.
     * @param debitDocument the debit document
     * @return the pending revenue with tax at current date
     */
    public Double getPendingRevenueWithTaxAtCurrentDate(DebitDocument debitDocument) {
        Double pendingRevenue = 0d;
        DecimalFormat df = new DecimalFormat("#.00");
        if (debitDocument == null)
            return 0d;
        List<CustomerDBR> customerDBRList = getCustomerDBRListBetweenStartDateAndEndDate(LocalDate.now(), debitDocument);
        pendingRevenue = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(LocalDate.now())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));

        if (LocalDate.now().isBefore(debitDocument.getStartdate().toLocalDate())) {
            customerDBRList = getCustomerDBRListBetweenStartDateAndEndDate(debitDocument.getStartdate().toLocalDate(), debitDocument);
            pendingRevenue = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(debitDocument.getStartdate().toLocalDate())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
            if (pendingRevenue > 0.0d)
                pendingRevenue = (debitDocument.getTotalamount() / (debitDocument.getSubtotal() + debitDocument.getDiscount())) * pendingRevenue;
        } else {
            if (pendingRevenue > 0.0d)
                pendingRevenue = (debitDocument.getTotalamount() / (debitDocument.getSubtotal() + debitDocument.getDiscount())) * pendingRevenue;
        }
        pendingRevenue = Double.parseDouble(df.format(pendingRevenue));
        return pendingRevenue;
    }

    /**
     * Revert partner ledger and detail.
     * @param document the document
     * @param creditNoteAmount the credit note amount
     */
    public void revertPartnerLedgerAndDetail(DebitDocument document, Double creditNoteAmount) {
//        try {
//            Double adjustedCreditNoteAmount=creditNoteAmount;
//            Double totalAdjustedAmount=0.0;
//            QPartnerLedgerDetails qpartnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
//            BooleanExpression exp = qpartnerLedgerDetails.isNotNull();
//            exp = exp.and(qpartnerLedgerDetails.debitDocId.eq(document.getId().longValue())).and(qpartnerLedgerDetails.isDeleted.eq(false));
//            List<PartnerLedgerDetails> details = (List<PartnerLedgerDetails>) partnerLedgerDetailsRepository.findAll(exp);
//
//            List<CreditDebitDocMapping> creditDebitDocMappings=creditDebtMappingRepository.findBydebtDocId(document.getId());
//            List<Integer> creditDocIdList=creditDebitDocMappings.stream().map(x->x.getCreditDocId()).collect(Collectors.toList());
//            List<CreditDocument> creditDocuments=creditDocRepository.findAllByIdIn(creditDocIdList);
//            creditDocuments=creditDocuments.stream().filter(x->x.getStatus().equalsIgnoreCase(CommonConstants.PAYMENT_STATUS_PENDDING) || x.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_REJECTED)).collect(Collectors.toList());
//            creditDocIdList=creditDocuments.stream().map(x->x.getId()).collect(Collectors.toList());
//
//            creditDocIdList.stream().forEach(id->{
//                for(int i=0;i<creditDebitDocMappings.size();i++)
//                {
//                    if(creditDebitDocMappings.get(i).getCreditDocId().equals(id))
//                    {
//                        creditDebitDocMappings.remove(creditDebitDocMappings.get(i));
//                    }
//                }
//            });
//
//            if(creditDebitDocMappings!=null && !creditDebitDocMappings.isEmpty()) {
//                totalAdjustedAmount=creditDebitDocMappings.stream().mapToDouble(x->x.getAdjustedAmount()).sum();
//                adjustedCreditNoteAmount = creditDebitDocMappings.get(creditDebitDocMappings.size() - 1).getAdjustedAmount();
//            }
//
//            if(creditNoteAmount>0.0)
//            {
//                if (details != null && !details.isEmpty()) {
//                    List<PartnerLedgerDetails> commissionList = details.stream().filter(x -> x.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_COMMISSION)).collect(Collectors.toList());
//                    if (commissionList != null && !commissionList.isEmpty()) {
//                        Double commission = commissionList.stream().mapToDouble(x -> x.getCommission()).sum();
//                        if(document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() != null)
//                            commission = commissionList.stream().mapToDouble(x -> x.getAmount()).sum();
//
//                        Double prorateCommission = creditNoteAmount * commission / commissionList.get(0).getGrossOfferPrice();
//                        if (document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() != null) {
//                            revertCommission(document, creditNoteAmount, prorateCommission, commissionList, null);
//                        } else if (document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() == null) {
//                            revertCommission(document, creditNoteAmount, prorateCommission, commissionList, null);
//                        } else if (!document.getCustomer().getIs_from_pwc() && document.getCustomer().getPartner().getId() != CommonConstants.DEFAULT_PARTNER_ID) {
//                            revertCommission(document, creditNoteAmount, prorateCommission, commissionList, null);
//                        }
//                    }
//                    List<PartnerLedgerDetails> balanceList = details.stream().filter(x -> x.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_ADD_BALANCE) && x.getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)).collect(Collectors.toList());
//                    if (balanceList != null && !balanceList.isEmpty()) {
//                        revertCreditNoteAmount(document, creditNoteAmount);
//                    }
//                }
//                else if (!document.getCustomer().getIs_from_pwc() && document.getCustomer().getPartner().getId() != CommonConstants.DEFAULT_PARTNER_ID) {
//                    if(document.getTotalamount().doubleValue()> creditNoteAmount)
//                    {
//                        QTempPartnerLedgerDetail qTempPartnerLedgerDetail = QTempPartnerLedgerDetail.tempPartnerLedgerDetail;
//                        BooleanExpression expression = qTempPartnerLedgerDetail.isNotNull();
//                        expression = expression.and(qTempPartnerLedgerDetail.debitDocId.eq(document.getId().longValue())).and(qTempPartnerLedgerDetail.isDeleted.eq(false));
//                        expression=expression.and(qTempPartnerLedgerDetail.transcategory.eq(CommonConstants.TRANS_CATEGORY_COMMISSION));
//                        List<TempPartnerLedgerDetail> details1 = (List<TempPartnerLedgerDetail>) tempPartnerLedgerDetailsRepository.findAll(expression);
//                        if (details1 != null && !details1.isEmpty()) {
//                            Double commission = details1.stream().mapToDouble(x -> x.getCommission()).sum();
//                            Double prorateCommission = creditNoteAmount * commission / details1.get(0).getGrossOfferPrice();
//                            addRevertCommissionEntryInTmp(document,creditNoteAmount,prorateCommission,details1);
//                            if(creditDebitDocMappings!=null && !creditDebitDocMappings.isEmpty())
//                            {
//                                if(document.getTotalamount().doubleValue()==totalAdjustedAmount.doubleValue())
//                                {
//                                    QTempPartnerLedgerDetail qTempPartnerLedgerDetail1 = QTempPartnerLedgerDetail.tempPartnerLedgerDetail;
//                                    BooleanExpression exp1 = qTempPartnerLedgerDetail1.isNotNull();
//                                    exp1 = exp1.and(qTempPartnerLedgerDetail1.debitDocId.eq(document.getId().longValue())).and(qTempPartnerLedgerDetail1.isDeleted.eq(false));
//                                    List<TempPartnerLedgerDetail> detail = (List<TempPartnerLedgerDetail>) tempPartnerLedgerDetailsRepository.findAll(exp1);
//
//                                    if (detail != null && !detail.isEmpty()) {
//                                        tempPartnerLedgerDetailsRepository.deleteAll(detail);
//                                        partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(detail);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Unable to Add Revert Commission Against CreditNote :error{};exception{}", APIConstants.FAIL, e.getStackTrace());
//        }
    }

    /**
     * Gets customer charge dbr list between start date and end date andcust inv mapp id.
     * @param pendingDate the pending date
     * @param document the document
     * @param custInvMapp the cust inv mapp
     * @return the customer charge dbr list between start date and end date andcust inv mapp id
     */
    public List<CustomerChargeDBR> getCustomerChargeDBRListBetweenStartDateAndEndDateAndcustInvMappId(LocalDate pendingDate, DebitDocument document, List<Long> custInvMapp) {
//        QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
//        BooleanExpression expression = qCustomerChargeDBR.isNotNull();
//        expression = expression.and(qCustomerChargeDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerChargeDBR.startdate.between(pendingDate, document.getEndate().toLocalDate()))
//                .and(qCustomerChargeDBR.custInvMappingId.in(custInvMapp));
        List<CustomerChargeDBR> dbrList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAllByInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqualAndCustInvMappingIdIn(document.getId().longValue(), pendingDate, document.getEndate().toLocalDate(), custInvMapp);
        return dbrList;
    }

    /**
     * Gets customer charge dbr list between start date and end date.
     * @param pendingDate the pending date
     * @param document the document
     * @return the customer charge dbr list between start date and end date
     */
    public List<CustomerChargeDBR> getCustomerChargeDBRListBetweenStartDateAndEndDate(LocalDate pendingDate, DebitDocument document) {
//        QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
//        BooleanExpression expression = qCustomerChargeDBR.isNotNull();
//        expression = expression.and(qCustomerChargeDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerChargeDBR.startdate.between(pendingDate, document.getEndate().toLocalDate()));
        List<CustomerChargeDBR> dbrList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAllByInvoiceIdAndStartdateBetween(document.getId(), pendingDate, document.getEndate().toLocalDate());
        return dbrList;
    }

    /**
     * Gets customer dbr list between start date and end date and by service.
     * @param pendingDate the pending date
     * @param document the document
     * @param custPackIds the cust pack ids
     * @return the customer dbr list between start date and end date and by service
     */
    public List<CustomerDBR> getCustomerDBRListBetweenStartDateAndEndDateAndByService(LocalDate pendingDate, DebitDocument document, List<Long> custPackIds) {
//        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
//        BooleanExpression expression = qCustomerDBR.isNotNull();
//        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.cprid.in(custPackIds)).and(qCustomerDBR.startdate.between(pendingDate, document.getEndate().toLocalDate())).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAllByCpridInAndInvoiceIdAndStartdateGreaterThanEqualAndStartdateLessThanEqual(custPackIds, document.getId().longValue(), pendingDate, document.getEndate().toLocalDate());
        return dbrList;
    }

    /**
     * Add dbr for customer inventory charge.
     * @param custId the cust id
     * @param debitDocument the debit document
     */
    public void addDbrForCustomerInventoryCharge(Integer custId, DebitDocument debitDocument) {
        try {
            List<DebitDocDetails> debitDocDetails = debitDocument.getDebitDocDetailsList();
//        List<ItemCharge> inventoryCharges = message.getItemCharges();
            for (int i = 0; i < debitDocDetails.size(); i++) {
                Double applicableAmount = debitDocDetails.get(i).getSubtotal();
                Double tax = 0.0;
                Optional<Customers> customers = customersRepository.findById(custId);

                CustomerDBR dbr = new CustomerDBR();
                dbr.setInvoiceId(Long.valueOf(debitDocument.getId()));
                dbr.setCustid(Long.valueOf(custId));
                dbr.setStartdate(LocalDate.now());
                dbr.setEnddate(LocalDate.now());
                dbr.setDbr(applicableAmount);
                dbr.setPendingamt(0.0);
                dbr.setCustname(customers.get().getCustname());
                dbr.setStatus("Active");
                dbr.setCusttype(customers.get().getCusttype());
                dbr.setIsDirectCharge(true);
                dbr.setCumm_revenue(applicableAmount);
                dbr.setServiceId(-1l);
                dbr.setRemark("Inventory Direct Charge Added");
                if (customers.isPresent()) {
//                dbr.setServiceArea(customers.get().getServicearea().getId());
                    dbr.setBuId(customers.get().getBuId());
                    dbr.setMvnoId(customers.get().getMvnoId());
                }
                customerDBRRepository.save(dbr);
                addDbrForCustomerInventoryChargeForChargeLevel(customers, debitDocDetails.get(i), debitDocument.getId());
            }
        } catch (Exception ex) {
            logger.error("Exception at addDbrForCustomerInventoryCharge: " + ex.getMessage());
        }
//        ApplicationLogger.logger.info("End addDbrForCustomerInventoryCharge() ", APIConstants.SUCCESS, message);
    }

    private void addDbrForCustomerInventoryChargeForChargeLevel(Optional<Customers> customers, DebitDocDetails debitDocDetail, Integer invoiceId) {
        try {
            Double applicableAmount = debitDocDetail.getSubtotal();
            Double tax = 0.0;
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            if (debitDocDetail.getChargeid() != null)
                dbr.setChargeId(Long.parseLong(String.valueOf(debitDocDetail.getChargeid())));
            dbr.setInvoiceId(Long.valueOf(invoiceId));
            dbr.setCustid(customers.get().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(applicableAmount);
            dbr.setPendingamt(0.0);
            dbr.setCustname(customers.get().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(customers.get().getCusttype());
            dbr.setIsDirectCharge(true);
            dbr.setCumm_revenue(applicableAmount);
            dbr.setServiceId(-1l);
            dbr.setRemark("Inventory Direct Charge Added");
            if (customers.isPresent()) {
//            dbr.setServiceArea(customers.get().getServicearea().getId());
                dbr.setBuId(customers.get().getBuId());
                dbr.setMvnoId(customers.get().getMvnoId());
            }
            customerChargeDBRRepository.save(dbr);
        } catch (Exception ex) {
        }
    }

    /**
     * Update service area id for customer.
     * @param custId the cust id
     * @param serviceArea the service area
     * @param currentDate the current date
     */
    public void updateServiceAreaIdForCustomer(Integer custId, ServiceArea serviceArea, LocalDate currentDate) {
        if (custId != null && serviceArea != null) {
            List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.getAllByCustomerId(custId);
            dbrList = dbrList.stream().filter(x -> x.getStartdate().equals(currentDate)).collect(Collectors.toList());
            if (dbrList != null && !dbrList.isEmpty()) {
                dbrList.stream().forEach(x -> {
                    x.setServiceArea(serviceArea.getId());
                    customerDBRRepository.save(x);
                });
            }
        }
    }

    /**
     * Add day wise revenue.
     */
    @Scheduled(cron = "${cronjobtimeforrevenueeverydaymidnight}")
    public void addDayWiseRevenue() {
        logger.info("XXXXXXXXXXXX----------Day WiseRevenue Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.SCHEDULER_ADD_DAY_WISE_REVENUE);
        if (!schedulerLockService.isSchedulerLocked(CommonConstants.CRONJOB_Day_Wise_Revenue_Generate)) {
             schedulerLockService.acquireSchedulerLock(CommonConstants.CRONJOB_Day_Wise_Revenue_Generate);
            try {
                LocalDate currentDate = LocalDate.now();
                List<AggregateCount> aggregateList = customerDBRRepository.getAllByAggregateByDate(currentDate);
                if (aggregateList != null && !aggregateList.isEmpty()) {
                    aggregateList.stream().forEach(y -> {

                        String query = "Select  rev from CustomerDBR rev Where ";

                        query += "rev.startdate ='" + currentDate + "'";

                        if (y.getMvnoId() != null)
                            query += " and rev.mvnoId =" + y.getMvnoId().intValue();
                        else
                            query += " and rev.mvnoId IS NULL";

                        if (y.getBuId() != null)
                            query += " and rev.buId =" + y.getBuId();
                        else
                            query += " and rev.buId IS NULL";

                        if (y.getServiceAreaId() != null)
                            query += " and rev.serviceArea =" + y.getServiceAreaId();
                        else
                            query += " and rev.serviceArea IS NULL";

                        Query q = entityManager.createQuery(query, CustomerDBR.class);
                        List<CustomerDBR> dbrList = q.getResultList();
                        Double revenue = dbrList.stream().mapToDouble(x -> x.getDbr()).sum();
                        Double outstanding = dbrList.stream().mapToDouble(x -> x.getPendingamt()).sum();
                        CustomDailyRevenue dailyRevenue = new CustomDailyRevenue();
                        dailyRevenue.setDate(currentDate);
                        dailyRevenue.setRevenue(revenue);
                        dailyRevenue.setOutstanding(outstanding);
                        if (y.getMvnoId() != null)
                            dailyRevenue.setMvnoId(y.getMvnoId().intValue());
                        else
                            dailyRevenue.setMvnoId(null);

                        if (y.getBuId() != null)
                            dailyRevenue.setBuId(y.getBuId());
                        else
                            dailyRevenue.setBuId(null);

                        if (y.getServiceAreaId() != null)
                            dailyRevenue.setServiceAreaId(y.getServiceAreaId());
                        else
                            dailyRevenue.setServiceAreaId(null);
                        customDailyRevenueRepository.save(dailyRevenue);
                    });
                }
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Add Day Wise Revenue Generate Successfull");
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(aggregateList.size());

            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                logger.error(ex.toString(), ex);
                logger.error("**********Invoice Number Generate Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CommonConstants.CRONJOB_Day_Wise_Revenue_Generate);
                logger.info("XXXXXXXXXXXX---------- Add Day WiseRevenue Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Day WiseRevenue Scheduler Lock held by another instance");
            schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            logger.warn("XXXXXXXXXXXX----------Day WiseRevenue Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    /**
     * Add month wise revenue.
     */
    @Scheduled(cron = "${cronjobtimeforeverymonthfirstday}")
    public void addMonthWiseRevenue() {
        logger.info("XXXXXXXXXXXX----------Month Wise Revenue Schedular START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.SCHEDULER_ADD_MONTH_WISE_REVENUE);
        if (!schedulerLockService.isSchedulerLocked(CommonConstants.CRONJOB_EVERY_MONTH_FIRST_DAY)) {
            schedulerLockService.acquireSchedulerLock(CommonConstants.CRONJOB_EVERY_MONTH_FIRST_DAY);
            try {
                List<AggregateCount> aggregateList = customDailyRevenueRepository.getAllByAggregateByDate();
                LocalDate currentDate = LocalDate.now().minusDays(1);
                Integer month = currentDate.getMonth().getValue();
                Integer year = currentDate.getYear();
                aggregateList.stream().forEach(y -> {
                    String query = "Select  rev from CustomDailyRevenue rev Where ";

                    if (y.getMvnoId() != null)
                        query += "rev.mvnoId =" + y.getMvnoId().intValue();
                    else
                        query += "rev.mvnoId IS NULL";


                    if (y.getBuId() != null)
                        query += " and rev.buId =" + y.getBuId();
                    else
                        query += " and rev.buId IS NULL";

                    if (y.getServiceAreaId() != null)
                        query += " and rev.serviceAreaId =" + y.getServiceAreaId();
                    else
                        query += " and rev.serviceAreaId IS NULL";

                    Query q = entityManager.createQuery(query, CustomDailyRevenue.class);

                    List<CustomDailyRevenue> dailyRevenues = q.getResultList();
                    Double revenue = dailyRevenues.stream().filter(x -> x.getDate().getMonth().getValue() == month && x.getDate().getYear() == year).mapToDouble(x -> x.getRevenue()).sum();
                    Double outstanding = dailyRevenues.stream().mapToDouble(x -> x.getOutstanding()).sum();
                    CustomMonthlyRevenue monthlyRevenue = new CustomMonthlyRevenue();
                    monthlyRevenue.setMonth(currentDate.getMonthValue());
                    monthlyRevenue.setYear(year.toString());
                    monthlyRevenue.setRevenue(revenue);
                    monthlyRevenue.setOutstanding(outstanding);
                    if (y.getMvnoId() != null)
                        monthlyRevenue.setMvnoId(y.getMvnoId().intValue());
                    else
                        monthlyRevenue.setMvnoId(null);

                    if (y.getBuId() != null)
                        monthlyRevenue.setBuId(y.getBuId());
                    else
                        monthlyRevenue.setBuId(null);

                    if (y.getServiceAreaId() != null)
                        monthlyRevenue.setServiceAreaId(y.getServiceAreaId());
                    else
                        monthlyRevenue.setServiceAreaId(null);
                    customMonthlyRevenueRepository.save(monthlyRevenue);
                });
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Add Month Wise Revenue Schedular Run Sccessfull");
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(aggregateList.size());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                logger.error(ex.toString(), ex);
                logger.error("**********Scheduler Error***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CommonConstants.CRONJOB_EVERY_MONTH_FIRST_DAY);
                logger.info("XXXXXXXXXXXX---------- Add Month Wise Revenue Schedular Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Month Wise Revenue Schedular Lock held by another instance");
            schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            logger.warn("XXXXXXXXXXXX----------Month Wise Revenue Schedular Locked held by another instance ---------XXXXXXXXXXXX");
        }
    }

    /**
     * Dbr hold on service pause.
     * @param cprIds the cpr ids
     */
    public void dbrHoldOnServicePause(List<Long> cprIds) {
        List<CustomerDBR> dbrList = new ArrayList<>();
        List<CustomerChargeDBR> dbrChargeList = new ArrayList<>();
        dbrList = customerDBRRepository.findAllByCpridInAndStartdateAfter(cprIds, LocalDate.now().minusDays(1));
        dbrChargeList = customerChargeDBRRepository.findAllByCpridInAndStartdateAfter(cprIds, LocalDate.now().minusDays(1));
        moveDbrListIntoTemp(dbrList);
        moveDbrChargeListIntoTemp(dbrChargeList);
        customerDBRRepository.deleteAll(dbrList);
        customerChargeDBRRepository.deleteAll(dbrChargeList);
    }

    /**
     * Move dbr list into temp.
     * @param dbrList the dbr list
     */
    public void moveDbrListIntoTemp(List<CustomerDBR> dbrList) {
        if (dbrList != null && !dbrList.isEmpty()) {
            dbrList.stream().forEach(x -> {
                TempCustomerDBR dbr = new TempCustomerDBR();
                dbr.setBuId(x.getBuId());
                dbr.setCprid(x.getCprid());
                dbr.setCustname(x.getCustname());
                dbr.setCustid(x.getCustid());
                dbr.setCusttype(x.getCusttype());
                dbr.setDbrid(x.getDbrid());
                dbr.setDbr(x.getDbr());
                dbr.setInvoiceId(x.getInvoiceId());
                dbr.setEnddate(x.getEnddate());
                dbr.setStartdate(x.getStartdate());
                dbr.setRemark(x.getRemark());
                dbr.setServiceArea(x.getServiceArea());
                dbr.setCumm_revenue(x.getCumm_revenue());
                dbr.setDeleteFlag(x.getDeleteFlag());
                dbr.setIsDirectCharge(x.getIsDirectCharge());
                dbr.setMvnoId(x.getMvnoId());
                dbr.setOffer_price(x.getOffer_price());
                dbr.setPendingamt(x.getPendingamt());
                dbr.setPlanid(x.getPlanid());
                dbr.setPlanname(x.getPlanname());
                dbr.setStatus(x.getStatus());
                dbr.setValidity_days(x.getValidity_days());
                dbr.setPartnerId(x.getPartnerId());
                dbr.setServiceId(x.getServiceId());
                tempCustomerDBRRepository.save(dbr);
            });
        }
    }

    private void moveDbrChargeListIntoTemp(List<CustomerChargeDBR> dbrChargeList) {
        if (dbrChargeList != null && !dbrChargeList.isEmpty()) {
            dbrChargeList.stream().forEach(x -> {
                TempCustomerChargeDBR dbr = new TempCustomerChargeDBR();
                dbr.setBuId(x.getBuId());
                dbr.setCprid(x.getCprid());
                dbr.setCustname(x.getCustname());
                dbr.setCustid(x.getCustid());
                dbr.setCusttype(x.getCusttype());
                dbr.setDbrid(x.getDbrid());
                dbr.setDbr(x.getDbr());
                dbr.setInvoiceId(x.getInvoiceId());
                dbr.setEnddate(x.getEnddate());
                dbr.setStartdate(x.getStartdate());
                dbr.setRemark(x.getRemark());
                dbr.setServiceArea(x.getServiceArea());
                dbr.setCumm_revenue(x.getCumm_revenue());
                dbr.setDeleteFlag(x.getDeleteFlag());
                dbr.setIsDirectCharge(x.getIsDirectCharge());
                dbr.setMvnoId(x.getMvnoId());
                dbr.setOffer_price(x.getOffer_price());
                dbr.setPendingamt(x.getPendingamt());
                dbr.setPlanid(x.getPlanid());
                dbr.setPlanname(x.getPlanname());
                dbr.setStatus(x.getStatus());
                dbr.setValidity_days(x.getValidity_days());
                dbr.setServiceId(x.getServiceId());
                tempCustomerChargeDBRRepository.save(dbr);
            });
        }
    }


    /**
     * Dbr resume on service resume.
     * @param cprIds the cpr ids
     */
    public void dbrResumeOnServiceResume(List<Long> cprIds) {
        List<TempCustomerDBR> dbrList = new ArrayList<>();
        List<TempCustomerChargeDBR> dbrChargeList = new ArrayList<>();
        dbrList = tempCustomerDBRRepository.findAll(cprIds);
        dbrChargeList = tempCustomerChargeDBRRepository.findAll(cprIds);

        moveTempDbrListIntoMain(dbrList);
        moveTempDbrChargeListIntoMain(dbrChargeList);
        tempCustomerDBRRepository.deleteAll(dbrList);
        tempCustomerChargeDBRRepository.deleteAll(dbrChargeList);
    }

    private void moveTempDbrChargeListIntoMain(List<TempCustomerChargeDBR> dbrChargeList) {
        if (dbrChargeList != null && !dbrChargeList.isEmpty()) {
            List<Long> cprIds = dbrChargeList.stream().map(x -> x.getCprid()).distinct().collect(Collectors.toList());
            if (cprIds != null && !cprIds.isEmpty()) {
                cprIds.stream().forEach(cprId -> {
                    List<TempCustomerChargeDBR> list = dbrChargeList.stream().filter(x -> x.getCprid().equals(cprId)).collect(Collectors.toList());
                    AtomicReference<Integer> count = new AtomicReference<>(0);
                    LocalDate startDate = LocalDate.now();
                    list.stream().forEach(x -> {
                        CustomerChargeDBR dbr = new CustomerChargeDBR();
                        dbr.setBuId(x.getBuId());
                        dbr.setChargeId(x.getChargeId());
                        dbr.setCprid(x.getCprid());
                        dbr.setCustname(x.getCustname());
                        dbr.setPlanname(x.getPlanname());
                        dbr.setCustid(x.getCustid());
                        dbr.setCusttype(x.getCusttype());
                        dbr.setDbr(x.getDbr());
                        dbr.setInvoiceId(x.getInvoiceId());
                        dbr.setEnddate(x.getEnddate());
                        dbr.setStartdate(startDate.plusDays(count.get()));
                        dbr.setRemark(x.getRemark());
                        dbr.setServiceArea(x.getServiceArea());
                        dbr.setCumm_revenue(x.getCumm_revenue());
                        dbr.setDeleteFlag(x.getDeleteFlag());
                        dbr.setIsDirectCharge(x.getIsDirectCharge());
                        dbr.setMvnoId(x.getMvnoId());
                        dbr.setOffer_price(x.getOffer_price());
                        dbr.setPendingamt(x.getPendingamt());
                        dbr.setPlanid(x.getPlanid());
                        dbr.setStatus(x.getStatus());
                        dbr.setValidity_days(x.getValidity_days());
                        dbr.setServiceId(x.getServiceId());
                        customerChargeDBRRepository.save(dbr);
                        count.getAndSet(count.get() + 1);
                    });
                });
            }
        }
    }

    private void moveTempDbrListIntoMain(List<TempCustomerDBR> dbrList) {
        if (dbrList != null && !dbrList.isEmpty()) {
            List<Long> cprIds = dbrList.stream().map(x -> x.getCprid()).distinct().collect(Collectors.toList());
            if (cprIds != null && !cprIds.isEmpty()) {
                cprIds.stream().forEach(cprId -> {
                    List<TempCustomerDBR> list = dbrList.stream().filter(x -> x.getCprid().equals(cprId)).collect(Collectors.toList());
                    AtomicReference<Integer> count = new AtomicReference<>(0);
                    LocalDate startDate = LocalDate.now();
                    list.stream().forEach(x -> {
                        CustomerDBR dbr = new CustomerDBR();
                        dbr.setBuId(x.getBuId());
                        dbr.setCprid(x.getCprid());
                        dbr.setCustname(x.getCustname());
                        dbr.setPlanname(x.getPlanname());
                        dbr.setCustid(x.getCustid());
                        dbr.setCusttype(x.getCusttype());
                        dbr.setDbr(x.getDbr());
                        dbr.setInvoiceId(x.getInvoiceId());
                        dbr.setEnddate(x.getEnddate());
                        dbr.setStartdate(startDate.plusDays(count.get()));
                        dbr.setRemark(x.getRemark());
                        dbr.setServiceArea(x.getServiceArea());
                        dbr.setCumm_revenue(x.getCumm_revenue());
                        dbr.setDeleteFlag(x.getDeleteFlag());
                        dbr.setIsDirectCharge(x.getIsDirectCharge());
                        dbr.setMvnoId(x.getMvnoId());
                        dbr.setOffer_price(x.getOffer_price());
                        dbr.setPendingamt(x.getPendingamt());
                        dbr.setPlanid(x.getPlanid());
                        dbr.setStatus(x.getStatus());
                        dbr.setValidity_days(x.getValidity_days());
                        dbr.setServiceId(x.getServiceId());
                        customerDBRRepository.save(dbr);
                        count.getAndSet(count.get() + 1);
                    });
                });
            }
        }
    }

    public Map<Long, List<CustomerDBRPartial>> getCustomerDBRListForDocsBetweenStartDateAndEndDate(
            List<Long> docIds, LocalDate today) {
        List<CustomerDBRPartial> allDBRs = customerDBRRepository.findAllByDocIdsAndDate(docIds, today);
        return allDBRs.stream()
                .collect(Collectors.groupingBy(CustomerDBRPartial::getDebitDocId));
    }

}
