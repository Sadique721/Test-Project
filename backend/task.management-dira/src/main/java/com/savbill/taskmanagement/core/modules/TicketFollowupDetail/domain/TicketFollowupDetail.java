package com.savbill.taskmanagement.core.modules.TicketFollowupDetail.domain;

import com.savbill.taskmanagement.core.data.IBaseData;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblticketfollowupdetail")
public class TicketFollowupDetail implements IBaseData<Long> {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticketfollowid")
    private Long id;

    private String remark;
    
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Column(name = "case_id")
    private Long caseId;
    
    @Column(name = "staffid")
    private Integer staffId;
    
    @Column(name = "custid")
    private Integer custId;

    @Column(name = "remark_date", nullable = false)
    private LocalDateTime remarkDate;

    @Column(name = "remark_type")
    private String remarkType;

    @Column(name = "is_from_customer")
    private Boolean isFromCustomer;

    @Transient
    private String caseTitle;
    
    @Transient
    private String staffUserName;
    
    @Transient
    private String customersName;
    
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDelete;
    }
}
