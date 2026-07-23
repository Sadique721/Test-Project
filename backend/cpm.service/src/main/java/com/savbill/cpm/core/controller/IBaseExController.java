package com.savbill.cpm.core.controller;

import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.savbill.cpm.core.dto.GenericDataDTO;
import com.savbill.cpm.core.dto.PaginationRequestDTO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

public interface IBaseExController<T> {
    GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req);

    GenericDataDTO getEntityById(@PathVariable String id,HttpServletRequest req)throws Exception;

    GenericDataDTO getAllWithoutPagination();

    GenericDataDTO save(@Valid @RequestBody T entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception;

    GenericDataDTO update(@Valid @RequestBody T entityDTO, BindingResult result, Authentication authentication,HttpServletRequest req)throws Exception;

    GenericDataDTO delete(@RequestBody T entityDTO, Authentication authentication,HttpServletRequest req)throws Exception;

}
