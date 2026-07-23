package com.savbill.commonGateway.moules.SettingsManagement.StaffUserLocationMapping;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tbltstafflocationmapping")
@EntityListeners(AuditableListener.class)
public class StaffUserLocationMapping extends Auditable {
    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "location_name")
    private String locationName;
}
