package com.savbill.ticketmanagement.core.controller;

import com.savbill.ticketmanagement.core.dto.GenericDataDTO;
import com.savbill.ticketmanagement.core.dto.PaginationRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public interface IBaseExController<T> {
    GenericDataDTO getAll(@RequestBody PaginationRequestDTO paginationRequestDTO,HttpServletRequest req);

    GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception;

    GenericDataDTO getAllWithoutPagination();

    GenericDataDTO save(@RequestBody T entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception;

    GenericDataDTO update(@RequestBody T entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception;

    GenericDataDTO delete(@RequestBody T entityDTO, Authentication authentication, HttpServletRequest req) throws Exception;

}
