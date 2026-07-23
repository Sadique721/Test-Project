package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;



import com.savbill.ticketmanagement.core.modules.Teams.domain.TeamHierarchyMapping;
import lombok.Data;

import java.util.List;

@Data
public class UpdateHierarchyShareDataMessage {
    private Long id;

    private Integer mvnoId;

    private Boolean isDeleted;

    private Long buId;

    private String hierarchyName;

    private String eventName;

    private List<TeamHierarchyMapping> teamHierarchyMappingList;

    private Integer lcoId;
}
