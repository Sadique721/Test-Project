package com.savbill.cpm.modules.SubBusinessUnit.Controller;

import com.savbill.cpm.constants.UrlConstants;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SUB_BUSINESS_UNIT)
public class SubBusinessUnitController{ // extends ExBaseAbstractController<SubBusinessUnitDTO> {
//
//    private static final Logger logger= LoggerFactory.getLogger(SubBusinessUnitController.class);
//    @Autowired
//    SubBusinessUnitService subBusinessUnitService;
//    public SubBusinessUnitController(SubBusinessUnitService service) {
//        super(service);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[SubBusinessUnitController]";
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody SubBusinessUnitDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if (getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//
//        MDC.put("type", "Create");
//        boolean flag = subBusinessUnitService.duplicateVerifyAtSave(entityDTO.getSubbuname());
//        GenericDataDTO dataDTO = new GenericDataDTO();
//
//        if (flag) {
//            dataDTO = super.save(entityDTO, result, authentication, req);
//            SubBusinessUnitDTO subBusinessUnitDTO = (SubBusinessUnitDTO) dataDTO.getData();
//            logger.info("SubBusinessUnit created Successfully With name " + entityDTO.getSubbuname() + "  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//        }else{
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.SUB_BUSINESS_UNIT_NAME_EXITS);
//            logger.error("Unable to Create SubBusinessUnit with SubBusiness  name " +entityDTO.getSubbuname()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody SubBusinessUnitDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        MDC.put("type", "Update");
//        String oldName= subBusinessUnitService.getSubBUName(entityDTO.getSubbuname());
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = subBusinessUnitService.duplicateVerifyAtEdit(entityDTO.getSubbuname(),entityDTO.getId());
//        if (flag) {
//            dataDTO = super.update(entityDTO, result, authentication, req);
//            SubBusinessUnitDTO subBusinessUnitDTO = (SubBusinessUnitDTO) dataDTO.getData();
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
//            logger.error("Unable to Update Subbuname "+entityDTO.getSubbuname()+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.SUB_BU);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody SubBusinessUnitDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Delete");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//
//        dataDTO = super.delete(entityDTO, authentication, req);
//        SubBusinessUnitDTO subBusinessUnitDTO = (SubBusinessUnitDTO) dataDTO.getData();
//        if(subBusinessUnitDTO != null) {
//            logger.info("Business Uint With name "+ entityDTO.getSubbuname()+" is Deleted Successsfully :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//        }
//        else {
//            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//            dataDTO.setResponseMessage(DeleteContant.SUB_BUSINESS_UNIT_EXIST);
//            logger.error("Unable to Delete Subbuname Unit With name "+entityDTO.getSubbuname()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.METHOD_NOT_ALLOWED.value(),DeleteContant.BUSINESS_UNIT_EXIST);
//        }
//        MDC.remove("tyqpe");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_UNIT_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }
}
