package com.savbill.radius.entity;

import com.savbill.radius.kafka.message.CustomerTimeBasePolicyDetailsMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltcustomertimebasepolicydetails")
public class CustomerTimeBasePolicyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "details_id", nullable = false)
    private Long detailsId;

    @Column(name = "from_day", nullable = false, length = 500)
    private String fromDay;

    @Column(name = "to_day", nullable = false, length = 500)
    private String toDay;

    @Column(name = "from_time", nullable = false, length = 500)
    private String fromTime;

    @Column(name = "to_time", nullable = false, length = 500)
    private String toTime;

    @Column(name = "speed", nullable = false, length = 500)
    private String speed;

    @Column(name = "access")
    private Boolean access;

    @Column(name = "custid", nullable = false)
    private Long  customerId;

    @Column(name = "planid", nullable = false)
    private Long  planId;

    @Column(name = "quotadtlid", nullable = false)
    private Long  quotadtlId;

//    @Column(name = "qqsid", nullable = false)
//    private Long qqsid;


    public CustomerTimeBasePolicyDetails(CustomerTimeBasePolicyDetailsMessage customerTimeBasePolicyDetailsMessage) {
        Map<String, Object> message = customerTimeBasePolicyDetailsMessage.getData();
        if (message.get("id") != null)
            this.detailsId = Long.parseLong(message.get("id").toString());
        if (message.get("fromday") != null)
            this.fromDay = message.get("fromday").toString();
        if (message.get("today") != null)
            this.toDay = message.get("today").toString();
        if (message.get("fromtime") != null)
            this.fromTime = message.get("fromtime").toString();
        if (message.get("totime") != null)
            this.toTime = message.get("totime").toString();
        if (message.get("speed") != null)
            this.speed = message.get("speed").toString();
        if (message.get("access") != null)
            this.access = Boolean.valueOf(message.get("access").toString());
        if (message.get("custid") != null)
            this.customerId = Long.parseLong(message.get("custid").toString());
        if (message.get("planid") != null)
            this.planId = Long.parseLong(message.get("planid").toString());
//        if(message.get("qqsid") != null)
//            this.qqsid = Long.parseLong(message.get("qqsid").toString());
        if (message.get("quotadtlid") != null)
            this.quotadtlId = Long.parseLong(message.get("quotadtlid").toString());

    }
}
