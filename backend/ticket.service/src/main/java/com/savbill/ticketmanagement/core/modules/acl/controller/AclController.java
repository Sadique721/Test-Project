//package com.savbill.ticketmanagement.core.modules.acl.controller;
//
//import com.savbill.ticketmanagement.core.controller.ApiBaseController;
//import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
//import com.savbill.ticketmanagement.core.modules.acl.model.AclMenuStructureDTO;
//import com.savbill.ticketmanagement.core.modules.acl.model.RoleACLEntryDTO;
//import com.savbill.ticketmanagement.core.modules.acl.service.AclService;
//import com.savbill.ticketmanagement.core.modules.constants.UrlConstants;
//import com.savbill.ticketmanagement.core.modules.utils.APIConstants;
//import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//@RestController
//@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ACL)
//public class AclController extends ApiBaseController {
//
//    private static String MODULE = " [AclController] ";
//
//    @Autowired
//    AclService aclService;
//
//    @GetMapping(value = "/getModuleOperations")
//    public GenericDataDTO getModuleOperations() throws Exception {
//        String SUBMODULE = MODULE + " [getModuleOperations()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "fetch");
//        try {
//            //Get operations
//
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setDataList(aclService.getModuleOperations());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(1);
//            genericDataDTO.setTotalPages(1);
//            ApplicationLogger.logger.info("getting module operation:  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
//            genericDataDTO.setCurrentPageNumber(1);
//        } catch (Exception ex) {
//            ///ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            ApplicationLogger.logger.error("Unable to fetch MOdule operations :Request : : {{}};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//
//    @GetMapping(value = "/getAclMenu")
//    public ResponseEntity<?> getAclMenu() throws Exception {
//        String SUBMODULE = MODULE + " [getModuleOperations()] ";
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        MDC.put("type", "fetch");
//        try {
//            List<AclMenuStructureDTO> list = aclService.createAclMenuStructure();
//            if (!CollectionUtils.isEmpty(list))
//                response.put("dataList", list);
//            else response.put("dataList", new ArrayList<>());
//            RESP_CODE = APIConstants.SUCCESS;
//            ApplicationLogger.logger.info("Fetching ACL menu:  Response : {{}};message: {}", APIConstants.SUCCESS,RESP_CODE);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, ex.getMessage());
//            ApplicationLogger.logger.error("Unable to fetch ACL menu: {{}};message: {};exception:{}", APIConstants.FAIL,response.get(APIConstants.ERROR_TAG),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response, null);
//    }
//
//    @GetMapping(value = "/getAclEntry")
//    public ResponseEntity<?> getAclEntry(@RequestParam(name = "roleId") Long roleId) throws Exception {
//        String SUBMODULE = MODULE + " [getModuleOperations()] ";
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        MDC.put("type", "fetch");
//        try {
//            List<RoleACLEntryDTO> list = aclService.fetchRoleAclEntryByRoleId(roleId);
//            if (!CollectionUtils.isEmpty(list))
//                response.put("dataList", list);
//            else response.put("dataList", new ArrayList<>());
//            RESP_CODE = APIConstants.SUCCESS;
//            ApplicationLogger.logger.info("Fetching ACL menu:  Response : {{}};message: {}", APIConstants.SUCCESS,RESP_CODE);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, ex.getMessage());
//            ApplicationLogger.logger.error("Unable to fetch ACL menu: {{}};message: {};exception:{}", APIConstants.FAIL,response.get(APIConstants.ERROR_TAG),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response, null);
//    }
//
//    @GetMapping(value = "/getAllRoleOperations")
//    public GenericDataDTO getAllRoleOperations() throws Exception {
//        String SUBMODULE = MODULE + " [getRoleOperations()]";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "fetch");
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setDataList(aclService.getAllRoleOperations());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(1);
//            genericDataDTO.setTotalPages(1);
//            genericDataDTO.setCurrentPageNumber(1);
//            ApplicationLogger.logger.info("Fetching All role operations:  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
//        }
//        catch (Exception ex){
//        //    ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            ApplicationLogger.logger.error("Fetching all role operations Response : {{}};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @GetMapping(value = "/getRoleOperations")
//    public GenericDataDTO getRoleOperations() throws Exception {
//        MDC.put("type", "fetch");
//        String SUBMODULE = MODULE + " [getRoleOperations()]";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String roles = ((LoggedInUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRolesList();
//     //   ApplicationLogger.logger.info(roles);
//
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setDataList(aclService.getRoleOperations(roles));
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(1);
//            genericDataDTO.setTotalPages(1);
//            genericDataDTO.setCurrentPageNumber(1);
//            ApplicationLogger.logger.info("Fetching All role operations:  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
//
//        }
//        catch (Exception ex){
//       //     ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            ApplicationLogger.logger.error("Unable to fetch role operations  : {{}};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @GetMapping(value = "/getMenuStructure")
//    public GenericDataDTO getMenuStructure() throws Exception {
//        MDC.put("type", "fetch");
//        String SUBMODULE = MODULE + " [getRoleOperations()]";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(aclService.getMenuStructure());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(1);
//            genericDataDTO.setTotalPages(1);
//            genericDataDTO.setCurrentPageNumber(1);
//            ApplicationLogger.logger.info("Fetching All Menu Structure:  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
//        }
//        catch (Exception ex){
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            ApplicationLogger.logger.error("Unable to fetch by type  :code:{};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @GetMapping(value = "/getAllMenu")
//    public GenericDataDTO getAllMenu() throws Exception {
//        MDC.put("type", "fetch");
//        String SUBMODULE = MODULE + " [getRoleOperations()]";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(aclService.getAclMenuByOrder());
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(1);
//            genericDataDTO.setTotalPages(1);
//            genericDataDTO.setCurrentPageNumber(1);
//            ApplicationLogger.logger.info("Fetching All Menu :  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
//        }
//        catch (Exception ex){
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            ApplicationLogger.logger.error("Unable to fetch by type  :code:{};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//}
