package com.savbill.taskmanagement.core.modules.tasks.domain;


import com.savbill.taskmanagement.core.data.Auditable;
import com.savbill.taskmanagement.core.data.IBaseData;
import com.savbill.taskmanagement.core.modules.common.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblcasedocdetails")
@EntityListeners(AuditableListener.class)
public class CaseDocDetails extends Auditable implements IBaseData<Long> {

    @DiffIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docId;

    @DiffIgnore
    @Column(name = "ticket_id")
    private Long ticketId;
    private String docStatus;
    private String filename;
    private String uniquename;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;




    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return docId;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }
}
