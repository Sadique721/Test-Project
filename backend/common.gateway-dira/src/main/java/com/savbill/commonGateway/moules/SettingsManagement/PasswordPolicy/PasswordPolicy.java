package com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "tblm_password_policy")
public class PasswordPolicy extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private String status;

    @Column(name = "min_length")
    private Long min_length;

    @Column(name = "max_length")
    private Long max_length;

    @Column(name = "expiration_days")
    private Long expiration_days;

    @Column(name = "disable_recycling_prevention")
    private Long disable_recycling_prevention;

    @Column(name = "disable_account_lockout")
    private Long disable_account_lockout;

    @Column(name = "pattern")
    private String pattern;

    @Column(name = "pattern_description")
    private String pattern_description;

    @DiffIgnore
    @Column(name = "mvnoId", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "is_notification_required")
    private Boolean isNotificationRequired = false;
}
