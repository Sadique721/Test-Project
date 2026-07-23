package com.savbill.cpm.KRA.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.paytm.pg.merchant.PaytmChecksum;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.savbill.cpm.KRA.KRAUtils;
import com.savbill.cpm.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.cpm.audit.AuditResponse;
import com.savbill.cpm.audit.AuditSearchRequest;
import com.savbill.cpm.audit.AuditService;
import com.savbill.cpm.audit.EntityPojo;
import com.savbill.cpm.constants.*;
import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.controller.api.ApiBaseController;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.GenericSearchModel;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.core.dto.ValidationData;
import com.savbill.cpm.core.exceptions.DataNotFoundException;
import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.exception.CustomValidationException;
import com.savbill.cpm.kafka.KafkaMessageData;
import com.savbill.cpm.kafka.KafkaMessageSender;
import com.savbill.cpm.mapper.postpaid.CustomerAddressMapper;
import com.savbill.cpm.model.common.*;
import com.savbill.cpm.model.lead.LeadMaster;
import com.savbill.cpm.model.postpaid.*;
import com.savbill.cpm.model.postpaid.PlanService;
import com.savbill.cpm.model.radius.RadiusProfile;
import com.savbill.cpm.model.radius.RadiusProfileCheckItem;
import com.savbill.cpm.modules.Alert.smsScheduler.service.SmsSchedulerService;
import com.savbill.cpm.modules.Area.domain.Area;
import com.savbill.cpm.modules.Area.repository.AreaRepository;
import com.savbill.cpm.modules.Branch.repository.BranchRepository;
import com.savbill.cpm.modules.Branch.service.BranchService;
import com.savbill.cpm.modules.BusinessUnit.domain.BusinessUnit;
import com.savbill.cpm.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.savbill.cpm.modules.BusinessVerticals.Respository.BusinessVerticalsMappingRepository;
import com.savbill.cpm.modules.BusinessVerticals.Respository.BusinessVerticalsRepository;
import com.savbill.cpm.modules.BusinessVerticals.domain.BusinessVerticalsMapping;
import com.savbill.cpm.modules.Customers.CustomerShowDTO;
import com.savbill.cpm.modules.Customers.CustomersController;
import com.savbill.cpm.modules.Customers.SpecialCustomerDTO;
import com.savbill.cpm.modules.DisconnectSubscriber.model.UserDisconnectByNameReqDTO;
import com.savbill.cpm.modules.DisconnectSubscriber.model.UserDisconnectBySessionDTO;
import com.savbill.cpm.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.savbill.cpm.modules.InventoryManagement.PopManagement.domain.PopManagement;
import com.savbill.cpm.modules.InventoryManagement.PopManagement.repository.PopManagementRepository;
import com.savbill.cpm.modules.InventoryManagement.Product_Plan_Mapping.domain.Productplanmapping;
import com.savbill.cpm.modules.InventoryManagement.Product_Plan_Mapping.mapper.Productplanmappingmapper;
import com.savbill.cpm.modules.InventoryManagement.Product_Plan_Mapping.repository.ProductPlanMappingRepository;
import com.savbill.cpm.modules.InventoryManagement.Product_Plan_Mapping.service.ProductplanmappingService;
import com.savbill.cpm.modules.InventoryManagement.item.ItemRepository;
import com.savbill.cpm.modules.LocationMaster.domain.CustomerLocationMapping;
import com.savbill.cpm.modules.LocationMaster.domain.LocationMaster;
import com.savbill.cpm.modules.LocationMaster.repository.CustomerLocationRepository;
import com.savbill.cpm.modules.LocationMaster.repository.LocationMasterRepository;
import com.savbill.cpm.modules.Mvno.model.MvnoDTO;
import com.savbill.cpm.modules.Mvno.service.MvnoService;
import com.savbill.cpm.modules.NetworkDevices.domain.NetworkDevices;
import com.savbill.cpm.modules.NetworkDevices.repository.NetworkDeviceRepository;
import com.savbill.cpm.modules.Notification.model.NotificationDTO;
import com.savbill.cpm.modules.Notification.service.NotificationService;
import com.savbill.cpm.modules.PartnerLedger.domain.PartnerLedger;
import com.savbill.cpm.modules.PartnerLedger.domain.PartnerPayment;
import com.savbill.cpm.modules.PartnerLedger.model.*;
import com.savbill.cpm.modules.PartnerLedger.repository.PartnerPaymentRepository;
import com.savbill.cpm.modules.PartnerLedger.service.PartnerLedgerDetailsService;
import com.savbill.cpm.modules.PartnerLedger.service.PartnerLedgerService;
import com.savbill.cpm.modules.PartnerLedger.service.PartnerPaymentService;
import com.savbill.cpm.modules.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.cpm.modules.PaymentConfig.service.PaymentConfigService;
import com.savbill.cpm.modules.PlanQosMapping.PlanQosMappingEntity;
import com.savbill.cpm.modules.PlanQosMapping.PlanQosMappingMapper;
import com.savbill.cpm.modules.PlanQosMapping.PlanQosMappingPojo;
import com.savbill.cpm.modules.PlanQosMapping.PlanQosMappingService;
import com.savbill.cpm.modules.Region.domain.Region;
import com.savbill.cpm.modules.Region.domain.RegionBranchMapping;
import com.savbill.cpm.modules.Region.repository.RegionBranchRepository;
import com.savbill.cpm.modules.ServiceArea.SubscriberMapper;
import com.savbill.cpm.modules.ServiceArea.service.ServiceAreaService;
import com.savbill.cpm.modules.Teams.service.HierarchyService;
import com.savbill.cpm.modules.TimeBasePolicy.service.TimeBasePolicyService;
import com.savbill.cpm.modules.acl.constants.AclConstants;
import com.savbill.cpm.modules.auditLog.service.AuditLogService;
import com.savbill.cpm.modules.childcustomer.dto.ChildCustPojo;
import com.savbill.cpm.modules.childcustomer.entity.ChildCustomer;
import com.savbill.cpm.modules.childcustomer.implemetation.ChildCustomerImpl;
import com.savbill.cpm.modules.childcustomer.repository.ChildCustomerRepo;
import com.savbill.cpm.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.savbill.cpm.modules.payments.model.PaytmDto;
import com.savbill.cpm.modules.payments.model.PaytmPaymentDto;
import com.savbill.cpm.modules.servicePlan.service.ServicesService;
import com.savbill.cpm.modules.subscriber.mapper.SubscriberDetailsMapper;
import com.savbill.cpm.modules.subscriber.model.*;
import com.savbill.cpm.modules.subscriber.service.ChargeThread;
import com.savbill.cpm.modules.subscriber.service.ReceiptThread;
import com.savbill.cpm.modules.subscriber.service.SubscriberService;
import com.savbill.cpm.pojo.ClientServicePojo;
import com.savbill.cpm.pojo.CustomerNotesDto;
import com.savbill.cpm.pojo.DocumentDto;
import com.savbill.cpm.pojo.LocationVo;
import com.savbill.cpm.pojo.NewCustPojos.CustFieldsPojo;
import com.savbill.cpm.pojo.NewCustPojos.PincodeAreaCityProjection;
import com.savbill.cpm.pojo.api.*;
import com.savbill.cpm.pojo.customer.CafDto;
import com.savbill.cpm.rabbitMq.MessageSender;
import com.savbill.cpm.rabbitMq.message.*;
import com.savbill.cpm.repository.LeadMasterRepository;
import com.savbill.cpm.repository.common.*;
import com.savbill.cpm.repository.postpaid.*;
import com.savbill.cpm.repository.radius.CustomerCafImageMappingRepository;
import com.savbill.cpm.repository.radius.CustomerServiceMappingRepository;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.service.BulkService.BulkManagementConstant;
import com.savbill.cpm.service.common.*;
import com.savbill.cpm.service.customerServices.CustomerCreation;
import com.savbill.cpm.service.postpaid.*;
import com.savbill.cpm.service.radius.*;
import com.savbill.cpm.spring.LoggedInUser;
import com.savbill.cpm.spring.MessagesPropertyConfig;
import com.savbill.cpm.spring.SpringContext;
import com.savbill.cpm.threadconfig.CustomThreadPool;
import com.savbill.cpm.utils.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class KRAController extends ApiBaseController {
    private static final String MODULE = " [KRAController] ";

    private static final Logger log = LoggerFactory.getLogger(KRAController.class);
    private static final String CUSTOMER_PAYMENT = "CustomerPayment";
    private static final String OTP = "otp";

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @Autowired
    private MessagesPropertyConfig messagesProperty;
    @Autowired
    private PostpaidPlanMapper postpaidPlanMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private SubscriberMapper subscriberMapper;
    @Autowired
    private SubscriberDetailsMapper subscriberDetailsMapper;
    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private SmsSchedulerService smsSchedulerService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ChargeMapper chargeMapper;
    @Autowired
    private CountryService countryService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private PartnerLedgerDetailsService partnerLedgerDetailsService;
    @Autowired
    private StateService stateService;
    @Autowired
    private PartnerPaymentService partnerPaymentService;

    @Autowired
    private PartnerCommissionService partnerCommissionService;

    @Autowired
    private CustomerAddressMapper customerAddressMapper;

    @Autowired
    private WorkflowAuditService workflowAuditService;

    private List<String> chargeCategoryList = new ArrayList<>();
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private CustomersService customersService;
    @Autowired
    AuditLogService auditLogService;
    @Autowired
    PostpaidPlanService postpaidPlanService;
    @Autowired
    ClientServiceSrv clientServiceSrv;
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ServicesService servicesService;

    @Autowired
    private TimeBasePolicyService timeBasePolicyService;
    @Autowired
    private DocumentVerification documentVerification;
    @Autowired
    private GoogleMaps googleMaps;

    @Autowired
    private CustomerLedgerRepository customerLedgerRepository;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    ShiftLocationRepository shiftLocationRepository;

    @Autowired
    PartnerPaymentRepository partnerPaymentRepository;

    @Autowired
    private ShorterRepository shorterRepository;

    @Autowired
    private ShorterService shorterService;

    @Autowired
    private PlanGroupService planGroupService;

    @Autowired
    private PlanGroupMappingService planGroupMappingService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private DbrService dbrService;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private ProductPlanMappingRepository planMappingRepository;

    @Autowired
    BusinessVerticalsMappingRepository businessVerticalsMappingRepository;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    BusinessVerticalsRepository businessVerticalsRepository;

    @Autowired
    CustMacMapppingRepository custMacMapppingRepository;

    @Autowired
    private TaxService taxService;

    @Autowired
    private ProductPlanMappingRepository productPlanMappingRepository;

    @Autowired
    Productplanmappingmapper productplanmappingmapper;
    @Autowired
    MessageSender messageSender;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    RegionBranchRepository regionBranchRepository;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;
    @Autowired
    private EnterpriseETRAuditRepository enterpriseETRAuditRepository;
    @Autowired
    private ProductplanmappingService productplanmappingService;

    @Autowired
    private PlanQosMappingService planQosMappingService;

    @Autowired
    private PlanQosMappingMapper planQosMappingMapper;
    // @PreAuthorize("validatePermission(\"" +
    // AclConstants.OPERATION_BULK_UPLOAD_NETWORK_DEVICES + "\",\"" +
    // AclConstants.OPERATION_BULK_UPLOAD_NETWORK_DEVICES + "\")")
//	@Autowired
////	private OTPService otpService;

    @Autowired
    BranchService branchService;
    @Autowired
    BranchServiceAreaMappingService branchServiceAreaMappingService;

    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private PlanGroupMappingMapper planGroupMappingMapper;

    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;
    @Autowired
    private PopManagementRepository popManagementRepository;
    @Autowired
    private BatchPaymentService batchPaymentService;

    @Autowired
    private CreateDataSharedService createDataSharedService;

    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustPlanMappingService custPlanMappingService;
    @Autowired
    LocationMasterRepository locationMasterRepository;

    @Autowired
    private CustomerLocationRepository customerLocationRepository;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private CustChargeDetailsRepository custChargeDetailsRepository;

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CustomerLocationMapper customerLocationMapper;

    @Autowired
    private Tracer tracer;

    @Autowired
    private PaymentConfigService paymentConfigService;

    @Autowired
    CustSpecialPlanMapper custSpecialPlanMapper;

    @Autowired
    private CmsClientUtil cmsClientUtil;

    @Autowired
    private CustSpecialPlanRelMapppingRepository custSpecialPlanRelMapppingRepository;

    @Autowired
    private CustomerCreation customerCreation;

    @Autowired
    private LeadMasterRepository leadMasterRepository;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private PostpaidPlanChargeService postpaidPlanChargeService;

    @Autowired
    private PaymentGatewayService paymentGatewayService;


    @Autowired
    private CustomerAddressService customerAddressService;


    @Autowired
    private HierarchyService hierarchyService;

    @Autowired
    private ChildCustomerImpl childCustomerimpl;

    @Autowired
    private ChildCustomerRepo childCustomerRepo;

    @Autowired
    private CustomerCafImageMappingRepository customerCafImageMappingRepository;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    private KRAUtils KRAUtils;

    CustomThreadPool threadPool = new CustomThreadPool(50,
            50, 60, TimeUnit.SECONDS, "Child-Cust-th");

    @Value(value = "${child-create-pool}")
    private String childCreatePool;


    @PostMapping("/intg/kra/customer")
    public ResponseEntity<?> processEtimsAddCustomer(@RequestBody List<Integer> customerIds, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Page<Customers> pageRes = null;
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Customers> customers = customersRepository.findAllById(customerIds);
            List<CustomersPojo> listPojo = new ArrayList<>();
            customers.forEach(customer -> {
                CustomersPojo pojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                listPojo.add(pojo);
            });
            KRAUtils.processEtimsAddCustomer(listPojo, req);
            return ResponseEntity.ok("Batch processed successfully");

        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Customers list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, pageRes);
    }
    @PostMapping("/intg/kra/plan")
    public ResponseEntity<?> processEtimsAddItemsListBatch(@RequestBody List<Integer> planIds, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Page<Customers> pageRes = null;
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<PostpaidPlan> plans = postpaidPlanRepo.findAllById(planIds);
            List<PostpaidPlan> unsyncedPlans = plans.stream()
                    .filter(plan -> !plan.isKraSynced())
                    .collect(Collectors.toList());
            response.put("requestedCount", planIds.size());
            response.put("processedCount", unsyncedPlans.size());
            response.put("skippedSyncedCount", plans.size() - unsyncedPlans.size());
            if (unsyncedPlans.isEmpty()) {
                response.put(APIConstants.MESSAGE, "No unsynced plans found for KRA sync");
                return apiResponse(APIConstants.SUCCESS, response);
            }
            List<PostpaidPlanPojo> listPojo = new ArrayList<>();
            unsyncedPlans.forEach(plan -> {
                PostpaidPlanPojo pojo = null;
                try {
                    pojo = postpaidPlanMapper.domainToDTO(plan, new CycleAvoidingMappingContext());
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                listPojo.add(pojo);
            });
            KRAUtils.processEtimsAddItemsListBatch(listPojo);
            response.put(APIConstants.MESSAGE, "Plan sync payload prepared successfully");
            return apiResponse(APIConstants.SUCCESS, response);

        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Customers list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, pageRes);
    }

    @PostMapping("/intg/kra/charge")
    public ResponseEntity<?> processEtimsAddChargeItemsListBatch(@RequestBody List<Integer> chargeIds, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Page<Customers> pageRes = null;
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Charge> charges = chargeRepository.findAllById(chargeIds);
            List<Charge> unsyncedCharges = charges.stream()
                    .filter(charge -> !charge.isKraSynced())
                    .collect(Collectors.toList());
            response.put("requestedCount", chargeIds.size());
            response.put("processedCount", unsyncedCharges.size());
            response.put("skippedSyncedCount", charges.size() - unsyncedCharges.size());
            if (unsyncedCharges.isEmpty()) {
                response.put(APIConstants.MESSAGE, "No unsynced charges found for KRA sync");
                return apiResponse(APIConstants.SUCCESS, response);
            }
            KRAUtils.processEtimsAddChargeItemsListBatch(unsyncedCharges);
            response.put(APIConstants.MESSAGE, "Charge sync payload prepared successfully");
            return apiResponse(APIConstants.SUCCESS, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All Charge list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, pageRes);
    }


    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
        MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        if (null == requestDTO.getPage()) requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize()) requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy()) requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder()) requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }
}
