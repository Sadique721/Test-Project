package com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "tbltstaffservicearearel")
@Data
@NoArgsConstructor
@ToString
@EntityListeners(AuditableListener.class)
public class StaffUserServiceAreaMapping extends Auditable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staffid", nullable = false, length = 40)
    private Integer staffId;

    @Column(name = "serviceareaid", nullable = false, length = 40)
    private  Integer serviceId;

    @Column(name = "created_on", nullable = false, length = 40)
    private LocalDateTime createdOn;

    @Column(name = "lastmodified_on", nullable = false, length = 40)
    private LocalDateTime lastmodifiedOn ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StaffUserServiceAreaMapping(Integer staffId, Long serviceId) {
        this.staffId = staffId;
        this.serviceId = Math.toIntExact(serviceId);
        this.createdOn = LocalDateTime.now();
        this.lastmodifiedOn = LocalDateTime.now();
    }

    public static List<StaffUserServiceAreaMapping> createMappings(Integer staffId, List<Long> serviceIds) {
        return serviceIds.stream()
                .map(serviceId -> new StaffUserServiceAreaMapping(staffId, serviceId))
                .collect(Collectors.toList());
    }
}
