package com.savbill.cpm.modules.placeOrder.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.placeOrder.domain.Order;
import com.savbill.cpm.modules.placeOrder.model.OrderDTO;

@Mapper
public interface OrderMapper extends IBaseMapper<OrderDTO, Order> {
}
