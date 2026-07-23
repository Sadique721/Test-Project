package com.savbill.revenuemanagement.productmanagement.PlanGroup.domain;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.mastermanagement.ServiceArea.domain.ServiceArea;
import com.savbill.revenuemanagement.productmanagement.Product_Plan_Group_Mapping.ProductPlanGroupMapping;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


@Data
@Entity
@Table(name = "tblmplangroup")
public class PlanGroup  extends Auditable {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plangroupid")
    private Integer planGroupId;

    @Column(name = "plangroupname", nullable = false, length = 40)
    private String planGroupName;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "plantype", nullable = false, length = 40)
    private String plantype;

    @Column(name = "planmode", nullable = false, length = 50)
    private String planMode;

    @Column(name = "is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;;


    @OneToMany(targetEntity = PlanGroupMapping.class, cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "plangroupid", referencedColumnName = "plangroupid")
    @ToString.Exclude
    @JsonManagedReference
    private List<PlanGroupMapping> planMappingList = new ArrayList<>();

    @Column(name = "dbr", nullable = true)
    private Double dbr;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "plangrouptype", nullable = false, length = 100)
    private String planGroupType;

    @Column(name = "PLANCATEGORY", nullable = false, length = 40)
    private String category;

    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMappingId;

    @Column(name = "next_staff")
    private Integer nextStaff;

    @Column(name = "accessibility")
    private String accessibility;

    @Column(name = "allowdiscount")
    private boolean allowDiscount;

    @Column(name ="offerprice")
    private Double offerprice;

    @Column(name = "template_id", length = 40)
    private Long templateId;

    @Column(name="invoicetoorg")
    private Boolean invoiceToOrg;

    @Column(name="requiredapproval")
    private Boolean requiredApproval;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @ToString.Exclude
    @JoinTable(name = "tblmserviceareaplangroupmapping", joinColumns = {@JoinColumn(name = "plangroupid")}, inverseJoinColumns = {@JoinColumn(name = "service_area_id")})
    private List<ServiceArea> servicearea = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL,targetEntity = ProductPlanGroupMapping.class)
    @JoinColumn(name = "plan_group_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    @ToString.Exclude
    List<ProductPlanGroupMapping> productPlanGroupMappingList;
}
