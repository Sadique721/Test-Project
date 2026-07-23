package com.savbill.inventorymanagement.modules.Postpaidplan;

import com.savbill.inventorymanagement.core.data.Auditable;
import com.savbill.inventorymanagement.core.data.IBaseData;
import com.savbill.inventorymanagement.modules.InventoryManagement.ProductPlanMapping.Productplanmappingdto;
import com.savbill.inventorymanagement.modules.MasterManagement.ServiceArea.ServiceArea;
import com.savbill.inventorymanagement.modules.PostpaidPlanCharge.PostpaidPlanCharge;
import com.savbill.inventorymanagement.security.spring.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "tblmpostpaidplan")
@EntityListeners(AuditableListener.class)
public class PostpaidPlan extends Auditable implements IBaseData {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postpaidplanid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "displayname", nullable = false, length = 40)
    private String displayName;
    @Column(name = "status", nullable = false, length = 40)
    private String status;
    @Column(name = "planstatus", length = 40)
    private String planStatus;
    @Column(name = "MVNOID", length = 40, updatable = false)
    private Integer mvnoId;
    @Column(name = "serviceid", length = 40)
    private Integer serviceId;
    @Column(name = "plantype", nullable = false, length = 40)
    private String plantype;
    @Column(name = "plangroup", nullable = false, length = 100)
    private String planGroup;
    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    private List<PostpaidPlanCharge> chargeList = new ArrayList<>();
    @Transient
    private String serviceName;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltplanservicearearel", joinColumns = {@JoinColumn(name = "planid")}
            , inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    @Column(name = "BUID",length = 40)
    private Long buId;
    @Transient
    private List<Productplanmappingdto> productplanmappingList = new ArrayList<>();

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }
}
