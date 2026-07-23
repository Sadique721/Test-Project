package com.savbill.radius.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.radius.aaa.constant.MenuConstants;
import com.savbill.radius.dto.CDRSearchDTO;
import com.savbill.radius.helper.AcctCdrSearchDTO;
import com.savbill.radius.helper.AcctShowDTO;
import com.savbill.radius.utils.LogConstants;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.savbill.radius.dto.PaginationDTO;
import com.savbill.radius.entity.AcctCdr;
import com.savbill.radius.services.AcctCdrService;
import com.savbill.radius.services.ExcelExportService;
import com.savbill.radius.utils.RadiusConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "AcctCdr Management", description = "REST APIs related to AcctCdr Entity!!!!", tags = "AcctCdr")
@RestController
@RequestMapping("/SavbillRadius")
public class AcctCdrController {

    private static final String ACCTCDR_LIST = "acctCdrList";

    @Autowired
    private AcctCdrService acctCdrService;

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private ExcelExportService excelExportService;

	@Autowired
	private Tracer tracer;

    private static final Logger log = LoggerFactory.getLogger(AcctCdrController.class);

    @GetMapping("/acctCdrs")
    //@PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
	//@PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CDR +"\")")
	public ResponseEntity<Map<String, Object>> findAllAcctCdrs(PaginationDTO paginationDTO ,
	    @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
	Map<String, Object> response = new HashMap<>();
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID,traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID,traceContext.spanIdString());
	try {
	    Page<AcctCdr> page = acctCdrService.findAllAcctCdr(mvnoId, paginationDTO,request);
	    Integer responseCode = 0;
		if(CollectionUtils.isEmpty(page.getContent())) {
			response.put("status",RadiusConstants.NO_CONTENT_FOUND);
			response.put("message","No Records Found!");
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Request to fetch AcctCdr details," + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.NOT_FOUND.value());
			return apiResponseController.apiResponse(HttpStatus.NO_CONTENT.value(), response);
		} else {
			responseCode=RadiusConstants.SUCCESS;
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Request to fetch AcctCdr details," + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
			response.put("acctCdr",page);
		}
		return apiResponseController.apiResponse(responseCode, response);

	} catch (Exception e) {

	    Integer responseCode = RadiusConstants.FAIL;
		log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  fetch Email config details ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
	    response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
	    response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
	    return apiResponseController.apiResponse(responseCode, response);
	} finally {
	    MDC.remove(RadiusConstants.TYPE);
		MDC.remove(RadiusConstants.TRACE_ID);
		MDC.remove(RadiusConstants.SPAN_ID);
	}
    }

    @PostMapping("/findAcctCdrByUserName")
//    @PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
	@PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST_DETAILS_CDR_SESSION +"\", \"" + MenuConstants.RADIUS_CDR	+ "\")")

	public ResponseEntity<Map<String, Object>> findAcctCdrByUserName(@RequestBody CDRSearchDTO paginationDTO , @RequestParam (required = true) Integer  mvnoId, HttpServletRequest request) {

		Map<String, Object> response = new HashMap<>();
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID,traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID,traceContext.spanIdString());

	try {
		if(paginationDTO.getSize() < 1) {
			response.put(RadiusConstants.ERROR_MESSAGE, "Page size must not be less than one!");
			return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
		}
	    Page<AcctCdr> page = acctCdrService.findAcctCrdUsingFilter(paginationDTO, mvnoId);
	    Integer responseCode = 0;
		if(CollectionUtils.isEmpty(page.getContent())) {
			responseCode=RadiusConstants.SUCCESS;
			response.put("infomsg", "No Records Found!");
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Request to fetch AcctCdr details with name ,"+ paginationDTO.getUserName() + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.NOT_FOUND.value());
		} else {
			responseCode=RadiusConstants.SUCCESS;
			response.put("acctCdr",page);
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Request to fetch AcctCdr details with name," + paginationDTO.getUserName() + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
		}
	    return apiResponseController.apiResponse(responseCode, response);

	} catch (Exception e) {
	    Integer responseCode = RadiusConstants.FAIL;
		log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Error while fetching AcctCdr by name: ," + paginationDTO.getUserName() + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
	    response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
	    return apiResponseController.apiResponse(responseCode, response);

	}  finally {
		MDC.remove(RadiusConstants.TYPE);
		MDC.remove(RadiusConstants.TRACE_ID);
		MDC.remove(RadiusConstants.SPAN_ID);
	}
    }

    @DeleteMapping("/deleteAcctCdr")
//    @PreAuthorize("@roleAccesses.hasPermission('cdrs','deleteAccess',#request.getHeader('requestFrom'))")
	@PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CUST + "\")")
    public ResponseEntity<Map<String, Object>> deleteAcctCdr(@RequestParam(name = "cdrid", required = true) Long cdrId,
	    @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {

	Map<String, Object> response = new HashMap<>();
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_DELETE);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID,traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID,traceContext.spanIdString());

	try {
	    acctCdrService.deleteAcctCdrById(cdrId, mvnoId);
	    Integer responseCode = RadiusConstants.SUCCESS;
	    response.put(RadiusConstants.MESSAGE, "AcctCdr has been deleted successfully.");
		log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " AcctCdr has been deleted successfully with id," +cdrId + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
	    return apiResponseController.apiResponse(responseCode, response);

	} catch (Exception e) {
		log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  DELETE ACCTCDR with id ," +cdrId+ LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
	    Integer responseCode = RadiusConstants.FAIL;
	    response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
	    return apiResponseController.apiResponse(responseCode, response);

	} finally {
		MDC.remove(RadiusConstants.TYPE);
		MDC.remove(RadiusConstants.TRACE_ID);
		MDC.remove(RadiusConstants.SPAN_ID);
	}
    }

    @GetMapping("/cdrDetail")
//    @PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getCdrDetail(@RequestParam(name = "cdrId", required = true) Long cdrId,
	    @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID,traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID,traceContext.spanIdString());
	try {
	    response.put("cdrDetail", acctCdrService.findAcctCdrById(cdrId, mvnoId));
		log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Request to fetch AcctCdr details ," + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
	    return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
	} catch (Exception e) {
		log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  fetch Fetch ACCTCDR details ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
	    apiResponseController.buildErrorMessageForResponse(response, e);
	    return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
	}  finally {
		MDC.remove(RadiusConstants.TYPE);
		MDC.remove(RadiusConstants.TRACE_ID);
		MDC.remove(RadiusConstants.SPAN_ID);
	}
    }

    @ApiOperation(value = "Excel Export")
    @GetMapping(value = "/exportExcel")
//    @PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> exportExcel(
	    @RequestParam(name = "userName", required = false) String userName,
	    @RequestParam(name = "framedIp", required = false) String framedIp,
	    @RequestParam(name = "fromDate", required = false) String fromDate,
	    @RequestParam(name = "toDate", required = false) String toDate,
	    @RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletResponse httpResponse,
	    HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID,traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID,traceContext.spanIdString());
	try {

	    httpResponse.setContentType("application/octet-stream");
	    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	    String currentDateTime = dateFormatter.format(new Date());
	    Integer responseCode = RadiusConstants.SUCCESS;
	    String headerKey = "Content-Disposition";
	    String headerValue = "attachment; filename=CDRUsers" + currentDateTime + ".xlsx";
	    httpResponse.setHeader(headerKey, headerValue);
	    CDRSearchDTO paginationDTO = new CDRSearchDTO();
	    if(fromDate != null) {
	    	paginationDTO.setFromDate(fromDate);
	    }
	    if(fromDate != null) {
	    	paginationDTO.setToDate(toDate);
	    }
	    Page<AcctCdr> page = acctCdrService.findAcctCrdUsingFilter(paginationDTO, mvnoId);
	    if (CollectionUtils.isEmpty(page.getContent())) {
			responseCode=RadiusConstants.NULL_VALUE;
			response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
			log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Request to EXPORT ACCTCDR ,"+userName + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.NOT_FOUND.value());
	    }
	    excelExportService.exportExcel(page.getContent(), httpResponse);
		log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Request to EXPORT ACCTCDR is successfull  ," + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
	    return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
	} catch (Exception e) {
		log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  Export AcctCDR ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
	    apiResponseController.buildErrorMessageForResponse(response, e);
	    return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
	}  finally {
		MDC.remove(RadiusConstants.TYPE);
		MDC.remove(RadiusConstants.TRACE_ID);
		MDC.remove(RadiusConstants.SPAN_ID);
	}
    }

	@PostMapping("/findAcctCdr")
	//@PreAuthorize("@roleAccesses.hasPermission('cdrs','readAccess',#request.getHeader('requestFrom'))")
	//@PreAuthorize("validatePermission(\"" + MenuConstants.RADIUS_CDR +"\")")
	public ResponseEntity<Map<String, Object>> findAllAcctCdrsByparameters(@RequestParam(name = "mvnoId", required = true) Integer mvnoId,@RequestBody AcctCdrSearchDTO acctCdrSearchDTO, HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
		TraceContext traceContext = tracer.currentSpan().context();
		MDC.put(RadiusConstants.TRACE_ID,traceContext.traceIdString());
		MDC.put(RadiusConstants.SPAN_ID,traceContext.spanIdString());
		try {
			Page<AcctShowDTO> page = acctCdrService.findAcctCdrByRequest(acctCdrSearchDTO,mvnoId);
			Integer responseCode = 0;
			if(CollectionUtils.isEmpty(page.getContent())) {
				responseCode=RadiusConstants.SUCCESS;
				response.put(RadiusConstants.ERROR_MESSAGE, "No Records Found!");
				log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + "Request for AcctCdrs has been fetched successfully  ," + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.NOT_FOUND.value());
			} else {
				responseCode=RadiusConstants.SUCCESS;
				log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR + " Request for ACCTCDR has been fetched succesfully   ," + LogConstants.REQUEST_BY +  MDC.get(RadiusConstants.USER_NAME) +","+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+","+ LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.OK.value());
				response.put("acctCdr",page);
			}
			return apiResponseController.apiResponse(responseCode, response);

		} catch (Exception e) {

			Integer responseCode = RadiusConstants.FAIL;
			log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Unable to  fetch AccTcdr By Parameter ," + LogConstants.REQUEST_BY +MDC.get(RadiusConstants.USER_NAME)+","+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+","+LogConstants.LOG_STATUS_CODE+":"+ HttpStatus.EXPECTATION_FAILED.value());
			response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
			response.put(RadiusConstants.ERROR_MESSAGE, e.getMessage());
			return apiResponseController.apiResponse(responseCode, response);

		}  finally {
			MDC.remove(RadiusConstants.TYPE);
			MDC.remove(RadiusConstants.TRACE_ID);
			MDC.remove(RadiusConstants.SPAN_ID);
		}
	}

}
