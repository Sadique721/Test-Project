package com.savbill.commonGateway.moules.SettingsManagement.StaffUserAccessibleRole;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tbltstaffaccessiblerole")
public class StaffAccessibleRoleMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accessibleroleid", nullable = false)
    private Long id;

    @Column(name = "staffid",nullable = false)
    private Integer staffId;

    @Column(name = "staffaccessibleroleid", nullable = false)
    private Long staffAccessibleRoleId;

}
