package com.savbill.salescrmsbss.AuditLog.controllor;
import com.savbill.salescrmsbss.AuditLog.dto.AuditSearchPojo;
import com.savbill.salescrmsbss.AuditLog.entity.PaginationDetails;
import com.savbill.salescrmsbss.AuditLog.service.AuditTrailService;
import com.savbill.salescrmsbss.controller.ApiBaseController;
import com.savbill.salescrmsbss.entity.pojo.GenericDataDTO;
import com.savbill.salescrmsbss.helper.PaginationRequestDTO;
import com.savbill.salescrmsbss.utils.*;
import com.savbill.salescrmsbss.utils.*;
import org.apache.log4j.Logger;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.AUDIT_TRAIL)
public class AuditTrailController extends ApiBaseController {

    @Autowired
    Javers javers;

    @Autowired
    AuditTrailService auditTrailService;
    final Logger log = Logger.getLogger(AuditTrailController.class);

    private String MODULE = "[AuditTrailController]";

    @PreAuthorize("validatePermission(\"" + UrlConstants.AUDIT_LOG + "\")")
    @GetMapping("/all")
    public ResponseEntity<?> getAllAuditTrails(@RequestParam(required = true) Integer pageIndex,
                                               @RequestParam(required = true) Integer pageSize) {
        int skip = pageIndex * pageSize;
        String mvnoId = auditTrailService.getLoggedInUser().getMvnoId().toString();
        QueryBuilder queryBuilder = QueryBuilder.anyDomainObject()
                .withCommitPropertyLike("mvnoId", mvnoId); // Filter by MVNO property

        // Build the JQL query with pagination
        JqlQuery jqlQuery = queryBuilder
                .limit(pageSize) // Set limit for the current page
                .skip(skip)
                .build();
        Changes changes = javers.findChanges(jqlQuery);
        JqlQuery jqlQueryCount = QueryBuilder.anyDomainObject().withCommitPropertyLike("mvnoId", mvnoId).limit(Integer.MAX_VALUE).build();
        int totalRecords = javers.findSnapshots(jqlQueryCount).size();
        HashMap<String, Object> response = auditTrailService.getAllAuditTrails(changes, pageIndex, pageSize);
        response.put("totalRecords", totalRecords);
        return apiResponse(APIConstants.SUCCESS, response);
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response , null);
    }

    @PostMapping("/byModule")
    public GenericDataDTO getAuditTrailsByModule(@RequestBody AuditSearchPojo auditSearchPojo, HttpServletRequest request) {
        Changes changes = javers.findChanges(QueryBuilder.anyDomainObject().build());

        PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
        paginationRequestDTO.setPage(auditSearchPojo.getPageIndex());
        paginationRequestDTO.setPageSize(auditSearchPojo.getPageSize());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageableResponse<GenericDataDTO> page;
        if (auditSearchPojo.getStartDate() != null && auditSearchPojo.getEndDate() != null) {
            page = auditTrailService.searchAuditTrailsByModule(changes, auditSearchPojo.getModuleName(), auditSearchPojo.getEntityName(), paginationRequestDTO, auditSearchPojo.getStartDate().atStartOfDay(), auditSearchPojo.getEndDate().atStartOfDay());
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch audit by Start-End Date " + auditSearchPojo.getEntityName()  + LogConstants.REQUEST_BY + auditTrailService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND  + LogConstants.LOG_STATUS_CODE  + HttpStatus.OK.value());
        } else {
            page = auditTrailService.searchAuditTrailsByModule(changes, auditSearchPojo.getModuleName(), auditSearchPojo.getEntityName(), paginationRequestDTO, null, null);
        }
        Map<String, Object> response = new HashMap<>();
        if (page.getData().isEmpty()) {
            response.put(DocumentConstants.MESSAGE, "No record found with the given event type , entityName : " + auditSearchPojo.getEntityName());
            genericDataDTO.setResponseCode(APIConstants.NOT_FOUND);
            genericDataDTO.setResponseMessage("No record found with the given event type , entityName : " + auditSearchPojo.getEntityName());
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to  Fetch audit data by entity name " + auditSearchPojo.getEntityName() + LogConstants.REQUEST_BY + auditTrailService.getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE  + HttpStatus.OK.value());
        }else{
            genericDataDTO.setData(page.getData());
            genericDataDTO.setResponseCode(200);
            genericDataDTO.setResponseMessage("Record found sucessfully");
            genericDataDTO.setTotalRecords(page.getTotalRecords());
            genericDataDTO.setCurrentPageNumber(page.getCurrentPage());
            genericDataDTO.setTotalPages(page.getTotalPages());
            genericDataDTO.setTotalPages(page.getTotalPages());
        }
        return genericDataDTO;
    }

    @GetMapping("/bySubModule")
    public ResponseEntity<?> getAuditTrailsBySubModule(@RequestParam(required = true) String submoduleName, @RequestParam(required = true) Integer pageIndex,
                                                       @RequestParam(required = true) Integer pageSize) {
        Changes changes = javers.findChanges(QueryBuilder.anyDomainObject().build());
        HashMap<String, Object> response = auditTrailService.getAllAuditSubmodule(changes, submoduleName, pageIndex, pageSize);

        //  return apiResponse(APIConstants.SUCCESS, changes);
        return ResponseEntity.ok(changes);
    }

    @GetMapping("/getoldRecords")
    public ResponseEntity<?> getAuditTrailsoldRecord(@RequestParam(required = true) String moduleName, @RequestParam(required = true) Integer pageIndex,
                                                     @RequestParam(required = true) Integer pageSize) {
        Changes changes = javers.findChanges(QueryBuilder.anyDomainObject().build());

        HashMap<String, Object> response = auditTrailService.getOldAuditTrails(changes, moduleName, pageIndex, pageSize);

        return apiResponse(APIConstants.SUCCESS, response);
    }

    @GetMapping("/search/operation")
    public ResponseEntity<?> getAuditTrailsByOperation(@RequestParam(required = true) String operation, @RequestParam(required = true) Integer pageIndex,
                                                       @RequestParam(required = true) Integer pageSize) {
        Changes changes = javers.findChanges(QueryBuilder.anyDomainObject().build());

        HashMap<String, Object> response = auditTrailService.getAuditTrailsByOperation(changes, operation, pageIndex, pageSize);

        return apiResponse(APIConstants.SUCCESS, response);


    }
    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        String SUBMODULE = MODULE ;
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
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

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
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
