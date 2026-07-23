package com.savbill.cpm.modules.paymentGatewayMaster.service;


import com.savbill.cpm.OnlinePaymentAudit.Service.OnlinePayAuditService;
import com.savbill.cpm.constants.Constants;
import com.savbill.cpm.constants.SubscriberConstants;
import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.kafka.KafkaMessageReceiver;
import com.savbill.cpm.model.common.*;
import com.savbill.cpm.model.common.*;
import com.savbill.cpm.model.postpaid.CustPlanMappping;
import com.savbill.cpm.model.postpaid.CustomerMapper;
import com.savbill.cpm.model.postpaid.PostpaidPlan;
import com.savbill.cpm.modules.acl.constants.AclConstants;
import com.savbill.cpm.modules.auditLog.service.AuditLogService;
import com.savbill.cpm.pojo.BudPay.BudPayPojo;
import com.savbill.cpm.pojo.BudPay.BudPayResponse;
import com.savbill.cpm.model.postpaid.*;
import com.savbill.cpm.modules.Mvno.repository.MvnoRepository;
import com.savbill.cpm.modules.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.savbill.cpm.modules.PaymentConfig.service.PaymentConfigService;
import com.savbill.cpm.modules.paymentGatewayMaster.domain.PaymentGatewayResponse;
import com.savbill.cpm.modules.subscriber.model.ChangePlanRequestDTO;
import com.savbill.cpm.modules.subscriber.model.CustomChangePlanDTO;
import com.savbill.cpm.modules.subscriber.model.CustomersBasicDetailsPojo;
import com.savbill.cpm.modules.subscriber.service.SubscriberService;
import com.savbill.cpm.pojo.AdditionalInformationDTO;
import com.savbill.cpm.pojo.PaginationDetails;
import com.savbill.cpm.pojo.api.CustomersPojo;
import com.savbill.cpm.rabbitMq.message.CustPayDTOMessage;
import com.savbill.cpm.repository.common.CustomerPaymentRepository;
import com.savbill.cpm.repository.common.StaffUserRepository;
import com.savbill.cpm.repository.postpaid.CustPlanMappingRepository;
import com.savbill.cpm.repository.postpaid.CustomerChargeHistoryRepo;
import com.savbill.cpm.repository.postpaid.DebitDocRepository;
import com.savbill.cpm.repository.postpaid.PostpaidPlanRepo;
import com.savbill.cpm.repository.radius.CustomerServiceMappingRepository;
import com.savbill.cpm.repository.radius.CustomersRepository;
import com.savbill.cpm.service.common.CustomersService;
import com.savbill.cpm.service.postpaid.DebitDocService;
import com.savbill.cpm.service.postpaid.PostpaidPlanService;
import com.savbill.cpm.spring.LoggedInUser;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.service.ExBaseAbstractService;
import com.savbill.cpm.core.utillity.log.ApplicationLogger;
import com.savbill.cpm.modules.auditLog.model.AuditForResponseModel;
import com.savbill.cpm.modules.paymentGatewayMaster.domain.PaymentGateWay;
import com.savbill.cpm.modules.paymentGatewayMaster.dto.PaymentGatewayDTO;
import com.savbill.cpm.modules.paymentGatewayMaster.mapper.PaymentGatewayMapper;
import com.savbill.cpm.modules.paymentGatewayMaster.repository.PaymentGatewayRepository;
import com.savbill.cpm.utils.CommonConstants;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentGatewayService extends ExBaseAbstractService<PaymentGatewayDTO, PaymentGateWay, Long> {

    private static final Logger log = LoggerFactory.getLogger(PaymentGatewayService.class);

    @Autowired
    private PaymentGatewayRepository paymentGatewayRepository;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    private PostpaidPlanService planService;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private PaymentConfigService paymentConfigService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private OnlinePayAuditService onlinePayAuditService;

    @Autowired
    private CustomerChargeHistoryRepo customerChargeHistoryRepo;


    public PaymentGatewayService(PaymentGatewayRepository repository, PaymentGatewayMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return " [PaymentGatewayService] ";
    }

    public PaymentGatewayDTO getPGByName(String name) {
        String SUBMODULE = getModuleNameForLog() + " [getPGByName()] ";
        try {
            List<PaymentGateWay> transactionModeList = paymentGatewayRepository.findAllByNameAndIsDeletedIsFalse(name);
            return (null != transactionModeList && 0 < transactionModeList.size())
                    ? getMapper().domainToDTO(transactionModeList.get(0), new CycleAvoidingMappingContext()) : null;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public PaymentGatewayDTO getByIdAndStatus(Long id) {
        return getMapper().domainToDTO(paymentGatewayRepository.findByIdAndStatus(id, CommonConstants.ACTIVE_STATUS), new CycleAvoidingMappingContext());
    }

    public List<PaymentGatewayDTO> getPGForUsers() {
        return this.paymentGatewayRepository.findByUserenableflagAndStatusAndIsDeletedIsFalse(true, CommonConstants.ACTIVE_STATUS)
                .stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<PaymentGatewayDTO> getPGForPartner() {
        return this.paymentGatewayRepository.findByPartnerenableflagAndStatusAndIsDeletedIsFalse(true, CommonConstants.ACTIVE_STATUS)
                .stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<PaymentGatewayDTO> getPGForBoth() {
        return this.paymentGatewayRepository.findByUserenableflagAndPartnerenableflagAndStatusAndIsDeletedIsFalse(true, true, CommonConstants.ACTIVE_STATUS)
                .stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<PaymentGatewayDTO> getAllByStatus() {
        return this.paymentGatewayRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS)
                .stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<AuditForResponseModel> getPGListForAuditFor() {
        String SUBMODULE = getModuleNameForLog() + " [getPGListForAuditFor()] ";
        List<AuditForResponseModel> responseList = new ArrayList<>();
        try {
            List<PaymentGatewayDTO> pgList = getAllByStatus();
            if (null != pgList && 0 < pgList.size()) {
                for (PaymentGatewayDTO pgDTO : pgList) {
                    AuditForResponseModel responseModel = new AuditForResponseModel();
                    responseModel.setId(pgDTO.getId().intValue());
                    responseModel.setName(pgDTO.getName());
                    responseList.add(responseModel);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return responseList;
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);



            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }

            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }

    public ResponseEntity<?> apiResponses(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }


//    public DebitDocSearchPojo getInvoiceDetails(Integer invoiceId, Integer custId) {
//
//        QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
//        BooleanExpression exp = qDebitDocument.isNotNull();
//        exp = exp.and(qDebitDocument.id.eq(invoiceId)).and(qDebitDocument.customer.id.eq(custId));
//
//        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
//
//        List<DebitDocSearchPojo> queryResults =  queryFactory
//                .select(Projections.constructor(
//                        DebitDocSearchPojo.class,
//                        qDebitDocument.customer.title.concat(" ").concat(qDebitDocument.customer.firstname.concat(" ").concat(qDebitDocument.customer.lastname)),
//                        qDebitDocument.billrunstatus,
//                        qDebitDocument.createdate,
//                        qDebitDocument.totalamount,
//                        qDebitDocument.docnumber,
//                        qDebitDocument.billdate,
//                        qDebitDocument.billrunid,
//                        qDebitDocument.amountinwords,
//                        qDebitDocument.discount,
//                        qDebitDocument.latepaymentdate,
//                        qDebitDocument.startdate,
//                        qDebitDocument.endate,
//                        qDebitDocument.tax))
//                .from(qDebitDocument)
//                .where(exp)
//                .fetch();
//
//        return  queryResults.get(0);
//
//    }

     public PaymentGatewayResponse getRazorpayResponse(Long orderId , String pgTransactionId) throws Exception {
        PaymentGatewayResponse paymentGatewayResponse =  new PaymentGatewayResponse();
        paymentGatewayResponse.setOrderId(orderId);
        paymentGatewayResponse.setPgTransactionId(pgTransactionId);
         QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
         CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId))).orElse(null);
         Long redirectTimeInSeconds = 10L;
         if(Objects.isNull(customerPayment)){
             throw new RuntimeException("Customer not available for transaction id");
         }
         if (Objects.nonNull(customerPayment)) {

             {
                 subscriberService.validateTransaction(pgTransactionId , orderId);
                 /**Payment Gateway parameter started**/
                 Optional<Customers> customersformvnoId = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                 HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.RAZORPAY , customersformvnoId.get().getMvnoId());
                 String REDIRECT_CAPTIVE_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.CAPTIVE_REDIRECT_URL);
                 String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.CWSC_REDIRECT_URL);
                 String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.REDIRECT_TIME_IN_SECONDS);
                 String STAFFUSER_USERNAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_USERNAME);
                 String STAFFUSER_PASSWORD = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_PASSWORD);
                 StaffUser staffUser = staffUserRepository.findStaffUserByUsername(STAFFUSER_USERNAME);
                 Long mvnoId = staffUser.getMvnoId().longValue();
                 String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
                 redirectTimeInSeconds = Long.valueOf(REDIRECT_TIME_IN_SECONDS);
                 paymentGatewayResponse.setRedirectTimeInSecond(Integer.valueOf(REDIRECT_TIME_IN_SECONDS));
                 /**Payment Gateway parameter ended**/
                 List<GrantedAuthority> role_name=new ArrayList<>();
                 role_name.add(new SimpleGrantedAuthority("ADMIN"));
                 LoggedInUser user = new LoggedInUser(STAFFUSER_USERNAME, STAFFUSER_PASSWORD, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), customersformvnoId.get().getCreatedById(), customersformvnoId.get().getPartner().getId(), "ADMIN", null, customersformvnoId.get().getMvnoId(), null, customersformvnoId.get().getCreatedById(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
                 UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                 SecurityContextHolder.getContext().setAuthentication(auth);
                 SecurityContextHolder.getContext().setAuthentication(auth);
                 Integer testMvno = getMvnoIdFromCurrentStaff();
//                        model.addAttribute("homeRedirectUrl", clientServiceSrv.getValueByName(ClientServiceConstant.HOME_REDIRECT_URL_CAPTIVE_PORTAL) + "?customerId=" + customerPayment.getCustId() + "&transactionId=" + customerPayment.getOrderId() + "&amount=" + customerPayment.getPayment() + "&currency=" + this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                 Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                 if(customerPayment.getIsFromCaptive()){
                     String url = REDIRECT_CAPTIVE_URL;
                     url = url.replace("{userName}",customers.get().getUsername());
                     url = url.replace("{Password}",customers.get().getPassword());
                     paymentGatewayResponse.setRedirecturl(url);
                 }
                 else {
                     paymentGatewayResponse.setRedirecturl(REDIRECT_CWSC_URL);
                 }

                 ChangePlanRequestDTO requestDTO = new ChangePlanRequestDTO();
                 PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(customerPayment.getPlanId())).get();
                 if(postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW) || postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)) {
                     requestDTO.setPurchaseType(CommonConstants.PLAN_GROUP_RENEW);
                 }
                 else{
                     requestDTO.setPurchaseType("Addon");
                 }
                 requestDTO.setPlanId(Math.toIntExact(customerPayment.getPlanId()));
                 requestDTO.setIsPaymentReceived(false);
                 requestDTO.setRemarks("Transaction ID:-" + pgTransactionId);
                 requestDTO.setIsAdvRenewal(false);
                 requestDTO.setCustId(customers.get().getId());
                 requestDTO.setIsRefund(false);
                 requestDTO.setRecordPaymentDTO(null);
                 requestDTO.setOnlinePurType("RENEW");
                 requestDTO.setAddonStartDate(null);
                 requestDTO.setBillableCustomerId(null);
                 requestDTO.setIsParent(true);
                 requestDTO.setDiscount(0.0000);
                 requestDTO.setNewPlanList(null);
                 requestDTO.setPlanMappingList(null);
                 requestDTO.setPaymentOwnerId(customers.get().getCreatedById());
                 requestDTO.setIsTriggerCoaDm(true);
                 if(customerPayment.getLinkId() != null  && !customerPayment.getLinkId().isEmpty()) {
                     requestDTO.setCustServiceMappingId(Integer.parseInt(customerPayment.getLinkId()));
                 }
                 else{
                     List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findByCustId(customerPayment.getCustId());
                     if(!customerServiceMappingList.isEmpty()){
                         CustomerServiceMapping customerServiceMapping = customerServiceMappingList.get(customerServiceMappingList.size() -1);
                         requestDTO.setCustServiceMappingId(customerServiceMapping.getId());
                     }
                 }
                 String number=String.valueOf(CommonUtils.gen());
                 CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number,null,null);
                 CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                 try {
                     Customers customer = customersService.get(basicDetailsPojo.getId());
                     customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());
                     CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                     List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerIdAndPlanId(customersformvnoId.get().getId() , requestDTO.getPlanId());
                     custPlanMappping = custPlanMappping.stream().filter(custPlanMappping1 -> custPlanMappping1.getOfferPrice() >0).collect(Collectors.toList());
                     Customers customers1 = customersRepository.getOne(customersformvnoId.get().getId());
                     AdditionalInformationDTO additionalInformationDTO =  new AdditionalInformationDTO();
                     String transactionNumber = "";
                     transactionNumber = customerPayment.getOrderId().toString();
                     additionalInformationDTO.setTransactionNumber(transactionNumber);
                     debitDocService.createInvoice(customers1 , Constants.RENEW,"RAZORPAY", null,additionalInformationDTO, null,false,false,null,null);
                 } catch (Exception e) {
                     ApplicationLogger.logger.error("" + e.getMessage(), e);
                     e.printStackTrace();
                 }
                 if (customers.isPresent()) {
                     customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(), customers.get().getId(),orderId.toString(), customerPayment.getPaymentDate().toString(),customers.get().getBuId(),null);

                 }
                 customerPayment.setStatus("Successful");
                 customerPayment.setPgTransactionId(pgTransactionId);
             }
             customerPaymentRepository.save(customerPayment);


         }
       return paymentGatewayResponse;
     }

    public GenericDataDTO getResponseFromBudPay(BudPayPojo budPayPojo, String secretKey, String requestUrl){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        OnlinePayAudit onlinePayAudit = new OnlinePayAudit();
        try{

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(secretKey);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(budPayPojo);

            HttpEntity<String> entity = new HttpEntity<>(requestBody,httpHeaders);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String>  response = restTemplate.exchange(requestUrl,HttpMethod.POST,entity,String.class);

            if(response.getStatusCode() == HttpStatus.OK){
                genericDataDTO.setResponseMessage("Transaction initiated successfully");
                genericDataDTO.setResponseCode(response.getStatusCode().value());
                genericDataDTO.setData(response.getBody());
                // Create an ObjectMapper instance
                ObjectMapper respMapper = new ObjectMapper();
                // Parse JSON string to JsonNode
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                // Access data field
                JsonNode dataNode = rootNode.get("data");
                BudPayResponse budPayResponse = new BudPayResponse();
                budPayResponse.setAuthorizationUrl(dataNode.get("authorization_url").asText());
                budPayResponse.setReference(dataNode.get("reference").asText());
                budPayResponse.setAccessCode(dataNode.get("access_code").asText());
                budPayResponse.setPayerId(dataNode.get("payer_id").asText());
                genericDataDTO.setData(budPayResponse);

                CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.parseLong(dataNode.get("reference").asText()));
                customerPayment.setPaymentLink(dataNode.get("authorization_url").asText());
                customerPaymentRepository.save(customerPayment);

            }else {
                genericDataDTO.setResponseMessage("Transaction initiation failed");
                genericDataDTO.setResponseCode(response.getStatusCode().value());
                genericDataDTO.setData(response.getBody());

            }
        }catch (Exception e){
            ApplicationLogger.logger.error(e.getMessage());
        }
        return genericDataDTO;
    }

    public void InitiateAddonOrRenewAfterPaymentSuccess(CustomerPayment customerPayment) {
        try {
            Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());
            Customers customersformvnoId = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());
            HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PHONEPE , customerPayment.getMvnoid());
            String STAFFUSER_USERNAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_USERNAME);
            String STAFFUSER_PASSWORD = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_PASSWORD);
            StaffUser staffUser = staffUserRepository.findStaffUserByUsername(STAFFUSER_USERNAME);
            Long mvnoId = staffUser.getMvnoId().longValue();
            String type = "";
            String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
            /**Payment Gateway parameter ended**/
            List<GrantedAuthority> role_name=new ArrayList<>();
            role_name.add(new SimpleGrantedAuthority("ADMIN"));
            LoggedInUser user = new LoggedInUser(STAFFUSER_USERNAME, STAFFUSER_PASSWORD, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), customersformvnoId.getCreatedById(), customersformvnoId.getPartner().getId(), "ADMIN", null, customersformvnoId.getMvnoId(), null, customersformvnoId.getCreatedById(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            SecurityContextHolder.getContext().setAuthentication(auth);
            Integer testMvno = getMvnoIdFromCurrentStaff();
            if(customers!=null){
                ChangePlanRequestDTO requestDTO = new ChangePlanRequestDTO();
                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(customerPayment.getPlanId())).get();
                if(postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW) || postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)) {
                    requestDTO.setPurchaseType(CommonConstants.PLAN_GROUP_RENEW);
                }
                else{
                    requestDTO.setPurchaseType("Addon");
                }
                requestDTO.setPlanId(Math.toIntExact(customerPayment.getPlanId()));
                requestDTO.setIsPaymentReceived(false);
                requestDTO.setRemarks("Transaction ID:-" + customerPayment.getPgTransactionId());
                requestDTO.setIsAdvRenewal(false);
                requestDTO.setCustId(customers.getId());
                requestDTO.setIsRefund(false);
                requestDTO.setRecordPaymentDTO(null);
                requestDTO.setOnlinePurType("RENEW");
                requestDTO.setAddonStartDate(null);
                requestDTO.setBillableCustomerId(null);
                requestDTO.setIsParent(true);
                requestDTO.setDiscount(0.0000);
                requestDTO.setNewPlanList(null);
                requestDTO.setPlanMappingList(null);
                requestDTO.setPaymentOwnerId(customers.getCreatedById());
                if(customerPayment.getLinkId() != null  && !customerPayment.getLinkId().isEmpty()) {
                    requestDTO.setCustServiceMappingId(Integer.parseInt(customerPayment.getLinkId()));
                }
                else{
                    List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findByCustId(customerPayment.getCustId());
                    if(!customerServiceMappingList.isEmpty()){
                        CustomerServiceMapping customerServiceMapping = customerServiceMappingList.get(customerServiceMappingList.size() -1);
                        requestDTO.setCustServiceMappingId(customerServiceMapping.getId());
                    }
                }
                String number=String.valueOf(CommonUtils.gen());
                CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, "FLutter wave", null, number,null,null);
                CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                try {
                    Customers customer = customersService.get(basicDetailsPojo.getId());
                    customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());
                    CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                    List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerIdAndPlanId(customersformvnoId.getId() , requestDTO.getPlanId());
                    custPlanMappping = custPlanMappping.stream().filter(custPlanMappping1 -> custPlanMappping1.getOfferPrice() >0).collect(Collectors.toList());
                    Customers customers1 = customersRepository.findByIdAndIsDeletedIsFalse(customersformvnoId.getId());
                    AdditionalInformationDTO additionalInformationDTO =  new AdditionalInformationDTO();
                    String transactionNumber = "";
                    transactionNumber = customerPayment.getOrderId().toString();
                    additionalInformationDTO.setTransactionNumber(transactionNumber);
                    String plangroup = postpaidPlanRepo.findPlangroupById(requestDTO.getPlanId());
                    if (plangroup.equalsIgnoreCase("Registration and Renewal" ) || plangroup.equalsIgnoreCase("Renewal")){
                        type = Constants.RENEW;
                    }else {
                        type = Constants.ADD_ON;
                    }
                    debitDocService.createInvoice(customers1 ,type ,"PHONEPE", null,additionalInformationDTO, null,false,false,null,null);
                } catch (Exception e) {
                    ApplicationLogger.logger.error("" + e.getMessage(), e);
                    e.printStackTrace();
                }
                if (customers!=null) {
                    customersService.sendCustPaymentSuccessMessage("Payment Success", customers.getUsername(), customerPayment.getPayment(), "Online", customers.getMvnoId(), customers.getCountryCode(), customers.getMobile(), customers.getEmail(), customers.getId(),customerPayment.getOrderId().toString(), customerPayment.getPaymentDate().toString(),customers.getBuId(),null);

                }
            }



        }catch (Exception e){
            ApplicationLogger.logger.error("Something went wrong request for plan addon/renew failed due to : "+e.getMessage());
        }
    }

    public PaymentGatewayResponse getCommonPaymentGatewayResponse(Long orderId , String pgTransactionId , String paymentGatewayName) throws Exception {
        PaymentGatewayResponse paymentGatewayResponse =  new PaymentGatewayResponse();
        paymentGatewayResponse.setOrderId(orderId);
        paymentGatewayResponse.setPgTransactionId(pgTransactionId);
        QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
        CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId))).orElse(null);
        Long redirectTimeInSeconds = 10L;
        if(Objects.isNull(customerPayment)){
            throw new RuntimeException("Customer not available for transaction id");
        }
        if (Objects.nonNull(customerPayment)) {

            {
                subscriberService.validateTransaction(pgTransactionId , orderId);
                /**Payment Gateway parameter started**/
                Optional<Customers> customersformvnoId = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(paymentGatewayName, customersformvnoId.get().getMvnoId());
                String REDIRECT_CAPTIVE_URL = null;
                String REDIRECT_CWSC_URL = null;
                String REDIRECT_TIME_IN_SECONDS = null;
                String STAFFUSER_USERNAME = null;
                String STAFFUSER_PASSWORD = null;
                if(paymentGatewayName.equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY)) {
                    REDIRECT_CAPTIVE_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.CAPTIVE_REDIRECT_URL);
                    REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.CWSC_REDIRECT_URL);
                }
                STAFFUSER_USERNAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_USERNAME);
                STAFFUSER_PASSWORD = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_PASSWORD);
                REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.REDIRECT_TIME_IN_SECONDS);
                StaffUser staffUser = staffUserRepository.findStaffUserByUsername(STAFFUSER_USERNAME);
                Long mvnoId = staffUser.getMvnoId().longValue();
                String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
                redirectTimeInSeconds = Long.valueOf(REDIRECT_TIME_IN_SECONDS);
                paymentGatewayResponse.setRedirectTimeInSecond(Integer.valueOf(REDIRECT_TIME_IN_SECONDS));
                /**Payment Gateway parameter ended**/
                List<GrantedAuthority> role_name=new ArrayList<>();
                role_name.add(new SimpleGrantedAuthority("ADMIN"));
                LoggedInUser user = new LoggedInUser(STAFFUSER_USERNAME, STAFFUSER_PASSWORD, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), customersformvnoId.get().getCreatedById(), customersformvnoId.get().getPartner().getId(), "ADMIN", null, customersformvnoId.get().getMvnoId(), null, customersformvnoId.get().getCreatedById(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
                SecurityContextHolder.getContext().setAuthentication(auth);
                Integer testMvno = getMvnoIdFromCurrentStaff();
//                        model.addAttribute("homeRedirectUrl", clientServiceSrv.getValueByName(ClientServiceConstant.HOME_REDIRECT_URL_CAPTIVE_PORTAL) + "?customerId=" + customerPayment.getCustId() + "&transactionId=" + customerPayment.getOrderId() + "&amount=" + customerPayment.getPayment() + "&currency=" + this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                if(customerPayment.getIsFromCaptive()){
                    String url = REDIRECT_CAPTIVE_URL;
                    url = url.replace("{userName}",customers.get().getUsername());
                    url = url.replace("{Password}",customers.get().getPassword());
                    paymentGatewayResponse.setRedirecturl(url);
                }
                else {
                    paymentGatewayResponse.setRedirecturl(REDIRECT_CWSC_URL);
                }

                ChangePlanRequestDTO requestDTO = new ChangePlanRequestDTO();
                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(customerPayment.getPlanId())).get();
                if(postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW) || postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)) {
                    requestDTO.setPurchaseType(CommonConstants.PLAN_GROUP_RENEW);
                }
                else{
                    requestDTO.setPurchaseType("Addon");
                }
                requestDTO.setPlanId(Math.toIntExact(customerPayment.getPlanId()));
                requestDTO.setIsPaymentReceived(false);
                requestDTO.setRemarks("Transaction ID:-" + pgTransactionId);
                requestDTO.setIsAdvRenewal(false);
                requestDTO.setCustId(customers.get().getId());
                requestDTO.setIsRefund(false);
                requestDTO.setRecordPaymentDTO(null);
                requestDTO.setOnlinePurType("RENEW");
                requestDTO.setAddonStartDate(null);
                requestDTO.setBillableCustomerId(null);
                requestDTO.setIsParent(true);
                requestDTO.setDiscount(0.0000);
                requestDTO.setNewPlanList(null);
                requestDTO.setPlanMappingList(null);
                requestDTO.setPaymentOwnerId(customers.get().getCreatedById());
                requestDTO.setIsTriggerCoaDm(true);
                if(customerPayment.getLinkId() != null  && !customerPayment.getLinkId().isEmpty()) {
                    requestDTO.setCustServiceMappingId(Integer.parseInt(customerPayment.getLinkId()));
                }
                else{
                    List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findByCustId(customerPayment.getCustId());
                    if(!customerServiceMappingList.isEmpty()){
                        CustomerServiceMapping customerServiceMapping = customerServiceMappingList.get(customerServiceMappingList.size() -1);
                        requestDTO.setCustServiceMappingId(customerServiceMapping.getId());
                    }
                }
                String number=String.valueOf(CommonUtils.gen());
                CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number,null,null);
                CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                try {
                    Customers customer = customersService.get(basicDetailsPojo.getId());
                    customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());
                    CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                    List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerIdAndPlanId(customersformvnoId.get().getId() , requestDTO.getPlanId());
                    custPlanMappping = custPlanMappping.stream().filter(custPlanMappping1 -> custPlanMappping1.getOfferPrice() >0).collect(Collectors.toList());
                    Customers customers1 = customersRepository.getOne(customersformvnoId.get().getId());
                    AdditionalInformationDTO additionalInformationDTO =  new AdditionalInformationDTO();
                    String transactionNumber = "";
                    transactionNumber = customerPayment.getOrderId().toString();
                    additionalInformationDTO.setTransactionNumber(transactionNumber);
                    additionalInformationDTO.setAmount(customerPayment.getPayment());
                    if(postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW) || postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)) {
                        debitDocService.createInvoice(customers1 , Constants.RENEW,paymentGatewayName, null,additionalInformationDTO, null,false,false,null,null);
                    }
                    else{
                        debitDocService.createInvoice(customers1 , Constants.ADD_ON,paymentGatewayName, null,additionalInformationDTO, null,false,false,null,null);
                    }
                } catch (Exception e) {
                    ApplicationLogger.logger.error("" + e.getMessage(), e);
                    e.printStackTrace();
                }
                if (customers.isPresent()) {
                    customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(), customers.get().getId(),orderId.toString(), customerPayment.getPaymentDate().toString(),customers.get().getBuId(),null);

                }
                customerPayment.setStatus("Successful");
                customerPayment.setPgTransactionId(pgTransactionId);
                customerPayment.setTransactionDate(LocalDateTime.now());
            }
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUYPLAN, AclConstants.OPERATION_BUYPLAN_BUY, null,null , Long.valueOf(customerPayment.getCustId()),customerPayment.getCustomerUsername());
            customerPaymentRepository.save(customerPayment);


        }
        return paymentGatewayResponse;
    }

    public void addCustomerPayment(CustPayDTOMessage message){
        log.info(
                "PG >> addCustomerPayment START | OrderId={} | Status={} | CustId={} | Amount={} | AccountNo={} | CustomerUUID={}",
                message.getOrderId(),
                message.getStatus(),
                message.getCustId(),
                message.getPayment(),
                message.getAccountNumber()
        );
        CustomerPayment customerPayment = null;
        customerPayment = customerPaymentRepository.findByOrderId(message.getOrderId());
        log.info("PG >> No existing payment found for OrderId={} | Creating NEW record", message.getOrderId());
        if(Objects.isNull(customerPayment)) {
            customerPayment = new CustomerPayment();
            customerPayment.setOrderId(message.getOrderId());
            customerPayment.setPayment(message.getPayment());
            customerPayment.setStatus(message.getStatus());
            customerPayment.setCustId(message.getCustId());
            customerPayment.setCustomerUsername(message.getCustomerUsername());
            customerPayment.setPlanId(message.getPlanId());
            customerPayment.setMvnoid(message.getMvnoid());
            if (message.getAccountNumber() != null) {
                customerPayment.setAccountNumber(message.getAccountNumber());
            }
            customerPayment.setBuid(message.getBuid());
            if (message.getMerchantName() != null) {
                customerPayment.setMerchantName(message.getMerchantName());
            }
            if (message.getPgTransactionId() != null) {
                customerPayment.setPgTransactionId(message.getPgTransactionId());
            }
            if (message.getPaymentLink() != null) {
                customerPayment.setPaymentLink(message.getPaymentLink());
            }
            if (message.getPartnerPaymentId() != null) {
                customerPayment.setPartnerPaymentId(message.getPartnerPaymentId());
            }
            if (message.getIsFromCaptive() != null) {
                customerPayment.setIsFromCaptive(message.getIsFromCaptive());
            }
            if (message.getChecksum() != null) {
                customerPayment.setChecksum(message.getChecksum());
            }
            if (message.getCreditDocumentId() != null) {
                customerPayment.setCreditDocumentId(message.getCreditDocumentId());
            }
            if (message.getCustServiceMappingId() != null) {
                customerPayment.setLinkId(message.getCustServiceMappingId().toString());
            }
            if (message.getTransactionDate() != null && !message.getTransactionDate().trim().isEmpty()) {
                customerPayment.setTransactionDate(LocalDateTime.parse(message.getTransactionDate()));
            }
            customerPayment.setPaymentDate(LocalDateTime.parse(message.getPaymentDate()));
            customerPaymentRepository.save(customerPayment);
            log.info("PG >> NEW Payment saved successfully | OrderId={}", message.getOrderId());
        }
        else{
            log.info("PG >> Existing payment FOUND | Updating payment status | OrderId={} | OldStatus={} | NewStatus={}",
                    message.getOrderId(), customerPayment.getStatus(), message.getStatus());
            customerPayment.setStatus(message.getStatus());
            customerPayment.setTransactionDate(LocalDateTime.now());
            if(message.getPgTransactionId() != null){
                customerPayment.setPgTransactionId(message.getPgTransactionId());
            }
            customerPaymentRepository.save(customerPayment);
            log.info("PG >> Existing payment UPDATED successfully | OrderId={}", message.getOrderId());
        }
    }

}
