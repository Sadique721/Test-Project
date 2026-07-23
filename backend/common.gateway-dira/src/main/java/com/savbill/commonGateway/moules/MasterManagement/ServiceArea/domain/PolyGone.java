package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tblmpolygone")
@AllArgsConstructor
@NoArgsConstructor
public class PolyGone extends Auditable implements IBaseData<Long> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="service_area_id")
    private Integer serviceAreaId;

    @Column(name = "lat",nullable = false)
    private String lat;

    @Column(name="lng",nullable = false)
    private String lng;

    @Column(name="poly_order")
    private Integer polyOrder;

    @Column(name="mvnoid")
    private Integer mvnoid;

    @Transient
    private String serviceAreaType;

    @Column(name="polygone_name")
    private String polygoneName;


    @Override
    public Long getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    public void setBuId(Long buId) {

    }

    public PolyGone(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
