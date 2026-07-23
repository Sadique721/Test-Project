package com.savbill.integrationsystem.mvno;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveDto {
    Integer custId;
    Integer creditDocID;
}
