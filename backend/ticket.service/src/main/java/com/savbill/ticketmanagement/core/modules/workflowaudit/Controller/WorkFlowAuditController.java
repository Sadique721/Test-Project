package com.savbill.ticketmanagement.core.modules.workflowaudit.Controller;


import com.savbill.ticketmanagement.core.constants.ClientServiceConstant;
import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.dto.PaginationRequestDTO;
import com.savbill.ticketmanagement.core.modules.ClientServ.service.ClientServiceSrv;
import com.savbill.ticketmanagement.core.modules.constants.UrlConstants;
import com.savbill.ticketmanagement.core.modules.utils.APIConstants;
import com.savbill.ticketmanagement.core.modules.workflowaudit.domain.WorkflowAudit;
import com.savbill.ticketmanagement.core.modules.workflowaudit.service.WorkflowAuditService;
import com.savbill.ticketmanagement.core.security.spring.SpringContext;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.WORKFLOW_AUDIT)
public class WorkFlowAuditController {

    private static String MODULE = " [WorkFlowAuditController] ";

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @Autowired
    ClientServiceSrv clientServiceSrv;


    @PostMapping("/list")
    public GenericDataDTO getWorkFlowAuditList(@RequestBody PaginationRequestDTO requestDTO, @RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "eventName") String eventName) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        HashMap<String, Object> response = new HashMap<>();
        Page<WorkflowAudit> workFlowAuditList = null;
        try {
            WorkflowAuditService workflowAuditService = SpringContext.getBean(WorkflowAuditService.class);
            requestDTO = setDefaultPaginationValues(requestDTO);
            if (entityId != null) {
                genericDataDTO = workflowAuditService.getListByCustomerId(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters(), entityId, eventName);
            }
        } catch (Exception ce) {
            ApplicationLogger.logger.error(MODULE + ce.getMessage(), ce);
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage(ce.getMessage());
            ce.printStackTrace();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return genericDataDTO;
    }

    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
        MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        if (null == requestDTO.getPage()) requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize()) requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy()) requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder()) requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }

    @PostMapping("/filter")
    public GenericDataDTO filterCase(@RequestParam(name="filterColumn") String filterColumn,@RequestParam(name="filterValue") String filterValue, @RequestBody PaginationRequestDTO requestDTO) {

        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            WorkflowAuditService workflowAuditService = SpringContext.getBean(WorkflowAuditService.class);
            genericDataDTO=workflowAuditService.filterAudit(filterColumn,filterValue,requestDTO);
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
        }catch (Exception e){
            e.getStackTrace();
            genericDataDTO.setResponseCode(APIConstants.FAIL);
        }
        MDC.remove("type");
        return genericDataDTO;
    }
}
