package com.savbill.notification.savbilliwfnotification.util;

import com.savbill.notification.savbilliwfnotification.dto.*;
import com.savbill.notification.savbilliwfnotification.dto.*;
import com.savbill.notification.savbilliwfnotification.service.IwfEmailService;
import com.savbill.notification.entity.Event;
import com.savbill.notification.utils.NotificationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;


@Component
public class PacketProcessing {
    private static Long lastTimeStamp = System.currentTimeMillis();
    private Logger logger = LoggerFactory.getLogger(PacketProcessing.class);
    private CopyOnWriteArrayList<PacketAttributeDTO> packetAttributeDTOS;
    private ConcurrentHashMap<Long, List<PacketAttributeDTO>> packetAttributeListConcurrentHashMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Long> packetLabels = new ConcurrentHashMap<>();

    @Autowired
    private IwfEmailService iwfEmailService;

//    public PacketProcessing(CopyOnWriteArrayList<PacketAttributeDTO> packetAttributeDTOS) {
//        logger.info("Initialized list of packet attributes....");
//    }

    /**
     * Method: Get Next Time Stamp
     *
     * @return
     */
    public synchronized long getNextTimeStamp() {
        long curentTimeStamp = System.currentTimeMillis();
        if (curentTimeStamp <= getLastTimeStamp()) {
            curentTimeStamp = getLastTimeStamp() + 1;
        }
        setLastTimeStamp(curentTimeStamp);
        return curentTimeStamp;
    }

    public static synchronized long getLastTimeStamp() {
        return lastTimeStamp;
    }

    public static synchronized void setLastTimeStamp(long timeStamp) {
        lastTimeStamp = timeStamp;
    }

    /**
     * Method: Process Packet Attributes
     *
     * @param dto
     * @param currentTimeStamp
     * @param eventTemplateBindingDTO
     * @param packetAttributeDTOS
     */
    public void processPacketAttributes(NotificationResponseDTO dto, Long currentTimeStamp, EventTemplateBindingDTO eventTemplateBindingDTO, List<PacketAttributeDTO> packetAttributeDTOS, Event event) {
        if (packetAttributeListConcurrentHashMap.isEmpty()) {
            /** Call Process First Attributes Method */
            processFirstPacketAttributes(dto, currentTimeStamp, eventTemplateBindingDTO, event);
        } else {
            /** Call Process Existing Packet Attributes Method */
            processExistingPacketAttributes(dto, currentTimeStamp, eventTemplateBindingDTO, packetAttributeDTOS, event);
        }
    }

    /**
     * Method: Process First Packet Attributes
     *
     * @param dto
     * @param currentTimeStamp
     * @param eventTemplateBindingDTO
     */
    public void processFirstPacketAttributes(NotificationResponseDTO dto, Long currentTimeStamp, EventTemplateBindingDTO eventTemplateBindingDTO, Event event) {
        /** Call Get Modified Packets Method */
        List<PacketAttributeDTO> modifiedPacketList = getModifiedPackets(dto.getPacketAttributes());
        for (PacketAttributeDTO packetAttributeDTO : modifiedPacketList) {
            packetLabels.put(packetAttributeDTO.getLabel(), currentTimeStamp);
        }
        packetAttributeListConcurrentHashMap.put(currentTimeStamp, modifiedPacketList);
        /** Call Release Packets To Notification Method */
        releasePacketsToNotification(dto, eventTemplateBindingDTO, event);
    }

    /**
     * Method: Process Existing Packet Attributes
     *
     * @param dto
     * @param currentTimeStamp
     * @param eventTemplateBindingDTO
     * @param packetAttributeDTOS
     */
    public void processExistingPacketAttributes(NotificationResponseDTO dto, Long currentTimeStamp, EventTemplateBindingDTO eventTemplateBindingDTO, List<PacketAttributeDTO> packetAttributeDTOS, Event event) {
        /** Call Get Modified Packets Method */
        List<PacketAttributeDTO> existPacketAttributeDTOS = getModifiedPackets(dto.getPacketAttributes());

        AtomicBoolean accepted = new AtomicBoolean(false);
        AtomicReference<Long> updatedTimeStamp = new AtomicReference<>();
        if (event.getIsFrequency()) {
            packetAttributeListConcurrentHashMap.forEach((timestamp, packetList) -> {
                if ((currentTimeStamp - timestamp) >= (event.getConvertedTime())) {
                    updatedTimeStamp.set(currentTimeStamp);
                    accepted.set(true);
                } else {
                    packetList.forEach(packetAttributeDTO -> {
                        if ((currentTimeStamp - packetLabels.get(packetAttributeDTO.getLabel())) >= (event.getConvertedTime())) {
                            packetAttributeDTOS.add(packetAttributeDTO);
                        }
                    });
                    if (!packetAttributeDTOS.isEmpty()) {
                        accepted.set(true);
                    }
                }
            });
        } else {
            accepted.set(true);
        }

        if (accepted.get()) {
            if (updatedTimeStamp.get() != null) {
                packetAttributeListConcurrentHashMap.clear();
                packetAttributeListConcurrentHashMap.put(updatedTimeStamp.get(), existPacketAttributeDTOS);
                existPacketAttributeDTOS.forEach(packetAttributeDTO -> {
                    packetLabels.put(packetAttributeDTO.getLabel(), updatedTimeStamp.get());
                });
                /** Call Release Packets To Notification Method */
                releasePacketsToNotification(dto, eventTemplateBindingDTO, event);
                updatedTimeStamp.set(null);
            } else {
                updatePacketLabels(packetAttributeDTOS, currentTimeStamp);
                packetAttributeListConcurrentHashMap.clear();
                packetAttributeListConcurrentHashMap.put(currentTimeStamp, dto.getPacketAttributes());
                dto.setPacketAttributes(new CopyOnWriteArrayList<>(packetAttributeDTOS));
                /** Call Release Packets To Notification Method */
                releasePacketsToNotification(dto, eventTemplateBindingDTO, event);
            }
        }
    }

    /**
     * Method: Update Packet Labels
     *
     * @param packetAttributeDTOS
     * @param currentTimeStamp
     */
    public void updatePacketLabels(List<PacketAttributeDTO> packetAttributeDTOS, Long currentTimeStamp) {
        for (PacketAttributeDTO packetAttributeDTO : packetAttributeDTOS) {
            if (packetLabels.containsKey(packetAttributeDTO.getLabel())) {
                packetLabels.put(packetAttributeDTO.getLabel(), currentTimeStamp);
            }
        }
    }

    /**
     * Method: Get Modified Packets
     *
     * @param existingList
     * @return
     */
    public List<PacketAttributeDTO> getModifiedPackets(List<PacketAttributeDTO> existingList) {
        return existingList.stream()
                .filter(packetAttributeDTO ->
                        (packetAttributeDTO.getLabel() != null &&
                                !packetAttributeDTO.getLabel().equalsIgnoreCase("")) &&
                                (packetAttributeDTO.getValue() != null &&
                                        !packetAttributeDTO.getValue().equals(""))
                )
                .collect(Collectors.toList());
    }

    /**
     * Method: Released Packet To Notification
     *
     * @param dto
     * @param eventTemplateBindingDTO
     */
    public void releasePacketsToNotification(NotificationResponseDTO dto, EventTemplateBindingDTO eventTemplateBindingDTO, Event event) {
        if ((dto.getPacketAttributes() != null && !dto.getPacketAttributes().isEmpty())) {
            /** Call Request Preparation Method */
            DataMaster dataMaster = requestPreparation(dto, eventTemplateBindingDTO, event);
            /** Call Send Email Template Bindind Notification Method */
            iwfEmailService.sendEmailTempBindNotification(dataMaster);
        }
    }

    public void releaseNotification(NotificationResponseDTO dto, EventTemplateBindingDTO eventTemplateBindingDTO, Event event) {
        /** Call Request Preparation Method */
        DataMaster dataMaster = requestPreparation(dto, eventTemplateBindingDTO, event);
        /** Call Send Email Template Bindind Notification Method */
        iwfEmailService.sendEmailTempBindNotification(dataMaster);
    }

    /**
     * Method: Request Preparation
     *
     * @param dto
     * @param eventTemplateBindingDTO
     * @return
     */
    public DataMaster requestPreparation(NotificationResponseDTO dto, EventTemplateBindingDTO eventTemplateBindingDTO, Event event) {
        DataMaster dataMaster = new DataMaster();

        Map<String, Object> objectMap = new ConcurrentHashMap<>();
        if (dto.getPacketAttributes() != null && !dto.getPacketAttributes().isEmpty()) {
            /** Call Convert Map To Mail Response Method With Convert To Map Method*/
            List<MailResponseDTO> mailResponseDTOS = convertMapToMailResponse(convetToMap(dto.getPacketAttributes()));
            objectMap.put("filteredAttributes", mailResponseDTOS);
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
        dataMaster.setMailContent(objectMap);

        if (dto.getManualMailContent() != null && !dto.getManualMailContent().isEmpty()) {
            dataMaster.setManualMailContent(dto.getManualMailContent());
        }
        dataMaster.setSubject(event.getEmailSubject());
        dataMaster.setIsEmailConfigured(eventTemplateBindingDTO.getIsEmailTemplate().toString());
        dataMaster.setEvent(event);
        dataMaster.setEventTemplateBindingDTO(eventTemplateBindingDTO);
        dataMaster.setEventName(dto.getEventName());
        dataMaster.setEventId(dto.getEventId());
        dataMaster.setActionDate(new Date().toString());
        dataMaster.setAppName(dto.getApplicationName());
        return dataMaster;
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
     * Method: Convert to Map of Packet Attributes
     *
     * @param packetAttributeDTOs
     * @return
     */
    public Map<String, String> convetToMap(List<PacketAttributeDTO> packetAttributeDTOs) {
        Map<String, String> map = new HashMap<>();
        for (PacketAttributeDTO packetAttribute : packetAttributeDTOs) {
            map.put(packetAttribute.getLabel(), String.valueOf(packetAttribute.getValue()));
        }
        return map;
    }

    public void commonReleaseNotification(Object dto, EventTemplateBindingDTO eventTemplateBindingDTO, Event event) {
        if (dto instanceof CommonNotificationDto) {
            CommonNotificationDto commonNotificationDto = (CommonNotificationDto) dto;

            /** Call Request Preparation Method */
            DataMaster dataMaster = commonRequestPreparation(commonNotificationDto, eventTemplateBindingDTO, event);
            /** Call Send Email Template Bindind Notification Method */
            iwfEmailService.sendEmailTempBindNotification(dataMaster);
        }
    }

    public DataMaster commonRequestPreparation(CommonNotificationDto dto, EventTemplateBindingDTO eventTemplateBindingDTO, Event event) {
        DataMaster dataMaster = new DataMaster();

        Map<String, Object> objectMap = new ConcurrentHashMap<>();
        /*   if (dto.getPacketAttributes() != null && !dto.getPacketAttributes().isEmpty()) {
         *//** Call Convert Map To Mail Response Method With Convert To Map Method*//*
            List<MailResponseDTO> mailResponseDTOS = convertMapToMailResponse(convetToMap(dto.getPacketAttributes()));
            objectMap.put("filteredAttributes",mailResponseDTOS);
        }*/
        objectMap.put("firstName", NotificationConstants.FIRST_NAME_VAL);
        objectMap.put("SENDER", NotificationConstants.SENDER);
        if (dto.getApplicationName().equalsIgnoreCase(NotificationConstants.ApplicationName.APIGATEWAY_COMMON_APPLICATION) && dto instanceof GeneratePasswordDto) {
            GeneratePasswordDto generatePasswordDto = (GeneratePasswordDto) dto;
            objectMap.put(NotificationConstants.EMAIL_ID, ((GeneratePasswordDto) dto).getEmail());
            objectMap.put(NotificationConstants.ALT_EMAIL, "default@gmail.com");
            objectMap.put(NotificationConstants.BCC_EMAIL, "default@gmail.com");
        } else {
            objectMap.put(NotificationConstants.EMAIL_ID, event.getToEmailId());
            objectMap.put(NotificationConstants.ALT_EMAIL, event.getCcEmailId());
            objectMap.put(NotificationConstants.BCC_EMAIL, event.getBccEmailId());
        }
        dataMaster.setMailContent(objectMap);

        if (dto.getManualMailContent() != null && !dto.getManualMailContent().isEmpty()) {
            dataMaster.setManualMailContent(dto.getManualMailContent());
        }
        dataMaster.setSubject(event.getEmailSubject());
        dataMaster.setIsEmailConfigured(eventTemplateBindingDTO.getIsEmailTemplate().toString());
        dataMaster.setEvent(event);
        dataMaster.setEventTemplateBindingDTO(eventTemplateBindingDTO);
        dataMaster.setEventName(dto.getEventName());
        dataMaster.setEventId(dto.getEventId());
        dataMaster.setActionDate(new Date().toString());
        dataMaster.setAppName(dto.getApplicationName());
        return dataMaster;
    }

    /**
     * Method: Evaluate Packet List
     *
     * @param packetAttributeDTOS
     * @param eventTemplateBindingDTO
     * @return
     */
    public Boolean evaluatePacketList(List<PacketAttributeDTO> packetAttributeDTOS, EventTemplateBindingDTO eventTemplateBindingDTO) {
        List<PacketAttributeDTO> evaluatePacketAttributeDTOS = new ArrayList<>();
        Boolean flag = false;
        if (eventTemplateBindingDTO != null &&
                (eventTemplateBindingDTO.getConstraintType() != null)
                && (eventTemplateBindingDTO.getColumnValue() != null)
        ) {
            switch (eventTemplateBindingDTO.getConstraintType()) {
                case NotificationConstants.ConstraintType.CONSTRAINT_TYPE_EXACT_MATCH:
                    flag = packetAttributeDTOS.stream()
                            .anyMatch(packetAttributeDTO ->
                                    packetAttributeDTO.getLabel().equalsIgnoreCase(eventTemplateBindingDTO.getColumnValue()));
                    break;
                case NotificationConstants.ConstraintType.CONSTRAINT_TYPE_REGEX_BASED:
                    flag = packetAttributeDTOS.stream()
                            .anyMatch(packetAttributeDTO ->
                                    packetAttributeDTO.getLabel().equalsIgnoreCase(eventTemplateBindingDTO.getColumnValue()));
                    if (flag) {
                        Object value = null;
                        List<PacketAttributeDTO> filterList = packetAttributeDTOS.stream()
                                .filter(packetAttributeDTO ->
                                        packetAttributeDTO.getLabel().equalsIgnoreCase(eventTemplateBindingDTO.getColumnValue()))
                                .collect(Collectors.toList());
                        if (filterList != null && filterList.isEmpty() && filterList.size() > 0) {
                            value = filterList.get(0).getValue();
                        }
                        /** Call Match String With Regex Method */
                        flag = matchStringWithRegex(eventTemplateBindingDTO.getRegex(), value.toString());
                    }
                    break;
            }
        } else if (eventTemplateBindingDTO != null && !eventTemplateBindingDTO.getIsFrequency()) {
            flag = true;
        }
        return flag;
    }

    /**
     * Method: Match String With Regex
     *
     * @param regex
     * @param value
     * @return
     * @throws PatternSyntaxException
     */
    public Boolean matchStringWithRegex(String regex, String value) throws PatternSyntaxException {
        boolean isEvaluated = false;
        if (value != null && value.equalsIgnoreCase("")) {
            Pattern pattern;
            pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);
            isEvaluated = matcher.matches();
        }
        return isEvaluated;
    }
}
