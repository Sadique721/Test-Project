package com.savbill.inventorymanagement.modules.auditLog.controller;

import com.savbill.inventorymanagement.core.constants.ClientServiceConstant;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import com.savbill.inventorymanagement.core.utillity.log.ApplicationLogger;
import com.savbill.inventorymanagement.modules.ClientService.ClientServiceService;
import com.savbill.inventorymanagement.modules.MasterManagement.Country.CountryService;
import com.savbill.inventorymanagement.modules.acl.constants.AclConstants;
import com.savbill.inventorymanagement.modules.auditLog.model.AuditLogEntryDTO;
import com.savbill.inventorymanagement.modules.auditLog.model.AuditLogSearchRequestDTO;
import com.savbill.inventorymanagement.modules.auditLog.repository.AuditLogRepository;
import com.savbill.inventorymanagement.modules.auditLog.service.AuditLogService;
import com.savbill.inventorymanagement.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.AUDIT_LOG)
public class AuditLogController extends ExBaseAbstractController<AuditLogEntryDTO> {
    private static String MODULE = " [AuditLogController] ";
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private CountryService countryService;
//    @Autowired
//    private StaffUserService staffUserService;
//    @Autowired
//    private PartnerService partnerService;
//    @Autowired
//    private PaymentGatewayService pgService;
    @Autowired
     ClientServiceService  clientServiceSrv;

    public AuditLogController(AuditLogService service,
                              AuditLogRepository auditLogRepository) {
        super(service);
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public String getModuleNameForLog() {
        return " [AuditLogController] ";
    }


    private static final Logger logger= LoggerFactory.getLogger(AuditLogController.class);
    private final AuditLogRepository auditLogRepository;

    //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AUDIT_ALL + "\")")
    @PostMapping("/searchAudit")
    public GenericDataDTO getAuditLogByParam(@RequestBody AuditLogSearchRequestDTO reqDTO) {

        String SUBMODULE = getModuleNameForLog() + " [getAuditLogByParam()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        try {
            if (null == reqDTO) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Request!");
                logger.info("Unable to fetch Auditlog for "+reqDTO.getAuditFor()+" :  request: { Response : {{}}", APIConstants.NULL_VALUE);
                return genericDataDTO;
            }

            PaginationRequestDTO paginationRequestDTO = setDefaultPaginationValues(new PaginationRequestDTO());
            if (null == reqDTO.getPage())
                reqDTO.setPage(paginationRequestDTO.getPage());
            if (null == reqDTO.getPageSize())
                reqDTO.setPageSize(paginationRequestDTO.getPageSize());
            if (null == reqDTO.getSortOrder())
                reqDTO.setSortOrder(paginationRequestDTO.getSortOrder());
            if (null == reqDTO.getSortBy())
                reqDTO.setSortBy(paginationRequestDTO.getSortBy());
            if (null != reqDTO.getPageSize() && reqDTO.getPageSize() > MAX_PAGE_SIZE)
                reqDTO.setPageSize(MAX_PAGE_SIZE);
            logger.info("fetch Auditlog for "+reqDTO.getAuditFor()+" :  request: { Response : {{}}", APIConstants.SUCCESS);
            return auditLogService.getAuditHistoryByRequestParam(reqDTO);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);

            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to fetch Auditlog for"+reqDTO.getAuditFor()+":  request: { Response : {{}};Error :{} ;Exception:{};",HttpStatus.NOT_ACCEPTABLE,APIConstants.ERROR_MESSAGE,ex.getStackTrace());
           return genericDataDTO;
        }
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AUDIT_ALL + "\")")
    @GetMapping("/getListByAuditFor/{auditFor}")
    public GenericDataDTO getAuditForList(@PathVariable String auditFor) {
        String SUBMODULE = getModuleNameForLog() + " [getAuditForList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == auditFor) {
                genericDataDTO.setResponseMessage("Please Provide AuditFor!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info("Unable to fetch Auditlog for "+auditFor+" :  request: { Response : {{}}", APIConstants.NULL_VALUE);
                return genericDataDTO;
            }
            if (null != auditFor) {
//                if (auditFor.equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_COUNTRY))
//                    return GenericDataDTO.getGenericDataDTO(countryService.getCaseListForAuditFor());
//                if (auditFor.equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_EMPLOYEE))
//                    return GenericDataDTO.getGenericDataDTO(staffUserService.getStaffListForAuditFor());
//                if (auditFor.equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_PARTNER))
//                    return GenericDataDTO.getGenericDataDTO(partnerService.getPartnerListForAuditFor());
//                if (auditFor.equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_PAYMENT_GATEWAY))
//                    return GenericDataDTO.getGenericDataDTO(pgService.getPGListForAuditFor());
                logger.info("fetch Auditlog for "+auditFor+" :  request: { Response : {{}}", APIConstants.SUCCESS);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to fetch Auditlog for"+auditFor+":  request: { Response : {{}};Error :{} ;Exception:{};",HttpStatus.NOT_ACCEPTABLE,APIConstants.ERROR_MESSAGE,ex.getStackTrace());
        }
        return genericDataDTO;
    }

    @PostMapping("/getAuditList/{entity_id}")
//    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_SHIFT_LOCATION + "\",\""
//            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_SHIFT_LOCATION+ "\")")
    public GenericDataDTO getAuditForList(@PathVariable Long entity_id,@RequestBody PaginationRequestDTO paginationRequestDTO) {
        String SUBMODULE = getModuleNameForLog() + " [getAuditForList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            if (null == entity_id) {
                genericDataDTO.setResponseMessage("Please Provide entity reference id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info("Unable to fetch Auditlog for "+entity_id+" :  request: { Response : {{}}", APIConstants.NULL_VALUE);
                return genericDataDTO;
            }
           genericDataDTO = auditLogService.getAllEntitiesbyEntityrefId(entity_id, paginationRequestDTO);


        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to fetch Auditlog for"+entity_id+":  request: { Response : {{}};Error :{} ;Exception:{};",HttpStatus.NOT_ACCEPTABLE,APIConstants.ERROR_MESSAGE,ex.getStackTrace());
        }
        return genericDataDTO;
    }
}
