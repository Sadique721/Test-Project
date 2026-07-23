package com.savbill.integrationsystem.Case;

import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tblmticketassignstaffmapping")
public class TicketAssignStaffMapping {


    @Id
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketId;

    private Integer staffId;

    public TicketAssignStaffMapping(List<TicketAssignStaffMapping> ticketAssignStaffMappings) {

        if (ticketAssignStaffMappings != null) {
            for (TicketAssignStaffMapping mapping : ticketAssignStaffMappings) {
                this.id = mapping.getId();
                this.ticketId = mapping.getTicketId();
                this.staffId = mapping.getStaffId();
            }
        }
    }
}
