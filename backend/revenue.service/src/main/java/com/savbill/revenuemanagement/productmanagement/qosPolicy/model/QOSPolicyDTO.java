package com.savbill.revenuemanagement.productmanagement.qosPolicy.model;


import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.dto.common.IBaseDto;
import com.savbill.revenuemanagement.productmanagement.qosPolicy.domain.QOSPolicyGatewayMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class QOSPolicyDTO extends Auditable implements IBaseDto {

    private Long id;
    @NotNull(message = "Please enter name")
    private String name;
    @NotNull(message = "Please enter description")
    private String description;
    @NotNull(message = "Please enter basepolicyname")
    private String basepolicyname;
    private String thpolicyname;
    private String baseparam1;
    private String baseparam2;
    private String baseparam3;
    private String thparam1;
    private String thparam2;
    private String thparam3;
    private Boolean isDeleted = false;

    private Integer mvnoId;
    private Long buId;
    List<QOSPolicyGatewayMapping> qosPolicyGatewayMappingList;

    private String type;

    private Integer displayId;
    private String displayName;

    private String qosspeed;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

}
