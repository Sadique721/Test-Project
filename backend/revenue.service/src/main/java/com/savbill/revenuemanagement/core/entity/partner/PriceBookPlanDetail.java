package com.savbill.revenuemanagement.core.entity.partner;


import com.savbill.revenuemanagement.core.data.IBaseData;
import com.savbill.revenuemanagement.productmanagement.Plan.domain.PostpaidPlan;
import com.savbill.revenuemanagement.productmanagement.PlanGroup.domain.PlanGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.*;

@Data
@Entity
@Table(name = "tblpricebookplandtls")
@NoArgsConstructor
public class PriceBookPlanDetail implements IBaseData<Long> {

    @Id
    @Column(name = "pbdetailid")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double offerprice;
    private Double partnerofficeprice;
    private String revsharen = "No";
    private String registration = "No";
    private String renewal = "No";

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "bookid")
    private PriceBook priceBook;

    @ManyToOne
    @JoinColumn(name = "planid")
    private PostpaidPlan postpaidPlan;

    @Column(name = "revenue_share_percentage", length = 10)
    private String revenueSharePercentage;

    @Column(name = "is_tax_included", columnDefinition = "Boolean default false")
    private Boolean isTaxIncluded = false;


    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
    @ManyToOne
    @JoinColumn(name = "planbundleid")
    public PlanGroup planGroup;
    @Transient
    private Integer planGroupId;
    @Transient
    private Integer postpaidplanid;

    public PriceBookPlanDetail(Long id, Double offerprice, Double partnerofficeprice, String revsharen, String registration, String renewal, Boolean isDeleted, PriceBook priceBook, PostpaidPlan postpaidPlan, String revenueSharePercentage, Boolean isTaxIncluded, PlanGroup planGroup, Integer planGroupId, Integer postpaidplanid, Long pricebookid) {
        this.id = id;
        this.offerprice = offerprice;
        this.partnerofficeprice = partnerofficeprice;
        this.revsharen = revsharen;
        this.registration = registration;
        this.renewal = renewal;
        this.isDeleted = isDeleted;
        this.priceBook = priceBook;
        this.postpaidPlan = postpaidPlan;
        this.revenueSharePercentage = revenueSharePercentage;
        this.isTaxIncluded = isTaxIncluded;
        this.planGroup = planGroup;
        this.planGroupId = planGroupId;
        this.postpaidplanid = postpaidplanid;
        this.pricebookid = pricebookid;
    }

    @Transient
    private Long pricebookid;

}
