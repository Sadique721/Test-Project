package com.savbill.inventorymanagement.modules.InventoryManagement.Outward;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.savbill.inventorymanagement.core.constants.*; 
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.OutWordUploadService;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.Item;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductCategory.ProductCategoryService;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUser;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.modules.StaffUser.StaffUserRepository;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceService;
import com.savbill.inventorymanagement.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapingDTO;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.Inward.InwardServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Item.ItemServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.NonSerializedItem.NonSerializedItemServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.Product;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Product.ProductServiceImpl;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductOwner.ProductOwnerService;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.modules.constants.UpdateDiffFinder;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@Api(value = "OutwardController", description = "REST APIs related to inward Entity!!!!", tags = "outwards-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.OUTWARDS)
public class OutwardController extends ExBaseAbstractController<OutwardDto> {
    @Autowired
    OutwardServiceImpl outwardService;
    @Autowired
    ClientServiceService clientServiceSrv;
    @Autowired
    InwardServiceImpl inwardService;
    @Autowired
    ProductOwnerService productOwnerService;
    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    ProductServiceImpl productService;
    @Autowired
    ProductCategoryService productCategoryService;
    @Autowired
    NonSerializedItemServiceImpl nonSerializedItemService;
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductCategoryRepository productCategoryRepository;
    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    InwardMapper inwardMapper;
    @Autowired
    InwardRepository inwardRepository;
    @Autowired
    OutwardMapper outwardMapper;
    @Autowired
    OutwardRepository outwardRepository;
    @Autowired
    Tracer tracer;
    @Autowired
    private OutWordUploadService outWordUploadService;

    public OutwardController(OutwardServiceImpl outwardService) {
        super(outwardService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[OutwardController]";
    }

    OutwardDto outwardDto = new OutwardDto();
    private static final Logger LOGGER = Logger.getLogger(OutwardController.class);

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD_CREATE + "\")")
    @Override
    @Transactional
    public GenericDataDTO save(@Valid @RequestBody OutwardDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            String defaultTimezone = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
            Integer second = tz.getOffset(new Date().getTime()) / 1000;
            LocalDateTime localDateTime = entityDTO.getOutwardDateTime().plusSeconds(second);
            entityDTO.setOutwardDateTime(localDateTime);
            OutwardDto outwardDto = outwardService.saveEntity(entityDTO, false);
            genericDataDTO.setData(outwardDto);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Outward" + LogConstant.LOG_BY_NAME + entityDTO.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Outward" + LogConstant.LOG_BY_NAME + entityDTO.outwardNumber + LogConstant.LOG_BY_NAME + entityDTO.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Outward" + LogConstant.LOG_BY_NAME + entityDTO.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD_CREATE + "\")")
    @PostMapping("/saveBulk")
    @ApiOperation(
            value = "Create multiple outward entries",
            notes = "Send a JSON array of OutwardDto objects. Same business logic as single create will be applied to each item."
    )
    public GenericDataDTO saveBulk(@RequestBody List<OutwardDto> entityDTOList,
                                   HttpServletRequest req) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create-Bulk");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            List<OutwardDto> savedList = outwardService.saveBulk(entityDTOList);

            genericDataDTO.setData(savedList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());

            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Bulk Create Outward"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS
                    + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Bulk Create Outward"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED
                    + APIConstants.ERROR_MESSAGE + ce.getMessage()
                    + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Bulk Create Outward"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED
                    + APIConstants.ERROR_MESSAGE + ex.getMessage()
                    + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD_CREATE + "\")")
    @PostMapping("/saveBulkOutward")
    @ApiOperation(
            value = "Create multiple outward entries",
            notes = "Creates grouped outward records using first outward ID as groupId and performs aggregated stock update."
    )
    public GenericDataDTO saveBulkOutward(@RequestBody List<OutwardDto> entityDTOList,
                                   HttpServletRequest req) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();

        MDC.put("type", "Create-Bulk-Outward");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        try {

            //  CALL YOUR NEW BULK METHOD
            List<OutwardDto> savedList = outwardService.saveBulkEntity(entityDTOList, false);

            genericDataDTO.setData(savedList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Bulk Outward Created Successfully");

            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Bulk Create Outward"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS
                    + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS
                    + " | Total Records: " + savedList.size());

        } catch (CustomValidationException ce) {

            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Bulk Create Outward"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED
                    + APIConstants.ERROR_MESSAGE + ce.getMessage()
                    + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {

            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Something went wrong while processing bulk outward");

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Bulk Create Outward"
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


    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD_CREATE + "\")")
    @PostMapping(
            value = "/saveBulkOutwardAndMacMapping",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ApiOperation(
            value = "Create multiple outward entries with optional file upload",
            notes = "Accepts JSON string payload and files. Files are matched using fileName."
    )
    public GenericDataDTO saveBulkOutwardWithMacMapping(
            @RequestPart("bulkOutwardsList") String entityDTOList,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            HttpServletRequest req) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();

        MDC.put("type", "Create-Bulk-Outward-With-Upload");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());

        try {

            //  OBJECT MAPPER CONFIG
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

            //  PARSE JSON SAFELY (NO TypeReference)
            JsonNode rootNode = mapper.readTree(entityDTOList);

            List<OutwardDto> entityList = new ArrayList<>();

            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    OutwardDto dto = mapper.treeToValue(node, OutwardDto.class);
                    entityList.add(dto);
                }
            } else {
                OutwardDto dto = mapper.treeToValue(rootNode, OutwardDto.class);
                entityList.add(dto);
            }

            //  VALIDATION
            if (entityList.isEmpty()) {
                throw new IllegalArgumentException("Outward list cannot be empty");
            }

            //  BUILD FILE MAP (filename → file)
            Map<String, MultipartFile> fileMap = new HashMap<>();

            if (files != null) {
                for (MultipartFile file : files) {
                    if (file.getOriginalFilename() != null) {
                        fileMap.put(file.getOriginalFilename().trim(), file);
                    }
                }
            }

            //  STEP 1: SAVE BULK OUTWARD
            List<OutwardDto> savedList =
                    outwardService.saveBulkEntity(entityList, false);

            //  STEP 2: FILE UPLOAD (OPTIONAL PER RECORD)
            for (OutwardDto saved : savedList) {

                String fileName = saved.getFileName();

                //  NON-SERIALIZED → SKIP
                if (fileName == null || fileName.trim().isEmpty()) {
                    continue;
                }

                MultipartFile file = fileMap.get(fileName.trim());

                if (file == null) {
                    throw new IllegalArgumentException("File not found for: " + fileName);
                }

                if (file.isEmpty()) {
                    throw new IllegalArgumentException("File is empty: " + fileName);
                }

                String uploadedName = file.getOriginalFilename();

                boolean isCsv = uploadedName != null && uploadedName.toLowerCase().endsWith(".csv");
                boolean isExcel = uploadedName != null &&
                        (uploadedName.toLowerCase().endsWith(".xls") || uploadedName.toLowerCase().endsWith(".xlsx"));

                if (!isCsv && !isExcel) {
                    throw new IllegalArgumentException("Invalid file type: " + uploadedName);
                }

                //  HEADER VALIDATION
                if (isCsv) {
                    outWordUploadService.validateHeadersOnly(file, saved.getId(), CommonConstants.OUTWARD);
                } else {
                    outWordUploadService.validateExcelHeadersOnly(file, saved.getId(), CommonConstants.OUTWARD);
                }

                //  EXISTING UPLOAD LOGIC (ASYNC)
                outWordUploadService.uploadOutwardFile(
                        file,
                        saved.getId(),
                        saved.getProductId().getId(),
                        saved.getDestinationId(),
                        saved.getDestinationType()
                );
            }

            genericDataDTO.setData(savedList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Bulk Outward Created & Files Processed Successfully");

            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Bulk Create Outward With Upload"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS
                    + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS
                    + " | Total Records: " + savedList.size());

        } catch (CustomValidationException ce) {

            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());

            LOGGER.error("Validation failed in bulk outward upload", ce);

        } catch (Exception ex) {

            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());

            LOGGER.error("Bulk outward with upload failed", ex);

        } finally {

            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove(LogConstant.TRACE_ID);
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @GetMapping("/findByGroupId/{id}")
    @ApiOperation(
            value = "Get outward group (parent + children)",
            notes = "Fetch parent outward and all its child records using any outward ID (parent or child)"
    )
    public GenericDataDTO getOutwardGroup(@PathVariable Long id, HttpServletRequest req) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();

        MDC.put("type", "Fetch-Outward-Group");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {

            //  CALL SERVICE
            List<OutwardDto> list = outwardService.getOutwardGroup(id);

            genericDataDTO.setData(list);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Outward group fetched successfully");

            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Fetch Outward Group"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS
                    + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS
                    + " | GroupId: " + id
                    + " | Total Records: " + list.size());

        } catch (RuntimeException ex) {

            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage(ex.getMessage());

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Fetch Outward Group"
                    + LogConstant.REQUEST_BY + getLoggedInUser().getUsername()
                    + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED
                    + APIConstants.ERROR_MESSAGE + ex.getMessage()
                    + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_FOUND.value());

        } catch (Exception ex) {

            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Error while fetching outward group");

            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom")
                    + LogConstant.REQUEST_FOR + "Fetch Outward Group"
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


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ADD + "\")")
    @PostMapping(value = "/saveAllInventoryRequest")
    public GenericDataDTO saveAllInventoryRequest(@Valid @RequestBody List<OutwardDto> entityDTOList, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            List<OutwardDto> finalOutwardDTO = new ArrayList<>();
            for (OutwardDto entityDTO : entityDTOList) {
                String defaultTimezone = TimeZone.getDefault().getID();
                TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
                Integer second = tz.getOffset(new Date().getTime()) / 1000;
                LocalDateTime localDateTime = entityDTO.getOutwardDateTime().plusSeconds(second);
                entityDTO.setOutwardDateTime(localDateTime);
                OutwardDto outwardDto = outwardService.saveEntity(entityDTO, false);
                outwardService.sendInventoryFulfilmentKafkaMessage(outwardDto);
                finalOutwardDTO.add(outwardDto);
            }
            genericDataDTO.setDataList(finalOutwardDTO);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create All Inventory Request" + LogConstant.LOG_BY_NAME + outwardDto.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create All Inventory Request" + LogConstant.LOG_BY_NAME + outwardDto.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + e.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD_EDIT + "\")")
    @Override
    @Transactional
    public GenericDataDTO update(@Valid @RequestBody OutwardDto entityDTO, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            outwardService.getEntityForUpdateAndDelete(entityDTO.getId());
            OutwardDto outwardD = outwardMapper.domainToDTO(outwardRepository.findById(entityDTO.getId()).orElse(null), new CycleAvoidingMappingContext());
            Outward old = outwardService.getOutwardById(entityDTO.getId());
            Outward oldClone = new Outward(old);
            long increasedQty = (entityDTO.getQty() - entityDTO.getUsedQty() - outwardD.getUnusedQty());
            if (increasedQty != 0) {
//                InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(entityDTO.getInwardId().getId());
                InwardDto inwardDto = inwardMapper.domainToDTO(inwardRepository.findById(entityDTO.getInwardId().getId()).orElse(null), new CycleAvoidingMappingContext());
                inwardDto.setUsedQty(increasedQty + inwardDto.getUsedQty());
                inwardDto.setUnusedQty(inwardDto.getUnusedQty() - increasedQty);
                inwardService.updateEntity(inwardDto);
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update outward" + LogConstant.LOG_BY_NAME + entityDTO.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_UNAUTHORIZED + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);
            }
            entityDTO.setQty(0L);
            entityDTO.setUnusedQty(0L);
            entityDTO.setInTransitQty(entityDTO.getInTransitQty());
            entityDTO.setUsedQty(0L);
            entityDTO.setOutTransitQty(0L);
            entityDTO.setRejectedQty(0L);
            entityDTO.setApprovalStatus(CommonConstants.PENDING);
            String defaultTimezone = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(defaultTimezone);
            Integer second = tz.getOffset(new Date().getTime()) / 1000;
            LocalDateTime localDateTime = entityDTO.getOutwardDateTime().plusSeconds(second);
            entityDTO.setOutwardDateTime(localDateTime);
            OutwardDto outwardDto = outwardService.updateEntity(entityDTO);
            inwardService.updateInwardOfOutwardStatus(entityDTO.getId(), entityDTO.getStatus());
            genericDataDTO.setData(outwardDto);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            Outward outward = outwardMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
//          auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update outward" + LogConstant.LOG_BY_NAME + outwardDto.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + " Updated Details " + UpdateDiffFinder.getUpdatedDiff(oldClone, outward) + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update outward" + LogConstant.LOG_BY_NAME + outwardDto.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ce.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update outward" + LogConstant.LOG_BY_NAME + outwardDto.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    /*@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getAllOutwardByProductAndStaff")
    public GenericDataDTO getAllOutwardByProductAndStaff(@RequestParam(name = "productId") Long productId, @RequestParam(name = "staffId") Long staffId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(outwardService.getAllOutwardByProductAndStaff(productId, staffId));
            logger.info("get All outward product and Staff with Staff id "+productId+"   is Fetched Succesfully Successfully:  request: { From : {}}; Response : {{}}",getModuleNameForLog(),APIConstants.SUCCESS);
            //            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("get All outward product and Staff with product id "+productId+":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
        }
        MDC.remove("type");
        return genericDataDTO;

    }
*/
    /*@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ADD + "\")")
    @Transactional
    @PostMapping("/assignToCustomer")
    public GenericDataDTO assignToCustomer(@RequestBody CustomerInventoryMappingDto inventoryMappingDto) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        MDC.put("type", "Update");
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(customerInventoryMappingService.saveEntity(inventoryMappingDto));
            logger.info("Assigning outward  To customer With customer name "+inventoryMappingDto.getCustomerName()+" is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(ce.getMessage(), ce);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable to asign outward to customer with name "+inventoryMappingDto.getCustomerName()+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog() ,genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ce.getMessage());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to asign outward to customer with name "+inventoryMappingDto.getCustomerName()+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog() ,genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
*/
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ADD + "\")")
//    @Transactional
//    @PostMapping("/assignToCustomer")
//    public GenericDataDTO assignToCustomer(@RequestBody CustomerInventoryMappingDto inventoryMappingDto) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(customerInventoryMappingService.saveEntity(inventoryMappingDto));

    /// /            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getByStaffId")
    public GenericDataDTO assignToCustomer(@RequestParam(name = "staffId") Long staffId, HttpServletRequest req) {
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
            genericDataDTO.setDataList(outwardService.getByStaffId(staffId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch staff by id : " + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            //            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Assign Outward Service to Staff name "+staffname+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch staff by id : " + staffId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

  /*  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getCustomerInventoryMappingByStaffId")
    public GenericDataDTO getCustomerInventoryMappingByStaffId(@RequestParam(name = "staffId") Long staffId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String staffname=customerInventoryMappingService.getStaffDetails(staffId);
        MDC.put("type", "Fetch");
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(customerInventoryMappingService.getCustomerInventoryMappingByStaffId(staffId));
            logger.info("Outward get Customer inventory by Staff  "+staffname+"  is created Successfully:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to  get Customer inventory by Staff  "+staffname+"  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());

        }
        MDC.remove("type");
        return genericDataDTO;

    }*/

  /*  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping("/getByCustomerId")
    public GenericDataDTO getByCustomerId(@RequestBody PaginationRequestDTO requestDTO) {

        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            logger.info("Outward get by Customer Id  "+requestDTO+"  is created Successfully:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
            return customerInventoryMappingService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Outward get  by Customer Id  "+requestDTO+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());

        }
        MDC.remove("type");
        return genericDataDTO;

    }*/

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page,
                                 @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize,
                                 @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder,
                                 @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy,
                                 @RequestBody GenericSearchDTO filter, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", outwardService.getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter, req);
            if (genericDataDTO.getDataList().isEmpty()) {
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Outward By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + outwardService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            } else
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Outward By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + outwardService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search Outward By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstant.REQUEST_BY + outwardService.getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + APIConstants.EXPECTATION_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping(value = "/searchAssignInventories")
    public GenericDataDTO searchAssignInventories(@RequestParam(name = "staffId") Long staffId, @RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page, @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize, @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder, @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        String SUBMODULE = getModuleNameForLog() + " [searchAssignInventories()] ";
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        try {
//            if (genericDataDTO.getResponseCode() == 406)
//            {
//                List<DTO> list = service.getAllEntities().stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff() || d.getMvnoId() == null ).collect(Collectors.toList());
//                genericDataDTO.setDataList(list);
//                genericDataDTO.setTotalRecords(list.size());
//                return genericDataDTO;
//            }
            if (null == filter || null == filter.getFilter() || 0 == filter.getFilter().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please provide search criteria!");
//                logger.error("Unable to search  with Assign invoices :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());

                return genericDataDTO;
            }
            if (null != pageSize && pageSize > MAX_PAGE_SIZE) pageSize = MAX_PAGE_SIZE;
            genericDataDTO = outwardService.searchAssignInventories(filter.getFilter(), page, pageSize, sortBy, sortOrder, staffId);

            if (null != genericDataDTO) {

                if (genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO = new GenericDataDTO();
                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);
                    LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search assigned inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_NULL_VALUE + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);

                }

                return genericDataDTO;

            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No Record Found!");
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search assigned inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search assigned inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Search assigned inventory" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

  /*  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping(value = "/getAllAssignInventories")
    public GenericDataDTO getAllAssignInventories(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "staffId") Long staffId) {
        String SUBMODULE = getModuleNameForLog() + " [getAllAssignInventories()] ";

        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            genericDataDTO = outwardService.getAssignInventories(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), staffId);
            if (null != genericDataDTO) {
                logger.info("fetching allAssigned inventories:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                logger.error("Unable to fetch all inventories :  request: { From : {},}; Response : {{}};Error :{} ;",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to  to fetch all inventories :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage(),ex.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }*/

   /* @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
    @GetMapping("/approveInventory")
    public GenericDataDTO approveInventory(@RequestParam(name = "customerInventoryMappingId") Long customerInventoryMappingId,@RequestParam(name = "isApproveRequest") boolean isApproveRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        MDC.put("type", "Fetch");
        try {
            logger.info("Getting Inventory Approve from  with id  "+customerInventoryMappingId+"  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
            return customerInventoryMappingService.approveInventory(customerInventoryMappingId,isApproveRequest);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to Approve inventory  with "+customerInventoryMappingId+":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),genericDataDTO.getResponseMessage());
        }
        MDC.remove("type");
        return genericDataDTO;

    }*/

  /*    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
    @GetMapping("/replaceInventory")
    public GenericDataDTO replaceInventory(@RequestParam(name = "oldMacMappingId") Long oldMacMappingId, @RequestParam(name = "newMacMappingId") Long newMacMappingId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setData(customerInventoryMappingService.replaceInventory(oldMacMappingId, newMacMappingId));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());

//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }*/

  /*  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
    @GetMapping("/approveReplaceInventory")
    public GenericDataDTO approveReplaceInventory(@RequestParam(name = "macMappingId") Long macMappingId, @RequestParam(name = "billAble") String billAble, @RequestParam(name = "isApproveRequest") boolean isApproveRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return customerInventoryMappingService.approveReplaceInventory(macMappingId, Boolean.parseBoolean(billAble),isApproveRequest);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;

    }*/

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getInventoryApproveProgressForReplace")
//    public GenericDataDTO getInventoryApproveProgressForReplace(@RequestParam(name = "macMappingId") Long macMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setDataList(customerInventoryMappingService.getInventoryApproveProgressForReplace(macMappingId));
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
//    @GetMapping("/rejectInventory")
//    public GenericDataDTO rejectInventory(@RequestParam(name = "customerInventoryMappingId") Long customerInventoryMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return customerInventoryMappingService.rejectInventory(customerInventoryMappingId);
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_EDIT + "\")")
//    @GetMapping("/rejectReplaceInventory")
//    public GenericDataDTO rejectReplaceInventory(@RequestParam(name = "macMappingId") Long macMappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return customerInventoryMappingService.rejectReplaceInventory(macMappingId);

    /// /            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody OutwardDto entityDTO, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            outwardService.getEntityForUpdateAndDelete(entityDTO.getId());
            boolean flag = outwardService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                genericDataDTO = super.delete(entityDTO, req);
                OutwardDto outwardDto = (OutwardDto) genericDataDTO.getData();
                if (outwardDto != null)
                    //                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_OUTWARD_MANAGEMENT,
                    //                        AclConstants.OPERATION_OUTWARD_MANAGEMENT_DELETE, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getInwardNumber());
                    LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleting Outward" + LogConstant.LOG_BY_NAME + entityDTO.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(DeleteContant.OUTWARD_NUMBER_DELETE_EXIST);
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleting Outward" + LogConstant.LOG_BY_NAME + entityDTO.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            }
        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleting Outward " + LogConstant.LOG_BY_NAME + entityDTO.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping("/getItemHistoryByProduct")
    public GenericDataDTO getInOutMacMapping(@RequestBody ItemHistoryRequestDTO itemHistoryRequestDTO,
                                             HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            PaginationRequestDTO paginationRequestDTO = itemHistoryRequestDTO.getPaginationRequestDTO();
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(itemService.getInOutwardMappingDataList(genericDataDTO, itemHistoryRequestDTO));
            int totalRecords = genericDataDTO.getDataList().size();
            int totalPages = (int) Math.ceil((double) totalRecords / paginationRequestDTO.getPageSize());
            int fromIndex = (paginationRequestDTO.getPage() - 1) * paginationRequestDTO.getPageSize();
            int toIndex = Math.min(fromIndex + paginationRequestDTO.getPageSize(), totalRecords);
            if (totalRecords > 0) {
                List paginatedList = genericDataDTO.getDataList().subList(fromIndex, toIndex);
                genericDataDTO.setDataList(paginatedList);
                // Set pagination information
                genericDataDTO.setTotalRecords(totalRecords);
                genericDataDTO.setPageRecords(paginatedList.size());
                genericDataDTO.setCurrentPageNumber(paginationRequestDTO.getPage());
                genericDataDTO.setTotalPages(totalPages);
                if (genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                    genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                }
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch In out Mc-address" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch In out Mc-address" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/getItemBasedOnCondtion")
    public GenericDataDTO getInOutMacMappingForItemCondition(@RequestBody PaginationRequestDTO paginationRequestDTO ,@RequestParam(name = "productId") Long productId, @RequestParam(name = "itemId") Long itemId, @RequestParam(name = "ownerId") Integer ownerId, @RequestParam("ownerShipType") String ownerShipType, @RequestParam("replacementReason") String replacementReason, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            Product product = productRepository.findById(productId).get();
            boolean hasSerial = product.getProductCategory().isHasSerial();
            boolean isTrackable = product.getProductCategory().isHasTrackable();
            boolean hasMac = product.getProductCategory().isHasMac();
            if (product.getProductCategory().getType().contains("CustomerBind")) {
                StaffUser staffUser = staffUserRepository.findById(ownerId).get();
                if (staffUser.getPartnerid() != 1) {
                    ownerId = Integer.valueOf(staffUser.getPartnerid());
                    ownerShipType = CommonConstants.PARTNER;
                }
                if (hasMac || hasSerial) {
                    genericDataDTO.setDataList(itemService.getInOutMacMappingForSerializedItemBasedOnItemCondtion(productId, itemId, Long.valueOf(ownerId), ownerShipType, replacementReason));
                }
                if (!hasSerial && !isTrackable) {
                    genericDataDTO.setDataList(itemService.getInOutMacMappingForSerializedItemBasedOnItemCondtion(productId, itemId, Long.valueOf(ownerId), ownerShipType, replacementReason));
                }
            }
            int totalRecords = genericDataDTO.getDataList().size();
            int totalPages = (int) Math.ceil((double) totalRecords / paginationRequestDTO.getPageSize());
            int fromIndex = (paginationRequestDTO.getPage() - 1) * paginationRequestDTO.getPageSize();
            int toIndex = Math.min(fromIndex + paginationRequestDTO.getPageSize(), totalRecords);
            if (totalRecords > 0) {
                List paginatedList = genericDataDTO.getDataList().subList(fromIndex, toIndex);
                genericDataDTO.setDataList(paginatedList);
                genericDataDTO.setTotalRecords(totalRecords);
                genericDataDTO.setPageRecords(paginatedList.size());
                genericDataDTO.setCurrentPageNumber(paginationRequestDTO.getPage());
                genericDataDTO.setTotalPages(totalPages);
            }
            if (genericDataDTO.getDataList().isEmpty()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Item based on condition" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_NOT_FOUND + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);
            }
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Item based on condition" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Item based on condition" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }
    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getItemBasedOnProductType")
    public GenericDataDTO getAllItembasedOnProductType(@RequestParam("productId") Long productId, @RequestParam("ownerid") Long ownerid, @RequestParam("ownerType") String ownerType, @RequestParam("planId") Long planId, @RequestParam(name = "planGroupId", required = false) Long planGroupId, @RequestParam(name = "productCategoryId", required = false) Long productCategoryId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOList = new ArrayList<>();
            Product product = productRepository.findById(productId).get();
            boolean hasSerial = product.getProductCategory().isHasSerial();
            boolean isTrackable = product.getProductCategory().isHasTrackable();
            boolean hasMac = product.getProductCategory().isHasMac();
            if (product.getProductCategory().getType().equalsIgnoreCase("CustomerBind") || product.getProductCategory().getType().equalsIgnoreCase("CustomerBind, NetworkBind")) {
                if (hasMac || hasSerial) {
                    genericDataDTO.setDataList(itemService.getInOutMacMappingBasedOnProductType(productId, ownerid, ownerType, planId, planGroupId, productCategoryId));
                }
                if (!hasSerial && isTrackable) {
                    genericDataDTO.setDataList(itemService.getInOutMacMappingForNonSerializedItemBasedOnProductCondtion(productId, ownerid, ownerType, planId, planGroupId, productCategoryId));
                }
                if (!hasSerial && !isTrackable) {
                    genericDataDTO.setDataList(itemService.getInOutMacMappingForSerializedItemBasedOnProductType(productId, ownerid, ownerType, planId, planGroupId, productCategoryId));
                }
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Item based on product type" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

            }
            if (genericDataDTO.getDataList().isEmpty()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Item based on product type" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_STATUS_CODE + APIConstants.FAIL);

            }
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Item based on product type" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")

    @GetMapping("/getAvailableQtyDetailsByProductAndDestination")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Request.INVENTORY_ASSIGNED_REQUEST_FULLFILLMENT + "\")")
    public GenericDataDTO getAvailableQtyDetailsByProductAndDestination(@RequestParam(name = "productId") Long productId, @RequestParam(name = "ownerId") Long ownerId, @RequestParam(name = "ownerType") String ownerType, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(productOwnerService.getAvailableQtyDetailsByProductAndDestination(productId, ownerId, ownerType));
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch available qty details" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch available qty details" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PostMapping("/getItemForOutward")
    public GenericDataDTO getItemForOutward(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "productId") Long productId, @RequestParam(name = "ownerId") Long ownerId, @RequestParam(name = "ownerType") String ownerType, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
            Long pcId = productRepository.findProductCategoryIdByProductId(productId);
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            if (hasMac || hasSerial) {
                Page<Item> serializedItemForOutward = itemService.getSerializedItemForOutward(productId, ownerId, ownerType, requestDTO);
                if (serializedItemForOutward.getSize() > 0) {
                    genericDataDTO = itemService.makeGenericResponse(genericDataDTO, serializedItemForOutward);
                }
            } else {
                genericDataDTO.setDataList(nonSerializedItemService.getNonSerializedItemForOutward(productId, ownerId, ownerType));
            }
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Items for outward" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch items for outward" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD_SHOW_MAC + "\")")
    @PostMapping("/getAssignOutwardItem")
    public GenericDataDTO getAssignOutwardItem(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "outwardId") Long outwardId, @RequestParam(name = "productId") Long productId, @RequestParam(name = "ownerId") Long ownerId, @RequestParam(name = "ownerType") String ownerType, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
//            requestDTO = setDefaultPaginationValues(requestDTO);
            Long inwardId = inwardRepository.findInwardIdByOutwardId(outwardId);
            String inwardApprovalStatus = inwardRepository.findApprovalStatusByInwardId(inwardId);
            Long pcId = productRepository.findProductCategoryIdByProductId(productId);
            boolean hasSerial = productCategoryRepository.findHasSerialById(pcId);
            boolean hasMac = productCategoryRepository.findHasMacById(pcId);
            if (hasMac || hasSerial) {
                Page<Item> serializedItemForInward = itemService.getSerializedItemForInward(inwardId, productId, ownerId, ownerType, inwardApprovalStatus, requestDTO);
//                genericDataDTO.setDataList(serializedItemForInward);
                if (serializedItemForInward.getSize() > 0) {
                    genericDataDTO = itemService.makeGenericResponse(genericDataDTO, serializedItemForInward);
                }
//                genericDataDTO.setDataList(itemService.getSerializedItemForInward(inwardId, productId, ownerId, ownerType, inwardApprovalStatus, requestDTO));
            } else {
                genericDataDTO.setDataList(nonSerializedItemService.getNonSerializedItemForInward(inwardId, productId, ownerId, ownerType));
            }
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch items for outward" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch items for outward" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_OUTWARD_MANAGEMENT_VIEW + "\")")
    @GetMapping("/getNonTrackableProductQty")
    public GenericDataDTO getNonTrackableProductQty(@RequestParam(name = "productId") Long productId, @RequestParam(name = "ownerId") Long ownerId, @RequestParam(name = "ownerType") String ownerType, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            Product product = productRepository.findById(productId).get();
            boolean hasSerial = product.getProductCategory().isHasSerial();
            boolean isTrackable = product.getProductCategory().isHasTrackable();
            if (!hasSerial && !isTrackable) {
                genericDataDTO.setDataList(productOwnerService.getNonTrackableProductQty(productId, ownerId, ownerType));
            }
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch non-Trackable product" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch non-Trackable product" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());


        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD + "\")")
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

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = super.getEntityById(id, req);
        OutwardDto outwardDto = (OutwardDto) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH, AclConstants.OPERATION_BRANCH_VIEW, req.getRemoteAddr(), null, popManagementDTO.getId(), popManagementDTO.getName());
        return dataDTO;
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

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD_DELETE + "\")")
    @DeleteMapping("/deleteOutWard")
    public GenericDataDTO deleteInward(@RequestParam(name = "outwardId") Long outwardId, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            OutwardDto outwardDto = outwardService.getEntityForUpdateAndDelete(outwardId);
            if (Objects.nonNull(outwardDto)) {
                boolean flag = outwardService.deleteVerification(Math.toIntExact(outwardId));
                if (flag) {
                    genericDataDTO = super.delete(outwardDto, req);
                    OutwardDto responseDto = (OutwardDto) genericDataDTO.getData();
                    if (outwardDto != null)
                        //                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_OUTWARD_MANAGEMENT,
                        //                        AclConstants.OPERATION_OUTWARD_MANAGEMENT_DELETE, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getInwardNumber());
                        LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleting Outward" + LogConstant.LOG_BY_NAME + outwardDto.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

                } else {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    genericDataDTO.setResponseMessage(DeleteContant.OUTWARD_NUMBER_DELETE_EXIST);
                    LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleting Outward" + LogConstant.LOG_BY_NAME + outwardDto.outwardNumber + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
                }
            } else {
                LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleting Outward " + LogConstant.LOG_BY_NAME + outwardId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
            }
        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Deleting Outward " + LogConstant.LOG_BY_NAME + outwardId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + LogConstant.LOG_NO_RECORD_FOUND + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/searchItemHistoryByProduct")
    public GenericDataDTO searchInOutMacMapping(@RequestBody SearchInOutMacMapping searchInOutMacMapping,
                                                HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            PaginationRequestDTO paginationRequestDTO = searchInOutMacMapping.getPaginationRequestDTO();
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(itemService.searchInOutwardMacMapping(searchInOutMacMapping));
            int totalRecords = genericDataDTO.getDataList().size();
            int totalPages = (int) Math.ceil((double) totalRecords / paginationRequestDTO.getPageSize());
            int fromIndex = (paginationRequestDTO.getPage() - 1) * paginationRequestDTO.getPageSize();
            int toIndex = Math.min(fromIndex + paginationRequestDTO.getPageSize(), totalRecords);
            if (totalRecords > 0) {
                List paginatedList = genericDataDTO.getDataList().subList(fromIndex, toIndex);
                genericDataDTO.setDataList(paginatedList);
                // Set pagination information
                genericDataDTO.setTotalRecords(totalRecords);
                genericDataDTO.setPageRecords(paginatedList.size());
                genericDataDTO.setCurrentPageNumber(paginationRequestDTO.getPage());
                genericDataDTO.setTotalPages(totalPages);
                if (genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                    genericDataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                }
            }
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch In out Mc-address" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch In out Mc-address" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/getAllRemarks/{outwardId}")
    public ResponseEntity<?> getRemarksByOutwardId(@RequestBody PaginationRequestDTO paginationRequestDTO, @PathVariable Long outwardId) {

        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (outwardId == null) {
                genericDataDTO.setResponseCode(HttpStatus.BAD_REQUEST.value());
                genericDataDTO.setResponseMessage("Outward ID cannot be null");
                return ResponseEntity.badRequest().body(genericDataDTO);
            }

            GenericDataDTO response = outWordUploadService.getRemarksByOutwardId(outwardId, paginationRequestDTO);

            return ResponseEntity.ok(response);

        } catch (CustomValidationException e) {
            GenericDataDTO error = new GenericDataDTO();
            error.setResponseCode(e.getErrCode());
            error.setResponseMessage(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.valueOf(e.getErrCode()))
                    .body(error);
        }catch (IllegalArgumentException e) {
            GenericDataDTO error = new GenericDataDTO();
            error.setResponseCode(HttpStatus.BAD_REQUEST.value());
            error.setResponseMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

        } catch (Exception ex) {
            ex.printStackTrace();
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericDataDTO);
        }
    }

}
