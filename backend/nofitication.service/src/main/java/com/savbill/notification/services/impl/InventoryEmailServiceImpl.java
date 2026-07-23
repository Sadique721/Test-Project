package com.savbill.notification.services.impl;

import com.savbill.notification.entity.Event;
import com.savbill.notification.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryEmailServiceImpl {
    private static final String EVENT_ID = "eventId";
    private static final String EMAIL_CONTENT = "emailContent";
    private static final String TEMPLATE_NOT_FOUND = "template not found";

    @Autowired
    EventRepository eventRepository;

    @Autowired
    TemplateServiceImpl templateServiceImpl;

    @Autowired
    EmailServiceImpl emailServiceImpl;

    public Map<String, Object> buildInventoryAssignmentForStaffMessage(Map<String, Object> data, String emailContent,
                                                                       String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get("employeeName") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{employeeName\\}",
                            data.get("employeeName").toString());
                }
                if (emailTemplateData != null && data.get("assetId") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{assetId\\}",
                            data.get("assetId").toString());
                }
                if (emailTemplateData != null && data.get("serialNumber") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{serialNumber\\}",
                            data.get("serialNumber").toString());
                }
                if (emailTemplateData != null && data.get("assetsSpecification") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{specifications\\}",
                            data.get("assetsSpecification").toString());
                }
                if (emailTemplateData != null && data.get("assignDate") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{assignDate\\}",
                            data.get("assignDate").toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                emailServiceImpl.saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildInventoryRequestMessage(Map<String, Object> data, String emailContent,
                                                                       String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get("requestTo") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{requestTo\\}",
                            data.get("requestTo").toString());
                }
                if (emailTemplateData != null && data.get("requester") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{requester\\}",
                            data.get("requester").toString());
                }
                if (emailTemplateData != null && data.get("onBehalfOf") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{onBehalfOf\\}",
                            data.get("onBehalfOf").toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                emailServiceImpl.saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildInventoryThresholdMessage(Map<String, Object> data, String emailContent,
                                                              String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);

            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(
                        optionalEvent.get(),
                        (Integer) data.get("mvnoId"),
                        (Integer) data.get("buId"),
                        true
                );

                if (emailTemplateData != null && data.get("productName") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{productName\\}", data.get("productName").toString());
                }
                if (emailTemplateData != null && data.get("warehouseName") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{warehouseName\\}", data.get("warehouseName").toString());
                }
                if (emailTemplateData != null && data.get("currentQty") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{currentQty\\}", data.get("currentQty").toString());
                }

                emailContent = emailTemplateData;
            }

            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                emailServiceImpl.saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
            }

            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public Map<String, Object> buildInventoryFulfilmentMessage(Map<String, Object> data, String emailContent,
                                                                       String eventName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get("requestDestinationName") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{requestDestinationName\\}",
                            data.get("requestDestinationName").toString());
                }
                if (emailTemplateData != null && data.get("requestSourcename") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{requestSourcename\\}",
                            data.get("requestSourcename").toString());
                }
                if (emailTemplateData != null && data.get("productName") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{productName\\}",
                            data.get("productName").toString());
                }
                if (emailTemplateData != null && data.get("quantity") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{quantity\\}",
                            data.get("quantity").toString());
                }
                if (emailTemplateData != null && data.get("inwardNumber") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{inwardNumber\\}",
                            data.get("inwardNumber").toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                emailServiceImpl.saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> buildInventoryDeviceInputPortConsumedPercentageMessage(Map<String, Object> data, String emailContent, String eventName, String sourceName, String emailTemplate) {
        try {
            Map<String, Object> returnData = new HashMap<>();
            Optional<Event> optionalEvent = eventRepository.findByEventName(eventName);
            if (optionalEvent.isPresent()) {
                String emailTemplateData = templateServiceImpl.findTemplateByEventMvnoBU(optionalEvent.get(), (Integer) data.get("mvnoId"), (Integer) data.get("buId"), true);
                if (emailTemplateData != null && data.get("employeeName") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{PartnerName\\}",
                            data.get("employeeName").toString());
                }
                if (emailTemplateData != null && data.get("deviceName") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{DeviceName\\}",
                            data.get("deviceName").toString());
                }
                if (emailTemplateData != null && data.get("ownerName") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{OwnerName\\}",
                            data.get("ownerName").toString());
                }
                if (emailTemplateData != null && data.get("consumePercentage") != null) {
                    emailTemplateData = emailTemplateData.replaceAll("\\{ConsumedPercentage\\}",
                            data.get("consumePercentage").toString());
                }
                emailContent = emailTemplateData;
            }
            if (emailContent != null) {
                returnData.put(EVENT_ID, optionalEvent.get().getEventId());
            } else {
                emailServiceImpl.saveEmailNotificationOnFailure(null, data, null, emailTemplate, eventName);
                returnData.put(TEMPLATE_NOT_FOUND, true);
            }
            returnData.put(EMAIL_CONTENT, emailContent);
            return returnData;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
