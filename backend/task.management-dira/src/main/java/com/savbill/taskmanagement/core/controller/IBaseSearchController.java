package com.savbill.taskmanagement.core.controller;

import com.savbill.taskmanagement.core.dto.GenericDataDTO;
import com.savbill.taskmanagement.core.dto.GenericSearchDTO;

public interface IBaseSearchController {
    GenericDataDTO search(GenericSearchDTO filterList);
}
