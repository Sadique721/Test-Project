package com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Domain;



import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.data.IBaseData;
import com.savbill.commonGateway.moules.MasterManagement.BusinessVerticals.domain.BusinessVerticals;
import com.savbill.commonGateway.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmsubbusinessvertical")
@SQLDelete(sql = "UPDATE tblmsubbusinessvertical SET is_deleted = true WHERE sbvid=?")
@Where(clause = "is_deleted=false")
@EntityListeners(AuditableListener.class)
public class SubBusinessVertical extends Auditable implements IBaseData<Long> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sbvid")
    private Long id;

    @Column(name = "sbvname")
    private String sbvname;

    @OneToOne
    @JoinColumn(name = "bu_verticals_id")
    private BusinessVerticals businessVerticals ;

    private String status;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted=deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    @Override
    public void setBuId(Long buId) {

    }
    public SubBusinessVertical(SubBusinessVertical subBusinessVertical){
        this.id = subBusinessVertical.getId();
        this.mvnoId = subBusinessVertical.getMvnoId();
        this.status = subBusinessVertical.getStatus();
        this.sbvname = subBusinessVertical.getSbvname();
        this.isDeleted= subBusinessVertical.getIsDeleted();
    }
}

