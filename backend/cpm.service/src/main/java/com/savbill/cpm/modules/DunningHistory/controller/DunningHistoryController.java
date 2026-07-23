package com.savbill.cpm.modules.DunningHistory.controller;


import com.savbill.cpm.constants.MenuConstants;
import com.savbill.cpm.core.dto.PaginationRequestDTO;
import com.savbill.cpm.modules.DunningHistory.domain.DunningHistory;
import com.savbill.cpm.modules.DunningHistory.service.DunningHistoryService;
import com.savbill.cpm.modules.Voucher.module.APIResponseController;
import com.savbill.cpm.utils.APIConstants;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/cpm/dunnninghistory")
public class DunningHistoryController {

   @Autowired
   private DunningHistoryService dunningHistoryService;

   @Autowired
   private APIResponseController apiResponseController;

    private static final Logger logger = LoggerFactory.getLogger(DunningHistoryController.class);


    @ApiOperation(value = "Get list of  all dunning history")
    @PostMapping("/findAll")
    //@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<?> findAllDunningHistory(@RequestBody PaginationRequestDTO requestDTO) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            Page<DunningHistory> getAllCustomerDunningHistory = dunningHistoryService.findAllDunningHistory(requestDTO);
            Integer responseCode = APIConstants.SUCCESS;
            response.put("customerDunningHistory", getAllCustomerDunningHistory);
            logger.debug("All dunning History fetch successfully");
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            logger.error("Error while fetch DunningHistory: " + e.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @ApiOperation(value = "Get list of  all dunning history")
    @PostMapping("/findByPartnerOrCustomerId")
    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_DUNNUNG_MANAGEMENT + "\",\""
            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_DUNNUNG_MANAGEMENT + "\")")
    public ResponseEntity<?> findAllByPartnerOrCustomer(@RequestBody PaginationRequestDTO requestDTO) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            Page<DunningHistory> getAllCustomerDunningHistory = dunningHistoryService.findAllByPartnerOrCustomerDunningHistory(requestDTO);
            Integer responseCode = APIConstants.SUCCESS;
            response.put("customerDunningHistory", getAllCustomerDunningHistory);
            logger.debug("All dunning History fetch successfully");
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            logger.error("Error while fetch DunningHistory: " + e.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }
}
