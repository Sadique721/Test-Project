package com.savbill.inventorymanagement.modules.acl.domain;

import com.savbill.inventorymanagement.modules.Role.Role;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "tblmaclentry")
@NoArgsConstructor
public class RoleACLEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "roleid")
//    @ToString.Exclude
//    private Role role;

    @Column(nullable = false, name = "roleid")
    private Long roleId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int menuid;

    public RoleACLEntry(Role role, String code, int menuid,Long id) {
        this.id = id;
        this.roleId = role.getId();
        this.code = code;
        this.menuid = menuid;
    }
}
