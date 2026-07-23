package com.diameter.dto;

import com.diameter.enums.PackageType;
import com.diameter.enums.ServiceType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatePackageDTOMessage {
    private Long ratePackageId;
    private String packageName;
    private ServiceType serviceType;
    private PackageType packageType;
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    private String modifiedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modifiedDate;

    private Boolean isDeleted;
    private Boolean isPulseApplied;
    private Long pulseId;
    private Double ratePerPulse;
    private String roundingMode;
}
