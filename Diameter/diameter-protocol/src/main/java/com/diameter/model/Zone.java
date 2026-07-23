package com.diameter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblm_zone")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone {
    @Id
    @Column(name = "zone_id")
    private Long zoneId;

    @Column(name = "zone_name", nullable = false)
    private String zoneName;

    @Column(name = "prefix_pattern", nullable = false)
    private String prefixPattern;

    @Column(name = "zone_desc")
    private String description;

    @Column(name = "min_length")
    private Integer minLength;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "created_date", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Column(name = "modified_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime modifiedDate;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

}
