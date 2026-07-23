package com.savbill.integrationsystem.nms;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.core.utillity.log.ApplicationLogger;
import com.savbill.integrationsystem.nms.entity.ConfigRepocitory;
import com.savbill.integrationsystem.nms.entity.ConfigService;
import com.savbill.integrationsystem.nms.entity.Connfiguration;
import com.savbill.integrationsystem.nms.entity.PaginationDetails;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.CONFIG_CONTROLLER)
public class ConfigController {
    @Autowired
    ConfigService configService;
    private static final Logger logger = LoggerFactory.getLogger("NmsapiController.class");
    public Integer MAX_PAGE_SIZE = 5;

    public Map<String, String> sortColMap = new HashMap<>();

    public PageRequest pageRequest = null;
    public Integer PAGE = 1;
    public Integer PAGE_SIZE = 5;
    public Integer SORT_ORDER = 0;
    public String SORT_BY;
    @Autowired
    private Tracer tracer;
 

    @Autowired
    ConfigRepocitory configRepocitory;


    @PostMapping("/create")
    private GenericDataDTO createConfiguration(@RequestBody Connfiguration connfiguration,HttpServletRequest request){
       GenericDataDTO genericDataDTO=new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "CREATE");
        MDC.put("userName",configService.getLoggedInUser().getUsername());
        MDC.put("traceId",request.getHeader("traceId"));
        MDC.put("spanId",traceContext.spanIdString());
        try{
            Boolean flag=configService.duplicateVerifyName(connfiguration.getName());
            if(flag) {
                configService.createConfiguration(connfiguration);
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("Success");
                logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Create Configuration  "+LogConstants.REQUEST_BY + configService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }else{
                Connfiguration connfiguration1=configService.findByname(connfiguration.getName());
                connfiguration1.setIsdeleted(false);
                configService.updateConfiguration(connfiguration1,request);
            }
        }catch (Exception e){
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage("Error While Creating");
            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Create Configuration "+LogConstants.REQUEST_BY + configService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
            e.getMessage();
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return  genericDataDTO;
    }
    @PutMapping("/update")
    private GenericDataDTO updateConfiguration(@RequestBody Connfiguration connfiguration,HttpServletRequest request){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        MDC.put("type", "UPDATE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName",configService.getLoggedInUser().getUsername());
        MDC.put("traceId",request.getHeader("traceId"));
        MDC.put("spanId",traceContext.spanIdString());
        try{
            configService.updateConfiguration(connfiguration,request);
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Configuration Updated Successfully");
        }catch (Exception e){
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage("Unable to Update Configuration");
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Update Configuration"+ LogConstants.REQUEST_BY + configService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ APIConstants.FAIL);
            e.getMessage();
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/list")
    private ResponseEntity<?> fetchConfiguration(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest request){
        Page<Connfiguration> configlist = null;
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "FETCH");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName",configService.getLoggedInUser().getUsername());
        MDC.put("traceId",request.getHeader("traceId"));
        MDC.put("spanId",traceContext.spanIdString());
        try{
            requestDTO = setDefaultPaginationValues(requestDTO);

            RESP_CODE = APIConstants.SUCCESS;
            configlist= configService.fetchConfiguration(requestDTO.getPage(), requestDTO.getPageSize(),
                    requestDTO.getSortOrder(), requestDTO.getFilters());
            response.put("configlist", configlist);
            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetching Configuration List  "+LogConstants.REQUEST_BY + configService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception e){
            e.getMessage();
            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetching Configuration List"+LogConstants.REQUEST_BY + configService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, configlist);
    }

    @GetMapping("/findById")
    private GenericDataDTO findByIdC(@RequestParam(name = "id", required = true) Long id,HttpServletRequest request){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        MDC.put("type", "FETCH");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName",configService.getLoggedInUser().getUsername());
        MDC.put("traceId",request.getHeader("traceId"));
        MDC.put("spanId",traceContext.spanIdString());
        try{
            genericDataDTO= configService.findById(id);
            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetching Configuration with id "+id+LogConstants.REQUEST_BY + configService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception e){
            e.getMessage();
            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetching Configuration "+LogConstants.REQUEST_BY + configService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +LogConstants.LOG_STATUS_CODE + APIConstants.EXPECTATION_FAILED);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return  genericDataDTO;
    }

    @DeleteMapping("/deleteConfig")
    private GenericDataDTO deleteConfig(@RequestParam(name = "id", required = true) Long id,HttpServletRequest request){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        MDC.put("type", "DELETE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName",configService.getLoggedInUser().getUsername());
        MDC.put("traceId",request.getHeader("traceId"));
        MDC.put("spanId",traceContext.spanIdString());
        try{
            Connfiguration connfiguration=configRepocitory.findById(id).orElse(null);
            genericDataDTO = configService.deleConfig(id);
            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Delete Configuration with name  "+connfiguration.getName()+LogConstants.REQUEST_BY + configService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception e){
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage(e.getMessage());
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Delete Configuration "+ LogConstants.REQUEST_BY + configService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ APIConstants.FAIL);
            e.getMessage();
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return  genericDataDTO;
    }
    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        if (pageSize < MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        if (null != sortColMap && 0 < sortColMap.size()) {
            if (sortColMap.containsKey(sortBy)) {
                sortBy = sortColMap.get(sortBy);
            }
        }

        pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        return pageRequest;
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }

            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(String.valueOf(APIConstants.INTERNAL_SERVER_ERROR), e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }
    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        this.PAGE = 1;//Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        this.PAGE_SIZE = 5; //Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        this.SORT_BY = "id";//clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        this.SORT_ORDER = 0; //Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
        this.MAX_PAGE_SIZE = 100; //Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        if (null == requestDTO.getPage())
            requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize())
            requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy())
            requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder())
            requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {

        }
        return loggedInUser;
    }
}
