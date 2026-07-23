package com.savbill.notification.savbilliwfnotification.util;

import com.savbill.notification.savbilliwfnotification.dto.*;
import com.savbill.notification.savbilliwfnotification.dto.*;
import com.savbill.notification.savbilliwfnotification.service.IWFSMSService;
import com.savbill.notification.savbilliwfnotification.service.IwfEmailService;
import com.savbill.notification.savbilliwfnotification.service.IwfEventTempBindService;
import com.savbill.notification.entity.*;
import com.savbill.notification.entity.*;
import com.savbill.notification.repository.EventRepository;
import com.savbill.notification.repository.TemplateRepository;
import com.savbill.notification.services.SmsConfigEventTempBindingService;
import com.savbill.notification.services.SmsReceiverEventTempBindingService;
import com.savbill.notification.services.SmsService;
import com.savbill.notification.utils.NotificationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

/**
 * The type Receiver.
 */
@Component
@Profile("kafka")
@EnableAsync
@Slf4j
public class Receiver {

    private CountDownLatch latch = new CountDownLatch(1);
    @Autowired
    private IwfEmailService iwfEmailService;
    @Autowired
    private PacketProcessing packetProcessing;
    @Autowired
    private IwfEventTempBindService iwfEventTempBindService;
    @Autowired
    private EventRepository eventRepository;
    @Value("${email.dest.email}")
    private String toEmail;
    @Value("${email.dest.altEmail}")
    private String altEmail;
    @Value("${email.preconfigure.eventName}")
    private String preconfiguredEvent;
    @Value("${email.subject}")
    private String subject;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private SmsReceiverEventTempBindingService smsReceiverEventTempBindingService;
    @Autowired
    private SmsConfigEventTempBindingService smsConfigEventTempBindingService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private IWFSMSService iwfsmsService;
    @Value("${thread.pool.size}")
    private Integer numThreads;
    private ConcurrentLinkedQueue<NotificationResponseDTO> notificationQueue = new ConcurrentLinkedQueue<>();
    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(numThreads);
    }

    @KafkaListener(topics = "${spring.kafka.topic.registeredTopic}", containerFactory = "consumerFactory", groupId = NotificationConstants.IWF_NOTIFICATION_GROUP)
    public void receive(NotificationResponseDTO value) {
        try {
            if (value != null) {
                notificationQueue.add(value);
                processQueueAsync();
            }
        } catch (Exception e) {
            log.error("Kafka Received Payload Failed " + e.getMessage());
        }
    }

    private void processQueueAsync() {
        CompletableFuture.runAsync(() -> {
            while (!notificationQueue.isEmpty()) {
                NotificationResponseDTO value = notificationQueue.poll(); // Retrieve and remove
                if (value != null) {
                    try {
                        processNotification(value);
                        log.info("Process Notification Successfully");
                    } catch (Exception e) {
                        log.error("Notification not processed successfully, reason is{}", e.getMessage());
                    }
                }
            }
        }, executorService);
    }


    /**
     * Process notification.
     *
     * @param value the value
     */
    public void processNotification(NotificationResponseDTO value) {
        try {
            log.info("received payload='{}'" + value);
            switch (value.getApplicationName()) {
                case NotificationConstants.ApplicationName.ENRICHMENT_APPLICATION:
                    if (value.getPacketAttributes() == null || value.getPacketAttributes().isEmpty())
                        return;
                    recivedFromEnrichment(value);
                    break;
                case NotificationConstants.ApplicationName.COLLECTION_APPLICATION:
                    if (value.getManualMailContent().isEmpty())
                        return;
                    receiveFromCollection(value);
                    break;
                case NotificationConstants.ApplicationName.INDEX_COORDINATION_APPLICATION:
                    if (value.getManualMailContent().isEmpty())
                        return;
                    receiveFromIndexCoordination(value);
                    break;
                default:
                    log.warn("Unknown application name: " + value.getApplicationName());
                    break;
            }
        } catch (Exception e) {
            log.error("Kafka Received Payload Failed " + e.getMessage());
        }
    }

    /**
     * Method: Recived From Enrichment
     *
     * @param dto the dto
     */
    public void recivedFromEnrichment(NotificationResponseDTO dto) {
        log.info("recived payload :: recivedFromEnrichment() => '{}'", dto);
        Event event = eventRepository.findById(dto.getEventId()).orElse(null);
        Template template = templateRepository.findByEvent_EventIdAndServiceTypeContainingIgnoreCase(event.getEventId(), NotificationConstants.ServiceType.SERVICE_TYPE_IWF).orElse(null);
        if (template != null) {
            if (template.getIsEmailTemplate()) {
                if (event != null) {
                    /** Call Get Event Template Bind By Id  Method */
                    EventTemplateBindingDTO eventTemplateBindingDTO = iwfEventTempBindService.getEventTempBindById(event.getEventId());
                    if (dto.getEventName() != null && !dto.getEventName().equalsIgnoreCase("")) {
                        eventTemplateBindingDTO.getEventName();
                    }
                    /** Call Get Next Time Stamp Method */
                    Long currentTimeStamp = packetProcessing.getNextTimeStamp();
                    if ((dto.getPacketAttributes() != null &&
                            !dto.getPacketAttributes().isEmpty()) && eventTemplateBindingDTO != null) {
                        /** Call Evaluate Paket List Method */
                        Boolean isValid = packetProcessing.evaluatePacketList(dto.getPacketAttributes(), eventTemplateBindingDTO);
                        if (isValid) {
                            /** Call Proccess Packet Attribute Method */
                            packetProcessing.processPacketAttributes(dto, currentTimeStamp, eventTemplateBindingDTO, dto.getPacketAttributes(), event);
                        }
                    }
                } else {
                    /** Call Request Preparation Method */
                    DataMaster dataMaster = requestPreparation(dto.getPacketAttributes());
                    /** Call Send Email Notification Method */
                    iwfEmailService.sendEmailTempBindNotification(dataMaster);
                    latch.countDown();
                }
            }
            if (template.getIsSMSTemplate()) {
                sendSmsIwf(dto, event);
            }
        }
    }

    /**
     * Method: Received From Collection
     *
     * @param dto the dto
     */
    public void receiveFromCollection(NotificationResponseDTO dto) {
        log.info("receive payload :: receiveFromCollection() => '{}'", dto);
        try {
            Event event = eventRepository.findById(dto.getEventId()).orElse(null);
            Template template = templateRepository.findByEvent_EventIdAndServiceTypeContainingIgnoreCase(event.getEventId(), NotificationConstants.ServiceType.SERVICE_TYPE_IWF).orElse(null);
            if (template != null) {
                if (template.getIsEmailTemplate()) {
                    if (event != null) {
                        /** Call Get Event Template Bind By Id  Method */
                        EventTemplateBindingDTO eventTemplateBindingDTO = iwfEventTempBindService.getEventTempBindById(event.getEventId());
                        if (dto.getEventName() != null && !dto.getEventName().equalsIgnoreCase("")) {
                            eventTemplateBindingDTO.getEventName();
                        }
                        packetProcessing.releaseNotification(dto, eventTemplateBindingDTO, event);
                    }
                }
                if (template.getIsSMSTemplate()) {
                    sendSmsIwf(dto, event);
                }
            }
        } catch (Exception e) {
            log.error("Failed to recived notification response dto form collection with exception: {}", e.getMessage());
        }

    }

    /**
     * Method: Received From IndexCoordination
     *
     * @param dto the dto
     */
    public void receiveFromIndexCoordination(NotificationResponseDTO dto) {
        log.info("receive payload :: receiveFromIndexCoordination() => '{}'", dto);
        try {
            Event event = eventRepository.findById(dto.getEventId()).orElse(null);
            Template template = templateRepository.findByEvent_EventIdAndServiceTypeContainingIgnoreCase(event.getEventId(), NotificationConstants.ServiceType.SERVICE_TYPE_IWF).orElse(null);
            if (template != null) {
                if (template.getIsEmailTemplate()) {
                    if (event != null) {
                        /** Call Get Event Template Bind By Id  Method */
                        EventTemplateBindingDTO eventTemplateBindingDTO = iwfEventTempBindService.getEventTempBindById(event.getEventId());
                        if (dto.getEventName() != null && !dto.getEventName().equalsIgnoreCase("")) {
                            eventTemplateBindingDTO.getEventName();
                        }
                        packetProcessing.releaseNotification(dto, eventTemplateBindingDTO, event);
                    }
                }
                if (template.getIsSMSTemplate()) {
                    sendSmsIwf(dto, event);
                }
            }
        } catch (Exception e) {
            log.error("Failed to recived notification response dto from index-coordination with exception: {}", e.getMessage());
        }

    }

    /**
     * Method: Request Preparation
     *
     * @param value
     * @return
     */
    private DataMaster requestPreparation(List<PacketAttributeDTO> value) {
        DataMaster dataMaster = new DataMaster();
        dataMaster.setSubject(subject);
        Map<String, Object> obj = new ConcurrentHashMap<>();
        /** Call Convert Map To Mail Response Method with Convert To Map Method*/
        List<MailResponseDTO> mailResponseDTOS = convertMapToMailResponse(convetToMap(value));
        obj.put("filteredAttributes", mailResponseDTOS);
        obj.put("firstName", NotificationConstants.FIRST_NAME_VAL);
        obj.put("emailId", toEmail);
        obj.put("alternativeEmailId", altEmail);

        dataMaster.setMailContent(obj);
        dataMaster.setIsEmailConfigured(NotificationConstants.BOOL_TRUE_AS_STR);
        dataMaster.setIsSmsConfigured(NotificationConstants.BOOL_FALSE_AS_STR);
        dataMaster.setEventName(preconfiguredEvent);
        dataMaster.setActionDate(new Date().toString());
        return dataMaster;
    }

    /**
     * Send sms iwf.
     *
     * @param dto   the dto
     * @param event the event
     */
    public void sendSmsIwf(NotificationResponseDTO dto, Event event) {
//        log.info("***** Send SMS Start *****");
        try {
            if (dto instanceof CommonNotificationDto) {
                CommonNotificationDto commonNotificationDto = (CommonNotificationDto) dto;
                Template template = templateRepository.findByEvent_EventIdAndServiceTypeContainingIgnoreCase(event.getEventId(), NotificationConstants.ServiceType.SERVICE_TYPE_IWF).orElse(null);
                if (template != null && template.getIsSMSTemplate()) {
                    List<SmsReceiverEventTempBinding> smsReceiverEventTempBindingsList = smsReceiverEventTempBindingService.findAllSmsReceiverEventTempBindingByEvent(event.getEventId());
                    List<SmsConfigEventTempBinding> smsConfigEventTempBindingList = smsConfigEventTempBindingService.findAllSmsConfigEventTempBindingByEvent(event.getEventId());
                    if (smsReceiverEventTempBindingsList != null && smsReceiverEventTempBindingsList.size() > 0 && smsConfigEventTempBindingList != null
                            && smsConfigEventTempBindingList.size() > 0) {
                        for (SmsConfigEventTempBinding smsConfigEventTempBinding : smsConfigEventTempBindingList) {
                            SmsConfig smsConfig = smsConfigEventTempBinding.getSmsConfig();
                            for (SmsReceiverEventTempBinding smsReceiverEventTempBinding : smsReceiverEventTempBindingsList) {
                                iwfsmsService.sendIWFSMSNotification(smsReceiverEventTempBinding, smsConfigEventTempBinding, smsConfig, template, event, commonNotificationDto);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to send sms with exception: {}", e.getMessage());
        }
//        log.info("***** Send SMS END *****");
    }

    /**
     * Method: Convert Map To Mail Response
     *
     * @param convetToMap
     * @return
     */
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

    /**
     * Method: Convert to Map
     *
     * @param packetAttributeDTOs the packet attribute dt os
     * @return map
     */
    public Map<String, String> convetToMap(List<PacketAttributeDTO> packetAttributeDTOs) {
        Map<String, String> map = new HashMap<>();
        for (PacketAttributeDTO packetAttribute : packetAttributeDTOs) {
            map.put(packetAttribute.getLabel(), String.valueOf(packetAttribute.getValue()));
        }
        return map;
    }
}
