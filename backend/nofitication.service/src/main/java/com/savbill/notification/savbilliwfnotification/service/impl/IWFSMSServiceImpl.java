package com.savbill.notification.savbilliwfnotification.service.impl;

import com.savbill.notification.savbilliwfnotification.dto.CommonNotificationDto;
import com.savbill.notification.savbilliwfnotification.dto.MailResponseDTO;
import com.savbill.notification.savbilliwfnotification.dto.NotificationResponseDTO;
import com.savbill.notification.savbilliwfnotification.dto.PacketAttributeDTO;
import com.savbill.notification.savbilliwfnotification.service.IWFSMSService;
import com.savbill.notification.entity.*;
import com.savbill.notification.entity.*;
import com.savbill.notification.exceptions.CustomException;
import com.savbill.notification.repository.SmsConfigMappingRepository;
import com.savbill.notification.repository.SmsRepository;
import com.savbill.notification.utils.NotificationConstants;
import com.savbill.notification.utils.ValidateCrudTransactionData;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IWFSMSServiceImpl implements IWFSMSService {
    private final Logger log = LoggerFactory.getLogger(IWFSMSServiceImpl.class);
    @Autowired
    SmsRepository smsRepository;
    @Autowired
    SmsConfigMappingRepository smsConfigMappingRepository;

    @Override
    public void sendIWFSMSNotification(SmsReceiverEventTempBinding smsReceiverEventTempBinding, SmsConfigEventTempBinding smsConfigEventTempBinding, SmsConfig smsConfig, Template template, Event event, CommonNotificationDto dto) {
//        log.info("***** Send IWF SMS Notification Start *****");
        String senderMobileNumber = smsReceiverEventTempBinding.getMobileNumber();
        Map<String, Object> buildSmsBody = new HashMap<>();
        String[] parts = smsReceiverEventTempBinding.getMobileNumber().split("-");
        String code = parts[0];
        String mobileNumber = parts[1];
        String countryCode = "+" + code.substring(1);
        String appendUrl = template.getAppendUrl();
        String smsTemplateData = template.getSmsTemplateData();
        if (dto.getApplicationName().equalsIgnoreCase(NotificationConstants.ApplicationName.ENRICHMENT_APPLICATION)) {
            /** Replace packet attributes in template*/
            Map<String, Object> objectMap = new ConcurrentHashMap<>();
            if (dto instanceof NotificationResponseDTO) {
                NotificationResponseDTO notificationResponseDTO = (NotificationResponseDTO) dto;
                if (notificationResponseDTO.getPacketAttributes() != null && !notificationResponseDTO.getPacketAttributes().isEmpty()) {
//                 *//** Call Convert Map To Mail Response Method With Convert To Map Method*//*
                    List<MailResponseDTO> mailResponseDTOS = convertMapToMailResponse(convetToMap(notificationResponseDTO.getPacketAttributes()));
                    objectMap.put("filteredAttributes", mailResponseDTOS);
                }
            }
            objectMap.put("firstName", NotificationConstants.FIRST_NAME_VAL);
            objectMap.put("SENDER", NotificationConstants.SENDER);
            objectMap.put(NotificationConstants.EMAIL_ID, event.getToEmailId());
            if (event.getCcEmailId() != null) {
                objectMap.put(NotificationConstants.ALT_EMAIL, event.getCcEmailId());
            }
            if (event.getBccEmailId() != null) {
                objectMap.put(NotificationConstants.BCC_EMAIL, event.getBccEmailId());
            }
            List<MailResponseDTO> attributes = (List<MailResponseDTO>) objectMap.get("filteredAttributes");
            for (MailResponseDTO attr : attributes) {
                if (smsTemplateData.contains("{" + attr.getHeader() + "}")) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{" + attr.getHeader() + "\\}",
                            attr.getContent().toString());
                }
            }
        }
        if (dto.getApplicationName().equalsIgnoreCase(NotificationConstants.ApplicationName.COLLECTION_APPLICATION)) {
            Map<String, Object> manualMailContent = dto.getManualMailContent();
            if (manualMailContent != null) {
                smsTemplateData = processAttribute(smsTemplateData, manualMailContent, NotificationConstants.NotificationAttributes.SOURCE_IP, "sourceIp");
                smsTemplateData = processAttribute(smsTemplateData, manualMailContent, NotificationConstants.NotificationAttributes.LOCATIONNAME, "locationName");
                smsTemplateData = processAttribute(smsTemplateData, manualMailContent, NotificationConstants.NotificationAttributes.DEVICETYPE, "deviceType");
                smsTemplateData = replaceAttribute(smsTemplateData, manualMailContent, NotificationConstants.NotificationAttributes.DEVICE_DRIVER_NAME);
                smsTemplateData = replaceAttribute(smsTemplateData, manualMailContent, NotificationConstants.NotificationAttributes.DEVICE_PORT);
                smsTemplateData = replaceAttribute(smsTemplateData, manualMailContent, NotificationConstants.NotificationAttributes.DEVICE_TIME_INTERVAL);
            }
        }
        if (dto.getApplicationName().equalsIgnoreCase(NotificationConstants.ApplicationName.INDEX_COORDINATION_APPLICATION)) {
            if (dto.getManualMailContent() != null) {
                smsTemplateData = smsTemplateData.replaceAll("\\{" + NotificationConstants.NotificationAttributes.DEVICE_DRIVER + "\\}", dto.getManualMailContent().get(NotificationConstants.NotificationAttributes.DEVICE_DRIVER).toString());
                smsTemplateData = smsTemplateData.replaceAll("\\{" + NotificationConstants.FILE_PATH + "\\}", (String) dto.getManualMailContent().get(NotificationConstants.FILE_PATH));
                smsTemplateData = smsTemplateData.replaceAll("\\{" + NotificationConstants.CURRENT_CHECK_SUM + "\\}", dto.getManualMailContent().get(NotificationConstants.CURRENT_CHECK_SUM).toString());
                smsTemplateData = smsTemplateData.replaceAll("\\{" + NotificationConstants.NEW_CHECK_SUM + "\\}", dto.getManualMailContent().get(NotificationConstants.NEW_CHECK_SUM).toString());
            }
        }

        if (dto.getApplicationName().equalsIgnoreCase(NotificationConstants.ApplicationName.APIGATEWAY_COMMON_APPLICATION)) {
            if (dto.getManualMailContent() != null) {
                smsTemplateData = smsTemplateData.replaceAll("\\{" + NotificationConstants.GENERATE_PASS_URL + "\\}", (String) dto.getManualMailContent().get(NotificationConstants.GENERATE_PASS_URL));
                smsTemplateData = smsTemplateData.replaceAll("\\{" + NotificationConstants.PASS_USER_NAME + "\\}", (String) dto.getManualMailContent().get(NotificationConstants.PASS_USER_NAME));
            }
        }

        try {
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(mobileNumber)
                    && mobileNumber.matches("[0-9]+")) {
                if (smsConfig == null) {
                    createFailedSMSAudit(countryCode, mobileNumber, smsConfig, event, template);
                    log.debug("SMS failed with message: {} to moile number: {} from sms url: {}", template.getSmsTemplateData(), smsReceiverEventTempBinding.getMobileNumber(), smsConfig.getSmsUrl());
                    throw new CustomException("all sms config has problem please connect the administator", 417);
                } else {
                    List<SmsConfigMapping> smsConfigMapping = smsConfigMappingRepository.findBySmsConfigIdAndMvnoId(
                            smsConfig.getSmsConfigId(), event.getMvnoId());
                    //buildSmsBody = buildSMSBody(event, template.getSmsTemplateData());
                    buildSmsBody = buildSMSBody(event, smsTemplateData);
                    if ((smsConfig.getSmsUrl()).contains("twilio")) {
                        String accountSid = "";
                        String authToken = "";
                        String fromNo = "";

                        for (SmsConfigMapping smsConfigMappingVo : smsConfigMapping) {
                            switch (smsConfigMappingVo.getParameter()) {
                                case NotificationConstants.ACCOUNT_SID:
                                    accountSid = smsConfigMappingVo.getValue();
                                    break;
                                case NotificationConstants.AUTH_TOKEN:
                                    authToken = smsConfigMappingVo.getValue();
                                    break;
                                case NotificationConstants.FROM_NUMBER:
                                    fromNo = smsConfigMappingVo.getValue();
                                    break;
                            }
                        }
                        if (buildSmsBody.get("SMS_CONTENT") != null && !buildSmsBody.get("SMS_CONTENT").toString().isEmpty()
                                && !Objects.equals(buildSmsBody.get("SMS_CONTENT").toString(), "")) {
                            Twilio.init(accountSid, authToken);
                            if (countryCode.contains(null)) {
                                throw new RuntimeException("Country Code is Null");
                            } else {
                                Message.creator(
                                        new PhoneNumber(countryCode + mobileNumber),
                                        new PhoneNumber(fromNo), buildSmsBody.get("SMS_CONTENT").toString()).create();
                                if (buildSmsBody.get("EVENT_ID") != null
                                        && !buildSmsBody.get("EVENT_ID").toString().isEmpty()
                                        && !Objects.equals(buildSmsBody.get("EVENT_ID").toString(), "")) {
                                    createSuccessSMSAudit(countryCode, mobileNumber, smsConfig, event, template);
                                    log.debug("SMS successfully send with message: {} to moile number: {} from sms url: {}", template.getSmsTemplateData(), smsReceiverEventTempBinding.getMobileNumber(), smsConfig.getSmsUrl());
                                }
                            }
                        }
                    } else {
                        String username = "", password = "", sender = "", type = "", product = "", smsTemplate = "";
                        SSLContext sslContext = SSLContextBuilder.create()
                                .loadTrustMaterial((x509Certificates, s) -> true)
                                .build();
                        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

                        CloseableHttpClient httpclient = HttpClients.custom()
                                .setSSLSocketFactory(sslSocketFactory)
                                .build();
                        String templateKeyName = null;
                        String mobileKeyName = null;
                        String messageKeyName = null;
                        URIBuilder uriBuilder = null;
                        String sendurl = null;
                        HashMap<String, String> smsDynamicPara = new HashMap<>();
                        smsConfigMapping.forEach(smsConfigMapping1 -> smsDynamicPara
                                .put(smsConfigMapping1.getParameter(), smsConfigMapping1.getValue()));
                        // creating a list for keys
                        Set<String> keySet = smsDynamicPara.keySet();
                        ArrayList<String> listOfKeys = new ArrayList<String>(keySet);
                        // creating a list for values
                        Collection<String> values = smsDynamicPara.values();
                        ArrayList<String> listOfValues = new ArrayList<>(values);
                        String getwayUrl = smsConfig.getSmsUrl().replaceAll("\\s", "");
                        StringBuilder sendSmsUrl = new StringBuilder(getwayUrl);
                        uriBuilder = new URIBuilder(getwayUrl);
                        for (int i = 0; i < listOfKeys.size(); i++) {
                            if (i == 0) {
                                sendSmsUrl.append(listOfKeys.get(i)).append("=").append(listOfValues.get(i));
                            } else {
                                sendSmsUrl.append("&").append(listOfKeys.get(i)).append("=")
                                        .append(listOfValues.get(i));
                            }
                            if (listOfValues.get(i).equals("{template}")) {
                                templateKeyName = listOfKeys.get(i);
                            }
                        }
                        if (countryCode != null) {
                            countryCode = countryCode.replace("+", "");
                        } else {
                            countryCode = "";
                        }
                        sendurl = sendSmsUrl.toString();
                        HttpUriRequest httppost;
                        HttpPost httpPost;
                        if (appendUrl != null && template.getIsAppendRequired() != null && template.getIsAppendRequired()) {
                            sendurl = sendurl.replaceAll("\\s", "");
                            sendurl = sendurl.replaceAll("&CountryCode=yes", "");
                            sendurl = sendurl.replaceAll("\\?CountryCode=yes", "?");
                            appendUrl = appendUrl.replace("&template=", "");
                            sendurl = sendurl.replace("{template}", appendUrl);
                            sendurl = sendurl.replace("{mobile}", mobileNumber);
                            sendurl = sendurl.replace("{message}",
                                    URLEncoder.encode(buildSmsBody.get("SMS_CONTENT").toString()));
                        } else {
                            sendurl = sendurl.replaceAll("\\s", "");
                            sendurl = sendurl.replaceAll("&CountryCode=yes", "");
                            sendurl = sendurl.replaceAll("\\?CountryCode=yes", "?");
                            sendurl = sendurl.replace("{mobile}", mobileNumber);
                            sendurl = sendurl.replace("{message}",
                                    URLEncoder.encode(buildSmsBody.get("SMS_CONTENT").toString()));
                            httppost = RequestBuilder.get().setUri(sendurl).build();
                        }
                        httpPost = new HttpPost(sendurl);
                        String requestBody = "{\"key\": \"value\"}";
                        StringEntity requestEntity = new StringEntity(requestBody);
                        httpPost.setEntity(requestEntity);
                        CloseableHttpResponse response = httpclient.execute(httpPost);
                        HttpEntity responseEntity = response.getEntity();
                        String responseBody = EntityUtils.toString(responseEntity);
                        EntityUtils.consume(responseEntity);
                        if (buildSmsBody.get("EVENT_ID") != null && !buildSmsBody.get("EVENT_ID").toString().isEmpty()
                                && buildSmsBody.get("EVENT_ID").toString() != "") {
                            createSuccessSMSAudit(countryCode, mobileNumber, smsConfig, event, template);
                            log.debug("SMS successfully send with message: {} to moile number: {} from sms url: {}", template.getSmsTemplateData(), smsReceiverEventTempBinding.getMobileNumber(), smsConfig.getSmsUrl());
                        }
                    }
                }
            }
        } catch (Exception e) {
            createFailedSMSAudit(countryCode, mobileNumber, smsConfig, event, template);
            log.debug("SMS failed with message: {} to moile number: {} from sms url: {}", template.getSmsTemplateData(), smsReceiverEventTempBinding.getMobileNumber(), smsConfig.getSmsUrl());
        }
//        log.info("***** Send IWF SMS Notification End *****");
    }

    /**
     * Method: Get
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<Sms> getSmsAudits(int page, int pageSize, Long mvnoId) {
        log.info("Started execution of getEmailAudits method");

        log.info("Fetching list of email audits...");

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("smsId").descending());
        if (mvnoId == 1) {
            return smsRepository.findAllByServiceTypeContainingIgnoreCase(NotificationConstants.ServiceType.SERVICE_TYPE_IWF, pageable);
        } else {
            return smsRepository.findAllByServiceTypeContainingIgnoreCaseAndMvnoIdIn(NotificationConstants.ServiceType.SERVICE_TYPE_IWF, Arrays.asList(mvnoId, 1L), pageable);
        }
    }

    private List<MailResponseDTO> convertMapToMailResponse(Map<String, String> convetToMap) {
        List<MailResponseDTO> mailResponseDTOS = new ArrayList<>();
        for (Map.Entry<String, String> value : convetToMap.entrySet()) {
            MailResponseDTO mailResponseDTO = new MailResponseDTO();
            mailResponseDTO.setHeader(value.getKey());
            mailResponseDTO.setContent(value.getValue());
            mailResponseDTOS.add(mailResponseDTO);
        }
        return mailResponseDTOS;
    }

    public Map<String, String> convetToMap(List<PacketAttributeDTO> packetAttributeDTOs) {
        Map<String, String> map = new HashMap<>();
        for (PacketAttributeDTO packetAttribute : packetAttributeDTOs) {
            map.put(packetAttribute.getLabel(), String.valueOf(packetAttribute.getValue()));
        }
        return map;
    }

    private String processAttribute(String template, Map<String, Object> content, String attribute, String placeholder) {
        Object value = content.get(attribute);
        String strValue = value != null ? value.toString() : "";
        if ("".equals(strValue)) {
            return template.replaceAll("We're not receiving any syslogs from the " + placeholder + " \\{" + attribute + "\\}\\.", "");
        } else {
            return template.replaceAll("\\{" + attribute + "\\}", strValue);
        }
    }

    private String replaceAttribute(String template, Map<String, Object> content, String attribute) {
        Object value = content.get(attribute);
        return template.replaceAll("\\{" + attribute + "\\}", value != null ? value.toString() : "");
    }

    public void createFailedSMSAudit(String country_code, String mobile, SmsConfig smsConfig, Event event, Template template) {
//        log.info("***** Create Failed SMS Audit Start *****");
        try {
            Sms sms = new Sms();
            sms.setEventId(event.getEventId());
            sms.setBuId(null);
            sms.setMvnoId(event.getMvnoId());
            sms.setMessage("Sms with :" + template.getSmsTemplateData() + " is failed");
            sms.setCountryCode("+" + country_code);
            sms.setMobileNo(mobile);
            sms.setDate(LocalDateTime.now());
            sms.setStatus(NotificationConstants.NotificationStatus.FAILURE);
            sms.setSourceName(event.getEventName());
            sms.setServiceType(NotificationConstants.ServiceType.SERVICE_TYPE_IWF);
            sms.setEventName(event.getEventName());
            smsRepository.save(sms);
            log.debug("Save SMS Audit with Create Failed Audit");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
//        log.info("***** Create Failed SMS Audit End *****");
    }

    private Map<String, Object> buildSMSBody(Event event, String smsTemplate) {
//        log.info("***** Build SMS Body Start *****");
        Map<String, Object> returnData = new HashMap<>();
        try {
            if (event != null && smsTemplate != null) {
                returnData.put("EVENT_ID", event.getEventId());
                returnData.put("SMS_CONTENT", smsTemplate);
            }
//            log.info("***** Build SMS Body End *****");
        } catch (Exception e) {
            log.debug("Flied to build SMS body with exception: {}", e.getMessage());
        }
        return returnData;
    }

    public void createSuccessSMSAudit(String country_code, String mobileNumber, SmsConfig smsConfig, Event event, Template template) {
//        log.info("***** Create Success SMS Audit Start *****");
        try {
            Sms sms = new Sms();
            sms.setEventId(event.getEventId());
            sms.setBuId(null);
            sms.setMvnoId(event.getMvnoId());
            sms.setMessage("Sms with :" + template.getSmsTemplateData() + " is Success");
            sms.setCountryCode("+" + country_code);
            sms.setMobileNo(mobileNumber);
            sms.setDate(LocalDateTime.now());
            sms.setStatus(NotificationConstants.NotificationStatus.SENT);
            sms.setSourceName(event.getEventName());
            sms.setServiceType(NotificationConstants.ServiceType.SERVICE_TYPE_IWF);
            sms.setEventName(event.getEventName());
            smsRepository.save(sms);
            log.debug("Save SMS Audit with Create Success Audit");
        } catch (Exception e) {
            log.debug("Flied to Save SMS Audit with exception: {}", e.getMessage());
        }
//        log.info("***** Create Success SMS Audit End *****");
    }
}
