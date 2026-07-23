package com.savbill.commonGateway.moules.SettingsManagement.StaffUserBusinessUnitMapping;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbltstaffbusinessunitrel")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
@ToString
public class StaffUserBusinessUnitMapping extends Auditable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staffid", nullable = false, length = 40)
    private Integer staffId;

    @Column(name = "businessunitid", nullable = false, length = 40)
    private Long businessunitId;

    @Column(name = "created_on", nullable = false, length = 40)
    private LocalDateTime createdOn;

    @Column(name = "lastmodified_on", nullable = false, length = 40)
    private LocalDateTime lastmodifiedOn;

    public Long getId() { return  id; }

    public void setId(Long id) { this.id = id; }
}
