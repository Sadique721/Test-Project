package com.savbill.commonGateway.moules.auditLog.controller;


import com.savbill.commonGateway.Response.Response;
import com.savbill.commonGateway.common.controller.ApiBaseController;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.NotificationConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import com.savbill.commonGateway.moules.auditLog.model.AuditSearchPojo;
import com.savbill.commonGateway.moules.auditLog.service.AuditTrailService;
import com.savbill.commonGateway.utils.PageableResponse;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.AUDIT_TRAIL)
public class AuditTrailController extends ApiBaseController {

    @Autowired
    Javers javers;

    @Autowired
    AuditTrailService auditTrailService;

    final Logger log = LoggerFactory.getLogger(AuditTrailController.class);



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
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch audit by Start-End Date " + auditSearchPojo.getEntityName()  + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND  + LogConstants.LOG_STATUS_CODE  + HttpStatus.OK.value());
        } else {
            page = auditTrailService.searchAuditTrailsByModule(changes, auditSearchPojo.getModuleName(), auditSearchPojo.getEntityName(), paginationRequestDTO, null, null);
        }
        Map<String, Object> response = new HashMap<>();
        if (page.getData().isEmpty()) {
            response.put(NotificationConstants.MESSAGE, "No record found with the given event type , entityName : " + auditSearchPojo.getEntityName());
            genericDataDTO.setResponseCode(APIConstants.NOT_FOUND);
            genericDataDTO.setResponseMessage("No record found with the given event type , entityName : " + auditSearchPojo.getEntityName());
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to  Fetch audit data by entity name " + auditSearchPojo.getEntityName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()  + LogConstants.LOG_STATUS + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE  + HttpStatus.OK.value());
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
}
