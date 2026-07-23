package com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltnetworkdevicebind")
@EntityListeners(AuditableListener.class)
public class NetworkDeviceBind extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "currentdeviceid")
    private Long currentDeviceId;

    @Column(name = "porttype")
    private String portType;

    @Column(name = "otherdeviceid")
    private Long otherDeviceId;

    @Column(name = "current_device_port")
    private String currentDevicePort;

    @Column(name = "other_device_port")
    private String otherDevicePort;

    @Column(name = "mappingid")
    private Integer mappingId;

    @Column(name = "currentdevice")
    private String currentDevice;

    @Column(name = "otherdevice")
    private String otherDevice;

    @Column(name = "currentdeviceportnumber")
    private String currentDevicePortNumber;

    @Column(name = "otherdeviceportnumber")
    private String otherDevicePortNumber;

    @Column(name = "currentdevicetype")
    private String currentDeviceType;

    @Column(name = "otherdevicetype")
    private String otherDeviceType;


    @Transient
    private boolean canDelete;


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

    public NetworkDeviceBind() {
        super();
    }

    public NetworkDeviceBind(Long deviceId, String portType, Long parentDeviceId) {
        this.currentDeviceId = deviceId;
        this.portType = portType;

    }

    public NetworkDeviceBind(Long deviceId, String portType, Long deviceName, Long parentDeviceId, String inBind, String outBind) {
        this.currentDeviceId = deviceId;
        this.portType = portType;
        this.otherDeviceId =deviceName;

    }
}
