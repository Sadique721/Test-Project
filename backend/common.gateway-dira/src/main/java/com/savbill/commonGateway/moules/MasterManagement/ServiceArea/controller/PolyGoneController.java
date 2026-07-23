package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.LogConstants;
import com.savbill.commonGateway.constants.UrlConstants;
import com.savbill.commonGateway.core.controller.ExBaseAbstractController;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.PolyGone;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.PolyGoneDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.PolyGoneRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.repository.ServiceAreaRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service.PolygoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.POLYGONE)
public class PolyGoneController extends ExBaseAbstractController<PolyGoneDTO> {

    @Autowired
    private PolygoneService polygoneService;


    @Autowired
    private PolyGoneRepository polyGoneRepository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private Tracer tracer;

    private final Logger LOGGER = LoggerFactory.getLogger(ServiceAreaController.class);

    public PolyGoneController(PolygoneService service) {
        super(service);
    }


    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Override
    public GenericDataDTO getAllWithoutPagination( HttpServletRequest req, HttpServletResponse res) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            Integer mvnoId= getMvnoIdFromCurrentStaff();
            List<PolyGone> polyGoneList =  polyGoneRepository.findAllByMvnoid(mvnoId);
            genericDataDTO.setDataList(polyGoneList);
            genericDataDTO.setTotalRecords(polyGoneList.size());
            //    logger.info("Fetching Sevice area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            //   logger.error("Unable to fetch data without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
        }finally {
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }

        return genericDataDTO;
    }



    //@PreAuthorize("validatePermission(\"" + MenuConstants.Masters.SERVICE_AREA + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,HttpServletResponse res) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Search");
        MDC.put("userName", polygoneService.getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        long startTime = System.nanoTime();  // Start measuring
        try{
            genericDataDTO = super.search(page, pageSize, sortOrder, sortBy, filter ,req,res );
            if(genericDataDTO.getDataList().isEmpty()){
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Search Service area By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + polygoneService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_FAILED+ LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }else
                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Search Service area By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + polygoneService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);

        }catch (Exception ex){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Search Service area By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + polygoneService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +APIConstants.EXPECTATION_FAILED +APIConstants.ERROR_MESSAGE+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
            long durationInMs = (System.nanoTime() - startTime) / 1_000_000;
            res.addHeader("Server-Timing", "app;dur=" + durationInMs);
        }
        return genericDataDTO;
    }



    @GetMapping("/findbyServiceAreaId/{serviceAreaId}")
    public GenericDataDTO findbyServiceAreaId(@PathVariable Integer serviceAreaId, HttpServletRequest request){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type","Search");
        MDC.put("userName", polygoneService.getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        MDC.put("spanId",traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            ServiceArea serviceArea = serviceAreaRepository.findById(serviceAreaId.longValue()).orElse(null);
            List<PolyGone> polyGoneList = polyGoneRepository.findAllByServiceAreaIdAndMvnoid(serviceAreaId,serviceArea.getMvnoId());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(polyGoneList);
            genericDataDTO.setTotalRecords(polyGoneList.size());
        }catch (Exception e){
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
    }



    @GetMapping("/getAllPolyGones")
    public GenericDataDTO getAllPolyGoneWithoutPagination() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");

            List<PolyGone> polyGoneList =  polyGoneRepository.findAll();
//            polygoneService.getAllPolyGone(polyGoneList);

            genericDataDTO.setDataList(polygoneService.getAllPolyGone(polyGoneList));
            genericDataDTO.setTotalRecords(polygoneService.getAllPolyGone(polyGoneList).size());
            //    logger.info("Fetching Sevice area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            //   logger.error("Unable to fetch data without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
        }

        return genericDataDTO;
    }


}
