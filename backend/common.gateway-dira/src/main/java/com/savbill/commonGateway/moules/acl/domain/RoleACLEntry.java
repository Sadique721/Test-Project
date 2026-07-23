package com.savbill.commonGateway.moules.acl.domain;

import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "tblmaclentry")
@NoArgsConstructor
public class RoleACLEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "roleid")
    @ToString.Exclude
    private Role role;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int menuid;

    @Transient
    private Long roleId;

    @Column
    private String product;

    public RoleACLEntry(Role role, String code, int menuid,Long id, String product) {
        this.id = id;
        this.role = role;
        this.code = code;
        this.menuid = menuid;
        this.product = product;
    }
}
