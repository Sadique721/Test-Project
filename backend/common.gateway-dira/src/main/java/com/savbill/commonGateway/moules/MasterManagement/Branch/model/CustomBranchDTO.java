package com.savbill.commonGateway.moules.MasterManagement.Branch.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CustomBranchDTO {

    private Long id;
    private String name;
    private String status;

    @JsonProperty("branch_code")
    private String branchCode;

    private Integer mvnoId;
    private Boolean isDeleted;
    private Integer buId;

    @JsonProperty("revenue_sharing")
    private Boolean revenueSharing;

    @JsonProperty("sharing_percentage")
    private Double sharingPercentage;

    private String dunningDays;

    @JsonProperty("createdate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private LocalDateTime createDate;

    @JsonProperty("updatedate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private LocalDateTime updateDate;

    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;

    private List<Long> serviceAreaIdsList = new ArrayList<>();
    private List<String> serviceAreaNameList = new ArrayList<>();

    private Long displayId;
    private String displayName;

    public CustomBranchDTO(Long id,
                           String name,
                           String status,
                           String branchCode,
                           Integer mvnoId,
                           Boolean isDeleted,
                           Boolean revenueSharing,
                           Double sharingPercentage,
                           String dunningDays,
                           LocalDateTime createDate,
                           LocalDateTime updateDate,
                           Integer createdById,
                           Integer lastModifiedById,
                           String createdByName,
                           String lastModifiedByName) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.branchCode = branchCode;
        this.mvnoId = mvnoId;
        this.isDeleted = isDeleted;
        this.revenueSharing = revenueSharing;
        this.sharingPercentage = sharingPercentage;
        this.dunningDays = dunningDays;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.createdById = createdById;
        this.lastModifiedById = lastModifiedById;
        this.createdByName = createdByName;
        this.lastModifiedByName = lastModifiedByName;
    }

    public CustomBranchDTO() {}
}
