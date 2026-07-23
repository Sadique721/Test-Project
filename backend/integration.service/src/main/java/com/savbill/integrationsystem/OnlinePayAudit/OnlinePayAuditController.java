package com.savbill.integrationsystem.OnlinePayAudit;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.integrationsystem.PaymentIntegration.Model.CustomerPayment;
import com.savbill.integrationsystem.core.controller.APIResponseController;
import com.savbill.integrationsystem.core.dto.GenericSearchModel;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL+URLConstants.ONLINE_PAY_AUDIT)
public class OnlinePayAuditController extends APIResponseController {


    private static String MODULE = " [OnlinePayAuditController] ";

    @Autowired
    private Tracer tracer;

    @Autowired
    private OnlinePayAuditService onlinePayAuditService;

    private final Logger log = Logger.getLogger(OnlinePayAuditController.class);


    @PostMapping("/all")
    ResponseEntity<?> getAllOnlinePaymentAudit(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest request) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", onlinePayAuditService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            Page<CustomerPayment> onlinePayAuditList = onlinePayAuditService.getOnlinePayAuditList(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder(), paginationRequestDTO.getFilters());
            if (!onlinePayAuditList.isEmpty()) {
                response.put("onlineAuditData", onlinePayAuditList.getContent());
                response.put("totalRecords", onlinePayAuditList.getTotalElements());
                response.put("Status", "Success");
                response.put("message", "success");
                response.put("ResponseCode", APIConstants.SUCCESS);
            } else {
                response.put("onlineAuditData", new ArrayList<>());
                response.put("totalRecords", 0);
                response.put("message", "success");
                response.put("Status", "No Record Found !!");
                response.put("ResponseCode", APIConstants.NOT_FOUND);
            }

            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Online Payment Audit List" + LogConstants.REQUEST_BY + onlinePayAuditService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (IllegalArgumentException e) {
            response.put("status", "Failed");
            response.put("ResponseCode", APIConstants.NOT_FOUND);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("Status", "Failed");
            response.put("ResponseCode", APIConstants.FAIL);
            response.put("message", e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Online Payment Audit List" + LogConstants.REQUEST_BY + onlinePayAuditService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/allByCustId")
    ResponseEntity<?> getAllOnlinePaymentAuditForCustomer(@RequestParam("custId") Integer custId, HttpServletRequest request) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", onlinePayAuditService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            List<CustomerPayment> onlinePayAuditList = onlinePayAuditService.getOnlinePayAuditListByCustId(custId);
            if (!onlinePayAuditList.isEmpty()) {
                response.put("onlineAuditData", onlinePayAuditList);
                response.put("Status", "Success");
                response.put("message", "success");
                response.put("ResponseCode", APIConstants.SUCCESS);
            } else {
                response.put("onlineAuditData", new ArrayList<>());
                response.put("message", "success");
                response.put("Status", "No Record Found !!");
                response.put("ResponseCode", APIConstants.NOT_FOUND);
            }

            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Online Payment Audit List" + LogConstants.REQUEST_BY + onlinePayAuditService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception e) {
            response.put("Status", "Failed");
            response.put("ResponseCode", APIConstants.FAIL);
            response.put("message", e.getMessage());
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Online Payment Audit List" + LogConstants.REQUEST_BY + onlinePayAuditService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        }
        return apiResponse(RESP_CODE, response);
    }

    @ApiOperation(value = "Export transactions based on date range")
    @PostMapping("/exportTransactionsToCSV")
    @PreAuthorize("@permissionService.validatePermission(\"" + APIConstants.DOWNLOAD_ONLINEPAYADUIT + "\")")
    public ResponseEntity<Map<String, Object>> exportTransactionsToCSV(
            @RequestBody List<GenericSearchModel> filterList,
            HttpServletResponse request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);

        try {

            List<Map<String, String>> dataToExport = onlinePayAuditService.getTransactionsForExport(filterList);
            response.put("dataToExport", dataToExport);
            return onlinePayAuditService.apiResponse(APIConstants.SUCCESS, response);
        } catch (IllegalArgumentException e) {
            Integer responseCode = APIConstants.NOT_FOUND;
            response.put(APIConstants.MESSAGE, e.getMessage());
            return onlinePayAuditService.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return onlinePayAuditService.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

}
