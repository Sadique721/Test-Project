package com.savbill.integrationsystem.core.controller;


import com.savbill.integrationsystem.core.dto.GenericSearchModel;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.dto.ValidationData;
import com.savbill.integrationsystem.core.security.constants.Constants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.nms.entity.PaginationDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Service Status" , description = "REST API to check service status!!!!",tags = "Status")
public class APIResponseController {

    Logger log = LoggerFactory.getLogger(APIResponseController.class);

    public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, Map<String, Object> response , Page page) {

        try {

            //log.info(String.format("%s", new ObjectMapper().writeValueAsString(response)));

            response.put("timestamp",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }
            if(response.get(Constants.ERROR_MESSAGE) != null) {
                String errorMsg = response.get(Constants.ERROR_MESSAGE).toString().replace(Constants.NOT_FOUND, "");
                response.put(Constants.ERROR_MESSAGE, errorMsg);
            }
            if (responseCode.equals(Constants.SUCCESS)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            } else if (responseCode.equals(Constants.FAIL)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(Constants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.NO_CONTENT_FOUND)) {
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
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

    public ResponseEntity<Map<String, Object>> apiResponse(Integer responseCode, HashMap<String, Object> response){
        return  apiResponse(responseCode,response,null);
    }

    public Integer MAX_PAGE_SIZE = 5;
    public Integer PAGE = 1;
    public Integer PAGE_SIZE = 5;
    public Integer SORT_ORDER = 0;
    public String SORT_BY;

    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        this.PAGE = 1;//Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        this.PAGE_SIZE = 5; //Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        this.SORT_BY = "id";//clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        this.SORT_ORDER = 0; //Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
        this.MAX_PAGE_SIZE = 100; //Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        if (null == requestDTO.getPage())
            requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize())
            requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy())
            requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder())
            requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }

    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }

    public ValidationData validateSearchCriteria(List<GenericSearchModel> filterList) {
        ValidationData validationData = new ValidationData();
        if (null == filterList || 0 < filterList.size()) {
            validationData.setValid(false);
            validationData.setMessage("Please Provide Search Criteria");
            return validationData;
        }
        validationData.setValid(true);
        return validationData;
    }

    public void buildErrorMessageForResponse(Map<String, Object> response, Throwable e) {
        String errorMsg = e.getMessage();
        if(errorMsg.contains(Constants.NOT_PUT_IN_QUEUE)) {
            errorMsg = errorMsg.replace(Constants.NOT_PUT_IN_QUEUE, "");
        }
        if (errorMsg.contains(Constants.BASIC_STRING_MSG)) {
            errorMsg = errorMsg.replace(Constants.BASIC_STRING_MSG, "");
            response.put(Constants.VALIDATION_REASON, Constants.VALIDATION_REASON_BASIC_STRING_MESSAGE);
            response.put(Constants.ERROR_MESSAGE, errorMsg);
        } else if (errorMsg.contains(Constants.BASIC_NUMERIC_MSG)) {
            errorMsg = errorMsg.replace(Constants.BASIC_NUMERIC_MSG, "");
            response.put(Constants.VALIDATION_REASON, Constants.VALIDATION_REASON_BASIC_NUMERIC_MESSAGE);
            response.put(Constants.ERROR_MESSAGE, errorMsg);
        } else {
            response.put(Constants.ERROR_MESSAGE, errorMsg);
        }
    }

//	@PostMapping("/welcome")
//	public String showWelcomePage() {
//		return "Welcome To 'SavbillRadius'" + "<br>" + "User is authenticated and successfully logged in." + "<br>"
//				+ "You can access api by providing proper and correct url.";
//	}

    @ApiOperation(value = "Used to check whether Integration service is up or not.")
    @GetMapping("/serviceStatus")
    public String checkServiceStatus() {
        try {
            log.debug("Common API GateWay Service is Up");
            return "{\"success\": true,\"message\": \"Integration  Service is Up.\"}";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
