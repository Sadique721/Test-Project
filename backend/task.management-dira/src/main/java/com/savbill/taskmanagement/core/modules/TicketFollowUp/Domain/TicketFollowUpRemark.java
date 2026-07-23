package com.savbill.taskmanagement.core.modules.TicketFollowUp.Domain;


import com.savbill.taskmanagement.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTTICKETFOLLOWUPREMARK")
public class TicketFollowUpRemark implements IBaseData<Long> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_follow_up_remark_id", nullable = false)
    private Long id;

    @Column(name = "remark")
    private String remark;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_follow_up_id")
    private TicketFollowUp ticketFollowUp;

    @CreationTimestamp
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean getDeleteFlag() {
        // TODO Auto-generated method stub
        return false;
    }
}
