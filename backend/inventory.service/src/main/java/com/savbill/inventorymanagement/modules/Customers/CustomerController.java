package com.savbill.inventorymanagement.modules.Customers;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
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
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.CUSTOMER)
public class CustomerController extends ExBaseAbstractController<CustomersPojo> {
    public CustomerController(CustomerService service) {
        super(service);
    }

    private static final Logger LOGGER = Logger.getLogger(CustomerController.class);

    @Override
    public String getModuleNameForLog() {
        return "[CustomerController]";
    }

    @Autowired
    CustomerService customerService;

    @Autowired
    Tracer tracer;

    @GetMapping("/getCustNetworkDetail")
    public GenericDataDTO getCustNetworkDetail(@Valid @RequestParam(name = "customerId") Integer customerId, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, request.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO = customerService.getCustNetworkDetail(customerId);
            LOGGER.info(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Get Customer Network Details" + LogConstant.LOG_BY_NAME + customerId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Get Customer Network Details" + LogConstant.LOG_BY_NAME + customerId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/saveCustomerNetworkBind")
    public GenericDataDTO saveCustomerNetworkBind(@RequestBody CustomerNetworkBindDTO customerNetworkBindDTO, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, request.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            customerService.validateCustomerNetworkBindDetails(customerNetworkBindDTO);
            CustomerNetworkBind customerNetworkBind = customerService.saveCustomerNetworkBindDetails(customerNetworkBindDTO);
            genericDataDTO.setData(customerNetworkBind);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Save Customer Network Details" + LogConstant.LOG_BY_NAME + customerNetworkBindDTO.getCustomerid() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Save Customer Network Details" + LogConstant.LOG_BY_NAME + customerNetworkBindDTO.getCustomerid() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Save Customer Network Details" + LogConstant.LOG_BY_NAME + customerNetworkBindDTO.getCustomerid() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PutMapping("/updateCustomerNetworkBind")
    public GenericDataDTO updateCustomerNetworkBind(@RequestBody CustomerNetworkBindDTO customerNetworkBindDTO, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, request.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            customerService.validateCustomerNetworkBindDetails(customerNetworkBindDTO);
            CustomerNetworkBind updatedCustomerNetworkBind = customerService.updateCustomerNetworkBindDetails(customerNetworkBindDTO);
            genericDataDTO.setData(updatedCustomerNetworkBind);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Customer Network Details" + LogConstant.LOG_BY_NAME + customerNetworkBindDTO.getCustomerid() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Customer Network Details" + LogConstant.LOG_BY_NAME + customerNetworkBindDTO.getCustomerid() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Update Customer Network Details" + LogConstant.LOG_BY_NAME + customerNetworkBindDTO.getCustomerid() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/getCustomerNetworkBindByCustId")
    public GenericDataDTO getCustomerNetworkBindByCustId(@RequestParam Long custId,HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, request.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            CustomerNetworkBindDTO customerNetworkBindDetails = customerService.getCustomerNetworkBindDetailsByCustId(custId);
            genericDataDTO.setData(customerNetworkBindDetails);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Customer Network Details" + LogConstant.LOG_BY_NAME + custId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Customer Network Details" + LogConstant.LOG_BY_NAME + custId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Customer Network Details" + LogConstant.LOG_BY_NAME + custId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstant.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED.value());
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
}
