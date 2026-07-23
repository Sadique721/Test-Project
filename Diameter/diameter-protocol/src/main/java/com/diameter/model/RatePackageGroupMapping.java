package com.diameter.model;

import com.diameter.enums.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblt_rate_package_group_mapping")
public class RatePackageGroupMapping {

    @Id
    @Column(name = "mapping_id")
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "rate_package_id", nullable = false)
    private Long ratePackageId;

    @Column(name = "zone_mapping_id", nullable = false)
    private Long zoneMappingId;

    @Column(name = "checked_item", nullable = false, length = 255)
    private String checkedItem;

    @Column(name = "effective_date", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "package_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PackageType packageType;

    // NON_MONETARY: pulse config for reservation rounding
    @Column(name = "pulse_value")
    private Integer pulseValue;

    @Column(name = "pulse_unit", length = 20)
    private String pulseUnit;

    @Column(name = "rounding_mode", length = 20)
    private String roundingMode;
    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (packageType == PackageType.NON_MONETARY) {
            if (pulseValue == null || pulseUnit == null || roundingMode == null) {
                throw new IllegalArgumentException("Pulse configuration cannot be null for NON_MONETARY package type");
            }
        } else if (packageType == PackageType.MONETARY) {
            // For MONETARY, these fields should be null
            if (pulseValue != null || pulseUnit != null || roundingMode != null) {
                throw new IllegalArgumentException("Pulse configuration should be null for MONETARY package type");
            }
        }
    }

}