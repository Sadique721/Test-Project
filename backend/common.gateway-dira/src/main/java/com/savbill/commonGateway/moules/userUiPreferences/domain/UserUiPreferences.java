package com.savbill.commonGateway.moules.userUiPreferences.domain;

import com.savbill.commonGateway.common.domain.Auditable2;
import com.savbill.commonGateway.spring.security.AuditableListener2;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblmuseruipreferences")
@EntityListeners(AuditableListener2.class)
@NoArgsConstructor
public class UserUiPreferences extends Auditable2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @Column(name = "mvnoname", nullable = false, length = 100)
    private String mvnoName;

    @Column(name = "style", nullable = false)
    private String style;

    @Column(name = "logo_image", nullable = false, length = 240)
    private String logoImage;

    @Column(name = "bg_image", length = 240)
    private String bgImage;

    @Column(name = "page_name", length = 240)
    private String pageName;

    @Column(name = "status", length = 240)
    private String status;

    @Column(name = "is_delete")
    private Boolean isDelete;

}
