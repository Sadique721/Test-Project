package com.savbill.commonGateway.core.controller;

import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.PaginationRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

public interface IBaseExController<T> {
    GenericDataDTO getAll(@RequestBody PaginationRequestDTO paginationRequestDTO,HttpServletRequest req, HttpServletResponse res)throws Exception;

    GenericDataDTO getEntityById(@PathVariable String id,HttpServletRequest req, HttpServletResponse res)throws Exception;
    GenericDataDTO getAllWithoutPagination(HttpServletRequest req,HttpServletResponse res)throws Exception;

    GenericDataDTO save(@Valid @RequestBody T entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,HttpServletResponse res) throws Exception;

    GenericDataDTO update(@Valid @RequestBody T entityDTO, BindingResult result, Authentication authentication,HttpServletRequest req,HttpServletResponse res)throws Exception;

    GenericDataDTO delete(@RequestBody T entityDTO, Authentication authentication,HttpServletRequest req,HttpServletResponse res)throws Exception;

}
