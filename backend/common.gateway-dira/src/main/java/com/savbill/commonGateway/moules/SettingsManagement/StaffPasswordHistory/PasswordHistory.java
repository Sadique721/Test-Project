package com.savbill.commonGateway.moules.SettingsManagement.StaffPasswordHistory;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "tblm_staff_password_history")
public class PasswordHistory extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password_attempt_number")
    private Long passwordAttemptNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "staff_id")
    private Integer staffId;

    @Column(name = "uuid")
    private String uuid;
}
