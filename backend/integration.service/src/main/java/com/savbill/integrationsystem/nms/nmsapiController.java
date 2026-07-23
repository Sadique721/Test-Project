package com.savbill.integrationsystem.nms;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.savbill.integrationsystem.CDATA.Pojo.CdataCustDetailsPojo;
import com.savbill.integrationsystem.CDATA.Services.CdataServices;
import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.security.constants.LogConstants;
import com.savbill.integrationsystem.core.security.dto.LoggedInUser;
import com.savbill.integrationsystem.core.security.jwt.JwtUtil;
import com.savbill.integrationsystem.core.utillity.APIConstants;
import com.savbill.integrationsystem.core.utillity.URLConstants;
import com.savbill.integrationsystem.pojo.NMSServiceActivationDTO;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(URLConstants.BASE_PORTAL_API_URL + URLConstants.NMS_MASTER)
public class nmsapiController {

    @Autowired
    private NmsService nmsService;
    @Autowired
    private  Tracer tracer;
    @Autowired
    JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger("NmsapiController.class");

    @Autowired
    CdataServices cdataServices;

//    @PostMapping("/deactivateDevice")
//    private String deactivateDevice(@RequestHeader("User") String user, @RequestHeader("Password") String password, @RequestHeader("uuid") String uuid,HttpServletRequest request) {
//        String token = null;
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "DELETE");
//        MDC.put("userName",jwtUtil.getLoggedInUser().getUsername());
//        MDC.put("traceId",request.getHeader("traceId"));
//        MDC.put("spanId",traceContext.spanIdString());
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        try {
//            token = nmsService.deacctivate(user, password, uuid);
//            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Delete Service: for user  "+user+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
//        } catch (Exception e) {
//            e.getMessage();
//            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Delete Service: for user  "+user+  LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ APIConstants.FAIL);
//        } finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return token;
//    }

    @PostMapping("/activateService")
    private String activateNMSService( @RequestBody NMSServiceActivationDTO nmsServiceActivationData,HttpServletRequest request){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "CREATE");
        MDC.put("userName",jwtUtil.getLoggedInUser().getUsername());
        MDC.put("traceId",request.getHeader("traceId"));
        MDC.put("spanId",traceContext.spanIdString());
        String result=null;
    try{
        result=nmsService.activateNMSServices(nmsServiceActivationData);
        logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Activate  Service: "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
    }catch (Exception e){
        logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Activate Service:  "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ APIConstants.FAIL);
        e.getMessage();
    }finally {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
    }
        return result;
    }

    @GetMapping("/getUpstreamProfile")
    private GenericDataDTO getUpstreamProfile(@RequestParam String profiletype, HttpServletRequest request){
        MDC.put("type", "Fetch");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try{
            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetch UpastreamProfile : "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return nmsService.getUpstreamBandwidthProfileName(profiletype,getLoggedInUser().getUsername(),getLoggedInUser().getUserId());
        }catch (Exception e){
        e.getMessage();
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage(e.getMessage());
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch Upstream Profile:  "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ APIConstants.FAIL);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    return genericDataDTO;
    }

    @GetMapping("/getDownStreamProfile")
    private GenericDataDTO getDownStreamProfile(@RequestParam String profiletype, HttpServletRequest request){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try{
            genericDataDTO=nmsService.getDownstreamBandwidthProfileName(profiletype,getLoggedInUser().getUsername(),getLoggedInUser().getUserId());
            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Fetch DownStream Profile: "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception e){
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch DownStream Profile:  "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ APIConstants.FAIL);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping("/testAudit")
    private String testAudit(@RequestHeader("User") String user, @RequestHeader("Password") String password,HttpServletRequest request){
        String token = null;
        MDC.put("type", "CREATE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        try{
            token = nmsService.getJwtTokenfromUrl(user,password,"27.34.251.54","446","admin",2,null);
            nmsService.getAllUpstreamBWProfileDetails(token,"HSI","27.34.251.54","446");
            logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Testing Audit: "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception e){
            e.getMessage();
            logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Testing Audit:  "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ APIConstants.FAIL);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return token;
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


    @PostMapping("/CdataProvisioning")
    private String CdataProvisioning(@RequestBody CdataCustDetailsPojo cdataCustDetailsPojo){
       String apiStatus = "";
        MDC.put("type", "CREATE");
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        try{
            return apiStatus = cdataServices.generatePaylodForCDATA(cdataCustDetailsPojo);
            //logger.info(LogConstants.REQUEST_FROM +request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "Testing Audit: "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }catch (Exception e){
            e.getMessage();
            return apiStatus = "Something went wrong !!"+e.getMessage();

           // logger.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Testing Audit:  "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+ APIConstants.FAIL);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

    }


}
