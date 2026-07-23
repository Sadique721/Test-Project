package com.savbill.notification.savbilliwfnotification.service.impl;

import com.savbill.notification.savbilliwfnotification.dto.DataMaster;
import com.savbill.notification.savbilliwfnotification.dto.EventTemplateBindingDTO;
import com.savbill.notification.savbilliwfnotification.dto.MailResponseDTO;
import com.savbill.notification.savbilliwfnotification.service.IwfEmailService;
import com.savbill.notification.savbilliwfnotification.util.TemplateLoader;
import com.savbill.notification.entity.Email;
import com.savbill.notification.entity.EmailConfig;
import com.savbill.notification.entity.Event;
import com.savbill.notification.entity.Template;
import com.savbill.notification.repository.EmailConfigRepository;
import com.savbill.notification.repository.EmailRepository;
import com.savbill.notification.repository.EventRepository;
import com.savbill.notification.repository.TemplateRepository;
import com.savbill.notification.services.EmailConfigService;
import com.savbill.notification.utils.NotificationConstants;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Iwf email service.
 */
@Service
@Slf4j
public class IwfEmailServiceImpl implements IwfEmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private EmailConfigRepository emailConfigRepository;

    @Autowired
    private Configuration configuration;

    @Autowired
    private EmailConfigService emailConfigService;

    @Value("${email.template.dirPath}")
    private String tempDirPath;

    @Value("${email.subject}")
    private String emailSubject;

    /**
     * Method: Extract Plan Text
     *
     * @param htmlContent
     * @return
     */
    private static String extractPlainText(String htmlContent) {
        String withoutPTags = htmlContent.replaceAll("\\<p\\>|\\</p\\>", "");
        String withoutBrTags = withoutPTags.replaceAll("&lt;br /&gt;", "<br />");
        return withoutBrTags;
    }

    /**
     * Method: Create Template Directory
     * Description: Create Template Directory Based on Path
     */
    @PostConstruct
    public void createTemplatesDir() {
        // Create a Path object for the directory path
        Path dirPath = Paths.get(tempDirPath);

        try {
            // Create the directory if it doesn't exist
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
//                log.info("Directory created successfully.");
            } else {
//                log.info("Directory already exists.");
            }
        } catch (IOException e) {
            log.error("Error creating directory: " + e.getMessage());
        }

    }

    /**
     * Method: Get Email Audits with Pagination
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<Email> getEmailAudits(int page, int pageSize, Long mvnoId) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("emailId").descending());
        if (mvnoId == 1) {
            return emailRepository.findAllByServiceTypeContainingIgnoreCase(NotificationConstants.ServiceType.SERVICE_TYPE_IWF, pageable);
        } else {
            return emailRepository.findAllByServiceTypeContainingIgnoreCaseAndMvnoIdIn(NotificationConstants.ServiceType.SERVICE_TYPE_IWF, Arrays.asList(mvnoId, 1L), pageable);
        }
    }

    /**
     * Method: Sent Email Notification based on Event Template Bind
     *
     * @param dataMaster
     */
    @Override
    public void sendEmailTempBindNotification(DataMaster dataMaster) {
        MDC.put(NotificationConstants.TYPE, NotificationConstants.TYPE_CREATE);
        Event event = dataMaster.getEvent();
        EventTemplateBindingDTO eventTemplateBindingDTO = dataMaster.getEventTemplateBindingDTO();
        Map<String, Object> buildEmailBody = new ConcurrentHashMap<>();
        Optional<EmailConfig> emailConfig = emailConfigRepository.findById(eventTemplateBindingDTO.getEmailConfigId());
        try {
            if (event != null && emailConfig != null) {
                // Check SMTP authentication
                if (!emailConfigService.isSmtpAuthenticated(emailConfig.get().isSmtpAuth(), emailConfig.get().getAuthType(), emailConfig.get().getHostServer(), emailConfig.get().getPort(), emailConfig.get().getUserName(), emailConfig.get().getPassword())) {
                    log.error("SMTP authentication failed for user: " + emailConfig.get().getUserName());
                    saveEmailNotificationOnFailure(dataMaster, emailConfig.get());
                    return;
                }
                Properties props = new Properties();
                EmailConfig emailConfigDetails = emailConfig.get();
                props.put(NotificationConstants.AUTH_PARAM, emailConfigDetails.isSmtpAuth());
                if (emailConfigDetails.getAuthType().equalsIgnoreCase(NotificationConstants.START_TLS)) {
                    props.put(NotificationConstants.STARTTLS_PARAM, true);
                } else {
                    props.put(NotificationConstants.SSL_PARAM, true);
                }
                props.put(NotificationConstants.HOST_PARAM, emailConfigDetails.getHostServer());
                props.put(NotificationConstants.PORT_PARAM, emailConfigDetails.getPort());

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfigDetails.getUserName(),
                                emailConfigDetails.getPassword());
                    }
                });

                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(emailConfigDetails.getUserName(), false));

                List<String> toEmailList = Arrays.asList(dataMaster.getMailContent().get(NotificationConstants.EMAIL_ID).toString().split(","));
                InternetAddress[] toAddresses = new InternetAddress[toEmailList.size()];
                for (int i = 0; i < toEmailList.size(); i++) {
                    toAddresses[i] = new InternetAddress(toEmailList.get(i).trim());
                }
                msg.setRecipients(Message.RecipientType.TO, toAddresses);

                if (dataMaster.getMailContent().get(NotificationConstants.ALT_EMAIL) != null) {
                    List<String> ccEmailList = Arrays.asList(dataMaster.getMailContent().get(NotificationConstants.ALT_EMAIL).toString().split(","));
                    InternetAddress[] ccAddresses = new InternetAddress[ccEmailList.size()];
                    for (int i = 0; i < ccEmailList.size(); i++) {
                        ccAddresses[i] = new InternetAddress(ccEmailList.get(i).trim());
                    }
                    msg.setRecipients(Message.RecipientType.CC,
                            ccAddresses);
//                    log.info("Alternative Email is set" + "Alt Email : " + dataMaster.getMailContent().get(NotificationConstants.ALT_EMAIL).toString());
                }

                if (dataMaster.getMailContent().get(NotificationConstants.BCC_EMAIL) != null) {
                    List<String> bccEmailList = Arrays.asList(dataMaster.getMailContent().get(NotificationConstants.BCC_EMAIL).toString().split(","));
                    InternetAddress[] bccAddresses = new InternetAddress[bccEmailList.size()];
                    for (int i = 0; i < bccEmailList.size(); i++) {
                        bccAddresses[i] = new InternetAddress(bccEmailList.get(i).trim());
                    }
                    msg.setRecipients(Message.RecipientType.BCC,
                            bccAddresses);
                }

                msg.setSubject(dataMaster.getSubject());
                msg.setSentDate(new Date());
                dataMaster.setSourceName(NotificationConstants.SENDER);
                /** Call Build Email Body Method Method */
                buildEmailBody = buildEmailBody(dataMaster);

                if (buildEmailBody.get(NotificationConstants.TEMPLATE_NOT_FOUND).toString().equalsIgnoreCase(NotificationConstants.BOOL_TRUE_AS_STR)) {
                    throw new RuntimeException("Template not found !!");
                }
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                String emailContent = buildEmailBody.get(NotificationConstants.EMAIL_CONTENT).toString();

                if (emailContent != null) {
                    String converted = convertTags(emailContent);
                    messageBodyPart.setContent(converted, "text/html");
//                    log.info("Email is set to message body " + " Email : " + buildEmailBody.get(NotificationConstants.EMAIL_CONTENT));
                }

                Multipart multipart = new MimeMultipart();

                multipart.addBodyPart(messageBodyPart);

                msg.setContent(multipart);
                if (buildEmailBody.get(NotificationConstants.EMAIL_CONTENT) != null && !buildEmailBody.get(NotificationConstants.EMAIL_CONTENT).toString().isEmpty()
                        && buildEmailBody.get(NotificationConstants.EMAIL_CONTENT).toString() != "") {
                    Transport.send(msg);
                    log.info("Transport.send method called with the message : " + msg);

                    if (buildEmailBody.get(NotificationConstants.EVENT_ID) != null && !buildEmailBody.get(NotificationConstants.EVENT_ID).toString().isEmpty()) {
                        /** Call Save Email Notification On Success Method */
                        saveEmailNotificationOnSuccess(buildEmailBody.get(NotificationConstants.EMAIL_CONTENT).toString(), dataMaster.getMailContent(),
                                buildEmailBody.get(NotificationConstants.EVENT_ID).toString(), event.getMvnoId(), emailConfigDetails.getUserName());
                    }

                    log.info("Email successfully sent. Message : " + buildEmailBody.get(NotificationConstants.EMAIL_CONTENT).toString()
                            + " and Email Address is : " + dataMaster.getMailContent().get(NotificationConstants.EMAIL_ID).toString());
                }
            } else {
                if (!dataMaster.getIsEmailConfigured().equalsIgnoreCase(NotificationConstants.BOOL_FALSE_AS_STR)) {
                    log.info("Value of 'isEmailConfigured' column is false so email is not going to send");
                } else {
                    log.info("Email id is not valid so email is not going to send");
                }
            }
        } catch (Exception e) {
            /** Call Save Email Notification On Failure Method */
            saveEmailNotificationOnFailure(dataMaster, emailConfig.get());
            e.printStackTrace();
            log.error("Template not found!!");
        } catch (Throwable e) {
            /** Call Save Email Notification On Failure Method */
            saveEmailNotificationOnFailure(dataMaster, emailConfig.get());
            e.printStackTrace();
            log.error("Email failed. Message : " + e.getMessage() + "Email Address :"
                    + dataMaster.getMailContent().get(NotificationConstants.EMAIL_ID).toString());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(NotificationConstants.TYPE);
            try {
//                log.info("Message for Email has been prepared...");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                log.error("Failed to send email notification with exception: {}", e.getMessage());
            }
        }
    }

    /**
     * Convert tags string.
     *
     * @param input the input
     * @return the string
     */
    public String convertTags(String input) {
        // Replace <span> tags with <font> tags
        String output = input.replaceAll("<span class=\"ql-size-small\">(.*?)</span>", "<p><font size=-5>$1</font></p>");
        output = output.replaceAll("<span class=\"ql-size-large\">(.*?)</span>", "<p><font size=+2>$1</font></p>");
        output = output.replaceAll("<span class=\"ql-size-huge\">(.*?)</span>", "<p><font size=+5>$1</font></p>");
        return output;
    }

    /**
     * Method: Save Email Notification On Failure
     *
     * @param data
     * @param emailConfigDetails
     */
    private void saveEmailNotificationOnFailure(DataMaster data, EmailConfig emailConfigDetails) {
        try {
            String eventName = null;
            Long eventId = null;
            Boolean isTemplateNotFound = false;
            if (data.getEventName() != null) {
                eventName = data.getEventName();
            }
            if (data.getEventId() != null) {
                eventId = data.getEventId();
            }
            if (eventId != null && eventName != "" && !eventName.isEmpty()) {
                Optional<Event> optionalEvent = eventRepository.findById(eventId);
                if (optionalEvent.isPresent()) {
                    Email emailVo = new Email();
                    emailVo.setStatus(NotificationConstants.NotificationStatus.FAILURE);
                    emailVo.setEmailAddress(data.getMailContent().get(NotificationConstants.EMAIL_ID).toString());
                    emailVo.setEvent(optionalEvent.get());
                    emailVo.setMvnoId(optionalEvent.get().getMvnoId());
                    emailVo.setEmailSubject(emailSubject);
                    emailVo.setServiceType(NotificationConstants.ServiceType.SERVICE_TYPE_IWF);
                    if (!isTemplateNotFound) {
                        Map<String, Object> buildEmailBody = buildEmailBody(data);
                        if (buildEmailBody.get(NotificationConstants.EMAIL_CONTENT) != null
                                && !buildEmailBody.get(NotificationConstants.EMAIL_CONTENT).toString().isEmpty()
                                && buildEmailBody.get(NotificationConstants.EMAIL_CONTENT).toString() != "") {
                            if (buildEmailBody.get(NotificationConstants.EVENT_ID) != null && !buildEmailBody.get(NotificationConstants.EVENT_ID).toString().isEmpty()
                                    && buildEmailBody.get(NotificationConstants.EVENT_ID).toString() != "") {
                                emailVo.setMessage(buildEmailBody.get(NotificationConstants.EMAIL_CONTENT).toString());
                            }
                        }
                    } else {
                        emailVo.setMessage("Template is not found for event : " + eventName);
                        emailVo.setRemark("Template not found for event : " + eventName);
                        emailVo.setSourceName(emailConfigDetails.getUserName());
                    }
                    // emailVo.setMessage("Email not sent");
                    emailVo.setDate(LocalDateTime.now());
                    emailVo.setSourceName(emailConfigDetails.getUserName());
                    emailVo.setEmailConfigId(optionalEvent.get().getEmailConfigId());
                    emailRepository.save(emailVo);
                    log.info("Saving email notification on failure for event: {}, sender email: {}, receiver email: {}",
                            optionalEvent.get().getEventName(),
                            emailConfigDetails.getUserName(),
                            optionalEvent.get().getToEmailId());
                }
            }
        } catch (Throwable e) {
            log.error("Failed to save email notification on failure with exception: {}", e.getMessage());
        }
    }

    /**
     * Method: Build Email Body Based on Event Name and Content Type
     *
     * @param data
     * @return
     */
    private Map<String, Object> buildEmailBody(DataMaster data) {
        Map<String, Object> returnData = new ConcurrentHashMap<>();
        try {
            String emailContent = "";
            Optional<Event> eventOptional = eventRepository.findById(data.getEventId());
            Optional<Template> templateOptional = templateRepository.findByEvent_EventIdAndServiceTypeContainingIgnoreCase(eventOptional.get().getEventId(), NotificationConstants.ServiceType.SERVICE_TYPE_IWF);
            if (eventOptional.isPresent()) {
                if (eventOptional.get().getEventName().equalsIgnoreCase(NotificationConstants.BASIC_MAIL_SENDING)) {
                    /** Call Build Basic Mail Send Message Method */
                    returnData = buildBasicMailSendMessage(data, emailContent, eventOptional, data.getEmailTemplate(), data.getSourceName());
                } else if (eventOptional.get().getEventName().equalsIgnoreCase(NotificationConstants.MAIL_SENDING_WITH_DYNAMIC_FORMATTING)) {
                    /** Call Build HTMLBased Email Content Method */
                    returnData = buildHTMLBasedEmailContent(data, eventOptional);
                } else {
                    if (templateOptional.get().getContentType().equalsIgnoreCase(NotificationConstants.ContentType.CONTENT_TYPE_FTL_BASED)) {
                        /** Call Build FTL Mail Sent Message Method */
                        returnData = buildFTLMailSendMessage(data, eventOptional.get(), templateOptional.get());
                    } else if (templateOptional.get().getContentType().equalsIgnoreCase(NotificationConstants.ContentType.CONTENT_TYPE_MANUAL)) {
                        /** Call Manual/ Test Mail Send Message Method */
                        returnData = buildManualMailSendMessage(data, eventOptional.get(), templateOptional.get());
                    }
                }
            }
        } catch (Throwable e) {
            log.error("Failed to build email body with exception: {}", e.getMessage());
        }
        return returnData;
    }

    /**
     * Method: Build Message Contain Based on FTL file Content
     *
     * @param data     the data
     * @param event    the event
     * @param template the template
     * @return map
     * @throws IOException       the io exception
     * @throws TemplateException the template exception
     */
    public Map<String, Object> buildFTLMailSendMessage(DataMaster data, Event event, Template template) throws IOException, TemplateException {
        Map<String, Object> returnData = new ConcurrentHashMap<>();
        String emailTemplate = null;
        if (event != null) {
            if (template != null) {
                byte[] decodedBytes = Base64.getDecoder().decode(template.getContent());
                String decodedTest = new String(decodedBytes, StandardCharsets.UTF_8);
                freemarker.template.Template template1 = new freemarker.template.Template("template_formate", decodedTest, configuration);
                StringWriter writer = new StringWriter();
                template1.process(data.getMailContent(), writer);
                emailTemplate = writer.toString();
                if (decodedTest != null && !decodedTest.equalsIgnoreCase("")) {
                    returnData.put(NotificationConstants.EMAIL_CONTENT, emailTemplate);
                    returnData.put(NotificationConstants.EVENT_ID, event.getEventId());
                    returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, false);
                    returnData.put(NotificationConstants.ContentType.CONTENT_TYPE_FTL_BASED, template.getContentType());
                } else {
                    returnData.put(NotificationConstants.EMAIL_CONTENT, "");
                    returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
                }
            } else {
                returnData.put(NotificationConstants.EMAIL_CONTENT, "");
                returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
            }
        } else {
            returnData.put(NotificationConstants.EMAIL_CONTENT, "");
            returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
        }
        return returnData;
    }

    /**
     * Method: Build Message Contain Based on Manual Content
     *
     * @param data     the data
     * @param event    the event
     * @param template the template
     * @return map
     * @throws IOException       the io exception
     * @throws TemplateException the template exception
     */
    public Map<String, Object> buildManualMailSendMessage(DataMaster data, Event event, Template template) throws IOException, TemplateException {
        Map<String, Object> returnData = new ConcurrentHashMap<>();
        String emailTemplate = null;
        if (event != null) {
            if (template != null) {
                String htmlContent = template.getEmailTemplateData();
                /** Call Extract Plan Text Method for replace tags*/
                String plainText = extractPlainText(htmlContent);
                freemarker.template.Template template1 = new freemarker.template.Template("template_formate", plainText, configuration);
                StringWriter writer = new StringWriter();
                template1.process(data.getMailContent(), writer);
                emailTemplate = writer.toString();
//                log.info("Befor Email Template: {}", emailTemplate);
                if (data.getAppName().equalsIgnoreCase(NotificationConstants.ApplicationName.ENRICHMENT_APPLICATION)) {
                    /** Replace packet attributes in template*/
                    List<MailResponseDTO> attributes = (List<MailResponseDTO>) data.getMailContent().get("filteredAttributes");
                    for (MailResponseDTO attr : attributes) {
                        if (emailTemplate.contains("{" + attr.getHeader() + "}")) {
                            emailTemplate = emailTemplate.replaceAll("\\{" + attr.getHeader() + "\\}",
                                    attr.getContent().toString());
//                            log.info("After Enrichment Email Template: {}", emailTemplate);
                        }
                    }
                }
                if (data.getAppName().equalsIgnoreCase(NotificationConstants.ApplicationName.COLLECTION_APPLICATION)) {
                    Map<String, Object> manualMailContent = data.getManualMailContent();
                    if (manualMailContent != null) {
                        emailTemplate = processAttribute(emailTemplate, manualMailContent, NotificationConstants.NotificationAttributes.SOURCE_IP, "sourceIp");
                        emailTemplate = processAttribute(emailTemplate, manualMailContent, NotificationConstants.NotificationAttributes.LOCATIONNAME, "locationName");
                        emailTemplate = processAttribute(emailTemplate, manualMailContent, NotificationConstants.NotificationAttributes.DEVICETYPE, "deviceType");
                        emailTemplate = replaceAttribute(emailTemplate, manualMailContent, NotificationConstants.NotificationAttributes.DEVICE_DRIVER_NAME);
                        emailTemplate = replaceAttribute(emailTemplate, manualMailContent, NotificationConstants.NotificationAttributes.DEVICE_PORT);
                        emailTemplate = replaceAttribute(emailTemplate, manualMailContent, NotificationConstants.NotificationAttributes.DEVICE_TIME_INTERVAL);
                    }
//                    log.info("After Collection Email Template: {}", emailTemplate);
                }

                if (data.getAppName().equalsIgnoreCase(NotificationConstants.ApplicationName.INDEX_COORDINATION_APPLICATION)) {
                    if (data.getManualMailContent() != null) {
                        if (data.getManualMailContent().get(NotificationConstants.NotificationAttributes.DEVICE_DRIVER) != null) {
                            if ("".equals(data.getManualMailContent().get(NotificationConstants.NotificationAttributes.DEVICE_DRIVER))) {
                                emailTemplate = emailTemplate.replaceAll("We're not receiving any kafka records for specific device drivers \\{deviceDriverName}\\.", "");
                            } else {
                                emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.NotificationAttributes.DEVICE_DRIVER + "\\}", data.getManualMailContent().get(NotificationConstants.NotificationAttributes.DEVICE_DRIVER).toString());
                            }
                        }
                        emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.FILE_PATH + "\\}", (String) (data.getManualMailContent().get(NotificationConstants.FILE_PATH)));
                        emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.CURRENT_CHECK_SUM + "\\}", data.getManualMailContent().get(NotificationConstants.CURRENT_CHECK_SUM).toString());
                        emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.NEW_CHECK_SUM + "\\}", data.getManualMailContent().get(NotificationConstants.NEW_CHECK_SUM).toString());
                    }
//                    log.info("After Analytics Email Template: {}", emailTemplate);
                }
                if (NotificationConstants.ApplicationName.APIGATEWAY_COMMON_APPLICATION.equalsIgnoreCase(data.getAppName())) {
                    if (data.getManualMailContent() != null) {
                        String generatePassUrl = (String) data.getManualMailContent().get(NotificationConstants.NotificationAttributes.GENERATE_PASS_URL);
                        String userName = (String) data.getManualMailContent().get(NotificationConstants.NotificationAttributes.PASS_USER_NAME);
                        if (generatePassUrl != null && !generatePassUrl.isEmpty() || userName != null && !userName.isEmpty()) {
                            // Create a masked link in the email content
                            String maskedLink = "<a href=\"" + generatePassUrl + "\">click here</a>";

                            // Replace the placeholder in the email template with the masked link
                            emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.NotificationAttributes.GENERATE_PASS_URL + "\\}", maskedLink);
                            emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.NotificationAttributes.PASS_USER_NAME + "\\}", userName);
                        } else {
                            emailTemplate = emailTemplate.replaceAll("We're not receiving any kafka records for specific generate password urls", "");
                        }
                    }

//                    log.info("After Analytics Email Template: {}", emailTemplate);
                }
                if (emailTemplate != null) {
                    returnData.put(NotificationConstants.EMAIL_CONTENT, emailTemplate);
                    returnData.put(NotificationConstants.EVENT_ID, event.getEventId());
                    returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, false);
                    returnData.put(NotificationConstants.ContentType.CONTENT_TYPE_FTL_BASED, template.getContentType());
                } else {
                    returnData.put(NotificationConstants.EMAIL_CONTENT, "");
                    returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
                }
            } else {
                returnData.put(NotificationConstants.EMAIL_CONTENT, "");
                returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
            }
        } else {
            returnData.put(NotificationConstants.EMAIL_CONTENT, "");
            returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
        }
        return returnData;
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

    /**
     * Method: Build Basic Mail Send Message, If Event Name is Basic Mail Sending
     *
     * @param data          the data
     * @param emailContent  the email content
     * @param eventName     the event name
     * @param emailTemplate the email template
     * @param sourceName    the source name
     * @return map
     */
    public Map<String, Object> buildBasicMailSendMessage(DataMaster data, String emailContent,
                                                         Optional<Event> optionalEvent, String emailTemplate, String sourceName) {
        Map<String, Object> returnData = null;
        try {
            Optional<EmailConfig> emailConfig = emailConfigRepository.findAll().stream().findFirst();
            returnData = new ConcurrentHashMap<>();
            if (optionalEvent.isPresent()) {
                Optional<Template> templateOptional = templateRepository.findByEvent_EventName(optionalEvent.get().getEventName());
                if (templateOptional.isPresent()) {
                    emailTemplate = templateOptional.get().getEmailTemplateData();
                    if (data.getMailContent().get(NotificationConstants.FIRSTNAME) != null) {
                        emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.FIRSTNAME + "\\}", data.getMailContent().get(NotificationConstants.FIRSTNAME).toString());
                    }
                    if (sourceName != null) {
                        emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.SENDER_ENTITY_NAME + "\\}", sourceName);
                    }
                    emailContent = emailTemplate;
                }
            }
            if (emailContent != null) {
                returnData.put(NotificationConstants.EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(data, emailConfig.get());
                returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(NotificationConstants.EMAIL_CONTENT, emailContent);
        } catch (Throwable e) {
            log.error("Failed to build basic mail send message with exception: {}", e.getMessage());
        }
        return returnData;
    }

    /**
     * Build basic collection mail send message map.
     *
     * @param data          the data
     * @param emailContent  the email content
     * @param eventName     the event name
     * @param emailTemplate the email template
     * @param sourceName    the source name
     * @return the map
     */
    public Map<String, Object> buildBasicCollectionMailSendMessage(DataMaster data, String emailContent,
                                                                   Long eventId, String emailTemplate, String sourceName) {
        Map<String, Object> returnData = null;
        try {
            Optional<EmailConfig> emailConfig = emailConfigRepository.findAll().stream().findFirst();
            returnData = new ConcurrentHashMap<>();
            Optional<Event> optionalEvent = eventRepository.findById(eventId);
            if (optionalEvent.isPresent()) {
                Optional<Template> templateOptional = templateRepository.findByEvent_EventName(optionalEvent.get().getEventName());
                if (templateOptional.isPresent()) {
                    emailTemplate = templateOptional.get().getEmailTemplateData();
                    if (data.getManualMailContent() != null) {
                        if (data.getManualMailContent().get(NotificationConstants.NotificationAttributes.SOURCE_IP) != null) {
                            emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.NotificationAttributes.SOURCE_IP + "\\}", data.getManualMailContent().get(NotificationConstants.NotificationAttributes.SOURCE_IP).toString());
                        }
                        emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.NotificationAttributes.DEVICE_DRIVER_NAME + "\\}", data.getManualMailContent().get(NotificationConstants.NotificationAttributes.DEVICE_DRIVER_NAME).toString());
                        emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.NotificationAttributes.DEVICE_PORT + "\\}", data.getManualMailContent().get(NotificationConstants.NotificationAttributes.DEVICE_PORT).toString());
                        emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.NotificationAttributes.DEVICE_TIME_INTERVAL + "\\}", data.getManualMailContent().get(NotificationConstants.NotificationAttributes.DEVICE_TIME_INTERVAL).toString());
                    }
                    if (sourceName != null) {
                        emailTemplate = emailTemplate.replaceAll("\\{" + NotificationConstants.SENDER_ENTITY_NAME + "\\}", sourceName);
                    }
                    emailContent = emailTemplate;
                }
            }
            if (emailContent != null) {
                returnData.put(NotificationConstants.EVENT_ID, optionalEvent.get().getEventId());
            } else {
                saveEmailNotificationOnFailure(data, emailConfig.get());
                returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(NotificationConstants.EMAIL_CONTENT, emailContent);
        } catch (Throwable e) {
            log.error("Failed to build basic collection mail send messag with exception: {}", e.getMessage());
        }
        return returnData;
    }

    /**
     * Method: Build Basic Mail Send Message, If Event Name is Dynamic
     *
     * @param mailData  the mail data
     * @param eventName the event name
     * @return map
     * @throws IOException       the io exception
     * @throws TemplateException the template exception
     */
    public Map<String, Object> buildHTMLBasedEmailContent(DataMaster mailData, Optional<Event> optionalEvent) throws IOException, TemplateException {

        Map<String, Object> returnData = new ConcurrentHashMap<>();
        String emailTemplate = null;
        if (optionalEvent.isPresent()) {
            Optional<Template> templateOptional = templateRepository.findByEvent_EventName(optionalEvent.get().getEventName());
            if (templateOptional.isPresent()) {
                emailTemplate = templateOptional.get().getEmailTemplateData();
                configuration.setTemplateLoader(TemplateLoader.getTemplateLoader(tempDirPath));
//                log.info("Configure seperate dir path for template loading {}", configuration);
                freemarker.template.Template freeMarkerTemp = configuration.getTemplate(NotificationConstants.TEMPLATE_STRUCTURE_FILE);
                String templateDesign = FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerTemp, mailData.getMailContent());

                if (templateDesign != null && !templateDesign.equalsIgnoreCase("")) {
                    emailTemplate = emailTemplate.replace("{dynamicHTML}", templateDesign);
                    returnData.put(NotificationConstants.EMAIL_CONTENT, emailTemplate);
                    returnData.put(NotificationConstants.EVENT_ID, optionalEvent.get().getEventId());
                    returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, false);
                } else {
                    returnData.put(NotificationConstants.EMAIL_CONTENT, "");
                    returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
                }
            } else {
                returnData.put(NotificationConstants.EMAIL_CONTENT, "");
                returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
            }
        } else {
            returnData.put(NotificationConstants.EMAIL_CONTENT, "");
            returnData.put(NotificationConstants.TEMPLATE_NOT_FOUND, true);
        }
        return returnData;
    }

    /**
     * Method: Save Notification on Success
     *
     * @param message
     * @param data
     * @param eventId
     * @param sourceName
     */
    private void saveEmailNotificationOnSuccess(String message, Map<String, Object> data, String eventId, Long mvnoId,
                                                String sourceName) {
        try {
            Long longEventId = Long.parseLong(eventId);
            Email emailVo = new Email();
            emailVo.setEmailAddress(data.get(NotificationConstants.EMAIL_ID).toString());
            Optional<Event> eventVo = eventRepository.findById(longEventId);
            emailVo.setEvent(eventVo.get());
            emailVo.setEmailSubject(emailSubject);
            emailVo.setMessage(message);
            emailVo.setDate(LocalDateTime.now());
            emailVo.setStatus(NotificationConstants.NotificationStatus.SENT);
            emailVo.setSourceName(sourceName);
            emailVo.setMvnoId(mvnoId);
            emailVo.setServiceType(NotificationConstants.ServiceType.SERVICE_TYPE_IWF);
            emailVo.setEmailConfigId(eventVo.get().getEmailConfigId());
            emailRepository.save(emailVo);
            log.info("Saving email notification on success for event: {}, sender email: {}, receiver email: {}",
                    eventVo.get().getEventName(),
                    sourceName,
                    eventVo.get().getToEmailId());

        } catch (Throwable e) {
            log.error("Failed to save email notification on success with exception: {}", e.getMessage());
        }
    }
}
