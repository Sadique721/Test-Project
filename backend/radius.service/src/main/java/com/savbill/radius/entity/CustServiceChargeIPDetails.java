package com.savbill.radius.entity;

import com.savbill.radius.kafka.message.CustServiceChargeIPDtlsMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Entity
@Data
@ToString
@Table(name = "tblcustservicechargipedtls")
public class CustServiceChargeIPDetails {

    @Id
    @Column(name = "id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "custid", nullable = false, length = 40)
    private Integer custId;

    @Column(name = "custservicemappingid", nullable = false, length = 40)
    private Integer custServiceMappingId;

    @Column(name = "static_ip_address")
    private String staticIPAdrress;

    @Column(name = "static_ip_start_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime staticIPStartDate;

    @Column(name = "static_ip_end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime staticIPEndDate;

    @Column(name = "charge_id", nullable = false, length = 40)
    private Integer chargeId;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a", timezone = "Asia/Kolkata")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a", timezone = "Asia/Kolkata")
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

    public CustServiceChargeIPDetails(){}
    public CustServiceChargeIPDetails(CustServiceChargeIPDtlsMessage custServiceChargeIPDtlsMessage) {
        Map<String, Object> message = custServiceChargeIPDtlsMessage.getData();
            if (message.get("id") != null)
            this.id = Integer.valueOf((message.get("id").toString()));
        if (message.get("custid") != null)
            this.custId = Integer.valueOf(message.get("custid").toString());
        if (message.get("custservicemappingid") != null)
            this.custServiceMappingId = Integer.valueOf(message.get("custservicemappingid").toString());
        if (message.get("static_ip_address") != null)
            this.staticIPAdrress = (message.get("static_ip_address").toString());
        if (message.get("static_ip_start_date") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.staticIPStartDate = LocalDateTime.parse(message.get("static_ip_start_date").toString(), formatter);
        }
        if (message.get("static_ip_end_date") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.staticIPEndDate = LocalDateTime.parse(message.get("static_ip_end_date").toString(), formatter);
        }
    }
}
