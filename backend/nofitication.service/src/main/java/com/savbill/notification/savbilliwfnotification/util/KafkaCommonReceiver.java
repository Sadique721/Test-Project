package com.savbill.notification.savbilliwfnotification.util;

import com.savbill.notification.savbilliwfnotification.dto.CommonNotificationDto;
import com.savbill.notification.savbilliwfnotification.dto.EventTemplateBindingDTO;
import com.savbill.notification.savbilliwfnotification.dto.GeneratePasswordDto;
import com.savbill.notification.savbilliwfnotification.dto.NotificationResponseDTO;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * The type Receiver.
 */
@Component
@Profile("kafka")
@EnableAsync
@Slf4j
public class KafkaCommonReceiver {
    private final Map<Class<?>, Consumer<Object>> commonHandler = new HashMap<>();
    //    private Logger log = LoggerFactory.getLogger(KafkaCommonReceiver.class);
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
    private ConcurrentLinkedQueue<Object> commonQueue = new ConcurrentLinkedQueue<>();
    private ExecutorService executorService;
    private ObjectMapper objectMapper = new ObjectMapper();


    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(numThreads);
    }

    @KafkaListener(topics = NotificationConstants.KAFKA_COMMON_NOTIFICATION_TOPIC, containerFactory = "commonKafkaListenerContainer", groupId = NotificationConstants.COMMMON_NOTIFICATION_GROUP)
    public void receive(ConsumerRecord<String, Object> record) {
        try {
            Object value = record.value();
            if (value != null) {
                commonQueue.add(value);
                processCommonQueueAsync();
            }
        } catch (Exception e) {
            log.error("Kafka Received Payload Failed " + e.getMessage());
        }
    }

    private void processCommonQueueAsync() {
        CompletableFuture.runAsync(() -> {
            while (!commonQueue.isEmpty()) {
                Object value = commonQueue.poll(); // Retrieve and remove
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


    //generic method to process any dto
    private void processNotification(Object value) {
        try {
            // Assuming `value` is a Map or JSON string
            /** if value is String or any other dto/object then it is serialize into Json Format */
            String jsonValue = value instanceof String ? (String) value : objectMapper.writeValueAsString(value);
            boolean handlerFound = false;
            // Iterate over the registered handlers
            for (Map.Entry<Class<?>, Consumer<Object>> entry : commonHandler.entrySet()) {
                Class<?> dtoClass = entry.getKey();
                try {
                    /** Try to deserialize the JSON into the DTO class*/
                    Object dto = objectMapper.readValue(jsonValue, dtoClass);
                    /** If deserialization is successful, use the handler and after accept dto it dynamically call handler method */
                    entry.getValue().accept(dto);
                    handlerFound = true;
                    log.info("Processed notification with DTO: " + dtoClass.getName());
                    break;
                } catch (Exception e) {
                    log.error("Failed to deserialize JSON to " + dtoClass.getName() + ": " + e.getMessage());
                }
            }

            if (!handlerFound) {
                log.warn("No handler found for the provided value: " + jsonValue);
            }
        } catch (Exception e) {
            log.error("Failed to process notification: " + e.getMessage());
        }
    }

    @PostConstruct
    public void intializeHandlers() {
        commonHandler.put(GeneratePasswordDto.class, dto -> receiveGenereatePassword((GeneratePasswordDto) dto));
    }

    /**
     * Method: Received From IndexCoordination
     *
     * @param dto the dto
     */
    public void receiveGenereatePassword(GeneratePasswordDto dto) {
//        log.info("receive payload :: receiveFromIndexCoordination() => '{}'", dto);
        try {
            Event event = eventRepository.findById(dto.getEventId()).orElse(null);
            Template template = templateRepository.findByEvent_EventIdAndServiceTypeContainingIgnoreCase(event.getEventId(), NotificationConstants.ServiceType.SERVICE_TYPE_IWF).orElse(null);
            if (template != null) {
                if (template.getIsEmailTemplate()) {
                    if (event != null) {
                        /** Call Get Event Template Bind By Id  Method */
                        EventTemplateBindingDTO eventTemplateBindingDTO = iwfEventTempBindService.getEventTempBindById(event.getEventId());
                        if (dto.getEventId() != null && dto.getEventName() != null && !dto.getEventName().equalsIgnoreCase("")) {
                            eventTemplateBindingDTO.getEventName();
                        }
                        packetProcessing.commonReleaseNotification(dto, eventTemplateBindingDTO, event);
                    }
                }
                if (template.getIsSMSTemplate()) {
                    sendSms(dto, event);
                }
            }
        } catch (Exception e) {
            log.error("Failed to recived notification response dto from index-coordination with exception: {}", e.getMessage());
        }

    }


    // common handler for handling all types of dtos

    /**
     * Send sms iwf.
     *
     * @param dto   the dto
     * @param event the event
     */
    public void sendSms(Object dto, Event event) {
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

}
