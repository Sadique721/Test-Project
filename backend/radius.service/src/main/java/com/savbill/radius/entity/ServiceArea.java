package com.savbill.radius.entity;

import com.savbill.radius.kafka.message.ServiceAreaMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblservicearea")
/*@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)*/
public class ServiceArea {
    @Id
    @Column(name = "service_area_id")
    private Long id;

    private String name;

    private String status;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "CREATEDATE", nullable = false, length = 40)
    private LocalDateTime localDateTime;

    @Column(name = "LASTMODIFIEDDATE", nullable = false, length = 40)
    private LocalDateTime lastmodifieddate;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;


    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    @Column(name = "latitude", nullable = false, length = 50)
    private String latitude;

    @Column(name = "longitude", nullable = false, length = 50)
    private String longitude;


    public ServiceArea(Map map) {
        if (map.get("seriveareamappingid") != null) {
            this.id = Long.parseLong(map.get("seriveareamappingid").toString());
        }
        if (map.get("areaname") != null) {
            this.name =map.get("areaname").toString();
        }
        if (map.get("status") != null) {
            this.status = map.get("status").toString();
        }
        if (map.get("isdeleted") != null) {
             this.isDeleted = Boolean.valueOf(map.get("isdeleted").toString());
        }
        if (map.get("createdate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.localDateTime = LocalDateTime.parse(map.get("createdate").toString(), formatter);
        }
        if (map.get("updatedate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.lastmodifieddate = LocalDateTime.parse(map.get("updatedate").toString(), formatter);
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
        if (map.get("MvnoId") != null) {
            this.mvnoId = Integer.valueOf(map.get("MvnoId").toString());
        }
        if (map.get("latitude") != null) {
            this.latitude =map.get("latitude").toString();
        }
        if (map.get("longitude") != null) {
            this.longitude =map.get("longitude").toString();
        }
        if (map.get("statusservicearea") != null) {
            this.status =map.get("statusservicearea").toString();
        }
    }



    public ServiceArea(ServiceAreaMessage serviceAreaMessage) {
        Map<String, Object> map = serviceAreaMessage.getCustomerData();

        if (map.get("id") != null) {
            this.id = Long.parseLong(map.get("id").toString());
        }
        if (map.get("areaname") != null) {
            this.name =map.get("areaname").toString();
        }
        if (map.get("isdeleted") != null) {
            this.isDeleted = Boolean.valueOf(map.get("isdeleted").toString());
        }
        if (map.get("createdate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.localDateTime = LocalDateTime.parse(map.get("createdate").toString(), formatter);
        }
        if (map.get("lastmodifieddate") != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.lastmodifieddate = LocalDateTime.parse(map.get("lastmodifieddate").toString(), formatter);
        }
        if (map.get("createdById") != null) {
            this.createdById = Integer.valueOf(map.get("createdById").toString());
        }
        if (map.get("lastModifiedById") != null) {
            this.lastModifiedById = Integer.valueOf(map.get("lastModifiedById").toString());
        }
        if (map.get("createdByName") != null) {
            this.createdByName = map.get("createdByName").toString();
        }
        if (map.get("lastModifiedByName") != null) {
            this.lastModifiedByName =map.get("lastModifiedByName").toString();
        }
        if (map.get("MvnoId") != null) {
            this.mvnoId = Integer.valueOf(map.get("MvnoId").toString());
        }
        if (map.get("latitude") != null) {
            this.latitude =map.get("latitude").toString();
        }
        if (map.get("longitude") != null) {
            this.longitude =map.get("longitude").toString();
        }
        if (map.get("statusservicearea") != null) {
            this.status =map.get("statusservicearea").toString();
        }
    }
}
