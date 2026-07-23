package com.savbill.cpm.MicroSeviceDataShare.SharedMessages;

import com.savbill.cpm.modules.Region.domain.Region;
import lombok.Data;

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
