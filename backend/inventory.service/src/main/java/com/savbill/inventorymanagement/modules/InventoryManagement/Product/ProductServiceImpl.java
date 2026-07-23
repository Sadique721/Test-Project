package com.savbill.inventorymanagement.modules.InventoryManagement.Product;

import com.savbill.inventorymanagement.core.constants.ClientServiceConstant;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.SearchConstants;
import com.savbill.inventorymanagement.core.utillity.fileUtillity.FileUtility;
import com.savbill.inventorymanagement.kafka.KafkaConstant;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapingDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.repository.NetworkDeviceRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanGroupMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificatioParametersRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParameters;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters.SpecificationParametersMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement.Vendor;
import com.savbill.inventorymanagement.modules.InventoryManagement.VendorManagement.VendorRepo;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchModel;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.core.mapper.IBaseMapper;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.ChargeManagement.Charge;
import com.savbill.inventorymanagement.modules.ChargeManagement.ChargeMapper;
import com.savbill.inventorymanagement.modules.ChargeManagement.ChargePojo;
import com.savbill.inventorymanagement.modules.ChargeManagement.ChargeRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.QExternalItemManagement;
import com.savbill.inventorymanagement.modules.InventoryManagement.ExternalItemManagement.ExternalItemManagementRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwner;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Warehouse.WareHouseManagmentServiceAreamappingRepo;
import com.savbill.inventorymanagement.modules.PlanService.PlanService;
import com.savbill.inventorymanagement.modules.PlanService.PlanServiceRepository;
import com.savbill.inventorymanagement.modules.PostpaidPlanCharge.PostpaidPlanChargeRepo;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.Tax;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.TaxRepository;
import com.savbill.inventorymanagement.modules.TaxManagement.Tax.TaxService;
import com.savbill.inventorymanagement.rabbitmq.ChargeMessage;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.savbill.inventorymanagement.utils.APIConstants;

import java.net.MalformedURLException;
import java.nio.file.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ExBaseAbstractService<ProductDto, Product, Long> {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    InOutWardMacRepo inOutWardMacRepo;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ProductCategoryRepository productCategoryRepository;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductPlanMappingRepository productPlanMappingRepository;
    @Autowired
    ProductPlanGroupMappingRepository productPlanGroupMappingRepository;
    @Autowired
    Productplanmappingmapper productplanmappingmapper;
    @Autowired
    ProductPlanGroupMappingMapper productPlanGroupMappingMapper;
    @Autowired
    InOutWardMacMapper inOutWardMacMapper;
    @Autowired
    ProductOwnerRepository productOwnerRepository;
    @Autowired
    PlanServiceRepository planServiceRepository;
    @Autowired
    ProductCategoryService productCategoryService;
    @Autowired
    ExternalItemManagementRepository externalItemManagementRepository;
    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    ChargeMapper chargeMapper;
    @Autowired
    StaffUserRepository staffRepository;
    @Autowired
    TaxRepository taxRepository;
    @Autowired
    TaxService taxService;
    @Autowired
    ProductCategoryMapper productCategoryMapper;
    @Autowired
    PostpaidPlanChargeRepo postpaidPlanChargeRepo;
    @Autowired
    WareHouseManagmentServiceAreamappingRepo wareHouseManagmentServiceAreamappingRepo;
    @Autowired
    ProductOwnerMapper productOwnerMapper;
    @Autowired
    InwardRepository inwardRepository;
    @Autowired
    VendorRepo vendorRepo;

    @Autowired
    ProductParameterMappingRepo productParameterMappingRepo;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    SpecificatioParametersRepo specificatioParametersRepo;

    @Autowired
    SpecificationParametersMapper specificationParametersMapper;

    @Autowired
    NetworkDeviceRepository networkDeviceRepository;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    ClientServiceService clientServiceSrv;

    public ProductServiceImpl(ProductRepository productRepository, IBaseMapper<ProductDto, Product> mapper) {
        super(productRepository, mapper);
    }
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    private OutwardRepository outwardRepository;

    @Override
    public String getModuleNameForLog() {
        return "[ProductServiceImpl]";
    }

    /**
     Get All Active Product
     * @Author Darshan
     * @return
     */
    GenericDataDTO getAllActiveProduct() {
        String SUBMODULE = getModuleNameForLog() + " [getAllActiveProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ProductDto> productDtoList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                productDtoList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext()))
                        .sorted(Comparator.comparing(ProductDto::getId).reversed()).collect(Collectors.toList());
            } else {
                productDtoList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext()))
                        .sorted(Comparator.comparing(ProductDto::getId).reversed()).collect(Collectors.toList());
            }
            if (productDtoList.size() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to fetch all active product :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setDataList(productDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Fetching all active product :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all active product : request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;

    }

    /**
     Get All Product BY Service Id
     * @Author Darshan
     * @param serviceId
     * @return
     */
    GenericDataDTO getAllProductByServiceId(Long serviceId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllProductByServiceId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> productList= new ArrayList<>();
        try {
            PlanService planService=planServiceRepository.findById(Math.toIntExact(serviceId)).orElse(null);
            List<ProductOwner> productOwnerList = new ArrayList<>();
            if (getLoggedInUser().getPartnerId() != 1) {
                productOwnerList = productOwnerRepository.findAllByOwnerType(CommonConstants.PRODUCT_OWNER.PARTNER_OWNERTYPE);
            } else {
                productOwnerList = productOwnerRepository.findAllByOwnerType(CommonConstants.PRODUCT_OWNER.STAFF_OWNERTYPE);
            }
            List<Long> Ids = productOwnerList.stream().map(ProductOwner::getProductId).collect(Collectors.toList());
            if (getMvnoIdFromCurrentStaff() == 1) {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndIdIn(CommonConstants.ACTIVE_STATUS, Ids).stream()
                        .filter(product -> (product.getProductCategory().isHasMac() ||
                                product.getProductCategory().isHasSerial()) &&
                                        (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                                product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                        .collect(Collectors.toList());
            } else {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndIdInAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                        .filter(product -> (product.getProductCategory().isHasMac() ||
                                product.getProductCategory().isHasSerial()) &&
                                        (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                                product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                        .collect(Collectors.toList());
            }
            if (planService != null) {
                if (planService.getIs_dtv() == false && productList != null) {
                    productList.stream().forEach(product -> {
                        if (product.getCaseId() == null) {
                            if (product.getNewProductCharge() != null) {
                                if (product.getNewProductCharge().getId() != 0) {
                                    Charge charge = chargeRepository.findById(product.getNewProductCharge().getId()).get();
                                    if (charge.getTaxamount() != null) {
                                        product.setNewProductAmount(charge.getPrice() + charge.getTaxamount());
                                    } else {
                                        product.setNewProductAmount(charge.getPrice());
                                    }
                                }
                            }
                            if (product.getRefurburshiedProductCharge() != null) {
                                if (product.getRefurburshiedProductCharge().getId() != 0) {
                                    Charge charge = chargeRepository.findById(product.getRefurburshiedProductCharge().getId()).get();
                                    if (charge.getTaxamount() != null) {
                                        product.setRefurburshiedProductAmount(charge.getPrice() + charge.getTaxamount());
                                    } else {
                                        product.setRefurburshiedProductAmount(charge.getPrice());
                                    }
                                }
                            }
                        }
                    });
                }
                if (planService.getIs_dtv() == true && productList != null) {
                    productList.stream().forEach(product -> {
                        if (product.getCaseId() != null) {
                            if (product.getNewProductCharge() != null) {
                                if (product.getNewProductCharge().getId() != 0) {
                                    Charge charge = chargeRepository.findById(product.getNewProductCharge().getId()).get();
                                    if (charge.getTaxamount() != null) {
                                        product.setNewProductAmount(charge.getPrice() + charge.getTaxamount());
                                    } else {
                                        product.setNewProductAmount(charge.getPrice());
                                    }
                                }
                            }
                            if (product.getRefurburshiedProductCharge() != null) {
                                if (product.getRefurburshiedProductCharge().getId() != 0) {
                                    Charge charge = chargeRepository.findById(product.getRefurburshiedProductCharge().getId()).get();
                                    if (charge.getTaxamount() != null) {
                                        product.setRefurburshiedProductAmount(charge.getPrice() + charge.getTaxamount());
                                    } else {
                                        product.setRefurburshiedProductAmount(charge.getPrice());
                                    }
                                }
                            }
                        }
                    });
                }
            }
            List<ProductDto> productDtoList = productList.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            productDtoList=productDtoList.stream().filter(x->!x.getHasAssetConsider()).collect(Collectors.toList());
            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all active products by service Id " + serviceId +" :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable fetch all active products by service Id " + serviceId +" :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;

    }

    /**
     Get All Active Network And NA Binded Product
     * @Author Darshan Bharambe
     * @return
     */
    public GenericDataDTO getAllNetworkandNaBindProduct() {
        String SUBMODULE = getModuleNameForLog() + " [getAllNetworkandNaBindProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> productList = new ArrayList<>();
        try {
           if (getMvnoIdFromCurrentStaff() == 1) {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                        .filter(product ->
                                (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND) ||
                                product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND) ||
                                product.getProductCategory().getType().equals(CommonConstants.NA_Bind)) &&
                                        (product.getProductCategory().isHasMac() ||
                                                product.getProductCategory().isHasSerial()))
                        .sorted(Comparator.comparing(Product::getId).reversed())
                        .collect(Collectors.toList());
            } else {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                        .filter(product ->
                                (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NA_Bind)) &&
                                        (product.getProductCategory().isHasMac() ||
                                                product.getProductCategory().isHasSerial()))
                        .sorted(Comparator.comparing(Product::getId).reversed())
                        .collect(Collectors.toList());
            }
            List<ProductDto> productDtoList = productList.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            productDtoList=productDtoList.stream().filter(x->x.getHasAssetConsider()!=null && !x.getHasAssetConsider()).collect(Collectors.toList());
            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all active network bind and na type of products :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.info("Fetching all active network bind and na type of products :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }



    public GenericDataDTO getAllNetworkandNaBindProduct(Long networkDeviceId,Long productId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllNetworkandNaBindProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> productList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                        .filter(product ->
                                (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NA_Bind)) &&
                                        (product.getProductCategory().isHasMac() ||
                                                product.getProductCategory().isHasSerial()))
                        .collect(Collectors.toList());
            } else {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                        .filter(product ->
                                (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NA_Bind)) &&
                                        (product.getProductCategory().isHasMac() ||
                                                product.getProductCategory().isHasSerial()))
                        .collect(Collectors.toList());
            }

            List<ProductDto> productDtoList = productList.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            productDtoList=productDtoList.stream().filter(x->x.getHasAssetConsider()!=null && !x.getHasAssetConsider()).collect(Collectors.toList());

            if(networkDeviceId!=null && networkDeviceId>=0)
            {
                NetworkDevices devices=networkDeviceRepository.findById(networkDeviceId).orElse(null);
                if(devices!=null)
                {
                    Integer totalInPort = devices.getTotalInPorts();
                    if(totalInPort==null || totalInPort.intValue()<=0)
                        totalInPort=0;

                    Integer totalOutPort = devices.getTotalOutPorts();
                    if(totalOutPort==null || totalOutPort.intValue()<=0)
                        totalOutPort=0;

                    Integer availableInPorts = devices.getAvailableInPorts();
                    if(availableInPorts==null || availableInPorts.intValue()<=0)
                        availableInPorts=0;

                    Integer availableOutPorts = devices.getAvailableOutPorts();
                    if(availableOutPorts==null || availableOutPorts.intValue()<=0)
                        availableOutPorts=0;


                    Integer finalTotalInPort = totalInPort;
                    Integer finalTotalOutPort = totalOutPort;
                    Integer finalAvailableInPorts = availableInPorts;
                    Integer finalAvailableOutPorts = availableOutPorts;

                    productDtoList.stream().forEach(x->{
                        if(x.getTotalInPorts()==null || x.getTotalInPorts()<=0)
                            x.setTotalInPorts(0);

                        if(x.getTotalOutPorts()==null || x.getTotalOutPorts()<=0)
                            x.setTotalOutPorts(0);

                        if(x.getAvailableInPorts()==null || x.getAvailableInPorts()<=0)
                            x.setAvailableInPorts(0);

                        if(x.getAvailableOutPorts()==null || x.getAvailableOutPorts()<=0)
                            x.setAvailableOutPorts(0);
                    });
                    productDtoList=productDtoList.stream().filter(x->x.productCategory.getDeviceType()!=null && x.productCategory.getDeviceType().equalsIgnoreCase(devices.getProduct().getProductCategory().getDeviceType())).collect(Collectors.toList());
                    Integer usedInPort=finalTotalInPort.intValue()-finalAvailableInPorts.intValue()==0?0:(finalTotalInPort.intValue()-finalAvailableInPorts.intValue());
                    Integer usedOutPort=finalTotalOutPort.intValue()-finalAvailableOutPorts.intValue()==0?0:(finalTotalOutPort.intValue()-finalAvailableOutPorts.intValue());
                    productDtoList=productDtoList.stream().filter(data->(finalTotalInPort.intValue()<=data.getTotalInPorts().intValue() && finalTotalOutPort.intValue()<=(data.getTotalOutPorts().intValue())) || (usedInPort<=data.getTotalInPorts().intValue() && usedOutPort<=data.getTotalOutPorts().intValue())).collect(Collectors.toList());
                }
            }
            else if(productId!=null)
            {
                Product product=productRepository.findById(productId).orElse(null);
                if(product!=null && product.getProductCategory()!=null)
                    productDtoList=productDtoList.stream().filter(x->x.getId().longValue()==productId).collect(Collectors.toList());
            }

            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all active network bind and na type of products :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.info("Fetching all active network bind and na type of products :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;

    }

    /**
     Get All Product List with Pagination
     * @Author Darshan
     * @param pageNumber
     * @param customPageSize
     * @param sortBy
     * @param sortOrder
     * @param filterList
     * @return
     */
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);;
        Page<Product> paginationList = null;
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                paginationList = productRepository.findAllByIsDeletedIsFalse(pageRequest);
            } else {
                paginationList = productRepository.findAllByIsDeletedIsFalseAndMvnoIdIn(Arrays.asList(getMvnoIdFromCurrentStaff(), 1),pageRequest);
            }
            paginationList.stream().forEach(r-> {
                if (r.getNewProductTax()!=null){
                    Tax newtax= taxRepository.findById(r.getNewProductTax().intValue()).orElse(null);
                    if ( newtax != null) {
                        r.setNewProductTax(newtax.getId().longValue());
                        r.setNewProductTaxName(newtax.getName());
                    }
                }
                
                if (r.getRefurburshiedProductTax() != null) {
                    Tax oldtax = taxRepository.findById(r.getRefurburshiedProductTax().intValue()).orElse(null);
                    if (oldtax != null) {
                        r.setRefurburshiedProductTax(oldtax.getId().longValue());
                        r.setRefurburshiedProductTaxName(oldtax.getName());
                    }
                }
            });

            paginationList.stream().forEach(data->{
                List<SpecificationParameters> specificationParameters=specificatioParametersRepo.findAllByProductCategory_Id(data.getProductCategory().getId());
                List<SpecificationParametersDTO> parametersDTOS=specificationParameters.stream().map(x->specificationParametersMapper.domainToDTO(x,new CycleAvoidingMappingContext())).collect(Collectors.toList());
                parametersDTOS.forEach(x->{
                    List<ProductParameterDefaultValueMapping> mappings=productParameterMappingRepo.getProductMappingByProductIdAndParamId(data.getId(),x.getId());
                    if(mappings!=null && !mappings.isEmpty())
                    {
                        String defaultValue=mappings.get(mappings.size()-1).getDefaultValue();
                        x.setDefaultValue(defaultValue);
                    }

                    if(x.getIsMultiValueParam()!=null && x.getIsMultiValueParam().equals(true))
                        x.setIsMultiValueParam(true);
                    else
                        x.setIsMultiValueParam(false);

                    if(x.getIsMultiValueParam().equals(true))
                        x.setParamMultiValues(Arrays.asList(x.getParamValues().split(",",-1)));
                });

                data.setSpecificationParametersDTOList(parametersDTOS);
            });

            if (paginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
            if (paginationList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to fetch all product :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Fetching all product :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all product :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Search Product
     * @Author Darshan
     * @param filterList
     * @param page
     * @param pageSize
     * @param sortBy
     * @param sortOrder
     * @return
     */
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getProductList(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Unable to serch product :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, APIConstants.FAIL, HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(), ex.getStackTrace());
        }
        return null;
    }

    /**
     Get Product List
     * @Author Darshan
     * @param name
     * @param pageRequest
     * @return
     */
    public GenericDataDTO getProductList(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getProductList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Product> productList = null;
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                productList = productRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
            } else {
                productList = productRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
            productList.stream().forEach(r -> {
                Charge newcharge = new Charge();
                Charge oldCharge = newcharge;
                Tax newtax = new Tax();
                Tax oldtax = new Tax();
                if (r.getNewProductCharge() != null){
//                Charge newcharge = chargeRepository.findByName(r.getName() + "-NewCharge-" + r.getId());
                    newcharge = chargeRepository.findById(r.getNewProductCharge().getId()).orElse(null);
                    newtax= taxRepository.findById(newcharge.getTaxId()).orElse(null);
                    if (newcharge != null && newtax != null) {
                        Long newProductPrice = Math.round(newcharge.getActualprice() + taxService.getTaxAmountFromCharge(newcharge, null));
                        r.setNewPrice(newProductPrice);
                        r.setNewProductTax(newcharge.getTaxId().longValue());
                        r.setNewProductTaxName(newtax.getName());
                    }
                }
                if (r.getRefurburshiedProductCharge() != null) {
//                Charge oldCharge = chargeRepository.findByName(r.getName() + "-RefurbishedCharge-" + r.getId());
                    oldCharge = chargeRepository.findById(r.getRefurburshiedProductCharge().getId()).orElse(null);
                    oldtax = taxRepository.findById(oldCharge.getTaxId()).orElse(null);
                    if (oldCharge != null && oldtax != null) {
                        Long refurbishedProductPrice = Math.round((oldCharge.getActualprice() + taxService.getTaxAmountFromCharge(oldCharge, null)));
                        r.setRefurburshiedPrice(refurbishedProductPrice);
                        r.setRefurburshiedProductTax(oldCharge.getTaxId().longValue());
                        r.setRefurburshiedProductTaxName(oldtax.getName());
                    }
                }
            });
            if (null != productList && 0 < productList.getSize()) {
                makeGenericResponse(genericDataDTO, productList);
            }
            if (productList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
                logger.info("Unable to search product by name " + name + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
                logger.info("Search product by name " + name + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to seatch product by name" + name + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get All
     * @Author Darshan
     * @param chargeType
     * @return
     */
    public GenericDataDTO getAllChargeByType(String chargeType) {
        String SUBMODULE = getModuleNameForLog() + " [getAllChargeByType()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
             List<ChargePojo> chargePojoList =chargeRepository.findAllByChargetypeAndIsDeleteIsFalse(chargeType).stream().map(data ->
                            chargeMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(charge -> (charge.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)) && (charge.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || charge.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1) && (charge.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(charge.getBuId()))).collect(Collectors.toList());
            genericDataDTO.setDataList(IterableUtils.toList(chargePojoList));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all charge by type " + chargeType + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
       } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all charge by type " + chargeType + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;

    }

    /**
     @Author Darshan
     * @param id
     * @return
     * @throws Exception
     */
    public Product deleteEntity(Long id) throws Exception {
        String SUBMOULE = getModuleNameForLog() + " [deleteEntity()] ";
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product!= null) {
                product.setIsDeleted(true);
                return productRepository.save(product);
            } else {
                return null;
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            logger.error("Unable to delete product with id " + id + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMOULE, HttpStatus.EXPECTATION_FAILED, e.getMessage());
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(),null);
        }
    }

    /**
     Save Product
     * @Author Darshan
     * @param entity
     * @return
     * @throws Exception
     */
    //@Override
    @Transactional
    public ProductDto saveEntity(ProductDto entity, MultipartFile file) throws Exception {
        try {
            String SUBMODULE = getModuleNameForLog() + " [uploadDocument()] ";
            String PATH;
            PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.PRODUCT_DOC_PATH).get(0).getValue();
            String subFolderName = File.separator + entity.getName().trim() + File.separator;
            String path = PATH + subFolderName;
            ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + path);
            if (entity.getHasOEMConsider().equals(true))
                entity.setHasOEMConsider(true);
            else
                entity.setHasOEMConsider(false);

            if(entity.getHasAssetConsider()!=null && entity.getHasAssetConsider().equals(true))
                entity.setHasAssetConsider(true);
            else
                entity.setHasAssetConsider(false);
            if (file != null && !file.isEmpty()) {
                entity.setUniquename(fileUtility.saveFileToServer(file, path));
            }
            ProductDto productDto= super.saveEntity(entity);
            productDto.setNewPrice(entity.getActualpricenewProduct());
            productDto.setRefurburshiedPrice(entity.getActualpricerefurbishedProduct());
            productDto.setNewProductTax(entity.getNewProductTax());
            productDto.setRefurburshiedProductTax(entity.getRefurburshiedProductTax());
            //create New Product Charge
            if(entity.getActualpricenewProduct()!=null){
                saveNewActualPriceProductCharge(entity, productDto);
            }
            //create Refurbished Charges
            if(entity.getActualpricerefurbishedProduct()!=null){
            saveRefActualPriceProductCharge(entity, productDto);
        }
            updateParamDefaultValue(productDto,entity);
            return productDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     Save Product From RMS
     * @Author Darshan
     * @param entity
     * @return
     * @throws Exception
     */
    public ProductDto saveEntityFromRms(ProductDto entity) throws Exception {
        try {
            validateProduct(entity);
            // ProductDto productDto= super.saveEntity(entity);
            ProductDto productDto = productMapper.domainToDTO(productRepository.save(productMapper.dtoToDomain(entity,new CycleAvoidingMappingContext())),new CycleAvoidingMappingContext()) ;
            productDto.setNewPrice(entity.getActualpricenewProduct());
            productDto.setRefurburshiedPrice(entity.getActualpricerefurbishedProduct());
            productDto.setNewProductTax(entity.getNewProductTax());
            productDto.setRefurburshiedProductTax(entity.getRefurburshiedProductTax());
            //create New Product Charge
            if(entity.getActualpricenewProduct()!=null){
                saveNewActualPriceProductCharge(entity, productDto);
            }
            //create Refurbished Charges
            if(entity.getActualpricerefurbishedProduct()!=null){
                saveRefActualPriceProductCharge(entity,productDto);
            }
            return productDto;
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     Update Product
     * @Author Darshan
     * @param entity
     * @return
     * @throws Exception
     */
    //@Override
    public ProductDto updateEntity(ProductDto entity,MultipartFile file) throws Exception {
        try {
            String SUBMODULE = getModuleNameForLog() + " [uploadDocument()] ";
            String PATH;
            PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.PRODUCT_DOC_PATH).get(0).getValue();
            String subFolderName = File.separator + entity.getName().trim() + File.separator;
            String path = PATH + subFolderName;
            ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + path);
            if (file != null && !file.isEmpty()) {
                entity.setUniquename(fileUtility.saveFileToServer(file, path));
            }
            Product product=productRepository.findById(entity.getId()).orElse(null);
            ProductDto productDto = super.saveEntity(entity);
            productDto.setNewPrice(entity.getActualpricenewProduct());
            productDto.setRefurburshiedPrice(entity.getActualpricerefurbishedProduct());
            //update new Charge
            Charge newcharge = chargeRepository.findByName(product.getName() + "-NewCharge-" + productDto.getId());
            if (newcharge != null && entity.getActualpricenewProduct()!=null && entity.getNewProductTax() != null) {
                updateNewActualPriceProductCharge(newcharge, entity, productDto);
            } else {
                //create New Product Charge
                if(entity.getActualpricenewProduct()!=null){
                    saveNewActualPriceProductCharge(entity, productDto);
                }
            }
            Charge oldCharge = chargeRepository.findByName(product.getName() + "-RefurbishedCharge-" + productDto.getId());
            if (oldCharge != null && entity.getActualpricerefurbishedProduct()!=null && entity.getRefurburshiedProductTax() != null) {
                updateRefActualPriceProductCharge(oldCharge,entity, productDto);
            } else {
                //create Refurbished Charges
                if(entity.getActualpricerefurbishedProduct()!=null){
                    saveRefActualPriceProductCharge(entity, productDto);
                }
            }

            updateParamDefaultValue(productDto,entity);
            return productDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     Find product id is deleted or not
     * @Author Darshan
     * @param id
     * @return
     */
    @Override
    public boolean deleteVerification(Integer id){
        boolean flag = false;
        Product product = productRepository.findById(Long.valueOf(id)).get();
        Long count = inwardRepository.countByProductIdAndIsDeletedFalse(product);
        if(count==0){
            flag=true;
        }
        return flag;
    }

    /**
     Get All Product By MacMapping Id
     * @Author Darshan
     * @param macMappingId
     * @return
     */
    public GenericDataDTO getAllProductsByMacSerial(Long macMappingId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllProductsByMacSerial()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> productList = new ArrayList<>();
        try {
            InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(macMappingId).get();
            Item item = itemRepository.getOne(inOutWardMACMapping.getItemId());
            Product product = productRepository.getOne(item.getProductId());
            ProductCategory productCategory = productCategoryRepository.getOne(product.getProductCategory().getId());
            if (productCategory.isHasMac() && productCategory.isHasSerial()) {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                            .filter(product1 -> product1.getProductCategory().getId().equals(product.getProductCategory().getId()) &&
                                    (product1.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                            product1.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)) &&
                                    product1.getProductCategory().isHasMac() &&
                                    product1.getProductCategory().isHasSerial())
                            .collect(Collectors.toList());
                } else {
                    productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                            .filter(product1 -> product1.getProductCategory().getId().equals(product.getProductCategory().getId()) &&
                                    (product1.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                            product1.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)) &&
                                    product1.getProductCategory().isHasMac() &&
                                    product1.getProductCategory().isHasSerial())
                            .collect(Collectors.toList());
                }
            } else if (!productCategory.isHasMac()) {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                            .filter(product1 -> product1.getProductCategory().getId().equals(product.getProductCategory().getId()) &&
                                    (product1.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                            product1.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)) &&
                                    !product1.getProductCategory().isHasMac() &&
                                    product1.getProductCategory().isHasSerial())
                            .collect(Collectors.toList());
                } else {
                    productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                            .filter(product1 -> product1.getProductCategory().getId().equals(product.getProductCategory().getId()) &&
                                    (product1.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                            product1.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)) &&
                                    !product1.getProductCategory().isHasMac() &&
                                    product1.getProductCategory().isHasSerial())
                            .collect(Collectors.toList());
                }
            }
            List<ProductDto> productDtoList = productList.stream().map(product1 -> productMapper.domainToDTO(product1, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all products by inoutward mac mappingid " + macMappingId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all products by inoutward mac mappingid " + macMappingId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get All Products By Product Category Id
     * @Author Darshan
     * @param id
     * @return
     */
    public GenericDataDTO getAllProductsByProductCategoryId(Long id){
        String SUBMODULE = getModuleNameForLog() + " [getAllProductsByProductCategoryId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> products = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                products = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                        .filter(product -> product.getProductCategory().getId().equals(id) &&
                                (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                        .collect(Collectors.toList());
            } else {
                products = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                        .filter(product -> product.getProductCategory().getId().equals(id) &&
                                (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                        .collect(Collectors.toList());
            }
            products.stream().forEach(product -> {
                if (product.getNewProductCharge() != null) {
                    if (product.getNewProductCharge().getId() != 0) {
                        Charge charge = chargeRepository.findById(product.getNewProductCharge().getId()).get();
                        if (charge.getTaxamount() != null) {
                            product.setNewProductAmount(charge.getPrice() + charge.getTaxamount());
                        } else {
                            product.setNewProductAmount(charge.getPrice());
                        }
                    }
                }
                if (product.getRefurburshiedProductCharge() != null) {
                    if (product.getRefurburshiedProductCharge().getId() != 0) {
                        Charge charge = chargeRepository.findById(product.getRefurburshiedProductCharge().getId()).get();
                        if (charge.getTaxamount() != null) {
                            product.setRefurburshiedProductAmount(charge.getPrice() + charge.getTaxamount());
                        } else {
                            product.setRefurburshiedProductAmount(charge.getPrice());
                        }
                    }
                }
            });
            List<ProductDto> productDtoList =  products.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all products by product category id " + id + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all products by product category id " + id + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get All Product Plan Mapping By Plan Id for Individual
     * @Author Darshan
     * @param planId
     * @return
     */
    public GenericDataDTO getAllPlanInventorysIdOnPlanId(Long planId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllPlanInventorysIdOnPlanId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<Productplanmapping> finalProductPlanMapping = productPlanMappingRepository.findAllByPlanId(planId).stream()
                    .filter(productplanmapping -> {
                Product product = productRepository.findById(productplanmapping.getProductId()).orElse(null);
                return product.getProductCategory().getDtvCategory() == null || !product.getProductCategory().getDtvCategory().equalsIgnoreCase(CommonConstants.PRODUCT_CATEGOTY.CARD_DTV_CATEGORY);
            }).collect(Collectors.toList());
            List<Productplanmappingdto> productplanmappingdtos = finalProductPlanMapping.stream().map(p -> productplanmappingmapper.domainToDTO(p, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            genericDataDTO.setDataList(productplanmappingdtos);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all products by plan id " + planId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all products by plan id " + planId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get All Product Plan Group Mapping By Plan Id and Plan Group Id for Plangroup
     * @Author Darshan
     * @param planId
     * @param planGroupId
     * @return
     */
    public List<ProductPlanGroupMapping> getAllInventoryIdOnPlanIdAndPlanGroupId(Long planId, Long planGroupId) {
        try {
            QProductPlanGroupMapping qProductPlanGroupMapping = QProductPlanGroupMapping.productPlanGroupMapping;
            BooleanExpression booleanExpression = qProductPlanGroupMapping.isNotNull().and(qProductPlanGroupMapping.planId.eq(planId)).and(qProductPlanGroupMapping.planGroupId.eq(planGroupId));
            List<ProductPlanGroupMapping> productPlanGroupMappingList = IterableUtils.toList(productPlanGroupMappingRepository.findAll(booleanExpression));
            productPlanGroupMappingList.stream().forEach(productPlanGroupMapping -> {
                if(productPlanGroupMapping.getName() == null) {
                    if (productPlanGroupMapping.getPlanId() != null) {
                        QProductplanmapping qProductplanmapping = QProductplanmapping.productplanmapping;
                        BooleanExpression booleanExpression1 = qProductplanmapping.planId.eq(productPlanGroupMapping.getPlanId()).and(qProductplanmapping.productId.eq(productPlanGroupMapping.getProductId())).and(qProductplanmapping.productCategoryId.eq(productPlanGroupMapping.getProductCategoryId()));
                        List<Productplanmapping> productplanmappings = IterableUtils.toList(productPlanMappingRepository.findAll(booleanExpression1));
                        if(productplanmappings != null || productplanmappings.size() != 0) {
                            productPlanGroupMapping.setName(productplanmappings.get(0).getName());
                        }
                    }
                }
            });
            List<ProductPlanGroupMapping> finalProductPlanGroupMappingList = productPlanGroupMappingList.stream()
                    .filter(productplanmapping -> {
                        Product product = productRepository.findById(productplanmapping.getProductId()).orElse(null);
                        return product.getProductCategory().getDtvCategory() == null || !product.getProductCategory().getDtvCategory().equalsIgnoreCase(CommonConstants.PRODUCT_CATEGOTY.CARD_DTV_CATEGORY);
                    }).collect(Collectors.toList());
            return finalProductPlanGroupMappingList;
        } catch (CustomValidationException exception) {
            exception.printStackTrace();
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),exception.getMessage(),null);
        }
    }

    /**
     Get Product CAtegory By Product Plan Group Mapping Id
     * @Author Darshan
     * @param mappingId
     * @return
     */
    public GenericDataDTO getProductCategoryByProductPlanGroupMappingId(Long mappingId) {
        String SUBMODULE = getModuleNameForLog() + " [getProductCategoryByProductPlanGroupMappingId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
       List<ProductCategory> productCategoryList = new ArrayList<>();
        try {
            List<ProductPlanGroupMapping> productPlanGroupMappingList = productPlanGroupMappingRepository.findAll().stream().filter(productPlanGroupMapping -> productPlanGroupMapping.getId().equals(mappingId)).collect(Collectors.toList());
            productCategoryList = productPlanGroupMappingList.stream()
                    .filter(productPlanGroupMapping -> productPlanGroupMapping.getProductCategoryId() != null)
                    .map(productPlanGroupMapping -> productCategoryRepository.findById(productPlanGroupMapping.getProductCategoryId()).get())
                    .collect(Collectors.toList());
            List<ProductCategoryDto> productCategoryDtoList = productCategoryList.stream().map(productCategory -> productCategoryMapper.domainToDTO(productCategory, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            genericDataDTO.setDataList(productCategoryDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all product category by product plan group mapping id " + mappingId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all product category by product plan group mapping id " + mappingId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get Product By Product Plan Group Mapping Id
     * @Author Darshan
     * @param mappingId
     * @return
     */
    public GenericDataDTO getProductByProductPlanGroupMappingId(Long mappingId) {
        String SUBMODULE = getModuleNameForLog() + " [getProductByProductPlanGroupMappingId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> productList = new ArrayList<>();
        try {
            ProductPlanGroupMapping productPlanGroupMappingList = productPlanGroupMappingRepository.findById(mappingId).orElse(null);
            if (productPlanGroupMappingList.getProductId() != null) {
                Product products = productRepository.findById(productPlanGroupMappingList.getProductId()).get();
                productList.add(products);
            } else {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                            .filter(product -> product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                    product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND))
                            .collect(Collectors.toList());
                } else {
                    productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                            .filter(product -> product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) || product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND))
                            .collect(Collectors.toList());
                }
            }
            List<ProductDto> productDtoList = productList.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all product by product plan group mapping id " + mappingId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all product by product plan group mapping id " + mappingId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get All Item By Product Ids
     * @Author Darshan
     * @param productId
     * @return
     */
    public GenericDataDTO getAllItemBasedOnProduct(List<Long> productId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllItemBasedOnProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<Item> itemList=itemRepository.findAllByProductIdIn(productId).stream()
                    .filter(r->r.getItemStatus().equalsIgnoreCase(CommonConstants.UNALLOCATED) &&
                            (r.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.SUBISU_OWNED) ||
                                    r.getOwnershipType().equalsIgnoreCase(CommonConstants.SERIALIZED_ITEM_OWNERSHIP_TYPE.ORGANIZATION_OWNED)))
                    .collect(Collectors.toList());
            List<InOutWardMACMapping> finalInOutWardMacMappingList=new ArrayList<>();
            itemList.stream().forEach(r->{
                List<InOutWardMACMapping> inOutWardMACMapping=inOutWardMacRepo.findAllByItemId(r.getId()).stream().filter(p->p.getCustInventoryMappingId()==null && p.getInventoryMappingId()==null && p.getBulkConsumptionId()==null && p.getIsForwarded()==0).collect(Collectors.toList());
               if(!(inOutWardMACMapping.isEmpty())) {
                    finalInOutWardMacMappingList.add(inOutWardMACMapping.get(0));
                }
            });
            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS=inOutWardMacMapper.domainToDTO(finalInOutWardMacMappingList,new CycleAvoidingMappingContext());
            inOutWardMACMapingDTOS.stream().forEach(r->{
                r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
            });
            genericDataDTO.setDataList(inOutWardMACMapingDTOS);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all item history by productId :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all item history by productId :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get All Product for Non Trackable Product Category
     * @Author Darshan
     * @return
     */
    public GenericDataDTO getAllProductForNonTrackableProductCategory() {
        String SUBMODULE = getModuleNameForLog() + " [getAllProductForNonTrackableProductCategory()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ProductOwner> productOwnerList = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        try {
           if (getLoggedInUser().getPartnerId() != 1) {
                productOwnerList = productOwnerRepository.findAllByOwnerType(CommonConstants.PARTNER);
            } else {
                productOwnerList = productOwnerRepository.findAllByOwnerType(CommonConstants.STAFF);
            }
           if (productOwnerList != null) {
               List<Long> Ids = productOwnerList.stream().map(ProductOwner::getProductId).collect(Collectors.toList());
               if (getMvnoIdFromCurrentStaff() == 1) {
                   products = productRepository.findAllByStatusAndIsDeletedIsFalseAndIdIn(CommonConstants.ACTIVE_STATUS, Ids).stream()
                           .filter(product ->
                                   !product.getProductCategory().isHasMac() &&
                                   !product.getProductCategory().isHasSerial() &&
                                   !product.getProductCategory().isHasTrackable() &&
                                   (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                           product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                           .collect(Collectors.toList());
               } else {
                   products = productRepository.findAllByStatusAndIsDeletedIsFalseAndIdInAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                           .filter(product ->
                                   !product.getProductCategory().isHasMac() &&
                                           !product.getProductCategory().isHasSerial() &&
                                           !product.getProductCategory().isHasTrackable() &&
                                           (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                                   product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                           .collect(Collectors.toList());
               }
               if (products != null) {
                   products.stream().forEach(product -> {
                       if (product.getNewProductCharge() != null) {
                           Charge charge = chargeRepository.findById(product.getNewProductCharge().getId()).get();
                           if (charge.getTaxamount() != null) {
                               product.setNewProductAmount(charge.getPrice() + charge.getTaxamount());
                           } else {
                               product.setNewProductAmount(charge.getPrice());
                           }
                       }
                   });
               }
           }
            List<ProductDto> productDtoList = products.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all non trackable product :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all non trackable product :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get All Customer Bind Product
     * @Author Darshan
     * @return
     */
    public GenericDataDTO getAllCBProducts() {
        String SUBMODULE = getModuleNameForLog() + " [getAllCBProducts()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> productList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                        .filter(product -> (product.getProductCategory().isHasMac() ||
                                product.getProductCategory().isHasSerial()) &&
                                        (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                                product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                        .collect(Collectors.toList());
            } else {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                        .filter(product -> (product.getProductCategory().isHasMac() ||
                                product.getProductCategory().isHasSerial()) &&
                                        (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                                product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                        .collect(Collectors.toList());
            }
            List<ProductDto> productDtoList = productList.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext()))
                    .sorted(Comparator.comparing(ProductDto::getId).reversed()).collect(Collectors.toList());
            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all non trackable product :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all non trackable product :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get All Product By Itemtype
     * @Author Darshan
     * @param itemType
     * @return
     */
    public GenericDataDTO getAllProductbasedOnItemType(String itemType) {
        String SUBMODULE = getModuleNameForLog() + " [getAllProductbasedOnItemType()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> productList = new ArrayList<>();
        try {
            if(itemType.equalsIgnoreCase(CommonConstants.PRODUCT.SERIALIZED_ITEM)) {
                if (getMvnoIdFromCurrentStaff() == 1) {
                    productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                            .filter(product -> product.getProductCategory().isHasTrackable() ||
                                    product.getProductCategory().isHasSerial())
                            .collect(Collectors.toList());
                } else {
                    productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                            .filter(product -> product.getProductCategory().isHasTrackable() ||
                                    product.getProductCategory().isHasSerial())
                            .collect(Collectors.toList());
                }
            }
            if(itemType.equalsIgnoreCase(CommonConstants.PRODUCT.NON_SERIALIZED_ITEM)){
                if (getMvnoIdFromCurrentStaff() == 1) {
                    productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                            .filter(product -> !product.getProductCategory().isHasTrackable() &&
                                    !product.getProductCategory().isHasSerial() &&
                                    !product.getProductCategory().isHasTrackable() &&
                                    !product.getProductCategory().isHasCas() &&
                                    !product.getProductCategory().isHasPort())
                            .collect(Collectors.toList());
                } else {
                    productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                            .filter(product -> !product.getProductCategory().isHasTrackable() &&
                                    !product.getProductCategory().isHasSerial() &&
                                    !product.getProductCategory().isHasTrackable() &&
                                    !product.getProductCategory().isHasCas() &&
                                    !product.getProductCategory().isHasPort())
                            .collect(Collectors.toList());
                }
            }
            List<ProductDto> productDtos = productList.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext()))
                    .sorted(Comparator.comparing(ProductDto::getId).reversed()).collect(Collectors.toList());
            genericDataDTO.setDataList(productDtos);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all product by item type " + itemType + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all product by item type " + itemType + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get ALl Serialized Item By Prodyct Id, Item Type, Owner id, Owner type
     * @Author Darshan
     * @param productId
     * @param itemType
     * @param ownerId
     * @param ownerType
     * @return
     */
    public GenericDataDTO getAllSerializedItemBaseOnProduct(Long productId,String itemType, Long ownerId, String ownerType) {
        String SUBMODULE = getModuleNameForLog() + " [getAllSerializedItemBaseOnProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = new ArrayList<>();
        try {
            List<Item> itemList = itemRepository.findAllByIsDeletedIsFalseAndProductIdAndOwnerIdAndOwnerType(productId, ownerId, ownerType);
            itemList = itemList.stream().filter(item -> item.getItemStatus().equalsIgnoreCase(CommonConstants.UNALLOCATED) ||
                            item.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE) ||
                            item.getItemStatus().equalsIgnoreCase(CommonConstants.STAFF_ALLOCATED))
                    .collect(Collectors.toList());
            List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
            List<InOutWardMACMapping> inOutWardMACMappingList = inOutWardMacRepo.findAllByItemIdIn(itemIds).stream()
                    .filter(inOutWardMACMapping -> inOutWardMACMapping.getIsForwarded().equals(0) &&
                            inOutWardMACMapping.getCustInventoryMappingId() == null &&
                            inOutWardMACMapping.getBulkConsumptionId() == null)
                    .collect(Collectors.toList());
            inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
            inOutWardMACMapingDTOS.stream().forEach(r -> {
                Item item = itemRepository.findById(r.getItemId()).orElse(null);
                Product product = productRepository.findById(item.getProductId()).orElse(null);
                r.setProductId(product.getId());
                r.setProductName(product.getName());
                r.setHasMac(product.getProductCategory().isHasMac());
                r.setHasSerial(product.getProductCategory().isHasSerial());
                r.setCondition(item.getCondition());
                r.setOwnerShip(item.getOwnershipType());
            });
            genericDataDTO.setDataList(inOutWardMACMapingDTOS);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all item history by productId " + productId + ", item type " + itemType + ", owner id " + ownerId + " and owner type " + ownerType + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all item history by productId " + productId + ", item type " + itemType + ", owner id " + ownerId + " and owner type " + ownerType + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get All External Item Product By Customer Owned Or Customer Id
     * @Author Darshan
     * @param custId
     * @return
     */
    public GenericDataDTO getAllProductsByCustomerOwned(Long custId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            StaffUser loggedInUser = staffRepository.findById(getLoggedInUserId()).orElse(null);
            if(loggedInUser != null && loggedInUser.getPartnerid()==1) {
                QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
                BooleanExpression exp1 = qExternalItemManagement.isNotNull()
                        .and(qExternalItemManagement.isDeleted.eq(false))
                        .and(qExternalItemManagement.ownerId.eq(custId)
                                .and(qExternalItemManagement.ownershipType.equalsIgnoreCase(CommonConstants.EXTERNALITEM.CUSTOMER_OWNED)));
                List<ExternalItemManagement> externalItemManagementList = (List<ExternalItemManagement>) externalItemManagementRepository.findAll(exp1);
                List<Product> productLists = new ArrayList<>();
                externalItemManagementList.forEach(r -> {
                    Product product = productRepository.getOne(r.getProductId().getId());
                    productLists.add(product);
                });
                List<Long> prodIds = productLists.stream().map(Product::getId).collect(Collectors.toList());
                QProduct qProduct = QProduct.product;
                BooleanExpression booleanExpression2 = qProduct.isNotNull()
                        .and(qProduct.id.in(prodIds))
                        .and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS))
                        .and(qProduct.isDeleted.eq(false))
                        .and(qProduct.productCategory.type.contains(CommonConstants.CUSTOMER_BIND))
                        .and(qProduct.productCategory.hasSerial.eq(true));
                if (getMvnoIdFromCurrentStaff() != 1)
                    booleanExpression2 = booleanExpression2.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                List<Product> listOfProducts = IterableUtils.toList(this.productRepository.findAll(booleanExpression2));
                if (!listOfProducts.isEmpty()) {
                    genericDataDTO.setDataList(listOfProducts);
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                } else {
                    throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No any external item product binded with this customer.", null);
                }
            }else{

                QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;

                assert loggedInUser != null;
                BooleanExpression exp1 = qExternalItemManagement.isNotNull()
                        .and(qExternalItemManagement.isDeleted.eq(false))
                        .and(qExternalItemManagement.isDeleted.eq(false))
                        .and(qExternalItemManagement.ownerId.eq(custId)
                                .and(qExternalItemManagement.ownershipType.equalsIgnoreCase(CommonConstants.EXTERNALITEM.CUSTOMER_OWNED))
                                .or(qExternalItemManagement.ownerId.eq(loggedInUser.getPartnerid().longValue())
                                        .and(qExternalItemManagement.ownershipType.equalsIgnoreCase(CommonConstants.EXTERNALITEM.PARTNER_OWNED))));

                List<ExternalItemManagement> externalItemManagementList = (List<ExternalItemManagement>) externalItemManagementRepository.findAll(exp1);
                List<Product> productLists = new ArrayList<>();
                externalItemManagementList.forEach(r -> {
                    Product product = productRepository.getOne(r.getProductId().getId());
                    productLists.add(product);
                });
                List<Long> prodIds = productLists.stream().map(Product::getId).collect(Collectors.toList());
                QProduct qProduct = QProduct.product;
                BooleanExpression booleanExpression2 = qProduct.isNotNull().and(qProduct.id.in(prodIds)).and(qProduct.status.eq(CommonConstants.ACTIVE_STATUS)).and(qProduct.isDeleted.eq(false)).and(qProduct.productCategory.type.contains(CommonConstants.CUSTOMER_BIND)).and(qProduct.productCategory.hasSerial.eq(true));
                if (getMvnoIdFromCurrentStaff() != 1)
                    booleanExpression2 = booleanExpression2.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                List<Product> listOfProducts = IterableUtils.toList(this.productRepository.findAll(booleanExpression2));
                if (!listOfProducts.isEmpty()) {
                    genericDataDTO.setDataList(listOfProducts);
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                } else {
                    throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No any external item product binded with this customer.", null);
                }

            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }

    /**
     Get All Network and NA Bind Non Serialized Product
     * @Author Darshan
     * @return
     */
    public GenericDataDTO getAllNetworkAndNABindNonSerializedProduct() {
        String SUBMODULE = getModuleNameForLog() + " [getAllNetworkAndNABindNonSerializedProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ProductOwner> productOwnerList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        try {
            if (getLoggedInUser().getPartnerId() != 1) {
                productOwnerList = productOwnerRepository.findAllByOwnerType(CommonConstants.PARTNER);
            } else {
                productOwnerList = productOwnerRepository.findAllByOwnerType(CommonConstants.STAFF);
            }
            List<Long> Ids = productOwnerList.stream().map(ProductOwner::getProductId).collect(Collectors.toList());
            if (getMvnoIdFromCurrentStaff() == 1) {
              productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndIdIn(CommonConstants.ACTIVE_STATUS, Ids).stream()
                      .filter(product -> (!product.getProductCategory().isHasMac() &&
                              !product.getProductCategory().isHasSerial() &&
                              !product.getProductCategory().isHasTrackable()) &&
                              (product.getProductCategory().getType().equals(CommonConstants.NA_Bind) ||
                                      product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND) ||
                                      product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                      .collect(Collectors.toList());
            } else {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndIdInAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                productList=productList.stream()
                        .filter(product -> (!product.getProductCategory().isHasMac() &&
                                !product.getProductCategory().isHasSerial() &&
                                !product.getProductCategory().isHasTrackable()) &&
                                (product.getProductCategory().getType().equals(CommonConstants.NA_Bind) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                        .collect(Collectors.toList());
            }
            List<ProductDto> productDtoList = productList.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all network bind and na bind non serialized item :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all network bind and na bind non serialized item :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }



    public GenericDataDTO getAllNetworkAndNABindNonSerializedProduct(Long networkDeviceId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllNetworkAndNABindNonSerializedProduct()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ProductOwner> productOwnerList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        try {
            if (getLoggedInUser().getPartnerId() != 1) {
                productOwnerList = productOwnerRepository.findAllByOwnerType(CommonConstants.PARTNER);
            } else {
                productOwnerList = productOwnerRepository.findAllByOwnerType(CommonConstants.STAFF);
            }
            List<Long> Ids = productOwnerList.stream().map(ProductOwner::getProductId).collect(Collectors.toList());
            if (getMvnoIdFromCurrentStaff() == 1) {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndIdIn(CommonConstants.ACTIVE_STATUS, Ids).stream()
                        .filter(product -> (!product.getProductCategory().isHasMac() &&
                                !product.getProductCategory().isHasSerial() &&
                                !product.getProductCategory().isHasTrackable()) &&
                                (product.getProductCategory().getType().equals(CommonConstants.NA_Bind) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                        .collect(Collectors.toList());
            } else {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndIdInAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Ids, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                        .filter(product -> (!product.getProductCategory().isHasMac() &&
                                !product.getProductCategory().isHasSerial() &&
                                !product.getProductCategory().isHasTrackable()) &&
                                (product.getProductCategory().getType().equals(CommonConstants.NA_Bind) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND)))
                        .collect(Collectors.toList());
            }
            NetworkDevices devices=networkDeviceRepository.findById(networkDeviceId).orElse(null);
            List<ProductDto> productDtoList = productList.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            productDtoList=productDtoList.stream().filter(x->x.getHasAssetConsider()!=null && !x.getHasAssetConsider()).collect(Collectors.toList());
            if(devices!=null)
            {
                Integer totalInPort = devices.getTotalInPorts();
                if(totalInPort==null || totalInPort.intValue()<=0)
                    totalInPort=0;

                Integer totalOutPort = devices.getTotalOutPorts();
                if(totalOutPort==null || totalOutPort.intValue()<=0)
                    totalOutPort=0;

                Integer availableInPorts = devices.getAvailableInPorts();
                if(availableInPorts==null || availableInPorts.intValue()<=0)
                    availableInPorts=0;

                Integer availableOutPorts = devices.getAvailableOutPorts();
                if(availableOutPorts==null || availableOutPorts.intValue()<=0)
                    availableOutPorts=0;


                Integer finalTotalInPort = totalInPort;
                Integer finalTotalOutPort = totalOutPort;
                Integer finalAvailableInPorts = availableInPorts;
                Integer finalAvailableOutPorts = availableOutPorts;

                productDtoList.stream().forEach(x->{
                    if(x.getTotalInPorts()==null || x.getTotalInPorts()<=0)
                        x.setTotalInPorts(0);

                    if(x.getTotalOutPorts()==null || x.getTotalOutPorts()<=0)
                        x.setTotalOutPorts(0);

                    if(x.getAvailableInPorts()==null || x.getAvailableInPorts()<=0)
                        x.setAvailableInPorts(0);

                    if(x.getAvailableOutPorts()==null || x.getAvailableOutPorts()<=0)
                        x.setAvailableOutPorts(0);
                });
                productDtoList=productDtoList.stream().filter(x->x.productCategory.getDeviceType()!=null && x.productCategory.getDeviceType().equalsIgnoreCase(devices.getProduct().getProductCategory().getDeviceType())).collect(Collectors.toList());
                Integer usedInPort=finalTotalInPort.intValue()-finalAvailableInPorts.intValue()==0?0:(finalTotalInPort.intValue()-finalAvailableInPorts.intValue());
                Integer usedOutPort=finalTotalOutPort.intValue()-finalAvailableOutPorts.intValue()==0?0:(finalTotalOutPort.intValue()-finalAvailableOutPorts.intValue());

                productDtoList=productDtoList.stream().filter(data->(finalTotalInPort.intValue()<=data.getTotalInPorts().intValue() && finalTotalOutPort.intValue()<=(data.getTotalOutPorts().intValue())) || (usedInPort<=data.getTotalInPorts().intValue() && usedOutPort<=data.getTotalOutPorts().intValue())).collect(Collectors.toList());
            }
            
            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all network bind and na bind non serialized item :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all network bind and na bind non serialized item :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get Product Plan Group Mapping By Product Id, Plan Group Id, Plan Id, Product Category Id
     * @Author Darshan
     * @param planGroupId
     * @param planId
     * @param productCategoryId
     * @param productId
     * @return
     */
    public GenericDataDTO getProductPlanGroupMappingDetails(Long planGroupId, Long planId, Long productCategoryId, Long productId) {
        String SUBMODULE = getModuleNameForLog() + " [getProductPlanGroupMappingDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<ProductPlanGroupMapping> productPlanGroupMappingList = new ArrayList<>();
        try {
            productPlanGroupMappingList = productPlanGroupMappingRepository.findAllByPlanIdAndPlanGroupId(planId, planGroupId).stream()
                    .filter(productPlanGroupMapping -> productPlanGroupMapping.getProductCategoryId().equals(productCategoryId) &&
                            productPlanGroupMapping.getProductId().equals(productId))
                    .collect(Collectors.toList());
            List<ProductPlanGroupMappingDto> productPlanGroupMappingDtoList = productPlanGroupMappingList.stream().map(productPlanGroupMapping -> productPlanGroupMappingMapper.domainToDTO(productPlanGroupMapping, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            genericDataDTO.setDataList(productPlanGroupMappingDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all product plan group mapping by productId " + productId + ", planId " + planId + ", plan group id " + planGroupId + " and product category id " + productCategoryId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all product plan group mapping by productId " + productId + ", planId " + planId + ", plan group id " + planGroupId + " and product category id " + productCategoryId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Get Product Plan Group Mapping By Product Id, Plan Id, Product Category Id
     * @Author Darshan
     * @param planId
     * @param productCategoryId
     * @param productId
     * @return
     */
    public GenericDataDTO getProductPlanMappingDetails(Long planId, Long productCategoryId, Long productId) {
        String SUBMODULE = getModuleNameForLog() + " [getProductPlanGroupMappingDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Productplanmapping> productplanmappingList = new ArrayList<>();
        try {
            productplanmappingList = productPlanMappingRepository.findAllByPlanId(planId).stream()
                    .filter(productplanmapping -> productplanmapping.getProductCategoryId().equals(productCategoryId) &&
                            productplanmapping.getProductId().equals(productId))
                    .collect(Collectors.toList());
            List<Productplanmappingdto> productplanmappingdtoList = productplanmappingList.stream().map(productplanmapping -> productplanmappingmapper.domainToDTO(productplanmapping, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            genericDataDTO.setDataList(productplanmappingdtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all product plan mapping by productId " + productId + ", planId " + planId + " and product category id " + productCategoryId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        }catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch all product plan mapping by productId " + productId + ", planId " + planId + " and product category id " + productCategoryId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), ex.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Validate Product
     * @Author Darshan
     * @param entityDto
     */
    public void validateProduct(ProductDto entityDto) throws Exception {
        try {
            if (entityDto.getActualpricenewProduct() != null) {
                if (entityDto.getNewProductTax() == null) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select new product tax", null);
                }
            }
            if (entityDto.getActualpricerefurbishedProduct() != null) {
                if (entityDto.getRefurburshiedProductTax() == null) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select refurburshied product tax", null);
                }
            }
            if (entityDto.getNewProductTax() != null) {
                if (entityDto.getActualpricenewProduct() == null) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter Actual product price", null);
                }
            }
            if (entityDto.getRefurburshiedProductTax() != null) {
                if (entityDto.getActualpricerefurbishedProduct() == null) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter refurburshied Actual product price", null);
                }
            }
            ProductCategoryDto productCategoryDto = productCategoryService.getEntityById(entityDto.getProductCategory().getId());
            if (productCategoryDto.isHasCas()) {
                if (entityDto.getCaseId() == null) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select CAS", null);
                }
            }

            if(entityDto.getSpecificationParametersDTOList()!=null && !entityDto.getSpecificationParametersDTOList().isEmpty())
            {
                entityDto.getSpecificationParametersDTOList().stream().forEach(data->{
                    if(data.getDefaultValue()!=null && data.getDefaultValue().length()>40)
                    {
                        throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "40 Character value are allowed for Parameter "+data.getParamName(), null);
                    }

                    if(data.getParamValue()!=null && data.getIsMultiValueParam())
                    {
                        data.getParamMultiValues().stream().forEach(data1->{
                            if(data1.length()>40)
                                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),"40 Character value are allowed for Parameter "+data.getParamName(), null);
                        });
                    }
                });
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     Get All Active Products By Product Category Id
     * @Author Darshan
     * @param productCategoryId
     * @return
     */
    public GenericDataDTO getAllActiveProductsByProductCategoryId(Long productCategoryId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllActiveProductsByProductCategoryId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> productList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                        .filter(product -> product.getProductCategory().getId().equals(productCategoryId))
                        .collect(Collectors.toList());
            } else {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                        .filter(product -> product.getProductCategory().getId().equals(productCategoryId))
                        .collect(Collectors.toList());
            }
            List<ProductDto> productDtoList = productList.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            genericDataDTO.setDataList(productDtoList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            logger.info("Fetching all product by product category id " + productCategoryId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}}", SUBMODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            logger.error("Unable to fetch all product by product category id " + productCategoryId + " :  request: { Module : {}}; Response : {Code :{}; Message :{}; Exception :{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(), e.getStackTrace());
        }
        return genericDataDTO;
    }

    /**
     Duplicate Varification
     * @Author Darshan
     * @param productDto
     * @param operation
     * @return
     */
    public boolean duplicateVarification(ProductDto productDto, Integer operation) {
        try {
            boolean flag = false;
            QProduct qProduct = QProduct.product;
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            String name = productDto.getName();
            if(name != null) {
                name = name.trim();
                Long count = null;
                if (getMvnoIdFromCurrentStaff() == 1) {
                    count = productRepository.countByNameAndIsDeletedIsFalse(name);
                } else if (getMvnoIdFromCurrentStaff() != 1){
                    count = productRepository.countByNameAndIsDeletedIsFalseAndMvnoIdIn(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                }
                if (operation.equals(CommonConstants.OPERATION_ADD)) {
                    if (count == 0) {
                        flag = true;
                    }
                } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                    if (count >= 1) {
                        Long countEdit = null;
                        if (getMvnoIdFromCurrentStaff() == 1) {
                            countEdit = productRepository.countByNameAndIdAndIsDeletedIsFalse(name, productDto.getId());
                        } else {
                            countEdit = productRepository.countByNameAndIdAndIsDeletedIsFalseAndMvnoIdIn(name, productDto.getId(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                        }
                        if (countEdit == 1) {
                            flag = true;
                        }
                    } else {
                        flag = true;
                    }
                }
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     Find duplicate ProductId Varification at Save and Update Product
     * @Author Darshan
     * @param productDto
     * @param operation
     * @return
     */
    public boolean duplicateProductIdVarification(ProductDto productDto, Integer operation) {
        try {
            boolean flag = false;
            QProduct qProduct = QProduct.product;
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            String productId = productDto.getProductId();
            if(productId != null) {
                productId = productId.trim();
                Long count = null;
                if (getMvnoIdFromCurrentStaff() == 1) {
                    count = productRepository.countByProductIdAndIsDeletedFalse(productId);
                } else {
                    count = productRepository.countByProductIdAndIsDeletedFalseAndMvnoIdIn(productId, Arrays.asList(getMvnoIdFromCurrentStaff(),1));
                }
                if (operation.equals(CommonConstants.OPERATION_ADD)) {
                    if (count == 0) {
                        flag = true;
                    }
                } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                    if (count >= 1) {
                        Long countEdit = null;
                        if (getMvnoIdFromCurrentStaff() == 1) {
                            countEdit = productRepository.countByProductIdAndIsDeletedFalseAndId(productId, productDto.getId());
                        } else {
                            countEdit = productRepository.countByProductIdAndIsDeletedFalseAndMvnoIdInAndId(productId, Arrays.asList(getMvnoIdFromCurrentStaff(), 1), productDto.getId());
                        }
                        if (countEdit == 1) {
                            flag = true;
                        }
                    } else {
                        flag = true;
                    }
                }
            } else {
                flag = true;
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public GenericDataDTO getAllNetworkDeviceProduct() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> productList = new ArrayList<>();
        List<ProductDto> productDtoList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                        .filter(product ->
                                product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND) )
                        .collect(Collectors.toList());
            } else {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                        .filter(product ->
                                product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND) )
                        .collect(Collectors.toList());
            }
            productDtoList = productList.stream().map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            if (productDtoList != null) {
                genericDataDTO.setDataList(productDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }

    public GenericDataDTO filterProductsByDeviceType(String deviceType) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Product> productList = new ArrayList<>();
        List<ProductDto> productDtoList = new ArrayList<>();
        try {
            if (getMvnoIdFromCurrentStaff() == 1) {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS).stream()
                        .filter(product ->
                                (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND)) &&
                                        deviceType.equalsIgnoreCase(product.getProductCategory().getDeviceType())
                        ).collect(Collectors.toList());
            } else {
                productList = productRepository.findAllByStatusAndIsDeletedIsFalseAndMvnoIdIn(
                                CommonConstants.ACTIVE_STATUS,
                                Arrays.asList(getMvnoIdFromCurrentStaff(), 1)).stream()
                        .filter(product ->
                                (product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.CUSTOMER_BIND_NETWORK_BIND) ||
                                        product.getProductCategory().getType().equals(CommonConstants.NETWORK_BIND)) &&
                                        deviceType.equalsIgnoreCase(product.getProductCategory().getDeviceType())
                        ).collect(Collectors.toList());
            }

            productDtoList = productList.stream()
                    .map(product -> productMapper.domainToDTO(product, new CycleAvoidingMappingContext()))
                    .collect(Collectors.toList());

            if (!productDtoList.isEmpty()) {
                genericDataDTO.setDataList(productDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(MessageConstants.DATA_NOT_FOUND);
            }
        } catch (CustomValidationException e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }

    public void saveNewActualPriceProductCharge(ProductDto entity, ProductDto productDto) {
        try {
            Double newProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getNewProductTax()),productDto.getNewPrice());
            Optional<Tax> tax= taxRepository.findById(Math.toIntExact(entity.getNewProductTax()));
            if (!tax.isPresent()) {
               throw new NoSuchElementException("Tax with id " + entity.getNewProductTax() + " not found");
            }
            // Tax tax = taxOptional.get();
            Charge charge=new Charge();
            charge.setName(productDto.getName()+"-NewCharge-"+productDto.getId());
            charge.setChargecategory("Installation Charge");
            charge.setChargetype("CUSTOMER_DIRECT");
            charge.setService(null);
            charge.setDesc("Product Charge");
            charge.setStatus("Active");
            charge.setActualprice(newProductPriceWithoutTax);
            charge.setPrice(newProductPriceWithoutTax);
            charge.setTaxId(tax.get().getId());
            charge.setTaxamount(entity.getActualpricenewProduct()-newProductPriceWithoutTax);
            charge.setIsDelete(true);
            charge.setIsinventorycharge(true);
            charge.setCreatedById(getLoggedInUserId());
            charge.setLastModifiedById(getLoggedInUserId());
            charge.setMvnoId(getMvnoIdFromCurrentStaff());
            charge.setProductId(productDto.getId());

            ChargeMessage chargeMessage = new ChargeMessage(charge);
//        this.messageSender.send(chargeMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_NEW_CHARGE_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(chargeMessage,ChargeMessage.class.getSimpleName(), KafkaConstant.CREATE_NEW_CHARGE));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void saveRefActualPriceProductCharge(ProductDto entity, ProductDto productDto){
        try {
            Double refurbishedProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getRefurburshiedProductTax()),productDto.getRefurburshiedPrice());
            Optional<Tax> tax= taxRepository.findById(Math.toIntExact(entity.getRefurburshiedProductTax()));
            Charge charge=new Charge();
            charge.setName(productDto.getName()+"-RefurbishedCharge-"+productDto.getId());
            charge.setChargecategory("Installation Charge");
            charge.setChargetype("Customer Direct");
            charge.setService(null);
            charge.setDesc("Product Charge");
            charge.setStatus("Active");
            charge.setActualprice(refurbishedProductPriceWithoutTax);
            charge.setPrice(refurbishedProductPriceWithoutTax);
            charge.setTaxId(tax.get().getId());
            charge.setTaxamount(entity.getActualpricerefurbishedProduct()-refurbishedProductPriceWithoutTax);
            charge.setIsDelete(true);
            charge.setIsinventorycharge(true);
            charge.setCreatedById(getLoggedInUserId());
            charge.setLastModifiedById(getLoggedInUserId());
            charge.setMvnoId(getMvnoIdFromCurrentStaff());
            charge.setProductId(productDto.getId());
            ChargeMessage chargeMessage = new ChargeMessage(charge);
//        this.messageSender.send(chargeMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_CREATE_REF_CHARGE_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(chargeMessage,ChargeMessage.class.getSimpleName(),KafkaConstant.CREATE_REF_CHARGE));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public void updateNewActualPriceProductCharge(Charge newcharge, ProductDto entity, ProductDto productDto) {
        try {
            Double newProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getNewProductTax()),productDto.getNewPrice());
            Optional<Tax> tax= taxRepository.findById(Math.toIntExact(entity.getNewProductTax()));
            newcharge.setName(productDto.getName() + "-NewCharge-" + productDto.getId());
            newcharge.setChargecategory("Installation Charge");
            newcharge.setChargetype("Customer Direct");
            newcharge.setService(null);
            newcharge.setDesc("Product Charge");
            newcharge.setStatus("Active");
            newcharge.setActualprice(newProductPriceWithoutTax);
            newcharge.setPrice(newProductPriceWithoutTax);
//            newcharge.setTax(taxRepository.findById(Math.toIntExact(entity.getNewProductTax())).orElse(null));
            newcharge.setTaxId(tax.get().getId());
            newcharge.setTaxamount(entity.getActualpricenewProduct()-newProductPriceWithoutTax);
            newcharge.setIsDelete(true);
            newcharge.setIsinventorycharge(true);
            newcharge.setCreatedById(getLoggedInUserId());
            newcharge.setLastModifiedById(getLoggedInUserId());
            newcharge.setMvnoId(getMvnoIdFromCurrentStaff());
            newcharge.setProductId(productDto.getId());
            ChargeMessage chargeMessage = new ChargeMessage(newcharge);
//        this.messageSender.send(chargeMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_NEW_CHARGE_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(chargeMessage,ChargeMessage.class.getSimpleName(),KafkaConstant.UPDATE_NEW_CHARGE));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void updateRefActualPriceProductCharge(Charge oldCharge, ProductDto entity, ProductDto productDto) {
        try {
            Double refurbishedProductPriceWithoutTax=taxService.getPriceWithoutTax(Math.toIntExact(entity.getRefurburshiedProductTax()),productDto.getRefurburshiedPrice());
            Optional<Tax> tax= taxRepository.findById(Math.toIntExact(entity.getRefurburshiedProductTax()));
            oldCharge.setName(productDto.getName() + "-RefurbishedCharge-" + productDto.getId());
            oldCharge.setChargecategory("Installation Charge");
            oldCharge.setChargetype("CUSTOMER_DIRECT");
            oldCharge.setService(null);
            oldCharge.setDesc("Product Charge");
            oldCharge.setStatus("Active");
            oldCharge.setActualprice(entity.getActualpricerefurbishedProduct());
            oldCharge.setActualprice(refurbishedProductPriceWithoutTax);
            oldCharge.setPrice(refurbishedProductPriceWithoutTax);
//            oldCharge.setTax(taxRepository.findById(Math.toIntExact(entity.getRefurburshiedProductTax())).orElse(null));
            oldCharge.setTaxId(tax.get().getId());
            oldCharge.setTaxamount(entity.getActualpricerefurbishedProduct()-refurbishedProductPriceWithoutTax);
            oldCharge.setIsDelete(true);
            oldCharge.setIsinventorycharge(true);
            oldCharge.setCreatedById(getLoggedInUserId());
            oldCharge.setLastModifiedById(getLoggedInUserId());
            oldCharge.setMvnoId(getMvnoIdFromCurrentStaff());
            oldCharge.setProductId(productDto.getId());
            ChargeMessage chargeMessage = new ChargeMessage(oldCharge);
//        this.messageSender.send(chargeMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_UPDATE_REF_CHARGE_TO_CMS);
            kafkaMessageSender.send(new KafkaMessageData(chargeMessage,ChargeMessage.class.getSimpleName(),KafkaConstant.UPDATE_REF_CHARGE));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProductDto getEntityById(Long id) {
        try {
            Product product = productRepository.findById(id).orElse(null);
            ProductDto productDto = new ProductDto();
            productDto.setId(id);
            productDto.setHasOEMConsider(product.isHasOEMConsider());
            productDto.setName(product.getName());
            productDto.setDescription(product.getDescription());
            productDto.setStatus(product.getStatus());
            productDto.setMvnoId(product.getMvnoId());
            productDto.setIsDeleted(product.getIsDeleted());
            productDto.setExpiryTime(product.getExpiryTime());
            productDto.setExpiryTimeUnit(product.getExpiryTimeUnit());
            productDto.setProductCategory(product.getProductCategory());
            productDto.setAvailableInPorts(product.getAvailableInPorts());
            productDto.setAvailableOutPorts(product.getAvailableOutPorts());
            productDto.setTotalInPorts(product.getTotalInPorts());
            productDto.setTotalOutPorts(product.getTotalOutPorts());
            productDto.setProductId(product.getProductId());
            productDto.setNavLedgerId(product.getNavLedgerId());
            productDto.setRefurburshiedProductRefAmountInWarranty(product.getRefurburshiedProductRefAmountInWarranty());
            productDto.setRefurburshiedProductRefAmountPostWarranty(product.getRefurburshiedProductRefAmountPostWarranty());
            productDto.setNewProductRefAmountInWarranty(product.getNewProductRefAmountInWarranty());
            productDto.setNewProductRefAmountPostWarranty(product.getNewProductRefAmountPostWarranty());
            productDto.setNewProductAmount(product.getNewProductAmount());
            productDto.setRefurburshiedProductAmount(product.getRefurburshiedProductAmount());
            productDto.setCaseId(product.getCaseId());
            productDto.setHasAssetConsider(product.getHasAssetConsider());
//            productDto.setThresholdQty(product.getThresholdQty());
            if (product.getVendor().getId() != null) {
                productDto.setVendorId(product.getVendor().getId());
                Vendor vendor = vendorRepo.findById(product.getVendor().getId()).orElse(null);
                productDto.setVendorName(vendor.getName());
            }
            productDto.setNewPrice(product.getNewPrice());
            productDto.setRefurburshiedPrice(product.getRefurburshiedPrice());
            if (product.getNewProductTax()!=null){
                Tax newtax= taxRepository.findById(product.getNewProductTax().intValue()).orElse(null);
                if ( newtax != null) {
                    productDto.setNewProductTax(newtax.getId().longValue());
                    productDto.setNewProductTaxName(newtax.getName());
                }
            }

            if (product.getRefurburshiedProductTax() != null) {
                Tax oldtax = taxRepository.findById(product.getRefurburshiedProductTax().intValue()).orElse(null);
                if (oldtax != null) {
                    productDto.setRefurburshiedProductTax(oldtax.getId().longValue());
                    productDto.setRefurburshiedProductTaxName(oldtax.getName());
                }
            }

            productDto.setActualpricenewProduct(product.getActualpricenewProduct());
            productDto.setActualpricerefurbishedProduct(product.getActualpricerefurbishedProduct());

            List<SpecificationParameters> specificationParameters =specificatioParametersRepo.findAllByProductCategory_Id(product.getProductCategory().getId());
            List<SpecificationParametersDTO> specificationParametersDTOS=specificationParameters.stream().map(data ->specificationParametersMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());

            specificationParametersDTOS.forEach(data->{
                String defaultValue=productParameterMappingRepo.getByProductIdAndParamId(product.getId(),data.getId());
                data.setDefaultValue(defaultValue);
                if(data.getIsMultiValueParam()!=null && data.getIsMultiValueParam().equals(true))
                    data.setIsMultiValueParam(true);
                else
                    data.setIsMultiValueParam(false);
                if(data.getIsMultiValueParam().equals(true) && data.getParamValues()!=null)
                    data.setParamMultiValues(Arrays.asList(data.getParamValues().split(",",-1)));
            });
            productDto.setSpecificationParametersDTOList(specificationParametersDTOS);
            productDto.setFilename(product.getFilename());
            productDto.setUniquename(product.getUniquename());
            productDto.setLicenseDate(product.getLicenseDate());

            return productDto;
        } catch (CustomValidationException e) {
            e.printStackTrace();
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }


    public ProductDto updateParamDefaultValue(ProductDto productDto,ProductDto entity)
    {
        try {
            if(entity.getSpecificationParametersDTOList()!=null && !entity.getSpecificationParametersDTOList().isEmpty())
            {
                entity.getSpecificationParametersDTOList().forEach(x->{
                    if(x.getDefaultValue()!=null)
                    {
                        List<ProductParameterDefaultValueMapping> mappings=productParameterMappingRepo.getProductMappingByProductIdAndParamId(productDto.getId(),x.getId());
                        if(mappings!=null && !mappings.isEmpty())
                        {
                            mappings.get(mappings.size()-1).setDefaultValue(x.getDefaultValue());
                            productParameterMappingRepo.save(mappings.get(mappings.size()-1));
                        }
                        else
                        {
                            ProductParameterDefaultValueMapping mapping=new ProductParameterDefaultValueMapping();
                            mapping.setProductId(productDto.getId());
                            mapping.setParameterId(x.getId());
                            mapping.setDefaultValue(x.getDefaultValue());
                            productParameterMappingRepo.save(mapping);
                        }
                    }
                });
            }
            productDto.setSpecificationParametersDTOList(entity.getSpecificationParametersDTOList());
            return productDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public Product getProductByID(long id){
        return  productRepository.findById(id).get();
    }

    public Resource getProductDoc(Product product) {
        Resource resource = null;
        String PATH;
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.PRODUCT_DOC_PATH).get(0).getValue();
        try {
           if(product != null && product.getUniquename() != null){
               String subFolderName = File.separator + product.getName().trim() + File.separator;
               Path path = Paths.get(PATH + subFolderName);
               Path filePath = path.resolve(product.getUniquename()).normalize();
               resource = new UrlResource(filePath.toUri());
               if (resource.exists()){
                   return resource;
               }
           }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            resource = null;
        }
        return resource;
    }
}
