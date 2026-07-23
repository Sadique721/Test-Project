package com.savbill.salescrmsbss.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.savbill.salescrmsbss.AuditLog.entity.PaginationDetails;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import org.apache.log4j.Logger;
import brave.Tracer;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.salescrmsbss.utils.SalesCrmsConstants;

import io.swagger.annotations.Api;


@Api(value = "Common Controller" , description = "REST API to check Common Controller!!!!",tags = "Common Controller")
@RestController
@RequestMapping("api/v1/SavbillSalesCrmsBss")
public class APIResponseController {
	@Autowired
	Tracer tracer;
	private final Logger LOGGER = Logger.getLogger(APIResponseController.class);

	public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, Map<String, Object> response, Page page) {
		try {
			response.put("timestamp",
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
			response.put("status", responseCode);
			if (null != page) {
				response.put("pageDetails", setPaginationDetails(page));
			}

			if(response.get(SalesCrmsConstants.ERROR_MESSAGE) != null)
			{
				String errorMsg = response.get(SalesCrmsConstants.ERROR_MESSAGE).toString().replace(SalesCrmsConstants.NOT_FOUND, "");
				response.put(SalesCrmsConstants.ERROR_MESSAGE, errorMsg);
			}
			if (responseCode.equals(SalesCrmsConstants.SUCCESS)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			} else if (responseCode.equals(HttpStatus.EXPECTATION_FAILED.value())) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.EXPECTATION_FAILED);
			} else if (responseCode.equals(SalesCrmsConstants.FAIL)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			} else if (responseCode.equals(SalesCrmsConstants.INTERNAL_SERVER_ERROR)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			LOGGER.error("Error while performing operation", e);
			if (response == null) {
				response = new HashMap<>();
			}
			response.put("status", SalesCrmsConstants.INTERNAL_SERVER_ERROR);
			response.put(SalesCrmsConstants.ERROR_TAG, e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, Map<String, Object> response) {
		return apiResponse(responseCode, response, null);
	}

	public void buildErrorMessageForResponse(Map<String, Object> response, Throwable e) 
	{
		String errorMsg = e.getMessage();
		if(errorMsg.contains(SalesCrmsConstants.NOT_PUT_IN_QUEUE))
		{
			errorMsg = errorMsg.replace(SalesCrmsConstants.NOT_PUT_IN_QUEUE, "");
		}
		if (errorMsg.contains(SalesCrmsConstants.BASIC_STRING_MSG)) 
		{
			errorMsg = errorMsg.replace(SalesCrmsConstants.BASIC_STRING_MSG, "");
			response.put(SalesCrmsConstants.VALIDATION_REASON, SalesCrmsConstants.VALIDATION_REASON_BASIC_STRING_MESSAGE);
			response.put(SalesCrmsConstants.ERROR_MESSAGE, errorMsg);
		}
		else if (errorMsg.contains(SalesCrmsConstants.BASIC_NUMERIC_MSG))
		{
			errorMsg = errorMsg.replace(SalesCrmsConstants.BASIC_NUMERIC_MSG, "");
			response.put(SalesCrmsConstants.VALIDATION_REASON, SalesCrmsConstants.VALIDATION_REASON_BASIC_NUMERIC_MESSAGE);
			response.put(SalesCrmsConstants.ERROR_MESSAGE, errorMsg);
		}
		else
		{
			response.put(SalesCrmsConstants.ERROR_MESSAGE, errorMsg);
		}
	}
	@ApiOperation(value = "Used to check whether SalseCRM service is up or not.")
	@GetMapping("/serviceStatus")
	public String checkServiceStatus() {
		try {
			return "{\"success\": true,\"message\": \"SalseCRM Service is Up.\"}";
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
//For get the logged in user first name
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

	public PaginationDetails setPaginationDetails(Page page) {
		PaginationDetails pageDetails = new PaginationDetails();
		pageDetails.setTotalPages(page.getTotalPages());
		pageDetails.setTotalRecords(page.getTotalElements());
		pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
		pageDetails.setCurrentPageNumber(page.getNumber() + 1);
		return pageDetails;
	}

}
