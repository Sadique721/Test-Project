package com.savbill.revenuemanagement.core.service.prepaid;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.KRA.KRAUtils;
import com.savbill.revenuemanagement.autoassign.AutoRenewOrAddonPlanRequestDto;
import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
import com.savbill.revenuemanagement.core.Mvno.service.MvnoService;
import com.savbill.revenuemanagement.core.auditLog.service.AuditLogService;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.*;
import com.savbill.revenuemanagement.core.constants.*;
import com.savbill.revenuemanagement.core.controller.invoice.postpaid.*;
import com.savbill.revenuemanagement.core.dto.ChangePlanDto.ChildInvoiceDetails;
import com.savbill.revenuemanagement.core.dto.ChangePlanDto.CustChargeDetailsRevenue;
import com.savbill.revenuemanagement.core.dto.ChangePlanDto.CustPlanMappingRevenue;
import com.savbill.revenuemanagement.core.dto.ChangePlanDto.CustomerChargeHistoryRevenue;
import com.savbill.revenuemanagement.core.dto.ChangePlanDto.CustomerServiceMappingRevenue;
import com.savbill.revenuemanagement.core.dto.common.GenericDataDTO;
import com.savbill.revenuemanagement.core.dto.common.PaginationRequestDTO;
import com.savbill.revenuemanagement.core.dto.customer.CustPlanMapppingDto;
import com.savbill.revenuemanagement.core.dto.customer.Subscriber;
import com.savbill.revenuemanagement.core.dto.invoice.*;
import com.savbill.revenuemanagement.core.dto.invoice.AdditionalInformationDTO;
import com.savbill.revenuemanagement.core.dto.invoice.CreditDebitDataPojo;
import com.savbill.revenuemanagement.core.dto.invoice.CreditDebitMappingPojo;
import com.savbill.revenuemanagement.core.dto.invoice.RecordPaymentPojo;
import com.savbill.revenuemanagement.core.dto.invoice.xml.PlanInformation;
import com.savbill.revenuemanagement.core.entity.Billrun.BillRun;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.customers.*;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocDetails;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocumentTAXRel;
import com.savbill.revenuemanagement.core.entity.debitdoc.InvoiceDetails;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocument;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocumentDetail;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialDebitDocumentTAXRel;
import com.savbill.revenuemanagement.core.entity.debitdoc.TrialInvoiceDetails;
import com.savbill.revenuemanagement.core.entity.inventory.CustomerInventoryMapping;
import com.savbill.revenuemanagement.core.entity.invoice.*;
import com.savbill.revenuemanagement.core.entity.invoice.*;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDebitDocMapping;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedger;
import com.savbill.revenuemanagement.core.entity.ladger.CustomerLedgerDtls;
import com.savbill.revenuemanagement.core.entity.partner.Partner;
import com.savbill.revenuemanagement.core.entity.partner.PartnerDebitDocument;
import com.savbill.revenuemanagement.core.entity.partner.PartnerLedgerDetails;
import com.savbill.revenuemanagement.core.entity.partner.TempPartnerLedgerDetail;
import com.savbill.revenuemanagement.core.entity.staff.StaffUser;
import com.savbill.revenuemanagement.core.entity.staff.StaffUserServiceAreaMappingRepository;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.mapper.invoice.DebitDocumentMapper;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.customer.*;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocDetailRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocumentTAXRelRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocumentDetailRepository;
import com.savbill.revenuemanagement.core.repository.debit.TrialDebitDocumentTAXRelRepository;
import com.savbill.revenuemanagement.core.repository.inventory.CustomerInventoryMappingRepo;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDebtMappingRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.repository.ledger.CustomerLedgerDtlsRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerLedgerDetailsRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerRepository;
import com.savbill.revenuemanagement.core.repository.partner.PostpaidPlanChargeRepo;
import com.savbill.revenuemanagement.core.repository.partner.TempPartnerLedgerDetailsRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.security.dto.LoggedInUser;
import com.savbill.revenuemanagement.core.security.jwt.JwtUtil;
import com.savbill.revenuemanagement.core.service.ClientServ.domain.ClientService;
import com.savbill.revenuemanagement.core.service.ClientServ.repository.ClientServiceRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.common.CustomerInventoryUtil;
import com.savbill.revenuemanagement.core.service.common.InvoiceUtil;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.postpaid.DebitDocDetailList;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceThread;
import com.savbill.revenuemanagement.core.util.DateTimeUtil;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.isp.Item;
import com.savbill.revenuemanagement.isp.ProratedPayload;
import com.savbill.revenuemanagement.isp.ServicePayload;
import com.savbill.revenuemanagement.kafka.KafkaConstant;
import com.savbill.revenuemanagement.kafka.KafkaMessageData;
import com.savbill.revenuemanagement.kafka.KafkaMessageSender;
import com.savbill.revenuemanagement.mastermanagement.City.domain.City;
import com.savbill.revenuemanagement.mastermanagement.City.repository.CityRepository;
import com.savbill.revenuemanagement.mastermanagement.Country.domain.Country;
import com.savbill.revenuemanagement.mastermanagement.Pincode.domain.Pincode;
import com.savbill.revenuemanagement.mastermanagement.Pincode.repository.PincodeRepository;
import com.savbill.revenuemanagement.mastermanagement.State.domian.State;
import com.savbill.revenuemanagement.mastermanagement.State.repository.StateRepository;
import com.savbill.revenuemanagement.productmanagement.PlanService.repository.ServiceRepository;
import com.savbill.revenuemanagement.rabbitmq.messages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.CreditDebitDocMessage;
import com.savbill.revenuemanagement.server.CustomerData;
import com.savbill.revenuemanagement.server.CustomerProcessor;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import com.savbill.revenuemanagement.mastermanagement.Country.repository.CountryRepository;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.Plan.repository.PostpaidPlanRepo;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroup;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.repocitory.PlanGroupRepository;
import com.savbill.revenuemanagement.productmanagement.Tax.dto.TaxDto;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import com.savbill.revenuemanagement.rabbitmq.MessageReceiverWithThread;

import com.savbill.revenuemanagement.rabbitmq.SendOnlinePaymentRevenueMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.ChangePlanMessage;
import com.savbill.revenuemanagement.utils.CommonUtils;
import com.google.gson.Gson;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PrepaidInvoiceService extends PostpaidInvoiceThread{

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private MessageReceiverWithThread messageReceiverWithThread;

    @Autowired
    private CustomerChargeHistoryRepository customerChargeHistoryRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private InvoiceUtil invoiceUtil;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;

    @Autowired
    private CustomerLedgerDtlsRepository customerLedgerDtlsRepository;
    @Autowired
    CreditDocRepository creditDocRepository;
    @Autowired
    SubscriberService subscriberService;

    @Autowired
    private DbrService dbrService;
    @Autowired
    private CustPlanMapppingRepository custPlanMappingRepository;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private DebitDocumentMapper debitDocumentMapper;

    @Autowired
    private ClientServiceRepository clientServiceRepository;

    @Autowired
    private TaxService taxService;

    @Autowired
    private TrialDebitDocRepository trialDebitDocRepository;

    @Autowired
    private TrialDebitDocumentDetailRepository trialDebitDocumentDetailRepository;

    @Autowired
    private CustomerLedgerRepository customerLedgerRepository;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private CustomerServiceMapRepository customerServiceMapRepository;

//    @Autowired
//    MessageSender messageSender;

    @Autowired
    private DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;
    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private PostpaidInvoiceService postpaidInvoiceService;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    ClientServiceSrv clientServiceSrv;
    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;
    @Autowired
    StaffUserRepository staffUserRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;
    @Autowired
    CustChargeRepository custChargeRepository;
    @Autowired
    TempPartnerLedgerDetailsRepository tempPartnerLedgerDetailsRepository;
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
    @Autowired
    CustChargeDetailsRepository custChargeDetailsRepository;
    @Autowired
    CustPlanMappingService custPlanMappingService;
    public Map<String, String> sortColMap = new HashMap<>();
    public PageRequest pageRequest = null;

    @Autowired
    CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    private PartnerCommissionService partnerCommissionService;

    @Autowired
    TrialDebitDocumentTAXRelRepository trialDebitDocumentTAXRelRepository;

    @Autowired
    CustomerAddressRepository customerAddressRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    PlanGroupRepository planGroupRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    private Tracer tracer;
    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CustomerInventoryUtil customerInventoryUtil;

    @Autowired
    KRAUtils kraUtils;

    private static final Logger logger = Logger.getLogger(PrepaidInvoiceService.class);

    private Iterable<DebitDocument> debitDocument;

    public DebitDocument createPrepaidInvoice(CustomerBillingMessage customerBillingMessage, Customers customers) {
        Map<String, Object> data = customerBillingMessage.getData();
        Integer RESP_CODE = APIConstants.FAIL;
        String nextBillDate = null;
        LocalDate billDate = null;
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            TraceContext traceContext =customerBillingMessage.getTraceContext();
            MDC.put("type", "Fetch");
            MDC.put("traceId",traceContext.traceIdString());
            MDC.put("spanId",traceContext.spanIdString());
            Integer staffId = null;
            LocalDate strBillDate =  LocalDate.now();
            StaffUser staffUser =  null;
            if (data.get("currentUserLoggedInId") != null) {
                staffId = (Integer) data.get("currentUserLoggedInId");
                staffUser = staffUserRepository.findById(staffId).get();
            }
            MDC.put("userName", staffUser!=null? staffUser.getUsername() : "null" );

            if (CollectionUtils.isEmpty(data)) {
                logger.error("customer billing message data is empty");
            }
            if (!data.containsKey(CustomerBillingMessage.CUST_ID)) {
                logger.error("customer billing message custId is empty");
            }
            if(customerBillingMessage.getBilldate()!=null){
                billDate=customerBillingMessage.getBilldate();
            }
            Integer custId = (Integer) data.get(CustomerBillingMessage.CUST_ID);
            String createdByName = (String) data.get(CustomerBillingMessage.CREATED_BY_NAME);

            if(customers==null)
            {
                Optional<Customers> customersOptional = customersRepository.findById(custId);
                if (!customersOptional.isPresent()) {
                    logger.error("Given customer not available for id: " + custId);
                }
                customers = customersOptional.get();
            }
            logger.info("Initiated Prepaid Invoice creation for Customer: "+customers.getUsername());

            if(data.get("strDate") != null) {
                strBillDate = new DateTimeUtil().convertDateToDifferenFormat(inputFormatter, outputFormatter, (String) data.get("strDate"));
            }
            Integer renewalId;
            if (data.containsKey(CustomerBillingMessage.RENEWAL_ID)) {
                renewalId = (Integer) data.get(CustomerBillingMessage.RENEWAL_ID);
                logger.debug("Renewal Id for this Invoice of customer: "+customers.getUsername()+" is: "+ renewalId);
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
                if (renewalId != null &&  customerBillingMessage.getBilldateToday()==null) {
                    custPlanMapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getRenewalId() != null && custPlanMappping.getRenewalId().equals(renewalId)).collect(Collectors.toList());
                }
                if (customerBillingMessage.getOldCprIdsForChangePLan()!=null){
                    custPlanMapppings = custPlanMapppings.stream().filter(x->customerBillingMessage.getOldCprIdsForChangePLan().contains(x.getId())).collect(Collectors.toList());
                }
            }
            if(customers.getIstrialplan()){
                custPlanMapppings.removeIf(x->x.getIstrialplan());
            }
            if (CollectionUtils.isEmpty(custPlanMapppings)) {
                logger.error("Customer with username : " + customers.getUsername()+ "does not have any plan mapping!");
                return null;
            }
            //Direct charge during customer creation
            logger.info("Fetching overChargeList and  directChargeList is exist for  Customer: "+customers.getUsername());
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
            if(customerBillingMessage.getNewServiceId()!=null){
                custPlanMapppings = custPlanMapppings.stream().filter(x->x.getCustServiceMappingId().equals(customerBillingMessage.getNewServiceId())).collect(Collectors.toList());
            }
            List<Integer> planIds = custPlanMapppings.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
            List<Integer> custPackids = custPlanMapppings.stream().map(CustPlanMappping::getId).collect(Collectors.toList());
            List<Integer> csmIds = custPlanMapppings.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
            List<Long> custServiceIds = new ArrayList<>();
            List<CustomerServiceMapping> customerServiceMappings = new ArrayList<>();
            if(!CollectionUtils.isEmpty(customers.getCustomerServiceMappingList())) {
                customerServiceMappings = customers.getCustomerServiceMappingList();
            }else if(customers.getParentCustomers()!=null) {
                customerServiceMappings  = customers.getParentCustomers().getCustomerServiceMappingList();
            }
            logger.info("Fetching customerChargeHistories for  Customer: "+customers.getUsername());
            List<CustomerChargeHistory> customerChargeHistories = customers.getCustomerChargeHistories();
            List<Double> discounts = (List<Double>) data.get("discount");
            List<String> discountTypes = (List<String>) data.get("discountType");
            if(discountTypes!= null && discountTypes.contains(CommonConstants.DISCOUNT_TYPE.RECURRING)) {
                logger.debug("Setting  "+CommonConstants.DISCOUNT_TYPE.RECURRING+"  discount in CustomerServiceMappings for  Customer: "+customers.getUsername());
                if ((discounts == null || discounts.isEmpty() && (discountTypes == null || discountTypes.isEmpty()))) {
                    customerServiceMappings.forEach(c -> c.setDiscount(0.0));
                } else {
                    for (int i = 0; i < customerServiceMappings.size(); i++) {
                        double discount = i < discounts.size() ? discounts.get(i) : 0.0;
                        customerServiceMappings.get(i).setDiscount(discount);
                    }
                }
            }
            else  if(discountTypes!= null && discountTypes.contains(CommonConstants.DISCOUNT_TYPE.ONE_TIME)) {
                logger.debug("Setting  "+CommonConstants.DISCOUNT_TYPE.ONE_TIME+"  discount in  CustomerServiceMappings for  Customer: "+customers.getUsername());
                if ((discounts == null || discounts.isEmpty() && (discountTypes == null || discountTypes.isEmpty()))) {
                    customerServiceMappings.forEach(c -> c.setDiscount(0.0));
                } else {
                    for (int i = 0; i < customerServiceMappings.size(); i++) {
                        double discount = i < discounts.size() ? discounts.get(i) : 0.0;
                        customerServiceMappings.get(i).setDiscount(discount);
                    }
                }
            }

            if (CollectionUtils.isEmpty(customerChargeHistories)) {
                logger.error("Customer does not have any charge mapping! for userName:  "+ customers.getUsername());
                return null;
            }
            if(customerBillingMessage.getNewServiceId()!=null){
                customerChargeHistories = customerChargeHistories.stream().filter(x->custPackids.contains(x.getCustPlanMapppingId())).collect(Collectors.toList());
            }

            String postpaidAdvance = null;
            customerChargeHistories = customerChargeHistories.stream().filter(x -> !(x.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_ONE_TIME) && x.getIsFirstChargeApply().equals(true))).collect(Collectors.toList());
//            ****child invoice details for change plan having invoice type GROUP****
            if (customerBillingMessage.getChildIds()!=null && customerBillingMessage.getChildIds().size()>0){
                logger.error("Initiating Invoice Details process for Child customers with id's:  "+ customerBillingMessage.getChildIds());
                ChildInvoiceDetails childInvoiceDetails = getChildInvoiceDetails(customerBillingMessage.getChildIds(),renewalId,strBillDate);
                customerDircharges.addAll(custChargeDetailsRepository.findAllByCustomerInAndIsUsed(customersRepository.findAllById(customerBillingMessage.getChildIds()),false));
                custPlanMapppings.addAll(childInvoiceDetails.getCustPlanMapppings());
                if(childInvoiceDetails.getCustPlanMapppings().size()>0) {
                    custPackids.addAll(childInvoiceDetails.getCustPlanMapppings().stream().map(i -> i.getId()).collect(Collectors.toList()));
                }
                customerChargeHistories.addAll(childInvoiceDetails.getCustomerChargeHistories());
                customerServiceMappings.addAll(childInvoiceDetails.getCustomerServiceMappings());
            }

            if (customerBillingMessage.getType()!=null){
                postpaidAdvance = customerBillingMessage.getType();
            }


            List<Double> orignalChargeAmount=customerChargeHistories.stream().map(x->x.getChargeAmount()).collect(Collectors.toList());
            List<Integer> chargeIds=customerChargeHistories.stream().map(x->x.getId()).collect(Collectors.toList());
            List<Long> custInvIds = new ArrayList<>();
            if(data.containsKey("inventorycaftocustomer") && data.get("inventorycaftocustomer").equals(true)) {
                custInvIds = customerInventoryMappingRepo.findAllByCustomerId(customers.getId().longValue());
            }
            List<Integer> custpackForchc = custPlanMapppings.stream().map(i->i.getId()).collect(Collectors.toList());
            customerChargeHistories = customerChargeHistories.stream().filter(custChargeHis -> custpackForchc.contains(custChargeHis.getCustPlanMapppingId())).collect(Collectors.toList());
            logger.info("Initiating prepareInvoiceDetail process for  customer :  "+ customers.getUsername());
            InvoiceDetails invoiceDetails = invoiceUtil.prepareInvoiceDetail(customers, custPlanMapppings, customerChargeHistories, customerDircharges, customerServiceMappings,custInvIds,postpaidAdvance,customerBillingMessage.isPlanValidityChangePlan());

            DebitDocument debitDocument = invoiceDetails.getDebitDocument();
            if(debitDocument != null) {
                // skip
            } else {
                return null;
            }
             if(debitDocument.getTotalamount() <= 0) {
                if(customerBillingMessage.getData().containsKey("mvnoId")){
                    Integer mvnoId  = (Integer) customerBillingMessage.getData().get("mvnoId");
                    ClientService allowZeroInvoice = clientServiceRepository.getByNameAndMvnoId(Constants.ALLOWZEROCHARGEINVOICE,mvnoId);
                    if (allowZeroInvoice.getValue().equalsIgnoreCase("No")){
                        logger.error("Invoice can not be generated due to 0 ammount");
                        return null;
                    }
                }
            }
            if(custPlanMapppings != null){
                Boolean isOrgCustomer  = hasInvoiceToOrg(custPlanMapppings);
                if(isOrgCustomer){
                    logger.error("billTo organization customer is found.No Invoice will be generated.");
                    return null;
                }

            }

            debitDocument.setDebitDocDetailsList(null);

            if(customerBillingMessage.getData().get(CustomerBillingMessage.BILL_RUN_ID) != null) {
                messageReceiverWithThread.updateBillRunData(debitDocument, Integer.valueOf(customerBillingMessage.getData().get(CustomerBillingMessage.BILL_RUN_ID).toString()));
                debitDocument.setBillrunid(Integer.valueOf(customerBillingMessage.getData().get(CustomerBillingMessage.BILL_RUN_ID).toString()));
            }

            debitDocument = debitDocRepository.save(debitDocument);
            logger.info("DebitDocument Id is : "+debitDocument.getId()+" for  customer :  "+ customers.getUsername() );
            if (!custInvIds.isEmpty()) {
                List<CustomerInventoryMapping> inventoryMappings = customerInventoryMappingRepo.findAllByIdInAndCustomerId(custInvIds, customers.getId().longValue());
                inventoryMappings=inventoryMappings.stream().filter(x->x.getIsDeleted()!=null && x.getIsDeleted().equals(false)).collect(Collectors.toList());
                for(CustomerInventoryMapping inventoryMapping: inventoryMappings) {
                    DebitDocDetails debitDocDetails1 =customerInventoryUtil.setDebitDocDetailsForInventory(inventoryMapping);
                    if(debitDocDetails1 != null) {
                        DebitDocument debitDocument1=new DebitDocument();
                        debitDocument1.setId(debitDocument.getId());
                        List list=new ArrayList();
                        list.add(debitDocDetails1);
                        debitDocument1.setDebitDocDetailsList(list);
                        dbrService.addDbrForCustomerInventoryCharge(customers.getId(),debitDocument1);
                    }
                }
            }
            List<DebitDocDetails> debitDocDetailsList = invoiceDetails.getDebitDocDetails();
            debitDocument.setDebitDocDetailsList(debitDocDetailsList);
            DebitDocument finalDebitDocument = debitDocument;
            debitDocDetailsList = debitDocDetailsList.stream().peek(debitDocDetails -> debitDocDetails.setDebitdocumentid(finalDebitDocument.getId())).collect(Collectors.toList());
            debitDocDetailRepository.saveAll(debitDocDetailsList);
            logger.info("Initiating addCustomerLedger process for  customer :  "+ customers.getUsername() );
            addCustomerLedger(debitDocument, customers, createdByName, customerBillingMessage.getType(),customerBillingMessage.getChildIds(),customerBillingMessage.getPayableChildId());

            if (customerBillingMessage.getBilldate()!=null){billDate = customerBillingMessage.getBilldate();}
            createCNforChangePlanAndCancelAndRegenrate(customerBillingMessage, debitDocument, customerServiceMappings,custInvIds,billDate);

            if(customerBillingMessage.getData().containsKey("paymentSource")){
                logger.info("Initiating Online Payment process for  customer :  "+ customers.getUsername() + "when paymentSource flag exists");
                String paymentSource = String.valueOf(customerBillingMessage.getData().get("paymentSource"));
                if(paymentSource.length() > 0) {
                    List<Long> buIds =  null;
                    Integer mvnoId = null;
                    Integer partnerId = null;
                    Boolean isLco = false;
                    String getcreatedByName = "";
                    Integer getCreatedById = null;



                    if(customerBillingMessage.getData().containsKey("buIds")){
                        buIds = (List<Long>) customerBillingMessage.getData().get("buIds");
                    }
                    if(customerBillingMessage.getData().containsKey("mvnoId")){
                        mvnoId  = (Integer) customerBillingMessage.getData().get("mvnoId");
                    }
                    if(customerBillingMessage.getData().containsKey("partnerId")){
                        partnerId  = (Integer) customerBillingMessage.getData().get("partnerId");
                    }
                    if(customerBillingMessage.getData().containsKey("isLco")){
                        isLco  = (Boolean) customerBillingMessage.getData().get("isLco");
                    }
                    if(customerBillingMessage.getData().containsKey("createById")){
                        getCreatedById = (Integer) customerBillingMessage.getData().get("createById");
                    }
                    if(customerBillingMessage.getData().containsKey("createByName")){
                        getcreatedByName  = (String) customerBillingMessage.getData().get("createByName");
                    }
                    String transacationNumber = "";
                    Double amount = 0.0;
                    if(customerBillingMessage.getData().containsKey("additionalInformationDTO")){
                        AdditionalInformationDTO additionalInformationDTO = (AdditionalInformationDTO) customerBillingMessage.getData().get("additionalInformationDTO");
                        transacationNumber = additionalInformationDTO.getTransactionNumber();
                        amount =  additionalInformationDTO.getAmount();
                    }
                    RecordPaymentPojo recordPaymentPojo = creditDocService.createPaymentForOnlineWithAmount(debitDocument , paymentSource , transacationNumber, amount);
                    creditDocService.save(recordPaymentPojo,false,false,false,mvnoId , partnerId , buIds,isLco,getCreatedById,getcreatedByName);
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
            // if caf has any adjusted credit document that will also be adjusted in normal customer
            List<String> statusList = new ArrayList<>();
            statusList.add(Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
            statusList.add(Constants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            List<CreditDocument> getAllAdjustedCreditDoc = creditDocRepository.findAllByCustomerAndStatusInAndTypeNot(debitDocument.getCustomer(),statusList,Constants.CREDIT_DOC_TYPE_CREDITNOTE);
            List<CreditDebitDocMapping> getAdjustedEntries = new ArrayList<>();
            if(!getAllAdjustedCreditDoc.isEmpty()) {
                List<Integer> creditdocIds = getAllAdjustedCreditDoc.stream().map(creditDocument -> creditDocument.getId()).collect(Collectors.toList());
                getAdjustedEntries = creditDebtMappingRepository.findByCreditDocIdAndDebtDocIdNotNull(creditdocIds);
            }
            if (!getAllAdjustedCreditDoc.isEmpty() && getAdjustedEntries.isEmpty() && renewalId == null){
                logger.info("Adjusting Payment for   customer :  "+ customers.getUsername() + " if payment done during  CAF invoice payment");
                debitDocument=paymentAdjustmentForForCafCust(finalDebitDocument,getAllAdjustedCreditDoc);
                List<CreditDebitDocMapping> creditDebitDocMapping = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
                CreditDebitDocMessage creditDebitDocMessage = new CreditDebitDocMessage();
                creditDebitDocMessage.setCreditDebitDocMappingList(creditDebitDocMapping);
//                messageSender.send(creditDebitDocMessage,RabbitMqConstants.QUEUE_CREDIT_DEBIT_DOC_TO_CMS);
                logger.info("Queue sent to CMS reagring CreditDoc for   customer :  "+ customers.getUsername() + " if payment done during  CAF creation or customer portal");
                kafkaMessageSender.send(new KafkaMessageData(creditDebitDocMessage, CreditDebitDocMessage.class.getSimpleName()));
            }


            //Will be handle if payment done from customer aquasition portal
            List<CreditDocument> getAllCreditDoc = creditDocRepository.findAllByCustomerAndStatus(debitDocument.getCustomer(),CommonConstants.PAYMENT_CONDITION.ONLINE_PAYMENT_APPROVED);
            if (!getAllCreditDoc.isEmpty()){
                logger.info("Adjusting Payment for   customer :  "+ customers.getUsername() + " if payment done during  CAF creation or customer portal");
                debitDocument=paymentAdjustmentForCaptiPortalCust(finalDebitDocument,getAllCreditDoc);
                List<CreditDebitDocMapping> creditDebitDocMapping = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
                CreditDebitDocMessage creditDebitDocMessage = new CreditDebitDocMessage();
                creditDebitDocMessage.setCreditDebitDocMappingList(creditDebitDocMapping);
//                messageSender.send(creditDebitDocMessage,RabbitMqConstants.QUEUE_CREDIT_DEBIT_DOC_TO_CMS);
                logger.info("Queue sent to CMS reagring CreditDoc for   customer :  "+ customers.getUsername() + " if payment done during  CAF creation or customer portal");
                kafkaMessageSender.send(new KafkaMessageData(creditDebitDocMessage, CreditDebitDocMessage.class.getSimpleName()));
            }
            List<CreditDocument> getAllCreditDocPending = creditDocRepository.findAllByCustomerAndStatus(debitDocument.getCustomer(),CommonConstants.PAYMENT_CONDITION.ONLINE_PAYMENT_PENDING);
            if (!getAllCreditDocPending.isEmpty()){
                for (CreditDocument doc : getAllCreditDocPending){
                    doc.setStatus("Payment Failed");
                    doc.setInvoiceId(finalDebitDocument.getId());
                    creditDocRepository.save(doc);
                }
            }

            debitDocument.setDebitDocDetailsList(invoiceDetails.getDebitDocDetails());

            String customerTypeName = getCustomerType(customers.getCusttype());
            boolean advanceChargehistory = false;
            boolean recurrChargeHistory = false;
            advanceChargehistory = customerChargeHistories.stream()
                    .anyMatch(x -> x.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_ADVANCE));
            recurrChargeHistory = customerChargeHistories.stream()
                    .anyMatch(x -> x.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_RECURRING));
            //TODO: get value from common service for organization customer.
            if (!(customers.getUsername().equalsIgnoreCase(customerTypeName))) {
                logger.info("Initiating addCustomerLedger process for  customer :  "+ customers.getUsername() );
                if(customers.getCusttype().equals(Constants.CUSTOMER_TYPE.PREPAID))
                    dbrService.addDbrForPrepaidCustomer(customerChargeHistories, debitDocument, debitDocument.getCustomer(), custPlanMapppings, customerServiceMappings, customerDircharges, customerBillingMessage.isTrailPlanFromTrailDay());

                if(customers.getCusttype().equals(Constants.CUSTOMER_TYPE.POSTPAID)) {
                    if (advanceChargehistory){
                        dbrService.addDbrForPrepaidCustomer(customerChargeHistories, debitDocument, debitDocument.getCustomer(), custPlanMapppings, customerServiceMappings, customerDircharges, customerBillingMessage.isTrailPlanFromTrailDay());
                    }
                    if (recurrChargeHistory){
                        dbrService.addDbrForPostpaidCustomer(customerChargeHistories, debitDocument, debitDocument.getCustomer(), custPlanMapppings, customerServiceMappings, customerDircharges);
                    }
                }
                partnerCommissionService.addPartnerCommission(customerChargeHistories, custPlanMapppings, debitDocument, debitDocument.getCustomer(), staffId,staffUser);
            } else
                dbrService.addDbrForOrgCustomerPrepaid(debitDocument.getCustomer(), customerDircharges, debitDocument, custPlanMapppings, customerChargeHistories, customerServiceMappings);


            List<CustPlanMappping> custPlanMapppingList = custPlanMapppings.stream().filter(x->custPackids.contains(x.getId())).collect(Collectors.toList());
            logger.info("Initiating updateAfterInvoiceCreatedData process for  customer :  "+ customers.getUsername() );
            updateAfterInvoiceCreatedData(customers, custPlanMapppingList, Long.valueOf(finalDebitDocument.getId()), false, customerDircharges,postpaidAdvance,billDate);
            //DebitDoc tax relation
            List<DebitDocumentTAXRel> debitDocumentTAXRels = new ArrayList<>();
            List<CustomerChargeHistory> finalCustomerChargeHistories = customerChargeHistories;
            if(debitDocDetailsList!=null && !debitDocDetailsList.isEmpty())
            {
                for(DebitDocDetails details:debitDocDetailsList)
                    debitDocumentTAXRels = taxService.setTaxAmountFromCharge2(finalDebitDocument, details.getChargeid(),details.getDiscountPercentage(),details.getDebitdocdetailid().longValue(),details.getPlanId(),debitDocumentTAXRels);
                    //debitDocumentTAXRels.add(debitDocumentTAXRel);
            }

            if(!CollectionUtils.isEmpty(debitDocumentTAXRels))
                debitDocument.setDebitDocumentTAXRels(debitDocumentTAXRels);
            boolean isOrgCust=false;
            if(customers.getId()==1){
                isOrgCust=true;
            }
            String isCaf="false";
            if(customers.getCafno()!=null){
                isCaf="true";
            }
            Customers refCustomer = custPlanMapppings.get(0).getCustomer();
            List<Map.Entry<Integer, Long>> CustPackAndDebitDocIdPair = new ArrayList<>();

            // Adding pairs to the list
            for (CustPlanMappping custPlanMappping: customers.getPlanMappingList()) {
                CustPackAndDebitDocIdPair.add(new AbstractMap.SimpleEntry<>(custPlanMappping.getId(), custPlanMappping.getDebitdocid()));
            }

            customerChargeHistoryRepository.saveAll(customerChargeHistories);

            if (customers.getNextBillDate()!=null){
                nextBillDate = String.valueOf(customers.getNextBillDate());
            }
            List<Map.Entry<Integer, String>> CustPackAndEndDatePair = new ArrayList<>();

            if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)){
                for (CustPlanMappping custPlanMappping: customers.getPlanMappingList()) {
                    CustPackAndEndDatePair.add(new AbstractMap.SimpleEntry<>(custPlanMappping.getId(), custPlanMappping.getEndDate().toString()));
                }
            }
            PrepaidInvoiceCharges prepaidInvoiceCharges=new PrepaidInvoiceCharges( customers.getId(),customers.getUsername(),customers.getCustomerType(),debitDocument.getTotalamount(),debitDocument.getId().longValue(),customers.getUsername(),isOrgCust,debitDocument.getTotalamount(),refCustomer.getCreatedById(),null,custServiceIds,"null","false",isCaf,0L,debitDocument,customers.getWalletbalance(),debitDocument.getPaymentStatus(),debitDocument.getBillrunid(),debitDocument.getCreatedByName(),CustPackAndDebitDocIdPair,debitDocument.getAdjustedAmount(),debitDocument.getBillrunstatus(),false,debitDocument.getIsDirectChargeInvoice(),null,nextBillDate,CustPackAndEndDatePair,null);
            logger.info("PrepaidInvoiceCharges message sent to CMS to update DebitDoc , CustPackageRel and DebitDocdetails  for  customer :  "+ customers.getUsername() );
            kafkaMessageSender.send(new KafkaMessageData(prepaidInvoiceCharges,PrepaidInvoiceCharges.class.getSimpleName()));
            ClientService clientService = null;
            Integer MvnoId=customers.getMvnoId()!=null?customers.getMvnoId():1;
            try {
                clientService = clientServiceRepository.findByNameAndMvnoId(CommonConstants.REVENUE_AUTHORITY_NAME,MvnoId);
            } catch (Exception e) {
                // Log the exception but continue gracefully
                logger.warn("ClientService not found for REVENUE_AUTHORITY_NAME and mvnoId: "+MvnoId, e);
                clientService = null;
            }
            try {
                if(clientService!=null && "KRA".equalsIgnoreCase(clientService.getValue())) {
                    kraUtils.processEtimsAddInvoice(Collections.singletonList(debitDocument));
                }
            } catch (Exception e) {
                ApplicationLogger.logger.error("Some Exception occured while integrating to {}",e);
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + " Customer management Service, "+"Successfully Invoice Created for Customer id :" +  custId + LogConstants.REQUEST_BY +( staffUser!=null? staffUser.getUsername() : "null" )+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return debitDocument;
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            ex.printStackTrace();
            logger.error(LogConstants.REQUEST_FROM+ " Customer management Service, "+"Error During Invoice Generation for Customer id : " +   (Integer) data.get(CustomerBillingMessage.CUST_ID) +   LogConstants.REQUEST_BY + (String) data.get(CustomerBillingMessage.CREATED_BY_NAME) +  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;
    }

    public String setInvoiceXml(DebitDocument debitDocument) {
        String xml = "";
        List<ServiceQosPojo> serviceQosPojos = new ArrayList<>();
        List<ServiceTotalAmount> serviceTotalAmounts = new ArrayList<>();
        try {
            serviceQosPojos = serviceQosPojos(debitDocument);
            serviceTotalAmounts = getserviceTotalAmounts(debitDocument);
            Double dueAmountFromLastInvoice = debitDocRepository.findPreviousInvoiceRemainingAmount(debitDocument.getId(), debitDocument.getCustomer().getId());
            Object[] result = debitDocRepository.findPlanTypeByDebitDocumentId(debitDocument.getId());
            Object[] resultRow = (Object[]) result[0];
            String planType = (String) resultRow[0];
            Integer validity = ((BigDecimal) resultRow[1]).intValue();
            Timestamp timestamp = debitDocRepository.findFirstDebitDocumentStartDate(debitDocument.getCustomer().getId());
            LocalDate customerActivationDate = timestamp.toLocalDateTime().toLocalDate();
            Integer billingCycle = debitDocRepository.findLowestBillingCycle(debitDocument.getCustomer().getId(), debitDocument.getId());

            Map<String, Object> invoiceData = new HashMap<>();
            invoiceData.put("dueAmountFromLastInvoice", dueAmountFromLastInvoice);
            invoiceData.put("planType", planType);
            if(!planType.equalsIgnoreCase(CommonConstants.PLAN_TYPE_POSTPAID)){
                invoiceData.put("validity", validity);
            } else {
                invoiceData.put("billingCycle", billingCycle);
            }
            invoiceData.put("customerActivationDate", customerActivationDate);

            Invoice invoice = convertDebitDocToInvoice(debitDocument,serviceQosPojos,serviceTotalAmounts,invoiceData);
            xml = invoiceUtil.createXML(invoice);
            debitDocument.setDocument(xml);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while save xml: "+ex.getMessage());
        }
        return xml;
    }


    public String setPartnerInvoiceXml(PartnerDebitDocument debitDocument,Partner parentPartner,List<PartnerLedgerDetails> commissionList,List<PartnerLedgerDetails> revertCommissionList,List<PartnerLedgerDetails> transfercommissionList,Double transferCreditCommissionAmount,Double transferDebitCommissionAmount) {
        String xml = "";
        try {
            Invoice invoice = convertPartnerDebitDocToInvoice(debitDocument,parentPartner,commissionList,revertCommissionList,transfercommissionList,transferCreditCommissionAmount,transferDebitCommissionAmount);
            xml = invoiceUtil.createXML(invoice);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while save xml: "+ex.getMessage());
        }
        return xml;
    }


    public  List<ServiceQosPojo> serviceQosPojos (DebitDocument debitDocument){
        try{
            List<DebitDocDetails> debitDocDetailsList = debitDocDetailRepository.findDebitDocIdAndServiceIdByMvnoDebitDocId(debitDocument.getId());
            // add filter for not include direct charge in serviceqosNameCounts
            debitDocDetailsList = debitDocDetailsList.stream().filter(debitDocDetails -> !debitDocDetails.getChargetype().equalsIgnoreCase(Constants.CHARGE_TYPE_CUSTOMER_DIRECT)).collect(Collectors.toList());
            List<ServiceQosPojo> serviceQosPojos = new ArrayList<>();

            if (debitDocDetailsList!=null) {
                Map<Long, List<DebitDocDetailList>> groupedDetails = groupByServiceId(debitDocDetailsList);

                if (!CollectionUtils.isEmpty(groupedDetails)) {
                    for (Map.Entry<Long, List<DebitDocDetailList>> entry : groupedDetails.entrySet()) {
                        ServiceQosPojo serviceQosPojo = new ServiceQosPojo();
                        List<QosNameCount> qosNameCounts = new ArrayList<>();
//                        List<Long> debitDocIds = entry.getValue().stream().map(i -> i.getDebitDocId().longValue()).collect(Collectors.toList());
                        List<Integer> planIds = entry.getValue().stream()
                                .map(i -> Integer.parseInt(i.getPlanId()))
                                .collect(Collectors.toList());
                        List<QosPojo> qosPojoList = new ArrayList<>();
                        for(Integer planId:planIds) {
                            QosPojo qosPojo = postpaidPlanRepo.findQosDetails(planId);
                            qosPojoList.add(qosPojo);
                        }
                        String serviceName = " ";
                        if (!CollectionUtils.isEmpty(qosPojoList)) {
                            serviceName = serviceRepository.findserviceNameByServiceId(entry.getKey());
                            Map<String, Long> qospolicyIdCounts = qosPojoList.stream()
                                    .collect(Collectors.groupingBy(QosPojo::getQosName, Collectors.counting()));

                            for (Map.Entry<String, Long> countEntry : qospolicyIdCounts.entrySet()) {
                                QosNameCount qosNameCount = new QosNameCount();
                                qosNameCount.setQosName(countEntry.getKey());
                                qosNameCount.setCount(countEntry.getValue());
                                qosNameCounts.add(qosNameCount);
                            }
                        }
                        serviceQosPojo.setServiceName(serviceName);
                        serviceQosPojo.setQosNameCounts(qosNameCounts);
                        serviceQosPojos.add(serviceQosPojo);
                    }
                }
            }

            return  serviceQosPojos ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public  List<ServiceTotalAmount> getserviceTotalAmounts (DebitDocument debitDocument){
        try{
            List<DebitDocDetails> debitDocDetailsList = debitDocDetailRepository.findDebitDocIdAndServiceIdByMvnoDebitDocId(debitDocument.getId());
            Map<Long, List<DebitDocDetailList>> groupedDetails = groupByServiceId(debitDocDetailsList);

            List<ServiceTotalAmount> serviceTotalAmounts = new ArrayList<>();
            ServiceTotalAmount serviceTotalAmountForIntallionCharge = new ServiceTotalAmount();
            Double totalAmountCustomerDirect = 0d;
            for (Map.Entry<Long, List<DebitDocDetailList>> entry : groupedDetails.entrySet()) {

                ServiceTotalAmount serviceTotalAmount = new ServiceTotalAmount();
                String serviceName = serviceRepository.findserviceNameByServiceId(entry.getKey());
                Double totalAmount = entry.getValue().stream().filter(i->i.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_ADVANCE)).mapToDouble(amount -> amount.getTotalAmount()).sum();
                serviceTotalAmount.setServiceName(serviceName);
                serviceTotalAmount.setTotalAmount(totalAmount);

                Double customerDirectAmount = entry.getValue().stream().filter(i->i.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT)).mapToDouble(amount -> amount.getTotalAmount()).sum();
                if (customerDirectAmount>0){
                    totalAmountCustomerDirect+=customerDirectAmount;
                }

                serviceTotalAmounts.add(serviceTotalAmount);

            }
            if (totalAmountCustomerDirect>0){
                serviceTotalAmountForIntallionCharge.setServiceName(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT);
                serviceTotalAmountForIntallionCharge.setTotalAmount(totalAmountCustomerDirect);
                serviceTotalAmounts.add(serviceTotalAmountForIntallionCharge);
            }


            return  serviceTotalAmounts ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public  Map<Long, List<DebitDocDetailList>> groupByServiceId(List<DebitDocDetails> details) {
        List<Integer> planIds=details.stream().filter(x->x.getPlanId()!=null).map(x->Integer.valueOf(x.getPlanId())).distinct().collect(Collectors.toList());
        List<Integer> chargeIds=details.stream().filter(x->x.getChargeid()!=null).map(x->x.getChargeid()).distinct().collect(Collectors.toList());
        Map<Integer,String> qosNameByPlanId=new HashMap<>();
        Map<Integer,Charge> chargeByChargeId=new HashMap<>();
        Map<Integer,CustomerChargeHistory> chargeHistoryChargeId=new HashMap<>();
        planIds.stream().forEach(planId->{
            String qosName=postpaidPlanRepo.findQosName(planId);
            qosNameByPlanId.put(planId,qosName);
        });

        chargeIds.stream().forEach(chargeId->{
            Charge charge=chargeRepository.getOne(chargeId);
            chargeByChargeId.put(chargeId,charge);

            CustomerChargeHistory chargeHistory=new CustomerChargeHistory();
            chargeHistory.setChargeAmount(charge.getPrice());
            chargeHistory.setDiscount(0.0);
            taxService.calculateTierTax(chargeHistory,charge.getTax().getId());
            chargeHistoryChargeId.put(chargeId,chargeHistory);
        });

        Map<Long, List<DebitDocDetailList>> groupedByServiceId = new HashMap<>();
        for (DebitDocDetails detail : details) {
            Long serviceId = detail.getServiceId();
            if (!groupedByServiceId.containsKey(serviceId))
                groupedByServiceId.put(serviceId, new ArrayList<>());

            String qosName=qosNameByPlanId.get(Integer.parseInt(detail.getPlanId()));
            Charge charge=chargeByChargeId.get(detail.getChargeid());
            CustomerChargeHistory chargeHistory=chargeHistoryChargeId.get(detail.getChargeid());
            Double chargeActualOfferPriceWithTax=chargeHistory.getChargeAmount()+chargeHistory.getTaxAmount();
            DebitDocDetailList detailList = new DebitDocDetailList(detail.getDebitdocumentid(), serviceId,detail.getTotalamount(),detail.getChargetype(),detail.getPlanId(),detail.getChargeid(),detail.getOfferPrice(),qosName,chargeActualOfferPriceWithTax);
            groupedByServiceId.get(serviceId).add(detailList);
        }
        return groupedByServiceId;
    }




    public String setInvoiceXml(TrialDebitDocument debitDocument,List<TrialDebitDocumentDetail> details) {
        String xml = "";
        try {
            Object[] result = trialDebitDocRepository.findPlanTypeByDebitDocumentId(debitDocument.getId());
            Object[] resultRow = (Object[]) result[0];
            String planType = (String) resultRow[0];
            Integer validity = ((BigDecimal) resultRow[1]).intValue();
            Integer billingCycle = trialDebitDocRepository.findLowestBillingCycle(debitDocument.getCustomer().getId(), debitDocument.getId());

            Map<String, Object> invoiceData = new HashMap<>();
            invoiceData.put("dueAmountFromLastInvoice", null);
            invoiceData.put("planType", planType);
            if(!planType.equalsIgnoreCase(CommonConstants.PLAN_TYPE_POSTPAID)){
                invoiceData.put("validity", validity);
            } else {
                invoiceData.put("billingCycle", billingCycle);
            }
            invoiceData.put("customerActivationDate", null);
            Invoice invoice = convertDebitDocToInvoice(debitDocument, details, invoiceData);
            xml = invoiceUtil.createXML(invoice);
            debitDocument.setDocument(xml);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while save xml: "+ex.getMessage());
        }
        return xml;
    }

    public void updateAfterInvoiceCreatedData(Customers customers, List<CustPlanMappping> custPlanMapppings, Long debitdocid, boolean isCaf, List<CustChargeDetails> custChargeDetails,String postpaidAdvance,LocalDate billdate) {
        if (!CollectionUtils.isEmpty(custPlanMapppings)) {
            try {
                if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.PREPAID)) {
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
                }
                else if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                    custPlanMapppings = custPlanMapppings.stream().sorted(Comparator.comparing(CustPlanMappping::getEndDate).reversed()).collect(Collectors.toList());
                    customers.setIsUsingByThread(false);
                    //customers.setNextBillDate(custPlanMapppings.get(0).getEndDate().toLocalDate());
                    LocalDate nextBillDate = customers.getNextBillDate();
                    LocalDate lastBillDate = nextBillDate;
                    List<Integer> planIds = custPlanMapppings.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
                    List<Integer> chargeIds = postpaidPlanChargeRepo.getChargeListByPlanIdList(planIds);
                    List<Integer> cpridsActive = custPlanMapppings.stream().filter(i->i.getCustPlanStatus().equalsIgnoreCase("Active"))
                            .map(i->i.getId()).collect(Collectors.toList());
                    List<CustomerChargeHistory> customerChargeHistories = customerChargeHistoryRepository.findAllByCustomerIdAndChargeIdIn(custPlanMapppings.get(0).getCustomer().getId(), chargeIds);
                    customerChargeHistories = customerChargeHistories.stream().filter(i->cpridsActive.contains(i.getCustPlanMapppingId())).collect(Collectors.toList());
                    List<CustomerChargeHistory> minCycle = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(customerChargeHistories)) {
                        minCycle = customerChargeHistories.stream().filter(customerChargeHistory -> !customerChargeHistory.getChargeType().equalsIgnoreCase("NON_RECURRING")).sorted(Comparator.comparing(CustomerChargeHistory::getNextBillDate)).collect(Collectors.toList());
                        nextBillDate = minCycle.get(0).getNextBillDate();
                    }
                    if(postpaidAdvance!=null && !postpaidAdvance.equalsIgnoreCase("Advance")) {
                        customers.setNextBillDate(nextBillDate);
                    }
                    customers.setLastBillDate(lastBillDate);
                    //when we generate invoice of child and parent combined child cust id was getting updated to parent in customerservicemapping table
                    customers.getCustomerServiceMappingList().removeIf(i->i.getCustId()!=customers.getId());
                    customersRepository.save(customers);
                    List<CustomerChargeHistory> finalMinCycle = minCycle;
                    custPlanMapppings = custPlanMapppings.stream().peek(custPlanMappping -> {
//                        if(postpaidAdvance!=null && postpaidAdvance.equalsIgnoreCase("Advance")){
//                            custPlanMappping.setIsInvoiceCreated(false);
//                        }else {
//                            custPlanMappping.setIsInvoiceCreated(true);
//                        }

                        /*this if for extending cpr date so that in never expires unless it's changePlan with bill date*/
                        if (!finalMinCycle.isEmpty()) {
                            custPlanMappping.setEndDate(finalMinCycle.get(finalMinCycle.size()-1).getNextBillDate().atStartOfDay().plusHours(23));
                            custPlanMappping.setExpiryDate(finalMinCycle.get(finalMinCycle.size()-1).getNextBillDate().atStartOfDay().plusHours(23));
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
        if(!CollectionUtils.isEmpty(custChargeDetails)) {
            custChargeDetails = custChargeDetails.stream().peek(custChargeDet -> custChargeDet.setIsUsed(true)).collect(Collectors.toList());
            custChargeDetailsRepository.saveAll(custChargeDetails);
        }
    }


    public void addCustomerLedger(DebitDocument debitDocument, Customers customers, String createdByName, String type,List<Integer> childIds,Integer payableChildId) {
        try {
            //add customer ledger details
            CustomerLedgerDtls customerLedgerDtls = new CustomerLedgerDtls();
            if(payableChildId != null){
                customerLedgerDtls.setFromId(payableChildId);
                customerLedgerDtls.setToId(payableChildId);
            }
            else {
                if (customers.getParentCustomers() != null && customers.getCustomerServiceMappingList().get(0).getInvoiceType().equalsIgnoreCase("Group")) {
                    customerLedgerDtls.setCustomer(customers.getParentCustomers());
                } else {
                    customerLedgerDtls.setCustomer(customers);
                }
            }
            customerLedgerDtls.setTranstype("DR");
            customerLedgerDtls.setTranscategory("INVOICE");
            customerLedgerDtls.setAmount(debitDocument.getTotalamount());
            customerLedgerDtls.setDebitdocid(debitDocument.getId());
            customerLedgerDtls.setDescription("Invoice Generated");
            customerLedgerDtls.setIsDelete(false);
            customerLedgerDtls.setIsVoid(false);
            customerLedgerDtlsRepository.save(customerLedgerDtls);
            if(payableChildId != null){
                /**Add in main customer ledger for audit**/
                CustomerLedgerDtls mainCustomerLedgerDtls = new CustomerLedgerDtls();
                mainCustomerLedgerDtls.setCustomer(customers);
                mainCustomerLedgerDtls.setTranstype("CBINV");
                mainCustomerLedgerDtls.setTranscategory("CHILD_DEBIT");
                mainCustomerLedgerDtls.setAmount(debitDocument.getTotalamount());
                mainCustomerLedgerDtls.setDebitdocid(debitDocument.getId());
                mainCustomerLedgerDtls.setDescription("Invoice Generated");
                mainCustomerLedgerDtls.setIsDelete(false);
                mainCustomerLedgerDtls.setIsVoid(false);
                customerLedgerDtlsRepository.save(mainCustomerLedgerDtls);
            }
            if(customers.getWalletbalance()!=null && customers.getWalletbalance()>0){
                customers.setWalletbalance(customers.getWalletbalance() +customerLedgerDtls.getAmount());
            }else {
                customers.setWalletbalance(customerLedgerDtls.getAmount());
            }
            if (childIds!=null) {
                List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByCustomerId(customers.getId());
                List<CustPlanMappping> childCustPlanMappings = custPlanMapppingRepository.findAllByCustomerIdIn(childIds);
                custPlanMapppings.addAll( childCustPlanMappings);
                customers.setPlanMappingList(custPlanMapppings);
            }

            if (type != null && type.equalsIgnoreCase(Constants.INVOICE_TYPE.CREATE_CUSTOMER))
                customers.setWalletbalance(customerLedgerDtls.getAmount());
            //customersRepository.save(customers);

            if (type != null && type.equalsIgnoreCase(Constants.INVOICE_TYPE.CREATE_CUSTOMER))
                addCustMledger(customerLedgerDtls, createdByName, debitDocument.getCreatedById(),customers);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while save customer ledger");
        }
    }

    private void addCustMledger(CustomerLedgerDtls customerLedgerDtls, String createdByName, Integer createdById, Customers customers) {

        try {
            CustomerLedger customerLedger = null;
            if(customers.getParentCustomers()!=null && customers.getCustomerServiceMappingList().get(0).getInvoiceType().equalsIgnoreCase("Group")){
                customerLedger = customerLedgerRepository.findByCustomerId(customers.getParentCustomers().getId()).orElse(null);
            }else {
                customerLedger = customerLedgerRepository.findByCustomerId(customers.getId()).orElse(null);
            }
            if(customerLedger == null){
                customerLedger = new CustomerLedger();
            }
//            CustomerLedger customerLedger = new CustomerLedger();
            customerLedger.setCustomer(customerLedgerDtls.getCustomer());
            customerLedger.setTotaldue(customerLedgerDtls.getAmount());
            customerLedger.setTotalpaid(0.00);
            customerLedger.setCreatedById(createdById);
            customerLedger.setLastModifiedById(createdById);
            customerLedger.setCreatedByName(createdByName);
            customerLedger.setLastModifiedByName(createdByName);
            customerLedgerRepository.save(customerLedger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Common method to get organization customer
     *
     * @param customerType
     * @return orgCustType
     * @Author Vikas
     */
    public String getCustomerType(String customerType) {
        try {
            if (customerType.equalsIgnoreCase("Prepaid")) {
                String name = "ORGANIZATION";
                String custType = clientServiceRepository.findValueByNameAndMvnoId(name , clientServiceSrv.getMvnoIdFromCurrentStaff());

                return custType;
            } else {
                String name = "ORGANIZATIONPOST";
                String custType = clientServiceRepository.findValueByNameAndMvnoId(name , clientServiceSrv.getMvnoIdFromCurrentStaff());
                return custType;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createCNforChangePlanAndCancelAndRegenrate(CustomerBillingMessage customerBillingMessage, DebitDocument debitDocument, List<CustomerServiceMapping> customerServiceMappings, List<Long> inventoryMappings,LocalDate billdate) {
        if (customerBillingMessage.getType() != null) {
            logger.info("Initiating createCNforChangePlanAndCancelAndRegenrate process for  debitDocId :  "+ debitDocument.getId() );
            List<Integer> oldDebitDocumentIds=new ArrayList<>();
            Map<String, Object> data = customerBillingMessage.getData();
            if (data.containsKey("oldDebitDocId") && (customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN) || customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CANCEL_REGENERATE))) {
                List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
                if (data.get("oldDebitDocId").toString().contains(",")) {
                    String[] oldDebitdocs = data.get("oldDebitDocId").toString().split(",");
                    for (String oldDebitDocId : oldDebitdocs) {
                        Optional<DebitDocument> oldDebitDoc = debitDocRepository.findById(Integer.valueOf(oldDebitDocId));
                        oldDebitDoc.get().setOperationType(customerBillingMessage.getType());
                        oldDebitDocumentIds.add(oldDebitDoc.get().getId());
                        debitDocRepository.save(oldDebitDoc.get());
                        String chargeType = debitDocDetailRepository.findByDebitdocumentid(Integer.valueOf(oldDebitDocId));
                        if (oldDebitDoc.isPresent()) {
                            List<CreditDebitDocMapping> debitDocMappings = creditDebtMappingRepository.findBydebtDocId(oldDebitDoc.get().getId());
                            if (!CollectionUtils.isEmpty(debitDocMappings)) {
                                creditDebitDocMappings.addAll(debitDocMappings);
                            }
                            logger.info("Initiating creatCreditNotAsPerService process for  debitDocId :  "+ oldDebitDoc.get().getId() );
                            creditDocService.creatCreditNotAsPerService(oldDebitDoc.get(), debitDocument, customerServiceMappings, "CN for " + customerBillingMessage.getType() + " for Debit doc: " + oldDebitDoc.get().getDocnumber(), false, inventoryMappings, customerBillingMessage.getType(), chargeType, null,billdate);
                        }
                    }
                } else {
                    Optional<DebitDocument> oldDebitDoc = null;
                    String oldDebitDocId = data.get("oldDebitDocId").toString();
                    if (!oldDebitDocId.equals("null") && oldDebitDocId != null && !oldDebitDocId.equals("") ) {
                        oldDebitDoc = debitDocRepository.findById(Integer.valueOf(data.get("oldDebitDocId").toString()));
                        if (oldDebitDoc.isPresent()) {
                            oldDebitDoc.get().setOperationType(customerBillingMessage.getType());
                            oldDebitDocumentIds.add(oldDebitDoc.get().getId());
                            debitDocRepository.save(oldDebitDoc.get());
                            List<CreditDebitDocMapping> debitDocMappings = creditDebtMappingRepository.findBydebtDocId(oldDebitDoc.get().getId());
                            String chargeType = debitDocDetailRepository.findByDebitdocumentid(Integer.valueOf(oldDebitDocId));
                            if (!CollectionUtils.isEmpty(debitDocMappings)) {
                                creditDebitDocMappings.addAll(debitDocMappings);
                            }
                            logger.info("Initiating creatCreditNotAsPerService process for  debitDocId :  "+ oldDebitDoc.get().getId() );
                            creditDocService.creatCreditNotAsPerService(oldDebitDoc.get(), debitDocument, customerServiceMappings, "CN for " + customerBillingMessage.getType() + " for Debit doc: " + oldDebitDoc.get().getDocnumber(), false, inventoryMappings, customerBillingMessage.getType(), chargeType, null,billdate);
                        }
                    }
                    else {
                        if(customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN))
                        {
                            logger.info("Initiating creatCreditNotAsPerService process ");
                            creditDocService.createCreditNoteForOrgCustomer(customerServiceMappings,customerBillingMessage);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(creditDebitDocMappings)) {
                    List<Integer> creditNotIds = creditDebitDocMappings.stream().map(CreditDebitDocMapping::getCreditDocId).collect(Collectors.toList());
                    //List<CreditDebitDocMapping> debitDocMappingsRes = creditDocService.adjustCNPayementWithDebitDoc(creditNotIds, debitDocument,oldDebitDocumentIds);
                    //creditDebtMappingRepository.deleteAll(creditDebitDocMappings);
                    //As values not saved so saved it manually
                    //creditDebtMappingRepository.saveAll(debitDocMappingsRes);
                }
            }
        }
    }

    /**
     * @Author Dhaval Khalasi
     * this method is  payment adjustment for payment came through payment gateway
     * **/

    public void createPaymentAndAdjustPaymentWithInvoice(CustomerBillingMessage customerBillingMessage, DebitDocument debitDocument, List<CustomerServiceMapping> customerServiceMappings) {
        if (customerBillingMessage.getData().containsKey("paymentSource")) {
            if(customerBillingMessage.getData().get("paymentSource") != null) {

            }
        }
    }


    /**
     * invoice creation for caf
     *
     * @Author Vikas
     */

    public TrialDebitDocument createPrepaidInvoiceCaf(CustomerBillingMessage customerBillingMessage) {
        logger.info("Initiating createPrepaidInvoiceCaf Method Process of CAF creation for Trial invoice");
        Map<String, Object> data = customerBillingMessage.getData();
        Integer RESP_CODE = APIConstants.FAIL;
        String nextBillDate = null;
        LocalDate billDate = null;
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            TraceContext traceContext =customerBillingMessage.getTraceContext();
            MDC.put("type", "Create");
            MDC.put("traceId",traceContext.traceIdString());
            MDC.put("spanId",traceContext.spanIdString());
            if (CollectionUtils.isEmpty(data)) {
                logger.error("customer billing message data is empty");
            }
            if (!data.containsKey(CustomerBillingMessage.CUST_ID)) {
                logger.error("customer billing message custId is empty");
            }
            if(customerBillingMessage.getBilldate()!=null){
                billDate=customerBillingMessage.getBilldate();
            }
            LocalDate strBillDate =  LocalDate.now();
            Integer custId = (Integer) data.get(CustomerBillingMessage.CUST_ID);
            Optional<Customers> customersOptional = Optional.ofNullable(customersRepository.getByCustomerId(custId));
            String createdByName = (String) data.get(CustomerBillingMessage.CREATED_BY_NAME);
            if (!customersOptional.isPresent()) {
                logger.error("Given customer not available for id: " + custId);
            }
            Customers customers = customersOptional.get();
            logger.info("Initiated createPrepaidInvoiceCaf Method Process  for Trial invoice of Customer : " + customers.getUsername());
            Integer renewalId;
            if (data.containsKey(CustomerBillingMessage.RENEWAL_ID)) {
                renewalId = (Integer) data.get(CustomerBillingMessage.RENEWAL_ID);
                logger.debug("Renewal Id  Trial invoice of Customer : " + customers.getUsername()+" is: " + renewalId);
            } else {
                renewalId = null;
            }
            logger.debug("Fetching custPlanMapppings  for Trial invoice of Customer : " + customers.getUsername());
            List<CustPlanMappping> custPlanMapppings = customers.getPlanMappingList();
            if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                custPlanMapppings.removeIf(custPlanMappping -> !custPlanMappping.getCustPlanStatus().equalsIgnoreCase("NewActivation") &&
                        !custPlanMappping.getCustPlanStatus().equalsIgnoreCase("Active"));
                custPlanMapppings.removeIf(CustPlanMappping::getIsInvoiceCreated);
                if (renewalId != null) {
                    custPlanMapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getRenewalId() != null && custPlanMappping.getRenewalId().equals(renewalId)).collect(Collectors.toList());
                }
            }
            if(customers.getIstrialplan()){
                custPlanMapppings.removeIf(x->x.getIstrialplan());
            }
            if (CollectionUtils.isEmpty(custPlanMapppings)) {
                logger.error("Customer not have any plan mapping!");
                return null;
            }
            //Direct charge during customer creation
            logger.debug("Fetching overChargeList , directChargeList is exists  for Trial invoice of Customer : " + customers.getUsername());
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
            if(customerBillingMessage.getNewServiceId()!=null){
                custPlanMapppings = custPlanMapppings.stream().filter(x->x.getCustServiceMappingId().equals(customerBillingMessage.getNewServiceId())).collect(Collectors.toList());
            }
            List<Integer> planIds = custPlanMapppings.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
            List<Integer> csmIds = custPlanMapppings.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
            List<Integer> custPlanIds = custPlanMapppings.stream().map(CustPlanMappping::getId).collect(Collectors.toList());
            List<CustomerServiceMapping> customerServiceMappings = customers.getCustomerServiceMappingList();
            if (!CollectionUtils.isEmpty(customerServiceMappings)) {
                logger.debug("Filtering customerServiceMappings based on CustPackRel ids for Trial invoice of Customer : " + customers.getUsername());
                customerServiceMappings = customerServiceMappings.stream().filter(custSerMap -> csmIds.contains(custSerMap.getId())).collect(Collectors.toList());
            }
            List<Integer> chargeIds = postpaidPlanChargeRepo.getChargeListByPlanIdList(planIds);
            List<CustomerChargeHistory> customerChargeHistories = customerChargeHistoryRepository.findAllByCustomerIdAndChargeIdInAndCustPlanMapppingIdIn(custId, chargeIds,custPlanIds);
            if (CollectionUtils.isEmpty(customerChargeHistories)) {
                logger.error("Customer not have any charge mapping!");
                return null;
            }
            customerChargeHistories = customerChargeHistories.stream().filter(x -> !(x.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_ONE_TIME) && x.getIsFirstChargeApply().equals(true))).collect(Collectors.toList());
            String postpaidAdvance = null;
            if (customers.getCusttype().equalsIgnoreCase("Postpaid") ){
                logger.debug("For Postpaid Customer customerChargeHistories filters applied for Trial invoice of Customer : " + customers.getUsername());
                postpaidAdvance = (String) data.get(CustomerBillingMessage.POSTPAIDADVANCE);
                if(postpaidAdvance !=null && postpaidAdvance.equalsIgnoreCase("Advance")) {
                    customerChargeHistories = customerChargeHistories.stream().filter(x ->  x.getLastBillDate()!=null).collect(Collectors.toList());
                }
                if (postpaidAdvance !=null && postpaidAdvance.equalsIgnoreCase("Both")){
                    LocalDate finalStrBillDate = strBillDate;
                    customerChargeHistories = customerChargeHistories.stream().filter(x ->  x.getNextBillDate().isEqual(finalStrBillDate)).collect(Collectors.toList());
                }
                if (data.containsKey("oldDebitDocId")  && customerBillingMessage.getType()!=null && customerBillingMessage.getType().equalsIgnoreCase(CommonConstants.INVOICE_TYPE.CANCEL_REGENERATE)){
                    Long debitdocId = Long.valueOf(data.get("oldDebitDocId").toString());
                    List<Integer> chcId = customerChargeHistoryRepository.findChargeIds(debitdocId);
                    customerChargeHistories = customerChargeHistories.stream().filter(x->chcId.contains(x.getId())).collect(Collectors.toList());
                }
                if (customerBillingMessage.getCprIds()!=null && customerBillingMessage.getCprIds().size()>0){
                    customerChargeHistories = customerChargeHistoryRepository.findAllByCustomerIdAndChargeType(customers.getId(),CommonConstants.CHARGE_TYPE_RECURRING);
                    customerChargeHistories = customerChargeHistories.stream().filter(x->customerBillingMessage.getCprIds().contains(x.getCustPlanMapppingId())).collect(Collectors.toList());
                    postpaidAdvance =Constants.INVOICE_TYPE.CHANGE_PLAN;
                    custPlanMapppings = custPlanMappingRepository.findAllByIdIn(customerBillingMessage.getCprIds());
                }

            }
            logger.info("Initiating prepareInvoiceDetailCaf  for Trial invoice of Customer : " + customers.getUsername());
            TrialInvoiceDetails invoiceDetails = invoiceUtil.prepareInvoiceDetailCaf(customers, custPlanMapppings, customerChargeHistories, null, customerServiceMappings,postpaidAdvance);
            TrialDebitDocument debitDocument = invoiceDetails.getTrialDebitDocument();
            debitDocument.setTrialDebitDocumentDetails(null);

            if(debitDocument.getTotalamount() <= 0) {
                if(customerBillingMessage.getData().containsKey("mvnoId")){
                    Integer mvnoId  = (Integer) customerBillingMessage.getData().get("mvnoId");
                    ClientService allowZeroInvoice = clientServiceRepository.getByNameAndMvnoId(Constants.ALLOWZEROCHARGEINVOICE,mvnoId);
                    if (allowZeroInvoice.getValue().equalsIgnoreCase("No")){
                        logger.error("Invoice can not be generated due to 0 ammount");
                        return null;
                    }
                }
            }
            if(custPlanMapppings != null){
                Boolean isOrgCustomer  = hasInvoiceToOrg(custPlanMapppings);
                if(isOrgCustomer){
                    logger.error("billTo organization customer is found.No Invoice will be generated.");
                    return null;
                }

            }
            List<TrialDebitDocumentDetail> debitDocDetailsList = invoiceDetails.getTrialDebitDocDetails();
            Boolean isCustomerPortal = customerBillingMessage.getIsCaptiveportal();
            if (isCustomerPortal) {
                logger.debug("isCustomerPortal Flag is true and Customer is created from Portal  for Trial invoice of Customer : " + customers.getUsername() );
                debitDocument.setAdjustedAmount(0.0);
                debitDocument.setTotaldue(0.0);
                debitDocument.setPaymentStatus(CommonConstants.PAYMENT_CONDITION.ONLINE_PAYMENT_PENDING);
            }
            List<Object[]> trialDebitDocumentData = trialDebitDocRepository.findIdsAndAdjustedAmountsByCustomerId(debitDocument.getCustomer().getId());

            Double oldTrailDebitDocAdjustmentAmount = trialDebitDocumentData.stream()
                    .mapToDouble(obj -> obj[1] != null ? (Double) obj[1] : 0.0)
                    .sum();

            List<Integer> trialDebitDocIds = trialDebitDocumentData.stream()
                    .map(obj -> ((Number) obj[0]).intValue())
                    .collect(Collectors.toList());

            debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);

            if (!trialDebitDocIds.isEmpty()) {
                trialDebitDocRepository.updateAdjustedAmountsToNull(trialDebitDocIds);
                double difference = debitDocument.getTotalamount() - oldTrailDebitDocAdjustmentAmount;
                double tolerance = 0.1;
                if(difference <= tolerance) {
                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                    debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.FULLY_PAID);
                } else if(Math.abs(debitDocument.getTotalamount() - oldTrailDebitDocAdjustmentAmount) < debitDocument.getTotalamount()) {
                    debitDocument.setAdjustedAmount(oldTrailDebitDocAdjustmentAmount);
                    debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                } else {
                    debitDocument.setAdjustedAmount(0.0);
                    debitDocument.setPaymentStatus(Constants.DEBIT_DOC_STATUS.UNPAID);
                }
            }
            debitDocument = trialDebitDocRepository.save(debitDocument);

            if (!trialDebitDocIds.isEmpty()) {
                List<Integer> creditDocumentIds = creditDocRepository.findCreditDocumentIdsByTrialDebitDocIds(trialDebitDocIds);
                List<Integer> creditDebitDocMappingIds = creditDebtMappingRepository.findCreditDebitDocMappingIdsByTrialDebitDocIds(trialDebitDocIds);

                if (!creditDocumentIds.isEmpty()) {
                    creditDocRepository.updateTrialDebitdocIds(creditDocumentIds, debitDocument.getId());
                }
                if (!creditDebitDocMappingIds.isEmpty()) {
                    creditDebtMappingRepository.updateTrialDebitDocumentIds(creditDebitDocMappingIds, debitDocument.getId());
                }
                double tolerance = 0.1;
                double difference = oldTrailDebitDocAdjustmentAmount - debitDocument.getTotalamount();
                if (difference >= tolerance) {
                    creditDocRepository.updateCreditDocStatus(creditDocumentIds,Constants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                }
            }




            TrialDebitDocument finalDebitDocument = debitDocument;
            debitDocDetailsList = debitDocDetailsList.stream().peek(debitDocDetails -> debitDocDetails.setDebitdocumentid(finalDebitDocument.getId())).collect(Collectors.toList());
            trialDebitDocumentDetailRepository.saveAll(debitDocDetailsList);

//            if (customerBillingMessage.getType()!=null) {
//                if (data.containsKey("oldDebitDocId") && (customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN) || customerBillingMessage.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CANCEL_REGENERATE))) {
//                    if (data.get("oldDebitDocId").toString().contains(",")) {
//                        String[] oldDebitdocs = data.get("oldDebitDocId").toString().split(",");
//                        for (String oldDebitDocIds : oldDebitdocs) {
//                            Optional<DebitDocument> oldDebitDoc = debitDocRepository.findById(Integer.valueOf(oldDebitDocIds));
//                            if (oldDebitDoc.isPresent()){
//                                oldDebitDoc.get().setBillrunstatus(Constants.DEBIT_DOC_STATUS.CANCELLED);
//                                debitDocRepository.save(oldDebitDoc.get());
//                            }
//                        }
//                    } else {
//                        //For CAF customer
//                        Optional<DebitDocument> oldDebitDoc = null;
//                        String oldDebitDocId = data.get("oldDebitDocId").toString();
//                        if (oldDebitDocId!=null && !oldDebitDocId.equals("")) {
//                            oldDebitDoc = debitDocRepository.findById(Integer.valueOf(data.get("oldDebitDocId").toString()));
//                            if (oldDebitDoc.isPresent()){
//                                oldDebitDoc.get().setBillrunstatus(Constants.DEBIT_DOC_STATUS.CANCELLED);
//                                debitDocRepository.save(oldDebitDoc.get());
//                            }
//                        }
//                    }
//                }
//            }

            logger.info("Updating Data after Invoice creation  for Trial invoice of Customer : " + customers.getUsername() );
            updateAfterInvoiceCreatedData(customers, custPlanMapppings, Long.valueOf(finalDebitDocument.getId()), true, customerDircharges,null,null);
            //DebitDoc tax relation
            List<TrialDebitDocumentTAXRel> debitDocumentTAXRels = new ArrayList<>();
            int i=0;
            Long docDetailId=null;
            finalDebitDocument.setTrialDebitDocumentDetails(debitDocDetailsList);
            for (CustomerChargeHistory chargeHistory : customerChargeHistories) {
                if (debitDocDetailsList != null && !debitDocDetailsList.isEmpty())
                    docDetailId=debitDocDetailsList.get(i).getDebitdocdetailid().longValue();
                TrialDebitDocumentTAXRel debitDocumentTAXRel = taxService.setTaxAmountFromCharge1(finalDebitDocument, chargeHistory.getChargeId(), chargeHistory.getDiscount(), docDetailId);
                if (debitDocumentTAXRel != null) {
                    debitDocumentTAXRel.setPlanName(chargeHistory.getPlanName());
                    debitDocumentTAXRels.add(debitDocumentTAXRel);
                }
                i = i + 1;
            }
            if(!CollectionUtils.isEmpty(debitDocumentTAXRels))
                debitDocument.setTrialDebitDocumentTAXRels(debitDocumentTAXRels);

            if (isCustomerPortal) {
                logger.debug("isCustomerPortal Flag is true and Credit Doc entry is being set for Customer : " + customers.getUsername() );
                savePaymentOnline(debitDocument, customers,customerBillingMessage);
            }if(customerBillingMessage.getRecordPaymentDTO().getPaymentListPojos() != null && !isCustomerPortal) {
                savePaymentForCaf(debitDocument, customers,customerBillingMessage);
            }
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + " Customer management Service, "+"Successfully Invoice Created for Customer id :" +  custId + LogConstants.REQUEST_BY + createdByName+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return debitDocument;
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            logger.error(LogConstants.REQUEST_FROM+ " Customer management Service, "+"Error During Invoice Generation for Customer id : " +   (Integer) data.get(CustomerBillingMessage.CUST_ID) +   LogConstants.REQUEST_BY + (String) data.get(CustomerBillingMessage.CREATED_BY_NAME) +  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;

    }

    public Invoice convertDebitDocToInvoice(DebitDocument debitDocument,List<ServiceQosPojo> serviceQosPojos,List<ServiceTotalAmount> serviceTotalAmounts, Map<String, Object> invoiceData) {
        Customers customers = debitDocument.getCustomer();
        String mvnoFullName  = mvnoRepository.findFullnameBYmvnoCustId(customers.getId());
        Invoice invoice = new Invoice();
        invoice.setCustomerId(String.valueOf(customers.getId()));
        invoice.setPhone(customers.getPhone());
        invoice.setBUID(customers.getBuId());
        invoice.setTotal(debitDocument.getTotalamount());
        invoice.setTotalDue(debitDocument.getTotaldue());
        invoice.setTotalDueInWords(debitDocument.getTotaldueinwords());
        invoice.setTotalAmountInWords(debitDocument.getTotalamountinwords());
        invoice.setEmail(customers.getEmail());
        invoice.setMobile(customers.getMobile());
        invoice.setNumber(debitDocument.getDocnumber());

        invoice.setStartDate(getDatefromLocalDateTime(debitDocument.getStartdate()));
        invoice.setEndDate(getDatefromLocalDateTime(debitDocument.getEndate()));
        invoice.setDueDate(getDatefromLocalDateTime(debitDocument.getDuedate()));

        invoice.setCustomerInformation(convertCustomerToSubscriber(customers));
        invoice.setBillDate(getDatefromLocalDateTime(debitDocument.getBilldate()));
        invoice.setCharge(debitDocument.getSubtotal());
        invoice.setBillrunStatus(debitDocument.getBillrunstatus());
        invoice.setCreatebyname(debitDocument.getCreatedByName());
        invoice.setCreateDate(getDatefromLocalDateTime(debitDocument.getCreatedate()));
        invoice.setDiscount(debitDocument.getDiscount());
        invoice.setEmail(customers.getEmail());
        invoice.setTotalDueInWords(debitDocument.getTotaldueinwords());
        invoice.setTax(debitDocument.getTax());
        invoice.setTaxList(getTaxListFromDebitDoc(debitDocument));
        invoice.setInvoiceList(getInvoiceDetailFromDebitDoc(debitDocument));
        invoice.setChargeDetails(getchargeDetailFromDebitDoc(debitDocument));
        invoice.setPlanInformation(getPlanInformationFromDebitDoc(debitDocument));
        invoice.setAddressDetail(getCustomerAddressDetail(customers));
        invoice.setServiceQosPojos(serviceQosPojos);
        invoice.setServiceTotalAmounts(serviceTotalAmounts);
        invoice.setFullName(mvnoFullName);
        if (customers.getPan()!=null) {
            invoice.setPan(customers.getPan());
        }
        if(debitDocument.getQrCode() != null && !debitDocument.getQrCode().isEmpty()){
            invoice.setVerifyQr(debitDocument.getQrCode());
        }
        setKraReceiptDetails(invoice, debitDocument);
        invoice.setPreviousWalletBalance(debitDocument.getPreviousbalance() != null ? -(debitDocument.getPreviousbalance()) : 0);
        if(invoiceData.get("billingCycle") != null){
            invoice.setBillingCycleInMonths((Integer) invoiceData.get("billingCycle"));
        }
        if(invoiceData.get("customerActivationDate") != null){
            invoice.setCustomerActivationDate(Date.from(((LocalDate) invoiceData.get("customerActivationDate")).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if(debitDocument.getCustomer().getGraceDay() != null && debitDocument.getDuedate() != null){
            LocalDateTime dueDate = debitDocument.getDuedate();
            Integer graceDays = debitDocument.getCustomer().getGraceDay();
            LocalDateTime gracePeriodDateTime = dueDate.plusDays(graceDays);
            Date gracePeriodEndDate = Date.from(gracePeriodDateTime.atZone(ZoneId.systemDefault()).toInstant());
            invoice.setGracePeriodEndDate(gracePeriodEndDate);
        }
        if(invoiceData.get("dueAmountFromLastInvoice") != null){
            invoice.setDueAmountFromLastInvoice((double) invoiceData.get("dueAmountFromLastInvoice"));
        }
        if(invoiceData.get("validity") != null) {
            invoice.setPlanValidityInDays((Integer) invoiceData.get("validity"));
        }
        if(invoiceData.get("planType") != null) {
            invoice.setSubscriptionPlanType((String) invoiceData.get("planType"));
        }
        if(customers.getCurrency() != null){
            invoice.setCustomerCurrency(customers.getCurrency());
        } else {
            String currency = clientServiceRepository.findValueByNameAndMvnoId("CURRENCY_FOR_PAYMENT", customers.getMvnoId());
            if(currency != null){
                invoice.setCustomerCurrency(currency);
            }
        }
        return invoice;
    }

    private void setKraReceiptDetails(Invoice invoice, DebitDocument debitDocument) {
        invoice.setKraInvoiceId(valueOrEmpty(debitDocument.getKraInvoiceId()));
        invoice.setCurRecptNo(valueOrEmpty(debitDocument.getCurRecptNo()));
        invoice.setTotRecptNo(valueOrEmpty(debitDocument.getTotRecptNo()));
        invoice.setScuInternalData(valueOrEmpty(debitDocument.getScuInternalData()));
        invoice.setScuReceiptSignature(valueOrEmpty(debitDocument.getScuReceiptSignature()));
        invoice.setSdcid(valueOrEmpty(debitDocument.getSdcid()));
        invoice.setSdcmrcNo(valueOrEmpty(debitDocument.getSdcmrcNo()));
        invoice.setSdcDateTime(valueOrEmpty(debitDocument.getSdcDateTime()));
        invoice.setIsStockIO(valueOrEmpty(debitDocument.getIsStockIO()));
    }

    private String valueOrEmpty(Object value) {
        return value == null ? "" : value.toString();
    }

    private List<ChargeDetails> getchargeDetailFromDebitDoc(DebitDocument debitDocument) {
        ArrayList<ChargeDetails> chargeDetails = new ArrayList<>();
        try {
            List<DebitDocDetails> debitDocDetails = debitDocument.getDebitDocDetailsList();

            if (!CollectionUtils.isEmpty(debitDocDetails)) {
                Map<String, ChargeDetails> totalAmountsAndTaxesByChargeType = debitDocDetails.stream()
                        .collect(Collectors.groupingBy(
                                DebitDocDetails::getChargetype,
                                Collectors.collectingAndThen(
                                        Collectors.reducing(new ChargeDetails(0.0, 0.0, 0.0),
                                                d -> new ChargeDetails(d.getTax(), d.getTotalamount(), d.getSubtotal()),
                                                (a, b) -> new ChargeDetails(a.getTax() + b.getTax(), a.getTotal() + b.getTotal(), a.getPrice() + b.getPrice())),
                                        sum -> sum
                                )
                        ));

                for (Map.Entry<String, ChargeDetails> entry : totalAmountsAndTaxesByChargeType.entrySet()) {
                    ChargeDetails chargeDetail = entry.getValue();
                    chargeDetail.setChargeType(entry.getKey());
                    chargeDetails.add(chargeDetail);
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  chargeDetails;
    }

    public Invoice convertPartnerDebitDocToInvoice(PartnerDebitDocument debitDocument,Partner parentPartner,List<PartnerLedgerDetails> commissionList,List<PartnerLedgerDetails> revertCommissionList,List<PartnerLedgerDetails> transfercommissionList,Double transferCreditCommissionAmount,Double transferDebitCommissionAmount) {
        Partner partner = debitDocument.getPartner();
        Invoice invoice = new Invoice();
        invoice.setCustomerId(String.valueOf(partner.getId()));
        invoice.setPhone(partner.getMobile());
        invoice.setBUID(partner.getBuId());
        invoice.setTotal(Double.parseDouble(new DecimalFormat("##.##").format(debitDocument.getTotalamount())));
        invoice.setTotalDue(debitDocument.getTotaldue());
        invoice.setTotalDueInWords(debitDocument.getDueinwords());
        invoice.setTotalAmountInWords(debitDocument.getAmountinwords());
        invoice.setEmail(partner.getEmail());
        invoice.setMobile(partner.getMobile());
        invoice.setInvoiceNumber(debitDocument.getDocnumber());
        invoice.setNumber(debitDocument.getDocnumber());

        invoice.setStartDate(getDatefromLocalDateTime(debitDocument.getStartdate()));
        invoice.setEndDate(getDatefromLocalDateTime(debitDocument.getEndate()));
        invoice.setDueDate(getDatefromLocalDateTime(debitDocument.getEndate()));

        invoice.setCustomerInformation(convertPartnerToSubscriber(partner));
        invoice.setBillDate(getDatefromLocalDateTime(debitDocument.getBilldate()));
        invoice.setCharge(debitDocument.getSubtotal());
        invoice.setBillrunStatus(debitDocument.getBillrunstatus());
        invoice.setCreatebyname(null);
        invoice.setCreateDate(getDatefromLocalDateTime(debitDocument.getCreatedate()));
        invoice.setDiscount(debitDocument.getDiscount());
        invoice.setEmail(partner.getEmail());
        invoice.setTotalDueInWords(debitDocument.getDueinwords());
        invoice.setTax(debitDocument.getTax());
        invoice.setTaxList(null);
        invoice.setInvoiceList(null);
        invoice.setPlanInformation(getPlanInformationFromPartnerLedger(commissionList,revertCommissionList));
        invoice.setPartnerPlanCommissionDetail(getPartnerPlanInformationFromPartnerLedger(commissionList,revertCommissionList));
        invoice.setAddressDetail(null);
        if(debitDocument.getPartnerTax()!=null)
            invoice.setPartnerTax(Double.parseDouble(new DecimalFormat("##.##").format(debitDocument.getPartnerTax())));
        if(debitDocument.getTds()!=null)
            invoice.setTds(Double.parseDouble(new DecimalFormat("##.##").format(debitDocument.getTds())));
        invoice.setOperatorName(debitDocument.getToOperatorName());
        if(parentPartner!=null) {
            invoice.setAddressDetail(getPartnerAddressDetail(parentPartner));
            invoice.setOperatorAddress(getOperatorAddressDetail(parentPartner, invoice.getOperatorName()));
        }
        else
            invoice.setOperatorAddress(getOperatorAddressDetail(parentPartner, invoice.getOperatorName()));


        if(partner!=null)
            invoice.setChildPartnerAddress(getChildPartnerAddressDetail(partner));
        return invoice;
    }

    private ArrayList<SubscriberAddress> getCustomerAddressDetail(Customers customers) {
        CustomerAddress customerAddress= customerAddressRepository.findByAddressTypeAndCustomerAndVersion("Present",customers,"NEW");
        ArrayList<SubscriberAddress> list=new ArrayList<>();
        if(customerAddress!=null)
        {
            SubscriberAddress address=new SubscriberAddress();
            if(customerAddress.getPincode()!=null)
                address.setPincode(customerAddress.getPincode().getPincode());
            if(customerAddress.getCity()!=null)
                address.setCity(customerAddress.getCity().getName());
            if(customerAddress.getState()!=null)
                address.setState(customerAddress.getState().getName());
            if(customerAddress.getCountry()!=null)
                address.setCountry(customerAddress.getCountry().getName());
            if(customerAddress.getArea()!=null)
                address.setArea(customerAddress.getArea().getName());
            address.setLandmark(customerAddress.getLandmark());
            address.setAddress1(customerAddress.getAddress1());
            address.setAddress2(customerAddress.getAddress2());
            list.add(address);
        }
        return list;
    }


    private ArrayList<SubscriberAddress> getPartnerAddressDetail(Partner partner) {
        ArrayList<SubscriberAddress> list=new ArrayList<>();
        SubscriberAddress address=new SubscriberAddress();
        if(partner.getCountry()!=null) {
            Country country = countryRepository.findById(partner.getCountry()).orElse(null);
            if (country != null) address.setCountry(country.getName());
        }

        if(partner.getCountry()!=null) {
            State state = stateRepository.findById(partner.getState()).orElse(null);
            if (state != null) address.setState(state.getName());
        }

        if(partner.getCountry()!=null) {
            Pincode pincode = pincodeRepository.findById(Long.valueOf(partner.getPincode())).orElse(null);
            if (pincode != null) address.setPincode(pincode.getPincode());
        }

        if(partner.getCountry()!=null) {
            City city = cityRepository.findById(partner.getCity()).orElse(null);
            if (city != null) address.setCity(city.getName());
        }
        address.setAddress1("");
        address.setAddress2("");
        list.add(address);
        return list;
    }

    private OperatorAddress getOperatorAddressDetail(Partner partner, String operatorName) {
        OperatorAddress address=new OperatorAddress();
        if(partner!=null)
        {
            if(partner.getCountry()!=null) {
                Country country = countryRepository.findById(partner.getCountry()).orElse(null);
                if (country != null) address.setCountry(country.getName());
            }

            if(partner.getCountry()!=null) {
                State state = stateRepository.findById(partner.getState()).orElse(null);
                if (state != null) address.setState(state.getName());
            }

            if(partner.getCountry()!=null) {
                Pincode pincode = pincodeRepository.findById(Long.valueOf(partner.getPincode())).orElse(null);
                if (pincode != null) address.setPinCode(pincode.getPincode());
            }

            if(partner.getCountry()!=null) {
                City city = cityRepository.findById(partner.getCity()).orElse(null);
                if (city != null) address.setCity(city.getName());
            }
            address.setAddress1("");
        }
        else {
            address.setOperatorName(operatorName);
            address.setCountry("");
            address.setState("");
            address.setCity("");
            address.setPinCode("");
            address.setAddress1("");
        }

        return address;
    }

    private ChildPartnerAddress getChildPartnerAddressDetail(Partner partner) {
        ChildPartnerAddress address=new ChildPartnerAddress();
        if(partner!=null)
        {
            address.setChildPartnerName(partner.getName());
            if(partner.getCountry()!=null) {
                Country country = countryRepository.findById(partner.getCountry()).orElse(null);
                if (country != null) address.setCountry(country.getName());
            }

            if(partner.getCountry()!=null) {
                State state = stateRepository.findById(partner.getState()).orElse(null);
                if (state != null) address.setState(state.getName());
            }

            if(partner.getCountry()!=null) {
                Pincode pincode = pincodeRepository.findById(Long.valueOf(partner.getPincode())).orElse(null);
                if (pincode != null) address.setPinCode(pincode.getPincode());
            }

            if(partner.getCountry()!=null) {
                City city = cityRepository.findById(partner.getCity()).orElse(null);
                if (city != null) address.setCity(city.getName());
            }
            address.setAddress1("");
        }
        return address;
    }

    private List<ChargeDetails> getchargeDetailFromTrialDebitDoc(TrialDebitDocument debitDocument) {
        ArrayList<ChargeDetails> chargeDetails = new ArrayList<>();
        try {
            List<TrialDebitDocumentDetail> debitDocDetails = debitDocument.getTrialDebitDocumentDetails();

            if (!CollectionUtils.isEmpty(debitDocDetails)) {
                Map<String, ChargeDetails> totalAmountsAndTaxesByChargeType = debitDocDetails.stream()
                        .collect(Collectors.groupingBy(
                                TrialDebitDocumentDetail::getChargetype,
                                Collectors.collectingAndThen(
                                        Collectors.reducing(new ChargeDetails(0.0, 0.0, 0.0),
                                                d -> new ChargeDetails(d.getTax(), d.getTotalamount(), d.getSubtotal()),
                                                (a, b) -> new ChargeDetails(a.getTax() + b.getTax(), a.getTotal() + b.getTotal(), a.getPrice() + b.getPrice())),
                                        sum -> sum
                                )
                        ));

                for (Map.Entry<String, ChargeDetails> entry : totalAmountsAndTaxesByChargeType.entrySet()) {
                    ChargeDetails chargeDetail = entry.getValue();
                    chargeDetail.setChargeType(entry.getKey());
                    chargeDetails.add(chargeDetail);
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  chargeDetails;
    }



    public Invoice convertDebitDocToInvoice(TrialDebitDocument debitDocument,List<TrialDebitDocumentDetail> details, Map<String, Object> invoiceData) {
        Customers customers = debitDocument.getCustomer();
        Invoice invoice = new Invoice();
        invoice.setCustomerId(String.valueOf(customers.getId()));
        invoice.setPhone(customers.getPhone());
        invoice.setBUID(customers.getBuId());
        invoice.setTotal(debitDocument.getTotalamount());
        invoice.setTotalDue(debitDocument.getTotaldue());
        invoice.setTotalDueInWords(debitDocument.getDueinwords());
        invoice.setTotalAmountInWords(debitDocument.getAmountinwords());
        invoice.setEmail(customers.getEmail());
        invoice.setMobile(customers.getMobile());
        invoice.setNumber(debitDocument.getDocnumber());

        invoice.setStartDate(getDatefromLocalDateTime(debitDocument.getStartdate()));
        invoice.setEndDate(getDatefromLocalDateTime(debitDocument.getEndate()));
        invoice.setDueDate(getDatefromLocalDateTime(debitDocument.getEndate()));

        invoice.setCustomerInformation(convertCustomerToSubscriber(customers));
        invoice.setBillDate(getDatefromLocalDateTime(debitDocument.getBilldate()));
        invoice.setCharge(debitDocument.getSubtotal());
        invoice.setBillrunStatus(debitDocument.getBillrunstatus());
        invoice.setCreatebyname(debitDocument.getCreatedByName());
        invoice.setCreateDate(getDatefromLocalDateTime(debitDocument.getCreatedate()));
        invoice.setDiscount(debitDocument.getDiscount());
        invoice.setEmail(customers.getEmail());
        invoice.setTotalDueInWords(debitDocument.getDueinwords());
        invoice.setTax(debitDocument.getTax());
        invoice.setPlanInformation(getPlanInformationFromTrialDebitDoc(debitDocument));
        invoice.setChargeDetails(getchargeDetailFromTrialDebitDoc(debitDocument));
        invoice.setTaxList(getTaxListFromDebitDoc(debitDocument));
        invoice.setInvoiceList(getInvoiceDetailFromDebitDoc(debitDocument,details));
        invoice.setAddressDetail(getCustomerAddressDetail(customers));
        invoice.setPreviousWalletBalance(debitDocument.getPreviousbalance());
        if(invoiceData.get("billingCycle") != null){
            invoice.setBillingCycleInMonths((Integer) invoiceData.get("billingCycle"));
        }
        if(invoiceData.get("customerActivationDate") != null){
            invoice.setCustomerActivationDate(Date.from(((LocalDate) invoiceData.get("customerActivationDate")).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if(debitDocument.getCustomer().getGraceDay() != null && debitDocument.getDuedate() != null){
            LocalDateTime dueDate = debitDocument.getDuedate();
            Integer graceDays = debitDocument.getCustomer().getGraceDay();
            LocalDateTime gracePeriodDateTime = dueDate.plusDays(graceDays);
            Date gracePeriodEndDate = Date.from(gracePeriodDateTime.atZone(ZoneId.systemDefault()).toInstant());
            invoice.setGracePeriodEndDate(gracePeriodEndDate);
        }
        if(invoiceData.get("dueAmountFromLastInvoice") != null){
            invoice.setDueAmountFromLastInvoice((double) invoiceData.get("dueAmountFromLastInvoice"));
        }
        if(invoiceData.get("validity") != null) {
            invoice.setPlanValidityInDays((Integer) invoiceData.get("validity"));
        }
        if(invoiceData.get("planType") != null) {
            invoice.setSubscriptionPlanType((String) invoiceData.get("planType"));
        }
        if(customers.getCurrency() != null){
            invoice.setCustomerCurrency(customers.getCurrency());
        } else {
            String currency = clientServiceRepository.findValueByNameAndMvnoId("CURRENCY_FOR_PAYMENT", customers.getMvnoId());
            if(currency != null){
                invoice.setCustomerCurrency(currency);
            }
        }
        return invoice;
    }

    public ArrayList<InvoiceDetail> getInvoiceDetailFromDebitDoc(DebitDocument debitDocument) {
        List<DebitDocDetails> debitDocDetails = debitDocument.getDebitDocDetailsList();
        if(debitDocDetails ==null || CollectionUtils.isEmpty(debitDocDetails)) {
            debitDocDetails = debitDocDetailRepository.findAllByDebitdocumentid(debitDocument.getId());
            debitDocument.setDebitDocDetailsList(debitDocDetails);
        }
        Customers customers = debitDocument.getCustomer();
        ArrayList<InvoiceDetail> invoiceDetails = new ArrayList<>();
        for (DebitDocDetails docDetails : debitDocDetails) {
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            invoiceDetail.setCycle(docDetails.getNoofcycle() + "");
            invoiceDetail.setDescription(docDetails.getDescription());
            invoiceDetail.setDiscount(docDetails.getDiscount());
            invoiceDetail.setEndDate(new Date(getDatefromLocalDateTime(customers.getNextBillDate().atStartOfDay()).getTime() - 1 * 24 * 3600 * 1000));
            invoiceDetail.setInvoiceId(String.valueOf(debitDocument.getId()));
            invoiceDetail.setItemChargeId(String.valueOf(docDetails.getChargeid()));
            invoiceDetail.setName(docDetails.getChargename());
            invoiceDetail.setNoOfCycle(-1);
            invoiceDetail.setPrice(docDetails.getSubtotal());
            invoiceDetail.setProrationType("F");
            invoiceDetail.setCreatedByname(debitDocument.getCreatedByName());
            invoiceDetail.setUpdateByName(debitDocument.getCreatedByName());

            if (docDetails.getCustServiceId() != null)
                invoiceDetail.setCustServiceId(docDetails.getCustServiceId());
            if (docDetails.getServiceId() != null)
                invoiceDetail.setServiceId(docDetails.getServiceId());

            if (customers.getLastBillDate() != null)
                invoiceDetail.setStartDate(getDatefromLocalDateTime(customers.getLastBillDate().atStartOfDay()));
            else
                invoiceDetail.setStartDate(getDatefromLocalDateTime(debitDocument.getStartdate()));

            invoiceDetail.setTax(docDetails.getTax());
            invoiceDetail.setType(docDetails.getChargetype());
            invoiceDetail.setPlanId(docDetails.getPlanId());
            if (docDetails.getDiscount() != null) {
                invoiceDetail.setCustomerDiscount(docDetails.getDiscount());
                invoiceDetail.setDiscount(docDetails.getDiscount());
            } else {
                invoiceDetail.setCustomerDiscount(0d);
                invoiceDetail.setDiscount(0d);
            }
            invoiceDetail.setTotal(round(docDetails.getSubtotal() + docDetails.getTax() - invoiceDetail.getDiscount(), 2));
            invoiceDetails.add(invoiceDetail);
        }

        return invoiceDetails;
    }



    public ArrayList<InvoiceDetail> getInvoiceDetailFromDebitDoc(TrialDebitDocument debitDocument,List<TrialDebitDocumentDetail> debitDocumentDetails) {
        List<TrialDebitDocumentDetail> debitDocDetails = debitDocumentDetails;
        Customers customers = debitDocument.getCustomer();
        ArrayList<InvoiceDetail> invoiceDetails = new ArrayList<>();
        for (TrialDebitDocumentDetail docDetails : debitDocDetails) {
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            invoiceDetail.setCycle(docDetails.getNoofcycle() + "");
            invoiceDetail.setDescription(docDetails.getDescription());
            if(docDetails.getDiscount()!=null)
                invoiceDetail.setDiscount(docDetails.getDiscount());
            else
                invoiceDetail.setDiscount(0l);
            invoiceDetail.setEndDate(new Date(getDatefromLocalDateTime(customers.getNextBillDate().atStartOfDay()).getTime() - 1 * 24 * 3600 * 1000));
            invoiceDetail.setInvoiceId(String.valueOf(debitDocument.getId()));
            invoiceDetail.setItemChargeId(String.valueOf(docDetails.getChargeid()));
            invoiceDetail.setName(docDetails.getChargename());
            invoiceDetail.setNoOfCycle(-1);
            invoiceDetail.setPrice(docDetails.getSubtotal());
            invoiceDetail.setProrationType("F");
            invoiceDetail.setCreatedByname(debitDocument.getCreatedByName());
            invoiceDetail.setUpdateByName(debitDocument.getCreatedByName());

            //if (docDetails.getCustServiceId() != null)
            //invoiceDetail.setCustServiceId(docDetails.getCustServiceId());
            //if (docDetails.getServiceId() != null)
            //invoiceDetail.setServiceId(docDetails.getServiceId());

            if (customers.getLastBillDate() != null)
                invoiceDetail.setStartDate(getDatefromLocalDateTime(customers.getLastBillDate().atStartOfDay()));
            else
                invoiceDetail.setStartDate(getDatefromLocalDateTime(debitDocument.getStartdate()));

            invoiceDetail.setTax(docDetails.getTax());
            invoiceDetail.setType(docDetails.getChargetype());
            //invoiceDetail.setPlanId(docDetails.getPlanId());
            if (docDetails.getDiscount() != null) {
                invoiceDetail.setCustomerDiscount(docDetails.getDiscount());
                invoiceDetail.setDiscount(docDetails.getDiscount());
            } else {
                invoiceDetail.setCustomerDiscount(0d);
                invoiceDetail.setDiscount(0d);
            }
            invoiceDetail.setTotal(round(docDetails.getSubtotal() + docDetails.getTax() - invoiceDetail.getDiscount(), 2));
            invoiceDetails.add(invoiceDetail);
        }

        return invoiceDetails;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public Subscriber convertCustomerToSubscriber(Customers customers) {
        Subscriber subscriber = new Subscriber();
        subscriber.setAccountnumber(customers.getAcctno());
        subscriber.setFirstname(customers.getFirstname());
        subscriber.setUserName(customers.getUsername());
        subscriber.setEmail(customers.getEmail());
        subscriber.setPan(customers.getPan());
        subscriber.setMobile(customers.getMobile());
        subscriber.setPhone(customers.getPhone());
        subscriber.setLastname(customers.getLastname());
        subscriber.setCountry(customers.getCountryCode());
        subscriber.setName(customers.getFullName());
        return subscriber;
    }

    public Subscriber convertPartnerToSubscriber(Partner partner) {
        Subscriber subscriber = new Subscriber();
        subscriber.setFirstname(partner.getName());
        subscriber.setUserName(partner.getEmail());
        subscriber.setEmail(partner.getEmail());
        subscriber.setMobile(partner.getMobile());
        subscriber.setPhone(partner.getMobile());
        subscriber.setCountry(partner.getCountryCode());
        subscriber.setName(partner.getName());
        return subscriber;
    }



    public ArrayList<PlanInformation> getPlanInformationFromPartnerLedger(List<PartnerLedgerDetails> commissionList,List<PartnerLedgerDetails> revertCommissionList) {
        ArrayList<PlanInformation> planInformations = new ArrayList<>();
        Set<String> planIds = commissionList.stream().filter(commission -> commission.getPlanid() != null && commission.getPlanGroupId()==null).map(PartnerLedgerDetails::getPlanid).distinct().collect(Collectors.toSet());
        Set<Integer> planGroupIds = commissionList.stream().filter(commission -> commission.getPlanGroupId()!=null).map(PartnerLedgerDetails::getPlanGroupId).collect(Collectors.toSet());

        for (String planId : planIds) {
            List<PartnerLedgerDetails> commissionList1 = commissionList.stream().filter(commission -> commission.getPlanid() != null && commission.getPlanGroupId()==null && commission.getPlanid().equalsIgnoreCase(planId)).collect(Collectors.toList());
            List<PartnerLedgerDetails> revertCommissionList1 = commissionList.stream().filter(commission -> commission.getPlanid() != null && commission.getPlanGroupId()==null && commission.getPlanid().equalsIgnoreCase(planId)).collect(Collectors.toList());
            List<String> debitDocumentId=commissionList.stream().filter(commission -> commission.getPlanid() != null && commission.getPlanGroupId()==null && commission.getPlanid().equalsIgnoreCase(planId)).map(x->x.getInvoiceNo()).distinct().collect(Collectors.toList());
            Double reverseCommissionAmount=revertCommissionList1.stream().filter(commission->commission.getInvoiceNo()!=null && commission.getInvoiceNo().equalsIgnoreCase(debitDocumentId.get(0))).mapToDouble(x->x.getAmount() + x.getCommission()).sum();

            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(Integer.valueOf(planId));
            PlanInformation planInformation = new PlanInformation();
            planInformation.setDescription(postpaidPlan.get().getDesc());
            planInformation.setDisplayname(postpaidPlan.get().getDisplayName());
            planInformation.setName(postpaidPlan.get().getName());
            planInformation.setPlanGroupName(null);
            planInformation.setStatus(postpaidPlan.get().getStatus());
            planInformation.setTotalCommissionAmount(commissionList1.stream().filter(x->x.getCommission()!=null).mapToDouble(x->x.getCommission()).sum() - reverseCommissionAmount);
            planInformation.setCustomerCount(commissionList1.stream().filter(x->x.getCustid()!=null).distinct().count());
            planInformations.add(planInformation);
        }

        for (Integer planGroupId : planGroupIds) {
            List<PartnerLedgerDetails> commissionList1 = commissionList.stream().filter(commission -> commission.getPlanGroupId()!=null && commission.getPlanGroupId().equals(planGroupId)).collect(Collectors.toList());
            List<PartnerLedgerDetails> revertCommissionList1 = commissionList.stream().filter(commission -> commission.getPlanGroupId()!=null && commission.getPlanGroupId().equals(planGroupId)).collect(Collectors.toList());
            List<String> debitDocumentId=commissionList.stream().filter(commission -> commission.getPlanGroupId()!=null && commission.getPlanGroupId().equals(planGroupId)).map(x->x.getInvoiceNo()).distinct().collect(Collectors.toList());
            Double reverseCommissionAmount=revertCommissionList1.stream().filter(commission->commission.getInvoiceNo()!=null && commission.getInvoiceNo().equalsIgnoreCase(debitDocumentId.get(0))).mapToDouble(x->x.getAmount() + x.getCommission()).sum();

            PlanInformation planInformation = new PlanInformation();
            planInformation.setDescription(commissionList1.get(0).getPlanGroupName());
            planInformation.setDisplayname(commissionList1.get(0).getPlanGroupName());
            planInformation.setName(commissionList1.get(0).getPlanGroupName());
            planInformation.setPlanGroupName(commissionList1.get(0).getPlanGroupName());
            planInformation.setStatus(null);
            planInformation.setTotalCommissionAmount(commissionList1.stream().filter(x->x.getCommission()!=null).mapToDouble(x->x.getCommission()).sum() - reverseCommissionAmount);
            planInformation.setCustomerCount(commissionList1.stream().filter(x->x.getCustid()!=null).distinct().count());
            planInformations.add(planInformation);
        }
        return planInformations;
    }

    public ArrayList<PlanInformation> getPlanInformationFromDebitDoc(DebitDocument debitDocument) {
        ArrayList<PlanInformation> planInformations = new ArrayList<>();
        Set<String> planIds = debitDocument.getDebitDocDetailsList().stream().filter(docDetails -> docDetails.getPlanId() != null).map(DebitDocDetails::getPlanId).collect(Collectors.toSet());
        for (String planId : planIds) {
            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(Integer.valueOf(planId));
            PlanInformation planInformation = new PlanInformation();
            planInformation.setCreatedate(getDatefromLocalDateTime(debitDocument.getCreatedate()));
            planInformation.setEnddate(getDatefromLocalDateTime(debitDocument.getEndate()));
            planInformation.setDescription(postpaidPlan.get().getDesc());
            planInformation.setDisplayname(postpaidPlan.get().getDisplayName());
            planInformation.setName(postpaidPlan.get().getName());
            planInformation.setPlanGroupName(postpaidPlan.get().getPlanGroup());
            planInformation.setStatus(postpaidPlan.get().getStatus());
            planInformations.add(planInformation);
        }
        return planInformations;
    }

    public ArrayList<PlanInformation> getPlanInformationFromTrialDebitDoc(TrialDebitDocument debitDocument) {
        ArrayList<PlanInformation> planInformations = new ArrayList<>();
        Set<Integer> planIds = debitDocument.getTrialDebitDocumentDetails().stream().filter(docDetails -> docDetails.getPlanId() != null).map(TrialDebitDocumentDetail::getPlanId).collect(Collectors.toSet());
        for (Integer planId : planIds) {
            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(planId);
            PlanInformation planInformation = new PlanInformation();
            planInformation.setCreatedate(getDatefromLocalDateTime(debitDocument.getCreatedate()));
            planInformation.setEnddate(getDatefromLocalDateTime(debitDocument.getEndate()));
            planInformation.setDescription(postpaidPlan.get().getDesc());
            planInformation.setDisplayname(postpaidPlan.get().getDisplayName());
            planInformation.setName(postpaidPlan.get().getName());
            planInformation.setPlanGroupName(postpaidPlan.get().getPlanGroup());
            planInformation.setStatus(postpaidPlan.get().getStatus());
            if (postpaidPlan.get().getSaccode()!=null){
                planInformation.setSac(postpaidPlan.get().getSaccode());
            }
            if (postpaidPlan.get().getValidity()!=null){
                planInformation.setValidity(postpaidPlan.get().getValidity());
            }
            planInformations.add(planInformation);
        }
        return planInformations;
    }

    public ArrayList<TaxDto> getTaxListFromDebitDoc(DebitDocument debitDocument) {
        List<DebitDocumentTAXRel> debitDocumentTAXRels = debitDocumentTAXRelRepository.findAllByDebitdocumentid(debitDocument.getId());//debitDocument.getDebitDocumentTAXRels();
        Set<String> taxnames = debitDocumentTAXRels.stream().map(DebitDocumentTAXRel::getTaxname).collect(Collectors.toSet());
        ArrayList<TaxDto> taxes = new ArrayList<>();
        for (String taxname : taxnames) {
            DebitDocumentTAXRel documentTAXRel = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).findFirst().get();
            Double amount = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).mapToDouble(DebitDocumentTAXRel::getAmount).sum();
            TaxDto taxDto = new TaxDto();
            taxDto.setInvoiceId(String.valueOf(debitDocument.getId()));
            taxDto.setName(taxname);
            taxDto.setChargeid(String.valueOf(documentTAXRel.getChargeid()));
            taxDto.setEndDate(getDatefromLocalDateTime(documentTAXRel.getEnddate()));
            taxDto.setLevel(documentTAXRel.getTaxlevel().intValue());
            taxDto.setChargeid(String.valueOf(documentTAXRel.getChargeid()));
            taxDto.setTaxAmount(documentTAXRel.getAmount());
            taxDto.setStartDate(getDatefromLocalDateTime(documentTAXRel.getStartdate()));
            taxDto.setPercentage(documentTAXRel.getPercentage());
            taxDto.setDescription(documentTAXRel.getDescription());
            taxes.add(taxDto);
        }
        return taxes;
    }


    public ArrayList<TaxDto> getTaxListFromDebitDoc(TrialDebitDocument debitDocument) {
        List<TrialDebitDocumentTAXRel> debitDocumentTAXRels = trialDebitDocumentTAXRelRepository.findAllByTrialdebitdocumentid(debitDocument.getId());//debitDocument.getDebitDocumentTAXRels();
        Set<String> taxnames = debitDocumentTAXRels.stream().map(TrialDebitDocumentTAXRel::getTaxname).collect(Collectors.toSet());
        ArrayList<TaxDto> taxes = new ArrayList<>();
        for (String taxname : taxnames) {
            TrialDebitDocumentTAXRel documentTAXRel = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).findFirst().get();
            Double amount = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).mapToDouble(TrialDebitDocumentTAXRel::getAmount).sum();
            TaxDto taxDto = new TaxDto();
            taxDto.setInvoiceId(String.valueOf(debitDocument.getId()));
            taxDto.setName(taxname);
            taxDto.setChargeid(String.valueOf(documentTAXRel.getChargeid()));
            taxDto.setEndDate(getDatefromLocalDateTime(documentTAXRel.getEnddate()));
            taxDto.setLevel(documentTAXRel.getTaxlevel().intValue());
            taxDto.setChargeid(String.valueOf(documentTAXRel.getChargeid()));
            taxDto.setTaxAmount(documentTAXRel.getAmount());
            taxDto.setStartDate(getDatefromLocalDateTime(documentTAXRel.getStartdate()));
            taxDto.setPercentage(documentTAXRel.getPercentage());
            taxDto.setDescription(documentTAXRel.getDescription());
            taxes.add(taxDto);
        }
        return taxes;
    }

    public Date getDatefromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
        return new Date();
    }


    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.DebitDocument', '1')")
    public Page<DebitDocSearchPojo> searchInvoice(SearchDebitDocsPojo searchDebitDocsPojo, PaginationRequestDTO paginationDTO, boolean isInvoiceVoid, boolean isFromCustomerPortal, boolean isOutstandingDue,Boolean isKraSynced) {
        pageRequest = generatePageRequest(paginationDTO.getPage(), paginationDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
        LocalDateTime startDate=null;
        LocalDateTime endDate=null;
        if(searchDebitDocsPojo.getBillfromdate()!=null){
            startDate = searchDebitDocsPojo.getBillfromdate().atTime(00, 00, 00);
        }
        if(searchDebitDocsPojo.getBilltodate()!=null){
            endDate = searchDebitDocsPojo.getBilltodate().atTime(23, 59, 59);
        }
        LocalDate todaysDate = LocalDate.now();
        Page<DebitDocSearchPojo> debitDocSearchPojos = null;
        String staffName = null;
        StaffUser staff = new StaffUser();
        Customers customers = new Customers();
        if (searchDebitDocsPojo.getCustomerid() != null && searchDebitDocsPojo.getCustomerid() != 0) {
            customers = customersRepository.findCustomerById(searchDebitDocsPojo.getCustomerid());
        }

        if (searchDebitDocsPojo.getStaffId() != null) {
            staff = staffUserRepository.findById(searchDebitDocsPojo.getStaffId()).orElse(null);
            if (staff != null) {
                staffName = staff.getFirstname();
            }
        }
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Integer mvnoId= ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();

        try {
            String queryForDebitDocument=null;
            String countQueryDebitDocument=null;
            //JOIN PostpaidPlan  plan ON plan.id = deb.postpaidPlan
            if(mvnoId == 1) {

                queryForDebitDocument = "SELECT deb FROM DebitDocument deb  JOIN Customers cust ON deb.customer=cust WHERE deb.isDelete = false ";
                countQueryDebitDocument = "SELECT count(*) FROM DebitDocument deb  JOIN Customers cust ON deb.customer=cust.id WHERE deb.isDelete = false ";
            }else {
                queryForDebitDocument = "SELECT deb FROM DebitDocument deb  JOIN Customers cust ON deb.customer=cust WHERE deb.isDelete = false AND cust.mvnoId =:mvnoId";
                countQueryDebitDocument = "SELECT count(*) FROM DebitDocument deb  JOIN Customers cust ON deb.customer=cust.id WHERE deb.isDelete = false AND cust.mvnoId =:mvnoId";
            }

            if (getLoggedInUser().getLco()!= null && getLoggedInUser().getLco()) {
                queryForDebitDocument += " AND (deb.lcoId=" + getLoggedInUser().getPartnerId() + ")";
                countQueryDebitDocument += " AND (deb.lcoId=" + getLoggedInUser().getPartnerId() + ")";
            } else {
                queryForDebitDocument += " AND (deb.lcoId IS NULL)";
                countQueryDebitDocument += " AND (deb.lcoId IS NULL)";
            }
//            if (getLoggedInUser().getLco()) exp = exp.and(qDebitDocument.lcoId.eq(getLoggedInUser().getPartnerId()));
//            else exp = exp.and(qDebitDocument.lcoId.isNull());

            if (searchDebitDocsPojo.getCustomerid() != null && (searchDebitDocsPojo.getCustomerid().equals(1) || searchDebitDocsPojo.getCustomerid().equals(2))) {
                if (isInvoiceVoid) {
                    //  exp = exp.and(qDebitDocument.billrunstatus.notEqualsIgnoreCase("VOID"));
                    queryForDebitDocument += " AND (deb.billrunstatus !=" + "'VOID'" + ")";
                    countQueryDebitDocument += " AND (deb.billrunstatus !=" + "'VOID'" + ")";
                }

                if (searchDebitDocsPojo.getServiceAreaId() != null) {
                    queryForDebitDocument += " AND (cust.serviceAreaId =" + Long.valueOf(searchDebitDocsPojo.getServiceAreaId()) + ")";
                    countQueryDebitDocument += " AND (cust.serviceAreaId =" + Long.valueOf(searchDebitDocsPojo.getServiceAreaId()) + ")";
                    //exp = exp.and(qDebitDocument.customer.servicearea.id.eq(Long.valueOf(searchDebitDocsPojo.getServiceAreaId())));
                } else {
                    List<Long> ids = getServiceAreaIdsList();
                    String formattedList = "(" + ids.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")) + ")";
//                    List<Long> ids = customersService.getServiceAreaIdsList();
//                    if (getLoggedInUserId() != 1 && ids!=null) {
//                            exp = exp.and(qDebitDocument.customer.servicearea.id.in(ids));
//                    }
                    if (ids != null && customers.getId() != 1) {
                        queryForDebitDocument += " AND (cust.serviceAreaId IN " + formattedList + ")";
                        countQueryDebitDocument += " AND (cust.serviceAreaId IN " + formattedList + ")";
                    }
                }

                if (searchDebitDocsPojo.getPartnerId() != null) {
                    queryForDebitDocument += " AND (cust.partner=" + searchDebitDocsPojo.getPartnerId() + ")";
                    countQueryDebitDocument += " AND ( cust.partner=" + searchDebitDocsPojo.getPartnerId() + ")";
                    //exp = exp.and(qDebitDocument.customer.partner.id.eq(searchDebitDocsPojo.getPartnerId()));
                }

                if (searchDebitDocsPojo.getBranchId() != null) {
                    // exp = exp.and(qDebitDocument.customer.branch.eq(searchDebitDocsPojo.getBranchId()));
                    queryForDebitDocument += " AND ( cust.branch=" + searchDebitDocsPojo.getBranchId() + ")";
                    countQueryDebitDocument += " AND ( cust.branch=" + searchDebitDocsPojo.getBranchId() + ")";
                }

                if (searchDebitDocsPojo.getPartnerId() != null) {
                    queryForDebitDocument += " AND (deb.staffid=" + searchDebitDocsPojo.getStaffId() + ")";
                    countQueryDebitDocument += " AND (deb.staffid=" + searchDebitDocsPojo.getStaffId() + ")";
                    // exp = exp.and(qDebitDocument.staffid.eq(searchDebitDocsPojo.getStaffId()));
                }

                if (searchDebitDocsPojo.getStatus() != null && !searchDebitDocsPojo.getStatus().isEmpty()) {
                    queryForDebitDocument += " AND (deb.paymentStatus='" + searchDebitDocsPojo.getStatus() + "')";
                    countQueryDebitDocument += " AND (deb.paymentStatus='" + searchDebitDocsPojo.getStatus() + "')";
                    // exp = exp.and(qDebitDocument.paymentStatus.in(searchDebitDocsPojo.getStatus()));
                }
                if (searchDebitDocsPojo.getBillrunid() != null) {
                    queryForDebitDocument += " AND (deb.billrunid=" + searchDebitDocsPojo.getBillrunid() + ")";
                    countQueryDebitDocument += " AND (deb.billrunid=" + searchDebitDocsPojo.getBillrunid() + ")";
                    //exp = exp.and(qDebitDocument.billrunid.eq(searchDebitDocsPojo.getBillrunid()));
                }

                if (searchDebitDocsPojo.getCustomerid() != null) {
                    if (searchDebitDocsPojo.getBranchId() == null && searchDebitDocsPojo.getBusinessunit() == null)
                        queryForDebitDocument += " AND (deb.customer=" + searchDebitDocsPojo.getCustomerid() + ")";
                    countQueryDebitDocument += " AND (deb.customer=" + searchDebitDocsPojo.getCustomerid() + ")";
                    // exp = exp.and(qDebitDocument.customer.id.eq(searchDebitDocsPojo.getCustomerid()));
                }
//                if (startDate != null) {
//                    //exp = exp.and(qDebitDocument.billdate.goe(startDate.atTime(00, 00, 00)));
//                    queryForDebitDocument += " AND (deb.billdate >=" + startDate.atTime(00, 00, 00) + ")";
//                    countQueryDebitDocument += " AND (deb.billdate >= " + startDate.atTime(00, 00, 00) + ")";
//                }
//                if (endDate != null) {
//                    queryForDebitDocument += " AND (deb.billdate >= " + endDate.atTime(23, 59, 59) + ")";
//                    countQueryDebitDocument += " AND (deb.billdate >= " + endDate.atTime(23, 59, 59) + ")";
//                    // exp = exp.and(qDebitDocument.billdate.loe(endDate.atTime(23, 59, 59)));
//                }

                if (searchDebitDocsPojo.getCustname() != null && !searchDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
                    queryForDebitDocument += " AND ( cust.firstname='" + searchDebitDocsPojo.getCustname() + "')";
                    countQueryDebitDocument += " AND (cust.firstname='" + searchDebitDocsPojo.getCustname() + "')";
                    // exp = exp.and(qDebitDocument.customer.firstname.equalsIgnoreCase(searchDebitDocsPojo.getCustname()));
                    if (searchDebitDocsPojo.getCustname() != null && !searchDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
                        queryForDebitDocument += " AND (cust.lastname='" + searchDebitDocsPojo.getCustname() + "')";
                        countQueryDebitDocument += " AND (cust.lastname='" + searchDebitDocsPojo.getCustname() + "')";
                        //              exp = exp.or(qDebitDocument.customer.lastname.equalsIgnoreCase(searchDebitDocsPojo.getCustname()));
                    }
                }

                if (searchDebitDocsPojo.getType() != null && !searchDebitDocsPojo.getType().equalsIgnoreCase("")) {
                    queryForDebitDocument += " AND ( cust.custtype= '" + searchDebitDocsPojo.getType() + "')";
                    countQueryDebitDocument += " AND ( cust.custtype='" + searchDebitDocsPojo.getType() + "')";
                    //        exp = exp.and(qDebitDocument.customer.custtype.equalsIgnoreCase(searchDebitDocsPojo.getType()));
                }
                if (searchDebitDocsPojo.getStaffId() != null) {
                    queryForDebitDocument += " AND (deb.createdById=" + searchDebitDocsPojo.getStaffId() + ")";
                    countQueryDebitDocument += " AND (deb.createdById=" + searchDebitDocsPojo.getStaffId() + ")";
                    // exp = exp.and(qDebitDocument.createdById.eq(searchDebitDocsPojo.getStaffId()));
                }
                if (searchDebitDocsPojo.getServiceId() != null) {
                    if (searchDebitDocsPojo.getPlanId() != null) {
                        queryForDebitDocument += " AND (plan.id=" + searchDebitDocsPojo.getServiceId() + ")";
                        countQueryDebitDocument += " AND (plan.id=" + searchDebitDocsPojo.getPlanId() + ")";
                        //          exp = exp.and(qDebitDocument.postpaidPlan.id.eq(searchDebitDocsPojo.getPlanId()));
                    }
                    //filter by bu
//            if (searchDebitDocsPojo.getBusinessunit() != null) {
//                exp = exp.and(qDebitDocument.buId.eq(searchDebitDocsPojo.getBusinessunit()));
//            }
                    if (searchDebitDocsPojo.getBranchId() != null || searchDebitDocsPojo.getBusinessunit() != null) {
                        // List<DebitDocument>debitDocumentList = entityRepository.findAll();
                        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustRefIdIsNotNull();
                        List<Integer> custpackIds = custPlanMapppingList.stream().map(x -> x.getCustRefId()).collect(Collectors.toList());
                        List<CustPlanMappping> custPlanMapppingList1 = custPlanMappingRepository.findAllByIdIn(custpackIds);
                        List<Integer> customerIds = new ArrayList<>();
                        if (searchDebitDocsPojo.getBranchId() != null) {
                            customerIds = custPlanMapppingList1.stream().filter(i -> i.getCustomer().getBranch() == (searchDebitDocsPojo.getBranchId())).map(x -> x.getCustomer().getId()).collect(Collectors.toList());
                        } else {
                            customerIds = custPlanMapppingList1.stream().filter(i -> i.getCustomer().getBuId() == (searchDebitDocsPojo.getBusinessunit())).map(x -> x.getCustomer().getId()).collect(Collectors.toList());
                        }
                        // exp = exp.and(qDebitDocument.customer.id.in(customerIds));
                        queryForDebitDocument += " AND (deb.customer IN " + customerIds + ")";
                        countQueryDebitDocument += " AND (deb.customer IN " + customerIds + ")";

                    }

                    if (searchDebitDocsPojo.getCustmobile() != null && !searchDebitDocsPojo.getCustmobile().equalsIgnoreCase("")) {
                        //   exp = exp.and(qDebitDocument.customer.mobile.equalsIgnoreCase(searchDebitDocsPojo.getCustmobile()));
                        queryForDebitDocument += " AND (cust.mobile = '" + searchDebitDocsPojo.getCustmobile() + "')";
                        countQueryDebitDocument += " AND (cust.mobile =  '" + searchDebitDocsPojo.getCustmobile() + "')";

                    }

                    if (searchDebitDocsPojo.getDocnumber() != null
                            && !searchDebitDocsPojo.getDocnumber().trim().isEmpty()) {

                        String docNumber = searchDebitDocsPojo.getDocnumber().trim();
                        queryForDebitDocument += " AND deb.docnumber LIKE '%" + docNumber + "%'";
                        countQueryDebitDocument += " AND deb.docnumber LIKE '%" + docNumber + "%'";
                    }

                    if (searchDebitDocsPojo.getAdjustedAmount() != null && !searchDebitDocsPojo.getDocnumber().equalsIgnoreCase("")) {
                        queryForDebitDocument += " AND (deb.adjustedAmount = " + searchDebitDocsPojo.getAdjustedAmount() + ")";
                        countQueryDebitDocument += " AND (deb.adjustedAmount =  " + searchDebitDocsPojo.getAdjustedAmount() + ")";
                        //         exp = exp.and(qDebitDocument.adjustedAmount.eq(searchDebitDocsPojo.getAdjustedAmount()));
                    }
                    //  exp = exp.and(qDebitDocument.isDelete.eq(false)).and(qDebitDocument.customer.isDeleted.eq(false));

                    if (searchDebitDocsPojo.getType() != null && !searchDebitDocsPojo.getType().equalsIgnoreCase("")) {
                        queryForDebitDocument += " AND ( cust.custtype = '" + searchDebitDocsPojo.getType() + "')";
                        countQueryDebitDocument += " AND ( cust.custtype =  '" + searchDebitDocsPojo.getType() + "')";
                        //    exp = exp.and(qDebitDocument.customer.custtype.equalsIgnoreCase(searchDebitDocsPojo.getType()));
                    }
                }
                //    customerIds.add(searchDebitDocsPojo.getCustomerid());
            } else {
                if (isInvoiceVoid) {
                    queryForDebitDocument += " AND (deb.billrunstatus !=" + "'VOID'" + ")";
                    countQueryDebitDocument += " AND (deb.billrunstatus !=" + "'VOID'" + ")";
                    //                   exp = exp.and(qDebitDocument.billrunstatus.notEqualsIgnoreCase("VOID"));
                }

                if (searchDebitDocsPojo.getBillrunid() != null) {
                    queryForDebitDocument += " AND (deb.billrunid = " + searchDebitDocsPojo.getBillrunid() + ")";
                    countQueryDebitDocument += " AND (deb.billrunid =  " + searchDebitDocsPojo.getBillrunid() + ")";

                    //    exp = exp.and(qDebitDocument.billrunid.eq(searchDebitDocsPojo.getBillrunid()));
                }

                if (startDate != null) {
                    queryForDebitDocument += " AND (deb.billdate >= :startDate )";
                    countQueryDebitDocument += "AND (deb.billdate >= :startDate )";
                    //  exp = exp.and(qDebitDocument.billdate.goe(startDate.atTime(00, 00, 00)));
                }
                if (endDate != null) {
                    queryForDebitDocument += " AND (deb.billdate <= :endDate)";
                    countQueryDebitDocument += " AND (deb.billdate <= :endDate)";
                    //    exp = exp.and(qDebitDocument.billdate.loe(endDate.atTime(23, 59, 59)));
                }
                if (searchDebitDocsPojo.getSearchByTimeFrame() != null && !searchDebitDocsPojo.getSearchByTimeFrame().equalsIgnoreCase("")) {
                    if(searchDebitDocsPojo.getSearchByTimeFrame().equalsIgnoreCase("Week")) {
                        queryForDebitDocument += " AND (deb.startdate > '" + todaysDate.minusWeeks(1).atTime(00, 00, 00) + "')";
                        queryForDebitDocument += " AND (deb.startdate <= '" + todaysDate.atTime(23, 59, 59) + "')";
                        countQueryDebitDocument += " AND (deb.startdate > '" + todaysDate.minusWeeks(1).atTime(00, 00, 00) + "')";
                        countQueryDebitDocument += " AND (deb.startdate <= '" + todaysDate.atTime(23, 59, 59) + "')";
                    }
                    if(searchDebitDocsPojo.getSearchByTimeFrame().equalsIgnoreCase("Month")) {
                        queryForDebitDocument += " AND (deb.startdate > '" + todaysDate.minusMonths(1).atTime(00, 00, 00) + "')";
                        queryForDebitDocument += " AND (deb.startdate <= '" + todaysDate.atTime(00, 00, 00) + "')";
                        countQueryDebitDocument += " AND (deb.startdate > '" + todaysDate.minusMonths(1).atTime(00, 00, 00) + "')";
                        countQueryDebitDocument += " AND (deb.startdate <= '" + todaysDate.atTime(23, 59, 59) + "')";
                    }
                    if(searchDebitDocsPojo.getSearchByTimeFrame().equalsIgnoreCase("Last 6 Months")) {
                        queryForDebitDocument += " AND (deb.startdate > '" + todaysDate.minusMonths(6).atTime(00, 00, 00) + "')";
                        queryForDebitDocument += " AND (deb.startdate <= '" + todaysDate.atTime(23, 59, 59) + "')";
                        countQueryDebitDocument += " AND (deb.startdate > '" + todaysDate.minusMonths(6).atTime(00, 00, 00) + "')";
                        countQueryDebitDocument += " AND (deb.startdate <= '" + todaysDate.atTime(23, 59, 59) + "')";
                    }
                    //  exp = exp.and(qDebitDocument.billdate.goe(startDate.atTime(00, 00, 00)));
                }
                //            if (getLoggedInUser().getUserId() != 1) {
                //                queryForDebitDocument+=" AND (cust.serviceAreaId= "+ searchDebitDocsPojo.getServiceAreaId()+")";
                //                countQueryDebitDocument+=" AND ( cust.serviceAreaId =  "+searchDebitDocsPojo.getServiceAreaId()+")";
                //   //exp = exp.and(qDebitDocument.customer.servicearea.id.in(getServiceAreaIdList()));
                //            }

                if (searchDebitDocsPojo.getServiceAreaId() != null) {
                    queryForDebitDocument += " AND (cust.serviceAreaId = " + Long.valueOf(searchDebitDocsPojo.getServiceAreaId()) + ")";
                    countQueryDebitDocument += " AND ( cust.serviceAreaId=  " + Long.valueOf(searchDebitDocsPojo.getServiceAreaId()) + ")";
                    //  exp = exp.and(qDebitDocument.customer.servicearea.id.eq(Long.valueOf(searchDebitDocsPojo.getServiceAreaId())));
                } else {
                    List<Long> ids = getServiceAreaIdsList();
                    String formattedList = "(" + ids.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")) + ")";
                    // List<Long> ids = customersService.getServiceAreaIdsList();
                    //                    if (getLoggedInUser().getUserId() != 1 && !ids.isEmpty()) {
                    //                        queryForDebitDocument+="OR (cust.serviceAreaId IN "+formattedList+")";
                    //                        countQueryDebitDocument+="OR (cust.serviceAreaId IN " +formattedList+")";
                    //  //exp = exp.and(qDebitDocument.customer.servicearea.id.in(ids));
                    //                    }
                }

                if (searchDebitDocsPojo.getPartnerId() != null) {
                    queryForDebitDocument += " AND ( cust.partner = '" + searchDebitDocsPojo.getPartnerId() + "')";
                    countQueryDebitDocument += " AND (cust.partner  =  '" + searchDebitDocsPojo.getPartnerId() + "')";

                    //    exp = exp.and(qDebitDocument.customer.partner.id.eq(searchDebitDocsPojo.getPartnerId()));
                }

                if (searchDebitDocsPojo.getBranchId() != null) {
                    queryForDebitDocument += " AND ( cust.branch = '" + searchDebitDocsPojo.getBranchId() + "')";
                    countQueryDebitDocument += " AND ( cust.branch =  '" + searchDebitDocsPojo.getBranchId() + "')";
//                    exp = exp.and(qDebitDocument.customer.branch.eq(searchDebitDocsPojo.getBranchId()));
                }

                if (searchDebitDocsPojo.getStaffId() != null) {
                    queryForDebitDocument += " AND (deb.staffid = " + searchDebitDocsPojo.getStaffId() + ")";
                    countQueryDebitDocument += " AND (deb.staffid =  " + searchDebitDocsPojo.getStaffId() + ")";
                    //exp = exp.and(qDebitDocument.staffid.eq(searchDebitDocsPojo.getStaffId()));
                }

                if (searchDebitDocsPojo.getStatus() != null && !searchDebitDocsPojo.getStatus().isEmpty()) {
                    queryForDebitDocument += " AND (deb.paymentStatus =' " + searchDebitDocsPojo.getStatus() + "')";
                    countQueryDebitDocument += " AND (deb.paymentStatus =  '" + searchDebitDocsPojo.getStatus() + "')";
                    //         exp = exp.and(qDebitDocument.paymentStatus.in(searchDebitDocsPojo.getStatus()));
                }

                if (searchDebitDocsPojo.getBillableToName() != null && !searchDebitDocsPojo.getBillableToName().equalsIgnoreCase(" ")) {
                    queryForDebitDocument += "AND (deb.billableToName =' " + searchDebitDocsPojo.getBillableToName() + "')";
                    countQueryDebitDocument += "AND (deb.billableToName =  '" + searchDebitDocsPojo.getBillableToName() + "')";
                    //         exp = exp.and(qDebitDocument.paymentStatus.in(searchDebitDocsPojo.getStatus()));
                }

                if (searchDebitDocsPojo.getCustname() != null && !searchDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
                    queryForDebitDocument += " AND ( cust.firstname = '" + searchDebitDocsPojo.getCustname() + "')";
                    countQueryDebitDocument += " AND (cust.firstname  = ' " + searchDebitDocsPojo.getCustname() + "')";
                    // exp = exp.and(qDebitDocument.customer.firstname.equalsIgnoreCase(searchDebitDocsPojo.getCustname()));


                    if (searchDebitDocsPojo.getCustname() != null && !searchDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
                        queryForDebitDocument += " AND (cust.lastname = '" + searchDebitDocsPojo.getCustname() + "')";
                        countQueryDebitDocument += " AND ( cust.lastname =  '" + searchDebitDocsPojo.getCustname() + "')";
                        //exp = exp.or(qDebitDocument.customer.lastname.equalsIgnoreCase(searchDebitDocsPojo.getCustname()));
                    }
                }

                if (searchDebitDocsPojo.getType() != null && !searchDebitDocsPojo.getType().equalsIgnoreCase("")) {
                    queryForDebitDocument += " AND (cust.custtype = '" + searchDebitDocsPojo.getType() + "')";
                    countQueryDebitDocument += " AND (cust.custtype = ' " + searchDebitDocsPojo.getType() + "')";
                    //  exp = exp.and(qDebitDocument.customer.custtype.equalsIgnoreCase(searchDebitDocsPojo.getType()));
                }

//                if (searchDebitDocsPojo.getCustmobile() != null && !searchDebitDocsPojo.getCustmobile().equalsIgnoreCase("")) {
//                    queryForDebitDocument += " AND (cust.mobile = " + searchDebitDocsPojo.getCustmobile() + ")";
//                    countQueryDebitDocument += " AND (cust.mobile =  " + searchDebitDocsPojo.getCustmobile() + ")";
//                    // exp = exp.and(qDebitDocument.customer.mobile.equalsIgnoreCase(searchDebitDocsPojo.getCustmobile()));
//                }

                if (searchDebitDocsPojo.getDocnumber() != null
                        && !searchDebitDocsPojo.getDocnumber().trim().isEmpty()) {
                    String docNumber = searchDebitDocsPojo.getDocnumber().trim();
                    queryForDebitDocument +=
                            " AND deb.docnumber LIKE '%" + docNumber + "%'";
                    countQueryDebitDocument +=
                            " AND deb.docnumber LIKE '%" + docNumber + "%'";
                }
                if (searchDebitDocsPojo.getCustmobile() != null && !searchDebitDocsPojo.getCustmobile().equalsIgnoreCase("")) {
                    queryForDebitDocument += " AND (cust.mobile = '" + searchDebitDocsPojo.getCustmobile() + "')";
                    countQueryDebitDocument += " AND (cust.mobile =  '" + searchDebitDocsPojo.getCustmobile() + "')";
                    //     exp = exp.and(qDebitDocument.docnumber.equalsIgnoreCase(searchDebitDocsPojo.getDocnumber()));
                }

                if (searchDebitDocsPojo.getAdjustedAmount() != null && !searchDebitDocsPojo.getDocnumber().equalsIgnoreCase("")) {
                    queryForDebitDocument += " AND (deb.adjustedAmount = " + searchDebitDocsPojo.getAdjustedAmount() + ")";
                    countQueryDebitDocument += " AND (deb.adjustedAmount =  " + searchDebitDocsPojo.getAdjustedAmount() + ")";
                    //   exp = exp.and(qDebitDocument.adjustedAmount.eq(searchDebitDocsPojo.getAdjustedAmount()));
                }
                //             exp = exp.and(qDebitDocument.isDelete.eq(false)).and(qDebitDocument.customer.isDeleted.eq(false));


                if (searchDebitDocsPojo.getCustomerid() != null) {
                    queryForDebitDocument += " AND (deb.customer = " + searchDebitDocsPojo.getCustomerid() + ")";
                    countQueryDebitDocument += " AND (deb.customer =  " + searchDebitDocsPojo.getCustomerid() + ")";
                    //                  exp = exp.and(qDebitDocument.customer.id.eq(searchDebitDocsPojo.getCustomerid()));
                }
            }
//            if (searchDebitDocsPojo.getCustomerid() != null) {
//                queryForDebitDocument+="OR (deb.customer = "+searchDebitDocsPojo.getCustomerid()+")";
//                countQueryDebitDocument+="OR (deb.customer =  " +searchDebitDocsPojo.getCustomerid()+")";
//                //                  exp = exp.and(qDebitDocument.customer.id.eq(searchDebitDocsPojo.getCustomerid()));
//            }

            if (isOutstandingDue) {
                queryForDebitDocument += " AND (deb.totalamount - deb.adjustedAmount > 0)";
                countQueryDebitDocument += " AND (deb.totalamount - deb.adjustedAmount > 0)";
            }
            if(searchDebitDocsPojo.getStartfromdate()!=null){
                queryForDebitDocument += " AND (deb.startdate >= :startFromDate )";
                countQueryDebitDocument += " AND (deb.startdate >= :startFromDate )";

            }
            if(searchDebitDocsPojo.getStarttodate()!=null){
                queryForDebitDocument += " AND (deb.startdate < :startToDate )";
                countQueryDebitDocument += " AND (deb.startdate < :startToDate )";


            }
            if (searchDebitDocsPojo.getAcctno() != null && !searchDebitDocsPojo.getAcctno().equalsIgnoreCase("")) {
                queryForDebitDocument += " AND ( cust.acctno= '" + searchDebitDocsPojo.getAcctno() + "')";
                countQueryDebitDocument += " AND ( cust.acctno='" + searchDebitDocsPojo.getAcctno() + "')";
            }
            if (isKraSynced != null) {
                if(isKraSynced){
                    queryForDebitDocument += " AND (deb.isKraSynced = " + isKraSynced + ")";
                    countQueryDebitDocument += " AND (deb.isKraSynced = " + isKraSynced + ")";
                }
                else {

                    queryForDebitDocument += " AND (deb.isKraSynced = " + isKraSynced + ")";
                    countQueryDebitDocument += " AND (deb.isKraSynced = " + isKraSynced + ")";
                    queryForDebitDocument += " AND (deb.paymentStatus = 'Fully Paid' OR deb.paymentStatus = 'Payable')";
                    countQueryDebitDocument += " AND (deb.paymentStatus = 'Fully Paid' OR deb.paymentStatus = 'Payable')";

                }

            }
            queryForDebitDocument += " order by deb.id DESC";
            Query q = entityManager.createQuery(queryForDebitDocument, DebitDocument.class);
            Query queryTotal = entityManager.createQuery(countQueryDebitDocument);
            if (mvnoId != null && mvnoId != 1) {
                q.setParameter("mvnoId", mvnoId);
                queryTotal.setParameter("mvnoId", mvnoId);
            }
            if(startDate!=null){
                q.setParameter("startDate",startDate);
                queryTotal.setParameter("startDate",startDate);
            }
            if(endDate!=null){
                q.setParameter("endDate",endDate);
                queryTotal.setParameter("endDate",endDate);
            }
            if(searchDebitDocsPojo.getStartfromdate()!=null){
                LocalDateTime startFromDate = searchDebitDocsPojo.getStartfromdate().atStartOfDay();
                q.setParameter("startFromDate",startFromDate);
                queryTotal.setParameter("startFromDate",startFromDate);
            }
            if(searchDebitDocsPojo.getStarttodate()!=null){
                LocalDateTime startToDate = searchDebitDocsPojo.getStarttodate().plusDays(1).atStartOfDay();
                q.setParameter("startToDate",startToDate);
                queryTotal.setParameter("startToDate",startToDate);
            }

            q.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
            q.setMaxResults(pageRequest.getPageSize());
            List<DebitDocument> debitdocList = q.getResultList();
            List<DebitDocSearchPojo> docSearchPojos = new ArrayList<DebitDocSearchPojo>();
            for (DebitDocument debitDocument : debitdocList)
            {
                String planGroup = debitDocRepository.findPlanGroupByDebitDocumentId(debitDocument.getId());
                boolean isBoosterplan = false;
                if(planGroup != null && (planGroup.equalsIgnoreCase(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER) || planGroup.equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER) || planGroup.equalsIgnoreCase(CommonConstants.PLAN_GROUP_DTV_ADDON))){
                    isBoosterplan = true;
                }
                debitDocument.setMvnoName(mvnoRepository.findMvnoNameById(debitDocument.getCustomer().getMvnoId().longValue()));
                List<CreditDebitDocMapping> creditDebitDocMapping=creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
                List<CreditDocument> creditDocuments = creditDocRepository.findLightCreditDocumentByIdIn(creditDebitDocMapping.stream().map(i->i.getCreditDocId()).collect(Collectors.toList()));

                creditDocuments = creditDocuments.stream()
                        .filter(creditDocument -> "pending".equals(creditDocument.getStatus()))
                        .collect(Collectors.toList());


                debitDocument.setCreditDocumentList(creditDocuments);
                DebitDocSearchPojo debitDocSearchPojo = new DebitDocSearchPojo(debitDocument, isBoosterplan);
                List<Integer> serviceMappingIds = custPlanMapppingRepository.findAllByDebitdocid(debitDocument.getId().longValue())
                        .stream()
                        .map(CustPlanMappping::getId)
                        .collect(Collectors.toList());
                debitDocSearchPojo.setCustServiceMappingId(serviceMappingIds);
                if (debitDocument.getTotalamount()-debitDocument.getAdjustedAmount()>0){
                    CreditDocument creditDocument =  creditDocRepository.findLightCreditDocumentById(debitDocument.getId());
                    if(creditDocument != null){
                        if (creditDocument.getStatus().equalsIgnoreCase("Payment Failed")){
                            debitDocSearchPojo.setReferenceNo(creditDocument.getReferenceno());
                        }
                    }
                }
                debitDocSearchPojo.setTotalamount((double) Math.round(debitDocSearchPojo.getTotalamount()));
                docSearchPojos.add(debitDocSearchPojo);
            }
            if(isFromCustomerPortal) {
                docSearchPojos = docSearchPojos.stream().filter(a -> a.isBoosterplan() == false).collect(Collectors.toList());
            }
            long countResult = (long) queryTotal.getSingleResult();
//            return new PageImpl<DebitDocSearchPojo>(docSearchPojos, PageRequest.of(0, pageRequest.getPageSize()),
//                    countResult);
            return new PageImpl<DebitDocSearchPojo>(docSearchPojos, pageRequest,countResult);
            //   return new PageImpl<>(debitDocSearchPojos, PageRequest.of(paginationDTO.getPage() - 1, paginationDTO.getPageSize()), totalRecords);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return debitDocSearchPojos;

    }

    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).getValue());
        if (pageSize > MAX_PAGE_SIZE)
            pageSize = MAX_PAGE_SIZE;

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        else
            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        return pageRequest;
    }

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

    public List<java.lang.Long> getServiceAreaIdsList() {
        List<java.lang.Long> serviceAreaIds = new ArrayList<>();
        ;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                serviceAreaIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getServiceAreaIdList().stream()
                        .map(Long::valueOf)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            serviceAreaIds = new ArrayList<>();
        }
        return serviceAreaIds;
    }

    @Transactional
    public GenericDataDTO voidInvoice(Integer invoiceId, String invoiceCancelRemark, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            DebitDocument debitDocumentForCustomer = debitDocRepository.findById(invoiceId).orElse(null);
            List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(debitDocumentForCustomer.getCustomer().getId());
            List<DebitDocument> debitDocumentList = new ArrayList<>();
            if (lastInvoice.size() == 0) {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage("No invoice available for void.");
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Void Invoice"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_INFO  + "No invoice available for void." + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                return genericDataDTO;
            } else if (LocalDate.now().isAfter(debitDocumentForCustomer.getStartdate().toLocalDate())) {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage("Invoice can not be void only on same day.");
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Void Invoice"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_INFO  + "Invoice can not be void only on same day" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            } else {
//                QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
//                List<CreditDebitDocMapping> creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(qCreditDebitDocMapping.debtDocId.eq(debitDocumentForCustomer.getId()));
                List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findCreditDebitDocMappingsForDebitDocument(debitDocumentForCustomer.getId());
                if (creditDebitDocMappings.size() > 0) {
                    RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                    genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                    genericDataDTO.setResponseMessage("Invoice can not be void as some payment is adjusted to this invoice.");
                    logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Void Invoice"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_INFO  + "Invoice can not be void as some payment is adjusted to this invoice." + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }
            }

            //subisu void function
            voidBilltoSubisuInvoice(invoiceId, invoiceCancelRemark);

            //Void function for Remove LCO PartnerLedger and Revert Commission add in Revenue
            if (debitDocumentForCustomer != null && debitDocumentForCustomer.getCustomer().getLcoId() != null)
                voidLcoPartnerLedgerAndRevertCommission(invoiceId);
            VoidInvoiceMessage voidInvoiceMessage = new VoidInvoiceMessage();
            debitDocumentList.add(debitDocumentForCustomer);
            if (lastInvoice.get(0).getId().equals(debitDocumentForCustomer.getId())) {
                //Normal customer cpr data
                List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByDebitdocumentid(debitDocumentForCustomer.getId().longValue());
                if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                    if (custPlanMapppings.get(0).getBillTo().equalsIgnoreCase(Constants.SUBISU) && custPlanMapppings.get(0).getIsInvoiceToOrg()) {

                        Set<Integer> custRefIds = custPlanMapppings.stream().map(CustPlanMappping::getId).filter(Objects::nonNull).collect(Collectors.toSet());
                        List<Integer> debitDocList = custPlanMappingRepository.findAllByCustRefId(custRefIds);
//                        QDebitDocument qDebitDocumentForSubisu = QDebitDocument.debitDocument;
//                        BooleanExpression expression = qDebitDocumentForSubisu.isNotNull().and(qDebitDocumentForSubisu.id.in(debitDocList)).and(qDebitDocumentForSubisu.billrunstatus.ne("VOID"));
                        List<DebitDocument> debitDocumentListForSubisu = (List<DebitDocument>) debitDocRepository.findAllByDebiidocidAndBillrunStatus(debitDocList);
                        debitDocumentListForSubisu.stream().distinct();
                        debitDocumentList.addAll(debitDocumentListForSubisu);
                    }
                }

                for (DebitDocument debitDocument : debitDocumentList) {

                    List<TempPartnerLedgerDetail> tempPartnerLedgerDetail = tempPartnerLedgerDetailsRepository.findAllByInvoiceId(debitDocument.getId());
                    if (tempPartnerLedgerDetail != null && !tempPartnerLedgerDetail.isEmpty()) {
                        tempPartnerLedgerDetailsRepository.delete(tempPartnerLedgerDetail.get(0));
                    }

                    //  QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;

                    // List<CreditDebitDocMapping> creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(qCreditDebitDocMapping.debtDocId.eq(debitDocument.getId()));
                    List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findCreditDebitDocMappingsForDebitDocument(debitDocument.getId());
                    double refundAmount = 0D;
                    if (creditDebitDocMappings.size() > 0) {
                        for (CreditDebitDocMapping debitDocMapping : creditDebitDocMappings) {
                            if (debitDocMapping.getAdjustedAmount() > 0) {
//                                refundAmount = refundAmount + debitDocMapping.getAdjustedAmount();
//                                creditDebtMappingRepository.delete(debitDocMapping);
                                CreditDocument creditDocument = creditDocRepository.getOne(debitDocMapping.getCreditDocId());
                                creditDocument.setAdjustedAmount(0d);
                                creditDocument.setPaytype("advance");
                                creditDocRepository.save(creditDocument);
                                debitDocMapping.setIsDeleted(true);
                                creditDebtMappingRepository.delete(debitDocMapping);

                            }
                        }
                    }

                    debitDocument.setBillrunstatus("VOID");
                    if (invoiceCancelRemark != null)
                        debitDocument.setInvoiceCancelRemarks(invoiceCancelRemark);
                    debitDocRepository.save(debitDocument);

                    //If invoice is direct charge
                    if (debitDocument.getIsDirectChargeInvoice()) {
                        List<DebitDocDetails> debitDocDetails = debitDocDetailRepository.findAllByDebitdocumentid(debitDocument.getId());
                        for (DebitDocDetails debitDocDet : debitDocDetails) {
                            List<CustChargeDetails> custChargeDetails = custChargeRepository.findAllByCustomerAndChargeidAndIsDeletedIsFalse(debitDocument.getCustomer(), debitDocDet.getChargeid());
                            if (!CollectionUtils.isEmpty(custChargeDetails)) {
                                custChargeDetails = custChargeDetails.stream().peek(custChargeDetails1 -> custChargeDetails1.setIsDeleted(true)).collect(Collectors.toList());
                                custChargeRepository.saveAll(custChargeDetails);
                            }
                        }
                    }
                    Partner partner = partnerRepository.findById(debitDocument.getCustomer().getPartner()).orElse(null);
                    if (!Objects.equals(partner, CommonConstants.DEFAULT_PARTNER_ID)) {
                        //  QPartnerLedgerDetails partnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
                        List<PartnerLedgerDetails> partnerLedgerDetailsList = (List<PartnerLedgerDetails>) partnerLedgerDetailsRepository.findAllByDebiDocId(Long.valueOf(debitDocument.getId()));
                        double refundAmountForPartner = 0;
                        for (PartnerLedgerDetails details : partnerLedgerDetailsList) {
                            if (details.getCommission() > 0) {
                                refundAmountForPartner = refundAmountForPartner + details.getCommission();
                            }
                        }
                        if (refundAmountForPartner != 0) {

                            PartnerLedgerDetails reverseCommission = new PartnerLedgerDetails();
                            reverseCommission.setAmount(refundAmountForPartner);
                            reverseCommission.setDebitDocId(debitDocument.getId().longValue());
                            reverseCommission.setTranstype("DR");
                            reverseCommission.setCustid(debitDocument.getCustomer().getId());
                            reverseCommission.setPartner(partner);
                            reverseCommission.setIsDeleted(false);
                            reverseCommission.setCreateDate(LocalDateTime.now());
                            reverseCommission.setDescription("Commission reverted as the invoice " + debitDocument.getDocnumber() + " got void .");
                            reverseCommission.setTranscategory("Revert Commission");
                            partnerLedgerDetailsRepository.save(reverseCommission);
                        }
                    }
                }
                if (lastInvoice.get(0).getIsDirectChargeInvoice()) {
                    dbrService.removedbrByCPRStartDate(Long.valueOf(lastInvoice.get(0).getId()), lastInvoice.get(0).getStartdate().toLocalDate(), lastInvoice.get(0).getEndate().toLocalDate());
                    dbrService.removeDbrByCPRStartDateAtChargeLevel(Long.valueOf(lastInvoice.get(0).getId()), lastInvoice.get(0).getStartdate().toLocalDate(), lastInvoice.get(0).getEndate().toLocalDate());
                }
                List<CustPlanMappping> mapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getDebitdocid().intValue() == invoiceId).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(mapppings)) {
                    for (CustPlanMappping mapping : mapppings) {
//                            mapping.setIsDelete(true);
                        mapping.setIsVoid(Boolean.TRUE);
                        mapping.setEndDate(LocalDateTime.now());
                        mapping.setExpiryDate(LocalDateTime.now());
                        if (mapping.getStartDate().isAfter(mapping.getEndDate())) {
                            mapping.setStartDate(LocalDateTime.now());
                            mapping.setEndDate(mapping.getStartDate().plusSeconds(1));
                            mapping.setExpiryDate(mapping.getStartDate().plusSeconds(1));
                        }
                        mapping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                        custPlanMappingService.save(mapping, "");
                        Optional<DebitDocument> debitDocument = debitDocRepository.findById(mapping.getDebitdocid().intValue());
                        if (debitDocument.isPresent() && debitDocument.get().getCustomer().getCusttype().equalsIgnoreCase("PrePaid")) {
                            dbrService.removedbrByCPRStartDate(mapping.getDebitdocid(), debitDocument.get().getStartdate().toLocalDate(), debitDocument.get().getEndate().toLocalDate());
                            dbrService.removeDbrByCPRStartDateAtChargeLevel(mapping.getDebitdocid(), debitDocument.get().getStartdate().toLocalDate(), debitDocument.get().getEndate().toLocalDate());
                        }

                        if (debitDocument.isPresent() && debitDocument.get().getCustomer().getCusttype().equalsIgnoreCase("PostPaid"))
                        {
                            dbrService.removedbrByCPRStartDate(mapping.getDebitdocid());
                            dbrService.removeDbrByCPRStartDateAtChargeLevel(mapping.getDebitdocid());

                        }
                    }
                    // ezBillServiceUtility.deactivateService(mapppings, 13);
                }

                List<Integer> cprIds = mapppings.stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
                voidInvoiceMessage.setCprIdlist(cprIds);
                if (cprIds != null && !cprIds.isEmpty()) {
                    cprIds.stream().forEach(cprId -> {
                        List<CustomerChargeHistory> chargeHistories = customerChargeHistoryRepository.findAllChargesByCprId(cprId);
                        if (chargeHistories != null && !chargeHistories.isEmpty()) {
                            chargeHistories.stream().forEach(data -> {
                                Optional<Charge> charge = chargeRepository.findById(data.getChargeId());
                                if (charge.isPresent() && charge.get().getChargetype().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_NONRECURRING) && data.getIsFirstChargeApply()) {
                                    customerChargeHistoryRepository.delete(data);
                                }
                            });
                        }
                    });
                }

                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Invoice voided successfully.");
                RESP_CODE = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Void Invoice"  + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);


            } else {
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage("This invoice can not be void.");
                return genericDataDTO;
            }
            //remove ledger details entry
//            QCustomerLedgerDtls qCustomerLedgerDtls = QCustomerLedgerDtls.customerLedgerDtls;
//            BooleanExpression exp = qCustomerLedgerDtls.isNotNull();
//            exp = exp.and(qCustomerLedgerDtls.debitdocid.eq(invoiceId)).and(qCustomerLedgerDtls.customer.id.eq(debitDocumentForCustomer.getCustomer().getId()));
            Optional<CustomerLedgerDtls> customerLedgerDtls = customerLedgerDtlsRepository.findBYCustomerId(debitDocumentForCustomer.getCustomer().getId(),invoiceId);

            if (customerLedgerDtls.isPresent()) {
                customerLedgerDtls.get().setIsVoid(true);
                customerLedgerDtls.get().setIsDelete(true);
                customerLedgerDtlsRepository.save(customerLedgerDtls.get());

//                QCustomerLedger qCustomerLedger = QCustomerLedger.customerLedger;
//                BooleanExpression expression = qCustomerLedger.isNotNull();
//                expression = expression.and(qCustomerLedger.customer.id.eq(customerLedgerDtls.get().getCustomer().getId()));

                Optional<CustomerLedger> customerLedger = customerLedgerRepository.findByCustomerId(customerLedgerDtls.get().getCustomer().getId());
                if (customerLedger.isPresent()) {
                    customerLedger.get().setTotaldue(customerLedger.get().getTotaldue() - customerLedgerDtls.get().getAmount());
                    customerLedgerRepository.save(customerLedger.get());
                }
            }

//            messageSender.send(voidInvoiceMessage, RabbitMqConstants.QUEUE_UPDATE_VOID_INVOICE_STATUS);

        } catch (Exception e) {
            ApplicationLogger.logger.error("[CreditDocService]" + e.getMessage(), e);
            e.printStackTrace();
            return genericDataDTO;
        }
        return genericDataDTO;
    }

    private void voidBilltoSubisuInvoice(Integer invoiceId, String invoiceCancelRemark) {
        try {
            List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByDebitdocid(invoiceId.longValue());
            List<Integer> cprids = custPlanMapppings.stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toList());
            if (cprids.size() > 0) {
                for (Integer CustRefId : cprids) {
                    List<Integer> subisinvoiceId = custPlanMappingRepository.findAllByCustRefId(Collections.singleton(CustRefId));
                    if (subisinvoiceId.size() > 0) {
                        for (Integer id : subisinvoiceId) {
                            voidInvoice(id, invoiceCancelRemark,null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void voidLcoPartnerLedgerAndRevertCommission(Integer invoiceId) {
        Optional<DebitDocument> document = debitDocRepository.findById(invoiceId);
        if (document.isPresent()) {
//            QPartnerLedgerDetails partnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
//            BooleanExpression expression = partnerLedgerDetails.isNotNull().and(partnerLedgerDetails.debitDocId.eq(invoiceId.longValue()));
            List<PartnerLedgerDetails> details = (List<PartnerLedgerDetails>) partnerLedgerDetailsRepository.findAllByInvoiceId(invoiceId);
            if (!CollectionUtils.isEmpty(details)) {
                Double commission = details.stream().mapToDouble(x -> x.getAmount()).sum();
                //To Do
                Partner partner = partnerRepository.findById(document.get().getCustomer().getPartner()).orElse(null);
                // Partner partner = document.get().getCustomer().getPartner();


                //
                partner.setCommrelvalue(partner.getCommrelvalue() - commission);
                partnerRepository.save(partner);
                details.stream().forEach(x -> {
                    x.setIsDeleted(true);
                    partnerLedgerDetailsRepository.save(x);
                });
                PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
                partnerAmountMessage.setPartnerId(partner.getId());
                partnerAmountMessage.setComrelval(partner.getCommrelvalue());
                partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
                partnerAmountMessage.setBalance(partner.getBalance());
                partnerAmountMessage.setCredit(partner.getCredit());
                partnerAmountMessage.setRenewcust_count(0);
                partnerAmountMessage.setNewCustomer_count(0);
                kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//                messageSender.send(partnerAmountMessage, SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
            }
        }
    }

    @Transactional
    public TrialDebitDocument cancelAndRegenerateForTrailInvoice(Integer debitDocId) {
        try {
            TrialDebitDocument trialDebitDocument = trialDebitDocRepository.findById(debitDocId).orElse(null);
//            QTrialDebitDocument qTrialDebitDocument = QTrialDebitDocument.trialDebitDocument;
//            JPAQuery<TrialDebitDocument> debitDocumentJPAQuery = new JPAQuery<>(entityManager);
//            List<TrialDebitDocument> lastInvoice = debitDocumentJPAQuery.from(qTrialDebitDocument).where(qTrialDebitDocument.customer.id.eq(trialDebitDocument.getCustomer().getId()).and(qTrialDebitDocument.billrunstatus.eq("Generated").or(qTrialDebitDocument.billrunstatus.eq("Exported")))).orderBy(qTrialDebitDocument.docnumber.desc()).limit(1).fetch();
            List<TrialDebitDocument> lastInvoice = trialDebitDocRepository.findByCustomerId(trialDebitDocument.getCustomer().getId(), "Generated", "Exported");
            List<TrialDebitDocument> debitDocumentList = new ArrayList<>();
            if (lastInvoice.size() > 0 && !lastInvoice.contains(trialDebitDocument)) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only latest invoice can be regeneate and cancelled", null);
            }
            if (trialDebitDocument == null) {
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Invalid debit doc id: " + debitDocId, null);
            }
//            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
//            BooleanExpression expression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.customer.id.eq(trialDebitDocument.getCustomer().getId()));
            List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByCustomerId(trialDebitDocument.getCustomer().getId());
            //  QCustomerChargeHistory qCustomerChargeHistory = QCustomerChargeHistory.customerChargeHistory;
            List<Integer> custPlanIds = custPlanMapppings.stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toList());
            //BooleanExpression booleanExpression = qCustomerChargeHistory.isNotNull().and(qCustomerChargeHistory.custPlanMapppingId.in(custPlanIds)).and(qCustomerChargeHistory.chargeType.eq(CommonConstants.CHARGE_TYPE_NONRECURRING));
            //List<CustomerChargeHistory> customerChargeHistories = IterableUtils.toList(customerChargeHistoryRepository.findAll(booleanExpression));
            List<CustomerChargeHistory> customerChargeHistories = customerChargeHistoryRepository.findAllCustChargeHistory(custPlanIds, CommonConstants.CHARGE_TYPE_NONRECURRING);
            if (customerChargeHistories.size() > 0) {
                customerChargeHistories.forEach(customerChargeHistory -> {
                    customerChargeHistory.setIsFirstChargeApply(false);
                    customerChargeHistoryRepository.save(customerChargeHistory);
                });
            }
            List<CustChargeDetails> custChargeDetails = custChargeDetailsRepository.findAllByCustPlanMapppingIdIn(custPlanIds);

            if (!CollectionUtils.isEmpty(custChargeDetails)) {
                custChargeDetails.forEach(chargeDetails -> {
                    chargeDetails.setIsUsed(false);
                    chargeDetails.setDebitdocid(null);
                });
                custChargeDetailsRepository.saveAll(custChargeDetails);
            }

            if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                custPlanMapppings.forEach(custPlanMappping -> {
                    custPlanMappping.setDebitdocid(null);
                    custPlanMappping.setIsInvoiceCreated(false);
                });
                custPlanMappingRepository.saveAll(custPlanMapppings);
            }
            trialDebitDocument.setBillrunstatus("Cancelled");
            Customers customers = trialDebitDocument.getCustomer();
            customers.setLastBillDate(LocalDate.now());
            return trialDebitDocRepository.save(trialDebitDocument);
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (RuntimeException ex) {
            throw new RuntimeException("Exception at Cancel and Regenerate invoice for id: " + debitDocId + " ex: " + ex.getMessage());
        }

    }

    private boolean isPaymentPendingSent(Integer debitDocId) {
        try {
            List<Integer> creditDocids = new ArrayList<>();
            if (debitDocId != null) {
//                QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
//                BooleanExpression exp = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.debtDocId.eq(debitDocId));
                List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findCreditDebitDocMappingsForDebitDocument(debitDocId);
                //  List<CreditDebitDocMapping> creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(exp);
                creditDocids = creditDebitDocMappings.stream().filter(i -> i.getCreditDocId() != null).map(i -> i.getCreditDocId()).collect(Collectors.toList());
//                QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
//                BooleanExpression exp2 = qCreditDocument.isNotNull().and(qCreditDocument.id.in(creditDocids)).and(qCreditDocument.status.equalsIgnoreCase("pending"));

                return creditDocRepository.isPresent(creditDocids, "pending");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public List<Integer> save(ChangePlanMessage message) {

        try {
            List<Integer> oldDebitIds = new ArrayList<>();
            String custStatus =null;
            String custType = null;
            String userName = null;
            LocalDate maxDate = null;
            List<Integer> cprIdsActive = new ArrayList<>();
            List<Integer> OldDebitDocIds = new ArrayList<>();
            if (message.getNewCustPlanMappingRevenues().size() > 0) {
                List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
                for (CustPlanMappingRevenue data : message.getNewCustPlanMappingRevenues()) {
                    Customers customers = customersRepository.findById(data.getCustomerId()).get();
                    custType = customers.getCusttype();
                    custStatus = customers.getStatus();
                    userName = customers.getUsername();
                    PlanGroup planGroup = null;
                    if (data.getPlanGroupId()!=null) {
                        planGroup = planGroupRepository.findById(data.getPlanGroupId()).get();
                    }
                    CustPlanMappping custPlanMapping = new CustPlanMappping(data, customers, message.getType(),planGroup);
                    custPlanMapppings.add(custPlanMapping);
                }
                custPlanMapppingRepository.saveAll(custPlanMapppings);
            }


            if (message.getOldCustPlanMappingRevenues()!=null && message.getOldCustPlanMappingRevenues().size() > 0) {
                List<CustPlanMappping> custPlanMapppings = new ArrayList<>();
                List<Integer> ids = new ArrayList<>();

                List<CustomerChargeHistory> customerChargeHistory = customerChargeHistoryRepository.findAllByCustomerIdAndChargeType(message.getNewCustPlanMappingRevenues().get(0).getCustomerId(),CommonConstants.CHARGE_TYPE_RECURRING);
                Set<Integer> cprIds = customerChargeHistory.stream().map(x->x.getCustPlanMapppingId()).collect(Collectors.toSet());
                cprIdsActive = custPlanMappingRepository.getAllCustPlanMappingByCustCPRListAndStatus(cprIds,"Active");
                for (CustPlanMappingRevenue data : message.getOldCustPlanMappingRevenues()) {
                    CustPlanMappping custPlanMappping = custPlanMapppingRepository.findById(data.getId()).get();
                    custPlanMappping.setEndDate(parseFlexibleDateTime(data.getEndDate()));
                    custPlanMappping.setExpiryDate(parseFlexibleDateTime(data.getExpiryDate()));
                    custPlanMappping.setCustPlanStatus(data.getCustPlanStatus());
                    custPlanMapppings.add(custPlanMappping);
                    if(custPlanMappping.getDebitdocid()!=null)
                        OldDebitDocIds.add(debitDocRepository.findAllByIdandStatus(custPlanMappping.getDebitdocid().intValue()));
                    ids.add(data.getId());
                }
                custPlanMapppingRepository.saveAll(custPlanMapppings);
                List<CustomerChargeHistory> customerChargeHistories = customerChargeHistoryRepository.findAllByCustPlanMapppingIdIn(ids);
                Optional<LocalDate> maxDateOptional = customerChargeHistories.stream()
                        .filter(history -> history.getDiscountExpDate() != null)
                        .map(CustomerChargeHistory::getDiscountExpDate)
                        .max(LocalDate::compareTo);

                maxDate = maxDateOptional.orElse(null);
                if (custStatus.equalsIgnoreCase("NewActivation") && message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN)) {
                    List<TrialDebitDocument> olddebit = trialDebitDocRepository.findOldDebitDocIds(ids);
                    if (!CollectionUtils.isEmpty(olddebit)){
                        olddebit.get(0).setBillrunstatus("Cancelled");
                        olddebit.get(0).setPaymentStatus("Cancelled");
                        trialDebitDocRepository.save(olddebit.get(0));
                    } else {
                        System.out.println("No old debit document found. Complimentary plan detected.");
                        logger.info("No old debit document found. Complimentary plan detected.");
                    }
                }else {
                    if (message.getType().equalsIgnoreCase(Constants.INVOICE_TYPE.CHANGE_PLAN)) {
                        //wrote this old ids were gwtting duplicated and chance of null ids, if we don't remove null credit note won't generate
                        //TODO improve thos code
                        OldDebitDocIds.addAll(debitDocRepository.findOldDebitDocIdsWithoutDirectCharge(ids));
                        List<Integer> oldIds = OldDebitDocIds.stream().distinct().collect(Collectors.toList());
                        List<Integer> listWithoutNulls = oldIds.stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                        oldDebitIds.addAll(listWithoutNulls);
                    }
                    else
                        oldDebitIds.addAll(debitDocRepository.findOldDebitDocIds(ids));
                }
            }


            if (message.getCustomerServiceMappingRevenues().size() > 0) {
                List<CustomerServiceMapping> customerServiceMappings = new ArrayList<>();
                for (CustomerServiceMappingRevenue data : message.getCustomerServiceMappingRevenues()) {
                    CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping(data);
                    if (message.getType() != null && message.getType().equalsIgnoreCase("addNewService")) {
                        customerServiceMapping.setStatus(CommonConstants.CUSTOMER_STATUS_ACTIVE);
                    } else if (message.getType() != null && message.getType().equalsIgnoreCase("isCAFCustomer")){
                        customerServiceMapping.setStatus(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION);
                    }
                    customerServiceMappings.add(customerServiceMapping);
                }
                customerServiceMapRepository.saveAll(customerServiceMappings);
            }

            String customerTypeName = getCustomerType(custType);

            Customers customers = customersRepository.findById( message.getNewCustPlanMappingRevenues().get(0).getCustomerId()).get();
            if(customers.getPartner()!=null) {
                if (message.getType().equalsIgnoreCase("renew") || message.getType().equalsIgnoreCase("Change_Plan")) {
                    Partner partner=partnerRepository.findById(customers.getPartner()).orElse(null);
                    if(partner != null) {
                        if(partner.getRenewCustomerCount()!=null)
                            partner.setRenewCustomerCount(partner.getRenewCustomerCount().longValue() +1);
                        else
                            partner.setRenewCustomerCount(0l);
                        partnerRepository.save(partner);
                        PartnerAmountMessage partnerAmountMessage=new PartnerAmountMessage();
                        partnerAmountMessage.setPartnerId(customers.getPartner());
                        partnerAmountMessage.setRenewcust_count(partner.getRenewCustomerCount().intValue());
                        partnerAmountMessage.setBalance(partner.getBalance());
                        partnerAmountMessage.setCredit(partner.getCredit());
                        partnerAmountMessage.setCreditconsume(partner.getCreditConsume());
                        partnerAmountMessage.setComrelval(partner.getCommrelvalue());
//                        messageSender.send(partnerAmountMessage,SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_PARTNER);
//                        messageSender.send(partnerAmountMessage,SharedDataConstants.QUEUE_PARTNER_BALANCE_DATA_SHARE_API);
//                        kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(),"BALANCE_DATA_PARTNER"));
                        kafkaMessageSender.send(new KafkaMessageData(partnerAmountMessage, PartnerAmountMessage.class.getSimpleName(), KafkaConstant.SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER));
                    }
                }
            }
            boolean generateProratePostpaidInvoice =  false;

            if (customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                List<CustomerChargeHistory> customerChargeHistory = customerChargeHistoryRepository.findAllByCustomerIdAndChargeType(message.getNewCustPlanMappingRevenues().get(0).getCustomerId(),CommonConstants.CHARGE_TYPE_RECURRING);
                for (CustomerChargeHistory chargeHistory : customerChargeHistory){
                    if (chargeHistory.getLastBillDate() != null && chargeHistory.getLastBillDate().isBefore(LocalDate.now()) && !message.isChangePlanNextBillDate()) {
                        generateProratePostpaidInvoice = true;
                        break;
                    } else if (chargeHistory.getCreatedate().isBefore(LocalDate.now().atStartOfDay())  && !message.isChangePlanNextBillDate()) {
                        generateProratePostpaidInvoice = true;
                        break;
                    }
                }
                customerChargeHistory = customerChargeHistoryRepository.findAllByCustPlanMapppingIdIn(message.getOldCustPlanMappingRevenues().stream().map(x->x.getId()).collect(Collectors.toList()));
                if(message.isChangePlanNextBillDate()){
                    customerChargeHistory = customerChargeHistory.stream().peek(x->x.setNextBillDate(customers.getNextBillDate())).collect(Collectors.toList());
                    customerChargeHistoryRepository.saveAll(customerChargeHistory);
                }else {
                    //Issue resolved for change plan postpaid customer with recurring postapid charge
                    customerChargeHistory = customerChargeHistory.stream().peek(x->x.setNextBillDate(LocalDate.now())).collect(Collectors.toList());
                    customerChargeHistoryRepository.saveAll(customerChargeHistory);
                    message.setBillDateToday("Today");
                }
                if (generateProratePostpaidInvoice){
                    if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)){
                        message.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
                    }
                    createInvoiceForPostpaidChangePlanProrate(customers.getId(),message,cprIdsActive,null);
                }

            }
            if (message.getCustomerChargeHistoryRevenues().size() > 0) {
                //this condition is when we renew customer we cannot apply non recurring charge again , so this condtion is must
                if (message.getType().equalsIgnoreCase("renew") && !(userName.equalsIgnoreCase(customerTypeName))){
                    message.getCustomerChargeHistoryRevenues().removeIf(i->i.getChargeType().equalsIgnoreCase("NON_RECURRING"));
                }
                List<CustomerChargeHistory> customerChargeHistoryList = new ArrayList<>();
                for (CustomerChargeHistoryRevenue data : message.getCustomerChargeHistoryRevenues()) {
                    //ANG-10900
                    maxDate= customerServiceMapRepository.newDiscountExpiryDate(message.getNewCustPlanMappingRevenues().get(0).getCustServiceMappingId());
                    CustomerChargeHistory customerChargeHistory = new CustomerChargeHistory(data,customers,message.isChangePlanNextBillDate(),maxDate, message.getType());
                    customerChargeHistoryList.add(customerChargeHistory);
                }
                customerChargeHistoryRepository.saveAll(customerChargeHistoryList);
                if(generateProratePostpaidInvoice){
                    List<CustomerChargeHistory> customerChargeHistories = customerChargeHistoryRepository.findAllByCustomerId(customers.getId());
                    if (!CollectionUtils.isEmpty(customerChargeHistories)) {
                        Set<Integer> cprids = customerChargeHistories.stream().map(i->i.getCustPlanMapppingId()).collect(Collectors.toSet());
                        if (cprids.size()>0){
                            List<Integer> expiredCustPackids = custPlanMappingRepository.getAllCustPlanMappingByCustCPRListAndStatus(cprids,"STOP");
                            if (expiredCustPackids.size()>0) {
                                customerChargeHistories = customerChargeHistories.stream().filter(i -> expiredCustPackids.contains(i.getCustPlanMapppingId()))
                                        .peek(i->i.setNextBillDate(LocalDate.now())).collect(Collectors.toList());
                                customerChargeHistoryRepository.saveAll(customerChargeHistories);
                            }
                            List<CustomerChargeHistory> customerChargeHistoriesforUpdate = customerChargeHistoryRepository.findAllByCustomerId(customers.getId());
                            List<Integer> activeids = custPlanMappingRepository.getAllCustPlanMappingByCustCPRListAndStatus(cprids,"Active");
                            if(!CollectionUtils.isEmpty(customerChargeHistoriesforUpdate)) {
                                customerChargeHistoriesforUpdate = customerChargeHistories.stream().filter(i -> activeids.contains(i.getCustPlanMapppingId())).collect(Collectors.toList());
                                if(!CollectionUtils.isEmpty(customerChargeHistoriesforUpdate)) {
                                    customerChargeHistoriesforUpdate = customerChargeHistories.stream().filter(customerChargeHistory -> !customerChargeHistory.getChargeType().equalsIgnoreCase("NON_RECURRING")).sorted(Comparator.comparing(CustomerChargeHistory::getNextBillDate)).collect(Collectors.toList());
                                    LocalDate nextBillDate = customerChargeHistoriesforUpdate.get(0).getNextBillDate();
                                    customers.setNextBillDate(nextBillDate);
                                    customersRepository.save(customers);
                                }
                            }
                        }
                    }

                }
            }
            if(customers.getStatus().equalsIgnoreCase("NewActivation")){
                List<Integer> oldCprids= message.getOldCustPlanMappingRevenues().stream().map(x->x.getId()).collect(Collectors.toList());
                if (oldCprids!=null && oldCprids.size()>0) {
                    List<CustomerChargeHistory> chcs = customerChargeHistoryRepository.findAllByCustPlanMapppingIdIn(oldCprids);
                    customerChargeHistoryRepository.deleteAll(chcs);
                }
            }

            return oldDebitIds;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createInvoiceForPostpaidChangePlanProrate(Integer id,ChangePlanMessage message,List<Integer> cprIdsActive,LocalDate billdate) {
        try {
            CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
            Map<String, Object> data = new HashMap<>();
            if (message!=null) {
                data.put(CustomerBillingMessage.CUST_ID,id);
            }

            if (billdate==null){
                data.put(CustomerBillingMessage.RENEWAL_ID, message.getRenewalId());
                data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF, message.getCreatedById());
            }

            if(message.getBillDateToday()!=null){
                customerBillingMessage.setBilldateToday("Today");
            }



            if (message.getBuId() != null && !message.getBuId().isEmpty()) {
                data.put(CustomerBillingMessage.BUIDS, message.getBuId());
            }
            if (message.getMvnoId() != null) {
                data.put(CustomerBillingMessage.MVNOID, message.getMvnoId());
            }
            if (message.getLcoId() != null) {
                data.put(CustomerBillingMessage.PARTNERID, message.getLcoId());
            }

            if (message.getCreatedById() != null) {
                data.put(CustomerBillingMessage.CREATEDBYID, message.getCreatedById());
            }
            if (message.getGetCreatedByName() != null) {
                data.put(CustomerBillingMessage.CREATEDBYNAME, message.getGetCreatedByName());
            }
            if (message.getRecordPaymentDTO() != null) {
                customerBillingMessage.setRecordPaymentDTO(message.getRecordPaymentDTO());
            }

            customerBillingMessage.setData(data);
            customerBillingMessage.setType(message.getType());
            if (cprIdsActive!=null){
                customerBillingMessage.setOldCprIdsForChangePLan(cprIdsActive);
                List<Integer> oldDebitIds = custPlanMappingRepository.getAllDebitIdsByCustCPRList(cprIdsActive);
                if (!CollectionUtils.isEmpty(oldDebitIds)) {
                    String oldDebitDocumentIdStr = "";
                    for (Integer invoiceNo : oldDebitIds) {
                        oldDebitDocumentIdStr = oldDebitDocumentIdStr + invoiceNo + ",";
                    }
                    oldDebitDocumentIdStr = oldDebitDocumentIdStr.substring(0, oldDebitDocumentIdStr.length() - 1);
                    data.put(CustomerBillingMessage.oldDebitDocId, oldDebitDocumentIdStr);
                } else {
                    data.put(CustomerBillingMessage.oldDebitDocId, "");
                }
            }
            data.put(CustomerBillingMessage.RENEWAL_ID, message.getRenewalId());
            customerBillingMessage.setCprIds(cprIdsActive);
            message.getCreatedById();
            if (message.getParentId() != null && message.getChildIds() != null && message.getChildIds().size() > 0) {
                data.put(CustomerBillingMessage.CUST_ID, message.getParentId());
                customerBillingMessage.setChildIds(message.getChildIds());
            }
            customerBillingMessage.setBilldate(billdate);
            if (message.getCustType()!=null && message.getCustType().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)){
                customerBillingMessage.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
            }
            TraceContext traceContext =tracer.currentSpan().context();
            customerBillingMessage.setTraceContext(traceContext);
            customerBillingMessage.setIsEarlyBillDate(false);
            messageReceiverWithThread.processMessage(customerBillingMessage,null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public DebitDocumentPojo convertDebitDocumentModelToDebitDocumentPojo(DebitDocument debitDocument) throws Exception {

        DebitDocumentPojo pojo = null;
        if (debitDocument != null) {
            pojo = new DebitDocumentPojo();
            pojo.setId(debitDocument.getId());
            pojo.setDocnumber(debitDocument.getDocnumber());
            if (debitDocument.getCustomer() != null) {
                pojo.setCustomer(subscriberService.convertCustomersModelToCustomersPojo(debitDocument.getCustomer()));
            }
            pojo.setBilldate(debitDocument.getBilldate());
            pojo.setCreatedate(debitDocument.getCreatedate());
            pojo.setStartdate(debitDocument.getStartdate());
            pojo.setEndate(debitDocument.getEndate());
            pojo.setDuedate(debitDocument.getDuedate());
            pojo.setLatepaymentdate(debitDocument.getLatepaymentdate());
            pojo.setSubtotal(debitDocument.getSubtotal());
            pojo.setTax(debitDocument.getTax());
            pojo.setDiscount(debitDocument.getDiscount());
            pojo.setTotalamount(debitDocument.getTotalamount());
            pojo.setPreviousbalance(debitDocument.getPreviousbalance());
            pojo.setLatepaymentfee(debitDocument.getLatepaymentfee());
            pojo.setCurrentcredit(debitDocument.getCurrentcredit());
            pojo.setCurrentdebit(debitDocument.getCurrentdebit());
            pojo.setTotaldue(debitDocument.getTotaldue());
            pojo.setAmountinwords(debitDocument.getTotalamountinwords());
            pojo.setDueinwords(debitDocument.getTotaldueinwords());
            pojo.setBillrunid(debitDocument.getBillrunid());
            pojo.setBillrunstatus(debitDocument.getBillrunstatus());
            pojo.setDocument(debitDocument.getDocument());
            pojo.setCreditDocumentList(debitDocument.getCreditDocumentList());
            pojo.setAdjustedAmount(debitDocument.getAdjustedAmount());
            pojo.setCreatedByName(debitDocument.getCreatedByName());
            pojo.setLastModifiedByName(debitDocument.getLastModifiedByName());
            pojo.setBillableToName(debitDocument.getBillableToName());
            if (debitDocument.getPaymentStatus() != null && !"".endsWith(debitDocument.getPaymentStatus())) {
                pojo.setPaymentStatus(debitDocument.getPaymentStatus());
            }
            if (debitDocument.getCustomer() != null) {
                pojo.setCustid(debitDocument.getCustomer().getId());
                pojo.setCustomerName(debitDocument.getCustomer().getFullName());
                pojo.setCustType(debitDocument.getCustomer().getCusttype());
            }
            pojo.setCustRefName(debitDocument.getCustRefName());
            pojo.setRefundAbleAmount(getRefundAmount(pojo.getId()));
            pojo.setDebitDocumentTAXRels(getDebitTaxPojo(debitDocument.getId()));
            pojo.setNextStaff(debitDocument.getNextStaff());
            pojo.setNextTeamHierarchyMappingId(debitDocument.getNextTeamHierarchyMappingId());
            //pojo.setPaymentStatus(getStatus(debitDocument));
            List<DebitDocDetails> details = new ArrayList<>();
            List<DebitDocDetails> debitDocDetails = debitDocRepository.debitDocDetailsByDebitDocId(debitDocument.getId());
            Map<Integer, List<DebitDocDetails>> collect = debitDocDetails.stream().collect(Collectors.groupingBy(DebitDocDetails::getChargeid));
            for (Map.Entry<Integer, List<DebitDocDetails>> entry : collect.entrySet()) {
                List<DebitDocDetails> list = entry.getValue();
                Double subtotal = list.stream().mapToDouble(x -> x.getSubtotal()).sum();
                Double total = list.stream().mapToDouble(x -> x.getTotalamount()).sum();
                Double discount = list.stream().mapToDouble(x -> x.getDiscount()).sum();
                Double tax = list.stream().mapToDouble(x -> x.getTax()).sum();
                list.get(0).setSubtotal(subtotal);
                list.get(0).setTotalamount(total);
                list.get(0).setDiscount(discount);
                list.get(0).setTax(tax);
                details.add(list.get(0));
            }
//            List<DebitDocumentInventoryRel> debitDocumentInventoryRels = debitDocument.getDebitDocumentInventoryRels();
//            if (!CollectionUtils.isEmpty(debitDocumentInventoryRels)) {
//                pojo.setDebitDocumentInventoryRels(debitDocumentInventoryRels);
//            }

            pojo.setDebitDocDetails(details);
            pojo.setStatus(debitDocument.getStatus());
            if (debitDocument.getPaymentowner() != null && debitDocument.getPaymentowner() != "") {
                pojo.setPaymentowner(debitDocument.getPaymentowner());
            } else if (debitDocument.getStaffid() != null) {
                StaffUser staffUser = staffUserRepository.findById(debitDocument.getStaffid()).get();
                pojo.setPaymentowner(staffUser.getUsername());
            }

            pojo.setIsPromiseToPayInOldCPR(debitDocument.getIsPromiseToPayInOldCPR());
            pojo.setPromiseToPayHoldDays(debitDocument.getPromiseToPayHoldDays());
            pojo.setPromiseEndDate(debitDocument.getPromiseEndDate());
            pojo.setPromiseStartDate(debitDocument.getPromiseStartDate());
            pojo.setIsCNEnable(debitDocument.getIsCNEnable());
            pojo.setPendingAmt(debitDocument.getPendingAmt());
            if (debitDocument.getInvoiceCancelRemarks() != null && !"".endsWith(debitDocument.getInvoiceCancelRemarks())) {
                pojo.setInvoiceCancelRemarks(debitDocument.getInvoiceCancelRemarks());
            }
        }
        return pojo;
    }

    private String getRefundAmount(Integer id ) {
        DecimalFormat df = new DecimalFormat("#.00");
        DebitDocument debitDocument = debitDocRepository.findById(id).orElse(null);
        Double amount = getPendingRevenueWithTaxAtCurrentDate(debitDocument);
        if (amount != null)
            return amount.toString();
        else
            return "0.0";
    }

    public Map<Integer, Double> getRefundMapForDocsOptimized(List<DebitDocumentCreditNoteView> docs) {
        Map<Integer, Double> refundMap = new HashMap<>();
        if (docs == null || docs.isEmpty()) return refundMap;

        List<Long> docIds = docs.stream()
                .map(DebitDocumentCreditNoteView::getId)
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<CustPlanMappingPartial> allCustPlanMappings = custPlanMappingRepository.findRelevantMappingsForDocs(docIds);

        LocalDate today = LocalDate.now();
        Map<Long, List<CustomerDBRPartial>> docIdToDBRs = dbrService
                .getCustomerDBRListForDocsBetweenStartDateAndEndDate(docIds, today);

        for (DebitDocumentCreditNoteView doc : docs) {
            try {
                // ✅ FIX: If invoice has not started yet → full refund
                LocalDate startDate = doc.getStartdate().toLocalDate();
                if (startDate.isAfter(today)) {

                    double adjusted = doc.getAdjustedAmount() == null ? 0.0 : doc.getAdjustedAmount();
                    double fullRefund = doc.getTotalamount();
                    if (fullRefund < 0) fullRefund = 0.0;

                    refundMap.put(doc.getId(), fullRefund);
                    continue;   // <- IMPORTANT: Skip normal DBR logic
                }

                double pendingRevenue = calculatePendingRevenue(doc, allCustPlanMappings, docIdToDBRs, today);
                refundMap.put(doc.getId(), pendingRevenue);
            } catch (Exception e) {
                refundMap.put(doc.getId(), 0.0);
                System.err.println("Error calculating refund for docId=" + doc.getId() + ": " + e.getMessage());
            }
        }

        return refundMap;
    }

    private double calculatePendingRevenue(
            DebitDocumentCreditNoteView debitDocument,
            List<CustPlanMappingPartial> allCustPlanMappings,
            Map<Long, List<CustomerDBRPartial>> docIdToDBRs,
            LocalDate today) {

        if (debitDocument == null) return 0d;

        boolean includeCustPackRel = Boolean.TRUE.equals(debitDocument.getIsDirectChargeInvoice())
                && debitDocument.getCustpackrelid() != null;

        List<CustPlanMappingPartial> custPlanMappings = allCustPlanMappings.stream()
                .filter(m -> m.getDebitDocId().equals(debitDocument.getId().longValue()))
                .filter(m -> !includeCustPackRel || (includeCustPackRel && m.getCustPackRelId() != null && m.getCustPackRelId().equals(debitDocument.getCustpackrelid())))
                .collect(Collectors.toList());

        LocalDate effectiveDate = today;
        if (!custPlanMappings.isEmpty()) {
            LocalDateTime startDateTime = custPlanMappings.get(0).getStartDate();
            if (LocalDateTime.now().isBefore(startDateTime)) {
                effectiveDate = debitDocument.getStartdate().toLocalDate();
            }
        }

        List<CustomerDBRPartial> customerDBRList = docIdToDBRs.getOrDefault(debitDocument.getId().longValue(), Collections.emptyList());
        if (customerDBRList.isEmpty()) return 0d;

        final LocalDate dateForLambda = effectiveDate;
        double basePendingRevenue = customerDBRList.stream()
                .filter(x -> dateForLambda.equals(x.getStartdate()))
                .mapToDouble(x -> x.getPendingamt() + x.getDbr())
                .sum();

        if (basePendingRevenue <= 0d) return 0d;

        double ratio = safeDivide(debitDocument.getTotalamount(),
                debitDocument.getSubtotal() + debitDocument.getDiscount());
        double pendingRevenue = basePendingRevenue * ratio;

        if (Double.isNaN(pendingRevenue) || Double.isInfinite(pendingRevenue)) return 0d;

        return roundTo4Decimals(pendingRevenue);
    }



    private List<DebitDocumentTAXRel> getDebitTaxPojo(Integer id) {
        return debitDocRepository.getAllDebitDocTaxDetails(id);
    }

    public Double getPendingRevenueWithTaxAtCurrentDate(DebitDocument debitDocument) {
        Double pendingRevenue = 0d;
        DecimalFormat df = new DecimalFormat("#.0000");
        if (debitDocument == null)
            return 0d;
        List<CustomerDBR> customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(LocalDate.now(), debitDocument);
        pendingRevenue = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(LocalDate.now())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
        List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByDebitdocid(debitDocument.getId());
        if (debitDocument.getIsDirectChargeInvoice()){
            custPlanMapppings.add(custPlanMappingRepository.findById(debitDocument.getCustpackrelid()).get());
        }
        if(!custPlanMapppings.isEmpty()){
            LocalDateTime startDate = custPlanMapppings.get(0).getStartDate();
            if (LocalDateTime.now().isBefore(startDate)) {
                customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(debitDocument.getStartdate().toLocalDate(), debitDocument);
                pendingRevenue = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(startDate.toLocalDate())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
                if (pendingRevenue > 0.0d)
                    pendingRevenue = (debitDocument.getTotalamount() / (debitDocument.getSubtotal() + debitDocument.getDiscount())) * pendingRevenue;
            } else {
                if (pendingRevenue >= 0.0d)
                    pendingRevenue = (debitDocument.getTotalamount() / (debitDocument.getSubtotal() + debitDocument.getDiscount())) * pendingRevenue;
            }
        }if(pendingRevenue.isNaN()){
            pendingRevenue = 0d;
        }else {
            pendingRevenue = Double.parseDouble(df.format(pendingRevenue));
        }
        return pendingRevenue;
    }

    private double safeDivide(double numerator, double denominator) {
        return (denominator == 0) ? 0 : numerator / denominator;
    }

    private double roundTo4Decimals(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }



    public List<DebitDocument> getAllByCustomerForCreditNote(Integer customerid) {

        //Customers customer = subscriberService.get(customerid);
        List<DebitDocument> list = IterableUtils.toList(debitDocRepository.pendingDebitDocumentList(customerid));
        List<Integer> ids = list.stream().map(x -> x.getCustpackrelid()).collect(Collectors.toList());
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustPlanStatusAndIdIn("STOP", ids);
        List<Integer> custPlanIds = custPlanMapppingList.stream().map(x -> x.getId()).collect(Collectors.toList());
        list.removeIf(debitDocument -> custPlanIds.contains(debitDocument.getCustpackrelid()));
        list.removeIf(i -> i.getBillrunstatus().equalsIgnoreCase("Cancelled"));
//        list.removeIf(item -> {
//            Double pendingAmt = creditDocRepository.findTotalPendingAmountByDebitDocIdforCN(item.getId());
//            if(pendingAmt!=null) {
//                return (pendingAmt.equals(0.0));
//            }else {
//                return false;
//            }
//        });
        return list;
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.DebitDocument', '1')")
    public List<DebitDocumentPojo> convertResponseModelIntoPojo(List<DebitDocument> debitDocumentList) throws Exception {
        List<DebitDocumentPojo> pojoListRes = new ArrayList<DebitDocumentPojo>();
        if (debitDocumentList != null && debitDocumentList.size() > 0) {
            for (DebitDocument debitDocument : debitDocumentList) {
                if (debitDocument.getBillrunstatus().equalsIgnoreCase("Exported")) {
                    debitDocument.setBillrunstatus("Printed");
                }
                pojoListRes.add(convertDebitDocumentModelToDebitDocumentPojo(debitDocument));
            }
        }
        return pojoListRes.stream().sorted(Comparator.comparing(DebitDocumentPojo::getId).reversed()).collect(Collectors.toList());
    }


    public DebitDocSearchPojo getInvoiceDetails(Integer invoiceId, Integer custId) {
        DebitDocument debitDocument = debitDocRepository.findAllDebitDocSearhcResult(invoiceId, custId);
        List<CustPlanMappping>  custPlanMappping =  debitDocument.getCustomer().getPlanMappingList().stream().filter(i->i.getId().equals(debitDocument.getCustpackrelid())).collect(Collectors.toList());
        DebitDocSearchPojo debitDocSearchPojo = new DebitDocSearchPojo(debitDocument,custPlanMappping);
        return debitDocSearchPojo;
    }

    public TrialDebitDocSearchPojo getInvoiceDetailsTrial(Integer invoiceId, Integer custId) {
        TrialDebitDocument debitDocument = trialDebitDocRepository.findAllDebitDocSearhcResult(invoiceId, custId);
        TrialDebitDocSearchPojo debitDocSearchPojo = new TrialDebitDocSearchPojo(debitDocument);
        return debitDocSearchPojo;
    }


    public List<ViewAdjustedPaymentPojo> FindAdjustedPaymentAgainstBill(Integer invoiceId) {
        List<ViewAdjustedPaymentPojo> viewAdjustedPaymentPojos = new ArrayList<>();
        List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
        if (invoiceId != null) {
            creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocId(invoiceId);
            for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappings) {
                CreditDocument creditDocument = creditDocRepository.findById(creditDebitDocMapping.getCreditDocId()).orElse(null);
                if (Objects.nonNull(creditDocument)) {
                    ViewAdjustedPaymentPojo viewAdjustedPaymentPojo = new ViewAdjustedPaymentPojo();
                    viewAdjustedPaymentPojo.setAdjustedAmount(creditDebitDocMapping.getAdjustedAmount());
                    viewAdjustedPaymentPojo.setReferenceNumber(creditDocument.getCreditdocumentno());
                    viewAdjustedPaymentPojo.setAmount(creditDocument.getAmount());
                    viewAdjustedPaymentPojo.setPaymode(creditDocument.getPaymode());
                    viewAdjustedPaymentPojo.setPaymentdate(creditDocument.getPaymentdate());
                    viewAdjustedPaymentPojo.setType(creditDocument.getType());
                    viewAdjustedPaymentPojo.setStatus(creditDocument.getStatus());
                    viewAdjustedPaymentPojos.add(viewAdjustedPaymentPojo);
                }
            }
        }
        viewAdjustedPaymentPojos = viewAdjustedPaymentPojos.stream().filter(viewAdjustedPaymentPojo -> (viewAdjustedPaymentPojo.getAdjustedAmount() != null /*&& viewAdjustedPaymentPojo.getAdjustedAmount() > 0*/)).collect(Collectors.toList());

        return viewAdjustedPaymentPojos;
    }

    public List<DebitDocument> getAllByCustomer(Integer customerid) {
        Customers customer = customersRepository.getByCustomerId(customerid);
        // BooleanExpression booleanExpression = qDebitDocument.isNotNull().and(qDebitDocument.customer.id.eq(customer.getId())).and(qDebitDocument.billrunstatus.notEqualsIgnoreCase("VOID")).and(qDebitDocument.billrunstatus.notEqualsIgnoreCase("Cancelled")).and(qDebitDocument.totalamount.subtract(qDebitDocument.adjustedAmount.coalesce(0d)).ne((double) 0));
        List<DebitDocument> debitDocuments = debitDocRepository.findAllByTotalamount(customer.getId()).stream().map(debitDocument -> {
            debitDocument.setPendingAmt(creditDocRepository.findTotalPendingAmountByDebitDocId(debitDocument.getId()));
            return debitDocument;
        }).collect(Collectors.toList());

        return IterableUtils.toList(debitDocuments);
    }

    public void saveCustDirectCharge(ChangePlanMessage message) {
        try {
            if (message == null) {
                logger.error("Received null message in saveCustDirectCharge.");
                return;
            }

            if (CollectionUtils.isEmpty(message.getCustChargeDetailsRevenues())) {
                logger.error("CustChargeDetailsRevenues is null or empty.");
                return;
            }

            CustChargeDetailsRevenue firstRevenue = message.getCustChargeDetailsRevenues().get(0);
            if (firstRevenue.getCustomerId() == null) {
                logger.error("Customer ID is null in CustChargeDetailsRevenues.");
                return;
            }

            Customers customers = customersRepository.findById(firstRevenue.getCustomerId()).orElse(null);
            if (customers == null) {
                logger.error("No customer found for ID: " + firstRevenue.getCustomerId());
                return;
            }

            StaffUser staffUser = null;
            if (message.getCreatedById() != null) {
                staffUser = staffUserRepository.findById(message.getCreatedById()).orElse(null);
            }

            Boolean isRenew = false;
            if (!CollectionUtils.isEmpty(message.getCustChargeDetailsRevenues())) {
                List<CustChargeDetails> custChargeDetailsList = new ArrayList<>();
                for (CustChargeDetailsRevenue data : message.getCustChargeDetailsRevenues()) {
                    if (data == null) {
                        logger.warn("Skipping null CustChargeDetailsRevenue.");
                        continue;
                    }

                    isRenew = data.getIsRenew();
                    CustChargeDetails custChargeDetails = new CustChargeDetails(data, customers);
                    custChargeDetailsList.add(custChargeDetails);
                }
                custChargeDetailsRepository.saveAll(custChargeDetailsList);
            }

            if (Boolean.FALSE.equals(isRenew)) {
                CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
                Map<String, Object> data = new HashMap<>();
                data.put(CustomerBillingMessage.CUST_ID, firstRevenue.getCustomerId());
                data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF, message.getCreatedById());
                customerBillingMessage.setData(data);
                customerBillingMessage.setType(message.getType());
                customerBillingMessage.setCustChargeIds(message.getCustChargeIds());

                customerBillingMessage.setCreatedByName((staffUser != null) ? staffUser.getFullName() : "Superadmin");
                customerBillingMessage.setMvnoCustomer(Boolean.TRUE.equals(message.getIsMvnoCustomer()));

                if (message.getIspFromDate() != null) {
                    customerBillingMessage.setIspFromDate(DateTimeUtil.getLocaldateTimefromString(message.getIspFromDate()).toLocalDate());
                    customerBillingMessage.setIspToDate(DateTimeUtil.getLocaldateTimefromString(message.getIspToDate()).toLocalDate());
                }

                if (!CollectionUtils.isEmpty(message.getDebitDocDetailIds())) {
                    customerBillingMessage.setDebitDocDetailIds(message.getDebitDocDetailIds());
                }

                if (CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION.equalsIgnoreCase(customers.getStatus())) {
                    data.put(CustomerBillingMessage.IS_CAF_CUSTOMER_DIRECT_CHARGE, "true");
                }

                messageReceiverWithThread.receiveBillingInvoiceMessageForManual(customerBillingMessage);
            }

        } catch (Exception e) {
            logger.error("Exception in saveCustDirectCharge: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    public void cafToCustomer(CaftoCustomerMessage message) {
        try {
            if(message.getStatus() != null && message.getCafApproveStatus() != null && message.getStatus().equalsIgnoreCase("Active") && message.getCafApproveStatus().equalsIgnoreCase("Approved") )
            {
                Customers customersCaf = customersRepository.findById(message.getCustomerId()).orElse(null);
                customersCaf.setCafApproveStatus(message.getCafApproveStatus() != null ? message.getCafApproveStatus() : null);
                customersCaf.setStatus(message.getStatus() != null ? message.getStatus() : null);
                customersCaf.setFirstActivationDate(LocalDateTime.now());
                customersRepository.save(customersCaf);
                setServiceAddActive(customersCaf);
                Integer count = customerChargeHistoryRepository.countAllByCustomerIdAndAndChargeType(customersCaf.getId(),CommonConstants.CHARGE_TYPE_ADVANCE);
                CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
                Map<String, Object> data = new HashMap<>();
                data.put(CustomerBillingMessage.CUST_ID, message.getCustomerId());
                data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF,message.getLoggedInUser());
                data.put(CustomerBillingMessage.MVNOID,customersCaf.getMvnoId());


                List<Long> invMappingIds = customerInventoryMappingRepo.findAllByCustomerId(message.getCustomerId().longValue());
                if (!CollectionUtils.isEmpty(invMappingIds)) {
                    data.put(CustomerBillingMessage.CUSTOMER_INVENTORY_CAF_TO_CUSTOMER, true);
                }
                if (count>0 && customersCaf.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)){
                    data.put(CustomerBillingMessage.POSTPAIDADVANCE,"Advance");
                    customerBillingMessage.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
                }

                Integer count1 = customerChargeHistoryRepository.countAllByCustomerIdAndAndChargeType(message.getCustomerId(),CommonConstants.CHARGE_TYPE_ADVANCE);
                boolean postPaidInvoiceForPrepaidCharge = false;
                if (customersCaf.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID) && count1>0){
                    postPaidInvoiceForPrepaidCharge = true;
                }
                if ((customersCaf.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.PREPAID)) || postPaidInvoiceForPrepaidCharge) {
                    if (count>0){
                        data.put(CustomerBillingMessage.POSTPAIDADVANCE,"Advance");
                    }
                    customerBillingMessage.setData(data);
                    customerBillingMessage.setType(Constants.INVOICE_TYPE.CREATE_CUSTOMER);
                    customerBillingMessage.setCafCustomerApprove(true);
                    messageReceiverWithThread.receiveBillingInvoiceMessageForManual(customerBillingMessage);
                }
            } else {
                Customers customersCaf = customersRepository.findById(message.getCustomerId()).orElse(null);
                customersCaf.setCafApproveStatus(message.getCafApproveStatus() != null ? message.getCafApproveStatus() : null);
                customersCaf.setStatus(message.getStatus() != null ? message.getStatus() : null);
                customersCaf.setFirstActivationDate(LocalDateTime.now());
                customersRepository.save(customersCaf);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void setServiceAddActive(Customers customers) {
        if (customers != null) {
            List<CustomerServiceMapping> customerServiceMappings = new ArrayList<>();
//            QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
//            BooleanExpression booleanExpression = qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.custId.eq(custID)).and(qCustomerServiceMapping.status.eq("NewActivation"));
            customerServiceMappings = customerServiceMapRepository.findAllByCustIdAndStatus(customers.getId(), "NewActivation");
            if (customerServiceMappings.size() > 0) {
                for (CustomerServiceMapping customerServiceMapping : customerServiceMappings) {
                    customerServiceMapping.setStatus("Active");
                    customerServiceMapping.setNextStaff(null);
                    customerServiceMapping.setNextTeamHierarchyMappingId(null);
                    updateCustomerServiseToActive(customerServiceMapping, customers);
                    customerServiceMapRepository.save(customerServiceMapping);
                }
            }
//            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
//            BooleanExpression expcpr = qCustPlanMappping.isNotNull().and(qCustPlanMappping.customer.id.eq(custID));
            List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerId(customers.getId());
            List<CustPlanMappping> custpacksave = new ArrayList<>();
            for (CustPlanMappping custPlanMappping1 : custPlanMappping) {
                custPlanMappping1.setIsInvoiceCreated(false);
                custPlanMappping1.setDebitdocid(null);
                if(customers.getStatus().equalsIgnoreCase("Active")){
                    custPlanMappping1.setStatus("Active");
                    custPlanMappping1.setCustPlanStatus("Active");
                }
                custpacksave.add(custPlanMappping1);
            }
            custPlanMappingRepository.saveAll(custpacksave);


//            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
//            BooleanExpression expInventoryMapping  = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.customer.id.eq(custID));
//            List<CustomerInventoryMapping> customerInventoryMappingList = (List<CustomerInventoryMapping>) customerInventoryMappingRepo.findAll(expInventoryMapping);
//            List<CustomerInventoryMapping> custinventorySave  = new ArrayList<>();
//            for (CustomerInventoryMapping customerInventoryMapping: customerInventoryMappingList){
//                customerInventoryMapping.setIsInvoiceCreated(false);
//                custinventorySave.add(customerInventoryMapping);
//            }
//            customerInventoryMappingRepo.saveAll(custinventorySave);

//            QCustChargeDetails qCustChargeDetails = QCustChargeDetails.custChargeDetails;
//            BooleanExpression expChargeDtls = qCustChargeDetails.isNotNull().and(qCustChargeDetails.customer.id.eq(custID));
            List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllByCustomerId(customers.getId());
            List<CustChargeDetails> saveCustchargedtls = new ArrayList<>();

            for (CustChargeDetails custChargeDetails : custChargeDetailsList) {
                custChargeDetails.setIsUsed(false);
                saveCustchargedtls.add(custChargeDetails);
            }
            custChargeDetailsRepository.saveAll(saveCustchargedtls);

        }
    }

    public void updateCustomerServiseToActive(CustomerServiceMapping customerServiceMapping, Customers customers) {
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingId(customerServiceMapping.getId());
        for (CustPlanMappping custPlanMappping : custPlanMapppingList) {
            if (custPlanMappping.getIstrialplan()==null ||  !custPlanMappping.getIstrialplan()) {
                custPlanMappping.setIsinvoicestop(false);
            }
            LocalDate startDate = custPlanMappping.getStartDate().toLocalDate();
            Long daysDiff = ChronoUnit.DAYS.between(startDate, LocalDate.now());
//            if (daysDiff > 0) {
            custPlanMappping.setStartDate(custPlanMappping.getStartDate().plusDays(daysDiff));
            if (customers.getParentCustomers() == null) {
                custPlanMappping.setEndDate(custPlanMappping.getEndDate().plusDays(daysDiff));
                custPlanMappping.setExpiryDate(custPlanMappping.getExpiryDate().plusDays(daysDiff));

            }
            custPlanMappping.setStartDate(LocalDateTime.now());
            custPlanMappingRepository.save(custPlanMappping);
//            }
        }
    }

    public void adjustBillToSubisuInvoiceWithCreditNote(Double adjustedAmount, DebitDocument debitDocument) {
        try {
            List<CustPlanMappping> custMappings = custPlanMappingRepository.findAllByDebitdocid(debitDocument.getId());
            if (!CollectionUtils.isEmpty(custMappings)) {
                List<Integer> cprIds = custMappings.stream().map(x -> x.getId()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(cprIds)) {
                    List<CustPlanMappping> mapppings = custPlanMappingRepository.getAllCustPlanMappingByCustCPRList(cprIds);
                    if (!CollectionUtils.isEmpty(mapppings)) {
                        Long debitDocumentId = mapppings.get(0).getDebitdocid();
                        Optional<DebitDocument> subisuDocument = debitDocRepository.findById(debitDocumentId.intValue());
                        if (subisuDocument.isPresent()) {
                            Double newAdjustAmount = (adjustedAmount / debitDocument.getTotalamount()) * subisuDocument.get().getTotalamount();
                            if (newAdjustAmount > 0.0)
                                creditDocService.adjustCreditNoteForBillToSubisu(newAdjustAmount, subisuDocument.get());
                        }
                    }
                }
            }
        }catch (Exception ex) {
            logger.error("Exception on adjust bill to organization amount: "+ex.getMessage());
        }
    }


    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.DebitDocument', '1')")
    public List<DebitShowDocumentPojo> convertResponseModelIntoShowPojo(List<DebitShowDocumentPojo> debitDocumentList) throws Exception {
        List<DebitShowDocumentPojo> pojoListRes = new ArrayList<DebitShowDocumentPojo>();
        if (debitDocumentList != null && debitDocumentList.size() > 0) {
            for (DebitShowDocumentPojo debitDocument : debitDocumentList) {
                if (debitDocument.getBillrunstatus().equalsIgnoreCase("Exported")) {
                    debitDocument.setBillrunstatus("Printed");
                }
                if(debitDocument.getCustStatus() != null && (debitDocument.getCustStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION) || debitDocument.getCustStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING))){
                    TrialDebitDocument debitDocument1 = trialDebitDocRepository.findById(debitDocument.getId()).get();
                    if (debitDocument1.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.UNPAID)) {
                        debitDocument.setRefundAbleAmount("0");
                    } else {
                        debitDocument.setRefundAbleAmount(getRefundAmountShow(debitDocument));
                    }
                } else {
                    DebitDocument debitDocument1 = debitDocRepository.findById(debitDocument.getId()).get();
                    if (debitDocument1.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.UNPAID)) {
                        debitDocument.setRefundAbleAmount("0");
                    } else {
                        debitDocument.setRefundAbleAmount(getRefundAmountShow(debitDocument));
                    }
                }
                pojoListRes.add(debitDocument);
            }
        }
        return pojoListRes.stream().sorted(Comparator.comparing(DebitShowDocumentPojo::getId).reversed()).collect(Collectors.toList());
    }

    public List<DebitShowDocumentPojo> getAllByCustomerShow(Integer customerid) {
        Customers customer = customersRepository.getByCustomerId(customerid);
        List<DebitShowDocumentPojo> debitDocuments = new ArrayList<>();
        if(customer != null && customer.getStatus() != null && (customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION) || customer.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING))) {
            debitDocuments = trialDebitDocRepository.findAllInvoiceByTotalamount(customerid).stream().map(debitDocument -> {
                debitDocument.setPendingAmt(creditDocRepository.findTotalPendingAmountByDebitDocId(debitDocument.getId()));
                return debitDocument;
            }).collect(Collectors.toList());
        } else {
            debitDocuments = debitDocRepository.findAllInvoiceByTotalamount(customerid).stream().map(debitDocument -> {
                debitDocument.setPendingAmt(creditDocRepository.findTotalPendingAmountByDebitDocId(debitDocument.getId()));
                return debitDocument;
            }).collect(Collectors.toList());
        }
        return IterableUtils.toList(debitDocuments);
    }

    private String getRefundAmountShow(DebitShowDocumentPojo pojo) {
        Double pendingRevenue = 0d;
        DecimalFormat df = new DecimalFormat("#.00");
        DebitDocument debitDocument = debitDocRepository.findById(pojo.getId()).orElse(null);
        Double amount = getPendingRevenueWithTaxAtCurrentDateShow(debitDocument);
        if (amount != null)
            return amount.toString();
        else
            return "0.0";
    }

    public Double getPendingRevenueWithTaxAtCurrentDateShow(DebitDocument debitDocument) {
        Double pendingRevenue = 0d;
        DecimalFormat df = new DecimalFormat("#.00");
        if (debitDocument == null)
            return 0d;
        List<CustomerDBR> customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(LocalDate.now(), debitDocument);
        pendingRevenue = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(LocalDate.now())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));

        if (LocalDate.now().isBefore(debitDocument.getStartdate().toLocalDate())) {
            customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(debitDocument.getStartdate().toLocalDate(), debitDocument);
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

    public ChildInvoiceDetails getChildInvoiceDetails(List<Integer> childIds,Integer renewalId,LocalDate strBillDate) {
        try {

            ChildInvoiceDetails childInvoiceDetails = new ChildInvoiceDetails();
            List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
            List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
            List<CustomerChargeHistory> customerChargeHistorieList = new ArrayList<>();
            List<Long> custServiceIdList  = new ArrayList<>();

            for (Integer custId : childIds) {
                Optional<Customers> customersOptional = customersRepository.findById(custId);
                logger.info("Initiating Invoice Details process for Child customer :  "+ customersOptional.get().getUsername());
                Customers customers = customersOptional.get();
                List<CustPlanMappping> custPlanMapppings = customers.getPlanMappingList();

                if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                    if (!customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID))
                        custPlanMapppings.removeIf(custPlanMappping -> !custPlanMappping.getCustPlanStatus().equalsIgnoreCase(Constants.CUSTOMER_STATUS_ACTIVE));
                    custPlanMapppings.removeIf(CustPlanMappping::getIsInvoiceCreated);
                    if (renewalId != null) {
                        custPlanMapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getRenewalId() != null && custPlanMappping.getRenewalId().equals(renewalId)).collect(Collectors.toList());
                    }
                }


                if (CollectionUtils.isEmpty(custPlanMapppings)) {
                    logger.error("Child Customer does not have any plan mapping! with userName: " + customersOptional.get().getUsername());
                    return null;
                }
                custPlanMapppingList.addAll(custPlanMapppings);


                //Direct charge during customer creation
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
                List<Integer> planIds = custPlanMapppings.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
                List<Integer> csmIds = custPlanMapppings.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
                List<Integer> custPlanIds = custPlanMapppings.stream().map(CustPlanMappping::getId).collect(Collectors.toList());


                List<Long> custServiceIds = new ArrayList<>();
                List<CustomerServiceMapping> customerServiceMappings = customers.getCustomerServiceMappingList();
                if (!CollectionUtils.isEmpty(customerServiceMappings)) {
                    customerServiceMappings = customerServiceMappings.stream().filter(custSerMap -> csmIds.contains(custSerMap.getId())).collect(Collectors.toList());
                    custServiceIds = customerServiceMappings.stream().map(CustomerServiceMapping::getServiceId).collect(Collectors.toList());
                }
                customerServiceMappingList.addAll(customerServiceMappings);
                custServiceIdList.addAll(custServiceIds);


                List<Integer> chargeIds = postpaidPlanChargeRepo.getChargeListByPlanIdList(planIds);
                List<CustomerChargeHistory> customerChargeHistories = customerChargeHistoryRepository.findAllByCustomerIdAndChargeIdInAndCustPlanMapppingIdIn(custId, chargeIds, custPlanIds);
                if (CollectionUtils.isEmpty(customerChargeHistories)) {
                    logger.error("Child Customer does not have any charge mapping! with userName: " + customersOptional.get().getUsername());
                    return null;
                }

                customerChargeHistories = customerChargeHistories.stream().filter(x -> !(x.getChargeType().equalsIgnoreCase(Constants.CHARGE_TYPE_ONE_TIME) && x.getIsFirstChargeApply().equals(true))).collect(Collectors.toList());
                if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)){
                    LocalDate finalStrBillDate = strBillDate;
                    customerChargeHistories = customerChargeHistories.stream().filter(x ->  x.getNextBillDate().isEqual(finalStrBillDate)).collect(Collectors.toList());
                }
                customerChargeHistorieList.addAll(customerChargeHistories);
                logger.info("Prepared Invoice Details process for Child customer :  "+ customersOptional.get().getUsername() +" and " + " added required List to parent invoice list" );
            }

            childInvoiceDetails.setCustPlanMapppings(custPlanMapppingList);
            childInvoiceDetails.setCustServiceIdList(custServiceIdList);
            childInvoiceDetails.setCustomerChargeHistories(customerChargeHistorieList);
            childInvoiceDetails.setCustomerServiceMappings(customerServiceMappingList);
            return childInvoiceDetails;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public DebitDocumentPojoCreditNote convertDebitDocumentModelToDebitDocumentPojoCreditNote(DebitDocument debitDocument) throws Exception {

        DebitDocumentPojoCreditNote pojo = null;
        if (debitDocument != null) {
            pojo = new DebitDocumentPojoCreditNote();
            pojo.setId(debitDocument.getId());
            pojo.setDocnumber(debitDocument.getDocnumber());
            pojo.setTax(debitDocument.getTax());
            pojo.setTotalamount(debitDocument.getTotalamount());
            pojo.setAdjustedAmount(debitDocument.getAdjustedAmount());
            pojo.setRefundAbleAmount(getRefundAmount(pojo.getId()));
            pojo.setCreatedByName(debitDocument.getCreatedByName());
            List<DebitDocDetails> details = new ArrayList<>();
            List<DebitDocDetails> debitDocDetails = debitDocRepository.debitDocDetailsByDebitDocId(debitDocument.getId());
            Map<Integer, List<DebitDocDetails>> collect = debitDocDetails.stream().collect(Collectors.groupingBy(DebitDocDetails::getChargeid));
            for (Map.Entry<Integer, List<DebitDocDetails>> entry : collect.entrySet()) {
                List<DebitDocDetails> list = entry.getValue();
                Double subtotal = list.stream().mapToDouble(x -> x.getSubtotal()).sum();
                Double total = list.stream().mapToDouble(x -> x.getTotalamount()).sum();
                Double discount = list.stream().mapToDouble(x -> x.getDiscount()).sum();
                Double tax = list.stream().mapToDouble(x -> x.getTax()).sum();
                list.get(0).setSubtotal(subtotal);
                list.get(0).setTotalamount(total);
                list.get(0).setDiscount(discount);
                list.get(0).setTax(tax);
                details.add(list.get(0));
            }
//            List<DebitDocumentInventoryRel> debitDocumentInventoryRels = debitDocument.getDebitDocumentInventoryRels();
//            if (!CollectionUtils.isEmpty(debitDocumentInventoryRels)) {
//                pojo.setDebitDocumentInventoryRels(debitDocumentInventoryRels);
//            }
            pojo.setStatus(debitDocument.getStatus());
            if (debitDocument.getPaymentowner() != null && debitDocument.getPaymentowner() != "") {

            } else if (debitDocument.getStaffid() != null) {
                StaffUser staffUser = staffUserRepository.findById(debitDocument.getStaffid()).get();
            }
        }
        return pojo;
    }

    @PreAuthorize("hasPermission('com.savbill.apigw.model.postpaid.DebitDocument', '1')")
    public List<DebitDocumentPojoCreditNote> convertResponseModelIntoPojoCreditNote(List<DebitDocument> debitDocumentList) throws Exception {
        List<DebitDocumentPojoCreditNote> pojoListRes = new ArrayList<DebitDocumentPojoCreditNote>();
        if (debitDocumentList != null && debitDocumentList.size() > 0) {
            for (DebitDocument debitDocument : debitDocumentList) {
                if (debitDocument.getBillrunstatus().equalsIgnoreCase("Exported")) {
                    debitDocument.setBillrunstatus("Printed");
                }
                pojoListRes.add(convertDebitDocumentModelToDebitDocumentPojoCreditNote(debitDocument));
            }
        }
        return pojoListRes.stream().sorted(Comparator.comparing(DebitDocumentPojoCreditNote::getId).reversed()).collect(Collectors.toList());
    }

    public void billToOrg(AppproveOrgInvoiceMessage message) {
        try{
            if (message.getDebitdocId() != null) {
                DebitDocument debitDocument = debitDocRepository.findById(message.getDebitdocId().intValue()).orElse(null);
                if (debitDocument != null) {
                    List<CustPlanMappping> customerPlanMappingsForOrg = (List<CustPlanMappping>) custPlanMappingRepository.findAllByDebitdocid(message.getDebitdocId());

                    CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid()).get();
                    CustPlanMappping actualCustomerPlanMapping = custPlanMappingRepository.findById(custPlanMappping.getCustRefId()).get();
                    Customers acualCust = actualCustomerPlanMapping.getCustomer();
                    if (!message.getIsApproveRequest()){
                        debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                        debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                        debitDocRepository.save(debitDocument);

                        Long debitDocIds = actualCustomerPlanMapping.getDebitdocid();
                        List<CustPlanMappping> custPlanMappping1 = new ArrayList<>();
                        if (debitDocIds != null) {
                            {
                                custPlanMappping1 = custPlanMappingRepository.findAllByDebitdocid(debitDocIds.intValue());
                                Boolean flag = true;
                                if (custPlanMappping1.size() > 0) {
                                    DebitDocument customerDebitDoc = debitDocRepository.findById(debitDocIds.intValue()).get();
                                    List<CustPlanMapppingDto> expiredCustPackRel = new ArrayList<>();
                                    for (CustPlanMappping custplanids : custPlanMappping1) {
                                        Optional<DebitDocument> customerDebitDocOptonal = debitDocRepository.findById(custplanids.getDebitdocid().intValue());
                                        if (customerDebitDocOptonal.isPresent() && flag) {
                                            debitDocRepository.save(customerDebitDocOptonal.get());
                                            customerDebitDocOptonal.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                            customerDebitDocOptonal.get().setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                            flag = false;
                                        }

                                        custplanids.setCustPlanStatus(CommonConstants.STOP_STATUS);
                                        custplanids.setStartDate(LocalDateTime.now());
                                        custplanids.setEndDate(LocalDateTime.now());
                                        custplanids.setExpiryDate(LocalDateTime.now());
                                        custPlanMappingRepository.save(custplanids);
                                        CustPlanMapppingDto custPlanMapppingDto =new  CustPlanMapppingDto(custplanids);
                                        expiredCustPackRel.add(custPlanMapppingDto);
                                    }
                                    CreditDocument creditDocumentForCust = creditDocService.creatCreditNotAsPerService(customerDebitDoc,null,acualCust.getCustomerServiceMappingList(),"service Stop", false, null, null, null, null,null);
                                    OrganizationInvoiceRejectMesssage organizationInvoiceRejectMesssage = new OrganizationInvoiceRejectMesssage();
                                    organizationInvoiceRejectMesssage.setCustPlanMapppingDtos(expiredCustPackRel);
                                    kafkaMessageSender.send(new KafkaMessageData(organizationInvoiceRejectMesssage, OrganizationInvoiceRejectMesssage.class.getSimpleName()));
//                                    messageSender.send(organizationInvoiceRejectMesssage,SharedDataConstants.QUEUE_REJECT_ORG_INVOICE);
                                }
                            }
                        }
                    }
                    else
                    {
                        createCreditNote(debitDocument, "invoice", CommonConstants.TRANS_CATEGORY_PAYMENT, "Automatic Payment for business promotion invoice..", CommonConstants.PAYMENT_MODE.BUSINESS_PROMOTION, false);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public void createCreditNote(DebitDocument debitDocument, String payType, String type, String remarks, String mode, boolean isDbrRequired) {
        try {
            CreditDocument creditDocument = new CreditDocument();
            creditDocument.setAmount(debitDocument.getTotalamount());
            creditDocument.setCustomer(debitDocument.getCustomer());
            creditDocument.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
            List<Integer> invoiceId = new ArrayList<>();
            invoiceId.add(debitDocument.getId());
            creditDocument.setInvoiceId(debitDocument.getId());
            creditDocument.setPaymentdate(LocalDate.now());
            creditDocument.setPaymode(mode);
            creditDocument.setPaytype(payType);
            creditDocument.setType(type);
            creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            creditDocument.setIsDelete(false);
            creditDocument.setMvnoId(debitDocument.getCustomer().getMvnoId());
            creditDocument.setBuID(debitDocument.getCustomer().getBuId());
            creditDocument.setRemarks(remarks);
            creditDocument.setTdsamount(0d);
            creditDocument.setAbbsAmount(0d);
            creditDocument.setLcoid(debitDocument.getLcoId());
            creditDocument.setCreatedById(debitDocument.getCreatedById());
            creditDocument.setCreatedByName(debitDocument.getCreatedByName());
            creditDocument.setXmldocument(this.creditDocService.assemblePaymentXML(creditDocument, CommonConstants.ADDR_TYPE_PRESENT));
            if (type.equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_PAYMENT) && mode.equalsIgnoreCase(CommonConstants.PAYMENT_MODE.BUSINESS_PROMOTION))
                creditDocument.setCreditdocumentno(creditDocService.getPaymentInvoiceNo());
            else
                creditDocument.setCreditdocumentno(creditDocService.getInvoiceNo());

            DecimalFormat df = new DecimalFormat("#.00");
            if (debitDocument.getAdjustedAmount() != null)
                creditDocument.setAmount(debitDocument.getTotalamount() - debitDocument.getAdjustedAmount());
            else
                creditDocument.setAmount(debitDocument.getTotalamount());
            creditDocument.setAdjustedAmount(creditDocument.getAmount());


            creditDocument = creditDocRepository.save(creditDocument);
            if (debitDocument.getAdjustedAmount() != null)
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + creditDocument.getAmount());
            else
                debitDocument.setAdjustedAmount(creditDocument.getAmount());
            debitDocRepository.save(debitDocument);

            CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
            creditDebtMappingRepository.save(creditDebitDocMapping);
            creditDocService.addLedgeAfterApproval(creditDocument);
        } catch (Exception e) {
            throw new RuntimeException("Exception when creating credit note for invoice: " + debitDocument.getId());
        }
    }

    public void adjustAllPaymentAgainstInvoice(SendOnlinePaymentRevenueMessage message) throws Exception {
        Map<String, Object> customerData = message.getCustomerData();

        if(customerData.get("custid") != null && customerData.get("custid").toString().length() > 0){
            if(customerData.get("amount") != null && customerData.get("amount").toString().length() > 0){
                List<DebitDocument> debitDocumentList = debitDocRepository.findByCustomerId(Integer.parseInt(customerData.get("custid").toString()));
                debitDocumentList = debitDocumentList.stream().filter(debitDocument1 -> !debitDocument1.getBillrunstatus().equalsIgnoreCase("Cancelled") && !debitDocument1.getBillrunstatus().equalsIgnoreCase("VOID") && debitDocument1.getTotalamount() - debitDocument1.getAdjustedAmount() > 0).collect(Collectors.toList());
                if(!debitDocumentList.isEmpty()){
                    RecordPaymentPojo recordPaymentPojo = creditDocService.createPaymentFromAmount(Integer.parseInt(customerData.get("custid").toString()) , Double.parseDouble(customerData.get("amount").toString()),"FlutterWave",debitDocumentList); /**Create a record payment from amount**/
                    Optional<Customers> customers = customersRepository.findById(Integer.parseInt(customerData.get("custid").toString()));
                    if(customers.isPresent()) {
                        Integer mvnoId = customers.get().getMvnoId();
                        List<Long> buIds = new ArrayList<>();
                        if(customers.get().getBuId() != null){
                            buIds.add(customers.get().getBuId());
                        }
                        if(buIds.isEmpty()){
                            buIds = null;
                        }
                        Integer partnerId = customers.get().getPartner();
                        Integer getCreatedById = customers.get().getCreatedById();
                        String getcreatedByName =customers.get().getCreatedByName();
                        Boolean isLco = false;
                        creditDocService.save(recordPaymentPojo, false, false, false, mvnoId, partnerId, buIds, isLco, getCreatedById, getcreatedByName);
                        for(DebitDocument debitDocument1 : debitDocumentList) {
                            List<Integer> invoiceids = new ArrayList<>();
                            invoiceids.add(debitDocument1.getId());
                            List<CreditDocument> getAllCreditDoc = creditDocRepository.findAllByInvoiceIdIn(invoiceids);
                            if (!getAllCreditDoc.isEmpty()) {
                                creditDocService.addPaymentInCustomerLedger(customers.get(), getAllCreditDoc.get(getAllCreditDoc.size() - 1));  /**This method add payment in customer ledger**/
                                CreditDebitMappingPojo creditDebitDocMappingPojo = new CreditDebitMappingPojo();
                                creditDebitDocMappingPojo.setInvoiceId(debitDocument1.getId());
                                CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
                                creditDebitDataPojo.setAmount(debitDocument1.getTotalamount());
                                creditDebitDataPojo.setId(getAllCreditDoc.get(getAllCreditDoc.size() - 1).getId());
                                List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
                                creditDebitDataPojoList.add(creditDebitDataPojo);
                                creditDebitDocMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
                                creditDocService.adjustManualPaymentToInvoice(creditDebitDocMappingPojo);
                            }
                        }

                    }
                }
                else{
                    logger.error("either all invoice is void or cancelled or no invoice forund for customer");
                }
            }
            else{
                logger.error("can not adjust amount because cms amount parameter is null");
            }
        }
        else{
            logger.error("can not adjust amount because cms customer parameter is null");
        }

    }

    public void sendInvoiceEmail(Integer debitdocid) throws Exception{
        Optional<DebitDocument> debitDocument = debitDocRepository.findById(debitdocid);
        if(debitDocument.isPresent()) {
            Resource resource = null;
            String billDir;
            Path billPath;
            Integer mvnoId=debitDocument.get().getCustomer().getMvnoId();
//            Integer mvnoId = jwtUtil.getLoggedInUser().getMvnoId();
            billDir = clientServiceRepository.findValueByNameAndMvnoId(Constants.PATHS.PDF_READ_PATH,mvnoId);
            List<String> paths = Arrays.asList(billDir.split(","));
            String InvoiceNo = debitDocument.get().getCustomer().getId() + File.separator + debitDocument.get().getDocnumber() + ".pdf";
            logger.info("Bill Dir is :" + billDir);
            for (String path : paths) {
                billPath = Paths.get(path).toAbsolutePath().normalize();
                Path filePath = billPath.resolve(InvoiceNo).normalize();
                logger.info("Bill Path is :" + filePath.toString());
                resource = new UrlResource(filePath.toUri());
                if (!resource.exists()) {
                    logger.info("File not found " + InvoiceNo);
                    throw new CustomValidationException(APIConstants.FAIL, "File not found", null);
                }
                Optional<Customers> customers = customersRepository.findById(debitDocument.get().getCustomer().getId());
                if (customers.isPresent()) {
                    String finalFilePath = billPath + File.separator + debitDocument.get().getCustomer().getId().toString();
                    logger.info("File path ::: " + finalFilePath);
                    logger.info("File name ::: " + resource.getFilename());
                    buildSendEmailMessage(customers.get(), finalFilePath, resource.getFilename());
                } else {
                    logger.error("customer is not find by cust id");
                    throw new CustomValidationException(APIConstants.FAIL, "customer is not find by id", null);
                }
                break;


            }
        }
        else{
            logger.error("debit doc not find by id");
            throw new CustomValidationException(APIConstants.FAIL, "debit document not found with id", null);
        }
    }

    public void buildSendEmailMessage(Customers customers , String filepath , String filename){
        CustomerInvoiceMessage customerInvoiceMessage = new CustomerInvoiceMessage(customers.getMobile() , customers.getCountryCode() , customers.getEmail() , customers.getUsername() , filename , filepath , customers.getMvnoId() , customers.getBuId());
        Gson gson = new Gson();
        gson.toJson(customerInvoiceMessage);
        kafkaMessageSender.send(new KafkaMessageData(customerInvoiceMessage,CustomerInvoiceMessage.class.getSimpleName()));
//        messageSender.send(customerInvoiceMessage , RabbitMqConstants.QUEUE_SEND_INVOICE_TO_NOTIFICATION);
    }


    public void buildSendEmailMessage(CustomerData customers , String filepath , String filename){
        CustomerInvoiceMessage customerInvoiceMessage = new CustomerInvoiceMessage(customers.getMobile() , customers.getCountryCode() != null ? customers.getCountryCode().toString() : "", customers.getEmail() , customers.getUsername() , filename , filepath , customers.getMvnoId() , customers.getBuId());
        Gson gson = new Gson();
        gson.toJson(customerInvoiceMessage);
        kafkaMessageSender.send(new KafkaMessageData(customerInvoiceMessage,CustomerInvoiceMessage.class.getSimpleName()));
//        messageSender.send(customerInvoiceMessage , RabbitMqConstants.QUEUE_SEND_INVOICE_TO_NOTIFICATION);
    }


    public void savePaymentOnline(TrialDebitDocument finalDebitDocument, Customers customers,CustomerBillingMessage customerBillingMessage) {
        try {
            logger.debug("Initializing savePaymentOnline and Credit Doc entry is being set for Customer : " + customers.getUsername() + " for TrialDebitDocument with Id: "+ finalDebitDocument.getId());
            CreditDocument savedCreditDocument = null;
            RecordPaymentPojo pojo = new RecordPaymentPojo();
            StaffUser loggedInUser = null;
            if (customers.getCreatedById() != null) {
                loggedInUser = staffUserRepository.findById(customers.getCreatedById()).orElse(null);
            }
            List<CreditDocMessage> creditDocMessage = new ArrayList<>();
            if (customers.getMvnoId() != null) {
                pojo.setMvnoId(customers.getMvnoId());
            }

            CreditDocument doc = new CreditDocument();
            doc.setCustomer(customers);
            doc.setCreatedByName(customers.getUsername());
            doc.setPaymode("Online");
            doc.setPaymentdate(LocalDate.now());
            doc.setAmount(finalDebitDocument.getTotalamount());
            doc.setStatus(CommonConstants.PAYMENT_CONDITION.ONLINE_PAYMENT_PENDING);
            doc.setRemarks("Verified");
            doc.setIsDelete(false);
            doc.setTdsamount(0.0);
            doc.setMvnoId(pojo.getMvnoId());
            if (customers.getStatus().equals(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION)){
                doc.setTrialDebitdocId(finalDebitDocument.getId());
            }else {
                doc.setInvoiceId(finalDebitDocument.getId());
            }
            doc.setPaytype("Invoice");
            doc.setType("Payment");
            doc.setAdjustedAmount(finalDebitDocument.getTotalamount());
            doc.setRemainingAmount(0.0);
            doc.setCreatedate(LocalDateTime.now());
            doc.setUpdatedate(LocalDateTime.now());
            if (loggedInUser!=null) {
                doc.setCreatedByName(loggedInUser.getFirstname());
            }
            if (customerBillingMessage.getReferenceNo()!=null){
                doc.setReferenceno(customerBillingMessage.getReferenceNo());
                logger.debug("Reference No..  for online payment is "+customerBillingMessage.getReferenceNo()+" for Customer : " + customers.getUsername() + " for TrialDebitDocument with Id: "+ finalDebitDocument.getId());
            }else {
                doc.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
            }
            doc = creditDocRepository.save(doc);

            BudPayPaymentMessage budPayPaymentMessage = new BudPayPaymentMessage(customers.getId(),doc.getReferenceno(),doc.getId());
            kafkaMessageSender.send(new KafkaMessageData(budPayPaymentMessage,BudPayPaymentMessage.class.getSimpleName()));
            logger.debug("Message sent to CMS having CreditDoc details  for Customer : " + customers.getUsername() );
//            messageSender.send(budPayPaymentMessage,RabbitMqConstants.QUEUE_SEND_BUD_PAYMENT_CREDIT_TO_REVENUE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DebitDocument paymentAdjustmentForCaptiPortalCust(DebitDocument debitDocument,List<CreditDocument> getAllCreditDoc) {
        try {
            logger.info("Adjusting Payment for   debitdoc ID :  "+ debitDocument.getId() + " if payment done during  CAF creation or customer portal");
            List<CreditDocument> creditDocuments =  new ArrayList<>();
            for (CreditDocument doc :getAllCreditDoc) {
                doc.setAdjustedAmount(0.0);
                doc.setRemainingAmount(0.0);
                doc.setInvoiceId(debitDocument.getId());
                creditDocuments.add(doc);
            }
            creditDocuments = creditDocRepository.saveAll(creditDocuments);
            Customers customers = debitDocument.getCustomer();
            customers.setWalletbalance(debitDocument.getTotalamount()-debitDocument.getAdjustedAmount());
            customersRepository.save(customers);
            if (!creditDocuments.isEmpty()) {
                creditDocService.addPaymentInCustomerLedger(customers,creditDocuments); /**for ledger correction**/

                CreditDebitMappingPojo creditDebitDocMappingPojo = new CreditDebitMappingPojo();
                List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
                creditDebitDocMappingPojo.setInvoiceId(debitDocument.getId());
                for (CreditDocument  creditDocument : creditDocuments) {
                    CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
                    creditDebitDataPojo.setAmount(creditDocument.getAmount());
                    creditDebitDataPojo.setId(creditDocument.getId());
                    creditDebitDataPojoList.add(creditDebitDataPojo);
                }
                creditDebitDocMappingPojo.setCreditDocumentList(creditDebitDataPojoList);

                creditDocService.adjustManualPaymentToInvoice(creditDebitDocMappingPojo);
                DebitDocument debitDocumentContainingCDmapping = debitDocRepository.findById(debitDocument.getId()).get();
                debitDocumentContainingCDmapping.setTotaldue(debitDocument.getTotalamount()-debitDocument.getAdjustedAmount());
                debitDocument = debitDocRepository.save(debitDocumentContainingCDmapping);
//                debitDocument.setTotaldue(debitDocument.getTotalamount()-debitDocument.getAdjustedAmount());
//                debitDocRepository.save(debitDocument);
                logger.info("Payment Adjusted for   debitdoc ID :  "+ debitDocument.getId() + " if payment done during  CAF creation or customer portal");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  debitDocument;

    }

    public void updateProvisionalPortalCustomer(BudPayPaymentMessage message) {
        try {
            String customerStatus = customersRepository.findStatusById(message.getCustomerId());
            if (customerStatus.equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION)) {
                TrialDebitDocument trialDebitDocument = trialDebitDocRepository.findByReferenceNo(message.getReferenceNumber());
                trialDebitDocument.setAdjustedAmount(trialDebitDocument.getTotalamount());
                trialDebitDocument.setTotaldue(0.0);
                trialDebitDocument.setPaymentStatus("Fully Paid");
                trialDebitDocRepository.save(trialDebitDocument);
                CreditDocument  creditDocument = creditDocRepository.findAllByReferenceno(message.getReferenceNumber());
                creditDocument.setStatus(CommonConstants.PAYMENT_CONDITION.ONLINE_PAYMENT_APPROVED);
                creditDocument.setCreditdocumentno(creditDocService.getPaymentInvoiceNo());
                creditDocument.setReciptNo(creditDocument.getCreditdocumentno());
                creditDocRepository.save(creditDocument);
            }else {
                DebitDocument debitDocument  = debitDocRepository.findByReferenceNo(message.getReferenceNumber());
                List<String> statuses = Arrays.asList("Online Payment Pending", "Payment Failed");
                List<CreditDocument> getAllCreditDoc =  creditDocRepository.findAllByCustomerAndStatusIn(debitDocument.getCustomer().getId(),statuses,message.getReferenceNumber());
                if (message.getPaymentStatus().equalsIgnoreCase("Success") && !getAllCreditDoc.isEmpty()){
                    paymentAdjustmentForCaptiPortalCust(debitDocument, getAllCreditDoc);
                    List<CreditDebitDocMapping> creditDebitDocMapping = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
                    CreditDebitDocMessage creditDebitDocMessage = new CreditDebitDocMessage();
                    creditDebitDocMessage.setCreditDebitDocMappingList(creditDebitDocMapping);
                    kafkaMessageSender.send(new KafkaMessageData(creditDebitDocMessage, CreditDebitDocMessage.class.getSimpleName())); kafkaMessageSender.send(new KafkaMessageData(creditDebitDocMessage, CreditDebitDocMessage.class.getSimpleName()));
//                    messageSender.send(creditDebitDocMessage,RabbitMqConstants.QUEUE_CREDIT_DEBIT_DOC_TO_CMS);
                }else{
                    for (CreditDocument doc : getAllCreditDoc){
                        doc.setStatus("Payment Failed");
                        doc.setInvoiceId(debitDocument.getId());
                        creditDocRepository.save(doc);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Page<MvnoDebitDocDetailsPojo> invoiceListOfMvnoCustomer(Integer invoiceId, PaginationRequestDTO requestDTO) {
        try {

            List<Integer> custDebitDocIds = debitDocDetailRepository.findDebitDocIdByMvnoDebitDocId(invoiceId);
            Pageable pageable = PageRequest.of(requestDTO.getPage()-1, requestDTO.getPageSize());
            Page<MvnoDebitDocDetailsPojo> mvnoDebitDocDetailsPojos = debitDocRepository.findInvoiceDetailsofCusMvno(custDebitDocIds,pageable);


            return mvnoDebitDocDetailsPojos;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public PartnerPlanCommissionDetail getPartnerPlanInformationFromPartnerLedger(List<PartnerLedgerDetails> commissionList, List<PartnerLedgerDetails> revertCommissionList) {
        PartnerPlanCommissionDetail partnerPlanCommissionDetails = new PartnerPlanCommissionDetail();
        List<PartnerPlanWiseCommission> list=new ArrayList<>();

        Set<String> planIds = commissionList.stream().filter(commission -> commission.getPlanid() != null && commission.getPlanGroupId()==null).map(PartnerLedgerDetails::getPlanid).distinct().collect(Collectors.toSet());
        Set<Integer> planGroupIds = commissionList.stream().filter(commission -> commission.getPlanGroupId()!=null).map(PartnerLedgerDetails::getPlanGroupId).collect(Collectors.toSet());

        for (String planId : planIds) {
            List<PartnerLedgerDetails> commissionList1 = commissionList.stream().filter(commission -> commission.getPlanid() != null && commission.getPlanGroupId()==null && commission.getPlanid().equalsIgnoreCase(planId)).collect(Collectors.toList());
            List<PartnerLedgerDetails> revertCommissionList1 = commissionList.stream().filter(commission -> commission.getPlanid() != null && commission.getPlanGroupId()==null && commission.getPlanid().equalsIgnoreCase(planId)).collect(Collectors.toList());
            List<String> debitDocumentId=commissionList.stream().filter(commission -> commission.getPlanid() != null && commission.getPlanGroupId()==null && commission.getPlanid().equalsIgnoreCase(planId)).map(x->x.getInvoiceNo()).distinct().collect(Collectors.toList());
            Double reverseCommissionAmount=revertCommissionList1.stream().filter(commission->commission.getInvoiceNo()!=null && commission.getInvoiceNo().equalsIgnoreCase(debitDocumentId.get(0))).mapToDouble(x->x.getAmount() + x.getCommission()).sum();

            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(Integer.valueOf(planId));
            PartnerPlanWiseCommission planInformation=new PartnerPlanWiseCommission();
            planInformation.setPlanOrPlanGroupName(postpaidPlan.get().getDisplayName());
            planInformation.setIsPlanGroup(false);
            planInformation.setCommissionAmount(Double.parseDouble(new DecimalFormat("##.##").format(commissionList1.stream().filter(x->x.getCommission()!=null).mapToDouble(x->x.getCommission()).sum() - reverseCommissionAmount)));
            planInformation.setTotalCustomerCount(commissionList1.stream().filter(x->x.getCustid()!=null).mapToInt(x->x.getCustid()).distinct().count());
            list.add(planInformation);
        }

        for (Integer planGroupId : planGroupIds) {
            List<PartnerLedgerDetails> commissionList1 = commissionList.stream().filter(commission -> commission.getPlanGroupId()!=null && commission.getPlanGroupId().equals(planGroupId)).collect(Collectors.toList());
            List<PartnerLedgerDetails> revertCommissionList1 = commissionList.stream().filter(commission -> commission.getPlanGroupId()!=null && commission.getPlanGroupId().equals(planGroupId)).collect(Collectors.toList());
            List<String> debitDocumentId=commissionList.stream().filter(commission -> commission.getPlanGroupId()!=null && commission.getPlanGroupId().equals(planGroupId)).map(x->x.getInvoiceNo()).distinct().collect(Collectors.toList());
            Double reverseCommissionAmount=revertCommissionList1.stream().filter(commission->commission.getInvoiceNo()!=null && commission.getInvoiceNo().equalsIgnoreCase(debitDocumentId.get(0))).mapToDouble(x->x.getAmount() + x.getCommission()).sum();

            PartnerPlanWiseCommission planInformation=new PartnerPlanWiseCommission();
            planInformation.setPlanOrPlanGroupName(commissionList1.get(0).getPlanGroupName());
            planInformation.setIsPlanGroup(true);
            planInformation.setCommissionAmount(Double.parseDouble(new DecimalFormat("##.##").format(commissionList1.stream().filter(x->x.getCommission()!=null).mapToDouble(x->x.getCommission()).sum() - reverseCommissionAmount)));
            planInformation.setTotalCustomerCount(commissionList1.stream().filter(x->x.getCustid()!=null).mapToInt(x->x.getCustid()).distinct().count());
            list.add(planInformation);
        }

        partnerPlanCommissionDetails.setPartnerPlanWiseCommissionList(list);
        return partnerPlanCommissionDetails;
    }

    public void sendInvoiceEmailFromScheduler(Integer debitDocId,String debitDocNumber, CustomerData customerData) throws Exception{
        if(debitDocId!=null) {
            Resource resource = null;
            String billDir;
            Path billPath;
            Integer mvnoId=customerData.getMvnoId();
            billDir = clientServiceRepository.findValueByNameAndMvnoId(Constants.PATHS.PDF_READ_PATH,mvnoId);
            List<String> paths = Arrays.asList(billDir.split(","));
            String InvoiceNo = customerData.getId() + File.separator + debitDocNumber + ".pdf";
            logger.info("Bill Dir is :" + billDir);
            for (String path : paths) {
                billPath = Paths.get(path).toAbsolutePath().normalize();
                Path filePath = billPath.resolve(InvoiceNo).normalize();
                logger.info("Bill Path is :" + filePath.toString());
                resource = new UrlResource(filePath.toUri());

                //Optional<Customers> customers = customersRepository.findById(debitDocument.getCustomer().getId());
                if (customerData!=null) {
                    String finalFilePath = billPath + File.separator + customerData.getId().toString();
                    logger.info("File path ::: " + finalFilePath);
                    logger.info("File name ::: " + resource.getFilename());
                    if (!resource.exists()) {
                        buildSendEmailMessage(customerData, null,null);
                    }
                    buildSendEmailMessage(customerData, finalFilePath, resource.getFilename());
                } else {
                    logger.error("customer is not find by cust id");
                    throw new CustomValidationException(APIConstants.FAIL, "customer is not find by id", null);
                }
                break;
            }
        }
        else{
            logger.error("debit doc not find by id");
            throw new CustomValidationException(APIConstants.FAIL, "debit document not found with id", null);
        }
    }

    public void savePaymentForCaf(TrialDebitDocument finalDebitDocument, Customers customers,CustomerBillingMessage customerBillingMessage) {
        try {
            CreditDocument savedCreditDocument = null;
            RecordPaymentPojo pojo = new RecordPaymentPojo();
            StaffUser loggedInUser = null;
            if (customers.getCreatedById() != null) {
                loggedInUser = staffUserRepository.findById(customers.getCreatedById()).orElse(null);
            }
            List<CreditDocMessage> creditDocMessage = new ArrayList<>();
            if (customers.getMvnoId() != null) {
                pojo.setMvnoId(customers.getMvnoId());
            }

            CreditDocument doc = new CreditDocument();
            doc.setCustomer(customers);
            doc.setCreatedByName(customers.getUsername());
            doc.setPaymode(customerBillingMessage.getRecordPaymentDTO().getPaymode());
            doc.setPaymentdate(LocalDate.now());
            doc.setAmount(finalDebitDocument.getTotalamount());
            doc.setStatus(CommonConstants.PAYMENT_CONDITION.ONLINE_PAYMENT_APPROVED);
            doc.setRemarks("Verified");
            doc.setIsDelete(false);
            doc.setTdsamount(0.0);
            doc.setMvnoId(pojo.getMvnoId());
            if (customers.getStatus().equals(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION)){
                doc.setTrialDebitdocId(finalDebitDocument.getId());
            }else {
                doc.setInvoiceId(finalDebitDocument.getId());
            }
            doc.setPaytype("Invoice");
            doc.setType("Payment");
            doc.setAdjustedAmount(finalDebitDocument.getTotalamount());
            doc.setRemainingAmount(0.0);
            doc.setCreatedate(LocalDateTime.now());
            doc.setUpdatedate(LocalDateTime.now());
            if (loggedInUser!=null) {
                doc.setCreatedByName(loggedInUser.getFirstname());
            }
            if (customerBillingMessage.getReferenceNo()!=null){
                doc.setReferenceno(customerBillingMessage.getReferenceNo());
            }else {
                doc.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
            }
            doc = creditDocRepository.save(doc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @Transactional
    public void saveCustomerFromAPI(SaveCustomerDataShareMessage saveCustomerDataShareMessage){
        try {
            TraceContext traceContext = tracer.currentSpan().context();
            Customers customers = subscriberService.saveCustomersData(saveCustomerDataShareMessage);
            if(customers != null && saveCustomerDataShareMessage.getRefMvno() != null) {
                mvnoService.updateMvnoRefForInvoice(Long.valueOf(saveCustomerDataShareMessage.getRefMvno()), customers.getId());
            }

            boolean postPaidInvoiceForPrepaidCharge = false;
            BillRun billRun = null;

            if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID))
            {
                Integer count = customerChargeHistoryRepository.countAllByCustomerIdAndAndChargeType(customers.getId(), CommonConstants.CHARGE_TYPE_ADVANCE);

                if (count>0){
                    postPaidInvoiceForPrepaidCharge = true;
                    billRun = postpaidInvoiceService.addBillRunData(0,0d,0,0);
                }else {
                    LocalDate earlyBilldate = customers.getNextBillDate().minusDays(customers.getEarlyBillDays());
                    if (earlyBilldate.isEqual(LocalDate.now()) || earlyBilldate.isAfter(LocalDate.now())){
                        customers.setEarlyBilldate(earlyBilldate);
                        customersRepository.updateEarlyBillDate(customers.getId(),earlyBilldate);
                        if (earlyBilldate.isEqual(LocalDate.now())){
                            postPaidInvoiceForPrepaidCharge = true;
                        }
                    }
                }
            }

            if(customers.getStatus() != null && (customers.getStatus().equalsIgnoreCase(Constants.CUSTOMER_STATUS_SUSPEND) || customers.getStatus().equalsIgnoreCase(Constants.CUSTOMER_STATUS_INACTIVE))){
                postPaidInvoiceForPrepaidCharge = false;
            }

            if ((customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.PREPAID)) || postPaidInvoiceForPrepaidCharge) {
                CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
                Map<String, Object> data = new HashMap<>();
                data.put(CustomerBillingMessage.CUST_ID, customers.getId());
                data.put("Bullable_CUST_ID", customers.getPlanMappingList().get(0).getBillableCustomerId());
                data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF, saveCustomerDataShareMessage.getCreatedById());
                if (postPaidInvoiceForPrepaidCharge){
                    data.put(CustomerBillingMessage.POSTPAIDADVANCE,"Advance");
                }
                if (billRun!=null){
                    data.put(CustomerBillingMessage.BILL_RUN_ID,billRun.getId());
                }
                customerBillingMessage.setData(data);
                if (customers.getStatus().equalsIgnoreCase("NewActivation")) {
                    customerBillingMessage.setType(Constants.INVOICE_TYPE.IS_CAF_CUSTOMER);
                } else {
                    customerBillingMessage.setType(Constants.INVOICE_TYPE.CREATE_CUSTOMER);
                }
                customerBillingMessage.setIsCaptiveportal(saveCustomerDataShareMessage.getIsCaptiveportal());
                customerBillingMessage.setReferenceNo(saveCustomerDataShareMessage.getReferenceNo());
                if (saveCustomerDataShareMessage.getRecordPaymentPojo() != null) {
                    RecordPaymentPojo recordPaymentPojo = saveCustomerDataShareMessage.getRecordPaymentPojo();
                    recordPaymentPojo.setCustomerid(customers.getId());
                    customerBillingMessage.setRecordPaymentDTO(recordPaymentPojo);
                    data.put(CustomerBillingMessage.MVNOID, customers.getMvnoId());
                }
                if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)){
                    customerBillingMessage.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
                }
                customerBillingMessage.setTraceContext(traceContext);
                customerBillingMessage.setIsEarlyBillDate(false);
                customerBillingMessage.setCustomerStatus(customers.getStatus());
                CustomerProcessor customerProcessor = new CustomerProcessor(messageReceiverWithThread,customerBillingMessage,customers);
                getInvoicePool().execute(customerProcessor);
                //messageReceiverWithThread.processMessage(customerBillingMessage,customers);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void processChangePLan(ChangePlanMessage dataMessage) {
        try {
            TraceContext traceContext =tracer.currentSpan().context();
            MDC.put("type", "Fetch");
            MDC.put("userName", dataMessage.getGetCreatedByName());
            MDC.put("traceId",traceContext.traceIdString());
            MDC.put("spanId",traceContext.spanIdString());
            Customers customers=customersRepository.findById(dataMessage.getNewCustPlanMappingRevenues().get(0).getCustomerId()).orElse(null);
            List<Integer> oldDebitIds = this.save(dataMessage);
            Thread.sleep(2000);

            String eventType=dataMessage.getType();
            Long advanceCount=0l;
            if(dataMessage.getCustomerChargeHistoryRevenues()!=null && !dataMessage.getCustomerChargeHistoryRevenues().isEmpty())
                advanceCount=dataMessage.getCustomerChargeHistoryRevenues().stream().filter(x->x.getChargeType()!=null && x.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_ADVANCE)).count();

            Integer count = customerChargeHistoryRepository.countAllByCustomerIdAndAndChargeType(customers.getId(),CommonConstants.CHARGE_TYPE_ADVANCE);
            boolean postPaidInvoiceForPrepaidCharge = false;
            if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID) && count>0 && !dataMessage.isChangePlanNextBillDate() && ((advanceCount>0 && eventType!=null && eventType.equalsIgnoreCase("addNewService")) || (eventType==null || !eventType.equalsIgnoreCase("addNewService")))){
                postPaidInvoiceForPrepaidCharge = true;
            }

            if ((customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.PREPAID)) || postPaidInvoiceForPrepaidCharge) 
			{
                CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
                Map<String, Object> data = new HashMap<>();
                data.put(CustomerBillingMessage.CUST_ID, dataMessage.getNewCustPlanMappingRevenues().get(0).getCustomerId());
                data.put(CustomerBillingMessage.RENEWAL_ID, dataMessage.getRenewalId());
                data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF, dataMessage.getCreatedById());
                if (dataMessage.getPaySource() != null && dataMessage.getPaySource().length() > 0) {
                    data.put(CustomerBillingMessage.PAYMENT_SOURCE, dataMessage.getPaySource());
                }
                if (dataMessage.getAdditionalInformationDTO() != null) {
                    data.put(CustomerBillingMessage.ADDITIONALINFORMATIONDTO, dataMessage.getAdditionalInformationDTO());
                }
                List<CustomerChargeHistoryRevenue> chargeHistoryRevenue = dataMessage.getCustomerChargeHistoryRevenues();
                List<Double> discounts = new ArrayList<>();
                List<String> discountTypes = new ArrayList<>();
                Map<Integer, Double> discountMap = new HashMap<>();
                Map<Integer, String> discountTypeMap = new HashMap<>();
                for (CustPlanMappingRevenue chargeHistoryRevenues : dataMessage.getNewCustPlanMappingRevenues()) {
                    Integer custServiceMappingId = chargeHistoryRevenues.getCustServiceMappingId(); // Get the custServiceId
                    Double discount = chargeHistoryRevenues.getDiscount(); // Get the discount

                    // Retrieve the customer service mapping by the specific service ID
                    customerServiceMapRepository.findById(custServiceMappingId).ifPresent(mapping -> {
                        // Store the discount and discount type mapped by service ID
                        discountMap.put(custServiceMappingId, mapping.getDiscount());
                        discountTypeMap.put(custServiceMappingId, mapping.getDiscountType());
                    });
                }

// Collect discounts and discount types in the order of service IDs
                discountMap.keySet().stream().sorted().forEach(id -> {
                    discounts.add(discountMap.get(id));
                    discountTypes.add(discountTypeMap.get(id));
                });
//                Collections.reverse(discounts);
                data.put(CustomerBillingMessage.DISCOUNT, discounts);
                data.put(CustomerBillingMessage.DISCOUNTTYPE, discountTypes);
                if (dataMessage.getBuId() != null && !dataMessage.getBuId().isEmpty()) {
                    data.put(CustomerBillingMessage.BUIDS, dataMessage.getBuId());
                }
                if (dataMessage.getMvnoId() != null) {
                    data.put(CustomerBillingMessage.MVNOID, dataMessage.getMvnoId());
                }
                if (dataMessage.getLcoId() != null) {
                    data.put(CustomerBillingMessage.PARTNERID, dataMessage.getLcoId());
                }
                if (dataMessage.getIsLco() != null) {
                    data.put(CustomerBillingMessage.ISLCO, dataMessage.getIsLco());
                }
                if (dataMessage.getCreatedById() != null) {
                    data.put(CustomerBillingMessage.CREATEDBYID, dataMessage.getCreatedById());
                }
                if (dataMessage.getGetCreatedByName() != null) {
                    data.put(CustomerBillingMessage.CREATEDBYNAME, dataMessage.getGetCreatedByName());
                }
                if (!CollectionUtils.isEmpty(dataMessage.getOverrideChargeIds())) {
                    data.put(CustomerBillingMessage.OVERRIDECHARGES, dataMessage.getOverrideChargeIds());
                }
                if (dataMessage.getRecordPaymentDTO() != null) {
                    customerBillingMessage.setRecordPaymentDTO(dataMessage.getRecordPaymentDTO());
                }
                if (count > 0) {
                    data.put(CustomerBillingMessage.POSTPAIDADVANCE, "Advance");
                }
                customerBillingMessage.setType(dataMessage.getType());
                data.put(CustomerBillingMessage.RENEWAL_ID, dataMessage.getRenewalId());

                if (!CollectionUtils.isEmpty(oldDebitIds)) {
                    String oldDebitDocumentIdStr = "";
                    for (Integer invoiceNo : oldDebitIds) {
                        oldDebitDocumentIdStr = oldDebitDocumentIdStr + invoiceNo + ",";
                    }
                    oldDebitDocumentIdStr = oldDebitDocumentIdStr.substring(0, oldDebitDocumentIdStr.length() - 1);
                    data.put(CustomerBillingMessage.oldDebitDocId, oldDebitDocumentIdStr);
                } else {
                    data.put(CustomerBillingMessage.oldDebitDocId, "");
                }
                dataMessage.getCreatedById();
                if (dataMessage.getParentId() != null && dataMessage.getChildIds() != null && dataMessage.getChildIds().size() > 0) {
                    data.put(CustomerBillingMessage.CUST_ID, dataMessage.getParentId());
                    customerBillingMessage.setChildIds(dataMessage.getChildIds());
                }
                customerBillingMessage.setData(data);
                if (dataMessage.getType() != null && dataMessage.getType().equalsIgnoreCase("addNewService") || dataMessage.getType().equalsIgnoreCase("isCAFCustomer")) {
                    customerBillingMessage.setNewServiceId(dataMessage.getCustomerServiceMappingRevenues().get(0).getId());
                }
                if (customers.getCusttype().equalsIgnoreCase(Constants.CUSTOMER_TYPE.POSTPAID)) {
                    customerBillingMessage.setCustType(Constants.CUSTOMER_TYPE.POSTPAID);
                    customerBillingMessage.setIsEarlyBillDate(false);
                    if (dataMessage.getType() != null) {
                        customerBillingMessage.setRenew(dataMessage.getType());
                    }
                }
                if (dataMessage.getPayingChildId() != null) {
                    customerBillingMessage.setPayableChildId(dataMessage.getPayingChildId());
                }

                customerBillingMessage.setAutoPaymentAdjustment(dataMessage.getIsAutoPaymentRequired());
                customerBillingMessage.setCreditDocumentPaymentPojos(dataMessage.getCreditDocumentPaymentPojoList());
                customerBillingMessage.setPlanValidityChangePlan(!dataMessage.getType().equalsIgnoreCase("renew"));
                messageReceiverWithThread.receiveBillingInvoiceMessageForManual(customerBillingMessage);
            }

            logger.info(LogConstants.REQUEST_FROM + LogConstants.CMS+ LogConstants.REQUEST_FOR + dataMessage.getType() +", for Customer Id: " +  customers.getId() + LogConstants.REQUEST_BY + dataMessage.getGetCreatedByName()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS);

            logger.info("Handled ChangePlanRevenue successfully: " + dataMessage);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error handling ChangePlanRevenue: " + e.getMessage(), e);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
    public void saveCustDirectChargeListRevenue(ChangePlanMessageList message) {
        logger.info("Received Message From RabbitMq receiverMessage : <" + message + ">");

        if (message == null) {
            logger.error("Received null message from RabbitMQ.");
            return;
        }

        if (message.getChangePlanMessageList() == null) {
            logger.error("ChangePlanMessageList is null in the received message.");
            return;
        }

        try {
            for (ChangePlanMessage changePlanMessage : message.getChangePlanMessageList()) {
                if (changePlanMessage == null) {
                    logger.warn("Encountered a null ChangePlanMessage inside the list.");
                    continue;
                }

                logger.info("Processing ChangePlanMessage: " + changePlanMessage);
                try {
                    saveCustDirectCharge(changePlanMessage);
                } catch (Exception ex) {
                    logger.error("Error while saving ChangePlanMessage: " + ex.getMessage(), ex);
                }
            }
            logger.info("Cust direct charges updated");
        } catch (Exception e) {
            logger.error("receiveMessageUpdateBranch Failed: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    public DebitDocument paymentAdjustmentForForCafCust(DebitDocument debitDocument,List<CreditDocument> getAllCreditDoc) {
        try {
            logger.info("Adjusting Payment for   debitdoc ID :  "+ debitDocument.getId() + " if payment done during  CAF creation or customer portal");
            List<CreditDocument> creditDocuments =  new ArrayList<>();
            for (CreditDocument doc :getAllCreditDoc) {
                doc.setAdjustedAmount(0.0);
                doc.setRemainingAmount(0.0);
                doc.setInvoiceId(debitDocument.getId());
                doc.setTrialDebitdocId(null);
                creditDocuments.add(doc);
            }
            creditDocuments = creditDocRepository.saveAll(creditDocuments);
            Customers customers = debitDocument.getCustomer();
            customers.setWalletbalance(debitDocument.getTotalamount()-debitDocument.getAdjustedAmount());
            customersRepository.save(customers);
            if (!creditDocuments.isEmpty()) {

                CreditDebitMappingPojo creditDebitDocMappingPojo = new CreditDebitMappingPojo();
                List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
                creditDebitDocMappingPojo.setInvoiceId(debitDocument.getId());
                for (CreditDocument  creditDocument : creditDocuments) {
                    CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
                    creditDebitDataPojo.setAmount(creditDocument.getAmount());
                    creditDebitDataPojo.setId(creditDocument.getId());
                    creditDebitDataPojoList.add(creditDebitDataPojo);
                }
                creditDebitDocMappingPojo.setCreditDocumentList(creditDebitDataPojoList);

                creditDocService.adjustManualPaymentToInvoice(creditDebitDocMappingPojo);
                DebitDocument debitDocumentContainingCDmapping = debitDocRepository.findById(debitDocument.getId()).get();
                debitDocumentContainingCDmapping.setTotaldue(debitDocument.getTotalamount()-debitDocument.getAdjustedAmount());
                debitDocument = debitDocRepository.save(debitDocumentContainingCDmapping);
//                debitDocument.setTotaldue(debitDocument.getTotalamount()-debitDocument.getAdjustedAmount());
//                debitDocRepository.save(debitDocument);
                logger.info("Payment Adjusted for   debitdoc ID :  "+ debitDocument.getId() + " if payment done during  CAF creation or customer portal");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  debitDocument;

    }



    public  List<ServiceQosPojo> serviceQosPojo (Integer debitDocumentId){
        try{
            List<DebitDocDetails> debitDocDetailsList = debitDocDetailRepository.findDebitDocIdAndServiceIdByMvnoDebitDocId(debitDocumentId);
            List<ServiceQosPojo> serviceQosPojos = new ArrayList<>();

            if (debitDocDetailsList!=null) {
                Map<Long, List<DebitDocDetailList>> groupedDetails = groupByServiceId(debitDocDetailsList);

                if (!CollectionUtils.isEmpty(groupedDetails)) {
                    Double totalAmountCustomerDirect = 0d;
                    for (Map.Entry<Long, List<DebitDocDetailList>> entry : groupedDetails.entrySet()) {
                        String serviceName = " ";
                        serviceName = serviceRepository.findserviceNameByServiceId(entry.getKey());

                        ServiceQosPojo serviceQosPojo = new ServiceQosPojo();
                        List<QosNameCount> qosNameCounts = new ArrayList<>();

                        List<DebitDocDetailList> debitDocDetailLists = entry.getValue();
                        for (DebitDocDetailList debitDocDetailList : debitDocDetailLists){

                            Map<String, Map<Double, Long>> groupedData = debitDocDetailLists.stream()
                                    .collect(Collectors.groupingBy(
                                            debitDoc -> postpaidPlanRepo.findQosName(Integer.valueOf(debitDoc.getPlanId())), // Group by QoS
                                            Collectors.groupingBy(
                                                    DebitDocDetailList::getTotalAmount, // Group by totalAmount within each QoS
                                                    Collectors.counting() // Count entries with each totalAmount
                                            )
                                    ));
                        }
                        Double totalAmount = entry.getValue().stream().filter(i->i.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_ADVANCE)).mapToDouble(amount -> amount.getTotalAmount()).sum();
                        Double customerDirectAmount = entry.getValue().stream().filter(i->i.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT)).mapToDouble(amount -> amount.getTotalAmount()).sum();
                        if (customerDirectAmount>0){
                            totalAmountCustomerDirect+=customerDirectAmount;
                        }
                    }
                }
            }

            return  serviceQosPojos ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public  List<Item> getQosItemPayload (List<DebitDocDetailList> debitDocDetailsList){
        try{
            DecimalFormat df = new DecimalFormat("0.0000");
            List<Item> itemList = new ArrayList<>();
            debitDocDetailsList=debitDocDetailsList.stream().filter(i->i.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_ADVANCE)).collect(Collectors.toList());
            Map<String,List<DebitDocDetailList>> groupByQosName = debitDocDetailsList.stream().collect(Collectors.groupingBy(debitDoc ->debitDoc.getQosName()));
            for(Map.Entry<String,List<DebitDocDetailList>>qosEntry:groupByQosName.entrySet())
            {
                List<DebitDocDetailList> docDetailLists=qosEntry.getValue();
                Map<Integer,List<DebitDocDetailList>> groupByChargeId = docDetailLists.stream().collect(Collectors.groupingBy(debitDoc ->debitDoc.getChargeId()));

                for(Map.Entry<Integer,List<DebitDocDetailList>>chargeEntry:groupByChargeId.entrySet())
                {
                    Item item = new Item();
                    List<DebitDocDetailList> mainData=chargeEntry.getValue().stream().filter(x->x.getTotalAmount().equals(x.getChargeActualOfferPrice())).collect(Collectors.toList());
                    List<DebitDocDetailList> prorateData=chargeEntry.getValue().stream().filter(x->!x.getTotalAmount().equals(x.getChargeActualOfferPrice())).collect(Collectors.toList());
                    item.setBandwidth(qosEntry.getKey());
                    if(mainData!=null && mainData.isEmpty()) {
                        item.setUnitPrice(Double.valueOf(df.format(chargeEntry.getValue().get(0).getChargeActualOfferPrice())));
                        item.setQuantity(0);
                    }
                    else {
                        item.setUnitPrice(Double.valueOf(df.format(mainData.get(0).getChargeActualOfferPrice())));
                        item.setQuantity(mainData.size());
                    }
                    Map<Double,List<DebitDocDetailList>> groupByChargeAmount = prorateData.stream().collect(Collectors.groupingBy(debitDoc ->debitDoc.getTotalAmount()));
                    List<ProratedPayload> proratedPayloads=new ArrayList<>();
                    for(Map.Entry<Double,List<DebitDocDetailList>>chargeAmountEntry:groupByChargeAmount.entrySet())
                    {
                        ProratedPayload payload=new ProratedPayload();
                        payload.setQuantity(chargeAmountEntry.getValue().size());
                        payload.setUnitPrice(Double.valueOf(df.format(chargeAmountEntry.getKey())));
                        proratedPayloads.add(payload);
                    }
                    if(proratedPayloads!=null && !proratedPayloads.isEmpty()) {
                        item.setProrated(proratedPayloads);
                    }
                    itemList.add(item);
                }
            }
            return  itemList ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public  List<ServicePayload> getServicePayload (Integer debitDocumentId){
        try{
            DecimalFormat df = new DecimalFormat("0.0000");
            List<DebitDocDetails> debitDocDetailsList = debitDocDetailRepository.findDebitDocIdAndServiceIdByMvnoDebitDocId(debitDocumentId);
            Map<Long, List<DebitDocDetailList>> groupedDetails = groupByServiceId(debitDocDetailsList);

            List<ServicePayload> servicePayloads=new ArrayList<>();
            ServicePayload servicePayloadInstallingCharge=new ServicePayload();
            Double totalAmountCustomerDirect = 0d;

            for (Map.Entry<Long, List<DebitDocDetailList>> entry : groupedDetails.entrySet()) {

                ServicePayload servicePayload=new ServicePayload();
                String serviceName = serviceRepository.findserviceNameByServiceId(entry.getKey());
                Double totalAmount = entry.getValue().stream().filter(i->i.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_ADVANCE)).mapToDouble(amount -> amount.getTotalAmount()).sum();
                totalAmount=Double.valueOf(df.format(totalAmount));

                servicePayload.setService(serviceName);
                servicePayload.setAmount(totalAmount);

                List<DebitDocDetailList> debitDocDetailLists=entry.getValue();
                List<Item> items = getQosItemPayload(debitDocDetailLists);
                if(items!=null && !items.isEmpty()) {
                    servicePayload.setItems(items);
                }

                Double customerDirectAmount = entry.getValue().stream().filter(i->i.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT) || i.getChargeType().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_NONRECURRING)).mapToDouble(amount -> amount.getTotalAmount()).sum();
                if (customerDirectAmount>0)
                    totalAmountCustomerDirect+=customerDirectAmount;
                servicePayloads.add(servicePayload);
            }

            if (totalAmountCustomerDirect>0){
                servicePayloadInstallingCharge.setService(CommonConstants.INSTALLATION_SERVICE);
                servicePayloadInstallingCharge.setAmount(Double.valueOf(df.format(totalAmountCustomerDirect)));
                servicePayloads.add(servicePayloadInstallingCharge);
            }
            return  servicePayloads;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void sendAutoRenewOrAddonPlanRequestToCms(AutoRenewOrAddonPlanRequestDto autoRenewOrAddonPlanRequestDto){
        kafkaMessageSender.send(new KafkaMessageData(autoRenewOrAddonPlanRequestDto,autoRenewOrAddonPlanRequestDto.getClass().getSimpleName()));
    }

    public  boolean hasInvoiceToOrg(List<CustPlanMappping> custPlanMapppings) {
        return custPlanMapppings.stream()
                .anyMatch(custPlanMappping -> Constants.ORGANIZATION.equalsIgnoreCase(custPlanMappping.getBillTo()));
    }

    public Map<Integer, List<Double>> getTaxPercentagesByCustomers(List<Integer> customerIds) {
        Map<Integer, List<Double>> taxMap = new HashMap<>();

        if (customerIds == null || customerIds.isEmpty()) {
            return taxMap;
        }

        List<Object[]> results = debitDocumentTAXRelRepository.findTaxPercentagesByCustomerIds(customerIds);

        if (results.isEmpty()) {
            System.out.println("No tax data found for customer IDs: " + customerIds);
        }

        for (Object[] obj : results) {
            if (obj[0] != null && obj[1] != null) {
                Integer customerId = (Integer) obj[0];
                Double taxPercentage = ((Number) obj[1]).doubleValue();
                taxMap.computeIfAbsent(customerId, k -> new ArrayList<>()).add(taxPercentage);
            }
        }

        return taxMap;
    }


    public Map<Integer, List<String>> getDebitDOcIdByCustomers(List<Integer> debitDocId) {
        Map<Integer, List<String>> debitdocMap = new HashMap<>();

        if (debitDocId == null || debitDocId.isEmpty()) {
            return debitdocMap;
        }

        List<Object[]> results = debitDocumentTAXRelRepository.findDebitdocIdByCustomerIds(debitDocId);

        if (results.isEmpty()) {
            System.out.println("No Debitdoc data found for customer IDs: " + debitDocId);
        }
        for (Object[] obj : results) {
            if (obj[0] != null && obj[1] != null) {
                Integer customerId = (Integer) obj[0];
                String docNumber = (obj[1]).toString();
                debitdocMap.computeIfAbsent(customerId, k -> new ArrayList<>()).add(docNumber);
            }
        }
        return debitdocMap;
    }
    public GenericDataDTO updateGraceDays(Integer debitDocId, Integer debitDocGraceDays) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            Optional<DebitDocument> optional = debitDocRepository.findById(debitDocId);
            if (!optional.isPresent()) {
                RESP_CODE = APIConstants.NO_CONTENT_FOUND;
                genericDataDTO.setResponseCode(RESP_CODE);
                genericDataDTO.setResponseMessage("DebitDocument ID  not found.");
                return genericDataDTO;
            }
            DebitDocument doc = optional.get();
            doc.setDebitDocGraceDays(debitDocGraceDays);
            debitDocRepository.save(doc);
            RESP_CODE = APIConstants.SUCCESS;
            UpdateDebitdocGraceDayMessage UpdateDebitdocGraceDayMessage = new UpdateDebitdocGraceDayMessage();
            UpdateDebitdocGraceDayMessage.setDebitDocId(debitDocId);
            UpdateDebitdocGraceDayMessage.setDebitDocGraceDays(debitDocGraceDays);
            kafkaMessageSender.send(new KafkaMessageData(UpdateDebitdocGraceDayMessage, UpdateDebitdocGraceDayMessage.class.getSimpleName()));
            genericDataDTO.setResponseCode(RESP_CODE);
            genericDataDTO.setResponseMessage("Grace days updated successfully");
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_GRACE_PERIOD,
             AclConstants.OPERATION_XSLT_MANAGEMENT_EDIT, null, null,
             doc.getCustomer().getId().longValue(), null,doc.getDocnumber(), debitDocId , debitDocGraceDays);
        } catch (Exception e){
            e.printStackTrace();
            RESP_CODE = APIConstants.EXPECTATION_FAILED;
            genericDataDTO.setResponseCode(RESP_CODE);
            genericDataDTO.setResponseMessage("Grace days updated failed");
        }
        return genericDataDTO;
    }

    public void postpaidCustomerInstallmentForDirectCharge() {
        try {
            logger.debug("****************----------PostPaid - Direct Charge Installment Scheduler START----------****************");
            List<Integer> custIdList = custChargeRepository.findAllCustomerWhereInstallmentEnabled();
            if (custIdList.isEmpty()) {
                logger.warn("No customer found for Installment of direct charge");
                return;
            }


            custIdList.parallelStream().forEach(custId -> {  // Parallel Processing for High TPS
                List<Object[]> custChargeDetails = custChargeRepository.findCustChargeByCustId(custId);
                for(Object[] custChargeDetail : custChargeDetails){
                    Integer cstchargeid = convertToInteger(custChargeDetail[0]);
                    Integer createdById = convertToInteger(custChargeDetail[1]);
                    String createbyname = custChargeDetail[2] != null ? custChargeDetail[2].toString() : null;
                    String customerStatus = custChargeDetail[3] != null ? custChargeDetail[3].toString() : null;
                    String custType = custChargeDetail[4] != null ? custChargeDetail[4].toString() : null;

                    CustomerBillingMessage customerBillingMessage = new CustomerBillingMessage();
                    Map<String, Object> data = new HashMap<>();
                    data.put(CustomerBillingMessage.CUST_ID, custId);
                    data.put(CustomerBillingMessage.CURRENT_LOGGED_IN_STAFF, createdById);
                    customerBillingMessage.setData(data);
                    customerBillingMessage.setType(Constants.INVOICE_TYPE.CUSTOMER_CHARGE);
                    customerBillingMessage.setCustChargeIds(Collections.singletonList(cstchargeid));
                    customerBillingMessage.setCreatedByName(createbyname);
                    customerBillingMessage.setCustType(custType);
                    customerBillingMessage.setTracerIdNotRequired(true);

                    if (CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION.equalsIgnoreCase(customerStatus)) {
                        data.put(CustomerBillingMessage.IS_CAF_CUSTOMER_DIRECT_CHARGE, "true");
                    }
                    messageReceiverWithThread.receiveBillingInvoiceMessageForManual(customerBillingMessage);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Integer convertToInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof BigInteger) return ((BigInteger) obj).intValue();
        if (obj instanceof Number) return ((Number) obj).intValue(); // covers Long, Double, etc.
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }


    public static LocalDateTime parseFlexibleDateTime(String dateStr) {
        List<DateTimeFormatter> FORMATTERS = Arrays.asList(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        );
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (Exception e) {
                // Try next formatter
            }
        }
        throw new IllegalArgumentException("Unsupported date format: " + dateStr);
    }

    public List<Map<String, Object>> getAllByCustomerForCreditNoteFast(Integer customerid) {

        long startTime = System.currentTimeMillis();
        System.out.println("Step 1: Start fetching DebitDocumentCreditNoteView for customerId=" + customerid);

        List<DebitDocumentCreditNoteView> docs = debitDocRepository.findAllByCustomerForCreditNoteView(customerid);
        long afterFetchDocs = System.currentTimeMillis();
        System.out.println("Step 1: Fetched " + docs.size() + " docs in " + (afterFetchDocs - startTime) + " ms");

        if (docs.isEmpty()) {
            System.out.println("No documents found. Total Execution Time: " + (afterFetchDocs - startTime) + " ms");
            return Collections.emptyList();
        }

        System.out.println("Step 2: Calculating refund amounts for each doc");
        long refundStart = System.currentTimeMillis();

        Map<Integer, Double> refundMap = getRefundMapForDocsOptimized(docs);

        long afterRefund = System.currentTimeMillis();
        System.out.println("Step 2: Calculated refund amounts for all docs in " + (afterRefund - refundStart) + " ms");

        System.out.println("Step 3: Building final response map");
        long responseStart = System.currentTimeMillis();

        List<Map<String, Object>> result = docs.stream().map(d -> {
            Double refund = refundMap.getOrDefault(d.getId(), 0.0);
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getId());
            map.put("createdByName", d.getCreatedByName());
            map.put("docnumber", d.getDocnumber());
            map.put("tax", d.getTax());
            map.put("totalamount", d.getTotalamount());
            map.put("adjustedAmount", d.getAdjustedAmount());
            map.put("refundAbleAmount", refund.toString());
            map.put("status", d.getStatus());
            return map;
        }).collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        System.out.println("Step 3: Built final response in " + (endTime - responseStart) + " ms");
        System.out.println("⚡ Total Execution Time: " + (endTime - startTime) + " ms for " + docs.size() + " docs");

        return result;
    }


}
