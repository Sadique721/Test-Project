package com.diameter.dto;

import com.diameter.enums.PackageType;
import com.diameter.model.RatePackageGroupMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatePackageGroupMappingDTO {
    private Long id;
    private String checkedItem;
    private Long groupId;
    private Long ratePackageId;
    private Long zoneMappingId;
    private PackageType packageType;

    // NON_MONETARY fields
    private Integer pulseValue;
    private String pulseUnit;
    private String roundingMode;

    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private List<RatePackageGroupMapping> ratePackageGroupMappingDTOS;
}
