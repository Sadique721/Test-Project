package com.savbill.radius.CronJobs;

import com.savbill.radius.aaa.constant.AAAConstant;
import com.savbill.radius.aaa.db.DBCustomerDetails;
import com.savbill.radius.dto.quotaReset.CustQuotaResetDTO;
import com.savbill.radius.entity.*;
import com.savbill.radius.entity.CustPlanMappping;
import com.savbill.radius.entity.Customers;
import com.savbill.radius.entity.PostpaidPlan;
import com.savbill.radius.entity.SchedularAudit;
import com.savbill.radius.helper.CustomerPlanDataForResetQuota;
import com.savbill.radius.helper.CustomerQuotaReset;
import com.savbill.radius.kafka.KafkaConstant;
import com.savbill.radius.kafka.KafkaMessageData;
import com.savbill.radius.kafka.KafkaMessageSender;
import com.savbill.radius.repository.CustPlanMappingRepository;
import com.savbill.radius.repository.CustQuotaDetailsRepository;
import com.savbill.radius.repository.CustomersRepository;
import com.savbill.radius.repository.PostpaidPlanRepository;
import com.savbill.radius.services.SchedularAuditService;
import com.savbill.radius.services.impl.CustomerServiceImpl;
import com.savbill.radius.spring.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Component
@EnableScheduling
//@ConditionalOnProperty(name = "spring.enable.planexpiry.scheduling")
public class PostPaidPlanExpireryJob {
    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private PostpaidPlanRepository postpaidPlanRepository;

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private CustQuotaDetailsRepository custQuotaDetailsRepository;

    @Autowired
    private SchedularAuditService schedularAuditService;

    @Value("${spring.enable.planexpiry.scheduling}")
    private boolean renewCustomerFromRadius;

    private static final Logger log = LoggerFactory.getLogger(PostPaidPlanExpireryJob.class);

    /*
    public void cronJobForPostpaidPlanExpiry1() {
        SchedularAudit schedularAudit = new SchedularAudit();
        try {
            schedularAudit.setStartTime(LocalDateTime.now());
            schedularAudit.setSchedularName(AAAConstant.SCHEDULAR_UPADATE_CUSTOMER_NAME);
            log.info(String.format("Cron job run for PostPaid Plan expiry date at: %s ", LocalDateTime.now()));
            LocalDate todayLocaldate = LocalDate.now();
            List<Customers> customerList = customersRepository.findPostpaidCustomerByNextQuotaResetDate(todayLocaldate);
            HashMap<Integer, LocalDate> custNextBillDateMap = new HashMap<>();
            HashMap<Long, LocalDateTime> cprEndDateMap = new HashMap<>();
            HashMap<Integer, LocalDate> custNextQuotaDateMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(customerList)) {
                log.warn("Number of customers found for update next bill date: " + customerList.size());
                List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
                for (Customers customer : customerList) {
                    boolean renewCustomer = renewCustomerFromRadius && customer.getNextBillDate().isEqual(todayLocaldate) && customer.getCusttype().equalsIgnoreCase("Postpaid");

                    List<CustPlanMappping> planMapppings = custPlanMappingRepository.findAllByCustid(customer.getId());
                    List<CustPlanMappping> cprList = planMapppings.stream().
                            filter(customerPlan -> !customerPlan.getPurchaseType().equalsIgnoreCase("Bandwidthbooster") &&
                                    !customerPlan.getPurchaseType().equalsIgnoreCase("Volume Booster") && !customerPlan.getCustPlanStatus().equalsIgnoreCase("stop")).collect(Collectors.toList());//custPlanMappingRepository.findAllByCustid(customer.getId());
                    if (!CollectionUtils.isEmpty(cprList)) {
                        List<CustPlanMappping> updateCustPlanMappingList = new ArrayList<>();
                        for (CustPlanMappping customerPlan : cprList) {
                            PostpaidPlan plan = postpaidPlanRepository.findById(customerPlan.getPlanId()).orElse(null);
                            if (plan != null && renewCustomer) {
                                LocalDateTime nextEndDate = getdateBasedOnPlanValidaty(plan.getQuotaUnit(), plan.getValidity(), customerPlan.getEndDate());
                                customerPlan.setEndDate(nextEndDate);
                                customerPlan.setExpiryDate(nextEndDate);
                                log.debug("Update customer plan: " + customerPlan.getPlanName() + " ,endDate: " + nextEndDate + " ,customer: " + customer.getUsername());
                                updateCustPlanMappingList.add(customerPlan);
                                LocalDate custNextBillaDate = getdateBasedOnPlanValidaty(plan.getQuotaUnit(), plan.getValidity(), customer.getNextBillDate(), customer.getBillday());
                                customer.setNextBillDate(custNextBillaDate);
                                custNextBillDateMap.put(customer.getId(), custNextBillaDate);
                                cprEndDateMap.put(customerPlan.getId(), nextEndDate);
                            }
                            CustQuotaDetails custQuotaDetails = custQuotaDetailsRepository.findByCustPlanMapppingId(customerPlan.getId());
                            if (custQuotaDetails != null) {
                                UpdateCustomerQuotaDto customerQuotaDto = new UpdateCustomerQuotaDto();
                                customerQuotaDto.setMvnoId(Long.valueOf(customer.getMvnoId()));
                                customerQuotaDto.setCustId(customer.getId());
                                customerQuotaDto.setQuotaDetailId(custQuotaDetails.getId());
                                customerQuotaDto.setUserName(customer.getUsername());
                                customerQuotaDto.setUsedQuota(custQuotaDetails.getUsedQuota());
                                customerQuotaDto.setUsedQuotaKB(custQuotaDetails.getUsedQuotaKB());
                                customerQuotaDto.setUsedTimeQuota(custQuotaDetails.getTimeQuotaUsed());
                                customerQuotaDto.setUsedTimeQuotaSec(custQuotaDetails.getTimeUsedQuotaSec());
                                customerQuotaDto.setChunkAvailable(custQuotaDetails.isChunkAvailable());
                                if (custQuotaDetails.getReservedQuotaInPer() != null)
                                    customerQuotaDto.setReservedQuotaInPer(custQuotaDetails.getReservedQuotaInPer().intValue());
                                customerQuotaDto.setSkipQuotaUpdate(custQuotaDetails.getSkipQuotaUpdate());
                                LocalDate nexQuotaResetDate = getNextQuotaResetDate(customer, customer.getNextBillDate(), customerPlan, plan);
                                customerQuotaDto.setNextQuotaReset(nexQuotaResetDate);
                                customer.setNextQuotaResetDate(nexQuotaResetDate);
                                log.debug("Update customer plan Quota: " + customerPlan.getPlanName() + " ,customer: " + customer.getUsername());
                                customerService.updateCustomerQuota(customerQuotaDto);
                                custNextQuotaDateMap.put(customer.getId(), nexQuotaResetDate);
                            }
                            customersRepository.saveAndFlush(customer);
                        }
                        if (!CollectionUtils.isEmpty(updateCustPlanMappingList)) {
                            custPlanMappingRepository.saveAll(updateCustPlanMappingList);
                            custPlanMapppings.addAll(updateCustPlanMappingList);
                        } else
                            log.debug("No Customer plan found for renew customer: " + customer.getUsername());

                        CustNextBilldateMessage custNextBilldateMessage = new CustNextBilldateMessage(custNextBillDateMap, cprEndDateMap, custNextQuotaDateMap);
                        RadiusUtility radiusUtility = new RadiusUtility();
                        radiusUtility.SendCustNextBillDateMessageInfo(custNextBilldateMessage);
                    }
                }
            } else {
                log.info("There is no Customers found for update Next Bill Date..!");
            }
            schedularAudit.setStatus(AAAConstant.SCHEDULAR_STATUS_SUCCESS);
            schedularAudit.setEndTime(LocalDateTime.now());
            schedularAudit.setTotalCount(customerList.size());
            schedularAudit.setDescription("Customer Plan Update Successfully");
        } catch (Exception e) {
            schedularAudit.setStatus(AAAConstant.SCHEDULAR_STATUS_FAILURE);
            schedularAudit.setEndTime(LocalDateTime.now());
            schedularAudit.setDescription(e.getMessage());
        } finally {
            schedularAuditService.saveEntity(schedularAudit);
        }
    }
    */

    @Scheduled(cron = "${radius.postpaid.plan.expiry.schedule}")
    public void cronJobForPostpaidPlanExpiry() {
        LocalDate todayLocaldate = LocalDate.now();
        cronJobForPostpaidPlanExpiryWithDate(todayLocaldate);
    }

    public void cronJobForPostpaidPlanExpiryWithDate(LocalDate todayLocaldate) {

        HashMap<Integer, String> custNextQuotaResetDate = new HashMap<>(); //Customer and Next Quota Reset Date Mapping
        HashMap<Integer, String> custNextBillDate = new HashMap<>(); //Customer and NextBillDate Mapping
        HashMap<Long, String> cprLastQuotaResetDate = new HashMap<>(); //Customer and LastQuotaResetDate Mapping
        HashMap<Long, String> cprEndDate = new HashMap<>(); //CPR and EndDate Mapping

        SchedularAudit schedularAudit = new SchedularAudit();
        try {
            LocalDateTime today = LocalDateTime.now();
            schedularAudit.setStartTime(today);
            schedularAudit.setSchedularName(AAAConstant.SCHEDULAR_UPADATE_CUSTOMER_NAME);
            log.info(String.format("Cron job run for PostPaid Plan expiry date at: %s ", todayLocaldate));
            log.info(String.format("Cron job run for PostPaid Plan expiry date start at: %s , mill: %s", today, System.currentTimeMillis()));
            DBCustomerDetails dbCustomerDetails = new DBCustomerDetails();
            Map<Integer, CustomerQuotaReset> customerDtls = dbCustomerDetails.getCustomerPlansDtls(todayLocaldate.toString());
            int count = 0;
            if (customerDtls != null) {
                count = customerDtls.size();
                log.warn("Number of customers found for update next bill date: " + count);
            }
            for (CustomerQuotaReset customerQuotaReset : customerDtls.values()) {
                //update plan enddate
                boolean renewCustomer = renewCustomerFromRadius && customerQuotaReset.getNextBillDate().isEqual(todayLocaldate) && customerQuotaReset.getCustType().equalsIgnoreCase("Postpaid");
                LocalDate custNextBillaDate = null;
                String quotaResetInterval = "monthly"; // If value get null then default will set monthly
//                if (renewCustomer) {
                    for (CustomerPlanDataForResetQuota planData : customerQuotaReset.getCustomerPlanData()) {
                        if (planData.getUnitsofvalidity() != null && planData.getValidity() != null) {
                            LocalDateTime nextEndDate = getdateBasedOnPlanValidaty(planData.getUnitsofvalidity(), Double.valueOf(planData.getValidity()), today);
                            // If quota reset date and customer next bill date is same then update next bill date
                            if(todayLocaldate.equals(customerQuotaReset.getNextBillDate()))
                                custNextBillaDate = getdateBasedOnPlanValidaty(planData.getUnitsofvalidity(), Double.valueOf(planData.getValidity()), customerQuotaReset.getNextBillDate(), customerQuotaReset.getBillDay());
                            else
                                custNextBillaDate = customerQuotaReset.getNextBillDate();

                            quotaResetInterval = planData.getQuotarestinterval();

                            //add renew check flag
                            if (renewCustomer) {
                                dbCustomerDetails.updateCPRendDate(planData.getCprId(), nextEndDate);
                                cprEndDate.put(planData.getCprId(), nextEndDate.toString());
                            }
                            if (customerQuotaReset.getCdrId() != null && customerQuotaReset.getCdrId() != 0)
                                dbCustomerDetails.updateQuota(planData.getCprId(), true, today);
                            else
                                dbCustomerDetails.updateQuota(planData.getCprId(), false, today);
                            cprLastQuotaResetDate.put(planData.getCprId(), today.toString());
                            log.debug("Update customer plan: " + planData.getPlanName() + " ,endDate: " + nextEndDate + " ,customer: " + customerQuotaReset.getUsername());
                        }
                    }
//                }
                //update customer next bill date
                if (renewCustomer) {
                    LocalDate nexQuotaResetDate = getNextQuotaResetDate1(customerQuotaReset.getNextQuotaResetDate(), customerQuotaReset.getCustType(), custNextBillaDate, quotaResetInterval);
                    dbCustomerDetails.updateCustomerNextBillDate(customerQuotaReset.getCustId(), custNextBillaDate, nexQuotaResetDate);
                    custNextQuotaResetDate.put(customerQuotaReset.getCustId(), nexQuotaResetDate.toString());
                    if(custNextBillaDate != null)
                       custNextBillDate.put(customerQuotaReset.getCustId(), custNextBillaDate.toString());
                } else {
                    LocalDate nexQuotaResetDate = getNextQuotaResetDate1(customerQuotaReset.getNextQuotaResetDate(), customerQuotaReset.getCustType(), custNextBillaDate, quotaResetInterval);
                    dbCustomerDetails.updateCustomerNextBillDate(customerQuotaReset.getCustId(), custNextBillaDate, nexQuotaResetDate);
                    custNextQuotaResetDate.put(customerQuotaReset.getCustId(), nexQuotaResetDate.toString());
                    if(custNextBillaDate != null)
                        custNextBillDate.put(customerQuotaReset.getCustId(), custNextBillaDate.toString());
                }
            }
            schedularAudit.setStatus(AAAConstant.SCHEDULAR_STATUS_SUCCESS);
            schedularAudit.setEndTime(LocalDateTime.now());
            schedularAudit.setTotalCount(count);
            schedularAudit.setDescription("Customer Plan Update Successfully");
            //Send kafka call for CMS
            KafkaMessageSender kafkaMessageSender = SpringContext.getBean(KafkaMessageSender.class);
            CustQuotaResetDTO custQuotaResetDTO = new CustQuotaResetDTO(custNextQuotaResetDate, custNextBillDate, cprLastQuotaResetDate, cprEndDate);
            kafkaMessageSender.send(new KafkaMessageData(custQuotaResetDTO, custQuotaResetDTO.getClass().getSimpleName(), KafkaConstant.SEND_QUOTA_RESET));
            log.info(String.format("Cron job run for PostPaid Plan expiry date start at: %s , mill: %s", LocalDateTime.now(), System.currentTimeMillis()));
        } catch (Exception e) {
            e.printStackTrace();
            schedularAudit.setStatus(AAAConstant.SCHEDULAR_STATUS_FAILURE);
            schedularAudit.setEndTime(LocalDateTime.now());
            schedularAudit.setDescription(e.getMessage());
        } finally {
            schedularAuditService.saveEntity(schedularAudit);
        }

    }

    public LocalDateTime getdateBasedOnPlanValidaty(String validatyUnit, Double validaty, LocalDateTime date) {
        if (date != null) {
            //skip
        } else {
            date = LocalDateTime.now();
        }
        LocalDateTime updatedDate;
        switch (validatyUnit) {
            case "Days":
                updatedDate = date.plusDays(validaty.longValue());
                return updatedDate;

            case "Months":
                updatedDate = date.plusMonths(validaty.longValue());
                return updatedDate;

            case "Hours":
                updatedDate = date.plusHours(validaty.longValue());
                return updatedDate;

            default:
                return date;
        }
    }

    public LocalDate getdateBasedOnPlanValidaty(String validatyUnit, Double validaty, LocalDate date, Integer billDay) {
        if (billDay == null) {
            billDay = 1;
        }
        LocalDate updatedDate;
        switch (validatyUnit) {
            case "Days":
                updatedDate = date.plusDays(validaty.longValue());
                //return updatedDate;
                break;
            case "Months": {
                updatedDate = date.plusMonths(validaty.longValue());
                break;
            }
            case "Hours":
                return date;

            default:
                return date;
        }

        // Adjust the date based on billDay
        int year = updatedDate.getYear();
        int month = updatedDate.getMonthValue();

        // Ensure billDay is within the valid range for the month
        int lastDayOfMonth = updatedDate.lengthOfMonth();
        int adjustedBillDay = Math.min(billDay, lastDayOfMonth);
        LocalDate newUpdateDate = LocalDate.of(year, month, adjustedBillDay);
        if (newUpdateDate.getMonthValue() >= updatedDate.getMonthValue() && newUpdateDate.isAfter(LocalDate.now())) {
            return newUpdateDate;
        } else {
            return updatedDate;
        }
    }


    /**
     * Based on customer get NextQuotaResetDate
     *
     * @param customer
     * @param custNextBillDate
     * @param custPlanMappping
     * @param plan
     * @return
     */
    public LocalDate getNextQuotaResetDate(Customers customer, LocalDate custNextBillDate, CustPlanMappping custPlanMappping, PostpaidPlan plan) {

        LocalDate minQuotaResetDate = LocalDate.now().plusDays(400);
        LocalDate currentQuotaResetDate = LocalDate.now();
        if (customer.getNextQuotaResetDate() != null) {
            currentQuotaResetDate = customer.getNextQuotaResetDate();
            minQuotaResetDate = currentQuotaResetDate;
        }
        if (customer.getCusttype().equalsIgnoreCase("Postpaid")) {
            LocalDate quotaResetDate = calculateCustomerQuotaResetDate(custNextBillDate, plan.getQuotaResetInterval(), currentQuotaResetDate);
            if (quotaResetDate.isBefore(minQuotaResetDate)) minQuotaResetDate = quotaResetDate;
            return minQuotaResetDate;
        } else {
            LocalDate quotaResetDate = calculateCustomerQuotaResetDate(custPlanMappping.getEndDate().toLocalDate(), plan.getQuotaResetInterval(), currentQuotaResetDate);
            if (quotaResetDate.isBefore(minQuotaResetDate)) minQuotaResetDate = quotaResetDate;
            return minQuotaResetDate;

        }
    }

    /**
     * Based on nextQuotaResetDate and custTyp get NextQuotaResetDate
     *
     * @param nextQuotaResetDate
     * @param custType
     * @param custNextBillDate
     * @param custPlanMappping
     * @param plan
     * @return
     */
    public LocalDate getNextQuotaResetDate1(LocalDate nextQuotaResetDate, String custType, LocalDate custNextBillDate, String quotaResetInterval) {

        LocalDate minQuotaResetDate = LocalDate.now().plusDays(400);
//        LocalDate currentQuotaResetDate = LocalDate.now();
//        if (nextQuotaResetDate != null) {
//            currentQuotaResetDate = nextQuotaResetDate;
//            minQuotaResetDate = currentQuotaResetDate;
//        }
        LocalDate quotaResetDate = calculateCustomerQuotaResetDate(custNextBillDate, quotaResetInterval, nextQuotaResetDate);
        if (quotaResetDate.isBefore(minQuotaResetDate)) minQuotaResetDate = quotaResetDate;
        return minQuotaResetDate;
    }

    public LocalDate calculateCustomerQuotaResetDate(LocalDate endDate, String quotaResetInterval, LocalDate currentQuotaResetDate) {
        LocalDate nextBillDate = LocalDate.now();
        LocalDate today = LocalDate.now();
        quotaResetInterval = quotaResetInterval.toLowerCase();
//        LocalDate startDate = custPlanMappping.getStartDate(.).toLocalDate();
        switch (quotaResetInterval) {
            case "daily":
                //add daily quota reset
                nextBillDate = currentQuotaResetDate.plusDays(1);
                break;
            case "weekly":
                nextBillDate = currentQuotaResetDate.plusWeeks(1);
                break;
            case "monthly":
                nextBillDate = currentQuotaResetDate.plusMonths(1);
                break;
            default:
                nextBillDate = LocalDate.now().minusDays(1);
                break;
        }

        if (nextBillDate != null && nextBillDate.isAfter(endDate)) {
            nextBillDate = endDate;
        }
        return nextBillDate;
    }

    public void updateCustomerNextQuotaDate(Customers customer, CustPlanMappping custPlanMappping) {
        List<CustPlanMappping> planMapppings = custPlanMappingRepository.findAllByCustid(customer.getId());
        boolean isValid = !custPlanMappping.getPurchaseType().equalsIgnoreCase("Bandwidthbooster") &&
                !custPlanMappping.getPurchaseType().equalsIgnoreCase("Volume Booster") && !custPlanMappping.getCustPlanStatus().equalsIgnoreCase("stop");
//        List<CustPlanMappping> cprList = planMapppings.stream().
//                filter(customerPlan -> !customerPlan.getPurchaseType().equalsIgnoreCase("Bandwidthbooster") &&
//                        !customerPlan.getPurchaseType().equalsIgnoreCase("Volume Booster") && !customerPlan.getCustPlanStatus().equalsIgnoreCase("stop")).collect(Collectors.toList());//custPlanMappingRepository.findAllByCustid(customer.getId());
        if (isValid) {
            Optional<PostpaidPlan> plan = postpaidPlanRepository.findById(custPlanMappping.getPlanId());
            LocalDate nexQuotaResetDate = getNextQuotaResetDate(customer, customer.getNextBillDate(), custPlanMappping, plan.get());
            customer.setNextQuotaResetDate(nexQuotaResetDate);
            customersRepository.save(customer);
        }

    }
}
