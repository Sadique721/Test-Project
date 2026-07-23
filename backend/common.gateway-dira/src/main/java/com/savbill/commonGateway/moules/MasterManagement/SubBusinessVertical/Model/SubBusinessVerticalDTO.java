package com.savbill.commonGateway.moules.MasterManagement.SubBusinessVertical.Model;


import com.savbill.commonGateway.constants.Constants;
import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
public class SubBusinessVerticalDTO extends Auditable implements IBaseDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sbvid")
    private Long id;

    @NotBlank(message = "sbvname" + Constants.MANDATORY_NOT_NULL_MSG)
    private String sbvname;

    @NotNull(message = "buVerticalsId"+ Constants.MANDATORY_NOT_NULL_MSG)
    private Long buVerticalsId;

    @NotBlank(message = "status" + Constants.MANDATORY_NOT_NULL_MSG)
    @Pattern(regexp = "Active|Inactive|", flags = Pattern.Flag.CASE_INSENSITIVE, message = "status" + Constants.STATUS_VALIDATION)
    private String status;

    private Boolean isDeleted = false;

    @JsonIgnore
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {

        return id;
    }
}
