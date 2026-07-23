package com.savbill.integrationsystem.billgen.entity;


import com.savbill.integrationsystem.rabbitmq.ServiceAreaIn;
import lombok.*;
import javax.persistence.*;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblservicearea")
public class ServiceArea {
    @Id
    @Column(name = "service_area_id")
    private Long id;

    private String name;

    private String status;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    
    @Column(name = "latitude", nullable = false, length = 50)
    private String latitude;
    
    @Column(name = "longitude", nullable = false, length = 50)
    private String longitude;

    @Column(name = "areaid", nullable = true)
    private Long areaId;

    @Column(name = "cityid", length = 40)
    private Long cityid;

    public ServiceArea(Long id) {
        this.id = id;
    }

    public ServiceArea(ServiceAreaIn message){
        this.id= message.getId();
        this.name=message.getName();
        this.status=message.getStatus();
        this.isDeleted=message.getIsDeleted();
        this.mvnoId=message.getMvnoId();
        this.latitude=message.getLatitude();
        this.longitude=message.getLongitude();
        this.areaId=message.getAreaid();
        this.cityid=message.getCityid();
    }



}
