package com.savbill.integrationsystem.core.controller;

import com.savbill.integrationsystem.core.dto.GenericDataDTO;
import com.savbill.integrationsystem.core.dto.PaginationRequestDTO;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public interface IBaseExController<T> {
    GenericDataDTO getAll(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req);

    GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception;

    GenericDataDTO getAllWithoutPagination();

    GenericDataDTO save(@RequestBody T entityDTO, BindingResult result, HttpServletRequest req) throws Exception;

    GenericDataDTO update(@RequestBody T entityDTO, BindingResult result, HttpServletRequest req) throws Exception;

    GenericDataDTO delete(@RequestBody T entityDTO, HttpServletRequest req) throws Exception;

}
