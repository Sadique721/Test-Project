package com.savbill.taskmanagement.core.controller;


import com.savbill.taskmanagement.core.constants.Constants;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Api(value = "Service Status" , description = "REST API to check service status!!!!",tags = "Status")
@RestController
@RequestMapping("/Savbilltest")
public class APIResponseController {

	Logger log = LoggerFactory.getLogger(APIResponseController.class);
	public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, Map<String, Object> response) {

		try {

			//log.info(String.format("%s", new ObjectMapper().writeValueAsString(response)));

			response.put("timestamp",
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
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

			log.error("Error while performing operation", e);

			if (response == null) {
				response = new HashMap<>();
			}

			response.put("status", Constants.INTERNAL_SERVER_ERROR);
			response.put(Constants.ERROR_TAG, e.getMessage());

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

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


}
