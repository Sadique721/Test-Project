package com.savbill.radius.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Entity
@Table(name = "tblstaffservicearearel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class StaffUserServiceAreaMapping {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "staffid", nullable = false, length = 40)
    private Integer staffId;

    @Column(name = "serviceareaid", nullable = false, length = 40)
    private  Long serviceId;

    @Column(name = "created_on", nullable = false, length = 40)
    private LocalDateTime createdOn;

    @Column(name = "lastmodified_on", nullable = false, length = 40)
    private LocalDateTime lastmodifiedOn;

   public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById;

    public StaffUserServiceAreaMapping(Map map) {
        if (map.get("seriveareamappingid") != null) {
            this.id = Long.parseLong(map.get("seriveareamappingid").toString());
        }
       if (map.get("staffid") != null) {
           this.staffId = Integer.parseInt(map.get("staffid").toString());
       }
        if (map.get("serviceareaid") != null) {
            this.serviceId = Long.parseLong(map.get("serviceareaid").toString());
        }
        if (map.get("created_on") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.createdOn = LocalDateTime.parse(map.get("created_on").toString(), formatter);
        }
        if (map.get("lastmodified_on") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.lastmodifiedOn = LocalDateTime.parse(map.get("lastmodified_on").toString(), formatter);
        }
        if (map.get("createdate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.createdate = LocalDateTime.parse(map.get("createdate").toString(), formatter);
        }
        if (map.get("updatedate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.updatedate = LocalDateTime.parse(map.get("updatedate").toString(), formatter);
        }
        if (map.get("createdByName") != null) {
            this.createdByName = map.get("createdByName").toString();
        }
        if (map.get("lastModifiedByName") != null) {
            this.lastModifiedByName = map.get("lastModifiedByName").toString();
        }
        if (map.get("createdById") != null) {
            this.createdById = Integer.valueOf(map.get("createdById").toString());
        }
        if (map.get("lastModifiedById") != null) {
            this.lastModifiedById = Integer.valueOf(map.get("lastModifiedById").toString());
        }

    }
}

