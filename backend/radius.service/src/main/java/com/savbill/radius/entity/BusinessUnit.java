package com.savbill.radius.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Entity
@Data
@Table(name = "tblmbusinessunit")
public class BusinessUnit {
    @Id
    @Column(name = "businessunitid")
    private Long id;

    @Column(name = "buname")
    private String buname;

    @Column(name = "bucode")
    private String bucode;

    @Column(name = "status")
    private String status;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "plan_binding_type",length = 50)
    private String planBindingType;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "lastmodifieddate")
    private LocalDateTime updatedate;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @Column(name = "createdbystaffid", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "lastmodifiedbystaffid", nullable = false, length = 40)
    private Integer lastModifiedById;

    public BusinessUnit(Map map) {
        if (map.get("businessunitmappingid") != null) {
            this.id = Long.parseLong(map.get("businessunitmappingid").toString());
        }
        if (map.get("buname") != null) {
            this.buname = map.get("buname").toString();
        }
        if (map.get("isDelete") != null) {
            this.isDeleted = Boolean.valueOf(map.get("isDelete").toString());
        }
        if (map.get("MvnoId") != null) {
            this.mvnoId = Integer.valueOf(map.get("MvnoId").toString());
        }
        if (map.get("createdbyid") != null) {
            this.createdById = Integer.valueOf(map.get("createdbyid").toString());
        }
        if (map.get("lastmodifiedbyid") != null) {
            this.lastModifiedById = Integer.valueOf(map.get("lastmodifiedbyid").toString());
        }
        if (map.get("createdByName") != null) {
            this.createdByName = map.get("createdByName").toString();
        }
        if (map.get("lastmodifiedbyname") != null) {
            this.lastModifiedByName =map.get("lastmodifiedbyname").toString();
        }
    }

    public BusinessUnit() {
    }
}
