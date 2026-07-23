package com.savbill.notification.Schedular;

import com.savbill.notification.services.SchedulerLockService;
import com.savbill.notification.entity.SchedulerAudit;
import com.savbill.notification.services.SchedulerAuditService;
import com.savbill.notification.entity.Sms;
import com.savbill.notification.entity.SmsConfig;
import com.savbill.notification.entity.SmsConfigMapping;
import com.savbill.notification.repository.SmsConfigMappingRepository;
import com.savbill.notification.repository.SmsConfigRepository;
import com.savbill.notification.repository.SmsRepository;
import com.savbill.notification.spring.SpringContext;
import com.savbill.notification.utils.NotificationConstants;
import com.twilio.Twilio;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@EnableScheduling
@ConditionalOnProperty(name = "spring.enable.scheduling")
public class SMSScheduler {

    //final Logger log = Logger.getLogger(SMSScheduler.class);
    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Scheduled(cron = "${cronjobtimeforsmsnotification}")
    public void sendSMSScheduler() throws InterruptedException {
        // System.out.println("*******SMS schedular started********");
        try {
            sendsms();
        } catch (Exception e) {
            e.getMessage();
        } finally {
//            System.out.println("******SMS Schedular ended******");
        }

    }

    public void sendsms() throws IOException {
        log.info("XXXXXXXXXXXX----------CRON TIME_FOR_SMS_NOTIFICATION_SCHEDULER START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_SMS_NOTIFICATION_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_SMS_NOTIFICATION)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_SMS_NOTIFICATION);
            try {
                SmsConfigRepository smsConfigRepository = SpringContext.getBean(SmsConfigRepository.class);
                List<SmsConfig> smsConfigList = smsConfigRepository.findAllByConfigStatus(false);
                if (smsConfigList.isEmpty()) {
                    //log.info("All sms config is active nothing changed");
//                   System.out.println("All sms config is active nothing changed");
                } else {
                    for (SmsConfig eachsmsConfig : smsConfigList) {
                        String accountSid = "";
                        String authToken = "";
                        String fromNo = "";
                        Optional<SmsConfig> smsConfig = Optional.ofNullable(eachsmsConfig);
                        SmsConfigMappingRepository smsConfigMappingRepository = SpringContext.getBean(SmsConfigMappingRepository.class);
                        List<SmsConfigMapping> smsConfigMapping = smsConfigMappingRepository
                                .findSmsConfigMappingBySmsConfigId(smsConfig.get().getSmsConfigId());
                        SmsConfig smsConfigDetails = smsConfig.get();
                        if ((smsConfigDetails.getSmsUrl()).contains("twilio")) {
                            for (int i = 0; i < smsConfigMapping.size(); i++) {
                                SmsConfigMapping smsConfigMappingVo = smsConfigMapping.get(i);
                                if (smsConfigMappingVo.getParameter().equals(NotificationConstants.ACCOUNT_SID)) {
                                    accountSid = smsConfigMappingVo.getValue();
                                } else if (smsConfigMappingVo.getParameter().equals(NotificationConstants.AUTH_TOKEN)) {
                                    authToken = smsConfigMappingVo.getValue();
                                } else if (smsConfigMappingVo.getParameter().equals(NotificationConstants.FROM_NUMBER)) {
                                    fromNo = smsConfigMappingVo.getValue();
                                }
                            }
                            Twilio.init(accountSid, authToken);
                            // System.out.println("here is my id:"+message.getSid());
                        } else {
                            String username = "", password = "", sender = "", type = "", product = "", template = "";
                            CloseableHttpClient httpclient = HttpClients.createDefault();
                            try {
                                for (int i = 0; i < smsConfigMapping.size(); i++) {
                                    SmsConfigMapping smsConfigMappingVo = smsConfigMapping.get(i);
                                    if (smsConfigMappingVo.getParameter().equals("username")) {
                                        username = smsConfigMappingVo.getValue();
                                    } else if (smsConfigMappingVo.getParameter().equals("password")) {
                                        password = smsConfigMappingVo.getValue();
                                    } else if (smsConfigMappingVo.getParameter().equals("sender")) {
                                        sender = smsConfigMappingVo.getValue();
                                    } else if (smsConfigMappingVo.getParameter().equals("type")) {
                                        type = smsConfigMappingVo.getValue();
                                    } else if (smsConfigMappingVo.getParameter().equals("product")) {
                                        product = smsConfigMappingVo.getValue();
                                    } else if (smsConfigMappingVo.getParameter().equals("template")) {
                                        template = smsConfigMappingVo.getValue();
                                    }
                                }
                                SmsRepository smsRepository = SpringContext.getBean(SmsRepository.class);
                                List<Sms> failedsmsList = smsRepository.findAllBySmsConfigIdAndMvnoIdAndBuIdAndStatusEqualsIgnoreCaseAndEventIdIsNotNull(smsConfig.get().getSmsConfigId(), smsConfig.get().getMvnoId(), smsConfig.get().getBuId(), "Failed");
                                Sms failedSms = failedsmsList.get(failedsmsList.size() - 1);
                                HttpUriRequest httppost = RequestBuilder.get().setUri(new URI(smsConfigDetails.getSmsUrl()))
                                        .addParameter("username", username).addParameter("password", password)
                                        .addParameter("sender", sender).addParameter("mobile", failedSms.getCountryCode() + failedSms.getMobileNo())
                                        .addParameter("type", type).addParameter("product", product).addParameter("template", template)
                                        .addParameter("message", "test sms send").build();
                                CloseableHttpResponse response = httpclient.execute(httppost);
//                        System.out.println("Send sms URL is  " + httppost);
                                Sms sms = new Sms();
                                sms.setSmsConfigId(smsConfigDetails.getSmsConfigId());
                                sms.setSourceName("Notification Schedular");
                                sms.setMessage("sms config name : " + smsConfigDetails.getSmsUrl() + "Success");
                                sms.setDate(LocalDateTime.now());
                                sms.setMobileNo(failedSms.getMobileNo());
                                sms.setStatus("Sent");
                                sms.setEventId(failedSms.getEventId());
                                sms.setCountryCode(failedSms.getCountryCode());
                                sms.setMvnoId(failedSms.getMvnoId());
                                sms.setBuId(failedSms.getBuId());
                                smsRepository.save(sms);
                                smsConfig.get().setConfigStatus(true);
                                sms.setEventName(failedSms.getEventName());
                                smsConfigRepository.save(smsConfig.get());
                                try {
                                    // System.out.println(EntityUtils.toString(response.getEntity()));
                                } finally {
                                    response.close();
                                }
                            } catch (Throwable e) {
                                SmsRepository smsRepository = SpringContext.getBean(SmsRepository.class);
                                Sms sms = new Sms();
                                List<Sms> failedsmsList = smsRepository.findAllBySmsConfigIdAndMvnoIdAndBuIdAndStatusEqualsIgnoreCaseAndEventIdIsNotNull(smsConfig.get().getSmsConfigId(), smsConfig.get().getMvnoId(), smsConfig.get().getBuId(), "Failed");
                                Sms failedSms = failedsmsList.get(failedsmsList.size() - 1);
                                sms.setSmsConfigId(smsConfigDetails.getSmsConfigId());
                                sms.setSourceName("Notification Schedular");
                                sms.setMessage("sms config name : " + smsConfigDetails.getSmsUrl() + "failed");
                                sms.setDate(LocalDateTime.now());
                                sms.setMobileNo(failedSms.getMobileNo());
                                sms.setStatus("Failed");
                                sms.setEventId(failedSms.getEventId());
                                sms.setCountryCode(failedSms.getCountryCode());
                                sms.setMvnoId(failedSms.getMvnoId());
                                sms.setBuId(failedSms.getBuId());
                                sms.setEventName(failedSms.getEventName());
                                smsRepository.save(sms);
                                throw new RuntimeException(e.getMessage());
                            } finally {
                                httpclient.close();
                            }
                        }
                    }
                }
                System.out.println("***** cronjobtimeforsmsnotification Ended !!! *****");
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("SMS Notification Scheduler Run Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(smsConfigList.size());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_SMS_NOTIFICATION);
                log.info("XXXXXXXXXXXX---------- SMS Notification Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("SMS Notification Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------SMS Notification Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }
}