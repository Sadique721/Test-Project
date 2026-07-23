package com.savbill.inventorymanagement.core.controller;

import com.savbill.inventorymanagement.core.dto.GenericDataDTO;
import com.savbill.inventorymanagement.core.dto.PaginationRequestDTO;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public interface IBaseExController<T> {
    GenericDataDTO getAll(@RequestBody PaginationRequestDTO paginationRequestDTO);

    GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception;

    GenericDataDTO getAllWithoutPagination();

    GenericDataDTO save(@RequestBody T entityDTO, BindingResult result, HttpServletRequest req) throws Exception;

    GenericDataDTO update(@RequestBody T entityDTO, BindingResult result, HttpServletRequest req) throws Exception;

    GenericDataDTO delete(@RequestBody T entityDTO, HttpServletRequest req) throws Exception;

}
