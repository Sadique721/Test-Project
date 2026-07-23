package com.savbill.radius.entity;

import com.savbill.radius.kafka.message.TimeBasePolicyDetailsMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Map;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltimebasepolicydetails")
public class TimeBasePolicyDetails {
    @Id
    @Column(name = "details_id", nullable = false, length = 40)
    private Long detailsid;


    @Column(name = "from_day", nullable = false)
    private String fromDay;

    @Column(name = "to_day", nullable = false)
    private String toDay;

    @Column(name = "from_time", nullable = false)
    private String fromTime;

    @Column(name = "to_time", nullable = false)
    private String toTime;

    @Column(name = "qqsid", nullable = false)
    private Long qqsid;

    @Column(name = "access")
    private Boolean access;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;


    @Column(name = "policy_id")
    private Long policy_id;

    @Column(name = "is_free_quota", columnDefinition = "Boolean default false")
    private Boolean isFreeQuota;

    public TimeBasePolicyDetails(TimeBasePolicyDetailsMessage timeBasePolicymessage){
        Map<String, Object> message = timeBasePolicymessage.getData();
        if (message.get("id") != null)
            this.detailsid = Long.parseLong(message.get("id").toString());
        if(message.get("policy_id") !=  null)
            this.policy_id = Long.parseLong(message.get("policy_id").toString());
        if (message.get("fromday") != null)
            this.fromDay = message.get("fromday").toString();
        if (message.get("today") != null)
            this.toDay = message.get("today").toString();
        if (message.get("fromtime") != null)
            this.fromTime = message.get("fromtime").toString();
        if (message.get("totime") != null)
            this.toTime = message.get("totime").toString();
        if (message.get("qqsid") != null)
            this.qqsid = Long.parseLong(message.get("qqsid").toString());
        if (message.get("access") != null)
            this.access = Boolean.parseBoolean(message.get("access").toString());
        if (message.get("isDeleted") != null)
            this.isDeleted = Boolean.parseBoolean(message.get("isDeleted").toString());
        if (message.get("isFreeQuota") != null)
            this.isFreeQuota = Boolean.parseBoolean(message.get("isFreeQuota").toString());
        else this.isFreeQuota = false;
    }

}
