package com.savbill.inventorymanagement.core.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.Constants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.modules.InventoryDashboard.DashboardController;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;

@Api(value = "Service Status" , description = "REST API to check service status!!!!",tags = "Status")
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL)
public class APIResponseController {
	@Autowired
	private Tracer tracer;
	private static final Logger LOGGER = Logger.getLogger(DashboardController.class);
	public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, Map<String, Object> response) {
		TraceContext traceContext =tracer.currentSpan().context();
		MDC.put("type", "Fetch");
		MDC.put("userName", getLoggedInUser().getUsername());
		MDC.put("traceId", traceContext.traceIdString());
		MDC.put("spanId", traceContext.spanIdString());
		try {
			LOGGER.info(LogConstant.REQUEST_FROM  + LogConstant.REQUEST_FOR + " REST API to check service status is Success : "  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE+ APIConstants.SUCCESS);
			response.put("timestamp",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
			response.put("status", responseCode);
			if(response.get(Constants.ERROR_MESSAGE) != null)
			{
				String errorMsg = response.get(Constants.ERROR_MESSAGE).toString().replace(Constants.NOT_FOUND, "");
				response.put(Constants.ERROR_MESSAGE, errorMsg);
			}
			if (responseCode.equals(Constants.SUCCESS)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			} else if (responseCode.equals(Constants.FAIL)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			} else if (responseCode.equals(Constants.INTERNAL_SERVER_ERROR)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			LOGGER.error(LogConstant.REQUEST_FROM + LogConstant.REQUEST_FOR + " Unable to API to check service status : " + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
			if (response == null) {
				response = new HashMap<>();
			}
			response.put("status", Constants.INTERNAL_SERVER_ERROR);
			response.put(Constants.ERROR_TAG, e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
	}

	public void buildErrorMessageForResponse(Map<String, Object> response, Throwable e)
	{
		String errorMsg = e.getMessage();
		if(errorMsg.contains(Constants.NOT_PUT_IN_QUEUE))
		{
			errorMsg = errorMsg.replace(Constants.NOT_PUT_IN_QUEUE, "");
		}
		if (errorMsg.contains(Constants.BASIC_STRING_MSG)) 
		{
			errorMsg = errorMsg.replace(Constants.BASIC_STRING_MSG, "");
			response.put(Constants.VALIDATION_REASON, Constants.VALIDATION_REASON_BASIC_STRING_MESSAGE);
			response.put(Constants.ERROR_MESSAGE, errorMsg);
		}
		else if (errorMsg.contains(Constants.BASIC_NUMERIC_MSG))
		{
			errorMsg = errorMsg.replace(Constants.BASIC_NUMERIC_MSG, "");
			response.put(Constants.VALIDATION_REASON, Constants.VALIDATION_REASON_BASIC_NUMERIC_MESSAGE);
			response.put(Constants.ERROR_MESSAGE, errorMsg);
		}
		else
		{
			response.put(Constants.ERROR_MESSAGE, errorMsg);
		}
	}

//	@PostMapping("/welcome")
//	public String showWelcomePage() {
//		return "Welcome To 'SavbillRadius'" + "<br>" + "User is authenticated and successfully logged in." + "<br>"
//				+ "You can access api by providing proper and correct url.";
//	}

	@ApiOperation(value = "Used to check whether inventory management service is up or not.")
	@GetMapping("/serviceStatus")
	public String checkServiceStatus(HttpServletRequest req) {
		TraceContext traceContext =tracer.currentSpan().context();
		MDC.put("type", "Fetch");
		MDC.put("userName", getLoggedInUser().getUsername());
		MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
		MDC.put("spanId", traceContext.spanIdString());

		try {
			LOGGER.info(LogConstant.REQUEST_FROM +req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Inventory Management Service Status is Up"  + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE+ APIConstants.SUCCESS);
			return "{\"success\": true,\"message\": \"Inventory Management Service is Up.\"}";
		} catch (Exception e) {
			LOGGER.error(LogConstant.REQUEST_FROM +req.getHeader("requestFrom")+ LogConstant.REQUEST_FOR + "Error While Inventory Management Up" + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());
			throw new RuntimeException(e.getMessage());
		}finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
	}

//for user first name getting from abstract class
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
