package com.savbill.integrationsystem.kafka;

import com.savbill.integrationsystem.Case.CaseRepo;
import com.savbill.integrationsystem.Case.CaseService;
import com.savbill.integrationsystem.Customer.CAFCustomerStatusMessage;
import com.savbill.integrationsystem.Customer.CustomersCreateService;
import com.savbill.integrationsystem.Customer.SaveCustomerDataShareMessage;
import com.savbill.integrationsystem.Customer.UpdateCustomerShareDataMessage;
import com.savbill.integrationsystem.CustomerInventoryMapping.CustomerInventoryService;
import com.savbill.integrationsystem.CustomerPackage.entity.CustomerPackageService;
import com.savbill.integrationsystem.CustomerServiceMapping.CustomerServiceMappingService;
import com.savbill.integrationsystem.InventoryItem.ApproveInventoryItemService;
import com.savbill.integrationsystem.InventoryItem.ApproveRemoveInventoryItemService;
import com.savbill.integrationsystem.InventoryItem.IntentoryItemRepo;
import com.savbill.integrationsystem.InventoryItem.InventoryItemService;
import com.savbill.integrationsystem.IspPayloadService.IspInvoicePayloadService;
import com.savbill.integrationsystem.NewNMSIntegration.message.NMSIntegrationMessage;
import com.savbill.integrationsystem.NewNMSIntegration.service.APIIntegrationService;
import com.savbill.integrationsystem.PaymentConfig.service.PaymentConfigService;
import com.savbill.integrationsystem.PaymentIntegration.Repository.CustomerPaymentRepository;
import com.savbill.integrationsystem.PaymentIntegration.Service.CustomerPaymentService;
import com.savbill.integrationsystem.PostpaidPlan.PostpaidPlanServiceImpl;
import com.savbill.integrationsystem.Services.ServicesService;
import com.savbill.integrationsystem.billgen.entity.SaveBranchSharedDataMessage;
import com.savbill.integrationsystem.billgen.service.*;
import com.savbill.integrationsystem.billgen.service.*;
import com.savbill.integrationsystem.config.SpringContext;
import com.savbill.integrationsystem.core.dto.KRAGenericResponseDTO;
import com.savbill.integrationsystem.deviceveri.domain.CustomersData;
import com.savbill.integrationsystem.etims.DTO.*;
import com.savbill.integrationsystem.etims.KRAConstant;
import com.savbill.integrationsystem.etims.service.KRAETimsService;
import com.savbill.integrationsystem.isp.IspMainPayload;
import com.savbill.integrationsystem.middleware.Invoice.dto.customerdetail.SendInvoiceMessage;
import com.savbill.integrationsystem.middleware.Invoice.service.TraInvoiceService;
import com.savbill.integrationsystem.mvno.MvnoService;
import com.savbill.integrationsystem.mvno.SaveMvnoSharedDataMessage;
import com.savbill.integrationsystem.mvno.UpdateMvnoSharedDataMessage;
import com.savbill.integrationsystem.nms.NmsService;

import com.savbill.integrationsystem.nms.entity.UuidDataDTO;

import com.savbill.integrationsystem.pojo.NMSServiceActivationDTO;
import com.savbill.integrationsystem.rabbitmq.*;
import com.savbill.integrationsystem.rabbitmq.*;
import com.savbill.integrationsystem.rms.model.InwardDto;
import com.savbill.integrationsystem.rms.model.ProductCategoryDto;
import com.savbill.integrationsystem.rms.model.WareHouseDto;
import com.savbill.integrationsystem.rms.service.InwardService;
import com.savbill.integrationsystem.rms.service.ProductCategoryService;
import com.savbill.integrationsystem.rms.service.WareHouseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Consumer;


@Component
public class KafkaMessageReceiver {

    private static Log log = LogFactory.getLog(KafkaMessageReceiver.class);
    Map<String, Consumer<KafkaMessageData>> messageHandler = new HashMap<>();
    @Autowired
    BillGenService billGenService;
    @Autowired
    ChargeServices chargeServices;
    @Autowired
    PlanGroupService planGroupService;
    @Autowired
    CustomerService customerService;
    @Autowired
    CreditdocService creditdocService;
    @Autowired
    TaxService taxService;

    @Autowired
    ServiceAreaInService serviceAreaInService;
    @Autowired
    BusinessUnitService businessUnitService;
    @Autowired
    DebitDocumentService debitDocumentService;
    @Autowired
    StaffUserService staffUserService;
    @Autowired
    BranchService branchService;

    @Autowired
    WareHouseService wareHouseService;

    @Autowired
    ApproveInventoryItemService approveInventoryItemService;

    @Autowired
    ApproveRemoveInventoryItemService inventoryRemoveItemService;
    @Autowired
    InventoryItemService inventoryItemService;

    @Autowired
    private CustomerPackageService customerPackageService;

    @Autowired
    private CustomerInventoryService customerInventoryService;

    @Autowired
    private CustomerServiceMappingService customerServiceMappingService;

    @Autowired
    private ServicesService servicesService;

    @Autowired
    private PostpaidPlanServiceImpl postpaidPlanService;
    @Autowired
    private IntentoryItemRepo intentoryItemRepo;
    @Autowired
    private CaseService caseService;

    @Autowired
    private CaseRepo caseRepo;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private InwardService inwardService;

    @Autowired
    private NmsService nmsService;

    @Autowired
    private APIIntegrationService apiIntegrationService;

    @Autowired
    private PaymentConfigService paymentConfigService;


    @Autowired
    CustomerPaymentService customerPaymentService;

    @Autowired
    CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private CustomersCreateService customersCreateService;

    @Autowired
    private TraInvoiceService traInvoiceService;
    @Autowired
    private KRAETimsService kraeTimsService;


    public KafkaMessageReceiver(){
        messageHandler.put("CreditNoteMessageIntegrationSystem", this::saveCreditNoteGendataHandler);
        messageHandler.put("SaveChargeSharedDataMessage", this::saveChargeHandler);
        messageHandler.put("SaveServicesSharedDataMessage", this::savePlanServiceHandler);
        messageHandler.put("SaveTaxSharedDataMessage", this::saveTaxHandler);
        messageHandler.put("SaveServiceAreaSharedDataMessge", this::saveServiceAreaHandler);
        messageHandler.put("BusinessUnitMessage", this::saveBusinessUnitHandler);
        messageHandler.put("DebitDocumentMessage", this::saveDebitDocumentHandler);
        messageHandler.put("CreditDocMessage", this::saveCreditDocumentHandler);
        messageHandler.put("SaveStaffUserSharedDataMessage", this::saveStaffUserHandle);
        messageHandler.put("SaveBranchSharedDataMessage", this::saveBranchHandler);
        messageHandler.put("SaveCustomerDataShareMessage", this::saveCustomerHandler);
        messageHandler.put("CancelRegenerateInvoice", this::cancelRegenerateInvoiceHandler);
        messageHandler.put("CustomerPackageRelMessage", this::saveCustPackageRelHandler);
        messageHandler.put("CustomerInventoryMappingMessage", this::saveCustomerInventoryMappingHandler);
        messageHandler.put("CustomerServiceMappingMessage", this::saveCustServiceMappingHandle);
        messageHandler.put("PlanServiceForIntegrationMessage", this::saveServiceHandler);
        messageHandler.put("PostpaidPlanMessage", this::savePostpaidPlanHandler);
        messageHandler.put("ItemMessage", this::saveItemHandler);
        messageHandler.put("CustPlanMappingUpdateMessage", this::updateCustPlanMappingHandler);
        messageHandler.put("ApproveInventoryItemMessage", this::approveInventoryItemHandler);
        messageHandler.put("ApproveRemoveInventoryItemRequestMessage", this::approveRemoveItemRequestHandler);
        messageHandler.put("ProductCategoryDto", this::saveProductCategoryHandler);
        messageHandler.put("WareHouseDto", this::saveWareHouseHandler);
        messageHandler.put("InwardDto", this::saveInwardFromInventoryHandler);
        messageHandler.put("TicketMessageIntegration", this::ticketIntegrationHandler);
        messageHandler.put("NMSServiceActivationDTO", this::activateNMSServiceHandler);
        messageHandler.put("NMSIntegrationMessage", this::nmsIntegrationHandler);
        messageHandler.put("UuidDataDTO", this::deleteNmsServiceHandler);
        messageHandler.put("PaymentConfigMessage", this::savePaymentConfigHandler);
        messageHandler.put("CustPayDTOMessage", this::saveCustomerPaymentHandler);
        messageHandler.put("IspMainPayload:SEND", this::sendIspInvoicePayload);
//        messageHandler.put("SaveCustomerDataShareMessage",this::saveCustomerData); comment because no need to use but only to uncomment if you want to use
//        messageHandler.put("UpdateCustomerShareDataMessage",this::updateCustomerData);
        messageHandler.put("CAFCustomerStatusMessage", this::CAFCustomerStatus);
        messageHandler.put("SaveMvnoSharedDataMessage", this::handleMessageForMvnoCreate);
        messageHandler.put("UpdateMvnoSharedDataMessage", this::handleMessageForMvnoUpdate);
        messageHandler.put("SendInvoiceMessage:" + KafkaConstant.TRA_INTEGRATION, this::handleSendInvoiceMessage);
        messageHandler.put("ETimsCustomerListDTO:"+KRAConstant.ADD_CUSTOMER, this::handleETimsCustomer);
        messageHandler.put("ETimsItemListDTO:"+ KRAConstant.ADD_ITEMS,this::handleETimsAddItem);
        messageHandler.put("ETimsItemDTO:"+ KRAConstant.UPDATE_ITEMS,this::handleETimsUpdateItem);
        messageHandler.put("ETimsInvoiceListDTO:"+KRAConstant.ADD_INVOICE,this::handleAddInvoice);
        messageHandler.put("ETimsCreditNoteListDTO:"+KRAConstant.ADD_CREDIT,this::handleAddCredit);
        messageHandler.put("ETimsItemListDTO:"+ KRAConstant.ADD_CHARGE,this::handleETimsAddItem);




    }
    @Async
    private void handleAddCredit(KafkaMessageData kafkaMessageData) {
        try {
            Gson gson = buildKraPayloadGson();
            List<ETimsCreditNoteDTO> creditNoteDTOSList = new ArrayList<>();
            for (Object obj : (List) ((Map) kafkaMessageData.getData()).get("etimsCreditNoteListDTO"))
                creditNoteDTOSList.add(gson.fromJson(gson.toJson(obj), ETimsCreditNoteDTO.class));
            kraeTimsService.processEtimsAddCreditNote(creditNoteDTOSList);
            log.info("Received eTimsCreditNoteDTO Data from ETims-Micro-Service : " + creditNoteDTOSList);
        } catch (Exception e) {
            log.error("Kafka Error Message received from Revenue Microservice while adding Credit Note: {} " + e.getMessage());
        }

    }
    @Async
    private void handleAddInvoice(KafkaMessageData kafkaMessageData) {
        try {
            Gson gson = buildKraPayloadGson();
            List<ETimsSaleDTO> invoiceList = new ArrayList<>();
            for (Object obj : (List) ((Map) kafkaMessageData.getData()).get("etimsInvoiceListDTO"))
                invoiceList.add(gson.fromJson(gson.toJson(obj), ETimsSaleDTO.class));
            kraeTimsService.processEtimsAddSale(invoiceList);
            log.info("Received ETimsInvoiceDTO Data from ETims-Micro-Service : " + invoiceList);
        } catch (Exception e) {
            log.error("Kafka Error Message received from Revenue Microservice while adding Inoice : {} " + e.getMessage());
        }
    }
    @Async
    private void handleETimsUpdateItem(KafkaMessageData kafkaMessageData) {
        try {
            ETimsItemDTO dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), ETimsItemDTO.class);
            kraeTimsService.processEtimsUpdateItem(dataMessage);
            log.info("Received ETimsCustomerDTO Data from ETims-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message received from CPM Microservice while Update item: {}  " + e.getMessage());
        }
    }
    @Async
    private void handleETimsAddItem(KafkaMessageData kafkaMessageData) {
        try {
            Gson gson = buildKraPayloadGson();
            List<ETimsItemDTO> itemDTOS = new ArrayList<>();
            for (Object obj : (List) ((Map) kafkaMessageData.getData()).get("responesDTO"))
                itemDTOS.add(gson.fromJson(gson.toJson(obj), ETimsItemDTO.class));
            kraeTimsService.processEtimsAddItemsListBatch(itemDTOS);
            log.info("Received ETimsCustomerDTO Data from ETims-Micro-Service : " );
        } catch (Exception e) {
            log.error("Kafka Error Message received from CPM Microservice while adding Item: {} " + e.getMessage());
        }
    }
    @Async
    private void handleETimsCustomer(KafkaMessageData kafkaMessageData) {
        try {
            Gson gson = buildKraPayloadGson();
            List<ETimsCustomerDTO> customerList = new ArrayList<>();
            for (Object obj : (List) ((Map) kafkaMessageData.getData()).get("etimsCustomerListDTO"))
                customerList.add(gson.fromJson(gson.toJson(obj), ETimsCustomerDTO.class));
            kraeTimsService.processEtimsAddCustomer(customerList);
            log.info("Received ETimsCustomerDTO Data from ETims-Micro-Service : " + customerList);
        } catch (Exception e) {
            log.error("Kafka Error Message received from CPM Microservice while adding Customer: {} " + e.getMessage());
        }
    }

    private Gson buildKraPayloadGson() {
        JsonDeserializer<Integer> integerDeserializer = (json, typeOfT, context) -> {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            String value = json.getAsString();
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return Double.valueOf(value).intValue();
        };
        return new GsonBuilder()
                .registerTypeAdapter(Integer.class, integerDeserializer)
                .registerTypeAdapter(int.class, integerDeserializer)
                .create();
    }


    private void saveWareHouseHandler(KafkaMessageData kafkaMessageData) {
        try {
            WareHouseDto dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), WareHouseDto.class);
            wareHouseService.saveWareHouseFromIntegration(dataMessage);
            log.info("Received WareHouse Data from Inventory-Micro-Service : " + dataMessage.toString());
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Inventory-Micro-Service : " + e.getMessage());
        }
    }

    private void saveCreditNoteGendataHandler(KafkaMessageData kafkaMessageData) {
        try {
            CreditNoteMessageIntegrationSystem dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), CreditNoteMessageIntegrationSystem.class);
            creditdocService.saveCreditNoteGenData(dataMessage);
            log.info("Received Credit Note Data from Revenue-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Revenue -Micro-Service : " + e.getMessage());
        }
    }

    private void saveChargeHandler(KafkaMessageData kafkaMessageData) {
        try {
            ChargeMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(kafkaMessageData.getData()), ChargeMessage.class);
//            ChargeMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()),ChargeMessage.class);
            chargeServices.save(dataMessage);
            log.info("Received Charge Data from Revenue-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS-Micro-Service : " + e.getMessage());
        }
    }

    //No use
    private void savePlanServiceHandler(KafkaMessageData kafkaMessageData) {
        try {
            PlanServiceMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), PlanServiceMessage.class);
            planGroupService.save(dataMessage);
            log.info("Received Plan Service Data from CMS-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS-Micro-Service : " + e.getMessage());
        }
    }

    private void saveTaxHandler(KafkaMessageData kafkaMessageData) {
        try {
            TaxMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), TaxMessage.class);
            taxService.save(dataMessage);
            log.info("Received Tax Data from CMS-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS-Micro-Service : " + e.getMessage());
        }
    }

    // no use
    private void saveServiceAreaHandler(KafkaMessageData kafkaMessageData) {
        try {
            ServiceAreaIn dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), ServiceAreaIn.class);
            serviceAreaInService.save(dataMessage);
            log.info("Received Service Area Data from common-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Common-Micro-Service : " + e.getMessage());
        }
    }

    private void saveBusinessUnitHandler(KafkaMessageData kafkaMessageData) {
        try {
            BusinessUnitMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), BusinessUnitMessage.class);
            businessUnitService.save(dataMessage);
            log.info("Received Business Unit Data from CMS-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Common-Micro-Service : " + e.getMessage());
        }
    }

    private void saveDebitDocumentHandler(KafkaMessageData kafkaMessageData) {
        try {
            DebitDocumentMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), DebitDocumentMessage.class);
            debitDocumentService.save(dataMessage);
            log.info("Received Debit Note Data from Revenue-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Revenue -Micro-Service : " + e.getMessage());
        }
    }

    private void saveCreditDocumentHandler(KafkaMessageData kafkaMessageData) {
        try {
            CreditDocMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), CreditDocMessage.class);
            creditdocService.save(dataMessage);
            log.info("Received Credit Note Data from Revenue-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Revenue -Micro-Service : " + e.getMessage());
        }
    }

    private void saveStaffUserHandle(KafkaMessageData kafkaMessageData) {
        try {
            SaveStaffUserSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveStaffUserSharedDataMessage.class);
            staffUserService.save(dataMessage);
            log.info("Received Staff User Data from Common-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Common-Micro-Service : " + e.getMessage());
        }
    }

    private void saveBranchHandler(KafkaMessageData kafkaMessageData) {
        try {
            SaveBranchSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), SaveBranchSharedDataMessage.class);
            branchService.save(dataMessage);
            log.info("Received Branch Data from Common-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Common-Micro-Service : " + e.getMessage());
        }
    }

    private void saveCustomerHandler(KafkaMessageData kafkaMessageData) {
        try {
//            CustomerMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), CustomerMessage.class);
            CustomerMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(kafkaMessageData.getData()), CustomerMessage.class);
            customerService.save(dataMessage);
            log.info("Received Customer Data from CMS-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS-Micro-Service : " + e.getMessage());
        }
    }

    private void cancelRegenerateInvoiceHandler(KafkaMessageData kafkaMessageData) {
        try {
            CancelRegenerateInvoice dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), CancelRegenerateInvoice.class);
            debitDocumentService.deleteflag(dataMessage);
            log.info("Received Cancel Regenerate Invoice Data from Revenue-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Revenue -Micro-Service : " + e.getMessage());
        }
    }

    private void saveCustPackageRelHandler(KafkaMessageData kafkaMessageData) {
        try {
            CustomerPackageRelMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), CustomerPackageRelMessage.class);
            customerPackageService.save(dataMessage);
            log.info("Received Customer Package Mapping Data from Revenue -Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Revenue -Micro-Service : " + e.getMessage());
        }
    }

    private void saveCustomerInventoryMappingHandler(KafkaMessageData kafkaMessageData) {
        try {
            CustomerInventoryMappingMessage dataMessage = new ObjectMapper().readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(kafkaMessageData.getData()), CustomerInventoryMappingMessage.class);
//                    new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()),CustomerInventoryMappingMessage.class);
            customerInventoryService.save(dataMessage);
            log.info("Received Customer Inventory Mapping Data from Inventory-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Inventory-Micro-Service : " + e.getMessage());
        }
    }

    private void saveCustServiceMappingHandle(KafkaMessageData kafkaMessageData) {
        try {
            CustomerServiceMappingMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), CustomerServiceMappingMessage.class);
            customerServiceMappingService.save(dataMessage);
            log.info("Received Customer Service Mapping Data from Revenue -Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS-Micro-Service : " + e.getMessage());
        }
    }

    private void saveServiceHandler(KafkaMessageData kafkaMessageData) {
        try {
            PlanServiceForIntegrationMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), PlanServiceForIntegrationMessage.class);
            servicesService.save(dataMessage);
            log.info("Received Service Data from CMS-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS-Micro-Service : " + e.getMessage());
        }
    }

    private void savePostpaidPlanHandler(KafkaMessageData kafkaMessageData) {
        try {
            PostpaidPlanMessage dataMessage = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(kafkaMessageData.getData()), PostpaidPlanMessage.class);
//            PostpaidPlanMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), PostpaidPlanMessage.class);
            postpaidPlanService.save(dataMessage);
            log.info("Received Postpaid Plan Data from CMS-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS-Micro-Service : " + e.getMessage());
        }
    }

    private void saveItemHandler(KafkaMessageData kafkaMessageData) {
        try {
            ItemMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), ItemMessage.class);
            inventoryItemService.save(dataMessage);
            log.info("Received Item Data from Inventory-Micro-Service : " + dataMessage);

        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Inventory-Micro-Service : " + e.getMessage());
        }
    }

    private void updateCustPlanMappingHandler(KafkaMessageData kafkaMessageData) {
        try {
            CustPlanMappingUpdateMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), CustPlanMappingUpdateMessage.class);
            customerPackageService.update(dataMessage);
            log.info("Received Customer Plan Mapping Update Data from CMS-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS-Micro-Service : " + e.getMessage());
        }
    }

    private void approveInventoryItemHandler(KafkaMessageData kafkaMessageData) {
        try {
            ApproveInventoryItemMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), ApproveInventoryItemMessage.class);
            approveInventoryItemService.save(dataMessage);
            log.info("Received Approve Inventory Item Data from Inventory-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Inventory-Micro-Service : " + e.getMessage());
        }
    }

    private void approveRemoveItemRequestHandler(KafkaMessageData kafkaMessageData) {
        try {
            ApproveRemoveInventoryItemRequestMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), ApproveRemoveInventoryItemRequestMessage.class);
            inventoryRemoveItemService.save(dataMessage);
            log.info("Received Approve Remove Item Request Data from Inventory-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Inventory-Micro-Service : " + e.getMessage());
        }
    }

    private void saveProductCategoryHandler(KafkaMessageData kafkaMessageData) {
        try {
            ProductCategoryDto dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), ProductCategoryDto.class);
            productCategoryService.saveProductCategoryFromInventory(dataMessage);
            log.info("Received Product Category Data from CMS-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Inventory-Micro-Service : " + e.getMessage());
        }
    }

    private void saveInwardFromInventoryHandler(KafkaMessageData kafkaMessageData) {
        try {
            InwardDto dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), InwardDto.class);
            inwardService.saveInwardFromInventory(dataMessage);
            log.info("Received Inward From Inventory Data from Inventory-Micro-Service : " + dataMessage);

        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Inventory-Micro-Service : " + e.getMessage());
        }
    }

    private void ticketIntegrationHandler(KafkaMessageData kafkaMessageData) {
        try {
            TicketMessageIntegration dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), TicketMessageIntegration.class);
            caseService.save(dataMessage);
            log.info("Received Ticket Integration Data from Ticketing-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Ticketing-Micro-Service : " + e.getMessage());
        }
    }

    private void activateNMSServiceHandler(KafkaMessageData kafkaMessageData) {
        try {
            NMSServiceActivationDTO dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), NMSServiceActivationDTO.class);
            nmsService.activateNMSServices(dataMessage);
            log.info("Received Activate NMS Service Data from CMS -Micro-Service : " + dataMessage);

        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS -Micro-Service : " + e.getMessage());
        }
    }

    private void nmsIntegrationHandler(KafkaMessageData kafkaMessageData) {
        try {
            NMSIntegrationMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), NMSIntegrationMessage.class);
            apiIntegrationService.nmsIntegration(dataMessage);
            log.info("Received Activate NMS Service Data from CMS -Micro-Service : " + dataMessage);

        } catch (Exception e) {
            log.error("Kafka Error Message Receive From CMS -Micro-Service : " + e.getMessage());
        }
    }

    private void deleteNmsServiceHandler(KafkaMessageData kafkaMessageData) {
        try {
            UuidDataDTO dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), UuidDataDTO.class);
            nmsService.deleteNMSService(dataMessage);
            log.info("Received Delete NMS Service Data from CMS -Micro-Service : " + dataMessage);

        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Revenue -Micro-Service : " + e.getMessage());
        }
    }

    private void savePaymentConfigHandler(KafkaMessageData kafkaMessageData) {
        try {
            PaymentConfigMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), PaymentConfigMessage.class);
            paymentConfigService.handleRecievePaymentConfig(dataMessage);
            log.info("Received Payment Config Data from Revenue-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Revenue -Micro-Service : " + e.getMessage());
        }
    }

    private void saveCustomerPaymentHandler(KafkaMessageData kafkaMessageData) {
        try {
            CustPayDTOMessage dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), CustPayDTOMessage.class);
            Boolean isCustomerPaymentExit = customerPaymentRepository.existsById(dataMessage.getOrderId());
            if (!isCustomerPaymentExit) {
                customerPaymentService.saveCustomerPayment(dataMessage);
            } else {
                customerPaymentService.updateCustomerPayment(dataMessage);
            }
            log.info("Received Customer Payment Data from Revenue-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Revenue -Micro-Service : " + e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(topics = {KafkaConstant.KAFKA_COMMON_TOPIC}, groupId = KafkaConstant.KAFKA_COMMON_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormCommonMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);

            if (handler == null) {
                handler = messageHandler.get(keyWithoutEventType);
            }

            if (handler != null) {
                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Integration Service Receive Kafka Error Message From Common-Micro-Service : " + e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(topics = {KafkaConstant.KAFKA_CMS_TOPIC}, groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormCustomerMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);

            if (handler == null) {
                handler = messageHandler.get(keyWithoutEventType);
            }

            if (handler != null) {
                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Integration Service Receive Kafka Error Message From Customer-Micro-Service : " + e.getMessage());
        }
    }


//    @Transactional
//    @RetryableTopic(attempts = "1",dltStrategy = DltStrategy.NO_DLT)
//    @KafkaListener(topics = {KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA},groupId = KafkaConstant.KAFKA_CMS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
//    public void kafkaMessageForCustomerData(KafkaMessageData message) {
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
//            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
//
//            if (handler == null) {

    /// /                If not found, try to find the handler with only dataType
//                handler = messageHandler.get(keyWithoutEventType);
//            }
//
//            if (handler != null) {
//                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
//                handler.accept(message);
//            } else {
//                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Revenue Service Receive Kafka Error Message From Customer-Micro-Service : "+ e.getMessage());
//        }
//    }
    @Transactional
    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(topics = {KafkaConstant.KAFKA_INVENTORY_TOPIC}, groupId = KafkaConstant.KAFKA_INVENTORY_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormInventoryMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);

            if (handler == null) {
                handler = messageHandler.get(keyWithoutEventType);
            }

            if (handler != null) {
                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Integration Service Receive Kafka Error Message From Inventory-Micro-Service : " + e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(topics = {KafkaConstant.KAFKA_REVENUE_TOPIC}, groupId = KafkaConstant.KAFKA_REVENUE_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormRevenueMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
            if (handler == null) {
                handler = messageHandler.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Integration Service Receive Kafka Error Message From Revenue-Micro-Service : " + e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(topics = {KafkaConstant.KAFKA_NOTIFICATION_TOPIC}, groupId = KafkaConstant.KAFKA_NOTIFICATION_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormNotificationMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
            if (handler == null) {
                handler = messageHandler.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Integration Service Receive Kafka Error Message From Notification-Micro-Service : " + e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(topics = {KafkaConstant.KAFKA_RADIUS_TOPIC}, groupId = KafkaConstant.KAFKA_RADIUS_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormRadiusMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
            if (handler == null) {
                handler = messageHandler.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Integration Service Receive Kafka Error Message From Radius-Micro-Service : " + e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(topics = {KafkaConstant.KAFKA_TICKET_TOPIC}, groupId = KafkaConstant.KAFKA_TICKET_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormTicketMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
            if (handler == null) {
                handler = messageHandler.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Integration Service Receive Kafka Error Message From Ticket-Micro-Service : " + e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(topics = {KafkaConstant.KAFKA_NETCONFIG_TOPIC}, groupId = KafkaConstant.KAFKA_NETCONFIG_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormNetConfigMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
            if (handler == null) {
                handler = messageHandler.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Integration Service Receive Kafka Error Message From NetConfig-Micro-Service : " + e.getMessage());
        }
    }


    @Transactional
    @RetryableTopic(attempts = "1", dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(topics = {KafkaConstant.KAFKA_SALES_CRM_TOPIC}, groupId = KafkaConstant.KAFKA_SALES_CRM_GROUP_ID, containerFactory = "kafkaListenerContainerFactory")
    public void kafkaMessageReceiveFormSalesCRMMicroService(KafkaMessageData message) {
        try {
            String dataType = message.getDataType();
            String eventType = message.getEventType();
            log.info("Received Kafka message with dataType: " + dataType + " and eventType: " + eventType);
            String keyWithEventType = dataType + ":" + eventType;
            String keyWithoutEventType = dataType;
            Consumer<KafkaMessageData> handler = messageHandler.get(keyWithEventType);
            if (handler == null) {
                handler = messageHandler.get(keyWithoutEventType);
            }
            if (handler != null) {
                log.info("Handling message with key: " + (handler == messageHandler.get(keyWithEventType) ? keyWithEventType : keyWithoutEventType));
                handler.accept(message);
            } else {
                log.debug("No handler found for key: " + keyWithEventType + " or " + keyWithoutEventType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Integration Service Receive Kafka Error Message From SalesCRM-Micro-Service : " + e.getMessage());
        }
    }

    private void sendIspInvoicePayload(KafkaMessageData kafkaMessageData) {
        try {
            IspMainPayload dataMessage = new Gson().fromJson(new Gson().toJson(kafkaMessageData.getData()), IspMainPayload.class);
            IspInvoicePayloadService ispInvoicePayloadService = SpringContext.getBean(IspInvoicePayloadService.class);
            ispInvoicePayloadService.requestToSendIspInvoicePayload(dataMessage);
            log.info("Received ISP Payload Data from Revenue-Micro-Service : " + dataMessage);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Revenue-Micro-Service : " + e.getMessage());
        }
    }

    private void saveCustomerData(KafkaMessageData message) {
        try {
            SaveCustomerDataShareMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveCustomerDataShareMessage.class);
            CustomersData customers = customersCreateService.saveCustomers(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }

    private void updateCustomerData(KafkaMessageData message) {
        try {
            UpdateCustomerShareDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateCustomerShareDataMessage.class);
            customersCreateService.updateCustomers(dataMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }

    private void CAFCustomerStatus(KafkaMessageData message) {
        try {
            CAFCustomerStatusMessage cafCustomerStatusMessage = new Gson().fromJson(new Gson().toJson(message.getData()), CAFCustomerStatusMessage.class);
            customersCreateService.saveCafToCustomer(cafCustomerStatusMessage);
            log.info("Kafka Message Receive From CMS-Micro-Service : " + message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Kafka Error Message Receive From Customer-Micro-Service : " + e.getMessage());
        }
    }

    public void handleMessageForMvnoCreate(KafkaMessageData message) {
        try {
            SaveMvnoSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), SaveMvnoSharedDataMessage.class);
            mvnoService.saveMVNOEntity(dataMessage);
            log.info("Handled MessageForMvnoCreate successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageForMvnoCreate: " + e.getMessage(), e);
        }
    }

    public void handleMessageForMvnoUpdate(KafkaMessageData message) {
        try {
            UpdateMvnoSharedDataMessage dataMessage = new Gson().fromJson(new Gson().toJson(message.getData()), UpdateMvnoSharedDataMessage.class);
            mvnoService.updateMVNOEntity(dataMessage);
            log.info("Handled MessageForMvnoUpdate successfully: " + message);
        } catch (Exception e) {
            log.error("Error handling MessageForMvnoUpdate: " + e.getMessage(), e);
        }
    }

    public void handleSendInvoiceMessage(KafkaMessageData messageData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            SendInvoiceMessage dataMessage = objectMapper.readValue(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(messageData.getData()), SendInvoiceMessage.class);
            traInvoiceService.processInvoiceMessage(dataMessage);
            log.info("Handled SendInvoiceMessage successfully: " + messageData);
        } catch (Exception e) {
            log.error("Error handling SendInvoiceMessage: " + e.getMessage(), e);
        }

    }


}
