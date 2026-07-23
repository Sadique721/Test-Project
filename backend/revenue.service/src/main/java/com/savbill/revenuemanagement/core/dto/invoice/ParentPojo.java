package com.savbill.revenuemanagement.core.dto.invoice;

import com.savbill.revenuemanagement.core.dto.common.Auditable;
import lombok.Data;

@Data
public class ParentPojo extends Auditable {

    Integer errCode;
    String errMessage;

}