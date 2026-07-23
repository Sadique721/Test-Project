package com.savbill.radius.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.savbill.radius.utils.APIConstants;
import com.savbill.radius.utils.PaginationDetails;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.savbill.radius.utils.RadiusConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Service Status" , description = "REST API to check service status!!!!",tags = "Status")
@RestController
@RequestMapping("/SavbillRadius")
public class APIResponseController {
	
	private static final Logger log = LoggerFactory.getLogger(APIResponseController.class);
	public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, Map<String, Object> response) {
		
		try {
			
			//log.info(String.format("%s", new ObjectMapper().writeValueAsString(response)));
			
			response.put("timestamp",
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
			response.put("status", responseCode);
			if(response.get(RadiusConstants.ERROR_MESSAGE) != null)
			{
				String errorMsg = response.get(RadiusConstants.ERROR_MESSAGE).toString().replace(RadiusConstants.NOT_FOUND, "");
				response.put(RadiusConstants.ERROR_MESSAGE, errorMsg);
			}
			if (responseCode.equals(RadiusConstants.SUCCESS)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			}else if (responseCode.equals(RadiusConstants.NO_CONTENT_FOUND)) {
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			} else if (responseCode.equals(RadiusConstants.FAIL)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			} else if (responseCode.equals(RadiusConstants.INTERNAL_SERVER_ERROR)) {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			
			log.error("Error while performing operation", e);
			
			if (response == null) {
				response = new HashMap<>();
			}
			
			response.put("status", RadiusConstants.INTERNAL_SERVER_ERROR);
			response.put(RadiusConstants.ERROR_TAG, e.getMessage());
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}

	public void buildErrorMessageForResponse(Map<String, Object> response, Throwable e)
	{
		String errorMsg = e.getMessage();
		if(errorMsg.contains(RadiusConstants.NOT_PUT_IN_QUEUE))
		{
			errorMsg = errorMsg.replace(RadiusConstants.NOT_PUT_IN_QUEUE, "");
		}
		if (errorMsg.contains(RadiusConstants.BASIC_STRING_MSG)) 
		{
			errorMsg = errorMsg.replace(RadiusConstants.BASIC_STRING_MSG, "");
			response.put(RadiusConstants.VALIDATION_REASON, RadiusConstants.VALIDATION_REASON_BASIC_STRING_MESSAGE);
			response.put(RadiusConstants.ERROR_MESSAGE, errorMsg);
		}
		else if (errorMsg.contains(RadiusConstants.BASIC_NUMERIC_MSG))
		{
			errorMsg = errorMsg.replace(RadiusConstants.BASIC_NUMERIC_MSG, "");
			response.put(RadiusConstants.VALIDATION_REASON, RadiusConstants.VALIDATION_REASON_BASIC_NUMERIC_MESSAGE);
			response.put(RadiusConstants.ERROR_MESSAGE, errorMsg);
		}
		else
		{
			response.put(RadiusConstants.ERROR_MESSAGE, errorMsg);
		}
	}

//	@PostMapping("/welcome")
//	public String showWelcomePage() {
//		return "Welcome To 'SavbillRadius'" + "<br>" + "User is authenticated and successfully logged in." + "<br>"
//				+ "You can access api by providing proper and correct url.";
//	}

	@ApiOperation(value = "Used to check whether radius service is up or not.")
	@GetMapping("/serviceStatus")
	public String checkServiceStatus() {
		try {
			log.debug("Radius Service is Up");
			return "{\"success\": true,\"message\": \"Radius Service is Up.\"}";
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
		try {
			response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
			response.put("status", responseCode);

			if (null != page) {
				response.put("pageDetails", setPaginationDetails(page));
			}

			if (responseCode.equals(APIConstants.SUCCESS)) {
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else if (responseCode.equals(APIConstants.FAIL)) {
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			} else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
				return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			} else if (responseCode.equals(APIConstants.NOT_FOUND)) {
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			} else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			} else {
				return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			if (response == null) {
				response = new HashMap<>();
			}
			response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
			response.put(APIConstants.ERROR_TAG, e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
