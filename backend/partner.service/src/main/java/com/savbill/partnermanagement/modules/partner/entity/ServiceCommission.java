package com.savbill.partnermanagement.modules.partner.entity;


import com.savbill.partnermanagement.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblservicecommission")
public class ServiceCommission implements IBaseData<Long> {
    @Id
    @DiffIgnore
    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "bookid")
    private PriceBook1 priceBook;

    @Column(name = "serviceid")
    private Long serviceId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "revenue_share_percentage")
    private Integer revenue_share_percentage;

    @Column(name = "royalty_percentage")
    private Double royaltyPercentage;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

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
}
