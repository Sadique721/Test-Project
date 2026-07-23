package com.savbill.partnermanagement.modules.partner.entity;

import com.savbill.partnermanagement.core.data.IBaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblpricebookslabdtls")
public class PriceBookSlabDetails implements IBaseData<Long> {

    @Id
    @DiffIgnore
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pbslabdetailid", updatable = false, nullable = false)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(targetEntity = PriceBook1.class)
    @JoinColumn(name = "bookid", updatable = true, insertable = true)
    private PriceBook1 priceBook;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Column(name = "from_range")
    private Long fromRange;

    @Column(name = "to_range")
    private Long toRange;

    @Column(name = "commission_amount")
    private Double commissionAmount;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
