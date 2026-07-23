package com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.inventorymanagement.core.constants.MessageConstants;
import com.savbill.inventorymanagement.core.constants.UrlConstants;
import com.savbill.inventorymanagement.core.controller.ExBaseAbstractController;
import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.modules.constants.LogConstant;
import com.savbill.inventorymanagement.security.dto.LoggedInUser;
import com.savbill.inventorymanagement.utils.APIConstants;
import io.swagger.annotations.Api;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(value = "ProductPlanMappingController", description = "REST APIs related to product plan mapping Entity!!!!", tags = "product_plan_mapping")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BASE_INVENTORY_MANAGEMENT_API_URL + UrlConstants.PRODUCT_PLAN_MAPPING)
public class Productplanmappingcontroller extends ExBaseAbstractController<Productplanmappingdto> {
    private static String MODULE = " [Productplanmappingcontroller] ";
    private static final Logger LOGGER = Logger.getLogger(Productplanmappingcontroller.class);
    @Autowired
    private ProductplanmappingService mappingService;
    @Autowired
    Tracer tracer;
    public Productplanmappingcontroller(ProductplanmappingService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[Productplanmappingcontroller]";
    }

    @GetMapping("/getproductfromplan")
    public List<Productplanmapping> getproductfromplan(@RequestParam("id") Long id) throws Exception {
        List<Productplanmapping> list = new ArrayList<>();
        list = mappingService.getallfromplan(id);
        return list;
    }
    // Get Product Category Details By PlanId
    @GetMapping("/getProductCategoryByPlanId")
    public GenericDataDTO getProductCategoryByPlanId(@RequestParam("mappingId") Long mappingId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(mappingService.getProductCategoryByPlanId(mappingId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch ProductCategory By PlanId : " + mappingId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex){
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR +  "Fetch ProductCategory By PlanId : " + mappingId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    // Get Product By PlanId
    @GetMapping("/getProductByPlanId")
    public GenericDataDTO getProductByPlanId(@RequestParam("mappingId") Integer mappingId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, req.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(mappingService.getProductByPlanId(mappingId));
            LOGGER.info(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Product By PlanId : " + mappingId +  LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception ex){
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            LOGGER.error(LogConstant.REQUEST_FROM + req.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Fetch Product By PlanId : " + mappingId + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + ex.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
    //Delete Product Plan and Plan Group Mapping by Plan Group Id and Plan Id
    @DeleteMapping("/deleteProductPlanGroupMapping")
    public GenericDataDTO deleteProductPlanGroupMapping(@RequestParam(name = "planGroupId", required = true) Long planGroupId, @RequestParam (name = "planId", required = true) Long planId, HttpServletRequest request) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext =tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstant.TRACE_ID, request.getHeader(LogConstant.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Productplanmappingdto productplanmappingdto=new Productplanmappingdto();
        try {
            genericDataDTO.setDataList(mappingService.deleteProductPlanGroupMapping(planGroupId, planId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(MessageConstants.DELETE_SUCCESSFUL);
            LOGGER.info(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete PlanGroupMapping "+LogConstant.LOG_BY_NAME+productplanmappingdto.getName() +  LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS + LogConstant.LOG_SUCCESS + LogConstant.LOG_STATUS_CODE + APIConstants.SUCCESS );

        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to deletePlanGroupMappingById with plangroup id " + planGroupId + "and plan id " + planId + ":  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE,e.getMessage());
            LOGGER.error(LogConstant.REQUEST_FROM + request.getHeader("requestFrom") + LogConstant.REQUEST_FOR + "Delete PlanGroupMapping "+LogConstant.LOG_BY_NAME+productplanmappingdto.getName() + LogConstant.REQUEST_BY + getLoggedInUser().getUsername() + LogConstant.LOG_STATUS +LogConstant.LOG_FAILED  + LogConstant.LOG_ERROR + e.getMessage() +  LogConstant.LOG_STATUS_CODE+HttpStatus.NOT_ACCEPTABLE.value());

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    @GetMapping("/getProductPlanMappingDetails")
    public GenericDataDTO getProductPlanMappingDetails(@RequestParam(name = "planId") Integer planId) {
        return mappingService.getProductPlanMappingByPlanId(planId);
    }
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

}
