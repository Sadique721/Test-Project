package com.savbill.inventorymanagement.modules.Customers;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.savbill.inventorymanagement.modules.InventoryManagement.NetworkDevices.domain.NetworkDevices;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import lombok.Data;

@Data
public class CustomersPojo extends Auditable implements IBaseDto {

    private Integer id;
    private String title;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private ServiceArea servicearea;
    private String status;
    private Integer mvnoId;
    private String fullName;
    private Long buId;
    private Long popid;
    private Boolean isDeleted = false;
    private Long oltid;
    private String ezyBillCustomersId;
    private String latitude;
    private String longitude;
    private String custname;
    private String ezyBillAccountNumber;
    private String parentExperience;
    private String custtype;
    private Integer networkDeviceId;
    private Integer parentCustomersId;
    private Integer partnerId;
    private Integer parentCustId;
    private String nasPort;
    private String ipPoolNameBind;
    private String framedIp;
    private String framedIpBind;
    private Long masterdbid;
    private Long splitterid;
    private Long oltslotid;
    private Long oltportid;
    private NetworkDevices networkdevices;

    @Override
    public String toString() {
        return "CustomersPojo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", status='" + status + '\'' +
                ", custType='" + custtype + '\'' +
                ", popid='" + popid + '\'' +
                ", oltid='" + oltid + '\'' +
                ", masterdbid='" + masterdbid + '\'' +
                ", splitterid='" + splitterid + '\'' +
                '}';
    }

    @Override
    public Long getIdentityKey() {
        return Long.valueOf(id);
    }
}
