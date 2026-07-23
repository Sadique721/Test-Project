package com.diameter.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblm_pulse_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PulseManagement {
    @Id
    @Column(name = "pulse_id")
    private Long id;

//    @Column(name = "rate_package_id")
//    private Long ratePackageId;

    @Column(name = "pulse_name", nullable = false, length = 50)
    private String pulseName;

    @Column(name = "pulse_unit", nullable = false, length = 20)
    private String pulseUnit;

    @Column(name = "pulse_value", nullable = false, precision = 10, scale = 2)
    private Double pulseValue;

//    @Column(name = "rate_per_pulse", nullable = false, precision = 10, scale = 2)
//    private Double ratePerPulse;
//
//    @Column(name = "rounding_mode", nullable = false, length = 20)
//    private String roundingMode;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate = LocalDateTime.now();

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

}
