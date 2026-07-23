package com.savbill.salescrmsbss.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import com.savbill.salescrmsbss.utils.*;

import com.savbill.salescrmsbss.utils.*;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.savbill.salescrmsbss.entity.QuotationDetails;
import com.savbill.salescrmsbss.entity.QuotationPODoc;
import com.savbill.salescrmsbss.entity.pojo.CreateLeadQuotationDTO;
import com.savbill.salescrmsbss.entity.pojo.EmailAuditingDTO;
import com.savbill.salescrmsbss.entity.pojo.QuotationDetailsDTO;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.repository.QuotationDetailsRepository;
import com.savbill.salescrmsbss.repository.QuotationPODocRepository;
import com.savbill.salescrmsbss.service.LeadQuotationService;
import com.savbill.salescrmsbss.service.QuotationPODocService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "LeadQuotation", description = "REST APIs related to Lead Quotation Report Generation", tags = "LeadQuotation")
@RestController
@RequestMapping("api/v1/SavbillSalesCrmsBss/leadQuotation")
@CrossOrigin(origins = URLConstant.URL_CONSTANT)
public class LeadQuotationController extends BaseController {

    private static String MODULE = " [LeadQuotationController] ";

    private final Logger LOGGER = Logger.getLogger(LeadQuotationController.class);

    private static final String LEAD_QUOTATION = "leadQuotation";

    private static final String LEAD_QUOTATION_LIST = "leadQuotationList";

    @Autowired
    private APIResponseController apiResponseController;
    @Autowired
    Tracer tracer;
    @Autowired
    private LeadQuotationService leadQuotationService;

    @Autowired
    private PDFGenerator pdfGenerator;

    @Autowired
    private QuotationPODocService quotationPODocService;

    @Autowired
    private QuotationPODocRepository quotationPODocRepository;

    @Autowired
    private QuotationDetailsRepository quotationDetailsRepository;

    @Autowired
    private FileUtility fileUtility;

    @PostMapping("/save")
    @ApiOperation(value = "Create lead quotation")
    @PreAuthorize("validatePermission(\"" + MenuConstants.QM_GENERATE + "\")")
    public ResponseEntity<Map<String, Object>> createLeadQuotationByCircuit(
            @RequestBody CreateLeadQuotationDTO createLeadQuotationDTO, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {

            response.put(LEAD_QUOTATION, leadQuotationService.createLeadQuotationByCircuit(createLeadQuotationDTO));
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create lead quotation" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create lead quotation" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create lead quotation" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findListOfQuotationDetailsByLeadId")
    @ApiOperation(value = "Get list of QuotationDetails by lead id in the system")
    @PreAuthorize("validatePermission(\"" + MenuConstants.QUOTATION_MGMT + "\")")
    public ResponseEntity<Map<String, Object>> findListOfQuotationDetailsByLeadId(
            @RequestParam(value = "leadId", required = true) Long leadId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            List<QuotationDetailsDTO> list = leadQuotationService.findListOfQuotationDetailsByLeadId(leadId);
            if (list.isEmpty()) {
                response.put(LEAD_QUOTATION_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
            } else {
                response.put(LEAD_QUOTATION_LIST, list);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch leadQuotationList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch leadQuotationList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch leadQuotationList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/generateQuotationReport/{quotationId}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.QM_DOWNLOAD_PDF + "\",\"" + MenuConstants.QM_SHOW + "\")")
    public ResponseEntity<Void> generateQuotationReport(@PathVariable("quotationId") Long quotationId,
                                                        HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + pdfGenerator.getPdfNameWithDate();
        response.setHeader(headerKey, headerValue);
        pdfGenerator.generatePdfReport(quotationId, response);
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/sendEmailWithQuotationDetails")
    @ApiOperation(value = "Send Mail With Quotation Report")
    public ResponseEntity<Map<String, Object>> sendEmailWithQuotationDetails(
            @RequestBody EmailAuditingDTO emailDTO,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
//			String authHeaderString = null;
//			if (request.getHeader(CommonConstants.AUTHORIZATION_HEADER_STRING) != null) {
//				authHeaderString = request.getHeader(CommonConstants.AUTHORIZATION_HEADER_STRING);
            leadQuotationService.sendEmailWithQuotationDetails(emailDTO);
            responseCode = SalesCrmsConstants.SUCCESS;
            response.put(SalesCrmsConstants.MESSAGE, "Quotation Report Sent Successfully");
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Send mail with Quotation Report" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

//			} else {
//				responseCode = SalesCrmsConstants.FAIL;
//				response.put(SalesCrmsConstants.ERROR_MESSAGE, "Access token value is not present!");
//				logger.info("Access token value is not present!", MODULE, responseCode, response);
//			}
            return apiResponseController.apiResponse(responseCode, response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Send mail with Quotation Report" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Send mail with Quotation Report" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/uploadQuotationPODoc")
    @ApiOperation(value = "UploadQuotationPODoc")
    @PreAuthorize("validatePermission(\"" + MenuConstants.QM_ASSIGN_PO + "\")")
    public ResponseEntity<Map<String, Object>> uploadQuotationPODoc(@RequestParam Long quotationId, @RequestParam String poNumber
            , @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        try {
            QuotationPODoc quotationPODoc = this.quotationPODocService.save(quotationId, poNumber, file);
            if (quotationPODoc != null) {
                response.put("quotationPoDoc", quotationPODoc);
                response.put(SalesCrmsConstants.MESSAGE, "Quotation PO document has been uploaded successfully");
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Upload Quotation document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                responseCode = SalesCrmsConstants.SUCCESS;
            } else {
                responseCode = SalesCrmsConstants.FAIL;
            }
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Upload Quotation document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Upload Quotation document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @RequestMapping(value = "/po/download/{quotationId}/{quotationPoDocId}", method = RequestMethod.GET)
    @PreAuthorize("validatePermission(\"" + MenuConstants.QM_DOWNLOAD_PO + "\")")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long quotationId, @PathVariable Long quotationPoDocId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = "LeadQuotationController" + " [downloadDocument()] ";
        Resource resource = null;
        try {
            QuotationDetails quotationDetails = quotationDetailsRepository.findById(quotationId).get();
            if (null == quotationDetails) {
                return ResponseEntity.notFound().build();
            }
            QuotationPODoc docDetails = quotationPODocRepository.findById(quotationPoDocId).get();
            if (null == docDetails) {
                return ResponseEntity.notFound().build();
            }
            resource = fileUtility.getPoDoc(quotationDetails.getId(), docDetails.getUniquename());
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Downloading document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Downloading document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.NOT_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

            }
        } catch (Exception ex) {
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Downloading document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return null;
    }

    @GetMapping("/assignworkflowForQuotation/{quotationId}")
    @ApiOperation(value = "Assign Workflow By quotationId")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> assignWorkflowByQuotationId(@PathVariable Long quotationId,
                                                                           HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            String authTokenHeader = request.getHeader("Authorization");
            response.put(LEAD_QUOTATION,
                    this.leadQuotationService.assignWorkFlow(quotationId, getStaffId(authTokenHeader), getBUId(authTokenHeader), getMvnoId(authTokenHeader)));
            response.put(SalesCrmsConstants.MESSAGE, "Assign Workflow is successfully");
            responseCode = SalesCrmsConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Assign Workflow" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Assign Workflow" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Assign Workflow" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    //get logger in user first name
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
