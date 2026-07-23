package com.savbill.commonGateway.core.controller;

import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchDTO;

public interface IBaseSearchController {
    GenericDataDTO search(GenericSearchDTO filterList);
}
