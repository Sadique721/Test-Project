package com.savbill.inventorymanagement.modules.InventoryManagement.Inward;

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
import com.savbill.inventorymanagement.core.utillity.fileUtillity.FileUtility;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.Customers.CustomerService;
import com.savbill.inventorymanagement.modules.Customers.CustomersPojo;
import com.savbill.inventorymanagement.modules.Customers.CustomersRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.ApproveReplaceAllInventoryDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.OutWordUploadService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMappingDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventoryMapping.InventoryMappingService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InventorySpecification.CustInvParamsDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.SearchInventoryDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.NonSerializedItemServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryService;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerService;
import com.savbill.inventorymanagement.modules.WorkflowManagement.Hierarchy.HierarchyService;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
import com.savbill.inventorymanagement.rabbitmq.CustInvParamsMessage;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@Api(value = "InwardController", description = "REST APIs related to inward Entity!!!!", tags = "inwards-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.INWARDS)
public class InwardController extends ExBaseAbstractController<InwardDto> {

    @Autowired
    InwardServiceImpl inwardService;
    @Autowired
    InwardMapper inwardMapper;
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;
    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    InventoryMappingService inventoryMappingService;

    @Autowired
    private InOutWardMACService inOutWardMACService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private InwardRepository inwardRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private NonSerializedItemServiceImpl nonSerializedItemService;

    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private InOutWardMacRepo inOutWardMacRepo;
    @Autowired
    private ProductOwnerService productOwnerService;
    @Autowired
    Tracer tracer;
    @Autowired
    private HierarchyService hierarchyService;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private CustomerService customersService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private OutWordUploadService outWordUploadService;

    public InwardController(InwardServiceImpl inwardService) {
        super(inwardService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[InwardController]";
    }

    private static final Logger LOGGER = Logger.getLogger(InwardController.class);

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inward.INWARD_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody InwardDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            inwardService.validateInward(entityDTO);
            if (entityDTO.getType() == null)
                entityDTO.setType(CommonConstants.NEW);
            String defaultTimezone = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
            Integer second = tz.getOffset(new Date().getTime()) / 1000;
            LocalDateTime localDateTime = entityDTO.getInwardDateTime().plusSeconds(second);
            entityDTO.setInwardDateTime(localDateTime);
            InwardDto inwardDto = inwardService.saveEntity(entityDTO, false, false);
            genericDataDTO.setData(inwardDto);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Inward" + LogConstant.LOG_BY_NAME + entityDTO.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Inward" + LogConstant.LOG_BY_NAME + entityDTO.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Inward" + LogConstant.LOG_BY_NAME + entityDTO.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

  
    @GetMapping("/findByGroupId/{id}")
    @ApiOperation(
            value = "Get inward group (parent + children)",
            notes = "Fetch parent inward and all its child records using any inward ID (parent or child)"
    )
    public GenericDataDTO getInwardGroup(@PathVariable Long id, HttpServletRequest req) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();

        MDC.put("type", "Fetch-Inward-Group");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {

            // CALL SERVICE
            List<InwardDto> list = inwardService.getInwardGroup(id);

            genericDataDTO.setData(list);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Inward group fetched successfully");

            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Fetch Inward Group"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS
                    + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS
                    + " | GroupId: " + id
                    + " | Total Records: " + list.size());

        } catch (RuntimeException ex) {

            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(ex.getMessage());

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Fetch Inward Group"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED
                    + APIConstants.ERROR_MESSAGE + ex.getMessage()
                    + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_FOUND.value());

        } catch (Exception ex) {

            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Error while fetching inward group");

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Fetch Inward Group"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED
                    + APIConstants.ERROR_MESSAGE + ex.getMessage()
                    + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value(), ex);

        } finally {

            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove(LogConstant.TRACE_ID);
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ADD + "\")")
//@PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inward.INWARD_CREATE +"\")")
//public GenericDataDTO save(@Valid @RequestBody List<InwardDto> entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Create");
//        try {
//            //InwardDto inwardDto = inwardService.saveEntity(entityDTO, false, false);
//            List<InwardDto> dtoList = new ArrayList<>();
//            InwardDto inwardDto = new InwardDto();
//            for(int i=0;i<entityDTO.size();i++) {
//                if(entityDTO.get(i).getType() == null)
//                    entityDTO.get(i).setType(CommonConstants.NEW);
//                inwardDto = inwardService.saveEntity((InwardDto) entityDTO, false, false);
//                dtoList.add(inwardDto);
//                genericDataDTO.setData(dtoList);

    /// /                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, dtoList.get(i).getId(), dtoList.get(i).getInwardNumber().toString());
//            }
//            logger.info("InWard controller successfully created  :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(), APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to search :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),HttpStatus.NOT_ACCEPTABLE, APIConstants.FAIL,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getInwardDetailsByProductAndDestination")
    public GenericDataDTO getInwardDetailsByProductAndDestId(@RequestParam(name = "productId") Long productId, @RequestParam(name = "destinationId") Long destinationId, @RequestParam(name = "destinationType") String destinationType, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inwardService.getInwardDetailsByProductAndDestination(productId, destinationId, destinationType));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Inward" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Inward" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inward.INWARD_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody InwardDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            Inward old = inwardService.getInWardById(entityDTO.getId());
            Inward oldClone = new Inward(old);
            inwardService.validateInward(entityDTO);
            inwardService.getEntityForUpdateAndDelete(entityDTO.id);
            InwardDto existingInward = inwardService.getEntityById(entityDTO.id);
            String defaultTimezone = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
            Integer second = tz.getOffset(new Date().getTime()) / 1000;
            LocalDateTime localDateTime = entityDTO.getInwardDateTime().plusSeconds(second);
            entityDTO.setInwardDateTime(localDateTime);
            InwardDto inwardDto = inwardService.updateEntity(entityDTO, false, false);
            genericDataDTO.setData(inwardDto);
            Inward inward = inwardMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());

//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, inwardDto.getId(), inwardDto.getInwardNumber().toString());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Inward" + LogConstant.LOG_BY_NAME + entityDTO.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + " Updated Inward " + UpdateDiffFinder.getUpdatedDiff(oldClone, inward) + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Inward" + LogConstant.LOG_BY_NAME + entityDTO.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Inward" + LogConstant.LOG_BY_NAME + entityDTO.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inward.INWARD + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req) {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter, req);
            if (genericDataDTO.getDataList().isEmpty()) {

                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Inward By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            } else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Inward By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Inward By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_NO_RECORD_FOUND + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }


    @PostMapping("/searchByCustomerAndPopAndServiceAreaName")
    public GenericDataDTO searchByCustomerAndPopAndServiceAreaName(@RequestBody PaginationRequestDTO requestDTO, @RequestParam("staffId") Long staffId, @RequestParam("filtername") String filterName, @RequestParam("isSerelized") boolean isSerelized, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = inwardService.searchByCustomerAndPopAndServiceAreaName(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder(), staffId, filterName, isSerelized);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "search Customer, Pop And Service" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "search Customer, Pop And Service" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            throw ex;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllInwardByProductAndStaff")
    public GenericDataDTO getAllInwardByProductAndStaff(@RequestParam(name = "productId") Long productId, @RequestParam(name = "staffId") Long staffId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inwardService.getAllInwardByProductAndStaff(productId, staffId));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, inwardDto.getId(), inwardDto.getInwardNumber());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fatch All Inward By ProductId : " + productId + " And StaffId : " + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fatch all Inward By ProductId : " + productId + " And StaffId : " + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllInwardByProductAndStaffforpopandserivearea")
    public GenericDataDTO getAllInwardByProductAndStaffforPopandServiceArea(@RequestParam(name = "productId") Long productId, @RequestParam(name = "staffId") Long staffId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inwardService.getAllNetworkBindInwards(productId, staffId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "get All Inward By ProductId : " + productId + " And StaffId : " + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "get All Inward By ProductId : " + productId + " And StaffId : " + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllInwardByProductAndStaffforPopandSeriveareaandCustomer")
    public GenericDataDTO getAllInwardByProductAndStaffforPopandServiceAreaandCustomer(@RequestParam(name = "productId") Long productId, @RequestParam(name = "staffId") Long staffId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inwardService.getAllNetworkBindandCustomerandPopInwards(productId, staffId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Inward Product Id : " + productId + " And Staff Id : " + staffId + " for Pop And Service Areas" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Inward Product Id : " + productId + " And Staff Id : " + staffId + " for Pop And Service Areas" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping("/getByCustomerId")
    public GenericDataDTO getByCustomerId(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Get By Customer Id" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            return customerInventoryMappingService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getInwardNumber());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Unable to get By Customer Id" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.INVENTORY_PRE_CUSTOMER_ASSIGN.PRE_CUST_INVENTORY + "\",\"" + ACLMenuConstants.INVENTORY_POST_CUSTOMER_ASSIGN.POST_CUST_INVENTORY + "\",\"" + ACLMenuConstants.INVENTORY_PRE_CUSTOMER_ASSIGN.PRE_CUST_INVENTORY_PLAN + "\"," +
            "\"" + ACLMenuConstants.INVENTORY_POST_CUSTOMER_ASSIGN.POST_CUST_INVENTORY_PLAN + "\",\"" + ACLMenuConstants.INVENTORY_PRE_CUSTOMER_ASSIGN.PRE_CUST_INVENTORY_OTHER + "\",\"" + ACLMenuConstants.INVENTORY_POST_CUSTOMER_ASSIGN.POST_CUST_INVENTORY_OTHER + "\",\"" +
            ACLMenuConstants.INVENTORY_PRE_CUSTOMER_ASSIGN.PRE_CUST_INVENTORY_EXTERNAL + "\",\"" + ACLMenuConstants.INVENTORY_POST_CUSTOMER_ASSIGN.POST_CUST_INVENTORY_EXTERNAL + "\")")
    @GetMapping("/getAllCustomerInventoryList")
    public GenericDataDTO getAllCustomerInventoryList(@RequestParam("custId") Integer custId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setDataList(customerInventoryMappingService.getAllCustomerInventoryList(custId, false));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Customer Inventory List By Customer Id : " + custId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Customer Inventory List By Customer Id : " + custId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
//@PreAuthorize("validatePermission(\"" + ACLMenuConstants.Pop.POP_INVENTORY_LIST +"\")")
    @PostMapping("/getByOwnerIdAndType")
    public GenericDataDTO getByOwnerIdAndType(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Owner by Id , Staff" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            return inventoryMappingService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getInwardNumber());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ADD + "\")")
    @PostMapping("/assignToCustomer")
    public GenericDataDTO assignToCustomer(@RequestBody CustomerInventoryMappingDto inventoryMappingDto, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            inventoryMappingDto.setDiscount(0d);
            if (inventoryMappingDto.getBillTo().equalsIgnoreCase(CommonConstants.CUSTOMER_INVENTORY_BILL_TO.ORGANIZATION) && inventoryMappingDto.getIsInvoiceToOrg()) {
                inventoryMappingDto.setIsRequiredApproval(true);
            } else {
                inventoryMappingDto.setIsRequiredApproval(false);
            }
            customerInventoryMappingService.validate(inventoryMappingDto);
            String defaultTimezone = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
            Integer second = tz.getOffset(new Date().getTime()) / 1000;
            LocalDateTime localDateTime = inventoryMappingDto.getAssignedDateTime().plusSeconds(second);
            inventoryMappingDto.setAssignedDateTime(localDateTime);
            /** Save Customer Inventory List */
            List<CustomerInventoryMappingDto> customerInventoryMappingDtoList = customerInventoryMappingService.saveEntityList(inventoryMappingDto);
            List<CustInvParamsDto> custInvParamsDtos = new ArrayList<>();
            if (!CollectionUtils.isEmpty(inventoryMappingDto.getCustInvParams()) && !CollectionUtils.isEmpty(customerInventoryMappingDtoList)) {
                custInvParamsDtos = customerInventoryMappingService.saveCustInvParams(inventoryMappingDto.getCustInvParams(), Long.valueOf(inventoryMappingDto.getCustomerId()), inventoryMappingDto.getCustServiceMapId(), customerInventoryMappingDtoList.get(0).getId());
            }
            customerInventoryMappingService.sendAssignInventoryToCMS(customerInventoryMappingDtoList, custInvParamsDtos, inventoryMappingDto.isHasMac(), inventoryMappingDto.isHasSerial());
            //update inoutward history
            if (inventoryMappingDto.getExternalItemId() == null && !inventoryMappingDto.isItemAssemblyflag()) {
                List<Long> custInventoryId = customerInventoryMappingDtoList.stream().filter(customerInventoryMappingDto -> customerInventoryMappingDto.getCustomerId().equals(inventoryMappingDto.getCustomerId())).map(CustomerInventoryMappingDto::getId).collect(Collectors.toList());
                inOutWardMACService.updateInoutwardMacMappingforSerialized(custInventoryId.get(0), inventoryMappingDto);
            }
            //update product owner after assign inventory to customer
            if (inventoryMappingDto.getExternalItemId() == null) {
                productOwnerService.updateProductOwnerForSerializedProduct(inventoryMappingDto.getQty(), inventoryMappingDto.getProductId(), Integer.valueOf(inventoryMappingDto.getStaffId()), CommonConstants.STAFF);
            }
            if (inventoryMappingDto.getExternalItemId() == null && inventoryMappingDto.isItemAssemblyflag() && customerInventoryMappingDtoList.size() == 2) {
                List<CustomerInventoryMappingDto> customerInventoryMappingDtos = customerInventoryMappingService.setCustomerInventoryIdToItemHistory(customerInventoryMappingDtoList, inventoryMappingDto);
                genericDataDTO.setDataList(customerInventoryMappingDtos);
            } else {
                genericDataDTO.setDataList(customerInventoryMappingDtoList);
            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "assign to customer" + LogConstant.LOG_BY_NAME + inventoryMappingDto.getInwardNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(ce.getMessage(), ce);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "assign to customer" + LogConstant.LOG_BY_NAME + inventoryMappingDto.getInwardNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "assign to customer" + LogConstant.LOG_BY_NAME + inventoryMappingDto.getInwardNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @Transactional
    @PostMapping("/assignToEndOwner")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Pop.POP_INVENTORY_LIST_ASSIGN_INVENTORY + "\")")
    public GenericDataDTO assignToEndOwner(@RequestBody InventoryMappingDto inventoryMappingDto, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            inventoryMappingDto.setId(null);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            Long pcId = productRepository.findProductCategoryIdByProductId(inventoryMappingDto.getProductId());
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            if (hasMac) {
                inventoryMappingService.validateMac(inventoryMappingDto);
            }
            if (hasSerial) {
                inventoryMappingService.validateSerialNumber(inventoryMappingDto);
            }
            //inventoryMappingService.validateMac(inventoryMappingDto);
            String defaultTimezone = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
            Integer second = tz.getOffset(new Date().getTime()) / 1000;
            LocalDateTime localDateTime = inventoryMappingDto.getAssignedDateTime().plusSeconds(second);
            inventoryMappingDto.setAssignedDateTime(localDateTime);
            genericDataDTO.setData(inventoryMappingService.saveEntity(inventoryMappingDto));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Assign To End User" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Assign to End user" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Unable to Assign to end user : " + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.INVENTORY_PRE_CUSTOMER_ASSIGN.PRE_CUST_INVENTORY_REPLACE + "\",\"" + ACLMenuConstants.INVENTORY_POST_CUSTOMER_ASSIGN.POST_CUST_INVENTORY_REPLACE + "\",\"" + ACLMenuConstants.INVENTORY_PRE_CUSTOMER_ASSIGN.PRE_CUST_INVENTORY_EDIT + "\",\"" + ACLMenuConstants.INVENTORY_POST_CUSTOMER_ASSIGN.POST_CUST_INVENTORY_EDIT + "\")")
    @PostMapping("/replaceInventory")
    public GenericDataDTO replaceInventory(@RequestBody List<ApproveReplaceAllInventoryDTO> approveReplaceAllInventoryDTOS, Long customerId, @RequestParam("inventoryType") String ownerShipType, @RequestParam("replacementReason") String replacementReason, @RequestParam("approvalRemark") String approvalRemark, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {

            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(customerInventoryMappingService.replaceAllInvetories(approveReplaceAllInventoryDTOS, customerId, ownerShipType, replacementReason, approvalRemark));

//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update inventory" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Inventory" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    @PostMapping("/searchByProductAndStatusAndServiceName")
    public GenericDataDTO searchByProductAndStatusAndServiceName(@RequestParam("filterColumn") String filterColumn, @RequestParam("filterValue") String filterValue, @RequestParam("customerId") Long customerId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Inventory from end user By Key : " + filterValue + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            return inwardService.searchByProductAndStatusAndServiceName(filterColumn, filterValue, customerId);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Inventory from end user By Key : " + filterValue + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            throw ex;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
    @GetMapping("/replaceInventoryFromEndOwner")
    public GenericDataDTO replaceInventoryFromEndOwner(@RequestParam(name = "oldMacMappingId") Long oldMacMappingId, @RequestParam(name = "newMacMappingId") Long newMacMappingId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {
            genericDataDTO.setData(inventoryMappingService.replaceInventory(oldMacMappingId, newMacMappingId));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Inventroy  from end user" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Inventory from end user" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.SUCCESS + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inward.INWARD_DELETE + "\")")
    @Override
//    public GenericDataDTO delete(@RequestBody InwardDto entityDTO, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//    TraceContext traceContext =tracer.currentSpan().context();
//    MDC.put("type", "Delete");
//    MDC.put("userName", getLoggedInUser().getUsername());
//    MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
//    MDC.put("spanId", traceContext.spanIdString());
//
//        try{
//            inwardService.getEntityForUpdateAndDelete(entityDTO.id);
//            boolean flag = inwardService.deleteVerification(entityDTO.getId().intValue());
//            if (flag) {
//            inwardService.deleteInward(entityDTO);
//            if (entityDTO.getApprovalStatus().equalsIgnoreCase(CommonConstants.PENDING) && entityDTO.getOutwardId() == null) {
//                entityDTO.setApprovalStatus("Deleted");
//            }
//            genericDataDTO = super.delete(entityDTO, req);
//            InwardDto inwardDto = (InwardDto) genericDataDTO.getData();
//            if(entityDTO != null)
////                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_INWARD_MANAGEMENT,
////                        AclConstants.OPERATION_INWARD_MANAGEMENT_DELETE, req.getRemoteAddr(), null, inwardDto.getId(), inwardDto.getInwardNumber());
//                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Inward"+LogConstant.LOG_BY_NAME+entityDTO.inwardNumber+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
//        } else {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(DeleteContant.INWARD_NUMBER_DELETE_EXIST);
//            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Inward"+LogConstant.LOG_BY_NAME+entityDTO.inwardNumber+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR +   LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
//        }
//        }catch (Exception ex ){
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Inward"+LogConstant.LOG_BY_NAME+entityDTO.inwardNumber+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE+ex.getMessage() +   LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
//        }finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//    return genericDataDTO;
//    }

    public GenericDataDTO delete(@RequestBody InwardDto entityDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        // Extract trace context
        TraceContext traceContext = tracer.currentSpan().context();

        // Set MDC (Mapped Diagnostic Context) for logging
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            // Validate if entity exists before deletion
            inwardService.getEntityForUpdateAndDelete(entityDTO.getId());

            // Verify if deletion is allowed
            if (inwardService.deleteVerification(entityDTO.getId().intValue())) {
                inwardService.deleteInward(entityDTO);

                if (CommonConstants.PENDING.equalsIgnoreCase(entityDTO.getApprovalStatus()) && entityDTO.getOutwardId() == null) {
                    entityDTO.setApprovalStatus("Deleted");
                }

                // Call super method for deletion
                genericDataDTO = super.delete(entityDTO, req);

                // Success log
                LOGGER.info(String.format(
                        "%s%s Delete Inward %s %s %s %s %s %s %s %d",
                        LogConstant.REQUEST_FROM, req.getHeader("requestFrom"),
                        LogConstant.LOG_BY_NAME, entityDTO.getInwardNumber(),
                        LogConstant.REQUEST_BY, getLoggedInUser().getUsername(),
                        LogConstant.LOG_STATUS, LogConstant.LOG_SUCCESS,
                        LogConstant.LOG_STATUS_CODE, APIConstants.SUCCESS
                ));
            } else {
                // If deletion is not allowed
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(DeleteContant.INWARD_NUMBER_DELETE_EXIST);

                LOGGER.error(String.format(
                        "%s%s Delete Inward %s %s %s %s %s %s %s %d",
                        LogConstant.REQUEST_FROM, req.getHeader("requestFrom"),
                        LogConstant.LOG_BY_NAME, entityDTO.getInwardNumber(),
                        LogConstant.REQUEST_BY, getLoggedInUser().getUsername(),
                        LogConstant.LOG_STATUS, LogConstant.LOG_FAILED,
                        LogConstant.LOG_ERROR, HttpStatus.NOT_ACCEPTABLE.value()
                ));
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());

            LOGGER.error(String.format(
                    "%s%s Delete Inward %s %s %s %s %s %s %s %s %d",
                    LogConstant.REQUEST_FROM, req.getHeader("requestFrom"),
                    LogConstant.LOG_BY_NAME, entityDTO.getInwardNumber(),
                    LogConstant.REQUEST_BY, getLoggedInUser().getUsername(),
                    LogConstant.LOG_STATUS, LogConstant.LOG_FAILED,
                    APIConstants.ERROR_MESSAGE, ex.getMessage(),
                    LogConstant.LOG_STATUS_CODE, HttpStatus.EXPECTATION_FAILED.value()
            ));
        } finally {
            // Clean up MDC
            MDC.clear();
        }

        return genericDataDTO;
    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
    @PostMapping("/approveInventory")
    public GenericDataDTO approveInventory(@RequestBody List<Long> customerInventoryMappingId, boolean isApproveRequest, Integer nextstaff, String remark, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +
                    " Approve Inventory " + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber +
                    LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +
                    LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            GenericDataDTO response = customerInventoryMappingService.approveIndividualInventory(
                    customerInventoryMappingId, isApproveRequest, nextstaff, remark
            );
            return response;
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +
                    " Approve inventory " + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber +
                    LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +
                    LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() +
                    LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +
                    " Unable to Approve inventory " + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber +
                    " with " + customerInventoryMappingId + LogConstant.REQUEST_BY +
                    getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED +
                    APIConstants.ERROR_MESSAGE + ex.getMessage() +
                    LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
        } finally {
//            long endTime = System.currentTimeMillis(); // End time tracking
//            LOGGER.info("approveInventory execution time (failure case): " + (endTime - startTime) + " ms");

            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }


    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Pop.POP_INVENTORY_LIST_ASSIGN_INVENTORY + "\")")
    @GetMapping("/approveInventoryFromOwner")
    public GenericDataDTO approveInventoryFromOwner(@RequestParam(name = "inventoryMappingId") Long inventoryMappingId, @RequestParam(name = "isApproveRequest") boolean isApproveRequest, @RequestParam(name = "inventoryApprovalRemark") String inventoryApprovalRemark, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        InwardDto inwardDto = new InwardDto();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Approve inventory from owner : " + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return inventoryMappingService.approveInventory(inventoryMappingId, isApproveRequest, inventoryApprovalRemark);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Approve inventory from owner : " + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + inventoryMappingId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
    @PostMapping("/approveReplaceInventory")
    public GenericDataDTO approveReplaceInventory(@RequestBody List<ApproveReplaceAllInventoryDTO> approveReplaceAllInventoryDTOS, @RequestParam(name = "billAble") String billAble, @RequestParam(name = "isApproveRequest") boolean isApproveRequest, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update ");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Approve replace inventory" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return customerInventoryMappingService.approveAllReplaceInventory(approveReplaceAllInventoryDTOS, Boolean.parseBoolean(billAble), isApproveRequest);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Approve replace inventory" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_EDIT + "\")")
    @GetMapping("/approveReplaceInventoryFromEndOwner")
    public GenericDataDTO approveReplaceInventoryFromEndOwner(@RequestParam(name = "macMappingId") Long macMappingId, @RequestParam(name = "billAble") String billAble, @RequestParam(name = "isApproveRequest") boolean isApproveRequest, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update ");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Approve replace inventory from owner" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return inventoryMappingService.approveReplaceInventory(macMappingId, Boolean.parseBoolean(billAble), isApproveRequest);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Approve replace inventory from owner" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping(value = "/getAllAssignInventories")
    public GenericDataDTO getAllAssignInventories(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "staffId") Long staffId, HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [getAllAssignInventories()] ";

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = inwardService.getAssignInventories(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), staffId);
            if (null != genericDataDTO) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " fetch all Assigned inventory By Staff Id : " + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch all Assigned inventory By Staff Id : " + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);

            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch all assigned inventory By Staff Id : " + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_INVENTORY + "\",\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_TO_CUSTOMER + "\",\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_SERIALIZED + "\",\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_NON_SERIALIZED + "\")")
    @PostMapping("/getCustomerInventoryMappingByStaffId")
    public GenericDataDTO getCustomerInventoryMappingByStaffId(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "staffId") Long staffId, boolean isGetSerializedItem, HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [getCustomerInventoryMappingByStaffId()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = customerInventoryMappingService.getCustomerInventoryMappingByStaffId(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), staffId, isGetSerializedItem);
            if (null != genericDataDTO) {

                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Costumer Inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Costumer Inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + genericDataDTO.getResponseMessage() + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);

            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Costumer Inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_INVENTORY + "\",\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_TO_POP + "\",\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_SERIALIZED + "\",\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_NON_SERIALIZED + "\")")
    @PostMapping("/getPopByInventoryMappingByStaffId")
    public GenericDataDTO getPopByInventoryMappingByStaffId(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "staffId") Long staffId, boolean isGetSerializedItem, HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [getPopByInventoryMappingByStaffId()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = inventoryMappingService.getPopInventoryMappingByStaffId(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), staffId, isGetSerializedItem);
            if (null != genericDataDTO) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Pop inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Pop inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);

            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Pop inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return null;
    }

    @PostMapping("/getServiceAreaByInventoryMappingByStaffId")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_INVENTORY + "\",\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_TO_SERVICE_AREA + "\",\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_SERIALIZED + "\",\"" + ACLMenuConstants.Inventory_Details.INVENTORY_DETAILS_ASSIGNED_NON_SERIALIZED + "\")")
    public GenericDataDTO getServiceAreaByInventoryMappingByStaffId(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "staffId") Long staffId, boolean isGetSerializedItem, HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [getServiceAreaByInventoryMappingByStaffId()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = inventoryMappingService.getServiceAreaInventoryMappingByStaffId(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), staffId, isGetSerializedItem);
            if (null != genericDataDTO) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetching service area by Inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetching service area by Inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetching service area by Inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;
    }


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getInventoryMappingByStaffId")
    public GenericDataDTO getInventoryMappingByStaffId(@RequestParam(name = "staffId") Long staffId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String staffname = customerInventoryMappingService.getStaffDetails(staffId);
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inventoryMappingService.getInventoryMappingByStaffId(staffId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch inventory by staff id" + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch inventory by staff id" + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping(value = "/getAllInventoriesByOwner")
    public GenericDataDTO getAllInventoriesByOwner(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "ownerId") Long ownerId, @RequestParam(name = "ownerType") String ownerType, HttpServletRequest req) {
        String SUBMODULE = getModuleNameForLog() + " [getAllAssignInventories()] ";

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = inwardService.getAllInventoriesByOwner(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), ownerId, ownerType);
            if (null != genericDataDTO) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Inventroy by owner" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);

                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Inventroy by owner" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + genericDataDTO.getResponseMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + " Unable to  to fetch all inventories : " + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    /**
     * Inward Approval API
     * @param inwardDto
     * @param req
     * @return
     * @throws Exception
     */
    @PutMapping("/inwardApproval")
    @Transactional
    public GenericDataDTO saveInwardApproval(@Valid @RequestBody InwardApprovalDTO inwardDto, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Inward inward = inwardRepository.findById(inwardDto.getId()).get();
        InwardDto inwardDto1 = new InwardDto();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            Long pcId = productRepository.findProductCategoryIdByProductId(inwardDto.getProductId());
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            boolean hasTrackable = productCategoryRepository.findHasTrackableById(pcId);
            boolean isoemConsiderByProductId = productRepository.findIsoemConsiderByProductId(inwardDto.getProductId());
            String uom = productCategoryRepository.findUnitById(pcId);
            List<Item> items = new ArrayList<>();
            /**
             * Save Differnt Entity at Inward Approve
             */
            if (inwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.APPROVE)) {
                if (hasMac || hasSerial) {
                    if (!Objects.equals(inward.getInTransitQty(), inward.getTotalMacSerial())) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only " + inward.getTotalMacSerial() + " items out of " + inward.getInTransitQty() + " are present in inward", null);
                    }
                }
                if (inward.getOutwardId() == null) {
                    /**
                     * Save Manual Item
                     */
                    if (hasMac || hasSerial) {
                        long startTime = System.currentTimeMillis();
                        items = inOutWardMACService.saveManualItems(inward, CommonConstants.APPROVE, isoemConsiderByProductId);
                        long endTime = System.currentTimeMillis();
//                        System.out.println("Execution duration for save manual item: " + (endTime - startTime) + " ms");
                    }
                    /**
                     * Save NonSerialized Items
                     */
                    if (!hasSerial && hasTrackable) {
                        inOutWardMACService.saveNonSerializedItemsAfterApprovalInward(inwardDto, uom);
                    }
                }
            }
            /**
             * Save Differnt Entity at Inward Reject
             */
            else if (inwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.REJECTED) && inward.getOutwardId() == null) {
                if (hasMac || hasSerial) {
                    items = inOutWardMACService.saveManualItems(inward, CommonConstants.REJECTED, isoemConsiderByProductId);
                }
            }
            /**
             * Save Inward after Approvel
             */
            long startTime = System.currentTimeMillis();
            InwardDto saveInwardApproval = inwardService.saveInwardApproval(inward, inwardDto.getApprovalStatus(), inwardDto.getApprovalRemark(),
                    inwardDto.getProductId(), items, hasMac, hasSerial, hasTrackable, inward.getOutwardId(), true);
            long endTime = System.currentTimeMillis();
//            System.out.println("Execution duration for save inward approval: " + (endTime - startTime) + " ms");
            if (inward.getOutwardId() != null &&
                    inwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.APPROVE)) {
                productOwnerService.sharedThresholdRequestMessage(inwardDto.getProductId(), inward.getSourceId(), inward.getSourceType());
            } else if (inward.getOutwardId() == null &&
                    inwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.APPROVE)) {
                long startingTime = System.currentTimeMillis();
                productOwnerService.setIsNotify(inwardDto.getProductId(), inward.getDestinationId(), inward.getDestinationType());
                long endingTime = System.currentTimeMillis();
//                System.out.println("Execution duration for set is notify: " + (endingTime - startingTime) + " ms");
            }
            genericDataDTO.setData(saveInwardApproval);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Inward Approval Status : " + inwardDto.getApprovalStatus() + LogConstant.LOG_BY_NAME + inward.getInwardNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Inward Approval" + LogConstant.LOG_BY_NAME + inward.getInwardNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            ex.printStackTrace();
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Inward Approval" + LogConstant.LOG_BY_NAME + inward.getInwardNumber() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }


    @PutMapping("/inwardGroupApproval")
    public GenericDataDTO inwardApproval(@Valid @RequestBody InwardApprovalDTO inwardDto,
                                         HttpServletRequest req) {

        GenericDataDTO response = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {

            List<InwardDto> result = inwardService.processInwardApproval(inwardDto);

            if (result.size() == 1) {
                response.setData(result.get(0)); // same as old behavior
            } else {
                response.setData(result); // group response
            }

            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage(HttpStatus.OK.getReasonPhrase());

        } catch (CustomValidationException ex) {

            response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setResponseMessage(ex.getMessage());

        } catch (Exception ex) {

            response.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setResponseMessage("Error while processing inward");

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove(LogConstant.TRACE_ID);
            MDC.remove("spanId");
        }

        return response;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping(value = "/getAllInwards")
    public GenericDataDTO getAllInwards(HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inwardService.getAllInwards());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fatch All Inwards" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fatch All Inwards" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;

    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inward.INWARD_SHOW_MAC + "\")")
    @PostMapping("/getItemForInward")
    public GenericDataDTO getItemForInward(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "inwardId") Long inwardId, @RequestParam(name = "productId") Long productId, @RequestParam(name = "ownerId") Long ownerId, @RequestParam(name = "ownerType") String ownerType, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            Long pcId = productRepository.findProductCategoryIdByProductId(productId);
            String inwardApprovalStatus = inwardRepository.findApprovalStatusByInwardId(inwardId);
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            if (hasMac || hasSerial) {
                Page<Item> serializedItemForInward = itemService.getSerializedItemForInward(inwardId, productId, ownerId, ownerType, inwardApprovalStatus, requestDTO);
                if (serializedItemForInward.getSize() > 0) {
                    genericDataDTO = itemService.makeGenericResponse(genericDataDTO, serializedItemForInward);
                }
            } else {
                genericDataDTO.setDataList(nonSerializedItemService.getNonSerializedItemForInward(inwardId, productId, ownerId, ownerType));
            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Inward show mac address" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Inward show mac address " + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inward.INWARD_SHOW_MAC + "\")")
    @PostMapping("/searchInwardOutwardItem")
    public GenericDataDTO searchInwardOutwardItem(@RequestBody SearchInventoryDTO searchInventoryDTO, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            Long pcId = productRepository.findProductCategoryIdByProductId(searchInventoryDTO.getProductId());
            Long inwardId = itemService.resolveInwardId(searchInventoryDTO);
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            if (hasMac || hasSerial) {
                Page<Item> serializedItems = itemService.fetchSerializedItems(inwardId, searchInventoryDTO);
                if (!serializedItems.isEmpty()) {
                    genericDataDTO = itemService.makeGenericResponse(genericDataDTO, serializedItems);
                }
            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Inward show mac address" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Inward show mac address " + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/assignNonSerializedItemToCustomer")
    public GenericDataDTO assignNonSerializedItemToCustomer(@RequestBody CustomerInventoryMappingDto inventoryMappingDto, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {
            String defaultTimezone = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
            Integer second = tz.getOffset(new Date().getTime()) / 1000;
            LocalDateTime localDateTime = inventoryMappingDto.getAssignedDateTime().plusSeconds(second);
            inventoryMappingDto.setAssignedDateTime(localDateTime);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(customerInventoryMappingService.saveNonSerializedEntity(inventoryMappingDto));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Non Serialized Item to Customer" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Non Serialized Item to Customer" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Non Serialized Item to Customer" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;

    }

    @PostMapping("/assignNonSerializedItemToEndOwner")
    public GenericDataDTO assignNonSerializedItemToEndOwner(@RequestBody InventoryMappingDto inventoryMappingDto, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {
            String defaultTimezone = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
            Integer second = tz.getOffset(new Date().getTime()) / 1000;
            LocalDateTime localDateTime = inventoryMappingDto.getAssignedDateTime().plusSeconds(second);
            inventoryMappingDto.setAssignedDateTime(localDateTime);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(inventoryMappingService.saveNonSerializedEntity(inventoryMappingDto));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update non Serialized Item To End Owner" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update non Serialized Item To End Owner" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update non Serialized Item To End Owner" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    //Get Customer Based on DTV History
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.INVENTORY_PRE_CUSTOMER_ASSIGN.PRE_CUST_INVENTORY_DTV + "\",\"" + ACLMenuConstants.INVENTORY_POST_CUSTOMER_ASSIGN.POST_CUST_INVENTORY_DTV + "\")")
    @GetMapping("/getCustomerbasedOnDtvHistory")
    public GenericDataDTO getCustomerbasedOnDtvHistory(@RequestParam("customerId") Long customerid, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Customer Based on DTV History " + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            return customerInventoryMappingService.getAllDtvHistoryByCustomer(customerid);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Customer Based on DTV History" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @PostMapping("/reactivateBoxResponse")
    public GenericDataDTO reactivateBoxResponse(@RequestBody List<Long> customerInventoryMappingId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {
            customerInventoryMappingService.getCas(customerInventoryMappingId);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Reactivate Successfully");
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Reactive Box Response" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Reactive Box Response" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/pairBox")
    public GenericDataDTO pairBox(@RequestBody List<Long> customerInventoryMappingId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();
        try {
            customerInventoryMappingService.getpairSTB(customerInventoryMappingId);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Paired  Successfully");
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Pair box" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Pair box" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @PostMapping("/unPairBox")
    public GenericDataDTO unPairBox(@RequestBody List<Long> customerInventoryMappingId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        InwardDto inwardDto = new InwardDto();

        try {
            customerInventoryMappingService.getunpairSTB(customerInventoryMappingId);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Unpaired Successfully");
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Unpair box" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Unpair box" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getDetailsBasedOnConnectionNumber")
    public GenericDataDTO getDetailsBasedOnConnectionNumber(@RequestParam("connectionNumber") String connectionNumber, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setDataList(customerInventoryMappingService.getDetailsBasedOnConnectionNumber(connectionNumber));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Details based on  Connection Number" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Details based on  Connection Number" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/swapServicesFromParantCustomerToChildCustomer")
    public GenericDataDTO swapServicesFromParantCustomerToChildCustomer(@RequestParam("childconnectionNumber") String childconnectionNumber, @RequestParam("parentconnectionNumber") String parentconnectionNumber, @RequestParam("serviceId") Long serviceId, @RequestParam("serviceName") String serviceName, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch service Parent customer to child customer" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return customerInventoryMappingService.swapServicesFromParantToChild(childconnectionNumber, parentconnectionNumber, serviceId, serviceName);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch service Parent-customer to child-customer" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }


    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.INVENTORY_PRE_CUSTOMER_ASSIGN.PRE_CUST_INVENTORY_SWAP + "\",\"" + ACLMenuConstants.INVENTORY_POST_CUSTOMER_ASSIGN.POST_CUST_INVENTORY_SWAP + "\")")
    @GetMapping("/getChildAndParentCustomer")
    public GenericDataDTO getChildAndParentCustomer(@RequestParam("customerId") Long customerId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Child And Parent Customer" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            return customerInventoryMappingService.getChildAndParentCustomer(customerId);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch Child And Parent Customer" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

            return genericDataDTO;
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
    }


    @GetMapping("/getActiveSerialnumberByConnectionNo")
    public GenericDataDTO getActiveSerialnumberByConnectionNo(@RequestParam("connectionNumber") String connectionNumber, @RequestParam("customerId") Integer customerId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setDataList(customerInventoryMappingService.getActiveSerialnumberByConnectionNo(connectionNumber, customerId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All Active SerialNumber By Connection Number" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException exception) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(exception.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All Active SerialNumber By Connection Number" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + exception.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //Assign From Staff List for Inventory Workflow
    @GetMapping("/assignFromStaffList")
    public GenericDataDTO assignFromStaffList(@RequestParam(name = "nextAssignStaff") Integer nextAssignStaff, @RequestParam(name = "eventName") String eventName, @RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "isApproveRequest") boolean isApproveRequest, @RequestParam(name = "isAssignPairItem") boolean isAssignPairItem, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Assigned to next staff");
            customerInventoryMappingService.assignFromStaffList(nextAssignStaff, eventName, entityId, isApproveRequest, isAssignPairItem);
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setPageRecords(0);
            genericDataDTO.setCurrentPageNumber(1);
            genericDataDTO.setTotalPages(1);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch staff list" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch staff list" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch staff list" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    /**
     * Get List of All Inward API
     * @param requestDTO
     * @return
     */
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_INWARD_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inward.INWARD + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.getAll(requestDTO);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inward.INWARD + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = super.getEntityById(id, req);
        InwardDto inwardDto = (InwardDto) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH, AclConstants.OPERATION_BRANCH_VIEW, req.getRemoteAddr(), null, popManagementDTO.getId(), popManagementDTO.getName());
        return dataDTO;
    }

    @PostMapping(value = "/saveManualMacSerial/upload/{inwardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO processUploadFile(@RequestParam MultipartFile file, @PathVariable(name = "inwardId") Long inwardId, HttpServletRequest req) throws IOException {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("spanId", traceContext.spanIdString());
        List<MacSerialListDTO> list = new ArrayList<>();
        try {
            if (!file.getContentType().equals("text/csv") && !file.getContentType().equals("application/vnd.ms-excel")) {
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create mac-address serial no. " + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + " Only CSV file allowed " + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
                throw new Exception("Only CSV files are allowed..!");
            }
            list = fileUtility.readCsv(MacSerialListDTO.class, file.getInputStream());
            for (int i = 0; i < list.size(); i++) {
                MacSerialListDTO dto = list.get(i);
                String mac = dto.getMacAddress();

                if (mac != null) {
                    mac = mac.trim();

                    boolean isNumeric = mac.matches("^[0-9.E+-]+$");

                    if (isNumeric && mac.toUpperCase().contains("E")) {
                        throw new Exception(
                                "Invalid data at row " + (i + 2) +
                                        " Please upload CSV with Number format."
                        );
                    }
                }
            }
            InwardSaveMacSerialDTO inwardSaveMacSerialDTO = new InwardSaveMacSerialDTO();
            inwardSaveMacSerialDTO.setInwardId(inwardId);
            inwardSaveMacSerialDTO.setMacSerialListDTOList(list);
            Long inTrasQty = inwardRepository.findInTransitQuantityByInwardId(inwardSaveMacSerialDTO.getInwardId());
            Long productId = inwardRepository.findProductIdByInwardId(inwardSaveMacSerialDTO.getInwardId());
            Long pcId = productRepository.findProductCategoryIdByProductId(productId);
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            Inward entity = inwardRepository.findById(inwardSaveMacSerialDTO.getInwardId())
                    .orElseThrow(() -> new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Inward not found", null));
            inwardService.validateInwardMAC(inwardSaveMacSerialDTO, hasSerial, hasMac, inTrasQty, entity, false);
            inOutWardMACService.saveManualMacSerial(inwardSaveMacSerialDTO, hasMac, hasSerial, true, entity);
            genericDataDTO.setData(null);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create mac-address serial no." + LogConstant.LOG_BY_NAME + list.size() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create mac-address serial no." + LogConstant.LOG_BY_NAME + list.size() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove(LogConstant.TRACE_ID);
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
//@PostMapping(value = "/saveManualMacSerial/upload/{inwardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//public ResponseEntity<GenericDataDTO> processUploadFile(@RequestParam MultipartFile file,
//                                                        @PathVariable(name = "inwardId") Long inwardId,
//                                                        HttpServletRequest req) throws IOException {
//    GenericDataDTO response = new GenericDataDTO();
//
//    if (!file.getContentType().equals("text/csv")) {
//
//        response.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//        response.setResponseMessage("Only CSV files are allowed!");
//        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
//    }
//
//    // Process file asynchronously using batch processing
//    List<MacSerialListDTO> list = new ArrayList<>();
//    list = fileUtility.readCsv(MacSerialListDTO.class, file.getInputStream());
//    inOutWardMACService.processCsv(list);
//    response.setResponseCode(HttpStatus.ACCEPTED.value());
//    response.setResponseMessage("File is being processed asynchronously.");
//    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
//}


    @PostMapping("/saveManualMacSerial")
    public GenericDataDTO saveManualMacSerial(@Valid @RequestBody InwardSaveMacSerialDTO inwardSaveMacSerialDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            Long inTrasQty = inwardRepository.findInTransitQuantityByInwardId(inwardSaveMacSerialDTO.getInwardId());
            Long productId = inwardRepository.findProductIdByInwardId(inwardSaveMacSerialDTO.getInwardId());
            Long pcId = productRepository.findProductCategoryIdByProductId(productId);
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            Inward entity = inwardRepository.findById(inwardSaveMacSerialDTO.getInwardId())
                    .orElseThrow(() -> new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Inward not found", null));
            inwardService.validateInwardMAC(inwardSaveMacSerialDTO, hasSerial, hasMac, inTrasQty, entity, true);
            inOutWardMACService.saveManualMacSerial(inwardSaveMacSerialDTO, hasMac, hasSerial, false, entity);
            genericDataDTO.setData(null);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create mac-address Serial no. By Inward Id : " + inwardSaveMacSerialDTO.getInwardId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create mac-address serial no. By Inward Id : " + inwardSaveMacSerialDTO.getInwardId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create mac-address serial no. By Inward Id : " + inwardSaveMacSerialDTO.getInwardId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PutMapping("/cust/params/{custId}")
    public GenericDataDTO updateInvParams(@RequestBody CustInvParamsMessage pojo, @PathVariable Integer custId, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        CustomersPojo customersPojo = customersService.getEntityById(custId);
        try {
            genericDataDTO.setData(customerInventoryMappingService.updateCustInvParams(pojo, custId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "update Customer Inventory Params" + LogConstant.LOG_BY_NAME + customersPojo.getUsername() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ce.getMessage() + LogConstant.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            ex.printStackTrace();
            genericDataDTO.setResponseCode(RESP_CODE);
            genericDataDTO.setResponseMessage(ex.getMessage());
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "update Customer Inventory Params" + LogConstant.LOG_BY_NAME + customersPojo.getUsername() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
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

    /**
     * Upload image inventory API
     * @param req
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/inventory/document/upload/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GenericDataDTO assignInventoryDocUpload(@ModelAttribute InventoryFileUploadRequest request, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Upload");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            CustomerInventoryMappingDto dto = customerInventoryMappingService.assignInventoryDocUpload(request);
            genericDataDTO.setData(dto);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Upload Document By Invetory Mapping ID : " + request.getCustomerInventoryId() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Upload Document By Invetory Mapping ID : " + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @RequestMapping(value = "/inventory/document/download/{inventoryMappingId}/{uniqueName}/{section}/", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Integer inventoryMappingId, @PathVariable String uniqueName, @PathVariable String section) {
        org.slf4j.MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [getAllAssignInventories()] ";
        Resource resource = null;
        try {
            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(inventoryMappingId.longValue()).orElse(null);
            if (null == customerInventoryMapping) {
                return ResponseEntity.notFound().build();
            }
            resource = customerInventoryMappingService.getAssignInventoryDoc(customerInventoryMapping, uniqueName, section);
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                LOGGER.info("Downloading document with  " + customerInventoryMapping.getId() + " downloaded Successfully  :  request: { From : {} }; Response : {{}}");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                String errorMessage = "File not found: " + uniqueName + " for inventoryId: " + customerInventoryMapping.getId();
                LOGGER.error(errorMessage);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Error-Message", errorMessage).build();
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to downloadDocument " + inventoryMappingId + "   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}");
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        org.slf4j.MDC.remove("type");
        return null;
    }

    @RequestMapping(value = "/inventory/document/delete/{inventoryMappingId}/{fileName}/{uniqueName}/{section}/", method = RequestMethod.DELETE)
    public GenericDataDTO deleteDocument(@PathVariable Integer inventoryMappingId, @PathVariable String fileName, @PathVariable String uniqueName, @PathVariable String section) {
        org.slf4j.MDC.put("type", "Delete");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(inventoryMappingId.longValue()).orElse(null);
            if (customerInventoryMapping == null) {
                LOGGER.error("CustomerInventoryMapping not found for ID: {}" + inventoryMappingId);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Inventory Mapping not found.");
                return genericDataDTO;
            }
            File file = customerInventoryMappingService.getAssignInventoryFile(customerInventoryMapping, uniqueName, section);
            if (!file.exists()) {
                LOGGER.error("File not found: {} for inventoryMappingId: {}" + uniqueName + customerInventoryMapping.getId());
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("File not found.");
                return genericDataDTO;
            } else if (file.exists()) {
                customerInventoryMappingService.deleteFileFromDatabase(uniqueName);
                LOGGER.info("File deleted successfully: {} for inventoryMappingId: {}" + uniqueName + customerInventoryMapping.getId());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("File deleted successfully.");

            } else {
                LOGGER.error("Failed to delete file: {} for inventoryMappingId: {}" + uniqueName + customerInventoryMapping.getId());
                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                genericDataDTO.setResponseMessage("Failed to delete file.");
            }
            if (file.delete()) {
                LOGGER.info("File deleted successfully: {} for inventoryMappingId: {}" + uniqueName + customerInventoryMapping.getId());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("File deleted successfully.");
            } else {
                LOGGER.error("Failed to delete file: {} for inventoryMappingId: {}" + uniqueName + customerInventoryMapping.getId());
                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                genericDataDTO.setResponseMessage("Failed to delete file.");
            }
        } catch (Exception ex) {
            LOGGER.error("Error occurred while deleting file for inventoryMappingId: {}" + inventoryMappingId, ex);
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage("An error occurred while deleting the file.");
        } finally {
            org.slf4j.MDC.remove("type");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inward.INWARD_DELETE + "\")")
    @DeleteMapping("/deleteInward")
    public GenericDataDTO deleteInward(@RequestParam(name = "inwardId") Long inwardId, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(inwardId);
            boolean flag = inwardService.deleteVerification(inwardId.intValue());
            if (Objects.nonNull(inwardDto)) {
                if (flag) {
                    inwardService.deleteInward(inwardDto);
                    if (inwardDto.getApprovalStatus().equalsIgnoreCase(CommonConstants.PENDING) && inwardDto.getOutwardId() == null) {
                        inwardDto.setApprovalStatus("Deleted");
                    }
                    genericDataDTO = super.delete(inwardDto, req);
                    if (inwardDto != null)
                        LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Inward" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
                } else {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(DeleteContant.INWARD_NUMBER_DELETE_EXIST);
                    LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Inward" + LogConstant.LOG_BY_NAME + inwardDto.inwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
                }
            } else {
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Inward" + LogConstant.LOG_BY_NAME + inwardId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
//            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Inward"+LogConstant.LOG_BY_NAME+inwardDto.inwardNumber+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE+ex.getMessage() +   LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @DeleteMapping("/item/{id}")
    public GenericDataDTO deleteItem(@PathVariable long id) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        boolean isDeleted = itemService.deleteItemById(id);
        if (isDeleted) {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Item with ID " + id + "deleted successfully.");
        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage("Item is bounded cannot be deleted.");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/getInventoryApprovals")
    public GenericDataDTO getInventoryApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO, @RequestParam(name = "mvnoId", required = false) Integer mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return customerInventoryMappingService.getInventoryApprovals(paginationRequestDTO, mvnoId);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch wareHouse And ProductWiseInventories" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @RequestMapping(value = "/inventory/documentList/{inventoryMappingId}", method = RequestMethod.GET)
    public GenericDataDTO downloadDocument(@PathVariable Integer inventoryMappingId) {
        org.slf4j.MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [getAllInventoryDocument()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<FileMappingList> customerInventoryFileMappingList = customerInventoryMappingService.getFilesByInventoryId(Long.valueOf(inventoryMappingId));
            if (customerInventoryFileMappingList.isEmpty()) {
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setData(null);
                genericDataDTO.setDataList(new ArrayList());
                genericDataDTO.setResponseMessage("No Record found for download");
            } else {
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setData(null);
                genericDataDTO.setDataList(customerInventoryFileMappingList);
                genericDataDTO.setResponseMessage("Record found successfully");
            }

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setData(null);
            LOGGER.error("Unable to fetch document by  " + inventoryMappingId + "   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}");
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        org.slf4j.MDC.remove("type");
        return genericDataDTO;
    }



    @GetMapping("/downloadSkipData")
    public ResponseEntity<?> downloadExcel(
            @RequestParam("id") Long id,
            @RequestParam("type") String type) {
        try{
            if (id == null) {
                throw new IllegalArgumentException("Id must not be null");
            }

            ByteArrayInputStream excelFile = outWordUploadService.generateDynamicExcel(id, type);

            InputStreamResource resource = new InputStreamResource(excelFile);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=skipped_items_" + id + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(
                            MediaType.APPLICATION_OCTET_STREAM
                    )
                    .body(resource);

        } catch (CustomValidationException e) {
            GenericDataDTO data =    new GenericDataDTO();
            data.setResponseCode(e.getErrCode());
            data.setResponseMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(data);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong");
        }
    }
}
