package com.savbill.notification.savbilliwfnotification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {

    private List<PacketAttributeDTO> packetAttributes;
    private String driverId;
    private String edgeLocation;
    private String conditions;
    private String ruleName;
    private String eventName;
    private Long eventId;
    private String topicName;
    private String applicationName;
    private Map<String, Object> manualMailContent = new HashMap<>();
    private List<CheckSumDto> checkSumDtoList;


}
