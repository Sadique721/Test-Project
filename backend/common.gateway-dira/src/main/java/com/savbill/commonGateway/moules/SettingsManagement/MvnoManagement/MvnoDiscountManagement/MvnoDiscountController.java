package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoDiscountManagement;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.MVNO_DISCOUNT)
public class MvnoDiscountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MvnoDiscountController.class);

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private MvnoDiscountService mvnoDiscountService;

    @Autowired
    private Tracer tracer;

    @Autowired
    private StaffUserService staffUserService;

    @PostMapping("/save")
    public GenericDataDTO save(@RequestBody MvnoDiscountDTO mvnoDiscountDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        MDC.put("userName",staffUserService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO.setData(mvnoDiscountService.saveMvnoDiscount(mvnoDiscountDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "save Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "save Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PutMapping("/update")
    public GenericDataDTO update(@RequestBody MvnoDiscountDTO mvnoDiscountDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        MDC.put("userName",staffUserService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO.setData(mvnoDiscountService.updateMvnoDiscount(mvnoDiscountDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/all/{mvnoId}")
    public GenericDataDTO fetchAll(@PathVariable(name = "mvnoId") Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        MDC.put("userName",staffUserService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<MvnoDiscountMappingDTO> mvnoDiscountMappingDTOS = mvnoDiscountService.fetchAllMvnoDiscountDetailByMvnoId(mvnoId);
            if(!CollectionUtils.isEmpty(mvnoDiscountMappingDTOS)) {
                genericDataDTO.setDataList(mvnoDiscountService.fetchAllMvnoDiscountDetailByMvnoId(mvnoId));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
            } else {
                genericDataDTO.setData(new ArrayList<>());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("No Data Found!");
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @DeleteMapping("/delete/{mvnoId}")
    public GenericDataDTO update(@PathVariable(name = "mvnoId") Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        MDC.put("userName",staffUserService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO.setData(mvnoDiscountService.deleteAllMvnoDiscountByMvnoId(mvnoId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

}
