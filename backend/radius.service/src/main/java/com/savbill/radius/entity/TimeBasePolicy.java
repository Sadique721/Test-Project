package com.savbill.radius.entity;

import com.savbill.radius.kafka.message.TimeBasePolicyMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmtimebasepolicy")
public class TimeBasePolicy {
    @Id
    @Column(name = "policy_id", nullable = false, length = 40)
    private Long id;

    @Column(name = "policy_name", nullable = false, length = 40)
    private String name;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

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


    public TimeBasePolicy(TimeBasePolicyMessage timeBasePolicymessage){
        Map<String, Object> message = timeBasePolicymessage.getData();
        if (message.get("policy_id") != null)
            this.id = Long.parseLong(message.get("policy_id").toString());
        if (message.get("policy_name") != null)
            this.name = message.get("policy_name").toString();
        if(message.get("status") != null)
            this.status = message.get("status").toString();
        if (message.get("isDeleted") != null)
            this.isDeleted = Boolean.parseBoolean(message.get("isDeleted").toString());
        if (message.get("mvnoId") != null)
            this.mvnoId = Integer.parseInt(message.get("mvnoId").toString());
    }
}
