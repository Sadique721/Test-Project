package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.dto.IBaseDto;
import com.savbill.commonGateway.moules.SettingsManagement.CustAccountProfileManagement.CustAccountProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MvnoDTO extends Auditable implements IBaseDto {

    private Long id;

    private String name;

    private String username;

    private String password;

    private String suffix;

    private String description;

    private String email;

    private String phone;

    private String status;

    private String logfile;

    private String mvnoHeader;

    private String mvnoFooter;

    private Long passwordPolicyId;

    private String eventName;

    private Long eventId;

    private CustAccountProfile custAccountProfile;

    private Long profileId;
    private Long threshold;




    @Transient
    private String address;
    private String fullName;
    private Boolean isDelete = false;
    private Long roleId;
    private Integer custInvoiceRefId;
    private Boolean mvnoDeactivationFlag;
    private Integer chargeId;
    private byte[] profileImage;
    private String logo_file_name;
    private Integer mvnoPaymentDueDays;
    private Boolean isTwoFactorEnabled;
    private String authEventName;
    private Integer ispBillDay;
    private String billType;
    private Double ispCommissionPercentage;
    private String clientId;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
    }

    public MvnoDTO(Long id, String name, String username, String password, String status, Boolean isDelete) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.status = status;
        this.isDelete = isDelete;
    }

    public  MvnoDTO(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
