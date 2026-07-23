package com.savbill.revenuemanagement.core.service.postpaid;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.InvoiceIntigration.InvoiceIntigrationService;
import com.savbill.revenuemanagement.autoassign.AutoRenewOrAddonPlanRequestDto;
import com.savbill.revenuemanagement.autoassign.AutoRenewOrAddonPlanService;
import com.savbill.revenuemanagement.autoassign.CustomerWalletPojo;
import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
import com.savbill.revenuemanagement.core.constants.APIConstants;
import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.constants.Constants;
import com.savbill.revenuemanagement.core.constants.LogConstants;
import com.savbill.revenuemanagement.core.dto.ChangePlanDto.ChildInvoiceDetails;
import com.savbill.revenuemanagement.core.dto.common.ResponseObject;
import com.savbill.revenuemanagement.core.dto.customer.CustPlanMapppingDto;
import com.savbill.revenuemanagement.core.dto.invoice.*;
import com.savbill.revenuemanagement.core.dto.invoice.*;
import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.*;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.repository.BillRun.BillRunRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustChargeDetailsRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerChargeHistoryRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerServiceMapRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocDetailRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocumentTAXRelRepository;
import com.savbill.revenuemanagement.core.repository.inventory.CustomerInventoryMappingRepo;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CustomerLedgerDtlsRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerRepository;
import com.savbill.revenuemanagement.core.repository.partner.PostpaidPlanChargeRepo;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.SchedulerLockService;
import com.savbill.revenuemanagement.core.service.common.NumberSequenceUtil;
import com.savbill.revenuemanagement.core.service.common.TransactionUtil;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.prepaid.*;
import com.savbill.revenuemanagement.core.service.prepaid.*;
import com.savbill.revenuemanagement.core.util.DateTimeUtil;
import com.savbill.revenuemanagement.isp.IspInvoicePayload;
import com.savbill.revenuemanagement.isp.IspMainPayload;
import com.savbill.revenuemanagement.isp.ServicePayload;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import com.savbill.revenuemanagement.rabbitmq.MessageReceiverWithThread;

import com.savbill.revenuemanagement.rabbitmq.messages.CustomerBillingMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.CreditDebitDocMessage;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAudit;
import com.savbill.revenuemanagement.scheduler.audit.SchedulerAuditService;
import com.savbill.revenuemanagement.server.InvoiceProcessor;
import com.savbill.revenuemanagement.server.CustomerData;
import com.savbill.revenuemanagement.server.DebitDocData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The type Postpaid invoice service.
 */
@Slf4j
@Service
public class PostpaidInvoiceService extends PostpaidInvoiceThread {

    private static final org.apache.log4j.Logger logger = Logger.getLogger(PostpaidInvoiceService.class);

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PrepaidInvoiceService prepaidInvoiceService;

    @Autowired
    private MessageReceiverWithThread messageReceiverWithThread;

    @Autowired
    private BillRunRepository billRunRepository;

    @Autowired
    private CustPlanMapppingRepository custPlanMappingRepository;

    //@Autowired
    // private MessageSender messageSender;

    @Autowired
    private CustomerChargeHistoryRepository customerChargeHistoryRepository;

    @Autowired
    private CustomerServiceMapRepository customerServiceMapRepository;

    @Autowired
    private CustChargeDetailsRepository custChargeDetailsRepository;

    /**
     * The Staff user repository.
     */
    @Autowired
    StaffUserRepository staffUserRepository;

    /**
     * The Post paid invoice util.
     */
    @Autowired
    PostPaidInvoiceUtil postPaidInvoiceUtil;

    /**
     * The Customer inventory mapping repo.
     */
    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private ClientServiceRepository clientServiceRepository;

    @Autowired
    private DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;

    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;


    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private DbrService dbrService;

    @Autowired
    private PartnerCommissionService partnerCommissionService;

    @Autowired
    private TaxService taxService;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;
    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private CustomerLedgerDtlsRepository customerLedgerDtlsRepository;
    /**
     * The Entity manager.
     */
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private Tracer tracer;

    /**
     * The Kafka message sender.
     */
    @Autowired
    KafkaMessageSender kafkaMessageSender;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private InvoiceIntigrationService invoiceIntigrationService;

    @Autowired
    private AutoRenewOrAddonPlanService autoRenewOrAddonPlanService;

    @Value("${spring.enable.auto.assign.renewal.scheduling: false}")
    private String isAutoAssignRenewalSchecudlerEnabled;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Value("${postpaid.invoice.day.midnight.isNextBilldate}")
    private boolean isNextBilldate;

    @Autowired
    private TransactionUtil transactionUtil;

    /**
     * Create postpaid invoice response object.
     * @param billDateStr the bill date str
     * @param earlyBill the early bill
     * @return the response object
     */
//    @Transactional
    public ResponseObject createPostpaidInvoice(String billDateStr, Boolean earlyBill) {
        ResponseObject response = new ResponseObject();
        try {
            logger.info("Initiating PostpaidInvoice Generation  Process for Billdate  " + billDateStr);
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate billDate = new DateTimeUtil().convertDateToDifferenFormat(inputFormatter, outputFormatter, billDateStr);
            LoggedInUser user = getLoggedInUser();
            List<BigInteger> custIds = new ArrayList<>();

            if (earlyBill != null && earlyBill) {
                if (!billDate.equals(LocalDate.now()) && billDate.isAfter(LocalDate.now())) {
                    response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
                    response.setResponseMessage("Not allowed to generate future bill!");
                    return response;
                }
            }


            //query to fetch custId as per next bill date
            String query = null;
            if (earlyBill) {
                logger.debug("PostpaidInvoice Generation  Process on EarlyBilldate  " + billDate);
                query = "Select rev.custid from tblcustomers rev Where rev.customertype = 'Postpaid' and rev.cstatus = 'Active' and date(rev.earlybilldate) = '" + billDate + "'";
            } else {
                query = "Select rev.custid from tblcustomers rev Where rev.customertype = 'Postpaid' and rev.cstatus = 'Active' and date(rev.NEXTBILLDATE) = '" + billDate + "'";
            }
            if (user.getPartnerId() != null && !Objects.equals(user.getPartnerId(), Constants.USER_CONSTANTS.DEFAULT_PARTNER_ID)) {
                query = query + " and rev.partnerid = " + user.getPartnerId();
            }

            Integer mvnoId = getLoggedInUser().getMvnoId();
            logger.debug("Initiating PostpaidInvoice Generation  Process for EarlyBilldate  " + billDate);
            List<Long> buIdsList = getLoggedInUser().getBuIds();
            List<Integer> serviceAreaIdsList = getLoggedInUser().getServiceAreaIdList();
            if (mvnoId != null) {
                //expression = expression.and(qCustomDailyRevenue.mvnoId.eq(mvnoId));
                query += " and rev.MVNOID =" + mvnoId;
            } else {
                //  expression = expression.and(qCustomDailyRevenue.mvnoId.isNull());
                query += " and (rev.MVNOID ='null')";

            }
            if (!CollectionUtils.isEmpty(buIdsList)) {
                String formattedList = "(" + buIdsList.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + ")";
                query += " and (rev.BUID In " + formattedList + ")";
                // expression = expression.and(qCustomDailyRevenue.buId.in(buIdsList));
            }
            if (!CollectionUtils.isEmpty(serviceAreaIdsList)) {
                String formattedList = "(" + serviceAreaIdsList.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + ")";
                query += " and rev.servicearea_id In " + formattedList;
                //    expression = expression.and(qCustomDailyRevenue.serviceAreaId.in(serviceAreaIdsList));
            }

            query += " and (rev.is_using_by_thread IS NULL OR rev.is_using_by_thread=false) ";

            Query q = entityManager.createNativeQuery(query);
            custIds = q.getResultList();
            if (CollectionUtils.isEmpty(custIds)) {
                response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
                response.setResponseMessage("No customer found for given Date!");
                return response;
            }
            BillRun billRun = addBillRunData(0, 0d, 0, 0);
            Map<String, Object> data = new HashMap<>();
            List<Integer> parentIds = customersRepository.findParentIds(custIds);
            if (parentIds != null && parentIds.size() > 0) {
                custIds = custIds.stream().filter(i -> parentIds.contains(i.intValue())).collect(Collectors.toList());
            }
            List<CustomerBillingMessage> messages = createCustomerBillingMessage(custIds.stream().map(BigInteger::intValue).collect(Collectors.toList()), billDateStr, billRun.getId(), user != null ? user.getStaffId() : null);
            if (!CollectionUtils.isEmpty(messages)) {
                for (CustomerBillingMessage msg : messages) {

                    TraceContext traceContext = tracer.currentSpan().context();
                    msg.setTraceContext(traceContext);
                    logger.info("Initiating InvoiceProcessor method   for Billdate  " + billDate);
                    InvoiceProcessor invoiceProcessor = new InvoiceProcessor(msg, custPlanMappingRepository, prepaidInvoiceService, messageReceiverWithThread, billDate, customersRepository);
                    getInvoicePool().execute(invoiceProcessor);

//                    msg.getData().put(CustomerBillingMessage.POSTPAIDADVANCE,"Both");
//                    Map<String, Object> datas = msg.getData();
//                    msg.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
//                    Integer custId = (Integer) datas.get(CustomerBillingMessage.CUST_ID);
//                    List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByCustomerIdAndEndDate(custId,billDate.atStartOfDay().minusSeconds(1));
//                    List<Integer> activeCprIds = custPlanMapppings.stream().map(i->i.getId()).collect(Collectors.toList());
//                    if (custPlanMapppings.size()>0){
//                        custPlanMapppings = custPlanMapppings.stream().peek(x->x.setCustPlanStatus("STOP")).collect(Collectors.toList());
//                        custPlanMappingRepository.saveAll(custPlanMapppings);
//                        ChangePlanMessage changePlanMessage = new ChangePlanMessage();
//                        changePlanMessage.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
//                        Integer count = customerChargeHistoryRepository.countAllByCustPlanMapppingIdInAndChargeType(activeCprIds,CommonConstants.CHARGE_TYPE_RECURRING);
//                        if (count>0) {
//                            prepaidInvoiceService.createInvoiceForPostpaidChangePlanProrate(custId.intValue(), changePlanMessage, activeCprIds, billDate);
//                        }
//                    }
//                    msg.setIsEarlyBillDate(earlyBill);
//                    msg.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
//                    messageReceiverWithThread.receiveBillingInvoiceMessageForManual(msg);
                }
            }

            response.setResponseCode(HttpStatus.OK.toString());
            response.setResponseMessage("Invoice has been generated successfully.");
//            synchronized (this.entityManager)
//            {
//                synchronized (this)
//                {
//                    Thread.sleep(3000);
//                    Query q = entityManager.createNativeQuery(query);
//                    custIds = q.getResultList();
//
//                    if(custIds!=null && !custIds.isEmpty())
//                    {
//                        String formattedList = "(" + custIds.stream()
//                                .map(String::valueOf)
//                                .collect(Collectors.joining(", ")) + ")";
//                        String updateQuery="UPDATE tblcustomers SET is_using_by_thread=true WHERE custid IN "+formattedList;
//                        entityManager.createNativeQuery(updateQuery).executeUpdate();
//                    }
//                }
//            }
            synchronizedMethod(query);

            System.out.println("*****.29 server logs for invoice generation inside createPostpaidInvoice Method************");
            System.out.println("*****.29 server logs for invoice generation :::: ************ " + LocalDateTime.now());


        } catch (Exception ex) {
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
            response.setResponseMessage(Constants.FAIL_REPLY);
            response.setResponseObject(new Object());
            logger.error(ex.toString(), ex);
        }
        return response;
    }

    /**
     * Synchronized method.
     * @param query the query
     */
    @Transactional
    public void synchronizedMethod(String query) {
//        try {
//            List<BigInteger> custIds=new ArrayList<>();
//            synchronized (this.entityManager) {
//                synchronized (this) {
//                    Thread.sleep(3000);
//                    Query q = entityManager.createNativeQuery(query);
//                    custIds = q.getResultList();
//
//                    if (custIds != null && !custIds.isEmpty()) {
//                        String formattedList = "(" + custIds.stream()
//                                .map(String::valueOf)
//                                .collect(Collectors.joining(", ")) + ")";
//                        String updateQuery = "UPDATE tblcustomers SET is_using_by_thread=true WHERE custid IN " + formattedList;
//                        entityManager.createNativeQuery(updateQuery).executeUpdate();
//                    }
//                }
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

    }

    /**
     * Create postpaid trial invoice response object.
     * @param billDateStr the bill date str
     * @return the response object
     */
    @Transactional
    public ResponseObject createPostpaidTrialInvoice(String billDateStr) {
        ResponseObject response = new ResponseObject();
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate billDate = new DateTimeUtil().convertDateToDifferenFormat(inputFormatter, outputFormatter, billDateStr);
            LoggedInUser user = getLoggedInUser();

            //query to fetch custId as per next bill date
            String query = "Select rev.custid from tblcustomers rev Where rev.customertype = 'Postpaid' and rev.cstatus = 'Active' and date(rev.NEXTBILLDATE) = '" + billDate + "'";

            if (user.getPartnerId() != null && !Objects.equals(user.getPartnerId(), Constants.USER_CONSTANTS.DEFAULT_PARTNER_ID)) {
                query = query + " and rev.partnerid = " + user.getPartnerId();
            }

            Integer mvnoId = getLoggedInUser().getMvnoId();
            List<Long> buIdsList = getLoggedInUser().getBuIds();
            List<Integer> serviceAreaIdsList = getLoggedInUser().getServiceAreaIdList();
            if (mvnoId != null) {
                //expression = expression.and(qCustomDailyRevenue.mvnoId.eq(mvnoId));
                query += " and rev.MVNOID =" + mvnoId;
            } else {
                //  expression = expression.and(qCustomDailyRevenue.mvnoId.isNull());
                query += " and (rev.MVNOID ='null')";

            }
            if (!CollectionUtils.isEmpty(buIdsList)) {
                String formattedList = "(" + buIdsList.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + ")";
                query += " and (rev.BUID In " + formattedList + ")";
                // expression = expression.and(qCustomDailyRevenue.buId.in(buIdsList));
            }
            if (!CollectionUtils.isEmpty(serviceAreaIdsList)) {
                String formattedList = "(" + serviceAreaIdsList.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ")) + ")";
                query += " and rev.servicearea_id In " + formattedList;
                //    expression = expression.and(qCustomDailyRevenue.serviceAreaId.in(serviceAreaIdsList));
            }
            Query q = entityManager.createNativeQuery(query);
//            List<Integer> custsIds = customersRepository.getCustomersByQuery();
            List<BigInteger> custIds = q.getResultList();
            if (CollectionUtils.isEmpty(custIds)) {
                response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
                response.setResponseMessage("No customer found for given Date!");
                return response;
            }
            BillRun billRun = addBillRunData(0, 0d, 0, 0);
            List<CustomerBillingMessage> messages = createCustomerBillingMessage(custIds.stream().map(BigInteger::intValue).collect(Collectors.toList()), billDateStr, billRun.getId(), user != null ? user.getStaffId() : null);
            if (!CollectionUtils.isEmpty(messages)) {
                for (CustomerBillingMessage msg : messages) {
                    messageReceiverWithThread.receiveBillingInvoiceMessageForManual(msg);
                }

            }

            response.setResponseCode(HttpStatus.OK.toString());
            response.setResponseMessage("Invoice has been generated successfully.");

        } catch (Exception ex) {
            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.toString());
            response.setResponseMessage(Constants.FAIL_REPLY);
            response.setResponseObject(new Object());
            logger.error(ex.toString(), ex);
        }
        return response;
    }


    /**
     * Add bill run data bill run.
     * @param billrunCount the billrun count
     * @param invoiceAmount the invoice amount
     * @param successCount the success count
     * @param failCount the fail count
     * @return the bill run
     */
    public BillRun addBillRunData(Integer billrunCount, Double invoiceAmount, Integer successCount, Integer failCount) {
        Long buid = null;//getLoggedInUser().getBuIds().get(0);
        if (getLoggedInUser() != null && !CollectionUtils.isEmpty(getLoggedInUser().getBuIds())) {
            buid = getLoggedInUser().getBuIds().get(0);
        }
        BillRun billRun = new BillRun(billrunCount, invoiceAmount, "Pending", LocalDateTime.now(),
                false, getLoggedInUser() != null ? getLoggedInUser().getMvnoId() : null, "Postpaid", buid, getLoggedInUser() != null ? getLoggedInUser().getPartnerId() : null,
                successCount, failCount);
        return billRunRepository.save(billRun);
    }


    /**
     * Add bill run data post bill run.
     * @param billrunCount the billrun count
     * @param invoiceAmount the invoice amount
     * @param successCount the success count
     * @param failCount the fail count
     * @param mvnoid the mvnoid
     * @param buid the buid
     * @return the bill run
     */
    public BillRun addBillRunDataPost(Integer billrunCount, Double invoiceAmount, Integer successCount, Integer failCount, Integer mvnoid, Long buid) {
        BillRun billRun = new BillRun(billrunCount, invoiceAmount, "Pending", LocalDateTime.now(),
                false, mvnoid, "Postpaid", buid, null, successCount, failCount);
        return billRunRepository.save(billRun);
    }

    /**
     * Create customer billing message list.
     * @param custIds the cust ids
     * @param billDateStr the bill date str
     * @param billRunId the bill run id
     * @param loggedInStaffUser the logged in staff user
     * @return the list
     */
    public List<CustomerBillingMessage> createCustomerBillingMessage(List<Integer> custIds, String billDateStr, Integer billRunId, Integer loggedInStaffUser) {
        List<CustomerBillingMessage> response = new ArrayList<>();
        if (!CollectionUtils.isEmpty(custIds)) {
            for (Integer custId : custIds) {
                Map<String, Object> data = new HashMap<>();
                data.put(CustomerBillingMessage.CUST_ID, custId);
                data.put("isCancelRegenerate", false);
//                data.put("createdByName", "parth");
                data.put("paymentOwnerId", -1);
                data.put("isCAFCustomer", false);
                data.put("renewalId", null);
                data.put("partnerLedgerMappingId", null);
//                data.put("updateByName", "parth");
                data.put("nextbilldate", LocalDateTime.now());
                data.put("packageRelId", null);
                data.put("oldDebitDocId", null);
                data.put("currentUserLoggedInId", loggedInStaffUser);
                data.put("chargeId", null);
                data.put("inventoryMappingId", null);
                data.put("isFromFlutterWave", false);
                data.put("invoiceType", null);
                data.put("strDate", billDateStr);
                data.put(CustomerBillingMessage.BILL_RUN_ID, billRunId);
                List<Integer> childIds = customerServiceMapRepository.findChildIds(custId, LocalDate.now());
                CustomerBillingMessage message = new CustomerBillingMessage(data);
                if (!CollectionUtils.isEmpty(childIds)) {
                    message.setChildIds(childIds);
                }
                response.add(message);
            }
        }
        return response;
    }

    /**
     * Create customer trial billing message list.
     * @param custIds the cust ids
     * @param billDateStr the bill date str
     * @param billRunId the bill run id
     * @param loggedInStaffUser the logged in staff user
     * @return the list
     */
    public List<CustomerBillingMessage> createCustomerTrialBillingMessage(List<Integer> custIds, String billDateStr, Integer billRunId, Integer loggedInStaffUser) {
        List<CustomerBillingMessage> response = new ArrayList<>();
        if (!CollectionUtils.isEmpty(custIds)) {
            for (Integer custId : custIds) {
                Map<String, Object> data = new HashMap<>();
                data.put(CustomerBillingMessage.CUST_ID, custId);
                data.put("isCancelRegenerate", false);
//                data.put("createdByName", "parth");
                data.put("paymentOwnerId", -1);
                data.put(Constants.INVOICE_TYPE.IS_CAF_CUSTOMER, true);
                data.put("renewalId", null);
                data.put("partnerLedgerMappingId", null);
                data.put("nextbilldate", LocalDateTime.now());
                data.put("packageRelId", null);
                data.put("oldDebitDocId", null);
                data.put("currentUserLoggedInId", loggedInStaffUser);
                data.put("chargeId", null);
                data.put("inventoryMappingId", null);
                data.put("isFromFlutterWave", false);
                data.put("invoiceType", null);
                data.put("strDate", billDateStr);
                data.put("type", "trial");
                data.put(CustomerBillingMessage.BILL_RUN_ID, billRunId);
                CustomerBillingMessage message = new CustomerBillingMessage(data);
                response.add(message);
                message.setType("CAF");
            }
        }
        return response;
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
     * Postpaid invoice scheduler.
     */
    @Scheduled(cron = "${cronjobforpostpaidinvoicedaymidnight}")
    @Transactional
    public void postpaidInvoiceScheduler() {
        logger.info("XXXXXXXXXXXX----------PostPaid-Invoice Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.SCHEDULER_POSTPAID_INVOICE);
        if (!schedulerLockService.isSchedulerLocked(CommonConstants.POSTPAID_INVOICE_DAY_MIDNIGHT)) {
            schedulerLockService.acquireSchedulerLock(CommonConstants.POSTPAID_INVOICE_DAY_MIDNIGHT);
            try {
                prepaidInvoiceService.postpaidCustomerInstallmentForDirectCharge();
                if (isAutoAssignRenewalSchecudlerEnabled.equalsIgnoreCase("true")) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            prepaidCustomerAutoRenewalOrAddon();
                        } catch (Exception e) {
                            logger.error("Error processing during Auto assign renew : ", e);
                        }
                    });
                }
                logger.info("**********Postpaid Invoice Scheduler Started***********");
                String billDateStr = LocalDate.now().toString().replaceAll("-", "");
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate billDate = new DateTimeUtil().convertDateToDifferenFormat(inputFormatter, outputFormatter, billDateStr);
                LoggedInUser user = getLoggedInUser();
                List<BigInteger> custIds = new ArrayList<>();

                String query = "";
                if(isNextBilldate){
                    query = "Select rev.custid from tblcustomers rev Where rev.customertype = 'Postpaid' and rev.cstatus = 'Active' and date(rev.NEXTBILLDATE) = '" + billDate + "'";
                } else {
                    query = "Select rev.custid from tblcustomers rev Where rev.customertype = 'Postpaid' and rev.cstatus = 'Active' and date(rev.earlybilldate) = '" + billDate + "'";
                }

                query += " and (rev.is_using_by_thread IS NULL OR rev.is_using_by_thread=false)";

                Query q = entityManager.createNativeQuery(query);
                custIds = q.getResultList();

                if (custIds != null && !custIds.isEmpty()) {
                    String formattedList = "(" + custIds.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")) + ")";
                    String updateQuery = "UPDATE tblcustomers SET is_using_by_thread=true WHERE custid IN " + formattedList;
                    entityManager.createNativeQuery(updateQuery).executeUpdate();
                } else {
                    schedulerAudit.setEndTime(LocalDateTime.now());
                    schedulerAudit.setDescription("Customer Ids Empty");
                    schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                    schedulerAudit.setTotalCount(custIds.size());
                    return;
                }
                BillRun billRun = addBillRunDataPost(0, 0d, 0, 0, null, null);
                List<CustomerBillingMessage> messages = createCustomerBillingMessage(custIds.stream().map(BigInteger::intValue).collect(Collectors.toList()), billDateStr, billRun.getId(), user != null ? user.getStaffId() : 2);
                if (!CollectionUtils.isEmpty(messages)) {
                    for (CustomerBillingMessage msg : messages) {
                        TraceContext traceContext = tracer.currentSpan().context();
                        msg.setTraceContext(traceContext);
                        InvoiceProcessor invoiceProcessor = new InvoiceProcessor(msg, custPlanMappingRepository, prepaidInvoiceService, messageReceiverWithThread, billDate, customersRepository);
                        getInvoicePool().execute(invoiceProcessor);
                    }
                }
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Postpaid Invoice Generation Successfull");
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(custIds.size());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                logger.error(ex.toString(), ex);
                logger.error("**********Scheduler Error***********");
            } finally {
            schedulerAuditService.saveEntity(schedulerAudit);
            schedulerLockService.releaseSchedulerLock(CommonConstants.POSTPAID_INVOICE_DAY_MIDNIGHT);
            logger.info("XXXXXXXXXXXX---------- Day WiseRevenue Scheduler Locked released ---------XXXXXXXXXXXX");
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
     * Invoice number generate scheduler.
     */
    @Scheduled(cron = "${cronjobforgenerateinvoicenumber}")
    @Transactional
    public void invoiceNumberGenerateScheduler() {
        logger.info("XXXXXXXXXXXX----------PostPaid-Invoice-Number Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(Constants.SCHEDULER_AUDIT.SCHEDULER_GENERATE_INVOICE_NUMBER);
        if (!schedulerLockService.isSchedulerLocked(CommonConstants.GENERATE_POSTPAID_INVOICE_NUMBER)) {
            schedulerLockService.acquireSchedulerLock(CommonConstants.GENERATE_POSTPAID_INVOICE_NUMBER);
            try {
                logger.info("**********Invoice Number Generate Scheduler Started***********");
                List<DebitDocData> debitDocumentIds = debitDocRepository.findByDocnumberIsEmptyAndUsedByThreadIsFalse();
                logger.info("**********Total Invoice Number Will be Generate " + debitDocumentIds.size());

                List<ClientService> clientServices = clientServiceRepository.findAllByNameAndMvnoIdIn(CommonConstants.IS_ISP_INVOICE_PAYLOAD_SEND, Arrays.asList(1));
                List<IspInvoicePayload> ispPayloads = new ArrayList<>();
                IspMainPayload ispMainPayloads = new IspMainPayload();
                List<Integer> invoiceIds = new ArrayList<>();
                Map<Integer, List<Integer>> mvnoToDebitDocIdsMap = new HashMap<>();
                if (debitDocumentIds != null && !debitDocumentIds.isEmpty()) {
                    debitDocumentIds.stream().forEach(debitDocData -> {
                        CustomerData customerData = customersRepository.findByCustomerId(debitDocData.getSubscriberid());
                        if (customerData != null) {
                            boolean isLco = false;
                            if (customerData.getIsLco() != null)
                                isLco = true;
                            generateInvoiceNumber(debitDocData.getDebitDocumentId(), isLco, customerData.getMvnoId(), customerData.getPartnerId(), customerData);
                            // Group DebitDocIds by mvnoId
                            mvnoToDebitDocIdsMap.putIfAbsent(customerData.getMvnoId(), new ArrayList<>());
                            mvnoToDebitDocIdsMap.get(customerData.getMvnoId()).add(debitDocData.getDebitDocumentId());
                            if (clientServices != null && !clientServices.isEmpty()) {
                                if (clientServices.get(clientServices.size() - 1).getValue().equalsIgnoreCase("1")) {
                                    Long mvnoDebitDocCount = debitDocDetailRepository.countByMvnoDebitDocumentId(debitDocData.getDebitDocumentId());
                                    if (mvnoDebitDocCount > 0) {
                                        IspInvoicePayload invoicePayload = generateIspInvoiceDetailPayload(debitDocData.getDebitDocumentId(), debitDocData.getSubscriberid());
                                        ispPayloads.add(invoicePayload);
                                        invoiceIds.add(debitDocData.getDebitDocumentId());
                                    }
                                }
                            }
                        }
                    });
                }
                ispMainPayloads.setInvoicePayloads(ispPayloads);
                ispMainPayloads.setInvoiceIds(invoiceIds);
                sendIspInvoicePayloadToIntegrationService(ispMainPayloads);
                /** Submit tasks for each MVNO (with concurrency limit) **/
                mvnoToDebitDocIdsMap.forEach((mvnoId, debitDocIds) -> {
                    CompletableFuture.runAsync(() -> {
                        try {
                            /** Fetch the integration type for the MVNO **/
                            List<String> integrationTypeList = invoiceIntigrationService.getIntegrationTypeForMvno(mvnoId);
                            if (!integrationTypeList.isEmpty()) {
                                for (String integrationType : integrationTypeList) {
                                    /** Call the appropriate integration API with the debitDocIds **/
                                    invoiceIntigrationService.processIntegrationForMvno(mvnoId, debitDocIds, integrationType);
                                }
                            } else {
                                logger.warn("IntegrationTypeList is empty no api calls required");
                            }
                        } catch (Exception e) {
                            logger.error("Error processing integration for MVNO: " + mvnoId, e);
                        }
                    });
                });
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Invoice Number Generation Successfull");
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(debitDocumentIds.size());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                logger.error(ex.toString(), ex);
                logger.error("**********Invoice Number Generate Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CommonConstants.GENERATE_POSTPAID_INVOICE_NUMBER);
                logger.info("XXXXXXXXXXXX----------PostPaid Invoice Number Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("PostPaid Invoice Number Scheduler Lock held by another instance");
            schedulerAudit.setStatus(Constants.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            logger.warn("XXXXXXXXXXXX----------PostPaid Invoice Number Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }

    }


    /**
     * Generate invoice number string.
     * @param debitDocId the debit doc id
     * @param isLco the is lco
     * @param mvnoId the mvno id
     * @param partnerId the partner id
     * @param customerData the customer data
     * @return the string
     */
    @Transactional
    public String generateInvoiceNumber(Integer debitDocId, Boolean isLco, Integer mvnoId, Integer partnerId, CustomerData customerData) {
        try {
            String number = numberSequenceUtil.getInvoiceNumber(isLco, partnerId, mvnoId);
            logger.info("Document number " + number + " generated for DebitDocument id " + debitDocId);
            debitDocRepository.updateDebitDocumentNumber(debitDocId, number);
            if (customerData.getCusttype() != null && customerData.getCusttype().equalsIgnoreCase("Postpaid")) {
//                prepaidInvoiceService.sendInvoiceEmailFromScheduler(debitDocId, number, customerData);
//                debitDocRepository.updateDebitDocumentBillRunStatus(debitDocId, Constants.DEBIT_DOC_STATUS.DISTRIBUTED);
            }
            return number;
        } catch (Exception e) {
            logger.error("Error Message:- " + e.getMessage());
        }
        return "";
    }

    /**
     * Create post paid invoice debit document.
     * @param customerBillingMessage the customer billing message
     * @param customers the customers
     * @return the debit document
     */
    public DebitDocument createPostPaidInvoice(CustomerBillingMessage customerBillingMessage, Customers customers) {
        logger.info("Initiating createPostPaidInvoice process");
        Map<String, Object> data = customerBillingMessage.getData();
        Integer RESP_CODE = APIConstants.FAIL;
        String nextBillDate = null;
        LocalDate billDate = null;
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            MDC.put("type", "Fetch");
            MDC.put("traceId", customerBillingMessage.getTraceContext().traceIdString());
            MDC.put("spanId", customerBillingMessage.getTraceContext().spanIdString());
            Integer staffId = null;
            LocalDate strBillDate = LocalDate.now();
            StaffUser staffUser = null;
            if (data.get("currentUserLoggedInId") != null) {
                staffId = (Integer) data.get("currentUserLoggedInId");
                staffUser = staffUserRepository.findById(staffId).get();
            }
            MDC.put("userName", staffUser != null ? staffUser.getUsername() : "null");

            if (CollectionUtils.isEmpty(data)) {
                logger.error("customer billing message data is empty");
            }
            if (!data.containsKey(CustomerBillingMessage.CUST_ID)) {
                logger.error("customer billing message custId is empty");
            }

            if (customerBillingMessage.getBilldate() != null) {
                billDate = customerBillingMessage.getBilldate();
            }
            Integer custId = (Integer) data.get(CustomerBillingMessage.CUST_ID);
            String createdByName = (String) data.get(CustomerBillingMessage.CREATED_BY_NAME);
            if (customers == null && custId != null)
                customers = customersRepository.findById(custId).orElse(null);

            if (customers == null) {
                logger.error("Given customer not available for id: " + custId);
            }

            if (customerBillingMessage.getIsEarlyBillDate() != null && customerBillingMessage.getIsEarlyBillDate()) {
                if (customers != null && customers.getEarlyBilldate() != null && (!customers.getEarlyBilldate().equals(LocalDate.now()) && customers.getEarlyBilldate().isAfter(LocalDate.now()))) {
                    return null;
                }
            }

            logger.info("Initiating createPostPaidInvoice process for customer: " + customers.getUsername());
            if (data.get("strDate") != null) {
                strBillDate = new DateTimeUtil().convertDateToDifferenFormat(inputFormatter, outputFormatter, (String) data.get("strDate"));
            }
            Integer renewalId;
            if (data.containsKey(CustomerBillingMessage.RENEWAL_ID)) {
                renewalId = (Integer) data.get(CustomerBillingMessage.RENEWAL_ID);
                logger.debug("Renewal Id for this Invoice of customer: " + customers.getUsername() + " is: " + renewalId);
            } else {
                renewalId = null;
            }
            List<CustPlanMappping> custPlanMapppings = customers.getPlanMappingList();


            if (customers.getStatus().equalsIgnoreCase("NewActivation")) {
                List<CustPlanMappping> updatedcustPlanMapppings = custPlanMapppings.stream()
                        .peek(mapping -> mapping.setIsInvoiceCreated(false))
                        .collect(Collectors.toList());
                custPlanMapppings = custPlanMappingRepository.saveAll(updatedcustPlanMapppings);
            }

            if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                if (!customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                    custPlanMapppings.removeIf(CustPlanMappping::getIsInvoiceCreated);
                }
                /*this condition is for change plan in post paid, to create invoice for post paid charge according to pror rate who's Status are STOP*/
                if (billDate == null && customerBillingMessage.getBilldateToday() == null) {
                    custPlanMapppings.removeIf(custPlanMappping -> !custPlanMappping.getCustPlanStatus().equalsIgnoreCase(Constants.CUSTOMER_STATUS_ACTIVE));
                }
                if (renewalId != null && customerBillingMessage.getBilldateToday() == null) {
                    custPlanMapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getRenewalId() != null && custPlanMappping.getRenewalId().equals(renewalId)).collect(Collectors.toList());
                }
                if (customerBillingMessage.getOldCprIdsForChangePLan() != null) {
                    custPlanMapppings = custPlanMapppings.stream().filter(x -> customerBillingMessage.getOldCprIdsForChangePLan().contains(x.getId())).collect(Collectors.toList());
                }
            }
            if (customers.getIstrialplan()) {
                custPlanMapppings.removeIf(x -> x.getIstrialplan());
            }
            if (CollectionUtils.isEmpty(custPlanMapppings)) {
                logger.error("Customer with username : " + customers.getUsername() + "does not have any plan mapping!");
                return null;
            }
            //Direct charge during customer creation
            logger.info("Fetching overChargeList and  directChargeList is exist for  Customer: " + customers.getUsername());
            List<CustChargeDetails> overChargeList = customers.getOverChargeList();
            List<CustChargeDetails> directChargeList = customers.getIndiChargeList();
            List<CustChargeDetails> customerDircharges = new ArrayList<>();
            if (!CollectionUtils.isEmpty(overChargeList)) {
                customerDircharges.addAll(overChargeList);
            }
            if (!CollectionUtils.isEmpty(directChargeList)) {
                customerDircharges.addAll(directChargeList);
                customerDircharges.removeIf(CustChargeDetails::getIsUsed);
            }
            if (customerBillingMessage.getNewServiceId() != null) {
                custPlanMapppings = custPlanMapppings.stream().filter(x -> x.getCustServiceMappingId().equals(customerBillingMessage.getNewServiceId())).collect(Collectors.toList());
            }
            List<Integer> planIds = custPlanMapppings.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
            List<Integer> custPackids = custPlanMapppings.stream().map(CustPlanMappping::getId).collect(Collectors.toList());
            List<CustPlanMappping> futurePlans = custPlanMapppings.stream()
                    .filter(plan -> plan.getStartDate().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
            if (!futurePlans.isEmpty()) {
                custPackids = futurePlans.stream()
                        .map(CustPlanMappping::getId)
                        .collect(Collectors.toList());
            }
            Set<Integer> custPackidsSet = new HashSet<>(custPackids);
            List<Integer> csmIds = custPlanMapppings.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
            List<Long> custServiceIds = new ArrayList<>();
            List<CustomerServiceMapping> customerServiceMappings = customers.getCustomerServiceMappingList();
            List<CustomerChargeHistory> customerChargeHistories = customers.getCustomerChargeHistories();//customerChargeHistoryRepository.findAllByCustomerIdAndChargeIdInAndCustPlanMapppingIdIn(custId, chargeIds, custPlanIds);
            if (!futurePlans.isEmpty()) {
                customerChargeHistories =  customers.getCustomerChargeHistories().stream()
                        .filter(history -> custPackidsSet.contains(history.getCustPlanMapppingId()))
                        .collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(customerChargeHistories)) {
                logger.error("Customer not have any charge mapping!");
                return null;
            }
            if (customerBillingMessage.getNewServiceId() != null) {
                customerChargeHistories = customerChargeHistories.stream().filter(x -> custPackidsSet.contains(x.getCustPlanMapppingId())).collect(Collectors.toList());
            }
            logger.info("Fetching customerChargeHistories for  Customer: " + customers.getUsername());
            String postpaidAdvance = null;
            //addOn plan Cancel Nand regenerate isFirstChargeApply will be true
            if (customerBillingMessage.getType() == null || !customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CANCEL_REGENERATE)) {
                customerChargeHistories = customerChargeHistories.stream().filter(x -> !(x.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_ONE_TIME) && x.getIsFirstChargeApply().equals(true))).collect(Collectors.toList());
            }
            if (customers.getCusttype().equalsIgnoreCase("Postpaid")) {
                postpaidAdvance = (String) data.get(CustomerBillingMessage.POSTPAIDADVANCE);
                /*condition for post paid advance charge only during customer creation to ignore Recurring postPaid charge */
                if (postpaidAdvance != null && postpaidAdvance.equalsIgnoreCase("Advance") && customerBillingMessage != null && customerBillingMessage.getType() != null && !customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CANCEL_REGENERATE)) {
                    customerChargeHistories = customerChargeHistories.stream().filter(x -> x.getLastBillDate() != null).collect(Collectors.toList());
                }
                /*condition for post paid  during postpaid invoice creation scheduler considers both advance and Recurring postPaid charge  */
                if (postpaidAdvance != null && postpaidAdvance.equalsIgnoreCase("Both")) {
                    if (customerBillingMessage.getIsEarlyBillDate()) {
                        strBillDate = customers.getEarlyBillDay() > 0 ? customers.getNextBillDate() :
                                customers.getEarlyBillDays() > 0 ? strBillDate.plusDays(customers.getEarlyBillDays()) : strBillDate;
                    }
                    LocalDate finalStrBillDate = strBillDate;
                    customerChargeHistories = customerChargeHistories.stream().filter(x -> x.getNextBillDate().isEqual(finalStrBillDate)).collect(Collectors.toList());
                }
                /*condition for Cancel and Reg to consider only those charge history who's debit doc id matches */
                if (data.containsKey("oldDebitDocId") && customerBillingMessage.getType() != null && customerBillingMessage.getType().equalsIgnoreCase(CommonConstants.INVOICE_TYPE.CANCEL_REGENERATE)) {
                    Long debitdocId = Long.valueOf(data.get("oldDebitDocId").toString());
                    List<Integer> chcId = customerChargeHistoryRepository.findChargeIds(debitdocId);
                    customerChargeHistories = customerChargeHistories.stream().filter(x -> chcId.contains(x.getId()) && x.getLastBillDate() != null && x.getLastBillDate().isEqual(LocalDate.now())).collect(Collectors.toList());
                }
                if (customerBillingMessage.getCprIds() != null && customerBillingMessage.getCprIds().size() > 0) {
                    customerChargeHistories = customerChargeHistoryRepository.findAllByCustomerIdAndChargeType(customers.getId(), CommonConstants.CHARGE_TYPE_RECURRING);
                    customerChargeHistories = customerChargeHistories.stream().filter(x -> customerBillingMessage.getCprIds().contains(x.getCustPlanMapppingId())).collect(Collectors.toList());
                    postpaidAdvance = Constants.INVOICE_TYPE.CHANGE_PLAN;
                    customerBillingMessage.setType(CommonConstants.INVOICE_TYPE.CHANGE_PLAN);
                    custPlanMapppings = custPlanMappingRepository.findAllByIdIn(customerBillingMessage.getCprIds());
                } else if (customerBillingMessage.getType() != null && (customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN) || customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.RENEW) || customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.ADDON))) {
                    postpaidAdvance = customerBillingMessage.getType();
                    customerChargeHistories = customerChargeHistories.stream().filter(custChargeHis -> custPackidsSet.contains(custChargeHis.getCustPlanMapppingId())).collect(Collectors.toList());
                }

            }
//            ****child invoice details for change plan having invoice type GROUP****
            if (customerBillingMessage.getChildIds() != null && customerBillingMessage.getChildIds().size() > 0) {
                logger.error("Initiating Invoice Details process for Child customers with id's:  " + customerBillingMessage.getChildIds());
                ChildInvoiceDetails childInvoiceDetails = prepaidInvoiceService.getChildInvoiceDetails(customerBillingMessage.getChildIds(), renewalId, strBillDate);
                customerDircharges.addAll(custChargeDetailsRepository.findAllByCustomerInAndIsUsed(customersRepository.findAllById(customerBillingMessage.getChildIds()), false));
                custPlanMapppings.addAll(childInvoiceDetails.getCustPlanMapppings());
                customerChargeHistories.addAll(childInvoiceDetails.getCustomerChargeHistories());
                customerServiceMappings.addAll(childInvoiceDetails.getCustomerServiceMappings());
            }


            List<Double> orignalChargeAmount = customerChargeHistories.stream().map(x -> x.getChargeAmount()).collect(Collectors.toList());
            List<Integer> chargeIds = customerChargeHistories.stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Long> custInvIds = new ArrayList<>();
            if (data.containsKey("inventorycaftocustomer") && data.get("inventorycaftocustomer").equals(true)) {
                custInvIds = customerInventoryMappingRepo.findAllByCustomerId(customers.getId().longValue());
            }
            logger.info("Initiating prepareInvoiceDetail process for  customer :  " + customers.getUsername());
            InvoiceDetails invoiceDetails = postPaidInvoiceUtil.prepareInvoiceDetail(customers, custPlanMapppings, customerChargeHistories, customerDircharges, customerServiceMappings, custInvIds, postpaidAdvance, customerBillingMessage.getIsEarlyBillDate(), customerBillingMessage.isTrailPlanFromToday(), customerBillingMessage.isTrailPlanFromTrailDay(), customerBillingMessage.isCafCustomerApprove());
            DebitDocument debitDocument = invoiceDetails.getDebitDocument();
            if (debitDocument != null) {
                // skip
            } else {
                logger.error("Invoice is not created as debitDocument is null");
                return null;
            }

            if (debitDocument != null && debitDocument.getTotalamount() == null)
                return null;

            if (debitDocument.getTotalamount() <= 0) {
                if (customerBillingMessage.getData().containsKey("mvnoId")) {
                    Integer mvnoId = (Integer) customerBillingMessage.getData().get("mvnoId");
                    ClientService allowZeroInvoice = clientServiceRepository.getByNameAndMvnoId(Constants.ALLOWZEROCHARGEINVOICE, mvnoId);
                    if (allowZeroInvoice.getValue().equalsIgnoreCase("No")) {
                        logger.error("Invoice can not be generated due to 0 ammount");
                        return null;
                    }
                }
            }
            if(custPlanMapppings != null){
               Boolean isOrgCustomer  = prepaidInvoiceService.hasInvoiceToOrg(custPlanMapppings);
               if(isOrgCustomer){
                   logger.error("billTo organization customer is found.No Invoice will be generated.");
                   return null;
               }

            }
            debitDocument.setDebitDocDetailsList(null);
            Double previousbalance = customerLedgerDtlsRepository.findClsoingAmountById(custId);
            debitDocument.setPreviousbalance(previousbalance);

            if (customerBillingMessage.getData().get(CustomerBillingMessage.BILL_RUN_ID) != null) {
                messageReceiverWithThread.updateBillRunData(debitDocument, Integer.valueOf(customerBillingMessage.getData().get(CustomerBillingMessage.BILL_RUN_ID).toString()));
                debitDocument.setBillrunid(Integer.valueOf(customerBillingMessage.getData().get(CustomerBillingMessage.BILL_RUN_ID).toString()));
            }

            debitDocument = debitDocRepository.save(debitDocument);
            logger.warn("Invoice is created id: " + debitDocument.getId() + " for customer: " + customers.getUsername());
            List<DebitDocDetails> debitDocDetailsList = invoiceDetails.getDebitDocDetails();
            debitDocument.setDebitDocDetailsList(debitDocDetailsList);
            DebitDocument finalDebitDocument = debitDocument;
            debitDocDetailsList = debitDocDetailsList.stream().peek(debitDocDetails -> debitDocDetails.setDebitdocumentid(finalDebitDocument.getId())).collect(Collectors.toList());
            debitDocDetailRepository.saveAll(debitDocDetailsList);
            prepaidInvoiceService.addCustomerLedger(debitDocument, customers, createdByName, customerBillingMessage.getType(), customerBillingMessage.getChildIds(),customerBillingMessage.getPayableChildId());

            if (customerBillingMessage.getBilldate() != null) {
                billDate = customerBillingMessage.getBilldate();
            }
            prepaidInvoiceService.createCNforChangePlanAndCancelAndRegenrate(customerBillingMessage, debitDocument, customerServiceMappings, null, billDate);

            if (customerBillingMessage.getData().containsKey("paymentSource")) {
                String paymentSource = String.valueOf(customerBillingMessage.getData().get("paymentSource"));
                if (paymentSource.length() > 0) {
                    List<Long> buIds = null;
                    Integer mvnoId = null;
                    Integer partnerId = null;
                    Boolean isLco = false;
                    String getcreatedByName = "";
                    Integer getCreatedById = null;


                    if (customerBillingMessage.getData().containsKey("buIds")) {
                        buIds = (List<Long>) customerBillingMessage.getData().get("buIds");
                    }
                    if (customerBillingMessage.getData().containsKey("mvnoId")) {
                        mvnoId = (Integer) customerBillingMessage.getData().get("mvnoId");
                    }
                    if (customerBillingMessage.getData().containsKey("partnerId")) {
                        partnerId = (Integer) customerBillingMessage.getData().get("partnerId");
                    }
                    if (customerBillingMessage.getData().containsKey("isLco")) {
                        isLco = (Boolean) customerBillingMessage.getData().get("isLco");
                    }
                    if (customerBillingMessage.getData().containsKey("createById")) {
                        getCreatedById = (Integer) customerBillingMessage.getData().get("createById");
                    }
                    if (customerBillingMessage.getData().containsKey("createByName")) {
                        getcreatedByName = (String) customerBillingMessage.getData().get("createByName");
                    }
                    String transacationNumber = "";
                    if (customerBillingMessage.getData().containsKey("additionalInformationDTO")) {
                        AdditionalInformationDTO additionalInformationDTO = (AdditionalInformationDTO) customerBillingMessage.getData().get("additionalInformationDTO");
                        transacationNumber = additionalInformationDTO.getTransactionNumber();
                    }
                    RecordPaymentPojo recordPaymentPojo = creditDocService.createPaymentForOnline(debitDocument, paymentSource, transacationNumber);
                    creditDocService.save(recordPaymentPojo, false, false, false, mvnoId, partnerId, buIds, isLco, getCreatedById, getcreatedByName);
                    List<CreditDocument> getAllCreditDoc = creditDocRepository.findAllByCustomer(debitDocument.getCustomer());
                    if(!getAllCreditDoc.isEmpty()) {
                        creditDocService.addPaymentInCustomerLedger(debitDocument.getCustomer() , getAllCreditDoc.get(getAllCreditDoc.size()-1)); /**for ledger correction**/
                        CreditDebitMappingPojo creditDebitDocMappingPojo = new CreditDebitMappingPojo();
                        creditDebitDocMappingPojo.setInvoiceId(debitDocument.getId());
                        CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
                        creditDebitDataPojo.setAmount(debitDocument.getTotalamount());
                        creditDebitDataPojo.setId(getAllCreditDoc.get(getAllCreditDoc.size()-1).getId());
                        List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
                        creditDebitDataPojoList.add(creditDebitDataPojo);
                        creditDebitDocMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
                        creditDocService.adjustManualPaymentToInvoiceWithWallet(creditDebitDocMappingPojo);
                        debitDocument = debitDocRepository.findById(debitDocument.getId()).get();
                        debitDocument.setDebitDocDetailsList(debitDocDetailsList);
                        List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findByCreditDocId(getAllCreditDoc.get(getAllCreditDoc.size()-1).getId());
                        creditDocService.deleteDuplicateEntry(creditDebitDocMappingList);
                    }

                }
            }
            //handle if caf have already adjusted invoice then
            // if caf has any adjusted credit document that will also be adjusted in normal customer
            List<String> statusList = new ArrayList<>();
            statusList.add(Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
            statusList.add(Constants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            List<CreditDocument> getAllAdjustedCreditDoc = creditDocRepository.findAllByCustomerAndStatusInAndTypeNot(debitDocument.getCustomer(), statusList,Constants.CREDIT_DOC_TYPE_CREDITNOTE);
            List<CreditDebitDocMapping> getAdjustedEntries = new ArrayList<>();
            if(!getAllAdjustedCreditDoc.isEmpty()) {
                List<Integer> creditdocIds = getAllAdjustedCreditDoc.stream().map(creditDocument -> creditDocument.getId()).collect(Collectors.toList());
                getAdjustedEntries = creditDebtMappingRepository.findByCreditDocIdAndDebtDocIdNotNull(creditdocIds);
            }
            if (!getAllAdjustedCreditDoc.isEmpty() && getAdjustedEntries.isEmpty() && renewalId == null) {
                logger.info("Adjusting Payment for   customer :  " + customers.getUsername() + " if payment done during  CAF invoice payment");
                debitDocument = prepaidInvoiceService.paymentAdjustmentForForCafCust(finalDebitDocument, getAllAdjustedCreditDoc);
                List<CreditDebitDocMapping> creditDebitDocMapping = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
                CreditDebitDocMessage creditDebitDocMessage = new CreditDebitDocMessage();
                creditDebitDocMessage.setCreditDebitDocMappingList(creditDebitDocMapping);
//                messageSender.send(creditDebitDocMessage,RabbitMqConstants.QUEUE_CREDIT_DEBIT_DOC_TO_CMS);
                logger.info("Queue sent to CMS reagring CreditDoc for   customer :  " + customers.getUsername() + " if payment done during  CAF creation or customer portal");
                kafkaMessageSender.send(new KafkaMessageData(creditDebitDocMessage, CreditDebitDocMessage.class.getSimpleName()));
            }


            //Will be handle if payment done from customer aquasition portal
            List<CreditDocument> getAllCreditDoc = creditDocRepository.findAllByCustomerAndStatus(debitDocument.getCustomer(), CommonConstants.PAYMENT_CONDITION.ONLINE_PAYMENT_APPROVED);
            if (!getAllCreditDoc.isEmpty()) {
                debitDocument = prepaidInvoiceService.paymentAdjustmentForCaptiPortalCust(finalDebitDocument, getAllCreditDoc);
                if (CollectionUtils.isEmpty(debitDocument.getDebitDocDetailsList())) {
                    debitDocument.setDebitDocDetailsList(debitDocDetailsList);
                }
                List<CreditDebitDocMapping> creditDebitDocMapping = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
                CreditDebitDocMessage creditDebitDocMessage = new CreditDebitDocMessage();
                creditDebitDocMessage.setCreditDebitDocMappingList(creditDebitDocMapping);
                kafkaMessageSender.send(new KafkaMessageData(creditDebitDocMessage, creditDebitDocMessage.getClass().getSimpleName()));
            }
            List<CreditDocument> getAllCreditDocPending = creditDocRepository.findAllByCustomerAndStatus(debitDocument.getCustomer(), CommonConstants.PAYMENT_CONDITION.ONLINE_PAYMENT_PENDING);
            if (!getAllCreditDocPending.isEmpty()) {
                for (CreditDocument doc : getAllCreditDocPending) {
                    doc.setStatus("Payment Failed");
                    doc.setInvoiceId(finalDebitDocument.getId());
                    creditDocRepository.save(doc);
                }
            }

            if (CollectionUtils.isEmpty(debitDocument.getDebitDocDetailsList()) ||  debitDocument.getDebitDocDetailsList() == null) {
                debitDocument.setDebitDocDetailsList(invoiceDetails.getDebitDocDetails());
            }

            String customerTypeName = prepaidInvoiceService.getCustomerType(customers.getCusttype());
            boolean advanceChargehistory = false;
            boolean recurrChargeHistory = false;
            advanceChargehistory = customerChargeHistories.stream()
                    .anyMatch(x -> x.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_ADVANCE));
            recurrChargeHistory = customerChargeHistories.stream()
                    .anyMatch(x -> x.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_RECURRING));
            //TODO: get value from common service for organization customer.
            if (!(customers.getUsername().equalsIgnoreCase(customerTypeName))) {
                if (customers.getCusttype().equals(Constants.CUSTOMER_TYPE.PREPAID))
                    dbrService.addDbrForPrepaidCustomer(customerChargeHistories, debitDocument, debitDocument.getCustomer(), custPlanMapppings, customerServiceMappings, customerDircharges, customerBillingMessage.isTrailPlanFromTrailDay());

                if (customers.getCusttype().equals(Constants.CUSTOMER_TYPE.POSTPAID)) {
                    if (advanceChargehistory) {
                        dbrService.addDbrForPrepaidCustomer(customerChargeHistories, debitDocument, debitDocument.getCustomer(), custPlanMapppings, customerServiceMappings, customerDircharges, customerBillingMessage.isTrailPlanFromTrailDay());
                    } else if (recurrChargeHistory) {
                        dbrService.addDbrForPostpaidCustomer(customerChargeHistories, debitDocument, debitDocument.getCustomer(), custPlanMapppings, customerServiceMappings, customerDircharges);
                    } else {
                        dbrService.addDbrForPrepaidCustomer(customerChargeHistories, debitDocument, debitDocument.getCustomer(), custPlanMapppings, customerServiceMappings, customerDircharges, customerBillingMessage.isTrailPlanFromTrailDay());
                    }

                }
                partnerCommissionService.addPartnerCommission(customerChargeHistories, custPlanMapppings, debitDocument, debitDocument.getCustomer(), staffId, staffUser);
            } else
                dbrService.addDbrForOrgCustomerPrepaid(debitDocument.getCustomer(), customerDircharges, debitDocument, custPlanMapppings, customerChargeHistories, customerServiceMappings);

            //after renewing we need old expired cpr to excluded
            List<Integer> cprids = customerChargeHistories.stream().map(x -> x.getCustPlanMapppingId()).collect(Collectors.toList());
            List<CustPlanMappping> custPlanMapppingList = customers.getPlanMappingList().stream().filter(x -> cprids.contains(x.getId())).collect(Collectors.toList());


            updateAfterInvoiceCreatedData(customers, custPlanMapppingList, Long.valueOf(finalDebitDocument.getId()), false, customerDircharges, postpaidAdvance, billDate, customerBillingMessage.getType(), customerBillingMessage.isTrailPlanFromToday(), customerBillingMessage.isTrailPlanFromTrailDay());

            //DebitDoc tax relation
            List<DebitDocumentTAXRel> debitDocumentTAXRels = new ArrayList<>();
            List<CustomerChargeHistory> finalCustomerChargeHistories = customerChargeHistories;
            if (debitDocDetailsList != null && !debitDocDetailsList.isEmpty()) {
                debitDocDetailsList.forEach(chargeHistory -> {
                    DebitDocumentTAXRel debitDocumentTAXRel = taxService.setTaxAmountFromCharge1(finalDebitDocument, chargeHistory.getChargeid(), chargeHistory.getDiscountPercentage(), chargeHistory.getDebitdocdetailid().longValue(), chargeHistory.getPlanId());
                    debitDocumentTAXRels.add(debitDocumentTAXRel);
                });
            }

            if (!CollectionUtils.isEmpty(debitDocumentTAXRels)) {
                debitDocument.setDebitDocumentTAXRels(debitDocumentTAXRels);
            }
            boolean isOrgCust = false;
            if (customers.getId() == 1) {
                isOrgCust = true;
            }
            String isCaf = "false";
            if (customers.getCafno() != null) {
                isCaf = "true";
            }
            Customers refCustomer = custPlanMapppings.get(0).getCustomer();
            List<Map.Entry<Integer, Long>> CustPackAndDebitDocIdPair = new ArrayList<>();

            // Adding pairs to the list
            for (CustPlanMappping custPlanMappping : customers.getPlanMappingList()) {
                CustPackAndDebitDocIdPair.add(new AbstractMap.SimpleEntry<>(custPlanMappping.getId(), custPlanMappping.getDebitdocid()));
            }

            customerChargeHistoryRepository.saveAll(customerChargeHistories);

            if (customerBillingMessage.getType() != null && customerBillingMessage.getType().equalsIgnoreCase(CommonConstants.INVOICE_TYPE.RENEW)) {
                customers.setNextBillDate(customerChargeHistories.get(0).getNextBillDate());
                customersRepository.save(customers);
            }


            List<Map.Entry<Integer, String>> childIdNextBillDatePair = new ArrayList<>();
            List<Integer> msgChildIds = new ArrayList<>();
            List<Customers> childCustomers = new ArrayList<>();
            if (customers.getNextBillDate() != null) {
                nextBillDate = String.valueOf(customers.getNextBillDate());
                if (customerBillingMessage.getChildIds() != null && customerBillingMessage.getChildIds().size() > 0) {

                    for (Integer childCustId : customerBillingMessage.getChildIds()) {
                        LocalDateTime childNextBilldate = customerChargeHistoryRepository.findNearestNextBillDate(childCustId);
                        childIdNextBillDatePair.add(new AbstractMap.SimpleEntry<>(childCustId, childNextBilldate.toString()));

                        Customers childCustomer = customersRepository.findById(childCustId).get();
                        childCustomer.setNextBillDate(childNextBilldate.toLocalDate());
                        childCustomers.add(childCustomer);
                    }
                    customersRepository.saveAll(childCustomers);
                }
            }

            List<Map.Entry<Integer, String>> CustPackAndEndDatePair = new ArrayList<>();

            if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                for (CustPlanMappping custPlanMappping : customers.getPlanMappingList()) {
                    CustPackAndEndDatePair.add(new AbstractMap.SimpleEntry<>(custPlanMappping.getId(), custPlanMappping.getEndDate().toString()));
                }

                if (CustPackAndEndDatePair != null && !CustPackAndEndDatePair.isEmpty()) {
                    UpdateCprMessage message = new UpdateCprMessage(CustPackAndEndDatePair);
                    //messageSender.send(message,RabbitMqConstants.QUEUE_CPR_UPDATE_FROM_REVENUE_CMS);
                    kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
                }
            }

            PrepaidInvoiceCharges prepaidInvoiceCharges = new PrepaidInvoiceCharges(customers.getId(), customers.getUsername(), customers.getCustomerType(), debitDocument.getTotalamount(), debitDocument.getId().longValue(), customers.getUsername(), isOrgCust, debitDocument.getTotalamount(), refCustomer.getCreatedById(), null, custServiceIds, "null", "false", isCaf, 0L, debitDocument, customers.getWalletbalance(), debitDocument.getPaymentStatus(), debitDocument.getBillrunid(), debitDocument.getCreatedByName(), CustPackAndDebitDocIdPair, debitDocument.getAdjustedAmount(), debitDocument.getBillrunstatus(), false, debitDocument.getIsDirectChargeInvoice(), null, nextBillDate, CustPackAndEndDatePair, childIdNextBillDatePair);
            //messageSender.send(prepaidInvoiceCharges, RabbitMqConstants.QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION);
            kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges, PrepaidInvoiceCharges.class.getSimpleName()));
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + " Customer management Service, " + "Successfully Invoice Created for Customer id :" + custId + LogConstants.REQUEST_BY + (staffUser != null ? staffUser.getUsername() : "null") + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return debitDocument;
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            ex.printStackTrace();
            logger.error(LogConstants.REQUEST_FROM + " Customer management Service, " + "Error During Invoice Generation for Customer id : " + (Integer) data.get(CustomerBillingMessage.CUST_ID) + LogConstants.REQUEST_BY + (String) data.get(CustomerBillingMessage.CREATED_BY_NAME) + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;
    }

    /**
     * Update after invoice created data.
     * @param customers the customers
     * @param custPlanMapppings the cust plan mapppings
     * @param debitdocid the debitdocid
     * @param isCaf the is caf
     * @param custChargeDetails the cust charge details
     * @param postpaidAdvance the postpaid advance
     * @param billdate the billdate
     * @param type the type
     */
    public void updateAfterInvoiceCreatedData(Customers customers, List<CustPlanMappping> custPlanMapppings, Long debitdocid, boolean isCaf, List<CustChargeDetails> custChargeDetails, String postpaidAdvance, LocalDate billdate, String type, boolean trailPlanFromToday, boolean trailPlanFromTrailDay) {
        logger.warn("In updateAfterInvoiceCreatedData customer: " + customers.getUsername() + " billdate: " + billdate);
        if (!CollectionUtils.isEmpty(custPlanMapppings)) {
            List<Integer> cpridsActive = new ArrayList<>();
            List<Integer> planIds = new ArrayList<>();
            try {
                if (!customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                    custPlanMapppings = custPlanMapppings.stream().peek(custPlanMappping -> {
                        if (!customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID))
                            custPlanMappping.setIsInvoiceCreated(true);
                        if (isCaf)
                            custPlanMappping.setTraildebitdocid(debitdocid);
                        else
                            custPlanMappping.setDebitdocid(debitdocid);
                    }).collect(Collectors.toList());
                    custPlanMappingRepository.saveAll(custPlanMapppings);
                    customersRepository.save(customers);
                } else if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                    custPlanMapppings = custPlanMapppings.stream().sorted(Comparator.comparing(CustPlanMappping::getEndDate).reversed()).collect(Collectors.toList());
                    List<Integer> custPlanMapppingIds = custPlanMappingRepository.findCustpackIdsBycustIdsAndStatus(customers.getId(), "Active");
                    custPlanMapppings = custPlanMapppings.stream().filter(i -> custPlanMapppingIds.contains(i.getId())).collect(Collectors.toList());
                    customers.setIsUsingByThread(false);
                    LocalDate nextBillDate = customers.getNextBillDate();
                    LocalDate lastBillDate = nextBillDate;


                    planIds = custPlanMapppings.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
                    cpridsActive = custPlanMapppings.stream().filter(i -> i.getCustPlanStatus().equalsIgnoreCase("Active"))
                            .map(i -> i.getId()).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(custPlanMapppings)) {// when we changeplan postpaidrecurring we need to update next bill date based on active cprs
                        cpridsActive = custPlanMapppingIds;
                        planIds = custPlanMappingRepository.getAllPlansByCustCPRList(cpridsActive);
                    }
                    List<Integer> chargeIds = postpaidPlanChargeRepo.getChargeListByPlanIdList(planIds);

                    List<CustomerChargeHistory> customerChargeHistories = customerChargeHistoryRepository.findAllByCustomerIdAndChargeIdIn(customers.getId(), chargeIds);
                    List<Integer> finalCpridsActive = cpridsActive;
                    customerChargeHistories = customerChargeHistories.stream().filter(i -> finalCpridsActive.contains(i.getCustPlanMapppingId())).collect(Collectors.toList());
                    List<CustomerChargeHistory> minCycle = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(customerChargeHistories)) {
                        minCycle = customerChargeHistories.stream().filter(customerChargeHistory -> !customerChargeHistory.getChargeType().equalsIgnoreCase("NON_RECURRING")).sorted(Comparator.comparing(CustomerChargeHistory::getNextBillDate)).collect(Collectors.toList());
                        nextBillDate = minCycle.get(0).getNextBillDate();
                    }
                    if (postpaidAdvance != null && !postpaidAdvance.equalsIgnoreCase("Advance")) {
                        customers.setNextBillDate(nextBillDate);
                    }
                    LocalDate earlyBillDate = getEarlyBillDate(customers, nextBillDate, type, trailPlanFromToday, trailPlanFromTrailDay);
                    customers.setEarlyBilldate(earlyBillDate);
                    customers.setLastBillDate(lastBillDate);
                    //when we generate invoice of child and parent combined child cust id was getting updated to parent in customerservicemapping table
                    customers.getCustomerServiceMappingList().removeIf(i -> !i.getCustId().equals(customers.getId()));
                    customersRepository.save(customers);
                    List<CustomerChargeHistory> finalMinCycle = minCycle;
                    custPlanMapppings = custPlanMapppings.stream().peek(custPlanMappping -> {

                        /*this if for extending cpr date so that in never expires unless it's changePlan with bill date*/
                        if (!finalMinCycle.isEmpty()) {
                            custPlanMappping.setEndDate(finalMinCycle.get(finalMinCycle.size() - 1).getNextBillDate().atStartOfDay().plusHours(23));
                            custPlanMappping.setExpiryDate(finalMinCycle.get(finalMinCycle.size() - 1).getNextBillDate().atStartOfDay().plusHours(23));
                        }
                        if (isCaf)
                            custPlanMappping.setTraildebitdocid(debitdocid);
                        else
                            custPlanMappping.setDebitdocid(debitdocid);
                    }).collect(Collectors.toList());
                    custPlanMappingRepository.saveAll(custPlanMapppings);

                }
            } catch (Exception ex) {
                logger.error("Exception while update customer data after invoice created: " + ex.getMessage());
            }
        }
        if (!CollectionUtils.isEmpty(custChargeDetails)) {
            custChargeDetails = custChargeDetails.stream().peek(custChargeDet -> custChargeDet.setIsUsed(true)).collect(Collectors.toList());
            custChargeDetailsRepository.saveAll(custChargeDetails);
        }
    }

    private LocalDate getEarlyBillDate(Customers customers, LocalDate nextBillDate, String type, boolean trailPlanFromToday, boolean trailPlanFromTrailDay) {
        try {
            LocalDate earlyBillDate = nextBillDate;
            logger.info(String.format("In getEarlyBillDate type: %s, nextBillDate: %s, EarlyBillDays: %s, getEarlyBillDay: %s", type, nextBillDate,
                    customers.getEarlyBillDays(), customers.getEarlyBillDay()));
            if (customers.getEarlyBillDays() > 0) {
                earlyBillDate = nextBillDate.minusDays(customers.getEarlyBillDays());
            } else if (customers.getEarlyBillDay() > 0) {
                LocalDate inputDate = nextBillDate;

                int year = inputDate.getYear();
                int month = inputDate.getMonthValue();

                LocalDate currentMonthTwentyFifth = LocalDate.of(year, month, customers.getEarlyBillDay());

                LocalDate nearestPastTwentyFifth;
                if (inputDate.isBefore(currentMonthTwentyFifth) || inputDate.isEqual(currentMonthTwentyFifth)) {
                    nearestPastTwentyFifth = inputDate.minusMonths(1).withDayOfMonth(customers.getEarlyBillDay());
                } else {
                    nearestPastTwentyFifth = currentMonthTwentyFifth;
                }

                earlyBillDate = nearestPastTwentyFifth;
            }
            logger.info(String.format("In getEarlyBillDate after date calculation nextBillDate: %s, EarlyBillDate: %s, CurrentDate"
                    , customers.getNextBillDate(), customers.getEarlyBilldate(), LocalDate.now()));
            if ((type != null && !type.equalsIgnoreCase("Scheduler")) && (earlyBillDate.equals(LocalDate.now()) || earlyBillDate.isBefore(LocalDate.now()))) {
                earlyBillDate = LocalDate.now().plusDays(1);
            }

            if(trailPlanFromToday || trailPlanFromTrailDay){
                earlyBillDate = LocalDate.now().plusDays(1);
            }
            return earlyBillDate;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Online customer payment.
     * @param request the request
     */
    public void onlineCustomerPayment(OnlinePaymentDTO request) {
        try {
            Customers customers = customersRepository.findById(request.getCustomerid()).get();
            TrialDebitDocument debitDocument = new TrialDebitDocument();
            CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
            debitDocument.setTotalamount(request.getAmount());
            debitDocument.setId(request.getInvoiceId());
            customerBillingMessage.setReferenceNo(request.getReferenceno());
            prepaidInvoiceService.savePaymentOnline(debitDocument, customers, customerBillingMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate isp invoice detail payload isp invoice payload.
     * @param mvnoDebitDocumentId the mvno debit document id
     * @param customerId the customer id
     * @return the isp invoice payload
     */
    public IspInvoicePayload generateIspInvoiceDetailPayload(Integer mvnoDebitDocumentId, Integer customerId) {

        String clientId = mvnoRepository.findClientIdByMvnoCustId(customerId);
        Optional<DebitDocument> debitDocument = debitDocRepository.findById(mvnoDebitDocumentId);
        if (debitDocument.isPresent()) {
            IspInvoicePayload invoicePayload = new IspInvoicePayload();
            invoicePayload.setInvoiceId(debitDocument.get().getDocnumber());
            invoicePayload.setClientId(clientId);
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            invoicePayload.setInvoiceDate(debitDocument.get().getCreatedate() != null ? debitDocument.get().getCreatedate().toLocalDate().format(myFormatObj) : null);
            List<ServicePayload> servicePayloads = prepaidInvoiceService.getServicePayload(mvnoDebitDocumentId);
            invoicePayload.setServices(servicePayloads);
            return invoicePayload;
        }
        return null;
    }

    private void sendIspInvoicePayloadToIntegrationService(IspMainPayload ispPayloads) throws JsonProcessingException {
        if (ispPayloads.getInvoicePayloads() != null && !ispPayloads.getInvoicePayloads().isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(ispPayloads.getInvoicePayloads());
            jsonString = jsonString.replaceAll(",\"items\":null", "");
            jsonString = jsonString.replaceAll(",\"prorated\":null", "");
            ispPayloads.setJsonPayload(jsonString);
            logger.info("\n******************************ISP Payload Message******************************\n\n" + jsonString + "\n\n*******************************************************************************\n");
            kafkaMessageSender.send(new KafkaMessageData(ispPayloads, ispPayloads.getClass().getSimpleName(), "SEND"));
        }
    }

    /**
     * Send isp payload to integration.
     * @param invoicePayload the invoice payload
     * @param document the document
     * @throws JsonProcessingException the json processing exception
     */
    public void sendIspPayloadToIntegration(IspInvoicePayload invoicePayload, DebitDocument document) throws JsonProcessingException {
        List<IspInvoicePayload> ispPayloads = new ArrayList<>();
        ispPayloads.add(invoicePayload);
        IspMainPayload ispMainPayloads = new IspMainPayload();
        List<Integer> invoiceIds = new ArrayList<>();
        invoiceIds.add(document.getId());
        ispMainPayloads.setInvoicePayloads(ispPayloads);
        ispMainPayloads.setInvoiceIds(invoiceIds);
        sendIspInvoicePayloadToIntegrationService(ispMainPayloads);
    }


    /**
     * Prepaid customer auto renewal or addon.
     */
    @Transactional
    public void prepaidCustomerAutoRenewalOrAddon() {
        try {
            logger.info("******************** Prepaid Customer Auto Renewal Scheduler Process Start *****************************************");
            Long autoRenewNumberOfDaysBeforeExpiry;
            ClientService clientService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.AUTO_RENEW_BEFORE_EXPIRY_DAY, 1);
            if (clientService != null)
                autoRenewNumberOfDaysBeforeExpiry = Long.parseLong(clientService.getValue());
            else {
                autoRenewNumberOfDaysBeforeExpiry = -1L;
            }
            List<Integer> customerIds = customersRepository.findAllIdByCustomertypeIgnoreCaseAndStatus(Constants.CUSTOMER_TYPE.PREPAID);
            customerIds.stream().forEach(customerId -> {
                List<CustPlanMapppingDto> futurePlanList = autoRenewOrAddonPlanService.getFuturePlanListByCustomerId(customerId);
                if (futurePlanList == null || (futurePlanList != null && futurePlanList.isEmpty())) {
                    CustomerWalletPojo customerWallet = autoRenewOrAddonPlanService.getCurrentWalletAmountByCustomerId(Long.valueOf(customerId));
                    if (customerWallet.getWalletAmount() != null && customerWallet.getWalletAmount() > 0.0) {
                        List<CustPlanMapppingDto> activePlanList = autoRenewOrAddonPlanService.getActivePlanListByCustomerId(customerId);
                        if (activePlanList != null && !activePlanList.isEmpty()) {
                            CustPlanMapppingDto dto = activePlanList.get(activePlanList.size() - 1);
                            compareAndSendPayloadForAutoRenewalToCms(customerId, dto, customerWallet, autoRenewNumberOfDaysBeforeExpiry, true, true);
                        } else {
                            List<CustPlanMapppingDto> expirePlanList = autoRenewOrAddonPlanService.getExpirePlanListByCustomerId(customerId);
                            if (expirePlanList != null && !expirePlanList.isEmpty()) {
                                CustPlanMapppingDto dto = expirePlanList.get(expirePlanList.size() - 1);
                                compareAndSendPayloadForAutoRenewalToCms(customerId, dto, customerWallet, autoRenewNumberOfDaysBeforeExpiry, false, true);
                            }
                        }
                    }
                }
            });
            logger.info("******************** Prepaid Customer Auto Renewal Scheduler Process Finished *****************************************");
        } catch (Exception e) {
            logger.error("Error processing during Auto assign renew : ", e);
            e.printStackTrace();
        }
    }

    /**
     * Compare and send payload for auto renewal to cms.
     * @param customerId the customer id
     * @param dto the dto
     * @param customerWallet the customer wallet
     * @param days the days
     * @param isActivePlan the is active plan
     * @param isRequestFromScheduler the is request from scheduler
     */
    public void compareAndSendPayloadForAutoRenewalToCms(Integer customerId, CustPlanMapppingDto dto, CustomerWalletPojo customerWallet, Long days, Boolean isActivePlan, Boolean isRequestFromScheduler) {
        try {
            if (dto.getPlanId() != null) {
                if (isRequestFromScheduler) {
                    Double planOfferPriceWithTax = autoRenewOrAddonPlanService.getPlanPriceByPlanId(dto.getPlanId(), null);
                    long numberOfDaysRemainingBeforeExpire = Duration.between(LocalDate.now().atStartOfDay(), dto.getExpiryDate().toLocalDate().atStartOfDay()).toDays();
                    if (customerWallet.getWalletAmount() >= planOfferPriceWithTax && (((isActivePlan && days.longValue() >= numberOfDaysRemainingBeforeExpire) || !isActivePlan))) {
                        AutoRenewOrAddonPlanRequestDto renewOrAddonPlanRequestDto = new AutoRenewOrAddonPlanRequestDto();
                        renewOrAddonPlanRequestDto.setPurchaseType("Renew");
                        renewOrAddonPlanRequestDto.setCustomerId(customerId);
                        renewOrAddonPlanRequestDto.setRemarks("Auto Assignment for Renew for CustomerId:- " + customerId);
                        renewOrAddonPlanRequestDto.setAddonEndDate(null);
                        renewOrAddonPlanRequestDto.setAddonStartDate(null);
                        renewOrAddonPlanRequestDto.setPlanId(dto.getPlanId());
                        renewOrAddonPlanRequestDto.setCustomerServiceMappingId(dto.getCustServiceMappingId());
                        renewOrAddonPlanRequestDto.setIsAutoRefund(false);
                        renewOrAddonPlanRequestDto.setIsParent(true);
                        renewOrAddonPlanRequestDto.setPaymentOwnerId(1);
                        renewOrAddonPlanRequestDto.setDiscount(0.0);
                        renewOrAddonPlanRequestDto.setIsAutoPaymentRequired(true);
                        renewOrAddonPlanRequestDto.setCreditDocumentPaymentPojoList(autoRenewOrAddonPlanService.getCurrentWalletAmountByCustomerId(Long.valueOf(customerId)).getCreditDocumentPaymentPojos());
                        kafkaMessageSender.send(new KafkaMessageData(renewOrAddonPlanRequestDto, renewOrAddonPlanRequestDto.getClass().getSimpleName()));
                    }
                } else {
                    AutoRenewOrAddonPlanRequestDto renewOrAddonPlanRequestDto = new AutoRenewOrAddonPlanRequestDto();
                    renewOrAddonPlanRequestDto.setPurchaseType("Renew");
                    renewOrAddonPlanRequestDto.setCustomerId(customerId);
                    renewOrAddonPlanRequestDto.setRemarks("Auto Assignment for Renew for CustomerId:- " + customerId);
                    renewOrAddonPlanRequestDto.setAddonEndDate(null);
                    renewOrAddonPlanRequestDto.setAddonStartDate(null);
                    renewOrAddonPlanRequestDto.setPlanId(dto.getPlanId());
                    renewOrAddonPlanRequestDto.setCustomerServiceMappingId(dto.getCustServiceMappingId());
                    renewOrAddonPlanRequestDto.setIsAutoRefund(false);
                    renewOrAddonPlanRequestDto.setIsParent(true);
                    renewOrAddonPlanRequestDto.setPaymentOwnerId(1);
                    renewOrAddonPlanRequestDto.setDiscount(0.0);
                    renewOrAddonPlanRequestDto.setIsAutoPaymentRequired(true);
                    renewOrAddonPlanRequestDto.setCreditDocumentPaymentPojoList(autoRenewOrAddonPlanService.getCurrentWalletAmountByCustomerId(Long.valueOf(customerId)).getCreditDocumentPaymentPojos());
                    kafkaMessageSender.send(new KafkaMessageData(renewOrAddonPlanRequestDto, renewOrAddonPlanRequestDto.getClass().getSimpleName()));
                }
            }
        } catch (Exception e) {
            logger.error("Error processing during Auto assign renew : ", e);
        }
    }

    public void updateEarlyBillDates(Integer custId){
        String billingCycleQuery = "SELECT billing_cycle FROM tbltcustchargehistory " +
                "WHERE cust_id = " + custId + " ORDER BY history_id ASC LIMIT 1";
        List<?> resultList = entityManager.createNativeQuery(billingCycleQuery).getResultList();

        if (!resultList.isEmpty()) {
            int billingCycle = ((Number) resultList.get(0)).intValue();
            logger.info("**************** billing cycle found for custId: " + custId + " billingCycle : " + billingCycle);
            customersRepository.updateEarlyBillDate(billingCycle, custId);
        } else {
            logger.info("**************** No billing cycle found for custId: " + custId);
        }
    }

    public void updateChargeHistory(UpdateChargeHistoryMessage updateChargeHistoryMessage){
          transactionUtil.updateCustomerChargeHistory(updateChargeHistoryMessage.getCustCharhistoryIds() , updateChargeHistoryMessage.getNewAmount() ,  updateChargeHistoryMessage.getTaxAmount());
    }
}
