package com.savbill.integrationsystem.core.controller;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface IBaseController<T> {
    GenericDataDTO getAll();
    GenericDataDTO getEntityById(@PathVariable String id);
    GenericDataDTO save(@RequestBody T entityDTO);
    GenericDataDTO update(@RequestBody T entityDTO);
    GenericDataDTO delete(@RequestBody T entityDTO);
}
