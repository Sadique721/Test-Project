package com.savbill.notification.entity;

import com.savbill.notification.helper.EmailConfigDto;
import com.savbill.notification.helper.UpdateEmailConfigDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBLMEMAILCONFIG")
public class EmailConfig extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated email config Id", required = true)
    @Column(name = "emailconfigid", nullable = false)
    private Long emailConfigId;

    @ApiModelProperty(notes = "This is username", required = true)
    @Column(name = "username", length = 100, nullable = false)
    private String userName;

    @ApiModelProperty(notes = "This is password", required = true)
    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @ApiModelProperty(notes = "This is smtp authentication value", required = true)
    @Column(name = "smtpauth", nullable = false)
    private boolean smtpAuth;

    @ApiModelProperty(notes = "This is auth type", allowableValues = "StartTLS,SSL", value = "This field accept value only : StartTLS or SSL", required = true)
    @Column(name = "authtype", length = 100, nullable = false)
    private String authType;

    @ApiModelProperty(notes = "This is hostvalue", required = true)
    @Column(name = "hostserver", length = 100, nullable = false)
    private String hostServer;

    @ApiModelProperty(notes = "This is port value", required = true)
    @Column(name = "port", length = 100, nullable = false)
    private String port;

    @DiffIgnore
    @ApiModelProperty(notes = "This is mvno id", required = true)
    @Column(name = "mvnoid", nullable = false)
    private Long mvnoId;

    @DiffIgnore
    @ApiModelProperty(notes = "this is bu id", required = false)
    @Column(name = "BUID", length = 40, updatable = false)
    private Long buId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Column(name = "servicetype")
    private String serviceType;

    @Transient
    private String mvnoName;

    public EmailConfig(EmailConfigDto emailConfigDto, Long mvnoId) {
        this.authType = emailConfigDto.getAuthType();
        this.hostServer = emailConfigDto.getHostServer();
        this.mvnoId = mvnoId;
        this.password = emailConfigDto.getPassword();
        this.port = emailConfigDto.getPort();
        this.smtpAuth = emailConfigDto.isSmtpAuth();
        this.userName = emailConfigDto.getUserName();
    }

    public EmailConfig(UpdateEmailConfigDto emailConfigDto, Long mvnoId) {
        this.emailConfigId = emailConfigDto.getEmailConfigId();
        this.authType = emailConfigDto.getAuthType();
        this.hostServer = emailConfigDto.getHostServer();
        this.mvnoId = mvnoId;
        this.password = emailConfigDto.getPassword();
        this.port = emailConfigDto.getPort();
        this.smtpAuth = emailConfigDto.isSmtpAuth();
        this.userName = emailConfigDto.getUserName();
    }
}
