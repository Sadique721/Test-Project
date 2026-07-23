package com.savbill.commonGateway.moules.Notification.domain;

import com.savbill.commonGateway.core.data.IBaseData;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblnotifcation_config")
public class NotificationConfig implements IBaseData<Long> {

    @Id
    @Column(name = "noti_config_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String config_entity;
    private String config_attribute;
    private String config_atrr_type;
    private String atrr_condi;
    private String attr_value;

    @OneToOne
    @JoinColumn(name = "notification_id")
    @ToString.Exclude
    private Notification notification;

    @Override
    public Long getPrimaryKey() {
        return this.id;
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

    //private Long broadcastid;
//    @ManyToOne
//    @JoinColumn(name = "planid")
//    private PostpaidPlan postpaidPlan;
}
