package com.savbill.commonGateway.moules.SettingsManagement.StaffUserLocationMapping;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "StaffUserLocationMappController", description = "CRUD operation on location staff user mapping", tags = "Staffuser-Location-Mapping-Controller")
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL+ UrlConstants.LOCATION_STAFF_MAPPING)
public class StaffUserLocationMappController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffUserLocationMappService.class);

    @Autowired
    StaffUserLocationMappService staffUserLocationMappService;

    @Autowired
    private Tracer tracer;

    @Autowired
    private StaffUserService staffUserService;

    /**
     * API: Create Location Staff Mapping
     * @param staffUserLocationMappingDto
     * @param req
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/createLocationStaffMapping", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            operationId = "createLocationStaffMapping",
            summary = "To simply add location staff user mapping, Call this API",
            description = "createLocationStaffMapping method is HTTP POST mapping so put some location staff user mapping details to save the location staff user mapping object."
    )
    public GenericDataDTO createLocationStaffMapping(@RequestBody StaffUserLocationMappingDto staffUserLocationMappingDto, HttpServletRequest req) throws Exception{
        TraceContext traceContext = tracer.currentSpan().context();
        Integer respCode = APIConstants.FAIL;
        MDC.put("type", "Create");
        MDC.put("userName", staffUserService.getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (staffUserLocationMappingDto != null) {
                genericDataDTO.setData(staffUserLocationMappService.save(staffUserLocationMappingDto));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                        LogConstants.REQUEST_FOR + " create Entity " +
                        LogConstants.LOG_BY_NAME + staffUserLocationMappingDto.getLocationName() +
                        LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() +
                        LogConstants.LOG_STATUS +
                        LogConstants.LOG_SUCCESS +
                        LogConstants.LOG_STATUS_CODE +
                        respCode);
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + " Create Entity " +
                    LogConstants.LOG_BY_NAME + staffUserLocationMappingDto.getLocationName() +
                    LogConstants.REQUEST_BY +
                    LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() +
                    LogConstants.LOG_SUCCESS +
                    LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE +
                    respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
}
