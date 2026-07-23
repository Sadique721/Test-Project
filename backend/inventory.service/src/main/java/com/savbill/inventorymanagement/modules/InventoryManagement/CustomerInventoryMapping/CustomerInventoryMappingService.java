package com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping;

import com.savbill.inventorymanagement.core.constants.ClientServiceConstant;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.NMSIntegrationConstants;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.utillity.fileUtillity.FileUtility;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceService;
import com.savbill.inventorymanagement.modules.Customers.Customers;
import com.savbill.inventorymanagement.modules.Customers.QCustomers;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.CustInvParams;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.CustInvParamsDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.CustInvParamsMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.CustInvParamsRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory.InventorySpecificationHistory;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecificationHistory.InventorySpecificationHistoryRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping.ItemAssignHistoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemAssignHistoryMapping.ItemAssignHistoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDeviceBind;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkdeviceBindRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.IntegrationSpecificParamDTO;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaService;
import com.savbill.inventorymanagement.rabbitmq.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificatioParametersRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParameters;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserPojo;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.CasMaster.CasMaster;
import com.savbill.inventorymanagement.modules.CasMaster.CasMasterRepository;
import com.savbill.inventorymanagement.modules.CasMaster.CasParameterMapping;
import com.savbill.inventorymanagement.modules.ChargeManagement.Charge;
import com.savbill.inventorymanagement.modules.ChargeManagement.ChargeRepository;
import com.savbill.inventorymanagement.modules.ClientService.ClientService;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceRepository;
import com.savbill.inventorymanagement.modules.CustPlanMapping.CustPlanMappping;
import com.savbill.inventorymanagement.modules.CustPlanMapping.CustPlanMapppingRepository;
import com.savbill.inventorymanagement.modules.CustPlanMapping.QCustPlanMappping;
import com.savbill.inventorymanagement.modules.CustomerPackage.CustomerPackage;
import com.savbill.inventorymanagement.modules.CustomerPackage.CustomerPackageRepository;
import com.savbill.inventorymanagement.modules.CustomerServiceMapping.CustomerServiceMapping;
import com.savbill.inventorymanagement.modules.CustomerServiceMapping.CustomerServiceMappingRepository;
import com.savbill.inventorymanagement.modules.DebitDoc.DebitDocRepository;
import com.savbill.inventorymanagement.modules.DebitDoc.DebitDocument;
import com.savbill.inventorymanagement.modules.DebitDocInventoryRel.DebitDocumentInventoryRel;
import com.savbill.inventorymanagement.modules.DebitDocInventoryRel.DebitDocumentInventoryRelRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMappingService;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMappping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMapppingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.QCustMacMappping;
import com.savbill.inventorymanagement.modules.InventoryManagement.DtvHistory.DtvHistory;
import com.savbill.inventorymanagement.modules.InventoryManagement.DtvHistory.DtvHistoryRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.QExternalItemManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagementDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagementService;
import com.savbill.inventorymanagement.modules.InventoryManagement.GenerateremoveInventoryRequest.GenerateRemoveRequest;
import com.savbill.inventorymanagement.modules.InventoryManagement.GenerateremoveInventoryRequest.GenerateRemoveRequestRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.ApproveReplaceAllInventoryDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMappingMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemGroup.ItemAssemblyController;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemGroup.ItemAssemblyDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemGroup.ItemAssemblyRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemGroup.ItemAssemblyServiceImp;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.ItemWarranty.ItemWarrantyMappingServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.model.NetworkDeviceDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkDeviceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.service.NetworkDeviceService;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.NonSerializedItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategory;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwner;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.ProductPlanMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ReplacementItemHistory.ReplacementItemHistory;
import com.savbill.inventorymanagement.modules.InventoryManagement.ReplacementItemHistory.ReplacementItemHistoryRepo;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaMapper;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.inventorymanagement.modules.PlanGroupMapping.PlanGroupMappingRepository;
import com.savbill.inventorymanagement.modules.PlanGroup.PlanGroupRepository;
import com.savbill.inventorymanagement.modules.PlanService.PlanServiceRepository;
import com.savbill.inventorymanagement.modules.Postpaidplan.PostpaidPlanRepo;
import com.savbill.inventorymanagement.modules.Services.ServiceRepository;
import com.savbill.inventorymanagement.modules.Services.Services;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserService;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.Tax;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.TaxRepository;
import com.savbill.inventorymanagement.modules.TaxManagement.TaxTier.TaxTypeTier;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy.HierarchyRepository;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy.HierarchyService;
import com.savbill.inventorymanagement.modules.WorkflowManagement.WorkflowAudit.WorkflowAuditService;
import com.savbill.inventorymanagement.modules.WorkflowManagement.workflow.domain.WorkflowAssignStaffMapping;
import com.savbill.inventorymanagement.modules.WorkflowManagement.workflow.repository.WorkflowAssignStaffMappingRepo;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.StatusConstants;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Customer inventory mapping service.
 */
//TODO: Remove ExBaseAbstractService and add AbstractService
@Service
public class CustomerInventoryMappingService extends ExBaseAbstractService<CustomerInventoryMappingDto, CustomerInventoryMapping, Long> {
    /**
     * Instantiates a new Customer inventory mapping service.
     * @param repository the repository
     * @param mapper the mapper
     */
    public CustomerInventoryMappingService(CustomerInventoryMappingRepo repository, CustomerInventoryMappingMapper mapper) {
        super(repository, mapper);
    }

    private static final Logger logger = Logger.getLogger(CustomerInventoryMappingService.class);

    /**
     * Gets module name for log.
     * @return the module name for log
     */
    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CustomerInventoryMappingRepo repository;

    @Autowired
    InventoryMappingRepo inventoryMappingRepo;

    @Autowired
    ProductServiceImpl productService;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    CustomerInventoryMappingMapper mapper;
    @Autowired
    HierarchyRepository hierarchyRepository;
    //
    /// /    @Autowired
    /// /    StaffUserService staffUserService;
//
    @Autowired
    HierarchyService hierarchyService;
    //
    @Autowired
    OutwardServiceImpl outwardService;

    @Autowired
    InwardServiceImpl inwardService;

    @Autowired
    ExternalItemManagementService externalItemManagementService;

    @Autowired
    InOutWardMACService inOutWardMACService;

    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    CustMacMappingService custMacMapppingService;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    InwardMapper inwardMapper;
//    @Autowired
//    private ClientServiceSrv clientServiceSrv;

    @Autowired
    WorkflowAuditService workflowAuditService;
    @Autowired
    StaffUserService staffUserService;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    InwardRepository inwardRepository;

    @Autowired
    ProductCategoryService productCategoryService;

    @Autowired
    ServiceAreaMapper serviceAreaMapper;

    @Autowired
    NetworkDeviceService networkDeviceService;
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;

    @Autowired
    private CustPlanMapppingRepository custPlanMappingRepository;

    @Autowired
    ItemServiceImpl itemService;

    @Autowired
    ItemWarrantyMappingRepository itemWarrantyMappingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ExternalItemManagementRepository externalItemManagementRepository;


    @Autowired
    InOutWardMacRepo inOutWardMacRepo;
    @Autowired
    ExternalItemMacSerialMappingRepo externalItemMacSerialMappingRepo;
    @Autowired
    NetworkDeviceRepository networkDeviceRepository;
    /**
     * The Networkdevice bind repository.
     */
    @Autowired
    NetworkdeviceBindRepository networkdeviceBindRepository;

    @Autowired
    ItemWarrantyMappingServiceImpl itemWarrantyMappingService;

//    @Autowired
//    private ServicesService servicesService;

    @Autowired
    private ServiceRepository serviceRepository;

//    @Autowired
//    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustomerPackageRepository customerPackageRepository;

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;
    //
    @Autowired
    private CustMacMapppingRepository custMacMapppingRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    private ProductOwnerService productOwnerService;

    @Autowired
    private ItemAssemblyRepo itemAssemblyRepo;

    @Autowired
    private ItemAssemblyController itemAssemblyController;

    @Autowired
    private CustomerInventoryMappingMapper customerInventoryMappingMapper;

    @Autowired
    private ItemAssemblyServiceImp itemAssemblyServiceImp;

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private PlanGroupMappingRepository planGroupMappingRepository;
    @Autowired
    private PlanGroupRepository planGroupRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    CasMasterRepository casMasterRepo;

//    @Autowired
//    EzBillServiceUtility ezBillServiceUtility;

    @Autowired
    PlanServiceRepository planServiceRepository;

    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    ProductPlanMappingRepository productPlanMappingRepository;

    @Autowired
    InventoryMappingMapper inventoryMappingMapper;
    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private GenerateRemoveRequestRepo generateRemoveRequestRepo;

    @Autowired
    private NonSerializedItemRepository nonSerializedItemRepository;

    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private ProductOwnerRepository productOwnerRepository;

    @Autowired
    private DtvHistoryRepo dtvHistoryRepo;

    @Autowired
    private DebitDocumentInventoryRelRepository debitDocumentInventoryRelRepository;
    @Autowired
    private ReplacementItemHistoryRepo replacementItemHistoryRepo;
    @Autowired
    WorkflowAssignStaffMappingRepo workflowAssignStaffMappingRepo;
    @Autowired
    private CustInvParamsMapper custInvParamsMapper;
    @Autowired
    private CustInvParamsRepo custInvParamsRepo;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    ItemAssignHistoryMappingRepo itemAssignHistoryMappingRepo;

    @Autowired
    ClientServiceService clientServiceSrv;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private SpecificatioParametersRepo specificatioParametersRepo;

    @Autowired
    private InventorySpecificationHistoryRepo inventorySpecificationHistoryRepo;

    @Autowired
    private CustomerInventoryFileMappingRepo customerInventoryFileMappingRepo;

    @Autowired
    ServiceAreaService serviceAreaService;

    private static final Logger LOGGER = Logger.getLogger(CustomerInventoryMappingService.class);

    /**
     * Approve individual inventory generic data dto.
     * @param customerInventoryMappingIdList the customer inventory mapping id list
     * @param isApproveRequest the is approve request
     * @param nextstaff the nextstaff
     * @param remark the remark
     * @return the generic data dto
     * @throws Exception the exception
     */
   @Transactional
    public GenericDataDTO approveIndividualInventory(List<Long> customerInventoryMappingIdList, boolean isApproveRequest, Integer nextstaff, String remark) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<CustomerInventoryMappingDto> customerInventoryMappingDtoList = new ArrayList<>();
            for (Long customerInventoryMappingId : customerInventoryMappingIdList) {
                /** Method: Approve Inventory */
                genericDataDTO = approveInventory(customerInventoryMappingId, isApproveRequest, nextstaff, remark);
                customerInventoryMappingDtoList.add((CustomerInventoryMappingDto) genericDataDTO.getData());
            }
            if (genericDataDTO.getData() != null) {
                /** Method: Approve Pair Inventory */
                approvePairInventory(customerInventoryMappingDtoList, isApproveRequest, genericDataDTO);
                /** Method: Approve Single Inventory */
                approveSingleInventory(customerInventoryMappingDtoList, isApproveRequest, genericDataDTO);
            }
            // genericDataDTO.setDataList(customerInventoryMappingIdList);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            return genericDataDTO;
        } catch (CustomValidationException customValidationException) {
            customValidationException.printStackTrace();
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), customValidationException.getMessage(), null);
        } catch (Exception exception) {
            exception.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(exception.getMessage());
        }
        return genericDataDTO;
    }

    /**
     * Send customer inventory to nms integration.
     * @param item the item
     * @param product the product
     * @param custInventoryId the cust inventory id
     * @param customerId the customer id
     * @param operation the operation
     */
    public void sendCustomerInventoryToNMSIntegration(Item item, Long pcId, Long custInventoryId, Customers customers, String operation) {
        try {
            List<SpecificationParameters> specificationParameters = specificatioParametersRepo.findAllByProductCategory_Id(pcId);
            if (!specificationParameters.isEmpty()) {
                List<InventorySpecificationHistory> inventorySpecifications = inventorySpecificationHistoryRepo.findAllByItemIdAndStatus(item.getId(), CommonConstants.NEW);
                if (!inventorySpecifications.isEmpty()) {
                    List<IntegrationSpecificParamDTO> integrationParams = buildIntegrationSpecificParams(inventorySpecifications, item.getSerialNumber());
                    if (!integrationParams.isEmpty()) {
                        addSerialNumberParam(item, integrationParams);
                        addONUIDParam(item, integrationParams);
                        addPPPOEUSERParam(integrationParams, customers.getUsername());
                        addPPPOEPASSWDParam(integrationParams, customers.getPassword());
                        NMSIntegrationMessage nmsIntegrationMessage = buildNMSIntegrationMessage(integrationParams, item, custInventoryId, customers.getId().longValue(), operation);
                        kafkaMessageSender.send(new KafkaMessageData(nmsIntegrationMessage, NMSIntegrationMessage.class.getSimpleName()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Add onuid param.
     * @param item the item
     * @param integrationParams the integration params
     */
    private void addONUIDParam(Item item, List<IntegrationSpecificParamDTO> integrationParams) {
        IntegrationSpecificParamDTO serialNumberParam = new IntegrationSpecificParamDTO();
        serialNumberParam.setParamName(NMSIntegrationConstants.NMS_INTEGRATION.ONU_ID);
        serialNumberParam.setParamValue(item.getSerialNumber());
        integrationParams.add(serialNumberParam);
    }

    /**
     * Build integration specific params list.
     * @param inventorySpecifications the inventory specifications
     * @param serialNumber the serial number
     * @return the list
     */
    private List<IntegrationSpecificParamDTO> buildIntegrationSpecificParams(List<InventorySpecificationHistory> inventorySpecifications, String serialNumber) {
        try {
            List<IntegrationSpecificParamDTO> integrationParams = new ArrayList<>();
            for (InventorySpecificationHistory inventorySpecification : inventorySpecifications) {
                Optional<SpecificationParameters> specParamOptional = specificatioParametersRepo.findById(inventorySpecification.getParamId());
                if (specParamOptional.isPresent()) {
                    SpecificationParameters specParam = specParamOptional.get();
                    IntegrationSpecificParamDTO paramDTO = new IntegrationSpecificParamDTO();
                    if (specParam.getParamName().equalsIgnoreCase(NMSIntegrationConstants.NMS_INTEGRATION.SERIAL_NO)) {
                        paramDTO.setParamName(NMSIntegrationConstants.NMS_INTEGRATION.SERIAL_NO);
                        paramDTO.setParamValue(serialNumber);
                    } else if (specParam.getParamName().equalsIgnoreCase(NMSIntegrationConstants.NMS_INTEGRATION.ONU_ID)) {
                        paramDTO.setParamName(NMSIntegrationConstants.NMS_INTEGRATION.ONU_ID);
                        paramDTO.setParamValue(serialNumber);
                    } else {
                        paramDTO.setParamName(specParam.getParamName());
                        paramDTO.setParamValue(inventorySpecification.getParamValue());
                    }
                    integrationParams.add(paramDTO);
                }
            }
            return integrationParams;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Add serial number param.
     * @param item the item
     * @param integrationParams the integration params
     */
    private void addSerialNumberParam(Item item, List<IntegrationSpecificParamDTO> integrationParams) {
        IntegrationSpecificParamDTO serialNumberParam = new IntegrationSpecificParamDTO();
        serialNumberParam.setParamName(NMSIntegrationConstants.NMS_INTEGRATION.SERIAL_NO);
        serialNumberParam.setParamValue(item.getSerialNumber());
        integrationParams.add(serialNumberParam);
    }

    private void addPPPOEUSERParam(List<IntegrationSpecificParamDTO> integrationParams, String username) {
        IntegrationSpecificParamDTO serialNumberParam = new IntegrationSpecificParamDTO();
        serialNumberParam.setParamName(NMSIntegrationConstants.NMS_INTEGRATION.PPPOEUSER);
        serialNumberParam.setParamValue(username);
        integrationParams.add(serialNumberParam);
    }

    private void addPPPOEPASSWDParam(List<IntegrationSpecificParamDTO> integrationParams, String password) {
        IntegrationSpecificParamDTO serialNumberParam = new IntegrationSpecificParamDTO();
        serialNumberParam.setParamName(NMSIntegrationConstants.NMS_INTEGRATION.PPPOEPASSWD);
        serialNumberParam.setParamValue(password);
        integrationParams.add(serialNumberParam);
    }

    /**
     * Build nms integration message nms integration message.
     * @param integrationParams the integration params
     * @param item the item
     * @param custInventoryId the cust inventory id
     * @param customerId the customer id
     * @param operation the operation
     * @return the nms integration message
     */
    private NMSIntegrationMessage buildNMSIntegrationMessage(List<IntegrationSpecificParamDTO> integrationParams,
                                                             Item item, Long custInventoryId,
                                                             Long customerId, String operation) {
        try {
            NMSIntegrationMessage message = new NMSIntegrationMessage();
            message.setList(integrationParams);
            message.setOperation(operation);
            message.setCustomerId(customerId);
            message.setItemId(item.getId());
            message.setConfigName(NMSIntegrationConstants.NMS_INTEGRATION.CONFIGURATION_NAME);
            message.setCustInvenId(custInventoryId);
            message.setMvnoId(getMvnoIdFromCurrentStaff().longValue());
            message.setLoggedInUserId(getLoggedInUserId());
            message.setSerialNumber(item.getSerialNumber());
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Approve single inventory.
     * @param customerInventoryMappingDtoList the customer inventory mapping dto list
     * @param isApproveRequest the is approve request
     * @param genericDataDTO the generic data dto
     */
    private void approveSingleInventory(List<CustomerInventoryMappingDto> customerInventoryMappingDtoList, boolean isApproveRequest, GenericDataDTO genericDataDTO) {
        try {
            if (customerInventoryMappingDtoList.size() == 1 && isApproveRequest && ((CustomerInventoryMappingDto) genericDataDTO.getData()).getStatus().equalsIgnoreCase("ACTIVE")) {
                String authToken = "";
                Customers customers = customersRepository.findById(customerInventoryMappingDtoList.get(0).getCustomerId()).orElse(null);
                ProductCategory productCategory = productCategoryRepository.findById(productRepository.findById(customerInventoryMappingDtoList.get(0).getProductId()).get().getProductCategory().getId()).get();
                if (productCategory.isHasCas() == true && productCategory.getDtvCategory().equalsIgnoreCase("STB")) {
                    Product product = productRepository.findById(customerInventoryMappingDtoList.get(0).getProductId()).get();
                    CasMaster casMaster = casMasterRepo.findById(product.getCaseId()).get();
                    if (casMaster.getCasParameterMappings().size() > 0) {
                        List<CasParameterMapping> casParamaterMappings = casMaster.getCasParameterMappings().stream().filter(casParamaterMapping -> casParamaterMapping.getParamName().equalsIgnoreCase(CommonConstants.CAS_PARAMS.AUTH_TOKEN_EZ_BILL)).collect(Collectors.toList());
                        if (casParamaterMappings.size() > 0) {
                            authToken = casParamaterMappings.get(0).getParamName();
                        }
                        String boxNumber = itemRepository.findById(customerInventoryMappingDtoList.get(0).getItemId()).get().getSerialNumber();
                    }
                    //                        ezBillServiceUtility.getPairedInfoResponse(boxNumber, null, customerInventoryMappingDtoList.get(0).getConnectionNo(), customers, casMaster, customerInventoryMappingDtoList.get(0));
                }
            }
            CustomerInventoryMappingDto customerInventoryMappingDto = ((CustomerInventoryMappingDto) genericDataDTO.getData());
            /** Method: Save Inventory Assign History Mapping */
            saveInventoryAssignHistoryMapping(Collections.singletonList(customerInventoryMappingDto));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Approve pair inventory.
     * @param customerInventoryMappingDtoList the customer inventory mapping dto list
     * @param isApproveRequest the is approve request
     * @param genericDataDTO the generic data dto
     */
    private void approvePairInventory(List<CustomerInventoryMappingDto> customerInventoryMappingDtoList, boolean isApproveRequest, GenericDataDTO genericDataDTO) {
        try {
            if (customerInventoryMappingDtoList.size() == 2 && isApproveRequest && ((CustomerInventoryMappingDto) genericDataDTO.getData()).getStatus().equalsIgnoreCase("ACTIVE")) {
                Set<Long> productId = customerInventoryMappingDtoList.stream().map(CustomerInventoryMappingDto::getProductId).collect(Collectors.toSet());
                List<Product> productList = productRepository.findAllById(productId);
                Set<Long> pcIdList = new HashSet<>();
                productList.stream().forEach(r -> {
                    pcIdList.add(Long.valueOf(r.getProductCategory().getId()));

                });
                List<ProductCategory> productCategoryList = productCategoryRepository.findAllByIdIn(pcIdList);
                if ((productCategoryList.get(0).getDtvCategory().equalsIgnoreCase("STB") || productCategoryList.get(0).getDtvCategory().equalsIgnoreCase("Card")) && ((productCategoryList.get(1).getDtvCategory().equalsIgnoreCase("STB") || productCategoryList.get(1).getDtvCategory().equalsIgnoreCase("Card")))) {
                    List<CustomerInventoryMapping> assemblyInventories = null;
                    for (CustomerInventoryMappingDto customerInventoryMappingDto : customerInventoryMappingDtoList) {
                        Customers customers = customersRepository.findById(customerInventoryMappingDto.getCustomerId()).orElse(null);
                        ProductCategory productCategory = productCategoryRepository.findById(productRepository.findById(itemRepository.findById(customerInventoryMappingDto.getItemId()).get().getProductId()).get().getProductCategory().getId()).get();
                        if (productCategory.getDtvCategory().equalsIgnoreCase("STB")) {
                            assemblyInventories = customerInventoryMappingRepo.findAllByItemAssemblyId(customerInventoryMappingDto.getItemAssemblyId());
                            Product product = productRepository.findById(customerInventoryMappingDto.getProductId()).get();
                            CasMaster casMaster = casMasterRepo.findById(product.getCaseId()).get();
                            if (casMaster != null) {
                                String boxNumber = itemRepository.findById(customerInventoryMappingDto.getItemId()).get().getSerialNumber();
                            }
                        }
                        if (productCategory.getDtvCategory().equalsIgnoreCase("Card")) {
                            Product product = productRepository.findById(customerInventoryMappingDto.getProductId()).get();
                            //                        CasMaster casMaster = casMasterRepo.findById(product.getCaseId()).get();
                            String cardNumber = itemRepository.findById(customerInventoryMappingDto.getItemId()).get().getSerialNumber();
                        }
                    }
                    //                        ezBillServiceUtility.getPairedInfoResponse(boxNumber.toString(), cardNumber.toString(), customerInventoryMappingDtoList.get(0).getConnectionNo(), customers, casMaster, customerInventoryMappingDtoList.get(0));
                    assemblyInventories.forEach(assemblyInventorie -> assemblyInventorie.setPairStatus("Paired"));
                    List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.saveAll(assemblyInventories);
                    List<CustomerInventoryMappingDto> inventoryDtos = new ArrayList<>();
                    for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                        CustomerInventoryMappingDto customerInventoryMappingDto = new CustomerInventoryMappingDto();
                        customerInventoryMappingDto.setItemId(customerInventoryMapping.getItemId());
                        customerInventoryMappingDto.setCustomerId(customerInventoryMapping.getCustomer().getId());
                        inventoryDtos.add(customerInventoryMappingDto);
                    }
                    /** Method: Save Inventory Assign History Mapping */
                    saveInventoryAssignHistoryMapping(inventoryDtos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Save inventory assign history mapping.
     * @param inventoryMappingDtos the inventory mapping dtos
     */
    private void saveInventoryAssignHistoryMapping(List<? extends CustomerInventoryMappingDto> inventoryMappingDtos) {
        try {
            for (CustomerInventoryMappingDto inventoryMappingDto : inventoryMappingDtos) {
                if (inventoryMappingDto != null) {
                    Optional<ItemAssignHistoryMapping> existingItemAssignHistoryMapping =
                            itemAssignHistoryMappingRepo.findLatestByItemId(inventoryMappingDto.getItemId());
                    if (existingItemAssignHistoryMapping.isPresent()) {
                        ItemAssignHistoryMapping oldItemAssignHistoryMapping = existingItemAssignHistoryMapping.get();
                        ItemAssignHistoryMapping itemAssignHistoryMapping = new ItemAssignHistoryMapping();
                        itemAssignHistoryMapping.setItemId(inventoryMappingDto.getItemId());
                        itemAssignHistoryMapping.setOwnerId(inventoryMappingDto.getCustomerId() != null
                                ? inventoryMappingDto.getCustomerId().longValue()
                                : null);
                        itemAssignHistoryMapping.setOwnerType(CommonConstants.CUSTOMER);
                        itemAssignHistoryMapping.setCreatedate(LocalDateTime.now());
                        itemAssignHistoryMapping.setSpecificationHistoryId(oldItemAssignHistoryMapping.getSpecificationHistoryId());
                        itemAssignHistoryMappingRepo.save(itemAssignHistoryMapping);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Approve inventory generic data dto.
     * @param customerInventoryMappingId the customer inventory mapping id
     * @param isApproveRequest the is approve request
     * @param nextStaff the next staff
     * @param remark the remark
     * @return the generic data dto
     * @throws Exception the exception
     */
   @Transactional
    public GenericDataDTO approveInventory(Long customerInventoryMappingId, boolean isApproveRequest, Integer nextStaff, String remark) throws Exception {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            CustomerInventoryMappingDto entity = super.getEntityById(customerInventoryMappingId);
            Customers customers = customersRepository.findById(entity.getCustomerId()).get();
            Product product = productRepository.findById(entity.getProductId()).get();
//        ProductDto dto = productMapper.domainToDTO(product, new CycleAvoidingMappingContext());
            StaffUser loggedInUser = staffUserRepository.findById(Math.toIntExact(Long.valueOf(getLoggedInUserId()))).get();
//        CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(customerInventoryMappingId).get();
            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
            CustomerInventoryMapping subisuCustInvenMapping = null;
            Long subisuCustInventoryMappingId = null;
            if (customerInventoryMapping.getBillTo().equalsIgnoreCase(CommonConstants.CUSTOMER_INVENTORY_BILL_TO.SUBISU) || customerInventoryMapping.getBillTo().equalsIgnoreCase(CommonConstants.CUSTOMER_INVENTORY_BILL_TO.ORGANIZATION)) {
                QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression = qCustomerInventoryMapping.mapping_ref_id.eq(customerInventoryMappingId);
                subisuCustInvenMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                subisuCustInventoryMappingId = subisuCustInvenMapping.getId();
            }
            boolean hasmac = product.getProductCategory().isHasMac();
            boolean hasserial = product.getProductCategory().isHasSerial();
            Item item = new Item();
            if (hasserial || hasmac) {
                item = itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
            }
            if (Objects.equals(loggedInUser.getUsername(), "admin") || Objects.equals(loggedInUser.getUsername(), "superadmin")) {
                if (isApproveRequest) {
                    /** Method: Approve Inventory By Admin or Superadmin User */
                    entity = approveByAdminOrSuperadmin(entity, customerInventoryMappingId, isApproveRequest,
                            customerInventoryMapping, genericDataDTO, hasmac, hasserial,
                            subisuCustInventoryMappingId, subisuCustInvenMapping, item);
                } else {
                    /** Method: Reject Inventory By Admin or Superadmin User*/
                    entity = rejectByAdminOrSuperadmin(entity, customerInventoryMappingId, isApproveRequest,
                            customerInventoryMapping, genericDataDTO, subisuCustInventoryMappingId,
                            subisuCustInvenMapping, item);
                }
                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remark + "\n" + entity.getFlag() + " By :- " + loggedInUser.getUsername());
                genericDataDTO.setData(super.saveEntity(entity));
                //if(isApproveRequest && customers!=null && customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION))
                //{
                //CustomerInventoryMapping mapping=customerInventoryMappingRepo.findById(customerInventoryMappingId).orElse(null);
                //mapping.setStatus(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION);
                //customerInventoryMappingRepo.save(mapping);
                //}
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            }

            if (clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
                StaffUser assignedUser = null;
                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                    StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                    assignedUser = staffUser;
                    entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    entity.setNextApproverId(Integer.valueOf(map.get("staffId")));
                    entity.setPreviousApproveId(getLoggedInUserId());
                    entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    entity.setStatus("PENDING");
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
                } else {
                    if (isApproveRequest) {
                        /** Method: Automatic Workflow Assign for Approve Inventory */
                        entity = autoApproveRequest(entity, customerInventoryMappingId, isApproveRequest,
                                customerInventoryMapping, genericDataDTO, hasmac, hasserial,
                                subisuCustInventoryMappingId, subisuCustInvenMapping, item);
                    } else {
                        /** Method: Automatic Workfolw Assign for Reject Inventory */
                        entity = autoRejectRequest(entity, customerInventoryMappingId, isApproveRequest,
                                customerInventoryMapping, genericDataDTO, subisuCustInventoryMappingId,
                                subisuCustInvenMapping, item);
                    }
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
                }
                //TAT functionality
                //            if (assignedUser != null) {
                //                if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                //                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                //                        map.put("tat_id", map.get("current_tat_id"));
                //                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, entity.getId().intValue(), null);
                //                }
                //            }
            } else {
                if (!isApproveRequest && entity.getTeamHierarchyMappingId() == null) {
                    /** Method: Reject Inventory Request with Hierarchy Null */
                    entity = rejectRequestWithTeamHierarchyNull(entity, customerInventoryMappingId, isApproveRequest,
                            customerInventoryMapping, item, loggedInUser, remark);
                } else {
                    Map<String, Object> map = hierarchyService.getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
                    if (map.containsKey("assignableStaff")) {
                        //                    StaffUser staffUser = staffUserService.get(nextStaff);
                        StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(nextStaff)).get();
                        genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
                        entity.setStatus("PENDING");
                        if (isApproveRequest) {
                            entity.setFlag("approved");
                        } else {
                            entity.setFlag("rejected");
                        }
                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remark + "\n" + entity.getFlag() + " By :- " + staffUser.getUsername());
                        return genericDataDTO;
                    } else {
                        if (isApproveRequest) {
                            /** Method: Approve Inventory Request with Other User */
                            entity = approveRequestByOtherUser(entity, customerInventoryMappingId, isApproveRequest,
                                    customerInventoryMapping, genericDataDTO, hasmac, hasserial,
                                    subisuCustInventoryMappingId, subisuCustInvenMapping, item);
                        } else {
                            /** Method: Reject Inventory Request with Other User */
                            entity = rejectRequestByOtherUser(entity, customerInventoryMappingId, isApproveRequest,
                                    customerInventoryMapping, genericDataDTO, subisuCustInventoryMappingId,
                                    subisuCustInvenMapping, item);
                        }
                        entity.setPreviousApproveId(null);
                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remark + "\n" + entity.getFlag() + " By :- " + loggedInUser.getUsername());
                    }
                }
            }
            CustomerInventoryMappingDto customerInventoryMappingDto = super.saveEntity(entity);
//        boolean itemAssemblyflag = customerInventoryMappingDto.isItemAssemblyflag();
//        Long externalItem = customerInventoryMappingDto.getExternalItemId();
//        Long custInventoryId = customerInventoryMappingDto.getId();
//        Long customerId = customerInventoryMappingDto.getCustomerId().longValue();
//        String status = customerInventoryMappingDto.getStatus();
//        String nmsEnable = clientServiceRepository.findValueByNameAndMvnoId(NMSIntegrationConstants.NMS_INTEGRATION.NMS_ENABLE, getMvnoIdFromCurrentStaff());
//        if (!itemAssemblyflag &&
//                isApproveRequest &&
//                status.equalsIgnoreCase("ACTIVE") &&
//                externalItem == null &&
//                nmsEnable.equalsIgnoreCase(NMSIntegrationConstants.NMS_INTEGRATION.TRUE_FLAG)) {
//            if (hasserial || hasmac) {
//                sendCustomerInventoryToNMSIntegration(item, product, custInventoryId, customerId, NMSIntegrationConstants.NMS_INTEGRATION.ADD_ONU_OPERATION);
//            }
//        }
            genericDataDTO.setData(customerInventoryMappingDto);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Reject request by other user customer inventory mapping dto.
     * @param entity the entity
     * @param customerInventoryMappingId the customer inventory mapping id
     * @param isApproveRequest the is approve request
     * @param customerInventoryMapping the customer inventory mapping
     * @param genericDataDTO the generic data dto
     * @param subisuCustInventoryMappingId the subisu cust inventory mapping id
     * @param subisuCustInvenMapping the subisu cust inven mapping
     * @param item the item
     * @return the customer inventory mapping dto
     * @throws Exception the exception
     */
    private CustomerInventoryMappingDto rejectRequestByOtherUser(CustomerInventoryMappingDto entity, Long customerInventoryMappingId, boolean isApproveRequest, CustomerInventoryMapping customerInventoryMapping, GenericDataDTO genericDataDTO, Long subisuCustInventoryMappingId, CustomerInventoryMapping subisuCustInvenMapping, Item item) throws Exception {
        try {
            entity.setStatus("REJECTED");
            entity = updateItemChanges(customerInventoryMappingId, isApproveRequest, customerInventoryMapping.getCreatedById(), customerInventoryMapping.getLastModifiedById());
            entity.setFlag("rejected");
            entity.setNextApproverId(null);
            entity.setTeamHierarchyMappingId(null);
            entity.setPreviousApproveId(null);
            genericDataDTO.setData(super.saveEntity(entity));
            CustomerInventoryMappingDto subisuCustInvenDto = new CustomerInventoryMappingDto();
            if (subisuCustInventoryMappingId != null && subisuCustInvenMapping != null) {
                subisuCustInvenDto.setStatus("REJECTED");
                subisuCustInvenDto = updateItemChanges(subisuCustInventoryMappingId, isApproveRequest, subisuCustInvenMapping.getCreatedById(), subisuCustInvenMapping.getLastModifiedById());
                subisuCustInvenDto.setFlag("rejected");
                subisuCustInvenDto.setNextApproverId(null);
                subisuCustInvenDto.setPreviousApproveId(null);
                subisuCustInvenDto.setTeamHierarchyMappingId(null);
                genericDataDTO.setData(super.saveEntity(subisuCustInvenDto));
            }
            /**
             * Send Rejected Inventory From Inventory to CMS
             */
            InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
            inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
            inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
            if (item != null) {
                inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
            }
            inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
            inventorySerialNumberMessage.setOperation(CommonConstants.ASSIGN_INVETORIES);
            inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
            inventorySerialNumberMessage.setCustInventoryId(customerInventoryMappingId);
            inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
            inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
            if (item != null) {
                inventorySerialNumberMessage.setItemId(item.getId());
                inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                inventorySerialNumberMessage.setItemName(item.getName());
            }
            inventorySerialNumberMessage.setStatus("REJECTED");
            inventorySerialNumberMessage.setQty(customerInventoryMapping.getQty());
            inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
            inventorySerialNumberMessage.setVendorId(getvendorIdAgainstCustomerInventory(Long.valueOf(customerInventoryMapping.getProduct().getId())));
            kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
//                        messageSender.send(inventorySerialNumberMessage, RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS);
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Approve request by other user customer inventory mapping dto.
     * @param entity the entity
     * @param customerInventoryMappingId the customer inventory mapping id
     * @param isApproveRequest the is approve request
     * @param customerInventoryMapping the customer inventory mapping
     * @param genericDataDTO the generic data dto
     * @param hasmac the hasmac
     * @param hasserial the hasserial
     * @param subisuCustInventoryMappingId the subisu cust inventory mapping id
     * @param subisuCustInvenMapping the subisu cust inven mapping
     * @param item the item
     * @return the customer inventory mapping dto
     * @throws Exception the exception
     */
    private CustomerInventoryMappingDto approveRequestByOtherUser(CustomerInventoryMappingDto entity, Long customerInventoryMappingId, boolean isApproveRequest, CustomerInventoryMapping customerInventoryMapping, GenericDataDTO genericDataDTO, boolean hasmac, boolean hasserial, Long subisuCustInventoryMappingId, CustomerInventoryMapping subisuCustInvenMapping, Item item) throws Exception {
        try {
            entity.setStatus("ACTIVE");
            entity = updateItemChanges(customerInventoryMappingId, isApproveRequest, customerInventoryMapping.getCreatedById(), customerInventoryMapping.getLastModifiedById());
            entity.setFlag("approved");
            entity.setNextApproverId(null);
            entity.setTeamHierarchyMappingId(null);
            entity.setPreviousApproveId(null);
            genericDataDTO.setData(super.saveEntity(entity));
            if (hasmac || hasserial) {
                if (customerInventoryMapping != null) {
                    Long itemId = customerInventoryMapping.getItemId();
                    itemService.changeItemWarrantyStatus(customerInventoryMappingId, itemId, customerInventoryMapping.getAssignedDateTime());
                }
            }
            CustomerInventoryMappingDto subisuCustInvenDto = new CustomerInventoryMappingDto();
            if (subisuCustInventoryMappingId != null && subisuCustInvenMapping != null) {
                subisuCustInvenDto.setStatus("ACTIVE");
                subisuCustInvenDto = updateItemChanges(subisuCustInventoryMappingId, isApproveRequest, subisuCustInvenMapping.getCreatedById(), subisuCustInvenMapping.getLastModifiedById());
                subisuCustInvenDto.setFlag("approved");
                subisuCustInvenDto.setNextApproverId(null);
                subisuCustInvenDto.setPreviousApproveId(null);
                subisuCustInvenDto.setTeamHierarchyMappingId(null);
                genericDataDTO.setData(super.saveEntity(subisuCustInvenDto));
            }
            if (item != null) {
                ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
                //                            messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
            }
            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
//                        if (customerInventoryMapping.getPlanId() != null || customerInventoryMapping.getPlanGroupId() != null) {
            /**
             * Send Approve Inventory From Inventory to CMS
             */
            InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
            inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
            inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
            if (item != null) {
                inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
            }
            inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
            inventorySerialNumberMessage.setOperation(CommonConstants.ASSIGN_INVETORIES);
            inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
            inventorySerialNumberMessage.setCustInventoryId(customerInventoryMappingId);
            inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
            inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
            if (item != null) {
                inventorySerialNumberMessage.setItemId(item.getId());
                inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                inventorySerialNumberMessage.setItemName(item.getName());
            }
            inventorySerialNumberMessage.setStatus("ACTIVE");
            inventorySerialNumberMessage.setQty(customerInventoryMapping.getQty());
            inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
            inventorySerialNumberMessage.setVendorId(getvendorIdAgainstCustomerInventory(Long.valueOf(customerInventoryMapping.getProduct().getId())));
            kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
//                        messageSender.send(inventorySerialNumberMessage, RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS);
//                        }
            if (customerInventoryMapping.getExternalItemId() == null) {
                CustomerInventoryRevenueMessage customerInventoryRevenueMessage = new CustomerInventoryRevenueMessage(customerInventoryMapping);
                //                            messageSender.send(customerInventoryRevenueMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE);
                kafkaMessageSender.send(new KafkaMessageData(customerInventoryRevenueMessage, CustomerInventoryRevenueMessage.class.getSimpleName()));
            }
            if (subisuCustInvenMapping != null && subisuCustInvenMapping.getIsInvoiceToOrg().equals(true)) {
                CustomerInventoryRevenueMessage customerInventoryRevenueMessage = new CustomerInventoryRevenueMessage(subisuCustInvenMapping);
                //                            messageSender.send(customerInventoryRevenueMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE);
                kafkaMessageSender.send(new KafkaMessageData(customerInventoryRevenueMessage, CustomerInventoryRevenueMessage.class.getSimpleName()));
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Reject request with team hierarchy null customer inventory mapping dto.
     * @param entity the entity
     * @param customerInventoryMappingId the customer inventory mapping id
     * @param isApproveRequest the is approve request
     * @param customerInventoryMapping the customer inventory mapping
     * @param item the item
     * @param loggedInUser the logged in user
     * @param remark the remark
     * @return the customer inventory mapping dto
     * @throws Exception the exception
     */
    private CustomerInventoryMappingDto rejectRequestWithTeamHierarchyNull(CustomerInventoryMappingDto entity, Long customerInventoryMappingId, boolean isApproveRequest, CustomerInventoryMapping customerInventoryMapping, Item item, StaffUser loggedInUser, String remark) throws Exception {
        try {
            hierarchyService.rejectDirectFromCreatedStaff(CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, entity.getId().intValue());
            entity.setStatus("REJECTED");
            entity = updateItemChanges(customerInventoryMappingId, isApproveRequest, customerInventoryMapping.getCreatedById(), customerInventoryMapping.getLastModifiedById());
            entity.setFlag("rejected");
            entity.setNextApproverId(null);
            entity.setTeamHierarchyMappingId(null);
            entity.setPreviousApproveId(null);
            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remark + "\n" + entity.getFlag() + " By :- " + loggedInUser.getUsername());
            /**
             * Send Rejected Inventory From Inventory to CMS
             */
            InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
            inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
            inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
            if (item != null) {
                inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
            }
            inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
            inventorySerialNumberMessage.setOperation(CommonConstants.ASSIGN_INVETORIES);
            inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
            inventorySerialNumberMessage.setCustInventoryId(customerInventoryMappingId);
            inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
            inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
            if (item != null) {
                inventorySerialNumberMessage.setItemId(item.getId());
                inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                inventorySerialNumberMessage.setItemName(item.getName());
            }
            inventorySerialNumberMessage.setStatus("REJECTED");
            inventorySerialNumberMessage.setQty(0L);
            inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
            inventorySerialNumberMessage.setVendorId(getvendorIdAgainstCustomerInventory(Long.valueOf(customerInventoryMapping.getProduct().getId())));
//                messageSender.send(inventorySerialNumberMessage, RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Auto reject request customer inventory mapping dto.
     * @param entity the entity
     * @param customerInventoryMappingId the customer inventory mapping id
     * @param isApproveRequest the is approve request
     * @param customerInventoryMapping the customer inventory mapping
     * @param genericDataDTO the generic data dto
     * @param subisuCustInventoryMappingId the subisu cust inventory mapping id
     * @param subisuCustInvenMapping the subisu cust inven mapping
     * @param item the item
     * @return the customer inventory mapping dto
     * @throws Exception the exception
     */
    private CustomerInventoryMappingDto autoRejectRequest(CustomerInventoryMappingDto entity, Long customerInventoryMappingId, boolean isApproveRequest, CustomerInventoryMapping customerInventoryMapping, GenericDataDTO genericDataDTO, Long subisuCustInventoryMappingId, CustomerInventoryMapping subisuCustInvenMapping, Item item) throws Exception {
        try {
            entity.setStatus("REJECTED");
            entity = updateItemChanges(customerInventoryMappingId, isApproveRequest, customerInventoryMapping.getCreatedById(), customerInventoryMapping.getLastModifiedById());
            entity.setFlag("rejected");
            entity.setNextApproverId(null);
            entity.setTeamHierarchyMappingId(null);
            entity.setPreviousApproveId(null);
            genericDataDTO.setData(super.saveEntity(entity));
            CustomerInventoryMappingDto subisuCustInvenDto = new CustomerInventoryMappingDto();
            if (subisuCustInventoryMappingId != null && subisuCustInvenMapping != null) {
                subisuCustInvenDto.setStatus("REJECTED");
                subisuCustInvenDto = updateItemChanges(subisuCustInventoryMappingId, isApproveRequest, subisuCustInvenMapping.getCreatedById(), subisuCustInvenMapping.getLastModifiedById());
                subisuCustInvenDto.setFlag("rejected");
                subisuCustInvenDto.setNextApproverId(null);
                subisuCustInvenDto.setPreviousApproveId(null);
                subisuCustInvenDto.setTeamHierarchyMappingId(null);
                genericDataDTO.setData(super.saveEntity(subisuCustInvenDto));
            }
            /**
             * Send Rejected Inventory From Inventory to CMS
             */
            InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
            inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
            inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
            if (item != null) {
                inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
            }
            inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
            inventorySerialNumberMessage.setOperation(CommonConstants.ASSIGN_INVETORIES);
            inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
            inventorySerialNumberMessage.setCustInventoryId(customerInventoryMappingId);
            inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
            inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
            if (item != null) {
                inventorySerialNumberMessage.setItemId(item.getId());
                inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                inventorySerialNumberMessage.setItemName(item.getName());
            }
            inventorySerialNumberMessage.setStatus("REJECTED");
            inventorySerialNumberMessage.setQty(customerInventoryMapping.getQty());
            inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
            inventorySerialNumberMessage.setVendorId(getvendorIdAgainstCustomerInventory(Long.valueOf(customerInventoryMapping.getProduct().getId())));
            kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
//                    messageSender.send(inventorySerialNumberMessage, RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS);
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Auto approve request customer inventory mapping dto.
     * @param entity the entity
     * @param customerInventoryMappingId the customer inventory mapping id
     * @param isApproveRequest the is approve request
     * @param customerInventoryMapping the customer inventory mapping
     * @param genericDataDTO the generic data dto
     * @param hasmac the hasmac
     * @param hasserial the hasserial
     * @param subisuCustInventoryMappingId the subisu cust inventory mapping id
     * @param subisuCustInvenMapping the subisu cust inven mapping
     * @param item the item
     * @return the customer inventory mapping dto
     * @throws Exception the exception
     */
    private CustomerInventoryMappingDto autoApproveRequest(CustomerInventoryMappingDto entity, Long customerInventoryMappingId, boolean isApproveRequest, CustomerInventoryMapping customerInventoryMapping, GenericDataDTO genericDataDTO, boolean hasmac, boolean hasserial, Long subisuCustInventoryMappingId, CustomerInventoryMapping subisuCustInvenMapping, Item item) throws Exception {
        try {
            entity.setStatus("ACTIVE");
            entity = updateItemChanges(customerInventoryMappingId, isApproveRequest, customerInventoryMapping.getCreatedById(), customerInventoryMapping.getLastModifiedById());
            entity.setFlag("approved");
            entity.setNextApproverId(null);
            entity.setTeamHierarchyMappingId(null);
            entity.setPreviousApproveId(null);
            genericDataDTO.setData(super.saveEntity(entity));
            if (hasmac || hasserial) {
                if (customerInventoryMapping != null) {
                    Long itemId = customerInventoryMapping.getItemId();
                    itemService.changeItemWarrantyStatus(customerInventoryMappingId, itemId, customerInventoryMapping.getAssignedDateTime());
                }
            }
            CustomerInventoryMappingDto subisuCustInvenDto = new CustomerInventoryMappingDto();
            if (subisuCustInventoryMappingId != null && subisuCustInvenMapping != null) {
                subisuCustInvenDto.setStatus("ACTIVE");
                subisuCustInvenDto = updateItemChanges(subisuCustInventoryMappingId, isApproveRequest, subisuCustInvenMapping.getCreatedById(), subisuCustInvenMapping.getLastModifiedById());
                subisuCustInvenDto.setFlag("approved");
                subisuCustInvenDto.setNextApproverId(null);
                subisuCustInvenDto.setPreviousApproveId(null);
                subisuCustInvenDto.setTeamHierarchyMappingId(null);
                genericDataDTO.setData(super.saveEntity(subisuCustInvenDto));
            }
            if (item != null) {
                ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
                //                        messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
            }
            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
//                    messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
//                    if (customerInventoryMapping.getPlanId() != null || customerInventoryMapping.getPlanGroupId() != null) {
            /**
             * Send Approve Inventory From Inventory to CMS
             */
            InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
            inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
            inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
            if (item != null) {
                inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
            }
            inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
            inventorySerialNumberMessage.setOperation(CommonConstants.ASSIGN_INVETORIES);
            inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
            inventorySerialNumberMessage.setCustInventoryId(customerInventoryMappingId);
            inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
            inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
            if (item != null) {
                inventorySerialNumberMessage.setItemId(item.getId());
                inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                inventorySerialNumberMessage.setItemName(item.getName());
            }
            inventorySerialNumberMessage.setStatus("ACTIVE");
            inventorySerialNumberMessage.setQty(customerInventoryMapping.getQty());
            inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
            inventorySerialNumberMessage.setVendorId(getvendorIdAgainstCustomerInventory(Long.valueOf(customerInventoryMapping.getProduct().getId())));
            kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
//                    messageSender.send(inventorySerialNumberMessage, RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS);
//                    }
            if (customerInventoryMapping.getExternalItemId() == null) {
                CustomerInventoryRevenueMessage customerInventoryRevenueMessage = new CustomerInventoryRevenueMessage(customerInventoryMapping);
                //                        messageSender.send(customerInventoryRevenueMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE);
                kafkaMessageSender.send(new KafkaMessageData(customerInventoryRevenueMessage, CustomerInventoryRevenueMessage.class.getSimpleName()));
            }
            if (subisuCustInvenMapping != null && subisuCustInvenMapping.getIsInvoiceToOrg().equals(true)) {
                CustomerInventoryRevenueMessage customerInventoryRevenueMessage = new CustomerInventoryRevenueMessage(subisuCustInvenMapping);
                //                        messageSender.send(customerInventoryRevenueMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE);
                kafkaMessageSender.send(new KafkaMessageData(customerInventoryRevenueMessage, CustomerInventoryRevenueMessage.class.getSimpleName()));
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Reject by admin or superadmin customer inventory mapping dto.
     * @param entity the entity
     * @param customerInventoryMappingId the customer inventory mapping id
     * @param isApproveRequest the is approve request
     * @param customerInventoryMapping the customer inventory mapping
     * @param genericDataDTO the generic data dto
     * @param subisuCustInventoryMappingId the subisu cust inventory mapping id
     * @param subisuCustInvenMapping the subisu cust inven mapping
     * @param item the item
     * @return the customer inventory mapping dto
     * @throws Exception the exception
     */
    private CustomerInventoryMappingDto rejectByAdminOrSuperadmin(CustomerInventoryMappingDto entity,
                                                                  Long customerInventoryMappingId,
                                                                  boolean isApproveRequest,
                                                                  CustomerInventoryMapping customerInventoryMapping,
                                                                  GenericDataDTO genericDataDTO, Long subisuCustInventoryMappingId,
                                                                  CustomerInventoryMapping subisuCustInvenMapping,
                                                                  Item item) throws Exception {
        try {
            entity.setStatus("REJECTED");
            entity = updateItemChanges(customerInventoryMappingId, isApproveRequest, customerInventoryMapping.getCreatedById(), customerInventoryMapping.getLastModifiedById());
            entity.setFlag("rejected");
            entity.setNextApproverId(null);
            entity.setPreviousApproveId(null);
            entity.setTeamHierarchyMappingId(null);
            genericDataDTO.setData(super.saveEntity(entity));
            CustomerInventoryMappingDto subisuCustInvenDto = new CustomerInventoryMappingDto();
            if (subisuCustInventoryMappingId != null && subisuCustInvenMapping != null) {
                subisuCustInvenDto.setStatus("REJECTED");
                subisuCustInvenDto = updateItemChanges(subisuCustInventoryMappingId, isApproveRequest, subisuCustInvenMapping.getCreatedById(), subisuCustInvenMapping.getLastModifiedById());
                subisuCustInvenDto.setFlag("rejected");
                subisuCustInvenDto.setNextApproverId(null);
                subisuCustInvenDto.setPreviousApproveId(null);
                subisuCustInvenDto.setTeamHierarchyMappingId(null);
                genericDataDTO.setData(super.saveEntity(subisuCustInvenDto));
            }
            /**
             * Send Rejected Inventory From Inventory to CMS
             */
            InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
            inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
            inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
            if (item != null) {
                inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
            }
            inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
            inventorySerialNumberMessage.setOperation(CommonConstants.ASSIGN_INVETORIES);
            inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
            inventorySerialNumberMessage.setCustInventoryId(customerInventoryMappingId);
            inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
            inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
            if (item != null) {
                inventorySerialNumberMessage.setItemId(item.getId());
                inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                inventorySerialNumberMessage.setItemName(item.getName());
            }
            inventorySerialNumberMessage.setStatus("REJECTED");
            inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
            inventorySerialNumberMessage.setQty(customerInventoryMapping.getQty());
            inventorySerialNumberMessage.setVendorId(getvendorIdAgainstCustomerInventory(Long.valueOf(customerInventoryMapping.getProduct().getId())));
//                messageSender.send(inventorySerialNumberMessage, RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Approve by admin or superadmin customer inventory mapping dto.
     * @param entity the entity
     * @param customerInventoryMappingId the customer inventory mapping id
     * @param isApproveRequest the is approve request
     * @param customerInventoryMapping the customer inventory mapping
     * @param genericDataDTO the generic data dto
     * @param hasmac the hasmac
     * @param hasserial the hasserial
     * @param subisuCustInventoryMappingId the subisu cust inventory mapping id
     * @param subisuCustInvenMapping the subisu cust inven mapping
     * @param item the item
     * @return the customer inventory mapping dto
     * @throws Exception the exception
     */
    private CustomerInventoryMappingDto approveByAdminOrSuperadmin(CustomerInventoryMappingDto entity,
                                                                   Long customerInventoryMappingId,
                                                                   boolean isApproveRequest,
                                                                   CustomerInventoryMapping customerInventoryMapping,
                                                                   GenericDataDTO genericDataDTO, boolean hasmac,
                                                                   boolean hasserial, Long subisuCustInventoryMappingId,
                                                                   CustomerInventoryMapping subisuCustInvenMapping,
                                                                   Item item) throws Exception {
        try {
            entity.setStatus("ACTIVE");
            entity = updateItemChanges(customerInventoryMappingId, isApproveRequest, customerInventoryMapping.getCreatedById(), customerInventoryMapping.getLastModifiedById());
            entity.setFlag("approved");
            entity.setNextApproverId(null);
            entity.setPreviousApproveId(null);
            entity.setTeamHierarchyMappingId(null);
            genericDataDTO.setData(super.saveEntity(entity));
            if (hasmac || hasserial) {
                if (customerInventoryMapping != null) {
                    Long itemId = customerInventoryMapping.getItemId();
                    itemService.changeItemWarrantyStatus(customerInventoryMappingId, itemId, customerInventoryMapping.getAssignedDateTime());
                }
            }
            CustomerInventoryMappingDto subisuCustInvenDto = new CustomerInventoryMappingDto();
            if (subisuCustInventoryMappingId != null && subisuCustInvenMapping != null) {
                subisuCustInvenDto.setStatus("ACTIVE");
                subisuCustInvenDto = updateItemChanges(subisuCustInventoryMappingId, isApproveRequest, subisuCustInvenMapping.getCreatedById(), subisuCustInvenMapping.getLastModifiedById());
                subisuCustInvenDto.setFlag("approved");
                subisuCustInvenDto.setNextApproverId(null);
                subisuCustInvenDto.setPreviousApproveId(null);
                subisuCustInvenDto.setTeamHierarchyMappingId(null);
                genericDataDTO.setData(super.saveEntity(subisuCustInvenDto));
            }
            if (item != null) {
                ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
                //                    messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
            }
            //Todo: Code for Approve Serialized Item for Integration
//                messageSender.send(itemMessage, RabbitMqConstants.QUEUE_APIGW_APPROVE_SERIALIZEDITEM_FOR_INTEGRATION);
            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
//                if (customerInventoryMapping.getPlanId() != null || customerInventoryMapping.getPlanGroupId() != null) {
            /**
             * Send Approve Inventory From Inventory to CMS
             */
            InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
            inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
            inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
            if (item != null) {
                inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
            }
            inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
            inventorySerialNumberMessage.setOperation(CommonConstants.ASSIGN_INVETORIES);
            inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
            inventorySerialNumberMessage.setCustInventoryId(customerInventoryMappingId);
            inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
            inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
            if (item != null) {
                inventorySerialNumberMessage.setItemId(item.getId());
                inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                inventorySerialNumberMessage.setItemName(item.getName());
            }
            inventorySerialNumberMessage.setStatus("ACTIVE");
            inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
            inventorySerialNumberMessage.setQty(customerInventoryMapping.getQty());
            inventorySerialNumberMessage.setVendorId(getvendorIdAgainstCustomerInventory(Long.valueOf(customerInventoryMapping.getProduct().getId())));
            kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
//                messageSender.send(inventorySerialNumberMessage, RabbitMqConstants.QUEUE_SEND_ITEM_SERIAL_NUMBER_INVENTORY_TO_CMS);
//                }
            if (customerInventoryMapping.getExternalItemId() == null) {
                CustomerInventoryRevenueMessage customerInventoryRevenueMessage = new CustomerInventoryRevenueMessage(customerInventoryMapping);
                //                    messageSender.send(customerInventoryRevenueMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE);
                kafkaMessageSender.send(new KafkaMessageData(customerInventoryRevenueMessage, CustomerInventoryRevenueMessage.class.getSimpleName()));
            }
            if (subisuCustInvenMapping != null && subisuCustInvenMapping.getIsInvoiceToOrg().equals(true)) {
                CustomerInventoryRevenueMessage customerInventoryRevenueMessage = new CustomerInventoryRevenueMessage(subisuCustInvenMapping);
                //                    messageSender.send(customerInventoryRevenueMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_CUSTOMER_INVENTORY_TO_REVENUE);
                kafkaMessageSender.send(new KafkaMessageData(customerInventoryRevenueMessage, CustomerInventoryRevenueMessage.class.getSimpleName()));
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets repository.
     * @return the repository
     */
    @Override
    public CustomerInventoryMappingRepo getRepository() {
        return repository;
    }


    /**
     * Gets customer inventory mapping by staff id.
     * @param pageNumber the page number
     * @param customPageSize the custom page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @param filterList the filter list
     * @param staffId the staff id
     * @param isGetSerializedItem the is get serialized item
     * @return the customer inventory mapping by staff id
     */
    public GenericDataDTO getCustomerInventoryMappingByStaffId(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long staffId, boolean isGetSerializedItem) {
        String SUBMODULE = getModuleNameForLog() + " [getCustomerInventoryMappingByStaffId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
            PageRequest pageRequest;
            Page<CustomerInventoryMapping> customerInventoryMappingPage = null;
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            //get all serialized item of  customer inventory mapping details by staff Id
            if (isGetSerializedItem) {
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomerInventoryMapping.qty.gt(0)).and(qCustomerInventoryMapping.inOutWardMACMapping.isNotEmpty());
                if (getMvnoIdFromCurrentStaff() != 1) {
                    booleanExpression = booleanExpression.and(qCustomerInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                }
                customerInventoryMappingPage = repository.findAll(booleanExpression, pageRequest);

                if (customerInventoryMappingPage.getSize() != 0) {
                    customerInventoryMappingPage.stream().forEach(r -> {
                        r.setCustomerFirstName(r.getCustomer().getFirstname());
                        r.setCustomerLastName(r.getCustomer().getLastname());
                        r.setServiceAreaName(r.getCustomer().getServicearea().getName());
                        Item item = itemRepository.findById(r.getItemId()).orElse(null);
                        if (item != null) {
                            r.setItemwarranty(item.getWarranty());
                            r.setExpDate(item.getExpireDate());
                        }
                    });
                }
                if (customerInventoryMappingPage.getSize() > 0) {
                    genericDataDTO = customerInventoryMappingService.makeGenericDTOResponse(genericDataDTO, customerInventoryMappingPage);
                }
            }
            //get all nonserialized item(wires, etc) of  customer inventory mapping details by staff Id
            if (!isGetSerializedItem) {
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomerInventoryMapping.qty.gt(0)).and(qCustomerInventoryMapping.inOutWardMACMapping.isEmpty());
                if (getMvnoIdFromCurrentStaff() != 1) {
                    booleanExpression = booleanExpression.and(qCustomerInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                }
                customerInventoryMappingPage = repository.findAll(booleanExpression, pageRequest);

                if (customerInventoryMappingPage.getSize() != 0) {
                    customerInventoryMappingPage.stream().forEach(r -> {
                        r.setCustomerFirstName(r.getCustomer().getFirstname());
                        r.setCustomerLastName(r.getCustomer().getLastname());
                        r.setServiceAreaName(r.getCustomer().getServicearea().getName());
                    });
                }
                if (customerInventoryMappingPage.getSize() > 0) {
                    genericDataDTO = customerInventoryMappingService.makeGenericDTOResponse(genericDataDTO, customerInventoryMappingPage);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    /**
     * Make generic dto response generic data dto.
     * @param genericDataDTO the generic data dto
     * @param paginationList the pagination list
     * @return the generic data dto
     */
    public GenericDataDTO makeGenericDTOResponse(GenericDataDTO genericDataDTO, Page<CustomerInventoryMapping> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    public void validatePlanInventory(Long productPlanMappingId, Integer customerId, Long planId) {
        /** Code for varification of product planning product quantity and customer inventory mapping */
        Integer productQuantity = productPlanMappingRepository.findProductQuantityById(productPlanMappingId);
        int inventoryCount = customerInventoryMappingRepo.countActiveOrPendingInventory(customerId, planId);
        if (inventoryCount >= productQuantity) {
            throw new RuntimeException("The plan is having " + productQuantity + " Inventory. We cannot bind more Inventory.");
        }
    }

    public void validate(CustomerInventoryMappingDto inventoryMappingDto) throws Exception {
        try {
            if (inventoryMappingDto.isHasMac()) {
                customerInventoryMappingService.validateMac(inventoryMappingDto);
            }
            if (inventoryMappingDto.isHasSerial()) {
                customerInventoryMappingService.validateSerialNumber(inventoryMappingDto);
            }
            if (inventoryMappingDto.getProductPlanMappingId() != null) {
                customerInventoryMappingService.validatePlanInventory(inventoryMappingDto.getProductPlanMappingId(), inventoryMappingDto.getCustomerId(), inventoryMappingDto.getPlanId());
            }
            Customers customers = customersRepository.findAllLightCustomerById(inventoryMappingDto.getCustomerId());
            if (inventoryMappingDto.isHasCas() && customers != null && customers.getStatus().equals(CommonConstants.CUSTOMER_STATUS_ACTIVE)) {
                customerInventoryMappingService.validateConnectionNumber(inventoryMappingDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Save entity list list.
     * @param entity the entity
     * @return the list
     * @throws Exception the exception
     */
   @Transactional
    public List<CustomerInventoryMappingDto> saveEntityList(CustomerInventoryMappingDto entity) throws Exception {
        List<CustomerInventoryMappingDto> customerInventoryMappingDtoList = new ArrayList<>();
        List<CustomerInventoryMappingDto> finalCustomerInventoryMappingDtoList = new ArrayList<>();
        Double enteredNewAmount = null;
        if (entity.getNewAmount() != null) {
            enteredNewAmount = entity.getNewAmount();
        }
        try {
            /** Set Connection number in entity if connection number empty in entity */
            String connectionNo = entity.getConnectionNo();
            if (entity.getConnectionNo().isEmpty()) {
                connectionNo = customerServiceMappingRepository.findConnectionNoByServiceIdAndCustId(entity.getServiceId(), entity.getCustomerId());
                entity.setConnectionNo(connectionNo);
            }
            /** Set mac address and serial number in entity if null */
            List<InOutWardMACMapping> mappings = new ArrayList<>();
            for (InOutWardMACMapping mapping : entity.getInOutWardMACMapping()) {
                /** Update MAC address if not null */
                if (mapping.getMacAddress() != null && entity.isHasMac()) {
                    mapping.setMacAddress(itemRepository.findMacByItemId(mapping.getItemId()));
                }
                /** Update Serial Number if not null */
                if (mapping.getSerialNumber() != null && entity.isHasSerial()) {
                    mapping.setSerialNumber(itemRepository.findSerialNumberByItemId(mapping.getItemId()));
                }
                mappings.add(mapping);
            }
            if (!mappings.isEmpty()) {
                entity.setInOutWardMACMapping(mappings);
            }

            String inventoryJobType = entity.getInventoryJobType();
            String nature = entity.getNature();

            entity.setInventoryJobType(inventoryJobType);
            entity.setNature(nature);

            /** Get Item Condition By Item Id*/
            String itemCondition = itemRepository.findItemConditionByItemId(entity.getItemId());
            /** Get Product details by product id */
            Product product = productRepository.findById(entity.getProductId()).get();
            /** Get Customer Details by customer id */
            Customers customers = customersRepository.findById(entity.getCustomerId()).get();
            Integer newChargeTexId;
            Integer refChargeTaxId;
            /** Set New Charge Tax Id By New Product Charge Id */
            if (product != null && product.getNewProductCharge() != null && product.getNewProductCharge().getId() != null) {
                newChargeTexId = chargeRepository.findTaxIdByChargeId(product.getNewProductCharge().getId());
            } else {
                newChargeTexId = null;
            }
            /** Set Ref Charge Tax Id By Ref Product Charge Id */
            if (product != null && product.getRefurburshiedProductCharge() != null && product.getRefurburshiedProductCharge().getId() != null) {
                refChargeTaxId = chargeRepository.findTaxIdByChargeId(product.getRefurburshiedProductCharge().getId());
            } else {
                refChargeTaxId = null;
            }
            /** Set new and ref chare in Single Item */
            /**OfferPrice will be set here **/
//            if (!entity.isItemAssemblyflag()) {
//                if (entity.productId != null) {
//                    if (entity.getNewAmount() != null) {
//                        if (itemCondition != null && itemCondition.equalsIgnoreCase("New") && newChargeTexId != null) {
//                            entity.setNewAmount(getPriceWithoutTax(Math.toIntExact(newChargeTexId), entity.getNewAmount()));
//                        } else if (itemCondition != null && itemCondition.equalsIgnoreCase("Refurbished") && refChargeTaxId != null) {
//                            entity.setNewAmount(getPriceWithoutTax(Math.toIntExact(refChargeTaxId), entity.getNewAmount()));
//                        }
//                    }
//                }
//            }
            /** Set new and ref charge in assembly item */
            if (entity.isItemAssemblyflag()) {
                customerInventoryMappingDtoList.addAll(getAssemblyCustInvenMapDto(entity, product, newChargeTexId, refChargeTaxId));
            } else {
                customerInventoryMappingDtoList.addAll(getSingleCustInvenMapDto(entity, product));
            }
            /** Code fpr Bill to Organization and Bill to Customer */
            if (entity.getBillTo().equalsIgnoreCase(CommonConstants.CUSTOMER_INVENTORY_BILL_TO.SUBISU) ||
                    entity.getBillTo().equalsIgnoreCase(CommonConstants.CUSTOMER_INVENTORY_BILL_TO.ORGANIZATION)) {
                finalCustomerInventoryMappingDtoList.addAll(handleBillToOrganization(customerInventoryMappingDtoList, entity, customers,
                        enteredNewAmount, itemCondition, newChargeTexId, product, refChargeTaxId));
            } else if (entity.getBillTo().equalsIgnoreCase("CUSTOMER")) {
                for (CustomerInventoryMappingDto customerInventoryMappingDto : customerInventoryMappingDtoList) {
                    finalCustomerInventoryMappingDtoList.add(customerInventoryMappingDto);
                }
            }
            /** Final Method for Save Entities */
            for (CustomerInventoryMappingDto customerInventoryMappingDto : finalCustomerInventoryMappingDtoList) {
                /** Save Enternal Customer Inventory */
                if (entity.getExternalItemId() != null) {
                    customerInventoryMappingDto = saveExternalItem(customerInventoryMappingDto);
                }
                /** Set Assign To Customeer Workflow */
                customerInventoryMappingDto = setAssignToCustWorkFlow(customerInventoryMappingDto, customers);
                /** Set Item Assembly name in Customer Inventory Mapping*/
                if (entity.isItemAssemblyflag()) {
                    customerInventoryMappingDto.setItemAssemblyName(entity.getItemAssemblyName());
                } else {
                    String productName = productRepository.findProductNameByProductId(customerInventoryMappingDto.getProductId());
                    customerInventoryMappingDto.setItemAssemblyName(productName);
                }
                /** Code for Set Expiry Date Time Based on Expiry Time Unit */
                Long pcId = productRepository.findProductCategoryIdByProductId(customerInventoryMappingDto.getProductId());
                boolean hasMac = productCategoryRepository.findHasMacById(pcId);
                boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
                if (hasMac || hasSerial) {
                    String expiryTimeUnit = productRepository.findExpiryTimeUnitByProductId(customerInventoryMappingDto.getProductId());
                    Integer expiryTime = productRepository.findExpiryTimeByProductId(customerInventoryMappingDto.getProductId());
                    if (expiryTimeUnit != null && expiryTime != null) {
                        switch (expiryTimeUnit) {
                            case "Day":
                                customerInventoryMappingDto.setExpiryDateTime(LocalDateTime.now().plusDays(expiryTime));
                                break;
                            case "Month":
                                customerInventoryMappingDto.setExpiryDateTime(LocalDateTime.now().plusMonths(expiryTime));
                                break;
                        }
                    }
                }
                /** Update Inward by Customer Inventory Mapping */
                if (entity.getInwardId() != null) {
                    customerInventoryMappingDto = updateInwardByEntity(customerInventoryMappingDto);
                }
                /** Update External Item by Customer Inventory Mapping */
                if (entity.getExternalItemId() != null) {
                    customerInventoryMappingDto = updateExternalItemByEntity(customerInventoryMappingDto);
                }
                if (customers != null) {
                    if (customers.getUsername().equalsIgnoreCase("SUBISUPRE") ||
                            customers.getUsername().equalsIgnoreCase("SUBISUPOS") ||
                            customers.getUsername().equalsIgnoreCase("ORGANIZATIONPRE") ||
                            customers.getUsername().equalsIgnoreCase("ORGANIZATIONPOS")) {
                        CustomerInventoryMapping domain = mapper.dtoToDomain(customerInventoryMappingDto, new CycleAvoidingMappingContext());
                        domain.setCustomer(customers);
                        mapper.domainToDTO(customerInventoryMappingRepo.save(domain), new CycleAvoidingMappingContext());
                    } else {
                        super.saveEntity(customerInventoryMappingDto);
                    }
                }
            }
            return customerInventoryMappingDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        }

    }

    private CustomerInventoryMappingDto updateExternalItemByEntity(CustomerInventoryMappingDto customerInventoryMappingDto) throws Exception {
        try {
            ExternalItemManagementDTO externalItemManagementDTO = externalItemManagementService.getEntityForUpdateAndDelete(customerInventoryMappingDto.getExternalItemId());
            if (externalItemManagementDTO.getUnusedQty() <= 0 && externalItemManagementDTO.getUsedQty() <= 0) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), " ** qty -ve **", null);
            } else {
                List<InOutWardMACMapping> list = customerInventoryMappingDto.getInOutWardMACMapping();
                List<Long> ids = list.stream().map(InOutWardMACMapping::getItemId).collect(Collectors.toList());
                List<InOutWardMACMapping> mapMappingLists = IterableUtils.toList(inOutWardMacRepo.findAllByItemIdIn(ids));

                List<InOutWardMACMapping> inOutWardMACMappings = new ArrayList<>();
                for (int i = 0; i < customerInventoryMappingDto.getInOutWardMACMapping().size(); i++) {
                    if (Objects.equals(customerInventoryMappingDto.getInOutWardMACMapping().get(i).getMacAddress(), mapMappingLists.get(i).getMacAddress())) {
                        inOutWardMACMappings.add(mapMappingLists.get(i));
                    }
                }
                for (InOutWardMACMapping mapping : inOutWardMACMappings) {
                    long count = Duration.between(LocalDateTime.now(), customerInventoryMappingDto.assignedDateTime).toDays();
                    mapping.setUsedCount((int) count);
                }
                customerInventoryMappingDto.setExternalItemNumber(externalItemManagementDTO.getExternalItemGroupNumber());
                customerInventoryMappingDto.setInOutWardMACMapping(inOutWardMACMappings);
                customerInventoryMappingDto = super.saveEntity(customerInventoryMappingDto);
                externalItemManagementDTO.setUnusedQty(externalItemManagementDTO.getUnusedQty() - customerInventoryMappingDto.getQty());
                externalItemManagementDTO.setUsedQty(externalItemManagementDTO.getUsedQty() + customerInventoryMappingDto.getQty());
                externalItemManagementService.updateEntity(externalItemManagementDTO);
            }
            return customerInventoryMappingDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private CustomerInventoryMappingDto updateInwardByEntity(CustomerInventoryMappingDto customerInventoryMappingDto) throws Exception {
        try {
            InwardDto inwardDto = inwardMapper.domainToDTO(inwardRepository.findById(customerInventoryMappingDto.getInwardId()).orElse(null), new CycleAvoidingMappingContext());
            if (inwardDto.getUnusedQty() <= 0 && inwardDto.getUsedQty() <= 0) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), " ** qty -ve **", null);
            } else {
                List<InOutWardMACMapping> inOutWardMACMappings = customerInventoryMappingDto.getInOutWardMACMapping();
                List<Long> ids = inOutWardMACMappings.stream().map(InOutWardMACMapping::getItemId).collect(Collectors.toList());
                List<Item> itemList = itemRepository.findAllById(ids);
                for (InOutWardMACMapping mapping : inOutWardMACMappings) {
                    long count = Duration.between(LocalDateTime.now(), customerInventoryMappingDto.assignedDateTime).toDays();
                    mapping.setUsedCount((int) count);
                }
                for (int i = 0; i < inOutWardMACMappings.size(); i++) {
                    customerInventoryMappingDto.setItemId(inOutWardMACMappings.get(i).getItemId());
                }
                customerInventoryMappingDto.setInwardNumber(inwardDto.getInwardNumber());
                customerInventoryMappingDto.setInOutWardMACMapping(inOutWardMACMappings);
                customerInventoryMappingDto = super.saveEntity(customerInventoryMappingDto);
                inwardDto.setUnusedQty(inwardDto.getUnusedQty() - customerInventoryMappingDto.getQty());
                inwardDto.setUsedQty(inwardDto.getUsedQty() + customerInventoryMappingDto.getQty());
                inwardService.updateEntity(inwardDto);
            }
            return customerInventoryMappingDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private CustomerInventoryMappingDto setAssignToCustWorkFlow(CustomerInventoryMappingDto customerInventoryMappingDto, Customers customers) {
        try {
            if (Objects.equals(getLoggedInUser().getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {
                customerInventoryMappingDto.setNextApproverId(getLoggedInUserId());
                customerInventoryMappingDto.setTeamHierarchyMappingId(null);
                customerInventoryMappingDto.setStatus("PENDING");
            } else {
                Map<String, String> map = null;
                StaffUser assignedUser = null;
                if (clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
                    map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().dtoToDomain(customerInventoryMappingDto, new CycleAvoidingMappingContext()));
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).orElse(null);
                        //                            assignedUser = staffUser;
                        customerInventoryMappingDto.setNextApproverId(Integer.valueOf(map.get("staffId")));
                        customerInventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                        customerInventoryMappingDto.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        customerInventoryMappingDto.setStatus("PENDING");
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.INVENTORY + " with product name : " + " ' " + customerInventoryMappingDto.getProductName() + " ' " + "and " + "quantity : " + " ' " + customerInventoryMappingDto.getQty() + " '";
                        //                            hierarchyService.sendWorkflowAssignActionMessage(assignedUser.getCountryCode(), assignedUser.getPhone(), assignedUser.getEmail(), assignedUser.getMvnoId(), assignedUser.getFullName(), action);
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(customerInventoryMappingDto.getId()), customerInventoryMappingDto.getProductName(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
                    } else {
                        customerInventoryMappingDto.setNextApproverId(getLoggedInUserId());
                        customerInventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                        customerInventoryMappingDto.setTeamHierarchyMappingId(null);
                        customerInventoryMappingDto.setStatus("PENDING");
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(customerInventoryMappingDto.getId()), customerInventoryMappingDto.getProductName(), assignedUser.getId(), assignedUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedUser.getUsername());
                    }
                } else {
                    customerInventoryMappingDto.setNextApproverId(getLoggedInUserId());
                    customerInventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                    customerInventoryMappingDto.setTeamHierarchyMappingId(null);
                    customerInventoryMappingDto.setStatus("PENDING");
                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(customerInventoryMappingDto.getId()), customerInventoryMappingDto.getProductName(), getLoggedInUser().getUserId(), getLoggedInUser().getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + getLoggedInUser().getUsername());
                }
            }
            return customerInventoryMappingDto;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private CustomerInventoryMappingDto saveExternalItem(CustomerInventoryMappingDto customerInventoryMappingDto) {
        try {
            /** Get the list of MAC mappings */
            List<InOutWardMACMapping> list = customerInventoryMappingDto.getInOutWardMACMapping();
            /** Fetch the itemId from the first MAC mapping (if list is not empty) */
            if (!list.isEmpty()) {
                Long id = list.get(0).getItemId();
                /** Set the itemId for all mappings in the list */
                customerInventoryMappingDto.setItemId(id);
                /** Find all ExternalItemMacSerialMappings by itemId */
                List<ExternalItemMacSerialMapping> externalItemMacSerialMappings = externalItemMacSerialMappingRepo.findAllByItemId(id);
                /** Check if the itemList is not empty before proceeding */
                if (!externalItemMacSerialMappings.isEmpty()) {
                    /** Iterate over both lists and update the mappings */
                    for (int i = 0; i < externalItemMacSerialMappings.size(); i++) {
                        if (externalItemMacSerialMappings.get(i).getItemId().equals(id)) {
                            externalItemMacSerialMappings.get(i).setCustInventoryMappingId(customerInventoryMappingDto.getId());
                            externalItemMacSerialMappingRepo.save(externalItemMacSerialMappings.get(i));
                        }
                    }
                }
            }
            return customerInventoryMappingDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<CustomerInventoryMappingDto> handleBillToOrganization(
            List<CustomerInventoryMappingDto> customerInventoryMappingDtoList,
            CustomerInventoryMappingDto entity,
            Customers customers,
            Double enteredNewAmount,
            String itemCondition,
            Integer newChargeTexId,
            Product product,
            Integer refChargeTaxId) throws CloneNotSupportedException {
        List<CustomerInventoryMappingDto> finalCustomerInventoryMappingDtoList = new ArrayList<>();
        try {
            for (CustomerInventoryMappingDto customerInventoryMappingDto : customerInventoryMappingDtoList) {
                CustomerInventoryMappingDto entityClone = (CustomerInventoryMappingDto) entity.clone();
                List<CustomerPackage> customerPackage = customerPackageRepository.findAllByCustomersId(customers.getId());
                if (!customerPackage.isEmpty() && entity.getIsInvoiceToOrg() != null) {
                    String planType = customers.getCusttype();
                    String clientServiceName = planType.equalsIgnoreCase("Prepaid") ? "ORGANIZATION" : "ORGANIZATIONPOST";
                    ClientService value = clientServiceRepository.getByNameAndMvnoId(clientServiceName, getMvnoIdFromCurrentStaff());
                    List<Customers> organizeCustomer = customersRepository.findByUsername(value.getValue());
                    entityClone.setCustomerId(organizeCustomer.get(0).getId());
                    entityClone.setQty(1L);
                    entityClone.setMvnoId(getMvnoIdFromCurrentStaff());
                    entityClone.setMapping_ref_id(customerInventoryMappingDto.getId());
                    if (enteredNewAmount != null) {
                        Double subisuAmount = null;
                        if ("New".equalsIgnoreCase(itemCondition) && newChargeTexId != null) {
                            subisuAmount = product.getActualpricenewProduct() - enteredNewAmount;
                            entityClone.setNewAmount(getPriceWithoutTax(newChargeTexId, subisuAmount));
                        } else if ("Refurbished".equalsIgnoreCase(itemCondition) && refChargeTaxId != null) {
                            subisuAmount = product.getActualpricerefurbishedProduct() - enteredNewAmount;
                            entityClone.setNewAmount(getPriceWithoutTax(refChargeTaxId, subisuAmount));
                        }
                    }
                    CustomerInventoryMapping domain = mapper.dtoToDomain(entityClone, new CycleAvoidingMappingContext());
                    domain.setCustomer(organizeCustomer.get(0));
                    domain.setMapping_ref_id(customerInventoryMappingDto.getId());
                    CustomerInventoryMappingDto savedDto = mapper.domainToDTO(customerInventoryMappingRepo.save(domain), new CycleAvoidingMappingContext());
                    finalCustomerInventoryMappingDtoList.add(savedDto);
                }
                finalCustomerInventoryMappingDtoList.add(customerInventoryMappingDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing BillTo Organization", e);
        }
        return finalCustomerInventoryMappingDtoList;
    }

    private List<CustomerInventoryMappingDto> getSingleCustInvenMapDto(CustomerInventoryMappingDto entity, Product product) {
        try {
            entity.setQty(1L);
            if (entity.getItemType() != null) {
                if (entity.getItemType().equalsIgnoreCase("New") && product.getNewProductCharge() != null && product.getNewProductCharge().getId() != 0) {
                    entity.setChargeId(Long.valueOf(product.getNewProductCharge().getId()));
                }
                if (entity.getItemType().equalsIgnoreCase("Refurbished") && product.getRefurburshiedProductCharge() != null && product.getRefurburshiedProductCharge().getId() != 0) {
                    entity.setChargeId(Long.valueOf(product.getRefurburshiedProductCharge().getId()));
                }
            }
            List<CustomerInventoryMappingDto> customerInventoryMappingDtos = new ArrayList<>();
            CustomerInventoryMappingDto savedDto = super.saveEntity(entity);
            customerInventoryMappingDtos.add(savedDto);
            return customerInventoryMappingDtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<CustomerInventoryMappingDto> getAssemblyCustInvenMapDto(CustomerInventoryMappingDto entity, Product product, Integer newChargeTexId, Integer refChargeTaxId) {
        List<CustomerInventoryMappingDto> customerInventoryMappingDtos = new ArrayList<>();
        try {
            /** Create and save ItemAssembly group */
            ItemAssemblyDto itemAssemblyDto = new ItemAssemblyDto();
            itemAssemblyDto.setItemAssemblyName(entity.getItemAssemblyName());
            itemAssemblyDto.setStatus(entity.getItemAssemblyStatus());
            List<Long> itemIds = entity.getInOutWardMACMapping().stream()
                    .map(InOutWardMACMapping::getItemId)
                    .collect(Collectors.toList());
            itemAssemblyDto.setItemListLongId(itemIds);
            ItemAssemblyDto savedAssemblyDto = itemAssemblyServiceImp.saveEntity(itemAssemblyDto);
            if (Objects.isNull(savedAssemblyDto)) {
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), MessageConstants.ITEM_GROUP, null);
            }
            /** Process inventory mappings */
            for (InOutWardMACMapping mapping : entity.getInOutWardMACMapping()) {
                Long productId = itemRepository.findProductIdByItemId(mapping.getItemId());
                entity.setProductId(productId);
                entity.setItemAssemblyId(savedAssemblyDto.getId());
                entity.setItemId(mapping.getItemId());
                entity.setQty(1L);
                if (entity.getItemType() != null) {
                    updateChargeDetails(entity, product, newChargeTexId, refChargeTaxId);
                }
                CustomerInventoryMappingDto savedDto = super.saveEntity(entity);
                customerInventoryMappingDtos.add(savedDto);
            }
            return customerInventoryMappingDtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void updateChargeDetails(CustomerInventoryMappingDto entity, Product product, Integer newChargeTexId, Integer refChargeTaxId) {
        try {
            if (entity.getItemType().equalsIgnoreCase("New") && product.getNewProductCharge() != null) {
                entity.setChargeId(Long.valueOf(product.getNewProductCharge().getId()));
                if (isSTB(product) && newChargeTexId != null) {
                    entity.setNewAmount(getPriceWithoutTax(Math.toIntExact(newChargeTexId), entity.getNewAmount()));
                }
                if (isCard(product)) {
                    entity.setNewAmount(0d);
                }
            }

            if (entity.getItemType().equalsIgnoreCase("Refurbished") && product.getRefurburshiedProductCharge() != null) {
                entity.setChargeId(Long.valueOf(product.getRefurburshiedProductCharge().getId()));
                if (isSTB(product) && refChargeTaxId != null) {
                    entity.setNewAmount(getPriceWithoutTax(Math.toIntExact(refChargeTaxId), entity.getNewAmount()));
                }
                if (isCard(product)) {
                    entity.setNewAmount(0d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private boolean isSTB(Product product) {
        return "STB".equalsIgnoreCase(product.getProductCategory().getDtvCategory());
    }

    private boolean isCard(Product product) {
        return "Card".equalsIgnoreCase(product.getProductCategory().getDtvCategory());
    }

    /**
     * Save cust inv params list.
     * @param custInvParamsDtos the cust inv params dtos
     * @param custId the cust id
     * @param custServMappId the cust serv mapp id
     * @param custInvId the cust inv id
     * @return the list
     */
    public List<CustInvParamsDto> saveCustInvParams(List<CustInvParamsDto> custInvParamsDtos, Long custId, Long custServMappId, Long custInvId) {
        try {
            List<CustInvParams> custInvParams = custInvParamsMapper.dtoToDomain(custInvParamsDtos, new CycleAvoidingMappingContext());
            for (CustInvParams invParams : custInvParams) {
                invParams.setCustSerMapId(custServMappId);
                invParams.setCustId(custId);
                invParams.setCustInvId(custInvId);
            }
            custInvParams = custInvParamsRepo.saveAll(custInvParams);
            return custInvParamsMapper.domainToDTO(custInvParams, new CycleAvoidingMappingContext());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Search generic data dto.
     * @param filterList the filter list
     * @param page the page
     * @param pageSize the page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the generic data dto
     */
//
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        try {
            PageRequest pageRequest = super.generatePageRequest(page, pageSize, "createdate", sortOrder);
            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
            QInward qInward = QInward.inward;
            QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.qty.gt(0));
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (filterList.size() > 0) {
                for (GenericSearchModel genericSearchModel : filterList) {
                    switch (genericSearchModel.getFilterColumn()) {
                        case "customerId":
                            booleanExpression = booleanExpression.and(qCustomerInventoryMapping.customer.id.eq(Integer.valueOf(genericSearchModel.getFilterValue())));
                            break;
                        case "inwardNumber":
                            booleanExpression = booleanExpression.and(qInward.inwardNumber.eq(genericSearchModel.getFilterValue()));
                            break;
                        case "productName":
                            booleanExpression = booleanExpression.and(qCustomerInventoryMapping.product.name.eq(genericSearchModel.getFilterValue()));
                            break;
                        case "externalItemGroupNumber":
                            booleanExpression = booleanExpression.and(qExternalItemManagement.externalItemGroupNumber.eq(genericSearchModel.getFilterValue()));
                            break;
                    }
                }
            }
            return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Make generic response generic data dto.
     * @param genericDataDTO the generic data dto
     * @param paginationList the pagination list
     * @return the generic data dto
     */
    @Override
    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<CustomerInventoryMapping> paginationList) {

        try {
            List<CustomerInventoryMappingDto> list = paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());

            list.forEach(customerInventoryMappingDto -> {
                if (customerInventoryMappingDto.getCustPackId() == null) {
                    customerInventoryMappingDto.setCurrentPlan("");
                } else if (customerInventoryMappingDto.getCustPackId() != null) {
                    customerInventoryMappingDto.setCurrentPlan(customerPackageRepository.findByCustPackageId(Math.toIntExact(customerInventoryMappingDto.getCustPackId())).getPlan().getDisplayName());
                }
            });

            list.forEach(customerInventoryMappingDto -> {
                try {
                    if (customerInventoryMappingDto.getItemId() == null) {
                        customerInventoryMappingDto.setItemId(0L);
                    } else if (customerInventoryMappingDto.getItemId() != null) {
                        Services services = serviceRepository.findById(customerInventoryMappingDto.getServiceId()).get();
                        customerInventoryMappingDto.setServiceName(services.getServiceName());
                    }
                } catch (Exception e) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
                }
            });

            list.forEach(customerInventoryMappingDto -> {
                if ((customerInventoryMappingDto.getExternalItemId()) == null) {
                    customerInventoryMappingDto.setWarranty(itemRepository.findById(customerInventoryMappingDto.getItemId()).get().getWarranty());
                    customerInventoryMappingDto.setItemType(itemRepository.findById(customerInventoryMappingDto.getItemId()).get().getCondition());

                } else if ((customerInventoryMappingDto.getExternalItemId()) != null) {
                    customerInventoryMappingDto.setWarranty("");
                    customerInventoryMappingDto.setItemType("");

                }
            });

            genericDataDTO.setDataList(list);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(paginationList.getTotalElements());
            genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
            genericDataDTO.setTotalPages(paginationList.getTotalPages());
            return genericDataDTO;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return genericDataDTO;

    }

    /**
     * Sets customer inventory id to item history.
     * @param customerInventoryMappingDtoList the customer inventory mapping dto list
     * @param inventoryMappingDto the inventory mapping dto
     * @return the customer inventory id to item history
     */
    public List<CustomerInventoryMappingDto> setCustomerInventoryIdToItemHistory(List<CustomerInventoryMappingDto> customerInventoryMappingDtoList, CustomerInventoryMappingDto inventoryMappingDto) {
        try {
            customerInventoryMappingDtoList.stream().forEach(r -> {
                ProductCategory productCategory = productCategoryRepository.findById(productRepository.findById(r.getProductId()).get().getProductCategory().getId()).orElse(null);
                if (productCategory.getDtvCategory().equalsIgnoreCase("STB")) {
                    if (r.getItemId() == inventoryMappingDto.getInOutWardMACMapping().get(0).getItemId()) {
                        inventoryMappingDto.getInOutWardMACMapping().get(0).setCustInventoryMappingId(r.getId());
                        inOutWardMacRepo.save(inventoryMappingDto.getInOutWardMACMapping().get(0));

                    }
                    if (r.getItemId() == inventoryMappingDto.getInOutWardMACMapping().get(1).getItemId()) {
                        inventoryMappingDto.getInOutWardMACMapping().get(1).setCustInventoryMappingId(r.getId());
                        inOutWardMacRepo.save(inventoryMappingDto.getInOutWardMACMapping().get(1));

                    }
                }

                if (productCategory.getDtvCategory().equalsIgnoreCase("Card")) {
                    if (r.getItemId() == inventoryMappingDto.getInOutWardMACMapping().get(0).getItemId()) {
                        inventoryMappingDto.getInOutWardMACMapping().get(0).setCustInventoryMappingId(r.getId());
                        inOutWardMacRepo.save(inventoryMappingDto.getInOutWardMACMapping().get(0));

                    }
                    if (r.getItemId() == inventoryMappingDto.getInOutWardMACMapping().get(1).getItemId()) {
                        inventoryMappingDto.getInOutWardMACMapping().get(1).setCustInventoryMappingId(r.getId());
                        inOutWardMacRepo.save(inventoryMappingDto.getInOutWardMACMapping().get(1));

                    }
                }
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }


    /**
     * Gets all customer inventory list.
     * @param custId the cust id
     * @return the all customer inventory list
     */
    public List<CustomerInventoryMappingDto> getAllCustomerInventoryList(Integer custId, boolean pendingFilter) {
        try {
            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull();
            booleanExpression = booleanExpression.and(qCustomerInventoryMapping.customer.id.eq(custId).and(qCustomerInventoryMapping.isDeleted.eq(false)));
            OrderSpecifier<Long> orderByIdDesc = qCustomerInventoryMapping.id.desc();
            List<CustomerInventoryMapping> customerInventoryMappings = (List<CustomerInventoryMapping>) customerInventoryMappingRepo.findAll(booleanExpression, orderByIdDesc);
            QCustomers qCustomers = QCustomers.customers;
            BooleanExpression getChildExpression = qCustomers.isNotNull().and(qCustomers.parentCustId.eq(custId)).and(qCustomers.parentExperience.equalsIgnoreCase(CommonConstants.PARENT_EXPERIENCE_SINGLE).and(qCustomers.isDeleted.eq(false).and(qCustomers.status.eq(CommonConstants.CUSTOMER_STATUS_ACTIVE))));
            List<Integer> childCustIds = ((List<Customers>) customersRepository.findAll(getChildExpression)).stream().map(Customers::getId).collect(Collectors.toList());
            if (childCustIds != null && childCustIds.size() > 0) {
                QCustomerInventoryMapping qChildCustInvMapping = QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression getChildInventoryEx = qChildCustInvMapping.isNotNull();
                getChildInventoryEx = getChildInventoryEx.and(qChildCustInvMapping.customer.id.in(childCustIds).and(qChildCustInvMapping.isDeleted.eq(false)));
                List<CustomerInventoryMapping> childCustomerInventoryMappings = (List<CustomerInventoryMapping>) customerInventoryMappingRepo.findAll(getChildInventoryEx);
                if (childCustomerInventoryMappings != null && childCustomerInventoryMappings.size() > 0) {
                    customerInventoryMappings.addAll(childCustomerInventoryMappings);
                }
            }
            List<CustomerInventoryMappingDto> customerInventoryMappingDtoList = customerInventoryMappingMapper.domainToDTO(customerInventoryMappings, new CycleAvoidingMappingContext());
            customerInventoryMappingDtoList.stream().forEach(r -> {
                GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findRequestByCustomerInventoryId(r.getId());
                if (generateRemoveRequest != null) {
                    r.setGenerateRemoveRequest(true);
                    r.setRemoveRequestStatus(generateRemoveRequest.getRequestStatus());
                    if (generateRemoveRequest.getRevisedcharge() != null) {
                        r.setRevisedCharge(generateRemoveRequest.getRevisedcharge());
                    }
                } else {
                    r.setGenerateRemoveRequest(false);
                }
                if (r.getItemAssemblyName() == null) {
                    r.setItemAssemblyName(null);
                } else {
                    String itemAssemblyName = itemAssemblyRepo.findAssemblyNameById(r.getItemAssemblyId());
                    r.setItemAssemblyName(itemAssemblyName);
                }
                if (r.getItemAssemblyId() == null) {
                    r.setCustInventoryListId(r.getId());
                } else {
                    r.setCustInventoryListId(r.getItemAssemblyId());
                }
                if (r.getServiceId() != null) {
                    r.setServiceName(serviceRepository.findServiceNameById(r.getServiceId()));
                }
                if (r.getPlanId() != null) {
                    r.setCurrentPlan(postpaidPlanRepo.findNameById(Math.toIntExact(r.getPlanId())));
                }
                if (r.getInOutWardMACMapping().size() != 0) {
                    r.setItemType(itemRepository.findItemConditionByItemId(r.getItemId()));
                    r.setWarranty(itemRepository.findWarrantyByItemId(r.getItemId()));
                }
                Long pcId = productRepository.findProductCategoryIdByProductId(r.getProductId());
                if (pcId != null) {
                    boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
                    boolean hasMac = productCategoryRepository.findHasMacById(pcId);
                    if (!hasMac && !hasSerial) {
                        r.setExpDate(null);
                    } else if (hasMac || hasSerial) {
                        r.setExpDate(itemRepository.findExpiry_dateByItemId(r.getItemId()));
                    }
                }

                List<InOutWardMACMapping> inOutWardMACMappingList = r.getInOutWardMACMapping();
                if (inOutWardMACMappingList.size() == 2) {
                    List<InOutWardMACMapping> outWardMACMappingList = new ArrayList<>(inOutWardMACMappingList);
                    inOutWardMACMappingList.stream().forEach(t -> {
                        if (t.getStatus().equalsIgnoreCase("PENDING")) {
                            outWardMACMappingList.add(0, t);
                        } else {
                            outWardMACMappingList.add(1, t);
                        }
                    });
                    r.setInOutWardMACMapping(outWardMACMappingList);
                    for (int i = r.getInOutWardMACMapping().size() - 1; i > 1; i--) {
                        r.getInOutWardMACMapping().remove(i);
                    }
                }
                if (r.getNextApproverId() == null && r.getStatus().equals("PENDING") && r.getTeamHierarchyMappingId() != null) {
                    Integer custInveId = Math.toIntExact(r.getId());
                    List<WorkflowAssignStaffMapping> workflowAssignStaffMappingList = workflowAssignStaffMappingRepo.findAllByEntityIdAndStaffIdAndTeamHierarchyMappingId(custInveId, getLoggedInUserId(), r.getTeamHierarchyMappingId());
                    if (!workflowAssignStaffMappingList.isEmpty()) {
                        r.setNextApproverId(workflowAssignStaffMappingList.get(0).getStaffId());
                    }
                }
                if (r.getConnectionNo() != null) {
                    List<Integer> custServIds = customerServiceMappingRepository.findCustServiceIdByConnectionNo(r.getConnectionNo());
                    if (!CollectionUtils.isEmpty(custServIds)) {
                        r.setCustServiceMapId(Long.valueOf(custServIds.get(0)));
                    }
                } else if (r.getServiceId() != null) {
                    List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findAllByServiceIdAndCustId(r.getServiceId(), r.getCustomerId());
                    if (!CollectionUtils.isEmpty(customerServiceMappings)) {
                        r.setCustServiceMapId(Long.valueOf(customerServiceMappings.get(0).getId()));
                    }
                }
            });
            return customerInventoryMappingDtoList;

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * Update item changes customer inventory mapping dto.
     * @param customerInventoryMappingId the customer inventory mapping id
     * @param isApproveRequest the is approve request
     * @param createdById the created by id
     * @param lastModifiedById the last modified by id
     * @return the customer inventory mapping dto
     * @throws Exception the exception
     */
   @Transactional
    public CustomerInventoryMappingDto updateItemChanges(Long customerInventoryMappingId, boolean isApproveRequest, Integer createdById, Integer lastModifiedById) throws Exception {
        try {
            CustomerInventoryMappingDto entity = super.getEntityById(customerInventoryMappingId);
            Customers customers = customersRepository.findById(entity.getCustomerId()).get();
            Product product = productRepository.findById(entity.getProductId()).get();
            ProductDto dto = productMapper.domainToDTO(product, new CycleAvoidingMappingContext());
            ProductCategoryDto productCategoryDto = productCategoryService.getEntityById(dto.getProductCategory().getId());
            StaffUser loggedInUser = staffUserRepository.findById(Integer.valueOf(getLoggedInUserId())).get();
//            System.out.println(getLoggedInUser());
            /**
             * Code For External Inventory Item
             */
            if (entity.getExternalItemId() != null) {
                List<InOutWardMACMapping> inOutWardMACMappingList = entity.getInOutWardMACMapping();
                List<Long> id = inOutWardMACMappingList.stream().map(InOutWardMACMapping::getItemId).collect(Collectors.toList());
                List<Item> itemList = itemRepository.findAllById(id);
                //update Item Status Item Status mapping
//                if (isApproveRequest == true) {
//                    for (int i = 0; i <= itemList.size() - 1; i++) {
//                        if ((itemList.get(i).getId().equals(id.get(i)))) {
//                            itemList.get(i).setOwnerId(Long.valueOf(entity.getCustomerId()));
//                            itemList.get(i).setOwnerType(CommonConstants.CUSTOMER);
//                            itemService.updateItemStatusForCustomer(itemList.get(i).getId(), CommonConstants.ALLOCATED, entity.getAssignedDateTime(), customers.getId().longValue(), CommonConstants.ASSIGN_INVETORIES);
//                            itemRepository.save(itemList.get(i));
//                        }
//                    }
//                }
                if (isApproveRequest) {
                    List<Item> updatedItems = new ArrayList<>();
                    for (int i = 0; i < itemList.size(); i++) {
                        Item item = itemList.get(i);
                        if (item.getId().equals(id.get(i))) {
                            item.setOwnerId(Long.valueOf(entity.getCustomerId()));
                            item.setOwnerType(CommonConstants.CUSTOMER);
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.ALLOCATED, entity.getAssignedDateTime(), customers.getId().longValue(), CommonConstants.ASSIGN_INVETORIES
                            );
                            updatedItems.add(item);
                        }
                    }
                    if (!updatedItems.isEmpty()) {
                        itemRepository.saveAll(updatedItems);
                    }
                }
                if (isApproveRequest == false) {
                    List<Item> updatedItems = new ArrayList<>();
                    for (int i = 0; i < itemList.size(); i++) {
                        Item item = itemList.get(i);
                        if (item.getId().equals(id.get(i))) {
                            item.setOwnerType(CommonConstants.SERIALISED_ITEM_OWNERTYPE.SERVICEAREA);
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(
                                    item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REJECT_INVETORIES
                            );
                            updatedItems.add(item);
                        }
                    }
                    if (!updatedItems.isEmpty()) {
                        itemRepository.saveAll(updatedItems);
                    }

                    //removemappingWith ItemHistory
                    if (productCategoryDto.isHasMac() || productCategoryDto.isHasSerial() || productCategoryDto.isHasTrackable()) {
                        for (InOutWardMACMapping inOutWardMACMapping : entity.getInOutWardMACMapping()) {
                            inOutWardMACService.removeMappingWithCustomerInventory(inOutWardMACMapping.getId(), customers);
                        }
                    }

                    ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(entity.getExternalItemId()).get();
                    externalItemManagement.setUnusedQty(externalItemManagement.getUnusedQty() + 1);
                    externalItemManagement.setUsedQty(externalItemManagement.getUsedQty() - 1);
                    externalItemManagementRepository.save(externalItemManagement);
                    entity.setQty(0L);

                    for (int i = 0; i < entity.getExternalItemMacSerialMappings().size(); i++) {
                        ExternalItemMacSerialMapping externalItemMacSerialMapping = externalItemMacSerialMappingRepo.findById(entity.getExternalItemMacSerialMappings().get(i).getId()).get();
                        externalItemMacSerialMapping.setCustInventoryMappingId(null);
                        externalItemMacSerialMappingRepo.save(externalItemMacSerialMapping);
                    }
                }

                if (isApproveRequest) {
                    //Push CustMacmapping with only Without STB Card category product
                    entity.getInOutWardMACMapping().forEach(inOutWardMACMapping -> {
                        ProductCategory productCategory = productCategoryRepository.findById(productRepository.findById(itemRepository.findById(inOutWardMACMapping.getItemId()).get().getProductId()).get().getProductCategory().getId()).get();
                        CustMacMappping checkCustmacMapping = custMacMapppingRepository.findByMacAddressAndIsDeletedIsFalseAndMacAddressIsNotNull(itemRepository.findById(inOutWardMACMapping.getItemId()).get().getMacAddress());
                        if (checkCustmacMapping != null) {
                            if (productCategory.isHasCas() == false && inOutWardMACMapping.getMacAddress() != null) {
                                CustMacMappping custMacMappping = new CustMacMappping();
                                custMacMappping.setMacAddress(inOutWardMACMapping.getMacAddress());
                                custMacMappping.setCustomer(customers);
                                custMacMapppingService.saveData(custMacMappping, customers);
                            }
                        }
                    });
                }
            }
            /**
             * Code for Other/ Plan Inventory Item
             */
            if (entity.getExternalItemId() == null) {
                boolean hasmac = product.getProductCategory().isHasMac();
                boolean hasserial = product.getProductCategory().isHasSerial();
                if (!customers.getId().equals(1) && !customers.getId().equals(2)) {
                    if (isApproveRequest) {
                        //update Item Status
                        if (hasserial || hasmac) {
                            Item item = itemRepository.findById(entity.getItemId()).orElse(null);
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.ALLOCATED, entity.getAssignedDateTime(), customers.getId().longValue(), CommonConstants.ASSIGN_INVETORIES);
                            item.setOwnerId(Long.valueOf(entity.getCustomerId()));
                            item.setOwnerType(CommonConstants.CUSTOMER);
                            item.setItemStatus(CommonConstants.ALLOCATED);
                            itemRepository.save(item);
                        }

                        //Push CustMacmapping with only Without STB Card category product
                        if (hasmac || hasserial) {
                            entity.getInOutWardMACMapping().forEach(inOutWardMACMapping -> {
                                ProductCategory productCategory = productCategoryRepository.findById(productRepository.findById(itemRepository.findById(inOutWardMACMapping.getItemId()).get().getProductId()).get().getProductCategory().getId()).get();
                                CustMacMappping checkCustmacMapping = custMacMapppingRepository.findByMacAddressAndIsDeletedIsFalseAndMacAddressIsNotNull(itemRepository.findById(inOutWardMACMapping.getItemId()).get().getMacAddress());
                                if (checkCustmacMapping == null) {
                                    if (productCategory.isHasCas() == false && inOutWardMACMapping.getMacAddress() != null) {
                                        CustMacMappping custMacMappping = new CustMacMappping();
                                        custMacMappping.setMacAddress(inOutWardMACMapping.getMacAddress());
                                        custMacMappping.setCustomer(customers);
                                        custMacMapppingService.save(custMacMappping);
                                    }
                                }
                            });
                        }
                    }

                    if (isApproveRequest == false) {
                        List<InOutWardMACMapping> inOutWardMACMappings = inOutWardMacRepo.findAllByCustInventoryMappingId(customerInventoryMappingId);
                        if (hasmac || hasserial) {
                            inOutWardMACMappings.forEach(inOutWardMACMapping -> {
                                Item serializedItem = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
                                /** Called: Method Update Item Status Customer */
                                itemService.updateItemStatusForCustomer(serializedItem, CommonConstants.UNALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REJECT_INVETORIES);
                                if (getLoggedInUser().getPartnerId() != 1) {
//                            item.setOwnerId(Long.valueOf(staffUserService.get(getLoggedInUserId()).getPartnerid()));
                                    serializedItem.setOwnerId(Long.valueOf(loggedInUser.getPartnerid()));
                                    serializedItem.setOwnerType(CommonConstants.PARTNER);
                                } else {
//                            item.setOwnerId(Long.valueOf(staffUserService.get(getLoggedInUserId()).getId()));
                                    serializedItem.setOwnerId(Long.valueOf(loggedInUser.getId()));
                                    serializedItem.setOwnerType(CommonConstants.STAFF);
                                    serializedItem.setItemStatus(CommonConstants.STAFF_ALLOCATED);
                                }
                                itemRepository.save(serializedItem);
                            });
                        }

                        //removemappingWith ItemHistory
                        if (productCategoryDto.isHasMac() || productCategoryDto.isHasSerial() || productCategoryDto.isHasTrackable()) {
                            for (InOutWardMACMapping inOutWardMACMapping : entity.getInOutWardMACMapping()) {
                                inOutWardMACService.removeMappingWithCustomerInventory(inOutWardMACMapping.getId(), customers);
                            }
                            productOwnerService.updateProductOwnerForSerializedProductReject(entity.getQty(), entity.getProductId(), Integer.valueOf(entity.getStaffId()), CommonConstants.STAFF);
                        } else {
                            productOwnerService.updateProductOwnerForNonTrackableAfterReject(entity.getQty(), entity.getProductId(), Long.valueOf(entity.getStaffId()), CommonConstants.STAFF);
                        }

                        entity.setQty(0L);
                        if (entity.getInwardId() != null) {
                            for (InOutWardMACMapping inOutWardMACMapping : entity.getInOutWardMACMapping()) {
                                inOutWardMACService.removeMappingWithCustomerInventory(inOutWardMACMapping.getId(), customers);
                            }
                            Inward inward = inwardRepository.findById(entity.getInwardId()).get();
                            inward.setUnusedQty(inward.getUnusedQty() + 1);
                            inward.setUsedQty(inward.getUsedQty() - 1);
                            inwardRepository.save(inward);
                        }
                    }
                }
            }
            if (isApproveRequest) {
                entity.setStatus("ACTIVE");
                //Add Network Device
                if (!customers.getId().equals(1) && !customers.getId().equals(2)) {
                    if (productCategoryDto.isHasMac() || productCategoryDto.isHasSerial()) {
                        if (entity.getStatus().equalsIgnoreCase("ACTIVE")) {
                            /** Called: Method Create Network Device */
                            createNetworkDevice(dto, customers, entity.getInOutWardMACMapping().get(0).getSerialNumber(),
                                    entity.getInwardId(), entity.getId(), entity.getItemId());
                        }
                    }
                }
            }

            if (!isApproveRequest) {
                entity.setStatus("REJECTED");
            }
            entity.setCreatedById(createdById);
            entity.setLastModifiedById(lastModifiedById);
            return super.saveEntity(entity);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }


    /**
     * Gets subisu cust id.
     * @param custId the cust id
     * @return the subisu cust id
     */
    private Integer getSubisuCustId(Integer custId) {
        try {
            if (custId != null) {
                Optional<Customers> customers = customersRepository.findById(custId);
                if (customers.isPresent()) {
                    String planType = customers.get().getCusttype();
                    ClientService value = null;

                    if (planType.equalsIgnoreCase("Prepaid")) {
                        value = clientServiceRepository.getByNameAndMvnoId("ORGANIZATION", getMvnoIdFromCurrentStaff());
                    } else {
                        value = clientServiceRepository.getByNameAndMvnoId("ORGANIZATIONPOST", getMvnoIdFromCurrentStaff());
                    }

                    List<Customers> customersUser = customersRepository.findByUsername(value.getValue());

                    Integer custId1 = customersUser.get(0).getId();

                    return custId1;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Create network device.
     * @param entity the entity
     * @param id
     * @param dto the dto
     * @param customers the customers
     * @param serialNumber
     * @param inwardId
     * @param itemId
     * @throws Exception the exception
     */
    private void createNetworkDevice(ProductDto dto, Customers customers, String serialNumber, Long inwardId,
                                     Long custInventoryId, Long itemId) throws Exception {
        NetworkDeviceDTO networkDeviceDTO = new NetworkDeviceDTO();
        try {
            networkDeviceDTO.setProductId(dto.getId());
            Long count = itemRepository.findAllByIsDeletedIsFalseAndOwnerIdAndOwnerType(customers.getId().longValue(), "Customer");
            networkDeviceDTO.setName(dto.getName() + "-" + customers.getFirstname() + "-" + serialNumber + "-" + count);
            networkDeviceDTO.setDisplayname(dto.getName() + "-" + customers.getFirstname() + "-" + serialNumber + "-" + count);
            networkDeviceDTO.setStatus(dto.getStatus());
            networkDeviceDTO.setMvnoId(dto.getMvnoId());
            networkDeviceDTO.setDevicetype("");
            networkDeviceDTO.setLatitude(customers.getLatitude());
            networkDeviceDTO.setLongitude(customers.getLongitude());
            networkDeviceDTO.setAvailableInPorts(dto.getAvailableInPorts());
            networkDeviceDTO.setTotalInPorts(dto.getTotalInPorts());
            networkDeviceDTO.setAvailableOutPorts(dto.getAvailableOutPorts());
            networkDeviceDTO.setTotalOutPorts(dto.getTotalOutPorts());
            if (dto.getTotalInPorts() != null && dto.getTotalOutPorts() != null) {
                networkDeviceDTO.setTotalPorts(dto.getTotalInPorts() + dto.getTotalOutPorts());
                networkDeviceDTO.setAvailablePorts(dto.getTotalInPorts() + dto.getTotalOutPorts());
            }
            networkDeviceDTO.setInwardId(inwardId);
            networkDeviceDTO.setServiceAreaNameList(Collections.singletonList(serviceAreaMapper.domainToDTO(customers.getServicearea(), new CycleAvoidingMappingContext())));
            networkDeviceDTO.setServiceAreaIdsList(Collections.singletonList(customers.getServicearea().getId()));
//            Product product = productRepository.getOne(dto.getId());
            networkDeviceDTO.setProductName(dto.getName());
            networkDeviceDTO.setIsDeleted(dto.getIsDeleted());
            networkDeviceDTO.setCustInventoryId(custInventoryId);
            networkDeviceDTO.setItemId(itemId);
            networkDeviceService.saveEntity(networkDeviceDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
    }

    /**
     * Replace all invetories generic data dto.
     * @param approveReplaceAllInventoryDTOS the approve replace all inventory dtos
     * @param customerId the customer id
     * @param ownerShipType the owner ship type
     * @param replacementReason the replacement reason
     * @param remark the remark
     * @return the generic data dto
     */
   @Transactional
    public GenericDataDTO replaceAllInvetories(List<ApproveReplaceAllInventoryDTO> approveReplaceAllInventoryDTOS, Long customerId, String ownerShipType, String replacementReason, String remark) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<InOutWardMACMapping> inOutWardMACMappingList = new ArrayList<>();
        try {
            approveReplaceAllInventoryDTOS.stream().forEach(r -> {
                if (r.getOldMacMappingId() != null && r.getNewMacMappingId() != null) {
                    inOutWardMACMappingList.add(replaceInventory(r.getOldMacMappingId(), r.getNewMacMappingId(), customerId, ownerShipType, replacementReason, remark));
                } else {
                    throw new RuntimeException("To Replace Select Both Items");
                }
            });

            genericDataDTO.setDataList(inOutWardMACMappingList);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    /**
     * Replace inventory in out ward mac mapping.
     * @param oldMacMappingId the old mac mapping id
     * @param newMacMappingId the new mac mapping id
     * @param customerId the customer id
     * @param ownerShipType the owner ship type
     * @param replacementReason the replacement reason
     * @param remark the remark
     * @return the in out ward mac mapping
     */
   @Transactional
    public InOutWardMACMapping replaceInventory(Long oldMacMappingId, Long newMacMappingId, Long customerId, String ownerShipType, String replacementReason, String remark) {
        try {
            InOutWardMACMapping oldInOutWardMACMapping = inOutWardMACService.getRepository().findById(oldMacMappingId).orElse(null);
            InOutWardMACMapping newInOutWardMACMapping = inOutWardMACService.getRepository().findById(newMacMappingId).orElse(null);

            InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(newMacMappingId).get();
            Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
            Product product = productRepository.findById(item.getProductId()).get();
            boolean hasMac = product.getProductCategory().isHasMac();
            boolean hasSerial = product.getProductCategory().isHasSerial();
            if (hasMac) {
                customerInventoryMappingService.validateMacAtReplace(inOutWardMACMapping, item);
            }
            if (hasSerial) {
                customerInventoryMappingService.validateSerialNumberAtReplace(inOutWardMACMapping, item);
            }

            Item olditem = itemRepository.findById(oldInOutWardMACMapping.getItemId()).orElse(null);
            Item newItem = itemRepository.findById(newInOutWardMACMapping.getItemId()).orElse(null);
            if (ownerShipType.equalsIgnoreCase("Temporary Replacement")) {
                if (!(olditem.getWarranty().equalsIgnoreCase("InWarranty"))) {
                    throw new RuntimeException("The given item is not in InWarrenty");
                }
                newItem.setIntransiantOwnership(newItem.getOwnershipType());
                newItem.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.TEMPORARY);
                olditem.setIntransiantOwnership(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.TEMPORARY);
                itemRepository.save(newItem);
                itemRepository.save(olditem);

            }
            if (ownerShipType.equalsIgnoreCase("Permanant Replacement")) {
                newItem.setIntransiantOwnership(olditem.getOwnershipType());
                itemRepository.save(newItem);

            }
            Customers customer = customersRepository.findById(Math.toIntExact(customerId)).get();
            //   if (!customer.getIstrialplan()) {
            if (olditem.getCondition().equalsIgnoreCase("Refurbished")) {
                if (!newItem.getCondition().equalsIgnoreCase("Refurbished"))
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Item condition mismatched.Please assign Refurbished category Item.", null);
            }

            if (olditem.getWarranty().equalsIgnoreCase("Expired")) {
                itemRepository.save(newItem);
            }
            if (olditem.getRemainingDays() != null) {
                olditem.setIntransiantWarrenty(olditem.getRemainingDays());
                newItem.setIntransiantWarrenty(olditem.getRemainingDays());
                itemRepository.save(olditem);
                itemRepository.save(newItem);
            }
            if (olditem.getExpireDate() != null) {
                olditem.setIntransiantexpireDate(olditem.getExpireDate());
                newItem.setIntransiantexpireDate(olditem.getExpireDate());
                itemRepository.save(olditem);
                itemRepository.save(newItem);

            }

            if (olditem.getWarranty().equalsIgnoreCase("InWarranty") || olditem.getWarranty().equalsIgnoreCase("NotStarted") || olditem.getWarranty().equalsIgnoreCase("Expired") || olditem.getWarranty().equalsIgnoreCase("Paused") || olditem.getWarranty().equalsIgnoreCase("NoWarranty")) {
                olditem.setIntransiantWarrentyStatus(olditem.getWarranty());
                newItem.setIntransiantWarrentyStatus(olditem.getWarranty());
            }

            //setReplacementReason for olditem
            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(oldInOutWardMACMapping.getCustInventoryMappingId()).orElse(null);
            if (customerInventoryMapping != null) {
                customerInventoryMapping.setTeamHierarchyMappingId(null);
                customerInventoryMapping.setPreviousApproveId(getLoggedInUserId());
                customerInventoryMapping.setReplacementReason(replacementReason);
                customerInventoryMapping.setApprovalRemark(remark);
                customerInventoryMappingRepo.save(customerInventoryMapping);
            }

            //update ProductOwner Table Details
            ProductOwner newproductOwner = null;
            ProductOwner oldProductOwner = null;
            Product newInventoryProduct = productRepository.findById(itemRepository.findById(inOutWardMacRepo.findById(newMacMappingId).get().getItemId()).get().getProductId()).orElse(null);
            Product oldInventoryProduct = productRepository.findById(itemRepository.findById(inOutWardMacRepo.findById(oldMacMappingId).get().getItemId()).get().getProductId()).orElse(null);

            if (newInventoryProduct.getId() == oldInventoryProduct.getId() || newInventoryProduct != null) {
                if (getLoggedInUser().getPartnerId() != 1) {
                    newproductOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(newInventoryProduct.getId(), customerInventoryMapping.getCreatedById().longValue(), "Partner");
                } else {
                    newproductOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(newInventoryProduct.getId(), customerInventoryMapping.getCreatedById().longValue(), "Staff");
                }
                newproductOwner.setQuantity(newproductOwner.getQuantity());
                newproductOwner.setUnusedQty(newproductOwner.getUnusedQty() - 1);
                newproductOwner.setUsedQty(newproductOwner.getUsedQty() + 1);
                productOwnerRepository.save(newproductOwner);
            }
//            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            if (oldInOutWardMACMapping != null) {
                CustomerInventoryMappingDto entity = super.getEntityById(oldInOutWardMACMapping.getCustInventoryMappingId());
//                Customers customers = customersService.get(entity.getCustomerId());
                Customers customers = customersRepository.findById(entity.getCustomerId()).get();
//                StaffUser loggedInStaffUser = staffUserService.get(getLoggedInUserId());
                StaffUser loggedInStaffUser = staffUserRepository.findById(Integer.valueOf(getLoggedInUserId())).get();
                if (clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        Long daysDiff = Duration.between(entity.assignedDateTime, LocalDateTime.now()).toDays();
                        oldInOutWardMACMapping.setStatus(CommonConstants.PENDING);
                        newInOutWardMACMapping.setCurrentApproveId(Integer.valueOf(map.get("staffId")));
                        newInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                        newInOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        newInOutWardMACMapping.setStatus("New");
                        newInOutWardMACMapping.setCustInventoryMappingId(entity.getId());
//                        loggedInStaffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
                        loggedInStaffUser = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                    } else {
                        Long daysDiff = Duration.between(entity.assignedDateTime, LocalDateTime.now()).toDays();
                        oldInOutWardMACMapping.setStatus(CommonConstants.PENDING);
                        oldInOutWardMACMapping.setUsedCount(Math.toIntExact(daysDiff));
                        newInOutWardMACMapping.setCurrentApproveId(null);
//                        newInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                        newInOutWardMACMapping.setTeamHierarchyMappingId(null);
                        newInOutWardMACMapping.setCustInventoryMappingId(entity.getId());
                        newInOutWardMACMapping.setStatus("New");
                    }
                } else {
                    Long daysDiff = Duration.between(entity.assignedDateTime, LocalDateTime.now()).toDays();
                    oldInOutWardMACMapping.setStatus(CommonConstants.PENDING);
                    newInOutWardMACMapping.setCurrentApproveId(loggedInStaffUser.getId());
                    newInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                    newInOutWardMACMapping.setTeamHierarchyMappingId(null);
                    newInOutWardMACMapping.setCustInventoryMappingId(entity.getId());
                    newInOutWardMACMapping.setStatus("New");
                }

                //item validity exprired set proper validity
                inOutWardMACService.getRepository().save(oldInOutWardMACMapping);
                newInOutWardMACMapping = inOutWardMACService.getRepository().save(newInOutWardMACMapping);
                workflowAuditService.saveAudit(0, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(newInOutWardMACMapping.getId()), newInOutWardMACMapping.getSerialNumber() == null ? "" : newInOutWardMACMapping.getSerialNumber(), loggedInStaffUser.getId(), loggedInStaffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + loggedInStaffUser.getUsername());
                return newInOutWardMACMapping;
            } else {
                throw new RuntimeException("No mapping found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error(e.getMessage());
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }

    }

    /**
     * Gets active serialnumber by connection no.
     * @param connectionNo the connection no
     * @param customerId the customer id
     * @return the active serialnumber by connection no
     */
    public List<CustomerInventorySerialnumberDto> getActiveSerialnumberByConnectionNo(String connectionNo, Integer customerId) {
        try {
            List<CustomerInventoryMapping> customerInventoryMappings = new ArrayList<>();
            if (getMvnoIdFromCurrentStaff() == 1) {
                customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatus(connectionNo, customerId, CommonConstants.ACTIVE_STATUS);
            } else {
                customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatusAndMvnoIdIn(connectionNo, customerId, CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
            List<CustomerInventorySerialnumberDto> customerInventorySerialnumberDtoList = new ArrayList<>();
            customerInventoryMappings.stream().forEach(customerInventoryMapping -> {
                        CustomerInventorySerialnumberDto customerInventorySerialnumberDto = new CustomerInventorySerialnumberDto();
                        customerInventorySerialnumberDto.setCustomerId(customerInventoryMapping.getCustomer().getId());
                        customerInventorySerialnumberDto.setConnectionNo(customerInventoryMapping.getConnectionNo());
                        Item item = itemRepository.findById(customerInventoryMapping.getItemId()).get();
                        customerInventorySerialnumberDto.setSerialNumber(item.getSerialNumber());
                        customerInventorySerialnumberDto.setProductName(customerInventoryMapping.getProduct().getName());
                        customerInventorySerialnumberDto.setItemId(item.getId());
                        customerInventorySerialnumberDto.setCustInventoryMappingId(customerInventoryMapping.getId());
                        if (customerInventoryMapping.getProduct().getProductCategory().isHasCas()) {
                            customerInventorySerialnumberDto.setDtvCategory(customerInventoryMapping.getProduct().getProductCategory().getDtvCategory());
                        }
                        customerInventorySerialnumberDtoList.add(customerInventorySerialnumberDto);
                    }
            );
            List<CustomerInventorySerialnumberDto> finalCustomerInventorySerialNumberDto = customerInventorySerialnumberDtoList.stream().sorted(Comparator.comparing(CustomerInventorySerialnumberDto::getCustInventoryMappingId).reversed()).collect(Collectors.toList());
            if (!finalCustomerInventorySerialNumberDto.isEmpty())
                finalCustomerInventorySerialNumberDto.get(0).setPrimary(true);
            return finalCustomerInventorySerialNumberDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Approve replace inventory generic data dto.
     * @param oldMacMappingId the old mac mapping id
     * @param newMacMappingId the new mac mapping id
     * @param billAble the bill able
     * @param isApproveRequest the is approve request
     * @param nextApprover the next approver
     * @return the generic data dto
     */
   @Transactional
    public GenericDataDTO approveReplaceInventory(Long oldMacMappingId, Long newMacMappingId, boolean billAble, boolean isApproveRequest, Integer nextApprover) {
//        System.out.println("Approve Replace Inventory Started");
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            InOutWardMACMapping oldinOutWardMACMapping = inOutWardMACService.getRepository().findById(oldMacMappingId).orElse(null);
            InOutWardMACMapping newinOutWardMACMapping = inOutWardMACService.getRepository().findById(newMacMappingId).orElse(null);
            CustomerInventoryMapping oldcustomersInventoryMapping = customerInventoryMappingRepo.findById(oldinOutWardMACMapping.getCustInventoryMappingId()).orElse(null);
            CustomerInventoryMappingDto entity = customerInventoryMappingMapper.domainToDTO(customerInventoryMappingRepo.findById(oldinOutWardMACMapping.getCustInventoryMappingId()).orElse(null), new CycleAvoidingMappingContext());
            Customers customers = customersRepository.findAllLightCustomerById(entity.getCustomerId());
            StaffUser loggedInUser = staffUserRepository.findLightStaffUserById(Integer.valueOf(getLoggedInUserId())).get();
            Long pcId = productRepository.findProductCategoryIdByProductId(entity.getProductId());
            ProductCategory productCategory = productCategoryRepository.findProductCategoryAttributesById(pcId);
            boolean hasmac = productCategory.isHasMac();
            boolean hasserial = productCategory.isHasSerial();
            Optional<Item> oldItem = itemRepository.findById(oldcustomersInventoryMapping.getItemId());
            Optional<Item> newItem = itemRepository.findById(newinOutWardMACMapping.getItemId());
            if (Objects.equals(loggedInUser.getUsername(), "admin") || Objects.equals(loggedInUser.getUsername(), "superadmin")) {
                entity.setNextApproverId(null);
                entity.setPreviousApproveId(getLoggedInUserId());
                entity.setTeamHierarchyMappingId(null);
                if (isApproveRequest) {
                    entity.setStatus("ACTIVE");
                    /** Called: Method Replace Old Inventory */
                    entity = customerInventoryMappingMapper.domainToDTO(replaceOldInventory(newMacMappingId, oldMacMappingId, billAble, isApproveRequest, oldcustomersInventoryMapping), new CycleAvoidingMappingContext());
                } else {
                    entity.setStatus("REJECTED");
                    /** Called: Method Replace Old Inventory */
                    entity = customerInventoryMappingMapper.domainToDTO(replaceOldInventory(newMacMappingId, oldMacMappingId, billAble, isApproveRequest, oldcustomersInventoryMapping), new CycleAvoidingMappingContext());
                }
                //To Set Plan Id
                if (oldcustomersInventoryMapping.getPlanId() != null) {
                    entity.setPlanId(oldcustomersInventoryMapping.getPlanId());
                    super.saveEntity(entity);

                }
                //maintain Replacemement History
                replacementHistory(oldcustomersInventoryMapping, entity, oldinOutWardMACMapping, newinOutWardMACMapping, (long) getLoggedInUserId());
                if (isApproveRequest) {
                    List<Long> deviceIds = networkDeviceRepository.findAllIdsByCustomerInventoryId(oldcustomersInventoryMapping.getId());
                    if (!deviceIds.isEmpty()) {
                        List<NetworkDeviceBind> deviceBindsToDelete = networkdeviceBindRepository.findByDeviceIds(deviceIds);
                        if (!deviceBindsToDelete.isEmpty()) {
                            updateNetworkDeviceBindAfterReplaceCustInventory(deviceBindsToDelete, entity);
                        }
                    }
                }
                genericDataDTO.setData(entity);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            }
            if (clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, newinOutWardMACMapping);
                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                    StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
//                    StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                    newinOutWardMACMapping.setCurrentApproveId(Integer.valueOf(map.get("staffId")));
                    newinOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    newinOutWardMACMapping.setStatus("PENDING");
//                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(newinOutWardMACMapping.getId()), newinOutWardMACMapping.getSerialNumber() == null ? "" : newinOutWardMACMapping.getSerialNumber(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(newinOutWardMACMapping.getId()), newinOutWardMACMapping.getSerialNumber() == null ? "" : newinOutWardMACMapping.getSerialNumber(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
                } else {
                    if (isApproveRequest) {
                        newinOutWardMACMapping.setCurrentApproveId(null);
                        newinOutWardMACMapping.setTeamHierarchyMappingId(null);
//                        newinOutWardMACMapping.setStatus("PENDING");
                        entity.setStatus("ACTIVE");
                        /** Called: Method Replace Old Inventory */
                        entity = customerInventoryMappingMapper.domainToDTO(replaceOldInventory(newMacMappingId, oldMacMappingId, billAble, isApproveRequest, oldcustomersInventoryMapping), new CycleAvoidingMappingContext());
                        /** Called: Method Replace History */
                        replacementHistory(oldcustomersInventoryMapping, entity, oldinOutWardMACMapping, newinOutWardMACMapping, newinOutWardMACMapping.getPreviousApproveId().longValue());
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
                        genericDataDTO.setData(super.saveEntity(entity));
                        List<Long> deviceIds = networkDeviceRepository.findAllIdsByCustomerInventoryId(oldcustomersInventoryMapping.getId());
                        if (!deviceIds.isEmpty()) {
                            List<NetworkDeviceBind> deviceBindsToDelete = networkdeviceBindRepository.findByDeviceIds(deviceIds);
                            if (!deviceBindsToDelete.isEmpty()) {
                                updateNetworkDeviceBindAfterReplaceCustInventory(deviceBindsToDelete, entity);
                            }
                        }
                    } else {
                        newinOutWardMACMapping.setCurrentApproveId(null);
                        newinOutWardMACMapping.setTeamHierarchyMappingId(null);
                        /** Called: Method Replace Old Inventory */
                        entity = customerInventoryMappingMapper.domainToDTO(replaceOldInventory(newMacMappingId, oldMacMappingId, billAble, isApproveRequest, oldcustomersInventoryMapping), new CycleAvoidingMappingContext());
                        /** Called: Method Replace History */
                        replacementHistory(oldcustomersInventoryMapping, entity, oldinOutWardMACMapping, newinOutWardMACMapping, newinOutWardMACMapping.getPreviousApproveId().longValue());
                        entity.setStatus("REJECTED");
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
                        genericDataDTO.setData(super.saveEntity(entity));
                    }
                    //Replacement Plan Inventory
                    if (oldcustomersInventoryMapping.getPlanId() != null) {
                        entity.setPlanId(oldcustomersInventoryMapping.getPlanId());
                        super.saveEntity(entity);

                    }
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(newinOutWardMACMapping.getId()), newinOutWardMACMapping.getSerialNumber() == null ? "" : newinOutWardMACMapping.getSerialNumber(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
                }
            } else {
                Map<String, Object> map = hierarchyService.getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, newinOutWardMACMapping);
                if (map.containsKey("assignableStaff")) {
                    genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                    workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(newinOutWardMACMapping.getId()), newinOutWardMACMapping.getSerialNumber() == null ? "" : newinOutWardMACMapping.getSerialNumber(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + loggedInUser.getUsername());
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(newinOutWardMACMapping.getId()), newinOutWardMACMapping.getSerialNumber() == null ? "" : newinOutWardMACMapping.getSerialNumber(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
                } else {
                    if (isApproveRequest) {
                        newinOutWardMACMapping.setCurrentApproveId(null);
                        newinOutWardMACMapping.setTeamHierarchyMappingId(null);
                        entity.setStatus("ACTIVE");
                        /** Called: Method Replace Old Inventory */
                        entity = customerInventoryMappingMapper.domainToDTO(replaceOldInventory(newMacMappingId, oldMacMappingId, billAble, isApproveRequest, oldcustomersInventoryMapping), new CycleAvoidingMappingContext());
                        /** Called: Method Replace History */
                        replacementHistory(oldcustomersInventoryMapping, entity, oldinOutWardMACMapping, newinOutWardMACMapping, newinOutWardMACMapping.getPreviousApproveId().longValue());
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
                        genericDataDTO.setData(super.saveEntity(entity));
                        List<Long> deviceIds = networkDeviceRepository.findAllIdsByCustomerInventoryId(oldcustomersInventoryMapping.getId());
                        if (!deviceIds.isEmpty()) {
                            List<NetworkDeviceBind> deviceBindsToDelete = networkdeviceBindRepository.findByDeviceIds(deviceIds);
                            if (!deviceBindsToDelete.isEmpty()) {
                                updateNetworkDeviceBindAfterReplaceCustInventory(deviceBindsToDelete, entity);
                            }
                        }
                    } else {
                        newinOutWardMACMapping.setCurrentApproveId(null);
                        newinOutWardMACMapping.setTeamHierarchyMappingId(null);
                        /** Called: Method Replace Old Inventory */
                        entity = customerInventoryMappingMapper.domainToDTO(replaceOldInventory(newMacMappingId, oldMacMappingId, billAble, isApproveRequest, oldcustomersInventoryMapping), new CycleAvoidingMappingContext());
                        /** Called: Method Replace History */
                        replacementHistory(oldcustomersInventoryMapping, entity, oldinOutWardMACMapping, newinOutWardMACMapping, newinOutWardMACMapping.getPreviousApproveId().longValue());
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        genericDataDTO.setData(super.saveEntity(entity));
                        entity.setPreviousApproveId(null);
                        genericDataDTO.setData(super.saveEntity(entity));
                    }
                    //Replace Plan Mapping
                    if (oldcustomersInventoryMapping.getPlanId() != null) {
                        entity.setPlanId(oldcustomersInventoryMapping.getPlanId());
                        super.saveEntity(entity);

                    }
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? (Integer) map.get("eventId") : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(newinOutWardMACMapping.getId()), newinOutWardMACMapping.getSerialNumber() == null ? "" : newinOutWardMACMapping.getSerialNumber(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
                }
                inOutWardMacRepo.save(newinOutWardMACMapping);
                genericDataDTO.setData(super.saveEntity(entity));
                if (!map.containsKey("assignableStaff") && isApproveRequest) {
                    CustomerInventoryMappingDto customerInventoryMappingDto = (CustomerInventoryMappingDto) genericDataDTO.getData();
                    boolean newItemAssemblyflag = customerInventoryMappingDto.isItemAssemblyflag();
                    Long newCustInventoryId = customerInventoryMappingDto.getId();
                    Long oldCustInventoryId = oldcustomersInventoryMapping.getId();
                    Long customerId = customerInventoryMappingDto.getCustomerId().longValue();
                    Long externalItem = customerInventoryMappingDto.getExternalItemId();
                    String status = customerInventoryMappingDto.getStatus();
                    String nmsEnable = clientServiceRepository.findValueByNameAndMvnoId(NMSIntegrationConstants.NMS_INTEGRATION.NMS_ENABLE, 1);
                    if (!newItemAssemblyflag &&
                            isApproveRequest &&
                            status.equalsIgnoreCase("ACTIVE") &&
                            externalItem == null &&
                            nmsEnable.equalsIgnoreCase(NMSIntegrationConstants.NMS_INTEGRATION.TRUE_FLAG)) {
                        if (hasserial || hasmac) {
                            sendCustomerInventoryToNMSIntegration(oldItem.get(), pcId, oldCustInventoryId, customers, NMSIntegrationConstants.NMS_INTEGRATION.DELETE_ONU_OPERATION);
                            sendCustomerInventoryToNMSIntegration(newItem.get(), pcId, newCustInventoryId, customers, NMSIntegrationConstants.NMS_INTEGRATION.ADD_ONU_OPERATION);
                        }
                    }
                }
            }
//            System.out.println("Approve Replace Inventory Ended");
            return genericDataDTO;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }

    private void updateNetworkDeviceBindAfterReplaceCustInventory(List<NetworkDeviceBind> deviceBindsToDelete, CustomerInventoryMappingDto entity) {
        for (NetworkDeviceBind networkDeviceBind : deviceBindsToDelete) {
            NetworkDevices newNetworkDevices = networkDeviceRepository.findByCustInventoryId(entity.getId());
            if (newNetworkDevices != null) {
                if (networkDeviceBind.getPortType().equalsIgnoreCase("IN")) {
                    networkDeviceBind.setCurrentDeviceId(newNetworkDevices.getId());
                    String newDevicePort = newNetworkDevices.getName() + networkDeviceBind.getCurrentDevicePort().substring(networkDeviceBind.getCurrentDevicePort().indexOf("-IN-Port-"));
                    networkDeviceBind.setCurrentDevicePort(newDevicePort);
                    String currentDeviceType = networkDeviceRepository.findDeviceTypeById(newNetworkDevices.getId());
                    Long currentProductId = networkDeviceRepository.findProductIdById(newNetworkDevices.getId());
                    String currentProductName = productRepository.findProductNameByProductId(currentProductId);
                    // For current device
                    String[] currentParts = networkDeviceBind.getCurrentDevicePort().split("Port", 2);
                    String setCurrentPortNumber = "Port" + currentParts[1];
                    networkDeviceBind.setCurrentDevicePortNumber(setCurrentPortNumber);
                    networkDeviceBind.setCurrentDevice(currentProductName);
                    networkDeviceBind.setCurrentDeviceType(currentDeviceType);
                    networkdeviceBindRepository.save(networkDeviceBind);
                } else {
                    networkDeviceBind.setOtherDeviceId(newNetworkDevices.getId());
                    String newDevicePort = newNetworkDevices.getName() + networkDeviceBind.getOtherDevicePort().substring(networkDeviceBind.getOtherDevicePort().indexOf("-IN-Port-"));
                    networkDeviceBind.setOtherDevicePort(newDevicePort);
                    String otherDeviceType = networkDeviceRepository.findDeviceTypeById(newNetworkDevices.getId());
                    Long otherProductId = networkDeviceRepository.findProductIdById(newNetworkDevices.getId());
                    String otherProductName = productRepository.findProductNameByProductId(otherProductId);
                    // For other device
                    String[] otherParts = networkDeviceBind.getOtherDevicePort().split("Port", 2);
                    String setOtherPortNumber = "Port" + otherParts[1];
                    networkDeviceBind.setOtherDevicePortNumber(setOtherPortNumber);
                    networkDeviceBind.setOtherDevice(otherProductName);
                    networkDeviceBind.setOtherDeviceType(otherDeviceType);
                    networkdeviceBindRepository.save(networkDeviceBind);
                }
            }
        }
    }

    /**
     * Replacement history.
     * @param oldCustomerInventoryMapping the old customer inventory mapping
     * @param newcustomerInventoryMapping the newcustomer inventory mapping
     * @param oldinOutWardMACMapping the oldin out ward mac mapping
     * @param newinOutWardMACMapping the newin out ward mac mapping
     * @param raisedrequestStaffId the raisedrequest staff id
     */
   @Transactional
    public void replacementHistory(CustomerInventoryMapping oldCustomerInventoryMapping, CustomerInventoryMappingDto newcustomerInventoryMapping, InOutWardMACMapping oldinOutWardMACMapping, InOutWardMACMapping newinOutWardMACMapping, Long raisedrequestStaffId) {
        try {
            ReplacementItemHistory replacementItemHistory = new ReplacementItemHistory();
            replacementItemHistory.setCustomerId(newcustomerInventoryMapping.getCustomerId().longValue());
            replacementItemHistory.setOldcustomerinventoryId(oldCustomerInventoryMapping.getId());
            replacementItemHistory.setNewcustomerinventoryId(newcustomerInventoryMapping.getId());
            replacementItemHistory.setRaisedrequeststaffId(raisedrequestStaffId);
            replacementItemHistory.setOlditemId(oldinOutWardMACMapping.getItemId());
            replacementItemHistory.setNewitemId(newinOutWardMACMapping.getItemId());
            replacementItemHistory.setOldmac(oldinOutWardMACMapping.getMacAddress());
            replacementItemHistory.setNewmac(newinOutWardMACMapping.getMacAddress());
            replacementItemHistory.setOldserialNumber(oldinOutWardMACMapping.getSerialNumber());
            replacementItemHistory.setNewserialNumber(newinOutWardMACMapping.getSerialNumber());
            replacementItemHistory.setStatus(newcustomerInventoryMapping.getStatus());
            replacementItemHistoryRepo.save(replacementItemHistory);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * Approve replace individual inventory generic data dto.
     * @param macMappingId the mac mapping id
     * @param billAble the bill able
     * @param isApproveRequest the is approve request
     * @return the generic data dto
     * @throws Exception the exception
     */
   @Transactional
    public GenericDataDTO approveReplaceIndividualInventory(Long macMappingId, boolean billAble, boolean isApproveRequest) throws Exception {
        try {
//            System.out.println("Approve Replace Individual Inventory Started");
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            StaffUser assignedUser = null;
            InOutWardMACMapping inOutWardMACMapping = inOutWardMACService.getRepository().findById(macMappingId).orElse(null);
            CustomerInventoryMapping customerInventoryMapping1123 = customerInventoryMappingRepo.findById(inOutWardMACMapping.getCustInventoryMappingId()).get();
            CustomerInventoryMappingDto entity = customerInventoryMappingMapper.domainToDTO(customerInventoryMapping1123, new CycleAvoidingMappingContext());
            Customers customers = customersRepository.findById(entity.getCustomerId()).get();
            ProductDto dto = productService.getEntityById(entity.getProductId());
            ProductCategory productCategory = productCategoryRepository.findProductCategoryAttributesById(dto.getProductCategory().getId());
            CustomerInventoryMapping newInventoryMapping = new CustomerInventoryMapping();
            Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).orElse(null);
            if (item.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.TEMPORARY) && isApproveRequest) {
                if (inOutWardMACMapping.getStatus().equalsIgnoreCase(CommonConstants.PENDING) && isApproveRequest) {
                    if (customerInventoryMapping1123.getReplacementReason().equalsIgnoreCase(CommonConstants.DEFECTIVE)) {
                        /** Called: Method Update Item Status Customer */
                        itemService.updateItemStatusForCustomer(item, CommonConstants.DEFECTIVE, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REPLACE_INVETORIES);
                    } else {
                        /** Called: Method Update Item Status Customer */
                        itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REPLACE_INVETORIES);
                    }
                    NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(item.getId(), entity.getId());
                    if (!Objects.isNull(networkDevices)) {
                        networkDevices.setIsDeleted(true);
                        networkDeviceRepository.save(networkDevices);
                    }
                    itemService.updateItemWarranty(item, "Paused");
                    if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                        //remover CustMacMapping Id
                        QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
                        BooleanExpression booleanExpression = qCustMacMappping.isNotNull();
                        booleanExpression = booleanExpression.and(qCustMacMappping.macAddress.eq(inOutWardMACMapping.getMacAddress()).and(qCustMacMappping.isDeleted.eq(false)));
                        Optional<CustMacMappping> custMacMappping = custMacMapppingRepository.findOne(booleanExpression);
                        if (custMacMappping.isPresent()) {
                            custMacMappping.get().setIsDeleted(true);
                            custMacMapppingRepository.save(custMacMappping.get());
                            //Send in Radius
                            deleteOldMacFromRadius(inOutWardMACMapping, customers);
                        }
                    }
                    inOutWardMACMapping.setCustInventoryMappingId(null);
                    inOutWardMacRepo.save(inOutWardMACMapping);
                    itemService.replaceAndreturnItemfromStaffremove(item, customers.getId());
                    if (getLoggedInUser().getPartnerId() != 1) {
                        QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                        BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(item.getId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(customers.getId()));
                        CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                        item.setOwnerType(CommonConstants.PARTNER);
                        Integer partnerIdByUserId = staffUserRepository.findPartnerIdByUserId(Integer.valueOf(customerInventoryMapping.getPreviousApproveId()));
                        item.setOwnerId(partnerIdByUserId.longValue());
                        customerInventoryMapping.setPreviousApproveId(null);
                        customerInventoryMappingRepo.save(customerInventoryMapping);
                        CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                    messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                        kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                    } else {
                        QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                        BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(item.getId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(customers.getId()));
                        CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                        item.setOwnerType(CommonConstants.STAFF);
                        item.setItemStatus(CommonConstants.STAFF_ALLOCATED);
                        item.setOwnerId(customerInventoryMapping.getPreviousApproveId().longValue());
                        customerInventoryMapping.setPreviousApproveId(null);
                        customerInventoryMappingRepo.save(customerInventoryMapping);
                        CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                    messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                        kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                    }
                    item.setIntransiantOwnership(null);
                    itemRepository.save(item);
                    ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                    kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                }
                if (inOutWardMACMapping.getStatus().equalsIgnoreCase("New") && isApproveRequest) {
                    item.setRemainingDays(String.valueOf(item.getIntransiantWarrenty()));
                    item.setIntransiantWarrenty(null);
                    item.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.TEMPORARY);
                    /** Called: Method Update Item Status Customer */
                    itemService.updateItemStatusForCustomer(item, CommonConstants.ALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.ASSIGN_INVETORIES);
                    item.setOwnerType(CommonConstants.CUSTOMER);
                    item.setOwnerId(customers.getId().longValue());
                    item.setWarranty(item.getIntransiantWarrentyStatus());
                    item.setExpireDate(item.getIntransiantexpireDate());
                    itemRepository.save(item);
                    ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                    kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                    CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(inOutWardMACMapping.getCustInventoryMappingId()).get();
                    CustomerInventoryMapping mapping = customerInventoryMapping;
                    //Add customerInvertory
                    newInventoryMapping = createCutomerInventory(item, customers, inOutWardMACMapping, mapping);
                    inOutWardMACMapping.setCustInventoryMappingId(newInventoryMapping.getId());
                    inOutWardMacRepo.save(inOutWardMACMapping);

                    //remove OldCustometInvetoryMapping Id
                    customerInventoryMapping.setIsDeleted(true);
                    if (mapping.getItemAssemblyId() != null) {
                        customerInventoryMapping.setItemAssemblyId(null);
                    }
                    customerInventoryMappingRepo.save(customerInventoryMapping);
                    CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                    kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));

                    //Add CustomerMac mapping
                    if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                        CustMacMappping newcustMacMappping = new CustMacMappping();
                        newcustMacMappping.setCustomer(customers);
                        newcustMacMappping.setMacAddress(item.getMacAddress());
                        custMacMapppingRepository.save(newcustMacMappping);
                        saveNewMacInRadius(inOutWardMACMapping, billAble, isApproveRequest);
                    }

                }
                item.setIntransiantWarrentyStatus(null);
                item.setIntransiantOwnership(null);
                item.setIntransiantWarrenty(null);
                item.setIntransiantexpireDate(null);
                itemRepository.save(item);
                ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//            messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
            } else {
                if (inOutWardMACMapping.getStatus().equalsIgnoreCase(CommonConstants.PENDING) && isApproveRequest) {
                    item.setIntransiantWarrenty(null);
                    item.setIntransiantOwnership(null);
                    item.setIntransiantWarrentyStatus(null);
                    item.setIntransiantexpireDate(null);
                    if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(item.getId(), entity.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }

                        if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                            //remove CustMacMapping Id
                            QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
                            BooleanExpression booleanExpression = qCustMacMappping.isNotNull();
                            booleanExpression = booleanExpression.and(qCustMacMappping.macAddress.equalsIgnoreCase(inOutWardMACMapping.getMacAddress()).and(qCustMacMappping.isDeleted.eq(false)));
                            Optional<CustMacMappping> custMacMappping = custMacMapppingRepository.findOne(booleanExpression);
                            if (custMacMappping.isPresent()) {
                                custMacMappping.get().setIsDeleted(true);
                                custMacMapppingRepository.save(custMacMappping.get());
                                //Send in Radius
                                deleteOldMacFromRadius(inOutWardMACMapping, customers);
                            }
                        }

                        inOutWardMACMapping.setCustInventoryMappingId(null);
                        inOutWardMacRepo.save(inOutWardMACMapping);
                        itemService.replaceAndreturnItemfromStaffremove(item, customers.getId());
                        itemService.updateItemWarranty(item, "Paused");
                        item.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
                        if (getLoggedInUser().getPartnerId() != 1) {
                            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(item.getId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(customers.getId()));
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                            item.setOwnerType(CommonConstants.PARTNER);
                            Integer partnerIdByUserId = staffUserRepository.findPartnerIdByUserId(Integer.valueOf(customerInventoryMapping.getPreviousApproveId()));
                            item.setOwnerId(partnerIdByUserId.longValue());
                            customerInventoryMapping.setPreviousApproveId(null);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                        } else {
                            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(item.getId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(customers.getId()));
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                            item.setOwnerType(CommonConstants.STAFF);
                            item.setItemStatus(CommonConstants.STAFF_ALLOCATED);
                            item.setOwnerId(customerInventoryMapping.getPreviousApproveId().longValue());
                            customerInventoryMapping.setPreviousApproveId(null);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                        }
                        if (customerInventoryMapping1123.getReplacementReason().equalsIgnoreCase(CommonConstants.DEFECTIVE)) {
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.DEFECTIVE, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REPLACE_INVETORIES);
                        } else {
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REPLACE_INVETORIES);
                        }
                        itemRepository.save(item);
                        ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                    messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                        kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                    }

                    if (item.getWarranty().equalsIgnoreCase("NotStarted")) {

                        if (customerInventoryMapping1123.getReplacementReason().equalsIgnoreCase(CommonConstants.DEFECTIVE)) {
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.DEFECTIVE, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REPLACE_INVETORIES);
                        } else {
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REPLACE_INVETORIES);
                        }
                        item.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(item.getId(), entity.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        inOutWardMACMapping.setCustInventoryMappingId(null);
                        inOutWardMacRepo.save(inOutWardMACMapping);
                        itemService.replaceAndreturnItemfromStaffremove(item, customers.getId());
                        if (getLoggedInUser().getPartnerId() != 1) {
                            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(item.getId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(customers.getId()));
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                            item.setOwnerType(CommonConstants.PARTNER);
                            Integer partnerIdByUserId = staffUserRepository.findPartnerIdByUserId(Integer.valueOf(customerInventoryMapping.getPreviousApproveId()));
                            item.setOwnerId(partnerIdByUserId.longValue());
                            customerInventoryMapping.setPreviousApproveId(null);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                        } else {
                            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(item.getId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(customers.getId()));
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                            item.setOwnerType(CommonConstants.STAFF);
                            item.setItemStatus(CommonConstants.STAFF_ALLOCATED);
                            item.setOwnerId(customerInventoryMapping.getPreviousApproveId().longValue());
                            customerInventoryMapping.setPreviousApproveId(null);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                        }
                        itemRepository.save(item);
                        ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                    messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                        kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                        if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                            //remover CustMacMapping Id
                            QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
                            BooleanExpression booleanExpression = qCustMacMappping.isNotNull();
//                        booleanExpression = booleanExpression.and(qCustMacMappping.macAddress.isNull().or(qCustMacMappping.macAddress.eq(inOutWardMACMapping.getMacAddress()).and(qCustMacMappping.isDeleted.eq(false))).or(null));
//                        Optional<CustMacMappping> custMacMappping = custMacMapppingRepository.findOne(booleanExpression);
                            booleanExpression = booleanExpression.and(qCustMacMappping.macAddress.equalsIgnoreCase(inOutWardMACMapping.getMacAddress()).and(qCustMacMappping.isDeleted.eq(false)));
                            Optional<CustMacMappping> custMacMappping = custMacMapppingRepository.findOne(booleanExpression);
                            if (custMacMappping.isPresent()) {
                                custMacMappping.get().setIsDeleted(true);
                                custMacMapppingRepository.save(custMacMappping.get());
                                //Send in Radius
                                deleteOldMacFromRadius(inOutWardMACMapping, customers);
                            }
                        }
                    }
                    if (item.getWarranty().equalsIgnoreCase("NoWarranty")) {
                        if (customerInventoryMapping1123.getReplacementReason().equalsIgnoreCase(CommonConstants.DEFECTIVE)) {
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.DEFECTIVE, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REPLACE_INVETORIES);
                        } else {
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REPLACE_INVETORIES);
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(item.getId(), entity.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        item.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
                        if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                            //remove CustMacMapping Id
                            QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
                            BooleanExpression booleanExpression = qCustMacMappping.isNotNull();
                            booleanExpression = booleanExpression.and(qCustMacMappping.macAddress.equalsIgnoreCase(inOutWardMACMapping.getMacAddress()).and(qCustMacMappping.isDeleted.eq(false)));
                            Optional<CustMacMappping> custMacMappping = custMacMapppingRepository.findOne(booleanExpression);
                            if (custMacMappping.isPresent()) {
                                custMacMappping.get().setIsDeleted(true);
                                custMacMapppingRepository.save(custMacMappping.get());
                                //Send in Radius
                                deleteOldMacFromRadius(inOutWardMACMapping, customers);
                            }
                        }
                        inOutWardMACMapping.setCustInventoryMappingId(null);
                        inOutWardMacRepo.save(inOutWardMACMapping);
                        itemService.replaceAndreturnItemfromStaffremove(item, customers.getId());
                        if (getLoggedInUser().getPartnerId() != 1) {
                            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(item.getId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(customers.getId())).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS));;
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                            item.setOwnerType(CommonConstants.PARTNER);
                            StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(customerInventoryMapping.getPreviousApproveId())).get();
                            item.setOwnerId(staffUser.getPartnerid().longValue());
                            customerInventoryMapping.setPreviousApproveId(null);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                        } else {
                            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(item.getId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(customers.getId())).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS));
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                            item.setOwnerType(CommonConstants.STAFF);
                            item.setItemStatus(CommonConstants.STAFF_ALLOCATED);
                            item.setOwnerId(customerInventoryMapping.getPreviousApproveId().longValue());
                            customerInventoryMapping.setPreviousApproveId(null);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                        }
                        itemRepository.save(item);
                        ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                    messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                        kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                    }
                    if (item.getWarranty().equalsIgnoreCase("Expired")) {
                        if (customerInventoryMapping1123.getReplacementReason().equalsIgnoreCase(CommonConstants.DEFECTIVE)) {
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.DEFECTIVE, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REPLACE_INVETORIES);
                        } else {
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.UNALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.REPLACE_INVETORIES);
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(item.getId(), entity.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        item.setOwnershipType(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED);
                        if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                            //remove CustMacMapping Id
                            QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
                            BooleanExpression booleanExpression = qCustMacMappping.isNotNull();
                            booleanExpression = booleanExpression.and(qCustMacMappping.macAddress.eq(inOutWardMACMapping.getMacAddress()).and(qCustMacMappping.isDeleted.eq(false)));
                            Optional<CustMacMappping> custMacMappping = custMacMapppingRepository.findOne(booleanExpression);
                            if (custMacMappping.isPresent()) {
                                custMacMappping.get().setIsDeleted(true);
                                custMacMapppingRepository.save(custMacMappping.get());
                                //Send in Radius
                                deleteOldMacFromRadius(inOutWardMACMapping, customers);
                            }
                        }
                        inOutWardMACMapping.setCustInventoryMappingId(null);
                        inOutWardMacRepo.save(inOutWardMACMapping);
                        itemService.replaceAndreturnItemfromStaffremove(item, customers.getId());
                        if (getLoggedInUser().getPartnerId() != 1) {
                            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(item.getId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(customers.getId()));
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                            item.setOwnerType(CommonConstants.PARTNER);
                            StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(customerInventoryMapping.getPreviousApproveId())).get();
                            item.setOwnerId(staffUser.getPartnerid().longValue());
                            customerInventoryMapping.setPreviousApproveId(null);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                        } else {
                            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(item.getId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.customer.id.eq(customers.getId()));
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                            item.setOwnerType(CommonConstants.STAFF);
                            item.setItemStatus(CommonConstants.STAFF_ALLOCATED);
                            item.setOwnerId(customerInventoryMapping.getPreviousApproveId().longValue());
                            customerInventoryMapping.setPreviousApproveId(null);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                        }
                        itemRepository.save(item);
                        ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                    messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                        kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                    }
                    customerInventoryMappingRepo.save(customerInventoryMapping1123);
                }
                if (inOutWardMACMapping.getStatus().equalsIgnoreCase("New") && isApproveRequest) {
                    if (item.getWarranty() != null) {
                        if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                            item.setRemainingDays(String.valueOf(item.getIntransiantWarrenty()));
                            if (item.getIntransiantOwnership().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.TEMPORARY)) {
                                item.setOwnershipType("Sold");
                                item.setIntransiantOwnership(null);
                            } else {
                                item.setOwnershipType(item.getIntransiantOwnership());
                                item.setIntransiantOwnership(null);
                            }
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.ALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.ASSIGN_INVETORIES);
                            item.setOwnerType(CommonConstants.CUSTOMER);
                            item.setOwnerId(customers.getId().longValue());
                            item.setWarranty("InWarranty");
                            item.setIntransiantWarrentyStatus(null);
                            item.setIntransiantWarrenty(null);
                            item.setExpireDate(item.getIntransiantexpireDate());
                            item.setIntransiantexpireDate(null);
                            List<ItemWarrantyMapping> itemWarrantyMappings = itemWarrantyMappingRepository.findByItemId(item.getId());
                            if (!itemWarrantyMappings.isEmpty()) {
                                itemWarrantyMappings.forEach(itemWarrantyMapping -> {
                                    itemWarrantyMapping.setWarranty("InWarranty");
                                    itemWarrantyMappingRepository.save(itemWarrantyMapping);
                                });
                            } else {
                                ItemWarrantyMappingDto itemWarrantyMappingDto = new ItemWarrantyMappingDto();
                                itemWarrantyMappingDto.setWarranty("InWarranty");
                                itemWarrantyMappingDto.setItemId(item.getId());
                                itemWarrantyMappingService.saveEntity(itemWarrantyMappingDto);
                            }
                            itemRepository.save(item);
                            ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                        messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                            kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(inOutWardMACMapping.getCustInventoryMappingId()).get();
                            CustomerInventoryMapping mapping = customerInventoryMapping;
                            //Add customerInvertory
                            newInventoryMapping = createCutomerInventory(item, customers, inOutWardMACMapping, mapping);
                            inOutWardMACMapping.setCustInventoryMappingId(newInventoryMapping.getId());
                            inOutWardMacRepo.save(inOutWardMACMapping);
                            if (productCategory.isHasMac() || productCategory.isHasSerial()) {
                                /** Called: Method Create Network Device */
                                createNetworkDevice(dto, customers, inOutWardMACMapping.getSerialNumber(), newInventoryMapping.getInwardId(), newInventoryMapping.getId(), newInventoryMapping.getItemId());
                            }
                            //remove OldCustometInvetoryMapping Id
                            if (customerInventoryMapping.getItemAssemblyId() != null) {
                                customerInventoryMapping.setItemAssemblyId(null);
                            }
                            customerInventoryMapping.setIsDeleted(true);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                            //Add CustomerMac mapping
                            if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                                CustMacMappping newcustMacMappping = new CustMacMappping();
                                newcustMacMappping.setCustomer(customers);
                                newcustMacMappping.setMacAddress(item.getMacAddress());
                                custMacMapppingRepository.save(newcustMacMappping);
                                saveNewMacInRadius(inOutWardMACMapping, billAble, isApproveRequest);
                            }
                        }
                    }
                    if (item.getWarranty() != null && item.getWarranty().equalsIgnoreCase("NoWarranty")) {
//                        item.setRemainingDays(String.valueOf(item.getIntransiantWarrenty()));
                        /** Called: Method Update Item Status Customer */
                        itemService.updateItemStatusForCustomer(item, CommonConstants.ALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.ASSIGN_INVETORIES);
                        item.setOwnershipType(item.getIntransiantOwnership());
                        item.setOwnerType(CommonConstants.CUSTOMER);
                        item.setOwnerId(customers.getId().longValue());
                        itemRepository.save(item);
                        ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                        messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                        kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                        CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(inOutWardMACMapping.getCustInventoryMappingId()).get();
                        CustomerInventoryMapping mapping = customerInventoryMapping;
                        //Add customerInvertory
                        newInventoryMapping = createCutomerInventory(item, customers, inOutWardMACMapping, mapping);
                        inOutWardMACMapping.setCustInventoryMappingId(newInventoryMapping.getId());
                        inOutWardMacRepo.save(inOutWardMACMapping);
                        if (productCategory.isHasMac() || productCategory.isHasSerial()) {
                            /** Called: Method Create Network Device */
                            createNetworkDevice(dto, customers, inOutWardMACMapping.getSerialNumber(), newInventoryMapping.getInwardId(), newInventoryMapping.getId(), newInventoryMapping.getItemId());
                        }
                        //remove OldCustometInvetoryMapping Id
                        if (customerInventoryMapping.getItemAssemblyId() != null) {
                            customerInventoryMapping.setItemAssemblyId(null);
                        }
                        customerInventoryMapping.setIsDeleted(true);
                        customerInventoryMappingRepo.save(customerInventoryMapping);
                        CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                        kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                        //Add CustomerMac mapping
                        if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                            CustMacMappping newcustMacMappping = new CustMacMappping();
                            newcustMacMappping.setCustomer(customers);
                            newcustMacMappping.setMacAddress(item.getMacAddress());
                            custMacMapppingRepository.save(newcustMacMappping);
                            saveNewMacInRadius(inOutWardMACMapping, billAble, isApproveRequest);
                        }
                    }
                    if (item.getWarranty() != null) {
                        if (item.getWarranty().equalsIgnoreCase("NotStarted")) {
                            item.setRemainingDays(String.valueOf(item.getIntransiantWarrenty()));
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.ALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.ASSIGN_INVETORIES);
                            item.setOwnershipType(item.getIntransiantOwnership());
                            item.setOwnerType(CommonConstants.CUSTOMER);
                            item.setOwnerId(customers.getId().longValue());
                            if (item.getIntransiantexpireDate() != null) {
                                item.setExpireDate(item.getIntransiantexpireDate());
                                item.setIntransiantexpireDate(null);
                            }
                            item.setIntransiantOwnership(null);
                            item.setIntransiantWarrenty(null);
                            item.setIntransiantWarrentyStatus(null);
                            item.setWarranty("InWarranty");
                            List<ItemWarrantyMapping> itemWarrantyMappings = itemWarrantyMappingRepository.findByItemId(item.getId());
                            if (!itemWarrantyMappings.isEmpty()) {
                                itemWarrantyMappings.forEach(itemWarrantyMapping -> {
                                    itemWarrantyMapping.setWarranty("InWarranty");
                                    itemWarrantyMappingRepository.save(itemWarrantyMapping);
                                });
                            } else {
                                ItemWarrantyMappingDto itemWarrantyMappingDto = new ItemWarrantyMappingDto();
                                itemWarrantyMappingDto.setWarranty("InWarranty");
                                itemWarrantyMappingDto.setItemId(item.getId());
                                itemWarrantyMappingService.saveEntity(itemWarrantyMappingDto);
                            }
                            itemRepository.save(item);
                            ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                        messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                            kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(inOutWardMACMapping.getCustInventoryMappingId()).get();
                            CustomerInventoryMapping mapping = customerInventoryMapping;
                            //Add customerInvertory
                            newInventoryMapping = createCutomerInventory(item, customers, inOutWardMACMapping, mapping);
                            inOutWardMACMapping.setCustInventoryMappingId(newInventoryMapping.getId());
                            inOutWardMacRepo.save(inOutWardMACMapping);
                            if (productCategory.isHasMac() || productCategory.isHasSerial()) {
                                /** Called: Method Create Network Device */
                                createNetworkDevice(dto, customers, inOutWardMACMapping.getSerialNumber(), newInventoryMapping.getInwardId(), newInventoryMapping.getId(), newInventoryMapping.getItemId());
                            }
                            //remove OldCustometInvetoryMapping Id
                            if (customerInventoryMapping.getItemAssemblyId() != null) {
                                customerInventoryMapping.setItemAssemblyId(null);
                            }
                            customerInventoryMapping.setIsDeleted(true);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                            //Add CustomerMac mapping
                            if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                                CustMacMappping newcustMacMappping = new CustMacMappping();
                                newcustMacMappping.setCustomer(customers);
                                newcustMacMappping.setMacAddress(item.getMacAddress());
                                custMacMapppingRepository.save(newcustMacMappping);
                                saveNewMacInRadius(inOutWardMACMapping, billAble, isApproveRequest);
                            }
                        }
                    }
                    if (item.getWarranty() != null) {
                        if (item.getWarranty().equalsIgnoreCase("Expired")) {
                            item.setRemainingDays(String.valueOf(item.getIntransiantWarrenty()));
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.ALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.ASSIGN_INVETORIES);
                            item.setOwnershipType(item.getIntransiantOwnership());
                            item.setOwnerType(CommonConstants.CUSTOMER);
                            item.setOwnerId(customers.getId().longValue());
                            itemRepository.save(item);
                            ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                        messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                            kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                            //remove OldCustometInvetoryMapping Id
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(inOutWardMACMapping.getCustInventoryMappingId()).get();
                            CustomerInventoryMapping mapping = customerInventoryMapping;
                            //Add customerInvertory
                            newInventoryMapping = createCutomerInventory(item, customers, inOutWardMACMapping, mapping);
                            inOutWardMACMapping.setCustInventoryMappingId(newInventoryMapping.getId());
                            inOutWardMacRepo.save(inOutWardMACMapping);
                            if (productCategory.isHasMac() || productCategory.isHasSerial()) {
                                /** Called: Method Create Network Device */
                                createNetworkDevice(dto, customers, inOutWardMACMapping.getSerialNumber(), newInventoryMapping.getInwardId(), newInventoryMapping.getId(), newInventoryMapping.getItemId());
                            }
                            //remove OldCustometInvetoryMapping Id
                            if (customerInventoryMapping.getItemAssemblyId() != null) {
                                customerInventoryMapping.setItemAssemblyId(null);
                            }
                            customerInventoryMapping.setIsDeleted(true);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                            //Add CustomerMac mapping
                            if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                                CustMacMappping newcustMacMappping = new CustMacMappping();
                                newcustMacMappping.setCustomer(customers);
                                newcustMacMappping.setMacAddress(item.getMacAddress());
                                custMacMapppingRepository.save(newcustMacMappping);
                                saveNewMacInRadius(inOutWardMACMapping, billAble, isApproveRequest);
                            }

                        }
                    }
                    if (item.getWarranty() != null) {
                        if (item.getWarranty().equalsIgnoreCase("Paused")) {
                            item.setRemainingDays(String.valueOf(item.getIntransiantWarrenty()));
                            /** Called: Method Update Item Status Customer */
                            itemService.updateItemStatusForCustomer(item, CommonConstants.ALLOCATED, LocalDateTime.now(), customers.getId().longValue(), CommonConstants.ASSIGN_INVETORIES);
                            item.setOwnershipType(item.getIntransiantOwnership());
                            item.setOwnerType(CommonConstants.CUSTOMER);
                            item.setOwnerId(customers.getId().longValue());
                            item.setWarranty("InWarranty");
                            item.setIntransiantWarrentyStatus(null);
                            item.setIntransiantWarrenty(null);
                            item.setExpireDate(item.getIntransiantexpireDate());
                            item.setIntransiantexpireDate(null);
                            itemRepository.save(item);
                            List<ItemWarrantyMapping> itemWarrantyMappings = itemWarrantyMappingRepository.findByItemId(item.getId());
                            if (!itemWarrantyMappings.isEmpty()) {
                                itemWarrantyMappings.forEach(itemWarrantyMapping -> {
                                    itemWarrantyMapping.setWarranty("InWarranty");
                                    itemWarrantyMappingRepository.save(itemWarrantyMapping);
                                });
                            } else {
                                ItemWarrantyMappingDto itemWarrantyMappingDto = new ItemWarrantyMappingDto();
                                itemWarrantyMappingDto.setWarranty("InWarranty");
                                itemWarrantyMappingDto.setItemId(item.getId());
                                itemWarrantyMappingService.saveEntity(itemWarrantyMappingDto);
                            }
                            ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                        messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                            kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));
                            //remove OldCustometInvetoryMapping Id
                            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(inOutWardMACMapping.getCustInventoryMappingId()).get();
                            CustomerInventoryMapping mapping = customerInventoryMapping;
                            //Add customerInvertory
                            newInventoryMapping = createCutomerInventory(item, customers, inOutWardMACMapping, mapping);
                            inOutWardMACMapping.setCustInventoryMappingId(newInventoryMapping.getId());
                            inOutWardMacRepo.save(inOutWardMACMapping);
                            if (productCategory.isHasMac() || productCategory.isHasSerial()) {
                                /** Called: Method Create Network Device */
                                createNetworkDevice(dto, customers, inOutWardMACMapping.getSerialNumber(), newInventoryMapping.getInwardId(), newInventoryMapping.getId(), newInventoryMapping.getItemId());
                            }
                            //remove OldCustometInvetoryMapping Id
                            if (customerInventoryMapping.getItemAssemblyId() != null) {
                                customerInventoryMapping.setItemAssemblyId(null);
                            }
                            customerInventoryMapping.setIsDeleted(true);
                            customerInventoryMappingRepo.save(customerInventoryMapping);
                            CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                            kafkaMessageSender.send(new KafkaMessageData(message, CustomerInventoryMappingMessage.class.getSimpleName()));
                            //Add CustomerMac mapping
                            if (dto.getCaseId() == null && inOutWardMACMapping.getMacAddress() != null) {
                                CustMacMappping newcustMacMappping = new CustMacMappping();
                                newcustMacMappping.setCustomer(customers);
                                newcustMacMappping.setMacAddress(item.getMacAddress());
                                custMacMapppingRepository.save(newcustMacMappping);
                                saveNewMacInRadius(inOutWardMACMapping, billAble, isApproveRequest);
                            }
                        }
                    }
                }
            }
//            List<InOutWardMACMapping> inOutWardMACMappingList = entity.getInOutWardMACMapping();
//            List<Long> id = inOutWardMACMappingList.stream().map(InOutWardMACMapping::getItemId).collect(Collectors.toList());
//            List<Item> itemList = itemRepository.findAllByIdIn(id);
/*
        if (!isApproveRequest == true) {
            for (int i = 0; i <= itemList.size() - 1; i++) {
                if (customers.getIstrialplan()) {
                    if ((itemList.get(i).getId().equals(id.get(i)))) {

                        if (((itemList.get(i).getItemStatus().equalsIgnoreCase(CommonConstants.UNALLOCATED)) || (itemList.get(i).getItemStatus().equalsIgnoreCase(CommonConstants.RETURNED))) && (inOutWardMACMappingList.get(i).getStatus().equalsIgnoreCase(CommonConstants.NEW))) {
                            itemList.get(i).setItemStatus(CommonConstants.ALLOCATED);
                        } else if (itemList.get(i).getItemStatus().equalsIgnoreCase(CommonConstants.ALLOCATED) && (inOutWardMACMappingList.get(i).getStatus().equalsIgnoreCase(CommonConstants.TICKET_STATUS.PENDING))) {
                            itemList.get(i).setItemStatus(CommonConstants.RETURNED);
                        }
                        itemRepository.save(itemList.get(i));
                    }
                }
            }
        }
        if (isApproveRequest == false) {
            for (int i = 0; i <= itemList.size() - 1; i++) {

                if ((itemList.get(i).getId().equals(id.get(i)))) {
                    itemList.get(i).setItemStatus(CommonConstants.UNALLOCATED);
                    itemRepository.save(itemList.get(i));
                }
            }
        }*/


//        if (!billAble) {
//            inOutWardMACMapping.setCustInventoryMappingId(null);
//            if(inOutWardMACMapping.getMacAddress() != null) {
//                custMacMapppingService.deleteByMacAddress(inOutWardMACMapping.getMacAddress(), customers.getId());
//            }
//        }

//            if (Objects.equals(inOutWardMACMapping.getStatus(), "ACTIVE") && billAble && entity.getExpiryDateTime().isBefore(LocalDateTime.now()) && dto.getRefurburshiedProductCharge() != null) {
            /*Charge charge = chargeService.get(dto.getChargeId());
            Double applicableAmount = charge.getPrice();
            Double tax = 0.0;
            for (int k = 0; k < charge.getTax().getTieredList().size(); k++) {
                tax = tax + applicableAmount * charge.getTax().getTieredList().get(k).getRate() / 100.0;
                applicableAmount += applicableAmount * charge.getTax().getTieredList().get(k).getRate() / 100.0;
            }*/
//                String itemConditionByItemId = itemRepository.findItemConditionByItemId(entity.getItemId());
//                Long itemId = null;
//                String itemCondition = null;
//                if (itemConditionByItemId != null) {
//                    itemId = entity.getItemId();
//                    itemCondition = itemConditionByItemId;
//                }

//            Runnable chargeRunnable = new ChargeThread(customers.getId(), customersService, entity.getId(), itemId, itemCondition, null, null);
//            Thread billChargeThread = new Thread(chargeRunnable);
//            billChargeThread.start();

            /*CustomerDBR dbr = new CustomerDBR();
            dbr.setCustid(customers.getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(applicableAmount);
            dbr.setPendingamt(0.0);
            dbr.setCustname(customers.getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(customers.getCusttype());
            customerDBRRepository.save(dbr);*/
//            }
            //  genericDataDTO.setData(inOutWardMACService.getRepository().save(inOutWardMACMapping));
            genericDataDTO.setData(newInventoryMapping);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            System.out.println("Approve Replace Individual Inventory Ended");
            return genericDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

//    public void eazyBillReplacement(CasMaster oldCasMaster, Item oldstbNumber, Item newstbNumber, Item oldCardNumber, Item newCardNumber, CustomerInventoryMapping customerInventoryMapping) {
//
//        try {

    /**
     * /                    if (customerInventoryMapping.getItemAssemblyId() != null) { @param oldstbId the oldstb id
     * @param newStb the new stb
     * @param oldCard the old card
     * @param newCard the new card
     */
    /// /            EzBillServiceUtility ezBillService = new EzBillServiceUtility();
//            //PairReplacement
//            if (oldstbNumber != null && newstbNumber != null && oldCardNumber != null && newCardNumber != null && customerInventoryMapping != null) {
//
//                ezBillService.getPairedInfo(oldCasMaster, newstbNumber.getSerialNumber(), newCardNumber.getSerialNumber());
//

//
//                if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Defective")) {
//                    ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 1, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                } else if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Upgrade")) {
//                    ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 2, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                } else if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Surrender")) {
//                    ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 4, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                } else if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Others")) {
//                    ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 6, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                }
//                ezBillService.getUnPairedInfoResponse(oldCasMaster, oldstbNumber.getSerialNumber());
//
//
//                //   ezBillServiceUtility.getPairedInfoResponse(newstbNumber.toString(), newCardNumber.toString(), customerInventoryMapping.getConnectionNo(), customerInventoryMapping.getCustomer(), newCasMaster);

//            }
//            if (oldstbNumber != null && newstbNumber != null && oldCardNumber == null && newCardNumber == null) {
//
//
//                if (customerInventoryMapping.getItemAssemblyId() == null) {
//
//                    if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Defective")) {
//                        ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 1, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                    } else if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Upgrade")) {
//                        ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 2, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                    } else if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Surrender")) {
//                        ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 4, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                    } else if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Others")) {
//                        ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 6, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                    }
//                }
//            }
//
//        } catch (CustomValidationException exception) {
//            throw new RuntimeException(exception.getMessage());
//        } catch (Exception exception) {
//            throw new RuntimeException(exception.getMessage());
//        }
//    }
    public void eazyBuildReplacement(Long oldstbId, Long newStb, Long oldCard, Long newCard) {

        try {
//            EzBillServiceUtility ezBillService = new EzBillServiceUtility();
            //PairReplacement
            if (oldstbId != null && newStb != null && oldCard != null && newCard != null) {
                //STBItems
                InOutWardMACMapping oldStbmapping = inOutWardMacRepo.findById(oldstbId).orElse(null);
                InOutWardMACMapping newStbmapping = inOutWardMacRepo.findById(newStb).orElse(null);

                //CardItems
                InOutWardMACMapping oldCardMapping = inOutWardMacRepo.findById(oldCard).orElse(null);
                InOutWardMACMapping newCardmappingMapping = inOutWardMacRepo.findById(newCard).orElse(null);


                CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(oldStbmapping.getCustInventoryMappingId()).orElse(null);
                if (oldStbmapping != null && newStbmapping != null && oldCard != null && newCard != null && customerInventoryMapping != null) {

                    //STBItems
                    Item oldstbNumber = itemRepository.findById(oldStbmapping.getItemId()).orElse(null);
                    Item newstbNumber = itemRepository.findById(newStbmapping.getItemId()).orElse(null);

                    //OLDCardItems
                    Item oldCardNumber = itemRepository.findById(oldCardMapping.getItemId()).orElse(null);
                    Item newCardNumber = itemRepository.findById(newCardmappingMapping.getItemId()).orElse(null);

                    ProductCategory productCategory = productCategoryRepository.findById(productRepository.findById(oldstbNumber.getProductId()).get().getProductCategory().getId()).orElse(null);
                    CasMaster oldCasMaster = casMasterRepo.findById(productRepository.findById(oldstbNumber.getProductId()).get().getCaseId()).get();
                    CasMaster newCasMaster = casMasterRepo.findById(productRepository.findById(newstbNumber.getProductId()).get().getCaseId()).get();

//                    ezBillService.getPairedInfo(oldCasMaster, newstbNumber.getSerialNumber(), newCardNumber.getSerialNumber());
//
//                    if (productCategory.getDtvCategory().equalsIgnoreCase("STB") && customerInventoryMapping.getItemAssemblyId() != null) {
//
//                        if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Defective")) {
//                            ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 1, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                        } else if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Upgrade")) {
//                            ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 2, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                        } else if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Surrender")) {
//                            ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 4, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                        } else if (customerInventoryMapping.getReplacementReason().equalsIgnoreCase("Others")) {
//                            ezBillService.replaceSetupBox(oldCasMaster, newstbNumber.getSerialNumber(), oldstbNumber.getSerialNumber(), 6, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
//                        }
//                        ezBillService.getUnPairedInfoResponse(oldCasMaster, oldstbNumber.getSerialNumber());
//
//
//                        //   ezBillServiceUtility.getPairedInfoResponse(newstbNumber.toString(), newCardNumber.toString(), customerInventoryMapping.getConnectionNo(), customerInventoryMapping.getCustomer(), newCasMaster);
//                    }
                }
            }
            if (oldstbId != null && newStb != null && oldCard == null && newCard == null) {


                InOutWardMACMapping oldStbmappingId = inOutWardMacRepo.findById(oldstbId).orElse(null);
                InOutWardMACMapping newStbMappingId = inOutWardMacRepo.findById(newStb).orElse(null);

                Item oldStbNumber = itemRepository.findById(oldStbmappingId.getItemId()).orElse(null);
                Item newStbNumber = itemRepository.findById(newStbMappingId.getItemId()).orElse(null);


                CustomerInventoryMapping inventoryMapping = customerInventoryMappingRepo.findById(oldStbmappingId.getCustInventoryMappingId()).orElse(null);
//                if (inventoryMapping.getItemAssemblyId() == null) {
//                    CasMaster casMaster = casMasterRepo.findById(inventoryMapping.getProduct().getCaseId()).get();
//                    if (inventoryMapping.getReplacementReason().equalsIgnoreCase("Defective")) {
//                        ezBillService.replaceSetupBox(casMaster, newStbNumber.getSerialNumber(), oldStbNumber.getSerialNumber(), 1, inventoryMapping.getConnectionNo(), inventoryMapping);
//                    } else if (inventoryMapping.getReplacementReason().equalsIgnoreCase("Upgrade")) {
//                        ezBillService.replaceSetupBox(casMaster, newStbNumber.getSerialNumber(), oldStbNumber.getSerialNumber(), 2, inventoryMapping.getConnectionNo(), inventoryMapping);
//                    } else if (inventoryMapping.getReplacementReason().equalsIgnoreCase("Surrender")) {
//                        ezBillService.replaceSetupBox(casMaster, newStbNumber.getSerialNumber(), oldStbNumber.getSerialNumber(), 4, inventoryMapping.getConnectionNo(), inventoryMapping);
//                    } else if (inventoryMapping.getReplacementReason().equalsIgnoreCase("Others")) {
//                        ezBillService.replaceSetupBox(casMaster, newStbNumber.getSerialNumber(), oldStbNumber.getSerialNumber(), 6, inventoryMapping.getConnectionNo(), inventoryMapping);
//                    }
//                }
            }

        } catch (CustomValidationException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }


    /**
     * Create cutomer inventory customer inventory mapping.
     * @param item the item
     * @param customers the customers
     * @param inOutWardMACMapping the in out ward mac mapping
     * @param mapping the mapping
     * @return the customer inventory mapping
     */
    @Transactional
    CustomerInventoryMapping createCutomerInventory(Item item, Customers customers, InOutWardMACMapping inOutWardMACMapping, CustomerInventoryMapping mapping) {
        try {
            CustomerInventoryMapping newInventoryMapping = new CustomerInventoryMapping();
            newInventoryMapping.setAssignedDateTime(LocalDateTime.now());
            newInventoryMapping.setItemId(item.getId());
            newInventoryMapping.setProduct(productRepository.findById(item.getProductId()).get());
            newInventoryMapping.setStaff(staffUserRepository.findById(Integer.valueOf(getLoggedInUserId())).get());
            newInventoryMapping.setInwardId(inOutWardMACMapping.getInwardId());
            newInventoryMapping.setExpiryDateTime(mapping.getExpiryDateTime());
            newInventoryMapping.setServiceId(mapping.getServiceId());
            newInventoryMapping.setCustomer(customers);
            newInventoryMapping.setIsDeleted(false);
            newInventoryMapping.setQty(mapping.getQty());
            newInventoryMapping.setCustPackId(mapping.getCustPackId());
            newInventoryMapping.setExternalItemId(mapping.getExternalItemId());
            newInventoryMapping.setNextApprover(mapping.getNextApprover());
            newInventoryMapping.setPreviousApproveId(mapping.getPreviousApproveId());
            newInventoryMapping.setStatus(mapping.getStatus());
            newInventoryMapping.setReplacementReason(mapping.getReplacementReason());
            newInventoryMapping.setPlanId(mapping.getPlanId());
            newInventoryMapping.setMapping_ref_id(mapping.getMapping_ref_id());
            newInventoryMapping.setApprovalRemark(mapping.getApprovalRemark());
            newInventoryMapping.setPlanGroupId(mapping.getPlanGroupId());
            newInventoryMapping.setOfferPrice(mapping.getOfferPrice());
            newInventoryMapping.setChargeId(mapping.getChargeId());
            newInventoryMapping.setBillTo(mapping.getBillTo());
            newInventoryMapping.setIsInvoiceToOrg(mapping.getIsInvoiceToOrg());
            newInventoryMapping.setNewAmount(mapping.getNewAmount());
            newInventoryMapping.setDiscount(mapping.getDiscount());
            newInventoryMapping.setIsRequiredApproval(mapping.getIsRequiredApproval());
            newInventoryMapping.setIsFree(mapping.getIsFree());
            newInventoryMapping.setPaymentOwnerId(mapping.getPaymentOwnerId());
            newInventoryMapping.setEzyBillStockId(mapping.getEzyBillStockId());
            // newInventoryMapping.setTeamHierarchyMapping(mapping.teamHierarchyMapping);
            newInventoryMapping.setMvnoId(getMvnoIdFromCurrentStaff());
            if (mapping.getItemAssemblyId() != null) {
                newInventoryMapping.setItemAssemblyId(mapping.getItemAssemblyId());
            }
            newInventoryMapping = customerInventoryMappingRepo.save(newInventoryMapping);
            return newInventoryMapping;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


//    CustomerInventoryMapping createCutomerInventory(Item item,Customers customers,InOutWardMACMapping inOutWardMACMapping,CustomerInventoryMapping mapping){
//        CustomerInventoryMapping newInventoryMapping = new CustomerInventoryMapping();
//        newInventoryMapping.setAssignedDateTime(LocalDateTime.now());
//        newInventoryMapping.setItemId(item.getId());
//        newInventoryMapping.setProduct(productRepository.findById(item.getProductId()).get());
//        newInventoryMapping.setStaff(staffUserRepository.findById(getLoggedInUserId()).get());
//        newInventoryMapping.setInwardId(inOutWardMACMapping.getInwardId());
//        newInventoryMapping.setExpiryDateTime(mapping.getExpiryDateTime());
//        newInventoryMapping.setServiceId(mapping.getServiceId());
//        newInventoryMapping.setCustomer(customers);
//        newInventoryMapping.setIsDeleted(false);
//        newInventoryMapping.setQty(mapping.getQty());
//        newInventoryMapping.setCustPackId(mapping.getCustPackId());
//        newInventoryMapping.setExternalItemId(mapping.getExternalItemId());
//        newInventoryMapping.setNextApprover(mapping.getNextApprover());
//        newInventoryMapping.setPreviousApproveId(mapping.getPreviousApproveId());
//        newInventoryMapping.setStatus(mapping.getStatus());
//        newInventoryMapping.setTeamHierarchyMapping(mapping.teamHierarchyMapping);
//        newInventoryMapping.setMvnoId(getMvnoIdFromCurrentStaff());
//        newInventoryMapping = customerInventoryMappingRepo.save(newInventoryMapping);
//        return newInventoryMapping;
//    }


//   @Transactional
//    public GenericDataDTO rejectInventory(Long customerInventoryMappingId) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        CustomerInventoryMappingDto entity = super.getEntityById(customerInventoryMappingId);

    /// /        CustomersService customersService = SpringContext.getBean(CustomersService.class);
    /// /        Customers customers = customersService.get(entity.getCustomerId());
//        Customers customers = customersRepository.findById(entity.getCustomerId()).get();
//        if (Objects.equals(getLoggedInUser().getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {
//            entity.setNextApproverId(null);
//            entity.setTeamHierarchyMappingId(null);
//            entity.setStatus("ACTIVE");
//            genericDataDTO.setData(super.saveEntity(entity));
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            return genericDataDTO;
//        }

//        StaffUser staffUser = staffUserRepository.findById(Long.valueOf(getLoggedInUserId())).get();
//        List<Long> buIds = new ArrayList<>();
//        if (staffUser.getBusinessUnitNameList().size() > 0) {
//            staffUser.getBusinessUnitNameList().forEach(businessUnit -> buIds.add(businessUnit.getId()));
//        }
//        Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.LIST_TYPE_HIERARCHY, null, null, null, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()), false, null, null);
//        if (map.size() > 0) {
//            entity.setNextApproverId(Integer.valueOf(map.get("staffId")));
//            entity.setPreviousApproveId(staffUser.getId());
//            entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//            entity.setStatus("PENDING");
//
//        } else {
//            entity.setNextApproverId(null);
//            entity.setPreviousApproveId(staffUser.getId());
//            entity.setStatus("INACTIVE");
//            entity.setTeamHierarchyMappingId(null);
//
//        }
//        genericDataDTO.setData(super.saveEntity(entity));
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//    }

//   @Transactional
//    public GenericDataDTO rejectReplaceInventory(Long macMappingId) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        InOutWardMACMapping inOutWardMACMapping = inOutWardMACService.getRepository().findById(macMappingId).orElse(null);
//        CustomerInventoryMappingDto entity = super.getEntityById(inOutWardMACMapping.getCustInventoryMappingId());
//        CustomersService customersService = SpringContext.getBean(CustomersService.class);
//        Customers customers = customersService.get(entity.getCustomerId());
//        if (Objects.equals(getLoggedInUser().getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {
//            entity.setNextApproverId(null);
//            entity.setTeamHierarchyMappingId(null);
//            entity.setStatus("ACTIVE");
//            genericDataDTO.setData(super.saveEntity(entity));
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            return genericDataDTO;
//        }
//        StaffUser staffUser = staffUserService.get(getLoggedInUserId());
//        List<Long> buIds = new ArrayList<>();
//        if (staffUser.getBusinessUnitNameList().size() > 0) {
//            staffUser.getBusinessUnitNameList().forEach(businessUnit -> buIds.add(businessUnit.getId()));
//        }
//        Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(staffUser.getId(), customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.LIST_TYPE_HIERARCHY, null, null, null, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()), false, null, null);
//        if (map.size() > 0) {
//            inOutWardMACMapping.setCurrentApproveId(Integer.valueOf(map.get("staffId")));
//            inOutWardMACMapping.setPreviousApproveId(staffUser.getId());
//            inOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//            inOutWardMACMapping.setStatus("PENDING");
//
//        } else {
//            inOutWardMACMapping.setCurrentApproveId(null);
//            inOutWardMACMapping.setPreviousApproveId(staffUser.getId());
//            inOutWardMACMapping.setStatus("INACTIVE");
//            inOutWardMACMapping.setTeamHierarchyMappingId(null);
//
//        }
//        genericDataDTO.setData(inOutWardMACService.getRepository().save(inOutWardMACMapping));
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        return genericDataDTO;
//    }
    public String getStaffDetails(Long customerInventoryMappingId) {
//        StaffUser staffUser = staffUserService.get(getLoggedInUserId());
        StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(getLoggedInUserId())).get();
        return staffUser.getUsername();
    }

    public CustomerInventoryMappingDto saveEntityForNonTrackable(CustomerInventoryMappingDto entity, Double enteredNewAmount) throws Exception {
        CustomerInventoryMappingDto customerInventoryMappingDto = null;
        try {
            entity.setItemId(entity.getProductId());
            customerInventoryMappingDto = super.saveEntity(entity);

            //For Subisu
            if (entity.getBillTo().equalsIgnoreCase(CommonConstants.CUSTOMER_INVENTORY_BILL_TO.SUBISU) || entity.getBillTo().equalsIgnoreCase(CommonConstants.CUSTOMER_INVENTORY_BILL_TO.ORGANIZATION)) {
                CustomerInventoryMappingDto entity1 = (CustomerInventoryMappingDto) entity.clone();
                Integer custId = entity1.getCustomerId();
                List<CustomerPackage> customerPackages = customerPackageRepository.findAllByCustomersId(custId);
                if (customerPackages.size() > 0) {
                    List<Long> cprIds = customerPackages.stream().filter(i -> i.isInvoiceToOrg()).map(i -> i.getCustPackageId()).collect(Collectors.toList());
                    if (entity.getIsInvoiceToOrg() != null) {
                        Optional<Customers> customerForValue = customersRepository.findById(custId);
                        String planType = customerForValue.get().getCusttype();
                        ClientService value = null;
                        if (planType.equalsIgnoreCase("Prepaid"))
                            value = clientServiceRepository.getByNameAndMvnoId("ORGANIZATION", getMvnoIdFromCurrentStaff());
                        else
                            value = clientServiceRepository.getByNameAndMvnoId("ORGANIZATIONPOST", getMvnoIdFromCurrentStaff());

                        List<Customers> customersList = customersRepository.findByUsername(value.getValue());
                        if (planType.equalsIgnoreCase("Prepaid")) {
                            entity1.setCustomerId(customersList.get(0).getId());
                        } else {
                            entity1.setCustomerId(customersList.get(0).getId());
                        }
                        entity1.setQty(entity.getQty());
                        entity1.setMvnoId(getMvnoIdFromCurrentStaff());
                        entity1.setMapping_ref_id(customerInventoryMappingDto.getId());
                        entity1.setItemId(entity.getProductId());
                        if (enteredNewAmount != null) {
                            Product product = productRepository.findById(entity.getProductId()).get();
                            if (product.getNewProductCharge() != null) {
                                Double subisuAmount = product.getActualpricenewProduct() - enteredNewAmount;
                                Charge charge = chargeRepository.findById(product.getNewProductCharge().getId()).get();
                                if (charge.getTaxId() != null) {
                                    entity1.setNewAmount(getPriceWithoutTax(Math.toIntExact(charge.getTaxId()), subisuAmount));
                                }
                            }
                        }
                        CustomerInventoryMapping domain = mapper.dtoToDomain(entity1, new CycleAvoidingMappingContext());
                        domain.setCustomer(customersList.get(0));
                        domain.setMapping_ref_id(customerInventoryMappingDto.getId());
                        CustomerInventoryMappingDto customerInventoryMappingDtoList2 = mapper.domainToDTO(customerInventoryMappingRepo.save(domain), new CycleAvoidingMappingContext());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return customerInventoryMappingDto;
    }

   @Transactional
    public CustomerInventoryMappingDto saveNonSerializedEntity(CustomerInventoryMappingDto entity) throws Exception {
        try {
            String inventoryJobType = entity.getInventoryJobType();
            String nature = entity.getNature();
            entity.setInventoryJobType(inventoryJobType);
            entity.setNature(nature);
            if (entity.getQty() == null) {
                throw new Exception("Please Enter Assign Quantity");
            } else {
                CustomerInventoryMappingDto customerInventoryMappingDto = entity;
                ProductDto productDto = productService.getEntityById(customerInventoryMappingDto.getProductId());
                Product product = productRepository.findById(productDto.getId()).get();
                boolean hasSerial = product.getProductCategory().isHasSerial();
                boolean isTrackable = product.getProductCategory().isHasTrackable();
                Double enteredNewAmount = null;
                if (entity.getNewAmount() != null) {
                    enteredNewAmount = entity.getNewAmount();
                }
                Customers customers = customersRepository.findById(entity.getCustomerId()).get();
                if (entity.getNewAmount() != null) {
                    entity.setNewAmount(entity.getNewAmount());
                    if (product.getNewProductCharge() != null) {
                        Charge charge = chargeRepository.findById(product.getNewProductCharge().getId()).get();
                        customerInventoryMappingDto.setChargeId(Long.valueOf(charge.getId()));
                        if (charge.getTaxId() != null) {
                            customerInventoryMappingDto.setNewAmount(getPriceWithoutTax(Math.toIntExact(charge.getTaxId()), entity.getNewAmount()));
                        }
                    }
                }
                if (!hasSerial && !isTrackable) {
                    if (Objects.equals(getLoggedInUser().getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {
                        customerInventoryMappingDto.setNextApproverId(null);
                        customerInventoryMappingDto.setTeamHierarchyMappingId(null);
                        customerInventoryMappingDto.setStatus("ACTIVE");

                    } else {
                        Map<String, String> map = null;
                        StaffUser assignedUser = null;
                        if (clientServiceRepository.findValueByNameAndMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN, getMvnoIdFromCurrentStaff()).equals("TRUE")) {
                            map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().dtoToDomain(customerInventoryMappingDto, new CycleAvoidingMappingContext()));
                            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                                assignedUser = staffUser;
                                customerInventoryMappingDto.setNextApproverId(Integer.valueOf(map.get("staffId")));
                                customerInventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                                customerInventoryMappingDto.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                customerInventoryMappingDto.setStatus("PENDING");
                                String action = CommonConstants.WORKFLOW_MSG_ACTION.INVENTORY + " with product name : " + " ' " + customerInventoryMappingDto.getProductName() + " ' " + "and " + "quantity : " + " ' " + customerInventoryMappingDto.getQty() + " '";
                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(customerInventoryMappingDto.getId()), customerInventoryMappingDto.getProductName(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
                            } else {
                                customerInventoryMappingDto.setNextApproverId(getLoggedInUserId());
                                customerInventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                                customerInventoryMappingDto.setTeamHierarchyMappingId(null);
                                customerInventoryMappingDto.setStatus("PENDING");
                            }
                        } else {
                            customerInventoryMappingDto.setNextApproverId(getLoggedInUserId());
                            customerInventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                            customerInventoryMappingDto.setTeamHierarchyMappingId(null);
                            customerInventoryMappingDto.setStatus("PENDING");
                        }

                        //TAT functionality
//                        if (assignedUser != null) {
//                            if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                                if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
//                                    map.put("tat_id", map.get("current_tat_id"));
//                                tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, customerInventoryMappingDto.getId().intValue(), null);
//                            }
//                        }
                    }
//                    Bill to Subisu
                    saveEntityForNonTrackable(customerInventoryMappingDto, enteredNewAmount);
                    productOwnerService.updateProductOwnerForNonTrackable(entity.getQty(), entity.productId, Long.valueOf(entity.staffId), CommonConstants.STAFF);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return entity;
    }

    public void validateMac(CustomerInventoryMappingDto inventoryMappingDto) throws Exception {
        if (inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please update mac in selected item", null);
        } else {
            validateMacInItem(inventoryMappingDto);
        }
    }

    public void validateMacInItem(CustomerInventoryMappingDto inventoryMappingDto) throws Exception {
        Long itemId = inventoryMappingDto.getInOutWardMACMapping().get(0).getItemId();
        String macAddress = itemRepository.findMacByItemId(itemId);
        if (macAddress == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please update mac in selected item", null);
        }
    }

    public void validateSerialNumber(CustomerInventoryMappingDto inventoryMappingDto) throws Exception {
        if (inventoryMappingDto.getInOutWardMACMapping().get(0).getSerialNumber() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter serial number in selected item", null);
        } else {
            validateSerialNumberInItem(inventoryMappingDto);
        }
    }

    public void validateSerialNumberInItem(CustomerInventoryMappingDto inventoryMappingDto) throws Exception {
        Long itemId = inventoryMappingDto.getInOutWardMACMapping().get(0).getItemId();
        String serialNumber = itemRepository.findSerialNumberByItemId(itemId);
        if (serialNumber == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please update serial number in selected item", null);
        }
    }

    public List<Long> activateOrReactivateService(List<Long> customerInventoryMappingId) throws Exception {

        try {
            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.id.in(customerInventoryMappingId)).and(qCustomerInventoryMapping.isDeleted.eq(false));
            List<CustomerInventoryMapping> customerInventoryMappings = IterableUtils.toList(customerInventoryMappingRepo.findAll(booleanExpression));
            String boxNumber = null;
            Customers customers = null;
            CasMaster casMaster = null;
            CustomerInventoryMappingDto customerInventoryMappingDto = null;
            if (customerInventoryMappings.size() == 2) {
                String cardNumber = null;
                List<CustomerInventoryMapping> assemblyInventories = null;
                //Push in /appEasy Build
                for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                    customers = customerInventoryMapping.getCustomer();
//                            ProductCategory productCategory = productCategoryRepository.findById(productRepository.findById(itemRepository.findById(customerInventoryMapping.getItemId()).get().getProductId()).get().getProductCategory().getId()).get();
                    Product product = customerInventoryMapping.getProduct();
                    ProductCategory productCategory = product.getProductCategory();
                    if (productCategory.getDtvCategory().equalsIgnoreCase("STB")) {
                        customerInventoryMappingDto = getEntityForUpdateAndDelete(customerInventoryMapping.getId());
                        assemblyInventories = customerInventoryMappingRepo.findAllByItemAssemblyId(customerInventoryMapping.getItemAssemblyId());

                        casMaster = casMasterRepo.findById(product.getCaseId()).get();
                        if (casMaster != null) {
                            boxNumber = itemRepository.findById(customerInventoryMapping.getItemId()).get().getSerialNumber();
                        }
                    }
                    if (productCategory.getDtvCategory().equalsIgnoreCase("Card")) {
                        cardNumber = itemRepository.findById(customerInventoryMapping.getItemId()).get().getSerialNumber();
                    }
                }
//                ezBillServiceUtility.getPairedInfoResponse(boxNumber.toString(), cardNumber.toString(), customerInventoryMappings.get(0).getConnectionNo(), customers, casMaster, customerInventoryMappingDto);
                assemblyInventories.forEach(assemblyInventorie -> assemblyInventorie.setPairStatus("Paired"));
                customerInventoryMappingRepo.saveAll(assemblyInventories);

            }
            if (customerInventoryMappings.size() == 1) {

                for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                    customers = customerInventoryMapping.getCustomer();
                    Product product = customerInventoryMapping.getProduct();
                    ProductCategory productCategory = product.getProductCategory();
                    if (productCategory.getDtvCategory().equalsIgnoreCase("STB")) {
                        customerInventoryMappingDto = getEntityForUpdateAndDelete(customerInventoryMapping.getId());
                        casMaster = casMasterRepo.findById(product.getCaseId()).get();
                        if (casMaster != null) {
                            boxNumber = itemRepository.findById(customerInventoryMapping.getItemId()).get().getSerialNumber();
                        }
                    }
                }
//                ezBillServiceUtility.getPairedInfoResponse(boxNumber, null, customerInventoryMappings.get(0).getConnectionNo(), customers, casMaster, customerInventoryMappingDto);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return customerInventoryMappingId;
    }

    public List<Long> getCas(List<Long> customerInventoryMappingId) throws Exception {
        try {
            for (int i = 0; i < customerInventoryMappingId.size(); i++) {
                CustomerInventoryMappingDto customerInventoryMappingDto = getEntityForUpdateAndDelete(customerInventoryMappingId.get(i));
                ProductDto productDto = productService.getEntityById(customerInventoryMappingDto.getProductId());
                ProductCategoryDto productCategoryDto = productCategoryService.getEntityById(productDto.getProductCategory().getId());
                if (productCategoryDto.isHasCas() == true) {
                    if (productCategoryDto.getDtvCategory().equalsIgnoreCase("STB")) {

                        CasMaster casMaster = casMasterRepo.findById(productDto.getCaseId()).get();
                        ItemDto itemDto = itemService.getEntityForUpdateAndDelete(customerInventoryMappingDto.getItemId());
                        String boxNumber = itemDto.getSerialNumber();
                        try {
                            activateOrReactivateService(customerInventoryMappingId);
//                            ReactivateBoxResponse reactivateBoxResponse = ezBillServiceUtility.reactivateBoxResponse(casMaster, boxNumber);
                        } catch (Exception e) {
//                            ReactivateBoxResponse reactivateBoxResponse = ezBillServiceUtility.reactivateBoxResponse(casMaster, boxNumber);
                        }
                    }
                }
            }
            //save Data In DtvHistory
            saveDtvHistory(customerInventoryMappingId, "ReactiveBox");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return customerInventoryMappingId;
    }

    public void saveDtvHistory(List<Long> customerInventoryMappingId, String evenType) {
        try {
            DtvHistory dtvHistory = new DtvHistory();
            dtvHistory.setEvenType(evenType);
            dtvHistory.setCustomerId(customerInventoryMappingRepo.findById(customerInventoryMappingId.get(0)).get().getCustomer().getId().longValue());
            customerInventoryMappingId.stream().forEach(r -> {
                Product product = productRepository.findById(customerInventoryMappingRepo.findById(r).get().getProduct().getId()).orElse(null);
                if (product.getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                    Item item = itemRepository.findById(customerInventoryMappingRepo.findById(r).get().getItemId()).orElse(null);
                    dtvHistory.setStbSerialNumber(item.getSerialNumber());
                }
                if (product.getProductCategory().getDtvCategory().equalsIgnoreCase("Card")) {
                    Item item = itemRepository.findById(customerInventoryMappingRepo.findById(r).get().getItemId()).orElse(null);
                    dtvHistory.setCardSerialNumber(item.getSerialNumber());
                }
            });
            dtvHistoryRepo.save(dtvHistory);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }

    public GenericDataDTO getAllDtvHistoryByCustomer(Long customerId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List custIds = new ArrayList();
            custIds.add(customerId);
            QCustomers qCustomers = QCustomers.customers;
            BooleanExpression getChildExpression = qCustomers.isNotNull().and(qCustomers.parentCustId.eq(customerId.intValue())).and(qCustomers.parentExperience.equalsIgnoreCase(CommonConstants.PARENT_EXPERIENCE_SINGLE).and(qCustomers.isDeleted.eq(false).and(qCustomers.status.eq(CommonConstants.CUSTOMER_STATUS_ACTIVE))));
            List<Long> childCustIds = ((List<Customers>) customersRepository.findAll(getChildExpression)).stream().map(customers -> Long.valueOf(customers.getId())).collect(Collectors.toList());
            if (childCustIds != null && childCustIds.size() > 0) {
                custIds.addAll(childCustIds);
            }
            List<DtvHistory> dtvHistoryList = dtvHistoryRepo.findAllByCustomerIdIn(custIds);
            genericDataDTO.setDataList(dtvHistoryList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return genericDataDTO;
    }

    public List<Long> getpairSTB(List<Long> customerInventoryMappingId) throws Exception {
        CasMaster casMaster = null;
        String boxNumber = null;
        String vcNumber = null;
        List<CustomerInventoryMapping> assemblyInventories = null;
        CustomerInventoryMappingDto stbCustomerInventoryMappingDTO = null;
        Customers customer = null;
        try {
            for (int i = 0; i < customerInventoryMappingId.size(); i++) {
                CustomerInventoryMappingDto customerInventoryMappingDto = getEntityForUpdateAndDelete(customerInventoryMappingId.get(i));
                ProductDto productDto = productService.getEntityById(customerInventoryMappingDto.getProductId());
                ProductCategoryDto productCategoryDto = productCategoryService.getEntityById(productDto.getProductCategory().getId());
                if (productCategoryDto.isHasCas() == true) {
                    if (productCategoryDto.getDtvCategory().equalsIgnoreCase("STB")) {
                        assemblyInventories = customerInventoryMappingRepo.findAllByItemAssemblyId(customerInventoryMappingDto.getItemAssemblyId());
                        stbCustomerInventoryMappingDTO = customerInventoryMappingDto;
                        casMaster = casMasterRepo.findById(productDto.getCaseId()).get();
                        ItemDto itemDto = itemService.getEntityForUpdateAndDelete(customerInventoryMappingDto.getItemId());
                        boxNumber = itemDto.getSerialNumber();

                    } else if (productCategoryDto.getDtvCategory().equalsIgnoreCase("Card")) {
                        ItemDto itemDto = itemService.getEntityForUpdateAndDelete(customerInventoryMappingDto.getItemId());
                        vcNumber = itemDto.getSerialNumber();

                    }
                }
            }
            if (assemblyInventories != null) {
//                ezBillServiceUtility.pairSTB(boxNumber, vcNumber, casMaster, stbCustomerInventoryMappingDTO);
                assemblyInventories.forEach(assemblyInventorie -> assemblyInventorie.setPairStatus("Paired"));
//            assemblyInventories.get(0).setPairStatus("Paired");
//            assemblyInventories.get(1).setPairStatus("Paired");
                customerInventoryMappingRepo.saveAll(assemblyInventories);
            }
            saveDtvHistory(customerInventoryMappingId, "PairBox");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return customerInventoryMappingId;
    }

    public List<Long> getunpairSTB(List<Long> customerInventoryMappingId) throws Exception {
        List<CustomerInventoryMapping> assemblyInventories = null;

        try {
            for (int i = 0; i < customerInventoryMappingId.size(); i++) {
                CustomerInventoryMappingDto customerInventoryMappingDto = getEntityForUpdateAndDelete(customerInventoryMappingId.get(i));
                ProductDto productDto = productService.getEntityById(customerInventoryMappingDto.getProductId());
                ProductCategoryDto productCategoryDto = productCategoryService.getEntityById(productDto.getProductCategory().getId());
                if (productCategoryDto.isHasCas() == true) {
                    if (productCategoryDto.getDtvCategory().equalsIgnoreCase("STB")) {
                        assemblyInventories = customerInventoryMappingRepo.findAllByItemAssemblyId(customerInventoryMappingDto.getItemAssemblyId());
                        CasMaster casMaster = casMasterRepo.findById(productDto.getCaseId()).get();
                        ItemDto itemDto = itemService.getEntityForUpdateAndDelete(customerInventoryMappingDto.getItemId());
                        String boxNumber = itemDto.getSerialNumber();
//                        ezBillServiceUtility.getUnPairedInfoResponse(casMaster, boxNumber);
                    }
                }
            }
            assemblyInventories.forEach(assemblyInventorie -> assemblyInventorie.setPairStatus("Unpaired"));
            customerInventoryMappingRepo.saveAll(assemblyInventories);
            //update Dtv History
            saveDtvHistory(customerInventoryMappingId, "UnpairBox");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Serial Number is Already UnPaired")) {
                assemblyInventories.forEach(assemblyInventorie -> assemblyInventorie.setPairStatus("Unpaired"));
                customerInventoryMappingRepo.saveAll(assemblyInventories);
            }
            throw new RuntimeException(e);
        }
        return customerInventoryMappingId;
    }

    public void validateMacAtReplace(InOutWardMACMapping inOutWardMACMapping, Item item) throws Exception {
        if (inOutWardMACMapping.getMacAddress() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter mac in selected item", null);
        } else {
            validateMacInItemAtReplace(inOutWardMACMapping, item);
        }
    }

    public void validateMacInItemAtReplace(InOutWardMACMapping inOutWardMACMapping, Item item) throws Exception {
        if (item.getMacAddress() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please update mac in selected item", null);
        }
    }

    public void validateSerialNumberAtReplace(InOutWardMACMapping inOutWardMACMapping, Item item) throws Exception {
        if (inOutWardMACMapping.getSerialNumber() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter serial number in selected item", null);
        } else {
            validateSerialNumberInItemAtReplace(inOutWardMACMapping, item);
        }
    }

    public void validateSerialNumberInItemAtReplace(InOutWardMACMapping inOutWardMACMapping, Item item) throws Exception {
        if (item.getSerialNumber() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please update serial number in selected item", null);
        }
    }

   @Transactional
    public CustomerInventoryMapping replaceOldInventory(Long newMacMappingId, Long oldMacMappingId, boolean billAble, boolean isApproveRequest, CustomerInventoryMapping oldcustomersInventoryMapping) throws Exception {
//        System.out.println("Replace Old Inventory Started");
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            Long oldCustInvMapId = inOutWardMacRepo.findCustInvMapId(oldMacMappingId);
            Long createdbyIdByMappingId = customerInventoryMappingRepo.findCreatedbyIdByMappingId(oldCustInvMapId);
            Long newItemIdById = inOutWardMacRepo.findItemIdById(newMacMappingId);
            Long oldItemIdById = inOutWardMacRepo.findItemIdById(oldMacMappingId);
            Long newOwerIdById = itemRepository.findOwerIdById(newItemIdById);
            Long oldProductIdByItemId = itemRepository.findProductIdByItemId(oldItemIdById);
            Long newProductIdByItemId = itemRepository.findProductIdByItemId(newItemIdById);
            //update ProductOwner Table Details
            ProductOwner newproductOwner = null;
            ProductOwner oldProductOwner = null;
            Long newproductquantity = null;
            Long newproductusedQty = null;
            Long newproductunUsedQty = null;
            Long oldproductquantity = null;
            Long oldproductusedQty = null;
            Long oldproductunUsedQty = null;
            if (newProductIdByItemId == oldProductIdByItemId || newProductIdByItemId != null) {
                if (getLoggedInUser().getPartnerId() != 1) {
                    newproductOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(newProductIdByItemId, newOwerIdById, "Partner");
                } else {
                    newproductOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(newProductIdByItemId, newOwerIdById, "Staff");
                }
                newproductquantity = newproductOwner.getQuantity();
                newproductunUsedQty = newproductOwner.getUnusedQty();
                newproductusedQty = newproductOwner.getUsedQty();
            }
            if (oldProductIdByItemId != newProductIdByItemId) {
                if (getLoggedInUser().getPartnerId() != 1) {
                    oldProductOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(oldProductIdByItemId, createdbyIdByMappingId, "Partner");
                } else {
                    oldProductOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(oldProductIdByItemId, createdbyIdByMappingId, "Staff");
                }
                oldproductquantity = oldProductOwner.getQuantity();
                oldproductunUsedQty = oldProductOwner.getUnusedQty();
                oldproductusedQty = oldProductOwner.getUsedQty();
            }
            if (isApproveRequest) {
                if (oldMacMappingId != null) {
                    // Approve Replace Individual Inventory
                    /** Called: Method Approve Replce Individual Inventory */
                    genericDataDTO = approveReplaceIndividualInventory(oldMacMappingId, billAble, isApproveRequest);
                }
                if (newMacMappingId != null) {
                    /** Called: Method Approve Replce Individual Inventory */
                    genericDataDTO = approveReplaceIndividualInventory(newMacMappingId, billAble, isApproveRequest);
                    Object data = genericDataDTO.getData();
                    CustomerInventoryMapping customerInventoryMapping = (CustomerInventoryMapping) data;
                    CustomerInventoryMappingDto customerInventoryMappingDto = customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext());
                    customerInventoryMappingDto.setConnectionNo(oldcustomersInventoryMapping.getConnectionNo());
                    genericDataDTO.setData(super.saveEntity(customerInventoryMappingDto));
                    // ToDo: Comment Out due to Can't Create Invoice
//                    createExpiryInvoice(newMacMappingId, oldMacMappingId);
                    if (oldProductOwner != null) {
                        if (isApproveRequest) {
                            oldProductOwner.setQuantity(oldproductquantity);
                            oldProductOwner.setUsedQty(oldproductusedQty - 1);
                            oldProductOwner.setUnusedQty(oldproductunUsedQty + 1);
                            productOwnerRepository.save(oldProductOwner);
                        } else {
                            oldProductOwner.setQuantity(oldproductquantity);
                            oldProductOwner.setUsedQty(oldproductusedQty);
                            oldProductOwner.setUnusedQty(oldproductunUsedQty);
                            productOwnerRepository.save(oldProductOwner);
                        }
                    }
                    if (oldProductOwner != null && newproductOwner != null) {
                        if (isApproveRequest) {
                            newproductOwner.setQuantity(newproductquantity);
                            newproductOwner.setUsedQty(newproductusedQty);
                            newproductOwner.setUnusedQty(newproductunUsedQty);
                            productOwnerRepository.save(newproductOwner);
                        } else {
                            newproductOwner.setQuantity(newproductquantity);
                            newproductOwner.setUsedQty(newproductusedQty - 1);
                            newproductOwner.setUnusedQty(newproductunUsedQty + 1);
                            productOwnerRepository.save(newproductOwner);
                        }
                    }
                    if (oldProductOwner == null && newproductOwner != null) {
                        newproductOwner.setQuantity(newproductquantity);
                        newproductOwner.setUsedQty(newproductusedQty - 1);
                        newproductOwner.setUnusedQty(newproductunUsedQty + 1);
                        productOwnerRepository.save(newproductOwner);
                    }
//                    System.out.println("Replace Old Inventory Ended");
                    return customerInventoryMappingMapper.dtoToDomain(super.saveEntity(customerInventoryMappingDto), new CycleAvoidingMappingContext());
                }
            }
            if (!isApproveRequest) {
                /** Called: Method Approve Replce Individual Inventory */
                genericDataDTO = approveReplaceIndividualInventory(newMacMappingId, billAble, isApproveRequest);
                InOutWardMACMapping newInOutWardMACMapping = inOutWardMacRepo.findById(newMacMappingId).get();
                newInOutWardMACMapping.setCustInventoryMappingId(null);
                Item item = itemRepository.findById(newInOutWardMACMapping.getItemId()).get();
                // item.setOwnershipType(item.getIntransiantOwnership());
                item.setIntransiantWarrentyStatus(null);
                item.setIntransiantWarrenty(null);
                item.setIntransiantOwnership(null);
                item.setIntransiantexpireDate(null);
                itemRepository.save(item);
                inOutWardMacRepo.save(newInOutWardMACMapping);
                InOutWardMACMapping oldInOutWardMACMapping = inOutWardMacRepo.findById(oldMacMappingId).get();
                oldInOutWardMACMapping.setStatus(CommonConstants.ACTIVE_STATUS);
                inOutWardMacRepo.save(oldInOutWardMACMapping);
                //RemoveIntransiant entry from old item
                Item olditem = itemRepository.findById(oldInOutWardMACMapping.getItemId()).get();
                olditem.setIntransiantWarrentyStatus(null);
                olditem.setIntransiantWarrenty(null);
                olditem.setIntransiantOwnership(null);
                olditem.setIntransiantexpireDate(null);
                itemRepository.save(olditem);
                //setReplaceReason Null
                CustomerInventoryMapping oldcustomerInventoryMapping = customerInventoryMappingRepo.findById(oldInOutWardMACMapping.getCustInventoryMappingId()).orElse(null);
                oldcustomerInventoryMapping.setReplacementReason(null);
                //updateProductOwner Table
                if (newproductOwner != null) {
                    newproductOwner.setQuantity(newproductquantity);
                    newproductOwner.setUsedQty(newproductusedQty - 1);
                    newproductOwner.setUnusedQty(newproductunUsedQty + 1);
                    productOwnerRepository.save(newproductOwner);
                }
                genericDataDTO.setData(oldInOutWardMACMapping);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                System.out.println("Replace Old Inventory Ended");
                return oldcustomerInventoryMapping;
            }
//            System.out.println("Replace Old Inventory Ended");
            return (CustomerInventoryMapping) genericDataDTO.getData();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


   @Transactional
    public GenericDataDTO approveAllReplaceInventory(List<ApproveReplaceAllInventoryDTO> customerInventoryMappingDtoList, boolean billAble, boolean isApproveRequest) {
//        System.out.println("Approve All Replace Inventory Started");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<GenericDataDTO> genericDataDTOList = new ArrayList<>();
        try {
            ApproveReplaceAllInventoryDTO approveReplaceAllInventoryDTO1 = customerInventoryMappingDtoList.get(0);
            Long oldCustInvMapId = inOutWardMacRepo.findCustInvMapId(approveReplaceAllInventoryDTO1.getOldMacMappingId());
            Long oldItemIdById = inOutWardMacRepo.findItemIdById(approveReplaceAllInventoryDTO1.getOldMacMappingId());
            Long newItemIdById = inOutWardMacRepo.findItemIdById(approveReplaceAllInventoryDTO1.getNewMacMappingId());
            CustomerInventoryMapping inventoryMapping = customerInventoryMappingRepo.findById(oldCustInvMapId).orElse(null);
            Boolean isDTVById = planServiceRepository.findIsDTVById(inventoryMapping.getServiceId());
            if (isDTVById) {
                replacementInventoryForIsDTV(inventoryMapping, approveReplaceAllInventoryDTO1, customerInventoryMappingDtoList, isDTVById);
            }
            ApplicationLogger.logger.info("Replacing Inventory");
            /** Call to Approve Replace Inventory Method */
            customerInventoryMappingDtoList.stream().forEach(r -> {
                genericDataDTOList.add(approveReplaceInventory(r.getOldMacMappingId(), r.getNewMacMappingId(), billAble, isApproveRequest, r.getNextApprover()));

            });
            Item oldItem = itemRepository.findById(oldItemIdById).orElse(null);
            if (isApproveRequest) {
                oldItem.setItemStatus("Staff Allocated");
                itemRepository.save(oldItem);
            }

            Item newItem = itemRepository.findById(newItemIdById).orElse(null);
            if (isApproveRequest) {
//                if (inventoryMapping.getPlanId() != null || inventoryMapping.getPlanGroupId() != null) {
                /**
                 * Send Approve Inventory From Inventory to CMS
                 */
                InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
                inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
                inventorySerialNumberMessage.setPlanId(inventoryMapping.getPlanId());
                inventorySerialNumberMessage.setSerialNumber(newItem.getSerialNumber());
                inventorySerialNumberMessage.setConnectionNo(inventoryMapping.getConnectionNo());
                inventorySerialNumberMessage.setOperation(CommonConstants.REPLACE_INVETORIES);
                inventorySerialNumberMessage.setPlanGroupId(inventoryMapping.getPlanGroupId());
                inventorySerialNumberMessage.setCustInventoryId(inventoryMapping.getId());
                inventorySerialNumberMessage.setProductId(inventoryMapping.getProduct().getProductId());
                inventorySerialNumberMessage.setCustId(inventoryMapping.getCustomer().getId());
                inventorySerialNumberMessage.setItemId(newItem.getId());
                inventorySerialNumberMessage.setMacAddress(newItem.getMacAddress());
                inventorySerialNumberMessage.setItemName(newItem.getName());
                inventorySerialNumberMessage.setStatus("ACTIVE");
                inventorySerialNumberMessage.setQty(inventoryMapping.getQty());
                inventorySerialNumberMessage.setMvnoId(inventoryMapping.getMvnoId());
                inventorySerialNumberMessage.setVendorId(getvendorIdAgainstCustomerInventory(Long.valueOf(inventoryMapping.getProduct().getId())));
                kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
            }
            ApplicationLogger.logger.info("Inventory Replaced Successfully");
            genericDataDTO.setDataList(genericDataDTOList);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            System.out.println("Approve All Replace Inventory Ended");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return genericDataDTO;
    }

   @Transactional
    private void replacementInventoryForIsDTV(CustomerInventoryMapping inventoryMapping, ApproveReplaceAllInventoryDTO approveReplaceAllInventoryDTO1, List<ApproveReplaceAllInventoryDTO> customerInventoryMappingDtoList, Boolean isDTVById) {
        try {
            Item oldstbNumber = null;
            Item newstbNumber = null;
            //OLDCardItems
            Item oldCardNumber = null;
            Item newCardNumber = null;
            CasMaster oldCasMaster = null;
            CasMaster newCasMaster = null;
            String replacementReason = inventoryMapping.getReplacementReason();
//        if (isDTVById) {
            ApproveReplaceAllInventoryDTO approveReplaceAllInventoryDTO2 = null;
            Long oldstbId = approveReplaceAllInventoryDTO1.getOldMacMappingId();
            Long newStb = approveReplaceAllInventoryDTO1.getNewMacMappingId();
            Long oldCard = null;
            Long newCard = null;
            if (customerInventoryMappingDtoList.size() == 2) {
                approveReplaceAllInventoryDTO2 = customerInventoryMappingDtoList.get(1);
                oldCard = approveReplaceAllInventoryDTO2.getOldMacMappingId();
                newCard = approveReplaceAllInventoryDTO2.getNewMacMappingId();
            }
            if (oldstbId != null && newStb != null && oldCard != null && newCard != null) {
                //STBItems
                Long oldSTBItemIdById = inOutWardMacRepo.findItemIdById(oldstbId);
                Long newSTBItemIdById = inOutWardMacRepo.findItemIdById(newStb);
                //CardItems
                Long oldCardItemIdById = inOutWardMacRepo.findItemIdById(oldCard);
                Long newCardItemIdById = inOutWardMacRepo.findItemIdById(newCard);
                //                    CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(oldStbmapping.getCustInventoryMappingId()).orElse(null);
                if (oldSTBItemIdById != null && newSTBItemIdById != null && oldCardItemIdById != null && newCardItemIdById != null) {
                    //STBItems
                    oldstbNumber = itemRepository.findById(oldSTBItemIdById).orElse(null);
                    newstbNumber = itemRepository.findById(newSTBItemIdById).orElse(null);
                    //OLDCardItems
                    oldCardNumber = itemRepository.findById(oldCardItemIdById).orElse(null);
                    newCardNumber = itemRepository.findById(newCardItemIdById).orElse(null);
                    ProductCategory productCategory = productCategoryRepository.findById(productRepository.findById(oldstbNumber.getProductId()).get().getProductCategory().getId()).orElse(null);
                    oldCasMaster = casMasterRepo.findById(productRepository.findById(oldstbNumber.getProductId()).get().getCaseId()).get();
                    newCasMaster = casMasterRepo.findById(productRepository.findById(newstbNumber.getProductId()).get().getCaseId()).get();
                }
            }
            if (oldstbId != null && newStb != null && oldCard == null && newCard == null) {
                InOutWardMACMapping oldStbmappingId = inOutWardMacRepo.findById(oldstbId).orElse(null);
                InOutWardMACMapping newStbMappingId = inOutWardMacRepo.findById(newStb).orElse(null);
                oldstbNumber = itemRepository.findById(oldStbmappingId.getItemId()).orElse(null);
                newstbNumber = itemRepository.findById(newStbMappingId.getItemId()).orElse(null);
                oldCasMaster = casMasterRepo.findById(productRepository.findById(oldstbNumber.getProductId()).get().getCaseId()).get();
                newCasMaster = casMasterRepo.findById(productRepository.findById(newstbNumber.getProductId()).get().getCaseId()).get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
//        }
//        ApplicationLogger.logger.info("Replacing inventory at Ezybill");
        //eazyBuild Replacement
//            CustomerInventoryMappingDto entity = (CustomerInventoryMappingDto) genericDataDTOList.get(0).getData();
//            if (entity.getStatus().equalsIgnoreCase("ACTIVE")) {
//                if (planService.getIs_dtv() == true && isApproveRequest) {
//
////                    eazyBillReplacement(oldCasMaster, oldstbNumber, newstbNumber, oldCardNumber, newCardNumber, customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
////                    if (customerInventoryMappingDtoList.size() == 2) {
////                        ApproveReplaceAllInventoryDTO approveReplaceAllInventoryDTO2 = customerInventoryMappingDtoList.get(1);
////                        eazyBuildReplacement(approveReplaceAllInventoryDTO1.getOldMacMappingId(), approveReplaceAllInventoryDTO1.getNewMacMappingId(), approveReplaceAllInventoryDTO2.getOldMacMappingId(), approveReplaceAllInventoryDTO2.getNewMacMappingId());
////                    } else {
////                        eazyBuildReplacement(approveReplaceAllInventoryDTO1.getOldMacMappingId(), approveReplaceAllInventoryDTO1.getNewMacMappingId(), null, null);
////                    }
//                }
//            }
//            ApplicationLogger.logger.info("Inventory Replaced Successfully at Ezybill");
    }


    //Validation for assign inventory with connection number
    public void validateConnectionNumber(CustomerInventoryMappingDto inventoryMappingDto) throws Exception {
        try {
            if (inventoryMappingDto.getConnectionNo() != null) {
                QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression = qCustomerInventoryMapping.connectionNo.eq(inventoryMappingDto.getConnectionNo());
                List<CustomerInventoryMapping> customerInventoryMappings = IterableUtils.toList(customerInventoryMappingRepo.findAll(booleanExpression));
                if (customerInventoryMappings.size() != 0) {
                    customerInventoryMappings.forEach(customerInventoryMapping -> {
                        if (customerInventoryMapping.getIsDeleted().equals(false)) {
                            if (customerInventoryMapping.getStatus().equalsIgnoreCase("PENDING") || customerInventoryMapping.getStatus().equalsIgnoreCase("ACTIVE")) {
                                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Do not bind multiple setupbox with same connection number", null);
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void createExpiryInvoice(Long newMacMappingId, Long oldMacMappingId) {
//        System.out.println("Create Expiry Invoice Started");
        // Expired Inventory Replacement Invoice generate
        try {
            InOutWardMACMapping oldMacMapping = inOutWardMacRepo.findById(oldMacMappingId).get();
            Item item = itemRepository.findById(oldMacMapping.getItemId()).orElse(null);
            InOutWardMACMapping newMacMapping = inOutWardMacRepo.findById(newMacMappingId).get();
            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(newMacMapping.getCustInventoryMappingId()).get();
            CustomerInventoryMappingDto entity = null;
            entity = super.getEntityById(customerInventoryMapping.getId());
//            CustomersService customersService = SpringContext.getBean(CustomersService.class);
//            Customers customers = customersService.get(entity.getCustomerId());
            Customers customers = customersRepository.findById(entity.getCustomerId()).get();
//            if (item.getWarranty().equalsIgnoreCase("Expired")) {
//                createInventoryInvoice(entity, customers, customersService, customerInventoryMapping.getId());
//            }
            List<Long> mappingIds = new ArrayList<>();
            mappingIds.add(customerInventoryMapping.getId());
            StaffUser loggedInUser = staffUserRepository.findById(Math.toIntExact(Long.valueOf(getLoggedInUserId()))).get();
//            CustInventoryInvoiceMessage custInventoryInvoiceMessage = new CustInventoryInvoiceMessage();
//            messageSender.send(new CustInventoryInvoiceMessage(mappingIds, customers.getId().longValue(),"Inventory", loggedInUser.getFirstname(), loggedInUser.getId()), RabbitMqConstants.QUEUE_BILLING_INVOICE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Delete Old Mac Address from Radius If Replacement is Approve
   @Transactional
    public void deleteOldMacFromRadius(InOutWardMACMapping inOutWardMACMapping, Customers customers) {
        try {
            if (inOutWardMACMapping.getMacAddress() != null) {
                custMacMapppingService.deleteByMacAddress(inOutWardMACMapping.getMacAddress(), customers);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
    }

    public List<CustomerInventoryMappingDto> getDetailsBasedOnConnectionNumber(String connectionNumber) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<CustomerInventoryMappingDto> customerInventoryMappingDtoList;
        try {

            List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalse(connectionNumber);
            customerInventoryMappingDtoList = customerInventoryMappingMapper.domainToDTO(customerInventoryMappings, new CycleAvoidingMappingContext());
            if (customerInventoryMappingDtoList.size() != 0) {
                customerInventoryMappingDtoList.stream().forEach(r -> {
                    Product product = productRepository.findById(r.getProductId()).orElse(null);
                    ProductCategory productCategory = product.getProductCategory();
                    if (productCategory.isHasTrackable() == true && productCategory.isHasSerial() == true) {
                        r.setProductType("Serialized Item");
                        r.setProductCategoryName(productCategory.getName());
                    }
                    if (productCategory.isHasTrackable() == false && productCategory.isHasSerial() == false && productCategory.isHasMac() == false) {
                        r.setProductType("NonSerialized Item");
                        r.setProductCategoryName(productCategory.getName());
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return customerInventoryMappingDtoList;

    }

    public GenericDataDTO swapServicesFromParantToChild(String childConnectionNumber, String parentConnectionNumber, Long serviceId, String serviceName) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            //check connection number
            if (childConnectionNumber.equalsIgnoreCase(parentConnectionNumber)) {
                throw new RuntimeException("Both Connection number are same .Please select different connection number !!! ");
            }

            //Customer Service Mapping
            CustomerServiceMapping childcustomerServiceMapping = customerServiceMappingRepository.findByConnectionNo(childConnectionNumber);
            CustomerServiceMapping parentcustomerServiceMapping = customerServiceMappingRepository.findByConnectionNo(parentConnectionNumber);

            //CustomerInventory Mapping
            List<CustomerInventoryMapping> childcustomerInventoryMapping = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(childConnectionNumber, childcustomerServiceMapping.getCustId());
            List<CustomerInventoryMapping> parentcustomerInventoryMapping = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(parentConnectionNumber, parentcustomerServiceMapping.getCustId());

            // De-active Child STB device
            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
            BooleanExpression exp1 = qCustPlanMappping.isNotNull().and(qCustPlanMappping.isDelete.eq(false).and(qCustPlanMappping.custServiceMappingId.eq(childcustomerServiceMapping.getId()))).and(qCustPlanMappping.custPlanStatus.eq("Active"));
            List<CustPlanMappping> childCustPlanMapppingList = IterableUtils.toList(custPlanMappingRepository.findAll(exp1));
            if (childCustPlanMapppingList.size() > 0) {
//                ezBillServiceUtility.deactivateService(childCustPlanMapppingList, 13);
            }

            // De-active parent STB device
            exp1 = qCustPlanMappping.isNotNull().and(qCustPlanMappping.isDelete.eq(false).and(qCustPlanMappping.custServiceMappingId.eq(parentcustomerServiceMapping.getId()))).and(qCustPlanMappping.custPlanStatus.eq("Active"));
            List<CustPlanMappping> parentCustPlanMapppingList = IterableUtils.toList(custPlanMappingRepository.findAll(exp1));
            if (parentCustPlanMapppingList.size() > 0) {
//                ezBillServiceUtility.deactivateService(parentCustPlanMapppingList, 13);
            }

            if (childcustomerInventoryMapping.size() != 0) {

                //swap oldCustomerInvetory
                childcustomerInventoryMapping.stream().forEach(oldMapping -> {
                    if (oldMapping.getServiceId() == parentcustomerServiceMapping.getServiceId()) {
                        oldMapping.setServiceId(parentcustomerServiceMapping.getServiceId());
                        oldMapping.setCustomer(customersRepository.findById(parentcustomerServiceMapping.getCustId()).get());
                        oldMapping.setConnectionNo(parentcustomerServiceMapping.getConnectionNo());
                        customerInventoryMappingRepo.save(oldMapping);

                        //update SerelizedItem  Table
                        Item item = itemRepository.findById(oldMapping.getItemId()).orElse(null);
                        if (item != null) {
                            item.setOwnerId(parentcustomerServiceMapping.getCustId().longValue());
                            itemRepository.save(item);
                        }

                    } else {
                        throw new RuntimeException("The Services is not match for Parent Customer and Child Customer ");
                    }
                });
            } else {
                throw new RuntimeException("The Child Customer doesn't having any Inventory");

            }

            if (parentcustomerInventoryMapping.size() != 0) {
                //swap newCustomerInventory
                parentcustomerInventoryMapping.stream().forEach(newMapping -> {
                    if (newMapping.getServiceId() == childcustomerServiceMapping.getServiceId()) {
                        newMapping.setServiceId(childcustomerServiceMapping.getServiceId());
                        newMapping.setConnectionNo(childcustomerServiceMapping.getConnectionNo());
                        newMapping.setCustomer(customersRepository.findById(childcustomerServiceMapping.getCustId()).orElse(null));
                        customerInventoryMappingRepo.save(newMapping);

                        //update SerelizedItem  Table
                        Item item = itemRepository.findById(newMapping.getItemId()).orElse(null);
                        if (item != null) {
                            item.setOwnerId(childcustomerServiceMapping.getCustId().longValue());
                            itemRepository.save(item);
                        }
                    } else {
                        throw new RuntimeException("The Services is not match for Parent Customer and Child Customer ");
                    }
                });
            } else {
                throw new RuntimeException("The Parent Customer doesn't having any Inventory");
            }

//            ezBillServiceUtility.activatebyConnectionNumber(childCustPlanMapppingList, childConnectionNumber);
//            ezBillServiceUtility.activatebyConnectionNumber(parentCustPlanMapppingList, parentConnectionNumber);

            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Swap Service Successfully");


        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
        return genericDataDTO;
    }

    public GenericDataDTO getChildAndParentCustomer(Long customerId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Integer count = customersRepository.countByParentCustId(customerId.intValue());
            if (count != 0) {
                //getChildCustomerServiceMappingList
                QCustomers qCustomers = QCustomers.customers;
                BooleanExpression booleanExpression = qCustomers.isNotNull();
                booleanExpression = booleanExpression.and(qCustomers.parentCustId.eq(customerId.intValue()).and(qCustomers.isDeleted.eq(false)));
                List<Customers> childcustomerServiceMappingsList = (List<Customers>) customersRepository.findAll(booleanExpression);

                //getParentCustomerList
                List<Integer> childCustomerIdList = childcustomerServiceMappingsList.stream().map(Customers::getId).collect(Collectors.toList());

                List<CustomerServiceMapping> parentCustomerServiceAreamapping = customerServiceMappingRepository.findByCustId(customerId.intValue());
                List<CustomerServiceMapping> childCustomerServiceAreamapping = customerServiceMappingRepository.findAllByCustIdIn(childCustomerIdList);

                parentCustomerServiceAreamapping.stream().forEach(r -> {
                    Services services = serviceRepository.findById(r.getServiceId()).orElse(null);
                    r.setServiceName(services.getServiceName());
                });
                childCustomerServiceAreamapping.stream().forEach(r -> {
                    Services services = serviceRepository.findById(r.getServiceId()).orElse(null);
                    r.setServiceName(services.getServiceName());
                });

                CustomerInvetoryChildParantCustomerDto customerInvetoryChildParantCustomerDto = new CustomerInvetoryChildParantCustomerDto();
                customerInvetoryChildParantCustomerDto.setChildcustomerServiceMappings(childCustomerServiceAreamapping);
                customerInvetoryChildParantCustomerDto.setParentcustomerServiceMappings(parentCustomerServiceAreamapping);
                genericDataDTO.setData(customerInvetoryChildParantCustomerDto);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Successfully get Child and ParentCustomer Details");
                return genericDataDTO;
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("The Customer doesn't have child customer");
                return genericDataDTO;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }

    }

    // Save New Mac Address from Radius If Replacement is Approve
   @Transactional
    public void saveNewMacInRadius(InOutWardMACMapping inOutWardMACMapping, boolean billAble, boolean isApproveRequest) {
        try {
            String connectionNoByMappingId = customerInventoryMappingRepo.findConnectionNoByMappingId(inOutWardMACMapping.getCustInventoryMappingId());
            Long customerIdByMappingId = customerInventoryMappingRepo.findCustomerIdByMappingId(inOutWardMACMapping.getCustInventoryMappingId());
            Long serviceIdByMappingId = customerInventoryMappingRepo.findServiceIdByMappingId(inOutWardMACMapping.getCustInventoryMappingId());
            Customers customers = customersRepository.findAllLightCustomerById(customerIdByMappingId.intValue());
            QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
            BooleanExpression booleanExpression = qCustMacMappping.isNotNull();
            booleanExpression = booleanExpression.and(qCustMacMappping.macAddress.eq(inOutWardMACMapping.getMacAddress()).and(qCustMacMappping.isDeleted.eq(false)));
            Optional<CustMacMappping> custMacMappping = custMacMapppingRepository.findOne(booleanExpression);
            CustMacMappingMessage message = new CustMacMappingMessage(custMacMappping.get(), customers.getMvnoId(), customers.getUsername());
//            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByConnectionNo(connectionNoByMappingId);
            if (serviceIdByMappingId != null) {
                message.getData().put("custsermapid", serviceIdByMappingId);
                kafkaMessageSender.send(new KafkaMessageData(message, CustMacMappingMessage.class.getSimpleName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
    }

    //To get new offer price without tax
    public Double getPriceWithoutTax(int taxId, Double priceWithTax) {
        try {
            Optional<Tax> newProducttaxO = taxRepository.findById(taxId);
            Double newPriceWithoutTax = Double.valueOf(priceWithTax);
            if (newProducttaxO.isPresent()) {
                Tax newProducttax = newProducttaxO.get();
                List<TaxTypeTier> taxTypeTiers = newProducttax.getTieredList();
                //taxTypeTiers.forEach(taxTypeTier->taxTypeTier.getTax());
                Double newProducttaxRate = taxTypeTiers.get(0).getRate();
                newPriceWithoutTax = priceWithTax * 100 / (100 + newProducttaxRate);
            }
            return newPriceWithoutTax;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * findAllCustomerRelatedInventory
     **/
    public List<Integer> getServiceInventoryMapping(Integer custId) {
        try {
            List<Integer> serviceMappingIds = new ArrayList<>();
            List<CustomerInventoryMapping> customerInventoryMappingList = customerInventoryMappingRepo.findAllByCustomerId(custId);
            if (!customerInventoryMappingList.isEmpty()) {
                for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappingList) {
                    DebitDocumentInventoryRel debitDocumentInventoryRel = debitDocumentInventoryRelRepository.findByCustInventoryMappingId(customerInventoryMapping.getId());
                    if (debitDocumentInventoryRel != null) {
                        DebitDocument debitDocument = debitDocRepository.findById(debitDocumentInventoryRel.getDebitdocumentid()).get();
                        if (debitDocument != null) {
                            if (Objects.isNull(debitDocument.getPaymentStatus())) {
                                debitDocument.setPaymentStatus(StatusConstants.INVOICE_STATUS.UNPAID);
                            }
                            if (debitDocument.getPaymentStatus().equalsIgnoreCase(StatusConstants.INVOICE_STATUS.UNPAID)) {
                                List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findAllByConnectionNo(customerInventoryMapping.getConnectionNo());
                                if (!customerServiceMappingList.isEmpty()) {
                                    serviceMappingIds.add(customerServiceMappingList.get(0).getId());
                                }
                            }
                        }

                    }
                }
            }
            return serviceMappingIds;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void assignFromStaffList(Integer nextAssignStaff, String eventName, Integer entityId,
                                    boolean isApproveRequest, boolean isAssignPairItem) {
        try {
            if (isAssignPairItem) {
                QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isDeleted.eq(false).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.PENDING)).and(qCustomerInventoryMapping.itemAssemblyId.eq(Long.valueOf(entityId)));
                List<CustomerInventoryMapping> customerInventoryMappingList = IterableUtils.toList(customerInventoryMappingRepo.findAll(booleanExpression));
                customerInventoryMappingList.stream().forEach(customerInventoryMapping -> {
                    try {
                        hierarchyService.assignFromStaffList(nextAssignStaff, eventName, Math.toIntExact(customerInventoryMapping.getId()), isApproveRequest);
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                try {
                    hierarchyService.assignFromStaffList(nextAssignStaff, eventName, entityId, isApproveRequest);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void sendAssignInventoryToCMS(List<CustomerInventoryMappingDto> customerInventoryMappingDtoList, List<CustInvParamsDto> custInvParamsDtos, boolean hasMac, boolean hasSerial) {
        /**
         * Send Approve Inventory From Inventory to CMS
         */
        try {
            for (CustomerInventoryMappingDto customerInventoryMappingDto : customerInventoryMappingDtoList) {
                CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(customerInventoryMappingDto.getId()).orElse(null);
                if (customerInventoryMapping != null) {
                    Item item = itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
                    InventorySerialNumberMessage inventorySerialNumberMessage = new InventorySerialNumberMessage();
                    inventorySerialNumberMessage.setLoggedInUserName(getLoggedInUser().getUsername());
                    inventorySerialNumberMessage.setPlanId(customerInventoryMapping.getPlanId());
                    if (item != null) {
                        inventorySerialNumberMessage.setSerialNumber(item.getSerialNumber());
                    }
                    inventorySerialNumberMessage.setConnectionNo(customerInventoryMapping.getConnectionNo());
                    inventorySerialNumberMessage.setOperation(CommonConstants.ASSIGN_INVETORIES);
                    inventorySerialNumberMessage.setPlanGroupId(customerInventoryMapping.getPlanGroupId());
                    inventorySerialNumberMessage.setCustInventoryId(customerInventoryMapping.getId());
                    inventorySerialNumberMessage.setProductId(customerInventoryMapping.getProduct().getProductId());
                    inventorySerialNumberMessage.setCustId(customerInventoryMapping.getCustomer().getId());
                    if (item != null) {
                        inventorySerialNumberMessage.setItemId(item.getId());
                        inventorySerialNumberMessage.setMacAddress(item.getMacAddress());
                        inventorySerialNumberMessage.setItemName(item.getName());
                    }
                    inventorySerialNumberMessage.setStatus("PENDING");
                    inventorySerialNumberMessage.setMvnoId(customerInventoryMapping.getMvnoId());
                    inventorySerialNumberMessage.setQty(customerInventoryMapping.getQty());
                    inventorySerialNumberMessage.setVendorId(getvendorIdAgainstCustomerInventory(Long.valueOf(customerInventoryMapping.getProduct().getId())));
                    kafkaMessageSender.send(new KafkaMessageData(inventorySerialNumberMessage, InventorySerialNumberMessage.class.getSimpleName()));
                }
            }
            if (!CollectionUtils.isEmpty(custInvParamsDtos)) {
                CustInvParamsMessage custInvParamsMessage = new CustInvParamsMessage();
                custInvParamsMessage.setCustInvParams(custInvParamsDtos);
                custInvParamsMessage.setIsUpdate(false);
                custInvParamsMessage.setCustId(custInvParamsDtos.stream().map(CustInvParamsDto::getCustId).findAny().get());
                kafkaMessageSender.send(new KafkaMessageData(custInvParamsMessage, CustInvParamsMessage.class.getSimpleName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

   @Transactional
    public List<CustInvParams> updateCustInvParams(CustInvParamsMessage message, Integer custId) {
        if (message.getCustInvId() != null) {
            List<CustInvParamsDto> custInvParamsDtos = message.getCustInvParams();
            if (!CollectionUtils.isEmpty(custInvParamsDtos)) {
                try {
                    List<CustInvParams> custInvParams = custInvParamsRepo.findAllByCustomerInventoryId(message.getCustInvId());
                    custInvParamsRepo.deleteInBatch(custInvParams);
                    custInvParamsDtos = custInvParamsDtos.stream().peek(custInvParamsDto -> {
                        custInvParamsDto.setCustId(Long.valueOf(custId));
                        if (message.getCustSerMapId() != null) {
                            custInvParamsDto.setCustSerMapId(message.getCustSerMapId());
                        } else {
                            custInvParamsDto.setCustSerMapId(custInvParams.get(0).getCustSerMapId());
                        }
                        custInvParamsDto.setCustInvId(message.getCustInvId());
                    }).collect(Collectors.toList());
                    List<CustInvParams> newCustInvParams = custInvParamsMapper.dtoToDomain(custInvParamsDtos, new CycleAvoidingMappingContext());
                    newCustInvParams = custInvParamsRepo.saveAll(newCustInvParams);
                    List<CustInvParamsDto> invParamsDtos = custInvParamsMapper.domainToDTO(newCustInvParams, new CycleAvoidingMappingContext());
                    CustInvParamsMessage custInvParamsMessage = new CustInvParamsMessage();
                    custInvParamsMessage.setCustInvParams(invParamsDtos);
                    custInvParamsMessage.setCustSerMapId(message.getCustSerMapId());
                    custInvParamsMessage.setIsUpdate(true);
                    custInvParamsMessage.setCustId(Long.valueOf(custId));
//                    messageSender.send(custInvParamsMessage, RabbitMqConstants.QUEUE_SEND_CUST_INV_DETAIL_TO_CMS);
                    kafkaMessageSender.send(new KafkaMessageData(custInvParamsMessage, CustInvParamsMessage.class.getSimpleName()));
                    return newCustInvParams;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LOGGER.error("Error to update Customer Inventory Mapping: " + ex.getMessage() + " for CustId: " + message);
                    throw new RuntimeException("Error to update customer inventory params: " + ex.getMessage());
                }
            }
        }
        return null;
    }

    public void updateCustomerInvStatusFromCMS(CustomerInventoryMappingMessage message) {
        try {
            if (message.getCustomerInventoryData() != null) {
                Map<String, Object> customerInventoryData = message.getCustomerInventoryData();
                List<Long> custInvId = message.getIds();
                String status = message.getStatus();
                if (!CollectionUtils.isEmpty(custInvId)) {
                    customerInventoryMappingRepo.updateStatusByIds(status, custInvId);
                }
            }
            logger.info("Update customer inventory status successfully");
        } catch (CustomValidationException e) {
            e.printStackTrace();
            logger.error("Unable to update customer inventory status with error: " + e.getMessage());
        }
    }


    public Long getvendorIdAgainstCustomerInventory(Long productId) {
        Product product = productRepository.findById(Long.valueOf(productId)).orElse(null);
        if (product != null) {
            return product.getVendor().getId();
        } else {
            return null;
        }
    }

    public CustomerInventoryMappingDto assignInventoryDocUpload(InventoryFileUploadRequest request) throws IOException {
        try {
            Optional<CustomerInventoryMapping> inventoryMappingOpt = customerInventoryMappingRepo.findById(request.getCustomerInventoryId());
            String SUBMODULE = getModuleNameForLog() + " [uploadDocument()] ";
            if (!inventoryMappingOpt.isPresent()) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "CustomerInventoryMapping not found for ID: " + request.getCustomerInventoryId(), null);
            }

            CustomerInventoryMapping inventoryMapping = inventoryMappingOpt.get();
            List<CustomerInventoryFileMapping> fileMappings = new ArrayList<>();

            for (SectionUploadRequest sectionUploadRequest : request.getSections()) {

                if (sectionUploadRequest.getFiles() != null) {
                    String PATH = clientServiceSrv.getByName(ClientServiceConstant.ASSIGN_INVENTORY_DOC_PATH, getMvnoIdFromCurrentStaff()).getValue();
                    String subFolderName = File.separator + request.getCustomerInventoryId() + File.separator + sectionUploadRequest.getName() + File.separator;
                    String path = PATH + subFolderName;
                    ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + path);
                    for (MultipartFile file : sectionUploadRequest.getFiles()) {
                        if (!file.isEmpty()) {
                            if (!isValidFileExtension(file.getOriginalFilename())) {
                                throw new CustomValidationException(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                                        "Unsupported file type: " + file.getOriginalFilename(), null);
                            }

                            String uniqueName = fileUtility.saveFileToServerForAssignInventoryDoc(file, path, getMvnoIdFromCurrentStaff() != null ? getMvnoIdFromCurrentStaff() : null);

                            CustomerInventoryFileMapping fileMapping = new CustomerInventoryFileMapping();
                            fileMapping.setCustomerInventoryMapping(inventoryMapping.getId());
                            fileMapping.setFilename(file.getOriginalFilename());
                            fileMapping.setUniquename(uniqueName);
                            fileMapping.setSection(sectionUploadRequest.getName());
                            if (sectionUploadRequest.getLatitude() != null && !sectionUploadRequest.getLatitude().isEmpty()) {
                                fileMapping.setLatitiude(sectionUploadRequest.getLatitude());
                            }
                            if (sectionUploadRequest.getLongitude() != null && !sectionUploadRequest.getLongitude().isEmpty()) {
                                fileMapping.setLongitude(sectionUploadRequest.getLongitude());
                            }
                            if (sectionUploadRequest.getOpticalRange() != null && !sectionUploadRequest.getOpticalRange().isEmpty() && !sectionUploadRequest.getOpticalRange().equalsIgnoreCase("null")) {
                                fileMapping.setOpticalRange(sectionUploadRequest.getOpticalRange());
                            }
                            fileMappings.add(fileMapping);

                            //Update is_sg_img_upload in CPM, for CAF Workflow Action:Document Upload - Digital
                            if (sectionUploadRequest.getName().equals(CommonConstants.Smart_Gadget)){
                                CustomerServiceMappingMessage custServiceMapping = repository.findServiceAndCustomerByMappingId(inventoryMapping.getId());
                                kafkaMessageSender.send(new KafkaMessageData(custServiceMapping, custServiceMapping.getClass().getSimpleName()));
                            }
                        }
                    }
                } else {
                    List<CustomerInventoryFileMapping> existingMappingOpt = customerInventoryFileMappingRepo.findByCustomerInventoryMappingAndSection(inventoryMapping.getId(), sectionUploadRequest.getName());
                    CustomerInventoryFileMapping fileMapping;
                    if (!existingMappingOpt.isEmpty()) {
                        fileMapping = existingMappingOpt.get(0);
                        if (sectionUploadRequest.getOpticalRange() != null && !sectionUploadRequest.getOpticalRange().isEmpty() && !sectionUploadRequest.getOpticalRange().equalsIgnoreCase("null")) {
                            fileMapping.setOpticalRange(sectionUploadRequest.getOpticalRange());
                        }
                    } else {
                        fileMapping = new CustomerInventoryFileMapping();
                        fileMapping.setCustomerInventoryMapping(inventoryMapping.getId());
                        fileMapping.setSection(sectionUploadRequest.getName());
                        if (sectionUploadRequest.getOpticalRange() != null && !sectionUploadRequest.getOpticalRange().isEmpty()) {
                            fileMapping.setOpticalRange(sectionUploadRequest.getOpticalRange());
                        }
                    }
                    if (sectionUploadRequest.getOpticalRange() != null && !sectionUploadRequest.getOpticalRange().isEmpty() && !sectionUploadRequest.getOpticalRange().equalsIgnoreCase("null")) {
                        fileMappings.add(fileMapping);
                    }
                }
            }

            customerInventoryFileMappingRepo.saveAll(fileMappings);
            if (request.getOpticalPowerRange() != null && !request.getOpticalPowerRange().isEmpty()) {
                inventoryMapping.setOpticalPowerRange(request.getOpticalPowerRange());
                customerInventoryMappingRepo.save(inventoryMapping);
            }
            return customerInventoryMappingMapper.domainToDTO(inventoryMapping, new CycleAvoidingMappingContext());
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Resource getAssignInventoryDoc(CustomerInventoryMapping customerInventoryMapping, String uniqueName, String section) {
        Resource resource = null;
        String PATH = clientServiceSrv.getByName(ClientServiceConstant.ASSIGN_INVENTORY_DOC_PATH, getMvnoIdFromCurrentStaff()).getValue();

        try {
            String subFolderName = File.separator + customerInventoryMapping.getId() + File.separator + section + File.separator;
            Path basePath = Paths.get(PATH + subFolderName);
            Path filePath = basePath.resolve(uniqueName).normalize();
            resource = new UrlResource(filePath.toUri());
            return resource;

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
            LOGGER.error("Error while get Assign Inventory Doc : " + ex.getMessage() + " for inventoryId : " + customerInventoryMapping.getId());
        }
        return resource;
    }

    public File getAssignInventoryFile(CustomerInventoryMapping customerInventoryMapping, String uniqueName, String section) {
        String PATH = clientServiceSrv.getByName(ClientServiceConstant.ASSIGN_INVENTORY_DOC_PATH, getMvnoIdFromCurrentStaff()).getValue();
        if (customerInventoryMapping == null) {
            String errorMessage = "Invalid customer inventory mapping or unique name is null.";
            LOGGER.error(errorMessage);
            throw new CustomValidationException(400, errorMessage, null);
        }
        try {
            String subFolderName = File.separator + customerInventoryMapping.getId() + File.separator + section + File.separator;
            Path basePath = Paths.get(PATH + subFolderName);
            Path filePath = basePath.resolve(uniqueName).normalize();
            return filePath.toFile();
        } catch (Exception ex) {
            ex.printStackTrace();
            String errorMessage = "Error while retrieving the assigned inventory file: " + ex.getMessage();
            LOGGER.error(errorMessage, ex);
            throw new CustomValidationException(500, errorMessage, ex);
        }
    }

    private boolean isValidFileExtension(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        return lowerCaseFilename.endsWith(".png") ||
                lowerCaseFilename.endsWith(".jpeg") ||
                lowerCaseFilename.endsWith(".jpg") ||
                lowerCaseFilename.endsWith(".pdf");
    }


    public GenericDataDTO getInventoryApprovals(PaginationRequestDTO paginationRequestDTO, Integer mvnoId) {
        try {
            // Generate page request for pagination
            PageRequest pageRequest = staffUserService.generatePageRequest(
                    paginationRequestDTO.getPage(),
                    paginationRequestDTO.getPageSize(),
                    "createdate",
                    CommonConstants.SORT_ORDER_DESC
            );
            List<Integer> customerIds = getCustomerIds();
            List<CustomerInventoryMappingDto> allCustomerInventoryList = new ArrayList<>();
            // Collect inventory data for each customer
            List<CustomerInventoryMappingDto> customerInventoryList = customerInventoryMappingService.getAllCustInventoryDashboard(customerIds, true, pageRequest.getPageNumber(), paginationRequestDTO.getPageSize());
            if (!customerInventoryList.isEmpty()) {
                allCustomerInventoryList.addAll(customerInventoryList);
            }
            int totalRecords = allCustomerInventoryList.size();
            int totalPages = (int) Math.ceil((double) totalRecords / paginationRequestDTO.getPageSize());
            int startIndex = pageRequest.getPageNumber() * pageRequest.getPageSize();
            int endIndex = Math.min(startIndex + pageRequest.getPageSize(), totalRecords);
            if (startIndex >= totalRecords) {
                allCustomerInventoryList = new ArrayList<>();
            } else {
                allCustomerInventoryList = allCustomerInventoryList.subList(startIndex, endIndex);
            }
            GenericDataDTO response = new GenericDataDTO();
            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("OK");
            response.setDataList(allCustomerInventoryList);
            response.setTotalRecords(totalRecords);
            response.setPageRecords(allCustomerInventoryList.size());
            response.setCurrentPageNumber(paginationRequestDTO.getPage());
            response.setTotalPages(totalPages);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getCustomerIds() {
        try {
            List<Long> serviceAreaIds = serviceAreaService.getServiceAreaByStaffIdLong();
            boolean checkServiceAreas = getLoggedInUserId() != 1 && !serviceAreaIds.isEmpty();
            long mvnoId = getMvnoIdFromCurrentStaff();
            boolean checkMvno = mvnoId != 1;
            List<Long> mvnoIds = Arrays.asList(mvnoId, 1L);
            List<Long> buIds = getBUIdsFromCurrentStaff();
            boolean checkBu = !buIds.isEmpty();
            int partnerId = getLoggedInUserPartnerId();
            boolean checkPartner = partnerId != 1;
            return customersRepository.getCustomerIdsByNativeQuery(
                    CommonConstants.PARENT_EXPERIENCE_ACTUAL,
                    checkServiceAreas,
                    serviceAreaIds,
                    checkMvno,
                    mvnoIds,
                    checkBu,
                    buIds,
                    checkPartner,
                    partnerId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            partnerId = -1;
        }
        return partnerId;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        }
        return user;
    }

    public List<Long> getBUIdsFromCurrentStaff() {
        List<Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //        ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public List<FileMappingList> getFilesByInventoryId(Long inventorMappingId) {
        List<FileMappingList> fileMappingList = new ArrayList<>();
        List<CustomerInventoryFileMapping> customerInventoryFileMappingList = customerInventoryFileMappingRepo.findByCustomerInventoryMappingId(inventorMappingId);
        if (!customerInventoryFileMappingList.isEmpty()) {
            fileMappingList = convertToFileMappingList(customerInventoryFileMappingList);
        }

        return fileMappingList;
    }

    public List<FileMappingList> convertToFileMappingList(List<CustomerInventoryFileMapping> mappings) {
        try {
            return mappings.stream()
                    .collect(Collectors.groupingBy(CustomerInventoryFileMapping::getSection))
                    .entrySet().stream()
                    .map(entry -> {
                        FileMappingList fileMappingList = new FileMappingList();
                        fileMappingList.setSectionName(entry.getKey());

                        List<FileDetails> fileDetailsList = entry.getValue().stream()
                                .map(mapping -> {
                                    FileDetails details = new FileDetails();
                                    details.setFileName(mapping.getFilename());
                                    details.setUniqueName(mapping.getUniquename());
                                    details.setLatitude(mapping.getLatitiude());
                                    details.setLongitude(mapping.getLongitude());
                                    details.setCustomerInventoryId(mapping.getCustomerInventoryMapping());
                                    details.setOpticalRange(mapping.getOpticalRange());
                                    return details;
                                })
                                .collect(Collectors.toList());

                        fileMappingList.setFileDetails(fileDetailsList);
                        return fileMappingList;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public void deleteFileFromDatabase(String uniquename) {
        CustomerInventoryFileMapping customerInventoryFileMapping = customerInventoryFileMappingRepo.findByCustomerInventoryByUniqueName(uniquename);
        if (customerInventoryFileMapping != null) {
            customerInventoryFileMappingRepo.delete(customerInventoryFileMapping);
        }
    }

    public List<CustomerInventoryMappingDto> getAllCustInventoryDashboard(List<Integer> custId, boolean pendingFilter, int pageNumber, Integer pageSize) {
        try {
            List<Long> custInventoryIds = getCustInventoryByNativeQuery(custId, pendingFilter);
            List<Long> childCustInventoryIds = getChildCustInventoryByNativeQuery(custId);
            if (!childCustInventoryIds.isEmpty()) {
                custInventoryIds.addAll(childCustInventoryIds);
            }
            if (custInventoryIds.isEmpty()) {
                return Collections.emptyList();
            }
            List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByIdIn(custInventoryIds);
            List<CustomerInventoryMappingDto> customerInventoryMappingDtoList = customerInventoryMappingMapper.domainToDTO(customerInventoryMappings, new CycleAvoidingMappingContext());
            customerInventoryMappingDtoList.stream().forEach(r -> {
                GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findRequestByCustomerInventoryId(r.getId());
                if (generateRemoveRequest != null) {
                    r.setGenerateRemoveRequest(true);
                    r.setRemoveRequestStatus(generateRemoveRequest.getRequestStatus());
                    if (generateRemoveRequest.getRevisedcharge() != null) {
                        r.setRevisedCharge(generateRemoveRequest.getRevisedcharge());
                    }
                } else {
                    r.setGenerateRemoveRequest(false);
                }
                if (r.getItemAssemblyName() == null) {
                    r.setItemAssemblyName(null);
                } else {
                    String itemAssemblyName = itemAssemblyRepo.findAssemblyNameById(r.getItemAssemblyId());
                    r.setItemAssemblyName(itemAssemblyName);
                }
                if (r.getItemAssemblyId() == null) {
                    r.setCustInventoryListId(r.getId());
                } else {
                    r.setCustInventoryListId(r.getItemAssemblyId());
                }
                if (r.getServiceId() != null) {
                    r.setServiceName(serviceRepository.findServiceNameById(r.getServiceId()));
                }
                if (r.getPlanId() != null) {
                    r.setCurrentPlan(postpaidPlanRepo.findNameById(Math.toIntExact(r.getPlanId())));
                }
                if (r.getInOutWardMACMapping().size() != 0) {
                    r.setItemType(itemRepository.findItemConditionByItemId(r.getItemId()));
                    r.setWarranty(itemRepository.findWarrantyByItemId(r.getItemId()));
                }
                Long pcId = productRepository.findProductCategoryIdByProductId(r.getProductId());
                if (pcId != null) {
                    boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
                    boolean hasMac = productCategoryRepository.findHasMacById(pcId);
                    if (!hasMac && !hasSerial) {
                        r.setExpDate(null);
                    } else if (hasMac || hasSerial) {
                        r.setExpDate(itemRepository.findExpiry_dateByItemId(r.getItemId()));
                    }
                }

                List<InOutWardMACMapping> inOutWardMACMappingList = r.getInOutWardMACMapping();
                if (inOutWardMACMappingList.size() == 2) {
                    List<InOutWardMACMapping> outWardMACMappingList = new ArrayList<>(inOutWardMACMappingList);
                    inOutWardMACMappingList.stream().forEach(t -> {
                        if (t.getStatus().equalsIgnoreCase("PENDING")) {
                            outWardMACMappingList.add(0, t);
                        } else {
                            outWardMACMappingList.add(1, t);
                        }
                    });
                    r.setInOutWardMACMapping(outWardMACMappingList);
                    for (int i = r.getInOutWardMACMapping().size() - 1; i > 1; i--) {
                        r.getInOutWardMACMapping().remove(i);
                    }
                }
                if (r.getNextApproverId() == null && r.getStatus().equals("PENDING") && r.getTeamHierarchyMappingId() != null) {
                    Integer custInveId = Math.toIntExact(r.getId());
                    List<WorkflowAssignStaffMapping> workflowAssignStaffMappingList = workflowAssignStaffMappingRepo.findAllByEntityIdAndStaffIdAndTeamHierarchyMappingId(custInveId, getLoggedInUserId(), r.getTeamHierarchyMappingId());
                    if (!workflowAssignStaffMappingList.isEmpty()) {
                        r.setNextApproverId(workflowAssignStaffMappingList.get(0).getStaffId());
                    }
                }
                if (r.getConnectionNo() != null) {
                    List<Integer> custServIds = customerServiceMappingRepository.findCustServiceIdByConnectionNo(r.getConnectionNo());
                    if (!CollectionUtils.isEmpty(custServIds)) {
                        r.setCustServiceMapId(Long.valueOf(custServIds.get(0)));
                    }
                } else if (r.getServiceId() != null) {
                    List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findAllByServiceIdAndCustId(r.getServiceId(), r.getCustomerId());
                    if (!CollectionUtils.isEmpty(customerServiceMappings)) {
                        r.setCustServiceMapId(Long.valueOf(customerServiceMappings.get(0).getId()));
                    }
                }
            });
            return customerInventoryMappingDtoList;

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }

    private List<Long> getCustInventoryByNativeQuery(List<Integer> custId, boolean pendingFilter) {
        return repository.findCustomerInventoryIdsByNativeQuery(custId, pendingFilter, Long.valueOf(getLoggedInUserId()));
    }

    private List<Long> getChildCustInventoryByNativeQuery(List<Integer> custId) {
        return repository.findChildCustomerInventoryIdsByNativeQuery(
                custId,
                CommonConstants.PARENT_EXPERIENCE_SINGLE.toUpperCase(),
                CommonConstants.CUSTOMER_STATUS_ACTIVE,
                Long.valueOf(getLoggedInUserId())
        );
    }
}
