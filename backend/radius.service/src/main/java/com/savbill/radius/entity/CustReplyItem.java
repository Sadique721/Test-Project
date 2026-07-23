package com.savbill.radius.entity;

import com.savbill.radius.kafka.CustomMessage;
import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
@Table(name = "tblradiuscustomerreply")
public class CustReplyItem {

    @Id
    @Column(name = "attributeid", nullable = false, length = 40)
    private Integer id;
    private Integer custid;
    private String attribute;
    private String attributevalue;
    @Transient
    private String tempid;

    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    public CustReplyItem() {
    }
    public CustReplyItem(CustomMessage customMessage) {
        Map<String, Object> message = customMessage.getData();
        if (message.get("id") != null)
            this.id = Integer.parseInt(message.get("id").toString());
        if (message.get("custid") != null)
            this.custid = Integer.parseInt(message.get("custid").toString());
        if (message.get("attribute") != null)
            this.attribute = message.get("attribute").toString();
        if (message.get("attributevalue") != null)
            this.attributevalue = message.get("attributevalue").toString();
        if (message.get("tempid") != null)
            this.tempid = message.get("tempid").toString();
        if (message.get("mvnoId") != null)
            this.mvnoId = Integer.parseInt(message.get("mvnoId").toString());
    }
}
