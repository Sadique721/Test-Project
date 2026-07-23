package com.savbill.inventorymanagement.core.controller;

import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.GenericSearchDTO;

public interface IBaseSearchController {
    GenericDataDTO search(GenericSearchDTO filterList);
}
