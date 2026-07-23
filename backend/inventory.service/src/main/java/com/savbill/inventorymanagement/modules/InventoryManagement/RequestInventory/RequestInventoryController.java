package com.savbill.inventorymanagement.modules.InventoryManagement.RequestInventory;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.ACLMenuConstants;
import com.savbill.inventorymanagement.core.constants.CommonConstants;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.REQUEST_INVENTORY)
@Api(value = "RequestInventoryController", description = "REST APIs related to RequestInventory Entity!!!!", tags = "RequestInventory_Controller")

public class RequestInventoryController extends ExBaseAbstractController<RequestInventoryDto> {

    @Autowired
    RequestInventoryServiceImpl requestInventoryService;

    @Autowired
    Tracer tracer;
    private static Logger LOGGER = Logger.getLogger(RequestInventoryController.class);
    /**
     * Create API for Request Inventory
     * @Author Darshan
     * @param requestInventoryDto
     * @param result
     * @param req
     * @return
     * @throws Exception
     */
    @Override
    @PostMapping("/save")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Request.INVENTORY_RAISED_REQUEST +"\")")
    public GenericDataDTO save(@Valid @RequestBody RequestInventoryDto requestInventoryDto, BindingResult result, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(MessageConstants.CREATE_SUCCESSFUL);
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        if (getMvnoIdFromCurrentStaff() != null) {
            requestInventoryDto.setMvnoId(getMvnoIdFromCurrentStaff());
        }
        try {
            requestInventoryService.validateRequest(requestInventoryDto);
            RequestInventoryDto inventoryDto = requestInventoryService.saveEntity(requestInventoryDto);
            genericDataDTO.setData(inventoryDto);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create inventory request"+LogConstant.LOG_BY_NAME+requestInventoryDto.getRequestInventoryName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create inventory request" +LogConstant.LOG_BY_NAME+requestInventoryDto.getRequestInventoryName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  +APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Create inventory request" +LogConstant.LOG_BY_NAME+requestInventoryDto.getRequestInventoryName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  +APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getById")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Request.INVENTORY_REQUEST +"\")")
    public GenericDataDTO findById(@RequestParam("id") Long id, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        try {
            RequestInventoryDto requestInventoryDto = requestInventoryService.findById(id);
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(requestInventoryDto);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch inventory request" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch inventory request" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE+ ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());


        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REQUEST_INVENTORY_ADD)
    @GetMapping("/approveStatus")
    public GenericDataDTO approvreStatus(@RequestParam("status") String status, @RequestParam("id") Long id, @RequestParam("remarks") String remarks, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());

        try {
            if(status.equalsIgnoreCase(CommonConstants.APPROVE)) {
                requestInventoryService.validateApproveRequest(id);
            }
            RequestInventoryDto requestInventoryDto = requestInventoryService.approveStatus(status, id, remarks);
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(requestInventoryDto);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Approv request" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Approv request" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE+ ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REQUEST_INVENTORY_ADD)
    @GetMapping("/onbehalfoff")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_VIEW + "\")")
    public GenericDataDTO getAll(@RequestParam("onBehalfOf") String onBehalfOf, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO = requestInventoryService.getAll(onBehalfOf);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All request" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All request" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
            throw new RuntimeException(ex.getMessage());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/getAllAssignedRequestInventory")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Request.INVENTORY_ASSIGNED_REQUEST +"\")")
    public GenericDataDTO getAllAssignedRequestInventory(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO = requestInventoryService.getAllAssignedRequestInventory(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All Inventory request" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "fetch All Inventory request : " + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  +APIConstants.ERROR_MESSAGE+ ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }


    @PostMapping("/getAllByCurrentStaff")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Request.INVENTORY_REQUEST +"\")")
    public GenericDataDTO getAllRequestByCurrentStaff(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO = requestInventoryService.getAllRequestByCurrentStaff(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortBy(), requestDTO.getSortOrder());
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Request By Current Staff" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All Request By Current Staff" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  +APIConstants.ERROR_MESSAGE+ ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

            throw new RuntimeException(ex.getMessage());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getAllWareHouses")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_VIEW + "\")")
    public GenericDataDTO getAllWareHouse(HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setDataList(requestInventoryService.getAllWareHouse());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All WareHouse" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch All  WareHouse" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE+ ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REQUEST_INVENTORY_DELETE)
    @DeleteMapping("/delete")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVENTORY_REQUEST_ALL + "\",\"" + AclConstants.OPERATION_INVENTORY_REQUEST_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Request.INVENTORY_REQUEST_DELETE +"\")")
    public GenericDataDTO delete(@RequestParam("id") Long id, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        RequestInventoryDto requestInventoryDto = new RequestInventoryDto();
        try{
            {
                dataDTO.setData(requestInventoryService.deleteInventory(id));
                dataDTO.setResponseMessage(MessageConstants.DELETE_SUCCESSFUL);
                dataDTO.setResponseCode(HttpStatus.OK.value());
                LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Inventory Request"+LogConstant.LOG_BY_NAME+requestInventoryDto.getRequestInventoryName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
            }
        }catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete Inventory Request"+LogConstant.LOG_BY_NAME+requestInventoryDto.getRequestInventoryName()+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + APIConstants.ERROR_MESSAGE+ ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;

    }

    public RequestInventoryController(RequestInventoryServiceImpl service) {
        super(service);
    }


    @Override
    public String getModuleNameForLog() {
        return null;
    }


    @PostMapping("/forwardReqInv")
    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Inventory_Request.INVENTORY_ASSIGNED_REQUEST_FORWARD + "\")")
    public GenericDataDTO forwardRequestToWareHouse(@RequestParam("reqId") Long reqId, @RequestParam("forwardToReqId") Long forwardToReqId, @RequestParam("remarks") String remarks, HttpServletRequest req){

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        RequestInventoryDto requestInventoryDto = new RequestInventoryDto();
        try {
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(requestInventoryService.forwardRequestToWareHouse(reqId,forwardToReqId,remarks));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update for WareHouse"+LogConstant.LOG_BY_NAME+requestInventoryDto.getRequestInventoryName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update for WareHouse"+LogConstant.LOG_BY_NAME+requestInventoryDto.getRequestInventoryName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //For get the user First Name
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
