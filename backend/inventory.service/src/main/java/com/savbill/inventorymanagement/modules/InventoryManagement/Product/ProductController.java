package com.savbill.inventorymanagement.modules.InventoryManagement.Product;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.*;
import com.savbill.inventorymanagement.core.constants.*;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.kafka.KafkaMessageData;
import com.savbill.inventorymanagement.kafka.KafkaMessageSender;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
import com.savbill.inventorymanagement.rabbitmq.MessageSender;
import com.savbill.inventorymanagement.rabbitmq.ProductMessage;
import com.savbill.inventorymanagement.utils.APIConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(value = "ProductController", description = "REST APIs related to product Entity!!!!", tags = "product-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.PRODUCT_MANAGEMENT)
public class ProductController extends ExBaseAbstractController<ProductDto> {


    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    Tracer tracer;

    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    private static final Logger LOGGER = Logger.getLogger(ProductController.class);

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    public ProductController(ProductServiceImpl productService) {
        super(productService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ProductController]";
    }


    @Override
    public GenericDataDTO getAllWithoutPagination() {
        return super.getAllWithoutPagination();
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Product.PRODUCT + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO){
        return super.getAll(requestDTO);
    }
    /**
     Save Product API
     * @Author Darshan
     * @RequestParam productDetailList
     * @RequestParam file
     * @param req
     * @return
     * @throws Exception
     */
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Product.PRODUCT_CREATE + "\")")
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO save(@RequestParam String productDetailList
            , @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest req) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [save()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", productService.getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            if (null != productDetailList) {
                ProductDto entityDTO = new ObjectMapper().registerModule(new JavaTimeModule())
                        .readValue(productDetailList, new TypeReference<ProductDto>() {
                        });
                if (getMvnoIdFromCurrentStaff() != null) {
                    entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                }
                if (entityDTO.getName().length() > 250 || entityDTO.getDescription().length() > 500) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(MessageConstants.INPUT_SIZE_ERROR);
                } else {
                    productService.validateProduct(entityDTO);
                    boolean flag = productService.duplicateVarification(entityDTO, CommonConstants.OPERATION_ADD);
                    boolean productIdFlag = productService.duplicateProductIdVarification(entityDTO, CommonConstants.OPERATION_ADD);
                    if (flag && productIdFlag) {
                        if (getMvnoIdFromCurrentStaff() != null) {
                            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                        }
                        ProductDto productDto = productService.saveEntity(entityDTO,file);
                        //        Shared Product Data to Revenue Microservice
                        if (productDto != null) {
                            Product product = productRepository.findById(productDto.getId()).orElse(null);
                            ProductMessage productMessage = new ProductMessage(product);
//                        this.messageSender.send(productMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_PRODUCT_TO_REVENUE);
                            kafkaMessageSender.send(new KafkaMessageData(productMessage, ProductMessage.class.getSimpleName()));

                        }
                        genericDataDTO.setData(productDto);
                        genericDataDTO.setResponseCode(HttpStatus.OK.value());
                        genericDataDTO.setResponseMessage(MessageConstants.CREATE_SUCCESSFUL);
                        LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Product" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

                    } else if (!productIdFlag) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Product Id is already existing", null);
                    } else if (!flag) {
                        genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                        genericDataDTO.setResponseMessage(MessageConstants.PRODUCT_NAME_EXITS);
                        LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Product" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_DUPLICATE_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);
                    }
                }
            }
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            //LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Product"+LogConstant.LOG_BY_NAME+entityDTO.getName() + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     Search Product API
     * @Author Darshan
     * @param page
     * @param pageSize
     * @param sortOrder
     * @param sortBy
     * @param filter
     * @return
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @Override
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Product.PRODUCT + "\")")
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", productService.getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            genericDataDTO =  super.search(page, pageSize, sortOrder, sortBy, filter , req);
            if(genericDataDTO.getDataList().isEmpty()) {

                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Product By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED+ LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR +"Search Product By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Search Product By Keyword : "+ filter.getFilter().get(0).getFilterValue() +LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +APIConstants.EXPECTATION_FAILED+ APIConstants.ERROR_MESSAGE +ex.getMessage()+ LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    /**
     Get All Active Product
     * @Author Darshan
     * @return
     */
    @GetMapping("/getAllActiveProduct")
    public GenericDataDTO getAllActiveProduct() {
        return productService.getAllActiveProduct();
    }

    /**
     Get All Products BY Service Id
     * @Author Darshan
     * @param serviceId
     * @return
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllProductByServiceId")
    public GenericDataDTO getAllProductByServiceId(@RequestParam(name="serviceId")Long serviceId) {
        return productService.getAllProductByServiceId(serviceId);
    }

    /**
     Get All Network and NA Binded Product
     * @Author Darshan
     * @return
     */
    @GetMapping("/getAllNetworkandNaBindProduct")
    public GenericDataDTO getAllNetworkandNaBindProduct() {
        return productService.getAllNetworkandNaBindProduct();
    }


    @GetMapping("/getAllNetworkandNaBindProductBasedOnDeviceId/{deviceId}/{productId}")
    public GenericDataDTO getAllNetworkandNaBindProductBasedOnDeviceId(@PathVariable(name = "deviceId") Long deviceId,@PathVariable(name = "productId") Long productId) {
        return productService.getAllNetworkandNaBindProduct(deviceId,productId);
    }

    /**
     Get All Charge Type
     * @Author Darshan
     * @param chargeType
     * @return
     */
    @GetMapping("/getAllChargeType/{chargeType}")
    public GenericDataDTO getAllChargeType(@PathVariable(name = "chargeType") String chargeType) {
        return productService.getAllChargeByType(chargeType);
    }

    /**
     Delete Product API
     * @Author Darshan
     * @param id
     * @param request
     * @return
     * @throws Exception
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Product.PRODUCT_DELETE+"\")")
    @DeleteMapping("/delete/{id}")
    public GenericDataDTO delete(@PathVariable("id") Long id, HttpServletRequest request) throws Exception {
        String SUBMODULE = getModuleNameForLog() + "[delete()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", productService.getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, request.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        ProductDto productDto1 = new ProductDto();
        try {
            productService.getEntityForUpdateAndDelete(id);
            boolean flag = productService.deleteVerification(Math.toIntExact(id));
            if (flag) {
                Product product = productService.deleteEntity(id);
                //        Shared Product Data to Revenue Microservice
                if (product != null) {
                    ProductMessage productMessage = new ProductMessage(product);
//                    this.messageSender.send(productMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_PRODUCT_TO_REVENUE);
                    kafkaMessageSender.send(new KafkaMessageData(productMessage, ProductMessage.class.getSimpleName()));
                }
                ProductDto productDto = productMapper.domainToDTO(product, new CycleAvoidingMappingContext());
                genericDataDTO.setData(productDto);
                genericDataDTO.setResponseMessage(MessageConstants.DELETE_SUCCESSFUL);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                LOGGER.info(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleted Product"+LogConstant.LOG_BY_NAME+productDto.getName()+ LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(DeleteContant.VENDOR_EXITS);
                LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleted Product"+LogConstant.LOG_BY_NAME+productDto1.getName()+ LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR +  LogConstant.LOG_STATUS_CODE+APIConstants.FAIL);

            }
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleted Product"+LogConstant.LOG_BY_NAME+productDto1.getName() + LogConstant.REQUEST_BY +productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  +APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     Update Product API
     * @Author Darshan
     * @param productDetailList
     * @param file
     * @param req
     * @return
     * @throws Exception
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Product.PRODUCT_EDIT+"\")")
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO update(@RequestParam String productDetailList
            , @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest req) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [update()] ";
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", productService.getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            if (null != productDetailList) {
                ProductDto entityDTO = new ObjectMapper().registerModule(new JavaTimeModule())
                        .readValue(productDetailList, new TypeReference<ProductDto>() {
                        });
                productService.getEntityForUpdateAndDelete(entityDTO.getId());
                productService.validateProduct(entityDTO);
                Product old = productService.getProductByID(entityDTO.getId());
                Product oldClone = new Product(old);
                boolean flag = productService.duplicateVarification(entityDTO, CommonConstants.OPERATION_UPDATE);
                boolean productIdFlag = productService.duplicateProductIdVarification(entityDTO, CommonConstants.OPERATION_UPDATE);
                if (flag && productIdFlag) {
                    if (getMvnoIdFromCurrentStaff() != null) {
                        entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
                    }
                    ProductDto productDto = productService.updateEntity(entityDTO,file);
                    //        Shared Product Data to Revenue Microservice
                    if (productDto != null) {
                        Product product = productRepository.findById(productDto.getId()).orElse(null);
                        ProductMessage productMessage = new ProductMessage(product);
//                    this.messageSender.send(productMessage, RabbitMqConstants.QUEUE_INVENTORY_SEND_PRODUCT_TO_REVENUE);
                        kafkaMessageSender.send(new KafkaMessageData(productMessage, ProductMessage.class.getSimpleName()));
                    }
                    dataDTO.setData(productDto);
                    dataDTO.setResponseCode(HttpStatus.OK.value());
                    dataDTO.setResponseMessage(MessageConstants.UPDATE_SUCCESSFUL);
                    Product product = productMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
                    LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Product" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + " , Updated Product Details " + UpdateDiffFinder.getUpdatedDiff(oldClone, product) + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

                } else if (!productIdFlag) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Product Id is already existing", null);
                } else if (!flag) {
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    dataDTO.setResponseMessage(MessageConstants.PRODUCT_NAME_EXITS);
                    LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Product" + LogConstant.LOG_BY_NAME + entityDTO.getName() + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
                }
            }
        } catch (CustomValidationException e) {
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            //LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Update Product " + LogConstant.LOG_BY_NAME+entityDTO.getName()+ LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    /**
     Get All Products By MacMapping Id
     * @Author Darshan
     * @param macMappingId
     * @return
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllProductsByMacSerial")
    public GenericDataDTO getAllProductsByMacSerial(@RequestParam(value = "macMappingId") Long macMappingId) {
        return productService.getAllProductsByMacSerial(macMappingId);
    }

    /**
     Get All Product By Product Category Id
     * @Author Darshan
     * @param id
     * @return
     */
    @GetMapping("/getAllProductsByProductCategoryId")
    public GenericDataDTO getAllProductsByProductCategoryId(@RequestParam("pc_id") Long id) {
        return productService.getAllProductsByProductCategoryId(id);
    }

    /**
     Get All Product Plan Mapping By Plan Id for Individual
     * @Author Darshan
     * @param planId
     * @return
     */
    @GetMapping("/getAllPlanIvnetoryIdOnPlanId/planId")
    public GenericDataDTO getAllPlanIvnetoryIdOnPlanId(@RequestParam("planId") Long planId){
        return productService.getAllPlanInventorysIdOnPlanId(planId);
    }

    /**
     Get All Product Plan Group Mapping By Plan Id and Plan Group Id for Plangroup
     * @Author Darshan
     * @param planId
     * @param planGroupId
     * @return
     */
    @GetMapping("/getAllInventoryIdOnPlanIdAndPlanGroupId")
    public GenericDataDTO getAllInventoryIdOnPlanIdAndPlanGroupId(@RequestParam(name = "planId") Long planId, @RequestParam(name = "planGroupId") Long planGroupId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",productService.getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try{
            genericDataDTO.setDataList(productService.getAllInventoryIdOnPlanIdAndPlanGroupId(planId, planGroupId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all InventoryId , PlanId , GroupId" + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        }
        catch (Exception exception) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(exception.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all InventoryId , PlanId , GroupId" + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + exception.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /**
     Get Product Category By Product Plan Group Mapping Id
     * @Author Darshan
     * @param mappingId
     * @return
     */
    @GetMapping("/getProductCategoryByProductPlanGroupMappingId")
    public GenericDataDTO getProductCategoryByProductPlanGroupMappingId(@RequestParam("mappingId") Long mappingId) {
        return productService.getProductCategoryByProductPlanGroupMappingId(mappingId);
    }

    /**
     Get Product By Product Plan Group Mapping Id
     * @Author Darshan
     * @param mappingId
     * @return
     */
    @GetMapping("/getProductByProductPlanGroupMappingId")
    public GenericDataDTO getProductByProductPlanGroupMappingId(@RequestParam("mappingId") Long mappingId) {
        return productService.getProductByProductPlanGroupMappingId(mappingId);
    }

    /**
     Get All Item Based On Product Id
     * @Author Darshan
     * @param productId
     * @return
     */
    @PostMapping("/getAllItemBasedOnProduct")
    public GenericDataDTO getAllItemBasedOnProduct(@RequestBody List<Long> productId){
        return productService.getAllItemBasedOnProduct(productId);
    }

    /**
     Get All Product For Non Trackable Product Category
     * @Author Darshan
     * @return
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllProductForNonTrackableProductCategory")
    public GenericDataDTO getAllProductForNonTrackableProductCategory() {
        return productService.getAllProductForNonTrackableProductCategory();
    }

    /**
     Get ALl Customer Binded Products
     * @Author Darshan
     * @return
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllCBProducts")
    public GenericDataDTO getAllCBProducts() {
        return productService.getAllCBProducts();
    }

    /**
     Get All Product Based on Item Type
     * @Author Darshan
     * @param itemType
     * @return
     */
    @GetMapping("/getAllProductbasedOnItemType")
    public GenericDataDTO getAllProductbasedOnItemType(@RequestParam("itemtype")String itemType) {
        return productService.getAllProductbasedOnItemType(itemType);
    }

    /**
     Get All Serialized Item Based On Product
     * @Author Darshan
     * @param productId
     * @param ownerId
     * @param ownerType
     * @return
     */
    @GetMapping("/getAllSerializedItemBaseOnProduct")
    public GenericDataDTO getAllSerializedItemBaseOnProduct(@RequestParam("productId")Long productId, @RequestParam("ownerId") Long ownerId, @RequestParam("ownerType") String ownerType) {
        String itemType = CommonConstants.PRODUCT.SERIALIZED_ITEM;
        return productService.getAllSerializedItemBaseOnProduct(productId,itemType, ownerId, ownerType);
    }

    /**
     Get All Products By Customer Owned
     * @Author Darshan
     * @param custId
     * @return
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllProductsByCustomerOwned")
    public GenericDataDTO getAllProductsByCustomerOwned(@RequestParam("custId") Long custId) {
        return productService.getAllProductsByCustomerOwned(custId);
    }

    /**
     Get All Network and NA Bind Non Serialized Product
     * @Author Darshan
     * @return
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Pop.POP_INVENTORY_LIST_ASSIGN_INVENTORY +"\")")
    @GetMapping("/getAllNetworkAndNABindNonSerializedProduct")
    public GenericDataDTO getAllNetworkAndNABindNonSerializedProduct() {
        return productService.getAllNetworkAndNABindNonSerializedProduct();
    }


    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Pop.POP_INVENTORY_LIST_ASSIGN_INVENTORY +"\")")
    @GetMapping("/getAllNetworkAndNABindNonSerializedProductBasedOnDeviceId/{deviceId}")
    public GenericDataDTO getAllNetworkAndNABindNonSerializedProductBasedOnDeviceId(@PathVariable(name = "deviceId") Long deviceId) {
        return productService.getAllNetworkAndNABindNonSerializedProduct(deviceId);
    }

    /**
     Get Mapping Detail By Plan group Id, Plan Id, Product category Id, and Product Id
     * @Author Darshan
     * @param planGroupId
     * @param planId
     * @param productCategoryId
     * @param productId
     * @return
     */
    @GetMapping("/getMappingDetails")
    public GenericDataDTO getMappingDetails(@RequestParam(name = "planGroupId", required = false) Long planGroupId,HttpServletRequest req , @RequestParam(name = "planId") Long planId, @RequestParam(name = "productCategoryId") Long productCategoryId, @RequestParam(name = "productId") Long productId) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",productService.getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            if (planGroupId != null) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Mapping Details" + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
                return productService.getProductPlanGroupMappingDetails(planGroupId, planId, productCategoryId, productId);
            }
        }catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch all Mapping Details" + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED +APIConstants.ERROR_MESSAGE+ex.getMessage() + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return productService.getProductPlanMappingDetails(planId, productCategoryId, productId);

    }

    /**
     Get All Active Products By Product Category Id
     * @Author Darshan
     * @param id
     * @return
     * @throws Exception
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllActiveProductsByProductCategoryId")
    public GenericDataDTO getAllActiveProductsByProductCategoryId(@RequestParam("pc_id") Long id) throws Exception {
        return productService.getAllActiveProductsByProductCategoryId(id);
    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_PRODUCT_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllNetworkDeviceProduct")
    public GenericDataDTO getAllNetworkDeviceProduct() {
//        return productService.search(pageDto.getFilters(),pageDto.getPage(),pageDto.getPageSize(),0,"id");
        return productService.getAllNetworkDeviceProduct();
    }

    @GetMapping("/filterProductsByDeviceType")
    public GenericDataDTO filterProductsByDeviceType(@RequestParam String deviceType) {
        return productService.filterProductsByDeviceType(deviceType);
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Product.PRODUCT +  "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",productService.getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = super.getEntityById(id,req);
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Inventory Product" + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
        }catch (Exception ex ){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Inventory Product" + LogConstant.REQUEST_BY + productService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED +APIConstants.ERROR_MESSAGE+ex.getMessage() + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getProductVarifiedWithCDATAManufacturer")
    public boolean getProductVerifiedWithCDATAManufacturer(@RequestParam("customerId") Integer customerId,@RequestParam("connectionNumber")String connectionNumber,@RequestParam("manufacturerName") String manufacturerName) {
        CustomerInventoryMapping  customerInventoryMapping = customerInventoryMappingRepo.findByCustomerIdAndConnectionNoAndIsDeletedFalse(customerId,connectionNumber);
        if(customerInventoryMapping!=null){
            Product product = productRepository.findById(customerInventoryMapping.getProduct().getId()).orElse(null);
            if (product!=null){
                if(manufacturerName.equalsIgnoreCase(product.getVendor().getName())){
                    return  true;
                }else{
                    return  false;
                }
            }
        }
        return false;
    }


    @GetMapping("/getMenufacturerName")
    public String getProductVerifiedWithCDATAManufacturer(@RequestParam("customerId") Integer customerId,@RequestParam("connectionNumber")String connectionNumber) {
        CustomerInventoryMapping  customerInventoryMapping = customerInventoryMappingRepo.findByCustomerIdAndConnectionNoAndIsDeletedFalse(customerId,connectionNumber);
        if(customerInventoryMapping!=null){
            Product product = productRepository.findById(customerInventoryMapping.getProduct().getId()).orElse(null);
            if (product!=null){
                return product.getVendor().getName();
            }
        }
        return "";
    }

    @RequestMapping(value = "/document/download/{productId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Integer productId) {
        org.slf4j.MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [downloadFile()] ";
        Resource resource = null;
        try {
            Product product = productRepository.findById(productId.longValue()).orElse(null);
            if (null == product) {
                return ResponseEntity.notFound().build();
            }
            resource =  productService.getProductDoc(product);
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                LOGGER.info("Downloading document with  " + productId + " downloaded Successfully  :  request: { From : {} }; Response : {{}}");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                LOGGER.error("Unable to downloadDocument " + productId + " :  request: { From : {}}; Response : {{}};Error :{} ;");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to downloadDocument " + productId + "   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}");
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        org.slf4j.MDC.remove("type");
        return null;
    }

}
