package com.savbill.cpm.pojo.api;

import com.savbill.cpm.model.common.Auditable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustServiceChargeIPDetailsPojo extends Auditable {

    private Integer id;

    private Integer custId;

    private Integer custServiceMappingId;

    private String staticIPAdrress;

    private LocalDateTime staticIPStartDate;

    private LocalDateTime staticIPEndDate;

    private  Integer chargeId;
}
