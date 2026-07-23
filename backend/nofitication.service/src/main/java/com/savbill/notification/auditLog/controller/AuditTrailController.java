package com.savbill.notification.auditLog.controller;




import com.savbill.notification.Response.Response;
import com.savbill.notification.auditLog.model.AuditSearchPojo;
import com.savbill.notification.auditLog.service.AuditTrailService;
import com.savbill.notification.controller.APIResponseController;
import com.savbill.notification.entity.LoggedInUser;
import com.savbill.notification.helper.PageableResponse;
import com.savbill.notification.helper.PaginationRequestDTO;
import com.savbill.notification.utils.*;
import com.savbill.notification.utils.*;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/SavbillNotification/auditTrail")
@Api(value = "AuditTrail", description = "REST APIs related to AuditTrail Entity!!!!", tags = "AuditTrail")
@Slf4j
public class AuditTrailController {

    @Autowired
    Javers javers;

    @Autowired
    AuditTrailService auditTrailService;

    @Autowired
    private APIResponseController apiResponseController;

    private TokenDataExtractor tokenDataExtractor;

    //final Logger log = Logger.getLogger(AuditTrailController.class);

//    @PreAuthorize("validatePermission(\"" + CommonConstants.AUDIT_LOG + "\")")
    @GetMapping("/all")
    public ResponseEntity<?> getAllAuditTrails(@RequestParam(required = true) Integer pageIndex,
                                               @RequestParam(required = true) Integer pageSize) {
        int skip = pageIndex * pageSize;
        Integer mvnoId = getLoggedInMvnoId();
        QueryBuilder queryBuilder;
        if(mvnoId==-1) {
             queryBuilder = QueryBuilder.anyDomainObject(); // Filter by MVNO property
        }else{
             queryBuilder = QueryBuilder.anyDomainObject()
                    .withCommitPropertyLike("mvnoId", mvnoId.toString()); // Filter by MVNO property
        }
        // Build the JQL query with pagination
        JqlQuery jqlQuery = queryBuilder
                .limit(pageSize) // Set limit for the current page
                .skip(skip)
                .build();
        Changes changes = javers.findChanges(jqlQuery);
        JqlQuery jqlQueryCount = queryBuilder.limit(Integer.MAX_VALUE).build();
//        int totalRecords = javers.findSnapshots(jqlQueryCount).size();
        int totalRecords = javers.findSnapshots(QueryBuilder.anyDomainObject().limit(Integer.MAX_VALUE).build()).size();
        HashMap<String, Object> response = auditTrailService.getAllAuditTrails(changes, pageIndex, pageSize);
        response.put("totalRecords", totalRecords);
        return apiResponseController.apiResponse(CommonConstants.SUCCESS, response);

    }
    @PostMapping("/byModule")
    public GenericDataDTO getAuditTrailsByModule(@RequestBody AuditSearchPojo auditSearchPojo, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Changes changes = javers.findChanges(QueryBuilder.anyDomainObject().build());
            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
            paginationRequestDTO.setPage(auditSearchPojo.getPageIndex());
            paginationRequestDTO.setPageSize(auditSearchPojo.getPageSize());
            PageableResponse<GenericDataDTO> page;
            if (auditSearchPojo.getStartDate() != null && auditSearchPojo.getEndDate() != null) {
                page = auditTrailService.searchAuditTrailsByModule(changes, auditSearchPojo.getModuleName(), auditSearchPojo.getEntityName(), paginationRequestDTO, auditSearchPojo.getStartDate().atStartOfDay(), auditSearchPojo.getEndDate().atStartOfDay(), request);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch audit by Start-End Date " + auditSearchPojo.getEntityName() + LogConstants.REQUEST_BY  + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + HttpStatus.OK.value());
            } else {
                page = auditTrailService.searchAuditTrailsByModule(changes, auditSearchPojo.getModuleName(), auditSearchPojo.getEntityName(), paginationRequestDTO, null, null, request);
            }
            Map<String, Object> response = new HashMap<>();
            if (page.getData().isEmpty()) {
                response.put(NotificationConstants.MESSAGE, "No record found with the given event type , entityName : " + auditSearchPojo.getEntityName());
                genericDataDTO.setResponseCode(NotificationConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No record found with the given event type , entityName : " + auditSearchPojo.getEntityName());
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to  Fetch audit data by entity name " + auditSearchPojo.getEntityName() + LogConstants.REQUEST_BY + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + HttpStatus.OK.value());
            } else {
                genericDataDTO.setData(page.getData());
                genericDataDTO.setResponseCode(200);
                genericDataDTO.setResponseMessage("Record found sucessfully");
                genericDataDTO.setTotalRecords(page.getTotalRecords());
                genericDataDTO.setCurrentPageNumber(page.getCurrentPage());
                genericDataDTO.setTotalPages(page.getTotalPages());
                genericDataDTO.setTotalPages(page.getTotalPages());
            }
            return genericDataDTO;
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Exception : " + e.getMessage());
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

        return apiResponseController.apiResponse(CommonConstants.SUCCESS, response);

    }

    @GetMapping("/search/operation")
    public ResponseEntity<?> getAuditTrailsByOperation(@RequestParam(required = true) String operation, @RequestParam(required = true) Integer pageIndex,
                                                       @RequestParam(required = true) Integer pageSize) {
        Changes changes = javers.findChanges(QueryBuilder.anyDomainObject().build());

        HashMap<String, Object> response = auditTrailService.getAuditTrailsByOperation(changes, operation, pageIndex, pageSize);

        return apiResponseController.apiResponse(CommonConstants.SUCCESS, response);

    }
    @CrossOrigin(origins = "*")
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAuditTrailsByfilter(@RequestBody PaginationRequestDTO dto) {
        try {
            List<Object> obj = auditTrailService.getChangesFromDto(dto);
            Changes changes = (Changes) obj.get(0);
            HashMap<String, Object> response = auditTrailService.getAllAuditTrails(changes, dto.getPage(), dto.getPageSize());
            response.put("totalRecords", (int)obj.get(1));
            if (changes == null || changes.isEmpty()) {
                log.debug("No records found in Audit found");
                return ResponseEntity.ok(
                        Response.builder()
                                .responseTime(LocalDateTime.now())
                                .status(HttpStatus.NO_CONTENT)
                                .statusCode(HttpStatus.NO_CONTENT.value())
                                .message("No records found.")
                                .data(null)
                                .build()
                );
            } else {
                log.debug("Audit fetched successfully");
                return ResponseEntity.ok(
                        Response.builder()
                                .responseTime(LocalDateTime.now())
                                .status(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .message("Total Audits Found: " + (int)obj.get(1))
                                .data(response)
                                .build()
                );
            }
        }catch (ClassNotFoundException e) {
            log.error("An error occurred while searching for AuditByEntityType: {}");
            return ResponseEntity.ok(
                    Response.builder()
                            .responseTime(LocalDateTime.now())
                            .status(HttpStatus.NO_CONTENT)
                            .statusCode(HttpStatus.NO_CONTENT.value())
                            .message("Please Enter Valid Entity Name.")
                            .data(null)
                            .build()
            );
        }
        catch (Exception e) {
            log.error("An error occurred while searching for AuditByEntityType: {}");
            return ResponseEntity.ok(
                    Response.builder()
                            .responseTime(LocalDateTime.now())
                            .status(HttpStatus.EXPECTATION_FAILED)
                            .statusCode(HttpStatus.EXPECTATION_FAILED.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }
    public int getLoggedInMvnoId() {
        int loggedInMvnoId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInMvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            loggedInMvnoId = -1;
        }
        return loggedInMvnoId;
    }

}
