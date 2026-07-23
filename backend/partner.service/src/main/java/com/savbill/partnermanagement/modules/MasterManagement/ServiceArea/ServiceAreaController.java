package com.savbill.partnermanagement.modules.MasterManagement.ServiceArea;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.partnermanagement.constants.APIConstants;
import com.savbill.partnermanagement.constants.LogConstants;
import com.savbill.partnermanagement.core.constants.UrlConstants;
import com.savbill.partnermanagement.core.controller.ExBaseAbstractController;
import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.modules.StaffUser.StaffUserService;
import com.savbill.partnermanagement.modules.acl.constants.AclConstants;
import com.savbill.partnermanagement.modules.partner.service.PartnerService;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PARTNER_CONTROLLER + UrlConstants.SERVICE_AREA)
@Api(value = "ServiceAreaController", description = "REST APIs related to Servicearea Entity!!!!", tags = "service_area_controller")
public class ServiceAreaController extends ExBaseAbstractController<ServiceAreaDTO> {

    public ServiceAreaController(ServiceAreaService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ServiceAreaController]";
    }

    @Autowired
    ServiceAreaService serviceAreaService;

    @Autowired
    StaffUserService staffUserService;


    @Autowired
    PartnerService partnerService;

    @Autowired
    private Tracer tracer;

    private final Logger log = Logger.getLogger(ServiceAreaController.class);
    private static String MODULE = " [ServiceAreaController] ";


    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
    @GetMapping("/getAllServiceAreaByStaff")
    public GenericDataDTO getAllServiceAreaByStaff(HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            RESP_CODE = APIConstants.SUCCESS;
            genericDataDTO.setDataList(serviceAreaService.getAllServiceAreaByStaffId());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " fetch All searvicAarea by  staffid: " + serviceAreaService.getAllServiceAreaByStaffId() + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " fetch All searvicAarea by  staffid: " + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //Get StaffIds by ServiceAreas
//    @GetMapping("/getStaffUserByServiceArea")
//    public GenericDataDTO getStaffUserByServiceArea() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(staffUserService
//                    .getStaffUserByServiceArea());
//            genericDataDTO.setTotalRecords(staffUserService
//                    .getStaffUserByServiceArea().size());
//            logger.info("Fetching All Warehouse Without pagination  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Fetch all without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
//        }
//        return genericDataDTO;
//    }
}
