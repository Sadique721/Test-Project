package com.savbill.commonGateway.moules.acl.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.common.controller.ApiBaseController;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.constants.Constants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.RoleService;
import com.savbill.commonGateway.moules.acl.model.AclMenuStructureDTO;
import com.savbill.commonGateway.moules.acl.model.RoleACLEntryDTO;
import com.savbill.commonGateway.moules.acl.repository.RoleAclRepository;
import com.savbill.commonGateway.moules.acl.service.AclService;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ACL)
public class AclController extends ApiBaseController {

    private static String MODULE = " [AclController] ";

    @Autowired
    private Tracer tracer;

    private Logger LOGGER = LoggerFactory.getLogger(AclController.class);
    @Autowired
    AclService aclService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleAclRepository roleAclRepository;

    @GetMapping(value = "/getModuleOperations")
    public GenericDataDTO getModuleOperations(HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + " [getModuleOperations()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            //Get operations

            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(aclService.getModuleOperations());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch ACL Modules" +  LogConstants.REQUEST_BY +getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch ACL Modules" +  LogConstants.REQUEST_BY +getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
    
    
    @GetMapping(value = "/getAclMenu")
    public ResponseEntity<?> getAclMenu(HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + " [getModuleOperations()] ";
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("username",getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<AclMenuStructureDTO> list = aclService.createAclMenuStructure(Constants.BSS);
            if (!CollectionUtils.isEmpty(list))
                response.put("dataList", list);
            else response.put("dataList", new ArrayList<>());
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All ACL Manu" +  LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch ACL Manu" +  LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response, null);
    }

    @GetMapping(value = "/getCommonAclMenu/{productName}")
    public ResponseEntity<?> getCommonAclMenu(@PathVariable String productName , HttpServletRequest req) throws Exception{
        String SUBMODULE = MODULE + "[getModuleOperations()]";
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        HashMap<String , Object> response = new HashMap<>();
        try{
            List<Integer> menuIds = roleAclRepository.findMenoIdFromRole(getLoggedInUser().getRoleIds().get(0));
            List<AclMenuStructureDTO> list = aclService.createAclMenuStructure(productName,menuIds.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList()));
            if(!CollectionUtils.isEmpty(list))
                response.put("datalist",list);
            else response.put("datalist",new ArrayList<>());
            respCode=APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Common Acl Module" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception ex){
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Common ACL Modules" +  LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response, null);
    }

    @GetMapping(value = "/getAclEntry")
    public ResponseEntity<?> getAclEntry(@RequestParam(name = "roleId", required = false) Long roleId , HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + " [getModuleOperations()] ";
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());        try {
            roleId = getLoggedInRoleId();
            List<RoleACLEntryDTO> list = aclService.fetchRoleAclEntryByRoleId(roleId);
            if (!CollectionUtils.isEmpty(list))
                response.put("dataList", list);
            else response.put("dataList", new ArrayList<>());
            respCode = APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ACL Modules" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch ACL Modules" +  LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response, null);
    }

    @GetMapping(value = "/getAllRoleOperations")
    public GenericDataDTO getAllRoleOperations(HttpServletRequest req) throws Exception {
        String SUBMODULE = MODULE + " [getRoleOperations()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch" );
        MDC.put("userName",getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(aclService.getAllRoleOperations());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " fetch All Acl Role Operation" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }
        catch (Exception ex){
        //    ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All ACl Role Operation" +  LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/getRoleOperations")
    public GenericDataDTO getRoleOperations(HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        int respCode = APIConstants.FAIL;
        MDC.put("type", "fetch");
        MDC.put("userName",getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getRoleOperations()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String roles = ((LoggedInUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRolesList();
     //   ApplicationLogger.logger.info(roles);

        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(aclService.getRoleOperations(roles));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Role Operation"+LogConstants.REQUEST_BY+ getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }
        catch (Exception ex){
       //     ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Role Operation" +  LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/getMenuStructure")
    public GenericDataDTO getMenuStructure(HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch" );
        MDC.put("userName",getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getRoleOperations()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(aclService.getMenuStructure());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " fetch ACl Menu Structure" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);        }
        catch (Exception ex){
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch ACL Menu Structure" +  LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/getAllMenu")
    public GenericDataDTO getAllMenu(HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getFirstName());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getRoleOperations()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(aclService.getAclMenuByOrder());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch All menu" + LogConstants.REQUEST_BY + getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }
        catch (Exception ex){
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch All Menu" +  LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }
}
