package com.savbill.taskmanagement.core.auditLog.controller;


import com.savbill.taskmanagement.core.auditLog.model.AuditSearchPojo;
import com.savbill.taskmanagement.core.auditLog.service.AuditTrailService;
import com.savbill.taskmanagement.core.constants.LogConstants;
import com.savbill.taskmanagement.core.constants.MenuConstants;
import com.savbill.taskmanagement.core.constants.NotificationConstants;
import com.savbill.taskmanagement.core.controller.ApiBaseController;
import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.PaginationRequestDTO;
import com.savbill.taskmanagement.core.modules.constants.UrlConstants;
import com.savbill.taskmanagement.core.modules.utils.APIConstants;

import com.savbill.taskmanagement.core.security.dto.LoggedInUser;
import com.savbill.taskmanagement.core.utillity.PageableResponse;
import com.savbill.taskmanagement.core.utillity.log.ApplicationLogger;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.AUDIT_TRAIL)
public class AuditTrailController extends ApiBaseController {

    @Autowired
    Javers javers;

    @Autowired
    AuditTrailService auditTrailService;



    @PreAuthorize("validatePermission(\"" + MenuConstants.AUDIT_LOG + "\")")
    @GetMapping("/all")
    public ResponseEntity<?> getAllAuditTrails(@RequestParam(required = true) Integer pageIndex,
                                              @RequestParam(required = true) Integer pageSize) {
        int skip = pageIndex * pageSize;
        String mvnoId = getLoggedInUser().getMvnoId().toString();
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

//    @GetMapping("/byModule")
//    public ResponseEntity<?> getAuditTrailsByModule(@RequestParam(required = true) String moduleName, @RequestParam(required = true) Integer pageIndex,
//                                               @RequestParam(required = true) Integer pageSize) {
////        Changes changes = javers.findChanges(QueryBuilder.anyDomainObject().build());
//
//        HashMap<String, Object> response = auditTrailService.getAuditTraildByModule(moduleName, pageIndex, pageSize);
//
//        return apiResponse(APIConstants.SUCCESS, response);
//    }


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
        } else {
            page = auditTrailService.searchAuditTrailsByModule(changes, auditSearchPojo.getModuleName(), auditSearchPojo.getEntityName(), paginationRequestDTO, null, null);
        }


        Map<String, Object> response = new HashMap<>();
        if (page.getData().isEmpty()) {
            response.put(NotificationConstants.MESSAGE, "No record found with the given event type , entityName : " + auditSearchPojo.getEntityName());
            genericDataDTO.setResponseCode(APIConstants.NOT_FOUND);
            genericDataDTO.setResponseMessage("No record found with the given event type , entityName : " + auditSearchPojo.getEntityName());
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to  Fetch audit data by entity name " + auditSearchPojo.getEntityName() + " ," + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + "," + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + "," + LogConstants.LOG_STATUS_CODE + ":" + HttpStatus.OK.value());
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

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("Audit Trail Controller" + e.getStackTrace(), e);
        }
        return loggedInUser;
    }
}
