package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
//import com.savbill.commonGateway.moules.SettingsManagement.CustAccountProfileManagement.AccountNumGenerator;
import com.savbill.commonGateway.moules.SettingsManagement.CustAccountProfileManagement.CustAccountProfile;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoDiscountManagement.MvnoDiscountMapping;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@ToString
@Table(name = "tblmmvno")
@EntityListeners(AuditableListener.class)
public class Mvno extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 64)
    private String name;

    @Column(name = "USERNAME", nullable = false, length = 200)
    private String username;

    @Column(name = "PASSWORD", length = 200)
    private String password;

    @Column(name = "SUFFIX",  length = 16)
    private String suffix;

    @Column(name = "DESCRIPTION", nullable = false, length = 255)
    private String description;

    @Column(name = "EMAIL", nullable = false, length = 255)
    private String email;

    @Column(name = "PHONE", nullable = false, length = 255)
    private String phone;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "LOGOFILE",length = 255)
    private String logfile;

    @Column(name = "MVNOHEADER",length = 255)
    private String mvnoHeader;

    @Column(name = "MVNOFOOTER", length = 255)
    private String mvnoFooter;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "full_name",nullable = false, length = 255)
    private String fullName;

    @Column(name = "cust_invoice_ref_id")
    private Integer custInvoiceRefId;

    @Column(name = "mvno_deactivation_flag")
    private Boolean mvnoDeactivationFlag;
    @Lob
    @Column(name = "logo_image", columnDefinition = "BLOB", length = 30000)
    private byte[] profileImage;

    @Column(name = "logo_file_name", nullable = false, length = 200)
    private String logo_file_name;
    @Column(name = "mvno_payment_due_days")
    private Integer mvnoPaymentDueDays;

    @OneToMany(mappedBy = "mvno", cascade = CascadeType.ALL)
    private List<MvnoDiscountMapping> mvnoDiscountMapping;

    @Column(name="is_two_factor_enabled", nullable = false)
    private Boolean isTwoFactorEnabled;

    @Column(name="auth_event_name", nullable = false)
    private String authEventName;

    @Column(name="password_policy_id")
    private Long passwordPolicyId;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "bill_type")
    private String billType;

    @Column(name = "isp_bill_day")
    private Integer ispBillDay;

    @Column(name = "isp_commission_percentage")
    private Double ispCommissionPercentage;

    @Column(name="clientid")
    private String clientId;


    @OneToOne(targetEntity = CustAccountProfile.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id", nullable = false)
    private CustAccountProfile custAccountProfile;

    @Column(name = "threshold")
    private Long threshold;

    public Mvno() {

    }

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDelete;
    }

    @Override
    public void setBuId(Long buId) {

    }


    public Mvno(Mvno mvno){
        this.id = mvno.getId();
        this.name = mvno.getName();
        this.address = mvno.getAddress();
        this.fullName = mvno.getFullName();
        this.status = mvno.getStatus();
        this.isDelete = mvno.getIsDelete();
        this.suffix = mvno.getSuffix();
        this.username = mvno.getUsername();
        this.password = mvno.getPassword();
        this.phone = mvno.getPhone();
        this.email = mvno.getEmail();
        this.description = mvno.getDescription();
        this.mvnoFooter = mvno.getMvnoFooter();
        this.mvnoHeader = mvno.getMvnoHeader();
        this.logfile = mvno.getLogfile();
        this.eventName = mvno.getEventName();
        this.eventId = mvno.getEventId();
        this.clientId = mvno.getClientId();
    }

    public Mvno(String eventName, Long eventId){
        this.eventName = eventName;
        this.eventId = eventId;
    }

}

