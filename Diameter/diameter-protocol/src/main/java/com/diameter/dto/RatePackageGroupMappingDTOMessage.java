package com.diameter.dto;

import com.diameter.enums.PackageType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RatePackageGroupMappingDTOMessage {
    private Long groupId;
    private List<RatePackageGroupMappingDTO> mappings;

    @Data
    public static class MappingItem {
        private Long mappingId;
        private Long groupId;
        private Long ratePackageId;
        private Long zoneMappingId;
        private String checkedItem;
        private LocalDateTime effectiveDate;
        private LocalDateTime expiryDate;
        private PackageType packageType;
        private Integer pulseValue;
        private String pulseUnit;
        private String roundingMode;
    }
}