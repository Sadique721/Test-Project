package com.savbill.integrationsystem.core.dto;

import com.savbill.integrationsystem.navmaster.entity.NAVMaster;
import lombok.Data;

@Data
public class MultipleBodyForPost {
    PaginationRequestDTO paginationRequestDTO;
    Object billGenFinalData;
    NAVMaster navMaster;
}
