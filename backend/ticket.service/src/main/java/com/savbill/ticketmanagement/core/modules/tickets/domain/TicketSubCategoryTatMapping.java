package com.savbill.ticketmanagement.core.modules.tickets.domain;



import com.savbill.ticketmanagement.core.data.IBaseData;
import com.savbill.ticketmanagement.core.modules.TicketTatMatrix.Domain.TicketTatMatrix;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "tblttickettatsubcategorymapping")
public class TicketSubCategoryTatMapping implements IBaseData<Long> {
    public TicketSubCategoryTatMapping(Long ticketReasonSubCategoryId, TicketTatMatrix ticketTatMatrix, Boolean isDeleted) {
        this.ticketReasonSubCategoryId = ticketReasonSubCategoryId;
        this.ticketTatMatrix = ticketTatMatrix;
        this.isDeleted = isDeleted;
    }

    @DiffIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @DiffIgnore
    @Column(name = "sub_category_mapping_id",nullable = false)
    private Long ticketReasonSubCategoryId;
//    @DiffIgnore
    @ManyToOne(targetEntity = TicketTatMatrix.class)
    @JoinColumn(name = "ticket_tat_mapping_id", nullable = false,referencedColumnName = "id")
    private TicketTatMatrix ticketTatMatrix;
//    @DiffIgnore
    @OneToMany(targetEntity = TatQueryFieldMapping.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "tat_mapping_id")
    private List<TatQueryFieldMapping> tatQueryFieldMappingList;


    @Column(name ="is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name ="order_id")
    private Long orderid;


    @Override
    public Long getPrimaryKey() { return id; }

    @Override
    public void setDeleteFlag(boolean deleteFlag) { this.isDeleted = deleteFlag; }

    @Override
    public boolean getDeleteFlag()  {
        return isDeleted;
    }

    public TicketSubCategoryTatMapping() {
    }
}
