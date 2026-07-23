package com.savbill.radius.controller;

import com.savbill.radius.CronJobs.LiveUserSessionPurgeJob;
import com.savbill.radius.CronJobs.MacRetentionJob;
import com.savbill.radius.CronJobs.PostPaidPlanExpireryJob;
import com.savbill.radius.utils.RadiusConstants;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Api(value = "Scheduler", description = "REST APIs related to Scheduler!!!!", tags = "Scheduler", hidden = true)
@RestController
@RequestMapping("/SavbillRadius")
//@Profile("dev") // Only enabled in the "dev" profile
public class SchedulerTestController {
    private static final Logger log = LoggerFactory.getLogger(SchedulerTestController.class);

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private LiveUserSessionPurgeJob liveUserSessionPurgeJob;

    @Autowired
    private MacRetentionJob macRetentionJob;

    @Autowired
    private PostPaidPlanExpireryJob postPaidPlanExpireryJob;

    @GetMapping("/scheduler/liveSessionPurge")
    public ResponseEntity<Map<String, Object>> liveSessionPurgeScheduler(HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        try {
            liveUserSessionPurgeJob.cronJobToPruneLiveSession();
            response.put(RadiusConstants.MESSAGE, "Live Session Prune Job Scheduler Run Successfully.");
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error("Error in liveSessionPurgeScheduler: " + e.getMessage());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/scheduler/macRetention")
    public ResponseEntity<Map<String, Object>> macRetentionScheduler(HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        try {
            macRetentionJob.cronJobForMacRetention();
            response.put(RadiusConstants.MESSAGE, "Mac Retention Scheduler Run Successfully.");
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error("Error in liveSessionPurgeScheduler: " + e.getMessage());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }

    @GetMapping("/scheduler/customerPlanRenewal")
    public ResponseEntity<Map<String, Object>> customerPlanRenewalScheduler(HttpServletRequest request, @RequestParam(name = "quotaResetDateStr", required = true) String quotaResetDateStr) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(RadiusConstants.TYPE, RadiusConstants.TYPE_FETCH);
        try {
            LocalDate quotaResetDate = LocalDate.parse(quotaResetDateStr);
            postPaidPlanExpireryJob.cronJobForPostpaidPlanExpiryWithDate(quotaResetDate);
            response.put(RadiusConstants.MESSAGE, "Customer plan Scheduler Run Successfully.");
            return apiResponseController.apiResponse(RadiusConstants.SUCCESS, response);
        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error("Error in customerPlanRenewalScheduler: " + e.getMessage());
            return apiResponseController.apiResponse(RadiusConstants.FAIL, response);
        } finally {
            MDC.remove(RadiusConstants.TYPE);
            MDC.remove(RadiusConstants.TRACE_ID);
            MDC.remove(RadiusConstants.SPAN_ID);
        }
    }
}
