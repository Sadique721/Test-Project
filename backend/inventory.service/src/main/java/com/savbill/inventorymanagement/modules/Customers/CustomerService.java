package com.savbill.inventorymanagement.modules.Customers;

import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.CustPlanMapping.CustPlanMappping;
import com.savbill.inventorymanagement.modules.CustPlanMapping.CustPlanMapppingPojo;
import com.savbill.inventorymanagement.modules.CustPlanMapping.CustPlanMapppingRepository;
import com.savbill.inventorymanagement.modules.CustomerServiceMapping.CustomerServiceMapping;
import com.savbill.inventorymanagement.modules.CustomerServiceMapping.CustomerServiceMappingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMappping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustMacMapping.CustMacMapppingRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMappingRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDeviceBind;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkDeviceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkdeviceBindRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.PopManagement.PopManagementRepository;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceAreaRepository;
import com.savbill.inventorymanagement.rabbitmq.CAFCustomerStatusMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveCustomerDataShareMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateCustomerShareDataMessage;
import com.savbill.inventorymanagement.utils.StatusConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Customer service.
 */
@Service
public class CustomerService extends ExBaseAbstractService<CustomersPojo, Customers, Integer> {


    /**
     * Instantiates a new Customer service.
     *
     * @param repository the repository
     * @param mapper the mapper
     */
    public CustomerService(CustomersRepository repository, CustomerMapper mapper) {
        super(repository, mapper);
    }

    /**
     * Gets module name for log.
     *
     * @return the module name for log
     */
    @Override
    public String getModuleNameForLog() {
        return "[CustomerService]";
    }

    /**
     * The constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    /**
     * The Cust plan mappping repository.
     */
    @Autowired
    CustPlanMapppingRepository custPlanMapppingRepository;

    /**
     * The Customers repository.
     */
    @Autowired
    CustomersRepository customersRepository;

    /**
     * The Customer service mapping repository.
     */
    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;

    /**
     * The Service area repository.
     */
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    /**
     * The Pop management repository.
     */
    @Autowired
    PopManagementRepository popManagementRepository;
    /**
     * The Network device repository.
     */
    @Autowired
    NetworkDeviceRepository networkDeviceRepository;
    /**
     * The Cust mac mappping repository.
     */
    @Autowired
    CustMacMapppingRepository custMacMapppingRepository;
    /**
     * The Customer inventory mapping repo.
     */
    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;
    /**
     * The Item repository.
     */
    @Autowired
    ItemRepository itemRepository;
    /**
     * The Inventory mapping repo.
     */
    @Autowired
    InventoryMappingRepo inventoryMappingRepo;
    /**
     * The Networkdevice bind repository.
     */
    @Autowired
    NetworkdeviceBindRepository networkdeviceBindRepository;
    /**
     * The Customer network bind repository.
     */
    @Autowired
    CustomerNetworkBindRepository customerNetworkBindRepository;

    /**
     * The Cust plan mappping.
     */
    CustPlanMappping custPlanMappping = new CustPlanMappping();
    /**
     * The Cust plan mapppingdto.
     */
    CustPlanMapppingPojo custPlanMapppingdto = new CustPlanMapppingPojo();

    /**
     * The Customer service mapping.
     */
    CustomerServiceMapping customerServiceMapping = new CustomerServiceMapping();

    /**
     * Save customers.
     *
     * @param message the message
     * @throws Exception the exception
     */
    public void saveCustomers(SaveCustomerDataShareMessage message) throws Exception {
        try {
            Customers customer = new Customers();
            // Set values from message to customer object
            customer.setId(message.getId());
            customer.setTitle(message.getTitle());
            customer.setUsername(message.getUsername());
            customer.setPassword(message.getPassword());
            customer.setFirstname(message.getFirstname());
            customer.setLastname(message.getLastname());
            customer.setCustname(message.getCustname());
            customer.setNetworkDeviceId(message.getNetworkdevicesId());
            customer.setStatus(message.getStatus());
            customer.setCusttype(message.getCusttype());
            customer.setMvnoId(message.getMvnoId());
            customer.setBuId(message.getBuId());
            customer.setIsDeleted(message.getIsDeleted());
            customer.setPopid(message.getPopId());
            customer.setOltid(message.getOltId());
            customer.setFramedIpBind(message.getFramedIpBind());
            customer.setMasterdbid(message.getMasterdbid());
            customer.setSplitterid(message.getSplitterid());
            customer.setOltportid(message.getOltportid());
            customer.setOltslotid(message.getOltslotid());
            customer.setFullName(message.getFullName());
            customer.setNasPort(message.getNasPort());
            customer.setIpPoolNameBind(message.getIpPoolNameBind());
            customer.setFramedIp(message.getFramedIp());
            customer.setPartnerId(message.getParnterId());
            customer.setParentCustId(message.getParentCustId());
            customer.setCreatedById(message.getCreatedById());
            ServiceArea serviceArea = serviceAreaRepository.findById(message.getServiceAreaId()).orElse(null);
            customer.setServicearea(serviceArea);
            customer.setBlockNo(message.getBlockNo());
            customer.setLastModifiedById(message.getLastModifiedById());
            List<CustPlanMappping> custPlanMapppingList = addCustPlanMapping(message.getCustPlanMapppingList());
            List<CustomerServiceMapping> customerServiceMappingList = addCustServiceMapping(message.getCustomerServiceMappingList());
            // Save the customer using the repository
            customersRepository.save(customer);
            custPlanMapppingRepository.saveAll(custPlanMapppingList);
            customerServiceMappingRepository.saveAll(customerServiceMappingList);
            logger.info("Customer created successfully with name " + message.getUsername());
        } catch (CustomValidationException e) {
            logger.error("Unable to create customer with name " + message.getUsername() + " , Error: " + e.getMessage());
        }
    }

    /**
     * Update customers.
     *
     * @param message the message
     * @throws Exception the exception
     */
    public void updateCustomers(UpdateCustomerShareDataMessage message) throws Exception {
        try {
            Customers customer = customersRepository.findById(message.getId()).orElse(null);
            // Set values from message to customer object
            if (customer != null) {
                // Set values from message to customer object
                customer.setId(message.getId());
                customer.setTitle(message.getTitle());
                customer.setUsername(message.getUsername());
                customer.setPassword(message.getPassword());
                customer.setFirstname(message.getFirstname());
                customer.setLastname(message.getLastname());
                customer.setCustname(message.getCustname());
                customer.setNetworkDeviceId(message.getNetworkdevicesId());
                customer.setStatus(message.getStatus());
                ServiceArea serviceArea = serviceAreaRepository.findById(message.getServiceAreaId()).orElse(null);
                customer.setServicearea(serviceArea);
                customer.setCusttype(message.getCusttype());
                customer.setMvnoId(customer.getMvnoId());
                customer.setBuId(customer.getBuId());
                customer.setPopid(message.getPopId());
                customer.setOltid(message.getOltId());
                customer.setMasterdbid(message.getMasterdbid());
                customer.setSplitterid(message.getSplitterid());
                customer.setFramedIpBind(message.getFramedIpBind());
                customer.setNasPort(message.getNasPort());
                customer.setIpPoolNameBind(message.getIpPoolNameBind());
                customer.setFramedIp(message.getFramedIp());
                customer.setIsDeleted(message.getIsDeleted());
                customer.setOltportid(message.getOltportid());
                customer.setOltslotid(message.getOltslotid());
                customer.setFullName(message.getFullName());
                customer.setPartnerId(message.getParnterId());
                customer.setParentCustId(message.getParentCustId());
                customer.setCreatedById(message.getCreatedById());
                customer.setLastModifiedById(message.getLastModifiedById());
                customer.setBlockNo(message.getBlockNo());
                List<CustPlanMappping> custPlanMapppingList = addCustPlanMapping(message.getCustPlanMapppingList());
                List<CustomerServiceMapping> customerServiceMappingList = addCustServiceMapping(message.getCustomerServiceMappingList());// Save the customer using the repository
                customersRepository.save(customer);
                custPlanMapppingRepository.saveAll(custPlanMapppingList);
                customerServiceMappingRepository.saveAll(customerServiceMappingList);
                logger.info("Customer updated successfully with name " + message.getUsername());
            } else {
                Customers customer1 = new Customers();
                // Set values from message to customer object
                customer1.setId(message.getId());
                customer1.setTitle(message.getTitle());
                customer1.setUsername(message.getUsername());
                customer1.setPopid(message.getPopId());
                customer1.setOltid(message.getOltId());
                customer1.setMasterdbid(message.getMasterdbid());
                customer1.setSplitterid(message.getSplitterid());
                customer1.setNasPort(message.getNasPort());
                customer1.setIpPoolNameBind(message.getIpPoolNameBind());
                customer1.setFramedIp(message.getFramedIp());
                customer1.setFramedIpBind(message.getFramedIpBind());
                customer1.setPassword(message.getPassword());
                customer1.setFirstname(message.getFirstname());
                customer1.setLastname(message.getLastname());
                customer1.setCustname(message.getCustname());
                customer1.setNetworkDeviceId(message.getNetworkdevicesId());
                customer1.setStatus(message.getStatus());
                customer1.setCusttype(message.getCusttype());
                ServiceArea serviceArea = serviceAreaRepository.findById(message.getServiceAreaId()).orElse(null);
                customer1.setServicearea(serviceArea);
                customer1.setMvnoId(message.getMvnoId());
                customer1.setBuId(message.getBuId());
                customer1.setIsDeleted(message.getIsDeleted());
                customer1.setOltportid(message.getOltportid());
                customer1.setOltslotid(message.getOltslotid());
                customer1.setFullName(message.getFullName());
                customer1.setPartnerId(message.getParnterId());
                customer1.setParentCustId(message.getParentCustId());
                customer1.setCreatedById(message.getCreatedById());
                customer1.setLastModifiedById(message.getLastModifiedById());
                customer1.setBlockNo(message.getBlockNo());
                List<CustPlanMappping> custPlanMapppingList = addCustPlanMapping(message.getCustPlanMapppingList());
                List<CustomerServiceMapping> customerServiceMappingList = addCustServiceMapping(message.getCustomerServiceMappingList());
                // Save the customer using the repository
                customersRepository.save(customer1);
                custPlanMapppingRepository.saveAll(custPlanMapppingList);
                customerServiceMappingRepository.saveAll(customerServiceMappingList);
                logger.info("Customer updated successfully with name " + message.getUsername());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update customer with name " + message.getUsername() + " , Error: " + e.getMessage());
        }
    }

    /**
     * Convert dto to domain cust plan mapping cust plan mappping.
     *
     * @param custPlanMapppingPojo the cust plan mappping pojo
     * @return the cust plan mappping
     */
    public CustPlanMappping convertDtoToDomainCustPlanMapping(CustPlanMapppingPojo custPlanMapppingPojo) {
        CustPlanMappping custPlanMappping = new CustPlanMappping();
        custPlanMappping.setId(custPlanMapppingPojo.getId());
        custPlanMappping.setCustId(custPlanMapppingPojo.getCustid());
        custPlanMappping.setPlanId(custPlanMapppingPojo.getPlanId());
        custPlanMappping.setBillTo(custPlanMapppingPojo.getBillTo());
        custPlanMappping.setIsInvoiceToOrg(custPlanMapppingPojo.getIsInvoiceToOrg());
        custPlanMappping.setService(custPlanMapppingPojo.getService());
        custPlanMappping.setIsDelete(custPlanMapppingPojo.getIsDelete());
        custPlanMappping.setCustPlanStatus(custPlanMapppingPojo.getCustPlanStatus());
        custPlanMappping.setStatus(custPlanMapppingPojo.getStatus());
        custPlanMappping.setCustServiceMappingId(custPlanMapppingPojo.getCustServiceMappingId());
        return custPlanMappping;
    }

    /**
     * Add cust plan mapping list.
     *
     * @param custPlanMapppings the cust plan mapppings
     * @return the list
     */
    public List<CustPlanMappping> addCustPlanMapping(List<CustPlanMapppingPojo> custPlanMapppings) {
        List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
        for (CustPlanMapppingPojo custPlanMapppingPojo : custPlanMapppings) {
            this.custPlanMapppingdto.setId(custPlanMapppingPojo.getId());
            this.custPlanMapppingdto.setCustid(custPlanMapppingPojo.getCustid());
            this.custPlanMapppingdto.setPlanId(custPlanMapppingPojo.getPlanId());
            this.custPlanMapppingdto.setPlangroupid(custPlanMapppingPojo.getPlangroupid());
            this.custPlanMapppingdto.setBillTo(custPlanMapppingPojo.getBillTo());
            this.custPlanMapppingdto.setIsInvoiceToOrg(custPlanMapppingPojo.getIsInvoiceToOrg());
            this.custPlanMapppingdto.setService(custPlanMapppingPojo.getService());
            this.custPlanMapppingdto.setIsDelete(custPlanMapppingPojo.getIsDelete());
            this.custPlanMapppingdto.setCustPlanStatus(custPlanMapppingPojo.getCustPlanStatus());
            this.custPlanMapppingdto.setStatus(custPlanMapppingPojo.getStatus());
            this.custPlanMapppingdto.setCustServiceMappingId(custPlanMapppingPojo.getCustServiceMappingId());
            this.custPlanMappping = convertDtoToDomainCustPlanMapping(this.custPlanMapppingdto);
            custPlanMapppingList.add(custPlanMappping);
        }
        return custPlanMapppingList;
    }

    /**
     * Add cust service mapping list.
     *
     * @param customerServiceMappings the customer service mappings
     * @return the list
     */
    public List<CustomerServiceMapping> addCustServiceMapping(List<CustomerServiceMapping> customerServiceMappings) {
        List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
        for (CustomerServiceMapping serviceMapping : customerServiceMappings) {
            this.customerServiceMapping.setId(serviceMapping.getId());
            this.customerServiceMapping.setServiceId(serviceMapping.getServiceId());
            this.customerServiceMapping.setCustId(serviceMapping.getCustId());
            this.customerServiceMapping.setConnectionNo(serviceMapping.getConnectionNo());
            this.customerServiceMapping.setPartner(serviceMapping.getPartner());
            this.customerServiceMapping.setPop(serviceMapping.getPop());
            this.customerServiceMapping.setStaticOrPooledIP(serviceMapping.getStaticOrPooledIP());
            this.customerServiceMapping.setIsDelete(serviceMapping.getIsDelete());
            this.customerServiceMapping.setCreatedById(serviceMapping.getCreatedById());
            this.customerServiceMapping.setLastModifiedById(serviceMapping.getLastModifiedById());
            this.customerServiceMapping.setStatus(serviceMapping.getStatus());
            this.customerServiceMapping.setMvnoId(serviceMapping.getMvnoId());
            this.customerServiceMapping.setBuId(serviceMapping.getBuId());
            customerServiceMappingList.add(this.customerServiceMapping);
        }
        return customerServiceMappingList;
    }

    /**
     * Gets cust network detail.
     *
     * @param custId the cust id
     * @return the cust network detail
     */
    public GenericDataDTO getCustNetworkDetail(Integer custId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        CustomerNetworkDeviceDTO customerNetworkDeviceDTO = new CustomerNetworkDeviceDTO();
        try {
            Optional<Customers> customersOptional = customersRepository.findById(custId);
            Optional<CustomerNetworkBind> customerNetworkBindOptional = customerNetworkBindRepository.findByCustomerId(custId.longValue());
            if (customersOptional.isPresent()) {
                Customers customers = customersOptional.get();
                customerNetworkDeviceDTO.setCustomerid(custId);
                if (customers.getPopid() != null && customers.getPopid() != 0) {
                    Optional<String> popName = popManagementRepository.findNameById(customers.getPopid());
                    popName.ifPresent(pop -> {
                        customerNetworkDeviceDTO.setPopid(customers.getPopid());
                        customerNetworkDeviceDTO.setPopName(pop);
                    });
                } else if (customerNetworkBindOptional.isPresent() && customerNetworkBindOptional.get().getPopId() != null) {
                    CustomerNetworkBind bind = customerNetworkBindOptional.get();
                    Optional<PopManagement> popOptional = popManagementRepository.findById(bind.getPopId());
                    popOptional.ifPresent(pop -> {
                        customerNetworkDeviceDTO.setPopid(bind.getPopId());
                        customerNetworkDeviceDTO.setPopName(pop.getName());
                    });
                }
                if (customers.getOltid() != null) {
                    Optional<String> oltOptional = networkDeviceRepository.findNameById(customers.getOltid());
                    oltOptional.ifPresent(networkDevices -> {
                        customerNetworkDeviceDTO.setOltid(customers.getOltid());
                        customerNetworkDeviceDTO.setOltDeviceName(networkDevices);
                    });
                } else if (customerNetworkBindOptional.isPresent() && customerNetworkBindOptional.get().getOltId() != null) {
                    CustomerNetworkBind bind = customerNetworkBindOptional.get();
                    Optional<String> oltOptional = networkDeviceRepository.findNameById(bind.getOltId());
                    oltOptional.ifPresent(networkDevices -> {
                        customerNetworkDeviceDTO.setOltid(bind.getOltId());
                        customerNetworkDeviceDTO.setOltDeviceName(networkDevices);
                    });
                }
                if (customers.getMasterdbid() != null) {
                    Optional<String> masterDbOptional = networkDeviceRepository.findNameById(customers.getMasterdbid());
                    masterDbOptional.ifPresent(networkDevices -> {
                        customerNetworkDeviceDTO.setMasterdbid(customers.getMasterdbid());
                        customerNetworkDeviceDTO.setMasterdbDeviceName(networkDevices);
                    });
                } else if (customerNetworkBindOptional.isPresent() && customerNetworkBindOptional.get().getMasterDBId() != null) {
                    CustomerNetworkBind bind = customerNetworkBindOptional.get();
                    Optional<String> masterDbOptional = networkDeviceRepository.findNameById(bind.getMasterDBId());
                    masterDbOptional.ifPresent(networkDevices -> {
                        customerNetworkDeviceDTO.setMasterdbid(bind.getMasterDBId());
                        customerNetworkDeviceDTO.setMasterdbDeviceName(networkDevices);
                    });
                }
                if (customers.getSplitterid() != null) {
                    Optional<String> splitterOptional = networkDeviceRepository.findNameById(customers.getSplitterid());
                    splitterOptional.ifPresent(networkDevices -> {
                        customerNetworkDeviceDTO.setDnsplitterid(customers.getSplitterid());
                        customerNetworkDeviceDTO.setDnsplitterDerviceName(networkDevices);
                    });
                }else if (customerNetworkBindOptional.isPresent()) {
                    if (customerNetworkBindOptional.get().getDnSplitterId()!= null) {
                        CustomerNetworkBind bind = customerNetworkBindOptional.get();
                        Optional<String> splitterOptional = networkDeviceRepository.findNameById(bind.getDnSplitterId());
                        splitterOptional.ifPresent(networkDevices -> {
                            customerNetworkDeviceDTO.setDnsplitterid(bind.getDnSplitterId());
                            customerNetworkDeviceDTO.setDnsplitterDerviceName(networkDevices);
                        });
                    }
                    if (customerNetworkBindOptional.get().getSnSplitterId()!= null) {
                        CustomerNetworkBind bind = customerNetworkBindOptional.get();
                        Optional<String> splitterOptional = networkDeviceRepository.findNameById(bind.getSnSplitterId());
                        splitterOptional.ifPresent(networkDevices -> {
                            customerNetworkDeviceDTO.setSnsplitterid(bind.getSnSplitterId());
                            customerNetworkDeviceDTO.setSnsplitterDerviceName(networkDevices);
                        });
                    }
                }
                if (customers.getNasPort() != null || customers.getNasPort() != "") {
                    customerNetworkDeviceDTO.setNasPort(customers.getNasPort());
                } else if (customers.getNasPort() == null || customers.getNasPort() == "") {
                    customerNetworkDeviceDTO.setNasPort(null);
                }
                if (customers.getIpPoolNameBind() != null || customers.getIpPoolNameBind() != "") {
                    customerNetworkDeviceDTO.setIpPoolNameBind(customers.getIpPoolNameBind());
                } else if (customers.getIpPoolNameBind() == null || customers.getIpPoolNameBind() == "") {
                    customerNetworkDeviceDTO.setIpPoolNameBind(null);
                }
                if (customers.getFramedIp() != null || customers.getFramedIp() != "") {
                    customerNetworkDeviceDTO.setFramedIp(customers.getFramedIp());
                } else if (customers.getFramedIp() == null || customers.getFramedIp() == "") {
                    customerNetworkDeviceDTO.setFramedIp(null);
                }
                if (customers.getFramedIpBind() != null || customers.getFramedIpBind() != "") {
                    customerNetworkDeviceDTO.setFramedIpBind(customers.getFramedIpBind());
                } else if (customers.getFramedIpBind() == null || customers.getFramedIpBind() == "") {
                    customerNetworkDeviceDTO.setFramedIpBind(null);
                }
                customerNetworkDeviceDTO.setOltportid(customers.getOltportid());
                customerNetworkDeviceDTO.setOltslotid(customers.getOltslotid());

                List<CustMacMappping> custMacMapppingList = custMacMapppingRepository.findAllByCustomerIdAndIsDeletedIsFalse(custId);
                if (custMacMapppingList.size() != 0) {
//
                    customerNetworkDeviceDTO.setMacAddress(custMacMapppingList.stream()
                            .map(CustMacMappping::getMacAddress)
                            .collect(Collectors.toList()));
                }
//                List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo
//                        .findAllByCustomerIdAndExternalItemIdIsNullAndIsDeletedIsFalseAndStatus(custId, CommonConstants.ACTIVE_STATUS);
                List<Long> allItemIds = customerInventoryMappingRepo.findAllItemIdsByCustIdAndExternalIdIsNullAndStatus(custId, CommonConstants.ACTIVE_STATUS);
                if (allItemIds.size() != 0) {
//                    List<Long> itemIds = customerInventoryMappings.stream()
//                            .map(CustomerInventoryMapping::getItemId)
//                            .collect(Collectors.toList());
                    List<Item> items = itemRepository.findAllByIdIn(allItemIds);
                    if (!items.isEmpty()) {
                        customerNetworkDeviceDTO.setOnuSerialNumber(items.stream()
                                .map(Item::getSerialNumber)
                                .collect(Collectors.toList()));
                    }
                }
                List<Long> externalItemIds = customerInventoryMappingRepo.findAllItemIdsByCustIdAndExternalIdIsNotNullAndStatus(custId, CommonConstants.ACTIVE_STATUS);
                if (externalItemIds.size() != 0) {
                    List<Item> items = itemRepository.findAllByIdIn(externalItemIds);
                    if (!items.isEmpty()) {
                        customerNetworkDeviceDTO.setExternalOnuSerialNumber(items.stream()
                                .map(Item::getSerialNumber)
                                .collect(Collectors.toList()));
                    }
                }
                genericDataDTO.setData(customerNetworkDeviceDTO);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Customer not found");
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }

    /**
     * Gets customer list service area.
     *
     * @param serviceAreaIds the service area ids
     * @param requestDTO the request dto
     * @return the customer list service area
     */
    public GenericDataDTO getCustomerListServiceArea(Long serviceAreaIds, PaginationRequestDTO requestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Customers> customersList;
        PageRequest pageRequest = generatePageRequest(requestDTO.getPage(), requestDTO.getPageSize(), "createdate", requestDTO.getSortOrder());
//        if (getMvnoIdFromCurrentStaff() == 1) {
//            customersList = customersRepository.findAllByIsDeletedIsFalseAndStatusAndServiceareaId(CommonConstants.ACTIVE_STATUS, serviceAreaIds, pageRequest);
//        } else {
//            customersList = customersRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndServiceareaId(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), serviceAreaIds, pageRequest);
//        }
        QCustomers qCustomers = QCustomers.customers;
        BooleanExpression booleanExpression = qCustomers.isDeleted.eq(false);
        if (getMvnoIdFromCurrentStaff() != 1) {
            booleanExpression = booleanExpression.and(qCustomers.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
        }
        if (!getBUIdsFromCurrentStaff().isEmpty()) {
            booleanExpression = booleanExpression.and(qCustomers.buId.in(getBUIdsFromCurrentStaff()));
        }
        booleanExpression = booleanExpression.and(qCustomers.status.containsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomers.servicearea.id.eq(serviceAreaIds));
        customersList = customersRepository.findAll(booleanExpression, pageRequest);
        if (customersList != null && customersList.getSize() > 0) {
            makeGenericResponse(genericDataDTO, customersList);
        }
        return genericDataDTO;
    }

    /**
     * Search customers by service area generic data dto.
     *
     * @param serviceAreaIds the service area ids
     * @param filterList the filter list
     * @param page the page
     * @param pageSize the page size
     * @param sortBy the sort by
     * @param sortOrder the sort order
     * @return the generic data dto
     */
//    @Override
    public GenericDataDTO searchCustomersByServiceArea(Long serviceAreaIds, List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("name")) {
                        return getCustomersByFirstName(serviceAreaIds, searchModel.getFilterValue(), pageRequest);
                    }
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("username")) {
                        return getCustomersByUsername(serviceAreaIds, searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Gets customers by first name.
     *
     * @param serviceAreaIds the service area ids
     * @param name the name
     * @param pageRequest the page request
     * @return the customers by first name
     */
    public GenericDataDTO getCustomersByFirstName(Long serviceAreaIds, String name, PageRequest pageRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Customers> customersPage = null;
        List<Customers> customersList;
        if (getMvnoIdFromCurrentStaff() == 1) {
            customersList = customersRepository.findAllByIsDeletedIsFalseAndStatusAndServiceareaId(CommonConstants.ACTIVE_STATUS, serviceAreaIds)
                    .stream().filter(customers -> customers.getFirstname().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
        } else {
            customersList = customersRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndServiceareaId(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), serviceAreaIds)
                    .stream().filter(customers -> customers.getFirstname().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
        }
        List<Customers> paginatedList = customersList.stream()
                .skip(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .sorted(Comparator.comparing(Customers::getCreatedate, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        customersPage = new PageImpl<>(paginatedList, pageRequest, customersList.size());
        if (customersList != null && customersPage.getSize() > 0) {
            makeGenericResponse(genericDataDTO, customersPage);
        }
        return genericDataDTO;
    }

    /**
     * Gets customers by username.
     *
     * @param serviceAreaIds the service area ids
     * @param username the username
     * @param pageRequest the page request
     * @return the customers by username
     */
    public GenericDataDTO getCustomersByUsername(Long serviceAreaIds, String username, PageRequest pageRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Customers> customersPage = null;
        List<Customers> customersList;
        if (getMvnoIdFromCurrentStaff() == 1) {
            customersList = customersRepository.findAllByIsDeletedIsFalseAndStatusAndServiceareaId(CommonConstants.ACTIVE_STATUS, serviceAreaIds)
                    .stream().filter(customers -> customers.getUsername().toLowerCase().contains(username.toLowerCase())).collect(Collectors.toList());
        } else {
            customersList = customersRepository.findAllByIsDeletedIsFalseAndStatusAndMvnoIdInAndServiceareaId(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), serviceAreaIds)
                    .stream().filter(customers -> customers.getUsername().toLowerCase().contains(username.toLowerCase())).collect(Collectors.toList());
        }
        List<Customers> paginatedList = customersList.stream()
                .skip(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .sorted(Comparator.comparing(Customers::getCreatedate, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        customersPage = new PageImpl<>(paginatedList, pageRequest, customersList.size());
        if (customersList != null && customersPage.getSize() > 0) {
            makeGenericResponse(genericDataDTO, customersPage);
        }
        return genericDataDTO;
    }

    /**
     * Save caf to customer.
     *
     * @param message the message
     * @throws Exception the exception
     */
    public void saveCafToCustomer(CAFCustomerStatusMessage message) throws Exception {
        try {
            Customers cafCustomer = customersRepository.findById(message.getCustomerId()).orElse(null);
            if (cafCustomer != null) {
                cafCustomer.setStatus(message.getStatus());
                customersRepository.save(cafCustomer);
                logger.info("Successfully convert caf to customer with id " + message.getCustomerId());
            } else {
                logger.error("Data not found for convert caf to customer with id " + message.getCustomerId());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to convert caf to customer with id " + message.getCustomerId() + " , Error: " + e.getMessage());
        }
    }

    /**
     * Change status of cust services.
     *
     * @param custServIds the cust serv ids
     * @param status the status
     * @param remark the remark
     * @param aFalse the a false
     * @param generatecn the generatecn
     */
    public void changeStatusOfCustServices(List<Integer> custServIds, String status, String remark, Boolean aFalse, Boolean generatecn) {
        List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findAllByIdIn(custServIds);
        if (CollectionUtils.isEmpty(customerServiceMappings)) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer service not found!", null);
        }
        try {
            Customers customers = customersRepository.findById(customerServiceMappings.get(0).getCustId()).get();
            customerServiceMappings.forEach(customerServiceMapping -> {
                customerServiceMapping.setStatus(status);
                customerServiceMappingRepository.save(customerServiceMapping);
            });
            if (status.equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE)) {
                if (!customerServiceMappingRepository.existsByCustIdAndStatusNotIn(customers.getId(), Collections.singletonList(StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE))) {
                    changeCustomerStatus(Collections.singletonList(customers), StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE);
                }
            }
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Exception while updating customer service status: " + ex.getMessage(), null);
        }
    }

    /**
     * Change customer status.
     *
     * @param customers the customers
     * @param status the status
     */
    private void changeCustomerStatus(List<Customers> customers, String status) {
        try {
            switch (status) {
                default: {
                    customers = customers.stream().peek(customer -> customer.setStatus(status)).collect(Collectors.toList());
                }
            }
            customersRepository.saveAll(customers);
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Exception while updating customer status: " + ex.getMessage(), null);
        }
    }

    /**
     * Save customer network bind details customer network bind.
     *
     * @param customerNetworkBindDTO the customer network bind dto
     * @return the customer network bind
     * @throws Exception the exception
     */
    @Transactional
    public CustomerNetworkBind saveCustomerNetworkBindDetails(CustomerNetworkBindDTO customerNetworkBindDTO) throws Exception {
        try {
            CustomerNetworkBind customerNetworkBind = new CustomerNetworkBind();
            customerNetworkBind.setCustomerId(customerNetworkBindDTO.getCustomerid());
            customerNetworkBind.setPopId(customerNetworkBindDTO.getPopid());
            customerNetworkBind.setOltId(customerNetworkBindDTO.getOltid());
            customerNetworkBind.setDnSplitterId(customerNetworkBindDTO.getDnsplitterid());
            customerNetworkBind.setSnSplitterId(customerNetworkBindDTO.getSnsplitterid());
            customerNetworkBind.setMasterDBId(customerNetworkBindDTO.getMasterdbid());
            CustomerNetworkBind saveCustomerNetworkBind = customerNetworkBindRepository.save(customerNetworkBind);
            return saveCustomerNetworkBind;
        } catch (CustomValidationException exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    /**
     * Update customer network bind details customer network bind.
     *
     * @param customerNetworkBindDTO the customer network bind dto
     * @return the customer network bind
     * @throws Exception the exception
     */
    @Transactional
    public CustomerNetworkBind updateCustomerNetworkBindDetails(CustomerNetworkBindDTO customerNetworkBindDTO) throws Exception {
        try {
            Optional<CustomerNetworkBind> optionalBind = customerNetworkBindRepository.findById(customerNetworkBindDTO.getId());
            if (!optionalBind.isPresent()) {
                throw new CustomValidationException("Customer Network Bind record not found for ID: " + customerNetworkBindDTO.getId());
            }
            CustomerNetworkBind existingBind = optionalBind.get();
            if (customerNetworkBindDTO.getSnsplitterid() != existingBind.getSnSplitterId()) {
               deleteCustomerDevicesSNSplitterMapping(existingBind.getSnSplitterId(), existingBind.getCustomerId());
            }
            existingBind.setCustomerId(customerNetworkBindDTO.getCustomerid());
            existingBind.setPopId(customerNetworkBindDTO.getPopid());
            existingBind.setOltId(customerNetworkBindDTO.getOltid());
            existingBind.setDnSplitterId(customerNetworkBindDTO.getDnsplitterid());
            existingBind.setSnSplitterId(customerNetworkBindDTO.getSnsplitterid());
            existingBind.setMasterDBId(customerNetworkBindDTO.getMasterdbid());
            return customerNetworkBindRepository.save(existingBind);
        } catch (CustomValidationException exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    private void deleteCustomerDevicesSNSplitterMapping(Long snSplitterId, Long customerId) {
        List<Long> custInvenIds = customerInventoryMappingRepo
                .findAllIdsByCustIdAndExternalIdIsNullAndStatus(customerId.intValue(), CommonConstants.ACTIVE_STATUS);

        if (custInvenIds.isEmpty()) return;

        List<Long> deviceIds = networkDeviceRepository.findAllIdsByCustomerInventoryIds(custInvenIds);
        if (deviceIds.isEmpty()) return;

        List<NetworkDeviceBind> deviceBindsToDelete = networkdeviceBindRepository.findByDeviceIds(deviceIds);
        if (!deviceBindsToDelete.isEmpty()) {
            networkdeviceBindRepository.deleteAll(deviceBindsToDelete);
        }
    }

    /**
     * Gets customer network bind details by cust id.
     *
     * @param id the id
     * @return the customer network bind details by cust id
     * @throws Exception the exception
     */
    @Transactional
    public CustomerNetworkBindDTO getCustomerNetworkBindDetailsByCustId(Long id) throws Exception {
        try {
            Optional<CustomerNetworkBind> optionalBind = customerNetworkBindRepository.findByCustomerId(id);
            if (!optionalBind.isPresent()) {
                throw new CustomValidationException("Customer Network Not Bind for customer ID: " + id);
            }
            CustomerNetworkBind existingBind = optionalBind.get();
            CustomerNetworkBindDTO customerNetworkBindDTO = new CustomerNetworkBindDTO();
            customerNetworkBindDTO.setId(existingBind.getId());
            customerNetworkBindDTO.setCustomerid(existingBind.getCustomerId());
            customerNetworkBindDTO.setPopid(existingBind.getPopId());
            customerNetworkBindDTO.setOltid(existingBind.getOltId());
            customerNetworkBindDTO.setDnsplitterid(existingBind.getDnSplitterId());
            customerNetworkBindDTO.setSnsplitterid(existingBind.getSnSplitterId());
            customerNetworkBindDTO.setMasterdbid(existingBind.getMasterDBId());
            return customerNetworkBindDTO;
        } catch (CustomValidationException exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    /**
     * Validate customer network bind details.
     *
     * @param dto the dto
     */
    public void validateCustomerNetworkBindDetails(CustomerNetworkBindDTO dto) {
        final String SUB_MODULE = getModuleNameForLog() + "[validateCustomerNetworkBindDetails()]";
        if (dto.getPopid() == null) {
            logger.error("Module: {} - Please select POP", SUB_MODULE);
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select pop", null);
        }
        try {
            validateOLTMapping(dto, SUB_MODULE);
            validateOLTToDNSplitterBinding(dto, SUB_MODULE);
            validateDNToSNSplitterBinding(dto, SUB_MODULE);
        } catch (CustomValidationException ex) {
            logger.error("Module: {} - Validation failed: {}", SUB_MODULE, ex.getMessage());
            throw ex;
        }
    }

    /**
     * Validate olt mapping.
     *
     * @param dto the dto
     * @param module the module
     */
    private void validateOLTMapping(CustomerNetworkBindDTO dto, String module) {
        if (dto.getOltid() == null) return;
        int countMapping;
        Long productIdById = networkDeviceRepository.findProductIdById(dto.getOltid());
        if (getMvnoIdFromCurrentStaff() == 1) {
            countMapping = inventoryMappingRepo.countMappingByOwnerIdAndTypeAndProductId(
                    dto.getPopid(), CommonConstants.POP, productIdById
            );
        } else {
            countMapping = inventoryMappingRepo.countMappingByOwnerIdAndTypeAndProductIdAndMvnoIds(
                    dto.getPopid(), CommonConstants.POP, productIdById,
                    Arrays.asList(getMvnoIdFromCurrentStaff(), 1)
            );
        }
        if (countMapping == 0) {
            logger.error("Module: {} - Selected OLT ID: {} is not assigned to selected POP ID: {}", module, dto.getOltid(), dto.getPopid());
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Selected olt is not assign to selected pop", null);
        }
    }

    /**
     * Validate olt to dn splitter binding.
     *
     * @param dto the dto
     * @param module the module
     */
    private void validateOLTToDNSplitterBinding(CustomerNetworkBindDTO dto, String module) {
        if (dto.getDnsplitterid() == null) return;
        Integer count = networkdeviceBindRepository.countOutPortBindings(dto.getOltid(), dto.getDnsplitterid());
        if (count == 0) {
            logger.error("Module: {} - Selected OLT ID: {} and DN Splitter ID: {} are not bound", module, dto.getOltid(), dto.getDnsplitterid());
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Selected olt and selected dn splitter is not bind each other", null);
        }
    }

    /**
     * Validate dn to sn splitter binding.
     *
     * @param dto the dto
     * @param module the module
     */
    private void validateDNToSNSplitterBinding(CustomerNetworkBindDTO dto, String module) {
        if (dto.getSnsplitterid() == null) return;
        Integer count = networkdeviceBindRepository.countOutPortBindings(dto.getDnsplitterid(), dto.getSnsplitterid());
        if (count == 0) {
            logger.error("Module: {} - Selected DN Splitter ID: {} and SN Splitter ID: {} are not bound", module, dto.getDnsplitterid(), dto.getSnsplitterid());
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Selected dn splitter and selected sn splitter is not bind each other", null);
        }
    }
}
