package com.savbill.inventorymanagement.core.controller;

import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface IBaseController<T> {
    GenericDataDTO getAll();
    GenericDataDTO getEntityById(@PathVariable String id);
    GenericDataDTO save(@RequestBody T entityDTO);
    GenericDataDTO update(@RequestBody T entityDTO);
    GenericDataDTO delete(@RequestBody T entityDTO);
}
