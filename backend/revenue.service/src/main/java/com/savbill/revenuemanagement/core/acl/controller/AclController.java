package com.savbill.revenuemanagement.core.acl.controller;


//@RestController
//@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ACL)
public class AclController {
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
//    public GenericDataDTO getAclMenu() throws Exception {
//        String SUBMODULE = MODULE + " [getModuleOperations()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "fetch");
//        try {
//            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            List<AclMenuDTO> list = aclService.createAclMenuStructure();
//            genericDataDTO.setDataList(list);
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(1);
//            genericDataDTO.setTotalPages(1);
//            genericDataDTO.setCurrentPageNumber(1);
//            ApplicationLogger.logger.info("Fetching ACL menu:  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            ApplicationLogger.logger.error("Unable to fetch ACL menu: {{}};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
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
}
