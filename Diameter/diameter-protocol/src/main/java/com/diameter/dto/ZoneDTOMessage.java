package com.diameter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneDTOMessage {
    private Long zoneId;
    private String zoneName;
    private String prefixPattern;
    private String description;
    private Integer minLength;
    private Integer maxLength;
}
