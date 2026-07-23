package com.savbill.inventorymanagement.modules.InventoryManagement.InOutMACMapping;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.ACLMenuConstants;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.*;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.Outward;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardMapper;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardRepository;
import com.savbill.inventorymanagement.modules.InventoryManagement.Outward.OutwardServiceImpl;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@RestController
@Api(value = "InwardController", description = "REST APIs related to IN/OUT Ward mac mapping  Entity!!!!", tags = "in-out-ward-MAC-mapping-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.IN_OUT_WARD_MAC_MAPPING)
public class InOutWardMACMappingController extends ExBaseAbstractController<InOutWardMACMapingDTO> {

    @Autowired
    InOutWardMACService inOutWardMACService;

    @Autowired
    OutwardServiceImpl outwardService;

    @Autowired
    private OutwardRepository outwardRepository;

    @Autowired
    private  OutWordUploadService outWordUploadService;

    @Autowired
    private OutwardMapper outwardMapper;
    private static String MODULE = " [InOutWardMACMappingController] ";
    private static final Logger LOGGER = Logger.getLogger(InOutWardMACMappingController.class);

    public InOutWardMACMappingController(InOutWardMACService inOutWardMACService) {
        super(inOutWardMACService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[InOutWardMACMappingController]";
    }

    @Autowired
    private Tracer tracer;
    InOutWardMACMapingDTO inOutWardMACMapingDTO = new InOutWardMACMapingDTO();
    @Override
    @PostMapping("/save")
    public GenericDataDTO save(@RequestBody InOutWardMACMapingDTO dto, BindingResult result, HttpServletRequest req) throws Exception {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.CREATE_SUCCESSFUL);
            inOutWardMACService.saveEntity(dto);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create Inoutward mac"+LogConstant.LOG_BY_NAME+inOutWardMACMapingDTO.getMacAddress() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
        }catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Create Inoutward mac"+LogConstant.LOG_BY_NAME+inOutWardMACMapingDTO.getMacAddress()  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Create Inoutward mac"+LogConstant.LOG_BY_NAME+inOutWardMACMapingDTO.getMacAddress()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

        @GetMapping("/getInwardMacMapping")
    public GenericDataDTO getInwardMacMapping(@RequestParam(name = "inwardId") Long inwardId, Authentication authentication, HttpServletRequest req) {
            TraceContext traceContext =tracer.currentSpan().context();
            MDC.put("type", "Fetch");
            MDC.put("userName",getLoggedInUser().getUsername());
            MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
            MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getByInwardId(inwardId));

            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch inoutward mac by inward id : " + inwardId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        }catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch inoutward mac by inward id : " + inwardId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch inoutward mac by inward id : " + inwardId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @Transactional
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Outward.OUTWARD_ADD_MAC +"\")")
    @PostMapping("/updateMACMappingList")
    public GenericDataDTO saveMACMappingList(@RequestBody List<InOutWardMACMapping> list, Authentication authentication, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        InOutWardMACMapingDTO inOutWardMACMapingDTO = new InOutWardMACMapingDTO();

        try {
            if (list == null || list.isEmpty()) {
                throw new CustomValidationException("Input list is empty");
            }

            Outward outwardDto = outwardRepository.findById(list.get(0).getOutwardId())
                    .orElseThrow(() -> new Exception("Outward not found for ID: " + list.get(0).getOutwardId()));
            boolean hasSerial = outwardDto.getProductId().getProductCategory().isHasSerial();
            boolean hasMac = outwardDto.getProductId().getProductCategory().isHasMac();
            if (hasMac || hasSerial) {
                inOutWardMACService.validateUpdateMacMappingList(list, outwardDto, hasMac, hasSerial);
                inOutWardMACService.checkItemsForInwardOfOutward(list);
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +
                    " Update inoutward mac List " + inOutWardMACMapingDTO.getMacAddress() +
                    LogConstant.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS +
                    LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +
                    " Update inoutward mac List " + inOutWardMACMapingDTO.getMacAddress() +
                    LogConstant.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstant.LOG_STATUS + LogConstant.LOG_FAILED +
                    LogConstant.LOG_ERROR + ex.getMessage() +
                    LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +
                    " Update inoutward mac List " + inOutWardMACMapingDTO.getMacAddress() +
                    LogConstant.REQUEST_BY + getLoggedInUser().getUsername() +
                    LogConstant.LOG_STATUS + LogConstant.LOG_FAILED +
                    LogConstant.LOG_ERROR + e.getMessage() +
                    LogConstant.LOG_STATUS_CODE + HttpStatus.INTERNAL_SERVER_ERROR.value());

        } finally {
//            long endTime = System.currentTimeMillis(); // Capture end time
//            System.out.println("saveMACMappingList ended at: "+endTime);
//            System.out.println("Total execution time: " +(endTime - startTime));

            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(
            value = "/saveManualOutward/upload/{outwardId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<GenericDataDTO> uploadOutwardAsync(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long outwardId,
            @RequestParam(name = "productId") Long productId, @RequestParam(name = "ownerId") Long ownerId, @RequestParam(name = "ownerType") String ownerType) {

        GenericDataDTO response = new GenericDataDTO();

        try {
            
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Uploaded file is empty");
            }

            String fileName = file.getOriginalFilename();

            boolean isCsv = fileName != null && fileName.toLowerCase().endsWith(".csv");
            boolean isExcel = fileName != null &&
                    (fileName.toLowerCase().endsWith(".xls") || fileName.toLowerCase().endsWith(".xlsx"));

            if (!isCsv && !isExcel) {
                throw new IllegalArgumentException("Only CSV or Excel (.xls, .xlsx) files are allowed");
            }

            if (isCsv) {
                outWordUploadService.validateHeadersOnly(file, outwardId, CommonConstants.OUTWARD);
            } else {
                outWordUploadService.validateExcelHeadersOnly(file, outwardId, CommonConstants.OUTWARD);
            }

            GenericDataDTO result = outWordUploadService.uploadOutwardFile(file, outwardId, productId, ownerId, ownerType);

//            response.setResponseCode(HttpStatus.OK.value());
//            response.setResponseMessage("Upload Successful");
//            response.setData(Collections.singletonList(result));

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setResponseMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);

        }catch (CustomValidationException e) {
            e.printStackTrace();
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setResponseMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Error processing file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/getAllMACMappingByInwardId")
    public GenericDataDTO getAllMACMappingByInwardId(@RequestParam(name = "inward_id") Long inwardId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getAllMACMappingByInwardId(inwardId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All MAC Mapping By Inward Id : " + inwardId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All MAC Mapping By Inward Id : " + inwardId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @GetMapping("/getAllMACByExstingMacType")
    public GenericDataDTO getAllMACByExstingMacType(@RequestParam(name = "inward_id") Long inwardId,@RequestParam(name="inOutMappingId") Long inOutMappingId,@RequestParam(name="inventoryType")String inventoryType, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getAllMACByExisitingMacType(inwardId,inOutMappingId,inventoryType));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All MAC By Existing Mac Type By InwardId : "+ inwardId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR+ "Fetch All MAC By Existing Mac Type By InwardId : "+ inwardId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getAllMACMappingByExternalId")
    public GenericDataDTO getAllMACMappingByExternalId(@RequestParam(name = "external_id") Long externalId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getAllMACMappingByExternalId(externalId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Mac Mapping By External Id : " +externalId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
        } catch (Exception ex) {

            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR+ "Fetch All Mac Mapping By External Id : " +externalId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @PostMapping("/saveMACMappingCustomer")
//    public GenericDataDTO saveMACMappingCustomer(@RequestBody List<CustMacMappping> custMacMappingList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            inOutWardMACService.saveMACMappingCustomer(custMacMappingList);
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

    @GetMapping("/deleteMacMapInCustomer")
    public GenericDataDTO deleteMacMappInCustomer(@RequestParam(name = "customerId") Integer customerId, @RequestParam(name = "macAddress") String macAddress, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            inOutWardMACService.deleteMacMapInCustomer(customerId, macAddress);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Mac Map In Customer by id : "+customerId+LogConstant.LOG_BY_NAME+macAddress + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Mac Map In Customer by id : "+customerId +LogConstant.LOG_BY_NAME+macAddress+LogConstant.LOG_BY_NAME + macAddress + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @GetMapping("/deletemac")
    public GenericDataDTO deqletemac(@RequestParam(name = "itemId") Long itemId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            inOutWardMACService.deleteMac(itemId);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Mac By"+LogConstant.LOG_BY_NAME+inOutWardMACMapingDTO.getMacAddress()+" And Item Id : " +itemId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +  "Delete Mac By"+LogConstant.LOG_BY_NAME+inOutWardMACMapingDTO.getMacAddress()+" And Item Id : " +itemId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }





    @GetMapping("/removeMappingWithCustomerInventory")
    public GenericDataDTO removeMappingWithCustomerInventory(@RequestParam(name = "mappingId") Long mappingId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setData(inOutWardMACService.removeMappingWithCustomerInventory(mappingId, null));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Remove Mapping WIth Customer Inventory by Id : "+mappingId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Remove Mapping WIth Customer Inventory by Id : "+mappingId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

//    @GetMapping("/removeInventory")
//    public GenericDataDTO removeInventory(@RequestParam(name = "macMappingId") Long mappingId, @RequestParam(name = "customerInventoryId") Long customerInventoryId,@RequestParam(name = "customerId") Long customerId, @RequestParam(name = "isflag") boolean isflag,@RequestParam("remark")String remark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            inOutWardMACService.removeInventory(mappingId, customerInventoryId,customerId,isflag,remark);
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.INVENTORY_PRE_CUSTOMER_ASSIGN.PRE_CUST_INVENTORY_REMOVE + "\",\""+ ACLMenuConstants.INVENTORY_POST_CUSTOMER_ASSIGN.POST_CUST_INVENTORY_REMOVE + "\")")
    @GetMapping("/removeInventory")
    public GenericDataDTO removeInventory(@RequestParam(name = "macMappingId") Long mappingId, @RequestParam(name = "customerInventoryId") Long customerInventoryId,@RequestParam(name = "customerId") Long customerId,@RequestParam(name="nextstaff")Integer nextstaff,@RequestParam("remark")String remark,@RequestParam("isApprove") boolean isApprove, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Remove Inventory "+LogConstant.LOG_BY_NAME+inOutWardMACMapingDTO.getMacAddress()+"by Mapping Id : " +mappingId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
            return  inOutWardMACService.removeInventoryWorkFlowNew(mappingId,customerInventoryId,customerId, nextstaff, remark,isApprove);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +  "Remove Inventory "+LogConstant.LOG_BY_NAME+inOutWardMACMapingDTO.getMacAddress()+"by Mapping Id : " +mappingId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            return genericDataDTO;
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @GetMapping("/generateRemoveInventoryRequest")
    public GenericDataDTO generateRemoveInventoryRequest(@RequestParam(name = "macMappingId") Long mappingId, @RequestParam(name = "customerInventoryId") Long customerInventoryId,@RequestParam(name = "customerId") Long customerId, @RequestParam(name = "isflag") boolean isflag,@RequestParam(name="revisedcharge", required = false)Long revisedcharge, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Generate Remove Inventory Request By Id : "+ mappingId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
            return  inOutWardMACService.genearateRemoveInventoryRequest(mappingId,customerInventoryId,customerId, isflag, revisedcharge);

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Generate Remove Inventory Request By Id : " +mappingId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            return genericDataDTO;
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }


//    @GetMapping("/removeInventory")
//    public GenericDataDTO removeInventory(@RequestParam(name = "macMappingId") Long mappingId, @RequestParam(name = "customerInventoryId") Long customerInventoryId,@RequestParam(name = "customerId") Long customerId, @RequestParam(name = "isflag") boolean isflag,@RequestParam("remark")String remark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            inOutWardMACService.removeInventory(mappingId, customerInventoryId,customerId,isflag,remark,isApproveRequest);
//            //genericDataDTO.setDataList(inOutWardMACService.getAllAssemblyInventory(assemblyId));
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }
//


    @GetMapping("/getAllAssemblyInventory")
    public GenericDataDTO getAllAssemblyInventory(@RequestParam(name = "assemblyId") Long assemblyId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getAllAssemblyInventory(assemblyId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Assembly Inventory By Id : "+ assemblyId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Assembly InventoryBy Id : "+ assemblyId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getbyinwardid")
    public GenericDataDTO getbyinwardid(@RequestParam(name = "inwardId") Long inwardId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.findbyinwardid(inwardId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch inOutWardMac By Inward Id : " +inwardId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch inOutWardMac By Inward Id : " +inwardId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/inwardOfOutwardId")
    public GenericDataDTO getByinwardOfOutwardId(@RequestParam(name = "inwardId") Long inwardOfOutwardId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.findbyinwardOfOutwardId(inwardOfOutwardId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch inOutWardMac By inwardOfOutward Id : " +inwardOfOutwardId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch inOutWardMac By inwardOfOutward Id : " +inwardOfOutwardId+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getbyoutwardid")
    public GenericDataDTO getbyoutwardid(@RequestParam(name = "id") Long id, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.findbyoutwardid(id));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch inOutWardMac By outward Id : " +id+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch inOutWardMac By outward Id : " +id+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/removeInventoryfromowner")
    public GenericDataDTO removeInventoryfromowner(@RequestParam(name = "macMappingId") Long mappingId,@RequestParam("isflag") boolean isflag, HttpServletRequest req){
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            inOutWardMACService.removeInventoryfrompop(mappingId,isflag,null);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Remove Inventory From Owner"+LogConstant.LOG_BY_NAME+inOutWardMACMapingDTO.getMacAddress()+" By MappingId : "+mappingId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Remove Inventory From Owner"+LogConstant.LOG_BY_NAME+inOutWardMACMapingDTO.getMacAddress()+" By MappingId : "+mappingId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
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


}


