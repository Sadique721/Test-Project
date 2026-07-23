package com.savbill.taskmanagement.rabbitmq.messages.DataShareMessage;


import com.savbill.taskmanagement.core.modules.Region.domain.Region;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class UpdateBusinessVerticalSharedDataMessage {
    private Long id;
    private String vname;
    @JsonIgnore
    private List<Region> buregionidList;
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
