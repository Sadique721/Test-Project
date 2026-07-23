package com.savbill.salescrmsbss.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;
import com.savbill.salescrmsbss.utils.*;
import com.savbill.salescrmsbss.utils.*;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.savbill.salescrmsbss.entity.LeadDocDetails;
import com.savbill.salescrmsbss.entity.LeadMaster;
import com.savbill.salescrmsbss.entity.pojo.DocumentDto;
import com.savbill.salescrmsbss.entity.pojo.LeadDocDetailsDTO;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.repository.LeadMasterRepository;
import com.savbill.salescrmsbss.service.LeadDocDetailsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "LeadDocDetailsController", tags = "LeadDocDetailsController")
@RestController
@RequestMapping("api/v1/SavbillSalesCrmsBss/leadDoc")
@CrossOrigin(origins = URLConstant.URL_CONSTANT)
public class LeadDocDetailsController extends BaseController {

    private final Logger LOGGER = Logger.getLogger(LeadSourceController.class);

    private static final String LEAD_DOC = "custmerDoc";

    private static final String LEAD_DOC_LIST = "custmerDocList";

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private LeadDocDetailsService leadDocDetailsService;

    @Autowired
    private LeadMasterRepository leadMasterRepository;

    @Autowired
    private DocumentVerification documentVerification;

    @Autowired
    Tracer tracer;

    @Autowired
    private FileUtility fileUtility;

    @ApiOperation(value = "Get list of LeadDocDetails in the system")
    @GetMapping("/all/{id}")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_VIEW_DOCS + "\",\"" + MenuConstants.ENTERPRISE_DOCS_VIEW + "\")")
    public ResponseEntity<Map<String, Object>> findAll(@PathVariable Long id, @RequestParam(value = "page", defaultValue = "1", required = false) Integer page, @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(page);
            paginationRequestDTO.setPageSize(pageSize);
            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
            Page<LeadDocDetails> leadDocDetailsList = this.leadDocDetailsService.findAll(paginationRequestDTO, id);
            if (leadDocDetailsList == null || leadDocDetailsList.isEmpty()) {
                response.put(LEAD_DOC_LIST, new ArrayList<>());
                response.put(SalesCrmsConstants.MESSAGE, "No Records Found!");
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NOT_FOUND);

            } else {
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                response.put(LEAD_DOC_LIST, leadDocDetailsList);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get list of LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @GetMapping("/findById")
    @ApiOperation(value = "Get LeadDocDetails based on the given id")
    public ResponseEntity<Map<String, Object>> findById(@RequestParam("leadDocId") Long leadDocId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            LeadDocDetails leadDocDetails = this.leadDocDetailsService.findById(leadDocId);
            if (leadDocDetails == null) {
                response.put(SalesCrmsConstants.MESSAGE, "No record found for leadDocDetails with the given leadDocId :" + leadDocId);
                LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NOT_FOUND);

            } else {
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                response.put(LEAD_DOC, leadDocDetails);
            }
            responseCode = SalesCrmsConstants.SUCCESS;
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Get LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @DeleteMapping("/delete/{leadDocId}")
    @ApiOperation(value = "Delete existing LeadDocDetails based on id")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_DOCS_DELETE + "\",\"" + MenuConstants.ENTERPRISE_DOCS_DELETE + "\")")
    public ResponseEntity<Map<String, Object>> deleteLeadDocDetails(@PathVariable(name = "leadDocId", required = true) Long leadDocId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());

        try {
            LeadDocDetails leadDocDetails = this.leadDocDetailsService.findById(leadDocId);
            if (leadDocDetails == null) {
                response.put(SalesCrmsConstants.MESSAGE, "No record found for LeadDocDetails with the given leadDocId :" + leadDocId);
                LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete existing LeadDocDetails" + leadDocId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NOT_FOUND);

            } else {
                this.leadDocDetailsService.deleteLeadDocDetails(leadDocId);
                response.put(SalesCrmsConstants.MESSAGE, "LeadDocDetails has been deleted successfully");
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete existing LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            }
            responseCode = SalesCrmsConstants.SUCCESS;
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete existing LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete existing LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

            apiResponseController.buildErrorMessageForResponse(response, e);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponseController.apiResponse(responseCode, response);
    }


    @PostMapping("/save")
    @ApiOperation(value = "Add/Update LeadDocDetails")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_DOCS_CREATE + "\",\"" + MenuConstants.ENTERPRISE_DOCS_CREATE + "\",\"" + MenuConstants.ENTERPRISE_DOCS_EDIT + "\",\"" + MenuConstants.LEAD_DOCS_EDIT + "\")")
    public ResponseEntity<Map<String, Object>> addLeadDocDetails(@RequestParam String docDetails, @RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Save");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());

        Integer responseCode = SalesCrmsConstants.FAIL;
        try {
            LeadDocDetailsDTO leadDocDetailsDto = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(docDetails, new TypeReference<LeadDocDetailsDTO>() {
            });
            if (leadDocDetailsDto.getDocId() != null)
                this.leadDocDetailsService.validateRequest(leadDocDetailsDto, CommonConstants.OPERATION_UPDATE);
            else this.leadDocDetailsService.validateRequest(leadDocDetailsDto, CommonConstants.OPERATION_ADD);
            response.put(LEAD_DOC, this.leadDocDetailsService.save(leadDocDetailsDto, file));
            if (leadDocDetailsDto.getDocId() != null) {
                response.put(SalesCrmsConstants.MESSAGE, "LeadDocDetails has been updated successfully");
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            } else {
                response.put(SalesCrmsConstants.MESSAGE, "LeadDocDetails has been added successfully");
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }

            responseCode = SalesCrmsConstants.SUCCESS;
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create/update LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create/update LeadDocDetails" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @PostMapping("/uploadDocOnline")
    @ApiOperation(value = "uploadDocOnline LeadDocDetails")
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_DOCS_EDIT + "\",\"" + MenuConstants.LEAD_DOCS_CREATE + "\",\"" + MenuConstants.ENTERPRISE_DOCS_EDIT + "\",\"" + MenuConstants.ENTERPRISE_DOCS_CREATE + "\")")
    public ResponseEntity<Map<String, Object>> uploadDocumentOnline(@RequestBody LeadDocDetailsDTO customerDocDetails, @RequestParam Boolean isUpdate, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Save");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());

        try {
            if (!isUpdate)
                this.leadDocDetailsService.validateRequest(customerDocDetails, CommonConstants.OPERATION_ADD);
            else this.leadDocDetailsService.validateRequest(customerDocDetails, CommonConstants.OPERATION_UPDATE);

            response.put(LEAD_DOC, this.leadDocDetailsService.uploadDocumentOnline(customerDocDetails, isUpdate));
            response.put(SalesCrmsConstants.MESSAGE, "Documents uploaded successfully");
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Documents uploaded" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            responseCode = SalesCrmsConstants.SUCCESS;
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Documents uploaded" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Documents uploaded" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }


    @GetMapping("/approveDoc/{docId}/{status}")
    public ResponseEntity<Map<String, Object>> approveCustDoc(@PathVariable Long docId, @PathVariable String status, HttpServletRequest req) {
        Map<String, Object> response = new HashMap<>();
        Integer responseCode = SalesCrmsConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));

        MDC.put("spanId", traceContext.spanIdString());

        try {
            response.put(LEAD_DOC, leadDocDetailsService.approveLeadDocDetails(docId, status));
            response.put(SalesCrmsConstants.MESSAGE, "Approve document successfully");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Approve document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            responseCode = SalesCrmsConstants.SUCCESS;
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(SalesCrmsConstants.ERROR_MESSAGE, e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Approve document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Approve document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return apiResponseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "verifyDocument")
    @PostMapping(value = "/verifyDocument")
    public ResponseEntity<?> verifyDocument(@Valid @RequestBody DocumentDto documentDto, HttpServletRequest request) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Verify");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("verifyDocument", documentVerification.authenticateAndVerifyDoc(documentDto));
            response.put("message", response.get(DocumentConstants.MESSAGE));
            RESP_CODE = Integer.parseInt(response.get("code").toString());
            MDC.remove("type");
            LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "verifyDocument" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

            return apiResponseController.apiResponse(RESP_CODE, response);
        } catch (Exception e) {
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "verifyDocument" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);

            //e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
    }

    @RequestMapping(value = "/document/download/{docId}/{leadId}", method = RequestMethod.GET)
    @PreAuthorize("validatePermission(\"" + MenuConstants.LEAD_DOCS_DOWNLOAD + "\",\"" + MenuConstants.ENTERPRISE_DOCS_DOWNLOAD + "\")")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Long leadId, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, 123);

        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = "LeadDocDetailsController" + " [downloadDocument()] ";
        Resource resource = null;
        try {
            LeadMaster leadMaster = leadMasterRepository.findById(leadId).get();
            if (null == leadMaster) {
                return ResponseEntity.notFound().build();
            }
            LeadDocDetails docDetails = leadDocDetailsService.findById(docId);
            if (null == docDetails) {
                return ResponseEntity.notFound().build();
            }
            resource = fileUtility.getLeadDoc(leadMaster.getId(), docDetails.getUniquename());
            //resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
//                log.info("Downloading document with  "+docId+" downloaded Successfully  :  request: { From : {} }; Response : {{}}",SUBMODULE,APIConstants.SUCCESS);
                LOGGER.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Downloading document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Downloading document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NOT_FOUND);

                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            LOGGER.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Downloading document" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.NOT_FOUND);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return null;
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
