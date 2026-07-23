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
@Table(name = "tbltacsurlmapping")
public class AcsMasterUrlParamMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "param_name")
    private String paramName;

    @Column(name = "param_value")
    private String paramValue;

    @Column(name = "acs_master_id")
    private Long accMasterId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AcsMasterUrlParamMapping that = (AcsMasterUrlParamMapping) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
