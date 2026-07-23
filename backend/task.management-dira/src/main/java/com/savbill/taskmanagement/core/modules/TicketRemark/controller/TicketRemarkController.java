package com.savbill.taskmanagement.core.modules.TicketRemark.controller;


import com.savbill.taskmanagement.core.controller.APIResponseController;
import com.savbill.taskmanagement.core.controller.ApiBaseController;
import com.savbill.taskmanagement.core.exceptions.CustomValidationException;
import com.savbill.taskmanagement.core.modules.TicketRemark.domain.TicketRemark;
import com.savbill.taskmanagement.core.modules.TicketRemark.model.FetchTicketRemarkDTO;
import com.savbill.taskmanagement.core.modules.TicketRemark.model.TicketRemarkDTO;
import com.savbill.taskmanagement.core.modules.TicketRemark.service.TicketRemarkService;
import com.savbill.taskmanagement.core.modules.acl.constants.AclConstants;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TICKETREMARK)
public class TicketRemarkController extends ApiBaseController {

    private static final Logger logger = LoggerFactory.getLogger(TicketRemarkController.class);

    @Autowired
    private TicketRemarkService ticketRemarkService;

    @Autowired
    private APIResponseController responseController;


    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_EDIT + "\",\"" + AclConstants.OPERATION_CASE_VIEW+ "\")")
    @PostMapping("/addremark")
    public ResponseEntity<?> addTicketRemark(@RequestBody TicketRemarkDTO ticketRemarkDTO) throws Exception {
        MDC.put("type", "add");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        try {
            if (ticketRemarkDTO != null) {
                if(Objects.isNull(ticketRemarkDTO.getIsFromCustomer())){
                    ticketRemarkDTO.setIsFromCustomer(false);
                }
                responseEntity =  ticketRemarkService.saveRemark(ticketRemarkDTO);
                RESP_CODE = APIConstants.SUCCESS;
                logger.info("add Remark for  " + ticketRemarkDTO.getTicketNo() + "  :  request: { From : {}}; Response : {{}}", "TicketRemark", RESP_CODE, response);
                return  responseEntity;
            }
        } catch (CustomValidationException ce) {
            logger.error("TicketRemark" + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.MESSAGE, ce.getMessage());
            responseEntity = responseController.apiResponse(APIConstants.EXPECTATION_FAILED , response);
            logger.error("Unable to add remark for " + ticketRemarkDTO.getTicketNo() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "TicketRemark", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            logger.error("ticketRemark" + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.put(APIConstants.MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            responseEntity = responseController.apiResponse(APIConstants.INTERNAL_SERVER_ERROR , response);
            logger.error("Unable to add remarks for " + ticketRemarkDTO.getTicketNo() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "TicketRemark", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return responseEntity;
    }


    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_EDIT + "\",\"" + AclConstants.OPERATION_CASE_VIEW+ "\")")
    @PostMapping("/getremark")
    public ResponseEntity<?> getTicketRemark(@RequestBody FetchTicketRemarkDTO fetchTicketRemarkDTO) throws Exception {
        MDC.put("type", "add");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        try {
            if (fetchTicketRemarkDTO != null) {
                List<TicketRemark> fetchTicketRemark = ticketRemarkService.fetchTicketRemark(fetchTicketRemarkDTO.getTicketId());
                if(fetchTicketRemark.isEmpty()) {
                    response.put("ticketremarklist",fetchTicketRemark);
                    response.put(APIConstants.MESSAGE,"No Record Found");
                    responseEntity =  responseController.apiResponse(APIConstants.NOT_FOUND,response);
                }
                else{
                    response.put("ticketremarklist",fetchTicketRemark);
                    response.put(APIConstants.MESSAGE,"Success");
                    responseEntity =  responseController.apiResponse(APIConstants.SUCCESS,response);
                }
                RESP_CODE = APIConstants.SUCCESS;
                logger.info("get Remark for  " + fetchTicketRemarkDTO.getTicketId() + "  :  request: { From : {}}; Response : {{}}", "TicketRemark", RESP_CODE, response);
                return  responseEntity;
            }
        } catch (CustomValidationException ce) {
            logger.error("TicketRemark" + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.MESSAGE, ce.getMessage());
            responseEntity = responseController.apiResponse(APIConstants.EXPECTATION_FAILED , response);
            logger.error("Unable to fetch remark for " + fetchTicketRemarkDTO.getTicketId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "TicketRemark", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
            logger.error("ticketRemark" + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.put(APIConstants.MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            responseEntity = responseController.apiResponse(APIConstants.INTERNAL_SERVER_ERROR , response);
            logger.error("Unable to fetch remarks for " + fetchTicketRemarkDTO.getTicketId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "TicketRemark", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return responseEntity;
    }
}
