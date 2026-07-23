package com.savbill.inventorymanagement.modules.InventoryManagement.SpecificationParameters;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
//import liquibase.pro.packaged.A;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL +UrlConstants.SPECIFICATION_PARAMETERS)
public class SpecificParametersController {
    @Autowired
    SpecificationParametersService specificationParametersService;
    @Autowired
    Tracer tracer;
    private static final Logger LOGGER = Logger.getLogger(SpecificParametersController.class);

    @GetMapping("/getSpecificParametersByid")
    public GenericDataDTO getSpecificParametersByid(@Valid @RequestParam Long product_id, HttpServletRequest req){
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
    try {
        LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Specific Parameter By Id : "+product_id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
    }catch (Exception ex){
        LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Specific Parameter By Id : "+product_id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  +APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+ HttpStatus.NOT_ACCEPTABLE.value());
    }finally {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
    }

        return specificationParametersService.getSpecificParametersByid(product_id);
    }

    @GetMapping("/getSpecificParametersByProductCategoryId")
    public GenericDataDTO getSpecificParametersByProductCategoryId(@Valid @RequestParam Long product_category_id, HttpServletRequest req){
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Specific Parameter By Id : "+product_category_id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );
        }catch (Exception ex){
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Specific Parameter By Id : "+product_category_id + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  +APIConstants.ERROR_MESSAGE + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+ HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return specificationParametersService.getSpecificParametersByProductCategoryId(product_category_id);

    }

    @PostMapping("/customerParam")
    public GenericDataDTO findAllPartnerByCustId(@RequestBody SpecificationParametersDTO parametersDTO) {
        return specificationParametersService.getSpecificParametersByCustId(parametersDTO.getCustId(), parametersDTO.getConnectionNo(),parametersDTO.getCustInvMap());
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
