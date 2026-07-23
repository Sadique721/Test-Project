package com.savbill.radius.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tbltfaultymac")
public class FaultyMAC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mack_id")
    private String mackId;

    @ApiModelProperty(hidden = true)
    @Column(name = "mvnoid", nullable = false)
    private Integer mvnoId;

    @Column(name="is_active", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isActive=true;

    @Column(name = "is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "lastconnected", updatable = true)
    private LocalDateTime lastConnected;
}
