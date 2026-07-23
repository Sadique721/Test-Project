package com.savbill.notification.services.impl;

import com.savbill.notification.entity.Event;
import com.savbill.notification.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class InventorySmsServiceImpl {

    private static final String EVENT_ID = "eventId";
    private static final String SMS_CONTENT = "smsContent";
    private static final String TEMPLATE_NOT_FOUND = "template not found";

    @Autowired
    EventRepository eventRepository;

    @Autowired
    TemplateServiceImpl templateServiceImpl;

    @Autowired
    SmsServiceImpl smsService;

    public Map<String, Object> buildInventoryAssignmentForStaffMessage(Map<String, Object> data, String smsContent,
                                                         String eventName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);

            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get("employeeName") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{employeeName\\}",
                            data.get("employeeName").toString());
                }
                if (smsTemplateData != null && data.get("assetId") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{assetId\\}",
                            data.get("assetId").toString());
                }
                if (smsTemplateData != null && data.get("serialNumber") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{serialNumber\\}",
                            data.get("serialNumber").toString());
                }
                if (smsTemplateData != null && data.get("assetsSpecification") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{specifications\\}",
                            data.get("assetsSpecification").toString());
                }
                if (smsTemplateData != null && data.get("assignDate") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{assignDate\\}",
                            data.get("assignDate").toString());
                }
                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                smsService.saveSmsNotificationOnFailure(null, data, null, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildInventoryRequestMessage(Map<String, Object> data, String smsContent,
                                                                       String eventName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);

            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get("requestTo") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{requestTo\\}",
                            data.get("requestTo").toString());
                }
                if (smsTemplateData != null && data.get("requester") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{requester\\}",
                            data.get("requester").toString());
                }
                if (smsTemplateData != null && data.get("onBehalfOf") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{onBehalfOf\\}",
                            data.get("onBehalfOf").toString());
                }
                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                smsService.saveSmsNotificationOnFailure(null, data, null, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildInventoryThresholdMessage(Map<String, Object> data, String smsContent,
                                                                       String eventName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);

            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get("productName") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{productName\\}", data.get("productName").toString());
                }
                if (smsTemplateData != null && data.get("warehouseName") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{warehouseName\\}", data.get("warehouseName").toString());
                }
                if (smsTemplateData != null && data.get("currentQty") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{currentQty\\}", data.get("currentQty").toString());
                }
                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                smsService.saveSmsNotificationOnFailure(null, data, null, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildInventoryFulfilmentMessage(Map<String, Object> data, String smsContent,
                                                                       String eventName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);

            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get("requestDestinationName") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{requestDestinationName\\}",
                            data.get("requestDestinationName").toString());
                }
                if (smsTemplateData != null && data.get("requestSourcename") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{requestSourcename\\}",
                            data.get("requestSourcename").toString());
                }
                if (smsTemplateData != null && data.get("productName") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{productName\\}",
                            data.get("productName").toString());
                }
                if (smsTemplateData != null && data.get("quantity") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{quantity\\}",
                            data.get("quantity").toString());
                }
                if (smsTemplateData != null && data.get("inwardNumber") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{inwardNumber\\}",
                            data.get("inwardNumber").toString());
                }
                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                smsService.saveSmsNotificationOnFailure(null, data, null, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildInventoryDeviceInputPortConsumedPercentageMessage(Map<String, Object> data, String smsContent,
                                                               String eventName, String smsTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);

            if (optionalEvent.isPresent()) {
                String smsTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), false);
                if (smsTemplateData != null && data.get("employeeName") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{PartnerName\\}",
                            data.get("employeeName").toString());
                }
                if (smsTemplateData != null && data.get("deviceName") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{DeviceName\\}",
                            data.get("deviceName").toString());
                }
                if (smsTemplateData != null && data.get("ownerName") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{OwnerName\\}",
                            data.get("ownerName").toString());
                }
                if (smsTemplateData != null && data.get("consumePercentage") != null) {
                    smsTemplateData = smsTemplateData.replaceAll("\\{ConsumedPercentage\\}",
                            data.get("consumePercentage").toString());
                }
                smsContent = smsTemplateData;
            }
            if (smsContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                smsService.saveSmsNotificationOnFailure(null, data, null, smsContent, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(SMS_CONTENT, smsContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
