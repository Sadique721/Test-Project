package com.savbill.partnermanagement.modules.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @ManyToOne
    @JoinColumn(name = "roleid")
    @ToString.Exclude
    private Role role;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int menuid;

    public RoleACLEntry(Role role, String code, int menuid, Long id) {
        this.id = id;
        this.role = role;
        this.code = code;
        this.menuid = menuid;
    }
}
