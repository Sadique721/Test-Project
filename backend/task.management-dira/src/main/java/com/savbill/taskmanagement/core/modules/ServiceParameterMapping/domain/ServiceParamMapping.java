package com.savbill.taskmanagement.core.modules.ServiceParameterMapping.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltserviceparamservicemapping")
public class ServiceParamMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

//    @ManyToMany(targetEntity = PlanService.class)
//    @JoinColumn(name = "serviceid", referencedColumnName = "id", updatable = true, insertable = true)
    @Column(name = "serviceid")
    private Long serviceid;

    @Column(name = "serviceparamid")
    private Long serviceParamId;

    @Column(name = "value")
    private String value;

    @Column(name = "ismandatory")
    private Boolean isMandatory;

    @Column(name = "serviceparamname")
    private  String serviceParamName;
//    @Override
//    @JsonIgnore
//    public Serializable getPrimaryKey() {
//        return id;
//    }
//
//    @Override
//    @JsonIgnore
//    public void setDeleteFlag(boolean deleteFlag) {
//
//    }
//
//    @Override
//    @JsonIgnore
//    public boolean getDeleteFlag() {
//        return false;
//    }
//
//    @Override
//    @JsonIgnore
//    public void setBuId(Long buId) {
//
//    }
}
