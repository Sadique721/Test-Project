package com.savbill.inventorymanagement.modules.InventoryDashboard;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.ACLMenuConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;

import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import org.apache.log4j.Logger;

import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController(value = "dashboardRestController")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.DASHBOARD)
public class DashboardController  {
    @Autowired
    private Tracer tracer;

    private static final String MODULE = " [DashboardController] ";
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class);
    @Autowired
    DashboardServiceImpl dashboardService;

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Dashboards.DASHBOARD_INVENTORY +"\")")
    @PostMapping(value = "/inventory/getProductQtyByStaff")
    public GenericDataDTO getProductQtyByStaff(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req , @RequestParam(name = "mvnoId", required = false) Integer mvnoId) {
       String SUBMODULE = MODULE + " [getProductCategoryByOwnerIdAndOwnerType()] ";
       GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch product quality by staff"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE+ APIConstants.SUCCESS);
            return dashboardService.getProductQtyByStaff(paginationRequestDTO, mvnoId);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch product quality by staff"+ LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

       return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + ACLMenuConstants.Dashboards.DASHBOARD_INVENTORY +"\")")
    @PostMapping(value = "/inventory/getProductQtyByWarehouse")
    public GenericDataDTO getProductQtyByWarehouse(@RequestBody PaginationRequestDTO paginationRequestDTO, @RequestParam(name = "mvnoId", required = false) Integer mvnoId, HttpServletRequest req) {
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getProductCategoryByOwnerIdAndOwnerType()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch product quality by WareHouse" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
            return dashboardService.getProductQtyByWarehouse(paginationRequestDTO, mvnoId);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +"Fetch product quality by WareHouse" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//For get the loggen in user first name
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
