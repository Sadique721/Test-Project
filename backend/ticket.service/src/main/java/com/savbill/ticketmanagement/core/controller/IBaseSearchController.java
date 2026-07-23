package com.savbill.ticketmanagement.core.controller;

import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.dto.GenericSearchDTO;

public interface IBaseSearchController {
    GenericDataDTO search(GenericSearchDTO filterList);
}
