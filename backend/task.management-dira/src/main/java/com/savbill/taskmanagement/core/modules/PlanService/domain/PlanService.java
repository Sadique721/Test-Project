package com.savbill.taskmanagement.core.modules.PlanService.domain;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.data.IBaseData;
import com.savbill.taskmanagement.core.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import com.savbill.taskmanagement.core.modules.common.AuditableListener;
import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLMSERVICES")
@EntityListeners(AuditableListener.class)
public class PlanService extends Auditable<Integer> implements IBaseData {
	
	
	/*
CREATE TABLE TBLMSERVICES
(
	serviceid SERIAL PRIMARY KEY,
	servicename varchar(255),
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);  
	 */
    @DiffIgnore
    @Id
    @Column(name = "serviceid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "servicename", nullable = false, length = 40)
    private String name;

    @Column(name = "icname", nullable = false, length = 40)
    private String icname;

    @Column(name = "iccode", nullable = false, length = 40)
    private String iccode;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "is_qosv", nullable = false, columnDefinition = "Boolean default true")
    private Boolean isQoSV;

    @Column(name = "expiry",nullable = false,length = 100)
    private String expiry;

    private String ledgerId;

    @Column(name = "is_dtv")
    private Boolean is_dtv;

    @Column(name = "investmentcode_id")
    private Long investmentid;

//    @ManyToMany
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @JoinTable(name = "tbltserviceinventorymapping",
//            joinColumns = @JoinColumn(name = "serviceid", referencedColumnName = "serviceid"),
//            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "product_id"))
//    private List<ProductCategory> productCategories = new ArrayList<>();
//
//
//    @OneToMany(cascade = CascadeType.ALL,targetEntity = ServiceParamMapping.class,fetch = FetchType.LAZY)
//    @JoinColumn(name="serviceid")
//    List<ServiceParamMapping> serviceParamMappingList;

    @Column(name = "feasibility")
    private Boolean feasibility;
    @Column(name = "poc")
    private Boolean poc;
    @Column(name = "installation")
    private Boolean installation;
    @Column(name = "provisioning")
    private Boolean provisioning;
    @Column(name = "is_price_editable")
    private Boolean isPriceEditable;
    @Column(name = "feasibility_team_id")
    private Long feasibilityTeamId;
    @Column(name = "poc_team_id")
    private Long pocTeamId;
    @Column(name = "installation_team_id")
    private Long installationTeamId;
    @Column(name = "provisioning_team_id")
    private Long provisioningTeamId;
    @OneToMany(cascade = CascadeType.ALL,targetEntity = ServiceParamMapping.class,fetch = FetchType.LAZY)
    @JoinColumn(name="serviceid")
    List<ServiceParamMapping> serviceParamMappingList;

    @Override
    public Serializable getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
