package com.savbill.partnermanagement.modules.ServiceParameters.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.partnermanagement.constants.APIConstants;
import com.savbill.partnermanagement.constants.LogConstants;
import com.savbill.partnermanagement.core.constants.UrlConstants;
import com.savbill.partnermanagement.core.dto.GenericDataDTO;
import com.savbill.partnermanagement.core.utillity.log.ApplicationLogger;
import com.savbill.partnermanagement.modules.ServiceParameters.domain.ServiceParameter;
import com.savbill.partnermanagement.modules.ServiceParameters.service.ServiceParametersService;
import com.savbill.partnermanagement.modules.partner.service.PartnerService;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.SERVICE_PARAMETERS)
public class Serviceparameterscontroller {
    //    public Serviceparameterscontroller(ServiceParametersService service) {
//        super(service);
//    }
    private static String SUBMODULE = " [Serviceparameterscontroller] ";
    @Autowired
    ServiceParametersService serviceParametersService;

    @Autowired
    PartnerService partnerService;

    @Autowired
    private Tracer tracer;

    private final Logger log = Logger.getLogger(Serviceparameterscontroller.class);

    @GetMapping("/all")
    public GenericDataDTO getAllWithoutPagination(HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = getModuleNameForLog() + " [getAllWithoutPagination()] ";
        log.info(getModuleNameForLog() + "--" + "Fetching AllWithoutPagination .Data[" + SUBMODULE.toString() + "]");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", partnerService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            List<ServiceParameter> list = serviceParametersService.findall();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            RESP_CODE = APIConstants.SUCCESS;
            // logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching ALL DATA without pagination" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            // logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching ALL DATA without pagination" + LogConstants.REQUEST_BY + partnerService.getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;

    }

    //    @Override
    public String getModuleNameForLog() {
        return "[Serviceparameterscontroller]";
    }
}
