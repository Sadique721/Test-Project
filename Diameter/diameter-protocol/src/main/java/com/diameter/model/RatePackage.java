package com.diameter.model;


import com.diameter.enums.PackageType;
import com.diameter.enums.ServiceType;
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
@Table(name = "tblm_rate_package")
public class RatePackage {

    @Id
    @Column(name = "rate_package_id")
    private Long id;

    @Column(name = "package_name", nullable = false, unique = true, length = 100)
    private String packageName;

    //    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "package_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PackageType packageType; // MONETARY, NON_MONETARY

//    @Column(name = "rate", nullable = false, precision = 10, scale = 2)
//    private String rate;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "unit", nullable = false)
//    private DataUnit rateUnit; // e.g., GB
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "rounding_mode", nullable = false)
//    private RoundingMode roundingMode; // UPPER or DEFAULT

//    @Column(name = "start_date", nullable = false)
//    private LocalDateTime effectiveDate;

//    @Column(name = "expiry_date")
//    private LocalDateTime expiryDate;

    // Audit Fields
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

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }

    public Long getPrimaryKey() {
        return id;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    public boolean getDeleteFlag() {
        return  this.isDeleted;
    }

    @Column(name = "is_pulse_applied", nullable = false)
    private Boolean isPulseApplied = false;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "pulse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rate_package_pulse"))
    private PulseManagement pulse;

    @Column(name = "rate_per_pulse", nullable = false, precision = 10, scale = 2)
    private Double ratePerPulse;

    @Column(name = "rounding_mode", nullable = false, length = 20)
    private String roundingMode;

    @Transient
    private String consumedPulses;
}