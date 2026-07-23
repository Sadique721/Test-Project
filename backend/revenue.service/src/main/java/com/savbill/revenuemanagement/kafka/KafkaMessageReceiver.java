package com.savbill.revenuemanagement.kafka;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.revenuemanagement.InvoiceIntigration.InvoiceIntigrationService;
import com.savbill.revenuemanagement.InvoiceIntigration.SendinvoiceQRMessage;
import com.savbill.revenuemanagement.KRA.Dtos.KRAGenericResponseDTO;
import com.savbill.revenuemanagement.KRA.Dtos.KRAGenericResponseDTOMessage;
import com.savbill.revenuemanagement.KRA.KRAConstant;
import com.savbill.revenuemanagement.core.entity.ladger.CreditDocument;
import com.savbill.revenuemanagement.core.repository.ledger.CreditDocRepository;
import com.savbill.revenuemanagement.core.CustomerOnlinePaymentAudit.service.CustomerOnlinePaymentAuditService;
import com.savbill.revenuemanagement.core.Mvno.domain.UpdateMvnoData;
import com.savbill.revenuemanagement.core.Mvno.service.MvnoService;
import com.savbill.revenuemanagement.core.MvnoDiscountManagement.MvnoDiscountService;
import com.savbill.revenuemanagement.core.constants.CommonConstants;
import com.savbill.revenuemanagement.core.dto.customer.CustPayDTOMessage;
import com.savbill.revenuemanagement.core.dto.customer.CustomerUpdateMessage;
import com.savbill.revenuemanagement.core.dto.invoice.OnlineInvoicePaymentDTO;
import com.savbill.revenuemanagement.core.entity.customers.Customers;
import com.savbill.revenuemanagement.core.entity.customers.SubscriberService;
import com.savbill.revenuemanagement.core.entity.customers.UpdateChargeHistoryMessage;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.entity.partner.Partner;
import com.savbill.revenuemanagement.core.entity.partner.PricebookService;
import com.savbill.revenuemanagement.core.entity.staff.RolesService;
import com.savbill.revenuemanagement.core.entity.staff.StaffUserService;
import com.savbill.revenuemanagement.core.entity.staff.TeamsService;
import com.savbill.revenuemanagement.core.exceptions.AlreadyExistException;
import com.savbill.revenuemanagement.core.integrationMenu.ThirdPartyIntegrationMenuDto;
import com.savbill.revenuemanagement.core.integrationMenu.ThirdPartyIntegrationMenuService;
import com.savbill.revenuemanagement.core.repository.customer.CustPlanMappingService;
import com.savbill.revenuemanagement.core.repository.customer.CustomerChargeHistoryRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomerServiceMapRepository;
import com.savbill.revenuemanagement.core.repository.customer.CustomersRepository;
import com.savbill.revenuemanagement.core.repository.debit.DebitDocRepository;
import com.savbill.revenuemanagement.core.repository.partner.PartnerRepository;
import com.savbill.revenuemanagement.core.repository.staff.StaffUserRepository;
import com.savbill.revenuemanagement.core.service.ClientServ.service.ClientServiceSrv;
import com.savbill.revenuemanagement.core.service.Inventory.ProductServiceImpl;
import com.savbill.revenuemanagement.core.service.common.InvoiceUtil;
import com.savbill.revenuemanagement.core.service.ledger.CreditDocService;
import com.savbill.revenuemanagement.core.service.ledger.DebitDocService;
import com.savbill.revenuemanagement.core.service.partner.PartnerLedgerDetailsService;
import com.savbill.revenuemanagement.core.service.partner.PartnerService;
import com.savbill.revenuemanagement.core.service.postpaid.PostpaidInvoiceService;
import com.savbill.revenuemanagement.core.service.prepaid.DbrService;
import com.savbill.revenuemanagement.core.service.prepaid.PartnerCommissionService;
import com.savbill.revenuemanagement.core.service.prepaid.PrepaidInvoiceService;
import com.savbill.revenuemanagement.isp.IspMainPayload;
import com.savbill.revenuemanagement.mastermanagement.Area.service.AreaService;
import com.savbill.revenuemanagement.mastermanagement.BankManagement.service.BankManagementService;
import com.savbill.revenuemanagement.mastermanagement.Branch.service.BranchService;
import com.savbill.revenuemanagement.mastermanagement.BusinessUnit.service.BusinessUnitService;
import com.savbill.revenuemanagement.mastermanagement.City.service.CityService;
import com.savbill.revenuemanagement.mastermanagement.Country.service.CountryService;
import com.savbill.revenuemanagement.mastermanagement.Department.service.DepartmentService;
import com.savbill.revenuemanagement.mastermanagement.Pincode.service.PincodeService;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.service.ServiceAreaService;
import com.savbill.revenuemanagement.mastermanagement.State.service.StateService;
import com.savbill.revenuemanagement.productmanagement.Charge.service.ChargeService;
import com.savbill.revenuemanagement.productmanagement.Charge.domain.Charge;
import com.savbill.revenuemanagement.productmanagement.Charge.repocitory.ChargeRepository;
import com.savbill.revenuemanagement.productmanagement.Discount.service.DiscountService;
import com.savbill.revenuemanagement.productmanagement.Plan.service.PostPaidPlanService;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.service.PlanGroupService;
import com.savbill.revenuemanagement.productmanagement.Tax.service.TaxService;
import com.savbill.revenuemanagement.productmanagement.childcustomer.UpdateChildCustometMessesge;
import com.savbill.revenuemanagement.productmanagement.childcustomer.entity.ChildCustomer;
import com.savbill.revenuemanagement.productmanagement.childcustomer.service.ChildCustomerService;
import com.savbill.revenuemanagement.productmanagement.parentchildmapping.CafChildCustomerApproveMessege;
import com.savbill.revenuemanagement.productmanagement.parentchildmapping.ParentChildMappinService;
import com.savbill.revenuemanagement.productmanagement.parentchildmapping.ParentChildMappingRel;
import com.savbill.revenuemanagement.productmanagement.servicePlan.service.ServicesService;
import com.savbill.revenuemanagement.rabbitmq.*;
import com.savbill.revenuemanagement.rabbitmq.messages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.MasterManagementMessages.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.*;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage.SaveServicesSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.inventory.CustomerInventoryRevenueMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.inventory.ProductMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.inventory.RecordPaymentMessage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Component
public class KafkaMessageReceiver implements Runnable{

    @Autowired
    private CountryService countryService;
    @Autowired
    private StateService stateService;
    @Autowired
    private CityService cityService;
    @Autowired
    private PincodeService pincodeService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private BusinessUnitService businessUnitService;
    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    private BankManagementService bankManagementService;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private MvnoService mvnoService;
    @Autowired
    private TeamsService teamsService;
    @Autowired
    private RolesService roleService;
    @Autowired
    ServicesService servicesService;
    @Autowired
    TaxService taxService;
    @Autowired
    ChargeService chargeService;
    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    PostPaidPlanService postPaidPlanService;
    @Autowired
    DiscountService discountService;
    @Autowired
    PlanGroupService planGroupService;
    @Autowired
    PrepaidInvoiceService prepaidInvoiceService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageReceiverWithThread messageReceiverWithThread;
    @Autowired
    private DebitDocRepository debitDocRepository;
    @Autowired
    private CreditDocRepository creditDocRepository;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    PricebookService pricebookService;
    @Autowired
    private CustPlanMappingService custPlanMappingService;
    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    PartnerCommissionService partnerCommissionService;
    @Autowired
    PartnerLedgerDetailsService partnerLedgerDetailsService;
    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    DbrService dbrService;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    @Autowired
    CreditDocService creditDocService;
    @Autowired
    PostpaidInvoiceService postpaidInvoiceService;
    @Autowired
    CustomerChargeHistoryRepository customerChargeHistoryRepository;
    @Autowired
    private Tracer tracer;
    @Autowired
    private CustomerServiceMapRepository customerServiceMapRepository;

    @Autowired
    SubscriberService subscriberService;
    @Autowired
    private MvnoDiscountService mvnoDiscountService;

    @Autowired
    private InvoiceUtil invoiceUtil;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private InvoiceIntigrationService invoiceIntigrationService;

    @Autowired
    private ThirdPartyIntegrationMenuService thirdPartyIntegrationMenuService;
    @Autowired
    KafkaConsumerConfig consumerConfig;
    @Autowired
    private ChildCustomerService childCustomerService;
    @Autowired
    private ParentChildMappinService parentChildMappinService;

    @Autowired
    private CustomerOnlinePaymentAuditService customerOnlinePaymentAuditService;

    @Autowired
   private DepartmentService departmentService;

    private final ExecutorService executor = new ThreadPoolExecutor(30, 50, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());



    private static Log log = LogFactory.getLog(KafkaMessageReceiver.class);
    private static final Logger logger = Logger.getLogger(MessageReceiver.class);

    private final Map<String, Consumer<KafkaMessageData>> messageHandlers = new HashMap<>();


    public KafkaMessageReceiver() {

        //Receive From CMS
        messageHandlers.put("SaveServicesSharedDataMessage", this::handlesavePlanService);
        messageHandlers.put("UpdateServicesSharedDataMessage", this::handleupdatePlanService);
        messageHandlers.put("SaveTaxSharedDataMessage", this::handlesaveTaxmessage);
        messageHandlers.put("UpdateTaxSharedDataMessage", this::handleupdateTaxmessage);
        messageHandlers.put("SaveChargeSharedDataMessage", this::handlesaveChargemessage);
        messageHandlers.put("UpdateChargeSharedDataMessage", this::handleupdateChargemessage);
        messageHandlers.put("SavePlanSharedDataMessage", this::handlesavePostPaidPlanmessage);
        messageHandlers.put("UpdatePlanSharedDataMessage", this::handleupdatePostPaidPlanmessage);
        messageHandlers.put("SaveDiscountSharedMessage", this::handlesaveDiscountmessage);
        messageHandlers.put("UpdateDiscountSharedMessage", this::handleupdateDiscountmessage);
        messageHandlers.put("SavePlanGroupSharedDataMessage", this::handlesavePlanGroupmessage);
        messageHandlers.put("UpdatePlanGroupSharedDataMessage", this::handleupdatePlanGroupmessage);
        messageHandlers.put("SaveCustomerDataShareMessage", this::handlesaveCustomersmessage);
        messageHandlers.put("UpdateCustomerShareDataMessage", this::handleMessageUpdateCustomer);
        messageHandlers.put("ChangePlanMessage", this::handleMessageChangePlanRevenue);
        messageHandlers.put("ChangePlanMessage:DIRECT_CHARG", this::handleMessageCustDirectChargeRevenue);
        messageHandlers.put("CaftoCustomerMessage", this::handleMessageCafToCustomere);
        messageHandlers.put("AppproveOrgInvoiceMessage", this::handleMessageApproveOrgInvoice);
        messageHandlers.put("SavePricebookSharedMessage:PRICEBOOK_CREATE", this::handleMessagePricebook);
        messageHandlers.put("UpdateCustomerCprDateAndStatus", this::handleMessageUpdateCPr);
        messageHandlers.put("UpdatePricebookSharedMessage:PRICEBOOK_UPDATE", this::handleMessagePriceUpdatebook);
        messageHandlers.put("CustomerTerminationMessage", this::handleCustomerChangeStaus);
        messageHandlers.put("ShiftlocationMessage", this::handleShiftLocationParters);
        messageHandlers.put("SavePartnerPaymentMessage", this::handlePaymentMessage);
        messageHandlers.put("ServiceTerminationMessage", this::handleMessageServiceTermination);
        messageHandlers.put("SendOnlinePaymentRevenueMessage", this::handleMessageForOnlinePayementAdjustment);
        messageHandlers.put("DbrHoldResumeMessage", this::handleMessageForInventoryCreditNote);
        messageHandlers.put("CustomerDiscountPojo", this::handleupdateCustomerDiscountmessage);
        messageHandlers.put("SaveVoucherBatchSharedDataMessage", this::handlesaveVoucherBatchMessage);
        messageHandlers.put("BudPayPaymentMessage", this::handleBudPayPaymentMessage);
        messageHandlers.put("MvnoDiscountMessage", this::handleMvnoDiscountMessage);
        messageHandlers.put("BudpayChangePlanMessage", this::handleBudpayChangePlanMessage);
        messageHandlers.put("CreditDocMessage", this::handleMessageCreditDocFromAPIGW);
        messageHandlers.put("CustomerUpdateMessage", this::handleCustomerStatusChangeMessage);
        messageHandlers.put("ChildCustomer", this::handleSaveChildCustomer);
        messageHandlers.put("UpdateChildCustometMessesge", this::handleUpdateChildCustomer);
        messageHandlers.put("ParentChildMappingRel", this::handleParentChildMappingRel);
        messageHandlers.put("UpdateChargeHistoryMessage",this::handleUpdateCustChargeHistory);
        messageHandlers.put("CafChildCustomerApproveMessege",this::handleCafChildCustomerApproveMessege);
        messageHandlers.put("PlanUpdateMessage", this::handlePlanUpdateMessage);
        messageHandlers.put("AutoRenewalBoosterPlanMessage", this::handleAutoRenewalBoosterPlanMessage);

        //Receive From Inventory
        messageHandlers.put("ProductMessage", this::handleMessageInventoryProductRevenue);
        messageHandlers.put("CustomerInventoryRevenueMessage", this::handleMessageInventoryCustomerRevenue);
        messageHandlers.put("RecordPaymentMessage", this::handleMessageForInventoryCreditNoteFromInventory);

        //Receive From Common
        messageHandlers.put("SaveCountrySharedDataMessage", this::handleMessageCreateCountry);
        messageHandlers.put("UpdateCountrySharedDataMessage", this::handleMessageUpdateCountry);

        messageHandlers.put("SaveStateSharedDataMessage", this::handleMessageCreateState);
        messageHandlers.put("UpdateStateSharedDataMessage", this::handleMessageUpdateState);

        messageHandlers.put("UpdateStaffUserSharedDataMessage", this::handleMessageUpdateStaffUser);
        messageHandlers.put("SaveStaffUserSharedDataMessage", this::handleStaffUserRevenue);

        messageHandlers.put("SaveCitySharedDataMessage", this::handleMessageCreateCity);
        messageHandlers.put("UpdateCitySharedDataMessage", this::handleMessageUpdateCity);

        messageHandlers.put("SavePincodeSharedDataMessage", this::handleMessageCreatePincode);
        messageHandlers.put("UpdatePincodeSharedDataMessage", this::handleMessageUpdatePincode);
        messageHandlers.put("SaveAreaSharedDataMessage", this::handleMessageCreateArea);
        messageHandlers.put("UpdateAreaSharedDataMessage", this::handleMessageUpdateArea);

        messageHandlers.put("SaveServiceAreaSharedDataMessge", this::handleMessageCreateServiceArea);
        messageHandlers.put("UpdateServiceAreaSharedDataMessage", this::handleMessageUpdateServiceArea);
        messageHandlers.put("SaveBusinessUnitSharedDataMessage", this::handleMessageCreateBusinessUnit);
        messageHandlers.put("UpdateBusinessUnitSharedDataMessage", this::handleMessageUpdateBusinessUnit);

        messageHandlers.put("SaveBankManagementSharedDataMessage", this::handleMessageSaveBank);
        messageHandlers.put("UpdateBankManagementSharedDataMessage", this::handleMessageUpdateBank);

        messageHandlers.put("SaveBranchSharedDataMessage", this::handleMessageCreateBranch);
        messageHandlers.put("UpdateBranchSharedData", this::handleMessageUpdateBranch);

        messageHandlers.put("CommonRoleMessage:CREATE_DATA_ROLE", this::handleMessageRoleCreateFromCMS);
        messageHandlers.put("CommonRoleMessage:DELETE_DATA_ROLE", this::handleMessageRoleDeleteFromCMS);
        messageHandlers.put("UpdateRoleSharedDataMessage", this::handleMessageUpdateRole);

        messageHandlers.put("SaveMvnoSharedDataMessage", this::handleMessageForMvnoCreate);
        messageHandlers.put("UpdateMvnoSharedDataMessage", this::handleMessageForMvnoUpdate);

        messageHandlers.put("SaveClientServMessge", this::handleMessageCreateClientService);
        messageHandlers.put("UpdateClientServMessage", this::handleMessageUpdateClientService);

        messageHandlers.put("SaveTeamsSharedSharedData", this::handleMessageCreateTeam);
        messageHandlers.put("UpdateTeamsSharedData", this::handleMessageUpdateTeam);
        messageHandlers.put("UpdateMvnoData", this::handleMessageCreateMvnoISP);
        messageHandlers.put("SavePartnerSharedDataMessage:" + KafkaConstant.CREATE_PARTNER, this::handleSavePartnerData);
        messageHandlers.put("UpdatePartnerSharedDataMessage:" + KafkaConstant.UPDATE_PARTNER, this::handleUpdatePartnerData);
        messageHandlers.put("ChangePlanMessageList:DIRECT_CHARGLIST", this::handleCustDirectChargeListRevenue);

        //intigration handler started
        messageHandlers.put("OnlineInvoicePaymentDTO:" + KafkaConstant.INVOICE_PAYMENT_PURCHASE, this::handleOnlineInvoicePayment);

        //Integration Call
        messageHandlers.put("IspMainPayload:ResponseCode", this::handleIspPayloadResponseFromIntegration);
        messageHandlers.put("SendinvoiceQRMessage:" + KafkaConstant.SEND_QR, this::handleInvoiceIntigrationMessage);
        messageHandlers.put("ThirdPartyIntegrationMenuDto:" + KafkaConstant.CREATE, this::handleSaveThirdPartyIntigrationMessage);
        messageHandlers.put("ThirdPartyIntegrationMenuDto:" + KafkaConstant.UPDATE, this::handleUpdateThirdPartyIntigrationMessage);
        messageHandlers.put("ThirdPartyIntegrationMenuDto:" + KafkaConstant.DELETE, this::handleDeleteThirdPartyIntigrationMessage);
        messageHandlers.put("CustPayDTOMessage:" + KafkaConstant.ADD_WALLET , this::handleAddWalletPayment);
        messageHandlers.put("SavePlanAssignmentMessage", this::handlePlanServiceAreaMapping);
        messageHandlers.put("CustPayDTOMessage",this::handleOnlinePayment);
        messageHandlers.put("KRAGenericResponseDTOMessage:"+ KRAConstant.ADDINVOICE,this::handleKRASaleResponse);
        messageHandlers.put("KRAGenericResponseDTOMessage:"+ KRAConstant.ADDCREDIT,this::handleKRACreditNoteResponse);
        messageHandlers.put("KRAGenericResponseDTOMessage:"+ KRAConstant.ADDITEMS,this::handleKRAAddItemsResponse);


        messageHandlers.put("ServiceChnageStatus", this::handleServicePause);

        messageHandlers.put("SaveDepartmentSharedDataMessage", this::handleDepartment);
        messageHandlers.put("UpdateDepartmentSharedDataMessage", this::handleUpdateDepartment);
        messageHandlers.put("UpdateCustplanMappingMessage", this::handleUpdateCustPlanMapping);
    }



    /**
     * Consumes messages from multiple Kafka topics and processes them based on their data type and event type.
     * The method subscribes to several topics and continuously polls for new messages.
     * Each message is processed asynchronously based on its type, and the corresponding handler is invoked.
     *
     * If no handler is found for a specific combination of data type and event type, it will try to find a handler based only on the data type.
     *
     * This method is executed within a transactional context to ensure consistency of operations.
     *
     * <p>Each message is processed asynchronously using an {@link CompletableFuture} to ensure that handlers can operate concurrently
     * without blocking the main consumer thread.</p>
     *
     * <p>Exceptions during message processing are logged with details about the failed record.</p>
     *
     */
    @javax.transaction.Transactional
    @Override
    public void run() {
        // Create consumer factory with proper type information
        DefaultKafkaConsumerFactory<String, KafkaMessageData> factory =
                new DefaultKafkaConsumerFactory<>(consumerConfig.packetDataPropsPrimary(),
                        new StringDeserializer(),
                        new JsonDeserializer<>(KafkaMessageData.class, false));

        // Create the consumer from factory
        try (KafkaConsumer<String, KafkaMessageData> primaryConsumer = (KafkaConsumer<String, KafkaMessageData>) factory.createConsumer()) {

            // Subscribe to topics
            primaryConsumer.subscribe(Collections.unmodifiableList(Arrays.asList(
                    KafkaConstant.KAFKA_COMMON_TOPIC,
                    KafkaConstant.KAFKA_CMS_TOPIC,
                    KafkaConstant.KAFKA_PMS_TOPIC,
                    KafkaConstant.KAFKA_INVENTORY_TOPIC,
                    KafkaConstant.KAFKA_INTEGRATION_TOPIC,
                    KafkaConstant.KAFKA_CMS_CHANGE_PLAN_TOPIC,
                    KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA)));

            long startTime = System.currentTimeMillis();

            while (true) {
                ConsumerRecords<String, KafkaMessageData> records = primaryConsumer.poll(Duration.ofMillis(5000));
                log.debug(".......(Received Kafka records from topic. Number of records)........: " + records.count());

                for (ConsumerRecord<String, KafkaMessageData> record : records) {
                    try {
                        KafkaMessageData message = record.value();
                        String dataType = message.getDataType();
                        String eventType = message.getEventType();

                        // key with both dataType and eventType
                        String keyWithEventType = dataType + ":" + eventType;


                        // handler with both dataType and eventType
                        Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);

                        if (handler == null) {
                            handler = messageHandlers.get(dataType);
                        }

                        if (handler != null) {
                            log.debug("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : dataType));
                            // accept message in future task
                            Consumer<KafkaMessageData> finalHandler = handler;
//                            CompletableFuture.runAsync(() -> finalHandler.accept(message), executor);
                            Span parentSpan = tracer.currentSpan();
                            TraceContext traceContext = (parentSpan != null) ? parentSpan.context() : null;

                            Span span = (traceContext != null)
                                    ? tracer.newChild(traceContext).name("KafkaMessageProcessing").start()
                                    : tracer.nextSpan().name("KafkaMessageProcessing").start();
                            CompletableFuture.runAsync(() -> {
                                try (Tracer.SpanInScope innerScope = tracer.withSpanInScope(span)) {
                                    finalHandler.accept(message);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    span.error(e);
                                } finally {
                                    span.finish();
                                }
                            }, executor);
                        }
                        log.debug("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
                    } catch (Exception e) {
                        log.error("Error processing message at offset " + record.offset() + " from partition " + record.partition(), e);
                    }
                }

                long endTime = System.currentTimeMillis();
                log.debug(":::::::::::::::::: (Total time taken for per poll in consumer (ms)):::::::::::::: " + (endTime - startTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Radius-Micro-Service: " + e.getMessage(), e);
        }
    }

//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_COMMON_TOPIC}, groupId = KafkaConstant.KAFKA_COMMON_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCommonMicroService(KafkaMessageData message) {
//        try {
            /*if(message.getDataType().equalsIgnoreCase("SaveCountrySharedDataMessage")) {
                CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
                countryService.saveCountry(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveStateSharedDataMessage")) {
                StateSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),StateSharedDataMessage.class);
                stateService.saveStateEntity(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveCitySharedDataMessage")) {
                CitySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CitySharedDataMessage.class);
                cityService.saveCityEntity(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SavePincodeSharedDataMessage")) {
                SavePincodeSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SavePincodeSharedDataMessage.class);
                pincodeService.savePincode(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveAreaSharedDataMessage")) {
                SaveAreaSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveAreaSharedDataMessage.class);
                areaService.saveAreaEntity(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveServiceAreaSharedDataMessge")) {
                SaveServiceAreaSharedDataMessge dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveServiceAreaSharedDataMessge.class);
                serviceAreaService.saveServiceArea(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveBusinessUnitSharedDataMessage")) {
                SaveBusinessUnitSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveBusinessUnitSharedDataMessage.class);
                businessUnitService.saveBusineeUnit(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveBranchSharedDataMessage")) {
                SaveBranchSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveBranchSharedDataMessage.class);
                branchService.saveBranch(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveStaffUserSharedDataMessage")) {
                SaveStaffUserSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveStaffUserSharedDataMessage.class);
                staffUserService.saveStaffuser(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveClientServMessge")) {
                SaveClientServMessge dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveClientServMessge.class);
                clientServiceSrv.saveSharedClientService(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveBankManagementSharedDataMessage")) {
                SaveBankSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveBankSharedDataMessage.class);
                bankManagementService.saveBankdata(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveRoleSharedDataMessage")) {
                SaveRoleSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveRoleSharedDataMessage.class);
                rolesService.createNewRole(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveMvnoSharedDataMessage")) {
                SaveMvnoSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveMvnoSharedDataMessage.class);
                mvnoService.saveMVNOEntity(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("SaveTeamsSharedSharedData")) {
                SaveTeamsSharedSharedData dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveTeamsSharedSharedData.class);
                teamsService.saveTeams(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateCountrySharedDataMessage")) {
                CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
                countryService.updateCountry(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateStateSharedDataMessage")) {
                StateSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),StateSharedDataMessage.class);
                stateService.updateStateEntity(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateCitySharedDataMessage")) {
                CitySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CitySharedDataMessage.class);
                cityService.updateCityEntity(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdatePincodeSharedDataMessage")) {
                SavePincodeSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SavePincodeSharedDataMessage.class);
                pincodeService.updatePincode(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateAreaSharedDataMessage")) {
                SaveAreaSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveAreaSharedDataMessage.class);
                areaService.updateAreaEntity(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateServiceAreaSharedDataMessage")) {
                SaveServiceAreaSharedDataMessge dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveServiceAreaSharedDataMessge.class);
                serviceAreaService.updateServiceArea(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateBusinessUnitSharedDataMessage")) {
                SaveBusinessUnitSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveBusinessUnitSharedDataMessage.class);
                businessUnitService.updateBusinessUnit(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateBranchSharedData")) {
                SaveBranchSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveBranchSharedDataMessage.class);
                branchService.updateBranch(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateStaffUserSharedDataMessage")) {
                UpdateStaffUserSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),UpdateStaffUserSharedDataMessage.class);
                staffUserService.updateStaffUser(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateClientServMessage")) {
                UpdateClientServMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),UpdateClientServMessage.class);
                clientServiceSrv.updateSharedClientService(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateBankManagementSharedDataMessage")) {
                SaveBankSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),SaveBankSharedDataMessage.class);
                bankManagementService.updateBankdata(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateRoleSharedDataMessage")) {
                UpdateRoleSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),UpdateRoleSharedDataMessage.class);
                rolesService.updateRoles(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateMvnoSharedDataMessage")) {
                UpdateMvnoSharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),UpdateMvnoSharedDataMessage.class);
                mvnoService.updateMVNOEntity(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateTeamsSharedData")) {
                UpdateTeamsSharedData dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),UpdateTeamsSharedData.class);
                teamsService.updateTeams(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("UpdateMvnoData")) {
                UpdateMvnoData dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),UpdateMvnoData.class);
                mvnoService.updateMvnoIsp(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("CommonRoleMessage") && message.getEventType().equalsIgnoreCase("CREATE_DATA_ROLE")) {
                //CommonRoleMessage dataMessage=new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()),CommonRoleMessage.class);
                CommonRoleMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CommonRoleMessage.class);
                roleService.saveRole(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }

            else if(message.getDataType().equalsIgnoreCase("CommonRoleMessage") && message.getEventType().equalsIgnoreCase("DELETE_DATA_ROLE")) {
                //CommonRoleMessage dataMessage=new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()),CommonRoleMessage.class);
                CommonRoleMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CommonRoleMessage.class);
                roleService.deleteRole(dataMessage);
                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
            }*/
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
////                  key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
////                  handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Common-Micro-Service : " + e.getMessage());
//        }
//    }

@Async
public void handleKRAAddItemsResponse(KafkaMessageData message) {
    try {
        KRAGenericResponseDTOMessage dataMessage =
                objectMapper.convertValue(message.getData(), KRAGenericResponseDTOMessage.class);
        if (dataMessage != null && dataMessage.getResponseDTO() != null && !dataMessage.getResponseDTO().isEmpty()) {
            List<KRAGenericResponseDTO> responseDTOs = dataMessage.getResponseDTO();
            Map<Integer, String> chargeItemCodes = new HashMap<>();

            responseDTOs.stream()
                    .filter(dto -> dto.getData() instanceof Map)
                    .forEach(dto -> {
                        Map<?, ?> dataMap = (Map<?, ?>) dto.getData();
                        Object responseDataObj = dataMap.get("responseData");
                        if (responseDataObj instanceof List<?>) {
                            ((List<?>) responseDataObj).stream()
                                    .filter(item -> item instanceof Map)
                                    .map(item -> (Map<?, ?>) item)
                                    .forEach(itemMap -> {
                                        Object val = itemMap.get("itemCode");
                                        if (val != null) {
                                            String itemCode = String.valueOf(val);
                                            if (itemCode.startsWith("Charge_") || itemCode.startsWith("CHARGE_")) {
                                                try {
                                                    chargeItemCodes.put(Integer.parseInt(itemCode.replaceAll("[^0-9]", "")), itemCode);
                                                } catch (NumberFormatException e) {
                                                    log.warn("Invalid KRA charge itemCode format received: " + itemCode);
                                                }
                                            }
                                        }
                                    });
                        }
                    });

            if (!chargeItemCodes.isEmpty()) {
                List<Charge> chargeList = chargeRepository.findAllById(chargeItemCodes.keySet());
                chargeList.forEach(charge -> {
                    charge.setKraSyncId(chargeItemCodes.get(charge.getId()));
                    charge.setIsKraSynced(true);
                });
                chargeRepository.saveAll(chargeList);
            }
        }
        log.info("Handled KRAGenericResponseDTOMessage for Add Items: " + message);
    } catch (Exception e) {
        log.error("Error handling KRAGenericResponseDTOMessage for Add Items: " + e.getMessage(), e);
    }
}
@Async
public void handleKRASaleResponse(KafkaMessageData message) {
    try {
        KRAGenericResponseDTOMessage dataMessage =
                objectMapper.convertValue(message.getData(), KRAGenericResponseDTOMessage.class);
        if (dataMessage != null && dataMessage.getResponseDTO() != null && !dataMessage.getResponseDTO().isEmpty()) {
            List<KRAGenericResponseDTO> responseDTOs = dataMessage.getResponseDTO();

            Map<Integer, KRAGenericResponseDTO> dtoMap = responseDTOs.stream()
                    .filter(dto -> dto.getData() instanceof Map)
                    .filter(dto -> Boolean.TRUE.equals(((Map<?, ?>) dto.getData()).get("status")))
                    .filter(dto -> dto.getTraderInvoiceNo() != null)
                    .collect(Collectors.toMap(
                            dto -> Integer.valueOf(dto.getTraderInvoiceNo()),
                            dto -> dto
                    ));

            if (!dtoMap.isEmpty()) {
                List<Integer> debitDocumentNumbers = new ArrayList<>(dtoMap.keySet());
                List<DebitDocument> debitDocuments = debitDocRepository.findAllByIdIn(debitDocumentNumbers);

                for (DebitDocument invoice : debitDocuments) {
                    try {
                        KRAGenericResponseDTO dto = dtoMap.get(invoice.getId());
                        if (dto != null) {
                            invoice.setIsKraSynced(true);
                            invoice.setKraInvoiceId(dto.getKRAInvoiceId());
                            invoice.setQrCode(dto.getInvoiceQR());

                            // Extract additional fields from responseData
                            if (dto.getData() instanceof Map) {
                                Map<?, ?> dataMap = (Map<?, ?>) dto.getData();
                                Object responseDataObj = dataMap.get("responseData");
                                if (responseDataObj instanceof Map) {
                                    Map<?, ?> responseData = (Map<?, ?>) responseDataObj;
                                    log.info("handleKRASaleResponse :: DebitDoc ID=" + invoice.getId()
                                            + " :: responseData=" + new Gson().toJson(responseData));

                                    if (responseData.get("curRecptNo") != null) {
                                        invoice.setCurRecptNo(Long.valueOf(responseData.get("curRecptNo").toString()));
                                    }
                                    if (responseData.get("totRecptNo") != null) {
                                        invoice.setTotRecptNo(Long.valueOf(responseData.get("totRecptNo").toString()));
                                    }
                                    if (responseData.get("scuInternalData") != null) {
                                        invoice.setScuInternalData(responseData.get("scuInternalData").toString());
                                    }
                                    if (responseData.get("scuReceiptSignature") != null) {
                                        invoice.setScuReceiptSignature(responseData.get("scuReceiptSignature").toString());
                                    }
                                    if (responseData.get("sdcid") != null) {
                                        invoice.setSdcid(responseData.get("sdcid").toString());
                                    }
                                    if (responseData.get("sdcmrcNo") != null) {
                                        invoice.setSdcmrcNo(responseData.get("sdcmrcNo").toString());
                                    }
                                    if (responseData.get("sdcDateTime") != null) {
                                        String sdcDateTimeStr = responseData.get("sdcDateTime").toString();
                                        log.info("handleKRASaleResponse :: DebitDoc ID=" + invoice.getId()
                                                + " :: Parsing sdcDateTime=" + sdcDateTimeStr);
                                        java.time.format.DateTimeFormatter formatter =
                                                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                                        invoice.setSdcDateTime(LocalDateTime.parse(sdcDateTimeStr, formatter));
                                    }
                                    if (responseData.get("isStockIO") != null) {
                                        invoice.setIsStockIO(Boolean.valueOf(responseData.get("isStockIO").toString()));
                                    }

                                    log.info("handleKRASaleResponse :: DebitDoc ID=" + invoice.getId()
                                            + " :: Set curRecptNo=" + invoice.getCurRecptNo()
                                            + ", totRecptNo=" + invoice.getTotRecptNo()
                                            + ", scuInternalData=" + invoice.getScuInternalData()
                                            + ", scuReceiptSignature=" + invoice.getScuReceiptSignature()
                                            + ", sdcid=" + invoice.getSdcid()
                                            + ", sdcmrcNo=" + invoice.getSdcmrcNo()
                                            + ", sdcDateTime=" + invoice.getSdcDateTime()
                                            + ", isStockIO=" + invoice.getIsStockIO());
                                } else {
                                    log.warn("handleKRASaleResponse :: DebitDoc ID=" + invoice.getId()
                                            + " :: responseData is null or not a Map");
                                }
                            }
                            prepaidInvoiceService.setInvoiceXml(invoice);
                        }
                    } catch (Exception ex) {
                        log.error("handleKRASaleResponse :: Error processing DebitDoc ID=" + invoice.getId() + " :: " + ex.getMessage(), ex);
                    }
                }

                debitDocRepository.saveAll(debitDocuments);
            }
        }
        log.info("Handled KRAGenericResponseDTOMessage for Sale: " + message);
    } catch (Exception e) {
        log.error("Error handling KRAGenericResponseDTOMessage for Sale: " + e.getMessage(), e);
    }
}

@Async
public void handleKRACreditNoteResponse(KafkaMessageData message) {
    try {
        KRAGenericResponseDTOMessage dataMessage =
                objectMapper.convertValue(message.getData(), KRAGenericResponseDTOMessage.class);
        if (dataMessage != null && dataMessage.getResponseDTO() != null && !dataMessage.getResponseDTO().isEmpty()) {
            List<KRAGenericResponseDTO> responseDTOs = dataMessage.getResponseDTO();

            Map<Integer, KRAGenericResponseDTO> dtoMap = responseDTOs.stream()
                    .filter(dto -> dto.getData() instanceof Map)
                    .filter(dto -> Boolean.TRUE.equals(((Map<?, ?>) dto.getData()).get("status")))
                    .filter(dto -> dto.getTraderInvoiceNo() != null)
                    .collect(Collectors.toMap(
                            dto -> Integer.parseInt(dto.getTraderInvoiceNo()),
                            dto -> dto
                    ));

            if (!dtoMap.isEmpty()) {
                List<Integer> creditDocumentNumbers = new ArrayList<>(dtoMap.keySet());
                List<CreditDocument> creditDocuments = creditDocRepository.findAllByIdIn(creditDocumentNumbers);

                for (CreditDocument creditDoc : creditDocuments) {
                    KRAGenericResponseDTO dto = dtoMap.get(creditDoc.getId());
                    if (dto != null) {
                        creditDoc.setIsKraSynced(true);
                        creditDoc.setQrCode(dto.getInvoiceQR());
                    }
                }

                creditDocRepository.saveAll(creditDocuments);
            }
        }
        log.info("Handled KRAGenericResponseDTOMessage for Credit Note: " + message);
    } catch (Exception e) {
        log.error("Error handling KRAGenericResponseDTOMessage for Credit Note: " + e.getMessage(), e);
    }
}

    @Async
    public void handleMessageCreateCountry(KafkaMessageData message) {
        try {
            CountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CountrySharedDataMessage.class);
            countryService.saveCountry(dataMessage);
            log.info("Handled MessageCreateCountry successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateCountry: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateCountry(KafkaMessageData message) {
        try {
            CountrySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CountrySharedDataMessage.class);
            countryService.updateCountry(dataMessage);
            log.info("Handled MessageUpdateCountry successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateCountry: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCreateState(KafkaMessageData message) {
        try {
            StateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), StateSharedDataMessage.class);
            stateService.saveStateEntity(dataMessage);
            log.info("Handled MessageCreateState successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateState: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateState(KafkaMessageData message) {
        try {
            StateSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), StateSharedDataMessage.class);
            stateService.updateStateEntity(dataMessage);
            log.info("Handled MessageUpdateState successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateState: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleStaffUserRevenue(KafkaMessageData message) {
        try {
            SaveStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveStaffUserSharedDataMessage.class);
            staffUserService.saveStaffuser(dataMessage);
            log.info("Handled StaffUserRevenue successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling StaffUserRevenue: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateStaffUser(KafkaMessageData message) {
        try {
            UpdateStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateStaffUserSharedDataMessage.class);
            staffUserService.updateStaffUser(dataMessage);
            log.info("Handled MessageUpdateStaffUser successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateStaffUser: " + e.getMessage(), e);
        }
    }
    @Async
    public void handleMessageCreateCity(KafkaMessageData message) {
        try {
            CitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CitySharedDataMessage.class);
            cityService.saveCityEntity(dataMessage);
            log.info("Handled MessageCreateCity successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateCity: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateCity(KafkaMessageData message) {
        try {
            CitySharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CitySharedDataMessage.class);
            cityService.updateCityEntity(dataMessage);
            log.info("Handled MessageUpdateCity successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateCity: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCreatePincode(KafkaMessageData message) {
        try {
            SavePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePincodeSharedDataMessage.class);
            pincodeService.savePincode(dataMessage);
            log.info("Handled MessageCreatePincode successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreatePincode: " + e.getMessage(), e);
        }
    }
    @Async
    public void handleMessageUpdatePincode(KafkaMessageData message) {
        try {
            SavePincodeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePincodeSharedDataMessage.class);
            pincodeService.updatePincode(dataMessage);
            log.info("Handled MessageUpdatePincode successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdatePincode: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCreateArea(KafkaMessageData message) {
        try {
            SaveAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveAreaSharedDataMessage.class);
            areaService.saveAreaEntity(dataMessage);
            log.info("Handled MessageCreateArea successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateArea: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateArea(KafkaMessageData message) {
        try {
            SaveAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveAreaSharedDataMessage.class);
            areaService.updateAreaEntity(dataMessage);
            log.info("Handled MessageCreateArea successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateArea: " + e.getMessage(), e);
        }
    }
    @Async
    public void handleMessageCreateServiceArea(KafkaMessageData message) {
        try {
            SaveServiceAreaSharedDataMessge dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveServiceAreaSharedDataMessge.class);
            serviceAreaService.saveServiceArea(dataMessage);
            log.info("Handled MessageCreateServiceArea successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateServiceArea: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateServiceArea(KafkaMessageData message) {
        try {
            UpdateServiceAreaSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateServiceAreaSharedDataMessage.class);
            serviceAreaService.updateServiceArea(dataMessage);
            log.info("Handled MessageUpdateServiceArea successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateServiceArea: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCreateBusinessUnit(KafkaMessageData message) {
        try {
            SaveBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBusinessUnitSharedDataMessage.class);
            businessUnitService.saveBusineeUnit(dataMessage);
            log.info("Handled MessageCreateBusinessUnit successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateBusinessUnit: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateBusinessUnit(KafkaMessageData message) {
        try {
            SaveBusinessUnitSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBusinessUnitSharedDataMessage.class);
            businessUnitService.updateBusinessUnit(dataMessage);
            log.info("Handled MessageUpdateBusinessUnit successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateBusinessUnit: " + e.getMessage(), e);
        }
    }
    @Async
    public void handleMessageSaveBank(KafkaMessageData message) {
        try {
            SaveBankSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBankSharedDataMessage.class);
            bankManagementService.saveBankdata(dataMessage);
            log.info("Handled MessageSaveBank successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageSaveBank: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateBank(KafkaMessageData message) {
        try {
            SaveBankSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBankSharedDataMessage.class);
            bankManagementService.updateBankdata(dataMessage);
            log.info("Handled MessageUpdateBank successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateBank: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCreateBranch(KafkaMessageData message) {
        try {
            SaveBranchSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBranchSharedDataMessage.class);
            branchService.saveBranch(dataMessage);
            log.info("Handled MessageCreateBranch successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateBranch: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateBranch(KafkaMessageData message) {
        try {
            SaveBranchSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveBranchSharedDataMessage.class);
            branchService.updateBranch(dataMessage);
            log.info("Handled MessageUpdateBranch successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateBranch: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageRoleCreateFromCMS(KafkaMessageData message) {
        try {
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            rolesService.saveRole(dataMessage);
            log.info("Handled MessageRoleCreateFromCMS successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageRoleCreateFromCMS: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateRole(KafkaMessageData message) {
        try {
            UpdateRoleSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateRoleSharedDataMessage.class);
            rolesService.updateRoles(dataMessage);
            log.info("Handled MessageUpdateRole successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateRole: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageRoleDeleteFromCMS(KafkaMessageData message) {
        try {
            CommonRoleMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CommonRoleMessage.class);
            rolesService.deleteRole(dataMessage);
            log.info("Handled MessageRoleDeleteFromCMS successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageRoleDeleteFromCMS: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageForMvnoCreate(KafkaMessageData message) {
        try {
            SaveMvnoSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveMvnoSharedDataMessage.class);
            mvnoService.saveMVNOEntity(dataMessage);
            log.info("Handled MessageForMvnoCreate successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageForMvnoCreate: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageForMvnoUpdate(KafkaMessageData message) {
        try {
            UpdateMvnoSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateMvnoSharedDataMessage.class);
            mvnoService.updateMVNOEntity(dataMessage);
            log.info("Handled MessageForMvnoUpdate successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageForMvnoUpdate: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCreateClientService(KafkaMessageData message) {
        try {
            SaveClientServMessge dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveClientServMessge.class);
            clientServiceSrv.saveSharedClientService(dataMessage);
            log.info("Handled MessageCreateClientService successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateClientService: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateClientService(KafkaMessageData message) {
        try {
            UpdateClientServMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateClientServMessage.class);
            clientServiceSrv.updateSharedClientService(dataMessage);
            log.info("Handled MessageUpdateClientService successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateClientService: " + e.getMessage(), e);
        }
    }
    @Async
    public void handleMessageCreateTeam(KafkaMessageData message) {
        try {
            SaveTeamsSharedSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveTeamsSharedSharedData.class);
            teamsService.saveTeams(dataMessage);
            log.info("Handled MessageCreateTeam successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateTeam: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateTeam(KafkaMessageData message) {
        try {
            UpdateTeamsSharedData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateTeamsSharedData.class);
            teamsService.updateTeams(dataMessage);
            log.info("Handled MessageUpdateTeam successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateTeam: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCreateMvnoISP(KafkaMessageData message) {
        try {
            UpdateMvnoData dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateMvnoData.class);
            mvnoService.updateMvnoIsp(dataMessage);
            log.info("Handled MessageCreateMvnoISP successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateMvnoISP: " + e.getMessage(), e);
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_CMS_TOPIC}, groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCustomerMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
////                  key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
////                  handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : " + e.getMessage());
//        }
//    }

    @Async
    public void handlesavePlanService(KafkaMessageData message) {
        try {
            SaveServicesSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveServicesSharedDataMessage.class);
            servicesService.saveService(dataMessage);
            log.info("Handled savePlanService successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling savePlanService: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleupdatePlanService(KafkaMessageData message) {
        try {
            UpdateServicesSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateServicesSharedDataMessage.class);
            servicesService.UpdateService(dataMessage);
            log.info("Handled updatePlanService successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling updatePlanService: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlesaveTaxmessage(KafkaMessageData message) {
        try {
            SaveTaxSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveTaxSharedDataMessage.class);
            taxService.saveTaxData(dataMessage);
            log.info("Handled saveTaxmessag successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling saveTaxmessag: " + e.getMessage(), e);
        }
    }
    @Async
    public void handleupdateTaxmessage(KafkaMessageData message) {
        try {
            UpdateTaxSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateTaxSharedDataMessage.class);
            taxService.updateTaxData(dataMessage);
            log.info("Handled updateTaxmessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling updateTaxmessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlesaveChargemessage(KafkaMessageData message) {
        try {
            SaveChargeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveChargeSharedDataMessage.class);
            chargeService.saveChargeData(dataMessage);
            log.info("Handled saveChargemessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling saveChargemessage: " + e.getMessage(), e);
        }
    }
    @Async
    public void handleupdateChargemessage(KafkaMessageData message) {
        try {
            UpdateChargeSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateChargeSharedDataMessage.class);
            chargeService.updateChargeData(dataMessage);
            log.info("Handled updateChargemessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling updateChargemessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlesavePostPaidPlanmessage(KafkaMessageData message) {
        try {
            SavePlanSharedDataMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SavePlanSharedDataMessage.class);
            postPaidPlanService.savePostPaidPlanData(dataMessage);
            log.info("Handled savePostPaidPlanmessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling savePostPaidPlanmessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleupdatePostPaidPlanmessage(KafkaMessageData message) {
        try {
            UpdatePlanSharedDataMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), UpdatePlanSharedDataMessage.class);
            postPaidPlanService.updatePostPaidPlanData(dataMessage);
            log.info("Handled updatePostPaidPlanmessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling updatePostPaidPlanmessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlesaveDiscountmessage(KafkaMessageData message) {
        try {
            SaveDiscountSharedMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveDiscountSharedMessage.class);
            discountService.saveDiscountData(dataMessage);
            log.info("Handled saveDiscountmessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling saveDiscountmessage: " + e.getMessage(), e);
        }
    }
    @Async
    public void handleupdateDiscountmessage(KafkaMessageData message) {
        try {
            UpdateDiscountSharedMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateDiscountSharedMessage.class);
            discountService.updateDiscountData(dataMessage);
            log.info("Handled updateDiscountmessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling updateDiscountmessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlesavePlanGroupmessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonData = objectMapper.writeValueAsString(message.getData());

            SavePlanGroupSharedDataMessage dataMessage = objectMapper.readValue(jsonData, SavePlanGroupSharedDataMessage.class);
            planGroupService.savePlanGroupData(dataMessage);
            log.info("Handled savePlanGroupmessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling savePlanGroupmessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleupdatePlanGroupmessage(KafkaMessageData message) {
        try {


            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String jsonData = objectMapper.writeValueAsString(message.getData());

            UpdatePlanGroupSharedDataMessage dataMessage = objectMapper.readValue(jsonData, UpdatePlanGroupSharedDataMessage.class);
            planGroupService.updatePlanGroupData(dataMessage);
            log.info("Handled updatePlanGroupmessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling updatePlanGroupmessage: " + e.getMessage(), e);
        }
    }

    @Async
    @Transactional
    public void handlesaveCustomersmessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SaveCustomerDataShareMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SaveCustomerDataShareMessage.class);
            prepaidInvoiceService.saveCustomerFromAPI(dataMessage);
            log.info("Handled saveCustomersmessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling saveCustomersmessage: " + e.getMessage(), e);
        }
    }
    @Async
    public void handleMessageUpdateCustomer(KafkaMessageData message) {
        try {
            UpdateCustomerShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustomerShareDataMessage.class);
            subscriberService.updateCustomersData(dataMessage);
            log.info("Handled MessageUpdateCustomer successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageUpdateCustomer: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageChangePlanRevenue(KafkaMessageData message) {
        try {
            ChangePlanMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ChangePlanMessage.class);
            prepaidInvoiceService.processChangePLan(dataMessage);
            log.info("Handled ChangePlanRevenue successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling ChangePlanRevenue: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCustDirectChargeRevenue(KafkaMessageData message) {
        try {
            ChangePlanMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ChangePlanMessage.class);
            prepaidInvoiceService.saveCustDirectCharge(dataMessage);
            log.info("Handled receiveMessageCustDirectChargeRevenue successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessageCustDirectChargeRevenue: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCafToCustomere(KafkaMessageData message) {
        try {
            CaftoCustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CaftoCustomerMessage.class);
            prepaidInvoiceService.cafToCustomer(dataMessage);
            log.info("Handled receiveMessageCafToCustomere successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessageCafToCustomere: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageApproveOrgInvoice(KafkaMessageData message) {
        try {
            AppproveOrgInvoiceMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), AppproveOrgInvoiceMessage.class);
            if (dataMessage.getIsApproveRequest() != null)
                prepaidInvoiceService.billToOrg(dataMessage);
            DebitDocument debitDocument = debitDocRepository.findById(dataMessage.getDebitdocId()).orElse(null);
            if (debitDocument != null) {
                if (dataMessage.getIsApproveRequest() != null && dataMessage.getIsApproveRequest()) {
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                    debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
                }
                if (dataMessage.getIsApproveRequest() != null && !dataMessage.getIsApproveRequest()) {
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                    debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
                }
                debitDocRepository.save(debitDocument);
            }
            log.info("Handled receiveMessageApproveOrgInvoicee successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessageApproveOrgInvoice: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessagePricebook(KafkaMessageData message) {
        try {
            SavePricebookSharedMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePricebookSharedMessage.class);
            pricebookService.save(dataMessage);
            log.info("Handled receiveMessagePricebook successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessagePricebook: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageUpdateCPr(KafkaMessageData message) {
        try {
            UpdateCustomerCprDateAndStatus dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustomerCprDateAndStatus.class);
            custPlanMappingService.updateCprDateAndStatus(dataMessage);
            log.info("Handled receiveMessageUpdateCPr successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessageUpdateCPr: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessagePriceUpdatebook(KafkaMessageData message) {
        try {
            UpdatePricebookSharedMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePricebookSharedMessage.class);
            pricebookService.update(dataMessage);
            log.info("Handled receiveMessagePriceUpdatebook successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessagePriceUpdatebook: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleCustomerChangeStaus(KafkaMessageData message) {
        try {
            CustomerTerminationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerTerminationMessage.class);
            subscriberService.terminate(dataMessage);
            log.info("Handled receiveCustomerChangeStaus successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveCustomerChangeStaus: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleShiftLocationParters(KafkaMessageData message) {
        try {
            ShiftlocationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ShiftlocationMessage.class);
            Customers customers = customersRepository.findById(dataMessage.getCustId()).orElse(null);
            if (customers != null) {
                if (dataMessage.getTranferConnission() != null && dataMessage.getTranferConnission() > 0.0) {
                    partnerCommissionService.transferCommissionFromOnePartnerToAnotherPartner(dataMessage.getOldpartnerId(), dataMessage.getNewPartnerId(), dataMessage.getTranferConnission(), customers);
                }
                if (dataMessage.getTransferBalance() != null && dataMessage.getTransferBalance() > 0.0) {
                    partnerCommissionService.transferBalanceFromOnePartnerToAnotherPartner(dataMessage.getOldpartnerId(), dataMessage.getNewPartnerId(), dataMessage.getTransferBalance(), customers);
                }
            }
            ServiceArea serviceArea = serviceAreaRepository.findById(dataMessage.getServiceAreaId()).orElse(null);
            dbrService.updateServiceAreaIdForCustomer(dataMessage.getCustId(), serviceArea, LocalDate.now());


            log.info("Handled receiveCustomerChangeStaus successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveCustomerChangeStaus: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlePaymentMessage(KafkaMessageData message) {
        try {
            SavePartnerPaymentMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePartnerPaymentMessage.class);
            partnerService.approvebalance(dataMessage.getPartnerPaymentDTO(), dataMessage.getPartnerPayment());
            log.info("Handled receivePaymentMessag successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receivePaymentMessag: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageServiceTermination(KafkaMessageData message) {
        try {
            ServiceTerminationMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ServiceTerminationMessage.class);
            custPlanMappingService.changeStatusOfCustServices(dataMessage.getCustomerServiceId(), dataMessage.getCustomerStatus(), dataMessage.getRemarks(), Boolean.FALSE, dataMessage.getGeneratecn());
            log.info("Handled receiveMessageServiceTermination successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessageServiceTermination: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageForOnlinePayementAdjustment(KafkaMessageData message) {
        try {
            SendOnlinePaymentRevenueMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SendOnlinePaymentRevenueMessage.class);
            prepaidInvoiceService.adjustAllPaymentAgainstInvoice(dataMessage);
            log.info("Handled receiveMessageForOnlinePayementAdjustment successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessageForOnlinePayementAdjustment: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageForInventoryCreditNote(KafkaMessageData message) {
        try {
            DbrHoldResumeMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), DbrHoldResumeMessage.class);
            if (dataMessage != null && dataMessage.getCprIds() != null && !dataMessage.getCprIds().isEmpty()) {
                if (dataMessage.getIsServiceHold())
                    dbrService.dbrHoldOnServicePause(dataMessage.getCprIds());
                else
                    dbrService.dbrResumeOnServiceResume(dataMessage.getCprIds());
            }
            log.info("Handled receiveMessageForOnlinePayementAdjustment successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveMessageForOnlinePayementAdjustment: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleupdateCustomerDiscountmessage(KafkaMessageData message) {
        try {
            CustomerDiscountPojo dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerDiscountPojo.class);
            subscriberService.updateCustomerDiscount(dataMessage);
            log.info("Handled updateCustomerDiscountmessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling updateCustomerDiscountmessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlesaveVoucherBatchMessage(KafkaMessageData message) {
        try {
            SaveVoucherBatchSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveVoucherBatchSharedDataMessage.class);
            partnerService.updatePartnerBalanceForVoucherBatch(dataMessage);
            log.info("Handled saveVoucherBatchMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling saveVoucherBatchMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleBudPayPaymentMessage(KafkaMessageData message) {
        try {
            BudPayPaymentMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), BudPayPaymentMessage.class);
            prepaidInvoiceService.updateProvisionalPortalCustomer(dataMessage);
            log.info("Handled receiveBudPayPaymentMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling receiveBudPayPaymentMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMvnoDiscountMessage(KafkaMessageData message) {
        try {
            MvnoDiscountMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), MvnoDiscountMessage.class);
            mvnoDiscountService.saveMvnoDiscountFromMessageReceiver(dataMessage);
            log.info("Handled MvnoDiscountMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MvnoDiscountMessagee: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleBudpayChangePlanMessage(KafkaMessageData message) {
        try {
            BudpayChangePlanMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), BudpayChangePlanMessage.class);
            creditDocService.processBudPaychangePlanMessage(dataMessage);
            log.info("Handled BudpayChangePlanMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling BudpayChangePlanMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageCreditDocFromAPIGW(KafkaMessageData message) {
        try {
            logger.info("===========================+++++++++Inside kafka  call: updating handleMessageCreditDocFromAPIGW++++++++++++==============================");
            logger.info("===========================+++++++++Received Message creditDocMessage "+message.toString()+"++++++++++++==============================");
            CreditDocMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CreditDocMessage.class);
            creditDocService.save(dataMessage);
//            Customers customers = customersRepository.findById(dataMessage.getCustomer()).orElse(null);
//            logger.info(">>> CUSTOMERS FETCHED: "+customers);
//            customers.setWalletbalance(dataMessage.getWalletBalance());
//            customersRepository.save(customers);
            log.info("Handled MessageCreditDocFromAPIGW successfully: " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error handling MessageCreditDocFromAPIGW: " + e.getMessage(), e);
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_PMS_TOPIC}, groupId = KafkaConstant.KAFKA_PMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFromPartnerMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Customer-Micro-Service: " + e.getMessage(), e);
//        }
//    }

    @Async
    private void handleSavePartnerData(KafkaMessageData message) {
        try {
            SavePartnerSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SavePartnerSharedDataMessage.class);
            partnerService.savePartnerEntiry(dataMessage);
            Partner partner = partnerRepository.findById(dataMessage.getId()).orElse(null);
            if (partner != null && partner.getBalance() != null && partner.getBalance() > 0)
                partnerLedgerDetailsService.reverseBalance(null, 0.0, dataMessage.getBalance(), dataMessage.getId(), CommonConstants.TRANS_CATEGORY_ADD_BALANCE, "Add Balance in Partner wallet");
            log.info("CMS Service Receive Kafka Message From Integration-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleitemHistoryFromRms" + e.getMessage());
        }
    }

    @Async
    private void handleUpdatePartnerData(KafkaMessageData message) {
        try {
            UpdatePartnerSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdatePartnerSharedDataMessage.class);
            partnerService.updatePartnerData(dataMessage);
            log.info("CMS Service Receive Kafka Message From Integration-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling handleitemHistoryFromRms" + e.getMessage());
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_INVENTORY_TOPIC}, groupId = KafkaConstant.KAFKA_INVENTORY_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormInventoryMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
////                  key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
////                  handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Inventory-Micro-Service : " + e.getMessage());
//        }
//    }

    @Async
    public void handleMessageInventoryProductRevenue(KafkaMessageData message) {
        try {
            ProductMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ProductMessage.class);
            productService.configureProductReceiveMessage(dataMessage);
            log.info("Handled MessageCreditDocFromAPIGW successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreditDocFromAPIGW: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageInventoryCustomerRevenue(KafkaMessageData message) {
        try {
//            CustomerInventoryRevenueMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()),CustomerInventoryRevenueMessage.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustomerInventoryRevenueMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustomerInventoryRevenueMessage.class);

            productService.configureCustomerInventoryReceiveMessage(dataMessage);
            log.info("Handled MessageInventoryCustomerRevenue successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageInventoryCustomerRevenue: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleMessageForInventoryCreditNoteFromInventory(KafkaMessageData message) {
        try {
            RecordPaymentMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), RecordPaymentMessage.class);
            creditDocService.adjustCreditNoteForInventory(dataMessage);
            log.info("Handled MessageForInventoryCreditNoteFromInventory successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageForInventoryCreditNoteFromInventory: " + e.getMessage(), e);
        }
    }

//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_NOTIFICATION_TOPIC},groupId = KafkaConstant.KAFKA_NOTIFICATION_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormNotificationMicroService(KafkaMessageData message) {
//        try {
//            if(message.getDataType().contains("CLASS-NAME")) {
//                //CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
//                //countryService.saveCountry(dataMessage);
//                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Notification-Micro-Service : "+ e.getMessage());
//        }
//    }


//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_RADIUS_TOPIC},groupId = KafkaConstant.KAFKA_RADIUS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormRadiusMicroService(KafkaMessageData message) {
//        try {
//            if(message.getDataType().contains("CLASS-NAME")) {
//                //CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
//                //countryService.saveCountry(dataMessage);
//                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Radius-Micro-Service : "+ e.getMessage());
//        }
//    }
//
//
//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_TICKET_TOPIC},groupId = KafkaConstant.KAFKA_TICKET_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormTicketMicroService(KafkaMessageData message) {
//        try {
//            if(message.getDataType().contains("CLASS-NAME")) {
//                //CountrySharedDataMessage dataMessage=new Gson().fromJson(new Gson().toJson(message.getData()),CountrySharedDataMessage.class);
//                //countryService.saveCountry(dataMessage);
//                System.out.println("Revenue Service Receive Kafka Message From Common-Micro-Service  : " + message);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Ticket-Micro-Service : "+ e.getMessage());
//        }
//    }

//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA}, groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageForCustomerData(KafkaMessageData message) {
//        try {
//            handleCustomerDataTransactionally(message);
//        } catch (UnexpectedRollbackException e) {
//            log.error("Transaction was rolled back: " + e.getMessage(), e);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Error processing Kafka message: " + e.getMessage());
//        }
//    }

    @Async
    @Transactional
    public void handleCustomerDataTransactionally(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);

//                  key with both dataType and eventType
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;

//                  handler with both dataType and eventType
            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);

            if (handler == null) {
//                If not found, try to find the handler with only dataType
                handler = messageHandlers.get(keyWithoutEventType);
            }

            if (handler != null) {
                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : " + e.getMessage());
            throw e;
        }
    }


    @Async
    public void handleCustomerStatusChangeMessage(KafkaMessageData message) {
        log.info("Received Message From Kafka receiverMessage : <" + message + ">");
        try {
            CustomerUpdateMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CustomerUpdateMessage.class);
            Map<String, Object> customerData = dataMessage.getCustomerData();
            String idString = customerData.get("id").toString();
            int id = (int) Double.parseDouble(idString); // Parse the id safely
            Customers customers = customersRepository.findById(id).orElse(null);
            if (customers != null) {
                customers.setStatus(customerData.get("status").toString());
                customersRepository.save(customers);
            }
        } catch (Exception e) {
            log.error("receiveMessageUpdateCustomerStatus Failed :" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Async
    public void handleCustDirectChargeListRevenue(KafkaMessageData message) {
        try {
            ChangePlanMessageList dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), ChangePlanMessageList.class);
            prepaidInvoiceService.saveCustDirectChargeListRevenue(dataMessage);
            log.info("Handled MessageCreateMvnoISP successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageCreateMvnoISP: " + e.getMessage(), e);
        }
    }


//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_CMS_CHANGE_PLAN_TOPIC}, groupId = KafkaConstant.KAFKA_CMS_CHANGE_PLAN_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFormCustomerChangePlanMicroService(KafkaMessageData message) {
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
////                  key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
////                  handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : " + e.getMessage());
//        }
//    }

//    @javax.transaction.Transactional
//    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.KAFKA_INTEGRATION_TOPIC}, groupId = KafkaConstant.KAFKA_INTEGRATION_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageReceiveFromIntegrationMicroService(KafkaMessageData message) {
//
//        try {
//            String dataType = message.getDataType();
//            String eventType = message.getEventType();
//            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
//
//            //            key with both dataType and eventType
//            String keyWithEventType = dataType + ":" + eventType;
//            String keyWithoutEventType = dataType;
//
//            //             handler with both dataType and eventType
//            Consumer<KafkaMessageData> handler = messageHandlers.get(keyWithEventType);
//
//            if (handler == null) {
////                If not found, try to find the handler with only dataType
//                handler = messageHandlers.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandlers.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//
//        } catch (Exception e) {
//            log.error("Kafka Error Message Receive From Integration Microservice: " + e.getMessage(), e);
//        }
//    }

    @Async
    public void handleOnlineInvoicePayment(KafkaMessageData message) {
        try {
//            CustomerInventoryRevenueMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()),CustomerInventoryRevenueMessage.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            OnlineInvoicePaymentDTO dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), OnlineInvoicePaymentDTO.class);

            invoiceUtil.adjustInvoicePaymentFromOnlinePayment(dataMessage);
            log.info("Handled OnlineInvoicePaymentDTO successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling OnlineInvoicePaymentDTO: " + e.getMessage(), e);
        }
    }


    @Async
    public void handleIspPayloadResponseFromIntegration(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            IspMainPayload dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), IspMainPayload.class);
            debitDocService.updateStatusCode(dataMessage);
            log.info("Handled handleIspPayloadResponseFromIntegration successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling handleIspPayloadResponseFromIntegration: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleInvoiceIntigrationMessage(KafkaMessageData message) {
        try {
            log.info("endinvoiceQRMessage enter in revenue : "+message);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SendinvoiceQRMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), SendinvoiceQRMessage.class);
            invoiceIntigrationService.saveInvoiceQr(dataMessage);
            log.info("Handled SendinvoiceQRMessage successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling SendinvoiceQRMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleSaveThirdPartyIntigrationMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ThirdPartyIntegrationMenuDto dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), ThirdPartyIntegrationMenuDto.class);
            thirdPartyIntegrationMenuService.save(dataMessage);
            log.info("Handled ThirdPartyIntegrationMenuDto successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling ThirdPartyIntegrationMenuDto: " + e.getMessage(), e);
        } catch (AlreadyExistException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void handleUpdateThirdPartyIntigrationMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ThirdPartyIntegrationMenuDto dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), ThirdPartyIntegrationMenuDto.class);
            thirdPartyIntegrationMenuService.update(dataMessage);
            log.info("Handled ThirdPartyIntegrationMenuDto successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling ThirdPartyIntegrationMenuDto: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleDeleteThirdPartyIntigrationMessage(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ThirdPartyIntegrationMenuDto dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), ThirdPartyIntegrationMenuDto.class);
            thirdPartyIntegrationMenuService.delete(dataMessage.getId());
            log.info("Handled ThirdPartyIntegrationMenuDto successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling ThirdPartyIntegrationMenuDto: " + e.getMessage(), e);
        }
    }

    public void handleAddWalletPayment(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustPayDTOMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message.getData()), CustPayDTOMessage.class);

            creditDocService.addWalletAmount(dataMessage);
            log.info("Handled CustPayDTOMessage for advance payment successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling CustPayDTOMessage for advance payment: " + e.getMessage(), e);
        }
    }

    @Async
    public void handlePlanServiceAreaMapping(KafkaMessageData message) {
        try {
            Gson gson = GsonConfig.buildGson();
            SavePlanAssignmentMessage dataMessage = gson.fromJson(gson.toJson(message.getData()),SavePlanAssignmentMessage.class);
            postPaidPlanService.assignPlanToServiceArea(dataMessage.getMappingList());
        } catch (Exception e) {
            log.error("Error parsing message", e);
        }
    }
    private void handleSaveChildCustomer(KafkaMessageData kafkaMessageData)  {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ChildCustomer childCustomer = mapper.registerModule(new JavaTimeModule()).readValue(new ObjectMapper().writeValueAsString(kafkaMessageData.getData()), ChildCustomer.class);
            if (Objects.nonNull(childCustomer)) {
                childCustomerService.save(childCustomer);
                log.info("Revenue Service Receive Kafka Message From CMS-Service  : " + childCustomer);
            }
        } catch (Exception e) {
            log.error("Error handling handleitemHistoryFromRms" + e.getMessage());
        }
    }
    private void handleUpdateChildCustomer(KafkaMessageData kafkaMessageData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            UpdateChildCustometMessesge data = mapper.registerModule(new JavaTimeModule()).readValue(new ObjectMapper().writeValueAsString(kafkaMessageData.getData()), UpdateChildCustometMessesge.class);
            if (Objects.nonNull(data)) {
                childCustomerService.update(data);
                log.info("Revenue Service Receive Kafka Message From CMS-Service  : " + data);
            }
        } catch (Exception e) {
            log.error("Error handling handle item History From Rms" + e.getMessage());
        }
    }

    private void handleParentChildMappingRel(KafkaMessageData data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ParentChildMappingRel parentChildMappingRel = objectMapper.registerModule(new JavaTimeModule()).readValue(new ObjectMapper().writeValueAsString(data.getData()), ParentChildMappingRel.class);
            if (Objects.nonNull(parentChildMappingRel)) {
                parentChildMappinService.saveParentChildMapping(parentChildMappingRel);
                log.info("Revenue Service Receive Kafka Message From CMS-Service  : " + data);
            }
        } catch (Exception e) {
            log.error("Error handling handle item History From CMS" + e.getMessage());
        }
    }
    private void handleServicePause(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            TypeReference<List<ServiceChnageStatus>> typeRef = new TypeReference<List<ServiceChnageStatus>>() {};
            List<ServiceChnageStatus> statuses = objectMapper.readValue(
                    objectMapper.writeValueAsString(message.getData()), typeRef
            );
            creditDocService.creatCreditNotAsPauseSevicePerService(statuses);
        } catch (Exception e) {
            log.error("Error parsing message", e);
        }
    }

    @Async
    private void handleUpdateCustChargeHistory(KafkaMessageData message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            UpdateChargeHistoryMessage updateChargeHistoryMessage = objectMapper.registerModule(new JavaTimeModule()).readValue(new ObjectMapper().writeValueAsString(message.getData()), UpdateChargeHistoryMessage.class);
            postpaidInvoiceService.updateChargeHistory(updateChargeHistoryMessage);
        } catch (Exception e) {
            log.error("Error parsing message for ", e);
        }
    }
    private void handleOnlinePayment(KafkaMessageData kafkaMessageData) {
        try {
            log.info("Received CustPayDTOMessage for Online Payment: " + kafkaMessageData);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CustPayDTOMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(kafkaMessageData.getData()), CustPayDTOMessage.class);
            log.info("Parsed CustPayDTOMessage for orderId: " + dataMessage.getOrderId());
            customerOnlinePaymentAuditService.saveOrUpdateOnlinePayment(dataMessage);
            log.info("Successfully processed Online Payment message for orderId: " + dataMessage.getOrderId());
        } catch (Exception e) {
            log.error("error handling online payment", e);
        }
    }
    private void handleCafChildCustomerApproveMessege(KafkaMessageData kafkaMessageData) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CafChildCustomerApproveMessege approveMessege = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(kafkaMessageData.getData()), CafChildCustomerApproveMessege.class);
            childCustomerService.cafChildCustomerApprove(approveMessege);

        }catch (Exception e ){
            log.error("error handling online payment", e);
        }
    }

    @Async
    public void handlePlanUpdateMessage(KafkaMessageData message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            PlanUpdateMessage planUpdateMessage = mapper.convertValue(message.getData(), PlanUpdateMessage.class);
            custPlanMappingService.updatePlanWhileCafApproval(planUpdateMessage.getPlanUpdateCafApprovalMessages());
            log.info("Successfully handlePlanUpdateMessage: {}");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error handling handlePlanUpdateMessage: {}"+ message, e);
        }
    }
    @Async
    public void handleAutoRenewalBoosterPlanMessage(KafkaMessageData message) {
        try {
            AutoRenewalBoosterPlanMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), AutoRenewalBoosterPlanMessage.class);
            custPlanMappingService.updateAutoRenewalForBooster(dataMessage);
            log.info("Successfully handleAutoRenewalBoosterPlanMessage: {}");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error handling handleAutoRenewalBoosterPlanMessage: {}"+ message, e);
        }
    }
    @Async
    public void handleDepartment(KafkaMessageData message) {
        try {
            SaveDepartmentSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveDepartmentSharedDataMessage.class);
            departmentService.saveDepartment(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling SaveDepartmentSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateDepartment(KafkaMessageData message) {
        try {
            UpdateDepartmentSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateDepartmentSharedDataMessage.class);
            departmentService.updateDepartment(dataMessage);
            log.info("CMS Service Receive Kafka Message From Common-Micro-Service  : " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateDepartmentSharedDataMessage: " + e.getMessage(), e);
        }
    }

    @Async
    public void handleUpdateCustPlanMapping(KafkaMessageData message) {
        try {
            UpdateCustplanMappingMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustplanMappingMessage.class);
            custPlanMappingService.updateCustPlanMapping(dataMessage);
            log.info("Handled UpdateCustPlanMappingForP2Pmessage: " + message);
        } catch (Exception e) {
            log.error("Error handling UpdateCustPlanMappingForP2Pmessage: " + e.getMessage(), e);
        }
    }
}
