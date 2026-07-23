package com.savbill.ticketmanagement.core.modules.BusinessVerticals.Controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BUSINESS_VERTICALS)
public class BusinessVerticalsController{ //extends ExBaseAbstractController<BusinessVerticalsDTO>
//{
//    @Autowired
//    AuditLogService auditLogService;
//    private static String MODULE = " [BusinessVerticalsController] ";
//
//    @Autowired
//    private MessageSender messageSender;
//
//    private static final Logger logger= LoggerFactory.getLogger(BusinessUnitController.class);
//    @Autowired
//    BusinessVerticalsService businessVerticalsService;
//
//    public BusinessVerticalsController(BusinessVerticalsService service) {
//        super(service);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[BusinessVerticalController]";
//    }
//
//   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody BusinessVerticalsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        MDC.put("type", "Create");
//        boolean flag = businessVerticalsService.duplicateVerifyAtSave(entityDTO.getVname());
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        if (flag /*&& flagforUcode*/) {
//            dataDTO = super.save(entityDTO, result, authentication, req);
//            BusinessVerticalsDTO businessVerticalsDTO = (BusinessVerticalsDTO) dataDTO.getData();
//            logger.info("BusinessVertical created Successfully With name "+ entityDTO.getVname()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//                    AclConstants.OPERATION_BUSINESS_VERTICALS_ADD, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
//        } else{
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.BUSINESS_VERTICALS_NAME_EXITS);
//            logger.error("Unable to Create Business Verticals with verticals name " +entityDTO.getVname()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody BusinessVerticalsDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        String oldname=businessVerticalsService.getById(entityDTO.getId()).getVname();
//        MDC.put("type", "Update");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = businessVerticalsService.duplicateVerifyAtEdit(entityDTO.getVname(), entityDTO.getId());
//        if (flag) {
//            dataDTO = super.update(entityDTO, result, authentication, req);
//            BusinessVerticalsDTO businessVerticalsDTO = (BusinessVerticalsDTO) dataDTO.getData();
//            if(businessVerticalsDTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//                        AclConstants.OPERATION_BUSINESS_VERTICALS_EDIT, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
//            }
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
//        }
//        return dataDTO;
//    }
//
////    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE + "\")")
////    @Override
////    public GenericDataDTO delete(@RequestBody BusinessVerticalsDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
////        org.slf4j.MDC.put("type", "Delete");
////        GenericDataDTO dataDTO = new GenericDataDTO();
////            dataDTO = super.delete(entityDTO, authentication, req);
////            BusinessVerticalsDTO businessVerticalsDTO = (BusinessVerticalsDTO) dataDTO.getData();
////            if (businessVerticalsDTO != null) {
////                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
////                        AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
////                logger.info("Business Verticals  With name " + entityDTO.getVname() + " is deleted Successfully  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
////        } else {
////            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
////            dataDTO.setResponseMessage(DeleteContant.BUSINESS_VERTICALS_DELETE_EXIST);
////            logger.error("Unable to Delete Bank With name: "+entityDTO.getVname() +"  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value());
////        }
////        org.slf4j.MDC.remove("type");
////        return dataDTO;
////    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody BusinessVerticalsDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Delete");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        dataDTO = super.delete(entityDTO, authentication, req);
//        businessVerticalsService.deleteBusinessVerticalMapping(entityDTO.getId());
//        BusinessVerticalsDTO businessVerticalsDTO = (BusinessVerticalsDTO) dataDTO.getData();
//        if (businessVerticalsDTO != null) {
//            // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//            //  AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
//            logger.info("BusinessVerticals  With name " + entityDTO.getVname() + " is deleted Successfully  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//        } else {
//            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//            dataDTO.setResponseMessage(DeleteContant.REGION_DELETE_EXIST);
//            logger.error("Unable to Delete BusinessVerticals With name: "+entityDTO.getVname() +"  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value());
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
////        @Override
////        public GenericDataDTO getAllWithoutPagination () {
////            return super.getAllWithoutPagination();
////        }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAllWithoutPagination () {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            List<BusinessVerticalsDTO> list = businessVerticalsService.getAllEntities().stream().filter(businessVerticalsDTO -> !businessVerticalsDTO.getIsDeleted() && businessVerticalsDTO.getStatus().equalsIgnoreCase("ACTIVE")).collect(Collectors.toList());
//            genericDataDTO.setDataList(list);
//            genericDataDTO.setTotalRecords(list.size());
//            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage("Failed to load data");
//            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//
//        }
//
//        return genericDataDTO;
//
//    }
//
//        @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW + "\")")
//        @Override
//        public GenericDataDTO getEntityById (@PathVariable String id, HttpServletRequest req) throws Exception {
//            MDC.put("type", "Fetch");
//            GenericDataDTO dataDTO = super.getEntityById(id, req);
//            BusinessVerticalsDTO businessUnitDTO = (BusinessVerticalsDTO) dataDTO.getData();
//            List<Long> region_id = businessUnitDTO.getRegion_id().stream().distinct().collect(Collectors.toList());
//            businessUnitDTO.setRegion_id(region_id);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//                    AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getVname());
//            MDC.remove("type");
//            return dataDTO;
//        }
//
//        @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW + "\")")
//        @Override
//        public GenericDataDTO getAll (@RequestBody PaginationRequestDTO requestDTO){
//            String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            try {
//                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                requestDTO = setDefaultPaginationValues(requestDTO);
//                if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
//
//                    genericDataDTO = businessVerticalsService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
//                            , requestDTO.getPageSize()
//                            , requestDTO.getSortBy()
//                            , requestDTO.getSortOrder()
//                            , requestDTO.getFilters());
//
//                else
//                    genericDataDTO = businessVerticalsService.search(requestDTO.getFilters()
//                            , requestDTO.getPage(), requestDTO.getPageSize()
//                            , requestDTO.getSortBy()
//                            , requestDTO.getSortOrder());
//
//
//                if (null != genericDataDTO) {
//                    //logger.info("Fetching data :  request: { From : {}}; Response : {Code{},Message:{};}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//                    return genericDataDTO;
//                } else {
//                    genericDataDTO = new GenericDataDTO();
//                    genericDataDTO.setDataList(new ArrayList<>());
//                    genericDataDTO.setTotalRecords(0);
//                    genericDataDTO.setPageRecords(0);
//                    genericDataDTO.setCurrentPageNumber(1);
//                    genericDataDTO.setTotalPages(1);
//                    //logger.info("Unable to fetch all Entities   :  request: { module : {}}; Response : {Code{},Message:{};}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                }
//            } catch (Exception ex) {
//                genericDataDTO = new GenericDataDTO();
//                ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//                genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//                genericDataDTO.setTotalRecords(0);
//                logger.error("Unable to fetch all Entities   :  request: { Module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//            }
//            return genericDataDTO;
//        }
//
//        @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW + "\")")
//        public GenericDataDTO search (@RequestParam(required = false, defaultValue = "${request.defaultPage}") List<GenericSearchModel>
//        page
//                , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//                , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//                , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String  sortBy
//                , @RequestBody Integer filter){
//                return businessVerticalsService.search(page, pageSize, sortOrder, sortBy, filter);
//        }
//
//    @PostMapping("/getAllVerticalsByRegion")
//    public GenericDataDTO getAllVerticalsByRegion(@RequestBody List<Long> regionId, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            BusinessVerticalsService businessVerticalsService = SpringContext.getBean(BusinessVerticalsService.class);
//            genericDataDTO.setDataList(businessVerticalsService.getAllVerticalsByRegion(regionId));
//            logger.info("Fetching BussinessVertical list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Branch list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }

}
