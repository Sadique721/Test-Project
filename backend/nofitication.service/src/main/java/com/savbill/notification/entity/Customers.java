package com.savbill.notification.entity;

import com.savbill.notification.rabbitmq.message.CustomMessage;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "tblmcustomer")
@NoArgsConstructor
public class Customers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="id", nullable = false)
    private Long id;

    @Column(name = "custid")
    private Integer custid;

    @Column(name ="username")
    private String username;

    @Column(name = "is_notification_enable")
    private Boolean isNotificationEnable;
    @Column(name = "mvnoid")
    private Long mvnoId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCustid() {
        return custid;
    }

    public void setCustid(Integer custid) {
        this.custid = custid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getNotificationEnable() {
        return isNotificationEnable;
    }

    public void setNotificationEnable(Boolean notificationEnable) {
        isNotificationEnable = notificationEnable;
    }



    public Customers(CustomMessage customMessage) {
        Map<String, Object> message = customMessage.getCustomerData();
        if (message.get("id") != null) {
            this.custid = Integer.parseInt(message.get("id").toString());
        }
        if (message.get("isNotificationEnable") != null) {
            this.isNotificationEnable = Boolean.parseBoolean(message.get("isNotificationEnable").toString());
        }
        if (message.get("username") != null) {
            this.username = message.get("username").toString();
        }
        if (message.get("mvnoId") != null) {
            this.mvnoId = Long.parseLong(message.get("mvnoId").toString());
        }
    }
}
