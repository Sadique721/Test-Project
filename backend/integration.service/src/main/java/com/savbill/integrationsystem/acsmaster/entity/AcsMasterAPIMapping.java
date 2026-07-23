package com.savbill.integrationsystem.acsmaster.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "tbltacsapimapping")
public class AcsMasterAPIMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "api_method")
    private String apiMethod;

    @Column(name = "api_name")
    private String apiName;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "acs_master_id")
    private Long acsMasterId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AcsMasterAPIMapping that = (AcsMasterAPIMapping) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
