package com.savbill.radius.entity;

import com.savbill.radius.kafka.CustomMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "tbl_qos_policy")
public class QOSPolicy {

    @Id
    private Long id;
    private String name;
    private String description;
    private String basepolicyname;
    private String thpolicyname;
    private String baseparam1;
    private String baseparam2;
    private String baseparam3;
    private String thparam1;
    private String thparam2;
    private String thparam3;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

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

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = QOSPolicyGatewayMapping.class, cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "qos_policy_id")
    List<QOSPolicyGatewayMapping> qosPolicyGatewayMappingList=new ArrayList<>();

    @Column(name = "type")
    private String type;

    @Column(name="qosspeed")
    private String qosspeed;
    @Column(name = "upstreamprofileuid")
    private String upstreamprofileuid;
    @Column(name = "downstreamprofileuid")
    private String downstreamprofileuid;

    public QOSPolicy() {
    }

    public QOSPolicy(CustomMessage customMessage) {
        Map<String, Object> message = customMessage.getData();
        if (message.get("id") != null) {
            this.id = Long.parseLong(message.get("id").toString());
        }
        if (message.get("name") != null) {
            this.name = message.get("name").toString();
        }
        if (message.get("description") != null) {
            this.description = message.get("description").toString();
        }
        if (message.get("basepolicyname") != null) {
            this.basepolicyname = message.get("basepolicyname").toString();
        }
        if (message.get("thpolicyname") != null) {
            this.thpolicyname = message.get("thpolicyname").toString();
        }
        if (message.get("baseparam1") != null) {
            this.baseparam1 = message.get("baseparam1").toString();
        }
        if (message.get("baseparam2") != null) {
            this.baseparam2 = message.get("baseparam2").toString();
        }
        if (message.get("baseparam3") != null) {
            this.baseparam3 = message.get("baseparam3").toString();
        }
        if (message.get("thparam1") != null) {
            this.thparam1 = message.get("thparam1").toString();
        }
        if (message.get("thparam2") != null) {
            this.thparam2 = message.get("thparam2").toString();
        }
        if (message.get("thparam3") != null) {
            this.thparam3 = message.get("thparam3").toString();
        }
        if (message.get("isDeleted") != null) {
            this.isDeleted = Boolean.parseBoolean(message.get("isDeleted").toString());
        }
        if (message.get("mvnoId") != null) {
            this.mvnoId = Integer.parseInt(message.get("mvnoId").toString());
        }
        if(message.get("type") != null){
            this.type = message.get("type").toString();
        }
        if(message.get("qosspeed") != null){
            this.qosspeed = message.get("qosspeed").toString();
        }
        if(message.get("upstreamprofileuid") != null){
            this.upstreamprofileuid = message.get("upstreamprofileuid").toString();
        }
        if(message.get("downstreamprofileuid") != null){
            this.downstreamprofileuid = message.get("downstreamprofileuid").toString();
        }
        if (message.get("qosPolicyGatewayMappingList") != null) {
            List<LinkedHashMap<String, Object>> qosPolicyGatewayMappings = (List<LinkedHashMap<String, Object>>) message.get("qosPolicyGatewayMappingList");

            for (LinkedHashMap<String, Object> qosPolicyGatewayMapping : qosPolicyGatewayMappings) {
                QOSPolicyGatewayMapping qosPolicyGatewayMapping1 = new QOSPolicyGatewayMapping();
                Integer id = (Integer) qosPolicyGatewayMapping.get("id");
                qosPolicyGatewayMapping1.setId(id.longValue());
                qosPolicyGatewayMapping1.setGatewayName(qosPolicyGatewayMapping.get("gatewayName").toString());
                qosPolicyGatewayMapping1.setDownloadSpeed(qosPolicyGatewayMapping.get("downloadSpeed").toString());
                qosPolicyGatewayMapping1.setUploadSpeed(qosPolicyGatewayMapping.get("uploadSpeed").toString());
                qosPolicyGatewayMapping1.setBaseUploadSpeed(qosPolicyGatewayMapping.get("baseUploadSpeed").toString());
                qosPolicyGatewayMapping1.setBaseDownloadSpeed(qosPolicyGatewayMapping.get("baseDownloadSpeed").toString());
                qosPolicyGatewayMapping1.setThrottleUploadSpeed(qosPolicyGatewayMapping.get("throttleUploadSpeed").toString());
                qosPolicyGatewayMapping1.setThrottleDownloadSpeed(qosPolicyGatewayMapping.get("throttleDownloadSpeed").toString());
                Integer qosPolicyId = (Integer) qosPolicyGatewayMapping.get("qosPolicyId");
                if (qosPolicyId == null) {
                    qosPolicyGatewayMapping1.setQosPolicyId(null);
                } else {
                    qosPolicyGatewayMapping1.setQosPolicyId(qosPolicyId.longValue());
                }
                qosPolicyGatewayMappingList.add(qosPolicyGatewayMapping1);
            }
//            qosPolicyGatewayMappings.forEach(qosPolicyGatewayMapping -> {
//
//            });
        }

    }
}
