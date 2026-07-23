package com.savbill.partnermanagement.rabbitmq.master;

import lombok.Data;

import javax.swing.plaf.synth.Region;
import java.util.List;

@Data
public class UpdateBusinessVerticalSharedDataMessage {
    private Long id;
    private String vname;
    private List<Region> buregionidList;
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
