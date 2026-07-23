//package com.savbill.ticketmanagement.core.modules.acl.domain;
//
//
//import com.savbill.ticketmanagement.core.modules.role.domain.Role;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//import javax.persistence.*;
//
//@Entity
//@Setter
//@Getter
//@Table(name = "tblmaclentry")
//@NoArgsConstructor
//public class RoleACLEntry {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "roleid")
//    @ToString.Exclude
//    private Role role;
//
//    @Column(nullable = false)
//    private String code;
//
//    @Column(nullable = false)
//    private int menuid;
//
//    public RoleACLEntry(Role role, String code, int menuid) {
//        this.role = role;
//        this.code = code;
//        this.menuid = menuid;
//    }
//}
